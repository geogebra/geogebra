package org.geogebra.common.spreadsheet.kernel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

public class KernelTabularDataAdapterTest extends BaseUnitTest {

	private KernelTabularDataAdapter tabularData;

	@Before
	public void setupData() {
		tabularData = new KernelTabularDataAdapter(getApp().getSettings().getSpreadsheet(),
				getKernel());
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
}
