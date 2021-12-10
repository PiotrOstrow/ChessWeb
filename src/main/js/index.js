import React, {useEffect, useState} from 'react';
import ReactDOM from 'react-dom';

import Api from "./api/Api";
import Board from "./components/board/Board";
import ChessPosition from "./ChessPosition";
import ChessBoardModal from "./components/ChessBoardModal";
import LoginForm from "./components/LoginForm";

function App() {
    const [loggedIn, setLoggedIn] = useState(false);

    return loggedIn ? <Game/> : <LoginForm onLoggedIn={() => setLoggedIn(true)}/>;
}

function Game() {
    const [gameApi, setGameApi] = useState(null);
    const [chessPosition, setChessPosition] = useState(ChessPosition.default());
    const [isPlaying, setIsPlaying] = useState(false);
    const [isInQue, setIsInQue] = useState(false);

    useEffect(() => {
        const gameApi = Api.gameApi();
        gameApi.onRecvMove = (data) => onMove(data.from, data.to);
        gameApi.onRecvStart = (data) => setIsPlaying(true);
        setGameApi(gameApi);
    }, []);

    const onMove = (from, to) => {
        setChessPosition(prevPosition => {
            const newPosition = prevPosition.copy();
            newPosition.pieces.set(to, newPosition.getPiece(from));
            newPosition.pieces.set(from, null);
            setChessPosition(newPosition);
        });
    }

    const onOwnMove = (from, to) => {
        onMove(from, to);
        gameApi.move(from, to);
    }

    const onPressPlay = () => {
        setIsInQue(true);
        gameApi.play();
    }

    return (
        <div className="game-container">
            <Board chessPosition={chessPosition} onMove={(from, to) => onOwnMove(from, to)}/>
            <ChessBoardModal isPlaying={isPlaying} isInQueue={isInQue} onPressPlay={onPressPlay}/>
        </div>
    );
}

ReactDOM.render(
    <App/>,
    document.getElementById('root')
);
