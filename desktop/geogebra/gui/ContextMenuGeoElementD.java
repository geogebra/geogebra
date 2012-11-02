/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */


package geogebra.gui;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.Animatable;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement.TraceModesEnum;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoUserInputElement;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.ViewCreator;
import geogebra.common.main.App;
import geogebra.euclidian.EuclidianViewD;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.layout.LayoutD;
import geogebra.gui.util.LayoutUtil;
import geogebra.main.AppD;

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

/**
 * Context menu for GeoElement objects.
 * @author  Markus Hohenwarter
 */
public class ContextMenuGeoElementD extends geogebra.common.gui.ContextMenuGeoElement {

	private static final long serialVersionUID = 1L;
	/** background color*/
	protected final static Color bgColor = Color.white;
	/** foreground color*/
	protected final static Color fgColor = Color.black;
	protected JPopupMenu wrappedPopup;
	/**
	 * Creates new context menu
	 * @param app application
	 */
	ContextMenuGeoElementD(AppD app) {
		this.app = app;    
		this.wrappedPopup = new JPopupMenu();
		wrappedPopup.setBackground(bgColor);
	}
	
	/** Creates new MyPopupMenu for GeoElement
	 * @param app application
	 * @param geos selected elements
	 * @param location screen position
	 */
	public ContextMenuGeoElementD(AppD app, ArrayList<GeoElement> geos, Point location) {
		this(app);
		this.geos = geos;
		geo = geos.get(0);

		String title;

		if (geos.size() == 1) {
			title = getDescription(geo);
		} else {
			title = app.getPlain("Selection");
		}
		setTitle(title);        

		if (app.getGuiManager().showView(App.VIEW_ALGEBRA)) {
			addPointItems();
			addLineItems();
			addVectorItems();
			addConicItems();
			addNumberItems();	
			addUserInputItem();
			
			addViewForValueStringItems();
				
		}
		
		//TODO remove the condition when ggb version >= 5
		if (app.getKernel().getManager3D()!=null)
			addPlaneItems();


		
		
		if (wrappedPopup.getComponentCount() > 2)
			wrappedPopup.addSeparator();
		addForAllItems();
		
		app.setComponentOrientation(wrappedPopup);

	}

	private void addPointItems() {
		if (!(geo instanceof GeoPoint))
			return;
		GeoPoint point = (GeoPoint) geo;
		int mode = point.getMode();
		AbstractAction action;

		if (mode != Kernel.COORD_CARTESIAN && !geo.isFixed() && point.getMode() != Kernel.COORD_COMPLEX) {
			action = new AbstractAction(app.getPlain("CartesianCoords")) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					cartesianCoordsCmd();
				}
			};
			addAction(action);
		}

		if (mode != Kernel.COORD_POLAR && !geo.isFixed() && point.getMode() != Kernel.COORD_COMPLEX) {
			action = new AbstractAction(app.getPlain("PolarCoords")) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					polarCoorsCmd();
				}
			};
			addAction(action);
		}
		

		/*
        if (mode != Kernel.COORD_COMPLEX && !geo.isFixed()) {
            action = new AbstractAction(app.getPlain("ComplexNumber")) {
                /**
		 * 
		 *
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    point.setMode(Kernel.COORD_COMPLEX);
                    point.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        } */
	}

	private void addLineItems() {
		if (!(geo instanceof GeoLine))
			return;
		if (geo instanceof GeoSegment)
			return;        

		GeoLine line = (GeoLine) geo;
		int mode = line.getMode();
		AbstractAction action;
		StringBuilder sb = new StringBuilder();

		if (mode != GeoLine.EQUATION_IMPLICIT) {
			sb.setLength(0);
			sb.append(app.getPlain("Equation"));
			sb.append(' ');
			sb.append(app.getPlain("ImplicitLineEquation"));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					equationImplicitEquationCmd();
				}
			};
			addAction(action);
		}

		if (mode != GeoLine.EQUATION_EXPLICIT) {
			sb.setLength(0);
			sb.append(app.getPlain("Equation"));
			sb.append(' ');
			sb.append(app.getPlain("ExplicitLineEquation"));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					equationExplicitEquationCmd();
				}
			};
			addAction(action);
		}

		if (mode != GeoLine.PARAMETRIC) {
			action = new AbstractAction(app.getPlain("ParametricForm")) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					parametricFormCmd();
				}
			};
			addAction(action);
		}

	}

	private void addVectorItems() {
		if (!(geo instanceof GeoVector))
			return;
		GeoVector vector = (GeoVector) geo;
		int mode = vector.getMode();
		AbstractAction action;

		if (mode != Kernel.COORD_CARTESIAN) {
			action = new AbstractAction(app.getPlain("CartesianCoords")) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					cartesianCoordsForVectorItemsCmd();
				}
			};
			addAction(action);
		}

		if (mode != Kernel.COORD_POLAR) {
			action = new AbstractAction(app.getPlain("PolarCoords")) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					polarCoordsForVectorItemsCmd();
				}
			};
			addAction(action);
		}
		/*
        if (mode != Kernel.COORD_COMPLEX) {
            action = new AbstractAction(app.getPlain("ComplexNumber")) {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					vector.setMode(Kernel.COORD_COMPLEX);
					vector.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }*/
	}

	private void addConicItems() {
		if (geo.getClass() != GeoConic.class)
			return;
		GeoConic conic = (GeoConic) geo;

		// there's no need to show implicit equation
		// if you can't select the specific equation
		boolean specificPossible = conic.isSpecificPossible();
		boolean explicitPossible = conic.isExplicitPossible();
		if (!(specificPossible || explicitPossible))
			return;

		int mode = conic.getToStringMode();
		AbstractAction action;
		StringBuilder sb = new StringBuilder();

		if (mode != GeoConicND.EQUATION_IMPLICIT) {
			sb.append(app.getPlain("Equation"));
			sb.append(' ');
			sb.append(app.getPlain("ImplicitConicEquation"));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
				sb.append(app.getPlain("Equation"));
				sb.append(' ');
				sb.append(conicEqn);
				action = new AbstractAction(sb.toString()) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						equationConicEqnCmd();
					}
				};
				addAction(action);
			}
		}

		if (explicitPossible && mode != GeoConicND.EQUATION_EXPLICIT) {
			sb.setLength(0);
			sb.append(app.getPlain("Equation"));
			sb.append(' ');
			sb.append(app.getPlain("ExplicitConicEquation"));
			action = new AbstractAction(sb.toString()) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					equationExplicitConicEquationCmd();
				}
			};
			addAction(action);
		}
	}
	
	private void addUserInputItem(){
		if (geo instanceof GeoUserInputElement){
			final GeoUserInputElement inputElement=(GeoUserInputElement)geo;
			if (inputElement.isValidInputForm()){
				AbstractAction action;
				if (inputElement.isInputForm()){
					action=new AbstractAction(app.getPlain("ExtendedForm")) {
						
						private static final long serialVersionUID = 1L;
	
						public void actionPerformed(ActionEvent e) {
							extendedFormCmd(inputElement);
						}
					};
				}else{
					action=new AbstractAction(app.getPlain("InputForm")) {
						
						private static final long serialVersionUID = 1L;
	
						public void actionPerformed(ActionEvent e) {
							inputFormCmd(inputElement);
						}
					};
				}
				addAction(action);
			}
		}
	}

	private void addNumberItems() {
		//no items
	}

	private void addPin() {
		if (geo.isPinnable()) {
			//GeoText geoText = (GeoText) geo;
			// show object
			final JCheckBoxMenuItem cbItem = new JCheckBoxMenuItem(app.getPlain("AbsoluteScreenLocation"));
			((AppD) app).setEmptyIcon(cbItem);
			cbItem.setIcon(((AppD) app).getImageIcon("pin.png"));
			cbItem.setSelected(geo.isPinned());
			cbItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean isSelected = cbItem.isSelected();
					pinCmd(isSelected);
				}        	
			});
			addItem(cbItem);     
		}
	}
	
	
	private void addPlaneItems() {
		if (!(geo instanceof ViewCreator))
			return;
		final ViewCreator plane = (ViewCreator) geo;

		AbstractAction action;

		action = new AbstractAction(app.getPlain("Show2DViewFromA", geo.getLabelSimple())) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				plane.setView2DVisible(true);
			}
		};
		addAction(action);

	}
	
	private void addViewForValueStringItems() {

		AbstractAction action;
		
		if (!geo.hasValueStringChangeableRegardingView())
			return;

		DockPanel panel = ((LayoutD) app.getGuiManager().getLayout()).getDockManager().getFocusedEuclidianPanel();
		if (panel==null)
			return;
		
		EuclidianViewInterfaceCommon oldView = (EuclidianViewInterfaceCommon)geo.getViewForValueString();
		EuclidianViewInterfaceCommon newView = app.getActiveEuclidianView();
		
		if (newView==app.getEuclidianView2())
			newView=app.getEuclidianView1(); //graphics and graphics2 are treated the same
		
		if (oldView==newView)
			return;


		if (oldView==null)
			if (newView==app.getEuclidianView1() || newView==app.getEuclidianView2()){
				if (!geo.isGeoElement3D()) // if 2D geo and new view is 2D standard view, no changes
					return;
			}else if (!(newView instanceof EuclidianViewD))
				if (geo.isGeoElement3D()) // if 3D geo and new view is 3D view, no changes
					return;
		
		
		
		action = new AbstractAction(app.getPlain("ShowValueStringRegardingA",newView.getTranslatedFromPlaneString())) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				//TODO change that to chooser
				showValueStringRegardingACmd();
				
			}
		};
		addAction(action);

	}


	private void addForAllItems() {
		// SHOW, HIDE
		
		//G.Sturr 2010-5-14: allow menu to show spreadsheet trace for non-drawables
        if (geo.isDrawable() || (geo.isSpreadsheetTraceable() && app.getGuiManager().showView(App.VIEW_SPREADSHEET))) {  			
        	
        	JCheckBoxMenuItem cbItem;

			// show object
			if (geo.isEuclidianShowable() && geo.getShowObjectCondition() == null && (!geo.isGeoBoolean() || geo.isIndependent())) {
				cbItem = new JCheckBoxMenuItem( app.getPlain("ShowObject"));
				cbItem.setIcon(((AppD) app).getImageIcon("mode_showhideobject_16.gif"));
				cbItem.setSelected(geo.isSetEuclidianVisible());
				cbItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showObjectCmd();
					}        	
				});
				addItem(cbItem);
			}

			if (geo.isLabelShowable()) {           
				// show label
				cbItem = new JCheckBoxMenuItem( app.getPlain("ShowLabel"));	           
				cbItem.setSelected(geo.isLabelVisible());
				cbItem.setIcon(((AppD) app).getImageIcon("mode_showhidelabel_16.gif"));
				cbItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showLabelCmd();
					}        	
				});
				addItem(cbItem);	        		            	            	            
			}     

			//  trace
			if (geo.isTraceable()) {            	
				cbItem = new JCheckBoxMenuItem( app.getPlain("TraceOn"));
				cbItem.setIcon(((AppD) app).getImageIcon("trace_on.gif"));
				cbItem.setSelected(((Traceable) geo).getTrace());
				cbItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						traceCmd();
					}

					
				});
				addItem(cbItem);            	
			}  

			//  trace to spreadsheet 
			
			// G.Sturr 2010-5-12 
			// modified to use SpreadsheetTrace Dialog

			if (app.getGuiManager().showView(App.VIEW_SPREADSHEET) && geo.hasSpreadsheetTraceModeTraceable()) {

				//if multiple geos selected, check if recordable as a list
				if (geos.size()==1 || GeoList.getTraceModes(geos)!=TraceModesEnum.NOT_TRACEABLE){
					cbItem = new JCheckBoxMenuItem(app.getMenu("RecordToSpreadsheet"));
					cbItem.setIcon(((AppD) app).getImageIcon("spreadsheettrace.gif"));
					cbItem.setSelected(geo.getSpreadsheetTrace());

					cbItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							recordToSpreadSheetCmd();
						}
					});
					addItem(cbItem);


				}
			}
			
			/* ------------ OLD CODE ---------------------
			if (geo.isGeoPoint() && app.getGuiManager().showSpreadsheetView()) {            	
				cbItem = new JCheckBoxMenuItem( app.getPlain("TraceToSpreadsheet"));
				cbItem.setIcon(app.getImageIcon("spreadsheettrace.gif"));
				cbItem.setSelected(((GeoPoint) geo).getSpreadsheetTrace());
				cbItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						((GeoPoint) geo).setSpreadsheetTrace(!((GeoPoint) geo).getSpreadsheetTrace());
						geo.updateRepaint();
						app.storeUndoInfo();
					}       	
				});
				addItem(cbItem);            	
			}    
			*/
			//END G.Sturr
			

			//  animation
			if (geo.isAnimatable()) {            	
				cbItem = new JCheckBoxMenuItem( app.getPlain("Animating"));
				((AppD) app).setEmptyIcon(cbItem);
                cbItem.setSelected(((Animatable) geo).isAnimating() && app.getKernel().getAnimatonManager().isRunning());
				cbItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						animationCmd();	
					}

					
				});
				addItem(cbItem);            	
			}

			// AUXILIARY OBJECT

			if (app.getGuiManager().showView(App.VIEW_ALGEBRA) && app.showAuxiliaryObjects() && 
					geo.isAlgebraShowable()) {

				// show object
				cbItem = new JCheckBoxMenuItem( app.getPlain("AuxiliaryObject"));
				cbItem.setIcon(((AppD) app).getImageIcon("aux_folder.gif"));
				cbItem.setSelected(geo.isAuxiliaryObject());
				cbItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showObjectAuxiliaryCmd();
					}
				});
				addItem(cbItem);                      
			}                                                           

			//  fix object
			if (geo.isFixable() && (geo.isGeoText() || geo.isGeoImage())) {   

				cbItem = new JCheckBoxMenuItem( app.getPlain("FixObject"));
				((AppD) app).setEmptyIcon(cbItem);
				cbItem.setSelected(geo.isFixed());
				cbItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						fixObjectCmd();
					}       	
				});
				addItem(cbItem);            	
			} else
			
			if (geo.isGeoNumeric()) {
				final GeoNumeric num = (GeoNumeric)geo;
				if (num.isSlider()) {   

					cbItem = new JCheckBoxMenuItem( app.getPlain("FixObject"));
					((AppD) app).setEmptyIcon(cbItem);
					cbItem.setSelected(num.isSliderFixed());
					cbItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							fixObjectNumericCmd(num);
						}       	
					});
					addItem(cbItem);            	
				}  
			}

			// Pinnable
			addPin();


			wrappedPopup.addSeparator();
		}


/*
		// EDIT: copy to input bar       
		if (geos.size() == 1 && app.showAlgebraInput() && !geo.isGeoImage() && geo.isDefined()) {
			addAction(new AbstractAction(
					app.getMenu("CopyToInputBar"),
					app.getImageIcon("edit.png")) {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {                    
					AlgebraInput ai = (AlgebraInput) app.getGuiManager().getAlgebraInput();
					if (ai != null) {    
						// copy into text field
						ai.getTextField().setText(geo.getValueForInputBar());
						ai.requestFocus();
					}
				}
			});
			addSeparator();
		}
*/
		/*
        // EDIT in AlgebraView
        else if (app.showAlgebraView() && geo.isChangeable() && !geo.isGeoImage()) { 
            addAction(new AbstractAction(
                app.getPlain("Edit"),
                app.getImageIcon("edit.png")) {
					private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    app.startEditing(geo);
                }
            });
        }    
		 */                                    

		// Rename      
		if (geos.size() == 1 && app.letRename() && geo.isRenameable())  {    
			addAction(new AbstractAction(
					app.getPlain("Rename"),
					((AppD) app).getImageIcon("rename.png")) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					renameCmd();
				}
			});
		}

		// EDITING      
		// EDIT Text in special dialog
		if (geos.size() == 1 && geo.isTextValue() && !geo.isTextCommand() && !geo.isFixed()) {
			addAction(new AbstractAction(
					app.getPlain("Edit"),
					((AppD) app).getImageIcon("edit.png")) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					editCmd(); 
				}
			});
		}      

		/*
        // REDEFINE    
        else if (app.letRedefine() && geo.isRedefineable()) {     
                addAction(new AbstractAction(
                            app.getPlain("Redefine"),
                            app.getImageIcon("edit.png")) {

								private static final long serialVersionUID = 1L;

							public void actionPerformed(ActionEvent e) {
                                app.showRedefineDialog(geo);
                            }
                        });         
        }
		 */

		// DELETE    
		if (app.letDelete() && !geo.isFixed()) {  
			addAction(new AbstractAction(
					app.getPlain("Delete"),
					((AppD) app).getImageIcon("delete_small.gif")) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					deleteCmd();
				}
			});       
		}

		if (((AppD) app).letShowPropertiesDialog() && geo.hasProperties()) {
			wrappedPopup.addSeparator();

			// open properties dialog      
			addAction(new AbstractAction(
					app.getPlain("Properties") + " ...",
					((AppD) app).getImageIcon("view-properties16.png")) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					openPropertiesDialogCmd();
				}
			});
		}
	}
	//private ArrayList tempArrayList = new ArrayList();

	/**
	 * Adds given action to this menu
	 * @param ac action
	 */
	void addAction(Action ac) {
		JMenuItem mi = wrappedPopup.add(ac);
		mi.setBackground(bgColor);              
	}

	/**
	 * Adds given item to this menu
	 * @param mi item
	 */
	void addItem(JMenuItem mi) {        
		mi.setBackground(bgColor);
		wrappedPopup.add(mi);
	}

	/**
	 * Sets title of this menu; e.g. "Point A" or "Selection"
	 * @param str title of this menu
	 */
	protected void setTitle(String str) {
		JLabel title = new JLabel(str);
		title.setFont(((AppD) app).getBoldFont());                      
		title.setBackground(bgColor);
		title.setForeground(fgColor);
		
		title.setIcon(((AppD) app).getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 15)); 
		
		// wrap title JLabel in a panel to prevent unneeded spacing
		wrappedPopup.add(LayoutUtil.flowPanel(0,0,0,title));
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
	 * @param mi item
	 * @param acc accelerator
	 */
	protected void setMenuShortCutAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask());
		mi.setAccelerator(ks);
	}
	
	/**
	 * @return the wrapped PopupMenu
	 */
	public JPopupMenu getWrappedPopup() {
		return this.wrappedPopup;
	}

	public void showValueStringRegardingACmd() {
		geo.setViewForValueString(app.getActiveEuclidianView());
		geo.update();
		app.getGuiManager().getAlgebraView().remove(geo);
		app.getGuiManager().getAlgebraView().add(geo);
	}

	public void recordToSpreadSheetCmd() {
		GeoElement geoRecordToSpreadSheet;
		if (geos.size()==1)
			geoRecordToSpreadSheet = geo;
		else{
			geoRecordToSpreadSheet = app.getKernel().getAlgoDispatcher().List(null, geos, false);
			geoRecordToSpreadSheet.setAuxiliaryObject(true);
		}
			
		((GuiManagerD) app.getGuiManager()).getSpreadsheetView().showTraceDialog(geoRecordToSpreadSheet, null);
	}       	
	

}
