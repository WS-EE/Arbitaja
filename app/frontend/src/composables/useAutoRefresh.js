import { watch, onUnmounted } from 'vue'

/**
 * Composable for auto-refresh functionality.
 * @param {Function} callback - The function to call on each refresh tick
 * @param {import('vue').Ref<number>} interval - Reactive ref for the interval duration (ms)
 * @param {import('vue').Ref<boolean>} enabled - Reactive ref controlling whether auto-refresh is active
 */
export function useAutoRefresh(callback, interval, enabled) {
    let handle = null

    function start() {
        stop()
        handle = setInterval(callback, interval.value)
    }

    function stop() {
        if (handle !== null) {
            clearInterval(handle)
            handle = null
        }
    }

    watch(enabled, (active) => {
        active ? start() : stop()
    })

    watch(interval, () => {
        if (enabled.value) start()
    })

    onUnmounted(stop)

    return { start, stop }
}
