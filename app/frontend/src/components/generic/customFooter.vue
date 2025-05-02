<script setup>
import { getCurrentInstance } from 'vue'

const { appContext } = getCurrentInstance()
const appVersion = appContext.config.globalProperties.$arbitajaVersion
const copyrightHeader = appContext.config.globalProperties.$copyrightHeader

</script>

<template>
    <footer>
        <div class="text-body" id="footer-text">
            <p class="footer-center">
                {{ copyrightHeader }}
            </p>
            <p class="footer-version">
                {{ appVersion }}
            </p>
        </div>
    </footer>
</template>
