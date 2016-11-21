package com.anke.vehicle.entity;

public class PadAmbInfo {
	private String AmbCode; //车辆编码
	private String RealSign; //实际标志
	private String TelCode; //车载电话
	
    public String GetAmbCode()
    {
        return AmbCode;
    }
    public void SetAmbCode(String mAmbCode)
    {
    	AmbCode = mAmbCode;
    }

    public String GetRealSign()
    {
        return RealSign;
    }   
    public void SetRealSign(String mRealSign)
    {
    	RealSign = mRealSign;
    }
    
    public String GetTelCode()
    {
        return TelCode;
    }
    public void SetTelCode(String mTelCode)
    {
    	TelCode = mTelCode;
    }

}
