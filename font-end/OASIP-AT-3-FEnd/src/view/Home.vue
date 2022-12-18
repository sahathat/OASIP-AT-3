<script setup>
import { useRouter } from "vue-router";
import { onBeforeMount, ref } from "vue";

const myRouter = useRouter();
const goLogin = () => myRouter.push({ name: "Login" });
const goBooking = () => myRouter.push({ name: "Booking" });
const goScheduleView =()=>myRouter.push({name:'ScheduleView'})

const userRole = ref('guest')
const checkRole = () => {
    const role = localStorage.getItem('role')
    if(role !== null){
        if(role.substring(6,role.length-1)=='admin') userRole.value = 'admin'
        else if(role.substring(6,role.length-1)=='lecturer') userRole.value = 'lecturer'
        else if(role.substring(6,role.length-1)=='student') userRole.value = 'student'
    }
    else userRole.value = 'guest'
    // console.log(userRole.value)
    return userRole.value
}
onBeforeMount(async () => {
  checkRole()
});

setInterval(async () => {
    checkRole()
}, 1000);

</script>

<template>
  <body class="mh-100 mw-100">
    <section class="position-relative py-4 py-xl-5">
        <div class="container">
            <div class="rounded border-0 d-flex flex-column justify-content-center align-items-center p-4 py-5" style="background: linear-gradient(rgba(0,123,255,0.2), rgba(0,123,255,0.2)), center / cover;height: 500px;">
                <p class="fw-bold text-primary mb-2">Would you like to make a reservation?<br></p>
                <div class="row">
                    <div class="col-md-10 col-lg-12 col-xl-8 text-center d-flex d-sm-flex d-md-flex justify-content-center align-items-center mx-auto justify-content-md-start align-items-md-center justify-content-xl-center">
                        <div>
                            <h1 class="text-uppercase fw-bold mb-3"><strong>ONLINE &nbsp; APPOINTMENT</strong><br><strong>SCHEDULING SYSTEM</strong><br></h1>
                            <p class="mb-4">Booking appointment by yourself!<br></p>

                            <!-- button -->
                            <!-- <button v-if="checkRole=='guest'" class="btn btn-primary fs-5 me-2 py-2 px-4" type="button" @click="goLogin">Start</button> -->
                            <button v-if="userRole!=='guest'" class="btn btn-primary fs-5 me-2 py-2 px-4" type="button" @click="goBooking">Start</button>
                            <button v-if="userRole=='guest'" class="btn btn-primary fs-5 py-2 px-4 ml-2 my-3" type="button" @click="goScheduleView">
                              View Reservation
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
  </body>
</template>

<style scoped>
.showUp {
  position: relative;
  animation: wii 1s;
  animation-timing-function: ease-in-out;
}

/* animation */
@keyframes wii {
  0% {
    top: -40px;
    opacity: 0;
  }

  100% {
    top: 0px;
    opacity: 1.5;
  }
}
</style>
