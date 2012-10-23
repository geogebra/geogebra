package geogebra.gui.view.assignment;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.gui.view.Gridable;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.print.PageFormat;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Christoph Reinisch
 *
 */
public class AssignmentView extends JPanel implements View, Gridable {
	private static final long serialVersionUID = 1L;
	private AppD app;
	private Kernel kernel;
	
	public JScrollPane scrollPane;

	public AssignmentView(final AppD app) {
		super(new BorderLayout());
		
		this.app = app;
		kernel = app.getKernel();
		//data = new ConstructionTableData();
		
		
		
	}

	public void attachView() {
		kernel.attach(this);
	}

	public void detachView() {
		kernel.detach(this);
//		clearView();
	}

	public void add(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void update(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void repaintView() {
		// TODO Auto-generated method stub
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void clearView() {
		// TODO Auto-generated method stub
		
	}

	public void setMode(int mode,ModeSetter m) {
		// TODO Auto-generated method stub
		
	}

	public int getViewID() {
		return App.VIEW_ASSIGNMENT;
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[] getGridColwidths() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getGridRowHeights() {
		// TODO Auto-generated method stub
		return null;
	}

	public AppD getApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	public Component[][] getPrintComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	public JComponent getStyleBar() {
		// TODO Auto-generated method stub
		return null;
	}

		
	
}