<template>
	<div class="login-container">
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
					<span class="title-char" v-for="(char, index) in '忆笙博客'" :key="index" :style="`--delay: ${index * 0.1}s`">{{ char }}</span>
				</h1>
				<h2 class="sub-title">YiSheng Blog</h2>
				<div class="title-decoration">
					<div class="decoration-line"></div>
					<div class="decoration-dot"></div>
					<div class="decoration-line"></div>
				</div>
				<p class="title-description">创新 · 分享 · 成长</p>
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

		<!-- 登录框 -->
		<div class="login-box">
			<!-- 科技感边框 -->
			<div class="tech-border">
				<div class="border-corner corner-tl"></div>
				<div class="border-corner corner-tr"></div>
				<div class="border-corner corner-bl"></div>
				<div class="border-corner corner-br"></div>
			</div>

			<!-- 右上角二维码切换按钮 -->
			<div class="qr-switch" @click="toggleQRCode">
				<el-icon :size="24" color="#fff">
					<component :is="showQRCode ? 'User' : 'FullScreen'" />
				</el-icon>
			</div>

			<!-- 二维码登录 -->
			<div v-if="showQRCode" class="qr-login">
				<div class="qr-title">扫码登录</div>
				<div class="qr-code">
					<img src="https://picsum.photos/seed/qrcode/200/200.jpg" alt="QR Code" />
					<!-- 扫描动画 -->
					<div class="scan-animation"></div>
				</div>
				<div class="qr-tips">打开手机APP扫码登录</div>
			</div>

			<!-- 账号/手机登录 -->
			<div v-else class="login-form">
				<div class="login-title">忆笙博客管理系统</div>
				<div class="login-subtitle">Welcome to YiSheng Blog Admin</div>

				<!-- Tab切换 -->
				<el-tabs v-model="activeTab" class="login-tabs">
					<el-tab-pane label="账号密码登录" name="account">
						<el-form :model="accountForm" :rules="accountRules" ref="accountFormRef">
							<el-form-item prop="username">
								<el-input v-model="accountForm.username" placeholder="请输入用户名" prefix-icon="User" class="custom-input" />
							</el-form-item>
							<el-form-item prop="password">
								<el-input
									v-model="accountForm.password"
									type="password"
									placeholder="请输入密码"
									prefix-icon="Lock"
									class="custom-input"
									show-password
								/>
							</el-form-item>
							<el-form-item prop="captcha">
								<div class="captcha-container">
									<el-input v-model="accountForm.captcha" placeholder="验证码" prefix-icon="Key" class="custom-input captcha-input" />
									<div class="captcha-img" @click="refreshCaptcha">
										<img :src="captchaUrl" alt="验证码" />
									</div>
								</div>
							</el-form-item>
							<!-- 记住我选项 -->
							<el-form-item>
								<div class="remember-forgot">
									<el-checkbox v-model="accountForm.remember" class="remember-me">记住我</el-checkbox>
									<el-link type="primary" class="forgot-password">忘记密码?</el-link>
								</div>
							</el-form-item>

							<el-form-item>
								<el-button type="primary" size="large" class="login-btn" @click="handleAccountLogin">
									<span class="btn-text">登 录</span>
									<div class="btn-effect"></div>
								</el-button>
							</el-form-item>
						</el-form>
					</el-tab-pane>

					<el-tab-pane label="手机号登录" name="phone">
						<el-form :model="phoneForm" :rules="phoneRules" ref="phoneFormRef">
							<el-form-item prop="phone">
								<el-input v-model="phoneForm.phone" placeholder="请输入手机号" prefix-icon="Iphone" size="large" class="custom-input" />
							</el-form-item>
							<el-form-item prop="code">
								<div class="code-container">
									<el-input v-model="phoneForm.code" placeholder="请输入验证码" prefix-icon="Message" size="large" class="custom-input code-input" />
									<el-button
										type="primary"
										size="large"
										class="code-btn"
										:disabled="countdown > 0 || isSendingCode"
										:loading="isSendingCode"
										@click="sendCode"
									>
										{{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
									</el-button>
								</div>
							</el-form-item>
							<el-form-item>
								<el-button type="primary" size="large" class="login-btn" @click="handlePhoneLogin">
									<span class="btn-text">登 录</span>
									<div class="btn-effect"></div>
								</el-button>
							</el-form-item>
						</el-form>
					</el-tab-pane>
				</el-tabs>
				<!-- 用户协议 -->
				<div class="agreement-container">
					<el-checkbox v-model="agreed" class="agreement-checkbox"> 我已阅读并同意 </el-checkbox>
					<el-link type="primary" @click="showAgreement('user')">《用户协议》</el-link>
					<span>和</span>
					<el-link type="primary" @click="showAgreement('privacy')">《隐私政策》</el-link>
				</div>
				<!-- 注册按钮 -->
				<div class="register-container">
					<span class="register-text">还没有账号？</span>
					<el-link type="primary" class="register-btn" @click="goToRegister"> 立即注册 </el-link>
				</div>

				<!-- 社交登录 -->
				<div class="social-login">
					<div class="social-title">其他登录方式</div>
					<div class="social-icons">
						<el-tooltip content="微信登录" placement="top">
							<div class="social-icon wechat" @click="socialLogin('wechat')">
								<el-icon><Wechat /></el-icon>
							</div>
						</el-tooltip>
						<el-tooltip content="QQ登录" placement="top">
							<div class="social-icon qq" @click="socialLogin('qq')">
								<el-icon><Service /></el-icon>
							</div>
						</el-tooltip>
						<el-tooltip content="GitHub登录" placement="top">
							<div class="social-icon github" @click="socialLogin('github')">
								<el-icon><GithubFilled /></el-icon>
							</div>
						</el-tooltip>
						<el-tooltip content="钉钉登录" placement="top">
							<div class="social-icon dingtalk" @click="socialLogin('dingtalk')">
								<el-icon><Bell /></el-icon>
							</div>
						</el-tooltip>
					</div>
				</div>
			</div>
		</div>

		<!-- 协议弹窗 -->
		<el-dialog v-model="agreementDialogVisible" :title="agreementTitle" width="50%" class="agreement-dialog">
			<div class="agreement-content" v-html="agreementContent"></div>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="agreementDialogVisible = false">关闭</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

// 状态管理
const activeTab = ref('account');
const showQRCode = ref(false);
const countdown = ref(0);
const captchaUrl = ref('https://picsum.photos/seed/captcha/100/40.jpg');
const agreed = ref(false);
const isSendingCode = ref(false);
const agreementDialogVisible = ref(false);
const agreementTitle = ref('');
const agreementContent = ref('');

// 表单引用
const accountFormRef = ref<FormInstance>();
const phoneFormRef = ref<FormInstance>();

// 账号密码表单
const accountForm = reactive({
	username: '',
	password: '',
	captcha: '',
	remember: false,
});

// 手机号表单
const phoneForm = reactive({
	phone: '',
	code: '',
});

// 验证规则
const accountRules: FormRules = {
	username: [
		{ required: true, message: '请输入用户名', trigger: 'blur' },
		{ min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
	],
	password: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
	],
	captcha: [
		{ required: true, message: '请输入验证码', trigger: 'blur' },
		{ len: 4, message: '验证码长度为4位', trigger: 'blur' },
	],
};

const phoneRules: FormRules = {
	phone: [
		{ required: true, message: '请输入手机号', trigger: 'blur' },
		{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
	],
	code: [
		{ required: true, message: '请输入验证码', trigger: 'blur' },
		{ len: 6, message: '验证码长度为6位', trigger: 'blur' },
	],
};

// 刷新验证码
const refreshCaptcha = () => {
	captchaUrl.value = `https://picsum.photos/seed/captcha${Date.now()}/100/40.jpg`;
};

// 发送验证码
const sendCode = async () => {
	if (!phoneFormRef.value) return;

	try {
		await phoneFormRef.value.validateField('phone');
		// 设置发送状态
		isSendingCode.value = true;

		// 模拟API请求
		setTimeout(() => {
			isSendingCode.value = false;
			ElMessage.success('验证码已发送');
			countdown.value = 60;

			const timer = setInterval(() => {
				countdown.value--;
				if (countdown.value <= 0) {
					clearInterval(timer);
				}
			}, 1000);
		}, 1000);
	} catch (error) {
		isSendingCode.value = false;
		console.error('验证失败:', error);
	}
};

// 账号密码登录
const handleAccountLogin = async () => {
	if (!accountFormRef.value) return;

	try {
		// 检查是否同意协议
		if (!agreed.value) {
			ElMessage.warning('请先阅读并同意用户协议和隐私政策');
			return;
		}
		await accountFormRef.value.validate();

		// 这里添加登录逻辑---

		ElMessage.success('登录成功');
	} catch (error) {
		console.error('验证失败:', error);
	}
};

// 手机号登录
const handlePhoneLogin = async () => {
	if (!phoneFormRef.value) return;

	try {
		// 检查是否同意协议
		if (!agreed.value) {
			ElMessage.warning('请先阅读并同意用户协议和隐私政策');
			return;
		}

		await phoneFormRef.value.validate();
		// 这里添加登录逻辑---

		ElMessage.success('登录成功');
		// 这里添加登录逻辑
	} catch (error) {
		console.error('验证失败:', error);
	}
};

// 社交登录
const socialLogin = (type: string) => {
	ElMessage.info(`${type} 登录功能开发中`);
};

// 切换二维码
const toggleQRCode = () => {
	showQRCode.value = !showQRCode.value;
};

// 显示协议
const showAgreement = (type: string) => {
  if (type === 'user') {
    agreementTitle.value = '用户协议'
    agreementContent.value = `
      <h3>忆笙博客用户协议</h3>
      <p>欢迎使用忆笙博客平台！请仔细阅读以下用户协议：</p>
      <h4>1. 服务内容</h4>
      <p>忆笙博客提供个人博客发布、内容分享、评论互动等服务。</p>
      <h4>2. 用户义务</h4>
      <p>用户需遵守国家法律法规，不得发布违法、违规内容。</p>
      <h4>3. 隐私保护</h4>
      <p>我们重视用户隐私，具体内容请参考《隐私政策》。</p>
      <h4>4. 免责声明</h4>
      <p>对于因不可抗力或非本平台原因造成的损失，本平台不承担责任。</p>
      <p>更新时间：2023年10月1日</p>
    `
  } else if (type === 'privacy') {
    agreementTitle.value = '隐私政策'
    agreementContent.value = `
      <h3>忆笙博客隐私政策</h3>
      <p>我们非常重视您的隐私保护，以下是我们的隐私政策：</p>
      <h4>1. 信息收集</h4>
      <p>我们收集您主动提供的信息，如注册资料、发布内容等。</p>
      <h4>2. 信息使用</h4>
      <p>我们使用收集的信息为您提供更好的服务，不会用于其他用途。</p>
      <h4>3. 信息共享</h4>
      <p>未经您的同意，我们不会与第三方共享您的个人信息。</p>
      <h4>4. 信息安全</h4>
      <p>我们采取各种安全措施保护您的个人信息不被泄露、毁损或丢失。</p>
      <p>更新时间：2023年10月1日</p>
    `
  }
  agreementDialogVisible.value = true
}
// 去注册
const goToRegister = () => {
  ElMessage.info('注册功能开发中')
  // 这里可以添加跳转到注册页面的逻辑
}

// 组件挂载时检查是否记住登录
onMounted(() => {
  const rememberLogin = localStorage.getItem('rememberLogin')
  if (rememberLogin === 'true') {
    const username = localStorage.getItem('username')
    if (username) {
      accountForm.username = username
      accountForm.remember = true
    }
  }
})

</script>

<style scoped>
.login-container {
	position: relative;
	width: 100vw;
	height: 100vh;
	overflow: hidden;
	background: #000;
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

.login-box {
	position: absolute;
	top: 50%;
	right: 10%;
	transform: translateY(-50%);
	width: 420px;
	background: rgba(0, 0, 0, 0.7);
	backdrop-filter: blur(10px);
	border-radius: 10px;
	border: 1px solid rgba(0, 255, 255, 0.2);
	box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5);
	z-index: 10;
	padding: 40px;
	transition: all 0.3s ease;
	overflow: hidden;
}

.login-box:hover {
	transform: translateY(-50%) scale(1.02);
	box-shadow: 0 25px 50px rgba(0, 0, 0, 0.6);
}

/* 科技感边框 */
.tech-border {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	pointer-events: none;
	z-index: 1;
}

.border-corner {
	position: absolute;
	width: 20px;
	height: 20px;
	border: 2px solid #00ffff;
}

.corner-tl {
	top: 10px;
	left: 10px;
	border-right: none;
	border-bottom: none;
}

.corner-tr {
	top: 10px;
	right: 10px;
	border-left: none;
	border-bottom: none;
}

.corner-bl {
	bottom: 10px;
	left: 10px;
	border-right: none;
	border-top: none;
}

.corner-br {
	bottom: 10px;
	right: 10px;
	border-left: none;
	border-top: none;
}

.qr-switch {
	position: absolute;
	top: 20px;
	right: 20px;
	width: 40px;
	height: 40px;
	background: rgba(0, 255, 255, 0.2);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	transition: all 0.3s ease;
	z-index: 2;
}

.qr-switch:hover {
	background: rgba(0, 255, 255, 0.3);
	transform: scale(1.1);
}

.qr-login {
	text-align: center;
	padding: 20px 0;
}

.qr-title {
	font-size: 24px;
	color: #fff;
	margin-bottom: 30px;
	font-weight: 600;
}

.qr-code {
	width: 200px;
	height: 200px;
	margin: 0 auto 20px;
	border-radius: 10px;
	overflow: hidden;
	border: 2px solid rgba(0, 255, 255, 0.3);
	position: relative;
}

.qr-code img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

/* 扫描动画 */
.scan-animation {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 2px;
	background: linear-gradient(90deg, transparent, rgba(0, 255, 255, 0.8), transparent);
	animation: qrScan 2s linear infinite;
}

@keyframes qrScan {
	0% {
		top: 0;
	}
	100% {
		top: 100%;
	}
}

.qr-tips {
	color: rgba(255, 255, 255, 0.7);
	font-size: 14px;
}

.login-form {
	padding: 20px 0;
	position: relative;
	z-index: 2;
}

.login-title {
	font-size: 28px;
	color: #fff;
	text-align: center;
	margin-bottom: 10px;
	font-weight: 700;
	background: linear-gradient(45deg, #00ffff, #0080ff);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
}

.login-subtitle {
	font-size: 14px;
	color: rgba(255, 255, 255, 0.6);
	text-align: center;
	margin-bottom: 30px;
}

.login-tabs {
	margin-bottom: 30px;
}

.login-tabs :deep(.el-tabs__nav-wrap::after) {
	background-color: rgba(255, 255, 255, 0.1);
}

.login-tabs :deep(.el-tabs__item) {
	color: rgba(255, 255, 255, 0.6);
	font-size: 16px;
}

.login-tabs :deep(.el-tabs__item.is-active) {
	color: #00ffff;
}

.login-tabs :deep(.el-tabs__active-bar) {
	background-color: #00ffff;
}

.custom-input {
	background: rgba(0, 0, 0, 0.3);
	border: 1px solid rgba(0, 255, 255, 0.3);
	border-radius: 10px;
	transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper) {
	background: transparent;
	box-shadow: none;
}

.custom-input :deep(.el-input__inner) {
	color: #fff;
	height: 40px;
}

.custom-input :deep(.el-input__prefix-inner) {
	color: rgba(255, 255, 255, 0.6);
}

.custom-input:hover {
	border-color: rgba(0, 255, 255, 0.5);
}

.custom-input:focus-within {
	border-color: #00ffff;
	box-shadow: 0 0 20px rgba(0, 255, 255, 0.3);
}

.captcha-container {
	display: flex;
	gap: 10px;
}

.captcha-input {
	flex: 1;
}

.captcha-img {
	width: 100px;
	height: 50px;
	border-radius: 10px;
	overflow: hidden;
	cursor: pointer;
	border: 1px solid rgba(0, 255, 255, 0.3);
	transition: all 0.3s ease;
}

.captcha-img:hover {
	border-color: rgba(0, 255, 255, 0.5);
}

.captcha-img img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.code-container {
	display: flex;
	gap: 10px;
}

.code-input {
	flex: 1;
}

.code-btn {
	width: 120px;
	background: linear-gradient(45deg, #00ffff, #0080ff);
	border: none;
	border-radius: 10px;
	transition: all 0.3s ease;
}

.code-btn:hover {
	transform: translateY(-2px);
	box-shadow: 0 10px 20px rgba(0, 255, 255, 0.3);
}

.login-btn {
	width: 100%;
	height: 50px;
	background: linear-gradient(45deg, #00ffff, #0080ff);
	border: none;
	border-radius: 10px;
	font-size: 18px;
	font-weight: 600;
	transition: all 0.3s ease;
	position: relative;
	overflow: hidden;
}

.btn-text {
	position: relative;
	z-index: 2;
}

.btn-effect {
	position: absolute;
	top: 0;
	left: -100%;
	width: 100%;
	height: 100%;
	background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
	transition: left 0.5s;
}

.login-btn:hover {
	transform: translateY(-2px);
	box-shadow: 0 15px 30px rgba(0, 255, 255, 0.4);
}

.login-btn:hover .btn-effect {
	left: 100%;
}

.social-login {
	margin-top: 30px;
	text-align: center;
}

.social-title {
	color: rgba(255, 255, 255, 0.6);
	font-size: 14px;
	margin-bottom: 20px;
}

.social-icons {
	display: flex;
	justify-content: center;
	gap: 20px;
}

.social-icon {
	width: 40px;
	height: 40px;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	transition: all 0.3s ease;
	font-size: 20px;
}

.social-icon:hover {
	transform: translateY(-3px) scale(1.1);
}

.social-icon.wechat {
	background: linear-gradient(45deg, #07c160, #1aad19);
}

.social-icon.qq {
	background: linear-gradient(45deg, #12b7f5, #00a1d6);
}

.social-icon.github {
	background: linear-gradient(45deg, #24292e, #586069);
}

.social-icon.dingtalk {
	background: linear-gradient(45deg, #1890ff, #40a9ff);
}

@media (max-width: 768px) {
	.login-box {
		right: 5%;
		width: 90%;
		max-width: 400px;
	}

	.tech-title-container {
		left: 5%;
		width: 90%;
		max-width: 300px;
	}

	.logo {
		width: 80px;
		height: 80px;
	}

	.logo-text {
		font-size: 32px;
	}

	.main-title {
		font-size: 36px;
	}

	.sub-title {
		font-size: 18px;
	}
}
/* 新增样式 */
.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.remember-me {
  color: rgba(255, 255, 255, 0.7);
}

.forgot-password {
  color: rgba(0, 255, 255, 0.8);
}

.agreement-container {
  margin: 20px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.5;
}

.agreement-container .el-checkbox {
  margin-right: 5px;
  height: auto;
  display: flex;
  align-items: center;
}

.agreement-container .el-link {
  margin: 0 3px;
  font-size: 14px;
  vertical-align: baseline;
  line-height: 1.5;
}

.agreement-container span {
  margin: 0 3px;
  line-height: 1.5;
}

.register-container {
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1.5;
}

.register-container .register-text {
  margin-right: 5px;
  line-height: 1.5;
}

.register-container .register-btn {
  font-size: 14px;
  vertical-align: baseline;
  line-height: 1.5;
}


.agreement-dialog {
  border-radius: 10px;
}

.agreement-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
  line-height: 1.6;
  color: #333;
}

.agreement-content h3 {
  color: #00ffff;
  margin-bottom: 15px;
}

.agreement-content h4 {
  color: #0080ff;
  margin-top: 15px;
  margin-bottom: 10px;
}

.agreement-content p {
  margin-bottom: 10px;
}

/* 手机验证码按钮样式优化 */
.code-btn {
  width: 120px;
  background: linear-gradient(45deg, #00ffff, #0080ff);
  border: none;
  border-radius: 10px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.code-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 255, 255, 0.3);
}

.code-btn:disabled {
  background: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.6);
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .agreement-container {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .agreement-checkbox {
    margin-bottom: 5px;
  }
}
</style>
