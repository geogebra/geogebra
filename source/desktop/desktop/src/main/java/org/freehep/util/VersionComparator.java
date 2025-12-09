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

package org.freehep.util;

import java.util.Comparator;

/**
 *
 * @author Tony Johnson
 * @version $Id: VersionComparator.java,v 1.3 2008-05-04 12:22:38 murkle Exp $
 */
public class VersionComparator implements Comparator {
	private static String[] special = { "alpha", "beta", "rc" };
	private static String pattern = "\\.+";

	/**
	 * Compares two version numbers of the form 1.2.3.4
	 * 
	 * @return &gt;0 if v1&gt;v2, &lt;0 if v1&lt;v2 or 0 if v1=v2
	 */
	public int versionNumberCompare(String v1, String v2)
			throws NumberFormatException {
		String[] t1 = replaceSpecials(v1).split(pattern);
		String[] t2 = replaceSpecials(v2).split(pattern);
		int maxLength = Math.max(t1.length, t2.length);
		int i = 0;
		for (; i < maxLength; i++) {
			int i1 = i < t1.length ? Integer.parseInt(t1[i]) : 0;
			int i2 = i < t2.length ? Integer.parseInt(t2[i]) : 0;

			if (i1 == i2) {
				continue;
			}
			return i1 - i2;
		}
		return 0;
	}

	private static String replaceSpecials(String in) {
		for (int i = 0; i < special.length; i++) {
			int j = -special.length + i;
			in = in.replaceAll(special[i], "." + j + ".");
		}
		return in;
	}

	@Override
	public int compare(Object obj, Object obj1) {
		return versionNumberCompare(obj.toString(), obj1.toString());
	}
}