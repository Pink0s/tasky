import { Fragment } from 'react'
import { Disclosure, Menu, Transition } from '@headlessui/react'
import { Bars3Icon, XMarkIcon } from '@heroicons/react/24/outline'
import {UserCircleIcon} from "@heroicons/react/24/outline";
import Logo from "../../assets/logo.png"
import {useAuth} from "../../contexts/AuthContext.jsx";
import {page} from "../../hooks/useRouter.jsx";
import {useNavigate} from "react-router-dom";



function classNames(...classes) {
    return classes.filter(Boolean).join(' ')
}

export default function SideBar({children, pageName}) {
    const {user, logout} = useAuth();
    const navigate = useNavigate();

    const navigation = [
        {
            name: 'Dashboard',
            handleClick:() => {
                navigate(page.DashBoard)
            },
            current: true }
    ]

    const userNavigation = [
        {
            name: 'Your Profile',
            handleClick: () => {
                navigate(page.Profile)
            }
        },
        {
            name: 'Sign out',
            handleClick: () => {
                logout()
                navigate(page.DashBoard)
            }
        }]

    return (
        <>
            <div className="min-h-full">
                <div className="bg-primary pb-32">
                    <Disclosure as="nav" className="border-b border-secondary border-opacity-25 bg-secondary lg:border-none">
                        {({ open }) => (
                            <>
                                <div className="mx-auto max-w-7xl px-2 sm:px-4 lg:px-8">
                                    <div className="relative flex h-16 items-center justify-between lg:border-b lg:border-secondary lg:border-opacity-25">
                                        <div className="flex items-center px-2 lg:px-0">
                                            <div className="flex-shrink-0">
                                                <img
                                                    className="block h-8 w-12"
                                                    src={Logo}
                                                    alt="Tasky"
                                                />
                                            </div>
                                            <div className="hidden lg:ml-10 lg:block">
                                                <div className="flex space-x-4">
                                                    {navigation.map((item) => (
                                                        <a
                                                            key={item.name}
                                                            className={classNames(
                                                                item.current
                                                                    ? 'bg-accent text-white hover:bg-primary'
                                                                    : 'text-white hover:bg-accent hover:bg-opacity-75',
                                                                'rounded-md py-2 px-3 text-sm font-medium'
                                                            )}
                                                            onClick={item.handleClick}
                                                            aria-current={item.current ? 'page' : undefined}
                                                        >
                                                            {item.name}
                                                        </a>
                                                    ))}
                                                </div>
                                            </div>
                                        </div>

                                        <div className="flex lg:hidden">
                                            {/* Mobile menu button */}
                                            <Disclosure.Button className="relative inline-flex items-center justify-center rounded-md bg-accent p-2 text-secondary hover:bg-accent hover:bg-opacity-75 hover:text-white focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-primary">
                                                <span className="absolute -inset-0.5" />
                                                <span className="sr-only">Open main menu</span>
                                                {open ? (
                                                    <XMarkIcon className="block h-6 w-6" aria-hidden="true" />
                                                ) : (
                                                    <Bars3Icon className="block h-6 w-6" aria-hidden="true" />
                                                )}
                                            </Disclosure.Button>
                                        </div>
                                        <div className="hidden lg:ml-4 lg:block">
                                            <div className="flex items-center">

                                                {/* Profile dropdown */}
                                                <Menu as="div" className="relative ml-3 flex-shrink-0">
                                                    <div>
                                                        {/*here is the profile pic when full width*/}
                                                        <Menu.Button className="relative flex rounded-full bg-accent text-sm text-secondary focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-accent hover:text-primaryButton" >
                                                            <span className="absolute -inset-1.5" />
                                                            <span className="sr-only">Open user menu</span>
                                                            <UserCircleIcon className="block h-8 w-8"/>
                                                        </Menu.Button>
                                                    </div>
                                                    <Transition
                                                        as={Fragment}
                                                        enter="transition ease-out duration-100"
                                                        enterFrom="transform opacity-0 scale-95"
                                                        enterTo="transform opacity-100 scale-100"
                                                        leave="transition ease-in duration-75"
                                                        leaveFrom="transform opacity-100 scale-100"
                                                        leaveTo="transform opacity-0 scale-95"
                                                    >
                                                        <Menu.Items className="absolute right-0 z-10 mt-2 w-48 origin-top-right rounded-md bg-white py-1 shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                                                            {userNavigation.map((item) => (
                                                                <Menu.Item key={item.name}>
                                                                    {({ active }) => (
                                                                        <a
                                                                            className={classNames(
                                                                                active ? 'bg-gray-100' : '',
                                                                                'block px-4 py-2 text-sm text-gray-700'
                                                                            )}
                                                                            onClick={item.handleClick}
                                                                        >
                                                                            {item.name}
                                                                        </a>
                                                                    )}
                                                                </Menu.Item>
                                                            ))}
                                                        </Menu.Items>
                                                    </Transition>
                                                </Menu>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <Disclosure.Panel className="lg:hidden">
                                    <div className="space-y-1 px-2 pb-3 pt-2">
                                        {navigation.map((item) => (
                                            <Disclosure.Button
                                                key={item.name}
                                                as="a"
                                                //href={item.href}
                                                className={classNames(
                                                    item.current
                                                        ? 'bg-accent text-white hover:bg-primary'
                                                        : 'text-white hover:bg-primary hover:bg-opacity-75',
                                                    'block rounded-md py-2 px-3 text-base font-medium'
                                                )}
                                                onClick={item.handleClick}
                                                aria-current={item.current ? 'page' : undefined}
                                            >
                                                {item.name}
                                            </Disclosure.Button>
                                        ))}
                                    </div>
                                    {/*is inside dropdown menu*/}
                                    <div className="border-t border-primary pb-3 pt-4">
                                        <div className="flex items-center px-5">
                                            <div className="ml-3">
                                                <div className="text-base font-medium text-primary">{user.firstName+" "+user.lastName}</div>
                                                <div className="text-sm font-medium text-accent">{user.email}</div>
                                            </div>
                                        </div>
                                        <div className="mt-3 space-y-1 px-2">
                                            {userNavigation.map((item) => (
                                                <Disclosure.Button
                                                    key={item.name}
                                                    onClick={item.handleClick}
                                                    as="a"
                                                    className="block rounded-md px-3 py-2 text-base font-medium text-accent hover:bg-accent hover:text-secondary hover:bg-opacity-75"
                                                >
                                                    {item.name}
                                                </Disclosure.Button>
                                            ))}
                                        </div>
                                    </div>
                                </Disclosure.Panel>
                            </>
                        )}
                    </Disclosure>
                    <header className="py-10">
                        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
                            <h1 className="text-3xl font-bold tracking-tight text-white">{pageName}</h1>
                        </div>
                    </header>
                </div>

                <main className="-mt-32">
                    <div className="mx-auto max-w-7xl px-4 pb-12 sm:px-6 lg:px-8 bg-secondary rounded-md">{children}</div>
                </main>
            </div>
        </>
    )
}
