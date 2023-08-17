const CustomInputField = (props) => {
    return (
        <>
            <input type={props.type} id={props.useFor} placeholder={props.placeholder} {...props.formik.getFieldProps(props.useFor)}
                   className="w-full px-3 py-2 placeholder-accent border border-gray-300 rounded-md  bg-secondary text-primary focus:outline-none focus:ring focus:ring-blue-300 focus:border-blue-600 "/>
            {props.touched && props.errors ? (
                <div className="text-sm px-3 py-2 text-error w-full">{props.errors}</div>
            ) : null}
        </>
    )
}

export default CustomInputField