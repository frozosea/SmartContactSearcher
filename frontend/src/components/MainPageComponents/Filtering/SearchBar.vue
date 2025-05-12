<template>
    <div class="search-bar">
      <div class="search-container">
        <!-- Search Input -->
        <input
          v-model="searchText"
          type="text"
          placeholder="Search contacts..."
          class="search-input"
        />
        
        <!-- Search Button -->
        <button @click="applySearch" class="search-btn" :disabled="isLoading">Search</button>
      </div>
      
      <!-- Clear Query Button (only appears if search has been made) -->
      <button v-if="searchText && noResultsFound" @click="clearQuery" class="clear-btn">Clear Query</button>
      
      <!-- No results message -->
      <p v-if="noResultsFound" class="no-results">No results found for your query.</p>
  
      <!-- Loading Spinner -->
      <div v-if="isLoading" class="spinner">Loading...</div>
    </div>
  </template>
  
  <script setup>
  import { ref, defineProps,defineEmits } from 'vue';
  import { useStore } from 'vuex';
  const props = defineProps({
    contacts: Array, // Assuming contacts are passed as a prop
  })
  const emit = defineEmits(['applySearch'])
  const store = useStore();
  const token = store.getters.authToken; // Assuming token is stored in the auth state
  
  const searchText = ref('');
  const noResultsFound = ref(false);
  const searchResults = ref([]);
  const isLoading = ref(false); // To track loading state
  
  // Apply Search logic when the user presses the "Search" button
  const applySearch = async () => {
    noResultsFound.value = false; // Reset the "no results" message
    store.commit('setShowSearchingSpinner', true); // Start loading spinner
  
    if (searchText.value.trim()) {
      // First, try filtering the contacts in store
      const filteredResults = props.contacts.filter(contact =>
        contact.name.toLowerCase().includes(searchText.value.toLowerCase())
      );
  
      if (filteredResults.length > 0) {
        // If results found locally, use them
        searchResults.value = filteredResults;
        emit('applySearch', searchResults)
        return
      } else {
        // If no results found locally, perform the smart search request
        const searchData = {
          user_id: store.state.userId,
          query_text: searchText.value,
          k: 5,
          num_candidates: 10
        };
  
        try {
          const response = await store.getters.getApi.query.searchContacts(searchData, token);
          console.log(response.data)
          if (response.data.length > 0) {
            searchResults.value = response.data;
            emit('applySearch', searchResults)
          } else {
            noResultsFound.value = true;
          }
        } catch (error) {
          console.error('Error during smart search:', error);
          noResultsFound.value = true;
        }
      }
    } else {
      // If the search text is empty, reset results
      searchResults.value = [];
    }
  
    store.commit('setShowSearchingSpinner', false); // Stop loading spinner
  };
  
  // Clear the search query and reset results
  const clearQuery = () => {
    searchText.value = '';
    searchResults.value = [];
    noResultsFound.value = false;
    store.commit('setShowSearchingSpinner', false); // Reset spinner
  };
  </script>
  
  <style scoped>
  .search-bar {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    margin-top: 50px;
    padding-bottom: 20px;
  }
  
  .search-container {
    display: flex;
    width: 100%;
    gap: 10px;
    align-items: stretch;
  }
  
  .search-input {
    flex: 1;
    padding: 12px 16px;
    font-size: 16px;
    border-radius: 6px;
    border: 1px solid #ccc;
    transition: all 0.3s ease;
  }
  
  .search-input:focus {
    border-color: #007bff;
    outline: none;
    box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
  }
  
  .search-btn {
    padding: 12px 24px;
    border-radius: 6px;
    background-color: #007bff;
    color: white;
    border: none;
    cursor: pointer;
    transition: background-color 0.3s ease;
    font-size: 16px;
    font-weight: 500;
    white-space: nowrap;
  }
  
  .search-btn:hover {
    background-color: #0056b3;
  }
  
  .search-btn:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
  }
  
  .clear-btn {
    padding: 8px 16px;
    border-radius: 6px;
    background-color: #dc3545;
    color: white;
    border: none;
    cursor: pointer;
    transition: background-color 0.3s ease;
    font-size: 14px;
  }
  
  .clear-btn:hover {
    background-color: #c82333;
  }
  
  .no-results {
    color: red;
    font-size: 14px;
  }
  
  .spinner {
    font-size: 14px;
    color: #007bff;
    font-weight: bold;
  }
  </style>
  