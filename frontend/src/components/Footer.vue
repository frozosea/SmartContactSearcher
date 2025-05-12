<template>
    <footer class="footer">
      <!-- Left: Links for non-authenticated users -->
      <div class="footer-left">
        <template v-if="!isAuth">
          <router-link to="/login" class="footer-link">Login</router-link>
          <span>|</span>
          <router-link to="/register" class="footer-link">Register</router-link>
        </template>
  
        <!-- Right: Profile link for authenticated users -->
        <template v-else>
          <router-link to="/profile" class="profile-link">
            <span class="profile-icon">
              <i class="fas fa-user"></i> <!-- Replace with your icon -->
            </span>
            <span class="profile-text"><strong>Profile</strong></span>
          </router-link>
        </template>
      </div>
    </footer>
    
    <!-- Divider between footer and main content -->
  </template>
  
  <script setup>
  import { computed } from 'vue';
  import { useStore } from 'vuex';
  
  // Access the global login state from Vuex
  const store = useStore();
  const isAuth = computed(() => store.getters.isLogin);
  </script>
  
  <style scoped>
  /* Make sure the footer is fixed at the bottom of the page */
  body, html {
    margin: 0;
    padding: 0;
    height: 100%;
  }
  
  #app {
    display: flex;
    flex-direction: column;
    height: 100%;
  }
  
  .footer {
    display: flex;
    justify-content: space-between; /* Logo on the left and buttons on the right */
    align-items: center;
    padding: 10px 20px;
    background-color: #333; /* Background color for the footer */
    color: white;
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    z-index: 1000; /* Ensure footer stays at the bottom */
  }
  
  /* Footer Link Styling */
  .footer-link {
    text-decoration: none;
    color: #007bff;
    margin: 0 10px;
  }
  
  .footer-link:hover {
    text-decoration: underline;
  }
  
  /* Profile link styling (similar to header's profile block) */
  .profile-link {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    background-color: #f0f0f0;
    border-radius: 20px;
    text-decoration: none;
    color: #333;
  }
  
  .profile-link:hover {
    background-color: #e0e0e0;
  }
  
  .profile-icon {
    margin-right: 2px;
  }
  
  .profile-text {
    font-size: 14px;
  }
  
  /* Divider between footer and main content */
  .footer-divider {
    width: 100%;
    border: 0;
    border-top: 1px solid #ccc;
    margin-top: 60px; /* Adjust to make space below the fixed footer */
  }
  </style>
  