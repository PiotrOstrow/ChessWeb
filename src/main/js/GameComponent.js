import React, {useEffect, useRef, useState} from "react";
import Game from "./chess/Game";
import ChessPosition from "./chess/ChessPosition";
import Api from "./api/Api";
import Board from "./components/board/Board";
import ChessBoardModal from "./components/ChessBoardModal";
import SidePanel from "./components/SidePanel";

function GameComponent() {
    const [gameApi, setGameApi] = useState(null);
    const [game, setGame] = useState(new Game());
    const [chessPosition, setChessPosition] = useState(ChessPosition.default());
    const [isPlaying, setIsPlaying] = useState(false);
    const [isInQue, setIsInQue] = useState(false);
    const [lastGameResult, setLastGameResult] = useState(null);
    const [color, setColor] = useState('WHITE');
    const [legalMoves, setLegalMoves] = useState(new Map());
    const [moveHistory, setMoveHistory] = useState([]);
    const [opponentName, setOpponentName] = useState(null);
    const [maxTime, setMaxTime] = useState(180 * 1000);
    const [whiteTime, setWhiteTime] = useState(180 * 1000);
    const [blackTime, setBlackTime] = useState(180 * 1000);

    useEffect(() => {
        const gameApi = Api.gameApi();
        gameApi.onRecvMove = data => onRecvMove(data);
        gameApi.onRecvStart = data => {
            setIsPlaying(true);
            setIsInQue(false)
            setColor(data.color);
            setChessPosition(ChessPosition.default());
            setOpponentName(data.opponent);
            setMaxTime(data.time * 1000);
            setWhiteTime(data.time * 1000);
            setBlackTime(data.time * 1000);
            setMoveHistory([]);
            game.reset();
        };
        gameApi.onRecvGameOver = data => onGameOver(data);
        setGameApi(gameApi);
    }, []);

    useEffect(() => {
        setLegalMoves(game.getLegalMoves());
    }, [chessPosition]);


    useInterval(() => {
        if (moveHistory.length % 2 === 0) {
            setWhiteTime(Math.max(0, whiteTime - 100));
        } else {
            setBlackTime(Math.max(0, blackTime - 100));
        }
    }, isPlaying ? 100 : null);

    const onGameOver = data => {
        setLastGameResult(data.gameResult);
        setIsPlaying(false);
        setIsInQue(false);
    }

    const onRecvMove = data => {
        // if last move was our own, don't update the game state
        const lastMove = moveHistory[moveHistory.length - 1];
        if (lastMove == null || lastMove.color.toUpperCase() !== color.charAt(0)) {
            move(data.move.from, data.move.to);
        }

        setWhiteTime(data.whiteTime);
        setBlackTime(data.blackTime);
    }

    const move = (from, to) => {
        game.move(from, to);
        setChessPosition(game.getChessPosition());
        setMoveHistory(game.getMoveHistory());
    }

    const onOwnMove = (from, to) => {
        if (game.getActiveColor() === color) {
            move(from, to);
            gameApi.move(from, to);
        }
    }

    const onPressPlay = () => {
        setIsInQue(true);
        gameApi.play();
    }

    const showMove = i => {
        if (i === moveHistory.length - 1) {
            setChessPosition(game.getChessPosition());
        } else {
            setChessPosition(game.getPreviousPosition(i + 1));
        }
    }

    return (
        <div className="game-container">
            <div style={{display: 'flex', flexDirection: 'horizontal', margin: '25px auto', justifyContent: 'center'}}>
                <Board chessPosition={chessPosition}
                       onStartMove={() => showMove(moveHistory.length - 1)}
                       onMove={(from, to) => onOwnMove(from, to)}
                       flipped={color === 'BLACK'}
                       legalMoves={legalMoves}>
                    <ChessBoardModal isPlaying={isPlaying}
                                     isInQueue={isInQue}
                                     lastGameResult={lastGameResult}
                                     onPressPlay={onPressPlay}
                                     onPressReplay={() => setLastGameResult(null)}
                                     flipped={color === 'BLACK'}
                    />
                </Board>
                {(isPlaying || opponentName != null) && <SidePanel
                    moveHistory={moveHistory}
                    onShowMove={i => showMove(i)}
                    playerName={Api.getUsername()}
                    opponentName={opponentName}
                    whiteTime={whiteTime}
                    blackTime={blackTime}
                    playingAs={color}
                    maxTime={maxTime}
                />}
            </div>

        </div>
    );
}

function useInterval(callback, interval) {
    const savedCallback = useRef();

    useEffect(() => {
        savedCallback.current = callback;
    }, [callback]);

    useEffect(() => {
        if (interval !== null) {
            let id = setInterval(() => savedCallback.current(), interval);
            return () => clearInterval(id);
        }
    }, [interval]);
}

export default GameComponent;
