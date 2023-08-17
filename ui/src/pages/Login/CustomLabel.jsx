const CustomLabel = (props) => {
    return <label htmlFor={props.useFor} className="block mb-2 text-sm text-secondary">{props.title}</label>
}
export default CustomLabel