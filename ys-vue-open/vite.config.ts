import { resolve } from 'path';
import { defineConfig, loadEnv, ConfigEnv, type UserConfig } from 'vite';
import { buildConfig } from './src/utils/build';

const pathResolve = (dir: string) => {
	return resolve(__dirname, '.', dir);
};

const alias: Record<string, string> = {
	'@': pathResolve('./src/'),
	'vue-i18n': 'vue-i18n/dist/vue-i18n.cjs.js',
};

const viteConfig = defineConfig(async (mode: ConfigEnv): Promise<UserConfig> => {
	const env = loadEnv(mode.mode, process.cwd());
	
	// 动态导入 ESM 插件
	const [vue, vueJsx, viteCompression, vueSetupExtend] = await Promise.all([
		import('@vitejs/plugin-vue').then(m => m.default),
		import('@vitejs/plugin-vue-jsx').then(m => m.default),
		import('vite-plugin-compression').then(m => m.default),
		import('vite-plugin-vue-setup-extend').then(m => m.default)
	]);
	
	return {
		plugins: [vue(), vueJsx(), vueSetupExtend(), viteCompression(), JSON.parse(env.VITE_OPEN_CDN) ? buildConfig.cdn() : null],
		root: process.cwd(),
		resolve: { 
			alias,
			// 导入时可省略的扩展名
      		extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
		 },
		base: mode.command === 'serve' ? './' : env.VITE_PUBLIC_PATH,
		server: {
			host: '0.0.0.0',
			port: env.VITE_PORT as unknown as number,
			open: JSON.parse(env.VITE_OPEN),
			hmr: true,
			proxy: {
				'/api': {
					target: env.VITE_API_URL,
					ws: true,
					changeOrigin: true,
					rewrite: (path) => path.replace(/^\/api/, ''),
				},
			},
		},
		build: {
			outDir: 'dist',
			chunkSizeWarningLimit: 1500,
			rollupOptions: {
				output: {
					chunkFileNames: 'assets/js/[name]-[hash].js',
					entryFileNames: 'assets/js/[name]-[hash].js',
					assetFileNames: 'assets/[ext]/[name]-[hash].[ext]',
					manualChunks(id) {
						if (id.includes('node_modules')) {
							return id.toString().match(/\/node_modules\/(?!.pnpm)(?<moduleName>[^\/]*)\//)?.groups!.moduleName ?? 'vender';
						}
					},
				},
				...(JSON.parse(env.VITE_OPEN_CDN) ? { external: buildConfig.external } : {}),
			},
		},
		css: { preprocessorOptions: { 
				css: { charset: false },
				scss: {
					api: 'modern-compiler',
          			silenceDeprecations: ['legacy-js-api'],
				}
		 	} 
		},
		define: {
			__VUE_I18N_LEGACY_API__: JSON.stringify(false),
			__VUE_I18N_FULL_INSTALL__: JSON.stringify(false),
			__INTLIFY_PROD_DEVTOOLS__: JSON.stringify(false),
			__NEXT_VERSION__: JSON.stringify(process.env.npm_package_version),
			__NEXT_NAME__: JSON.stringify(process.env.npm_package_name),
		},
	};
});

export default viteConfig;
