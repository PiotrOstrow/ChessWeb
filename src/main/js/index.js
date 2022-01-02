import React, {useState} from 'react';
import ReactDOM from 'react-dom';
import LoginForm from "./components/LoginForm";
import {createTheme, ThemeProvider} from "@mui/material/styles";
import GameComponent from "./GameComponent";
import {MemoryRouter, Route, Routes} from "react-router-dom";
import {CssBaseline} from "@mui/material";
import NavBar from "./components/NavBar";
import Api from "./api/Api";
import PuzzlePage from "./PuzzlePage";

const theme = createTheme({
    palette: {
        mode: 'dark',
    },
});

function App() {
    const [loggedIn, setLoggedIn] = useState(false);
    const [gameApi, setGameApi] = useState(null);

    const onLoggedIn = () => {
        setGameApi(Api.gameApi());
        setLoggedIn(true);
    }

    if (!loggedIn) {
        return <LoginForm onLoggedIn={onLoggedIn}/>;
    } else {
        return (
            <div>
                <NavBar/>
                <div>
                    <Routes>
                        <Route path="/" element={<GameComponent gameApi={gameApi}/>}/>
                        <Route path="/profile" element={<ProfilePage/>}/>
                        <Route path="/puzzles" element={<PuzzlePage/>}/>
                    </Routes>
                </div>
            </div>
        );
    }
}

function ProfilePage() {
    return (
        <div>
            profile page
        </div>
    );
}

ReactDOM.render(
    <MemoryRouter>
        <ThemeProvider theme={theme}>
            <App/>
            <CssBaseline/>
        </ThemeProvider>
    </MemoryRouter>,
    document.getElementById('root')
);
