package org.geogebra.cas;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.geogebra.common.cas.CasParserToolsImpl;
import org.junit.Before;
import org.junit.Test;


public class CasParserToolsImplTest {
	
	CasParserToolsImpl parserTools;
	
	@Before
	public void createParserTools() {
		parserTools = new CasParserToolsImpl('b');
	}
	
	@Test
	public void convertFromMathPiper() {
		parserTools = new CasParserToolsImpl('e');
		assertThat(parserTools.convertScientificFloatNotation("3.4e-5"), equalTo("3.4E-5"));
	}
	
	@Test
	public void convertFromMaxima() {
		assertThat(parserTools.convertScientificFloatNotation("3.4b-5"), equalTo("3.4E-5"));
	}
	
	@Test
	public void convertExponentCharacterIfPreceedingCharIsDigitIgnoreRest() {
		assertThat(parserTools.convertScientificFloatNotation("3b"), equalTo("3E"));
		assertThat(parserTools.convertScientificFloatNotation("3bb"), equalTo("3Eb"));
	}
	
	@Test
	public void onlyConvertExponentCharacterIfPreceedingCharIsDigit() {
		assertThat(parserTools.convertScientificFloatNotation("3.b-5"), equalTo("3.b-5"));
		assertThat(parserTools.convertScientificFloatNotation("bb"), equalTo("bb"));
	}
	
	@Test
	public void convertMultipleOccurences() {
		assertThat(parserTools.convertScientificFloatNotation("(3.4b-2)/(0.2b+3)"), equalTo("(3.4E-2)/(0.2E+3)"));
		assertThat(parserTools.convertScientificFloatNotation("2b4b"), equalTo("2E4b"));
	}
	
}
