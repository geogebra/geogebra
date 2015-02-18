package geogebra.common.gui.inputfield;

import geogebra.common.gui.inputfield.DynamicTextElement.DynamicTextType;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoDependentText;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyStringBuffer;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

import java.util.ArrayList;

/**
 * Utility class with methods for converting a GeoText string into a list of
 * DynamicTextElements and vice-versa.
 * 
 * A GeoText string is composed of static and dynamic substrings separated by
 * quotes. Dynamic substrings reference the labels of other GeoElements. In raw
 * form GeoText strings are difficult for users to handle correctly, so GeoGebra
 * text editors simplify the process by inserting these dynamic strings into
 * special editing containers (e.g. an embedded text field).
 * 
 * @author G. Sturr
 * 
 */
public class DynamicTextProcessor {

	private App app;

	private ArrayList<DynamicTextElement> dList;

	/**
	 * Constructor
	 * 
	 * @param app
	 */
	public DynamicTextProcessor(App app) {
		this.app = app;
		dList = new ArrayList<DynamicTextElement>();
	}

	/**
	 * Converts the string of a GeoText into a list of DynamicTextElements *
	 * 
	 * @param geo
	 *            GeoText
	 * @return list of DynamicTextElements representing the string a the given
	 *         GeoText
	 */
	public ArrayList<DynamicTextElement> buildDynamicTextList(GeoText geo) {

		dList.clear();

		if (geo == null)
			return dList;

		if (geo.isIndependent()) {
			dList.add(new DynamicTextElement(geo.getTextString(),
					DynamicTextType.STATIC));
			return dList;
		}

		// if dependent text then get the root
		ExpressionNode root = ((AlgoDependentText) geo.getParentAlgorithm())
				.getRoot();

		// parse the root and set the text content
		this.splitString(root, dList);
		return dList;

	}

	/**
	 * Parses an expression node into substrings and stores these in a list of
	 * DynamicTextElements.
	 * 
	 * @param en
	 *            node to be parsed
	 * @param dList
	 *            list of DynamicTextElements derived from the given node
	 */
	private void splitString(ExpressionNode en,
			ArrayList<DynamicTextElement> dList) {

		ExpressionValue left = en.getLeft();
		ExpressionValue right = en.getRight();
		StringTemplate tpl = StringTemplate.defaultTemplate;

		if (en.isLeaf()) {

			if (left.isGeoElement()) {
				DynamicTextElement d = createDynamicTextElement(((GeoElement) left)
						.getLabel(tpl));
				// add at end
				dList.add(d);
			} else if (left.isExpressionNode())
				splitString((ExpressionNode) left, dList);
			else if (left instanceof MyStringBuffer) {
				DynamicTextElement d = createDynamicTextElement(left.toString(
						tpl).replaceAll("\"", ""));
				dList.add(d);
			} else {
				DynamicTextElement d = createDynamicTextElement(left
						.toString(tpl));
				dList.add(d);
			}
		}

		// STANDARD case: no leaf
		else {

			if (right != null && !en.containsMyStringBuffer()) {
				// neither left nor right are free texts, eg a+3 in
				// (a+3)+"hello"
				// so no splitting needed
				dList.add(createDynamicTextElement(en.toString(tpl)));
				return;
			}

			// expression node
			if (left.isGeoElement()) {
				dList.add(createDynamicTextElement(((GeoElement) left)
						.getLabel(tpl)));

			} else if (left.isExpressionNode())
				this.splitString((ExpressionNode) left, dList);

			else if (left instanceof MyStringBuffer) {
				dList.add(new DynamicTextElement(left.toString(tpl).replaceAll(
						"\"", ""), DynamicTextType.STATIC));
			} else {
				dList.add(createDynamicTextElement(left.toString(tpl)));
			}

			if (right != null) {
				if (right.isGeoElement()) {
					dList.add(createDynamicTextElement(((GeoElement) right)
							.getLabel(tpl)));

				} else if (right.isExpressionNode())
					this.splitString((ExpressionNode) right, dList);

				else if (right instanceof MyStringBuffer) {

					dList.add(new DynamicTextElement(right.toString(tpl)
							.replaceAll("\"", ""), DynamicTextType.STATIC));
				} else {
					dList.add(createDynamicTextElement(right.toString(tpl)));
				}
			}
		}

	}

	/**
	 * Creates a DynamicTextElement instance from a given string. The string is
	 * processed to remove unnecessary prefixes and evaluated to determine its
	 * dynamic text type.
	 * 
	 * @param text
	 *            text to put in the dynamic field
	 * 
	 * @return DynamicText instance
	 */
	private DynamicTextElement createDynamicTextElement(String text) {

		String contentString = text;
		DynamicTextType type = DynamicTextType.VALUE;
		String prefix;

		if (contentString.endsWith("]")) {
			if (contentString.startsWith(prefix = app.getLocalization()
					.getCommand("LaTeX") + "[")) {

				// strip off outer command
				contentString = contentString.substring(prefix.length(),
						contentString.length() - 1);

				// check for second argument in LaTeX[str, false]
				int commaIndex = contentString.lastIndexOf(',');
				int bracketCount = 0;
				for (int i = commaIndex + 1; i < contentString.length(); i++) {
					if (contentString.charAt(i) == '[')
						bracketCount++;
					else if (contentString.charAt(i) == ']')
						bracketCount--;
				}
				if (bracketCount != 0 || commaIndex == -1) {
					// no second argument
					type = DynamicTextType.FORMULA_TEXT;
				}

			} else if (contentString.startsWith(prefix = app.getLocalization()
					.getCommand("Name") + "[")) {

				// strip off outer command
				contentString = contentString.substring(prefix.length(),
						contentString.length() - 1);
				type = DynamicTextType.DEFINITION;
			}
		}
		return new DynamicTextElement(contentString, type);
	}

	/**
	 * Converts a list of DynamicTextElements into a GeoText string.
	 * 
	 * @param latex
	 *            boolean
	 * @return GeoText string, e.g. "value is " + a
	 */
	public String buildGeoGebraString(ArrayList<DynamicTextElement> list,
			boolean latex) {

		if (list == null || list.size() == 0) {
			return "";
		}

		char currentQuote = Unicode.OPEN_DOUBLE_QUOTE;

		StringBuilder sb = new StringBuilder();
		String text;
		DynamicTextType mode;
		for (int i = 0; i < list.size(); i++) {
			text = list.get(i).text;
			mode = list.get(i).type;

			if (mode == DynamicTextType.STATIC) {
				for (int k = 0; k < text.length(); k++) {
					currentQuote = StringUtil.processQuotes(sb,
							text.substring(k, k + 1),
							currentQuote);
				}

			} else {
				if (mode == DynamicTextType.DEFINITION) {
					sb.append("\"+");
					sb.append("Name[");
					sb.append(text);
					sb.append(']');
					sb.append("+\"");
				} else if (latex || mode == DynamicTextType.FORMULA_TEXT) {
					sb.append("\"+");
					sb.append("LaTeX["); // internal name for FormulaText[ ]
					sb.append(text);
					sb.append(']');
					sb.append("+\"");
				} else if (mode == DynamicTextType.VALUE) {
					// brackets needed for eg "hello"+(a+3)
					sb.append("\"+(");
					sb.append(text);
					sb.append(")+\"");
				}
			}
		}

		// add quotes at start and end so it parses to a text
		sb.insert(0, '"');
		sb.append('"');

		return sb.toString();

	}
}
