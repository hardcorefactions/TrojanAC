package net.valorhcf.trojan.packet;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.profile.Profile;

import java.util.logging.Level;

public class TrojanChannelDuplexHandler extends ChannelDuplexHandler {

    private final Trojan main = Trojan.getInstance();

    private final Profile profile;

    public TrojanChannelDuplexHandler(Profile profile) {
        this.profile = profile;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        long millis = System.currentTimeMillis();
        long nanos = System.nanoTime();

        super.channelRead(ctx, message);

        // Getting nanoTime again because I'm not sure how long the super call takes
        long start = System.nanoTime();

        try {
            profile.handleInboundPacket(message, millis, nanos);
        } catch (Exception e) {
            main.getLogger().log(Level.SEVERE, "Could not handle inbound packet", e);
        }

        main.getCheckTimings().addTiming(System.nanoTime() - start);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object message, ChannelPromise promise) throws Exception {
        long millis = System.currentTimeMillis();
        long nanos = System.nanoTime();

        try {
            profile.handleOutboundPacketBeforeWrite(message, millis, nanos);
        } catch (Exception e) {
            main.getLogger().log(Level.SEVERE, "Could not handle outbound packet", e);
        }

        super.write(ctx, message, promise);

        try {
            profile.handleOutboundPacket(message, millis, nanos);
        } catch (Exception e) {
            main.getLogger().log(Level.SEVERE, "Could not handle outbound packet", e);
        }
    }
}
