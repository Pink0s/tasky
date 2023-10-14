import { useEffect, useState } from "react";
import SideBar from "../DashBoard/SideBar"
import { useAuth } from "../../contexts/AuthContext";
import { deleteFeature, fetchTasks, getMyProfile } from "../../utils/client";
import { fetchFeatureById } from "../../utils/client";
import { useNavigate, useParams } from "react-router-dom";
import UpdateFeatureForm from "./UpdateFeatureForm";
import AddTaskForm from "./AddTaskForm";
import SuccessFullyCreated from "../DashBoard/AdminDashboard/SuccessFullyCreated";
const Feature = () => {
    const {user, authLoading,isUserAuthenticated, parseJwt, setAuthLoading, setUser} = useAuth();
    const params = useParams();
    const navigate = useNavigate()
    const [feature, setFeature] = useState(null)
    const [alertContent,setAlertContent] = useState({})
    const [loading, setIsLoading] = useState(true)
    const [showUpdateFeatureForm, setShowUpdateFeatureForm] = useState(false)
    const [showAddTaskForm, setShowAddTaskForm] = useState(false)
    const [showAlertCompletion, setShowAlertCompletion] = useState(false)
    const [tasks, setTasks] = useState([])
    const [pageable, setPageable] = useState({})
    const [page, setPage] = useState(0)
    
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
        if(page >= 0) {
            let id = params.id
            fetchTasks(Number(id),{page:page})
                .then((res) => {
                    setTasks(res.data.toDos)
                    setPageable(res.data.pageable)   
                })
        }
    },[page])

    useEffect(() => {
        const id = params.id;
        if(!authLoading) {
            fetchFeatureById(id).then((res) => {
                setFeature(res.data)
                setIsLoading(false)
            })

            fetchTasks(Number(id)).then((res) => {
                setTasks(res.data.toDos)
                setPageable(res.data.pageable)
            })
        }
    },[authLoading,alertContent])

    if(authLoading) {
        return <div>Loading ...</div>
    }

    if(loading) {
        return <div>Loading ...</div>
    }

    

    return <SideBar pageName={`Feature : ${feature.name}`}>
        {showUpdateFeatureForm && <UpdateFeatureForm setOpen={setShowUpdateFeatureForm} open={showUpdateFeatureForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
        {showAddTaskForm && <AddTaskForm setOpen={setShowAddTaskForm} open={showAddTaskForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
        {showAlertCompletion && <SuccessFullyCreated alertContent={alertContent.content} open={showAlertCompletion} alertTitle={alertContent.title} setOpen={setShowAlertCompletion} setAlertContent={setAlertContent}/>}   
        <div className="px-4 sm:px-0">
            <div>
                
                <p className="text-blue-500">
                    <a href={`/project/${params.projectId}`} className="text-blue-500 hover:underline">Project</a> / <a href={`/project/${params.projectId}/run/${params.runId}`} className="text-blue-500 hover:underline">Run</a>
                </p>

                {user.role === "PROJECT_MANAGER" && <>
                    <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Action Panel</h3>
                            
                    <div className=" flex flex-row gap-2 mt-4 sm:ml-2 sm:mt-0 sm:flex-none">
                        <button
                            type="button"
                            className="block rounded-md bg-red-800 px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-error focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                deleteFeature(params.id)
                                    .then((res) => {
                                        navigate(`/project/${params.projectId}/run/${params.runId}`)
                                    }).catch()
                            }}
                        >
                            Delete Feature
                        </button>
                        <button
                            type="button"
                            className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                setShowUpdateFeatureForm(!showUpdateFeatureForm)
                            }}
                        >
                            Update Feature
                        </button>
                        <button
                            type="button"
                            className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                setShowAddTaskForm(!showAddTaskForm)
                            }}
                        >
                            Add a task
                        </button>
                    </div>
                </>}
                
            </div>
        </div>
        <div className="px-4 sm:px-0">
            <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Feature description</h3>
                <p className="mt-1 max-w-2xl text-sm leading-6 text-gray-500">{feature.description}</p>
        </div>
        <div className="mt-6">
            <dl className="grid grid-cols-1 sm:grid-cols-2">
                <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                    <dt className="text-sm font-medium leading-6 text-gray-900">Feature id</dt>
                    <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{feature.id}</dd>
                </div>
                <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                    <dt className="text-sm font-medium leading-6 text-gray-900">Feature status</dt>
                    <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{feature.status}</dd>
                </div>
            </dl>
        </div>
        {pageable.numberOfPage > 0 && <>
            <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Tasks</h3>

            <div role="list" className="bg-white border-2 rounded-md px-4">
                    <ul className="grid grid-cols-1 gap-4 justify-center pt-3 pb-3 md:grid-cols-2">
                        {tasks.map((task, index) => (
                            <li key={task.toDoId} className={`bg-gray-300 rounded-lg shadow-md p-4 ${index < 3 ? 'pt-4' : ''}`} onClick={() => {
                                navigate(`/project/${params.projectId}/run/${params.runId}/feature/${params.id}/task/${task.toDoId}`)}}>
                                <h3 className="text-lg font-semibold text-primary mb-2">
                                    {task.title}
                                </h3>
                                <p className="text-gray-600">
                                    <span className="text-secondary font-semibold">Description:</span> {task.description}
                                </p>
                                <p className="text-gray-600">
                                    <span className="text-secondary font-semibold">Type:</span> {task.type}
                                </p>
                                <p className="text-gray-600">
                                    <span className="text-secondary font-semibold">Status:</span> {task.status}
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

export default Feature