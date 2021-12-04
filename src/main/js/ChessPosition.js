import parseFen from './parseFen.js';

class ChessPosition {
    constructor(fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1") {
        this.fen = fen;
        this.pieces = parseFen(fen);
    }

    getPiece(coordinate) {
        return this.pieces.get(coordinate);
    }
}

export default ChessPosition;