import {Typography} from "@mui/material";
import React from "react";
import MoveButton from "./MoveButton";
import Timer from "./Timer";

function SidePanel(props) {
    const style = {
        color: 'white',
        margin: '0 25px',
        border: 'solid 1px white',
        display: 'flex',
        flexDirection: 'column',
        padding: '10px',
        position: 'relative'
    }

    const moves = props.moveHistory.map(e => e.san);
    const moveList = [];

    for (let i = 0; i < moves.length; i += 2) {
        moveList.push(
            <div style={{display: 'flex', width: '100%'}} key={i}>
                <Typography style={{color: 'white', padding: '5px 10px', width: '30px'}}>{i / 2 + 1}.</Typography>
                <div style={{margin: '0 auto'}}>
                    <MoveButton label={moves[i]} onClick={() => props.onShowMove(i)}/>
                    <MoveButton label={moves[i + 1]} onClick={() => props.onShowMove(i + 1)}/>
                </div>
            </div>
        );
    }

    return (
        <div style={style} className="side-panel">
            <div style={{display: 'flex'}}>
                <Typography component="h2" style={{color: 'white', padding: '25px', flexGrow: 2}} align="center">
                    {props.opponentName}
                </Typography>
                <Timer time={props.playingAs === 'WHITE' ? props.blackTime : props.whiteTime} maxTime={props.maxTime}/>
            </div>
            <div style={{overflowY: 'auto', flexGrow: 8}}>
                {moveList}
            </div>
            <div style={{display: 'flex'}}>
                <Typography component="h2" style={{color: 'white', padding: '25px', flexGrow: 2}} align="center">
                    {props.playerName}
                </Typography>
                <Timer time={props.playingAs === 'WHITE' ? props.whiteTime : props.blackTime} maxTime={props.maxTime}/>
            </div>
        </div>
    );
}

export default SidePanel;