import {CheckCircleIcon, PaperClipIcon} from '@heroicons/react/20/solid'
import SideBar from "../DashBoard/SideBar.jsx";
import {useAuth} from "../../contexts/AuthContext.jsx";
import {useFormik} from "formik";
import * as Yup from 'yup';
import {changePassword} from "../../utils/client.js";
import ErrorNotification from "../../components/ErrorNotification.jsx";
import {useState} from "react";
export default function Profile() {

    const {user, authLoading} = useAuth();
    const [notify, setNotify] = useState(null);
    const [isVisible, setIsVisible] = useState(true);

    const handleClose = () => {
        setIsVisible(false);
        setNotify(null);
    };


    const formik = useFormik({
        initialValues:{
            oldPassword:'',
            newPassword:'',
            confirmPassword:''
        },
        validationSchema: Yup.object({
            oldPassword: Yup.string()
                .required('Required'),
            newPassword: Yup.string()
                .min(8, 'Must be 8 characters or more')
                .required('Required')
                .matches(/[0-9]/, 'Password requires a number')
                .matches(/[a-z]/, 'Password requires a lowercase letter')
                .matches(/[A-Z]/, 'Password requires an uppercase letter')
                .matches(/[^\w]/, 'Password requires a symbol'),
            confirmPassword: Yup.string().oneOf(
                [Yup.ref('newPassword'), null],'Passwords must match')
                .required('Required'),
        }),
        onSubmit: values => {
            const oldPassword = values.oldPassword
            const newPassword = values.newPassword
            changePassword({
                oldPassword, newPassword
            }).then((res) => {
                setNotify({message: "Success", color: "green"})
                setIsVisible(true)
            }).catch((err) => {
                setNotify({message: err.response.data.message, color: "red"})
                setIsVisible(true)
            })

        }
    })

    if(authLoading) {
        return <div>Loading ....</div>
    }
    return (
        <SideBar pageName="My profile">
            <div>
                <div className="py-4 px-4 sm:px-0">
                    <h3 className="text-base font-semibold leading-7 text-gray-900">My profile</h3>
                    <p className="mt-1 max-w-2xl text-sm leading-6 text-gray-500">Personal details.</p>
                </div>
                <div className="mt-6 border-t border-gray-100">
                    <dl className="divide-y divide-gray-100">
                        <div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
                            <dt className="text-sm font-medium leading-6 text-gray-900">Full name</dt>
                            <dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">{user.firstName +" "+user.lastName}</dd>
                        </div>
                        <div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
                            <dt className="text-sm font-medium leading-6 text-gray-900">Role</dt>
                            <dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">{user.role}</dd>
                        </div>
                        <div className="px-4 py-6 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-0">
                            <dt className="text-sm font-medium leading-6 text-gray-900">Email address</dt>
                            <dd className="mt-1 text-sm leading-6 text-gray-700 sm:col-span-2 sm:mt-0">{user.email}</dd>
                        </div>
                    </dl>
                </div>
                <form onSubmit={formik.handleSubmit} className="grid grid-cols-1 gap-x-8 gap-y-10 border-b border-t pt-4 border-gray-900/10 pb-12 md:grid-cols-3">
                    <div>
                        <h2 className="text-base font-semibold leading-7 text-gray-900">Change your Password</h2>

                    </div>

                    <div className="grid max-w-2xl grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-6 md:col-span-2">
                        <div className="sm:col-span-3">
                            <label htmlFor="first-name" className="block text-sm font-medium leading-6 text-gray-900">
                                New password
                            </label>
                            <div className="mt-2">
                                <input
                                    type="password"
                                    name="newPassword"
                                    id="newPassword"
                                    onChange={formik.handleChange}
                                    value={formik.values.newPassword}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primaryButton sm:text-sm sm:leading-6"
                                />
                                {formik.touched.newPassword && formik.errors.newPassword ? (
                                    <div className="text-error">{formik.errors.newPassword}</div>
                                ) : null}
                            </div>
                        </div>

                        <div className="sm:col-span-3">
                            <label htmlFor="last-name" className="block text-sm font-medium leading-6 text-gray-900">
                                New password confirmation
                            </label>
                            <div className="mt-2">
                                <input
                                    type="password"
                                    name="confirmPassword"
                                    id="confirmPassword"
                                    onChange={formik.handleChange}
                                    value={formik.values.confirmPassword}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primaryButton sm:text-sm sm:leading-6"
                                />
                                {formik.touched.confirmPassword && formik.errors.confirmPassword ? (
                                    <div className="text-error">{formik.errors.confirmPassword}</div>
                                ) : null}
                            </div>
                        </div>

                        <div className="sm:col-span-3">
                            <label htmlFor="email" className="block text-sm font-medium leading-6 text-gray-900">
                                Actual password
                            </label>
                            <div className="mt-2">
                                <input
                                    id="oldPassord"
                                    name="oldPassword"
                                    type="password"
                                    onChange={formik.handleChange}
                                    value={formik.values.oldPassword}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primaryButton sm:text-sm sm:leading-6"
                                />
                                {formik.touched.oldPassword && formik.errors.oldPassword ? (
                                    <div className="text-error">{formik.errors.oldPassword}</div>
                                ) : null}
                            </div>
                        </div>
                        <div className="col-span-2 flex align-bottom">
                            <button
                                type="submit"
                                className="inline-flex items-center gap-x-2 rounded-md bg-primaryButton mt-8 mb-6 px-3.5 py-2.5 text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                            >
                                Submit
                                <CheckCircleIcon className="-mr-0.5 h-5 w-5" aria-hidden="true" />
                            </button>
                        </div>
                    </div>

                </form>
                {notify && (
                    <ErrorNotification isVisible={isVisible} handleClose={handleClose} message={notify.message} color={notify.color}/>
                )}
            </div>

        </SideBar>
    )
}
