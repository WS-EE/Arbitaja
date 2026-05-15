<script setup lang="ts">
import { computed, onMounted, ref, toRef } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import {
    GridComponent,
    LegendComponent,
    TitleComponent,
    TooltipComponent,
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import PulseLoader from 'vue-spinner/src/PulseLoader.vue'
import displayAlert from '@/components/generic/displayAlert.vue'
import { apiClient, CompetitorDashboardResponse } from '@/services/api'
import { useAutoRefresh } from '@/composables/useAutoRefresh'

// Register only what's needed (tree-shakeable)
use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent])

// --- Props ---
const props = defineProps({
    competition_id: {
        type: Number,
        required: true,
    },
    refreshInterval: {
        type: Number,
        default: 30000,
    },
    autoRefresh: {
        type: Boolean,
        default: false,
    },
})

// --- Alert ---
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')
const alertTrigger = ref(0)

function showAlert(message: string, type: string, timeout: number = 3000) {
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
    alertTrigger.value++
}

// --- Data ---
const isLoadingResults = ref(true)
const results = ref<CompetitorDashboardResponse[]>([])

const CHART_PALETTE = [
    '#0d6efd',
    '#198754',
    '#fd7e14',
    '#dc3545',
    '#6f42c1',
    '#20c997',
    '#0dcaf0',
]

// --- ECharts option (reactive) ---
const chartOption = computed(() => {
    if (!results.value){
        return {}
    }
    const maxTimestamp = Math.max(
        ...results.value.flatMap(c =>
            (c.results ?? []).map(r => new Date(r.timestamp).getTime())
        )
    )
      return {
        animation: false,
        grid: {
          left: '3%',
          right: '4%',
          bottom: '12%',
          containLabel: true,
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {type: 'cross'},
          formatter(params: any) {
            const time = new Date(params[0].value[0]).toLocaleTimeString()
            const lines = params.map(
                (p: any) => `<span style="color:${p.color}">●</span> ${p.seriesName}: <b>${p.value[1]}</b>`
            )
            return `${time}<br/>${lines.join('<br/>')}`
          },
        },
        legend: {
          bottom: 0,
          type: 'scroll',
        },
        xAxis: {
          type: 'time',
          name: 'Timestamp',
          nameLocation: 'middle',
          nameGap: 30,
        },
        yAxis: {
          type: 'value',
          name: 'Points',
          nameLocation: 'middle',
          nameGap: 40,
          minInterval: 1,
        },
        series: results.value.map((competitor, index) => {
          const sorted = [...(competitor.results ?? [])]
              .sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime())

          const data = sorted.map(r => [r.timestamp, r.point_amount])

          // Extend to the global max timestamp with the last known value
          const last = sorted.at(-1)
          if (last && new Date(last.timestamp).getTime() < maxTimestamp) {
            data.push([maxTimestamp, last.point_amount])
          }
          return {
            name: competitor.name,
            type: 'line',
            step: 'end',
            symbol: 'circle',
            symbolSize: 4,
            color: CHART_PALETTE[index % CHART_PALETTE.length],
            data,
          }
        })
      }
})

// --- Fetch ---
async function fetchResults() {
    try {
        isLoadingResults.value = true
        const response = await apiClient.scoring.dashboard.history(props.competition_id)
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error')
        }
        results.value = response.data.competitors ?? []
    } catch (error) {
        showAlert(`Couldn't load chart data. Error: ${error}`, 'warning')
    } finally {
        isLoadingResults.value = false
    }
}

// --- Auto-refresh ---
useAutoRefresh(fetchResults, toRef(props, 'refreshInterval'), toRef(props, 'autoRefresh'))

// --- Lifecycle ---
onMounted(async () => {
    await fetchResults()
})
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />

    <div class="position-relative" style="min-height: 420px;">
        <!-- Loading spinner -->
        <div v-if="isLoadingResults" class="position-absolute top-50 start-50">
            <PulseLoader />
        </div>

        <!-- Chart -->
        <VChart
            v-if="!isLoadingResults"
            class="w-100 h-100"
            style="min-height: 420px;"
            :option="chartOption"
            autoresize
        />
    </div>
</template>
