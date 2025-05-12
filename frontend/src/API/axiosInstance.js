import axios from 'axios';

// Base URL for the backend API
const API_BASE_URL = 'http://localhost:8002';

// Create an Axios instance with a base URL.
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
});

// Helper to conditionally add the Authorization header if a token is provided.
export const authHeader = (token) => (token ? { headers: { Authorization: `Bearer ${token}` } } : {});

export default axiosInstance; 