// Copyright 2003, FreeHEP.
package org.freehep.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Special property class which allows typed properties to be set and returned.
 * It also allows the hookup of two default property objects to be searched if
 * this property object does not contain the property.
 *
 * FIXME check what org.freehep.application.PropertyUtilities.java has to offer
 * and merge, or not
 *
 * FIXME: This class does not seem general enough to be a "public" utility.
 * Should be improved and merged with PropertyUtilities, or moved into the
 * graphicsio package (tonyj)
 *
 * @author Mark Donszelmann
 * @version $Id: UserProperties.java,v 1.4 2009-06-22 02:18:20 hohenwarter Exp $
 */

public class UserProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3297483251864528952L;
	protected Properties altDefaults;

	public UserProperties() {
		super();
		altDefaults = new Properties();
	}

	public UserProperties(Properties defaults) {
		super(defaults);
		altDefaults = new Properties();
	}

	/**
	 * Constructs UserProperties with a defaults and altDefaults table, which
	 * are searched in that order.
	 */
	public UserProperties(Properties defaults, Properties altDefaults) {
		super(defaults);
		this.altDefaults = altDefaults;
	}

	@Override
	public Enumeration propertyNames() {

		List list = new ArrayList();

		for (Enumeration e = super.propertyNames(); e.hasMoreElements();) {
			list.add(e.nextElement());
		}
		if (altDefaults != null) {
			for (Enumeration e = altDefaults.propertyNames(); e
					.hasMoreElements();) {
				list.add(e.nextElement());
			}
		}

		return Collections.enumeration(list);
	}

	/**
	 * Copies properties, including its defaults into this UserProperties
	 */
	public void setProperties(Properties properties) {
		for (Enumeration e = properties.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			setProperty(key, properties.getProperty(key));
		}
	}

	@Override
	public Object setProperty(String key, String value) {
		if (value == null) {
			return super.setProperty(key, "null");
		}
		return super.setProperty(key, value);
	}

	public Object setProperty(String key, String[] value) {
		return setProperty(this, key, value);
	}

	public static Object setProperty(Properties properties, String key,
			String[] value) {
		if (value == null) {
			return properties.setProperty(key, "null");
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < value.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(value[i]);
		}
		return properties.setProperty(key, sb.toString());
	}

	public Object setProperty(String key, Color value) {
		return setProperty(this, key, value);
	}

	public static Object setProperty(Properties properties, String key,
			Color value) {
		if (value == null) {
			return properties.setProperty(key, "null");
		}
		return properties.setProperty(key,
				value.getRed() + ", " + value.getGreen() + ", "
						+ value.getBlue() + ", " + value.getAlpha());
	}

	public Object setProperty(String key, Rectangle value) {
		return setProperty(this, key, value);
	}

	public static Object setProperty(Properties properties, String key,
			Rectangle value) {
		if (value == null) {
			return properties.setProperty(key, "null");
		}
		return properties.setProperty(key, value.x + ", " + value.y + ", "
				+ value.width + ", " + value.height);
	}

	public Object setProperty(String key, Insets value) {
		return setProperty(this, key, value);
	}

	public static Object setProperty(Properties properties, String key,
			Insets value) {
		if (value == null) {
			return properties.setProperty(key, "null");
		}
		return properties.setProperty(key, value.top + ", " + value.left + ", "
				+ value.bottom + ", " + value.right);
	}

	public Object setProperty(String key, Dimension value) {
		return setProperty(this, key, value);
	}

	public static Object setProperty(Properties properties, String key,
			Dimension value) {
		if (value == null) {
			return properties.setProperty(key, "null");
		}
		return properties.setProperty(key, value.width + ", " + value.height);
	}

	public Object setProperty(String key, int value) {
		return setProperty(this, key, value);
	}

	public static Object setProperty(Properties properties, String key,
			int value) {
		return properties.setProperty(key, Integer.toString(value));
	}

	public Object setProperty(String key, double value) {
		return setProperty(this, key, value);
	}

	public static Object setProperty(Properties properties, String key,
			double value) {
		return properties.setProperty(key, Double.toString(value));
	}

	public Object setProperty(String key, float value) {
		return setProperty(this, key, value);
	}

	public static Object setProperty(Properties properties, String key,
			float value) {
		return properties.setProperty(key, Float.toString(value));
	}

	public Object setProperty(String key, boolean value) {
		return setProperty(this, key, value);
	}

	public static Object setProperty(Properties properties, String key,
			boolean value) {
		return properties.setProperty(key, Boolean.toString(value));
	}

	@Override
	public String getProperty(String key) {
		String value = super.getProperty(key);
		return value != null ? value : altDefaults.getProperty(key);
	}

	@Override
	public String getProperty(String key, String def) {
		String value = getProperty(key);
		return value != null ? value : def;
	}

	public String[] getPropertyStringArray(String key) {
		return getPropertyStringArray(key, null);
	}

	public String[] getPropertyStringArray(String key, String[] def) {
		String s = getProperty(key);
		if (s == null) {
			return def;
		}
		if (s.equals("null")) {
			return null;
		}

		return s.split(", ");
	}

	public Color getPropertyColor(String key) {
		return getPropertyColor(key, null);
	}

	public Color getPropertyColor(String key, Color def) {
		String s = getProperty(key);
		if (s == null) {
			return def;
		}
		if (s.equals("null")) {
			return null;
		}

		String[] r = s.split(", ");
		return new Color(Integer.parseInt(r[0]), Integer.parseInt(r[1]),
				Integer.parseInt(r[2]), Integer.parseInt(r[3]));
	}

	public Rectangle getPropertyRectangle(String key) {
		return getPropertyRectangle(key, null);
	}

	public Rectangle getPropertyRectangle(String key, Rectangle def) {
		String s = getProperty(key);
		if (s == null) {
			return def;
		}
		if (s.equals("null")) {
			return null;
		}

		String[] r = s.split(", ");
		return new Rectangle(Integer.parseInt(r[0]), Integer.parseInt(r[1]),
				Integer.parseInt(r[2]), Integer.parseInt(r[3]));
	}

	public Insets getPropertyInsets(String key) {
		return getPropertyInsets(key, null);
	}

	public Insets getPropertyInsets(String key, Insets def) {
		String s = getProperty(key);
		if (s == null) {
			return def;
		}
		if (s.equals("null")) {
			return null;
		}

		String[] r = s.split(", ");
		return new Insets(Integer.parseInt(r[0]), Integer.parseInt(r[1]),
				Integer.parseInt(r[2]), Integer.parseInt(r[3]));
	}

	public Dimension getPropertyDimension(String key) {
		return getPropertyDimension(key, null);
	}

	public Dimension getPropertyDimension(String key, Dimension def) {
		String s = getProperty(key);
		if (s == null) {
			return def;
		}
		if (s.equals("null")) {
			return null;
		}

		String[] d = s.split(", ");
		return new Dimension(Integer.parseInt(d[0]), Integer.parseInt(d[1]));
	}

	public int getPropertyInt(String key) {
		return getPropertyInt(key, 0);
	}

	public int getPropertyInt(String key, int def) {
		// FIXME: Yow!, two unnecessary object creations for each key lookup.
		return Integer.parseInt(getProperty(key, Integer.toString(def)));
	}

	public double getPropertyDouble(String key) {
		return getPropertyDouble(key, 0.0);
	}

	public double getPropertyDouble(String key, double def) {
		return new Double(getProperty(key, Double.toString(def))).doubleValue();
	}

	public float getPropertyFloat(String key) {
		return getPropertyFloat(key, 0.0f);
	}

	public float getPropertyFloat(String key, float def) {
		return new Float(getProperty(key, Float.toString(def))).floatValue();
	}

	public boolean isProperty(String key) {
		return isProperty(key, false);
	}

	public boolean isProperty(String key, boolean def) {
		return Boolean.valueOf(getProperty(key, Boolean.toString(def)))
				.booleanValue();
	}

}
