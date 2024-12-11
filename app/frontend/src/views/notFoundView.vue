<script setup>
import notFound from '@/assets/media/404.gif'
import { RouterLink } from 'vue-router';
</script>

<template>
    <div class="page_404">
        <div class="container">
            <div class="row">
                <div class="col">
                    <div class="text-center">
                
                        <h1 class="text-center">404</h1>
                        <h3>
                            Page not Found.
                        </h3>
                        <p>Something went wrong.</p>
                        <RouterLink to="home" class="btn btn-dark">Back Home</RouterLink>
                        <a href="https://www.instagram.com/motionmarkus/" class="btn btn-outline-dark m-3 set-item-top" target="_blank" rel="noopener noreferrer">Caveman by Markus Magnusson</a>

                        <div class="allign-center">
                            <img alt="Caveman by Markus Magnusson" :src="notFound" class="set-item-bottom image-width"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.image-width {
    width: 800px;
}

@media (max-width: 800px) {
    .image-width {
        width: 100%;
    }
}
</style>