import axios from "axios";

export const login = async (usernameAndPassword) => {
    // eslint-disable-next-line no-useless-catch
    try {
        return await axios.post(
            `${import.meta.env.BASE_URL}/api/v1/user/auth`,
            usernameAndPassword
        )
    } catch (e) {
        throw e;
    }
}