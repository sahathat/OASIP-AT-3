import { createApp } from 'vue'
import router from './route'
import App from './App.vue'
import './index.css'
import 'boxicons'

createApp(App).use(router).mount('#app')
