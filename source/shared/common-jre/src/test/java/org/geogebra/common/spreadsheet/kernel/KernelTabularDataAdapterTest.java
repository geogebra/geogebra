package org.geogebra.common.spreadsheet.kernel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.Before;
import org.junit.Test;

public class KernelTabularDataAdapterTest extends BaseUnitTest {

	private KernelTabularDataAdapter tabularData;

	@Before
	public void setupData() {
		tabularData = new KernelTabularDataAdapter(getApp());
		getKernel().attach(tabularData);
	}

	@Test
	public void numberOfRowsShouldAdjust() {
		add("A987=1");
		assertThat(tabularData.numberOfRows(), equalTo(987));
	}

	@Test
	public void numberOfColumnsShouldAdjust() {
		add("ZZ1=1");
		assertThat(tabularData.numberOfColumns(), equalTo(702));
	}

	@Test
	public void testContentSerialization() {
		add("A1:x=y");
		// this should be the default string template, not the one for editor
		assertThat(tabularData.serializeContentAt(0, 0), equalTo("x = y"));
	}

	@Test
	public void updateShouldNotChangeStyle() {
		GeoElementND a1 = add("A1:x=y");
		assertTrue(a1.isEuclidianVisible());
		tabularData.update(a1.toGeoElement());
		assertTrue(a1.isEuclidianVisible());
	}

	@Test
	public void columnOperationsShouldUpdateRange() {
		add("A1=1");
		add("A2=2");
		add("A3=3");
		GeoNumeric sum = add("A4=Sum(A1:A3)");
		tabularData.insertColumnAt(0);
		assertEquals("B4 = Sum(B1:B3)", sum.getDefinitionForInputBar());
		tabularData.deleteColumnAt(0);
		assertEquals("A4 = Sum(A1:A3)", sum.getDefinitionForInputBar());
	}

	@Test
	public void rowOperationsShouldUpdateRange() {
		add("A1=1");
		add("A2=2");
		add("A3=3");
		GeoNumeric sum = add("A4=Sum(A1:A3)");
		tabularData.insertRowAt(1);
		assertEquals("A5 = Sum(A1:A4)", sum.getDefinitionForInputBar());
		tabularData.deleteRowAt(1);
		assertEquals("A4 = Sum(A1:A3)", sum.getDefinitionForInputBar());
	}
}
