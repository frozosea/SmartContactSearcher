<template>
    <CustomModal :show="showModalProp" @update:show="updateShow">
      <div class="modal-header">
        <h3>Contact Details</h3>
        <div class="modal-actions">
          <button class="edit-btn" @click="toggleEdit">{{ isEditing ? 'Save Changes' : 'Edit Contact' }}</button>
          <button class="delete-btn" @click="deleteContact">Delete Contact</button>
        </div>
      </div>
  
      <div class="contact-info">
        <div v-if="isEditing">+
          <input v-model="contactData.name" placeholder="Contact Name" />
          <input v-model="contactData.jobTitle" placeholder="Job Title" />
          <textarea v-model="noteContent" placeholder="Add a note"></textarea>
        </div>
        <div v-else>
          <h2>Name:</h2>
          <p style="border-bottom: 1px solid var(--color-border);"><strong>{{ contactData.name }}</strong></p>
          <h2>Job Title:</h2>
          <p style="border-bottom: 1px solid var(--color-border);">{{ contactData.jobTitle }}</p>
        </div>
        <h1>Notes:</h1>
        <div class="note-list">
          <div v-for="note in notes" :key="note.id" class="note-item">
            <p>{{ note.content }}</p>
            <button v-if="isEditing" class="delete-note-btn" @click="deleteNote(note.id)">Delete Note</button>
          </div>
        </div>
  
        <div v-if="isEditing">
          <button @click="addNote" class="add-note-btn">Add Note</button>
        </div>
      </div>
    </CustomModal>
  </template>
  
  <script setup>
  import { defineProps, ref, onMounted, watch } from 'vue';
  import { useStore } from 'vuex';
  import { useRouter } from 'vue-router';
  import { useNotification } from '@kyvg/vue3-notification';
  import CustomModal from '../../UI/CustomModal.vue';
  
  const props = defineProps({
    contact: {
      type: Object,
      required: true
    },
    showModalProp: {
      type: Boolean,
      required: true,
      default: false
    }
  });

  const emit = defineEmits(['update:showModalProp']);

  const updateShow = (value) => {
    emit('update:showModalProp', value);
  };

  const store = useStore();
  const router = useRouter();
  const { notify } = useNotification();
  
  const isEditing = ref(false);
  const contactData = ref({ ...props.contact });
  const noteContent = ref('');
  const notes = ref([]);
  
  // Reset contact data when contact prop changes
  watch(() => props.contact, (newContact) => {
    contactData.value = { ...newContact };
  }, { deep: true });

  // Reset editing state when modal closes
  watch(() => props.showModalProp, (newValue) => {
    if (!newValue) {
      isEditing.value = false;
    }
  });
  
  onMounted(async () => {
    await fetchNotesAndTags();
  });
  
  const fetchNotesAndTags = async () => {
    const token = store.getters.authToken;
    try {
      const contactNotes = await store.getters.getApi.note.getNoteByContactId(props.contact.owner, token);
      notes.value = contactNotes.data;
      console.log(contactNotes)
    } catch (error) {
      console.error('Error fetching notes:', error);
    }
  };
  
  const toggleEdit = () => {
    isEditing.value = !isEditing.value;
    if (!isEditing.value) {
      saveContact();
    }
  };
  
  const saveContact = async () => {
    const token = store.getters.authToken;
    try {
      const contactData = {
        id: contactData.value.id,
        name: contactData.value.name,
        jobTitle: contactData.value.jobTitle,
        tags: contactData.value.tags
      };
      await store.getters.getApi.contact.updateContact(contactData.value.id, contactData, token);
      notify({
        title: 'Contact Updated',
        type: 'success',
        speed: 200,
        position: 'bottom left',
        duration: 1000
      });
    } catch (error) {
      console.error('Error updating contact:', error);
    }
  };
  
  const addNote = async () => {
    const token = store.getters.authToken;
    const noteData = { content: noteContent.value, contactId: contactData.value.id };
    try {
      await store.getters.getApi.note.createNote(contactData.value.id, noteData, token);
      notes.value.push(noteData);
      noteContent.value = ''; // Clear note content after adding
    } catch (error) {
      console.error('Error adding note:', error);
    }
  };
  
  const deleteNote = async (noteId) => {
    const token = store.getters.authToken;
    try {
      await store.getters.getApi.note.deleteNote(noteId, token);
      notes.value = notes.value.filter(note => note.id !== noteId);
    } catch (error) {
      console.error('Error deleting note:', error);
    }
  };
  
  const deleteContact = async () => {
    const token = store.getters.authToken;
    try {
      await store.getters.getApi.contact.deleteContact(contactData.value.id, token);
      notify({
        title: 'Contact Deleted',
        type: 'success',
        speed: 200,
        position: 'bottom left',
        duration: 1000
      });
      router.push('/');
    } catch (error) {
      console.error('Error deleting contact:', error);
    }
  };
  </script>
  
  <style scoped>
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .modal-actions {
    display: flex;
    gap: 1rem;
  }
  
  .edit-btn,
  .delete-btn {
    padding: 8px 16px;
    border-radius: 6px;
    border: none;
    cursor: pointer;
  }
  
  .edit-btn {
    background-color: #28a745;
    color: white;
  }
  
  .delete-btn {
    background-color: #dc3545;
    color: white;
  }
  
  .contact-info {
    margin-top: 1rem;
  }
  
  .contact-info input,
  .contact-info textarea {
    width: 100%;
    padding: 8px;
    margin: 8px 0;
    border-radius: 6px;
    border: 1px solid var(--color-border);
  }
  
  .note-list .note-item {
    margin-bottom: 1rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
    border-bottom: 1px solid var(--color-border);
    margin-top: 8px;
  }
  
  .add-note-btn {
    padding: 8px 16px;
    background-color: #007bff;
    color: white;
    border-radius: 6px;
    border: none;
    cursor: pointer;
  }

  .delete-note-btn {
    padding: 3px 8px;
    background-color: #dc3545;
    color: white;
    border-radius: 6px;
    border: none;
    cursor: pointer;
  }
  </style>
  