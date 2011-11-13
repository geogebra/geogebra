/**
 * 
 */
package org.concord.sensor.impl;

/**
 * This is the most common type of calibration
 * 
 * @author scott
 *
 */
public class LinearCalibration implements SensorCalibration 
{
	private float k0;
	private float k1;
	
	public LinearCalibration(float k0, float k1)
	{
		this.k0 = k0;
		this.k1 = k1;
	}
	
	public float calibrate(float voltage)
	{
		return k0 + k1*voltage;
	}
}