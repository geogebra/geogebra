package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.geos.inputbox.UserInputConverter;
import org.junit.Test;

public class UserInputConverterTest {
	private UserInputConverter converter = new UserInputConverter();

	@Test
	public void defined2DMatrixShouldStay() {
		assertEquals("{{1},{2}}",
				converter.matrixToUndefined("{{1},{2}}"));
	}

	@Test
	public void empty4x4MatrixToUndefined() {
		assertEquals("{{?,?,?,?},{?,?,?,?},{?,?,?,?},{?,?,?,?}}",
				converter.matrixToUndefined("{{,,,},{,,,},{,,,},{,,,}}"));
	}

	@Test
	public void emptyVectorToUndefined() {
		assertEquals("{{?},{?},{?}}",
				converter.matrixToUndefined("{{},{},{}}"));
	}

	@Test
	public void semiEmpty4x4MatrixToUndefined() {
		converter = new UserInputConverter();
		assertEquals("{{1,?,?,?},{?,2,?,?},{?,?,3,?},{?,4,5,6}}",
				converter.matrixToUndefined("{{1,,,},{,2,,},{,,3,},{,4,5,6}}"));
	}
}
