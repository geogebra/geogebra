package geogebra.gui;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.kernel.geos.FromMeta;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.main.Application;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


/**
 * Popup Menu for choosing a geo
 * 
 * @author mathieu
 *
 */
public class ContextMenuChooseGeo extends ContextMenuGeoElement {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	


	
	/**
	 * 
	 */
	protected AbstractEuclidianView view;
	
	/**
	 * polygons/polyhedra parents of segments, polygons, ...
	 */
	private TreeSet<GeoElement> metas;
	
	
	private JMenu selectAnotherMenu;

	/**
	 * 
	 * @param app application
	 * @param view view
	 * @param selectedGeos selected geos
	 * @param geos geos
	 * @param location place to show
	 */
	public ContextMenuChooseGeo(Application app, EuclidianViewND view, 
			ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, Point location) {

		super(app, selectedGeos, location);
		
		//section to choose a geo
		addSeparator();
		addSelectAnotherMenu();
		
		//return if just one geo, or if first geos more than one
		if (geos.size()<2 || selectedGeos.size()>1)
			return;
		
		this.view = view;
		
		GeoElement geoSelected = selectedGeos.get(0);
		
		
		//add geos
		metas = new TreeSet<GeoElement>();
		
		for (GeoElement geo : geos){
			if (geo!=geoSelected){//don't add selected geo
				addGeo(geo);
			}
			
			if (geo.isFromMeta()){
				GeoElement meta = ((FromMeta) geo).getMeta();
				if (!metas.contains(meta)){
					addGeo(meta);
				}
			}
		}
	}
	
	
	
	
	
	private void addSelectAnotherMenu(){
		selectAnotherMenu = new JMenu(app.getPlain("SelectAnother") );
		selectAnotherMenu.setIcon(app.getEmptyIcon());
		selectAnotherMenu.setBackground(getBackground());  
		add(selectAnotherMenu);     
		
	}
	
	
	/**
	 * 
	 */
	private void addGeo(GeoElement geo) {
		
		GeoAction chooser = new GeoAction(geo);
		JMenuItem mi = selectAnotherMenu.add(chooser);    
		mi.setBackground(bgColor);  
		mi.setText(getDescription(geo));			
		mi.addMouseListener(new MyMouseAdapter(geo));
	
		//prevent to add meta twice
		metas.add(geo);
			
		            
	}
	
	/**
	 * Action when select a geo
	 * @author mathieu
	 *
	 */
	private class GeoAction extends AbstractAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private GeoElement geo;
		
		
		/**
		 * Create chooser for this geo
		 * @param geo geo to choose
		 */
		public GeoAction(GeoElement geo){
			super();
			this.geo = geo;
				
		}
		
		public void actionPerformed(ActionEvent e) {

			//AbstractApplication.debug(geo.getLabelSimple());
			app.clearSelectedGeos(false); //repaint done next step
			app.addSelectedGeo(geo);
		}
		
	}
	
	
	private class MyMouseAdapter extends MouseAdapter{
		
		private GeoElement geo;
		
		public MyMouseAdapter(GeoElement geo){
			this.geo=geo;
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			//AbstractApplication.debug(geo.getLabelSimple());
			/*
			geo.setHighlighted(true);
			app.getKernel().notifyRepaint();
			*/
			view.getEuclidianController().doSingleHighlighting(geo);
		}
		
		/*
		@Override
		public void mouseExited(MouseEvent e) {
			AbstractApplication.debug(geo.getLabelSimple());
			geo.setHighlighted(false);
			app.getKernel().notifyRepaint();
			
		}
		*/
	}
}
