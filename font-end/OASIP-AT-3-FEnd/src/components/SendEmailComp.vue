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
const resetPasswordLink = `${forLink}api/users/verify`;
const getTokenLink = `${forLink}api/users/get_token`;

//About email
const emailLength = 50;
const email = ref("");
const confirmStatus = ref(undefined);
const isEnterEmail = ref(false);

//token 
const token = ref("");
const getToken = async () => {
  email.value = localStorage.getItem('email_password')
  console.log(email.value)
  const res = await fetch(getTokenLink, {
    method: "POST",
    headers: {
      "content-type": "application/json",
    },
    body: JSON.stringify({
      email: email.value
    }),
  })
  if (res.status === 200) {
    //get token
    const uuid = await res.json()
    // set localStorage
    localStorage.setItem('token_password',uuid)

    console.log(uuid)
    console.log(localStorage.getItem('email_password'))

  } else {
    // isEnterEmail.value = false
    // Swal.fire('Error!', 'Sorry, try to enter your email again','error')
  };
}

const sendEmail = async () => {
  console.log('เข้า method')
  // const key = localStorage.getItem('key')
  // console.log(key)
  const res1 = await fetch(resetPasswordLink, {
    method: "POST",
    headers: {
      // "Authorization":'Bearer ' + key ,
      // "Accept": 'application/json',
      "content-type": "application/json",
    },
    body: JSON.stringify({
      email: email.value
    }),
  });

  if (res1.status === 200) {
    // isEnterEmail.value = true
    Swal.fire('Email has been sent.', 'Please check your mail and confirm to reset password continue','success')

    const res2 = await fetch(getTokenLink, {
     method: "POST",
     headers: {
      "content-type": "application/json",
     },
     body: JSON.stringify({
      email: email.value
     }),
    })
    if (res2.status === 200) {
    //get token
    const uuid = await res2.text()
    console.log(uuid)
    // set localStorage
    localStorage.setItem('token_password',uuid)

    }
  } else {
    // isEnterEmail.value = false
    Swal.fire('Error!', 'Sorry, try to enter your email again','error')
  }

  return confirmStatus;
};

onBeforeMount(async ()=>{
  getToken()
})

</script>
 
<template>
  <!-- step 1 : enter email -->
  <section class="position-relative py-4 py-xl-5">
        <div class="container">
            <div class="row mb-5" style="margin-bottom: 26px;">
                <div class="col-md-8 col-xl-6 text-center mx-auto">
                    <h2>Reset Password</h2>
                    <p class="w-lg-50">Fill up the form below to reset password.</p>
                </div>
            </div>
            <div class="row d-flex justify-content-center" style="margin-top: -35px;">
                <div class="col-md-6 col-lg-8 col-xl-6">
                    <div class="card mb-5" style="padding-bottom: 17px;background: #eef0f2;">
                        <div class="card-body d-flex flex-column align-items-center" style="margin-bottom: 0px;padding-bottom: 0px;padding-top: 0px;padding-right: 50px;padding-left: 50px;">
                            <div class="bs-icon-xl bs-icon-circle bs-icon-semi-white bs-icon mt-5 mb-4">
                              <svg xmlns="http://www.w3.org/2000/svg" width="3em" height="3em" fill="currentColor" viewBox="0 0 16 16" class="bi bi-person text-dark">
                                 <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4zm-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10c-2.29 0-3.516.68-4.168 1.332-.678.678-.83 1.418-.832 1.664h10z"></path>
                              </svg>
                            </div>
                            <form class="text-center w-75" style="margin-bottom: 30px;">
                                <div class="text-start mb-3">
                                  <label class="form-label fw-semibold text-start">Enter your email :&nbsp;</label>
                                  <input v-model="email" class="form-control" type="email" name="email" placeholder="email">
                                </div>
                                <div class="mb-3">
                                  <button class="btn btn-dark d-block w-100" type="button" style="margin-bottom: 16px;padding-top: 10px;padding-bottom: 10px;" @click="sendEmail">Submit</button>
                                </div>
                            </form>
                        </div>
                    </div>
                    <button class="btn btn-danger float-end d-lg-flex justify-content-end align-items-end align-content-end align-self-end me-auto" type="button" @click="goHome">Back</button>
                </div>
            </div>
        </div>
    </section>

    <!-- for alert -->
    <!-- warning alert-->
    <div class="alert-area">
        <div v-if="email == ''" class="alert warning text-sm">
          <strong class="block">Warning!</strong> Please input your email.
        </div>
    </div>

</template>
 
<style scoped>
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
</style>