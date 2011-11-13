package org.concord.sensor.impl;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.impl.SensorConfigImpl;

/**
 * SensorUtilJava <br>
 * A class to house helper methods for the Java implementations and users of the sensor library.
 * They are "Java" because the library works in waba too, and the methods below use Java specific
 * things like reflection and System.out
 * 
 * <p>
 * Date created: Apr 28, 2008
 * 
 * @author scytacki<p>
 *
 */
public class SensorUtilJava {

	public static String getTypeConstantName(int type) {
		Field[] declaredFields = SensorConfig.class.getDeclaredFields();
		for(int i=0; i<declaredFields.length; i++){
			Field field = declaredFields[i];
			
			// make sure it is static
			int mod = field.getModifiers();
			if(!Modifier.isStatic(mod)){
				continue;
			}
			
			// make sure the name is correct
			String name = field.getName();
			if(!name.startsWith("QUANTITY_")){
				continue;
			}
		
			int fieldValue;
			try {
				fieldValue = field.getInt(null);
				if(fieldValue == type){
					return name;
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public static void printExperimentConfig(ExperimentConfig currentConfig) {
		PrintStream out = System.out;
		
		out.println("ExperimentConfig");
		out.println("  deviceName " + currentConfig.getDeviceName());
		out.println("  dataReadPeriod " + currentConfig.getDataReadPeriod());
		out.println("  exactPeriod " + currentConfig.getExactPeriod());
		out.println("  invalid " + currentConfig.isValid());
		out.println("  invalidReason " + currentConfig.getInvalidReason());
		
		SensorConfig[] sensors = currentConfig.getSensorConfigs();
		for (int i=0; i<sensors.length; i++) {
			SensorConfig sensor = sensors[i];
			out.println("  SensorConfig");
			out.println("    name " + sensor.getName());
			out.println("    type " + sensor.getType());
			out.println("    typeConstant " + getTypeConstantName(sensor.getType()));
			out.println("    port " + sensor.getPort());
			out.println("    portName " + sensor.getPortName());
			out.println("    stepSize " + sensor.getStepSize());
			out.println("    confirmed " + sensor.isConfirmed());
			out.println("    unit " + sensor.getUnit().getDimension());
		}	
	}
	
	public static String experimentConfigToString(ExperimentConfig currentConfig) {
		String ret = "ExperimentConfig\n"; 		
		
		ret += "  deviceName " + currentConfig.getDeviceName() + "\n";
		ret += "  dataReadPeriod " + currentConfig.getDataReadPeriod() + "\n";
		ret += "  exactPeriod " + currentConfig.getExactPeriod() + "\n";
		ret += "  invalid " + currentConfig.isValid() + "\n";
		ret += "  invalidReason " + currentConfig.getInvalidReason() + "\n";
		
		SensorConfig[] sensors = currentConfig.getSensorConfigs();
		for (int i=0; i<sensors.length; i++) {
			SensorConfig sensor = sensors[i];
			ret += "  SensorConfig" + "\n";
			ret += "    name " + sensor.getName() + "\n";
			ret += "    type " + sensor.getType() + "\n";
			ret += "    typeConstant " + getTypeConstantName(sensor.getType()) + "\n";
			ret += "    port " + sensor.getPort() + "\n";
			ret += "    portName " + sensor.getPortName() + "\n";
			ret += "    stepSize " + sensor.getStepSize() + "\n";
			ret += "    confirmed " + sensor.isConfirmed() + "\n";
			ret += "    unit " + sensor.getUnit().getDimension() + "\n";
		}	
		
		return ret;
	}

	public static String experimentRequestToString(ExperimentRequest request) 
	{
		 String ret = "ExperimentRequest\n";		
		 ret += "  numberOfSamples: " + request.getNumberOfSamples() + "\n";
		 ret += "  period: " + request.getPeriod() + "\n";
		 SensorRequest[] sensorRequests = request.getSensorRequests();
		 for (SensorRequest sensor: sensorRequests){
			ret += "  sensorRequest:\n";
		    ret += "    displayPrecision: " + sensor.getDisplayPrecision() + "\n";
		    ret += "    port: " + sensor.getPort() + "\n";
		    ret += "    requiredMax: " + sensor.getRequiredMax() + "\n";
		    ret += "    requiredMin: " + sensor.getRequiredMin() + "\n";		    
		    ret += "    stepSize: " + sensor.getStepSize() + "\n";
		    ret += "    type: " + getTypeConstantName(sensor.getType()) + "\n";
		    ret += "    unit: " + sensor.getUnit().getDimension() + "\n";
			String[] sensorParamKeys = sensor.getSensorParamKeys();
		    if(sensorParamKeys != null && sensorParamKeys.length > 0){
		    	ret += "    sensorParams:\n";
		    	for (String key : sensorParamKeys) {
					ret += "      " + key + ": " + sensor.getSensorParam(key) + "\n";
				}
		    }
		 }
		 
		 return ret;
	}

    public static float scoreSensorType(SensorConfig config, SensorRequest request)
    {
    	// There are a few cases where different types should still match
    	// or different types should be given preference

    	if(request.getType() == SensorConfig.QUANTITY_TEMPERATURE){
    		// We always prefer the temperature wand over just plain temperature
    		if(config.getType() == SensorConfig.QUANTITY_TEMPERATURE) {
    			return 0.75f;
    		}
    		if(config.getType() == SensorConfig.QUANTITY_TEMPERATURE_WAND) {
    			return 1f;
    		}
    	}  

    	// Some devices return velocity when they should be returning distance
    	// currently we don't have any devices actually returning velocity 
    	// and no activities need velocity, so this is currently ok.
    	if(config.getType() == SensorConfig.QUANTITY_DISTANCE) {
    		if(request.getType() == SensorConfig.QUANTITY_DISTANCE ||
    				request.getType() == SensorConfig.QUANTITY_VELOCITY) {
    			return 1f;
    		}
    	}

    	// The types don't match and this isn't one of the cases above
    	if(config.getType() != request.getType()) {
    		return 0f;
    	}

    	return 1;
    }

    public static float scoreValueRange(SensorConfig config, SensorRequest request)
    {
        if(config instanceof SensorConfigImpl){
        	Range valueRange = ((SensorConfigImpl)config).getValueRange();
        	if(valueRange == null){
        		return 1f;
        	} 
        	
        	// valueRange is not null

        	float reqMin = request.getRequiredMin();
        	float reqMax = request.getRequiredMax();

          	if(!Float.isNaN(reqMin) && 
          			reqMin < valueRange.minimum){
          		// at least this requirement is out of range
          		return 0.5f;
          	}
          	
          	if(!Float.isNaN(reqMax) && 
          			reqMax > valueRange.maximum){
          		// at least this requirement is out of range
          		return 0.5f;	
          	}

          	// if we got here either both requirements are not specified
          	// or they passed. 
          	return 1f;
        }
        
        // We know nothing about the value range of the sensor config so 
        // we should not dock the score
        return 1f;
    }
    
    public static float scoreStepSize(SensorConfig config, SensorRequest request)
    {
    	// we currently only care about the step size for pressure and force
    	// sensors
    	// for pressure 
    	// it is how we differenciate between a barometer sensor and a regular
    	// pressure sensor
    	// for force it is how we differenciate between the 10N and 50N
    	// setting on many force probes
    	if(request.getType() == SensorConfig.QUANTITY_GAS_PRESSURE ||
    			request.getType() == SensorConfig.QUANTITY_FORCE){
    		if(!Float.isNaN(request.getStepSize())){
    			// the request doesn't have a valid step size
    			return 1f;
    		}
    		
    		if(request.getStepSize() <= 0 && config.getStepSize() <= 0){
    			// if either the stepSize is <= 0 it means the step size isn't
    			// specified or isn't available so in this case we have to 
    			// play dumb and return 1f
    			return 1f;
    		}
    		
    		if(request.getStepSize() >= config.getStepSize()){
    			// The request has a larger step size, in otherwords it requires
    			// less precision, so this is ok.
    			return 1f;
    		}
    		
    		// The request step size is less than the available step size of 
    		// the sensor.  So this sensor can not be used for this experiment.
    		// Typically it might be better to simply return a low value here 
    		// not zero, but in the case of pressure it is difficult measure 
    		// barometric pressure if the sensor isn't precise enough.
    		return 0f;
    	}
    	
    	return 1f;
    }

}
