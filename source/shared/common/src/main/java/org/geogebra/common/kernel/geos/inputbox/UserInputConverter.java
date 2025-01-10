package org.geogebra.common.kernel.geos.inputbox;

import java.util.ArrayList;
import java.util.List;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Converts incomplete user input to a string that Algebra Processor can handle without error,
 * eg (,,) -&gt; (?,?,?)
 */
public class UserInputConverter {

	/**
	 * Converts incomplete but probably valid input of a point
	 * to a processable text eg. (,) -&gt; (?,?) or (1,) -&gt; (1,?)
	 *
	 * @param text incomplete input for points.
	 * @return the completed input for AlgebraProcessor.
	 */
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
		return text.indexOf(',') == -1 && text.indexOf(Unicode.verticalLine) == -1;
	}

	private String inside(String text) {
		return text.substring(1, text.length() - 1);
	}

	private String replaceCommas(String text) {
		String[] items = text.split("[," + Unicode.verticalLine + "]", -1);
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

	/**
	 * Converts incomplete but probably valid input of a matrix
	 * to a processable text eg. {{,}, {,}} &gt; {{?,?}, {?,?}}
	 *
	 * @param text incomplete input for matrix.
	 * @return the completed input for AlgebraProcessor.
	 */
	public String matrixToUndefined(String text) {
		String content = inside(text);
		String[] rows = content.split("\\},\\{");
		List<String> list = new ArrayList<>();

		for (String row: rows) {
			list.add(replaceCommas(row.replace("{", "").replace("}", "")));
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
