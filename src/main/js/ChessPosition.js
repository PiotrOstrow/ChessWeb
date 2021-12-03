

class ChessPosition {
    constructor(fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1") {
        this.fen = fen;
    }

    getPiece(coordinate) {
        return Math.random() > 0.5 ? 'black-rook' : null;
    }
}

export default ChessPosition;