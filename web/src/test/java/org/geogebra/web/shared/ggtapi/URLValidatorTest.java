package org.geogebra.web.shared.ggtapi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.gwtproject.regexp.server.JavaRegExpFactory;
import org.gwtproject.regexp.shared.RegExpFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class URLValidatorTest {
	private static URLValidator validator;

	@BeforeClass
	public static void setup() {
		RegExpFactory.setPrototypeIfNull(new JavaRegExpFactory());
		validator = new URLValidator();
	}

	@Test
	public void testSimpleUrl() {
		assertTrue(validator.isValid("https://geogebra.org"));
	}

	@Test
	public void testSimpleUrlWithPort() {
		assertTrue(validator.isValid("https://geogebra.org:1234/"));
	}

	@Test
	public void testSimpleUrlWithMorePort() {
		assertFalse(validator.isValid("https://geogebra.org:1234:1234/"));
	}

	@Test
	public void testSimpleUrlWithPortWithoutSlash() {
		assertTrue(validator.isValid("https://geogebra.org:1234"));
	}

	@Test
	public void testUrlWithBadProtocol() {
		assertFalse(validator.isValid("ttps://geogebra.org"));
	}

	@Test
	public void testUrlWithBadHost() {
		assertFalse(validator.isValid("https://geog@ebra.org"));
	}

	@Test
	public void testUrlWithBadPort() {
		assertFalse(validator.isValid("https://geogebra.org:5y34"));
	}

	@Test
	public void testUrlWithQueryString() {
		assertTrue(validator.isValid("https://geogebra.org/?cb=jenkins123&user=xyz"));
	}

	@Test
	public void testUrlWithBracketsCoded() {
		assertTrue(validator.isValid("https://de.wikipedia.org/w/index.php?title=%28Parabel_Mathematik%28"));
	}

	@Test
	public void testUrlWithBrackets() {
		assertTrue(validator.isValid("https://de.wikipedia.org/w/index.php?title=(Parabel_Mathematik)"));
	}

	@Test
	public void testBadUrl() {
		assertFalse(validator.isValid("/imbadReallyBad"));
	}

	@Test
	public void testUrlWithAccent() {
		assertTrue(validator.isValid("http://árvíztűrő.hu/tükörfúrógép.html"));
	}

	@Test
	public void testUrlWithTheta() {
		assertTrue(validator.isValid("https://en.wikipedia.org/wiki/Θ"));
	}
}