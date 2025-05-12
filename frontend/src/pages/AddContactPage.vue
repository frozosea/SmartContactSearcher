<template>
    <div class="add-contact-form">
    <notifications position="bottom left" />

      <h1>Add New Contact</h1>
  
      <form @submit.prevent="submitForm">
        <!-- Name -->
        <div class="form-group">
          <label for="name">Name:</label>
          <input v-model="contact.name" type="text" id="name" required />
        </div>
  
        <!-- Job Title -->
        <div class="form-group">
          <label for="jobTitle">Job Title:</label>
          <input v-model="contact.jobTitle" type="text" id="jobTitle" required />
        </div>

        <div class="form-group">
          <label for="jobTitle">Email:</label>
          <input v-model="contact.email" type="text" id="jobTitle" required />
        </div>

        <div class="form-group">
          <label for="jobTitle">Phone number:</label>
          <input v-model="contact.phone" type="text" id="jobTitle" required />
        </div>
  
        <!-- Tags -->
        <div class="form-group">
          <label>Tags:</label>
          <div class="tags">
            <div 
              v-for="(tag, index) in tags" 
              :key="index" 
              :class="['tag', { selected: selectedTags.includes(tag.id) }]"
              @click="toggleTag(tag)">
              {{ tag.name }}
            </div>
          </div>
          <button type="button" @click="addNewTag">Add New Tag</button>
        </div>
  
        <!-- Note -->
        <div class="form-group">
          <label for="note">Note:</label>
          <textarea v-model="note" id="note" placeholder="Write a note about the contact..."></textarea>
        </div>
  
        <!-- Submit Button -->
        <button type="submit" :disabled="!isValidForm">Save Contact</button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref,onMounted,computed } from 'vue';
  import { useStore } from 'vuex';
  import { useRouter } from 'vue-router';
  import { useNotification } from "@kyvg/vue3-notification";
  import { Notifications } from '@kyvg/vue3-notification';

  const store = useStore();
  const router = useRouter();
  const { notify } = useNotification();
  
  const contact = ref({
    name: '',
    jobTitle: '',
    tags: [],
    phone: '',
    email: ''
  });
  const jobTitles = ref([]);
  const tags = ref([]);
  const selectedTags = ref([]);
  const note = ref('');
  
  // Get tags and job titles
  onMounted(async () => {
    try {
      const token = store.getters.authToken;
      const response = await store.getters.getApi.tag.getAllTags(token);
      tags.value = response.data;
      jobTitles.value = ['Software Engineer', 'Factory Engineer', 'Sales']; // Example job titles
    } catch (error) {
      console.error('Error fetching tags:', error);
    }
  });
  
  // Handle tag selection
  const toggleTag = (tag) => {
    if (selectedTags.value.includes(tag.id)) {
      selectedTags.value = selectedTags.value.filter(item => item !== tag.id);
    } else {
      selectedTags.value.push(tag.id);
    }
  };
  
  // Handle adding a new tag
  const addNewTag = async () => {
    const newTag = prompt('Enter the new tag:');
    if (newTag) {
      const token = store.getters.authToken;
      try {
        const response = await store.getters.getApi.tag.createTag({ name: newTag }, token);
        tags.value.push(response.data);
      } catch (error) {
        console.error('Error creating new tag:', error);
      }
    }
  };
  
  // Handle form submission
  const submitForm = async () => {
    if (selectedTags.value.length === 0) {
      alert('Please select at least one tag!');
      return;
    }
  
    const token = store.getters.authToken;
  
    try {
      // Save contact
      const contactData = { 
        name: contact.value.name, 
        jobTitle: contact.value.jobTitle, 
        tags: selectedTags.value
      };
      const response = await store.getters.getApi.contact.createContact({
        "name": contactData.name,
        "jobTitle": contactData.jobTitle,
        "ownerId": store.getters.getUserId,
        "contactTags": contactData.tags,
        "phoneNumber": contact.value.phone,
        "email": contact.value.email,
    }, token);
  
      // Save note after contact creation
      await store.getters.getApi.note.createNote(response.data.id, note.value, token);
  
      // Notify user and redirect to the main page
      notify({
            title: 'Contact created!',
            speed: 5,
            position: 'bottom left',
            duration: 1000
        });
  
      router.push('/');
    } catch (error) {
      console.error('Error creating contact:', error);
    }
  };
  
  // Check if form is valid
  const isValidForm = computed(() => contact.value.name && contact.value.jobTitle && selectedTags.value.length > 0);
  </script>
  
  <style scoped>
  .add-contact-form {
    width: 100%;
    max-width: 500px;
    margin: 0 auto;
    margin-top: 60px;
    background-color: var(--color-background-soft);
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 5px;
  }
  
  .form-group input, .form-group select, .form-group textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 6px;
    background-color: #fff;
  }
  
  .form-group textarea {
    height: 100px;
  }
  
  button {
    padding: 10px 20px;
    margin-top: 10px;
    background-color: var(--vt-c-indigo);
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
  }
  
  button:hover {
    background-color: #0056b3;
  }
  
  .tags {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .tag {
    padding: 8px 12px;
    border-radius: 5px;
    background-color: #464646;
    cursor: pointer;
  }
  
  .tag.selected {
    background-color: green;
    color: #fff;
  }
  </style>
  