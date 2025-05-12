import time
import asyncio
from starlette.middleware.base import BaseHTTPMiddleware, RequestResponseEndpoint
from starlette.requests import Request
from starlette.responses import JSONResponse, Response

class RateLimiterMiddleware(BaseHTTPMiddleware):
    def __init__(self, app, allowed_ips, rate_limit, window_seconds=60):
        """
        :param allowed_ips: List of IPs allowed to connect.
        :param rate_limit: Maximum number of requests per user within the window.
        :param window_seconds: Time window in seconds.
        """
        super().__init__(app)
        self.allowed_ips = allowed_ips
        self.rate_limit = rate_limit
        self.window_seconds = window_seconds
        self.requests = {}  # maps user_id to list of request timestamps
        self.lock = asyncio.Lock()

    async def dispatch(self, request: Request, call_next: RequestResponseEndpoint) -> Response:
        client_ip = request.client.host
        if client_ip not in self.allowed_ips:
            return JSONResponse(status_code=403, content={"detail": "IP not allowed"})

        # Extract user_id from header "X-User-Id" or query parameter "user_id"
        if request.url.path.startswith("/search") or request.url.path.startswith("/embedding"):
            user_id = request.headers.get("X-User-Id") or request.query_params.get("user_id")
            if not user_id:
                return JSONResponse(
                    status_code=400, content={"detail": "Missing user_id in header or query parameters"}
                )
            async with self.lock:
                now = time.time()
                window_start = now - self.window_seconds
                # Initialize if necessary and purge outdated requests
                self.requests.setdefault(user_id, [])
                self.requests[user_id] = [ts for ts in self.requests[user_id] if ts > window_start]
                if len(self.requests[user_id]) >= self.rate_limit:
                    return JSONResponse(status_code=429, content={"detail": "Rate limit exceeded"})
                self.requests[user_id].append(now)

        return await call_next(request)
