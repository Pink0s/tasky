import {createBrowserRouter} from "react-router-dom";
import Login from "../pages/Login";
import ProtectedRoute from "../utils/protectedRoute.jsx";
import React from "react";
import DashBoard from "../pages/DashBoard/index.jsx";
import Profile from "../pages/Profile/index.jsx";
import ProjectView from "../pages/DashBoard/ProjectView/index.jsx";
import Run from "../pages/Run";
import Task from "../pages/Task";
import Feature from "../pages/Feature";

export const page = {
    Login: "/",
    DashBoard: "/dashboard",
    Profile: "/profile",
    Project: "/project/:id",
    Run: "/project/:projectId/run/:runId",
    Feature: "/project/:projectId/run/:runId/feature/:id",
    Task: "/project/:projectId/run/:runId/feature/:featureId/task/:id",
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
            },
            {
                path:page.Project,
                element: <ProtectedRoute><ProjectView></ProjectView></ProtectedRoute>
            },
            {
                path:page.Run,
                element: <ProtectedRoute><Run></Run></ProtectedRoute>
            },
            {
                path:page.Feature,
                element: <ProtectedRoute><Feature></Feature></ProtectedRoute>
            },
            {
                path:page.Task,
                element: <ProtectedRoute><Task></Task></ProtectedRoute>
            }
        ]
    )

    return {router}

}



