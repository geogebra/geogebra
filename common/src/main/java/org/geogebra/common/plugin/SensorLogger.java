package org.geogebra.common.plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.debug.Log;

public abstract class SensorLogger {

	public static final int DEFAULT_LIMIT = 20000;

	/** kernel */
	protected final Kernel kernel;

	/**
	 * port to receive UDP logging on
	 */
	private int port = 7166;
	public String appID = "ABCD";
	public boolean oldUndoActive = false;

	public static enum Types {
		// DON'T CHANGE STRINGS - USED IN XML FOR DATA COLLECTION VIEW
		TIMESTAMP("time"),

		ACCELEROMETER_X("Ax", true),

		ACCELEROMETER_Y("Ay", true),

		ACCELEROMETER_Z("Az", true),

		ORIENTATION_X("Ox", true),

		ORIENTATION_Y("Oy", true),

		ORIENTATION_Z("Oz", true),

		MAGNETIC_FIELD_X("Mx", true),

		MAGNETIC_FIELD_Y("My", true),

		MAGNETIC_FIELD_Z("Mz", true),

		PROXIMITY("proximity", true),

		LIGHT("light", true),

		LOUDNESS("loudness", true),

		DATA_COUNT("datacount"),

		EDAQ0("EDAQ0", true),

		EDAQ1("EDAQ1", true),

		EDAQ2("EDAQ2", true),

		PORT("port", true),

		APP_ID("appID"),

		MOBILE_FOUND("mobile_found"),

		FREQUENCY("frequency");

		private String string;
		private boolean storeInXML = false;

		Types(String s) {
			this.string = s;
		}

		Types(String s, boolean storeInXML) {
			this.string = s;
			this.storeInXML = storeInXML;
		}

		@Override
		public String toString() {
			return string;
		}

		public String toXMLString() {
			return string;
		}

		/**
		 * @param s
		 *            type name
		 * @return type with given name
		 */
		public static Types lookup(String s) {
			for (Types type : Types.values()) {
				if (type.string.equals(s)) {
					return type;
				}
			}

			return null;
		}

		public boolean storeInXML() {
			return storeInXML;
		}
	}

	protected HashMap<Types, GeoNumeric> listeners = new HashMap<>();
	protected HashMap<Types, GeoList> listenersL = new HashMap<>();
	protected HashMap<Types, GeoFunction> listenersF = new HashMap<>();
	protected HashMap<Types, Integer> listLimits = new HashMap<>();
	protected HashMap<Types, Integer> listenersAges = new HashMap<>();

	private int stepsToGo = DEFAULT_LIMIT;

	protected SensorLogger(Kernel kernel) {
		this.kernel = kernel;
	}

	protected int getUDPPort() {
		return port;
	}

	public abstract boolean startLogging();

	protected abstract void closeSocket();

	/**
	 * @param s
	 *            sensor type name
	 * @param geo
	 *            logging object
	 */
	public void registerGeo(String s, GeoElement geo) {
		Types type = Types.lookup(s);

		if (type != null) {
			if (type == Types.PORT) {
				port = (int) ((GeoNumeric) geo).getValue();
			} else if (type == Types.APP_ID) {
				appID = ((GeoText) geo).getTextString();
				Log.debug(appID);
			} else {
				prepareRegister(type, geo, 0);
				listeners.put(type, (GeoNumeric) geo);
			}
		}
	}

	/**
	 * Decrease the count of remaining steps
	 */
	protected void beforeLog() {
		stepsToGo--;
	}

	/**
	 * @param sensor
	 *            {@link Types}
	 */
	public void removeRegisteredGeo(Types sensor) {
		listeners.remove(sensor);
		listenersL.remove(sensor);
		listenersF.remove(sensor);
		listenersAges.remove(sensor);
		listLimits.remove(sensor);
	}

	/**
	 * @param geo
	 *            {@link GeoElement}
	 */
	public void removeRegisteredGeo(GeoElement geo) {
		Types typeToRemove = null;
		if (geo instanceof GeoNumeric) {
			for (Entry<Types, GeoNumeric> entry : this.listeners.entrySet()) {
				Types type = entry.getKey();
				if (entry.getValue() == geo) {
					typeToRemove = type;
				}
			}
		} else if (geo instanceof GeoList) {
			for (Entry<Types, GeoList> entry : this.listenersL.entrySet()) {
				Types type = entry.getKey();
				if (entry.getValue() == geo) {
					typeToRemove = type;
				}
			}
		} else if (geo instanceof GeoFunction) {
			for (Entry<Types, GeoFunction> entry : this.listenersF.entrySet()) {
				Types type = entry.getKey();
				if (entry.getValue() == geo) {
					typeToRemove = type;
				}
			}
		}
		if (typeToRemove != null) {
			removeRegisteredGeo(typeToRemove);
		}
	}

	private void prepareRegister(Types type, GeoElement geo, double limit) {
		Log.debug("logging " + type + " to " + geo.getLabelSimple());
		listenersL.remove(type);
		listeners.remove(type);
		listenersF.remove(type);
		int lim = (int) Math.round(limit);
		if (lim < 0) {
			lim = 0;
		}
		listLimits.put(type, lim);
		listenersAges.put(type, 0);
	}

	public void registerGeoList(String s, GeoList list) {
		registerGeoList(s, list, 0);
	}

	public void registerGeoFunction(String s, GeoFunction list) {
		registerGeoFunction(s, list, 0);
	}

	/**
	 * @param s
	 *            sensor type name
	 * @param function
	 *            logging function
	 * @param limit
	 *            max number of values
	 */
	public void registerGeoFunction(String s, GeoFunction function,
			double limit) {
		Types type = Types.lookup(s);

		if (type != null) {
			this.prepareRegister(type, function, limit);
			listenersF.put(type, function);
		}
	}

	/**
	 * @param s
	 *            sensor type name
	 * @param list
	 *            logging list
	 * @param limit
	 *            max number of values
	 */
	public void registerGeoList(String s, GeoList list, double limit) {
		Types type = Types.lookup(s);

		if (type != null) {
			this.prepareRegister(type, list, limit);
			listenersL.put(type, list);

		}
	}

	/**
	 * Stop logging.
	 */
	public void stopLogging() {
		kernel.setUndoActive(oldUndoActive);
		kernel.storeUndoInfo();

		closeSocket();
		listeners.clear();
		listenersL.clear();
		listenersF.clear();
		listenersAges.clear();
	}

	protected void initStartLogging() {
		Log.debug(
				"startLogging called, undoActive is: " + kernel.isUndoActive());
		// make sure that running StartLogging twice does not switch undo off
		oldUndoActive = oldUndoActive || kernel.isUndoActive();

		kernel.setUndoActive(false);

		Log.debug("undoActive is: " + kernel.isUndoActive());
	}

	protected void log(Types type, double timestamp, double val) {
		log(type, timestamp, val, true, true, true);
	}

	protected void log(Types type, double timestamp, double val,
			boolean repaint, boolean update, boolean atleast) {
		if (stepsToGo <= 0) {
			return;
		}
		GeoNumeric geo = listeners.get(type);
		if (geo != null) {

			// if (repaint)

			// If we do not want to repaint, probably logging
			// should be avoided as well...

			geo.setValue(val);

			if (repaint) {
				geo.updateRepaint();
			} else if (update || !atleast) {
				geo.updateCascade();
			}
			else {
				geo.update(); // at least call updateScripts
			}

			registerLog(type);
		} else {
			GeoList list = listenersL.get(type);
			if (list != null) {

				geo = new GeoNumeric(list.getConstruction(), val);

				Integer ll = listLimits.get(type);
				if (ll == null || ll == 0 || ll > list.size()) {
					list.add(geo);
				} else {
					list.addQueue(geo);
				}

				if (repaint) {
					list.updateRepaint();
				} else if (update || !atleast) {
					list.updateCascade();
				}
				else {
					list.update(); // at least call updateScripts
				}

				registerLog(type);
			} else {
				GeoFunction fn = listenersF.get(type);
				if (fn == null) {
					return;
				}
				ExpressionValue ev = fn.getFunctionExpression().unwrap().wrap()
						.getRight();

				if (ev instanceof MyNumberPair
						&& ((MyNumberPair) ev).getX() instanceof MyList
						&& ((MyNumberPair) ev).getY() instanceof MyList) {
					MyList mx = (MyList) ((MyNumberPair) ev).getX();
					MyList my = (MyList) ((MyNumberPair) ev).getY();
					Integer ll = listLimits.get(type);
					if (mx.size() > 0 && timestamp < mx
							.getListElement(mx.size() - 1).evaluateDouble()) {
						mx.clear();
						my.clear();
					}
					if (ll == null || ll == 0 || ll + 2 > mx.size()) {
						mx.addListElement(new MyDouble(kernel, timestamp));
						my.addListElement(new MyDouble(kernel, val));
					} else {
						mx.addQue(timestamp, 0);
						my.addQue(val, 0);
					}

				}

				if (repaint) {
					fn.updateRepaint();
				} else if (update || !atleast) {
					fn.updateCascade();
				}
				else {
					fn.update(); // at least call updateScripts
				}

				registerLog(type);
			}
		}
	}

	private void registerLog(Types type) {
		Types thistype;
		GeoNumeric geo;
		GeoList list;
		GeoFunction function;
		int referenceAge = listenersAges.get(type);
		listenersAges.put(type, referenceAge + 1);
		referenceAge = listenersAges.get(type);

		int numOld = 0;
		int numAll = 0;
		// ages grow, and too little ages have to keep pace
		Iterator<Entry<Types, Integer>> it = listenersAges.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<Types, Integer> entry = it.next();
			thistype = entry.getKey();
			Integer age = entry.getValue();
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
					} else {
						function = listenersF.get(thistype);
						if (function != null) {
							function.update();
						}
					}
				}
			}
		}

		if (numOld == numAll) {
			// we can decrease the ages of all
			it = listenersAges.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Types, Integer> entry = it.next();
				thistype = entry.getKey();
				Integer age = entry.getValue();
				listenersAges.put(thistype, age - 100);
			}
		}
	}

	/**
	 * @param limit
	 *            step limit
	 */
	public void setLimit(double limit) {
		this.stepsToGo = (int) limit;
	}

}