package org.geogebra.editor.share.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.editor.share.tree.Korean;
import org.junit.jupiter.api.Test;

public class KoreanTest {

	@Test
	public void flattenKorean() {
		assertEquals("\u1103\u116e\u11af", Korean.flattenKorean("\uB458"));

		assertEquals("\u1103\u116E", Korean.flattenKorean("\uB450"));
	}
}
