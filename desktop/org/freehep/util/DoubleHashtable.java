// Copyright 2002, SLAC, Stanford, U.S.A.
package org.freehep.util;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Stores a hashtable of hashtables, which can be indexed by a key and a subkey.
 * Keys and Values can be null.
 *
 * @author Mark Donszelmann
 * @version $Id: DoubleHashtable.java,v 1.4 2009-06-22 02:18:20 hohenwarter Exp $
 */

public class DoubleHashtable extends AbstractCollection implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -545653328241864972L;
	private Hashtable table;

    /**
     * creates a hashtable of hashtables
     */
    public DoubleHashtable() {
        table = new Hashtable();
    }

    /**
     * removes all entries and sub-tables
     */
    public void clear() {
        table.clear();
    }

    /**
     * removes all entries from a subtable
     */
    public void clear(Object key) {
        Hashtable subtable = get(key);
        if (subtable != null) {
            subtable.clear();
        }
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("DoubleHashtable.clone() is not (yet) supported.");
    }

    /**
     * @return true if value exists in some sub-table
     */
    public boolean contains(Object value) {
        if (value == null) value = this;

        for (Enumeration e=table.keys(); e.hasMoreElements(); ) {
            Hashtable subtable = get(e.nextElement());
            if (subtable.contains(value)) return true;
        }
        return false;
    }

    /**
     * @return true if sub-table exists for key
     */
    public boolean containsKey(Object key) {
        if (key == null) key = this;
        return table.containsKey(key);
    }

    /**
     * @return true if value exists for key and subkey
     */
    public boolean containsKey(Object key, Object subKey) {
        if (subKey == null) subKey = this;
        Hashtable subtable = get(key);
        return (subtable != null) ? subtable.containsKey(subKey) : false;
    }

    /**
     * @return enumeration over all values in all sub-tables
     */
    public Enumeration elements() {
        return new Enumeration() {
            private Enumeration subtableEnumeration = table.elements();
            private Enumeration valueEnumeration;
            private Object nullValue = DoubleHashtable.this;

            public boolean hasMoreElements() {
                if ((valueEnumeration == null) || (!valueEnumeration.hasMoreElements())) {
                    if (!subtableEnumeration.hasMoreElements()) {
                        return false;
                    }
                    valueEnumeration = ((Hashtable)subtableEnumeration.nextElement()).elements();
                }
                return true;
            }

            public Object nextElement() {
                hasMoreElements();
                Object value = valueEnumeration.nextElement();
                return (value == nullValue) ? null : value;
            }
        };
    }

    /**
     * @return iterator over all values in all sub-tables
     */
    public Iterator iterator() {
        return new Iterator() {
            private Iterator subtableIterator = table.entrySet().iterator();
            private Map subtable;
            private Iterator valueIterator;
            private Object nullValue = DoubleHashtable.this;

            public boolean hasNext() {
                if ((valueIterator == null) || (!valueIterator.hasNext())) {
                    if (!subtableIterator.hasNext()) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry)subtableIterator.next();
                    subtable = (Map)entry.getValue();
                    valueIterator = subtable.entrySet().iterator();
                }
                return true;
            }

            public Object next() {
                hasNext();
                Map.Entry entry = (Map.Entry)valueIterator.next();
                Object value = entry.getValue();
                return (value == nullValue) ? null : value;
            }

            public void remove() {
                valueIterator.remove();

                if (subtable.isEmpty()) {
                    subtableIterator.remove();
                }
            }
        };
    }

    /**
     * @return sub-table for key
     */
    public Hashtable get(Object key) {
        if (key == null) key = this;
        return (Hashtable)table.get(key);
    }

    /**
     * @return value for key and subkey, null in non-existent or null value was stored
     */
    public Object get(Object key, Object subKey) {
        if (subKey == null) subKey = this;
        Hashtable table = get(key);
        Object value = (table==null) ? null : table.get(subKey);
        return (value == this) ? null : value;
    }

    /**
     * @return true if table is empty
     */
    public boolean isEmpty() {
        return table.isEmpty();
    }

    /**
     * @return enumeration of keys in table
     */
    public Enumeration keys() {
        return table.keys();
    }

    /**
     * @return enumeration in subkeys of sub-table pointed by key, and empty if sub-table does not exist
     */
    public Enumeration keys(Object key) {
        final Hashtable subtable = get(key);
        return new Enumeration() {
            private Enumeration subkeys = (subtable == null) ? null : subtable.keys();
            private Object nullKey = DoubleHashtable.this;

            public boolean hasMoreElements() {
                return (subkeys == null) ? false : subkeys.hasMoreElements();
            }
            public Object nextElement() {
                if (subkeys == null) {
                    throw new NoSuchElementException();
                }
                Object subkey = subkeys.nextElement();
                return (subkey == nullKey) ? null : subkey;
            }
        };
    }

    /**
     * puts a value in sub-table specified by key and subkey.
     *
     * @return previous value
     */
    public Object put(Object key, Object subKey, Object value) {
        // Make sure there exists a subtable
        Hashtable subtable = get(key);
        if (subtable == null) {
            subtable = new Hashtable();
            if (key == null) key = this;
            table.put(key, subtable);
        }

        // add entry and handle nulls
        if (subKey == null) subKey = this;
        if (value == null) value = this;
        Object old = subtable.get(subKey);
        subtable.put(subKey, value);

        // return previous entry
        return (old == this) ? null : old;
    }

    /**
     * removes value from sub-table specified by key and subkey.
     *
     * @return previous value
     */
    public Object remove(Object key, Object subKey) {
        // look for subtable
        Hashtable subtable = get(key);
        if (subtable == null) return null;

        // remove from subtable
        if (subKey == null) subKey = this;
        Object old = subtable.remove(subKey);

        // remove subtable if needed
        if (subtable.isEmpty()) {
            if (key == null) key = this;
            table.remove(key);
        }

        // return old value
        return (old == this) ? null : old;
    }

    /**
     * @return size of all tables
     */
    public int size() {
        int size =0;
        for (Enumeration e = table.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            Hashtable subtable = get(key);
            size += subtable.size();
        }
        return size;
    }

    /**
     * @return a string representation of the table
     */
    public String toString() {
        return "DoubleHashtable@"+hashCode();
    }
}
