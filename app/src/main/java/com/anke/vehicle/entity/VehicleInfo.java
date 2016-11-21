package com.anke.vehicle.entity;

import java.util.ArrayList;
import java.util.List;

public class VehicleInfo
{
	public String updatetime="";//更新时间
	public String version="0";//版本
	public String downurl="";//下载地址
	public String telCodeString="";//本车电话号码
	public List<PADInfo> padinfo=new ArrayList<PADInfo>();
}
