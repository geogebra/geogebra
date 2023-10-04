package org.geogebra.common.kernel.commands;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.spreadsheet.CellRange;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.AlgoCellRange;
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
		assertEquals("CellRange(A1,A3,\"point\")", range.getParentAlgorithm()
				.toString(StringTemplate.xmlTemplate));
	}

	@Test
	public void cmdCellRangeWith3ArgsShouldCreateObjects() {
		checkCellObjects(add("CellRange(A1,A3, \"point\")"),
				"point",
				"A1", "A2", "A3");
		checkCellObjects(add("CellRange(B12,D14, \"boolean\")"),
				"boolean",
				"B12", "B13", "B14",
				"C12", "C13", "C14",
				"D12", "D13", "D14");
		checkCellObjects(add("CellRange(E3,E4, \"button\")"),
				"button",
				"E3", "E4");

	}

	private void checkCellObjects(GeoList l1, String elemType, String... labels) {
		List<String> actualLabels = l1.elements().
		filter(geo -> geo.isAuxiliaryObject() && geo.getXMLtypeString().equals(elemType))
				.flatMap(geo -> Stream.of(geo.getLabelSimple()))
				.collect(Collectors.toList());

		assertEquals(Arrays.asList(labels), actualLabels);
	}
}
