package com.anke.vehicle.entity;

/**
 * 默认 120指挥急救中心
 * 默认指挥 1
 * 默认版本号 v 3
 */
public class DisplayInfo
{
	private String AmbDeskName;//默认指挥 1
	private String AmbulanceCode;//默认版本号 v 3
	private String CenterName;//默认北京120急救中心
	private String StationCode; //分站编码
	private String StationName; //分站名称
	public String getAmbDeskName()
	{
		return AmbDeskName;
	}
	public void setAmbDeskName(String ambDeskName)
	{
		AmbDeskName = ambDeskName;
	}
	public String getAmbulanceCode()
	{
		return AmbulanceCode;
	}
	public void setAmbulanceCode(String ambulanceCode)
	{
		AmbulanceCode = ambulanceCode;
	}
	public String getCenterName()
	{
		return CenterName;
	}
	public void setCenterName(String centerName)
	{
		CenterName = centerName;
	}
	public String getStationCode() {
		return StationCode;
	}
	public void setStationCode(String stationCode) {
		StationCode = stationCode;
	}
	public String getStationName() {
		return StationName;
	}
	public void setStationName(String stationName) {
		StationName = stationName;
	}
	
	
}
