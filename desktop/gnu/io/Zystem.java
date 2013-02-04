/*-------------------------------------------------------------------------
|   RXTX License v 2.1 - LGPL v 2.1 + Linking Over Controlled Interface.
|   RXTX is a native interface to serial ports in java.
|   Copyright 2002-2007 by Trent Jarvi tjarvi@qbang.org and others who
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
package gnu.io;

import java.io.RandomAccessFile;

public class Zystem
{
	public static final int SILENT_MODE	= 0;
	public static final int FILE_MODE	= 1;
	public static final int NET_MODE	= 2;
	public static final int MEX_MODE	= 3;
	public static final int PRINT_MODE	= 4;
	public static final int J2EE_MSG_MODE   = 5;
 	public static final int J2SE_LOG_MODE = 6;

	static int mode;

	static
	{
		/* 
		The rxtxZystem library uses Python code and is not
		included with RXTX.  A seperate library will be released
		to avoid potential license conflicts.

		Trent Jarvi taj@www.linux.org.uk
		*/

		//System.loadLibrary( "rxtxZystem" );
		mode = SILENT_MODE;
	}

	private static String target;

	public Zystem( int m ) throws UnSupportedLoggerException
	{
		mode = m;
		startLogger( "asdf" );
	}
    /**
     * Constructor.
     * Mode is taken from the java system property "gnu.io.log.mode". The available values are :<ul>
     * <li> SILENT_MODE No logging
     * <li> FILE_MODE log to file
     * <li> NET_MODE
     * <li> MEX_MODE
     * <li> PRINT_MODE
     * <li> J2EE_MSG_MODE
     * <li> J2SE_LOG_MODE log to java.util.logging
     * </ul>
     */
  public Zystem () throws UnSupportedLoggerException
  {
    String s = System.getProperty ("gnu.io.log.mode");
    if (s != null)
      {
	if ("SILENT_MODE".equals (s))
	  {
	    mode = SILENT_MODE;
	  }
	else if ("FILE_MODE".equals (s))
	  {
	    mode = FILE_MODE;
	  }
	else if ("NET_MODE".equals (s))
	  {
	    mode = NET_MODE;
	  }
	else if ("MEX_MODE".equals (s))
	  {
	    mode = MEX_MODE;
	  }
	else if ("PRINT_MODE".equals (s))
	  {
	    mode = PRINT_MODE;
	  }
	else if ("J2EE_MSG_MODE".equals (s))
	  {
	    mode = J2EE_MSG_MODE;
	  }
	else if ("J2SE_LOG_MODE".equals (s))
	  {
	    mode = J2SE_LOG_MODE;
	  }
	else
	  {
	    try
	    {
	      mode = Integer.parseInt (s);
	    }
	    catch (NumberFormatException e)
	    {
	      mode = SILENT_MODE;
	    }
	  }
      }
    else
      {
	mode = SILENT_MODE;
      }
    startLogger ("asdf");
  }


	public void startLogger( ) throws UnSupportedLoggerException
	{
		if ( mode == SILENT_MODE || mode == PRINT_MODE )
		{
			//nativeNetInit( );
			return;
		}
		throw new UnSupportedLoggerException( "Target Not Allowed" );
	}

	/*  accept the host or file to log to. */

	public void startLogger( String t ) throws UnSupportedLoggerException
	{
		target = t;
	/*
		if ( mode == NET_MODE )
		{
			nativeNetInit( );
		}
		if ( nativeInit( ) )
		{
			throw new UnSupportedLoggerException(
				"Port initializion failed" );
		}
	*/
		return;
	}

	public void finalize()
	{
	/*
		if ( mode == NET_MODE )
		{
			nativeNetFinalize( );
		}
		nativeFinalize();
	*/
		mode = SILENT_MODE;
		target = null;
	}

	public void filewrite( String s )
	{
		try {
			RandomAccessFile w =
				new RandomAccessFile( target, "rw" );
			w.seek( w.length() );
			w.writeBytes( s );
			w.close();
		} catch ( Exception e ) {
			System.out.println("Debug output file write failed");
		}
	}

	public boolean report( String s)
	{
		if ( mode == NET_MODE )
		{
		//	return( nativeNetReportln( s ) );
		}
		else if ( mode == PRINT_MODE )
		{
			System.out.println( s );
			return( true );
		}
		else if ( mode == MEX_MODE )
		{
		//	return( nativeMexReport( s ) );
		}
		else if ( mode == SILENT_MODE )
		{
			return( true );
		}
		else if ( mode == FILE_MODE )
		{
			filewrite( s );
		}
		else if ( mode == J2EE_MSG_MODE )
		{
			return( false );
		}
		else if (mode == J2SE_LOG_MODE)
		{
			java.util.logging.Logger.getLogger ("gnu.io").fine (s);
			return (true);
		}
		return( false );
	}

	public boolean reportln( )
	{
		boolean b;
		if ( mode == NET_MODE )
		{
		//	b= nativeNetReportln( "\n" );
		//	return(b);
		}
		else if ( mode == PRINT_MODE )
		{
			System.out.println( );
			return( true );
		}
		else if ( mode == MEX_MODE )
		{
		//	b = nativeMexReportln( "\n" );
		//	return(b);
		}
		else if ( mode == SILENT_MODE )
		{
			return( true );
		}
		else if ( mode == FILE_MODE )
		{
			filewrite( "\n" );
		}
		else if ( mode == J2EE_MSG_MODE )
		{
			return( false );
		}
		return( false );
	}

	public boolean reportln( String s)
	{
		boolean b;
		if ( mode == NET_MODE )
		{
		//	b= nativeNetReportln( s + "\n" );
		//	return(b);
		}
		else if ( mode == PRINT_MODE )
		{
			System.out.println( s );
			return( true );
		}
		else if ( mode == MEX_MODE )
		{
		//	b = nativeMexReportln( s + "\n" );
		//	return(b);
		}
		else if ( mode == SILENT_MODE )
		{
			return( true );
		}
		else if ( mode == FILE_MODE )
		{
			filewrite( s + "\n" );
		}
		else if ( mode == J2EE_MSG_MODE )
		{
			return( false );
		}
		else if (mode == J2SE_LOG_MODE)
		{
			return (true);
		}
		return( false );
	}

/*
	private native boolean nativeInit( );
	private native void nativeFinalize();
*/

	/* open and close the socket */
/*
	private native boolean nativeNetInit( );
	private native void nativeNetFinalize();
*/

	/* dumping to a remote machine */
/*
	public native boolean nativeNetReport( String s );
	public native boolean nativeNetReportln( String s );
*/

	/*  specific to Matlab */
/*
	public native boolean nativeMexReport( String s );
	public native boolean nativeMexReportln( String s );
*/
}
