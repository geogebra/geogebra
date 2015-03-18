package geogebra.plugin;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;
import geogebra.common.plugin.SensorLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author michael
 * 
 *         class to listen to UDP packets and then eg update slider with the
 *         values
 * 
 */
public class UDPLoggerD implements SensorLogger {

	@SuppressWarnings("javadoc")
	Kernel kernel;

	static enum Types {
		TIMESTAMP("time"), ACCELEROMETER_X("Ax"), ACCELEROMETER_Y("Ay"), ACCELEROMETER_Z(
				"Az"), ORIENTATION_X("Ox"), ORIENTATION_Y("Oy"), ORIENTATION_Z(
				"Oz"), MAGNETIC_FIELD_X("Mx"), MAGNETIC_FIELD_Y("My"), MAGNETIC_FIELD_Z(
				"Mz"), DATA_COUNT("datacount"), EDAQ0("EDAQ0"), EDAQ1("EDAQ1"), EDAQ2(
				"EDAQ2"), PORT("port");
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

	HashMap<Types, GeoNumeric> listeners = new HashMap<Types, GeoNumeric>();
	HashMap<Types, GeoList> listenersL = new HashMap<Types, GeoList>();
	HashMap<Types, Integer> listLimits = new HashMap<Types, Integer>();
	HashMap<Types, Integer> listenersAges = new HashMap<Types, Integer>();

	/**
	 * port to receive UDP logging on
	 */
	public static int port = 7166;

	@SuppressWarnings("javadoc")
	Thread thread;

	@SuppressWarnings("javadoc")
	DatagramSocket dsocket;

	/**
	 * @param kernel
	 *            kernel
	 */
	public UDPLoggerD(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public void stopLogging() {

		kernel.setUndoActive(oldUndoActive);
		kernel.storeUndoInfo();

		if (dsocket != null) {
			dsocket.close();

			// stops thread
			dsocket = null;
		}

		listeners.clear();
		listenersL.clear();
		listenersAges.clear();
	}

	private void log(Types type, double val) {
		log(type, val, true, true, true);
	}

	private void log(Types type, double val, boolean repaint, boolean update,
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

	private float getFloat(byte[] buffer1, int i) {
		byte[] bytes = { buffer1[i], buffer1[i + 1], buffer1[i + 2],
				buffer1[i + 3] };
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}

	public void handleJSON(byte[] buffer, int length, String address,
			boolean quicker) {
		// TODO: convert to-from string by a specific encoding, e.g. UTF-8
		JSONArray ja = new JSONArray(new String(buffer, 0, length));
		JSONObject jo;
		String key;

		if ("EDAQ".equals(ja.getString(0))) {
			// EDAQ 530

			// "EDAQ;{sensor1},{doublebits8};{sensor},{doublebits};{sensor},{doublebits}"...
			// we could even spare the ; and , but still left

			boolean atleast = true;
			for (int bp = 1; bp < ja.length(); bp++, atleast = (bp + 1 < ja
					.length())) {
				jo = ja.getJSONObject(bp);
				key = (String) jo.keys().next();
				switch (Integer.parseInt(key)) {
				case 0:
					log(Types.EDAQ0, jo.getDouble(key), false, !quicker,
							atleast);
					break;
				case 1:
					log(Types.EDAQ1, jo.getDouble(key), false, !quicker,
							atleast);
					break;
				case 2:
					log(Types.EDAQ2, jo.getDouble(key), false, !quicker,
							atleast);
					break;

				default:
					App.error("unknown EDAQ port!");
				}
			}

			// flush repainting of logs!
			kernel.notifyRepaint();
		}
	}

	public void handle(byte[] buffer, int length, String address,
			boolean quicker) {

		// App.debug("undoActive is: " + kernel.isUndoActive());

		byte c0 = buffer[0];
		byte c1 = buffer[1];
		byte c2 = buffer[2];
		byte c3 = buffer[3];
		byte c4 = buffer[4];
		if (c0 == 'F' && c1 == 'S' && c2 == 0x01) {

			// https://itunes.apple.com/gb/app/sensor-data-streamer/id608278214?mt=8
			App.debug("data is from 'Sensor Streamer' (c) 2013 FNI Co LTD");
			log(Types.ACCELEROMETER_X, getFloat(buffer, 4));
			log(Types.ACCELEROMETER_Y, getFloat(buffer, 8));
			log(Types.ACCELEROMETER_Z, getFloat(buffer, 12));

			log(Types.ORIENTATION_X, getFloat(buffer, 16));
			log(Types.ORIENTATION_Y, getFloat(buffer, 20));
			log(Types.ORIENTATION_Z, getFloat(buffer, 24));

			log(Types.MAGNETIC_FIELD_X, getFloat(buffer, 28));
			log(Types.MAGNETIC_FIELD_Y, getFloat(buffer, 36));
			log(Types.MAGNETIC_FIELD_Z, getFloat(buffer, 44));

		} else if (c0 == 'E' && c1 == 'D' && c2 == 'A' && c3 == 'Q') {
			// EDAQ 530

			// "EDAQ;{sensor1},{doublebits8};{sensor},{doublebits};{sensor},{doublebits}"...
			// we could even spare the ; and , but still left

			boolean atleast = true;
			for (int bp = 5; bp < length; bp += 11, atleast = (bp + 11 < length)) {
				// "{sensor1},{doublebits8};"
				// ,,23456789

				if (buffer[bp - 1] != ';') {
					App.error("error in UDP transmission");
				}

				long gotit = 0;
				for (int place = bp + 2, shift = 56; place < bp + 10; place++, shift -= 8) {
					gotit |= ((buffer[place] & 0xFFL) << shift);
				}

				if (buffer[bp + 1] != ',') {
					App.error("error in UDP transmission");
				}

				switch (buffer[bp]) {
				case 0:
					log(Types.EDAQ0, Double.longBitsToDouble(gotit), false,
							!quicker, atleast);
					break;
				case 1:
					log(Types.EDAQ1, Double.longBitsToDouble(gotit), false,
							!quicker, atleast);
					break;
				case 2:
					log(Types.EDAQ2, Double.longBitsToDouble(gotit), false,
							!quicker, atleast);
					break;

				default:
					App.error("unknown EDAQ port!");
				}
			}

			// flush repainting of logs!
			kernel.notifyRepaint();

		} else {

			// https://play.google.com/store/apps/details?id=jp.ac.ehime_u.cite.sasaki.SensorUdp
			App.debug("Assume data is from Android/SensorUDP");

			String msg = new String(buffer, 0, length);

			App.debug(msg);

			String[] split = msg.split(", ");

			switch (buffer[0]) {
			case 'A':

				// App.debug("received" + msg);

				log(Types.ACCELEROMETER_X,
						Double.parseDouble(split[3].replace(",", ".")));
				log(Types.ACCELEROMETER_Y,
						Double.parseDouble(split[4].replace(",", ".")));
				log(Types.ACCELEROMETER_Z,
						Double.parseDouble(split[5].replace(",", ".")));

				break;
			case 'M':

				log(Types.MAGNETIC_FIELD_X,
						Double.parseDouble(split[3].replace(",", ".")));
				log(Types.MAGNETIC_FIELD_Y,
						Double.parseDouble(split[4].replace(",", ".")));
				log(Types.MAGNETIC_FIELD_Z,
						Double.parseDouble(split[5].replace(",", ".")));

				break;
			case 'O':
				log(Types.ORIENTATION_X,
						Double.parseDouble(split[3].replace(",", ".")));
				log(Types.ORIENTATION_Y,
						Double.parseDouble(split[4].replace(",", ".")));
				log(Types.ORIENTATION_Z,
						Double.parseDouble(split[5].replace(",", ".")));

				break;

			default:
				// App.debug("unknown data type: " + buffer[0]);
				// App.debug(msg);
				break;
			}
			// timestamp and data-count logged always
			log(Types.TIMESTAMP, Math.abs(now - Double.parseDouble(split[2])));
			log(Types.DATA_COUNT, Double.parseDouble(split[1]));

			// for (int i = 1; i < split.length; i++) {
			// App.debug(split[i]);
			// }

			// Convert the contents to a string, and display
			// them
			App.debug(address + ": " + msg);
		}
	}

	private boolean oldUndoActive = false;
	private long now;

	@Override
	public boolean startLogging() {

		now = System.currentTimeMillis();

		App.debug("startLogging called, undoActive is: "
				+ kernel.isUndoActive());
		// make sure that running StartLogging twice does not switch undo off
		oldUndoActive = oldUndoActive || kernel.isUndoActive();

		kernel.setUndoActive(false);

		App.debug("undoActive is: " + kernel.isUndoActive());

		// Create a socket to listen on the port.
		try {

			if (dsocket != null) {
				dsocket.close();
			}

			dsocket = new DatagramSocket(port);

			// Don't block more than 3 sec...
			// useful in case the datagram flow stops,
			// or is not yet started at all
			dsocket.setSoTimeout(3000);

		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}

		// Create a buffer to read datagrams into. If a
		// packet is larger than this buffer, the
		// excess will simply be discarded!
		// final byte[] buffer = new byte[2048];

		// EDAQx's internal frequency can be 1000 Hz, and this can send
		// 1000 * 3 * 11 bytes in one second

		// however, it's actually sending the data with pauses of 50ms or more,
		// with not more than 200 * 11 bytes at once, and this gives:
		// a maximum of (1000/50) * (200 * 11) bytes in one second
		// that is, 20 * 200 * 11, or 1000 * 4 * 11... however, the 50ms
		// is more than that because of computation times, so the
		// 1000 * 4 * 11 can be less, and in this case, maybe the software
		// is not able to transmit the 1000 * 3 * 11 in this rate...

		// Possible solution: increase the bytes it can send at once,
		// to its double! Now we can do it, a piece of data needs only
		// 11 bytes, not 33 bytes... however, we should not change the 50ms...

		// so trying 1000 bytes of original buffer instead of 400 in
		// SerialReader.java,
		// and this gives 500 * 11 bytes at once! Plus the EDAQ string.
		// this happens e.g. if the 50ms of waiting time is interrupted
		// by many ms of computer processing time.
		// ??

		// buffer size is computed in SerialReader.java in EDAQx branch
		// this is the maximal possible value (maybe more), but it can be much
		// smaller if the sample rate is less than 1000Hz and there are less
		// sensors
		// used than 3... and it can be also larger if time data will be sent...
		// so in the future, it would be good to get the sample rate frequency
		// data and compute the buffer size dynamically here (especially for
		// old machines)
		final byte[] buffer = new byte[15551];

		// of course, this will only be used when needed, and anyway, 6 KB
		// is not much in the age of kilobytes and megabytes...

		// Create a packet to receive data into the buffer
		final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		// Now loop forever, waiting to receive packets and printing them.

		// if this changes, another thread has started -> terminate this one
		final DatagramSocket socketCopy = dsocket;

		// Maybe important!!! We should neglect all information from the sensors
		// coming from the past! Because it may happen that the sensors were
		// switched on much earlier than we switched on receiving their data,
		// and in this case, so much lag can be introduced which cannot be
		// conquered in a reasonable time. So, we should storno the socket
		// somehow, discard all datagram packets already sent... but how?

		// This doesn't work:
		/*
		 * try { dsocket.receive(packet); while (packet.getLength() > 0) { if
		 * (socketCopy != dsocket) { dsocket.close(); break; } try {
		 * dsocket.receive(packet); } catch (Exception ex) { dsocket.close();
		 * break; } } } catch (Exception ex) { dsocket.close(); }
		 * 
		 * if (dsocket.isClosed()) { // do not even start the Thread return
		 * false; }
		 */


		thread = new Thread() {


			@Override
			public void run() {

				App.debug("thread starting");

				while (socketCopy == dsocket) {
					// Wait to receive a datagram
					try {
						App.debug("waiting");
						dsocket.receive(packet);
					} catch (IOException e) {
						if (dsocket != null)
							dsocket.close();
						dsocket = null;
						App.debug("logging failed");

						// stoplogging also drops exception here, so no need
						// error message if
						// stoplogging called
						if (e instanceof SocketTimeoutException) {
							kernel.getApplication().showError(
									kernel
									.getApplication().getPlain("LoggingError"));
						}

						e.printStackTrace();
					}

					if (socketCopy == dsocket) {
						synchronized (kernel.getConcurrentModificationLock()) {

							// final byte[] bufferCopy = buffer.clone();
							final int length = packet.getLength();

							// SwingUtilities.invokeLater(new Runnable() {
							// public void run() {
							if ("[".getBytes()[0] == buffer[0]) {
								handleJSON(buffer, length, packet.getAddress()
										.getHostAddress()
										+ " "
										+ packet.getAddress().getHostName(),
										false);
							} else {
								handle(buffer, length, packet.getAddress()
										.getHostAddress()
										+ " "
										+ packet.getAddress().getHostName(),
										false);
							}
							// }
							// });
							// Reset the length of the packet before reusing
							// it.
							packet.setLength(buffer.length);

						}
					}

				}

				if (dsocket != null)
					dsocket.close();
				App.debug("thread ending");
			}

			private float getInt(byte[] buffer1, int i) {
				byte[] bytes = { buffer1[i], buffer1[i + 1], buffer1[i + 2],
						buffer1[i + 3] };
				return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
						.getInt();
			}

			private double getDouble(byte[] buffer1, int i) {
				byte[] bytes = { buffer1[i], buffer1[i + 1], buffer1[i + 2],
						buffer1[i + 3], buffer1[i + 4], buffer1[i + 5],
						buffer1[i + 6], buffer1[i + 7] };
				return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
						.getDouble();
			}
		};
		thread.start();

		return true;

	}

	public void registerGeo(String s, GeoNumeric geo) {

		Types type = Types.lookup(s);

		if (type != null) {
			if (type == Types.PORT) {
				port = (int) geo.getValue();
			} else {
				App.debug("logging " + type + " to " + geo.getLabelSimple());
				listenersL.remove(type);
				listLimits.put(type, 0);
				listeners.put(type, geo);
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
}
