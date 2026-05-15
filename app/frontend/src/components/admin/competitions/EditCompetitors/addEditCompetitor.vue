<script setup lang="ts">
import {computed, onMounted, ref} from 'vue';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import {apiClient, CompetitorResponse, SchoolResponse, CompetitorUpsertRequest} from '@/services/api';
import displayAlert from '@/components/generic/displayAlert.vue';

const props = defineProps({
    modalId: {
        type: String,
        default: "modelId"
    },
    buttonName: {
        type: String,
        default: 'Button',
    },
    addButtonDivClass: {
        type: String,
        default: '',
    },
    apiEndpoint: {
        type: String,
    },
    isLinked: {
        type: Boolean,
        default: false,
    },
    isEdit: {
        type: Boolean,
        default: false
    },
    existingCompetitors: {
        type: Array as () => Array<CompetitorResponse>,
        default: () => []
    },
    competitor: {
        type: Object as () => CompetitorResponse,
        default: () => ({})
    },
    modalHeader: {
        type: String,
        default: 'Edit Competitor'
    }
})

// personal data mappings
const competitorData = ref<CompetitorUpsertRequest>({
    alias: '',
    public_display_name_type: 1,
    personal_data_id: undefined,
    full_name: '',
    email: '',
    school_id: undefined,
})
const isLoadingMain = ref<boolean>(true)
const isLoadingSchool = ref<boolean>(true)
const isLoadingUsers = ref<boolean>(true)
const avaliableDisplayTypes = [
    { id: 1, name: "Full Name" },
    { id: 2, name: "School" },
    { id: 3, name: "Alias" }
]
const allSchools = ref<SchoolResponse[]>([])
const allUsers = ref<CompetitorResponse[]>([])

// Track selected school/user names for display in dropdowns
const selectedSchoolName = ref<string>('')
const userName = ref<string>('')

const emit = defineEmits(['addItem', 'editCompetitor'])

// Change public displayname type
const changeType = (id: number) => {
    competitorData.value.public_display_name_type = id
}

// Edit a competitor
const editCompetitor = async(id: number, editedCompetitor: CompetitorUpsertRequest) => {
    try {
        const response = await apiClient.competitors.update(id, editedCompetitor)
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }

        showAlert('Edit competitor <strong>' + competitorData.value.alias + '</strong> was a success.', 'success')
        emit('editCompetitor')
    } catch (error) {
        showAlert("An error. Couldn't update competitor. Error" + error, 'danger')
    }
}

// Create either linked competitor or a "dummy" competitor
const createAndAddCompetitor = async(competitor: CompetitorUpsertRequest) => {
    try {
        const response = await apiClient.competitors.create(competitor)

        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }

        showAlert(props.buttonName + ' <strong>' + competitorData.value.alias + '</strong> was a success.', 'success')
        emit('addItem')
    } catch(e) {
        showAlert('Couldn\'t ' + props.buttonName + '. <br> Error: ' + e + '<br>' + e, 'danger', 9000)
    }
}

// Get display type
const getDisplayTypeNameById = (id: number) => {
  const displayType = avaliableDisplayTypes.find(p => p.id == id)
  return displayType ? displayType.name : 'Not Set'
}

// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

function showAlert(message: string, type: string, timeout: number = 3000) {
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

// Get schools for competition add
const getSchools = async() => {
    try {
        isLoadingSchool.value = true
        const response = await apiClient.schools.list({ size: 500 })
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }
        allSchools.value = response.data.content
    } catch(error) {
        showAlert('Couldn\'t get data for all the schools. Error:' + error, 'danger', 9000)
    } finally {
        isLoadingSchool.value = false
    }
}

// Get all the users
const getAllUsers = async() => {
    try {
        isLoadingUsers.value = true
        const response = await apiClient.users.list({ size: 500 })
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }
        allUsers.value = response.data.content
    } catch(error) {
        showAlert('Couldn\'t get data for all the users. Error:' + error, 'danger', 9000)
    } finally {
        isLoadingUsers.value = false
    }
}

// Do change the linked user displayed on the dropdown of the modal
const changeLinkedUser = (id: number, name?: string) => {
    if (id == null) {
        return
    }
    competitorData.value.personal_data_id = id
    userName.value = name ?? ''
}

// Set the new school
const changeSchool = (id: number, name?: string) => {
    competitorData.value.school_id = id
    selectedSchoolName.value = name ?? ''
}

// On mount get certain things
onMounted(async() => {
    try {
        await getSchools();
        await getAllUsers();

        // Get competitor data for competitor edit
        if(props.isEdit) {
            competitorData.value.public_display_name_type = props.competitor.public_display_name_type ?? 1
            competitorData.value.alias = props.competitor.alias
        }

    } catch (error) {
        showAlert("Couldn't get competitor data. Error:" + error, "danger")
    } finally {
        isLoadingMain.value = false
    }
})


// Search bar function
// filter schools based on name
const searchSchools = ref('');
const filteredSchools = computed(() => {
  const query = searchSchools.value.toLowerCase()
  return allSchools.value.filter(school =>
    school.name?.toLowerCase().includes(query)
  )
})

// filter out users that are already added
const unLinkedUsers = computed(() => {
    const existingUserIds = new Set(
        props.existingCompetitors
            .map(c => c.personal_data?.id)
            .filter((id): id is number => id != null)
    )
    return allUsers.value.filter(user => {
        const competitorDataId = user?.personal_data?.id
        return competitorDataId != null && !existingUserIds.has(competitorDataId)
    })
})

// filter users based on name
const searchUsers = ref('');
const filteredUsers = computed(() => {
  const query = searchUsers.value.toLowerCase()
  return unLinkedUsers.value.filter(user =>
    (user?.personal_data?.full_name ?? '').toLowerCase().includes(query)
  )
})


</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
    <!-- Button trigger modal -->
    <button
        type="button"
        :class="addButtonDivClass"
        class="btn btn-success"
        data-bs-toggle="modal"
        :data-bs-target="'#' + modalId"
        
    >
        {{ props.buttonName }}
    </button>
    <!-- Modal -->
    <div 
        class="modal fade"
        :id="modalId"
        tabindex="-1"
        role="dialog"
        aria-labelledby="modalTitleId"
        aria-hidden="true"
    >
        <div class="modal-dialog" role="document">
            <div v-if="isLoadingSchool && isLoadingUsers && isLoadingMain" class="position-absolute top-50 start-50">
                <PulseLoader />
            </div>
            <div v-else class="modal-content">
                <div class="modal-header">
                    <h5 v-if="isEdit" class="modal-title" id="modalTitleId">
                        {{ props.modalHeader }}
                    </h5>
                    <h5 v-else class="modal-title" id="modalTitleId">
                        {{ props.buttonName }}
                    </h5>
                    <button
                        type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Close"
                    ></button>
                </div>
                <div class="modal-body">
                    <!-- Normal block -->
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            Display Type:
                        </div>
                        <div class="col">
                        <!-- Default dropright button -->
                        <div class="btn-group">
                            <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                {{ getDisplayTypeNameById(competitorData.public_display_name_type) }}
                            </button>
                            
                            <ul class="dropdown-menu">
                                <li>
                                    
                                </li>
                                <!-- Dropdown menu links -->
                                <li 
                                    v-for="type in avaliableDisplayTypes" 
                                    @click="changeType(type.id)"
                                    class="dropdown-item"
                                >
                                    {{ type.name }}
                                </li>
                            </ul>
                        </div>
                    </div>
                    </div>
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            Alias:
                        </div>
                        <div class="col">
                            <input type="text" class="rounded p-1 form-control" v-model="competitorData.alias">
                        </div>
                    </div>

                    <!-- Start linked user block -->
                    <div v-if="isLinked && !isEdit">
                        <div class="row mt-2 mb-2">
                            <div class="col-4">
                                Linked User:
                            </div>
                            <div class="col">
                                <!-- Default dropright button -->
                                <div class="btn-group">
                                    <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                        {{ userName || 'Select User' }}
                                    </button>
                                    <ul class="dropdown-menu">
                                        <li class="px-2 py-1">
                                            <div class="input-group rounded">
                                                <input type="search" class="form-control rounded me-1 ms-1"
                                                    placeholder="Search" aria-label="Search" aria-describedby="search-addon"
                                                    v-model="searchUsers"
                                                />
                                            </div>
                                        </li>
                                        <!-- Dropdown menu links -->
                                        <li 
                                            v-for="user in filteredUsers" 
                                            @click="changeLinkedUser(user?.personal_data?.id!, user?.personal_data?.full_name)"
                                            class="dropdown-item"
                                        >
                                            {{ user?.personal_data?.full_name }}
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- Start unlinked user block -->
                    <div v-if="!isLinked && !isEdit">
                        <div class="row mt-2 mb-2">
                            <div class="col-4">
                                Full Name:
                            </div>
                            <div class="col">
                                <input type="text" class="rounded p-1 form-control" v-model="competitorData.full_name">
                            </div>
                        </div>
                        <div class="row mt-2 mb-2">
                            <div class="col-4">
                                E-Mail:
                            </div>
                            <div class="col">
                                <input type="text" class="rounded p-1 form-control" v-model="competitorData.email">
                            </div>
                        </div>
                        <!-- School logic -->
                        <div class="row mt-2 mb-2">
                            <div class="col-4">
                                School:
                            </div>
                            <div class="col">
                                <!-- Default dropright button -->
                                <div class="btn-group">
                                    <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                        {{ selectedSchoolName || 'Select School' }}
                                    </button>
                                    <ul class="dropdown-menu">
                                        <li class="px-2 py-1">
                                            <div class="input-group rounded">
                                                <input type="search" class="form-control rounded me-1 ms-1"
                                                    placeholder="Search" aria-label="Search" aria-describedby="search-addon"
                                                    v-model="searchSchools"
                                                />
                                            </div>
                                        </li>
                                        <!-- Dropdown menu links -->
                                        <li 
                                            v-for="school in filteredSchools" 
                                            @click="changeSchool(school.id, school.name)" 
                                            class="dropdown-item"
                                        >
                                            {{ school.name }}
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button
                        @click.prevent="createAndAddCompetitor(competitorData)"
                        type="button" 
                        class="btn btn-success" 
                        data-bs-dismiss="modal"
                        v-if="!isEdit"
                    >
                        Add
                    </button>
                    <button 
                        @click.prevent="editCompetitor(competitor.id, competitorData)"
                        type="button" 
                        class="btn btn-success" 
                        data-bs-dismiss="modal"
                        v-if="isEdit"
                    >
                        Edit
                    </button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

</template>