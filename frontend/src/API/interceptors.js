import axiosInstance from './axiosInstance';
import { tokenState } from '../utils/tokenState';

// Add a request interceptor
axiosInstance.interceptors.request.use(
  async (config) => {
    // Don't check refresh token for auth endpoints
    if (config.url?.includes('/auth/')) {
      return config;
    }

    // Check if token needs refresh before making the request
    if (tokenState.shouldRefreshToken()) {
      await tokenState.refreshTokens();
    }
    
    // Get the token from state
    const token = tokenState.getAuthToken();
    
    // If token exists, add it to the request headers
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If the error is 401 and we haven't tried to refresh the token yet
    if (error.response?.status === 401 && !originalRequest._retry && !originalRequest.url?.includes('/auth/')) {
      originalRequest._retry = true;

      try {
        // Try to refresh the token
        const success = await tokenState.refreshTokens();
        
        if (success) {
          // Retry the original request with the new token
          const token = tokenState.getAuthToken();
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return axiosInstance(originalRequest);
        }
      } catch (refreshError) {
        // If refresh fails, clear tokens and redirect to login
        tokenState.clearTokens();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
); 