import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Notifications from '@kyvg/vue3-notification'
import ElementPlus from 'element-plus';
import { tokenState } from './utils/tokenState';

const app = createApp(App)

app.use(router)
app.use(store)
app.use(Notifications)

// Set up the refresh callback
tokenState.setRefreshCallback(async () => {
  return await store.dispatch('refreshToken');
});

app.mount('#app')
