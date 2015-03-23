package geogebra.common.plugin;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;

import java.util.HashMap;
import java.util.Iterator;

public abstract class SensorLogger {

	@SuppressWarnings("javadoc")
	protected Kernel kernel;

	/**
	 * port to receive UDP logging on
	 */
	public static int port = 7166;
	public static String appID = "ABCD";
	public boolean oldUndoActive = false;

	protected static enum Types {
			TIMESTAMP("time"), ACCELEROMETER_X("Ax"), ACCELEROMETER_Y("Ay"), ACCELEROMETER_Z(
					"Az"), ORIENTATION_X("Ox"), ORIENTATION_Y("Oy"), ORIENTATION_Z(
					"Oz"), MAGNETIC_FIELD_X("Mx"), MAGNETIC_FIELD_Y("My"), MAGNETIC_FIELD_Z(
					"Mz"), DATA_COUNT("datacount"), EDAQ0("EDAQ0"), EDAQ1("EDAQ1"), EDAQ2(
				"EDAQ2"), PORT("port"), APP_ID("appID");
			private String string;
	
			Types(String s) {
				this.string = s;
			}
	
			public static Types lookup(String s) {
				for (Types type : Types.values()) {
					if (type.string.equals(s)) {
						return type;
					}
				}
	
				return null;
			}
		}

	protected HashMap<Types, GeoNumeric> listeners = new HashMap<Types, GeoNumeric>();
	protected HashMap<Types, GeoList> listenersL = new HashMap<Types, GeoList>();
	protected HashMap<Types, Integer> listLimits = new HashMap<Types, Integer>();
	protected HashMap<Types, Integer> listenersAges = new HashMap<Types, Integer>();
	protected long now;

	public abstract boolean startLogging();

	protected abstract void closeSocket();


	public void registerGeo(String s, GeoElement geo) {
	
		Types type = Types.lookup(s);
	
		if (type != null) {
			if (type == Types.PORT) {
				port = (int) ((GeoNumeric) geo).getValue();
			} else if (type == Types.APP_ID) {
				appID = ((GeoText) geo).getTextString();
			} else {
				App.debug("logging " + type + " to " + geo.getLabelSimple());
				listenersL.remove(type);
				listLimits.put(type, 0);
				listeners.put(type, (GeoNumeric) geo);
				listenersAges.put(type, 0);
			}
		}
	}

	public void registerGeoList(String s, GeoList list) {
		registerGeoList(s, list, 0);
	}

	public void registerGeoList(String s, GeoList list, double limit) {
		Types type = Types.lookup(s);
	
		if (type != null) {
			App.debug("logging " + type + " to " + list.getLabelSimple());
			listeners.remove(type);
			listenersL.put(type, list);
			listenersAges.put(type, 0);
	
			int lim = (int) Math.round(limit);
			if (lim < 0) {
				lim = 0;
			}
			listLimits.put(type, lim);
		}
	}

	public void stopLogging() {
	
		kernel.setUndoActive(oldUndoActive);
		kernel.storeUndoInfo();
	
		closeSocket();
	
		listeners.clear();
		listenersL.clear();
		listenersAges.clear();
	}

	protected void initStartLogging() {
		now = System.currentTimeMillis();
	
		App.debug("startLogging called, undoActive is: "
				+ kernel.isUndoActive());
		// make sure that running StartLogging twice does not switch undo off
		oldUndoActive = oldUndoActive || kernel.isUndoActive();
	
		kernel.setUndoActive(false);
	
		App.debug("undoActive is: " + kernel.isUndoActive());
	}

	protected void log(Types type, double val) {
		log(type, val, true, true, true);
	}

	protected void log(Types type, double val, boolean repaint, boolean update,
			boolean atleast) {
				GeoNumeric geo = listeners.get(type);
				if (geo != null) {
			
					// if (repaint)
					App.debug(type + ": " + val);
			
					// If we do not want to repaint, probably logging
					// should be avoided as well...
			
					geo.setValue(val);
			
					if (repaint)
						geo.updateRepaint();
					else if (update || !atleast)
						geo.updateCascade();
					else
						geo.update(); // at least call updateScripts
			
					registerLog(type);
				} else {
					GeoList list = listenersL.get(type);
					if (list != null) {
						App.debug(type + ": " + val);
			
						geo = new GeoNumeric(list.getConstruction(), val);
			
						Integer ll = listLimits.get(type);
						if (ll == null || ll == 0 || ll > list.size()) {
							list.add(geo);
						} else {
							list.addQueue(geo);
						}
			
						if (repaint)
							list.updateRepaint();
						else if (update || !atleast)
							list.updateCascade();
						else
							list.update(); // at least call updateScripts
			
						registerLog(type);
					}
				}
			}

	private void registerLog(Types type) {
		Types thistype;
		GeoNumeric geo;
		GeoList list;
		int referenceAge = listenersAges.get(type);
		listenersAges.put(type, referenceAge + 1);
		referenceAge = listenersAges.get(type);
	
		int numOld = 0;
		int numAll = 0;
		// ages grow, and too little ages have to keep pace
		Iterator it = listenersAges.keySet().iterator();
		while (it.hasNext()) {
			thistype = (Types) it.next();
			Integer age = listenersAges.get(thistype);
			if (age > 100) {
				numOld++;
			}
			numAll++;
			if (referenceAge > age + 1) {
				// grow the intermediates as well
				listenersAges.put(thistype, age + 1);
	
				geo = listeners.get(thistype);
				if (geo != null) {
					geo.update();
				} else {
					list = listenersL.get(thistype);
					if (list != null) {
						list.update();
					}
				}
			}
		}
	
		if (numOld == numAll) {
			// we can decrease the ages of all
			it = listenersAges.keySet().iterator();
			while (it.hasNext()) {
				thistype = (Types) it.next();
				Integer age = listenersAges.get(thistype);
				listenersAges.put(thistype, age - 100);
			}
		}
	}

}
