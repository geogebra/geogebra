package geogebra.gui.util;

/////////////////////////////////////////////////////////
// Bare Bones Browser Launch                          //
// Version 1.5                                        //
// December 10, 2005                                  //
// Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
// Example Usage:                                     //
//  String url = "http://www.centerkey.com/";       //
//  BareBonesBrowserLaunch.openURL(url);            //
// Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

import geogebra.main.AppD;
import geogebra.util.Util;

import java.lang.reflect.Method;
import java.net.URI;

import javax.swing.JOptionPane;

public class BrowserLauncher {

	private static final String errMsg = "Error attempting to launch web browser";

	public static void openURL(String url) {
		// java 6 supports open URLs in the default browser directly
		double javaVersion = Util.getJavaVersion();
		if (javaVersion >= 1.6) {
			try {
				URI uri = new URI(url);

				// since Java 6:
				// java.awt.Desktop.getDesktop().browse(uri)
				Class<?> desktopClass = Class.forName("java.awt.Desktop");
				Method getDesktop = desktopClass.getDeclaredMethod("getDesktop", null);   
				Method browse = desktopClass.getDeclaredMethod("browse", new Class[] {URI.class});   
				Object desktopObj = getDesktop.invoke(null, null);     
				browse.invoke(desktopObj, new Object[] {uri});

				return; // java 1.6 was successful
			} catch (Exception e) {
				e.printStackTrace();
			}            
		}

		// older Java version 
		//String osName = System.getProperty("os.name");
		try {
			if (AppD.MAC_OS) { // Michael Borcherds 2008-03-21
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] {String.class});
				openURL.invoke(null, new Object[] {url});
			}
			else if (AppD.WINDOWS) // Michael Borcherds 2008-03-21
			{
				//    	 Michael Borcherds 2008-03-21 BEGIN
				// replace file:/c:/Program Files/etc
				// by    file:///c:\Program Files\etc
				if (url.indexOf("file:") == 0) // local URL
				{
					url = url.replaceAll("file:///","");    // remove file:/// from the start
					url = url.replaceAll("file:/","");      // remove file:/ from the start

					url = url.replaceAll("[/\\\\]+", "\\" + "\\");  // replace slashes with backslashes

					url = "file:///"+url; // put "file:///" back in
				}
				//    	 Michael Borcherds 2008-03-21 END
				Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler " + url);
			}
			else { //assume Unix or Linux
				String[] browsers = {
						"xdg-open","firefox","google-chrome","chromium-browser","opera",
						"konqueror", "epiphany", "safari", "mozilla", "netscape", "seamonkey"
				};
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime().exec(
							new String[] {"which", browsers[count]}).waitFor() == 0)
						browser = browsers[count];
				if (browser == null) {
					throw new Exception("Could not find web browser");
				}
				Runtime.getRuntime().exec(new String[] {browser, url});
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, errMsg + ":\n" + e.getLocalizedMessage());
		}
	}

}
