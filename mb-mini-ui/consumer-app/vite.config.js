import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Consumer app runs on 5173 by default.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: true,
  },
});
