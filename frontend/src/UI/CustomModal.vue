<template>
    <div v-if="show" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <img src="../assets/close.svg" alt="Close" class="close-icon" @click="closeModal" />
        <slot></slot>
      </div>
    </div>
  </template>
  
  <script setup>
  const props = defineProps({
    show: {
      type: Boolean,
      required: true,
      default: false
    }
  });
  
  const emit = defineEmits(['update:show']);
  
  const closeModal = () => {
    emit('update:show', false);
  };
  </script>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .modal-content {
    background-color: var(--color-background-soft);
    border-radius: 12px;
    padding: 20px;
    position: relative;
    width: 80%;
    max-width: 600px;
    box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .close-icon {
    position: absolute;
    top: 10px;
    right: 10px;
    width: 20px;
    cursor: pointer;
  }
  </style>
  