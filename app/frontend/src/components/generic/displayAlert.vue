<script setup>
const props = defineProps({
    message: {
        type: String,
        default: "Error"
    },
    type: {
      type: String,
      default: "primary",
    },
    timeout: {
      type: Number,
    },
})
import { ref, watch } from 'vue';

// Alert function
const showAlert = ref(false)
const alertMessage = ref('')
const alertType = ref('')

watch(() => ({ type: props.type, message: props.message, timeout: props.timeout }),
  (val) => {
    // display alert
    displayAlert(val.message, val.type, val.timeout)
  }
);


function displayAlert(message, type, timeout){
    // Set default type to primary(info)
    if (timeout === undefined){
        timeout = 3000;
    }
    if (type === undefined){
        type = 'primary';
    }

    // Set the alert type
    alertType.value = 'alert-' + type

    // Tell user that changes were discarded
    showAlert.value = true

    alertMessage.value = message
    // Fade out alert after 3000ms
    if (timeout !== 0) {
        setTimeout(() => {
            showAlert.value = false
        }, timeout);
    }
}
</script>

<template>
    <!-- Alert when needed -->
  <div class="container text-center fixed-bottom">
    <div class="row justify-content-center align-items-center align-self-center">
      <Transition class="m-2 col-10 col-md-8 col-lg-6" name="alert">
        <div
            v-if="showAlert"
            class="alert alert-sizes align-self-center"
            :class="alertType"
            role="alert"
        >
          <span v-html="alertMessage"></span>
        </div>
      </Transition>
    </div>
  </div>
</template>