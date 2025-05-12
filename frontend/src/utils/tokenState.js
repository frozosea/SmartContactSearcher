const TOKEN_KEY = 'auth_token';
const REFRESH_TOKEN_KEY = 'refresh_token';
const TOKEN_EXPIRY_KEY = 'token_expiry';

class TokenState {
  constructor() {
    this._authToken = localStorage.getItem(TOKEN_KEY) || null;
    this._refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY) || null;
    this._tokenExpiry = localStorage.getItem(TOKEN_EXPIRY_KEY) ? parseInt(localStorage.getItem(TOKEN_EXPIRY_KEY)) : null;
    this._refreshCallback = null;
    this._isRefreshing = false;
    this._refreshSubscribers = [];
  }

  setTokens(authToken, refreshToken, expiresIn) {
    this._authToken = authToken;
    this._refreshToken = refreshToken;
    this._tokenExpiry = Date.now() + (expiresIn * 1000);
    
    // Store in localStorage
    localStorage.setItem(TOKEN_KEY, authToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    localStorage.setItem(TOKEN_EXPIRY_KEY, this._tokenExpiry.toString());
  }

  getAuthToken() {
    return this._authToken;
  }

  getRefreshToken() {
    return this._refreshToken;
  }

  clearTokens() {
    this._authToken = null;
    this._refreshToken = null;
    this._tokenExpiry = null;
    this._isRefreshing = false;
    this._refreshSubscribers = [];
    
    // Clear localStorage
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(TOKEN_EXPIRY_KEY);
  }

  setRefreshCallback(callback) {
    this._refreshCallback = callback;
  }

  subscribeToRefresh(callback) {
    this._refreshSubscribers.push(callback);
  }

  notifySubscribers(token) {
    this._refreshSubscribers.forEach(callback => callback(token));
    this._refreshSubscribers = [];
  }

  async refreshTokens() {
    try {
      // If already refreshing, wait for the current refresh to complete
      if (this._isRefreshing) {
        return new Promise(resolve => {
          this.subscribeToRefresh(token => {
            resolve(true);
          });
        });
      }

      this._isRefreshing = true;

      if (this._refreshCallback) {
        const success = await this._refreshCallback();
        this._isRefreshing = false;
        
        if (success) {
          this.notifySubscribers(this._authToken);
          return true;
        }
      }

      this._isRefreshing = false;
      return false;
    } catch (error) {
      this._isRefreshing = false;
      this.clearTokens();
      return false;
    }
  }

  shouldRefreshToken() {
    if (!this._tokenExpiry) return false;
    // Refresh if token expires in less than 5 minutes
    return Date.now() >= (this._tokenExpiry - 5 * 60 * 1000);
  }

  isTokenExpired() {
    if (!this._tokenExpiry) return true;
    return Date.now() >= this._tokenExpiry;
  }
}

export const tokenState = new TokenState(); 