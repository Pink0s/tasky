import * as Yup from 'yup'
import {useFormik} from "formik";
import {useAuth} from "../contexts/AuthContext.jsx";
import {useNavigate} from "react-router-dom";



const Login = () => {
    const { user, login } = useAuth()
    const navigate = useNavigate()
    const formik = useFormik({
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
            //alert(JSON.stringify(values, null, 2));
            login(values).then(
                navigate("/test")
            ).catch(err => {
                console.error(err)
            })
        },
    });

    return <div className="h-screen overflow-hidden flex items-center justify-center bg-gray-900">
        <div className="flex items-center min-h-screen bg-white dark:bg-gray-900">
            <div className="container mx-auto">
                <div className="max-w-md mx-auto my-10">
                    <div className="text-center">
                        <h1 className="my-3 text-3xl font-semibold text-gray-700 dark:text-gray-200">Sign in</h1>
                        <p className="text-gray-500 dark:text-gray-400">Sign in to access your account</p>
                    </div>
                    <div className="m-7">
                        <form onSubmit={formik.handleSubmit}>
                            <div className="mb-6">
                                <label htmlFor="email" className="block mb-2 text-sm text-gray-600 dark:text-gray-400">Email
                                    Address</label>
                                <input type="email" id="username" placeholder="you@tasky.com" {...formik.getFieldProps('username')}
                                       className="w-full px-3 py-2 placeholder-gray-300 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300 dark:bg-gray-700 dark:text-white dark:placeholder-gray-500 dark:border-gray-600 dark:focus:ring-gray-900 dark:focus:border-gray-500"/>
                                {formik.touched.username && formik.errors.username ? (
                                    <div className="text-sm px-3 py-2 text-red-400 w-full">{formik.errors.username}</div>
                                ) : null}
                            </div>
                            <div className="mb-6">
                                <div className="flex justify-between mb-2">
                                    <label htmlFor="password"
                                           className="text-sm text-gray-600 dark:text-gray-400">Password</label>
                                </div>
                                <input type="password" id="password" placeholder="Your Password" {...formik.getFieldProps('password')}
                                       className="w-full px-3 py-2 placeholder-gray-300 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-indigo-100 focus:border-indigo-300 dark:bg-gray-700 dark:text-white dark:placeholder-gray-500 dark:border-gray-600 dark:focus:ring-gray-900 dark:focus:border-gray-500"/>
                                {formik.touched.password && formik.errors.password ? (
                                    <div className="text-sm px-3 py-2 text-red-400 w-full">{formik.errors.password}</div>
                                ) : null}
                            </div>
                            <div className="mb-6">
                                <button type="submit"
                                        className="w-full px-3 py-4 text-white bg-indigo-500 rounded-md focus:bg-indigo-600 focus:outline-none">Sign
                                    in
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
}

export default Login