import SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";
import Game from "../chess/Game";

class GameApi {
    constructor(jwtToken, onConnect = () => {}) {
        this.stompClient = Stomp.over(new SockJS('/websocket'));

        this.inQueue = false;
        this.playing = false;
        this.color = 'WHITE';
        this.lastMove = 'WHITE';
        this.opponentsName = '';
        this.lastGameResult = null;
        this.game = new Game();

        this.startTime = Date.now();
        this.maxTime = 180000;
        this.whiteTime = 180000;
        this.blackTime = 180000;

        this.onRecvStart = () => {
        };
        this.onRecvMove = () => {
        };
        this.onRecvGameOver = () => {
        };

        this.stompClient.connect({auth: jwtToken}, frame => {
            onConnect();
            this.stompClient.subscribe('/user/topic/game-start', message => {
                const data = JSON.parse(message.body);

                this.playing = true;
                this.inQueue = false;
                this.color = data.color;
                this.lastMove = 'WHITE';
                this.opponentsName = data.opponent;
                this.game.reset();

                this.startTime = Date.now()
                this.maxTime = data.time * 1000;
                this.whiteTime = data.time * 1000;
                this.blackTime = data.time * 1000;

                this.onRecvStart(data);
            });
            this.stompClient.subscribe('/user/topic/game-move', message => {
                const data = JSON.parse(message.body);

                this.whiteTime = data.whiteTime;
                this.blackTime = data.blackTime;

                // TODO should not call when receiving own move
                this.game.move(data.move.from, data.move.to);

                this.onRecvMove(data);
            });
            this.stompClient.subscribe('/user/topic/game-over', message => {
                let data = JSON.parse(message.body);

                this.playing = false;
                this.lastGameResult = data.gameResult;

                this.onRecvGameOver(data);
            })
        });
    }

    play() {
        this.inQueue = true;
        this.send('/ws/play');
    }

    move(from, to) {
        this.game.move(from, to);
        this.send('/ws/move', {'from': from, 'to': to});
    }

    send(destination, message = {}) {
        this.stompClient.send(destination, {}, JSON.stringify(message));
    }

    isInQueue() {
        return this.inQueue;
    }

    isPlaying() {
        return this.playing;
    }

    getColor() {
        return this.color;
    }

    getActiveColor() {
        return this.game.getActiveColor();
    }

    getChessPosition() {
        return this.game.getChessPosition();
    }

    getPreviousPosition(i) {
        return this.game.getPreviousPosition(i);
    }

    getLegalMoves() {
        return this.game.getLegalMoves();
    }

    getMoveHistory() {
        return this.game.getMoveHistory();
    }

    getOpponentsName() {
        return this.opponentsName;
    }

    getMaxTime() {
        return this.maxTime;
    }

    getWhiteTime() {
        if (!this.isPlaying() || this.getActiveColor() !== 'WHITE') {
            return this.whiteTime;
        }

        const timeElapsedSinceStartOfGame = Date.now() - this.startTime;

        const blackElapsed = this.maxTime - this.blackTime;

        return this.maxTime - timeElapsedSinceStartOfGame + blackElapsed;
    }

    getBlackTime() {
        if (!this.isPlaying() || this.getActiveColor() !== 'BLACK') {
            return this.blackTime;
        }

        const timeElapsedSinceStartOfGame = Date.now() - this.startTime;

        const whiteElapsed = this.maxTime - this.whiteTime;

        return this.maxTime - timeElapsedSinceStartOfGame + whiteElapsed;
    }

    getLastGameResult() {
        return this.lastGameResult;
    }
}

export default GameApi;
