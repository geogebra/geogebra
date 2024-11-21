package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.geogebra.common.gui.ContextMenuGeoElement;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.ReflexAngleModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.gui.contextmenu.OrderSubMenu;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.html5.AttachedToDOM;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CopyPasteW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.dom.client.Element;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.InlineHTML;

/**
 * @author gabor
 *
 *         ContextMenuGeoElement for Web
 *
 */
public class ContextMenuGeoElementW extends ContextMenuGeoElement
		implements AttachedToDOM {

	/**
	 * popup menu
	 */
	protected final GPopupMenuW wrappedPopup;
	/**
	 * localization
	 */
	protected Localization loc;
	private LabelController labelController;
	private final ContextMenuItemFactory factory;

	/**
	 * Creates new context menu
	 *  @param app
	 *            application
	 * @param factory
	 *            widget factory
	 */
	ContextMenuGeoElementW(AppW app, ContextMenuItemFactory factory) {
		super(app);
		this.factory = factory;
		this.app = app;
		this.loc = app.getLocalization();
		wrappedPopup = factory.newPopupMenu(app);
	}

	/**
	 * Creates new MyPopupMenu for GeoElement
	 *
	 * @param app
	 *            application
	 * @param geos
	 *            selected elements
	 */
	public ContextMenuGeoElementW(AppW app, ArrayList<GeoElement> geos,
								  ContextMenuItemFactory factory) {
		this(app, factory);
		initPopup(geos);
	}

	/**
	 * @param geos
	 *            list of geos
	 */
	private void initPopup(ArrayList<GeoElement> geos) {
		wrappedPopup.clearItems();
		if (geos == null || geos.size() == 0 || !geos.get(0).isLabelSet()) {
			return;
		}
		this.setGeos(geos);
		setGeo(geos.get(0));

		if (!app.isUnbundledOrWhiteboard()) {
			String title;
			if (geos.size() == 1) {
				title = getGeoTitle();
			} else {
				title = loc.getMenu("Selection");
			}
			setTitle(title);
		}
	}

	private String getGeoTitle() {
		if (noLabel()) {
			return getGeo().getTypeString();
		}
		return getDescription(getGeo(), false);
	}

	private boolean noLabel() {
		if (labelController == null) {
			labelController = new LabelController();
		}
		return !app.getConfig().hasAutomaticLabels()
				&& !labelController.hasLabel(getGeo());
	}

	/**
	 * add other items like special for lines and conics
	 */
	public void addOtherItems() {
		if (app.getGuiManager() != null
				&& app.getGuiManager().showView(App.VIEW_ALGEBRA)) {
			addCoordsModeItems();
			if (app.getSettings().getCasSettings().isEnabled()) {
				addLineItems();
				addConicItems();
				addNumberItems();
				addUserInputItem();
			}
		}

		// TODO remove the condition when ggb version >= 5
		if (app.getKernel().getManager3D() != null) {
			addPlaneItems();
		}

		if (wrappedPopup.getComponentCount() > 2 && !app.isWhiteboardActive()) {
			wrappedPopup.addSeparator();
		}

		if (getFocusedGroupElement() != null) {
			addItemsForFocusedInGroup();
		} else {
			addForAllItems();
		}
	}

	private GeoElement getFocusedGroupElement() {
		return app.getSelectionManager().getFocusedGroupElement();
	}

	private void addItemsForFocusedInGroup() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(getFocusedGroupElement());
		InlineFormattingItems textitems = addInlineTextItems(geos);
		textitems.addFormatItems();
		textitems.addTableItemsIfNeeded();
		textitems.addChartItem();
		addLayerItem(geos);
	}

	private void addForAllItems() {
		if (getGeo() == null) {
			return;
		}

		if (app.isUnbundled()) {
			addDuplicate();
			addAnglePropertiesForUnbundled();
			addPinItem();
			addFixForUnbundledOrNotes();
		} else if (app.isWhiteboardActive()) {
			InlineFormattingItems textItems = addInlineTextItems(getGeos());
			textItems.addFormatItems();
			textItems.addChartItem();
			if (!textItems.isEditModeTable() || textItems.isSingleTableCellSelection()) {
				addCutCopyPaste();
			}
			textItems.addTableItemsIfNeeded();
			if (textItems.isEditModeTable() || textItems.isSingleTableCellSelection()) {
				return;
			}
			boolean layerAdded = addLayerItem(getGeos());
			boolean groupsAdded = addGroupItems();
			if (layerAdded || groupsAdded) {
				getWrappedPopup().addSeparator();
			}
			addFixForUnbundledOrNotes();
		}

		// SHOW, HIDE

		// G.Sturr 2010-5-14: allow menu to show spreadsheet trace for
		// non-drawables
		if (getGeo().isDrawable() || (getGeo().isSpreadsheetTraceable()
				&& app.getGuiManager() != null
				&& app.getGuiManager().showView(App.VIEW_SPREADSHEET))) {

			addShowObjectItem();
			addShowLabelItem();
			addTraceItem();
			addSpreadsheetTraceItem();
			addAnimationItem();
			addAuxiliaryItem();

			if (!app.isUnbundledOrWhiteboard()) {
				addLockForClassic();
				addPinItem();
			}
			if (!app.isWhiteboardActive()) {
				wrappedPopup.addSeparator();
			}
		}

		if (!app.isUnbundledOrWhiteboard()) {
			addRenameForClassic();
		}

		// DELETE
		addDeleteItem();
		addPropertiesItem();
	}

	private InlineFormattingItems addInlineTextItems(ArrayList<GeoElement> geos) {
		return new InlineFormattingItems(app, geos, wrappedPopup, factory);
	}

	private boolean addLayerItem(ArrayList<GeoElement> geos) {
		if (containsMask(geos)) {
			return false;
		}

		wrappedPopup.addItem(newSubMenuItem("General.Order",
				new OrderSubMenu(app, geos, factory)));
		return true;
	}

	private static boolean containsMask(Collection<GeoElement> geos) {
		for (GeoElement geo : geos) {
			if (geo.isMask()) {
				return true;
			}
		}
		return false;
	}

	private AriaMenuItem newSubMenuItem(String key, AriaMenuBar submenu) {
		return factory.newAriaMenuItem(app.getLocalization().getMenu(key), null, submenu);
	}

	private boolean addGroupItems() {
		GroupItems items = new GroupItems(app);
		return items.addAvailableItems(wrappedPopup);
	}

	private void addPropertiesItem() {
		// Object properties menuitem
		if (app.showMenuBar() && app.letShowPropertiesDialog()
				&& getGeo().hasProperties()) {
			if (!app.isUnbundledOrWhiteboard()) {
				wrappedPopup.addSeparator();
			}

			SVGResource img = MaterialDesignResources.INSTANCE.gear();

			// open properties dialog
			addHtmlAction(() -> openPropertiesDialogCmd(), img,
					loc.getMenu("Settings"));
		}
	}

	private void addDeleteItem() {
		if (app.letDelete() && !getGeo().isProtected(EventType.REMOVE)
				&& !app.isUnbundledOrWhiteboard()) {

			SVGResource img = MaterialDesignResources.INSTANCE.delete_black();

			addHtmlAction(() -> deleteCmd(false),
					img, loc.getMenu("Delete"));
		}
	}

	private void addAnimationItem() {
		if (getGeo().isAnimatable()) {
			ResourcePrototype img = GuiResourcesSimple.INSTANCE.play_circle();

			GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					img, loc.getMenu("Animation"),
					getGeo().isAnimating()
							&& app.getKernel().getAnimatonManager().isRunning(),
					this::animationCmd
			);
			wrappedPopup.addItem(cmItem);
		}
	}

	private void addAuxiliaryItem() {
		if (app.getGuiManager() != null
				&& app.getGuiManager().showView(App.VIEW_ALGEBRA)
				&& app.showAuxiliaryObjects() && getGeo().isAlgebraShowable()) {
			ResourcePrototype img = AppResources.INSTANCE.aux_folder();

			GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					img, loc.getMenu("AuxiliaryObject"),
					getGeo().isAuxiliaryObject(),
					this::showObjectAuxiliaryCmd
			);
			wrappedPopup.addItem(cmItem);
		}
	}

	private void addSpreadsheetTraceItem() {
		if (getGeo().isSpreadsheetTraceable()
				&& app.getGuiManager().showView(App.VIEW_SPREADSHEET)) {
			// check if other geos are recordable
			for (int i = 1; i < getGeos().size(); i++) {
				if (!getGeos().get(i).isSpreadsheetTraceable()) {
					return;
				}
			}

			ResourcePrototype img = MaterialDesignResources.INSTANCE.record_to_spreadsheet_black();

			GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					img, loc.getMenu("RecordToSpreadsheet"),
					getGeo().getSpreadsheetTrace(),
					this::recordToSpreadSheetCmd
			);
			wrappedPopup.addItem(cmItem);
		}
	}

	private void addTraceItem() {
		if (getGeo().isTraceable() && !app.getConfig().disableTraceCM()) {
			ResourcePrototype img = MaterialDesignResources.INSTANCE.trace_black();

			GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					img, loc.getMenu("ShowTrace"),
					isTracing(),
					this::traceCmd
			);
			wrappedPopup.addItem(cmItem);
		}
	}

	private void addShowLabelItem() {
		if (!app.isUnbundledOrWhiteboard() && getGeo().isLabelShowable()) {
			ResourcePrototype img = ToolbarSvgResourcesSync.INSTANCE.mode_showhidelabel_32();
			GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					img, loc.getMenu("ShowLabel"),
					isLabelShown(),
					this::showLabelCmd
			);
			wrappedPopup.addItem(cmItem);
		}
	}

	private void addShowObjectItem() {
		if (!app.isUnbundledOrWhiteboard() && getGeo().isEuclidianToggleable()) {
			ResourcePrototype img = ToolbarSvgResources.INSTANCE.mode_showhideobject_32();
			GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					img, loc.getMenu("ShowObject"),
					getGeo().isSetEuclidianVisible(),
					this::showObjectCmd
			);
			wrappedPopup.addItem(cmItem);
		}
	}

	private void addLockForClassic() {
		ResourcePrototype img = MaterialDesignResources.INSTANCE.lock_black();

		if (getGeo().isFixable() && (getGeo().isGeoText()
				|| getGeo().isGeoImage() || getGeo().isGeoButton())) {
			GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					img, loc.getMenu("LockObject"),
					getGeo().isLocked(),
					() -> fixObjectCmd(!getGeo().isLocked())
			);
			wrappedPopup.addItem(cmItem);
		} else if (getGeo().isGeoNumeric()) {
			final GeoNumeric num = (GeoNumeric) getGeo();
			if (num.isSlider()) {
				GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
						img, loc.getMenu("LockObject"),
						num.isLockedPosition(),
						() -> fixObjectNumericCmd(num)
				);
				wrappedPopup.addItem(cmItem);
			}
		} else if (getGeo().isGeoBoolean()) {
			GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					img, loc.getMenu("FixCheckbox"),
					getGeo().isLockedPosition(),
					this::fixCheckboxCmd
			);
			wrappedPopup.addItem(cmItem);
		}
	}

	private void addRenameForClassic() {
		if (getGeos() == null || !(getGeos().size() == 1 && app.letRename()
				&& getGeo().isRenameable())) {
			return;
		}

		SVGResource img = MaterialDesignResources.INSTANCE.rename_black();

		addHtmlAction(() -> renameCmd(),
				img, loc.getMenu("Rename"));

		if (getGeos().size() == 1 && getGeo() instanceof TextValue
				&& !getGeo().isTextCommand()
				&& !getGeo().isProtected(EventType.UPDATE)) {

			SVGResource img2 = MaterialDesignResources.INSTANCE.edit_black();

			addHtmlAction(() -> editCmd(),
					img2, loc.getMenu("Edit"));
		}
	}

	private void addAnglePropertiesForUnbundled() {
		if (AngleArcSizeModel.match(getGeo())) {
			ResourcePrototype img = MaterialDesignResources.INSTANCE.angle_black();
			addSubmenuAction(img, loc.getMenu("Angle"), getAngleSubMenu());
		}
	}

	private void addPinItem() {
		final GeoElement geo = getGeo();

		if (geo.isPinnable()) {
			ResourcePrototype img = MaterialDesignResources.INSTANCE.pin_black();

			final GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					img, loc.getMenu("PinToScreen"),
					geo.isPinned(),
					() -> pinCmd(geo.isPinned())
			);

			wrappedPopup.addItem(cmItem);
		}
	}

	private void addFixForUnbundledOrNotes() {
		ArrayList<GeoElement> selection = app.getSelectionManager().getSelectedGeos();
		if (selection.size() > 1) {
			addFixForSelection(selection);
		} else {
			addFixObjectForOneGeo();
		}
	}

	private void addFixObjectForOneGeo() {
		final GeoElement geo = getGeo();
		// change back to old name-> Fix instead of Lock
		if (geo.isFixable() && (!app.getConfig().isObjectDraggingRestricted()
				|| !geo.isFunctionOrEquationFromUser())) {
			addFixObjectMenuItem(geo.isLocked(), () -> fixObjectCmd(!geo.isLocked()));
		}
	}

	private void addFixObjectMenuItem(boolean locked, Runnable command) {
		SVGResource img = MaterialDesignResources.INSTANCE.lock_black();
		final GCheckmarkMenuItem cmItem = factory.newCheckmarkMenuItem(
				img, loc.getMenu("FixObject"),
				locked, command::run);
		wrappedPopup.addItem(cmItem);
	}

	private void addFixForSelection(ArrayList<GeoElement> selectedGeos) {
		boolean fixable = true;
		boolean locked = !app.getConfig().isObjectDraggingRestricted();
		for (GeoElement geo: selectedGeos) {
			fixable = fixable && geo.isFixable();
			locked = locked && geo.isLocked();
		}

		if (!fixable) {
			return;
		}

		final boolean fix = !locked;
		addFixObjectMenuItem(locked, () -> fixObjectCmd(fix));
	}

	private void addCutCopyPaste() {
		if (!(getGeo() instanceof GeoEmbed
				&& ((GeoEmbed) getGeo()).isGraspableMath())) {
			addCutCopy();
		}
		addPasteItem();
		wrappedPopup.addSeparator();
	}

	private void addCutCopy() {
		MaterialDesignResources resources = MaterialDesignResources.INSTANCE;

		Command cutCommand = () -> {
			app.setWaitCursor();
			cutCmd();
			app.setDefaultCursor();
		};

		addHtmlAction(cutCommand, resources.cut_black(),
				loc.getMenu("Cut"));

		Command copyCommand = () -> {
			app.setWaitCursor();
			copyCmd();
			app.setDefaultCursor();
		};

		addHtmlAction(copyCommand, resources.copy_black(), loc.getMenu("Copy"));
	}

	private void addDuplicate() {
		Command duplicateCommand = () -> {
			app.setWaitCursor();
			duplicateCmd();
			app.setDefaultCursor();
		};

		addHtmlAction(duplicateCommand,
						MaterialDesignResources.INSTANCE.duplicate_black(),
						loc.getMenu("Duplicate"));

	}

	/**
	 * add paste menu item
	 */
	protected void addPasteItem() {
		ResourcePrototype img = MaterialDesignResources.INSTANCE.paste_black();

		Command pasteCommand = () -> {
			app.setWaitCursor();
			pasteCmd();
			app.setDefaultCursor();
		};

		final AriaMenuItem menuPaste = addHtmlAction(pasteCommand, img,
				loc.getMenu("Paste"));

		CopyPasteW.checkClipboard(menuPaste::setEnabled);
	}

	private void addPlaneItems() {
		if (!(getGeo() instanceof ViewCreator)) {
			return;
		}

		final ViewCreator plane = (ViewCreator) getGeo();

		Command action = () -> {
			plane.setView2DVisible(true);
			Log.debug("set plane visible : " + plane);
		};
		addAction(action, app.getLocalization().getPlain("ShowAas2DView",
				getGeo().getLabelSimple()));
	}

	private void addUserInputItem() {
		if (app.isUnbundledOrWhiteboard()) {
			return;
		}

		GeoElement geo = getGeo();

		if (geo instanceof GeoImplicit) {
			final GeoImplicit inputElement = (GeoImplicit) geo;
			if (inputElement.isValidInputForm()) {
				Command action;
				if (inputElement.isInputForm()) {
					action = () -> implicitConicEquationCmd();
					addAction(action, loc.getMenu("ExpandedForm"));
				} else {
					action = () -> inputFormCmd(geo);
					addAction(action, loc.getMenu("InputForm"));
				}

			}
		} else if (needsInputFormItem(geo)) {
			Command action = () -> inputFormCmd(geo);
			addAction(action, loc.getMenu("InputForm"));
		} else if (geo instanceof GeoPlaneND && geo.getDefinition() != null) {
			Command action = () -> implicitConicEquationCmd();
			addAction(action, loc.getMenu("ExpandedForm"));
		}
	}

	private void addNumberItems() {
		// no items
	}

	private void addConicItems() {
		if (app.isUnbundledOrWhiteboard()) {
			return;
		}
		if (!ConicEqnModel.isValid(getGeo())) {
			return;
		}
		GeoQuadricND conic = (GeoQuadricND) getGeo();
		// there's no need to show implicit equation
		// if you can't select the specific equation
		boolean specificPossible = conic.isSpecificPossible();
		boolean explicitPossible = conic.isExplicitPossible();
		boolean vertexformPossible = conic.isVertexformPossible();
		boolean conicformPossible = conic.isConicformPossible();
		boolean userPossible = conic.getDefinition() != null;
		if (!(specificPossible || explicitPossible || userPossible)) {
			return;
		}

		int mode = conic.getToStringMode();
		Command action;
		StringBuilder sb = new StringBuilder();

		if (mode != QuadraticEquationRepresentable.Form.CONST_IMPLICIT) {
			sb.append(ConicEqnModel.getImplicitEquation(conic, loc, true));
			action = () -> implicitConicEquationCmd();
			addAction(action, sb.toString());
		}

		if (specificPossible && mode != QuadraticEquationRepresentable.Form.CONST_SPECIFIC) {
			// specific conic string
			String conicEqn = conic.getSpecificEquation();
			if (conicEqn != null) {
				sb.setLength(0);
				sb.append(loc.getMenu("Equation"));
				sb.append(' ');
				sb.append(conicEqn);
				action = () -> equationConicEqnCmd();
				addAction(action, sb.toString());
			}
		}

		if (explicitPossible && mode != QuadraticEquationRepresentable.Form.CONST_EXPLICIT) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ExplicitConicEquation"));
			action = () -> equationExplicitConicEquationCmd();
			addAction(action, sb.toString());
		}

		if (vertexformPossible && mode != QuadraticEquationRepresentable.Form.CONST_VERTEX) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ParabolaVertexForm"));
			action = () -> equationVertexEquationCmd();
			addAction(action, sb.toString());
		}

		if (conicformPossible && mode != QuadraticEquationRepresentable.Form.CONST_CONICFORM) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ParabolaConicForm"));
			action = () -> equationConicformEquationCmd();
			addAction(action, sb.toString());
		}
	}

	private void addLineItems() {
		if (app.isUnbundledOrWhiteboard()) {
			return;
		}
		if (!(getGeo() instanceof GeoLine)) {
			return;
		}
		if (getGeo() instanceof GeoSegment) {
			return;
		}

		GeoLine line = (GeoLine) getGeo();
		int mode = line.getToStringMode();
		Command action;

		StringBuilder sb = new StringBuilder();

		if (mode != LinearEquationRepresentable.Form.IMPLICIT.rawValue) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ImplicitLineEquation"));
			action = () -> equationImplicitEquationCmd();
			addAction(action, sb.toString());
		}

		if (mode != LinearEquationRepresentable.Form.EXPLICIT.rawValue) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ExplicitLineEquation"));
			action = () -> equationExplicitEquationCmd();
			addAction(action, sb.toString());
		}

		if (mode != LinearEquationRepresentable.Form.PARAMETRIC.rawValue) {
			action = () -> parametricFormCmd();
			addAction(action, loc.getMenu("ParametricForm"));
		}

		if (mode != LinearEquationRepresentable.Form.GENERAL.rawValue) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("GeneralLineEquation"));
			action = () -> equationGeneralLineEquationCmd();
			addAction(action, sb.toString());
		}
	}

	private void addCoordsModeItems() {
		if (app.isUnbundledOrWhiteboard()) {
			return;
		}

		if (!(getGeo() instanceof CoordStyle) || getGeo() instanceof GeoLine) {
			return;
		}

		if (getGeo().isProtected(EventType.UPDATE)) {
			return;
		}

		CoordStyle point = (CoordStyle) getGeo();
		int mode = point.getToStringMode();
		Command action;

		switch (mode) {
		case Kernel.COORD_COMPLEX:
		default:
			return;

		// 2D coords styles
		case Kernel.COORD_POLAR:
			action = () -> setCoordStyle(Kernel.COORD_CARTESIAN);
			addAction(action, loc.getMenu("CartesianCoords"));
			break;

		case Kernel.COORD_CARTESIAN:
			action = () -> setCoordStyle(Kernel.COORD_POLAR);
			addAction(action, loc.getMenu("PolarCoords"));
			break;

		// 3D coords styles
		case Kernel.COORD_SPHERICAL:
			action = () -> setCoordStyle(Kernel.COORD_CARTESIAN_3D);
			addAction(action, loc.getMenu("CartesianCoords"));
			break;

		case Kernel.COORD_CARTESIAN_3D:
			action = () -> setCoordStyle(Kernel.COORD_SPHERICAL);
			addAction(action, loc.getMenu("Spherical"));
			break;
		}
	}

	/**
	 * @param action
	 *            action to perform on click
	 * @param text
	 *            text of menu item
	 */
	private void addAction(Command action, String text) {
		AriaMenuItem mi = factory.newAriaMenuItem(null, text, action);
		wrappedPopup.addItem(mi);
	}

	/**
	 * @param action
	 *            action to perform on click
	 * @param html
	 *            html string of menu item
	 * @return new menu item
	 */
	private AriaMenuItem addHtmlAction(Command action, ResourcePrototype icon, String html) {
		AriaMenuItem mi = factory.newAriaMenuItem(icon, html, action);
		wrappedPopup.addItem(mi);
		return mi;
	}

	/**
	 * @param img
	 *            icon
	 * @param text
	 *            name of menu item
	 * @param subMenu
	 *            sub menu
	 */
	private void addSubmenuAction(ResourcePrototype img, String text,
			AriaMenuBar subMenu) {
		AriaMenuItem mi = factory.newAriaMenuItem(text, img, subMenu);
		wrappedPopup.addItem(mi);
	}

	/**
	 * @param str
	 *            title of menu (first menu item)
	 */
	protected void setTitle(String str) {
		AriaMenuItem title = new AriaMenuItem(new InlineHTML(str),
				() -> wrappedPopup.setVisible(false));
		title.addStyleName("menuTitle");

		wrappedPopup.addItem(title);
		wrappedPopup.addSeparator();
	}

	/**
	 * @return popup
	 */
	public GPopupMenuW getWrappedPopup() {
		return wrappedPopup;
	}

	/**
	 * @param c
	 *            canvas
	 * @param x
	 *            coord
	 * @param y
	 *            coord
	 */
	public void showScaled(Element c, int x, int y) {
		wrappedPopup.showAndFocus(c, x, y);
	}

	@Override
	public void removeFromDOM() {
		getWrappedPopup().removeFromDOM();
	}

	private AriaMenuBar getAngleSubMenu() {
		String[] angleIntervals = new String[GeoAngle.getIntervalMinListLength()
				- 1];
		for (int i = 0; i < GeoAngle.getIntervalMinListLength() - 1; i++) {
			angleIntervals[i] = app.getLocalization().getPlain(
					"AngleBetweenAB.short", GeoAngle.getIntervalMinList(i),
					GeoAngle.getIntervalMaxList(i));
		}

		AriaMenuBar mnu = new AriaMenuBar();
		GeoElement[] geos = { getGeo() };
		final ReflexAngleModel model = new ReflexAngleModel(app, false);
		model.setGeos(geos);

		for (int i = 0; i < angleIntervals.length; i++) {
			final int idx = i;
			AriaMenuItem mi = factory.newAriaMenuItem(
					AppResources.INSTANCE.empty(), angleIntervals[i],
					() -> model.applyChanges(idx));
			mnu.addItem(mi);
		}
		return mnu;
	}

	/**
	 * update whole popup
	 */
	public void update() {
		initPopup(app.getActiveEuclidianView()
				.getEuclidianController().getAppSelectedGeos());
		addOtherItems();
	}
}
