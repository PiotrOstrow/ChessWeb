import {Button} from "@mui/material";
import React from "react";

function MoveButton(props) {
    const style = {
        margin: '0 10px',
        textTransform: 'none'
    }

    if (props.label == null)
        style.visibility = 'hidden';

    return (
        <Button size="small" variant="outlined" style={style} onClick={() => props.onClick()}>
            {props.label}
        </Button>
    );
}

export default MoveButton;
