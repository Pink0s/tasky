import { Fragment, useState } from 'react'
import { Dialog, Transition } from '@headlessui/react'
import { XMarkIcon } from '@heroicons/react/24/outline'
import {useFormik} from "formik";
import * as Yup from 'yup';
import {createUser} from "../../../utils/client.js";
import ErrorNotification from "../../../components/ErrorNotification.jsx";

export default function CreateUserForm({setOpen,open,setShowAlertCompletion, setAlertContent}) {
    const [error, setError] = useState(null);
    const [visible, setIsVisible] = useState(null);
    const handleClose = () => {
        setError(false)
        setIsVisible(false)
    }

    const formik = useFormik({
        initialValues: {
            firstName: '',
            lastName: '',
            email: ''
        },
        validationSchema: Yup.object({
            firstName: Yup.string()
                .max(20, 'Must be 20 characters or less')
                .required('Required'),
            lastName: Yup.string()
                .max(20, 'Must be 20 characters or less')
                .required('Required'),
            email: Yup.string()
                .email('Invalid email address')
                .required('Required'),
        }),
        onSubmit: values => {
                const {firstName, lastName, email} = values;

                createUser({firstName, lastName, email})
                    .then((res) => {
                        setOpen(false)
                        setShowAlertCompletion(true)

                        const data = {
                            content: `Here is the password for the created user.
                                Kindly transfer it to the concerned user.
                                Please note that once notification is closed, it will not be retrievable. 
                                password : ${res.data.password}
                              `,
                            title: "User Successfully created"
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
            },
    });

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
                                                            New User
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
                                                            Get started by filling in the information below to create your new user.
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
                                                                    <input
                                                                        type="text"
                                                                        name="firstName"
                                                                        id="firstName"
                                                                        onChange={formik.handleChange}
                                                                        value={formik.values.firstName}
                                                                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primary sm:text-sm sm:leading-6"
                                                                    />
                                                                </div>
                                                                {formik.touched.firstName && formik.errors.firstName ? (
                                                                    <div className="text-error">{formik.errors.firstName}</div>
                                                                ) : null}
                                                            </div>
                                                            <div>
                                                                <label
                                                                    htmlFor="lastName"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    User lastname
                                                                </label>
                                                                <div className="mt-2">
                                                                    <input
                                                                        type="text"
                                                                        name="lastName"
                                                                        id="lastName"
                                                                        onChange={formik.handleChange}
                                                                        value={formik.values.lastName}
                                                                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primary sm:text-sm sm:leading-6"
                                                                    />
                                                                </div>
                                                                {formik.touched.lastName && formik.errors.lastName ? (
                                                                    <div className="text-error">{formik.errors.lastName}</div>
                                                                ) : null}
                                                            </div>
                                                            <div>
                                                                <label
                                                                    htmlFor="email"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    User email
                                                                </label>
                                                                <div className="mt-2">
                                                                    <input
                                                                        type="email"
                                                                        name="email"
                                                                        id="email"
                                                                        onChange={formik.handleChange}
                                                                        value={formik.values.email}
                                                                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primary sm:text-sm sm:leading-6"
                                                                    />

                                                                </div>
                                                                {formik.touched.email && formik.errors.email ? (
                                                                    <div className="text-error">{formik.errors.email}</div>
                                                                ) : null}
                                                            </div>
                                                        </div>
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
                                                    Create
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
