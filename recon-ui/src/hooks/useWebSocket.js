import { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

function useWebSocket(topic, onMessage) {

    const [connected, setConnected] = useState(false);

    const clientRef = useRef(null);
    const onMessageRef = useRef(onMessage);

    useEffect(() => {
        onMessageRef.current = onMessage;
    }, [onMessage]);

    useEffect(() => {

        const token = localStorage.getItem("jwtToken");

        const client = new Client({

            webSocketFactory: () =>
                        new SockJS("/ws"),

            // webSocketFactory: () =>
            //     new SockJS("http://localhost:8082/ws"),

            reconnectDelay: 5000,

            connectHeaders: {
                Authorization: `Bearer ${token}`
            },

            onConnect: () => {

                console.log("Connected");

                setConnected(true);

                client.subscribe(topic, message => {

                    const body = JSON.parse(message.body);

                    onMessageRef.current(body);
                });
            },

            onStompError: frame => {
                console.error(frame);
            },

            onWebSocketError: error => {
                console.error(error);
            },

            onWebSocketClose: event => {
                console.log(event);
                setConnected(false);
            }
        });

        client.activate();

        clientRef.current = client;

        return () => {

            if (client.active) {
                client.deactivate();
            }
        };

    }, [topic]);

    return { connected };
}

export default useWebSocket;




// import { useEffect, useRef, useState } from "react";
// import { Client } from "@stomp/stompjs";
// import SockJS from "sockjs-client";

// function useWebSocket(topic, onMessage) {

//     const [connected, setConnected] = useState(false);
//     const clientRef    = useRef(null);
//     const onMessageRef = useRef(onMessage);

//     useEffect(() => {
//         onMessageRef.current = onMessage;
//     }, [onMessage]);

//     // useWebSocket.js
//     useEffect(() => {

//         console.log("useWebSocket: attempting to connect to ws://localhost:8082/ws");

//         const client = new Client({
//             brokerURL: "http://localhost:8082/ws",
//             // ...
//             onConnect: () => {
//                 console.log("useWebSocket: CONNECTED ✅");
//                 setConnected(true);
//                 // ...
//             },
//             onStompError: (frame) => {
//                 console.error("useWebSocket: STOMP ERROR ❌", frame);
//             },
//             onWebSocketError: (event) => {
//                 console.error("useWebSocket: WS ERROR ❌", event);
//             },
//             onWebSocketClose: (event) => {
//                 console.warn("useWebSocket: WS CLOSED", event);
//             }
//         });

//         client.activate();
//         // ...
//     }, [topic]);

//     return { connected };
// }

// export default useWebSocket;