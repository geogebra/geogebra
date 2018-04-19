package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.util.StringUtil;

/**
 * Makes a JSON object with a list of steps
 *
 */
public class StepGuiBuilderJson implements StepGuiBuilder {

	private StringBuilder sb = new StringBuilder();
	
	@Override
	public void addPlainRow(String s) {

		if (sb.length() > 0) {
			sb.append(',');
		}

		sb.append("{ 'text':'");
		sb.append(StringUtil.toJavaString(s));
		sb.append("', 'plain':true }");
		
	}

	@Override
	public void addLatexRow(String s) {
		if (sb.length() > 0) {
			sb.append(',');
		}

		sb.append("{ 'text':'");
		sb.append(StringUtil.toJavaString(s));
		sb.append("', 'latex':true }");
		
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
