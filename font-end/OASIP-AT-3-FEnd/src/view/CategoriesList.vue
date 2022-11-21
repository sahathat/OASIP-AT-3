<script setup>
import { ref, onBeforeMount, onUpdated } from "vue";
import { useRoute,useRouter } from "vue-router";
const categoryList = ref([]);
const categoryOwnerList = ref([]);
const categoryCheck = ref(false);

const { params } = useRoute();
const myRouoter = useRouter();
const goHome = () => myRouoter.push({ name: "Home" });

const db = "http://localhost:5000/booking";
// const categoryLink = `${import.meta.env.BASE_URL}api/categories`;
// const refreshLink = `${import.meta.env.BASE_URL}api/users/refresh`;
// const categoryOwnerLink = `${import.meta.env.BASE_URL}api/mappings`;
const categoryLink = "http://localhost:8443/api/categories";
const refreshLink  = "http://localhost:8443/api/users/refresh";
const categoryOwnerLink  = "http://localhost:8443/api/mappings";

const userRole = ref('guest')
const checkRole = () => {
    const role = localStorage.getItem('role')
    if(role !== null){
        if(role.substring(6,role.length-1)=='admin') userRole.value = 'admin'
        else if(role.substring(6,role.length-1)=='lecturer') userRole.value = 'lecturer'
        else if(role.substring(6,role.length-1)=='student') userRole.value = 'student'
    }
    else userRole.value = 'guest'
    return userRole.value
}

//GET category
const getCategory = async () => {
  const key = localStorage.getItem('key')
  const res = await fetch(categoryLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    categoryList.value = await res.json();
  } else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
    console.log('test...')
    const resForRefresh = await fetch(refreshLink, {
      headers: {
        Authorization: "Bearer " + key,
        isRefreshToken: true ,
      },
    })
      const jwt = await resForRefresh.json()
      console.log(jwt)
      if(resForRefresh.status === 200){
        // set localStorage
        localStorage.setItem('key',jwt.token)
        localStorage.setItem('token','refreshToken')
        getCategory()
      }
    }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        // console.log('เข้า')
    }
};

//GET category owner
// const getCategoryOwner = async () => {
//   const key = localStorage.getItem('key')
//   const res = await fetch(categoryOwnerLink, {
//     method: "GET",
//     headers: {
//             "Authorization":'Bearer ' + key ,
//             "Accept": 'application/json',
//             "content-type": "application/json",
//         }
//   });
//   if (res.status === 200) {
//     categoryOwnerList.value = await res.json();
//   } else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
//     console.log('test...')
//     const resForRefresh = await fetch(refreshLink, {
//       headers: {
//         Authorization: "Bearer " + key,
//         isRefreshToken: true ,
//       },
//     })
//       const jwt = await resForRefresh.json()
//       console.log(jwt)
//       if(resForRefresh.status === 200){
//         // set localStorage
//         localStorage.setItem('key',jwt.token)
//         localStorage.setItem('token','refreshToken')
//         getCategory()
//         getCategoryOwner()
//         console.log(categoryOwnerList.value)
//       }
//     }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
//         localStorage.removeItem('key')
//         localStorage.removeItem('token')
//         goHome()
//         // console.log('เข้า')
//     }
// };

//router
const myRouter = useRouter();
const goCategories= (input) =>
  myRouter.push({
    name: "Categories",
    params: {
      id: input.id,
    },
  });

onBeforeMount(async () => {
  await getCategory();
  // await getCategoryOwner()
  checkRole()
  console.log(categoryList.value)
});
onUpdated(async () => {
  await getCategory();
  // await getCategoryOwner()
});

</script>

<template>
<body>
    <!-- not have permissions -->
    <div v-if="userRole!=='admin' && userRole!=='lecturer'" class="container py-4 py-xl-5" style="margin-bottom: 0px;">
        <h1 class="text-center">Categories</h1>
        <p class="fs-4 text-center">Only admins and lecturers can view this page.</p>
    </div>

    <!-- have permissions -->
    <div v-if="userRole=='admin' || userRole=='lecturer'" class="container py-4 py-xl-5" style="margin-bottom: 0px;">
        <h1 class="text-center">Categories</h1>
        <p class="text-center">The total of Clinic are {{ categoryList.length }} categories</p>
    </div>

    <!-- have clinic -->
    <div v-if="categoryList.length !== 0 && (userRole=='admin' || userRole=='lecturer')" class="container py-4 py-xl-5" style="margin-top: -64px;">
        <div class="row gy-4 row-cols-1 row-cols-md-2 row-cols-xl-3" style="padding-left: 50px;padding-right: 50px;margin-top: 0px;">
            <div v-for="cat in categoryList" :key="cat.id" class="col-lg-3">
                <div>
                  <img class="rounded img-fluid d-block w-100 fit-cover" style="height: 200px;" src="../assets/clinic/3.png">
                    <div class="py-4">
                        <h4> {{ cat.eventCategoryName }} </h4>
                        <!-- owner -->
                        <h6> Owner :  <span class="text-base"> {{ cat.eventCategoryDescription }} </span> </h6>
                        
                        <!-- description -->
                        <h6> Description :  <span> {{ cat.eventCategoryDescription }} </span> </h6>
                        
                        <!-- duration -->
                        <h6> Duration : <span> {{ cat.eventCategoryDuration }} Min. </span> </h6>
                        <button class="btn btn-primary" @click="goCategories(cat)">More Detail</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</template>

<style scoped>
/* slice bar */
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

/* filter button */
.custom-btn {
  width: 130px;
  height: 40px;
  padding: 10px 25px;

  font-family: "Lato", sans-serif;
  font-weight: 500;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  display: inline-block;
}

/* search */
.search {
  box-shadow: -7px -7px 20px 0px #00000011, -4px -4px 5px 0px #00000011,
    7px 7px 20px 0px #00000011, 4px 4px 5px 0px #00000011;
}
.search:hover {
  transition: all 0.2s ease-in-out;
  background-color: rgb(58, 58, 58);
  color: white;
}

/* reset */
.reset {
  box-shadow: -7px -7px 20px 0px #00000011, -4px -4px 5px 0px #00000011,
    7px 7px 20px 0px #00000011, 4px 4px 5px 0px #00000011;
}
.reset:hover {
  transition: all 0.2s ease-in-out;
  background-color: rgb(90, 26, 26);
  color: white;
}
/* alert */
.alert-area {
  position: fixed;
  top: 100px;
  right: 0;
  background-color: transparent;
  width: 23%;
  height: 10%;
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
