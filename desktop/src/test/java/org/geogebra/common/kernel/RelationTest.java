package org.geogebra.common.kernel;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.factories.CASFactoryD;
import org.geogebra.desktop.factories.UtilFactoryD;
import org.junit.Test;

public class RelationTest extends BaseUnitTest {

	@Test
	public void moreButtonShouldShowNDGS() {
		UtilFactory.setPrototypeIfNull(new UtilFactoryD());
		getApp().setCASFactory(new CASFactoryD());
		GeoElement A = add("A=(0,0)");
		GeoElement B = add("B=(0,1)");
		add("f=Line(A,B)");
		GeoElement C = add("C=Midpoint(A,B)");
		Relation rel = new Relation(getApp(), A, B, C, null);
		assertThat(rel.getRows()[0].getInfo(),
				containsString("collinear"));
		assertThat(rel.getExpandedRow(0).getInfo(),
				allOf(containsString("under the condition"),
						containsString("are not equal")));
	}
}
