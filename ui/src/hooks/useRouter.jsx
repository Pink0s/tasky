import {createBrowserRouter} from "react-router-dom";
import Login from "../pages/Login";
import ProtectedRoute from "../utils/protectedRoute.jsx";
import React from "react";
import DashBoard from "../pages/DashBoard/index.jsx";
import Profile from "../pages/Profile/index.jsx";

export const page = {
    Login: "/",
    DashBoard: "/dashboard",
    Profile: "/profile"
}

export function useRouter() {

    const router = createBrowserRouter(
        [
            {
                path: page.Login,
                element: <Login />,
            },
            {
                path: page.DashBoard,
                element: <ProtectedRoute><DashBoard /></ProtectedRoute>,
            },
            {
                path:page.Profile,
                element: <ProtectedRoute><Profile/></ProtectedRoute>
            }
        ]
    )

    return {router}

}



