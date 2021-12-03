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
        document.addEventListener('mousemove', this.onMouseMove);
        document.addEventListener('mouseup', this.onMouseUp);

        this.startingX = event.nativeEvent.pageX - event.nativeEvent.offsetX + this.divElement.clientWidth / 2;
        this.startingY = event.nativeEvent.pageY - event.nativeEvent.offsetY + this.divElement.clientHeight / 2;

        this.setState({
            x: event.pageX - this.startingX,
            y: event.pageY - this.startingY,
            dragging: true
        });
    }

    onMouseUp = () => {
        document.removeEventListener('mousemove', this.onMouseMove);
        document.removeEventListener('mouseup', this.onMouseUp);

        this.setState({
            x: 0,
            y: 0,
            dragging: false
        })
    }

    onMouseMove = (event) => {
        this.setState({
            x: event.pageX - this.startingX,
            y: event.pageY - this.startingY
        });
    }

    render() {
        const style = {
            zIndex: this.state.dragging ? 1000 : 0,
            pointerEvents: this.state.dragging ? 'none' : 'inherit',
            transform: 'translate(' + this.state.x + 'px, ' + this.state.y + 'px)'
        }

        return (
            <div
                style={style}
                className={'draggable div-image piece ' + this.props.piece}
                onMouseDown={this.onMouseDown.bind(this)}
                ref={ divElement => { this.divElement = divElement } }
            />
        );
    }
}

class Square extends React.Component {
    render() {
        const coordinate = String.fromCharCode('a'.charCodeAt(0) + this.props.x) + (8 - this.props.y);
        const imageName = coordinate === 'd4' ? 'd6' : coordinate; // missing d4 image
        const imagePath = "./Chess_Artwork/Chess_Board/Stone_Grey/" + imageName + ".png";

        const style = {
            backgroundImage: "url('" + imagePath + "')"
        };

        const chessPiece = this.props.position.getPiece(coordinate);

        return (
            <div
                onMouseEnter={() => console.log(coordinate)}
                key={coordinate}
                style={style}
                className="unselectable div-image square"
            >
                {chessPiece !== null && <Piece piece={chessPiece} />}
            </div>
        );
    }
}

class Board extends React.Component {
    render() {
        const chessPosition = new ChessPosition();

        const squares = Array.from({length: 64})
            .map((_, i) => <Square key={i} x={i % 8} y={Math.floor(i / 8)} position={chessPosition}/>)

        return (
            <div className="board">
                {squares}
            </div>
        )
    }
}

ReactDOM.render(
    <Board/>,
    document.getElementById('root')
);
