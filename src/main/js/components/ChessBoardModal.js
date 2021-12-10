import React from 'react';
import Modal from "./Modal";

function ChessBoardModal(props) {
    if(props.isPlaying) {
        return null;
    }

    if(props.isInQueue) {
        return (
            <Modal>
                <p style={{color: 'black'}}>Looking for another player...</p>
            </Modal>
        );
    } else {
        return (
            <Modal>
                <button onClick={() => props.onPressPlay()}>Play against a player</button>
                <button disabled>Play against a computer</button>
            </Modal>
        );
    }
}

export default ChessBoardModal;
