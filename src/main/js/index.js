import React, {useState} from 'react';
import ReactDOM from 'react-dom';
import LoginForm from "./components/LoginForm";
import {createTheme, ThemeProvider} from "@mui/material/styles";
import GameComponent from "./GameComponent";

const theme = createTheme({
    palette: {
        mode: 'dark',
    },
});

function App() {
    const [loggedIn, setLoggedIn] = useState(false);

    return loggedIn ? <GameComponent/> : <LoginForm onLoggedIn={() => setLoggedIn(true)}/>;
}

ReactDOM.render(
    <ThemeProvider theme={theme}>
        <App/>
    </ThemeProvider>,
    document.getElementById('root')
);
