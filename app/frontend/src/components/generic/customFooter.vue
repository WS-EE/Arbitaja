<script setup>
import { getCurrentInstance } from 'vue'

const { appContext } = getCurrentInstance()
const appVersion = appContext.config.globalProperties.$arbitajaVersion

</script>

<template>
    <footer>
        <div class="text-body" id="footer-text">
            <p class="footer-center">
                &copy; 2024 WorldSkills Estonia
            </p>
            <p class="footer-version">
                {{ appVersion }}
            </p>
        </div>
    </footer>
</template>
