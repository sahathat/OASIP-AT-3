<script setup>
import { computed } from "@vue/reactivity";
import { onBeforeMount, onUpdated, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import vueFilePond from "vue-filepond";

// Create component
// const FilePond = vueFilePond(
//   FilePondPluginFileValidateType,
//   FilePondPluginImagePreview
// );

const name = ref("");
const eMail = ref('')
const loginEmail = localStorage.getItem('email')
const startDate = ref("");
const category = ref("");
const startTime = ref("");
const noteText = ref("");
const cateId = ref("");

const { params } = useRoute();
const myRouoter = useRouter();
const goHome = () => myRouoter.push({ name: "Home" });

const nameLength = 100;
const emailLength = 100;
const noteLength = 500;

// check is Login ?
const isLogin = () => {
  if(loginEmail!==null && userRole.value!=='admin') eMail.value = loginEmail
  else if(userRole.value=='admin' || userRole.value=='guest') eMail.value = ''
  return eMail.value
}

const userRole = ref('guest')
const checkRole = () => {
    const role = localStorage.getItem('role')
    // console.log(role.substring(6,role.length-1))
    if(role !== null){
        if(role.substring(6,role.length-1)=='admin') userRole.value = 'admin'
        else if(role.substring(6,role.length-1)=='lecturer') userRole.value = 'lecturer'
        else if(role.substring(6,role.length-1)=='student') userRole.value = 'student'
    }
    else userRole.value = 'guest'
    return userRole.value
}

const db = "http://localhost:5000/booking";
// //for vm
// const eventLink = `${import.meta.env.BASE_URL}api/events`;
// const categoryLink = `${import.meta.env.BASE_URL}api/categories`;
// const eventLinkForGuest = `${import.meta.env.BASE_URL}api/guests/events`;
// const categoryLinkForGuest = `${import.meta.env.BASE_URL}api/guests/categories`;
// const refreshLink = `${import.meta.env.BASE_URL}api/users/refresh`;
// const fileLink = `${import.meta.env.BASE_URL}api/files/events`;

//for localhost
const eventLink = "http://localhost:8443/api/events";
const categoryLink = "http://localhost:8443/api/categories";
const eventLinkForGuest = "http://localhost:8443/api/guests/events";
const categoryLinkForGuest = "http://localhost:8443/api/guests/categories";
const refreshLink = "http://localhost:8443/api/users/refresh";
const fileLink = "http://localhost:8443/api/files/events";

const eventList = ref([]);
const categoryList = ref([]);
const addSuccess = ref(undefined);
const getStatus = ref(undefined);

// validate past
const validateIsPast = ref(undefined);

// validate name
const validateNameisNotNull = ref(undefined);
const validateNameLength = ref(undefined);

// validate between date
const validateBetweenDate = ref(undefined);

// validate category
const validateCategoryisNotNull = ref(undefined);

// validate start Date
const validateStartDateisNotNull = ref(undefined);

// validate start Time
const validateStartTimeisNotNull = ref(undefined);

// validate note
const validateNoteLength = ref(undefined);

// validate email
const validateEmailisNotNull = ref(undefined);
const validateEmailLength = ref(undefined);
const validateEmailValid = ref(undefined);
const valEmail = (input) => {
  let valid =
    /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
  if (input.match(valid)) {
    return true;
  } else {
    return false;
  }
};

// overlap
const betweenDateWarning = ref(undefined);

const overlap = () => {
  betweenDateWarning.value = undefined;
  let isOverlap = undefined;
  for (let check of eventList.value) {
    if (check.categoryName == category.value) {
      // console.log(Date.parse(`${startDate.value}T${startTime.value}:00+07:00`))
      // console.log(Date.parse(check.eventStartTime))
      // console.log(Date.parse(`${check.eventStartTime.substring(0,10)}T${calTime(parseInt(check.eventStartTime.substring(11,13)) ,parseInt(check.eventStartTime.substring(14,16)),check.eventDuration)}:00+07:00`))
      // console.log('cut')

      if (
        Date.parse(`${startDate.value}T${startTime.value}:00+07:00`) >=
          Date.parse(check.eventStartTime) &&
        Date.parse(`${startDate.value}T${startTime.value}:00+07:00`) <=
          Date.parse(
            `${check.eventStartTime.substring(0, 10)}T${calTime(
              parseInt(check.eventStartTime.substring(11, 13)),
              parseInt(check.eventStartTime.substring(14, 16)),
              check.eventDuration
            )}:00+07:00`
          )
      ) {
        isOverlap = true;
        betweenDateWarning.value = String(
          `Cannot select during ${check.eventStartTime.substring(
            0,
            10
          )} between ${check.eventStartTime.substring(11, 16)} - ${calTime(
            parseInt(check.eventStartTime.substring(11, 13)),
            parseInt(check.eventStartTime.substring(14, 16)),
            check.eventDuration
          )}`
        );
      } else if (
        Date.parse(
          `${startDate.value}T${calTime(
            parseInt(startTime.value.substring(0, 2)),
            parseInt(startTime.value.substring(3, 5)),
            durationTime.value
          )}:00+07:00`
        ) >= Date.parse(check.eventStartTime) &&
        Date.parse(
          `${startDate.value}T${calTime(
            parseInt(startTime.value.substring(0, 2)),
            parseInt(startTime.value.substring(3, 5)),
            durationTime.value
          )}`
        ) <=
          Date.parse(
            `${check.eventStartTime.substring(0, 10)}T${calTime(
              parseInt(check.eventStartTime.substring(11, 13)),
              parseInt(check.eventStartTime.substring(14, 16)),
              check.eventDuration
            )}:00+07:00`
          )
      ) {
        isOverlap = true;
        betweenDateWarning.value = String(
          `Your event time is overlapped between ${check.eventStartTime.substring(
            11,
            16
          )} and ${calTime(
            parseInt(check.eventStartTime.substring(11, 13)),
            parseInt(check.eventStartTime.substring(14, 16)),
            check.eventDuration
          )}
          ${check.eventStartTime.substring(0, 10)}`
        );
      }
    }
  }
  return isOverlap;
};

//for calculate time
const calTime = (hour, minute, addTime) => {
  let sum = undefined;
  let h = hour;
  let m = minute + addTime;
  // console.log(`minute + add time :${m}`)
  // console.log(`this is minute : ${minute} and this addTime : ${addTime}`)
  // console.log(`this is hour : ${h}`)
  if (m >= 60) {
    h = h + 1;
    m = m - 60;
  }
  h = h < 10 ? `0${h}` : h;
  m = m < 10 ? `0${m}` : m;

  sum = `${h}:${m}`;
  // console.log(sum)
  return sum;
};

// check date & time
const day = ref();
const month = ref();
const year = ref();
const hours = ref();
const minutes = ref();
const minDate = ref("");
const minTime = ref("");
let clock = () => {
  let dateToday = new Date();
  day.value =
    dateToday.getDate() < 10
      ? `0${dateToday.getDate()}`.toString()
      : dateToday.getDate(); // day
  month.value =
    dateToday.getMonth() + 1 < 10
      ? `0${dateToday.getMonth() + 1}`.toString()
      : dateToday.getMonth() + 1; // month
  year.value = dateToday.getFullYear().toString(); // year

  hours.value =
    dateToday.getHours() < 10
      ? `0${dateToday.getHours()}`.toString()
      : dateToday.getHours(); // hour
  minutes.value =
    dateToday.getMinutes() < 10
      ? `0${dateToday.getMinutes()}`.toString()
      : dateToday.getMinutes(); // minute

  //  combine
  minTime.value = `${hours.value}:${minutes.value}`;
  minDate.value = `${year.value}-${month.value}-${day.value}`;
};
setInterval(clock, 1000);

// submit
const toSubmit = ref(false)
const submitt = () => {
  toSubmit.value = true
  validateEmailisNotNull.value = undefined;
  validateNameisNotNull.value = undefined;
  validateCategoryisNotNull.value = undefined;
  validateStartDateisNotNull.value = undefined;
  validateStartTimeisNotNull.value = undefined;
  validateEmailLength.value = undefined;
  validateNameLength.value = undefined;
  validateNoteLength.value = undefined;
  validateEmailValid.value = undefined;
  validateIsPast.value = undefined;
  validateBetweenDate.value = undefined;
  addSuccess.value = undefined;
  if (
    name.value !== "" &&
    eMail.value !== "" &&
    startDate.value !== "" &&
    category.value !== "" &&
    startTime.value !== "" &&
    durationTime.value !== ""
  ) {
    if (name.value.length > nameLength) {
      validateNameLength.value = false;
      //alert('The number of characters name is exceeded.')
    } else if (valEmail(eMail.value) == false) {
      validateEmailValid.value = false;
      //alert('Invalid email address!')
    } else if (eMail.value.length > emailLength) {
      validateEmailLength.value = false;
      //alert('The number of characters in the email exceeded the limit.')
    } else if (noteText.value.length > noteLength) {
      validateNoteLength.value = false;
      //alert('Characters beyond the limit')
    } else if (
      Date.parse(`${startDate.value}T${startTime.value}`) <
      Date.parse(`${minDate.value}T${minTime.value}`)
    ) {
      validateStartDateisNotNull.value = false;
      validateStartTimeisNotNull.value = false;
      validateIsPast.value = false;
    } else if (overlap()) {
      validateBetweenDate.value = true;
      //  alert(`event is overlap`)
    } else if (addBooking()) {
      name.value = "";
      eMail.value = "";
      category.value = "";
      startDate.value = "";
      startTime.value = "";
      noteText.value = "";
      validateEmailisNotNull.value = undefined;
      validateNameisNotNull.value = undefined;
      validateCategoryisNotNull.value = undefined;
      validateStartDateisNotNull.value = undefined;
      validateStartTimeisNotNull.value = undefined;
      validateEmailLength.value = undefined;
      validateNameLength.value = undefined;
      validateNoteLength.value = undefined;
      validateEmailValid.value = undefined;
      validateIsPast.value = undefined;
      validateBetweenDate.value = undefined;
      addSuccess.value = undefined;
    }
  } else {
    // alert('Please complete the information.')
    if (name.value == "") {
      validateNameisNotNull.value = false;
    }
    if (eMail.value == "") {
      validateEmailisNotNull.value = false;
    }
    if (category.value == "") {
      validateCategoryisNotNull.value = false;
    }
    if (startDate.value == "") {
      validateStartDateisNotNull.value = false;
    }
    if (startTime.value == "") {
      validateStartTimeisNotNull.value = false;
    }
  }
};

// fetch create
const isStatus = ref(undefined);
const addBooking = async () => {
  let createStatus = undefined;
  const key = localStorage.getItem('key')
  // console.log(key)
  let eventForFetch 
  if(userRole.value=='guest') eventForFetch = eventLinkForGuest
  else if(userRole.value!=='guest') eventForFetch = eventLink

  const res = await fetch(eventForFetch, {
    method: "POST",
    headers: {
            "Authorization": "Bearer " + key,
            "Accept": 'application/json',
            "content-type": "application/json",
    },
    body: JSON.stringify({
      bookingName: name.value,
      bookingEmail: eMail.value,
      eventStartTime: `${startDate.value}T${startTime.value}:00+07:00`,
      eventNotes: noteText.value,
      eventCategoryId: cateId.value,
    }),
  });

  const event = ref(0)
  if (res.status === 201) {
    addSuccess.value = true;
    createStatus = true;
    isStatus.value = true;
    isLogin()
    
    event.value = await res.json()
    console.log(event.value.id)

    const data = new FormData();
    if(fileName.value.length != 0) data.append("file",fileName.value)
    console.log(data.values)

    //add file
    const resFile = await fetch(`${fileLink}/${event.value.id}`, {
      method: "POST",
      headers: {
            "Authorization": "Bearer " + key,
      },
      body: data
    });
    console.log(resFile.status)
    setTimeout(() => (addSuccess.value = false), 5000);

  } else if (res.status === 400) {
    validateBetweenDate.value = true;
    isStatus.value = false;
  }
  // refresh token ----------------------------------------------------------- //
    else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
          console.log('test...')
          const resForRefresh = await fetch(refreshLink, {
            headers: {
            Authorization: "Bearer " + key,
            isRefreshToken: true ,
            },
            })
          const jwt = await resForRefresh.json()
          console.log(jwt)
          if(resForRefresh.status === 200){
              // set localStorage
              localStorage.setItem('key',jwt.token)
              localStorage.setItem('token','refreshToken')
              addBooking()
          }
    } else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
          localStorage.removeItem('key')
          localStorage.removeItem('token')
          goHome()
        // console.log('เข้า')
    } 
  // ------------------------------------------------------------------------- //
    else {
    createStatus = false;
    isStatus.value = false;
  }
  return createStatus;
};

//GET category
// first get Category
const getCategory = async () => {
  const key = localStorage.getItem('key')
  let categoryForFetch 
  if(userRole.value=='guest') categoryForFetch = categoryLinkForGuest
  else if(userRole.value!=='guest') categoryForFetch = categoryLink
  // console.log(categoryForFetch)
  const res = await fetch(categoryForFetch,{
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
    }
  });
  if (res.status === 200) {
    categoryList.value = await res.json();
    getStatus.value = true;
  } 
  // refresh token ----------------------------------------------------------- //
    else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
      console.log('test...')
         const resForRefresh = await fetch(refreshLink, {
            headers: {
            Authorization: "Bearer " + key,
            isRefreshToken: true ,
            },
            })
          const jwt = await resForRefresh.json()
          console.log(jwt)
          if(resForRefresh.status === 200){
              // set localStorage
              localStorage.setItem('key',jwt.token)
              localStorage.setItem('token','refreshToken')
              getCategory()
          }
    } else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
          localStorage.removeItem('key')
          localStorage.removeItem('token')
          goHome()
        // console.log('เข้า')
    } 
    // ------------------------------------------------------------------------- //
      else {
        getStatus.value = false;
    }
};

// get event
// const resGetEvent = ref(undefined);
// //const countGetEvent=ref(0)

// // check every 10 second
// setInterval(async () => {
//   const key = localStorage.getItem('key')
//   //console.log(countGetEvent.value++)
//   resGetEvent.value = await fetch(eventLink,{
//     method: "GET",
//     headers: {
//             "Authorization":'Bearer ' + key ,
//             "Accept": 'application/json',
//             "content-type": "application/json",
//     }
//   });
//   if (resGetEvent.value.status === 200) {
//     eventList.value = await resGetEvent.value.json();
//     getStatus.value = true;
//   } else getStatus.value = false;
// }, 10000);

//GET event
const getEvent = async () => {
  const key = localStorage.getItem('key')
  // let eventForFetch 
  // if(userRole.value=='guest') eventForFetch = eventLinkForGuest
  // else if(userRole.value!=='guest') eventForFetch = eventLink
  const res = await fetch(eventLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    eventList.value = await res.json();
  } else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
    console.log('test...')
    const resForRefresh = await fetch(refreshLink, {
      headers: {
        Authorization: "Bearer " + key ,
        isRefreshToken: true ,
      },
    })
      const jwt = await resForRefresh.json()
      console.log(jwt)
      if(resForRefresh.status === 200){
        // set localStorage
        localStorage.setItem('key',jwt.token)
        localStorage.setItem('token','refreshToken')
        getEvent()
      }
    }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        console.log('เข้า')
    }
};

//get duration
const durationTime = computed(() => {
  let value = undefined;
  for (let cate of categoryList.value) {
    if (cate.eventCategoryName == category.value) {
      value = cate.eventCategoryDuration;
      cateId.value = cate.id;
    } else if (category.value == "") value = "00";
  }
  return value;
});

const fileName = ref("")
const fileChanged = (e) => {
  fileName.value = e.target.files[0]
  console.log(fileName.value);
}

onBeforeMount(async () => {
  await getCategory();
  await getEvent();
  isLogin()
  checkRole()
});

onUpdated(async () => {
  checkRole()
});

</script>

<template>
<body>
  <section class="position-relative py-4 py-xl-5" style="margin-top: 30px;">
        <div class="container">
            <div class="row mb-5">
                <div class="col-md-8 col-xl-6 text-center mx-auto">
                    <h2>Booking</h2>
                    <p class="w-lg-50">Fill up the form below to send a online appointment.<br></p>
                </div>
            </div>
            <div class="row d-flex justify-content-center" style="margin-top: -45px;">
                <div class="col-md-8 col-lg-8 col-xl-6 col-xxl-6">
                    <div class="card mb-5">
                        <div class="card-body d-flex flex-column align-items-center" style="padding-top: 30px;padding-bottom: 30px;">
                            <form class="text-start" style="width: 300px;">

                                <!-- name input -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold" for="name" style="margin-bottom: -5px;">Name :</label>
                                  <small class="float-end align-self-end"> {{ name.length }}/{{ nameLength }} charecters </small>
                                  <input v-model="name" class="form-control" type="text" name="name" placeholder="Somchai Jaidee (AT-3)" required>
                                </div>

                                <!-- email input -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold" style="padding-bottom: 0px;margin-bottom: 0px;">Email :</label>
                                  <small class="float-end align-self-end"> {{ eMail.length }}/{{ emailLength }} charecters </small>

                                  <input v-if="userRole=='guest'|| userRole=='admin'" v-model="eMail" class="form-control" type="email" name="email" placeholder="somchai.jai@mail.kmutt.ac.th" required="">
                                  <input v-if="loginEmail!==null && (userRole =='student' || userRole =='lecturer')"  :disabled="true" v-model="eMail" class="form-control" type="email" name="email" placeholder="somchai.jai@mail.kmutt.ac.th" required="">
                                </div>

                                <!-- category input -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold" style="margin-bottom: 0px;">Category :</label>
                                  <select v-model="category" class="form-select" required="">
                                        <optgroup label="Select category">
                                            <option  v-for="cat in categoryList" :key="cat.id" :value="cat.eventCategoryName">
                                              {{ cat.eventCategoryName }}
                                            </option>
                                        </optgroup>
                                    </select>
                                </div>

                                <!-- duration -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold" style="margin-bottom: 0px;">Duration :</label>
                                  <input v-model="durationTime" class="form-control" type="email" name="email" placeholder="00" disabled="" >
                                  <small class="fw-normal float-end d-lg-flex justify-content-lg-end">minutes</small>
                                </div>

                                <!-- startDate input -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold">Start Date:</label>
                                  <input v-model="startDate" :min="minDate" class="form-control" type="date" required="">
                                </div>

                                <!-- startTime input -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold">Start Time:</label>
                                  <input v-model="startTime" class="form-control" type="time" required="">
                                </div>

                                <!-- note input -->
                                <div class="mb-3 mb-lg-0">
                                  <label class="form-label fw-semibold" style="padding-bottom: 0px;margin-bottom: 0px;">Note :</label>
                                  <small class="float-end align-self-end">{{ noteText.length }}/{{ noteLength }} charecters</small>
                                </div>
                                <textarea v-model="noteText" class="form-control form-control-md" style="margin-top: -14px;"></textarea>

                                <!-- add file -->
                                <div class="mb-3 mt-lg-3" style="margin-top: 15px;">
                                  <label class="form-label fw-semibold" style="margin-bottom: 0px;">Add File :</label>
                                  <input @change="fileChanged($event)" ref="pond" label-idle="Drop files here or <span class='filepond--label-action'>Browse</span>" class="form-control" type="file" name="file" placeholder="somchai.jai@mail.kmutt.ac.th">
                                  <small class="float-end align-self-end" style="margin-bottom: 40px;">The file size cannot be larger than 10 MB.<br></small>
                                </div>

                                <!-- button -->
                                <div class="mb-3">
                                  <button class="btn btn-primary d-block w-100" type="button" style="margin-bottom: 15px;margin-top: 25px;" data-bs-target="#confirm" data-bs-toggle="modal">Submit</button>
                                  <button class="btn btn-light d-block w-100" type="button">Cancel</button>
                                </div>

                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- confirm  -->
        <div class="modal fade" role="dialog" tabindex="-1" id="confirm">
          <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
            <div class="modal-content">
              <div class="modal-header text-bg-warning" style="padding-top: 10px;padding-bottom: 10px;padding-left: 20px;padding-right: 20px;">
                <h4 class="modal-title fs-3">Are you sure ?</h4><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                <p class="fs-6">Are you sure to create new event ?</p>
              </div>
              <div class="modal-footer" style="padding-bottom: 5px;padding-top: 5px;">
                <button class="btn btn-primary btn-sm" type="button" @click="submitt" data-bs-dismiss="modal" data-bs-target="#">Yes</button>
                <button class="btn btn-danger btn-sm" type="button" data-bs-dismiss="modal" data-bs-target="#">Cancel</button>
              </div>
            </div>
          </div>
        </div>
    </section>

  <!-- -------------------------------------------------------------------------------------------------------------------------------- -->
      <!-- for alert -->
      <div class="alert-area">
        <div v-if="validateNameisNotNull == false" class="alert text-sm">
          <span class="closebtn" @click="validateNameisNotNull = true">x</span>
          <strong class="block">Error!</strong> Please input your name.
        </div>

        <div v-if="validateNameLength == false" class="alert text-sm">
          <span class="closebtn" @click="validateNameLength = true">x</span>
          <strong class="block">Error!</strong> The number of characters name is
          exceeded.
        </div>

        <div v-if="validateEmailisNotNull == false" class="alert text-sm">
          <span class="closebtn" @click="validateEmailisNotNull = true">x</span>
          <strong class="block">Error!</strong> Please input your email.
        </div>

        <div v-if="validateEmailValid == false" class="alert text-sm">
          <span class="closebtn" @click="validateEmailValid = true">x</span>
          <strong class="block">Error!</strong> Invalid email address!.
        </div>

        <div v-if="validateEmailLength == false" class="alert text-sm">
          <span class="closebtn" @click="validateEmailLength = true">x</span>
          <strong class="block">Error!</strong> The number of characters email
          is exceeded.
        </div>

        <div v-if="validateCategoryisNotNull == false" class="alert text-sm">
          <span class="closebtn" @click="validateCategoryisNotNull = true"
            >x</span
          >
          <strong class="block">Error!</strong> Please select category.
        </div>

        <div v-if="validateStartDateisNotNull == false" class="alert text-sm">
          <span class="closebtn" @click="validateStartDateisNotNull = true"
            >x</span
          >
          <strong class="block">Error!</strong> Please input date.
        </div>

        <div v-if="validateStartTimeisNotNull == false" class="alert text-sm">
          <span class="closebtn" @click="validateStartTimeisNotNull = true"
            >x</span
          >
          <strong class="block">Error!</strong> Please input time.
        </div>

        <div v-if="validateNoteLength == false" class="alert text-sm">
          <span class="closebtn" @click="validateNoteLength = true">x</span>
          <strong class="block">Error!</strong> Characters note beyond the limit
          .
        </div>

        <div v-if="validateIsPast == false" class="alert text-sm">
          <span class="closebtn" @click="validateIsPast = true">x</span>
          <strong class="block">Error!</strong> Can't select past date and time
          .
        </div>

        <div v-if="addSuccess == true" class="alert success text-sm">
          <span class="closebtn" @click="addSuccess = false">x</span>
          <strong class="block">Success!</strong> Add data success.
        </div>

        <div v-if="validateBetweenDate == true" class="alert warning text-sm">
          <span class="closebtn" @click="validateBetweenDate = false">x</span>
          <strong class="block">Warning!</strong> {{ betweenDateWarning }} 
        </div>

        <div v-if="isStatus == false" class="alert error text-sm">
          <span class="closebtn" @click="isStatus = true">x</span>
          <strong class="block">Sorry!</strong> Can't create booking.
        </div>

        <div
          v-if="
            getStatus == false ||
            (categoryList.length == 0 && eventList.length == 0)
          "
          class="alert warning text-sm"
        >
          <strong class="block">Warning!</strong> A system error has occurred,
          please try again.
        </div>
      </div>
</body>
</template>

<style scoped>
/* width */
::-webkit-scrollbar {
  width: 10px;
}

/* Track */
::-webkit-scrollbar-track {
  box-shadow: inset 0 0 5px rgb(255, 255, 255);
  border-radius: 10px;
}

/* Handle */
::-webkit-scrollbar-thumb {
  background: rgb(111, 129, 151);
  border-radius: 0px;
}

/* Handle on hover */
::-webkit-scrollbar-thumb:hover {
  background: #53719d;
}

/* for notification */
.overlay {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.7);
  transition: opacity 500ms;
  visibility: hidden;
  opacity: 0;
}

.overlay:target {
  visibility: visible;
  opacity: 1;
}

.popup {
  margin: 120px auto;
  padding: 20px;
  background: #fff;
  border-radius: 5px;
  width: 70%;
  position: relative;
  transition: all 5s ease-in-out;
}

.popup h2 {
  margin-top: 0;
  color: #333;
}

.popup .close {
  position: absolute;
  top: 20px;
  right: 30px;
  transition: all 200ms;
  font-size: 30px;
  font-weight: bold;
  text-decoration: none;
  color: #333;
}

.popup .close:hover {
  color: #06d85f;
}

@media screen and (max-width: 700px) {
  .popup {
    width: 70%;
  }
}

/* submit */
.popup2 {
  margin: auto;
  margin-top: 17%;
  padding-top: 23px;
  background: #fff;

  width: 20%;
  height: 130px;
  position: relative;
  transition: all 5s ease-in-out;
}

.popup2 h2 {
  margin-top: 0;
  color: #333;
}

.popup2 .option {
  bottom: 0;
}

@media screen and (max-width: 700px) {
  .popup2 {
    width: 70%;
  }

  .option {
    width: 20%;
  }
}

/* alert */
.alert-area {
  position: fixed;
  top: 150px;
  right: 0;
  background-color: transparent;
  width: 23%;
  margin-right: 10px;
}

.alert {
  position: relative;
  width: 100%;
  padding-top: 15px;
  padding-left: 15px;
  padding-right: 15px;
  padding-bottom: 10px;
  animation: moveLeft 0.9s;
  animation-timing-function: ease-in-out;
  background-color: #f44336;
  color: white;
  opacity: 1;
  transition: opacity 0.6s;
  margin-bottom: 15px;
}

.alert.success {
  background-color: #04aa6d;
}

.alert.info {
  background-color: #2196f3;
}

.alert.warning {
  background-color: #ff9800;
}

.closebtn {
  margin-left: 15px;
  color: white;
  font-weight: bold;
  float: right;
  font-size: 22px;
  line-height: 20px;
  cursor: pointer;
  transition: 0.3s;
}

.closebtn:hover {
  color: black;
}

/* animetion */
@keyframes moveLeft {
  0% {
    right: -300px;
    top: 0px;
    opacity: 0;
  }

  100% {
    right: 0px;
    top: 0px;
    opacity: 1.5;
  }
}

@keyframes wii {
  0% {
    opacity: 0;
  }

  100% {
    opacity: 1.5;
  }
}

.showUp {
  position: relative;
  animation: wii 1s;
  animation-timing-function: ease-in-out;
}
</style>
