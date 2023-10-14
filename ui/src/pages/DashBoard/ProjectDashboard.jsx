import SideBar from "./SideBar.jsx";
import {useEffect, useState} from "react";
import CreateProjectForm from "./CreateProjectForm.jsx";
import SuccessFullyCreated from "./AdminDashboard/SuccessFullyCreated.jsx";
import {useAuth} from "../../contexts/AuthContext.jsx";
import {fetchProjects} from "../../utils/client.js";
import Pagination from "./Pagination.jsx";
import {MagnifyingGlassIcon} from "@heroicons/react/20/solid/index.js";
import { useNavigate } from "react-router-dom";

const ProjectDashBoard = ({title}) => {
    const [showCreateForm, setShowCreateForm] = useState(false)
    const [showAlertCompletion, setShowAlertCompletion] = useState(false)
    const [alertContent,setAlertContent] = useState({})
    const {user} = useAuth();
    const [page,setPage] = useState(0)
    const [searchInput, setSearchInput] = useState("")
    const [criteria, setCriteria] = useState("name")
    const [loader, setLoader] = useState(true);
    const [pageable, setPageable] = useState({})
    const [projects, setProjects] = useState([])
    const [clear, setClear] = useState(false)
    const navigate = useNavigate();
    
    const handleChange = (e) => {
        e.preventDefault();
        setSearchInput(e.target.value);
    };

    const onChange = (event) => {
        const value = event.target.value;
        setCriteria(value);
    };


    useEffect(() => {
        if(searchInput !== "") {

            const timeOut = setTimeout(() => {
                setPage(0)
                fetchProjects({type:criteria, pattern: searchInput})
                    .then((res) => {
                        setProjects(res.data.projects)
                        setPageable(res.data.pageable)
                        setLoader(false)
                    })
                    .catch((err) => {
                        setPageable({})
                        setLoader(false)
                    })
            }, 500)
            return () => clearTimeout(timeOut)
        } else {
            setClear(true)
        }

    },[searchInput,page])


    useEffect( () => {

        setLoader(true)
        setClear(false)
        fetchProjects({page:page})
            .then((res) => {
                setProjects(res.data.projects)
                setPageable(res.data.pageable)
                setLoader(false)
            })
            .catch((err) => {
                console.error(err)
                setLoader(false)
            })

    },[showCreateForm, page, clear])

    if(loader) {
        return (
            <SideBar pageName="{title}">
                <div>Loading ...</div>
            </SideBar>
        )
    }
    
    function formatDate(date) {
        const parts = date.split("T");
        return parts[0]
    }

    return (
        <SideBar pageName={title}>
            {showCreateForm && <CreateProjectForm setOpen={setShowCreateForm} open={showCreateForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
            {showAlertCompletion && <SuccessFullyCreated alertContent={alertContent.content} open={showAlertCompletion} alertTitle={alertContent.title} setOpen={setShowAlertCompletion} setAlertContent={setAlertContent}/>}

            <div className="px-4 sm:px-6 lg:px-8">
                <div className="sm:flex sm:items-center">
                    <div className="sm:flex-auto">
                        <h1 className="text-base font-semibold leading-6 text-gray-900">Projects</h1>
                        <p className="mt-2 text-sm text-gray-700">
                            A list of all Projects.
                        </p>
                    </div>
                    <div className="flex flex-1 justify-center px-2 lg:ml-6 lg:justify-end">
                        <div className="w-full max-w-lg lg:max-w-xs">
                            <label htmlFor="search" className="sr-only">
                                Search
                            </label>
                            <div className="relative text-gray-400 focus-within:text-gray-600">
                                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                                    <MagnifyingGlassIcon className="h-5 w-5" aria-hidden="true" />
                                </div>
                                <input
                                    id="search"
                                    className="block w-full rounded-md border-0 bg-white py-1.5 pl-10 pr-3 text-gray-900 focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-indigo-600 sm:text-sm sm:leading-6"
                                    placeholder="Search"
                                    type="search"
                                    name="search"
                                    onChange={handleChange}
                                    value={searchInput}
                                />
                            </div>
                        </div>

                    </div>
                    {user.role === "PROJECT_MANAGER" && <div className="mt-4 sm:ml-16 sm:mt-0 sm:flex-none">
                        <button
                            type="button"
                            className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                            onClick={ () => {
                                setShowCreateForm(true)
                            }}
                        >
                            Add Project
                        </button>
                    </div>}



                </div>
                <div className="px-2 mx-4 mt-8 sm:-mx-0">
                    <table className="min-w-full divide-y divide-gray-300">
                        <thead>
                        <tr>
                            <th scope="col" className="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-0">
                                Project name
                            </th>
                            <th
                                scope="col"
                                className="hidden px-3 py-3.5 text-left text-sm font-semibold text-gray-900 lg:table-cell"
                            >
                                Project id
                            </th>
                            <th
                                scope="col"
                                className="hidden px-3 py-3.5 text-left text-sm font-semibold text-gray-900 sm:table-cell"
                            >
                                Due date
                            </th>
                            <th scope="col" className="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                                Project Manager
                            </th>
                            <th scope="col" className="relative py-3.5 pl-3 pr-4 sm:pr-0">
                                <span className="sr-only">Edit</span>
                            </th>
                        </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200 bg-white">
                            {
                                projects.map( (project) => (
                                        <tr key={project.projectID}>
                                            <td className="w-full max-w-0 py-4 pl-4 pr-3 text-sm font-medium text-gray-900 sm:w-auto sm:max-w-none sm:pl-4">
                                                {project.name}
                                                <dl className="font-normal lg:hidden">
                                                    <dt className="sr-only">Project due date</dt>
                                                    <dd className="mt-1 truncate text-gray-700">{
                                                        formatDate(project.dueDate)
                                                    }</dd>
                                                    <dt className="sr-only sm:hidden">Project id</dt>
                                                    <dd className="mt-1 truncate text-gray-500 sm:hidden">{project.projectID}</dd>
                                                </dl>
                                            </td>
                                            <td className="hidden px-3 py-4 text-sm text-gray-500 lg:table-cell">{project.projectID}</td>
                                            <td className="hidden px-3 py-4 text-sm text-gray-500 sm:table-cell">{formatDate(project.dueDate)}</td>
                                            <td className="px-3 py-4 text-sm text-gray-500">{project.creator}</td>
                                            <td className="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                                                <a
                                                    onClick={() => {
                                                        navigate("/project/"+project.projectID)
                                                    }}
                                                   className="text-accent hover:text-primary hover:cursor-pointer">
                                                    Access<span className="sr-only">, {project.projectID} </span>
                                                </a>
                                            </td>
                                        </tr>
                                    )

                                )
                            }
                        </tbody>
                    </table>
                    <div className="bg-white px-6 py-2">
                        {   (pageable && (pageable.numberOfPage > 1 ) ) &&
                            <Pagination numberOfItems={pageable.numberOfResult} currentPage={pageable.currentPage+1} numberOfPage={pageable.numberOfPage} setPage={setPage}></Pagination>
                        }
                    </div>
                </div>
            </div>
        </SideBar>
    )
}

export default ProjectDashBoard