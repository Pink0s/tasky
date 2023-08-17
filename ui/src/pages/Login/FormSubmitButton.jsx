const FormSubmitButton = (props) => {
    return (
        <button type="submit" className="w-full px-3 py-4 text-primaryButton bg-secondaryButton rounded-md focus:bg-accent focus:outline-none hover:bg-accent" >
            {props.title}
        </button>
    )
}
export default FormSubmitButton