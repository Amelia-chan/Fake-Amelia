package pw.mihou.akari.websocket.listeners;

import io.javalin.websocket.WsMessageContext;
import pw.mihou.akari.websocket.facade.AkariWebsocket;

public interface AkariWebsocketListener {

    /**
     * Executed whenever the connection received has a type that
     * matches the message.
     *
     * @param connection    The connection that this event came from.
     * @param data          The data received from this event.
     */
    void onMessage(AkariWebsocket websocket, WsMessageContext connection, String data);

    /**
     * Gets the class type that this listener accepts.
     *
     * @return  The generic class type that this listener accepts.
     */
    String accepts();

}
