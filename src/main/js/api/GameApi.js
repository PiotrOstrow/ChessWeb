import SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";

class GameApi {
    constructor(jwtToken, onConnect = () => {}) {
        this.stompClient = Stomp.over(new SockJS('/websocket'));

        this.onRecvStart = () => {};
        this.onRecvMove = () => {
        };
        this.onRecvGameOver = () => {
        };

        this.stompClient.connect({auth: jwtToken}, frame => {
            onConnect();
            this.stompClient.subscribe('/user/topic/game-start', message => this.onRecvStart(JSON.parse(message.body)));
            this.stompClient.subscribe('/user/topic/game-move', message => this.onRecvMove(JSON.parse(message.body)));
            this.stompClient.subscribe('/user/topic/game-over', message => this.onRecvGameOver(JSON.parse(message.body)))
        });
    }

    play() {
        this.send('/ws/play');
    }

    move(from, to) {
        this.send('/ws/move', {'from': from, 'to': to});
    }

    send(destination, message = {}) {
        this.stompClient.send(destination, {}, JSON.stringify(message));
    }
}

export default GameApi;
