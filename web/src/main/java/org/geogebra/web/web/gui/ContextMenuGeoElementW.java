package org.geogebra.web.web.gui;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.gui.ContextMenuGeoElement;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.ReflexAngleModel;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.geos.Animatable;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.html5.AttachedToDOM;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor
 * 
 *         ContextMenuGeoElement for Web
 *
 */
public class ContextMenuGeoElementW extends ContextMenuGeoElement
		implements AttachedToDOM {

	protected GPopupMenuW wrappedPopup;
	protected Localization loc;
	// private MenuItem mnuCopy;
	private MenuItem mnuCut;
	// private MenuItem mnuDuplicate;
	private MenuItem mnuPaste;
	private MenuItem mnuDelete;

	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 */
	ContextMenuGeoElementW(AppW app) {
		super(app);
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
		initPopup(app, geos);
	}

	public void initPopup(AppW app, ArrayList<GeoElement> geos) {
		if (geos == null || geos.size() == 0) {
			return;
		}
		this.setGeos(geos);
		setGeo(geos.get(0));

		wrappedPopup.clearItems();

		String title;

		if (geos.size() == 1) {
			title = getDescription(getGeo(), false);
		} else {
			title = loc.getMenu("Selection");
		}
		setTitle(title);
		if (isWhiteboard()) {
			wrappedPopup.getPopupPanel().addStyleName("contextMenu");
		}
	}

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

		if (wrappedPopup.getComponentCount() > 2) {
			wrappedPopup.addSeparator();
		}
		addForAllItems();
	}

	protected boolean isWhiteboard() {
		return app.has(Feature.WHITEBOARD_APP) && app.has(Feature.CONTEXT_MENU);
	}

	private void addForAllItems() {
		if (isWhiteboard()) {
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
			GCheckBoxMenuItem cbItem;
			if (!(app.has(Feature.WHITEBOARD_APP)
					&& app.has(Feature.CONTEXT_MENU))
					&& getGeo().isEuclidianShowable()
					&& getGeo().getShowObjectCondition() == null
					&& (!getGeo().isGeoBoolean() || getGeo().isIndependent())) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
						AppResources.INSTANCE.mode_showhideobject_16()
								.getSafeUri().asString(),
						loc.getMenu("ShowObject")), new Command() {

							@Override
							public void execute() {
								showObjectCmd();
							}
						}, true, app);
				cbItem.setSelected(getGeo().isSetEuclidianVisible());
				wrappedPopup.addItem(cbItem);

			}

			if (!isWhiteboard() && getGeo().isLabelShowable()) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
						AppResources.INSTANCE.mode_showhidelabel_16()
								.getSafeUri().asString(),
						loc.getMenu("ShowLabel")), new Command() {

							@Override
							public void execute() {
								showLabelCmd();
							}
						}, true, app);
				cbItem.setSelected(isLabelShown());
				wrappedPopup.addItem(cbItem);
			}

			// trace
			if (getGeo().isTraceable()) {

				String img;
				if (isWhiteboard()) {
					if (!isTracing() || !app.has(Feature.CLEAR_VIEW_STYLEBAR)) {
						img = AppResources.INSTANCE.trace20().getSafeUri()
								.asString();
					} else {
						img = AppResources.INSTANCE.trace_off20().getSafeUri()
								.asString();
					}

				} else {
					img = AppResources.INSTANCE.trace_on().getSafeUri()
							.asString();
				}

				if (app.has(Feature.IMPROVE_CONTEXT_MENU)) {
					cbItem = new GCheckBoxMenuItem(
							MainMenu.getMenuBarHtml(img, "", true),
							loc.getMenu("HideTrace"), loc.getMenu("ShowTrace"),
							new Command() {

								@Override
								public void execute() {
									traceCmd();
								}
							}, true, app);
					cbItem.setSelected(isTracing());
				} else {
					cbItem = new GCheckBoxMenuItem(
							MainMenu.getMenuBarHtml(img,
									loc.getMenu("ShowTrace"), true),
							new Command() {

								@Override
								public void execute() {
									traceCmd();
								}
							}, true, app);
					cbItem.setSelected(((Traceable) getGeo()).getTrace());
				}
				wrappedPopup.addItem(cbItem);
			}

			if (getGeo().isSpreadsheetTraceable()
					&& app.getGuiManager().showView(App.VIEW_SPREADSHEET)) {
				boolean showRecordToSpreadsheet = true;
				// check if other geos are recordable
				for (int i = 1; i < getGeos().size()
						&& showRecordToSpreadsheet; i++) {
					showRecordToSpreadsheet &= getGeos().get(i)
							.isSpreadsheetTraceable();
				}

				if (showRecordToSpreadsheet) {

					String img;
					if (isWhiteboard()) {
						img = AppResources.INSTANCE.record_to_spreadsheet20()
								.getSafeUri().asString();
					} else {
						img = AppResources.INSTANCE.spreadsheettrace()
								.getSafeUri().asString();
					}

					if (app.has(Feature.IMPROVE_CONTEXT_MENU)) {
						cbItem = new GCheckBoxMenuItem(
								MainMenu.getMenuBarHtml(img, ""),
								loc.getMenu("DontRecordToSpreadsheet"),
								loc.getMenu("RecordToSpreadsheet"),
								new Command() {

									@Override
									public void execute() {
										recordToSpreadSheetCmd();
									}
								}, true, app);
					} else {
						cbItem = new GCheckBoxMenuItem(
								MainMenu.getMenuBarHtml(img,
										loc.getMenu("RecordToSpreadsheet")),
								new Command() {

									@Override
									public void execute() {
										recordToSpreadSheetCmd();
									}
								}, true, app);
					}
					cbItem.setSelected(getGeo().getSpreadsheetTrace());
					wrappedPopup.addItem(cbItem);
				}
			}

			if (getGeo().isAnimatable()) {

				String img;
				if (isWhiteboard()) {
					img = AppResources.INSTANCE.animation20().getSafeUri()
							.asString();
				} else {
					img = AppResources.INSTANCE.empty().getSafeUri().asString();
				}

				cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(img, loc.getMenu("Animation")),
						new Command() {

							@Override
							public void execute() {
								animationCmd();
							}
						}, true, app);
				cbItem.setSelected(((Animatable) getGeo()).isAnimating()
						&& app.getKernel().getAnimatonManager().isRunning());
				wrappedPopup.addItem(cbItem);
			}

			if (app.getGuiManager() != null
					&& app.getGuiManager().showView(App.VIEW_ALGEBRA)
					&& app.showAuxiliaryObjects()
					&& getGeo().isAlgebraShowable()) {
				cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(
								AppResources.INSTANCE.aux_folder().getSafeUri()
										.asString(),
								loc.getMenu("AuxiliaryObject")),
						new Command() {

							@Override
							public void execute() {
								showObjectAuxiliaryCmd();
							}
						}, true, app);
				cbItem.setSelected(getGeo().isAuxiliaryObject());
				wrappedPopup.addItem(cbItem);

			}

			if (!isWhiteboard()) {
				// fix object
				if (getGeo().isFixable() && (getGeo().isGeoText()
						|| getGeo().isGeoImage() || getGeo().isGeoButton())) {

					String img;
					if (isWhiteboard()) {
						img = AppResources.INSTANCE.lock20().getSafeUri()
								.asString();
					} else {
						img = AppResources.INSTANCE.objectFixed().getSafeUri()
								.asString();
					}

					if (app.has(Feature.IMPROVE_CONTEXT_MENU)) {
						cbItem = new GCheckBoxMenuItem(
								MainMenu.getMenuBarHtml(img, ""),
								loc.getMenu("UnlockObject"),
								loc.getMenu("LockObject"), new Command() {

									@Override
									public void execute() {
										fixObjectCmd();
									}
								}, true, app);
					} else {
						cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(img,
								loc.getMenu("LockObject")), new Command() {

									@Override
									public void execute() {
										fixObjectCmd();
									}
								}, true, app);						
					}

					cbItem.setSelected(getGeo().isFixed());
					wrappedPopup.addItem(cbItem);
				} else if (getGeo().isGeoNumeric()) {
					final GeoNumeric num = (GeoNumeric) getGeo();
					if (num.isSlider()) {

						String img;
						if (isWhiteboard()) {
							img = AppResources.INSTANCE.lock20().getSafeUri()
									.asString();
						} else {
							img = AppResources.INSTANCE.objectFixed()
									.getSafeUri().asString();
						}

						if (app.has(Feature.IMPROVE_CONTEXT_MENU)) {
							cbItem = new GCheckBoxMenuItem(
									MainMenu.getMenuBarHtml(img, ""),
									loc.getMenu("UnlockObject"),
									loc.getMenu("LockObject"),
									new Command() {

										@Override
										public void execute() {
											fixObjectNumericCmd(num);
										}
									}, true, app);
						} else {
							cbItem = new GCheckBoxMenuItem(
									MainMenu.getMenuBarHtml(img,
											loc.getMenu("LockObject")),
									new Command() {

										@Override
										public void execute() {
											fixObjectNumericCmd(num);
										}
									}, true, app);
						}

						cbItem.setSelected(num.isSliderFixed());
						wrappedPopup.addItem(cbItem);
					}
				} else if (getGeo().isGeoBoolean()) {

					String img;
					if (isWhiteboard()) {
						img = AppResources.INSTANCE.lock20().getSafeUri()
								.asString();
					} else {
						img = AppResources.INSTANCE.objectFixed().getSafeUri()
								.asString();
					}

					cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(img,
							loc.getMenu("FixCheckbox")), new Command() {

								@Override
								public void execute() {
									fixCheckboxCmd();
								}
							}, true, app);
					cbItem.setSelected(
							((GeoBoolean) getGeo()).isCheckboxFixed());
					wrappedPopup.addItem(cbItem);
				}

				// Pinnable
				addPin();
			}

			wrappedPopup.addSeparator();
		}

		if (!isWhiteboard()) {
			addRename();
		}

		// DELETE
		if (app.letDelete() && !getGeo().isFixed() && !isWhiteboard()) {

			String img;
			if (isWhiteboard()) {
				img = AppResources.INSTANCE.delete20().getSafeUri().asString();
			} else {
				img = AppResources.INSTANCE.delete_small().getSafeUri()
						.asString();
			}

			addAction(new Command() {

				@Override
				public void execute() {
					deleteCmd(false);
				}
			}, MainMenu.getMenuBarHtml(img, loc.getMenu("Delete")),
					loc.getMenu("Delete"));
		}

		// if (isWhiteboard()) {
		// wrappedPopup.addSeparator();
		// addSelect();
		// addOrder();
		// }
		// Object properties menuitem
		if (app.showMenuBar() && app.letShowPropertiesDialog()
				&& getGeo().hasProperties()) {
			if (!isWhiteboard()) {
				wrappedPopup.addSeparator();
			}

			String img;
			if (isWhiteboard()) {
				img = AppResources.INSTANCE.properties20().getSafeUri()
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
			}, MainMenu.getMenuBarHtml(img, loc.getMenu("Properties")),
					loc.getMenu("Properties"));
		}

	}

	private void addRename() {
		if (!(getGeos().size() == 1 && app.letRename()
				&& getGeo().isRenameable())) {
			return;
		}

		String img;
		if (isWhiteboard()) {
			img = AppResources.INSTANCE.rename20().getSafeUri().asString();
		} else {
			img = AppResources.INSTANCE.rename().getSafeUri().asString();
		}

		addAction(new Command() {

			@Override
			public void execute() {
				renameCmd();
			}
		}, MainMenu.getMenuBarHtml(img, loc.getMenu("Rename")),
				loc.getMenu("Rename"));

		if (getGeos().size() == 1 && getGeo() instanceof TextValue
				&& !getGeo().isTextCommand() && !getGeo().isFixed()) {

			String img2;
			if (isWhiteboard()) {
				img2 = AppResources.INSTANCE.edit20().getSafeUri().asString();
			} else {
				img2 = AppResources.INSTANCE.edit().getSafeUri().asString();
			}

			addAction(new Command() {

				@Override
				public void execute() {
					editCmd();
				}
			}, MainMenu.getMenuBarHtml(img2, loc.getMenu("Edit")),
					loc.getMenu("Edit"));
		}

	}

	private void addObjectPropertiesMenu() {
		if (!isWhiteboard()) {
			return;
		}

		GeoElement geo = getGeo();

		boolean showLabel = ShowLabelModel.match(geo);
		boolean angle = AngleArcSizeModel.match(geo);

		if (!(showLabel || angle)) {
			return;
		}

		wrappedPopup.addSeparator();

		// Label
		if (showLabel) {

			String img;
			if (isWhiteboard()) {
				img = AppResources.INSTANCE.label20().getSafeUri().asString();
			} else {
				img = AppResources.INSTANCE.mode_showhidelabel_16().getSafeUri()
						.asString();
			}
			addSubmenuAction(MainMenu.getMenuBarHtml(img, loc.getMenu("Label")),
					loc.getMenu("Label"), getLabelSubMenu());
		}

		// Angle
		if (angle) {

			String img;
			if (isWhiteboard()) {
				img = AppResources.INSTANCE.angle20().getSafeUri().asString();
			} else {
				img = AppResources.INSTANCE.stylingbar_angle_interval()
						.getSafeUri().asString();
			}
			addSubmenuAction(MainMenu.getMenuBarHtml(img, loc.getMenu("Angle")),
					loc.getMenu("Angle"), getAngleSubMenu());

		}
		// wrappedPopup.addSeparator();

	}

	private void addPinAndFixObject() {
		if (!isWhiteboard()) {
			return;
		}

		final GeoElement geo = getGeo();
		boolean pinnable = geo.isPinnable();
		boolean fixable = geo.isFixable();
		if (!(pinnable || fixable)) {
			Log.debug("NEMFIXABLE!");
			return;
		}

		wrappedPopup.addSeparator();

		if (pinnable) {

			String img;
			final boolean pinned = geo.isPinned();

			if (isWhiteboard()) {
				if (!app.has(Feature.IMPROVE_CONTEXT_MENU) || !pinned) {
					img = AppResources.INSTANCE.pin20().getSafeUri().asString();
				} else {
					img = AppResources.INSTANCE.unpin20().getSafeUri()
							.asString();
				}
			} else {
				img = AppResources.INSTANCE.pin().getSafeUri().asString();
			}

			if (app.has(Feature.IMPROVE_CONTEXT_MENU)) {
				GCheckBoxMenuItem cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(img, ""),
						loc.getMenu("UnpinFromScreen"),
						loc.getMenu("PinToScreen"), new Command() {

							@Override
							public void execute() {
								pinCmd(pinned);
							}
						}, true, app);
				cbItem.setSelected(pinned);
				wrappedPopup.addItem(cbItem);
			} else {
				addAction(new Command() {

					@Override
					public void execute() {
						pinCmd(pinned);
					}
				}, MainMenu.getMenuBarHtml(img, loc.getMenu("PinToScreen")),
						loc.getMenu("PinToScreen"));
			}
		}

		Command cmd = null;
		String label = loc.getMenu("LockObject");
		if (fixable) {
			cmd = new Command() {

				public void execute() {
					ArrayList<GeoElement> geoArray = new ArrayList<GeoElement>();
					geoArray.add(geo);
					if (app.has(Feature.IMPROVE_CONTEXT_MENU)) {
						boolean dsVisible = app.getActiveEuclidianView()
								.getDynamicStyleBar().isVisible();
						EuclidianStyleBarStatic.applyFixObject(geoArray,
								!geo.isFixed(), app.getActiveEuclidianView());
						app.getActiveEuclidianView().getDynamicStyleBar()
								.setVisible(dsVisible);
					} else {
						EuclidianStyleBarStatic.applyFixObject(geoArray,
								!geo.isFixed(), app.getActiveEuclidianView());
					}

				}
			};
		}

		if (cmd != null) {

			String img;
			if (isWhiteboard()) {
				if (geo.isFixed()) {
					img = AppResources.INSTANCE.lock20().getSafeUri()
							.asString();
				} else {
					img = AppResources.INSTANCE.unlock20().getSafeUri()
							.asString();

				}
			} else {
				img = AppResources.INSTANCE.objectFixed().getSafeUri()
						.asString();
			}

			if (app.has(Feature.IMPROVE_CONTEXT_MENU)) {
				GCheckBoxMenuItem mi = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(img, ""),
						loc.getMenu("UnlockObject"), loc.getMenu("LockObject"),
						cmd, true, app);
				mi.setSelected(getGeo().isFixed());
				wrappedPopup.addItem(mi);
			} else {
				addAction(cmd, MainMenu.getMenuBarHtml(img, label), label);
			}
		}

		// wrappedPopup.addSeparator();

	}

	private void addEditItems() {
		if (!isWhiteboard()) {
			return;
		}

		wrappedPopup.addSeparator();

		final SelectionManager selection = app.getSelectionManager();

		String img;
		if (isWhiteboard()) {
			img = AppResources.INSTANCE.cut20().getSafeUri().asString();
		} else {
			img = AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		mnuCut = addAction(new Command() {

			public void execute() {
				app.setWaitCursor();
				cutCmd();
				app.setDefaultCursor();
			}
		}, MainMenu.getMenuBarHtml(img, loc.getMenu("Cut"), true),
				loc.getMenu("Cut"));

		String img2;
		if (isWhiteboard()) {
			img2 = AppResources.INSTANCE.copy20().getSafeUri().asString();
		} else {
			img2 = GuiResources.INSTANCE.menu_icon_edit_copy().getSafeUri()
					.asString();
		}

		addAction(new Command() {

			public void execute() {
				if (!selection.getSelectedGeos().isEmpty()) {
					app.setWaitCursor();
					app.getCopyPaste().copyToXML(app,
							selection.getSelectedGeos(), false);
					// initActions(); // app.updateMenubar(); - it's needn't to
					// // update the all menubar here
					app.setDefaultCursor();
				}
			}
		}, MainMenu.getMenuBarHtml(img2, loc.getMenu("Copy"), true),
				loc.getMenu("Copy"));

		String img3;
		if (isWhiteboard()) {
			img3 = AppResources.INSTANCE.duplicate20().getSafeUri().asString();
		} else {
			img3 = AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		addAction(new Command() {

			public void execute() {
				app.setWaitCursor();
				duplicateCmd();
				app.setDefaultCursor();

			}
		}, MainMenu.getMenuBarHtml(img3, loc.getMenu("Duplicate"), true),
				loc.getMenu("Duplicate"));

		addPasteItem();

		String img4;
		if (isWhiteboard()) {
			img4 = AppResources.INSTANCE.delete20().getSafeUri().asString();
		} else {
			img4 = AppResources.INSTANCE.delete_small().getSafeUri().asString();
		}

		mnuDelete = addAction(new Command() {

			public void execute() {
				deleteCmd(false);
			}
		}, MainMenu.getMenuBarHtml(img4, loc.getMenu("Delete"), true),
				loc.getMenu("Delete"));

		updateEditItems();
	}

	protected void addPasteItem() {

		String img;
		if (isWhiteboard()) {
			img = AppResources.INSTANCE.paste20().getSafeUri().asString();
		} else {
			img = GuiResources.INSTANCE.menu_icon_edit_paste().getSafeUri()
					.asString();
		}

		mnuPaste = addAction(new Command() {

			public void execute() {
				if (!app.getCopyPaste().isEmpty()) {
					app.setWaitCursor();
					app.getCopyPaste().pasteFromXML(app, false);
					app.setDefaultCursor();
				}
			}
		}, MainMenu.getMenuBarHtml(img, loc.getMenu("Paste"), true),
				loc.getMenu("Paste"));
	}

	protected void updatePasteItem() {
		mnuPaste.setEnabled(!app.getCopyPaste().isEmpty());
	}

	protected void updateEditItems() {
		if (!isWhiteboard()) {
			return;
		}

		boolean canDelete = app.letDelete() && !getGeo().isFixed();
		mnuCut.setEnabled(canDelete);
		updatePasteItem();
		mnuDelete.setEnabled(canDelete);
	}

	private void addPin() {
		if (getGeo().isPinnable()) {

			String img;
			final boolean pinned = getGeo().isPinned();

			if (isWhiteboard()) {
				if (!app.has(Feature.IMPROVE_CONTEXT_MENU) || !pinned) {
					img = AppResources.INSTANCE.pin20().getSafeUri().asString();
				} else {
					img = AppResources.INSTANCE.unpin20().getSafeUri()
							.asString();
				}
			} else {
				img = AppResources.INSTANCE.pin().getSafeUri().asString();
			}

			GCheckBoxMenuItem cbItem;
			
			if(app.has(Feature.IMPROVE_CONTEXT_MENU)){
				cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(img, ""),
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
						MainMenu.getMenuBarHtml(img, loc.getMenu("PinToScreen")),
						new Command() {

							@Override
							public void execute() {
								pinCmd(pinned);
							}
						}, true, app);
			}

			cbItem.setSelected(pinned);
			//
			// final MenuItem cbItem = new MenuItem(MainMenu.getMenuBarHtml(
			// AppResources.INSTANCE.pin().getSafeUri().asString(),
			// loc.getMenu("AbsoluteScreenLocation")), true,
			// new Command() {
			//
			// public void execute() {
			// // must set emtpy because "not initialized..." error
			// }
			// });
			// cbItem.setScheduledCommand(new Command() {
			//
			// public void execute() {
			// boolean isSelected = (cbItem.getStyleName().indexOf(
			// "checked") > -1);
			// pinCmd(isSelected);
			// }
			// });
			// MainMenu.setMenuSelected(cbItem, getGeo().isPinned());
			wrappedPopup.addItem(cbItem);
			// cbItem.addStyleName("mi_with_image");
		}
	}

	private void addPlaneItems() {

		if (!(getGeo() instanceof ViewCreator)) {
			return;
		}

		Log.debug("==================== addPlaneItems");

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
		if (getGeo() instanceof GeoImplicit) {
			final GeoImplicit inputElement = (GeoImplicit) getGeo();
			if (inputElement.isValidInputForm()) {
				Command action;
				if (inputElement.isInputForm()) {
					action = new Command() {

						@Override
						public void execute() {
							extendedFormCmd(inputElement);
						}
					};
					addAction(action, null, loc.getMenu("ExtendedForm"));
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
		}

	}

	private void addNumberItems() {
		// no items
	}

	private void addConicItems() {
		if (getGeo().getClass() != GeoConic.class) {
			return;
		}
		GeoConic conic = (GeoConic) getGeo();
		// there's no need to show implicit equation
		// if you can't select the specific equation
		boolean specificPossible = conic.isSpecificPossible();
		boolean explicitPossible = conic.isExplicitPossible();
		boolean vertexformPossible = conic.isVertexformPossible();
		boolean conicformPossible = conic.isConicformPossible();
		if (!(specificPossible || explicitPossible)) {
			return;
		}

		int mode = conic.getToStringMode();
		Command action;
		StringBuilder sb = new StringBuilder();

		if (mode != GeoConicND.EQUATION_IMPLICIT) {
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ImplicitConicEquation"));
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
		if (!(getGeo() instanceof GeoLine)) {
			return;
		}
		if (getGeo() instanceof GeoSegment) {
			return;
		}

		GeoLine line = (GeoLine) getGeo();
		int mode = line.getMode();
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

		if (!(getGeo() instanceof CoordStyle)) {
			return;
		}

		if (getGeo().isFixed()) {
			return;
		}

		CoordStyle point = (CoordStyle) getGeo();
		int mode = point.getMode();
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

	protected MenuItem addAction(Command action, String html, String text) {
		MenuItem mi;
		if (html != null) {
			mi = new MenuItem(html, true, action);
			if (!isWhiteboard()) {
				mi.addStyleName("mi_with_image"); // TEMP
			}
		} else {
			mi = new MenuItem(text, action);
			mi.addStyleName("mi_no_image"); // TEMP
		}

		wrappedPopup.addItem(mi);
		return mi; // TODO: need we this?
		// return wrappedPopup.add(action, html, text);
	}

	protected void addSubmenuAction(String html, String text, MenuBar subMenu) {
		MenuItem mi;
		if (html != null) {
			mi = new MenuItem(html, true, subMenu);
			if (!isWhiteboard()) {
				mi.addStyleName("mi_with_image"); // TEMP
			}
		} else {
			mi = new MenuItem(text, true, subMenu);
			mi.addStyleName("mi_no_image"); // TEMP
		}

		wrappedPopup.addItem(mi);
		// return mi; // TODO: need we this?
		// return wrappedPopup.add(action, html, text);
	}

	protected void setTitle(String str) {
		MenuItem title = new MenuItem(MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty().getSafeUri().asString(), str),
				true, new Command() {

					@Override
					public void execute() {
						wrappedPopup.setVisible(false);
					}
				});
		title.addStyleName("menuTitle");
		wrappedPopup.addItem(title);
	}

	public GPopupMenuW getWrappedPopup() {
		return wrappedPopup;
	}

	public void show(Canvas c, int x, int y) {
		updateEditItems();
		wrappedPopup.show(c, x, y);
	}

	public void show(GPoint p) {
		updateEditItems();
		wrappedPopup.show(p);
	}

	// public void reInit(ArrayList<GeoElement> geos, GPoint location) {
	// initPopup((AppW) this.app, geos);
	// addOtherItems();
	// }

	@Override
	public void removeFromDOM() {
		getWrappedPopup().removeFromDOM();
	}

	private MenuBar getLabelSubMenu() {

		String[] labels = { loc.getMenu("stylebar.Hidden"), loc.getMenu("Name"),
				loc.getMenu("NameAndValue"), loc.getMenu("Value"),
				loc.getMenu("Caption") };

		MenuBar mnu = new MenuBar(true);
		// mnu.addStyleName("gwt-PopupPanel");
		// mnu.addStyleName("contextMenuSubmenu");
		GeoElement geos[] = { getGeo() };
		final ShowLabelModel model = new ShowLabelModel(app, null);
		model.setGeos(geos);
		for (int i = 0; i < labels.length; i++) {
			final int idx = i;
			MenuItem mi = new MenuItem(
					MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
							.getSafeUri().asString(), labels[i]),
					true, new Command() {

						@Override
						public void execute() {
							if (idx == 0) {
								model.applyModeChanges(4, false);

							} else {
								model.applyModeChanges(idx - 1, true);
							}

						}
					});
			mnu.addItem(mi);
		}

		return mnu;
	}

	private MenuBar getAngleSubMenu() {
		String[] angleIntervals = new String[GeoAngle.getIntervalMinListLength()
				- 1];
		for (int i = 0; i < GeoAngle.getIntervalMinListLength() - 1; i++) {
			angleIntervals[i] = app.getLocalization().getPlain(
					"AngleBetweenAB.short", GeoAngle.getIntervalMinList(i),
					GeoAngle.getIntervalMaxList(i));
		}

		MenuBar mnu = new MenuBar(true);
		// mnu.addStyleName("gwt-PopupPanel");
		// mnu.addStyleName("contextMenuSubmenu");
		GeoElement geos[] = { getGeo() };
		final ReflexAngleModel model = new ReflexAngleModel(app, false);
		model.setGeos(geos);

		for (int i = 0; i < angleIntervals.length; i++) {
			final int idx = i;
			MenuItem mi = new MenuItem(
					MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
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

	public void update() {
		initPopup((AppW) app, app.getActiveEuclidianView()
				.getEuclidianController().getAppSelectedGeos());
		addOtherItems();
	}
}
