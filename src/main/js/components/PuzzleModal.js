import React, {useEffect, useState} from 'react';
import Modal from "./Modal";
import {Button, Typography} from "@mui/material";
import useInterval from "../hooks/useInterval";

function easeInOutSine(x) {
    return -(Math.cos(Math.PI * x) - 1) / 2;
}

function PuzzleModal(props) {
    const [rating, setRating] = useState(0);
    const [delta, setDelta] = useState(0);
    const [tick, setTick] = useState(1);

    useEffect(() => {
        setRating(props.rating);
        setDelta(props.ratingDelta);
    }, [props.win])


    useInterval(() => {
        let newDelta = delta * easeInOutSine(tick);

        setTick(tick - 0.0008);
        setDelta(newDelta);
        setRating(rating + (delta - newDelta));
    }, Math.round(delta) !== 0 ? 10 : null);

    return (
        <Modal>
            <div className={props.flipped ? ' flipped' : ''}>
                <Typography component="h3" variant="h3" style={{color: props.win ? '#388e3c' : '#d32f2f'}}
                            align="center">
                    {props.win ? 'Correct!' : 'Incorrect!'}
                </Typography>
                <Typography component="h4" variant="h4" align="center">
                    {Math.round(rating)} ({delta >= 0 ? '+' : ''}{Math.round(props.ratingDelta)})
                </Typography>
                <Button onClick={props.onClick} style={{margin: '10px auto', display: 'block'}}>
                    Next puzzle
                </Button>
            </div>
        </Modal>
    );
}

export default PuzzleModal;
