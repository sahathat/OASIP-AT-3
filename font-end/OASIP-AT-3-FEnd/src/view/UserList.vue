<script setup>
import { ref, onBeforeMount,onUpdated } from "vue";
import { useRoute,useRouter } from "vue-router";

const userList = ref([]);
const db = "http://localhost:5000/booking";
// const userLink = `${import.meta.env.BASE_URL}api/userList`;
// const refreshLink =  `${import.meta.env.BASE_URL}api/users/refresh`;
const userLink = "http://localhost:8443/api/userList";
const refreshLink = "http://localhost:8443/api/users/refresh";

const { params } = useRoute();
const myRouoter = useRouter();
const goHome = () => myRouoter.push({ name: "Home" });
const goCreateUser = ()=>myRouter.push({name:'CreateUser'})

//GET users
const getUser = async () => {
  const key = localStorage.getItem('key')
  // const user_role = localStorage.getItem('role').substring()

  const res = await fetch(userLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200 ) {
    userList.value = await res.json();
  } else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
    console.log('test...')
    const resForRefresh = await fetch(refreshLink, {
      headers: {
        Authorization: "Bearer " + localStorage.getItem('key'),
        isRefreshToken: true ,
      },
    })
      const jwt = await resForRefresh.json()
      console.log(jwt)
      if(resForRefresh.status === 200){
        // set localStorage
        localStorage.setItem('key',jwt.token)
        localStorage.setItem('token','refreshToken')
        getUser()
      }
    }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        console.log('เข้า')
    }
};

//router
const myRouter = useRouter();
const goUserInfo= (input) =>
  myRouter.push({
    name: "UserInfo",
    params: {
      id: input.id,
    },
  });

const userRole = ref('guest')
const checkRole = () => {
    // const getRole = localStorage.getItem('role')
    // const role = getRole.substring(6,getRole.length-1)
    const role = localStorage.getItem('role')
    // console.log(role.substring(6,role.length-1))
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
  await getUser();
  checkRole()
});
onUpdated(async () => {
  await getUser();
});

</script>

<template>
  <!-- for user table -->
  <div
    class="showUp bg-gray-200 md:inline-block mx-auto mt-5 p-4 rounded-r"
    style="height: 540px; width: 80%"
  >
    <div class="my-auto" v-if="userRole=='admin'">
      <p class="text-right text-lg font-bold text-gray-900">
          The total of users are <span class="text-xl text-red-500">{{ userList.length }}</span> users
        <button class="items-center">
          <img src="../assets/add-user.png" 
              @click="goCreateUser"
              class="w-10">
        </button>
      </p>
    </div>
    <div v-if="userRole!=='admin'">
      <h1 class="drop-shadow-2xl mx-auto w-fit my-20 font-semibold">
        Only admins can view this page.
      </h1>
    </div>

    <div v-if="userList.length === 0 && userRole=='admin'">
      <h1 class="drop-shadow-2xl mx-auto w-fit my-20 font-semibold">
        No user
      </h1>
    </div>
    

    <div
      v-if="userList.length !== 0 && userRole=='admin'"
      class="drop-shadow-2xl bg-white overflow-y-auto mx-auto h-fit"
      style="height: 90%; width: 100%"
    >
      <table class="table-fixed m-auto md:table-flexed w-full">
        <thead
          class="sticky top-0 text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400"
        >
          <tr>
            <th scope="col" class="px-6 py-3">Username</th>
            <th scope="col" class="px-3 py-3 w-2/5">E-mail</th>
            <th scope="col" class="px-6 py-3">Role</th>
            <th scope="col" class="px-6 py-3">more detail</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="user in userList"
            :key="user.id"
            class="text-center border-y border-t-0 dark:border-gray-700"
          >
            <th
              scope="row"
              class="px-6 py-4 text-gray-900 font-bold whitespace-nowrap text-ellipsis overflow-hidden"
            >
              {{ user.name.substring(0,20) }}
              {{ user.name.substring(21,40) }}
            </th>
            <td class="px-1 py-4">
              <div class="block m-auto">
                <span class="px-5 w-full block">
                  {{ user.email }}
                </span>
              </div>
            </td>
            <td class="px-6 py-4 text-ellipsis overflow-hidden">
              {{ user.role }}
            </td>
            <td class="px-14 py-4">
              <button
                @click="goUserInfo(user)"
                class="text-black hover:text-white border border-gray-800 hover:bg-gray-900 focus:ring-4 focus:outline-none focus:ring-gray-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center mr-2 mb-2 dark:border-gray-600 dark:text-gray-400 dark:hover:text-white dark:hover:bg-gray-600 dark:focus:ring-gray-800"
              >
                Detail
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
  </div>
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
