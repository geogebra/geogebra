/* Help for eyes.py
 * '''
EYES for Young Engineers and Scientists (EYES 1.0)
Python library to communicate to the AtMega32 uC running 'eyes.c'
Author  : Ajith Kumar B.P, bpajith@gmail.com
License : GNU GPL version 3
Started on 1-Nov-2010

The hardware consisists of :
1) 2 Digital Inputs
2) 2 Digital Outputs
3) 2 DAC channels
4) 8 ADC channels (only 6 used)
       0,1 : -5V to 5V inputs
		 2 : 0 to 5V input

5) 1 Square wave generator using ATmega32
6) 1 Square wave generator using IC555 (frequency range selectable through Atmega32)
7) 1 Pulse Width Modulator Output using ATmega32
8) A 100 Hz sine wave generator, bipolar
9) 1 Current source controlled by DAC channel 1
10)1 Non-Inverting Amplifier using OP27, gain can be set by an external resistor
11)1 Inverting amplifier, gain can be selected using a series resistance at the input
12)2 Inverting amplifiers with gain = 47 , mainly used for microphones. 
'''

Converted to Java by Michael Borcherds
Started: 15th October 2011

needs J2DXX.dll in Java run path
and ftd2xx.dll (installed by CDM20814_Setup.exe)
 */

package geogebra.plugin.jd2xx;

import java.io.IOException;

import jd2xx.JD2XX;
import jd2xx.JD2XX.ProgramData;

public class Eyes {

	JD2XX fd = null;

	public static void main(String[] cmdArgs) {  


		Eyes e = new Eyes();
	}

	public Eyes() {  
		fd = new JD2XX();
		//List devices by serial number.
		Object[] devs;
		try {
			devs = fd.listDevicesBySerialNumber();
			for (int i=0; i<devs.length; ++i)
				System.out.println(devs[i]);
			//List devices by description.
			devs = fd.listDevicesByDescription();
			for (int i=0; i<devs.length; ++i)
				System.out.println(devs[i]);
			//List devices by port location.
			devs = fd.listDevicesByLocation();
			for (int i=0; i<devs.length; ++i)
				System.out.println(
						Integer.toHexString((Integer)devs[i])
						);

			fd.open(0);
			//Configure thy device.
			fd.setBaudRate(38400);
			fd.setDataCharacteristics(
					8, JD2XX.STOP_BITS_1, JD2XX.PARITY_EVEN
					);
			fd.setFlowControl(
					JD2XX.FLOW_NONE, 0, 0
					);
			fd.setTimeouts(300, 300);
			//Send a message.
			//String msg = "Hello Duke.";
			//msg += "The message is 'Fiat experimentum in corpore vili'";
			//int ret = jd.write(msg.getBytes());
			//System.out.println(ret + " bytes sent.");
			//Receive data.
			//byte[] rd = jd.read(10);
			//System.out.println(new String(rd));
			//Read device configuration EEPROM.
			ProgramData pd = fd.eeRead();
			System.out.println(pd.toString());
			//Java dudes do it with streams.
			//JD2XXInputStream ins = new JD2XXInputStream(jd);
			//JD2XXOutputStream outs = new JD2XXOutputStream(jd);
			//...
			//byte[] data = new byte[DATA_SIZE];
			//int ret = ins.read(data);
			//data = processData(data);
			//outs.write(data);
			//Finally, be polite.
			//ins.close();
			//outs.jd2xx.close();
			//outs.close();

			// put 1000Hz square wave on SQR1
			set_sqr1(1000);

			//pause
			Thread.sleep(1000);

			// turn it off
			set_sqr1(0);

			fd.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static double[] div = {4000000.0, 500000.0, 125000.0, 62500.0, 31250.0, 15625.0, 3906.25};

	final public static char SETCOUNTER0	=  82;	//# Square wave on OSC0 (SQR2)
	final public static char SETCOUNTER1	=  83;	//# Square wave on OSC2 (SQR1)

	private double set_sqr0(int freq) throws IOException { //:        # Sets Squarewave on the PULSE output
		return set_sqr(SETCOUNTER0, freq);
	}

	private double set_sqr1(int freq) throws IOException { //:        # Sets Squarewave on the PULSE output
		return set_sqr(SETCOUNTER1, freq);
	}

	private double set_sqr2(int freq) throws IOException { //:        # Sets Squarewave on the PULSE output
		return set_sqr(SETCOUNTER0, freq);
	}

	private double set_sqr(char counter, int freq) throws IOException { //:        # Sets Squarewave on the PULSE output
		/*'''
	Sets a square wave on the PULSE output. Frequency from 15Hz to 40000000 Hz (4 MHz), but 
	it is not possible to set all intermediate values. 
	The function sets the nearest possible value and returns it.
	'''*/
		if (freq < 1) {       //# Disable squarewave on PULSE
			fd.write(counter);
			fd.write(0);
			fd.write(0);
			fd.read(1);
			return 0;
		}

		byte clock_sel = 0;
		double freq0 = 0;

		for (int i = 0 ; i < 6 ; i++) {
			clock_sel = (byte) (i+1);
			freq0 = div[i];
			if ( freq0/ freq <= 256) break;
		}
		double setpoint = freq0/freq;
		if (setpoint > 255) setpoint = 255;
		byte OCR0 = (byte) (setpoint - 1);
		//Application.debug(clock_sel+" "+OCR0);
		fd.write((byte) counter);
		fd.write(clock_sel);
		fd.write(OCR0);

		byte[] res = fd.read(1);
		if (res[0] != 'D')
			return Double.NaN;
		if (setpoint == 0)
			return freq0;
		else
			return freq0/(OCR0+1);
	}

}
