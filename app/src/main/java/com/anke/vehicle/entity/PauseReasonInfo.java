package com.anke.vehicle.entity;

//暂停调用实体  2016-05-03  xmx
public class PauseReasonInfo {
	private String Code; //终止任务原因编码
	private String Name; //终止任务原因名称
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
}
