import axios from 'axios';
import GameApi from "./GameApi";

let jwtToken = null;
let user = {
    username: null,
    roles: []
}

const authInstance = axios.create();
authInstance.interceptors.response.use(response => {
    jwtToken = response.headers.authorization;

    user.username = response.data.username;
    user.roles = response.data.roles;

    return response;
})

const Api = {
    login(username, password) {
        return authInstance.post('/auth/login/', {
            'username': username,
            'password': password
        })
    },
    register(username, password, email) {
        return authInstance.post('/users/', {
            'username': username,
            'password': password,
            'email': email
        });
    },
    get(url) {
        return axios.get(url, {
            headers: {
                authorization: 'Bearer ' + jwtToken
            }
        });
    },
    post(url, data = {}) {
        return axios.post(url, data, {
            headers: {
                authorization: 'Bearer ' + jwtToken
            }
        });
    },
    gameApi(onConnect) {
        return new GameApi(jwtToken, onConnect);
    },
    getUsername() {
        return user.username;
    }
}

export default Api;
