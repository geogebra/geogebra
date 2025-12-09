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

package org.geogebra.common.kernel.geos.properties;

/** enum for auxiliary state */
public enum Auxiliary {
	/** is auxiliary */
	YES_DEFAULT(true, false) {
		@Override
		public Auxiliary toggle() {
			return Auxiliary.NO_SAVE;
		}
	},
	/** is not auxiliary */
	NO_DEFAULT(false, false) {
		@Override
		public Auxiliary toggle() {
			return Auxiliary.YES_SAVE;
		}
	},
	/** is not auxiliary, needs to save to XML */
	NO_SAVE(false, true) {
		@Override
		public Auxiliary toggle() {
			return YES_DEFAULT;
		}
	},
	/** is auxiliary, needs to save to XML */
	YES_SAVE(true, true) {
		@Override
		public Auxiliary toggle() {
			return NO_DEFAULT;
		}
	};
	
	private final boolean isOn;
	private final boolean needsSaveToXML;

	private Auxiliary(boolean isOn, boolean needsSaveToXML) {
		this.isOn = isOn;
		this.needsSaveToXML = needsSaveToXML;
	}
	
	/**
	 * 
	 * @return true if is auxiliary
	 */
	public boolean isOn() {
		return isOn;
	}

	/**
	 * 
	 * @return true if it needs save to XML
	 */
	public boolean needsSaveToXML() {
		return needsSaveToXML;
	}

	/**
	 * 
	 * @return the opposite value
	 */
	abstract public Auxiliary toggle();
}