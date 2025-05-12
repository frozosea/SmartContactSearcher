// store/index.js
import { createStore } from 'vuex';
import { AuthApi, UserApi, TagApi, NoteApi, ContactApi, QueryApi, HomeApi } from '../API';
import { tokenState } from '../utils/tokenState';

// Create API instances
const api = {
  auth: new AuthApi(),
  user: new UserApi(),
  tag: new TagApi(),
  note: new NoteApi(),
  contact: new ContactApi(),
  query: new QueryApi(),
  home: new HomeApi(),
};

export default createStore({
  state: {
    // JWT auth token
    authToken: tokenState.getAuthToken() || "",
    refreshToken: tokenState.getRefreshToken() || "",
    
    // Flag for login status
    isLogin: !!tokenState.getAuthToken(),
    // API classes instances to work with the backend
    userId: 1,
    tagFilters: [],
    filterContacts: [],
    jobTitleFilters: [],
    contactsData: [],
    contactModalData: null,
    contactModalDataVisibility: false,
    ShowSearchingSpinner: false, 
    selectedContact: {},
    username: "",
    api,
  },
  mutations: {
    // Set the JWT token and update login status
    setAuthToken(state, { token, refreshToken, expiresIn }) {
      state.authToken = token;
      state.refreshToken = refreshToken;
      state.isLogin = !!token;
      tokenState.setTokens(token, refreshToken, expiresIn);
    },
    setIsAuth(state, payload){
      state.isLogin = payload;
    },
    setIsLogin(state, payload){
      state.isLogin = payload;
    },
    // Clear the JWT token and set login status to false
    clearAuthToken(state) {
      state.authToken = null;
      state.refreshToken = null;
      state.isLogin = false;
      tokenState.clearTokens();
    },
    clearRefreshToken(state){
      state.authToken = null;
      state.refreshToken = null;
      state.isLogin = false;
      tokenState.clearTokens();
    },
    setRefreshToken(state, payload){
      state.refreshToken = payload;
    },
    setTagFilters(state, payload){
      state.tagFilters = payload
    },
    setFilterContacts(state, payload){
      state.filterContacts = payload
    },
    setJobTitleFilters(state, payload){
      state.jobTitleFilters = payload
    },
    setShowSearchingSpinner(state, payload){
      state.ShowSearchingSpinner = payload
    },
    setContactModalData(state, payload){
      state.contactModalData = payload
    },
    setContactModalVisibility(state,payload){
      state.contactModalDataVisibility = payload
    },
    setUsername(state, payload){
      state.username = payload
    },
    setUserId(state,payload){
      state.userId = payload
    },
    setContactsData(state, payload){
      state.contactsData = payload
    },
    setSelectedContact(state, payload){
      state.selectedContact = payload
    },
  },
  actions: {
    // Action to handle login, expecting the token as payload
    async login({ commit }, { token, refreshToken, expiresIn }) {
      commit('setAuthToken', { token, refreshToken, expiresIn });
      commit('setIsLogin', true);
    },
    // Action to handle logout
    logout({ commit }) {
      commit('clearAuthToken');
    },
    // Action to refresh token
    async refreshToken({ commit, state }) {
      try {
        const response = await state.api.auth.refresh(state.refreshToken);
        const { token, refreshToken, expiresIn } = response.data;
        commit('setAuthToken', { token, refreshToken, expiresIn });
        return true;
      } catch (error) {
        commit('clearAuthToken');
        return false;
      }
    },
    // Action to check and refresh token if needed
    async checkAndRefreshToken({ dispatch, state }) {
      if (tokenState.shouldRefreshToken()) {
        return await dispatch('refreshToken');
      }
      return true;
    }
  },
  getters: {
    getAuthToken: (state) => state.authToken,
    getRefreshToken: (state) => state.refreshToken,
    isLogin: (state) => state.isLogin,
    // Access the API classes instance(s)
    getUserId: (state) => state.userId,
    getApi: (state) => state.api,
    getTagFilters: (state) => state.tagFilters,
    getFilterContacts: (state) => state.filterContacts,
    getJobTitleFilters: (state) => state.jobTitleFilters,
    getShowSearchingSpinner: (state) => state.ShowSearchingSpinner,
    getContactModalData: (state) => state.contactModalData,
    getContactModalVisibility: (state) => state.contactModalDataVisibility,
    getUsername: (state) => state.username,
    getUserId: (state) => state.userId,
    getContactsData: (state) => state.contactsData,
    getSelectedContact: (state) => state.selectedContact,
  },
});
