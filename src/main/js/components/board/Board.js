import React, {useState} from "react";
import Square from "./Square";
import Piece from "./Piece";

function Board(props) {
    const [highlight, setHighlight] = useState(null);

    const move = (from) => {
        if(highlight !== null && from !== highlight) {
            props.onMove(from, highlight);
        }
    }

    const squares = Array.from({length: 64})
        .map((_, i) => String.fromCharCode('a'.charCodeAt(0) + (i % 8)) + (8 - Math.floor(i / 8)))
        .map((position, i) => <Square
            key={i}
            position={position}
            highlight={highlight}
            onMouseEnter={pos => setHighlight(pos)}
            onMouseLeave={() => setHighlight(null)}
        />)

    const pieces = Array.from(props.chessPosition.pieces.entries())
        .filter(([, value]) => value !== null)
        .map(([position, piece]) =>
            <Piece
                key={position}
                position={position}
                piece={piece}
                onMouseEnter={pos => setHighlight(pos)}
                onMouseLeave={() => setHighlight(null)}
                onMouseUp={() => move(position)}
                flipped={props.flipped}
            />);

    return (
        <div className={'board' + (props.flipped ? ' flipped' : '')} onContextMenu={event => event.preventDefault()}>
            <div className="square-container">
                {squares}
            </div>
            {pieces}
        </div>
    )
}

export default Board;