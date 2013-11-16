package com.antmendoza.model;

public class Signal {

	private int id;
	private String sensor;
	private int floor;
	private double temperature;
	private double humidity;

	public Signal() {
		super();
	}

	public Signal(int id, String sensor, int floor, double temperature,
			double humidity) {
		super();

		this.id = id;
		this.sensor = sensor;
		this.floor = floor;
		this.temperature = temperature;
		this.humidity = humidity;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

}
