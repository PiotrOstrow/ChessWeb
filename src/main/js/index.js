import React from 'react';
import ReactDOM from 'react-dom';

import Board from "./components/board/Board";
import LoginForm from "./components/LoginForm";


class App extends React.Component {
    constructor(prop) {
        super(prop);
        this.state = {
            loggedIn: false
        }
    }

    onLogin() {
        this.setState({loggedIn: true});
    }

    render() {
        return this.state.loggedIn ? <Board/> : <LoginForm onLoggedIn={this.onLogin.bind(this)}/>;
    }
}

ReactDOM.render(
    <App/>,
    document.getElementById('root')
);
