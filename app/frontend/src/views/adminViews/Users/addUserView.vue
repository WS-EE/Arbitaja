<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { apiClient, SignupRequest } from '@/services/api';
import displayAlert from '@/components/generic/displayAlert.vue';

const router = useRouter();

const form = ref<SignupRequest>({
    username: '',
    password: '',
    full_name: '',
    email: '',
    school_id: undefined,
});

const alertTimeout = ref(3000);
const alertMessage = ref('');
const alertType = ref('');

function showAlert(message: string, type: string, timeout = 3000) {
    alertMessage.value = message;
    alertType.value = type;
    alertTimeout.value = timeout;
}

const submit = async () => {
    try {
        const response = await apiClient.users.adminCreate(form.value);
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        showAlert('User "' + form.value.username + '" created successfully.', 'success');
        setTimeout(() => router.push('/admin/users/user'), 1000);
    } catch (e) {
        showAlert('Could not create user. Error: ' + e, 'danger', 9000);
    }
};
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
    <div class="container mt-3">
        <h3>Create User</h3>
        <form @submit.prevent="submit" class="mt-3">
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Username</label>
                <div class="col-sm-9">
                    <input v-model="form.username" type="text" class="form-control" required placeholder="username" />
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Password</label>
                <div class="col-sm-9">
                    <input v-model="form.password" type="password" class="form-control" required placeholder="password" />
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Full Name</label>
                <div class="col-sm-9">
                    <input v-model="form.full_name" type="text" class="form-control" placeholder="Full Name" />
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Email</label>
                <div class="col-sm-9">
                    <input v-model="form.email" type="email" class="form-control" placeholder="user@example.com" />
                </div>
            </div>
            <div class="d-flex justify-content-end gap-2">
                <button type="button" class="btn btn-outline-dark" @click="router.back()">Cancel</button>
                <button type="submit" class="btn btn-success">Create User</button>
            </div>
        </form>
    </div>
</template>
