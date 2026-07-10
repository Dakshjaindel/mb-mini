import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Admin app runs on 5174 by default (consumer uses 5173).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5174,
    host: true,
  },
});
