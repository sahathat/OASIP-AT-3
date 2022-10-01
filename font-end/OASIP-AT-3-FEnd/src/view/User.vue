<script setup>
import { useRoute, useRouter } from "vue-router";
import { onBeforeMount, ref , computed} from "vue";

const { params } = useRoute();

const db = "http://localhost:5000/booking";
// const userLink = `${import.meta.env.BASE_URL}api/userList`;
// const refreshLink = `${import.meta.env.BASE_URL}api/users/refresh`;
const userLink = "http://localhost:8443/api/userList";
const refreshLink = "http://localhost:8443/api/users/refresh";

// ขาดเช็คชื่อ-เมล์ซ้ำ
const id = params.id;
const name = ref("");
const email = ref("");
const role = ref("");
const createdOn = ref("");
const updatedOn = ref(undefined);
const userDetail = ref({});
const userList=ref([])

const isNotNull = ref(false);
const myRouoter = useRouter();
const goUserList = () => myRouoter.push({ name: "UserList" });
const goHome = () => myRouoter.push({ name: "Home" });

const roles = ['admin','lecturer','student']

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
// const getStatus=ref(undefined)
// const resGetUser=ref(undefined)
// setInterval(async ()=>{
//   const key = localStorage.getItem('key')
//   resGetUser.value= await fetch(userLink, {
//     method: "GET",
//     headers: {
//             "Authorization":'Bearer ' + key ,
//             "Accept": 'application/json',
//             "content-type": "application/json",
//         }
//   })
//   if (resGetUser.value.status === 200) {
//     userList.value = await resGetUser.value.json();
//     getStatus.value = true;
//   }
//     // refresh Token --------------------------------------- //
//   else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
//     console.log('test...')
//     const resForRefresh = await fetch(refreshLink, {
//       headers: {
//         Authorization: "Bearer " + localStorage.getItem('key'),
//         isRefreshToken: true ,
//       },
//     })
//       const jwt = await resForRefresh.json()
//       console.log(jwt)
//       if(resForRefresh.status === 200){
//         // set localStorage
//         localStorage.setItem('key',jwt.token)
//         localStorage.setItem('token','refreshToken')
//         getUser()
//       }
//     }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
//         localStorage.removeItem('key')
//         localStorage.removeItem('token')
//         goHome()
//         console.log('เข้า')
//     }
//     // ------------------------------------------------- //
//      else getStatus.value = false;
// },10000)

//GET users
const getUser = async () => {
  const key = localStorage.getItem('key')

  const res = await fetch(userLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    userList.value = await res.json();
  } 
  // refresh Token --------------------------------------- //
  else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
    console.log('test...')
    const resForRefresh = await fetch(refreshLink, {
      headers: {
        Authorization: "Bearer " + localStorage.getItem('key'),
        isRefreshToken: true ,
      },
    })
      const jwt = await resForRefresh.json()
      console.log(jwt)
      if(resForRefresh.status === 200){
        // set localStorage
        localStorage.setItem('key',jwt.token)
        localStorage.setItem('token','refreshToken')
        getUser()
      }
    }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        console.log('เข้า')
    }
    // ------------------------------------------------- //
};

// get value
const getDetail = async () => {
  const key = localStorage.getItem('key')
  // console.log(key)
  const res = await fetch(`${userLink}/${params.id}`, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    userDetail.value = await res.json();
    //console.log(userDetail.value);
    if (userDetail.value.id == id) {
      isNotNull.value = true;
      name.value = userDetail.value.name;
      email.value = userDetail.value.email;
      role.value = userDetail.value.role
      createdOn.value = userDetail.value.createdOn;
      updatedOn.value = userDetail.value.updatedOn;
      console.log(userDetail.value)
    }
  }
  // refresh token ----------------------------- //
    else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
    console.log('test...')
    const resForRefresh = await fetch(refreshLink, {
      headers: {
        Authorization: "Bearer " + localStorage.getItem('key'),
        isRefreshToken: true ,
      },
    })
      const jwt = await resForRefresh.json()
      console.log(jwt)
      if(resForRefresh.status === 200){
        // set localStorage
        localStorage.setItem('key',jwt.token)
        localStorage.setItem('token','refreshToken')
        getUser()
      }
    }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        console.log('เข้า')
    }
    // ------------------------------------------------- //
};

//format date
const formatDate = (datetime) => { 
  const date = new Date(datetime)
  const month = date.getMonth() + 1
  if(month<10){ return `${date.getDate()}-0${month}-${date.getFullYear()}` }
  else{ return `${date.getDate()}-${month}-${date.getFullYear()}` }
}


//format time
const formatTime = (datetime) => { 
  const time = new Date(datetime)
  const hour = computed(() => {
    // console.log(time.getHours());
    if (time.getHours() < 10) return "0" + time.getHours();
    else return time.getHours();
  });
  const minute = computed(() => {
    // console.log(time.getMinutes());
    if (time.getMinutes() < 10) return "0" + time.getMinutes();
    else return time.getMinutes();
  });
  return `${hour.value}.${minute.value}` 
}


onBeforeMount(async()=>{
       await getUser()
       await getDetail()
});

//remove information
const removeUser = async () => {
  const key = localStorage.getItem('key')

  const res = await fetch(`${userLink}/${id}`, { 
    method: "DELETE",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
    }
  });
  if (res.status === 200) {
    console.log("delete successfully");
    goUserList();
  }
  // refresh token ----------------------------- //
    else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
    console.log('test...')
    const resForRefresh = await fetch(refreshLink, {
      headers: {
        Authorization: "Bearer " + localStorage.getItem('key'),
        isRefreshToken: true ,
      },
    })
      const jwt = await resForRefresh.json()
      console.log(jwt)
      if(resForRefresh.status === 200){
        // set localStorage
        localStorage.setItem('key',jwt.token)
        localStorage.setItem('token','refreshToken')
        getUser()
      }
    }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        console.log('เข้า')
    }
    // ------------------------------------------------- // 
    else console.log("error");
};

// assign to edit attribute
const isEdit = ref(false);
const editName = ref("");
const editEmail = ref("");
const editRole = ref("");

const editInfo = () => {
  isEdit.value = true;
  editName.value = name.value;
  editEmail.value = email.value;
  editRole.value = role.value;
};

const cancel = () => {
  isEdit.value = false;
  editName.value = '';
  editEmail.value = '';
  editRole.value = '';
  isEdit.value = false;
};

const edit =async()=>{
       let canEdit=undefined
       const key = localStorage.getItem('key')
        const res = await fetch(`${userLink}/${id}`, {
        method: "PUT",
        headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        },
        body: JSON.stringify({
          name:editName.value,
          email:editEmail.value,
          role:editRole.value,
        }),
      });
      if (res.status === 200) {
        let editUserDetail = await res.json();
        name.value = editUserDetail.name;
        email.value = editUserDetail.email;
        role.value = editUserDetail.role;
        isEdit.value = false;
        canEdit=true
        editSuccess.value=true
       }
       // refresh token ----------------------------- //
      else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
        console.log('test...')
        const resForRefresh = await fetch(refreshLink, {
        headers: {
          Authorization: "Bearer " + localStorage.getItem('key'),
          isRefreshToken: true ,
        },
      })
      const jwt = await resForRefresh.json()
      console.log(jwt)
      if(resForRefresh.status === 200){
        // set localStorage
        localStorage.setItem('key',jwt.token)
        localStorage.setItem('token','refreshToken')
        getUser()
      }
    } else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        console.log('เข้า')
    }
    // ------------------------------------------------- //
    else {
        canEdit=false
        editSuccess.value=false
    }
    return canEdit
}

// submit
const isInput=ref(undefined)
const editSuccess=ref(undefined)

const submitt = async () => {
    edit()
    goUserList()
    getUser()
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
      <h2 class="text-center mb-1 font-bold text-xl">No data</h2>
      <div class="w-fit m-auto">
        <button @click="goUserList" class="custom-btn back block">Go Back</button>
      </div>
    </div>

    <!-- have data -->
    <div
      v-else-if="isNotNull == true"
      class="p-4 border-double border-4 border-neutral-300 max-w-screen-lg"
    >
      <div class="mx-2 w-full">
        <h1 class="text-center mb-1 font-bold text-2xl">User Information</h1>
        <h3 class="text-center">----------------</h3>
      </div>

      <div class="flex mx-auto my-4 w-full">
        <div class="px-2 w-4/5 inline-flex my-5 mx-auto">
        <!-- Name -->
          <div class="pr-2 font-semibold flex m-auto text-gray-400"> Username :</div>
          <div
            v-if="isEdit == false"
            class="overflow-hidden overflow-x-scroll border-2 rounded-md p-1.5 pt-2.5 font-normal bg-white flex w-2/5"
          >
            {{ name }}
          </div>
          <div
            v-if="isEdit == true"
            class="edit-color showUp border-2 rounded-md p-1.5 pt-2.5 font-normal bg-white flex w-2/5 h-12"
          >
            <input type="text" class="w-full h-full" v-model="editName" />
          </div>

          <!-- role -->
          <div class="pr-2 font-semibold flex m-auto text-gray-400"> Role :</div>
          <div
            v-if="isEdit == false"
            class="overflow-hidden overflow-x-scroll border-2 rounded-md p-1.5 pt-2.5 justify-center font-normal m-auto -ml-2 bg-amber-400 flex w-1/4 h-12"
          >
            {{ role }}
          </div>
          <div
            v-if="isEdit == true"
            class="edit-color showUp border-2 rounded-md p-1.5 text-center font-normal bg-white inline-block w-1/4 h-12"
          >
            <select v-model="editRole" class="p-0.5 w-9/10 h-8 w-full text-center">
                  <option value disabled selected>Select Role</option>
                  <option
                    v-for="(eachRole, index) in roles"
                    :key="index"
                    :value="eachRole"
                  >
                    {{ eachRole }}
                  </option>
            </select>
          </div>
        </div>
      </div>
          
      
      <!-- E-mail -->
        <div class="w-4/5 inline-flex">
          <div class="pr-2 font-semibold flex my-auto ml-28 mr-4 w-1/5 text-gray-400">
            E-mail :
          </div>
          <div
            v-if="isEdit == false"
            class="overflow-hidden overflow-x-scroll border-2 rounded-md p-1.5 pt-2.5 font-normal bg-white flex w-full h-12 "
          >
            {{ email }}
          </div>
          <div
            v-if="isEdit == true"
            class="edit-color showUp overflow-hidden overflow-x-scroll border-2 rounded-md p-1.5 pt-2.5 font-normal bg-white flex w-full h-12 "
          >
            <input type="text" class="w-full h-full" v-model="editEmail" />
          </div>
        </div>

        <div class="text-center mt-5"><p> --------------------------------------------------------------- </p></div>

      <!-- created on -->
      <div class="inline-block">
      <div class="flex mt-5 mb-3 w-full">
        <div class="pr-2 flex text-sm my-auto ml-24 mr-4 text-gray-400">
          <div class="p-3 inline-block m-auto text-gray-400">
            Created on :
          </div>
          <div
            class="border-2 text-black rounded-md p-1.5 font-normal bg-white inline-block text-center w-60 h-10"
          >
            <!-- {{ createdOn }} -->
            Date : {{ formatDate(createdOn) }} , Time : {{ formatTime(createdOn) }}
          </div>
        </div>
      </div>

      <!-- updated on -->
      <div 
        class="flex my-3 w-full"
        v-if="updatedOn !== createdOn"
        >
        <div class="pr-2 text-sm flex my-auto ml-24 mr-4 text-gray-400">
          <div class="p-3 inline-block m-auto text-gray-400">
            Updated on :
          </div>
          <div
            class="border-2 text-black rounded-md p-1.5 font-normal bg-white inline-block text-center w-60 h-10"
          >
            Date : {{ formatDate(updatedOn) }} , Time : {{ formatTime(updatedOn) }}
          </div>
        </div>
      </div>
      </div>


       <!-- button not edit mode -->
        <div v-if="isEdit == false" class="showUp m-auto w-fit">
          <button @click="editInfo" class="m-4 custom-btn edit">Edit</button>
          <a  href="#remove" class="m-4 custom-btn remove">
            Remove
          </a>
          <button @click="goUserList"  class="m-4 custom-btn back">Go Back</button>
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


  <!-- for alert -->
  <!-- <div class="alert-area">
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
          userList.length == 0
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

  </div> -->

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
          @click="removeUser"
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
.edit-color {
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
