package com.anke.vehicle.entity;

public class BindAmbInfo {

	private String AmbCode; //车辆编码
	private String TelCode; //车载电话
	
	public String AmbCode()
	{
		return AmbCode;
	}
	public void SetAmbCode(String mAmbCode)
	{
		AmbCode = mAmbCode;
	}
	public String TelCode()
	{
		return TelCode;
	}
	public void SetTelCode(String mTelCode)
	{
		TelCode = mTelCode;
	}
}
