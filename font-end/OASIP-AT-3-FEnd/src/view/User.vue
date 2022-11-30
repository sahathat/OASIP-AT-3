<script setup>
import { useRoute, useRouter } from "vue-router";
import { onBeforeMount, ref , computed} from "vue";
import Swal from 'sweetalert2'

const { params } = useRoute();

const db = "http://localhost:5000/booking";

// //for vm
// const forLink = '${import.meta.env.BASE_URL}'
//for localhost
const forLink = 'http://localhost:8443/'
const userLink = `${forLink}api/userList`;
const refreshLink = `${forLink}api/users/refresh`;


// ขาดเช็คชื่อ-เมล์ซ้ำ
const id = params.id;
const name = ref("");
const email = ref("");
const role = ref("");
const createdOn = ref("");
const updatedOn = ref(undefined);
const userDetail = ref({});
const userList=ref([])

const isNotNull = ref(false);
const myRouoter = useRouter();
const goUserList = () => myRouoter.push({ name: "UserList" });
const goHome = () => myRouoter.push({ name: "Home" });
const goConfirmResetPassword = () => myRouoter.push({ name: "SendEmail" });

const roles = ['admin','lecturer','student']

// timer
const day = ref();
const month = ref();
const year = ref();
const hours = ref();
const minutes = ref();
const date = ref("");
const time = ref("");

let clock = () => {
  let dateToday = new Date();
  day.value =
    dateToday.getDate() < 10
      ? `0${dateToday.getDate()}`.toString()
      : dateToday.getDate(); // day
  month.value =
    dateToday.getMonth() + 1 < 10
      ? `0${dateToday.getMonth() + 1}`.toString()
      : dateToday.getMonth() + 1; // month
  year.value = dateToday.getFullYear().toString(); // year

  hours.value =
    dateToday.getHours() < 10
      ? `0${dateToday.getHours()}`.toString()
      : dateToday.getHours(); // hour
  minutes.value =
    dateToday.getMinutes() < 10
      ? `0${dateToday.getMinutes()}`.toString()
      : dateToday.getMinutes(); // minute

  //  combine
  time.value = `${hours.value}:${minutes.value}`;
  date.value = `${year.value}-${month.value}-${day.value}`;
};
setInterval(clock, 1000);

// get every 10 sec
// const getStatus=ref(undefined)
// const resGetUser=ref(undefined)
// setInterval(async ()=>{
//   const key = localStorage.getItem('key')
//   resGetUser.value= await fetch(userLink, {
//     method: "GET",
//     headers: {
//             "Authorization":'Bearer ' + key ,
//             "Accept": 'application/json',
//             "content-type": "application/json",
//         }
//   })
//   if (resGetUser.value.status === 200) {
//     userList.value = await resGetUser.value.json();
//     getStatus.value = true;
//   }
//     // refresh Token --------------------------------------- //
//   else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
//     console.log('test...')
//     const resForRefresh = await fetch(refreshLink, {
//       headers: {
//         Authorization: "Bearer " + localStorage.getItem('key'),
//         isRefreshToken: true ,
//       },
//     })
//       const jwt = await resForRefresh.json()
//       console.log(jwt)
//       if(resForRefresh.status === 200){
//         // set localStorage
//         localStorage.setItem('key',jwt.token)
//         localStorage.setItem('token','refreshToken')
//         getUser()
//       }
//     }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
//         localStorage.removeItem('key')
//         localStorage.removeItem('token')
//         goHome()
//         console.log('เข้า')
//     }
//     // ------------------------------------------------- //
//      else getStatus.value = false;
// },10000)

//GET users
const getUser = async () => {
  const key = localStorage.getItem('key')

  const res = await fetch(userLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    userList.value = await res.json();
  } 
  // refresh Token --------------------------------------- //
  else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
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
    // ------------------------------------------------- //
};

// get value
const getDetail = async () => {
  const key = localStorage.getItem('key')
  // console.log(key)
  const res = await fetch(`${userLink}/${params.id}`, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    userDetail.value = await res.json();
    //console.log(userDetail.value);
    if (userDetail.value.id == id) {
      isNotNull.value = true;
      name.value = userDetail.value.name;
      email.value = userDetail.value.email;
      role.value = userDetail.value.role
      createdOn.value = userDetail.value.createdOn;
      updatedOn.value = userDetail.value.updatedOn;
      console.log(userDetail.value)
    }
  }
  // refresh token ----------------------------- //
    else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
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
    // ------------------------------------------------- //
};

//format date
const formatDate = (datetime) => { 
  const date = new Date(datetime)
  const month = date.getMonth() + 1
  if(month<10){ return `${date.getDate()}-0${month}-${date.getFullYear()}` }
  else{ return `${date.getDate()}-${month}-${date.getFullYear()}` }
}


//format time
const formatTime = (datetime) => { 
  const time = new Date(datetime)
  const hour = computed(() => {
    // console.log(time.getHours());
    if (time.getHours() < 10) return "0" + time.getHours();
    else return time.getHours();
  });
  const minute = computed(() => {
    // console.log(time.getMinutes());
    if (time.getMinutes() < 10) return "0" + time.getMinutes();
    else return time.getMinutes();
  });
  return `${hour.value}.${minute.value}` 
}


onBeforeMount(async()=>{
       await getUser()
       await getDetail()
});

//remove information
const removeUser = async () => {
  const key = localStorage.getItem('key')

  const res = await fetch(`${userLink}/${id}`, { 
    method: "DELETE",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
    }
  });
  if (res.status === 200) {
    // console.log("delete successfully");
    Swal.fire('Delete Successful !', 'The user has been deleted.','success')
    goUserList();
  }
  // refresh token ----------------------------- //
    else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
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
    // ------------------------------------------------- // 
    else Swal.fire('Delete Fail !', 'Please try again.','error')
    // console.log("error");
};

// assign to edit attribute
const isEdit = ref(false);
const editName = ref("");
const editEmail = ref("");
const editRole = ref("");

const editInfo = () => {
  isEdit.value = true;
  editName.value = name.value;
  editEmail.value = email.value;
  editRole.value = role.value;
};

const cancel = () => {
  isEdit.value = false;
  editName.value = '';
  editEmail.value = '';
  editRole.value = '';
  isEdit.value = false;
};

const edit =async()=>{
       let canEdit=undefined
       const key = localStorage.getItem('key')
        const res = await fetch(`${userLink}/${id}`, {
        method: "PUT",
        headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        },
        body: JSON.stringify({
          name:editName.value,
          email:editEmail.value,
          role:editRole.value,
        }),
      });
      if (res.status === 200) {
        let editUserDetail = await res.json();
        name.value = editUserDetail.name;
        email.value = editUserDetail.email;
        role.value = editUserDetail.role;
        isEdit.value = false;
        canEdit=true
        editSuccess.value=true
        Swal.fire('Edit Successful !', 'The User Information has been modified.','success')
       }
       // refresh token ----------------------------- //
      else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
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
    } else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        console.log('เข้า')
    }
    // ------------------------------------------------- //
    else {
        canEdit=false
        editSuccess.value=false
        Swal.fire('Edit Fail !', 'Please try to edit again.','error')
    }
    return canEdit
}

// submit
const isInput=ref(undefined)
const editSuccess=ref(undefined)

const submitt = async () => {
    edit()
    await getUser()
    await getDetail()
};


// function
const calTime = (hour, minute, addTime) => {
  let sum = undefined;
  let h = hour;
  let m = minute + addTime;
  // console.log(`minute + add time :${m}`)
  // console.log(`this is minute : ${minute} and this addTime : ${addTime}`)
  // console.log(`this is hour : ${h}`)
  if (m >= 60) {
    h = h + 1;
    m = m-60;
  }
  h = h < 10 ? `0${h}` : h;
  m = m < 10 ? `0${m}` : m;

  sum = `${h}:${m}`;
  // console.log(sum)
  return sum;
};
</script>

<template>
<body>
    <div class="container py-4 py-xl-5">
        <div class="row mb-5">
            <div class="col-md-8 col-xl-6 text-center mx-auto">
                <h2>User Information<br></h2>
            </div>
        </div>
        
        <!-- no have data -->
        <div v-if="isNotNull == false" class="row row-cols-md-2 row-cols-xl-3 justify-content-center" style="margin-top: -40px;">
            <div class="col-lg-6">
                <section class="py-4 py-xl-5">
                    <div class="container">
                        <div class="bg-light rounded border-0 border-light d-flex flex-column justify-content-between flex-lg-row p-4 p-md-5">
                            <div class="pb-2 pb-lg-1">
                                <h2 class="fw-bold mb-2">No data of<br> User id : {{id}}</h2>
                                <p class="mb-0"></p>
                            </div>
                            <div class="align-items-center my-2">
                              <a class="btn btn-primary fs-5 m-auto py-2 px-4" role="button" @click="goUserList"> Back </a>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>

        <!-- have data -->
        <div v-else-if="isNotNull == true" class="row row-cols-md-3 row-cols-xl-3 justify-content-center" style="margin-top: -40px;">
            <div class="col-md-7 col-lg-7">
                <div class="card"><img class="card-img-top w-100 d-block fit-cover" style="height: 200px;" src="../assets/maewzomUser.jpg" width="454" height="200">
                    <div class="card-body flex-fill p-4">
                        <p class="text-end text-primary card-text mb-0">ID : {{id}}</p>
                        <h4 class="card-title" style="margin-top: 15px;">Username : {{ name }}</h4>
                        <div class="flex-fill" style="margin-top: 5px;">
                          <strong>E-mail : </strong>
                          <span style="margin-left: 10px;">{{ email }}</span>
                        </div>

                        <div class="flex-fill" style="margin-top: 5px;">
                          <strong>Role:</strong>
                          <span style="margin-left: 10px;">{{ role }}</span>
                        </div>
                        
                        <div style="padding-top: 0px;margin-top: 25px;">
                          <strong>Create On :&nbsp;</strong>
                          <span style="margin-left: 15px;">Date : {{ formatDate(createdOn) }} , Time : {{ formatTime(createdOn) }}</span>
                        </div>
                        <div style="padding-top: 0px;margin-top: 10px;">
                          <strong>Update on :</strong>
                          <span style="margin-left: 15px;">Date : {{ formatDate(updatedOn) }} , Time : {{ formatTime(updatedOn) }}</span>
                        </div>

                        <div class="d-sm-flex d-md-flex d-lg-flex flex-fill justify-content-center justify-content-sm-end justify-content-lg-center" style="margin-top: 20px;margin-bottom: 0px;">
                          <button @click="editInfo" class="btn btn-warning" type="button" style="margin-right: 10px;" data-bs-target="#editUser" data-bs-toggle="modal">Edit</button>
                          <button class="btn btn-danger" type="button" style="margin-right: 10px;"  data-bs-target="#confirmToRemove" data-bs-toggle="modal">Remove</button>
                          <button class="btn btn-secondary" type="button" @click="goUserList">Back</button></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- to edit user profile -->
    <div class="modal fade" role="dialog" tabindex="-1" id="editUser">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header" style="background: #f0ac72;">
                        <h4 class="modal-title">Edit User Information:&nbsp;</h4>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="container position-relative" style="padding-left: 0px;padding-right: 0px;">
                            <div class="row text-start d-flex justify-content-center align-items-center" style="margin-left: 0px;padding-right: 0px;margin-right: 0px;">
                                <div class="col-lg-12">
                                    <div>
                                      <!-- edit username -->
                                        <div style="margin-top: 10px;margin-bottom: 10px;">
                                          <label class="form-label fw-semibold">Username :&nbsp;</label>
                                          <input v-model="editName" type="text" placeholder="name" name="name" class="form-control">
                                        </div>

                                        <!-- edit email -->
                                        <div style="margin-top: 10px;margin-bottom: 10px;">
                                          <label class="form-label fw-semibold">Email :&nbsp;</label>
                                          <input v-model="editEmail" type="text" placeholder="email" name="email" class="form-control">
                                        </div>

                                        <!-- edit role -->
                                        <div style="margin-top: 10px;margin-bottom: 10px;">
                                          <label class="form-label fw-semibold">Role :&nbsp;</label>
                                          <select v-model="editRole" class="form-control">
                                                <optgroup label="Select role">
                                                    <option v-for="(eachRole, index) in roles" :key="index" :value="eachRole"> {{ eachRole }} </option>
                                                </optgroup>
                                          </select>
                                        </div>

                                        <!-- reset password -->
                                        <div style="margin-top: 10px;margin-bottom: 10px;">
                                          <label class="form-label fw-semibold"> Reset Password &nbsp; </label>
                                          <span class="font-thin text-decoration-underline" @click="goConfirmResetPassword" data-bs-dismiss="modal"> click here !</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                      <button class="btn btn-light" type="button" data-bs-dismiss="modal">Cancel</button>
                      <button class="btn btn-primary" type="button" style="background: #20c997;" data-bs-target="#confirmToEdit" data-bs-toggle="modal">Save</button>
                    </div>
                </div>
            </div>
        </div>

    <!-- for confirm to remove event  -->
    <div class="modal fade" role="dialog" tabindex="-1" id="confirmToRemove">
      <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
          <div class="modal-content">
              <div class="modal-header text-bg-warning" style="padding-top: 10px;padding-bottom: 10px;padding-left: 20px;padding-right: 20px;">
                <h4 class="modal-title fs-3">Are you sure ?</h4><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                <p class="fs-6">Are you sure to remove this user?</p>
              </div>
              <div class="modal-footer" style="padding-bottom: 5px;padding-top: 5px;">
                <button class="btn btn-primary btn-sm" type="button" @click="removeUser" data-bs-dismiss="modal" data-bs-target="#">Yes</button>
                <button class="btn btn-danger btn-sm" type="button" data-bs-dismiss="modal" data-bs-target="#">Cancel</button>
              </div>
          </div>
      </div>
    </div>

    <!-- for confirm to edit event  -->
    <div class="modal fade" role="dialog" tabindex="-1" id="confirmToEdit">
      <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
          <div class="modal-content">
              <div class="modal-header text-bg-warning" style="padding-top: 10px;padding-bottom: 10px;padding-left: 20px;padding-right: 20px;">
                <h4 class="modal-title fs-3">Are you sure ?</h4><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                <p class="fs-6">Are you sure to edit this user?</p>
              </div>
              <div class="modal-footer" style="padding-bottom: 5px;padding-top: 5px;">
                <button class="btn btn-primary btn-sm" type="button" @click="submitt" data-bs-dismiss="modal" data-bs-target="#">Yes</button>
                <button class="btn btn-danger btn-sm" type="button" data-bs-dismiss="modal" data-bs-target="#">Cancel</button>
              </div>
          </div>
      </div>
    </div>
</body>
</template>

<style scoped>
.custom-btn {
  width: 100px;
  height: 40px;
  padding: 10px 25px;
  font-family: "Lato", sans-serif;
  font-weight: 500;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  display: inline-block;
  text-align: center;
}
/* back*/
.back {
  background: rgb(115, 115, 115);
  color: #fff;
  line-height: 42px;
  padding: 0;
  border: none;
}
.back:hover {
  background: transparent;
  color: #000;
  box-shadow: -7px -7px 20px 0px #fff9, -4px -4px 5px 0px #fff9,
    7px 7px 20px 0px #0002, 4px 4px 5px 0px #0001;
}
.back:before,
.back:after {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  height: 2px;
  width: 0;
  background: #000;
  transition: 400ms ease all;
}
.back:after {
  right: inherit;
  top: inherit;
  left: 0;
  bottom: 0;
}
.back:hover:before,
.back:hover:after {
  width: 100%;
  transition: 800ms ease all;
}

/* edit */
.edit {
  background: rgb(166, 166, 166);
  color: #fff;
  line-height: 42px;
  padding: 0;
  border: none;
}
.edit:hover {
  background: rgb(252, 140, 252);
  color: #000;
  box-shadow: -7px -7px 20px 0px #fff9, -4px -4px 5px 0px #fff9,
    7px 7px 20px 0px #0002, 4px 4px 5px 0px #0001;
}
.edit:before,
.edit:after {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  height: 2px;
  width: 0;
  background: #000;
  transition: 400ms ease all;
}
.edit:after {
  right: inherit;
  top: inherit;
  left: 0;
  bottom: 0;
}
.edit:hover:before,
.edit:hover:after {
  width: 100%;
  transition: 800ms ease all;
}
.edit-color {
  border-color: rgb(252, 140, 252);
}

/* remove */
.remove {
  background: rgb(166, 166, 166);
  color: #fff;
  line-height: 42px;
  padding: 0;
  border: none;
}
.remove:hover {
  background: rgb(255, 172, 172);
  color: #000;
  box-shadow: -7px -7px 20px 0px #fff9, -4px -4px 5px 0px #fff9,
    7px 7px 20px 0px #0002, 4px 4px 5px 0px #0001;
}
.remove:before,
.remove:after {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  height: 2px;
  width: 0;
  background: #000;
  transition: 400ms ease all;
}
.remove:after {
  right: inherit;
  top: inherit;
  left: 0;
  bottom: 0;
}
.remove:hover:before,
.remove:hover:after {
  width: 100%;
  transition: 800ms ease all;
}

/* scrol bar */

/* width */
::-webkit-scrollbar {
  height: 7.5px;
  width: 9px;
}

/* Track */
::-webkit-scrollbar-track {
  box-shadow: inset 0 0 5px transparent;
  border-radius: 10px;
}

/* Handle */
::-webkit-scrollbar-thumb {
  background: rgb(109, 109, 112);
  border-radius: 10px;
}

/* Handle on hover */
::-webkit-scrollbar-thumb:hover {
  background: #577fbb;
}

/* submit */
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
  opacity: 1;
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

@media screen and (max-width: 700px) {
  .popup2 {
    width: 70%;
  }

  .option {
    width: 20%;
  }
}

/* alert */
.alert-area {
  position: fixed;
  top: 170px;
  right: 0;
  background-color: transparent;
  width: 18%;
  
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
  background-color: #ac00d7;
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
