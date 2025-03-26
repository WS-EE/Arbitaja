<script setup>
// Import modules
import axios from 'axios';
import { ref, onMounted, defineProps } from 'vue';
import { useRoute } from 'vue-router';
import { DateTime } from "luxon";
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import router from '@/router';

// Import displayalert
// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

import displayAlert from '@/components/generic/displayAlert.vue';

function showAlert(message, type, timeout){
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

// Main content
const props = defineProps({
    isEdit: {
        type: Boolean,
        required: true,
    },
})

const route = useRoute();
const isLoading = ref(true);
const competition = ref();
const start_time = ref();
const end_time = ref();
const score_showtime = ref();
const adminUsers = ref([]);

onMounted(async () => {
    try {
        // check the active link
        if (props.isEdit === true){
            // Get id of the competition
            const competition_id = route.params.id

            // If prop schools is not defined try to get them ourselves
            const response = await axios.get('competition/get?id=' + competition_id);
            competition.value = response.data

            // Format dates
            start_time.value = DateTime.fromISO(competition.value.start_time, { zone: "utc" })
                .setZone(DateTime.local().zoneName)
                .toFormat("yyyy-MM-dd'T'HH:mm");
            end_time.value = DateTime.fromISO(competition.value.end_time, { zone: "utc" })
                .setZone(DateTime.local().zoneName)
                .toFormat("yyyy-MM-dd'T'HH:mm");
            score_showtime.value = DateTime.fromISO(competition.value.score_showtime, { zone: "utc" })
                .setZone(DateTime.local().zoneName)
                .toFormat("yyyy-MM-dd'T'HH:mm");

            // TEMP REMOVE
            console.log(competition.value)
        }
        
        if (props.isEdit === false){
            // set empty values for displayed items
            competition.value = ({ 
                name: null,
                start_time: null,
                end_time: null,
                score_showtime: null,
                organizer: { full_name: 'Not set' }
            })
        }

        // Get all users
        // Try getting the Users
        const allUsers = await axios.get('user/profile/all')
        
        // Get all the admin users
        allUsers.data.forEach((user) => {
            user.roles.forEach((role) => {
                if(role.name === "admin"){
                    const adminUser = { full_name: user.personal_data.full_name, id: user.id, username: user.username }
                    adminUsers.value.push(adminUser)
                }
            })
        });

    } catch(error) {
        // Throw console log error if fail
        showAlert('Something went wrong while loading.', 'danger')
    } finally {
        // Show content when done loading
        isLoading.value = false
    }
})

// Change organizer
const setOrganizer = (user) => {
    competition.value.organizer = user
}

// Add/Save function
const saveComp = async() => {
    try {
        // convert date data back to UTC time 
        // add new dates to the competition object
        competition.value.start_time = DateTime.fromISO(start_time.value)
            .setZone(DateTime.local().zoneName)
            .toUTC()
            .toISO()
        competition.value.start_time = DateTime.fromISO(start_time.value)
            .setZone(DateTime.local().zoneName)
            .toUTC()
            .toISO()
        competition.value.start_time = DateTime.fromISO(start_time.value)
            .setZone(DateTime.local().zoneName)
            .toUTC()
            .toISO()

        if (props.isEdit === true){
            await axios.put('competition/edit', competition.value)
        } else {
            await axios.post('competition/add', competition.value)
        }

        // Show success when everything is done
        showAlert('Succesfully saved', 'success')
    } catch(error) {
        console.log(error)
        // Throw console log error if fail
        showAlert('Something went wrong while saveing.', 'danger')
    }
}

</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout"/>
    <div v-if="isLoading" class="text-center pt-5">
        <PulseLoader/>
    </div>
    <div v-else class="container p-3 container-bottom">
        <h1 class="">Competition</h1>
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

        <!-- System data start block -->
        <h5 class="pt-3">Organizer</h5>
        <div class="row pt-3">
            <div class="col">
                <label for="">Name</label>
            </div>
            <div class="col">
                
                <div class="btn-group">
                    <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        {{ competition.organizer.full_name }}
                    </button>
                    <ul class="dropdown-menu">
                        <li 
                            v-for="user in adminUsers" 
                            @click="setOrganizer(user)" 
                            class="dropdown-item"
                        >
                            {{ user.full_name }}
                        </li>
                    </ul>
                </div>
            </div>
        </div> 
    </div>
    <!-- Action Buttons -->
    <div class="container d-flex justify-content-end align-items-end pt-3 pb-3">
        <button @click.prevent="saveComp" class="btn btn-success me-3">Save<i class="ms-1 bi bi-floppy"></i></button>
        <button @click="discardChanges" class="btn btn-outline-danger me-3">Discard<i class="ms-1 bi bi-trash"></i></button>
        <button @click="router.back()" class="btn btn-outline-dark me-3">Go Back</button>
    </div>
</template>