import React, {useState} from "react";
import Square from "./Square";
import Piece from "./Piece";

function Board(props) {
    const [highlight, setHighlight] = useState(null);
    const [draggingFrom, setDraggingFrom] = useState(null);

    const move = (from) => {
        setDraggingFrom(null);
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
            highlightLegalMove={props.legalMoves.has(draggingFrom) && props.legalMoves.get(draggingFrom).has(position)}
            onMouseEnter={pos => setHighlight(pos)}
            onMouseLeave={() => setHighlight(null)}
            lastMove={props.lastMove}
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
                onMouseDown={() => {
                    setDraggingFrom(position);
                    props.onStartMove()
                }}
                flipped={props.flipped}
            />);

    return (
        <div className={'board' + (props.flipped ? ' flipped' : '')} onContextMenu={event => event.preventDefault()}>
            <div className="square-container">
                {squares}
            </div>
            {pieces}
            {props.children}
        </div>
    )
}

export default Board;