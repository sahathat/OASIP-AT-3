<script setup>
import { computed } from "@vue/reactivity";
import { onBeforeMount, onUpdated, ref } from "vue";
import { useRoute,useRouter } from "vue-router";

const { params } = useRoute();
const myRouoter = useRouter();
const goHome = () => myRouoter.push({ name: "Home" });

const email = ref("");
const password = ref("");
const statusMessage = ref("");
const status = ref(0)

const db = "http://localhost:5000/booking";
const matchedLink = `${import.meta.env.BASE_URL}api/users/matched`;
// const userLink = "http://ip21at3.sit.kmutt.ac.th:8081/api/users";

// validate email
const isEmailEmpty = ref(undefined);
const validateEmailValid = () => {
    isEmailEmpty.value = email.value == null || email.value == ''
}

//validate password
const isPasswordEmpty = ref(undefined);
const validationPassword = () => {
    isPasswordEmpty.value = password.value == null || password.value == ''
}

//show or hide password
const showPassword = ref(false);
const toggleShow = () => {
    showPassword.value = !showPassword.value;
};

const cancel = () => {
  email.value = "" ;
  password.value = "" ;
  goHome()
};

const LoginUser = () => {
    if (email.value == null || email.value == '' || password.value == null || password.value == '') {
        isPasswordEmpty.value = email.value == null || email.value == ''
        isPasswordEmpty.value = password.value == null || password.value == ''
    } else {
        checkMatchTODB(email.value, password.value)
    }
};

//check password 
const checkMatchTODB = async () => {
    console.log('Sign in..');
    // console.log(email.value)
    // console.log(password.value)
    const res = await fetch(matchedLink, {
        method: "POST",
        headers: {
            "content-type": "application/json",
        },
        body: JSON.stringify({ email: email.value, password: password.value })
    });

    console.log(res.status);
    status.value = res.status
    statusMessage.value = res.status == 200 ? 'Password Matches !' :
        res.status == 401 ? 'Password Not Matches !' :
        res.status == 404 ? 'A user with the specified email DOSE NOT exist !' : ''
}

</script>

<template>
  <div class="showUp container mx-auto">
    <div
      class="max-w-screen-sm p-5 pb-7 mt-10 bg-gray-200 rounded-md shadow-xl mx-auto justify-items-center"
    >
      <div class="text-center">
        <h1 class="my-3 text-3xl font-semibold text-gray-700"> Sign in</h1>
        <p class="text-gray-400">
          Fill up the form below to login your account.
        </p>
      </div>

    <div>
        <!-- email -->
        <div class="justify-center mx-auto mt-10 my-3 inline-flex px-4 w-full">
          <div class="inline-block">
            <div class="px-3 w-full">
              <label for="email" class="font-medium text-sm text-gray-600"
                >Email Address</label
              >
              <span v-if="email == ''"
                class="text-red-500 font-medium ml-1 text-sm" 
              > enter your email !!
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
                  isEmailEmpty == false ? 'border-color:red' : '',
                ]"
                class="w-80 px-3 py-2 mx-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              />
            </div>
          </div>
          </div>
        
        <!-- Password -->
        <div class="justify-center ml-4 my-3 inline-flex px-4 w-full">
          <div class="inline-block m-auto mx-2">
            <div class="px-3 w-full">
              <label for="name" class="font-medium m-auto text-sm text-gray-600"
                >Password</label
              >
              <span v-if="password == ''"
                class="text-red-500 font-medium ml-1 text-sm" 
              >
                enter your password !!
              </span>
            </div>
            <div>
            <!-- open password -->
              <input
               v-if="showPassword"
                v-model="password"
                type="text"
                name="password"
                required
                :style="[
                  isPasswordEmpty == false ? 'border-color:red' : '',
                ]"
                class="w-80 px-3 py-2 mx-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              />

            <!-- close password -->
              <input
               v-if="!showPassword"
                v-model="password"
                type="password"
                name="password"
                required
                :style="[
                  isPasswordEmpty == false ? 'border-color:red' : '',
                ]"
                class="w-80 px-3 py-2 mx-2 placeholder-gray-300 border border-gray-400 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              />

              <!-- button to show/hide password -->
               <button
                    class="w-8 h-8 flex-1 px-2 py-2 ml-1 text-gray-600 border border-gray-300 rounded-md focus:ring-gray-500 focus:border-gray-900 sm:text-sm focus:outline-none"
                    @click="toggleShow">
                    <span class="icon is-small is-right">
                        <i class="fas" 
                            :class="{
                                'fa-eye-slash': !showPassword,
                                'fa-eye': showPassword,
                        }"></i>
                    </span>
                </button>
            </div>
          </div>
        </div>
    </div>

         
          <!-- submit button -->
          <div class="justify-center w-96 mx-auto">
          <div class="mx-auto w-2/5 justify-center inline-flex p-5">
            <a
              href="#submit"
              class="font-bold text-gray-900 hover:text-white border border-gray-800 hover:border-green-400 hover:scale-110 focus:ring-1 focus:outline-none focus:ring-gray-300 rounded-lg text-sm text-center dark:border-gray-600 dark:text-gray-400 dark:hover:text-white dark:hover:bg-green-400 dark:focus:ring-gray-800 p-5"
            >
              Submit !
            </a>
          </div>

          <!-- cancel button -->
          <div class="inline-flex p-5 mx-auto w-3/5justify-center">
            <button
              @click="cancel()"
              class="font-bold text-gray-900 hover:text-white border border-gray-800 hover:border-red-400 hover:scale-110 focus:ring-1 focus:outline-none focus:ring-gray-300 rounded-lg text-sm text-center dark:border-gray-600 dark:text-gray-400 dark:hover:text-white dark:hover:bg-red-400 dark:focus:ring-gray-800 p-5"
            >
              Cancel
            </button>
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
              @click="checkMatchTODB"
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
        <div v-if="email == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input your email.
        </div>

         <div v-if="validateEmailValid == false" class="alert text-sm">
           <span class="closebtn" @click="validateEmailValid = true">x</span> 
         </div>

        <!-- <div v-if="valEmail(email) == false" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Invalid email address!.
        </div> -->

        <div v-else-if="isEmailEmpty == true" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input your email.
        </div>

        <!-- <div v-if="validateRoleisNotNull == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateRoleisNotNull = true">x</span> -->
        <div v-if="password == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please enter your password.
        </div>

        <!-- add success alert-->
        <!-- <div v-if="addSuccess == true" class="alert success text-sm">
          <span class="closebtn" @click="addSuccess = false">x</span>
          <strong class="block">Success!</strong> Create new user success.
        </div> -->

        <!-- add error alert-->
        <!-- <div v-if="addSuccess == false" class="alert text-sm">
          <span class="closebtn" @click="isStatus = true">x</span>
          <strong class="block">Error!</strong> Cannot create new user.
        </div> -->

        </div>
      </div>
        <footer 
        v-if="status !== 0"
        class="border-4 bg-amber-500 max-w-screen-md p-5 pb-7 mt-5 rounded-md shadow-xl mx-auto">
            {{ statusMessage }}
            <!-- hgfdsafgh -->
        </footer>
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