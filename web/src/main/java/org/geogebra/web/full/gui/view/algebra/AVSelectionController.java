package org.geogebra.web.full.gui.view.algebra;

import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

/**
 * Selection controller for AV
 * 
 * @author Laszlo
 */
public class AVSelectionController {
	/** Selection mode */
	public enum SelectMode {
		/** single select */
		Single,
		/** ctrl-select */
		Toggle,
		/** shift-select */
		Continuous,
		/** none */
		None
	}

	private GeoElement lastSelectedGeo;
	private App app;
	private SelectionManager selection;
	private boolean selectHandled;
	private SelectMode lastMode;

	/**
	 * @param app
	 *            application
	 */
	public AVSelectionController(App app) {
		this.app = app;
		selection = app.getSelectionManager();
		setLastSelectedGeo(null);
		lastMode = SelectMode.None;
	}

	private void singleSelect(GeoElement geo) {
		lastMode = SelectMode.Single;
		selection.clearSelectedGeos(false); // repaint will be done
		// next step
		selection.addSelectedGeo(geo);
		setLastSelectedGeo(geo);
	}

	private void toggleSelect(GeoElement geo) {
		lastMode = SelectMode.Toggle;
		selection.toggleSelectedGeo(geo);
		if (contains(geo)) {
			setLastSelectedGeo(geo);
		} else {
			setLastSelectedGeo(null);

		}
	}

	private void continuousSelect(GeoElement geo) {
		SortMode sortMode = app.getSettings().getAlgebra().getTreeMode();
		switch (sortMode) {
		case LAYER:
			break;
		case ORDER:
			continuous(geo,
					app.getKernel().getConstruction()
							.getGeoSetConstructionOrder(),
					geo.getConstructionIndex() < getLastSelectedGeo()
							.getConstructionIndex());
			break;
		case TYPE:
			continuous(geo,
					app.getKernel().getConstruction().getGeoSetLabelOrder(),
					geo.getLabel(StringTemplate.defaultTemplate)
							.compareTo(getLastSelectedGeo().getLabel(
									StringTemplate.defaultTemplate)) < 0);
			break;
		case DEPENDENCY:

		default:
			continuousDependent(geo);
		}
		setLastMode(SelectMode.Continuous);
	}

	private void continuous(GeoElement geo0, TreeSet<GeoElement> geoSet,
			boolean direction) {
		ensureClearLastSelection();

		GeoElement geoFrom = direction ? geo0 : getLastSelectedGeo();
		GeoElement geoTo = direction ? getLastSelectedGeo() : geo0;

		for (GeoElement geo : geoSet.subSet(geoFrom, true, geoTo, true)) {
			selection.toggleSelectedGeo(geo);
		}
	}

	private void continuousDependent(GeoElement geo) {
		boolean nowSelecting = true;
		boolean selecting = false;
		boolean aux = geo.isAuxiliaryObject();
		boolean ind = geo.isIndependent();

		boolean aux2 = getLastSelectedGeo().isAuxiliaryObject();
		boolean ind2 = getLastSelectedGeo().isIndependent();

		ensureClearLastSelection();

		if ((aux == aux2 && aux) || (aux == aux2 && ind == ind2)) {
			boolean direction = geo.getLabel(StringTemplate.defaultTemplate)
					.compareTo(getLastSelectedGeo()
							.getLabel(StringTemplate.defaultTemplate)) < 0;

			for (GeoElement geo2 : app.getKernel().getConstruction().getGeoSetLabelOrder()) {
				if ((geo2.isAuxiliaryObject() == aux && aux)
						|| (geo2.isAuxiliaryObject() == aux
						&& geo2.isIndependent() == ind)) {

					if (direction && geo2.equals(getLastSelectedGeo())) {
						selecting = !selecting;
					}
					if (!direction && geo2.equals(geo)) {
						selecting = !selecting;
					}

					if (selecting) {
						selection.toggleSelectedGeo(geo2);
						nowSelecting = selection.getSelectedGeos()
								.contains(geo2);
					}
					if (!direction && geo2.equals(getLastSelectedGeo())) {
						selecting = !selecting;
					}
					if (direction && geo2.equals(geo)) {
						selecting = !selecting;
					}
				}
			}
		}

		if (nowSelecting) {
			setLastSelectedGeo(geo);
		} else {
			selection.removeSelectedGeo(getLastSelectedGeo());
			setLastSelectedGeo(null);
		}
	}

	private void ensureClearLastSelection() {
		if (lastMode != SelectMode.Continuous || lastMode != SelectMode.None) {
			selection.clearSelectedGeos();
		}

	}

	/**
	 * Selecting the GeoElement
	 * 
	 * @param geo
	 *            GeoElement to be selected/toggled.
	 * @param toggle
	 *            If selection should be toggled (Control-select).
	 * @param continuous
	 *            if selection is continuous (Shift-select)
	 */
	public void select(GeoElement geo, boolean toggle, boolean continuous) {
		if (toggle) {
			toggleSelect(geo);
		} else if (continuous && getLastSelectedGeo() != null) {
			selection.clearSelectedGeos();
			continuousSelect(geo);
		} else {
			singleSelect(geo);
		}
	}

	/**
	 * @return last selected geo
	 */
	public GeoElement getLastSelectedGeo() {
		return lastSelectedGeo;
	}

	/**
	 * @param lastSelectedGeo
	 *            last selected geo
	 */
	public void setLastSelectedGeo(GeoElement lastSelectedGeo) {
		this.lastSelectedGeo = lastSelectedGeo;
	}

	/**
	 * Clear global selection
	 */
	public void clear() {
		selection.clearSelectedGeos();
	}

	/**
	 * 
	 * @return if a multiselect is happening (with Shift or Control)
	 */
	public boolean isMultiSelect() {

		return GlobalKeyDispatcherW.getControlDown();
	}

	/**
	 * @return !hasMultGeos()
	 */
	public boolean isSingleGeo() {
		return selection.getSelectedGeos().size() == 1;
	}

	/**
	 * @return whether more than 1 geo selected
	 */
	public boolean hasMultGeos() {
		return selection.getSelectedGeos().size() > 1;
	}

	/**
	 * @return selected geos
	 */
	public List<GeoElement> getSelectedGeos() {
		return selection.getSelectedGeos();
	}

	/**
	 * @return whether some geos are selected
	 */
	public boolean isEmpty() {
		return selection.getSelectedGeos().isEmpty();
	}

	/**
	 * @return whether selection of UI is in sync
	 */
	public boolean isSelectHandled() {
		return selectHandled;
	}

	/**
	 * @param selectHandled
	 *            whether selection of UI is in sync
	 */
	public void setSelectHandled(boolean selectHandled) {
		this.selectHandled = selectHandled;
	}

	/**
	 * @param geo
	 *            element
	 * @return whether geo is selected
	 */
	public boolean contains(GeoElement geo) {
		return selection.getSelectedGeos().contains(geo);
	}

	/**
	 * @return last selection mode
	 */
	public SelectMode getLastMode() {
		return lastMode;
	}

	private void setLastMode(SelectMode lastMode) {
		this.lastMode = lastMode;
	}
}
