package geogebra3D.gui;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.kernel.geos.FromMeta;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.ContextMenuGeoElement;
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
	
	private int index;
	
	private static final int MORE_INDEX = 4;
	
	private JMenu moreMenu;

	/**
	 * 
	 * @param app application
	 * @param view view
	 * @param firstGeo first geo
	 * @param geos geos
	 * @param location place to show
	 */
	public ContextMenuChooseGeo(Application app, EuclidianViewND view, 
			ArrayList<GeoElement> firstGeo,
			ArrayList<GeoElement> geos, Point location) {

		super(app, firstGeo, location);
		
		//section to choose a geo
		
		//return if just one geo, or if first geos more than one
		if (geos.size()<2 || firstGeo.size()>1)
			return;
		
		this.view = view;
		
		
		
		setChooseGeoTitle(app.getPlain("SelectOther"));	
		
		//add geos
		metas = new TreeSet<GeoElement>();
		
		index = 0;	
		for (GeoElement geo : geos){
			if (index>0){//don't add first geo
				addGeo(geo);
			}else
				index++;
			if (geo.isFromMeta()){
				GeoElement meta = ((FromMeta) geo).getMeta();
				if (!metas.contains(meta)){
					addGeo(meta);
					metas.add(meta);
				}
			}
		}
	}
	
	
	
	private void setChooseGeoTitle(String str) {
		JLabel title = new JLabel(str);
		title.setFont(app.getBoldFont());                      
		title.setBackground(bgColor);
		title.setForeground(fgColor);          
		
		title.setIcon(app.getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(5, 15, 2, 5));  
		addSeparator();   
		add(title);
		addSeparator();   

		title.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});
	}
	
	
	/**
	 * 
	 */
	private void addGeo(GeoElement geo) {

		if (index==MORE_INDEX){ //create more menu
			moreMenu = new JMenu(app.getPlain("More") );
			moreMenu.setIcon(app.getEmptyIcon());
			moreMenu.setBackground(getBackground());  
			add(moreMenu);     
		}
		
		GeoAction chooser = new GeoAction(geo);
		JMenuItem mi;
		if (index<MORE_INDEX){ //put it in regular popup menu
			mi = this.add(chooser);			
		}else{ //put it in more menu
			mi = moreMenu.add(chooser);    
		}
		
		mi.setBackground(bgColor);  
		mi.setText(geo.getNameDescriptionHTML(false, true));			
		mi.addMouseListener(new MyMouseAdapter(geo));
	
			
		index++;
		            
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
			app.clearSelectedGeos();
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
