package org.geogebra.common.util;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class CopyPasteTest extends BaseUnitTest {

	@Test
	@Issue("APPS-5464")
	public void shouldCopyPointOnAxis() {
		add("A=(1,1)");
		add("B=(3,2)");
		add("C=Point(xAxis)");
		GeoElement poly = add("t=Polygon(A,B,C)");
		InternalClipboard.duplicate(getApp(), Collections.singletonList(poly));
		assertThat(add("t==t_1"), hasValue("true"));
	}

	@Test
	public void shouldCopyPointOnLine() {
		add("A=(1,1)");
		add("B=(3,2)");
		add("C=Point(x=0)");
		GeoElement poly = add("t=Polygon(A,B,C)");
		InternalClipboard.duplicate(getApp(), Collections.singletonList(poly));
		assertThat(add("t==t_1"), hasValue("true"));
	}
}
