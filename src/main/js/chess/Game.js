import * as ChessJS from "chess.js";
import ChessPosition from "./ChessPosition";

const Chess = typeof ChessJS === "function" ? ChessJS : ChessJS.Chess;

function getPieceName(type) {
    switch (type) {
        case 'k':
            return 'king';
        case 'q':
            return 'queen';
        case 'r':
            return 'rook';
        case 'n':
            return 'knight';
        case 'b':
            return 'bishop';
        case 'p':
            return 'pawn';
    }
}

class Game {
    constructor() {
        this.chess = new Chess();
        this.positionHistory = [this.getChessPosition()];
    }

    getChessPosition() {
        const pieces = new Map();
        this.chess.board()
            .map((rank, rankIndex) => rank.map((piece, fileIndex) => {
                return {'piece': piece, 'rank': rankIndex, 'file': fileIndex}
            }))
            .flatMap(e => e)
            .filter(e => e.piece != null)
            .forEach(e => {
                const rank = 8 - e.rank;
                const file = String.fromCharCode('a'.charCodeAt(0) + e.file);
                const color = e.piece.color === 'w' ? 'white' : 'black'
                const piece = getPieceName(e.piece.type);

                pieces.set(file + rank, color + '-' + piece);
            });

        return new ChessPosition(pieces);
    }

    getPreviousPosition(i) {
        return this.positionHistory[i];
    }

    getActiveColor() {
        return this.chess.turn() === 'w' ? 'WHITE' : 'BLACK';
    }

    getNonActiveColor() {
        return this.chess.turn() === 'w' ? 'BLACK' : 'WHITE';
    }

    move(from, to) {
        if (this.isMoveLegal(from, to)) {
            this.chess.move(from + to, {sloppy: true});
            this.positionHistory.push(this.getChessPosition())
        }
    }

    isMoveLegal(from, to) {
        return this.getLegalMoves().has(from) && this.getLegalMoves().get(from).has(to);
    }

    getLegalMoves() {
        const map = new Map();
        this.chess.moves({verbose: true})
            .forEach(e => {
                if (!map.has(e.from)) {
                    map.set(e.from, new Set());
                }

                const value = map.get(e.from);
                value.add(e.to);
            });
        return map;
    }

    reset() {
        this.chess.reset();
    }

    getMoveHistory() {
        return this.chess.history({verbose: true});
    }

    setFen(fen) {
        this.chess = new Chess(fen);
    }

    getLastMove() {
        const moveHistory = this.getMoveHistory();
        if (moveHistory.length === 0) {
            return null;
        }
        return moveHistory[moveHistory.length - 1];
    }
}

export default Game;
