import { useEffect, useState } from "react";
import SideBar from "../DashBoard/SideBar"
import { useAuth } from "../../contexts/AuthContext";
import { deleteTask, fetchComments, getMyProfile } from "../../utils/client";
import { useNavigate, useParams } from "react-router-dom";
import { fetchTaskById } from "../../utils/client";
import SuccessFullyCreated from "../DashBoard/AdminDashboard/SuccessFullyCreated";
import UpdateTaskForm from "./UpdateTaskForm";
import AddCommentForm from "./AddCommentForm";

const Task = () => {
    const {user, authLoading,isUserAuthenticated, parseJwt, setAuthLoading, setUser} = useAuth();
    const [task, setTask] = useState(null)
    const params = useParams()
    const navigate = useNavigate()
    const [alertContent,setAlertContent] = useState({})
    const [loading, SetIsLoading] = useState(true)
    const [showUpdateTaskForm, setShowUpdateTaskForm] = useState(false)
    const [showAlertCompletion, setShowAlertCompletion] = useState(false)
    const [showAddCommentForm, setShowAddCommentForm] = useState(false)
    const [comments, setComments] = useState([])
    const [pageable, setPageable] = useState({})
    const [page,setPage] = useState(0)

    useEffect(() => {
        if(page >= 0) {
            let id = params.id
            fetchComments(Number(id),{page:page})
                .then((res) => {
                    setComments(res.data.comments)
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
        const id = params.id;
        if(!authLoading) {
            fetchTaskById(Number(id)).then((res) => {
                setTask(res.data)
                SetIsLoading(false)
            })

            fetchComments(Number(id)).then((res) => {
                setComments(res.data.comments)
                setPageable(res.data.pageable)
            }
                
            )
        }
    },[authLoading, alertContent])



    if(authLoading) {
        return <div>Loading ...</div>
    }

    if(loading) {
        return <div>Loading ...</div>
    }


    return (
        <SideBar pageName={`Task : ${task.title}`}>
            {showAlertCompletion && <SuccessFullyCreated alertContent={alertContent.content} open={showAlertCompletion} alertTitle={alertContent.title} setOpen={setShowAlertCompletion} setAlertContent={setAlertContent}/>}   
            {showUpdateTaskForm && <UpdateTaskForm setOpen={setShowUpdateTaskForm} open={showUpdateTaskForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
            {showAddCommentForm && <AddCommentForm setOpen={setShowAddCommentForm} open={showAddCommentForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}

            <div className="px-4 sm:px-0">
                <div>
                    
                    <p className="text-blue-500">
                        <a href={`/project/${params.projectId}`} className="text-blue-500 hover:underline">Project</a>
                         / 
                         <a href={`/project/${params.projectId}/run/${params.runId}`} className="text-blue-500 hover:underline">Run</a>
                         /
                         <a href={`/project/${params.projectId}/run/${params.runId}/feature/${params.featureId}`} className="text-blue-500 hover:underline">Feature</a>
                    </p>

                    {user.role === "PROJECT_MANAGER" && <>
                        <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Action Panel</h3>
                                
                        <div className=" flex flex-row gap-2 mt-4 sm:ml-2 sm:mt-0 sm:flex-none">
                            <button
                                type="button"
                                className="block rounded-md bg-red-800 px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-error focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                                onClick={ () => {
                                    deleteTask(params.id)
                                        .then((res) => {
                                            navigate(`/project/${params.projectId}/run/${params.runId}/feature/${params.featureId}`)
                                        })
                                }}
                            >
                                Delete Task
                            </button>
                            <button
                                type="button"
                                className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                                onClick={ () => {
                                    setShowUpdateTaskForm(!showUpdateTaskForm)
                                }}
                            >
                                Update Task
                            </button>
                            
                            
                            <button
                                type="button"
                                className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                                onClick={ () => {
                                    setShowAddCommentForm(!showAddCommentForm)
                                }}
                            >
                                Add a comment
                            </button>
                        </div>
                    </>}
                    
                </div>
            </div>
            <div className="mt-6">
                <dl className="grid grid-cols-1 sm:grid-cols-2">
                    <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                        <dt className="text-sm font-medium leading-6 text-gray-900">Task description</dt>
                        <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{task.description}</dd>
                    </div>
                    <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                        <dt className="text-sm font-medium leading-6 text-gray-900">Task status</dt>
                        <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{task.status}</dd>
                    </div>
                    <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                        <dt className="text-sm font-medium leading-6 text-gray-900">Task id</dt>
                        <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{task.toDoId}</dd>
                    </div>
                    <div className="border-t border-gray-100 px-4 py-6 sm:col-span-1 sm:px-0">
                        <dt className="text-sm font-medium leading-6 text-gray-900">Task type</dt>
                        <dd className="mt-1 text-sm leading-6 text-gray-700 sm:mt-2">{task.type}</dd>
                    </div>
                    
                </dl>
            </div>
                {pageable.numberOfPage > 0 && <>
                <h3 className="pt-4 text-base font-semibold leading-7 text-gray-900">Comments</h3>

                <div role="list" className="bg-gray-100 rounded-md px-4 p-2">
                    <ul className="space-y-2">
                        {comments.map((comment, index) => (
                        <li
                            key={comment.commentId}
                            className={`bg-white rounded-lg shadow-md p-3 ${
                            index % 2 === 0 ? 'self-start' : 'self-end'
                            }`}
                        >
                            <p className="text-gray-600">{comment.content}</p>
                            <p className="text-xs text-gray-500">{comment.name}</p>
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
    )
}

export default Task