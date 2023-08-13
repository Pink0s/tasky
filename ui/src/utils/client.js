import axios from "axios";
/**
 * Makes an asynchronous POST request to the authentication endpoint to log in a user.
 *
 * @async
 * @function
 * @param {Object} usernameAndPassword - An object containing the user's username and password.
 * @param {string} usernameAndPassword.username - The username of the user.
 * @param {string} usernameAndPassword.password - The password of the user.
 * @throws {Error} Throws an error if the login attempt fails.
 * @returns {Promise} A Promise that resolves to the response data from the authentication endpoint.
 */
export const login = async (usernameAndPassword) => {
    // eslint-disable-next-line no-useless-catch
    try {

        return await axios.post(
            `${import.meta.env.VITE_API_URL}/api/v1/user/auth`,
            usernameAndPassword
        )
    } catch (e) {
        throw new Error(e.message);
    }
}