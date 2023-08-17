import SideBar from "../SideBar.jsx";
import Pagination from "../Pagination.jsx";
import {useEffect, useState} from "react";
import {fetchUsers} from "../../../utils/client.js";
import CreateUserForm from "./CreateUserForm.jsx";
import SuccessFullyCreated from "./SuccessFullyCreated.jsx";
import EditUserForm from "./EditUserForm.jsx";
import {MagnifyingGlassIcon} from "@heroicons/react/20/solid/index.js";

const AdminDashboard = () => {

    const [users, setUsers] = useState([]);
    const [pageable, setPageable] = useState({})
    const [loader, setLoader] = useState(true);
    const [showCreateForm, setShowCreateForm] = useState(false)
    const [showAlertCompletion, setShowAlertCompletion] = useState(false)
    const [alertContent,setAlertContent] = useState({})
    const [showUpdateForm, setShowUpdateForm] = useState(false)
    const [userId, setUserId] = useState(null)
    const [searchInput, setSearchInput] = useState("")
    const [criteria, setCriteria] = useState("email")
    const [page,setPage] = useState(0)
    const handleChange = (e) => {
        e.preventDefault();
        setSearchInput(e.target.value);

    };

    const onChange = (event) => {
        const value = event.target.value;
        console.log(value)
        setCriteria(value);
    };


    useEffect(() => {
        if(searchInput !== "") {

            const timeOut = setTimeout(() => {
                setPage(0)
                fetchUsers({type:criteria, pattern: searchInput})
                    .then((res) => {
                        setUsers(res.data.users)
                        setPageable(res.data.pageableDto)
                        setLoader(false)
                    })
                    .catch((err) => {
                        setUsers([])
                        setPageable({})
                        setLoader(false)
                    })
            }, 500)
            return () => clearTimeout(timeOut)
        }

    },[searchInput,page])


    useEffect( () => {

        setLoader(true)

        fetchUsers({page:page})
            .then((res) => {
                setUsers(res.data.users)

                setPageable(res.data.pageableDto)
                setLoader(false)
            })
            .catch((err) => {
                console.error(err)
                setLoader(false)
            })

    },[showCreateForm,showUpdateForm,page])

    if(loader) {
        return (
            <SideBar pageName="Admin Dasboard">
                <div>Loading ...</div>
            </SideBar>
        )
    }

    return <SideBar pageName="Admin Dasboard">
        {showCreateForm && <CreateUserForm setOpen={setShowCreateForm} open={showCreateForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
        {showUpdateForm && <EditUserForm setOpen={setShowUpdateForm} open={showUpdateForm} setShowAlertCompletion={setShowAlertCompletion} setAlertContent={setAlertContent} userId={userId}/>}

        {showAlertCompletion && <SuccessFullyCreated alertContent={alertContent.content} open={showAlertCompletion} alertTitle={alertContent.title} setOpen={setShowAlertCompletion} setAlertContent={setAlertContent}/>}
        <div className="px-4 sm:px-6 lg:px-8">
            <div className="sm:flex sm:items-center">
                <div className="sm:flex-auto">
                    <h1 className="text-base font-semibold leading-6 text-gray-900">Users</h1>
                    <p className="mt-2 text-sm text-gray-700">
                        A list of all the users including their Firstname, Lastname, email and role.
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
                <select
                    onChange={onChange}
                    value={criteria}
                    id="passwordReset"
                    name="passwordReset"
                    className="mt-2 block w-30 rounded-md border-0 py-0 pl-3 pr-10 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-indigo-600 sm:text-sm sm:leading-6"

                >
                    <option value="email">Email</option>
                    <option value="lastName">Lastname</option>
                    <option value="firstName">Firstname</option>

                </select>

                <div className="mt-4 sm:ml-16 sm:mt-0 sm:flex-none">
                    <button
                        type="button"
                        className="block rounded-md bg-primaryButton px-3 py-2 text-center text-sm font-semibold text-secondaryButton shadow-sm hover:bg-accent focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primaryButton"
                        onClick={ () => {
                            setShowCreateForm(true)
                        }}
                    >
                        Add user
                    </button>
                </div>
            </div>

            <div className="mt-8 flow-root">
                <div className="-mx-4 -my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
                    <div className="inline-block min-w-full py-2 align-middle sm:px-6 lg:px-8">
                        <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 sm:rounded-lg">
                            <table className="min-w-full divide-y divide-gray-300">
                                <thead className="bg-gray-50">
                                <tr>
                                    <th scope="col" className="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6">
                                        Firstname
                                    </th>
                                    <th scope="col" className="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                                        Lastname
                                    </th>
                                    <th scope="col" className="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                                        Email
                                    </th>
                                    <th scope="col" className="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                                        Role
                                    </th>
                                    <th scope="col" className="relative py-3.5 pl-3 pr-4 sm:pr-6">
                                        <span className="sr-only">Access</span>
                                    </th>
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-200 bg-white">
                                {users.map((user) => (
                                    <tr key={user.email}>
                                        <td className="whitespace-nowrap py-4 pl-4 pr-3 text-sm font-medium text-gray-900 sm:pl-6">
                                            {user.firstName}
                                        </td>
                                        <td className="whitespace-nowrap px-3 py-4 text-sm text-gray-500">{user.lastName}</td>
                                        <td className="whitespace-nowrap px-3 py-4 text-sm text-gray-500">{user.email}</td>
                                        <td className="whitespace-nowrap px-3 py-4 text-sm text-gray-500">{user.role}</td>
                                        <td className="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
                                            <a onClick={() => {
                                                setShowUpdateForm(true)
                                                setUserId(user.id)
                                            }} className="text-accent hover:text-primary">
                                                Access<span className="sr-only">, {user.id}</span>
                                            </a>
                                        </td>
                                    </tr>
                                ))}

                                </tbody>

                            </table>
                            <div className="bg-white px-6 py-2">
                                {   (pageable && (pageable.numberOfPage > 1 ) ) &&
                                        <Pagination numberOfItems={pageable.numberOfResult} currentPage={pageable.currentPage+1} numberOfPage={pageable.numberOfPage} setPage={setPage}></Pagination>
                                }
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </SideBar>
}

export default AdminDashboard