package org.geogebra.stepbystep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;

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
		final String path = "build" + File.separator + "reports";
		File dir = new File(path);
		dir.mkdirs();
		File f = new File(path + File.separator + filename);

		OutputStreamWriter isw = null;
		try {
			isw = new OutputStreamWriter(new FileOutputStream(f));
			isw.write(getHtml());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (isw != null) {
			try {
				isw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("file:///" + f.getAbsolutePath());

	}
}
