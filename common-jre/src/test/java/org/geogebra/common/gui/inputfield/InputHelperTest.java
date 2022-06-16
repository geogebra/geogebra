package org.geogebra.common.gui.inputfield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class InputHelperTest extends BaseUnitTest {

	@Test
	public void testNeedsAutocomplete() {
		assertThat(InputHelper.needsAutocomplete("$$$", getKernel()), is(true));
	}
}
