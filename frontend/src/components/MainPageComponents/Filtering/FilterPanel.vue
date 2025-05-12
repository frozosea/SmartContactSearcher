<template>
  <div class="filter-panel">
    <!-- Job Titles Filter -->
    <div class="filter-group">
      <h3>Job Titles</h3>
      <div class="filter-items">
        <div
          v-for="(title, index) in jobTitles"
          :key="index"
          :class="['filter-item', selectedJobTitles.includes(title) ? 'selected' : '']"
          @click="toggleJobTitleFilter(title)"
        >
          {{ title }}
        </div>
      </div>
    </div>

    <!-- Tags Filter -->
    <div class="filter-group">
      <h3>Tags</h3>
      <div class="filter-items">
        <div
          v-for="(tag, index) in tags"
          :key="index"
          :class="['filter-item', selectedTags.includes(tag.id) ? 'selected' : '']"
          @click="toggleTagFilter(tag)"
        >
          {{ tag.name }}
        </div>
      </div>
    </div>

    <!-- Apply Filters Button -->
    <div class="filter-actions">
      <button class="apply-button" @click="applyFilters">Apply Filters</button>
      <button class="reset-button" @click="resetFilters">Reset Filters</button>
    </div>
  </div>
</template>

<script setup>
import { ref,watch, onMounted, onBeforeMount } from 'vue';
import { useStore } from 'vuex';
import { defineProps,defineEmits } from 'vue';
  
  // Props: contacts data
const props = defineProps({
    contacts: Array
});
const emit = defineEmits(['applyFilters','resetFilters'])
const store = useStore();
const token = store.getters.authToken;

const jobTitles = ref([]);
const tags = ref([]);
const selectedJobTitles = ref([]);
const selectedTags = ref([]);

watch(
  () => props.contacts,
  (newContacts) => {
    if (newContacts && newContacts.length) {
      // Update jobTitles with unique job titles
      jobTitles.value = [...new Set(newContacts.map(contact => contact.jobTitle))];
      console.log("Job Titles updated:", jobTitles.value);
    }
  },
  { immediate: true } // Immediate execution for the first time to handle initial contacts
);

onMounted(async () => {
  try {
    const response = await store.getters.getApi.tag.getAllTags(token);
    tags.value = response.data;
  } catch (error) {
    console.error('Error fetching tags:', error);
  }
  jobTitles.value = [...new Set(props.contacts.map(contact => contact.jobTitle))];
  console.log(jobTitles)
});

const toggleJobTitleFilter = (title) => {
  if (selectedJobTitles.value.includes(title)) {
    selectedJobTitles.value = selectedJobTitles.value.filter(item => item !== title);
  } else {
    selectedJobTitles.value.unshift(title);
  }
};

const toggleTagFilter = (tag) => {
  if (selectedTags.value.includes(tag.id)) {
    selectedTags.value = selectedTags.value.filter(item => item !== tag.id);
  } else {
    selectedTags.value.unshift(tag.id);
  }
};

const applyFilters = () => {
  store.commit('setJobTitleFilters', selectedJobTitles.value);
  store.commit('setTagFilters', selectedTags.value);
  emit('applyFilters',{jobs: selectedJobTitles.value, tagIds: selectedTags.value});
};

const resetFilters = () => {
  selectedJobTitles.value = [];
  selectedTags.value = [];
  store.commit('setJobTitleFilters', []);
  store.commit('setTagFilters', []);
  emit('resetFilters')
};
</script>

<style scoped>
.filter-panel {
  padding: 15px;
  background-color: var(--color-background-soft);
  border-radius: 12px; /* Make the panel have rounded corners */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Apply shadow like the login box */
}

.filter-group {
  margin-bottom: 30px;
}

.filter-items {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-item {
  padding: 8px 12px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  background-color: #464646;
  transition: background-color 0.3s ease;
}

.filter-item:hover {
  background-color: green;
}

.filter-item.selected {
  background-color: green;
  color: #fff;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.apply-button,
.reset-button {
  padding: 8px 15px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
}

.apply-button {
  background-color: #28a745;
  color: white;
}

.apply-button:hover {
  background-color: #218838;
}

.reset-button {
  background-color: #dc3545;
  color: white;
}

.reset-button:hover {
  background-color: #c82333;
}
</style>
