/**
 * 
 */
package org.concord.sensor.vernier.labpro;

import org.concord.sensor.serial.SerialException;

/**
 * @author scott
 *
 */
public class LabProProtocol
{
	public final static class CMD{
        public static final int RESET = 0;
        public static final int CHANNEL_SETUP = 1;
        public static final int DATA_COLLECTION_SETUP = 3;
        public static final int CONVERSION_EQUATION_SETUP = 4;
        public static final int DATA_CONTROL = 5;
        public static final int SYSTEM_SETUP = 6;
        public static final int REQUEST_SYSTEM_STATUS = 7;
        public static final int REQUEST_CHANNEL_STATUS = 8;
        public static final int REQUEST_CHANNEL_DATA = 9;
        public static final int ADVANCED_DATA_REDUCTION = 10;
        public static final int DIGITAL_DATA_CAPTURE = 12;
        public static final int PORT_POWER_CONTROL_COMMAND = 102;
        public static final int BAUD_RATE_SELECTION = 105;
        public static final int MOTION_DETECTOR_UNDERSAMPLE_RATE = 106;
        public static final int OVERSAMPLING_BURST = 107;
        public static final int REQUEST_SETUP_INFORMATION = 115;
        public static final int REQUEST_LONG_SENSOR_NAME = 116;
        public static final int REQUEST_SHORT_SENSOR_NAME = 117;
        public static final int REQUEST_ALTERNATE_CALIBRATION = 119;
        public static final int ARCHIVE_OPERATIONS_COMMAND = 201;
        public static final int ANALOG_OUTPUT_SETUP = 401;
        public static final int SET_LED_COMMAND = 1998;
        public static final int SOUND_COMMAND = 1999;
        public static final int DIRECT_OUTPUT_TO_DIGITALOUT_PORT = 2001;
    }
	public final static class SYS_STATUS{
    	public static final int SOFTWARE_ID = 0;
    	public static final int ERROR = 1;
    	public static final int BATTERY = 2;
    	public static final int CONST_8888 = 3;
    	public static final int SAMPLE_TIME = 4;
    	public static final int TRIGGER_CONDITION = 5;
    	public static final int CHANNEL_FUNCTION = 6;
    	public static final int CHANNEL_POST = 7;
    	public static final int CHANNEL_FILTER = 8;
    	public static final int NUM_SAMPLES = 9;
    	public static final int RECORD_TIME = 10;
    	public static final int TEMPERATURE = 11;
    	public static final int PIEZO_FLAG = 12;
    	public static final int SYSTEM_STATE = 13;
    	public static final int DATASTART = 14;
    	public static final int DATAEND = 15;
    	public static final int SYSTEM_ID = 16;
    }
	public final static boolean PRINT_SENDS = true;
	
	LabProSensorDevice labProDevice;
	
	public LabProProtocol(LabProSensorDevice device)
	{
		labProDevice = device;
	}
	
	protected void sendCommand(int command, String params) 
		throws SerialException		
	{
		if(params != null){
			params = "," + params;
		} else {
			params = "";
		}
		String cmdStr = "s{" + command + params + "}\n";

		if(PRINT_SENDS){
			System.out.print("sending: " + cmdStr);
		}
		
		byte [] strBytes = cmdStr.getBytes();
		// this might not work in waba
		labProDevice.getPort().write(strBytes);		
	}

	public void requestSystemStatus() 
		throws SerialException
	{
		sendCommand(LabProProtocol.CMD.REQUEST_SYSTEM_STATUS, null);
	}
	
	public void requestChannelStatus(int channel, int reqtype)
	    throws SerialException
	{
		sendCommand(LabProProtocol.CMD.REQUEST_CHANNEL_STATUS, "" + channel + ",0");
	}

	public void channelSetup(int channel, int operation) 
		throws SerialException
	{
		sendCommand(LabProProtocol.CMD.CHANNEL_SETUP, "" + channel + "," + operation);
	}
	
	public void channelSetup(int channel, int operation, int postProc,
			int delta, int equ) 
	throws SerialException
	{
		sendCommand(LabProProtocol.CMD.CHANNEL_SETUP, "" + channel + "," + operation + "," +
				postProc + "," + delta + "," + equ);
	}	
	
	public void dataCollectionSetupPrevious() 
		throws SerialException
	{
		sendCommand(LabProProtocol.CMD.DATA_COLLECTION_SETUP, "-1");
	}

	public void dataCollectionSetup(float samptime) 
		throws SerialException
	{
		sendCommand(LabProProtocol.CMD.DATA_COLLECTION_SETUP, "" + samptime);
	}
	
	public void dataCollectionSetup(float samptime, int numpoints, int trigtype) 
	throws SerialException
	{
		sendCommand(LabProProtocol.CMD.DATA_COLLECTION_SETUP, "" 
				+ samptime + "," + numpoints + "," + trigtype);
	}

	public void portPowerControl(int powerControl) 
		throws SerialException
	{
		sendCommand(CMD.PORT_POWER_CONTROL_COMMAND, 
				"" + powerControl);
	}
	
	public void reset() 
		throws SerialException
	{
		sendCommand(LabProProtocol.CMD.RESET, null);
	}

	private byte [] wakeUpBytes = new byte [] { 's', '\n'};
	public void wakeUp()
		throws SerialException
	{
		if(PRINT_SENDS){
			System.out.println("s");
		}
		labProDevice.getPort().write(wakeUpBytes);
		labProDevice.getDeviceService().sleep(100);
	}
	

}
