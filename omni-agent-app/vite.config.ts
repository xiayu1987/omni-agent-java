import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'


export default defineConfig({
  plugins: [vue()],
  server: {
    port: 20002,
    proxy: {
      // 1. 把 /agent/api 开头的请求代理到 http://localhost:20003/omni/agent
      '/agent/api': {
        target: 'http://localhost:20003/omni/agent',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/agent\/api/, ''), // 去掉 /agent/api 前缀
      }
    }
  },
  build: {
    // lib: {
    //   entry: 'src/main.ts',
    //   name: 'omni-agent-app',
    //   fileName: (format) => `omni-agent-app.${format}.js`,
    // },
    outDir: 'dist/app',
    rollupOptions: {
      // 可以配置外部依赖等
    }
  }
})