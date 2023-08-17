import React from 'react'
import ReactDOM from 'react-dom/client'
import './styles/index.css'
import {RouterProvider} from "react-router-dom";
import AuthProvider from "./contexts/AuthContext.jsx";
import {useRouter} from "./hooks/useRouter.jsx";

const {router} = useRouter()

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
      <AuthProvider>
          <RouterProvider router={router}/>
      </AuthProvider>
  </React.StrictMode>,
)
