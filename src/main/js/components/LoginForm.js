import Api from "../Api";
import React, {useState} from "react";

function LoginForm(props) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState(null);

    const onSubmit = event => {
        event.preventDefault();

        setErrorMessage(null);

        Api.login(username, password)
            .then(response => props.onLoggedIn())
            .catch(error => {
                if(error.response) {
                    switch (error.response.status) {
                        case 401: setErrorMessage('Incorrect username or password'); break;
                        default: setErrorMessage('Unknown error: ' + error.message);
                    }
                } else {
                    setErrorMessage('Error connecting to the server: ' + error.message);
                }
            });
    }

    return (
        <div className="form-container">
            <form onSubmit={onSubmit}>
                <input placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} type="text"/>
                <input placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} type="password"/>

                {errorMessage !== null && <p className="form-error-message">{errorMessage}</p>}

                <input type="submit" value="Login"/>
            </form>
        </div>
    )
}

export default LoginForm;