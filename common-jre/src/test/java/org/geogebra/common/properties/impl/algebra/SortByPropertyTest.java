package org.geogebra.common.properties.impl.algebra;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.junit.Test;

public class SortByPropertyTest  extends BaseUnitTest {

	@Test
	public void shouldStayAfterReload() {
		SortByProperty sortProp = new SortByProperty(getSettings().getAlgebra(), getLocalization());
		sortProp.setValue(AlgebraView.SortMode.LAYER);
		String xml = getApp().getXML();
		getApp().getSettings().resetSettings(getApp());
		assertThat(sortProp.getValue(), equalTo(AlgebraView.SortMode.TYPE));
		getApp().setXML(xml, true);
		assertThat(getSettings().getAlgebra().getTreeMode(), equalTo(AlgebraView.SortMode.LAYER));
	}
}
