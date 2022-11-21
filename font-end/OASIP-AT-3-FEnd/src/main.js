import { createApp } from 'vue'
import router from './route'
import App from './App.vue'
// import './index.css'
import 'boxicons'

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle'

createApp(App).use(router).mount('#app')
