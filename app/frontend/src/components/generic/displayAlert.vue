<script setup lang="ts">
import { ref, watch } from 'vue'

const props = withDefaults(defineProps<{
  message?: string
  type?: string
  timeout?: number
}>(), {
  message: 'Error',
  type: 'primary',
})

const visible = ref(false)
const alertMessage = ref('')
const alertClass = ref('')

watch(
  () => [props.type, props.message, props.timeout] as const,
  ([type, message, timeout]) => {
    alertClass.value = 'alert-' + (type ?? 'primary')
    alertMessage.value = message ?? ''
    visible.value = true

    const ms = timeout ?? 3000
    if (ms !== 0) {
      setTimeout(() => { visible.value = false }, ms)
    }
  }
)
</script>

<template>
  <div class="container text-center fixed-bottom">
    <div class="row justify-content-center align-items-center align-self-center">
      <Transition class="m-2 col-10 col-md-8 col-lg-6" name="alert">
        <div
          v-if="visible"
          class="alert alert-sizes align-self-center"
          :class="alertClass"
          role="alert"
        >
          <span v-html="alertMessage"></span>
        </div>
      </Transition>
    </div>
  </div>
</template>
