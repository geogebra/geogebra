package org.rosuda.REngine;

// REngine library - Java client interface to R
// Copyright (C) 2004,2007,2008 Simon Urbanek

import java.util.*;

/** implementation of R-lists<br>
    All lists (dotted-pair lists, language lists, expressions and vectors) are regarded as named generic vectors. 
    Note: This implementation has changed radically in Rserve 0.5!

    This class inofficially implements the Map interface. Unfortunately a conflict in the Java iterface classes Map and List doesn't allow us to implement both officially. Most prominently the Map 'remove' method had to be renamed to removeByKey.

    @version $Id: RList.java 3199 2009-09-21 15:53:10Z urbanek $
*/
public class RList extends Vector implements List {
    public Vector names;

    /** constructs an empty list */
    public RList() { super(); names=null; }

    /** constructs an initialized, unnamed list
	 * @param contents - an array of {@link REXP}s to use as contents of this list */
    public RList(REXP[] contents) {
	super(contents.length);
	int i=0;
	while (i<contents.length)
	    super.add(contents[i++]);
	names=null;
    }

    public RList(int initSize, boolean hasNames) {
	super(initSize);
	names=null;
	if (hasNames) names=new Vector(initSize);
    }
    
    /** constructs an initialized, unnamed list
	 * @param contents - a {@link Collection} of {@link REXP}s to use as contents of this list */
    public RList(Collection contents) {
	super(contents);
	names=null;
    }

    /** constructs an initialized, named list. The length of the contents vector determines the length of the list.
	 * @param contents - an array of {@link REXP}s to use as contents of this list
	 * @param names - an array of {@link String}s to use as names */
    public RList(REXP[] contents, String[] names) {
	this(contents);
	if (names!=null && names.length>0) {
	    this.names=new Vector(names.length);
	    int i = 0;
	    while (i < names.length) this.names.add(names[i++]);
	    while (this.names.size()<size()) this.names.add(null);
	}
    }
    
    /** constructs an initialized, named list. The size of the contents collection determines the length of the list.
	 * @param contents - a {@link Collection} of {@link REXP}s to use as contents of this list
	 * @param names - an array of {@link String}s to use as names */
    public RList(Collection contents, String[] names) {
	this(contents);
	if (names!=null && names.length>0) {
	    this.names=new Vector(names.length);
	    int i = 0;
	    while (i < names.length) this.names.add(names[i++]);
	    while (this.names.size()<size()) this.names.add(null);
	}
    }

    /** constructs an initialized, named list. The size of the contents collection determines the length of the list.
	 * @param contents - a {@link Collection} of {@link REXP}s to use as contents of this list
	 * @param names - an {@link Collection} of {@link String}s to use as names */
    public RList(Collection contents, Collection names) {
	this(contents);
	if (names!=null && names.size()>0) {
	    this.names=new Vector(names);
	    while (this.names.size()<size()) this.names.add(null);
	}
    }

	/** checks whether this list is named or unnamed
	 * @return <code>true</code> if this list is named, <code>false</code> otherwise */
    public boolean isNamed() {
	return names!=null;
    }

    /** get xpression given a key
	@param v key
	@return value which corresponds to the given key or
	        <code>null</code> if the list is unnamed or key not found */
    public REXP at(String v) {
	if (names==null) return null;
	int i = names.indexOf(v);
	if (i < 0) return null;
	return (REXP)elementAt(i);
    }

    /** get element at the specified position
	@param i index
	@return value at the index or <code>null</code> if the index is out of bounds */
    public REXP at(int i) {
	return (i>=0 && i<size())?(REXP)elementAt(i):null;
    }

	/** return the key (name) at a given index
	 @param i index
	 @return ket at the index - can be <code>null</code> is the list is unnamed or the index is out of range */
    public String keyAt(int i) {
	return (names==null || i<0 || i>=names.size())?null:(String)names.get(i);
    }

	/** set key at the given index. Using this method automatically makes the list a named one even if the key is <code>null</code>. Out of range operations are undefined (currently no-ops)
	 @param i index
	 @param value key name */
	public void setKeyAt(int i, String value) {
		if (i < 0) return;
		if (names==null)
			names = new Vector();
		if (names.size() < size()) names.setSize(size());
		if (i < size()) names.set(i, value);
	}

    /** returns all keys of the list
	 * @return array containing all keys or <code>null</code> if list unnamed */
    public String[] keys() {
	if (names==null) return null;
	int i = 0;
	String k[] = new String[names.size()];
	while (i < k.length) { k[i] = keyAt(i); i++; };
	return k;
    }

    // --- overrides that sync names

    public void add(int index, Object element) {
	super.add(index, element);
	if (names==null) return;
	names.add(index, null);
    }

	public boolean add(Object element) {
		super.add(element);
		if (names != null)
			names.add(null);
		return true;
	}
	
    public boolean addAll(Collection c) {
	boolean ch = super.addAll(c);
	if (names==null) return ch;
	int l = size();
	while (names.size()<l) names.add(null);
	return ch;
    }

    public boolean addAll(int index, Collection c) {
	boolean ch = super.addAll(index, c);
	if (names==null) return ch;
	int l = c.size();
	while (l-- > 0) names.add(index, null);
	return ch;
    }

    public void clear() {
	super.clear();
	names=null;
    }

    public Object clone() {
	return new RList(this, names);	
    }

    public Object remove(int index) {
	Object o = super.remove(index);
	if (names != null) {
	    names.remove(index);
	    if (size()==0) names=null;
	}
	return o;
    }

    public boolean remove(Object elem) {
	int i = indexOf(elem);
	if (i<0) return false;
	remove(i);
	if (size()==0) names=null;
	return true;
    }

    public boolean removeAll(Collection c) {
	if (names==null) return super.removeAll(c);
	boolean changed=false;
	Iterator it = c.iterator();
	while (it.hasNext())
	    changed|=remove(it.next());
	return changed;
    }

    public boolean retainAll(Collection c) {
	if (names==null) return super.retainAll(c);
	boolean rm[] = new boolean[size()];
	boolean changed=false;
	int i = 0;
	while (i<rm.length) {
	    changed|=rm[i]=!c.contains(get(i));
	    i++;
	}
	while (i>0) {
	    i--;
	    if (rm[i]) remove(i);
	}
	return changed;
    }

    // --- old API mapping
    public void removeAllElements() { clear(); }
    public void insertElementAt(Object obj, int index) { add(index, obj); }
    public void addElement(Object obj) { add(obj); }
    public void removeElementAt(int index) { remove(index); }
    public boolean removeElement(Object obj) { return remove(obj); }

    // --- Map interface

    public boolean containsKey(Object key) {
	return (names==null)?false:names.contains(key);
    }

    public boolean containsValue(Object value) {
	return contains(value);
    }

    /** NOTE: THIS IS UNIMPLEMENTED and always returns <code>null</code>! Due to the fact that R lists are not proper maps we canot maintain a set-view of the list */
    public Set entrySet() {
	return null;
    }

    public Object get(Object key) {
	return at((String)key);
    }

    /** Note: sinde RList is not really a Map, the returned set is only an approximation as it cannot reference duplicate or null names that may exist in the list */
    public Set keySet() {
	if (names==null) return null;
	return new HashSet(names);
    }

    public Object put(Object key, Object value) {
	if (key==null) {
	    add(value);
	    return null;
	}
	if (names != null) {
	    int p = names.indexOf(key);
	    if (p >= 0)
		return super.set(p, value);
	}
	int i = size();
	super.add(value);
	if (names==null)
	    names = new Vector(i+1);
	while (names.size() < i) names.add(null);
	names.add(key);
	return null;
    }

    public void putAll(Map t) {
	if (t==null) return;
	// NOTE: this if branch is dead since RList cannot inherit from Map
	if (t instanceof RList) { // we need some more sophistication for RLists as they may have null-names which we append
	    RList l = (RList) t;
	    if (names==null) {
		addAll(l);
		return;
	    }
	    int n = l.size();
	    int i = 0;
	    while (i < n) {
		String key = l.keyAt(i);
		if (key==null)
		    add(l.at(i));
		else
		    put(key, l.at(i));
		i++;
	    }
	} else {
	    Set ks = t.keySet();
	    Iterator i = ks.iterator();
	    while (i.hasNext()) {
		Object key = i.next();
		put(key, t.get(key));
	    }
	}
    }

    public void putAll(RList t) {
	if (t == null) return;
	RList l = (RList) t;
	if (names==null) {
	    addAll(l);
	    return;
	}
	int n = l.size();
	int i = 0;
	while (i < n) {
	    String key = l.keyAt(i);
	    if (key == null)
		add(l.at(i));
	    else
		put(key, l.at(i));
	    i++;
	}
    }
    
    public Object removeByKey(Object key) {
	if (names==null) return null;
	int i = names.indexOf(key);
	if (i<0) return null;
	Object o = elementAt(i);
	removeElementAt(i);
	names.removeElementAt(i);
	return o;
    }

    public Collection values() {
	return this;
    }
	
	// other
	public String toString() {
		return "RList"+super.toString()+"{"+(isNamed()?"named,":"")+size()+"}";
	}
}
