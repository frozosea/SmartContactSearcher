<template>
    <router-link to="/add-contact" class="add-contact-btn">
      <span class="add-icon">
        <i class="fas fa-plus"></i> <!-- FontAwesome "+" icon -->
      </span>
      <span class="btn-text">Add Contact</span>
    </router-link>
  </template>
  
  <script setup>
  </script>
  
  <style scoped>
  .add-contact-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 10px 20px;
    background-color: #007bff;
    color: #fff;
    border: none;
    border-radius: 4px;
    text-decoration: none;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s ease;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start;
    margin-bottom: 20px;

  }
  
  .add-contact-btn:hover {
    background-color: #0056b3;
  }
  
  .add-icon {
    margin-right: 8px; /* Space between icon and text */
  }
  
  .add-icon i {
    font-size: 18px; /* Adjust the icon size */
  }
  
  .btn-text {
    font-size: 14px;
  }
  </style>
  