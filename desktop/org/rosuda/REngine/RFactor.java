package org.rosuda.REngine;

// REngine
// Copyright (C) 2007 Simon Urbanek
// --- for licensing information see LICENSE file in the original distribution ---

import java.util.*;

/** representation of a factor variable. In R there is no actual object
    type called "factor", instead it is coded as an int vector with a list
    attribute. The parser code of REXP converts such constructs directly into
    the RFactor objects and defines an own XT_FACTOR type 
    
    @version $Id: RFactor.java 2841 2008-02-27 18:47:46Z urbanek $
*/    
public class RFactor {
    int ids[];
    String levels[];
	int index_base;

    /** create a new, empty factor var */
    public RFactor() { ids=new int[0]; levels=new String[0]; }
    
    /** create a new factor variable, based on the supplied arrays.
		@param i array of IDs (inde_base..v.length+index_base-1)
		@param v values - cotegory names
		@param copy copy above vaules or just retain them
		@param index_base index of the first level element (1 for R factors, cannot be negtive)
		*/
    public RFactor(int[] i, String[] v, boolean copy, int index_base) {
		if (i==null) i = new int[0];
		if (v==null) v = new String[0];
		if (copy) {
			ids=new int[i.length]; System.arraycopy(i,0,ids,0,i.length);
			levels=new String[v.length]; System.arraycopy(v,0,levels,0,v.length);
		} else {
			ids=i; levels=v;
		}
		this.index_base = index_base;
    }

	/** create a new factor variable by factorizing a given string array. The levels will be created in the orer of appearance.
		@param c contents
		@param index_base base of the level index */
	public RFactor(String c[], int index_base) {
		this.index_base = index_base;
		if (c == null) c = new String[0];
		Vector lv = new Vector();
		ids = new int[c.length];
		int i = 0;
		while (i < c.length) {
			int ix = (c[i]==null)?-1:lv.indexOf(c[i]);
			if (ix<0 && c[i]!=null) {
				ix = lv.size();
				lv.add(c[i]);
			}
			ids[i] = (ix<0)?REXPInteger.NA:(ix+index_base);
			i++;
		}
		levels = new String[lv.size()];
		i = 0;
		while (i < levels.length) {
			levels[i] = (String) lv.elementAt(i);
			i++;
		}
	}
	
	/** same as <code>RFactor(c, 1)</code> */
	public RFactor(String c[]) {
		this(c, 1);
	}
	
	/** same as <code>RFactor(i,v, true, 1)</code> */
	public RFactor(int[] i, String[] v) {
		this(i, v, true, 1);
	}
	
    /** returns the level of a given case
		@param i case number
		@return name. may throw exception if out of range */
    public String at(int i) {
		int li = ids[i] - index_base;
		return (li<0||li>levels.length)?null:levels[li];
    }

	/** returns <code>true</code> if the data contain the given level index */
	public boolean contains(int li) {
		int i = 0;
		while (i < ids.length) {
			if (ids[i] == li) return true;
			i++;
		}
		return false;
	}
	
	/** return <code>true</code> if the factor contains the given level (it is NOT the same as levelIndex==-1!) */
	public boolean contains(String name) {
		int li = levelIndex(name);
		if (li<0) return false;
		int i = 0;
		while (i < ids.length) {
			if (ids[i] == li) return true;
			i++;
		}
		return false;
	}
	
	/** count the number of occurences of a given level index */
	public int count(int levelIndex) {
		int i = 0;
		int ct = 0;
		while (i < ids.length) {
			if (ids[i] == levelIndex) ct++;
			i++;
		}
		return ct;
	}
	
	/** count the number of occurences of a given level name */
	public int count(String name) {
		return count(levelIndex(name));
	}
	
	/** return an array with level counts. */
	public int[] counts() {
		int[] c = new int[levels.length];
		int i = 0;
		while (i < ids.length) {
			final int li = ids[i] - index_base;
			if (li>=0 && li<levels.length)
				c[li]++;
			i++;
		}
		return c;
	}
	
	/** return the index of a given level name or -1 if it doesn't exist */
	public int levelIndex(String name) {
		if (name==null) return -1;
		int i = 0;
		while (i < levels.length) {
			if (levels[i]!=null && levels[i].equals(name)) return i + index_base;
			i++;
		}
		return -1;
	}
	
	/** return the list of levels (0-based, use {@link #indexBase} correction if you want to access it by level index) */
	public String[] levels() {
		return levels;
	}
	
	/** return the contents as integer indices (with the index base of this factor) */
	public int[] asIntegers() {
		return ids;
	}
	
	/** return the contents as integer indices with a given index base */
	public int[] asIntegers(int desired_index_base) {
		if (desired_index_base == index_base) return ids;
		int[] ix = new int[ids.length];
		int j = 0; while (j < ids.length) { ix[j] = ids[j] - index_base + desired_index_base; j++; }
		return ix;
	}
	
	/** return the level name for a given level index */
	public String levelAtIndex(int li) {
		li -= index_base;
		return (li<0||li>levels.length)?null:levels[li];
	}
	
	/** return the level index for a given case */
	public int indexAt(int i) {
		return ids[i];
	}
	
	/** return the factor as an array of strings */
	public String[] asStrings() {
		String[] s = new String[ids.length];
		int i = 0;
		while (i < ids.length) {
			s[i] = at(i);
			i++;
		}
		return s;	
	}
	
	/** return the base of the levels index */
	public int indexBase() {
		return index_base;
	}
	
    /** returns the number of cases */
    public int size() { return ids.length; }

	public String toString() {
		return super.toString()+"["+ids.length+","+levels.length+",#"+index_base+"]";
	}
	
    /** displayable representation of the factor variable
    public String toString() {
	//return "{"+((val==null)?"<null>;":("levels="+val.size()+";"))+((id==null)?"<null>":("cases="+id.size()))+"}";
	StringBuffer sb=new StringBuffer("{levels=(");
	if (val==null)
	    sb.append("null");
	else
	    for (int i=0;i<val.size();i++) {
		sb.append((i>0)?",\"":"\"");
		sb.append((String)val.elementAt(i));
		sb.append("\"");
	    };
	sb.append("),ids=(");
	if (id==null)
	    sb.append("null");
	else
	    for (int i=0;i<id.size();i++) {
		if (i>0) sb.append(",");
		sb.append((Integer)id.elementAt(i));
	    };
	sb.append(")}");
	return sb.toString();
    } */
}

