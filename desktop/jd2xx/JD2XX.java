/*
	Copyright (c) 2004, 2006, 2007 Pablo Bleyer Kocik.

	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions are met:

	1. Redistributions of source code must retain the above copyright notice, this
	list of conditions and the following disclaimer.

	2. Redistributions in binary form must reproduce the above copyright notice,
	this list of conditions and the following disclaimer in the documentation
	and/or other materials provided with the distribution.

	3. The name of the author may not be used to endorse or promote products
	derived from this software without specific prior written permission.

	THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
	WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
	EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
	SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
	BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
	IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
	ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
	POSSIBILITY OF SUCH DAMAGE.
*/

package jd2xx;

import geogebra.main.AppD;

import java.io.IOException;
import java.util.TooManyListenersException;

/** Java D2XX class */
public class JD2XX implements Runnable {

	/* Device status */
	public static final int
		OK = 0,
		INVALID_HANDLE = 1,
		DEVICE_NOT_FOUND = 2,
		DEVICE_NOT_OPENED = 3,
		IO_ERROR = 4,
		INSUFFICIENT_RESOURCES = 5,
		INVALID_PARAMETER = 6,
		INVALID_BAUD_RATE = 7,
		DEVICE_NOT_OPENED_FOR_ERASE = 8,
		DEVICE_NOT_OPENED_FOR_WRITE = 9,
		FAILED_TO_WRITE_DEVICE = 10,
		EEPROM_READ_FAILED = 11,
		EEPROM_WRITE_FAILED = 12,
		EEPROM_ERASE_FAILED = 13,
		EEPROM_NOT_PRESENT = 14,
		EEPROM_NOT_PROGRAMMED = 15,
		INVALID_ARGS = 16,
		NOT_SUPPORTED = 17,
		OTHER_ERROR = 18,
		DEVICE_LIST_NOT_READY = 19;

	/* openEx flags */
	public static final int
		OPEN_BY_SERIAL_NUMBER = 1<<0,
		OPEN_BY_DESCRIPTION = 1<<1,
		OPEN_BY_LOCATION = 1<<2;

	/* listDevices flags (used in conjunction with openEx flags) */
	public static final int
		LIST_NUMBER_ONLY = 1<<31,
		LIST_BY_INDEX = 1<<30,
		LIST_ALL = 1<<29,
		LIST_MASK = LIST_NUMBER_ONLY|LIST_BY_INDEX|LIST_ALL;

	/* Baud rates */
	public static final int
		BAUD_300 = 300,
		BAUD_600 = 600,
		BAUD_1200 = 1200,
		BAUD_2400 = 2400,
		BAUD_4800 = 4800,
		BAUD_9600 = 9600,
		BAUD_14400 = 14400,
		BAUD_19200 = 19200,
		BAUD_38400 = 38400,
		BAUD_57600 = 57600,
		BAUD_115200 = 115200,
		BAUD_230400 = 230400,
		BAUD_460800 = 460800,
		BAUD_921600 = 921600;

	/* Word lengths */
	public static final int
		BITS_8 = 8,
		BITS_7 = 7,
		BITS_6 = 6,
		BITS_5 = 5;

	/* Stop bits */
	public static final int
		STOP_BITS_1 = 0,
		STOP_BITS_1_5 = 1,
		STOP_BITS_2 = 2;

	/* Parity */
	public static final int
		PARITY_NONE = 0,
		PARITY_ODD = 1,
		PARITY_EVEN = 2,
		PARITY_MARK = 3,
		PARITY_SPACE = 4;

	/* Flow control */
	public static final int
		FLOW_NONE = 0,
		FLOW_RTS_CTS = 1<<8,
		FLOW_DTR_DSR = 1<<9,
		FLOW_XON_XOFF = 1<<10;

	/* Purge rx and tx buffers */
	public static final int
		PURGE_RX = 1<<0,
		PURGE_TX = 1<<1;

	/* Events */
	public static final int
		EVENT_RXCHAR = 1<<0,
		EVENT_MODEM_STATUS = 1<<1,
		EVENT_LINE_STATUS = 1<<2;

	/* Timeouts */
	public static final int
		DEFAULT_RX_TIMEOUT = 300,
		DEFAULT_TX_TIMEOUT = 300;

	/* Device types */
	public static final int
		DEVICE_BM = 0,
		DEVICE_AM = 1,
		DEVICE_100AX = 2,
		DEVICE_UNKNOWN = 3,
		DEVICE_2232C = 4,
		DEVICE_232R = 5,
    DEVICE_2232H = 6,
    DEVICE_4232H = 7,
    DEVICE_232H = 8;

  /* Device information flags */
	public static final int
  	FLAGS_OPENED = 1,
    FLAGS_HISPEED = 2;


	/* Bit modes */
	public static final int
		BITMODE_RESET = 0x00,
		BITMODE_ASYNC_BITBANG = 0x01,
		BITMODE_MPSSE = 0x02,
		BITMODE_SYNC_BITBANG = 0x04,
		BITMODE_MCU_HOST = 0x08,
		BITMODE_FAST_SERIAL = 0x10,
		BITMODE_CBUS_BITBANG = 0x20,
		BITMODE_SYNC_FIFO = 0x40;

	/* FT232R CBUS Options EEPROM values */
	public static final int
		FT232R_CBUS_TXDEN = 0x00,	//	Tx Data Enable
		FT232R_CBUS_PWRON = 0x01,	//	Power On
		FT232R_CBUS_RXLED = 0x02,	//	Rx LED
		FT232R_CBUS_TXLED = 0x03,	//	Tx LED
		FT232R_CBUS_TXRXLED = 0x04,	//	Tx and Rx LED
		FT232R_CBUS_SLEEP = 0x05,	//	Sleep
		FT232R_CBUS_CLK48 = 0x06,	//	48MHz clock
		FT232R_CBUS_CLK24 = 0x07,	//	24MHz clock
		FT232R_CBUS_CLK12 = 0x08,	//	12MHz clock
		FT232R_CBUS_CLK6 = 0x09,	//	6MHz clock
		FT232R_CBUS_IOMODE = 0x0A,	//	IO Mode for CBUS bit-bang
		FT232R_CBUS_BITBANG_WR = 0x0B,	//	Bit-bang write strobe
		FT232R_CBUS_BITBANG_RD = 0x0C;	//	Bit-bang read strobe

	/* FT232H CBUS Options EEPROM values */
	public static final int
		FT232H_CBUS_TRISTATE = 0x00,	//	Tristate
		FT232H_CBUS_TXLED = 0x01,	//	Tx LED
		FT232H_CBUS_RXLED = 0x02,	//	Rx LED
		FT232H_CBUS_TXRXLED = 0x03,	//	Tx and Rx LED
		FT232H_CBUS_PWREN = 0x04,	//	Power Enable
		FT232H_CBUS_SLEEP = 0x05,	//	Sleep
		FT232H_CBUS_DRIVE_0 = 0x06,	//	Drive pin to logic 0
		FT232H_CBUS_DRIVE_1 = 0x07,	//	Drive pin to logic 1
		FT232H_CBUS_IOMODE = 0x08,	//	IO Mode for CBUS bit-bang
		FT232H_CBUS_TXDEN = 0x09,	//	Tx Data Enable
		FT232H_CBUS_CLK30 = 0x0A,	//	30MHz clock
		FT232H_CBUS_CLK15 = 0x0B,	//	15MHz clock
		FT232H_CBUS_CLK7_5 = 0x0C;	//	7.5MHz clock

	/** EEPROM programming interface */
	public static class ProgramData {
		public int signature1; // 0x0000_0000
		public int signature2; // 0xffff_ffff
		// FT_PROGRAM_DATA version
		// 0 = original, 1 = FT2232C, 2 = FT232R, 3 = FT2232H, 4 = FT4232H, 5 = FT232H
		public int version;

		public int vendorID; // 0x0403
		public int productID; // 0x6001
		public String manufacturer; // "FTDI"
		public String manufacturerID; // "FT"
		public String description; // "USB HS Serial Converter"
		public String serialNumber; // "FT000001" if fixed, or NULL
		public int maxPower; // 0 < MaxPower <= 500
		public boolean pnp; // 0 = disabled, 1 = enabled
		public boolean selfPowered; // 0 = bus powered, 1 = self powered
		public boolean remoteWakeup; // 0 = not capable, 1 = capable

		/* Rev4 extensions */
		public boolean rev4; // non-zero if Rev4 chip, zero otherwise
		public boolean isoIn; // non-zero if in endpoint is isochronous
		public boolean isoOut; // non-zero if out endpoint is isochronous
		public boolean pullDownEnable; // non-zero if pull down enabled
		public boolean serNumEnable; // non-zero if serial number to be used
		public boolean usbVersionEnable; // non-zero if chip uses USBVersion
		public int usbVersion; // BCD (0x0200 => USB2)

		/* FT2232C extensions */
		public boolean rev5; // non-zero if Rev5 chip, zero otherwise
		public boolean isoInA; // non-zero if in endpoint is isochronous
		public boolean isoInB; // non-zero if in endpoint is isochronous
		public boolean isoOutA; // non-zero if out endpoint is isochronous
		public boolean isoOutB; // non-zero if out endpoint is isochronous
		public boolean pullDownEnable5; // non-zero if pull down enabled
		public boolean serNumEnable5; // non-zero if serial number to be used
		public boolean usbVersionEnable5; // non-zero if chip uses USBVersion
		public int usbVersion5; // BCD (0x0200 => USB2)
		public boolean aIsHighCurrent; // non-zero if interface is high current
		public boolean bIsHighCurrent; // non-zero if interface is high current
		public boolean ifAIsFifo; // non-zero if interface is 245 FIFO
		public boolean ifAIsFifoTar; // non-zero if interface is 245 FIFO CPU target
		public boolean ifAIsFastSer; // non-zero if interface is Fast serial
		public boolean aIsVCP; // non-zero if interface is to use VCP drivers
		public boolean ifBIsFifo; // non-zero if interface is 245 FIFO
		public boolean ifBIsFifoTar; // non-zero if interface is 245 FIFO CPU target
		public boolean ifBIsFastSer; // non-zero if interface is Fast serial
		public boolean bIsVCP; // non-zero if interface is to use VCP drivers

		/* FT232R extensions */
		public boolean useExtOsc; // Use External Oscillator
		public boolean highDriveIOs; // High Drive I/Os
		public int endpointSize; // Endpoint size
		public boolean pullDownEnableR; // non-zero if pull down enabled
		public boolean serNumEnableR; // non-zero if serial number to be used
		public boolean invertTXD; // non-zero if invert TXD
		public boolean invertRXD; // non-zero if invert RXD
		public boolean invertRTS; // non-zero if invert RTS
		public boolean invertCTS; // non-zero if invert CTS
		public boolean invertDTR; // non-zero if invert DTR
		public boolean invertDSR; // non-zero if invert DSR
		public boolean invertDCD; // non-zero if invert DCD
		public boolean invertRI; // non-zero if invert RI
		public int cbus0; // Cbus Mux control
		public int cbus1; // Cbus Mux control
		public int cbus2; // Cbus Mux control
		public int cbus3; // Cbus Mux control
		public int cbus4; // Cbus Mux control

		public boolean rIsD2XX; // non-zero if using D2XX driver

		/* Rev 7 (FT2232H) Extensions */
		public boolean pullDownEnable7;		// non-zero if pull down enabled
		public boolean serNumEnable7;		// non-zero if serial number to be used
		public boolean alSlowSlew;			// non-zero if AL pins have slow slew
		public boolean alSchmittInput;		// non-zero if AL pins are Schmitt input
		public int alDriveCurrent;		// valid values are 4mA, 8mA, 12mA, 16mA
		public boolean ahSlowSlew;			// non-zero if AH pins have slow slew
		public boolean ahSchmittInput;		// non-zero if AH pins are Schmitt input
		public int ahDriveCurrent;		// valid values are 4mA, 8mA, 12mA, 16mA
		public boolean blSlowSlew;			// non-zero if BL pins have slow slew
		public boolean blSchmittInput;		// non-zero if BL pins are Schmitt input
		public int blDriveCurrent;		// valid values are 4mA, 8mA, 12mA, 16mA
		public boolean bhSlowSlew;			// non-zero if BH pins have slow slew
		public boolean bhSchmittInput;		// non-zero if BH pins are Schmitt input
		public int bhDriveCurrent;		// valid values are 4mA, 8mA, 12mA, 16mA
		public boolean ifAIsFifo7;			// non-zero if interface is 245 FIFO
		public boolean ifAIsFifoTar7;		// non-zero if interface is 245 FIFO CPU target
		public boolean ifAIsFastSer7;		// non-zero if interface is Fast serial
		public boolean aIsVCP7;				// non-zero if interface is to use VCP drivers
		public boolean ifBIsFifo7;			// non-zero if interface is 245 FIFO
		public boolean ifBIsFifoTar7;		// non-zero if interface is 245 FIFO CPU target
		public boolean ifBIsFastSer7;		// non-zero if interface is Fast serial
		public boolean bIsVCP7;				// non-zero if interface is to use VCP drivers
		public boolean powerSaveEnable;		// non-zero if using BCBUS7 to save power for self-powered designs

		/* Rev 8 (FT4232H) Extensions */
		public boolean pullDownEnable8;		// non-zero if pull down enabled
		public boolean serNumEnable8;		// non-zero if serial number to be used
		public boolean aSlowSlew;			// non-zero if AL pins have slow slew
		public boolean aSchmittInput;		// non-zero if AL pins are Schmitt input
		public int aDriveCurrent;		// valid values are 4mA, 8mA, 12mA, 16mA
		public boolean bSlowSlew;			// non-zero if AH pins have slow slew
		public boolean bSchmittInput;		// non-zero if AH pins are Schmitt input
		public int bDriveCurrent;		// valid values are 4mA, 8mA, 12mA, 16mA
		public boolean cSlowSlew;			// non-zero if BL pins have slow slew
		public boolean cSchmittInput;		// non-zero if BL pins are Schmitt input
		public int cDriveCurrent;		// valid values are 4mA, 8mA, 12mA, 16mA
		public boolean dSlowSlew;			// non-zero if BH pins have slow slew
		public boolean dSchmittInput;		// non-zero if BH pins are Schmitt input
		public int dDriveCurrent;		// valid values are 4mA, 8mA, 12mA, 16mA
		public boolean aRIIsTXDEN;			// non-zero if port A uses RI as RS485 TXDEN
		public boolean bRIIsTXDEN;			// non-zero if port B uses RI as RS485 TXDEN
		public boolean cRIIsTXDEN;			// non-zero if port C uses RI as RS485 TXDEN
		public boolean dRIIsTXDEN;			// non-zero if port D uses RI as RS485 TXDEN
		public boolean aIsVCP8;				// non-zero if interface is to use VCP drivers
		public boolean bIsVCP8;				// non-zero if interface is to use VCP drivers
		public boolean cIsVCP8;				// non-zero if interface is to use VCP drivers
		public boolean dIsVCP8;				// non-zero if interface is to use VCP drivers

		/* Rev 9 (FT232H) Extensions */
		public boolean pullDownEnableH;		// non-zero if pull down enabled
		public boolean serNumEnableH;		// non-zero if serial number to be used
		public boolean acSlowSlewH;			// non-zero if AC pins have slow slew
		public boolean acSchmittInputH;		// non-zero if AC pins are Schmitt input
		public int acDriveCurrentH;		// valid values are 4mA, 8mA, 12mA, 16mA
		public boolean adSlowSlewH;			// non-zero if AD pins have slow slew
		public boolean adSchmittInputH;		// non-zero if AD pins are Schmitt input
		public int adDriveCurrentH;		// valid values are 4mA, 8mA, 12mA, 16mA
		public int cbus0H;				// Cbus Mux control
		public int cbus1H;				// Cbus Mux control
		public int cbus2H;				// Cbus Mux control
		public int cbus3H;				// Cbus Mux control
		public int cbus4H;				// Cbus Mux control
		public int cbus5H;				// Cbus Mux control
		public int cbus6H;				// Cbus Mux control
		public int cbus7H;				// Cbus Mux control
		public int cbus8H;				// Cbus Mux control
		public int cbus9H;				// Cbus Mux control
		public boolean isFifoH;				// non-zero if interface is 245 FIFO
		public boolean isFifoTarH;			// non-zero if interface is 245 FIFO CPU target
		public boolean isFastSerH;			// non-zero if interface is Fast serial
		public boolean isFt1248H;			// non-zero if interface is FT1248
		public boolean ft1248CpolH;			// FT1248 clock polarity - clock idle high (1) or clock idle low (0)
		public boolean ft1248LsbH;			// FT1248 data is LSB (1) or MSB (0)
		public boolean ft1248FlowControlH;	// FT1248 flow control enable
		public boolean isVCPH;				// non-zero if interface is to use VCP drivers
		public boolean powerSaveEnableH;		// non-zero if using ACBUS7 to save power for self-powered designs


		public ProgramData() {
			signature1 = (int)0x00000000;
			signature2 = (int)0xffffffff;
		}

		public String toString() {
			StringBuffer b = new StringBuffer();
			b.append("signature1: 0x" + Integer.toHexString(signature1));
			b.append(", signature2: 0x" + Integer.toHexString(signature2));
			b.append(", version: 0x" + Integer.toHexString(version));
			b.append(", vendorID: 0x" + Integer.toHexString(vendorID));
			b.append(", productID: 0x" + Integer.toHexString(productID));
			b.append(", manufacturer: " + manufacturer);
			b.append(", manufacturerID: " + manufacturerID);
			b.append(", description: " + description);
			b.append(", serialNumber: " + serialNumber);
			b.append(", maxPower: " + maxPower);
			b.append(", pnp: " + pnp);
			b.append(", selfPowered: " + selfPowered);
			b.append(", remoteWakeup: " + remoteWakeup);
			b.append(", rev4: " + rev4);
			b.append(", isoIn: " + isoIn);
			b.append(", isoOut: " + isoOut);
			b.append(", pullDownEnable: " + pullDownEnable);
			b.append(", serNumEnable: " + serNumEnable);
			b.append(", usbVersionEnable: " + usbVersionEnable);
			b.append(", usbVersion: 0x" + Integer.toHexString(usbVersion));
			b.append(", rev5: " + rev5);
			b.append(", isoInA: " + isoInA);
			b.append(", isoInB: " + isoInB);
			b.append(", isoOutA: " + isoOutA);
			b.append(", isoOutB: " + isoOutB);
			b.append(", pullDownEnable5: " + pullDownEnable5);
			b.append(", serNumEnable5: " + serNumEnable5);
			b.append(", usbVersionEnable5: " + usbVersionEnable5);
			b.append(", usbVersion5: 0x" + Integer.toHexString(usbVersion5));
			b.append(", aIsHighCurrent: " + aIsHighCurrent);
			b.append(", bIsHighCurrent: " + bIsHighCurrent);
			b.append(", ifAIsFifo: " + ifAIsFifo);
			b.append(", ifAIsFifoTar: " + ifAIsFifoTar);
			b.append(", ifAIsFastSer: " + ifAIsFastSer);
			b.append(", aIsVCP: " + aIsVCP);
			b.append(", ifBIsFifo: " + ifBIsFifo);
			b.append(", ifBIsFifoTar: " + ifBIsFifoTar);
			b.append(", ifBIsFastSer: " + ifBIsFastSer);
			b.append(", bIsVCP: " + bIsVCP);
			b.append(", useExtOsc: " + useExtOsc);
			b.append(", highDriveIOs: " + highDriveIOs);
			b.append(", endpointSize: " + endpointSize);
			b.append(", pullDownEnableR: " + pullDownEnableR);
			b.append(", serNumEnableR: " + serNumEnableR);
			b.append(", invertTXD: " + invertTXD);
			b.append(", invertRXD: " + invertRXD);
			b.append(", invertRTS: " + invertRTS);
			b.append(", invertCTS: " + invertCTS);
			b.append(", invertDTR: " + invertDTR);
			b.append(", invertDSR: " + invertDSR);
			b.append(", invertDCD: " + invertDCD);
			b.append(", invertRI: " + invertRI);
			b.append(", cbus0: 0x" + Integer.toHexString(cbus0));
			b.append(", cbus1: 0x" + Integer.toHexString(cbus1));
			b.append(", cbus2: 0x" + Integer.toHexString(cbus2));
			b.append(", cbus3: 0x" + Integer.toHexString(cbus3));
			b.append(", cbus4: 0x" + Integer.toHexString(cbus4));
			b.append(", rIsD2XX: " + rIsD2XX);

			return b.toString();
		}
	}

	/** Device information */
	public static class DeviceInfo {
		public int index; // device index in info list
		public int flags; // device flags
		public int type; // device type
		public int id; // device ID
		public int location; // device location ID
		public String serial;
		public String description;
		public int handle; // device handle

		public String toString() {
			StringBuffer b = new StringBuffer();
			b.append("index: " + Integer.toString(index));
			b.append(", flags: 0x" + Integer.toHexString(flags));
			b.append(", type: 0x" + Integer.toHexString(type));
			b.append(", id: 0x" + Integer.toHexString(id));
			b.append(", location: 0x" + Integer.toHexString(location));
			b.append(", serial: " + serial);
			b.append(", description: " + description);
			b.append(", handle: 0x" + Integer.toHexString(handle));
			return b.toString();
		}
	}

	/* D2XX API */
	/** Get library version */
	public native int getLibraryVersion();

	/** Builds a device information list and returns the number of D2XX devices
	connected to the system. The list contains information about both unopen and
	open devices.
	*/
	public native int createDeviceInfoList();

	/** Returns an entry from the device information list
		@return DeviceInfo object with device information details
	*/
	public native DeviceInfo getDeviceInfoDetail(int dn) throws IOException;

	/** Open device by number and associate it to this JD2XX object
		@param deviceNumber device enumeration
	*/
	public native void open(int deviceNumber) throws IOException;
	/** Close device
	*/
	public native void close() throws IOException;
	/** List devices
		@param flags control how devices are listed
		@return device information list as array of objects (types depend on flag)
	*/
	public native Object[] listDevices(int flags) throws IOException;
	/** Extended open (by name)
		@param name device serial number or description
		@param flags selects open from serial number or description
	*/
	public native void openEx(String name, int flags) throws IOException;
	/** Extended open (by number)
		@param location device location
		@param flags selects open by location
	*/
	public native void openEx(int location, int flags) throws IOException;

	/** Read bytes from device
		@param bytes array to store read bytes
		@param offset begin index
		@param length amount of bytes desired
		@return number of bytes actually read
	*/
	public native int read(byte[] bytes, int offset, int length) throws IOException;
	/** Write bytes to device
		@param bytes array with bytes to be sent
		@param offset begin index
		@param length amount of bytes desired
		@return number of bytes actually written
	*/
	public native int write(byte[] bytes, int offset, int length) throws IOException;

	// public native void ioCtl(...);

	/** Set device baud rate
		@param baudRate baud rate
	*/
	public native void setBaudRate(int baudRate) throws IOException;
	/** Set device divisor
		@param divisor divisor number
	*/
	public native void setDivisor(int divisor) throws IOException;
	/** Set device communication characteristics
		@param wordLength number of data bits
		@param stopBits number of stop bits
		@param parity parity method
	*/
	public native void setDataCharacteristics(int wordLength, int stopBits, int parity) throws IOException;
	/** Set device flow control type
		@param flowControl flow control mode
		@param xonChar XON character (software handshake)
		@param xoffChar XOFF character (software handshake)
	*/
	public native void setFlowControl(int flowControl, int xonChar, int xoffChar) throws IOException;
	/** Reset device */
	public native void resetDevice() throws IOException;
	/** Set DTR signal */
	public native void setDtr() throws IOException;
	/** Clear DTR signal */
	public native void clrDtr() throws IOException;
	/** Set RTS signal */
	public native void setRts() throws IOException;
	/** Clear RTS signal */
	public native void clrRts() throws IOException;
	/** Get modem status
		@return device modem status
	*/
	public native int getModemStatus() throws IOException;
	/** Set device special characters
		@param eventChar event character
		@param eventCharEn enable/disable event character
		@param errorChar error character
		@param errorCharEn enable/disable error character
	*/
	public native void setChars(int eventChar, boolean eventCharEn, int errorChar, boolean errorCharEn) throws IOException;
	/** Purge device queues
		@param mask selects queue(s) to purge
	*/
	public native void purge(int mask) throws IOException;
	/** Set device timeouts
		@param readTimeout timeout for reads
		@param writeTimeout timeout for writes
	*/
	public native void setTimeouts(int readTimeout, int writeTimeout) throws IOException;

	/** Get queue status
		@return number of characters in the receive queue
	*/
	public native int getQueueStatus() throws IOException;
	/** Turn on break in device */
	public native void setBreakOn() throws IOException;
	/** Turn off break in device */
	public native void setBreakOff() throws IOException;
	/** Get device status
		@return array with number of bytes in RX queue, TX queue and event status
	*/
	public native int[] getStatus() throws IOException;

	/** Set event notification mask
		@param mask event mask
		@param handle native event handle
	*/
	public native void setEventNotification(int mask, int handle) throws IOException;
	/** Set wait mask
		@param mask wait mask
	*/
	public native void setWaitMask(int mask) throws IOException;
	/** Wait on mask
		@return mask
	*/
	public native int waitOnMask() throws IOException;
	/** Get event status
		@return event status
	*/
	public native int getEventStatus() throws IOException;

	/** Read word value from EEPROM
		@param wordOffset word address offset
		@return word value (2 bytes)
	*/
	public native short readEE(int wordOffset) throws IOException;
	/** Write word value to EEPROM
		@param wordOffset word address offset
		@param value word value (2 bytes)
	*/
	public native void writeEE(int wordOffset, short value) throws IOException;
	/** Clear EEPROM */
	public native void eraseEE() throws IOException;

	/** Program EEPROM
		@param data ProgramData object holding device information
	*/
	public native void eeProgram(ProgramData data) throws IOException;
	/** Extended program EEPROM
		@param data ProgramData object holding device information
		@param manufacturer device manufacturer string
		@param manufacturerId device manufacturerId string
		@param description device description string
		@param serialNumber device serial number string
	*/
	public native void eeProgramEx(
		ProgramData data,
		String manufacturer, String manufacturerId,
		String description, String serialNumber
	) throws IOException;
	/** Read device information from EEPROM
		@return ProgramData object with device information
	*/
	public native ProgramData eeRead() throws IOException;
	/** Extended read device information from EEPROM
		@param manufacturer device manufacturer string
		@param manufacturerId device manufacturerId string
		@param description device description string
		@param serialNumber device serial number string
		@return ProgramData object with device information
	*/
	public native ProgramData eeReadEx(
		String manufacturer, String manufacturerId,
		String description, String serialNumber
	) throws IOException;

	/** Get EEPROM user area size
		@return user area size in bytes
	*/
	public native int eeUASize() throws IOException;
	/** Write bytes to EEPROM user area
		@param uaData array with bytes to write to EEPROM user area
	*/
	public native void eeUAWrite(byte[] uaData) throws IOException;
	/** Read bytes from EEPROM user area
		@param numBytes number of bytes to read
		@return array of bytes read from EEPROM user area
	*/
	public native byte[] eeUARead(int numBytes) throws IOException;

	/** Set device latency timer
		@param time timer value in milliseconds (2-255)
	*/
	public native void setLatencyTimer(int time) throws IOException;
	/** Get device latency timer
		@return timer value in milliseconds (2-255)
	*/
	public native int getLatencyTimer() throws IOException;

	/** Set device bit mode
		@param mask bit mode mask
		@param enable enable/disable bit mode
	*/
	public native void setBitMode(int mask, int mode) throws IOException;
	/** Get device bit mode
		@return bit mode value
	*/
	public native int getBitMode() throws IOException;

	/** Set device USB parameters
		@param inputSize transfer size for input request
		@param outputSize transfer size for output request
	*/
	public native void setUSBParameters(int inputSize, int outputSize) throws IOException;

	/** Set deadman timeout
		@param timeout deadman timeout
	*/
	public native void setDeadmanTimeout(int timeout) throws IOException;

	/** Get device information
		@return DeviceInfo object with device information
	*/
	public native DeviceInfo getDeviceInfo() throws IOException;

	/** Stop the driver's IN task */
	public native void stopInTask() throws IOException;
	/** Restarts the driver's IN task */
	public native void restartInTask() throws IOException;
	/** Set the reset pipe retry amount
		@param count count value
	*/
	public native void setResetPipeRetryCount(int count) throws IOException;
	/** Send reset command to port */
	public native void resetPort() throws IOException;
	/** Send cycle command to port */
	public native void cyclePort() throws IOException;
	/** Get device driver version */
	public native int getDriverVersion();

	/** Rescan devices */
	public native void rescan();

	/** Reload device with specific VID and PID */
	public native void reload(int vid, int pid);

	/** Get COM port number */
	public native int getComPortNumber() throws IOException;

	/* FT232H additional EEPROM functions */
	public native int eeReadConfig(int address) throws IOException;
	public native void eeWriteConfig(int address, int value) throws IOException;
	public native int eeReadEcc(int option) throws IOException;
	public native int getQueueStatusEx() throws IOException;

	/** Add event listener
		@param el JD2XX event listener object
	*/
	// public native void addEventListener(JD2XXEventListener el) throws TooManyListenersException;
	/** Remove event listener */
	// public native void removeEventListener();
	/** Set notify event and event mask */
	public native void registerEvent(int m) throws IOException;
	public synchronized native void signalEvent();
	public native int waitEvent();


	/** Internal FT_HANDLE */
	protected int handle = -1;
	/** Internal event handle */
	protected int event = -1;
	/** Internal event mask */
	protected int mask = 0;
	/** Kill notifier thread */
	protected boolean kill = false;
	/** Event listener object */
	protected JD2XXEventListener listener = null;
	/** Listener notifier thread */
	protected Thread notifier = null;

	static {
		
		if (AppD.WINDOWS) {
		
			String arch = System.getenv("PROCESSOR_ARCHITECTURE");
			if ((arch != null) && ((arch.equals("AMD64")) || (arch.equals("IA64")))) {
				System.loadLibrary("jd2xx_64");
			} else {
				System.loadLibrary("jd2xx");
			}
		
		} else {
			AppD.printStacktrace("Linux / Mac support for JD2XX not enabled");
		}
	}

	/** Create a new unopened JD2XX object */
	public JD2XX() {
	}

	/** Create a new JD2XX object and open device by number */
	public JD2XX(int deviceNumber) throws IOException {
		open(deviceNumber);
	}

	/** Create a new JD2XX object and open device by serial number or description */
	public JD2XX(String name, int flags) throws IOException {
		openEx(name, flags);
	}

	/** Create a new JD2XX object and open device by location */
	public JD2XX(int location, int flags) throws IOException {
		openEx(location, flags);
	}

	protected void finalize()
	throws Throwable {
		try {
			// if (handle != 0) close();
			close();
		}
		finally {
			super.finalize();
		}
	}

	/** Open device by serial number alias */
	public void openBySerialNumber(String name) throws IOException {
		openEx(name, OPEN_BY_SERIAL_NUMBER);
	}

	/** Open device by description alias */
	public void openByDescription(String name) throws IOException {
		openEx(name, OPEN_BY_DESCRIPTION);
	}

	/** Open device by location alias */
	public void openByLocation(int location) throws IOException {
		openEx(location, OPEN_BY_LOCATION);
	}

	/** List devices by serial number alias */
	public Object[] listDevicesBySerialNumber() throws IOException {
		return listDevices(OPEN_BY_SERIAL_NUMBER);
	}

	/** List devices by serial description alias */
	public Object[] listDevicesByDescription() throws IOException {
		return listDevices(OPEN_BY_DESCRIPTION);
	}

	/** List devices by location alias */
	public Object[] listDevicesByLocation() throws IOException {
		return listDevices(OPEN_BY_LOCATION);
	}

	/** Read bytes from device helper function *
	public byte[] read(int s) throws IOException {
		byte[] b = new byte[s];
		int r = read(b);
		if (r == b.length) return b;
		else {
			byte[] c = new byte[r];
			System.arraycopy(b, 0, c, 0, r);
			return c;
		}
	}*/

	/** Read bytes from device helper function 
	 * 
	 *  changed to return String for eyes.py (from expEYES project)
	 * */
	public String read(int s) throws IOException {
		byte[] b = new byte[s];
		int r = read(b);
		
		String ret = new String(b);

		if (r == b.length) {
			return ret;
		}else {
			return ret.substring(0, r);
		}
	}

	/** Read bytes from device helper function */
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	/** Force read single byte from device */
	public int read() throws IOException {
		byte[] b = new byte[1];
		if (read(b) != 1) throw new IOException("io error");
		return b[0] & 0xff;
	}

	/** Write single byte to device */
	public int write(int b) throws IOException {
		byte[] c = new byte[1];
		c[0] = (byte)b;
		return write(c);
	}

	/** Write bytes to device helper function */
	public int write(byte b[]) throws IOException {
		return write(b, 0, b.length);
	}

	/** Add event listener
		@param el JD2XX event listener object
	*/
	public void addEventListener(JD2XXEventListener el) throws TooManyListenersException {
		if (listener == null) listener = el;
		else throw new TooManyListenersException();
	}
	/** Remove event listener */
	public void removeEventListener() {
		listener = null;
	}

	public void dispatchEvent(int et) {
		if (listener != null) listener.jd2xxEvent(new JD2XXEvent(this, et));
	}

	public void notifyOnEvent(int m, boolean v) throws IOException {
		int nm = (v) ? (mask | m) : (mask & ~m);

		if (nm != 0) {
			if (notifier != null) return;
			kill = false;
			notifier = new Thread(this);
			registerEvent(nm);
			notifier.start();
		}
		else {
			if (notifier == null) return;
			kill = true;
			signalEvent();
			// notifier.interrupt();
			while (notifier.isAlive()) ;
			registerEvent(0);
			notifier = null;
		}
		mask = nm;
	}

	/** Notify on RXCHAR event alias */
	public void notifyOnRxchar(boolean v) throws IOException {
		notifyOnEvent(EVENT_RXCHAR, v);
	}

	/** Notify on MODEM_STATUS event alias */
	public void notifyOnModemStatus(boolean v) throws IOException {
		notifyOnEvent(EVENT_MODEM_STATUS, v);
	}

	/** Notifier thread function */
	public void run() {
		while (true) {
			int et = waitEvent();
			if (kill) break;
			else dispatchEvent(et);
		}
	}

	public static void main(String[] args) throws IOException {
		JD2XX jd = new JD2XX();
		Object[] devs = jd.listDevicesBySerialNumber();
		for (int i=0; i<devs.length; ++i) System.out.println(devs[i]);
		devs = jd.listDevicesByDescription();
		for (int i=0; i<devs.length; ++i) System.out.println(devs[i]);
		devs = jd.listDevicesByLocation();
		for (int i=0; i<devs.length; ++i) System.out.println(Integer.toHexString((Integer)devs[i]));

		int n = jd.createDeviceInfoList();
		DeviceInfo di = jd.getDeviceInfoDetail(0);
		System.out.println(di.toString());

		jd.open(0);
		String msg = "Hello dude. This is the message.";
		int ret = jd.write(msg.getBytes());
		System.out.println(ret + " bytes sent.");

//		byte[] rd = jd.read(10);
//		System.out.println(rd);

		ProgramData pd = jd.eeRead();
		System.out.println(pd.toString());

		di = jd.getDeviceInfo();
		System.out.println(di.toString());

		try {
			jd.addEventListener(
				new JD2XXEventListener() {
					public void jd2xxEvent(JD2XXEvent ev) {
						JD2XX jo = (JD2XX)ev.getSource();
						int et = ev.getEventType();
						try {
							if ((et & EVENT_RXCHAR) != 0) {
								int r = jo.getQueueStatus();
								System.out.println("RX event: " + new String(jo.read(r)));
							}
							else if ((et & EVENT_MODEM_STATUS) != 0) {
								System.out.println("Modem status event");
							}
						}
						catch (IOException e) { }
					}
				}
			);
		}
		catch (TooManyListenersException e) { }
		jd.notifyOnEvent(EVENT_RXCHAR | EVENT_MODEM_STATUS, true);
	}

}
