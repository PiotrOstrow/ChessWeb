import React, {useEffect, useState} from "react";
import ChessPosition from "./chess/ChessPosition";
import Game from "./chess/Game";
import Board from "./components/board/Board";
import PuzzleModal from "./components/PuzzleModal";

function PuzzlePage() {
    const [chessPosition, setChessPosition] = useState(ChessPosition.default());
    const [game, setGame] = useState(new Game());
    const [color, setColor] = useState('WHITE');
    const [legalMoves, setLegalMoves] = useState(new Map());
    const [win, setWin] = useState(false);
    const [loss, setLoss] = useState(false);

    const [puzzle, setPuzzle] = useState({
        id: "nRv2B",
        fen: "1R2q2k/6pp/2n2r2/pQ2pp2/3p4/3P2P1/4PP1P/2R3K1 b - - 7 29",
        moves: ["e8b8", "b5b8", "c6b8", "c1c8", "f6f8", "c8f8"],
        rating: "647",
        themes: "backRankMate endgame fork long mate mateIn3"
    });

    const nextPuzzle = () => {
        restartPuzzle();
    }

    const restartPuzzle = () => {
        setWin(false);
        setLoss(false);
        setPuzzle({
            fen: "8/2k5/5p2/3KpP1p/P6P/8/8/8 w - - 1 50",
            moves: ["d5e6", "e5e4", "e6f6", "e4e3", "f6g7", "e3e2", "f5f6", "e2e1q"],
        });
    }

    useEffect(() => {
        const firstMove = puzzle.moves[0];
        game.setFen(puzzle.fen);
        setColor(game.getNonActiveColor());
        setLegalMoves(game.getLegalMoves());
        setChessPosition(game.getChessPosition());

        setTimeout(() => move(firstMove.substr(0, 2), firstMove.substr(2, 2)), 1000);
    }, [puzzle]);

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