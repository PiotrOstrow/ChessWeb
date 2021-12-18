import React from "react";

function Square(props) {
    return (
        <div
            onMouseEnter={() => props.onMouseEnter(props.position)}
            onMouseLeave={() => props.onMouseLeave(props.position)}
            className={"unselectable div-image square " + props.position + (props.highlight === props.position ? " highlight" : "")}
        >
            {props.highlightLegalMove && <div className="legal-move"/>}
        </div>
    );
}

export default Square;