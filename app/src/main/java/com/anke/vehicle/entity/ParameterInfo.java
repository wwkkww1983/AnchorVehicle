package com.anke.vehicle.entity;

public class ParameterInfo
{
	private String telphone;//电话
	private String port;//端口号
	private String ipaddress;//服务器IP
	private String versionUrl;//版本URL
	private String apkUrl;//更新APKurl
	private int relink;
	private int islocation;
	private int isBindCar;
	private int isStopTask;
	private int isChangStation;
	private int isPauseCall;
	private int isNewTask;
	private int isGaoZhi;
	private int isShouFei;
	private int isShiGu;
	private String httpport;

	public int getIslocation() {
		return islocation;
	}

	public void setIslocation(int islocation) {
		this.islocation = islocation;
	}

	public int getRelink() {
		return relink;
	}

	public void setRelink(int relink) {
		this.relink = relink;
	}

	public String getTelphone()
	{
		return telphone;
	}

	public void setTelphone(String telphone)
	{
		this.telphone = telphone;
	}

	public String getIpaddress()
	{
		return ipaddress;
	}

	public void setIpaddress(String ipaddress)
	{
		this.ipaddress = ipaddress;
	}

	public String getPort()
	{
		return port;
	}

	public void setPort(String port)
	{
		this.port = port;
	}

	public String getVersionUrl()
	{
		return versionUrl;
	}

	public void setVersionUrl(String versionUrl)
	{
		this.versionUrl = versionUrl;
	}

	public String getApkUrl()
	{
		return apkUrl;
	}

	public void setApkUrl(String apkUrl)
	{
		this.apkUrl = apkUrl;
	}

	public int getIsBindCar() {
		return isBindCar;
	}

	public void setIsBindCar(int isBindCar) {
		this.isBindCar = isBindCar;
	}

	public int getIsStopTask() {
		return isStopTask;
	}

	public void setIsStopTask(int isStopTask) {
		this.isStopTask = isStopTask;
	}

	public int getIsChangStation() {
		return isChangStation;
	}

	public void setIsChangStation(int isChangStation) {
		this.isChangStation = isChangStation;
	}

	public int getIsPauseCall() {
		return isPauseCall;
	}

	public void setIsPauseCall(int isPauseCall) {
		this.isPauseCall = isPauseCall;
	}

	public int getIsNewTask() {
		return isNewTask;
	}

	public void setIsNewTask(int isNewTask) {
		this.isNewTask = isNewTask;
	}

	public int getIsGaoZhi() {
		return isGaoZhi;
	}

	public void setIsGaoZhi(int isGaoZhi) {
		this.isGaoZhi = isGaoZhi;
	}

	public int getIsShouFei() {
		return isShouFei;
	}

	public void setIsShouFei(int isShouFei) {
		this.isShouFei = isShouFei;
	}

	public int getIsShiGu() {
		return isShiGu;
	}

	public void setIsShiGu(int isShiGu) {
		this.isShiGu = isShiGu;
	}

	public String getHttpport() {
		return httpport;
	}

	public void setHttpport(String httpport) {
		this.httpport = httpport;
	}
	
}
