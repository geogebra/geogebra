package org.geogebra.common.main.syntax.suggestionfilter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LineSelectorTest {

	@Test
	public void test() {
		assertEquals("a\nc", LineSelector.select("a\nb\nc", 0, 2));
	}
}
