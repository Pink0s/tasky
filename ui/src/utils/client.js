import axios from "axios";

const getAuthConfig = () => ({
        headers: {
                Authorization: `Bearer ${localStorage.getItem("access_token")}`
        }
})

export const login = async (usernameAndPassword) => {
        return await axios.post(
            `${import.meta.env.VITE_API_URL}/api/v1/user/auth`,
            usernameAndPassword
        )

}

export const getMyProfile = async () => {
        return await axios.get(
            `${import.meta.env.VITE_API_URL}/api/v1/user/profile`,
            getAuthConfig()
        )
}

export const changePassword = async ({oldPassword, newPassword}) => {
        return await axios.patch(
            `${import.meta.env.VITE_API_URL}/api/v1/user/profile`,
            {
                    oldPassword,
                    newPassword
            },
            getAuthConfig()
        )
}

export const fetchUsers = async (params) => {
    if(params) {
        return await axios.get(
            `${import.meta.env.VITE_API_URL}/api/v1/user`,
            {
                params: {
                    ...params
                },
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("access_token")}`
                }
            },

        )
    }

    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/user`,
        getAuthConfig()
    )
}

export const fetchUserById = async ({id}) => {

    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/user/${id}`,
        getAuthConfig()
    )
}

export const createUser = async ({firstName, lastName, email}) => {
    return await axios.post(
        `${import.meta.env.VITE_API_URL}/api/v1/user`,
        {
            firstName,
            lastName,
            email
        },
        getAuthConfig()
    )
}

export const updateUser = async (id, {passwordReset, role}) => {

    const url = `${import.meta.env.VITE_API_URL}/api/v1/user/${id}`

    return await axios.patch(
        url,
        {
            passwordReset,
            role
        },
        getAuthConfig()
    )
}

export const deleteUser = async (id) => {
    const url = `${import.meta.env.VITE_API_URL}/api/v1/user/${id}`

    return await axios.delete(
        url,
        getAuthConfig()
    )

}


export const createProject = async ({name, date, description}) => {
    const url = `${import.meta.env.VITE_API_URL}/api/v1/project`

    let dueDate = Math.floor(new Date(date).getTime() / 1000)

    return await axios.post(
        url,
        {
            name,
            dueDate,
            description
        },
        getAuthConfig()
    )
}

export const fetchProjects = async (params) => {
    if(params) {
        return await axios.get(
            `${import.meta.env.VITE_API_URL}/api/v1/project`,
            {
                params: {
                    ...params
                },
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("access_token")}`
                }
            },

        )
    }

    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/project`,
        getAuthConfig()
    )
}

export const fetchProjectById = async ({id}) => {
    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/project/${id}`,
        getAuthConfig()
    )
}

export const deleteProject = async (id) => {
    const url = `${import.meta.env.VITE_API_URL}/api/v1/project/${id}`

    return await axios.delete(
        url,
        getAuthConfig()
    )

}

export const updateProject = async (id, {name, date, description}) => {

    const url = `${import.meta.env.VITE_API_URL}/api/v1/project/${id}`
    return await axios.patch(
        url,
        {
            name,
            dueDate : Math.floor(new Date(date).getTime() / 1000),
            description
        },
        getAuthConfig()
    )



}

export const addUser = async (id, {userId}) => {

    const url = `${import.meta.env.VITE_API_URL}/api/v1/project/${id}/addUser`
    return await axios.patch(
        url,
        {
            userId
        },
        getAuthConfig()
    )



}

export const addRun = async (id, { name, description, startDate, endDate }) => {

    const url = `${import.meta.env.VITE_API_URL}/api/v1/run/project/${id}`
    
    return await axios.post(
        url,
        {
            name, 
            description,
            startDate: Math.floor(new Date(startDate).getTime() / 1000),
            endDate: Math.floor(new Date(endDate).getTime() / 1000)
        },
        getAuthConfig()
    )
}


export const fetchRuns = async (id, params) => {
    if(params) {
        return await axios.get(
            `${import.meta.env.VITE_API_URL}/api/v1/run/project/${id}`,
            {
                params: {
                    ...params
                },
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("access_token")}`
                }
            },

        )
    }

    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/run/project/${id}`,
        getAuthConfig()
    )
}

export const fetchRunById = async (id) => {
    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/run/${id}`,
        getAuthConfig()
    )
}

export const fetchFeatureById = async (id) => {
    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/feature/${id}`,
        getAuthConfig()
    )
}

export const fetchTaskById = async (id) => {
    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/toDo/${id}`,
        getAuthConfig()
    )
}


export const addFeature = async (id, { name, description }) => {

    const url = `${import.meta.env.VITE_API_URL}/api/v1/feature/run/${id}`
    
    return await axios.post(
        url,
        {
            name, 
            description,
        },
        getAuthConfig()
    )
}

export const fetchFeatures = async (id, params) => {
    if(params) {
        return await axios.get(
            `${import.meta.env.VITE_API_URL}/api/v1/feature/run/${id}`,
            {
                params: {
                    ...params
                },
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("access_token")}`
                }
            },

        )
    }

    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/feature/run/${id}`,
        getAuthConfig()
    )
}


export const deleteRun = async (id) => {
    const url = `${import.meta.env.VITE_API_URL}/api/v1/run/${id}`

    return await axios.delete(
        url,
        getAuthConfig()
    )

}


export const updateRun = async (id, {name, description, status, formatedStartDate, formatedEndDate}) => {

    const url = `${import.meta.env.VITE_API_URL}/api/v1/run/${id}`

    return await axios.patch(
        url,
        {
            name, 
            description, 
            status, 
            startDate : formatedStartDate,
            endDate: formatedEndDate
        },
        getAuthConfig()
    )
}


export const deleteFeature = async (id) => {
    const url = `${import.meta.env.VITE_API_URL}/api/v1/feature/${id}`

    return await axios.delete(
        url,
        getAuthConfig()
    )

}

export const updateFeature = async (id, {name, description, status}) => {

    const url = `${import.meta.env.VITE_API_URL}/api/v1/feature/${id}`

    return await axios.patch(
        url,
        {
            name, 
            description, 
            status
        },
        getAuthConfig()
    )
}

export const addTask = async (id, { name, description, type }) => {

    const url = `${import.meta.env.VITE_API_URL}/api/v1/toDo/feature/${id}`
    
    return await axios.post(
        url,
        {
            name, 
            description,
            type
        },
        getAuthConfig()
    )
}

export const fetchTasks = async (id, params) => {
    if(params) {
        return await axios.get(
            `${import.meta.env.VITE_API_URL}/api/v1/toDo/feature/${id}`,
            {
                params: {
                    ...params
                },
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("access_token")}`
                }
            },

        )
    }

    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/toDo/feature/${id}`,
        getAuthConfig()
    )
}


export const deleteTask = async (id) => {
    const url = `${import.meta.env.VITE_API_URL}/api/v1/toDo/${id}`

    return await axios.delete(
        url,
        getAuthConfig()
    )

}

export const updateTask = async (id, {title, description, status, type}) => {

    const url = `${import.meta.env.VITE_API_URL}/api/v1/toDo/${id}`

    return await axios.patch(
        url,
        {
            name: title, 
            description, 
            status, 
            type
        },
        getAuthConfig()
    )
}


export const addComment = async (id, { name, content }) => {

    const url = `${import.meta.env.VITE_API_URL}/api/v1/comment/toDo/${id}`
    
    return await axios.post(
        url,
        {
            name, 
            content
        },
        getAuthConfig()
    )
}


export const fetchComments = async (id, params) => {
    if(params) {
        return await axios.get(
            `${import.meta.env.VITE_API_URL}/api/v1/comment/toDo/${id}`,
            {
                params: {
                    ...params
                },
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("access_token")}`
                }
            },

        )
    }

    return await axios.get(
        `${import.meta.env.VITE_API_URL}/api/v1/comment/toDo/${id}`,
        getAuthConfig()
    )
}