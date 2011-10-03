package org.concord.sensor.labprousb.test;

import org.concord.sensor.labprousb.LabProUSB;

public class LabProUSBTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		System.loadLibrary("labprousb_wrapper");
		
		// try opening the labpro and check the result
		short ret = LabProUSB.open();
		System.out.println("opened ret: " + ret);
		
		ret = LabProUSB.close();
		System.out.println("closed ret: " + ret);
	}

}
