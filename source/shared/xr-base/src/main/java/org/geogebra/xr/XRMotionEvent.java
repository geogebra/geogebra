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

package org.geogebra.xr;

public interface XRMotionEvent  {
	public static int FIRST_FINGER_DOWN = 0;
	public static int FIRST_FINGER_UP = 1;
	public static int ON_MOVE = 2;
	public static int ACTION_CANCEL = 3;
	public static int SECOND_FINGER_DOWN = 5;
	public static int SECOND_FINGER_UP = 6;

	int getPointerCount();

	int getAction();

	Object getXREvent();
}
