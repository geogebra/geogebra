package org.geogebra.web.full.gui.menubar.action;

import com.google.gwt.canvas.client.Canvas;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.exam.ExamEnvironment;
import org.geogebra.common.main.exam.ExamLogBuilder;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.app.HTMLLogBuilder;
import org.geogebra.web.full.gui.exam.ExamDialog;
import org.geogebra.web.full.gui.exam.ExamExitConfirmDialog;
import org.geogebra.web.full.gui.exam.ExamUtil;
import org.geogebra.web.full.gui.layout.LayoutW;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;

/**
 * Exit exam action
 */
public class ExitExamAction extends MenuAction<Void> {
	/**
	 * Canvas line height
	 */
	protected static final int LINE_HEIGHT = 24;
	private static final double PADDING = 24;
	private AppW app;

	/**
	 * @param app application
	 */
	public ExitExamAction(AppW app) {
		super("exam_menu_exit", MaterialDesignResources.INSTANCE.signout_black());
		this.app = app;
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
		String buttonText = null;
		AsyncOperation<String[]> handler = null;
		AsyncOperation<String[]> welcomeHandler = null;
		if (examFile && !app.isUnbundledGraphing()) {
			handler = new AsyncOperation<String[]>() {
				@Override
				public void callback(String[] dialogResult) {
					getApp().setNewExam();
					ExamDialog.startExam(null, getApp());
				}
			};
			welcomeHandler = new AsyncOperation<String[]>() {

				@Override
				public void callback(String[] obj) {
					getApp().getLAF().toggleFullscreen(true);
					getApp().setNewExam();
					getApp().examWelcome();
				}
			};
			buttonText = loc.getMenu("Restart");
			exam.setHasGraph(true);
			boolean supportsCAS = app.getSettings().getCasSettings().isEnabled();
			boolean supports3D = app.getSettings().getEuclidian(-1).isEnabled();
			if (!supports3D && supportsCAS) {
				showFinalLog(loc.getMenu("ExamCAS"), buttonText, handler);
			} else if (!supports3D && !supportsCAS) {
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
					getApp().fileNew();
				}
			};
			buttonText = loc.getMenu("OK");
			showFinalLog(loc.getMenu("exam_log_header") + " " + app.getVersionString(),
					buttonText, handler);
		}
		resetAfterExam();
	}

	private void showFinalLog(String menu, String buttonText, AsyncOperation<String[]> handler) {
		getApp().fileNew();
		HTMLLogBuilder htmlBuilder = new HTMLLogBuilder();
		getApp().getExam().getLog(app.getLocalization(), getApp().getSettings(), htmlBuilder);
		getApp().showMessage(htmlBuilder.getHTML(), menu, buttonText, handler);
		saveScreenshot(menu);
	}

	/**
	 * @return application
	 */
	AppW getApp() {
		return app;
	}

	private void resetAfterExam() {
		getApp().setExam(null);
		getApp().resetViewsEnabled();
		LayoutW.resetPerspectives(getApp());
		getApp().getLAF().addWindowClosingHandler(getApp());
		getApp().fireViewsChangedEvent();
		getApp().getGuiManager().updateToolbarActions();
		getApp().getGuiManager()
				.setGeneralToolBarDefinition(ToolBar.getAllToolsNoMacros(true, false, getApp()));
		getApp().getGuiManager().resetMenu();
		getApp().setActivePerspective(0);
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
		getApp().getLAF().toggleFullscreen(false);
		saveScreenshot(getApp().getLocalization().getMenu((getApp().getConfig()
				.getAppName())));
		getApp().fileNew();
		resetAfterExam();
	}

	/**
	 * Show exit exam dialog
	 */
	protected void showExamExitDialog() {
		Localization loc = app.getLocalization();
		// set Firefox dom.allow_scripts_to_close_windows in about:config to
		// true to make this work
		String[] optionNames = {loc.getMenu("Cancel"), loc.getMenu("Exit")};

		if (getApp().getConfig().hasExam()) {
			new ExamExitConfirmDialog(getApp(), new AsyncOperation<String>() {
				@Override
				public void callback(String obj) {
					if ("exit".equals(obj)) {
						exitAndResetExamOffline();
					}
				}
			}).show();
		} else {
			getApp().getGuiManager().getOptionPane().showOptionDialog(
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

	@Override
	public void execute(Void geo, AppWFull appx) {
		showExamExitDialog();
	}
}
