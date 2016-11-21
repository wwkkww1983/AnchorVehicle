package com.anke.vehicle.status;

/**
 * Created by Administrator on 2016/8/31.
 * 服务器连接状态
 */
public class ServerStatus {
    /**
     *  重新连接服务端
     */
    public final static int RECONNRCTING = 1;
    /**
     * 后台服务器关闭或者断网
     */
    public final static int SERVER_OR_Client_EXCEPTION = 4;
}
