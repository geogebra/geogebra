// -*- mode:C++ ; compile-command: "g++ -I.. -g -c ti89.cc -fno-strict-aliasing -DGIAC_GENERIC_CONSTANTS -DIN_GIAC -DHAVE_CONFIG_H" -*-
#include "giacPCH.h"
/*
 *  Copyright (C) 2000,2007 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
using namespace std;
#include <stdexcept>
#include <cmath>
#include <cstdlib>
#include <string.h>
// #include <numeric_limits>
#include "misc.h"
#include "usual.h"
#include "sym2poly.h"
#include "rpn.h"
#include "prog.h"
#include "derive.h"
#include "subst.h"
#include "intg.h"
#include "vecteur.h"
#include "ifactor.h"
#include "solve.h"
#include "modpoly.h"
#include "permu.h"
#include "sym2poly.h"
#include "plot.h"
#include "lin.h"
#include "modpoly.h"
#include "desolve.h"
#include "alg_ext.h"
#include "moyal.h"
#include "ti89.h"
#include "maple.h"
#include "input_parser.h"
#include "input_lexer.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  /*
    string printasti_not_implemented(const gen & feuille,const char * sommetstr){
    if (feuille.type!=_VECT || feuille._VECTptr->size()!=2)
    return sommetstr+("("+feuille.print()+")");
    vecteur & v=*feuille._VECTptr;
    if (v[0].type==_STRNG)
    return *v[0]._STRNGptr+"("+v[1].print()+")";
    else
    return v[0].print()+"("+v[1].print()+")";
    }
  */
  static const char _ti_not_implemented_s[]="ti_not_implemented";
  static define_unary_function_eval (__ti_not_implemented,&_nop,_ti_not_implemented_s); // never evaled
  define_unary_function_ptr( at_ti_not_implemented ,alias_at_ti_not_implemented ,&__ti_not_implemented);

  static const char _ti_endtag_s[]="ti_endtag";
  static define_unary_function_eval (__ti_endtag,&_nop,_ti_endtag_s); // never evaled
  define_unary_function_ptr( at_ti_endtag ,alias_at_ti_endtag ,&__ti_endtag);

  gen _seq(const gen & g,GIAC_CONTEXT){
    gen g1(g);
    if (g.type==_VECT && g.subtype==_SEQ__VECT && !g._VECTptr->empty()){
      vecteur v(*g._VECTptr);
      if (v.size()>=2){
	gen x(v[1]);
	if (x.is_symb_of_sommet(at_equal) && x._SYMBptr->feuille.type==_VECT && !x._SYMBptr->feuille._VECTptr->empty())
	  x=x._SYMBptr->feuille._VECTptr->front();
	if (v.front().is_symb_of_sommet(at_quote))
	  v.front()=v.front()._SYMBptr->feuille;
	//gen tmp(quote_eval(makevecteur(v.front()),makevecteur(x),contextptr));
	//v.front()=tmp[0];
      }
      else
	v.front()=eval(v.front(),eval_level(contextptr),contextptr);
      g1=gen(v,_SEQ__VECT);
    }
    return seqprod(g1,0,contextptr);
  }
  static const char _seq_s[]="seq";
  static define_unary_function_eval_quoted (__seq,&_seq,_seq_s);
  define_unary_function_ptr5( at_seq ,alias_at_seq,&__seq,_QUOTE_ARGUMENTS,true);

  gen _logb(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    int n=0; gen e1(g._VECTptr->front()),b(g._VECTptr->back()),q;
    if (is_integer(e1) && is_integer(b) && is_strictly_greater(b,1,contextptr) && !is_zero(e1)){
      while (is_zero(irem(e1,b,q))){
	if (q.type==_ZINT)
	  e1=*q._ZINTptr;
	else
	  e1=q;
	++n;
      }
    }
    return rdiv(ln(e1,contextptr),ln(b,contextptr),contextptr)+n;
    // return ln(g._VECTptr->front(),contextptr)/ln(g._VECTptr->back(),contextptr);
  }
  static const char _logb_s[]="logb";
  static define_unary_function_eval (__logb,&_logb,_logb_s);
  define_unary_function_ptr5( at_logb ,alias_at_logb,&__logb,0,true);

  static string getType(const gen & g){
    switch (g.type){
    case _INT_: case _REAL: case _DOUBLE_:
      return "NUM";
    case _VECT:
      if (ckmatrix(g))
	return "MAT";
      else
	return "LIST";
    case _IDNT:
      return "VAR";
    case _SYMB:
      if (g.is_symb_of_sommet(at_program))
	return "FUNC";
      else
	return "EXPR";
    case _CPLX:
      return "EXPR";
    case _STRNG:
      return "STR";
    default:
      return "OTHER";
    }
  }
  gen _getType(const gen & g,GIAC_CONTEXT){
    return string2gen(getType(g),false);
  }
  static const char _getType_s[]="getType";
  static define_unary_function_eval (__getType,&_getType,_getType_s);
  define_unary_function_ptr5( at_getType ,alias_at_getType,&__getType,0,true);

  gen _Define(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    gen g1=v.front(),g2=v.back();
    if (!g1.is_symb_of_sommet(at_of))
      return gensizeerr(contextptr);
    gen &g11=g1._SYMBptr->feuille;
    if (g11.type!=_VECT || g11._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen gname=g11._VECTptr->front(),garg=g11._VECTptr->back();
    return symb_sto(symb_program(garg,garg*zero,symb_bloc(g2),contextptr),gname);
  }
  static const char _colDim_s[]="colDim";
  static define_unary_function_eval (__colDim,&_ncols,_colDim_s);
  define_unary_function_ptr5( at_colDim ,alias_at_colDim,&__colDim,0,true);

  static const char _rowDim_s[]="rowDim";
  static define_unary_function_eval (__rowDim,&_nrows,_rowDim_s);
  define_unary_function_ptr5( at_rowDim ,alias_at_rowDim,&__rowDim,0,true);

  static const char _randMat_s[]="randMat";
  static define_unary_function_eval (__randMat,&_ranm,_randMat_s);
  define_unary_function_ptr5( at_randMat ,alias_at_randMat,&__randMat,0,true);

  static const char _eigVc_s[]="eigVc";
  static define_unary_function_eval (__eigVc,&_egv,_eigVc_s);
  define_unary_function_ptr5( at_eigVc ,alias_at_eigVc,&__eigVc,0,true);

  static const char _eigVl_s[]="eigVl";
  static define_unary_function_eval (__eigVl,&_egvl,_eigVl_s);
  define_unary_function_ptr5( at_eigVl ,alias_at_eigVl,&__eigVl,0,true);

  static const char _transpose_s[]="transpose";
  static define_unary_function_eval (__transpose,&_tran,_transpose_s);
  define_unary_function_ptr5( at_transpose ,alias_at_transpose,&__transpose,0,true);

  static const char _identity_s[]="identity";
  static define_unary_function_eval (__identity,&_idn,_identity_s);
  define_unary_function_ptr5( at_identity ,alias_at_identity,&__identity,0,true);

  gen _isprime(const gen & args,GIAC_CONTEXT){
    gen g=_is_prime(args,contextptr);
    if (is_undef(g)) return g;
    if (g.type==_VECT)
      return g;
    if (g==0){
      g.subtype=_INT_BOOLEAN;
      return g;
    }
    g=plus_one;
    g.subtype=_INT_BOOLEAN;
    return g;
  }
  static const char _isprime_s[]="isprime";
  static define_unary_function_eval (__isprime,&_isprime,_isprime_s);
  define_unary_function_ptr5( at_isprime ,alias_at_isprime,&__isprime,0,true);

  /*
    gen _Input(const gen & g){
    vecteur v(gen2vecteur(g));
    int s=v.size();
    gen res;
    if (s==1)
    res=_inputform(symbolic(at_click,makesequence(v[0],0,v[0])));
    else {
    if (s==2)
    res= _inputform(symbolic(at_click,makesequence(v[0],0,v[1])));
    else
    res= __click.op(g);
    }
    __interactive.op(symbolic(at_print,makesequence(g,res)));
    return res;
    }
  */
  gen _Input(const gen & args,GIAC_CONTEXT){
    return _input(args,false,contextptr);
  }
  static const char _Input_s[]="Input";
  static define_unary_function_eval2_quoted (__Input,&_Input,_Input_s,&printastifunction);
  define_unary_function_ptr5( at_Input ,alias_at_Input,&__Input,_QUOTE_ARGUMENTS,T_RETURN);

  static const char _lis_s[]="lis";
  static define_unary_function_eval2_quoted (__lis,&_Input,_lis_s,&printastifunction);
  define_unary_function_ptr5( at_lis ,alias_at_lis,&__lis,_QUOTE_ARGUMENTS,T_RETURN);

  gen _InputStr(const gen & g,GIAC_CONTEXT){
    vecteur v(gen2vecteur(g));
    int s=v.size();
    gen res;
    if (s==1)
      res= __click.op(makevecteur(string2gen(v[0].print(contextptr)),0,v[0],1),contextptr);
    else {
      if (s==2)
	res= __click.op(makevecteur(string2gen(eval(v[0],eval_level(contextptr),contextptr).print(contextptr)),v[1],1),contextptr);
      else
	res= _input(g,true,contextptr);
    }
    if (is_zero(res))
      return gensizeerr(contextptr);
    return res;
  }

  /*
    gen _InputStr(const gen & args,GIAC_CONTEXT){
    return _input(args,true,contextptr);
    }
  */
  static const char _InputStr_s[]="InputStr";
  static define_unary_function_eval2_quoted (__InputStr,&_InputStr,_InputStr_s,&printastifunction);
  define_unary_function_ptr5( at_InputStr ,alias_at_InputStr,&__InputStr,_QUOTE_ARGUMENTS,T_RETURN);

  static const char _lis_phrase_s[]="lis_phrase";
  static define_unary_function_eval2_quoted (__lis_phrase,&_InputStr,_lis_phrase_s,&printastifunction);
  define_unary_function_ptr5( at_lis_phrase ,alias_at_lis_phrase,&__lis_phrase,_QUOTE_ARGUMENTS,T_RETURN);

  gen _Prompt(const gen & g,GIAC_CONTEXT){
    gen res= _inputform(symbolic(at_click,makesequence(g,g,g)),contextptr);
    __interactive.op(symbolic(at_print,makesequence(g,res)),contextptr);
    return res;
  }
  static const char _Prompt_s[]="Prompt";
  static define_unary_function_eval2_quoted (__Prompt,&_Prompt,_Prompt_s,&printastifunction);
  define_unary_function_ptr5( at_Prompt ,alias_at_Prompt,&__Prompt,_QUOTE_ARGUMENTS,T_RETURN);

  static const char _PopUp_s[]="PopUp";
  static define_unary_function_eval2_quoted (__PopUp,&_choosebox,_PopUp_s,&printastifunction);
  define_unary_function_ptr5( at_PopUp ,alias_at_PopUp,&__PopUp,_QUOTE_ARGUMENTS,T_RETURN);

  gen _cSolve(const gen & g,GIAC_CONTEXT){
    bool old_complex_mode=complex_mode(contextptr);
    complex_mode(true,contextptr);
    gen res=_solve(g,contextptr);
    complex_mode(old_complex_mode,contextptr);
    return res;
  }
  static const char _cSolve_s[]="cSolve";
  static define_unary_function_eval_quoted (__cSolve,&_cSolve,_cSolve_s);
  define_unary_function_ptr5( at_cSolve ,alias_at_cSolve,&__cSolve,_QUOTE_ARGUMENTS,true);

  static const char _csolve_s[]="csolve";
  static define_unary_function_eval_quoted (__csolve,&_cSolve,_csolve_s);
  define_unary_function_ptr5( at_csolve ,alias_at_csolve,&__csolve,_QUOTE_ARGUMENTS,true);

  static const char _resoudre_dans_C_s[]="resoudre_dans_C";
  static define_unary_function_eval_quoted (__resoudre_dans_C,&_cSolve,_resoudre_dans_C_s);
  define_unary_function_ptr5( at_resoudre_dans_C ,alias_at_resoudre_dans_C,&__resoudre_dans_C,_QUOTE_ARGUMENTS,true);

  gen _cFactor(const gen & g,GIAC_CONTEXT){
    bool old_complex_mode=complex_mode(contextptr);
    complex_mode(true,contextptr);
    gen res=_factor(g,contextptr);
    complex_mode(old_complex_mode,contextptr);
    return res;
  }
  static const char _cFactor_s[]="cFactor";
  static define_unary_function_eval (__cFactor,&_cFactor,_cFactor_s);
  define_unary_function_ptr5( at_cFactor ,alias_at_cFactor,&__cFactor,0,true);

  static const char _cfactor_s[]="cfactor";
  static define_unary_function_eval (__cfactor,&_cFactor,_cfactor_s);
  define_unary_function_ptr5( at_cfactor ,alias_at_cfactor,&__cfactor,0,true);

  static const char _factoriser_sur_C_s[]="factoriser_sur_C";
  static define_unary_function_eval (__factoriser_sur_C,&_cFactor,_factoriser_sur_C_s);
  define_unary_function_ptr5( at_factoriser_sur_C ,alias_at_factoriser_sur_C,&__factoriser_sur_C,0,true);

  gen _cpartfrac(const gen & g,GIAC_CONTEXT){
    bool old_complex_mode=complex_mode(contextptr);
    complex_mode(true,contextptr);
    gen res=_partfrac(g,contextptr);
    complex_mode(old_complex_mode,contextptr);
    return res;
  }
  static const char _cpartfrac_s[]="cpartfrac";
  static define_unary_function_eval (__cpartfrac,&_cpartfrac,_cpartfrac_s);
  define_unary_function_ptr5( at_cpartfrac ,alias_at_cpartfrac,&__cpartfrac,0,true);

  gen _nSolve(const gen & g,GIAC_CONTEXT){
    vecteur v(gen2vecteur(g));
    if (v.size()!=2)
      return gensizeerr(contextptr);
    gen var=v[1],guess;
    if (var.type==_SYMB && (var._SYMBptr->sommet==at_equal || var._SYMBptr->sommet==at_same)){
      guess=var._SYMBptr->feuille._VECTptr->back();
      if (guess.is_symb_of_sommet(at_interval))
	return _fsolve(g,contextptr);
      var=var._SYMBptr->feuille._VECTptr->front();
      return newton(remove_equal(v[0]),var,guess,20,1e-5,1e-12,!complex_mode(contextptr),1,0,1,0,1,contextptr);
    }
    else
      return newton(remove_equal(v[0]),var,undef,20,1e-5,1e-12,!complex_mode(contextptr),1,0,1,0,1,contextptr);
  }
  static const char _nSolve_s[]="nSolve";
  static define_unary_function_eval (__nSolve,&_nSolve,_nSolve_s);
  define_unary_function_ptr5( at_nSolve ,alias_at_nSolve,&__nSolve,0,true);

  static gen zeros(const gen &g,bool complexmode,GIAC_CONTEXT){
    vecteur v(solvepreprocess(g,complexmode,contextptr));
    int s=v.size();
    if (s>2)
      return gentoomanyargs("solve");
    return solve(v.front(),v.back(),complexmode,contextptr);
  }
  gen _zeros(const gen & g,GIAC_CONTEXT){
    return zeros(g,false,contextptr);
  }
  static const char _zeros_s[]="zeros";
  static define_unary_function_eval_quoted (__zeros,&_zeros,_zeros_s);
  define_unary_function_ptr5( at_zeros ,alias_at_zeros,&__zeros,_QUOTE_ARGUMENTS,true);

  gen _cZeros(const gen & g,GIAC_CONTEXT){
    return zeros(g,true,contextptr);
  }
  static const char _cZeros_s[]="cZeros";
  static define_unary_function_eval_quoted (__cZeros,&_cZeros,_cZeros_s);
  define_unary_function_ptr5( at_cZeros ,alias_at_cZeros,&__cZeros,_QUOTE_ARGUMENTS,true);
  static const char _czeros_s[]="czeros";
  static define_unary_function_eval_quoted (__czeros,&_cZeros,_czeros_s);
  define_unary_function_ptr5( at_czeros ,alias_at_czeros,&__czeros,_QUOTE_ARGUMENTS,true);

  static gen getDenom(const gen & g,GIAC_CONTEXT){
    vecteur num,den;
    prod2frac(g,num,den);
    return vecteur2prod(den);
  }
  gen _getDenom(const gen & g,GIAC_CONTEXT){
    return apply(g,getDenom,contextptr);
  }
  static const char _getDenom_s[]="getDenom";
  static define_unary_function_eval (__getDenom,&_getDenom,_getDenom_s);
  define_unary_function_ptr5( at_getDenom ,alias_at_getDenom,&__getDenom,0,true);

  static gen denom(const gen & g,GIAC_CONTEXT){
    gen res=_fxnd(g,contextptr);
    return res._VECTptr->back();
  }
  gen _denom(const gen & g,GIAC_CONTEXT){
    return apply(g,denom,contextptr);
  }
  static const char _denom_s[]="denom";
  static define_unary_function_eval (__denom,&_denom,_denom_s);
  define_unary_function_ptr5( at_denom ,alias_at_denom,&__denom,0,true);

  static gen getNum(const gen & g,GIAC_CONTEXT){
    vecteur num,den;
    prod2frac(g,num,den);
    return vecteur2prod(num);
  }
  gen _getNum(const gen & g,GIAC_CONTEXT){
    return apply(g,getNum,contextptr);
  }
  static const char _getNum_s[]="getNum";
  static define_unary_function_eval (__getNum,&_getNum,_getNum_s);
  define_unary_function_ptr5( at_getNum ,alias_at_getNum,&__getNum,0,true);

  static gen numer(const gen & g,GIAC_CONTEXT){
    gen res=_fxnd(g,contextptr);
    return res._VECTptr->front();
  }
  gen _numer(const gen & g,GIAC_CONTEXT){
    return apply(g,numer,contextptr);
  }
  static const char _numer_s[]="numer";
  static define_unary_function_eval (__numer,&_numer,_numer_s);
  define_unary_function_ptr5( at_numer ,alias_at_numer,&__numer,0,true);

  static const char _propFrac_s[]="propFrac";
  static define_unary_function_eval (__propFrac,&_propfrac,_propFrac_s);
  define_unary_function_ptr5( at_propFrac ,alias_at_propFrac,&__propFrac,0,true);

  static const char _tCollect_s[]="tCollect";
  static define_unary_function_eval (__tCollect,&_tcollect,_tCollect_s);
  define_unary_function_ptr5( at_tCollect ,alias_at_tCollect,&__tCollect,0,true);

  gen _tExpand(const gen & g,GIAC_CONTEXT){
    return _simplify(_texpand(g,contextptr),contextptr);
  }
  static const char _tExpand_s[]="tExpand";
  static define_unary_function_eval (__tExpand,&_tExpand,_tExpand_s);
  define_unary_function_ptr5( at_tExpand ,alias_at_tExpand,&__tExpand,0,true);

  gen _comDenom(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<2 )
      return ratnormal(g);
    vecteur & v(*g._VECTptr);
    return _reorder(makesequence(v.front(),vecteur(v.begin()+1,v.end())),contextptr);
  }
  static const char _comDenom_s[]="comDenom";
  static define_unary_function_eval (__comDenom,&_comDenom,_comDenom_s);
  define_unary_function_ptr5( at_comDenom ,alias_at_comDenom,&__comDenom,0,true);

  gen _randPoly(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    int vs=v.size(),deg=10;
    gen x=vx_var;
    gen f=0;
    if (vs==1){
      if (v[0].type==_INT_)
	deg=v[0].val;
      else {
	x=v[0];
	deg=10;
      }
    }
    else {
      if (vs>=2){
	if (v[0].type==_INT_){
	  deg=v[0].val;
	  if (v[1].type==_IDNT)
	    x=v[1];
	  else
	    f=v[1];
	}
	else {
	  if (v[1].type==_INT_)
	    deg=v[1].val;
	  else
	    f=v[1];
	  x=v[0];
	}
      }
      if (vs>=3){
	f=v[2];
      }
      if (vs>=4 && v[2].type==_INT_ && v[3].type==_INT_){
	vecteur w(absint(deg)+1,1);
	for (unsigned i=1;i<=(unsigned)absint(deg);++i){
	  w[i]=int(giac_rand(contextptr)/(rand_max2+1.0)*(v[3].val-v[2].val+1)+v[2].val);
	}
#ifdef GIAC_HAS_STO_38
	return w;
#else
	return symb_horner(w,x);
#endif
      }
      if (vs==4){
	if (f.type==_FUNC)
	  f=symbolic(*f._FUNCptr,v.back());
	else
	  f=symb_of(f,v.back());
      }
      if (vs>4){
	if (f.type==_FUNC)
	  f=symbolic(*f._FUNCptr,gen(vecteur(v.begin()+3,v.end()),_SEQ__VECT));
	else
	  f=symb_of(f,gen(vecteur(v.begin()+3,v.end()),_SEQ__VECT));
      }
    }
    vecteur w;
    w=vranm(absint(deg),f,contextptr);
    w.insert(w.begin(),1);
#ifdef GIAC_HAS_STO_38
    return w;
#else
    return symb_horner(w,x);
#endif
  }
  static const char _randPoly_s[]="randPoly";
  static define_unary_function_eval (__randPoly,&_randPoly,_randPoly_s);
  define_unary_function_ptr5( at_randPoly ,alias_at_randPoly,&__randPoly,0,true);

  static const char _randpoly_s[]="randpoly";
  static define_unary_function_eval (__randpoly,&_randPoly,_randpoly_s);
  define_unary_function_ptr5( at_randpoly ,alias_at_randpoly,&__randpoly,0,true);

  gen _nInt(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=4)
      return gensizeerr(contextptr);
    return evalf(symbolic(at_integrate,g),1,contextptr);
  }
  static const char _nInt_s[]="nInt";
  static define_unary_function_eval (__nInt,&_nInt,_nInt_s);
  define_unary_function_ptr5( at_nInt ,alias_at_nInt,&__nInt,0,true);

  gen _nDeriv(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<2)
      return gensizeerr(contextptr);
    gen step(0.001);
    if (v.size()>2)
      step=v[2];
    return evalf(rdiv(subst(v[0],v[1],v[1]+step,false,contextptr)-subst(v[0],v[1],v[1]-step,false,contextptr),2*step,contextptr),1,contextptr);
  }
  static const char _nDeriv_s[]="nDeriv";
  static define_unary_function_eval (__nDeriv,&_nDeriv,_nDeriv_s);
  define_unary_function_ptr5( at_nDeriv ,alias_at_nDeriv,&__nDeriv,0,true);

  gen _avgRC(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<2)
      return gensizeerr(contextptr);
    gen step(0.001);
    if (v.size()>2)
      step=v[2];
    return evalf(rdiv(subst(v[0],v[1],v[1]+step,false,contextptr)-subst(v[0],v[1],v[1],false,contextptr),step,contextptr),1,contextptr);
  }
  static const char _avgRC_s[]="avgRC";
  static define_unary_function_eval (__avgRC,&_avgRC,_avgRC_s);
  define_unary_function_ptr5( at_avgRC ,alias_at_avgRC,&__avgRC,0,true);

  gen _fMin(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()==1)
      v.push_back(vx_var);
    if (v.size()==4) // fMin(func,[constraints],[variables],[guess])
      return fmin_cobyla(v[0],gen2vecteur(v[1]),gen2vecteur(v[2]),gen2vecteur(v[3]),undef,undef,contextptr);
    if (v.size()==5) // fMin(func,[constraints],[variables],[guess],eps)
      return fmin_cobyla(v[0],gen2vecteur(v[1]),gen2vecteur(v[2]),gen2vecteur(v[3]),v[4],undef,contextptr);
    if (v.size()==6) // fMin(func,[constraints],[variables],[guess],eps,maxiter)
      return fmin_cobyla(v[0],gen2vecteur(v[1]),gen2vecteur(v[2]),gen2vecteur(v[3]),v[4],v[5],contextptr);
    gen w(fminmax(v,4,contextptr));
    if (is_undef(w)) return w;
    return solvepostprocess(w,v[1],contextptr);
  }
  static const char _fMin_s[]="fMin";
  static define_unary_function_eval (__fMin,&_fMin,_fMin_s);
  define_unary_function_ptr5( at_fMin ,alias_at_fMin,&__fMin,0,true);

  gen _fMax(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()==1)
      v.push_back(vx_var);
    if (v.size()==4) // fMax(func,[constraints],[variables],[guess])
      return fmin_cobyla(-v[0],gen2vecteur(v[1]),gen2vecteur(v[2]),gen2vecteur(v[3]),undef,undef,contextptr);
    if (v.size()==5) // fMax(func,[constraints],[variables],[guess],eps)
      return fmin_cobyla(-v[0],gen2vecteur(v[1]),gen2vecteur(v[2]),gen2vecteur(v[3]),v[4],undef,contextptr);
    if (v.size()==6) // fMax(func,[constraints],[variables],[guess],eps,maxiter)
      return fmin_cobyla(-v[0],gen2vecteur(v[1]),gen2vecteur(v[2]),gen2vecteur(v[3]),v[4],v[5],contextptr);
    gen w(fminmax(v,5,contextptr));
    if (is_undef(w)) return w;
    return solvepostprocess(w,v[1],contextptr);
  }
  static const char _fMax_s[]="fMax";
  static define_unary_function_eval (__fMax,&_fMax,_fMax_s);
  define_unary_function_ptr5( at_fMax ,alias_at_fMax,&__fMax,0,true);

  gen _taylor(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    /* if (xcas_mode(contextptr)==0)
       return _series(g); */
    vecteur v(gen2vecteur(g));
    if (v.empty())
      return gentoofewargs("Taylor needs 3 args");    
    if (v.size()<2)
      v.push_back(vx_var);
    if (v.size()<3)
      v.push_back(5);
    else is_integral(v[2]);
    gen x0;
    if (v.size()==4)
      x0=v[3];
    if (v[1].is_symb_of_sommet(at_equal))
      return _series(makesequence(v[0],v[1],v[2]),contextptr);
    return _series(makesequence(v[0],symbolic(at_equal,makesequence(v[1],x0)),v[2]),contextptr);
  }
  static const char _taylor_s[]="taylor";
  static define_unary_function_eval (__taylor,&_taylor,_taylor_s);
  define_unary_function_ptr5( at_taylor ,alias_at_taylor,&__taylor,0,true);

  gen _arcLen(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()==3)
      v.insert(v.begin()+1,ggb_var(v.front()));
    if (v.size()!=4 || v[1].type!=_IDNT)
      return gentoofewargs("arcLen");
    gen fprime=derive(v[0],v[1],contextptr);
    if (is_undef(fprime)) return fprime;
    if (fprime.type==_VECT)
      fprime=_l2norm(fprime,contextptr);
    else
      fprime=sqrt(normal(sq(fprime)+1,contextptr),contextptr);
    return _integrate(gen(makevecteur(fprime,v[1],v[2],v[3]),_SEQ__VECT),contextptr);
  }
  static const char _arcLen_s[]="arcLen";
  static define_unary_function_eval (__arcLen,&_arcLen,_arcLen_s);
  define_unary_function_ptr5( at_arcLen ,alias_at_arcLen,&__arcLen,0,true);

  gen _dim(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!ckmatrix(g))
      return _size(g,contextptr);
    vecteur res(2);
    if (!g._VECTptr->empty()){
      res[0]=int(g._VECTptr->size());
      res[1]=int(g._VECTptr->front()._VECTptr->size());
    }
    return res;
  }
  static const char _dim_s[]="dim";
  static define_unary_function_eval (__dim,&_dim,_dim_s);
  define_unary_function_ptr5( at_dim ,alias_at_dim,&__dim,0,true);

  // FIXME: the print() should have an additionnal format argument
  // that would cover normal, tex, C, and formatted output
  static string format(const gen & g,const string & forme,GIAC_CONTEXT){
    if (g.type == _ZINT){
      string txt = g.print();
      if (!forme.empty()){
	char ch=forme[0];
	if (tolower(ch) == 'f')
	  return txt;
	if (forme.size()<2)
	  return txt;
	unsigned int digits = atol(forme.substr(1,forme.size()-1).c_str()) - 1;
	if (tolower(ch) == 'e')
	  digits ++;
	digits = digits < 2 ? 2 : digits;
	if (digits + 1 < txt.size()){
	  string tmp = txt.substr(0, 1) + "." + txt.substr(1, digits) + "e+" + print_INT_(txt.size() - 1);
	  return tmp;
	}
      }  
      return txt;
    }
    else {
      gen tmp=evalf_double(g,eval_level(contextptr),contextptr);
      string saveforme=format_double(contextptr);
      format_double(contextptr)=forme;
      string s=tmp.print(contextptr);
      format_double(contextptr)=saveforme;
      return s;
    }
  }
  gen _format(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()!=2 || v[1].type!=_STRNG)
      return gensizeerr(contextptr);
    return string2gen(format(v.front(),*v[1]._STRNGptr,contextptr),false);
  }
  static const char _format_s[]="format";
  static define_unary_function_eval (__format,&_format,_format_s);
  define_unary_function_ptr5( at_format ,alias_at_format,&__format,0,true);

  gen _inString(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<2 || v[0].type!=_STRNG || v[1].type!=_STRNG)
      return gensizeerr(contextptr);
    int shift = xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    int debut=shift;
    if (v.size()>2 && v[2].type==_INT_)
      debut=v[2].val;
    return v[0]._STRNGptr->find(*v[1]._STRNGptr,debut-shift)+shift;
  }
  static const char _inString_s[]="inString";
  static define_unary_function_eval (__inString,&_inString,_inString_s);
  define_unary_function_ptr5( at_inString ,alias_at_inString,&__inString,0,true);

  gen _left(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_SYMB && g._SYMBptr->feuille.type==_VECT && !g._SYMBptr->feuille._VECTptr->empty())
      return g._SYMBptr->feuille._VECTptr->front();
    vecteur v(1,g);
    if (g.type==_VECT && g.subtype==_SEQ__VECT)
      v=*g._VECTptr;
    if (v.size()<2 || v[1].type!=_INT_)
      return g;
    if (v[0].type==_STRNG)
      return string2gen(v[0]._STRNGptr->substr(0,v[1].val),false);
    if (v[0].type==_VECT){
      const_iterateur it=v[0]._VECTptr->begin(),itend=v[0]._VECTptr->end();
      int length=giacmax(0,giacmin(itend-it,v[1].val));
      return gen(vecteur(it,it+length),v[0].subtype);
    }
    return g;
  }
  static const char _left_s[]="left";
  static define_unary_function_eval (__left,&_left,_left_s);
  define_unary_function_ptr5( at_left ,alias_at_left,&__left,0,true);

  static const char _gauche_s[]="gauche";
  static define_unary_function_eval (__gauche,&_left,_gauche_s);
  define_unary_function_ptr5( at_gauche ,alias_at_gauche,&__gauche,0,true);

  gen _right(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_SYMB && g._SYMBptr->feuille.type==_VECT && !g._SYMBptr->feuille._VECTptr->empty())
      return g._SYMBptr->feuille._VECTptr->back();
    vecteur v(1,g);
    if (g.type==_VECT && g.subtype==_SEQ__VECT)
      v=*g._VECTptr;
    if (v.size()<2 || v[1].type!=_INT_)
      return g;
    if (v[0].type==_STRNG){
      string & s=*v[0]._STRNGptr;
      int l=s.size();
      int m=giacmin(giacmax(v[1].val,0),l);
      return string2gen(s.substr(l-m,m),false);
    }
    if (v[0].type==_VECT){
      const_iterateur it=v[0]._VECTptr->begin(),itend=v[0]._VECTptr->end();
      int length=giacmax(0,giacmin(itend-it,v[1].val));
      return gen(vecteur(itend-length,itend),v[0].subtype);
    }
    return g;
  }
  static const char _right_s[]="right";
  static define_unary_function_eval (__right,&_right,_right_s);
  define_unary_function_ptr5( at_right ,alias_at_right,&__right,0,true);

  static const char _droit_s[]="droit";
  static define_unary_function_eval (__droit,&_right,_droit_s);
  define_unary_function_ptr5( at_droit ,alias_at_droit,&__droit,0,true);

  gen _mid(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(1,g);
    if (g.type==_VECT && g.subtype==_SEQ__VECT)
      v=*g._VECTptr;
    if (v.size()<2 || v[1].type!=_INT_)
      return g;
    int shift = xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    int debut=v[1].val-shift;
    int nbre=RAND_MAX;
    if (v.size()>2 && v[2].type==_INT_)
      nbre=v[2].val;
    if (v[0].type==_STRNG){
      string & s=*v[0]._STRNGptr;
      if (debut>=signed(s.size()) || debut<0)
	return string2gen("",false);
      int m=giacmin(giacmax(nbre,0),s.size());
      return string2gen(s.substr(debut,m),false);
    }
    if (v[0].type==_VECT){
      const_iterateur it=v[0]._VECTptr->begin(),itend=v[0]._VECTptr->end();
      if (debut>=itend-it || debut<0)
	return gen(vecteur(0),v[0].subtype);
      int length=giacmax(0,giacmin(itend-it-debut,nbre));
      return gen(vecteur(it+debut,it+debut+length),v[0].subtype);
    }
    return g;
  }
  static const char _mid_s[]="mid";
  static define_unary_function_eval (__mid,&_mid,_mid_s);
  define_unary_function_ptr5( at_mid ,alias_at_mid,&__mid,0,true);

  gen _ord(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT)
      return apply(g,_ord,contextptr);
    if (g.type!=_STRNG || !g._STRNGptr->size())
      return gensizeerr(contextptr);
    return int((*g._STRNGptr)[0]);
  }
  static const char _ord_s[]="ord";
  static define_unary_function_eval (__ord,&_ord,_ord_s);
  define_unary_function_ptr5( at_ord ,alias_at_ord,&__ord,0,true);

  static gen shiftrotate(const gen & g,bool right){
    bool shift=right;
    vecteur v(1,g);
    if (g.type==_VECT && g.subtype==_SEQ__VECT)
      v=*g._VECTptr;
    int nbre=-1;
    /* if (shift)
       nbre=1; */
    if (v.size()>1 && v[1].type==_INT_)
      nbre=v[1].val;
    if (nbre<0){
      nbre=-nbre;
      right=!right;
    }
    gen & a=v[0];
    if (a.type==_INT_){
      if (right)
	return a.val >> nbre;
      else
	return a.val << nbre;
    }
    if (a.type==_VECT){
      const_iterateur it=a._VECTptr->begin(),itend=a._VECTptr->end();
      nbre=giacmin(nbre,itend-it);
      if (shift){
	if (right)
	  return gen(mergevecteur(vecteur(it+nbre,itend),vecteur(nbre,undef)),a.subtype);
	return gen(mergevecteur(vecteur(nbre,undef),vecteur(it,itend-nbre)),a.subtype);
      }
      if (right)
	return gen(mergevecteur(vecteur(itend-nbre,itend),vecteur(it,itend-nbre)),a.subtype);
      return gen(mergevecteur(vecteur(it+nbre,itend),vecteur(it,it+nbre)),a.subtype);
    }
    if (a.type==_STRNG){
      string & s=*a._STRNGptr;
      int l=s.size();
      nbre=giacmin(nbre,l);
      if (shift){
	if (right)
	  return string2gen(s.substr(nbre,l-nbre)+string(nbre,' '),false);
	return string2gen(string(l-nbre,' ')+s.substr(0,nbre),false);
      }
      if (right)
	return string2gen(s.substr(l-nbre,nbre)+s.substr(0,l-nbre),false);
      return string2gen(s.substr(nbre,l-nbre)+s.substr(0,nbre),false);
    }
    return a;
  }
  gen _rotate(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return shiftrotate(g,false);
  }
  static const char _rotate_s[]="rotate";
  static define_unary_function_eval (__rotate,&_rotate,_rotate_s);
  define_unary_function_ptr5( at_rotate ,alias_at_rotate,&__rotate,0,true);

  gen _shift(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return shiftrotate(g,true);
  }
  static const char _shift_s[]="shift";
  static define_unary_function_eval (__shift,&_shift,_shift_s);
  define_unary_function_ptr5( at_shift ,alias_at_shift,&__shift,0,true);

  gen _augment(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return concat(g,false,contextptr);
  }
  static const char _augment_s[]="augment";
  static define_unary_function_eval (__augment,&_augment,_augment_s);
  define_unary_function_ptr5( at_augment ,alias_at_augment,&__augment,0,true);

  gen _semi_augment(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return concat(g,true,contextptr);
  }
  static const char _semi_augment_s[]="semi_augment";
  static define_unary_function_eval (__semi_augment,&_semi_augment,_semi_augment_s);
  define_unary_function_ptr5( at_semi_augment ,alias_at_semi_augment,&__semi_augment,0,true);

  static const char _crossP_s[]="crossP";
  static define_unary_function_eval (__crossP,&_cross,_crossP_s);
  define_unary_function_ptr5( at_crossP ,alias_at_crossP,&__crossP,0,true);

  static const char _scalarProduct_s[]="scalarProduct";
  static define_unary_function_eval (__scalarProduct,&_dotprod,_scalarProduct_s);
  define_unary_function_ptr5( at_scalarProduct ,alias_at_scalarProduct,&__scalarProduct,0,true);

  static const char _dotP_s[]="dotP";
  static define_unary_function_eval (__dotP,&_dotprod,_dotP_s);
  define_unary_function_ptr5( at_dotP ,alias_at_dotP,&__dotP,0,true);

  gen _cumSum(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    const_iterateur it=v.begin(),itend=v.end();
    if (it==itend)
      return zero;
    vecteur res;
    res.reserve(itend-it);
    gen somme(it->type==_STRNG?string2gen("",false):zero*(*it));
    for (;it!=itend;++it){
      res.push_back(somme=somme+*it);
    }
    return gen(res,g.subtype);
  }
  static const char _cumSum_s[]="cumSum";
  static define_unary_function_eval (__cumSum,&_cumSum,_cumSum_s);
  define_unary_function_ptr5( at_cumSum ,alias_at_cumSum,&__cumSum,0,true);

  static gen _rightapply(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return _right(g,contextptr);
    return apply(g,_rightapply,contextptr);
  }
  gen _exp2list(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen g1(g);
    if (!g1.is_symb_of_sommet(at_ou))
      g1=eval(g,eval_level(contextptr),contextptr);
    g1=remove_and(g1,at_ou);
    return _rightapply(g1,contextptr);
  }
  static const char _exp2list_s[]="exp2list";
  static define_unary_function_eval_quoted (__exp2list,&_exp2list,_exp2list_s);
  define_unary_function_ptr5( at_exp2list ,alias_at_exp2list,&__exp2list,_QUOTE_ARGUMENTS,true);

  gen _list2mat(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    gen taille=evalf_double(v[1],1,contextptr);
    if (g.subtype!=_SEQ__VECT || v.size()!=2 || v[0].type!=_VECT || taille.type!=_DOUBLE_)
      return vecteur(1,v);
    vecteur res;
    int nbre=giacmax(1,int(taille._DOUBLE_val));
    const_iterateur it=v[0]._VECTptr->begin(),itend=v[0]._VECTptr->end();
    for (;it!=itend;it+=nbre){
      if (itend-it<nbre){
	res.push_back(mergevecteur(vecteur(it,itend),vecteur(nbre-(itend-it))));
	break;
      }
      res.push_back(vecteur(it,it+nbre));
    }
    return res;
  }
  static const char _list2mat_s[]="list2mat";
  static define_unary_function_eval (__list2mat,&_list2mat,_list2mat_s);
  define_unary_function_ptr5( at_list2mat ,alias_at_list2mat,&__list2mat,0,true);

  gen _deltalist(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    const_iterateur it=v.begin(),itend=v.end();
    if (itend-it<2)
      return gendimerr(contextptr);
    vecteur res;
    res.reserve(itend-it-1);
    gen prec=*it;
    ++it;
    for (;it!=itend;++it){
      res.push_back(*it-prec);
      prec=*it;
    }
    return gen(res,g.subtype);
  }
  static const char _deltalist_s[]="deltalist";
  static define_unary_function_eval (__deltalist,&_deltalist,_deltalist_s);
  define_unary_function_ptr5( at_deltalist ,alias_at_deltalist,&__deltalist,0,true);

  gen _mat2list(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    if (!ckmatrix(v))
      return g;
    vecteur res;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      res=mergevecteur(res,*it->_VECTptr);
    }
    return res;
  }
  static const char _mat2list_s[]="mat2list";
  static define_unary_function_eval (__mat2list,&_mat2list,_mat2list_s);
  define_unary_function_ptr5( at_mat2list ,alias_at_mat2list,&__mat2list,0,true);

  gen _newList(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (absint(g.val) > LIST_SIZE_LIMIT)
      return genstabilityerr(contextptr);
    if (g.type!=_INT_)
      return gensizeerr(contextptr);
    return vecteur(absint(g.val));
  }
  static const char _newList_s[]="newList";
  static define_unary_function_eval (__newList,&_newList,_newList_s);
  define_unary_function_ptr5( at_newList ,alias_at_newList,&__newList,0,true);

  static gen polyEval(const gen & p,const gen & x){
    if (x.type==_VECT)
      return apply2nd(p,x,polyEval);
    return horner(p,x);
  }
  gen _polyEval(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    return polyEval(v[0],v[1]);
  }
  static const char _polyEval_s[]="polyEval";
  static define_unary_function_eval (__polyEval,&_polyEval,_polyEval_s);
  define_unary_function_ptr5( at_polyEval ,alias_at_polyEval,&__polyEval,0,true);

  gen _product(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT){
      if (g.subtype!=_SEQ__VECT)
	return prodsum(g.eval(eval_level(contextptr),contextptr),true);
      vecteur v=*g._VECTptr;
      maple_sum_product_unquote(v,contextptr);
      int s=v.size();
      if (!adjust_int_sum_arg(v,s))
	return gensizeerr(contextptr);
      if (v.size()==4 && (v[2].type!=_INT_ || v[3].type!=_INT_)){
	if (v[1].type!=_IDNT){
	  if (v[1].type!=_SYMB || lidnt(v[1]).empty())
	    return prodsum(g.eval(eval_level(contextptr),contextptr),true);
	  identificateur tmp("x");
	  v[0]=quotesubst(v[0],v[1],tmp,contextptr);
	  v[1]=tmp;
	  gen res=_product(gen(v,g.subtype),contextptr);
	  return quotesubst(res,tmp,v[1],contextptr);
	}
	v=quote_eval(v,makevecteur(v[1]),contextptr);
	gen n=v[1];
	vecteur lv(1,n);
	lvar(v[0],lv);
	if (is_zero(derive(vecteur(lv.begin()+1,lv.end()),n,contextptr),contextptr)){
	  v[0]=e2r(v[0],lv,contextptr);
	  gen p1,p2;
	  fxnd(v[0],p1,p2);
	  return simplify(product(gen2polynome(p1,lv.size()),lv,n,v[2],v[3],contextptr)/product(gen2polynome(p2,lv.size()),lv,n,v[2],v[3],contextptr),contextptr);
	}
      }
      if (v.size()==4 && v[2].type==_INT_ && v[3].type==_INT_ && v[2].val>v[3].val){
	if (v[3].val==v[2].val-1)
	  return 1;
#if defined RTOS_THREADX || defined BESTA_OS
	{ gen a=v[2]; v[2]=v[3]; v[3]=a; }
#else
	swap(v[2],v[3]);
#endif
	v[2]=v[2]+1;
	v[3]=v[3]-1;
	return inv(seqprod(gen(v,_SEQ__VECT),1,contextptr),contextptr);
      }
      return seqprod(gen(v,_SEQ__VECT),1,contextptr);
    }
    gen tmp=g.eval(eval_level(contextptr),contextptr);
    if (tmp.type==_VECT)
      return _product(tmp,contextptr);
    return seqprod(g,1,contextptr);
  }
  static const char _product_s[]="product";
  static define_unary_function_eval_quoted (__product,&_product,_product_s);
  define_unary_function_ptr5( at_product ,alias_at_product,&__product,_QUOTE_ARGUMENTS,true);

  // maple name for product
  static const char _mul_s[]="mul";
  static define_unary_function_eval (__mul,&_product,_mul_s);
  define_unary_function_ptr5( at_mul ,alias_at_mul,&__mul,0,true);

  static gen sortad(const vecteur & v,bool ascend,GIAC_CONTEXT){
    vecteur valeur=*eval(v,eval_level(contextptr),contextptr)._VECTptr;
    bool ismat=ckmatrix(valeur);
    if (!ismat)
      valeur=vecteur(1,valeur);
    valeur=mtran(valeur);
    if (ascend)
      sort(valeur.begin(),valeur.end(),first_ascend_sort);
    else
      sort(valeur.begin(),valeur.end(),first_descend_sort);
    valeur=mtran(valeur);
    if (!ismat)
      return valeur.front();
    return valeur;
  }
  gen _SortA(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT)
      return sortad(*g._VECTptr,true,contextptr);
    if (g.type!=_IDNT)
      return gensizeerr(contextptr);
    gen valeur=eval(g,eval_level(contextptr),contextptr);
    if (valeur.type!=_VECT)
      return gensizeerr(contextptr);
    return sto(sortad(*valeur._VECTptr,true,contextptr),g,contextptr);
  }
  static const char _SortA_s[]="SortA";
  static define_unary_function_eval_quoted (__SortA,&_SortA,_SortA_s);
  define_unary_function_ptr5( at_SortA ,alias_at_SortA,&__SortA,_QUOTE_ARGUMENTS,T_RETURN);

  gen _SortD(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT)
      return sortad(*g._VECTptr,false,contextptr);
    if (g.type!=_IDNT)
      return gensizeerr(contextptr);
    gen valeur=eval(g,eval_level(contextptr),contextptr);
    if (valeur.type!=_VECT)
      return gensizeerr(contextptr);
    return sto(sortad(*valeur._VECTptr,false,contextptr),g,contextptr);
  }
  static const char _SortD_s[]="SortD";
  static define_unary_function_eval_quoted (__SortD,&_SortD,_SortD_s);
  define_unary_function_ptr5( at_SortD ,alias_at_SortD,&__SortD,_QUOTE_ARGUMENTS,T_RETURN);

  static const char _approx_s[]="approx";
  static define_unary_function_eval (__approx,&_evalf,_approx_s);
  define_unary_function_ptr5( at_approx ,alias_at_approx,&__approx,0,true);

  static const char _ceiling_s[]="ceiling";
  static define_unary_function_eval (__ceiling,&_ceil,_ceiling_s);
  define_unary_function_ptr5( at_ceiling ,alias_at_ceiling,&__ceiling,0,true);

  static const char _imag_s[]="imag";
  static define_unary_function_eval (unary__imag,(const gen_op_context)im,_imag_s);
  define_unary_function_ptr5( at_imag ,alias_at_imag,&unary__imag,0,true);

  static const char _real_s[]="real";
  static define_unary_function_eval (unary__real,(const gen_op_context)re,_real_s);
  define_unary_function_ptr5( at_real ,alias_at_real,&unary__real,0,true);

  static const char _intDiv_s[]="intDiv";
  static define_unary_function_eval (__intDiv,(const gen_op_context)_iquo,_intDiv_s);
  define_unary_function_ptr5( at_intDiv ,alias_at_intDiv,&__intDiv,0,true);

  static const char _remain_s[]="remain";
  static define_unary_function_eval (__remain,(const gen_op_context)_irem,_remain_s);
  define_unary_function_ptr5( at_remain ,alias_at_remain,&__remain,0,true);

  gen _int(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (xcas_mode(contextptr)==3)
      return _floor(g,contextptr);
    else
      return _integrate(g,contextptr);
  }
  static const char _int_s[]="int";
  static define_unary_function_eval (__int,(const gen_op_context)_int,_int_s);
  define_unary_function_ptr5( at_int ,alias_at_int,&__int,0,true);

  static const char _isPrime_s[]="isPrime";
  static define_unary_function_eval (__isPrime,&_isprime,_isPrime_s);
  define_unary_function_ptr5( at_isPrime ,alias_at_isPrime,&__isPrime,0,true);

  static const char _nCr_s[]="nCr";
  static define_unary_function_eval (__nCr,&_comb,_nCr_s);
  define_unary_function_ptr5( at_nCr ,alias_at_nCr,&__nCr,0,true);

  static const char _nPr_s[]="nPr";
  static define_unary_function_eval (__nPr,&_perm,_nPr_s);
  define_unary_function_ptr5( at_nPr ,alias_at_nPr,&__nPr,0,true);

  gen _iPart(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return evalf(_floor(g,contextptr),eval_level(contextptr),contextptr);
  }
  static const char _iPart_s[]="iPart";
  static define_unary_function_eval (__iPart,&_iPart,_iPart_s);
  define_unary_function_ptr5( at_iPart ,alias_at_iPart,&__iPart,0,true);

  gen _Fill(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()!=2 || v[1].type!=_IDNT)
      return gensizeerr(contextptr);
    gen value(eval(v[1],eval_level(contextptr),contextptr));
    if (value.type!=_VECT)
      return sto(v[0],v[1],contextptr);
    gen dim=_dim(value,contextptr);
    if (dim.type==_INT_)
      return sto(vecteur(dim.val,eval(v[0],eval_level(contextptr),contextptr)),v[1],contextptr);
    if (dim.type==_VECT){
      vecteur & w=*dim._VECTptr;
      if (w.size()!=2 || w.front().type!=_INT_ || w.back().type!=_INT_)
	return gensizeerr(contextptr);
      return sto(vecteur(w[0].val,vecteur(w[1].val,eval(v[0],eval_level(contextptr),contextptr))),v[1],contextptr);
    }
    return gensizeerr(contextptr);
  }
  static const char _Fill_s[]="Fill";
  static define_unary_function_eval_quoted (__Fill,&_Fill,_Fill_s);
  define_unary_function_ptr5( at_Fill ,alias_at_Fill,&__Fill,_QUOTE_ARGUMENTS,T_RETURN);

  gen _mRow(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (!v.empty() && v[1].type==_IDNT){
      gen v1=v[1];
      gen args=eval(g,eval_level(contextptr),contextptr);
      if (ckmatrix(args[1]))
	return sto(_mRow(args,contextptr),v1,contextptr);
    }
    if (v.size()!=3 || !ckmatrix(v[1]) || v[2].type!=_INT_ )
      return gentypeerr(contextptr);
    int s=v[1]._VECTptr->size();
    int shift = xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    int l=v[2].val-shift;
    if (l<0 || l>=s)
      return gendimerr(contextptr);
    vecteur w=*v[1]._VECTptr;
    w[l]=v[0]*w[l];
    return w;
  }
  static const char _mRow_s[]="mRow";
  static define_unary_function_eval (__mRow,&_mRow,_mRow_s);
  define_unary_function_ptr5( at_mRow ,alias_at_mRow,&__mRow,0,true);

  gen _mRowAdd(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (!v.empty() && v[1].type==_IDNT){
      gen v1=v[1];
      gen args=eval(g,eval_level(contextptr),contextptr);
      if (ckmatrix(args[1]))
	return sto(_mRowAdd(args,contextptr),v1,contextptr);
    }
    if (v.size()!=4)
      return gentypeerr(contextptr);
    v[3]=_floor(v[3],contextptr);
    v[2]=_floor(v[2],contextptr);
    if (!ckmatrix(v[1]) || v[2].type!=_INT_ || v[3].type!=_INT_)
      return gentypeerr(contextptr);
    int s=v[1]._VECTptr->size();
    int shift = xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    int l1=v[2].val-shift,l2=v[3].val-shift;
    if (l1<0 || l1>=s || l2<0 || l2>=s)
      return gendimerr(contextptr);
    vecteur w=*v[1]._VECTptr;
    w[l2]=v[0]*w[l1]+w[l2];
    return w;
  }
  static const char _mRowAdd_s[]="mRowAdd";
  static define_unary_function_eval (__mRowAdd,&_mRowAdd,_mRowAdd_s);
  define_unary_function_ptr5( at_mRowAdd ,alias_at_mRowAdd,&__mRowAdd,0,true);

  gen _rowAdd(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (!v.empty() && v[0].type==_IDNT){
      gen v0=v[0];
      gen args=eval(g,eval_level(contextptr),contextptr);
      if (ckmatrix(args))
	return sto(_rowAdd(args,contextptr),v0,contextptr);
    }
    if (v.size()!=3)
      return gentypeerr(contextptr);
    v[1]=_floor(v[1],contextptr);
    v[2]=_floor(v[2],contextptr);
    if (!ckmatrix(v[0]) || v[1].type!=_INT_ || v[2].type!=_INT_)
      return gentypeerr(contextptr);
    int s=v[0]._VECTptr->size();
    int shift = xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    int l1=v[1].val-shift,l2=v[2].val-shift;
    if (l1<0 || l1>=s || l2<0 || l2>=s)
      return gendimerr(contextptr);
    vecteur w=*v[0]._VECTptr;
    w[l2]=w[l1]+w[l2];
    return w;
  }
  static const char _rowAdd_s[]="rowAdd";
  static define_unary_function_eval (__rowAdd,&_rowAdd,_rowAdd_s);
  define_unary_function_ptr5( at_rowAdd ,alias_at_rowAdd,&__rowAdd,0,true);

  gen _rowSwap(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (!v.empty() && v[0].type==_IDNT){
      gen v0=v[0];
      gen args=eval(g,eval_level(contextptr),contextptr);
      if (ckmatrix(args[0]))
	return sto(_rowSwap(args,contextptr),v0,contextptr);
    }
    if (v.size()!=3)
      return gentypeerr(contextptr);
    v[1]=_floor(v[1],contextptr);
    v[2]=_floor(v[2],contextptr);
    if (!ckmatrix(v[0]) || v[1].type!=_INT_ || v[2].type!=_INT_)
      return gentypeerr(contextptr);
    int s=v[0]._VECTptr->size();
    int shift = xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    int l1=v[1].val-shift,l2=v[2].val-shift;
    if (l1<0 || l1>=s || l2<0 || l2>=s)
      return gendimerr(contextptr);
    vecteur w=*v[0]._VECTptr;
    swapgen(w[l2],w[l1]);
    return w;
  }
  static const char _rowSwap_s[]="rowSwap";
  static define_unary_function_eval (__rowSwap,&_rowSwap,_rowSwap_s);
  define_unary_function_ptr5( at_rowSwap ,alias_at_rowSwap,&__rowSwap,0,true);

  gen _LU(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (abs_calc_mode(contextptr)==38){
      gen args=evalf(g,eval_level(contextptr),contextptr);
      gen res=lu(args,contextptr);
      if (res.type==_VECT && res._VECTptr->size()==3){
	vecteur & v = *res._VECTptr;
	gen tmpv=_permu2mat(v[0],contextptr);
	if (is_undef(tmpv))
	  return tmpv;
	return gen(makevecteur(v[1],v[2],tmpv),_LIST__VECT);
      }
      return res;
    }
    vecteur v(gen2vecteur(g));
    gen res;
    if (v.size()==5)
      v.pop_back();
    if (v.size()!=4 || !is_squarematrix(res=eval(v[0],eval_level(contextptr),contextptr)) || v[1].type!=_IDNT || v[2].type!=_IDNT || v[3].type!=_IDNT)
      return gentypeerr(contextptr);
    res=lu(res,contextptr); // P,L,U
    if (is_undef(res) || res.type!=_VECT || res.subtype!=_SEQ__VECT || res._VECTptr->size()!=3)
      return res;
    gen tmpsto=sto(res[1],v[1],contextptr);
    if (is_undef(tmpsto)) return tmpsto;
    tmpsto=sto(res[2],v[2],contextptr);
    if (is_undef(tmpsto)) return tmpsto;
    res=sto(_permu2mat(res[0],contextptr),v[3],contextptr);
    return res;
  }
  static const char _LU_s[]="LU";
  static define_unary_function_eval_quoted (__LU,&_LU,_LU_s);
  define_unary_function_ptr5( at_LU ,alias_at_LU,&__LU,_QUOTE_ARGUMENTS,true);

  gen _QR(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (abs_calc_mode(contextptr)==38){
      gen args=evalf(g,eval_level(contextptr),contextptr);
      gen res=qr(makevecteur(args,-1),contextptr);
      if (res.type==_VECT)
	res.subtype=_SEQ__VECT;
      return res;
    }
    vecteur v(gen2vecteur(g));
    if (v.size()==4)
      v.pop_back();
    gen res;
    if (v.size()!=3 || !is_squarematrix(res=eval(v[0],eval_level(contextptr),contextptr)) || v[1].type!=_IDNT || v[2].type!=_IDNT )
      return gentypeerr(contextptr);
    gen res1=qr(res,contextptr);
    if (is_undef(res1) || !ckmatrix(res1))
      return res;
    gen tmpsto=sto(res*inv(res1,contextptr),v[1],contextptr);
    if (is_undef(tmpsto)) return tmpsto;
    return sto(res1,v[2],contextptr);
  }
  static const char _QR_s[]="QR";
  static define_unary_function_eval_quoted (__QR,&_QR,_QR_s);
  define_unary_function_ptr5( at_QR ,alias_at_QR,&__QR,_QUOTE_ARGUMENTS,true);

  gen _newMat(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    vecteur & v=*g._VECTptr;
    if (v[0].type!=_INT_ || v[1].type!=_INT_ )
      return gensizeerr(contextptr);
    int l=absint(v[0].val),c=absint(v[1].val);
    if (l > LIST_SIZE_LIMIT || c > LIST_SIZE_LIMIT || longlong(l) * c > LIST_SIZE_LIMIT)
      return gendimerr(contextptr);
    return vecteur(l,vecteur(c));
  }
  static const char _newMat_s[]="newMat";
  static define_unary_function_eval (__newMat,&_newMat,_newMat_s);
  define_unary_function_ptr5( at_newMat ,alias_at_newMat,&__newMat,0,true);

  gen _ref(const gen & a,GIAC_CONTEXT) {
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (!ckmatrix(a))
      return gensizeerr(contextptr);
    gen det;
    vecteur pivots;
    matrice res;
    mrref(*a._VECTptr,res,pivots,det,0,a._VECTptr->size(),0,a._VECTptr->front()._VECTptr->size(),
	  /* fullreduction */0,0,true,1,0,
	  contextptr);
    bool reducelast = a._VECTptr->size()!=a._VECTptr->front()._VECTptr->size()-1;
    mdividebypivot(res,reducelast?-1:-2);
    return res;
  }
  static const char _ref_s[]="ref";
  static define_unary_function_eval (__ref,&giac::_ref,_ref_s);
  define_unary_function_ptr5( at_ref ,alias_at_ref,&__ref,0,true);

  vecteur gen2vecteur(const gen & g,int exclude){
    if (g.type!=_VECT || g.subtype==exclude)
      return vecteur(1,g);
    return *g._VECTptr;
  }
  gen _subMat(const gen & g,GIAC_CONTEXT) {
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g,0));
    if (v.empty() || !ckmatrix(v[0]) )
      return gentypeerr(contextptr);
    int lignedeb=1,colonnedeb=1,lignefin=1,colonnefin=1;
    mdims(*v[0]._VECTptr,lignefin,colonnefin);
    int shift = xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    if (v.size()>2 && v[1].is_symb_of_sommet(at_interval)){
      gen & f = v[1]._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2 && f._VECTptr->front().type==_INT_ && f._VECTptr->back().type==_INT_){
	lignedeb=giacmax(1,f._VECTptr->front().val+!shift);
	lignefin=giacmax(1,f._VECTptr->back().val+!shift);
      }
      if (v[2].is_symb_of_sommet(at_interval)){
	gen & f = v[2]._SYMBptr->feuille;
	if (f.type==_VECT && f._VECTptr->size()==2 && f._VECTptr->front().type==_INT_ && f._VECTptr->back().type==_INT_){
	  colonnedeb=giacmax(1,f._VECTptr->front().val+!shift);
	  colonnefin=giacmax(1,f._VECTptr->back().val+!shift);
	}
      }
    }
    if (v.size()>2 && v[1].type==_VECT && v[1]._VECTptr->size()==2 && v[2].type==_VECT && v[2].type==_VECT){
      lignedeb=giacmax(1,v[1]._VECTptr->front().val+!shift);
      colonnedeb=giacmax(1,v[1]._VECTptr->back().val+!shift);
      lignefin=giacmax(1,v[2]._VECTptr->front().val+!shift);
      colonnefin=giacmax(1,v[2]._VECTptr->back().val+!shift);
    }
    if (v.size()>1 && v[1].type==_INT_)
      lignedeb=giacmax(1,v[1].val+!shift);
    if (v.size()>2 && v[2].type==_INT_)
      colonnedeb=giacmax(1,v[2].val+!shift);
    if (v.size()>3 && v[3].type==_INT_)
      lignefin=giacmax(giacmin(lignefin,v[3].val+!shift),lignedeb);
    if (v.size()>4 && v[4].type==_INT_)
      colonnefin=giacmax(colonnedeb,giacmin(colonnefin,v[4].val+!shift));
    return matrice_extract(*v[0]._VECTptr,lignedeb-1,colonnedeb-1,lignefin-lignedeb+1,colonnefin-colonnedeb+1);
  }
  static const char _subMat_s[]="subMat";
  static define_unary_function_eval (__subMat,&giac::_subMat,_subMat_s);
  define_unary_function_ptr5( at_subMat ,alias_at_subMat,&__subMat,0,true);

  static const char _submatrix_s[]="submatrix";
  static define_unary_function_eval (__submatrix,&giac::_subMat,_submatrix_s);
  define_unary_function_ptr5( at_submatrix ,alias_at_submatrix,&__submatrix,0,true);

  gen _unitV(const gen & g,GIAC_CONTEXT) {
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return rdiv(g,_l2norm(g,contextptr),contextptr);
  }
  static const char _unitV_s[]="unitV";
  static define_unary_function_eval (__unitV,&giac::_unitV,_unitV_s);
  define_unary_function_ptr5( at_unitV ,alias_at_unitV,&__unitV,0,true);

  gen L1norm(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT)
      return abs(g,contextptr);
    vecteur & v=*g._VECTptr;
    gen res;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      res=res+abs(*it,contextptr);
    return res;
  }

  gen _rowNorm(const gen & g,GIAC_CONTEXT) {
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!ckmatrix(g))
      return gentypeerr(contextptr);
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    gen res;
    for (;it!=itend;++it){
      res=max(res,L1norm(*it,contextptr),contextptr);
    }
    return res;
  }
  static const char _rowNorm_s[]="rowNorm";
  static define_unary_function_eval (__rowNorm,&giac::_rowNorm,_rowNorm_s);
  define_unary_function_ptr5( at_rowNorm ,alias_at_rowNorm,&__rowNorm,0,true);

  static const char _rownorm_s[]="rownorm";
  static define_unary_function_eval (__rownorm,&giac::_rowNorm,_rownorm_s);
  define_unary_function_ptr5( at_rownorm ,alias_at_rownorm,&__rownorm,0,true);

  gen _colNorm(const gen & g,GIAC_CONTEXT) {
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!ckmatrix(g))
      return gentypeerr(contextptr);
    return _rowNorm(mtran(*g._VECTptr),contextptr);
  }
  static const char _colNorm_s[]="colNorm";
  static define_unary_function_eval (__colNorm,&giac::_colNorm,_colNorm_s);
  define_unary_function_ptr5( at_colNorm ,alias_at_colNorm,&__colNorm,0,true);

  static const char _colnorm_s[]="colnorm";
  static define_unary_function_eval (__colnorm,&giac::_colNorm,_colnorm_s);
  define_unary_function_ptr5( at_colnorm ,alias_at_colnorm,&__colnorm,0,true);

  gen _ClrIO(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return __interactive.op(symbolic(at_ClrIO,0),contextptr);
  }
  static const char _ClrIO_s[]="ClrIO";
  static define_unary_function_eval2 (__ClrIO,&_ClrIO,_ClrIO_s,&printastifunction);
  define_unary_function_ptr5( at_ClrIO ,alias_at_ClrIO,&__ClrIO,0,T_EXPRESSION);

  gen _Output(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()!=3 || v[0].type!=_INT_ || v[1].type!=_INT_ )
      return gentypeerr(contextptr);
    return __interactive.op(g,contextptr);
  }
  static const char _Output_s[]="Output";
  static define_unary_function_eval2 (__Output,&_Output,_Output_s,&printastifunction);
  define_unary_function_ptr5( at_Output ,alias_at_Output,&__Output,0,T_RETURN);

  gen _getKey(const gen & g,GIAC_CONTEXT){
    if (interactive_op_tab && interactive_op_tab[4])
      return interactive_op_tab[4](g,contextptr);
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    char ch;
    cerr << "Waiting for a keystroke in konsole screen" << endl;
    cin >> ch;
    return int(ch);
  }
  static const char _getKey_s[]="getKey";
#if defined RTOS_THREADX || defined BESTA_OS
  static define_unary_function_eval(__getKey,&_getKey,_getKey_s);
#else
  unary_function_eval __getKey(0,&_getKey,_getKey_s);
#endif
  define_unary_function_ptr5( at_getKey ,alias_at_getKey,&__getKey,0,true);

  gen _CopyVar(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()!=2 || v[0].type!=_IDNT || v[1].type!=_IDNT )
      return gentypeerr(contextptr);
    return sto(v[0].eval(1,contextptr),v[1],contextptr);
  }
  static const char _CopyVar_s[]="CopyVar";
  static define_unary_function_eval2_quoted (__CopyVar,&_CopyVar,_CopyVar_s,&printastifunction);
  define_unary_function_ptr5( at_CopyVar ,alias_at_CopyVar,&__CopyVar,_QUOTE_ARGUMENTS,T_RETURN);

  gen _DelFold(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen res=_SetFold(0,contextptr);
    gen e=_purge(g,contextptr);
    if (is_undef(e)){
      _SetFold(res,contextptr);
      return e;
    }
    return res;
  }
  static const char _DelFold_s[]="DelFold";
  static define_unary_function_eval2_quoted (__DelFold,&_DelFold,_DelFold_s,&printastifunction);
  define_unary_function_ptr5( at_DelFold ,alias_at_DelFold,&__DelFold,_QUOTE_ARGUMENTS,T_RETURN);

  gen _DispG(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return __interactive.op(symbolic(at_DispG,0),contextptr);
  }
  static const char _DispG_s[]="DispG";
  static define_unary_function_eval2 (__DispG,&_DispG,_DispG_s,&printastifunction);
  define_unary_function_ptr5( at_DispG ,alias_at_DispG,&__DispG,0,T_EXPRESSION);

  gen _DispHome(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return __interactive.op(symbolic(at_DispHome,0),contextptr);
  }
  static const char _DispHome_s[]="DispHome";
  static define_unary_function_eval2 (__DispHome,&_DispHome,_DispHome_s,&printastifunction);
  define_unary_function_ptr5( at_DispHome ,alias_at_DispHome,&__DispHome,0,T_EXPRESSION);

  static const char _entry_s[]="entry";
  static define_unary_function_eval (__entry,&_quest,_entry_s);
  define_unary_function_ptr5( at_entry ,alias_at_entry,&__entry,0,true);

  gen _Exec(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return string2gen("TI instruction not supported",false);
  }
  static const char _Exec_s[]="Exec";
  static define_unary_function_eval2 (__Exec,&_Exec,_Exec_s,&printastifunction);
  define_unary_function_ptr5( at_Exec ,alias_at_Exec,&__Exec,0,T_RETURN);

  static const char _Get_s[]="Get";
  static define_unary_function_eval2_quoted (__Get,&_Exec,_Get_s,&printastifunction);
  define_unary_function_ptr5( at_Get ,alias_at_Get,&__Get,_QUOTE_ARGUMENTS,T_RETURN);

  static const char _GetCalc_s[]="GetCalc";
  static define_unary_function_eval2_quoted (__GetCalc,&_Exec,_GetCalc_s,&printastifunction);
  define_unary_function_ptr5( at_GetCalc ,alias_at_GetCalc,&__GetCalc,_QUOTE_ARGUMENTS,T_RETURN);

  gen _NewFold(const gen & g,GIAC_CONTEXT){ 
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_IDNT)
      return gensizeerr(contextptr);
    _SetFold(0,contextptr);
    gen tmpsto=sto(gen(vecteur(1,vecteur(0)),_FOLDER__VECT),g,contextptr);
    if (is_undef(tmpsto)) return tmpsto;
    return _SetFold(g,contextptr);
  }
  static const char _NewFold_s[]="NewFold";
  static define_unary_function_eval2_quoted (__NewFold,&_NewFold,_NewFold_s,&printastifunction);
  define_unary_function_ptr5( at_NewFold ,alias_at_NewFold,&__NewFold,_QUOTE_ARGUMENTS,T_RETURN);

  gen _GetFold(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return getfold(current_folder_name);
  }
  static const char _GetFold_s[]="GetFold";
  static define_unary_function_eval2 (__GetFold,&_GetFold,_GetFold_s,&printastifunction);
  define_unary_function_ptr5( at_GetFold ,alias_at_GetFold,&__GetFold,0,T_EXPRESSION);

  gen _StoPic(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_IDNT)
      return gensizeerr(contextptr);
    return sto(__interactive.op(0,contextptr),g,contextptr);
  }
  static const char _StoPic_s[]="StoPic";
  static define_unary_function_eval2_quoted (__StoPic,&_StoPic,_StoPic_s,&printastifunction);
  define_unary_function_ptr5( at_StoPic ,alias_at_StoPic,&__StoPic,_QUOTE_ARGUMENTS,T_RETURN);

  gen _RclPic(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_IDNT)
      return gensizeerr(contextptr);
    gen tmp=eval(g,eval_level(contextptr),contextptr);
    if (tmp.type!=_VECT)
      return gensizeerr(contextptr);
    return __interactive.op(symbolic(at_RclPic,tmp),contextptr);
  }
  static const char _RclPic_s[]="RclPic";
  static define_unary_function_eval2_quoted (__RclPic,&_RclPic,_RclPic_s,&printastifunction);
  define_unary_function_ptr5( at_RclPic ,alias_at_RclPic,&__RclPic,_QUOTE_ARGUMENTS,T_RETURN);

  gen _RplcPic(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_IDNT)
      return gensizeerr(contextptr);
    gen tmp=eval(g,eval_level(contextptr),contextptr);
    if (tmp.type!=_VECT)
      return gensizeerr(contextptr);
    return __interactive.op(symbolic(at_RplcPic,tmp),contextptr);
  }
  static const char _RplcPic_s[]="RplcPic";
  static define_unary_function_eval2_quoted (__RplcPic,&_RplcPic,_RplcPic_s,&printastifunction);
  define_unary_function_ptr5( at_RplcPic ,alias_at_RplcPic,&__RplcPic,_QUOTE_ARGUMENTS,T_RETURN);

  gen _ClrGraph(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return __interactive.op(symbolic(at_erase,0),contextptr);
  }
  static const char _ClrGraph_s[]="ClrGraph";
  static define_unary_function_eval2 (__ClrGraph,&_ClrGraph,_ClrGraph_s,&printastifunction);
  define_unary_function_ptr5( at_ClrGraph ,alias_at_ClrGraph,&__ClrGraph,0,T_EXPRESSION);

  gen _ClrDraw(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return __interactive.op(symbolic(at_erase,0),contextptr);
  }
  static const char _ClrDraw_s[]="ClrDraw";
  static define_unary_function_eval2 (__ClrDraw,&_ClrDraw,_ClrDraw_s,&printastifunction);
  define_unary_function_ptr5( at_ClrDraw ,alias_at_ClrDraw,&__ClrDraw,0,T_EXPRESSION);

  gen _PtOn(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return _point(g,contextptr);
  }
  static const char _PtOn_s[]="PtOn";
  static define_unary_function_eval2 (__PtOn,&_PtOn,_PtOn_s,&printastifunction);
  define_unary_function_ptr5( at_PtOn ,alias_at_PtOn,&__PtOn,0,T_RETURN);

  gen _PtOff(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen tmp=_point(g,contextptr);
    if (tmp.type==_SYMB && tmp._SYMBptr->sommet==at_pnt)
      return symb_pnt(tmp[0],int(FL_WHITE),contextptr);
    else
      return tmp;
  }
  static const char _PtOff_s[]="PtOff";
  static define_unary_function_eval2 (__PtOff,&_PtOff,_PtOff_s,&printastifunction);
  define_unary_function_ptr5( at_PtOff ,alias_at_PtOff,&__PtOff,0,T_RETURN);

  static const char _PxlOn_s[]="PxlOn";
  static define_unary_function_eval2 (__PxlOn,&_pixon,_PxlOn_s,&printastifunction);
  define_unary_function_ptr5( at_PxlOn ,alias_at_PxlOn,&__PxlOn,0,T_RETURN);

  static const char _PxlOff_s[]="PxlOff";
  static define_unary_function_eval2 (__PxlOff,&_pixoff,_PxlOff_s,&printastifunction);
  define_unary_function_ptr5( at_PxlOff ,alias_at_PxlOff,&__PxlOff,0,T_RETURN);

  gen _Line(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<4)
      return gensizeerr(contextptr);
    int couleur=FL_BLACK;
    if (v.size()==5 && v[4].val==0)
      couleur=FL_WHITE;
    return _couleur(makesequence(_segment(makesequence(v[0]+cst_i*v[1],v[2]+cst_i*v[3]),contextptr),couleur),contextptr);
  }
  static const char _Line_s[]="Line";
  static define_unary_function_eval2 (__Line,&_Line,_Line_s,&printastifunction);
  define_unary_function_ptr5( at_Line ,alias_at_Line,&__Line,0,T_RETURN);

  gen _LineHorz(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<1)
      return gensizeerr(contextptr);
    int couleur=FL_BLACK;
    if (v.size()==2 && v[1].val==0)
      couleur=FL_WHITE;
    return _couleur(makesequence(_droite(makesequence(cst_i*v[0],1+cst_i*v[0]),contextptr),couleur),contextptr);
  }
  static const char _LineHorz_s[]="LineHorz";
  static define_unary_function_eval2 (__LineHorz,&_LineHorz,_LineHorz_s,&printastifunction);
  define_unary_function_ptr5( at_LineHorz ,alias_at_LineHorz,&__LineHorz,0,T_RETURN);

  gen _LineVert(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<1)
      return gensizeerr(contextptr);
    int couleur=FL_BLACK;
    if (v.size()==2 && v[1].val==0)
      couleur=FL_WHITE;
    return _couleur(makesequence(_droite(makesequence(v[0],cst_i+v[0]),contextptr),couleur),contextptr);
  }
  static const char _LineVert_s[]="LineVert";
  static define_unary_function_eval2 (__LineVert,&_LineVert,_LineVert_s,&printastifunction);
  define_unary_function_ptr5( at_LineVert ,alias_at_LineVert,&__LineVert,0,T_RETURN);

  gen _DrawSlp(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<3)
      return gensizeerr(contextptr);
    gen pt(v[0]+cst_i*v[1]);
    return _droite(makesequence(pt,pt+1+cst_i*v[2]),contextptr);
  }
  static const char _DrawSlp_s[]="DrawSlp";
  static define_unary_function_eval2 (__DrawSlp,&_DrawSlp,_DrawSlp_s,&printastifunction);
  define_unary_function_ptr5( at_DrawSlp ,alias_at_DrawSlp,&__DrawSlp,0,T_RETURN);

  gen _Circle(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<3)
      return gensizeerr(contextptr);
    int couleur=FL_BLACK;
    if (v.size()==4 && v[3].val==0)
      couleur=FL_WHITE;
    gen centre(v[0]+cst_i*v[1]);
    return _couleur(makesequence(_cercle(makesequence(centre,v[2]),contextptr),couleur),contextptr);
  }
  static const char _Circle_s[]="Circle";
  static define_unary_function_eval2 (__Circle,&_Circle,_Circle_s,&printastifunction);
  define_unary_function_ptr5( at_Circle ,alias_at_Circle,&__Circle,0,T_RETURN);

  gen _PtText(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<3)
      return gensizeerr(contextptr);
    gen tmp(v[1]+cst_i*v[2]);
    return _legende(makesequence(tmp,v[0]),contextptr);
  }
  static const char _PtText_s[]="PtText";
  static define_unary_function_eval2 (__PtText,&_PtText,_PtText_s,&printastifunction);
  define_unary_function_ptr5( at_PtText ,alias_at_PtText,&__PtText,0,T_RETURN);

  gen _NewPic(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<2|| !ckmatrix(v[0]) || v[1].type!=_IDNT)
      return gensizeerr(contextptr);
    vecteur w=*v[0]._VECTptr;
    iterateur it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      if (it->_VECTptr->size()!=2)
	return gensizeerr(contextptr);
      *it=symbolic(at_pnt,makevecteur(it->_VECTptr->front()+cst_i*it->_VECTptr->back(),int(FL_BLACK)),_PNT__VECT);
    }
    return sto(gen(w,_SEQ__VECT),v[1],contextptr);
  }
  static const char _NewPic_s[]="NewPic";
  static define_unary_function_eval2 (__NewPic,&_NewPic,_NewPic_s,&printastifunction);
  define_unary_function_ptr5( at_NewPic ,alias_at_NewPic,&__NewPic,0,T_RETURN);

  vecteur zoom_save;
  gen _ZoomSto(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v;
    v.push_back(gnuplot_xmin);
    v.push_back(gnuplot_xmax);
    v.push_back(gnuplot_ymin);
    v.push_back(gnuplot_ymax);
    v.push_back(gnuplot_zmin);
    v.push_back(gnuplot_zmax);
    v.push_back(gnuplot_tmin);
    v.push_back(gnuplot_tmax);
    v.push_back(global_window_xmin);
    v.push_back(global_window_xmax);
    v.push_back(global_window_ymin);
    v.push_back(global_window_ymax);
    v.push_back(show_axes(contextptr));
    zoom_save=v;
    return v;
  }
  static const char _ZoomSto_s[]="ZoomSto";
  static define_unary_function_eval2 (__ZoomSto,&_ZoomSto,_ZoomSto_s,&printastifunction);
  define_unary_function_ptr5( at_ZoomSto ,alias_at_ZoomSto,&__ZoomSto,0,T_RETURN);

  gen _ZoomRcl(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v;
    if (g.type!=_VECT || g._VECTptr->size()<13)
      v=zoom_save;
    else
      v=*g._VECTptr;
    return _xyztrange(v,contextptr);
  }
  static const char _ZoomRcl_s[]="ZoomRcl";
  static define_unary_function_eval2 (__ZoomRcl,&_ZoomRcl,_ZoomRcl_s,&printastifunction);
  define_unary_function_ptr5( at_ZoomRcl ,alias_at_ZoomRcl,&__ZoomRcl,0,T_RETURN);

  gen _deSolve(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.empty())
      return gensizeerr(contextptr);
    if (v[0].is_symb_of_sommet(at_and))
      v[0]=remove_and(v[0],at_and);
    return _desolve(gen(v,_SEQ__VECT),contextptr);
  }
  static const char _deSolve_s[]="deSolve";
  static define_unary_function_eval2_quoted (__deSolve,&_deSolve,_deSolve_s,&printastifunction);
  define_unary_function_ptr5( at_deSolve ,alias_at_deSolve,&__deSolve,_QUOTE_ARGUMENTS,T_RETURN);

  gen _LineTan(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(g));
    int s=read_attributs(v,attributs,contextptr);
    if (s<1 || s>3)
      return gensizeerr(contextptr);
    gen f(v[0]),x(vx_var),x0(0);
    if (s==3){
      x=v[1];
      x0=v[2];
    }
    if (s==2){
      x0=v[1];
      if (x0.is_symb_of_sommet(at_equal)){
	gen & x0f=x0._SYMBptr->feuille;
	if (x0f.type==_VECT && x0f._VECTptr->size()==2){
	  x=x0f._VECTptr->front();
	  x0=x0f._VECTptr->back();
	}
      }
    }
    gen fprime(derive(f,x,contextptr));
    if (is_undef(fprime)) return fprime;
    gen M0(x0+cst_i*subst(f,x,x0,false,contextptr));
    gen direction(1+cst_i*subst(fprime,x,x0,false,contextptr));
    return put_attributs(_droite(makesequence(M0,M0+direction),contextptr),attributs,contextptr);
  }
  static const char _LineTan_s[]="LineTan";
  static define_unary_function_eval2 (__LineTan,&_LineTan,_LineTan_s,&printastifunction);
  define_unary_function_ptr5( at_LineTan ,alias_at_LineTan,&__LineTan,0,T_RETURN);

  static const char _droite_tangente_s[]="droite_tangente";
  static define_unary_function_eval2 (__droite_tangente,&_LineTan,_droite_tangente_s,&printastifunction);
  define_unary_function_ptr5( at_droite_tangente ,alias_at_droite_tangente,&__droite_tangente,0,true);

  static const char _tangente_s[]="tangente";
  static define_unary_function_eval2 (__tangente,&_tangent,_tangente_s,&printastifunction);
  define_unary_function_ptr5( at_tangente ,alias_at_tangente,&__tangente,0,true);

  gen _CyclePic(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    int s=v.size();
    if (s<2 || v[0].type!=_STRNG || v[1].type!=_INT_)
      return gensizeerr(contextptr);
    int n=giacmax(absint(v[1].val),1);
    double delay=1.0;
    if (s>2 && v[2].type==_DOUBLE_)
      delay=v[2]._DOUBLE_val;
    delay=std::abs(delay*1e3);
    int d=int(delay);
    int ds=d/1000,ns=(d%1000)*1000000;
#ifndef HAVE_NO_SYS_TIMES_H
    timespec t; // ,tr;
    t.tv_sec=ds;
    t.tv_nsec=ns;
#endif
    int repete=1;
    if (s>3 && v[3].type==_INT_)
      repete=giacmax(absint(v[3].val),1);
    int direction=1;
    if (s>4 && is_minus_one(v[4]))
      direction=-1;
    string & orig_name = *v[0]._STRNGptr;
    for (int i=0;i<repete;++i){
      if (direction==1){
	for (int j=1;j<=n;++j){
	  string name=orig_name+print_INT_(j);
	  gen g(name,contextptr);
	  if (g.type==_IDNT)
	    _RplcPic(g,contextptr);
	  usleep(d*1000);
	  /*
	    #ifdef WIN32
	    sleep(ds);
	    #else
	    #ifdef __APPLE__
	    usleep(2000);
	    #else
	    nanosleep(&t,&tr);
	    #endif
	    #endif
	  */
	}
      }
      else {
	for (int j=n;j>=1;--j){
	  string name=orig_name+print_INT_(j);
	  gen g(name,contextptr);
	  if (g.type==_IDNT)
	    _RplcPic(g,contextptr);
	  usleep(d*1000);
	  /*
	    #ifdef WIN32
	    sleep(ds);
	    #else
	    #ifdef __APPLE__
	    usleep(2000);
	    #else
	    nanosleep(&t,&tr);
	    #endif
	    #endif
	  */
	}
      }
    }
    return zero;
  }
  static const char _CyclePic_s[]="CyclePic";
  static define_unary_function_eval2 (__CyclePic,&_CyclePic,_CyclePic_s,&printastifunction);
  define_unary_function_ptr5( at_CyclePic ,alias_at_CyclePic,&__CyclePic,0,T_RETURN);

  gen _RandSeed(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
#if defined(VISUALC) || defined(__MINGW_H) || defined BESTA_OS || defined EMCC
    srand(g.val);
#else
#ifndef GNUWINCE
    srandom(g.val);
#endif
#endif // visualc
    return g;
  }
  static const char _RandSeed_s[]="RandSeed";
  static define_unary_function_eval2 (__RandSeed,&_RandSeed,_RandSeed_s,&printastifunction);
  define_unary_function_ptr5( at_RandSeed ,alias_at_RandSeed,&__RandSeed,0,T_RETURN);

  gen _Store(const gen & g,const context * contextptr){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return _sto(g,contextptr);
  }
  static const char _Store_s[]="Store";
  static define_unary_function_eval_quoted (__Store,&_Store,_Store_s);
  define_unary_function_ptr5( at_Store ,alias_at_Store,&__Store,_QUOTE_ARGUMENTS,true);

  gen exact_double(double d,double eps){
    if (eps<1e-14)
      eps=1e-14;
    if (d<0)
      return -exact_double(-d,eps);
    if (d > (1<<30) )
      return _floor(d,context0);
    vector<int> res;
    double eps1(1+eps);
    for (;!interrupted;){
      if (ctrl_c) { 
	interrupted = true; ctrl_c=false;
	return gensizeerr(gettext("Stopped by user interruption.")); 
      }
      res.push_back(int(d*eps1));
      d=d-int(d*eps1);
      if (d<=eps)
	break;
      d=1/d;
      if (d > (1<<30))
	break;
      eps=eps*d*d;
    }
    reverse(res.begin(),res.end());
    vector<int>::const_iterator it=res.begin(),itend=res.end();
    gen x(*it);
    for (++it;it!=itend;++it){
      x=*it+inv(x,context0);
    }
    return x;
  }
  gen exact(const gen & g,GIAC_CONTEXT){
    switch (g.type){
    case _DOUBLE_:
      return exact_double(g._DOUBLE_val,epsilon(contextptr));
#ifdef BCD
    case _FLOAT_:
      return exact_double(evalf_double(g,1,contextptr)._DOUBLE_val,1e-10);
#endif
    case _CPLX:
      return exact(re(g,contextptr),contextptr)+cst_i*exact(im(g,contextptr),contextptr);
    case _SYMB:
      return symbolic(g._SYMBptr->sommet,exact(g._SYMBptr->feuille,contextptr));
    case _VECT:
      return apply(g,exact,contextptr);
    default:
      return g;
    }
  }
  gen _exact(const gen & g,GIAC_CONTEXT){
#if 0 // def BCD
    return symb_quote(exact(g,contextptr));
#else
    return exact(g,contextptr);
#endif
  }
  static const char _exact_s[]="exact";
  static define_unary_function_eval (__exact,&_exact,_exact_s);
  define_unary_function_ptr5( at_exact ,alias_at_exact,&__exact,0,true);

  gen fPart(const gen & g,GIAC_CONTEXT){
    if (is_equal(g))
      return apply_to_equal(g,fPart,contextptr);
    if (is_strictly_positive(-g,contextptr))
      return -fPart(-g,contextptr);
    return g-_floor(g,contextptr);
  }
  static const char _fPart_s[]="fPart";
  static define_unary_function_eval (__fPart,&fPart,_fPart_s);
  define_unary_function_ptr5( at_fPart ,alias_at_fPart,&__fPart,0,true);

  static const char _frac_s[]="frac";
  static define_unary_function_eval (__frac,&fPart,_frac_s);
  define_unary_function_ptr5( at_frac ,alias_at_frac,&__frac,0,true);

  gen simult(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    gen m1(v[0]),m2(v[1]);
    if (!is_squarematrix(m1)|| !ckmatrix(m2) )
      return gensizeerr(contextptr);
    matrice m=mtran(mergevecteur(mtran(*m1._VECTptr),mtran(*m2._VECTptr)));
    m=mrref(m,contextptr);
    mdividebypivot(m);
    int n,c;
    mdims(m,n,c);
    return matrice_extract(m,0,n,n,c-n);
  }
  static const char _simult_s[]="simult";
  static define_unary_function_eval (__simult,&simult,_simult_s);
  define_unary_function_ptr5( at_simult ,alias_at_simult,&__simult,0,true);

  /* TI89 compatibility Notes
     Use xcas_mode(contextptr)(3) in your config file (~/.xcasrc or xcas.rc) or
     begin your session with cas configuration (or type xcas_mode(contextptr)(3))

     1/ Instructions implemented
     Algebra: all
     Calculus: all except product
     Strings: all
     Graphics: pixel instructions are not implemented except PxlOn/PxlOff
     And/Xor/Pixel tests are not implemented
     Zoom instructions not implemented except ZoomSto/ZoomRcl
     BldData/RclGDB/StoGDB not implemented
     Graph currently points to DrawFunc
     Lists: all
     Maths: all except conversions and units
     Matrices: all except statistics. 
     LU/QR work with numeric matrices and tolerance argument is ignored
     Program: all except 
     mode handling, units, 
     getKeys, the Custom and Toolbar instructions
     Lock/Unlock
     Stats: implemented !, rand, RandSeed, nCr, nPr, median, mean, stddev

     2/ Be sure to use correct case. archive (native) is not like Archive (TI)
     Conversion of special char (ASCII code >128)
     conversion sign translated to to
     Store sign: =>
     Different: != or <>, Greater or equal: >=, Lower or equal: <=
     i=sqrt(-1): use the i button of xcas
     transpose sign not translated (use transpose())
  */

  // FIXME SECURITY
  // archive is made of couples name/value
  static sym_tab read_ti_archive(const string & s,GIAC_CONTEXT){
    ifstream inf(s.c_str());
    vecteur v;
    readargs_from_stream(inf,v,contextptr);
    sym_tab res;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type!=_STRNG)
	continue;
      string name=*it->_STRNGptr;
      ++it;
      if (it==itend)
	break;
      gen value=*it;
      res[name]=value;
    }
    return res;
  }
  
  static void print_ti_archive(const string & s,const sym_tab & m){
    if (is_undef(check_secure()))
      return ;
    ofstream of(s.c_str());
    sym_tab::const_iterator it=m.begin(),itend=m.end();
    if (it==itend)
      of << "[ ]" << endl;
    of << "[" << string2gen(it->first,false) ;
    of << "," << it->second ;
    ++it;
    for (;it!=itend;++it){
      of << "," << endl ;
      of << string2gen(it->first,false) ;
      of << "," << it->second ;
    }
    of << "]" << endl;
  }
  gen _Archive(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    sym_tab arc(read_ti_archive("archive",contextptr));
    if (g.type==_IDNT)
      arc[g.print(contextptr)]=eval(g,eval_level(contextptr),contextptr);
    else {
      if (g.type!=_VECT)
	return gensizeerr(contextptr);
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	if (it->type==_IDNT)
	  arc[it->print(contextptr)]=eval(*it,eval_level(contextptr),contextptr);
      }
    }
    print_ti_archive("archive",arc);
    return 1;
  }
  static const char _Archive_s[]="Archive";
  static define_unary_function_eval2_quoted (__Archive,&_Archive,_Archive_s,&printastifunction);
  define_unary_function_ptr5( at_Archive ,alias_at_Archive,&__Archive,_QUOTE_ARGUMENTS,T_RETURN);

  gen _Unarchiv(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    sym_tab arc(read_ti_archive("archive",contextptr));
    gen tmpsto;
    if (g.type==_IDNT)
      return sto(arc[g.print(contextptr)],g,contextptr);
    else {
      if (g.type!=_VECT)
	return gensizeerr(contextptr);
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	if (it->type==_IDNT){
	  sto(arc[it->print(contextptr)],*it,contextptr);
	  if (is_undef(tmpsto)) return tmpsto;	
	}
      }
    }
    return 1;
  }
  static const char _Unarchiv_s[]="Unarchiv";
  static define_unary_function_eval2_quoted (__Unarchiv,&_Unarchiv,_Unarchiv_s,&printastifunction);
  define_unary_function_ptr5( at_Unarchiv ,alias_at_Unarchiv,&__Unarchiv,_QUOTE_ARGUMENTS,T_RETURN);

  static const char _ti_then_s[]="ti_then";
  static define_unary_function_eval (__ti_then,0,_ti_then_s); // never evaled
  define_unary_function_ptr( at_ti_then ,alias_at_ti_then ,&__ti_then);

  static const char _ti_elseif_s[]="ti_elseif";
  static define_unary_function_eval (__ti_elseif,0,_ti_elseif_s); // never evaled
  define_unary_function_ptr( at_ti_elseif ,alias_at_ti_elseif ,&__ti_elseif);

  static const char _ti_else_try_s[]="ti_else_try";
  static define_unary_function_eval (__ti_else_try,0,_ti_else_try_s); // never evaled
  define_unary_function_ptr( at_ti_else_try ,alias_at_ti_else_try ,&__ti_else_try);

  static const char _ti_else_s[]="ti_else";
  static define_unary_function_eval (__ti_else,0,_ti_else_s); // never evaled
  define_unary_function_ptr( at_ti_else ,alias_at_ti_else ,&__ti_else);

  static gen ti_stop(const gen & g,GIAC_CONTEXT){
    return gensizeerr(gettext("TI stop"));
  }
  static const char _ti_stop_s[]="ti_stop";
  static define_unary_function_eval (__ti_stop,&ti_stop,_ti_stop_s); // never evaled
  define_unary_function_ptr( at_ti_stop ,alias_at_ti_stop ,&__ti_stop);


  // French structure keywords
  static string printassialorssinon(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr)==3)
      return printasifte(feuille,sommetstr,contextptr);
    int l=language(contextptr);
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=3) )
      return localize("sialorssinon",l)+"("+feuille.print(contextptr)+')';
    const_iterateur it=feuille._VECTptr->begin(); // ,itend=feuille._VECTptr->end();
    string res(localize("si",l)+' ');
    res += sametoequal(*it).print(contextptr);
    ++it;
    res += ' ';
    res += localize("alors",l);
    res += ' ';
    debug_ptr(contextptr)->indent_spaces +=2;
    if ((it->type==_SYMB) && (it->_SYMBptr->sommet==at_bloc))
      res += printasinnerbloc(it->_SYMBptr->feuille,contextptr);
    else
      res += it->print(contextptr) ;
    debug_ptr(contextptr)->indent_spaces -=2;
    res += ' ';
    res += localize("sinon",l);
    res += ' ';
    ++it;
    debug_ptr(contextptr)->indent_spaces +=2;
    if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_bloc))
      res += printasinnerbloc(it->_SYMBptr->feuille,contextptr);
    else
      res += it->print(contextptr) ;
    debug_ptr(contextptr)->indent_spaces -=2;
    res += indent(contextptr)+ localize("fsi",l);
    return res;
  }

  gen _sialorssinon(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return _ifte(g,contextptr);
  }
  static const char _sialorssinon_s[]="sialorssinon";
  static define_unary_function_eval2_quoted (__sialorssinon,&_ifte,_sialorssinon_s,&printassialorssinon);
  define_unary_function_ptr5( at_sialorssinon ,alias_at_sialorssinon,&__sialorssinon,_QUOTE_ARGUMENTS,T_IFTE);

  static const char _si_s[]="si";
  static define_unary_function_eval2_quoted (__si,&_ifte,_si_s,&printassialorssinon);
  define_unary_function_ptr5( at_si ,alias_at_si,&__si,_QUOTE_ARGUMENTS,T_IF);

  static const char _alors_s[]="alors";
  static define_unary_function_eval_quoted (__alors,&_ifte,_alors_s);
  define_unary_function_ptr5( at_alors ,alias_at_alors,&__alors,_QUOTE_ARGUMENTS,T_THEN);
  
  static const char _sinon_s[]="sinon";
  static define_unary_function_eval_quoted (__sinon,&_ifte,_sinon_s);
  define_unary_function_ptr5( at_sinon ,alias_at_sinon,&__sinon,_QUOTE_ARGUMENTS,T_ELSE);
  
  static string printaspour(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr)==3)
      return printasfor(feuille,sommetstr,contextptr);
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=4) )
      return sommetstr+('('+feuille.print(contextptr)+')');
    int l=language(contextptr);
    const_iterateur it=feuille._VECTptr->begin(); //,itend=feuille._VECTptr->end();
    string res;
    if (is_zero(*it,contextptr) && is_zero(*(it+2),contextptr)){
      ++it;
      res += localize("tantque",l);
      res += ' ';
      res += sametoequal(*it).print(contextptr) + ' ';
      res += localize("faire",l);
      res += ' ';
      ++it;
      ++it;
      debug_ptr(contextptr)->indent_spaces += 2;
      if ((it->type==_SYMB) && (it->_SYMBptr->sommet==at_bloc))
	res += printasinnerbloc(it->_SYMBptr->feuille,contextptr);
      else
	res += it->print(contextptr) ;
      debug_ptr(contextptr)->indent_spaces -= 2;
      return res+indent(contextptr)+' '+localize("ftantque",l)+';';
    }
    else {  
      if ( (it->type!=_SYMB) || (it->_SYMBptr->sommet!=at_sto) || ((it+2)->type!=_SYMB) || ((it+2)->_SYMBptr->sommet!=at_sto) || (it->_SYMBptr->feuille._VECTptr->back()!=(it+2)->_SYMBptr->feuille._VECTptr->back()) ){
	res=localize("pour",l)+" (";
	res += it->print(contextptr) + ';';
	++it;
	res += it->print(contextptr) + ';';
	++it;
	res += it->print(contextptr) + ") ";
	++it;
	debug_ptr(contextptr)->indent_spaces += 2;
	res += it->print(contextptr) ;
	debug_ptr(contextptr)->indent_spaces -= 2;
      }
      else {
	gen var_name=it->_SYMBptr->feuille._VECTptr->back();
	gen step=normal((it+2)->_SYMBptr->feuille._VECTptr->front()-var_name,contextptr);
	gen condition=*(it+1),limite;
	bool simple_loop=false,strict=true,ascending=true;
	if (condition.type==_SYMB){
	  const unary_function_ptr op=condition._SYMBptr->sommet;
	  if (condition._SYMBptr->feuille.type==_VECT){
	    if (op==at_inferieur_strict)
	      simple_loop=true;
	    if (op==at_inferieur_egal){
	      strict=false;
	      simple_loop=true;
	    }
	    if (op==at_superieur_strict){
	      simple_loop=(xcas_mode(contextptr)==2);
	      ascending=false;
	    }
	    if (op==at_superieur_egal){
	      simple_loop=(xcas_mode(contextptr)==2);
	      ascending=false;
	      strict=false;
	    }
	  }
	  if (simple_loop){
	    simple_loop=(condition._SYMBptr->feuille._VECTptr->front()==var_name);
	    limite=condition._SYMBptr->feuille._VECTptr->back();
	  }
	}
	res =localize("pour",l)+' ';
	res += var_name.print(contextptr);
	res += ' '+localize("de",l)+' ';
	res += it->_SYMBptr->feuille._VECTptr->front().print(contextptr);
	if (simple_loop){
	  step = abs(step,contextptr);
	  if (ascending)
	    res += ' '+localize("jusque",l)+' ';
	  else
	    res += ' '+localize("downto",l)+' ';
	  res += limite.print(contextptr);
	  if (strict){
	    if (ascending)
	      res +="+";
	    else
	      res += "-";
	    res += step.print(contextptr);
	    res += "/2";
	  }
	}
	if (!is_one(step)){
	  res += ' '+localize("by",l)+' ';
	  res += step.print(contextptr);
	}
	if (!simple_loop){
	  res += ' '+localize("tantque",l)+' ';
	  res += (it+1)->print(contextptr);
	}
	res += ' '+localize("faire",l)+' ';
	it += 3;
	if ((it->type==_SYMB) && (it->_SYMBptr->sommet==at_bloc))
	  res += printasinnerbloc(it->_SYMBptr->feuille,contextptr);
	else
	  res += it->print(contextptr) ;
	return res + indent(contextptr)+' '+localize("fpour",l)+';';
      }
    }
    if (res[res.size()-1]!='}')
      res += "; ";
    return res;
  }
  gen _pour(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return _for(g,contextptr);
  }
  static const char _pour_s[]="pour";
  static define_unary_function_eval2_quoted (__pour,&_for,_pour_s,&printaspour);
  define_unary_function_ptr5( at_pour ,alias_at_pour,&__pour,_QUOTE_ARGUMENTS,T_FOR);

  static const char _de_s[]="de";
  static define_unary_function_eval_quoted (__de,&_for,_de_s);
  define_unary_function_ptr5( at_de ,alias_at_de,&__de,_QUOTE_ARGUMENTS,T_FROM);

  static const char _faire_s[]="faire";
  static define_unary_function_eval_quoted (__faire,&_for,_faire_s);
  define_unary_function_ptr5( at_faire ,alias_at_faire,&__faire,_QUOTE_ARGUMENTS,T_DO);

  static const char _pas_s[]="pas";
  static define_unary_function_eval_quoted (__pas,&_for,_pas_s);
  define_unary_function_ptr5( at_pas ,alias_at_pas,&__pas,_QUOTE_ARGUMENTS,T_BY);

  static const char _jusque_s[]="jusque";
  static define_unary_function_eval_quoted (__jusque,&_for,_jusque_s);
  define_unary_function_ptr5( at_jusque ,alias_at_jusque,&__jusque,_QUOTE_ARGUMENTS,T_TO);

  static const char _tantque_s[]="tantque";
  static define_unary_function_eval2_quoted (__tantque,&_for,_tantque_s,&printaspour);
  define_unary_function_ptr5( at_tantque ,alias_at_tantque,&__tantque,_QUOTE_ARGUMENTS,T_MUPMAP_WHILE);

  static const char _et_s[]="et";
  static define_unary_function_eval_quoted (__et,&_and,_et_s);
  define_unary_function_ptr5( at_et ,alias_at_et,&__et,_QUOTE_ARGUMENTS,T_AND_OP);

  static const char _oufr_s[]="ou";
  static define_unary_function_eval_quoted (__oufr,&_ou,_oufr_s);
  define_unary_function_ptr5( at_oufr ,alias_at_oufr,&__oufr,_QUOTE_ARGUMENTS,T_AND_OP);

  static const char _non_s[]="non";
  static define_unary_function_eval (__non,&_not,_non_s);
  define_unary_function_ptr5( at_non ,alias_at_non,&__non,0,T_NOT);

  /*
  static const char _resultat_s[]="resultat";
  static define_unary_function_eval_quoted (__resultat,&_nop,_resultat_s);
  define_unary_function_ptr5( at_resultat ,alias_at_resultat,&__resultat,_QUOTE_ARGUMENTS,T_RETURN);
  */

  static const char _fonction_s[]="fonction";
  static define_unary_function_eval_quoted (__fonction,&_for,_fonction_s);
  define_unary_function_ptr5( at_fonction ,alias_at_fonction,&__fonction,_QUOTE_ARGUMENTS,T_PROC);

#if defined RTOS_THREADX || defined BESTA_OS || defined EMCC || defined __MINGW_H

  gen _unarchive_ti(const gen & g,GIAC_CONTEXT){
    return undef;
  }

#else

  static gen ti_decode_unsigned(octet * & ptr,GIAC_CONTEXT){
    short l=(*ptr);
    --ptr;
    // Horner
    gen res;
    for (short i=0;i<l;++i,--ptr)
      res=256*res+int(*ptr);
    return res;
  }

  static double ti_decode_double(octet * & ptr,GIAC_CONTEXT){
    ptr -= 8;
    octet c1=*ptr,c2=*(ptr+1);
    unsigned int c=c1*256+c2;
    int mantissa=c-4*16*16*16-13;
    octet * q=ptr+2;
    --ptr;
    double d=0;
    for (int i=0;i<7;++i,++q){
      // now read quartet by quartet in BCD
      c1=(*q) & 0xF0;
      c1=c1 >> 4;
      d=d*10+c1;
      c2=(*q) & 0x0F;
      d=d*10+c2;      
    }
    // multiply by 10^-mantissa
    d=d*std::pow(10.0,double(mantissa));
    return d;
  }

  static gen ti_decode_fraction(octet * & ptr,GIAC_CONTEXT){
    --ptr;
    gen num=ti_decode_unsigned(ptr,contextptr);
    gen den=ti_decode_unsigned(ptr,contextptr);
    return fraction(num,den);
  }

  gen make_symbol(const string & s,GIAC_CONTEXT){
    gen res;
    if (find_or_make_symbol(s,res,0,false,contextptr)==T_SYMBOL)
      return res;
    else
      return make_symbol("_"+s,contextptr);
  }

  static gen ti_decode_identificateur(octet * &ptr,GIAC_CONTEXT){
    string s;
    octet c;
    for (;(c=*ptr)!=VAR_NAME_TAG;--ptr){
      if (c>0x7f){
	s="Z"+print_INT_(-char(c))+s;
      }
      else
	s = (char) c+s;
    }
    --ptr;
    int pos=s.find('\\');
    int l=s.size();
    gen res;
    if (pos>0 && pos+1<l){
      string s1=s.substr(0,pos);
      string s2=s.substr(pos+1,l-pos-1);
      res=symb_double_deux_points(makevecteur(make_symbol(s1,contextptr),make_symbol(s2,contextptr)));
    }
    else
      res=make_symbol(s,contextptr);
    return res;
  }

  // Also used for variable arguments functions
  static vecteur ti_decode_list(octet * & ptr,octet end,GIAC_CONTEXT){
    vecteur res;
    for (;(*ptr)!=end;)
      res.push_back(ti_decode_tag(ptr,contextptr));
    --ptr;
    return res;
  }

  // true if it's a newline or nextexp
  static bool ti_decode_newline(octet * & ptr,GIAC_CONTEXT){
    if (*ptr==NEWLINE_TAG || *ptr==NEXTEXPR_TAG){
      --ptr;
      if (!*ptr)
	--ptr;
      ti_decode_newline(ptr,contextptr);
      return true;
    }
    return false;
  }
  static vecteur ti_decode_list(octet * & ptr,octet end1,octet end2,GIAC_CONTEXT){
    vecteur res;
    for (;;){
      ti_decode_newline(ptr,contextptr);
      if (*(ptr-1)==end1 && *ptr==end2)
	break;
      res.push_back(ti_decode_tag(ptr,contextptr));
    }
    ptr -=2 ;
    return res;
  }

  static string ti_decode_string(octet * & ptr,GIAC_CONTEXT){
    string s;
    char c;
    for (; (c=(*ptr)) ;--ptr){
      switch (c){
      case '\\':
	s="::"+s;
      default:
	s=c+s;
      }
    }
    --ptr;
    return s;
  }

  static gen ti_decode_function(octet * & ptr,GIAC_CONTEXT){
    octet flag1=*ptr;
    if (!(flag1 &0x08)){
      ptr -=3;
      vecteur param;
      // decode parameter list
      for (;*ptr!=END_TAG;){ // END_TAG==0xE5
	param.push_back(ti_decode_tag(ptr,contextptr));
      }
      --ptr;
      // now either it's a function like x->sin(x)
      // or a bloc function/program with local var
      // in the second case we will have FUNC_ITAG 0x17 or PRGM_ITAG 0x19 0xE4
      // if local var are there we will in addition have LOCAL_ITAG 0xE4 etc.
      vecteur prog;
      for (;*ptr!=END_OF_SEGMENT;)
	prog.push_back(ti_decode_tag(ptr,contextptr));
      --ptr;
      if (prog.size()==1)
	return symb_program(gen(param,_SEQ__VECT),param*zero,prog.front(),contextptr);
      else
	return symb_program(gen(param,_SEQ__VECT),param*zero,prog,contextptr);
    }
    return gensizeerr(gettext("Can't find length of non tokenized programs"));
  }
  static gen ti_decode_sysvar_tag(octet * & ptr,GIAC_CONTEXT){
    gen res;
    switch (*ptr){
    case X_BAR_TAG:
      res=make_symbol("xmean",contextptr);
      break; case Y_BAR_TAG:
      res=make_symbol("ymean",contextptr);
      break; case SIGMA_X_TAG:
      res=make_symbol("Sx",contextptr);
      break; case SIGMA_X2_TAG:
      res=make_symbol("Sx2",contextptr);
      break; case SIGMA_Y_TAG:
      res=make_symbol("Sy",contextptr);
      break; case SIGMA_Y2_TAG:
      res=make_symbol("Sy2",contextptr);
      break; case SIGMA_XY_TAG:
      res=make_symbol("Sxy",contextptr);
      break; case SX_TAG:
      res=make_symbol("Sx",contextptr);
      break; case SY_TAG:
      res=make_symbol("Sy",contextptr);
      break; case SMLSIGMA_X_TAG:
      res=make_symbol("sx",contextptr);
      break; case SMLSIGMA_Y_TAG:
      res=make_symbol("sy",contextptr);
      break; case NSTAT_TAG:
      res=make_symbol("nStat",contextptr);
      break; case MINX_TAG:
      res=make_symbol("minX",contextptr);
      break; case MINY_TAG:
      res=make_symbol("minY",contextptr);
      break; case Q1_TAG:
      res=make_symbol("q1",contextptr);
      break; case MEDSTAT_TAG:
      res=make_symbol("medStat",contextptr);
      break; case Q3_TAG:
      res=make_symbol("q3",contextptr);
      break; case MAXX_TAG:
      res=make_symbol("maxX",contextptr);
      break; case MAXY_TAG:
      res=make_symbol("maxY",contextptr);
      break; case CORR_TAG:
      res=make_symbol("corr",contextptr);
      break; case R2_TAG:
      res=make_symbol("R2",contextptr);
      break; case MEDX1_TAG:
      res=make_symbol("mdex1",contextptr);
      break; case MEDX2_TAG:
      res=make_symbol("medx2",contextptr);
      break; case MEDX3_TAG:
      res=make_symbol("medx3",contextptr);
      break; case MEDY1_TAG:
      res=make_symbol("medy1",contextptr);
      break; case MEDY2_TAG:
      res=make_symbol("medy2",contextptr);
      break; case MEDY3_TAG:
      res=make_symbol("medy3",contextptr);
      break; case XC_TAG:
      res=make_symbol("xc",contextptr);
      break; case YC_TAG:
      res=make_symbol("yc",contextptr);
      break; case ZC_TAG:
      res=make_symbol("zc",contextptr);
      break; case TC_TAG:
      res=make_symbol("tc",contextptr);
      break; case RC_TAG:
      res=make_symbol("rc",contextptr);
      break; case THETA_C_TAG:
      res=make_symbol("qc",contextptr);
      break; case NC_TAG:
      res=make_symbol("nc",contextptr);
      break; case XFACT_TAG:
      res=make_symbol("xfact",contextptr);
      break; case YFACT_TAG:
      res=make_symbol("yfact",contextptr);
      break; case ZFACT_TAG:
      res=make_symbol("zfact",contextptr);
      break; case XMIN_TAG:
      res=make_symbol("xmin",contextptr);
      break; case XMAX_TAG:
      res=make_symbol("xmax",contextptr);
      break; case XSCL_TAG:
      res=make_symbol("xscl",contextptr);
      break; case YMIN_TAG:
      res=make_symbol("ymin",contextptr);
      break; case YMAX_TAG:
      res=make_symbol("ymax",contextptr);
      break; case YSCL_TAG:
      res=make_symbol("yscl",contextptr);
      break; case DELTA_X_TAG:
      res=make_symbol("Dx",contextptr);
      break; case DELTA_Y_TAG:
      res=make_symbol("Dy",contextptr);
      break; case XRES_TAG:
      res=make_symbol("xres",contextptr);
      break; case XGRID_TAG:
      res=make_symbol("xgrid",contextptr);
      break; case YGRID_TAG:
      res=make_symbol("ygrid",contextptr);
      break; case ZMIN_TAG:
      res=make_symbol("zmin",contextptr);
      break; case ZMAX_TAG:
      res=make_symbol("zmax",contextptr);
      break; case ZSCL_TAG:
      res=make_symbol("zscl",contextptr);
      break; case EYE_THETA_TAG:
      res=make_symbol("eyeq",contextptr);
      break; case EYE_PHI_TAG:
      res=make_symbol("eyeF",contextptr);
      break; case THETA_MIN_TAG:
      res=make_symbol("qmin",contextptr);
      break; case THETA_MAX_TAG:
      res=make_symbol("qmax",contextptr);
      break; case THETA_STEP_TAG:
      res=make_symbol("qstep",contextptr);
      break; case TMIN_TAG:
      res=make_symbol("tmin",contextptr);
      break; case TMAX_TAG:
      res=make_symbol("tmax",contextptr);
      break; case TSTEP_TAG:
      res=make_symbol("tstep",contextptr);
      break; case NMIN_TAG:
      res=make_symbol("nmin",contextptr);
      break; case NMAX_TAG:
      res=make_symbol("nmax",contextptr);
      break; case PLOTSTRT_TAG:
      res=make_symbol("plotStrt",contextptr);
      break; case PLOTSTEP_TAG:
      res=make_symbol("plotStep",contextptr);
      break; case ZXMIN_TAG:
      res=make_symbol("zxmin",contextptr);
      break; case ZXMAX_TAG:
      res=make_symbol("zxmax",contextptr);
      break; case ZXSCL_TAG:
      res=make_symbol("zxscl",contextptr);
      break; case ZYMIN_TAG:
      res=make_symbol("zymin",contextptr);
      break; case ZYMAX_TAG:
      res=make_symbol("zymax",contextptr);
      break; case ZYSCL_TAG:
      res=make_symbol("zyscl",contextptr);
      break; case ZXRES_TAG:
      res=make_symbol("zxres",contextptr);
      break; case Z_THETA_MIN_TAG:
      res=make_symbol("zqmin",contextptr);
      break; case Z_THETA_MAX_TAG:
      res=make_symbol("zqmax",contextptr);
      break; case Z_THETA_STEP_TAG:
      res=make_symbol("zqstep",contextptr);
      break; case ZTMIN_TAG:
      res=make_symbol("ztmin",contextptr);
      break; case ZTMAX_TAG:
      res=make_symbol("ztmax",contextptr);
      break; case ZTSTEP_TAG:
      res=make_symbol("ztsep",contextptr);
      break; case ZXGRID_TAG:
      res=make_symbol("zxgrid",contextptr);
      break; case ZYGRID_TAG:
      res=make_symbol("zygrid",contextptr);
      break; case ZZMIN_TAG:
      res=make_symbol("zzmin",contextptr);
      break; case ZZMAX_TAG:
      res=make_symbol("zzmax",contextptr);
      break; case ZZSCL_TAG:
      res=make_symbol("zzscl",contextptr);
      break; case ZEYE_THETA_TAG:
      res=make_symbol("zeyeq",contextptr);
      break; case ZEYE_PHI_TAG:
      res=make_symbol("zeyeF",contextptr);
      break; case ZNMIN_TAG:
      res=make_symbol("znmin",contextptr);
      break; case ZNMAX_TAG:
      res=make_symbol("znmax",contextptr);
      break; case ZPLTSTEP_TAG:
      res=make_symbol("zpltstep",contextptr);
      break; case ZPLTSTRT_TAG:
      res=make_symbol("zpltstrt",contextptr);
      break; case SEED1_TAG:
      res=make_symbol("seed1",contextptr);
      break; case SEED2_TAG:
      res=make_symbol("seed2",contextptr);
      break; case OK_TAG:
      res=make_symbol("ok",contextptr);
      break; case ERRORNUM_TAG:
      res=make_symbol("errornum",contextptr);
      break; case SYSMATH_TAG:
      res=make_symbol("sysMath",contextptr);
      break; case SYSDATA_TAG:
      res=make_symbol("sysData",contextptr);
      break; case REGEQ_TAG:
      res=make_symbol("regEq",contextptr);
      break; case REGCOEF_TAG:
      res=make_symbol("regCoef",contextptr);
      break; case TBLINPUT_TAG:
      res=make_symbol("tblInput",contextptr);
      break; case TBLSTART_TAG:
      res=make_symbol("tblStart",contextptr);
      break; case DELTA_TBL_TAG:
      res=make_symbol("Dtbl",contextptr);
      break; case FLDPIC_TAG:
      res=make_symbol("fldpic",contextptr);
      break; case EYE_PSI_TAG:
      res=make_symbol("eyeY",contextptr);
      break; case TPLOT_TAG:
      res=make_symbol("tplot",contextptr);
      break; case DIFTOL_TAG:
      res=make_symbol("diftol",contextptr);
      break; case ZEYE_PSI_TAG:
      res=make_symbol("zeyeY",contextptr);
      break; case T0_TAG:
      res=make_symbol("t0",contextptr);
      break; case DTIME_TAG:
      res=make_symbol("dtime",contextptr);
      break; case NCURVES_TAG:
      res=make_symbol("ncurves",contextptr);
      break; case FLDRES_TAG:
      res=make_symbol("fldres",contextptr);
      break; case ESTEP_TAG:
      res=make_symbol("Estep",contextptr);
      break; case ZT0DE_TAG:
      res=make_symbol("zt0de",contextptr);
      break; case ZTMAXDE_TAG:
      res=make_symbol("ztmaxde",contextptr);
      break; case ZTSTEPDE_TAG:
      res=make_symbol("ztstepde",contextptr);
      break; case ZTPLOTDE_TAG:
      res=make_symbol("ztplotde",contextptr);
      break; case NCONTOUR_TAG:
      res=make_symbol("ncontout",contextptr);
    default:
      return gensizeerr(gettext("Unknown sysvar tag")+print_INT_(*ptr));
    }
    --ptr;
    return res;
  }

  static gen ti_decode_unary(octet * & ptr,const unary_function_ptr * u,GIAC_CONTEXT){
    --ptr;
    gen g=ti_decode_tag(ptr,contextptr);
    return symbolic(u,g);
  }

  static gen ti_decode_binary(octet * & ptr,const unary_function_ptr * u,bool normal_order=true,GIAC_CONTEXT=0){
    --ptr;
    gen g1=ti_decode_tag(ptr,contextptr);
    gen g2=ti_decode_tag(ptr,contextptr);
    if (normal_order)
      return symbolic(u,makesequence(g1,g2));
    else
      return symbolic(u,makesequence(g2,g1));
  }

  static gen ti_decode_nary(octet * & ptr,const unary_function_ptr * u,int n,GIAC_CONTEXT){
    --ptr;
    vecteur arg;
    for (;*ptr!=END_TAG;)
      arg.push_back(ti_decode_tag(ptr,contextptr));
    --ptr;
    return symbolic(u,gen(arg,_SEQ__VECT));
  }

  static gen ti_decode_arb_real(octet * & ptr,bool real,GIAC_CONTEXT){
    unsigned int i=*ptr;
    --ptr;
    string s;
    gen res;
    if (real)
      s="at_";
    else
      s="at_n";
    s += print_INT_(i);
    find_or_make_symbol(s,res,0,false,contextptr);
    return res;
  }

  static gen ti_decode_not_implemented(octet * & ptr,const string & s,int i,GIAC_CONTEXT){
    gen gs(string2gen(s,false));
    gen g;
    if (i==1)
      g=ti_decode_unary(ptr,at_ti_not_implemented,contextptr);
    else {
      if (i==2)
	g=ti_decode_binary(ptr,at_ti_not_implemented,true,contextptr); 
      else
	g=ti_decode_nary(ptr,at_ti_not_implemented,-i,contextptr);
    }
    gen gf=g._SYMBptr->feuille;
    if (gf.type==_VECT)
      gf.subtype=_SEQ__VECT;
    return symbolic(at_ti_not_implemented,makesequence(gs,gf));
  }

  // secondary or extension tag
  static gen ti_secondary_tag(octet * & ptr,GIAC_CONTEXT){
    switch (*ptr){
    case INDIR_TAG:
      return ti_decode_unary(ptr,at_hash,contextptr);
    case GETKEY_TAG:
      return ti_decode_not_implemented(ptr,"getKey",-0,contextptr);
      // --ptr;
      // return symbolic(at_getKey,vecteur(0));
    case GETFOLD_TAG:
      return ti_decode_not_implemented(ptr,"getFold",-0,contextptr);
      // --ptr;
      // return symbolic(at_GetFold,vecteur(0));
    case SWITCH_TAG:
      return ti_decode_nary(ptr,at_window_switch,0,contextptr);
    case UNITCONV_TAG:
      return ti_decode_not_implemented(ptr,"UnitConv",2,contextptr);
    case ORD_TAG:
      return ti_decode_unary(ptr,at_ord,contextptr);
    case EXPR_TAG:
      return ti_decode_unary(ptr,at_expr,contextptr);
    case CHAR_TAG:
      return ti_decode_unary(ptr,at_char,contextptr);
    case STRING_TAG:
      return ti_decode_unary(ptr,at_string,contextptr);
    case SETFOLD_TAG:
      return ti_decode_unary(ptr,at_SetFold,contextptr);
    case GETTYPE_TAG:
      return ti_decode_unary(ptr,at_getType,contextptr);
    case GETMODE_TAG:
      return ti_decode_not_implemented(ptr,"getMode",1,contextptr);
    case PTTEST_TAG:
      return ti_decode_not_implemented(ptr,"ptTest",2,contextptr);
    case PXLTEST_TAG:
      return ti_decode_not_implemented(ptr,"PxlTest",2,contextptr);
    case SETGRAPH_TAG:
      return ti_decode_not_implemented(ptr,"setGraph",2,contextptr);
    case SETTABLE_TAG:
      return ti_decode_not_implemented(ptr,"setTable",2,contextptr);
    case SETMODE_TAG:
      return ti_decode_not_implemented(ptr,"setMode",-1,contextptr);
    case FORMAT_TAG:
      return ti_decode_nary(ptr,at_format,1,contextptr);
    case INSTRING_TAG:
      return ti_decode_nary(ptr,at_inString,2,contextptr);
    case APPEND_TAG:
      return ti_decode_binary(ptr,at_append,false,contextptr);
    case DD_TAG:
      return ti_decode_not_implemented(ptr,"DD",1,contextptr);
    case EXPR2DMS_TAG:
      return ti_decode_not_implemented(ptr,"->DMS",1,contextptr);
    case VEC2RECT_TAG:
      return ti_decode_not_implemented(ptr,">Rect",1,contextptr);
    case VEC2POLAR_TAG:
      return ti_decode_not_implemented(ptr,">Polar",1,contextptr);
    case VEC2CYLIND_TAG:
      return ti_decode_not_implemented(ptr,">Cylind",1,contextptr);
    case VEC2SPHERE_TAG:
      return ti_decode_not_implemented(ptr,">Sphere",1,contextptr);
    case PARENTH_START_TAG:
    case PARENTH_END_TAG:
    case MAT_START_TAG:
    case MAT_END_TAG:
    case LIST_START_TAG:
    case LIST_END_TAG:
    case COMMA_TAG:
    case SEMICOLON_TAG:
    case COMPLEX_ANGLE_TAG:
    case SINGLE_QUOTE_TAG:
    case QUOTE_TAG:
      return gensizeerr(gettext("Parser internal token"));
    case POLCPLX_TAG: // FIXME not clear if 2 or (2 or more args)
      return ti_decode_not_implemented(ptr,"polCplx",-2,contextptr);
      // return ti_decode_not_implemented(ptr,"polCplx",2,contextptr);
    case TMPCNV_TAG:
      return ti_decode_not_implemented(ptr,"tmpCnv",2,contextptr);
    case DELTA_TMPCNV_TAG:
      return ti_decode_not_implemented(ptr,"DtmpCnv",2,contextptr);
    case GETUNITS_TAG:
      return ti_decode_not_implemented(ptr,"getUnits",-0,contextptr);
      // --ptr;
      // return string2gen("getUnits",false);
    case SETUNITS_TAG:
      return ti_decode_not_implemented(ptr,"setUnits",1,contextptr);
    case BIN_TAG:
      return ti_decode_not_implemented(ptr,"0b",1,contextptr);
    case HEX_TAG:
      return ti_decode_not_implemented(ptr,"0h",1,contextptr);
    case INT2BIN_TAG:
      return ti_decode_not_implemented(ptr,">Bin",1,contextptr);
    case INT2DEC_TAG:
      return ti_decode_not_implemented(ptr,">Dec",1,contextptr);
    case INT2HEX_TAG:
      return ti_decode_not_implemented(ptr,">Hex",1,contextptr);
    case DET_TOL_TAG:
      return ti_decode_binary(ptr,at_det,true,contextptr);
    case REF_TOL_TAG:
      return ti_decode_binary(ptr,at_ref,true,contextptr);
    case RREF_TOL_TAG:
      return ti_decode_binary(ptr,at_rref,true,contextptr);
    case SIMULT_TOL_TAG:
      return ti_decode_nary(ptr,at_simult,3,contextptr);
    case GETCONFG_TAG:
      return ti_decode_not_implemented(ptr,"getConfg",-0,contextptr);
      // --ptr;
      // return string2gen("getConfg",false);
    case V_AUGMENT_TAG:
      return ti_decode_binary(ptr,at_semi_augment,true,contextptr);
    case VARIANCE_TWOARG_TAG:
      return ti_decode_binary(ptr,at_variance,true,contextptr);
    default:
      return gensizeerr(gettext("Unknown secondary tag")+print_INT_(*ptr));
    }
    return 0;
  }

  static gen ti_decode_ifthen(const vecteur & arg0,GIAC_CONTEXT){
    vecteur arg(arg0);
    gen gif,gthen,gelse;
    int i=equalposcomp(arg,at_ti_then);
    if (!i){
      gif=arg.front();
      arg=vecteur(arg.begin()+1,arg.end());
    }
    else {
      gif=symb_bloc(vecteur(arg.begin(),arg.begin()+i-1));
      arg=vecteur(arg.begin()+i,arg.end());
    }
    // elseif?
    i=equalposcomp(arg,at_ti_elseif);
    if (i){
      gthen=symb_bloc(vecteur(arg.begin(),arg.begin()+i-1));
      arg=vecteur(arg.begin()+i,arg.end());
      return symb_ifte(gif,gthen,ti_decode_ifthen(arg,contextptr));
    }
    // no more elseif found, search for else
    i=equalposcomp(arg,at_ti_else);
    if (!i)
      gelse=undef;
    else {
      gelse=symb_bloc(vecteur(arg.begin()+i,arg.end()));
      arg=vecteur(arg.begin(),arg.begin()+i-1);
    }
    gthen=symb_bloc(arg);
    return symb_ifte(gif,gthen,gelse);
  }

  static gen ti_decode_ifthen(octet * & ptr,GIAC_CONTEXT){
    vecteur arg(ti_decode_list(ptr,ENDIF_ITAG,COMMAND_TAG,contextptr));
    return ti_decode_ifthen(arg,contextptr);
  }

  static gen ti_decode_if(octet * & ptr,GIAC_CONTEXT){
    gen test(ti_decode_tag(ptr,contextptr));
    ti_decode_newline(ptr,contextptr);
    gen ifclause(ti_decode_tag(ptr,contextptr));
    ti_decode_newline(ptr,contextptr);
    return symb_ifte(test,ifclause,undef);
  }

  static gen ti_decode_loop(octet * & ptr,GIAC_CONTEXT){
    vecteur arg(ti_decode_list(ptr,ENDLOOP_ITAG,COMMAND_TAG,contextptr));
    ptr -= 2; // skip size
    return symb_for(zero,plus_one,zero,symb_bloc(arg));
  }

  static gen ti_decode_for(octet * & ptr,GIAC_CONTEXT){
    gen gcompteur(ti_decode_tag(ptr,contextptr));
    gen gdebut(ti_decode_tag(ptr,contextptr));
    gen gfin(ti_decode_tag(ptr,contextptr));
    gen gstep(plus_one);
    if (*ptr==END_TAG)
      --ptr;
    else {
      if (!ti_decode_newline(ptr,contextptr)){
	gstep=ti_decode_tag(ptr,contextptr);
	if (*ptr==END_TAG)
	  --ptr;
      }
    }
    ti_decode_newline(ptr,contextptr);    
    vecteur arg(ti_decode_list(ptr,ENDFOR_ITAG,COMMAND_TAG,contextptr));
    ptr -= 2; // skip size
    if (ck_is_strictly_positive(gstep,contextptr))
      return symb_for(symb_sto(gdebut,gcompteur),symb_inferieur_egal(gcompteur,gfin),symb_sto(gcompteur+gstep,gcompteur),symb_bloc(arg));
    else
      return symb_for(symb_sto(gdebut,gcompteur),symb_superieur_egal(gcompteur,gfin),symb_sto(gcompteur+gstep,gcompteur),symb_bloc(arg));
  }

  static gen ti_decode_while(octet * & ptr,GIAC_CONTEXT){
    gen test(ti_decode_tag(ptr,contextptr));
    ti_decode_newline(ptr,contextptr);
    vecteur arg(ti_decode_list(ptr,ENDWHILE_ITAG,COMMAND_TAG,contextptr));
    ptr -= 2; // skip size
    return symb_for(zero,test,zero,symb_bloc(arg));
  }

  static gen ti_decode_try(octet * & ptr,GIAC_CONTEXT){
    vecteur arg(ti_decode_list(ptr,ENDTRY_ITAG,COMMAND_TAG,contextptr));
    ptr -= 2; // skip size
    int i=equalposcomp(arg,at_ti_else_try);
    gen gtry=symb_bloc(vecteur(arg.begin()+i,arg.end()));
    gen gelse=symb_bloc(vecteur(arg.begin(),arg.begin()+i-1));
    return symb_try_catch(makevecteur(gtry,at_break,gelse));
  }

  static gen ti_decode_func(octet * & ptr,octet end,GIAC_CONTEXT){
    vecteur localvar;
    ti_decode_newline(ptr,contextptr);
    if (*ptr==COMMENT_TAG){ // skip comment
      --ptr;
      while (!*ptr) --ptr;
      ti_decode_string(ptr,contextptr);
      ti_decode_newline(ptr,contextptr);
    }
    if (*(ptr-1)==LOCAL_ITAG && *ptr==COMMAND_TAG){ // check local
      ptr -=2;
      localvar=ti_decode_list(ptr,END_TAG,contextptr);
    }
    vecteur arg(ti_decode_list(ptr,end,COMMAND_TAG,contextptr));
    if (localvar.empty())
      return symb_bloc(arg);
    else
      return symb_local(localvar,arg,contextptr);
  }

  static gen ti_decode_dialog(octet * & ptr,GIAC_CONTEXT){
    vecteur arg(ti_decode_list(ptr,ENDDLOG_ITAG,COMMAND_TAG,contextptr));
    return symbolic(at_Dialog,arg);
  }

  static gen ti_decode_custom(octet * & ptr,GIAC_CONTEXT){
    vecteur arg(ti_decode_list(ptr,ENDCUSTM_ITAG,COMMAND_TAG,contextptr));
    return symbolic(at_ti_not_implemented,arg);
  }

  static gen ti_decode_toolbar(octet * & ptr,GIAC_CONTEXT){
    vecteur arg(ti_decode_list(ptr,ENDTBAR_ITAG,COMMAND_TAG,contextptr));
    return symbolic(at_ti_not_implemented,arg);
  }

  static gen ti_decode_local(octet * & ptr,GIAC_CONTEXT){
    // decode list until ENDFUNC or ENDPRGM COMMAND_TAG
    vecteur res;
    for (;;){
      ti_decode_newline(ptr,contextptr);
      if ( (*(ptr-1)==ENDPRGM_ITAG || *(ptr-1)==ENDFUNC_ITAG) && 
	   *ptr==COMMAND_TAG)
	break;
      res.push_back(ti_decode_tag(ptr,contextptr));
    }
    // do not decrement ptr, keep the end tags for func_itag or prgm_itag
    // split res using first end_tag
    int i=equalposcomp(res,at_ti_endtag);
    if (!i)
      return gensizeerr(gettext("empty local declaration"));
    gen gloc=vecteur(res.begin(),res.begin()+i-1);
    gen gbloc=vecteur(res.begin()+i,res.end());
    return symb_local(gloc,gbloc,contextptr);
  }

  // command or instruction tag
  static gen ti_command_tag(octet * & ptr,GIAC_CONTEXT){
    switch (*ptr){
    case CLRDRAW_ITAG:
      --ptr;
      return symbolic(at_ClrDraw,vecteur(0));
    case CLRGRAPH_ITAG:
      --ptr;
      return symbolic(at_ClrGraph,vecteur(0));
    case CLRIO_ITAG:
      --ptr;
      return symbolic(at_ClrIO,vecteur(0));
    case CLRHOME_ITAG: // FIXME
      --ptr;
      return symbolic(at_ti_not_implemented,string2gen("ClrHome",false));
    case CLRTABLE_ITAG:
      --ptr;
      return symbolic(at_ti_not_implemented,string2gen("ClrTable",false));
    case DISPG_ITAG:
      --ptr;
      return symbolic(at_DispG,vecteur(0));
    case DISPTBL_ITAG:
      --ptr;
      return symbolic(at_ti_not_implemented,string2gen("DispTbl",false));
    case CYCLE_ITAG:
      --ptr; // maybe ptr -= 3 to skip offset
      return symbolic(at_continue,zero);
    case CUSTOM_ITAG:
      --ptr;
      return ti_decode_custom(ptr,contextptr);
    case DIALOG_ITAG:
      --ptr;
      return ti_decode_dialog(ptr,contextptr);
    case TOOLBAR_ITAG:
      --ptr;
      return ti_decode_toolbar(ptr,contextptr);
    case ELSE_ITAG:
      --ptr;
      return at_ti_else;
    case ENDCUSTM_ITAG:
    case ENDDLOG_ITAG: 
    case ENDFOR_ITAG:
    case ENDFUNC_ITAG:
    case ENDIF_ITAG:
    case ENDLOOP_ITAG:
    case ENDPRGM_ITAG:
    case ENDTBAR_ITAG:
    case ENDTRY_ITAG:
    case ENDWHILE_ITAG:
      return gensizeerr(gettext("End structure ")+print_INT_(*ptr)+" "+print_INT_(*(ptr-2))+" "+print_INT_(*(ptr-1)));
    case EXIT_ITAG:
      ptr -=3 ; // skip offset
      return symbolic(at_break,zero);
    case FUNC_ITAG:
      --ptr;
      return ti_decode_func(ptr,ENDFUNC_ITAG,contextptr);
    case PRGM_ITAG:
      --ptr;
      return ti_decode_func(ptr,ENDPRGM_ITAG,contextptr);
    case LOOP_ITAG:
      --ptr;
      return ti_decode_loop(ptr,contextptr);
    case STOP_ITAG:
      --ptr; // maybe ptr -=3 to skip itag
      return at_ti_stop;
    case THEN_ITAG:
      --ptr;
      return at_ti_then;
    case TRY_ITAG:
      --ptr;
      return ti_decode_try(ptr,contextptr);
    case SHOWSTAT_ITAG:
      --ptr;
      return string2gen("showStat",false);
    case TRACE_ITAG:
      --ptr;
      return string2gen("Trace",false);
    case ZOOMBOX_ITAG:
      --ptr;
      return string2gen("ZoomBox",false);
    case ZOOMDATA_ITAG:
      --ptr;
      return string2gen("ZoomData",false);
    case ZOOMDEC_ITAG:
      --ptr;
      return string2gen("ZoomDec",false);
    case ZOOMFIT_ITAG:
      --ptr;
      return string2gen("ZoomFit",false);
    case ZOOMIN_ITAG:
      --ptr;
      return string2gen("ZoomIn",false);
    case ZOOMINT_ITAG:
      --ptr;
      return string2gen("ZoomInt",false);
    case ZOOMOUT_ITAG:
      --ptr;
      return string2gen("ZoomOut",false);
    case ZOOMPREV_ITAG:
      --ptr;
      return string2gen("ZoomPrev",false);
    case ZOOMRCL_ITAG:
      --ptr;
      return string2gen("ZoomRcl",false);
    case ZOOMSQR_ITAG:
      --ptr;
      return string2gen("ZoomSqr",false);
    case ZOOMSTD_ITAG:
      --ptr;
      return string2gen("ZoomStd",false);
    case ZOOMSTO_ITAG:
      --ptr;
      return string2gen("ZoomSto",false);
    case ZOOMTRIG_ITAG: // FIXME
      --ptr;
      return string2gen("ZoomTrig",false);
    case DRAWFUNC_ITAG:
      return ti_decode_unary(ptr,at_plotfunc,contextptr);
    case DRAWINV_ITAG: // FIXME
      return ti_decode_unary(ptr,at_DrawInv,contextptr);
      // return ti_decode_unary(ptr,at_ti_drawinv,contextptr);
    case GOTO_ITAG:
      return ti_decode_unary(ptr,at_goto,contextptr);
    case LBL_ITAG:
      return ti_decode_unary(ptr,at_label,contextptr);
    case GET_ITAG: 
      return ti_decode_unary(ptr,at_Get,contextptr);
    case SEND_ITAG:
      return ti_decode_not_implemented(ptr,"Send",1,contextptr);
    case GETCALC_ITAG:
      return ti_decode_unary(ptr,at_GetCalc,contextptr);
    case SENDCALC_ITAG:
      return ti_decode_not_implemented(ptr,"SendCalc",1,contextptr);
    case NEWFOLD_ITAG:
      return ti_decode_unary(ptr,at_NewFold,contextptr);
    case PRINTOBJ_ITAG: //FIXME
      return ti_decode_not_implemented(ptr,"printObj",1,contextptr);
    case RCLGDB_ITAG:
      return ti_decode_not_implemented(ptr,"RclGDB",1,contextptr);
    case STOGDB_ITAG:
      return ti_decode_not_implemented(ptr,"StoGDB",1,contextptr);
    case ELSEIF_ITAG:
      --ptr;
      return at_ti_elseif;
    case IF_ITAG:
      --ptr;
      return ti_decode_if(ptr,contextptr);
    case IFTHEN_ITAG:
      --ptr;
      return ti_decode_ifthen(ptr,contextptr);
    case WHILE_ITAG:
      --ptr;
      return ti_decode_while(ptr,contextptr);
    case RANDSEED_ITAG:
      return ti_decode_unary(ptr,at_RandSeed,contextptr);
    case COPYVAR_ITAG:
      return ti_decode_binary(ptr,at_CopyVar,true,contextptr);
    case RENAME_ITAG:
      return ti_decode_not_implemented(ptr,"Rename",2,contextptr);
    case STYLE_ITAG:
      return ti_decode_not_implemented(ptr,"Style",2,contextptr);
    case LINETAN_ITAG: 
      return ti_decode_binary(ptr,at_LineTan,true,contextptr);
    case FILL_ITAG:
      return ti_decode_not_implemented(ptr,"Fill",2,contextptr);
    case REQUEST_ITAG:
      return ti_decode_binary(ptr,at_Request,true,contextptr);
    case POPUP_ITAG:
      return ti_decode_binary(ptr,at_PopUp,true,contextptr);
    case PTCHG_ITAG:
      return ti_decode_not_implemented(ptr,"PtChg",2,contextptr);
    case PTOFF_ITAG:
      return ti_decode_binary(ptr,at_PtOff,true,contextptr);
    case PTON_ITAG:
      return ti_decode_binary(ptr,at_PtOn,true,contextptr);
    case PXLCHG_ITAG:
      return ti_decode_not_implemented(ptr,"PxlChg",2,contextptr);
    case PXLOFF_ITAG:
      return ti_decode_binary(ptr,at_PxlOff,true,contextptr);
    case PXLON_ITAG:
      return ti_decode_binary(ptr,at_PxlOn,true,contextptr);
    case MOVEVAR_ITAG:
      return ti_decode_not_implemented(ptr,"MoveVar",-3,contextptr);
    case DROPDOWN_ITAG:
      return ti_decode_nary(ptr,at_DropDown,3,contextptr);
    case OUTPUT_ITAG:
      return ti_decode_nary(ptr,at_Output,3,contextptr);
    case PTTEXT_ITAG:
      return ti_decode_nary(ptr,at_PtText,3,contextptr);
    case PXLTEXT_ITAG:
      return ti_decode_not_implemented(ptr,"PxlText",-3,contextptr);
    case DRAWSLP_ITAG:
      return ti_decode_nary(ptr,at_DrawSlp,3,contextptr);
    case PAUSE_ITAG:
      return ti_decode_nary(ptr,at_Pause,0,contextptr);
    case RETURN_ITAG:
      return ti_decode_nary(ptr,at_return,0,contextptr);
    case INPUT_ITAG:
      return ti_decode_nary(ptr,at_Input,2,contextptr);
    case PLOTSOFF_ITAG:
      return ti_decode_not_implemented(ptr,"PlotsOff",-0,contextptr);
    case PLOTSON_ITAG:
      return ti_decode_not_implemented(ptr,"PlotsOn",-0,contextptr);
    case ONEVAR_ITAG:
      return ti_decode_not_implemented(ptr,"OneVar",-1,contextptr);
    case TITLE_ITAG:
      return ti_decode_nary(ptr,at_Title,1,contextptr);
    case ITEM_ITAG:
      return ti_decode_not_implemented(ptr,"Item",-1,contextptr); 
    case INPUTSTR_ITAG:
      return ti_decode_nary(ptr,at_InputStr,3,contextptr);
    case LINEHORZ_ITAG:
      return ti_decode_nary(ptr,at_LineHorz,4,contextptr);
    case LINEVERT_ITAG:
      return ti_decode_nary(ptr,at_LineVert,4,contextptr);
    case PXLHORZ_ITAG:
      return ti_decode_not_implemented(ptr,"PxlHorz",-1,contextptr);
    case PXLVERT_ITAG:
      return ti_decode_not_implemented(ptr,"PxlVert",-1,contextptr);
    case ANDPIC_ITAG:
      return ti_decode_not_implemented(ptr,"AndPic",-1,contextptr);
    case XORPIC_ITAG:
      return ti_decode_not_implemented(ptr,"XorPic",-1,contextptr);
    case DRAWPOL_ITAG:
      return ti_decode_not_implemented(ptr,"DrawPol",-1,contextptr);
    case TABLE_ITAG:
      return ti_decode_not_implemented(ptr,"Table",-1,contextptr);
    case RCLPIC_ITAG:
      return ti_decode_nary(ptr,at_RclPic,1,contextptr);
    case RPLCPIC_ITAG:
      return ti_decode_nary(ptr,at_RplcPic,1,contextptr);
    case TEXT_ITAG:
      return ti_decode_nary(ptr,at_Text,1,contextptr);
    case STOPIC_ITAG:
      return ti_decode_nary(ptr,at_StoPic,1,contextptr);
    case GRAPH_ITAG:
      return ti_decode_nary(ptr,at_Graph,1,contextptr);
    case NEWPIC_ITAG:
      return ti_decode_nary(ptr,at_NewPic,1,contextptr);
    case DRAWPARM_ITAG:
      return ti_decode_nary(ptr,at_DrawParm,2,contextptr);
    case CYCLEPIC_ITAG:
      return ti_decode_nary(ptr,at_CyclePic,2,contextptr);
    case CUBICREG_ITAG:
      return ti_decode_not_implemented(ptr,"CubicReg",-2,contextptr);
    case EXPREG_ITAG:
      return ti_decode_not_implemented(ptr,"ExpReg",-2,contextptr);
    case LINREG_ITAG:
      return ti_decode_not_implemented(ptr,"LinReg",-2,contextptr);
    case LNREG_ITAG:
      return ti_decode_not_implemented(ptr,"LnReg",-2,contextptr);
    case MEDMED_ITAG:
      return ti_decode_not_implemented(ptr,"MedMed",-2,contextptr);
    case POWERREG_ITAG:
      return ti_decode_not_implemented(ptr,"PowerReg",-2,contextptr);
    case QUADREG_ITAG:
      return ti_decode_not_implemented(ptr,"QuadReg",-2,contextptr);
    case QUARTREG_ITAG:
      return ti_decode_not_implemented(ptr,"QuartReg",-2,contextptr);
    case SINREG_ITAG:
      return ti_decode_not_implemented(ptr,"SinReg",-2,contextptr);
    case LOGISTIC_ITAG:
      return ti_decode_not_implemented(ptr,"Logistic",-2,contextptr);
    case TWOVAR_ITAG:
      return ti_decode_not_implemented(ptr,"TwoVar",-2,contextptr);
    case SHADE_ITAG:
      return ti_decode_not_implemented(ptr,"Shade",-2,contextptr);
    case FOR_ITAG:
      --ptr;
      return ti_decode_for(ptr,contextptr);
    case CIRCLE_ITAG:
      return ti_decode_nary(ptr,at_Circle,2,contextptr);
    case LINE_ITAG:
      return ti_decode_nary(ptr,at_Line,2,contextptr);
    case DISP_ITAG:
      return ti_decode_nary(ptr,at_print,1,contextptr);
    case PXLCRCL_ITAG:
      return ti_decode_not_implemented(ptr,"PxlCrcl",-3,contextptr);
    case NEWPLOT_ITAG:
      return ti_decode_not_implemented(ptr,"NewPlot",-0,contextptr);
    case PXLLINE_ITAG:
      return ti_decode_not_implemented(ptr,"PxlLine",-4,contextptr);
    case FNOFF_ITAG:
      return ti_decode_not_implemented(ptr,"FnOff",-0,contextptr);
    case FNON_ITAG:
      return ti_decode_not_implemented(ptr,"FnOn",-0,contextptr);
    case LOCAL_ITAG:
      --ptr;
      return ti_decode_local(ptr,contextptr);
    case DELFOLD_ITAG: // FIXME might be nary or unary!!!
      return ti_decode_unary(ptr,at_DelFold,contextptr);
    case DELVAR_ITAG:
      return ti_decode_nary(ptr,at_DelVar,0,contextptr);
    case PROMPT_ITAG:
      return ti_decode_nary(ptr,at_Prompt,0,contextptr);
    case SORTA_ITAG:
      return ti_decode_nary(ptr,at_SortA,1,contextptr);
    case SORTD_ITAG:
      return ti_decode_nary(ptr,at_SortD,1,contextptr);
    case LOCK_ITAG:
      return ti_decode_not_implemented(ptr,"Lock",-1,contextptr);
    case UNLOCK_ITAG: // FIXME maybe unary op
      return ti_decode_not_implemented(ptr,"Unlock",-1,contextptr);
    case NEWDATA_ITAG:
      return ti_decode_not_implemented(ptr,"NewData",-2,contextptr);
    case DEFINE_ITAG:
      return _Define(ti_decode_binary(ptr,at_nop,true,contextptr)._SYMBptr->feuille,contextptr);
    case ELSE_TRY_ITAG:
      --ptr;
      return at_ti_else_try;
    case CLRERR_ITAG:
      --ptr;
      return string2gen("ClrErr",false);
    case PASSERR_ITAG:
      --ptr;
      return string2gen("PassErr",false);
    case DISPHOME_ITAG:
      --ptr;
      return symbolic(at_DispHome,vecteur(0));
    case EXEC_ITAG:
      return ti_decode_nary(ptr,at_Exec,0,contextptr);      
    case ARCHIVE_ITAG:
      return ti_decode_nary(ptr,at_Archive,1,contextptr);      
    case UNARCHIV_ITAG:
      return ti_decode_nary(ptr,at_Unarchiv,1,contextptr);      
    case LU_ITAG:
      return ti_decode_nary(ptr,at_LU,4,contextptr);      
    case QR_ITAG:
      return ti_decode_nary(ptr,at_QR,4,contextptr);   
    case BLDDATA_ITAG:
      return ti_decode_not_implemented(ptr,"BldData",-1,contextptr);
    case DRWCTOUR_ITAG:
      return ti_decode_unary(ptr,at_DrwCtour,contextptr);
    case NEWPROB_ITAG:
      --ptr;
      return string2gen("NewProb",false);
    case CUSTMON_ITAG:
      --ptr;
      return string2gen("CustmOn",false);
    case CUSTMOFF_ITAG:
      --ptr;
      return string2gen("CustmOff",false);
    case SENDCHAT_ITAG:
      return ti_decode_not_implemented(ptr,"SendChat",1,contextptr);      
    default:
      return gensizeerr(gettext("Unknown instruction tag:")+print_INT_(*ptr));
    }
    return 0;
  }

  // convert a TI9x object to a gen
  // ptr points to the tag of the structure (at the end)
  // after the call ptr points to the address previous beginning of object
  gen ti_decode_tag(octet * & ptr,GIAC_CONTEXT){
    // unsigned short l;
    gen res;
    switch (*ptr){
    case COMPLEX_TAG: // FIXME
      res=cst_i*ti_decode_tag(ptr,contextptr);
      return ti_decode_tag(ptr,contextptr)+res;
      // return ti_decode_not_implemented(ptr,"ComplexTag",2,contextptr);
    case NONNEGATIVE_INTEGER_TAG:
      --ptr;
      return ti_decode_unsigned(ptr,contextptr);
    case NEGATIVE_INTEGER_TAG:
      --ptr;
      return -ti_decode_unsigned(ptr,contextptr);
    case POSITIVE_FRACTION_TAG:
      --ptr;
      return ti_decode_fraction(ptr,contextptr);
    case NEGATIVE_FRACTION_TAG:
      --ptr;
      return -ti_decode_fraction(ptr,contextptr);
    case FLOAT_TAG:
      --ptr;
      return ti_decode_double(ptr,contextptr);
    case STR_DATA_TAG:
      ptr-=2;
      return string2gen(ti_decode_string(ptr,contextptr),false);
    case LIST_TAG: 
      --ptr;
      return ti_decode_list(ptr,END_TAG,contextptr);
    case MATRIX_TAG:
      --ptr;
      res=ti_decode_list(ptr,END_TAG,contextptr);
      res.subtype=_MATRIX__VECT;
      return res;
    case USER_DEF_TAG:
      --ptr;
      return ti_decode_function(ptr,contextptr);
    case DATA_VAR_TAG:
      return gensizeerr(gettext("Data_var_tag not implemented"));
    case GDB_VAR_TAG:
      return gensizeerr(gettext("Gdb_var_tag not implemented"));
    case PIC_VAR_TAG:
      return gensizeerr(gettext("Unable to find pic_var_tag length"));      
    case TEXT_VAR_TAG:
      return gensizeerr(gettext("Unable to find text_var_tag length"));
    case COMMAND_TAG: // or INSTRUCTION_TAG
      --ptr;
      return ti_command_tag(ptr,contextptr);
    case EXT_TAG: // or SECONDARY_TAG
      --ptr;
      return ti_secondary_tag(ptr,contextptr);
    case END_TAG:
      --ptr;
      return at_ti_endtag;
    case END_OF_SEGMENT:
      return gensizeerr(gettext("End_tag/End_of_segment"));
    case ASM_PRGM_TAG:
      return gensizeerr(gettext("Asm programs not implemented"));
    case GEN_DATA_TEG:
      return gensizeerr(gettext("3rd party data"));
    case VAR_NAME_TAG:
      --ptr;
      return ti_decode_identificateur(ptr,contextptr);
    case VAR_A_TAG:
      --ptr;
      return a__IDNT_e;
    case VAR_B_TAG:
      --ptr;
      return b__IDNT_e;
    case VAR_C_TAG:
      --ptr;
      return c__IDNT_e;
    case VAR_D_TAG:
      --ptr;
      return d__IDNT_e;
    case VAR_E_TAG:
      --ptr;
      return symbolic(at_exp,1);
    case VAR_F_TAG:
      --ptr;
      return f__IDNT_e;
    case VAR_G_TAG:
      --ptr;
      return g__IDNT_e;
    case VAR_H_TAG:
      --ptr;
      return h__IDNT_e;
    case VAR_I_TAG:
      --ptr;
      return i__IDNT_e; 
    case VAR_J_TAG:
      --ptr;
      return j__IDNT_e;
    case VAR_K_TAG:
      --ptr;
      return k__IDNT_e;
    case VAR_L_TAG:
      --ptr;
      return l__IDNT_e;
    case VAR_M_TAG:
      --ptr;
      return m__IDNT_e;
    case VAR_N_TAG:
      --ptr;
      return n__IDNT_e;
    case VAR_O_TAG:
      --ptr;
      return o__IDNT_e;
    case VAR_P_TAG:
      --ptr;
      return p__IDNT_e;
    case _VAR_Q_TAG:
      cerr << "_var_q_tag" << endl;
    case VAR_Q_TAG:
      --ptr;
      return q__IDNT_e;
    case VAR_R_TAG:
      --ptr;
      return r__IDNT_e;
    case VAR_S_TAG:
      --ptr;
      return s__IDNT_e;
    case VAR_T_TAG:
      --ptr;
      return t__IDNT_e;
    case VAR_U_TAG:
      --ptr;
      return u__IDNT_e;
    case VAR_V_TAG:
      --ptr;
      return v__IDNT_e;
    case VAR_W_TAG:
      --ptr;
      return w__IDNT_e;
    case VAR_X_TAG:
      --ptr;
      return x__IDNT_e;
    case VAR_Y_TAG:
      --ptr;
      return y__IDNT_e;
    case VAR_Z_TAG:
      --ptr;
      return z__IDNT_e;
    case EXT_SYSTEM_TAG:
      --ptr;
      return ti_decode_sysvar_tag(ptr,contextptr);
    case ARB_REAL_TAG:
      --ptr;
      return ti_decode_arb_real(ptr,true,contextptr);
    case ARB_INT_TAG:
      --ptr;
      return ti_decode_arb_real(ptr,false,contextptr);
    case PI_TAG:
      --ptr;
      return cst_pi;
    case EXP_TAG:
      --ptr;
      return symbolic(at_exp,1);
    case IM_TAG:
      --ptr;
      return cst_i;
    case NEGINFINITY_TAG:
      --ptr;
      return minus_inf;
    case INFINITY_TAG:
      --ptr;
      return plus_inf;       
    case PN_INFINITY_TAG:
      --ptr;
      return unsigned_inf;
    case UNDEF_TAG:
      --ptr;
      return undef;
    case FALSE_TAG:
      --ptr;
      return zero;
    case TRUE_TAG:
      --ptr;
      return plus_one;
    case NOTHING_TAG:
      return ti_decode_unary(ptr,at_nop,contextptr);
    case ACOSH_TAG:
      return ti_decode_unary(ptr,at_acosh,contextptr);
    case ASINH_TAG:
      return ti_decode_unary(ptr,at_asinh,contextptr);
    case ATANH_TAG:
      return ti_decode_unary(ptr,at_atanh,contextptr);
    case COSH_TAG:
      return ti_decode_unary(ptr,at_acosh,contextptr);
    case SINH_TAG:
      return ti_decode_unary(ptr,at_asinh,contextptr);
    case TANH_TAG:
      return ti_decode_unary(ptr,at_atanh,contextptr);
    case ACOS_TAG:
      return ti_decode_unary(ptr,at_acos,contextptr);
    case ASIN_TAG:
      return ti_decode_unary(ptr,at_asin,contextptr);
    case ATAN_TAG:
      return ti_decode_unary(ptr,at_atan,contextptr);
    case RACOS_TAG:
      return ti_decode_unary(ptr,at_acos,contextptr);
    case RASIN_TAG:
      return ti_decode_unary(ptr,at_asin,contextptr);
    case RATAN_TAG:
      return ti_decode_unary(ptr,at_atan,contextptr);
    case COS_TAG:
      return ti_decode_unary(ptr,at_cos,contextptr);
    case SIN_TAG:
      return ti_decode_unary(ptr,at_sin,contextptr);
    case TAN_TAG:
      return ti_decode_unary(ptr,at_tan,contextptr);
    case 0x47: // FIXME!! where is 10^x?
      return ti_decode_unary(ptr,at_nop,contextptr);
    case ITAN_TAG:
      return ti_decode_unary(ptr,at_tan,contextptr);
    case ABS_TAG:
      return ti_decode_unary(ptr,at_abs,contextptr);
    case ANGLE_TAG:
      return ti_decode_unary(ptr,at_arg,contextptr);
    case CEILING_TAG:
      return ti_decode_unary(ptr,at_ceil,contextptr);
    case FLOOR_TAG:
      return ti_decode_unary(ptr,at_floor,contextptr);
    case INT_TAG:
      return ti_decode_unary(ptr,at_floor,contextptr);
    case SIGN_TAG:
      return ti_decode_unary(ptr,at_sign,contextptr);
    case SQRT_TAG:
      return ti_decode_unary(ptr,at_sqrt,contextptr);
    case EXPF_TAG:
      return ti_decode_unary(ptr,at_exp,contextptr);
    case LN_TAG:
      return ti_decode_unary(ptr,at_ln,contextptr);
    case LOG_TAG:
      return ti_decode_unary(ptr,at_log10,contextptr);
    case FPART_TAG:
      return ti_decode_unary(ptr,at_fPart,contextptr);
    case IPART_TAG:
      return ti_decode_unary(ptr,at_iPart,contextptr);
    case CONJ_TAG:
      return ti_decode_unary(ptr,at_conj,contextptr);
    case IMAG_TAG:
      return ti_decode_unary(ptr,at_im,contextptr);
    case REAL_TAG:
      return ti_decode_unary(ptr,at_re,contextptr);
    case APPROX_TAG:
      return ti_decode_unary(ptr,at_evalf,contextptr);
    case TEXPAND_TAG:
      return ti_decode_unary(ptr,at_texpand,contextptr);
    case TCOLLECT_TAG:
      return ti_decode_unary(ptr,at_tcollect,contextptr);
    case GETDENOM_TAG:
      return ti_decode_unary(ptr,at_getDenom,contextptr);
    case GETNUM_TAG:
      return ti_decode_unary(ptr,at_getNum,contextptr);
    case CUMSUM_TAG:
      return ti_decode_unary(ptr,at_cumSum,contextptr);
    case DET_TAG:
      return ti_decode_unary(ptr,at_det,contextptr);
    case COLNORM_TAG:
      return ti_decode_unary(ptr,at_colNorm,contextptr);
    case ROWNORM_TAG:
      return ti_decode_unary(ptr,at_rowNorm,contextptr);
    case NORM_TAG:
      return ti_decode_unary(ptr,at_l2norm,contextptr);
    case MEAN_TAG:
      return ti_decode_unary(ptr,at_mean,contextptr);
    case MEDIAN_TAG:
      return ti_decode_unary(ptr,at_median,contextptr);
    case PRODUCT_TAG:
      return ti_decode_unary(ptr,at_product,contextptr);
    case STDDEV_TAG:
      return ti_decode_unary(ptr,at_stddev,contextptr);
    case SUM_TAG:
      return ti_decode_unary(ptr,at_sum,contextptr);
    case VARIANCE_TAG:
      return ti_decode_unary(ptr,at_variance,contextptr);
    case UNITV_TAG:
      return ti_decode_unary(ptr,at_unitV,contextptr);
    case DIM_TAG:
      return ti_decode_unary(ptr,at_dim,contextptr);
    case MAT2LIST_TAG: 
      return ti_decode_unary(ptr,at_mat2list,contextptr);
    case NEWLIST_TAG:
      return ti_decode_unary(ptr,at_newList,contextptr);
    case RREF_TAG:
      return ti_decode_unary(ptr,at_rref,contextptr);
    case REF_TAG:
      return ti_decode_unary(ptr,at_ref,contextptr);
    case IDENTITY_TAG:
      return ti_decode_unary(ptr,at_identity,contextptr);
    case DIAG_TAG:
      return ti_decode_unary(ptr,at_diag,contextptr);
    case COLDIM_TAG:
      return ti_decode_unary(ptr,at_colDim,contextptr);
    case ROWDIM_TAG:
      return ti_decode_unary(ptr,at_rowDim,contextptr);
    case TRANSPOSE_TAG:
      return ti_decode_unary(ptr,at_transpose,contextptr);
    case FACTORIAL_TAG:
      return ti_decode_unary(ptr,at_factorial,contextptr);
    case PERCENT_TAG: 
      return ti_decode_not_implemented(ptr,"percent",1,contextptr);
    case RADIANS_TAG: 
      return ti_decode_not_implemented(ptr,"radians",1,contextptr);
    case NOT_TAG:
      return ti_decode_unary(ptr,at_not,contextptr);
    case MINUS_TAG:
      return ti_decode_unary(ptr,at_neg,contextptr);
    case VEC_POLAR_TAG:
      return ti_decode_not_implemented(ptr,">Polar",1,contextptr);
    case VEC_CYLIND_TAG:
      return ti_decode_not_implemented(ptr,">Cylind",1,contextptr);
    case VEC_SPHERE_TAG:
      return ti_decode_not_implemented(ptr,">Sphere",1,contextptr);
    case START_TAG: // internal tag
      return gensizeerr(gettext("start tag"));
    case ISTORE_TAG:
      return ti_decode_binary(ptr,at_sto,false,contextptr);
    case STORE_TAG:
      return ti_decode_binary(ptr,at_sto,false,contextptr);
    case WITH_TAG: 
      return ti_decode_binary(ptr,at_tilocal,true,contextptr);
    case XOR_TAG:
      return ti_decode_binary(ptr,at_xor,true,contextptr);
    case OR_TAG:
      return ti_decode_binary(ptr,at_ou,true,contextptr);
    case AND_TAG:
      return ti_decode_binary(ptr,at_and,true,contextptr);
    case LT_TAG:
      return ti_decode_binary(ptr,at_inferieur_strict,true,contextptr);
    case LE_TAG:
      return ti_decode_binary(ptr,at_inferieur_egal,true,contextptr);
    case EQ_TAG:
      return ti_decode_binary(ptr,at_equal,true,contextptr);
    case GE_TAG:
      return ti_decode_binary(ptr,at_superieur_strict,true,contextptr);
    case GT_TAG:
      return ti_decode_binary(ptr,at_superieur_egal,true,contextptr);
    case NE_TAG:
      return ti_decode_binary(ptr,at_different,true,contextptr);
    case ADD_TAG:
      return ti_decode_binary(ptr,at_plus,false,contextptr);
    case ADDELT_TAG:
      return ti_decode_binary(ptr,at_plus,false,contextptr);
    case SUB_TAG:
      res=ti_decode_binary(ptr,at_minus,false,contextptr);
      res=*res._SYMBptr->feuille._VECTptr;
      res._VECTptr->back()=-res._VECTptr->back();
      return symbolic(at_plus,res);
    case SUBELT_TAG:
      return ti_decode_binary(ptr,at_minus,false,contextptr);
    case MUL_TAG:
      return ti_decode_binary(ptr,at_prod,false,contextptr);
    case MULELT_TAG:
      return ti_decode_binary(ptr,at_pointprod,false,contextptr);
    case DIV_TAG:
      return ti_decode_binary(ptr,at_division,false,contextptr);
    case DIVELT_TAG:
      return ti_decode_binary(ptr,at_pointdivision,false,contextptr);
    case POW_TAG:
      return ti_decode_binary(ptr,at_pow,true,contextptr);
    case POWELT_TAG:
      return ti_decode_binary(ptr,at_pointpow,true,contextptr);
    case SINCOS_TAG:
      return gensizeerr(gettext("Internal sincos token"));
    case SOLVE_TAG:
      return ti_decode_binary(ptr,at_solve,true,contextptr);
    case CSOLVE_TAG:
      return ti_decode_binary(ptr,at_cSolve,true,contextptr);
    case NSOLVE_TAG:
      return ti_decode_binary(ptr,at_nSolve,true,contextptr);
    case ZEROS_TAG:
      return ti_decode_binary(ptr,at_zeros,true,contextptr);
    case CZEROS_TAG:
      return ti_decode_binary(ptr,at_cZeros,true,contextptr);
    case FMIN_TAG:
      return ti_decode_binary(ptr,at_fMin,true,contextptr);
    case FMAX_TAG:
      return ti_decode_binary(ptr,at_fMax,true,contextptr);
    case POLYEVAL_TAG:
      return ti_decode_binary(ptr,at_polyEval,true,contextptr);
    case RANDPOLY_TAG:
      return ti_decode_binary(ptr,at_randPoly,true,contextptr);
    case CROSSP_TAG:
      return ti_decode_binary(ptr,at_crossP,true,contextptr);
    case DOTP_TAG:
      return ti_decode_binary(ptr,at_dotP,true,contextptr);
    case GCD_TAG:
      return ti_decode_binary(ptr,at_gcd,true,contextptr);
    case LCM_TAG:
      return ti_decode_binary(ptr,at_lcm,true,contextptr);
    case MOD_TAG:
      return ti_decode_binary(ptr,at_irem,true,contextptr);
    case INTDIV_TAG:
      return ti_decode_binary(ptr,at_intDiv,true,contextptr);
    case REMAIN_TAG:
      return ti_decode_binary(ptr,at_remain,true,contextptr);
    case NCR_TAG:
      return ti_decode_binary(ptr,at_nCr,true,contextptr);
    case NPR_TAG:
      return ti_decode_binary(ptr,at_nPr,true,contextptr);
    case P2RX_TAG:
      return ti_decode_not_implemented(ptr,"P->Rx",2,contextptr);
    case P2RY_TAG:
      return ti_decode_not_implemented(ptr,"P->Ry",2,contextptr);
    case P2PTHETA_TAG:
      return ti_decode_not_implemented(ptr,"R->Pq",2,contextptr);
    case P2PR_TAG:
      return ti_decode_not_implemented(ptr,"R->Pr",2,contextptr);
    case AUGMENT_TAG:
      return ti_decode_binary(ptr,at_augment,true,contextptr);
    case NEWMAT_TAG:
      return ti_decode_binary(ptr,at_newMat,true,contextptr);
    case RANDMAT_TAG:
      return ti_decode_binary(ptr,at_randMat,true,contextptr);
    case SIMULT_TAG:
      return ti_decode_binary(ptr,at_simult,true,contextptr);
    case PART_TAG:
      return ti_decode_nary(ptr,at_part,1,contextptr);
    case EXP2LIST_TAG:
      return ti_decode_binary(ptr,at_exp2list,true,contextptr);
    case RANDNORM_TAG:
      return ti_decode_binary(ptr,at_randNorm,true,contextptr);
    case MROW_TAG:
      return ti_decode_nary(ptr,at_mRow,3,contextptr);
    case ROWADD_TAG:
      return ti_decode_nary(ptr,at_rowAdd,4,contextptr);
    case ROWSWAP_TAG:
      return ti_decode_nary(ptr,at_rowSwap,3,contextptr);
    case ARCLEN_TAG:
      return ti_decode_nary(ptr,at_arcLen,4,contextptr);
    case NINT_TAG:
      return ti_decode_nary(ptr,at_nInt,4,contextptr);
    case PI_PRODUCT_TAG:
      return ti_decode_nary(ptr,at_product,1,contextptr);
    case SIGMA_SUM_TAG:
      return ti_decode_nary(ptr,at_sum,4,contextptr);
    case MROWADD_TAG:
      return ti_decode_nary(ptr,at_mRowAdd,4,contextptr);
    case ANS_TAG:
      return ti_decode_nary(ptr,at_ans,0,contextptr);
    case ENTRY_TAG:
      return ti_decode_nary(ptr,at_entry,0,contextptr);
    case EXACT_TAG:
      return ti_decode_nary(ptr,at_exact,1,contextptr);
    case LOGB_TAG:
      return ti_decode_binary(ptr,at_logb,true,contextptr);
    case COMDENOM_TAG:
      return ti_decode_nary(ptr,at_comDenom,1,contextptr);
    case EXPAND_TAG:
      return ti_decode_nary(ptr,at_expand,1,contextptr);
    case FACTOR_TAG:
      return ti_decode_nary(ptr,at_factor,1,contextptr);
    case CFACTOR_TAG:
      return ti_decode_nary(ptr,at_cFactor,1,contextptr);
    case INTEGRATE_TAG:
      return ti_decode_nary(ptr,at_integrate,2,contextptr);
    case DIFFERENTIATE_TAG:
      return ti_decode_nary(ptr,at_derive,2,contextptr);
    case AVGRC_TAG:
      return ti_decode_nary(ptr,at_avgRC,2,contextptr);
    case NDERIV_TAG:
      return ti_decode_nary(ptr,at_nDeriv,2,contextptr);
    case TAYLOR_TAG:
      return ti_decode_nary(ptr,at_taylor,3,contextptr);
    case LIMIT_TAG:
      return ti_decode_nary(ptr,at_limit,3,contextptr);
    case PROPFRAC_TAG:
      return ti_decode_nary(ptr,at_propFrac,1,contextptr);
    case WHEN_TAG:
      return ti_decode_nary(ptr,at_when,3,contextptr);
    case ROUND_TAG:
      return ti_decode_nary(ptr,at_round,1,contextptr);
    case DMS_TAG: // FIXME not clear
      return ti_decode_not_implemented(ptr,"DMS",1,contextptr);
      // return ti_decode_not_implemented(ptr,"DMS",-1,contextptr);
    case LEFT_TAG:
      return ti_decode_nary(ptr,at_left,1,contextptr);
    case RIGHT_TAG:
      return ti_decode_nary(ptr,at_right,1,contextptr);
    case MID_TAG:
      return ti_decode_nary(ptr,at_mid,2,contextptr);
    case SHIFT_TAG:
      return ti_decode_nary(ptr,at_shift,1,contextptr);
    case SEQ_TAG:
      return ti_decode_nary(ptr,at_seq,4,contextptr);
    case LIST2MAT_TAG:
      return ti_decode_nary(ptr,at_list2mat,1,contextptr);
    case SUBMAT_TAG:
      return ti_decode_nary(ptr,at_subMat,1,contextptr);
    case SUBSCRIPT_TAG:
      res=ti_decode_nary(ptr,at_at,2,contextptr);
      if (res.type!=_SYMB || res._SYMBptr->feuille.type!=_VECT)
	return gensizeerr(contextptr);
      res=res._SYMBptr->feuille;
      if (res._VECTptr->size()==2)
	return symbolic(at_at,makesequence(res._VECTptr->front(),(res._VECTptr->back()-plus_one)));
      else
	return symbolic(at_at,makesequence(res._VECTptr->front(),gen(vecteur(res._VECTptr->begin()+1,res._VECTptr->end()),_SEQ__VECT)-gen(vecteur(res._VECTptr->size()-1,plus_one),_SEQ__VECT)));
    case RAND_TAG:
      return ti_decode_nary(ptr,at_rand,0,contextptr);
    case MIN_TAG:
      return ti_decode_nary(ptr,at_min,1,contextptr);
    case MAX_TAG:
      return ti_decode_nary(ptr,at_max,1,contextptr);
    case USERFUNC_TAG:
      res=ti_decode_nary(ptr,at_of,0,contextptr);
      if (res.type!=_SYMB || res._SYMBptr->feuille.type!=_VECT)
	return gensizeerr(contextptr);
      res=res._SYMBptr->feuille;
      return symbolic(at_of,makesequence(res._VECTptr->front(),gen(vecteur(res._VECTptr->begin()+1,res._VECTptr->end()),_SEQ__VECT)));
    case FIG_TAG:
      return gensizeerr(gettext("fig_tag"));
    case MAC_TAG:
      return gensizeerr(gettext("mac_tag"));
    case COMMENT_TAG:
      ptr -=2;
      while (!*ptr) --ptr;
      return string2gen(ti_decode_string(ptr,contextptr),false);
    case NEXTEXPR_TAG: case NEWLINE_TAG: // ":" or "\n" inside a prog
      ti_decode_newline(ptr,contextptr);
      return at_nop;
    case PN1_TAG:
      return ti_decode_unary(ptr,at_neg,contextptr);
    case PN2_TAG: 
      cerr << "pn2_tag" << endl;
      return ti_decode_binary(ptr,at_neg,true,contextptr);
    case ERROR_MSG_TAG:
      return ti_decode_not_implemented(ptr,"ErrorMsg",1,contextptr);
    case EIGVC_TAG:
      return ti_decode_unary(ptr,at_eigVc,contextptr);
    case EIGVL_TAG:
      return ti_decode_unary(ptr,at_eigVl,contextptr);
    case DASH_TAG:
      return ti_decode_unary(ptr,at_derive,contextptr);
    case LOCALVAR_TAG:
      --ptr;
      return ti_decode_tag(ptr,contextptr);
    case DESOLVE_TAG:
      return ti_decode_nary(ptr,at_deSolve,3,contextptr);
    case FDASH_TAG:
      return ti_decode_binary(ptr,at_derive,true,contextptr);
    case ISPRIME_TAG:
      return ti_decode_unary(ptr,at_isprime,contextptr);
    case ROTATE_TAG:
      return ti_decode_nary(ptr,at_rotate,1,contextptr);
    default:
      return gensizeerr(gettext("Unknown tag ")+print_INT_(*ptr));
    }
    --ptr;
    return res;
  }

  // convert a TI9x object to a gen
  // ptr points to the beginning of the structure
  gen ti2gen(octet * ptr,GIAC_CONTEXT){
    /*
      if (numeric_limits<unsigned char>::Digits!=8){
      return gensizeerr(gettext("Must recompile with a typedef for octet as 8-bit data"));
      }
    */
    int offset= ptr[0]*256+ptr[1];
    octet * tag = ptr + (offset +1);
    // Check here for text, non tokenized progs, 3rd party data 
    // since length must be known
    if (*tag==TEXT_VAR_TAG){
      char * ptrs=(char *)ptr+4;
      return string2gen(tiasc_translate(ptrs),false);
    }
    if (*tag==USER_DEF_TAG && (*(tag-1) & 0x08)){
      ptr +=2; // point to argument list
      string s(":tmpfunc");
      char c;
      tag -= 8;
      for (;ptr!=tag;++ptr){
	c=*ptr;
	if (*ptr>0x7F){
	  if (-c==87){ // must skip until end of line
	    for (;ptr!=tag;++ptr){
	      if (*ptr=='\r')
		break;
	    }
	  }
	  else
	    s += "Z"+print_INT_(-c);
	}
	else {
	  switch(c){
	  case 0:
	    continue;
	  case '\r':
	    s = (s+'\n')+':';
	    break;
	  case '\026':
	    s += "=>";
	    break;
	  default:
	    s += c;
	  }
	}
      }
      s=tiasc_translate(s);
      cerr << s << endl;
      int save_maple_mode=xcas_mode(contextptr);
      xcas_mode(contextptr)=3;
      gen res(s,contextptr);
      xcas_mode(contextptr)=save_maple_mode;
      return res;
    }
    return ti_decode_tag(tag,contextptr);
  }

  static gen decode_name(octet * buf,GIAC_CONTEXT){
    string lu;
    char c;
    for (int i=0;i<8 && (c=buf[i]);++i)
      lu += c;
    gen gname(lu,contextptr);
    if (gname.type!=_IDNT)
      gname=gen("_"+lu,contextptr);
    return gname;
  }

  // FIXME SECURITY
  // Format [length 2 bytes] 0xE9 prgm 0x19 or 0x13 0xE5 ... 0x0 0x0 0x0 0xDC
  // length is at offset 0x56 in a program file, name at 0x40
  gen _unarchive_ti(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_STRNG)
      return gensizeerr(contextptr);
    if (access(g._STRNGptr->c_str(),R_OK))
      return gensizeerr(gettext("Unable to open ")+g.print(contextptr));
    ifstream is(g._STRNGptr->c_str());
    string lu;
    char c;
    for (;!is.eof();){
      is.get(c);
      lu += c;
    }
    unsigned int s=lu.size();
    if (s<0x60)
      return gensizeerr(gettext("Too short for a TI archive"));
#ifdef VISUALC
    octet *buf=new octet[s]; // FIXME VISUALC delete
#else
    octet buf[s];
#endif
    memcpy(buf,lu.c_str(),s);
    if (lu[6]=='P'){ // package/group file
      unsigned int t=buf[0x3e]*65536+buf[0x3d]*256+buf[0x3c];
      if (s<t)
	return gensizeerr(gettext("Not long enough"));
      unsigned int nprogs=(t-0x52)/0x10;
      if (!nprogs){
	gen res(undef);
	// single program
	gen gname(decode_name(&buf[0x40],contextptr));
	cerr << "Fonction " << gname << endl;
	parser_filename(gname.print(contextptr),contextptr);
	unsigned int tt=buf[0x3e]*65536+buf[0x3d]*256+buf[0x3c]+4;
	if (tt>s)
	  return res;
	unsigned int ttt=buf[tt]*256+buf[tt+1];
	if (s<ttt+tt+2)
	  return res;
	gen gval;
#ifdef NO_STDEXCEPT
	gval=ti2gen(&buf[tt],contextptr);
#else
	try {
	  gval=ti2gen(&buf[tt],contextptr);
	}
	catch (std::runtime_error & e){
	  cerr << gname << ":" << e.what();
	  gval=string2gen(e.what(),false);
	}
#endif
	if (gval.is_symb_of_sommet(at_sto))
	  res=symb_sto(gval._SYMBptr->feuille[0],gname);
	else
	  res=symb_sto(gval,gname);
	return res;
      }
      // Group file format
      // First name is folder at 0x40 offset 0x4c (dir name)
      // List of names 0x50, 0x60, ... offsets 0x5c 0x6c etc.
      // offset + 4 = begin of func/prog, ex. offset=0x1322, length at 0x1326
      gen gfoldername(decode_name(&buf[0x40],contextptr));
      vecteur res;
      if (gfoldername.print(contextptr)!="main"){
	cerr << "Degrouping in folder " << gfoldername << endl;
	res.push_back(symbolic(at_NewFold,gfoldername));
	res.push_back(symbolic(at_SetFold,gfoldername));
      }
      // decode each prog
      for (unsigned int i=0;i<nprogs;++i){
	gen gname(decode_name(&buf[0x50+0x10*i],contextptr));
	cerr << "Fonction " << gname << endl;
	parser_filename(gname.print(contextptr),contextptr);
	unsigned int tt=buf[0x4e +0x10*i]*65536+buf[0x4d+0x10*i]*256+buf[0x4c+0x10*i]+4;
	if (tt>s)
	  return res;
	unsigned int ttt=buf[tt]*256+buf[tt+1];
	if (s<ttt+tt+2)
	  return res;
	gen gval;
#ifdef NO_STDEXCEPT
	gval=ti2gen(&buf[tt],contextptr);
#else
	try {
	  gval=ti2gen(&buf[tt],contextptr);
	}
	catch (std::runtime_error & e){
	  cerr << gname << ":" << e.what();
	  gval=string2gen(e.what(),false);
	}
#endif
	if (gval.is_symb_of_sommet(at_sto))
	  res.push_back(symb_sto(gval._SYMBptr->feuille[0],gname));
	res.push_back(symb_sto(gval,gname));
      }
      return res;
    }
    unsigned int t=buf[0x56]*256+buf[0x57];
    if (s<t+0x56+2)
      return gensizeerr(gettext("Not a TI89/92 program/function/group file"));
    gen gname(decode_name(&buf[0x40],contextptr));
    gen gval(ti2gen(&buf[0x56],contextptr));
    if (gval.is_symb_of_sommet(at_sto))
      return symb_sto(gval._SYMBptr->feuille[0],gname);
    return symb_sto(gval,gname);
  }
#endif //RTOS_THREADX
  static const char _unarchive_ti_s[]="unarchive_ti";
  static define_unary_function_eval (__unarchive_ti,&_unarchive_ti,_unarchive_ti_s);
  define_unary_function_ptr5( at_unarchive_ti ,alias_at_unarchive_ti,&__unarchive_ti,0,true);

#ifdef GIAC_HAS_STO_38
  static const char _fsi_s[]="fsi";
  static define_unary_function_eval_quoted (__fsi,&_ifte,_fsi_s);
  define_unary_function_ptr5( at_fsi ,alias_at_fsi,&__fsi,_QUOTE_ARGUMENTS,T_BLOC_END);

  static const char _ffaire_s[]="ffaire";
  static define_unary_function_eval_quoted (__ffaire,&_for,_ffaire_s);
  define_unary_function_ptr5( at_ffaire ,alias_at_ffaire,&__ffaire,_QUOTE_ARGUMENTS,T_BLOC_END);

  static const char _fpour_s[]="fpour";
  static define_unary_function_eval_quoted (__fpour,&_for,_fpour_s);
  define_unary_function_ptr5( at_fpour ,alias_at_fpour,&__fpour,_QUOTE_ARGUMENTS,T_BLOC_END);
  
  static const char _ftantque_s[]="ftantque";
  static define_unary_function_eval_quoted (__ftantque,&_for,_ftantque_s);
  define_unary_function_ptr5( at_ftantque ,alias_at_ftantque,&__ftantque,_QUOTE_ARGUMENTS,T_BLOC_END);
  
  static const char _ffonction_s[]="ffonction";
  static define_unary_function_eval_quoted (__ffonction,&_for,_ffonction_s);
  define_unary_function_ptr5( at_ffonction ,alias_at_ffonction,&__ffonction,_QUOTE_ARGUMENTS,T_BLOC_END);
#endif

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
