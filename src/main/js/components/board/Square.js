import React from "react";

function Square(props) {
    let classNames = "unselectable div-image square " + props.position;

    if (props.lastMove != null && (props.lastMove.from === props.position || props.lastMove.to === props.position)) {
        classNames += " last-move-highlight"
    }

    if (props.highlight === props.position) {
        classNames += " highlight";
    }

    return (
        <div
            onMouseEnter={() => props.onMouseEnter(props.position)}
            onMouseLeave={() => props.onMouseLeave(props.position)}
            className={classNames}
        >
            {props.highlightLegalMove && <div className="legal-move"/>}
        </div>
    );
}

export default Square;