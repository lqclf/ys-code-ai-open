import { createApp } from 'vue';        
import pinia from '@/stores/index';
import App from '@/App.vue';
import router from '@/router';
import { directive } from '@/directive/index';
import { i18n } from '@/i18n/index';
import other from '@/utils/other';
import ElementPlus from 'element-plus';  
import '@/theme/index.scss';
import VueGridLayout from 'vue-grid-layout';
import VxeUIAll from 'vxe-pc-ui';
import 'vxe-pc-ui/lib/style.min.css';

import VxeUITable from 'vxe-table';
import 'vxe-table/lib/style.min.css';

import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

//全局引入YsTable
import '@/theme/remixicon/remixicon.css'
//弹窗组件
import hljsVuePlugin from "@highlightjs/vue-plugin";
import 'highlight.js/lib/common';

const app = createApp(App);


directive(app); // 引入自定义指令
other.elSvg(app); // 引入svg图标 并注册全局组件
app.use(pinia)
    .use(router)
    .use(ElementPlus, {
        locale: zhCn,
    })
    .use(i18n)
    .use(VueGridLayout)
    .use(VxeUIAll)
    .use(VxeUITable)
    .use(hljsVuePlugin)
    .mount('#app');
