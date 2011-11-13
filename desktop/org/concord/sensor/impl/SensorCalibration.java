/**
 * 
 */
package org.concord.sensor.impl;

/**
 * The intention of this inteface is to unify the different calibration
 * objects used in the different implementations.  Eventually it might
 * be usefully so someone can introspect the sensor implementation either
 * for debugging, or education purposes and see the calibration being used
 * for a particular sensor.  
 * 
 * @author scott
 *
 */
public interface SensorCalibration{
	float calibrate(float voltage);
}