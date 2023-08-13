import React from 'react'
import ReactDOM from 'react-dom/client'
import Home from './pages/Home.jsx'
import './styles/index.css'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "./pages/Login.jsx";
import AuthProvider from "./contexts/AuthContext.jsx";
import ProtectedRoute from "./utils/protectedRoute.jsx";

const router = createBrowserRouter(
    [
        {
            path: "/",
            element: <Login />,
        },
        {
            path: "/test",
            element: <ProtectedRoute><Home /></ProtectedRoute>,
        },
    ]
)

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
      <AuthProvider>
          <RouterProvider router={router}/>
      </AuthProvider>
  </React.StrictMode>,
)
