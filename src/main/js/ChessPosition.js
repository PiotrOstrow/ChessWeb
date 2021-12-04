import parseFen from './parseFen.js';

class ChessPosition {
    constructor(pieces) {
        this.pieces = pieces;
    }

    getPiece(coordinate) {
        return this.pieces.get(coordinate);
    }

    copy() {
        return new ChessPosition(new Map(this.pieces));
    }

    static fromFen(fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1") {
        return new ChessPosition(parseFen(fen));
    }

    static default() {
        return this.fromFen();
    }
}

export default ChessPosition;