<script setup>
import { computed, onBeforeUpdate } from "vue";
import { ref, onBeforeMount } from "vue";
import { useRouter } from "vue-router";
const eventList = ref([]);
const categoryList = ref([]);
const categoryCheck = ref(false);
const filterReservationList = ref([]);

const db = "http://localhost:5000/booking";
const eventLink = `${import.meta.env.BASE_URL}api/events`;
const categoryLink = `${import.meta.env.BASE_URL}api/categories`;
// const eventLink = "http://ip21at3.sit.kmutt.ac.th:8081/api/events";
// const categoryLink = "http://ip21at3.sit.kmutt.ac.th:8081/api/categories";

//GET event
const getStatus = ref(undefined);
const resGetEvent = ref(undefined);
// get every 10 sec
setInterval(async () => {
  getStatus.value = undefined;
  resGetEvent.value = await fetch(eventLink);
  if (resGetEvent.value.status === 200) {
    eventList.value = await resGetEvent.value.json();
    getStatus.value = true;
    filterReservationList.value = eventList.value;
  } else getStatus.value = false;
}, 10000);

// first get event
const getEvent = async () => {
  const res = await fetch(eventLink);
  //const res = await fetch(`${import.meta.env.VITE_BASE_URL}/events?page=0&pageSize=1`)
  if (res.status === 200) {
    eventList.value = await res.json();
    filterReservationList.value = eventList.value;
    //console.log(bookingList.value)
  }
};

//GET category
const getCategory = async () => {
  const res = await fetch(categoryLink);
  if (res.status === 200) {
    categoryList.value = await res.json();
    categoryCheck.value = true;
    //console.log(getCategory.value)
  } else {
    categoryCheck.value = false;
  }
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
  await getEvent();
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
  <!-- for filter -->
  <div
    class="showUp md:inline-block mt-16 bg-gray-200 p-4 ml-20 w-1/3 rounded-l"
    style="height: 475px; width: 25%"
  >
    <div class="border-gray-500 border-4 border-double w-full">
      <h1 class="my-8 text-xl font-semibold text-gray-600 w-fit m-auto">
        Filter Booking
      </h1>

      <!-- start date -->
      <div class="w-full block my-2">
        <div class="px-3 w-full m-auto block">
          <label for="date" class="font-medium text-sm text-gray-600"
            >Start date :</label
          >
        </div>

        <div class="px-3 w-5/6 block">
          <input
            id="date"
            class="w-full drop-shadow-md px-3 py-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
            type="date"
            v-model="fStartDate"
          />
        </div>
      </div>

      <!-- status -->
      <div class="block w-full my-3">
        <div class="px-3 my-1 w-fit block">
          <label for="category" class="font-medium text-sm text-gray-600"
            >Status :</label
          >
        </div>
        <div class="px-1.5 w-5/6 block">
          <select
            id="category"
            class="w-full text-ellipsis overflow-hidden drop-shadow-md cursor-pointer px-3 py-2 mx-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
            v-model="fStatus"
          >
            <option value="" disable selected>All</option>
            <option value="upcoming">Upcoming</option>
            <option value="past">Past</option>
          </select>
        </div>
      </div>

      <!-- category -->
      <div class="block w-full my-3">
        <div class="px-3 my-1 w-fit block">
          <label for="category" class="font-medium text-sm text-gray-600"
            >Category :</label
          >
        </div>
        <div class="px-1.5 w-5/6 block">
          <select
            id="category"
            class="w-full text-ellipsis overflow-hidden drop-shadow-md cursor-pointer px-3 py-2 mx-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
            v-model="fCategory"
          >
            <option value="" disable selected>All</option>
            <option
              v-for="cat in categoryList"
              :key="cat.id"
              :value="cat.eventCategoryName"
            >
              {{ cat.eventCategoryName }}
            </option>
          </select>
        </div>
      </div>

      <!-- search button -->
      <div class="flex my-3 p-5 w-full">
        <div class="inline-flex w-1/2 m-auto">
          <button @click="reset" class="custom-btn reset">Reset</button>
        </div>

        <div class="inline-flex w-1/2 m-auto">
          <button @click="search" class="custom-btn search">Search</button>
        </div>
      </div>
    </div>
  </div>
  <!-- for booking table -->
  <div
    class="showUp bg-gray-200 md:inline-block mr-28 mt-16 p-4 rounded-r"
    style="height: 475px; width: 65%"
  >
    <div v-if="filterReservationList.length === 0">
      <h1 class="drop-shadow-2xl mx-auto w-fit my-20 font-semibold">
        No event
      </h1>
    </div>

    <div
      v-if="filterReservationList.length !== 0"
      class="drop-shadow-2xl bg-white overflow-y-auto mx-auto h-fit"
      style="height: 440px; width: 100%"
    >
      <table class="table-fixed m-auto md:table-flexed w-full">
        <thead
          class="sticky top-0 text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400"
        >
          <tr>
            <th scope="col" class="px-6 py-3">Name</th>
            <th scope="col" class="px-3 py-3">Start date</th>
            <th scope="col" class="px-6 py-3">Duration</th>
            <th scope="col" class="px-6 py-3">Category</th>
            <th scope="col" class="px-6 py-3">more detail</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="Booking in filterReservationList"
            :key="Booking.id"
            class="text-center border-y border-t-0 dark:border-gray-700"
          >
            <th
              scope="row"
              class="px-6 py-4 text-gray-900 font-semibold whitespace-nowrap text-ellipsis overflow-hidden"
            >
              {{ Booking.bookingName }}
            </th>
            <td class="px-1 py-4">
              <div class="block m-auto">
                <span class="px-5 w-full block font-semibold">
                  {{ Booking.eventStartTime.substring(11, 16) }}
                </span>
                <span class="px-5 w-full block font-normal">
                  {{ Booking.eventStartTime.substring(0, 10) }}
                </span>
              </div>
            </td>
            <td class="px-6 py-4">
              <span class="font-semibold">{{ Booking.eventDuration }}</span>
              <span>Min.</span>
            </td>
            <td class="px-6 py-4 text-ellipsis overflow-hidden">
              {{ Booking.categoryName }}
            </td>
            <td class="px-14 py-4">
              <button
                @click="goReservation(Booking)"
                class="text-black hover:text-white border border-gray-800 hover:bg-gray-900 focus:ring-4 focus:outline-none focus:ring-gray-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center mr-2 mb-2 dark:border-gray-600 dark:text-gray-400 dark:hover:text-white dark:hover:bg-gray-600 dark:focus:ring-gray-800"
              >
                Detail
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- for alert -->
    <div class="alert-area">
      <div v-if="noInputFilter == true" class="alert info text-sm">
        <span class="closebtn" @click="noInputFilter = undefined">x</span>
        <strong class="block">Info!</strong> Please input information to filter
      </div>

      <div
        v-else-if="
          getStatus == false ||
          (categoryList.length == 0 && eventList.length == 0)
        "
        class="alert warning text-sm"
      >
        <strong class="block">Warning!</strong> A system error has occurred,
        please try again.
      </div>
    </div>
  </div>
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
