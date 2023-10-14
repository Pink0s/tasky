import { useEffect } from "react";
import SideBar from "../DashBoard/SideBar"
import { useAuth } from "../../contexts/AuthContext";
import { deleteRun, fetchFeatures, getMyProfile } from "../../utils/client";
import { useParams } from "react-router-dom";
import { fetchRunById } from "../../utils/client";
import { useState } from "react";
import AddFeatureForm from "./AddFeatureForm";
import SuccessFullyCreated from "../DashBoard/AdminDashboard/SuccessFullyCreated";
import {useNavigate} from "react-router-dom";
import UpdateRunForm from "./UpdateRunForm";

const Run = () => {
    const {user, authLoading,isUserAuthenticated, parseJwt, setAuthLoading, setUser} = useAuth()
    const [alertContent,setAlertContent] = useState({})
    const params = useParams()
    const [run, setRun] = useState(null)
    const [loading, setIsLoading] = useState(true)
    const [showAlertCompletion, setShowAlertCompletion] = useState(false)
    const [showAddFeatureForm, setShowAddFeatureForm] = useState(false)
    const [showUpdateRunForm, setShowUpdateRunForm] = useState(false)
    const [pageable, setPageable] = useState({})
    const [features, setFeatures] = useState([])
    const [page, setPage] = useState(0)
    const navigate = useNavigate()

    useEffect(() => {
        if(page >= 0) {
            let id = params.runId
            fetchFeatures(id,{page:page})
                .then((res) => {
                    setFeatures(res.data.features)
                    setPageable(res.data.pageable)   
                })
                .catch()
        }
    },[page])

    useEffect(() => {

        if(isUserAuthenticated()) {
            const payload = parseJwt(localStorage.getItem("access_token"));
            const date = Math.floor(Date.now() / 1000)

            if(date < payload.exp) {
                getMyProfile()
                    .then( (res) => {
                        setUser(res.data)
                    })
                    .catch((err) => {
                        localStorage.removeItem("access_token")
                    }).finally(() => {
                    setAuthLoading(false);
                })
            } else {
                localStorage.removeItem("access_token")
                setAuthLoading(false);
            }
        }

    },[])

    useEffect(() => {
        const id = params.runId;
        setIsLoading(true)
        if(!authLoading) {
            fetchRunById(id).then((res) => {
                setRun(res.data)
                setIsLoading(false)
            })

            fetchFeatures(id).then((res) => {
                setFeatures(res.data.features)
                setPageable(res.data.pageable)
            })
        }
    },[authLoading, alertContent])

    if(authLoading) {
        return <div>Loading ...</div>
    }

    if(loading) {
        return <div>Loading ...</div>
    }

    function formatDate(date) {
        const parts = date.split("T");
        return parts[0]
    }

    return <SideBar pageName={`Run : ${run.name} `}>
        {showAlertCompletion && <SuccessFullyCreated alertContent={alertContent.content} open={showAlertCompletion} alertTitle={alertContent.title} setOpen={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
        {showAddFeatureForm && <AddFeatureForm setOpen={setShowAddFeatureForm} open={showAddFeatureForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
        {showUpdateRunForm && <UpdateRunForm setOpen={setShowUpdateRunForm} open={showUpdateRunForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
        <div className="px-4 sm:px-0">
            <a href={`/project/${params.projectId}`} className="text-blue-500 hover:underline">Project</a>
                    {user.role === "PROJECT_MANAGER" && <>

                        <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Action Panel</h3>
                        
                        <div className=" flex flex-row gap-2 mt-4 sm:ml-2 sm:mt-0 sm:flex-none">
                        <button
                            type="button"
                            className="block rounded-md bg-red-800 px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-error focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                deleteRun(params.runId)
                                    .then((res) => {
                                        navigate(`/project/${params.projectId}`)
                                    }).catch()
                            }}
                        >
                            Delete run
                        </button>
                        <button
                            type="button"
                            className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                setShowUpdateRunForm(!showUpdateRunForm)
                            }}
                        >
                            Update run
                        </button>
                        
                        <button
                            type="button"
                            className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                setShowAddFeatureForm(!showAddFeatureForm)
                            }}
                        >
                            Add Feature
                        </button>
                    </div>
                    </>
                    }
        </div>
        <div className="px-4 sm:px-0">
            <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Run description</h3>
                <p className="mt-1 max-w-2xl text-sm leading-6 text-gray-500">{run.description}</p>
        </div>
        <div className="mt-6">
            <dl className="grid grid-cols-1 sm:grid-cols-2">
                <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                    <dt className="text-sm font-medium leading-6 text-gray-900">Run id</dt>
                    <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{run.runId}</dd>
                </div>
                <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                    <dt className="text-sm font-medium leading-6 text-gray-900">Run start date</dt>
                    <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{formatDate(run.startDate)}</dd>
                </div>
                <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                    <dt className="text-sm font-medium leading-6 text-gray-900">Run end date</dt>
                    <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{formatDate(run.endDate)}</dd>
                </div>
                <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                    <dt className="text-sm font-medium leading-6 text-gray-900">Run status</dt>
                    <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{run.status}</dd>
                </div>
            </dl>
        </div>
        {pageable.numberOfPage > 0 && <>
            <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Features</h3>

            <div role="list" className="bg-white border-2 rounded-md px-4">
                    <ul className="grid grid-cols-1 gap-4 justify-center pt-3 pb-3 md:grid-cols-2">
                        {features.map((feature, index) => (
                            <li key={feature.id} className={`bg-gray-300 rounded-lg shadow-md p-4 ${index < 3 ? 'pt-4' : ''}`} onClick={() => {
                                navigate(`/project/${params.projectId}/run/${params.runId}/feature/${feature.id}`)}}>
                                <h3 className="text-lg font-semibold text-primary mb-2">
                                    {feature.name}
                                </h3>
                                <p className="text-gray-600">
                                    <span className="text-secondary font-semibold">Description:</span> {feature.description}
                                </p>
                                <p className="text-gray-600">
                                    <span className="text-secondary font-semibold">Status:</span> {feature.status}
                                </p>
                            </li>
                        ))}
                    </ul>
            </div>
            <div className="flex justify-between items-center p-4 bg-gray-100 rounded-md">
                    <button onClick={() => { setPage(page-1) } }disabled={pageable.currentPage === 0} className="px-4 py-2 text-gray-700 rounded-lg bg-gray-300 hover:bg-gray-400">
                        Previous
                    </button>
                    <span className="text-gray-600 text-sm font-semibold">Page {pageable.currentPage+1} of {pageable.numberOfPage}</span>
                    <button  onClick={() => {setPage(page+1)}}disabled={(pageable.currentPage+1) === pageable.numberOfPage} className="px-4 py-2 text-gray-700 rounded-lg bg-gray-300 hover:bg-gray-400">
                        Next
                    </button>
            </div>
        </>}
    </SideBar>
}

export default Run