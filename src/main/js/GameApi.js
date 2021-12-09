import SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";

class GameApi {
    constructor(jwtToken, onConnect = () => {}) {
        this.stompClient = Stomp.over(new SockJS('/websocket'));
        this.stompClient.connect({auth: jwtToken}, frame => {
            onConnect();
            this.stompClient.subscribe('/user/topic/private-messages', message => console.log(message.body));
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