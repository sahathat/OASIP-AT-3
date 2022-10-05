<script setup>
import { useRouter } from "vue-router";
const myRouter = useRouter();
const goLogin = () => myRouter.push({ name: "Login" });
const goBooking = () => myRouter.push({ name: "Booking" });
</script>

<template>
  <div class="w-3/4 m-auto mt-12 p-10 flex text-gray-100">
    <!--Left Col-->
    <div class="showUp w-1/2 inline-block my-9 mx-20">
      <div class="uppercase tracking-loose w-fit">
        <p>Would you like to make a reservation?</p>
      </div>
      <div class="text-5xl font-bold leading-tight w-fit">
        <span>Online Appointment</span><br />
        <span>Scheduling System</span>
      </div>
      <div class="leading-normal text-2xl mb-8 w-fit">
        <p>Booking appointment by yourself!</p>
      </div>
      <div class="w-fit">
        <button
          class="hover:underline bg-white text-gray-800 font-bold rounded-full py-4 px-8 shadow-lg focus:outline-none focus:shadow-outline transform transition hover:scale-105 duration-300 ease-in-out"
          @click="goLogin"
        >
          Start
        </button>
      </div>
      <div class="w-fit mt-2">
        <button
          class="hover:underline bg-white text-gray-800 font-bold rounded-full py-4 px-8 shadow-lg focus:outline-none focus:shadow-outline transform transition hover:scale-105 duration-300 ease-in-out"
          @click="goBooking"
        >
          Start as guest
        </button>
      </div>
    </div>

    <!--Right Col-->
    <div class="showUp w-1/3 inline-block h-fit mt-20">
      <!-- ขึ้นลง -->
      <div class="w-62 m-auto">
        <img class="z-50 animate-bounce" src="../assets/test3.png" />
      </div>

      <!-- เข้มจาง -->
      <!-- <img class="w-full md:w-3/5 z-50 animate-pulse " src="../assets/test2.png" /> -->
      <!-- https://tailwindcss.com/docs/animation -->
    </div>
  </div>
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
