package org.geogebra.common.kernel.geos.inputbox;

import java.util.ArrayList;
import java.util.List;

public class UserInputConverter {

	public String pointToUndefined(String text) {
		if (noBrackets(text) || noCommas(text)) {
			return text;
		}
		return "(" + replaceCommas(inside(text)) + ")";
	}

	private boolean noBrackets(String text) {
		return !(text.startsWith("(") && text.endsWith(")"));
	}

	private boolean noCommas(String text) {
		return text.indexOf(',') == -1;
	}

	private String inside(String text) {
		return text.substring(1, text.length() - 1);
	}

	public String replaceCommas(String text) {
		String[] items = text.split(",", -1);
		String separator = "";
		StringBuilder sb = new StringBuilder();
		for (String item: items) {
			sb.append(separator);
			if ("".equals(item)) {
				sb.append("?");
			} else {
				sb.append(item);
			}
			separator = ",";
		}
		return sb.toString();
	}

	public String matrixToUndefined(String text) {
		String content = inside(text);
		String[] rows = content.split("\\},\\{");
		List<String> list = new ArrayList<>();

		for (String row: rows) {
			list.add(replaceCommas(row.replace("{","").replace("}","")));
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < list.size() - 1; i++) {
			wrapRow(list, sb, i);
			sb.append(",");
		}
		wrapRow(list, sb, list.size() - 1);
		sb.append("}");
		return sb.toString();
	}

	private void wrapRow(List<String> list, StringBuilder sb, int i) {
		sb.append("{");
		sb.append(list.get(i));
		sb.append("}");
	}
}
