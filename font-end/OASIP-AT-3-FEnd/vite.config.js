import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server:{
    base: "/at3",
    proxy: {
      '/api':{
        // target: 'http://intproj21.sit.kmutt.ac.th',
        target: 'http://10.4.56.83:8443',
        changeOrigin:true,
        secure:false,
        // rewrite:  (path)=>path.replace(/^\/api/,'')
      },
    },
  },
});
