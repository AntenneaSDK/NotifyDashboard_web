package org.antennae.server.notifier.ws;

import org.antennae.common.messages.ClientMessage;
import org.antennae.common.messages.ClientMessageWrapper;
import org.antennae.common.messages.ServerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.inject.Inject;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <code>ClientTextWebSocketHandler</code> handles the incoming web-socket messages.
 *
 * reads the json, identifies the <code>topic</code> and then sends it to the <code>TopicProcessor</code>
 */
public class ClientTextWebSocketHandler extends org.springframework.web.socket.handler.TextWebSocketHandler implements IClientHandler {

    private static Logger logger = LoggerFactory.getLogger(ClientTextWebSocketHandler.class);
    private Map<String,WebSocketSession> clientSessions = new ConcurrentHashMap<String,WebSocketSession>();

    @Inject
    ServerTextWebSocketHandler serverHandler;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Client connection established : " + session.getId());
        clientSessions.put( session.getId(), session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.info("connection error");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Client connection closed: " + session.getId() + ", status :" + status.toString());
        if( clientSessions.get(session.getId()) != null ){
            clientSessions.remove(session.getId());
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage incomingMessage) {

        if (incomingMessage == null) {
            return;
        }

        String textmessage = incomingMessage.getPayload();

        logger.info("SessionId: " + session.getId() + ", incomingMessage: " + textmessage );

        ServerMessage message=null;
        try {
            message = ServerMessage.fromJson(textmessage);
        }catch( Throwable throwable ){
            logger.error("Unable to parse the incoming incomingMessage. ignoring...");
            return;
        }

        if( message == null ){
            return;
        }

        // TODO: Store the message in a DB before proecessing

        // check whether the incomingMessage is meant for the server or a user/app
        // if topic is set, then it is meant for server side processing
        // if "ServerAddress" object is set, then it should be sent to the appropriate user/app.

        switch ( message.getMessageType()){
            case PUB_SUB:
                // the message is sent to the correct processor (consumer) of that message.
                // no response expected
                processPubSub( message);
                break;
            case REQUEST_RESPONSE:
                // Request-Response is similar to PUB_SUB.
                // but the "consumer" sends the a response, which will be sent back the original "producer"
                processRequestResponse( session.getId(), message );
                break;
        }
    }

    // This message should be processed by server side
    private void processPubSub(ServerMessage message) {
        // no response expected from client
        serverHandler.processPubSub(message);
    }

    private void processRequestResponse(String sessionId , ServerMessage message){

        // response expected from client
        serverHandler.processRequestResponse( sessionId, message);

    }

    private void processPointToPoint( ServerMessage message ){

    }

    @Override
    public void receiveFromClient(ServerMessage message) {

    }

    /**
     * <code>sendToClient</code> sends the message to clients ( real-world clients ).
     * It first tries to send through an existing session (connection).
     * If connection is not found, then sends the message through GCM/APNS.
     *
     * @param clientMessageWrapper
     */
    @Override
    public void sendToClient(ClientMessageWrapper clientMessageWrapper) {

        if( clientMessageWrapper == null ){
            return;
        }

        ClientMessage clientMessage = clientMessageWrapper.getClientMessage();
        if( clientMessage == null ){
            return;
        }

        String sessionId = clientMessageWrapper.getSessionId();
        if( sessionId != null && clientSessions.get(sessionId) != null ){

            WebSocketSession session = clientSessions.get(sessionId);

            if( session.isOpen() ){

                TextMessage textMessage = new TextMessage( clientMessage.getPayLoad());

                try {

                    session.sendMessage( textMessage );

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{

                // TODO: send the message using GCM, or when the app wakes up
            }
        }else{
            // TODO: send the message thru GCM or when the app wakes up
        }
    }


}