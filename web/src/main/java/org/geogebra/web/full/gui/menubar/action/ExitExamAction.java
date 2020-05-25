package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.exam.ExamEnvironment;
import org.geogebra.common.main.exam.ExamLogBuilder;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.app.HTMLLogBuilder;
import org.geogebra.web.full.gui.exam.ExamDialog;
import org.geogebra.web.full.gui.exam.ExamExitConfirmDialog;
import org.geogebra.web.full.gui.exam.ExamUtil;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.awt.GGraphics2DW;

import com.google.gwt.canvas.client.Canvas;

/**
 * Exits exam mode.
 */
public class ExitExamAction extends DefaultMenuAction<Void> {
	/**
	 * Canvas line height
	 */
	protected static final int LINE_HEIGHT = 24;
	private static final double PADDING = 24;

	private AppWFull app;

	@Override
	public void execute(Void item, AppWFull app) {
		this.app = app;
		showExamExitDialog();
	}

	/**
	 * Show exit exam dialog
	 */
	protected void showExamExitDialog() {
		Localization loc = app.getLocalization();
		// set Firefox dom.allow_scripts_to_close_windows in about:config to
		// true to make this work
		String[] optionNames = {loc.getMenu("Cancel"), loc.getMenu("Exit")};

		if (app.getConfig().hasExam()) {
			new ExamExitConfirmDialog(app, new AsyncOperation<String>() {
				@Override
				public void callback(String obj) {
					if ("exit".equals(obj)) {
						exitAndResetExamOffline();
					}
				}
			}).show();
		} else {
			app.getGuiManager().getOptionPane().showOptionDialog(
					loc.getMenu("exam_exit_confirmation"), // ExitExamConfirm
					loc.getMenu("exam_exit_header"), // ExitExamConfirmTitle
					1, GOptionPane.WARNING_MESSAGE, null, optionNames,
					new AsyncOperation<String[]>() {
						@Override
						public void callback(String[] obj) {
							if ("1".equals(obj[0])) {
								exitAndResetExam();
							}
						}
					});
		}
	}

	/**
	 * Exit exam and restore normal mode
	 */
	protected void exitAndResetExam() {
		app.getLAF().toggleFullscreen(false);
		final Localization loc = app.getLocalization();
		ExamEnvironment exam = app.getExam();
		exam.exit();
		boolean examFile = app.getArticleElement().hasDataParamEnableGraphing();
		String buttonText;
		AsyncOperation<String[]> handler;
		AsyncOperation<String[]> welcomeHandler;
		if (examFile && !app.isUnbundledGraphing()) {
			handler = new AsyncOperation<String[]>() {
				@Override
				public void callback(String[] dialogResult) {
					app.setNewExam();
					ExamDialog.startExam(null, app);
				}
			};
			welcomeHandler = new AsyncOperation<String[]>() {

				@Override
				public void callback(String[] obj) {
					app.getLAF().toggleFullscreen(true);
					app.setNewExam();
					app.examWelcome();
				}
			};
			buttonText = loc.getMenu("Restart");
			exam.setHasGraph(true);
			boolean supportsCAS = app.getSettings().getCasSettings().isEnabled();
			boolean supports3D = app.getSettings().getEuclidian(-1).isEnabled();
			if (!supports3D && supportsCAS) {
				showFinalLog(loc.getMenu("ExamCAS"), buttonText, handler);
			} else if (!supports3D) {
				if (app.enableGraphing()) {
					showFinalLog(loc.getMenu("ExamGraphingCalc.long"), buttonText, handler);
				} else {
					showFinalLog(loc.getMenu("ExamSimpleCalc.long"), buttonText, handler);
				}
			} else {
				showFinalLog(loc.getMenu("exam_log_header") + " " + app.getVersionString(),
						buttonText, welcomeHandler);
			}
		} else {
			handler = new AsyncOperation<String[]>() {
				@Override
				public void callback(String[] dialogResult) {
					app.fileNew();
				}
			};
			buttonText = loc.getMenu("OK");
			showFinalLog(loc.getMenu("exam_log_header") + " " + app.getVersionString(),
					buttonText, handler);
		}
		app.endExam();
	}

	private void showFinalLog(String menu, String buttonText,
							  AsyncOperation<String[]> handler) {
		app.fileNew();
		HTMLLogBuilder htmlBuilder = new HTMLLogBuilder();
		app.getExam().getLog(app.getLocalization(), app.getSettings(), htmlBuilder);
		app.showMessage(htmlBuilder.getHTML(), menu, buttonText, handler);
		saveScreenshot(menu);
	}

	private void saveScreenshot(String menu) {
		final int header = 78;
		Canvas canvas = Canvas.createIfSupported();
		final GGraphics2DW g2 = new GGraphics2DW(canvas);

		g2.setCoordinateSpaceSize(500, app.getExam().getEventCount() * LINE_HEIGHT + 350);
		g2.setColor(GColor.WHITE);
		g2.fillRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		g2.setPaint(GColor.newColorRGB(app.getExam().isCheating() ? 0xD32F2F : 0x3DA196));
		g2.fillRect(0, 0, 500, header);
		g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 12));
		g2.setColor(GColor.WHITE);
		g2.drawString(menu, PADDING, PADDING);
		g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 16));
		g2.drawString(ExamUtil.status(app), PADDING, PADDING + LINE_HEIGHT);
		g2.setColor(GColor.BLACK);
		ExamLogBuilder canvasLogBuilder = new ExamLogBuilder() {
			private int yOffset = header + LINE_HEIGHT;

			@Override
			public void addLine(StringBuilder sb) {
				g2.setColor(GColor.BLACK);
				g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 16));
				g2.drawString(sb.toString(), PADDING, yOffset);
				yOffset += LINE_HEIGHT;
			}

			@Override
			public void addField(String name, String value) {
				g2.setColor(GColor.GRAY);
				g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 12));
				g2.drawString(name, PADDING, yOffset);
				yOffset += LINE_HEIGHT;
				// no empty line after "Activity"
				if (!StringUtil.empty(value)) {
					g2.setColor(GColor.BLACK);
					g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 16));
					g2.drawString(value, PADDING, yOffset);
					yOffset += LINE_HEIGHT;
				}
			}

		};

		app.getExam().getLog(app.getLocalization(), app.getSettings(), canvasLogBuilder);
		Browser.exportImage(canvas.toDataUrl(), "ExamLog.png");
	}

	/**
	 * Exit exam and restore normal mode
	 */
	protected void exitAndResetExamOffline() {
		app.getLAF().toggleFullscreen(false);
		saveScreenshot(app.getLocalization().getMenu((app.getConfig()
				.getAppName())));
		app.fileNew();
		app.endExam();
	}
}
