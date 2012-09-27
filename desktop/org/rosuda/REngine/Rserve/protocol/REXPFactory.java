package org.rosuda.REngine.Rserve.protocol;

// JRclient library - client interface to Rserve, see http://www.rosuda.org/Rserve/
// Copyright (C) 2004-8 Simon Urbanek
// --- for licensing information see LICENSE file in the original JRclient distribution ---

import java.util.*;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

/** representation of R-eXpressions in Java

    @version $Id: REXPFactory.java 3426 2012-02-14 15:50:54Z urbanek $
*/
public class REXPFactory {
    /** xpression type: NULL */
    public static final int XT_NULL=0;
    /** xpression type: integer */
    public static final int XT_INT=1;
    /** xpression type: double */
    public static final int XT_DOUBLE=2;
    /** xpression type: String */
    public static final int XT_STR=3;
    /** xpression type: language construct (currently content is same as list) */
    public static final int XT_LANG=4;
    /** xpression type: symbol (content is symbol name: String) */
    public static final int XT_SYM=5;
    /** xpression type: RBool */    
    public static final int XT_BOOL=6;
    /** xpression type: S4 object
	@since Rserve 0.5 */
    public static final int XT_S4=7;
    /** xpression type: generic vector (RList) */
    public static final int XT_VECTOR=16;
    /** xpression type: dotted-pair list (RList) */
    public static final int XT_LIST=17;
    /** xpression type: closure (there is no java class for that type (yet?). currently the body of the closure is stored in the content part of the REXP. Please note that this may change in the future!) */
    public static final int XT_CLOS=18;
    /** xpression type: symbol name
	@since Rserve 0.5 */
    public static final int XT_SYMNAME=19;
    /** xpression type: dotted-pair list (w/o tags)
	@since Rserve 0.5 */
    public static final int XT_LIST_NOTAG=20;
    /** xpression type: dotted-pair list (w tags)
	@since Rserve 0.5 */
    public static final int XT_LIST_TAG=21;
    /** xpression type: language list (w/o tags)
	@since Rserve 0.5 */
    public static final int XT_LANG_NOTAG=22;
    /** xpression type: language list (w tags)
	@since Rserve 0.5 */
    public static final int XT_LANG_TAG=23;
    /** xpression type: expression vector */
    public static final int XT_VECTOR_EXP=26;
    /** xpression type: string vector */
    public static final int XT_VECTOR_STR=27;
    /** xpression type: int[] */
    public static final int XT_ARRAY_INT=32;
    /** xpression type: double[] */
    public static final int XT_ARRAY_DOUBLE=33;
    /** xpression type: String[] (currently not used, Vector is used instead) */
    public static final int XT_ARRAY_STR=34;
    /** internal use only! this constant should never appear in a REXP */
    public static final int XT_ARRAY_BOOL_UA=35;
    /** xpression type: RBool[] */
    public static final int XT_ARRAY_BOOL=36;
    /** xpression type: raw (byte[])
	@since Rserve 0.4-? */
    public static final int XT_RAW=37;
    /** xpression type: Complex[]
	@since Rserve 0.5 */
    public static final int XT_ARRAY_CPLX=38;
    /** xpression type: unknown; no assumptions can be made about the content */
    public static final int XT_UNKNOWN=48;

    /** xpression type: RFactor; this XT is internally generated (ergo is does not come from Rsrv.h) to support RFactor class which is built from XT_ARRAY_INT */
    public static final int XT_FACTOR=127; 

	/** used for transport only - has attribute */
	private static final int XT_HAS_ATTR=128;
	   
	int type;
	REXPFactory attr;
	REXP cont;
	RList rootList;
	
    public REXP getREXP() { return cont; }
    public REXPList getAttr() { return (attr==null)?null:(REXPList)attr.cont; }
	
	public REXPFactory() {
	}
	
	public REXPFactory(REXP r) throws REXPMismatchException {
		if (r == null) r=new REXPNull();
		REXPList a = r._attr();
		cont = r;
		if (a != null) attr = new REXPFactory(a);
		if (r instanceof REXPNull) {
			type=XT_NULL;
		} else if (r instanceof REXPList) {
			RList l = r.asList();
			type = l.isNamed()?XT_LIST_TAG:XT_LIST_NOTAG;
			if (r instanceof REXPLanguage)
				type = (type==XT_LIST_TAG)?XT_LANG_TAG:XT_LANG_NOTAG;
		} else if (r instanceof REXPGenericVector) {
			type = XT_VECTOR; // FIXME: may have to adjust names attr
		} else if (r instanceof REXPS4) {
			type = XT_S4;
		} else if (r instanceof REXPInteger) { // this includes factor - FIXME: do we need speacial handling?
			type = XT_ARRAY_INT;
		} else if (r instanceof REXPDouble) {
			type = XT_ARRAY_DOUBLE;
		} else if (r instanceof REXPString) {
			type = XT_ARRAY_STR;
		} else if (r instanceof REXPSymbol) {
			type = XT_SYMNAME;
		} else if (r instanceof REXPRaw) {
			type = XT_RAW;
		} else if (r instanceof REXPLogical) {
			type = XT_ARRAY_BOOL;
		} else {
			// throw new REXPMismatchException(r, "decode");
			System.err.println("*** REXPFactory unable to interpret "+r);
		}
	}

    /** parses byte buffer for binary representation of xpressions - read one xpression slot (descends recursively for aggregated xpressions such as lists, vectors etc.)
		@param buf buffer containing the binary representation
		@param o offset in the buffer to start at
        @return position just behind the parsed xpression. Can be use for successive calls to {@link #parseREXP} if more than one expression is stored in the binary array. */
    public int parseREXP(byte[] buf, int o) throws REXPMismatchException {
		int xl = RTalk.getLen(buf,o);
		boolean hasAtt = ((buf[o]&128)!=0);
        boolean isLong = ((buf[o]&64)!=0);
		int xt = (int)(buf[o]&63);
        //System.out.println("parseREXP: type="+xt+", len="+xl+", hasAtt="+hasAtt+", isLong="+isLong);
        if (isLong) o+=4;
        o+=4;
		int eox=o+xl;
	
		type=xt; attr=new REXPFactory(); cont=null;
		if (hasAtt) o = attr.parseREXP(buf, o);
		if (xt==XT_NULL) {
			cont = new REXPNull(getAttr());
			return o;
		}
		if (xt==XT_DOUBLE) {
			long lr = RTalk.getLong(buf,o);
			double[] d = new double[] { Double.longBitsToDouble(lr) };
			o+=8;
			if (o!=eox) {
				System.err.println("Warning: double SEXP size mismatch\n");
				o=eox;
			}
			cont = new REXPDouble(d, getAttr());
			return o;
		}
		if (xt==XT_ARRAY_DOUBLE) {
			int as=(eox-o)/8,i=0;
			double[] d=new double[as];
			while (o<eox) {
				d[i]=Double.longBitsToDouble(RTalk.getLong(buf,o));
				o+=8;
				i++;
			}
			if (o!=eox) {
				System.err.println("Warning: double array SEXP size mismatch\n");
				o=eox;
			}
			cont = new REXPDouble(d, getAttr());
			return o;
		}
		if (xt==XT_BOOL) {
			byte b[] = new byte[] { buf[o] };
			if (b[0] != 0 && b[0] != 1) b[0] = REXPLogical.NA;
			cont = new REXPLogical(b, getAttr());
			o++;
			if (o!=eox) {
                if (eox!=o+3) // o+3 could happen if the result was aligned (1 byte data + 3 bytes padding)
                    System.err.println("Warning: bool SEXP size mismatch\n");
				o=eox;
			}
			return o;
		}
		if (xt==XT_ARRAY_BOOL_UA) {
			int as=(eox-o), i=0;
			byte[] d=new byte[as];
			System.arraycopy(buf,o,d,0,eox-o);
			o = eox;
			for (int j = 0; j < d.length; j++) if (d[j] != 0 && d[j] != 1) d[j] = REXPLogical.NA;
			cont = new REXPLogical(d, getAttr());
			return o;
		}
        if (xt==XT_ARRAY_BOOL) {
            int as=RTalk.getInt(buf,o);
            o+=4;
            byte[] d=new byte[as];
			System.arraycopy(buf,o,d,0,as);
			for (int j = 0; j < d.length; j++) if (d[j] != 0 && d[j] != 1) d[j] = REXPLogical.NA;
			o = eox;
			cont = new REXPLogical(d, getAttr());
            return o;
        }
        if (xt==XT_INT) {
			int i[] = new int[] { RTalk.getInt(buf,o) };
			cont = new REXPInteger(i, getAttr());
			o+=4;
			if (o!=eox) {
				System.err.println("Warning: int SEXP size mismatch\n");
				o=eox;
			}
			return o;
		}
		if (xt==XT_ARRAY_INT) {
			int as=(eox-o)/4,i=0;
			int[] d=new int[as];
			while (o<eox) {
				d[i]=RTalk.getInt(buf,o);
				o+=4;
				i++;
			}
			if (o!=eox) {
				System.err.println("Warning: int array SEXP size mismatch\n");
				o=eox;
			}
			cont = null;
			// hack for lists - special lists attached to int are factors
			try {
			    if (getAttr()!=null) {
					REXP ca = getAttr().asList().at("class");
					REXP ls = getAttr().asList().at("levels");
					if (ca != null && ls != null && ca.asString().equals("factor")) {
						// R uses 1-based index, Java uses 0-based one
						cont = new REXPFactor(d, ls.asStrings(), getAttr());
						xt = XT_FACTOR;
					}
			    }
			} catch (Exception e) {
			}
			if (cont == null) cont = new REXPInteger(d, getAttr());
			return o;
		}
        if (xt==XT_RAW) {
            int as=RTalk.getInt(buf,o);
            o+=4;
            byte[] d=new byte[as];
			System.arraycopy(buf,o,d,0,as);
			o = eox;
			cont = new REXPRaw(d, getAttr());
            return o;
        }
		if (xt==XT_LIST_NOTAG || xt==XT_LIST_TAG ||
			xt==XT_LANG_NOTAG || xt==XT_LANG_TAG) {
			REXPFactory lc = new REXPFactory();
			REXPFactory nf = new REXPFactory();
			RList l = new RList();
			while (o<eox) {
				String name = null;
				o = lc.parseREXP(buf, o);
				if (xt==XT_LIST_TAG || xt==XT_LANG_TAG) {
					o = nf.parseREXP(buf, o);
					if (nf.cont.isSymbol() || nf.cont.isString()) name = nf.cont.asString();
				}
				if (name==null) l.add(lc.cont);
				else l.put(name, lc.cont);
			}
			cont = (xt==XT_LANG_NOTAG || xt==XT_LANG_TAG)?
				new REXPLanguage(l, getAttr()):
				new REXPList(l, getAttr());
			if (o!=eox) {
				System.err.println("Warning: int list SEXP size mismatch\n");
				o=eox;
			}
			return o;
		}
		if (xt==XT_LIST || xt==XT_LANG) { //old-style lists, for comaptibility with older Rserve versions - rather inefficient since we have to convert the recusively stored structures into a flat structure
			boolean isRoot = false;
			if (rootList == null) {
				rootList = new RList();
				isRoot = true;
			}
			REXPFactory headf = new REXPFactory();
			REXPFactory tagf = new REXPFactory();
			o = headf.parseREXP(buf, o);
			int elIndex = rootList.size();
			rootList.add(headf.cont);
			//System.out.println("HEAD="+headf.cont);
			o = parseREXP(buf, o); // we use ourselves recursively for the body
			if (o < eox) {
				o = tagf.parseREXP(buf, o);
				//System.out.println("TAG="+tagf.cont);
				if (tagf.cont != null && (tagf.cont.isString() || tagf.cont.isSymbol()))
					rootList.setKeyAt(elIndex, tagf.cont.asString());
			}
			if (isRoot) {
				cont = (xt==XT_LIST)?
				new REXPList(rootList, getAttr()):
				new REXPLanguage(rootList, getAttr());
				rootList = null;
				//System.out.println("result="+cont);
			}
			return o;
		}
		if (xt==XT_VECTOR || xt==XT_VECTOR_EXP) {
			Vector v=new Vector(); //FIXME: could we use RList?
			while(o<eox) {
				REXPFactory xx=new REXPFactory();
				o = xx.parseREXP(buf,o);
				v.addElement(xx.cont);
			}
			if (o!=eox) {
				System.err.println("Warning: int vector SEXP size mismatch\n");
				o=eox;
			}
			// fixup for lists since they're stored as attributes of vectors
			if (getAttr()!=null && getAttr().asList().at("names") != null) {
				REXP nam = getAttr().asList().at("names");
				String names[] = null;
				if (nam.isString()) names = nam.asStrings();
				else if (nam.isVector()) { // names could be a vector if supplied by old Rserve
					RList l = nam.asList();
					Object oa[] = l.toArray();
					names = new String[oa.length];
					for(int i = 0; i < oa.length; i++) names[i] = ((REXP)oa[i]).asString();
				}
				RList l = new RList(v, names);
				cont = (xt==XT_VECTOR_EXP)?
					new REXPExpressionVector(l, getAttr()):
					new REXPGenericVector(l, getAttr());
			} else
				cont = (xt==XT_VECTOR_EXP)?
					new REXPExpressionVector(new RList(v), getAttr()):
					new REXPGenericVector(new RList(v), getAttr());
			return o;
		}
		if (xt==XT_ARRAY_STR) {
			int c = 0, i = o;
			/* count the entries */
			while (i < eox) if (buf[i++] == 0) c++;
			String s[] = new String[c];
			if (c > 0) {
				c = 0; i = o;
				while (o < eox) {
					if (buf[o] == 0) {
						try {
							if (buf[i] == -1) { /* if the first byte is 0xff (-1 in signed char) then it either needs to be skipped (doubling) or there is an NA value */
								if (buf[i + 1] == 0)
									s[c] = null; /* NA */
								else
									s[c] = new String(buf, i + 1, o - i - 1, RConnection.transferCharset);
							} else
								s[c] = new String(buf, i, o - i, RConnection.transferCharset);
						} catch (java.io.UnsupportedEncodingException ex) {
							s[c]="";
						}
						c++;
						i = o + 1;
					}
					o++;
				}
			}
			cont = new REXPString(s, getAttr());
			return o;
		}
		if (xt==XT_VECTOR_STR) {
			Vector v=new Vector();
			while(o<eox) {
				REXPFactory xx=new REXPFactory();
				o = xx.parseREXP(buf,o);
				v.addElement(xx.cont.asString());
			}
			if (o!=eox) {
				System.err.println("Warning: int vector SEXP size mismatch\n");
				o=eox;
			}
			String sa[] = new String[v.size()];
			int i = 0; while (i < sa.length) { sa[i]=(String)v.get(i); i++; }
			cont = new REXPString(sa, getAttr());
			return o;
		}
		if (xt==XT_STR||xt==XT_SYMNAME) {
			int i = o;
			while (buf[i]!=0 && i<eox) i++;
			try {
				if (xt==XT_STR)
					cont = new REXPString(new String[] { new String(buf, o, i-o, RConnection.transferCharset) }, getAttr());
				else
					cont = new REXPSymbol(new String(buf, o, i-o, RConnection.transferCharset));					
			} catch(Exception e) {
				System.err.println("unable to convert string\n");
				cont = null;
			}
			o = eox;
			return o;
		}
		if (xt==XT_SYM) {
			REXPFactory sym = new REXPFactory();
			o = sym.parseREXP(buf, o); // PRINTNAME that's all we will use
			cont = new REXPSymbol(sym.getREXP().asString()); // content of a symbol is its printname string (so far)
			o=eox;
			return o;
		}
		
		if (xt==XT_CLOS) {
			/*
			REXP form=new REXP();
			REXP body=new REXP();
			o=parseREXP(form,buf,o);
			o=parseREXP(body,buf,o);
			if (o!=eox) {
				System.err.println("Warning: closure SEXP size mismatch\n");
				o=eox;
			}
			x.cont=body;
			 */
			o=eox;
			return o;
		}
		
		if (xt==XT_UNKNOWN) {
			cont = new REXPUnknown(RTalk.getInt(buf,o), getAttr());
			o=eox;
			return o;
		}
		
		if (xt==XT_S4) {
			cont = new REXPS4(getAttr());
			o=eox;
			return o;
		}
		
		cont = null;
		o = eox;
		System.err.println("unhandled type: "+xt);
		return o;
    }

    /** Calculates the length of the binary representation of the REXP including all headers. This is the amount of memory necessary to store the REXP via {@link #getBinaryRepresentation}.
        <p>Please note that currently only XT_[ARRAY_]INT, XT_[ARRAY_]DOUBLE and XT_[ARRAY_]STR are supported! All other types will return 4 which is the size of the header.
        @return length of the REXP including headers (4 or 8 bytes)*/
    public int getBinaryLength() throws REXPMismatchException {
		int l=0;
		int rxt = type;
		if (type==XT_LIST || type==XT_LIST_TAG || type==XT_LIST_NOTAG)
			rxt=(cont.asList()!=null && cont.asList().isNamed())?XT_LIST_TAG:XT_LIST_NOTAG;
		//System.out.print("len["+xtName(type)+"/"+xtName(rxt)+"] ");
		if (type==XT_VECTOR_STR) rxt=XT_ARRAY_STR; // VECTOR_STR is broken right now
		
		/*
		if (type==XT_VECTOR && cont.asList()!=null && cont.asList().isNamed())
			setAttribute("names",new REXPString(cont.asList().keys()));
		 */
		
		boolean hasAttr = false;
		REXPList a = getAttr();
		RList al = null;
		if (a!=null) al = a.asList();
		if (al != null && al.size()>0) hasAttr=true;
		if (hasAttr)
			l+=attr.getBinaryLength();
		switch (rxt) {
			case XT_NULL:
			case XT_S4:
				break;
			case XT_INT: l+=4; break;
			case XT_DOUBLE: l+=8; break;
			case XT_RAW: l+=4 + cont.asBytes().length; if ((l&3)>0) l=l-(l&3)+4; break;
			case XT_STR:
			case XT_SYMNAME:
				l+=(cont==null)?1:(cont.asString().length()+1);
				if ((l&3)>0) l=l-(l&3)+4;
					break;
			case XT_ARRAY_INT: l+=cont.asIntegers().length*4; break;
			case XT_ARRAY_DOUBLE: l+=cont.asDoubles().length*8; break;
			case XT_ARRAY_CPLX: l+=cont.asDoubles().length*8; break;
			case XT_ARRAY_BOOL: l += cont.asBytes().length + 4; if ((l & 3) > 0) l = l - (l & 3) + 4; break;
			case XT_LIST_TAG:
			case XT_LIST_NOTAG:
			case XT_LANG_TAG:
			case XT_LANG_NOTAG:
			case XT_LIST:
			case XT_VECTOR:
			{
				final RList lst = cont.asList();
				int i=0;
				while (i<lst.size()) {
					REXP x = lst.at(i);
					l += (x==null)?4:(new REXPFactory(x).getBinaryLength());
					if (rxt==XT_LIST_TAG) {
						int pl=l;
						String s = lst.keyAt(i);
						l+=4; // header for a symbol
						l+=(s==null)?1:(s.length()+1);
						if ((l&3)>0) l=l-(l&3)+4;
						// System.out.println("TAG length: "+(l-pl));
					}
					i++;
				}
				if ((l&3)>0) l=l-(l&3)+4;
				break;
			}
			case XT_ARRAY_STR:
			{
				String sa[] = cont.asStrings();
				int i=0;
				while (i < sa.length) {
					if (sa[i] != null) {
						try {
							byte b[] = sa[i].getBytes(RConnection.transferCharset);
							if (b.length > 0) {
								if (b[0] == -1) l++;
								l += b.length;
							} else l++;
							b = null;
						} catch (java.io.UnsupportedEncodingException uex) {
							// FIXME: we should so something ... so far we hope noone's gonna mess with the encoding
						}
					} else l++;
					l++;
					i++;
				}
				if ((l&3)>0) l=l-(l&3)+4;
				break;
			}
		} // switch
        if (l>0xfffff0) l+=4; // large data need 4 more bytes
							  // System.out.println("len:"+(l+4)+" "+xtName(rxt)+"/"+xtName(type)+" "+cont);
		return l+4; // add the header
    }

    /** Stores the REXP in its binary (ready-to-send) representation including header into a buffer and returns the index of the byte behind the REXP.
        <p>Please note that currently only XT_[ARRAY_]INT, XT_[ARRAY_]DOUBLE and XT_[ARRAY_]STR are supported! All other types will be stored as SEXP of the length 0 without any contents.
        @param buf buffer to store the REXP binary into
        @param off offset of the first byte where to store the REXP
        @return the offset of the first byte behind the stored REXP */
    public int getBinaryRepresentation(byte[] buf, int off) throws REXPMismatchException {
		int myl=getBinaryLength();
        boolean isLarge=(myl>0xfffff0);
		boolean hasAttr = false;
		final REXPList a = getAttr();
		RList al = null;
		if (a != null) al = a.asList();
		if (al != null && al.size()>0) hasAttr=true;
		int rxt=type, ooff=off;
		if (type==XT_VECTOR_STR) rxt=XT_ARRAY_STR; // VECTOR_STR is broken right now
		if (type==XT_LIST || type==XT_LIST_TAG || type==XT_LIST_NOTAG)
			rxt=(cont.asList()!=null && cont.asList().isNamed())?XT_LIST_TAG:XT_LIST_NOTAG;
		// System.out.println("@"+off+": "+xtName(rxt)+"/"+xtName(type)+" "+cont+" ("+myl+"/"+buf.length+") att="+hasAttr);
        RTalk.setHdr(rxt|(hasAttr?XT_HAS_ATTR:0),myl-(isLarge?8:4),buf,off);
        off+=(isLarge?8:4);
		if (hasAttr) off=attr.getBinaryRepresentation(buf, off);
		switch (rxt) {
			case XT_S4:
			case XT_NULL:
				break;
			case XT_INT: RTalk.setInt(cont.asInteger(),buf,off); break;
			case XT_DOUBLE: RTalk.setLong(Double.doubleToRawLongBits(cont.asDouble()),buf,off); break;
			case XT_ARRAY_INT:
			{
				int ia[]=cont.asIntegers();
				int i=0, io=off;
				while(i<ia.length) {
					RTalk.setInt(ia[i++],buf,io); io+=4;
				}
				break;
			}
			case XT_ARRAY_BOOL:
			{
				byte ba[] = cont.asBytes();
				int io = off;
				RTalk.setInt(ba.length, buf, io);
				io += 4;
				if (ba.length > 0) {
					for(int i =0; i < ba.length; i++)
						buf[io++] = (byte) ( (ba[i] == REXPLogical.NA) ? 2 : ((ba[i] == REXPLogical.FALSE) ? 0 : 1) );
					while ((io & 3) != 0) buf[io++] = 3;
				}
				break;
			}
			case XT_ARRAY_DOUBLE:
			{
				double da[]=cont.asDoubles();
				int i=0, io=off;
				while(i<da.length) {
					RTalk.setLong(Double.doubleToRawLongBits(da[i++]),buf,io); io+=8;
				}
				break;
			}
			case XT_RAW:
			{
				byte by[] = cont.asBytes();
				RTalk.setInt(by.length, buf, off); off+=4;
				System.arraycopy(by, 0, buf, off, by.length);
				break;
			}
			case XT_ARRAY_STR:
			{
				String sa[] = cont.asStrings();
				int i = 0, io = off;
				while (i < sa.length) {
					if (sa[i] != null) {
						try {
							byte b[] = sa[i].getBytes(RConnection.transferCharset);
							if (b.length > 0) {
								if (b[0] == -1) /* if the first entry happens to be -1 then we need to double it so it doesn't get confused with NAs */
									buf[io++] = -1;
								System.arraycopy(b, 0, buf, io, b.length);
								io += b.length;
							}
							b = null;
						} catch (java.io.UnsupportedEncodingException uex) {
							// FIXME: we should so something ... so far we hope noone's gonna mess with the encoding
						}
					} else
						buf[io++] = -1; /* NAs are stored as 0xff (-1 in signed bytes) */
					buf[io++] = 0;
					i++;
				}
				i = io - off;
				while ((i & 3) != 0) { buf[io++] = 1; i++; } // padding if necessary..
				break;
			}
			case XT_LIST_TAG:
			case XT_LIST_NOTAG:
			case XT_LANG_TAG:
			case XT_LANG_NOTAG:
			case XT_LIST:
			case XT_VECTOR:
			case XT_VECTOR_EXP:
			{
				int io = off;
				final RList lst = cont.asList();
				if (lst != null) {
					int i=0;
					while (i<lst.size()) {
						REXP x = lst.at(i);
						if (x == null) x=new REXPNull();
						io = new REXPFactory(x).getBinaryRepresentation(buf, io);
						if (rxt == XT_LIST_TAG || rxt == XT_LANG_TAG)
							io = new REXPFactory(new REXPSymbol(lst.keyAt(i))).getBinaryRepresentation(buf, io);
						i++;
					}
				}
				// System.out.println("io="+io+", expected: "+(ooff+myl));
				break;
			}
	    
			case XT_SYMNAME:
			case XT_STR:
				getStringBinaryRepresentation(buf, off, cont.asString());
				break;
		}
		return ooff+myl;
    }

    public static int getStringBinaryRepresentation(byte[] buf, int off, String s) {
		if (s==null) s="";
		int io=off;
		try {
			byte b[]=s.getBytes(RConnection.transferCharset);
			// System.out.println("<str> @"+off+", len "+b.length+" (cont "+buf.length+") \""+s+"\"");
			System.arraycopy(b,0,buf,io,b.length);
			io+=b.length;
			b=null;
		} catch (java.io.UnsupportedEncodingException uex) {
			// FIXME: we should so something ... so far we hope noone's gonna mess with the encoding
		}
		buf[io++]=0;
		while ((io&3)!=0) buf[io++]=0; // padding if necessary..
		return io;
    }

    /** returns human-readable name of the xpression type as string. Arrays are denoted by a trailing asterisk (*).
	@param xt xpression type
	@return name of the xpression type */
    public static String xtName(int xt) {
		if (xt==XT_NULL) return "NULL";
		if (xt==XT_INT) return "INT";
		if (xt==XT_STR) return "STRING";
		if (xt==XT_DOUBLE) return "REAL";
		if (xt==XT_BOOL) return "BOOL";
		if (xt==XT_ARRAY_INT) return "INT*";
		if (xt==XT_ARRAY_STR) return "STRING*";
		if (xt==XT_ARRAY_DOUBLE) return "REAL*";
		if (xt==XT_ARRAY_BOOL) return "BOOL*";
		if (xt==XT_ARRAY_CPLX) return "COMPLEX*";
		if (xt==XT_SYM) return "SYMBOL";
		if (xt==XT_SYMNAME) return "SYMNAME";
		if (xt==XT_LANG) return "LANG";
		if (xt==XT_LIST) return "LIST";
		if (xt==XT_LIST_TAG) return "LIST+T";
		if (xt==XT_LIST_NOTAG) return "LIST/T";
		if (xt==XT_LANG_TAG) return "LANG+T";
		if (xt==XT_LANG_NOTAG) return "LANG/T";
		if (xt==XT_CLOS) return "CLOS";
		if (xt==XT_RAW) return "RAW";
		if (xt==XT_S4) return "S4";
		if (xt==XT_VECTOR) return "VECTOR";
		if (xt==XT_VECTOR_STR) return "STRING[]";
		if (xt==XT_VECTOR_EXP) return "EXPR[]";
		if (xt==XT_FACTOR) return "FACTOR";
		if (xt==XT_UNKNOWN) return "UNKNOWN";
		return "<unknown "+xt+">";
    }	
}
