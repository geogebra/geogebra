package org.geogebra.common.kernel.stepbystep;

import java.util.List;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.TextElement;
import org.geogebra.common.main.Localization;
import org.geogebra.test.util.ReportBuilder;

/**
 * Builds step-by step report for multiple testcases, separated by headings into
 * categories
 *
 */
public class HtmlStepBuilder implements StepGuiBuilder {

	private int groupCnt;
	private int alternativeCnt;

	private Localization loc;

	private StringBuilder sb;

	public HtmlStepBuilder(Localization loc) {
		this.loc = loc;

		sb = new StringBuilder()
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
	}

	@Override
	public void buildStepGui(SolutionStep step) {
		buildStepGui(step.cleanupSteps(), true, 0);
	}

	public void buildStepGui(SolutionStep step, boolean detailed, int indent) {
		List<SolutionStep> substeps = step.getSubsteps();

		if (step instanceof SolutionLine) {
			SolutionLine line = (SolutionLine) step;

			switch (line.getType()) {
				case WRAPPER:
					for (int i = 0; substeps != null && i < substeps.size(); i++) {
						buildStepGui(substeps.get(i), true, indent + 1);
						if (i != substeps.size() - 1) {
							linebreak();
						}
					}
					return;
				case GROUP_WRAPPER:
					for (SolutionStep substep : substeps) {
						buildStepGui(substep, true, indent);
					}
					return;
				case SUBSTEP_WRAPPER:
					startDefault();
					buildStepGui(substeps.get(1), false, indent);
					addDefaultButton();
					buildStepGui(substeps.get(substeps.size() - 1), false, indent);

					switchToDetailed();
					for (int i = 0; i < substeps.size(); i++) {
						buildStepGui(substeps.get(i), true, indent);
						if (i == 0) {
							addDetailedButton();
						}
					}

					endDetailed();
					return;
			}
		}

		if (detailed) {
			addRow(step.getDetailed(loc), indent);
		} else {
			addRow(step.getDefault(loc), indent);
		}

		addGroup(substeps, indent);
	}

	private void addGroup(List<SolutionStep> substeps, int indent) {
		if (substeps != null) {
			startGroup();
			for (int i = 0; i < substeps.size(); i++) {
				buildStepGui(substeps.get(i), true, indent + 1);
				if (i != substeps.size() - 1) {
					linebreak();
				}
			}
			endGroup();
		}
	}

	private void addRow(List<TextElement> equations, int indent) {
		sb.append("<br>");
		sb.append("<span style=\"margin-left:")
				.append(indent)
				.append("em\">");

		for (TextElement str : equations) {
			if (str.latex != null) {
				sb.append("<canvas class=\"latex\" data-content=\"")
						.append(str.latex)
						.append("\">")
						.append(str.latex)
						.append("</canvas>");
			} else {
				sb.append("<span>")
				.append(str.plain)
				.append("</span>");
			}
		}

		sb.append("</span>");
	}

	private void addDefaultButton() {
		sb.append("<canvas class='latex' onclick=\"switch_to_detailed('")
				.append(alternativeCnt)
				.append("');\" data-content='\\Xi'>\\Xi</canvas>\n");
	}

	private void addDetailedButton() {
		sb.append("<canvas class='latex' onclick=\"switch_to_default('")
				.append(alternativeCnt)
				.append("');\" data-content='\\Xi'>\\Xi</canvas>\n");
	}

	private void startGroup() {
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

	private void endGroup() {
		sb.append("</span>");
	}

	private void startDefault() {
		alternativeCnt++;

		sb.append("<span id = 'default")
				.append(alternativeCnt)
				.append("' >");
	}

	private void switchToDetailed() {
		sb.append("</span>");
		sb.append("<span id = 'detailed")
				.append(alternativeCnt)
				.append("' style='display: none'>");
	}

	private void endDetailed() {
		sb.append("</span>");
	}

	private void linebreak() {
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
