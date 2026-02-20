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

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.util.StringUtil;

/**
 * Test implementation of {@link ScreenReaderAdapter} that captures
 * accessibility announcements for verification in unit tests.
 *
 * <p>Announcements are recorded in order and can be retrieved as
 * a joined string or as a list. Delayed reads are treated the same
 * as immediate reads, and cancelReadDelayed clears all pending announcements.</p>
 */
public class EchoScreenReader implements ScreenReaderAdapter {

	private List<String> announcements = new ArrayList<>();

	@Override
	public void readText(String text) {
		announcements.add(text);
	}

	@Override
	public void readDelayed(String text) {
		readText(text);
	}

	@Override
	public void cancelReadDelayed() {
		announcements.clear();
	}

	/**
	 * @return all announcements joined into a single string, separated by {@code ";"}
	 */
	public String getAnnouncements() {
		return StringUtil.join(";", announcements);
	}

	/**
	 * Clears all captured announcements.
	 */
	public void clear() {
		announcements.clear();
	}

	/**
	 * @return the list of captured announcements in order
	 */
	public final List<String> getAnnouncementsAsList() {
		return new ArrayList<>(announcements);
	}
}
