import {Fragment, useEffect, useState} from "react";
import {useFormik} from "formik";
import * as Yup from 'yup';
import { Dialog, Transition } from "@headlessui/react";
import ErrorNotification from "../../components/ErrorNotification.jsx";
import { XMarkIcon } from "@heroicons/react/24/outline/index.js";
import {useParams} from "react-router-dom";
import { fetchTaskById , updateTask} from "../../utils/client.js";

const UpdateTaskForm = ({setOpen,open,setShowAlertCompletion, setAlertContent}) => {
    const [error, setError] = useState(null);
    const [visible, setIsVisible] = useState(null);
    const [task, setTask] = useState({})
    const [loading, setIsLoading] = useState(true)
    const params = useParams()

    const handleClose = () => {
        setError(false)
        setIsVisible(false)
    }

    function formatDate(date) {
        if(date === undefined) return null
        let parts = date.split("T");
        return parts[0]
    }

    useEffect(() => {

        const id = Number(params.id)
        
        fetchTaskById(id).then(
            (res) => {
                setTask(res.data)
                setIsLoading(false)
            }
        ).catch((err) => {
            setIsVisible(true)
            setError({message: err.response.data.message, color: "red"})
            setIsLoading(false)
        })
    },[])

    const formik = useFormik({
        initialValues: {
            title: task.title,
            description: task.description,
            status: task.status,
            type: task.type
        },
        enableReinitialize: true,
        validationSchema: Yup.object({
            title : Yup.string()
                .max(20, 'Must be 20 characters or less')
                .required('Required'),
            description: Yup.string()
                .min(5, 'Must be 5 characters or more')
                .required('Required'),
            status: Yup.string()
                .required('Required'),
            type: Yup.string()
                .required('Required'),
        }),

        onSubmit: values => {
            let {title, description, status, type} = values;
            const id = params.id

            updateTask(Number(id),{title, description, status, type})
                 .then((res) => {
                    setOpen(false)
                    setShowAlertCompletion(true)

                    const data = {
                        content: `Task successfully updated.`,
                        title: "Task successfully updated."
                    }

                    setAlertContent((alertContent) => ({
                        ...alertContent,
                        ...data
                    }))
                })
                .catch((err) => {
                    setIsVisible(true)
                    setError({message: "err.response.data.message", color: "red"})
                    console.error(err)
                })
        },
    });

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
                                                            Edit task
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
                                                        Begin by modifying the information below to update the task. In edit mode, you can modify the task name, the status, the type and the description.
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
                                                            Edit task
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
                                                            Begin by modifying the information below to update the task. In edit mode, you can modify the task name, the status, the type and the description.                                                        
                                                        </p>
                                                    </div>
                                                </div>
                                                <div className="flex flex-1 flex-col justify-between">
                                                    <div className="divide-y divide-gray-200 px-4 sm:px-6">
                                                        <div className="space-y-6 pb-5 pt-6">
                                                        <div>
                                                                <label
                                                                    htmlFor="name"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    Task title
                                                                </label>
                                                                <div className="mt-2">
                                                                    <input
                                                                        type="text"
                                                                        name="title"
                                                                        id="title"
                                                                        onChange={formik.handleChange}
                                                                        defaultValue={formik.values.title}
                                                                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primary sm:text-sm sm:leading-6"
                                                                    />
                                                                </div>
                                                                {formik.touched.title && formik.errors.title ? (
                                                                    <div className="text-error">{formik.errors.title}</div>
                                                                ) : null}
                                                            </div>
                                                            <div>
                                                                <label
                                                                    htmlFor="description"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    Task description
                                                                </label>
                                                                <div className="mt-2">
                                                                    <input
                                                                        type="text"
                                                                        name="description"
                                                                        id="description"
                                                                        onChange={formik.handleChange}
                                                                        defaultValue={formik.values.description}
                                                                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primary sm:text-sm sm:leading-6"
                                                                    />
                                                                </div>
                                                                {formik.touched.description && formik.errors.description ? (
                                                                    <div className="text-error">{formik.errors.description}</div>
                                                                ) : null}
                                                            </div>
                                                            <div>
                                                                <label
                                                                    htmlFor="status"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    Task status
                                                                </label>
                                                                <div className="mt-2">
                                                                    <select
                                                                        id="status"
                                                                        name="status"
                                                                        className="mt-2 block w-full rounded-md border-0 py-1.5 pl-3 pr-10 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-indigo-600 sm:text-sm sm:leading-6"
                                                                        defaultValue={task.status}
                                                                        onChange={formik.handleChange}
                                                                    >
                                                                        <option value="New">New</option>
                                                                        <option value="In progress">In progress</option>
                                                                        <option value="Completed">Completed</option>
                                                                    </select>
                                                                </div>
                                                            </div>
                                                            <div>
                                                                <label
                                                                    htmlFor="type"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    Task type
                                                                </label>
                                                                <div className="mt-2">
                                                                    <select
                                                                        id="type"
                                                                        name="type"
                                                                        className="mt-2 block w-full rounded-md border-0 py-1.5 pl-3 pr-10 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-indigo-600 sm:text-sm sm:leading-6"
                                                                        defaultValue={task.type}
                                                                        onChange={formik.handleChange}
                                                                    >
                                                                        <option value="task">Task</option>
                                                                        <option value="bug">Bug</option>
                                                                    </select>
                                                                </div>
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
export default UpdateTaskForm