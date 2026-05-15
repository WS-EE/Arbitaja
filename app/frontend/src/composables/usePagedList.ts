import { ref, watch } from 'vue';
import type { PagedResponse, PageParams, ApiResponse } from '@/services/api';

export function usePagedList<T>(
  fetchFn: (params: PageParams) => Promise<ApiResponse<PagedResponse<T>>>,
  defaultSort = ''
) {
  const items = ref<T[]>([]);
  const search = ref('');
  const page = ref(0);
  const pageSize = ref(20);
  const totalElements = ref(0);
  const totalPages = ref(0);
  const isLoading = ref(false);
  const error = ref('');

  const parts = defaultSort.split(',');
  const sortField = ref(parts[0] || '');
  const sortDir = ref<'asc' | 'desc'>((parts[1] as 'asc' | 'desc') || 'asc');

  let searchTimeout: ReturnType<typeof setTimeout> | null = null;

  const load = async () => {
    isLoading.value = true;
    error.value = '';
    try {
      const sort = sortField.value ? `${sortField.value},${sortDir.value}` : undefined;
      const params: PageParams = {
        search: search.value || undefined,
        page: page.value,
        size: pageSize.value,
        sort,
      };
      const response = await fetchFn(params);
      if (!response.success) {
        error.value = response.error?.message ?? 'Failed to load data';
        return;
      }
      items.value = response.data.content as T[];
      totalElements.value = response.data.totalElements;
      totalPages.value = response.data.totalPages;
    } catch (e) {
      error.value = String(e);
    } finally {
      isLoading.value = false;
    }
  };

  const onSearchInput = () => {
    if (searchTimeout) clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
      page.value = 0;
      load();
    }, 300);
  };

  const goToPage = (p: number) => {
    page.value = p;
    load();
  };

  const setSort = (field: string) => {
    if (sortField.value === field) {
      sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc';
    } else {
      sortField.value = field;
      sortDir.value = 'asc';
    }
    page.value = 0;
    load();
  };

  watch(pageSize, () => {
    page.value = 0;
    load();
  });

  return {
    items,
    search,
    page,
    pageSize,
    totalElements,
    totalPages,
    isLoading,
    error,
    sortField,
    sortDir,
    load,
    onSearchInput,
    goToPage,
    setSort,
  };
}
