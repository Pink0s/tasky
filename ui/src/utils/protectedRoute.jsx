import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../contexts/AuthContext.jsx";

/**
 * ProtectedRoute component ensures that the provided children components are only
 * rendered if the user is authenticated. If the user is not authenticated, the
 * component will redirect them to the specified route.
 *
 * @component
 * @param {Object} props - Component props
 * @param {React.ReactNode} props.children - The child components to be rendered if the user is authenticated.
 * @returns {React.ReactNode|string} The provided children components if authenticated, otherwise an empty string.
 */
const ProtectedRoute = ({ children }) => {
    const { isUserAuthenticated } = useAuth()
    const navigate = useNavigate();

    useEffect(() => {
        if (!isUserAuthenticated()) {
            navigate("/")
        }
    })

    return isUserAuthenticated() ? children : "";
}

export default ProtectedRoute;