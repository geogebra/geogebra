package org.geogebra.web.web.gui;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.ContextMenuGeoElement;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.geos.Animatable;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.HasCoordsMode;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.html5.AttachedToDOM;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor
 * 
 *         ContextMenuGeoElement for Web
 *
 */
public class ContextMenuGeoElementW extends ContextMenuGeoElement implements
        AttachedToDOM {

	protected GPopupMenuW wrappedPopup;

	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 */
	ContextMenuGeoElementW(AppW app) {

		this.app = app;
		wrappedPopup = new GPopupMenuW(app);

	}

	/**
	 * Creates new MyPopupMenu for GeoElement
	 * 
	 * @param app
	 *            application
	 * @param geos
	 *            selected elements
	 * @param location
	 *            screen position
	 */
	public ContextMenuGeoElementW(AppW app, ArrayList<GeoElement> geos,
	        GPoint location) {
		this(app);
		initPopup(app, geos);
	}

	public void initPopup(AppW app, ArrayList<GeoElement> geos) {
		this.geos = geos;
		setGeo(geos.get(0));

		wrappedPopup.clearItems();

		String title;

		if (geos.size() == 1) {
			title = getDescription(getGeo(), false);
		} else {
			title = app.getPlain("Selection");
		}
		setTitle(title);
	}

	public void addOtherItems() {
		if (app.getGuiManager() != null
		        && app.getGuiManager().showView(App.VIEW_ALGEBRA)) {
			addCoordsModeItems();
			addLineItems();
			addConicItems();
			addNumberItems();
			addUserInputItem();

		}

		// TODO remove the condition when ggb version >= 5
		if (app.getKernel().getManager3D() != null)
			addPlaneItems();

		if (wrappedPopup.getComponentCount() > 2)
			wrappedPopup.addSeparator();
		addForAllItems();
	}

	private void addForAllItems() {
		// SHOW, HIDE

		// G.Sturr 2010-5-14: allow menu to show spreadsheet trace for
		// non-drawables
		if (getGeo().isDrawable()
		        || (getGeo().isSpreadsheetTraceable() && app.getGuiManager() != null && app
		                .getGuiManager().showView(App.VIEW_SPREADSHEET))) {
			GCheckBoxMenuItem cbItem;
			if (getGeo().isEuclidianShowable()
			        && getGeo().getShowObjectCondition() == null
			        && (!getGeo().isGeoBoolean() || getGeo().isIndependent())) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
				        AppResources.INSTANCE.mode_showhideobject_16()
				                .getSafeUri().asString(),
				        app.getPlain("ShowObject")), new Command() {

					public void execute() {
						showObjectCmd();
					}
				}, true);
				cbItem.setSelected(getGeo().isSetEuclidianVisible());
				wrappedPopup.addItem(cbItem);

			}

			if (getGeo().isLabelShowable()) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
				        AppResources.INSTANCE.mode_showhidelabel_16()
				                .getSafeUri().asString(),
				        app.getPlain("ShowLabel")), new Command() {

					public void execute() {
						showLabelCmd();
					}
				}, true);
				cbItem.setSelected(isLabelShown());
				wrappedPopup.addItem(cbItem);
			}

			// trace
			if (getGeo().isTraceable()) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
				        AppResources.INSTANCE.trace_on().getSafeUri()
				                .asString(), app.getPlain("TraceOn")),
				        new Command() {

					        public void execute() {
						        traceCmd();
					        }
						}, true);
				cbItem.setSelected(((Traceable) getGeo()).getTrace());
				wrappedPopup.addItem(cbItem);
			}

			if (getGeo().isSpreadsheetTraceable()
			        && app.getGuiManager().showView(App.VIEW_SPREADSHEET)) {
				boolean showRecordToSpreadsheet = true;
				// check if other geos are recordable
				for (int i = 1; i < geos.size() && showRecordToSpreadsheet; i++)
					showRecordToSpreadsheet &= geos.get(i)
					        .isSpreadsheetTraceable();

				if (showRecordToSpreadsheet) {
					cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
					        AppResources.INSTANCE.spreadsheettrace()
					                .getSafeUri().asString(),
					        app.getMenu("RecordToSpreadsheet")), new Command() {

						public void execute() {
							recordToSpreadSheetCmd();
									// Log.debug("not ported yet
									// recordToSpreadSheetCmd();");
						}
					}, true);
					cbItem.setSelected(getGeo().getSpreadsheetTrace());
					wrappedPopup.addItem(cbItem);
				}
			}

			if (getGeo().isAnimatable()) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
				        AppResources.INSTANCE.empty().getSafeUri().asString(),
				        app.getPlain("Animating")), new Command() {

					public void execute() {
						animationCmd();
					}
				}, true);
				cbItem.setSelected(((Animatable) getGeo()).isAnimating()
				        && app.getKernel().getAnimatonManager().isRunning());
				wrappedPopup.addItem(cbItem);
			}

			if (app.getGuiManager() != null
			        && app.getGuiManager().showView(App.VIEW_ALGEBRA)
			        && app.showAuxiliaryObjects() && getGeo().isAlgebraShowable()) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
				        AppResources.INSTANCE.aux_folder().getSafeUri()
				                .asString(), app.getPlain("AuxiliaryObject")),
				        new Command() {

					        public void execute() {
						        showObjectAuxiliaryCmd();
					        }
						}, true);
				cbItem.setSelected(getGeo().isAuxiliaryObject());
				wrappedPopup.addItem(cbItem);

			}

			// fix object
			if (getGeo().isFixable()
			        && (getGeo().isGeoText() || getGeo().isGeoImage() || getGeo()
			                .isGeoButton())) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
AppResources.INSTANCE.objectFixed().getSafeUri().asString(),
				        app.getPlain("FixObject")), new Command() {

					public void execute() {
						fixObjectCmd();
					}
				}, true);
				cbItem.setSelected(getGeo().isFixed());
				wrappedPopup.addItem(cbItem);
			} else if (getGeo().isGeoNumeric()) {
				final GeoNumeric num = (GeoNumeric) getGeo();
				if (num.isSlider()) {
					cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
							AppResources.INSTANCE.objectFixed().getSafeUri()
					                .asString(), app.getPlain("FixObject")),
					        new Command() {

						        public void execute() {
							        fixObjectNumericCmd(num);
						        }
							}, true);
					cbItem.setSelected(num.isSliderFixed());
					wrappedPopup.addItem(cbItem);
				}
			} else if (getGeo().isGeoBoolean()) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
						AppResources.INSTANCE.objectFixed().getSafeUri()
								.asString(),
				        app.getPlain("FixCheckbox")), new Command() {

					public void execute() {
						fixCheckboxCmd();
					}
				}, true);
				cbItem.setSelected(((GeoBoolean) getGeo()).isCheckboxFixed());
				wrappedPopup.addItem(cbItem);
			}

			// Pinnable
			addPin();

			wrappedPopup.addSeparator();
		}

		// Rename
		if (geos.size() == 1 && app.letRename() && getGeo().isRenameable()) {
			addAction(
			        new Command() {

				        public void execute() {
					        renameCmd();
				        }
			        },
			        MainMenu.getMenuBarHtml(AppResources.INSTANCE.rename()
			                .getSafeUri().asString(), app.getPlain("Rename")),
			        app.getPlain("Rename"));
		}

		if (geos.size() == 1 && getGeo() instanceof TextValue
		        && !getGeo().isTextCommand() && !getGeo().isFixed()) {
			addAction(
			        new Command() {

				        public void execute() {
					        editCmd();
				        }
			        },
			        MainMenu.getMenuBarHtml(AppResources.INSTANCE.edit()
			                .getSafeUri().asString(), app.getPlain("Edit")),
			        app.getPlain("Edit"));
		}

		// DELETE
		if (app.letDelete() && !getGeo().isFixed()) {
			addAction(
			        new Command() {

				        public void execute() {
					        deleteCmd();
				        }
			        },
			        MainMenu.getMenuBarHtml(AppResources.INSTANCE
			                .delete_small().getSafeUri().asString(),
			                app.getPlain("Delete")), app.getPlain("Delete"));
		}

		// Object properties menuitem
		if (app.showMenuBar() && app.letShowPropertiesDialog()
		        && getGeo().hasProperties()) {
			wrappedPopup.addSeparator();

			// open properties dialog
			addAction(
			        new Command() {

				        public void execute() {
					        openPropertiesDialogCmd();
				        }
			        },
			        MainMenu.getMenuBarHtml(AppResources.INSTANCE
			                .view_properties16().getSafeUri().asString(),
			                app.getPlain("Properties")),
			        app.getPlain("Properties"));
		}

	}

	private void addPin() {
		if (getGeo().isPinnable()) {
			final MenuItem cbItem = new MenuItem(MainMenu.getMenuBarHtml(
			        AppResources.INSTANCE.pin().getSafeUri().asString(),
			        app.getPlain("AbsoluteScreenLocation")), true,
			        new Command() {

				        public void execute() {
					        // must set emtpy because "not initialized..." error
				        }
			        });
			cbItem.setScheduledCommand(new Command() {

				public void execute() {
					boolean isSelected = (cbItem.getStyleName().indexOf(
					        "checked") > -1);
					pinCmd(isSelected);
				}
			});
			MainMenu.setMenuSelected(cbItem, getGeo().isPinned());
			addItem(cbItem);
			cbItem.addStyleName("mi_with_image");
		}
	}

	private void addItem(MenuItem item) {
		wrappedPopup.addItem(item);
	}

	private void addPlaneItems() {

		if (!(getGeo() instanceof ViewCreator))
			return;

		Log.debug("==================== addPlaneItems");

		final ViewCreator plane = (ViewCreator) getGeo();

		Command action = new Command() {

			public void execute() {
				plane.setView2DVisible(true);
				Log.debug("set plane visible : " + plane);
			}
		};
		addAction(
		        action,
		        null,
		        app.getLocalization().getPlain("ShowAas2DView",
		                getGeo().getLabelSimple()));

	}

	private void addUserInputItem() {
		if (getGeo() instanceof GeoImplicit) {
			final GeoImplicit inputElement = (GeoImplicit) getGeo();
			if (inputElement.isValidInputForm()) {
				Command action;
				if (inputElement.isInputForm()) {
					action = new Command() {

						public void execute() {
							extendedFormCmd(inputElement);
						}
					};
					addAction(action, null, app.getPlain("ExtendedForm"));
				} else {
					action = new Command() {

						public void execute() {
							inputFormCmd(inputElement);
						}
					};
					addAction(action, null, app.getPlain("InputForm"));
				}

			}
		}

	}

	private void addNumberItems() {
		// no items
	}

	private void addConicItems() {
		if (getGeo().getClass() != GeoConic.class)
			return;
		GeoConic conic = (GeoConic) getGeo();
		// there's no need to show implicit equation
		// if you can't select the specific equation
		boolean specificPossible = conic.isSpecificPossible();
		boolean explicitPossible = conic.isExplicitPossible();
		if (!(specificPossible || explicitPossible))
			return;

		int mode = conic.getToStringMode();
		Command action;
		StringBuilder sb = new StringBuilder();

		if (mode != GeoConicND.EQUATION_IMPLICIT) {
			sb.append(app.getPlain("Equation"));
			sb.append(' ');
			sb.append(app.getPlain("ImplicitConicEquation"));
			action = new Command() {

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
				sb.append(app.getPlain("Equation"));
				sb.append(' ');
				sb.append(conicEqn);
				action = new Command() {

					public void execute() {
						equationConicEqnCmd();
					}
				};
				addAction(action, null, sb.toString());
			}
		}

		if (explicitPossible && mode != GeoConicND.EQUATION_EXPLICIT) {
			sb.setLength(0);
			sb.append(app.getPlain("Equation"));
			sb.append(' ');
			sb.append(app.getPlain("ExplicitConicEquation"));
			action = new Command() {

				public void execute() {
					equationExplicitConicEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

	}



	private void addLineItems() {
		if (!(getGeo() instanceof GeoLine))
			return;
		if (getGeo() instanceof GeoSegment)
			return;

		GeoLine line = (GeoLine) getGeo();
		int mode = line.getMode();
		Command action;

		StringBuilder sb = new StringBuilder();

		if (mode != GeoLine.EQUATION_IMPLICIT) {
			sb.setLength(0);
			sb.append(app.getPlain("Equation"));
			sb.append(' ');
			sb.append(app.getPlain("ImplicitLineEquation"));
			action = new Command() {

				public void execute() {
					equationImplicitEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (mode != GeoLine.EQUATION_EXPLICIT) {
			sb.setLength(0);
			sb.append(app.getPlain("Equation"));
			sb.append(' ');
			sb.append(app.getPlain("ExplicitLineEquation"));
			action = new Command() {

				public void execute() {
					equationExplicitEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (mode != GeoLine.PARAMETRIC) {
			action = new Command() {

				public void execute() {
					parametricFormCmd();
				}
			};
			addAction(action, null, app.getPlain("ParametricForm"));
		}

	}

	private void addCoordsModeItems() {

		if (!(getGeo() instanceof HasCoordsMode))
			return;

		if (getGeo().isFixed()) {
			return;
		}

		HasCoordsMode point = (HasCoordsMode) getGeo();
		int mode = point.getMode();
		Command action;

		switch (mode) {
		case Kernel.COORD_COMPLEX:
		default:
			return;

			// 2D coords styles
		case Kernel.COORD_POLAR:
			action = new Command() {

				public void execute() {
					cartesianCoordsCmd();
				}
			};
			addAction(action, null, app.getPlain("CartesianCoords"));
			break;

		case Kernel.COORD_CARTESIAN:
			action = new Command() {

				public void execute() {
					polarCoorsCmd();
				}
			};
			addAction(action, null, app.getPlain("PolarCoords"));
			break;

		// 3D coords styles
		case Kernel.COORD_SPHERICAL:
			action = new Command() {

				public void execute() {
					cartesianCoords3dCmd();
				}
			};
			addAction(action, null, app.getPlain("CartesianCoords"));
			break;

		case Kernel.COORD_CARTESIAN_3D:
			action = new Command() {

				public void execute() {
					sphericalCoordsCmd();
				}
			};
			addAction(action, null, app.getPlain("Spherical"));
			break;
		}


	}

	protected MenuItem addAction(Command action, String html, String text) {
		MenuItem mi;
		if (html != null) {
			mi = new MenuItem(html, true, action);
			mi.addStyleName("mi_with_image"); // TEMP
		} else {
			mi = new MenuItem(text, action);
			mi.addStyleName("mi_no_image"); // TEMP
		}

		wrappedPopup.addItem(mi);
		return mi; // TODO: need we this?
		// return wrappedPopup.add(action, html, text);
	}

	protected void setTitle(String str) {
		MenuItem title = new MenuItem(MainMenu.getMenuBarHtml(
		        AppResources.INSTANCE.empty().getSafeUri().asString(), str),
		        true, new Command() {

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

		wrappedPopup.show(c, x, y);
	}

	public void show(GPoint p) {
		wrappedPopup.show(p);
	}

	// public void reInit(ArrayList<GeoElement> geos, GPoint location) {
	// initPopup((AppW) this.app, geos);
	// addOtherItems();
	// }

	public void removeFromDOM() {
		getWrappedPopup().removeFromDOM();
	}

}
