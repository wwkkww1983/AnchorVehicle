package com.anke.vehicle.entity;


public class PADInfo
{
	private String TelCode;
	private int TaskOrder;
	private int WorkStateID;
	private int Type;	
	private Object Content;
	public int getType()
	{
		return Type;
	}
	public void setType(int type)
	{
		Type = type;
	}
	public Object getContent()
	{
		return Content;
	}
	public void setContent(Object content)
	{
		Content = content;
	}
	public String getTelCode()
	{
		return TelCode;
	}
	public void setTelCode(String telCode)
	{
		this.TelCode = telCode;
	}
	public int getTaskOrder()
	{
		return TaskOrder;
	}
	public void setTaskOrder(int taskOrder)
	{
		TaskOrder = taskOrder;
	}
	public int getWorkStateID()
	{
		return WorkStateID;
	}
	public void setWorkStateID(int workStateID)
	{
		WorkStateID = workStateID;
	}
}
