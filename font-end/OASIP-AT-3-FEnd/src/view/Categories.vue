<script setup>
import { useRoute, useRouter } from "vue-router";
import { onBeforeMount, ref } from "vue";
import Swal from 'sweetalert2'

const db = "http://localhost:5000/booking";
// //for vm
// const forLink = '${import.meta.env.BASE_URL}'
//for localhost
const forLink = 'http://localhost:8443/'
const categoryLink = `${forLink}api/categories`;
const refreshLink = `${forLink}api/users/refresh`;


const { params } = useRoute();
const myRouoter = useRouter();
const goHome = () => myRouoter.push({ name: "Home" });
const goCategoriesList = () => myRouoter.push({ name: "CategoriesList" });

// ขาดเช็คชื่อคลิกนิกซ้ำ
const id = params.id;
const name = ref("");
const description = ref("")
const duration = ref("");
const categoryList=ref([])
const isNotNull = ref(false);
const categoryDetail = ref({})

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


// get every 10 sec
// const getStatus=ref(undefined)
// const resGetCategory=ref(undefined)
// setInterval(async ()=>{
//   const key = localStorage.getItem('key')

//   resGetCategory.value= await fetch(categoryLink, {
//     method: "GET",
//     headers: {
//             "Authorization":'Bearer ' + key ,
//             "Accept": 'application/json',
//             "content-type": "application/json",
//         }
//   })
//   if (resGetCategory.value.status === 200) {
//     categoryList.value = await resGetCategory.value.json();
//     getStatus.value = true;
//   } else getStatus.value = false;
// },10000)

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
  } 
  // refresh token ------------------------------------------------------ //
  else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
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
    // ------------------------------------------------------------------- //
};

// get value
const getDetail = async () => {
  const key = localStorage.getItem('key')

  const res = await fetch(`${categoryLink}/${params.id}`, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    categoryDetail.value = await res.json();
    if (categoryDetail.value.id == id) {
      isNotNull.value = true;
      name.value = categoryDetail.value.eventCategoryName;
      description.value = categoryDetail.value.eventCategoryDescription;
      duration.value = categoryDetail.value.eventCategoryDuration;
    }
  } 
  // refresh token ------------------------------------------------------ //
  else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
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
        getDetail()
      }
    }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        // console.log('เข้า')
    }
    // ---------------------------------------------------------------- //

};

onBeforeMount(async()=>{
       await getCategory()
       await getDetail()
       checkRole()
});


// assign to edit attribute
const isEdit = ref(false);
const editCategoryName = ref("")
const editCategoryDuration = ref(1)
const editCategoryDescription = ref("")

const editInfo = () => {
  isEdit.value = true;
  editCategoryName.value = name.value;
  editCategoryDescription.value = description.value;
  editCategoryDuration.value = duration.value;
  // console.log(editCategoryName.value,editCategoryDescription.value,editCategoryDuration.value)
};

const cancel = () => {
  isEdit.value = false;
  editCategoryName.value = "";
  editCategoryDuration.value = 1;
  editCategoryDescription.value = "";
};

//edit category
const edit =async()=>{
      const key = localStorage.getItem('key')

       let canEdit=undefined
       console.log(description)
        const res = await fetch(`${categoryLink}/${id}`, {
        method: "PUT",
        headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        },
        body: JSON.stringify({
          eventCategoryName: editCategoryName.value,
          eventCategoryDescription: editCategoryDescription.value,
          eventCategoryDuration:editCategoryDuration.value,
        }),
      });
      if (res.status == 200) {
        let editCategoryDetail = await res.json();
        name.value = editCategoryDetail.eventCategoryName;
        duration.value = editCategoryDetail.eventCategoryDuration;
        description.value = editCategoryDetail.eventCategoryDescription;
        isEdit.value = false;
        canEdit=true
        editSuccess.value=true
        Swal.fire('Edit Successful !', 'The Category Detail has been modified.','success')
        // console.log(`editSuccess = ${editSuccess.value}`)
      } 
      // refresh token ----------------------------------------------------------- //
      else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
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
              getDetail()
          }
      } else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
          localStorage.removeItem('key')
          localStorage.removeItem('token')
          goHome()
        // console.log('เข้า')
      } 
      // ------------------------------------------------------------------------------------------------- //
      else {
              canEdit=false
              editSuccess.value=false
              Swal.fire('Edit Fail!', 'Please try to edit again.','error')
              // console.log(`editSuccess = ${editSuccess.value}`)
       }
       return canEdit
}

// submit
// const isSuccess=ref(undefined)
const editSuccess=ref()

const submitt = async () => {
    await edit()
    console.log(editSuccess.value)
    getCategory()
};

</script>

<template>
<body>
  <h1 class="text-center" style="margin-top: 30px;margin-bottom: 30px;">Category Detail</h1>
    <!-- no have data -->
    <div v-if="isNotNull == false" class="row row-cols-md-2 row-cols-xl-3 justify-content-center" style="margin-top: -40px;">
      <div class="col-lg-6">
        <section class="py-4 py-xl-5">
          <div class="container">
            <div class="bg-light rounded border-0 border-light d-flex flex-column justify-content-between flex-lg-row p-4 p-md-5">
                <div class="pb-2 pb-lg-1">
                    <h2 class="fw-bold mb-2">No data of<br> Category id : {{id}}</h2>
                    <p class="mb-0"></p>
                </div>
              <div class="align-items-center my-2">
                <a class="btn btn-primary fs-5 m-auto py-2 px-4" role="button" @click="goCategoriesList"> Back </a>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>

    <!-- have data : show category detail  -->
    <div v-else-if="isNotNull == true" class="w-50 card-group justify-content-center align-items-center align-content-center align-self-center my-auto mx-auto" style="margin-left: 100px;margin-right: 100px;margin-bottom: 20px;margin-top: 20px;">
        <div class="card">
            <img class="card-img-top w-100 d-block" width="554" height="100" src="../assets/consult.png">
            <div class="card-body text-center" style="background: #b0f2ff;">
                <h4 class="text-center card-title"> {{ name }} </h4>
                <p class="card-text">Owner :</p>
                <p class="card-text">Description : {{ description }}</p>
                <p class="card-text">Duration : {{ duration }} minutes </p>
                <button class="btn btn-light btn-sm float-end" type="button" style="margin-left: 10px;" @click="goCategoriesList()">Back</button>
                <button class="btn btn-primary btn-sm float-end" type="button" data-bs-target="#editDetail" data-bs-toggle="modal" @click="editInfo">Edit</button>
            </div>
        </div>
    </div>

    <!-- edit details -->
    <div class="modal fade" role="dialog" tabindex="-1" id="editDetail">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header" style="background: #f0ac72;">
                    <h4 class="modal-title">Edit Category Detail :&nbsp;</h4>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                   <div class="container position-relative" style="padding-left: 0px;padding-right: 0px;">
                      <div class="row text-start d-flex justify-content-center align-items-center" style="margin-left: 0px;padding-right: 0px;margin-right: 0px;">
                          <div class="col-lg-12">
                                    <div style="margin-top: 20px;">
                                        <div class="px-2">
                                            <h6 class="mb-0">Clinic Name :&nbsp;</h6>
                                            <input v-model="editCategoryName" type="text" class="form-control" style="margin-top: 5px;">
                                        </div>
                                    </div>
                                    <div style="margin-top: 20px;">
                                        <div class="px-2">
                                            <h6 class="mb-0">Lecturer Owner :&nbsp;</h6>
                                            <input type="text" class="form-control" style="margin-top: 5px;">
                                        </div>
                                    </div>
                                    <div style="margin-top: 20px;">
                                        <div class="px-2">
                                            <h6 class="mb-0">Duration :&nbsp;</h6>
                                            <input type="number" style="margin-top: 5px;" class="form-control" v-model="editCategoryDuration">
                                            <span class="float-end"> minutes </span>
                                        </div>
                                    </div>
                                    <div style="margin-top: 20px;">
                                        <div class="px-2">
                                            <h6 class="mb-0">Description :&nbsp;</h6>
                                            <textarea style="padding-right: 10px;padding-left: 10px;width: 350px;height: 100px;margin-top: 5px;" class="form-control" v-model="editCategoryDescription"></textarea>
                                        </div>
                                    </div>
                                </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer"><button class="btn btn-light" type="button" data-bs-dismiss="modal">Close</button>
                <button class="btn btn-primary" type="button" style="background: #20c997;" data-bs-target="#submit" data-bs-toggle="modal">Save</button></div>
            </div>
        </div>
    </div>

    <!-- for submit  -->
    <div class="modal fade" role="dialog" tabindex="-1" id="submit">
      <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
          <div class="modal-content">
              <div class="modal-header text-bg-warning" style="padding-top: 10px;padding-bottom: 10px;padding-left: 20px;padding-right: 20px;">
                <h4 class="modal-title fs-3">Are you sure ?</h4><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                <p class="fs-6">Are you sure to edit category details ?</p>
              </div>
              <div class="modal-footer" style="padding-bottom: 5px;padding-top: 5px;">
                <button class="btn btn-primary btn-sm" type="button" @click="submitt()" data-bs-dismiss="modal" data-bs-target="#">Yes</button>
                <button class="btn btn-danger btn-sm" type="button" data-bs-dismiss="modal" data-bs-target="#">Cancel</button>
              </div>
          </div>
      </div>
    </div>
<!-- -------------------------------------------------------------------------------------------------  -->

  <!-- for alert -->
  <div class="alert-area">
    <div v-if="editCategoryName == '' && isEdit == true" class="alert warning text-sm">
      <strong class="block">Warning!</strong> Please enter category name.
    </div>
    <div v-if="editCategoryDuration < 1 ||editCategoryDuration > 480  && isEdit == true" class="alert warning text-sm">
      <strong class="block">Warning!</strong> Duration must be between 1 and 480.
    </div>
    <div v-if="editCategoryDescription.length > 500  && isEdit == true" class="alert warning text-sm">
      <strong class="block">Warning!</strong> Description must be has less than 500 characters.
    </div>
    
    <div v-if="editSuccess == true" class="alert success text-sm">
        <span class="closebtn" @click="editSuccess = undefined">x</span>
        <strong class="block">Success!</strong> Edit data success.
    </div>
    <div v-if="editSuccess == false" class="alert unsuccess text-sm">
        <span class="closebtn" @click="editSuccess = undefined">x</span>
        <strong class="block">Error!</strong> Edit data unsuccess.
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
.alert.unsuccess {
  background-color: #dd3324;
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
