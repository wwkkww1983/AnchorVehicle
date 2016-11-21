package com.anke.vehicle.entity;


public class GPSInfo
{
	private String GpsTime;
	private double X;
	private double Height;
	private double Y;
	private double Speed;
	private int Dir;
	public double getX()
	{
		return X;
	}
	public void setX(double x)
	{
		X = x;
	}
	public String getGpsTime()
	{
		return GpsTime;
	}
	public void setGpsTime(String gpsTime)
	{
		GpsTime = gpsTime;
	}
	public double getY()
	{
		return Y;
	}
	public void setY(double y)
	{
		Y = y;
	}
	public double getHeight()
	{
		return Height;
	}
	public void setHeight(double height)
	{
		Height = height;
	}
	public int getDir()
	{
		return Dir;
	}
	public void setDir(int dir)
	{
		Dir = dir;
	}
	public double getSpeed()
	{
		return Speed;
	}
	public void setSpeed(double speed)
	{
		Speed = speed;
	}
	
}
