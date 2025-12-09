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
 
package org.geogebra.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

public class TestMaterialCallback implements MaterialCallbackI {
	private final ArrayList<String> titles = new ArrayList<>();
	private final ArrayList<String> errors = new ArrayList<>();
	private int expectedCount = 1;
	private boolean loaded;

	@Override
	public final void onLoaded(List<Material> result, Pagination meta) {
		loaded = true;
		for (int i = 0; i < result.size(); i++) {
			String title = result.get(i).getTitle();
			if (handleMaterial(result.get(i))) {
				titles.add(title);
			}
		}
	}

	protected boolean handleMaterial(Material material) {
		return true;
		// overridden in subclasses
	}

	@Override
	public final void onError(Throwable exception) {
		errors.add("API error:" + exception.getMessage());
	}

	protected void verify(String title) {
		assertEquals("", StringUtil.join(",", errors));
		assertEquals(title, StringUtil.join(",", titles));
	}

	protected void verifyError(String errorPattern) {
		String errorsS = StringUtil.join(",", errors);
		assertTrue(errorsS, errorsS.matches(errorPattern));
	}

	/**
	 * Periodically check if it's done, if not fail after timeout.
	 * 
	 * @param time
	 *            timeout in seconds
	 */
	public void await(int time) {
		for (int i = 0; i < time * 5; i++) {
			Log.debug(titles.size() + " of " + expectedCount);
			if ((loaded && titles.size() >= expectedCount)
					|| !errors.isEmpty()) {
				return;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Log.warn("cannot sleep");
			}
		}
	}

	public void setExpectedCount(int size) {
		expectedCount = size;
	}

}
