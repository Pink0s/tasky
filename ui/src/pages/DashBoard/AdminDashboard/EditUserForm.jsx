import {Fragment, useEffect, useState} from 'react'
import { Dialog, Transition } from '@headlessui/react'
import { XMarkIcon } from '@heroicons/react/24/outline'
import {useFormik} from "formik";
import * as Yup from 'yup';
import {createUser, deleteUser, fetchUserById, updateUser} from "../../../utils/client.js";
import ErrorNotification from "../../../components/ErrorNotification.jsx";

export default function EditUserForm({setOpen,open,setShowAlertCompletion, setAlertContent, userId}) {
    const [error, setError] = useState(null);
    const [visible, setIsVisible] = useState(null);
    const [user, setUser] = useState({})
    const [loading, setIsLoading] = useState(true)
    const handleClose = () => {
        setError(false)
        setIsVisible(false)
    }

    async function userDelete() {
        deleteUser(
            Number(userId)
        ).then((res) => {
            setOpen(false)
            setShowAlertCompletion(true)

            const data = {
                content: `User successfully deleted`,
                title: "User Successfully deleted"
            }

            setAlertContent((alertContent) => ({
                ...alertContent,
                ...data
            }))

        }).catch((err) => {
            setIsVisible(true)
            setError({message: err.response.data.message, color: "red"})
            console.error(err)
        })
    }


    useEffect(() => {
        const id = Number(userId)
        fetchUserById({id}).then(
            (res) => {
                setUser(res.data)
                setIsLoading(false)
            }
        ).catch((err) => {
            setIsVisible(true)
            setError({message: err.response.data.message, color: "red"})
            setIsLoading(false)
        })
    },[])


    const formik = useFormik(
        {
            initialValues: {
                passwordReset: false,
                role: user.role
            },
            onSubmit: values => {
                const {passwordReset, role} = values;
                const id = Number(userId)

                const body = {
                    passwordReset,
                    role
                }

                updateUser(Number(userId),body)
                    .then((res) => {
                        setOpen(false)
                        setShowAlertCompletion(true)

                        let data = {
                            content: `Role successfully updated`,
                            title: "User Successfully updated"
                        }

                        if(res.data.password) {

                            data = {
                                content: `Here is the password for the updated user.
                                Kindly transfer it to the concerned user.
                                Please note that once notification is closed, it will not be retrievable. 
                                password : ${res.data.password}
                              `,
                                title: "User Successfully updated"
                            }

                        }

                        setAlertContent((alertContent) => ({
                            ...alertContent,
                            ...data
                        }))

                    })
                    .catch((err) => {
                        setIsVisible(true)
                        setError({message: err.response.data.message, color: "red"})
                        console.error(err)
                    })

            }
        }
    )

    if(loading) {
        return         <>
            <Transition.Root show={open} as={Fragment}>

                <Dialog as="div" className="relative z-10" onClose={setOpen}>
                    {error && (
                        <ErrorNotification isVisible={visible} handleClose={handleClose} message={error.message} color={error.color}/>
                    )}
                    <div className="fixed inset-0" />

                    <div className="fixed inset-0 overflow-hidden">
                        <div className="absolute inset-0 overflow-hidden">
                            <div className="pointer-events-none fixed inset-y-0 right-0 flex max-w-full pl-10 sm:pl-16">
                                <Transition.Child
                                    as={Fragment}
                                    enter="transform transition ease-in-out duration-500 sm:duration-700"
                                    enterFrom="translate-x-full"
                                    enterTo="translate-x-0"
                                    leave="transform transition ease-in-out duration-500 sm:duration-700"
                                    leaveFrom="translate-x-0"
                                    leaveTo="translate-x-full"
                                >
                                    <Dialog.Panel className="pointer-events-auto w-screen max-w-md">
                                        <form className="flex h-full flex-col divide-y divide-gray-200 bg-white shadow-xl">
                                            <div className="h-0 flex-1 overflow-y-auto">
                                                <div className="bg-primary px-4 py-6 sm:px-6">
                                                    <div className="flex items-center justify-between">
                                                        <Dialog.Title className="text-base font-semibold leading-6 text-white">
                                                            Edit user
                                                        </Dialog.Title>
                                                        <div className="ml-3 flex h-7 items-center">
                                                            <button
                                                                type="button"
                                                                className="relative rounded-md bg-accent text-secondary hover:text-white focus:outline-none focus:ring-2 focus:ring-white"
                                                                onClick={() => setOpen(false)}
                                                            >
                                                                <span className="absolute -inset-2.5" />
                                                                <span className="sr-only">Close panel</span>
                                                                <XMarkIcon className="h-6 w-6" aria-hidden="true" />
                                                            </button>
                                                        </div>
                                                    </div>
                                                    <div className="mt-1">
                                                        <p className="text-sm text-secondary">
                                                            Begin by modifying the information below to update a user. In edit mode, you can reset a user's password, change their role, or delete the user.
                                                        </p>
                                                    </div>
                                                </div>
                                                <div className="flex flex-1 flex-col justify-between">
                                                    <div className="divide-y divide-gray-200 px-4 sm:px-6">
                                                        <div>Loading</div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="flex flex-shrink-0 justify-end px-4 py-4">
                                                <button
                                                    type="button"
                                                    className="rounded-md bg-secondaryButton px-3 py-2 text-sm font-semibold text-primaryButton shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-secondary"
                                                    onClick={() => setOpen(false)}
                                                >
                                                    Cancel
                                                </button>
                                                <button
                                                    type="submit"
                                                    className="ml-4 inline-flex justify-center rounded-md bg-primaryButton px-3 py-2 text-sm font-semibold text-secondaryButton shadow-sm hover:bg-primary focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
                                                >
                                                    Update
                                                </button>
                                            </div>
                                        </form>
                                    </Dialog.Panel>
                                </Transition.Child>
                            </div>
                        </div>
                    </div>
                </Dialog>
            </Transition.Root>
        </>
    }

    return (
        <>
            <Transition.Root show={open} as={Fragment}>

                <Dialog as="div" className="relative z-10" onClose={setOpen}>
                    {error && (
                        <ErrorNotification isVisible={visible} handleClose={handleClose} message={error.message} color={error.color}/>
                    )}
                    <div className="fixed inset-0" />

                    <div className="fixed inset-0 overflow-hidden">
                        <div className="absolute inset-0 overflow-hidden">
                            <div className="pointer-events-none fixed inset-y-0 right-0 flex max-w-full pl-10 sm:pl-16">
                                <Transition.Child
                                    as={Fragment}
                                    enter="transform transition ease-in-out duration-500 sm:duration-700"
                                    enterFrom="translate-x-full"
                                    enterTo="translate-x-0"
                                    leave="transform transition ease-in-out duration-500 sm:duration-700"
                                    leaveFrom="translate-x-0"
                                    leaveTo="translate-x-full"
                                >
                                    <Dialog.Panel className="pointer-events-auto w-screen max-w-md">
                                        <form className="flex h-full flex-col divide-y divide-gray-200 bg-white shadow-xl" onSubmit={formik.handleSubmit}>
                                            <div className="h-0 flex-1 overflow-y-auto">
                                                <div className="bg-primary px-4 py-6 sm:px-6">
                                                    <div className="flex items-center justify-between">
                                                        <Dialog.Title className="text-base font-semibold leading-6 text-white">
                                                            Edit user
                                                        </Dialog.Title>
                                                        <div className="ml-3 flex h-7 items-center">
                                                            <button
                                                                type="button"
                                                                className="relative rounded-md bg-accent text-secondary hover:text-white focus:outline-none focus:ring-2 focus:ring-white"
                                                                onClick={() => setOpen(false)}
                                                            >
                                                                <span className="absolute -inset-2.5" />
                                                                <span className="sr-only">Close panel</span>
                                                                <XMarkIcon className="h-6 w-6" aria-hidden="true" />
                                                            </button>
                                                        </div>
                                                    </div>
                                                    <div className="mt-1">
                                                        <p className="text-sm text-secondary">
                                                            Begin by modifying the information below to update a user. In edit mode, you can reset a user's password, change their role, or delete the user.
                                                        </p>
                                                    </div>
                                                </div>
                                                <div className="flex flex-1 flex-col justify-between">
                                                    <div className="divide-y divide-gray-200 px-4 sm:px-6">
                                                        <div className="space-y-6 pb-5 pt-6">
                                                            <div>
                                                                <label
                                                                    htmlFor="firstName"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    User firstname
                                                                </label>
                                                                <div className="mt-2">
                                                                    <p className="block w-full  py-1.5 text-gray-900 shadow-sm sm:text-sm sm:leading-6">{user.firstName}</p>
                                                                </div>

                                                            </div>
                                                            <div>
                                                                <label
                                                                    htmlFor="lastName"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    User lastname
                                                                </label>
                                                                <div className="mt-2">
                                                                    <p className="block w-full  py-1.5 text-gray-900 shadow-sm sm:text-sm sm:leading-6">{user.lastName}</p>
                                                                </div>

                                                            </div>
                                                            <div>
                                                                <label
                                                                    htmlFor="email"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    User email
                                                                </label>
                                                                <div className="mt-2">
                                                                    <p className="block w-full  py-1.5 text-gray-900 shadow-sm sm:text-sm sm:leading-6">{user.email}</p>
                                                                </div>
                                                            </div>
                                                            <div>
                                                                <label htmlFor="role" className="block text-sm font-medium leading-6 text-gray-900">
                                                                    Role
                                                                </label>
                                                                <select
                                                                    id="role"
                                                                    name="role"
                                                                    className="mt-2 block w-full rounded-md border-0 py-1.5 pl-3 pr-10 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-indigo-600 sm:text-sm sm:leading-6"
                                                                    defaultValue={user.role}
                                                                    onChange={formik.handleChange}
                                                                    //value={formik.values.role}
                                                                >
                                                                    <option value="ADMIN">Admin</option>
                                                                    <option value="USER">User</option>
                                                                    <option value="PROJECT_MANAGER">Project manager</option>
                                                                </select>
                                                            </div>
                                                            <div>
                                                                <label htmlFor="passwordReset" className="block text-sm font-medium leading-6 text-gray-900">
                                                                    Reset Password
                                                                </label>
                                                                <select
                                                                    onChange={formik.handleChange}
                                                                    value={formik.values.passwordReset}
                                                                    id="passwordReset"
                                                                    name="passwordReset"
                                                                    className="mt-2 block w-full rounded-md border-0 py-1.5 pl-3 pr-10 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-indigo-600 sm:text-sm sm:leading-6"
                                                                    //defaultValue="No"
                                                                >
                                                                    <option value={true}>Yes</option>
                                                                    <option value={false}>No</option>
                                                                </select>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="flex flex-shrink-0 justify-end px-4 py-4">
                                                <button
                                                    type="button"
                                                    className="mx-4 rounded-md bg-error px-3 py-2 text-sm font-semibold text-secondaryButton shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-secondary"
                                                    onClick={userDelete}>
                                                    Delete
                                                </button>
                                                <button
                                                    type="button"
                                                    className="rounded-md bg-secondaryButton px-3 py-2 text-sm font-semibold text-primaryButton shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-secondary"
                                                    onClick={() => setOpen(false)}
                                                >
                                                    Cancel
                                                </button>
                                                <button
                                                    type="submit"
                                                    className="ml-4 inline-flex justify-center rounded-md bg-primaryButton px-3 py-2 text-sm font-semibold text-secondaryButton shadow-sm hover:bg-primary focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
                                                >
                                                    Update
                                                </button>
                                            </div>
                                        </form>
                                    </Dialog.Panel>
                                </Transition.Child>
                            </div>
                        </div>
                    </div>
                </Dialog>
            </Transition.Root>
        </>

    )
}
