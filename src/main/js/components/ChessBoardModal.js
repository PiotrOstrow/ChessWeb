import React from 'react';
import Modal from "./Modal";
import {Button, CircularProgress, Typography} from "@mui/material";

function ChessBoardModal(props) {
    if(props.isPlaying) {
        return null;
    }

    if(props.isInQueue) {
        return (
            <Modal>

                <div>
                    <CircularProgress sx={{display: 'block', margin: '10px auto'}}/>
                    <Typography component="p" style={{color: 'white'}}>
                        Looking for another player...
                    </Typography>
                </div>
            </Modal>
        );
    } else {
        let style = {display: 'flex', flexDirection: 'column'};
        return (
            <Modal>

                <div style={style}>
                    <Button fullwidth onClick={() => props.onPressPlay()}>Play against a player</Button>
                    <Button fullwidth disabled>Play against a computer</Button>
                </div>
            </Modal>
        );
    }
}

export default ChessBoardModal;
