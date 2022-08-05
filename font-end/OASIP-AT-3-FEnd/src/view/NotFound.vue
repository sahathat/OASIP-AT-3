<script setup>
import {useRouter} from 'vue-router'
const myRouter = useRouter()
const goHome = ()=>myRouter.push({name:'Home'})
</script>

<template>
<div class="m-auto my-12 p-10 flex">
    <div class="flex-1 min-h-full min-w-full rounded-3xl bg-white shadow-xl p-10 lg:p-20 text-gray-800 relative md:flex items-center text-center md:text-left">
        <div class="w-full md:w-1/2">

            <div class="mb-10 md:mb-20 text-gray-600 font-light">
                <h1 class="font-black uppercase text-2xl lg:text-5xl text-red-500 mb-10">404 Page NOT FOUND</h1>
                <h1 class="font-black uppercase text-2xl lg:text-3xl text-yellow-500 mb-10">You seem to be lost!</h1>
                <p>The page you're looking for isn't available.</p>
                <p>Try searching again or use the Go Back button below.</p>
            </div>

            <div class="mb-20 md:mb-0">
                <button class="text-lg font-bolder outline-none focus:outline-none transform transition-all hover:scale-110 text-blue-500 hover:text-green-600"  @click="goHome">
                    <i class="mdi mdi-arrow-left mr-2">
                        </i>Go Back
                </button>
            </div>
            
        </div>

        <div class="w-full md:w-1/2 text-center">
            <div class="w-full max-w-lg lg:max-w-full mx-auto">
                 <img src="../assets/Rick_Rolling.gif" >
            </div>
        </div>
    </div>
</div>
    

</template>

<style>

</style>
