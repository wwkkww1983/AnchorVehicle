package com.anke.vehicle.comm;



import com.anke.vehicle.status.ConnectStatus;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TestClientIntHandler extends ChannelInboundHandlerAdapter {

    // 接收server端的消息，并打印出来
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf result = (ByteBuf) msg;
        byte[] aa = result.array();
        byte[] result1 = new byte[result.readableBytes()];
        result.readBytes(result1);
        if (onMessageListener != null)
            onMessageListener.onMessage(ctx, ConnectStatus.RECEIVER, result1);
        result.release();

    }

    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {


        if (onMessageListener != null) {  //连接成功
            onMessageListener.onMessage(ctx, ConnectStatus.CONNECT_SUCCESS, null);
        }
    }


    // 捕获到连接异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        // TODO Auto-generated method stub
        if (onMessageListener != null)
            onMessageListener.onMessage(ctx, ConnectStatus.CONNECT_FAIL, null);
        if (ctx != null)
            ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(io.netty.channel.ChannelHandlerContext ctx) throws java.lang.Exception {
        if (onMessageListener != null)
            onMessageListener.onMessage(ctx, ConnectStatus.CONNECT_FAIL, null);
        if (ctx != null)
            ctx.close();
    }

    // 发送消息
    public static void sendMsg(ChannelHandlerContext ctx, byte[] bytes) {
        if (ctx != null){
            ByteBuf buf = ctx.alloc().buffer(bytes.length);
            buf.writeBytes(bytes);
            ctx.writeAndFlush(buf);
        }

    }

    /**
     * 向外部暴露一个接口
     */
    public interface OnMessageListener {
        void onMessage(ChannelHandlerContext type, int sign, byte[] result);
    }

    public OnMessageListener onMessageListener;

    public void setOnMessageListener(OnMessageListener onMessageListener) {
        this.onMessageListener = onMessageListener;
    }
}
