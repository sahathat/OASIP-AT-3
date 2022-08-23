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
        component: Booking
    },
    {
        path:'/reservation/:id',
        name:'Reservation',
        component: Reservation
    },
    {
        path:'/reservation-list',
        name:'ReservationList',
        component: ReservationList
    },
    {

        path:'/categories/:id',
        name:'Categories',
        component: Categories
    },
    {
        path:'/categories-list',
        name:'CategoriesList',
        component: CategoriesList
    },
    {
        path:'/user-info/:id',
        name:'UserInfo',
        component: UserInfo
    },
    {
        path:'/user-list',
        name:'UserList',
        component: UserList
    },
    {
        path:'/create-user',
        name:'CreateUser',
        component: CreateUser
    },
    {
        path:'/contact-us',
        name:'ContactUs',
        component: ContactUs
    }
]

const router=createRouter({history,routes})
export default router