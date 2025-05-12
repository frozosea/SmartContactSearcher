<template>
    <div class="contact-list-section">
      <div v-if="contacts.length === 0" class="no-contacts">
        <img src="../../assets/question_mark_icon.svg" alt="No Contacts" class="no-contacts-icon" />
        <p>Contacts not found, please add contacts</p>
      </div>
      
      <div v-else class="contact-list">
        <ContactPreview v-for="contact in contacts" :key="contact.id" :contact="contact" v-on:showContactDataModal="showContactDataModal"/>
      </div>
    </div>
  </template>
  
  <script setup>
  import { defineProps,defineEmits } from 'vue';
  import ContactPreview from './ContactPreview.vue';
  // Props: contacts data
  const props = defineProps({
    contacts: Array
  });
  const emit = defineEmits(['showContactDataModal'])

  const showContactDataModal = (data) =>{
    console.log(data)
    // Emit the event to show the contact data modal
    emit('showContactDataModal',data);
  }

  </script>
  
  <style scoped>
  .contact-list-section {
    height: 100%;
    overflow-y: auto; /* Enables scrolling for the contact list */
  }
  
  .contact-list {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }
  
  .no-contacts {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    text-align: center;
  }
  
  .no-contacts-icon {
    width: 50px;
    height: 50px;
    margin-bottom: 10px;
  }
  
  .no-contacts p {
    font-size: 16px;
    color: var(--color-text-light-2);
  }
  </style>
  