package org.geogebra.keyboard.scientific.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ScientificLetterRowsBuilderTest {

	private ScientificLetterRowsBuilder rowsBuilder;

	@Before
	public void setUp() {
		rowsBuilder = new ScientificLetterRowsBuilder();
	}

	@Test
	public void rowsFrom() {
		String topRow = "qwertyuiop";
		String middleRow = "asdfghjkl";
		String bottomRow = "zxcvbnm";
		String[] rows = rowsBuilder.rowsFrom(topRow, middleRow, bottomRow);
		assertEquals("abcdefghi", rows[0]);
		assertEquals("jklmnopqr", rows[1]);
		assertEquals("stuvwxyz", rows[2]);
	}
}