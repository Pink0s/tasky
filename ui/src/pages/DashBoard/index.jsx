import {useAuth} from "../../contexts/AuthContext.jsx";
import AdminDashboard from "./AdminDashboard/index.jsx";
import ProjectManagerDashBoard from "./ProjectManagerDashBoard/index.jsx";
import UserDashBoard from "./UserDashBoard/index.jsx";
import {useEffect, useState} from "react";

const DashBoard = () => {
    const {user,authLoading} = useAuth();
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        if(!authLoading) {
            setLoading(false)
        }
    },[authLoading])

    if(loading) {
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