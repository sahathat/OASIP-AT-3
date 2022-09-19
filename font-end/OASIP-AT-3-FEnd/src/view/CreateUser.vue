<script setup>
import { computed } from "@vue/reactivity";
import { onBeforeMount, onUpdated, ref } from "vue";
import { useRoute,useRouter } from "vue-router";

const { params } = useRoute();
const myRouoter = useRouter();
const goSignin = () => myRouoter.push({ name: "Login" });

const name = ref("");
const email = ref("");
const role = ref("");
const password = ref("");
const confirmPassword = ref("");

const cornfirmPasswordLength = 14 ;
const passwordLength = 14 ;

const nameLength = 100;
const emailLength = 50;

const db = "http://localhost:5000/booking";
const userLink= `${import.meta.env.BASE_URL}api/users`;
// const userLink = 'http://localhost:8443/api/users';



const userList = ref([]);
const roles = ['admin','lecturer','student']

const addSuccess = ref(undefined);
const getStatus = ref(undefined);

// validate name
const validateNameisNotNull = ref(undefined);
const validateNameLength = ref(undefined);
const validateNameUnique = ref(false)

// validate email
const validateEmailisNotNull = ref(undefined);
const validateEmailLength = ref(undefined);
const validateEmailUnique = ref(false)
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

const checkUnique = () => {
    validateNameUnique.value = userList.value.map((user) =>{return user.name.trim()}).includes(name.value.trim())
    validateEmailUnique.value = userList.value.map((user) =>{return user.email.trim()}).includes(email.value.trim())
    // console.log(validateNameUnique.value);
    // console.log(validateEmailUnique.value);
}

// validate role
const validateRoleisNotNull = ref(undefined);

// validate password
const validatePassword = () => {
  if(password.value == ''|| password.value == null) { return false; }
}
const checkPassword = () => {
  if(password.value !== confirmPassword.value) { return false; }
}

const cancel = () => {
  name.value = "" ;
  email.value = "" ;
  role.value = "" ;
  password.value = "";
  confirmPassword.value = "";
  goUserList()
};

// submit
const submitt = async () => {
  if (
    name.value !== "" &&
    email.value !== "" &&
    role.value !== "" &&
    password.value !== "" &&
    confirmPassword .value !== ""
  ) {
    if (name.value.length > nameLength) {
      validateNameLength.value = false;
      //alert('The number of characters name is exceeded.')
    } else if (valEmail(email.value) == false) {
      validateEmailValid.value = false;
      //alert('Invalid email address!')
    } else if (email.value.length > emailLength) {
      validateEmailLength.value = false;
      //alert('The number of characters in the email exceeded the limit.')

    } else if (valEmail(email.value) == false) {
      validateEmailValid.value = false;
      //alert('Invalid email address!')
    
    } 
    else if (createUser()) {
        name.value = "";
        email.value = "";
        role.value = "";
        password.value = "";
        confirmPassword.value = "";
        // validateEmailisNotNull.value = undefined;
        // validateNameisNotNull.value = undefined;
        // validateRoleisNotNull.value = undefined;
        // validateEmailLength.value = undefined;
        // validateNameLength.value = undefined;
        // validateEmailValid.value = undefined;
        // addSuccess.value = undefined;
        // addSuccess.value = true;
    }
    } else {
    // alert('Please complete the information.')
    if (name.value == "") {
      validateNameisNotNull.value = false;
    }
    if (email.value == "") {
      validateEmailisNotNull.value = false;
    }
    if (role.value == "") {
      validateRoleisNotNull.value = false;
    }
  }
   goSignin()
  console.log(addSuccess.value)
  console.log(isStatus.value)
};

// fetch create
const statusMessage = ref("");
const status = ref(0)
const isStatus = ref(undefined);
const createUser = async () => {
  let createStatus = undefined;
  const res = await fetch(`${userLink}/signup`, {
    method: "POST",
    headers: {
      "content-type": "application/json",
    },
    body: JSON.stringify({
      name: name.value,
      email: email.value,
      role: role.value,
      password:password.value
    }),
  });
  
  if (res.status === 201) {
    status.value = 201
    addSuccess.value = true;
    createStatus = true;
    statusMessage.value = 'Create new user success'
    isStatus.value = true;
    console.log(addSuccess.value)
    setTimeout(() => (addSuccess.value = false), 5000);
  } else {
    status.value = res.status
    addSuccess.value = false;
    createStatus = false;
    isStatus.value = false;
  }
  return createStatus;
};

// ขาดแจ้งเตือนตอนadd ว่าได้หรือไม่
//GET user
const getUser = async () => {
  const key = localStorage.getItem('key')
  const res = await fetch(userLink, {
    method: "GET",
    headers: {
          "Authorization":'Bearer ' + key ,
          "Accept": 'application/json',
          "content-type": "application/json",
    }});
  if (res.status === 200) {
    userList.value = await res.json();
    getStatus.value = true;
  } else {
    getStatus.value = false;
  }
}

const resGetUser = ref(undefined);
// check every 10 second
setInterval(async () => {
  //console.log(countGetEvent.value++)
  const key = localStorage.getItem('key')
  resGetUser.value = await fetch(userLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json"
  }});
  if (resGetUser.value.status === 200) {
    userList.value = await resGetUser.value.json();
    getStatus.value = true;
  } else getStatus.value = false;
}, 10000);

onBeforeMount(async () => {
  await getUser();
});

</script>

<template>
  <div class="showUp container mx-auto">
    <div
      class="max-w-screen-md p-5 pb-7 mx-auto mt-10 bg-gray-200 rounded-md shadow-xl"
    >
      <div class="text-center">
        <h1 class="my-3 text-3xl font-semibold text-gray-700"> Sign up</h1>
        <p class="text-gray-400">
          Fill up the form below to create your account.
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

          <!-- role -->
          <div class="inline-block my-auto mr-9">
            <div class="px-3 w-full">
              <label for="category" class="text-sm font-medium text-gray-600"
                > Role </label
              >
            </div>
            <div>
              <select
                id="role"
                :style="[
                  validateRoleisNotNull == false ? 'border-color:red' : '',
                ]"
                class="text-ellipsis overflow-hidden cursor-pointer w-64 font-medium px-3 py-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
                v-model="role"
              >
                <option value disabled selected> Select your category </option>
                <option
                  v-for="(role,index) in roles"
                  :key="index"
                  :value="role"
                >
                  {{ role }}
                </option>
              </select>
            </div>
          </div>
        </div>

        <!-- email -->
        <div class="my-3 inline-flex px-4 w-full">
          <div class="inline-block ml-9">
            <div class="px-3 w-full">
              <label for="email" class="font-medium text-sm text-gray-600"
                >Email Address</label
              >
              <span
                class="text-gray-300 font-medium ml-1 text-sm"
                :style="[email.length > emailLength ? 'color:red' : '']"
              >
                {{ email.length }}/{{ emailLength }} charecters
              </span>
            </div>
            <div>
              <input
                v-model="email"
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
        
        <!-- Password -->
        <div class="mx-7 my-3 inline-flex px-4 w-full">
          <div class="inline-block m-auto mx-2">
            <div class="px-3 w-full">
              <label for="name" class="font-medium m-auto text-sm text-gray-600"
                >Password</label
              >
              <span
                class="text-gray-300 font-medium ml-1 text-sm"
                :style="[password.length > passwordLength ? 'color:red' : '']"
              >
                {{ password.length }}/{{ passwordLength }} charecters
              </span>
            </div>
            <div>
              <input
                v-model="password"
                type="password"
                name="name"
                required
                class="w-80 px-3 py-2 mx-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              />
            </div>
          </div>

          <!-- Confirm Password -->
          <div class="inline-block m-auto">
            <div class="px-3 w-full">
              <label for="name" class="font-medium m-auto text-sm text-gray-600">
                Confirm Password
              </label>
              <span
                class="text-gray-300 font-medium ml-1 text-sm"
                :style="[confirmPassword.length > cornfirmPasswordLength ? 'color:red' : '']"
              >
                {{ confirmPassword.length }}/{{ cornfirmPasswordLength }} charecters
              </span>
            </div>
            <div>
              <input
                v-model="confirmPassword"
                type="password"
                name="name"
                required
                class="w-80 px-3 py-2 mx-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              />
            </div>
          </div>
        </div>
        </div>

          <!-- submit button -->
          <div class="inline-flex m-auto p-5 w-60">
            <a
              @click="checkUnique"
              href="#submit"
              class="font-bold text-gray-900 hover:text-white border border-gray-800 hover:border-green-400 hover:scale-110 focus:ring-1 focus:outline-none focus:ring-gray-300 rounded-lg text-sm text-center dark:border-gray-600 dark:text-gray-400 dark:hover:text-white dark:hover:bg-green-400 dark:focus:ring-gray-800 ml-10 p-5"
            >
              Submit !
            </a>
          </div>

          <!-- cancel button -->
          <div class="inline-flex -ml-20 p-5 w-60">
            <button
              @click="cancel()"
              class="font-bold text-gray-900 hover:text-white border border-gray-800 hover:border-red-400 hover:scale-110 focus:ring-1 focus:outline-none focus:ring-gray-300 rounded-lg text-sm text-center dark:border-gray-600 dark:text-gray-400 dark:hover:text-white dark:hover:bg-red-400 dark:focus:ring-gray-800 p-5"
            >
              Cancel
            </button>
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

      <!-- for alert -->
      <!-- warning alert-->
      <div class="alert-area">
        <!-- <div v-if="validateNameisNotNull == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateNameisNotNull = true">x</span> -->
        <div v-if="name == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input your name.
        </div>

        <!-- <div v-if="validateNameLength == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateNameLength = true">x</span> -->
        <div v-else-if="name.length > nameLength" class="alert warning text-sm">
          <strong class="block">Warning!</strong> The number of characters name is
          exceeded.
        </div>

        <div v-else-if="validateNameUnique" class="alert warning text-sm">
          <strong class="block">Warning!</strong> This name is already exists.
        </div>

        <!-- <div v-if="validateEmailisNotNull == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateEmailisNotNull = true">x</span> -->
        <div v-if="email == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input your email.
        </div>

        <!-- <div v-if="validateEmailValid == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateEmailValid = true">x</span> -->
        <div v-if="valEmail(email) == false" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Invalid email address!.
        </div>

        <!-- <div v-if="validateEmailLength == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateEmailLength = true">x</span> -->
        <div v-if="email.length > emailLength " class="alert warning text-sm">
          <strong class="block">Warning!</strong> The number of characters email
          is exceeded.
        </div>

        <div v-else-if="validateEmailUnique" class="alert warning text-sm">
          <strong class="block">Warning!</strong> This email is already exists.
        </div>

        <!-- <div v-if="validateRoleisNotNull == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateRoleisNotNull = true">x</span> -->
        <div v-if="role == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please select role.
        </div>

        <!-- password alert alert -->
        <!-- <div v-if="validatePasswordValid == false" class="alert warning text-sm"> -->
        <div v-if="(password == ''|| password == null) == true" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input valid password.
        </div>
        <!-- <div v-if="checkPassword == false" class="alert warning text-sm"> -->
        <div v-if="(password !== confirmPassword) == true" class="alert warning text-sm">
          <strong class="block">Warning!</strong> The password DOES NOT match' and let the user enter confirm password again.
        </div>

        <div v-if="password.length < 8 || password.length > 14" class="alert warning text-sm">
          <strong class="block">Warning!</strong> The password must have between 8 and 14 characters.
        </div>

        <!-- add success alert-->
        <div v-else-if="status == 201" class="alert success text-sm">
          <span class="closebtn" @click="goUserList">x</span>
          <strong class="block">Success!</strong> create new account success.
        </div>

        <!-- add error alert-->
        <div v-if="status !== 201 && status !== 0" class="alert text-sm">
          <span class="closebtn" @click="cancel">x</span>
          <strong class="block">Error!</strong> Cannot create new user.
        </div>


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
