<template>
	<div class="login-wrapper">
		<!-- 科技感背景 -->
		<div class="tech-background">
			<div class="grid-overlay"></div>
			<div class="particles"></div>
			<div class="circuit-lines"></div>
			<!-- 新增：代码片段背景 -->
			<div class="code-snippets">
				<div class="code-snippet snippet-1">
					<span class="code-keyword">function</span> <span class="code-function">createBlog</span>() {
					<div class="code-line indent-1">
						<span class="code-keyword">const</span> <span class="code-variable">content</span> = <span
							class="code-string">"知识分享"</span>;
					</div>
					<div class="code-line indent-1"><span class="code-keyword">return</span> <span
							class="code-variable">content</span>;</div>
					}
				</div>
				<div class="code-snippet snippet-2">
					<span class="code-keyword">class</span> <span class="code-class">Developer</span> {
					<div class="code-line indent-1"><span class="code-function">constructor</span>() {</div>
					<div class="code-line indent-2">
						<span class="code-keyword">this</span>.<span class="code-property">skills</span> = [<span
							class="code-string">"创新"</span>,
						<span class="code-string">"分享"</span>];
					</div>
					<div class="code-line indent-1">}</div>
					}
				</div>
				<div class="code-snippet snippet-3">
					<span class="code-comment">// 探索技术边界</span>
					<div class="code-line">
						<span class="code-keyword">const</span> <span class="code-variable">future</span> = <span
							class="code-function">explore</span>();
					</div>
				</div>
			</div>
			<!-- 新增：二进制代码流 -->
			<div class="binary-rain"></div>
		</div>
	<div class="login-container">
		<div class="login-left">
			<div>
				<h1>忆笙博客系统</h1>
				<p>代码编织梦想，知识点亮未来<br />探索 · 创新 · 共享</p>
			</div>
			<div>
				<div class="education-icons">
					<el-icon class="icon">
						<Coin />
					</el-icon>
					<el-icon class="icon">
						<Reading />
					</el-icon>
					<el-icon class="icon">
						<Monitor />
					</el-icon>
					<el-icon class="icon">
						<UserFilled />
					</el-icon>
				</div>

				<!-- 新增：技术标签云 -->
				<div class="tech-tags">
					<span class="tag">JavaScript</span>
					<span class="tag">Vue</span>
					<span class="tag">Node.js</span>
					<span class="tag">Python</span>
					<span class="tag">AI</span>
					<span class="tag">开源</span>
				</div>
			</div>
		</div>

		<div class="login-right">
			<!-- 右上角扫码登录按钮 -->
			<div class="qr-login-btn" @click="toggleQRLogin">
				<el-tooltip content="扫码登录" placement="left" v-if="showQRLogin">
					<i class="cn cn-QRcode_line" style="font-size: 30px"></i>
				</el-tooltip>
				<el-tooltip content="账号密码登录" placement="left" v-if="!showQRLogin">
					<i class="cn cn-computer" style="font-size: 30px"></i>
				</el-tooltip>
			</div>

			<div class="login-header">
				<h2>欢迎登录</h2>
			</div>

			<!-- 只在非扫码登录模式下显示tab -->
			<el-tabs v-if="!showQRLogin" v-model="activeTab" class="login-tabs" @tab-click="handleTabClick">
				<el-tab-pane label="账号登录" name="account" />
				<el-tab-pane label="手机登录" name="phone" />
			</el-tabs>

			<!-- 账号密码登录 -->
			<el-form v-if="activeTab === 'account' && !showQRLogin" ref="accountFormRef" :model="accountForm"
				:rules="accountRules" label-width="0" @submit.prevent="handleAccountLogin">
				<el-form-item prop="username">
					<el-input v-model="accountForm.loginName" placeholder="账号" size="large" :prefix-icon="User" />
				</el-form-item>

				<el-form-item prop="password">
					<el-input v-model="accountForm.password" type="password" placeholder="密码" size="large"
						:prefix-icon="Lock" show-password />
				</el-form-item>

				<el-form-item prop="captcha" class="captcha-item">
					<div class="captcha-group">
						<el-input v-model="accountForm.code" placeholder="验证码" size="large" :prefix-icon="Key" />
						<img :src="captchaUrl" class="captcha-img" alt="验证码" @click="refreshCaptcha" />
					</div>
				</el-form-item>

				<div class="remember-forgot">
					<el-checkbox v-model="accountForm.remember">记住密码</el-checkbox>
					<el-link type="primary" underline="never" @click="handleForgotPassword">忘记密码？</el-link>
				</div>

				<el-button type="primary" size="large" class="btn-login" :loading="loading" @click="handleAccountLogin">
					登录 </el-button>
			</el-form>

			<!-- 手机号验证码登录 -->
			<el-form v-if="activeTab === 'phone' && !showQRLogin" ref="phoneFormRef" :model="phoneForm"
				:rules="phoneRules" label-width="0" @submit.prevent="handlePhoneLogin">
				<el-form-item prop="phoneNumber">
					<el-input v-model="phoneForm.phoneNumber" placeholder="手机号" size="large" :prefix-icon="Iphone" />
				</el-form-item>

				<el-form-item prop="smsCode" class="captcha-item">
					<div class="captcha-group">
						<el-input v-model="phoneForm.smsCode" placeholder="短信验证码" size="large" :prefix-icon="Key" />
						<el-button :disabled="countdown > 0" size="large" @click="sendSmsCode">
							{{ countdown > 0 ? `${countdown}s后重试` : '获取验证码' }}
						</el-button>
					</div>
				</el-form-item>

				<div class="remember-forgot">
					<el-checkbox v-model="phoneForm.rememberPhone">记住手机号</el-checkbox>
				</div>

				<el-button type="primary" size="large" class="btn-login" :loading="loading" @click="handlePhoneLogin">
					登录 </el-button>
			</el-form>

			<!-- 扫码登录 -->
			<div v-if="showQRLogin" class="qrcode-container">
				<div class="qrcode">
					<div class="qrcode-image">
						<i class="cn cn-QRcode_line" style="font-size: 30px" v-if="showQRLogin"></i>
					</div>
					<div class="scan-line"></div>
				</div>
				<p class="qr-tips">请使用手机扫描二维码登录</p>
				<el-button link @click="toggleQRLogin">返回账号登录</el-button>
			</div>

			<!-- 只在非扫码登录模式下显示其他登录方式 -->
			<template v-if="!showQRLogin">
				<el-divider>其他登录方式</el-divider>

				<div class="social-login">
					<el-tooltip content="微信登录" placement="top">
						<el-button circle class="social-btn wechat" @click="handleSocialLogin('wechat')">
							<i class="cn cn-wechat-fill" style="font-size: 30px"></i>
						</el-button>
					</el-tooltip>
					<el-tooltip content="QQ登录" placement="top">
						<el-button circle class="social-btn qq" @click="handleSocialLogin('qq')">
							<i class="cn cn-QQ-fill" style="font-size: 30px"></i>
						</el-button>
					</el-tooltip>
					<el-tooltip content="微博登录" placement="top">
						<el-button circle class="social-btn weibo" @click="handleSocialLogin('weibo')">
							<i class="cn cn-weibo-fill" style="font-size: 30px"></i>
						</el-button>
					</el-tooltip>
					<el-tooltip content="GitHub登录" placement="top">
						<el-button circle class="social-btn github" @click="handleSocialLogin('github')">
							<i class="cn cn-daimawangzhan" style="font-size: 30px"></i>
						</el-button>
					</el-tooltip>
				</div>
			</template>

			<div class="privacy-agreement">
				登录即表示您同意我们的
				<el-link type="primary" underline="never" @click="showAgreement('user')">《用户协议》</el-link>
				和
				<el-link type="primary" underline="never" @click="showAgreement('privacy')">《隐私政策》</el-link>
			</div>
		</div>
	</div>
	</div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, Lock, Iphone, Key, Reading, Monitor, UserFilled, Coin } from '@element-plus/icons-vue';
import { storeToRefs } from 'pinia';
import { useRoute, useRouter } from 'vue-router';
import { useThemeConfig } from '@/stores/themeConfig';
import Cookies from 'js-cookie';
import { Session } from '@/utils/storage';
import { initFrontEndControlRoutes } from '@/router/frontEnd';
import { initBackEndControlRoutes } from '@/router/backEnd';
import { formatAxis } from '@/utils/formatTime';
import { NextLoading } from '@/utils/loading';
import { useLoginApi } from '@/api/login';

// 当前激活的标签页
const activeTab = ref('account'); // 默认激活账号登录tab页
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig); // 主题配置
const loading = ref(false); // 登录按钮loading
const route = useRoute();           // 路由
const router = useRouter();         // 路由实例
const countdown = ref(0);           // 短信验证码倒计时
const captchaUrl = ref('');         // 验证码图片url
const showQRLogin = ref(false);     // 扫码登录
// 账号登录表单
const accountFormRef = ref();
const accountForm = reactive({
	loginName: 'admin',
	password: '1qaz!QAZ',
	code: '1234',
	type: 1, //登录类型 1、账号登录 2、手机号登录 3、邮箱登录 4、用户验证码 ...
	remember: false,
});
// 时间获取
const currentTime = computed(() => {
	return formatAxis(new Date());
});

// 手机登录表单
const phoneFormRef = ref();
const phoneForm = reactive({
	phoneNumber: '',
	smsCode: '',
	rememberPhone: false,
});
// 表单验证规则
const accountRules = {
	loginName: [{ required: true, message: '请输入学号/手机号/邮箱', trigger: 'blur' }],
	password: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
	],
	code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
};
const phoneRules = {
	phoneNumber: [
		{ required: true, message: '请输入手机号', trigger: 'blur' },
		{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
	],
	smsCode: [
		{ required: true, message: '请输入验证码', trigger: 'blur' },
		{ len: 6, message: '验证码长度为6位', trigger: 'blur' },
	],
};
// 刷新验证码
const refreshCaptcha = () => {
	captchaUrl.value = `https://picsum.photos/seed/captcha${Date.now()}/120/44`;
};
// 监听账号登录表单的变化
watch(
	() => [accountForm.loginName, accountForm.password, accountForm.code, phoneForm.phoneNumber, phoneForm.smsCode],
	() => {
		loading.value = false; // 表单变化时，将loading设为true
	}
);


//*********************************账号密码模块********************************* */
const loginApi = useLoginApi();
// 处理账号登录
const handleAccountLogin = async () => {
	if (!accountFormRef.value) return;

	const isValid = await accountFormRef.value.validate();
	if (isValid) {
		loading.value = true;
		const res = await loginApi.signIn(accountForm);
		if (res.code === 200) {
			const userInfos = res.data;
			Reflect.set(userInfos, 'btnRoles', userInfos.permissions); // 权限列表
			Reflect.set(userInfos, 'time', new Date().getTime());     // 登录时间
			//登陆成功，存储token
			// 存储 token 到浏览器缓存
			Session.set('token', userInfos.token);
			// Session.set('SM4-secretKey', res.data.data.sm4SecretKey);
			Cookies.set('loginName', userInfos.loginName);
			Cookies.set('userName', userInfos.userName);
			localStorage.setItem('loginName', userInfos.loginName);
			localStorage.setItem('userName', userInfos.userName);
			//存入用户输入的密码
			localStorage.setItem('password', accountForm.password);


			if (!themeConfig.value.isRequestRoutes) {
				// 前端控制路由，2、请注意执行顺序
				const isNoPower = await initFrontEndControlRoutes();
				signInSuccess(isNoPower);
			} else {
				// 添加完动态路由，再进行 router 跳转，否则可能报错 No match found for location with path "/"
				const isNoPower = await initBackEndControlRoutes();
				// 执行完 initBackEndControlRoutes，再执行 signInSuccess
				signInSuccess(isNoPower);
			}
		} else {
			ElMessage.warning(res.msg);
			loading.value = false;
		}
	}
};
// 登录成功后的跳转
const signInSuccess = (isNoPower: boolean | undefined) => {
	if (isNoPower) {
		ElMessage.warning('抱歉，您没有登录权限');
		Session.clear();
	} else {
		// 初始化登录成功时间问候语
		let currentTimeInfo = currentTime.value;
		// 登录成功，跳到转首页
		// 如果是复制粘贴的路径，非首页/登录页，那么登录成功后重定向到对应的路径中
		if (route.query?.redirect) {
			router.push({
				path: <string>route.query?.redirect,
				query: Object.keys(<string>route.query?.params).length > 0 ? JSON.parse(<string>route.query?.params) : '',
			});
		} else {
			router.push('/');
		}
		// 登录成功提示
		ElMessage.success(`${currentTimeInfo}，登录成功！`);
		// 添加 loading，防止第一次进入界面时出现短暂空白
		NextLoading.start();
	}
	loading.value = false;
};

//*********************************手机验证码登录模块********************************* */
// 发送短信验证码
const sendSmsCode = async () => {
	if (!phoneForm.phoneNumber) {
		ElMessage.error('请输入手机号');
		return;
	}

	if (!/^1[3-9]\d{9}$/.test(phoneForm.phoneNumber)) {
		ElMessage.error('手机号格式不正确');
		return;
	}

	countdown.value = 60;
	const timer = setInterval(() => {
		countdown.value--;
		if (countdown.value <= 0) {
			clearInterval(timer);
		}
	}, 1000);

	ElMessage.success('验证码已发送');
};
// 处理手机登录
const handlePhoneLogin = async () => {
	if (!phoneFormRef.value) return;

	await phoneFormRef.value.validate((valid: boolean) => {
		if (valid) {
			loading.value = true;

			// 模拟登录请求
			setTimeout(() => {
				loading.value = false;
				ElMessage.success('登录成功');

				// 记住手机号
				if (phoneForm.rememberPhone) {
					localStorage.setItem('phoneNumber', phoneForm.phoneNumber);
				}
			}, 1500);
		}
	});
};

//*********************************社交登录登录模块********************************* */
// 处理
const handleSocialLogin = (platform: string) => {
	ElMessage.info(`正在跳转到${platform === 'wechat' ? '微信' : platform === 'qq' ? 'QQ' : platform === 'weibo' ? '微博' : 'GitHub'}登录...`);
};

// 处理标签页切换
const handleTabClick = () => {
	// 切换标签时隐藏扫码登录
	showQRLogin.value = false;
};
// 切换扫码登录
const toggleQRLogin = () => {
	showQRLogin.value = !showQRLogin.value;
};
// 处理忘记密码
const handleForgotPassword = () => {
	ElMessageBox.alert('请联系管理员重置密码', '提示', {
		confirmButtonText: '确定',
	});
};

// 显示协议
const showAgreement = (type: string) => {
	const title = type === 'user' ? '用户协议' : '隐私政策';
	ElMessageBox.alert(`这里是${title}的具体内容...`, title, {
		confirmButtonText: '确定',
		customClass: 'agreement-dialog',
	});
};
// 组件挂载时初始化
onMounted(() => {
	refreshCaptcha();

	// 加载记住的信息
	const savedUsername = localStorage.getItem('loginName');
	const savedPassword = localStorage.getItem('password');
	const savedPhone = localStorage.getItem('phoneNumber');

	if (savedUsername && savedPassword) {
		accountForm.loginName = savedUsername;
		accountForm.password = savedPassword;
		accountForm.remember = true;
	}

	if (savedPhone) {
		phoneForm.phoneNumber = savedPhone;
		phoneForm.rememberPhone = true;
	}
});
</script>

<style scoped>
.login-wrapper {
	position: relative;
	width: 100%;
	height: 100vh;
	min-height: 100vh;
	background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
	display: flex;
	align-items: center;
	justify-content: center;
	overflow: hidden;
}

.login-container {
	display: flex;
	background: white;
	border-radius: 20px;
	box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
	overflow: hidden;
	width: 100%;
	max-width: 1000px;
	height: 710px;
	margin: 40px auto;
	position: relative;
}

/* 科技感背景 */
.tech-background {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 0;
	overflow: hidden;
}

.grid-overlay {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-image: linear-gradient(rgba(102, 126, 234, 0.05) 1px, transparent 1px),
		linear-gradient(90deg, rgba(102, 126, 234, 0.05) 1px, transparent 1px);
	background-size: 20px 20px;
	z-index: 1;
}

.particles {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 2;
}

.particles::before,
.particles::after {
	content: '';
	position: absolute;
	border-radius: 50%;
	background: rgba(102, 126, 234, 0.2);
	animation: float-particle 15s infinite ease-in-out;
}

.particles::before {
	width: 100px;
	height: 100px;
	top: 20%;
	left: 10%;
	animation-delay: 0s;
}

.particles::after {
	width: 150px;
	height: 150px;
	bottom: 20%;
	right: 10%;
	animation-delay: 5s;
}

@keyframes float-particle {

	0%,
	100% {
		transform: translate(0, 0);
	}

	25% {
		transform: translate(20px, -20px);
	}

	50% {
		transform: translate(-20px, 20px);
	}

	75% {
		transform: translate(10px, 10px);
	}
}

.circuit-lines {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-image: linear-gradient(90deg, transparent 98%, rgba(102, 126, 234, 0.1) 100%),
		linear-gradient(0deg, transparent 98%, rgba(102, 126, 234, 0.1) 100%);
	background-size: 50px 50px;
	z-index: 1;
	opacity: 0.5;
}

/* 新增：代码片段背景 */
.code-snippets {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 1;
	opacity: 0.15;
	pointer-events: none;
}

.code-snippet {
	position: absolute;
	font-family: 'Courier New', monospace;
	font-size: 12px;
	color: #ffffff;
	line-height: 1.5;
	background: rgba(0, 0, 0, 0.2);
	padding: 10px;
	border-radius: 5px;
	width: 180px;
}

.snippet-1 {
	top: 15%;
	left: 5%;
}

.snippet-2 {
	bottom: 20%;
	right: 10%;
}

.snippet-3 {
	top: 60%;
	left: 15%;
}

.code-keyword {
	color: #ff79c6;
}

.code-function {
	color: #50fa7b;
}

.code-string {
	color: #f1fa8c;
}

.code-variable {
	color: #8be9fd;
}

.code-class {
	color: #ff79c6;
}

.code-property {
	color: #66d9ef;
}

.code-comment {
	color: #6272a4;
}

.code-line {
	margin-left: 5px;
}

.indent-1 {
	padding-left: 15px;
}

.indent-2 {
	padding-left: 30px;
}

/* 新增：二进制代码流 */
.binary-rain {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: 1;
	opacity: 0.1;
	pointer-events: none;
	background-image: linear-gradient(0deg,
			transparent 24%,
			rgba(102, 126, 234, 0.05) 25%,
			rgba(102, 126, 234, 0.05) 26%,
			transparent 27%,
			transparent 74%,
			rgba(102, 126, 234, 0.05) 75%,
			rgba(102, 126, 234, 0.05) 76%,
			transparent 77%,
			transparent),
		linear-gradient(90deg,
			transparent 24%,
			rgba(102, 126, 234, 0.05) 25%,
			rgba(102, 126, 234, 0.05) 26%,
			transparent 27%,
			transparent 74%,
			rgba(102, 126, 234, 0.05) 75%,
			rgba(102, 126, 234, 0.05) 26%,
			transparent 77%,
			transparent);
	background-size: 50px 50px;
}

.login-left {
	flex: 1;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding: 60px 40px;
	color: white;
	display: flex;
	flex-direction: column;
	justify-content: space-around;
	position: relative;
	z-index: 1;
}

.login-left h1 {
	font-size: 36px;
	margin-bottom: 20px;
	position: relative;
	z-index: 1;
}

.login-left p {
	font-size: 18px;
	opacity: 0.9;
	line-height: 1.6;
	position: relative;
	z-index: 1;
}

.education-icons {
	display: flex;
	gap: 30px;
	margin-top: 20px;
	position: relative;
	z-index: 1;
	justify-content: center;
}

.icon {
	font-size: 48px;
	opacity: 0.8;
	transition: all 0.3s ease;
}

.icon:hover {
	transform: translateY(-5px);
	opacity: 1;
}

/* 新增：技术标签云 */
.tech-tags {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	margin-top: 20px;
	position: relative;
	z-index: 1;
}

.tag {
	background: rgba(255, 255, 255, 0.2);
	padding: 5px 12px;
	border-radius: 20px;
	font-size: 12px;
	transition: all 0.3s ease;
}

.tag:hover {
	background: rgba(255, 255, 255, 0.3);
	transform: scale(1.05);
}

.login-right {
	flex: 1;
	padding: 40px 50px;
	display: flex;
	flex-direction: column;
	position: relative;
	z-index: 1;
	height: 100%;
	overflow-y: auto;
}

/* 右上角扫码登录按钮 */
.qr-login-btn {
	position: absolute;
	top: 15px;
	right: 15px;
	cursor: pointer;
	color: #858585;
	transition: all 0.3s ease;
	z-index: 10;
}

.login-content {
	flex: 1;
	display: flex;
	flex-direction: column;
	min-height: 0;
	/* 添加：确保flex子元素可以收缩 */
}

.form-container {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: center;
	/* 垂直居中 */
	min-height: 400px;
	/* 设置最小高度 */
}

.qrcode-container {
	text-align: center;
	padding: 40px 0;
	min-height: 400px;
	/* 设置最小高度 */
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	flex: 1;
	/* 占用可用空间 */
}

.qr-login-btn:hover {
	transform: scale(1.1);
}

.login-header {
	text-align: center;
	margin-bottom: 30px;
}

.login-header h2 {
	color: #333;
	font-size: 28px;
	margin-bottom: 10px;
}

.login-tabs {
	margin-bottom: 30px;
}

.login-tabs :deep(.el-tabs__nav-wrap::after) {
	display: none;
}

.login-tabs :deep(.el-tabs__item) {
	font-size: 16px;
	color: #666;
	padding: 0 20px;
}

.login-tabs :deep(.el-tabs__item.is-active) {
	color: #667eea;
}

.login-tabs :deep(.el-tabs__active-bar) {
	background-color: #667eea;
}

.captcha-group {
	display: flex;
	gap: 10px;
}

.captcha-group .el-input {
	flex: 1;
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
	width: 120px;
	height: 44px;
	border-radius: 8px;
	cursor: pointer;
	border: 1px solid #e0e0e0;
}

.remember-forgot {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 25px;
}

.btn-login {
	width: 100%;
	height: 50px;
	font-size: 18px;
	font-weight: 500;
	margin-bottom: 20px;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border: none;
}

.btn-login:hover {
	transform: translateY(-2px);
	box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.qrcode-container {
	text-align: center;
	padding: 40px 0;
	min-height: 400px;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
}

.qrcode {
	width: 200px;
	height: 200px;
	margin: 0 auto 20px;
	background: white;
	border: 1px solid #e0e0e0;
	border-radius: 10px;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #667eea;
	position: relative;
	overflow: hidden;
}

.qrcode-image {
	z-index: 1;
}

/* 扫描线动画 */
.scan-line {
	position: absolute;
	width: 100%;
	height: 2px;
	background: linear-gradient(90deg, transparent, #667eea, transparent);
	top: 0;
	left: 0;
	animation: scan 2s linear infinite;
	z-index: 2;
}

@keyframes scan {
	0% {
		top: 0;
	}

	100% {
		top: 100%;
	}
}

.qr-tips {
	color: #666;
	font-size: 14px;
	margin-bottom: 15px;
}

.social-login {
	display: flex;
	justify-content: center;
	gap: 15px;
	margin-bottom: 20px;
}

.social-btn {
	width: 45px;
	height: 45px;
	border: 1px solid #e0e0e0;
	background: white;
	transition: all 0.3s ease;
}

.social-btn:hover {
	transform: translateY(-3px);
	box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.social-btn.wechat {
	color: #07c160;
}

.social-btn.qq {
	color: #12b7f5;
}

.social-btn.weibo {
	color: #e6162d;
}

.social-btn.github {
	color: #333;
}

.privacy-agreement {
	text-align: center;
	font-size: 12px;
	color: #999;
	line-height: 1.6;
}

.agreement-dialog :deep(.el-message-box__content) {
	padding: 20px;
	max-height: 400px;
	overflow-y: auto;
}

@media (max-width: 768px) {
	.login-container {
		flex-direction: column;
		max-width: 400px;
		margin: 20px;
		height: auto;
		min-height: 600px;
	}

	.login-left {
		padding: 30px 20px;
		min-height: 200px;
	}

	.login-left h1 {
		font-size: 24px;
	}

	.login-left p {
		font-size: 14px;
	}

	.icon {
		font-size: 36px;
	}

	.login-right {
		padding: 30px 20px;
		height: auto;
		min-height: 400px;
	}

	.form-container,
	.qrcode-container {
		min-height: 300px;
	}

	.tech-tags {
		margin-top: 10px;
	}
}
</style>
