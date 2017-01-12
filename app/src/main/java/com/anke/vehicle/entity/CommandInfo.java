package com.anke.vehicle.entity;

public class CommandInfo
{
	private boolean IsLabeled;
	private String AlarmTel;
	private String LinkTel;
	private String Extension;
	private double X;
	private double Y;
	private String AmbCode;
	private String TaskCode;
	private String SendTime;
	private String Content;
	public String getAlarmTel()
	{
		return AlarmTel;
	}
	public void setAlarmTel(String alarmTel)
	{
		AlarmTel = alarmTel;
	}
	public boolean isIsLabeled()
	{
		return IsLabeled;
	}
	public void setIsLabeled(boolean isLabeled)
	{
		IsLabeled = isLabeled;
	}
	public String getExtension()
	{
		return Extension;
	}
	public void setExtension(String extension)
	{
		Extension = extension;
	}
	public double getX()
	{
		return X;
	}
	public void setX(double x)
	{
		X = x;
	}
	public String getLinkTel()
	{
		return LinkTel;
	}
	public void setLinkTel(String linkTel)
	{
		LinkTel = linkTel;
	}
	public String getSendTime()
	{
		return SendTime;
	}
	public void setSendTime(String sendTime)
	{
		SendTime = sendTime;
	}
	public double getY()
	{
		return Y;
	}
	public void setY(double y)
	{
		Y = y;
	}
	public String getContent()
	{
		return Content;
	}
	public void setContent(String content)
	{
		Content = content;
	}
	public String getAmbCode()
	{
		return AmbCode;
	}
	public void setAmbCode(String ambCode)
	{
		AmbCode = ambCode;
	}
	public String getTaskCode()
	{
		return TaskCode;
	}
	public void setTaskCode(String taskCode)
	{
		TaskCode = taskCode;
	}

	@Override
	public String toString() {
		return "CommandInfo{" +
				"IsLabeled=" + IsLabeled +
				", AlarmTel='" + AlarmTel + '\'' +
				", LinkTel='" + LinkTel + '\'' +
				", Extension='" + Extension + '\'' +
				", X=" + X +
				", Y=" + Y +
				", AmbCode='" + AmbCode + '\'' +
				", TaskCode='" + TaskCode + '\'' +
				", SendTime='" + SendTime + '\'' +
				", Content='" + Content + '\'' +
				'}';
	}
}
