import os

import dotenv
from fastapi import FastAPI, Request, HTTPException
from fastapi.responses import JSONResponse
from loguru import logger

from app.util.config import Config, config
from app.util.rate_limiter import RateLimiterMiddleware

# Import routers directly from your resource modules
from app.resource.healthcheck_resource import router as healthcheck_router
from app.resource.search_resource import router as search_router
from app.resource.embedding_resource import router as embedding_router

# Configure the logger

# Create FastAPI application instance with metadata for Swagger docs
app = FastAPI(
    title="Contact Search Service",
    description="Service to search contacts using ElasticSearch and Embeddings.",
    version="1.0.0",
)

# --- Custom Middleware for IP Validation ---
# @app.middleware("http")
# async def validate_ip(request: Request, call_next):
#     client_ip = request.client.host
#     if client_ip not in Config.ALLOWED_IPS:
#         logger.warning(f"Unauthorized IP attempt: {client_ip}")
#         raise HTTPException(status_code=403, detail="Unauthorized IP")
#     return await call_next(request)
#
# # --- Add Rate Limiter Middleware ---
# app.add_middleware(
#     RateLimiterMiddleware,
#     allowed_ips=Config.ALLOWED_IPS,
#     rate_limit=Config.RATE_LIMIT,
#     window_seconds=60  # You can adjust this as needed or add it to your Config
# )

# --- Include Resource Routers ---
app.include_router(healthcheck_router, prefix="/healthcheck")
app.include_router(search_router, prefix="/contact")
app.include_router(embedding_router, prefix="/embedding")


# --- Application Startup and Shutdown Events ---
@app.on_event("startup")
async def startup():
    logger.info("Application startup completed.")


@app.on_event("shutdown")
async def shutdown():
    logger.info("Application shutdown initiated.")


@app.route("/")
async def start():
    return JSONResponse({"Message": "This is external service, for application"})


# --- Run the Application using Uvicorn ---
if __name__ == "__main__":
    import uvicorn

    dotenv.load_dotenv()
    uvicorn.run(app, host="0.0.0.0", port=8000)
