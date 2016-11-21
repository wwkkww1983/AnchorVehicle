package com.anke.vehicle.entity;

/**
 *  得到GPS配置
 *  添加非任务时间间隔，车辆编码字段 2015-07-06 肖明星 2015-7-16 修改 费晓波
 */
public class NPadIntervalInfo {
	public int TaskInterval;
	public int StationInterval;
	public int OffInterval;
	public boolean IsGPS;
	public String AmbCode;
	public int getTaskInterval() {
		return TaskInterval;
	}
	public void setTaskInterval(int taskInterval) {
		TaskInterval = taskInterval;
	}
	public int getStationInterval() {
		return StationInterval;
	}
	public void setStationInterval(int stationInterval) {
		StationInterval = stationInterval;
	}
	public int getOffInterval() {
		return OffInterval;
	}
	public void setOffInterval(int offInterval) {
		OffInterval = offInterval;
	}
	public boolean isIsGPS() {
		return IsGPS;
	}
	public void setIsGPS(boolean isGPS) {
		IsGPS = isGPS;
	}
	public String getAmbCode() {
		return AmbCode;
	}
	public void setAmbCode(String ambCode) {
		AmbCode = ambCode;
	}	
 
}
