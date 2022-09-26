package org.geogebra.common.gui.view.table;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.settings.TableSettings;
import org.junit.Test;

public class TableValuesViewInitializationTest extends BaseUnitTest {

	@Test
	public void testInitializationWithSettings() {
		TableSettings ts = getSettings().getTable();
		ts.setValuesStep(1);
		ts.setValuesMin(-2);
		ts.setValuesMax(2);
		TableValuesView tv = new TableValuesView(getKernel());
		assertThat(tv.getTableValuesModel().getRowCount(), is(5));
	}
}
