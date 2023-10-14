import {useParams} from "react-router-dom";
import SideBar from "../SideBar.jsx";
import {useAuth} from "../../../contexts/AuthContext.jsx";
import {useEffect, useState} from "react";
import {fetchProjectById, fetchRuns, getMyProfile} from "../../../utils/client.js";
import {deleteProject} from "../../../utils/client.js";
import {useNavigate} from "react-router-dom";
import UpdateProjectFrom from "./UpdateProjectForm.jsx";
import SuccessFullyCreated from "../AdminDashboard/SuccessFullyCreated.jsx";
import AddUserForm from "./AddUserForm.jsx";
import AddRunForm from "./AddRunForm.jsx";



const ProjectView = () => {
    const {user, authLoading,isUserAuthenticated, parseJwt, setAuthLoading, setUser} = useAuth();
    const [project, setProject] = useState(null)
    const [projectLoading, setProjectLoading] = useState(true)
    const navigate = useNavigate()
    const [showAlertCompletion, setShowAlertCompletion] = useState(false)
    const [alertContent,setAlertContent] = useState({})
    const [showUpdateForm, setShowUpdateForm] = useState(false)
    const [showAddUserForm, setShowAddUserForm] = useState(false)
    const [showAddRunForm, setShowAddRunForm] = useState(false)
    const [runs, setRuns] = useState([])
    const [pagination, setPagination] = useState({})
    const [page, setPage] = useState(0)
    const params = useParams() 

    useEffect(() => {
        if(page >= 0) {
            let id = params.id
            fetchRuns(id,{page:page})
                .then((res) => {
                       
                       setRuns(res.data.runs)
                       setPagination(res.data.pageable)
                       
                    }
                )
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
        if(!authLoading) {
            let id = params.id
            fetchProjectById({id})
                .then((res) => {
                       setProject(res.data)
                       setProjectLoading(false)
                    }
                )
                .catch()
            
            fetchRuns(id)
                .then((res) => {
                       
                       setRuns(res.data.runs)
                       setPagination(res.data.pageable)
                       
                    }
                )
                .catch()
            
        }
    },[authLoading, alertContent])

    

    function formatDate(date) {
        const parts = date.split("T");
        return parts[0]
    }

    if(authLoading) {
        return <div>Loading ...</div>
    }

    if(projectLoading) {
        return <SideBar pageName="Loading">
            <div>Loading ...</div>
        </SideBar>
    }

    return (
        <SideBar pageName={"Project : "+project.name}>
            {showUpdateForm && <UpdateProjectFrom setOpen={setShowUpdateForm} open={showUpdateForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
            {showAddUserForm && <AddUserForm setOpen={setShowAddUserForm} open={showAddUserForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
            {showAlertCompletion && <SuccessFullyCreated alertContent={alertContent.content} open={showAlertCompletion} alertTitle={alertContent.title} setOpen={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
            {showAddRunForm && <AddRunForm setOpen={setShowAddRunForm} open={showAddRunForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}

            <div>
                <div className="px-4 sm:px-0">

                    {user.role === "PROJECT_MANAGER" && <>

                        <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Action Panel</h3>
                        <div className=" flex flex-row gap-2 mt-4 sm:ml-2 sm:mt-0 sm:flex-none">
                        <button
                            type="button"
                            className="block rounded-md bg-red-800 px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-error focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                deleteProject(params.id)
                                    .then((res) => {
                                        navigate("/dashboard")
                                    }).catch()
                            }}
                        >
                            Delete project
                        </button>

                        <button
                            type="button"
                            className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                setShowUpdateForm(!showUpdateForm)
                            }}
                        >
                            Edit project
                        </button>

                        <button
                            type="button"
                            className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                setShowAddUserForm(!showAddUserForm)
                            }}
                        >
                            Add User
                        </button>
                        <button
                            type="button"
                            className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                setShowAddRunForm(!showAddRunForm)
                            }}
                        >
                            Add Run
                        </button>
                    </div>
                    </>
                    }
                </div>
                <div className="px-4 sm:px-0">
                    <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Project description</h3>
                    <p className="mt-1 max-w-2xl text-sm leading-6 text-gray-500">{project.description}</p>
                </div>
                <div className="mt-6">
                    <dl className="grid grid-cols-1 sm:grid-cols-2">
                        <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                            <dt className="text-sm font-medium leading-6 text-gray-900">Project id</dt>
                            <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{project.projectID}</dd>
                        </div>
                        <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                            <dt className="text-sm font-medium leading-6 text-gray-900">Due date</dt>
                            <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{formatDate(project.dueDate)}</dd>
                        </div>
                        <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                            <dt className="text-sm font-medium leading-6 text-gray-900">Project Manager</dt>
                            <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{project.creator}</dd>
                        </div>
                        <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                            <dt className="text-sm font-medium leading-6 text-gray-900">Number of users</dt>
                            <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{project.users.length}</dd>
                        </div>

                    </dl>
                </div>

            </div>

            {project.users.length > 0 && <>
                <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Users</h3>
                <ul role="list" className="flex flex-row flex-shrink bg-white border-2 rounded-md px-4">
                    {project.users.map((item) => (
                        <li key={item.id} className="py-4 bg-secondary rounded-md m-4 p-4">
                            <div className="flex items-center gap-x-3">
                                <h3 className="flex-auto truncate text-sm font-semibold leading-6 text-gray-900">{item.firstName + " "+ item.firstName}</h3>
                            </div>
                            <p className="mt-3 truncate text-sm text-gray-500">
                                <span className="text-gray-700">{item.email}</span>
                            </p>
                        </li>
                    ))}
                </ul>
            </>}
            {pagination.numberOfPage > 0 && <>
            
                <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Runs</h3>
                           
                           <div role="list" className="bg-white border-2 rounded-md px-4">
                               <ul className="grid grid-cols-1 gap-4 justify-center pt-3 pb-3 md:grid-cols-2">
                                   {runs.map((run, index) => (
                                       <li key={run.runId} className={`bg-gray-300 rounded-lg shadow-md p-4 ${index < 3 ? 'pt-4' : ''}`} onClick={() => {
                                           navigate(`/project/${params.id}/run/${run.runId}`)}}>
                                           <h3 className="text-lg font-semibold text-primary mb-2">
                                               {run.name}
                                           </h3>
                                           <p className="text-gray-600">
                                               <span className="text-secondary font-semibold">Description:</span> {run.description}
                                           </p>
                                           <p className="text-gray-600">
                                               <span className="text-secondary font-semibold">Status:</span> {run.status}
                                           </p>
                                       </li>
                                   ))}
                               </ul>
                           </div>
               
                           <div className="flex justify-between items-center p-4 bg-gray-100 rounded-md">
                               <button onClick={() => { setPage(page-1) } }disabled={pagination.currentPage === 0} className="px-4 py-2 text-gray-700 rounded-lg bg-gray-300 hover:bg-gray-400">
                                   Previous
                               </button>
                               <span className="text-gray-600 text-sm font-semibold">Page {pagination.currentPage+1} of {pagination.numberOfPage}</span>
                               <button  onClick={() => {setPage(page+1)}}disabled={(pagination.currentPage+1) === pagination.numberOfPage} className="px-4 py-2 text-gray-700 rounded-lg bg-gray-300 hover:bg-gray-400">
                                   Next
                               </button>
                           </div>
            
            </>}        
            

            
        </SideBar>
    )
}

export default ProjectView