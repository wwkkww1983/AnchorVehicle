package com.anke.vehicle.status;

/**
 * 创建作者： 张蔡奇
 * 创建时间： 2016/9/28
 * 创建公司： 珠海市安克电子技术有限公司合肥分公司
 * 服务器连接状态
 */
public class ConnectStatus {
    /**
     * 服务器连接失败 可能连接异常 或者断线
     */
    public  static final int CONNECT_FAIL = 1;

    /**
     * 接收服务器数据
     */
    public static final int RECEIVER = 2;
    /**
     * 连接成功 向server发送消息(无论服务器是否有网 都提示连接成功 netty4 bug)
     */
    public static final int CONNECT_SUCCESS = 3;

}
