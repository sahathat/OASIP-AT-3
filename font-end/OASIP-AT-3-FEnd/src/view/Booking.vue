<script setup>
import { computed } from "@vue/reactivity";
import { onBeforeMount, onUpdated, ref } from "vue";
import { useRouter } from "vue-router";

const name = ref("");
const eMail = ref("");
const startDate = ref("");
const category = ref("");
const startTime = ref("");
const noteText = ref("");
const cateId = ref("");

const nameLength = 100;
const emailLength = 100;
const noteLength = 500;

const db = "http://localhost:5000/booking";
const eventLink = `${import.meta.env.BASE_URL}api/events`;
const categoryLink = `${import.meta.env.BASE_URL}api/categories`;
// const eventLink = "http://localhost:8443/api/events";
// const categoryLink = "http://localhost:8443/api/categories";

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

const key = localStorage.getItem('key')

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
const submitt = () => {
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
  // console.log(key)

  const res = await fetch(eventLink, {
    method: "POST",
    headers: {
            "Authorization":'Bearer ' + key ,
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
  if (res.status === 201) {
    addSuccess.value = true;
    createStatus = true;
    isStatus.value = true;
    setTimeout(() => (addSuccess.value = false), 5000);
  } else if (res.status === 400) {
    validateBetweenDate.value = true;
    isStatus.value = false;
  } else {
    createStatus = false;
    isStatus.value = false;
  }
  return createStatus;
};

//GET category

// first get Category
const getCategory = async () => {
  key = localStorage.getItem('key')
  const res = await fetch(categoryLink,{
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
  } else {
    getStatus.value = false;
  }
};

// get event
const resGetEvent = ref(undefined);
//const countGetEvent=ref(0)

// check every 10 second
setInterval(async () => {
  key = localStorage.getItem('key')
  resGetEvent.value = await fetch(eventLink,{
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
    }
  });
  if (resGetEvent.value.status === 200) {
    eventList.value = await resGetEvent.value.json();
    getStatus.value = true;
  } else getStatus.value = false;
}, 10000);

// first get event
const getEvent = async () => {
  key = localStorage.getItem('key')
  const res = await fetch(eventLink,{
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
    }
  });
  if (res.status === 200) {
    eventList.value = await res.json();
    getStatus.value = true;
  } else getStatus.value = false;
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

onBeforeMount(async () => {
  await getCategory();
  await getEvent();
});
</script>

<template>
  <div class="showUp container mx-auto">
    <div
      class="max-w-screen-md p-5 pb-7 mx-auto mt-14 bg-gray-200 rounded-md shadow-sm shadow-xl"
    >
      <div class="text-center">
        <h1 class="my-3 text-3xl font-semibold text-gray-700">Booking</h1>
        <p class="text-gray-400">
          Fill up the form below to send a online appointment.
        </p>
      </div>
      <div>
        <!-- name -->
        <div class="my-3 inline-flex px-4 w-full">
          <div class="inline-block m-auto">
            <div class="px-3 w-full">
              <label for="name" class="font-medium m-auto text-sm text-gray-600"
                >Full Name</label
              >
              <span
                class="text-gray-300 font-medium ml-1 text-sm"
                :style="[name.length > nameLength ? 'color:red' : '']"
              >
                {{ name.length }}/{{ nameLength }} charecters
              </span>
            </div>
            <div>
              <input
                v-model="name"
                type="text"
                name="name"
                placeholder="Somchai Jaidee (AT-3)"
                required
                :style="[
                  validateNameisNotNull == false ? 'border-color:red' : '',
                ]"
                class="w-80 px-3 py-2 mx-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              />
            </div>
          </div>

          <!-- email -->
          <div class="mx-4 inline-block m-auto">
            <div class="px-3 w-full">
              <label for="email" class="font-medium text-sm text-gray-600"
                >Email Address</label
              >
              <span
                class="text-gray-300 font-medium ml-1 text-sm"
                :style="[eMail.length > emailLength ? 'color:red' : '']"
              >
                {{ eMail.length }}/{{ emailLength }} charecters
              </span>
            </div>
            <div>
              <input
                v-model="eMail"
                type="email"
                name="email"
                placeholder="somchai.jai@mail.kmutt.ac.th"
                required
                :style="[
                  validateEmailisNotNull == false ? 'border-color:red' : '',
                ]"
                class="w-80 px-3 py-2 mx-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              />
            </div>
          </div>
        </div>

        <!-- category -->
        <div class="my-3 px-6 inline-block">
          <div class="inline-block">
            <div class="px-3 w-full">
              <label for="category" class="text-sm font-medium text-gray-600"
                >Category</label
              >
              <!-- show category detail -->
              <a
                href="#category-detail"
                class="px-1.5 font-light mx-2 bg-white rounded-full text-xs bg-black text-white"
                >?</a
              >
            </div>
            <!-- select category -->
            <div>
              <select
                id="category"
                :style="[
                  validateCategoryisNotNull == false ? 'border-color:red' : '',
                ]"
                class="text-ellipsis overflow-hidden cursor-pointer w-64 font-medium px-3 py-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
                v-model="category"
              >
                <option value="" disable selected>select your category</option>
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
        </div>

        <!-- date & time -->
        <div class="inline-block px-3 w-max">
          <!-- start date -->
          <div class="w-fit inline-block">
            <div class="px-3 w-full m-auto">
              <label for="date" class="font-medium mx-2 text-sm text-gray-600"
                >Start date</label
              >
            </div>
            <div class="px-3 w-full m-auto">
              <input
                id="date"
                :min="minDate"
                :style="[
                  validateStartDateisNotNull == false ? 'border-color:red' : '',
                ]"
                class="px-3 py-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
                type="date"
                v-model="startDate"
              />
            </div>
          </div>

          <!-- start time -->
          <div class="w-fit inline-block">
            <div class="pr-2 w-full">
              <div class="inline-block font-medium text-sm text-gray-600">
                Start time
              </div>
              <div class="w-full">
                <input
                  :style="[
                    validateStartTimeisNotNull == false
                      ? 'border-color:red'
                      : '',
                  ]"
                  class="px-3 py-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
                  type="time"
                  v-model="startTime"
                />
              </div>
            </div>
          </div>

          <!-- duration -->
          <div class="w-fit inline-block">
            <div class="pr-2 w-full">
              <label
                class="font-medium pr-2 focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
                for="time"
                >Duration
              </label>
            </div>
            <div class="pr-3 w-full">
              <input
                disabled
                class="w-14 px-3 py-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
                type="text"
                v-model="durationTime"
                min="00:00"
                max="03:00"
              />
              <span class="pl-1.5">Min.</span>
            </div>
          </div>
        </div>

        <!-- note & button-->
        <div class="inline-flex w-full px-4">
          <!-- note -->
          <div class="w-3/5 block m-auto">
            <label for="textA" class="font-medium text-sm text-gray-600"
              >Your Message
              <span
                class="text-gray-300"
                :style="[noteText.length > noteLength ? 'color:red' : '']"
                >{{ noteText.length }}/{{ noteLength }} charecters
              </span>
            </label>
            <textarea
              :style="[noteText.length > noteLength ? 'border-color:red' : '']"
              id="textA"
              name="textA"
              rows="4"
              cols="50"
              class="w-full block px-3 py-2 placeholder-gray-300 border border-gray-400 resize-none rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              v-model="noteText"
            >
            </textarea>
          </div>

          <!-- booking button -->
          <div class="inline-flex m-auto p-5 w-60">
            <a
              href="#submit"
              class="font-bold text-gray-900 hover:text-white border border-gray-800 hover:bg-gray-900 focus:ring-4 focus:outline-none focus:ring-gray-300 rounded-lg text-sm text-center dark:border-gray-600 dark:text-gray-400 dark:hover:text-white dark:hover:bg-gray-600 dark:focus:ring-gray-800 m-auto p-5"
            >
              Submit !
            </a>
          </div>
        </div>
      </div>

      <!-- for submit  -->
      <div id="submit" class="overlay">
        <div class="popup2 h-96">
          <h2 class="mb-5 text-xl font-bold bg-white mx-auto w-fit">
            Are you sure ?
          </h2>

          <div class="option flex m-auto w-full mt-10">
            <a
              @click="submitt"
              href="#"
              class="w-full text-center p-2 px-2 bg-gray-200 hover:bg-green-500 font-bold hover:text-white"
              >Yes</a
            >
            <a
              href="#"
              class="w-full text-center p-2 px-2 bg-gray-200 hover:bg-rose-500 font-bold hover:text-white"
              >No</a
            >
          </div>
        </div>
      </div>

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

        <div v-if="isStatus == false" class="alert warning text-sm">
          <span class="closebtn" @click="isStatus = true">x</span>
          <strong class="block">Warning!</strong> Can't create booking.
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
    </div>
  </div>

  <!-- for show category detail  -->
  <div id="category-detail" class="overlay">
    <div class="popup h-96 overflow-auto">
      <h2 class="text-xl font-semibold text-gray-100 p-6">Category detail :</h2>
      <a class="close" href="#">&times;</a>

      <div v-if="categoryList.length == 0" class="w-fit m-auto my-32 text-lg">
        No category details
      </div>

      <div v-else v-for="(cat,index) in categoryList" :key="index">
        <ul>
          <li class="text-left">
            <br />
            <span class="block font-semibold text-base"
              >&emsp;{{ cat.eventCategoryName }}</span
            >
            <span class="block"
              >&emsp;&emsp;-->
              <span v-if="cat.eventCategoryDescription == null">
                Not specified
              </span>
              <span v-else>
                {{ cat.eventCategoryDescription }}
              </span>
            </span>
          </li>
        </ul>
      </div>
    </div>
  </div>
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
