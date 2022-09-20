<script setup>
import { useRoute, useRouter } from "vue-router";
import { onBeforeMount, ref } from "vue";

const { params } = useRoute();

const db = "http://localhost:5000/booking";
const categoryLink = `${import.meta.env.BASE_URL}api/categories`;
// const categoryLink = "http://localhost:8443/api/categories";

// ขาดเช็คชื่อคลิกนิกซ้ำ
const id = params.id;
const name = ref("");
const description = ref("")
const duration = ref("");
const categoryList=ref([])
const isNotNull = ref(false);
const categoryDetail = ref({})
const myRouoter = useRouter();
const goCategoriesList = () => myRouoter.push({ name: "CategoriesList" });

const key = localStorage.getItem('key')

// get every 10 sec
const getStatus=ref(undefined)
const resGetCategory=ref(undefined)

setInterval(async ()=>{
  const key = localStorage.getItem('key')
  resGetCategory.value= await fetch(categoryLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  })
  if (resGetCategory.value.status === 200) {
    categoryList.value = await resGetCategory.value.json();
    getStatus.value = true;
  } else getStatus.value = false;
},10000)

// first get event
const getCategory =async()=>{
  const key = localStorage.getItem('key')
  resGetCategory.value= await fetch(categoryLink, {
    method: "GET",
    headers: {
            "Authorization":'Bearer ' + key ,
            "Accept": 'application/json',
            "content-type": "application/json",
        }
  })
  if (resGetCategory.value.status === 200) {
    categoryList.value = await resGetCategory.value.json();
    getStatus.value = true;
  } else getStatus.value = false;       
}

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
    //console.log(categoryDetail.value);

    //console.log(Date.parse("2022-06-01T15:00:00+07:00"));
    if (categoryDetail.value.id == id) {
      isNotNull.value = true;
      name.value = categoryDetail.value.eventCategoryName;
      description.value = categoryDetail.value.eventCategoryDescription;
      duration.value = categoryDetail.value.eventCategoryDuration;
    }
  }
};

onBeforeMount(async()=>{
       await getCategory()
       await getDetail()
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
        console.log(`editSuccess = ${editSuccess.value}`)
       }else {
              canEdit=false
              editSuccess.value=false
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
    goCategoriesList()
    getCategory()
};

</script>

<template>
  <div
    class="showUp w-3/5 p-5 pb-7 mx-auto mt-10 bg-white rounded-md shadow-xl"
  >
    <!-- no data -->
    <div v-if="isNotNull == false">
      <h2 class="text-center my-5 font-bold text-xl">No categories data</h2>
      <div class="w-fit m-auto">
        <button @click="goCategoriesList" class="custom-btn back block">Go Back</button>
      </div>
    </div>

    <!-- have data -->
    <div
      v-else-if="isNotNull == true"
      class="p-4 border-double border-4 border-neutral-300 max-w-screen-lg"
    >
      <div class="mx-2 w-full">
        <h1 class="text-center mb-1 font-bold text-2xl">Category Detail</h1>
        <h3 class="text-center">----------------</h3>
      </div>
      <div class="mt-6 flex m-auto w-full">
        <!-- Name -->
        <div class=" px-2 w-2/5 inline-flex">
          <div class="pr-2 font-semibold flex m-auto text-gray-400">Name :</div>
          <div
            v-if="isEdit == false"
            class="border-2 rounded-md p-1.5 font-normal bg-white inline-block w-3/5 h-10"
          >
            {{ name }}
          </div>
          <div
            v-if="isEdit == true"
            class="text-center edit-color showUp border-2 rounded-md p-1.5 font-normal bg-white inline-block w-fit h-10"
          >
            <input type="text" v-model="editCategoryName" />
          </div>
        </div>

        <!-- Duration - -->
        <div class="px-2 w-3/5 inline-flex">
          <div class="font-semibold flex my-auto mx-5 text-gray-400">
            Duration  :
          </div>
          <div
            v-if="isEdit == false"
            class="text-center border-2 rounded-md p-1.5 font-normal bg-white inline-block w-2/5 h-10"
          >
            {{ duration  }}
          </div>
          <div
            v-if="isEdit == true"
            class="edit-color showUp border-2 rounded-md p-1.5 font-normal bg-white inline-block w-2/5 h-10"
          >
            <input type="number" :min="0" class="text-center" v-model="editCategoryDuration" />
          </div>
          <span class=" p-3">minutes</span>
        </div>
      </div>

      <!-- name charecters when edit name -->
      <div
          v-if="isEdit == true"
          class="text-gray-300 ml-12 text-right text-xs w-1/3"
          :style="[editCategoryName.length > 100 ? 'color:red' : '']"
          > 
          {{editCategoryName.length}} / 100 charecters
        </div>

      <!-- description -->
      <div class="ml-8 flex my-4 w-full">
          <div class="px-2 mb-2 font-semibold w-fit text-gray-400"> Description : </div>
          <div class="ml-3">
            <textarea
              readonly
              v-if="isEdit == false"
              rows="4"
              cols="50"
              class="border-2 text-black block px-3 py-2 placeholder-gray-300 resize-none rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              v-model="description"
            >
            </textarea>
            <textarea
              rows="4"
              cols="50"
              v-if="isEdit == true"
              class="edit-color showUp border-2 text-black block px-3 py-2 placeholder-gray-300 resize-none rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300"
              v-model="editCategoryDescription"
            >
                
            </textarea>
            <div
              v-if="isEdit == true"
              class="text-gray-300 text-xs p-1 text-right"
              :style="[editCategoryDescription.length > 500 ? 'color:red' : '']"
              > 
              {{editCategoryDescription.length}} / 500 charecters
            </div>
        </div>
       <!-- button not edit mode -->
        <div v-if="isEdit == false" class="showUp m-auto w-fit">
          <button @click="editInfo" class="m-4 custom-btn edit">Edit</button>
          <button @click="goCategoriesList"  class="m-4 custom-btn back">Go Back</button>
        </div>
       <!-- button edit mode -->
        <div v-if="isEdit == true" class="showUp m-auto w-fit">
          <button @click="cancel"  class="m-4 custom-btn remove">
            Cancel
          </button>
          <a href="#submit" class="m-4 custom-btn edit" > Submit </a>
        </div>
      </div>
    </div>
  </div>

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
