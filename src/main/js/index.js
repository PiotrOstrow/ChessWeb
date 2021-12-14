import React, {useEffect, useState} from 'react';
import ReactDOM from 'react-dom';

import Api from "./api/Api";
import Board from "./components/board/Board";
import ChessPosition from "./chess/ChessPosition";
import ChessBoardModal from "./components/ChessBoardModal";
import LoginForm from "./components/LoginForm";
import {createTheme, ThemeProvider} from "@mui/material/styles";
import Game from "./chess/Game";

const theme = createTheme({
    palette: {
        mode: 'dark',
    },
});

function App() {
    const [loggedIn, setLoggedIn] = useState(false);

    return loggedIn ? <GameComponent/> : <LoginForm onLoggedIn={() => setLoggedIn(true)}/>;
}


function GameComponent() {
    const [gameApi, setGameApi] = useState(null);
    const [game, setGame] = useState(new Game());
    const [chessPosition, setChessPosition] = useState(ChessPosition.default());
    const [isPlaying, setIsPlaying] = useState(false);
    const [isInQue, setIsInQue] = useState(false);
    const [lastGameResult, setLastGameResult] = useState(null);
    const [color, setColor] = useState('WHITE');

    useEffect(() => {
        const gameApi = Api.gameApi();
        gameApi.onRecvMove = data => onMove(data.from, data.to);
        gameApi.onRecvStart = data => {
            setIsPlaying(true);
            setIsInQue(false)
            setColor(data.color);
            setChessPosition(ChessPosition.default());
            game.reset();
        };
        gameApi.onRecvGameOver = data => onGameOver(data);
        setGameApi(gameApi);
    }, []);

    const onGameOver = data => {
        setLastGameResult(data.gameResult);
        setIsPlaying(false);
        setIsInQue(false);
    }

    const onMove = (from, to) => {
        game.move(from, to);
        setChessPosition(game.getChessPosition());
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
            <Board chessPosition={chessPosition} onMove={(from, to) => onOwnMove(from, to)}
                   flipped={color === 'BLACK'}/>
            <ChessBoardModal isPlaying={isPlaying}
                             isInQueue={isInQue}
                             lastGameResult={lastGameResult}
                             onPressPlay={onPressPlay}
                             onPressReplay={() => setLastGameResult(null)}
            />
        </div>
    );
}

ReactDOM.render(
    <ThemeProvider theme={theme}>
        <App/>
    </ThemeProvider>,
    document.getElementById('root')
);
