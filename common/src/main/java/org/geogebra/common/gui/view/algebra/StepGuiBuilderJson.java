package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.util.StringUtil;

import java.util.List;

/**
 * Makes a JSON object with a list of steps
 *
 */
public class StepGuiBuilderJson implements StepGuiBuilder {

	private StringBuilder sb = new StringBuilder();
	
	@Override
	public void addRow(List<String> s) {
		for (String ss : s) {
			if (sb.length() > 0) {
				sb.append(',');
			}

			sb.append("{ 'text':'");

			if (ss.startsWith("$")) {
				sb.append(StringUtil.toJavaString(ss.substring(1)));
				sb.append("', latex':true }");
			} else {
				sb.append(StringUtil.toJavaString(ss));
				sb.append("', 'plain':true }");
			}
		}
	}

	@Override
	public String toString() {
		return "[" + sb.toString() + "]";
	}

	@Override
	public void startGroup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endGroup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDefault() {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchToDetailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDetailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void linebreak() {
		// TODO Auto-generated method stub

	}

}
