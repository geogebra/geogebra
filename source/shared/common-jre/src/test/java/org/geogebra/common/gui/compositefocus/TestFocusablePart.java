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

/**
 * Simple {@link FocusablePart} implementation used in unit tests.
 *
 * <p>This test helper tracks whether it has been focused or blurred,
 * supplies a stable focus key for selection persistence tests, and
 * reports whether it requests native focus. It does not involve
 * UI or platform behavior; it serves as a lightweight part in composite
 * focus tests.</p>
 */
public class TestFocusablePart implements FocusablePart {

	private boolean focused;
	private boolean enterKey;
	private String label;
	private String key;

	/**
	 * Creates a test focusable part.
	 *
	 * @param label the accessible label used by screen reader tests
	 * @param key the stable focus key identifying this part
	 * @param enterKey whether this part requests native focus
	 */
	public TestFocusablePart(String label, String key, boolean enterKey) {
		this.enterKey = enterKey;
		this.label = label;
		this.key = key;
	}

	@Override
	public void focus() {
		focused = true;
	}

	@Override
	public void blur() {
		focused = false;
	}

	@Override
	public boolean handlesEnterKey() {
		return enterKey;
	}

	@Override
	public String getAccessibleLabel() {
		return label;
	}

	@Override
	public String getFocusKey() {
		return key;
	}
}
