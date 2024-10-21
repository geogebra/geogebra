package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamSummary;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.Settings;
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
	private final ExamController examController = GlobalScope.examController;

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
			examController.finishExam();
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
		saveScreenshot(app.getLocalization().getMenu("exam_log_header")
				+ " " + app.getVersionString());
		app.endExam();
	}

	private void saveScreenshot(String title) {
		Canvas canvas = Canvas.createIfSupported();
		final GGraphics2DW g2 = new GGraphics2DW(canvas);
		ExamSummary examSummary = examController.getExamSummary(
				app.getConfig(), app.getLocalization());
		int yOffset = LINE_HEIGHT + SCREENSHOT_HEADER_HEIGHT;

		addHeaderToScreenshot(g2, canvas, title);

		String deactivatedViews = getDeactivatedViewsText();
		if (!app.isUnbundled() && !deactivatedViews.isEmpty()) {
			yOffset = addLineToScreenshot(g2, deactivatedViews, yOffset);
		}
		if (examSummary != null) {
			yOffset = addStartDateToScreenshot(g2, examSummary, yOffset);
			yOffset = addStartTimeToScreenshot(g2, examSummary, yOffset);
			yOffset = addEndTimeToScreenshot(g2, examSummary, yOffset);
			yOffset = addActivityToScreenshot(g2, yOffset);
			addLogTimesToScreenshot(g2, examSummary, yOffset);
		}

		Browser.exportImage(canvas.toDataUrl(), "ExamLog.png");
	}

	private String getDeactivatedViewsText() {
		Settings settings = app.getSettings();
		Localization loc = app.getLocalization();
		boolean supportsCAS = settings.getCasSettings().isEnabled();
		boolean supports3D = settings.supports3D();

		StringBuilder sb = new StringBuilder();
		if (!supportsCAS || !supports3D) {
			sb.append(loc.getMenu("exam_views_deactivated"));
			sb.append(": ");
		}
		if (!supportsCAS) {
			sb.append(loc.getMenu("Perspective.CAS"));
		}
		if (!supportsCAS && !supports3D) {
			sb.append(", ");
		}
		if (!supports3D) {
			sb.append(loc.getMenu("Perspective.3DGraphics"));
		}
		return sb.toString();
	}

	private void addHeaderToScreenshot(GGraphics2DW g2, Canvas canvas, String title) {
		g2.setCoordinateSpaceSize(500,
				examController.getCheatingEvents().size() * LINE_HEIGHT + 350);
		g2.setColor(GColor.WHITE);
		g2.fillRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		GColor color = examController.isCheating() ? GColor.DARK_RED : EXAM_OK_COLOR;
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

	private void addLogTimesToScreenshot(GGraphics2DW g2, ExamSummary examSummary, int yOffset) {
		addCheatingEventsLogTimesToScreenshot(g2, examSummary,
				yOffset);
	}

	private void addCheatingEventsLogTimesToScreenshot(GGraphics2DW g2,
			ExamSummary examSummary, int yOffset) {
		int yOffsetForNextEntry = yOffset;
		for (String line : examSummary.getActivityLabelText().split("\n")) {
			yOffsetForNextEntry = addLineToScreenshot(g2, line, yOffsetForNextEntry);
		}
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
		ExamType examType = !examController.isIdle() ? examController.getExamType() : null;
		String title = "";
		if (examType != null) {
			title = examType.getDisplayName(app.getLocalization(), app.getConfig());
		}
		saveScreenshot(title);
		app.endExam();
		app.fileNew();
		app.clearSubAppCons();
	}
}
