package org.geogebra.web.full.gui;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.gui.ContextMenuGeoElement;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.ObjectNameModel;
import org.geogebra.common.gui.dialog.options.model.ReflexAngleModel;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.html5.AttachedToDOM;
import org.geogebra.web.full.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.Command;

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
	private AriaMenuItem mnuPaste;
	private LabelController labelController;

	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 */
	ContextMenuGeoElementW(AppW app) {
		super(app);
		this.app = app;
		this.loc = app.getLocalization();
		wrappedPopup = new GPopupMenuW(app);
	}

	/**
	 * Creates new MyPopupMenu for GeoElement
	 * 
	 * @param app
	 *            application
	 * @param geos
	 *            selected elements
	 */
	public ContextMenuGeoElementW(AppW app, ArrayList<GeoElement> geos) {
		this(app);
		initPopup(geos);
	}

	/**
	 * @param geos
	 *            list of geos
	 */
	public void initPopup(ArrayList<GeoElement> geos) {
		wrappedPopup.clearItems();
		if (geos == null || geos.size() == 0 || !geos.get(0).isLabelSet()) {
			return;
		}
		this.setGeos(geos);
		setGeo(geos.get(0));

		String title;
		if (geos.size() == 1) {
			title = getGeoTitle();
		} else {
			title = loc.getMenu("Selection");
		}
		setTitle(title);

		if (app.isUnbundledOrWhiteboard()) {
			wrappedPopup.getPopupPanel().addStyleName("matMenu");
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
		return ObjectNameModel.isAutoLabelNeeded(app)
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
		addForAllItems();
	}

	private void addForAllItems() {
		if (getGeo() == null) {
			return;
		}
		if (app.isUnbundledOrWhiteboard()) {
			addRename();
			addEditItems();
			addObjectPropertiesMenu();
			addPinAndFixObject();
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
				addLock();
				addPin();
			}
			if (!app.isWhiteboardActive()) {
				wrappedPopup.addSeparator();
			}
		}

		if (!app.isUnbundledOrWhiteboard()) {
			addRename();
		}

		// DELETE
		addDeleteItem();
		addPropertiesItem();
	}

	private void addPropertiesItem() {
		// if (isWhiteboard()) {
		// wrappedPopup.addSeparator();
		// addSelect();
		// addOrder();
		// }
		// Object properties menuitem
		if (app.showMenuBar() && app.letShowPropertiesDialog()
				&& getGeo().hasProperties()) {
			if (!app.isUnbundledOrWhiteboard()) {
				wrappedPopup.addSeparator();
			}

			String img;
			if (app.isUnbundledOrWhiteboard()) {
				img = MaterialDesignResources.INSTANCE.gear().getSafeUri()
						.asString();
			} else {
				img = AppResources.INSTANCE.view_properties16().getSafeUri()
						.asString();
			}

			// open properties dialog
			addAction(new Command() {

				@Override
				public void execute() {
					openPropertiesDialogCmd();
				}
			}, MainMenu.getMenuBarHtmlClassic(img,
					loc.getMenu("Settings")),
					loc.getMenu("Settings"));
		}
	}

	private void addDeleteItem() {
		if (app.letDelete() && !getGeo().isProtected(EventType.REMOVE)
				&& !app.isUnbundledOrWhiteboard()) {

			String img;
			if (app.isUnbundled()) {
				img = MaterialDesignResources.INSTANCE.delete_black()
						.getSafeUri().asString();
			} else {
				img = AppResources.INSTANCE.delete_small().getSafeUri()
						.asString();
			}

			addAction(new Command() {

				@Override
				public void execute() {
					deleteCmd(false);
				}
			}, MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("Delete")),
					loc.getMenu("Delete"));
		}
	}

	private void addAnimationItem() {
		if (getGeo().isAnimatable()) {
			GCheckBoxMenuItem cbItem;
			String img;
			if (app.isUnbundledOrWhiteboard()) {
				img = GuiResourcesSimple.INSTANCE.play_black().getSafeUri()
						.asString();
			} else {
				img = AppResources.INSTANCE.empty().getSafeUri().asString();
			}

			cbItem = new GCheckBoxMenuItem(
					MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("Animation")),
					new Command() {

						@Override
						public void execute() {
							animationCmd();
						}
					}, true, app);
			cbItem.setSelected(getGeo().isAnimating()
					&& app.getKernel().getAnimatonManager().isRunning(),
					wrappedPopup.getPopupMenu());
			wrappedPopup.addItem(cbItem);
		}
	}

	private void addAuxiliaryItem() {
		GCheckBoxMenuItem cbItem;
		if (app.getGuiManager() != null
				&& app.getGuiManager().showView(App.VIEW_ALGEBRA)
				&& app.showAuxiliaryObjects() && getGeo().isAlgebraShowable()) {
			cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtmlClassic(
					AppResources.INSTANCE.aux_folder().getSafeUri().asString(),
					loc.getMenu("AuxiliaryObject")), new Command() {

						@Override
						public void execute() {
							showObjectAuxiliaryCmd();
						}
					}, true, app);
			cbItem.setSelected(getGeo().isAuxiliaryObject(),
					wrappedPopup.getPopupMenu());
			wrappedPopup.addItem(cbItem);

		}
	}

	private void addSpreadsheetTraceItem() {
		if (getGeo().isSpreadsheetTraceable()
				&& app.getGuiManager().showView(App.VIEW_SPREADSHEET)) {
			GCheckBoxMenuItem cbItem;
			boolean showRecordToSpreadsheet = true;
			// check if other geos are recordable
			for (int i = 1; i < getGeos().size()
					&& showRecordToSpreadsheet; i++) {
				showRecordToSpreadsheet = getGeos().get(i)
						.isSpreadsheetTraceable();
			}

			if (showRecordToSpreadsheet) {

				String img;

				if (app.isUnbundledOrWhiteboard()) {
					img = MaterialDesignResources.INSTANCE
							.record_to_spreadsheet_black().getSafeUri()
							.asString();
				} else {
					img = AppResources.INSTANCE.spreadsheettrace().getSafeUri()
							.asString();
				}

				if (app.isUnbundledOrWhiteboard()) {
					cbItem = new GCheckBoxMenuItem(
							MainMenu.getMenuBarHtmlClassic(img, ""),
							loc.getMenu("DontRecordToSpreadsheet"),
							loc.getMenu("RecordToSpreadsheet"), new Command() {
								@Override
								public void execute() {
									recordToSpreadSheetCmd();
								}
							}, true, app);
				} else {
					cbItem = new GCheckBoxMenuItem(
							MainMenu.getMenuBarHtmlClassic(img,
									loc.getMenu("RecordToSpreadsheet")),
							new Command() {

								@Override
								public void execute() {
									recordToSpreadSheetCmd();
								}
							}, true, app);
				}
				cbItem.setSelected(getGeo().getSpreadsheetTrace(),
						wrappedPopup.getPopupMenu());
				wrappedPopup.addItem(cbItem);
			}
		}
	}

	private void addTraceItem() {
		GCheckBoxMenuItem cbItem;
		if (getGeo().isTraceable()) {

			ResourcePrototype img;

			if (app.isUnbundledOrWhiteboard()) {
				img = MaterialDesignResources.INSTANCE.trace_black();
			} else {
				img = AppResources.INSTANCE.trace_on();
			}

			if (app.isUnbundledOrWhiteboard()) {
				final GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
						MainMenu.getMenuBarHtml(img, loc.getMenu("ShowTrace")),
						MaterialDesignResources.INSTANCE.check_black(),
						isTracing());
				Command cmdTrace = new Command() {

					@Override
					public void execute() {
						traceCmd();
						cmItem.setChecked(isTracing());
					}
				};
				cmItem.setCommand(cmdTrace);
				wrappedPopup.addItem(cmItem);
			} else {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(img,
						loc.getMenu("ShowTrace")), new Command() {

							@Override
							public void execute() {
								traceCmd();
							}
						}, true, app);
				cbItem.setSelected(getGeo().getTrace(),
						wrappedPopup.getPopupMenu());
				wrappedPopup.addItem(cbItem);
			}
		}
	}

	private void addShowLabelItem() {
		GCheckBoxMenuItem cbItem;
		if (!app.isUnbundledOrWhiteboard() && getGeo().isLabelShowable()) {
			cbItem = new GCheckBoxMenuItem(
					MainMenu.getMenuBarHtmlClassic(
							AppResources.INSTANCE.mode_showhidelabel_16()
									.getSafeUri().asString(),
							loc.getMenu("ShowLabel")),
					new Command() {

						@Override
						public void execute() {
							showLabelCmd();
						}
					}, true, app);
			cbItem.setSelected(isLabelShown(), wrappedPopup.getPopupMenu());
			wrappedPopup.addItem(cbItem);
		}
	}

	private void addShowObjectItem() {
		GCheckBoxMenuItem cbItem;
		if (!app.isUnbundledOrWhiteboard() && getGeo().isEuclidianToggleable()) {
			cbItem = new GCheckBoxMenuItem(
					MainMenu.getMenuBarHtmlClassic(
							AppResources.INSTANCE.mode_showhideobject_16()
									.getSafeUri().asString(),
							loc.getMenu("ShowObject")),
					new Command() {

						@Override
						public void execute() {
							showObjectCmd();
						}
					}, true, app);
			cbItem.setSelected(getGeo().isSetEuclidianVisible(),
					wrappedPopup.getPopupMenu());
			wrappedPopup.addItem(cbItem);

		}
	}

	private void addLock() {
		GCheckBoxMenuItem cbItem;
		if (getGeo().isFixable() && (getGeo().isGeoText()
				|| getGeo().isGeoImage() || getGeo().isGeoButton())) {

			String img = AppResources.INSTANCE.objectFixed().getSafeUri()
					.asString();

			if (app.isUnbundledOrWhiteboard()) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtmlClassic(img, ""),
						loc.getMenu("UnlockObject"), loc.getMenu("LockObject"),
						new Command() {

							@Override
							public void execute() {
								fixObjectCmd();
							}
						}, true, app);
			} else {
				cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("LockObject")),
						new Command() {

							@Override
							public void execute() {
								fixObjectCmd();
							}
						}, true, app);
			}

			cbItem.setSelected(getGeo().isLocked(),
					wrappedPopup.getPopupMenu());
			wrappedPopup.addItem(cbItem);
		} else if (getGeo().isGeoNumeric()) {
			final GeoNumeric num = (GeoNumeric) getGeo();
			if (num.isSlider()) {

				String img = AppResources.INSTANCE.objectFixed().getSafeUri()
						.asString();

				if (app.isUnbundledOrWhiteboard()) {
					cbItem = new GCheckBoxMenuItem(
							MainMenu.getMenuBarHtmlClassic(img, ""),
							loc.getMenu("UnlockObject"),
							loc.getMenu("LockObject"), new Command() {

								@Override
								public void execute() {
									fixObjectNumericCmd(num);
								}
							}, true, app);
				} else {
					cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtmlClassic(img,
							loc.getMenu("LockObject")), new Command() {

								@Override
								public void execute() {
									fixObjectNumericCmd(num);
								}
							}, true, app);
				}

				cbItem.setSelected(num.isSliderFixed(),
						wrappedPopup.getPopupMenu());
				wrappedPopup.addItem(cbItem);
			}
		} else if (getGeo().isGeoBoolean()) {

			String img = AppResources.INSTANCE.objectFixed().getSafeUri()
					.asString();

			cbItem = new GCheckBoxMenuItem(
					MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("FixCheckbox")),
					new Command() {

						@Override
						public void execute() {
							fixCheckboxCmd();
						}
					}, true, app);
			cbItem.setSelected(((GeoBoolean) getGeo()).isCheckboxFixed(),
					wrappedPopup.getPopupMenu());
			wrappedPopup.addItem(cbItem);
		}
	}

	private void addRename() {
		if (getGeos() == null || !(getGeos().size() == 1 && app.letRename()
				&& getGeo().isRenameable()) || app.isUnbundledOrWhiteboard()) {
			return;
		}

		String img = AppResources.INSTANCE.rename20().getSafeUri().asString();

		addAction(new Command() {

			@Override
			public void execute() {
				renameCmd();
			}
		}, MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("Rename")),
				loc.getMenu("Rename"));

		if (getGeos().size() == 1 && getGeo() instanceof TextValue
				&& !getGeo().isTextCommand()
				&& !getGeo().isProtected(EventType.UPDATE)) {

			String img2 = AppResources.INSTANCE.edit().getSafeUri().asString();

			addAction(new Command() {

				@Override
				public void execute() {
					editCmd();
				}
			}, MainMenu.getMenuBarHtmlClassic(img2, loc.getMenu("Edit")),
					loc.getMenu("Edit"));
		}
	}

	private void addObjectPropertiesMenu() {
		if (!app.isUnbundledOrWhiteboard()) {
			return;
		}

		GeoElement geo = getGeo();

		boolean showLabel = ShowLabelModel.match(geo);
		boolean angle = AngleArcSizeModel.match(geo);

		if (!(showLabel || angle)) {
			return;
		}
		if (!app.isWhiteboardActive()) {
			wrappedPopup.addSeparator();
		}

		// Angle
		if (angle) {
			String img = MaterialDesignResources.INSTANCE.angle_black()
					.getSafeUri().asString();
			addSubmenuAction(MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("Angle")),
					loc.getMenu("Angle"), getAngleSubMenu());

		}
	}

	private void addPinAndFixObject() {
		if (!app.isUnbundledOrWhiteboard() || app.isWhiteboardActive()) {
			return;
		}

		final GeoElement geo = getGeo();
		boolean pinnable = geo.isPinnable();
		boolean fixable = geo.isFixable();
		if (!(pinnable || fixable)) {
			return;
		}

		if (pinnable) {
			final boolean pinned = geo.isPinned();

			String img = MaterialDesignResources.INSTANCE.pin_black().getSafeUri()
					.asString();

			final GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					MainMenu.getMenuBarHtmlClassic(img,
							loc.getMenu("PinToScreen")),
					MaterialDesignResources.INSTANCE.check_black(),
					pinned);
			
			Command cmdPin = new Command() {
				
				@Override
				public void execute() {
					pinCmd(pinned);
					cmItem.setChecked(pinned);
				}
			};
			cmItem.setCommand(cmdPin);
			wrappedPopup.addItem(cmItem);
		}

		// change back to old name-> Fix instead of Lock
		String label = loc.getMenu("FixObject");
		if (fixable
				&& app.getSelectionManager().getSelectedGeos().size() <= 1
				&& !app.isExam()) {
			Command cmd = new Command() {

				@Override
				public void execute() {
					ArrayList<GeoElement> geoArray = new ArrayList<>();
					geoArray.add(geo);
					EuclidianStyleBarStatic.applyFixObject(geoArray,
							!geo.isLocked(), app.getActiveEuclidianView());
				}
			};

			String img = MaterialDesignResources.INSTANCE.lock_black().getSafeUri()
					.asString();
			final GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
					MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("FixObject")),
					MaterialDesignResources.INSTANCE.check_black(),
					geo.isLocked());
			Command cmdLock = new Command() {

				@Override
				public void execute() {
					ArrayList<GeoElement> geoArray = new ArrayList<>();
					geoArray.add(geo);
					EuclidianStyleBarStatic.applyFixObject(geoArray,
							!geo.isLocked(), app.getActiveEuclidianView());
					cmItem.setChecked(geo.isLocked());
				}
			};
			cmItem.setCommand(cmdLock);
			wrappedPopup.addItem(cmItem);

			if (!app.isUnbundledOrWhiteboard()) {
				addAction(cmd, MainMenu.getMenuBarHtmlClassic(img, label), label);
			}
		}
	}

	private void addEditItems() {
		if (app.isUnbundledOrWhiteboard()) {
			SVGResource img = MaterialDesignResources.INSTANCE
					.duplicate_black();
			addAction(new Command() {

				@Override
				public void execute() {
					app.setWaitCursor();
					duplicateCmd();
					app.setDefaultCursor();

				}
			}, MainMenu.getMenuBarHtml(img, loc.getMenu("Duplicate")),
					loc.getMenu("Duplicate"));
		}
	}

	/**
	 * add paste menu item
	 */
	protected void addPasteItem() {
		if (app.isUnbundled()) {
			return;
		}

		ResourcePrototype img;
		if (app.isUnbundledOrWhiteboard()) {
			img = MaterialDesignResources.INSTANCE.paste_black();
		} else {
			img = GuiResources.INSTANCE.menu_icon_edit_paste();
		}

		mnuPaste = addAction(new Command() {

			@Override
			public void execute() {
				if (!app.getCopyPaste().isEmpty()) {
					app.setWaitCursor();
					app.getCopyPaste().pasteFromXML(app, false);
					app.setDefaultCursor();
				}
			}
		}, MainMenu.getMenuBarHtml(img, loc.getMenu("Paste")),
				loc.getMenu("Paste"));
	}

	/**
	 * update paste menu item
	 */
	protected void updatePasteItem() {
		if (!app.isUnbundled() && mnuPaste != null) {
			mnuPaste.setEnabled(!app.getCopyPaste().isEmpty());
		}
	}

	/**
	 * update edit menu item
	 */
	protected void updateEditItems() {
		// overridden in subclass
	}

	private void addPin() {
		if (getGeo().isPinnable()) {

			String img;
			final boolean pinned = getGeo().isPinned();

			if (app.isUnbundledOrWhiteboard()) {
				img = MaterialDesignResources.INSTANCE.pin_black()
								.getSafeUri().asString();
			} else {
				img = AppResources.INSTANCE.pin().getSafeUri().asString();
			}

			GCheckBoxMenuItem cbItem;
			
			if (app.isUnbundledOrWhiteboard()) {
				cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtmlClassic(img, ""),
						loc.getMenu("UnpinFromScreen"),
						loc.getMenu("PinToScreen"),
						new Command() {

							@Override
							public void execute() {
								pinCmd(pinned);
							}
						}, true, app);				
			} else {
				cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtmlClassic(img, loc.getMenu("PinToScreen")),
						new Command() {

							@Override
							public void execute() {
								pinCmd(pinned);
							}
						}, true, app);
			}

			cbItem.setSelected(pinned, wrappedPopup.getPopupMenu());

			wrappedPopup.addItem(cbItem);

		}
	}

	private void addPlaneItems() {
		if (!(getGeo() instanceof ViewCreator)) {
			return;
		}

		final ViewCreator plane = (ViewCreator) getGeo();

		Command action = new Command() {

			@Override
			public void execute() {
				plane.setView2DVisible(true);
				Log.debug("set plane visible : " + plane);
			}
		};
		addAction(action, null, app.getLocalization().getPlain("ShowAas2DView",
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
					action = new Command() {

						@Override
						public void execute() {
							implicitConicEquationCmd();
						}
					};
					addAction(action, null, loc.getMenu("ExpandedForm"));
				} else {
					action = new Command() {

						@Override
						public void execute() {
							inputFormCmd(inputElement);
						}
					};
					addAction(action, null, loc.getMenu("InputForm"));
				}

			}
		} else if (needsInputFormItem(geo)) {
			final EquationValue inputElement = (EquationValue) geo;
			Command action = new Command() {

				@Override
				public void execute() {
					inputFormCmd(inputElement);
				}
			};
			addAction(action, null, loc.getMenu("InputForm"));
		} else if (geo instanceof GeoPlaneND && geo.getDefinition() != null) {
			Command action = new Command() {

				@Override
				public void execute() {
					implicitConicEquationCmd();
				}
			};
			addAction(action, null, loc.getMenu("ExpandedForm"));
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

		if (mode != GeoConicND.EQUATION_IMPLICIT) {
			sb.append(ConicEqnModel.getImplicitEquation(conic, loc, true));
			action = new Command() {

				@Override
				public void execute() {
					implicitConicEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (specificPossible && mode != GeoConicND.EQUATION_SPECIFIC) {
			// specific conic string
			String conicEqn = conic.getSpecificEquation();
			if (conicEqn != null) {
				sb.setLength(0);
				sb.append(loc.getMenu("Equation"));
				sb.append(' ');
				sb.append(conicEqn);
				action = new Command() {

					@Override
					public void execute() {
						equationConicEqnCmd();
					}
				};
				addAction(action, null, sb.toString());
			}
		}

		if (explicitPossible && mode != GeoConicND.EQUATION_EXPLICIT) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ExplicitConicEquation"));
			action = new Command() {

				@Override
				public void execute() {
					equationExplicitConicEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (vertexformPossible && mode != GeoConicND.EQUATION_VERTEX) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ParabolaVertexForm"));
			action = new Command() {

				@Override
				public void execute() {
					equationVertexEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (conicformPossible && mode != GeoConicND.EQUATION_CONICFORM) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ParabolaConicForm"));
			action = new Command() {

				@Override
				public void execute() {
					equationConicformEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
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

		if (mode != GeoLine.EQUATION_IMPLICIT) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ImplicitLineEquation"));
			action = new Command() {

				@Override
				public void execute() {
					equationImplicitEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (mode != GeoLine.EQUATION_EXPLICIT) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ExplicitLineEquation"));
			action = new Command() {

				@Override
				public void execute() {
					equationExplicitEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (mode != GeoLine.PARAMETRIC) {
			action = new Command() {

				@Override
				public void execute() {
					parametricFormCmd();
				}
			};
			addAction(action, null, loc.getMenu("ParametricForm"));
		}

		if (mode != GeoLine.EQUATION_GENERAL) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("GeneralLineEquation"));
			action = new Command() {

				@Override
				public void execute() {
					equationGeneralLineEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
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
			action = new Command() {

				@Override
				public void execute() {
					cartesianCoordsCmd();
				}
			};
			addAction(action, null, loc.getMenu("CartesianCoords"));
			break;

		case Kernel.COORD_CARTESIAN:
			action = new Command() {

				@Override
				public void execute() {
					polarCoorsCmd();
				}
			};
			addAction(action, null, loc.getMenu("PolarCoords"));
			break;

		// 3D coords styles
		case Kernel.COORD_SPHERICAL:
			action = new Command() {

				@Override
				public void execute() {
					cartesianCoords3dCmd();
				}
			};
			addAction(action, null, loc.getMenu("CartesianCoords"));
			break;

		case Kernel.COORD_CARTESIAN_3D:
			action = new Command() {

				@Override
				public void execute() {
					sphericalCoordsCmd();
				}
			};
			addAction(action, null, loc.getMenu("Spherical"));
			break;
		}
	}

	/**
	 * @param action
	 *            action to perform on click
	 * @param html
	 *            html string of menu item
	 * @param text
	 *            text of menu item
	 * @return new menu item
	 */
	protected AriaMenuItem addAction(Command action, String html, String text) {
		AriaMenuItem mi;
		if (html != null) {
			mi = new AriaMenuItem(html, true, action);
			if (!app.isUnbundledOrWhiteboard()) {
				mi.addStyleName("mi_with_image"); // TEMP
			}
		} else {
			mi = new AriaMenuItem(text, false, action);
			mi.addStyleName("mi_no_image"); // TEMP
		}

		wrappedPopup.addItem(mi);
		return mi;
	}

	/**
	 * @param html
	 *            html string of superior menu item
	 * @param text
	 *            name of menu item
	 * @param subMenu
	 *            sub menu
	 */
	protected void addSubmenuAction(String html, String text,
			AriaMenuBar subMenu) {
		AriaMenuItem mi;
		if (html != null) {
			mi = new AriaMenuItem(html, true, subMenu);
			if (!app.isUnbundledOrWhiteboard()) {
				mi.addStyleName("mi_with_image"); // TEMP
			}
		} else {
			mi = new AriaMenuItem(text, true, subMenu);
			mi.addStyleName("mi_no_image"); // TEMP
		}

		wrappedPopup.addItem(mi);
	}

	/**
	 * @param str
	 *            title of menu (first menu item)
	 */
	protected void setTitle(String str) {
		AriaMenuItem title = new AriaMenuItem(MainMenu.getMenuBarHtmlClassic(
				AppResources.INSTANCE.empty().getSafeUri().asString(), str),
				true, new Command() {

					@Override
					public void execute() {
						if (app.isUnbundledOrWhiteboard()) {
							wrappedPopup.setVisible(true);
							wrappedPopup.setMenuShown(false);
						} else {
							wrappedPopup.setVisible(false);
						}
					}
				});
		wrappedPopup.addItem(title);
		if (app.isUnbundledOrWhiteboard()) {
			title.addStyleName("no-hover");
		} else {
			title.addStyleName("menuTitle");
			wrappedPopup.addSeparator();
		}
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
		updateEditItems();
		wrappedPopup.showScaled(c, x, y);
		focusDeferred();
	}

	/**
	 * @param p
	 *            show in p's coord
	 */
	public void show(GPoint p) {
		updateEditItems();
		wrappedPopup.show(p);
		focusDeferred();
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
		// mnu.addStyleName("gwt-PopupPanel");
		// mnu.addStyleName("contextMenuSubmenu");
		GeoElement[] geos = { getGeo() };
		final ReflexAngleModel model = new ReflexAngleModel(app, false);
		model.setGeos(geos);

		for (int i = 0; i < angleIntervals.length; i++) {
			final int idx = i;
			AriaMenuItem mi = new AriaMenuItem(
					MainMenu.getMenuBarHtmlClassic(AppResources.INSTANCE.empty()
							.getSafeUri().asString(), angleIntervals[i]),
					true, new Command() {

						@Override
						public void execute() {
							model.applyChanges(idx);
						}
					});
			mnu.addItem(mi);
		}
		return mnu;
	}

	/**
	 * @return true if menu is shown
	 */
	public boolean isMenuShown() {
		return wrappedPopup.isMenuShown();
	}

	/**
	 * @param menuShown
	 *            true if menu is shown
	 */
	public void setMenuShown(boolean menuShown) {
		wrappedPopup.setMenuShown(menuShown);
	}

	/**
	 * update whole popup
	 */
	public void update() {
		initPopup(app.getActiveEuclidianView()
				.getEuclidianController().getAppSelectedGeos());
		addOtherItems();
	}

	private void focusDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				wrappedPopup.getPopupMenu().moveSelectionDown();
			}
		});
	}
}
