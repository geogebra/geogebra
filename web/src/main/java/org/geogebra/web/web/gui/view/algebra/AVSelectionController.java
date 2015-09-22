package org.geogebra.web.web.gui.view.algebra;

import java.util.Iterator;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

public class AVSelectionController {
	public enum SelectMode {
		Single, Toggle, Continuous, None
	};

	private GeoElement selectedGeo;
	private GeoElement lastSelectedGeo;
	private App app;
	private SelectionManager selection;
	private boolean selectHandled;
	private SelectMode lastMode;

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
		boolean nowSelecting = true;
		boolean selecting = false;
		boolean aux = geo.isAuxiliaryObject();
		boolean ind = geo.isIndependent();

		boolean aux2 = getLastSelectedGeo().isAuxiliaryObject();
		boolean ind2 = getLastSelectedGeo().isIndependent();
		if (lastMode != SelectMode.Continuous) {
			selection.clearSelectedGeos(true);
			selection.addSelectedGeo(getLastSelectedGeo());
			selection.addSelectedGeo(geo);
		}

		if ((aux == aux2 && aux) || (aux == aux2 && ind == ind2)) {
			Iterator<GeoElement> it = app.getKernel().getConstruction()
					.getGeoSetLabelOrder().iterator();
			boolean direction = geo.getLabel(StringTemplate.defaultTemplate)
					.compareTo(getLastSelectedGeo()
							.getLabel(StringTemplate.defaultTemplate)) < 0;

			while (it.hasNext()) {
				GeoElement geo2 = it.next();
				if ((geo2.isAuxiliaryObject() == aux && aux)
						|| (geo2.isAuxiliaryObject() == aux
								&& geo2.isIndependent() == ind)) {

					if (direction && geo2.equals(getLastSelectedGeo()))
						selecting = !selecting;
					if (!direction && geo2.equals(geo))
						selecting = !selecting;

					if (selecting) {
						selection.toggleSelectedGeo(geo2);
						nowSelecting = selection.getSelectedGeos()
								.contains(geo2);
					}
					if (!direction && geo2.equals(getLastSelectedGeo()))
						selecting = !selecting;
					if (direction && geo2.equals(geo))
						selecting = !selecting;
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

	public GeoElement getLastSelectedGeo() {
		return lastSelectedGeo;
	}

	public void setLastSelectedGeo(GeoElement lastSelectedGeo) {
		this.lastSelectedGeo = lastSelectedGeo;
	}

	public GeoElement getSelectedGeo() {
		return selectedGeo;
	}

	public void setSelectedGeo(GeoElement selectedGeo) {
		this.selectedGeo = selectedGeo;
	}

	public void clear() {
		selection.clearSelectedGeos();
	}

	/**
	 * 
	 * @return if a multiselect is happening (with Shift or Control)
	 */
	public boolean isMultiSelect() {
		GlobalKeyDispatcherW gk = (GlobalKeyDispatcherW) app
				.getGlobalKeyDispatcher();
		return gk.getControlDown();
	}

	public boolean isSingleGeo() {
		return selection.getSelectedGeos().size() == 1;
	}

	public boolean isEmpty() {
		return selection.getSelectedGeos().isEmpty();
	}

	public boolean isSelectHandled() {
		return selectHandled;
	}

	public void setSelectHandled(boolean selectHandled) {
		this.selectHandled = selectHandled;
	}

	public boolean contains(GeoElement geo) {
		return selection.getSelectedGeos().contains(geo);
	}

	public SelectMode getLastMode() {
		return lastMode;
	}

	public void setLastMode(SelectMode lastMode) {
		this.lastMode = lastMode;
	}
}
