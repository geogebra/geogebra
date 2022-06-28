package org.geogebra.common.kernel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

public class ConstructionTest extends BaseUnitTest {

	private Construction cons;

	@Before
	public void setUp() {
		cons = getConstruction();
	}

	@Test
	public void testLookupLabelWithDollars() {
		assertThat(cons.lookupLabel("$$$", false), is(nullValue()));
		assertThat(cons.lookupLabel("$$$", true), is(nullValue()));
	}

	@Test
	public void testLookupLabelWithEmptyString() {
		assertThat(cons.lookupLabel("", false), is(nullValue()));
		assertThat(cons.lookupLabel("", true), is(nullValue()));
	}
}
