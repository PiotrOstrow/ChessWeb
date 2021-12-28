import React from "react";
import CircularProgress from '@mui/material/CircularProgress';
import {Box, Typography} from "@mui/material";

function CircularProgressWithLabel(props) {
    return (
        <Box sx={{position: 'relative', display: 'inline-flex', top: '50%', transform: 'translateY(-50%)'}}>
            <CircularProgress variant="determinate" {...props} />
            <Box
                sx={{
                    top: 0,
                    left: 0,
                    bottom: 0,
                    right: 0,
                    position: 'absolute',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                }}
            >
                <Typography variant="caption" component="div" color="text.secondary">
                    {props.label}
                </Typography>
            </Box>
        </Box>
    );
}

function Timer(props) {
    const value = props.time / props.maxTime * 100;

    const minutes = Math.floor(props.time / (60 * 1000));
    const seconds = Math.floor((props.time / 1000) % 60);
    const label = minutes + ':' + String(seconds).padStart(2, '0');

    return (
        <div style={{position: 'relative'}}>
            <CircularProgressWithLabel value={value} label={label}/>
        </div>
    );
}

export default Timer;
