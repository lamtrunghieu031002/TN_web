/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          red: '#b10f0f',
          redDark: '#970c0c',
        },
      },
    },
  },
  plugins: [],
}
