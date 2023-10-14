import {useAuth} from "../contexts/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import {useFormik} from "formik";
import * as Yup from "yup";
import {page} from "./useRouter.jsx";

export function useLogin() {
    const { user, login } = useAuth()
    const navigate = useNavigate()
    const [error, setError] = useState(null);
    const [isVisible, setIsVisible] = useState(true);

    const handleClose = () => {
        setIsVisible(false);
        setError(false);
    };

    const formik= useFormik({
        initialValues: {
            username: '',
            password: '',
        },
        validationSchema: Yup.object({
            username: Yup
                .string()
                .email("Username must be a valid email")
                .required('Username is required'),
            password: Yup.string()
                .max(30, 'Must be 30 characters or less')
                .min(5,'Must be 5 characters or more')
                .required('Password is required')
        }),
        onSubmit: values => {
            setError(null)
            setIsVisible(true)
            login(values)
                .then( res => {navigate(page.DashBoard)})
                .catch( (error) => {
                    setError({message: error.response.data.message, color: "red"})
                    setIsVisible(true)
                })
        },
    });

    return {formik, isVisible, handleClose, error}
}