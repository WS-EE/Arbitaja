<script setup>
// Import required vue modules
import { ref } from 'vue';

// Import axios
import axios from 'axios';

// Get props
const props = defineProps({
    // Get user ID to know which user to password reset
    userId: {
        type: Number,
        requried: true
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

import displayAlert from '@/components/generic/displayAlert.vue';

// Alert function
function showAlert(message, type, timeout){
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

// Set empty variable for password reset
const curPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

// Function to reset password
const resetPassword = async(userId, oldPassword, setPassword, confirmSetPassword) => {
    try {
        let apiObject = {}
        // Create the object to be submited 
        if (props.isAdmin) {
            apiObject = {
                id: userId,
                new_password: setPassword
            }
        }
        if (!props.isAdmin) {
            apiObject = {
                id: userId,
                new_password: setPassword,
                old_password: oldPassword
            }
        }

        // Test if the password are the same
        if (setPassword === confirmSetPassword) {

            // try reseting password
            await axios.put('user/profile/update_password', apiObject)

            // Show alert password changed
            showAlert('Password changed successfully!', 'success')
        } else {
            // If the password are not the same through an error
            showAlert('Passwords don\'t match!', 'danger', 2000)
        }
    } catch(error) {
        showAlert('Setting new password failed. Error: ' + error.response.data.message, 'danger', 9000)
    }
}

</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />

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