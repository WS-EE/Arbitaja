import "./assets/css/main.css"

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap";

import { createApp } from 'vue'
import App from './App.vue'
import router from "./router";
import VueCookies from 'vue-cookies'

const app = createApp(App);
app.use(VueCookies, { expires: '7d'});
app.use(router);
app.mount('#app');
