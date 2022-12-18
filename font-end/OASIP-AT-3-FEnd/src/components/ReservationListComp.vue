<script setup>
import { ref, onBeforeMount, onUpdated } from "vue";
import { useRoute,useRouter } from "vue-router";
const eventList = ref([]);
const categoryList = ref([]);
const filterReservationList = ref([]);

const { params } = useRoute();
const myRouoter = useRouter();
const goHome = () => myRouoter.push({ name: "Home" });

const db = "http://localhost:5000/booking";
// //for vm
const forLink = '${import.meta.env.BASE_URL}'
//for localhost
// const forLink = 'http://localhost:8443/'
const eventLink = `${forLink}api/events`;
const categoryLink = `${forLink}api/categories`;
const refreshLink = `${forLink}api/users/refresh`;

const userRole = ref('guest')
const checkRole = () => {
    // const getRole = localStorage.getItem('role')
    // const role = getRole.substring(6,getRole.length-1)
    const role = localStorage.getItem('role')
    // console.log(role.substring(6,role.length-1))
    if(role !== null){
        if(role.substring(6,role.length-1)=='admin') userRole.value = 'admin'
        else if(role.substring(6,role.length-1)=='lecturer') userRole.value = 'lecturer'
        else if(role.substring(6,role.length-1)=='student') userRole.value = 'student'
    }
    else userRole.value = 'guest'
    console.log(userRole.value)
    return userRole.value
}

//GET event
const getEvent = async () => {
  const key = localStorage.getItem('key')
  let link = ''
  if(userRole.value=='guest') link = eventGuestLink
  else if(userRole.value!=='guest') link = eventLink
  console.log(link)
  const res = await fetch(link, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    eventList.value = await res.json();
    filterReservationList.value = eventList.value
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
        // console.log('เข้า')
    }
};

//GET category
const getCategory = async () => {
  const key = localStorage.getItem('key')
  const res = await fetch(categoryLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    categoryList.value = await res.json();
  } 
  // refresh ------------------------------------------ //
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
    }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        // console.log('เข้า')
    }
    // ----------------------------------------- //
};

//router
const myRouter = useRouter();
const goReservation = (input) =>
  myRouter.push({
    name: "Reservation",
    params: {
      id: input.id,
    },
  });

onBeforeMount(async () => {
  checkRole();
  await getEvent();
  await getCategory();
});

onUpdated(async () => {
  // await getEvent();
  await getCategory();
});

// timer
const day = ref();
const month = ref();
const year = ref();
const hours = ref();
const minutes = ref();
const date = ref("");
const time = ref("");

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
  time.value = `${hours.value}:${minutes.value}`;
  date.value = `${year.value}-${month.value}-${day.value}`;
};
setInterval(clock, 1000);

// filter
const fStartDate = ref("");
const fStatus = ref("");
const fCategory = ref("");
const noInputFilter = ref(undefined);

const search = () => {
  noInputFilter.value = undefined;

  if (fStartDate.value == "" && fStatus.value == "" && fCategory.value == "") {
    // alert("input in form if you want to filter");
    noInputFilter.value = true;
  } else {
    filterReservationList.value = [];

    // หากมีแค่ start date จะทำการเลือก start date ตามนั้นแต่ว่าจะเลือเวลาทั้งวัน
    if (
      fStartDate.value !== "" &&
      fStatus.value == "" &&
      fCategory.value == ""
    ) {
      for (let filter of eventList.value) {
        if (
          Date.parse(filter.eventStartTime) >=
            Date.parse(`${fStartDate.value}T00:00:00+07:00`) &&
          Date.parse(filter.eventStartTime) <=
            Date.parse(`${fStartDate.value}T23:59:00+07:00`)
        ) {
          filterReservationList.value.push(filter);
        }
      }
      // console.log(filterReservationList.value)
    }
    // สำหรับ past and upcoming จะ show ข้อมูลที่จะมีในอนาคต หรืออดีต
    else if (
      fStatus.value !== "" &&
      fStartDate.value == "" &&
      fCategory.value == ""
    ) {
      if (fStatus.value == "upcoming") {
        for (let filter of eventList.value) {
          if (
            Date.parse(filter.eventStartTime) >
            Date.parse(`${date.value}T${time.value}:00+07:00`)
          ) {
            filterReservationList.value.push(filter);
          }
        }
      } else if (fStatus.value == "past") {
        for (let filter of eventList.value) {
          if (
            Date.parse(filter.eventStartTime) <=
            Date.parse(`${date.value}T${time.value}:00+07:00`)
          ) {
            filterReservationList.value.push(filter);
          }
        }
      }
    }
    // category
    else if (
      fCategory.value !== "" &&
      fStatus.value == "" &&
      fStartDate.value == ""
    ) {
      for (let filter of eventList.value) {
        if (filter.categoryName == fCategory.value) {
          filterReservationList.value.push(filter);
        }
      }
    }
    // start date and status
    else if (
      fStatus.value !== "" &&
      fStartDate.value !== "" &&
      fCategory.value == ""
    ) {
      for (let filter of eventList.value) {
        if (fStatus.value == "upcoming") {
          if (
            Date.parse(filter.eventStartTime) >=
              Date.parse(`${fStartDate.value}T00:00:00+07:00`) &&
            Date.parse(filter.eventStartTime) <=
              Date.parse(`${fStartDate.value}T23:59:00+07:00`) &&
            Date.parse(filter.eventStartTime) >
              Date.parse(`${date.value}T${time.value}:00+07:00`)
          ) {
            filterReservationList.value.push(filter);
          }
        } else if (fStatus.value == "past") {
          if (
            Date.parse(filter.eventStartTime) >=
              Date.parse(`${fStartDate.value}T00:00:00+07:00`) &&
            Date.parse(filter.eventStartTime) <=
              Date.parse(`${fStartDate.value}T23:59:00+07:00`) &&
            Date.parse(filter.eventStartTime) <=
              Date.parse(`${date.value}T${time.value}:00+07:00`)
          ) {
            filterReservationList.value.push(filter);
          }
        }
      }
    }
    // status and category
    else if (
      fStatus.value !== "" &&
      fStartDate.value == "" &&
      fCategory.value !== ""
    ) {
      for (let filter of eventList.value) {
        if (fStatus.value == "upcoming") {
          if (
            filter.categoryName == fCategory.value &&
            Date.parse(filter.eventStartTime) >
              Date.parse(`${date.value}T${time.value}:00+07:00`)
          ) {
            filterReservationList.value.push(filter);
          }
        } else if (fStatus.value == "past") {
          if (
            filter.categoryName == fCategory.value &&
            Date.parse(filter.eventStartTime) <=
              Date.parse(`${date.value}T${time.value}:00+07:00`)
          ) {
            filterReservationList.value.push(filter);
          }
        }
      }
    }
    // start date and category
    else if (
      fStatus.value == "" &&
      fStartDate.value !== "" &&
      fCategory.value !== ""
    ) {
      for (let filter of eventList.value) {
        if (
          filter.categoryName == fCategory.value &&
          Date.parse(filter.eventStartTime) >=
            Date.parse(`${fStartDate.value}T00:00:00+07:00`) &&
          Date.parse(filter.eventStartTime) <=
            Date.parse(`${fStartDate.value}T23:59:00+07:00`)
        ) {
          filterReservationList.value.push(filter);
        }
      }
    }
    // start date ,status, category
    else if (
      fStatus.value !== "" &&
      fStartDate.value !== "" &&
      fCategory.value !== ""
    ) {
      for (let filter of eventList.value) {
        if (fStatus.value == "upcoming") {
          if (
            filter.categoryName == fCategory.value &&
            Date.parse(filter.eventStartTime) >=
              Date.parse(`${fStartDate.value}T00:00:00+07:00`) &&
            Date.parse(filter.eventStartTime) <=
              Date.parse(`${fStartDate.value}T23:59:00+07:00`) &&
            Date.parse(filter.eventStartTime) >
              Date.parse(`${date.value}T${time.value}:00+07:00`)
          ) {
            filterReservationList.value.push(filter);
          }
        } else if (fStatus.value == "past") {
          if (
            filter.categoryName == fCategory.value &&
            Date.parse(filter.eventStartTime) >=
              Date.parse(`${fStartDate.value}T00:00:00+07:00`) &&
            Date.parse(filter.eventStartTime) <=
              Date.parse(`${fStartDate.value}T23:59:00+07:00`) &&
            Date.parse(filter.eventStartTime) <=
              Date.parse(`${date.value}T${time.value}:00+07:00`)
          ) {
            filterReservationList.value.push(filter);
          }
        }
      }
    }
  }
};

// reset filter
const reset = () => {
  fStartDate.value = "";
  fStatus.value = "";
  fCategory.value = "";
  filterReservationList.value = eventList.value;
};
</script>

<template>
<body>
  <h1 class="text-center" style="margin-top: 30px;">Reservation List</h1>
    <div class="row" style="margin-top: 15px;margin-bottom: 15px;">
        <div class="col align-items-end">
            <nav class="navbar navbar-light navbar-expand-md flex-fill align-items-center" style="background: #e0e4f8;padding-left: 0px;padding-right: 0px;">
                <div class="container-fluid">
                  
                  <button data-bs-toggle="collapse" class="navbar-toggler text-dark" data-bs-target="#navcol-1">
                    <span class="visually-hidden"></span>
                    <span class="navbar-toggler-icon"></span>
                  </button>

                  <!-- filter menu -->
                    <div class="collapse navbar-collapse fw-semibold" id="navcol-1" style="margin-left: 30px;margin-right: 30px;">
                        <strong class="flex-fill" style="margin-left: 30px;">Filter Booking :&nbsp;</strong>
                        <ul class="navbar-nav justify-content-end align-items-start" style="padding-left: 0px; margin-right: 10px; margin-bottom: 10px;">
                            <!-- status -->
                            <li class="nav-item" style="margin-left: 10px;">
                              <a class="nav-link active fw-semibold link-dark m-auto" href="#" style="width: auto;padding-bottom: 0px;padding-top: 0px;padding-left: 0px;padding-right: 0px;">Status :</a>
                              <select v-model="fStatus">
                                    <optgroup label="Select Status">
                                        <option value="" selected="">All</option>
                                        <option value="upcoming">Upcoming</option>
                                        <option value="past">Past</option>
                                    </optgroup>
                              </select>
                            </li>
                            
                            <!-- category -->
                            <li class="nav-item" style="margin-left: 10px;">
                              <a class="nav-link active fw-semibold link-dark m-auto" href="#" style="width: auto;padding-bottom: 0px;padding-top: 0px;padding-left: 0px;padding-right: 0px;">Category :</a>
                              <select v-model="fCategory">
                                    <optgroup label="Select Category">
                                        <option value="" selected="">All</option>
                                        <option v-for="cat in categoryList" :key="cat.id" :value="cat.eventCategoryName">
                                            {{ cat.eventCategoryName }}
                                        </option>
                                    </optgroup>
                              </select>
                            </li>
                            <li class="nav-item" style="margin-left: 10px;">
                              <a class="nav-link fw-semibold link-dark m-auto" href="#" style="width: auto;padding-bottom: 0px;padding-top: 0px;padding-left: 0px;padding-right: 0px;">Start date : &nbsp;</a>
                              <input class="form-control-sm" type="date" v-model="fStartDate">
                            </li>
                        </ul>
                    <button class="btn btn-dark my-auto" type="button" style="margin-right: 20px;" @click="search">Search</button>
                    <button class="btn btn-light my-auto" type="reset" style="margin-right: 10px;" @click="reset">Reset</button>
                    </div>
                </div>
            </nav>
        </div>
    </div>
    <div v-if="filterReservationList.length !== 0" class="table-responsive text-center" style="margin-top: 30px;padding-left: 40px;padding-right: 40px;">
        <table class="table">
            <thead>
                <tr>
                    <th v-if="userRole!=='guest'">NAME</th>
                    <th>START DATE</th>
                    <th>DURATION</th>
                    <th>CATEGORY</th>
                    <th v-if="userRole!=='guest'">MORE DETAIL</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="Booking in filterReservationList"
                    :key="Booking.id"
                    class="text-center border-y border-t-0 dark:border-gray-700"
                >
                    <td v-if="userRole!=='guest'"> {{ Booking.bookingName }} </td>
                    <td> <h6>{{ Booking.eventStartTime.substring(11, 16) }} </h6> {{ Booking.eventStartTime.substring(0, 10) }}</td>
                    <td> {{ Booking.eventDuration }} Min. </td>
                    <td> {{ Booking.categoryName }} </td>
                    <td v-if="userRole!=='guest'"><button  class="btn btn-primary" type="button" @click="goReservation(Booking)"> Detail</button>&nbsp;</td>
                </tr>
                <tr></tr>
            </tbody>
        </table>
    </div>

    <!-- ------------------------------------------------------------------------------------------------------ -->
    
    <!-- for alert -->
    <div class="alert-area">
      <div v-if="noInputFilter == true" class="alert info text-sm">
        <span class="closebtn" @click="noInputFilter = undefined">x</span>
        <strong class="block">Info!</strong> Please input information to filter
      </div>

      <div
        v-else-if="
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
/* slice bar */
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

/* filter button */
.custom-btn {
  width: 130px;
  height: 40px;
  padding: 10px 25px;

  font-family: "Lato", sans-serif;
  font-weight: 500;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  display: inline-block;
}

/* search */
.search {
  box-shadow: -7px -7px 20px 0px #00000011, -4px -4px 5px 0px #00000011,
    7px 7px 20px 0px #00000011, 4px 4px 5px 0px #00000011;
}
.search:hover {
  transition: all 0.2s ease-in-out;
  background-color: rgb(58, 58, 58);
  color: white;
}

/* reset */
.reset {
  box-shadow: -7px -7px 20px 0px #00000011, -4px -4px 5px 0px #00000011,
    7px 7px 20px 0px #00000011, 4px 4px 5px 0px #00000011;
}
.reset:hover {
  transition: all 0.2s ease-in-out;
  background-color: rgb(90, 26, 26);
  color: white;
}
/* alert */
.alert-area {
  position: fixed;
  top: 100px;
  right: 0;
  background-color: transparent;
  width: 23%;
  height: 10%;
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
