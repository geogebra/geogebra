package geogebraVoiceCommand.dialog.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;


public class WebSocketConnection {


    private StompFrameHandler stompFrameHandler;

    public void setStompFrameHandler(StompFrameHandler stompFrameHandler) {
        this.stompFrameHandler = stompFrameHandler;
    }

    public WebSocketConnection() {
        stompFrameHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.err.println(payload.toString());
            }
        };
    }

    private class MyStompSessionHandler extends StompSessionHandlerAdapter {
        private String userId;
        private StompFrameHandler stompFrameHandler;
        public MyStompSessionHandler(String userId, StompFrameHandler stompFrameHandler) {
            this.userId = userId;
            this.stompFrameHandler = stompFrameHandler;
        }


        private void subscribeTopic(String topic, StompSession session) {
            StompSession.Subscription subs = session.subscribe(topic, stompFrameHandler);
//            System.out.println(subs);
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            subscribeTopic("/queue/chat", session);
            subscribeTopic(String.format("/user/%s/queue/chat", userId), session);

        }


    }

    public String getId() throws IOException {

        String url = "http://darkyver.fun/users";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString().split(" ")[1];
    }

    public boolean connectWs(String userId) {
        try {
            WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
            List<Transport> transports = new ArrayList<>(1);
            transports.add(new WebSocketTransport(simpleWebSocketClient));

            SockJsClient sockJsClient = new SockJsClient(transports);
            WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            String url = "ws://darkyver.fun:8080/ws";
            StompSessionHandler sessionHandler = new MyStompSessionHandler(userId, stompFrameHandler);
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            headers.add("user_id", userId);

            StompSession session = stompClient.connect(url, headers, sessionHandler).get();
            return true;
        }  catch (Exception e) {
            return false;
        }
    }
/*
    public static void main(String args[]) throws Exception {
        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(simpleWebSocketClient));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

//        String url = "ws://localhost:8080/secured/room";

//        String userId = "spring-" + ThreadLocalRandom.current().nextInt(1, 99);
//        String userId = makeGetRequest("http://localhost:8080/users").split(" ")[1];
        String userId = makeGetRequest("http://darkyver.fun/users").split(" ")[1];
//        String url = "ws://localhost:8080/ws";
        String url = "ws://darkyver.fun:8080/ws";
        StompSessionHandler sessionHandler = new MyStompSessionHandler(userId);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("user_id", userId);

        StompSession session = stompClient.connect(url, headers, sessionHandler).get();
//        System.out.println(session.getSessionId());
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (; ; ) {
            System.out.print(userId + " >> ");
            System.out.flush();
            String line = in.readLine();
            if (line == null) break;
            if (line.length() == 0) continue;
            HelloMessage mess = new HelloMessage(line);
            session.send(userId, mess);
        }
    }




    private static String makeGetRequest(String url) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
*/

}
