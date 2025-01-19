<script setup>
import AddSchool from '@/components/admin/users/school/addSchool.vue';
import allSchools from '@/components/admin/users/school/allSchools.vue';
</script>

<template>
    <div class="container">
        <allSchools />
        <AddSchool modalId="addSchool" addButtonDivClass="pb-2 pt-2 sticky-bottom"/>
    </div>
</template>