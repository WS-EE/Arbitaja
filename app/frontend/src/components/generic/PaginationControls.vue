<script setup lang="ts">
defineProps<{
  page: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
}>();

const emit = defineEmits<{
  (e: 'go-to-page', page: number): void;
  (e: 'update:pageSize', size: number): void;
}>();
</script>

<template>
  <div class="d-flex align-items-center justify-content-between flex-wrap gap-2 mt-3">
    <div class="text-muted small">
      {{ totalElements }} total
    </div>

    <nav v-if="totalPages > 1">
      <ul class="pagination pagination-sm mb-0">
        <li class="page-item" :class="{ disabled: page === 0 }">
          <button class="page-link" @click="emit('go-to-page', page - 1)" :disabled="page === 0">
            &laquo;
          </button>
        </li>

        <template v-for="p in totalPages" :key="p">
          <li
            v-if="Math.abs(p - 1 - page) <= 2 || p === 1 || p === totalPages"
            class="page-item"
            :class="{ active: p - 1 === page }"
          >
            <button class="page-link" @click="emit('go-to-page', p - 1)">{{ p }}</button>
          </li>
          <li v-else-if="Math.abs(p - 1 - page) === 3" class="page-item disabled">
            <span class="page-link">…</span>
          </li>
        </template>

        <li class="page-item" :class="{ disabled: page >= totalPages - 1 }">
          <button class="page-link" @click="emit('go-to-page', page + 1)" :disabled="page >= totalPages - 1">
            &raquo;
          </button>
        </li>
      </ul>
    </nav>

    <div class="d-flex align-items-center gap-1">
      <label class="text-muted small mb-0">Per page:</label>
      <select
        class="form-select form-select-sm"
        style="width: auto"
        :value="pageSize"
        @change="emit('update:pageSize', Number(($event.target as HTMLSelectElement).value))"
      >
        <option value="10">10</option>
        <option value="20">20</option>
        <option value="50">50</option>
        <option value="100">100</option>
      </select>
    </div>
  </div>
</template>
