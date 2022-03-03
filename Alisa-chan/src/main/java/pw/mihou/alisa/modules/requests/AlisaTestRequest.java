package pw.mihou.alisa.modules.requests;

/**
 * {@link AlisaTestRequest} is a request that is sent from Ame-chan to Akari-chan
 * that tells Akari-chan to send the latest feed for the specific unique identifier
 * onto the websocket channel.
 * <br><br>
 * Akari-chan will then broadcast the message onto all sessions.
 */
public record AlisaTestRequest(long unique) {
}
