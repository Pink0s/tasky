const FormWrapper = ({children,title,description}) => {
    return(
        <div className="container mx-auto">
            <div className="max-w-md mx-auto my-10">
                <div className="text-center">
                    <h1 className="my-3 text-3xl font-semibold text-secondary">{title}</h1>
                    <p className="text-secondary">{description}</p>
                </div>
                {children}
            </div>
        </div>
    )

}

export default FormWrapper