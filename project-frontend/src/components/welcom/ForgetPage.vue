<script setup>

import {Lock, Message} from "@element-plus/icons-vue";
import router from "../../router";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net";

const active = ref(0)
const form = reactive({
  password: '',
  password_repeat: '',
  email: '',
  code: '',
})

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const rules = {
  password:[
    {required: true,message: '请输入密码' ,trigger: ['blur']},
    { min: 6, max: 16, message: '密码长度必须在6-16个字符之间', trigger: ['blur','change'] },
  ],
  password_repeat:[
    { validator: validatePassword, trigger: ['blur','change'] },
  ],
  email:[
    {required: true,message: '请输入电子邮件地址' ,trigger: ['blur']},
    {type: 'email', message: '请输入合法的电子邮件地址',trigger: ['blur','change']}
  ],
  code:[
    {required: true,message: '请输入验证码' ,trigger: ['blur']},
    { min: 6, max: 6, message: '验证码长度必须为6个字符', trigger: ['blur','change'] },
  ]
}
const formRef = ref()
const isEmailValid =ref(false)
const coldTime = ref(0)
const onValidate = (prop, isValid) =>{
  if (prop === 'email'){
    isEmailValid.value = isValid
  }
}

let coldInterval = null
const validateEmail = () =>{
  coldTime.value = 60
  post('/api/auth/valid-email-reset', {
    email: form.email
  }, (message) =>{
    ElMessage.success(message)
    coldTime.value = 60
    clearInterval(coldInterval)
    coldInterval =  setInterval(() => coldTime.value--, 1000)
  },(message) =>{
    ElMessage.success(message)
    coldTime.value = 0
  })
}


const startRest = () =>{
  formRef.value.validate((isValid) =>{
    if (isValid){
      post('/api/auth/start-reset',{
        email: form.email,
        code: form.code
      }, () => {
        active.value = 1
      })
    }else {
      ElMessage.warning('请完整填写表单内容')
    }
  })
}

const doReset = () =>{
  formRef.value.validate((isValid) =>{
    if (isValid){
      post('/api/auth/do-reset',{
        password: form.password,
      }, (message) => {
        ElMessage.success(message)
        router.push("/")
      })
    }else {
      ElMessage.warning('请完整填写表单内容')
    }
  })
}
</script>

<template>
  <div>
    <div style="margin: 30px 20px">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮件" />
        <el-step title="重新设置密码" />
      </el-steps>
    </div>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center; margin:20px;height: 100%" v-if="active === 0">
        <div style="margin-top: 100px">
          <div style="font-size: 25px; font-weight: bold">重置密码</div>
        </div>
        <div style="margin-top: 50px" >
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
            <el-form-item prop="email">
              <el-input v-model="form.email" type="email" placeholder="电子邮件地址" :prefix-icon="Message" />
            </el-form-item>
            <el-form-item prop="code">
              <el-row :gutter="10" style="width: 100%">
                <el-col :span="14">
                  <el-input v-model="form.code" type="text" :maxlength="6" placeholder="请输入验证码"/>
                </el-col>
                <el-col :span="3">
                  <el-button @click="validateEmail" type="success"  :disabled="!isEmailValid || coldTime > 0">
                    {{coldTime > 0 ? '请稍后' + coldTime + '秒' : '获取验证码'}}
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 50px">
          <el-button @click="startRest" style="width: 270px" type="danger" plain>下一步</el-button>
        </div>
      </div>
    </transition>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center; margin:20px;height: 100%" v-if="active === 1">
        <div style="margin-top: 100px">
          <div style="font-size: 25px; font-weight: bold">重置密码</div>
        </div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" :maxlength="16" placeholder="新密码" :prefix-icon="Lock" />
            </el-form-item>
            <el-form-item prop="password_repeat">
              <el-input v-model="form.password_repeat" type="password"  :maxlength="16" placeholder="重复新密码" :prefix-icon="Lock" />
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 50px">
          <el-button @click="doReset" style="width: 270px" type="danger" plain>确定</el-button>
        </div>
      </div>
    </transition>
  </div>


</template>

<style scoped>

</style>