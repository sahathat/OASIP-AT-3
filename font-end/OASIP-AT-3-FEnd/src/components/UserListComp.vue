<script setup>
import { ref, onBeforeMount,onUpdated } from "vue";
import { useRoute,useRouter } from "vue-router";

const userList = ref([]);
const db = "http://localhost:5000/booking";
// //for vm
const forLink = '${import.meta.env.BASE_URL}'
//for localhost
// const forLink = 'http://localhost:8443/'
const userLink = `${forLink}api/userList`;
const refreshLink =  `${forLink}api/users/refresh`;

const { params } = useRoute();
const myRouter = useRouter();
const goHome = () => myRouter.push({ name: "Home" });
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
const goUserInfo= (input) =>
  myRouter.push({
    name: "UserInfo",
    params: {
      id: input.id,
    },
  });

const userRole = ref('guest')
const checkRole = () => {
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
<body>
  <div v-if="userRole=='admin'">
    <div class="row" style="margin-top: 15px;margin-bottom: 15px;">
        <div class="col-lg-12 col-xxl-12 justify-content-center align-items-center">
            <div class="container py-4 py-xl-5" style="margin-bottom: 0px;">
                <h1 class="text-center">User List</h1>
                <p class="text-center" style="margin-left: 15px;">The total of users are {{ userList.length }} users
                <img @click="goCreateUser" src="../assets/add-user.png" type="button" width="40" height="40" style="margin-top: -15px; margin-left: 15px;"></p>
            </div>
        </div>
    </div>
    <div class="table-responsive text-center flex-fill" style="padding-left: 40px;padding-right: 40px;margin-top: -15px;">
        <table class="table">
            <thead>
                <tr>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>MORE DETAIL</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="user in userList" :key="user.id">
                    <td>{{ user.name.substring(0,30) }}<br>
                        {{ user.name.substring(30,60) }}
                    </td>
                    <td>{{ user.email }}</td>
                    <td>{{ user.role }}</td>
                    <td><button class="btn btn-primary" type="button" @click="goUserInfo(user)">Detail</button>&nbsp;</td>
                </tr>
                <tr></tr>
            </tbody>
        </table>
    </div>
    <div class="row">
        <div class="col" style="margin-right: 50px;">
          <i class="fas fa-user-plus text-start float-end m-auto" style="font-size: 60px;margin-top: -20px;margin-right: 30px;" @click="goCreateUser"></i>
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
