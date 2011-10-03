/**
 * 
 */
package org.concord.sensor.vernier;

public final class SensorID{
	/*
	 * These are taken from page 76 of the labpro tech manual.pdf
	 * I don't know if any of these values are correct.  The manual
	 * only lists their resistance value not the id number.  So the 
	 * resistance value is the number in the comment.
	 */
	public final static int NO_SENSOR_ID = 0;
	public final static int THEROCOUPLE = 1;  // 2.2K
	public final static int TI_VOLTAGE = 2;  // 33K
	public final static int CURRENT = 3;  // 6.8K
	public final static int RESISTANCE = 4;  // 3.3K: 1kOhm to 100 kOhm
	public final static int LONG_TEMP = 5;  // 22K:  extra long temp sensor degC
	public final static int CO2 = 6;  // 68K:  PPM 0 to 5000 ppm
	public final static int OXYGEN = 7;  // 100K: PCT 0 to 27%
	public final static int CV_VOLTAGE = 8;  // 150K: volts - Differential Voltage
	public final static int CV_CURRENT = 9;  // 220K: amps
	public final static int TEMPERATURE_C = 10;  // 10K:  verified for fast response probe
	public final static int TEMPERATURE_F = 11;  // 15K:
	public final static int LIGHT = 12;  // 4.7K: verified for light sensor
	public final static int HEART_RATE = 13;  // 1K:   BPM 
	public final static int VOLTAGE = 14;  // 47K: 
	public final static int EKG = 15;  // 1.5K:
	public final static int CO2_GAS = 17;
	public final static int OXYGEN_GAS = 18;

	/*
	 * smart sensors
	 */
	public final static int PH = 20;
	public final static int CONDUCTIVITY_200  = 21; 
	public final static int CONDUCTIVITY_2000 = 22;
	public final static int CONDUCTIVITY_20000 = 23; 
	public final static int GAS_PRESSURE = 24;
	public final static int DUAL_R_FORCE_10 = 25;
	public final static int DUAL_R_FORCE_50 = 26;
	public final static int _25G_ACCEL = 27; 
	public final static int LOWG_ACCEL = 28; 
	public final static int SMART_LIGHT_1 = 34;
	public final static int SMART_LIGHT_2 = 35;
	public final static int SMART_LIGHT_3 = 36;
	public final static int DISSOLVED_OXYGEN = 37;
	public final static int MAGNETIC_FIELD_HIGH = 44; 
	public final static int MAGNETIC_FIELD_LOW = 45;
	public final static int BAROMETER = 46;
	public final static int SMART_HUMIDITY = 47;
	public final static int COLORIMETER = 54;
	public final static int GO_TEMP = 60;
	public final static int SALINITY = 61; 
	public final static int BLOOD_PRESSURE = 66;
	public final static int HAND_DYNAMOMETER = 67;
	public final static int SPIROMETER = 68; 
	public final static int GO_MOTION = 69; 
	public final static int IR_TEMP = 73;
	public final static int SOUND_LEVEL = 74; 
	public final static int CO2_GAS_LOW = 75;

}