<script setup>
import { computed } from "@vue/reactivity";
import { onBeforeMount, onUpdated, ref } from "vue";
import { useRoute,useRouter } from "vue-router";
import Swal from 'sweetalert2'

const { params } = useRoute();
const myRouoter = useRouter();
const goSignin = () => myRouoter.push({ name: "Login" });
const goHome = () => myRouoter.push({ name: "Home" });

const name = ref("");
const email = ref("");
const role = ref("");
const password = ref("");
const confirmPassword = ref("");

const  confirmPasswordLength = 14 ;
const passwordLength = 14 ;

const nameLength = 100;
const emailLength = 50;

const showPassword = ref(false);
const toggleShow = () => {
    showPassword.value = !showPassword.value;
};
const showConfirmPassword = ref(false);
const toggleShowConfirm = () => {
    showConfirmPassword.value = !showConfirmPassword.value;
};

//validate password
const isPasswordEmpty = ref(undefined);
const validationPassword = () => {
    isPasswordEmpty.value = password.value == null || password.value == ''
}

const db = "http://localhost:5000/booking";
// //for vm
const forLink = '${import.meta.env.BASE_URL}'
//for localhost
// const forLink = 'http://localhost:8443/'
const userLink= `${forLink}api/userList`;
const signupLink= `${forLink}api/users/signup`;



const userList = ref([]);
const roles = ['student','lecturer']

const addSuccess = ref(undefined);
const getStatus = ref(undefined);

// validate name
const validateNameisNotNull = ref(undefined);
const validateNameLength = ref(undefined);
const validateNameUnique = ref(false)

// validate email
const validateEmailisNotNull = ref(undefined);
const validateEmailLength = ref(undefined);
const validateEmailUnique = ref(false)
const validateEmailValid = ref(undefined);
const valEmail = (input) => {
  let valid =
    /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
  if (input.match(valid)) {
    return true;
  } else {
    return false;
  }
};

const checkUnique = () => {
    validateNameUnique.value = userList.value.map((user) =>{return user.name.trim()}).includes(name.value.trim())
    validateEmailUnique.value = userList.value.map((user) =>{return user.email.trim()}).includes(email.value.trim())
    // console.log(validateNameUnique.value);
    // console.log(validateEmailUnique.value);
}

// validate role
const validateRoleisNotNull = ref(undefined);

// validate password
const validatePassword = () => {
  if(password.value == ''|| password.value == null) { return false; }
}
const checkPassword = () => {
  if(password.value !== confirmPassword.value) { return false; }
}

const cancel = () => {
  name.value = "" ;
  email.value = "" ;
  role.value = "" ;
  password.value = "";
  confirmPassword.value = "";
  goHome()
};

// submit
const submitt = async () => {
  if (
    name.value !== "" &&
    email.value !== "" &&
    role.value !== "" &&
    password.value !== "" &&
    confirmPassword .value !== ""
  ) {
    if (name.value.length > nameLength) {
      validateNameLength.value = false;
      //alert('The number of characters name is exceeded.')
    } else if (valEmail(email.value) == false) {
      validateEmailValid.value = false;
      //alert('Invalid email address!')
    } else if (email.value.length > emailLength) {
      validateEmailLength.value = false;
      //alert('The number of characters in the email exceeded the limit.')

    } else if (valEmail(email.value) == false) {
      validateEmailValid.value = false;
      //alert('Invalid email address!')
    
    } 
    else if (createUser()) {
        name.value = "";
        email.value = "";
        role.value = "";
        password.value = "";
        confirmPassword.value = "";
        // validateEmailisNotNull.value = undefined;
        // validateNameisNotNull.value = undefined;
        // validateRoleisNotNull.value = undefined;
        // validateEmailLength.value = undefined;
        // validateNameLength.value = undefined;
        // validateEmailValid.value = undefined;
        // addSuccess.value = undefined;
        // addSuccess.value = true;
    }
    } else {
    // alert('Please complete the information.')
    if (name.value == "") {
      validateNameisNotNull.value = false;
    }
    if (email.value == "") {
      validateEmailisNotNull.value = false;
    }
    if (role.value == "") {
      validateRoleisNotNull.value = false;
    }
  }
  console.log(addSuccess.value)
  console.log(isStatus.value)
};

// fetch create
const statusMessage = ref("");
const status = ref(0)
const isStatus = ref(undefined);
const createUser = async () => {
  let createStatus = undefined;
    console.log(name.value)
  const res = await fetch(signupLink, {
    method: "POST",
    headers: {
      "content-type": "application/json",
    },
    body: JSON.stringify({
      name: name.value,
      email: email.value,
      role: role.value,
      password:password.value
    }),
  });
  
  if (res.status === 201) {
    status.value = 201
    addSuccess.value = true;
    createStatus = true;
    // statusMessage.value = 'Create new user success'
    isStatus.value = true;
    Swal.fire('Sign up Successful !', 'Please, verify your email address to continue','success')

    // console.log(addSuccess.value)
    setTimeout(() => (addSuccess.value = false), 5000);
  } else if (res.status === 400) {
    Swal.fire('Sign up Fail !', `This name is exist`,'error')
  } else {
    status.value = res.status
    addSuccess.value = false;
    createStatus = false;
    isStatus.value = false;
    Swal.fire('Sign up Fail !', 'Sorry, try to sign up again','error')
  }
  return createStatus;
};

//GET user
const getUser = async () => {
  const key = localStorage.getItem('key')
  const res = await fetch(userLink, {
    method: "GET",
    headers: {
          "Authorization":'Bearer ' + key ,
          "Accept": 'application/json',
          "content-type": "application/json",
    }});
  if (res.status === 200) {
    userList.value = await res.json();
    getStatus.value = true;
  } else {
    getStatus.value = false;
  }
}

const resGetUser = ref(undefined);
// check every 10 second
setInterval(async () => {
  //console.log(countGetEvent.value++)
  const key = localStorage.getItem('key')
  resGetUser.value = await fetch(userLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json"
  }});
  if (resGetUser.value.status === 200) {
    userList.value = await resGetUser.value.json();
    getStatus.value = true;
  } else getStatus.value = false;
}, 10000);

onBeforeMount(async () => {
  await getUser();
});

</script>

<template>
<body>
  <section class="position-relative py-4 py-xl-5" style="margin-top: -20px;">
        <div class="container">
            <div class="row mb-5">
                <div class="col-md-8 col-xl-6 text-center mx-auto">
                    <h2>Sign up</h2>
                    <p class="w-lg-50">Fill up the form below to create your account.<br></p>
                </div>
            </div>
            <div class="row d-flex justify-content-center" style="margin-top: -45px;">
                <div class="col-md-8 col-lg-8 col-xl-6 col-xxl-6">
                    <div class="card mb-5">
                        <div class="card-body d-flex flex-column align-items-center" style="padding-top: 30px;padding-bottom: 30px;">
                            <form class="text-start" style="width: 300px;">

                              <!-- name input -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold" style="margin-bottom: -5px;">Full Name :</label>
                                  <small class="float-end align-self-end"> {{ name.length }}/{{ nameLength }} charecters </small>
                                  <input v-model="name" class="form-control" type="text" name="name" placeholder="Somchai Jaidee (AT-3)" required="">
                                </div>

                              <!-- email input -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold" style="padding-bottom: 0px;margin-bottom: 0px;">Email :</label>
                                  <small class="float-end align-self-end"> {{ email.length }}/{{ emailLength }} charecters </small>
                                  <input v-model="email" class="form-control" type="email" name="email" placeholder="somchai.jai@mail.kmutt.ac.th" required="">
                                </div>

                                <!-- role input -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold" style="margin-bottom: 0px;">Role :</label>
                                  <select v-model="role" class="form-select" required="">
                                        <optgroup label="Select your role">
                                            <option v-for="(role,index) in roles" :key="index" :value="role"> {{ role }} </option>
                                        </optgroup>
                                    </select></div>

                                <!-- password input -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold">Password :</label>
                                  <small class="float-end align-self-end">{{ password.length }}/{{ passwordLength }} charecters</small>
                                  
                                  <!-- hide password -->
                                  <div v-if="!showPassword" class="d-lg-flex align-items-lg-center">
                                    <input v-model="password" class="form-control" type="password" placeholder="enter password" style="margin-right: 5px;">
                                    <svg @click="toggleShow" class="bi bi-eye-slash-fill d-lg-flex justify-content-lg-end" xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" style="font-size: 21px;">
                                        <path d="m10.79 12.912-1.614-1.615a3.5 3.5 0 0 1-4.474-4.474l-2.06-2.06C.938 6.278 0 8 0 8s3 5.5 8 5.5a7.029 7.029 0 0 0 2.79-.588zM5.21 3.088A7.028 7.028 0 0 1 8 2.5c5 0 8 5.5 8 5.5s-.939 1.721-2.641 3.238l-2.062-2.062a3.5 3.5 0 0 0-4.474-4.474L5.21 3.089z"></path>
                                        <path d="M5.525 7.646a2.5 2.5 0 0 0 2.829 2.829l-2.83-2.829zm4.95.708-2.829-2.83a2.5 2.5 0 0 1 2.829 2.829zm3.171 6-12-12 .708-.708 12 12-.708.708z"></path>
                                    </svg>
                                  </div>

                                  <!-- show password -->
                                  <div v-else-if="showPassword" class="d-lg-flex align-items-lg-center">
                                    <input v-model="password" class="form-control" type="text" name="password" placeholder="enter password" style="margin-right: 5px;">
                                    <svg @click="toggleShow" xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" class="bi bi-eye-fill d-lg-flex justify-content-lg-end" style="font-size: 21px;">
                                            <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"></path>
                                            <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"></path>
                                    </svg>
                                  </div>
                                </div>

                                <!-- confirm password input -->
                                <div class="mb-3">
                                  <label class="form-label fw-semibold">Confirm Password :</label>
                                  <small class="float-end align-self-end">{{ confirmPassword.length }}/{{ confirmPasswordLength }} charecters</small>
                                  <!-- hide password -->
                                  <div v-if="!showConfirmPassword" class="d-lg-flex align-items-lg-center">
                                    <input v-model="confirmPassword" class="form-control" type="password" placeholder="enter password" style="margin-right: 5px;">
                                    <svg @click="toggleShowConfirm" class="bi bi-eye-slash-fill d-lg-flex justify-content-lg-end" xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" style="font-size: 21px;">
                                        <path d="m10.79 12.912-1.614-1.615a3.5 3.5 0 0 1-4.474-4.474l-2.06-2.06C.938 6.278 0 8 0 8s3 5.5 8 5.5a7.029 7.029 0 0 0 2.79-.588zM5.21 3.088A7.028 7.028 0 0 1 8 2.5c5 0 8 5.5 8 5.5s-.939 1.721-2.641 3.238l-2.062-2.062a3.5 3.5 0 0 0-4.474-4.474L5.21 3.089z"></path>
                                        <path d="M5.525 7.646a2.5 2.5 0 0 0 2.829 2.829l-2.83-2.829zm4.95.708-2.829-2.83a2.5 2.5 0 0 1 2.829 2.829zm3.171 6-12-12 .708-.708 12 12-.708.708z"></path>
                                    </svg>
                                  </div>

                                  <!-- show password -->
                                  <div v-else-if="showConfirmPassword" class="d-lg-flex align-items-lg-center">
                                    <input v-model="confirmPassword" class="form-control" type="text" name="password" placeholder="enter password" style="margin-right: 5px;">
                                    <svg @click="toggleShowConfirm" xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" class="bi bi-eye-fill d-lg-flex justify-content-lg-end" style="font-size: 21px;">
                                            <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"></path>
                                            <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"></path>
                                    </svg>
                                  </div>
                                </div>

                                <div class="mb-3">
                                  <button class="btn btn-primary d-block w-100" type="button" style="margin-bottom: 15px;margin-top: 25px;" data-bs-target="#confirm" data-bs-toggle="modal" @click="checkUnique">Submit</button>
                                  <button class="btn btn-light d-block w-100" type="button" @click="cancel">Cancel</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- confirm -->
        <div class="modal fade" role="dialog" tabindex="-1" id="confirm">
            <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header text-bg-warning">
                        <h4 class="modal-title fs-3">Are you sure ?</h4>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>Are you sure to create new user ?</p>
                    </div>
                    <div class="modal-footer">
                      <button class="btn btn-primary" type="button" @click="submitt" data-bs-dismiss="modal" data-bs-target="#" >Yes</button>
                      <button class="btn btn-danger" type="button" data-bs-dismiss="modal">Cancel</button>
                    </div>
                </div>
            </div>
        </div>
    </section>

  <!-- -------------------------------------------------------------------------------------------------------------------------------- -->

      <!-- for alert -->  
      <div class="alert-area">
    
        <!-- add success alert-->
        <div v-if="addSuccess == true" class="alert success text-sm">
          <!-- <span class="closebtn" @click="goUserList">x</span> -->
          <strong class="block">Success!</strong> create new account success.
        </div>
        
        <!-- warning alert-->
        <!-- <div v-if="validateNameisNotNull == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateNameisNotNull = true">x</span> -->
        <div v-if="name == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input your name.
        </div>

        <!-- <div v-if="validateNameLength == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateNameLength = true">x</span> -->
        <div v-else-if="name.length > nameLength" class="alert warning text-sm">
          <strong class="block">Warning!</strong> The number of characters name is
          exceeded.
        </div>

        <div v-else-if="validateNameUnique" class="alert warning text-sm">
          <strong class="block">Warning!</strong> This name is already exists.
        </div>

        <!-- <div v-if="validateEmailisNotNull == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateEmailisNotNull = true">x</span> -->
        <div v-if="email == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input your email.
        </div>

        <!-- <div v-if="validateEmailValid == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateEmailValid = true">x</span> -->
        <div v-if="valEmail(email) == false" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Invalid email address!.
        </div>

        <!-- <div v-if="validateEmailLength == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateEmailLength = true">x</span> -->
        <div v-if="email.length > emailLength " class="alert warning text-sm">
          <strong class="block">Warning!</strong> The number of characters email
          is exceeded.
        </div>

        <div v-else-if="validateEmailUnique" class="alert warning text-sm">
          <strong class="block">Warning!</strong> This email is already exists.
        </div>

        <!-- <div v-if="validateRoleisNotNull == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateRoleisNotNull = true">x</span> -->
        <div v-if="role == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please select role.
        </div>

        <!-- password alert alert -->
        <!-- <div v-if="validatePasswordValid == false" class="alert warning text-sm"> -->
        <div v-if="(password == ''|| password == null) == true" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input valid password.
        </div>
        <!-- <div v-if="checkPassword == false" class="alert warning text-sm"> -->
        <div v-if="(password !== confirmPassword) == true" class="alert warning text-sm">
          <strong class="block">Warning!</strong> The password DOES NOT match' and let the user enter confirm password again.
        </div>

        <div v-if="password.length < 8 || password.length > 14" class="alert warning text-sm">
          <strong class="block">Warning!</strong> The password must have between 8 and 14 characters.
        </div>

        

        <!-- add error alert-->
        <div v-if="status !== 201 && status !== 0" class="alert text-sm">
          <!-- <span class="closebtn" @click="cancel">x</span> -->
          <strong class="block">Error!</strong> Cannot create new user.
        </div>
      </div>

</body>
</template>

<style scoped>
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

/* for notification */
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

.popup {
  margin: 120px auto;
  padding: 20px;
  background: #fff;
  border-radius: 5px;
  width: 70%;
  position: relative;
  transition: all 5s ease-in-out;
}

.popup h2 {
  margin-top: 0;
  color: #333;
}

.popup .close {
  position: absolute;
  top: 20px;
  right: 30px;
  transition: all 200ms;
  font-size: 30px;
  font-weight: bold;
  text-decoration: none;
  color: #333;
}

.popup .close:hover {
  color: #06d85f;
}

@media screen and (max-width: 700px) {
  .popup {
    width: 70%;
  }
}

/* submit */
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
  top: 150px;
  right: 0;
  background-color: transparent;
  width: 23%;
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
