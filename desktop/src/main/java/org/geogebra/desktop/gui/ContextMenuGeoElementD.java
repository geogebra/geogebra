/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.geogebra.common.gui.ContextMenuGeoElement;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.geos.Animatable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.TraceModesEnum;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EventType;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Context menu for GeoElement objects.
 * 
 * @author Markus Hohenwarter
 */
public class ContextMenuGeoElementD extends ContextMenuGeoElement {

	/** background color */
	protected final static Color bgColor = Color.white;
	/** foreground color */
	protected final static Color fgColor = Color.black;
	/** the actual menu */
	protected JPopupMenu wrappedPopup;
	/** localization */
	protected final Localization loc;

	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 */
	ContextMenuGeoElementD(AppD app) {
		super(app);
		this.loc = app.getLocalization();
		this.wrappedPopup = new JPopupMenu();
		wrappedPopup.setBackground(bgColor);
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
	public ContextMenuGeoElementD(AppD app, ArrayList<GeoElement> geos,
			Point location) {
		this(app);
		this.setGeos(geos);
		setGeo(geos.get(0));

		String title;

		if (geos.size() == 1) {
			title = getDescription(getGeo(), true);
		} else {
			title = loc.getMenu("Selection");
		}
		setTitle(title);

		if (app.getGuiManager().showView(App.VIEW_ALGEBRA)) {
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

		app.setComponentOrientation(wrappedPopup);

	}

	private void addCoordsModeItems() {
		if (!(getGeo() instanceof CoordStyle) || getGeo() instanceof GeoLine) {
			return;
		}

		if (getGeo().isProtected(EventType.UPDATE)) {
			return;
		}

		CoordStyle point = (CoordStyle) getGeo();
		int mode = point.getToStringMode();
		AbstractAction action;

		switch (mode) {
		case Kernel.COORD_COMPLEX:
		default:
			return;

		// 2D coords styles
		case Kernel.COORD_POLAR:
			action = new AbstractAction(loc.getMenu("CartesianCoords")) {
				/**
						 * 
						 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					cartesianCoordsCmd();
				}
			};
			addAction(action);
			break;

		case Kernel.COORD_CARTESIAN:
			action = new AbstractAction(loc.getMenu("PolarCoords")) {
				/**
						 * 
						 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					polarCoorsCmd();
				}
			};
			addAction(action);
			break;

		// 3D coords styles
		case Kernel.COORD_SPHERICAL:
			action = new AbstractAction(loc.getMenu("CartesianCoords")) {
				/**
						 * 
						 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					cartesianCoords3dCmd();
				}
			};
			addAction(action);
			break;

		case Kernel.COORD_CARTESIAN_3D:
			action = new AbstractAction(loc.getMenu("Spherical")) {
				/**
						 * 
						 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					sphericalCoordsCmd();
				}
			};
			addAction(action);
			break;
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
		int mode = line.getToStringMode();
		AbstractAction action;
		StringBuilder sb = new StringBuilder();

		if (mode != GeoLine.EQUATION_IMPLICIT) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ImplicitLineEquation"));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					equationImplicitEquationCmd();
				}
			};
			addAction(action);
		}

		if (mode != GeoLine.EQUATION_EXPLICIT) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ExplicitLineEquation"));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					equationExplicitEquationCmd();
				}
			};
			addAction(action);
		}

		if (mode != GeoLine.PARAMETRIC) {
			action = new AbstractAction(loc.getMenu("ParametricForm")) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					parametricFormCmd();
				}
			};
			addAction(action);
		}

		if (mode != GeoLine.EQUATION_GENERAL) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("GeneralLineEquation"));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					equationGeneralLineEquationCmd();
				}
			};
			addAction(action);
		}

	}

	private void addConicItems() {
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
		AbstractAction action;
		StringBuilder sb = new StringBuilder();

		if (mode != GeoConicND.EQUATION_IMPLICIT) {
			sb.append(ConicEqnModel.getImplicitEquation(conic, loc, true));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					implicitConicEquationCmd();
				}
			};
			addAction(action);
		}

		if (specificPossible && mode != GeoConicND.EQUATION_SPECIFIC) {
			// specific conic string
			String conicEqn = conic.getSpecificEquation();
			if (conicEqn != null) {
				sb.setLength(0);
				sb.append(loc.getMenu("Equation"));
				sb.append(' ');
				sb.append(conicEqn);
				action = new AbstractAction(sb.toString()) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						equationConicEqnCmd();
					}
				};
				addAction(action);
			}
		}

		if (explicitPossible && mode != GeoConicND.EQUATION_EXPLICIT) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ExplicitConicEquation"));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					equationExplicitConicEquationCmd();
				}
			};
			addAction(action);
		}

		if (vertexformPossible && mode != GeoConicND.EQUATION_VERTEX) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ParabolaVertexForm"));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					equationVertexEquationCmd();
				}
			};
			addAction(action);
		}

		if (conicformPossible && mode != GeoConicND.EQUATION_CONICFORM) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ParabolaConicForm"));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					equationConicformEquationCmd();
				}
			};
			addAction(action);
		}
	}

	private void addUserInputItem() {
		if (getGeo() instanceof GeoImplicit) {
			final GeoImplicit inputElement = (GeoImplicit) getGeo();
			if (inputElement.isValidInputForm()) {
				AbstractAction action;
				if (inputElement.isInputForm()) {
					action = new AbstractAction(loc.getMenu("ExpandedForm")) {

						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							implicitConicEquationCmd();
						}
					};
				} else {
					action = new AbstractAction(loc.getMenu("InputForm")) {

						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							inputFormCmd(inputElement);
						}
					};
				}
				addAction(action);
			}
		} else if (needsInputFormItem(getGeo())) {
			final EquationValue inputElement = (EquationValue) getGeo();
			AbstractAction action = new AbstractAction(
					loc.getMenu("InputForm")) {

				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					inputFormCmd(inputElement);
				}
			};

			addAction(action);

		} else if (getGeo() instanceof GeoPlaneND
				&& getGeo().getDefinition() != null) {
			AbstractAction action = new AbstractAction(
					loc.getMenu("ExpandedForm")) {

				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					implicitConicEquationCmd();
				}
			};
			addAction(action);
		}
	}

	private void addNumberItems() {
		// no items
	}

	private void addPin() {
		if (getGeo().isPinnable()) {
			// GeoText geoText = (GeoText) geo;
			// show object
			final JCheckBoxMenuItem cbItem = new JCheckBoxMenuItem(
					loc.getMenu("AbsoluteScreenLocation"));
			((AppD) app).setEmptyIcon(cbItem);
			cbItem.setIcon(((AppD) app).getScaledIcon(GuiResourcesD.PIN));
			cbItem.setSelected(getGeo().isPinned());
			cbItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean isSelected = cbItem.isSelected();
					pinCmd(isSelected);
				}
			});
			addItem(cbItem);
		}
	}

	private void addPlaneItems() {
		if (!(getGeo() instanceof ViewCreator)) {
			return;
		}
		final ViewCreator plane = (ViewCreator) getGeo();

		AbstractAction action;

		action = new AbstractAction(app.getLocalization()
				.getPlain("ShowAas2DView", getGeo().getLabelSimple())) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				plane.setView2DVisible(true);
			}
		};
		addAction(action);

	}

	private void addForAllItems() {
		// SHOW, HIDE

		// G.Sturr 2010-5-14: allow menu to show spreadsheet trace for
		// non-drawables
		if (getGeo().isDrawable() || (getGeo().isSpreadsheetTraceable()
				&& app.getGuiManager().showView(App.VIEW_SPREADSHEET))) {

			JCheckBoxMenuItem cbItem;

			// show object
			if (getGeo().isEuclidianToggleable()) {
				cbItem = new JCheckBoxMenuItem(loc.getMenu("ShowObject"));
				cbItem.setIcon(((AppD) app)
						.getScaledIcon(GuiResourcesD.MODE_SHOWHIDEOBJECT_GIF));
				cbItem.setSelected(getGeo().isSetEuclidianVisible());
				cbItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showObjectCmd();
					}
				});
				addItem(cbItem);
			}

			if (getGeo().isLabelShowable()) {
				// show label
				cbItem = new JCheckBoxMenuItem(loc.getMenu("ShowLabel"));
				cbItem.setSelected(isLabelShown());
				cbItem.setIcon(((AppD) app)
						.getScaledIcon(GuiResourcesD.MODE_SHOWHIDELABEL));
				cbItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showLabelCmd();
					}
				});
				addItem(cbItem);
			}

			// trace
			if (getGeo().isTraceable()) {
				cbItem = new JCheckBoxMenuItem(loc.getMenu("TraceOn"));
				cbItem.setIcon(
						((AppD) app).getScaledIcon(GuiResourcesD.TRACE_ON));
				cbItem.setSelected(((Traceable) getGeo()).getTrace());
				cbItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						traceCmd();
					}

				});
				addItem(cbItem);
			}

			// trace to spreadsheet: use SpreadsheetTrace Dialog

			if (app.getGuiManager().showView(App.VIEW_SPREADSHEET)
					&& getGeo().hasSpreadsheetTraceModeTraceable()) {

				// if multiple geos selected, check if recordable as a list
				if (getGeos().size() == 1 || GeoList
						.getTraceModes(getGeos()) != TraceModesEnum.NOT_TRACEABLE) {
					cbItem = new JCheckBoxMenuItem(
							loc.getMenu("RecordToSpreadsheet"));
					cbItem.setIcon(((AppD) app)
							.getScaledIcon(GuiResourcesD.SPREADSHEETTRACE));
					cbItem.setSelected(getGeo().getSpreadsheetTrace());

					cbItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							recordToSpreadSheetCmd();
						}
					});
					addItem(cbItem);

				}
			}

			// animation
			if (getGeo().isAnimatable()) {
				cbItem = new JCheckBoxMenuItem(loc.getMenu("Animating"));
				((AppD) app).setEmptyIcon(cbItem);
				cbItem.setSelected(((Animatable) getGeo()).isAnimating()
						&& app.getKernel().getAnimatonManager().isRunning());
				cbItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						animationCmd();
					}

				});
				addItem(cbItem);
			}

			// AUXILIARY OBJECT

			if (app.getGuiManager().showView(App.VIEW_ALGEBRA)
					&& app.showAuxiliaryObjects()
					&& getGeo().isAlgebraShowable()) {

				// show object
				cbItem = new JCheckBoxMenuItem(loc.getMenu("AuxiliaryObject"));
				cbItem.setIcon(
						((AppD) app).getScaledIcon(GuiResourcesD.AUXILIARY));
				cbItem.setSelected(getGeo().isAuxiliaryObject());
				cbItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showObjectAuxiliaryCmd();
					}
				});
				addItem(cbItem);
			}

			// fix object
			if (getGeo().isFixable() && (getGeo().isGeoText()
					|| getGeo().isGeoImage() || getGeo().isGeoButton())) {

				cbItem = new JCheckBoxMenuItem(loc.getMenu("FixObject"));
				((AppD) app).setEmptyIcon(cbItem);
				cbItem.setSelected(getGeo().isLocked());
				cbItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						fixObjectCmd(!getGeo().isLocked());
					}
				});
				addItem(cbItem);
			} else

			if (getGeo().isGeoNumeric()) {
				final GeoNumeric num = (GeoNumeric) getGeo();
				if (num.isSlider()) {

					cbItem = new JCheckBoxMenuItem(loc.getMenu("FixObject"));
					((AppD) app).setEmptyIcon(cbItem);
					cbItem.setSelected(num.isLockedPosition());
					cbItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							fixObjectNumericCmd(num);
						}
					});
					addItem(cbItem);
				}
			} else if (getGeo().isGeoBoolean()) {

				cbItem = new JCheckBoxMenuItem(loc.getMenu("FixCheckbox"));
				((AppD) app).setEmptyIcon(cbItem);
				cbItem.setSelected(getGeo().isLockedPosition());
				cbItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						fixCheckboxCmd();
					}
				});
				addItem(cbItem);
			}

			// Pinnable
			addPin();

			wrappedPopup.addSeparator();
		}

		// Rename
		if (getGeos().size() == 1 && app.letRename() && getGeo().isRenameable()) {
			addAction(new AbstractAction(loc.getMenu("Rename"),
					((AppD) app).getScaledIcon(GuiResourcesD.RENAME)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					renameCmd();
				}
			});
		}

		// EDITING
		// EDIT Text in special dialog
		if (getGeos().size() == 1 && getGeo() instanceof TextValue
				&& !getGeo().isTextCommand()
				&& !getGeo().isProtected(EventType.UPDATE)) {
			addAction(new AbstractAction(loc.getMenu("Edit"),
					((AppD) app).getScaledIcon(GuiResourcesD.EDIT)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					editCmd();
				}
			});
		}

		// DELETE
		if (app.letDelete() && !getGeo().isProtected(EventType.REMOVE)) {
			addAction(new AbstractAction(loc.getMenu("Delete"),
					((AppD) app).getScaledIcon(GuiResourcesD.DELETE_SMALL)) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					deleteCmd(false);
				}
			});
		}

		if (((AppD) app).letShowPropertiesDialog()
				&& getGeo().hasProperties()) {
			wrappedPopup.addSeparator();

			// open properties dialog
			addAction(new AbstractAction(loc.getMenu("Properties") + " ...",
					((AppD) app)
							.getScaledIcon(GuiResourcesD.VIEW_PROPERTIES_16)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (isJustOneGeo()) {
						app.getSelectionManager().setSelectedGeos(checkOneGeo(),
								true);
					}
					openPropertiesDialogCmd();
				}
			});
		}
	}

	/**
	 * @return whether just one geo is selected
	 */
	protected boolean isJustOneGeo() {
		return justOneGeo;
	}

	/**
	 * Adds given action to this menu
	 * 
	 * @param ac
	 *            action
	 */
	void addAction(Action ac) {
		JMenuItem mi = wrappedPopup.add(ac);
		mi.setBackground(bgColor);
	}

	/**
	 * Adds given item to this menu
	 * 
	 * @param mi
	 *            item
	 */
	void addItem(JMenuItem mi) {
		mi.setBackground(bgColor);
		wrappedPopup.add(mi);
	}

	/**
	 * Sets title of this menu; e.g. "Point A" or "Selection"
	 * 
	 * @param str
	 *            title of this menu
	 */
	protected void setTitle(String str) {
		JLabel title = new JLabel(str);
		title.setFont(((AppD) app).getBoldFont());
		title.setBackground(bgColor);
		title.setForeground(fgColor);

		title.setIcon(((AppD) app).getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 15));

		// wrap title JLabel in a panel to prevent unneeded spacing
		wrappedPopup.add(LayoutUtil.flowPanel(0, 0, 0, title));
		wrappedPopup.addSeparator();

		title.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				wrappedPopup.setVisible(false);
			}
		});
	}

	/**
	 * Adds keyboard shortcut to given itemof this menu
	 * 
	 * @param mi
	 *            item
	 * @param acc
	 *            accelerator
	 */
	protected void setMenuShortCutAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		mi.setAccelerator(ks);
	}

	/**
	 * @return the wrapped PopupMenu
	 */
	public JPopupMenu getWrappedPopup() {
		return this.wrappedPopup;
	}

	@Override
	public void recordToSpreadSheetCmd() {
		GeoElement geoRecordToSpreadSheet;
		if (getGeos().size() == 1) {
			geoRecordToSpreadSheet = getGeo();
		} else {
			geoRecordToSpreadSheet = app.getKernel().getAlgoDispatcher()
					.list(null, getGeos(), false);
			geoRecordToSpreadSheet.setAuxiliaryObject(true);
		}

		((GuiManagerD) app.getGuiManager()).getSpreadsheetView()
				.showTraceDialog(geoRecordToSpreadSheet, null);
	}

}
