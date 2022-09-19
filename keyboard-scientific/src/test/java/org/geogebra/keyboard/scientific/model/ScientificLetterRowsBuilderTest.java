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
		assertEquals(rows[0], "abcdefghi");
		assertEquals(rows[1], "jklmnopqr");
		assertEquals(rows[2], "stuvwxyz");
	}
}