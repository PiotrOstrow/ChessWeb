import React from 'react';
import Modal from "./Modal";
import {Button, CircularProgress, Typography} from "@mui/material";

function ChessBoardModal(props) {
    if (props.isPlaying) {
        return null;
    }

    if (props.lastGameResult) {
        let message = props.lastGameResult === 'DISCONNECTED' ? 'Your opponent disconnected' : props.lastGameResult;

        return (
            <Modal>
                <div className={props.flipped ? ' flipped' : ''}>
                    <Typography style={{color: 'white'}} fullwidth align="center">
                        {message}
                    </Typography>
                    <Button fullWidth="true" onClick={() => props.onPressReplay()}>Play again</Button>
                </div>
            </Modal>
        );
    }

    if (props.isInQueue) {
        return (
            <Modal>
                <div className={props.flipped ? ' flipped' : ''}>
                    <CircularProgress sx={{display: 'block', margin: '10px auto'}}/>
                    <Typography component="p" style={{color: 'white'}} align="center">
                        Looking for another player...
                    </Typography>
                </div>
            </Modal>
        );
    } else {
        let style = {display: 'flex', flexDirection: 'column'};
        return (
            <Modal>
                <div className={props.flipped ? ' flipped' : ''} style={style}>
                    <Button fullwidth="true" onClick={() => props.onPressPlay()}>Play against a player</Button>
                    <Button fullwidth="true" disabled>Play against a computer</Button>
                </div>
            </Modal>
        );
    }
}

export default ChessBoardModal;
