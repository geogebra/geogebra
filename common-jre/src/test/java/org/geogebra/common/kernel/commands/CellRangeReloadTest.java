package org.geogebra.common.kernel.commands;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Test;

public class CellRangeReloadTest extends BaseUnitTest {
	@Test
	public void cellRangeShouldKeepType() {
		add("range=A1:A2");
		add("A1=\"foo\"");
		add("A2=\"bar\"");
		add("rotated=RotateText(Element(range,1),320deg)");
		assertThat(lookup("rotated"), hasValue("\\rotatebox{320.0}{ \\text{ foo }  }"));
		reload();
		assertThat(lookup("rotated"), hasValue("\\rotatebox{320.0}{ \\text{ foo }  }"));
	}

	@Test
	public void cellRangeReferenceShouldKeepType() {
		add("range=A1:A2");
		add("A1=\"foo\"");
		add("A2=\"bar\"");
		add("rangeRef=range");
		add("rotated=RotateText(Element(rangeRef,1),320deg)");
		assertThat(lookup("rotated"), hasValue("\\rotatebox{320.0}{ \\text{ foo }  }"));
		reload();
		assertThat(lookup("rotated"), hasValue("\\rotatebox{320.0}{ \\text{ foo }  }"));
	}

	@Test
	public void cellRangeShouldBeFullCommandInXML() {
		GeoElement range = add("A1:A3");
		add("A1=(1,2)");
		assertEquals("CellRange(A1,A3,\"point\")", range.getParentAlgorithm()
				.toString(StringTemplate.xmlTemplate));
	}

	@Test
	public void cellRangeOfUnknownTypeShouldNotBeFullCommandInXML() {
		GeoElement range = add("A1:A3");
		assertEquals("A1:A3", range.getParentAlgorithm()
				.toString(StringTemplate.xmlTemplate));
	}

	@Test
	public void cmdCellRangeWith3ArgsShouldProvideTypeHint() {
		add("l1=CellRange(A1,A3, \"point\")");
		GeoElement pt = add("Element(l1,1)");
		assertThat(pt, instanceOf(GeoPoint.class));
		add("l2=CellRange(B1,B3, \"boolean\")");
		GeoElement bool = add("Element(l2,1)");
		assertThat(bool, instanceOf(GeoBoolean.class));
		add("l3=CellRange(C1,C3, \"button\")");
		GeoElement btn = add("Element(l3,1)");
		assertThat(btn, instanceOf(GeoButton.class));
	}
}
