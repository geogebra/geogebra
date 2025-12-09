/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop;

import org.geogebra.common.util.debug.Log;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;

public class AppId {

	// DO NOT DO THIS, IT'S JUST FOR TESTING PURPOSE AS I'M NOT FREEING THE
	// MEMORY
	// AS REQUESTED BY THE DOCUMENTATION:
	//
	// http://msdn.microsoft.com/en-us/library/dd378419%28VS.85%29.aspx
	//
	// "The caller is responsible for freeing this string with CoTaskMemFree
	// when
	// it is no longer needed"

	/**
	 * <a href="https://learn.microsoft.com/en-us/windows/win32/api/shobjidl_core/nf-shobjidl_core-getcurrentprocessexplicitappusermodelid">docs</a>
	 * @return user model ID
	 */
	public static String getCurrentProcessExplicitAppUserModelID() {
		final PointerByReference r = new PointerByReference();

		if (GetCurrentProcessExplicitAppUserModelID(r).longValue() == 0) {
			final Pointer p = r.getValue();

			return p.getWideString(0); // here we leak native memory by
											// laziness
		}
		return "N/A";
	}

	/**
	 * <a href="https://learn.microsoft.com/en-us/windows/win32/api/shobjidl_core/nf-shobjidl_core-setCurrentProcessExplicitAppUserModelID">docs</a>
	 * @param appID user model ID
	 */
	public static void setCurrentProcessExplicitAppUserModelID(
			final String appID) {
		if (SetCurrentProcessExplicitAppUserModelID(new WString(appID))
				.longValue() != 0) {
			Log.error(
					"unable to set current process explicit AppUserModelID to: "
							+ appID);
		}
	}

	@SuppressWarnings("CheckStyle.MethodName")
	private static native NativeLong GetCurrentProcessExplicitAppUserModelID(
			PointerByReference appID);

	@SuppressWarnings("CheckStyle.MethodName")
	private static native NativeLong SetCurrentProcessExplicitAppUserModelID(
			WString appID);

	static {
		Native.register("shell32");
	}
}