<template>
    <div class="login-page">
      <div class="login-box">
        <h2>Sign Up</h2>
  
        <!-- Sign Up Form -->
        <form @submit.prevent="submitForm">
          <div class="input-group">
            <label for="email">Email</label>
            <input
              v-model="email"
              type="email"
              id="email"
              placeholder="Enter your email"
              @input="handleEmail"
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
              @input="handlePasswordInput"
              required
            />
          </div>
          <div class="input-group">
            <label for="confirmPassword">Confirm Password</label>
            <input
              v-model="confirmPassword"
              type="password"
              id="confirmPassword"
              placeholder="Repeat your password"
              @input="handleRepeatedPasswordInput"
              required
            />
            <!-- Error message for non-matching passwords -->
            <p v-if="showError" class="error-text">{{ errorMessage }}</p>
          </div>
  
          <!-- Submit Button -->
          <button :disabled="!valid" type="submit" class="btn">Sign Up</button>
        </form>
      </div>
    </div>
    <notifications position="bottom left" />

  </template>
  
  <script>
  import { ref } from 'vue';
  import { useStore } from 'vuex';
  import { useRouter } from 'vue-router';
  import { notify } from "@kyvg/vue3-notification";
  import { Notifications } from '@kyvg/vue3-notification';

  const router = useRouter(); // Correctly use the router instance

  export default {
    name: 'SignUpPage',
    setup() {
      // Reactive state
      const email = ref('');
      const password = ref('');
      const confirmPassword = ref('');
      const valid = ref(true); // Validation flag
      const showError = ref(false);
      const errorMessage = ref('');
      // Vuex store and notifications
      const store = useStore();
      console.log(store.getters)
      // Handle Email Input
      const handleEmail = (e) => {
        const emailValue = e.target.value;
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailValue)) {
          valid.value = false;
          showError.value = true;
          errorMessage.value = 'Please enter a valid email address.';
        } else {
          valid.value = true;
          showError.value = false;
          errorMessage.value = '';
          email.value = emailValue;
        }
      };
  
      // Handle Password Input
      const handlePasswordInput = (e) => {
        password.value = e.target.value;
        if (password.value !== confirmPassword.value) {
          valid.value = false;
          showError.value = true;
          errorMessage.value = 'Passwords do not match';
        } else {
          valid.value = true;
          showError.value = false;
          errorMessage.value = '';
        }
      };
  
      // Handle Repeated Password Input
      const handleRepeatedPasswordInput = (e) => {
        confirmPassword.value = e.target.value;
        if (password.value !== confirmPassword.value) {
          valid.value = false;
          showError.value = true;
          errorMessage.value = 'Passwords do not match';
        } else {
          valid.value = true;
          showError.value = false;
          errorMessage.value = '';
        }
      };
  
      // Form Submission
      const submitForm = async () => {
        try {
          showError.value = false;
          // Call the API for registration
          const api = store.getters.getApi.auth;
          const response = await api.register(email.value, password.value);
          
          // If registration is successful, show notification
          notify({
            title: 'Registration Successful!',
            type: 'success',
            text: 'You can now log in.',
          });
  
          // Call the login API to authenticate
          const loginResponse = await api.login(email.value, password.value);
  
          // Store user information in Vuex store
          store.commit('setAuthToken', loginResponse.tokens);
          store.commit('setRefreshToken', loginResponse.refreshToken);
          store.commit('setUsername', loginResponse.username);
          store.commit('setIsAuth', true);
  
          // Redirect to login page
          router.push('/');
        } catch (error) {
          // Handle errors such as user already existing
          if (error.response && error.response.data.message === 'User already exists') {
            errorMessage.value = 'User with this email already exists!';
            showError.value = true;
          } else {
            console.log(error)
            errorMessage.value = 'Something went wrong. Please try again.';
            showError.value = true;
          }
          valid.value = false;
        }
      };
  
      return {
        email,
        password,
        confirmPassword,
        valid,
        showError,
        errorMessage,
        handleEmail,
        handlePasswordInput,
        handleRepeatedPasswordInput,
        submitForm,
      };
    },
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
  
  .btn:disabled {
    background-color: grey;
    cursor: not-allowed;
  }
  
  /* Error text */
  .error-text {
    color: red;
    font-size: 12px;
    margin-top: 5px;
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
  