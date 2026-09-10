import { createApp } from 'vue';        
import pinia from '@/stores/index';
import App from '@/App.vue';
import router from '@/router';
import { directive } from '@/directive/index';
import { i18n } from '@/i18n/index';
import other from '@/utils/other';
import '@/theme/index.scss';
import VueGridLayout from 'vue-grid-layout';
import VxeUIAll from 'vxe-pc-ui';
import 'vxe-pc-ui/es/style.css';

import VxeUITable from 'vxe-table';
import 'vxe-table/es/style.css';

// Element Plus 按需引入（替代全量引入，减少打包体积约 60%）
import { registerElementPlus, getElementPlusLocale } from '@/utils/elementPlus';
import { ElConfigProvider } from 'element-plus';


//全局引入YsTable
import YsTable from '@/components/YsTable/index.vue';
import '@/theme/remixicon/remixicon.css'
//弹窗组件
import YsDialog from '@/components/YsDialog/index.vue';

// ECharts 全局注册 GraphicComponent（被多个大屏组件使用的图形组件）
import * as echarts from 'echarts/core';
import { GraphicComponent } from 'echarts/components';
echarts.use([GraphicComponent]);

const app = createApp(App);

directive(app); // 引入自定义指令
other.elSvg(app); // 引入svg图标 并注册全局组件

// 注册全局组件
app.component('YsDialog', YsDialog)
app.component('YsTable', YsTable)

// 按需注册 Element Plus 组件（必须在 mount 之前调用）
registerElementPlus(app);

app.use(ElConfigProvider, {
    locale: getElementPlusLocale(),
});

app.use(pinia)
    .use(router)
    .use(i18n)
    .use(VueGridLayout)
    .use(VxeUIAll)
    .use(VxeUITable)
    .mount('#app');
