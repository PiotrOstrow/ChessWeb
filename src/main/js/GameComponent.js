import React, {useEffect, useState} from "react";
import Api from "./api/Api";
import Board from "./components/board/Board";
import GameModal from "./components/GameModal";
import SidePanel from "./components/SidePanel";
import useInterval from "./hooks/useInterval";

function GameComponent(props) {
    const gameApi = props.gameApi;

    const [chessPosition, setChessPosition] = useState(gameApi.getChessPosition());
    const [isPlaying, setIsPlaying] = useState(gameApi.isPlaying());
    const [isInQue, setIsInQue] = useState(gameApi.isInQueue());
    const [lastGameResult, setLastGameResult] = useState(gameApi.getLastGameResult());
    const [color, setColor] = useState(gameApi.getColor());
    const [legalMoves, setLegalMoves] = useState(gameApi.getLegalMoves());
    const [moveHistory, setMoveHistory] = useState(gameApi.getMoveHistory());
    const [lastMove, setLastMove] = useState(gameApi.getLastMove());
    const [opponentName, setOpponentName] = useState(gameApi.getOpponentsName());
    const [maxTime, setMaxTime] = useState(gameApi.getMaxTime());
    const [whiteTime, setWhiteTime] = useState(gameApi.getWhiteTime());
    const [blackTime, setBlackTime] = useState(gameApi.getBlackTime());

    useEffect(() => {
        gameApi.onRecvMove = data => onRecvMove(data);
        gameApi.onRecvStart = data => {
            setIsPlaying(true);
            setIsInQue(false)
            setColor(data.color);
            setChessPosition(gameApi.getChessPosition());
            setOpponentName(data.opponent);
            setMaxTime(data.time * 1000);
            setWhiteTime(data.time * 1000);
            setBlackTime(data.time * 1000);
            setMoveHistory([]);
        };
        gameApi.onRecvGameOver = data => onGameOver(data);

        return () => {
            gameApi.onRecvMove = () => {
            };
            gameApi.onRecvStart = () => {
            };
            gameApi.onRecvGameOver = () => {
            };
        }
    }, []);

    useEffect(() => {
        setLegalMoves(gameApi.getLegalMoves());
    }, [chessPosition]);


    useInterval(() => {
        setWhiteTime(Math.max(0, gameApi.getWhiteTime()));
        setBlackTime(Math.max(0, gameApi.getBlackTime()));
    }, isPlaying ? 100 : null);

    const onGameOver = data => {
        setLastGameResult(data.gameResult);
        setIsPlaying(false);
        setIsInQue(false);
    }

    const onRecvMove = data => {
        setChessPosition(gameApi.getChessPosition());
        setMoveHistory(gameApi.getMoveHistory());
        setLastMove(gameApi.getLastMove());
        setWhiteTime(data.whiteTime);
        setBlackTime(data.blackTime);
    }

    const onOwnMove = (from, to) => {
        if (gameApi.getActiveColor() === color) {
            gameApi.move(from, to);
            setChessPosition(gameApi.getChessPosition());
            setMoveHistory(gameApi.getMoveHistory());
            setLastMove(gameApi.getLastMove());
        }
    }

    const onPressPlay = () => {
        setIsInQue(true);
        gameApi.play();
    }

    const showMove = i => {
        if (i === moveHistory.length - 1) {
            setChessPosition(gameApi.getChessPosition());
            setLastMove(gameApi.getLastMove());
        } else {
            setChessPosition(gameApi.getPreviousPosition(i + 1));
            setLastMove(moveHistory[i]);
        }
    }

    return (
        <div className="game-container">
            <div style={{display: 'flex', flexDirection: 'horizontal', margin: '25px auto', justifyContent: 'center'}}>
                <Board chessPosition={chessPosition}
                       onStartMove={() => showMove(moveHistory.length - 1)}
                       onMove={(from, to) => onOwnMove(from, to)}
                       flipped={color === 'BLACK'}
                       legalMoves={legalMoves}
                       lastMove={lastMove}
                >
                    <GameModal isPlaying={isPlaying}
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

export default GameComponent;
