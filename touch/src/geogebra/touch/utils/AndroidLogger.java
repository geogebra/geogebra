package geogebra.touch.utils;

public class AndroidLogger
{
	/**
	 * This method can be used to print to Andorid's LogCat
	 * 
	 * @param str
	 *          the String that shall be printed
	 */
	public static native void consoleLog(String str) /*-{
		console.log(message);
	}-*/;
}
