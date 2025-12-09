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

package org.geogebra.common.properties.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Before;
import org.junit.Test;

public class DividerIndicesViewAdapterTest {

	private DividerIndicesViewAdapter adapter;

	@Before
	public void setUp() {
		adapter = new DividerIndicesViewAdapter(new int[]{1, 3}, 5);
	}

	@Test
	public void testLength() {
		assertThat(adapter.getViewCount(), is(7));
	}

	@Test
	public void testIsDivider() {
		for (int i = 0; i < adapter.getViewCount(); i++) {
			assertThat(adapter.isDivider(i), is(i == 1 || i == 4));
		}
	}

	@Test
	public void testConvertViewIndexToModel() {
		assertThat(adapter.convertViewIndexToModel(0), is(0));
		assertThat(adapter.convertViewIndexToModel(2), is(1));
		assertThat(adapter.convertViewIndexToModel(3), is(2));
		assertThat(adapter.convertViewIndexToModel(5), is(3));
		assertThat(adapter.convertViewIndexToModel(6), is(4));
	}

	@Test
	public void testConvertModelIndexToView() {
		assertThat(adapter.convertModelIndexToView(0), is(0));
		assertThat(adapter.convertModelIndexToView(1), is(2));
		assertThat(adapter.convertModelIndexToView(2), is(3));
		assertThat(adapter.convertModelIndexToView(3), is(5));
		assertThat(adapter.convertModelIndexToView(4), is(6));
	}
}
