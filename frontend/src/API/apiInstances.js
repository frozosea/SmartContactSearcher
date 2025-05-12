import axiosInstance, { authHeader } from './axiosInstance';
import './interceptors';

// --- Auth API ---
export class AuthApi {
  // Register user: POST /auth/register
  register(username, password) {
    return axiosInstance.post('/auth/register', { username, password });
  }

  // Login user: POST /auth/login
  login(username, password) {
    return axiosInstance.post('/auth/login', { username, password });
  }

  // Refresh token: POST /auth/refresh
  refresh(refreshToken) {
    return axiosInstance.post('/auth/refresh', { refreshToken });
  }
}

// --- User API ---
export class UserApi {
  // Get a user by ID: GET /api/users/{id}
  getUser(id, token) {
    return axiosInstance.get(`/api/users/${id}`, authHeader(token));
  }

  // Update a user: PUT /api/users/{id}
  updateUser(id, userData, token) {
    return axiosInstance.put(`/api/users/${id}`, userData, authHeader(token));
  }

  // Delete a user: DELETE /api/users/{id}
  deleteUser(id, token) {
    return axiosInstance.delete(`/api/users/${id}`, authHeader(token));
  }

  // Get all users: GET /api/users
  getAllUsers(token) {
    return axiosInstance.get(`/api/users`, authHeader(token));
  }

  // Create a new user: POST /api/users
  createUser(userData, token) {
    return axiosInstance.post(`/api/users`, userData, authHeader(token));
  }
}

// --- Tag API ---
export class TagApi {
  // Get a tag by ID: GET /api/tags/{id}
  getTag(id, token) {
    return axiosInstance.get(`/api/tags/${id}`, authHeader(token));
  }

  // Update a tag: PUT /api/tags/{id}
  updateTag(id, tagData, token) {
    return axiosInstance.put(`/api/tags/${id}`, tagData, authHeader(token));
  }

  // Delete a tag: DELETE /api/tags/{id}
  deleteTag(id, token) {
    return axiosInstance.delete(`/api/tags/${id}`, authHeader(token));
  }

  // Get all tags: GET /api/tags
  getAllTags(token) {
    return axiosInstance.get(`/api/tags`, authHeader(token));
  }

  // Create a new tag: POST /api/tags
  createTag(tagData, token) {
    return axiosInstance.post(`/api/tags`, {name: tagData}, authHeader(token));
  }
}

// --- Note API ---
export class NoteApi {
  // Get a note by ID: GET /api/notes/{id}
  getNote(id, token) {
    return axiosInstance.get(`/api/notes/${id}`, authHeader(token));
  }

  // Update a note: PUT /api/notes/{id}
  updateNote(id, noteData, token) {
    return axiosInstance.put(`/api/notes/${id}`, noteData, authHeader(token));
  }

  // Delete a note: DELETE /api/notes/{id}
  deleteNote(id, token) {
    return axiosInstance.delete(`/api/notes/${id}`, authHeader(token));
  }

  // Get all notes: GET /api/notes
  getAllNotes(token) {
    return axiosInstance.get(`/api/notes`, authHeader(token));
  }

  // Create a new note: POST /api/notes
  createNote(contactId,noteData, token) {
    return axiosInstance.post(`/api/notes`, {"content":noteData, "contact": contactId}, authHeader(token));
  }

  // Get notes by contact ID: GET /api/notes/contact_{id}
  getNoteByContactId(id, token) {
    return axiosInstance.get(`/api/notes/contact/${id}`, authHeader(token));
  }
}

// --- Contact API ---
export class ContactApi {
  // Get a contact by ID: GET /api/contacts/{id}
  getContact(id, token) {
    return axiosInstance.get(`/api/contacts/${id}`, authHeader(token));
  }

  // Update a contact: PUT /api/contacts/{id}
  updateContact(id, contactData, token) {
    return axiosInstance.put(`/api/contacts/${id}`, contactData, authHeader(token));
  }

  // Delete a contact: DELETE /api/contacts/{id}
  deleteContact(id, token) {
    return axiosInstance.delete(`/api/contacts/${id}`, authHeader(token));
  }

  // Get all contacts: GET /api/contacts
  getAllContacts(token) {
    return axiosInstance.get(`/api/contacts`, authHeader(token));
  }

  // Create a new contact: POST /api/contacts
  createContact(contactData, token) {
    return axiosInstance.post(`/api/contacts`, contactData, authHeader(token));
  }

  // Get contacts by user: GET /api/contacts/owner_{id}
  getContactsByUser(id, token) {
    return axiosInstance.get(`/api/contacts/owner/${id}`, authHeader(token));
  }
}

// --- Query API ---
export class QueryApi {
  // Search contacts by free text: POST /api/query/search
  searchContacts(searchData, token) {
    return axiosInstance.post(`/api/query/search`, searchData, authHeader(token));
  }

  // Search contacts by tags: POST /api/query/search/tags
  searchContactsByTags(searchData, token) {
    return axiosInstance.post(`/api/query/search/tags`, searchData, authHeader(token));
  }

  // Generate embedding: POST /api/query/embedding
  generateEmbedding(embeddingData, token) {
    return axiosInstance.post(`/api/query/embedding`, embeddingData, authHeader(token));
  }
}

// --- Home API ---
export class HomeApi {
  // Home index: GET /
  index() {
    return axiosInstance.get(`/`);
  }
} 