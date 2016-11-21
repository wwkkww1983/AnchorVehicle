package com.anke.vehicle.entity;

public class DutyInfo
{
	private boolean IsOnDuty;
	private String PersonCode;
	private String GpsTime;
	
	public boolean isIsOnDuty()
	{
		return IsOnDuty;
	}
	public void setIsOnDuty(boolean isOnDuty)
	{
		IsOnDuty = isOnDuty;
	}
	public String getM_GpsTime()
	{
		return GpsTime;
	}
	public void setM_GpsTime(String GpsTime)
	{
		this.GpsTime = GpsTime;
	}
	public String getM_PersonCode()
	{
		return PersonCode;
	}
	public void setM_PersonCode(String PersonCode)
	{
		this.PersonCode = PersonCode;
	}
}
