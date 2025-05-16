package org.geogebra.common.kernel.cas;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoLengthFunctionTest extends BaseUnitTest {

	@Test
	@Issue("APPS-6513")
	public void functionLengthWithVerticalSegment() {
		assertThat(add("Length(sqrt(x-1),1,2)"), hasValue("1.48"));
		assertThat(add("Length(1/(x-1),1,2)"), hasValue("?"));
	}
}
