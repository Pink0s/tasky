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