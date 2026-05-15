<script setup lang="ts">
import { ref, watch } from 'vue'

const props = withDefaults(defineProps<{
  message?: string
  type?: string
  timeout?: number
  trigger?: number
}>(), {
  message: 'Error',
  type: 'primary',
  trigger: 0,
})

const visible = ref(false)
const alertMessage = ref('')
const alertClass = ref('')
let hideTimer: ReturnType<typeof setTimeout> | null = null

watch(
  () => [props.type, props.message, props.timeout, props.trigger] as const,
  ([type, message, timeout]) => {
    if (hideTimer) clearTimeout(hideTimer)
    alertClass.value = 'alert-' + (type ?? 'primary')
    alertMessage.value = message ?? ''
    visible.value = true

    const ms = timeout ?? 3000
    if (ms !== 0) {
      hideTimer = setTimeout(() => { visible.value = false }, ms)
    }
  }
)
</script>

<template>
  <div class="container text-center fixed-bottom" style="pointer-events: none">
    <div class="row justify-content-center align-items-center align-self-center">
      <Transition class="m-2 col-10 col-md-8 col-lg-6" name="alert">
        <div
          v-if="visible"
          class="alert alert-sizes align-self-center"
          :class="alertClass"
          role="alert"
          style="pointer-events: auto"
        >
          <span v-html="alertMessage"></span>
        </div>
      </Transition>
    </div>
  </div>
</template>
