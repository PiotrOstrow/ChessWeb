import Api from "../Api";
import React from "react";

class LoginForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            errorMessage: null
        }
    }

    onSubmit(event) {
        event.preventDefault();

        this.setState({errorMessage: null});

        Api.login(this.state.username, this.state.password)
            .then(response => this.props.onLoggedIn())
            .catch(error => {
                if(error.response) {
                    switch (error.response.status) {
                        case 401: this.setState({errorMessage: 'Incorrect username or password'}); break;
                        default: this.setState({errorMessage: 'Unknown error: ' + error.message});
                    }
                } else {
                    this.setState({errorMessage: 'Error connecting to the server: ' + error.message});
                }
            });
    }

    render() {
        return (
            <div className="form-container">
                <form onSubmit={this.onSubmit.bind(this)}>
                    <input placeholder="Username" value={this.state.username} onChange={e => this.setState({username: e.target.value})} type="text"/>
                    <input placeholder="Password" value={this.state.password} onChange={e => this.setState({password: e.target.value})} type="password"/>
                    {this.state.errorMessage !== null && <p className="form-error-message">{this.state.errorMessage}</p>}
                    <input type="submit" value="Login"/>
                </form>
            </div>
        )
    }
}

export default LoginForm;