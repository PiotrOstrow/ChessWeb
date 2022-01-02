import React from 'react';
import Modal from "./Modal";
import {Button, Typography} from "@mui/material";

function PuzzleModal(props) {
    if (!props.win && !props.loss) {
        return null;
    }

    return (
        <Modal>
            <div className={props.flipped ? ' flipped' : ''}>
                <Typography component="p" style={{color: 'white'}} align="center">
                    {props.win ? 'Correct!' : 'Incorrect!'}
                </Typography>
                <Button onClick={props.onClick}>
                    {props.win ? 'Next puzzle' : 'Try again'}
                </Button>
            </div>
        </Modal>
    );
}

export default PuzzleModal;
