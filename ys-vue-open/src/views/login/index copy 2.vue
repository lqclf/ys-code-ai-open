<template>
  <div class="login-container">
    <!-- 左侧动态特效区域 -->
    <div class="left-section">
      <!-- 左侧科技感标题与logo -->
      <div class="tech-title-container">
        <div class="logo-container">
          <div class="logo">
            <div class="logo-inner">
              <div class="logo-circle"></div>
              <div class="logo-text">YS</div>
              <!-- 全息效果 -->
              <div class="hologram-effect"></div>
            </div>
            <!-- 数据环 -->
            <div class="data-ring">
              <div class="data-pulse"></div>
            </div>
          </div>
        </div>

        <div class="title-container">
          <h1 class="main-title">
            <span class="title-char" v-for="(char, index) in '忆笙管理平台'" :key="index" :style="`--delay: ${index * 0.1}s`">{{ char }}</span>
          </h1>
          <h2 class="sub-title">YsCode</h2>
          <div class="title-decoration">
            <div class="decoration-line"></div>
            <div class="decoration-dot"></div>
            <div class="decoration-line"></div>
          </div>
          <p class="title-description">
            <img :src="logoImg" alt="" width="100px" height="100px">
            创新 · 分享 · 成长</p>
        </div>

        <!-- 科技感数据流 -->
        <div class="data-stream">
          <div class="stream-line" v-for="i in 5" :key="i" :style="`--delay: ${i * 0.5}s; --duration: ${3 + Math.random() * 2}s`"></div>
        </div>

        <!-- 科技感元素 -->
        <div class="tech-elements">
          <div class="tech-element element-1"></div>
          <div class="tech-element element-2"></div>
          <div class="tech-element element-3"></div>
          <div class="tech-element element-4"></div>
        </div>
      </div>
       <!-- 右侧登录区域 -->
      <div class="login-box">
        <!-- 二维码切换按钮 -->
        <div class="qr-toggle" @click="toggleQRCode">
          <el-icon v-if="!showQRCode"><Monitor /></el-icon>
          <el-icon v-else><User /></el-icon>
        </div>
        
        <!-- 二维码登录区域 -->
        <div v-if="showQRCode" class="qr-login">
          <h2>扫码登录</h2>
          <div class="qr-code">
            <img src="https://picsum.photos/seed/qrcode/200/200.jpg" alt="二维码">
          </div>
          <p>请使用忆笙管理平台APP扫码登录</p>
          
          <!-- 社交登录 -->
          <div class="social-login">
            <div class="divider">
              <span>其他登录方式</span>
            </div>
            <div class="social-icons">
              <el-tooltip content="微信登录" placement="top">
                <el-button circle class="social-btn">
                  <el-icon><Wechat /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="QQ登录" placement="top">
                <el-button circle class="social-btn">
                  <el-icon><ChatDotRound /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="微博登录" placement="top">
                <el-button circle class="social-btn">
                  <el-icon><Share /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="GitHub登录" placement="top">
                <el-button circle class="social-btn">
                  <el-icon><Platform /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </div>
        </div>
        
        <!-- 账号/手机登录区域 -->
        <div v-else class="form-login">
          <h2>欢迎登录忆笙管理平台</h2>
          
          <el-tabs v-model="activeTab" class="login-tabs">
            <!-- 账号密码登录 -->
            <el-tab-pane label="账号密码" name="account">
              <el-form :model="accountForm" :rules="accountRules" ref="accountFormRef">
                <el-form-item prop="username">
                  <el-input 
                    v-model="accountForm.username" 
                    placeholder="请输入用户名/邮箱"
                    prefix-icon="User"
                  />
                </el-form-item>
                
                <el-form-item prop="password">
                  <el-input 
                    v-model="accountForm.password" 
                    type="password" 
                    placeholder="请输入密码"
                    prefix-icon="Lock"
                    show-password
                  />
                </el-form-item>
                <el-form-item prop="captcha" class="captcha-item">
								<div class="captcha-container">
									<el-input v-model="accountForm.captcha" placeholder="验证码" prefix-icon="Key" class="custom-input captcha-input" />
									<div class="captcha-img" @click="refreshCaptcha">
										<img :src="captchaUrl" alt="验证码" />
									</div>
								</div>
							</el-form-item>
                
                <div class="login-options">
                  <el-checkbox v-model="accountForm.remember">记住密码</el-checkbox>
                  <el-link type="primary" underline="never">忘记密码?</el-link>
                </div>
                
                <el-form-item>
                  <el-button type="primary" class="login-btn" @click="handleAccountLogin">登录</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            
            <!-- 手机号登录 -->
            <el-tab-pane label="手机号" name="phone">
              <el-form :model="phoneForm" :rules="phoneRules" ref="phoneFormRef">
                <el-form-item prop="phone">
                  <el-input 
                    v-model="phoneForm.phone" 
                    placeholder="请输入手机号"
                    prefix-icon="Iphone"
                  />
                </el-form-item>
                
                <el-form-item prop="code">
                  <el-input 
                    v-model="phoneForm.code" 
                    placeholder="请输入验证码"
                    prefix-icon="Key"
                  >
                    <template #append>
                      <el-button 
                        type="primary" 
                        :disabled="countdown > 0"
                        @click="sendCode"
                      >
                        {{ countdown > 0 ? `${countdown}s后重试` : '获取验证码' }}
                      </el-button>
                    </template>
                  </el-input>
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" class="login-btn" @click="handlePhoneLogin">登录</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
          
          <!-- 社交登录 -->
          <div class="social-login">
            <div class="divider">
              <span>其他登录方式</span>
            </div>
            <div class="social-icons">
              <el-tooltip content="微信登录" placement="top">
                <el-button circle class="social-btn">
                  <el-icon><Wechat /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="QQ登录" placement="top">
                <el-button circle class="social-btn">
                  <el-icon><ChatDotRound /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="微博登录" placement="top">
                <el-button circle class="social-btn">
                  <el-icon><Share /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="GitHub登录" placement="top">
                <el-button circle class="social-btn">
                  <el-icon><Platform /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </div>
          
          <!-- 注册和协议 -->
          <div class="register-agreement">
            <span>还没有账号？</span>
            <el-link type="primary" underline="never" @click="goToRegister">立即注册</el-link>
          </div>
          <div class="agreement">
            <el-checkbox v-model="agreed"></el-checkbox>
            <span>我已阅读并同意</span>
            <el-link type="primary" underline="never">《用户协议》</el-link>
            <span>和</span>
            <el-link type="primary" underline="never">《隐私政策》</el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Picture, Iphone, Key, ChatDotRound, Share, Platform, Monitor } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import logoImg from '@/assets/logo.png';

const router = useRouter()
const activeTab = ref('account')
const showQRCode = ref(false)
const agreed = ref(false)
const countdown = ref(0)
const captchaUrl = ref('https://picsum.photos/seed/captcha/120/40.jpg')

// 账号密码表单
const accountFormRef = ref()
const accountForm = reactive({
  username: '',
  password: '',
  captcha: '',
  remember: false
})

const accountRules = {
  username: [
    { required: true, message: '请输入用户名/邮箱', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度为4位', trigger: 'blur' }
  ]
}

// 手机号表单
const phoneFormRef = ref()
const phoneForm = reactive({
  phone: '',
  code: ''
})

const phoneRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ]
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaUrl.value = `https://picsum.photos/seed/${Math.random().toString(36).substring(2)}/120/40.jpg`
}

// 切换二维码登录
const toggleQRCode = () => {
  showQRCode.value = !showQRCode.value
}

// 发送验证码
const sendCode = () => {
  phoneFormRef.value.validateField('phone', (valid: boolean) => {
    if (valid) {
      ElMessage.success('验证码已发送')
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    }
  })
}

// 账号密码登录
const handleAccountLogin = () => {
  accountFormRef.value.validate((valid: boolean) => {
    if (valid) {
      if (!agreed.value) {
        ElMessage.warning('请先同意用户协议和隐私政策')
        return
      }
      ElMessage.success('登录成功')
      router.push('/dashboard')
    }
  })
}

// 手机号登录
const handlePhoneLogin = () => {
  phoneFormRef.value.validate((valid: boolean) => {
    if (valid) {
      if (!agreed.value) {
        ElMessage.warning('请先同意用户协议和隐私政策')
        return
      }
      ElMessage.success('登录成功')
      router.push('/dashboard')
    }
  })
}

// 去注册
const goToRegister = () => {
  ElMessage.info('跳转到注册页面')
}

onMounted(() => {
  // 初始化验证码
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  background-color: #f5f7fa;
  overflow: hidden;
}

/* 左侧动态特效区域 */
.left-section {
  flex: 1;
  position: relative;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  overflow: hidden;
}

.dynamic-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
}

/* 粒子效果 */
.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.particle {
  position: absolute;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: float 15s infinite linear;
}

@keyframes float {
  0% {
    transform: translateY(0) translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) translateX(100px);
    opacity: 0;
  }
}

/* 科技线条 */
.tech-lines {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.line {
  position: absolute;
  background-color: rgba(255, 255, 255, 0.2);
  height: 1px;
}

.line-1 {
  width: 300px;
  top: 20%;
  left: 10%;
  transform: rotate(30deg);
  animation: lineMove 8s infinite alternate;
}

.line-2 {
  width: 400px;
  top: 50%;
  right: 15%;
  transform: rotate(-20deg);
  animation: lineMove 10s infinite alternate;
}

.line-3 {
  width: 250px;
  bottom: 30%;
  left: 20%;
  transform: rotate(45deg);
  animation: lineMove 12s infinite alternate;
}

@keyframes lineMove {
  0% {
    opacity: 0.2;
    transform: translateX(0) rotate(30deg);
  }
  100% {
    opacity: 0.8;
    transform: translateX(50px) rotate(35deg);
  }
}

/* 科技圆圈 */
.tech-circles {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circle {
  position: absolute;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 15%;
  right: 20%;
  animation: pulse 4s infinite alternate;
}

.circle-2 {
  width: 150px;
  height: 150px;
  bottom: 20%;
  left: 15%;
  animation: pulse 5s infinite alternate;
}

.circle-3 {
  width: 100px;
  height: 100px;
  top: 40%;
  left: 40%;
  animation: pulse 6s infinite alternate;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.3;
  }
  100% {
    transform: scale(1.2);
    opacity: 0.8;
  }
}

/* 左侧内容 */
.tech-content {
  text-align: center;
  z-index: 10;
}

.tech-content h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

.tech-content p {
  font-size: 1.2rem;
  opacity: 0.9;
}

/* 右侧登录区域 */
/* .right-section {
  width: 45%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
} */

.login-box {
  position: absolute;
	top: 50%;
	right: 10%;
	transform: translateY(-50%);
	width: 420px;
  background-color: white;
	backdrop-filter: blur(10px);
	border-radius: 12px;
	border: 1px solid rgba(0, 255, 255, 0.2);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
	z-index: 10;
	padding: 30px;
	transition: all 0.3s ease;
	overflow: hidden;
}

/* 二维码切换按钮 */
.qr-toggle {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 36px;
  height: 36px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
}

.qr-toggle:hover {
  background-color: #e6e8eb;
}

/* 二维码登录 */
.qr-login {
  text-align: center;
}

.qr-login h2 {
  margin-bottom: 20px;
  color: #303133;
}

.qr-code {
  margin: 20px 0;
  display: flex;
  justify-content: center;
}

.qr-code img {
  width: 200px;
  height: 200px;
  border: 1px solid #ebeef5;
}

.qr-login p {
  color: #606266;
  margin-bottom: 20px;
}

/* 表单登录 */
.form-login h2 {
  text-align: center;
  margin-bottom: 25px;
  color: #303133;
}

.login-tabs {
  margin-bottom: 20px;
}

.captcha-item {
  display: flex;
  align-items: center;
}

.captcha-item :deep(.el-form-item__content) {
  display: block;
  align-items: center;
}

.captcha-img {
  margin-left: 10px;
  cursor: pointer;
  height: 40px;
  width: 120px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.captcha-img img {
  height: 100%;
  width: 100%;
  object-fit: cover;
}

.captcha-container {
	display: flex;
	gap: 2px;
}

.captcha-input {
	flex: 1;
}

.captcha-img:hover {
	border-color: rgba(0, 255, 255, 0.5);
}
.login-options {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: 500;
}

/* 社交登录 */
.social-login {
  margin-top: 25px;
}

.divider {
  display: flex;
  align-items: center;
  margin: 15px 0;
  color: #909399;
  font-size: 14px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background-color: #e4e7ed;
}

.divider span {
  padding: 0 15px;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.social-btn {
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  border: none;
  color: #606266;
}

.social-btn:hover {
  background-color: #e6e8eb;
  color: #409eff;
}

/* 注册和协议 */
.register-agreement {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.agreement {
  margin-top: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #909399;
}

.agreement span {
  margin: 0 3px;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .login-container {
    flex-direction: column;
  }
  
  .left-section {
    display: none;
  }
  
  .right-section {
    width: 100%;
    height: 100%;
    justify-content: center;
    align-items: center;
  }
  
  .login-box {
    max-width: 400px;
  }
}

/* 左侧科技感标题与logo样式 */
.tech-title-container {
	position: absolute;
	top: 45%;
	left: 20%;
	transform: translateY(-50%);
	z-index: 10;
	display: flex;
	flex-direction: column;
	align-items: center;
	width: 400px;
}

.logo-container {
	margin-bottom: 40px;
	position: relative;
}

.logo {
	width: 120px;
	height: 120px;
	position: relative;
	animation: logoFloat 6s ease-in-out infinite;
}

.logo-inner {
	width: 100%;
	height: 100%;
	background: rgba(0, 255, 255, 0.1);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	overflow: hidden;
	border: 2px solid rgba(0, 255, 255, 0.3);
	box-shadow: 0 0 30px rgba(0, 255, 255, 0.3);
}

.logo-circle {
	position: absolute;
	width: 100%;
	height: 100%;
	border-radius: 50%;
	border: 1px solid rgba(0, 255, 255, 0.5);
	animation: logoRotate 20s linear infinite;
}

.logo-text {
	font-size: 48px;
	font-weight: 700;
	color: #fff;
	letter-spacing: 2px;
	z-index: 2;
	background: linear-gradient(45deg, #00ffff, #0080ff);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
	text-shadow: 0 0 20px rgba(0, 255, 255, 0.5);
}

/* 全息效果 */
.hologram-effect {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: linear-gradient(45deg, transparent 30%, rgba(0, 255, 255, 0.2) 50%, transparent 70%);
	animation: hologramScan 3s linear infinite;
	pointer-events: none;
}

@keyframes hologramScan {
	0% {
		transform: translateX(-100%) translateY(-100%);
	}
	100% {
		transform: translateX(100%) translateY(100%);
	}
}

/* 数据环 */
.data-ring {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	width: 150px;
	height: 150px;
	border-radius: 50%;
	border: 1px solid rgba(0, 255, 255, 0.2);
	animation: dataRingRotate 15s linear infinite;
}

.data-pulse {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	width: 10px;
	height: 10px;
	border-radius: 50%;
	background: #00ffff;
	box-shadow: 0 0 20px rgba(0, 255, 255, 0.8);
}

@keyframes dataRingRotate {
	0% {
		transform: translate(-50%, -50%) rotate(0deg);
	}
	100% {
		transform: translate(-50%, -50%) rotate(360deg);
	}
}

.title-container {
	text-align: center;
}

.main-title {
	font-size: 48px;
	font-weight: 700;
	color: #fff;
	margin: 0 0 10px;
	letter-spacing: 4px;
	position: relative;
	display: inline-block;
}

.title-char {
	display: inline-block;
	opacity: 0;
	transform: translateY(20px);
	animation: titleCharAppear 0.5s forwards;
	animation-delay: var(--delay);
}

@keyframes titleCharAppear {
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

.main-title::after {
	content: '';
	position: absolute;
	bottom: -5px;
	left: 0;
	width: 0;
	height: 2px;
	background: linear-gradient(90deg, #00ffff, #0080ff);
	animation: titleLine 2s ease-out forwards;
	animation-delay: 0.5s;
}

.sub-title {
	font-size: 24px;
	color: rgba(255, 255, 255, 0.7);
	margin: 0 0 20px;
	letter-spacing: 2px;
	font-weight: 300;
}

.title-decoration {
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 20px 0;
}

.decoration-line {
	width: 40px;
	height: 1px;
	background: rgba(0, 255, 255, 0.5);
}

.decoration-dot {
	width: 6px;
	height: 6px;
	border-radius: 50%;
	background: #00ffff;
	margin: 0 10px;
	box-shadow: 0 0 10px rgba(0, 255, 255, 0.7);
	animation: dotPulse 2s ease-in-out infinite;
}

.title-description {
	font-size: 16px;
	color: rgba(255, 255, 255, 0.6);
	margin: 0;
	letter-spacing: 2px;
}

/* 科技感数据流 */
.data-stream {
	position: absolute;
	width: 100%;
	height: 100%;
	top: 0;
	left: 0;
	pointer-events: none;
}

.stream-line {
	position: absolute;
	width: 2px;
	height: 100px;
	background: linear-gradient(to bottom, transparent, rgba(0, 255, 255, 0.8), transparent);
	animation: streamFlow var(--duration) linear infinite;
	animation-delay: var(--delay);
	opacity: 0;
}

@keyframes streamFlow {
	0% {
		transform: translateY(-100px);
		opacity: 0;
	}
	10% {
		opacity: 1;
	}
	90% {
		opacity: 1;
	}
	100% {
		transform: translateY(calc(100vh + 100px));
		opacity: 0;
	}
}

/* 科技感元素 */
.tech-elements {
	position: absolute;
	width: 100%;
	height: 100%;
	top: 0;
	left: 0;
	pointer-events: none;
}

.tech-element {
	position: absolute;
	border: 1px solid rgba(0, 255, 255, 0.3);
	border-radius: 50%;
}

.element-1 {
	width: 300px;
	height: 300px;
	top: -150px;
	left: -150px;
	animation: elementPulse 4s ease-in-out infinite;
}

.element-2 {
	width: 200px;
	height: 200px;
	bottom: -100px;
	right: -100px;
	animation: elementPulse 4s ease-in-out infinite 1s;
}

.element-3 {
	width: 150px;
	height: 150px;
	top: 50%;
	right: -75px;
	animation: elementPulse 4s ease-in-out infinite 2s;
}

.element-4 {
	width: 100px;
	height: 100px;
	bottom: 20%;
	left: -50px;
	animation: elementPulse 4s ease-in-out infinite 3s;
}

@keyframes elementPulse {
	0%,
	100% {
		transform: scale(1);
		opacity: 0.2;
		box-shadow: 0 0 10px rgba(0, 255, 255, 0.2);
	}
	50% {
		transform: scale(1.1);
		opacity: 0.5;
		box-shadow: 0 0 20px rgba(0, 255, 255, 0.5);
	}
}

@keyframes logoFloat {
	0%,
	100% {
		transform: translateY(0);
	}
	50% {
		transform: translateY(-10px);
	}
}

@keyframes logoRotate {
	0% {
		transform: rotate(0deg);
	}
	100% {
		transform: rotate(360deg);
	}
}

@keyframes titleLine {
	0% {
		width: 0;
	}
	100% {
		width: 100%;
	}
}

@keyframes dotPulse {
	0%,
	100% {
		transform: scale(1);
		opacity: 0.5;
	}
	50% {
		transform: scale(1.5);
		opacity: 1;
	}
}

</style>