<script setup>
import {ref, onBeforeMount,onMounted, onUpdated} from 'vue'
import BaseDate from './components/BaseDate.vue'
import {useRouter} from 'vue-router'

// console.clear()
const logoSize = 'width:75px;';

const myRouter = useRouter()
const goHome = ()=>myRouter.push({name:'Home'})
const goBooking = ()=>myRouter.push({name:'Booking'})
const goReservationList =()=>myRouter.push({name:'ReservationList'})
const goCategoriesList =()=>myRouter.push({name:'CategoriesList'})
const goUserList = ()=>myRouter.push({name:'UserList'})
const goContactUs = ()=>myRouter.push({name:'ContactUs'})
const goCreateUser = ()=>myRouter.push({name:'CreateUser'})
const goLogin = ()=>myRouter.push({name:'Login'})

const signOut = () => {
    localStorage.removeItem('key')
    localStorage.removeItem('token')
    localStorage.removeItem('email')
    localStorage.removeItem('role')
    haveToken.value = null
    console.log(localStorage.getItem('key'))
    goHome()
}

const haveToken = ref(null)
const isHaveToken = () => {
    const token = localStorage.getItem('key')
    if(token!==null && token!==undefined) haveToken.value = true
    else haveToken.value = false
    return haveToken.value
}

const userRole = ref('guest')
const checkRole = () => {
    // const getRole = localStorage.getItem('role')
    // const role = getRole.substring(6,getRole.length-1)
    const role = localStorage.getItem('role')
    // console.log(role.substring(6,role.length-1))
    if(role !== null){
        if(role.substring(6,role.length-1)=='admin') userRole.value = 'admin'
        else if(role.substring(6,role.length-1)=='student') userRole.value = 'student'
        else if(role.substring(6,role.length-1)=='lecturer') userRole.value = 'lecturer'
    }
    else userRole.value = 'guest'
    // console.log(userRole.value)
    return userRole.value
}

//show navbar2
onBeforeMount(async () => {
    isHaveToken()
    checkRole()
})

setInterval(async () => {
    isHaveToken()
    checkRole()
}, 1000);

</script>

<template>
<!--   
  <router-link :to="{ name:'Home'}" class="font-bold">home</router-link>
  <router-view></router-view> -->
 
  <div class=" lg:pt-2 md:w-screen h-screen" id="bg_color">
    <nav class="bg-transparent drop-shadow-md md:rounded-lg md:mx-auto w-full">
                <div class="max-w-screen-xl mx-auto px-4 ">
                    <div class="md:flex justify-between drop-shadow-lg">
                        <div class="md:flex space-x-2 w-2/5">
                            <!-- logo -->
                            <div>
                                <a href="#" class="flex md:items-center py-5 px-0.5 text-gray-700 hover:text-gray-900"
                                    @click="goHome">
                                    <img src="./assets/icon.png" alt="LOGO" :style="logoSize" />
                                    <span class="font-bold text-xl text-white ml-5">
                                        <BaseDate :isTime='true' />
                                        
                                        <br>
                                        <span class=" font-medium">
                                          <BaseDate :isDate="true" />
                                        </span>
                                    </span>
 
                                </a>
                            </div>
                        </div>
                        

                        <!-- secondary nav -->
                        <div v-show="haveToken==true" class="w-full lg:flex items-center py-3 mx-auto" >
                            <ul>

                                <li>
                                    <button type="button" class="inline-block px-2 py-2  bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded  
                            hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500 focus:shadow-lg 
                            focus:outline-none focus:ring-0 active:bg-gray-900 active:shadow-lg 
                            transition duration-150 ease-in-out " @click="goHome">Home</button>
                                </li>

                                <!-- admin & student can see -->
                                <li v-if="userRole=='admin' || userRole=='student'">
                                    <button type="button" class="inline-block px-4 py-2 bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded 
                             hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500
                            focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-600
                            active:shadow-lg transition duration-150 ease-in-out" @click="goBooking">Booking</button>
                                </li>

                                <!-- everyone can see -->
                                <li>
                            <button type="button" class="inline-block px-2 py-2  bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded  
                            hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500 focus:shadow-lg 
                            focus:outline-none focus:ring-0 active:bg-gray-900 active:shadow-lg 
                            transition duration-150 ease-in-out " @click="goReservationList">Reservation</button>
                                </li>

                                <!-- admin & lecture can see -->
                                <li v-if="userRole=='admin' || userRole=='lecturer'">
                            <button type="button" class="inline-block px-2 py-2  bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded 
                            hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500 focus:shadow-lg 
                            focus:outline-none focus:ring-0 active:bg-gray-900 active:shadow-lg 
                            transition duration-150 ease-in-out " @click="goCategoriesList">Categories</button>
                                </li>

                                <!-- admin can see -->
                                <li  v-if="userRole=='admin'">
                            <button type="button" class="inline-block px-2 py-2  bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded 
                            hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500 focus:shadow-lg 
                            focus:outline-none focus:ring-0 active:bg-gray-900 active:shadow-lg 
                            transition duration-150 ease-in-out " @click="goUserList">Users</button>
                                </li>

                                <!-- everyone can see -->
                                <li>
                            <button type="button" class="inline-block px-2 py-2  bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded 
                            hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500 focus:shadow-lg 
                            focus:outline-none focus:ring-0 active:bg-gray-900 active:shadow-lg 
                            transition duration-150 ease-in-out " @click="goContactUs">Contact us</button>
                                </li>
                            </ul>
                        </div>

                        <!-- third nav -->
                        <div v-if="haveToken==false" class="lg:flex justify-end space-x-1 w-4/5">
                            <div class="lg:flex items-center space-x-1 w-1/5" >
                                <button type="button" class="hover:underline bg-white text-gray-800 font-bold rounded-full 
                                py-4 px-8 shadow-lg focus:outline-none focus:shadow-outline transform transition hover:scale-105 hover:bg-green-300
                                duration-300 ease-in-out" @click="goCreateUser"> Sign up </button>
                            </div>
                            <div class="lg:flex items-center space-x-1 w-1/5" >
                                <button type="button" class="hover:underline bg-white text-gray-800 font-bold rounded-full 
                                py-4 px-8 shadow-lg focus:outline-none focus:shadow-outline transform transition hover:scale-105 hover:bg-amber-400
                                duration-300 ease-in-out" @click="goLogin"> Sign in </button>
                            </div>
                        </div>

                        <!-- user account -->

                        <!-- sign out button -->
                        <div v-if="haveToken==true" class="lg:flex justify-end items-center w-1/6" >
                                <a  
                                class="hover:underline bg-white text-gray-800 font-bold rounded-full 
                                text-center w-full h-1/2 shadow-lg focus:outline-none focus:shadow-outline transform transition hover:scale-105 hover:bg-amber-400
                                duration-300 ease-in-out signout"
                                href="#signout"> 
                                    Sign Out 
                                </a>

                        </div>
                    </div>
                </div>
            </nav>
            
            <div class="flex object-cover ">
                <router-view></router-view> 
            </div>

            <!-- for confirm signout  -->
            <div id="signout" class="overlay">
                <div class="popup2 h-96">
                    <h2 class="mb-5 text-xl font-bold bg-white mx-auto w-fit">
                    Are you sure (Sign out) ?
                    </h2>

                    <div class="option flex m-auto w-full mt-10">
                        <a
                            @click="signOut"
                            href="#"
                            class="w-full text-center p-2 px-2 bg-gray-200 hover:bg-green-500 font-bold hover:text-white"
                            >Yes
                        </a>
                        <a
                            href="#"
                            class="w-full text-center p-2 px-2 bg-gray-200 hover:bg-rose-500 font-bold hover:text-white"
                            >No
                        </a>
                    </div>
                </div>
            </div>
  </div>
 
</template>

<style scoped>

ul li {
      list-style: none;
      margin: 0 auto;
      border-left: 2px solid #000000;
      display: inline-block;
      padding: 0 10px;
      position: relative;
      text-decoration: none;
      text-align: center;
      font-family: arvo;
    }
#bg_color {
	background: linear-gradient(-45deg, #525252, #1b1b1b,#525252,#1b1b1b, #525252,#1b1b1b);
	background-size: 400% 400%;
	animation: gradient 30s ease infinite;
	height: 100vh;
}
/* signout */
.signout {
  background: #fff;
  color: #000;
  line-height: 3em;
  padding: 3;
  border: none;
}
.signout:before,
.signout:after {
  content: "";
  position: absolute ;
  top: 0;
  right: 0;
  height: 2px;
  width: 0;
  background: #000;
  transition: 400ms ease all;
}
.signout:after {
  right: inherit;
  top: inherit;
  left: 0;
  bottom: 0;
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
  opacity: 10;
}

/* animation */
@keyframes gradient {
	0% {
		background-position: 0% 50%;
	}
	50% {
		background-position: 100% 50%;
	}
	100% {
		background-position: 0% 50%;
	}
}
</style>
