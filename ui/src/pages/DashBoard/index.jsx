import {useAuth} from "../../contexts/AuthContext.jsx";
import AdminDashboard from "./AdminDashboard/index.jsx";
import ProjectManagerDashBoard from "./ProjectManagerDashBoard/index.jsx";
import UserDashBoard from "./UserDashBoard/index.jsx";
import {useEffect, useState} from "react";
import {getMyProfile} from "../../utils/client.js";

const DashBoard = () => {
    const {user,authLoading,isUserAuthenticated, parseJwt, setAuthLoading, setUser} = useAuth();

    useEffect(() => {

        if(isUserAuthenticated()) {
            const payload = parseJwt(localStorage.getItem("access_token"));
            const date = Math.floor(Date.now() / 1000)

            if(date < payload.exp) {
                getMyProfile()
                    .then( (res) => {
                        setUser(res.data)
                    })
                    .catch((err) => {
                        localStorage.removeItem("access_token")
                    }).finally(() => {
                    setAuthLoading(false);
                })
            } else {
                localStorage.removeItem("access_token")
                setAuthLoading(false);
            }
        }

    },[])

    if(authLoading) {
        return <div>Loading ...</div>
    } else {
        switch (user.role) {
            case "ADMIN":
                return <AdminDashboard/>
            case "PROJECT_MANAGER":
                return <ProjectManagerDashBoard/>
            case "USER":
                return <UserDashBoard/>
            default:
                return <div>Error Role not correct</div>

        }
    }
}

export default DashBoard