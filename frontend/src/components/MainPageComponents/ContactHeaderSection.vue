<template>
    <div class="contacts-header">
      <h1>Contacts</h1>
      <p class="total-count">{{ totalContacts }} Contacts</p>
    </div>
  </template>
  
  <script setup>
  import { defineProps } from 'vue';
  import { ref } from 'vue';
  const props = defineProps({
    totalContacts: Number
  });
  </script>
  
  <style scoped>
  .contacts-header {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start;
    margin-bottom: 20px;
  }
  
  h1 {
    font-size: 32px;
    color: #333;
  }
  
  .total-count {
    font-size: 16px;
    color: #777;
  }
  </style>
  