<template>
  <div class="main-page">
    <!-- Check if user is logged in -->
    <div v-if="isLogin">
      <!-- Left Section: Filter and Search -->
      <div class="left-section">
        <!-- Search Bar -->
        <SearchBar :contacts="contacts" @applySearch="applySearch"/>

        <!-- Filter Panel -->
        <FilterPanel 
          :contacts="contacts" 
          @applyFilters="applyFilters" 
          @resetFilters="resetFilters" 
        />

        <!-- Add Contact Button (optional on left side) -->
      </div>

      <!-- Right Section: Contacts Header and List -->
      <div class="right-section">

        <!-- Contacts Header Section -->
        <ContactHeaderSection :totalContacts="filteredContacts.length" />
        <AddContactButton/>

        <!-- Contact List Section -->
        <ContactListSection :contacts="filteredContacts" @showContactDataModal="showContactDataModal"/>
        <!-- @showContactDataModal="showContactDataModal"-->
      </div>

    </div>

    <!-- If the user is not logged in -->
    <div v-else class="no-access">
      <p>
        You cannot give access to the main functionality of the application. 
        Please sign in first.
      </p>
    </div>
  </div>

  <!-- Contact Data Modal -->
   <div v-if="isShowContactDataModal">
    <ContactData :contact="selectedContact" :showModalProp="isShowContactDataModal" />
   </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { useStore } from 'vuex';
import SearchBar from '../components/MainPageComponents/Filtering/SearchBar.vue';
import FilterPanel from '../components/MainPageComponents/Filtering/FilterPanel.vue';
import AddContactButton from '../components/MainPageComponents/AddContactButton.vue';
import ContactHeaderSection from '../components/MainPageComponents/ContactHeaderSection.vue';
import ContactListSection from '../components/MainPageComponents/ContactListSection.vue';
import ContactData from '@/components/MainPageComponents/ContactData.vue';

const store = useStore();
const isLogin = store.getters.isLogin; 
const contacts = ref([]);
const filteredContacts = ref([]);
const selectedContact = ref({});
const isShowContactDataModal = ref(false);
// Fetch contacts if the user is logged in
onMounted(async () => {
  if (isLogin) {
    try {
      const userId = store.state.userId;
      const token = store.state.authToken;
      const response = await store.getters.getApi.contact.getContactsByUser(userId, token);
      contacts.value = response.data;
      store.commit('setContactsData', contacts.value);
      filteredContacts.value = contacts.value; // Initially, all contacts
    } catch (error) {
      console.error("Error fetching contacts:", error);
    }
  }
});

const applyFilters = ({ jobs, tagIds }) => {
  filteredContacts.value = contacts.value.filter(contact => {
    const jobMatch = jobs.length === 0 || jobs.includes(contact.jobTitle);
    const tagMatch = tagIds.length === 0 
      || (contact.tags && contact.tags.some(tagId => tagIds.includes(tagId)));
    return jobMatch && tagMatch;
  });
};

const applySearch = (searchedContacts) => {
  console.log(searchedContacts.value)
  filteredContacts.value = searchedContacts.value
}

const resetFilters = () => {
  filteredContacts.value = contacts.value; 
};

const showContactDataModal = (data) =>{
  selectedContact.value = data;
  console.log(selectedContact.value)
  store.commit('setContactModalVisibility', true);
  isShowContactDataModal.value = true;
}

watch(contacts, () => {
  applyFilters({ jobs: [], tagIds: [] });
});
</script>


