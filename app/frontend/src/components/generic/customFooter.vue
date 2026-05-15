<script setup lang="ts">
import { APP_VERSION, COPYRIGHT_HEADER } from '@/config'
</script>

<template>
    <footer>
        <div class="text-body" id="footer-text">
            <p class="footer-center">
                {{ COPYRIGHT_HEADER }}
            </p>
            <p class="footer-version">
                {{ APP_VERSION }}
            </p>
        </div>
    </footer>
</template>
