import React from "react";

class Square extends React.Component {
    render() {
        return (
            <div
                onMouseEnter={() => this.props.onMouseEnter(this.props.position)}
                onMouseLeave={() => this.props.onMouseLeave(this.props.position)}
                className={"unselectable div-image square " + this.props.position + (this.props.highlight === this.props.position ? " highlight" : "")}
            />
        );
    }
}

export default Square;