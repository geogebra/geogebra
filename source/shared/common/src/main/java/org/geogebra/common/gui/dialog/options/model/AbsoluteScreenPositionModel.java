package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.debug.Log;

public abstract class AbsoluteScreenPositionModel extends TextPropertyModel {

	public interface PositionListener extends PropertyListener {

		void setLocation(int x);
	}

	public static class ForX extends AbsoluteScreenPositionModel {
		public ForX(App app) {
			super(app);
		}

		@Override
		protected int getIndex() {
			return 0;
		}

		@Override
		public String getTitle() {
			return "x";
		}

		@Override
		protected void setCoord(AbsoluteScreenLocateable geo, int coord) {
			geo.setAbsoluteScreenLoc(coord, geo.getAbsoluteScreenLocY());
		}

		@Override
		public String getCoord(AbsoluteScreenLocateable abs) {
			return abs.getAbsoluteScreenLocX() + "";
		}

		@Override
		protected String getCoord(MyVecNode geoAt) {
			return geoAt.getX().toString(StringTemplate.editTemplate);
		}
	}

	public static class ForY extends AbsoluteScreenPositionModel {
		public ForY(App app) {
			super(app);
		}

		@Override
		protected int getIndex() {
			return 1;
		}

		@Override
		public String getTitle() {
			return "y";
		}

		@Override
		protected void setCoord(AbsoluteScreenLocateable geo, int coord) {
			geo.setAbsoluteScreenLoc(geo.getAbsoluteScreenLocX(), coord);
		}

		@Override
		public String getCoord(AbsoluteScreenLocateable abs) {
			return abs.getAbsoluteScreenLocY() + "";
		}

		@Override
		protected String getCoord(MyVecNode geoAt) {
			return geoAt.getY().toString(StringTemplate.editTemplate);
		}
	}

	protected AbsoluteScreenPositionModel(App app) {
		super(app);
	}

	@Override
	protected boolean isValidAt(int index) {
		return getGeoAt(index) instanceof AbsoluteScreenLocateable
				&& ((AbsoluteScreenLocateable) getGeoAt(index)).isAbsoluteScreenLocActive();
	}

	@Override
	public void applyChanges(GeoNumberValue value, String str) {
		if (value != null) {
			for (GeoElement geo : getGeosAsList()) {
				MyVecNode def = getPositionDef(geo);
				String[] newDef = def == null ? new String[] {
						((AbsoluteScreenLocateable) geo).getAbsoluteScreenLocX() + "",
						((AbsoluteScreenLocateable) geo).getAbsoluteScreenLocY() + "",
				} : new String[] {
						def.getX().toString(StringTemplate.editTemplate),
						def.getY().toString(StringTemplate.editTemplate)
				};
				newDef[getIndex()] = str;
				GeoPointND eval = app.getKernel().getAlgebraProcessor().evaluateToPoint(
						"(" + String.join(",", newDef) + ")", ErrorHelper.silent(), true);

				if (Inspecting.isDynamicGeoElement(eval)) {
					try {
						((AbsoluteScreenLocateable) geo).setStartPoint(eval);
					} catch (CircularDefinitionException e) {
						Log.warn(e);
					}
				} else {
					((AbsoluteScreenLocateable) geo).setAbsoluteScreenLoc((int) eval.getInhomX(),
							(int) eval.getInhomY());
				}
				geo.updateVisualStyleRepaint(GProperty.POSITION);
			}
			storeUndoInfo();
		}
	}

	protected abstract int getIndex();

	protected abstract void setCoord(AbsoluteScreenLocateable geo, int coord);

	@Override
	public String getText() {
		AbsoluteScreenLocateable temp, geo0 = (AbsoluteScreenLocateable) getGeoAt(0);
		boolean equalCoord = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = (AbsoluteScreenLocateable) getGeoAt(i);
			if (!getCoordStr(geo0).equals(getCoordStr(temp))) {
				equalCoord = false;
			}
		}

		if (equalCoord) {
			return getCoordStr(geo0);
		} else {
			return "";
		}
	}

	private String getCoordStr(AbsoluteScreenLocateable abs) {
		MyVecNode def = getPositionDef(abs);
		return def != null ? getCoord(def) : getCoord(abs);
	}

	private MyVecNode getPositionDef(GeoElementND abs) {
		if (abs instanceof Locateable) {
			GeoPointND sp = ((Locateable) abs).getStartPoint();
			if (sp != null && sp.getDefinition() != null
					&& sp.getDefinition().unwrap() instanceof MyVecNode) {
				return (MyVecNode) sp.getDefinition().unwrap();
			}
		}
		return null;
	}

	protected abstract String getCoord(AbsoluteScreenLocateable geoAt);

	protected abstract String getCoord(MyVecNode geoAt);
}
