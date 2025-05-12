<template>
    <div class="contact-preview">
      <!-- Left: Contact Icon -->
      <img src="../../assets/contact.svg" alt="Contact Icon" class="contact-icon" />
      
      <!-- Main contact information -->
      <div class="contact-info">
        <p class="contact-name">{{ contact.name }}</p>
        <p class="contact-job-title">{{ contact.jobTitle }}</p>
      </div>
      
      <!-- Action buttons -->
      <div class="action-buttons">
        <button @click="openMoreModal" class="more-btn">MORE</button>
        <a :href="'mailto:' + contact.email" class="mail-btn">
          <img src="../../assets/email.svg" alt="Mail Icon" class="action-icon" >
        </img>
        </a>
        <a :href="'tel:' + contact.phone" class="phone-btn">
          <img src="../../assets/phone-call.svg" alt="Phone Icon" class="action-icon" />
        </a>
      </div>
    </div>
  </template>
  
  <script setup>
  import { defineProps,defineEmits } from 'vue';
  import { useStore } from 'vuex';

  // Props: contact data
  const props = defineProps({
    contact: Object
  });
  const emit = defineEmits(['showContactDataModal'])
  console.log(props.contact)
  // Store access (for token or other state if needed)
  const store = useStore();
  const token = store.getters.authToken; // Assuming token is stored in the auth state

  // Format the timestamp to "YYYY-MM-DD HH:mm"
  const formatTimestamp = (timestamp) => {
    console.log(timestamp)
    const date = new Date(timestamp);
    return date.toISOString().slice(0, 16).replace('T', ' ');
  };
  
  // Open MORE modal and fetch additional data for the contact
  const openMoreModal = async () => {
    try {
      // Show the modal (passing the full contact info with notes and tags)
      // Open the ContactModal component (assumed setup)
      store.commit('setContactModalVisibility', true);
      store.commit('setSelectedContact', props.contact);
      console.log(props.contact)
      emit('showContactDataModal', props.contact)
    } catch (error) {
      console.error('Error fetching contact details:', error);
    }
  };
  </script>
  
  <style scoped>
.contact-preview {
  display: flex;
  align-items: center;
  background-color: var(--color-background-soft);
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.contact-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 15px;
}

/* Container for Name and Job Title in one row */
.contact-info {
  flex-grow: 1;
  display: flex;
  align-items: center;
  gap: 20px; /* fixed gap between name and job title */
}

/* Fixed width for name with ellipsis and bold styling */
.contact-name {
  font-size: 18px;
  color: var(--color-heading);
  font-weight: bold;
  max-width: 200px;  /* adjust as needed */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Fixed width for job title with ellipsis */
.contact-job-title {
  font-size: 16px;
  color: var(--color-text-light-2);
  max-width: 150px;  /* adjust as needed */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.action-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* Fixed-size buttons with green background; text won’t affect size */
.more-btn,
.mail-btn,
.phone-btn {
  width: 80px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #28a745;
  color: #fff;
  border-radius: 5px;
  text-decoration: none;
  cursor: pointer;
  font-size: 14px;
  border: none;
  padding: 0; /* ensure fixed dimensions */
}

.more-btn:hover,
.mail-btn:hover,
.phone-btn:hover {
  background-color: #218838;
}

/* Center icons inside the fixed-size button */
.action-icon {
  width: 18px;
  height: 18px;
  display: block;
  /* Removed margin to ensure centering */
}
</style>
  
  