<script setup>
const props = defineProps({
    schools: {
        type: Object,
        required: true,
    },
})
const schools = props.schools 
</script>

<template>
    <div class="mt-2">
        <div v-for="school in schools" class="row border border-2 mt-2 justify-content-center text-center align-items-center">
            <div class="col-xl-2 d-xl-block d-lg-none mt-3">
                <p>{{ school.id }}:</p>
            </div>
            <div class="col-lg-12 col-xl-10 mt-3">
                <p>{{ school.name }}</p>
            </div>
        </div>
    </div>
</template>