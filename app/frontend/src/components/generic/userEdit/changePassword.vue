<script setup lang="ts">
// Import required vue modules
import { ref } from 'vue';

// Import axios
import { apiClient, ChangePasswordRequest } from '@/services/api'

// Get props
const props = defineProps({
    // Get user ID to know which user to password reset
    userId: {
        type: Number,
        required: true
    },
    isAdmin: {
        type: Boolean,
        default: true
    }
})

// Import Alert
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')
const alertTrigger = ref(0)

import displayAlert from '@/components/generic/displayAlert.vue';

// Alert function
function showAlert(message: string, type: string, timeout: number = 3000) {
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
    alertTrigger.value++
}

// Set empty variable for password reset
const curPassword = ref<string>('')
const newPassword = ref<string>('')
const confirmPassword = ref<string>('')

// Function to reset password
const resetPassword = async(userId: number, oldPassword: string, setPassword: string, confirmSetPassword: string) => {
    try {
        let apiObject: ChangePasswordRequest = {} as ChangePasswordRequest
        // Create the object to be submited 
        if (props.isAdmin) {
            apiObject = {
                newPassword: setPassword
            }
        }
        if (!props.isAdmin) {
            apiObject = {
                newPassword: setPassword,
                oldPassword: oldPassword
            }
        }

        // Test if the password are the same
        if (setPassword === confirmSetPassword) {

            // try reseting password
            const response = await apiClient.users.changePassword(userId, apiObject)

            if (!response.success) {
                throw new Error(response.error.message || 'Unknown error')
            }

            // Show alert password changed
            showAlert('Password changed successfully!', 'success')
        } else {
            // If the password are not the same through an error
            showAlert('Passwords don\'t match!', 'danger', 2000)
        }
    } catch(error) {
        showAlert('Setting new password failed. Error: ' + error, 'danger', 9000)
    }
}

</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />

    <!-- Start main body -->
    <!-- Modal trigger button -->
    <button
        type="button"
        class="btn btn-outline-dark"
        data-bs-toggle="modal"
        data-bs-target="#passwordResetId"
    >
        Reset Password
    </button>
    
    <!-- Modal Body -->
    <!-- if you want to close by clicking outside the modal, delete the last endpoint:data-bs-backdrop and data-bs-keyboard -->
    <div
        class="modal fade"
        id="passwordResetId"
        tabindex="-1"
        
        role="dialog"
        aria-labelledby="modalTitleId"
    >
        <div
            class="modal-dialog modal-dialog-scrollable modal-dialog-centered modal-lg"
            role="document"
        >
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitleId">
                        Reset Password
                    </h5>
                    <button
                        type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Close"
                    ></button>
                </div>
                <div class="modal-body">
                    <div v-if="!isAdmin" class="row pt-3">
                        <div class="col-4">
                            Current password:
                        </div>
                        <div class="col">
                            <input
                                type="password"
                                class="form-control"
                                v-model="curPassword"
                                id="username"
                            />
                        </div>
                    </div>
                    <div class="row pt-3">
                        <div class="col-4">
                            New password:
                        </div>
                        <div class="col">
                            <input
                                type="password"
                                class="form-control"
                                v-model="newPassword"
                                id="username"
                            />
                        </div>
                    </div>
                    <div class="row pt-3">
                        <div class="col-4">
                            Re-type new password:
                        </div>
                        <div class="col">
                            <input
                                type="password"
                                class="form-control"
                                v-model="confirmPassword"
                                id="username"
                            />
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button @click.prevent="resetPassword(props.userId, curPassword, newPassword, confirmPassword)" type="button" class="btn btn-success">Save</button>
                    <button
                        type="button"
                        class="btn btn-outline-dark"
                        data-bs-dismiss="modal"
                    >
                        Close
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>