package org.geogebra.common.gui.view.table;

import static org.junit.Assert.*;

import org.geogebra.common.gui.view.table.dimensions.LaTeXTextSizeMeasurer;
import org.geogebra.common.io.FactoryProviderCommon;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class LaTeXTextSizeMeasurerTests {

	private LaTeXTextSizeMeasurer measurer;

	@Before
	public void setup() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		measurer = new LaTeXTextSizeMeasurer(10);
	}

	@Test
	public void testValidFormula() {
		int width = measurer.getWidth("10");
		assertTrue(width > 0);
	}

	/** APPS-4584 */
	@Test
	public void testInvalidFormula() {
		int width = measurer.getWidth("&"); // this should not throw
		assertEquals(0, width);
	}
}
