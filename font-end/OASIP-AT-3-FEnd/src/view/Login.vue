<script setup>
import { computed } from "@vue/reactivity";
import { onBeforeMount, onUpdated, ref } from "vue";
import { useRoute,useRouter } from "vue-router";

const { params } = useRoute();
const myRouoter = useRouter();
const goHome = () => myRouoter.push({ name: "Home" });
const goBooking = () => myRouoter.push({ name: "Booking" });
const goSignup = () => myRouoter.push({ name: "CreateUser" });
const goResetPassword = () => myRouoter.push({ name: "ResetPassword" });

const email = ref("");
const password = ref("");
const statusMessage = ref("");
const status = ref(0)

const db = "http://localhost:5000/booking";
const signinLink = 'http://localhost:8443/api/users/signin';
// const signinLink = `${import.meta.env.BASE_URL}api/users/signin`;

// validate email
const isEmailEmpty = ref(undefined);
const validateEmailValid = () => {
    isEmailEmpty.value = email.value == null || email.value == ''
}

//validate password
const isPasswordEmpty = ref(undefined);
const validationPassword = () => {
    isPasswordEmpty.value = password.value == null || password.value == ''
}

//show or hide password
const showPassword = ref(false);
const toggleShow = () => {
    showPassword.value = !showPassword.value;
};

const cancel = () => {
  email.value = "" ;
  password.value = "" ;
  showPassword.value = false;
  goHome()
};

const LoginUser = () => {
    if (email.value == null || email.value == '' || password.value == null || password.value == '') {
        isPasswordEmpty.value = email.value == null || email.value == ''
        isPasswordEmpty.value = password.value == null || password.value == ''
    } else {
        checkMatchTODB(email.value, password.value)
    }
};

//check password 
const checkMatchTODB = async () => {
    console.log('Sign in..');
    // console.log(email.value)
    // console.log(password.value)
    const res = await fetch(signinLink, {
        method: "POST",
        headers: {
            "content-type": "application/json",
        },
        body: JSON.stringify({ email: email.value, password: password.value })
    })
    console.log(res.status);

    //get token
    const jwt = await res.json()
    
    // set localStorage
    localStorage.setItem('key',jwt.token)
    localStorage.setItem('token','accessToken')
    localStorage.setItem('name',jwt.name)
    localStorage.setItem('email',jwt.email)
    localStorage.setItem('role',jwt.role)

    const role = localStorage.getItem('role')
    console.log(localStorage.getItem('role').substring(6,role.length-1))
    
    status.value = res.status
    statusMessage.value = res.status == 200 ? 'Login Successful !' && goHome() :
        res.status == 401 ? 'Password Incorrect !' :
        res.status == 404 ? 'A user with the specified email DOES NOT exist and allow the user to edit the email' : ''
}




</script>

<template>
  <body>
    <section class="position-relative py-4 py-xl-5">
        <div class="container">
            <div class="row mb-5" style="margin-bottom: 15px;">
                <div class="col-md-8 col-xl-6 text-center mx-auto">
                    <h2>Sign in</h2>
                    <p class="w-lg-50">Fill up the form below to login your account.</p>
                </div>
            </div>
            <div class="row d-flex justify-content-center" style="margin-top: -45px;">
                <div class="col-md-6 col-lg-8 col-xl-6">
                    <div class="card mb-5" style="padding-bottom: 17px; padding-top: 17px; background: #eef0f2;">
                        <div class="card-body d-flex flex-column align-items-center" style="margin-bottom: 0px;padding-bottom: 0px;padding-top: 0px;padding-right: 50px;padding-left: 50px;">
                            <div class="bs-icon-xl bs-icon-circle bs-icon-semi-white bs-icon my-4">
                              <svg xmlns="http://www.w3.org/2000/svg" width="3em" height="3em" fill="currentColor" viewBox="0 0 16 16" class="bi bi-person text-dark">
                                 <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4zm-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10c-2.29 0-3.516.68-4.168 1.332-.678.678-.83 1.418-.832 1.664h10z"></path>
                              </svg>
                            </div>
                            
                            <!-- form log in -->
                            <div class="w-75 text-center" style="margin-bottom: 30px;">
                                <!-- email input -->
                                <div class="mb-3">
                                  <input v-model="email" class="form-control" type="email" name="email" placeholder="Email" 
                                          required :style="[ isEmailEmpty == false ? 'border:red' : '',]">
                                  <!-- <span v-if="email == ''">
                                    enter your email !!
                                  </span> -->
                                </div>

                                <!-- password input -->
                                <div class="d-lg-flex align-items-lg-center mb-3">

                                  <!-- show pw -->
                                  <input v-if="showPassword"
                                         v-model="password"
                                         class="form-control" type="text" name="password" placeholder="Password" style="margin-right: -27px;" 
                                         required :style="[ isPasswordEmpty == false ? 'border:red' : '',]" >
                                  
                                  <!-- hide pw -->
                                  <input v-if="!showPassword"
                                         v-model="password"
                                         class="form-control" type="password" name="password" placeholder="Password" style="margin-right: -27px;" 
                                         required :style="[ isPasswordEmpty == false ? 'border:red' : '',]" >

                                  <!-- to show pw button -->
                                  <span v-if="!showPassword" @click="toggleShow">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" class="bi bi-eye-slash-fill text-dark">
                                        <path d="m10.79 12.912-1.614-1.615a3.5 3.5 0 0 1-4.474-4.474l-2.06-2.06C.938 6.278 0 8 0 8s3 5.5 8 5.5a7.029 7.029 0 0 0 2.79-.588zM5.21 3.088A7.028 7.028 0 0 1 8 2.5c5 0 8 5.5 8 5.5s-.939 1.721-2.641 3.238l-2.062-2.062a3.5 3.5 0 0 0-4.474-4.474L5.21 3.089z"></path>
                                        <path d="M5.525 7.646a2.5 2.5 0 0 0 2.829 2.829l-2.83-2.829zm4.95.708-2.829-2.83a2.5 2.5 0 0 1 2.829 2.829zm3.171 6-12-12 .708-.708 12 12-.708.708z"></path>
                                    </svg>
                                  </span>
                                  
                                  <!-- to hide pw button -->
                                  <span v-if="showPassword" @click="toggleShow">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" class="bi bi-eye-fill text-dark">
                                        <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"></path>
                                        <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"></path>
                                    </svg>
                                  </span>
                                </div>
                                <!-- <span v-if="password == ''" > enter your password !! </span> -->

                                <!-- forgot pw link -->
                                <div class="mb-3">
                                    <a><p class="fs-6 fw-lighter text-muted font-italic" @click="goResetPassword"> Forgot your password? </p></a>
                                    
                                    <!-- submit button -->
                                    <button class="btn btn-dark d-block w-100" style="margin-bottom: 16px;padding-top: 10px;padding-bottom: 10px;" data-bs-target="#submit" data-bs-toggle="modal">
                                      Login
                                    </button>

                                    <!-- login with ms -->
                                    <button class="btn btn-primary d-block w-100" style="margin-bottom: 16px;padding-top: 10px;padding-bottom: 10px;">
                                        Login with Microsoft
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- back button -->
                    <button @click="cancel()" class="btn btn-danger float-end d-lg-flex justify-content-end align-items-end align-content-end align-self-end me-auto" 
                            type="button" style="margin-top: -20px">
                      Back
                    </button>
                </div>
            </div>
        </div>
    </section>

    <!-- for submit  -->
    <div class="modal fade" role="dialog" tabindex="-1" id="submit">
      <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
          <div class="modal-content">
              <div class="modal-header text-bg-warning" style="padding-top: 10px;padding-bottom: 10px;padding-left: 20px;padding-right: 20px;">
                <h4 class="modal-title fs-3">Are you sure ?</h4><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                <p class="fs-6">Are you sure to sign in ?</p>
              </div>
              <div class="modal-footer" style="padding-bottom: 5px;padding-top: 5px;">
                <button class="btn btn-primary btn-sm" type="button" @click="checkMatchTODB()" data-bs-dismiss="modal" data-bs-target="#">Yes</button>
                <button class="btn btn-danger btn-sm" type="button" data-bs-dismiss="modal" data-bs-target="#">Cancel</button>
              </div>
          </div>
      </div>
    </div>

      <!-- for alert -->
      <!-- warning alert-->
      <div class="alert-area">
        <div v-if="email == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input your email.
        </div>

         <div v-if="validateEmailValid == false" class="alert text-sm">
           <span class="closebtn" @click="validateEmailValid = true">x</span> 
         </div>

        <!-- <div v-if="valEmail(email) == false" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Invalid email address!.
        </div> -->

        <div v-else-if="isEmailEmpty == true" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input your email.
        </div>

        <!-- <div v-if="validateRoleisNotNull == false" class="alert text-sm"> -->
          <!-- <span class="closebtn" @click="validateRoleisNotNull = true">x</span> -->
        <div v-if="password == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please enter your password.
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