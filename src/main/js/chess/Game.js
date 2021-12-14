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

    move(from, to) {
        this.chess.move(from + to, {sloppy: true});
    }

    getLegalMoves() {
        this.chess.moves({verbose: true});
    }

    reset() {
        this.chess.reset();
    }
}

export default Game;
