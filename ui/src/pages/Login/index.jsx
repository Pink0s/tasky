import {useLogin} from "../../hooks/useLogin.jsx";
import ErrorNotification from "../../components/ErrorNotification.jsx";
import FormWrapper from "./FormWrapper.jsx";
import SignInForm from "./SignInForm.jsx";

const Login = () => {

    const {formik, isVisible, handleClose, error} = useLogin()

    return <div className="h-screen overflow-hidden flex items-center justify-center bg-primary">
        <div className="flex items-center min-h-screen bg-primary">
            <FormWrapper title="Sign in" description="Sign in to access your account">
                <SignInForm formik={formik}/>
            </FormWrapper>
            {error && (
                <ErrorNotification isVisible={isVisible} handleClose={handleClose} message={error.message} color={error.color}/>
            )}
        </div>
    </div>
}

export default Login