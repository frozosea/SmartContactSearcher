<template>
    <div class="not-found-page">
      <img src="../assets/not_found.svg" alt="404 Not Found" class="not-found-icon" />
      <div class="not-found-text">404 Not Found</div>
      <notifications position="bottom left" />

    </div>
  </template>
  
  <script>
  import { onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { Notifications } from '@kyvg/vue3-notification';
  import { useNotification } from "@kyvg/vue3-notification";
  const { notify } = useNotification()

  notify({
            title: 'Page not found!',
            type: 'warning',
            speed: 200,
            position: 'bottom left',
            duration: 1000
          });

  export default {
    setup() {
      const router = useRouter();
      
      onMounted(() => {
        setTimeout(() => {
          router.push('/');  // Redirect to the main page after 3 seconds
        }, 60000000);
      });
    },
  };
  </script>
  
  <style scoped>
  .not-found-page {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
    text-align: center;
  }
  
  .not-found-icon {
    position: fixed;
    width: 200px; /* Adjust size as needed */
    height: auto;
    margin-bottom: 20px;
  }
  
  .not-found-text {
    position: fixed;
    font-size: 24px;
    color: var(--color-text);
  }
  </style>
  