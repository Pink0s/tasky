import {createContext, useContext, useState} from "react";
import {login as performLogin } from "../utils/client.js";

const AuthContext = createContext({})

// eslint-disable-next-line react/prop-types
const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null)
    const login = async (usernameAndPassword) => {
        return new Promise((resolve, reject) => {
          performLogin(usernameAndPassword).then(res => {
              const jwt = res.headers['authorization']

              localStorage.setItem("access_token",jwt)
              console.log(localStorage.getItem("access_token"))
              setUser({...res.data.userDto})
              resolve(res)
          }).catch( err =>
              reject(err)
          )
        })
    }

    return (
        <AuthContext.Provider value={{user,login}}>
            {children}
        </AuthContext.Provider>
    )
}
export const useAuth = () => useContext(AuthContext)
export default AuthProvider