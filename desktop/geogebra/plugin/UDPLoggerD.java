package geogebra.plugin;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;
import geogebra.common.plugin.UDPLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

/**
 * @author michael
 * 
 *         class to listen to UDP packets and then eg update slider with the
 *         values
 * 
 */
public class UDPLoggerD implements UDPLogger {

	@SuppressWarnings("javadoc")
	Kernel kernel;

	static enum Types {
		TIMESTAMP("time"), ACCELEROMETER_X("Ax"), ACCELEROMETER_Y("Ay"), ACCELEROMETER_Z(
				"Az");

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

	private HashMap<Types, GeoNumeric> listeners = new HashMap<Types, GeoNumeric>();

	/**
	 * port to receive UDP logging on
	 */
	final public static int port = 7166;

	@SuppressWarnings("javadoc")
	Thread thread;

	@SuppressWarnings("javadoc")
	boolean running;

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

		App.debug("killing thread");

		running = false;

		thread = null;
	}

	@Override
	public boolean startLogging() {

		// Create a socket to listen on the port.
		try {
			dsocket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}

		// Create a buffer to read datagrams into. If a
		// packet is larger than this buffer, the
		// excess will simply be discarded!
		final byte[] buffer = new byte[2048];

		// Create a packet to receive data into the buffer
		final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		// Now loop forever, waiting to receive packets and printing them.

		thread = new Thread() {

			@Override
			public void run() {

				App.debug("thread starting");

				running = true;

				while (running) {
					// Wait to receive a datagram
					try {
						dsocket.receive(packet);
					} catch (IOException e) {
						dsocket.close();
						App.debug("logging failed");
						e.printStackTrace();
						running = false;
					}

					if (running) {

						byte c0 = buffer[0];
						byte c1 = buffer[1];
						byte c2 = buffer[2];
						if (c0 == 'F' && c1 == 'S' && c2 == 0x01) {

							// https://itunes.apple.com/gb/app/sensor-data-streamer/id608278214?mt=8
							App.debug("data is from 'Sensor Streamer' (c) 2013 FNI Co LTD");
							log(Types.ACCELEROMETER_X, getFloat(buffer, 4));
							log(Types.ACCELEROMETER_Y, getFloat(buffer, 8));
							log(Types.ACCELEROMETER_Z, getFloat(buffer, 12));

							/*
							 * 
							 * App.debug("accelerometer x = " + getFloat(buffer,
							 * 4)); App.debug("accelerometer y = " +
							 * getFloat(buffer, 8));
							 * App.debug("accelerometer z = " + getFloat(buffer,
							 * 12)); App.debug("gyro x = " + getFloat(buffer,
							 * 16)); App.debug("gyro y = " + getFloat(buffer,
							 * 20)); App.debug("gyro z = " + getFloat(buffer,
							 * 24)); App.debug("teslameter x = " +
							 * getDouble(buffer, 28));
							 * App.debug("teslameter y = " + getDouble(buffer,
							 * 36)); App.debug("teslameter z = " +
							 * getDouble(buffer, 44));
							 * App.debug("magnetic heading = " +
							 * getDouble(buffer, 52));
							 * App.debug("true heading = " + getDouble(buffer,
							 * 60)); App.debug("latitude = " + getDouble(buffer,
							 * 68)); App.debug("longitude = " +
							 * getDouble(buffer, 76)); App.debug("altitude = " +
							 * getDouble(buffer, 84)); App.debug("proximity = "
							 * + buffer[92]); App.debug("touched1? = " +
							 * buffer[93]); App.debug("touch1 x = " +
							 * getInt(buffer, 94)); App.debug("touch1 y = " +
							 * getInt(buffer, 98)); App.debug("touched2? = " +
							 * buffer[102]); App.debug("touch2 x = " +
							 * getInt(buffer, 103)); App.debug("touch2 y = " +
							 * getInt(buffer, 107));
							 */

						} else {

							// https://play.google.com/store/apps/details?id=jp.ac.ehime_u.cite.sasaki.SensorUdp&feature=nav_result
							App.debug("Assume data is from Android/SensorUDP");

							String msg = new String(buffer, 0,
									packet.getLength());

							String[] split = msg.split(",");

							double x, y, z;

							switch (buffer[0]) {
							case 'A':

								log(Types.ACCELEROMETER_X,
										Double.parseDouble(split[3]));
								log(Types.ACCELEROMETER_Y,
										Double.parseDouble(split[4]));
								log(Types.ACCELEROMETER_Z,
										Double.parseDouble(split[5]));

								break;
							case 'M':

								App.debug("M");
								x = Double.parseDouble(split[3]);
								y = Double.parseDouble(split[4]);
								z = Double.parseDouble(split[5]);

								App.debug("accelerometer x = " + x);
								App.debug("accelerometer y = " + y);
								App.debug("accelerometer z = " + z);

								break;
							case 'O':
								App.debug("O");
								x = Double.parseDouble(split[3]);
								y = Double.parseDouble(split[4]);
								z = Double.parseDouble(split[5]);

								App.debug("accelerometer x = " + x);
								App.debug("accelerometer y = " + y);
								App.debug("accelerometer z = " + z);

								break;

							default:
								App.debug("unknown data type: " + buffer[0]);
								break;
							}

							// for (int i = 1; i < split.length; i++) {
							// App.debug(split[i]);
							// }

							// Convert the contents to a string, and display
							// them
							App.debug(packet.getAddress().getHostAddress()
									+ " " + packet.getAddress().getHostName()
									+ ": " + msg);
						}

						// Reset the length of the packet before reusing it.
						packet.setLength(buffer.length);

					}

				}

				dsocket.close();
				App.debug("thread ending");

			}

			private void log(Types type, double val) {
				GeoNumeric geo = listeners.get(type);

				if (geo != null) {
					App.debug(type + ": " + val);
					geo.setValue(val);
					geo.updateRepaint();
				}

			}

			private float getFloat(byte[] buffer1, int i) {
				byte[] bytes = { buffer1[i], buffer1[i + 1], buffer1[i + 2],
						buffer1[i + 3] };
				return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
						.getFloat();
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
			App.debug("logging " + type + " to " + geo.getLabelSimple());
			listeners.put(type, geo);
		}
	}

}
