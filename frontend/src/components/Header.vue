<template>
    <header class="header">
      <!-- Left: Clickable logo -->
      <div class="header-left">
        <router-link to="/">
          <img src="@/assets/logo.svg" alt="Logo" class="logo" />
        </router-link>
      </div>
  
      <!-- Right: Authenticated vs. non-authenticated options -->
      <div class="header-right">
        <!-- Not signed in: show login and register buttons -->
        <template v-if="!isAuth">
          <router-link to="/login" class="btn">Login</router-link>
          <router-link to="/register" class="btn">Register</router-link>
        </template>
  
        <!-- Signed in: show a rounded block linking to contact database -->
        <template v-else>
          <button @click="handleLogout" class="btn btn-logout">Logout</button>
        </template>
      </div>
    </header>
  
    <!-- Horizontal line to separate header from main page -->
  </template>
  
  <script setup>
  import { computed } from 'vue';
  import { useStore } from 'vuex';
  import { useRouter } from 'vue-router';
  
  // Access the global login state from Vuex
  const store = useStore();
  const router = useRouter();
  const isAuth = computed(() => store.getters.isLogin);
  
  const handleLogout = () => {
    store.dispatch('logout');
    router.push('/login');
  };
  </script>
  
  <style scoped>
  /* Make sure the header is at the top of the page */
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
  
  .header {
    display: flex;
    justify-content: space-between; /* Ensures logo is on the left and buttons are on the right */
    align-items: center;
    padding: 10px 20px;
    background-color: #333; /* Background color for the header */
    color: white; /* Text color */
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 1000; /* Ensure the header stays on top */
  }
  
  /* Logo styling */
  .logo {
    height: 40px;
    cursor: pointer;
  }
  
  /* Button styling for login/register */
  .btn {
    margin-left: 10px;
    padding: 10px 16px;
    background-color: #007bff;
    color: #fff;
    text-decoration: none;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    font-weight: 500;
  }
  
  .btn:hover {
    background-color: #0056b3;
  }
  
  .btn-logout {
    background-color: #dc3545;
  }
  
  .btn-logout:hover {
    background-color: #c82333;
  }
  
  /* Divider between header and main content */
  .header-divider {
    width: 100%;
    border: 0;
    border-top: 1px solid #ccc;
    margin-top: 60px; /* Adjust to make space below the fixed header */
  }
  </style>
  