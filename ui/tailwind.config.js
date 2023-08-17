/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
        colors: {
            'primary': '#181616',
            'secondary': '#f3f2f2',
            'primaryButton': '#6b766e',
            'secondaryButton': '#e4e2e2',
            'accent': '#3c3a40',
            'error': "#FF9494",
            'success': "#4BB543"
          }
      },

  },
  plugins: [require('@tailwindcss/forms')],
}

