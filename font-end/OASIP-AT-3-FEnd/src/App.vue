<script setup>
import {ref,computed} from 'vue'
import BaseDate from './components/BaseDate.vue'
import {useRouter} from 'vue-router'

console.clear()
const logoSize = 'width:75px;';

const myRouter = useRouter()
const goHome = ()=>myRouter.push({name:'Home'})
const goBooking = ()=>myRouter.push({name:'Booking'})
const goReservationList =()=>myRouter.push({name:'ReservationList'})
const goCategoriesList =()=>myRouter.push({name:'CategoriesList'})
const goUserList = ()=>myRouter.push({name:'UserList'})
const goContactUs = ()=>myRouter.push({name:'ContactUs'})

</script>

<template>
<!--   
  <router-link :to="{ name:'Home'}" class="font-bold">home</router-link>
  <router-view></router-view> -->
 
  <div class=" lg:pt-2 md:w-screen h-screen" id="bg_color">
    <nav class="bg-transparent drop-shadow-md md:rounded-lg md:mx-auto w-3/5">
                <div class="max-w-screen-xl mx-auto px-4 mx-20 ">
                    <div class="md:flex justify-between drop-shadow-lg">
                        <div class="md:flex space-x-4">
                            <!-- logo -->
                            <div>
                                <a href="#" class="flex md:items-center py-5 px-0.5 text-gray-700 hover:text-gray-900"
                                    @click="goHome">
                                    <img src="./assets/icon.png" alt="LOGO" :style="logoSize" />
                                    <span class="font-bold text-xl text-white mx-4">
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
                        <div class=" lg:flex items-center space-x-1 px-6 py-3 mx-auto" >
                            <ul>

                                <li>
                                    <button type="button" class="inline-block px-2 py-2  bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded  
                            hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500 focus:shadow-lg 
                            focus:outline-none focus:ring-0 active:bg-gray-900 active:shadow-lg 
                            transition duration-150 ease-in-out " @click="goHome">Home</button>
                            
                                </li>
                                <li>
                                    <button type="button" class="inline-block px-4 py-2 bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded 
                             hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500
                            focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-600
                            active:shadow-lg transition duration-150 ease-in-out" @click="goBooking">Booking</button>

                                </li>
                                <li>
                            <button type="button" class="inline-block px-2 py-2  bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded  
                            hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500 focus:shadow-lg 
                            focus:outline-none focus:ring-0 active:bg-gray-900 active:shadow-lg 
                            transition duration-150 ease-in-out " @click="goReservationList">Reservation</button>
                                </li>
                                <li>
                            <button type="button" class="inline-block px-2 py-2  bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded 
                            hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500 focus:shadow-lg 
                            focus:outline-none focus:ring-0 active:bg-gray-900 active:shadow-lg 
                            transition duration-150 ease-in-out " @click="goCategoriesList">Categories</button>
                                </li>
                                <li>
                            <button type="button" class="inline-block px-2 py-2  bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded 
                            hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500 focus:shadow-lg 
                            focus:outline-none focus:ring-0 active:bg-gray-900 active:shadow-lg 
                            transition duration-150 ease-in-out " @click="goUserList">User</button>
                                </li>
                                <li>
                            <button type="button" class="inline-block px-2 py-2  bg-transparent text-white 
                            font-medium text-lg leading-tight  rounded 
                            hover:bg-gray-900 hover:shadow-lg focus:bg-blue-500 focus:shadow-lg 
                            focus:outline-none focus:ring-0 active:bg-gray-900 active:shadow-lg 
                            transition duration-150 ease-in-out " @click="goContactUs">Contact us</button>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </nav>

            <div class="flex  object-cover ">
                <router-view></router-view> 

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
