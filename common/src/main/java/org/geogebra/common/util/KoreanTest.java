package org.geogebra.common.util;

public class KoreanTest {
	public static void main(String[] args) {

		String[] test = { "\uB098", "\uB108", "\uC6B0\uB9AC", "\uBBF8\uBD84",
				"\uBCA1\uD130", "\uC0C1\uC218", "\uB2ED\uBA39\uC5B4",
				"\uC6EC\uC77C", "\uC801\uBD84", "\uC288\uD37C\uB9E8",
				"\u3137\u3137", "\uC778\uD14C\uADF8\uB784", "\u3137", "\u3131",
				"\u3134", "asdf", "\uC8FC\uC778\uC7A5",
				"\uC774\uC81C\uC880\uC790\uC790",
				"\uC544 \uBAA8\uB974\uACA0\uB2E4" };
		String flat;
		String unflat;

		System.out.println("TEST 1");

		for (int i = 0; i < test.length; i++) {
			flat = Korean.flattenKorean(test[i]);
			unflat = Korean.unflattenKorean(flat).toString();

			if (unflat.equals(test[i])) {
				// System.out.println("OK");
			} else {
				System.err.println("not OK");
				System.err.println(
						test[i] + " " + StringUtil.toHexString(test[i]));
				System.err.println(flat + " " + StringUtil.toHexString(flat));
				System.err
						.println(unflat + " " + StringUtil.toHexString(unflat));
			}
		}

		System.out.println("TEST 2 Lead + Tail");
		for (char i = 0x1100; i <= 0x1112; i++) {
			for (char j = 0x1161; j <= 0x1175; j++) {
				String s = i + "" + j;
				System.out.println(i + " " + j + " -> "
						+ Korean.unflattenKorean(s).toString());
			}
		}

		System.out.println("TEST 3");

		for (char i = 0xac00; i <= 0xD788; i += 1) {
			String s = i + "";
			// System.out.println(i + " " + StringUtil.toHexString(s) + " "
			// + Korean.isKoreanLeadPlusVowelChar(i)
			// + (((i + 12) % 28) == 0));

			if ((((i + 12) % 28) == 0) != Korean.isKoreanLeadPlusVowelChar(i)) {
				System.out.println("error " + i);
			}
		}
	}

}