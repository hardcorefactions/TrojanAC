package net.valorhcf.trojan.log;

import cc.fyre.core.server.ServerModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogManager {

    private static final int DELAY = 20;

    private final Queue<Log> queue = new ConcurrentLinkedQueue<>();
    @Getter
    private final LogService service = Trojan.getInstance().getRetrofit().create(LogService.class);

    public LogManager() {
        Schedulers.async().runRepeating(() -> {
            List<Document> documentList = new ArrayList<>();

            Log log;
            while ((log = queue.poll()) != null) {
                documentList.add(log.toDocument());
            }

            if (!documentList.isEmpty()) {
                Trojan.getInstance().getLogsCollection().insertMany(documentList);
            }
        }, DELAY, DELAY);
    }

    public void log(Profile profile,String check, String metadata) {
        queue.add(new LogManager.Log(profile.uuid,profile.connectionTracker.keepAlivePing,check,ServerModule.INSTANCE.getServerId(),metadata,null));
    }

    public void log(Profile profile, Check check, String metadata) {
        queue.add(new LogManager.Log(profile.uuid,profile.connectionTracker.keepAlivePing,check.getName(),ServerModule.INSTANCE.getServerId(),metadata,null));
    }

    public void logBan(Profile profile,Check check, String message) {
        queue.add(new LogManager.Log(profile.uuid,profile.connectionTracker.keepAlivePing,check.getName(),ServerModule.INSTANCE.getServerId(),null,message));
    }

    @AllArgsConstructor
    public static class Log {
        private final long time = System.currentTimeMillis();
        private final String uuid;
        private final long ping;
        private final String check;
        private final String server;
        private final String metadata;
        private String banned;

        private Document toDocument() {
            Document document = new Document();
            document.put("time",time);
            document.put("uuid",uuid);
            document.put("ping",ping);
            document.put("check",check);
            document.put("server",server);
            document.put("metadata",metadata);

            if (banned != null) {
                document.put("banned",banned);
            }

            return document;
        }
    }
}
