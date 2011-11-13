/**
 * 
 */
package org.concord.sensor.vernier;

import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.DeviceService;
import org.concord.sensor.device.impl.SensorConfigImpl;
import org.concord.sensor.impl.LinearCalibration;
import org.concord.sensor.impl.Range;
import org.concord.sensor.impl.SensorCalibration;
import org.concord.sensor.impl.SensorUnit;

public class VernierSensor extends SensorConfigImpl
{
	public final static int CHANNEL_TYPE_ANALOG = 0;
	public final static int CHANNEL_TYPE_DIGITAL = 1;
	
	public final static byte kProbeTypeNoProbe = 0;
	public final static byte kProbeTypeTime = 1;
	public final static byte kProbeTypeAnalog5V = 2;
	public final static byte kProbeTypeAnalog10V = 3;
	public final static byte kProbeTypeHeatPulser = 4;
	public final static byte kProbeTypeAnalogOut =5;
	public final static byte kProbeTypeMD = 6;
	public final static byte kProbeTypePhotoGate = 7;
	public final static byte kProbeTypeDigitalCount = 10;
	public final static byte kProbeTypeRotary = 11;
	public final static byte kProbeTypeDigitalOut = 12;
	public final static byte kProbeTypeLabquestAudio = 13;
	
	/**
     * 
     */
    private final VernierSensorDevice device;

	SensorCalibration calibrationEquation;

	private int channelType;
	
	/**
	 * This corresponds to the OperationType filed in the SensorDDSRec structure.
	 * Its values are the constants starting with "kProbeType"
	 * in the verniersensormap.xml file there is a "Type" attribute on each sensor
	 * that defines this property.
	 */
	byte vernierProbeType = kProbeTypeAnalog5V;	
	
	/**
     * @param device
	 * @param channelNumber 
     */
    public VernierSensor(VernierSensorDevice device, DeviceService devService, 
    		int channelNumber, int channelType)
    {
        this.device = device;
        setPort(channelNumber);
        this.channelType = channelType; 
    }

	public void setCalibration(SensorCalibration calibration)
	{
		calibrationEquation = calibration;
	}
	   	
	public SensorCalibration getCalibration()
	{
		return calibrationEquation;
	}
	
	/**
	 * @param sensorId
	 * @return
	 */
	public int setupSensor(int sensorId, SensorRequest request)
	{
		if(channelType == CHANNEL_TYPE_DIGITAL){
			
			// This is a motion sensor
			if(sensorId == 2) {
				setConfirmed(true);

				// it is digital sensor
				setUnit(new SensorUnit("m"));
				setType(QUANTITY_DISTANCE);

				vernierProbeType = kProbeTypeMD;
				setStepSize(0.01f);
			}
			
		} else if(sensorId >= 20){
			// This is a smart sensor which means it has 
			// calibration information stored in the sensor itself
			
			setConfirmed(true);

			// TODO get the information from the auto id sensor
			// sprintf(sensConfig->name, ddsRec.SensorLongName);
			// state->calibrationFunct = NULL;
			Range valueRange = null;
			
			switch(sensorId){
			case SensorID.BAROMETER:
				setUnit(new SensorUnit("kPa"));
				setType(QUANTITY_GAS_PRESSURE);

				// for pressure this is required so it can tell the diff
				// between barometer and regular pressure
				setStepSize(0.01f); 
				
				valueRange = new Range(81.0f, 106.0f);
				setValueRange(valueRange);
				break;
			case SensorID.GAS_PRESSURE:
				setUnit(new SensorUnit("kPa"));
				setType(QUANTITY_GAS_PRESSURE);

				// for pressure this is required so it can tell the diff
				// between barometer and regular pressure
				setStepSize(0.05f); 
				break;
			case SensorID.DUAL_R_FORCE_10:
				setUnit(new SensorUnit("N"));
				setType(QUANTITY_FORCE);
				setStepSize(0.01f);
				
				valueRange = new Range(-10f, 10f);
				setValueRange(valueRange);
				break;

			case SensorID.DUAL_R_FORCE_50:
				setUnit(new SensorUnit("N"));
				setType(QUANTITY_FORCE);
				setStepSize(0.05f);

				valueRange = new Range(-50f, 50f);
				setValueRange(valueRange);
				break;
			case SensorID.SMART_LIGHT_1:
			case SensorID.SMART_LIGHT_2:
			case SensorID.SMART_LIGHT_3:
				setUnit(new SensorUnit("lx"));
				setType(QUANTITY_LIGHT);
				
				// we keep this artificially low so we don't restrict 
				// malformed requests which claim to require small step sizes
				setStepSize(0.01f);
				break;				
			case SensorID.GO_TEMP:
				setUnit(new SensorUnit("degC"));
				setType(QUANTITY_TEMPERATURE_WAND);
				
				setStepSize(0.01f);
				break;
			case SensorID.GO_MOTION:
				setUnit(new SensorUnit("m"));
				setType(QUANTITY_DISTANCE);

				setStepSize(0.01f);
				break;
			case SensorID.SMART_HUMIDITY:
				setUnit(new SensorUnit("%RH"));
				setType(QUANTITY_RELATIVE_HUMIDITY);
				
				// This is higher than the others
				// but we are not currently paying attention to step size
				// for humidity sensors @see AbstractSensorDevice#scoreStepSize
				setStepSize(0.1f);
				break;
			case SensorID.IR_TEMP:
				setUnit(new SensorUnit("degC"));
				setType(QUANTITY_TEMPERATURE_WAND);

				setStepSize(0.01f);
				break;
			case SensorID.PH:
				setUnit(new SensorUnit("pH"));
				setType(QUANTITY_PH);
				setStepSize(0.0077f);
				break;			
			case SensorID.SALINITY:
				setUnit(new SensorUnit("ppt"));
				setType(QUANTITY_SALINITY);
				
				// This is just a bit higher than the others so it might
				// cause problems, but again we aren't paying attention
				// to the step size right now @see AbstractSensorDevice#scoreStepSize
				setStepSize(0.02f);
				break;			
			case SensorID.CO2_GAS_LOW:
				setUnit(new SensorUnit("ppm"));
				setType(QUANTITY_CO2_GAS);
				
				// This is higher than the others
				// but we are not currently paying attention to step size
				// for co2 sensors @see AbstractSensorDevice#scoreStepSize				
				setStepSize(4.0f);
				break;
			case SensorID.COLORIMETER:
				setUnit(new SensorUnit("%T"));
				setType(QUANTITY_COLORIMETER);
				setStepSize(0.057f);
				break;
			case SensorID.HAND_DYNAMOMETER:
				setUnit(new SensorUnit("N"));
				setType(QUANTITY_HAND_DYNAMOMETER);
				setStepSize(0.35f);
				break;
			default:
				setType(QUANTITY_UNKNOWN);
				break;				
			}	

		} else if(sensorId != 0) {
			setConfirmed(true);

			// do a lookup from our list of known sensors and calibrations
			this.device.log("  current attached sensor: " + sensorId);

			switch(sensorId){
			case SensorID.TEMPERATURE_C:
				setUnit(new SensorUnit("degC"));
				setName("Temperature");
				setType(QUANTITY_TEMPERATURE);			
				
				// we keep this artificially low so we don't restrict 
				// malformed requests which claim to require small step sizes
				setStepSize(0.01f); 
				setCalibration(temperatureCalibration);
				break;
			case SensorID.THEROCOUPLE:
				setUnit(new SensorUnit("degC"));
				setName("Temperature");
				setType(QUANTITY_TEMPERATURE);			
				// we keep this artificially low so we don't restrict 
				// malformed requests which claim to require small step sizes
				setStepSize(0.01f); 
				setCalibration(temperatureCalibration);
				break;
			case SensorID.LIGHT:
				setUnit(new SensorUnit("lux"));
				setName("Illumaninace");
				setType(QUANTITY_LIGHT);			
				
				// This is higher than the others
				// but we are not currently paying attention to step size
				// for light sensors @see AbstractSensorDevice#scoreStepSize				
				setStepSize(2f);
				setCalibration(lightCalibration);
				break;			
			case SensorID.TI_VOLTAGE:			
			case SensorID.VOLTAGE:
			case SensorID.CV_VOLTAGE:
				setUnit(new SensorUnit("V"));
				setName("Voltage");
				setType(QUANTITY_VOLTAGE);

				setStepSize(0.01f);
				switch(sensorId){
				case SensorID.TI_VOLTAGE:
					setCalibration(tiVoltageCalibration);
					vernierProbeType = kProbeTypeAnalog10V;
					break;		
				case SensorID.VOLTAGE:
					setCalibration(rawVoltageCalibration);
					break;
				case SensorID.CV_VOLTAGE:
					setCalibration(differentialVoltageCalibration);
					break;
				}
				break;
			case SensorID.CO2_GAS:
				setUnit(new SensorUnit("ppm"));
				setName("CO2 Gas");
				setType(QUANTITY_CO2_GAS);			

				// This is higher than the others
				// but we are not currently paying attention to step size
				// for co2 sensors @see AbstractSensorDevice#scoreStepSize				
				setStepSize(4.0f);
				setCalibration(co2GasCalibration);			
				break;
			case SensorID.OXYGEN_GAS:
				setUnit(new SensorUnit("ppt"));
				setName("Oxygen Gas");
				setType(QUANTITY_OXYGEN_GAS);			

				// This is higher than the others
				// but we are not currently paying attention to step size
				// for oxygen sensors @see AbstractSensorDevice#scoreStepSize				
				setStepSize(0.1f); 
				setCalibration(oxygenGasCalibration);			
				break;
			case SensorID.CURRENT:
			case SensorID.RESISTANCE:
			case SensorID.LONG_TEMP:
			case SensorID.CO2:
			case SensorID.OXYGEN:
			case SensorID.CV_CURRENT:
			case SensorID.TEMPERATURE_F:
			case SensorID.HEART_RATE:
			case SensorID.EKG:
				this.device.log("Sensor type is not supported yet: " + sensorId);
				setType(QUANTITY_UNKNOWN);
				break;

			default:
				this.device.log("Unknown sensor id: " + sensorId);
				setType(QUANTITY_UNKNOWN);
			}

			// TODO Auto-generated method stub
			return 0;
		} else {
			// These are for sensors we can't auto id.
			// They will not work in the current design
			
			setConfirmed(false);
			
			// This is not an auto id sensor
			// as long as there is only one sensor that matches 
			// the requested quantity type.  If not then
			// we are going to have problems.  The api breaks
			// down here.  Lets cross our fingers and hope we don't
			// have to deal with that.
			if(request != null) {
				// we need a request to determine what calibration
				// to use.
				return 0;
			}
			switch(request.getType()){
			case QUANTITY_RELATIVE_HUMIDITY:
				setUnit(new SensorUnit("%RH"));
				setName("Relative Humidity");
				setType(QUANTITY_RELATIVE_HUMIDITY);
				setStepSize(0.04f);
				setCalibration(relativeHumidityCalibration);
				break;
			case QUANTITY_FORCE:
				// if we are here it means they are using
				// a student force sensor
				setUnit(new SensorUnit("N"));
				setName("Force");
				setType(QUANTITY_FORCE);
				setStepSize(0.02f);
				setCalibration(studentForceCalibration);
				break;
			}
		}
		


		return 0;
	}
	
	public byte getVernierProbeType()
	{
		return vernierProbeType;
	}
	
	public void setVernierProbeType(byte type) {
		vernierProbeType = type;
	}

	/**
	 * Special calibration function for simply return the data which is 
	 * passed in
	 */
	public final static SensorCalibration rawVoltageCalibration = 
		new LinearCalibration(
				0f,  // k0  
				1f   // k1 - return the same value passed in
				);

	/**
	 * Special calibration function for flagging raw data
	 * it should actually never be called.  So it returns a value
	 * which is hopefully noticably weird
	 */
	public final static SensorCalibration rawDataCalibration = 
		new LinearCalibration(
				0.12345f,  // k0  - return a constant value
				0f         // k1
				);

	public final static SensorCalibration temperatureCalibration =
		new SensorCalibration(){
		/*
		 * First get the R of the sensor: V0 = measured voltage Vres = reference
		 * voltage 5V Rknown = resistance of Vres V1 = voltage we measured
		 * Rsensor = V0*Rknown/(Vres-V0) this equation comes from the standard
		 * voltage division equation.
		 * 
		 * Now with the resistance the equation for the temp in degC is: 
		 * <pre>
		 * T(degC) = 1/(K0 + K1*ln(1000*R) + K2*ln(1000*R)^3) - 273.15 
		 * K0 = 1.02119E-3 
		 * K1 = 2.22468E-4 
		 * K2 = 1.33342E-7 
		 * </pre>
		 */
		public final static float TEMP_K0 = 1.02119E-3f;
		public final static float TEMP_K1 = 2.22468E-4f;
		public final static float TEMP_K2 = 1.33342E-7f;
		public float calibrate(float voltage)
		{
			float R = voltage*15/(5-voltage);

			float lnR = (float)Math.log(1000*R);

			return 1.0f /(TEMP_K0 + TEMP_K1*lnR + TEMP_K2*lnR*lnR*lnR) - 273.15f;
		}
	};
	
	/**
	 * Light Light Calibration
	 */
	public final static SensorCalibration lightCalibration =
		new SensorCalibration(){
		/*
		 * From the vernier light sensor booklet.
		 */ 
		//public final static float ILLUM_B0 = 5.0E-3f;  // most sensitive switch position
		public final static float ILLUM_B1 = 4.5E-4f;  // middle switch position
		//public final static float ILLUM_B2 = 2.0E-5f;  // least sensitive (outdoor) position
		public float calibrate(float voltage)
		{
			// The only sensor I have doesn't have a switch
			// so I'm going to guess it is in the middle position
			return voltage/ILLUM_B1;
		}
	};

	/**
	 * Relative Humidity Calibration
	 */
	public final static SensorCalibration relativeHumidityCalibration =
		new LinearCalibration(
				-23.8f,   // k0
				32.9f);   // k1
	
	/**
	 * Student Force
	 */
	public final static SensorCalibration studentForceCalibration =
		new LinearCalibration(
				9.8f,   // k0
				-9.8f); // k1
	
	public final static SensorCalibration tiVoltageCalibration =
		rawVoltageCalibration;

	/**
	 * Differental Voltage
	 */
	public final static SensorCalibration differentialVoltageCalibration =
		new LinearCalibration(
				6.25f,  // k0
				-2.5f); // k1

	/**
	 * CO2 Gas Calibration
	 * this ithe ppm calibration
	 */
	public final static SensorCalibration co2GasCalibration =
		new LinearCalibration(
				0f,     // k0
				2000f   // k1
				); 
	
	/**
	 * Oxygen Gas Calibration
	 * this is the ppt calibration
	 */
	public final static SensorCalibration oxygenGasCalibration =
		new LinearCalibration(
				0f,      // k0
				67.69f   // k1
				);

}