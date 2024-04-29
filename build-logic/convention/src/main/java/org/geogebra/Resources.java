package org.geogebra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class Resources {

	public static String getString(String resourcePath) throws IOException {
		InputStream in = Resources.class.getResourceAsStream(resourcePath);

		StringBuilder sb = new StringBuilder();
		try (Reader reader = new BufferedReader(
				new java.io.InputStreamReader(in, StandardCharsets.UTF_8))) {
			int c;
			while ((c = reader.read()) != -1) {
				sb.append((char) c);
			}
		}
		return sb.toString();
	}

}
