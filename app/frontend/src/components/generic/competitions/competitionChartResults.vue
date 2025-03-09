<script setup>
import Chart from 'primevue/chart';
import { ref, onMounted } from "vue";

onMounted(async() => {
    chartData.value = setChartData();
    chartOptions.value = setChartOptions();
});

const chartData = ref();
const chartOptions = ref();
        
const setChartData = () => {
    return {
        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
        datasets: [
            {
                label: 'Dataset 1',
                fill: false,
                yAxisID: 'y',
                tension: 0.4,
                data: [65, 59, 80, 81, 56, 55, 10]
            },
            {
                label: 'Dataset 2',
                fill: false,
                yAxisID: 'y',
                tension: 0.4,
                data: [28, 48, 40, 19, 86, 27, 90]
            },
            {
                label: 'Dataset 3',
                fill: false,
                yAxisID: 'y',
                tension: 0.4,
                data: [2, 50, 30, 29, 46, 7, 50]
            }
        ]
    };
};
const setChartOptions = () => {
    return {
        maintainAspectRatio: false,
        aspectRatio: 0.6,
        scales: {
            y: {
                display: true,
                position: 'left'
            }
        }
    };
}
</script>

<template>
    <div class="card">
        <Chart type="line" :data="chartData" :options="chartOptions" class="h-[30rem]" />
    </div>
</template>
