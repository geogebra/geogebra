package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

/**
 * Tests for CopyFreeObject command
 */
public class CopyFreeObjectTest extends BaseUnitTest {

	/**
	 * Check that all properties are maintained for points
	 */
	@Test
	public void copyShouldHaveSameXML() {
		add("A=(1,1)");
		lookup("A").setAnimationStep(2.0);
		String aXml = lookup("A").getXML();
		add("B=CopyFreeObject(A)");
		add("Delete(A)");
		add("Rename(B,A)");
		assertEquals(aXml, lookup("A").getXML());
	}
}
