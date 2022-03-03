package pw.mihou.akari.websocket.facade;

public interface AkariWebsocket {

    /**
     * Queues the data to be broadcast to all clients.
     * @param object    The data to be queued.
     */
    void send(Object object);

    /**
     * Queues the data to be broadcasted towards a specific client.
     *
     * @param session   The session identifier of the client.
     * @param object    The data to be broadcast.
     */
    void send(String session, Object object);

    /**
     * Starts the websocket server.
     */
    void start();

    /**
     * Gracefully stops the websocket server, allowing it to push all
     * the remaining messages that are in queue before closing the server.
     */
    void stop();

}
