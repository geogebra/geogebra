package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamRegion;
import org.geogebra.common.exam.ExamSummary;
import org.geogebra.common.main.exam.event.CheatingEvent;
import org.geogebra.common.ownership.GlobalScope;
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
import org.gwtproject.canvas.client.Canvas;

/**
 * Exits exam mode.
 */
public class ExitExamAction extends DefaultMenuAction<AppWFull> {
	/**
	 * Canvas line height
	 */
	protected static final int LINE_HEIGHT = 24;
	private static final double PADDING = 24;
	private static final GColor EXAM_OK_COLOR = GColor.newColorRGB(0x3DA196);
	private static final int SCREENSHOT_HEADER_HEIGHT = 78;

	private AppWFull app;

	@Override
	public void execute(AppWFull app) {
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
			GlobalScope.examController.finishExam();
			GlobalHeader.INSTANCE.resetAfterExam();
			new ExamLogAndExitDialog(app, false, returnHandler, null, "Exit").show();
		});
		exit.show();
	}

	/**
	 * Exit exam and restore normal mode
	 */
	protected void exitAndResetExam() {
		app.getLAF().toggleFullscreen(false);
		GlobalScope.examController.exitExam();
		saveScreenshot(app.getLocalization().getMenu("exam_log_header")
				+ " " + app.getVersionString(), null);
		app.endExam();
	}

	private void saveScreenshot(String title, StringBuilder settings) {
		Canvas canvas = Canvas.createIfSupported();
		final GGraphics2DW g2 = new GGraphics2DW(canvas);
		ExamSummary examSummary = GlobalScope.examController.getExamSummary(
				app.getConfig(), app.getLocalization());
		int yOffset = LINE_HEIGHT + SCREENSHOT_HEADER_HEIGHT;

		addHeaderToScreenshot(g2, canvas, title);

		if (examSummary != null) {
			yOffset = addStartDateToScreenshot(g2, examSummary, yOffset);
			yOffset = addStartTimeToScreenshot(g2, examSummary, yOffset);
			yOffset = addEndTimeToScreenshot(g2, examSummary, yOffset);
			yOffset = addActivityToScreenshot(g2, yOffset);
			yOffset = addLogTimesToScreenshot(g2, examSummary, yOffset);
		}

		if (settings != null) {
			addLineToScreenshot(g2, settings.toString(), yOffset);
		}

		Browser.exportImage(canvas.toDataUrl(), "ExamLog.png");
	}

	private void addHeaderToScreenshot(GGraphics2DW g2, Canvas canvas, String title) {
		g2.setCoordinateSpaceSize(500,
				GlobalScope.examController.getCheatingEvents().size() * LINE_HEIGHT + 350);
		g2.setColor(GColor.WHITE);
		g2.fillRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		GColor color = GlobalScope.examController.isCheating() ? GColor.DARK_RED : EXAM_OK_COLOR;
		g2.setPaint(color);
		g2.fillRect(0, 0, 500, SCREENSHOT_HEADER_HEIGHT);
		g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 12));
		g2.setColor(GColor.WHITE);
		g2.drawString(title, PADDING, PADDING);
		g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 16));
		g2.drawString(ExamUtil.status(app), PADDING, PADDING + LINE_HEIGHT);
		g2.setColor(GColor.BLACK);
	}

	private int addStartDateToScreenshot(GGraphics2DW g2, ExamSummary examSummary, int yOffset) {
		return addFieldToScreenshot(g2, examSummary.getStartDateHintText(),
				examSummary.getStartDateLabelText(), yOffset);
	}

	private int addStartTimeToScreenshot(GGraphics2DW g2, ExamSummary examSummary, int yOffset) {
		return addFieldToScreenshot(g2, examSummary.getStartTimeHintText(),
				examSummary.getStartTimeLabelText(), yOffset);
	}

	private int addEndTimeToScreenshot(GGraphics2DW g2, ExamSummary examSummary, int yOffset) {
		return addFieldToScreenshot(g2, examSummary.getEndTimeHintText(),
				examSummary.getEndTimeLabelText(), yOffset);
	}

	private int addActivityToScreenshot(GGraphics2DW g2, int yOffset) {
		return addFieldToScreenshot(g2, app.getLocalization().getMenu("exam_activity"),
				null, yOffset);
	}

	private int addLogTimesToScreenshot(GGraphics2DW g2, ExamSummary examSummary, int yOffset) {
		int yOffsetForCheatingLog = addStartLogTimeToScreenshot(g2, yOffset);
		int yOffsetForEndLogTime = addCheatingEventsLogTimesToScreenshot(g2, examSummary,
				yOffsetForCheatingLog);
		return addEndLogTimeToScreenshot(g2, examSummary, yOffsetForEndLogTime);
	}

	private int addStartLogTimeToScreenshot(GGraphics2DW g2, int yOffset) {
		StringBuilder sb = new StringBuilder("0:00 ");
		sb.append(app.getLocalization().getMenu("exam_started"));
		return addLineToScreenshot(g2, sb.toString(), yOffset);
	}

	private int addCheatingEventsLogTimesToScreenshot(GGraphics2DW g2,
			ExamSummary examSummary, int yOffset) {
		StringBuilder sb = new StringBuilder();
		int yOffsetForNextEntry = yOffset;
		for (CheatingEvent event : GlobalScope.examController.getCheatingEvents().getEvents()) {
			sb.setLength(0);
			sb.append(examSummary.formatEventTime(event.getDate())).append(' ');
			sb.append(event.getAction().toString(app.getLocalization()));
			yOffsetForNextEntry = addLineToScreenshot(g2, sb.toString(), yOffsetForNextEntry);
		}
		return yOffsetForNextEntry;
	}

	private int addEndLogTimeToScreenshot(GGraphics2DW g2, ExamSummary examSummary, int yOffset) {
		StringBuilder sb = new StringBuilder();
		if (GlobalScope.examController.getFinishDate() == null) {
			sb.append("0:00");
		} else {
			sb.append(examSummary.formatEventTime(GlobalScope.examController.getFinishDate()));
		}
		sb.append(' ');
		sb.append(app.getLocalization().getMenu("exam_ended"));
		return addLineToScreenshot(g2, sb.toString(), yOffset);
	}

	private int addFieldToScreenshot(GGraphics2DW g2, String name, String value, int yOffset) {
		g2.setColor(GColor.GRAY);
		g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 12));
		g2.drawString(name, PADDING, yOffset);
		int yOffsetForNextEntry = yOffset + LINE_HEIGHT;
		if (!StringUtil.empty(value)) {
			return addLineToScreenshot(g2, value, yOffsetForNextEntry);
		}
		return yOffsetForNextEntry;
	}

	private int addLineToScreenshot(GGraphics2DW g2, String text, final int yOffset) {
		g2.setColor(GColor.BLACK);
		g2.setFont(new GFontW("SansSerif", GFont.PLAIN, 16));
		g2.drawString(text, PADDING, yOffset);
		return yOffset + LINE_HEIGHT;
	}

	/**
	 * Exit exam and restore normal mode
	 */
	protected void exitAndResetExamOffline() {
		app.getLAF().toggleFullscreen(false);
		ExamController examController = GlobalScope.examController;
		ExamRegion examRegion = !examController.isIdle() && examController.getExamType() != null
				? examController.getExamType() : ExamRegion.GENERIC;
		String title = "";
		if (examRegion != null) {
			title = examRegion.getDisplayName(app.getLocalization(), app.getConfig());
		}
		saveScreenshot(title, null);
		app.endExam();
		app.fileNew();
		app.clearSubAppCons();
	}
}
