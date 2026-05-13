<script setup>
import {computed, onMounted, ref} from 'vue';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import {apiClient} from '@/services/api';
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
        type: Array,
        default: () => []
    },
    competitor: {
        type: Object,
        default: () => ({})
    },
    modalHeader: {
        type: String,
        default: 'Edit Competitor'
    }
})

// personal data mappings
const personalData = ref({
    "full_name": "",
    "email": "",
    "school": {
        "id": "",
        "name": "Chose a school"
    }
})
const isLoadingMain = ref(true)
const isLoadingSchool = ref(true)
const isLoadingUsers = ref(true)
const publicDisplayType = ref()
const publicAlias = ref('')
const avaliableDisplayTypes = [
    { id: "1", name: "Full Name" },
    { id: "2", name: "School" },
    { id: "3", name: "Alias" }
]
const allSchools = ref([])
const allUsers = ref([])
const userPersonalDataId = ref()
const userName = ref()

const emit = defineEmits(['addItem', 'editCompetitor'])

// Change public displayname type
const changeType = (id) => {
    publicDisplayType.value = id
}

const buildCompetitorPayload = (personalData, linkedPersonalDataId = null, includeId = false, id = null) => {
    const payload = {
        alias: publicAlias.value,
        public_display_name_type: publicDisplayType.value ? Number(publicDisplayType.value) : null,
        personal_data_id: linkedPersonalDataId ? Number(linkedPersonalDataId) : null,
        full_name: personalData?.full_name || null,
        email: personalData?.email || null,
        school_id: personalData?.school?.id ? Number(personalData.school.id) : null,
    }

    if (!includeId) {
        return payload
    }

    return {
        id,
        ...payload,
    }
}

// Edit a competitor
const editCompetitor = async(id, displayTypeId, alias, personalDataId) => {
    try {
        
        // Create edit competitor data object
        const editedCompetitor = {
            id,
            alias,
            public_display_name_type: displayTypeId ? Number(displayTypeId) : null,
            personal_data_id: personalDataId ? Number(personalDataId) : null,
        }

        // Edit data of competitor
         await apiClient.competitors.update(id, editedCompetitor)

        // Show success on edit
        showAlert('Edit competitor <strong>' + alias + '</strong> was a success.', 'success')

        // Emit competitor edited event
        emit('editCompetitor')
    } catch (error) {
        showAlert("An error. Couldn't update competitor. Error" + error, 'danger')
    }
}

// Create either linked competitor or a "dummy" competitor
const createCompetitor = async(newPersonalData, linkedPersonalDataId) => {
    try {
        // Make the api call
        if (props.isLinked) {
            await apiClient.competitors.create(buildCompetitorPayload(null, linkedPersonalDataId))
        } else {
            await apiClient.competitors.create(buildCompetitorPayload(newPersonalData))
        }
        
        // show alert of success
        showAlert(props.buttonName + ' <strong>' + newPersonalData.full_name + '</strong> was a success.', 'success')
        
        // emit event to parent
        emit('addItem')
        // empty out school on success 
        personalData.value = {
            "full_name": "",
            "email": "",
            "school": {
                "id": "",
                "name": "Chose a school"
            }
        }
    } catch(e) {
        showAlert('Couldn\'t ' + props.buttonName + '. <br> Error: ' + e + '<br>' + e.response.data.error, 'danger', 9000)
    }
}

// Get display type
const getDisplayTypeNameById = (id) => {
  const displayType = avaliableDisplayTypes.find(p => p.id === id)
  return displayType ? displayType.name : 'Not Set'
}

// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

function showAlert(message, type, timeout){
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

// Get schools for competition add
const getSchools = async() => {
    try {
        isLoadingSchool.value = true
        allSchools.value = await apiClient.schools.list()
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
        allUsers.value = await apiClient.users.list()
    } catch(error) {
        showAlert('Couldn\'t get data for all the schools. Error:' + error, 'danger', 9000)
    } finally {
        isLoadingUsers.value = false
    }
}

// Do change the linked user displayed on the dropdown of the modal
const changeLinkedUser = (id, name, newPersonalData) => {
    if (id == null || !newPersonalData) {
        return
    }
    userPersonalDataId.value = id
    userName.value = name
    personalData.value = { ...newPersonalData }
}


// Set the new school
const changeSchool = async(id, name) => {
    personalData.value.school.id = id
    personalData.value.school.name = name
}

// On mount get certain things
onMounted(async() => {
    try {

        // Get schools and users for competitor add
        await getSchools();
        await getAllUsers();

        // Get competitor data for competitor edit
        if(props.isEdit) {
            // Add values to existing competitors
            publicDisplayType.value = ''+props.competitor.public_display_name_type // this needs to be string otherwise it breaks.
            publicAlias.value = props.competitor.alias
        }

    // Catch error
    } catch (error) {
        showAlert("Couldn't get competitor data. Error:" + error, "danger")
    } finally {
        // Load main body of the modal
        isLoadingMain.value = false
    }

})


// Search bar function
// filter schools based on name
const searchSchools = ref('');
const filteredSchools = computed(() => {
  const query = searchSchools.value.toLowerCase()
  return allSchools.value.filter(school =>
    school.name.toLowerCase().includes(query)
  )
})

// filter out users that are already added
const unLinkedUsers = computed(() => {
    const existingUserIds = new Set(
        props.existingCompetitors
            .map(user => user?.personal_data?.id)
            .filter(id => id != null)
    )
    return allUsers.value.filter(user => {
        const personalDataId = user?.personal_data?.id
        return personalDataId != null && !existingUserIds.has(personalDataId)
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
                                {{ getDisplayTypeNameById(publicDisplayType) }}
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
                            <input type="text" class="rounded p-1 form-control" v-model="publicAlias">
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
                                        {{ userName }}
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
                                            @click="changeLinkedUser(user?.personal_data?.id, user?.personal_data?.full_name, user?.personal_data)"
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
                                <input type="text" class="rounded p-1 form-control" v-model="personalData.full_name">
                            </div>
                        </div>
                        <div class="row mt-2 mb-2">
                            <div class="col-4">
                                E-Mail:
                            </div>
                            <div class="col">
                                <input type="text" class="rounded p-1 form-control" v-model="personalData.email">
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
                                        {{ personalData.school.name }}
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
                        @click.prevent="createCompetitor(personalData, userPersonalDataId)"
                        type="button" 
                        class="btn btn-success" 
                        data-bs-dismiss="modal"
                        v-if="!isEdit"
                    >
                        Add
                    </button>
                    <button 
                        @click.prevent="editCompetitor(competitor.id, publicDisplayType, publicAlias, competitor.personal_data.id)"
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


