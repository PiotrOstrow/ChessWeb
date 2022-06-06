import axios from 'axios';
import GameApi from "./GameApi";
import jwt_decode from 'jwt-decode';

let accessToken = null;
let user = {
    username: null,
    roles: []
}

const authInstance = axios.create();
authInstance.interceptors.response.use(response => {
    accessToken = response.data.accessToken;

    user.username = response.data.username;
    user.roles = response.data.roles;

    return response;
})

const withRefreshInterceptor = axios.create();
withRefreshInterceptor.interceptors.response.use(response => response, async error => {
    const status = error?.response?.status;
    if (status === 401) {
        const refreshResult = await Api.refreshAccessToken();

        if (refreshResult) {
            error.config.headers['authorization'] = 'Bearer ' + accessToken;
            error.config.baseURL = undefined;

            return axios.request(error.config);
        }
    }

    return Promise.reject(error);
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
    async refreshAccessToken() {
        const result = await axios.post('/auth/refresh/', {withCredentials: true})
            .then(response => {
                let payload = jwt_decode(response.data.accessToken);
                user.username = payload.sub;
                user.roles = payload.roles.split(', ');
                accessToken = response.data.accessToken;
            });
        return result.status === 200;
    },
    get(url) {
        return withRefreshInterceptor.get(url, {
            headers: {
                authorization: 'Bearer ' + accessToken
            }
        });
    },
    post(url, data = {}) {
        return withRefreshInterceptor.post(url, data, {
            headers: {
                authorization: 'Bearer ' + accessToken
            }
        });
    },
    gameApi(onConnect) {
        return new GameApi(accessToken, onConnect);
    },
    getUsername() {
        return user.username;
    }
}

export default Api;
