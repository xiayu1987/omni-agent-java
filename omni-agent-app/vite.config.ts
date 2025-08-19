import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'


export default defineConfig({
  plugins: [vue()],
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