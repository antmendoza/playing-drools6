package com.antmendoza.model;

public class Signal {

	private int id;
	private String nameSensor;
	private int idFloor;
	private double temperature;
	private double humidity;

	public Signal() {
		super();
	}

	public Signal(int id, String nameSensor, int idFloor, double temperature,
			double humidity) {
		super();

		this.id = id;
		this.nameSensor = nameSensor;
		this.idFloor = idFloor;
		this.temperature = temperature;
		this.humidity = humidity;
	}

	public String getNameSensor() {
		return nameSensor;
	}

	public void setNameSensor(String nameSensor) {
		this.nameSensor = nameSensor;
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

	public int getIdFloor() {
		return idFloor;
	}

	public void setIdFloor(int idFloor) {
		this.idFloor = idFloor;
	}

}
