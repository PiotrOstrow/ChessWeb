import React, {useEffect, useState} from "react";
import ChessPosition from "./chess/ChessPosition";
import Game from "./chess/Game";
import Board from "./components/board/Board";
import PuzzleModal from "./components/PuzzleModal";
import Api from "./api/Api";

function PuzzlePage() {
    const [chessPosition, setChessPosition] = useState(ChessPosition.default());
    const [game, setGame] = useState(new Game());
    const [color, setColor] = useState('WHITE');
    const [legalMoves, setLegalMoves] = useState(new Map());
    const [win, setWin] = useState(false);
    const [loss, setLoss] = useState(false);
    const [puzzle, setPuzzle] = useState(null);

    useEffect(() => {
        Api.get('/puzzles/random').then(e => setPuzzle(e.data));
    }, []);

    useEffect(() => {
        if (puzzle != null) {
            const firstMove = puzzle.moves[0];
            game.setFen(puzzle.fen);

            setWin(false);
            setLoss(false);
            setColor(game.getNonActiveColor());
            setLegalMoves(game.getLegalMoves());
            setChessPosition(game.getChessPosition());

            setTimeout(() => move(firstMove.substr(0, 2), firstMove.substr(2, 2)), 1000);
        }
    }, [puzzle]);

    const nextPuzzle = () => {
        Api.get('/puzzles/random').then(e => setPuzzle(e.data));

    }
    const restartPuzzle = () => {
        setPuzzle({...puzzle});

    }

    const move = (from, to) => {
        game.move(from, to);
        setChessPosition(game.getChessPosition());
        setLegalMoves(game.getLegalMoves());
    }

    const ownMove = (from, to) => {
        if (game.getActiveColor() === color) {
            move(from, to);

            if (game.getActiveColor() === color) {
                return;
            }

            let moveHistory = game.getMoveHistory();

            const correctMove = puzzle.moves[moveHistory.length - 1];

            let promotion = moveHistory[moveHistory.length - 1].promotion;
            const actualMove = from + to + (promotion ? promotion : '');

            if (correctMove === actualMove) {
                if (game.getMoveHistory().length === puzzle.moves.length) {
                    setWin(true);
                } else {
                    const nextMove = puzzle.moves[game.getMoveHistory().length];
                    setTimeout(() => move(nextMove.substr(0, 2), nextMove.substr(2, 2)), 500);
                }
            } else {
                setLoss(true);
            }
        }
    }

    return (
        <div className="puzzle-container" style={{display: 'flex', flexDirection: 'row', justifyContent: 'center'}}>
            <Board chessPosition={chessPosition}
                   onStartMove={() => {
                   }}
                   onMove={(from, to) => ownMove(from, to)}
                   flipped={color === 'BLACK'}
                   legalMoves={legalMoves}>
                <PuzzleModal win={win} loss={loss} onClick={() => win ? nextPuzzle() : restartPuzzle()}
                             flipped={color === 'BLACK'}/>
            </Board>
        </div>
    );
}

export default PuzzlePage;