package org.mathpiper.mpreduce.functions.functionwithenvironment;

//

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.Spid;
import org.mathpiper.mpreduce.datatypes.Cons;
import org.mathpiper.mpreduce.datatypes.LispVector;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.functions.builtin.Fns;
import org.mathpiper.mpreduce.functions.lisp.LispFunction;
import org.mathpiper.mpreduce.numbers.LispInteger;
import org.mathpiper.mpreduce.numbers.LispNumber;
import org.mathpiper.mpreduce.symbols.Symbol;

// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2000.
//

/**************************************************************************
 * Copyright (C) 1998-2011, Codemist Ltd.                A C Norman       *
 *                            also contributions from Vijay Chauhan, 2002 *
 *                                                                        *
 * Redistribution and use in source and binary forms, with or without     *
 * modification, are permitted provided that the following conditions are *
 * met:                                                                   *
 *                                                                        *
 *     * Redistributions of source code must retain the relevant          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer.                                                      *
 *     * Redistributions in binary form must reproduce the above          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer in the documentation and/or other materials provided  *
 *       with the distribution.                                           *
 *                                                                        *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE         *
 * COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,   *
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS  *
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND *
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR  *
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF     *
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH   *
 * DAMAGE.                                                                *
 *************************************************************************/
public class Bytecode extends FnWithEnv
{

public Bytecode()
{
    env = new LispObject [0];
    bytecodes = null;
    nargs = 0;
}

public Bytecode(LispObject [] env)
{
    this.env = env;
    bytecodes = null;
    nargs = 0;
}

public Bytecode(int n)
{
    env = new LispObject [0];
    bytecodes = new byte [n];
    nargs = 0;
}


static StringBuffer sb = new StringBuffer();

private void handleInterrupt() throws Exception
{
    Jlisp.interruptEvaluation = false;
    Jlisp.error("Evaluation Interrupted.");
}

String printAs()
{
    sb.setLength(0);
    sb.append("#BPS");   
    if (nargs > 0xff)
    {   sb.append(Integer.toHexString(nargs >> 8));
        sb.append(":");
    }
    sb.append(Integer.toString(nargs & 0xff));
    sb.append("<");
    if (bytecodes == null) sb.append("null");
    else for (int i=0; i<bytecodes.length; i++)
    {   String s = Integer.toHexString(bytecodes[i] & 0xff);
        if (s.length() == 1) s = "0" + s;
        sb.append(s);
    }
    sb.append(">");
    return sb.toString();
}

public void iprint() throws ResourceException
{
    String s = printAs();
    if ((currentFlags & noLineBreak) == 0 &&
        currentOutput.column + s.length() > currentOutput.lineLength)
        currentOutput.println();
    currentOutput.print(s);
}

public void blankprint() throws ResourceException
{
    String s = printAs();
    if ((currentFlags & noLineBreak) == 0 &&
        currentOutput.column + s.length() >= currentOutput.lineLength)
        currentOutput.println();
    else currentOutput.print(" ");
    currentOutput.print(s);
}

public LispObject op0() throws Exception
{
    if (nargs != 0) Jlisp.error("Wrong number of arguments for " + name);
    return interpret(0);
}

public LispObject op1(LispObject a1) throws Exception
{
    if (nargs != 1) Jlisp.error("Wrong number of arguments for " + name);
    int spsave = sp;
    LispObject r = Environment.nil;
    stack[++sp] = a1;
    try
    {   r = interpret(0);
        sp--;
    }
    catch (Exception e)
    {   sp = spsave;
        throw e;
    }
    return r;
}

public LispObject op2(LispObject a1, LispObject a2) throws Exception
{
    if (nargs != 2) Jlisp.error("Wrong number of arguments for " + name);
    int spsave = sp;
    LispObject r = Environment.nil;
    stack[++sp] = a1;
    stack[++sp] = a2;
    try
    {   r = interpret(0);
        sp -= 2;
    }
    catch (Exception e)
    {   sp = spsave;
        throw e;
    }
    return r;
}

public LispObject opn(LispObject [] args) throws Exception
{
    int n = args.length;
    if (nargs != n) Jlisp.error("Wrong number of arguments for " + name);
    int spsave = sp;
    for (int i=0; i<args.length; i++)
        stack[++sp] = args[i];
    if (n > 3)
    {   if (n != (bytecodes[0] & 0xff)) 
        {   sp = spsave;
            error("Wrong number of args");
        }
        n = 1;
    }
    else n = 0;
    LispObject r = Environment.nil;
    try
    {   r = interpret(n);
        sp = spsave;
    }
    catch (Exception e)
    {   sp = spsave;
        throw e;
    }
    return r;
}


static LispFunction builtin0[], builtin1[], builtin2[], builtin3[];

static LispFunction lookupBuiltin(String s) throws ResourceException
{
    LispFunction r = (LispFunction)Jlisp.builtinFunctions.get(s);
    if (r == null) Jlisp.println("Function " + s + " not found");
    return r;
}

static int BIbatchp, BIdate, BIeject, BIerror1, BIgctime,
    BIgensym, BIlposn, BInext_random, BIposn, BIread,
    BIreadch, BIterpri, BItime, BItyi, BIload_spid,
    BIabs, BIadd1, BIatan, BIapply0, BIatom,
    BIboundp, BIchar_code, BIclose, BIcodep, BIcompress,
    BIconstantp, BIdigit, BIendp, BIeval, BIevenp,
    BIevlis, BIexplode, BIexplode2lc, BIexplode2, BIexplodec,
    BIfixp, BIfloat, BIfloatp, BIsymbol_specialp, BIgc,
    BIgensym1, BIgetenv, BIsymbol_globalp, BIiadd1, BIsymbolp,
    BIiminus, BIiminusp, BIindirect, BIintegerp, BIintern,
    BIisub1, BIlength, BIlengthc, BIlinelength, BIliter,
    BIload_module, BIlognot, BImacroexpand, BImacroexpand_1, BImacro_function,
    BImake_bps, BImake_global, BImake_simple_string, BImake_special, BIminus,
    BIminusp, BImkvect, BImodular_minus, BImodular_number, BImodular_reciprocal,
    BInull, BIoddp, BIonep, BIpagelength, BIpairp,
    BIplist, BIplusp, BIprin, BIprinc, BIprint,
    BIprintc, BIrandom, BIrational, BIload, BIrds,
    BIremd, BIreverse, BIreversip, BIreversip2, BIseprp, BIset_small_modulus,
    BIspaces, BIxtab, BIspecial_char, BIspecial_form_p, BIspool,
    BIstop, BIstringp, BIsub1, BIsymbol_env, BIsymbol_function,
    BIsymbol_name, BIsymbol_value, BIsystem, BIfix, BIttab,
    BItyo, BIremob, BIunmake_global, BIunmake_special, BIupbv,
    BIvectorp, BIsimple_vectorp, BIverbos, BIwrs, BIzerop,
    BIcar, BIcdr, BIcaar, BIcadr, BIcdar,
    BIcddr, BIqcar, BIqcdr, BIqcaar, BIqcadr,
    BIqcdar, BIqcddr, BIncons, BInumberp, BIis_spid,
    BIspid_to_nil, BImv_listSTAR, BIappend, BIash, BIassoc,
    BIatsoc, BIdeleq, BIdelete, BIdivide, BIeqcar,
    BIeql, BIeqn, BIexpt, BIflag, BIflagpcar,
    BIgcdn, BIgeq, BIgetv, BIgreaterp, BIidifference,
    BIigreaterp, BIilessp, BIimax, BIimin, BIiplus2,
    BIiquotient, BIiremainder, BIirightshift, BIitimes2, BIlcm,
    BIleq, BIlessp, BImake_random_state, BImax2, BImember,
    BImemq, BImin2, BImod, BImodular_difference, BImodular_expt,
    BImodular_plus, BImodular_quotient, BImodular_times, BInconc, BIneq,
    BIorderp, BIordp, BIquotient, BIremainder, BIremflag,
    BIremprop, BIrplaca, BIrplacd, BIschar, BIset,
    BIsmemq, BIsubla, BIsublis, BIsymbol_set_definition, BIsymbol_set_env,
    BIxcons, BIequal, BIeq, BIcons,
    BIlist2, BIget, BIqgetv, BIflagp, BIapply1,
    BIdifference, BIplus2, BItimes2, BIequalcar, BIiequal, BIbps_putv,
    BIerrorset, BIlist2STAR, BIlist3, BIputprop, BIputv,
    BIputv_char, BIsubst, BIapply2, BIacons;

public static void setupBuiltins() throws ResourceException
{
    builtin0 = new LispFunction[15];
    builtin1 = new LispFunction[114];
    builtin2 = new LispFunction[73];
    builtin3 = new LispFunction[10];

    builtin0[0]   = lookupBuiltin("batchp");               BIbatchp =                0;
    builtin0[1]   = lookupBuiltin("date");                 BIdate =                  1;
    builtin0[2]   = lookupBuiltin("eject");                BIeject =                 2;
    builtin0[3]   = lookupBuiltin("error1");               BIerror1 =                3;
    builtin0[4]   = lookupBuiltin("gctime");               BIgctime =                4;
    builtin0[5]   = lookupBuiltin("gensym");               BIgensym =                5;
    builtin0[6]   = lookupBuiltin("lposn");                BIlposn =                 6;
//  builtin0[7]   = lookupBuiltin("next-random");          BInext_random =           7;
    builtin0[8]   = lookupBuiltin("posn");                 BIposn =                  8;
    builtin0[9]   = lookupBuiltin("read");                 BIread =                  9;
    builtin0[10]  = lookupBuiltin("readch");               BIreadch =                10;
    builtin0[11]  = lookupBuiltin("terpri");               BIterpri =                11;
    builtin0[12]  = lookupBuiltin("time");                 BItime =                  12;
//  builtin0[13]  = lookupBuiltin("tyi");                  BItyi =                   13;
//  builtin0[14]  = lookupBuiltin("load-spid");            BIload_spid =             14;

    builtin1[0]   = lookupBuiltin("abs");                  BIabs =                   0;
    builtin1[1]   = lookupBuiltin("add1");                 BIadd1 =                  1;
    builtin1[2]   = lookupBuiltin("atan");                 BIatan =                  2;
    builtin1[3]   = lookupBuiltin("apply0");               BIapply0 =                3;
    builtin1[4]   = lookupBuiltin("atom");                 BIatom =                  4;
    builtin1[5]   = lookupBuiltin("boundp");               BIboundp =                5;
    builtin1[6]   = lookupBuiltin("char-code");            BIchar_code =             6;
    builtin1[7]   = lookupBuiltin("close");                BIclose =                 7;
    builtin1[8]   = lookupBuiltin("codep");                BIcodep =                 8;
    builtin1[9]   = lookupBuiltin("compress");             BIcompress =              9;
    builtin1[10]  = lookupBuiltin("constantp");            BIconstantp =             10;
    builtin1[11]  = lookupBuiltin("digit");                BIdigit =                 11;
    builtin1[12]  = lookupBuiltin("endp");                 BIendp =                  12;
    builtin1[13]  = lookupBuiltin("eval");                 BIeval =                  13;
    builtin1[14]  = lookupBuiltin("evenp");                BIevenp =                 14;
    builtin1[15]  = lookupBuiltin("evlis");                BIevlis =                 15;
    builtin1[16]  = lookupBuiltin("explode");              BIexplode =               16;
    builtin1[17]  = lookupBuiltin("explode2lc");           BIexplode2lc =            17;
    builtin1[18]  = lookupBuiltin("explode2");             BIexplode2 =              18;
    builtin1[18]  = lookupBuiltin("explodec");             BIexplodec =              18;
    builtin1[19]  = lookupBuiltin("fixp");                 BIfixp =                  19;
    builtin1[20]  = lookupBuiltin("float");                BIfloat =                 20;
    builtin1[21]  = lookupBuiltin("floatp");               BIfloatp =                21;
//  builtin1[22]  = lookupBuiltin("symbol-specialp");      BIsymbol_specialp =       22;
//  builtin1[23]  = lookupBuiltin("gc");                   BIgc =                    23;
    builtin1[24]  = lookupBuiltin("gensym1");              BIgensym1 =               24;
    builtin1[25]  = lookupBuiltin("getenv");               BIgetenv =                25;
//  builtin1[26]  = lookupBuiltin("symbol-globalp");       BIsymbol_globalp =        26;
    builtin1[27]  = lookupBuiltin("iadd1");                BIiadd1 =                 27;
    builtin1[28]  = lookupBuiltin("symbolp");              BIsymbolp =               28;
    builtin1[29]  = lookupBuiltin("iminus");               BIiminus =                29;
    builtin1[30]  = lookupBuiltin("iminusp");              BIiminusp =               30;
    builtin1[31]  = lookupBuiltin("indirect");             BIindirect =              31;
    builtin1[32]  = lookupBuiltin("integerp");             BIintegerp =              32;
    builtin1[33]  = lookupBuiltin("intern");               BIintern =                33;
    builtin1[34]  = lookupBuiltin("isub1");                BIisub1 =                 34;
    builtin1[35]  = lookupBuiltin("length");               BIlength =                35;
    builtin1[36]  = lookupBuiltin("lengthc");              BIlengthc =               36;
    builtin1[37]  = lookupBuiltin("linelength");           BIlinelength =            37;
    builtin1[38]  = lookupBuiltin("liter");                BIliter =                 38;
    builtin1[39]  = lookupBuiltin("load-module");          BIload_module =           39;
    builtin1[40]  = lookupBuiltin("lognot");               BIlognot =                40;
    builtin1[41]  = lookupBuiltin("macroexpand");          BImacroexpand =           41;
    builtin1[42]  = lookupBuiltin("macroexpand-1");        BImacroexpand_1 =         42;
    builtin1[43]  = lookupBuiltin("macro-function");       BImacro_function =        43;
    builtin1[44]  = lookupBuiltin("make-bps");             BImake_bps =              44;
    builtin1[45]  = lookupBuiltin("make-global");          BImake_global =           45;
    builtin1[46]  = lookupBuiltin("make-simple-string");   BImake_simple_string =    46;
    builtin1[47]  = lookupBuiltin("make-special");         BImake_special =          47;
    builtin1[48]  = lookupBuiltin("minus");                BIminus =                 48;
    builtin1[49]  = lookupBuiltin("minusp");               BIminusp =                49;
    builtin1[50]  = lookupBuiltin("mkvect");               BImkvect =                50;
    builtin1[51]  = lookupBuiltin("modular-minus");        BImodular_minus =         51;
    builtin1[52]  = lookupBuiltin("modular-number");       BImodular_number =        52;
    builtin1[53]  = lookupBuiltin("modular-reciprocal");   BImodular_reciprocal =    53;
    builtin1[54]  = lookupBuiltin("null");                 BInull =                  54;
    builtin1[55]  = lookupBuiltin("oddp");                 BIoddp =                  55;
    builtin1[56]  = lookupBuiltin("onep");                 BIonep =                  56;
    builtin1[57]  = lookupBuiltin("pagelength");           BIpagelength =            57;
    builtin1[58]  = lookupBuiltin("pairp");                BIpairp =                 58;
    builtin1[59]  = lookupBuiltin("plist");                BIplist =                 59;
    builtin1[60]  = lookupBuiltin("plusp");                BIplusp =                 60;
    builtin1[61]  = lookupBuiltin("prin");                 BIprin =                  61;
    builtin1[62]  = lookupBuiltin("princ");                BIprinc =                 62;
    builtin1[63]  = lookupBuiltin("print");                BIprint =                 63;
    builtin1[64]  = lookupBuiltin("printc");               BIprintc =                64;
//  builtin1[65]  = lookupBuiltin("random");               BIrandom =                65;
    builtin1[66]  = lookupBuiltin("rational");             BIrational =              66;
//  builtin1[67]  = lookupBuiltin("load");                 BIload =                  67;
    builtin1[68]  = lookupBuiltin("rds");                  BIrds =                   68;
    builtin1[69]  = lookupBuiltin("remd");                 BIremd =                  69;
    builtin1[70]  = lookupBuiltin("reverse");              BIreverse =               70;
    builtin1[71]  = lookupBuiltin("reversip");             BIreversip =              71;
    builtin1[72]  = lookupBuiltin("seprp");                BIseprp =                 72;
    builtin1[73]  = lookupBuiltin("set-small-modulus");    BIset_small_modulus =     73;
    builtin1[74]  = lookupBuiltin("spaces");               BIspaces =                74;
    builtin1[74]  = lookupBuiltin("xtab");                 BIxtab =                  74;
    builtin1[75]  = lookupBuiltin("special-char");         BIspecial_char =          75;
    builtin1[76]  = lookupBuiltin("special-form-p");       BIspecial_form_p =        76;
    builtin1[77]  = lookupBuiltin("spool");                BIspool =                 77;
    builtin1[78]  = lookupBuiltin("stop");                 BIstop =                  78;
    builtin1[79]  = lookupBuiltin("stringp");              BIstringp =               79;
    builtin1[80]  = lookupBuiltin("sub1");                 BIsub1 =                  80;
    builtin1[81]  = lookupBuiltin("symbol-env");           BIsymbol_env =            81;
    builtin1[82]  = lookupBuiltin("symbol-function");      BIsymbol_function =       82;
    builtin1[83]  = lookupBuiltin("symbol-name");          BIsymbol_name =           83;
    builtin1[84]  = lookupBuiltin("symbol-value");         BIsymbol_value =          84;
    builtin1[85]  = lookupBuiltin("system");               BIsystem =                85;
    builtin1[86]  = lookupBuiltin("fix");                  BIfix =                   86;
    builtin1[87]  = lookupBuiltin("ttab");                 BIttab =                  87;
    builtin1[88]  = lookupBuiltin("tyo");                  BItyo =                   88;
    builtin1[89]  = lookupBuiltin("remob");                BIremob =                 89;
    builtin1[90]  = lookupBuiltin("unmake-global");        BIunmake_global =         90;
    builtin1[91]  = lookupBuiltin("unmake-special");       BIunmake_special =        91;
    builtin1[92]  = lookupBuiltin("upbv");                 BIupbv =                  92;
    builtin1[93]  = lookupBuiltin("vectorp");              BIvectorp =               93;
//  builtin1[93]  = lookupBuiltin("simple-vectorp");       BIsimple_vectorp =        93;
    builtin1[94]  = lookupBuiltin("verbos");               BIverbos =                94;
    builtin1[95]  = lookupBuiltin("wrs");                  BIwrs =                   95;
    builtin1[96]  = lookupBuiltin("zerop");                BIzerop =                 96;
    builtin1[97]  = lookupBuiltin("car");                  BIcar =                   97;
    builtin1[98]  = lookupBuiltin("cdr");                  BIcdr =                   98;
    builtin1[99]  = lookupBuiltin("caar");                 BIcaar =                  99;
    builtin1[100] = lookupBuiltin("cadr");                 BIcadr =                  100;
    builtin1[101] = lookupBuiltin("cdar");                 BIcdar =                  101;
    builtin1[102] = lookupBuiltin("cddr");                 BIcddr =                  102;
    builtin1[103] = lookupBuiltin("qcar");                 BIqcar =                  103;
    builtin1[104] = lookupBuiltin("qcdr");                 BIqcdr =                  104;
    builtin1[105] = lookupBuiltin("qcaar");                BIqcaar =                 105;
    builtin1[106] = lookupBuiltin("qcadr");                BIqcadr =                 106;
    builtin1[107] = lookupBuiltin("qcdar");                BIqcdar =                 107;
    builtin1[108] = lookupBuiltin("qcddr");                BIqcddr =                 108;
    builtin1[109] = lookupBuiltin("ncons");                BIncons =                 109;
    builtin1[110] = lookupBuiltin("numberp");              BInumberp =               110;
//  builtin1[111] = lookupBuiltin("is-spid");              BIis_spid =               111;
//  builtin1[112] = lookupBuiltin("spid-to-nil");          BIspid_to_nil =           112;
//  builtin1[113] = lookupBuiltin("mv-list*");             BImv_listSTAR =           113;

    builtin2[0]   = lookupBuiltin("append");               BIappend =                0;
    builtin2[1]   = lookupBuiltin("ash");                  BIash =                   1;
    builtin2[2]   = lookupBuiltin("assoc");                BIassoc =                 2;
    builtin2[3]   = lookupBuiltin("atsoc");                BIatsoc =                 3;
    builtin2[4]   = lookupBuiltin("deleq");                BIdeleq =                 4;
    builtin2[5]   = lookupBuiltin("delete");               BIdelete =                5;
    builtin2[6]   = lookupBuiltin("divide");               BIdivide =                6;
    builtin2[7]   = lookupBuiltin("eqcar");                BIeqcar =                 7;
    builtin2[8]   = lookupBuiltin("eql");                  BIeql =                   8;
    builtin2[9]   = lookupBuiltin("eqn");                  BIeqn =                   9;
    builtin2[10]  = lookupBuiltin("expt");                 BIexpt =                  10;
    builtin2[11]  = lookupBuiltin("flag");                 BIflag =                  11;
    builtin2[12]  = lookupBuiltin("flagpcar");             BIflagpcar =              12;
    builtin2[13]  = lookupBuiltin("gcdn");                 BIgcdn =                  13;
    builtin2[14]  = lookupBuiltin("geq");                  BIgeq =                   14;
    builtin2[15]  = lookupBuiltin("getv");                 BIgetv =                  15;
    builtin2[16]  = lookupBuiltin("greaterp");             BIgreaterp =              16;
    builtin2[17]  = lookupBuiltin("idifference");          BIidifference =           17;
    builtin2[18]  = lookupBuiltin("igreaterp");            BIigreaterp =             18;
    builtin2[19]  = lookupBuiltin("ilessp");               BIilessp =                19;
    builtin2[20]  = lookupBuiltin("imax");                 BIimax =                  20;
    builtin2[21]  = lookupBuiltin("imin");                 BIimin =                  21;
    builtin2[22]  = lookupBuiltin("iplus2");               BIiplus2 =                22;
    builtin2[23]  = lookupBuiltin("iquotient");            BIiquotient =             23;
    builtin2[24]  = lookupBuiltin("iremainder");           BIiremainder =            24;
    builtin2[25]  = lookupBuiltin("irightshift");          BIirightshift =           25;
    builtin2[26]  = lookupBuiltin("itimes2");              BIitimes2 =               26;
//  builtin2[27]  = lookupBuiltin("lcm");                  BIlcm =                   27;
    builtin2[28]  = lookupBuiltin("leq");                  BIleq =                   28;
    builtin2[29]  = lookupBuiltin("lessp");                BIlessp =                 29;
    builtin2[30]  = lookupBuiltin("make-random-state");    BImake_random_state =     30;
    builtin2[31]  = lookupBuiltin("max2");                 BImax2 =                  31;
    builtin2[32]  = lookupBuiltin("member");               BImember =                32;
    builtin2[33]  = lookupBuiltin("memq");                 BImemq =                  33;
    builtin2[34]  = lookupBuiltin("min2");                 BImin2 =                  34;
    builtin2[35]  = lookupBuiltin("mod");                  BImod =                   35;
    builtin2[36]  = lookupBuiltin("modular-difference");   BImodular_difference =    36;
    builtin2[37]  = lookupBuiltin("modular-expt");         BImodular_expt =          37;
    builtin2[38]  = lookupBuiltin("modular-plus");         BImodular_plus =          38;
    builtin2[39]  = lookupBuiltin("modular-quotient");     BImodular_quotient =      39;
    builtin2[40]  = lookupBuiltin("modular-times");        BImodular_times =         40;
    builtin2[41]  = lookupBuiltin("nconc");                BInconc =                 41;
    builtin2[42]  = lookupBuiltin("neq");                  BIneq =                   42;
    builtin2[43]  = lookupBuiltin("orderp");               BIorderp =                43;
    builtin2[43]  = lookupBuiltin("ordp");                 BIordp =                  43;
    builtin2[44]  = lookupBuiltin("quotient");             BIquotient =              44;
    builtin2[45]  = lookupBuiltin("remainder");            BIremainder =             45;
    builtin2[46]  = lookupBuiltin("remflag");              BIremflag =               46;
    builtin2[47]  = lookupBuiltin("remprop");              BIremprop =               47;
    builtin2[48]  = lookupBuiltin("rplaca");               BIrplaca =                48;
    builtin2[49]  = lookupBuiltin("rplacd");               BIrplacd =                49;
    builtin2[50]  = lookupBuiltin("schar");                BIschar =                 50;
    builtin2[51]  = lookupBuiltin("set");                  BIset =                   51;
    builtin2[52]  = lookupBuiltin("smemq");                BIsmemq =                 52;
    builtin2[53]  = lookupBuiltin("subla");                BIsubla =                 53;
    builtin2[54]  = lookupBuiltin("sublis");               BIsublis =                54;
    builtin2[55]  = lookupBuiltin("symbol-set-definition");BIsymbol_set_definition = 55;
    builtin2[56]  = lookupBuiltin("symbol-set-env");       BIsymbol_set_env =        56;
////builtin2[57]  = lookupBuiltin("times2");               BItimes2 =                57;
    builtin2[58]  = lookupBuiltin("xcons");                BIxcons =                 58;
    builtin2[59]  = lookupBuiltin("equal");                BIequal =                 59;
    builtin2[60]  = lookupBuiltin("eq");                   BIeq =                    60;
    builtin2[61]  = lookupBuiltin("cons");                 BIcons =                  61;
    builtin2[62]  = lookupBuiltin("list2");                BIlist2 =                 62;
    builtin2[63]  = lookupBuiltin("get");                  BIget =                   63;
    builtin2[64]  = lookupBuiltin("qgetv");                BIqgetv =                 64;
    builtin2[65]  = lookupBuiltin("flagp");                BIflagp =                 65;
    builtin2[66]  = lookupBuiltin("apply1");               BIapply1 =                66;
    builtin2[67]  = lookupBuiltin("difference");           BIdifference =            67;
    builtin2[68]  = lookupBuiltin("plus2");                BIplus2 =                 68;
    builtin2[69]  = lookupBuiltin("times2");               BItimes2 =                69;
    builtin2[70]  = lookupBuiltin("equalcar");             BIequalcar =              70;
    builtin2[71]  = lookupBuiltin("iequal");               BIiequal =                71;
    builtin2[72]  = lookupBuiltin("reversip");             BIreversip2 =             72;

    builtin3[0]   = lookupBuiltin("bps-putv");             BIbps_putv =              0;
    builtin3[1]   = lookupBuiltin("errorset");             BIerrorset =              1;
    builtin3[2]   = lookupBuiltin("list2*");               BIlist2STAR =             2;
    builtin3[3]   = lookupBuiltin("list3");                BIlist3 =                 3;
//  builtin3[4]   = lookupBuiltin("putprop");              BIputprop =               4;
    builtin3[5]   = lookupBuiltin("putv");                 BIputv =                  5;
    builtin3[6]   = lookupBuiltin("putv-char");            BIputv_char =             6;
    builtin3[7]   = lookupBuiltin("subst");                BIsubst =                 7;
    builtin3[8]   = lookupBuiltin("apply2");               BIapply2 =                8;
    builtin3[9]   = lookupBuiltin("acons");                BIacons =                 9;
}



static final int LOADLOC       =     0x00;
static final int LOADLOC0      =     0x01;
static final int LOADLOC1      =     0x02;
static final int LOADLOC2      =     0x03;
static final int LOADLOC3      =     0x04;
static final int LOADLOC4      =     0x05;
static final int LOADLOC5      =     0x06;
static final int LOADLOC6      =     0x07;
static final int LOADLOC7      =     0x08;
static final int LOADLOC8      =     0x09;
static final int LOADLOC9      =     0x0a;
static final int LOADLOC10     =     0x0b;
static final int LOADLOC11     =     0x0c;
static final int LOC0LOC1      =     0x0d;
static final int LOC1LOC2      =     0x0e;
static final int LOC2LOC3      =     0x0f;
static final int LOC1LOC0      =     0x10;
static final int LOC2LOC1      =     0x11;
static final int LOC3LOC2      =     0x12;
static final int VNIL          =     0x13;
static final int LOADLIT       =     0x14;
static final int LOADLIT1      =     0x15;
static final int LOADLIT2      =     0x16;
static final int LOADLIT3      =     0x17;
static final int LOADLIT4      =     0x18;
static final int LOADLIT5      =     0x19;
static final int LOADLIT6      =     0x1a;
static final int LOADLIT7      =     0x1b;
static final int LOADFREE      =     0x1c;
static final int LOADFREE1     =     0x1d;
static final int LOADFREE2     =     0x1e;
static final int LOADFREE3     =     0x1f;
static final int LOADFREE4     =     0x20;
static final int STORELOC      =     0x21;
static final int STORELOC0     =     0x22;
static final int STORELOC1     =     0x23;
static final int STORELOC2     =     0x24;
static final int STORELOC3     =     0x25;
static final int STORELOC4     =     0x26;
static final int STORELOC5     =     0x27;
static final int STORELOC6     =     0x28;
static final int STORELOC7     =     0x29;
static final int STOREFREE     =     0x2a;
static final int STOREFREE1    =     0x2b;
static final int STOREFREE2    =     0x2c;
static final int STOREFREE3    =     0x2d;
static final int LOADLEX       =     0x2e;
static final int STORELEX      =     0x2f;
static final int CLOSURE       =     0x30;
static final int CARLOC0       =     0x31;
static final int CARLOC1       =     0x32;
static final int CARLOC2       =     0x33;
static final int CARLOC3       =     0x34;
static final int CARLOC4       =     0x35;
static final int CARLOC5       =     0x36;
static final int CARLOC6       =     0x37;
static final int CARLOC7       =     0x38;
static final int CARLOC8       =     0x39;
static final int CARLOC9       =     0x3a;
static final int CARLOC10      =     0x3b;
static final int CARLOC11      =     0x3c;
static final int CDRLOC0       =     0x3d;
static final int CDRLOC1       =     0x3e;
static final int CDRLOC2       =     0x3f;
static final int CDRLOC3       =     0x40;
static final int CDRLOC4       =     0x41;
static final int CDRLOC5       =     0x42;
static final int CAARLOC0      =     0x43;
static final int CAARLOC1      =     0x44;
static final int CAARLOC2      =     0x45;
static final int CAARLOC3      =     0x46;
static final int CALL0         =     0x47;
static final int CALL1         =     0x48;
static final int CALL2         =     0x49;
static final int CALL2R        =     0x4a;
static final int CALL3         =     0x4b;
static final int CALLN         =     0x4c;
static final int CALL0_0       =     0x4d;
static final int CALL0_1       =     0x4e;
static final int CALL0_2       =     0x4f;
static final int CALL0_3       =     0x50;
static final int CALL1_0       =     0x51;
static final int CALL1_1       =     0x52;
static final int CALL1_2       =     0x53;
static final int CALL1_3       =     0x54;
static final int CALL1_4       =     0x55;
static final int CALL1_5       =     0x56;
static final int CALL2_0       =     0x57;
static final int CALL2_1       =     0x58;
static final int CALL2_2       =     0x59;
static final int CALL2_3       =     0x5a;
static final int CALL2_4       =     0x5b;
static final int BUILTIN0      =     0x5c;
static final int BUILTIN1      =     0x5d;
static final int BUILTIN2      =     0x5e;
static final int BUILTIN2R     =     0x5f;
static final int BUILTIN3      =     0x60;
static final int APPLY1        =     0x61;
static final int APPLY2        =     0x62;
static final int APPLY3        =     0x63;
static final int APPLY4        =     0x64;
static final int JCALL         =     0x65;
static final int JCALLN        =     0x66;
static final int JUMP          =     0x67;
static final int JUMP_B        =     0x68;
static final int JUMP_L        =     0x69;
static final int JUMP_BL       =     0x6a;
static final int JUMPNIL       =     0x6b;
static final int JUMPNIL_B     =     0x6c;
static final int JUMPNIL_L     =     0x6d;
static final int JUMPNIL_BL    =     0x6e;
static final int JUMPT         =     0x6f;
static final int JUMPT_B       =     0x70;
static final int JUMPT_L       =     0x71;
static final int JUMPT_BL      =     0x72;
static final int JUMPATOM      =     0x73;
static final int JUMPATOM_B    =     0x74;
static final int JUMPATOM_L    =     0x75;
static final int JUMPATOM_BL   =     0x76;
static final int JUMPNATOM     =     0x77;
static final int JUMPNATOM_B   =     0x78;
static final int JUMPNATOM_L   =     0x79;
static final int JUMPNATOM_BL  =     0x7a;
static final int JUMPEQ        =     0x7b;
static final int JUMPEQ_B      =     0x7c;
static final int JUMPEQ_L      =     0x7d;
static final int JUMPEQ_BL     =     0x7e;
static final int JUMPNE        =     0x7f;
// I will put these things into byte arrays so I want to have values
// in the range -128 to +127.
static final int JUMPNE_B      =     0x80 - 0x100;
static final int JUMPNE_L      =     0x81 - 0x100;
static final int JUMPNE_BL     =     0x82 - 0x100;
static final int JUMPEQUAL     =     0x83 - 0x100;
static final int JUMPEQUAL_B   =     0x84 - 0x100;
static final int JUMPEQUAL_L   =     0x85 - 0x100;
static final int JUMPEQUAL_BL  =     0x86 - 0x100;
static final int JUMPNEQUAL    =     0x87 - 0x100;
static final int JUMPNEQUAL_B  =     0x88 - 0x100;
static final int JUMPNEQUAL_L  =     0x89 - 0x100;
static final int JUMPNEQUAL_BL =     0x8a - 0x100;
static final int JUMPL0NIL     =     0x8b - 0x100;
static final int JUMPL0T       =     0x8c - 0x100;
static final int JUMPL1NIL     =     0x8d - 0x100;
static final int JUMPL1T       =     0x8e - 0x100;
static final int JUMPL2NIL     =     0x8f - 0x100;
static final int JUMPL2T       =     0x90 - 0x100;
static final int JUMPL3NIL     =     0x91 - 0x100;
static final int JUMPL3T       =     0x92 - 0x100;
static final int JUMPL4NIL     =     0x93 - 0x100;
static final int JUMPL4T       =     0x94 - 0x100;
static final int JUMPST0NIL    =     0x95 - 0x100;
static final int JUMPST0T      =     0x96 - 0x100;
static final int JUMPST1NIL    =     0x97 - 0x100;
static final int JUMPST1T      =     0x98 - 0x100;
static final int JUMPST2NIL    =     0x99 - 0x100;
static final int JUMPST2T      =     0x9a - 0x100;
static final int JUMPL0ATOM    =     0x9b - 0x100;
static final int JUMPL0NATOM   =     0x9c - 0x100;
static final int JUMPL1ATOM    =     0x9d - 0x100;
static final int JUMPL1NATOM   =     0x9e - 0x100;
static final int JUMPL2ATOM    =     0x9f - 0x100;
static final int JUMPL2NATOM   =     0xa0 - 0x100;
static final int JUMPL3ATOM    =     0xa1 - 0x100;
static final int JUMPL3NATOM   =     0xa2 - 0x100;
static final int JUMPFREE1NIL  =     0xa3 - 0x100;
static final int JUMPFREE1T    =     0xa4 - 0x100;
static final int JUMPFREE2NIL  =     0xa5 - 0x100;
static final int JUMPFREE2T    =     0xa6 - 0x100;
static final int JUMPFREE3NIL  =     0xa7 - 0x100;
static final int JUMPFREE3T    =     0xa8 - 0x100;
static final int JUMPFREE4NIL  =     0xa9 - 0x100;
static final int JUMPFREE4T    =     0xaa - 0x100;
static final int JUMPFREENIL   =     0xab - 0x100;
static final int JUMPFREET     =     0xac - 0x100;
static final int JUMPLIT1EQ    =     0xad - 0x100;
static final int JUMPLIT1NE    =     0xae - 0x100;
static final int JUMPLIT2EQ    =     0xaf - 0x100;
static final int JUMPLIT2NE    =     0xb0 - 0x100;
static final int JUMPLIT3EQ    =     0xb1 - 0x100;
static final int JUMPLIT3NE    =     0xb2 - 0x100;
static final int JUMPLIT4EQ    =     0xb3 - 0x100;
static final int JUMPLIT4NE    =     0xb4 - 0x100;
static final int JUMPLITEQ     =     0xb5 - 0x100;
static final int JUMPLITNE     =     0xb6 - 0x100;
static final int JUMPB1NIL     =     0xb7 - 0x100;
static final int JUMPB1T       =     0xb8 - 0x100;
static final int JUMPB2NIL     =     0xb9 - 0x100;
static final int JUMPB2T       =     0xba - 0x100;
static final int JUMPFLAGP     =     0xbb - 0x100;
static final int JUMPNFLAGP    =     0xbc - 0x100;
static final int JUMPEQCAR     =     0xbd - 0x100;
static final int JUMPNEQCAR    =     0xbe - 0x100;
static final int CATCH         =     0xbf - 0x100;
static final int CATCH_B       =     0xc0 - 0x100;
static final int CATCH_L       =     0xc1 - 0x100;
static final int CATCH_BL      =     0xc2 - 0x100;
static final int UNCATCH       =     0xc3 - 0x100;
static final int THROW         =     0xc4 - 0x100;
static final int PROTECT       =     0xc5 - 0x100;
static final int UNPROTECT     =     0xc6 - 0x100;
static final int PVBIND        =     0xc7 - 0x100;
static final int PVRESTORE     =     0xc8 - 0x100;
static final int FREEBIND      =     0xc9 - 0x100;
static final int FREERSTR      =     0xca - 0x100;
static final int EXIT          =     0xcb - 0x100;
static final int NILEXIT       =     0xcc - 0x100;
static final int LOC0EXIT      =     0xcd - 0x100;
static final int LOC1EXIT      =     0xce - 0x100;
static final int LOC2EXIT      =     0xcf - 0x100;
static final int PUSH          =     0xd0 - 0x100;
static final int PUSHNIL       =     0xd1 - 0x100;
static final int PUSHNIL2      =     0xd2 - 0x100;
static final int PUSHNIL3      =     0xd3 - 0x100;
static final int PUSHNILS      =     0xd4 - 0x100;
static final int POP           =     0xd5 - 0x100;
static final int LOSE          =     0xd6 - 0x100;
static final int LOSE2         =     0xd7 - 0x100;
static final int LOSE3         =     0xd8 - 0x100;
static final int LOSES         =     0xd9 - 0x100;
static final int SWOP          =     0xda - 0x100;
static final int EQ            =     0xdb - 0x100;
static final int EQCAR         =     0xdc - 0x100;
static final int EQUAL         =     0xdd - 0x100;
static final int NUMBERP       =     0xde - 0x100;
static final int CAR           =     0xdf - 0x100;
static final int CDR           =     0xe0 - 0x100;
static final int CAAR          =     0xe1 - 0x100;
static final int CADR          =     0xe2 - 0x100;
static final int CDAR          =     0xe3 - 0x100;
static final int CDDR          =     0xe4 - 0x100;
static final int CONS          =     0xe5 - 0x100;
static final int NCONS         =     0xe6 - 0x100;
static final int XCONS         =     0xe7 - 0x100;
static final int ACONS         =     0xe8 - 0x100;
static final int LENGTH        =     0xe9 - 0x100;
static final int LIST2         =     0xea - 0x100;
static final int LIST2STAR     =     0xeb - 0x100;
static final int LIST3         =     0xec - 0x100;
static final int PLUS2         =     0xed - 0x100;
static final int ADD1          =     0xee - 0x100;
static final int DIFFERENCE    =     0xef - 0x100;
static final int SUB1          =     0xf0 - 0x100;
static final int TIMES2        =     0xf1 - 0x100;
static final int GREATERP      =     0xf2 - 0x100;
static final int LESSP         =     0xf3 - 0x100;
static final int FLAGP         =     0xf4 - 0x100;
static final int GET           =     0xf5 - 0x100;
static final int LITGET        =     0xf6 - 0x100;
static final int GETV          =     0xf7 - 0x100;
static final int QGETV         =     0xf8 - 0x100;
static final int QGETVN        =     0xf9 - 0x100;
static final int BIGSTACK      =     0xfa - 0x100;
static final int BIGCALL       =     0xfb - 0x100;
static final int ICASE         =     0xfc - 0x100;
static final int FASTGET       =     0xfd - 0x100;
static final int SPARE1        =     0xfe - 0x100;
static final int SPARE2        =     0xff - 0x100;

// The table of names is just for debugging - but that is of course
// very important!
//- 
//- static final String [] opnames =
//- {
//-     "LOADLOC      ", "LOADLOC0     ", "LOADLOC1     ", "LOADLOC2     ",
//-     "LOADLOC3     ", "LOADLOC4     ", "LOADLOC5     ", "LOADLOC6     ",
//-     "LOADLOC7     ", "LOADLOC8     ", "LOADLOC9     ", "LOADLOC10    ",
//-     "LOADLOC11    ", "LOC0LOC1     ", "LOC1LOC2     ", "LOC2LOC3     ",
//-     "LOC1LOC0     ", "LOC2LOC1     ", "LOC3LOC2     ", "VNIL         ",
//-     "LOADLIT      ", "LOADLIT1     ", "LOADLIT2     ", "LOADLIT3     ",
//-     "LOADLIT4     ", "LOADLIT5     ", "LOADLIT6     ", "LOADLIT7     ",
//-     "LOADFREE     ", "LOADFREE1    ", "LOADFREE2    ", "LOADFREE3    ",
//-     "LOADFREE4    ", "STORELOC     ", "STORELOC0    ", "STORELOC1    ",
//-     "STORELOC2    ", "STORELOC3    ", "STORELOC4    ", "STORELOC5    ",
//-     "STORELOC6    ", "STORELOC7    ", "STOREFREE    ", "STOREFREE1   ",
//-     "STOREFREE2   ", "STOREFREE3   ", "LOADLEX      ", "STORELEX     ",
//-     "CLOSURE      ", "CARLOC0      ", "CARLOC1      ", "CARLOC2      ",
//-     "CARLOC3      ", "CARLOC4      ", "CARLOC5      ", "CARLOC6      ",
//-     "CARLOC7      ", "CARLOC8      ", "CARLOC9      ", "CARLOC10     ",
//-     "CARLOC11     ", "CDRLOC0      ", "CDRLOC1      ", "CDRLOC2      ",
//-     "CDRLOC3      ", "CDRLOC4      ", "CDRLOC5      ", "CAARLOC0     ",
//-     "CAARLOC1     ", "CAARLOC2     ", "CAARLOC3     ", "CALL0        ",
//-     "CALL1        ", "CALL2        ", "CALL2R       ", "CALL3        ",
//-     "CALLN        ", "CALL0_0      ", "CALL0_1      ", "CALL0_2      ",
//-     "CALL0_3      ", "CALL1_0      ", "CALL1_1      ", "CALL1_2      ",
//-     "CALL1_3      ", "CALL1_4      ", "CALL1_5      ", "CALL2_0      ",
//-     "CALL2_1      ", "CALL2_2      ", "CALL2_3      ", "CALL2_4      ",
//-     "BUILTIN0     ", "BUILTIN1     ", "BUILTIN2     ", "BUILTIN2R    ",
//-     "BUILTIN3     ", "APPLY1       ", "APPLY2       ", "APPLY3       ",
//-     "APPLY4       ", "JCALL        ", "JCALLN       ", "JUMP         ",
//-     "JUMP_B       ", "JUMP_L       ", "JUMP_BL      ", "JUMPNIL      ",
//-     "JUMPNIL_B    ", "JUMPNIL_L    ", "JUMPNIL_BL   ", "JUMPT        ",
//-     "JUMPT_B      ", "JUMPT_L      ", "JUMPT_BL     ", "JUMPATOM     ",
//-     "JUMPATOM_B   ", "JUMPATOM_L   ", "JUMPATOM_BL  ", "JUMPNATOM    ",
//-     "JUMPNATOM_B  ", "JUMPNATOM_L  ", "JUMPNATOM_BL ", "JUMPEQ       ",
//-     "JUMPEQ_B     ", "JUMPEQ_L     ", "JUMPEQ_BL    ", "JUMPNE       ",
//-     "JUMPNE_B     ", "JUMPNE_L     ", "JUMPNE_BL    ", "JUMPEQUAL    ",
//-     "JUMPEQUAL_B  ", "JUMPEQUAL_L  ", "JUMPEQUAL_BL ", "JUMPNEQUAL   ",
//-     "JUMPNEQUAL_B ", "JUMPNEQUAL_L ", "JUMPNEQUAL_BL", "JUMPL0NIL    ",
//-     "JUMPL0T      ", "JUMPL1NIL    ", "JUMPL1T      ", "JUMPL2NIL    ",
//-     "JUMPL2T      ", "JUMPL3NIL    ", "JUMPL3T      ", "JUMPL4NIL    ",
//-     "JUMPL4T      ", "JUMPST0NIL   ", "JUMPST0T     ", "JUMPST1NIL   ",
//-     "JUMPST1T     ", "JUMPST2NIL   ", "JUMPST2T     ", "JUMPL0ATOM   ",
//-     "JUMPL0NATOM  ", "JUMPL1ATOM   ", "JUMPL1NATOM  ", "JUMPL2ATOM   ",
//-     "JUMPL2NATOM  ", "JUMPL3ATOM   ", "JUMPL3NATOM  ", "JUMPFREE1NIL ",
//-     "JUMPFREE1T   ", "JUMPFREE2NIL ", "JUMPFREE2T   ", "JUMPFREE3NIL ",
//-     "JUMPFREE3T   ", "JUMPFREE4NIL ", "JUMPFREE4T   ", "JUMPFREENIL  ",
//-     "JUMPFREET    ", "JUMPLIT1EQ   ", "JUMPLIT1NE   ", "JUMPLIT2EQ   ",
//-     "JUMPLIT2NE   ", "JUMPLIT3EQ   ", "JUMPLIT3NE   ", "JUMPLIT4EQ   ",
//-     "JUMPLIT4NE   ", "JUMPLITEQ    ", "JUMPLITNE    ", "JUMPB1NIL    ",
//-     "JUMPB1T      ", "JUMPB2NIL    ", "JUMPB2T      ", "JUMPFLAGP    ",
//-     "JUMPNFLAGP   ", "JUMPEQCAR    ", "JUMPNEQCAR   ", "CATCH        ",
//-     "CATCH_B      ", "CATCH_L      ", "CATCH_BL     ", "UNCATCH      ",
//-     "THROW        ", "PROTECT      ", "UNPROTECT    ", "PVBIND       ",
//-     "PVRESTORE    ", "FREEBIND     ", "FREERSTR     ", "EXIT         ",
//-     "NILEXIT      ", "LOC0EXIT     ", "LOC1EXIT     ", "LOC2EXIT     ",
//-     "PUSH         ", "PUSHNIL      ", "PUSHNIL2     ", "PUSHNIL3     ",
//-     "PUSHNILS     ", "POP          ", "LOSE         ", "LOSE2        ",
//-     "LOSE3        ", "LOSES        ", "SWOP         ", "EQ           ",
//-     "EQCAR        ", "EQUAL        ", "NUMBERP      ", "CAR          ",
//-     "CDR          ", "CAAR         ", "CADR         ", "CDAR         ",
//-     "CDDR         ", "CONS         ", "NCONS        ", "XCONS        ",
//-     "ACONS        ", "LENGTH       ", "LIST2        ", "LIST2STAR    ",
//-     "LIST3        ", "PLUS2        ", "ADD1         ", "DIFFERENCE   ",
//-     "SUB1         ", "TIMES2       ", "GREATERP     ", "LESSP        ",
//-     "FLAGP        ", "GET          ", "LITGET       ", "GETV         ",
//-     "QGETV        ", "QGETVN       ", "BIGSTACK     ", "BIGCALL      ",
//-     "ICASE        ", "FASTGET      ", "SPARE1       ", "SPARE2       "
//- };

static int stack_size = 5000;
static LispObject [] stack = new LispObject[stack_size];
static int sp = 0;
static int poll_time_countdown = 0;

static long last_clock = -1;

LispObject interpret(int pc) throws Exception
{
    if (--poll_time_countdown < 0) {
        poll_time_countdown = 10000;
        long t = System.currentTimeMillis();
        if (last_clock < 0) {
            last_clock = t;
        } else {
            while (t - last_clock > 1000) {
                last_clock += 1000;
                ResourceException.time_now++;
                if (ResourceException.time_limit > 0
                        && ResourceException.time_now > ResourceException.time_limit) {
                    if (Jlisp.headline) {
                        Jlisp.errprint("\n+++ Time limit exceeded\n");
                    }
                    throw new ResourceException("time limit exceeded");
                }
            }
        }
    }
    int spsave = sp;
    int arg;
    LispObject a = Environment.nil, b = Environment.nil, w;
    int iw, fname;

    if (sp > stack_size - 500) // the 500 is a pretty arbitrary margin!
                               // bad enough code could breach it.
    {   int new_size = (3*stack_size)/2;
        LispObject [] new_stack = new LispObject[new_size];
        for (int i=0; i<=sp; i++) new_stack[i] = stack[i];
        stack = new_stack;
        stack_size = new_size;
        if (Jlisp.verbosFlag != 0)
            Jlisp.errprint("+++ Stack enlarged to " + stack_size);
    }

    try { for (;;) 
    {
    switch (bytecodes[pc++])
    {
case LOADLOC:
        b = a; a = stack[sp-(bytecodes[pc++] & 0xff)];
        continue;
case LOADLOC0:
        b = a; a = stack[sp-0];
        continue;
case LOADLOC1:
        b = a; a = stack[sp-1];
        continue;
case LOADLOC2:
        b = a; a = stack[sp-2];
        continue;
case LOADLOC3:
        b = a; a = stack[sp-3];
        continue;
case LOADLOC4:
        b = a; a = stack[sp-4];
        continue;
case LOADLOC5:
        b = a; a = stack[sp-5];
        continue;
case LOADLOC6:
        b = a; a = stack[sp-6];
        continue;
case LOADLOC7:
        b = a; a = stack[sp-7];
        continue;
case LOADLOC8:
        b = a; a = stack[sp-8];
        continue;
case LOADLOC9:
        b = a; a = stack[sp-9];
        continue;
case LOADLOC10:
        b = a; a = stack[sp-10];
        continue;
case LOADLOC11:
        b = a; a = stack[sp-11];
        continue;
case LOC0LOC1:
        b = stack[sp-0];
        a = stack[sp-1];
        continue;
case LOC1LOC2:
        b = stack[sp-1];
        a = stack[sp-2];
        continue;
case LOC2LOC3:
        b = stack[sp-2];
        a = stack[sp-3];
        continue;
case LOC1LOC0:
        b = stack[sp-1];
        a = stack[sp-0];
        continue;
case LOC2LOC1:
        b = stack[sp-2];
        a = stack[sp-1];
        continue;
case LOC3LOC2:
        b = stack[sp-3];
        a = stack[sp-2];
        continue;
case VNIL:
        b = a; a = Environment.nil;
        continue;
case LOADLIT:
        b = a; a = env[bytecodes[pc++] & 0xff];
        continue;
case LOADLIT1:
        b = a; a = env[1];
        continue;
case LOADLIT2:
        b = a; a = env[2];
        continue;
case LOADLIT3:
        b = a; a = env[3];
        continue;
case LOADLIT4:
        b = a; a = env[4];
        continue;
case LOADLIT5:
        b = a; a = env[5];
        continue;
case LOADLIT6:
        b = a; a = env[6];
        continue;
case LOADLIT7:
        b = a; a = env[7];
        continue;
case LOADFREE:
        b = a; a = env[bytecodes[pc++] & 0xff].car/*value*/;
        continue;
case LOADFREE1:
        b = a; a = env[1].car/*value*/;
        continue;
case LOADFREE2:
        b = a; a = env[2].car/*value*/;
        continue;
case LOADFREE3:
        b = a; a = env[3].car/*value*/;
        continue;
case LOADFREE4:
        b = a; a = env[4].car/*value*/;
        continue;
case STORELOC:
        stack[sp-(bytecodes[pc++] & 0xff)] = a;
        continue;
case STORELOC0:
        stack[sp-0] = a;
        continue;
case STORELOC1:
        stack[sp-1] = a;
        continue;
case STORELOC2:
        stack[sp-2] = a;
        continue;
case STORELOC3:
        stack[sp-3] = a;
        continue;
case STORELOC4:
        stack[sp-4] = a;
        continue;
case STORELOC5:
        stack[sp-5] = a;
        continue;
case STORELOC6:
        stack[sp-6] = a;
        continue;
case STORELOC7:
        stack[sp-7] = a;
        continue;
case STOREFREE:
        env[bytecodes[pc++] & 0xff].car/*value*/ = a;
        continue;
case STOREFREE1:
        env[1].car/*value*/ = a;
        continue;
case STOREFREE2:
        env[2].car/*value*/ = a;
        continue;
case STOREFREE3:
        env[3].car/*value*/ = a;
        continue;
case LOADLEX:
        Jlisp.error("bytecode LOADLEX not implemented");
case STORELEX:
        Jlisp.error("bytecode STORELEX not implemented");
case CLOSURE:
        Jlisp.error("bytecode CLOSURE not implemented");
case CARLOC0:
        b = a;
        a = stack[sp-0];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC1:
        b = a;
        a = stack[sp-1];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC2:
        b = a;
        a = stack[sp-2];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC3:
        b = a;
        a = stack[sp-3];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC4:
        b = a;
        a = stack[sp-4];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC5:
        b = a;
        a = stack[sp-5];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC6:
        b = a;
        a = stack[sp-6];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC7:
        b = a;
        a = stack[sp-7];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC8:
        b = a;
        a = stack[sp-8];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC9:
        b = a;
        a = stack[sp-9];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC10:
        b = a;
        a = stack[sp-10];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CARLOC11:
        b = a;
        a = stack[sp-11];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CDRLOC0:
        b = a;
        a = stack[sp-0];
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        continue;
case CDRLOC1:
        b = a;
        a = stack[sp-1];
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        continue;
case CDRLOC2:
        b = a;
        a = stack[sp-2];
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        continue;
case CDRLOC3:
        b = a;
        a = stack[sp-3];
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        continue;
case CDRLOC4:
        b = a;
        a = stack[sp-4];
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        continue;
case CDRLOC5:
        b = a;
        a = stack[sp-5];
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        continue;
case CAARLOC0:
        b = a;
        a = stack[sp-0];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CAARLOC1:
        b = a;
        a = stack[sp-1];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CAARLOC2:
        b = a;
        a = stack[sp-2];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CAARLOC3:
        b = a;
        a = stack[sp-3];
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CALL0:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
	a = ((Symbol)env[arg]).fn.op0();
	continue;
case CALL1:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
	a = ((Symbol)env[arg]).fn.op1(a);
	continue;
case CALL2:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
	a = ((Symbol)env[arg]).fn.op2(b, a);
	continue;
case CALL2R:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
	a = ((Symbol)env[arg]).fn.op2(a, b);
	continue;
case CALL3:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        a = ((Symbol)env[arg]).fn.opn(new LispObject [] {stack[sp--], b, a});
	continue;
case CALLN:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        switch (bytecodes[pc++] & 0xff)
        {
    case 4: sp -= 2;
            a = ((Symbol)env[arg]).fn.opn(new LispObject [] 
                {stack[sp+1], stack[sp+2], b, a});
	    continue;
    case 5: sp -= 3;
            a = ((Symbol)env[arg]).fn.opn(new LispObject [] 
                {stack[sp+1], stack[sp+2], stack[sp+3], b, a});
	    continue;
    case 6: sp -= 4;
            a = ((Symbol)env[arg]).fn.opn(new LispObject [] 
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], b, a});
	    continue;
    case 7: sp -= 5;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
	        {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], b, a});
            continue;
    case 8: sp -= 6;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], b, a});
            continue;
    case 9: sp -= 7;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], b, a});
            continue;
    case 10:sp -= 8;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8], b, a});
            continue;
    case 11:sp -= 9;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8],
                 stack[sp+9], b, a});
            continue;
    case 12:sp -= 10;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8],
                 stack[sp+9], stack[sp+10], b, a});
            continue;
    case 13:sp -= 11;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8],
                 stack[sp+9], stack[sp+10], stack[sp+11], b, a});
            continue;
    case 14:sp -= 12;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8],
                 stack[sp+9], stack[sp+10], stack[sp+11], stack[sp+12], b, a});
            continue;
    case 15:sp -= 13;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8],
                 stack[sp+9], stack[sp+10], stack[sp+11], stack[sp+12],
                 stack[sp+13], b, a});
            continue;
// The Standard Lisp Report mandates at least 15 arguments must be supported.
// Common Lisp maybe does not have any real limit? Anyway I will go to 20
// here.
    case 16:sp -= 14;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8],
                 stack[sp+9], stack[sp+10], stack[sp+11], stack[sp+12],
                 stack[sp+13], stack[sp+14], b, a});
            continue;
    case 17:sp -= 15;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8],
                 stack[sp+9], stack[sp+10], stack[sp+11], stack[sp+12],
                 stack[sp+13], stack[sp+14], stack[sp+15], b, a});
            continue;
    case 18:sp -= 16;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8],
                 stack[sp+9], stack[sp+10], stack[sp+11], stack[sp+12],
                 stack[sp+13], stack[sp+14], stack[sp+15], stack[sp+16], b, a});
            continue;
    case 19:sp -= 17;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8],
                 stack[sp+9], stack[sp+10], stack[sp+11], stack[sp+12],
                 stack[sp+13], stack[sp+14], stack[sp+15], stack[sp+16],
                 stack[sp+17], b, a});
            continue;
    case 20:sp -= 18;
            a = ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[sp+1], stack[sp+2], stack[sp+3], stack[sp+4], 
                 stack[sp+5], stack[sp+6], stack[sp+7], stack[sp+8],
                 stack[sp+9], stack[sp+10], stack[sp+11], stack[sp+12],
                 stack[sp+13], stack[sp+14], stack[sp+15], stack[sp+16],
                 stack[sp+17], stack[sp+18], b, a});
            continue;
    default:
            Jlisp.error("calls with over 20 args not supported in this Lisp");
        }
case CALL0_0:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = op0();   // optimisation on call to self!
	continue;
case CALL0_1:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[1]).fn.op0();
	continue;
case CALL0_2:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[2]).fn.op0();
	continue;
case CALL0_3:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[3]).fn.op0();
	continue;
case CALL1_0:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = op1(a);   // call to self
	continue;
case CALL1_1:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[1]).fn.op1(a);
	continue;
case CALL1_2:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[2]).fn.op1(a);
	continue;
case CALL1_3:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[3]).fn.op1(a);
	continue;
case CALL1_4:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[4]).fn.op1(a);
	continue;
case CALL1_5:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[5]).fn.op1(a);
	continue;
case CALL2_0:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = op2(b, a);   // call to self
	continue;
case CALL2_1:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[1]).fn.op2(b, a);
	continue;
case CALL2_2:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[2]).fn.op2(b, a);
	continue;
case CALL2_3:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[3]).fn.op2(b, a);
	continue;
case CALL2_4:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
	a = ((Symbol)env[4]).fn.op2(b, a);
	continue;
case BUILTIN0:
        arg = bytecodes[pc++] & 0xff;
	a = builtin0[arg].op0();
	continue;
case BUILTIN1:
        arg = bytecodes[pc++] & 0xff;
	a = builtin1[arg].op1(a);
	continue;
case BUILTIN2:
        arg = bytecodes[pc++] & 0xff;
	a = builtin2[arg].op2(b, a);
	continue;
case BUILTIN2R:
        arg = bytecodes[pc++] & 0xff;
	a = builtin2[arg].op2(a, b);
 	continue;
case BUILTIN3:
        arg = bytecodes[pc++] & 0xff;
	a = builtin3[arg].opn(new LispObject [] {stack[sp--], b, a});
	continue;
case APPLY1:
        if (b instanceof Symbol)
        {   a = ((Symbol)b).fn.op1(a);
        }
        else if (b instanceof LispFunction)
        {   a = ((LispFunction)b).op1(a);
        }
        else a = Fns.apply1(b, a);
        continue;
case APPLY2:
        if (stack[sp] instanceof Symbol)
        {   a = ((Symbol)stack[sp--]).fn.op2(b, a);
        }
        else if (stack[sp] instanceof LispFunction)
        {   a = ((LispFunction)stack[sp--]).op2(b, a);
        }
        else a = Fns.apply2(stack[sp--], b, a);
        continue;
case APPLY3:
        sp -= 2;
        if (stack[sp+1] instanceof Symbol)
        {   a = ((Symbol)stack[sp+1]).fn.opn(new LispObject [] {stack[sp+2], b, a});
        }
        else if (stack[sp+1] instanceof LispFunction)
        {   a = ((LispFunction)stack[sp+1]).opn(new LispObject [] {stack[sp+2], b, a});
        }
        else a = Fns.apply3(stack[sp+1], stack[sp+2], b, a);
        continue;
case APPLY4:
        sp -= 3;
        if (stack[sp+1] instanceof Symbol)
        {   a = ((Symbol)stack[sp+1]).fn.opn(new LispObject [] {stack[sp+2], stack[sp+3], b, a});
        }
        else if (stack[sp+1] instanceof LispFunction)
        {   a = ((LispFunction)stack[sp+1]).opn(new LispObject [] {stack[sp+2], stack[sp+3], b, a});
        }
        else a = Fns.applyn(stack[sp+1], 
            new LispObject [] {stack[sp+2], stack[sp+3], b, a});
        continue;
case JCALL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++];
        switch (arg & 0xe0)  // number of args
        {
    case 0x00: arg &= 0x1f;
               if (arg == 0)
               {   sp = spsave;
                   pc = 0;
                   continue;
               }
               sp = spsave;
               return ((Symbol)env[arg & 0x1f]).fn.op0();
    case 0x20: arg &= 0x1f;
               if (arg == 0)
               {   stack[spsave] = a;
                   sp = spsave;
                   pc = 0;
                   continue;
               }
               sp = spsave;
               return ((Symbol)env[arg & 0x1f]).fn.op1(a);
    case 0x40: arg &= 0x1f;
               if (arg == 0)
               {   stack[spsave] = a;
                   stack[spsave-1] = b;
                   sp = spsave;
                   pc = 0;
                   continue;
               }
               sp = spsave;
               return ((Symbol)env[arg & 0x1f]).fn.op2(b, a);
    case 0x60: arg &= 0x1f;
               if (arg == 0)
               {   stack[spsave] = a;
                   stack[spsave-1] = b;
                   stack[spsave-2] = stack[sp];
                   sp = spsave;
                   pc = 0;
                   continue;
               }
               pc = sp;
               sp = spsave;
               return ((Symbol)env[arg & 0x1f]).fn.opn(
                          new LispObject [] {stack[pc--], b, a});
    case 0x80: arg &= 0x1f;
               if (arg == 0)
               {   stack[spsave] = a;
                   stack[spsave-1] = b;
                   stack[spsave-2] = stack[sp];
                   stack[spsave-3] = stack[sp-1];
                   sp = spsave;
                   pc = 1;  // NB to allow for arg-count byte
                   continue;
               }
               pc = sp;
               sp = spsave;
               pc -= 2; 
               return ((Symbol)env[arg & 0x1f]).fn.opn(
                   new LispObject [] {stack[pc+1], stack[pc+2], b, a});
    case 0xa0: arg &= 0x1f;
               if (arg == 0)
               {   stack[spsave] = a;
                   stack[spsave-1] = b;
                   stack[spsave-2] = stack[sp];
                   stack[spsave-3] = stack[sp-1];
                   stack[spsave-4] = stack[sp-2];
                   sp = spsave;
                   pc = 1;
                   continue;
               }
               pc = sp;
               sp = spsave;
               pc -= 3; 
               return ((Symbol)env[arg & 0x1f]).fn.opn(
                   new LispObject [] {stack[pc+1], stack[pc+2], stack[pc+3], b, a});
    case 0xc0: arg &= 0x1f;
               if (arg == 0)
               {   stack[spsave] = a;
                   stack[spsave-1] = b;
                   stack[spsave-2] = stack[sp];
                   stack[spsave-3] = stack[sp-1];
                   stack[spsave-4] = stack[sp-2];
                   stack[spsave-5] = stack[sp-3];
                   sp = spsave;
                   pc = 1;
                   continue;
               }
               pc = sp;
               sp = spsave;
               pc -= 4; 
               return ((Symbol)env[arg & 0x1f]).fn.opn(
                   new LispObject [] {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], b, a});
    case 0xe0: arg &= 0x1f;
               if (arg == 0)
               {   stack[spsave] = a;
                   stack[spsave-1] = b;
                   stack[spsave-2] = stack[sp];
                   stack[spsave-3] = stack[sp-1];
                   stack[spsave-4] = stack[sp-2];
                   stack[spsave-5] = stack[sp-3];
                   stack[spsave-6] = stack[sp-4];
                   sp = spsave;
                   pc = 1;
                   continue;
               }
               pc = sp;
               sp = spsave;
               pc -= 5; 
               return ((Symbol)env[arg & 0x1f]).fn.opn(
                   new LispObject [] {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], stack[pc+5], b, a});
    default:
               return Jlisp.error("oddity with JCALL " +
                                  Integer.toHexString(arg));
        }
case JCALLN:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++];
        switch (bytecodes[pc++] & 0xff)  // number of args
        {
// Just at present I do not treat calls to self specially here (in the
// way that I do for JCALL rather than JCALLN). This is something that I
// should fix sometime...
    case 0: sp = spsave;
            return ((Symbol)env[arg]).fn.op0();
    case 1: sp = spsave;
            return ((Symbol)env[arg]).fn.op1(a);
    case 2: sp = spsave;
            return ((Symbol)env[arg]).fn.op2(b, a);
    case 3: pc = sp;
            sp = spsave;
            return ((Symbol)env[arg]).fn.opn(new LispObject [] 
                {stack[pc--], b, a});
    case 4: pc = sp;
            sp = spsave;
            pc -= 2; 
            return ((Symbol)env[arg]).fn.opn(new LispObject [] 
                {stack[pc+1], stack[pc+2], b, a});
    case 5: pc = sp;
            sp = spsave;
            pc -= 3; 
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], b, a});
    case 6: pc = sp;
            sp = spsave;
            pc -= 4; 
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], b, a});
    case 7: pc = sp;
            sp = spsave;
            pc -= 5;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
	        {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], b, a});
    case 8: pc = sp;
            sp = spsave;
            pc -= 6;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], b, a});
    case 9: pc = sp;
            sp = spsave;
            pc -= 7;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], b, a});
    case 10:pc = sp;
            sp = spsave;
            pc -= 8;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8], b, a});
    case 11:pc = sp;
            sp = spsave;
            pc -= 9;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8],
                 stack[pc+9], b, a});
    case 12:pc = sp;
            sp = spsave;
            pc -= 10;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8],
                 stack[pc+9], stack[pc+10], b, a});
    case 13:pc = sp;
            sp = spsave;
            pc -= 11;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8],
                 stack[pc+9], stack[pc+10], stack[pc+11], b, a});
    case 14:pc = sp;
            sp = spsave;
            pc -= 12;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8],
                 stack[pc+9], stack[pc+10], stack[pc+11], stack[pc+12], b, a});
    case 15:pc = sp;
            sp = spsave;
            pc -= 13;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8],
                 stack[pc+9], stack[pc+10], stack[pc+11], stack[pc+12],
                 stack[pc+13], b, a});
// The Standard Lisp Report mandates at least 15 arguments must be supported.
// Common Lisp maybe does not have any real limit? Anyway I will go to 20
// here.
    case 16:pc = sp;
            sp = spsave;
            pc -= 14;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8],
                 stack[pc+9], stack[pc+10], stack[pc+11], stack[pc+12],
                 stack[pc+13], stack[pc+14], b, a});
    case 17:pc = sp;
            sp = spsave;
            pc -= 15;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8],
                 stack[pc+9], stack[pc+10], stack[pc+11], stack[pc+12],
                 stack[pc+13], stack[pc+14], stack[pc+15], b, a});
    case 18:pc = sp;
            sp = spsave;
            pc -= 16;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8],
                 stack[pc+9], stack[pc+10], stack[pc+11], stack[pc+12],
                 stack[pc+13], stack[pc+14], stack[pc+15], stack[pc+16], b, a});
    case 19:pc = sp;
            sp = spsave;
            pc -= 17;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8],
                 stack[pc+9], stack[pc+10], stack[pc+11], stack[pc+12],
                 stack[pc+13], stack[pc+14], stack[pc+15], stack[pc+16],
                 stack[pc+17], b, a});
    case 20:pc = sp;
            sp = spsave;
            pc -= 18;
            return ((Symbol)env[arg]).fn.opn(new LispObject []
                {stack[pc+1], stack[pc+2], stack[pc+3], stack[pc+4], 
                 stack[pc+5], stack[pc+6], stack[pc+7], stack[pc+8],
                 stack[pc+9], stack[pc+10], stack[pc+11], stack[pc+12],
                 stack[pc+13], stack[pc+14], stack[pc+15], stack[pc+16],
                 stack[pc+17], stack[pc+18], b, a});
    default:
            Jlisp.error("calls with over 20 args not supported in this Lisp");
        }
case JUMP:
        pc = pc + (bytecodes[pc] & 0xff) + 1;
        continue;
case JUMP_B:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        pc = pc - (bytecodes[pc] & 0xff) + 1;
        continue;
case JUMP_L:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        pc = pc + (arg << 8) + (bytecodes[pc] & 0xff) + 1;
        continue;
case JUMP_BL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        pc = pc - ((arg << 8) + (bytecodes[pc] & 0xff)) + 1;
        continue;
case JUMPNIL:
        if (a == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNIL_B:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        if (a == Environment.nil) pc = pc - (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNIL_L:
        arg = bytecodes[pc++] & 0xff;
        if (a == Environment.nil) pc = pc + (arg << 8) + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNIL_BL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        if (a == Environment.nil) pc = pc - ((arg << 8) + (bytecodes[pc] & 0xff)) + 1;
        else pc++;
        continue;
case JUMPT:
        if (a != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPT_B:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        if (a != Environment.nil) pc = pc - (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPT_L:
        arg = bytecodes[pc++] & 0xff;
        if (a != Environment.nil) pc = pc + (arg << 8) + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPT_BL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        if (a != Environment.nil) pc = pc - ((arg << 8) + (bytecodes[pc] & 0xff)) + 1;
        else pc++;
        continue;
case JUMPATOM:
        if (a.atom) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPATOM_B:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        if (a.atom) pc = pc - (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPATOM_L:
        arg = bytecodes[pc++] & 0xff;
        if (a.atom) pc = pc + (arg << 8) + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPATOM_BL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        if (a.atom) pc = pc - ((arg << 8) + (bytecodes[pc] & 0xff)) + 1;
        else pc++;
        continue;
case JUMPNATOM:
        if (!a.atom) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNATOM_B:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        if (!a.atom) pc = pc - (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNATOM_L:
        arg = bytecodes[pc++] & 0xff;
        if (!a.atom) pc = pc + (arg << 8) + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNATOM_BL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        if (!a.atom) pc = pc - ((arg << 8) + (bytecodes[pc] & 0xff)) + 1;
        else pc++;
        continue;
case JUMPEQ:
// @@@ here and many related places would need treatment of numbers if
// I had to support EQ being a reliable comparison on integers.
        if (a == b) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPEQ_B:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        if (a == b) pc = pc - (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPEQ_L:
        arg = bytecodes[pc++] & 0xff;
        if (a == b) pc = pc + (arg << 8) + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPEQ_BL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        if (a == b) pc = pc - ((arg << 8) + (bytecodes[pc] & 0xff)) + 1;
        else pc++;
        continue;
case JUMPNE:
        if (a != b) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNE_B:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        if (a != b) pc = pc - (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNE_L:
        arg = bytecodes[pc++] & 0xff;
        if (a != b) pc = pc + (arg << 8) + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNE_BL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        if (a != b) pc = pc - ((arg << 8) + (bytecodes[pc] & 0xff)) + 1;
        else pc++;
        continue;
case JUMPEQUAL:
        if (a.lispequals(b)) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPEQUAL_B:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        if (a.lispequals(b)) pc = pc - (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPEQUAL_L:
        arg = bytecodes[pc++] & 0xff;
        if (a.lispequals(b)) pc = pc + (arg << 8) + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPEQUAL_BL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        if (a.lispequals(b)) pc = pc - ((arg << 8) + (bytecodes[pc] & 0xff)) + 1;
        else pc++;
        continue;
case JUMPNEQUAL:
        if (!a.lispequals(b)) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNEQUAL_B:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        if (!a.lispequals(b)) pc = pc - (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNEQUAL_L:
        arg = bytecodes[pc++] & 0xff;
        if (!a.lispequals(b)) pc = pc + (arg << 8) + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNEQUAL_BL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        arg = bytecodes[pc++] & 0xff;
        if (!a.lispequals(b)) pc = pc - ((arg << 8) + (bytecodes[pc] & 0xff)) + 1;
        else pc++;
        continue;
case JUMPL0NIL:
        if (stack[sp-0] == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL0T:
        if (stack[sp-0] != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL1NIL:
        if (stack[sp-1] == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL1T:
        if (stack[sp-1] != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL2NIL:
        if (stack[sp-2] == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL2T:
        if (stack[sp-2] != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL3NIL:
        if (stack[sp-3] == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL3T:
        if (stack[sp-3] != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL4NIL:
        if (stack[sp-4] == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL4T:
        if (stack[sp-4] != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPST0NIL:
        if ((stack[sp-0] = a) == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPST0T:
        if ((stack[sp-0] = a) != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPST1NIL:
        if ((stack[sp-1] = a) == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPST1T:
        if ((stack[sp-1] = a) != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPST2NIL:
        if ((stack[sp-2] = a) == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPST2T:
        if ((stack[sp-2] = a) != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL0ATOM:
        if (stack[sp-0].atom) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL0NATOM:
        if (!stack[sp-0].atom) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL1ATOM:
        if (stack[sp-1].atom) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL1NATOM:
        if (!stack[sp-1].atom) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL2ATOM:
        if (stack[sp-2].atom) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL2NATOM:
        if (!stack[sp-2].atom) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL3ATOM:
        if (stack[sp-3].atom) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPL3NATOM:
        if (!stack[sp-3].atom) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFREE1NIL:
        if (env[1].car/*value*/ == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFREE1T:
        if (env[1].car/*value*/ != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFREE2NIL:
        if (env[2].car/*value*/ == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFREE2T:
        if (env[2].car/*value*/ != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFREE3NIL:
        if (env[3].car/*value*/ == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFREE3T:
        if (env[3].car/*value*/ != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFREE4NIL:
        if (env[4].car/*value*/ == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFREE4T:
        if (env[4].car/*value*/ != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFREENIL:
        arg = bytecodes[pc++] & 0xff;
        if (env[arg].car/*value*/ == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFREET:
        arg = bytecodes[pc++] & 0xff;
        if (env[arg].car/*value*/ != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPLIT1EQ:
        if (env[1] == a) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPLIT1NE:
        if (env[1] != a) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPLIT2EQ:
        if (env[2] == a) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPLIT2NE:
        if (env[2] != a) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPLIT3EQ:
        if (env[3] == a) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPLIT3NE:
        if (env[3] != a) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPLIT4EQ:
        if (env[4] == a) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPLIT4NE:
        if (env[4] != a) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPLITEQ:
        arg = bytecodes[pc++] & 0xff;
        if (env[arg] == a) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPLITNE:
        arg = bytecodes[pc++] & 0xff;
        if (env[arg] != a) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPB1NIL:
        arg = bytecodes[pc++] & 0xff;
        if (builtin1[arg].op1(a) == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPB1T:
        arg = bytecodes[pc++] & 0xff;
        if (builtin1[arg].op1(a) != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPB2NIL:
        arg = bytecodes[pc++] & 0xff;
        if (builtin2[arg].op2(b, a) == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPB2T:
        arg = bytecodes[pc++] & 0xff;
        if (builtin2[arg].op2(b, a) != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPFLAGP:
        arg = bytecodes[pc++] & 0xff;
        if (builtin2[BIflagp].op2(a, env[arg]) != Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNFLAGP:
        arg = bytecodes[pc++] & 0xff;
        if (builtin2[BIflagp].op2(a, env[arg]) == Environment.nil) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPEQCAR:
        arg = bytecodes[pc++] & 0xff;
        if (!a.atom && env[arg] == a.car) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case JUMPNEQCAR:
        arg = bytecodes[pc++] & 0xff;
        if (a.atom || env[arg] != a.car) pc = pc + (bytecodes[pc] & 0xff) + 1;
        else pc++;
        continue;
case CATCH:
        Jlisp.error("bytecode CATCH not implemented");
case CATCH_B:
        Jlisp.error("bytecode CATCH_B not implemented");
case CATCH_L:
        Jlisp.error("bytecode CATCH_L not implemented");
case CATCH_BL:
        Jlisp.error("bytecode CATCHH_BL not implemented");
case UNCATCH:
        Jlisp.error("bytecode UNCATCH not implemented");
case THROW:
        Jlisp.error("bytecode THROW not implemented");
case PROTECT:
        Jlisp.error("bytecode PROTECT not implemented");
case UNPROTECT:
        Jlisp.error("bytecode UNPROTECT not implemented");
case PVBIND:
        Jlisp.error("bytecode PVBIND not implemented");
case PVRESTORE:
        Jlisp.error("bytecode PVRESTORE not implemented");
case FREEBIND:
        arg = bytecodes[pc++] & 0xff;
        {   LispObject [] v = ((LispVector)env[arg]).vec;
            for (int i=0; i<v.length; i++)
            {   stack[++sp] = v[i].car/*value*/;
                v[i].car/*value*/ = Environment.nil;
            }
            stack[++sp] = env[arg];
            stack[++sp] = Spid.fbind;
        }
        continue;
case FREERSTR:
        {   sp--;   // ought to be the Spid
            LispObject [] v = ((LispVector)stack[sp--]).vec;
            for (int i=v.length-1; i>=0; i--)
            {   v[i].car/*value*/ = stack[sp--];
            }
        }
        continue;
case EXIT:
        sp = spsave;
        return a;
case NILEXIT:
        sp = spsave;
        return Environment.nil;
case LOC0EXIT:
        pc = sp;
        sp = spsave;
        return stack[pc-0];
case LOC1EXIT:
        pc = sp;
        sp = spsave;
        return stack[pc-1];
case LOC2EXIT:
        pc = sp;
        sp = spsave;
        return stack[pc-2];
case PUSH:
        stack[++sp] = a;
        continue;
case PUSHNIL:
        stack[++sp] = Environment.nil;
        continue;
case PUSHNIL2:
        stack[++sp] = Environment.nil;
        stack[++sp] = Environment.nil;
        continue;
case PUSHNIL3:
        stack[++sp] = Environment.nil;
        stack[++sp] = Environment.nil;
        stack[++sp] = Environment.nil;
        continue;
case PUSHNILS:
        arg = bytecodes[pc++] & 0xff;
        for (int i=0; i<arg; i++)
            stack[++sp] = Environment.nil;
        continue;
case POP:
        b = a;
        a = stack[sp--];
        continue;
case LOSE:
        sp--;
        continue;
case LOSE2:
        sp -= 2;
        continue;
case LOSE3:
        sp -= 3;
        continue;
case LOSES:
        sp -= (bytecodes[pc++] & 0xff);
        continue;
case SWOP:
        w = a; a = b; b = w;
        continue;
case EQCAR:
        if (b.atom)
        {   a = Environment.nil;
            continue;
        }
        else b = b.car;
        // drop through into EQ
case EQ:
        if (a instanceof LispInteger)                         // @@@ EQ
            a = a.lispequals(b) ? Jlisp.lispTrue : Environment.nil; // @@@ EQ
        else                                                  // @@@ EQ
            a = (a == b) ? Jlisp.lispTrue : Environment.nil;
        continue;
case EQUAL:
        a = (a.lispequals(b)) ? Jlisp.lispTrue : Environment.nil;
        continue;
case NUMBERP:
        a = (a instanceof LispNumber) ? Jlisp.lispTrue : Environment.nil;
        continue;
case CAR:
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CDR:
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        continue;
case CAAR:
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CADR:
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        continue;
case CDAR:
        if (a.atom) Jlisp.error("attempt to take car of an atom", a);
        a = a.car;
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        continue;
case CDDR:
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        if (a.atom) Jlisp.error("attempt to take cdr of an atom", a);
        a = a.cdr;
        continue;
case CONS:
        a = new Cons(b, a);
        continue;
case NCONS:
        a = new Cons(a, Environment.nil);
        continue;
case XCONS:
        a = new Cons(a, b);
        continue;
case ACONS:
        a = new Cons(new Cons(stack[sp--], b), a);
        continue;
case LENGTH:
        a = builtin1[BIlength].op1(a);
        continue;
case LIST2:
        a = new Cons(b, new Cons(a, Environment.nil));
        continue;
case LIST2STAR:
        a = new Cons(stack[sp--], new Cons(b, a));
        continue;
case LIST3:
        a = new Cons(stack[sp--], new Cons(b, new Cons(a, Environment.nil)));
        continue;
case PLUS2:
        a = builtin2[BIplus2].op2(b, a);
        continue;
case ADD1:
        a = builtin1[BIadd1].op1(a);
        continue;
case DIFFERENCE:
        a = builtin2[BIdifference].op2(b, a);
        continue;
case SUB1:
        a = builtin1[BIsub1].op1(a);
        continue;
case TIMES2:
        a = builtin2[BItimes2].op2(b, a);
        continue;
case GREATERP:
        a = builtin2[BIgreaterp].op2(b, a);
        continue;
case LESSP:
        a = builtin2[BIlessp].op2(b, a);
        continue;
case FLAGP:
        a = builtin2[BIflagp].op2(b, a);
        continue;
case GET:
        a = builtin2[BIget].op2(b, a);
        continue;
case LITGET:
        arg = bytecodes[pc++] & 0xff;
        a = builtin2[BIget].op2(a, env[arg]);
        continue;
case GETV:
        a = builtin2[BIgetv].op2(b, a);
        continue;
case QGETV:
        a = builtin2[BIqgetv].op2(b, a);
        continue;
case QGETVN:
        arg = bytecodes[pc++] & 0xff;
        a = builtin2[BIget].op2(a, LispInteger.valueOf(arg));
        continue;
case BIGSTACK:
        iw = bytecodes[pc++] & 0xff;
        System.out.printf("BIGSTACK %x%n", iw);
        switch (iw & 0xc0)
        {
    default:
    //case 0x00:
            b = a;
            iw = (iw & 0x3f) << 8;
            a = stack[sp-(iw + (bytecodes[pc++] & 0xff))];
            continue;
    case 0x40:
            iw = (iw & 0x3f) << 8;
            stack[sp-(iw + (bytecodes[pc++] & 0xff))] = a;
            continue;
    case 0x80:
            Jlisp.error("BIG CLOSURE not implemented");
    case 0xc0:
            Jlisp.error("BIG LEX ACCESS not implemented");
//          n = bytecodes[pc++] & 0xff;
//          k = bytecodes[pc++] & 0xff;
//          n = (n << 4) | (k >> 4);
//          r1 = stack[sp+1-n];
//          b = a;
//          n = w & 0x1f;
//          while (n != 0) n--;
//          if ((w & 0x20) == 0)
//              a = 0;
//          else ?? = a;
//          continue;
        }
case BIGCALL:
        if(Jlisp.interruptEvaluation == true)
        {
            handleInterrupt();
        }
        iw = bytecodes[pc++] & 0xff;
//        System.out.printf("BIGCALL %x%n", iw);
        fname = (bytecodes[pc++] & 0xff) + ((iw & 0xf) << 8);
        switch (iw >> 4)
        {
    default:
    //case 0: // call0
	    a = ((Symbol)env[fname]).fn.op0();
            continue;
    case 1:   // call1
	    a = ((Symbol)env[fname]).fn.op1(a);
            continue;
    case 2:   // call2
	    a = ((Symbol)env[fname]).fn.op2(b, a);
            continue;
    case 3:   // call3
            a = ((Symbol)env[fname]).fn.opn(
                new LispObject [] {stack[sp--], b, a});
            continue;
    case 4:   // calln
            Jlisp.error("BIG CALLN not implemented");
    case 5:   // call2r
	    a = ((Symbol)env[fname]).fn.op2(a, b);
            continue;
    case 6:   // loadfree
            b = a;
            a = env[fname].car/*value*/;
            continue;
    case 7:   // storefree
            env[fname].car/*value*/ = a;
            continue;
    case 8:   // jcall0
            sp = spsave;
            return ((Symbol)env[fname]).fn.op0();
    case 9:   // jcall1
            sp = spsave;
            return ((Symbol)env[fname]).fn.op1(a);
    case 10:  // jcall2
            sp = spsave;
            return ((Symbol)env[fname]).fn.op2(b, a);
    case 11:  // jcall3
            pc = sp;
            sp = spsave;
            return ((Symbol)env[fname]).fn.opn(
                new LispObject [] {stack[pc--], b, a});
    case 12:  // jcalln
            Jlisp.error("BIG JCALLN not implemented");
    case 13:  // freebind
            Jlisp.error("BIG FREEBIND not implemented");
    case 14:  // litget
            Jlisp.error("BIG LITGET not implemented");
    case 15:  // loadlit
            b = a;
            a = env[fname];
            continue;
        }
case ICASE:
        Jlisp.error("bytecode ICASE not implemented");
case FASTGET:
        Jlisp.error("bytecode FASTGET not implemented");
case SPARE1:
        Jlisp.error("bytecode SPARE1 not implemented");
case SPARE2:
        Jlisp.error("bytecode SPARE2 not implemented");
    }
    }}
    catch (Exception e)
    {
// What I NEED to do here is to restore any free bindings that have been made.
// I can find them because there is a Spid.fbind on the stack to mark them.
//
// What I also WANT to do is to print a fragment of backtrace in relevant
// cases.

        while (sp != spsave)
        {   a = stack[sp--];
            if (a != Spid.fbind) continue;
            LispObject [] v = ((LispVector)stack[sp--]).vec;
            for (int i=v.length-1; i>=0; i--)
            {   v[i].car/*value*/ = stack[sp--];
            }
        }
        if (Jlisp.backtrace)
        {   Jlisp.errprint("Within: ");
            env[0].errPrint();
            Jlisp.errprintln();
        }
        throw e;
    }
}

}


// End of Bytecode.java

