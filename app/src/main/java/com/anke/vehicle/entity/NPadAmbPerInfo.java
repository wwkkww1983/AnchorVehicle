package com.anke.vehicle.entity;

import java.util.List;

public class NPadAmbPerInfo
{	
	private boolean Success;
	private String Remark;
	private List<NPadPerInfo> PPInfo;
	public boolean getSuccess()
	{
		return Success;
	}
	public void setSuccess(boolean success)
	{
		Success = success;
	}
	public String getRemark()
	{
		return Remark;
	}
	public void setRemark(String remark)
	{
		Remark = remark;
	}
	public List<NPadPerInfo> getPPInfo()
	{
		return PPInfo;
	}
	public void setPPInfo(List<NPadPerInfo> pPInfo)
	{
		PPInfo = pPInfo;
	}
	
}
