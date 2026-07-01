import axios from "axios";

const apiClient = axios.create({
    baseURL: "/api/v1",
    timeout: 10000,
    headers: {
        "Content-Type": "application/json"
    }
});

// const apiClient = axios.create({
//     baseURL: "http://localhost:8082/api/v1",
//     timeout: 10000,
//     headers: {
//         "Content-Type": "application/json"
//     }
// });

apiClient.interceptors.request.use(
    (config) => {

        const token =
            localStorage.getItem("jwtToken");

        if (token) {
            config.headers.Authorization =
                `Bearer ${token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

// Handle JWT Expiration Automatically . Add response interceptor.

apiClient.interceptors.response.use(

    (response) => response,

    (error) => {

        if (
            error.response &&
            error.response.status === 401
        ) {

            localStorage.removeItem(
                "jwtToken"
            );

            window.location.href =
                "/login";
        }

        return Promise.reject(error);
    }
);

export default apiClient;