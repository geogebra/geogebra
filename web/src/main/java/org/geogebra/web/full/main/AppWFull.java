package org.geogebra.web.full.main;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.EVALUATOR_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GEOMETRY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.NOTES_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SCIENTIFIC_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SUITE_APPCODE;
import static org.geogebra.common.exam.ExamType.CHOOSE;
import static org.geogebra.common.gui.Layout.findDockPanelData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MaskWidgetList;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian.inline.InlineFormulaController;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.euclidian.smallscreen.AdjustScreen;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamOptions;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatCollada;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatColladaHTML;
import org.geogebra.common.gui.inputfield.HasLastItem;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.gui.view.algebra.scicalc.LabelHiderCallback;
import org.geogebra.common.gui.view.spreadsheet.CopyPasteCut;
import org.geogebra.common.gui.view.spreadsheet.DataImport;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.main.SaveController;
import org.geogebra.common.main.ShareController;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.common.main.settings.updater.SettingsUpdaterBuilder;
import org.geogebra.common.main.undo.UndoHistory;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.cas.giac.CASFactoryW;
import org.geogebra.web.full.euclidian.inline.InlineFormulaControllerW;
import org.geogebra.web.full.euclidian.inline.InlineTableControllerW;
import org.geogebra.web.full.euclidian.inline.InlineTextControllerW;
import org.geogebra.web.full.gui.CustomizeToolbarGUI;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.SaveControllerW;
import org.geogebra.web.full.gui.ShareControllerW;
import org.geogebra.web.full.gui.WhatsNewDialog;
import org.geogebra.web.full.gui.app.GGWCommandLine;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.dialog.RelationPaneW;
import org.geogebra.web.full.gui.exam.ExamControllerDelegateW;
import org.geogebra.web.full.gui.exam.ExamEventBus;
import org.geogebra.web.full.gui.exam.ExamUtil;
import org.geogebra.web.full.gui.exam.classic.ExamClassicStartDialog;
import org.geogebra.web.full.gui.keyboard.KeyboardManager;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.layout.DockGlassPaneW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.full.gui.layout.LayoutW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.menu.MenuViewController;
import org.geogebra.web.full.gui.menu.MenuViewListener;
import org.geogebra.web.full.gui.menubar.FileMenuW;
import org.geogebra.web.full.gui.menubar.PerspectivesPopup;
import org.geogebra.web.full.gui.menubar.action.StartExamAction;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.toolbarpanel.tableview.dataimport.CsvImportHandler;
import org.geogebra.web.full.gui.util.FontSettingsUpdaterW;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.SuiteHeaderAppPicker;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.ConstructionItemProvider;
import org.geogebra.web.full.main.activity.CASActivity;
import org.geogebra.web.full.main.activity.ClassicActivity;
import org.geogebra.web.full.main.activity.EvaluatorActivity;
import org.geogebra.web.full.main.activity.GeoGebraActivity;
import org.geogebra.web.full.main.activity.GeometryActivity;
import org.geogebra.web.full.main.activity.Graphing3DActivity;
import org.geogebra.web.full.main.activity.GraphingActivity;
import org.geogebra.web.full.main.activity.MebisNotesActivity;
import org.geogebra.web.full.main.activity.MixedRealityActivity;
import org.geogebra.web.full.main.activity.NotesActivity;
import org.geogebra.web.full.main.activity.ScientificActivity;
import org.geogebra.web.full.main.activity.SuiteActivity;
import org.geogebra.web.full.main.mask.MaskWidgetListW;
import org.geogebra.web.full.main.video.VideoManagerW;
import org.geogebra.web.full.move.googledrive.operations.GoogleDriveOperationW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.HasHide;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.tooltip.ComponentSnackbar;
import org.geogebra.web.html5.gui.tooltip.ToolTip;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.javax.swing.GImageIconW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.main.ScriptManagerW;
import org.geogebra.web.html5.move.googledrive.GoogleDriveOperation;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.Persistable;
import org.geogebra.web.shared.GlobalHeader;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.ggtapi.LoginOperationW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Overflow;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.HorizontalPanel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.web.MathFieldW;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.File;
import elemental2.dom.MessageEvent;
import elemental2.dom.URL;
import elemental2.webstorage.StorageEvent;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * App with all the GUI
 *
 */
public class AppWFull extends AppW implements HasKeyboard, MenuViewListener {

	private static final String RECENT_CHANGES_KEY = "RecentChangesInfo.Graphing";
	private static final boolean ALLOW_RECENT_CHANGES_DIALOG = false;
	private final static int AUTO_SAVE_PERIOD = 2000;
	// NB this needs to be adjusted in app-release if we change it here
	private static final int MIN_SIZE_FOR_PICKER = 650;

	private GuiManagerW guiManager = null;

	private CustomizeToolbarGUI ct;

	private ArrayList<Runnable> waitingForLocalization;
	private boolean localizationLoaded;
	/** browser / tablet / win store device */
	protected final GDevice device;
	/** material ID waiting for login */
	String toOpen = "";
	private PerspectivesPopup perspectivesPopup;

	private Perspective activePerspective;

	private boolean menuShowing = false;
	private final GeoGebraFrameFull frame;
	private DockSplitPaneW oldSplitLayoutPanel = null; // just a
																// technical
	private int spWidth;
	private int spHeight;
	private boolean isMenuInited = false;
	// helper
	// variable
	private HorizontalPanel splitPanelWrapper = null;
	private @CheckForNull MenuViewController menuViewController;

	private EmbedManagerW embedManager;
	private VideoManagerW videoManager;

	private SaveController saveController = null;

	private ShareControllerW shareController;
	private FileManager fm;
	private GoogleDriveOperation googleDriveOperation;
	private GeoGebraActivity activity;
	private KeyboardManager keyboardManager;
	/** dialog manager */
	protected DialogManagerW dialogManager = null;
	private String autosavedMaterial = null;
	private MaskWidgetList maskWidgets;
	private SuiteHeaderAppPicker suiteAppPickerButton;
	private final Map<String, Material> constructionJson = new HashMap<>();
	private final HashMap<String, UndoHistory> undoHistory = new HashMap<>();
	private InputBoxType inputBoxType;
	private List<String> functionVars = new ArrayList<>();
	private OpenSearch search;
	private CsvImportHandler csvImportHandler;
	private final ExamController examController = GlobalScope.examController;
	private AutocompleteProvider autocompleteProvider;
	private ExamEventBus examEventBus;

	/**
	 * @param geoGebraElement GeoGebra element
	 * @param dimension 2 or 3 (for 2D or 3D app)
	 * @param laf look and feel
	 * @param device browser / tablet / win store device
	 * @param frame frame
	 */
	public AppWFull(GeoGebraElement geoGebraElement, AppletParameters parameters,
			int dimension, GLookAndFeelI laf,
			GDevice device, GeoGebraFrameFull frame) {
		super(geoGebraElement, parameters, dimension, laf);
		this.frame = frame;
		this.device = device;

		if (getAppletParameters().getDataParamApp()) {
			startDialogChain();
		}

		setAppletHeight(frame.getComputedHeight());
		setAppletWidth(frame.getComputedWidth());

		this.useFullGui = !isApplet() || parameters.getDataParamShowAlgebraInput(false)
				|| parameters.getDataParamShowToolBar(false)
				|| parameters.getDataParamShowMenuBar(false)
				|| parameters.getDataParamEnableRightClick() || !isStartedWithFile();

		Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
				+ GeoGebraConstants.BUILD_DATE);
		initCommonObjects();
		initing = true;

		this.euclidianViewPanel = new EuclidianDockPanelW(this,
				allowStylebar());
		initCoreObjects();
		checkExamPerspective();
		afterCoreObjectsInited();
		getFontSettingsUpdater().resetFonts();
		Browser.removeDefaultContextMenu(geoGebraElement.getElement());
		if (getAppletParameters().getDataParamApp() && !this.getLAF().isSmart()) {
			RootPanel.getBodyElement().addClassName("application");
			setupHeader();
		}

		startActivity();

		setPurpose();
	}

	private void setupHeader() {
		GlobalHeader header = GlobalHeader.INSTANCE;
		header.setApp(this);
		if (showMenuBar()) {
			setupSignInButton(header);
		}
		if (isSuite()) {
			suiteAppPickerButton = SuiteHeaderAppPicker.addSuiteAppPicker(this);
		}
	}

	private void checkExamPerspective() {
		if (isLockedExam()) {
			ExamType examMode = getForcedExamType();
			if (examMode != null) {
				appletParameters.setAttribute("perspective", "");
				afterLocalizationLoaded(this::showExamWelcomeMessage);
			} else {
				String appCode = appletParameters.getDataParamAppName();
				String supportedModes = hasExamModes() ? getSupportedExamModes(appCode) : appCode;
				showErrorDialog("Invalid exam mode: "
						+ appletParameters.getParamExamMode()
						+ "\n Supported exam modes: " + supportedModes);
				appletParameters.setAttribute("examMode", "");
			}
		} else {
			ExamType examType = ExamType.byName(appletParameters.getParamExamMode());
			if (examType != null) {
				startExam(examType, null);
			}
		}
	}

	/**
	 * @param appCode app code for API (suite/graphing/classic/...)
	 * @return list of supported mode IDs
	 */
	private String getSupportedExamModes(String appCode) {
		List<ExamType> examTypes = ExamType.getAvailableValues(getLocalization(), getConfig());
		return Stream.concat(Stream.of(appCode, CHOOSE), examTypes.stream()
						.filter(r -> r != ExamType.GENERIC)
						.map(r -> r.name().toLowerCase(Locale.ROOT)))
				.collect(Collectors.joining(", "));
	}

	/**
	 * @return exam region forced by examMode and appName parameters
	 */
	public ExamType getForcedExamType() {
		String paramExamMode = appletParameters.getParamExamMode();
		if (paramExamMode.equals(appletParameters.getDataParamAppName())
			|| paramExamMode.equals(CHOOSE)) {
			return ExamType.GENERIC;
		}
		if (hasExamModes()) {
			return ExamType.byName(paramExamMode);
		}
		return null;
	}

	private boolean hasExamModes() {
		return isSuite() || isWhiteboardActive();
	}

	private void setupSignInButton(GlobalHeader header) {
		ensureLoginOperation();
		header.addSignIn(this);
	}

	@Override
	public AppConfig getConfig() {
		initActivity();
		if (activity == null) {
			return super.getConfig();
		}
		return activity.getConfig();
	}

	/**
	 * @return current activity (graphing, geometry, 3D, ...)
	 */
	public GeoGebraActivity getActivity() {
		return activity;
	}

	public GeoGebraActivity getCurrentActivity() {
		return activity == null ? null : activity.getSubapp();
	}

	private void initActivity() {
		if (appletParameters == null || activity != null) {
			return;
		}
		switch (appletParameters.getDataParamAppName()) {
		case GRAPHING_APPCODE:
			activity = new GraphingActivity();
			break;
		case GEOMETRY_APPCODE:
			activity = new GeometryActivity();
			break;
		case G3D_APPCODE:
			activity = new Graphing3DActivity();
			break;
		case "mr":
			activity = new MixedRealityActivity();
			break;
		case CAS_APPCODE:
			activity = new CASActivity();
			break;
		case SCIENTIFIC_APPCODE:
			activity = new ScientificActivity();
			break;
		case NOTES_APPCODE:
			activity = isMebis() ? new MebisNotesActivity() : new NotesActivity();
			break;
		case EVALUATOR_APPCODE:
			activity = new EvaluatorActivity();
			break;
		case SUITE_APPCODE:
			String disableCAS = NavigatorUtil.getUrlParameter("disableCAS");
			activity = new SuiteActivity(getLastUsedSubApp(), "".equals(disableCAS)
					|| "true".equals(disableCAS));
			break;
		default:
			activity = new ClassicActivity(new AppConfigDefault());
		}
	}

	/**
	 * @return Last used SubApp, only if it is saved in local storage and the app was not started
	 * with a file, Graphing SubApp otherwise <br/>
	 * If the app was started with a file, the activity should be updated from
	 * {@link #updateAppCodeSuite(String, Perspective)} anyways
	 */
	public String getLastUsedSubApp() {
		if (isLockedExam() || isStartedWithFile()) {
			return GRAPHING_APPCODE;
		}
		String lastUsedSubApp = BrowserStorage.LOCAL.getItem(BrowserStorage.LAST_USED_SUB_APP);
		return lastUsedSubApp != null && !lastUsedSubApp.isEmpty()
				? lastUsedSubApp : GRAPHING_APPCODE;
	}

	/**
	 * Initialize the activity
	 */
	private void startActivity() {
		initActivity();
		preloadAdvancedCommandsForSuiteCAS();
		activity.start(this);
	}

	/**
	 * Preloads the advanced commands for the CAS sub-app in suite
	 */
	private void preloadAdvancedCommandsForSuiteCAS() {
		if (isSuite() && "cas".equals(activity.getConfig().getSubAppCode())) {
			getAsyncManager().prefetch(null, "advanced", "giac", "cas");
		}
	}

	/**
	 * Makes a difference between the original app and the app that is opened for macro editing.
	 */
	private void setPurpose() {
		URL url = new URL(DomGlobal.location.href);
		if (url.searchParams != null) {
			setOpenedForMacroEditing(false);
			String editMacroName = url.searchParams.get(EDIT_MACRO_URL_PARAM_NAME);
			if (editMacroName != null) {
				if (storageContainsMacro(editMacroName)) {
					setOpenedForMacroEditing(true);
				} else {
					url.searchParams.delete(EDIT_MACRO_URL_PARAM_NAME);
					updateURL(url);
				}
			}
			if (isOpenedForMacroEditing()) {
				getKernel().removeAllMacros();
				restoreMacro(editMacroName);
				registerOpenFileListener(() -> openEditMacroFromStorage(editMacroName));
				// Close the tab if the macro is removed from local storage
				getGlobalHandlers().addEventListener(DomGlobal.window, "storage", event -> {
					StorageEvent storageEvent = (StorageEvent) event;
					if (storageEvent.newValue == null
							&& createStorageMacroKey(getEditMacro().getEditName())
							.equals(storageEvent.key)) {
						DomGlobal.window.close();
					}
				});
				// Before the tab is closed, remove the macro from local storage
				// in order to let the original app open the macro editing again.
				getGlobalHandlers().addEventListener(DomGlobal.window, "beforeunload", event ->
					removeMacroFromStorage(getEditMacro().getEditName())
				);
			} else {
				removeAllMacrosFromStorage();
				// Close all the editing tabs when the original app is closed.
				getGlobalHandlers().addEventListener(DomGlobal.window, "beforeunload", event ->
						removeAllMacrosFromStorage());
				// After the macro is edited and the save button is pressed, the editing tab
				// sends a message to the original app containing the XML of the edited macro.
				getGlobalHandlers().addEventListener(DomGlobal.window, "message", event -> {
					MessageEvent<?> message = Js.uncheckedCast(event);
					String editedMacroMessage = message.data.toString();
					try {
						JsPropertyMap<Object> messageProperties =
								Js.asPropertyMap(Global.JSON.parse(editedMacroMessage));
						Object macroName = messageProperties
								.get(EDITED_MACRO_NAME_KEY);
						if (macroName != null) {
							getKernel().removeMacro(macroName.toString());
							if (addMacroXML(
									messageProperties.get(EDITED_MACRO_XML_KEY).toString())) {
								setXML(getXML(), true);
							}
						}
					} catch (Throwable err) {
						Log.debug("Error occurred while updating the macro XML: " + err.getMessage()
								+ "\nEdited macro message: " + editedMacroMessage);
					}
				});
			}
		}
	}

	/**
	 * shows the on-screen keyboard (or e.g. a show-keyboard-button)
	 * @param textField keyboard listener
	 */
	public final void showKeyboard(MathKeyboardListener textField) {
		showKeyboard(textField, false);
	}

	@Override
	public final boolean showKeyboard(MathKeyboardListener textField,
			boolean forceShow) {
		boolean ret = getAppletFrame().showKeyboard(true, textField, forceShow);
		if (textField != null && ret) {
			CancelEventTimer.keyboardSetVisible();
		}
		return ret;
	}

	@Override
	public final void updateKeyboardHeight() {
		getAppletFrame().updateKeyboardHeight();
	}

	@Override
	public final void updateKeyboardField(MathKeyboardListener field) {
		getKeyboardManager().setOnScreenKeyboardTextField(field);
	}

	@Override
	public final GuiManagerW getGuiManager() {
		return guiManager;
	}

	@Override
	public final void initGuiManager() {
		// this should not be called from AppWsimple!
		setWaitCursor();
		guiManager = newGuiManager();
		getLocalization().registerLocalizedUI(guiManager);
		getGuiManager().setLayout(new LayoutW(this));
		getGuiManager().initialize();
		setDefaultCursor();
		initMenu();
	}

	private void initMenu() {
		if (isFloatingMenu()) {
			ensureLoginOperation();
			if (getVendorSettings().canSessionExpire()) {
				AuthenticationModel model = getLoginOperation().getModel();
				model.setSessionExpireTimer(newTimer(getDialogManager().getSessionExpireDialog(),
						AuthenticationModel.SESSION_TIME));
			}
			MenuViewController menuController = new MenuViewController(this);
			menuController.setMenuViewListener(this);
			menuViewController = menuController;
			isMenuInited = true;
		}
	}

	/**
	 * @return a GuiManager for GeoGebraWeb
	 */
	protected GuiManagerW newGuiManager() {
		return new GuiManagerW(this, getDevice());
	}

	@Override
	public final void hideKeyboard() {
		if (getGuiManager().getKeyboardListener() != null) {
			getGuiManager().getKeyboardListener().setFocus(false);
		}
		getAppletFrame().closeKeyboard();
	}

	@Override
	public final boolean letShowPropertiesDialog() {
		return rightClickEnabled
				|| getAppletParameters().getDataParamShowMenuBar(false)
				|| getAppletParameters().getDataParamApp();
	}

	@Override
	public final void updateKeyboard() {
		invokeLater(() -> {
			DockPanelW dp = getGuiManager().getLayout().getDockManager()
					.getPanelForKeyboard();
			MathKeyboardListener listener = getGuiManager()
					.getKeyboardListener(dp);
			if (listener != null) {
				// dp.getKeyboardListener().setFocus(true);
				listener.ensureEditing();
				listener.setFocus(true);
				if (getAppletFrame().appNeedsKeyboard()
						&& examController.getState() != ExamState.PREPARING) {
					getAppletFrame().showKeyboard(true, listener, true);
				}
			}
			if (!getAppletFrame().appNeedsKeyboard()) {
				getAppletFrame().showKeyboard(false, null, true);
			}

		});

	}

	@Override
	public void doSetLanguage(String lang, boolean asyncCall) {
		super.doSetLanguage(lang, asyncCall);
		if (asyncCall) {
			getKeyboardManager().updateKeyboardLanguage();
		}
	}

	@Override
	public final void notifyLocalizationLoaded() {
		localizationLoaded = true;
		if (waitingForLocalization == null) {
			return;
		}

		for (Runnable run : waitingForLocalization) {
			run.run();
		}

		waitingForLocalization.clear();
	}

	@Override
	public final void afterLocalizationLoaded(Runnable run) {
		if (localizationLoaded) {
			run.run();
		} else {
			if (waitingForLocalization == null) {
				waitingForLocalization = new ArrayList<>();
			}
			waitingForLocalization.add(run);
		}
	}

	@Override
	public final void showStartTooltip(Perspective perspective) {
		afterLocalizationLoaded(() -> doShowStartTooltip(perspective));
	}

	/**
	 * @param perspective perspective
	 */
	void doShowStartTooltip(Perspective perspective) {
		if (appletParameters.getDataParamShowStartTooltip(perspective != null)) {
			getToolTipManager().setBlockToolTip(false);
			String appName = perspective != null ? perspective.getId() : getConfig().getAppTitle();
			String helpText = getLocalization().getPlain("CheckOutTutorial",
					getLocalization().getMenu(appName));
			String tooltipURL = getLocalization().getTutorialURL(getConfig());
			ToolTipManagerW toolTipManagerW = getToolTipManager();
			String title = getLocalization().getMenu("NewToGeoGebra");
			toolTipManagerW.showBottomInfoToolTip(new ToolTip(title, helpText, "Help",
							tooltipURL), this,
					ComponentSnackbar.DEFAULT_TOOLTIP_DURATION);
			getToolTipManager().setBlockToolTip(true);
		}
	}

	@Override
	public final void checkSaved(AsyncOperation<Boolean> runnable) {
		getSaveController().showDialogIfNeeded(runnable, true);
	}

	@Override
	public final void openCSV(String csv) {
		String[][] data = DataImport.parseExternalData(this, csv, true);
		CopyPasteCut cpc = getGuiManager().getSpreadsheetView()
				.getSpreadsheetTable().getCopyPasteCut();
		cpc.pasteExternal(data, 0, 0, data.length > 0 ? data[0].length - 1 : 0,
				data.length);
		onOpenFile();
	}

	@Override
	public void resetUI() {
		resetEVs();
		// remove all Macros before loading preferences
		kernel.removeAllMacros();
		// reload the saved/(default) preferences
		Perspective p = null;
		if (isUnbundledOrWhiteboard() && getGuiManager() != null) {
			getGuiManager().getLayout().resetPerspectives(this);
		}

		if (getGuiManager() != null) {
			p = getGuiManager().getLayout().createPerspective();
		}

		if (isUnbundledOrWhiteboard()) {
			p = PerspectiveDecoder.getDefaultPerspective(getConfig().getForcedPerspective(),
					getGuiManager().getLayout());
		}

		if (isUnbundled()) {
			if (isPortrait()) {
				p.getSplitPaneData()[0].setDivider(PerspectiveDecoder.portraitRatio(
						getHeight(),
						isUnbundledGraphing() || isUnbundled3D()));
			} else {
				p.getSplitPaneData()[0].setDivider(
						PerspectiveDecoder.landscapeRatio(this, getWidth()));
			}
		}

		GeoGebraPreferencesW.loadForApp(this, p);

		resetAllToolbars();

		resetToolbarPanel();

		getGuiManager().updateGlobalOptions();

		if (isUnbundled() && getGuiManager()
				.getUnbundledToolbar() != null) {
			getGuiManager().getUnbundledToolbar()
					.updateContent();
		}
	}

	private void resetAllToolbars() {
		GuiManagerW gm = getGuiManager();
		DockPanelW[] panels = gm.getLayout().getDockManager().getPanels();
		for (DockPanelW panel : panels) {
			if (panel.canCustomizeToolbar()) {
				panel.setToolbarString(panel.getDefaultToolbarString());
			}
		}
		gm.setToolBarDefinition(gm.getDefaultToolbarString());

	}

	/**
	 * Resets toolbar
	 */
	protected final void resetToolbarPanel() {
		GuiManagerW gm = getGuiManager();
		DockPanel avPanel = gm.getLayout().getDockManager()
				.getPanel(VIEW_ALGEBRA);
		if (avPanel instanceof ToolbarDockPanelW) {
			final ToolbarDockPanelW dockPanel = (ToolbarDockPanelW) avPanel;
			if (dockPanel.getToolbar() != null) {
				dockPanel.getToolbar().reset();
			}
			dockPanel.tryBuildZoomPanel();
		}
		if (getCurrentActivity() != null) {
			getCurrentActivity().initTableOfValues(this);
		}
	}

	/**
	 * Updates the stylebar in Algebra View
	 */
	public final void updateAVStylebar() {
		if (getGuiManager() != null && getGuiManager().hasAlgebraView()) {
			AlgebraStyleBarW styleBar = ((AlgebraViewW) getView(
					App.VIEW_ALGEBRA)).getStyleBar(false);
			if (styleBar != null) {
				styleBar.update(null);
			}
		}
	}

	@Override
	public final void showExamWelcomeMessage() {
		if (examController.isIdle()) {
			if (isUnbundled()) {
				new StartExamAction().execute(this);
			} else {
				resetViewsEnabled();
				String negativeKey = isLockedExam()
						? null : "Cancel";
				DialogData data = new DialogData("exam_custom_header",
						negativeKey, "exam_start_button");
				new ExamClassicStartDialog(this, data).show();
			}
		}
	}

	@Override
	public ErrorHandler getDefaultErrorHandler() {
		return new ErrorHandlerW(this);
	}

	@Override
	public RelationPaneW getRelationDialog(String subTitle) {
		DialogData data = new DialogData(getLocalization().getCommand("Relation"), subTitle,
				null, "OK");
		return new RelationPaneW(this, data);
	}

	@Override
	public GeoGebraFrameFull getAppletFrame() {
		return frame;
	}

	@Override
	public final ToolBarInterface getToolbar() {
		return getAppletFrame().getToolbar();
	}

	private CustomizeToolbarGUI getCustomizeToolbarGUI() {
		if (this.ct == null) {
			this.ct = new CustomizeToolbarGUI(this);
		}
		int toolbarId = getGuiManager().getActiveToolbarId();
		Log.debug("[CT] toolbarId: " + toolbarId);
		ct.setToolbarId(toolbarId);
		return this.ct;
	}

	@Override
	public final void set1rstMode() {
		GGWToolBar.set1rstMode(this);
	}

	@Override
	public final void setLabels() {
		super.setLabels();
		if (this.ct != null) {
			ct.setLabels();
		}
		if (menuViewController != null) {
			menuViewController.setLabels();
		}
	}

	@Override
	public final boolean isSelectionRectangleAllowed() {
		return this.showToolBar;
	}

	@Override
	public final void toggleShowConstructionProtocolNavigation(int id) {
		super.toggleShowConstructionProtocolNavigation(id);
		if (getGuiManager() != null) {
			getGuiManager().updateMenubar();
		}
	}

	@Override
	public final FileManager getFileManager() {
		if (this.fm == null) {
			this.fm = getDevice().createFileManager(this);
		}
		return this.fm;
	}

	@Override
	public DialogManagerW getDialogManager() {
		if (dialogManager == null) {
			dialogManager = new DialogManagerW(this);
		}
		return dialogManager;
	}

	private void showBrowser(MyHeaderPanel headerPanel) {
		EuclidianController evController = getActiveEuclidianView().getEuclidianController();
		if (evController != null) {
			evController.hideDynamicStylebar();
		}
		getAppletFrame().setApplication(this);
		getAppletFrame().showPanel(headerPanel);
	}

	@Override
	public final void openSearch(String query) {
		if (search == null) {
			search = new OpenSearch(this);
		}
		search.show(query);
	}

	/**
	 * Open temporary saved files view in exam mode.
	 */
	public final void openSearchInExamMode() {
		if (search == null) {
			search = new OpenSearch(this);
		}
		search.openInExamMode();
	}

	@Override
	public GoogleDriveOperation getGoogleDriveOperation() {
		return googleDriveOperation;
	}

	@Override
	protected final void initGoogleDriveEventFlow() {
		googleDriveOperation = new GoogleDriveOperationW(this);
		String state = NavigatorUtil.getUrlParameter("state");
		if (getNetworkOperation().isOnline() && state != null
				&& !"".equals(state)) {
			googleDriveOperation.initGoogleDriveApi();
		}
	}

	@Override
	public final Element getFrameElement() {
		return getAppletFrame().getElement();
	}

	@Override
	public final void openMaterial(final String id,
			final AsyncOperation<String> onError) {
		if (getLoginOperation() != null
				&& getLoginOperation().getGeoGebraTubeAPI()
				.isCheckDone()) {
			doOpenMaterial(id, onError);
		} else {
			ensureLoginOperation();
			toOpen = id;
			// not logged in to Mebis while opening shared link: show login
			// dialog first
			if (!getLoginOperation().isLoggedIn() && !getLoginOperation()
					.getGeoGebraTubeAPI().anonymousOpen()) {
				getLoginOperation().getView()
						.add(new SharedFileOpenCallback(this, onError));
			} else {
				getLoginOperation().getView().add(new EventRenderable() {

					@Override
					public void renderEvent(BaseEvent event) {
						if (event instanceof LoginEvent
								|| event instanceof StayLoggedOutEvent
								|| event instanceof TubeAvailabilityCheckEvent) {
							checkOpen(onError, this);
						}
					}
				});
				getLoginOperation().performTokenLogin();
			}
			Log.debug("listening");
		}
	}

	/**
	 * Make sure login operation is initialized
	 */
	public void ensureLoginOperation() {
		if (getLoginOperation() == null) {
			this.initSignInEventFlow(new LoginOperationW(this));
		}
	}

	/**
	 * Initializes the user authentication
	 *  @param op
	 *            login operation
	 *
	 */
	public void initSignInEventFlow(LoginOperationW op) {
		// Initialize the signIn operation
		loginOperation = op;
		if (getNetworkOperation().isOnline()) {
			if (getLAF() != null && getLAF().supportsGoogleDrive()) {
				initGoogleDriveEventFlow();
			}
			if (!StringUtil.empty(appletParameters.getDataParamTubeID())
					|| appletParameters.getDataParamEnableFileFeatures()) {
				if (!op.loadUserFromSession()) {
					loginOperation.performTokenLogin();
				}
			}
		}
	}

	/**
	 * @param onError error handler
	 * @param caller temporary login listener, to be removed after opening
	 */
	protected void checkOpen(final AsyncOperation<String> onError,
			EventRenderable caller) {
		if (toOpen != null && toOpen.length() > 0) {
			doOpenMaterial(toOpen, onError);
			toOpen = "";
		}
		getLoginOperation().getView().remove(caller);
	}

	/**
	 * @param id material ID
	 * @param onError error callback
	 */
	public final void doOpenMaterial(String id,
			final AsyncOperation<String> onError) {
		getLoginOperation().getResourcesAPI()
				.getItem(id, new MaterialCallback() {

					@Override
					public void onLoaded(
							final List<Material> parseResponse,
							Pagination meta) {
						if (parseResponse.size() == 1) {
							Material material = parseResponse.get(0);
							material.setSyncStamp(
									parseResponse.get(0).getModified());
							AppWFull.this.setSyncStamp(
									parseResponse.get(0).getModified());
							registerOpenFileListener(
									getUpdateTitleCallback(material));
							if (!StringUtil.empty(material.getFileName())) {
								getArchiveLoader().processFileName(
										material.getFileName());
							} else {
								getGgbApi().setBase64(material.getBase64());
							}
							setActiveMaterial(material);
							if (material.isMultiuser()) {
								getShareController().startMultiuser(material.getSharingKeySafe());
							}
							ensureSupportedModeActive();
						} else {
							onError.callback(Errors.LoadFileFailed.getKey());
						}
					}

					@Override
					public void onError(Throwable error) {
						onError.callback(error.getMessage().contains("401")
								? Errors.NotAuthorized.getKey()
								: Errors.LoadFileFailed.getKey());
					}
				});
	}

	private void ensureSupportedModeActive() {
		if (getMode() == EuclidianConstants.MODE_MOVE && isWhiteboardActive()) {
			int mode = showToolBar ? EuclidianConstants.MODE_PEN
					: EuclidianConstants.MODE_SELECT_MOW;
			setMode(mode, ModeSetter.DOCK_PANEL);
		}
	}

	/**
	 * @param material loaded material
	 * @return callback that updates browser title
	 */
	public final OpenFileListener getUpdateTitleCallback(
			final Material material) {
		return () -> {
			this.updateMaterialURL(material);
			return true;
		};
	}

	@Override
	public final boolean isOffline() {
		return getDevice().isOffline(this);
	}

	/**
	 * @return glass pane
	 */
	public DockGlassPaneW getGlassPane() {
		return frame.getGlassPane();
	}

	@Override
	public final void showPerspectivesPopupIfNeeded() {
		boolean smallScreen = NavigatorUtil.getWindowWidth() < MIN_SIZE_FOR_PICKER
				|| NavigatorUtil.getWindowHeight() < MIN_SIZE_FOR_PICKER;
		if (isUnbundledOrWhiteboard() || smallScreen
				|| isAppletWithoutAppsPicker() || !examController.isIdle()
				|| !StringUtil.empty(getAppletParameters().getDataParamPerspective())) {
			return;
		}
		afterLocalizationLoaded(() -> getPerspectivesPopup().show());
	}

	private boolean isAppletWithoutAppsPicker() {
		return !(getAppletParameters().getDataParamShowAppsPicker() || getAppletParameters()
				.getDataParamApp());
	}

	/**
	 * Removed element called ggbsplash
	 */
	protected static void removeSplash() {
		Element el = DOM.getElementById("ggbsplash");
		if (el != null) {
			el.removeFromParent();
		}
	}

	@Override
	public final void appSplashCanNowHide() {
		String commands = NavigatorUtil.getUrlParameter("command");
		if (commands != null) {
			executeCommands(commands);
		}
		removeSplash();
	}

	private void executeCommands(String commands) {
		Log.debug("Executing commands: " + commands);
		EvalInfo info = EvalInfoFactory.getEvalInfoForAV(this);
		AsyncOperation<GeoElementND[]> callback =
				getConfig().hasAutomaticLabels() ? null : new LabelHiderCallback();
		for (String command : commands.split(";")) {
			Runnable r = () -> executeCommand(command, info, callback);
			getAsyncManager().runOrSchedule(r);
		}
	}

	private void executeCommand(String command, EvalInfo info,
			AsyncOperation<GeoElementND[]> callback) {
		try {
			getKernel().getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(command, false,
							ErrorHelper.silent(), info, callback);
		} catch (CommandNotLoadedError err) {
			throw err;
		} catch (Throwable throwable) {
			Log.error("Error evaluating input: " + command);
		}
	}

	@Override
	public final void setActivePerspective(Perspective perspective) {
		activePerspective = perspective;
	}

	/**
	 * @return active perspective ID
	 */
	public final Perspective getActivePerspective() {
		return activePerspective;
	}

	/**
	 * @return perspectives popup
	 */
	final PerspectivesPopup getPerspectivesPopup() {
		if (this.perspectivesPopup == null) {
			this.perspectivesPopup = new PerspectivesPopup(this);
		}
		return perspectivesPopup;
	}

	@Override
	public final boolean isPerspectivesPopupVisible() {
		return perspectivesPopup != null && perspectivesPopup.isShowing();
	}

	@Override
	public void updateViewSizes() {
		getEuclidianViewpanel().deferredOnResize();
		if (hasEuclidianView2(1)) {
			getGuiManager().getEuclidianView2DockPanel(1)
					.deferredOnResize();
		}
		if (getGuiManager().hasSpreadsheetView()) {
			DockPanel sp = getGuiManager().getLayout().getDockManager()
					.getPanel(App.VIEW_SPREADSHEET);
			if (sp != null) {
				sp.deferredOnResize();
			}
		}
		if (getGuiManager().hasCasView()) {
			DockPanelW sp = getGuiManager().getLayout().getDockManager()
					.getPanel(App.VIEW_CAS);
			if (sp != null) {
				sp.onResize();
			}
		}
		getAppletFrame()
				.setMenuHeight(getInputPosition() == InputPosition.bottom);
	}

	@Override
	public AppKeyboardType getKeyboardType() {
		if ("evaluator".equals(appletParameters.getDataParamAppName())) {
			String setting = appletParameters.getParamKeyboardType("normal");
			return AppKeyboardType.fromName(setting);
		}
		return getConfig().getKeyboardType();
	}

	@Override
	public InputBoxType getInputBoxType() {
		return inputBoxType;
	}

	@Override
	public List<String> getInputBoxFunctionVars() {
		return functionVars;
	}

	/**
	 * setter for input box function vars
	 * @param functionVars function vars connected to the inputbox
	 */
	public void setInputBoxFunctionVars(List<String> functionVars) {
		this.functionVars = functionVars;
	}

	/**
	 * setter for input box type
	 * @param inputBoxType new input box type
	 */
	public void setInputBoxType(InputBoxType inputBoxType) {
		this.inputBoxType = inputBoxType;
	}

	@Override
	public boolean attachedToEqEditor() {
		return isWhiteboardActive();
	}

	@Override
	public final GImageIconW wrapGetModeIcon(int mode) {
		GImageIconW icon = new GImageIconW("");
		GGWToolBar.getImageResource(mode, this, icon);
		return icon;
	}

	@Override
	public final void closePopups() {
		super.closePopups();
		PopupMenuButtonW.resetCurrentPopup();
		if (getToolbar() != null && getToolbar().isMobileToolbar()) {
			((GGWToolBar) getToolbar()).getToolBar().closeAllSubmenu();
		}
		if (isUnbundledOrWhiteboard()) {
			boolean justClosed = menuShowing;
			hideMenu();
			if (justClosed) {
				getEuclidianController().setPopupJustClosed(justClosed);
			}
		}
	}

	@Override
	public final void setToolbarPosition(int position, boolean update) {
		toolbarPosition = position;
		if (update) {
			updateApplicationLayout();
		}
	}

	/**
	 * @return device (tablet / Win store device / browser)
	 */
	public final GDevice getDevice() {
		if (device == null) {
			return new BrowserDevice();
		}
		return device;
	}

	@Override
	public final void setSaved() {
		super.setSaved();
		if (hasAutosave()) {
			getFileManager().deleteAutoSavedFile();
			getLAF().removeWindowClosingHandler();
		}
	}

	private boolean hasAutosave() {
		return appletParameters.getDataParamApp();
	}

	@Override
	public final void setUnsaved() {
		super.setUnsaved();
		if (hasAutosave() && kernel != null && kernel.getConstruction() != null
				&& kernel.getConstruction().isStarted()) {
			getLAF().addWindowClosingHandler(this);
		}
	}

	private void startDialogChain() {
		autosavedMaterial = getFileManager().getAutosaveJSON();
		afterLocalizationLoaded(this::maybeShowRecentChangesDialog);
	}

	private void maybeShowRecentChangesDialog() {
		if (ALLOW_RECENT_CHANGES_DIALOG
				&& shouldShowRecentChangesDialog(RECENT_CHANGES_KEY)
				&& isUnbundledGraphing()) {
			LocalizationW localization = getLocalization();
			String message = localization.getMenu(RECENT_CHANGES_KEY);
			String readMore = localization.getMenu("tutorial_apps_comparison");
			String link = "https://www.geogebra.org/m/" + readMore;
			showRecentChangesDialog(message, link, this::maybeStartAutosave);
			setHideRecentChanges(RECENT_CHANGES_KEY);
		} else {
			maybeStartAutosave();
		}
	}

	private String getRecentChangesCookieKey(String key) {
		return "RecentChanges" + key + "Shown";
	}

	private boolean shouldShowRecentChangesDialog(String key) {
		String shown = BrowserStorage.LOCAL.getItem(getRecentChangesCookieKey(key));
		return !"true".equals(shown);
	}

	private void setHideRecentChanges(String key) {
		BrowserStorage.LOCAL.setItem(getRecentChangesCookieKey(key), "true");
	}

	private void maybeStartAutosave() {
		if (hasMacroToRestore() || !getLAF().autosaveSupported()) {
			return;
		}
		if (autosavedMaterial != null && !isStartedWithFile() && examController.isIdle()) {
			afterLocalizationLoaded(() -> {
				getDialogManager().showRecoverAutoSavedDialog(
						this, autosavedMaterial);
				autosavedMaterial = null;
			});
		} else {
			startAutoSave();
		}
	}

	private void showRecentChangesDialog(String message, String link,
			final Runnable closingCallback) {
		DialogData data = new DialogData("WhatsNew", null, "OK");
		final WhatsNewDialog dialog = new WhatsNewDialog(this, data, message, link);
		dialog.addCloseHandler(closeEvent -> closingCallback.run());
		Timer timer = new Timer() {
			@Override
			public void run() {
				dialog.show();
			}
		};
		timer.schedule(0);
	}

	/**
	 * if there are unsaved changes, the file is saved to the localStorage.
	 */
	public final void startAutoSave() {
		Timer timer = new Timer() {
			private int counter = 0;

			@Override
			public void run() {
				counter++;
				if (!isSaved()) {
					getFileManager().autoSave(counter);
				}
				getFileManager().refreshAutosaveTimestamp();
			}
		};
		timer.scheduleRepeating(AUTO_SAVE_PERIOD);

	}

	@Override
	public final void loadPreferences(Perspective p) {
		GeoGebraPreferencesW.loadForApp(this, p);

	}

	/**
	 * Update the menu height
	 */
	public void updateMenuHeight() {
		if (menuShowing) {
			int h = this.oldSplitLayoutPanel.getOffsetHeight();
			if (!isFloatingMenu()) {
				frame.getMenuBar(this).setPixelSize(GLookAndFeel.MENUBAR_WIDTH,
						h);
			} else {
				frame.getMenuBar(this).setHeight(h + "px");
			}
		}
	}

	/**
	 * @return whether floating menu is used
	 */
	protected final boolean isFloatingMenu() {
		return isUnbundledOrWhiteboard();
	}

	@Override
	public final boolean isWhiteboardActive() {
		if (activity != null) {
			return activity.isWhiteboard();
		}
		return "notes"
				.equals(getAppletParameters().getDataParamAppName());
	}

	@Override
	public final void ensureStandardView() {
		getActiveEuclidianView()
				.setKeepCenter(true);
	}

	@Override
	public final void onHeaderVisible() {
		ToolbarPanel toolbar = getGuiManager().getUnbundledToolbar();
		if (isPortrait() && toolbar != null && toolbar.isClosed()) {
			toolbar.doCloseInPortrait();
		}
	}

	/**
	 * Empty the construction but don't initialize undo
	 */
	public void loadEmptySlide() {
		kernel.clearConstruction(true);
		getSelectionManager().clearSelectedGeos();
		resetMaxLayerUsed();
		setCurrentFile(null);
		resetUI();
		clearMedia();
		getEventDispatcher().dispatchEvent(EventType.LOAD_PAGE, null);
	}

	@Override
	public void setActiveSlide(String slideID) {
		if (getPageController() != null) {
			getPageController().setActiveSlide(slideID);
		}
	}

	private void afterCoreObjectsInited() {
		// Code to run before buildApplicationPanel
		initGuiManager();
		if (this.showConsProtNavigation(App.VIEW_EUCLIDIAN)) {
			((EuclidianDockPanelW) euclidianViewPanel).addNavigationBar();
		}
		// following lines were swapped before but for async file loading it
		// does not matter
		// and for sync file loading this makes sure perspective setting is not
		// blocked by initing flag
		initing = false;
		GeoGebraFrameW.handleLoadFile(appletParameters, this);
		MathFieldW.setGlobalEventCheck(GlobalKeyDispatcherW::isGlobalEvent);
	}

	private void buildSingleApplicationPanel() {
		if (frame != null) {
			frame.clear();
			frame.add((Widget) getEuclidianViewpanel());
			// we need to make sure trace works after this, see
			// https://jira.geogebra.org/browse/TRAC-4232
			// https://jira.geogebra.org/browse/TRAC-4034
			getEuclidianView1().createImage();
			getEuclidianView1().invalidateBackground();
			DockPanelW euclidianDockPanel = (DockPanelW) getEuclidianViewpanel();
			euclidianDockPanel.setVisible(true);

			euclidianDockPanel.setEmbeddedSize(getInnerAppletWidth());
			getEuclidianViewpanel().setPixelSize(
					getInnerAppletWidth(),
					getInnerAppletHeight());
			euclidianDockPanel.updatePanel(false);

			oldSplitLayoutPanel = null;
			updateVoiceover();
		}
	}

	@Override
	public void buildApplicationPanel() {
		if (!isUsingFullGui()) {
			if (showConsProtNavigation() || !isJustEuclidianVisible()) {
				useFullGui = true;
			}
		}

		if (!isUsingFullGui()) {
			buildSingleApplicationPanel();
			return;
		}
		for (int i = frame.getWidgetCount() - 1; i >= 0; i--) {
			if (!(frame.getWidget(i) instanceof HasKeyboardPopup
					|| frame.getWidget(i) instanceof TabbedKeyboard
					|| (menuViewController != null
					&& frame.getWidget(i) == menuViewController.getView())
					|| frame.getWidget(i) instanceof Persistable
					|| frame.getWidget(i).getStyleName().contains("perspectivePopup"))) {
				frame.remove(i);
			}
		}

		// showMenuBar should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (appletParameters.getDataParamShowMenuBar(showMenuBar)) {
			frame.attachMenubar(this);
		}
		// showToolBar should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (appletParameters.getDataParamShowToolBar(showToolBar)
				&& this.getToolbarPosition() != SwingConstants.SOUTH) {
			frame.attachToolbar(this);
		}
		if (this.getInputPosition() == InputPosition.top && appletParameters
				.getDataParamShowAlgebraInput(showAlgebraInput)) {
			attachAlgebraInput();
		}

		attachSplitLayoutPanel();

		if (isWhiteboardActive()) {
			frame.attachNotesUI(this);
		}
		GlobalHeader.INSTANCE.initLogo(this);
		GlobalHeader.INSTANCE.initAssignButton(() -> getShareController().assign(), this);

		// showAlgebraInput should come from data-param,
		// this is just a 'second line of defense'
		// otherwise it can be used for taking ggb settings into account too
		if (this.getInputPosition() == InputPosition.bottom && appletParameters
				.getDataParamShowAlgebraInput(showAlgebraInput)) {
			attachAlgebraInput();
		}
		if (appletParameters.getDataParamShowToolBar(showToolBar)
				&& this.getToolbarPosition() == SwingConstants.SOUTH) {
			frame.attachToolbar(this);
		}

		frame.attachGlass();
	}

	private void refreshSplitLayoutPanel() {
		if (frame != null && frame.getWidgetCount() != 0
				&& frame.getWidgetIndex(getSplitLayoutPanel()) == -1
				&& frame.getWidgetIndex(oldSplitLayoutPanel) != -1) {
			int wi = frame.getWidgetIndex(oldSplitLayoutPanel);
			frame.remove(oldSplitLayoutPanel);
			frame.insert(getSplitLayoutPanel(), wi);
			oldSplitLayoutPanel = getSplitLayoutPanel();
			Browser.removeDefaultContextMenu(
					getSplitLayoutPanel().getElement());
		}
	}

	/**
	 * Attach algebra input
	 */
	public void attachAlgebraInput() {
		// inputbar's width varies,
		// so it's probably good to regenerate every time
		GGWCommandLine inputbar = new GGWCommandLine();
		inputbar.attachApp(this);
		frame.add(inputbar);

		updateSplitPanelHeight();

		getGuiManager().getAlgebraInput()
				.setInputFieldWidth(this.getAppletWidth());
	}

	@Override
	protected final void updateTreeUI() {
		if (getSplitLayoutPanel() != null) {
			getSplitLayoutPanel().forceLayout();
		}
	}

	/**
	 * @return main panel
	 */
	public DockSplitPaneW getSplitLayoutPanel() {
		if (getGuiManager() == null) {
			return null;
		}
		if (getGuiManager().getLayout() == null) {
			return null;
		}
		return getGuiManager().getRootComponent();
	}

	private void attachSplitLayoutPanel() {
		boolean oldSLPanelChanged = oldSplitLayoutPanel != getSplitLayoutPanel();
		oldSplitLayoutPanel = getSplitLayoutPanel();

		if (oldSplitLayoutPanel != null) {
			if (!isFloatingMenu()
					&& getAppletParameters().getDataParamShowMenuBar(false)) {
				this.splitPanelWrapper = new HorizontalPanel();
				// TODO
				splitPanelWrapper.add(oldSplitLayoutPanel);
				if (this.menuShowing) {
					splitPanelWrapper.add(frame.getMenuBar(this));
				}
				frame.add(splitPanelWrapper);

			} else {
				frame.add(oldSplitLayoutPanel);
			}
			Browser.removeDefaultContextMenu(
					getSplitLayoutPanel().getElement());

			if (!oldSLPanelChanged) {
				return;
			}

			getGlobalHandlers().add(ClickStartHandler.init(oldSplitLayoutPanel,
					new ClickStartHandler() {
						@Override
						public void onClickStart(int x, int y,
								final PointerEventType type) {
							onUnhandledClick();
						}
					}));
		}
	}

	protected void onUnhandledClick() {
		updateAVStylebar();

		if (!isWhiteboardActive() && !CancelEventTimer.cancelKeyboardHide()) {
			DomGlobal.setTimeout(ignore -> hideKeyboard() , 0);
		}
	}

	@Override
	public void afterLoadFileAppOrNot(boolean asSlide) {
		super.afterLoadFileAppOrNot(asSlide);

		if (!getLAF().isSmart()) {
			removeSplash();
		}
		if (getAppletParameters().getDataParamApp()) {
			fitSizeToScreen();
		}
		String perspective = getAppletParameters().getDataParamPerspective();
		if (!isUsingFullGui()) {
			if (showConsProtNavigation() || !isJustEuclidianVisible()
					|| perspective.length() > 0) {
				useFullGui = true;
			}
		}
		frame.setApplication(this);

		if (!isUsingFullGui()) {
			buildSingleApplicationPanel();
			Perspective current = getTmpPerspective();
			if (current != null && current.getToolbarDefinition() != null) {
				getGuiManager().setGeneralToolBarDefinition(
						current.getToolbarDefinition());
				setPerspectives(current);
			}
		} else if (!asSlide) {
			getGuiManager().getLayout().getDockManager().init(frame);

			Perspective p = null;
			if (perspective != null && !StringUtil.isNaN(perspective)) {
				p = PerspectiveDecoder.decode(perspective,
						this.getKernel().getParser(),
						ToolBar.getAllToolsNoMacros(true, false, this),
						getLayout());
			}

			if (isUnbundled()) {
				setPerspectiveForUnbundled(p);
			}

			getGuiManager().updateFrameSize();
			if (appletParameters.getDataParamShowAlgebraInput(false)
					&& !isUnbundledOrWhiteboard()) {
				Perspective p2 = getTmpPerspective(p);
				if (!algebraVisible(p2)
						&& getInputPosition() == InputPosition.algebraView) {
					setInputPosition(InputPosition.bottom, false);
					p2.setInputPosition(InputPosition.bottom);
				}
			}

			if (!isUnbundled()) {
				setPerspectives(p);
			}
		} else {
			updateContentPane();
		}

		getEuclidianView1().synCanvasSize();

		if (!appletParameters.getDataParamFitToScreen()) {
			getAppletFrame().resetAutoSize();
		}

		getEuclidianView1().doRepaint2();
		frame.hideSplash();

		if (needsSpreadsheetTableModel()) {
			getSpreadsheetTableModel(); // spreadsheet trace also useful without UI
		}

		if (isUsingFullGui()) {
			refreshSplitLayoutPanel();

			// probably this method can be changed by more,
			// to be more like AppWapplication's method with the same name,
			// but preferring to change what is needed only to avoid new unknown
			// bugs
			if (getGuiManager().hasSpreadsheetView()) {
				DockPanel sp = getGuiManager().getLayout().getDockManager()
						.getPanel(App.VIEW_SPREADSHEET);
				if (sp != null) {
					sp.deferredOnResize();
				}
			}
		}

		if (isUsingFullGui()) {
			updateNavigationBars();
		}
		this.setPreferredSize(
				new Dimension((int) this.getWidth(), (int) this.getHeight()));
		setDefaultCursor();
		frame.useDataParamBorder();

		showStartTooltip(null);
		if (!isUnbundled() && isPortrait()) {
			adjustViews(false, false);
		}
		kernel.notifyScreenChanged();
		if (isWhiteboardActive()) {
			AdjustScreen.adjustCoordSystem(getActiveEuclidianView());
		}
		getScriptManager().ggbOnInit(); // should be only called after coord system is ready
		checkScaleContainer();
		onOpenFile();
		if (!asSlide) {
			// should run after coord system changed
			initUndoInfoSilent();
		} else {
			getEventDispatcher().dispatchEvent(EventType.LOAD_PAGE, null);
		}
		restoreCurrentUndoHistory();
	}

	/**
	 * Like Layout.setPerspective, but with additional checks for unbundled
	 *
	 * @param perspective perspective
	 */
	public void setPerspectiveForUnbundled(Perspective perspective) {
		Perspective fromXml = getTmpPerspective(perspective);

		Perspective forcedPerspective = PerspectiveDecoder
				.getDefaultPerspective(getConfig().getForcedPerspective(),
						getGuiManager().getLayout());

		LayoutW layout = getGuiManager().getLayout();
		updateAvVisibilityAndTab(forcedPerspective, fromXml);
		if (!StringUtil.empty(fromXml.getToolbarDefinition())) {
			layout.updateLayout(forcedPerspective, fromXml.getToolbarDefinition());
		} else {
			layout.updateLayout(forcedPerspective);
		}
		ToolbarPanel unbundledToolbar = getGuiManager().getUnbundledToolbar();
		if (unbundledToolbar != null) {
			unbundledToolbar.updateContent();
		}

		if (isPortrait()) {
			getGuiManager().getLayout().getDockManager().adjustViews(true);
		}

		setupToolbarPanelVisibility(fromXml.getDockPanelData());
	}

	private void updateAvVisibilityAndTab(Perspective forcedPerspective, Perspective fromXml) {
		DockPanelData[] oldDockPanelData = fromXml.getDockPanelData();
		DockPanelData[] dockPanelData = forcedPerspective.getDockPanelData();

		int oldAlgebra = findDockPanelData(oldDockPanelData, App.VIEW_ALGEBRA);
		int algebra = findDockPanelData(dockPanelData, App.VIEW_ALGEBRA);
		int viewId = getConfig().getMainGraphicsViewId();
		int oldEuclidian = findDockPanelData(oldDockPanelData, viewId);
		int euclidian = findDockPanelData(dockPanelData, viewId);

		double algebraWidth = 0;
		double euclidianWidth = 0;
		if (oldAlgebra != -1 && oldDockPanelData[oldAlgebra].isVisible()) {
			algebraWidth = oldDockPanelData[oldAlgebra].getEmbeddedSize();
		}

		if (oldEuclidian != -1 && oldDockPanelData[oldEuclidian].isVisible()) {
			euclidianWidth = oldDockPanelData[oldEuclidian].getEmbeddedSize();
		} else {
			dockPanelData[euclidian].setVisible(false);
		}
		if (algebraWidth != 0 || euclidianWidth != 0) {
			forcedPerspective.getSplitPaneData()[0]
					.setDivider(algebraWidth / (algebraWidth + euclidianWidth));
		}
		if (algebra != -1 && oldAlgebra != -1) {
			dockPanelData[algebra].setTabId(oldDockPanelData[oldAlgebra].getTabId());
		}
	}

	private void setPerspectives(Perspective p) {
		getGuiManager().getLayout().setPerspectiveOrDefault(
				p == null ? getTmpPerspective() : p);
	}

	private void setupToolbarPanelVisibility(DockPanelData[] dockPanelData) {
		int algebra = findDockPanelData(dockPanelData, App.VIEW_ALGEBRA);
		int euclidian = findDockPanelData(dockPanelData,
				isUnbundled3D() ? App.VIEW_EUCLIDIAN3D : App.VIEW_EUCLIDIAN);

		boolean isAvVisible = algebra != -1 && dockPanelData[algebra].isVisible();
		boolean isEvVisible = euclidian != -1 && dockPanelData[euclidian].isVisible();

		ToolbarPanel toolbarPanel = getGuiManager().getUnbundledToolbar();
		if (!isAvVisible) {
			toolbarPanel.hideToolbarImmediate();
			toolbarPanel.setLastOpenWidth(ToolbarPanel.OPEN_START_WIDTH_LANDSCAPE);
		} else if (isEvVisible) {
			invokeLater(() -> {
				toolbarPanel.setLastOpenWidth(ToolbarPanel.OPEN_START_WIDTH_LANDSCAPE);
				toolbarPanel.open();
			});
		} // else assume that toolbarPanel is fully open.
	}

	private static boolean algebraVisible(Perspective p2) {
		if (p2 == null || p2.getDockPanelData() == null) {
			return false;
		}
		for (DockPanelData dp : p2.getDockPanelData()) {
			if (dp.getViewId() == App.VIEW_ALGEBRA) {
				return dp.isVisible() && !dp.isOpenInFrame();
			}
		}
		return false;
	}

	@Override
	public boolean hasFocus() {
		return frame.getElement().isOrHasChild(Dom.getActiveElement());
	}

	@Override
	public void setCustomToolBar() {
		String customToolbar = appletParameters.getDataParamCustomToolBar();
		if ((customToolbar != null) && (customToolbar.length() > 0)
				&& (appletParameters.getDataParamShowToolBar(false))
				&& (getGuiManager() != null)) {
			getGuiManager().setGeneralToolBarDefinition(customToolbar);
		}
	}

	/**
	 * Check if just the euclidian view is visible in the document just loaded.
	 * @return whether just ev1 is isible
	 */
	private boolean isJustEuclidianVisible() {
		Perspective docPerspective = getTmpPerspective();

		if (docPerspective == null) {
			return true;
		}

		for (DockPanelData panel : docPerspective.getDockPanelData()) {
			if ((panel.getViewId() != App.VIEW_EUCLIDIAN)
					&& panel.isVisible()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void updateCenterPanel() {
		buildApplicationPanel();
		if (oldSplitLayoutPanel == null) {
			return; // simple GUI: avoid NPE
		}
		this.oldSplitLayoutPanel.setPixelSize(spWidth, spHeight);
		// we need relative position to make sure the menubar / toolbar are not
		// hidden
		this.oldSplitLayoutPanel.getElement().getStyle()
				.setPosition(Position.RELATIVE);
		if (!isUnbundled() && getGuiManager().hasAlgebraView()
				&& showView(App.VIEW_ALGEBRA)) {
			getAlgebraView().setShowAlgebraInput(showAlgebraInput()
					&& getInputPosition() == InputPosition.algebraView);
		}
	}

	@Override
	public double getWidth() {
		if (spWidth > 0) {
			return menuShowing && !isFloatingMenu()
					? spWidth + GLookAndFeel.MENUBAR_WIDTH : spWidth;
		}
		return super.getWidth();
	}

	@Override
	public void updateContentPane() {
		super.updateContentPane();
		frame.setApplication(this);
		frame.refreshKeyboard();
	}

	@Override
	public void persistWidthAndHeight() {
		if (this.oldSplitLayoutPanel != null) {
			spWidth = oldSplitLayoutPanel.getEstimateWidth();
			spHeight = oldSplitLayoutPanel.getEstimateHeight();
		}
	}

	@Override
	public int getWidthForSplitPanel(int fallback) {
		if (spWidth > 0) {
			return spWidth;
		}
		return super.getWidthForSplitPanel(fallback);
	}

	@Override
	public int getHeightForSplitPanel(int fallback) {
		if (spHeight > 0) {
			return spHeight;
		}
		return super.getHeightForSplitPanel(fallback);
	}

	@Override
	protected void getLayoutXML(StringBuilder sb, boolean asPreference) {
		super.getLayoutXML(sb, asPreference);
	}

	@Override
	public void toggleMenu() {
		if (!menuShowing) {
			getAppletFrame().hidePanel(null);
			menuShowing = true;
			boolean needsUpdate = isMenuInited;
			if (!isFloatingMenu() && !isMenuInited) {
				frame.getMenuBar(this).init(this);
				isMenuInited = true;
			} else if (menuViewController != null) {
				if (!menuViewController.getView().isAttached()) {
					frame.insert(menuViewController.getView(), 0);
					frame.getApp().invokeLater(() -> menuViewController.setMenuVisible(true));
				} else {
					menuViewController.setMenuVisible(true);
				}
				return;
			}
			splitPanelWrapper.add(frame.getMenuBar(this));
			spWidth = oldSplitLayoutPanel.getOffsetWidth()
					- GLookAndFeel.MENUBAR_WIDTH;
			oldSplitLayoutPanel.setPixelSize(spWidth,
					oldSplitLayoutPanel.getOffsetHeight());
			updateMenuHeight();
			if (needsUpdate) {
				frame.getMenuBar(this).getMenubar().updateMenubar();
			}
			getGuiManager().refreshDraggingViews();
			oldSplitLayoutPanel.getElement().getStyle()
					.setOverflow(Overflow.HIDDEN);
			frame.getMenuBar(this).getMenubar().dispatchOpenEvent();
		} else {
			if (menuViewController != null) {
				menuViewController.setMenuVisible(false);
			} else {
				hideMenu();
			}
		}
	}

	@Override
	public void onMenuOpened() {
		menuShowing = true;
		updateMenuBtnStatus(true);
	}

	@Override
	public void onMenuClosed() {
		menuShowing = false;
		updateMenuBtnStatus(false);
		getAccessibilityManager().focusFirstElement();
	}

	private void updateMenuBtnStatus(boolean expanded) {
		if (getGuiManager() != null) {
			ToolbarPanel toolbarPanel = getGuiManager()
					.getUnbundledToolbar();
			if (toolbarPanel != null) {
				toolbarPanel.markMenuAsExpanded(expanded);
			}
		}
	}

	@Override
	public void hideMenu() {
		if (!isMenuInited || !menuShowing) {
			return;
		}

		if (menuViewController != null) {
			menuViewController.setMenuVisible(false);
		} else {
			spWidth = this.oldSplitLayoutPanel.getOffsetWidth()
					+ GLookAndFeel.MENUBAR_WIDTH;
			this.oldSplitLayoutPanel.setPixelSize(
					spWidth,
					this.oldSplitLayoutPanel.getOffsetHeight());
			if (this.splitPanelWrapper != null) {
				this.splitPanelWrapper.remove(frame.getMenuBar(this));
			}
			oldSplitLayoutPanel.getElement().getStyle()
					.setOverflow(Overflow.VISIBLE);
		}
		this.menuShowing = false;

		if (getGuiManager() != null && getGuiManager().getLayout() != null) {
			getGuiManager().getLayout().getDockManager().resizePanels();
		}

		if (getGuiManager() != null) {
			getGuiManager().setDraggingViews(false, true);
		}
	}

	@Override
	public boolean isMenuShowing() {
		return this.menuShowing;
	}

	@Override
	public void addToHeight(int i) {
		this.setSpHeight(spHeight + i);
	}

	/**
	 * Updates height of split panel accordingly if there is algebra input
	 * and/or toolbar or not.
	 */
	@Override
	public void updateSplitPanelHeight() {
		int newHeight = frame.computeHeight() - getToolbarAndInputBarHeight();

		if (frame.isKeyboardShowing()) {
			newHeight -= frame.getKeyboardHeight();

		}
		if (newHeight >= 0) {
			this.setSpHeight(newHeight);
			if (oldSplitLayoutPanel != null) {
				oldSplitLayoutPanel.setHeight(spHeight + "px");
				getGuiManager().getLayout().getDockManager().resizeProbabilityCalculator();
				getGuiManager().updateUnbundledToolbar();
			}
		}
	}

	@Override
	public int getToolbarAndInputBarHeight() {
		int height = 0;
		if (showAlgebraInput()
				&& getInputPosition() != InputPosition.algebraView) {
			height += GLookAndFeel.COMMAND_LINE_HEIGHT;
		}
		if (showToolBar() && !isUnbundledOrWhiteboard()) {
			height += GLookAndFeel.TOOLBAR_HEIGHT;
		}
		if (isWhiteboardActive()) {
			height += frame.getNotesLayoutSafe(this).getTopBarHeight();
		}
		return height;
	}

	@Override
	public double getInnerWidth() {
		return getKeyboardManager().getKeyboardWidth();
	}

	@Override
	public void centerAndResizeViews() {
		centerAndResizePopups();
		resizePropertiesView();
		DockPanelW dp = getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA);
		if (dp instanceof AlgebraDockPanelW) {
			dp.onResize(); // to force branding visibility update
		}
	}

	private void centerAndResizePopups() {
		for (HasHide w : popups) {
			if (w instanceof RequiresResize) {
					((GPopupPanel) w).centerAndResize(
						this.getAppletFrame().getKeyboardHeight());
			}
		}
	}

	private void resizePropertiesView() {
		if (getGuiManager().hasPropertiesView()
				&& isUnbundledOrWhiteboard()) {
			((PropertiesViewW) getGuiManager().getPropertiesView()).resize(
					getWidth(), getHeight() - frame.getKeyboardHeight());
		}
	}

	@Override
	public void share() {
		FileMenuW.share(this, null);
	}

	@Override
	public void setFileVersion(String version, String appName) {
		super.setFileVersion(version, appName);
		if (!"auto".equals(appName)
				&& "auto".equals(getAppletParameters().getDataParamAppName())) {
			String appCode = getConfig().getAppCode();
			getAppletParameters().setAttribute("appName",
					appName == null ? "" : appName);

			boolean isClassic = "classic".equals(appName) || StringUtil.empty(appName);
			if (isClassic && !isApplet()) {
				removeHeader();
			}

			if (!appCode.equals(appName)) {
				this.activity = null;
				initActivity();
				getGuiManager().resetPanels();
			}
		}
	}

	@Override
	public void updateAppCodeSuite(String subApp, Perspective p) {
		if (SUITE_APPCODE.equals(getAppletParameters().getDataParamAppName())) {
			String appCode = getConfig().getSubAppCode();
			if (appCode != null && !appCode.equals(subApp)) {
				this.activity = new SuiteActivity(subApp,
						!getSettings().getCasSettings().isEnabled());
				setPerspective(p);
				updateSidebarAndMenu(subApp);
				reinitAlgebraView();
				getGuiManager().resetPanels();
				setSuiteHeaderButton(subApp);
			}
			getDialogManager().hideCalcChooser();
		}
	}

	private void removeUndoRedoPanel() {
		ToolbarPanel unbundledToolbar = getGuiManager().getUnbundledToolbar();
		if (unbundledToolbar != null) {
			unbundledToolbar.removeToolsTab();
			unbundledToolbar.removeUndoRedoPanel();
		}
	}

	@Override
	public void showCustomizeToolbarGUI() {
		showBrowser(getCustomizeToolbarGUI());
	}

	@Override
	public void exportCollada(boolean html) {
		if (html) {
			setDirectExport3D(new FormatColladaHTML());
		} else {
			setDirectExport3D(new FormatCollada());
		}
	}

	@Override
	public EmbedManager getEmbedManager() {
		if (embedManager == null && isWhiteboardActive()) {
			embedManager = new EmbedManagerW(this);
		}
		return embedManager;
	}

	@Override
	public final @Nonnull
	VideoManagerW getVideoManager() {
		if (videoManager == null) {
			videoManager = new VideoManagerW(this);
		}
		return videoManager;
	}

	@Override
	public MaskWidgetList getMaskWidgets() {
		if (maskWidgets == null) {
			maskWidgets = new MaskWidgetListW(this);
		}
		return maskWidgets;
	}

	/**
	 * Remove all widgets for videos and embeds.
	 */
	@Override
	public void clearMedia() {
		if (videoManager != null) {
			videoManager.removePlayers();
		}
		if (embedManager != null) {
			embedManager.removeAll();
		}
	}

	private void setSpHeight(int spHeight) {
		this.spHeight = spHeight;
	}

	@Override
	public void openPDF(File file) {
		this.getDialogManager().showPDFInputDialog(file);
	}

	@Override
	public SaveController getSaveController() {
		if (saveController == null) {
			saveController = new SaveControllerW(this);
		}
		return saveController;
	}

	@Override
	public ShareController getShareController() {
		if (shareController == null) {
			shareController = new ShareControllerW(this);
		}
		return shareController;
	}

	@Override
	public AlgebraViewW getAlgebraView() {
		if (getGuiManager() == null) {
			return null;
		}
		return getGuiManager().getAlgebraView();
	}

	@Override
	public JsPropertyMap<Object> getEmbeddedCalculators(boolean includeGraspableMath) {
		getEmbedManager();
		return embedManager != null
				? embedManager.getEmbeddedCalculators(includeGraspableMath)
				: null;
	}

	@Override
	public @Nonnull KeyboardManager getKeyboardManager() {
		if (keyboardManager == null) {
			keyboardManager = new KeyboardManager(this);
		}
		return keyboardManager;
	}

	/**
	 * Updates the keyboard size
	 */
	public void resizeKeyboard() {
		if (keyboardManager != null) {
			keyboardManager.resizeKeyboard();
		}
	}

	@Override
	public ScriptManager newScriptManager() {
		return new ScriptManagerW(this, getActivity().getExportedApi());
	}

	@Override
	protected SettingsUpdaterBuilder newSettingsUpdaterBuilder() {
		return super.newSettingsUpdaterBuilder()
				.withFontSettingsUpdater(new FontSettingsUpdaterW(this));
	}

	@Override
	public HasLastItem getLastItemProvider() {
		if (!getConfig().hasAnsButtonInAv()
				|| getActiveEuclidianView().getEuclidianController()
				.isSymbolicEditorSelected()) {
			return null;
		}
		return new ConstructionItemProvider(getKernel().getConstruction(), getAlgebraView(),
				createGeoElementValueConverter());
	}

	/**
	 * Starts the exam mode
	 * @param examType {@link ExamType}
	 * @param options exam options used for Classic
	 */
	public void startExam(ExamType examType, ExamOptions options) {
		attachToExamController();

		if (examController.getState() == ExamState.IDLE
				|| examController.getState() == ExamState.PREPARING) {
			examController.startExam(examType, options);
		}
		if (supportsExamUI()) {
			getLAF().toggleFullscreen(true);
		}
		if (guiManager != null) {
			guiManager.resetBrowserGUI();
			if (menuViewController != null) {
				menuViewController.setExamMenu();
				guiManager.updateUnbundledToolbarStyle();
				guiManager.resetMenu();
				guiManager.updateUnbundledToolbarContent();
				if (supportsExamUI()) {
					new ExamUtil(this).addVisibilityAndBlurHandlers();
					GlobalHeader.INSTANCE.addExamTimer();
					guiManager.initInfoBtnAction();
				}
			}
		}
	}

	private void attachToExamController() {
		examController.registerContext(this,
				getKernel().getAlgebraProcessor().getCommandDispatcher(),
				getKernel().getAlgebraProcessor(),
				getLocalization(),
				getSettings(),
				getAutocompleteProvider(),
				this);
		examController.registerRestrictable(this);
		examController.registerRestrictable(getEuclidianView1());
		examController.registerDelegate(new ExamControllerDelegateW(this));
		examController.addListener(getExamEventBus());
	}

	@Override
	public void detachFromExamController() {
		examController.unregisterContext(this);
		examController.unregisterRestrictable(this);
		examController.unregisterRestrictable(getEuclidianView1());
		examController.removeListener(getExamEventBus());
		if (getGuiManager() != null && getGuiManager().hasAlgebraView()) {
			GlobalScope.examController.unregisterRestrictable(
					getAlgebraView().getSelectionCallback());
		}
	}

	/**
	 * Ends the exam mode, exits the exam view.
	 */
	public void endExam() {
		examController.exitExam();
		resetViewsEnabled();
		getGuiManager().getLayout().resetPerspectives(this);
		getLAF().addWindowClosingHandler(this);
		fireViewsChangedEvent();
		guiManager.updateToolbarActions();
		guiManager.setGeneralToolBarDefinition(ToolBar.getAllToolsNoMacros(true, false, this));
		if (menuViewController != null) {
			menuViewController.setDefaultMenu();
		}
		guiManager.resetMenu();
		guiManager.resetBrowserGUI();
		guiManager.updateUnbundledToolbarContent();
		setActivePerspective(getGuiManager().getLayout().getDefaultPerspectives(0));
		resetCommandDict();
	}

	@Override
	public InlineTextController createInlineTextController(EuclidianView view, GeoInline geo) {
		Element parentElement = ((EuclidianViewW) view).getAbsolutePanel().getParent().getElement();
		return new InlineTextControllerW(geo, view, parentElement);
	}

	@Override
	public InlineFormulaController createInlineFormulaController(EuclidianView view,
			GeoFormula geo) {
		EuclidianDockPanelW panel = (EuclidianDockPanelW) getGuiManager().getLayout()
				.getDockManager().getPanel(VIEW_EUCLIDIAN);
		return new InlineFormulaControllerW(geo, this, panel.getEuclidianPanel());
	}

	@Override
	public InlineTableController createTableController(EuclidianView view, GeoInlineTable geo) {
		Element parentElement = ((EuclidianViewW) view).getAbsolutePanel().getParent().getElement();
		return new InlineTableControllerW(geo, view, parentElement);
	}

	@Override
	public void closeMenuHideKeyboard() {
		if (menuShowing) {
			hideMenu();
		}
		if (getAppletFrame().isKeyboardShowing()) {
			hideKeyboard();
		}
	}

	@Override
	protected void initFactories() {
		super.initFactories();
		if (!CASFactory.isInitialized()) {
			CASFactory.setPrototype(new CASFactoryW());
		}
	}

	private void reinitAlgebraView() {
		ToolbarPanel toolbar = getGuiManager().getUnbundledToolbar();
		if (toolbar != null) {
			toolbar.initGUI();
		}
	}

	@Override
	public void switchToSubapp(String subAppCode) {
		BrowserStorage.LOCAL.setItem(BrowserStorage.LAST_USED_SUB_APP, subAppCode);
		getDialogManager().hideCalcChooser();
		CommandFilter commandFilter = getConfig().getCommandFilter();
		if (commandFilter != null) {
			getKernel().getAlgebraProcessor().getCmdDispatcher().removeCommandFilter(commandFilter);
		}
		storeCurrentUndoHistory();
		storeCurrentMaterial();
		getGuiManager().closePropertiesView();
		activity = new SuiteActivity(subAppCode, !getSettings().getCasSettings().isEnabled());
		preloadAdvancedCommandsForSuiteCAS();
		activity.start(this);
		getKernel().removeAllMacros();
		getGuiManager().setGeneralToolBarDefinition(ToolBar.getAllTools(this));
		final Perspective perspective = PerspectiveDecoder.getDefaultPerspective(
				getConfig().getForcedPerspective(), getGuiManager().getLayout());
		updateSidebarAndMenu(subAppCode);
		reinitSettings();
		clearConstruction();
		resetToolbarPanel(); // after construction clear so that TV functions can be set up
		setTmpPerspective(null);
		removeUndoRedoPanel();
		getGuiManager().resetPanels();
		getGuiManager().getLayout().applyPerspective(perspective);

		frame.fitSizeToScreen();

		kernel.initUndoInfo();
		kernel.resetFiltersFromConfig();
		resetCommandDict();
		if (suiteAppPickerButton != null) {
			suiteAppPickerButton.setIconAndLabel(subAppCode);
		}
		if (restoreMaterial(subAppCode)) {
			registerOpenFileListener(() -> {
				afterMaterialRestored();
				return true;
			});
		} else {
			afterMaterialRestored();
		}
		getEventDispatcher().dispatchEvent(new Event(EventType.SWITCH_CALC, null, subAppCode));
	}

	private void afterMaterialRestored() {
		getGuiManager().getLayout().getDockManager().adjustViews(true);
		resetFullScreenBtn();
		reinitAlgebraView();
		if (!examController.isIdle()) {
			setActiveMaterial(examController.getNewTempMaterial());
		}
	}

	private void storeCurrentMaterial() {
		Material material = getActiveMaterial();
		if (material == null) {
			material = new Material(Material.MaterialType.ggb);
		}
		material.setContent(getGgbApi().getFileJSON(false));
		constructionJson.put(getConfig().getSubAppCode(), material);
		setActiveMaterial(null);
	}

	private boolean restoreMaterial(String subAppCode) {
		Material material = constructionJson.get(subAppCode);
		if (material != null) {
			Object oldConstruction = material.getContent();
			if (oldConstruction != null) {
				getGgbApi().setFileJSON(oldConstruction);
			}
			if (material.getSharingKey() != null) {
				setActiveMaterial(material);
				updateMaterialURL(material);
				return true;
			}
		}

		resetEVs();
		resetUrl();
		setTitle();
		return material != null;
	}

	@Override
	protected void resetFileHandle() {
		if (fm != null) {
			fm.resetFileHandle();
		}
	}

	private void storeCurrentUndoHistory() {
		UndoManager undoManager = kernel.getConstruction().getUndoManager();
		undoManager.undoHistoryTo(undoHistory);
	}

	private void restoreCurrentUndoHistory() {
		UndoManager undoManager = kernel.getConstruction().getUndoManager();
		undoManager.undoHistoryFrom(undoHistory);
	}

	private void updateSidebarAndMenu(String subAppCode) {
		getKernel().setSymbolicMode(
				GeoGebraConstants.CAS_APPCODE.equals(subAppCode)
						? SymbolicMode.SYMBOLIC_AV
						: SymbolicMode.NONE);

		if (menuViewController != null) {
			menuViewController.resetMenuOnAppSwitch(this);
		}
	}

	private void reinitSettings() {
		initSettingsUpdater().resetSettingsOnAppStart();
		guiManager.updatePropertiesView();
		guiManager.updatePropertiesViewStylebar();
		guiManager.updateGlobalOptions();
	}

	/**
	 * @param subappCode
	 *            - subapp code
	 */
	public void setSuiteHeaderButton(String subappCode) {
		if (suiteAppPickerButton != null) {
			suiteAppPickerButton.setIconAndLabel(subappCode);
			GlobalHeader.onResize();
		}
	}

	/**
	 * Clear type and function variables for input box.
	 */
	public void resetInputBox() {
		inputBoxType = null;
		functionVars = Collections.emptyList();
	}

	private void resetFullScreenBtn() {
		GuiManagerW gm = getGuiManager();
		DockPanel avPanel = gm.getLayout().getDockManager()
				.getPanel(VIEW_ALGEBRA);
		if (avPanel instanceof ToolbarDockPanelW) {
			((ToolbarDockPanelW) avPanel).tryBuildZoomPanel();
		}
	}

	/**
	 * clears the cunstruction of all subapps in suite
	 */
	public void clearSubAppCons() {
		constructionJson.clear();
	}

	/**
	 * @return csv import handler
	 */
	public CsvImportHandler getCsvImportHandler() {
		if (csvImportHandler == null) {
			csvImportHandler = new CsvImportHandler(this);
		}
		return csvImportHandler;
	}

	public Command getCsvHandler() {
		return getCsvImportHandler().getCsvHandler();
	}

	/**
	 * @return autocomplete provider for AV and classic input bar
	 */
	public AutocompleteProvider getAutocompleteProvider() {
		if (autocompleteProvider == null) {
			autocompleteProvider = new AutocompleteProvider(this, false);
		}
		return autocompleteProvider;
	}

	/**
	 * @return listener forwarding exam change events to other listeners
	 */
	public @Nonnull ExamEventBus getExamEventBus() {
		if (this.examEventBus == null) {
			examEventBus = new ExamEventBus();
		}
		return examEventBus;
	}
}
