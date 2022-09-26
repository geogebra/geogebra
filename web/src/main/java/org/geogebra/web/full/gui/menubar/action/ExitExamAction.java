package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.exam.ExamEnvironment;
import org.geogebra.common.main.exam.ExamLogBuilder;
import org.geogebra.common.main.exam.restriction.ExamRegion;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.exam.ExamExitConfirmDialog;
import org.geogebra.web.full.gui.exam.ExamLogAndExitDialog;
import org.geogebra.web.full.gui.exam.ExamUtil;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.shared.GlobalHeader;
import org.geogebra.web.shared.components.dialog.DialogData;

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
	private static final GColor EXAM_OK_COLOR = GColor.newColorRGB(0x3DA196);

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
		DialogData data = new DialogData(null,
				"Cancel", "Exit");
		ExamExitConfirmDialog exit = new ExamExitConfirmDialog(app, data);
		Runnable returnHandler = () -> {
			if (app.getConfig().hasExam()) {
				exitAndResetExamOffline();
			} else { // classic
				exitAndResetExam();
			}
		};
		exit.setOnPositiveAction(() -> {
			GlobalHeader.INSTANCE.resetAfterExam();
			app.getExam().storeEndTime();
			new ExamLogAndExitDialog(app, false, returnHandler, null, "Exit").show();
		});
		exit.show();
	}

	/**
	 * Exit exam and restore normal mode
	 */
	protected void exitAndResetExam() {
		app.getLAF().toggleFullscreen(false);
		ExamEnvironment exam = app.getExam();
		StringBuilder settings = exam.getSettings(app.getLocalization(), app.getSettings());
		exam.exit();
		saveScreenshot(app.getLocalization().getMenu("exam_log_header")
				+ " " + app.getVersionString(), settings);
		app.endExam();
	}

	private void saveScreenshot(String title, StringBuilder settings) {
		final int header = 78;
		Canvas canvas = Canvas.createIfSupported();
		final GGraphics2DW g2 = new GGraphics2DW(canvas);

		g2.setCoordinateSpaceSize(500, app.getExam().getEventCount() * LINE_HEIGHT + 350);
		g2.setColor(GColor.WHITE);
		g2.fillRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		GColor color = EXAM_OK_COLOR;
		if (app.getExam().isCheating()) {
			color = GColor.DARK_RED;
		}
		g2.setPaint(color);
		g2.fillRect(0, 0, 500, header);
		g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 12));
		g2.setColor(GColor.WHITE);
		g2.drawString(title, PADDING, PADDING);
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
		if (settings != null) {
			canvasLogBuilder.addLine(settings);
		}
		app.getExam().getLog(app.getLocalization(), app.getSettings(), canvasLogBuilder);
		Browser.exportImage(canvas.toDataUrl(), "ExamLog.png");
	}

	/**
	 * Exit exam and restore normal mode
	 */
	protected void exitAndResetExamOffline() {
		app.getLAF().toggleFullscreen(false);
		ExamRegion examRegion = app.isExam() ? app.getExam().getExamRegion() : ExamRegion.GENERIC;
		String title = examRegion.getDisplayName(app.getLocalization(), app.getConfig());
		saveScreenshot(title, null);
		app.endExam();
		app.fileNew();
		app.clearSubAppCons();
		app.clearRestictions();
	}
}
