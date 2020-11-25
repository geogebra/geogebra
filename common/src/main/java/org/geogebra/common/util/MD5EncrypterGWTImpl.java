package org.geogebra.common.util;

/*
 * Copyright 2010 Nicolas Lochet Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the 
 * License. You may obtain a copy of the License at
 *      
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

/**
 * MD5sum calculator implementation in GWT
 * 
 * downloaded from
 * https://java2s.com/Open-Source/Java/GWT/gwt-util/org/nicocube/
 * gwt/util/client/crypt/md5/MD5EncrypterGWTImpl.java.htm
 * 
 * see also
 * https://java2s.com/Open-Source/Java/GWT/gwt-util/org/nicocube/gwt/util
 * /client/crypt/md5/MD5EncrypterTest.java.htm
 * https://java2s.com/Open-Source/Java
 * /GWT/gwt-util/org/nicocube/gwt/util/client/
 * crypt/md5/MD5EncryptJSNIImpl.java.htm
 *
 * @author Nicolas Lochet
 * @version 0.5
 * @since 0.5
 */
public class MD5EncrypterGWTImpl {

	/**
	 * @param string0
	 *            text
	 * @return encoded text
	 */
	public static String encrypt(String string0) {

		int[] x;
		int k, AA, BB, CC, DD, a, b, c, d;
		int S11 = 7, S12 = 12, S13 = 17, S14 = 22;
		int S21 = 5, S22 = 9, S23 = 14, S24 = 20;
		int S31 = 4, S32 = 11, S33 = 16, S34 = 23;
		int S41 = 6, S42 = 10, S43 = 15, S44 = 21;

		String string = utf8Encode(string0);

		x = convertToWordArray(string);

		a = 0x67452301;
		b = 0xEFCDAB89;
		c = 0x98BADCFE;
		d = 0x10325476;

		for (k = 0; k < x.length - 15; k += 16) {
			AA = a;
			BB = b;
			CC = c;
			DD = d;
			a = ff(a, b, c, d, x[k + 0], S11, 0xD76AA478);
			d = ff(d, a, b, c, x[k + 1], S12, 0xE8C7B756);
			c = ff(c, d, a, b, x[k + 2], S13, 0x242070DB);
			b = ff(b, c, d, a, x[k + 3], S14, 0xC1BDCEEE);
			a = ff(a, b, c, d, x[k + 4], S11, 0xF57C0FAF);
			d = ff(d, a, b, c, x[k + 5], S12, 0x4787C62A);
			c = ff(c, d, a, b, x[k + 6], S13, 0xA8304613);
			b = ff(b, c, d, a, x[k + 7], S14, 0xFD469501);
			a = ff(a, b, c, d, x[k + 8], S11, 0x698098D8);
			d = ff(d, a, b, c, x[k + 9], S12, 0x8B44F7AF);
			c = ff(c, d, a, b, x[k + 10], S13, 0xFFFF5BB1);
			b = ff(b, c, d, a, x[k + 11], S14, 0x895CD7BE);
			a = ff(a, b, c, d, x[k + 12], S11, 0x6B901122);
			d = ff(d, a, b, c, x[k + 13], S12, 0xFD987193);
			c = ff(c, d, a, b, x[k + 14], S13, 0xA679438E);
			b = ff(b, c, d, a, x[k + 15], S14, 0x49B40821);
			a = gg(a, b, c, d, x[k + 1], S21, 0xF61E2562);
			d = gg(d, a, b, c, x[k + 6], S22, 0xC040B340);
			c = gg(c, d, a, b, x[k + 11], S23, 0x265E5A51);
			b = gg(b, c, d, a, x[k + 0], S24, 0xE9B6C7AA);
			a = gg(a, b, c, d, x[k + 5], S21, 0xD62F105D);
			d = gg(d, a, b, c, x[k + 10], S22, 0x2441453);
			c = gg(c, d, a, b, x[k + 15], S23, 0xD8A1E681);
			b = gg(b, c, d, a, x[k + 4], S24, 0xE7D3FBC8);
			a = gg(a, b, c, d, x[k + 9], S21, 0x21E1CDE6);
			d = gg(d, a, b, c, x[k + 14], S22, 0xC33707D6);
			c = gg(c, d, a, b, x[k + 3], S23, 0xF4D50D87);
			b = gg(b, c, d, a, x[k + 8], S24, 0x455A14ED);
			a = gg(a, b, c, d, x[k + 13], S21, 0xA9E3E905);
			d = gg(d, a, b, c, x[k + 2], S22, 0xFCEFA3F8);
			c = gg(c, d, a, b, x[k + 7], S23, 0x676F02D9);
			b = gg(b, c, d, a, x[k + 12], S24, 0x8D2A4C8A);
			a = hh(a, b, c, d, x[k + 5], S31, 0xFFFA3942);
			d = hh(d, a, b, c, x[k + 8], S32, 0x8771F681);
			c = hh(c, d, a, b, x[k + 11], S33, 0x6D9D6122);
			b = hh(b, c, d, a, x[k + 14], S34, 0xFDE5380C);
			a = hh(a, b, c, d, x[k + 1], S31, 0xA4BEEA44);
			d = hh(d, a, b, c, x[k + 4], S32, 0x4BDECFA9);
			c = hh(c, d, a, b, x[k + 7], S33, 0xF6BB4B60);
			b = hh(b, c, d, a, x[k + 10], S34, 0xBEBFBC70);
			a = hh(a, b, c, d, x[k + 13], S31, 0x289B7EC6);
			d = hh(d, a, b, c, x[k + 0], S32, 0xEAA127FA);
			c = hh(c, d, a, b, x[k + 3], S33, 0xD4EF3085);
			b = hh(b, c, d, a, x[k + 6], S34, 0x4881D05);
			a = hh(a, b, c, d, x[k + 9], S31, 0xD9D4D039);
			d = hh(d, a, b, c, x[k + 12], S32, 0xE6DB99E5);
			c = hh(c, d, a, b, x[k + 15], S33, 0x1FA27CF8);
			b = hh(b, c, d, a, x[k + 2], S34, 0xC4AC5665);
			a = ii(a, b, c, d, x[k + 0], S41, 0xF4292244);
			d = ii(d, a, b, c, x[k + 7], S42, 0x432AFF97);
			c = ii(c, d, a, b, x[k + 14], S43, 0xAB9423A7);
			b = ii(b, c, d, a, x[k + 5], S44, 0xFC93A039);
			a = ii(a, b, c, d, x[k + 12], S41, 0x655B59C3);
			d = ii(d, a, b, c, x[k + 3], S42, 0x8F0CCC92);
			c = ii(c, d, a, b, x[k + 10], S43, 0xFFEFF47D);
			b = ii(b, c, d, a, x[k + 1], S44, 0x85845DD1);
			a = ii(a, b, c, d, x[k + 8], S41, 0x6FA87E4F);
			d = ii(d, a, b, c, x[k + 15], S42, 0xFE2CE6E0);
			c = ii(c, d, a, b, x[k + 6], S43, 0xA3014314);
			b = ii(b, c, d, a, x[k + 13], S44, 0x4E0811A1);
			a = ii(a, b, c, d, x[k + 4], S41, 0xF7537E82);
			d = ii(d, a, b, c, x[k + 11], S42, 0xBD3AF235);
			c = ii(c, d, a, b, x[k + 2], S43, 0x2AD7D2BB);
			b = ii(b, c, d, a, x[k + 9], S44, 0xEB86D391);
			a = addUnsigned(a, AA);
			b = addUnsigned(b, BB);
			c = addUnsigned(c, CC);
			d = addUnsigned(d, DD);
		}

		String temp = StringUtil.convertToHex(a) + StringUtil.convertToHex(b)
				+ StringUtil.convertToHex(c) + StringUtil.convertToHex(d);
		return temp.toLowerCase();
	}

	/**
	 * @param string0
	 *            input text
	 * @return UTF encoded text
	 */
	public static String utf8Encode(String string0) {
		String string = string0.replaceAll("\r\n", "\n");
		StringBuilder utftext = new StringBuilder();

		for (int n = 0; n < string.length(); n++) {

			char c = string.charAt(n);

			if (c < 128) {
				utftext.append(c);
			} else if (c < 2048) {
				utftext.append((c >> 6) | 192);
				utftext.append((c & 63) | 128);
			} else {
				utftext.append((c >> 12) | 224);
				utftext.append(((c >> 6) & 63) | 128);
				utftext.append((c & 63) | 128);
			}

		}

		return utftext.toString();
	}

	private static int[] convertToWordArray(String string) {
		int lWordCount;
		int lMessageLength = string.length();
		int lNumberOfWords_temp1 = lMessageLength + 8;
		int lNumberOfWords_temp2 = (lNumberOfWords_temp1
				- (lNumberOfWords_temp1 % 64)) / 64;
		int lNumberOfWords = (lNumberOfWords_temp2 + 1) * 16;
		int[] lWordArray = new int[lNumberOfWords];
		int lBytePosition = 0;
		int lByteCount = 0;
		while (lByteCount < lMessageLength) {
			lWordCount = (lByteCount - (lByteCount % 4)) / 4;
			lBytePosition = (lByteCount % 4) * 8;
			lWordArray[lWordCount] = (lWordArray[lWordCount]
					| (string.charAt(lByteCount) << lBytePosition));
			lByteCount++;
		}
		lWordCount = (lByteCount - (lByteCount % 4)) / 4;
		lBytePosition = (lByteCount % 4) * 8;
		lWordArray[lWordCount] = lWordArray[lWordCount]
				| (0x80 << lBytePosition);
		lWordArray[lNumberOfWords - 2] = lMessageLength << 3;
		lWordArray[lNumberOfWords - 1] = lMessageLength >>> 29;
		return lWordArray;
	}

	public static int rotateLeft(int lValue, int iShiftBits) {
		return (lValue << iShiftBits) | (lValue >>> (32 - iShiftBits));
	}

	private static int addUnsigned(int lX, int lY) {
		int lX4, lY4, lX8, lY8, lResult;
		lX8 = (lX & 0x80000000);
		lY8 = (lY & 0x80000000);
		lX4 = (lX & 0x40000000);
		lY4 = (lY & 0x40000000);
		lResult = (lX & 0x3FFFFFFF) + (lY & 0x3FFFFFFF);
		if ((lX4 & lY4) != 0) {
			return (lResult ^ 0x80000000 ^ lX8 ^ lY8);
		}
		if ((lX4 | lY4) != 0) {
			if ((lResult & 0x40000000) != 0) {
				return (lResult ^ 0xC0000000 ^ lX8 ^ lY8);
			}
			return (lResult ^ 0x40000000 ^ lX8 ^ lY8);
		}
		return (lResult ^ lX8 ^ lY8);
	}

	private static int rf(int x, int y, int z) {
		return (x & y) | ((~x) & z);
	}

	private static int rg(int x, int y, int z) {
		return (x & z) | (y & (~z));
	}

	private static int rh(int x, int y, int z) {
		return (x ^ y ^ z);
	}

	private static int ri(int x, int y, int z) {
		return (y ^ (x | (~z)));
	}

	private static int ff(int a, int b, int c, int d, int x, int s, int ac) {
		int a1 = addUnsigned(a, addUnsigned(addUnsigned(rf(b, c, d), x), ac));
		return addUnsigned(rotateLeft(a1, s), b);
	}

	private static int gg(int a, int b, int c, int d, int x, int s, int ac) {
		int a1 = addUnsigned(a, addUnsigned(addUnsigned(rg(b, c, d), x), ac));
		return addUnsigned(rotateLeft(a1, s), b);
	}

	private static int hh(int a, int b, int c, int d, int x, int s, int ac) {
		int a1 = addUnsigned(a, addUnsigned(addUnsigned(rh(b, c, d), x), ac));
		return addUnsigned(rotateLeft(a1, s), b);
	}

	private static int ii(int a, int b, int c, int d, int x, int s, int ac) {
		int a1 = addUnsigned(a, addUnsigned(addUnsigned(ri(b, c, d), x), ac));
		return addUnsigned(rotateLeft(a1, s), b);
	}
}
