import { ArrowLongLeftIcon, ArrowLongRightIcon } from '@heroicons/react/20/solid'

export default function Pagination({numberOfItems, currentPage, numberOfPage, lastPage, setPage}) {

    return (<>

        <nav className="flex items-center justify-around border-t border-gray-200 px-4 sm:px-0">
            <div className="-mt-px flex w-0 flex-1">
                {currentPage !== 1 && <a onClick={() => {
                    setPage(Number(currentPage-2))

                }}
                    href="#"
                    className="inline-flex items-center border-t-2 border-transparent pr-1 pt-4 text-sm font-medium text-gray-500 hover:border-gray-300 hover:text-gray-700"
                >
                    <ArrowLongLeftIcon className="mr-3 h-5 w-5 text-gray-400" aria-hidden="true" />
                    Previous
                </a>}

            </div>

            <div  className="-mt-px flex w-0 flex-1 justify-end">
                {
                    (currentPage+1) === numberOfPage && <a
                        onClick={() => {
                            setPage(Number(currentPage))
                        }}
                        className="inline-flex items-center border-t-2 border-transparent pl-1 pt-4 text-sm font-medium text-gray-500 hover:border-gray-300 hover:text-gray-700"
                    >
                        Next
                        <ArrowLongRightIcon className="ml-3 h-5 w-5 text-gray-400" aria-hidden="true" />
                    </a>
                }

            </div>
        </nav>
            <div className={"flex justify-center items-center"}>
                <p className={"border-t-2 border-transparent text-sm font-medium text-gray-500"}>
                    Page {currentPage} of {numberOfPage}
                </p>
            </div>
            <div className={"flex justify-center items-center"}>
                <p className={"border-t-2 border-transparent text-sm font-medium text-gray-500"}>
                    Items {numberOfItems}
                </p>
            </div>


        </>
    )
}
