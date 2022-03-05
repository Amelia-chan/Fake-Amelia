package pw.mihou.akari.websocket.listeners.modules;

import io.javalin.websocket.WsMessageContext;
import pw.mihou.akari.activities.AkariFeeds;
import pw.mihou.akari.databases.AkariDatabases;
import pw.mihou.akari.websocket.facade.AkariWebsocket;
import pw.mihou.akari.websocket.listeners.AkariWebsocketListener;
import pw.mihou.alisa.AlisaGlobal;
import pw.mihou.alisa.modules.AlisaFeed;
import pw.mihou.alisa.modules.database.modules.AlisaIndex;
import pw.mihou.alisa.modules.exceptions.handler.AlisaExceptionHandler;
import pw.mihou.alisa.modules.requests.AlisaTestRequest;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AkariOnTestRequest implements AkariWebsocketListener {

    @Override
    public void onMessage(AkariWebsocket websocket, WsMessageContext connection, String data) {
        try {
            @Nonnull AlisaTestRequest request = Objects.requireNonNull(AlisaGlobal.MOSHI
                    .adapter(AlisaTestRequest.class)
                    .fromJson(data));

            AlisaFeed feed = AkariDatabases.FEEDS.get(new AlisaIndex("unique", request.unique())).join().orElseThrow();
            AkariFeeds.getAndUpdate(feed).forEach(chapter -> websocket.send(connection.getSessionId(), chapter));
        } catch (Exception exception) {
            AlisaExceptionHandler.accept(exception);
        }
    }

    @Override
    public String accepts() {
        return AlisaTestRequest.class.getName();
    }
}
