<script setup>
import { RouterLink } from 'vue-router';
</script>

<template>
    <div class="d-flex justify-content-center horizontal-center">
        <div class="text-center background-image" >
            <h1 class="text-center">404</h1>
            <h3>
                Page not Found.
            </h3>
            <p>Something went wrong.</p>
            <RouterLink to="/home" class="btn btn-outline-dark me-3">Back Home</RouterLink>
            <a @click="$router.back()" class="btn btn-dark me-3">Go Back</a>
            <div class="allign-center p-3">
                <a href="https://www.instagram.com/motionmarkus/" class="btn btn-outline-dark image-link" target="_blank" rel="noopener noreferrer">Caveman by Markus Magnusson</a>
            </div>
        </div>
    </div>
</template>

<style scoped>

.background-image {
    background-image: url('@/assets/media/404.gif');
    background-repeat: no-repeat;
    background-position-x: center;
    background-position: center 8rem;
    background: cover;
    background-size: cover;
    height: 600px;
    width: 800px;
    align-items: center;
}

.image-link {
    position: relative;
    margin-top: 24rem;    
}

.horizontal-center {
    padding-top: 12vh;   
}

@media (max-height: 760px) {
    .background-image {
        background-position: center 2rem;
    }

    .image-link {
        position: relative;
        margin-top: 18rem;    
    }

    .horizontal-center {
        padding-top: 5vh;
    }
}
</style>