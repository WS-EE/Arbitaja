<script setup lang="ts">
// Import modules
import { ref, onMounted, computed  } from 'vue';
import { useRoute, RouterLink } from 'vue-router';
import { DateTime } from "luxon";
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import router from '@/router';
import { apiClient, CompetitionResponse, CompetitionUpsertRequest, CompetitorResponse, ScoringCriterionResponse, UserProfileResponse } from '@/services/api';

// Import displayalert
// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')
const alertTrigger = ref(0)

import displayAlert from '@/components/generic/displayAlert.vue';
import CriteriaTabel from './EditCriterias/CriteriaTabel.vue';
import competitorTable from './EditCompetitors/competitorTable.vue';

function showAlert(message: string, type: string, timeout: number = 3000){
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
    alertTrigger.value++
}

// Main content
const props = defineProps({
    isEdit: {
        type: Boolean,
        required: true,
    },
})

// Set empty ref variables
const route = useRoute();
const isLoading = ref<boolean>(true);
const isLoadingCriteria = ref<boolean>(true)
const isLoadingcompetitors = ref<boolean>(true)
const competition = ref<CompetitionResponse>({} as CompetitionResponse);
const competitors = ref<CompetitorResponse[]>([]);
const start_time = ref<string | null>('');
const end_time = ref<string | null>('');
const score_showtime = ref<string | null>('');
const criterias = ref<ScoringCriterionResponse[]>([]);
const adminUsers = ref<UserProfileResponse[]>([])

// Get id of the competition
const competition_id: number = Number(route.params.id);

// function for getting criterias based on competition
const getCriteriasByCompetition = async(competitionId?: number) => {
    try {
        if(competitionId === undefined) {
            throw new Error('Competition ID is undefined');
        }

         // Get criterias based on competition id
         const response = await apiClient.scoring.criteria.byCompetition(competitionId)
         if (!response.success) {
             throw new Error(response.error.message || 'Unknown error');
         }
         criterias.value = response.data

    } catch (error) {
        showAlert('Something went wrong while loading criterias.', 'danger')
    } finally {
        isLoadingCriteria.value = false
    }
}

// function for getting the competition
const getCompetitionById = async(id?: number) => {
    try {
        // set loading to be true
        isLoading.value = true;
        // check the active link
        if (props.isEdit){
            if(id === undefined) {
                throw new Error('Competition ID is undefined');
            }

            // If prop schools is not defined try to get them ourselves
            const response = await apiClient.competitions.details(id);
            if (!response.success) {
                throw new Error(response.error.message || 'Unknown error');
            }
            competition.value = response.data
            setOrganizer(competition.value.organizer)

            const formatUtcToLocal = (isoString?: string | null) => {
                if (!isoString) {
                    return ''
                }
                return DateTime.fromISO(isoString, { zone: "utc" })
                    .setZone(DateTime.local().zoneName)
                    .toFormat("yyyy-MM-dd'T'HH:mm")
            }

            // Format dates
            start_time.value = formatUtcToLocal(competition.value.start_time)
            end_time.value = formatUtcToLocal(competition.value.end_time)
            score_showtime.value = formatUtcToLocal(competition.value.score_showtime)

            // Get competition criteria
            await getCriteriasByCompetition(competition_id);
            await getCompetitorsByCompetition(competition_id);
        }

        if (!props.isEdit){
            // set empty values for displayed items
            competition.value = {} as CompetitionResponse;
        }

        // Get all users
        // Try getting the Users
        const response = await apiClient.users.list({ size: 500 })
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }
        const allUsers = response.data.content

        // Get all the admin users
        allUsers.forEach((user) => {
            if(user.roles){
                user.roles.forEach((role) => {
                    if(role.name === "admin"){
                        adminUsers.value.push(user)
                    }
                })
            }
        });

    } catch(error) {
        // Throw error if fail
        showAlert('Something went wrong while loading competition.', 'danger')
    } finally {
        // Show content when done loading
        isLoading.value = false
    }
}

// Get Competitors
const getCompetitorsByCompetition = async(competitionId?: number) => {
    try {
        // Set competitor loading to true when starting function
        isLoadingcompetitors.value = true

        if(competitionId === undefined) {
            throw new Error('Competition ID is undefined');
        }

         // Get competitors based on competition id
         const response = await apiClient.competitors.byCompetition(competitionId)
         if (!response.success) {
             throw new Error(response.error.message || 'Unknown error');
         }
         competitors.value = response.data
    } catch(error) {
        // Throw console log error if fail
        showAlert('No competitors found!', 'warning')
    } finally {
        // Show content when done loading
        isLoadingcompetitors.value = false
    }
}


// actions on mount
onMounted(async () => {
    await getCompetitionById(competition_id);
})

// Change organizer
const setOrganizer = (user: CompetitionResponse['organizer']) => {
  competition.value.organizer = user
}

// discard change and reload the competition values again
const discardChanges = async() => {
    await getCompetitionById(competition_id);
}

const selectedOrganizerName = computed(() => {
  const match = adminUsers.value.find(u => u.id === competition.value?.organizer?.id)
  return match?.personal_data?.full_name ?? competition.value?.organizer?.full_name ?? 'Select organizer'
})

// Add/Save function
const saveComp = async() => {
    try {
        if (!start_time.value || !end_time.value || !score_showtime.value) {
            throw new Error('Start time, end time, and score show time are required');
        }

        const utcStart = DateTime.fromISO(start_time.value)
            .setZone(DateTime.local().zoneName)
            .toUTC()
            .toISO();
        const utcEnd = DateTime.fromISO(end_time.value)
            .setZone(DateTime.local().zoneName)
            .toUTC()
            .toISO();
        const utcScoreShow = DateTime.fromISO(score_showtime.value)
            .setZone(DateTime.local().zoneName)
            .toUTC()
            .toISO();

        if (!utcStart || !utcEnd || !utcScoreShow) {
            throw new Error('Invalid date values');
        }

        competition.value.start_time = utcStart
        competition.value.end_time = utcEnd
        competition.value.score_showtime = utcScoreShow

        if(competition.value.organizer?.id === undefined) {
            throw new Error('Organizer is required');
        }
          const payload: CompetitionUpsertRequest = {
            name:           competition.value.name,
            start_time:     competition.value.start_time,
            end_time:       competition.value.end_time,
            score_showtime: competition.value.score_showtime,
            publish_scores: competition.value.publish_scores,
            organizer_id:   competition.value.organizer?.id,
          }

         // Try to edit or add competition
      if (props.isEdit) {
          const response = await apiClient.competitions.update(competition.value.id, payload)
            if (!response.success) {
                throw new Error(response.error.message || 'Unknown error');
            }
      } else {
          const response = await apiClient.competitions.create(payload)
          if (!response.success) {
              throw new Error(response.error.message || 'Unknown error');
          }
      }

        // Show success when everything is done
        await showAlert('Succesfully saved', 'success')
    } catch(error) {
        // Throw console log error if fail
        showAlert('Something went wrong while saveing.<br>' + error, 'danger')
    }
}

</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger"/>
    <div v-if="isLoading" class="position-absolute top-50 start-50">
        <PulseLoader/>
    </div>
    <div v-else class="container p-3 container-bottom">
        <h1 class="">Competition</h1>
        <p>
            ID: <strong>{{ competition.id }}</strong> 
            |
            Name: <strong>{{ competition.name }}</strong></p>
        <p></p>
        <hr>

        <!-- Personal data start block -->
        <h5>Main data</h5>
        <div class="row justify-content-start pt-3">
            <div class="col">
                <label for="fullName" class="form-label">Name</label>
            </div>
            <div class="col">
                <input
                    type="text"
                    class="form-control"
                    placeholder="Noor Meister"
                    name=""
                    id="fullName"
                    v-model="competition.name"
                />
            </div>
        </div>
        <div class="row justify-content-start pt-3">
            <div class="col">
                <label for="email" class="form-label">Start Time</label>
            </div>
            <div class="col">
                <input
                    type="datetime-local"
                    class="form-control"
                    v-model="start_time"
                    id="start_time"
                />
            </div>
        </div>
        <div class="row justify-content-start pt-3">
            <div class="col">
                <label for="end_time" class="form-label">End Time</label>
            </div>
            <div class="col">
                <input
                    type="datetime-local"
                    class="form-control"
                    v-model="end_time"
                    id="end_time"
                />
            </div>
        </div>
        <div class="row justify-content-start pt-3">
            <div class="col">
                <label for="score_showtime" class="form-label"
                    title="
                        Public accounts can only view score updates til this time. 
                        After that they turn stale and scores will stop updating for the public.
                        Scores will continue to update for admin users.
                    ">
                    Public Score Update Time
                    <i class="bi bi-info-circle"></i>
                </label>
                
                
            </div>
            <div class="col">
                <input
                    type="datetime-local"
                    class="form-control"
                    v-model="score_showtime"
                    id="score_showtime"
                />
            </div>
            
        </div>

        <!-- Organizer data start block -->
        <h5 class="pt-3">Organizer</h5>
        <div class="row pt-3">
            <div class="col">
                <label for="">Name</label>
            </div>
            <div class="col">
                
                <div class="btn-group">
                    <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      {{ selectedOrganizerName }}
                    </button>
                    <ul class="dropdown-menu">
                        <li 
                            v-for="user in adminUsers" 
                            @click="setOrganizer(user)" 
                            class="dropdown-item"
                        >
                            {{ user.personal_data?.full_name }}
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <!-- Competitors -->
        <div class="row pt-3" v-if="isEdit">
            <div class="col">
                <h5 class="">Competitors</h5>
            </div>
            <div class="col">
                <!-- Horizontal under breakpoint -->
                <RouterLink :to="'/admin/competition/edit/competitors/' + competition.id" class="btn btn-outline-dark">Edit Competitors</RouterLink>
            </div>
            <competitorTable :competitors="competitors" :addActions="false"/>
        </div>
        <!-- Criterias -->
        
        <div class="row pt-3" v-if="isEdit">
            <div class="col justify-content-center">
                <h5 >Criteria</h5>
            </div>
            <div class="col">
                <!-- Horizontal under breakpoint -->
                <ul class="list-group list-group-horizontal">
                    <RouterLink :to="'/admin/competition/edit/criterias/' + competition.id" class="btn btn-outline-dark">Edit Criteria</RouterLink>
                </ul>       
            </div>
            <CriteriaTabel :criterias="criterias" :competition_id="competition.id"/>
        </div>

    </div>
    <!-- Action Buttons -->
    <div class="container d-flex justify-content-end align-items-end pt-3 pb-4 mb-3">
        <button @click.prevent="saveComp()" class="btn btn-success me-3">Save<i class="ms-1 bi bi-floppy"></i></button>
        <button @click="discardChanges()" class="btn btn-outline-danger me-3">Discard<i class="ms-1 bi bi-trash"></i></button>
        <button @click="router.back()" class="btn btn-outline-dark me-3">Go Back</button>
    </div>
</template>