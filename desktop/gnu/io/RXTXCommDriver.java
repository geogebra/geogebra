/*-------------------------------------------------------------------------
|   RXTX License v 2.1 - LGPL v 2.1 + Linking Over Controlled Interface.
|   RXTX is a native interface to serial ports in java.
|   Copyright 1998 Kevin Hester, kevinh@acm.org
|   Copyright 2000-2008 Trent Jarvi tjarvi@qbang.org and others who
|   actually wrote it.  See individual source files for more information.
|
|   A copy of the LGPL v 2.1 may be found at
|   http://www.gnu.org/licenses/lgpl.txt on March 4th 2007.  A copy is
|   here for your convenience.
|
|   This library is free software; you can redistribute it and/or
|   modify it under the terms of the GNU Lesser General Public
|   License as published by the Free Software Foundation; either
|   version 2.1 of the License, or (at your option) any later version.
|
|   This library is distributed in the hope that it will be useful,
|   but WITHOUT ANY WARRANTY; without even the implied warranty of
|   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
|   Lesser General Public License for more details.
|
|   An executable that contains no derivative of any portion of RXTX, but
|   is designed to work with RXTX by being dynamically linked with it,
|   is considered a "work that uses the Library" subject to the terms and
|   conditions of the GNU Lesser General Public License.
|
|   The following has been added to the RXTX License to remove
|   any confusion about linking to RXTX.   We want to allow in part what
|   section 5, paragraph 2 of the LGPL does not permit in the special
|   case of linking over a controlled interface.  The intent is to add a
|   Java Specification Request or standards body defined interface in the 
|   future as another exception but one is not currently available.
|
|   http://www.fsf.org/licenses/gpl-faq.html#LinkingOverControlledInterface
|
|   As a special exception, the copyright holders of RXTX give you
|   permission to link RXTX with independent modules that communicate with
|   RXTX solely through the Sun Microsytems CommAPI interface version 2,
|   regardless of the license terms of these independent modules, and to copy
|   and distribute the resulting combined work under terms of your choice,
|   provided that every copy of the combined work is accompanied by a complete
|   copy of the source code of RXTX (the version of RXTX used to produce the
|   combined work), being distributed under the terms of the GNU Lesser General
|   Public License plus this exception.  An independent module is a
|   module which is not derived from or based on RXTX.
|
|   Note that people who make modified versions of RXTX are not obligated
|   to grant this special exception for their modified versions; it is
|   their choice whether to do so.  The GNU Lesser General Public License
|   gives permission to release a modified version without this exception; this
|   exception also makes it possible to release a modified version which
|   carries forward this exception.
|
|   You should have received a copy of the GNU Lesser General Public
|   License along with this library; if not, write to the Free
|   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
|   All trademarks belong to their respective owners.
--------------------------------------------------------------------------*/
/* Martin Pool <mbp@linuxcare.com> added support for explicitly-specified
 * lists of ports, October 2000. */
/* Joseph Goldstone <joseph@lp.com> reorganized to support registered ports,
 * known ports, and scanned ports, July 2001 */

package gnu.io;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

/**
   This is the JavaComm for Linux driver.
*/
public class RXTXCommDriver implements CommDriver
{

	private final static boolean debug = false;
	private final static boolean devel = false;
	private final static boolean noVersionOutput = "true".equals( System.getProperty( "gnu.io.rxtx.NoVersionOutput" ) );

	static
	{
		if(debug ) System.out.println("RXTXCommDriver {}");
		if ("AMD64".equals(System.getenv("PROCESSOR_ARCHITECTURE"))) {
			System.loadLibrary( "rxtxSerial64" );
		} else {
			System.loadLibrary( "rxtxSerial" );
		}

		/*
		   Perform a crude check to make sure people don't mix
		   versions of the Jar and native lib

		   Mixing the libs can create a nightmare.

		   It could be possible to move this over to RXTXVersion
		   but All we want to do is warn people when first loading
		   the Library.
		*/
		String JarVersion = RXTXVersion.getVersion();
		String LibVersion;
		try {
		        LibVersion = RXTXVersion.nativeGetVersion();
		} catch ( Error UnsatisfiedLinkError )
		{
			// for rxtx prior to 2.1.7
			LibVersion = nativeGetVersion();
		}
		if ( devel )
		{
			if ( ! noVersionOutput )
			{
				System.out.println("Stable Library");
				System.out.println("=========================================");
				System.out.println("Native lib Version = " + LibVersion );
				System.out.println("Java lib Version   = " + JarVersion );
			}
		}

		if ( ! JarVersion.equals( LibVersion ) )
		{
			System.out.println( "WARNING:  RXTX Version mismatch\n\tJar version = " + JarVersion + "\n\tnative lib Version = " + LibVersion );
		}
		else if ( debug )
		{
			System.out.println( "RXTXCommDriver:\n\tJar version = " + JarVersion + "\n\tnative lib Version = " + LibVersion );
		}
	}

	/** Get the Serial port prefixes for the running OS */
	private String deviceDirectory;
	private String osName;
	private native boolean registerKnownPorts(int PortType);
	private native boolean isPortPrefixValid(String dev);
	private native boolean testRead(String dev, int type);
	private native String getDeviceDirectory();
	// for rxtx prior to 2.1.7
	public static native String nativeGetVersion();

	private final String[] getValidPortPrefixes(String CandidatePortPrefixes[])
	{
		/*
		256 is the number of prefixes ( COM, cua, ttyS, ...) not
		the number of devices (ttyS0, ttyS1, ttyS2, ...)

		On a Linux system there are about 400 prefixes in
		deviceDirectory.
		registerScannedPorts() assigns CandidatePortPrefixes to
		something less than 50 prefixes.

		Trent
		*/

		String ValidPortPrefixes[]=new String [256];
		if (debug)
			System.out.println("\nRXTXCommDriver:getValidPortPrefixes()");
		if(CandidatePortPrefixes==null)
		{
			if (debug)
				System.out.println("\nRXTXCommDriver:getValidPortPrefixes() No ports prefixes known for this System.\nPlease check the port prefixes listed for " + osName + " in RXTXCommDriver:registerScannedPorts()\n");
		}
		int i=0;
		for(int j=0;j<CandidatePortPrefixes.length;j++){
			if(isPortPrefixValid(CandidatePortPrefixes[j])) {
				ValidPortPrefixes[i++]=
					CandidatePortPrefixes[j];
			}
		}
		String[] returnArray=new String[i];
		System.arraycopy(ValidPortPrefixes, 0, returnArray, 0, i);
		if(ValidPortPrefixes[0]==null)
		{
			if (debug)
			{
				System.out.println("\nRXTXCommDriver:getValidPortPrefixes() No ports matched the list assumed for this\nSystem in the directory " + deviceDirectory + ".  Please check the ports listed for \"" + osName + "\" in\nRXTXCommDriver:registerScannedPorts()\nTried:");
				for(int j=0;j<CandidatePortPrefixes.length;j++){
					System.out.println("\t" +
						CandidatePortPrefixes[i]);
				}
			}
		}
		else
		{
			if (debug)
				System.out.println("\nRXTXCommDriver:getValidPortPrefixes()\nThe following port prefixes have been identified as valid on " + osName + ":\n");
/*
			for(int j=0;j<returnArray.length;j++)
			{
				if (debug)
					System.out.println("\t" + j + " " +
						returnArray[j]);
			}
*/
		}
		return returnArray;
	}

	/** handle solaris/sunos /dev/cua/a convention */
		private void checkSolaris(String PortName, int PortType)
	{
		char p[] =  { 91 };
		for( p[0] =97 ;p[0] < 123; p[0]++ )
		{
			if (testRead(PortName.concat(new String(p)),PortType))
			{
				CommPortIdentifier.addPortName(
					PortName.concat(new String(p)),
					PortType,
					this
				);
			}
		}
		/** check for 0-9 in case we have them (Solaris USB) */
		for( p[0] =48 ;p[0] <= 57; p[0]++ )
		{
			if (testRead(PortName.concat(new String(p)),PortType))
			{
				CommPortIdentifier.addPortName(
						PortName.concat(new String(p)),
						PortType,
						this
				);
			}
		}
	}
        private void registerValidPorts(
		String CandidateDeviceNames[],
		String ValidPortPrefixes[],
		int PortType
	) {
		int i =0;
		int p =0 ;
		/* FIXME quick fix to get COM1-8 on windows working.  The
		   Read test is not working properly and its crunch time...
		if(osName.toLowerCase().indexOf("windows") != -1 )
		{
			for( i=0;i < CandidateDeviceNames.length;i++ )
			{
			CommPortIdentifier.addPortName( CandidateDeviceNames[i],
							PortType, this );
			}
			return;

		}
		*/
		if (debug)
		{
			System.out.println("Entering registerValidPorts()");
	/* */
			System.out.println(" Candidate devices:");
			for (int dn=0;dn<CandidateDeviceNames.length;dn++)
				System.out.println("  "	+
					CandidateDeviceNames[dn]);
			System.out.println(" valid port prefixes:");
			for (int pp=0;pp<ValidPortPrefixes.length;pp++)
				System.out.println("  "+ValidPortPrefixes[pp]);
	/* */
		}
		if ( CandidateDeviceNames!=null && ValidPortPrefixes!=null)
		{
			for( i = 0;i<CandidateDeviceNames.length; i++ ) {
				for( p = 0;p<ValidPortPrefixes.length; p++ ) {
					/* this determines:
					 * device file         Valid ports
					 * /dev/ttyR[0-9]*  != /dev/ttyS[0-9]*
					 * /dev/ttySI[0-9]* != /dev/ttyS[0-9]*
					 * /dev/ttyS[0-9]*  == /dev/ttyS[0-9]*

					 * Otherwise we check some ports
					 * multiple times.  Perl would rock
					 * here.
					 *
					 * If the above passes, we try to read
					 * from the port.  If there is no err
					 * the port is added.
					 * Trent
					 */
					String V =  ValidPortPrefixes[ p ];
					int VL = V.length();
					String C =   CandidateDeviceNames[ i ];
					if( C.length() < VL ) continue;
					String CU =
						C.substring(VL).toUpperCase();
					String Cl =
						C.substring(VL).toLowerCase();
					if( !( C.regionMatches(0, V, 0, VL ) &&
						CU.equals( Cl ) ) )
					{
						continue;
					}
					String PortName;
					if(osName.toLowerCase().indexOf("windows") == -1 )
					{
						PortName = deviceDirectory + C;
					}
					else
					{
						PortName = C;
					}
					if (debug)
					{
						System.out.println( C +
								" " + V );
						System.out.println( CU +
								" " + Cl );
					}
					if( osName.equals("Solaris") ||
						osName.equals("SunOS"))
						checkSolaris(PortName,PortType);
					else if (testRead(PortName, PortType))
					{
						CommPortIdentifier.addPortName(
								PortName,
								PortType,
								this
						);
					}
				}
			}
		}
		if (debug)
			System.out.println("Leaving registerValidPorts()");
	}


   /*
    * initialize() will be called by the CommPortIdentifier's static
    * initializer. The responsibility of this method is:
    * 1) Ensure that that the hardware is present.
    * 2) Load any required native libraries.
    * 3) Register the port names with the CommPortIdentifier.
	*
	* <p>From the NullDriver.java CommAPI sample.
	*
	* added printerport stuff
	* Holger Lehmann
	* July 12, 1999
	* IBM

	* Added ttyM for Moxa boards
	* Removed obsolete device cuaa
	* Peter Bennett
	* January 02, 2000
	* Bencom

    */

	/**
	*  Determine the OS and where the OS has the devices located
	*/
	public void initialize()
	{

		if (debug) System.out.println("RXTXCommDriver:initialize()");

		osName=System.getProperty("os.name");
		deviceDirectory=getDeviceDirectory();

	/*
	 First try to register ports specified in the properties
	 file.  If that doesn't exist, then scan for ports.
	*/
		for (int PortType=CommPortIdentifier.PORT_SERIAL;PortType<=CommPortIdentifier.PORT_PARALLEL;PortType++) {
			if (!registerSpecifiedPorts(PortType)) {
				if (!registerKnownPorts(PortType)) {
					registerScannedPorts(PortType);
				}
			}
		}
	}

	private void addSpecifiedPorts(String names, int PortType)
	{
		final String pathSep = System.getProperty("path.separator", ":");
		final StringTokenizer tok = new StringTokenizer(names, pathSep);

		if (debug)
			System.out.println("\nRXTXCommDriver:addSpecifiedPorts()");
		while (tok.hasMoreElements())
		{
			String PortName = tok.nextToken();

			if (testRead(PortName, PortType))
				CommPortIdentifier.addPortName(PortName,
					PortType, this);
		}
	}

   /*
    * Register ports specified in the file "gnu.io.rxtx.properties"
    * Key system properties:
    *                   gnu.io.rxtx.SerialPorts
    * 			gnu.io.rxtx.ParallelPorts
    *
    * Tested only with sun jdk1.3
    * The file gnu.io.rxtx.properties must reside in the java extension dir
    *
    * Example: /usr/local/java/jre/lib/ext/gnu.io.rxtx.properties
    *
    * The file contains the following key properties:
    *
    *  gnu.io.rxtx.SerialPorts=/dev/ttyS0:/dev/ttyS1:
    *  gnu.io.rxtx.ParallelPorts=/dev/lp0:
    *
    */
	private boolean registerSpecifiedPorts(int PortType)
	{
		String val = null;
		Properties origp = System.getProperties();//save system properties

		try
		    {

		     String ext_dir=System.getProperty("java.ext.dirs")+System.getProperty("file.separator");
		     FileInputStream rxtx_prop=new FileInputStream(ext_dir+"gnu.io.rxtx.properties");
		     Properties p=new Properties();
		     p.load(rxtx_prop);
		     System.setProperties(p);
		     for (Iterator it = p.keySet().iterator(); it.hasNext();) {
		          String key = (String) it.next();
		          System.setProperty(key, p.getProperty(key));
		     }
		    }catch(Exception e){
			if (debug){
			    System.out.println("The file: gnu.io.rxtx.properties doesn't exists.");
			    System.out.println(e.toString());
			    }//end if
			}//end catch

		if (debug)
			System.out.println("checking for system-known ports of type "+PortType);
		if (debug)
			System.out.println("checking registry for ports of type "+PortType);

		switch (PortType) {
			case CommPortIdentifier.PORT_SERIAL:
				if ((val = System.getProperty("gnu.io.rxtx.SerialPorts")) == null)
				val = System.getProperty("gnu.io.SerialPorts");
				break;

			case CommPortIdentifier.PORT_PARALLEL:
				if ((val = System.getProperty("gnu.io.rxtx.ParallelPorts")) == null)
				val = System.getProperty("gnu.io.ParallelPorts");
				break;
			default:
				if (debug)
				System.out.println("unknown port type "+PortType+" passed to RXTXCommDriver.registerSpecifiedPorts()");
		}

		System.setProperties(origp); //recall saved properties
		if (val != null) {
			addSpecifiedPorts(val, PortType);
			return true;
		} else {
			return false;
		}
	}

   /*
    * Look for all entries in deviceDirectory, and if they look like they should
    * be serial ports on this OS and they can be opened then register
    * them.
    *
    */
	private void registerScannedPorts(int PortType)
	{
		String[] CandidateDeviceNames;
		if (debug)
			System.out.println("scanning device directory "+deviceDirectory+" for ports of type "+PortType);
		if(osName.equals("Windows CE"))
		{
			String[] temp =
			{ "COM1:", "COM2:","COM3:","COM4:",
			"COM5:", "COM6:", "COM7:", "COM8:" };
			CandidateDeviceNames=temp;
		}
		else if(osName.toLowerCase().indexOf("windows") != -1 )
		{
			String[] temp = new String[259];
			for( int i = 1; i <= 256; i++ )
			{
				temp[i - 1] = "COM" + i;
			}
			for( int i = 1; i <= 3; i++ )
			{
				temp[i + 255] = "LPT" + i;
			}
			CandidateDeviceNames=temp;
			}
			else if ( osName.equals("Solaris") || osName.equals("SunOS"))
			{
			/* Solaris uses a few different ways to identify ports.
			   They could be /dev/term/a /dev/term0 /dev/cua/a /dev/cuaa
			   the /dev/???/a appears to be on more systems.

			   The uucp lock files should not cause problems.
			*/
	/*
				File dev = new File( "/dev/term" );
				String deva[] = dev.list();
				dev = new File( "/dev/cua" );
				String devb[] = dev.list();
				String[] temp = new String[ deva.length + devb.length ];
				for(int j =0;j<deva.length;j++)
					deva[j] = "term/" + deva[j];
				for(int j =0;j<devb.length;j++)
					devb[j] = "cua/" + devb[j];
				System.arraycopy( deva, 0, temp, 0, deva.length );
				System.arraycopy( devb, 0, temp,
						deva.length, devb.length );
				if( debug ) {
					for( int j = 0; j< temp.length;j++)
						System.out.println( temp[j] );
				}
				CandidateDeviceNames=temp;
	*/

			/*

				ok..  Look the the dirctories representing the port
				kernel driver interface.

				If there are entries there are possibly ports we can
				use and need to enumerate.
			*/

				String term[] = new String[2];
				int l = 0;
				File dev = null;

				dev = new File( "/dev/term" );
				if( dev.list().length > 0 )
					term[l++] ="term/";
	/*
				dev = new File( "/dev/cua0" );
				if( dev.list().length > 0 )
					term[l++] = "cua/";
	*/
				String[] temp = new String[l];
				for(l--;l >= 0;l--)
					temp[l] = term[l];
				CandidateDeviceNames=temp;
			}
			else
			{
				File dev = new File( deviceDirectory );
				String[] temp = dev.list();
				CandidateDeviceNames=temp;
			}
			if (CandidateDeviceNames==null)
			{
				if (debug)
					System.out.println("RXTXCommDriver:registerScannedPorts() no Device files to check ");
				return;
			}

			String CandidatePortPrefixes[] = {};
			switch (PortType) {
				case CommPortIdentifier.PORT_SERIAL:
					if (debug)
						System.out.println("scanning for serial ports for os "+osName);


			/*  There are _many_ possible ports that can be used
			    on Linux.  See below in the fake Linux-all-ports  
			    case for a list.  You may add additional ports
			    here but be warned that too many will significantly
			    slow down port enumeration.  Linux 2.6 has udev
			    support which should be faster as only ports the
			    kernel finds should be exposed in /dev

			    See also how to override port enumeration and
			    specifying port in INSTALL.

			    taj
			*/

					if(osName.equals("Linux"))
					{
						String[] Temp = {
						"ttyS", // linux Serial Ports
						"ttySA", // for the IPAQs
						"ttyUSB", // for USB frobs
						"rfcomm",       // bluetooth serial device
						"ttyircomm", // linux IrCommdevices (IrDA serial emu)
						};
						CandidatePortPrefixes=Temp;
					}
					else if(osName.equals("Linux-all-ports"))
					{
					/* if you want to enumerate all ports ~5000
					   possible, then replace the above with this
					*/
						String[] Temp = {
						"comx",      // linux COMMX synchronous serial card
						"holter",    // custom card for heart monitoring
						"modem",     // linux symbolic link to modem.
						"rfcomm",       // bluetooth serial device
						"ttyircomm", // linux IrCommdevices (IrDA serial emu)
						"ttycosa0c", // linux COSA/SRP synchronous serial card
						"ttycosa1c", // linux COSA/SRP synchronous serial card
						"ttyACM",// linux CDC ACM devices
						"ttyC", // linux cyclades cards
						"ttyCH",// linux Chase Research AT/PCI-Fast serial card
						"ttyD", // linux Digiboard serial card
						"ttyE", // linux Stallion serial card
						"ttyF", // linux Computone IntelliPort serial card
						"ttyH", // linux Chase serial card
						"ttyI", // linux virtual modems
						"ttyL", // linux SDL RISCom serial card
						"ttyM", // linux PAM Software's multimodem boards
							// linux ISI serial card
						"ttyMX",// linux Moxa Smart IO cards
						"ttyP", // linux Hayes ESP serial card
						"ttyR", // linux comtrol cards
							// linux Specialix RIO serial card
						"ttyS", // linux Serial Ports
						"ttySI",// linux SmartIO serial card
						"ttySR",// linux Specialix RIO serial card 257+
						"ttyT", // linux Technology Concepts serial card
						"ttyUSB",//linux USB serial converters
						"ttyV", // linux Comtrol VS-1000 serial controller
						"ttyW", // linux specialix cards
						"ttyX"  // linux SpecialX serial card
						};
						CandidatePortPrefixes=Temp;
					}
					else if(osName.toLowerCase().indexOf("qnx") != -1 )
					{
						String[] Temp = {
							"ser"
						};
						CandidatePortPrefixes=Temp;
					}
					else if(osName.equals("Irix"))
					{
						String[] Temp = {
							"ttyc", // irix raw character devices
							"ttyd", // irix basic serial ports
							"ttyf", // irix serial ports with hardware flow
							"ttym", // irix modems
							"ttyq", // irix pseudo ttys
							"tty4d",// irix RS422
							"tty4f",// irix RS422 with HSKo/HSki
							"midi", // irix serial midi
							"us"    // irix mapped interface
						};
						CandidatePortPrefixes=Temp;
					}
					else if(osName.equals("FreeBSD")) //FIXME this is probably wrong
					{
						String[] Temp = {
							"ttyd",    //general purpose serial ports
							"cuaa",    //dialout serial ports
							"ttyA",    //Specialix SI/XIO dialin ports
							"cuaA",    //Specialix SI/XIO dialout ports
							"ttyD",    //Digiboard - 16 dialin ports
							"cuaD",    //Digiboard - 16 dialout ports
							"ttyE",    //Stallion EasyIO (stl) dialin ports
							"cuaE",    //Stallion EasyIO (stl) dialout ports
							"ttyF",    //Stallion Brumby (stli) dialin ports
							"cuaF",    //Stallion Brumby (stli) dialout ports
							"ttyR",    //Rocketport dialin ports
							"cuaR",    //Rocketport dialout ports
							"stl"      //Stallion EasyIO board or Brumby N 
						};
						CandidatePortPrefixes=Temp;
					}
					else if(osName.equals("NetBSD")) // FIXME this is probably wrong
					{
						String[] Temp = {
							"tty0"  // netbsd serial ports
						};
						CandidatePortPrefixes=Temp;
					}
					else if ( osName.equals("Solaris")
							|| osName.equals("SunOS"))
					{
						String[] Temp = {
							"term/",
							"cua/"
						};
						CandidatePortPrefixes=Temp;
					}
					else if(osName.equals("HP-UX"))
					{
						String[] Temp = {
							"tty0p",// HP-UX serial ports
							"tty1p" // HP-UX serial ports
						};
						CandidatePortPrefixes=Temp;
					}

					else if(osName.equals("UnixWare") ||
							osName.equals("OpenUNIX"))
					{
						String[] Temp = {
							"tty00s", // UW7/OU8 serial ports
							"tty01s",
							"tty02s",
							"tty03s"
						};
						CandidatePortPrefixes=Temp;
					}

				else if	(osName.equals("OpenServer"))
					{
						String[] Temp = {
							"tty1A",  // OSR5 serial ports
							"tty2A",
							"tty3A",
							"tty4A",
							"tty5A",
							"tty6A",
							"tty7A",
							"tty8A",
							"tty9A",
							"tty10A",
							"tty11A",
							"tty12A",
							"tty13A",
							"tty14A",
							"tty15A",
							"tty16A",
							"ttyu1A", // OSR5 USB-serial ports
							"ttyu2A",
							"ttyu3A",
							"ttyu4A",
							"ttyu5A",
							"ttyu6A",
							"ttyu7A",
							"ttyu8A",
							"ttyu9A",
							"ttyu10A",
							"ttyu11A",
							"ttyu12A",
							"ttyu13A",
							"ttyu14A",
							"ttyu15A",
							"ttyu16A"
						};
						CandidatePortPrefixes=Temp;
					}
					else if (osName.equals("Compaq's Digital UNIX") || osName.equals("OSF1"))
					{
						String[] Temp = {
							"tty0"  //  Digital Unix serial ports
						};
						CandidatePortPrefixes=Temp;
					}

					else if(osName.equals("BeOS"))
					{
						String[] Temp = {
							"serial" // BeOS serial ports
						};
						CandidatePortPrefixes=Temp;
					}
					else if(osName.equals("Mac OS X"))
					{
						String[] Temp = {
						// Keyspan USA-28X adapter, USB port 1
							"cu.KeyUSA28X191.",
						// Keyspan USA-28X adapter, USB port 1
							"tty.KeyUSA28X191.",
						// Keyspan USA-28X adapter, USB port 2
							"cu.KeyUSA28X181.",
						// Keyspan USA-28X adapter, USB port 2
							"tty.KeyUSA28X181.",
						// Keyspan USA-19 adapter
							"cu.KeyUSA19181.",
						// Keyspan USA-19 adapter
							"tty.KeyUSA19181."
						};
						CandidatePortPrefixes=Temp;
					}
					else if(osName.toLowerCase().indexOf("windows") != -1 )
					{
						String[] Temp = {
							"COM"     // win32 serial ports
							//"//./COM"    // win32 serial ports
					};
					CandidatePortPrefixes=Temp;
				}
				else
				{
					if (debug)
						System.out.println("No valid prefixes for serial ports have been entered for "+osName + " in RXTXCommDriver.java.  This may just be a typo in the method registerScanPorts().");
				}
				break;

			case CommPortIdentifier.PORT_PARALLEL:
				if (debug)
					System.out.println("scanning for parallel ports for os "+osName);
			/** Get the Parallel port prefixes for the running os
			* Holger Lehmann
			* July 12, 1999
			* IBM
			*/
				if(osName.equals("Linux")
			/*
				|| osName.equals("NetBSD") FIXME
				|| osName.equals("HP-UX")  FIXME
				|| osName.equals("Irix")   FIXME
				|| osName.equals("BeOS")   FIXME
				|| osName.equals("Compaq's Digital UNIX")   FIXME
			*/
				)
				{
					String[] temp={
						"lp"    // linux printer port
					};
					CandidatePortPrefixes=temp;
				}
				else if(osName.equals("FreeBSD"))
				{
					String[] temp={
						"lpt"
					};
					CandidatePortPrefixes=temp;
				}
				else if(osName.toLowerCase().indexOf("windows") != -1 )
				{
					String[] temp={
						"LPT"
					};
					CandidatePortPrefixes=temp;
				}
				else  /* printer support is green */
				{
					String [] temp={};
					CandidatePortPrefixes=temp;
				}
				break;
			default:
				if (debug)
					System.out.println("Unknown PortType "+PortType+" passed to RXTXCommDriver.registerScannedPorts()");
		}
		registerValidPorts(CandidateDeviceNames, CandidatePortPrefixes, PortType);
	}


	/*
	 * <p>From the NullDriver.java CommAPI sample.
	 */
	/**
	*  @param PortName The name of the port the OS recognizes
	*  @param PortType CommPortIdentifier.PORT_SERIAL or PORT_PARALLEL
	*  @return CommPort
	*  getCommPort() will be called by CommPortIdentifier from its
	*  openPort() method. PortName is a string that was registered earlier
	*  using the CommPortIdentifier.addPortName() method. getCommPort()
	*  returns an object that extends either SerialPort or ParallelPort.
	*/
	public CommPort getCommPort( String PortName, int PortType )
	{
		if (debug) System.out.println("RXTXCommDriver:getCommPort("
			+PortName+","+PortType+")");
		try {
			switch (PortType) {
				case CommPortIdentifier.PORT_SERIAL:
					if(osName.toLowerCase().indexOf("windows") == -1 )
					{
					
						return new RXTXPort( PortName );
					}
					else
					{
						return new RXTXPort( deviceDirectory + PortName );
					}
				default:
					if (debug)
						System.out.println("unknown PortType  "+PortType+" passed to RXTXCommDriver.getCommPort()");
			}
		} catch( PortInUseException e ) {
			if (debug)
				System.out.println(
					"Port "+PortName+" in use by another application");
		}
		return null;
	}

	/*  Yikes.  Trying to call println from C for odd reasons */
	public void Report( String arg )
	{
		System.out.println(arg);
	}
}
