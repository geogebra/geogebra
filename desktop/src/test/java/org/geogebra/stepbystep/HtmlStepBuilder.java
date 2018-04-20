package org.geogebra.stepbystep;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.test.util.ReportBuilder;

import java.util.List;

/**
 * Builds step-by step report for multiple testcases, separated by headings into
 * categories
 *
 */
public class HtmlStepBuilder implements StepGuiBuilder {
	private StringBuilder sb = new StringBuilder()
			.append("<!DOCTYPE html>")
			.append("<html>")
			.append("<head>")
			.append("<meta charset=\"UTF-8\">")
			.append("<script src='https://beta.geogebra.org/scripts/jlatexmath/jlatexmath.js'></script>")
			.append("<script src='https://beta.geogebra.org/scripts/jlatexmath/jlatexmath-tube.js'></script>")
			.append("<style>#line {margin:2px;}</style>")
			.append("<script type=\"text/javascript\"> "
					+ "function render_visible() { "
					+ "   els = document.getElementsByTagName(\"CANVAS\"); "
					+ "   for(var k in els) { "
					+ "      if (!els[k].getAttribute('rendered') && els[k].offsetParent !== null) { "
					+ "         GGBT_jlatexmath.drawLatexOnCanvas(els[k]); "
					+ "         els[k].setAttribute('rendered', true); "
					+ "      } "
					+ "   } "
					+ "} "
					+ "window.addEventListener(\"load\", render_visible)"
					+ "</script>")
			.append("<script type=\"text/javascript\"> "
					+ "function toggle_visibility(id) { "
					+ "   var e = document.getElementById('group' + id);"
					+ "   var button = document.getElementById('button' + id);"
					+ "   if(e.style.display == 'none') { "
					+ "      e.style.display = 'initial'; "
					+ "      button.setAttribute('data-content', '\\\\Delta'); "
					+ "      button.removeAttribute('rendered');"
					+ "      render_visible();"
					+ "   } else { "
					+ "      e.style.display = 'none'; "
					+ "      button.setAttribute('data-content', '\\\\nabla'); "
					+ "      button.removeAttribute('rendered');"
					+ "      render_visible();"
					+ "   } "
					+ "} "
					+ "</script>")
			.append("<script type=\"text/javascript\"> "
					+ "function switch_to_detailed(id) { "
					+ "   var def = document.getElementById('default' + id); "
					+ "   var detailed = document.getElementById('detailed' + id); "
					+ "   def.style.display = 'none'; "
					+ "   detailed.style.display = 'initial'; "
					+ "   render_visible();"
					+ "} "
					+ "</script>")	
			.append("<script type=\"text/javascript\"> "
					+ "function switch_to_default(id) { "
					+ "   var def = document.getElementById('default' + id);"
					+ "   var detailed = document.getElementById('detailed' + id);"
					+ "   def.style.display = 'initial'; "
					+ "   detailed.style.display = 'none'; "
					+ "   render_visible();"
					+ "} "
					+ "</script>")
			.append("</head>")
			.append("<body>");

	private int indent = 1;

	private int groupCnt;
	private int alternativeCnt;

	private boolean addDefaultButton;
	private boolean addDetailedButton;

	@Override
	public void addRow(List<String> equations) {
		sb.append("<br>");
		sb.append("<span style=\"margin-left:")
				.append(indent)
				.append("em\">");

		for (String str : equations) {
			if (str.startsWith("$") && !"$".equals(str)) {
				sb.append("<canvas class=\"latex\" data-content=\"")
						.append(str.substring(1))
						.append("\">")
						.append(str.substring(1))
						.append("</canvas>");
			} else {
				sb.append("<span>")
				.append(str)
				.append("</span>");
			}
		}

		if (addDefaultButton) {
			sb.append("<canvas class='latex' onclick=\"switch_to_detailed('")
					.append(alternativeCnt)
					.append("');\" data-content='\\Xi'>\\Xi</canvas>\n");
			addDefaultButton = false;
		}

		if (addDetailedButton) {
			sb.append("<canvas class='latex' onclick=\"switch_to_default('")
					.append(alternativeCnt)
					.append("');\" data-content='\\Xi'>\\Xi</canvas>\n");
			addDetailedButton = false;
		}

		sb.append("</span>");
	}

	@Override
	public void startGroup() {
		indent++;
		groupCnt++;
		
		sb.append("<canvas class='latex' id='button")
				.append(groupCnt)
				.append("' onclick=\"toggle_visibility('")
				.append(groupCnt)
				.append("');\" data-content='\\nabla'>\\nabla</canvas>\n");
		
		sb.append("<span style='display: none' id='group")
				.append(groupCnt)
				.append("'>");
	}


	@Override
	public void endGroup() {
		indent--;

		sb.append("</span>");
	}

	@Override
	public void startDefault() {
		alternativeCnt++;

		sb.append("<span id = 'default")
				.append(alternativeCnt)
				.append("' >");
		addDefaultButton = true;
	}

	@Override
	public void switchToDetailed() {
		sb.append("</span>");
		sb.append("<span id = 'detailed")
				.append(alternativeCnt)
				.append("' style='display: none'>");
		addDetailedButton = true;
	}

	@Override
	public void endDetailed() {
		sb.append("</span>");
	}

	@Override
	public void linebreak() {
		sb.append("<br>");
	}

	/**
	 * @return HTML content
	 */
	public String getHtml() {
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	/**
	 * @param methodName
	 *            caller method name
	 * @param i
	 *            heading level
	 */
	public void addHeading(String methodName, int i) {
		sb.append("<h")
				.append(i)
				.append(">")
				.append(methodName)
				.append("</h")
				.append(i)
				.append(">");
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
