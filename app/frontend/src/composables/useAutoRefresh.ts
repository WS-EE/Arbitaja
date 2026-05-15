import { watch, onUnmounted, Ref } from 'vue'

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
