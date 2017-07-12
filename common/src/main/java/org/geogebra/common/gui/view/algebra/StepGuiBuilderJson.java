package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.util.StringUtil;

/**
 * Makes a JSON object with a list of steps
 *
 */
public class StepGuiBuilderJson implements StepGuiBuilder {

	private StringBuilder sb = new StringBuilder();
	
	public void addPlainRow(String s) {

		if (sb.length() > 0) {
			sb.append(',');
		}

		sb.append("{ 'text':'");
		sb.append(StringUtil.toJavaString(s));
		sb.append("', 'plain':true }");
		
	}

	public void addLatexRow(String s) {
		if (sb.length() > 0) {
			sb.append(',');
		}

		sb.append("{ 'text':'");
		sb.append(StringUtil.toJavaString(s));
		sb.append("', 'latex':true }");
		
	}

	public void show() {
		// not needed
	}

	@Override
	public String toString() {
		return "[" + sb.toString() + "]";
	}

}
