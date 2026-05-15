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
  <div class="pagination-controls border-top mt-4 pt-3 pb-4 d-flex align-items-center justify-content-between flex-wrap gap-2">
    <div class="text-muted small">
      <span class="fw-semibold text-body">{{ totalElements }}</span> total results
    </div>

    <nav v-if="totalPages > 1" aria-label="Page navigation">
      <ul class="pagination pagination-sm mb-0">
        <li class="page-item" :class="{ disabled: page === 0 }">
          <button class="page-link" @click="emit('go-to-page', page - 1)" :disabled="page === 0" aria-label="Previous">
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
          <button class="page-link" @click="emit('go-to-page', page + 1)" :disabled="page >= totalPages - 1" aria-label="Next">
            &raquo;
          </button>
        </li>
      </ul>
    </nav>
    <div v-else class="text-muted small">Page 1 of 1</div>

    <div class="d-flex align-items-center gap-2">
      <label class="text-muted small mb-0 text-nowrap">Per page:</label>
      <select
        class="form-select form-select-sm"
        style="width: 75px"
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
