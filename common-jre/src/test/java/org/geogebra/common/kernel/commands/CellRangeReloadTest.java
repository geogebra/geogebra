package org.geogebra.common.kernel.commands;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
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
}
