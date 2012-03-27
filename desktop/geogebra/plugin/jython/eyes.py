'''
EYES for Young Engineers and Scientists (EYES 1.0)
Python library to communicate to the AtMega32 uC running 'eyes.c'
Author  : Ajith Kumar B.P, bpajith@gmail.com
License : GNU GPL version 3
Started on 1-Nov-2010

http://expeyes.in/sites/default/files/src/eyes.py.txt

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

#import serial
import struct, math, time, commands

#Commands with One byte argument (41 to 80) 
GETVERSION  =   1
DIGIN		= 	2	# Digital Input (4 bits)
USOUND	    =   3   # Pulse OD1 to get rising edge on ID2(internal)

#Commands with One byte argument (41 to 80) 
SETSAMTIME	=  41	# MCP3208 sampling duration
SETADCSIZE	=  42
READADC		=  43	#Read the specified ADC channel
R2FTIME		=  44	# Rise to Fall of signal on input pins
R2RTIME		=  45	# Rise to Fall of signal on input pins
F2RTIME		=  46	# Fall to Rise of signal on input pins
F2FTIME		=  47	# Fall to Rise of signal on input pins
SET2RTIME	=  48	# Setting of bit to rising edge
SET2FTIME	=  49	# to falling time
CLR2RTIME	=  50	# Setting of bit to rising edge
CLR2FTIME	=  51	# to falling time
PULSE2RTIME	=  52	# Pulse to rising edge
PULSE2FTIME	=  53	# Pulse to rising edge
SETPULSEWID	=  54	# width for PULSE2 functions (0 to 250)
SETPULSEPOL	=  55	# PULSE polarity (0 for HIGH true)
DIGOUT 		=  56	# Digital output (4 bits)
ADC2CMP		=  57	# Route ADC input to ACOMP-
SETPWM		=  58	# Set 488 Hz PWM wave on TC0
SETPWMDAC	=  59	# Set 31.25 kHz PWM wave on TC0
GETPORT		=  60	# PINX data from port X

# Commands with Two bytes argument (81 to 120)
SETPWM0		=  81	# PWM on on OSC0
SETCOUNTER0	=  82	# Square wave on OSC0
SETCOUNTER2	=  83	# Square wave on OSC2
SETACTION	=  84	# Capture Actions of SET/CLR type
MULTIR2R	=  85	# Rising edge to a rising edge after N cycles
ADCTRIGS	=  86	# Trigger levels for read_block functions
SETWAVEFORM	=  87	# ISR Wavegen. OCR0 and which DAC from the caller
PULSE_D0D1	=  88	# Interrupt driven square wave on D0 and D1
SETDDR		=  90	# DDRX = dirmask (arg1 = X, arg2 = mask)
SETPORT		=  91	# PORTX = DATA (arg1 = X, arg2 = DATA)

# Commands with Three bytes argument (121 to 160)    
SETDAC		= 121   # Serial DAC: send ch, dlo & dhi 
QCAPTURE01	= 122	# 2 bytes N, 1 byte dt. captures channel 0 and 1
WREEPROM	= 123	# Write EEPROM , 2 byte addr & 1 byte data
RDEEPROM	= 124	# Read EEPROM , 2 byte addr , 1 byte nb

#Commands with Four bytes argument (161 to 200)
CAPTURE01	= 161	 # 2 bytes N, 2 bytes dt. Capture channel 0 and 1
QCAPTURE 	= 162	 # Ch, 2 byte N, 1 byte dt. 

#Commands with Five bytes argument (201 to 240)
CAPTURE  	= 201	 # Ch, 2 byte N, 2 byte dt. Capture from MCP3208 ADC
CAPTURE_M32	= 202	 # Ch, 2 byte N, 2 byte dt. Capture from ATmega32 ADC

# Actions before capturing waveforms
ASET		= 1
ACLR		= 2
APULSEHI	= 3
APULSELO	= 4
AWAITHI		= 5
AWAITLO		= 6
AWAITRISE	= 7
AWAITFALL	= 8

BUFSIZE     = 1800       # status + adcinfo + 1800 data

# for Jython
from jd2xx import JD2XX

#Serial devices to search for EYES hardware.  
device_list = ['/dev/ttyUSB0', '/dev/ttyUSB1', '/dev/ttyUSB2', '/dev/ttyUSB3',\
		'/dev/tts/USB0','/dev/tts/USB1' ,0,1,2,3]

def open(dev = None):
	'''
	If EYES hardware in found, returns an instance of 'Eyes', else returns None.
	'''
	obj = Eyes()
	if obj.fd != None:
		obj.disable_actions()
		return obj
	print 'Could not find Phoenix-EYES hardware'
	print 'Check the connections.'

DACMAX = 4.993				# MCP4922 DAC goes only up to 4.933 volts
BAUDRATE = 38400			# Serial communication

class Eyes:
	#buf = array.array('B',BUFSIZE * [0])    # unsigned character array, Global
	fd = None								# init should fill this
	adcsize = 1
	m = [10.0/4095]*2 + [5.0/4095]*6 + [4095./DACMAX/2, 4095.0/DACMAX] # 8th and 9th are for DAC
	c = [-5.0]*2 + [0.0]*6 + [4095.0/2, 0]

	def __init__(self, dev = None):
		"""
		Searches for EYES hardware on RS232 ports and the USB-to-Serial adapters. Presence of the
		device is done by reading the version string.
		The timeout at Python end is set to 3.2 milliseconds, twice the minimum 555 output time period.
		TODO : Supporting more than one EYES on a PC to be done. The question is how to find out 
		whether a port is already open or not, without doing any transactions to it.
		"""
		self.adcsize = 2;
		
		# code modified by Michael Borcherds so that it works with Jython
		handle = JD2XX()
		handle.open(0)
		handle.setBaudRate(38400);
		handle.setDataCharacteristics(8, JD2XX.STOP_BITS_1, JD2XX.PARITY_EVEN);
		handle.setFlowControl(JD2XX.FLOW_NONE, 0, 0);
		
		self.fd = handle


#-------------------- Pulse Width Modulated Waveform on TC0 and TC2 ------------------
	def set_pwmdac(self, vout):        # Value in 0 to 5V
		'''
		Sets the PULSE output (T10) to 31.25 kHz and sets the duty cycle to make the
		average voltage = vout. Need External RC filter to use this as a DC output.
		0 to 5V range is covered in 255 steps and the function returns the value set.
		'''
		if 0 <= vout <= 5.0:
			val = int(vout*255.0/5.0)
			self.fd.write(chr(SETPWMDAC))
			self.fd.write(chr(val))
			self.fd.read(1)
			return val * 5.0 / 255

	def set_pulse(self, ds):        # Dutycycle in percentage
		'''
		Sets the frequency on PULSE to 488.3 Hz. Dutycycle is set to 'ds'. 
		Returns the actual value set.
		'''
		if 0 <= ds <= 100:
			val = int(ds*255.0/100)
			self.fd.write(chr(SETPWM))
			self.fd.write(chr(val))
			self.fd.read(1)
			return val * 100.0 / 255

#---------------- Square Wave Generation & Measuring the Frequency ------------------
	def set_sqr0(self, freq):        # Sets Squarewave on the PULSE output
		'''
		Sets a square wave on the PULSE output. Frequency from 15Hz to 40000000 Hz (4 MHz), but 
		it is not possible to set all intermediate values. 
		The function sets the nearest possible value and returns it.
		'''
		if freq < 1:        # Disable squarewave on PULSE
			self.fd.write(chr(SETCOUNTER0))
			self.fd.write(chr(0))
			self.fd.write(chr(0))
			self.fd.read(1)
			return 0

		div = [4000000.0, 500000.0, 125000.0, 62500.0, 31250.0,15625.0,3906.25]
		for i in range(7):
			clock_sel = i+1
			freq0 = div[i]
			if ( freq0/ freq) <= 256:
				break
		setpoint = freq0/freq
		if setpoint > 255:
			setpoint = 255
		OCR0 = int(setpoint)-1
		#print clock_sel, OCR2
		self.fd.write(chr(SETCOUNTER0))
		self.fd.write(chr(clock_sel))
		self.fd.write(chr(OCR0))
		res = self.fd.read(1)
		if res != 'D':
			return None
		if setpoint == 0:
			return freq0
		else:
			return freq0/(OCR0+1)

	def set_sqr1(self, freq):        # Freq in Hertz
		'''
		Sets the output frequency of the SQR1. Ranges from 15Hz to 40000000 Hz (4 MHz), but 
		it is not possible to set all intermediate values. 
		The function sets the nearest possible value and returns it.
		'''
		if freq < 1:        # Disable PWG
			self.fd.write(chr(SETCOUNTER2))
			self.fd.write(chr(0))
			self.fd.write(chr(0))
			self.fd.read(1)
			return 0

		div = [4000000.0, 500000.0, 125000.0, 62500.0, 31250.0,15625.0,3906.25]
		for i in range(7):
			clock_sel = i+1
			freq0 = div[i]
			if ( freq0/ freq) <= 256:
				break
		setpoint = freq0/freq
		if setpoint > 255:
			setpoint = 255
		OCR2 = int(setpoint)-1
		#print clock_sel, OCR2
		self.fd.write(chr(SETCOUNTER2))
		self.fd.write(chr(clock_sel))
		self.fd.write(chr(OCR2))
		res = self.fd.read(1)
		if res != 'D':
			return None
		if setpoint == 0:
			return freq0
		else:
			return freq0/(OCR2+1)

	def get_sqr1(self):
		'''
		This function measures the frequency of SQR1. There is no need of this
		since set_sqr1 returns the frequency actually set.
		'''
		self.adc2cmp(6)
		t = self.multi_r2rtime(4)
		if t < 10000:
			t = self.multi_r2rtime(4,9)
			return 1.0e7/t
		return 1.0e6 / t

	def set_sqr2(self, fmax):
		'''
		This function sets the frequency range of SQR2.
		The ranges are : 0.7 to 25, 25 to 1000, 1000 to 10000 and 10000 to 90000.
		You need to adjust the 22 KOhm variable resistor to get the desired frequency
		within the selected range. Software allows you to measure the frequency while 
		adjusting the resistor. Frequency can be set from .7 Hz to 90 KHz in different ranges.
		'''
		if fmax < 0:					#PA0 to LOW, makes 555 output LOW
			self.set_ddr(0,1)
			self.set_port(0,1)
		elif fmax == 0:					#PA0 to LOW, makes 555 output HIGH
			self.set_ddr(0,1)
			self.set_port(0,0)
		elif fmax<= 25:
			self.set_ddr(0, 2+4+8+16)	# connect (47 + 1 + 0.1 + 0.01) uF 
			self.set_port(0,0)
		elif fmax<= 1000:
			self.set_ddr(0, 2+4+8)		# connect (1 + 0.1 + 0.01) uF 
			self.set_port(0,0)
		elif fmax<= 10000:
			self.set_ddr(0, 2+4)		# connect (0.1 + 0.01) uF 
			self.set_port(0,0)
		elif fmax <= 90000:				# connect 0.01 uF
			self.set_ddr(0, 2)
			self.set_port(0,0)
		elif fmax > 300000:				# Oscllate with stray capacitance only
			self.set_ddr(0, 0)
			self.set_port(0,0)

	def get_sqr2(self):
		'''
		This function measures the frequency of SQR2 (555 oscillator).
		Call this while adjusting the frequency using the variable resistor.
		'''
		self.adc2cmp(6)
		t = self.multi_r2rtime(4)
		if t < 0:
			return t
		if 0 < t < 10000:
			t = self.multi_r2rtime(4,9)
			return 1.0e7/t
		return 1.0e6 / t

	def sensor_frequency(self):
		'''
		This function measures the frequency on the signal on SENS (T23) input.
		'''
		self.adc2cmp(5)
		t = self.multi_r2rtime(4)
		if t < 0:
			return t
		if 0 < t < 10000:
			t = self.multi_r2rtime(4,9)
			return 1.0e7/t
		return 1.0e6 / t

	def ampin_frequency(self):
		'''
		This function measures the frequency of an external BIPOLAR signal connected to Terminal 15.
		If your signal is unipolar , connect it through a 1uF series 
		The amplitude must be more than 100 mV
		'''
		return self.digin_frequency(2)	# Amplifier output is connected to PC2

	def digin_frequency(self, pin):
		'''
		This function measures the frequency of an external 0 to 5V PULSE on digital inputs.
		'''
		t = self.multi_r2rtime(pin)
		if t < 0:
			return t
		if 0 < t < 10000:
			t = self.multi_r2rtime(pin,9)
			return 1.0e7/t
		return 1.0e6 / t

#-------------------------------------- ADC & DAC Calibrations -----------------------------
	def eeprom_write_char(self,addr, dat):
		'''
		Writes one byte to the specified address of the EEPROM memory of ATmega32.
		Used for storing the calibration constants of ADC and DAC.
		WARNING: Using this function may destroy the Calibration Data.
		'''
		self.fd.write(chr(WREEPROM))
		self.fd.write(chr(addr&255))
		self.fd.write(chr(addr>>8))
		self.fd.write(dat)
		res = self.fd.read(1)
		if res != 'D':
			print 'eeprom write byte error = ', res

	def eeprom_read_block(self, addr, nb):	# get nb bytes starting from addr
		'''
		Reads 'nb' bytes starting from the specified address of the EEPROM memory of ATmega32.
		Used for restoring the calibration constants of ADC and DAC.
		'''
		self.fd.write(chr(RDEEPROM))
		self.fd.write(chr(addr&255))
		self.fd.write(chr(addr>>8))
		self.fd.write(chr(nb))
		res = self.fd.read(1)
		if res != 'D':
			print 'eeprom read block error = ', res
		dat = self.fd.read(nb)
		return dat

	def save_calib(self, ch, m, c):	# Saves m & c (8 bytes) to addr ch*8
		'''
		It is possible to reduce the offset and gain errors of the ADC, DAC and the op-amps
		used in the circuit by doing a calibration. The -5V to 5V output is connected to both
		the -5V to +5V inputs before running the calibrate.py program. The output is measured
		with a >= 4.5 digit voltmeter and the calibration constants are stored to the EEPROM.
		WARNING: Using this function may destroy the Calibration Data.
		'''
		addr = ch*8
		s = struct.pack('f'*2, m, c)	# pack to floats
		for i in range(2*4):	
			self.eeprom_write_char(addr+i, s[i])
			print ord(s[i]),
		print
		self.m[ch] = m
		self.c[ch] = c
		print 'SC: ch = %d m=%10.6f  c=%10.6f'%(ch, self.m[ch], self.c[ch])

	def load_calib(self, ch):	# Load m & c from EEPROM
		'''
		Loads the calibration constants from the EEPROM and assigns them to the slope & intercept.	
		'''
		res = self.eeprom_read_block(ch*8,8)
		if ord(res[0]) == 255 and ord(res[1]) == 255:
			print 'BAD Calibration data. EEPROM does not have any data '
			return
		raw = struct.unpack('f'*2, res)
		self.m[ch] = raw[0]
		self.c[ch] = raw[1]
		for c in res: print ord(c),
		print
		print 'LC: ch = %d m=%10.6f  c=%10.6f'%(ch, self.m[ch], self.c[ch])

#------------------------------------ ADC & DAC transactions -----------------------------

	def set_current(self, i):
		'''
		Sets the current of the Programmable Current Source.
		Possible to set it from .020 mA to 2 mA, provided the IR drop across the load resistor < 2V
		Returns the voltage at the Current Source Output.
		'''
		if (i < 0.020) or (i > 2.0):
			print 'ERR:Current must be from 0.02 to 2.0 mA'
			return None
		i += 0.005				# 5 uA correction is applied. NEED TO SOLVE THIS PROBLEM !!!
		Rc = 1000.0					  # Collector Resistance from 5V reference
		v = 5.0 - Rc * i * 1.0e-3  	  # mA to A
		#print 'DAC0 to set current = ', v
		self.set_voltage(1,v)
		return self.get_voltage(6)

	def write_dac(self, ch, data):
		'''
		Writes binary data to DAC. Low level routine, generally not used.
		'''
		if (data > 4095): 		# DAC linearity problem
			data = 4095
		self.fd.write(chr(SETDAC))
		self.fd.write(chr(ch))
		self.fd.write(chr(data&255))
		self.fd.write(chr(data>>8))
		res = self.fd.read(1)
		if res != 'D':
			print 'WRITEDAC error ', res
			return
		return data

	def set_voltage(self, ch, val):		# returns the interger send to DAC
		'''
		Sets the voltage outputs. Channel 0 is -5V to +5V and channel 1 is 0V to 5V.
		The DAC output goes only upto 4.990 volts.
		'''
		if val > DACMAX: val = DACMAX		# Patch for the MCP4922 Problem 
		if val < -DACMAX: val = -DACMAX
		iv = int(self.m[8+ch]*val + self.c[8+ch])
		return self.write_dac(ch,iv)

	def set_bpv(self, val):		# returns the interger send to DAC
		'''
		Sets the Bipolar Voltage Output (T30) from -4.99 to + 4.99 volts
		'''
		return self.set_voltage(0,val)

	def set_upv(self, val):		# returns the interger send to DAC
		'''
		Sets the Unipolar Voltage Output (T31) from 0 to + 4.99 volts
		'''
		if val < 0: return
		return self.set_voltage(1,val)

	def read_adc(self, ch):
		'''
		Reads the specified ADC channel, returns a number from 0 to 4095. Low level routine.
		'''
		if (ch > 7):
			print 'Argument error'
			return
		self.fd.write(chr(READADC))
		self.fd.write(chr(ch))
		res = self.fd.read(1)
		if res != 'D':
			print 'READADC error ', res
			return
		res = self.fd.read(2)
		iv = ord(res[0]) | (ord(res[1]) << 8)
		return iv

	def get_voltage(self, ch):
		'''
		Reads the specified channel of the ADC. Returns -5V to 5V for channels 0 and 1
		0V to 5V for other channels.
		'''
		if (ch > 7):
			print 'Argument error'
			return
		self.fd.write(chr(READADC))
		self.fd.write(chr(ch))
		res = self.fd.read(1)
		if res != 'D':
			print 'WRITEDAC error ', res
			return
		res = self.fd.read(2)
		iv = ord(res[0]) | (ord(res[1]) << 8)
		v = self.m[ch] * iv + self.c[ch]
		return v

	def get_voltage_time(self, ch):
		'''
		Reads the specified channel of the ADC. Returns -5V to 5V for channels 0 and 1
		0V to 5V for other channels. Adds the PC time info
		'''
		if (ch > 7):
			print 'Argument error'
			return
		self.fd.write(chr(READADC))
		self.fd.write(chr(ch))
		tm = time.time()				# Job is sent. Now mark the time
		res = self.fd.read(1)
		if res != 'D':
			print 'WRITEDAC error ', res
			return
		res = self.fd.read(2)
		iv = ord(res[0]) | (ord(res[1]) << 8)
		v = self.m[ch] * iv + self.c[ch]
		return tm, v

	def set_samtime(self, sam):
		'''
		Sets the sampling time of MCP3208 ADC, minimum required is 2 uSec. Give more for high input
		impedance signals.
		'''		
		if sam > 250:
			print 'Sampling time MUST NOT exceed 250 microseconds'
			return
		self.fd.write(chr(SETSAMTIME))
		self.fd.write(chr(sam))
		res = self.fd.read(1)
		if res != 'D':
			print 'SETSAMTIME ERROR ', res

	def set_adcsize(self, size):
		'''
		The ADC output is 12 bits (2 bytes space). Capture functions gives the option to discard
		4 LSBs and return the data in 1 byte, saving space and time.
		'''
		if size > 2:
			print 'ADC datasize MUST be 1 or 2 bytes'
			return
		self.fd.write(chr(SETADCSIZE))
		self.fd.write(chr(size))
		res = self.fd.read(1)
		if res != 'D':
			print 'SETADCSIZE ERROR ', res
		else:
			self.adcsize = size


	def capture(self, ch, np, delay):
		'''
		Arguments : channel number , number of samples and timegap between consecutive
		digitizations. Returns two lists of size 'np'; time and volatge.
		'''
		if delay < 10:
			return
		if delay < 20:
			self.fd.write(chr(QCAPTURE))
			self.fd.write(chr(ch))
			self.fd.write(chr(np&255))
			self.fd.write(chr(np>>8))
			self.fd.write(chr(delay))
			st = time.time()
			res = self.fd.read(1)
			if res != 'D':
				print 'QCAPTURE Error ', res, time.time()-st
				return 0,0
			asize = 1					# adc datasize = 1 for QCAPTURE
		else:
			self.fd.write(chr(CAPTURE))
			self.fd.write(chr(ch))
			self.fd.write(chr(np&255))
			self.fd.write(chr(np>>8))
			self.fd.write(chr(delay&255))
			self.fd.write(chr(delay>>8))
			res = self.fd.read(1)
			if res != 'D':
				print 'CAPTURE error ', res
				return
			res = self.fd.read(1)		# adc_size info from other end
			asize = ord(res)
		nc = asize * np 	
		data = self.fd.read(nc)
		dl = len(data)
		if dl != nc:
			print 'CAPTURE: size mismatch ', nc, dl
			return
		
		ta = []
		va = []
		if ch <= 1:									# Channel 0 or 1 (-5V to +5V)
			if asize == 2:							# 2 byte dataword
				raw = struct.unpack('H'* np, data)  # 2 byte words in the structure
				for i in range(np):
					ta.append(0.001 * i * delay)	# microseconds to milliseconds
					va.append(self.m[ch] * (raw[i]>>4) + self.c[ch])
			else:
				raw = struct.unpack('B'* np, data)  # 1 byte words in the structure
				for i in range(np):
					ta.append(0.001 * i * delay)		# microseconds to milliseconds
					va.append(raw[i]*10.0/255 - 5.0)
		else:
			if asize == 2:							# 2 byte dataword
				raw = struct.unpack('H'* np, data)  # 16 bit data in uint16 array
				for i in range(np):
					ta.append(0.001 * i * delay)	# microseconds to milliseconds
					va.append((raw[i]>>4) * 5.0 / 4095)
			else:
				raw = struct.unpack('B'* np, data)  # 8 bit data in byte array
				for i in range(np):
					ta.append(0.001 * i * delay)	# microseconds to milliseconds
					va.append(raw[i] * 5.0 / 255)
		return ta,va


	def capture01(self,np, delay):
		'''
		Samples the first two channels 'np' times. 
		Time gap between samples is 'delay' usecs.
		If delay < 20, 9 usecs offset between CH0 & CH1, else 17 usecs.
		'''
		if delay < 10:
			return
		if delay < 20:				# Fast Capture, datasize = 1 byte
			self.fd.write(chr(QCAPTURE01))
			self.fd.write(chr(np&255))
			self.fd.write(chr(np>>8))
			self.fd.write(chr(delay))
			res = self.fd.read(1)
			if res != 'D':
				print 'CAPTURE01 error ', res
				return		
			asize = 1
			tg01 =  0.009			# 0.009 milliseconds between CH0 and CH1
		else:						# A slow capture
			self.fd.write(chr(CAPTURE01))
			self.fd.write(chr(np&255))
			self.fd.write(chr(np>>8))
			self.fd.write(chr(delay&255))
			self.fd.write(chr(delay>>8))
			res = self.fd.read(1)
			if res != 'D':
				print 'CAPTURE01 error ', res
				return
			res = self.fd.read(1)	# adc_size info from other end
			asize = ord(res)
			tg01 = 0.017			# 0.017 milliseconds between Ch0 & Ch1 digitizations

		nb = asize *np * 2		# data from two channels 
		data = self.fd.read(nb)
		dl = len(data)
		if dl != nb:
			print 'CAPTURE01: size mismatch ', nb, dl
			return

		taa = []	# time & voltage arrays for CH0
		vaa = []	
		tba = []	# time & voltage arrays for CH1
		vba = []	
		if asize == 1:							# 1 byte dataword
			raw = struct.unpack('B'* 2*np, data)  # 8 bit data in byte array
			for i in range(np):
				taa.append(0.001 * 2 * i * delay)
				vaa.append(raw[2*i] * 10.0 / 255.0 - 5.0)
				tba.append(0.001 * 2 * i * delay + tg01)
				vba.append(raw[2*i +1] * 10.0 / 255.0 - 5.0)
		else:                					
			raw = struct.unpack('H'* 2*np, data)  # 16 bit data in uint16 array
			for i in range(np):
				taa.append(0.001 * 2 * i * delay)
				vaa.append((raw[2*i]>>4) * 10.0 / 4095.0 - 5.0)
				tba.append(0.001 * 2 * i * delay + tg01)
				vba.append((raw[2*i +1]>>4) * 10.0 / 4095.0 - 5.0)
		return taa,vaa,tba,vba


	def capture_m32(self, ch, np, delay):   # Not working properly
		'''
		Capture 'np' samples from the ATmega32 ADC.
		Arguments : channel number , number of samples and timegap between consecutive
		digitizations. Returns a list of [time, volatge] coordinates.
		'''
		if delay < 10:
			return
		self.fd.write(chr(CAPTURE_M32))
		self.fd.write(chr(ch))
		self.fd.write(chr(np&255))
		self.fd.write(chr(np>>8))
		self.fd.write(chr(delay&255))
		self.fd.write(chr(delay>>8))
		res = self.fd.read(1)
		if res != 'D':
			print 'CAPTURE_M32 error ', res
			return
		asize = 1			# datasize = 1 for CAPTURE_M32
		nc = asize * np 	
		data = self.fd.read(nc)
		dl = len(data)
		if dl != nc:
			print 'CAPTURE_M32: size mismatch ', nc, dl
			return
		
		ta = []
		va = []
		raw = struct.unpack('B'* np, data)  # 8 bit data in byte array
		for i in range(np):
			ta.append(0.001 * i * delay)	# microseconds to milliseconds
			va.append(raw[i] * 5.0 / 255)
		return ta,va

#------------------- Modifiers for Capture ------------------------------
	def disable_actions(self):
		'''
		Disable all modifiers to the capture call. The capture will try to
		do a self triggering on the ADC input.
		'''
		self.fd.write(chr(SETACTION))
		self.fd.write(chr(0))
		self.fd.write(chr(0))
		self.fd.read(1)

	def enable_wait_high(self, pin):
		'''
		Wait for a HIGH on the speciied 'pin' just before every Capture.
		'''
		if pin == 4:
			mask = 0
		else:
			mask = 1 << pin          
		self.fd.write(chr(SETACTION))
		self.fd.write(chr(AWAITHI))
		self.fd.write(chr(mask))
		self.fd.read(1)

	def enable_wait_rising(self, pin):
		'''
		Wait for a rising EDGE on the speciied 'pin' just before every Capture.
		'''
		if pin == 4:
			mask = 0
		else:
			mask = 1 << pin          
		print 'wait_rising ', AWAITRISE
		self.fd.write(chr(SETACTION))
		self.fd.write(chr(AWAITRISE))
		self.fd.write(chr(mask))
		self.fd.read(1)

	def enable_wait_low(self, pin):
		'''
		Wait for a LOW on the speciied 'pin' just before every Capture.
		'''
		if pin == 4:
			mask = 0
		else:
			mask = 1 << pin          
		self.fd.write(chr(SETACTION))
		self.fd.write(chr(AWAITLO))
		self.fd.write(chr(mask))
		self.fd.read(1)

	def enable_wait_falling(self, pin):
		'''
		Wait for a falling EDGE on the speciied 'pin' just before every Capture.
		'''
		if pin == 4:
			mask = 0
		else:
			mask = 1 << pin          
		print 'wait_rising ', AWAITRISE
		self.fd.write(chr(SETACTION))
		self.fd.write(chr(AWAITFALL))
		self.fd.write(chr(mask))
		self.fd.read(1)

	def enable_set_high(self, pin):
		'''
		Sets the speciied 'pin' HIGH, just before every Capture.
		'''
		mask = 1 << pin
		self.fd.write(chr(SETACTION))
		self.fd.write(chr(ASET))
		self.fd.write(chr(mask))
		self.fd.read(1)

	def enable_set_low(self, pin):
		'''
		Sets the speciied 'pin' LOW, just before every Capture.
		'''
		mask = 1 << pin
		self.fd.write(chr(SETACTION))
		self.fd.write(chr(ACLR))
		self.fd.write(chr(mask))
		self.fd.read(1)

	def enable_pulse_high(self, pin):
		'''
		Generate a HIGH TRUE Pulse on the speciied 'pin', just before every Capture.
		width is specified by the set_pulsewidth() function.
		'''
		mask = 1 << pin
		self.fd.write(chr(SETACTION))
		self.fd.write(chr(APULSEHI))
		self.fd.write(chr(mask))
		self.fd.read(1)

	def enable_pulse_low(self, pin):
		'''
		Generate a LOW TRUE Pulse on the speciied 'pin', just before every Capture.
		'''
		mask = 1 << pin
		self.fd.write(chr(SETACTION))
		self.fd.write(chr(APULSELO))
		self.fd.write(chr(mask))
		self.fd.read(1)

		

#------------------------Time Interval Measurement routines-------------
	def set_pulsepol(self, pol):
		'''
		Sets the 'pulse_polarity' parameter for pulse2rtime()
		pol = 0 means HIGH TRUE pulse 
		'''
		self.fd.write(chr(SETPULSEPOL))
		self.fd.write(chr(pol))
		res = self.fd.read(1)
		if res == 'D':
			self.pulse_pol = pol

	def set_pulsewidth(self, width):
		'''
		Sets the 'pulse_width' parameter for pulse2rtime() command. 
		Also used by usound_time() and the elable_pulse_high/low() functions
		'''
		self.fd.write(chr(SETPULSEW))
		self.fd.write(chr(width))
		res = self.fd.read(1)
		if res == 'D':
			self.pulse_width = width

	def usound_time(self):
		'''
		Used for measuring the velocity of sound. Connect the Transmitter Piezo to OD1 (T4).
		The Receiver is connected to the amplifier input T15. This function measures the time
		from a Pulse on ID1 to a signal on T15, in microseconds. 
		Use set_pulsewidth() to set the width to 13 microseconds.
		'''
		self.fd.write(chr(USOUND))
		res = self.fd.read(1)
		if res != 'D':
			print 'Echo error = ',res
			return -1.0
		res = self.fd.read(3)
		low = (ord(res[1]) << 8) | ord(res[0])
		return low + 50000 * ord(res[2])

	def __helper(self, cmd, pin1, pin2):    # pins 0 to 3
		'''
        Used by time measurement functions below.
        Make an 8 bit mask from pin1 and pin2.
		First argument (pin1) is encoded in the HIGH half.
        for example pin1 = 2 , pin2 = 0, mask = 0010:0001
		'''
		if pin1 == 4:        	# Analog Comparator
			hi = 0
		else:
			hi = 1 << (pin1+4)  # digin pins
           
		if pin2 == 4:        	# wait on Analog comparator
			low = 0
		else:
			low  = 1 << pin2
		mask = hi | low;
		self.fd.write(chr(cmd))
		self.fd.write(chr(mask))
		res = self.fd.read(1)
		if res != 'D':
			print 'Time Measurement call Error. CMD = ', cmd, res
			return -1.0
		res = self.fd.read(3)
		low = (ord(res[1]) << 8) | ord(res[0])
		return float(low + 50000 * ord(res[2]))
    
	def r2ftime(self, pin1, pin2):
		'''
		Measures time from a rising edge of pin1 to a falling edge on pin2.
		Pins could be same or distinct.
		'''
		return self.__helper(R2FTIME, pin1, pin2)

	def f2rtime(self, pin1, pin2):
		'''
		Measures time from a falling edge of pin1 to a rising edge on pin2.
		Pins could be same or distinct.
		'''
		return self.__helper(F2RTIME, pin1, pin2)

	def r2rtime(self, pin1, pin2):
		'''
		Measures time from a rising edge of pin1 to a rising edge on pin2.
		Pins could be same or distinct.
		'''
		return self.__helper(R2RTIME, pin1, pin2)

	def f2ftime(self, pin1, pin2):
		'''
		Measures time from a falling edge of pin1 to a falling edge on pin2.
		Pins could be same or distinct.
		'''
		return self.__helper(F2FTIME, pin1, pin2)

	def set2ftime(self, op, ip):
		'''
		Measures time from Setting output pin 'op' to a LOW on input pin 'ip'
		'''
		return self.__helper(SET2FTIME, op, ip)

	def set2rtime(self, op, ip):
		'''
		Measures time from Setting output pin 'op' to a HIGH on input pin 'ip'
		'''
		return self.__helper(SET2RTIME, op, ip)

	def clr2rtime(self, op, ip):
		'''
		Measures time from Clearing output pin 'op' to a HIGH on input pin 'ip'
		'''
		return self.__helper(CLR2RTIME, op, ip)

	def clr2ftime(self, op, ip):
		'''
		Measures time from Clearing output pin 'op' to a LOW on input pin 'ip'
		'''
		return self.__helper(CLR2FTIME, op, ip)

	def pulse2rtime(self, op, ip):
		'''
		Measures time from a Pulse on pin 'op' to a HIGH on input pin 'ip'
		'''
		return self.__helper(PULSE2RTIME, op, ip)

	def pulse2ftime(self, op, ip):
		'''
		Measures time from a Pulse on pin 'op' to a LOW on input pin 'ip'
		'''
		return self.__helper(PULSE2FTIME, op, ip)

	def multi_r2rtime(self, pin , skipcycles=0):
		'''
		Time between two rising edges on the same input pin.
		separated by 'skipcycles' number of cycles.
		If skipcycles is zero the period of the waveform is returned.
		'''
		if pin > 4:			# ADC inputs
			mask = pin << 4
		elif pin == 4:
			mask = 0
		else:
			mask = 1 << pin
		self.fd.write(chr(MULTIR2R))
		self.fd.write(chr(mask))
		self.fd.write(chr(skipcycles))
		if self.fd.read(1) != 'D':
			return -1.0
		res = self.fd.read(3)
		low = (ord(res[1]) << 8) | ord(res[0])
		return float(low + 50000 * ord(res[2]))


	def adc2cmp(self, ch):			# Route ADC input to comparator (AIN-)
		'''
		Route the specified ADC channel to the Analog Comparator Input (AIN-)
		'''
		self.fd.write(chr(ADC2CMP))
		self.fd.write(chr(ch))
		self.fd.read(1)

#----------------------------- Simple Digital I/O functions ----------------------------
	def write_outputs(self, val):
		'''
		Writes  a 2 bit number to the Digital Outputs
		'''
		self.fd.write(chr(DIGOUT))
		self.fd.write(chr(val))
		self.fd.read(1)

	def read_inputs(self):
		'''
		Gets a 4 bit number representing the Digital Input voltage Levels
		'''
		self.fd.write(chr(DIGIN))
		res = self.fd.read(1)
		if res != 'D':
			print 'DIGIN error'
			return
		res = self.fd.read(1)
		return ord(res) & 15		# 4 LSBs

#-----------DIRECT PORT ACCESS FUNCTIONS (Use only if you know what you are doing)---------
	def set_ddr(self, port, direc):
		self.fd.write(chr(SETDDR))           
		self.fd.write(chr(port))	 # 0 to 3 for A,B,C and D
		self.fd.write(chr(direc))
		self.fd.read(1)
		return

	def set_port(self, port, val):
		self.fd.write(chr(SETPORT))           
		self.fd.write(chr(port))	 # 0 to 3 for A,B,C and D
		self.fd.write(chr(val))
		self.fd.read(1)
		return

	def get_port(self, port):
		self.fd.write(chr(SETPORT))           
		self.fd.write(chr(port))	 # 0 to 3 for A,B,C and D
		self.fd.read(1)
		data = self.fd.read(1)     	 # get the status byte only
		return ord(data)

#--------------------------------- eyeutils.py ------------------------------
	def minimum(self,va):
		vmin = 1.0e10		# need to change
		for v in va:
			if v < vmin:
				vmin = v
		return vmin

	def maximum(self,va):
		vmax = 1.0e-10		# need to change
		for v in va:
			if v > vmax:
				vmax = v
		return vmax

	def rms(self,va):
		vsum = 0.0
		for v in va:
			vsum += v**2
		v = vsum / len(va)
		return math.sqrt(v)

	def mean(self,va):
		vsum = 0.0
		for v in va:
			vsum += v
		v = vsum / len(va)
		return v

	def save(self, data, filename = 'plot.dat'):
		'''
		Input data is of the form, [ [x1,y1], [x2,y2],....] where x and y are vectors
		'''
		if data == None: return
		import __builtin__					# Need to do this since 'eyes.py' redefines 'open'
		f = __builtin__.open(filename,'w')
		for xy in data:
			for k in range(len(xy[0])):
				f.write('%5.3f  %5.3f\n'%(xy[0][k], xy[1][k]))
			f.write('\n')
		f.close()

	def grace(self, data, xlab = '', ylab = '', title = ''):
		'''
		Input data is of the form, [ [x1,y1], [x2,y2],....] where x and y are vectors
		'''
		try:
			import pygrace
		except:
			return False
		pg = pygrace.grace()
		for xy in data:
			pg.plot(xy[0],xy[1])
			pg.hold(1)				# Do not erase the old data
		pg.xlabel(xlab)
		pg.ylabel(ylab)
		pg.title(title)
		return True

