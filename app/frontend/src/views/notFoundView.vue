<script setup>
import notFound from '@/assets/media/404.gif'
import logo from '@/assets/media/logo.svg';
</script>

<template>
    <div class="page_404">
        <div class="container">
            <div class="row">
                <div class="col">
                    <div class="text-center">
                        <div class="four_zero_four_bg">
                            <img alt="Credit: Andreas Gabriel Vannas" :src="logo" class="pt-2" width="125" height="125" />
                            <h1 class="text-center">404</h1>
                        </div>
                        <div class="contant_box_404">
                            <h3>
                                Page not Found.
                            </h3>
                            <p>Something went wrong.</p>
                            <div width="auto" class="row d-flex justify-content-center">
                                <img alt="Caveman by Markus Magnusson" :src="notFound" class="image-width"/>
                                <a href="https://www.instagram.com/motionmarkus/">Caveman by Markus Magnusson</a>
                            </div>
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