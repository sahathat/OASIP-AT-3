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
    localStorage.removeItem('name')
    haveKey.value = null
    console.log(localStorage.getItem('key'))
    goHome()
}

const haveKey = ref(null)
const isHaveKey = () => {
    const key = localStorage.getItem('key')
    if(key==null || key==undefined) haveKey.value = false
    else if(key!==null && key!==undefined) haveKey.value = true
    return haveKey.value
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

const userName = ref('')
const getUserEmail = () => {
    const name = localStorage.getItem('name')
    userName.value = name
}

//show navbar2
onBeforeMount(async () => {
    isHaveKey()
    checkRole()
    getUserEmail()
})

setInterval(async () => {
    isHaveKey()
    checkRole()
    getUserEmail()
}, 1000);

</script>

<template>
<!--   
  <router-link :to="{ name:'Home'}" class="font-bold">home</router-link>
  <router-view></router-view> -->
<body>
  <nav class="navbar navbar-dark navbar-expand-md bg-dark py-1">
        <!-- logo -->
        <div class="container">
            <a class="navbar-brand d-flex align-items-center" @click="goHome">
                <img src="./assets/icon.png" width="60" height="55">
            </a>
            <span class="w-100 text-white" style="margin-right: 5px;margin-left: 5px;padding-right: 10px;padding-left: 10px;">
                <BaseDate :isTime='true' />
                <br>
                <BaseDate :isDate="true" />
            </span>
            <!-- toggle navigation -->
            <button data-bs-toggle="collapse" class="navbar-toggler" data-bs-target="#navcol-5">
                <span class="visually-hidden"></span>
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <!-- menu -->
            <div v-show="haveKey==true && userRole!=='guest'" class="collapse navbar-collapse w-75" id="navcol-5" style="margin-top: 0px; margin-right: 60px; padding-right: 30px; padding-left: 30px; margin-left: 60px;">
                <ul class="navbar-nav ms-auto">
                    <!-- menu: home -->
                    <li v-if="userRole=='admin' || userRole=='student' || userRole=='lectuere'" class="nav-item">
                        <a class="nav-link" @click="goHome">Home</a>
                    </li>

                    <!-- menu: booking -->
                    <li v-if="userRole=='admin' || userRole=='student'" class="nav-item">
                        <a class="nav-link" @click="goBooking">Booking</a>
                    </li>

                    <!-- menu: reservation -->
                    <li v-if="userRole=='admin' || userRole=='student' || userRole=='lectuere'" class="nav-item">
                        <a class="nav-link" @click="goReservationList">Reservation</a>
                    </li>

                    <!-- menu: categories -->
                    <li v-if="userRole=='admin' || userRole=='lectuere'" class="nav-item">
                        <a class="nav-link" @click="goCategoriesList">Categories</a>
                    </li>

                    <!-- menu: users -->
                    <li v-if="userRole=='admin'" class="nav-item">
                        <a class="nav-link" @click="goUserList">Users</a>
                    </li>

                    <!-- menu: contact -->
                    <li v-if="userRole=='admin' || userRole=='student' || userRole=='lectuere'" class="nav-item">
                        <a class="nav-link" @click="goContactUs">Contact</a>
                    </li>
                </ul>
            </div>
            
            <!-- show username -->
            <p v-if="haveKey==true && userRole!=='guest'" class="ms-auto w-75 text-white" style="margin-right: 5px;margin-left: 5px;padding-right: 10px;padding-left: 10px;margin-top: 10px;"> 
                {{ userName }} 
            </p>
            
            <!-- button -->
            <!-- sign up / sign in -->
            <a v-if="haveKey==false || userRole=='guest'" class="btn btn-primary ms-md-2 mr-3" role="button" href="#" @click="goCreateUser"> Sign up </a>
            <a v-if="haveKey==false || userRole=='guest'" class="btn btn-primary ms-md-2" role="button" href="#" @click="goLogin"> Sign in </a>

            <!-- sign out -->
            <a v-if="haveKey==true && userRole!=='guest'" class="btn btn-danger ms-md-2 ml-2 w-25" role="button" href="#signout">Sign out</a>
        </div>
    </nav>
    
    <div class="flex object-cover ">
        <router-view></router-view> 
    </div>
 
 <!-- ------------------------------------------- -->
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

            <div class="modal fade" role="dialog" tabindex="-1" id="modal-1">
                <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header text-bg-warning" style="padding-top: 10px;padding-bottom: 10px;padding-left: 20px;padding-right: 20px;">
                            <h4 class="modal-title fs-3">Are you sure?</h4>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p class="fs-6">Are you sure to sign out ?</p>
                        </div>
                        <div class="modal-footer" style="padding-bottom: 5px;padding-top: 5px;">
                            <a href="#"><button class="btn btn-danger btn-sm" type="button" data-bs-dismiss="modal" data-bs-target="#modal-1" data-bs-toggle="modal">Cancel</button></a>
                            <button @click="signOut" class="btn btn-primary btn-sm" type="button" data-bs-target="#modal-1" data-bs-toggle="modal">Yes</button>
                        </div>
                    </div>
                </div>
            </div>
 </body>
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
