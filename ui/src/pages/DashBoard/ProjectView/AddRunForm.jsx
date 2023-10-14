import {Fragment, useState} from "react";
import {useParams} from "react-router-dom";
import {useFormik} from "formik";
import * as Yup from "yup";
import {addRun} from "../../../utils/client.js";
import {Dialog, Transition} from "@headlessui/react";
import ErrorNotification from "../../../components/ErrorNotification.jsx";
import {XMarkIcon} from "@heroicons/react/24/outline/index.js";

const AddRunForm = ({setOpen,open,setShowAlertCompletion, setAlertContent}) => {
    const [error, setError] = useState(null);
    const [visible, setIsVisible] = useState(null);
    const params = useParams()

    const handleClose = () => {
        setError(false)
        setIsVisible(false)
    }


    const formik = useFormik(
        {
            initialValues: {
                name: "",
                description: "",
                startDate: "",
                endDate: ""
            },
            enableReinitialize: true,
            validationSchema: Yup.object({
                name: Yup.string()
                    .min(3, "Name must have min 3 characters")
                    .max(20, "Name must have max 20 characters")
                    .required(),
                description: Yup.string()
                    .min(3,"Description must have min 3 characters")
                    .required(),
                startDate: Yup.date().required(),
                endDate: Yup.date().required()
            }),
            onSubmit: values => {
                const { name, description, startDate, endDate } = values
                addRun(params.id,{ name, description, startDate, endDate })
                    .then((res) => {
                        setOpen(false)
                        setShowAlertCompletion(true)
                
                        let data = {
                            content: `Run successfully added`,
                            title: "Run successfully added"
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
                                                            Add a run in the project
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
                                                            Add run to this project.
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
                                                                    Run name
                                                                </label>
                                                                {/*String name, String description, Long startDate, Long endDate*/}
                                                                <div className="mt-2">
                                                                    <input
                                                                        type="text"
                                                                        name="name"
                                                                        id="name"
                                                                        onChange={formik.handleChange}
                                                                        defaultValue={formik.values.name}
                                                                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primary sm:text-sm sm:leading-6"
                                                                    />
                                                                </div>
                                                                {formik.touched.name && formik.errors.name ? (
                                                                    <div className="text-error">{formik.errors.name}</div>
                                                                ) : null}
                                                            </div>
                                                            <div>
                                                                <label
                                                                    htmlFor="description"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    Run description
                                                                </label>
                                                                {/*String name, String description, Long startDate, Long endDate*/}
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
                                                                    htmlFor="startDate"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    Start date
                                                                </label>
                                                                {/*String name, String description, Long startDate, Long endDate*/}
                                                                <div className="mt-2">
                                                                    <input
                                                                        type="date"
                                                                        name="startDate"
                                                                        id="startDate"
                                                                        onChange={formik.handleChange}
                                                                        defaultValue={formik.values.startDate}
                                                                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primary sm:text-sm sm:leading-6"
                                                                    />
                                                                </div>
                                                                {formik.touched.startDate && formik.errors.startDate ? (
                                                                    <div className="text-error">{formik.errors.startDate}</div>
                                                                ) : null}
                                                            </div>
                                                            <div>
                                                                <label
                                                                    htmlFor="endDate"
                                                                    className="block text-sm font-medium leading-6 text-gray-900"
                                                                >
                                                                    End date
                                                                </label>
                                                                {/*String name, String description, Long startDate, Long endDate*/}
                                                                <div className="mt-2">
                                                                    <input
                                                                        type="date"
                                                                        name="endDate"
                                                                        id="endDate"
                                                                        onChange={formik.handleChange}
                                                                        defaultValue={formik.values.endDate}
                                                                        className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-primary sm:text-sm sm:leading-6"
                                                                    />
                                                                </div>
                                                                {formik.touched.endDate && formik.errors.endDate ? (
                                                                    <div className="text-error">{formik.errors.endDate}</div>
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
                                                    Add
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
export default AddRunForm