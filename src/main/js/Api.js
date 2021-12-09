import axios from 'axios';
import GameApi from "./GameApi";

let jwtToken = null;

const authInstance = axios.create();
authInstance.interceptors.response.use(response => {
    jwtToken = response.headers.authorization;
    return response;
})

const Api = {
    login(username, password) {
        return authInstance.post('/auth/login/', {
            'username': username,
            'password': password
        })
    },
    get(url) {
        return axios.get(url, {
            headers: {
                authorization: 'Bearer ' + jwtToken
            }
        });
    },
    gameApi(onConnect) {
        return new GameApi(jwtToken, onConnect);
    }
}

export default Api;
