package org.geogebra.common.gui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class GeoTabberTest extends BaseUnitTest {

	@Test
	public void textHighlight() {
		GeoElement txt = add("\"content\"");
		getApp().getSelectionManager().addSelectedGeo(txt);
		getApp().setShowToolBar(false);
		Drawable d = (Drawable) getApp().getActiveEuclidianView().getDrawableND(txt);
		assertThat("not highlighted by default", d.isHighlighted(), is(false));
		getApp().getSelectionManager().removeSelectedGeo(txt);
		GeoTabber tab = new GeoTabber(getApp());
		tab.focusNext();
		assertThat("highlighted from keyboard", d.isHighlighted(), is(true));
		getApp().getAccessibilityManager().setTabOverGeos();
		assertThat("not highlighted after click", d.isHighlighted(), is(false));
		getApp().setShowToolBar(true);
		assertThat("highlighted in full app", d.isHighlighted(), is(true));
	}
}
