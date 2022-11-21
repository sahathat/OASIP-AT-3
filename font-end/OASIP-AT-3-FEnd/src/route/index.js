import {createRouter, createWebHistory} from 'vue-router'
import Home from '../view/Home.vue'
import NotFound from '../view/NotFound.vue'
import Booking from '../view/Booking.vue'
import Reservation from '../view/Reservation.vue'
import ReservationList from '../view/ReservationList.vue'

import Categories from '../view/Categories.vue'
import CategoriesList from '../view/CategoriesList.vue'
import UserInfo from '../view/User.vue'
import UserList from '../view/UserList.vue'
import CreateUser from '../view/CreateUser.vue'
import Login from '../view/Login.vue'
import ResetPassword from '../view/ResetPassword.vue'

// import store from '../store/store'
// import { IS_USER_AUTHENTICATION_GETTER } from '../store/storeconstants'

import ContactUs from '../view/ContactUs.vue'
const history=createWebHistory(import.meta.env.BASE_URL)
      
const routes=[
    {   path:'/',
        name:'Home',
        component: Home
    },
    {
        path:'/:NoPage(.*)',
        name:'NotFound',
        component: NotFound
    },
    {
        path:'/booking',
        name:'Booking',
        component: Booking,
        meta: { auth:true }
    }, 
    {
        path:'/reservation/:id',
        name:'Reservation',
        component: Reservation,
        meta: { auth:true }
    },
    {
        path:'/reservation-list',
        name:'ReservationList',
        component: ReservationList,
        meta: { auth:true }
    },
    {

        path:'/categories/:id',
        name:'Categories',
        component: Categories,
        meta: { auth:true }
    },
    {
        path:'/categories-list',
        name:'CategoriesList',
        component: CategoriesList,
        meta: { auth:false }
    },
    {
        path:'/user-info/:id',
        name:'UserInfo',
        component: UserInfo,
        meta: { auth:true }
    },
    {
        path:'/user-list',
        name:'UserList',
        component: UserList,
        meta: { auth:true }
    },
    {
        path:'/create-user',
        name:'CreateUser',
        component: CreateUser,
        meta: { auth:false }
    },
    {
        path:'/login',
        name:'Login',
        component: Login,
        meta: { auth:false }
    },
    {
        path:'/resetPassword',
        name:'ResetPassword',
        component: ResetPassword,
        meta: { auth:false }
    },
    {
        path:'/contact-us',
        name:'ContactUs',
        component: ContactUs,
        meta: { auth:false }
    }
]

const router = createRouter({history,routes})

// router.beforeEach((to,from,next) => {
//     if(to.meta.auth && !store.gettes[`auth/${IS_USER_AUTHENTICATION_GETTER}`]){
//         next({ name: 'Login' })
//     }
//     else if( !to.meta.auth && store.gettes[`auth/${IS_USER_AUTHENTICATION_GETTER}`]){
//         next({ name: 'Booking' })
//     }
//     else{
//         next()
//     }
// })

export default router