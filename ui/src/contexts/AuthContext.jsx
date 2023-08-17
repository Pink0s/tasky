import {createContext, useContext, useEffect, useState} from "react";
import {getMyProfile, login as performLogin} from "../utils/client.js";

const AuthContext = createContext({})


function parseJwt (token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}

// eslint-disable-next-line react/prop-types

const AuthProvider = ({ children }) => {

    const [user, setUser] = useState(null)

    const [authLoading,setAuthLoading] = useState(true);

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

    const isUserAuthenticated = () => {
        return localStorage.getItem("access_token") != null;
    }

    const logout = () => {
        localStorage.removeItem("access_token")
    }

    useEffect(() => {

        if(isUserAuthenticated() && !user) {
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

    })

    return (
        <AuthContext.Provider value={{user,login,isUserAuthenticated,authLoading, logout}}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext)
export default AuthProvider