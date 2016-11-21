package com.anke.vehicle.status;

/**
 * Created by Administrator on 2016/8/31.
 * 车辆工作状态
 */
public class WorkState {
    /**
     * 开始分配任务
     */
    public  final static  int ASSIGNING_TASKS = 0;
    /**
     * 收到指令
     */
    public final static int RECEIVER_INVOKING = 1;
    /**
     * 驶向现场
     */
    public final static int GOING = 2;
    /**
     * 到达现场
     */
    public final static int ARRIVED = 3;
    /**
     * 病人上车
     */
    public final static int GETON = 4;
    /**
     * 到达医院
     */
    public final static int ARRIVED_HOSPITAL = 5;
    /**
     * 途中待命
     */
    public final static int ROADWAITTING = 6;
    /**
     * 站内待命
     */
    public final static int STATION_AITTING = 7;
    /**
     * 不能调用
     */
    public final static int CANNOT_INVOKING = 8;
    /**
     * 暂停调用
     */
    public final static int SUSPEND_INVOKING = 9;



}
