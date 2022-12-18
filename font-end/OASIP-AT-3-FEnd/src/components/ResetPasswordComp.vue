<script setup>
import { computed } from "@vue/reactivity";
import { onBeforeMount, onUpdated, ref } from "vue";
import { useRoute,useRouter } from "vue-router";
import Swal from 'sweetalert2'

const { params } = useRoute();
const myRouoter = useRouter();
const goHome = () => myRouoter.push({ name: "Home" });
const goBooking = () => myRouoter.push({ name: "Booking" });
const goSignup = () => myRouoter.push({ name: "CreateUser" });
const goSignin = () => myRouoter.push({ name: "Login" });

// //for vm
const forLink = '${import.meta.env.BASE_URL}'
//for localhost
// const forLink = 'http://localhost:8443/'
const getTokenLink = `${forLink}api/users/get_token`;
const confirmResetPasswordLink = `${forLink}api/users/reset_password`;

const haveKey = ref(null)
const isHaveKey = () => {
    const key = localStorage.getItem('key')
    if(key==null || key==undefined) haveKey.value = false
    else if(key!==null && key!==undefined) haveKey.value = true
    return haveKey.value
}

// About password
const  confirmPasswordLength = 14 ;
const passwordLength = 14 ;
const password = ref("");
const confirmPassword = ref("");

const showPassword = ref(false);
const toggleShow = () => {
    showPassword.value = !showPassword.value;
};
const showConfirmPassword = ref(false);
const toggleShowConfirm = () => {
    showConfirmPassword.value = !showConfirmPassword.value;
};

const confirmResetPassword = async () => {
  // console.log('เข้า method confirm')
  const res = await fetch(`${confirmResetPasswordLink}?token=${token.value}`, {
    method: "PUT",
    headers: {
      "content-type": "application/json",
    },
    body: JSON.stringify({
      password: password.value
    }),
  });
  
  if (res.status === 200) {
    password.value = ""
    confirmPassword.value = ""
    Swal.fire('Reset Password Successful!', 'The password has been changed successfully.','success')
    localStorage.removeItem('token_password')
    goSignin()

  } else {
    Swal.fire('Error!', 'Sorry, try again','error')
  }
};

const token = ref("")

onBeforeMount(async ()=>{
  token.value =  localStorage.getItem('token_password')
  console.log(token.value)
})

</script>
 
<template>
<body>
    <!-- step 2 : enter new password -->
    <!-- <section v-if="isEnterEmail== true" class="position-relative py-4 py-xl-5"> -->
    <section class="position-relative py-4 py-xl-5">
        <div class="container">
            <div class="row mb-5" style="margin-bottom: 26px;">
                <div class="col-md-6 col-lg-8 col-xl-6 text-center mx-auto">
                    <h2>Reset Password</h2>
                    <p class="w-lg-50">Fill up the form below to reset password.</p>
                </div>
            </div>
            <div class="row d-flex justify-content-center" style="margin-top: -35px;">
                <div class="col-md-6 col-lg-8 col-xl-6">
                    <div class="card mb-5" style="background: #eef0f2; padding-bottom: 0px;">
                        <div class="card-body d-flex flex-column align-items-center" style="margin-bottom: 0px;padding-bottom: 0px;padding-top: 0px;padding-right: 50px;padding-left: 50px;">
                            <div class="bs-icon-xl bs-icon-circle bs-icon-semi-white bs-icon my-5">
                              <svg xmlns="http://www.w3.org/2000/svg" width="2em" height="2em" fill="currentColor" viewBox="0 0 16 16" class="bi bi-lock-fill text-dark">
                                    <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2zm3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2z"></path>
                                </svg>
                            </div>
                            <form class="text-start w-75"  style="margin-bottom: 30px;">
                              <!-- new password -->
                              <label class="form-label">New Password :&nbsp;</label>
                              <small class="float-end align-self-end">{{ password.length }}/{{ passwordLength }} charecters</small>

                              <!-- hide password -->
                              <div v-if="!showPassword" class="d-lg-flex align-items-lg-center mb-3">
                                <input v-model="password" class="form-control" type="password" placeholder="enter new password" style="margin-right: 5px;">
                                <svg @click="toggleShow" class="bi bi-eye-slash-fill d-lg-flex justify-content-lg-end" xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" style="font-size: 21px;">
                                    <path d="m10.79 12.912-1.614-1.615a3.5 3.5 0 0 1-4.474-4.474l-2.06-2.06C.938 6.278 0 8 0 8s3 5.5 8 5.5a7.029 7.029 0 0 0 2.79-.588zM5.21 3.088A7.028 7.028 0 0 1 8 2.5c5 0 8 5.5 8 5.5s-.939 1.721-2.641 3.238l-2.062-2.062a3.5 3.5 0 0 0-4.474-4.474L5.21 3.089z"></path>
                                    <path d="M5.525 7.646a2.5 2.5 0 0 0 2.829 2.829l-2.83-2.829zm4.95.708-2.829-2.83a2.5 2.5 0 0 1 2.829 2.829zm3.171 6-12-12 .708-.708 12 12-.708.708z"></path>
                                </svg>
                              </div>

                              <!-- show password -->
                              <div v-else-if="showPassword" class="d-lg-flex align-items-lg-center mb-3">
                                <input v-model="password" class="form-control" type="text" name="password" placeholder="enter password" style="margin-right: 5px;">
                                <svg @click="toggleShow" xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" viewBox="0 0 16 16" class="bi bi-eye-fill d-lg-flex justify-content-lg-end" style="font-size: 21px;">
                                      <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"></path>
                                      <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"></path>
                                </svg>
                              </div>
                                
                              <!-- confirm password -->
                              <label class="form-label">Confirm New Password :</label>
                              <small class="float-end align-self-end">{{ confirmPassword.length }}/{{ confirmPasswordLength }} charecters</small>
                              <!-- hide password -->
                              <div v-if="!showConfirmPassword" class="d-lg-flex align-items-lg-center">
                                <input v-model="confirmPassword" class="form-control" type="password" placeholder="enter confirm password" style="margin-right: 5px;">
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

                              <button class="btn btn-dark d-block w-100" type="button" style="margin-top: 20px;margin-bottom: 10px;padding-top: 10px;padding-bottom: 10px;" data-bs-target="#confirm" data-bs-toggle="modal">Submit</button>
                            </form>
                        </div>
                    </div><button class="btn btn-danger float-end d-lg-flex justify-content-end align-items-end align-content-end align-self-end me-auto" type="button">Back</button>
                </div>
            </div>

            <!-- confirm reset password -->
            <div class="modal fade" role="dialog" tabindex="-1" id="confirm">
                <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header text-bg-warning" style="padding-top: 10px;padding-bottom: 10px;padding-left: 20px;padding-right: 20px;background: var(--bs-yellow);">
                            <h4 class="modal-title fs-3">Are you sure ?</h4><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p class="fs-6">Are you sure to change your password?</p>
                        </div>
                        <div class="modal-footer" style="padding-bottom: 5px;padding-top: 5px;">
                          <button class="btn btn-primary btn-sm" type="button"   data-bs-dismiss="modal" data-bs-target="#" @click="confirmResetPassword">Yes</button>
                          <button class="btn btn-danger btn-sm" type="button"  data-bs-dismiss="modal" data-bs-target="#">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- ---------------------------------------------------------------- -->

      <!-- for alert -->
      <!-- warning alert-->
      <div class="alert-area">
        <div v-if="password == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please enter your password.
        </div>

        <div v-if="(password !== confirmPassword) == true" class="alert warning text-sm">
          <strong class="block">Warning!</strong> The password DOES NOT match' and let the user enter confirm password again.
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