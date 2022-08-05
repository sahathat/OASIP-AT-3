
<script setup>
import {ref} from 'vue';

defineProps({
    isTime:{
        type:Boolean,
        default:false
    },
    isDate:{
        type:Boolean,
        default:false
    },
    isDay:{
        type:Boolean,
        default:false
    },
    isMonth:{
        type:Boolean,
        default:false
    },
    isYear:{
        type:Boolean,
        default:false
    },
    isHour:{
        type:Boolean,
        default:false
    },
    isMinute:{
        type:Boolean,
        default:false
    },
    isSecond:{
        type:Boolean,
        default:false
    }
})
const day = ref()
const month = ref()
const year = ref()
const hours = ref()
const minutes = ref()
const seconds = ref()
const time=ref()
const date =ref()

//assign to attribute
let clock = ()=>{
    let dateToday = new Date()
     day.value = dateToday.getDate()<10 ? (`0${dateToday.getDate()}`).toString():dateToday.getDate() // day
     month.value = (dateToday.getMonth()+1)<10 ? (`0${(dateToday.getMonth()+1)}`).toString() : (dateToday.getMonth()+1) // month
     year.value = dateToday.getFullYear().toString() // year
     
     hours.value = dateToday.getHours() < 10 ? (`0${dateToday.getHours()}`).toString():dateToday.getHours() // hour
    
     minutes.value = dateToday.getMinutes() < 10 ? (`0${dateToday.getMinutes()}`).toString(): dateToday.getMinutes() // minute
     
     seconds.value = dateToday.getSeconds() < 10 ? (`0${dateToday.getSeconds()}`).toString() : dateToday.getSeconds() // second
     
    //  convert to string
     time.value = `${hours.value}:${minutes.value}:${seconds.value}`
     date.value =`${day.value}-${month.value}-${year.value}`
}

    
    setInterval(clock,1000)

</script>
 
<template>
<span v-if="isTime===true">{{time}}</span>
<span v-else-if="isDate===true">{{date}}</span>
<span v-else-if="isDay===true">{{ day }}</span>
<span v-else-if="isMonth===true">{{ month }}</span>
<span v-else-if="isYear===true">{{ year }}</span>
<span v-else-if="isHour===true">{{ hours }}</span>
<span v-else-if="isMinute===true">{{ minutes }}</span>
<span v-else-if="isSecond===true">{{ seconds }}</span>

</template>
 
<style>

</style>