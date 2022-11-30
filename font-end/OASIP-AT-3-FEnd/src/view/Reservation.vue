<script setup>
import { useRoute, useRouter } from "vue-router";
import { onBeforeMount, ref } from "vue";
import Swal from 'sweetalert2'
import 'boxicons'

const { params } = useRoute();

const db = "http://localhost:5000/booking";
// //for vm
// const forLink = '${import.meta.env.BASE_URL}'
//for localhost
const forLink = 'http://localhost:8443/'
const eventLink = `${forLink}api/events`;
const refreshLink = `${forLink}api/users/refresh`;
const fileLink = `${forLink}api/files/events`;

// const eventLink = "http://localhost:8443/api/events";
// const refreshLink = "http://localhost:8443/api/users/refresh";
// const fileLink = "http://localhost:8443/api/files/events";

const id = params.id;
const name = ref("");
const eMail = ref("");
const category = ref("");
const startDate = ref("");
const startTime = ref("");
const duration = ref("");
const noteT = ref("");
const eventFile = ref("");
const detailBooking = ref({});
const eventList=ref([])
const isNotNull = ref(false);

const myRouoter = useRouter();
const goReservation = () => myRouoter.push({ name: "ReservationList" });
const goHome = () => myRouoter.push({ name: "Home" });

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

onBeforeMount(async()=>{
       await  getEvent()
       await getDetail()
       checkRole()
       fileUrl.value = await downloadFile(detailBooking.value.id);
       console.log(fileUrl.value);
      //  console.log(detailBooking.value.eventFile);
})

const fileUrl = ref("")
const downloadFile = async (eventId) => {
  console.log("In progress (Get UserDetail)");
  const key = localStorage.getItem('key')
  const res = await fetch(`${fileLink}/${eventId}/${detailBooking.value.eventFile}`,
    {
      headers: {
        "content-type": "application/octet-stream"
      },
    }
  );

  if (res.status === 200) {
    console.log("Successfully executed! " + res.status);
    console.log(fileUrl.value);
    console.log(res);
    return res.url;
    // return await res.json();
  } else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
    console.log('test...')
    const resForRefresh = await fetch(refreshLink, {
      headers: {
        Authorization: "Bearer " + key ,
        isRefreshToken: true ,
      },
    })
      const jwt = await resForRefresh.json()
      console.log(jwt)
      if(resForRefresh.status === 200){
        // set localStorage
        localStorage.setItem('key',jwt.token)
        localStorage.setItem('token','refreshToken')
        getEvent()
      }
    } else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        console.log('เข้า')
    }
};


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

//GET event
const getEvent = async () => {
  const key = localStorage.getItem('key')
  const res = await fetch(eventLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    eventList.value = await res.json();
  } 
  // refresh token ----------------------------- //
  else if (res.status === 401 && localStorage.getItem('token')==='accessToken') {
    console.log('test...')
    const resForRefresh = await fetch(refreshLink, {
      headers: {
        Authorization: "Bearer " + key ,
        isRefreshToken: true ,
      },
    })
      const jwt = await resForRefresh.json()
      console.log(jwt)
      if(resForRefresh.status === 200){
        // set localStorage
        localStorage.setItem('key',jwt.token)
        localStorage.setItem('token','refreshToken')
        getEvent()
      }
    }else if(res.status === 401 && localStorage.getItem('token')==='refreshToken'){
        localStorage.removeItem('key')
        localStorage.removeItem('token')
        goHome()
        console.log('เข้า')
    }
    // ----------------------------------------- //
};

// get value
const getDetail = async () => {
  const key = localStorage.getItem('key')

  const res = await fetch(`${eventLink}/${params.id}`,{
    method: 'GET',
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  });
  if (res.status === 200) {
    detailBooking.value = await res.json();
    //console.log(detailBooking.value);

    //console.log(Date.parse("2022-06-01T15:00:00+07:00"));
    if (detailBooking.value.id == id) {
      isNotNull.value = true;
      name.value = detailBooking.value.bookingName;
      eMail.value = detailBooking.value.bookingEmail;
      category.value = detailBooking.value.categoryName;
      startDate.value = detailBooking.value.eventStartTime.substring(0, 10);
      startTime.value = detailBooking.value.eventStartTime.substring(11, 16);
      duration.value = detailBooking.value.eventDuration;
      noteT.value = detailBooking.value.eventNotes;
      eventFile.value = detailBooking.value.eventFile
    }
    console.log(detailBooking.value)
    console.log(fileUrl)
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
};

//remove information
const removeInfo = async () => {
  const key = localStorage.getItem('key')
  const res = await fetch(`${eventLink}/${id}`, { 
    method: "DELETE",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  })
  if (res.status === 200) {
    Swal.fire('Delete Successful !', 'The booking has been deleted.','success')
    // console.log("delete successfully");
    goReservation();
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
      // ----------------------------------------------------------------------- //
      else Swal.fire('Delete Fail !', 'Please try again.','error')
};

// assign to edit attribute
const isEdit = ref(false);
const editStartTime = ref("");
const editStartDate = ref("");
const editNote = ref("");
const editFile = ref("")
const editFileName = ref("");
console.log(editFileName.value)

const editInfo = () => {
  isEdit.value = true;
  editStartTime.value = startTime.value;
  editStartDate.value = startDate.value;
  editNote.value = noteT.value;
  editFileName.value = eventFile.value
};

const cancel = () => {
  isEdit.value = false;
  changeFile.value = false;
  editStartTime.value = "";
  editStartDate.value = "";
  editFileName.value = detailBooking.value.eventFile;
};

//edit file
const changeFile = ref(false)
const openChangeFile = () => { changeFile.value = !changeFile.value }
const editFileChanged = (e) => {
  editFile.value = e.target.files[0]
  editFileName.value = e.target.files[0].name
  console.log(editFile.value);
  console.log(editFileName.value);
  console.log(eventFile.value);
}

//delete file
const deleteFile = () => {
  editFileName.value = undefined
  editFile.value = null
  console.log(editFile.value);
  console.log(editFileName.value);
}
const editFileSuccess = ref(undefined) 
const editFileToDB = async() => {
  const key = localStorage.getItem('key')
      console.log(editStartTime.value)
      console.log(editStartDate.value)
      console.log(editNote.value)
      console.log(eventFile.value)
       if(editFileName.value!==eventFile.value){
         console.log('เข้า')
         if(editFileName.value!==undefined) {
          console.log(editStartTime.value)
              console.log(editStartDate.value)
              console.log(editNote.value)
          console.log('เข้า if')
          console.log(editFile.value)
            const data = new FormData();
            data.append("file",editFile.value)
            console.log(data.values)
            const resFile = await fetch(`${fileLink}/${id}`, {
              method: "POST",
              headers: {
                "Authorization": "Bearer " + key,
              },
              body: data
            });
            console.log(resFile)
            getDetail()
            if(resFile.status==200){
              eventFile.value = editFileName.value
              console.log(eventFile.value)
            }
            else if(resFile.status!==200) editFileSuccess.value = false
          }
          else{
            console.log('เข้า else')
            console.log(editFile.value)
            getDetail()
            const resFile = await fetch(`${fileLink}/${id}`, {
              method: "DELETE",
              headers: {
                "Authorization": "Bearer " + key,
              }
            });
            
            console.log(resFile)
            
            if(resFile.status!==200) editFileSuccess.value = false
          }
        } 
        getDetail() 
}
const editEvent =async()=>{
        const key = localStorage.getItem('key')
        console.log(editStartTime.value)
        console.log(editStartDate.value)
        console.log(editNote.value)
        const res = await fetch(`${eventLink}/${id}`, {
        method: "PUT",
        headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        },
        body: JSON.stringify({
          eventStartTime: `${editStartDate.value}T${editStartTime.value}:00+07:00`,
          eventNotes: editNote.value,
        }),
      });

      if (res.status == 200) {
        let editDetailNote = await res.json();
        startDate.value = editDetailNote.eventStartTime.substring(0, 10);
        startTime.value = editDetailNote.eventStartTime.substring(11, 16);
        noteT.value = editDetailNote.eventNotes;
        Swal.fire('Edit Successful !', 'The booking has been modified.','success')
        // eventFile.value = editDetailNote.eventFile

        isEdit.value = false;
        canEdit.value=true
        editSuccess.value=true
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
      // ------------------------------------------------------------------------- //
        else {
              canEdit.value=false
              editSuccess.value=false
              Swal.fire('Edit Fail!', 'Please try to edit again.','error')
       }
       getDetail()
       return canEdit.value
}

const edit = ()=> {
  editFileToDB()
  editEvent()
}

// submit
const isInput=ref(undefined)
const isPast =ref(undefined)
const isOverlap =ref(undefined)
const editSuccess=ref(undefined)
const canEdit= ref(undefined)

const submitt = async () => {
       isInput.value=undefined
       isPast.value=undefined
       isOverlap.value=undefined
       editSuccess.value=undefined
  if (editStartDate.value !== "" && editStartTime.value !== "") {
    if (//สำหรับเลือกวันในอนาคต
      Date.parse(`${editStartDate.value}T${editStartTime.value}:00+07:00`) >
      Date.parse(`${date.value}T${time.value}:00+07:00`)
       ) {
           if(overlap()){
              isOverlap.value=true
      } else if(edit()){
             cancel()
             setTimeout(()=>editSuccess.value=false,5000)
      }
    } else isPast.value=true//alert(`Can't select past date and time `);
  }else isInput.value=false
};

// check overlap
const betweenDateWarning=ref(undefined)

const overlap = () => {
  betweenDateWarning.value = undefined;
  let isOverlap = undefined;
  const filterEventList = eventList.value.filter((event) => {
    return event.id != id
  })
  for (let check of filterEventList) {
    if (check.categoryName == category.value&&check.id!==id) {
      if (
        Date.parse(`${editStartDate.value}T${editStartTime.value}:00+07:00`) >=
        Date.parse(check.eventStartTime) &&
        Date.parse(`${editStartDate.value}T${editStartTime.value}:00+07:00`) <=
        Date.parse(
          `${check.eventStartTime.substring(0, 10)}T${calTime(
            parseInt(check.eventStartTime.substring(11, 13)),
            parseInt(check.eventStartTime.substring(14, 16)),
            check.eventDuration
          )}:00+07:00`
        )
      ) {
        isOverlap = true;
        betweenDateWarning.value = String(
          `Cannot select during ${check.eventStartTime.substring(
            0,
            10
          )} between ${check.eventStartTime.substring(11, 16)} - ${calTime(
            parseInt(check.eventStartTime.substring(11, 13)),
            parseInt(check.eventStartTime.substring(14, 16)),
            check.eventDuration
          )}`
        );
      } else if (
        Date.parse(
          `${startDate.value}T${calTime(
            parseInt(editStartTime.value.substring(0, 2)),
            parseInt(editStartTime.value.substring(3, 5)),
            duration.value
          )}:00+07:00`
        ) >= Date.parse(check.eventStartTime) &&
        Date.parse(
          `${startDate.value}T${calTime(
            parseInt(editStartTime.value.substring(0, 2)),
            parseInt(editStartTime.value.substring(3, 5)),
            duration.value
          )}`
        ) <=
        Date.parse(
          `${check.eventStartTime.substring(0, 10)}T${calTime(
            parseInt(check.eventStartTime.substring(11, 13)),
            parseInt(check.eventStartTime.substring(14, 16)),
            check.eventDuration
          )}:00+07:00`
        )
      ) {
        isOverlap = true;
        betweenDateWarning.value = String(
          `Your event time is overlapped between ${check.eventStartTime.substring(
            11,
            16
          )} and ${calTime(
            parseInt(check.eventStartTime.substring(11, 13)),
            parseInt(check.eventStartTime.substring(14, 16)),
            check.eventDuration
          )}
          ${check.eventStartTime.substring(0, 10)}`
        );
      }
    }
  }
  return isOverlap;
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
            <div class="col-md-8 col-xl-10 text-center mx-auto">
                <h2>Reservation Detail</h2>
            </div>
        </div>

        <!-- no have data -->
        <div v-if="isNotNull == false" class="row row-cols-md-2 row-cols-xl-3 justify-content-center" style="margin-top: -40px;">
            <div class="col-lg-6">
              <section class="py-4 py-xl-5">
                  <div class="container">
                    <div class="bg-light rounded border-0 border-light d-flex flex-column justify-content-between flex-lg-row p-4 p-md-5">
                      <div class="pb-2 pb-lg-1">
                        <h2 class="fw-bold mb-2">No data of<br> Reservation id : {{id}}</h2>
                        <p class="mb-0"></p>
                      </div>
                      <div class="align-items-center my-2">
                        <a class="btn btn-primary fs-5 m-auto py-2 px-4" role="button" @click="goReservationList"> Back </a>
                      </div>
                    </div>
                  </div>
              </section>
            </div>
        </div>

        <!-- have data -->
        <div v-else-if="isNotNull == true" class="row row-cols-1 row-cols-md-2 row-cols-xl-3 justify-content-center" style="margin-top: -40px;">
            <div class="col-md-7 col-lg-8 col-xl-5">
                <div class="card"><img class="card-img-top w-100 d-block fit-cover" style="height: 200px;" src="../assets/maewzom.png">

                    <!-- show details -->
                    <div class="card-body flex-fill p-4">
                        <p class="text-primary card-text mb-0 float-end">ID : {{ id }} </p>
                        <h4 class="card-title" style="margin-top: 30px;">Name : {{ name }} </h4>
                        <div>
                          <strong>E-mail :</strong>
                          <span style="margin-left: 10px;">{{ eMail }}</span>
                        </div>
                        <div class="flex-fill" style="margin-top: 5px;">
                          <strong>Start Date :</strong><span style="margin-left: 10px;"> {{ startDate }} </span>
                          <strong style="margin-left: 20px;">Start Time :</strong><span style="margin-left: 10px;"> {{ startTime }} </span>
                        </div>
                        <div style="margin-top: 5px;">
                          <strong>Category :</strong><span style="margin-left: 10px;">{{ category }}</span>
                          <strong style="margin-left: 9px;">Duration :</strong><span style="margin-left: 5px;"> {{ duration }} Min.</span>
                        </div>
                        <div v-if="(noteT !== null && noteT !== '')" style="margin-top: 5px; margin-right: 10px;"><strong>Note :</strong>
                            <span style="margin-left: 10px;"> {{ noteT }}</span>
                        </div>
                        <div v-if="eventFile !== undefined" style="margin-top: 5px;">
                          <strong>Attachment File :</strong>
                          <span style="margin-left: 10px;"> {{ eventFile }} </span>
                          <a :href="fileUrl" :download="id" style="margin-left: 15px;">
                            <box-icon name='cloud-download' size='md' border='circle' animation='tada-hover' class="" ></box-icon>
                          </a>  
                        </div>
                        <div class="d-sm-flex d-md-flex d-lg-flex flex-fill justify-content-center justify-content-sm-end justify-content-lg-center" style="margin-top: 20px;margin-bottom: 0px;">
                          <button class="btn btn-warning" type="button" style="margin-right: 10px;" data-bs-target="#editEvent" data-bs-toggle="modal"  @click="editInfo">Edit</button>
                          <button class="btn btn-danger" type="button" style="margin-right: 10px;" data-bs-target="#confirmToRemove" data-bs-toggle="modal">Remove</button>
                          <button class="btn btn-secondary" type="button" @click="goReservation">Back</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- edit event -->
    <div class="modal fade" role="dialog" tabindex="-1" id="editEvent">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header" style="background: #f0ac72;">
                    <h4 class="modal-title">Edit Reservation Detail :&nbsp;</h4>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                   <div class="container position-relative" style="padding-left: 0px;padding-right: 0px;">
                      <div class="row text-start d-flex justify-content-center align-items-center" style="margin-left: 0px;padding-right: 0px;margin-right: 0px;">
                          <div class="col-lg-12">
                            <div>
                                <div style="margin-top: 10px;margin-bottom: 10px;">
                                  <label class="form-label fw-semibold">Name :&nbsp;</label>
                                  <small class="float-end align-self-end"> {{ name.length }}/{{ nameLength }} charecters </small>
                                  <input class="form-control" type="text" disabled="" v-model="name">
                                </div>

                                <div style="margin-top: 10px;margin-bottom: 10px;">
                                  <label class="form-label fw-semibold">Email :&nbsp;</label>
                                  <small class="float-end align-self-end"> {{ eMail.length }}/{{ emailLength }} charecters </small>
                                  <input class="form-control" type="text" disabled="" v-model="eMail">
                                </div>
                                
                                <div style="margin-top: 10px;margin-bottom: 10px;">
                                  <label class="form-label fw-semibold">Category :&nbsp;</label>
                                  <input class="form-control" type="text" disabled="" v-model="category">
                                </div>
                            </div>
                                <div style="margin-top: 10px;margin-bottom: 10px;">
                                  <label class="form-label fw-semibold">Duration :&nbsp;</label>
                                  <input class="form-control" type="text" disabled="" v-model="duration">
                                </div>

                                <div style="margin-top: 10px;margin-bottom: 10px;">
                                  <label class="form-label fw-semibold">Start Date :&nbsp;</label>
                                  <input class="form-control" type="date" v-model="editStarDate" :min="date" >
                                </div>

                                <div style="margin-top: 10px;margin-bottom: 10px;">
                                  <label class="form-label fw-semibold">Start Time :&nbsp;</label>
                                  <input class="form-control" type="time" v-model="editStartTime">
                                </div>

                                <div style="margin-top: 10px;margin-bottom: 10px;">
                                  <label class="form-label fw-semibold">Note :&nbsp;</label>
                                  <small class="float-end align-self-end">{{ editNote.length }}/{{ noteLength }} charecters</small>
                                </div>
                                <textarea class="form-control-lg" style="margin-top: -10px;width: 390px;" v-model="editNote"></textarea>

                                <div style="margin-top: 10px;margin-bottom: 10px;">
                                  <!-- have file -->
                                    <div v-if="changeFile==false && editFileName!=undefined" class="flex-fill">
                                        <h6 class="form-label fw-semibold">File :&nbsp;</h6>
                                        <span class="form-control" style="margin-top: 10px;margin-bottom: 10px;"> {{ eventFile }} </span>
                                    </div>
                                    <!-- not have file   -->
                                    <div v-if="changeFile==true || editFileName==undefined" class="flex-fill">
                                        <h6 class="form-label fw-semibold"> Add File :&nbsp;</h6>
                                        <input @change="editFileChanged($event)" class="flex-fill form-control" type="file" style="margin-top: 5px;" readonly="">
                                        <p>The file size cannot be larger than 10 MB.</p>
                                    </div>
                                    <div class="d-sm-flex">
                                      <button class="btn btn-primary btn-sm" type="button" style="margin-right: 5px;" @click="openChangeFile">Change File</button>
                                      <button class="btn btn-danger btn-sm" type="button" @click="deleteFile">Delete File</button>
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
                <p class="fs-6">Are you sure to remove this reservation?</p>
              </div>
              <div class="modal-footer" style="padding-bottom: 5px;padding-top: 5px;">
                <button class="btn btn-primary btn-sm" type="button" @click="removeInfo" data-bs-dismiss="modal" data-bs-target="#">Yes</button>
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
                <p class="fs-6">Are you sure to edit this reservation?</p>
              </div>
              <div class="modal-footer" style="padding-bottom: 5px;padding-top: 5px;">
                <button class="btn btn-primary btn-sm" type="button" @click="submitt" data-bs-dismiss="modal" data-bs-target="#">Yes</button>
                <button class="btn btn-danger btn-sm" type="button" data-bs-dismiss="modal" data-bs-target="#">Cancel</button>
              </div>
          </div>
      </div>
    </div>

<!-- ----------------------------------------------------- -->

  <!-- for alert -->
  <div class="alert-area">
    <div v-if="isPast == true" class="alert text-sm">
      <span class="closebtn" @click="isPast = undefined">x</span>
      <strong class="block">Error!</strong> Can't select past date and time.
    </div>

    <div v-if="isInput == true" class="alert text-sm">
      <span class="closebtn" @click="isInput = undefined">x</span>
      <strong class="block">Error!</strong> Please input information.
    </div>
    
    <div v-if="
          eventList.length == 0
        " class="alert warning text-sm">
          <strong class="block">Warning!</strong> A system error has occurred,please try again.
     </div>

       <div v-if="isOverlap== true" class="alert warning text-sm">
          <span class="closebtn" @click="isOverlap = undefined">x</span>
          <strong class="block">Warning!</strong> {{ betweenDateWarning }}
       </div>

       <div v-if="editSuccess == true" class="alert success text-sm">
          <span class="closebtn" @click="editSuccess = false">x</span>
          <strong class="block">Success!</strong> Edit data success.
        </div>

        <div v-if="editFileSuccess == false" class="alert text-sm">
          <span class="closebtn" @click="editFileSuccess = undefined">x</span>
          <strong class="block">Error!</strong> can not edit with this file.
        </div>

  </div>

         <!-- for submit  -->
  <div id="submit" class="overlay">
    <div class="popup2 h-96">
      <h2 class="mb-5 text-xl font-bold bg-white mx-auto w-fit">
        Are you sure ?
      </h2>

      <div class="option flex m-auto w-full mt-10">
        <a
          @click="submitt"
          href="#"
          class="w-full text-center p-2 px-2 bg-gray-200 hover:bg-green-500 font-bold hover:text-white"
          >Yes</a
        >
        <a
          href="#"
          class="w-full text-center p-2 px-2 bg-gray-200 hover:bg-rose-500 font-bold hover:text-white"
          >No</a
        >
      </div>
    </div>
  </div>

  <!-- for remove  -->
  <div id="remove" class="overlay">
    <div class="popup2 h-96">
      <h2 class="mb-5 text-xl font-bold bg-white mx-auto w-fit">
        Are you sure(remove) ?
      </h2>

      <div class="option flex m-auto w-full mt-10">
        <a
          @click="removeInfo"
          href="#"
          class="w-full text-center p-2 px-2 bg-gray-200 hover:bg-green-500 font-bold hover:text-white"
          >Yes</a
        >
        <a
          href="#"
          class="w-full text-center p-2 px-2 bg-gray-200 hover:bg-rose-500 font-bold hover:text-white"
          >No</a
        >
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
.eidt-color {
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
