<template>
  <canvas ref="chartCanvas"></canvas>
</template>

<script>
import { defineComponent, onMounted, ref, watch } from 'vue';
import { Chart, registerables } from 'chart.js';
import 'chartjs-adapter-date-fns';
Chart.register(...registerables);

export default defineComponent({
  name: 'LineChart',
  props: {
    results: {
      type: Array,
      required: true
    }
  },
  setup(props) {
    const chartCanvas = ref(null);

    const createChart = () => {
      if (!chartCanvas.value) return;
      const ctx = chartCanvas.value.getContext('2d');

      const datasets = props.results.map((competitor) => {
        const data = competitor.results
            .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
            .map((result) => {
              return { x: new Date(result.timestamp), y: result.point_amount };
            });

        return {
          label: competitor.name,
          data,
          borderColor: getRandomColor(),
          fill: false,
          tension: 0
        };
      });

      new Chart(ctx, {
        type: 'line',
        data: {
          datasets: datasets
        },
        options: {
          responsive: true,
          scales: {
            x: {
              type: 'time',
              time: {
                unit: 'minute'
              },
              title: {
                display: true,
                text: 'Timestamp'
              }
            },
            y: {
              title: {
                display: true,
                text: 'Points'
              }
            }
          }
        }
      });
    };

    onMounted(createChart);
    watch(() => props.results, createChart);

    function getRandomColor() {
      return `hsl(${Math.random() * 360}, 100%, 50%)`;
    }

    return { chartCanvas };
  }
});
</script>