import React, {useEffect, useState} from 'react';
import ReactDOM from 'react-dom';

import Board from "./components/board/Board";
import LoginForm from "./components/LoginForm";
import Api from "./Api";
import ChessPosition from "./ChessPosition";

function App() {
    const [loggedIn, setLoggedIn] = useState(false);

    return loggedIn ? <Game/> : <LoginForm onLoggedIn={() => setLoggedIn(true)}/>;
}

function Game() {
    const [gameApi, setGameApi] = useState(null);
    const [chessPosition, setChessPosition] = useState(ChessPosition.default());

    useEffect(() => {
        const gameApi = Api.gameApi(() => gameApi.play());
        setGameApi(gameApi);
    }, []);

    const onMove = (from, to) => {
        gameApi.move(from, to);

        const newPosition = chessPosition.copy();
        newPosition.pieces.set(to, newPosition.getPiece(from));
        newPosition.pieces.set(from, null);

        setChessPosition(newPosition);
    }

    return (
        <div>
            <Board chessPosition={chessPosition} onMove={(from, to) => onMove(from, to)}/>
        </div>
    );
}

ReactDOM.render(
    <App/>,
    document.getElementById('root')
);
