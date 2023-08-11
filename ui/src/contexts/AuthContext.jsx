import {createContext, useContext, useState} from "react";
import {login as performLogin} from "../utils/client.js";

/**
 * Creates a context to manage authentication-related information.
 * @function createContext
 * @param {Object} defaultValue - The default value for the context.
 * @returns {AuthContext} The authentication context.
 */
const AuthContext = createContext({})

// eslint-disable-next-line react/prop-types
/**
 * Provides authentication-related functionality to its children components.
 * @component
 * @param {Object} props - Component props
 * @param {React.ReactNode} props.children - The child components that require authentication.
 * @returns {React.ReactNode} Children components wrapped in the authentication context.
 */
const AuthProvider = ({ children }) => {
    /**
     * State to store the authenticated user's information.
     * @typedef {Object} user
     */
    const [user, setUser] = useState(null)

    /**
     * Logs in a user with provided username and password.
     * @async
     * @function
     * @param {Object} usernameAndPassword - An object containing the user's username and password.
     * @param {string} usernameAndPassword.username - The username of the user.
     * @param {string} usernameAndPassword.password - The password of the user.
     * @throws {Error} Throws an error if the login attempt fails.
     * @returns {Promise} A Promise that resolves to the login response.
     */
    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
          performLogin(usernameAndPassword).then(res => {
              const jwt = res.headers['authorization']

              localStorage.setItem("access_token",jwt)

              setUser({...res.data.userDto})
              resolve(res)
          }).catch( err =>
              reject(err)
          )
        })
    }

    /**
     * Checks if the user is authenticated.
     * @function
     * @returns {boolean} True if the user is authenticated, otherwise false.
     */
    const isUserAuthenticated = () => {
        return localStorage.getItem("access_token") != null;
    }

    return (
        <AuthContext.Provider value={{user,login,isUserAuthenticated}}>
            {children}
        </AuthContext.Provider>
    )
}

/**
 * Custom hook to use the authentication context.
 * @function useAuth
 * @returns {AuthContext} Authentication context.
 */
export const useAuth = () => useContext(AuthContext)
export default AuthProvider