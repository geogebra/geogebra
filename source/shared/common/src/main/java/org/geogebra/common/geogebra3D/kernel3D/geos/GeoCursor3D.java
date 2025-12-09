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

package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.main.App;

/**
 * Extension of GeoPoint3D for 3D view cursor
 */
public class GeoCursor3D extends GeoPoint3D {

	static final public long NO_SOURCE = App.CE_ID_COUNTER_START - 1;
	static final public long CAPTURED = App.CE_ID_COUNTER_START - 2;

	private boolean isCaptured;
	private long source1;
	private long source2;

	/**
	 * constructor
	 * 
	 * @param c
	 *            construction
	 */
	public GeoCursor3D(Construction c) {
		super(c);
	}

	/**
	 * set that the cursor is captured (e.g. snapped to grid)
	 * 
	 * @param flag
	 *            flag
	 */
	public void setIsCaptured(boolean flag) {
		isCaptured = flag;
	}

	/**
	 * 
	 * @return true if the cursor is captured (e.g. snapped to grid)
	 */
	public boolean getIsCaptured() {
		return isCaptured;
	}

	/**
	 * set cursor source
	 * @param source source
	 */
	public void setSource(long source) {
		setSource(source, NO_SOURCE);
	}

	/**
	 * set cursor sources
	 * @param source1 first source
	 * @param source2 second source
	 */
	public void setSource(long source1, long source2) {
		this.source1 = source1;
		this.source2 = source2;
	}

	/**
	 *
	 * @return first source for cursor
	 */
	public long getSource1() {
		return source1;
	}

	/**
	 *
	 * @return second source for cursor
	 */
	public long getSource2() {
        return source2;
    }

}
