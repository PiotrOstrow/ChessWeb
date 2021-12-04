import React from 'react';
import ReactDOM from 'react-dom';
import ChessPosition from './ChessPosition.js';

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

    onMouseUp = () => {
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

    onMouseMove = (event) => {
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

class Square extends React.Component {
    constructor(props) {
        super(props);
        this.state = { highlight: false }
    }

    render() {
        const imageName = this.props.position === 'd4' ? 'd6' : this.props.position; // missing d4 image
        const imagePath = "./Chess_Artwork/Chess_Board/Stone_Grey/" + imageName + ".png";

        const style = {
            backgroundImage: "url('" + imagePath + "')"
        };

        return (
            <div
                onMouseEnter={() => this.props.onMouseEnter(this.props.position)}
                onMouseLeave={() => this.props.onMouseLeave(this.props.position)}
                key={this.props.position}
                style={style}
                className={"unselectable div-image square" + (this.props.highlight === this.props.position ? " highlight" : "")}
            />
        );
    }
}

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


ReactDOM.render(
    <Board/>,
    document.getElementById('root')
);
