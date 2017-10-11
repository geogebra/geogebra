package org.geogebra.stepbystep;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.test.util.ReportBuilder;

/**
 * Builds step-by step report for multiple testcases, separated by headings into
 * categories
 *
 */
public class HtmlStepBuilder implements StepGuiBuilder {
	private StringBuilder sb = new StringBuilder()
			.append("<script src='https://beta.geogebra.org/scripts/jlatexmath/jlatexmath.js'></script>")
			.append("<script src='https://beta.geogebra.org/scripts/jlatexmath/jlatexmath-tube.js'></script>")
			.append("<style>canvas{display:block;margin:2px;}</style>")
			.append("<script>window.addEventListener(\"load\",function(){els = document.getElementsByTagName(\"CANVAS\");for(var k in els){els[k].getAttribute && GGBT_jlatexmath.drawLatexOnCanvas(els[k])};})</script>");
	private int indent;

	public void addPlainRow(String equations) {
		// TODO Auto-generated method stub

	}

	public void addLatexRow(String equations) {
		sb.append("<canvas class=\"latex\" style=\"height:0.5em;margin-left:"
				+ indent
				+ "em\" data-content=\"" + equations + "\">" + equations
				+ "</canvas>\n");

	}

	public void show() {
		// TODO Auto-generated method stub

	}

	public void startGroup() {
		indent++;

	}

	public void endGroup() {
		indent--;

	}

	/**
	 * @return HTML content
	 */
	public String getHtml() {
		return sb.toString();
	}

	/**
	 * @param methodName
	 *            caller method name
	 * @param i
	 *            heading level
	 */
	public void addHeading(String methodName, int i) {
		sb.append("<h" + i + ">" + methodName + "</h" + i + ">");

	}

	/**
	 * Stores report in build/reports
	 * 
	 * @param filename
	 *            filename
	 */
	public void printReport(String filename) {
		ReportBuilder report = new ReportBuilder(filename);
		report.callback(getHtml());
		report.close();
	}
}
