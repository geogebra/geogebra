package geogebra.web.gui;


import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import geogebra.common.gui.ContextMenuGeoElement;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoUserInputElement;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.main.AppW;
import geogebra.web.openjdk.awt.geom.Point;

/**
 * @author gabor
 * 
 * ContextMenuGeoElement for Web
 *
 */
public class ContextMenuGeoElementW extends ContextMenuGeoElement {
	
	protected PopupPanel wrappedPopup;
	protected MenuBar popupMenu;
	private int popupMenuS;
	private int popupMenuSize = 0;
	
	/**
	 * Creates new context menu
	 * @param app application
	 */
	ContextMenuGeoElementW(AppW app) {
		this.app = app;    
		this.wrappedPopup = new PopupPanel();
		this.popupMenu = new MenuBar(true);
		wrappedPopup.add(popupMenu);
	}
	
	/** Creates new MyPopupMenu for GeoElement
	 * @param app application
	 * @param geos selected elements
	 * @param location screen position
	 */
	public ContextMenuGeoElementW(AppW app, ArrayList<GeoElement> geos, Point location) {
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


		
		
		if (popupMenuSize  > 2)
			popupMenu.addSeparator();
		addForAllItems();
	}

	private void addForAllItems() {
		// SHOW, HIDE
		
		//G.Sturr 2010-5-14: allow menu to show spreadsheet trace for non-drawables
        if (geo.isDrawable() || (geo.isSpreadsheetTraceable() && app.getGuiManager().showView(App.VIEW_SPREADSHEET))) {
        	//AG Continue here
        }
	    
    }

	private void addPlaneItems() {
	    App.debug("it is for 3D!");
    }

	private void addViewForValueStringItems() {
	   App.debug("it is for 3D!");
    }

	private void addUserInputItem() {
		if (geo instanceof GeoUserInputElement){
			final GeoUserInputElement inputElement=(GeoUserInputElement)geo;
			if (inputElement.isValidInputForm()){
				Command action;
				if (inputElement.isInputForm()){
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
		//no items
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

	private void addVectorItems() {
		if (!(geo instanceof GeoVector))
			return;
		GeoVector vector = (GeoVector) geo;
		int mode = vector.getMode();
		Command action;
		
		if (mode != Kernel.COORD_CARTESIAN) {
			action = new Command() {
				
				public void execute() {
					cartesianCoordsForVectorItemsCmd();
				}
			};
			addAction(action, null, app.getPlain("CartesianCoords"));
		}
		
		if (mode != Kernel.COORD_POLAR) {
			action = new Command() {
				
				public void execute() {
					polarCoordsForVectorItemsCmd();
				}
			};
			addAction(action, null, app.getPlain("PolarCoords"));
		}
	    
    }

	private void addLineItems() {
		if (!(geo instanceof GeoLine))
			return;
		if (geo instanceof GeoSegment)
			return;        

		GeoLine line = (GeoLine) geo;
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

	private void addPointItems() {
		if (!(geo instanceof GeoPoint))
			return;
		GeoPoint point = (GeoPoint) geo;
		int mode = point.getMode();
		Command action;
		
		if (mode != Kernel.COORD_CARTESIAN && !geo.isFixed() && point.getMode() != Kernel.COORD_COMPLEX) {
			action = new Command() {
				
				public void execute() {
					cartesianCoordsCmd();
				}
			};
			addAction(action, null, app.getPlain("CartesianCoords"));
		}
		
		if (mode != Kernel.COORD_POLAR && !geo.isFixed() && point.getMode() != Kernel.COORD_COMPLEX) {
			action = new Command() {
				
				public void execute() {
					polarCoorsCmd();
				}
			};
			addAction(action, null, app.getPlain("PolarCoords"));
		}
	    
    }

	private void addAction(Command action, String html, String text) {
		MenuItem mi;
	    if (html != null) {
	    	mi = new MenuItem(html, true, action);
	    } else {
	    	mi = new MenuItem(text, action);
	    }
	    popupMenu.addItem(mi); 
	    popupMenuSize++;
    }

	private void setTitle(String str) {
	    MenuItem title = new MenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(), str),
	    		true, new Command() {
					
					public void execute() {
						wrappedPopup.setVisible(false);
					}
				});
	    popupMenu.addItem(title);
    }
	
	public PopupPanel getWrappedPopup() {
	    return wrappedPopup;
    }
	
	public void show(Canvas c, int x, int y) {
		
	}
	
	
	
}
