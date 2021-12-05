import React from "react";
import ChessPosition from "../../ChessPosition";
import Square from "./Square";
import Piece from "./Piece";

class Board extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            chessPosition: ChessPosition.default(),
            highlight: null
        }
    }

    move(from) {
        if(this.state.highlight !== null && from !== this.state.highlight) {
            const newPosition = this.state.chessPosition.copy();
            newPosition.pieces.set(this.state.highlight, newPosition.getPiece(from));
            newPosition.pieces.set(from, null);

            this.setState({chessPosition: newPosition});
        }
    }

    render() {
        const squares = Array.from({length: 64})
            .map((_, i) => String.fromCharCode('a'.charCodeAt(0) + (i % 8)) + (8 - Math.floor(i / 8)))
            .map((position, i) => <Square
                key={i}
                position={position}
                highlight={this.state.highlight}
                onMouseEnter={pos => this.setState({highlight: pos})}
                onMouseLeave={() => this.setState({highlight: null})}
            />)

        const pieces = Array.from(this.state.chessPosition.pieces.entries())
            .filter(([, value]) => value !== null)
            .map(([position, piece]) =>
                <Piece
                    key={position}
                    position={position}
                    piece={piece}
                    onMouseEnter={pos => this.setState({highlight: pos})}
                    onMouseLeave={() => this.setState({highlight: null})}
                    onMouseUp={() => this.move(position)}
                />);

        return (
            <div className="board" onContextMenu={event => event.preventDefault()}>
                <div className="square-container">
                    {squares}
                </div>
                {pieces}
            </div>
        )
    }
}

export default Board;