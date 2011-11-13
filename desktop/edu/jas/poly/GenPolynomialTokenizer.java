/*
 * $Id: GenPolynomialTokenizer.java 2987 2010-01-30 20:08:49Z kredel $
 */

package edu.jas.poly;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import java.io.StreamTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.Power;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigComplex;
import edu.jas.arith.BigQuaternion;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
//import edu.jas.poly.OrderedPolynomialList;

import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;

import edu.jas.vector.ModuleList;
import edu.jas.vector.OrderedModuleList;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;


/**
 * GenPolynomial Tokenizer. 
 * Used to read rational polynomials and lists from input streams.
 * @author Heinz Kredel
 */

public class GenPolynomialTokenizer  {

    private static final Logger logger 
            = Logger.getLogger(GenPolynomialTokenizer.class);
    private boolean debug = logger.isDebugEnabled();

    private String[] vars;
    private int nvars = 1;
    private TermOrder tord;
    private RelationTable table;
    //private Reader in;
    private StreamTokenizer tok;

    private RingFactory                                fac;
    //private RingFactory<AlgebraicNumber<BigRational>> anfac;
    //private RingFactory<AlgebraicNumber<ModInteger>>  gffac;
    private static enum coeffType { BigRat, BigInt, ModInt, BigC, BigQ, 
            ANrat, ANmod, RatFunc, ModFunc, IntFunc };
    private coeffType parsedCoeff = coeffType.BigRat;


    private GenPolynomialRing                pfac;
    private static enum polyType { PolBigRat, PolBigInt, PolModInt, PolBigC, 
            PolBigQ, PolANrat, PolANmod, 
            PolRatFunc, PolModFunc, PolIntFunc };
    private polyType parsedPoly = polyType.PolBigRat;

    private GenSolvablePolynomialRing        spfac;



    /**
     * noargs constructor reads from System.in.
     */
    public GenPolynomialTokenizer() {
        this( new BufferedReader( new InputStreamReader( System.in ) ) );
    }


    /**
     * constructor with Ring and Reader.
     * @param rf ring factory.
     * @param r reader stream.
     */
    public GenPolynomialTokenizer(GenPolynomialRing rf, Reader r) {
        this(r);
        if ( rf == null ) {
            return;
        }
        if ( rf instanceof GenSolvablePolynomialRing ) {
            pfac = rf;
            spfac = (GenSolvablePolynomialRing)rf;
        } else {
            pfac = rf;
            spfac = null;
        }
        fac = rf.coFac;
        vars = rf.vars;
        if ( vars != null ) {
            nvars = vars.length;
        }
        tord = rf.tord;
        // relation table
        if ( spfac != null ) {
            table = spfac.table;
        } else {
            table = null;
        }
    }


    /**
     * constructor with Reader.
     * @param r reader stream.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomialTokenizer(Reader r) {
        //BasicConfigurator.configure();
        vars = null;
        tord = new TermOrder();
        //in = r;
        // table = rt;
        nvars = 1;
        //if ( vars != null ) {
        //    nvars = vars.length;
        //}
        //fac = null;
        fac = new BigRational(1);
        
        //pfac = null;
        pfac = new GenPolynomialRing<BigRational>(fac,nvars,tord,vars);

        //spfac = null;
        spfac = new GenSolvablePolynomialRing<BigRational>(fac,nvars,tord,vars);

        tok = new StreamTokenizer( r );
        tok.resetSyntax();
        // tok.eolIsSignificant(true); no more
        tok.eolIsSignificant(false);
        tok.wordChars('0','9');
        tok.wordChars('a', 'z');
        tok.wordChars('A', 'Z');
        tok.wordChars('/', '/'); // wg. rational numbers
        tok.wordChars(128 + 32, 255);
        tok.whitespaceChars(0, ' ');
        tok.commentChar('#');
        tok.quoteChar('"');
        tok.quoteChar('\'');
        //tok.slashStarComments(true); does not work

    }


    /**
     * initialize coefficient and polynomial factories.
     * @param rf ring factory.
     * @param ct coefficient type.
     */
    @SuppressWarnings("unchecked")
    public void initFactory( RingFactory rf, coeffType ct) {
        fac = rf;
        parsedCoeff = ct;

        switch ( ct ) {
        case BigRat: 
            pfac  = new GenPolynomialRing<BigRational>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolBigRat;
            break;
        case BigInt: 
            pfac  = new GenPolynomialRing<BigInteger>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolBigInt;
            break;
        case ModInt: 
            pfac = new GenPolynomialRing<ModInteger>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolModInt;
            break;
        case BigC: 
            pfac  = new GenPolynomialRing<BigComplex>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolBigC;
            break;
        case BigQ: 
            pfac  = new GenPolynomialRing<BigQuaternion>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolBigQ;
            break;
        case RatFunc: 
            pfac  = new GenPolynomialRing<Quotient<BigInteger>>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolRatFunc;
            break;
        case ModFunc: 
            pfac  = new GenPolynomialRing<Quotient<ModInteger>>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolModFunc;
            break;
        case IntFunc: 
            pfac  = new GenPolynomialRing<GenPolynomial<BigRational>>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolIntFunc;
            break;
        default: 
            pfac  = new GenPolynomialRing<BigRational>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolBigRat;
        }
    }


    /**
     * initialize coefficient and solvable polynomial factories.
     * @param rf ring factory.
     * @param ct coefficient type.
     */
    @SuppressWarnings("unchecked")
    public void initSolvableFactory( RingFactory rf, coeffType ct) {
        fac = rf;
        parsedCoeff = ct;

        switch ( ct ) {
        case BigRat: 
            spfac  = new GenSolvablePolynomialRing<BigRational>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolBigRat;
            break;
        case BigInt: 
            spfac  = new GenSolvablePolynomialRing<BigInteger>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolBigInt;
            break;
        case ModInt: 
            spfac = new GenSolvablePolynomialRing<ModInteger>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolModInt;
            break;
        case BigC: 
            spfac  = new GenSolvablePolynomialRing<BigComplex>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolBigC;
            break;
        case BigQ: 
            spfac  = new GenSolvablePolynomialRing<BigQuaternion>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolBigQ;
            break;
        case RatFunc: 
            spfac  = new GenSolvablePolynomialRing<Quotient<BigInteger>>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolRatFunc;
            break;
        case ModFunc: 
            spfac  = new GenSolvablePolynomialRing<Quotient<ModInteger>>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolModFunc;
            break;
        case IntFunc: 
            spfac  = new GenSolvablePolynomialRing<GenPolynomial<BigRational>>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolModFunc;
            break;
        default: 
            spfac  = new GenSolvablePolynomialRing<BigRational>(fac,nvars,tord,vars);
            parsedPoly = polyType.PolBigRat;
        }
    }


    /**
     * parsing method for GenPolynomial.
     * syntax ? (simple)
     * @return the next polynomial.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial nextPolynomial() throws IOException {
        if (debug) {
            logger.debug("torder = " + tord);
        }
        GenPolynomial a  = pfac.getZERO();
        GenPolynomial a1 = pfac.getONE();
        ExpVector leer = pfac.evzero;

        if (debug) { 
            logger.debug("a = " + a);
            logger.debug("a1 = " + a1);
        }
        GenPolynomial b = a1;
        GenPolynomial c;
        int tt; //, oldtt;
        //String rat = "";
        char first;
        RingElem r;
        ExpVector e;
        int ix;
        long ie;
        boolean done = false;
        while ( !done ) {
            // next input. determine next action
            tt = tok.nextToken();
            //System.out.println("while tt = " + tok);
            logger.debug("while tt = " + tok);
            if ( tt == StreamTokenizer.TT_EOF ) break;
            switch ( tt ) {
            case ')': 
            case ',': 
                return a; // do not change or remove
            case '-': 
                b = b.negate(); 
            case '+': 
            case '*': 
                tt = tok.nextToken();
                break;
            default: // skip
            }
            // read coefficient, monic monomial and polynomial
            if ( tt == StreamTokenizer.TT_EOF ) break;
            switch ( tt ) {
            case '_': 
                StringBuffer cf = new StringBuffer();
                tt = tok.nextToken();
                //System.out.println("tt = " + tt );
                while ( tt != '_' ) {
                    //cf.append( " " );
                    if ( tok.sval != null ) {
                        cf.append( " " + tok.sval );
                    } else {
                        cf.append( (char)tt );
                    }
                    tt = tok.nextToken();
                }
                //System.out.println("coeff = " + cf.toString() );
                r = (RingElem)fac.parse( cf.toString() );
                //System.out.println("r = " + r );
                if (debug) logger.debug("coeff " + r);
                b = b.multiply(r,leer); 
                tt = tok.nextToken();
                if (debug) logger.debug("tt,digit = " + tok);
                //no break;
                break;

            case '{': 
                StringBuffer rf = new StringBuffer();
                int level = 0;
                do {
                    tt = tok.nextToken();
                    //System.out.println("token { = " + ((char)tt) + ", " + tt + ", level = " + level);
                    //cf.append( " " );
                    if ( tt == '{' ) {
                        level++;
                    }
                    if ( tt == '}' ) {
                        level--;
                        if ( level < 0 ) {
                            continue; // skip last closing brace 
                        }
                    }
                    if ( tok.sval != null ) {
                        rf.append( " " + tok.sval );
                    } else {
                        rf.append( (char)tt );
                    }
                } while ( level >= 0 ); // || tt != '}' 
                //System.out.println("coeff = " + rf.toString() );
                r = (RingElem)fac.parse( rf.toString() );
                if (debug) logger.debug("coeff " + r);
                ie = nextExponent();
                if (debug) logger.debug("ie " + ie);
                r = Power.<RingElem>positivePower(r,ie);
                if (debug) logger.debug("coeff^ie " + r);
                b = b.multiply(r,leer); 
                tt = tok.nextToken();
                if (debug) logger.debug("tt,digit = " + tok);
                //no break;
                break;

            case StreamTokenizer.TT_WORD: 
                //System.out.println("TT_WORD: " + tok.sval);
                if ( tok.sval == null || tok.sval.length() == 0 ) break;
                // read coefficient
                first = tok.sval.charAt(0);
                if ( digit(first) ) {
                    r = (RingElem)fac.parse( tok.sval );
                    //System.out.println("r = " + r.toScriptFactory());
                    ie = nextExponent();
                    if (debug) logger.debug("ie " + ie);
                    // r = r^ie;
                    r = Power.<RingElem>positivePower(r,ie);
                    if (debug) logger.debug("coeff^ie " + r);
                    b = b.multiply(r,leer); 
                    tt = tok.nextToken();
                    if (debug) logger.debug("tt,digit = " + tok);
                } 
                if ( tt == StreamTokenizer.TT_EOF ) break;
                if ( tok.sval == null ) break;
                // read monomial 
                first = tok.sval.charAt(0);
                if ( letter(first) ) {
                    ix = indexVar( tok.sval );
                    if ( ix < 0 ) {
                        logger.error("Unkonown varibable " + tok.sval); 
                        done = true;
                        break;
                    }
                    //  System.out.println("ix: " + ix);
                    ie = nextExponent();
                    //  System.out.println("ie: " + ie);
                    // r = BigRational.RNONE;
                    e = ExpVector.create( vars.length, ix, ie);
                    //c = new GenPolynomial<BigRational>(r,e);
                    b = b.multiply(e); 
                    tt = tok.nextToken();
                    if (debug) logger.debug("tt,letter = " + tok);
                }
                break;

            case '(': 
                c = nextPolynomial();
                if (debug) logger.debug("factor " + c);
                ie = nextExponent();
                if (debug) logger.debug("ie " + ie);
                c = Power.<GenPolynomial>positivePower(c,ie);
                if (debug) logger.debug("factor^ie " + c);
                b = b.multiply(c); 
                tt = tok.nextToken();
                if (debug) logger.debug("tt,digit = " + tok);
                //no break;
                break;

            default: //skip 
            }
            if ( done ) break; // unknown variable
            if ( tt == StreamTokenizer.TT_EOF ) break;
            // complete polynomial
            tok.pushBack();
            switch ( tt ) {
            case '-': 
            case '+': 
            case ')': 
            case ',': 
                logger.debug("b, = " + b);
                a = a.sum(b); 
                b = a1;
                break;
            case '*': 
                logger.debug("b, = " + b);
                //a = a.sum(b); 
                //b = a1;
                break;
            case '\n':
                tt = tok.nextToken();
                if (debug) logger.debug("tt,nl = " + tt);
            default: // skip or finish ?
                if (debug) logger.debug("default: " + tok);
            }
        }
        if (debug) logger.debug("b = " + b);
        a = a.sum(b); 
        logger.debug("a = " + a);
        // b = a1;
        return a;
    }


    /**
     * parsing method for exponent (of variable).
     * syntax: ^long | **long.
     * @return the next exponent or 1.
     * @throws IOException
     */
    public long nextExponent() throws IOException {
        long e = 1;
        char first;
        int tt;
        tt = tok.nextToken();
        if ( tt == '^' ) {
            if (debug) logger.debug("exponent ^");
            tt = tok.nextToken();
            if ( tok.sval != null ) {
                first = tok.sval.charAt(0);
                if ( digit(first) ) {
                    e = Long.parseLong( tok.sval );
                    return e;
                }
            }
        }
        if ( tt == '*' ) {
            tt = tok.nextToken();
            if ( tt == '*' ) {
                if (debug) logger.debug("exponent **");
                tt = tok.nextToken();
                if ( tok.sval != null ) {
                    first = tok.sval.charAt(0);
                    if ( digit(first) ) {
                        e = Long.parseLong( tok.sval );
                        return e;
                    }
                }
            }
            tok.pushBack();
        }
        tok.pushBack();
        return e;
    }


    /**
     * parsing method for comments.
     * syntax: (* comment *) | /_* comment *_/ without _
     * Does not work with this pushBack(), unused.
     */
    public String nextComment() throws IOException {
        // syntax: (* comment *) | /* comment */ 
        StringBuffer c = new StringBuffer();
        int tt;
        if (debug) logger.debug("comment: " + tok);
        tt = tok.nextToken();
        if (debug) logger.debug("comment: " + tok);
        if ( tt == '(' ) {
            tt = tok.nextToken();
            if (debug) logger.debug("comment: " + tok);
            if ( tt == '*' ) {
                if (debug) logger.debug("comment: ");
                while (true) { 
                    tt = tok.nextToken();
                    if ( tt == '*' ) {
                        tt = tok.nextToken();
                        if ( tt == ')' ) {
                            return c.toString();
                        } 
                        tok.pushBack();
                    }
                    c.append(tok.sval);
                }
            } 
            tok.pushBack();
            if (debug) logger.debug("comment: " + tok);
        }
        tok.pushBack();
        if (debug) logger.debug("comment: " + tok);
        return c.toString();
    }


    /**
     * parsing method for variable list.
     * syntax: (a, b c, de) gives [ "a", "b", "c", "de" ]
     * @return the next variable list.
     * @throws IOException
     */
    public String[] nextVariableList() throws IOException {
        List<String> l = new ArrayList<String>();
        int tt;
        tt = tok.nextToken();
        //System.out.println("vList tok = " + tok);
        if ( tt == '(' || tt == '{') {
            logger.debug("variable list");
            tt = tok.nextToken();
            while ( true ) {
                if ( tt == StreamTokenizer.TT_EOF ) break;
                if ( tt == ')' || tt == '}' ) break;
                if ( tt == StreamTokenizer.TT_WORD ) {
                    //System.out.println("TT_WORD: " + tok.sval);
                    l.add( tok.sval );
                }
                tt = tok.nextToken();
            }
        }
        Object[] ol = l.toArray();
        String[] v = new String[ol.length];
        for (int i=0; i < v.length; i++ ) {
            v[i] = (String) ol[i];
        }
        return v;
    }


    /**
     * parsing method for coefficient ring.
     * syntax: Rat | Q | Int | Z | Mod modul | Complex 
     *         | C | Quat 
     *         | AN[ (var) ( poly ) | AN[ modul (var) ( poly ) ]
     *         | RatFunc (var_list) | ModFunc modul (var_list) | IntFunc (var_list) 
     * @return the next coefficient factory.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public RingFactory nextCoefficientRing() throws IOException {
        RingFactory coeff = null;
        coeffType ct = null;
        int tt;
        tt = tok.nextToken();
        if ( tok.sval != null ) {
            if ( tok.sval.equalsIgnoreCase("Q") ) {
                coeff = new BigRational(0);
                ct = coeffType.BigRat;
            }
            if ( tok.sval.equalsIgnoreCase("Rat") ) {
                coeff = new BigRational(0);
                ct = coeffType.BigRat;
            }
            if ( tok.sval.equalsIgnoreCase("Z") ) {
                coeff = new BigInteger(0);
                ct = coeffType.BigInt;
            }
            if ( tok.sval.equalsIgnoreCase("Int") ) {
                coeff = new BigInteger(0);
                ct = coeffType.BigInt;
            }
            if ( tok.sval.equalsIgnoreCase("C") ) {
                coeff = new BigComplex(0);
                ct = coeffType.BigC;
            }
            if ( tok.sval.equalsIgnoreCase("Complex") ) {
                coeff = new BigComplex(0);
                ct = coeffType.BigC;
            }
            if ( tok.sval.equalsIgnoreCase("Quat") ) {
                coeff = new BigQuaternion(0);
                ct = coeffType.BigQ;
            }
            if ( tok.sval.equalsIgnoreCase("Mod") ) {
                tt = tok.nextToken();
                boolean openb = false;
                if ( tt == '[' ) { // optional
                    openb = true;
                    tt = tok.nextToken();
                }
                if ( tok.sval != null && tok.sval.length() > 0 ) {
                    if ( digit( tok.sval.charAt(0) ) ) {
                        coeff = new ModIntegerRing(tok.sval);
                        ct = coeffType.ModInt;
                    } else {
                        tok.pushBack();
                    }
                } else {
                    tok.pushBack();
                }
                if ( tt == ']' && openb ) { // optional
                    tt = tok.nextToken();
                }
            } else if ( tok.sval.equalsIgnoreCase("RatFunc") ) {
                String[] rfv = nextVariableList();
                //System.out.println("rfv = " + rfv.length + " " + rfv[0]);
                int vr = rfv.length;
                BigInteger bi = new BigInteger();
                TermOrder to = new TermOrder( TermOrder.INVLEX );
                GenPolynomialRing<BigInteger> pcf 
                    = new GenPolynomialRing<BigInteger>( bi, vr, to, rfv );
                coeff = new QuotientRing( pcf );
                ct = coeffType.RatFunc;
            } else if ( tok.sval.equalsIgnoreCase("ModFunc") ) {
                tt = tok.nextToken();
                RingFactory mi = new ModIntegerRing("19");
                if ( tok.sval != null && tok.sval.length() > 0 ) {
                   if ( digit( tok.sval.charAt(0) ) ) {
                      mi = new ModIntegerRing(tok.sval);
                   } else {
                      tok.pushBack();
                   }
                } else {
                   tok.pushBack();
                }
                String[] rfv = nextVariableList();
                //System.out.println("rfv = " + rfv.length + " " + rfv[0]);
                int vr = rfv.length;
                TermOrder to = new TermOrder( TermOrder.INVLEX );
                GenPolynomialRing<ModInteger> pcf 
                    = new GenPolynomialRing<ModInteger>( mi, vr, to, rfv );
                coeff = new QuotientRing( pcf );
                ct = coeffType.ModFunc;
            } else if ( tok.sval.equalsIgnoreCase("IntFunc") ) {
                String[] rfv = nextVariableList();
                //System.out.println("rfv = " + rfv.length + " " + rfv[0]);
                int vr = rfv.length;
                BigRational bi = new BigRational();
                TermOrder to = new TermOrder( TermOrder.INVLEX );
                GenPolynomialRing<BigRational> pcf 
                    = new GenPolynomialRing<BigRational>( bi, vr, to, rfv );
                coeff = pcf;
                ct = coeffType.IntFunc;
            } else if ( tok.sval.equalsIgnoreCase("AN") ) {
                tt = tok.nextToken();
                if ( tt == '[' ) {
                    tt = tok.nextToken();
                    RingFactory tcfac = new ModIntegerRing("19");
                    if ( tok.sval != null && tok.sval.length() > 0 ) {
                        if ( digit( tok.sval.charAt(0) ) ) {
                            tcfac = new ModIntegerRing(tok.sval);
                        } else {
                            tcfac = new BigRational();
                            tok.pushBack();
                        }
                    } else {
                        tcfac = new BigRational();
                        tok.pushBack();
                    }
                    String[] anv = nextVariableList();
                    //System.out.println("anv = " + anv.length + " " + anv[0]);
                    int vs = anv.length;
                    if ( vs != 1 ) {
                       logger.error("AlgebraicNumber only for univariate polynomials");
                    }
                    String[] ovars = vars;
                    vars = anv;
                    GenPolynomialRing tpfac = pfac;
                    RingFactory tfac = fac;
                    fac = tcfac;
                    // pfac and fac used in nextPolynomial()
                    if ( tcfac instanceof ModIntegerRing ) {
                        pfac = new GenPolynomialRing<ModInteger>( tcfac, vs, new TermOrder(), anv );
                    } else {
                        pfac = new GenPolynomialRing<BigRational>( tcfac, vs, new TermOrder(), anv );
                    }
                    if ( debug ) {
                        logger.debug("pfac = " + pfac);
                    }
                    tt = tok.nextToken();
                    GenPolynomial mod;
                    if ( tt == '(' ) {
                        mod = nextPolynomial();
                        tt = tok.nextToken();
                        if ( tok.ttype != ')' ) tok.pushBack();
                    } else { 
                        tok.pushBack();
                        mod = nextPolynomial();
                    }
                    if ( debug ) {
                        logger.debug("mod = " + mod);
                    }
                    pfac = tpfac;
                    fac = tfac;
                    vars = ovars;
                    if ( tcfac instanceof ModIntegerRing ) {
                        GenPolynomial<ModInteger> gfmod;
                        gfmod = (GenPolynomial<ModInteger>)mod;
                        coeff = new AlgebraicNumberRing<ModInteger>( gfmod );
                        ct = coeffType.ANmod;
                    } else {
                        GenPolynomial<BigRational> anmod;
                        anmod = (GenPolynomial<BigRational>)mod;
                        coeff = new AlgebraicNumberRing<BigRational>( anmod );
                        ct = coeffType.ANrat;
                    }
                    if ( debug ) {
                        logger.debug("coeff = " + coeff);
                    }
                    tt = tok.nextToken();
                    if ( tt == ']' ) {
                        //ok, no nextToken();
                    } else {
                        tok.pushBack();
                    }
                } else {
                    tok.pushBack();
                }
            }
        } 
        if ( coeff == null ) {
            tok.pushBack();
            coeff = new BigRational();
            ct = coeffType.BigRat;
        }
        parsedCoeff = ct;
        return coeff;
    }


    /**
     * parsing method for weight list.
     * syntax: (w1, w2, w3, ..., wn)
     * @return the next weight list.
     * @throws IOException
     */
    public long[] nextWeightList() throws IOException {
        List<Long> l = new ArrayList<Long>();
        long[] w = null;
        long e;
        char first;
        int tt;
        tt = tok.nextToken();
        if ( tt == '(' ) {
            logger.debug("weight list");
            tt = tok.nextToken();
            while ( true ) {
                if ( tt == StreamTokenizer.TT_EOF ) break;
                if ( tt == ')' ) break;
                if ( tok.sval != null ) {
                    first = tok.sval.charAt(0);
                    if ( digit(first) ) {
                        e = Long.parseLong( tok.sval );
                        l.add( new Long(e) );
                        //System.out.println("w: " + e);
                    }
                }
                tt = tok.nextToken(); // also comma
            }
        }
        Object[] ol = l.toArray();
        w = new long[ ol.length ];
        for ( int i=0; i < w.length; i++ ) {
            w[i] = ((Long)ol[ ol.length-i-1 ]).longValue();
        }
        return w;
    }


    /**
     * parsing method for weight array.
     * syntax: ( (w11, ...,w1n), ..., (wm1, ..., wmn) )
     * @return the next weight array.
     * @throws IOException
     */
    public long[][] nextWeightArray() throws IOException {
        List<long[]> l = new ArrayList<long[]>();
        long[][] w = null;
        long[] e;
        char first;
        int tt;
        tt = tok.nextToken();
        if ( tt == '(' ) {
            logger.debug("weight array");
            tt = tok.nextToken();
            while ( true ) {
                if ( tt == StreamTokenizer.TT_EOF ) break;
                if ( tt == ')' ) break;
                if ( tt == '(' ) {
                    tok.pushBack();
                    e = nextWeightList();
                    l.add( e );
                    //System.out.println("wa: " + e);
                } else if ( tok.sval != null ) {
                    first = tok.sval.charAt(0);
                    if ( digit(first) ) {
                        tok.pushBack();
                        tok.pushBack();
                        e = nextWeightList();
                        l.add( e );
                        break;
                        //System.out.println("w: " + e);
                    }
                }
                tt = tok.nextToken(); // also comma
            }
        }
        Object[] ol = l.toArray();
        w = new long[ ol.length ][];
        for ( int i=0; i < w.length; i++ ) {
            w[i] = (long[])ol[ i ];
        }
        return w;
    }


    /**
     * parsing method for split index.
     * syntax: |i|
     * @return the next split index.
     * @throws IOException
     */
    public int nextSplitIndex() throws IOException {
        int e = -1; // =unknown
        int e0 = -1; // =unknown
        char first;
        int tt;
        tt = tok.nextToken();
        if ( tt == '|' ) {
            logger.debug("split index");
            tt = tok.nextToken();
            if ( tt == StreamTokenizer.TT_EOF ) {
                return e;
            }
            if ( tok.sval != null ) {
                first = tok.sval.charAt(0);
                if ( digit(first) ) {
                    e = Integer.parseInt( tok.sval );
                    //System.out.println("w: " + i);
                }
                tt = tok.nextToken();
                if ( tt != '|' ) {
                    tok.pushBack();
                }
            }
        } else if ( tt == '[' ) {
            logger.debug("split index");
            tt = tok.nextToken();
            if ( tt == StreamTokenizer.TT_EOF ) {
                return e;
            }
            if ( tok.sval != null ) {
                first = tok.sval.charAt(0);
                if ( digit(first) ) {
                    e0 = Integer.parseInt( tok.sval );
                    //System.out.println("w: " + i);
                }
                tt = tok.nextToken();
                if ( tt == ',' ) {
                    tt = tok.nextToken();
                    if ( tt == StreamTokenizer.TT_EOF ) {
                        return e;
                    }
                    if ( tok.sval != null ) {
                        first = tok.sval.charAt(0);
                        if ( digit(first) ) {
                            e = Integer.parseInt( tok.sval );
                            //System.out.println("w: " + i);
                        }
                    }
                    if ( tt != ']' ) {
                        tok.pushBack();
                    }
                }
            }
        } else {
            tok.pushBack();
        }
        return e;
    }


    /**
     * parsing method for term order name.
     * syntax: termOrderName = L, IL, LEX, G, IG, GRLEX, W(weights)
     *         |split index|
     * @return the next term order.
     * @throws IOException
     */
    public TermOrder nextTermOrder() throws IOException {
        int evord = TermOrder.DEFAULT_EVORD;
        int tt;
        tt = tok.nextToken();
        if ( tt == StreamTokenizer.TT_EOF ) { /* nop */
        }
        if ( tt == StreamTokenizer.TT_WORD ) {
            // System.out.println("TT_WORD: " + tok.sval);
            if ( tok.sval != null ) {
                if ( tok.sval.equalsIgnoreCase("L") ) {
                    evord = TermOrder.INVLEX;
                }
                if ( tok.sval.equalsIgnoreCase("IL") ) {
                    evord = TermOrder.INVLEX;
                }
                if ( tok.sval.equalsIgnoreCase("INVLEX") ) {
                    evord = TermOrder.INVLEX;
                }
                if ( tok.sval.equalsIgnoreCase("LEX") ) {
                    evord = TermOrder.LEX;
                }
                if ( tok.sval.equalsIgnoreCase("G") ) {
                    evord = TermOrder.IGRLEX;
                }
                if ( tok.sval.equalsIgnoreCase("IG") ) {
                    evord = TermOrder.IGRLEX;
                }
                if ( tok.sval.equalsIgnoreCase("IGRLEX") ) {
                    evord = TermOrder.IGRLEX;
                }
                if ( tok.sval.equalsIgnoreCase("GRLEX") ) {
                    evord = TermOrder.GRLEX;
                }
                if ( tok.sval.equalsIgnoreCase("W") ) {
                    long[][] w = nextWeightArray();
                    //int s = nextSplitIndex(); // no more
                    //if ( s <= 0 ) {
                    return new TermOrder( w );
                    //} else {
                    //return new TermOrder( w, s );
                    //}
                }
            }
        }
        int s = nextSplitIndex();
        if ( s <= 0 ) {
            return new TermOrder( evord );
        } else {
            return new TermOrder( evord, evord, vars.length, s );
        }
    }


    /**
     * parsing method for polynomial list.
     * syntax: ( p1, p2, p3, ..., pn )
     * @return the next polynomial list.
     * @throws IOException
     */
    public List<GenPolynomial> nextPolynomialList() throws IOException {
        GenPolynomial a;
        List<GenPolynomial> L = new ArrayList<GenPolynomial>();
        int tt;
        tt = tok.nextToken();
        if ( tt == StreamTokenizer.TT_EOF ) return L;
        if ( tt != '(' ) return L;
        logger.debug("polynomial list");
        while ( true ) {
            tt = tok.nextToken();
            if ( tok.ttype == ',' ) continue;
            if ( tt == '(' ) {
                a = nextPolynomial();
                tt = tok.nextToken();
                if ( tok.ttype != ')' ) tok.pushBack();
            } else { tok.pushBack();
                a = nextPolynomial();
            }
            logger.info("next pol = " + a); 
            L.add( a );
            if ( tok.ttype == StreamTokenizer.TT_EOF ) break;
            if ( tok.ttype == ')' ) break;
        }
        return L;
    }


    /**
     * parsing method for submodule list.
     * syntax: ( ( p11, p12, p13, ..., p1n ), 
     *           ..., 
     *           ( pm1, pm2, pm3, ..., pmn ) )
     * @return the next list of polynomial lists.
     * @throws IOException
     */
    public List< List<GenPolynomial> > nextSubModuleList() 
           throws IOException {
        List<List<GenPolynomial>> L = new ArrayList<List<GenPolynomial>>();
        int tt;
        tt = tok.nextToken();
        if ( tt == StreamTokenizer.TT_EOF ) return L;
        if ( tt != '(' ) return L;
        logger.debug("module list");
        List<GenPolynomial> v = null;
        while ( true ) {
            tt = tok.nextToken();
            if ( tok.ttype == ',' ) continue;
            if ( tok.ttype == ')' ) break;
            if ( tok.ttype == StreamTokenizer.TT_EOF ) break;
            if ( tt == '(' ) {
                tok.pushBack();
                v = nextPolynomialList();
                logger.info("next vect = " + v); 
                L.add( v );
            }
        }
        return L;
    }


    /**
     * parsing method for solvable polynomial relation table.
     * syntax: ( p_1, p_2, p_3, ..., p_{n+3} )
     * semantics: p_{n+1} * p_{n+2} = p_{n+3} 
     * The next relation table is stored into 
     * the solvable polynomial factory.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void nextRelationTable() throws IOException {
        if ( spfac == null ) {
            return;
        }
        RelationTable table = spfac.table;
        List<GenPolynomial> rels = null;
        GenPolynomial p;
        GenSolvablePolynomial sp;
        int tt;
        tt = tok.nextToken();
        if ( tok.sval != null ) {
            if ( tok.sval.equalsIgnoreCase("RelationTable") ) {
                rels = nextPolynomialList();
            }
        } 
        if ( rels == null ) {
            tok.pushBack();
            return;
        } 
        for ( Iterator<GenPolynomial> it = rels.iterator(); it.hasNext(); ) {
            p = it.next();
            ExpVector e = p.leadingExpVector();
            if ( it.hasNext() ) {
                p = it.next();
                ExpVector f = p.leadingExpVector();
                if ( it.hasNext() ) {
                    p = it.next();
                    sp = new GenSolvablePolynomial(spfac,p.val);
                    table.update( e, f, sp );
                }
            }
        }
        if ( debug ) {
            logger.info("table = " + table);
        }
        return;
    }


    /**
     * parsing method for polynomial set.
     * syntax: coeffRing varList termOrderName polyList.
     * @return the next polynomial set.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public PolynomialList nextPolynomialSet() throws IOException {
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = " + comments);

        RingFactory coeff = nextCoefficientRing();
        logger.info("coeff = " + coeff); 

        vars = nextVariableList();
        String dd = "vars ="; 
        for (int i = 0; i < vars.length ;i++) {
            dd+= " "+vars[i]; 
        }
        logger.info(dd); 
        if ( vars != null ) {
            nvars = vars.length;
        }

        tord = nextTermOrder();
        logger.info("tord = " + tord); 
        // check more TOs

        initFactory(coeff,parsedCoeff); // global: nvars, tord, vars
        List< GenPolynomial > s = null;
        s = nextPolynomialList();
        logger.info("s = " + s); 
        // comments += nextComment();
        return new PolynomialList(pfac,s);
    }


    /**
     * parsing method for module set.
     * syntax: coeffRing varList termOrderName moduleList.
     * @return the next module set.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public ModuleList nextSubModuleSet() throws IOException {
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = " + comments);

        RingFactory coeff = nextCoefficientRing();
        logger.info("coeff = " + coeff); 

        vars = nextVariableList();
        String dd = "vars ="; 
        for (int i = 0; i < vars.length ;i++) {
            dd+= " "+vars[i]; 
        }
        logger.info(dd); 
        if ( vars != null ) {
            nvars = vars.length;
        }

        tord = nextTermOrder();
        logger.info("tord = " + tord); 
        // check more TOs

        initFactory(coeff,parsedCoeff); // global: nvars, tord, vars
        List< List< GenPolynomial > > m = null;
        m = nextSubModuleList();
        logger.info("m = " + m); 
        // comments += nextComment();

        return new ModuleList(pfac,m);
    }


    /**
     * parsing method for solvable polynomial list.
     * syntax: ( p1, p2, p3, ..., pn )
     * @return the next solvable polynomial list.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public List<GenSolvablePolynomial> nextSolvablePolynomialList() 
        throws IOException {
        List<GenPolynomial> s = nextPolynomialList();
        logger.info("s = " + s); 
        // comments += nextComment();

        GenPolynomial p;
        GenSolvablePolynomial ps;
        List<GenSolvablePolynomial> sp 
            = new ArrayList<GenSolvablePolynomial>( s.size() );
        for ( Iterator<GenPolynomial> it = s.iterator(); it.hasNext(); ) {
            p = it.next();
            ps = new GenSolvablePolynomial(spfac,p.val);
            //System.out.println("ps = " + ps);
            sp.add( ps );
        }
        return sp;
    }


    /**
     * parsing method for solvable polynomial.
     * syntax: p.
     * @return the next polynomial.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public GenSolvablePolynomial nextSolvablePolynomial() 
        throws IOException {
        GenPolynomial p = nextPolynomial();
        logger.info("p = " + p); 
        // comments += nextComment();

        GenSolvablePolynomial ps
            = new GenSolvablePolynomial(spfac,p.val);
        //System.out.println("ps = " + ps);
        return ps;
    }


    /**
     * parsing method for solvable polynomial set.
     * syntax: varList termOrderName relationTable polyList.
     * @return the next solvable polynomial set.
     * @throws IOException
     */

    @SuppressWarnings("unchecked")
    public PolynomialList nextSolvablePolynomialSet() throws IOException {
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = " + comments);

        RingFactory coeff = nextCoefficientRing();
        logger.info("coeff = " + coeff); 

        vars = nextVariableList();
        String dd = "vars ="; 
        for (int i = 0; i < vars.length ;i++) {
            dd+= " "+vars[i]; 
        }
        logger.info(dd); 
        if ( vars != null ) {
            nvars = vars.length;
        }

        tord = nextTermOrder();
        logger.info("tord = " + tord); 
        // check more TOs

        initFactory(coeff,parsedCoeff);  // must be because of symmetric read
        initSolvableFactory(coeff,parsedCoeff); // global: nvars, tord, vars

        //System.out.println("pfac = " + pfac);
        //System.out.println("spfac = " + spfac);

        nextRelationTable();
        if ( logger.isInfoEnabled() ) {
            logger.info("table = " + table); 
        }

        List< GenSolvablePolynomial > s = null;
        s = nextSolvablePolynomialList();
        logger.info("s = " + s); 
        // comments += nextComment();
        return new PolynomialList(spfac,s); // Ordered ?
    }


    /**
     * parsing method for solvable submodule list.
     * syntax: ( ( p11, p12, p13, ..., p1n ), 
     *           ..., 
     *           ( pm1, pm2, pm3, ..., pmn ) )
     * @return the next list of solvable polynomial lists.
     * @throws IOException
     */
    public List< List<GenSolvablePolynomial> > nextSolvableSubModuleList() 
        throws IOException {
        List<List<GenSolvablePolynomial>> L 
            = new ArrayList<List<GenSolvablePolynomial>>();
        int tt;
        tt = tok.nextToken();
        if ( tt == StreamTokenizer.TT_EOF ) return L;
        if ( tt != '(' ) return L;
        logger.debug("module list");
        List<GenSolvablePolynomial> v = null;
        while ( true ) {
            tt = tok.nextToken();
            if ( tok.ttype == ',' ) continue;
            if ( tok.ttype == ')' ) break;
            if ( tok.ttype == StreamTokenizer.TT_EOF ) break;
            if ( tt == '(' ) {
                tok.pushBack();
                v = nextSolvablePolynomialList();
                logger.info("next vect = " + v); 
                L.add( v );
            }
        }
        return L;
    }


    /**
     * parsing method for solvable module set.
     * syntax: varList termOrderName relationTable moduleList.
     * @return the next solvable module set.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public ModuleList nextSolvableSubModuleSet() throws IOException {
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = " + comments);

        RingFactory coeff = nextCoefficientRing();
        logger.info("coeff = " + coeff); 

        vars = nextVariableList();
        String dd = "vars ="; 
        for (int i = 0; i < vars.length ;i++) {
            dd+= " "+vars[i]; 
        }
        logger.info(dd); 
        if ( vars != null ) {
            nvars = vars.length;
        }

        tord = nextTermOrder();
        logger.info("tord = " + tord); 
        // check more TOs

        initFactory(coeff,parsedCoeff);  // must be because of symmetric read
        initSolvableFactory(coeff,parsedCoeff); // global: nvars, tord, vars

        //System.out.println("spfac = " + spfac);

        nextRelationTable();
        if ( logger.isInfoEnabled() ) {
            logger.info("table = " + table); 
        }

        List<List<GenSolvablePolynomial>> s = null;
        s = nextSolvableSubModuleList();
        logger.info("s = " + s); 
        // comments += nextComment();

        return new OrderedModuleList(spfac,s); // Ordered
    }

    // must also allow +/- // does not work with tokenizer
    @SuppressWarnings("unused")
	private boolean number(char x) {
        return digit(x) || x == '-' || x == '+';
    }

    private boolean digit(char x) {
        return '0' <= x && x <= '9';
    }


    private boolean letter(char x) {
        return ( 'a' <= x && x <= 'z' ) || ( 'A' <= x && x <= 'Z' );
    }


    private int indexVar(String x) {
        for ( int i = 0; i < vars.length; i++ ) { 
            if ( x.equals( vars[i] ) ) { 
                return vars.length-i-1;
            }
        }
        return -1; // not found
    }


    // unused
    public void nextComma() throws IOException {
        int tt;
        if ( tok.ttype == ',' ) {
            if (debug) logger.debug("comma: ");
            tt = tok.nextToken();
        }
    }

}
