import FormSectionWrapper from "./FormSectionWrapper.jsx";
import CustomLabel from "./CustomLabel.jsx";
import CustomInputField from "./CustomInputField.jsx";
import FormSubmitButton from "./FormSubmitButton.jsx";

const SignInForm = (props) => {
    return (
        <div className="m-7">
            <form onSubmit={props.formik.handleSubmit}>
                <FormSectionWrapper>
                    <CustomLabel useFor="email" title="Email Address" />
                    <CustomInputField type="email" useFor="username" placeholder="you@tasky.com" touched={props.formik.touched.username} errors={props.formik.errors.username} formik={props.formik}/>
                </FormSectionWrapper>
                <FormSectionWrapper>
                    <div className="flex justify-between mb-2">
                        <CustomLabel useFor="password" title="Password" />
                    </div>
                    <CustomInputField type="password" useFor="password" placeholder="Your Password" touched={props.formik.touched.password} errors={props.formik.errors.password} formik={props.formik}/>
                </FormSectionWrapper>
                <FormSectionWrapper>
                    <FormSubmitButton title="Sign in"/>
                </FormSectionWrapper>
            </form>
        </div>
    )
}

export default SignInForm