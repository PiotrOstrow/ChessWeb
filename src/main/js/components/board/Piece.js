import React from "react";

class Piece extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            x: 0,
            y: 0,
            dragging: false
        }
    }

    onMouseDown(event) {
        if(event.button === 0) { // left click
            document.addEventListener('mousemove', this.onMouseMove);
            document.addEventListener('mouseup', this.onMouseUp);

            this.startingX = event.nativeEvent.pageX - event.nativeEvent.offsetX + this.divElement.clientWidth / 2;
            this.startingY = event.nativeEvent.pageY - event.nativeEvent.offsetY + this.divElement.clientHeight / 2;

            this.offsetX = 100 * (this.props.position.charCodeAt(0) - 'a'.charCodeAt(0));
            this.offsetY = 100 * (8 - Number(this.props.position.charAt(1)));

            const position = this.calculatePosition(event);

            this.setState({
                x: position.x,
                y: position.y,
                dragging: true
            });
        }
    }

    calculatePosition(event) {
        let factor = 100 / this.divElement.clientWidth;
        return {
            x: (event.pageX - this.startingX) * factor + this.offsetX,
            y: (event.pageY - this.startingY) * factor + this.offsetY,
        };
    }

    onMouseUp = event => {
        if(event.button === 0) { // left click
            document.removeEventListener('mousemove', this.onMouseMove);
            document.removeEventListener('mouseup', this.onMouseUp);

            this.setState({
                x: 0,
                y: 0,
                dragging: false
            });
            this.props.onMouseUp();
        }
    }

    onMouseMove = event => {
        this.setState(this.calculatePosition(event));
    }

    render() {
        const style = !this.state.dragging ? {} : {
            zIndex: 1000,
            pointerEvents: 'none',
            transform: 'translate(' + this.state.x + '%, ' + this.state.y + '%)'
        }

        return (
            <div
                style={style}
                className={'draggable div-image piece ' + this.props.piece + ' ' + this.props.position}
                onMouseDown={this.onMouseDown.bind(this)}
                onMouseEnter={() => this.props.onMouseEnter(this.props.position)}
                onMouseLeave={() => this.props.onMouseLeave(this.props.position)}
                ref={ divElement => { this.divElement = divElement } }
            />
        );
    }
}

export default Piece;