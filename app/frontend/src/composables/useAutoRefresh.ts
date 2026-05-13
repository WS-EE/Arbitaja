import { watch, onUnmounted, Ref } from 'vue'

/**
 * Composable for auto-refresh functionality.
 * @param {Function} callback - The function to call on each refresh tick
 * @param {Ref<number>} interval - Reactive ref for the interval duration (ms)
 * @param {Ref<boolean>} enabled - Reactive ref controlling whether auto-refresh is active
 */
export function useAutoRefresh(callback: () => void, interval: Ref<number>, enabled: Ref<boolean>) {
    let handle: number | null = null

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
