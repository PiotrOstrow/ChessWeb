import React, {useEffect, useState} from "react";
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

    useEffect(() => {
        const gameApi = Api.gameApi();
        gameApi.onRecvMove = data => onMove(data.from, data.to);
        gameApi.onRecvStart = data => {
            setIsPlaying(true);
            setIsInQue(false)
            setColor(data.color);
            setChessPosition(ChessPosition.default());
            setOpponentName(data.opponent);
            game.reset();
        };
        gameApi.onRecvGameOver = data => onGameOver(data);
        setGameApi(gameApi);
    }, []);

    useEffect(() => {
        setLegalMoves(game.getLegalMoves());
    }, [chessPosition]);

    const onGameOver = data => {
        setLastGameResult(data.gameResult);
        setIsPlaying(false);
        setIsInQue(false);
    }

    const onMove = (from, to) => {
        game.move(from, to);
        setChessPosition(game.getChessPosition());
        setMoveHistory(game.getMoveHistory());
    }

    const onOwnMove = (from, to) => {
        if (game.getActiveColor() === color) {
            onMove(from, to);
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
                />}
            </div>

        </div>
    );
}

export default GameComponent;
