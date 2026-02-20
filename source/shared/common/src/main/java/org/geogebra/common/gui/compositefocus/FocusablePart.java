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

package org.geogebra.common.gui.compositefocus;

public interface FocusablePart {

	/** Apply focus to this part */
	void focus();

	/** Remove focus from this part */
	void blur();

	/** @return true if this part handles enter key */
	boolean handlesEnterKey();

	/** @return text to announce to screen readers */
	String getAccessibleLabel();

	/**
	 * Returns the stable semantic key identifying this focusable part.
	 *
	 * <p>The key is used to preserve focus across rebuilds when the underlying
	 * widget instance changes.</p>
	 *
	 * @return a stable identifier for this part
	 */
	String getFocusKey();
}
