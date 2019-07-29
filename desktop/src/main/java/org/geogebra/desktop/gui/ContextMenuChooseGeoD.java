package org.geogebra.desktop.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.main.AppD;

/**
 * Popup Menu for choosing a geo
 * 
 * @author mathieu
 *
 */
public class ContextMenuChooseGeoD extends ContextMenuGeoElementD {

	/**
	 * 
	 */
	protected EuclidianView view;

	/**
	 * polygons/polyhedra parents of segments, polygons, ...
	 */
	private TreeSet<GeoElement> metas;

	private ArrayList<GeoElement> selectedGeos;
	private GPoint loc;

	private JMenu selectAnotherMenu;

	/**
	 * 
	 * @param app
	 *            application
	 * @param view
	 *            view
	 * @param selectedGeos
	 *            selected geos
	 * @param geos
	 *            geos
	 * @param location
	 *            place to show
	 */
	public ContextMenuChooseGeoD(AppD app, EuclidianView view,
			ArrayList<GeoElement> selectedGeos, ArrayList<GeoElement> geos,
			Point location, GPoint invokerLocation) {

		super(app, selectedGeos, location);

		this.view = view;
		this.selectedGeos = selectedGeos;

		// return if just one geo, or if first geos more than one
		if (/* geos.size()<2 || */selectedGeos.size() > 1) {
			justOneGeo = false;
			return;
		}

		justOneGeo = true;

		// section to choose a geo
		// addSeparator();
		createSelectAnotherMenu(view.getMode());

		this.loc = invokerLocation;
		this.setGeos(geos);

		GeoElement geoSelected = selectedGeos.get(0);

		// add geos
		geoAddedForSelectAnother = false;

		metas = new TreeSet<>();

		for (GeoElement geo : geos) {
			if (geo != geoSelected) {// don't add selected geo
				addGeo(geo);
			}

			if (geo.getMetasLength() > 0) {
				for (GeoElement meta : ((FromMeta) geo).getMetas()) {
					if (!metas.contains(meta) && (meta != geoSelected || !app.has(Feature.G3D_SELECT_META))) {
						addGeo(meta);
					}
				}
			}
		}

		if (geoAddedForSelectAnother) {
			addSelectAnotherMenu();
		}

		// TODO: clear selection is not working from here
		this.getWrappedPopup().getSelectionModel().clearSelection();

	}

	private void createSelectAnotherMenu(int mode) {
		Localization localization = app.getLocalization();
		if (EuclidianConstants.isMoveOrSelectionMode(mode)) {
			selectAnotherMenu = new JMenu(
					localization.getMenu("SelectAnother"));
		} else {
			selectAnotherMenu = new JMenu(
					localization.getMenu("PerformToolOn"));
		}
		selectAnotherMenu.setIcon(((AppD) app).getEmptyIcon());
		selectAnotherMenu.setBackground(getWrappedPopup().getBackground());
		selectAnotherMenu.setFont(((AppD) app).getItalicFont());
	}

	private void addSelectAnotherMenu() {

		// add the selection menu just under the title
		getWrappedPopup().add(selectAnotherMenu, 1);

	}

	private boolean geoAddedForSelectAnother = false;

	/**
	 * 
	 */
	private void addGeo(GeoElement geo) {

		// prevent selection of xOy plane
		if (geo == app.getKernel().getXOYPlane()) {
			return;
		}

		geoAddedForSelectAnother = true;

		GeoAction chooser = new GeoAction(geo);
		JMenuItem mi = selectAnotherMenu.add(chooser);
		mi.setBackground(bgColor);
		mi.setText(getDescription(geo, true));
		mi.addMouseListener(new MyMouseAdapter(geo));

		// prevent to add meta twice
		metas.add(geo);

	}

	/**
	 * Action when select a geo
	 * 
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
		 * 
		 * @param geo
		 *            geo to choose
		 */
		public GeoAction(GeoElement geo) {
			super();
			this.geo = geo;

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			geoActionCmd(this.geo, selectedGeos, getGeos(), view, loc);
		}

	}

	private class MyMouseAdapter extends MouseAdapter {

		private GeoElement geo;

		public MyMouseAdapter(GeoElement geo) {
			this.geo = geo;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// AbstractApplication.debug(geo.getLabelSimple());
			/*
			 * geo.setHighlighted(true); app.getKernel().notifyRepaint();
			 */
			view.getEuclidianController().doSingleHighlighting(geo);
		}

		/*
		 * @Override public void mouseExited(MouseEvent e) {
		 * AbstractApplication.debug(geo.getLabelSimple());
		 * geo.setHighlighted(false); app.getKernel().notifyRepaint();
		 * 
		 * }
		 */
	}

	@Override
	protected void setTitle(String str) {

		AbstractAction titleAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				if (selectedGeos.size() < 2) {
					if (EuclidianConstants.isMoveOrSelectionMode(view.getMode())) { // change
																			// selection
																			// to
																			// geo
																			// clicked

						app.getSelectionManager().clearSelectedGeos(false); // repaint
																			// done
																			// next
																			// step
						app.getSelectionManager().addSelectedGeo(getGeo());

					} else { // use geo clicked to process mode
						Hits hits = new Hits();
						hits.add(getGeo());
						view.getEuclidianController().processMode(hits, false);
					}
				}

			}
		};

		JMenuItem title = wrappedPopup.add(titleAction);
		title.setText(str);
		title.setFont(((AppD) app).getBoldFont());
		title.setBackground(bgColor);
		title.setForeground(fgColor);

		title.setIcon(((AppD) app).getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 15));

	}

}
