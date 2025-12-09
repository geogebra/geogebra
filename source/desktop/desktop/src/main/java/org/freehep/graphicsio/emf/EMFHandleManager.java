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

package org.freehep.graphicsio.emf;

import java.util.BitSet;

/**
 * Allocates and frees handles for EMF files
 * 
 * @author Tony Johnson
 * @version $Id: EMFHandleManager.java,v 1.4 2009-08-17 21:44:45 murkle Exp $
 */
public class EMFHandleManager {
	private BitSet handles = new BitSet();

	private int maxHandle;

	public int getHandle() {
		int handle = nextClearBit();
		handles.set(handle);
		if (handle > maxHandle) {
			maxHandle = handle;
		}
		return handle;
	}

	public int freeHandle(int handle) {
		handles.clear(handle);
		return handle;
	}

	private int nextClearBit() {
		// return handles.nextClearBit(1); // JDK 1.4
		for (int i = 1;; i++) {
			if (!handles.get(i)) {
				return i;
			}
		}
	}

	public int highestHandleInUse() {
		return handles.length() - 1;
	}

	public int maxHandlesUsed() {
		return maxHandle + 1;
	}
}
