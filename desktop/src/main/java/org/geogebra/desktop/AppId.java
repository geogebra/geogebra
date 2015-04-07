package org.geogebra.desktop;

import org.geogebra.common.main.App;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;

public class AppId {

	public static void main(String[] args) throws Exception {
		setCurrentProcessExplicitAppUserModelID(AppId.class.getName());

		System.out.println(getCurrentProcessExplicitAppUserModelID());
	}

	// DO NOT DO THIS, IT'S JUST FOR TESTING PURPOSE AS I'M NOT FREEING THE
	// MEMORY
	// AS REQUESTED BY THE DOCUMENTATION:
	//
	// http://msdn.microsoft.com/en-us/library/dd378419%28VS.85%29.aspx
	//
	// "The caller is responsible for freeing this string with CoTaskMemFree
	// when
	// it is no longer needed"
	public static String getCurrentProcessExplicitAppUserModelID() {
		final PointerByReference r = new PointerByReference();

		if (GetCurrentProcessExplicitAppUserModelID(r).longValue() == 0) {
			final Pointer p = r.getValue();

			return p.getString(0, true); // here we leak native memory by
											// lazyness
		}
		return "N/A";
	}

	public static void setCurrentProcessExplicitAppUserModelID(
			final String appID) {
		if (SetCurrentProcessExplicitAppUserModelID(new WString(appID))
				.longValue() != 0)
			App.error("unable to set current process explicit AppUserModelID to: "
					+ appID);
	}

	private static native NativeLong GetCurrentProcessExplicitAppUserModelID(
			PointerByReference appID);

	private static native NativeLong SetCurrentProcessExplicitAppUserModelID(
			WString appID);

	static {
		Native.register("shell32");
	}
}