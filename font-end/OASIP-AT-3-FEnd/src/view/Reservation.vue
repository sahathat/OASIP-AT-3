<script setup>
import { useRoute, useRouter } from "vue-router";
import { onBeforeMount, ref } from "vue";

const { params } = useRoute();

const db = "http://localhost:5000/booking";
const eventLink = `${import.meta.env.BASE_URL}api/events`;

const id = params.id;
const name = ref("");
const eMail = ref("");
const category = ref("");
const startDate = ref("");
const startTime = ref("");
const duration = ref("");
const noteT = ref("");
const detailBooking = ref({});
const eventList=ref([])
const isNotNull = ref(false);
const myRouoter = useRouter();
const goReservation = () => myRouoter.push({ name: "ReservationList" });

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

// get every 10 sec
const getStatus=ref(undefined)
const resGetEvent=ref(undefined)

setInterval(async ()=>{
  resGetEvent.value= await fetch(eventLink)
  if (resGetEvent.value.status === 200) {
    eventList.value = await resGetEvent.value.json();
    getStatus.value = true;
  } else getStatus.value = false;
},10000)

// first get event
const getEvent =async()=>{
 resGetEvent.value= await fetch(eventLink)
  if (resGetEvent.value.status === 200) {
    eventList.value = await resGetEvent.value.json();
    getStatus.value = true;
  } else getStatus.value = false;       
}

// get value
const getDetail = async () => {
  const res = await fetch(`${eventLink}/${params.id}`);
  if (res.status === 200) {
    detailBooking.value = await res.json();
    //console.log(detailBooking.value);

    //console.log(Date.parse("2022-06-01T15:00:00+07:00"));
    if (detailBooking.value.id == id) {
      isNotNull.value = true;
      name.value = detailBooking.value.bookingName;
      eMail.value = detailBooking.value.bookingEmail;
      category.value = detailBooking.value.categoryName;
      startDate.value = detailBooking.value.eventStartTime.substring(0, 10);
      startTime.value = detailBooking.value.eventStartTime.substring(11, 16);
      duration.value = detailBooking.value.eventDuration;
      noteT.value = detailBooking.value.eventNotes;
    }
  }
};

onBeforeMount(async()=>{
       await  getEvent()
       await getDetail()
});

//remove information
const removeInfo = async () => {
  const res = await fetch(`${eventLink}/${id}`, { method: "DELETE" });
  if (res.status === 200) {
    console.log("delete successfully");
    goReservation();
  } else console.log("error");
};

// assign to edit attribute
const isEdit = ref(false);
const editStartTime = ref("");
const editStartDate = ref("");
const editNote = ref("");

const editInfo = () => {
  isEdit.value = true;
  editStartTime.value = startTime.value;
  editStartDate.value = startDate.value;
  editNote.value = noteT.value;
};

const cancel = () => {
  isEdit.value = false;
  editStartTime.value = "";
  editStartDate.value = "";
};

const edit =async()=>{
       let canEdit=undefined
        const res = await fetch(`${eventLink}/${id}`, {
        method: "PUT",
        headers: {
          "content-type": "application/json",
        },
        body: JSON.stringify({
          eventStartTime: `${editStartDate.value}T${editStartTime.value}:00+07:00`,
          eventNotes: editNote.value,
        }),
      });
      if (res.status == 200) {
        let editDetailNote = await res.json();
        startDate.value = editDetailNote.eventStartTime.substring(0, 10);
        startTime.value = editDetailNote.eventStartTime.substring(11, 16);
        noteT.value = editDetailNote.eventNotes;
        isEdit.value = false;
        canEdit=true
        editSuccess.value=true
       }else {
              canEdit=false
              editSuccess.value=false
       }
       return canEdit
}

// submit
const isInput=ref(undefined)
const isPast =ref(undefined)
const isOverlap =ref(undefined)
const editSuccess=ref(undefined)

const submitt = async () => {
       isInput.value=undefined
       isPast.value=undefined
       isOverlap.value=undefined
       editSuccess.value=undefined
  if (editStartDate.value !== "" && editStartTime.value !== "") {
    if (//สำหรับเลือกวันในอนาคต
      Date.parse(`${editStartDate.value}T${editStartTime.value}:00+07:00`) >
      Date.parse(`${date.value}T${time.value}:00+07:00`)
       ) {
           if(overlap()){
              isOverlap.value=true
      } else if(edit()){
             cancel()
             setTimeout(()=>editSuccess.value=false,5000)
      }
    } else isPast.value=true//alert(`Can't select past date and time `);
  }else isInput.value=false
};

// check overlap
const betweenDateWarning=ref(undefined)

const overlap = () => {
  betweenDateWarning.value = undefined;
  let isOverlap = undefined;
for (let check of eventList.value) {
    if (check.categoryName == category.value&&check.id!==id) {
//       console.log(Date.parse(`${editStartDate.value}T${editStartTime.value}:00+07:00`))
//       console.log(Date.parse(check.eventStartTime))
//       console.log(Date.parse(`${check.eventStartTime.substring(0,10)}T${calTime(parseInt(check.eventStartTime.substring(11,13)) ,parseInt(check.eventStartTime.substring(14,16)),check.eventDuration)}:00+07:00`))
//       console.log('cut')

      if (
        Date.parse(`${editStartDate.value}T${editStartTime.value}:00+07:00`) >=
        Date.parse(check.eventStartTime) &&
        Date.parse(`${editStartDate.value}T${editStartTime.value}:00+07:00`) <=
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
            parseInt(editStartTime.value.substring(0, 2)),
            parseInt(editStartTime.value.substring(3, 5)),
            duration.value
          )}:00+07:00`
        ) >= Date.parse(check.eventStartTime) &&
        Date.parse(
          `${startDate.value}T${calTime(
            parseInt(editStartTime.value.substring(0, 2)),
            parseInt(editStartTime.value.substring(3, 5)),
            duration.value
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


// function
const calTime = (hour, minute, addTime) => {
  let sum = undefined;
  let h = hour;
  let m = minute + addTime;
  // console.log(`minute + add time :${m}`)
  // console.log(`this is minute : ${minute} and this addTime : ${addTime}`)
  // console.log(`this is hour : ${h}`)
  if (m >= 60) {
    h = h + 1;
    m = m-60;
  }
  h = h < 10 ? `0${h}` : h;
  m = m < 10 ? `0${m}` : m;

  sum = `${h}:${m}`;
  // console.log(sum)
  return sum;
};
</script>

<template>
  <div
    class="showUp w-3/5 p-5 pb-7 mx-auto mt-10 bg-white rounded-md shadow-xl"
  >
    <!-- no data -->
    <div v-if="isNotNull == false">
      <h2 class="text-center mb-1 font-bold text-xl">No date</h2>
      <div class="w-fit m-auto">
        <button @click="goReservation" class="custom-btn back block">Go Back</button>
      </div>
    </div>

    <!-- have data -->
    <div
      v-else-if="isNotNull == true"
      class="p-4 border-double border-4 border-neutral-300 max-w-screen-lg"
    >
      <div class="mx-2 w-full">
        <h1 class="text-center mb-1 font-bold text-2xl">Reservation</h1>
        <h3 class="text-center">----------------</h3>
      </div>
      <div class="flex m-auto my-4 w-full">
        <!-- Name -->
        <div class="px-2 w-1/2 inline-flex">
          <div class="pr-2 font-semibold flex m-auto text-gray-400">Name :</div>
          <div
            class="overflow-hidden overflow-x-scroll border-2 rounded-md p-1.5 pt-2.5 font-normal bg-white flex w-3/4 h-12"
          >
            {{ name }}
          </div>
        </div>

        <!-- E-mail -->
        <div class="px-2 w-1/2 inline-flex">
          <div class="pr-2 font-semibold flex m-auto text-gray-400">
            E-mail :
          </div>
          <div
            class="overflow-hidden overflow-x-scroll border-2 rounded-md p-1.5 pt-2.5 font-normal bg-white flex w-3/4 h-12"
          >
            {{ eMail }}
          </div>
        </div>
      </div>
      <!-- start date ,time and duration -->
      <div class="flex my-4 w-full">
        <div class="px-1 w-fit block">
          <div class="p-3 font-semibold inline-block m-auto text-gray-400">
            Start date :
          </div>
          <div
            v-if="isEdit == false"
            class="border-2 rounded-md p-1.5 font-normal bg-white inline-block w-fit h-10"
          >
            {{ startDate }}
          </div>
          <div
            v-if="isEdit == true"
            class="eidt-color showUp border-2 rounded-md p-1.5 font-normal bg-white inline-block w-fit h-10"
          >
            <input type="date" :min="date" v-model="editStartDate" />
          </div>
        </div>
        <div class="px-1 w-fit block">
          <div class="p-3 font-semibold inline-block m-auto text-gray-400">
            Start time :
          </div>
          <div
            v-if="isEdit == false"
            class="text-black border-2 rounded-md p-1.5 font-normal bg-white inline-block w-fit h-10"
          >
            {{ startTime }}
          </div>
          <div
            v-if="isEdit == true"
            class="eidt-color showUp border-2 rounded-md p-1.5 font-normal bg-white inline-block w-fit h-10"
          >
            <input type="time" v-model="editStartTime" />
          </div>
        </div>
        <div class="px-1 w-fit block">
          <div class="p-3 font-semibold inline-block m-auto text-gray-400">
            Duration :
          </div>
          <div
            class="border-2 text-black rounded-md p-1.5 font-normal bg-white inline-block w-max h-10"
          >
            {{ duration }}
          </div>
          <span class="p-3">minute</span>
        </div>
      </div>
      <!-- category -->
      <div class="px-2 font-semibold block w-fit">
        <div class="px-1 w-fit inline-flex">
          <div class="pr-2 font-semibold inline-block m-auto text-gray-400">
            Category :
          </div>
          <div
            class="text-ellipsis overflow-hidden border-2 text-black rounded-md p-1.5 font-normal bg-white inline-block w-fit"
          >
            {{ category }}
          </div>
        </div>
      </div>
      <!-- note -->
      <div class="ml-2 flex my-4 w-full">
        <div
          v-if="(noteT !== null && noteT !== '') || isEdit == true"
          class="inline-block"
        >
          <div class="px-2 font-semibold w-fit text-gray-400">Note :</div>
          <div class="ml-5 w-fit">
            <textarea
              readonly
              v-if="isEdit == false"
              rows="4"
              cols="50"
              class="border-2 text-black rounded-md block px-3 py-2 placeholder-gray-300 border resize-none rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              v-model="noteT"
            >
            </textarea>
            <textarea
              rows="4"
              cols="50"
              v-if="isEdit == true"
              class="eidt-color showUp border-2 text-black rounded-md block px-3 py-2 placeholder-gray-300 border resize-none rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              v-model="editNote"
            >
            </textarea>
          </div>
        </div>
       <!-- button not edit mode -->
        <div v-if="isEdit == false" class="showUp m-auto w-fit">
          <button @click="editInfo" class="m-4 custom-btn edit">Edit</button>
          <a  href="#remove" class="m-4 custom-btn remove">
            Remove
          </a>
          <button @click="goReservation"  class="m-4 custom-btn back">Go Back</button>
        </div>
       <!-- button edit mode -->
        <div v-if="isEdit == true" class="showUp m-auto w-fit">
          <button @click="cancel" class="m-4 custom-btn remove">
            Cancel
          </button>
          <a href="#submit" class="m-4 custom-btn edit">Submit</a>
        </div>
      </div>
    </div>
  </div>

  <!-- for alert -->
  <div class="alert-area">
    <div v-if="isPast == true" class="alert warning text-sm">
      <span class="closebtn" @click="isPast = undefined">x</span>
      <strong class="block">Error!</strong> Can't select past date and time.
    </div>

    <div v-if="isInput == true" class="alert  text-sm">
      <span class="closebtn" @click="isInput = undefined">x</span>
      <strong class="block">Error!</strong> Please input information.
    </div>
    
    <div v-if="
          getStatus == false ||
          eventList.length == 0
        " class="alert warning text-sm">
          <strong class="block">Warning!</strong> A system error has occurred,please try again.
     </div>

       <div v-if="isOverlap== true" class="alert warning text-sm">
          <span class="closebtn" @click="isOverlap = undefined">x</span>
          <strong class="block">Warning!</strong> {{ betweenDateWarning }}
       </div>

       <div v-if="editSuccess == true" class="alert success text-sm">
          <span class="closebtn" @click="editSuccess = false">x</span>
          <strong class="block">Success!</strong> Edit data success.
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

           <!-- for remove  -->
  <div id="remove" class="overlay">
    <div class="popup2 h-96">
      <h2 class="mb-5 text-xl font-bold bg-white mx-auto w-fit">
        Are you sure(remove) ?
      </h2>

      <div class="option flex m-auto w-full mt-10">
        <a
          @click="removeInfo"
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
</template>

<style scoped>
.custom-btn {
  width: 100px;
  height: 40px;
  padding: 10px 25px;
  font-family: "Lato", sans-serif;
  font-weight: 500;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  display: inline-block;
  text-align: center;
}
/* back*/
.back {
  background: rgb(115, 115, 115);
  color: #fff;
  line-height: 42px;
  padding: 0;
  border: none;
}
.back:hover {
  background: transparent;
  color: #000;
  box-shadow: -7px -7px 20px 0px #fff9, -4px -4px 5px 0px #fff9,
    7px 7px 20px 0px #0002, 4px 4px 5px 0px #0001;
}
.back:before,
.back:after {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  height: 2px;
  width: 0;
  background: #000;
  transition: 400ms ease all;
}
.back:after {
  right: inherit;
  top: inherit;
  left: 0;
  bottom: 0;
}
.back:hover:before,
.back:hover:after {
  width: 100%;
  transition: 800ms ease all;
}

/* edit */
.edit {
  background: rgb(166, 166, 166);
  color: #fff;
  line-height: 42px;
  padding: 0;
  border: none;
}
.edit:hover {
  background: rgb(252, 140, 252);
  color: #000;
  box-shadow: -7px -7px 20px 0px #fff9, -4px -4px 5px 0px #fff9,
    7px 7px 20px 0px #0002, 4px 4px 5px 0px #0001;
}
.edit:before,
.edit:after {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  height: 2px;
  width: 0;
  background: #000;
  transition: 400ms ease all;
}
.edit:after {
  right: inherit;
  top: inherit;
  left: 0;
  bottom: 0;
}
.edit:hover:before,
.edit:hover:after {
  width: 100%;
  transition: 800ms ease all;
}
.eidt-color {
  border-color: rgb(252, 140, 252);
}

/* remove */
.remove {
  background: rgb(166, 166, 166);
  color: #fff;
  line-height: 42px;
  padding: 0;
  border: none;
}
.remove:hover {
  background: rgb(255, 172, 172);
  color: #000;
  box-shadow: -7px -7px 20px 0px #fff9, -4px -4px 5px 0px #fff9,
    7px 7px 20px 0px #0002, 4px 4px 5px 0px #0001;
}
.remove:before,
.remove:after {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  height: 2px;
  width: 0;
  background: #000;
  transition: 400ms ease all;
}
.remove:after {
  right: inherit;
  top: inherit;
  left: 0;
  bottom: 0;
}
.remove:hover:before,
.remove:hover:after {
  width: 100%;
  transition: 800ms ease all;
}

/* scrol bar */

/* width */
::-webkit-scrollbar {
  height: 7.5px;
  width: 9px;
}

/* Track */
::-webkit-scrollbar-track {
  box-shadow: inset 0 0 5px transparent;
  border-radius: 10px;
}

/* Handle */
::-webkit-scrollbar-thumb {
  background: rgb(109, 109, 112);
  border-radius: 10px;
}

/* Handle on hover */
::-webkit-scrollbar-thumb:hover {
  background: #577fbb;
}

/* submit */
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
  top: 170px;
  right: 0;
  background-color: transparent;
  width: 18%;
  
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
  background-color: #ac00d7;
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
