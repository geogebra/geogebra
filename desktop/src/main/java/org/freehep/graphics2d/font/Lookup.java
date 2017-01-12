//Copyright 2001-2005 FreeHEP.
package org.freehep.graphics2d.font;

/**
 * Lookup class provides conversion between different encodings and character
 * tables using character name, encoding index, and unicode. In order to add new
 * tables original code should be modified.
 * 
 * @author Sami Kama
 * @version $Id: Lookup.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class Lookup {
	private static Lookup instance;

	private CharTable[] tables;

	public static Lookup getInstance() {
		if (instance == null) {
			instance = new Lookup();
		}
		return (instance);
	}

	private int ntables = 8;

	private Lookup() {
		tables = new CharTable[ntables];
		tables[0] = new Symbol();
		tables[1] = new Expert();
		tables[2] = new Zapfdingbats();
		tables[3] = new STDLatin();
		tables[4] = new MACLatin();
		tables[5] = new WINLatin();
		tables[6] = new PDFLatin();
		tables[7] = new ISOLatin();

	}

	/**
	 * Converts a name to unicode. This method takes name of the character and
	 * returns character as a unicode character.
	 * 
	 * @return requested unicode character.
	 */
	public char toUnicode(String name) {
		for (int i = 0; i < ntables; i++) {
			char uc = tables[i].toUnicode(name);
			if (uc != '\uffff') {
				return (uc);
			}
		}
		return ('\uffff');
	}

	/**
	 * Converts a unicode character to name.
	 * 
	 * @return requested character name.
	 */
	public String toName(char uc) {
		for (int i = 0; i < ntables; i++) {
			String name = tables[i].toName(uc);
			if (name != null) {
				return (name);
			}
		}
		return (null);
	}

	/**
	 * Converts a unicode character to name.
	 * 
	 * @return requested character name.
	 */
	public String toName(Character uc) {
		for (int i = 0; i < ntables; i++) {
			String name = tables[i].toName(uc);
			if (name != null) {
				return (name);
			}
		}
		return (null);
	}

	/**
	 * Gives the requested encoding table Valid table names can be requested
	 * with a call to
	 * 
	 * @see #getTableNames().
	 * 
	 * @return requested encoding.
	 */
	public CharTable getTable(String tableName) {
		for (int i = 0; i < ntables; i++) {
			String tblName = tables[i].getEncoding() + tables[i].getName();
			if (tblName.equalsIgnoreCase(tableName)) {
				return (tables[i]);
			}
		}
		return (null);
	}

	/**
	 * Gives the total number of tables included in the file
	 * 
	 * @return number of tables
	 */
	public int getNumberOfTables() {
		return (ntables);
	}

	/**
	 * Gives the names of tables that can be used in a string array.
	 * 
	 * @return An array of String[] containing the table names
	 */
	public String[] getTableNames() {
		String[] tblnames = new String[ntables];
		for (int i = 0; i < ntables; i++) {
			tblnames[i] = tables[i].getEncoding() + tables[i].getName();
		}
		return tblnames;
	}
}