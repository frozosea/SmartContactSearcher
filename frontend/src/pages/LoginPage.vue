<template>
    <div class="login-page">
      <div class="login-box">
        <h2>Sign In</h2>
  
        <!-- Login Form -->
        <form @submit.prevent="login">
          <div class="input-group">
            <label for="email">Email</label>
            <input
              v-model="email"
              type="email"
              id="email"
              placeholder="Enter your email"
              required
            />
          </div>
          <div class="input-group">
            <label for="password">Password</label>
            <input
              v-model="password"
              type="password"
              id="password"
              placeholder="Enter your password"
              required
            />
          </div>
  
          <!-- Submit Button -->
          <button type="submit" class="btn">Sign In</button>
        </form>
      </div>
    </div>
    <notifications position="bottom left" />
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useStore } from 'vuex';
  import { AuthApi } from '../API/index'; // Adjust path as needed
  import { useRouter } from 'vue-router';
  import { useNotification } from "@kyvg/vue3-notification";
  import { Notifications } from '@kyvg/vue3-notification';
  const router = useRouter(); // Correctly use the router instance
  const { notify } = useNotification()

  // Declare reactive variables
  const email = ref('');
  const password = ref('');
  
  // Access Vuex store
  const store = useStore();

  // Handle login functionality
  const login = async () => {
    try {
      // Send login request to backend
      const response = await new AuthApi().login(email.value, password.value);
      
      // Extract JWT token from response
      const { token, refreshToken , username, userId} = response.data;
  
      // Store the token in Vuex
      store.commit('setAuthToken', token);
      store.commit('setRefreshToken',refreshToken)
      store.commit('setUsername', username);
      store.commit('setUserId', userId);
      // Set isAuth to true in Vuex
      store.commit('setIsAuth', true);
      notify({
            title: 'Login Successful!',
            type: 'success',
            speed: 200,
            position: 'bottom left',
            duration: 1000
          });
      // Optionally redirect to another page (e.g., the dashboard)
      router.push('/');
    } catch (error) {
      console.error("Login failed:", error);
      // Handle errors (e.g., show an error message to the user)
    }
  };
  </script>
  
  <style scoped>
  @import '../assets/base.css';

.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  padding: 2rem;
}

.login-box {
  position: fixed;
  background-color: var(--color-background-soft);
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  width: 550px;
  max-width: 800px;
}

.login-box h2 {
  margin-bottom: 20px;
  color: var(--color-heading);
  font-size: 24px;
  text-align: center;
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  color: var(--color-text);
  font-size: 14px;
  margin-bottom: 8px;
}

.input-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  background-color: var(--color-background-mute);
  font-size: 14px;
  color: var(--color-text);
}

.input-group input:focus {
  border-color: var(--color-heading);
  background-color: var(--color-background-soft);
  outline: none;
}

.btn {
  width: 100%;
  padding: 15px;
  background-color: var(--vt-c-indigo);
  color: var(--vt-c-white);
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn:hover {
  background-color: hsla(160, 100%, 37%, 0.9);
}
@media (min-width: 1024px) {
  .login-page {
    padding: 0 2rem; /* Additional padding for larger screens */
  }

  .login-box {
    max-width: 600px; /* Increase max-width of the form box */
  }
}
  </style>
  