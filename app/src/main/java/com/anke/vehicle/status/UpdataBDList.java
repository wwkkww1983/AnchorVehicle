package com.anke.vehicle.status;

/**
 * 创建作者： 张蔡奇
 * 创建时间： 2016/9/29
 * 创建公司： 珠海市安克电子技术有限公司合肥分公司
 * 更新数据库分类
 */
public class UpdataBDList {
    /**
     * 更新医院数据库
     */
    public static final int UPDATE_HOSPITAL = 0;
    /**
     * 更新药品数据库
     */
    public static final int UPDATE_DRUG = 1;
    /**
     * 更新专家列表
     */
    public static final int UPDATE_MASTER = 2;
    /**
     * 选择停止任务原因
     */
    public static final  int UPDATE_STOPREASON = 3;
    /**
     * 选择暂停任务原因
     */
    public static final  int UPDATE_PAUSEREASON = 4;
    /**
     * 更新车载信息
     */
    public static final  int UPDATE_AMB = 5;
    /**
     * 提示更新成功
     */
    public static final  int UPDATE_SUCCESS = 6;
    /**
     * 获取车辆信息失败
     */
    public static final  int GET_AMB_FAIL = 7;
    /**
     * 处理绑定车辆
     */
    public static final  int DEAL_BIND_AMB = 8;
    /**
     * 绑定车辆失败
     */
    public static final  int BIND_AMB_FAIL = 9;
}
