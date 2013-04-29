// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -I../include -g -c usual.cc -Wall -D_I386_ -DHAVE_CONFIG_H -DIN_GIAC -msse" -*-
#include "giacPCH.h"

/*
 *  Copyright (C) 2000,7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#include "gen.h"
#include "identificateur.h"
#include "symbolic.h"
#include "poly.h"
#include "usual.h"
#include "series.h"
#include "modpoly.h"
#include "sym2poly.h"
#include "moyal.h"
#include "subst.h"
#include "gausspol.h"
#include "identificateur.h"
#include "ifactor.h"
#include "prog.h"
#include "rpn.h"
#include "plot.h"
#include "pari.h"
#include "tex.h"
#include "unary.h"
#include "intg.h"
#include "ti89.h"
#include "solve.h"
#include "alg_ext.h"
#include "lin.h"
#include "derive.h"
#include "series.h"
#include "misc.h"
#include "input_parser.h"
#include "giacintl.h"
#ifdef VISUALC
#include <float.h>
#endif
#ifdef HAVE_LIBGSL
#include <gsl/gsl_math.h>
#include <gsl/gsl_sf_gamma.h>
#include <gsl/gsl_sf_psi.h>
#include <gsl/gsl_sf_zeta.h>
#include <gsl/gsl_errno.h>
#include <gsl/gsl_sf_erf.h>
#include <gsl/gsl_sf_expint.h>
#endif
#ifdef TARGET_OS_IPHONE
#include "psi.h"
#endif
#ifdef USE_GMP_REPLACEMENTS
#undef HAVE_GMPXX_H
#undef HAVE_LIBMPFR
#undef HAVE_LIBPARI
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // must be declared before any function declaration with special handling
  vector<const unary_function_ptr *> & limit_tractable_functions(){
    static vector<const unary_function_ptr *> * ans = new vector<const unary_function_ptr *>;
    return * ans;
  }
  vector<gen_op_context> & limit_tractable_replace(){
    static vector<gen_op_context> * ans = new vector<gen_op_context>;
    return * ans;
  }
#ifdef HAVE_SIGNAL_H_OLD
  string messages_to_print ;
#endif

  gen frac_neg_out(const gen & g,GIAC_CONTEXT){
    if ( (is_integer(g) && is_strictly_positive(-g,contextptr)) || (g.type==_FRAC && (g._FRACptr->num.type<=_DOUBLE_ || g._FRACptr->num.type==_FLOAT_) && is_strictly_positive(-g._FRACptr->num,contextptr)) )
      return symbolic(at_neg,-g);
    if (g.is_symb_of_sommet(at_prod)){
      // count neg
      gen f=g._SYMBptr->feuille;
      vecteur fv(gen2vecteur(f));
      int count=0,fvs=fv.size();
      for (int i=0;i<fvs;++i){
	gen & fvi = fv[i];
	fvi=frac_neg_out(fvi,contextptr);
	if (fvi.is_symb_of_sommet(at_neg)){
	  ++count;
	  fvi=fvi._SYMBptr->feuille;
	}
      }
      if (fvs==1)
	f=fv[0];
      else {
	if (f.type==_VECT && *f._VECTptr==fv) // nothing changed
	  f=g;
	else
	  f=symbolic(at_prod,fv);
      }
      if (count%2)
	return symbolic(at_neg,f);
      else
	return f;
    }
    return g;
  }

  // utilities for trig functions
  enum { trig_deno=24 };

  static bool is_multiple_of_12(const gen & k0,int & l){
    if (!k0.is_integer())
      return false;
    gen k=smod(k0,trig_deno);
    if (k.type!=_INT_)
      return false;
    l=k.val+trig_deno/2;
    return true;
  }
  static bool is_multiple_of_pi_over_12(const gen & a,int & l,bool angle_unit,GIAC_CONTEXT){
    if (is_zero(a,contextptr)){
      l=0;
      return true;
    }
    gen k;
    if (angle_unit){
      if (!contains(a,cst_pi))
	return false;
      k=derive(a,cst_pi,contextptr);
      if (is_undef(k) || !is_constant_wrt(k,cst_pi,contextptr) || !is_zero(ratnormal(a-k*cst_pi)))
	return false;
      k=(trig_deno/2)*k;
      if (k.type==_SYMB)
	k=ratnormal(k);
      /*
      gen k1=normal(rdiv(a*gen(trig_deno/2),cst_pi),contextptr);
      if (k!=k1)
	setsizeerr();
      */
    }
    else 
      k=rdiv(a,15,context0);
    return is_multiple_of_12(k,l);
  }

  static bool is_rational(const gen & a,int &n,int &d){
    gen num,den;
    fxnd(a,num,den);
    if (num.type!=_INT_ || den.type!=_INT_)
      return false;
    n=num.val;
    d=den.val;
    return true;
  }
  // checking utility
  static bool check_2d_vecteur(const gen & args) {
    if (args.type!=_VECT)
      return false; // settypeerr(gettext("check_2d_vecteur"));
    if (args._VECTptr->size()!=2)
      return false; // setsizeerr(gettext("check_2d_vecteur"));
    return true;
  }

  // zero arg
  /*
  unary_function_constant __1(1);
  unary_function_ptr at_one (&__1);
  unary_function_constant __0(0);
  unary_function_ptr at_zero (&__0);
  */
  gen _constant_one(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return 1;
  }
  static const char _constant_one_s []="1";
  static define_unary_function_eval (__constant_one,&_constant_one,_constant_one_s);
  define_unary_function_ptr( at_one ,alias_at_one ,&__constant_one);
  
  gen _constant_zero(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return 0;
  }
  static const char _constant_zero_s []="0";
  static define_unary_function_eval (__constant_zero,&_constant_zero,_constant_zero_s);
  define_unary_function_ptr( at_zero ,alias_at_zero ,&__constant_zero);
  
  gen _rm_a_z(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
#ifndef RTOS_THREADX
#ifndef BESTA_OS
    if (variables_are_files(contextptr)){
      char a_effacer[]="a.cas";
      for (;a_effacer[0]<='z';++a_effacer[0]){
	unlink(a_effacer);
      }
    }
#endif
#endif
    for (char c='a';c<='z';c++){
      _purge(gen(string(1,c),contextptr),contextptr);
    }
    return args;
  }
  static const char _rm_a_z_s []="rm_a_z";
  static define_unary_function_eval (__rm_a_z,&_rm_a_z,_rm_a_z_s);
  define_unary_function_ptr5( at_rm_a_z ,alias_at_rm_a_z,&__rm_a_z,0,true);

  gen _rm_all_vars(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen g=_VARS(zero,contextptr);
    if (g.type!=_VECT)
      return g;
    vecteur & v=*g._VECTptr;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type==_IDNT && (*it!=cst_pi) )
	_purge(*it,contextptr);
    }
    return g;
  }
  static const char _rm_all_vars_s []="rm_all_vars";
  static define_unary_function_eval (__rm_all_vars,&_rm_all_vars,_rm_all_vars_s);
  define_unary_function_ptr5( at_rm_all_vars ,alias_at_rm_all_vars,&__rm_all_vars,0,true);

  bool is_equal(const gen & g){
    return (g.type==_SYMB) && (g._SYMBptr->sommet==at_equal);
  }

  gen apply_to_equal(const gen & g,const gen_op & f){
    if (g.type!=_SYMB || g._SYMBptr->sommet!=at_equal || g._SYMBptr->feuille.type!=_VECT)
      return f(g);
    vecteur & v=*g._SYMBptr->feuille._VECTptr;
    if (v.empty())
      return gensizeerr(gettext("apply_to_equal"));
    return symbolic(at_equal,gen(makevecteur(f(v.front()),f(v.back())),_SEQ__VECT));
  }

  gen apply_to_equal(const gen & g,gen (* f) (const gen &, GIAC_CONTEXT),GIAC_CONTEXT){
    if (g.type!=_SYMB || g._SYMBptr->sommet!=at_equal || g._SYMBptr->feuille.type!=_VECT)
      return f(g,contextptr);
    vecteur & v=*g._SYMBptr->feuille._VECTptr;
    if (v.empty())
      return gensizeerr(contextptr);
    return symbolic(at_equal,gen(makevecteur(f(v.front(),contextptr),f(v.back(),contextptr)),_SEQ__VECT));
  }

  // one arg
  gen _id(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return args;
  }
  define_partial_derivative_onearg_genop(D_at_id,"D_at_id",_constant_one);
  static const char _id_s []="id";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3 (__id,&_id,(unsigned long)&D_at_idunary_function_ptr,_id_s);
#else
  static define_unary_function_eval3 (__id,&_id,D_at_id,_id_s);
#endif
  define_unary_function_ptr5( at_id ,alias_at_id,&__id,0,true);

  static string printasnot(const gen & g,const char * s,GIAC_CONTEXT){
    if (abs_calc_mode(contextptr)==38)
      return "NOT "+g.print(contextptr);
    else
      return "not("+g.print(contextptr)+")";
    
  }
  symbolic symb_not(const gen & args){
    return symbolic(at_not,args);
  }
  gen _not(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT)
      return apply(args,_not,contextptr);
    return !equaltosame(args);
  }
  static const char _not_s []="not";
  static define_unary_function_eval2_index (64,__not,&_not,_not_s,&printasnot);
  define_unary_function_ptr( at_not ,alias_at_not ,&__not);

  // static symbolic symb_neg(const gen & args){  return symbolic(at_neg,args); }
  gen _neg(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return -args;
  }
  define_partial_derivative_onearg_genop( D_at_neg,"D_at_neg",_neg);
  static const char _neg_s []="-";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (4,__neg,&_neg,(unsigned long)&D_at_negunary_function_ptr,_neg_s);
#else
  static define_unary_function_eval3_index (4,__neg,&_neg,D_at_neg,_neg_s);
#endif
  define_unary_function_ptr( at_neg ,alias_at_neg ,&__neg);

  symbolic symb_inv(const gen & a){
    return symbolic(at_inv,a);
  }
  gen _inv(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ((args.type!=_VECT) || is_squarematrix(args))
      return inv(args,contextptr);
    iterateur it=args._VECTptr->begin(), itend=args._VECTptr->end();
    gen prod(1);
    for (;it!=itend;++it)
      prod = prod * (*it);
    return inv(prod,contextptr);
  }
  static const char _inv_s []="inv";
  static define_unary_function_eval_index (12,__inv,&_inv,_inv_s);
  define_unary_function_ptr5( at_inv ,alias_at_inv,&__inv,0,true);

  symbolic symb_ln(const gen & e){
    return symbolic(at_ln,e);
  }

  gen ln(const gen & e,GIAC_CONTEXT){
    // if (abs_calc_mode(contextptr)==38 && do_lnabs(contextptr) && !complex_mode(contextptr) && (e.type<=_POLY || e.type==_FLOAT_) && !is_positive(e,contextptr)) return gensizeerr(contextptr);
    if (e.type==_FLOAT_){
#ifdef BCD
      if (!is_positive(e,contextptr))
	return fln(-e._FLOAT_val)+cst_i*cst_pi;
      return fln(e._FLOAT_val);
#else
      return ln(get_double(e._FLOAT_val),contextptr);
#endif
    }
    if (e.type==_DOUBLE_){
      if (e._DOUBLE_val==0)
	return minus_inf;
      if (e._DOUBLE_val>0)
#ifdef _SOFTMATH_H
	return std::giac_gnuwince_log(e._DOUBLE_val);
#else
	return std::log(e._DOUBLE_val);
#endif
      else
#ifdef _SOFTMATH_H
	return M_PI*cst_i+std::giac_gnuwince_log(-e._DOUBLE_val);
#else
	return M_PI*cst_i+std::log(-e._DOUBLE_val);
#endif
    }
    if (e.type==_REAL){
      if (is_positive(e,contextptr))
	return e._REALptr->log();
      else
	return (-e)._REALptr->log()+cst_pi*cst_i;
    }
    if (e.type==_CPLX){ 
      if (e.subtype){
#ifdef _SOFTMATH_H
	return std::giac_gnuwince_log(gen2complex_d(e));
#else
	return std::log(gen2complex_d(e));
#endif
      }
      if (e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_){
	bool b=angle_radian(contextptr);
	angle_radian(true,contextptr);
	gen res=ln(abs(e,contextptr),contextptr)+cst_i*arg(e,contextptr);
	angle_radian(b,contextptr);
	return res;
      }
      if (is_zero(*e._CPLXptr,contextptr)){
	if (is_one(*(e._CPLXptr+1)))
	  return cst_i*cst_pi_over_2;
	if (is_minus_one(*(e._CPLXptr+1)))
	  return -cst_i*cst_pi_over_2;
      }
    }
    if (is_squarematrix(e))
      return analytic_apply(at_ln,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,giac::ln,contextptr);
    if (is_zero(e,contextptr))
      return calc_mode(contextptr)==1?unsigned_inf:minus_inf;
    if (is_one(e))
      return 0;
    if (is_minus_one(e))
      return cst_i*cst_pi;
    if (is_undef(e))
      return e;
    if ( (e==unsigned_inf) || (e==plus_inf))
      return e;
    if (e==minus_inf)
      return unsigned_inf;
    if (is_equal(e))
      return apply_to_equal(e,ln,contextptr);
    if (e.type==_SYMB){
      if (e._SYMBptr->sommet==at_inv && e._SYMBptr->feuille.type!=_VECT)
	return -ln(e._SYMBptr->feuille,contextptr);
      if (e._SYMBptr->sommet==at_exp){ 
	if (is_real(e._SYMBptr->feuille,contextptr) ) 
	  return e._SYMBptr->feuille;
      }
    }
    if (e.type==_FRAC && e._FRACptr->num==1)
      return -ln(e._FRACptr->den,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,ln(b,contextptr)),_SEQ__VECT));
    if (e.is_symb_of_sommet(at_pow) && e._SYMBptr->feuille.type==_VECT && e._SYMBptr->feuille._VECTptr->size()==2){
      gen a=e._SYMBptr->feuille._VECTptr->front();
      gen b=e._SYMBptr->feuille._VECTptr->back();
      // ln(a^b)
      if (is_positive(a,contextptr))
	return b*ln(a,contextptr);
    }
    return symb_ln(e);
  }
  gen log(const gen & e,GIAC_CONTEXT){
    return ln(e,contextptr);
  }
  static const char _ln_s []="ln"; // Using C notation, log works also for natural
  static gen d_ln(const gen & args,GIAC_CONTEXT){
    return inv(args,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_ln,"D_at_ln",&d_ln);
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (18,__ln,&giac::ln,(unsigned long)&D_at_lnunary_function_ptr,_ln_s);
#else
  static define_unary_function_eval3_index (18,__ln,&giac::ln,D_at_ln,_ln_s);
#endif
  define_unary_function_ptr5( at_ln ,alias_at_ln,&__ln,0,true);

  gen log10(const gen & e,GIAC_CONTEXT){
    if (e.type==_FLOAT_) {
      if (is_positive(e,contextptr)){
#ifdef BCD
	return flog10(e._FLOAT_val);
#else
	return log10(get_double(e._FLOAT_val),contextptr);
#endif
      }
      return ln(e,contextptr)/ln(10,contextptr);
    }
    if (e.type==_DOUBLE_ && e._DOUBLE_val>=0 ){
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_log10(e._DOUBLE_val);
#else
      return std::log10(e._DOUBLE_val);
#endif
    }
    if ( e.type==_DOUBLE_ || (e.type==_CPLX && e.subtype)){
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_log(gen2complex_d(e))/std::log(10.0);
#else
      return std::log(gen2complex_d(e))/std::log(10.0);
#endif
    }
    if (e.type==_CPLX && (e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_)){
      return (ln(abs(e,contextptr),contextptr)+cst_i*arg(e,contextptr))/ln(10,contextptr);
    }
    if (is_squarematrix(e))
      return analytic_apply(at_log10,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,giac::log10,contextptr);
    gen a,b;
#ifdef GIAC_HAS_STO_38
    if (has_evalf(e,a,1,contextptr))
      return log10(a,contextptr);
#endif
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,log10(b,contextptr)),_SEQ__VECT));
    int n=0; gen e1(e),q;
    if (is_integer(e1) && !is_zero(e1)){
      while (is_zero(irem(e1,10,q))){
	if (q.type==_ZINT)
	  e1=*q._ZINTptr;
	else
	  e1=q;
	++n;
      }
    }
    return rdiv(ln(e1,contextptr),ln(10,contextptr),contextptr)+n;
  }
  static const char _log10_s []="log10"; // Using C notation, log for natural
  static gen d_log10(const gen & args,GIAC_CONTEXT){
    return inv(args*ln(10,contextptr),contextptr);
  }
  define_partial_derivative_onearg_genop(D_at_log10,"D_at_log10",&d_log10);
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3 (__log10,&giac::log10,(unsigned long)&D_at_log10unary_function_ptr,_log10_s);
#else
  static define_unary_function_eval3 (__log10,&giac::log10,D_at_log10,_log10_s);
#endif
  define_unary_function_ptr5( at_log10 ,alias_at_log10,&__log10,0,true);

  gen alog10(const gen & e,GIAC_CONTEXT){
#ifdef BCD
    if (e.type==_FLOAT_)
      return falog10(e._FLOAT_val);
#endif
    if (is_squarematrix(e))
      return analytic_apply(at_alog10,*e._VECTptr,0);
    if (e.type==_VECT)
      return apply(e,contextptr,giac::alog10);
    if (is_equal(e))
      return apply_to_equal(e,alog10,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,alog10(b,contextptr)),_SEQ__VECT));
    return pow(gen(10),e,contextptr);
  }
  static const char _alog10_s []="alog10"; 
  static define_unary_function_eval (__alog10,&giac::alog10,_alog10_s);
  define_unary_function_ptr5( at_alog10 ,alias_at_alog10,&__alog10,0,true);

  symbolic symb_atan(const gen & e){
    return symbolic(at_atan,e);
  }
  static gen atanasln(const gen & e,GIAC_CONTEXT){
    return plus_one_half*cst_i*ln(rdiv(cst_i+e,cst_i-e,contextptr),contextptr);
  }
  gen atan(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_)
#ifdef BCD
      return fatan(e0._FLOAT_val,angle_mode(contextptr));
#else
      return atan(get_double(e0._FLOAT_val),contextptr);
#endif
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_DOUBLE_){
#ifdef _SOFTMATH_H
      double res=std::giac_gnuwince_atan(e._DOUBLE_val);
#else
      double res=std::atan(e._DOUBLE_val);
#endif
      if (angle_radian(contextptr)) 
	return res;
      else
	return res*rad2deg_d;
    }
    if (e.type==_REAL){
      if (angle_radian(contextptr)) 
	return e._REALptr->atan();
      else
	return 180*e._REALptr->atan()/cst_pi;
    }
    if ( (e.type==_CPLX) && (e.subtype || e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_)){
      if (angle_radian(contextptr)) 
	return no_context_evalf(atanasln(e,contextptr));
      else
	return no_context_evalf(atanasln(e,contextptr))*gen(rad2deg_d);
    }
    if (is_squarematrix(e))
      return analytic_apply(at_atan,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,giac::atan,contextptr);
    if (is_zero(e,contextptr))
      return e;
    if (is_one(e)){
      if (angle_radian(contextptr)) 
	return rdiv(cst_pi,4,contextptr);
      return 45;
    }
    if (is_minus_one(e)){
      if (angle_radian(contextptr)) 
	return rdiv(-cst_pi,4,contextptr);
      return -45;
    }
    if (e==plus_sqrt3_3){
      if (angle_radian(contextptr)) 
	return rdiv(cst_pi,6,contextptr);
      return 30;
    }
    if (e==plus_sqrt3){
      if (angle_radian(contextptr)) 
	return rdiv(cst_pi,3,contextptr);
      return 60;
    }
    if (e==plus_inf){
      if (angle_radian(contextptr)) 
	return cst_pi_over_2;
      return 90;
    }
    if (e==minus_inf){
      if (angle_radian(contextptr)) 
	return -cst_pi_over_2;
      return -90;
    }
    if (is_undef(e))
      return e;
    if (e==unsigned_inf)
      return undef;
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,atan(b,contextptr)),_SEQ__VECT));
    gen tmp=evalf_double(e,0,contextptr);
    if (tmp.type==_DOUBLE_){
      gen tmp2=normal(2*e/(1-e*e),contextptr);
      if (is_one(tmp2)){
	if (angle_radian(contextptr)) 
	  return tmp._DOUBLE_val>0?rdiv(cst_pi,8,contextptr):rdiv(-3*cst_pi,8,contextptr);
	return tmp._DOUBLE_val>0?fraction(45,2):fraction(-145,2);
      }
      if (is_minus_one(tmp2)){
	if (angle_radian(contextptr)) 
	  return tmp._DOUBLE_val>0?rdiv(3*cst_pi,8,contextptr):rdiv(-cst_pi,8,contextptr);
	return tmp._DOUBLE_val>0?fraction(145,2):fraction(-45,2);
      }
      if (tmp2==plus_sqrt3_3){
	if (angle_radian(contextptr)) 
	  return tmp._DOUBLE_val>0?rdiv(cst_pi,12,contextptr):rdiv(-5*cst_pi,12,contextptr);
	return tmp._DOUBLE_val>0?15:-165;
      }
      if (tmp2==rdiv(minus_sqrt3,3,contextptr)){
	if (angle_radian(contextptr)) 
	  return tmp._DOUBLE_val>0?rdiv(5*cst_pi,12,contextptr):rdiv(-cst_pi,12,contextptr);
	return tmp._DOUBLE_val>0?165:-15;
      }
    }
    if ((e.type==_SYMB) && (e._SYMBptr->sommet==at_neg))
      return -atan(e._SYMBptr->feuille,contextptr);
    if ( (e.type==_INT_) && (e.val<0) )
      return -atan(-e,contextptr);
    if (is_equal(e))
      return apply_to_equal(e,atan,contextptr);
    if (e.is_symb_of_sommet(at_tan)){
      gen tmp=cst_pi;
      if (!angle_radian(contextptr))
	tmp=180;
      gen tmp2=evalf(e._SYMBptr->feuille,1,contextptr);
      tmp2=_floor(tmp2/tmp+plus_one_half,contextptr);
      if (tmp2.type==_FLOAT_)
	tmp2=get_int(tmp2._FLOAT_val);
      return operator_minus(e._SYMBptr->feuille,tmp2*tmp,contextptr);
    }
    return symb_atan(e);
  }
  static gen d_atan(const gen & args,GIAC_CONTEXT){
    gen g=inv(1+pow(args,2),contextptr);
    if (angle_radian(contextptr))
      return g;
    return g*deg2rad_e;
  }
  define_partial_derivative_onearg_genop( D_at_atan," D_at_atan",&d_atan);
  static gen taylor_atan (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0; // no symbolic preprocessing
    shift_coeff=0;
    if (!is_inf(lim_point))
      return taylor(lim_point,ordre,f,0,shift_coeff,contextptr);
    vecteur v;
    identificateur x(" ");
    taylor(atan(x,contextptr),x,0,ordre,v,contextptr);
    v=negvecteur(v);
    v.front()=atan(lim_point,contextptr);
    return v;
  }
  static const char _atan_s []="atan";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor_index (42,__atan,&giac::atan,(unsigned long)&D_at_atanunary_function_ptr,&taylor_atan,_atan_s);
#else
  static define_unary_function_eval_taylor_index (42,__atan,&giac::atan,D_at_atan,&taylor_atan,_atan_s);
#endif
  define_unary_function_ptr5( at_atan ,alias_at_atan,&__atan,0,true);

  symbolic symb_exp(const gen & e){
    return symbolic(at_exp,e);
  }
  static gen numeric_matrix_exp(const gen & e,double eps,GIAC_CONTEXT){
    gen res=midn(e._VECTptr->size());
    gen eee(e);
    for (double j=2;j<max_numexp && linfnorm(eee,contextptr)._DOUBLE_val>eps;++j){
      res = res + eee;
      eee = gen(1/j) * eee * e ; 
    }
    return res;
  }

  gen exp(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_){
#ifdef BCD
      return fexp(e0._FLOAT_val);
#else
      return exp(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    if (is_integer(e0) && is_strictly_greater(0,e0,contextptr))
      return symb_inv(symb_exp(-e0));
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_SPOL1)
      return symb_exp(e);
    if (e.type==_DOUBLE_){
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_exp(e._DOUBLE_val);
#else
      return std::exp(e._DOUBLE_val);
#endif
    }
    if (e.type==_REAL)
      return e._REALptr->exp();
    if (e.type==_CPLX){ 
      if (e.subtype){
#ifdef _SOFTMATH_H
	return std::giac_gnuwince_exp(gen2complex_d(e));
#else
	return std::exp(gen2complex_d(e));
#endif
      }
      if (e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_){
	bool b=angle_radian(contextptr);
	angle_radian(true,contextptr);
	gen res=exp(*e._CPLXptr,contextptr)*(cos(*(e._CPLXptr+1),contextptr)+cst_i*sin(*(e._CPLXptr+1),contextptr));
	angle_radian(b,contextptr);
	return res;
      }
    }
    if (e.type==_VECT){
      if (is_squarematrix(e)){ 
	// check for numeric entries -> numeric exp
	if (is_fully_numeric(e))
	  return numeric_matrix_exp(e,epsilon(contextptr),contextptr);
	return analytic_apply(at_exp,*e._VECTptr,contextptr);
      }
      return apply(e,contextptr,giac::exp);
    }
    if (is_zero(e,contextptr))
      return 1;
    if (is_undef(e) || e==plus_inf)
      return e;
    if (e==unsigned_inf)
      return undef;
    if (e==minus_inf)
      return 0;
    if (e.type==_SYMB && e._SYMBptr->sommet==at_ln)
      return e._SYMBptr->feuille;
    if (e.type==_SYMB && e._SYMBptr->sommet==at_neg && e._SYMBptr->feuille.type==_SYMB && e._SYMBptr->feuille._SYMBptr->sommet==at_ln)
      return inv(e._SYMBptr->feuille._SYMBptr->feuille,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,exp(b,contextptr)),_SEQ__VECT));
    int k;
    if (contains(e,cst_pi)){ // if (!approx_mode(contextptr)) 
      gen a,b;
      if (is_linear_wrt(e,cst_pi,a,b,contextptr) && !is_zero(a)){ 
	if (is_multiple_of_12(a*cst_i*gen(trig_deno/2),k))
	  return (*table_cos[k]+cst_i*(*table_cos[(k+6)%24]))*exp(b,contextptr);
	else {
	  gen kk;
	  kk=normal(a*cst_i,contextptr);
	  if (is_assumed_integer(kk,contextptr)){ 
	    if (is_assumed_integer(normal(rdiv(kk,plus_two,contextptr),contextptr),contextptr))
	      return exp(b,contextptr);
	    else
	      return pow(minus_one,kk,contextptr)*exp(b,contextptr);
	  }
	  int n,d,q,r;
	  if (is_rational(kk,n,d) && d<7){ 
	    q=-n/d;
	    r=-n%d;
	    if (q%2)
	      q=-1;
	    else
	      q=1;
	    if (d<0){ r=-r; d=-d; }
	    if (r<0) r += 2*d;
	    // exp(r*i*pi/d) -> use rootof([1,..,0],cyclotomic(2*d))
	    vecteur vr(r+1);
	    vr[0]=1;
	    vecteur vc(cyclotomic(2*d));
	    if (!is_undef(vc))
	      return q*symb_rootof(vr,vc,contextptr)*exp(b,contextptr);
	    // initially it was return q*symb_exp(r*(cst_pi*cst_i/d));
	  }
	} // end else multiple of pi/12
      } // end is_linear_wrt
    } // end if contains(e,_IDNT_pi)
    if (is_equal(e))
      return apply_to_equal(e,exp,contextptr);
    return symb_exp(e);
  }
  define_partial_derivative_onearg_genop( D_at_exp,"D_at_exp",giac::exp);
  static gen taylor_exp (const gen & lim_point,const int ordre,const unary_function_ptr & f,int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0; // no symbolic preprocessing
    shift_coeff=0;
    gen image=f(lim_point,contextptr); // should simplify if contains i*pi
    vecteur v(1,image);
    if (is_undef(image))
      return v;
    gen factorielle(1);
    for (int i=1;i<=ordre;++i,factorielle=factorielle*gen(i))
      v.push_back(rdiv(image,factorielle,contextptr));
    v.push_back(undef);
    return v;
  }
  static const char _exp_s []="exp";
  static string texprintasexp(const gen & g,const char * s,GIAC_CONTEXT){
    return "e^{"+gen2tex(g,contextptr)+"}";
  }
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor2_index(16,__exp,&giac::exp,(unsigned long)&D_at_expunary_function_ptr,&taylor_exp,_exp_s,0,&texprintasexp);
#else
  static define_unary_function_eval_taylor2_index(16,__exp,&giac::exp,D_at_exp,&taylor_exp,_exp_s,0,&texprintasexp);
#endif
  define_unary_function_ptr5( at_exp ,alias_at_exp,&__exp,0,true);

  // static symbolic symb_sqrt(const gen & e){  return symbolic(at_sqrt,e);  }

  void zint2simpldoublpos(const gen & e,gen & simpl,gen & doubl,bool & pos,int d,GIAC_CONTEXT){
    simpl=1;
    doubl=1;
    if (!is_integer(e)){
      pos=true;
      simpl=e;
      return;
    }
    gen e_copy;
    pos=ck_is_positive(e,context0); // ok
    if (!pos)
      e_copy=-e;
    else
      e_copy=e;
    if (is_zero(e)){
      simpl=e;
      return;
    }
#ifdef GIAC_HAS_STO_38 
    vecteur u(pfacprem(e_copy,true,contextptr));
#else
    vecteur u(ifactors(e_copy,contextptr));
#endif
    // *logptr(contextptr) << u.size() << endl;
    gen f;
    int m,k;
    const_iterateur it=u.begin(),itend=u.end();
    for (;it!=itend;++it){
      f=*it;
      ++it;
      m=it->val;
      if (m%d)
	simpl = simpl*pow(f,m%d,contextptr);
      for (k=0;k<m/d;++k)
	doubl = doubl*f;
    }
  }

  // simplified sqrt without taking care of sign
  gen sqrt_noabs(const gen & e,GIAC_CONTEXT){
    identificateur tmpx(" x");
    vecteur w=solve(tmpx*tmpx-e,tmpx,1,contextptr); 
    if (lidnt(w).empty())
      w=protect_sort(w,contextptr);
    if (w.empty())
      return gensizeerr(gettext("sqrt_noabs of ")+e.print(contextptr));
    return w.back();
  }

  static float fsqrt(float f){
    return std::sqrt(f);
  }
  static gen sqrt_mod_pn(const gen & a0,const gen & p,const gen & n,gen & pn,GIAC_CONTEXT){
    pn=pow(p,n,context0);
    gen a(a0);
    int l=legendre(a,p);
    if (l==-1)
      return undef;
    gen res;
    if (n.type!=_INT_ || n.val<1)
      return undef;
    int N=n.val;
    gen pdiv2=1;
    if (p==2){
      for (;N>=2 && smod(a,2)==0;pdiv2=2*pdiv2){
	if (smod(a,4)!=0)
	  return undef;
	a=a/4;
	N-=2;
      }
      if (N==1)
	return smod(a,2)*pdiv2;
      // now a is odd
      if (N==2){
	if (is_one(smod(a,4)))
	  return pdiv2;
	return undef;
      }
      // find x square root of a modulo 8 then Hensel lift
      gen x=smod(a,8);
      if (x!=1)
	return undef;
      gen powk=8;
      for (int Nn=3;;Nn=2*Nn-1){
	// assume x^2=a mod 2^k, then find y / (x+2^k*y)^2=x^2+2^(k+1)*y*x=a mod 2^(2k)
	// => y=[(a-x^2)/2^(k+1)]/x mod 2^(k-1)
	gen y=(a-x*x)/powk;
	powk=powk/2;
	y=y*invmod(x,powk);
	x=x+powk*y;
	powk=powk*powk;
	x=smod(x,powk);
	if (Nn>N)
	  break;
      }
      return smod(x*pdiv2,pn);
    }
    if (is_zero(smod(a,p)))
      res=0;
    else {
      if (is_zero(smod(p+1,4)))
	res=powmod(smod(a,p),(p+1)/4,p);
      else {
	// could use Shank-Tonneli algorithm, here use gcd(x^2-a,powmod(x+rand,(p-1)/2,p,x^2-p)-1) to split x^2- in 2 parts with proba 1/2
	environment env;
	env.moduloon=true;
	env.modulo=p;
	modpoly A(3),B(2,1),C,D;
	A[0]=1; A[2]=-a; 
	while (true){
	  gen r=smod(gen(giac_rand(contextptr)),p);
	  B[1]=r;
	  D=powmod(B,(p-1)/2,A,&env);
	  D.back()=D.back()-1;
	  if (is_zero(D.front()))
	    continue;
	  gcdmodpoly(A,D,&env,C);
	  if (C.size()==2){
	    res=C[1];
	    break;
	  }
	}
      }
    }
    if (n.val>1){
      // Hensel lift res mod p^n
      pn=p;
      gen invmodu=invmod(2*res,p);
      for (int i=1;i<n.val;++i){
	res=res+pn*(smod((a-res*res)/pn*invmodu,p));
	pn=pn*p;
      }
    }
    return res;
  }
  gen sqrt_mod(const gen & a,const gen & b,bool isprime,GIAC_CONTEXT){
    if (!is_integer(b))
      return gensizeerr(contextptr);
    if (is_one(a) || is_zero(a))
      return a;
    if (b.type==_INT_){
      int A=smod(a,b).val,p=b.val;
      if (A<0) A+=p;
      if (A==0 || A==1) return A;
      if (isprime && p>1024 && (p+1)%4==0){
	A=powmod(A,(unsigned long)((p+1)/4),p);
	if (A>p-A)
	  A=p-A;
	return A;
      }
      if (p<65536){
	int sq=0,add=1;
	for (;add<=p;add+=2){
	  sq+=add;
	  if (sq>=p)
	    sq-=p;
	  if (sq==A)
	    return add/2+1;
	}
	return undef;
      }
    }
    int l=legendre(a,b);
    if (l==-1)
      return undef;
    vecteur v=ifactors(b,contextptr);
    gen oldres(0),pip(1);
    for (unsigned i=0;i<v.size()/2;++i){
      gen p=v[2*i],n=v[2*i+1],pn;
      gen res=sqrt_mod_pn(a,p,n,pn,contextptr);
      if (is_undef(res))
	return res;
      // ichinrem step
      if (i)
	oldres=ichinrem(oldres,res,pip,pn);
      else
	oldres=res;
      pip=pip*pn;
    }
    if (is_positive(-oldres,contextptr))
      oldres=-oldres;
    pip=b-oldres;
    if (is_greater(oldres,pip,contextptr))
      oldres=pip;
    return oldres;
  }

  gen sqrt(const gen & e,GIAC_CONTEXT){
    // if (abs_calc_mode(contextptr)==38 && do_lnabs(contextptr) &&!complex_mode(contextptr) && (e.type<=_POLY || e.type==_FLOAT_) && !is_positive(e,contextptr)) return gensizeerr(contextptr);
    if (e.type==_FLOAT_){
      if (fsign(e._FLOAT_val)==1)
	return fsqrt(e._FLOAT_val);
      if (is_zero(e,contextptr))
	return e;
      return fsqrt(-e._FLOAT_val)*cst_i;
    }
    if (e.type==_DOUBLE_){
      if (e._DOUBLE_val>=0){
#ifdef _SOFTMATH_H
	return std::giac_gnuwince_sqrt(e._DOUBLE_val);
#else
	return std::sqrt(e._DOUBLE_val);
#endif
      }
      else
#ifdef _SOFTMATH_H
	return gen(0.0,std::giac_gnuwince_sqrt(-e._DOUBLE_val));
#else
	return gen(0.0,std::sqrt(-e._DOUBLE_val));
#endif
    }
    if (e.type==_REAL){
      if (is_strictly_positive(-e,contextptr))
	return cst_i*sqrt(-e,contextptr);
      return e._REALptr->sqrt();
    }
    if (e.type==_USER){
      return e._USERptr->sqrt(contextptr);
    }
    gen a,b;
    if (e.type==_MOD){
      a=*e._MODptr;
      b=*(e._MODptr+1);
      a=sqrt_mod(a,b,false,contextptr);
      if (is_undef(a))
	return a;
      if (is_positive(-a,contextptr))
	a=-a;
      return makemod(a,b);
    }
    if (e.type==_CPLX){
      if (e.subtype){
#ifdef _SOFTMATH_H
	return std::giac_gnuwince_sqrt(gen2complex_d(e));
#else
	return std::sqrt(gen2complex_d(e));
#endif
      }
      // sqrt of an exact complex number
      // sqrt of an exact complex number
      a=re(e,contextptr);b=im(e,contextptr);
      if (is_zero(b,contextptr))
	return sqrt(a,contextptr);
      gen rho=sqrt(a*a+b*b,contextptr);
      gen realpart=normalize_sqrt(sqrt(2*(a+rho),contextptr),contextptr);
      return ratnormal(realpart/2)*(1+cst_i*b/(a+rho));
    }
    if (e.type==_VECT)
      return apply(e,giac::sqrt,contextptr);
    if (is_zero(e) || is_undef(e) || (e==plus_inf) || (e==unsigned_inf))
      return e;
    if (is_perfect_square(e))
      return isqrt(e);
    if (e.type==_INT_ || e.type==_ZINT){ 
      // factorization 
      if (e.type==_INT_ && e.val>0){
	switch (e.val){
	case 2:
	  return plus_sqrt2;
	case 3:
	  return plus_sqrt3;
	case 6:
	  return plus_sqrt6;
	}
      }
      bool pos;
      zint2simpldoublpos(e,a,b,pos,2,contextptr);
      if (!pos)
	return (a==1)?cst_i*b:cst_i*b*symbolic(at_pow,gen(makevecteur(a,plus_one_half),_SEQ__VECT));
      else
	return b*symbolic(at_pow,gen(makevecteur(a,plus_one_half),_SEQ__VECT));
    }
    if (e.type==_FRAC)
      return sqrt(e._FRACptr->num*e._FRACptr->den,contextptr)/abs(e._FRACptr->den,contextptr);
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,sqrt(b,contextptr)),_SEQ__VECT));
    if (e.is_symb_of_sommet(at_inv))
      return inv(sqrt(e._SYMBptr->feuille,contextptr),contextptr);
    return pow(e,plus_one_half,contextptr);
  }
  static gen d_sqrt(const gen & e,GIAC_CONTEXT){
    return inv(gen(2)*sqrt(e,contextptr),contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_sqrt," D_at_sqrt",&d_sqrt);
  static const char _sqrt_s []="sqrt";
  static string printassqrt(const gen & g,const char * s,GIAC_CONTEXT){
    bool need=need_parenthesis(g);
    if (abs_calc_mode(contextptr)==38)
      return (need?"√(":"√")+g.print(contextptr)+(need?")":"");
    else
      return "sqrt("+g.print(contextptr)+")";
  }
  static string texprintassqrt(const gen & g,const char * s,GIAC_CONTEXT){
    return "\\sqrt{"+gen2tex(g,contextptr)+"}";
  }
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval5 (__sqrt,&giac::sqrt,(unsigned long)&D_at_sqrtunary_function_ptr,_sqrt_s,&printassqrt,&texprintassqrt);
#else
  static define_unary_function_eval5 (__sqrt,&giac::sqrt,D_at_sqrt,_sqrt_s,&printassqrt,&texprintassqrt);
#endif
  define_unary_function_ptr5( at_sqrt ,alias_at_sqrt,&__sqrt,0,true);

  gen _sq(const gen & e,GIAC_CONTEXT){
    if ( e.type==_STRNG && e.subtype==-1) return  e;
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,_sq(b,contextptr)),_SEQ__VECT));
    return pow(e,2,contextptr);
  }
  static gen d_sq(const gen & e,GIAC_CONTEXT){
    return gen(2)*e;
  }
  define_partial_derivative_onearg_genop( D_at_sq," D_at_sq",&d_sq);
  static const char _sq_s []="sq";
  // static string texprintassq(const gen & g,const char * s,GIAC_CONTEXT){  return gen2tex(g,contextptr)+"^2";}
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (158,__sq,(const gen_op_context)giac::_sq,(unsigned long)&D_at_squnary_function_ptr,_sq_s);
#else
  static define_unary_function_eval3_index (158,__sq,(const gen_op_context)giac::_sq,D_at_sq,_sq_s);
#endif
  define_unary_function_ptr5( at_sq ,alias_at_sq,&__sq,0,true);

  symbolic symb_cos(const gen & e){
    return symbolic(at_cos,e);
  }
  gen cos(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_){
#ifdef BCD
      return fcos(e0._FLOAT_val,angle_mode(contextptr));
#else
      return cos(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_SPOL1)
      return symb_cos(e);
    if (e.type==_DOUBLE_){
      double d;
      if (angle_radian(contextptr)) 
	d=e._DOUBLE_val;
      else
	d=e._DOUBLE_val*deg2rad_d;
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_cos(d);
#else
      return std::cos(d);
#endif
    }	
    if (e.type==_REAL){
      if (angle_radian(contextptr)) 
	return e._REALptr->cos();
      else
	return ((e*cst_pi)/180)._REALptr->cos();
    }
    if (e.type==_CPLX){ 
      if (e.subtype){
	complex_double d;
	if (angle_radian(contextptr)) 
	  d=gen2complex_d(e);
	else
	  d=gen2complex_d(e)*deg2rad_d;
#ifdef _SOFTMATH_H
	return std::giac_gnuwince_cos(d);
#else
	return std::cos(d);
#endif
      }
      if (e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_){
	gen e1=e;
	if (!angle_radian(contextptr)) 
	  e1=e*deg2rad_g;
	gen e2=im(e1,contextptr);
	e1=re(e1,contextptr);
	bool tmp=angle_radian(contextptr);
	angle_radian(true,contextptr);
	e1= cos(e1,contextptr)*cosh(e2,contextptr)-cst_i*sinh(e2,contextptr)*sin(e1,contextptr);
	angle_radian(tmp,contextptr);
	return e1;
      }
    }
    if (is_squarematrix(e))
      return analytic_apply(at_cos,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,giac::cos,contextptr);
    if (is_zero(e,contextptr))
      return 1;
    if ( (e.type==_INT_) && (e.val<0) )
      return cos(-e,contextptr);
    if (is_undef(e))
      return e;
    if (is_inf(e))
      return undef;
    int k;
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,cos(b,contextptr)),_SEQ__VECT));
    bool doit=false,est_multiple;
    if (angle_radian(contextptr)){
      if (contains(e,cst_pi) && is_linear_wrt(e,cst_pi,a,b,contextptr) && !is_zero(a)){
	est_multiple=is_multiple_of_12(a*gen(trig_deno/2),k);
	doit=true;
      }
    }
    else {
      est_multiple=is_multiple_of_pi_over_12(e,k,angle_radian(contextptr),contextptr);
      doit=est_multiple;
    }
    if (doit){ 
      if (est_multiple){
	if (is_zero(b))
	  return *table_cos[k];
	gen C=cos(b,contextptr),S=sin(b,contextptr);
	if (k%6==0 || C.type!=_SYMB || S.type!=_SYMB)
	  return (*table_cos[k])*C+(*table_cos[(k+6)%24])*S;
      }
      else {
	if (is_assumed_integer(a,contextptr)){
	  if (is_assumed_integer(normal(rdiv(a,plus_two,contextptr),contextptr),contextptr))
	    return cos(b,contextptr);
	  else
	    return pow(minus_one,a,contextptr)*cos(b,contextptr);
	}
	int n,d,q,r;
	if (is_zero(b,contextptr) && is_rational(a,n,d)){
	  q=n/d;
	  r=n%d;
	  if (r>d/2){
	    r -= d;
	    ++q;
	  }
	  if (q%2)
	    q=-1;
	  else
	    q=1;
	  if (r<0)
	    r=-r;
	  if (!(d%2) && d%4){ 
	    d=d/2; // cos(r/(2*d)*pi) = sin(pi/2(1-r/d))
	    if (angle_radian(contextptr)) 
	      return -q*sin((r-d)/2*cst_pi/d,contextptr);
	    else 
	      return -q*sin(rdiv((r-d)*90,d,contextptr),contextptr);
	  }
	  if (angle_radian(contextptr)) 
	    return q*symb_cos(r*cst_pi/d);
	  else
	    return q*symb_cos(rdiv(r*180,d,contextptr));
	}
      }
    }
    if (e.type==_SYMB) {
      unary_function_ptr u=e._SYMBptr->sommet;
      gen f=e._SYMBptr->feuille;
      if (u==at_neg)
	return cos(f,contextptr);
      if (u==at_acos)
	return f;
      if (u==at_asin)
	return sqrt(1-pow(f,2),contextptr);
      if (u==at_atan)
	return sqrt(inv(pow(f,2)+1,contextptr),contextptr);
    }
    if (is_equal(e))
      return apply_to_equal(e,cos,contextptr);
    return symb_cos(e);
  }
  static gen d_cos(const gen & e ,GIAC_CONTEXT){
    if (angle_radian(contextptr)) 
      return -(sin(e,contextptr));
    else
      return -deg2rad_e*sin(e,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_cos," D_at_cos",d_cos);
  static const char _cos_s []="cos";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (34,__cos,&giac::cos,(unsigned long)&D_at_cosunary_function_ptr,_cos_s);
#else
  static define_unary_function_eval3_index (34,__cos,&giac::cos,D_at_cos,_cos_s);
#endif
  define_unary_function_ptr5( at_cos ,alias_at_cos,&__cos,0,true);

  symbolic symb_sin(const gen & e){
    return symbolic(at_sin,e);
  }
  gen sin(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_){
#ifdef BCD
      return fsin(e0._FLOAT_val,angle_mode(contextptr));
#else
      return sin(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_SPOL1)
      return symb_sin(e);
    if (e.type==_DOUBLE_){
      double d;
      if (angle_radian(contextptr)) 
	d=e._DOUBLE_val;
      else
	d=e._DOUBLE_val*deg2rad_d;
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_sin(d);
#else
      return std::sin(d);
#endif
    }	
    if (e.type==_REAL){
      if (angle_radian(contextptr)) 
	return e._REALptr->sin();
      else
	return ((e*cst_pi)/180)._REALptr->sin();
    }
    if (e.type==_CPLX){ 
      if (e.subtype){
	complex_double d;
	if (angle_radian(contextptr)) 
	  d=gen2complex_d(e);
	else
	  d=gen2complex_d(e)*deg2rad_d;
#ifdef _SOFTMATH_H
	return std::giac_gnuwince_sin(d);
#else
	return std::sin(d);
#endif
      }
      if (e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_){
	gen e1=e;
	if (!angle_radian(contextptr)) 
	  e1=e*deg2rad_g;
	gen e2=im(e1,contextptr);
	e1=re(e1,contextptr);
	bool tmp=angle_radian(contextptr);
	angle_radian(true,contextptr);
	gen res=sin(e1,contextptr)*cosh(e2,contextptr)+cst_i*sinh(e2,contextptr)*cos(e1,contextptr);
	angle_radian(tmp,contextptr);
	return res;
      }
    }
    if (is_squarematrix(e))
      return analytic_apply(at_sin,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,giac::sin,contextptr);
    if (is_zero(e,contextptr))
      return e;
    if ( (e.type==_INT_) && (e.val<0) )
      return -sin(-e,contextptr);
    if (is_undef(e))
      return e;
    if (is_inf(e))
      return undef;
    int k;
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,sin(b,contextptr)),_SEQ__VECT));
    bool doit=false,est_multiple;
    if (angle_radian(contextptr)){
      if (contains(e,cst_pi) && is_linear_wrt(e,cst_pi,a,b,contextptr) && !is_zero(a)){
	est_multiple=is_multiple_of_12(a*gen(trig_deno/2),k);
	doit=true;
      }
    }
    else {
      est_multiple=is_multiple_of_pi_over_12(e,k,angle_radian(contextptr),contextptr);
      doit=est_multiple;
    }
    if (doit){ 
      if (est_multiple){
	if (is_zero(b))
	  return *table_cos[(k+18)%24];
	gen C=cos(b,contextptr),S=sin(b,contextptr);
	if (k%6==0 || C.type!=_SYMB || S.type!=_SYMB)
	  return *table_cos[(k+18)%24]*C+(*table_cos[k%24])*S;
      }
      else {
	if (is_assumed_integer(a,contextptr)){
	  if (is_assumed_integer(normal(a/2,contextptr),contextptr))
	    return sin(b,contextptr);
	  else
	    return pow(minus_one,a,contextptr)*sin(b,contextptr);
	}
	int n,d,q,r;
	if (is_zero(b,contextptr) && is_rational(a,n,d)){
	  q=n/d;
	  r=n%d;
	  if (r>d/2){
	    r -= d;
	    ++q;
	  }
	  if (q%2)
	    q=-1;
	  else
	    q=1;
	  if (r<0){
	    r=-r;
	    q=-q;
	  }
	  if (!(d%2) && d%4){ 
	    d=d/2; // sin(r/(2*d)*pi) = cos(pi/2(1-r/d))
	    if (angle_radian(contextptr))
	      return q*cos((r-d)/2*cst_pi/d,contextptr);
	    else
	      return q*cos(rdiv((r-d)*90,d,contextptr),contextptr);
	  }
	  if (angle_radian(contextptr)) 
	    return q*symb_sin(r*cst_pi/d);
	  else
	    return q*symb_sin(rdiv(r*180,d,contextptr));
	}
      }
    }
    if (e.type==_SYMB) {
      unary_function_ptr u=e._SYMBptr->sommet;
      gen f=e._SYMBptr->feuille;
      if (u==at_neg)
	return -sin(f,contextptr);
      if (u==at_asin)
	return f;
      if (u==at_acos)
	return sqrt(1-pow(f,2),contextptr);
      if (u==at_atan)
	return rdiv(f,sqrt(pow(f,2)+1,contextptr),contextptr);
    }
    if (is_equal(e))
      return apply_to_equal(e,sin,contextptr);
    return symb_sin(e);
  }
  static gen d_sin(const gen & g,GIAC_CONTEXT){
    if (angle_radian(contextptr)) 
      return cos(g,contextptr);
    else
      return deg2rad_e*cos(g,contextptr);
  }
  static const char _sin_s []="sin";
  define_partial_derivative_onearg_genop( D_at_sin," D_at_sin",&d_sin);
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (32,__sin,&giac::sin,(unsigned long)&D_at_sinunary_function_ptr,_sin_s);
#else
  static define_unary_function_eval3_index (32,__sin,&giac::sin,D_at_sin,_sin_s);
#endif
  define_unary_function_ptr5( at_sin ,alias_at_sin,&__sin,0,true);

  symbolic symb_tan(const gen & e){
    return symbolic(at_tan,e);
  }
  gen tan(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_){
#ifdef BCD
      return ftan(e0._FLOAT_val,angle_mode(contextptr));
#else
      return tan(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_DOUBLE_){
      double d;
      if (angle_radian(contextptr)) 
	d=e._DOUBLE_val;
      else
	d=e._DOUBLE_val*deg2rad_d;
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_tan(d);
#else
      return std::tan(d);
#endif
    }	
    if (e.type==_REAL){
      if (angle_radian(contextptr)) 
	return e._REALptr->tan();
      else
	return ((e*cst_pi)/180)._REALptr->tan();
    }
    if (e.type==_CPLX){ 
      if (e.subtype){
	complex_double c(gen2complex_d(e));
	if (!angle_radian(contextptr)) 
	  c *= deg2rad_d;
#ifdef _SOFTMATH_H
	return std::giac_gnuwince_tan(c);
#else
	return std::sin(c)/std::cos(c);
#endif
      }
      if (e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_){
	gen e1=e;
	if (!angle_radian(contextptr)) 
	  e1=e*deg2rad_g;
	gen e2=im(e1,contextptr);
	e1=re(e1,contextptr);
	bool tmp=angle_radian(contextptr);
	angle_radian(true,contextptr);
	e1=tan(e1,contextptr);
	angle_radian(tmp,contextptr);
	e2=cst_i*tanh(e2,contextptr);
	return (e1+e2)/(1-e1*e2);
      }
    }
    if (is_squarematrix(e))
      return analytic_apply(at_tan,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,contextptr,giac::tan);
    if (is_zero(e,contextptr))
      return e;
    if (is_undef(e))
      return e;
    if (is_inf(e))
      return undef;
    if ( (e.type==_INT_) && (e.val<0) )
      return -tan(-e,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,tan(b,contextptr)),_SEQ__VECT));
    int k;
    if (!approx_mode(contextptr)){ 
      if (is_multiple_of_pi_over_12(e,k,angle_radian(contextptr),contextptr)) 
	return *table_tan[(k%12)];
      else {
	gen kk;
	if (angle_radian(contextptr)) 
	  kk=normal(rdiv(e,cst_pi,contextptr),contextptr);
	else
	  kk=normal(rdiv(e,180,contextptr),contextptr);
	if (is_assumed_integer(kk,contextptr))
	  return zero;
	int n,d;
	if (is_rational(kk,n,d)){
	  if (angle_radian(contextptr)) 
	    return symb_tan((n%d)*inv(d,contextptr)*cst_pi);
	  else
	    return symb_tan(rdiv((n%d)*180,d,contextptr));
	}
      }
    }
    if (e.type==_SYMB) {
      unary_function_ptr u=e._SYMBptr->sommet;
      gen f=e._SYMBptr->feuille;
      if (u==at_neg)
	return -tan(f,contextptr);
      if (u==at_atan)
	return f;
      if (u==at_acos)
	return rdiv(sqrt(1-pow(f,2),contextptr),f,contextptr);
      if (u==at_asin)
	return rdiv(f,sqrt(1-pow(f,2),contextptr),contextptr);
    }
    if (is_equal(e))
      return apply_to_equal(e,tan,contextptr);
    return symb_tan(e);
  }
  static gen d_tan(const gen & e,GIAC_CONTEXT){
    if (angle_radian(contextptr)) 
      return 1+pow(tan(e,contextptr),2);
    else
      return deg2rad_e*(1+pow(tan(e,contextptr),2));
  }
  define_partial_derivative_onearg_genop( D_at_tan," D_at_tan",&d_tan);
  static const char _tan_s []="tan";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (36,__tan,&giac::tan,(unsigned long)&D_at_tanunary_function_ptr,_tan_s);
#else
  static define_unary_function_eval3_index (36,__tan,&giac::tan,D_at_tan,_tan_s);
#endif
  define_unary_function_ptr5( at_tan ,alias_at_tan,&__tan,0,true);

  symbolic symb_asin(const gen & e){
    return symbolic(at_asin,e);
  }
  static gen asinasln(const gen & x,GIAC_CONTEXT){
    return -cst_i*ln(cst_i*x+sqrt(1-x*x,contextptr),contextptr);
    // return cst_i*ln(sqrt(x*x-1,contextptr)+x,contextptr)+cst_pi_over_2;
  }
  gen asin(const gen & e0,GIAC_CONTEXT){
    if (abs_calc_mode(contextptr)==38 && !complex_mode(contextptr) && (e0.type<=_POLY || e0.type==_FLOAT_) && (!is_positive(e0+1,contextptr) || !is_positive(1-e0,contextptr)))
      return gensizeerr(contextptr);
    if (e0.type==_FLOAT_){
      if (!is_positive(e0+1,contextptr) || !is_positive(1-e0,contextptr))
	return asinasln(e0,contextptr)*gen(angle_radian(contextptr)?1.0:rad2deg_d); // cst_i*ln(sqrt(e0*e0-1,contextptr)+e0,contextptr)+evalf(cst_pi_over_2,1,contextptr);
#ifdef BCD
      return fasin(e0._FLOAT_val,angle_mode(contextptr));
#else
      return asin(get_double(e0._FLOAT_val),contextptr);
#endif
    }
#ifndef VISUALC
    static gen * normal_sin_pi_12_ptr=0;
    if (!normal_sin_pi_12_ptr)
      normal_sin_pi_12_ptr=new gen(normal(sin_pi_12,contextptr));
    static gen * normal_cos_pi_12_ptr=0;
    if (!normal_cos_pi_12_ptr)
      normal_cos_pi_12_ptr=new gen(normal(cos_pi_12,contextptr));
#endif
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_DOUBLE_){
      if (e._DOUBLE_val>=-1 && e._DOUBLE_val<=1){
#ifdef _SOFTMATH_H
	double d= std::giac_gnuwince_asin(e._DOUBLE_val);
#else
	double d=std::asin(e._DOUBLE_val);
#endif
	if (angle_radian(contextptr)) 
	  return d;
	else
	  return d*rad2deg_d;
      }
    }
    if (e.type==_REAL){
      if (angle_radian(contextptr)) 
	return e._REALptr->asin();
      else
	return 180*e._REALptr->asin()/cst_pi;
    }
    if ( e.type==_DOUBLE_ || (e.type==_CPLX && (e.subtype || e._CPLXptr->type==_FLOAT_ || e._CPLXptr->type==_REAL)) ){
      if (angle_radian(contextptr)) 
	return no_context_evalf(asinasln(e,contextptr));
      else
	return no_context_evalf(asinasln(e,contextptr))*gen(rad2deg_d);
    }
    if (is_squarematrix(e))
      return analytic_apply(at_asin,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,giac::asin,contextptr);
    if (is_zero(e,contextptr))
      return e;
    if (is_one(e)){
      if (is_zero(e)) fonction_bidon();
      if (angle_radian(contextptr))
	return cst_pi_over_2;
      return 90;
    }
    if (e==sin_pi_12 
#ifndef VISUALC
	|| e==*normal_sin_pi_12_ptr
#endif
	){
      if (angle_radian(contextptr))
	return rdiv(cst_pi,12,contextptr);
      return 15;
    }
    if (e==cos_pi_12 
#ifndef VISUALC
	|| e==*normal_cos_pi_12_ptr
#endif
	){
      if (angle_radian(contextptr))
	return 5*cst_pi/12;
      return 75;
    }
    if (e==plus_sqrt3_2){
      if (angle_radian(contextptr))
	return rdiv(cst_pi,3,contextptr);
      return 60;
    }
    if (e==plus_sqrt2_2){
      if (angle_radian(contextptr)) 
	return rdiv(cst_pi,4,contextptr);
      return 45;
    }
    if (e==plus_one_half){
      if (angle_radian(contextptr)) 
	return rdiv(cst_pi,6,contextptr);
      return 30;
    }
    if (is_undef(e))
      return e;
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,asin(b,contextptr)),_SEQ__VECT));
    if ((e.type==_SYMB) && (e._SYMBptr->sommet==at_neg))
      return -asin(e._SYMBptr->feuille,contextptr);
    if (e.is_symb_of_sommet(at_cos))
      e=symbolic(at_sin,cst_pi_over_2-e._SYMBptr->feuille);
    if (e.is_symb_of_sommet(at_sin) && has_evalf(e._SYMBptr->feuille,a,1,contextptr)){
      // asin(sin(a))==a-2*k*pi or pi-a-2*k*pi
      gen n=_round(a/cst_pi,contextptr);
      b=a-n*cst_pi; // in [-pi/2,pi/2]
      if (n.type==_INT_ && n.val%2==0)
	return e._SYMBptr->feuille-n*cst_pi;
      return n*cst_pi-e._SYMBptr->feuille;
    }
    if ( (e.type==_INT_) && (e.val<0) )
      return -asin(-e,contextptr);
    if (is_equal(e))
      return apply_to_equal(e,asin,contextptr);
    if (lidnt(e).empty() && is_positive(e*e-1,contextptr))
      return asinasln(e,contextptr);
    return symb_asin(e);
  }
  static gen d_asin(const gen & args,GIAC_CONTEXT){
    gen g=inv(recursive_normal(sqrt(1-pow(args,2),contextptr),contextptr),contextptr);
    if (angle_radian(contextptr)) 
      return g;
    else
      return g*deg2rad_e;
  }
  static gen taylor_asin (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0; // no symbolic preprocessing
    if (is_one(lim_point)){
      shift_coeff=plus_one_half;
      identificateur x(" "); vecteur v;
      taylor(pow(2+x,minus_one_half,contextptr),x,0,ordre,v,contextptr);
      // integration with shift 
      v=integrate(v,shift_coeff);
      if (!direction)
	direction=1;
      return normal((gen(-direction)*cst_i)*gen(v),contextptr);
    }
    if (is_minus_one(lim_point)){
      shift_coeff=plus_one_half;
      identificateur x(" "); vecteur v;
      taylor(pow(2-x,minus_one_half,contextptr),x,0,ordre,v,contextptr);
      // integration with shift 
      v=integrate(v,shift_coeff);
      return v;
    }
    return taylor(lim_point,ordre,f,direction,shift_coeff,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_asin," D_at_asin",&d_asin);
  static const char _asin_s []="asin";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor_index( 38,__asin,&giac::asin,(unsigned long)&D_at_asinunary_function_ptr,&taylor_asin,_asin_s);
#else
  static define_unary_function_eval_taylor_index( 38,__asin,&giac::asin,D_at_asin,&taylor_asin,_asin_s);
#endif
  define_unary_function_ptr5( at_asin ,alias_at_asin,&__asin,0,true);

  static symbolic symb_acos(const gen & e){
    return symbolic(at_acos,e);
  }
  gen acos(const gen & e0,GIAC_CONTEXT){
    if (abs_calc_mode(contextptr)==38 && !complex_mode(contextptr) && (e0.type<=_POLY || e0.type==_FLOAT_) && (!is_positive(e0+1,contextptr) || !is_positive(1-e0,contextptr)))
      return gensizeerr(contextptr);
    if (e0.type==_FLOAT_ && is_positive(e0+1,contextptr) && is_positive(1-e0,contextptr)){
#ifdef BCD
      return facos(e0._FLOAT_val,angle_mode(contextptr));
#else
      return acos(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_DOUBLE_){
      if (e._DOUBLE_val>=-1 && e._DOUBLE_val<=1){
#ifdef _SOFTMATH_H
	double d= std::giac_gnuwince_acos(e._DOUBLE_val);
#else
	double d=std::acos(e._DOUBLE_val);
#endif
	if (angle_radian(contextptr)) 
	  return d;
	else
	  return d*rad2deg_d;
      }
    }
    if (e.type==_REAL){
      if (angle_radian(contextptr)) 
	return e._REALptr->acos();
      else
	return 180*e._REALptr->acos()/cst_pi;
    }
    if (e.type==_DOUBLE_ || e.type==_FLOAT_ || e.type==_REAL){
      gen res=cst_i*no_context_evalf(ln(sqrt(e*e-1,contextptr)+e,contextptr));
      if (angle_radian(contextptr)) 
	return res;
      else
	return res*gen(rad2deg_d);
    }
    if ( e.type==_CPLX && (e.subtype || e._CPLXptr->type==_FLOAT_ || e._CPLXptr->type==_REAL) ){
      gen res=cst_pi/2-asinasln(e,contextptr); // -cst_i*no_context_evalf(ln(sqrt(e*e-1,contextptr)+e,contextptr));
      if (angle_radian(contextptr)) 
	return res;
      else
	return res*gen(rad2deg_d);
    }
    if (is_squarematrix(e))
      return analytic_apply(at_acos,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,giac::acos,contextptr);
    if (is_equal(e))
      return apply_to_equal(e,acos,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,acos(b,contextptr)),_SEQ__VECT));
    gen g=asin(e,contextptr);
    if ( (g.type==_SYMB) && (g._SYMBptr->sommet==at_asin) )
      return symb_acos(e);
    if (angle_radian(contextptr)) 
      return normal(cst_pi_over_2-asin(e,contextptr),contextptr);
    else
      return 90-asin(e,contextptr);
  }
  static gen d_acos(const gen & args,GIAC_CONTEXT){
    gen g= -inv(recursive_normal(sqrt(1-pow(args,2),contextptr),contextptr),contextptr);
    if (angle_radian(contextptr)) 
      return g;
    else
      return g*deg2rad_e;
  }
  define_partial_derivative_onearg_genop( D_at_acos," D_at_acos",&d_acos);
  static gen taylor_acos (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0; // no symbolic preprocessing
    if (is_one(lim_point)){
      shift_coeff=plus_one_half;
      identificateur x(" "); vecteur v;
      taylor(pow(2+x,minus_one_half,contextptr),x,0,ordre,v,contextptr);
      // integration with shift 
      v=integrate(v,shift_coeff);
      if (!direction)
	direction=1;
      return -normal((gen(-direction)*cst_i)*gen(v),contextptr);
    }
    if (is_minus_one(lim_point)){
      shift_coeff=plus_one_half;
      identificateur x(" "); vecteur v;
      taylor(pow(2-x,minus_one_half,contextptr),x,0,ordre,v,contextptr);
      // integration with shift 
      v=integrate(v,shift_coeff);
      return -v;
    }
    return taylor(lim_point,ordre,f,direction,shift_coeff,contextptr);
  }
  static const char _acos_s []="acos";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor_index( 40,__acos,&acos,(unsigned long)&D_at_acosunary_function_ptr,&taylor_acos,_acos_s);
#else
  static define_unary_function_eval_taylor_index( 40,__acos,&giac::acos,D_at_acos,&taylor_acos,_acos_s);
#endif
  define_unary_function_ptr5( at_acos ,alias_at_acos,&__acos,0,true);

  symbolic symb_sinh(const gen & e){
    return symbolic(at_sinh,e);
  }
  gen sinh(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_){
#ifdef BCD
      return fsinh(e0._FLOAT_val);
#else
      return sinh(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_DOUBLE_){
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_sinh(e._DOUBLE_val);
#else
      return std::sinh(e._DOUBLE_val);
#endif
    }
    if (e.type==_REAL)
      return e._REALptr->sinh();
    if (e.type==_CPLX){
      if (e.subtype){
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_sinh(gen2complex_d(e));
#else
      return std::sinh(gen2complex_d(e));
#endif
      }
      if (e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_){
	gen g=exp(e,contextptr);
	return (g-inv(g,contextptr))/2;
      }
    }
    if (is_squarematrix(e))
      return analytic_apply(at_sinh,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,giac::sinh,contextptr);
    if ( is_zero(e,contextptr) || (is_undef(e)) || (is_inf(e)))
      return e;
    if (is_equal(e))
      return apply_to_equal(e,sinh,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,sinh(b,contextptr)),_SEQ__VECT));
    if (e.is_symb_of_sommet(at_neg))
      return -sinh(e._SYMBptr->feuille,contextptr);
    return symb_sinh(e);
  }
  static gen d_at_sinh(const gen & e,GIAC_CONTEXT){
    return cosh(e,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_sinh," D_at_sinh",&d_at_sinh);
  static const char _sinh_s []="sinh";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (44,__sinh,&giac::sinh,(unsigned long)&D_at_sinhunary_function_ptr,_sinh_s);
#else
  static define_unary_function_eval3_index (44,__sinh,&giac::sinh,D_at_sinh,_sinh_s);
#endif
  define_unary_function_ptr5( at_sinh ,alias_at_sinh,&__sinh,0,true);

  symbolic symb_cosh(const gen & e){
    return symbolic(at_cosh,e);
  }
  gen cosh(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_){
#ifdef BCD
      return fcosh(e0._FLOAT_val);
#else
      return cosh(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_DOUBLE_){
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_cosh(e._DOUBLE_val);
#else
      return std::cosh(e._DOUBLE_val);
#endif
    }
    if (e.type==_REAL)
      return e._REALptr->cosh();
    if (e.type==_CPLX){
      if (e.subtype){
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_cosh(gen2complex_d(e));
#else
      return std::cosh(gen2complex_d(e));
#endif
      }
      if (e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_){
	gen g=exp(e,contextptr);
	return (g+inv(g,contextptr))/2;
      }
    }
    if (is_squarematrix(e))
      return analytic_apply(at_cosh,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,giac::cosh,contextptr);
    if (is_zero(e,contextptr))
      return 1;
    if (is_undef(e))
      return e;
    if (is_inf(e))
      return plus_inf;
    if (is_equal(e))
      return apply_to_equal(e,cosh,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,cosh(b,contextptr)),_SEQ__VECT));
    if (e.is_symb_of_sommet(at_neg))
      return cosh(e._SYMBptr->feuille,contextptr);
    return symb_cosh(e);
  }
  define_partial_derivative_onearg_genop( D_at_cosh,"D_at_cosh",giac::sinh);
  static const char _cosh_s []="cosh";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (46,__cosh,&giac::cosh,(unsigned long)&D_at_coshunary_function_ptr,_cosh_s);
#else
  static define_unary_function_eval3_index (46,__cosh,&giac::cosh,D_at_cosh,_cosh_s);
#endif
  define_unary_function_ptr5( at_cosh ,alias_at_cosh,&__cosh,0,true);

  // static symbolic symb_tanh(const gen & e){ return symbolic(at_tanh,e);  }
  gen tanh(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_){
#ifdef BCD
      return ftanh(e0._FLOAT_val);
#else
      return tanh(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_DOUBLE_){
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_tanh(e._DOUBLE_val);
#else
      return std::tanh(e._DOUBLE_val);
#endif
    }
    if (e.type==_REAL)
      return e._REALptr->tanh();
    if (e.type==_CPLX){
      if (e.subtype){
	complex_double c(gen2complex_d(e));
#ifdef _SOFTMATH_H
	return std::giac_gnuwince_tanh(c);
#else
	return std::sinh(c)/std::cosh(c);
#endif
      }
      if (e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_){
	gen g=exp(2*e,contextptr);
	return (g+1)/(g-1);
      }
    }
    if (is_squarematrix(e))
      return analytic_apply(at_tanh,*e._VECTptr,contextptr);
    if (e.type==_VECT)
      return apply(e,giac::tanh,contextptr);
    if (is_zero(e,contextptr) || (is_undef(e)) || (e==unsigned_inf))
      return e;
    if (e==plus_inf)
      return 1;
    if (e==minus_inf)
      return -1;
    if (is_equal(e))
      return apply_to_equal(e,tanh,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,tanh(b,contextptr)),_SEQ__VECT));
    if (e.is_symb_of_sommet(at_neg))
      return -tanh(e._SYMBptr->feuille,contextptr);
    return symbolic(at_tanh,e);
  }
  static gen d_tanh(const gen & e,GIAC_CONTEXT){
    return 1-pow(tanh(e,contextptr),2);
  }
  define_partial_derivative_onearg_genop( D_at_tanh," D_at_tanh",&d_tanh);
  static const char _tanh_s []="tanh";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (48,__tanh,&giac::tanh,(unsigned long)&D_at_tanhunary_function_ptr,_tanh_s);
#else
  static define_unary_function_eval3_index (48,__tanh,&giac::tanh,D_at_tanh,_tanh_s);
#endif
  define_unary_function_ptr5( at_tanh ,alias_at_tanh,&__tanh,0,true);

  // static symbolic symb_asinh(const gen & e){  return symbolic(at_asinh,e);  }
  static gen asinhasln(const gen & x,GIAC_CONTEXT){
    return ln(x+sqrt(x*x+1,contextptr),contextptr);
  }
  gen asinh(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_){
#ifdef BCD
      return fasinh(e0._FLOAT_val);
#else
      return asinh(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_DOUBLE_)
      return asinhasln(e,contextptr);
    if (e.type==_REAL)
      return e._REALptr->asinh();
    if ( (e.type==_CPLX) && (e.subtype || e._CPLXptr->type==_REAL))
      return no_context_evalf(asinhasln(e,contextptr));
    if (is_squarematrix(e)){
      context tmp;
      return analytic_apply(at_asinh,*e._VECTptr,&tmp); 
    }
    if (e.type==_VECT)
      return apply(e,giac::asinh,contextptr);
    if (is_zero(e,contextptr) || is_inf(e))
      return e;
    if (is_undef(e))
      return e;
    if (is_equal(e))
      return apply_to_equal(e,asinh,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,asinh(b,contextptr)),_SEQ__VECT));
    return ln(e+sqrt(pow(e,2)+1,contextptr),contextptr);
    // return symbolic(at_asinh,e);
  }
  static gen d_asinh(const gen & args,GIAC_CONTEXT){
    return inv(recursive_normal(sqrt(pow(args,2)+1,contextptr),contextptr),contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_asinh," D_at_asinh",&d_asinh);
  static const char _asinh_s []="asinh";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (50,__asinh,&giac::asinh,(unsigned long)&D_at_asinhunary_function_ptr,_asinh_s);
#else
  static define_unary_function_eval3_index (50,__asinh,&giac::asinh,D_at_asinh,_asinh_s);
#endif
  define_unary_function_ptr5( at_asinh ,alias_at_asinh,&__asinh,0,true);

  // static symbolic symb_acosh(const gen & e){  return symbolic(at_cosh,e);  }
  static gen acoshasln(const gen & x,GIAC_CONTEXT){
    return ln(x+sqrt(x+1,contextptr)*sqrt(x-1,contextptr),contextptr);
  }
  gen acosh(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_){
      if (is_strictly_greater(1,e0,contextptr))
	return ln(e0+sqrt(pow(e0,2)-1,contextptr),contextptr);
#ifdef BCD
      return facosh(e0._FLOAT_val);
#else
      return acosh(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_DOUBLE_)
      return acoshasln(e,contextptr);
    if (e.type==_REAL)
      return e._REALptr->acosh();
    if ( (e.type==_CPLX) && (e.subtype|| e._CPLXptr->type==_REAL || e._CPLXptr->type==_FLOAT_))
      return no_context_evalf(acoshasln(e,contextptr));
    if (is_squarematrix(e))
      return analytic_apply(at_acosh,*e._VECTptr,0);
    if (e.type==_VECT)
      return apply(e,giac::acosh,contextptr);
    if (is_one(e))
      return 0;
    if (e==plus_inf)
      return plus_inf;
    if (is_undef(e))
      return e;
    if (is_equal(e))
      return apply_to_equal(e,acosh,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,acosh(b,contextptr)),_SEQ__VECT));
    return ln(e+sqrt(pow(e,2)-1,contextptr),contextptr);
    // return symbolic(at_acosh,e);
  }
  static gen d_acosh(const gen & args,GIAC_CONTEXT){
    return inv(recursive_normal(sqrt(pow(args,2)-1,contextptr),contextptr),contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_acosh," D_at_acosh",&d_acosh);
  static const char _acosh_s []="acosh";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (52,__acosh,&giac::acosh,(unsigned long)&D_at_acoshunary_function_ptr,_acosh_s);
#else
  static define_unary_function_eval3_index (52,__acosh,&giac::acosh,D_at_acosh,_acosh_s);
#endif
  define_unary_function_ptr5( at_acosh ,alias_at_acosh,&__acosh,0,true);

  // static symbolic symb_atanh(const gen & e){  return symbolic(at_atanh,e);}
  gen atanh(const gen & e0,GIAC_CONTEXT){
    if (e0.type==_FLOAT_){
      if (is_strictly_greater(e0,1,contextptr) || is_strictly_greater(-1,e0,contextptr))
	return rdiv(ln(rdiv(1+e0,1-e0),contextptr),plus_two,contextptr);
#ifdef BCD
      return fatanh(e0._FLOAT_val);
#else
      return atanh(get_double(e0._FLOAT_val),contextptr);
#endif
    }
    gen e=frac_neg_out(e0,contextptr);
    if (e.type==_DOUBLE_ && fabs(e._DOUBLE_val)<1){
#ifdef _SOFTMATH_H
      return std::giac_gnuwince_log((1+e._DOUBLE_val)/(1-e._DOUBLE_val))/2;
#else
      return std::log((1+e._DOUBLE_val)/(1-e._DOUBLE_val))/2;
#endif
    }
    if (e.type==_REAL)
      return e._REALptr->atanh();
    if ( (e.type==_CPLX) && (e.subtype || e._CPLXptr->type==_REAL))
      return no_context_evalf(rdiv(ln(rdiv(1+e,1-e,contextptr),contextptr),plus_two));
    if (is_squarematrix(e))
      return analytic_apply(at_atanh,*e._VECTptr,0);
    if (e.type==_VECT)
      return apply(e,giac::atanh,contextptr);
    if (is_zero(e,contextptr))
      return e;
    if (is_one(e))
      return plus_inf;
    if (is_minus_one(e))
      return minus_inf;
    if (is_undef(e))
      return e;
    if (is_equal(e))
      return apply_to_equal(e,atanh,contextptr);
    gen a,b;
    if (is_algebraic_program(e,a,b))
      return symbolic(at_program,gen(makevecteur(a,0,atanh(b,contextptr)),_SEQ__VECT));
    return rdiv(ln(rdiv(1+e,1-e,contextptr),contextptr),plus_two);
    // return symbolic(at_atanh,e);
  }
  static gen d_atanh(const gen & args,GIAC_CONTEXT){
    return inv(1-pow(args,2),contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_atanh," D_at_atanh",&d_atanh);
  static const char _atanh_s []="atanh";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3_index (54,__atanh,&giac::atanh,(unsigned long)&D_at_atanhunary_function_ptr,_atanh_s);
#else
  static define_unary_function_eval3_index (54,__atanh,&giac::atanh,D_at_atanh,_atanh_s);
#endif
  define_unary_function_ptr5( at_atanh ,alias_at_atanh,&__atanh,0,true);

  static string printasquote(const gen & g,const char * s,GIAC_CONTEXT){
    if (calc_mode(contextptr)==38)
      return "QUOTE("+g.print(contextptr)+")";
    else
      return "'"+g.print(contextptr)+"'"; 
  }
  symbolic symb_quote(const gen & arg){
    return symbolic(at_quote,arg);
  }
  gen quote(const gen & args,GIAC_CONTEXT){
    if (args.type==_VECT && args.subtype==_SEQ__VECT && !args._VECTptr->empty() && args._VECTptr->front().type==_FUNC){
      const unary_function_ptr & u =*args._VECTptr->front()._FUNCptr;
      vecteur v=vecteur(args._VECTptr->begin()+1,args._VECTptr->end());
      gen arg=eval(gen(v,_SEQ__VECT),eval_level(contextptr),contextptr);
      return symbolic(u,arg);
    }
    return args;
  }
  define_partial_derivative_onearg_genop( D_at_quote," D_at_quote",&quote);
  static const char _quote_s []="quote";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval5_quoted (__quote,&giac::quote,(unsigned long)&D_at_quoteunary_function_ptr,_quote_s,&printasquote,0);
#else
  static define_unary_function_eval5_quoted (__quote,&giac::quote,D_at_quote,_quote_s,&printasquote,0);
#endif
  define_unary_function_ptr5( at_quote ,alias_at_quote,&__quote,0,true);

  // symbolic symb_unquote(const gen & arg){    return symbolic(at_unquote,arg);  }
  gen unquote(const gen & arg,GIAC_CONTEXT){
    return eval(arg,1,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_unquote," D_at_unquote",(const gen_op_context)unquote);
  static const char _unquote_s []="unquote";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3 (__unquote,(const gen_op_context)giac::unquote,(unsigned long)&D_at_unquoteunary_function_ptr,_unquote_s);
#else
  static define_unary_function_eval3 (__unquote,(const gen_op_context)giac::unquote,D_at_unquote,_unquote_s);
#endif
  define_unary_function_ptr5( at_unquote ,alias_at_unquote,&__unquote,0,true);

  static symbolic symb_order_size(const gen & e){
    return symbolic(at_order_size,e);
  }
  gen order_size(const gen & arg,GIAC_CONTEXT){
    return symb_order_size(arg);
  }
  define_partial_derivative_onearg_genop( D_at_order_size," D_at_order_size",order_size);
  static const char _order_size_s []="order_size";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3 (__order_size,&giac::order_size,(unsigned long)&D_at_order_sizeunary_function_ptr,_order_size_s);
#else
  static define_unary_function_eval3 (__order_size,&giac::order_size,D_at_order_size,_order_size_s);
#endif
  define_unary_function_ptr5( at_order_size ,alias_at_order_size,&__order_size,0,true);

  gen re(const gen & a,GIAC_CONTEXT){
    if (is_equal(a))
      return apply_to_equal(a,re,contextptr);
    gen a1,b;
    if (is_algebraic_program(a,a1,b))
      return symbolic(at_program,gen(makevecteur(a1,0,symbolic(at_re,b)),_SEQ__VECT));
    return a.re(contextptr);
  }
  static const char _re_s []="re";
  static string texprintasre(const gen & g,const char * s,GIAC_CONTEXT){
    return "\\Re("+gen2tex(g,contextptr)+")";
  }
  static define_unary_function_eval4 (__re,(const gen_op_context)giac::re,_re_s,0,&texprintasre);
  define_unary_function_ptr5( at_re ,alias_at_re,&__re,0,true);

  gen im(const gen & a,GIAC_CONTEXT){
    if (is_equal(a))
      return apply_to_equal(a,im,contextptr);
    gen a1,b;
    if (is_algebraic_program(a,a1,b))
      return symbolic(at_program,gen(makevecteur(a1,0,symbolic(at_im,b)),_SEQ__VECT));
    return a.im(contextptr);
  }
  static const char _im_s []="im";
  static string texprintasim(const gen & g,const char * s,GIAC_CONTEXT){
    return "\\Im("+gen2tex(g,contextptr)+")";
  }
  static define_unary_function_eval4 (__im,(const gen_op_context)giac::im,_im_s,0,&texprintasim);
  define_unary_function_ptr5( at_im ,alias_at_im,&__im,0,true);

  // symbolic symb_conj(const gen & e){  return symbolic(at_conj,e);  }
  gen conj(const gen & a,GIAC_CONTEXT){
    if (is_equal(a))
      return apply_to_equal(a,conj,contextptr);
    gen a1,b;
    if (is_algebraic_program(a,a1,b))
      return symbolic(at_program,gen(makevecteur(a1,0,symbolic(at_conj,b)),_SEQ__VECT));
    return a.conj(contextptr);
  }
  static const char _conj_s []="conj";
  static string texprintasconj(const gen & g,const char * s,GIAC_CONTEXT){
    return "\\overline{"+gen2tex(g,contextptr)+"}";
  }
  static define_unary_function_eval4 (__conj,(const gen_op_context)giac::conj,_conj_s,0,&texprintasconj);
  define_unary_function_ptr5( at_conj ,alias_at_conj,&__conj,0,true);

  static gen taylor_sign (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0; // no symbolic preprocessing
    shift_coeff=0;
    if (is_strictly_positive(lim_point,contextptr) || (is_zero(lim_point,contextptr) && direction==1))
      return makevecteur(1);
    if (is_strictly_positive(-lim_point,contextptr) || (is_zero(lim_point,contextptr) && direction==-1))
      return makevecteur(-1);
    // FIXME? maybe add 
    if (!is_zero(lim_point)) return makevecteur(symbolic(at_sign,lim_point));
    return gensizeerr(gettext("Taylor sign with unsigned limit"));
  }

  gen _sign(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return apply(g,contextptr,giac::sign);
  }
  // static symbolic symb_sign(const gen & e){    return symbolic(at_sign,e);  }
  static const char _sign_s []="sign";
  define_partial_derivative_onearg_genop( D_at_sign,"D_at_sign",_constant_zero);
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __sign,_sign,(unsigned long)&D_at_signunary_function_ptr,&taylor_sign,_sign_s);
#else
  static define_unary_function_eval_taylor( __sign,_sign,D_at_sign,&taylor_sign,_sign_s);
#endif
  define_unary_function_ptr5( at_sign ,alias_at_sign,&__sign,0,true);

  gen _abs(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return apply(args,contextptr,giac::abs);
  }
  symbolic symb_abs(const gen & e){
    return symbolic(at_abs,e);
  }
  static gen taylor_abs (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0; // no symbolic preprocessing
    shift_coeff=0;
    if (is_strictly_positive(lim_point,contextptr) || (is_zero(lim_point,contextptr) && direction==1))
      return makevecteur(lim_point,1);
    if (is_strictly_positive(-lim_point,contextptr) || (is_zero(lim_point,contextptr) && direction==-1))
      return makevecteur(-lim_point,-1);
    return gensizeerr(gettext("Taylor abs with unsigned limit"));
  }
  static const char _abs_s []="abs";
  define_partial_derivative_onearg_genop( D_at_abs,"D_at_abs",giac::sign);
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor_index(20, __abs,&_abs,(unsigned long)&D_at_absunary_function_ptr,&taylor_abs,_abs_s);
#else
  static define_unary_function_eval_taylor_index(20, __abs,&_abs,D_at_abs,&taylor_abs,_abs_s);
#endif
  define_unary_function_ptr5( at_abs ,alias_at_abs,&__abs,0,true);

  // symbolic symb_arg(const gen & e){ return symbolic(at_arg,e);  }
  static const char _arg_s []="arg";
  define_unary_function_eval_index (22,__arg,&giac::arg,_arg_s);
  define_unary_function_ptr5( at_arg ,alias_at_arg,&__arg,0,true);

  static symbolic symb_cyclotomic(const gen & e){
    return symbolic(at_cyclotomic,e);
  }
  gen _cyclotomic(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type!=_INT_)
      return symb_cyclotomic(a);
    return cyclotomic(a.val);
  }
  static const char _cyclotomic_s []="cyclotomic";
  static define_unary_function_eval (__cyclotomic,&giac::_cyclotomic,_cyclotomic_s);
  define_unary_function_ptr5( at_cyclotomic ,alias_at_cyclotomic,&__cyclotomic,0,true);

  string printassto(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) )
      return sommetstr+('('+feuille.print(contextptr)+')');
    vecteur & v=*feuille._VECTptr;
    if (abs_calc_mode(contextptr)==38 && v.back().type!=_VECT){
      string s=v.back().print(contextptr);
      if (s.size()>2 && s[0]=='1' && s[1]=='_')
	s=s.substr(1,s.size()-1);
      return v.front().print(contextptr)+(calc_mode(contextptr)==38?"\xe2\x96\xba":"=>")+s;
    }
    if (xcas_mode(contextptr)==3){
      if ( (v.front().type==_SYMB) && (v.front()._SYMBptr->sommet==at_program)){
	gen & b=v.front()._SYMBptr->feuille._VECTptr->back();
	if  (b.type==_VECT || (b.type==_SYMB && (b._SYMBptr->sommet==at_local || b._SYMBptr->sommet==at_bloc))){
	  string s(v.front().print(contextptr));
	  s=s.substr(10,s.size()-10);
	  return ":"+v.back().print(contextptr)+s;
	}
	else {
	  vecteur & tmpv = *v.front()._SYMBptr->feuille._VECTptr;
	  if (tmpv[0].type==_VECT && tmpv[0].subtype==_SEQ__VECT && tmpv[0]._VECTptr->size()==1)
	    return tmpv[2].print(contextptr)+" => "+v.back().print(contextptr)+"("+tmpv[0]._VECTptr->front().print(contextptr)+")";
	  else
	    return tmpv[2].print(contextptr)+" => "+v.back().print(contextptr)+"("+tmpv[0].print(contextptr)+")";
	}
      }
      else 
	return v.front().print(contextptr)+" => "+v.back().print(contextptr);
    }
#ifndef GIAC_HAS_STO_38
    if (v.back().is_symb_of_sommet(at_of)){
      gen f=v.back()._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2){
	return f._VECTptr->front().print(contextptr)+"[["+f._VECTptr->back().print(contextptr)+"]] := " + v.front().print(contextptr);
      }
    }
#endif
    string s(v.back().print(contextptr)+":=");
    if (v.front().type==_SEQ__VECT)
      return s+"("+v.front().print(contextptr)+")";
    else
      return s+v.front().print(contextptr);
  }
  static string texprintassto(const gen & g,const char * sommetstr,GIAC_CONTEXT){
    if ( (g.type!=_VECT) || (g._VECTptr->size()!=2) )
      return sommetstr+('('+gen2tex(g,contextptr)+')');
    string s(gen2tex(g._VECTptr->back(),contextptr)+":=");
    if (g._VECTptr->front().type==_SEQ__VECT)
      return s+"("+gen2tex(g._VECTptr->front(),contextptr)+")";
    else
      return s+gen2tex(g._VECTptr->front(),contextptr);
  }

  gen _calc_mode(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    int & mode=calc_mode(contextptr);
    if (args.type==_INT_)
      mode=args.val;
    if (args.type==_DOUBLE_)
      mode=int(args._DOUBLE_val);
    if (args.type==_FLOAT_)
      mode=get_int(args._FLOAT_val);
    return mode;
  }
  static const char _calc_mode_s []="calc_mode";
  static define_unary_function_eval (__calc_mode,&giac::_calc_mode,_calc_mode_s);
  define_unary_function_ptr5( at_calc_mode ,alias_at_calc_mode,&__calc_mode,0,true); 
  
  bool is_numericv(const vecteur & v, int withfracint){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type==_VECT || !is_fully_numeric(*it, withfracint))
	return false;
    }
    return true;
  }
  bool is_numericm(const vecteur & v, int withfracint){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type!=_VECT || !is_numericv(*it->_VECTptr, withfracint))
	return false;
    }
    return true;
  }

  bool check_vect_38(const string & s){
    int ss=s.size();
    if (ss!=2)
      return false;
    char s0=s[0],s1=s[1];
    if (s1>'9')
      return true;
    switch (s0){
    case 'F': case 'R': case 'U': case 'X': case 'Y':
      return false;
    }
    return true;
  }
  // check value type for storing value in s using 38 compatibility mode
  bool check_sto_38(gen & value,const char * s){
    int ss=strlen(s);
    if (ss>2 || (ss==2 && s[1]>32 && isalpha(s[1])) ){
      if (s[0]=='G')
	return true;
      for (int i=0;i<ss;++i){
	const char & ch=s[i];
	if ( (ch>'Z' && ch!='i' && ch!='e')|| ch<'0')
	  return true;
      }
      return false;
    }
    char s0=s[0],s1=s[1];
    // a quick hack for R2(2) where R2 is COS(θ), done to help illustrate an issue
    // before hack: R2(2) in HOME gives COS(2); after: -0.416...
    // VariableGetFunc for R2(2) passes "(COS(θ))|θ=(2)" to Calc->Parse
    // perhaps a temporary variable instead of θ?
    // perhaps a move (of this code here) over the fence to Aspen?
    // TCalcData::LetterMemory(char const *utf8_name) can identify the names used
    if (ss==1 && s0>'Z') return true;
    if ( (ss==1 && s0<='Z') || (ss==2 && s0=='\xCE' && s1=='\xB8') ) { 
      value=evalf(value,1,context0);
      return value.type==_DOUBLE_ || value.type==_FLOAT_;
    }
    if (s1>'9')
      return true;
    switch (s0){
    case 'C': case 'L':
      if (value.type!=_VECT || (s0=='C' && !is_numericv(*value._VECTptr)))
	return false;
      value.subtype=_LIST__VECT;
      break;
    case 'F': case 'R': case 'U': case 'X': case 'Y':
      // if (!value.is_symb_of_sommet(at_program))
      // return false;
      break;
    case 'M':
      value=evalf(value,1,context0);
      value.subtype=0;
      return (ckmatrix(value) && is_numericm(*value._VECTptr)) || (value.type==_VECT && is_numericv(*value._VECTptr));
    case 'V':
      return false; // remove if V0..V9 is allowed
      value=evalf(value,1,context0);
      value.subtype=0;
      return value.type==_VECT && is_numericv(*value._VECTptr);
    case 'Z':
      value=evalf(value,1,context0);
      return value.type==_DOUBLE_ || value.type==_FLOAT_ || value.type==_CPLX;
    }
    return true;
  }

#ifdef GIAC_HAS_STO_38
  int do_sto_38(const gen & value,const char * name_space,const char * idname,gen indice,gen & error,GIAC_CONTEXT);
  int do_rcl_38(gen & value,const char * name_space,const char * idname,gen indice,bool at_of,GIAC_CONTEXT);
  int do_is_known_name_38(const char * name_space,const char * idname);
  int (*sto_38)(const gen & value,const char * name_space,const char * idname,gen indice,gen & error,GIAC_CONTEXT)=do_sto_38;
  int (*rcl_38)(gen & value,const char * name_space,const char * idname,gen indice,bool at_of,GIAC_CONTEXT)=do_rcl_38;
  int (*is_known_name_38)(const char * name_space,const char * idname)=do_is_known_name_38;
  gen do_of_pointer_38(const void * appptr,const void * varptr,const gen & args);
  gen (*of_pointer_38)(const void * appptr,const void * varptr,const gen & args)=do_of_pointer_38;
  extern gen_op_context interactive_op_tab_38[];
  gen_op_context * interactive_op_tab = &interactive_op_tab_38[0];
#else
  int (*sto_38)(const gen & value,const char * name_space,const char * idname,gen indice,gen & error,GIAC_CONTEXT)=0;
  int (*rcl_38)(gen & value,const char * name_space,const char * idname,gen indice,bool at_of,GIAC_CONTEXT)=0;
  int (*is_known_name_38)(const char * name_space,const char * idname)=0;
  gen (*of_pointer_38)(const void * appptr,const void * varptr,const gen & args)=0;
  gen_op_context * interactive_op_tab = 0;
#endif

  // store a in b
#ifdef HAVE_SIGNAL_H_OLD
  bool signal_store=true;
#endif

  bool is_local(const gen & b,GIAC_CONTEXT){
    if (b.type!=_IDNT)
      return false;
    if (contextptr){
      const context * ptr=contextptr;
      for (;ptr->previous && ptr->tabptr;ptr=ptr->previous){
	sym_tab::iterator it=ptr->tabptr->find(b._IDNTptr->id_name),itend=ptr->tabptr->end();
	if (it!=itend)
	  return true;
      }
    }
    return false;
  }

  gen sto(const gen & a,const gen & b,const context * contextptr){
    gen res= sto(a,b,false,contextptr);
    return res;
  }
  // in_place==true to store in vector/matrices without making a new copy
  gen sto(const gen & a,const gen & b,bool in_place,const context * contextptr_){
    if (is_undef(a) && a.type==_STRNG)
      return a;
    // *logptr(contextptr) << "Sto " << "->" << b << endl;
    const context * contextptr=contextptr_;
    if (contextptr && contextptr->parent)
      contextptr=contextptr->parent;
    if (b.type==_SYMB){ 
      if (b.is_symb_of_sommet(at_unit))
	return _convert(gen(makevecteur(a,b),_SEQ__VECT),contextptr);
      if (b._SYMBptr->sommet==at_hash && b._SYMBptr->feuille.type==_STRNG)
	return sto(a,gen(*b._SYMBptr->feuille._STRNGptr,contextptr),in_place,contextptr);
      if (b._SYMBptr->sommet==at_double_deux_points){ 
	// variable of another named context?
	gen a1,bb,error;
	if (!check_binary(b._SYMBptr->feuille,a1,bb))
	  return a1;
	if (sto_38 && !is_local(b,contextptr) && abs_calc_mode(contextptr)==38 && a1.type==_IDNT && bb.type==_IDNT && sto_38(a,a1._IDNTptr->id_name,bb._IDNTptr->id_name,0,error,contextptr)){
	  return is_undef(error)?error:a;
	}
#ifndef RTOS_THREADX
#ifndef BESTA_OS
#ifdef HAVE_LIBPTHREAD
	pthread_mutex_lock(&context_list_mutex);
#endif
	if (a1.type==_INT_ && a1.subtype==0 && a1.val>=0 && a1.val<(signed)context_list().size()){
	  context * ptr =context_list()[a1.val];
#ifdef HAVE_LIBPTHREAD
	  pthread_mutex_unlock(&context_list_mutex);
#endif
	  return sto(a,bb,in_place,ptr);
	}
	if (context_names){
	  map<string,context *>::iterator it=context_names->find(a1.print()),itend=context_names->end();
	  if (it!=itend){
	    context * ptr = it->second;
#ifdef HAVE_LIBPTHREAD
	    pthread_mutex_unlock(&context_list_mutex);
#endif
	    return sto(a,bb,in_place,ptr);
	  }
	}
#ifdef HAVE_LIBPTHREAD
	pthread_mutex_unlock(&context_list_mutex);
#endif
#endif
#endif
	// TI path
	gen ab=a1.eval(eval_level(contextptr),contextptr);
	if (ab.type==_VECT){
	  vecteur v=*ab._VECTptr;
	  iterateur it=v.begin(),itend=v.end();
	  for (;it!=itend;++it){
	    if (it->type!=_VECT || it->_VECTptr->size()!=2)
	      continue;
	    vecteur & w=*it->_VECTptr;
	    if (w[0]==bb)
	      w[1]=a;
	  }
	  if (it==itend)
	    v.push_back(makevecteur(bb,a));
	  return sto(gen(v,_FOLDER__VECT),a1,in_place,contextptr);
	}
	if (a1.type==_IDNT)
	  return sto(gen(vecteur(1,makevecteur(bb,a)),_FOLDER__VECT),a1,in_place,contextptr);
      } // end TI path
    }
    if (b.type==_IDNT){
      if (!contextptr){
	// Remove stale local assignements
#ifdef NO_STDEXCEPT
	b._IDNTptr->eval(1,b,contextptr); 
#else
	try {
	  b._IDNTptr->eval(1,b,contextptr); 
	} catch (std::runtime_error & ) { }
#endif
      }
      gen aa(a),error;
      if (strcmp(b._IDNTptr->id_name,string_pi)==0 || strcmp(b._IDNTptr->id_name,string_infinity)==0 || strcmp(b._IDNTptr->id_name,string_undef)==0)
	return gensizeerr(b.print(contextptr)+": reserved word");
      if (a==b)
	return _purge(b,contextptr);
      gen ans(aa);
      if ( (a.type==_SYMB) && (a._SYMBptr->sommet==at_parameter)){
	gen inter=a._SYMBptr->feuille,debut,fin,saut;
	bool calc_aa=false;
	if (inter.type==_VECT){
	  vecteur & interv=*inter._VECTptr;
	  int inters=interv.size();
	  if (inters>=3){
	    debut=interv[0];
	    fin=interv[1];
	    if (is_strictly_greater(debut,fin,contextptr))
	      swapgen(debut,fin);
	    aa=interv[2];
	    if (is_strictly_greater(aa,fin,contextptr))
	      aa=fin;
	    if (is_strictly_greater(debut,aa,contextptr))
	      aa=debut;
	    if (inters>=4)
	      saut=interv[3];
	  }
	  if (inters==2){
	    aa=interv.back();
	    inter=interv.front();
	  }
	}
	else
	  calc_aa=true;
	if ( (inter.type==_SYMB) && (inter._SYMBptr->sommet==at_interval) ){
	  debut=inter._SYMBptr->feuille._VECTptr->front();
	  fin=inter._SYMBptr->feuille._VECTptr->back();
	}
	if (calc_aa)
	  aa=rdiv(debut+fin,plus_two,contextptr);
	if (is_zero(saut,contextptr))
	  saut=(fin-debut)/100.;
	ans=symbolic(at_parameter,makesequence(b,debut,fin,aa,saut));
      } // end parameter
      if (abs_calc_mode(contextptr)==38){
	if (sto_38 && !is_local(b,contextptr) && sto_38(aa,0,b._IDNTptr->id_name,0,error,contextptr) )
	  return is_undef(error)?error:ans;
      }
      if (b._IDNTptr->quoted)
	*b._IDNTptr->quoted |= 2; // set dirty bit
      if (contextptr){
	const context * ptr=contextptr;
	bool done=false;
	for (;ptr->previous && ptr->tabptr;ptr=ptr->previous){
	  sym_tab::iterator it=ptr->tabptr->find(b._IDNTptr->id_name),itend=ptr->tabptr->end();
	  if (it!=itend){ // found in current local context
	    // check that the current value is a thread pointer
	    if (it->second.type==_POINTER_ && it->second.subtype==_THREAD_POINTER){
	      if (it->second._POINTER_val!=(void *)contextptr_)
		return gentypeerr(b.print(contextptr)+gettext(" is locked by thread ")+it->second.print(contextptr));
	    }
	    it->second=aa;
	    done=true;
	    break;
	  }
	}
	if (!done) {// store b globally
	  if (contains(lidnt(a),b))
	    *logptr(contextptr) << b.print(contextptr)+gettext(": recursive definition") << endl;
	  sym_tab * symtabptr=contextptr->globalcontextptr?contextptr->globalcontextptr->tabptr:contextptr->tabptr;
	  sym_tab::iterator it=symtabptr->find(b._IDNTptr->id_name),itend=symtabptr->end();
	  if (it!=itend){ 
	    // check that the current value is a thread pointer
	    if (it->second.type==_POINTER_ && it->second.subtype==_THREAD_POINTER){
	      if (it->second._POINTER_val!=(void *)contextptr_)
		return gentypeerr(b.print(contextptr)+gettext(" is locked by thread ")+it->second.print(contextptr));
	    }
	    it->second=aa;
	  }
	  else
	    (*symtabptr)[b._IDNTptr->id_name]=aa;
	}
#ifdef HAVE_SIGNAL_H_OLD
	if (!child_id && signal_store)
	  _signal(symb_quote(symbolic(at_sto,gen(makevecteur(aa,b),_SEQ__VECT))),contextptr);
#endif
	return ans;
      } // end if (contextptr)
      if (contains(lidnt(a),b))
	*logptr(contextptr) << b.print(contextptr)+gettext(": recursive definition") << endl;
      if (b._IDNTptr->localvalue && !b._IDNTptr->localvalue->empty() && (b.subtype!=_GLOBAL__EVAL))
	b._IDNTptr->localvalue->back()=aa;
      else {
	if (current_folder_name.type==_IDNT && current_folder_name._IDNTptr->value && current_folder_name._IDNTptr->value->type==_VECT){
	  vecteur v=*current_folder_name._IDNTptr->value->_VECTptr;
	  iterateur it=v.begin(),itend=v.end();
	  for (;it!=itend;++it){
	    if (it->type!=_VECT || it->_VECTptr->size()!=2)
	      continue;
	    vecteur & w=*it->_VECTptr;
	    if (w[0]==b){
	      w[1]=aa;
	      break;
	    }
	  }
	  if (it==itend)
	    v.push_back(makevecteur(b,aa));
	  gen gv(v,_FOLDER__VECT);
	  *current_folder_name._IDNTptr->value=gv;
#ifdef HAVE_SIGNAL_H_OLD
	  if (!child_id && signal_store)
	    _signal(symb_quote(symbolic(at_sto,gen(makevecteur(gv,current_folder_name),_SEQ__VECT))),contextptr);
#endif
	  return ans;
	}
	else {
	  if (b._IDNTptr->value)
	    delete b._IDNTptr->value;
	  if (b._IDNTptr->ref_count) 
	    b._IDNTptr->value = new gen(aa);
#ifdef HAVE_SIGNAL_H_OLD
	  if (!child_id && signal_store)
	    _signal(symb_quote(symbolic(at_sto,gen(makevecteur(aa,b),_SEQ__VECT))),contextptr);
#endif
	  if (!secure_run && variables_are_files(contextptr)){
	    ofstream a_out((b._IDNTptr->name()+string(cas_suffixe)).c_str());
	    a_out << aa << endl;
	  }
	}
      }
      return ans;
    } // end b.type==_IDNT
    if (b.type==_VECT){
      if (a.type!=_VECT)
	return gentypeerr(contextptr);
      return apply(a,b,contextptr,giac::sto);
    }
    if ( (b.type==_SYMB) && (b._SYMBptr->sommet==at_at || b._SYMBptr->sommet==at_of) ){
      // Store a in a vector or array or map
      gen destination=b._SYMBptr->feuille._VECTptr->front(),error; // variable name
      // if (sto_38 && destination.is_symb_of_sommet(at_double_deux_points) && destination._SYMBptr->feuille.type==_VECT && destination._SYMBptr->feuille._VECTptr->size()==2 &&destination._SYMBptr->feuille._VECTptr->front().type==_IDNT && destination._SYMBptr->feuille._VECTptr->back().type==_IDNT && sto_38(a,destination._SYMBptr->feuille._VECTptr->front()._IDNTptr->id_name,destination._SYMBptr->feuille._VECTptr->back()._IDNTptr->id_name,b,error,contextptr))	
      // return is_undef(error)?error:a;
      if (sto_38 && destination.type==_IDNT && sto_38(a,0,destination._IDNTptr->id_name,b,error,contextptr))
	return is_undef(error)?error:a;
      if (destination.type==_IDNT && destination._IDNTptr->quoted)
	*destination._IDNTptr->quoted |= 2; // set dirty bit
      gen valeur;
      if (!contextptr && in_place && destination.type==_IDNT && destination._IDNTptr->localvalue && !destination._IDNTptr->localvalue->empty() && local_eval(contextptr) )
	valeur=do_local_eval(*destination._IDNTptr,eval_level(contextptr),false);
      else
	valeur=destination.eval(in_place?1:eval_level(contextptr),contextptr);
      if ( valeur.type==_INT_ && valeur.val==0 && destination.type==_IDNT && destination._IDNTptr->localvalue && !destination._IDNTptr->localvalue->empty() )
	valeur=destination; // non (0) initialized local var
      gen indice=b._SYMBptr->feuille._VECTptr->back().eval(eval_level(contextptr),contextptr);
      if (indice.type==_VECT && indice.subtype==_SEQ__VECT && indice._VECTptr->size()==1)
	indice=indice._VECTptr->front();
      is_integral(indice);
      if (b._SYMBptr->sommet==at_of && valeur.type==_VECT && (1 || abs_calc_mode(contextptr)==38)){ // matrices and vector indices in HP38 compatibility mode
	if (indice.type==_INT_)
	  indice -= 1;
	if (indice.type==_VECT)
	  indice = indice - vecteur(indice._VECTptr->size(),1);
      }
      if ( (destination.type!=_IDNT && !destination.is_symb_of_sommet(at_double_deux_points)) || (valeur.type!=_VECT && valeur.type!=_MAP && valeur.type!=_IDNT && valeur.type!=_STRNG && valeur.type!=_SYMB) )
	return gentypeerr(gettext("sto ")+b.print(contextptr)+ "="+valeur.print(contextptr)+" not allowed!");
      if (valeur.type==_IDNT){ 
	// no previous vector at destination, 
	// create one in TI mode or create a map
	gen g;
	if (xcas_mode(contextptr)==3 && indice.type==_INT_ && indice.val>=0 ){
	  vecteur v(indice.val+1,zero);
	  v[indice.val]=a;
	  g=gen(v,destination.subtype);
	}
	else {
	  g=makemap();
	  (*g._MAPptr)[indice]=a;
	}
	return sto(g,destination,in_place,contextptr);
      }
      if (valeur.type==_STRNG){
	if (indice.type!=_INT_ || a.type!=_STRNG || a._STRNGptr->empty())
	  return gensizeerr(contextptr);
	if (indice.val<0 || indice.val>=(int) valeur._STRNGptr->size())
	  return gendimerr(contextptr);
	if (in_place){
	  (*valeur._STRNGptr)[indice.val]=(*a._STRNGptr)[0];
	  return string2gen("Done",false);
	}
	else {
	  string m(*valeur._STRNGptr);
	  m[indice.val]=(*a._STRNGptr)[0];
	  return sto(string2gen(m,false),destination,in_place,contextptr);
	}
      }
      if (valeur.type==_SYMB){
	if (indice.type==_VECT){
	  gen v(valeur);
	  vecteur empile;
	  iterateur it=indice._VECTptr->begin(),itend=indice._VECTptr->end();
	  for (;;){
	    if (it->type!=_INT_)
	      return gentypeerr(gettext("Bad index ")+indice.print(contextptr));
	    empile.push_back(v);
	    v=v[*it];
	    ++it;
	    if (it==itend)
	      break;
	  }
	  --itend;
	  v=empile.back();
	  if (v.type==_VECT){
	    vecteur vv=*v._VECTptr;
	    if (itend->val>=0&&itend->val<int(vv.size())) // additional check
	      vv[itend->val]=a;
	    v=gen(vv,v.subtype);
	  }
	  else {
	    if (v.type==_SYMB){
	      if (itend->val==0){
		if (a.type==_FUNC)
		  v=symbolic(*a._FUNCptr,v._SYMBptr->feuille);
		else
		  v=symbolic(at_of,makesequence(a,v._SYMBptr->feuille));
	      }
	      else {
		if (v._SYMBptr->feuille.type!=_VECT)
		  v=symbolic(v._SYMBptr->sommet,a);
		else {
		  vecteur vv=*v._SYMBptr->feuille._VECTptr;
		  if (itend->val>0&&itend->val<=int(vv.size())) // additional check
		    vv[itend->val-1]=a;
		  v=symbolic(v._SYMBptr->sommet,gen(vv,v._SYMBptr->feuille.subtype));
		}
	      }
	    }
	    else
	      v=a;
	  }
	  gen oldv;
	  it=indice._VECTptr->begin();
	  for (;;){
	    if (itend==it)
	      break;
	    --itend;
	    empile.pop_back();
	    oldv=empile.back();
	    if (oldv.type==_VECT){
	      vecteur vv=*oldv._VECTptr;
	      if (itend->val>=0&&itend->val<int(vv.size())) // additional check
		vv[itend->val]=v;
	      v=gen(vv,oldv.subtype);
	    }
	    else {
	      if (oldv.type==_SYMB){
		if (oldv._SYMBptr->feuille.type!=_VECT) // index should be 1
		  v=symbolic(oldv._SYMBptr->sommet,v);
		else {
		  vecteur vv=*oldv._SYMBptr->feuille._VECTptr;
		  if (itend->val>0 && itend->val<=int(vv.size())) // additional check
		    vv[itend->val-1]=v;
		  v=symbolic(oldv._SYMBptr->sommet,gen(vv,oldv._SYMBptr->feuille.subtype));
		}
	      }
	    } // end else oldv.type==_VECT
	  } // end for loop
	  return sto(v,destination,in_place,contextptr);
	}
	if (indice.type!=_INT_)
	  return gensizeerr(contextptr);
	if (indice.val<0 || indice.val>(int) gen2vecteur(valeur._SYMBptr->feuille).size())
	  return gendimerr(contextptr);
	gen nvaleur;
	if (indice.val==0){
	  if (a.type==_FUNC)
	    nvaleur=symbolic(*a._FUNCptr,valeur._SYMBptr->feuille);
	  else
	    nvaleur=symbolic(at_of,makesequence(a,valeur._SYMBptr->feuille));
	}
	else {
	  nvaleur=valeur._SYMBptr->feuille;
	  if (indice==1 && nvaleur.type!=_VECT)
	    nvaleur=a;
	  else {
	    nvaleur=gen(*nvaleur._VECTptr,nvaleur.subtype);
	    (*nvaleur._VECTptr)[indice.val-1]=a;
	  }
	  nvaleur=symbolic(valeur._SYMBptr->sommet,nvaleur);
	}
	return sto(nvaleur,destination,in_place,contextptr);
      }
      if (valeur.type==_MAP){
	if (valeur.subtype==1){ // array
	  gen_map::iterator it=valeur._MAPptr->find(indice),itend=valeur._MAPptr->end();
	  if (it==itend)
	    return gendimerr(gettext("Index outside of range"));
	  if (xcas_mode(contextptr)==1)
	    in_place=true;
	}
	if (in_place){
	  (*valeur._MAPptr)[indice]=a;
	  return string2gen("Done",false);
	}
	else {
	  gen_map m(*valeur._MAPptr);
	  m[indice]=a;
	  return sto(m,destination,in_place,contextptr);
	}
      }
      vecteur * vptr=0;
      vecteur v;
      if (in_place)
	vptr=valeur._VECTptr;
      else
	v=*valeur._VECTptr;
      if (indice.type!=_VECT){
	if (indice.type!=_INT_ || indice.val<0 )
	  return gentypeerr(gettext("Bad index ")+indice.print(contextptr));
	// check size
	int is=in_place?vptr->size():v.size();
	for (;is<=indice.val;++is){
	  if (in_place)
	    vptr->push_back(zero);
	  else
	    v.push_back(zero);
	}
	// change indice's value
	if (in_place){
	  (*vptr)[indice.val]=a;
	  return valeur; // string2gen("Done",false);
	}
	else {
	  v[indice.val]=a;
	  return sto(gen(v,valeur.subtype),destination,in_place,contextptr);
	}
      }
      // here indice is of type _VECT, we store inside a matrix
      vecteur empile;
      iterateur it=indice._VECTptr->begin(),itend=indice._VECTptr->end();
      for (;;){
	if (it->type!=_INT_)
	  return gentypeerr(gettext("Bad index ")+indice.print(contextptr));
	if (!in_place)
	  empile.push_back(v);
	gen tmp;
	if (in_place){
	  if ( it->val<0 || it->val>= (int)(vptr->size()) )
	    return gendimerr(contextptr);
	  tmp=(*vptr)[it->val];
	}
	else {
	  if ( it->val<0 || it->val>= (int)(v.size()) )
	    return gendimerr(contextptr);
	  tmp=v[it->val];	  
	}
	++it;
	if (it==itend)
	  break;
	if (tmp.type!=_VECT)
	  return gentypeerr(gettext("Bad index ")+indice.print(contextptr));
	if (in_place)
	  vptr= tmp._VECTptr;
	else
	  v=*tmp._VECTptr;
      }
      --itend;
      if (in_place){
	(*vptr)[itend->val]=a;
	return valeur; // string2gen("Done",false);
      }
      v[itend->val]=a;
      vecteur oldv;
      it=indice._VECTptr->begin();
      for (;;){
	if (itend==it)
	  break;
	--itend;
	empile.pop_back();
	oldv=*(empile.back()._VECTptr);
	oldv[itend->val]=v;
	v=oldv;
      }
      return sto(v,destination,in_place,contextptr);
    }
    if (b.type==_FUNC){
      string errmsg=b.print(contextptr)+ gettext(" is a reserved word, sto not allowed:");
      *logptr(contextptr) << errmsg << endl;
      return makevecteur(string2gen(errmsg,false),a);
    }
    return gentypeerr(gettext("sto ")+b.print(contextptr)+ gettext(" not allowed!"));
  }
  symbolic symb_sto(const gen & a,gen & b,bool in_place){
    if (in_place)
      return symbolic(at_array_sto,gen(makevecteur(a,b),_SEQ__VECT));
    return symbolic(at_sto,gen(makevecteur(a,b),_SEQ__VECT));
  }
  symbolic symb_sto(const gen & e){
    return symbolic(at_sto,e);
  }
  gen _sto(const gen & a,const context * contextptr){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type!=_VECT)
      return symb_sto(a);
    if (rpn_mode(contextptr)){
      if (a._VECTptr->size()<2)
	return gentoofewargs("STO");
      gen c=a._VECTptr->back();
      a._VECTptr->pop_back();
      gen b=a._VECTptr->back();
      a._VECTptr->pop_back();
      gen tmpsto=sto(b,c,contextptr);
      if (is_undef(tmpsto)) return tmpsto;
      return gen(*a._VECTptr,_RPN_STACK__VECT);
    }
    if (a._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    return sto(a._VECTptr->front(),a._VECTptr->back(),contextptr);
  }
  static const char _sto_s []="sto";
  define_unary_function_eval4_index (30,__sto,&giac::_sto,_sto_s,&printassto,&texprintassto);
  define_unary_function_ptr5( at_sto ,alias_at_sto,&__sto,0,true); 
  // NB argument quoting for sto is done in eval in symbolic.cc

  gen _array_sto(const gen & a,const context * contextptr){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type!=_VECT ||a._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    gen value=a._VECTptr->front().eval(eval_level(contextptr),contextptr);
    return sto(value,a._VECTptr->back(),true,contextptr);
  }
  static const char _array_sto_s []="array_sto";
  static define_unary_function_eval_quoted (__array_sto,&giac::_array_sto,_array_sto_s);
  define_unary_function_ptr5( at_array_sto ,alias_at_array_sto,&__array_sto,_QUOTE_ARGUMENTS,true);

  static string printasincdec(const gen & feuille,char ch,bool tex,GIAC_CONTEXT){
    if (feuille.type!=_VECT){
      string s(tex?gen2tex(feuille,contextptr):feuille.print(contextptr));
      return xcas_mode(contextptr)?((s+":="+s+ch)+"1"):((s+ch)+ch);
    }
    vecteur & v = *feuille._VECTptr;
    if (v.size()!=2)
      return "printasincdec: bad dimension";
    gen & a=v.front();
    gen & b=v.back();
    string sa((tex?gen2tex(a,contextptr):a.print(contextptr)));
    string sb((tex?gen2tex(b,contextptr):b.print(contextptr)));
    return xcas_mode(contextptr)?sa+":="+sa+ch+sb:(sa+ch+'='+sb);
  }

  static string printasincrement(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasincdec(feuille,'+',false,contextptr);
  }

  static string printasdecrement(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasincdec(feuille,'-',false,contextptr);
  }

  static string texprintasincrement(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasincdec(feuille,'+',true,contextptr);
  }

  static string texprintasdecrement(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasincdec(feuille,'-',true,contextptr);
  }

  static string printasmultcrement(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasincdec(feuille,'*',false,contextptr);
  }

  static string printasdivcrement(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasincdec(feuille,'/',false,contextptr);
  }

  static string texprintasmultcrement(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasincdec(feuille,'*',true,contextptr);
  }

  static string texprintasdivcrement(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasincdec(feuille,'/',true,contextptr);
  }

  static gen increment(const gen & var,const gen & val_orig,bool negatif,bool mult,GIAC_CONTEXT){
    if (var.type!=_IDNT)
      return gentypeerr(gettext("Increment"));
    gen val=val_orig.eval(1,contextptr);
    if (negatif)
      val=mult?inv(val,contextptr):-val;
    if (contextptr){
      sym_tab::iterator it,itend;
      const context * cptr=contextptr;
      for(;cptr;) {
	it=cptr->tabptr->find(var._IDNTptr->id_name);
	itend=cptr->tabptr->end();
	if (it!=itend)
	  break;
	if (!cptr->previous){
	  it=cptr->globalcontextptr->tabptr->find(var._IDNTptr->id_name);
	  if (it!=itend)
	    break;
	}
	cptr=cptr->previous;
      }
      if (!cptr){
	gen prev=eval(var,1,contextptr);
	return sto(mult?(prev*val):(prev+val),var,contextptr);
	// return gensizeerr(gettext("Increment"));
      }
      return it->second=mult?(it->second*val):(it->second+val);
    }
    if (!var._IDNTptr->localvalue)
      var._IDNTptr->localvalue = new vecteur;
    vecteur * w=var._IDNTptr->localvalue;
    if (!w->empty() && var.subtype!=_GLOBAL__EVAL)
      return w->back()=w->back()+val;
    if (!var._IDNTptr->value)
      return gensizeerr(gettext("Non assigned variable"));
    return *var._IDNTptr->value=mult?(*var._IDNTptr->value*val):(*var._IDNTptr->value+val);
  }
  gen _increment(const gen & a,const context * contextptr){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type!=_VECT)
      return increment(a,1,false,false,contextptr);
    if (a.type!=_VECT || a._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    return increment(a._VECTptr->front(),a._VECTptr->back(),false,false,contextptr);
  }
  static const char _increment_s []="increment";
  static define_unary_function_eval4_index (151,__increment,&giac::_increment,_increment_s,&printasincrement,&texprintasincrement);
  define_unary_function_ptr5( at_increment ,alias_at_increment,&__increment,_QUOTE_ARGUMENTS,true); 

  gen _decrement(const gen & a,const context * contextptr){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type!=_VECT)
      return increment(a,1,true,false,contextptr);
    if (a._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    return increment(a._VECTptr->front(),a._VECTptr->back(),true,false,contextptr);
  }
  static const char _decrement_s []="decrement";
  static define_unary_function_eval4_index (153,__decrement,&giac::_decrement,_decrement_s,&printasdecrement,&texprintasdecrement);
  define_unary_function_ptr5( at_decrement ,alias_at_decrement,&__decrement,_QUOTE_ARGUMENTS,true); 

  gen _multcrement(const gen & a,const context * contextptr){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type!=_VECT)
      return increment(a,1,false,true,contextptr);
    if (a.type!=_VECT || a._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    return increment(a._VECTptr->front(),a._VECTptr->back(),false,true,contextptr);
  }
  static const char _multcrement_s []="multcrement";
  static define_unary_function_eval4_index (155,__multcrement,&giac::_multcrement,_multcrement_s,&printasmultcrement,&texprintasmultcrement);
  define_unary_function_ptr5( at_multcrement ,alias_at_multcrement,&__multcrement,_QUOTE_ARGUMENTS,true); 

  gen _divcrement(const gen & a,const context * contextptr){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type!=_VECT)
      return increment(a,1,true,true,contextptr);
    if (a._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    return increment(a._VECTptr->front(),a._VECTptr->back(),true,true,contextptr);
  }
  static const char _divcrement_s []="divcrement";
  static define_unary_function_eval4_index (157,__divcrement,&giac::_divcrement,_divcrement_s,&printasdivcrement,&texprintasdivcrement);
  define_unary_function_ptr5( at_divcrement ,alias_at_divcrement,&__divcrement,_QUOTE_ARGUMENTS,true); 

  bool is_assumed_integer(const gen & g,GIAC_CONTEXT){
    if (is_integer(g))
      return true;
    if (g.type==_IDNT) {// FIXME GIAC_CONTEXT
      gen tmp=g._IDNTptr->eval(1,g,contextptr);
      if (tmp.type==_VECT && tmp.subtype==_ASSUME__VECT){
	vecteur & v = *tmp._VECTptr;
	if (!v.empty() && (v.front()==_INT_ || v.front()==_ZINT) )
	  return true;
      }
      return is_integer(tmp);
    }
    if (g.type!=_SYMB)
      return false;
    unary_function_ptr & u=g._SYMBptr->sommet;
    gen & f=g._SYMBptr->feuille;
    if ( (u==at_neg) || (u==at_abs) )
      return is_assumed_integer(f,contextptr);
    if ( (u==at_plus) || (u==at_prod) ){
      if (f.type!=_VECT)
	return is_assumed_integer(f,contextptr);
      const_iterateur it=f._VECTptr->begin(),itend=f._VECTptr->end();
      for (;it!=itend;++it)
	if (!is_assumed_integer(*it,contextptr))
	  return false;
      return true;
    }
    return false;
  }
  // v = previous assumptions, a=the real value, direction
  // is positive for [a,+inf[, negative for ]-inf,a]
  // |direction| = 1 (large) or 2 (strict) 
  gen doubleassume_and(const vecteur & v,const gen & a,int direction,bool or_assumption,GIAC_CONTEXT){
    vecteur v_intervalle,v_excluded;
    if ( (v.size()>=3) && (v[1].type==_VECT) && (v[2].type==_VECT) ){
      v_intervalle=*v[1]._VECTptr;
      v_excluded=*v.back()._VECTptr;
    }
    gen v0=_DOUBLE_;
    v0.subtype=1;
    if (!v.empty())
      v0=v.front();
    if (!(direction %2) && !equalposcomp(v_excluded,a))
      v_excluded.push_back(a);
    if (or_assumption){ 
      // remove excluded values if they are in the interval we add
      vecteur old_v(v_excluded);
      v_excluded.clear();
      const_iterateur it=old_v.begin(),itend=old_v.end();
      for (;it!=itend;++it){
	bool a_greater_sup=ck_is_greater(a,*it,contextptr);
	if (a_greater_sup && (direction<0) )
	  continue;
	if (!a_greater_sup && (direction>0) )
	  continue;
	v_excluded.push_back(*it);
      }
    }
    if (v_intervalle.empty() || or_assumption){
      if (direction>0)
	v_intervalle.push_back(gen(makevecteur(a,plus_inf),_LINE__VECT));
      else
	v_intervalle.push_back(gen(makevecteur(minus_inf,a),_LINE__VECT));
      if (or_assumption)
	return gen(makevecteur(v0,v_intervalle,v_excluded),_ASSUME__VECT);
    }
    else { // intersection of [a.+inf[ with every interval from v_intervalle
      vecteur old_v(v_intervalle);
      v_intervalle.clear();
      const_iterateur it=old_v.begin(),itend=old_v.end();
      for (;it!=itend;++it){
	if ( (it->type!=_VECT) || (it->subtype!=_LINE__VECT) || (it->_VECTptr->size()!= 2) )
	  return gensizeerr(contextptr);
	gen i_inf(it->_VECTptr->front()),i_sup(it->_VECTptr->back());
	bool a_greater_sup=ck_is_greater(a,i_sup,contextptr);
	if (a_greater_sup){
	  if (direction<0)
	    v_intervalle.push_back(*it);
	  continue;
	}
	bool a_greater_inf=ck_is_greater(a,i_inf,contextptr);
	if (!a_greater_inf){
	  if (direction>0)
	    v_intervalle.push_back(*it);
	  continue;
	}
	if (direction>0)
	  v_intervalle.push_back(gen(makevecteur(a,i_sup),_LINE__VECT));
	else
	  v_intervalle.push_back(gen(makevecteur(i_inf,a),_LINE__VECT));
      }
    }
    return gen(makevecteur(v0,v_intervalle,v_excluded),_ASSUME__VECT);
  }
  // returns the assumed idnt name
  // used if assumptions are in OR conjonction
  gen assumesymbolic(const gen & a,gen idnt_must_be,GIAC_CONTEXT){
    if (a.type==_IDNT)
      return a._IDNTptr->eval(eval_level(contextptr),a,contextptr);
    if ( (a.type!=_SYMB) || (a._SYMBptr->feuille.type!=_VECT) )
      return gensizeerr(contextptr);
    while (idnt_must_be.type==_SYMB){
      idnt_must_be=idnt_must_be._SYMBptr->feuille;
      if ( (idnt_must_be.type==_VECT) && !(idnt_must_be._VECTptr->empty()) )
	idnt_must_be=idnt_must_be._VECTptr->front();
    }
    unary_function_ptr s(a._SYMBptr->sommet);
    vecteur v(*a._SYMBptr->feuille._VECTptr);
    int l=v.size();
    if (!l)
      return gensizeerr(contextptr);
    gen arg0(v.front()),arg1(v.back()),hyp(undef);
    if (s==at_sto){
      gen tmp(arg0);
      arg0=arg1;
      arg1=tmp;
    }
    if (s==at_and || s==at_et){
      gen tmpg=assumesymbolic(arg0,0,contextptr);
      if (is_undef(tmpg)) return tmpg;
      return assumesymbolic(arg1,0,contextptr);
    }
    if (s==at_ou || s==at_oufr){
      gen a0(assumesymbolic(arg0,0,contextptr));
      if (is_undef(a0)) return a0;
      return assumesymbolic(arg1,a0,contextptr);
    }
    if (arg0.type!=_IDNT)
      arg0=arg0.eval(eval_level(contextptr),contextptr);
    if ( (arg0.type!=_IDNT) || (!is_zero(idnt_must_be,contextptr) && (arg0!=idnt_must_be) ) )
      return gensizeerr(contextptr);
    bool or_assumption= !is_zero(idnt_must_be,contextptr) && (arg0==idnt_must_be);
    vecteur last_hyp;
    arg1=arg0._IDNTptr->eval(eval_level(contextptr),arg0,contextptr);
    if ( (arg1.type!=_VECT) || (arg1.subtype!=_ASSUME__VECT) )
      last_hyp=makevecteur(vecteur(0),vecteur(0));
    else
      last_hyp=*arg1._VECTptr;
    if (l==2){
      if (s==at_sto)
	arg1=v[0].eval(eval_level(contextptr),contextptr);
      else
	arg1=v[1].eval(eval_level(contextptr),contextptr);
      gen borne_inf(gnuplot_xmin),borne_sup(gnuplot_xmax),pas;
      if ( (s==at_equal || s==at_same) || (s==at_sto) ){     
	// ex: assume(a=[1.7,1.1,2.3])
	if (arg1.type==_VECT && arg1._VECTptr->size()>=3){
	  vecteur vtmp=*arg1._VECTptr;
	  borne_inf=evalf_double(vtmp[1],eval_level(contextptr),contextptr);
	  borne_sup=evalf_double(vtmp[2],eval_level(contextptr),contextptr);
	  pas=(borne_sup-borne_inf)/100;
	  if (vtmp.size()>3)
	    pas=evalf_double(vtmp[3],eval_level(contextptr),contextptr);
	  arg1=evalf_double(vtmp[0],eval_level(contextptr),contextptr);
	}
	gen tmp=arg1.type;
	tmp.subtype=1;
	hyp=gen(makevecteur(tmp,arg1),_ASSUME__VECT);
      }
      if (s==at_inferieur_strict) // ex: assume(a<1.7)
	hyp=doubleassume_and(last_hyp,arg1,-2,or_assumption,contextptr);
      if (s==at_inferieur_egal) 
	hyp=doubleassume_and(last_hyp,arg1,-1,or_assumption,contextptr);
      if (s==at_superieur_strict)
	hyp=doubleassume_and(last_hyp,arg1,2,or_assumption,contextptr);
      if (s==at_superieur_egal) 
	hyp=doubleassume_and(last_hyp,arg1,1,or_assumption,contextptr);
      if (!is_undef(hyp)){
	gen tmpsto=sto(hyp,arg0,contextptr); 
	if (is_undef(tmpsto)) return tmpsto;
	if ( (s==at_equal || s==at_same) || (s==at_sto) )
	  return _parameter(makevecteur(arg0,borne_inf,borne_sup,arg1,pas),contextptr);
	return arg0;
      }
    }
    return gensizeerr(contextptr);
  }
  static void purge_assume(const gen & a,GIAC_CONTEXT){
    if (a.type==_SYMB && (a._SYMBptr->sommet==at_and || a._SYMBptr->sommet==at_et || a._SYMBptr->sommet==at_ou || a._SYMBptr->sommet==at_oufr || a._SYMBptr->sommet==at_inferieur_strict || a._SYMBptr->sommet==at_inferieur_egal || a._SYMBptr->sommet==at_superieur_egal || a._SYMBptr->sommet==at_superieur_strict) ){
      purge_assume(a._SYMBptr->feuille,contextptr);
      return;
    }
    if (a.type==_VECT && !a._VECTptr->empty())
      purge_assume(a._VECTptr->front(),contextptr);
    else
      _purge(a,contextptr);
  }
  gen giac_assume(const gen & a,GIAC_CONTEXT){
    if ( (a.type==_VECT) && (a._VECTptr->size()==2) ){
      gen a1(a._VECTptr->front()),a2(a._VECTptr->back());
      if (a2.type==_INT_){
	// assume(a,real) for example
	a2.subtype=1;
	gen tmpsto=sto(gen(makevecteur(a2),_ASSUME__VECT),a1,contextptr);
	if (is_undef(tmpsto)) return tmpsto;
	return a2;
      }
      if (a2==at_real){
	a2=_DOUBLE_;
	a2.subtype=1;
	gen tmpsto=sto(gen(makevecteur(a2),_ASSUME__VECT),a1,contextptr);
	if (is_undef(tmpsto)) return tmpsto;
	return a2;
      }
      if ( (a2.type==_FUNC) && (*a2._FUNCptr==at_ou) ){
	purge_assume(a1,contextptr);
	return assumesymbolic(a1,a1,contextptr);
      }
      if (a2==at_additionally)
	return giac_additionally(a1,contextptr);
    }
    purge_assume(a,contextptr);
    return assumesymbolic(a,0,contextptr);
  }
  static const char giac_assume_s []="assume";
  static define_unary_function_eval_quoted (giac__assume,&giac::giac_assume,giac_assume_s);
  define_unary_function_ptr5( at_assume ,alias_at_assume,&giac__assume,_QUOTE_ARGUMENTS,true);

  gen giac_additionally(const gen & a,GIAC_CONTEXT){
    if ( (a.type==_VECT) && (a._VECTptr->size()==2) ){
      gen a1(a._VECTptr->front()),a2(a._VECTptr->back());
      if (a1.type!=_IDNT)
	return gensizeerr(contextptr);
      gen a1val=a1._IDNTptr->eval(1,a1,contextptr);
      if (a1val.type==_VECT && a1val.subtype==_ASSUME__VECT && !a1val._VECTptr->empty()){
	if (a2.type==_INT_){
	  // assume(a,real) for example
	  a2.subtype=1;
	  a1val._VECTptr->front()=a2;
	  return a2;
	}
	if (a2==at_real){
	  a2=_DOUBLE_;
	  a2.subtype=1;
	  a1val._VECTptr->front()=a2;
	  return a2;
	}
      }
      else {
	gen tmp=giac_assume(a,contextptr);
	if (is_undef(tmp)) return tmp;
      }
    }    
    return assumesymbolic(a,0,contextptr);
  }
  static const char giac_additionally_s []="additionally";
  static define_unary_function_eval_quoted (giac__additionally,&giac::giac_additionally,giac_additionally_s);
  define_unary_function_ptr5( at_additionally ,alias_at_additionally,&giac__additionally,_QUOTE_ARGUMENTS,true);

  // multiargs
  symbolic symb_plus(const gen & a,const gen & b){
    if (a.is_symb_of_sommet(at_plus) && !is_inf(a._SYMBptr->feuille)){
      if (b.is_symb_of_sommet(at_plus) && !is_inf(b._SYMBptr->feuille))
	return symbolic(at_plus,gen(mergevecteur(*(a._SYMBptr->feuille._VECTptr),*(b._SYMBptr->feuille._VECTptr)),_SEQ__VECT));
	else
	  return symbolic(*a._SYMBptr,b);
    }
    return symbolic(at_plus,gen(makevecteur(a,b),_SEQ__VECT));
  }

  inline bool plus_idnt_symb(const gen & a){
    return (a.type==_IDNT && strcmp(a._IDNTptr->id_name,"undef") && strcmp(a._IDNTptr->id_name,"infinity")) || (a.type==_SYMB && !is_inf(a) && (a._SYMBptr->sommet==at_prod || a._SYMBptr->sommet==at_pow || a._SYMBptr->sommet==at_neg));
  }
  
  inline bool idnt_symb_int(const gen & b){
    return (b.type==_INT_ && b.val!=0) || b.type==_ZINT || (b.type==_SYMB && !is_inf(b) && b._SYMBptr->sommet!=at_unit && b._SYMBptr->sommet!=at_equal && !equalposcomp(plot_sommets,b._SYMBptr->sommet) ) || (b.type==_IDNT && strcmp(b._IDNTptr->id_name,"undef") && strcmp(b._IDNTptr->id_name,"infinity"));
  }

  gen _plus(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT){
      if ((args.type==_IDNT) && !strcmp(args._IDNTptr->id_name,string_infinity))
	return plus_inf;
      return args;
    }
    iterateur it=args._VECTptr->begin(), itend=args._VECTptr->end();
    if (it==itend)
      return zero;
    const gen & a=*it;
    ++it;
    if (itend==it)
      return a;
    const gen & b=*it;
    ++it;
    if (it==itend){
      // improve: if a is an idnt/symb and b also do not rebuild the vector
      if (idnt_symb_int(b) && plus_idnt_symb(a)){
	if (b.is_symb_of_sommet(at_neg) && a==b._SYMBptr->feuille)
	  return chkmod(zero,a);
	if (a.is_symb_of_sommet(at_neg) && b==a._SYMBptr->feuille)
	  return chkmod(zero,b);
	return new_ref_symbolic(symbolic(at_plus,args));
      }
      if (idnt_symb_int(a) && plus_idnt_symb(b)){
	if (b.is_symb_of_sommet(at_neg) && a==b._SYMBptr->feuille)
	  return chkmod(zero,a);
	if (a.is_symb_of_sommet(at_neg) && b==a._SYMBptr->feuille)
	  return chkmod(zero,b);
	return new_ref_symbolic(symbolic(at_plus,args));
      }
      return operator_plus(a,b,contextptr);
    }
    gen sum(operator_plus(a,b,contextptr));
    for (;it!=itend;++it){
      if (sum.type==_SYMB && sum._SYMBptr->sommet==at_plus && sum._SYMBptr->feuille.type==_VECT && sum._SYMBptr->feuille._VECTptr->size()>1){
	// Add remaining elements to the symbolic sum, check float/inf/undef
	ref_vecteur * vptr=new ref_vecteur(*sum._SYMBptr->feuille._VECTptr);
	vptr->v.reserve(vptr->v.size()+(itend-it));
	for (;it!=itend;++it){
	  if (it->type==_USER && vptr->v.front().type==_USER){
	    vptr->v.front()=operator_plus(vptr->v.front(),*it,contextptr);
	    continue;
	  }
	  if (it->type<=_POLY && vptr->v.back().type<=_POLY)
	    vptr->v.back()=operator_plus(vptr->v.back(),*it,contextptr);
	  else {
	    if (is_inf(*it) || is_undef(*it) || (it->type==_SYMB && it->_SYMBptr->sommet==at_plus))
	      break;
	    if (!is_zero(*it,contextptr))
	      vptr->v.push_back(*it);
	  }
	}
	if (is_zero(vptr->v.back(),contextptr))
	  vptr->v.pop_back();
	if (vptr->v.size()==1)
	  sum=vptr->v.front();
	else
	  sum=symbolic(at_plus,gen(vptr,_SEQ__VECT));
	if (it==itend)
	  break;
      }
      operator_plus_eq(sum ,*it,contextptr);
    }
    return sum;
  }

  /* derivative of + is handled in derive.cc
  static unary_function_ptr _D_at_plus (int i) {
    return at_one;
  }
  const partial_derivative_multiargs D_at_plus(&_D_at_plus);
  */
  static const char _plus_s []="+";
  static define_unary_function_eval2_index (2,__plus,&_plus,_plus_s,&printsommetasoperator);
  define_unary_function_ptr( at_plus ,alias_at_plus ,&__plus);

  inline bool prod_idnt_symb(const gen & a){
    return (a.type==_IDNT && strcmp(a._IDNTptr->id_name,"undef") && strcmp(a._IDNTptr->id_name,"infinity")) || (a.type==_SYMB && !is_inf(a) && (a._SYMBptr->sommet==at_plus || a._SYMBptr->sommet==at_pow || a._SYMBptr->sommet==at_neg));
  }
  
  gen symb_prod(const gen & a,const gen & b){
    if (a.is_symb_of_sommet(at_neg)){
      if (b.is_symb_of_sommet(at_neg))
	return symb_prod(a._SYMBptr->feuille,b._SYMBptr->feuille);
      return -symb_prod(a._SYMBptr->feuille,b);
    }
    if (b.is_symb_of_sommet(at_neg))
      return -symb_prod(a,b._SYMBptr->feuille);
    if ((a.type<=_REAL || a.type==_FLOAT_) && is_strictly_positive(-a,context0))
      return -symb_prod(-a,b);
    if ((b.type<=_REAL || b.type==_FLOAT_) && is_strictly_positive(-b,context0))
      return -symb_prod(a,-b);
    return symbolic(at_prod,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen _prod(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return args;
    iterateur it=args._VECTptr->begin(), itend=args._VECTptr->end();
    gen prod(1);
    /*
    if (it==itend)
      return 1;
    const gen & a=*it;
    ++it;
    if (itend==it)
      return a;
    const gen & b=*it;
    ++it;
    if (it==itend){
      // improve: if a is an idnt/symb and b also do not rebuild the vector
      if (idnt_symb_int(b) && prod_idnt_symb(a))
	return new symbolic(at_prod,args);
      if (idnt_symb_int(a) && prod_idnt_symb(b))
	return new symbolic(at_prod,args);
      return operator_plus(a,b,contextptr);
    }
    gen prod(operator_times(a,b,contextptr));
    */
    for (;it!=itend;++it){
      if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_inv) && (it->_SYMBptr->feuille.type!=_VECT) )
	prod = rdiv(prod,it->_SYMBptr->feuille,contextptr);
      else
	prod = operator_times(prod,*it,contextptr);
    }
    return prod;
  }
  /*
  unary_function_ptr _D_at_prod (int i) {
    vector<int> v;
    v.push_back(i);
    vector<unary_function_ptr> w;
    w.push_back(at_prod);
    w.push_back(new unary_function_innerprod(v));
    return new unary_function_compose(w);
  }
  const partial_derivative_multiargs D_at_prod(&_D_at_prod);
  static const char _prod_s []="*";
  unary_function_eval __prod(&_prod,D_at_prod,_prod_s,&printsommetasoperator);
  unary_function_ptr at_prod (&__prod);
  */
  static const char _prod_s []="*";
  static define_unary_function_eval2_index (8,__prod,&_prod,_prod_s,&printsommetasoperator);
  define_unary_function_ptr( at_prod ,alias_at_prod ,&__prod);

  std::string cprintaspow(const gen & feuille,const char * sommetstr_orig,GIAC_CONTEXT){
    gen f(feuille);
    if (f.type==_VECT)
      f.subtype=_SEQ__VECT;
    return "pow("+f.print(contextptr)+")";
  }
  symbolic symb_pow(const gen & a,const gen & b){
    return symbolic(at_pow,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen _pow(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return args;
    vecteur & v = *args._VECTptr;
    if (v.size()!=2)
      return gensizeerr(gettext("bad pow ")+args.print(contextptr));
    const gen & a =v.front();
    const gen & b =v.back();
    // fast check for monomials, do not recreate the vector
    if (b.type==_INT_){
#ifdef COMPILE_FOR_STABILITY
      if (b.val > FACTORIAL_SIZE_LIMIT)
	setstabilityerr(contextptr);
#endif
      if (b.val==1)
	return a;
      if (a.type==_IDNT){
	if (a==undef)
	  return a;
	if (a!=unsigned_inf)
	  return b.val?symbolic(at_pow,args):gen(1);
      }
      if (a.type==_SYMB && !is_inf(a) && (a._SYMBptr->sommet==at_plus || a._SYMBptr->sommet==at_prod)){
	return b.val?symbolic(at_pow,args):gen(1);
      }
    }
    return pow(a,b,contextptr);
  }
  /* derivative of ^ is handled in derive.cc
  static gen d1_pow(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    if (v.size()!=2)
      return gensizeerr(gettext("bad pow ")+args.print(contextptr));
    if (v[1].type<=_REAL)
      return v[1]*pow(v[0],v[1]-1,contextptr);
    else
      return v[1]/v[0]*symbolic(at_pow,gen(v,_SEQ__VECT));
  }
  static gen d2_pow(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    if (v.size()!=2)
      return gensizeerr(gettext("bad pow ")+args.print(contextptr));
    return ln(v[0],contextptr)*pow(v[0],v[1],contextptr);
  }
  static define_unary_function_eval(d1_pow_eval,&d1_pow,"d1_pow");
  define_unary_function_ptr( D1_pow,alias_D1_pow,&d1_pow_eval);
  static define_unary_function_eval(d2_pow_eval,&d2_pow,"d2_pow");
  define_unary_function_ptr( D2_pow,alias_D2_pow,&d2_pow_eval);
  static unary_function_ptr d_pow(int i){
    if (i==1)
      return D1_pow;
    if (i==2)
      return D2_pow;
    return gensizeerr(contextptr);
    return 0;
  }
  static const partial_derivative_multiargs D_pow(&d_pow);
  */
  const char _pow_s []="^";
#ifndef GIAC_HAS_STO_38
  unary_function_eval __pow(14,&_pow,0,_pow_s,&printsommetasoperator,0);
#else
  Defineunary_function_eval(__pow, 14, &_pow, 0, _pow_s, &printsommetasoperator, 14);
  #define __pow (*((unary_function_eval*)&unary__pow))
#endif
  define_unary_function_ptr( at_pow ,alias_at_pow ,&__pow);

  // print power like a^b (args==1), pow(a,b) (args==0) or a**b (args==-1)
  static gen _printpow(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_zero(args,contextptr)){
      __pow.printsommet=&cprintaspow;
      return string2gen("pow",false);
    }
    else {
      __pow.printsommet=&printsommetasoperator;
      if (is_minus_one(args))
	__pow.s="**";
      else
	__pow.s="^";
      return string2gen(__pow.s,false);
    }
  }
  static const char _printpow_s []="printpow";
  static define_unary_function_eval (__printpow,&_printpow,_printpow_s);
  define_unary_function_ptr5( at_printpow ,alias_at_printpow,&__printpow,0,true);

  // static symbolic symb_powmod(const gen & a,const gen & b,const gen & n){     return symbolic(at_powmod,makevecteur(a,b,n));  }
  static symbolic symb_powmod(const gen & a){
    return symbolic(at_powmod,a);
  }
  static gen findmod(const gen & g){
    if (g.type==_MOD)
      return *(g._MODptr+1);
    if (g.type==_VECT){
      gen res;
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	res=findmod(*it);
	if (!is_exactly_zero(res))
	  return res;
      }
    }
    if (g.type==_SYMB)
      return findmod(g._SYMBptr->feuille);
    return 0;
  }
  gen _powmod(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    int s;
    if ( args.type!=_VECT || (s=args._VECTptr->size())<3 )
      return symb_powmod(args);
    vecteur v = *args._VECTptr;
    gen a=v.front();
    is_integral(a);
    gen n=v[1];
    if (n.type==_VECT){
      vecteur w =*n._VECTptr;
      iterateur it=w.begin(),itend=w.end();
      for (;it!=itend;++it){
	v[1]=*it;
	*it=_powmod(gen(v,_SEQ__VECT),contextptr);
      }
      return gen(w,n.subtype);
    }
    if (!is_integral(n) || !is_integer(n))
      return symb_powmod(args);
    gen m=v[2];
    is_integral(m);
    if (s==3 && m.type!=_SYMB) // a^n mod m
      return powmod(v.front(),v[1],m);
    // powmod(a_x%m,n,p_x) or powmod(a_x,n,m,p_x,x)
    // a^n mod p,m or m,p or a^n mod p,m,x or m,p,x wrt var x
    gen var(vx_var),p;
    bool modafter=false;
    p=unmod(m);
    m=findmod(m);
    if (is_zero(m)){
      // find m inside a
      m=findmod(a);
    }
    modafter=!is_zero(m);
    a=unmod(a);
    if (modafter && s>3)
      var=v[3];
    if (!modafter && s>3){
      m=v[2];
      p=v[3];
      if (is_integer(p)){
	p=v[2]; m=v[3]; 
      }
    }
    if (s>=5)
      var=v[4];
    vecteur lv(1,var);
    lvar(v,lv);
    if (lv.size()!=1)
      return gensizeerr(gettext("Too many variables ")+gen(lv).print(contextptr));
    gen aa=e2r(a,lv,contextptr),aan,aad,bb=e2r(p,lv,contextptr),bbn,bbd;
    fxnd(aa,aan,aad);
    if ( (aad.type==_POLY) && (aad._POLYptr->lexsorted_degree() ) )
      return gensizeerr(contextptr);
    fxnd(bb,bbn,bbd);
    if ( (bbd.type==_POLY) && (bbd._POLYptr->lexsorted_degree() ) )
      return gensizeerr(contextptr); 
    if (bbn.type!=_POLY)
      return gensizeerr(contextptr);
    modpoly A;
    if (aan.type==_POLY)
      A=polynome2poly1(*aan._POLYptr);
    else
      A.push_back(aan);
    modpoly B=polynome2poly1(*bbn._POLYptr);
    environment env;
    env.moduloon=true;
    env.modulo=m;
    // if (!B.empty() && !is_zero(m)) mulmodpoly(B,invmod(B.front(),m),&env,B);
    modpoly res=powmod(A,n,B,&env);
    polynome R(poly12polynome(res));
    if (modafter)
      modularize(R,m);
    gen Res=r2e(R,lv,contextptr)/pow(r2e(aad,lv,contextptr),n,contextptr);
    return Res;
  }
  static const char _powmod_s []="powmod";
  static define_unary_function_eval (__powmod,&giac::_powmod,_powmod_s);
  define_unary_function_ptr5( at_powmod ,alias_at_powmod,&__powmod,0,true);

  symbolic symb_inferieur_strict(const gen & a,const gen & b){
    return symbolic(at_inferieur_strict,gen(makevecteur(a,b),_SEQ__VECT));
  }
  symbolic symb_inferieur_strict(const gen & a){
    return symbolic(at_inferieur_strict,a);
  }
  gen _inferieur_strict(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_inferieur_strict(args);
    return inferieur_strict(args._VECTptr->front(),args._VECTptr->back(),contextptr);
  }
  static const char _inferieur_strict_s []="<";
  static define_unary_function_eval4_index (70,__inferieur_strict,&giac::_inferieur_strict,_inferieur_strict_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_inferieur_strict ,alias_at_inferieur_strict ,&__inferieur_strict);

  symbolic symb_inferieur_egal(const gen & a,const gen & b){
    return symbolic(at_inferieur_egal,gen(makevecteur(a,b),_SEQ__VECT));
  }
  symbolic symb_inferieur_egal(const gen & a){
    return symbolic(at_inferieur_egal,a);
  }
  static string printasinferieur_egal(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr) > 0 || calc_mode(contextptr)!=38)
      return printsommetasoperator(feuille,"<=",contextptr);
    else
      return printsommetasoperator(feuille,"≤",contextptr);
  }
  gen _inferieur_egal(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_inferieur_egal(args);
    return inferieur_egal(args._VECTptr->front(), args._VECTptr->back(),contextptr);
  }
  static const char _inferieur_egal_s []="<=";//"≤";
  static string texprintasinferieur_egal(const gen & g,const char * s,GIAC_CONTEXT){
    return texprintsommetasoperator(g,"\\leq ",contextptr);
  }
  static define_unary_function_eval4_index (72,__inferieur_egal,&giac::_inferieur_egal,_inferieur_egal_s,&printasinferieur_egal,&texprintasinferieur_egal);
  define_unary_function_ptr( at_inferieur_egal ,alias_at_inferieur_egal ,&__inferieur_egal);

  symbolic symb_superieur_strict(const gen & a,const gen & b){
    return symbolic(at_superieur_strict,gen(makevecteur(a,b),_SEQ__VECT));
  }
  symbolic symb_superieur_strict(const gen & a){
    return symbolic(at_superieur_strict,a);
  }
  gen _superieur_strict(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_superieur_strict(args);
    return superieur_strict(args._VECTptr->front(),args._VECTptr->back(),contextptr);
  }
  static const char _superieur_strict_s []=">";
  static define_unary_function_eval4_index (74,__superieur_strict,&giac::_superieur_strict,_superieur_strict_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_superieur_strict ,alias_at_superieur_strict ,&__superieur_strict);

  static string printassuperieur_egal(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr) > 0 || calc_mode(contextptr)!=38)
      return printsommetasoperator(feuille,">=",contextptr);
    else
      return printsommetasoperator(feuille,"≥",contextptr);
  }
  
  symbolic symb_superieur_egal(const gen & a,const gen & b){
    return symbolic(at_superieur_egal,gen(makevecteur(a,b),_SEQ__VECT));
  }
  symbolic symb_superieur_egal(const gen & a){
    return symbolic(at_superieur_egal,a);
  }
  gen _superieur_egal(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_superieur_egal(args);
    return superieur_egal(args._VECTptr->front(), args._VECTptr->back(),contextptr);
  }
  static const char _superieur_egal_s []=">="; // "≥";
  static string texprintassuperieur_egal(const gen & g,const char * s,GIAC_CONTEXT){
    return texprintsommetasoperator(g,"\\geq ",contextptr);
  }
  static define_unary_function_eval4_index (76,__superieur_egal,&giac::_superieur_egal,_superieur_egal_s,&printassuperieur_egal,&texprintassuperieur_egal);
  define_unary_function_ptr( at_superieur_egal ,alias_at_superieur_egal ,&__superieur_egal);

  static string printasdifferent(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr) > 0 || calc_mode(contextptr)!=38)
      return printsommetasoperator(feuille,"<>",contextptr);
    else
      return printsommetasoperator(feuille,"≠",contextptr);
  }
  // static symbolic symb_different(const gen & a,const gen & b){    return symbolic(at_different,makevecteur(a,b));  }
  static symbolic symb_different(const gen & a){
    return symbolic(at_different,a);
  }
  gen _different(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_different(args);
    return args._VECTptr->front() != args._VECTptr->back();
  }
  static const char _different_s []="!=";
  static define_unary_function_eval2_index (78,__different,&giac::_different,_different_s,&printasdifferent);
  define_unary_function_ptr( at_different ,alias_at_different ,&__different);

  static string printasof_(const gen & feuille,const char * sommetstr,int format,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) )
      return sommetstr+('('+gen2string(feuille,format,contextptr)+')');
    string s=print_with_parenthesis_if_required(feuille._VECTptr->front(),format,contextptr)+'(';
    gen & g=feuille._VECTptr->back();
    if (format==0 && g.type==_VECT && g.subtype==_SEQ__VECT)
      return s+printinner_VECT(*g._VECTptr,_SEQ__VECT,contextptr)+')';
    else
      return s+gen2string(g,format,contextptr)+')';
  }
  static string texprintasof(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasof_(feuille,sommetstr,1,contextptr);
  }
  static string printasof(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasof_(feuille,sommetstr,0,contextptr);
  }
  // Find the best interpretation of a(b) either as a function of or as a product (implicit *)

  static void warn_implicit(const gen & a,const gen &b,GIAC_CONTEXT){
#ifndef GIAC_HAS_STO_38
    if (contains(lidnt(b),i__IDNT_e))
      *logptr(contextptr) << gettext("Implicit multiplication does not work with complex numbers.")<<endl;
    else
      *logptr(contextptr) << gettext("Warning : using implicit multiplication for (") << a.print(contextptr) << ")(" << b.print(contextptr) << ')' << endl;
#endif
  }
  gen check_symb_of(const gen& a,const gen & b0,GIAC_CONTEXT){
    if ( (a.type<_IDNT || a.type==_FLOAT_) && b0.type==_VECT && b0._VECTptr->empty())
      return a;
    gen b(b0);
    if (b0.type==_VECT && b0.subtype==_SEQ__VECT && b0._VECTptr->size()==1)
      b=b0._VECTptr->front();
    if (a.type<_IDNT || a.type==_FLOAT_){
      warn_implicit(a,b,contextptr);
      return a*b;
    }
    vecteur va(lvar(a));
    if (!va.empty() && calc_mode(contextptr)==38){
      // check names in va
      bool implicit=false;
      const_iterateur it=va.begin(),itend=va.end();
      for (;it!=itend;++it){
	if (it->type!=_IDNT){
#ifdef CAS38_DISABLED
	  implicit=true;
#else
	  implicit=it->type!=_SYMB;
#endif
	  continue;
	}
	const char * ch = it->_IDNTptr->id_name;
	if (strlen(ch)==2 && (ch[0]=='F' || ch[0]=='R' || ch[0]=='X' || ch[0]=='Y') )
	  return symb_of(a,b);
	if (strlen(ch)==1 && ch[0]<='a')
	  implicit=true;
      }
      if (implicit){
	warn_implicit(a,b,contextptr);
	return a*b;
      }
    }
    vecteur vb(lvar(b));
    vecteur vab(lvar(makevecteur(a,b)));
    if (vab.size()==va.size()+vb.size())
      return symb_of(a,b);
    warn_implicit(a,b,contextptr);
    return a*b;
  }
  symbolic symb_of(const gen & a,const gen & b){
    if (b.type==_VECT && b.subtype==_SEQ__VECT && b._VECTptr->size()==1)
      return symbolic(at_of,gen(makevecteur(a,b._VECTptr->front()),_SEQ__VECT));
    return symbolic(at_of,gen(makevecteur(a,b),_SEQ__VECT));
  }
  symbolic symb_of(const gen & a){
    gen aa(a);
    if (aa.type==_VECT)
      aa.subtype=_SEQ__VECT;
    return symbolic(at_of,aa);
  }
  static bool tri2_(const char * a,const char * b){
    return strcmp(a,b)<0;
  }

  // Keep alphabetically sorted
  static const char * const aspen_quoted_name_tab[]={
    "AREA",
    "Do1VStats",
    "Do2VStats",
    "DoFinance",
    "EXTREMUM",
    "ISECT",
    "RECURSE",
    "ROOT",
    "SLOPE",
    "SOLVE",
    "SetDepend",
    "SetFreq",
    "SetIndep",
    "SetSample",
  };
  static const char * const * const aspen_quoted_name_tab_end=aspen_quoted_name_tab+sizeof(aspen_quoted_name_tab)/sizeof(char *);
  gen _of(const gen & args,const context * contextptr){
    gen qf,b,f,value;
    // *logptr(contextptr) << &qf << endl;
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_of(args);
    qf=args._VECTptr->front();
    b=args._VECTptr->back();
    bool quoteb=false;
#ifdef GIAC_HAS_STO_38
    if (qf.type==_IDNT){
      std::pair<const char * const * const,const char * const * const> pp=equal_range(aspen_quoted_name_tab,aspen_quoted_name_tab_end,qf._IDNTptr->id_name,tri2_);
      if (pp.first!=pp.second && !strcmp(*pp.first,qf._IDNTptr->id_name)){
	quoteb=true;
      }
    }
#endif
    if (!quoteb){
      if (approx_mode(contextptr))
	b=b.evalf(eval_level(contextptr),contextptr);
      else
	b=b.eval(eval_level(contextptr),contextptr);
    }
    /*
    if (qf.type!=_IDNT || !(strcmp(qf._IDNTptr->id_name,"RECURSE")==0 ||
                            strcmp(qf._IDNTptr->id_name,"SOLVE")==0 ||
                            strcmp(qf._IDNTptr->id_name,"Do2VStats")==0 ||
                            strcmp(qf._IDNTptr->id_name,"Do1VStats")==0
                           )
      )
      b=b.eval(eval_level(contextptr),contextptr);
    */
    if (rcl_38){
      if (qf.type==_IDNT){
	if (strlen(qf._IDNTptr->id_name)==2 && qf._IDNTptr->id_name[0]=='U' && qf._IDNTptr->id_name[1]>='0' && qf._IDNTptr->id_name[1]<='9'){
	  gensizeerr(gettext("Not implemented"),value);
	  return value;
	}
	if (rcl_38(value,0,qf._IDNTptr->id_name,b,true,contextptr)){
	  return value;
	}
      }
      if (qf.is_symb_of_sommet(at_double_deux_points)){
	f=qf._SYMBptr->feuille;
	if (f.type==_VECT && (*f._VECTptr)[0].type==_IDNT && (*f._VECTptr)[1].type==_IDNT){
	  if (rcl_38(value,(*f._VECTptr)[0]._IDNTptr->id_name,(*f._VECTptr)[1]._IDNTptr->id_name,b,true,contextptr)){
	    return value;
	  }
	}
      }
    }
    f=qf.eval(eval_level(contextptr),contextptr);
    if ( f.is_symb_of_sommet(at_program) && qf.type==_IDNT ){
      value=f._SYMBptr->feuille;
      if (value.type!=_VECT)
	return gensizeerr(contextptr);
      value=gen(*value._VECTptr,value.subtype); // clone
      (*value._VECTptr)[1]=b;
      // vecteur v=(*value._VECTptr);
      // v[1]=b;
      // value=gen(v,value.subtype);
      return _program(value,qf,contextptr);
    }
    return f(b,contextptr);
  }
  static const char _of_s []="of";
  static define_unary_function_eval4_index (163,__of,&giac::_of,_of_s,&printasof,&texprintasof);
  define_unary_function_ptr5( at_of ,alias_at_of,&__of,_QUOTE_ARGUMENTS,0);

  string gen2string(const gen & g,int format,GIAC_CONTEXT){
    if (format==1) 
      return gen2tex(g,contextptr); 
    else 
      return g.print(contextptr);
  }

  string print_with_parenthesis_if_required(const gen & g,int format,GIAC_CONTEXT){
    if (g.type==_SYMB || g.type==_FRAC || g.type==_CPLX || (g.type==_VECT && g.subtype==_SEQ__VECT) )
      return '('+gen2string(g,format,contextptr)+')';
    else
      return gen2string(g,format,contextptr);
  }
  
  static string printasat_(const gen & feuille,const char * sommetstr,int format,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) )
      return sommetstr+('('+gen2string(feuille,format,contextptr)+')');
    vecteur & v=*feuille._VECTptr;
    if (xcas_mode(contextptr) > 0){
      gen indice;
      if (v.back().type==_VECT)
	indice=v.back()+vecteur(v.size(),plus_one);
      else
	indice=v.back()+plus_one;
      string s;
      return print_with_parenthesis_if_required(v.front(),format,contextptr)+'['+gen2string(indice,format,contextptr)+']';
    }
    else
      return print_with_parenthesis_if_required(feuille._VECTptr->front(),format,contextptr)+'['+gen2string(feuille._VECTptr->back(),format,contextptr)+']';
  }

  static string printasat(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasat_(feuille,sommetstr,0,contextptr);
  }
  static string texprintasat(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return printasat_(feuille,sommetstr,1,contextptr);
  }
  symbolic symb_at(const gen & a,const gen & b,GIAC_CONTEXT){
    if (xcas_mode(contextptr)){
      gen bb;
      if (b.type==_VECT)
	bb=b-vecteur(b._VECTptr->size(),plus_one);
      else
	bb=b-plus_one;
      return symbolic(at_at,gen(makevecteur(a,bb),_SEQ__VECT));
    }
    else
      return symbolic(at_at,gen(makevecteur(a,b),_SEQ__VECT));
  }
  symbolic symb_at(const gen & a){
    gen aa(a);
    if (aa.type==_VECT)
      aa.subtype=_SEQ__VECT;
    return symbolic(at_at,aa);
  }
  gen _at(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_at(args);
    vecteur & v=*args._VECTptr;
    if (v.size()!=2)
      return gensizeerr(contextptr);
    if (rcl_38){
      if (v.front().type==_IDNT){
	gen value;
	if (rcl_38(value,0,v.front()._IDNTptr->id_name,v.back(),false,contextptr)){
	  return value;
	}
      }
      if (v.front().is_symb_of_sommet(at_double_deux_points)){
	gen & f=v.front()._SYMBptr->feuille;
	if (f[0].type==_IDNT && f[1].type==_IDNT){
	  gen value;
	  if (rcl_38(value,f[0]._IDNTptr->id_name,f[1]._IDNTptr->id_name,v.back(),false,contextptr)){
	    return value;
	  }
	}
      }
    }
    gen a=v.front().eval(eval_level(contextptr),contextptr);
    gen b=v.back().eval(eval_level(contextptr),contextptr);
    if (a.type==_MAP){
      gen_map::const_iterator it=a._MAPptr->find(b),itend=a._MAPptr->end();
      if (it!=itend)
	return it->second;
      return symb_at(makevecteur(v.front(),b));
    }
    return a.operator_at(b,contextptr);
  }
  static const char _at_s []="at";
  static define_unary_function_eval4_index (165,__at,&giac::_at,_at_s,&printasat,&texprintasat);
  define_unary_function_ptr5( at_at ,alias_at_at,&__at,_QUOTE_ARGUMENTS,0);

  gen _table(const gen & arg,GIAC_CONTEXT){
    if ( arg.type==_STRNG && arg.subtype==-1) return  arg;
    vecteur v(gen2vecteur(arg));
    const_iterateur it=v.begin(),itend=v.end();
    gen_map m(ptr_fun(islesscomplexthanf));
    for (;it!=itend;++it){
      if (it->is_symb_of_sommet(at_equal)){
	gen & f =it->_SYMBptr->feuille;
	if (f.type==_VECT && f._VECTptr->size()==2){
	  vecteur & w=*f._VECTptr;
	  m[w.front()]=w.back();
	}
      }
    }
    return m;
  }
  static const char _table_s []="table";
  static define_unary_function_eval (__table,&giac::_table,_table_s);
  define_unary_function_ptr5( at_table ,alias_at_table,&__table,0,true);

  static string printasand(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (abs_calc_mode(contextptr)==38)
      return printsommetasoperator(feuille," AND ",contextptr);
    if (calc_mode(contextptr)==1)
      return printsommetasoperator(feuille," && ",contextptr);
    if (xcas_mode(contextptr) > 0)
      return printsommetasoperator(feuille," and ",contextptr);
    else
      return "("+printsommetasoperator(feuille,sommetstr,contextptr)+")";
  }
  static string texprintasand(const gen & g,const char * s,GIAC_CONTEXT){
    return texprintsommetasoperator(g,"\\mbox{ and }",contextptr);
  }
  symbolic symb_and(const gen & a,const gen & b){
    return symbolic(at_and,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen and2(const gen & a,const gen & b){
    return a && b;
  }
  gen _and(const gen & arg,GIAC_CONTEXT){
    if ( arg.type==_STRNG && arg.subtype==-1) return  arg;
    if (arg.type==_VECT && arg.subtype==_SEQ__VECT && arg._VECTptr->size()==2)
      return apply(equaltosame(arg._VECTptr->front()).eval(eval_level(contextptr),contextptr),equaltosame(arg._VECTptr->back()).eval(eval_level(contextptr),contextptr),and2);
    gen args=apply(arg,equaltosame);
    if (args.type!=_VECT || args._VECTptr->empty())
      return args.eval(eval_level(contextptr),contextptr);
    vecteur::const_iterator it=args._VECTptr->begin(),itend=args._VECTptr->end();
    gen res=eval(*it,eval_level(contextptr),contextptr);
    ++it;
    for (;it!=itend;++it){
      if (res.type==_INT_ && res.val==0)
	return res;
      res = res && eval(*it,eval_level(contextptr),contextptr);
    }
    return res;
  }
  static const char _and_s []="and";
  static define_unary_function_eval4_index (67,__and,&giac::_and,_and_s,&printasand,&texprintasand);
  define_unary_function_ptr5( at_and ,alias_at_and,&__and,_QUOTE_ARGUMENTS,T_AND_OP);

  static string texprintasor(const gen & g,const char * s,GIAC_CONTEXT){
    return texprintsommetasoperator(g,"\\mbox{ or }",contextptr);
  }
  static string printasor(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (abs_calc_mode(contextptr)==38)
      return printsommetasoperator(feuille," OR ",contextptr);
    if (calc_mode(contextptr)==1)
      return printsommetasoperator(feuille," || ",contextptr);
    if (xcas_mode(contextptr) > 0)
      return printsommetasoperator(feuille," or ",contextptr);
    else
      return "("+printsommetasoperator(feuille,sommetstr,contextptr)+")";
  }
  symbolic symb_ou(const gen & a,const gen & b){
    return symbolic(at_ou,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen ou2(const gen & a,const gen & b){
    return a || b;
  }
  gen _ou(const gen & arg,GIAC_CONTEXT){
    if ( arg.type==_STRNG && arg.subtype==-1) return  arg;
    if (arg.type==_VECT && arg.subtype==_SEQ__VECT && arg._VECTptr->size()==2)
      return apply(equaltosame(arg._VECTptr->front()).eval(eval_level(contextptr),contextptr),equaltosame(arg._VECTptr->back()).eval(eval_level(contextptr),contextptr),ou2);
    gen args=apply(arg,equaltosame);
    if (args.type!=_VECT || args._VECTptr->empty())
      return eval(args,eval_level(contextptr),contextptr);
    vecteur::const_iterator it=args._VECTptr->begin(),itend=args._VECTptr->end();
    gen res=eval(*it,eval_level(contextptr),contextptr);
    ++it;
    for (;it!=itend;++it){
      if (res.type==_INT_ && res.val)
	return res;
      res = res || eval(*it,eval_level(contextptr),contextptr);
    }
    return res;
  }
  static const char _ou_s []="or";
  static define_unary_function_eval4_index (69,__ou,&giac::_ou,_ou_s,&printasor,&texprintasor);
  define_unary_function_ptr5( at_ou ,alias_at_ou,&__ou,_QUOTE_ARGUMENTS,T_AND_OP);

  gen xor2(const gen & a,const gen & b,GIAC_CONTEXT){
    return is_zero(a,contextptr) ^ is_zero(b,contextptr);
  }
  gen _xor(const gen & arg,GIAC_CONTEXT){
    if ( arg.type==_STRNG && arg.subtype==-1) return  arg;
    if (arg.type==_VECT && arg.subtype==_SEQ__VECT && arg._VECTptr->size()==2)
      return apply(
		   equaltosame(arg._VECTptr->front()).eval(eval_level(contextptr),contextptr),
		   equaltosame(arg._VECTptr->back()).eval(eval_level(contextptr),contextptr),
		   contextptr,xor2);
    gen args=eval(apply(arg,equaltosame),eval_level(contextptr),contextptr);
    if (args.type!=_VECT)
      return args;
    vecteur::const_iterator it=args._VECTptr->begin(),itend=args._VECTptr->end();
    gen res=*it;
    ++it;
    for (;it!=itend;++it){
      if (is_zero(res,contextptr))
	res=*it;
      else
	res = !(*it);
    }
    return res;
  }
#ifdef GIAC_HAS_STO_38
  static const char _xor_s []=" XOR ";
#else
  static const char _xor_s []=" xor ";
#endif
  static define_unary_function_eval4_index (117,__xor,&giac::_xor,_xor_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr5( at_xor ,alias_at_xor,&__xor,_QUOTE_ARGUMENTS,0);

  symbolic symb_min(const gen & a,const gen & b){
    return symbolic(at_min,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen _min(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return args;
    if (args.type==_POLY){
      vector< monomial<gen> >::const_iterator it=args._POLYptr->coord.begin(),itend=args._POLYptr->coord.end();
      if (it==itend)
	return undef;
      gen m(it->value);
      for (++it;it!=itend;++it){
	if (is_strictly_greater(m,it->value,contextptr))
	  m=it->value;
      }
      return m;
    }
    vecteur::const_iterator it=args._VECTptr->begin(),itend=args._VECTptr->end();
    if (it==itend)
      return gendimerr(contextptr);
    if (ckmatrix(args)){
      gen res=*it;
      for (++it;it!=itend;++it){
	res=apply(res,*it,contextptr,min);
      }
      return res;
    }
    if (itend-it==2 && it->type==_VECT && (it+1)->type==_VECT )
      return matrix_apply(*it,*(it+1),contextptr,min);
    gen res=*it;
    ++it;
    for (;it!=itend;++it)
      res = min(res,*it,contextptr);
    return res;
  }
  static const char _min_s []="min";
  static define_unary_function_eval (giac__min,&giac::_min,_min_s);
  define_unary_function_ptr5( at_min ,alias_at_min,&giac__min,0,true);

  symbolic symb_max(const gen & a,const gen & b){
    return symbolic(at_max,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen _max(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_POLY){
      vector< monomial<gen> >::const_iterator it=args._POLYptr->coord.begin(),itend=args._POLYptr->coord.end();
      if (it==itend)
	return undef;
      gen m(it->value);
      for (++it;it!=itend;++it){
	if (is_strictly_greater(it->value,m,contextptr))
	  m=it->value;
      }
      return m;
    }
    if (args.type!=_VECT)
      return args;
    vecteur::const_iterator it=args._VECTptr->begin(),itend=args._VECTptr->end();
    if (itend==it)
      return gendimerr(contextptr);
    if (itend-it==1)
      return _max(*it,contextptr);
    if (ckmatrix(args)){
      gen res=*it;
      for (++it;it!=itend;++it){
	res=apply(res,*it,contextptr,max);
      }
      return res;
    }
    if (itend-it==2 && it->type==_VECT && (it+1)->type==_VECT )
      return matrix_apply(*it,*(it+1),contextptr,max);
    gen res=*it;
    ++it;
    for (;it!=itend;++it)
      res = max(res,*it,contextptr);
    return res;
  }
  static const char _max_s []="max";
  static define_unary_function_eval (giac__max,&giac::_max,_max_s);
  define_unary_function_ptr5( at_max ,alias_at_max,&giac__max,0,true);

  // static symbolic symb_gcd(const gen & a,const gen & b){    return symbolic(at_gcd,makevecteur(a,b));  }
  gen _gcd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_integer(args))
      return abs(args,contextptr);
    if (args.type!=_VECT)
      return args;
    if (debug_infolevel)
      cerr << "gcd begin " << clock() << endl;
    vecteur::const_iterator it=args._VECTptr->begin(),itend=args._VECTptr->end();
    if (ckmatrix(args) && itend-it==2)
      return apply(*it,*(it+1),gcd);
    gen res(0);
    for (;it!=itend;++it)
      res=gcd(res,*it);
    return res;
  }
  static const char _gcd_s []="gcd";
  static define_unary_function_eval (__gcd,&giac::_gcd,_gcd_s);
  define_unary_function_ptr5( at_gcd ,alias_at_gcd,&__gcd,0,true);

  // static symbolic symb_lcm(const gen & a,const gen & b){    return symbolic(at_lcm,makevecteur(a,b));  }
  gen _lcm(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return args;
    vecteur::const_iterator it=args._VECTptr->begin(),itend=args._VECTptr->end();
    if (itend==it)
      return 1;
    if (ckmatrix(args) && itend-it==2)
      return apply(*it,*(it+1),lcm);
    gen res(*it);
    for (++it;it!=itend;++it)
      res=lcm(res,*it);
    return res;
  }
  static const char _lcm_s []="lcm";
  static define_unary_function_eval (__lcm,&giac::_lcm,_lcm_s);
  define_unary_function_ptr5( at_lcm ,alias_at_lcm,&__lcm,0,true);

  // static symbolic symb_egcd(const gen & a,const gen & b){    return symbolic(at_egcd,makevecteur(a,b));  }
  gen _egcd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || args._VECTptr->empty() )
      return gensizeerr(contextptr);
    vecteur & a = *args._VECTptr;
    if ( (a.front().type==_VECT) && (a.back().type==_VECT) ){
      vecteur u,v,d;
      egcd(*a.front()._VECTptr,*a.back()._VECTptr,0,u,v,d);
      return gen(makevecteur(u,v,d),_POLY1__VECT);
    }
    vecteur lv;
    if (a.size()==3)
      lv=vecteur(1,a[2]);
    else
      lv=vecteur(1,vx_var);
    lvar(args,lv);
    gen aa=e2r(a[0],lv,contextptr),aan,aad,bb=e2r(a[1],lv,contextptr),bbn,bbd;
    fxnd(aa,aan,aad);
    if ( (aad.type==_POLY) && (aad._POLYptr->lexsorted_degree() ) )
      return gensizeerr(contextptr);
    fxnd(bb,bbn,bbd);
    if ( (bbd.type==_POLY) && (bbd._POLYptr->lexsorted_degree() ) )
      return gensizeerr(contextptr); 
    gen u,v,d;
    if ( (aan.type==_POLY) && (bbn.type==_POLY) ){
      polynome un(aan._POLYptr->dim),vn(aan._POLYptr->dim),dn(aan._POLYptr->dim);
      egcd(*aan._POLYptr,*bbn._POLYptr,un,vn,dn);
      u=un;
      v=vn;
      d=dn;
    }
    else {
      if (aan.type==_POLY){
	u=zero;
	v=plus_one;
	d=bbn;
      }
      else {
	u=plus_one;
	v=zero;
	d=aan;
      }
    }
    u=r2e(u*aad,lv,contextptr);
    v=r2e(v*bbd,lv,contextptr);
    d=r2e(d,lv,contextptr);
    return makevecteur(u,v,d);
  }
  static const char _egcd_s []="egcd";
  static define_unary_function_eval (__egcd,&giac::_egcd,_egcd_s);
  define_unary_function_ptr5( at_egcd ,alias_at_egcd,&__egcd,0,true);

  // static symbolic symb_iegcd(const gen & a,const gen & b){    return symbolic(at_iegcd,makevecteur(a,b));  }
  gen _iegcd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!check_2d_vecteur(args)) return gensizeerr(contextptr);
    gen a(args._VECTptr->front()),b(args._VECTptr->back()),u,v,d;
    if (!is_integral(a) || !is_integral(b))
      return gentypeerr(contextptr);
    egcd(a,b,u,v,d);
    return makevecteur(u,v,d);
  }
  static const char _iegcd_s []="iegcd";
  static define_unary_function_eval (__iegcd,&giac::_iegcd,_iegcd_s);
  define_unary_function_ptr5( at_iegcd ,alias_at_iegcd,&__iegcd,0,true);

  static const char _bezout_entiers_s []="bezout_entiers";
  static define_unary_function_eval (__bezout_entiers,&giac::_iegcd,_bezout_entiers_s);
  define_unary_function_ptr5( at_bezout_entiers ,alias_at_bezout_entiers,&__bezout_entiers,0,true);

  gen symb_equal(const gen & a,const gen & b){
    return symbolic(at_equal,gen(makevecteur(a,b),_SEQ__VECT));
  }
  static string printasequal(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
#ifdef GIAC_HAS_STO_38
    return printsommetasoperator(feuille," = ",contextptr);
#else
    return printsommetasoperator(feuille,"=",contextptr);
#endif
  }
  gen _equal(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if ((a.type!=_VECT) || (a._VECTptr->size()!=2))
      return equal(a,gen(vecteur(0),_SEQ__VECT),contextptr);
    return equal( (*(a._VECTptr))[0],(*(a._VECTptr))[1],contextptr );
  }
  static const char _equal_s []="=";
  static define_unary_function_eval4_index (80,__equal,&giac::_equal,_equal_s,&printasequal,&texprintsommetasoperator);
  define_unary_function_ptr( at_equal ,alias_at_equal ,&__equal);

  static string printassame(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr) > 0)
      return printsommetasoperator(feuille," = ",contextptr);
    else
      return "("+printsommetasoperator(feuille,sommetstr,contextptr)+")";
  }
  symbolic symb_same(const gen & a,const gen & b){
    return symbolic(at_same,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen symb_same(const gen & a){
    return symbolic(at_same,a);
  }
  gen _same(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if ((a.type!=_VECT) || (a._VECTptr->size()!=2))
      return symb_same(a);
    if (a._VECTptr->front().type==_SYMB || a._VECTptr->back().type==_SYMB){
      if (!is_inf(a._VECTptr->front()) && !is_undef(a._VECTptr->front()) && !is_inf(a._VECTptr->back()) && !is_undef(a._VECTptr->back()) && a._VECTptr->front().type!=_VECT &&a._VECTptr->back().type!=_VECT )
	return is_zero(a._VECTptr->front()-a._VECTptr->back(),contextptr);
    }
    return operator_equal(a._VECTptr->front(),a._VECTptr->back(),contextptr);
  }
  static const char _same_s []="==";
  static define_unary_function_eval4_index (148,__same,&giac::_same,_same_s,&printassame,&texprintsommetasoperator);
  define_unary_function_ptr( at_same ,alias_at_same ,&__same);

  // ******************
  // Arithmetic functions
  // *****************

  // symbolic symb_smod(const gen & a,const gen & b){ return symbolic(at_smod,makevecteur(a,b));  }
  gen _smod(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!check_2d_vecteur(args)) return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    if (ckmatrix(v))
      return apply(v[0],v[1],smod);
    if (!is_cinteger(v.back()) )
      return v.front()-v.back()*_round(v.front()/v.back(),contextptr);
    return smod(args._VECTptr->front(),args._VECTptr->back());
  }
  static const char _smod_s []="smod";
  static define_unary_function_eval (__smod,&giac::_smod,_smod_s);
  define_unary_function_ptr5( at_smod ,alias_at_smod,&__smod,0,true);

  // symbolic symb_rdiv(const gen & a,const gen & b){     return symbolic(at_rdiv,makevecteur(a,b));  }
  gen _rdiv(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!check_2d_vecteur(args)) return gensizeerr(contextptr);
    return rdiv(args._VECTptr->front(),args._VECTptr->back(),contextptr);
  }
  static const char _rdiv_s []="rdiv";
  static define_unary_function_eval (__rdiv,&giac::_rdiv,_rdiv_s);
  define_unary_function_ptr5( at_rdiv ,alias_at_rdiv,&__rdiv,0,true);

  gen unmod(const gen & g){
    if (g.type==_MOD)
      return *g._MODptr;
    if (g.type==_VECT)
      return apply(g,unmod);
    if (g.type==_SYMB)
      return symbolic(g._SYMBptr->sommet,unmod(g._SYMBptr->feuille));
    return g;
  }
  gen unmodunprod(const gen & g){
    gen h=unmod(g);
    if (h.is_symb_of_sommet(at_prod))
      h=_prod(h._SYMBptr->feuille,context0); // ok
    return h;
  }

  gen irem(const gen & a,const gen & b){
    gen q;
    return irem(a,b,q);
  }
  // symbolic symb_irem(const gen & a,const gen & b){    return symbolic(at_irem,makevecteur(a,b));  }
  gen _normalmod(const gen & g,GIAC_CONTEXT);
  gen _irem(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!check_2d_vecteur(args)) return gensizeerr(contextptr);
    if (ckmatrix(args))
      return apply(args._VECTptr->front(),args._VECTptr->back(),irem);
    gen q;
    vecteur & v=*args._VECTptr;
    if (v.front().type==_SYMB){
      gen arg=v.front()._SYMBptr->feuille;
      if (v.front()._SYMBptr->sommet==at_pow && arg.type==_VECT && arg._VECTptr->size()==2 ){
	if (is_integer(arg._VECTptr->front()) && is_integer(arg._VECTptr->back()) )
	  return powmod(_irem(gen(makevecteur(arg._VECTptr->front(),v.back()),_SEQ__VECT),contextptr),arg._VECTptr->back(),v.back());
	return pow(_irem(gen(makevecteur(arg._VECTptr->front(),v.back()),_SEQ__VECT),contextptr),arg._VECTptr->back(),contextptr);
      }
      if (v.front()._SYMBptr->sommet==at_neg)
	return _irem(gen(makevecteur(simplifier((v.back()-1)*arg,contextptr),v.back()),_SEQ__VECT),contextptr);
      if (v.front()._SYMBptr->sommet==at_prod || v.front()._SYMBptr->sommet==at_plus){
	return v.front()._SYMBptr->sommet(_irem(gen(makevecteur(arg,v.back()),_SEQ__VECT),contextptr),contextptr);
      }
      if (v.front()._SYMBptr->sommet==at_inv){
	gen g=invmod(arg,v.back());
	if (is_positive(g,contextptr))
	  return g;
	else
	  return g+v.back();
      }
      arg=_normalmod(makevecteur(arg,v.back()),contextptr);
      return unmod(v.front()._SYMBptr->sommet(arg,contextptr));
    }
    if (v.front().type==_FRAC){
      gen g=invmod(v.front()._FRACptr->den,v.back());
      if (!is_positive(g,contextptr))
	g= g+v.back();
      return _irem(gen(makevecteur(v.front()._FRACptr->num*g,v.back()),_SEQ__VECT),contextptr);
    }
    if (v.front().type==_VECT){
      const_iterateur it=v.front()._VECTptr->begin(),itend=v.front()._VECTptr->end();
      vecteur res;
      for (;it!=itend;++it)
	res.push_back(_irem(gen(makevecteur(*it,v.back()),_SEQ__VECT),contextptr));
      return gen(res,v.front().subtype);
    }
    if (v.front().type==_IDNT)
      return v.front();
    gen vf(v.front()),vb(v.back());
    if (!is_integral(vf) || !is_integral(vb) )
      return symbolic(at_irem,args);
    gen r=irem(vf,vb,q);
    if (is_integer(vb) && is_strictly_positive(-r,contextptr)){
      if (is_strictly_positive(vb,contextptr)){
	r = r + vb;
	q=q-1;
      }
      else {
	r = r - vb;
	q=q+1;
      }
    }
    return r;
  }
  static const char _irem_s []="irem";
  static define_unary_function_eval (__irem,&giac::_irem,_irem_s);
  define_unary_function_ptr5( at_irem ,alias_at_irem,&__irem,0,true);

  static const char _mods_s []="mods";
  static define_unary_function_eval (__mods,&giac::_smod,_mods_s);
  define_unary_function_ptr5( at_mods ,alias_at_mods,&__mods,0,true);

  gen _quote_pow(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gentypeerr(contextptr);
    vecteur & v = *args._VECTptr;
    if (ckmatrix(v.front()))
      return pow(v.front(),v.back(),contextptr);
    return symbolic(at_pow,args);
  }
  static const char _quote_pow_s []="&^";
  static define_unary_function_eval4_index (120,__quote_pow,&giac::_quote_pow,_quote_pow_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_quote_pow ,alias_at_quote_pow ,&__quote_pow);

  // symbolic symb_iquo(const gen & a,const gen & b){ return symbolic(at_iquo,makevecteur(a,b));  }
  bool is_integral(gen & indice){
    if (is_cinteger(indice))
      return true;
    if (indice.type==_FLOAT_){
      gen tmp=get_int(indice._FLOAT_val);
      if (is_zero(tmp-indice)){
	indice=tmp;
	return true;
      }
    }
    if (indice.type==_DOUBLE_){
      gen tmp=int(indice._DOUBLE_val);
      if (is_zero(tmp-indice)){
	indice=tmp;
	return true;
      }
    }
    return false;
  }
  gen Iquo(const gen & f0,const gen & b0){
    if (f0.type==_VECT)
      return apply1st(f0,b0,Iquo);
    gen f(f0),b(b0);
    if (!is_integral(f) || !is_integral(b) )
      return gensizeerr(gettext("Iquo")); // return symbolic(at_iquo,args);
    if (is_exactly_zero(b))
      return 0;
    return (f-_irem(gen(makevecteur(f,b),_SEQ__VECT),context0))/b; // ok
  }
  gen _iquo(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!check_2d_vecteur(args)) return gensizeerr(contextptr);
    gen & f=args._VECTptr->front();
    gen & b=args._VECTptr->back();
    if (ckmatrix(args))
      return apply(f,b,iquo);
    return Iquo(f,b);
  }
  static const char _iquo_s []="iquo";
  static define_unary_function_eval (__iquo,&giac::_iquo,_iquo_s);
  define_unary_function_ptr5( at_iquo ,alias_at_iquo,&__iquo,0,true);

  static vecteur iquorem(const gen & a,const gen & b){
    gen q,r;
    r=irem(a,b,q);
    return makevecteur(q,r);
  }
  // symbolic symb_iquorem(const gen & a,const gen & b){    return symbolic(at_iquorem,makevecteur(a,b));  }
  gen _iquorem(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!check_2d_vecteur(args)) return gensizeerr(contextptr);
    vecteur v=*args._VECTptr;
    if (!is_integral(v.front()) || !is_integral(v.back()) )
      return gensizeerr(contextptr); // symbolic(at_iquorem,args);
    return iquorem(args._VECTptr->front(),args._VECTptr->back());
  }
  static const char _iquorem_s []="iquorem";
  static define_unary_function_eval (__iquorem,&giac::_iquorem,_iquorem_s);
  define_unary_function_ptr5( at_iquorem ,alias_at_iquorem,&__iquorem,0,true);

  static symbolic symb_quorem(const gen & a,const gen & b){    return symbolic(at_quorem,makevecteur(a,b));  }
  gen quorem(const gen & a,const gen & b){
    if ((a.type!=_VECT) || (b.type!=_VECT))
      return symb_quorem(a,b);
    if (b._VECTptr->empty())
      return gensizeerr(gettext("Division by 0"));
    vecteur q,r;
    environment * env=new environment;
    DivRem(*a._VECTptr,*b._VECTptr,env,q,r,true);
    delete env;
    return makevecteur(gen(q,_POLY1__VECT),gen(r,_POLY1__VECT));
  }
  gen _quorem(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ((args.type!=_VECT) || (args._VECTptr->size()<2) )
      return gensizeerr(contextptr);
    vecteur & a =*args._VECTptr;
    if ( (a.front().type==_VECT) && (a[1].type==_VECT))
      return quorem(a.front(),a[1]);
    if ( (a.front().type==_POLY) && (a[1].type==_POLY)){
      int dim=a.front()._POLYptr->dim;
      if (a[1]._POLYptr->dim!=dim)
	return gendimerr(contextptr);
      if (a.size()==3 && a.back().type==_INT_){
	polynome rem,quo;
	if ( !divrem1(*a.front()._POLYptr,*a[1]._POLYptr,quo,rem,args._VECTptr->back().val) )
	  return gensizeerr(gettext("Unable to divide, perhaps due to rounding error")+a.front().print(contextptr)+" / "+a.back().print(contextptr));
	return makevecteur(quo,rem);
      }
      vecteur aa(polynome2poly1(*a.front()._POLYptr,1));
      vecteur bb(polynome2poly1(*a.back()._POLYptr,1));
      vecteur q,r;
      DivRem(aa,bb,0,q,r);
      return makevecteur(poly12polynome(q,1,dim),poly12polynome(r,1,dim));
    }
    vecteur lv;
    if (a.size()>=3 && a[2].type!=_INT_)
      lv=vecteur(1,unmodunprod(a[2]));
    else
      lv=vecteur(1,vx_var);
    lvar(args,lv);
    gen aa=e2r(a[0],lv,contextptr),aan,aad,bb=e2r(a[1],lv,contextptr),bbn,bbd;
    fxnd(aa,aan,aad);
    if ( (aad.type==_POLY) && (aad._POLYptr->lexsorted_degree() ) )
      return gensizeerr(contextptr);
    fxnd(bb,bbn,bbd);
    if ( (bbd.type==_POLY) && (bbd._POLYptr->lexsorted_degree() ) )
      return gensizeerr(contextptr);
    gen u,v;
    gen ad(r2e(aad,lv,contextptr));
    if ( (aan.type==_POLY) && (bbn.type==_POLY) ){
      if (a.size()>=3 && a.back().type==_INT_){
	polynome rem,quo;
	if ( !divrem1(*aan._POLYptr,*bbn._POLYptr,quo,rem,args._VECTptr->back().val) )
	  return gensizeerr(gettext("Unable to divide, perhaps due to rounding error")+aan.print(contextptr)+" / "+bbn.print(contextptr));
	u=rdiv(r2e(bbd,lv,contextptr),ad,contextptr)*r2e(quo,lv,contextptr);
	v=inv(ad,contextptr)*r2e(rem,lv,contextptr);
	return makevecteur(u,v);
      }
      vecteur aav(polynome2poly1(*aan._POLYptr,1)),bbv(polynome2poly1(*bbn._POLYptr,1)),un,vn;
      environment env;
      DivRem(aav,bbv,&env,un,vn);
      vecteur lvprime(lv.begin()+1,lv.end());
      u=rdiv(r2e(bbd,lv,contextptr),ad,contextptr)*symb_horner(*r2e(un,lvprime,contextptr)._VECTptr,lv.front());
      v=inv(ad,contextptr)*symb_horner(*r2e(vn,lvprime,contextptr)._VECTptr,lv.front());
      return makevecteur(u,v);
    }
    else {
      if ( (bbn.type!=_POLY) || !bbn._POLYptr->lexsorted_degree() ){
	u=rdiv(aan,bbn,contextptr);
	v=zero;
      }
      else {
	u=zero;
	v=aan;
      }
    }
    // aan=u*bbn+v -> aan/aad=u*bbd/aad * bbn/bbd +v/aad
    u=r2e(u*bbd,lv,contextptr);
    v=r2e(v,lv,contextptr);
    return makevecteur(rdiv(u,ad,contextptr),rdiv(v,ad,contextptr));
  }
  static const char _quorem_s []="quorem";
  static define_unary_function_eval (__quorem,&giac::_quorem,_quorem_s);
  define_unary_function_ptr5( at_quorem ,alias_at_quorem,&__quorem,0,true);

  // symbolic symb_quo(const gen & a,const gen & b){    return symbolic(at_quo,makevecteur(a,b));  }
  gen _quo(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return _quorem(args,contextptr)[0];
  }
  static const char _quo_s []="quo";
  static define_unary_function_eval (__quo,&giac::_quo,_quo_s);
  define_unary_function_ptr5( at_quo ,alias_at_quo,&__quo,0,true);

  // symbolic symb_rem(const gen & a,const gen & b){    return symbolic(at_rem,makevecteur(a,b));  }
  gen _rem(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->size()>=3 && args[2].type==_VECT){
      vecteur v = *args._VECTptr;
      gen g(_WITH_COCOA);
      g.subtype=_INT_GROEBNER;
      v.push_back(symb_equal(g,0));
      return _greduce(gen(v,_SEQ__VECT),contextptr);
    }
    return _quorem(args,contextptr)[1];
  }
  static const char _rem_s []="rem";
  static define_unary_function_eval (__rem,&giac::_rem,_rem_s);
  define_unary_function_ptr5( at_rem ,alias_at_rem,&__rem,0,true);

  gen double2gen(double d){
    if (my_isinf(d))
      return d;
    ref_mpz_t * m= new ref_mpz_t;
    mpz_set_d(m->z,d);
    return m;
  }
  static symbolic symb_floor(const gen & a){
    return symbolic(at_floor,a);
  }
  gen apply_unit(const gen & args,const gen_op_context & f,GIAC_CONTEXT){
    return symbolic(at_unit,gen(makevecteur(f(args._SYMBptr->feuille[0],contextptr),args._SYMBptr->feuille[1]),_SEQ__VECT));  
  }
  gen _floor(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_equal(args))
      return apply_to_equal(args,_floor,contextptr);
    if (is_inf(args)||is_undef(args))
      return args;
    if (args.is_symb_of_sommet(at_unit))
      return apply_unit(args,_floor,contextptr);
    if (args.type==_VECT)
      return apply(args,contextptr,_floor);
    if (args.type==_CPLX)
      return _floor(*args._CPLXptr,contextptr)+cst_i*_floor(*(args._CPLXptr+1),contextptr);
    if ( (args.type==_INT_) || (args.type==_ZINT))
      return args;
    if (args.type==_FRAC){
      gen n=args._FRACptr->num,d=args._FRACptr->den;
      if ( ((n.type==_INT_) || (n.type==_ZINT)) && ( (d.type==_INT_) || (d.type==_ZINT)) ){
	if (is_positive(args,contextptr))
	  return iquo(n,d);
	else
	  return iquo(n,d)-1;
      }
    }
    /* old code, changed for floor(sqrt(2))
    vecteur l(lidnt(args));
    vecteur lnew=*evalf(l,1,contextptr)._VECTptr;
    gen tmp=subst(args,l,lnew,false,contextptr);
    */
    vecteur l(lvar(args));
    vecteur lnew(l);
    int ls=l.size();
    for (int i=0;i<ls;i++){
      if (l[i].type==_IDNT || lidnt(l[i]).empty()){
	lnew[i]=evalf(l[i],1,contextptr);
#ifdef HAVE_LIBMPFR
	if (lnew[i].type==_DOUBLE_)
	  lnew[i]=accurate_evalf(lnew[i],100);
#endif
      }
    }
    gen tmp=subst(args,l,lnew,false,contextptr);
    if (tmp.type==_REAL){
#ifdef HAVE_LIBMPFR
      // reeval with the right precision
      gen lntmp=ln(abs(tmp,contextptr),contextptr);
      if (is_greater(lntmp,40,contextptr)){
	int prec=real2int(lntmp,contextptr).val+30;
	int oldprec=decimal_digits(contextptr);
	decimal_digits(prec,contextptr);
	for (int i=0;i<ls;i++){
	  if (l[i].type==_IDNT || lidnt(l[i]).empty())
	    lnew[i]=evalf(l[i],1,contextptr);	
	}
	decimal_digits(oldprec,contextptr);
	tmp=subst(args,l,lnew,false,contextptr);
      }
#endif
      gen res=real2int(tmp,contextptr);
      if (is_strictly_positive(-tmp,contextptr) && !is_zero(res-tmp,contextptr))
	return res-1;
      return res;
    }
    if (tmp.type==_FLOAT_)
      return ffloor(tmp._FLOAT_val);
    if (tmp.type!=_DOUBLE_)
      return symb_floor(args);
    return double2gen(giac_floor(tmp._DOUBLE_val));
  }
  static gen taylor_floor (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0; // no symbolic preprocessing
    shift_coeff=0;
    gen l=_floor(lim_point,contextptr);
    if (l==lim_point){
      if (direction==0)
	return gensizeerr(gettext("Taylor of floor with unsigned limit"));
      if (direction==-1)
	l=l-1;
    }
    return is_zero(l,contextptr)?vecteur(0):makevecteur(l);
  }
  static const char _floor_s []="floor";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __floor,&giac::_floor,(unsigned long)&D_at_signunary_function_ptr,&taylor_floor,_floor_s);
#else
  static define_unary_function_eval_taylor( __floor,&giac::_floor,D_at_sign,&taylor_floor,_floor_s);
#endif
  define_unary_function_ptr5( at_floor ,alias_at_floor,&__floor,0,true);

  gen _ceil(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_inf(args)||is_undef(args))
      return args;
    if (args.type==_VECT)
      return apply(args,contextptr,_ceil);
    if (args.type==_CPLX)
      return _ceil(*args._CPLXptr,contextptr)+cst_i*_ceil(*(args._CPLXptr+1),contextptr);
    if ( (args.type==_INT_) || (args.type==_ZINT))
      return args;
#ifdef BCD
    if (args.type==_FLOAT_)
      return fceil(args._FLOAT_val);
#endif
    return -_floor(-args,contextptr);
#if 0
    if (args.type==_FRAC){
      gen n=args._FRACptr->num,d=args._FRACptr->den;
      if ( ((n.type==_INT_) || (n.type==_ZINT)) && ( (d.type==_INT_) || (d.type==_ZINT)) )
	return Iquo(n,d)+1;
    }
    vecteur l(lidnt(args));
    vecteur lnew=*evalf(l,1,contextptr)._VECTptr;
    gen tmp=subst(args,l,lnew,false,contextptr);
    if (tmp.type==_REAL || tmp.type==_FLOAT_)
      return -_floor(-tmp,contextptr);
    if (tmp.type!=_DOUBLE_)
      return symb_ceil(args);
    return double2gen(giac_ceil(tmp._DOUBLE_val));
#endif
  }
  static gen taylor_ceil (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0; // no symbolic preprocessing
    shift_coeff=0;
    gen l=_ceil(lim_point,contextptr);
    if (l==lim_point){
      if (direction==0)
	return gensizeerr(gettext("Taylor of ceil with unsigned limit"));
      if (direction==1)
	l=l-1;
    }
    return is_zero(l,contextptr)?vecteur(0):makevecteur(l);
  }
  static const char _ceil_s []="ceil";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __ceil,&giac::_ceil,(unsigned long)&D_at_signunary_function_ptr,&taylor_ceil,_ceil_s);
#else
  static define_unary_function_eval_taylor( __ceil,&giac::_ceil,D_at_sign,&taylor_ceil,_ceil_s);
#endif
  define_unary_function_ptr5( at_ceil ,alias_at_ceil,&__ceil,0,true);

  static gen ceiltofloor(const gen & g,GIAC_CONTEXT){
    return -symbolic(at_floor,-g);
  }
  gen ceil2floor(const gen & g,GIAC_CONTEXT){
    const vector< const unary_function_ptr *> ceil_v(1,at_ceil);
    const vector< gen_op_context > ceil2floor_v(1,ceiltofloor);
    return subst(g,ceil_v,ceil2floor_v,false,contextptr);
  }

  // static symbolic symb_round(const gen & a){    return symbolic(at_round,a);  }
  gen _round(const gen & args,GIAC_CONTEXT){
    if ( is_undef(args))
      return args;
    if (args.type==_STRNG && args.subtype==-1) return  args;
    if (is_equal(args))
      return apply_to_equal(args,_round,contextptr);
    if (args.is_symb_of_sommet(at_unit))
      return apply_unit(args,_round,contextptr);
    if (is_inf(args)||is_undef(args))
      return args;
    if (args.type==_VECT && args._VECTptr->size()!=2)
      return apply(args,contextptr,_round);
    if (args.type==_VECT && args.subtype==_SEQ__VECT){
      gen b=args._VECTptr->back();
      if (is_integral(b)){
#ifdef BCD
	if (args._VECTptr->front().type==_FLOAT_)
	  return fround(args._VECTptr->front()._FLOAT_val,b.val); 
#endif
	/*
#ifdef _SOFTMATH_H
	double d=std::giac_gnuwince_pow(10.0,double(b.val));
#else
	double d=std::pow(10.0,double(b.val));
#endif
	*/
	gen d=10.0;
	if (b.val>14)
	  d=accurate_evalf(gen(10),int(b.val*3.32192809489+.5));
	d=pow(d,b.val,contextptr);
	gen e=_round(d*args._VECTptr->front(),contextptr);
	if (b.val>14)
	  e=accurate_evalf(e,int(b.val*3.32192809489+.5));
	e=rdiv(e,d,contextptr);
	return e;
      }
    }
    if (args.type==_CPLX)
      return _round(*args._CPLXptr,contextptr)+cst_i*_round(*(args._CPLXptr+1),contextptr);
    gen tmp=args+plus_one_half;
    if (tmp.type==_VECT)
      tmp.subtype=args.subtype;
    return _floor(tmp,contextptr);
  }
  static gen taylor_round (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0; // no symbolic preprocessing
    shift_coeff=0;
    gen l=_round(lim_point,contextptr);
    if (is_zero(ratnormal(l-lim_point-plus_one_half),contextptr)){
      if (direction==0)
	return gensizeerr(gettext("Taylor of round with unsigned limit"));
      if (direction==-1)
	l=l-1;
    }
    return is_zero(l,contextptr)?vecteur(0):makevecteur(l);
  }
  static const char _round_s []="round";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __round,&giac::_round,(unsigned long)&D_at_signunary_function_ptr,&taylor_round,_round_s);
#else
  static define_unary_function_eval_taylor( __round,&giac::_round,D_at_sign,&taylor_round,_round_s);
#endif
  define_unary_function_ptr5( at_round ,alias_at_round,&__round,0,true);

  static string printasprint(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr)!=3)
      return "print("+feuille.print(contextptr)+")";
    else
      return "Disp "+feuille.print(contextptr);
  }
  // static symbolic symb_print(const gen & a){    return symbolic(at_print,a);  }
  gen _print(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( debug_infolevel && (args.type==_IDNT) && args._IDNTptr->localvalue && (!args._IDNTptr->localvalue->empty()))
      *logptr(contextptr) << gettext("Local var protected ") << (*args._IDNTptr->localvalue)[args._IDNTptr->localvalue->size()-2].val << endl;
    gen tmp=args.eval(eval_level(contextptr),contextptr);
    // If giac used inside a console don't add to messages, since we print
#ifdef HAVE_SIGNAL_H_OLD
    if (!child_id){
      if (args.type==_IDNT)
        messages_to_print += args.print(contextptr) + ":"; 
      messages_to_print += tmp.print(contextptr) +'\n';
      // *logptr(contextptr) << "Child " << messages_to_print << endl;
    }
#endif
    if (tmp.type==_VECT && !tmp._VECTptr->empty() && tmp._VECTptr->front()==gen("Unquoted",contextptr)){
      vecteur & v=*tmp._VECTptr;
      int s=v.size();
      for (int i=1;i<s;++i)
	*logptr(contextptr) << (v[i].type==_STRNG?v[i]._STRNGptr->c_str():unquote(v[i].print(contextptr)));
    }
    else {
      if (args.type==_IDNT)
	*logptr(contextptr) << args << ":";
      if (tmp.type==_STRNG)
	*logptr(contextptr) << tmp._STRNGptr->c_str() << endl;
      else
	*logptr(contextptr) << tmp << endl;
    }
    return __interactive.op(symbolic(at_print,tmp),contextptr);
  }
  static const char _print_s []="print";
#ifdef RTOS_THREADX
  static define_unary_function_eval2(__print,&_print,_print_s,&printasprint);
#else
  const unary_function_eval __print(1,&giac::_print,_print_s,&printasprint);
#endif
  define_unary_function_ptr5( at_print ,alias_at_print,&__print,_QUOTE_ARGUMENTS,true);

  // static symbolic symb_is_prime(const gen & a){    return symbolic(at_is_prime,a);  }
  gen _is_prime(const gen & args0,GIAC_CONTEXT){
    gen args(args0);
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    int certif=0;
    if (args0.type==_VECT && args0._VECTptr->size()==2 && args0._VECTptr->back().type==_INT_){
      args=args0._VECTptr->front();
      certif=args0._VECTptr->back().val;
    }
    if (!is_integral(args))
      return gentypeerr(contextptr);
#ifdef HAVE_LIBPARI
    return pari_isprime(args,certif);
#else
    return is_probab_prime_p(args);
#endif
  }
  static const char _is_prime_s []="is_prime";
  static define_unary_function_eval (__is_prime,&giac::_is_prime,_is_prime_s);
  define_unary_function_ptr5( at_is_prime ,alias_at_is_prime,&__is_prime,0,true);

  gen _is_pseudoprime(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return is_probab_prime_p(args);
  }
  static const char _is_pseudoprime_s []="is_pseudoprime";
  static define_unary_function_eval (__is_pseudoprime,&giac::_is_pseudoprime,_is_pseudoprime_s);
  define_unary_function_ptr5( at_is_pseudoprime ,alias_at_is_pseudoprime,&__is_pseudoprime,0,true);

  gen nextprime1(const gen & a,GIAC_CONTEXT){
    if (is_strictly_greater(2,a,contextptr))
      return 2;
    return nextprime(a+1);
  }
  static const char _nextprime_s []="nextprime";
  static define_unary_function_eval (__nextprime,&giac::nextprime1,_nextprime_s);
  define_unary_function_ptr5( at_nextprime ,alias_at_nextprime,&__nextprime,0,true);

  gen prevprime1(const gen & a,GIAC_CONTEXT){
    if (is_greater(2,a,contextptr))
      return gensizeerr(contextptr);
    return prevprime(a-1);
  }
  static const char _prevprime_s []="prevprime";
  static define_unary_function_eval (__prevprime,&giac::prevprime1,_prevprime_s);
  define_unary_function_ptr5( at_prevprime ,alias_at_prevprime,&__prevprime,0,true);

  // static symbolic symb_jacobi_symbol(const gen & a,const gen & b){    return symbolic(at_jacobi_symbol,makevecteur(a,b));  }
  gen _jacobi_symbol(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!check_2d_vecteur(args)) return gensizeerr(contextptr);
    int res=jacobi(args._VECTptr->front(),args._VECTptr->back());
    if (res==-RAND_MAX)
      return gensizeerr(contextptr);
    return res;
  }
  static const char _jacobi_symbol_s []="jacobi_symbol";
  static define_unary_function_eval (__jacobi_symbol,&giac::_jacobi_symbol,_jacobi_symbol_s);
  define_unary_function_ptr5( at_jacobi_symbol ,alias_at_jacobi_symbol,&__jacobi_symbol,0,true);

  // static symbolic symb_legendre_symbol(const gen & a,const gen & b){    return symbolic(at_legendre_symbol,makevecteur(a,b));  }
  gen _legendre_symbol(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!check_2d_vecteur(args)) return gensizeerr(contextptr);
    return legendre(args._VECTptr->front(),args._VECTptr->back());    
  }
  static const char _legendre_symbol_s []="legendre_symbol";
  static define_unary_function_eval (__legendre_symbol,&giac::_legendre_symbol,_legendre_symbol_s);
  define_unary_function_ptr5( at_legendre_symbol ,alias_at_legendre_symbol,&__legendre_symbol,0,true);

  // static symbolic symb_ichinrem(const gen & a,const gen & b){     return symbolic(at_ichinrem,makevecteur(a,b));  }

  gen ichinrem2(const gen  & a_orig,const gen & b_orig){
    gen a=a_orig;
    gen b=b_orig;
    if (a.type==_MOD)
      a=makevecteur(*a._MODptr,*(a._MODptr+1));
    if (b.type==_MOD)
      b=makevecteur(*b._MODptr,*(b._MODptr+1));
    vecteur l(lvar(a)); lvar(b,l);
    if (l.empty()){
      if (!check_2d_vecteur(a)
	  || !check_2d_vecteur(b)) 
	return gensizeerr(gettext("check_2d_vecteur"));
      vecteur & av=*a._VECTptr;
      vecteur & bv=*b._VECTptr;
      gen ab=av.back();
      gen bb=bv.back();
      gen aa=av.front();
      gen ba=bv.front();
      if (!is_integral(ab) || !is_integral(bb) || !is_integral(aa) || !is_integral(ba))
	return gentypeerr(gettext("Non integral argument"));
      if (is_greater(1,bb,context0) || is_greater(1,ab,context0))
	return gentypeerr(gettext("Bad mod value"));
      gen res=ichinrem(aa,ba,ab,bb);
      if (a_orig.type==_MOD)
	return makemod(res,lcm(ab,bb));
      return makevecteur(res,lcm(ab,bb));
    }
    gen x=l.front();
    if (a.type!=_VECT || b.type!=_VECT ){
      // a and b are polynomial, must have the same degrees
      // build a new polynomial calling ichinrem2 on each element
      gen ax=_e2r(makevecteur(a_orig,x),context0),bx=_e2r(makevecteur(b_orig,x),context0); // ok
      if (ax.type!=_VECT || bx.type!=_VECT )
	return gensizeerr(gettext("ichinrem2 1"));
      int as=ax._VECTptr->size(),bs=bx._VECTptr->size();
      if (!as || !bs)
	return gensizeerr(gettext("Null polynomial"));
      gen a0=ax._VECTptr->front(),b0=bx._VECTptr->front(),m,n;
      if (a0.type==_MOD)
	m=*(a0._MODptr+1);
      else
	return gensizeerr(gettext("Expecting modular coeff"));
      if (b0.type==_MOD)
	n=*(b0._MODptr+1);
      else
	return gensizeerr(gettext("Expecting modular coeff"));
      gen mn=lcm(m,n);
      const_iterateur it=ax._VECTptr->begin(),itend=ax._VECTptr->end(),jt=bx._VECTptr->begin();
      vecteur res;
      for (;as>bs;--as,++it){
	res.push_back(makemod(unmod(*it),mn));
      }
      for (;bs>as;--bs,++jt){
	res.push_back(makemod(unmod(*jt),mn));
      }
      for (;it!=itend;++it,++jt)
	res.push_back(ichinrem2(makemod(unmod(*it),m),makemod(unmod(*jt),n)));
      return _r2e(gen(makevecteur(res,x),_SEQ__VECT),context0); // ok
    }
    if (a.type==_VECT && a._VECTptr->size()==2 && b.type==_VECT && b._VECTptr->size()==2 ){
      // ax and bx are the polynomials, 
      gen ax=_e2r(makevecteur(a._VECTptr->front(),x),context0),bx=_e2r(makevecteur(b._VECTptr->front(),x),context0); // ok
      if (ax.type!=_VECT || bx.type!=_VECT )
	return gensizeerr(gettext("ichinrem2 2"));
      gen m=a._VECTptr->back(),n=b._VECTptr->back(),mn=lcm(m,n);
      int as=ax._VECTptr->size(),bs=bx._VECTptr->size();
      const_iterateur it=ax._VECTptr->begin(),itend=ax._VECTptr->end(),jt=bx._VECTptr->begin();
      vecteur res;
      for (;as>bs;--as,++it){
	res.push_back(*it);
      }
      for (;bs>as;--bs,++jt){
	res.push_back(*jt);
      }
      for (;it!=itend;++it,++jt){
	gen tmp=ichinrem2(makevecteur(*it,m),makevecteur(*jt,n));
	if (tmp.type!=_VECT)
	  return gensizeerr(gettext("ichinrem2 3"));
	res.push_back(tmp._VECTptr->front());
      }
      if (a_orig.type==_MOD)
	return makemod(_r2e(gen(makevecteur(res,x),_SEQ__VECT),context0),mn); // ok
      return makevecteur(_r2e(gen(makevecteur(res,x),_SEQ__VECT),context0),m*n); // ok
    }
    return gensizeerr(gettext("ichinrem2 4"));
  }
  gen _ichinrem(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gentypeerr(gettext("[a % p, b % q,...]"));
    vecteur & v = *args._VECTptr;
    int s=v.size();
    if (s<2)
      return gendimerr(contextptr);
    gen res=ichinrem2(v[0],v[1]);
    for (int i=2;i<s;++i)
      res=ichinrem2(res,v[i]);
    return res;
  }
  static const char _ichinrem_s []="ichinrem";
  static define_unary_function_eval (__ichinrem,&giac::_ichinrem,_ichinrem_s);
  define_unary_function_ptr5( at_ichinrem ,alias_at_ichinrem,&__ichinrem,0,true);
  
  gen _fracmod(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2))
      return symbolic(at_fracmod,args);
    vecteur & v=*args._VECTptr;
    return fracmod(v[0],v[1]);
  }
  static const char _fracmod_s []="fracmod";
  static define_unary_function_eval (__fracmod,&giac::_fracmod,_fracmod_s);
  define_unary_function_ptr5( at_fracmod ,alias_at_fracmod,&__fracmod,0,true);
  
  static const char _iratrecon_s []="iratrecon"; // maple name, fracmod takes only 2 arg
  static define_unary_function_eval (__iratrecon,&giac::_fracmod,_iratrecon_s);
  define_unary_function_ptr5( at_iratrecon ,alias_at_iratrecon,&__iratrecon,0,true);
  
  // static symbolic symb_chinrem(const gen & a,const gen & b){    return symbolic(at_chinrem,makevecteur(a,b));  }
  static vecteur polyvect(const gen & a,const vecteur & v){
    if (a.type==_POLY)
      return polynome2poly1(*a._POLYptr,1);
    return vecteur(1,a);
  }
  gen _chinrem(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return gensizeerr(contextptr);
    gen a=args._VECTptr->front();
    gen b=(*args._VECTptr)[1];
    if (!check_2d_vecteur(a) ||
	!check_2d_vecteur(b) )
      return gensizeerr(contextptr);
    if ((a._VECTptr->front().type!=_VECT) || (a._VECTptr->back().type!=_VECT) || (b._VECTptr->front().type!=_VECT) || (b._VECTptr->back().type!=_VECT) ){
      vecteur lv;
      if (args._VECTptr->size()==3)
	lv=vecteur(1,(*args._VECTptr)[2]);
      else
	lv=vecteur(1,vx_var);
      lvar(args,lv);
      vecteur lvprime(lv.begin()+1,lv.end());
      gen aa=e2r(a,lv,contextptr),bb=e2r(b,lv,contextptr),aan,aad,bbn,bbd;
      fxnd(aa,aan,aad);
      if (aad.type==_POLY){
	if (aad._POLYptr->lexsorted_degree() )
	  return gensizeerr(contextptr);
	else
	  aad=aad._POLYptr->trunc1();
      }
      fxnd(bb,bbn,bbd);
      if (bbd.type==_POLY){
	if (bbd._POLYptr->lexsorted_degree() )
	  return gensizeerr(contextptr);
	else
	  bbd=bbd._POLYptr->trunc1();
      }
      vecteur & aanv=*aan._VECTptr;
      vecteur & bbnv=*bbn._VECTptr;
      aanv[0]=polyvect(aanv[0],lv)/aad;
      aanv[1]=polyvect(aanv[1],lv);
      bbnv[0]=polyvect(bbnv[0],lv)/bbd;
      bbnv[1]=polyvect(bbnv[1],lv);
      gen tmpg=_chinrem(makevecteur(aanv,bbnv),contextptr);
      if (is_undef(tmpg)) return tmpg;
      vecteur res=*tmpg._VECTptr;
      // convert back
      res[0]=symb_horner(*r2e(res[0],lvprime,contextptr)._VECTptr,lv.front());
      res[1]=symb_horner(*r2e(res[1],lvprime,contextptr)._VECTptr,lv.front());
      return res;
    }
    modpoly produit=(*a._VECTptr->back()._VECTptr)**b._VECTptr->back()._VECTptr;
    return makevecteur(gen(chinrem(*a._VECTptr->front()._VECTptr,*b._VECTptr->front()._VECTptr,*a._VECTptr->back()._VECTptr,*b._VECTptr->back()._VECTptr,0),_POLY1__VECT),gen(produit,_POLY1__VECT));    
  }
  static const char _chinrem_s []="chinrem";
  static define_unary_function_eval (__chinrem,&giac::_chinrem,_chinrem_s);
  define_unary_function_ptr5( at_chinrem ,alias_at_chinrem,&__chinrem,0,true);

  static string printasfactorial(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_SYMB)
      return feuille.print(contextptr)+"!";
    return "("+feuille.print(contextptr)+")!";
  }
  gen _factorial(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT)
      return apply(args,_factorial,contextptr);
    if (args.type!=_INT_)
      return Gamma(args+1,contextptr);
    if (args.val<0)
      return unsigned_inf;
    return factorial((unsigned long int) args.val);
  }
  static const char _factorial_s []="factorial";
  static define_unary_function_eval2 (__factorial,&giac::_factorial,_factorial_s,printasfactorial);
  define_unary_function_ptr5( at_factorial ,alias_at_factorial,&__factorial,0,true);

  gen double_is_int(const gen & g,GIAC_CONTEXT){
    gen f=_floor(g,contextptr);
    if (f.type==_FLOAT_)
      f=get_int(f._FLOAT_val);
    gen f1=evalf(g-f,1,contextptr);
    if ( (f1.type==_DOUBLE_ && fabs(f1._DOUBLE_val)<epsilon(contextptr))
	 || (f1.type==_FLOAT_ && fabs(f1._FLOAT_val)<epsilon(contextptr)) )
      return f;
    else
      return g;
  }
  gen comb(const gen & a_orig,const gen &b_orig,GIAC_CONTEXT){
    gen a=double_is_int(a_orig,contextptr);
    gen b=double_is_int(b_orig,contextptr);
    if (a.type!=_INT_ || b.type!=_INT_)
      return Gamma(a+1,contextptr)/Gamma(b+1,contextptr)/Gamma(a-b+1,contextptr);
    return comb((unsigned long int) a.val,(unsigned long int) b.val);
  }
  gen _comb(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (ckmatrix(args))
      return apply(args._VECTptr->front(),args._VECTptr->back(),contextptr,comb);
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2))
      return gentypeerr(contextptr);
    vecteur & v=*args._VECTptr;
    if (v.front().type!=_INT_ || v.back().type!=_INT_)
      return comb(v.front(),v.back(),contextptr); 
    if (v.front().val<v.back().val)
      return zero;
    if (v.front().val<0)
      return undef;
    return comb((unsigned long int) v.front().val,(unsigned long int) v.back().val);
  }
  static const char _comb_s []="comb";
  static define_unary_function_eval (__comb,&giac::_comb,_comb_s);
  define_unary_function_ptr5( at_comb ,alias_at_comb,&__comb,0,true);

  gen perm(const gen & a,const gen &b){
    if (a.type!=_INT_ || b.type!=_INT_)
      return symbolic(at_perm,gen(makevecteur(a,b),_SEQ__VECT));
    return perm((unsigned long int) a.val,(unsigned long int) b.val);
  }
  gen _perm(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (ckmatrix(args))
      return apply(args._VECTptr->front(),args._VECTptr->back(),perm);
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2))
      return gentypeerr(contextptr);
    if ( (args._VECTptr->front().type!=_INT_) || (args._VECTptr->back().type!=_INT_) )
      return _factorial(args._VECTptr->front(),contextptr)/_factorial(args._VECTptr->front()-args._VECTptr->back(),contextptr);
    if (args._VECTptr->front().val<args._VECTptr->back().val)
      return zero;
    if (args._VECTptr->front().val<0)
      return undef;
    return perm((unsigned long int) args._VECTptr->front().val,(unsigned long int) args._VECTptr->back().val);
  }
  static const char _perm_s []="perm";
  static define_unary_function_eval (__perm,&giac::_perm,_perm_s);
  define_unary_function_ptr5( at_perm ,alias_at_perm,&__perm,0,true);

  // ******************
  // Matrix functions
  // *****************

  symbolic symb_tran(const gen & a){
    return symbolic(at_tran,a);
  }
  symbolic symb_trace(const gen & a){
    return symbolic(at_trace,a);
  }
  symbolic symb_rref(const gen & a){
    return symbolic(at_rref,a);
  }
  symbolic symb_idn(const gen & e){
    return symbolic(at_idn,e);
  }
  symbolic symb_ranm(const gen & e){
    return symbolic(at_ranm,e);
  }
  symbolic symb_det(const gen & a){
    return symbolic(at_det,a);
  }
  symbolic symb_pcar(const gen & a){
    return symbolic(at_pcar,a);
  }
  symbolic symb_ker(const gen & a){
    return symbolic(at_ker,a);
  }  
  symbolic symb_image(const gen & a){
    return symbolic(at_image,a);
  }
  symbolic symb_moyal(const gen & a,const gen & b, const gen &vars,const gen & order){
    return symbolic(at_moyal,gen(makevecteur(a,b,vars,order),_SEQ__VECT));
  }

  gen _evalf(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.is_symb_of_sommet(at_equal)&&a._SYMBptr->feuille.type==_VECT && a._SYMBptr->feuille._VECTptr->size()==2){
      vecteur & v(*a._SYMBptr->feuille._VECTptr);
      return symbolic(at_equal,gen(makevecteur(evalf(v.front(),1,contextptr),evalf(v.back(),1,contextptr)),_SEQ__VECT));
    }
    if (a.type==_VECT && a.subtype==_SEQ__VECT && a._VECTptr->size()==2 && a._VECTptr->back().type==_INT_){
      int save_decimal_digits=decimal_digits(contextptr);
      int ndigits=a._VECTptr->back().val;
#ifndef HAVE_LIBMPFR
      if (ndigits>14)
	return gensizeerr(gettext("Longfloat library not available"));
#endif
      set_decimal_digits(ndigits,contextptr);
      gen res=a._VECTptr->front().evalf(1,contextptr);
      if (res.type==_REAL || res.type==_CPLX)
	res=accurate_evalf(res,digits2bits(a._VECTptr->back().val));
#if 0
      if (ndigits<=14 && calc_mode(contextptr)==1 && (res.type==_DOUBLE_ || res.type==_CPLX)){
	int decal=0;
	decal=int(std::floor(std::log10(abs(res,contextptr)._DOUBLE_val)));
	res=res*pow(10,ndigits-decal-1,contextptr);
	res=_floor(re(res,contextptr)+.5,contextptr)+cst_i*_floor(im(res,contextptr)+.5,contextptr);
	res=evalf(res,1,contextptr)*pow(10,decal+1-ndigits,contextptr);
      }
      else {
	if (ndigits<=14 && !is_undef(res)){
	  res=_round(gen(makevecteur(res,ndigits),_SEQ__VECT),contextptr);
	}
      }
#else
      if (ndigits<=14 && !is_undef(res))
	res=gen(res.print(contextptr),contextptr);
#endif
      set_decimal_digits(save_decimal_digits,contextptr);
      return res;
    }
    return a.evalf(1,contextptr);
  }
  static const char _evalf_s []="evalf";
  static define_unary_function_eval (__evalf,&giac::_evalf,_evalf_s);
  define_unary_function_ptr5( at_evalf ,alias_at_evalf,&__evalf,0,true);
  symbolic symb_evalf(const gen & a){  
    return symbolic(at_evalf,a);  
  }

  gen _eval(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.is_symb_of_sommet(at_equal)&&a._SYMBptr->feuille.type==_VECT && a._SYMBptr->feuille._VECTptr->size()==2){
      vecteur & v(*a._SYMBptr->feuille._VECTptr);
      return symbolic(at_equal,gen(makevecteur(eval(v.front(),eval_level(contextptr),contextptr),eval(v.back(),eval_level(contextptr),contextptr)),_SEQ__VECT));
    }
    if (a.type==_VECT && a.subtype==_SEQ__VECT && a._VECTptr->size()==2){
      gen a1=a._VECTptr->front(),a2=a._VECTptr->back();
      if (a2.type==_INT_)
	return a1.eval(a2.val,contextptr);
      return _subst(gen(makevecteur(eval(a1,eval_level(contextptr),contextptr),a2),_SEQ__VECT),contextptr);
    }
    return a.eval(1,contextptr).eval(eval_level(contextptr),contextptr);
  }
  static const char _eval_s []="eval";
  static define_unary_function_eval_quoted (__eval,&giac::_eval,_eval_s);
  define_unary_function_ptr5( at_eval ,alias_at_eval,&__eval,_QUOTE_ARGUMENTS,true);
  symbolic symb_eval(const gen & a){    
    return symbolic(at_eval,a);  
  }
  
  static const char _evalm_s []="evalm";
  static define_unary_function_eval (__evalm,&giac::_eval,_evalm_s);
  define_unary_function_ptr5( at_evalm ,alias_at_evalm,&__evalm,0,true);
  
  gen _ampersand_times(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    return g._VECTptr->front()*g._VECTptr->back();
  }
  static const char _ampersand_times_s []="&*";
  static define_unary_function_eval4_index (108,__ampersand_times,&giac::_ampersand_times,_ampersand_times_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_ampersand_times ,alias_at_ampersand_times ,&__ampersand_times);
  
  static const char _subst_s []="subst";
  gen _subst(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gentypeerr(contextptr);
    vecteur & v = *args._VECTptr;
    int s=v.size();
    if (s==2){
      gen e=v.back();
      if (e.type==_VECT){
	vecteur w;
	if (ckmatrix(e))
	  aplatir(*e._VECTptr,w);
	else
	  w = *e._VECTptr;
	vecteur vin,vout;
	const_iterateur it=w.begin(),itend=w.end();
	for (;it!=itend;++it){
	  if (it->type!=_SYMB)
	    continue;
	  if (it->_SYMBptr->sommet!=at_equal && it->_SYMBptr->sommet!=at_same)
	    continue;
	  vin.push_back(it->_SYMBptr->feuille._VECTptr->front());
	  vout.push_back(it->_SYMBptr->feuille._VECTptr->back());
	}
	gen res=subst(v.front(),vin,vout,false,contextptr);
	return res;
      }
      if (e.type!=_SYMB)
	return gentypeerr(contextptr);
      if (e._SYMBptr->sommet!=at_equal && e._SYMBptr->sommet!=at_same)
	return gensizeerr(contextptr);
      return subst(v.front(),e._SYMBptr->feuille._VECTptr->front(),e._SYMBptr->feuille._VECTptr->back(),false,contextptr);
    }
    if (s<3)
      return gentoofewargs(_subst_s);
    if (s>3)
      return gentoomanyargs(_subst_s);
    if (v[1].is_symb_of_sommet(at_equal))
      return _subst(makevecteur(v.front(),vecteur(v.begin()+1,v.end())),contextptr);
    return subst(v.front(),v[1],v.back(),false,contextptr);
  }
  static define_unary_function_eval (__subst,&giac::_subst,_subst_s);
  define_unary_function_ptr5( at_subst ,alias_at_subst,&__subst,0,true);
  // static symbolic symb_subst(const gen & a){    return symbolic(at_subst,a);  }

  string printassubs(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr)!=1 || feuille.type!=_VECT || feuille._VECTptr->size()!=2)
      return sommetstr+("("+feuille.print(contextptr)+")");
    vecteur & v=*feuille._VECTptr;
    vecteur w=mergevecteur(vecteur(1,v.back()),vecteur(v.begin(),v.end()-1));
    return sommetstr+("("+gen(w,_SEQ__VECT).print(contextptr)+")");
  }  
  gen _subs(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return _subst(g,contextptr);
  }
  static const char _subs_s []="subs";
  static define_unary_function_eval2 (__subs,&_subs,_subs_s,&printassubs);
  define_unary_function_ptr( at_subs ,alias_at_subs ,&__subs);

  string printasmaple_subs(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (xcas_mode(contextptr)==1 || feuille.type!=_VECT || feuille._VECTptr->size()<2)
      return sommetstr+("("+feuille.print(contextptr)+")");
    vecteur & v=*feuille._VECTptr;
    vecteur w=mergevecteur(vecteur(1,v.back()),vecteur(v.begin(),v.end()-1));
    return sommetstr+("("+gen(w,_SEQ__VECT).print(contextptr)+")");
  }  
  gen _maple_subs(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<2)
      return _subst(g,contextptr);
    vecteur &v=*g._VECTptr;
    if (v.size()==2)
      return _subst(makevecteur(v.back(),v.front()),contextptr);
    else
      return _subst(makevecteur(v.back(),vecteur(v.begin(),v.end()-1)),contextptr);
  }
  static const char _maple_subs_s []="subs";
  static define_unary_function_eval2 (__maple_subs,&_maple_subs,_maple_subs_s,&printasmaple_subs);
  define_unary_function_ptr( at_maple_subs ,alias_at_maple_subs ,&__maple_subs);


  string version(){
    return string("giac ")+VERSION;
  }
  gen _version(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    return string2gen(version(),false);
  }
  static const char _version_s []="version";
  static define_unary_function_eval (__version,&giac::_version,_version_s);
  define_unary_function_ptr5( at_version ,alias_at_version,&__version,0,true);

  void prod2frac(const gen & g,vecteur & num,vecteur & den){
    num.clear();
    den.clear();
    if (g.type==_FRAC){
      vecteur num2,den2;
      prod2frac(g._FRACptr->num,num,den);
      prod2frac(g._FRACptr->den,den2,num2);
      num=mergevecteur(num,num2);
      den=mergevecteur(den,den2);
      return;      
    }
    if (g.is_symb_of_sommet(at_neg)){
      prod2frac(g._SYMBptr->feuille,num,den);
      if (!num.empty()){
	num.front()=-num.front();
	return;
      }
    }
    if ( (g.type!=_SYMB) || (g._SYMBptr->sommet!=at_prod) || (g._SYMBptr->feuille.type!=_VECT)){
      if (g.is_symb_of_sommet(at_division)){
	vecteur num2,den2;
	prod2frac(g._SYMBptr->feuille._VECTptr->front(),num,den);
	prod2frac(g._SYMBptr->feuille._VECTptr->back(),den2,num2);
	num=mergevecteur(num,num2);
	den=mergevecteur(den,den2);
	return;
      }
      if (g.is_symb_of_sommet(at_inv))
	prod2frac(g._SYMBptr->feuille,den,num);
      else
	num=vecteur(1,g);
      return;
    }
    vecteur & v=*g._SYMBptr->feuille._VECTptr;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_inv) )
	den.push_back(it->_SYMBptr->feuille);
      else
	num.push_back(*it);
    }
  }

  gen vecteur2prod(const vecteur & num){
    if (num.empty())
      return plus_one;
    if (num.size()==1)
      return num.front();
    return symbolic(at_prod,gen(num,_SEQ__VECT));
  }

  bool need_parenthesis(const gen & g){
    if (g.type==_INT_ || g.type==_ZINT)
      return is_strictly_positive(-g,context0);  // ok
    if (g.type==_CPLX){
      gen rg=re(-g,context0),ig(im(-g,context0)); // ok
      if ( is_exactly_zero(rg))
	return is_strictly_positive(ig,context0); // ok
      if (is_exactly_zero(ig) )
	return is_strictly_positive(rg,context0); // ok
      return true;
    }
    if (g.type==_SYMB)
      return need_parenthesis(g._SYMBptr->sommet);
    if (g.type!=_FUNC)
      return false;
    unary_function_ptr & u=*g._FUNCptr;
    if (u==at_pow || u==at_division || u==at_prod)
      return false;
    if (u==at_neg || u==at_minus || u==at_and || u==at_et || u==at_ou || u==at_oufr || u==at_xor || u==at_same || u==at_equal || u==at_superieur_egal || u==at_superieur_strict || u==at_inferieur_egal || u==at_inferieur_strict)
      return true;
    if (!u.ptr()->printsommet)
      return false;
    return true;
  }

  gen _multistring(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    string res;
    if (args.type==_VECT){
      const_iterateur it=args._VECTptr->begin(),itend=args._VECTptr->end();
      for (;it!=itend;){
	if (it->type!=_STRNG)
	  break;
	res += *it->_STRNGptr;
	++it;
	if (it==itend)
	  return string2gen(res,false);
	res += '\n';
      }
    }
    else {// newline added, otherwise Eqw_compute_size would fail
      if (args.type==_STRNG)
	res=*args._STRNGptr;
      else
	res=args.print(contextptr);
      res += '\n'; 
    }
    return string2gen(res,false);
  }
  static const char _multistring_s []="multistring";
  static define_unary_function_eval (__multistring,&giac::_multistring,_multistring_s);
  define_unary_function_ptr( at_multistring ,alias_at_multistring ,&__multistring);

#ifndef HAVE_LONG_DOUBLE
  static const double LN_SQRT2PI = 0.9189385332046727418; //log(2*PI)/2
  static const double M_PIL=3.141592653589793238462643383279;
  static const double LC1 = 0.08333333333333333,
    LC2 = -0.002777777777777778,
    LC3 = 7.936507936507937E-4,
    LC4 = -5.952380952380953E-4;
  static const double L9[] = {
    0.99999999999980993227684700473478,
    676.520368121885098567009190444019,
    -1259.13921672240287047156078755283,
    771.3234287776530788486528258894,
    -176.61502916214059906584551354,
    12.507343278686904814458936853,
    -0.13857109526572011689554707,
    9.984369578019570859563e-6,
    1.50563273514931155834e-7
  };
#else
  static const long_double LN_SQRT2PI = 0.9189385332046727418L; //log(2*PI)/2
  static const long_double M_PIL=3.141592653589793238462643383279L;
  static const long_double LC1 = 0.08333333333333333L,
    LC2 = -0.002777777777777778L,
    LC3 = 7.936507936507937E-4L,
    LC4 = -5.952380952380953E-4L;
  static const long_double L9[] = {
    0.99999999999980993227684700473478L,
    676.520368121885098567009190444019L,
    -1259.13921672240287047156078755283L,
    771.3234287776530788486528258894L,
    -176.61502916214059906584551354L,
    12.507343278686904814458936853L,
    -0.13857109526572011689554707L,
    9.984369578019570859563e-6L,
    1.50563273514931155834e-7L
  };
#endif

  // Stirling/Lanczos approximation for ln(Gamma())
  double lngamma(double X){
    long_double res,x(X);
    if (x<0.5)
#ifndef HAVE_LONG_DOUBLE
      res=std::log(M_PIL) -std::log(std::sin(M_PIL*x)) - lngamma(1.-x);
#else
      res=std::log(M_PIL) -std::log(std::sin(M_PIL*x)) - lngamma(1.L-x);
#endif
    else {
      --x;
      if (x<20){
	long_double a = L9[0];
	for (int i = 1; i < 9; ++i) {
	  a+= L9[i]/(x+(long_double)(i));
	}
#ifndef HAVE_LONG_DOUBLE
	res= (LN_SQRT2PI + std::log(a) - 7.) + (x+.5)*(std::log(x+7.5)-1.);
#else
	res= (LN_SQRT2PI + std::log(a) - 7.L) + (x+.5L)*(std::log(x+7.5L)-1.L);
#endif
      }
      else {
	long_double
#ifndef HAVE_LONG_DOUBLE
	  r1 = 1./x,
#else
	  r1 = 1.L/x,
#endif
	  r2 = r1*r1,
	  r3 = r1*r2,
	  r5 = r2*r3,
	  r7 = r3*r3*r1;
#ifndef HAVE_LONG_DOUBLE
	res=(x+.5)*std::log(x) - x + LN_SQRT2PI + LC1*r1 + LC2*r3 + LC3*r5 + LC4*r7;
#else
	res=(x+.5L)*std::log(x) - x + LN_SQRT2PI + LC1*r1 + LC2*r3 + LC3*r5 + LC4*r7;
#endif
      }
    }
    return res;
  }
  
  static complex_long_double lngamma(complex_long_double x){
    complex_long_double res;
    if (x.real()<0.5)
#ifdef BESTA_OS
		assert(0);
		// besta compiler does not like this next line, it is unable to resolve the "-" operator
	    // code looks okay to me, but putting this here to move forward, needs to be fixed sometime....
#else
#ifndef HAVE_LONG_DOUBLE
      res=std::log(M_PIL) -std::log(std::sin(M_PIL*x)) - lngamma(1.-x);
#else
      res=std::log(M_PIL) -std::log(std::sin(M_PIL*x)) - lngamma(1.L-x);
#endif
#endif
	else {
#ifndef HAVE_LONG_DOUBLE
      x=x-1.;
#else
      x=x-1.L;
#endif
      complex_long_double a = L9[0];
      for (int i = 1; i < 9; ++i) {
	a+= L9[i]/(x+(long_double)(i));
      }
#ifndef HAVE_LONG_DOUBLE
      res= (LN_SQRT2PI + std::log(a) - 7.) + (x+.5)*(std::log(x+7.5)-1.);
#else
      res= (LN_SQRT2PI + std::log(a) - 7.L) + (x+.5L)*(std::log(x+7.5L)-1.L);
#endif
    }
    return res;
  }
  
  gen lngamma(const gen & x,GIAC_CONTEXT){
    gen g(x);
    if (g.type==_FLOAT_)
      g=evalf_double(g,1,contextptr);
    if (g.type==_DOUBLE_)
      return lngamma(g._DOUBLE_val);
    if (g.type==_CPLX && (g._CPLXptr->type==_DOUBLE_ || (g._CPLXptr+1)->type==_DOUBLE_ ||
			  g._CPLXptr->type==_FLOAT_ || (g._CPLXptr+1)->type==_FLOAT_)){
      g=evalf_double(g,1,contextptr);
      complex_long_double z(re(g,contextptr)._DOUBLE_val,im(g,contextptr)._DOUBLE_val);
      z=lngamma(z);
      return gen(double(z.real()),double(z.imag()));
    }
    return ln(Gamma(x,contextptr),contextptr);
  }

  // Gamma function
  // lnGamma_minus is ln(Gamma)-(z-1/2)*ln(z)+z which is tractable at +inf
  static gen taylor_lnGamma_minus(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0;
    if (lim_point!=plus_inf)
      return gensizeerr(contextptr);
    shift_coeff=0;
    vecteur v;
    // ln(Gamma(z)) = (z-1/2)*ln(z) - z +
    //                ln(2*pi)/2 + sum(B_2n /((2n)*(2n-1)*z^(2n-1)),n>=1)
    v.push_back(symbolic(at_ln,cst_two_pi)/2);
    for (int n=1;2*n<=ordre;++n){
      v.push_back(bernoulli(2*n)/(4*n*n-2*n));
      v.push_back(0);
    }
    v.push_back(undef);
    return v;
  }
  // lnGamma_minus is ln(Gamma)-(z-1/2)*ln(z)+z which is tractable at +inf
  static gen d_lnGamma_minus(const gen & args,GIAC_CONTEXT){
    return Psi(args,0)+1-symbolic(at_ln,args)-(args+minus_one_half)/args;
  }
  define_partial_derivative_onearg_genop( D_at_lnGamma_minus," D_at_lnGamma_minus",&d_lnGamma_minus);
  static gen _lnGamma_minus(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (is_inf(g))
      return symbolic(at_ln,cst_two_pi)/2;
    return symbolic(at_lnGamma_minus,g);
  }
  static const char _lnGamma_minus_s []="lnGamma_minus";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __lnGamma_minus,&_lnGamma_minus,(unsigned long)&D_at_lnGamma_minusunary_function_ptr,&taylor_lnGamma_minus,_lnGamma_minus_s);
#else
  static define_unary_function_eval_taylor( __lnGamma_minus,&_lnGamma_minus,D_at_lnGamma_minus,&taylor_lnGamma_minus,_lnGamma_minus_s);
#endif
  define_unary_function_ptr5( at_lnGamma_minus ,alias_at_lnGamma_minus,&__lnGamma_minus,0,true);
  // ln(Gamma) = lnGamma_minus + (z-1/2)*ln(z)-z which is tractable at +inf
  static gen Gamma_replace(const gen & g,GIAC_CONTEXT){
    return symbolic(at_exp,(g+minus_one_half)*symbolic(at_ln,g)-g)*symbolic(at_exp,_lnGamma_minus(g,contextptr));
  }
  static gen taylor_Gamma (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0){
      return 0; // statically handled now
      limit_tractable_functions().push_back(at_Gamma);
      limit_tractable_replace().push_back(Gamma_replace);
      return 1;
    }
    shift_coeff=0;
    if (!is_integer(lim_point) || is_strictly_positive(lim_point,contextptr))
      return taylor(lim_point,ordre,f,0,shift_coeff,contextptr);
    // Laurent series for Gamma
    if (lim_point.type!=_INT_)
      return gensizeerr(contextptr);
    vecteur v;
    identificateur x(" ");
    int n=-lim_point.val;
    gen decal(1);
    for (int i=1;i<=n;++i){
      decal = decal/(x-i);
    }
    taylor(decal,x,zero,ordre,v,contextptr);
    gen Psi1=taylor(1,ordre,f,0,shift_coeff,contextptr);
    shift_coeff=-1;
    if (Psi1.type!=_VECT)
      return gensizeerr(contextptr);
    v=operator_times(v,*Psi1._VECTptr,0);
    v=vecteur(v.begin(),v.begin()+ordre);
    v.push_back(undef);
    return v;
  }
  static gen d_Gamma(const gen & args,GIAC_CONTEXT){
    return Psi(args,0)*Gamma(args,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_Gamma," D_at_Gamma",&d_Gamma);
  gen Gamma(const gen & x,GIAC_CONTEXT){
    if (x.type==_VECT && x.subtype==_SEQ__VECT && x._VECTptr->size()>=2){
      gen s=x._VECTptr->front(),z=(*x._VECTptr)[1];
      if (s.type==_DOUBLE_)
	z=evalf_double(z,1,contextptr);
      if (z.type==_DOUBLE_)
	s=evalf_double(s,1,contextptr);
      if (s.type==_DOUBLE_ && z.type==_DOUBLE_){
	return upper_incomplete_gammad(s._DOUBLE_val,z._DOUBLE_val,x._VECTptr->size()==3?!is_zero(x._VECTptr->back()):false);
      }
      return symbolic(at_Gamma,x);
    }
    if (x.type==_FLOAT_)
      return fgamma(x._FLOAT_val);
    // return Gamma(get_double(x._FLOAT_val),contextptr);
    if (x.type==_INT_){
      if (x.val<=0)
	return unsigned_inf;
      return factorial(x.val-1);
    }
    if (x.type==_FRAC && x._FRACptr->den==2 && x._FRACptr->num.type==_INT_){
      int n=x._FRACptr->num.val;
      // compute Gamma(n/2)
      gen factnum=1,factden=1;
      for (;n>1;n-=2){
	factnum=(n-2)*factnum;
	factden=2*factden;
      }
      for (;n<1;n+=2){
	factnum=2*factnum;
	factden=n*factden;
      }
      return factnum/factden*sqrt(cst_pi,contextptr);
    }
#if 0 // def HAVE_LIBGSL
    if (x.type==_DOUBLE_)
      return gsl_sf_gamma(x._DOUBLE_val);
#endif
#ifdef HAVE_LIBMPFR
    if (x.type==_REAL && is_positive(x,contextptr)){
      mpfr_t gam;
      int prec=mpfr_get_prec(x._REALptr->inf);
      mpfr_init2(gam,prec);
      mpfr_gamma(gam,x._REALptr->inf,GMP_RNDN);
      real_object res(gam);
      mpfr_clear(gam);
      return res;
    }
#endif
#ifdef HAVE_LIBPARI
    if (x.type==_CPLX)
      return pari_gamma(x);
#endif
    if (x.type==_DOUBLE_ || ( x.type==_CPLX &&  
			      (x._CPLXptr->type==_DOUBLE_ || (x._CPLXptr+1)->type==_DOUBLE_ ||
			       x._CPLXptr->type==_FLOAT_ || (x._CPLXptr+1)->type==_FLOAT_)
			      )
	) {
#if 1
      if (is_strictly_positive(.5-re(x,contextptr),contextptr))
	return cst_pi / (sin(M_PI*x,contextptr)*Gamma(1-x,contextptr));
      return exp(lngamma(x,contextptr),contextptr);
#else
      static const double p[] = {
	0.99999999999980993, 676.5203681218851, -1259.1392167224028,
	771.32342877765313, -176.61502916214059, 12.507343278686905,
	-0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7};
      gen z = x-1;
      gen X = p[0];
      int g=7;
      for (int i=1;i<g+2;++i)
	X += gen(p[i])/(z+i);
      gen t = z + g + 0.5;
      return sqrt(2*cst_pi,contextptr) * pow(t,z+0.5,contextptr) * exp(-t,contextptr) * X;   
#endif   
    }
#ifdef GIAC_HAS_STO_38
    return gammatofactorial(x,contextptr);
#else
    return symbolic(at_Gamma,x);
#endif
  }
  gen _Gamma(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return Gamma(args,contextptr);
  }
  static const char _Gamma_s []="Gamma";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __Gamma,&_Gamma,(unsigned long)&D_at_Gammaunary_function_ptr,&taylor_Gamma,_Gamma_s);
#else
  static define_unary_function_eval_taylor( __Gamma,&_Gamma,D_at_Gamma,&taylor_Gamma,_Gamma_s);
#endif
  define_unary_function_ptr5( at_Gamma ,alias_at_Gamma,&__Gamma,0,true);

  // diGamma function
  static gen taylor_Psi_minus_ln(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0;
    if (lim_point!=plus_inf)
      return gensizeerr(contextptr);
    shift_coeff=1;
    vecteur v(1,minus_one_half);
    // Psi(z)=ln(z)-1/(2*z)-sum(B_2n /(2*n*z^(2n)),n>=1)
    for (int n=2;n<=ordre;n+=2){
      v.push_back(-bernoulli(n)/n);
      v.push_back(0);
    }
    v.push_back(undef);
    return v;
  }
  static gen d_Psi_minus_ln(const gen & args,GIAC_CONTEXT){
    return inv(args,contextptr)-Psi(args,1,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_Psi_minus_ln," D_at_Psi_minus_ln",&d_Psi_minus_ln);
  static gen _Psi_minus_ln(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (is_inf(g))
      return 0;
    return symbolic(at_Psi_minus_ln,g);
  }
  static const char _Psi_minus_ln_s []="Psi_minus_ln";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __Psi_minus_ln,&_Psi_minus_ln,(unsigned long)&D_at_Psi_minus_lnunary_function_ptr,&taylor_Psi_minus_ln,_Psi_minus_ln_s);
#else
  static define_unary_function_eval_taylor( __Psi_minus_ln,&_Psi_minus_ln,D_at_Psi_minus_ln,&taylor_Psi_minus_ln,_Psi_minus_ln_s);
#endif
  define_unary_function_ptr5( at_Psi_minus_ln ,alias_at_Psi_minus_ln,&__Psi_minus_ln,0,true);
  static gen Psi_replace(const gen & g,GIAC_CONTEXT){
    return symbolic(at_ln,g)+_Psi_minus_ln(g,contextptr);
  }
  static gen taylor_Psi (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0){
      return 0; // statically handled now
      limit_tractable_functions().push_back(at_Psi);
      limit_tractable_replace().push_back(Psi_replace);
      return 1;
    }
    shift_coeff=0;
    if (!is_integer(lim_point) || is_strictly_positive(lim_point,contextptr))
      return taylor(lim_point,ordre,f,0,shift_coeff,contextptr);
    // FIXME Laurent series for Psi
    if (lim_point.type!=_INT_)
      return gensizeerr(contextptr);
    vecteur v;
    identificateur x(" ");
    int n=-lim_point.val;
    gen decal;
    for (int i=0;i<n;++i){
      decal -= inv(x+i,contextptr);
    }
    taylor(decal,x,lim_point,ordre,v,contextptr);
    gen Psi1=taylor(1,ordre,f,0,shift_coeff,contextptr);
    shift_coeff=-1;
    if (Psi1.type!=_VECT)
      return gensizeerr(contextptr);
    v=v+*Psi1._VECTptr;
    v.insert(v.begin(),-1);
    return v;
  }
  static gen d_Psi(const gen & args,GIAC_CONTEXT){
    vecteur v(gen2vecteur(args));
    if (v.size()==1)
      v.push_back(0);
    if (v.size()!=2 || v.back().type!=_INT_)
      return gendimerr(contextptr);
    return Psi(v.front(),v.back().val+1,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_Psi," D_at_Psi",&d_Psi);

  gen Psi(const gen & x,GIAC_CONTEXT){
    if (x.type==_FLOAT_)
      return Psi(get_double(x._FLOAT_val),contextptr);
    if (is_positive(-x,contextptr)){
      if (is_integer(x))
	return unsigned_inf;
      return Psi(ratnormal(1-x),contextptr)-cst_pi/tan(cst_pi*x,contextptr);
    }
    if (x==plus_inf)
      return x;
    if (is_undef(x))
      return x;
    if (is_inf(x))
      return undef;
    if ( (x.type==_INT_) && (x.val<10000) && (x.val>=1)){
      identificateur tt(" t");
      return -cst_euler_gamma+sum_loop(inv(tt,contextptr),tt,1,x.val-1,contextptr);
    }
    if (x.type==_FRAC){
      // Psi(m/k) for 0<m<k
      // Psi(m/k) = -euler_gamma -ln(2k) - pi/2/tan(m*pi/k) +
      //    + 2 sum( cos(2 *pi*n*m/k)*ln(sin(n*pi/k)), n=1..floor (k-1)/2 )
      gen num=x._FRACptr->num,den=x._FRACptr->den;
      if (num.type==_INT_ && den.type==_INT_ && den.val<13){
	int m=num.val,k=den.val;
	gen res;
	int mk=m/k;
	for (int i=mk;i>0;--i){
	  m -= k;
	  res += inv(m,contextptr);
	}
	res = k*res - cst_euler_gamma - ln(2*k,contextptr) - cst_pi/2/tan(m*cst_pi/k,contextptr);
	gen res1 ;
	for (int n=1;n<=(k-1)/2;n++){
	  res1 += cos(2*n*m*cst_pi/k,contextptr)*ln(sin(n*cst_pi/k,contextptr),contextptr);
	}
	return res + 2*res1;
      }
    }
#if 0 // def HAVE_LIBGSL
    if (x.type==_DOUBLE_)
      return gsl_sf_psi(x._DOUBLE_val);
#endif
#ifdef TARGET_OS_IPHONE
    if (x.type == _DOUBLE_)
      return psi(x._DOUBLE_val);
#endif
    if (x.type==_DOUBLE_){
      double z=x._DOUBLE_val;
      // z<=0 , psi(z)=pi*cotan(pi*z)-psi(1-z)
      // z>0, psi(z)=psi(z+1)-1/z
      // until x>10, 
      double res0=0,res1=0,res2=0;
      bool sub=false;
      if (z<0){
	res0=M_PI/std::tan(M_PI*z);
	z=1-z;
	sub=true;
      }
      for (;z<10;z++){
	res1 -= 1/z;
      }
      // ln(x)-1/2/x-1/12*1/x^2+1/120*1/x^4-1/252*1/x^6+1/240*1/x^8-1/132*1/x^10+691/32760*1/x^12-1/12*1/x^14
      res1 += std::log(z);
      z=1/z;
      res1 -= z/2;
      z=z*z;
      res2 = -z/12;
      res2 *= z;
      res2 += 691./32760.;
      res2 *= z;
      res2 -= 1./132.;
      res2 *= z;
      res2 += 1./240.;
      res2 *= z;
      res2 -= 1./252.;
      res2 *= z;
      res2 += 1./120.;
      res2 *= z;
      res2 -= 1./12.;
      res2 *= z;
      res1 += res2;
      if (sub)
	return res0-res1;
      else
	return res1;
    }
#ifdef HAVE_LIBPARI
    // if (x.type==_CPLX || x.type==_REAL)
    if (x.type==_REAL)
      return pari_psi(x);
#endif
    return symbolic(at_Psi,x);
  }
  // n-th derivative of digamma function
  gen Psi(const gen & x,int n,GIAC_CONTEXT){
    if (n<-1)
      return gensizeerr(contextptr);
    if (n==-1)
      return Gamma(x,contextptr);
    if (is_positive(-x,contextptr))
      return unsigned_inf;
    if (is_one(x)){
      if (n%2)
	return Zeta(n+1,contextptr)*factorial(n);
      else
	return -Zeta(n+1,contextptr)*factorial(n);
    }
    if (x==plus_inf)
      return zero;
    if (is_undef(x))
      return x;
    if (is_inf(x))
      return undef;
    if (!n)
      return Psi(x,contextptr);
    if ( (x.type==_INT_) && (x.val<10000) ){
      identificateur tt(" t");
      if (n%2)
	return factorial(n)*(Zeta(n+1,contextptr)-sum_loop(pow(tt,-n-1),tt,1,x.val-1,contextptr));
      else
	return -factorial(n)*(Zeta(n+1,contextptr)-sum_loop(pow(tt,-n-1),tt,1,x.val-1,contextptr));
    }
#ifdef HAVE_LIBGSL
    if (x.type==_DOUBLE_)
      return gsl_sf_psi_n(n,x._DOUBLE_val);
#endif 
    return symbolic(at_Psi,gen(makevecteur(x,n),_SEQ__VECT));
  }
  gen _Psi(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return Psi(args,contextptr);
    if ( args._VECTptr->size()!=2 )
      return symbolic(at_Psi,args);
    gen x(args._VECTptr->front()),n(args._VECTptr->back());
    if (n.type==_REAL)
      n=n.evalf_double(1,contextptr);
    if (n.type==_DOUBLE_)
      n=int(n._DOUBLE_val);
    if (n.type!=_INT_)
      return gensizeerr(contextptr);
    return Psi(x,n.val,contextptr);
  }
  static const char _Psi_s []="Psi";
#ifdef GIAC_HAS_STO_38
  define_unary_function_eval_taylor (__Psi,&_Psi,(unsigned long)&D_at_Psiunary_function_ptr,&taylor_Psi,_Psi_s);
#else
  define_unary_function_eval_taylor (__Psi,&_Psi,D_at_Psi,&taylor_Psi,_Psi_s);
#endif
  define_unary_function_ptr5( at_Psi ,alias_at_Psi,&__Psi,0,true);

  gen _normalmod(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen & f =g._VECTptr->front();
    if (f.is_symb_of_sommet(at_equal))
      return symb_equal(_normalmod(makevecteur(f._SYMBptr->feuille[0],g._VECTptr->back()),contextptr),
			_normalmod(makevecteur(f._SYMBptr->feuille[1],g._VECTptr->back()),contextptr));
    if (f.type==_VECT){
      vecteur v=*f._VECTptr;
      for (unsigned i=0;i<v.size();++i)
	v[i]=_normalmod(makevecteur(v[i],g._VECTptr->back()),contextptr);
      return gen(v,f.subtype);
    }
    gen res=normal(makemodquoted(f,g._VECTptr->back()),contextptr);
    if (f.type==_VECT && res.type==_VECT)
      res.subtype=f.subtype;
    return res;
  }
#ifdef GIAC_HAS_STO_38
  static const char _normalmod_s []="%%";
#else
  static const char _normalmod_s []="%";
#endif
  static define_unary_function_eval4_index (166,__normalmod,&_normalmod,_normalmod_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr( at_normalmod ,alias_at_normalmod ,&__normalmod);

  // a=expression, x variable, n=number of terms, 
  // compute an approx value of sum((-1)^k*a(k),k,0,+infinity)
  // using Chebychev polynomials
  gen alternate_series(const gen & a,const gen & x,int n,GIAC_CONTEXT){
    gen d=normal((pow(3+2*sqrt(2,contextptr),n)+pow(3-2*sqrt(2,contextptr),n))/2,contextptr);
    gen p=1;
    gen c=d-p;
    gen S=subst(a,x,0,false,contextptr)*c;
    for (int k=1;k<n;k++) {
      p=p*gen(k+n-1)*gen(k-n-1)/gen(k-inv(2,contextptr))/gen(k);
      c=-p-c;
      S=S+subst(a,x,k,false,contextptr)*c;
    }
    return S/d;
  }

  gen Eta(const gen & s,int ndiff,GIAC_CONTEXT){
    if (s.type==_INT_ && !ndiff){
      if (s==1)
	return symbolic(at_ln,2);
      if (s%2==0)
	return (1-pow(2,1-s,contextptr))*Zeta(s,contextptr);
    }
    if (s.type==_DOUBLE_ || s.type==_REAL || (s.type==_CPLX && s.subtype==_DOUBLE_)){
      gen rx=re(s,contextptr).evalf_double(1,contextptr);
      if (rx._DOUBLE_val<0.5){
	if (ndiff){
	  identificateur id(" ");
	  gen t(id),zeta;
	  zeta=derive((1-pow(2,1-t,contextptr))*pow(2*cst_pi,t,contextptr)/cst_pi*sin(cst_pi*t/2,contextptr)*symbolic(at_Gamma,1-t)*symbolic(at_Zeta,1-t),t,ndiff,contextptr);
	  zeta=subst(zeta,t,s,false,contextptr);
	  return zeta;
	}
	gen zeta1=Eta(1-s,0,contextptr)/(1-pow(2,s,contextptr));
	gen zetas=pow(2,s,contextptr)*pow(cst_pi,s-1,contextptr)*sin(cst_pi*s/2,contextptr)*Gamma(1-s,contextptr)*zeta1;
	return (1-pow(2,1-s,contextptr))*zetas;
      }
      // find n such that 3*(1+2*|y|)*exp(|y|*pi/2)*10^ndigits < (3+sqrt(8))^n
      gen ix=im(s,contextptr).evalf_double(1,contextptr);
      if (ix.type!=_DOUBLE_)
	return gentypeerr(contextptr);
      double y=std::abs(ix._DOUBLE_val);
      int ndigits=16; // FIXME? use decimal_digits;
      double n=(std::log10(3*(1+2*y)*std::exp(y*M_PI/2))+ndigits)/std::log10(3.+std::sqrt(8.));
      identificateur idx(" ");
      gen x(idx);
      gen res=alternate_series(inv(pow(idx+1,s,contextptr),contextptr)*pow(-ln(idx+1,contextptr),ndiff,contextptr),idx,int(std::ceil(n)),contextptr);
      return res.evalf(1,contextptr);
    }
    else {
      if (ndiff)
	return symbolic(at_Eta,gen(makevecteur(s,ndiff),_SEQ__VECT));
      else
	return symbolic(at_Eta,s);
    }
  }

  gen Eta(const gen & s0,GIAC_CONTEXT){
    gen s=s0;
    int ndiff=0;
    if (s.type==_VECT){
      if (s._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      gen n=s._VECTptr->back();
      if (n.type==_REAL)
	n=n.evalf_double(1,contextptr);
      if (n.type==_DOUBLE_)
	n=int(n._DOUBLE_val);
      if (n.type!=_INT_)
	return gentypeerr(contextptr);
      ndiff=n.val;
      s=s._VECTptr->front();
    }
    return Eta(s,ndiff,contextptr);
  }

  gen Zeta(const gen & x,int ndiff,GIAC_CONTEXT){
    if (!ndiff)
      return Zeta(x,contextptr);
    if (x.type==_DOUBLE_ || x.type==_REAL || (x.type==_CPLX && x.subtype==_DOUBLE_)){
      gen rex=re(x,contextptr).evalf_double(1,contextptr);
      if (rex.type!=_DOUBLE_)
	return gensizeerr(contextptr);
      identificateur id(" ");
      gen t(id),zeta;
      if (rex._DOUBLE_val<0.5){
	// Zeta(x)=2^x*pi^(x-1)*sin(pi*x/2)*Gamma(1-x)*zeta(1-x)
	zeta=derive(pow(2*cst_pi,t,contextptr)/cst_pi*sin(cst_pi*t/2,contextptr)*symbolic(at_Gamma,1-t)*symbolic(at_Zeta,1-t),t,ndiff,contextptr);
	zeta=subst(zeta,t,x,false,contextptr);
      }
      else {
	// Zeta=Eta(x)/(1-2^(1-x))
	zeta=derive(symbolic(at_Eta,t)/(1-pow(2,1-t,contextptr)),t,ndiff,contextptr);
	zeta=subst(zeta,t,x,false,contextptr);
      }
      return zeta;
    }
    return symbolic(at_Zeta,gen(makevecteur(x,ndiff),_SEQ__VECT));
  }
  gen Zeta(const gen & x,GIAC_CONTEXT){
    if (x.type==_VECT){
      if (x._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      gen n=x._VECTptr->back();
      if (n.type==_REAL)
	n=n.evalf_double(1,contextptr);
      if (n.type==_DOUBLE_)
	n=int(n._DOUBLE_val);
      if (n.type!=_INT_)
	return gentypeerr(contextptr);
      int ndiff=n.val;
      return Zeta(x._VECTptr->front(),ndiff,contextptr);
    }
    if ( (x.type==_INT_)){
      int n=x.val;
      if (!n)
	return minus_one_half;
      if (n==1)
	return plus_inf;
      if (n<0){
	if (n%2)
	  return -rdiv(bernoulli(1-n),(1-n),contextptr) ;
	else
	  return zero;
      }
      if (n%2)
	return symbolic(at_Zeta,x);
      else
	return pow(cst_pi,n)*ratnormal(abs(bernoulli(x),contextptr)*rdiv(pow(plus_two,n-1),factorial(n),contextptr));
    }
#ifdef HAVE_LIBGSL
    if (x.type==_DOUBLE_)
      return gsl_sf_zeta(x._DOUBLE_val);
#endif // HAVE_LIBGSL
#ifdef HAVE_LIBPARI
    if (x.type==_CPLX)
      return pari_zeta(x);
#endif
#ifdef HAVE_LIBMPFR
    if (x.type==_REAL){
      mpfr_t gam;
      int prec=mpfr_get_prec(x._REALptr->inf);
      mpfr_init2(gam,prec);
      mpfr_zeta(gam,x._REALptr->inf,GMP_RNDN);
      real_object res(gam);
      mpfr_clear(gam);
      return res;
    }
#endif
    if (x.type==_CPLX || x.type==_DOUBLE_ || x.type==_REAL)
      return Eta(x,contextptr)/(1-pow(2,1-x,contextptr));
    return symbolic(at_Zeta,x);
  }
  gen _Zeta(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return Zeta(args,contextptr);
  }
  static gen d_Zeta(const gen & args,GIAC_CONTEXT){
    vecteur v(gen2vecteur(args));
    if (v.size()==1)
      v.push_back(0);
    if (v.size()!=2 || v.back().type!=_INT_)
      return gendimerr(contextptr);
    return Zeta(v.front(),v.back().val+1,contextptr);
  }
  static gen taylor_Zeta(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0; // no symbolic preprocessing
    if (is_one(lim_point)){
      shift_coeff=-1;
      identificateur x(" "); vecteur v,w;
      taylor(1-pow(2,1-x,contextptr),x,1,ordre+1,w,contextptr);
      w.erase(w.begin());
      reverse(w.begin(),w.end());
      if (!w.empty() && is_undef(w.front()))
	w.erase(w.begin());
      gen gw=horner(w,x);
      sparse_poly1 sp=series__SPOL1(symbolic(at_Eta,x+1)/gw,x,0,ordre,0,contextptr); 
      sparse_poly1::const_iterator it=sp.begin(),itend=sp.end();
      for (;it!=itend;++it){
	v.push_back(it->coeff); // assumes all coeffs are non zero...
      }
      return v;
    }
    return taylor(lim_point,ordre,f,direction,shift_coeff,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_Zeta," D_at_Zeta",&d_Zeta);
  static const char _Zeta_s []="Zeta";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __Zeta,&_Zeta,(unsigned long)&D_at_Zetaunary_function_ptr,&taylor_Zeta,_Zeta_s);
#else
  static define_unary_function_eval_taylor( __Zeta,&_Zeta,D_at_Zeta,&taylor_Zeta,_Zeta_s);
#endif
  define_unary_function_ptr5( at_Zeta ,alias_at_Zeta,&__Zeta,0,true);

  static gen d_Eta(const gen & args,GIAC_CONTEXT){
    vecteur v(gen2vecteur(args));
    if (v.size()==1)
      v.push_back(0);
    if (v.size()!=2 || v.back().type!=_INT_)
      return gendimerr(contextptr);
    return Eta(v.front(),v.back().val+1,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_Eta," D_at_Eta",&d_Eta);
  gen _Eta(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return Eta(args,contextptr);
  }
  static const char _Eta_s []="Eta";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3 (__Eta,&_Eta,(unsigned long)&D_at_Etaunary_function_ptr,_Eta_s);
#else
  static define_unary_function_eval3 (__Eta,&_Eta,D_at_Eta,_Eta_s);
#endif
  define_unary_function_ptr5( at_Eta ,alias_at_Eta,&__Eta,0,true);

  // error function
  static gen taylor_erfs(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0;
    if (!is_inf(lim_point))
      return gensizeerr(contextptr);
    shift_coeff=1;
    // erfs(x)=1/sqrt(pi) * 1/x* sum( (2*k)! / (-4)^k / k! * x^(-2k) )
    gen tmp(1);
    vecteur v;
    for (int n=0;n<=ordre;){
      v.push_back(tmp);
      v.push_back(0);
      n +=2 ;
      tmp=gen(n-1)/gen(-2)*tmp;
    }
    v.push_back(undef);
    return multvecteur(inv(sqrt(cst_pi,contextptr),contextptr),v);
  }
  gen _erfs(const gen & g,GIAC_CONTEXT);
  static gen d_erfs(const gen & args,GIAC_CONTEXT){
    return 2*args*_erfs(args,contextptr)-gen(2)/sqrt(cst_pi,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_erfs," D_at_erfs",&d_erfs);
  gen _erfs(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (is_inf(g))
      return 0;
    return symbolic(at_erfs,g);
  }
  static const char _erfs_s []="erfs";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __erfs,&_erfs,(unsigned long)&D_at_erfsunary_function_ptr,&taylor_erfs,_erfs_s);
#else
  static define_unary_function_eval_taylor( __erfs,&_erfs,D_at_erfs,&taylor_erfs,_erfs_s);
#endif
  define_unary_function_ptr5( at_erfs ,alias_at_erfs,&__erfs,0,true);
  static gen erf_replace(const gen & g,GIAC_CONTEXT){
    return symbolic(at_sign,g)*(1-symbolic(at_exp,-g*g)*_erfs(symbolic(at_abs,g),contextptr));
  }
  static gen taylor_erf (const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0){
      return 0; // statically handled now
      limit_tractable_functions().push_back(at_erf);
      limit_tractable_replace().push_back(erf_replace);
      return 1;
    }
    shift_coeff=0;
    return taylor(lim_point,ordre,f,0,shift_coeff,contextptr);
  }
  static gen d_erf(const gen & e,GIAC_CONTEXT){
    return 2*exp(-pow(e,2),contextptr)/sqrt(cst_pi,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_erf," D_at_erf",d_erf);

  static gen erf0(const gen & x,gen & erfc,GIAC_CONTEXT){
    if (x.type==_DOUBLE_){ 
      double absx=std::abs(x._DOUBLE_val);
      if (absx<=3){
	// numerical computation of int(exp(-t^2),t=0..x) 
	// by series expansion at x=0
	// x*sum( (-1)^n*(x^2)^n/n!/(2*n+1),n=0..inf)
	long_double z=x._DOUBLE_val,z2=z*z,res=0,pi=1;
	for (int n=0;;){
	  res += pi/(2*n+1);
	  ++n;
	  pi = -pi*z2/n;
	  if (pi<1e-17 && pi>-1e-17)
	    break;
	}
	erfc=double(1-2/std::sqrt(M_PI)*z*res);
	return 2/std::sqrt(M_PI)*double(z*res);
      }
      if (absx>=6.5){
	// asymptotic expansion at infinity of int(exp(-t^2),t=x..inf)
	// z=1/x
	// z*exp(-x^2)*(1/2 - 1/4 z^2 +3/8 z^4-15/16 z^6 + ...)
	long_double z=1/absx,z2=z*z/2,res=0,pi=0.5;
	for (int n=0;;++n){
	  res += pi;
	  pi = -pi*(2*n+1)*z2;
	  if (std::abs(pi)<1e-16)
	    break;
	}
	erfc=2/std::sqrt(M_PI)*double(std::exp(-1/z/z)*z*res);
	gen e=1-erfc;
	if (x._DOUBLE_val>=0)
	  return e;
	erfc=2-erfc;
	return -e;
      }
      else { 
	// erf(x)=2*x*exp(-x^2)/sqrt(pi)*sum(2^j*x^(2j)/1/3/5/.../(2j+1),j=0..inf)
	// or continued fraction
	// 2*exp(z^2)*int(exp(-t^2),t=z..inf)=1/(z+1/2/(z+1/(z+3/2/(z+...))))
	long_double z=absx,res=0;
	for (long_double n=40;n>=1;n--){
	  res=n/2/(z+res);
	}
	res=1/(z+res);
	erfc=std::exp(-absx*absx)*double(res)/std::sqrt(M_PI);
	gen e=1-erfc;
	if (x._DOUBLE_val>=0)
	  return e;
	erfc=2-erfc;
	return -e;
      }
#if 0
      // a:=convert(series(erfc(x)*exp(x^2),x=X,24),polynom):; b:=subst(a,x=X+h):;
      if (absx>3 && absx<=5){
	// Digits:=30; evalf(symb2poly(subst(b,X,4),h))
	long_double Zl=absx-4,res=0;
	long_double taberf[]={0.9323573505930262336910814663629e-18,-0.5637770672346891132663122366369e-17,0.3373969923698176600796949171416e-16,-0.1997937342757611758805760309387e-15,0.1170311628709846086671746844320e-14,-0.6779078623355796103927587022047e-14,0.3881943235655598141099274338263e-13,-0.2196789805508621713379735090290e-12,0.1228090799753488475137690971599e-11,-0.6779634525816110746734938098109e-11,0.3694326453071165814527058450923e-10,-0.1986203171147991823844885265211e-9,0.1053084120195192127202221248092e-8,-0.5503368542058483880654875851859e-8,0.2833197888944711586737808090450e-7,-0.1435964425391227330876779173688e-6,0.7160456646037012951391007806358e-6,-0.3510366649840828060143659147374e-5,0.1690564925777814684043808381146e-4,-0.7990888030555549397777128848414e-4,0.3703524689955564311420527395424e-3,-0.1681182076746114476323671722330e-2,0.7465433244975570766528102818814e-2,-0.3238350609502145478059791886069e-1,0.1369994576250613898894451230325};
	unsigned N=sizeof(taberf)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  res *= Zl;
	  res += taberf[i];
	}
	erfc = double(std::exp(-absx*absx)*res);
	return sign(x,contextptr)*(1-erfc);
      }
      if (absx>5 && absx<=6.5){
	// Digits:=30; evalf(symb2poly(subst(b,X,5.75),h))
	long_double Zl=absx-5.75,res=0;
	long_double taberf[]={-0.3899077949952308336341205103240e-12,0.2064555746398182434172952813760e-13,-0.7079917646274828801231710613009e-12,-0.2043006626755557967429543230042e-12,-0.2664588032913413248313045028978e-11,-0.3182230691773937386262907009549e-11,-0.4508687162250923186571888867300e-12,-0.2818971742901571639195611759894e-11,-0.4771270499789446447101554995178e-11,0.2345376254096117543212461524786e-11,-0.6529305258174487397807156793042e-11,0.9817004987916722489154147719630e-12,0.2085292084663647123257426988484e-10,-0.1586500138272075839895787048265e-9,0.1056533982771769784560244626854e-8,-0.6964568016562765632682760517056e-8,0.4530411628438409475101496352516e-7,-0.2918364042864784155554051827879e-6,0.1859299481340192895158490699981e-5,-0.1171241494503672776195474661763e-4,0.7292428889065898343608897828825e-4,-0.4485956983428598110336671805311e-3,0.2725273842847326036320664185043e-2,-0.1634321814380709002113440890281e-1,0.9669877816971385564543076482100e-1};
	unsigned N=sizeof(taberf)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  res *= Zl;
	  res += taberf[i];
	}
	erfc = double(std::exp(-absx*absx)*res);
	return sign(x,contextptr)*(1-erfc);
      }
#endif
    } // end x.type==_DOUBLE_
    gen z=evalf_double(abs(x,contextptr),1,contextptr);
    if (x.type==_CPLX && x._CPLXptr->type!=_REAL){
      double absx=z._DOUBLE_val;
      complex_long_double z(evalf_double(re(x,contextptr),1,contextptr)._DOUBLE_val,
			     evalf_double(im(x,contextptr),1,contextptr)._DOUBLE_val);
      if (absx<=3){
	// numerical computation of int(exp(-t^2),t=0..x) 
	// by series expansion at x=0
	// x*sum( (-1)^n*(x^2)^n/n!/(2*n+1),n=0..inf)
	complex_long_double z2=z*z,res=0,pi=1;
	for (long_double n=0;;){
	  res += pi/(2*n+1);
	  ++n;
	  pi = -pi*z2/n;
	  if (std::abs(pi)<1e-17)
	    break;
	}
#ifndef HAVE_LONG_DOUBLE
	res=(2.0/std::sqrt(M_PI))*z*res;
#else
	res=(2.0L/std::sqrt(M_PI))*z*res;
#endif
	gen e(double(res.real()),double(res.imag()));
	erfc=1.0-e;
	return e;
      }
      bool neg=z.real()<0;
      if (neg)
	z=-z;
      if (absx>=6.5){
	// asymptotic expansion at infinity of int(exp(-t^2),t=x..inf)
	// z=1/x
	// z*exp(-x^2)*(1/2 - 1/4 z^2 +3/8 z^4-15/16 z^6 + ...)
#ifndef HAVE_LONG_DOUBLE
	z=1.0/z;
	complex_long_double z2=z*z/2.0,res=0,pi=0.5;
#else
	z=1.0L/z;
	complex_long_double z2=z*z/2.0L,res=0,pi=0.5;
#endif
	for (long_double n=0;;++n){
	  res += pi;
	  pi = -pi*(2*n+1)*z2;
	  if (std::abs(pi)<1e-16)
	    break;
	}
#ifndef HAVE_LONG_DOUBLE
	res=complex_long_double(2.0/std::sqrt(M_PI))*std::exp(-1.0/z/z)*z*res;
#else
	res=complex_long_double(2.0/std::sqrt(M_PI))*std::exp(-1.0L/z/z)*z*res;
#endif
	erfc=gen(double(res.real()),double(res.imag()));
	gen e=1-erfc;
	if (!neg)
	  return e;
	erfc=2-erfc;
	return -e;
      }
      else { 
	// continued fraction
	// 2*exp(z^2)*int(exp(-t^2),t=z..inf)=1/(z+1/2/(z+1/(z+3/2/(z+...))))
	complex_long_double res=0;
	for (long_double n=40;n>=1;n--){
	  res=(n/2)/(z+res);
	}
#ifndef HAVE_LONG_DOUBLE
	res=1.0/(z+res);
#else
	res=1.0L/(z+res);
#endif
	res=std::exp(-z*z)*res/complex_long_double(std::sqrt(M_PI));
	erfc=gen(double(res.real()),double(res.imag()));
	gen e=1-erfc;
	if (!neg)
	  return e;
	erfc=2-erfc;
	return -e;
      }
    } // end low precision
    // take account of loss of accuracy
    int prec=decimal_digits(contextptr);
    int newprec,nbitsz=int(z._DOUBLE_val*z._DOUBLE_val/std::log(2.)),prec2=int(prec*std::log(10.0)/std::log(2.0)+.5);
    if (nbitsz>prec2){ 
      // use asymptotic expansion at z=inf
      z = accurate_evalf(inv(x,contextptr),prec2);
      gen z2=z*z/2,res=0,pi=inv(accurate_evalf(plus_two,prec2),contextptr),eps=accurate_evalf(pow(10,-prec,contextptr),prec2)/2;
      for (int n=0;;++n){
	res += pi;
	pi = -(2*n+1)*z2*pi;
	if (is_greater(eps,abs(pi,contextptr),contextptr))
	  break;
      }
      erfc=evalf(2*inv(sqrt(cst_pi,contextptr),contextptr),1,contextptr)*exp(-inv(z*z,contextptr),contextptr)*z*res;
      return 1-erfc;
    }
    if (z._DOUBLE_val>1)
      newprec = prec2+nbitsz+int(std::log(z._DOUBLE_val))+1;
    else
      newprec = prec2+2;
    // numerical computation of int(exp(-t^2),t=0..x) 
    // by series expansion at x=0
    // x*sum( (-1)^n*(x^2)^n/n!/(2*n+1),n=0..inf)
    z=accurate_evalf(x,newprec);
    gen z2=z*z,res=0,pi=1,eps=accurate_evalf(pow(10,-prec,contextptr),prec2)/2;
    for (int n=0;;){
      res += pi/(2*n+1);
      ++n;
      pi = -pi*z2/n;
      if (is_greater(eps,abs(pi,contextptr),contextptr))
	break;
    }
    res = evalf(2*inv(sqrt(cst_pi,contextptr),contextptr),1,contextptr)*z*res;
    erfc=accurate_evalf(1-res,prec2);
    return accurate_evalf(res,prec2);
  }
  gen erf(const gen & x,GIAC_CONTEXT){
    if (is_equal(x))
      return apply_to_equal(x,erf,contextptr);
    if (x.type==_FLOAT_)
      return erf(get_double(x._FLOAT_val),contextptr);
    if (x==plus_inf)
      return plus_one;
    if (x==minus_inf)
      return minus_one;
    if (is_undef(x))
      return x;
    if (is_inf(x))
      return undef;
    if (is_zero(x,contextptr))
      return x;
    gen erfc_;
    if (x.type==_DOUBLE_ || x.type==_CPLX || x.type==_REAL)
      return erf0(x,erfc_,contextptr);
#if 0 // def GIAC_HAS_STO_38
    return 1-2*symbolic(at_UTPN,x*plus_sqrt2);
#else
    return symbolic(at_erf,x);
#endif
    gen e=x.evalf(1,contextptr);
#ifdef HAVE_LIBGSL
    if (e.type==_DOUBLE_)
      return gsl_sf_erf(e._DOUBLE_val);
#endif
#ifdef HAVE_LIBMPFR
    if (x.type==_REAL){
      mpfr_t gam;
      int prec=mpfr_get_prec(x._REALptr->inf);
      mpfr_init2(gam,prec);
      mpfr_erf(gam,x._REALptr->inf,GMP_RNDN);
      real_object res(gam);
      mpfr_clear(gam);
      return res;
    }
#endif
#if 0 // def GIAC_HAS_STO_38
    return 1-2*symbolic(at_UTPN,x*plus_sqrt2);
#else
    return symbolic(at_erf,x);
#endif
  }
  gen _erf(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return apply(args,erf,contextptr);
  }
  static const char _erf_s []="erf";
#ifdef GIAC_HAS_STO_38
  define_unary_function_eval_taylor( __erf,&_erf,(unsigned long)&D_at_erfunary_function_ptr,&taylor_erf,_erf_s);
#else
  define_unary_function_eval_taylor( __erf,&_erf,D_at_erf,&taylor_erf,_erf_s);
#endif
  define_unary_function_ptr5( at_erf ,alias_at_erf,&__erf,0,true);

  static gen d_erfc(const gen & e,GIAC_CONTEXT){
    return -d_erf(e,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_erfc," D_at_erfc",d_erfc);
  gen erfc(const gen & x,GIAC_CONTEXT){
    if (x.type==_FLOAT_)
      return erfc(get_double(x._FLOAT_val),contextptr);
    if (is_equal(x))
      return apply_to_equal(x,erfc,contextptr);
    gen erfc_;
    if (x.type==_DOUBLE_ || x.type==_CPLX || x.type==_REAL){
      erf0(x,erfc_,contextptr);
      return erfc_;
    }
#if 0 // def GIAC_HAS_STO_38
    return 2*symbolic(at_UTPN,x*plus_sqrt2);
#else
    return 1-symbolic(at_erf,x);
#endif
    gen e=x.evalf(1,contextptr);
#ifdef HAVE_LIBGSL
    if (e.type==_DOUBLE_)
      return gsl_sf_erfc(e._DOUBLE_val);
#endif
#if 0 // def GIAC_HAS_STO_38
    return 2*symbolic(at_UTPN,x*plus_sqrt2);
#else
    return 1-symbolic(at_erf,x);
#endif
  }
  gen _erfc(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return apply(args,erfc,contextptr);
  }
  static const char _erfc_s []="erfc";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3 (__erfc,&_erfc,(unsigned long)&D_at_erfcunary_function_ptr,_erfc_s);
#else
  static define_unary_function_eval3 (__erfc,&_erfc,D_at_erfc,_erfc_s);
#endif
  define_unary_function_ptr5( at_erfc ,alias_at_erfc,&__erfc,0,true);

  // assumes z>=1
  static const double exp_minus_1_over_4=std::exp(-0.25);
  static void sici_fg(double z,double & fz,double & gz){
    // int([u*]exp(-u)/(u^2+z^2),0,inf)
    // #nstep=1000 in [0,1], then * e^(-1/4)
    double nstep=250,a=0;
    fz=0; gz=0;
    for (;nstep>0.25;nstep*=exp_minus_1_over_4){
      double Fz=0,Gz=0;
      int N=int(nstep+.5);
      if (N<1)
	N=1;
      // Simpson over [a,a+1]
      double t=a,tmp,z2=z*z,Ninv=1./N;
      t = a+Ninv/2.;
      double expt=std::exp(-t),expfact=std::exp(-Ninv);
      for (int i=0;i<N;++i){ // middle points
	tmp = expt/(t*t+z2); 
	Fz += tmp;
	Gz += t*tmp;
	expt *= expfact;
	t += Ninv;
      }
      Fz *= 2; Gz *= 2;
      t = a+Ninv;
      expt=std::exp(-t);
      for (int i=1;i<N;++i){ 
	tmp = expt/(t*t+z2); // endpoint
	Fz += tmp;
	Gz += t*tmp;
	expt *= expfact;
	t += Ninv;
      }
      Fz *= 2; Gz *= 2;
      tmp=std::exp(-a)/(a*a+z*z); // endpoint
      Fz += tmp;
      Gz += a*tmp;
      a++;
      tmp=std::exp(-a)/(a*a+z*z); // endpoint
      Fz += tmp;
      Gz += a*tmp;
      fz += Fz/(6*N);
      gz += Gz/(6*N);
    }
    fz *= z;
  }

  // mode=1 Si only, mode==2 Ci only
  static bool sici(const gen & z0,gen & siz,gen & ciz,int prec,int mode,GIAC_CONTEXT){
    gen z=evalf_double(z0,1,contextptr);
    if (z0.type==_DOUBLE_ && prec>13)
      prec=13;
#ifdef GIAC_HAS_STO_38
    if (z.type!=_DOUBLE_)
      return false;
    prec=13;
#endif
    if (z.type==_DOUBLE_ && prec<=13){
      double Z=z._DOUBLE_val,fz,gz;
#if defined HAVE_LIBGSL && 0
      if (mode==1){
	siz=gsl_sf_Si(Z);
	return true;
      }
      if (mode==2){
	if (Z<0)
	  ciz=gen(gsl_sf_Ci(-Z),M_PI);
	else
	  ciz=gsl_sf_Ci(Z);
	return true;
      }
#endif
      if (Z>=40 || Z<=-40){
	// use series expansion at infinity
	// Si: 1/2*PI - 1/z*cos(z) - 1/z^2*sin(z) + 2/z^3*cos(z) + 6/z^4*sin(z) - 24/z^5*cos(z) - 120/z^6*sin(z) + 720/z^7*cos(z) + 5040/z^8*sin(z) + O(1/z^9)	= 1/z^8( (pi/2*z-cz)*z ...)
	// Ci:1/z*sin(z) - 1/z^2*cos(z) - 2/z^3*sin(z) + 6/z^4*cos(z) + 24/z^5*sin(z) - 120/z^6*cos(z) - 720/z^7*sin(z) + 5040/z^8*cos(z)
	long_double sz=std::sin(Z);
	long_double cz=std::cos(Z);
	long_double invZ=1/Z;
	long_double pi=invZ;
	long_double sizd=Z>0?M_PI/2:-M_PI/2,cizd=0;
	for (int n=1;;++n){
	  switch (n%4){
	  case 1:
	    sizd -= pi*cz;
	    cizd += pi*sz;
	    break;
	  case 2:
	    sizd -= pi*sz;
	    cizd -= pi*cz;
	    break;
	  case 3:
	    sizd += pi*cz;
	    cizd -= pi*sz;
	    break;
	  case 0:
	    sizd += pi*sz;
	    cizd += pi*cz;
	    break;
	  }
	  if (pi<1e-16 && pi>-1e-16)
	    break;
	  pi *= n*invZ;
	}
	siz = double(sizd);
	ciz = Z>0?double(cizd):gen(double(cizd),M_PI);
	/*
	double z8=Z*Z;
	z8*=z8;
	z8*=z8;
	siz=((((((((M_PI/2*Z-cz)*Z-sz)*Z+2*cz)*Z+6*sz)*Z-24*cz)*Z-120*sz)*Z+720*cz)*Z+5040)/z8;
	ciz=((((((((sz)*Z-cz)*Z-2*sz)*Z+6*cz)*Z+24*sz)*Z-120*cz)*Z-720*sz)*Z+5040)/z8;
	*/
	return true;
      }
      bool neg=Z<0;
      if (neg) Z=-Z;
      if (Z<=8){
	long_double si=1,ci=0,z2=Z*Z,pi=1;
	for (long_double n=1;;n++){
	  pi = -pi*z2/2/n;
	  if (std::abs(pi)<1e-15)
	    break;
	  ci += pi/(2*n);
	  pi /= (2*n+1);
	  si += pi/(2*n+1);
	}
	siz=double(si*Z);
	ciz=double(ci)+std::log(Z)+cst_euler_gamma;
      }
      // Digits:=30; 
      // a:=convert(series(Si(x),x=X,24),polynom):; b:=subst(a,x=X+h):; 
      // c:=convert(series(Ci(x),x=X,24),polynom):; d:=subst(c,x=X+h):; 
      if (Z>8 && Z<=12){
	long_double Zl=Z-10,ress=0,resc=0;
	// evalf(symb2poly(subst(b,X,10),h))
	long_double tabsi[]={-0.1189416530979229549237628888274e-25,0.1535274533580010929112764295251e-23,0.5949595042899632105631168585106e-23,-0.8443813569533766615075729254715e-21,-0.2314493549701736156628360858149e-20,0.3873999840160633357506392314227e-18,0.6314325721978121436699115557946e-18,-0.1454512706077874699091092895960e-15,-0.7966560468972510895796615830931e-16,0.4362654888706402638259447137657e-13,-0.2168961216095219762135154593351e-13,-0.1013156299801104705978428503741e-10,0.1511362807589119975552764377684e-10,0.1746079592768605042057230051441e-8,-0.4215112103088864749315276215891e-8,-0.2100827763936543847478539598343e-6,0.6768578499749603731655034337648e-6,0.1604768832104729109406272571796e-4,-0.6129221770634575410555873017014e-4,-0.6629459359846050378314018176664e-3,0.2619937628043294083259734576549e-2,0.1168258324144788179314042496964e-1,-0.3923347089937577354591945908199e-1,-0.5440211108893698134047476618518e-1,1.658347594218874049330971879387};
	// evalf(symb2poly(subst(d,X,10),h))
	long_double tabci[]={-0.1031363603561483377414206719978e-24,0.1612636587877966055062771505636e-24,0.3224608155602632232346779331477e-22,0.1692359618263088728225313523766e-21,-0.1902123006508838353447776226346e-19,-0.3515542663511577547829464092398e-19,0.7651991655644681341759387392123e-17,0.8949035335940300076443987350711e-17,-0.2601535596364695291068633323882e-14,0.1492297097331336833098183955735e-16,0.6873241144777191906667850002758e-12,-0.6815990267379124633604933192924e-12,-0.1385917806100726110447949877846e-9,0.2729216217490738797188995072400e-9,0.2012042413927794669971423802132e-7,-0.5698511913235728873777759634867e-7,-0.1960205632344228431569991958688e-5,0.6982250568929225912819532196480e-5,0.1127699306487082097807313715561e-3,-0.4465373163022154950275303555869e-3,-0.3158611974102019356519036678335e-2,0.1189143127195082400887895227495e-1,0.3139641318985075293153170283171e-1,-0.8390715290764524522588639478252e-1,-0.4545643300445537263453282995265e-1};
	unsigned N=sizeof(tabsi)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  ress *= Zl;
	  ress += tabsi[i];
	  resc *= Zl;
	  resc += tabci[i];
	}
	siz = double(ress);
	ciz = double(resc);
      }
      if (Z>12 && Z<=16){
	long_double Zl=Z-14,ress=0,resc=0;
	// evalf(symb2poly(subst(b,X,14),h))
	long_double tabsi[]={0.4669904672048171207530336926784e-25,-0.9121786080411940331362079921979e-24,-0.2670914489796227617771058426181e-22,0.5191793400822084981037748064947e-21,0.1272661806026797328866077155393e-19,-0.2467115711789321804079825604360e-18,-0.4949985288801387245191790228644e-17,0.9598983157925822187983801948972e-16,0.1531267131408434243475192526878e-14,-0.2983793513482513341175920391396e-13,-0.3640741847215956508751181538961e-12,0.7178248462975320846576276463699e-11,0.6346880693851116027919629277690e-10,-0.1280755925139665180236732107401e-8,-0.7574841879361234814393864898316e-8,0.1596987755895423889046710494666e-6,0.5558236361973162161308791480329e-6,-0.1276894967030880532682787806556e-4,-0.2074774698099097315147678882137e-4,0.5764575129603710060263581799849e-3,0.2308201450150731015736777254153e-3,-0.1190515482960545313209358846732e-1,0.2356412497996938805126656809673e-2,0.7075766826391930770538248600113e-1,1.556211050077665053703631892805};
	// evalf(symb2poly(subst(d,X,14),h))
	long_double tabci[]={0.3590233410769916717046880365555e-25,0.1141530173081541381259185089975e-23,-0.2223749846955296806257123587014e-22,-0.5971404695883450494036609310213e-21,0.1158813189176036949355044721895e-19,0.2578286744261932540063357506560e-18,-0.4996649806240905822071761629155e-17,-0.8975849912962755765138097664122e-16,0.1743615446376990117868764420851e-14,0.2446425362039524592303128232180e-13,-0.4789979360061655431004102341364e-12,-0.5015267234200784104185754752298e-11,0.9985346701639551763361641926310e-10,0.7310382166844782587890268654904e-9,-0.1502609970889405144661999732289e-7,-0.6957715037844896183079492333545e-7,0.1519752625305293353227780696896e-5,0.3762397782384872898110463611961e-5,-0.9310463095669218193046370118801e-4,-0.8685445941902185669380116006632e-4,0.2944299062831149098901196099828e-2,0.7349281020023392469738413739405e-4,-0.3572765356616331098087728605326e-1,0.9766944157702399589209205476559e-2,0.6939635592758454727438326824349e-1};
	unsigned N=sizeof(tabsi)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  ress *= Zl;
	  ress += tabsi[i];
	  resc *= Zl;
	  resc += tabci[i];
	}
	siz = double(ress);
	ciz = double(resc);
      }
      if (Z>16 && Z<=20){
	long_double Zl=Z-18,ress=0,resc=0;
	// evalf(symb2poly(subst(b,X,18),h))
	long_double tabsi[]={-0.5458114686729331288343465771243e-25,-0.8535297797031477670863377809864e-25,0.3197608273507048053143023106525e-22,0.1246720240695204431925986220740e-22,-0.1566961070193290350813150654958e-19,0.1120194984050557421284180749579e-19,0.6303732529750142701943836944779e-17,-0.1093865529886308098492342051935e-16,-0.2034127294195017035840150503185e-14,0.5391577496552481117289518608625e-14,0.5113326950563000131064845650605e-12,-0.1755022270416406807283368583049e-11,-0.9642855115888351354955178064729e-10,0.3896583006063678015119758986001e-9,0.1297996094042326342264874814159e-7,-0.5741523749720224155210168829004e-7,-0.1165550911079956021485851292181e-5,0.5260587329402175094599529167340e-5,0.6336730653896082851113120333026e-4,-0.2682059741680869878136005839481e-3,-0.1788149401756335521591446074162e-2,0.6231324073036488917300258847156e-2,0.1950106172093382517043203223869e-1,-0.4172151370953756131945311580259e-1,1.536608096861185462361173893885};
	// evalf(symb2poly(subst(d,X,18),h))
	long_double tabci[]={0.4605275954646862944758091918129e-26,-0.1349519299011741936395476560680e-23,-0.1307682310402805091861215069192e-23,0.7246129197780950670155731691447e-21,-0.1246271694903983843859628582537e-21,-0.3225645499768452910431606017148e-18,0.3989357101823634351098507496171e-18,0.1165949042798889880769685702457e-15,-0.2573970823221046721180373208376e-15,-0.3334412559655945734048458981847e-13,0.1020420669194920098922521974402e-12,0.7298982331099322669182907795178e-11,-0.2745289334776461767357052328935e-10,-0.1171271721735576258954174760357e-8,0.4994621772350737506214065546293e-8,0.1300541789133113153488950327832e-6,-0.5864843122180159884437825091296e-6,-0.9221666449442426870101941344121e-5,0.4080390556697611593508057267335e-4,0.3702810510394427353858531043357e-3,-0.1453024604178293371013986047647e-2,-0.6848923209258520415117450659078e-2,0.1984174958896001500414615659760e-1,0.3668426156911556360089444693281e-1,-0.4347510299950100478344114920850e-1};
	unsigned N=sizeof(tabsi)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  ress *= Zl;
	  ress += tabsi[i];
	  resc *= Zl;
	  resc += tabci[i];
	}
	siz = double(ress);
	ciz = double(resc);
      }
      if (Z>20 && Z<=24){
	long_double Zl=Z-22,ress=0,resc=0;
	// evalf(symb2poly(subst(b,X,22),h))
	long_double tabsi[]={0.3374532347188926046754549614410e-25,0.9070719831057349388641148466696e-24,-0.2050470901003743543468128570765e-22,-0.4594235790411492575575248293744e-21,0.1043077621302187913107676015839e-19,0.1910932463670378689024003307389e-18,-0.4360804036884663718921536467101e-17,-0.6379227769542456535760586922387e-16,0.1464704096058402216999762277897e-14,0.1660737164480043388814198356103e-13,-0.3842072518362213364190884929092e-12,-0.3249870299707868231630032306411e-11,0.7591535353403568501001826557431e-10,0.4554066973680817003059315739181e-9,-0.1077692372064168550494073939261e-7,-0.4274452798917825988601610781581e-7,0.1030486252719749122401833140117e-5,0.2434192199039423608811016740448e-5,-0.6042868558775171616279520355687e-4,-0.7128407780774990405916452478794e-4,0.1868111001271415320776084256504e-2,0.7554565401849909491334584321080e-3,-0.2271723850350373234098156405564e-1,-0.4023322404729034509859207643533e-3,1.616083736594366543114431027190};
	// evalf(symb2poly(subst(d,X,22),h))
	long_double tabci[]={-0.3765941636516127667565314425887e-25,0.8496429623966734299562910340895e-24,0.2089658856058364573723209071877e-22,-0.4733667956308362832271784627271e-21,-0.9616010057686047674500629395344e-20,0.2188568968552838678620763986263e-18,0.3594653788547495746845572205294e-17,-0.8227046014999096676822143059749e-16,-0.1063984308386789623300816541173e-14,0.2451691569424535439026086961327e-13,0.2414081477262495987003288682969e-12,-0.5610174912354327213432820922583e-11,-0.4025699100579353159380991679438e-10,0.9460092423484752010231070364358e-9,0.4662816039604651635851886085410e-8,-0.1112697436829371328490149410190e-6,-0.3461508105965530058056263010906e-6,0.8452332929171272742294005025613e-5,0.1452920166854777888625357916022e-4,-0.3688187418989882360609440618036e-3,-0.2737432060552935755550251671918e-3,0.7538061305932840665075723970069e-2,0.1234183502875539666044793790453e-2,-0.4545276483611986938428066996417e-1,0.1640691915737749726680980654224e-2};
	unsigned N=sizeof(tabsi)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  ress *= Zl;
	  ress += tabsi[i];
	  resc *= Zl;
	  resc += tabci[i];
	}
	siz = double(ress);
	ciz = double(resc);
      }
      if (Z>24 && Z<=28){
	long_double Zl=Z-26,ress=0,resc=0;
	// evalf(symb2poly(subst(b,X,26),h))
	long_double tabsi[]={0.1887595283929249736721564257313e-26,-0.1139208683110194949664423805669e-23,0.1279293933254064768731875653817e-24,0.5994732455107313947881755894338e-21,-0.6938324133833276535897299455340e-21,-0.2609102593630429565352138793600e-18,0.5435603049161089391681629274496e-18,0.9202351759037677282055983009108e-16,-0.2642554055043397588710384653172e-15,-0.2565196994817985742348447471878e-13,0.8979673506711721488705329079164e-13,0.5477157384399615846158662911243e-11,-0.2160870416302321163457598611639e-10,-0.8604342982500329851062267981807e-9,0.3594354283136494037733432184009e-8,0.9424490157133423171849887794118e-7,-0.3925808731949007274318185773343e-6,-0.6671455253428628462019837043580e-5,0.2584931618742702225630929467268e-4,0.2717002055000108101387898426941e-3,-0.8869394862544894803875105833456e-3,-0.5192726828102161497754294326711e-2,0.1187673367608361403346165544076e-1,0.2932917117229241298137781896944e-1,1.544868862986338557887737260292};
	// evalf(symb2poly(subst(d,X,26),h))
	long_double tabci[]={0.4649965583842175529710094230187e-25,0.2092492486523365857112019931102e-25,-0.2673016958726137563529919009844e-22,0.1734025691834661144821904431335e-22,0.1282183044327200389255292828905e-19,-0.2098089236197822391981775009963e-19,-0.5037728169303807883904931875640e-17,0.1257389975122490684981594653098e-16,0.1585106167468127577346565423777e-14,-0.5084215538447167537343863000543e-14,-0.3884144934780823340053700189354e-12,0.1455636906316532474122453840240e-11,0.7154603949717881354941340050201e-10,-0.2926061246791173118887623808558e-9,-0.9458827111752210232753218600667e-8,0.3976537604156996965393015686438e-7,0.8424410070112619898093729646951e-6,-0.3418064228754065522708009442873e-5,-0.4606856152608972144114035752184e-4,0.1664083688146614131038736660077e-3,0.1330470954446840977471854832752e-2,-0.3758634727512557357034117912936e-2,-0.1514307620917034875601537686776e-1,0.2488151239725539779697630391881e-1,0.2829515103175713190842112993963e-1};
	unsigned N=sizeof(tabsi)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  ress *= Zl;
	  ress += tabsi[i];
	  resc *= Zl;
	  resc += tabci[i];
	}
	siz = double(ress);
	ciz = double(resc);
      }
      if (Z>28 && Z<=32){
	long_double Zl=Z-30,ress=0,resc=0;
	// evalf(symb2poly(subst(b,X,30),h))
	long_double tabsi[]={-0.3146268636172325352644123013773e-25,0.7254967655706034123075144269188e-24,0.1720174348300531678711926627452e-22,-0.3968569600567226603419285560888e-21,-0.7804624742706385979212734054562e-20,0.1797226284123711388607362992709e-18,0.2882267004838688287625265808775e-17,-0.6604428381371346662942676361200e-16,-0.8462601728339625717076212469620e-15,0.1921641824294710456184856743277e-13,0.1918622280824819669976604364077e-12,-0.4293140602943259867747088385429e-11,-0.3236373134197171392533933286615e-10,0.7078744765376845042933221167302e-9,0.3867645142776632643256131482060e-8,-0.8169087758905646352536906937371e-7,-0.3060269540778692505740670587310e-6,0.6120146081774563343983258719578e-5,0.1450591123346014175404634002192e-4,-0.2651270545919250186145646630982e-3,-0.3497315371034554476595429181296e-3,0.5419736490383548422011597468850e-2,0.3119763955955768506415340725399e-2,-0.3293438746976205966625829690981e-1,1.566756540030351110983731309007};
	// evalf(symb2poly(subst(d,X,30),h))
	long_double tabci[]={-0.2905250884383236235247049545527e-25,-0.7522147039999421611567058749977e-24,0.1735426584530328263231524611399e-22,0.3754699661541031707840824496312e-21,-0.8657191669382300006764470296916e-20,-0.1541017153772568372597216447575e-18,0.3541395808613865455763442962592e-17,0.5090911414277503198761655016250e-16,-0.1161952195659956230574302196387e-14,-0.1318846041657448769184055840206e-13,0.2975305748369348193892361410833e-12,0.2592737707940046163669864700563e-11,-0.5742839188863978180610659896436e-10,-0.3707322050098237760097147226680e-9,0.7983406320009368395835407645496e-8,0.3641426564991240108255042664073e-7,-0.7507713655866267126314943513395e-6,-0.2264698987507883174282691040657e-5,0.4355811042213145491501868952153e-4,0.7862739829137060637224202271609e-4,-0.1341741499597397210639678491289e-2,-0.1220985799040877684843355198131e-2,0.1638149848494348313828544726235e-1,0.5141714996252801690622071553807e-2,-0.3303241728207114377922644096301e-1};
	unsigned N=sizeof(tabsi)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  ress *= Zl;
	  ress += tabsi[i];
	  resc *= Zl;
	  resc += tabci[i];
	}
	siz = double(ress);
	ciz = double(resc);
      }
      if (Z>32 && Z<=36){
	long_double Zl=Z-34,ress=0,resc=0;
	// evalf(symb2poly(subst(b,X,34),h))
	long_double tabsi[]={0.3942701136493758442738611783193e-25,0.2833482488121121562882295508384e-25,-0.2240325735608330926855387141189e-22,0.7077167897405254555239797927716e-23,0.1062083455080499454129483304028e-19,-0.1296740970607253649730617065007e-19,-0.4125642950551060755694922602219e-17,0.8186731227770529065956573976157e-17,0.1284716025190091972930389282765e-14,-0.3331328985339278802607691455602e-14,-0.3121425078659463413394477246734e-12,0.9467354869456215167864364128202e-12,0.5717466510263820214761281907592e-10,-0.1880717150280359445242629986190e-9,-0.7546218335244881072513219000215e-8,0.2525355992897561612710992980882e-7,0.6743126364121930403816188890288e-6,-0.2149417013042573939863424458069e-5,-0.3721263582524267372627099974480e-4,0.1039917503622257770319033100678e-3,0.1091628590022319276482516968274e-2,-0.2344369704089296833875653244402e-2,-0.1270781662145181668046346804335e-1,0.1556125547411834767154373923623e-1,1.595256185182468624967114677624};
	// evalf(symb2poly(subst(d,X,34),h))
	long_double tabci[]={-0.1989238045881366884396486216401e-26,0.9603929081698055330298655453613e-24,0.1785462486600632168852339199134e-24,-0.4994884203374138675491408864852e-21,0.3922698307156298349251565849186e-21,0.2148764364742271875693500554094e-18,-0.3483461372134677428776900288130e-18,-0.7495914823918311062321675163567e-16,0.1730727343462677961269223481865e-15,0.2069644169058104778527335300702e-13,-0.5867510845332297156886886169438e-13,-0.4387405297943553775874545604808e-11,0.1397446669253207042207953256760e-10,0.6866413079078539001893686720856e-9,-0.2296062969528945671977337418042e-8,-0.7526095978772233458886342141369e-7,0.2479954929300904010747076408018e-6,0.5360278479932581967324034110246e-5,-0.1619607535570418039830425866209e-4,-0.2210046023539758078033314699734e-3,0.5534219043710011378918782268888e-3,0.4305022897404827350706361160583e-2,-0.7413599071494898236031397261835e-2,-0.2495794925837074078235212022679e-1,0.1626491643735576698165635194377e-1};
	unsigned N=sizeof(tabsi)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  ress *= Zl;
	  ress += tabsi[i];
	  resc *= Zl;
	  resc += tabci[i];
	}
	siz = double(ress);
	ciz = double(resc);
      }
      if (Z>36 && Z<=40){
	long_double Zl=Z-38,ress=0,resc=0;
	// evalf(symb2poly(subst(b,X,38),h))
	long_double tabsi[]={-0.2392585617788018962646757828598e-25,-0.6575431727115741067484987610228e-24,0.1413726322474858257725781544417e-22,0.3273737185831529184312733883924e-21,-0.6971366411141976840382089119809e-20,-0.1343722941531172977052740901750e-18,0.2818131926670801219167357117818e-17,0.4456155806753828158414772784641e-16,-0.9138685919934995126194762439509e-15,-0.1164855661769990237307373295310e-13,0.2314363358966319503074680596234e-12,0.2327043421113577898681844454632e-11,-0.4423649753121148492913255999601e-10,-0.3413423247744706973417929454576e-9,0.6100985644119355599936971303758e-8,0.3483915381970249998364550866465e-7,-0.5705724285037609250439734410714e-6,-0.2292102947589923998615699771400e-5,0.3301272634561039972744957647445e-4,0.8640908538565720173919303844142e-4,-0.1017258860929286693108579223376e-2,-0.1518531271099188526195110204881e-2,0.1246413777530741664504711921590e-1,0.7799173123931192562955174996028e-2,1.545492937235698740561891130750};
	// evalf(symb2poly(subst(d,X,38),h))
	long_double tabci[]={0.2755791623901057620946655522961e-25,-0.5942947240530035802859253027110e-24,-0.1501343221496874685931095691537e-22,0.3214487908378114008198384990138e-21,0.6802473399014152745014932500302e-20,-0.1438706210063382233790032141791e-18,-0.2516685599977182931504499600334e-17,0.5224619763598943054009172687575e-16,0.7435302907498417390873416469940e-15,-0.1502856747601093160946258165352e-13,-0.1706516669026401836883958369119e-12,0.3322517447592247618873995073660e-11,0.2938003484322757578999393723335e-10,-0.5429671029677216915133045537076e-9,-0.3623244387376009573032456389105e-8,0.6223561267305907587303733846902e-7,0.3003453535122368377351197880341e-6,-0.4643099720179236845576657726334e-5,-0.1523777445337208725039230269865e-4,0.2008948838914583162973945866431e-3,0.4061768073150514090668429199326e-3,-0.4114703864552309315370227396080e-2,-0.4230290732342083420531097274815e-2,0.2513351694861302256806674303698e-1,0.7129761801971379713551376511546e-2};
	unsigned N=sizeof(tabsi)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  ress *= Zl;
	  ress += tabsi[i];
	  resc *= Zl;
	  resc += tabci[i];
	}
	siz = double(ress);
	ciz = double(resc);
      }
      if (Z>40 && Z<40) { // not used anymore, too slow
	sici_fg(Z,fz,gz);
	siz=M_PI/2-fz*std::cos(Z)-gz*std::sin(Z);
	ciz=fz*std::sin(Z)-gz*std::cos(Z);
      }
      if (neg){
	siz = -siz;
	if (mode!=1)
	  ciz = gen(ciz,M_PI);
      }
      return true;
    }
    z=evalf_double(abs(z0,contextptr),1,contextptr);
    if (z.type!=_DOUBLE_)
      return false; // gentypeerr(gettext("sici")); 
    if (prec<13){
      gen z=z0;
      bool p=is_positive(re(z0,contextptr),contextptr);
      if (!p)
	z=-z;
      gen a=Ei(cst_i*z,contextptr),b=Ei(-cst_i*z,contextptr);
      ciz=(a+b)/2;
      if (!p){
	if (is_positive(im(z0,contextptr),contextptr))
	  ciz=ciz+cst_i*cst_pi;
	else
	  ciz=ciz-cst_i*cst_pi;
      }
      siz=(a-b)/2/cst_i-cst_pi_over_2;
      if (!p)
	siz=-siz;
      return true;
    }
    // find number of digits that must be added to prec
    // n^n/n! equivalent to e^n*sqrt(2*pi*n)
    int newprec,nbitsz=int(z._DOUBLE_val/std::log(2.)),prec2=int(prec*std::log(10.0)/std::log(2.0)+.5);
    if (nbitsz>prec2){ 
      // use asymptotic expansion at z=inf
      z = accurate_evalf(z0,prec2);
      gen sinz=sin(z,contextptr),cosz=cos(z,contextptr);
      gen invc=1,invs=0,pi=1,eps=accurate_evalf(pow(10,-prec,contextptr),prec2)/2;
      for (int n=1;;++n){
	if (is_greater(eps,abs(pi,contextptr),contextptr))
	  break;
	pi = (n*pi)/z;
	if (n%2){
	  if (n%4==1)
	    invs += pi;
	  else
	    invs -= pi;
	}
	else {
	  if (n%4==0)
	    invc += pi;
	  else
	    invc -= pi;
	}
      }
      siz=m_pi(prec2)/2-cosz/z*invc-sinz/z*invs;
      ciz=sinz/z*invc-cosz/z*invs;
      return true;
    }
    // use series expansion at z=0
    if (z._DOUBLE_val>1)
      newprec = prec2+nbitsz+int(std::log(z._DOUBLE_val)/2)+1;
    else
      newprec = prec2+2;
    z = accurate_evalf(z0,newprec);
    gen si=1,ci=0,z2=z*z,pi=1,eps=accurate_evalf(pow(10,-prec,contextptr),newprec)/2;
    for (int n=1;;n++){
      pi = pi*z2/(2*n*(2*n-1));
      if (is_greater(eps,abs(pi,contextptr),contextptr))
	break;
      if (mode!=1){
	if (n%2)
	  ci -= pi/(2*n);
	else
	  ci += pi/(2*n);
      }
      if (mode!=2){
	if (n%2)
	  si -= pi/((2*n+1)*(2*n+1));
	else
	  si += pi/((2*n+1)*(2*n+1));
      }
    }
    if (mode!=2)
      siz=si*accurate_evalf(z0,prec2);
    if (mode!=1){
      ciz=ci+ln(z,contextptr)+m_gamma(newprec);
      ciz=accurate_evalf(ciz,prec2);
    }
    return true;
  }

  static gen taylor_SiCi_f(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0;
    if (!is_inf(lim_point))
      return taylor(lim_point,ordre,f,0,shift_coeff,contextptr);
    shift_coeff=1;
    // f(x)=1/x* sum( +/-(2*k)!*x^(-2k) )
    gen tmp(1);
    vecteur v;
    for (int n=0;n<=ordre;){
      v.push_back(tmp);
      v.push_back(0);
      n +=2 ;
      tmp=-gen((n-1)*n)*tmp;
    }
    v.push_back(undef);
    return v;
  }
  gen _SiCi_g(const gen & args,GIAC_CONTEXT);
  static gen d_SiCi_f(const gen & args,GIAC_CONTEXT){
    return -_SiCi_g(args,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_SiCi_f," D_at_SiCi_f",&d_SiCi_f);
  gen _Si(const gen & args,GIAC_CONTEXT);
  gen _Ci(const gen & args,GIAC_CONTEXT);
  gen _SiCi_f(const gen & args,GIAC_CONTEXT){
    if (args.type==_FLOAT_)
      return _SiCi_f(get_double(args._FLOAT_val),contextptr);
    if (is_inf(args))
      return 0;
    if (is_zero(args,contextptr))
      return unsigned_inf;
    if (is_undef(args))
      return args;
    if (args.type==_DOUBLE_ || args.type==_REAL)
      return _Ci(args,contextptr)*sin(args,contextptr)+(evalf(cst_pi/2,1,contextptr)-_Si(args,contextptr))*cos(args,contextptr);
    return symbolic(at_SiCi_f,args);
  }
  static const char _SiCi_f_s []="SiCi_f";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __SiCi_f,&_SiCi_f,(unsigned long)&D_at_SiCi_funary_function_ptr,&taylor_SiCi_f,_SiCi_f_s);
#else
  static define_unary_function_eval_taylor( __SiCi_f,&_SiCi_f,D_at_SiCi_f,&taylor_SiCi_f,_SiCi_f_s);
#endif
  define_unary_function_ptr5( at_SiCi_f ,alias_at_SiCi_f,&__SiCi_f,0,true);

  static gen taylor_SiCi_g(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0;
    if (!is_inf(lim_point))
      return taylor(lim_point,ordre,f,0,shift_coeff,contextptr);
    shift_coeff=2;
    // f(x)=sum( +/-(2*k+1)!*x^(-2k+2) )
    gen tmp(1);
    vecteur v;
    for (int n=1;n<=ordre+1;){
      v.push_back(tmp);
      v.push_back(0);
      n +=2 ;
      tmp=-gen((n-1)*n)*tmp;
    }
    v.push_back(undef);
    return v;
  }
  static gen d_SiCi_g(const gen & args,GIAC_CONTEXT){
    return inv(args,contextptr)+_SiCi_f(args,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_SiCi_g," D_at_SiCi_g",&d_SiCi_g);
  gen _SiCi_g(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_FLOAT_)
      return _SiCi_g(get_double(args._FLOAT_val),contextptr);
    if (is_inf(args))
      return 0;
    if (is_zero(args,contextptr))
      return unsigned_inf;
    if (is_undef(args))
      return args;
    if (args.type==_DOUBLE_ || args.type==_REAL)
      return -_Ci(args,contextptr)*cos(args,contextptr)+(evalf(cst_pi/2,1,contextptr)-_Si(args,contextptr))*sin(args,contextptr);
    return symbolic(at_SiCi_g,args);
  }
  static const char _SiCi_g_s []="SiCi_g";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __SiCi_g,&_SiCi_g,(unsigned long)&D_at_SiCi_gunary_function_ptr,&taylor_SiCi_g,_SiCi_g_s);
#else
  static define_unary_function_eval_taylor( __SiCi_g,&_SiCi_g,D_at_SiCi_g,&taylor_SiCi_g,_SiCi_g_s);
#endif
  define_unary_function_ptr5( at_SiCi_g ,alias_at_SiCi_g,&__SiCi_g,0,true);

  static gen Si_replace(const gen & g,GIAC_CONTEXT){
    return cst_pi_over_2-_SiCi_f(g,contextptr)*cos(g,contextptr)-_SiCi_g(g,contextptr)*sin(g,contextptr);
  }
  static gen taylor_Si(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0){
      return 0; // statically handled now
      limit_tractable_functions().push_back(at_Si);
      limit_tractable_replace().push_back(Si_replace);
      return 1;
    }
    shift_coeff=0;
    if (is_zero(lim_point,contextptr)){
      shift_coeff=1;
      vecteur v;
      gen pi(1);
      for (int i=0;i<=ordre;){
	v.push_back(plus_one/pi);
	v.push_back(0);
	i += 2;
	pi = -(i*(i+1))*pi;
      }
      return v;
    }
    if (!is_inf(lim_point))
      return taylor(lim_point,ordre,f,direction,shift_coeff,contextptr);
    return gentypeerr(contextptr);
  }
  static gen d_Si(const gen & args,GIAC_CONTEXT){
    return sin(args,contextptr)/args;
  }
  define_partial_derivative_onearg_genop( D_at_Si," D_at_Si",&d_Si);
  gen _Si(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_FLOAT_)
      return evalf2bcd(_Si(get_double(args._FLOAT_val),contextptr),1,contextptr);
    if (is_zero(args,contextptr))
      return args;
    if (is_undef(args))
      return args;
    if (is_inf(args)){
      if (args==plus_inf)
	return cst_pi_over_2;
      if (args==minus_inf)
	return -cst_pi_over_2;
      return undef;
    }
    if (args.is_symb_of_sommet(at_neg))
      return -_Si(args._SYMBptr->feuille,contextptr);
    if (args.type!=_DOUBLE_ && args.type!=_REAL && args.type!=_CPLX)
      return symbolic(at_Si,args);
    gen si,ci;
    if (!sici(args,si,ci,decimal_digits(contextptr),1,contextptr))
      return gensizeerr(contextptr);
    return si;
  }
  static const char _Si_s []="Si";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __Si,&_Si,(unsigned long)&D_at_Siunary_function_ptr,&taylor_Si,_Si_s);
#else
  static define_unary_function_eval_taylor( __Si,&_Si,D_at_Si,&taylor_Si,_Si_s);
#endif
  define_unary_function_ptr5( at_Si ,alias_at_Si,&__Si,0,true);

  static gen Ci_replace(const gen & g,GIAC_CONTEXT){
    return _SiCi_f(g,contextptr)*sin(g,contextptr)-_SiCi_g(g,contextptr)*cos(g,contextptr);
  }
  gen _Ci0(const gen &,GIAC_CONTEXT);
  gen Ci_replace0(const gen & g,GIAC_CONTEXT){
    return _Ci0(g,contextptr)+cst_euler_gamma+ln(abs(g,contextptr),contextptr);  
  }
  static gen taylor_Ci(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0){
      return 0; // statically handled now
      limit_tractable_functions().push_back(at_Ci);
      limit_tractable_replace().push_back(Ci_replace);
      return 1;
    }
    shift_coeff=0;
    if (!is_inf(lim_point))
      return taylor(lim_point,ordre,f,direction,shift_coeff,contextptr);
    return gentypeerr(contextptr);
  }
  static gen d_Ci(const gen & args,GIAC_CONTEXT){
    return cos(args,contextptr)/args;
  }
  define_partial_derivative_onearg_genop( D_at_Ci," D_at_Ci",&d_Ci);
  gen _Ci(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_FLOAT_)
      return evalf2bcd(_Ci(get_double(args._FLOAT_val),contextptr),1,contextptr);
    if (is_zero(args,contextptr))
      return minus_inf;
    if (is_undef(args))
      return args;
    if (is_inf(args)){
      if (args==plus_inf)
	return 0;
      if (args==minus_inf)
	return cst_pi*cst_i;
      return undef;
    }
    if (args.type!=_DOUBLE_ && args.type!=_REAL && args.type!=_CPLX)
      return symbolic(at_Ci,args);
    gen si,ci;
    if (!sici(args,si,ci,decimal_digits(contextptr),2,contextptr))
      return gensizeerr(contextptr);
    return ci;
  }
  static const char _Ci_s []="Ci";
#ifdef GIAC_HAS_STO_38
  define_unary_function_eval_taylor( __Ci,&_Ci,(unsigned long)&D_at_Ciunary_function_ptr,&taylor_Ci,_Ci_s);
#else
  define_unary_function_eval_taylor( __Ci,&_Ci,D_at_Ci,&taylor_Ci,_Ci_s);
#endif
  define_unary_function_ptr5( at_Ci ,alias_at_Ci,&__Ci,0,true);

  static gen d_Ci0(const gen & args,GIAC_CONTEXT){
    return (cos(args,contextptr)-1)/args;
  }
  define_partial_derivative_onearg_genop( D_at_Ci0," D_at_Ci0",&d_Ci0);
  static gen taylor_Ci0(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0;
    if (!is_zero(lim_point,contextptr))
      return taylor(lim_point,ordre,f,0,shift_coeff,contextptr);
    shift_coeff=2;
    // sum( (-1)^k/(2*k)/(2*k)! * x^(2k) )
    gen tmp(1);
    vecteur v;
    for (int n=0;n<=ordre;){
      n +=2 ;
      tmp=-gen((n-1)*n)*tmp;
      v.push_back(inv(n*tmp,contextptr));
      v.push_back(0);
    }
    v.push_back(undef);
    return v;
  }
  gen _Ci0(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_zero(args,contextptr))
      return 0;
    if (is_undef(args))
      return args;
    if (is_inf(args))
      return minus_inf;
    if (args.type!=_DOUBLE_ && args.type!=_REAL && args.type!=_CPLX)
      return symbolic(at_Ci0,args);
    gen si,ci;
    if (!sici(args,si,ci,decimal_digits(contextptr),2,contextptr))
      return gensizeerr(contextptr);
    return ci-evalf(cst_euler_gamma,1,contextptr)-ln(args,contextptr);
  }
  static const char _Ci0_s []="Ci0";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __Ci0,&_Ci0,(unsigned long)&D_at_Ci0unary_function_ptr,&taylor_Ci0,_Ci0_s);
#else
  static define_unary_function_eval_taylor( __Ci0,&_Ci0,D_at_Ci0,&taylor_Ci0,_Ci0_s);
#endif
  define_unary_function_ptr5( at_Ci0 ,alias_at_Ci0,&__Ci0,0,true); /* FIXME should not registered */

  gen _Ei_f(const gen & args,GIAC_CONTEXT);
  static gen taylor_Ei_f(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0;
    if (!is_inf(lim_point))
      return gensizeerr(contextptr);
    shift_coeff=1;
    // f(x)=1/x* sum( k!/x^(k) )
    gen tmp(1);
    vecteur v;
    for (int n=1;n<=ordre+1;n++){
      v.push_back(tmp);
      tmp=n*tmp;
    }
    v.push_back(undef);
    return v;
  }
  static gen d_Ei_f(const gen & args,GIAC_CONTEXT){
    return -_Ei_f(args,contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_Ei_f," D_at_Ei_f",&d_Ei_f);
  gen _Ei_f(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_inf(args))
      return 0;
    if (is_zero(args,contextptr))
      return unsigned_inf;
    if (is_undef(args))
      return args;
    return symbolic(at_Ei_f,args);
  }
  static const char _Ei_f_s []="Ei_f";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __Ei_f,&_Ei_f,(unsigned long)&D_at_Ei_funary_function_ptr,&taylor_Ei_f,_Ei_f_s);
#else
  static define_unary_function_eval_taylor( __Ei_f,&_Ei_f,D_at_Ei_f,&taylor_Ei_f,_Ei_f_s);
#endif
  define_unary_function_ptr5( at_Ei_f ,alias_at_Ei_f,&__Ei_f,0,true);
  static gen Ei_replace(const gen & g,GIAC_CONTEXT){
    return _Ei_f(g,contextptr)*exp(g,contextptr);
  }  
  gen _Ei0(const gen & args,GIAC_CONTEXT);
  gen Ei_replace0(const gen & g,GIAC_CONTEXT){
    return _Ei0(g,contextptr)+cst_euler_gamma+ln(abs(g,contextptr),contextptr);  
  }
  static gen taylor_Ei(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0){
      return 0; // statically handled now
      limit_tractable_functions().push_back(at_Ei);
      limit_tractable_replace().push_back(Ei_replace);
      return 1;
    }
    shift_coeff=0;
    if (!is_inf(lim_point))
      return taylor(lim_point,ordre,f,direction,shift_coeff,contextptr);
    return gentypeerr(contextptr);
  }
  static gen d_Ei(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT)
      return exp(args,contextptr)/args;
    vecteur v=*args._VECTptr;
    if (v.size()==1)
      return exp(v.front(),contextptr)/v.front();
    return gendimerr(contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_Ei," D_at_Ei",&d_Ei);
  gen Ei(const gen & args,GIAC_CONTEXT){
    if (args.type==_FLOAT_)
      return Ei(get_double(args._FLOAT_val),contextptr);
    if (is_zero(args,contextptr))
      return minus_inf;
    if (args==plus_inf || is_undef(args))
      return args;
    if (args==minus_inf)
      return 0;
    if (is_inf(args))
      return undef;
    if (args.type!=_DOUBLE_ && args.type!=_REAL && args.type!=_CPLX)
      return symbolic(at_Ei,args);
    gen z=evalf_double(abs(args,contextptr),1,contextptr);
    if (z.type!=_DOUBLE_)
      return gentypeerr(contextptr); 
    int prec=decimal_digits(contextptr);
    if (args.type==_DOUBLE_ && prec>13)
      prec=13;
    if (args.type==_DOUBLE_ && prec<=13){
      double z=args._DOUBLE_val;
#if 0 // def HAVE_LIBGSL
      return gsl_sf_expint_Ei(z);
#endif
      if (z>=40 || z<=-40){
	long_double ei=1,pi=1,Z=z;
	for (long_double n=1;;++n){
	  if (pi<1e-16 && pi>-1e-16)
	    break;
	  pi = (n*pi)/Z;
	  ei += pi;
	}
	return double(std::exp(Z)/Z*ei);
      }
      if (z>=-4.8 && z<=40){
	// ? use __float80 or __float128
	/*
#ifdef __SSE__
#if defined __x86_64__ && defined __SSE_4_2__
	__float128 ei=0.0q,pi=1.0q;
#else
	__float80 ei=0.0w,pi=1.0w;
#endif // __SSE4_2__
	*/
	long_double ei=0.0,pi=1.0,Z=z;
	for (long_double n=1;;n++){
	  pi = pi*Z/n;
	  if (pi<1e-16 && pi>-1e-16)
	    break;
	  ei += pi/n;
	}
	ei=ei+std::log(std::abs(z))+0.577215664901532860610;
	return double(ei);
      }
      // continued fraction: http://people.math.sfu.ca/~cbm/aands/page_229.htm
      long_double x=-z;
      long_double result(1);
      long_double un(1);
      for (long_double n=40;n>=1;--n){
	result = un+n/result;
	result = x+n/result; 
      }
      result=-un/result*std::exp(-x);
      return gen(double(result));
#if 0
      // a:=convert(series(Ei(x)*exp(-x)*x,x=X,24),polynom):; b:=subst(a,x=X+h):; 
      if (z>=-6.8 && z<=-4.8){
	// X:=-5.8; evalf(symb2poly(b,h),30)
	long_double Z=z+5.8,res=0;
	long_double tabei[]={-0.3151760388807517547897224622361e-20,-0.1956623502102099783599191666531e-19,-0.1217520387814662777242246174541e-18,-0.7595047623259136899131074978509e-18,-0.4750592523487122640844934717658e-17,-0.2979985874126626857226335496504e-16,-0.1875104560810577497563994966761e-15,-0.1183827325179695999391747354358e-14,-0.7501052898029529880772284438566e-14,-0.4771568760084635063692127230846e-13,-0.3048290706102002977129135629199e-12,-0.1956495512880190361879173037779e-11,-0.1262184835369108393063770847820e-10,-0.8188615023197271684637356284403e-10,-0.5345588144426140308176155460920e-9,-0.3513748178583494436182263562858e-8,-0.2327416524478792602782072181673e-7,-0.1554886566283128983353292920891e-6,-0.1048823536032497188023815631115e-5,-0.7151898287935501422833421755707e-5,-0.4937247597221480470432894324202e-4,-0.3456479830723309944342902721052e-3,-0.2458903778230241247990734517862e-2,-0.1781701304096371729351217642124e-1,0.8681380405349396412209368563589};
	unsigned N=sizeof(tabei)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  res *= Z;
	  res += tabei[i];
	}
	return double(res*std::exp(z)/z);
      }
      if (z>=-10.4 && z<=-6.8){
	// X:=-8.6; evalf(symb2poly(b,h),30)
	long_double Z=z+8.6,res=0;
	long_double tabei[]={-0.3038274728374471199550377e-24,-0.2779136645187427028874693e-23,-0.3038274728374471199535898278331e-24,-0.2779136645187427028908730164655e-23,-0.2546955934813756337104395625899e-22,-0.2338906925011672805172017690719e-21,-0.2152486734154319991905416359525e-20,-0.1985479703050588294559030935503e-19,-0.1835926261877877389191827053171e-18,-0.1702095774807244266388321827485e-17,-0.1582462980029593441341590109962e-16,-0.1475687715549251315001108783383e-15,-0.1380597694124124865185429282546e-14,-0.1296174167643919255669841368436e-13,-0.1221540406726513790755510762310e-12,-0.1155953021811538418057560238246e-11,-0.1098796276688146853531628645304e-10,-0.1049579707566189469341186813219e-9,-0.1007939580405777662197671844560e-8,-0.9736450265211550752217060575644e-8,-0.9466101382146753511496418259404e-7,-0.9269139557230184816973381023220e-6,-0.9148312509513609827739907179727e-5,-0.9108785014936600974832901874747e-4,-0.9158817613130838424132243282306e-3,-0.9310767919833080253847393582574e-2,0.9041742295948504677274049567506};
	unsigned N=sizeof(tabei)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  res *= Z;
	  res += tabei[i];
	}
	return double(res*std::exp(z)/z);
      }
      if (z>=-18 && z<=-10.4){
	// X:=-14.2; evalf(symb2poly(b,h),30)
	long_double Z=z+14.2,res=0;
	long_double tabei[]={-0.2146565037696152744587246594658e-29,-0.3211304301798548507083223372513e-28,-0.4810676293065620718028423299344e-27,-0.7216859970389437335874447555454e-26,-0.1084277395671112546831370934086e-24,-0.1631614493247996191078695765867e-23,-0.2459334296271721080391164633531e-22,-0.3713482299827011821020880718604e-21,-0.5617631647301656953696071542646e-20,-0.8514935739998273979648471651860e-19,-0.1293355781746933007839041344760e-17,-0.1968884006429264318605910506869e-16,-0.3004345775527340969677228988332e-15,-0.4595950524037171042443121664043e-14,-0.7049704421594200850156215866786e-13,-0.1084472101777504561037738433261e-11,-0.1673430730968429348565586095542e-10,-0.2590825472368919959973457219967e-9,-0.4025494197722650107148738975481e-8,-0.6278735181439544538790456358391e-7,-0.9834030941528033320856991985477e-6,-0.1547201663333347394190105495487e-4,-0.2446158986745476113536605827777e-3,-0.3888037771084034459793304640141e-2,0.9378427721282495585084911135452};
	unsigned N=sizeof(tabei)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  res *= Z;
	  res += tabei[i];
	}
	return double(res*std::exp(z)/z);
      }
      if (z>=-28 && z<=-18){
	// X:=-23; evalf(symb2poly(b,h),30)
	long_double Z=z+23,res=0;
	long_double tabei[]={-0.2146168427075858494404136614850e-34,-0.5148499454995822704300301651629e-33,-0.1236169966295832115713010472045e-31,-0.2970790204951956634218583491140e-30,-0.7146257090942497496926778641796e-29,-0.1720741952629494954062600276217e-27,-0.4147649238148296325602078162016e-26,-0.1000823187587987074490282090878e-24,-0.2417703015904985630926544707734e-23,-0.5847381076193017965725594038649e-22,-0.1415979006710406329134293651920e-20,-0.3433325477996997527365162488189e-19,-0.8336110427404205659872823554255e-18,-0.2026897569209606939865219036707e-16,-0.4935733317936049783947792956182e-15,-0.1203807749521077525537452325013e-13,-0.2940930287406907738614747539418e-12,-0.7197370501359682859646891271814e-11,-0.1764684158114017110045008515596e-9,-0.4335208135324139738484130923065e-8,-0.1067215959531320911498195408991e-6,-0.2632980854624917892922066469020e-5,-0.6511098477481142813714679454527e-4,-0.1614117344014970753628852067852e-2,0.9598801957880143469722276499000};
	unsigned N=sizeof(tabei)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  res *= Z;
	  res += tabei[i];
	}
	return double(res*std::exp(z)/z);
      }
      if (z>=-40 && z<=-28){
	// X:=-34; evalf(symb2poly(b,h),30)
	long_double Z=z+34,res=0;
	long_double tabei[]={-0.1553338170441157171980055301967e-38,-0.6629618584891807480484960239352e-37,-0.2063078177267001621688370383419e-35,-0.7866363363832491801531045634862e-34,-0.2644473347532265615154979481412e-32,-0.9583293463074797094792612009694e-31,-0.3331568830413908327708929748672e-29,-0.1185447458884996131099536033526e-27,-0.4172725550206304813111805693360e-26,-0.1478006485774725675043732714563e-24,-0.5225975670861681162526724856274e-23,-0.1851238198626063152547466792341e-21,-0.6560288456307499178019660543116e-20,-0.2327113628743444723634985600950e-18,-0.8261670399157726998435042888476e-17,-0.2935758096939480075974014556136e-15,-0.1044193898775627537692001439649e-13,-0.3717665504357675255012806230568e-12,-0.1324967081687119761655731034082e-10,-0.4727208926929774019493746342695e-9,-0.1688455962743104637225875947715e-7,-0.6037839002081685958714139659847e-6,-0.2161738491599201737451243010035e-4,-0.7749596600489697701377944551020e-3,0.9721813893840475706338481431853};
	unsigned N=sizeof(tabei)/sizeof(long_double);
	for (unsigned i=0;i<N;i++){
	  res *= Z;
	  res += tabei[i];
	}
	return double(res*std::exp(z)/z);
      }
      // not used anymore, too slow
      // z<0: int(e^t/t,t,-inf,z)=e^z*int(e^(-u)/(u-z),t,0,inf)
      // z>0: Ei(9.)+int(e^t/t,t,9,z) = Ei(9.)-e^z*int(e^(-u)/(u-z),u,0,z-9)
      double nstep=400,a=0,fz=0; 
      for (;nstep>0.25;nstep*=exp_minus_1_over_4){
	double Fz=0;
	int N=int(nstep+.5);
	if (N<1)
	  N=1;
	double taille=1.0;
	if (z>0 && a+1>z-9)
	  taille=(z-9)-a;
	// Simpson over [a,a+taille]
	double t=a,tmp,Ninv=taille/N;
	t = a+Ninv/2.;
	double expt=std::exp(-t),expfact=std::exp(-Ninv);
	for (int i=0;i<N;++i){ // middle points
	  tmp = expt/(t-z); 
	  Fz += tmp;
	  expt *= expfact;
	  t += Ninv;
	}
	Fz *= 2; 
	t = a+Ninv;
	expt=std::exp(-t);
	for (int i=1;i<N;++i){ 
	  tmp = expt/(t-z); // endpoint
	  Fz += tmp;
	  expt *= expfact;
	  t += Ninv;
	}
	Fz *= 2; 
	tmp=std::exp(-a)/(a-z); // endpoint
	Fz += tmp;
	a += taille;
	tmp=std::exp(-a)/(a-z); // endpoint
	Fz += tmp;
	fz += Fz*taille/(6*N);
	if (z>0 && a>=z-9)
	  break;
      }
      fz *= std::exp(z);
      if (z<0)
	return fz;
      return 1037.878290717090-fz;
#endif 
    } // end real cas
    if (prec<=13 && z._DOUBLE_val>=2.5 && z._DOUBLE_val<=40){
      // continued fraction: http://people.math.sfu.ca/~cbm/aands/page_229.htm
      complex_long_double x(evalf_double(re(args,contextptr),1,contextptr)._DOUBLE_val,
			evalf_double(im(args,contextptr),1,contextptr)._DOUBLE_val);
      x=-x;
      if (x.real()>0 || std::abs(x.imag()/x.real())>=1){
	complex_long_double result(1);
	long_double un(1);
	for (long_double n=40;n>=1;--n){
	  result = un+n/result;
	  result = x+n/result; 
	}
	result=-un/result*std::exp(-x);
	return gen(double(result.real()),double(result.imag())+M_PI*(x.imag()>0?-1:1));
      }
    }
#if 1 // defined(__x86_64__) || defined(__i386__) // if long_double available use this
    gen tmp=evalf_double(args,1,contextptr);
    if (tmp.type==_CPLX && prec<=13){
      complex_long_double Z(tmp._CPLXptr->_DOUBLE_val,(tmp._CPLXptr+1)->_DOUBLE_val);
      if (z._DOUBLE_val>30){ 
	// expansion at infinity, order 30, error 1e-13
	complex_long_double ei=1.0,pi=1.0;
	for (long_double n=1;n<=30;n++){
	  pi = (n*pi)/Z;
	  ei += pi;
	}
	ei=std::exp(Z)/Z*ei;
	gen eig=gen(double(ei.real()),double(ei.imag()));
	// if (is_positive(-re(tmp,contextptr),contextptr))
	  {
	  gen pi=im(tmp,contextptr);
	  if (is_strictly_positive(pi,contextptr))
	    return eig+cst_pi*cst_i;
	  if (is_strictly_positive(-pi,contextptr))
	    return eig-cst_pi*cst_i;
	  }
	return eig;
      }
      else { 
	// use expansion at 0, 
	// cancellation for negative re(Z) but already computed with cont frac
	complex_long_double ei=0,pi=1;
	for (long_double n=1;n<=70;++n){
	  pi = pi*Z/n;
	  ei += pi/n;
	}
	if (is_zero(im(tmp,contextptr)) && is_positive(-re(tmp,contextptr),contextptr))
	  ei=ei+std::log(-Z);
	else
	  ei=ei+std::log(Z);
	ei += 0.577215664901532860610L;
	gen eig=gen(double(ei.real()),double(ei.imag()));
	return eig;
      }      
    }
#endif
    // find number of digits that must be added to prec
    // n^n/n! equivalent to e^n*sqrt(2*pi*n)
    // Note that Ei(z) might be as small as exp(-z) for relative prec
    int newprec,nbitsz=int(z._DOUBLE_val/std::log(2.)),prec2=int(prec*std::log(10.0)/std::log(2.0)+.5);
    if (nbitsz>prec2){ 
      // use asymptotic expansion at z=inf
      gen ei=1,pi=1,eps=accurate_evalf(pow(10,-prec,contextptr),prec2)/2;
      z = accurate_evalf(args,prec2);
      for (int n=1;;++n){
	if (is_greater(eps,abs(pi,contextptr),contextptr))
	  break;
	pi = (n*pi)/z;
	ei += pi;
      }
      ei=exp(z,contextptr)/z*ei;
      if (is_positive(-re(z,contextptr),contextptr)){
	pi=im(z,contextptr);
	if (is_strictly_positive(pi,contextptr))
	  return ei+cst_pi*cst_i;
	if (is_strictly_positive(-pi,contextptr))
	  return ei-cst_pi*cst_i;
      }
      return ei;
    }
    prec2 += nbitsz;
    // use series expansion at z=0
    if (z._DOUBLE_val>1)
      newprec = prec2+nbitsz+int(std::log(z._DOUBLE_val)/2)+2;
    else
      newprec = prec2+2;
    gen ei=0,pi=1,eps=accurate_evalf(pow(10,-2*prec,contextptr)*exp(-2*abs(z,contextptr),contextptr),newprec)/4,r,i;
    z = accurate_evalf(args,newprec);
    for (int n=1;;n++){
      pi = accurate_evalf(pi*z/n,newprec);
      reim(pi,r,i,contextptr);
      if (is_greater(eps,r*r+i*i,contextptr))
	break;
      ei = accurate_evalf(ei+pi/n,newprec);
    }
    ei = accurate_evalf(ei,newprec);
    if (is_zero(im(z,contextptr)) && is_positive(-re(z,contextptr),contextptr))
      ei=ei+ln(-z,contextptr);
    else
      ei=ei+ln(z,contextptr);
    r = re(ei,contextptr);
    r = r+accurate_evalf(m_gamma(newprec),newprec);
    r = accurate_evalf(r,prec2-nbitsz);
    i = accurate_evalf(im(ei,contextptr),prec2-nbitsz);
    ei = r+cst_i*i;
    return ei;
  }
  gen Ei(const gen & args,int n,GIAC_CONTEXT){
    if (n==1)
      return -Ei(-args,contextptr);
    if (n<2)
      return gendimerr(contextptr);
    if (is_zero(args,contextptr)){
      if (n==1)
	return plus_inf;
      return plus_one/gen(n-1);
    }
    if (args==plus_inf)
      return 0;
    if (args==minus_inf)
      return minus_inf;
    if (is_inf(args)|| is_undef(args))
      return undef;
    return (exp(-args,contextptr)-args*Ei(args,n-1,contextptr))/gen(n-1);
  }
  gen _Ei(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_FLOAT_)
      return evalf2bcd(_Ei(get_double(args._FLOAT_val),contextptr),1,contextptr);
    if (args.type!=_VECT){
      return Ei(args,contextptr);
    }
    if ( args._VECTptr->size()!=2 ){
      return symbolic(at_Ei,args);
    }
    gen x(args._VECTptr->front()),n(args._VECTptr->back());
    if (n.type==_REAL)
      n=n.evalf_double(1,contextptr);
    if (n.type==_DOUBLE_)
      n=int(n._DOUBLE_val);
    if (n.type!=_INT_)
      return gensizeerr(contextptr);
    if (n==1)
      *logptr(contextptr) << gettext("Warning, Ei(x,1) is defined as -Ei(-x), not as Ei(x)") << endl;
    return Ei(x,n.val,contextptr);
  }
  static const char _Ei_s []="Ei";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor (__Ei,&_Ei,(unsigned long)&D_at_Eiunary_function_ptr,&taylor_Ei,_Ei_s);
#else
  static define_unary_function_eval_taylor (__Ei,&_Ei,D_at_Ei,&taylor_Ei,_Ei_s);
#endif
  define_unary_function_ptr5( at_Ei ,alias_at_Ei,&__Ei,0,true);

  static gen d_Ei0(const gen & args,GIAC_CONTEXT){
    return (exp(args,contextptr)-1)/args;
  }
  define_partial_derivative_onearg_genop( D_at_Ei0," D_at_Ei0",&d_Ei0);
  static gen taylor_Ei0(const gen & lim_point,const int ordre,const unary_function_ptr & f, int direction,gen & shift_coeff,GIAC_CONTEXT){
    if (ordre<0)
      return 0;
    if (!is_zero(lim_point,contextptr))
      return taylor(lim_point,ordre,f,0,shift_coeff,contextptr);
    shift_coeff=1;
    // sum( 1/(k)/(k)! * x^(k) )
    gen tmp(1);
    vecteur v;
    for (int n=0;n<=ordre;){
      n++;
      tmp=n*tmp;
      v.push_back(inv(n*tmp,contextptr));
    }
    v.push_back(undef);
    return v;
  }
  gen _Ei0(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_zero(args,contextptr))
      return 0;
    if (is_undef(args))
      return args;
    if (is_inf(args))
      return minus_inf;
    if (args.type!=_DOUBLE_ && args.type!=_REAL && args.type!=_CPLX)
      return symbolic(at_Ei0,args);
    gen si,ci;
    if (!sici(args,si,ci,decimal_digits(contextptr),2,contextptr))
      return gensizeerr(contextptr);
    return ci-evalf(cst_euler_gamma,1,contextptr)-ln(args,contextptr);
  }
  static const char _Ei0_s []="Ei0";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval_taylor( __Ei0,&_Ei0,(unsigned long)&D_at_Ei0unary_function_ptr,&taylor_Ei0,_Ei0_s);
#else
  static define_unary_function_eval_taylor( __Ei0,&_Ei0,D_at_Ei0,&taylor_Ei0,_Ei0_s);
#endif
  define_unary_function_ptr5( at_Ei0 ,alias_at_Ei0,&__Ei0,0,true); /* FIXME should not registered */


  gen _Dirac(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args.subtype!=_SEQ__VECT)
      return apply(args,_Dirac,contextptr);
    gen f=args;
    if (args.type==_VECT && args.subtype==_SEQ__VECT && !args._VECTptr->empty())
      f=args._VECTptr->front();
    if (is_zero(f,contextptr))
      return unsigned_inf;
    if (f.type<_IDNT)
      return 0;
    return symbolic(at_Dirac,args);
  }
  static gen d_Dirac(const gen & args,GIAC_CONTEXT){
    vecteur v(gen2vecteur(args));
    if (v.size()==1)
      v.push_back(0);
    if (v.size()!=2 || v.back().type!=_INT_)
      return gendimerr(contextptr);
    return _Dirac(gen(makevecteur(v.front(),v.back().val+1),_SEQ__VECT),contextptr);
  }
  define_partial_derivative_onearg_genop( D_at_Dirac," D_at_Dirac",&d_Dirac);
  static const char _Dirac_s []="Dirac";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3 (__Dirac,&_Dirac,(unsigned long)&D_at_Diracunary_function_ptr,_Dirac_s);
#else
  static define_unary_function_eval3 (__Dirac,&_Dirac,D_at_Dirac,_Dirac_s);
#endif
  define_unary_function_ptr5( at_Dirac ,alias_at_Dirac,&__Dirac,0,true);
  define_partial_derivative_onearg_genop( D_Heaviside," D_Heaviside",&_Dirac);

  gen _Heaviside(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT)
      return apply(args,_Heaviside,contextptr);
    if (is_zero(args,contextptr))
      return plus_one;
    gen tmp=_sign(args,contextptr);
    if (tmp.type<=_DOUBLE_)
      return (tmp+1)/2;
    return symbolic(at_Heaviside,args);
  }
  static const char _Heaviside_s []="Heaviside";
#ifdef GIAC_HAS_STO_38
  static define_unary_function_eval3 (__Heaviside,&_Heaviside,(unsigned long)&D_Heavisideunary_function_ptr,_Heaviside_s);
#else
  static define_unary_function_eval3 (__Heaviside,&_Heaviside,D_Heaviside,_Heaviside_s);
#endif
  define_unary_function_ptr5( at_Heaviside ,alias_at_Heaviside,&__Heaviside,0,true);

  const char _sum_s []="sum";
  static define_unary_function_eval_quoted (__sum,&_sum,_sum_s);
  define_unary_function_ptr5( at_sum ,alias_at_sum,&__sum,_QUOTE_ARGUMENTS,true);

  // vector<unary_function_ptr > solve_fcns_v(solve_fcns,solve_fcns+sizeof(solve_fcns)/sizeof(unary_function_ptr));

  // #ifndef GNUWINCE
  // #ifndef WIN32
  // #endif
#if defined(GIAC_GENERIC_CONSTANTS) // || (defined(VISUALC) && !defined(RTOS_THREADX)) || defined( __x86_64__)
  const gen zero(0);
  const gen plus_one(1);
  const gen plus_two(2);
  const gen plus_three(3);
  const gen minus_one(-1);
  const gen cst_i(0,1);
#else
  const define_alias_gen(alias_zero,_INT_,0,0);
  const define_alias_gen(alias_plus_one,_INT_,0,1);
  const gen & zero = *(const gen *) & alias_zero;
  const gen & plus_one = *(const gen *) & alias_plus_one;
  define_alias_ref_complex(cst_i_ref,_INT_,0,0,_INT_,0,1);
  const define_alias_gen(alias_cst_i,_CPLX,0,&cst_i_ref);
  const gen & cst_i = *(const gen *) & alias_cst_i;

  const define_alias_gen(alias_minus_one,_INT_,0,-1);
  const gen & minus_one = *(const gen *) & alias_minus_one;
  const define_alias_gen(alias_plus_two,_INT_,0,2);
  const gen & plus_two = *(const gen *) & alias_plus_two;
  const define_alias_gen(alias_plus_three,_INT_,0,3);
  const gen & plus_three = *(const gen *) & alias_plus_three;
#endif

  const double rad2deg_d(180/M_PI);
  const double deg2rad_d(M_PI/180);
#if defined(DOUBLEVAL) || defined(GIAC_GENERIC_CONSTANTS) || defined(VISUALC) || defined(__x86_64__)
  static const gen rad2deg_g_(rad2deg_d);
  const gen & rad2deg_g=rad2deg_g_;
  static const gen deg2rad_g_(deg2rad_d);
  const gen & deg2rad_g=deg2rad_g_;
#else
    // Warning this does not work on ia64 with -O2
  const define_alias_gen(alias_rad2deg_g,_DOUBLE_, (*(longlong *)&rad2deg_d) >> 8,(*(longlong *)&rad2deg_d)>>16);
  const gen & rad2deg_g = *(const gen*) & alias_rad2deg_g;
  const define_alias_gen(alias_deg2rad_g,_DOUBLE_, (*(longlong *)&deg2rad_d) >> 8,(*(longlong *)&deg2rad_d)>>16);
  const gen & deg2rad_g = *(const gen*) & alias_deg2rad_g;
#endif

#if defined(GIAC_GENERIC_CONSTANTS) // || (defined(VISUALC) && !defined(RTOS_THREADX)) || defined(__x86_64__)
  gen cst_two_pi(symbolic(at_prod,makevecteur(plus_two,_IDNT_pi())));
  gen cst_pi_over_2(_FRAC2_SYMB(_IDNT_pi(),2));
  gen plus_inf(symbolic(at_plus,_IDNT_infinity()));
  gen minus_inf(symbolic(at_neg,_IDNT_infinity()));
  gen plus_one_half(fraction(1,2));
  gen minus_one_half(symbolic(at_neg,symb_inv(2)));
  gen plus_sqrt3(symbolic(at_pow,gen(makevecteur(3,plus_one_half),_SEQ__VECT)));
  gen plus_sqrt2(symbolic(at_pow,gen(makevecteur(2,plus_one_half),_SEQ__VECT)));
  gen plus_sqrt6(symbolic(at_pow,gen(makevecteur(6,plus_one_half),_SEQ__VECT)));
  gen minus_sqrt6(symbolic(at_neg,plus_sqrt6));
  gen minus_sqrt3(symbolic(at_neg,plus_sqrt3));
  gen minus_sqrt2(symbolic(at_neg,plus_sqrt2));
  gen minus_sqrt3_2(_FRAC2_SYMB(minus_sqrt3,2));
  gen minus_sqrt2_2(_FRAC2_SYMB(minus_sqrt2,2));
  gen minus_sqrt3_3(_FRAC2_SYMB(minus_sqrt3,3));
  gen plus_sqrt3_2(_FRAC2_SYMB(plus_sqrt3,2));
  gen plus_sqrt2_2(_FRAC2_SYMB(plus_sqrt2,2));
  gen plus_sqrt3_3(_FRAC2_SYMB(plus_sqrt3,3));
  gen cos_pi_12(_FRAC2_SYMB(
			    symbolic(at_plus,gen(makevecteur(plus_sqrt6,plus_sqrt2),_SEQ__VECT)),
			    4));
  gen minus_cos_pi_12(_FRAC2_SYMB(
			    symbolic(at_plus,gen(makevecteur(minus_sqrt6,minus_sqrt2),_SEQ__VECT)),
			    4));
  gen sin_pi_12(_FRAC2_SYMB(
			    symbolic(at_plus,gen(makevecteur(plus_sqrt6,minus_sqrt2),_SEQ__VECT)),
			    4));
  gen minus_sin_pi_12(_FRAC2_SYMB(
			    symbolic(at_plus,gen(makevecteur(plus_sqrt2,minus_sqrt6),_SEQ__VECT)),
			    4));
  gen tan_pi_12(symbolic(at_plus,gen(makevecteur(2,minus_sqrt3),_SEQ__VECT)));
  gen tan_5pi_12(symbolic(at_plus,gen(makevecteur(2,plus_sqrt3),_SEQ__VECT)));
  gen minus_tan_pi_12(symbolic(at_neg,tan_pi_12));
  gen minus_tan_5pi_12(symbolic(at_neg,tan_5pi_12));
  gen rad2deg_e(_FRAC2_SYMB(180,_IDNT_pi()));
  gen deg2rad_e(_FRAC2_SYMB(_IDNT_pi(),180));
  // 0 = -pi, 12=0, 24=pi
  const gen * const table_cos[trig_deno+1]={
    &minus_one,&minus_cos_pi_12,&minus_sqrt3_2,&minus_sqrt2_2,&minus_one_half,&minus_sin_pi_12,
    &zero,&sin_pi_12,&plus_one_half,&plus_sqrt2_2,&plus_sqrt3_2,&cos_pi_12,
    &plus_one,&cos_pi_12,&plus_sqrt3_2,&plus_sqrt2_2,&plus_one_half,&sin_pi_12,
    &zero,&minus_sin_pi_12,&minus_one_half,&minus_sqrt2_2,&minus_sqrt3_2,&minus_cos_pi_12,
    &minus_one
  };
  const gen * const table_tan[trig_deno/2+1]={
    &zero,&tan_pi_12,&plus_sqrt3_3,&plus_one,&plus_sqrt3,&tan_5pi_12,
    &unsigned_inf,&minus_tan_5pi_12,&minus_sqrt3,&minus_one,&minus_sqrt3_3,&minus_tan_pi_12,
    &zero
  };


#else 
  const define_alias_gen(alias_plus_four,_INT_,0,4);
  const gen & gen_plus_four = *(const gen *)&alias_plus_four;
  const define_alias_gen(alias_plus_six,_INT_,0,6);
  const gen & gen_plus_six = *(const gen *)&alias_plus_six;
  const define_alias_gen(alias_180,_INT_,0,180);
  const gen & gen_180 = *(const gen *)&alias_180;

  const define_tab2_alias_gen(alias_cst_two_pi_tab,_INT_,0,2,_IDNT,0,&ref_pi);
  const define_alias_ref_vecteur2(cst_two_pi_refv,alias_cst_two_pi_tab);

  // static const define_alias_gen(cst_two_pi_V,_VECT,_SEQ__VECT,&cst_two_pi_refv);
  const define_alias_ref_symbolic( cst_two_pi_symb ,alias_at_prod,_VECT,_SEQ__VECT,&cst_two_pi_refv);
  const define_alias_gen(alias_cst_two_pi,_SYMB,0,&cst_two_pi_symb);
  const gen & cst_two_pi = *(const gen *)&alias_cst_two_pi;

  const define_alias_ref_symbolic( inv_2_symb,alias_at_inv,_INT_,0,2);
  const define_alias_gen(alias_inv_2,_SYMB,0,&inv_2_symb);
  const gen & gen_inv_2 = *(const gen *)&alias_inv_2;

  const define_alias_ref_symbolic( inv_3_symb,alias_at_inv,_INT_,0,3)
  const define_alias_gen(alias_inv_3,_SYMB,0,&inv_3_symb);
  const gen & gen_inv_3 = *(const gen *)&alias_inv_3;

  const define_alias_ref_symbolic( inv_4_symb,alias_at_inv,_INT_,0,4)
  const define_alias_gen(alias_inv_4,_SYMB,0,&inv_4_symb);
  const gen & gen_inv_4 = *(const gen *)&alias_inv_4;

  const define_tab2_alias_gen(alias_cst_pi_over_2_tab,_IDNT,0,&ref_pi,_SYMB,0,&inv_2_symb);
  const define_alias_ref_vecteur2(cst_pi_over_2_refv,alias_cst_pi_over_2_tab);

  const define_alias_ref_symbolic( cst_pi_over_2_symb ,alias_at_prod,_VECT,_SEQ__VECT,&cst_pi_over_2_refv);
  const define_alias_gen(alias_cst_pi_over_2,_SYMB,0,&cst_pi_over_2_symb);
  const gen & cst_pi_over_2 = *(const gen *)&alias_cst_pi_over_2;

  const define_alias_ref_symbolic( plus_inf_symb ,alias_at_plus,_IDNT,0,&ref_infinity);
  const define_alias_gen(alias_plus_inf,_SYMB,0,&plus_inf_symb);
  const gen & plus_inf = *(const gen *)&alias_plus_inf;
  const define_alias_ref_symbolic( minus_inf_symb ,alias_at_neg,_IDNT,0,&ref_infinity);
  const define_alias_gen(alias_minus_inf,_SYMB,0,&minus_inf_symb);
  const gen & minus_inf = *(const gen *)&alias_minus_inf;

  const define_alias_ref_fraction(plus_one_half_ref,_INT_,0,1,_INT_,0,2);
  const define_alias_gen(alias_plus_one_half,_FRAC,0,&plus_one_half_ref);
  const gen & plus_one_half = *(const gen *)&alias_plus_one_half;
  const define_alias_ref_symbolic( minus_one_half_symb ,alias_at_neg,_SYMB,0,&inv_2_symb);
  const define_alias_gen(alias_minus_one_half,_SYMB,0,&minus_one_half_symb);
  const gen & minus_one_half = *(const gen *)&alias_minus_one_half;
  
  const define_tab2_alias_gen(alias_plus_sqrt3_tab,_INT_,0,3,_FRAC,0,&plus_one_half_ref);
  const define_alias_ref_vecteur2(plus_sqrt3_refv,alias_plus_sqrt3_tab);

  const define_alias_ref_symbolic( plus_sqrt3_symb ,alias_at_pow,_VECT,_SEQ__VECT,&plus_sqrt3_refv);
  const define_alias_gen(alias_plus_sqrt3,_SYMB,0,&plus_sqrt3_symb);
  const gen & plus_sqrt3 = *(const gen *)&alias_plus_sqrt3;

  const define_tab2_alias_gen(alias_plus_sqrt2_tab,_INT_,0,2,_FRAC,0,&plus_one_half_ref);
  const define_alias_ref_vecteur2(plus_sqrt2_refv,alias_plus_sqrt2_tab);
  const define_alias_ref_symbolic( plus_sqrt2_symb ,alias_at_pow,_VECT,_SEQ__VECT,&plus_sqrt2_refv);
  const define_alias_gen(alias_plus_sqrt2,_SYMB,0,&plus_sqrt2_symb);
  const gen & plus_sqrt2 = *(const gen *)&alias_plus_sqrt2;

  const define_tab2_alias_gen(alias_plus_sqrt6_tab,_INT_,0,6,_FRAC,0,&plus_one_half_ref);
  const define_alias_ref_vecteur2(plus_sqrt6_refv,alias_plus_sqrt6_tab);
  const define_alias_ref_symbolic( plus_sqrt6_symb ,alias_at_pow,_VECT,_SEQ__VECT,&plus_sqrt6_refv);
  const define_alias_gen(alias_plus_sqrt6,_SYMB,0,&plus_sqrt6_symb);
  const gen & plus_sqrt6 = *(const gen *)&alias_plus_sqrt6;

  const define_alias_ref_symbolic( minus_sqrt2_symb ,alias_at_neg,_SYMB,0,&plus_sqrt2_symb);
  const define_alias_gen(alias_minus_sqrt2,_SYMB,0,&minus_sqrt2_symb);
  const gen & minus_sqrt2 = *(const gen *)&alias_minus_sqrt2;

  const define_alias_ref_symbolic( minus_sqrt3_symb ,alias_at_neg,_SYMB,0,&plus_sqrt3_symb);
  const define_alias_gen(alias_minus_sqrt3,_SYMB,0,&minus_sqrt3_symb);
  const gen & minus_sqrt3 = *(const gen *)&alias_minus_sqrt3;

  const define_alias_ref_symbolic( minus_sqrt6_symb ,alias_at_neg,_SYMB,0,&plus_sqrt6_symb);
  const define_alias_gen(alias_minus_sqrt6,_SYMB,0,&minus_sqrt6_symb);
  const gen & minus_sqrt6 = *(const gen *)&alias_minus_sqrt6;

  const define_tab2_alias_gen(alias_minus_sqrt3_2_tab,_SYMB,0,&minus_sqrt3_symb,_SYMB,0,&inv_2_symb);
  const define_alias_ref_vecteur2(minus_sqrt3_2_refv,alias_minus_sqrt3_2_tab);
  const define_alias_ref_symbolic( minus_sqrt3_2_symb ,alias_at_prod,_VECT,_SEQ__VECT,&minus_sqrt3_2_refv);
  const define_alias_gen(alias_minus_sqrt3_2,_SYMB,0,&minus_sqrt3_2_symb);
  const gen & minus_sqrt3_2 = *(const gen *)&alias_minus_sqrt3_2;

  const define_tab2_alias_gen(alias_minus_sqrt2_2_tab,_SYMB,0,&minus_sqrt2_symb,_SYMB,0,&inv_2_symb);
  const define_alias_ref_vecteur2(minus_sqrt2_2_refv,alias_minus_sqrt2_2_tab);
  const define_alias_ref_symbolic( minus_sqrt2_2_symb ,alias_at_prod,_VECT,_SEQ__VECT,&minus_sqrt2_2_refv);
  const define_alias_gen(alias_minus_sqrt2_2,_SYMB,0,&minus_sqrt2_2_symb);
  const gen & minus_sqrt2_2 = *(const gen *)&alias_minus_sqrt2_2;

  const define_tab2_alias_gen(alias_minus_sqrt3_3_tab,_SYMB,0,&minus_sqrt3_symb,_SYMB,0,&inv_3_symb);
  const define_alias_ref_vecteur2(minus_sqrt3_3_refv,alias_minus_sqrt3_3_tab);
  const define_alias_ref_symbolic( minus_sqrt3_3_symb ,alias_at_prod,_VECT,_SEQ__VECT,&minus_sqrt3_3_refv);
  const define_alias_gen(alias_minus_sqrt3_3,_SYMB,0,&minus_sqrt3_3_symb);
  const gen & minus_sqrt3_3 = *(const gen *)&alias_minus_sqrt3_3;

  const define_tab2_alias_gen(alias_plus_sqrt3_2_tab,_SYMB,0,&plus_sqrt3_symb,_SYMB,0,&inv_2_symb);
  const define_alias_ref_vecteur2(plus_sqrt3_2_refv,alias_plus_sqrt3_2_tab);
  const define_alias_ref_symbolic( plus_sqrt3_2_symb ,alias_at_prod,_VECT,_SEQ__VECT,&plus_sqrt3_2_refv);
  const define_alias_gen(alias_plus_sqrt3_2,_SYMB,0,&plus_sqrt3_2_symb);
  const gen & plus_sqrt3_2 = *(const gen *)&alias_plus_sqrt3_2;

  const define_tab2_alias_gen(alias_plus_sqrt2_2_tab,_SYMB,0,&plus_sqrt2_symb,_SYMB,0,&inv_2_symb);
  const define_alias_ref_vecteur2(plus_sqrt2_2_refv,alias_plus_sqrt2_2_tab);
  const define_alias_ref_symbolic( plus_sqrt2_2_symb ,alias_at_prod,_VECT,_SEQ__VECT,&plus_sqrt2_2_refv);
  const define_alias_gen(alias_plus_sqrt2_2,_SYMB,0,&plus_sqrt2_2_symb);
  const gen & plus_sqrt2_2 = *(const gen *)&alias_plus_sqrt2_2;

  const define_tab2_alias_gen(alias_plus_sqrt3_3_tab,_SYMB,0,&plus_sqrt3_symb,_SYMB,0,&inv_3_symb);
  const define_alias_ref_vecteur2(plus_sqrt3_3_refv,alias_plus_sqrt3_3_tab);
  const define_alias_ref_symbolic( plus_sqrt3_3_symb ,alias_at_prod,_VECT,_SEQ__VECT,&plus_sqrt3_3_refv);
  const define_alias_gen(alias_plus_sqrt3_3,_SYMB,0,&plus_sqrt3_3_symb);
  const gen & plus_sqrt3_3 = *(const gen *)&alias_plus_sqrt3_3;

  const define_tab2_alias_gen(alias_cos_pi_12_4_tab,_SYMB,0,&plus_sqrt6_symb,_SYMB,0,&plus_sqrt2_symb);
  const define_alias_ref_vecteur2(cos_pi_12_4_refv,alias_cos_pi_12_4_tab);
  const define_alias_ref_symbolic( cos_pi_12_4_symb ,alias_at_plus,_VECT,_SEQ__VECT,&cos_pi_12_4_refv);

  const define_tab2_alias_gen(alias_cos_pi_12_tab,_SYMB,0,&cos_pi_12_4_symb,_SYMB,0,&inv_4_symb);
  const define_alias_ref_vecteur2(cos_pi_12_refv,alias_cos_pi_12_tab);
  const define_alias_ref_symbolic( cos_pi_12_symb ,alias_at_prod,_VECT,_SEQ__VECT,&cos_pi_12_refv);
  const define_alias_gen(alias_cos_pi_12,_SYMB,0,&cos_pi_12_symb);
  const gen & cos_pi_12 = *(const gen *)&alias_cos_pi_12;

  const define_tab2_alias_gen(alias_minus_cos_pi_12_4_tab,_SYMB,0,&minus_sqrt6_symb,_SYMB,0,&minus_sqrt2_symb);
  const define_alias_ref_vecteur2(minus_cos_pi_12_4_refv,alias_minus_cos_pi_12_4_tab);
  const define_alias_ref_symbolic( minus_cos_pi_12_4_symb ,alias_at_plus,_VECT,_SEQ__VECT,&minus_cos_pi_12_4_refv);
  const define_tab2_alias_gen(alias_minus_cos_pi_12_tab,_SYMB,0,&minus_cos_pi_12_4_symb,_SYMB,0,&inv_4_symb);
  const define_alias_ref_vecteur2(minus_cos_pi_12_refv,alias_minus_cos_pi_12_tab);
  const define_alias_ref_symbolic( minus_cos_pi_12_symb ,alias_at_prod,_VECT,_SEQ__VECT,&minus_cos_pi_12_refv);
  const define_alias_gen(alias_minus_cos_pi_12,_SYMB,0,&minus_cos_pi_12_symb);
  const gen & minus_cos_pi_12 = *(const gen *)&alias_minus_cos_pi_12;

  const define_tab2_alias_gen(alias_sin_pi_12_4_tab,_SYMB,0,&plus_sqrt6_symb,_SYMB,0,&minus_sqrt2_symb);
  const define_alias_ref_vecteur2(sin_pi_12_4_refv,alias_sin_pi_12_4_tab);
  const define_alias_ref_symbolic( sin_pi_12_4_symb ,alias_at_plus,_VECT,_SEQ__VECT,&sin_pi_12_4_refv);
  const define_tab2_alias_gen(alias_sin_pi_12_tab,_SYMB,0,&sin_pi_12_4_symb,_SYMB,0,&inv_4_symb);
  const define_alias_ref_vecteur2(sin_pi_12_refv,alias_sin_pi_12_tab);
  const define_alias_ref_symbolic( sin_pi_12_symb ,alias_at_prod,_VECT,_SEQ__VECT,&sin_pi_12_refv);
  const define_alias_gen(alias_sin_pi_12,_SYMB,0,&sin_pi_12_symb);
  const gen & sin_pi_12 = *(const gen *)&alias_sin_pi_12;

  const define_tab2_alias_gen(alias_minus_sin_pi_12_4_tab,_SYMB,0,&plus_sqrt2_symb,_SYMB,0,&minus_sqrt6_symb);
  const define_alias_ref_vecteur2(minus_sin_pi_12_4_refv,alias_minus_sin_pi_12_4_tab);
  const define_alias_ref_symbolic( minus_sin_pi_12_4_symb ,alias_at_plus,_VECT,_SEQ__VECT,&minus_sin_pi_12_4_refv);
  const define_tab2_alias_gen(alias_minus_sin_pi_12_tab,_SYMB,0,&minus_sin_pi_12_4_symb,_SYMB,0,&inv_4_symb);
  const define_alias_ref_vecteur2(minus_sin_pi_12_refv,alias_minus_sin_pi_12_tab);
  const define_alias_ref_symbolic( minus_sin_pi_12_symb ,alias_at_prod,_VECT,_SEQ__VECT,&minus_sin_pi_12_refv);
  const define_alias_gen(alias_minus_sin_pi_12,_SYMB,0,&minus_sin_pi_12_symb);
  const gen & minus_sin_pi_12 = *(const gen *)&alias_minus_sin_pi_12;

  const define_tab2_alias_gen(alias_tan_pi_12_tab,_INT_,0,2,_SYMB,0,&minus_sqrt3_symb);
  const define_alias_ref_vecteur2(tan_pi_12_refv,alias_tan_pi_12_tab);
  const define_alias_ref_symbolic( tan_pi_12_symb ,alias_at_plus,_VECT,_SEQ__VECT,&tan_pi_12_refv);
  const define_alias_gen(alias_tan_pi_12,_SYMB,0,&tan_pi_12_symb);
  const gen & tan_pi_12 = *(const gen *)&alias_tan_pi_12;

  const define_tab2_alias_gen(alias_tan_5pi_12_tab,_INT_,0,2,_SYMB,0,&plus_sqrt3_symb);
  const define_alias_ref_vecteur2(tan_5pi_12_refv,alias_tan_5pi_12_tab);
  const define_alias_ref_symbolic( tan_5pi_12_symb ,alias_at_plus,_VECT,_SEQ__VECT,&tan_5pi_12_refv);
  const define_alias_gen(alias_tan_5pi_12,_SYMB,0,&tan_5pi_12_symb);
  const gen & tan_5pi_12 = *(const gen *)&alias_tan_5pi_12;

  const define_alias_ref_symbolic( minus_tan_pi_12_symb ,alias_at_neg,_SYMB,0,&tan_pi_12_symb);
  const define_alias_gen(alias_minus_tan_pi_12,_SYMB,0,&minus_tan_pi_12_symb);
  const gen & minus_tan_pi_12 = *(const gen *)&alias_minus_tan_pi_12;

  const define_alias_ref_symbolic( minus_tan_5pi_12_symb ,alias_at_neg,_SYMB,0,&tan_5pi_12_symb);
  const define_alias_gen(alias_minus_tan_5pi_12,_SYMB,0,&minus_tan_5pi_12_symb);
  const gen & minus_tan_5pi_12 = *(const gen *)&alias_minus_tan_5pi_12;

  const define_alias_ref_symbolic( inv_pi_symb,alias_at_inv,_IDNT,0,&ref_pi);
  const define_alias_gen(alias_inv_pi,_SYMB,0,&inv_pi_symb);
  const gen & cst_inv_pi = * (const gen *) &alias_inv_pi;

  const define_alias_ref_symbolic( inv_180_symb,alias_at_inv,_INT_,0,180);
  const define_alias_gen(alias_inv_180,_SYMB,0,&inv_180_symb);
  const gen & cst_inv_180 = * (const gen *) &alias_inv_180;

  const define_tab2_alias_gen(alias_rad2deg_e_tab,_INT_,0,180,_SYMB,0,&inv_pi_symb);
  const define_alias_ref_vecteur2(rad2deg_e_refv,alias_rad2deg_e_tab);
  const define_alias_ref_symbolic( rad2deg_e_symb ,(unsigned long)&_prod,_VECT,_SEQ__VECT,&rad2deg_e_refv);
  const define_alias_gen(alias_rad2deg_e,_SYMB,0,&rad2deg_e_symb);
  const gen & rad2deg_e = *(const gen *)&alias_rad2deg_e;

  const define_tab2_alias_gen(alias_deg2rad_e_tab,_IDNT,0,&ref_pi,_SYMB,0,&inv_180_symb);
  const define_alias_ref_vecteur2(deg2rad_e_refv,alias_deg2rad_e_tab);
  const define_alias_ref_symbolic( deg2rad_e_symb ,(unsigned long)&__prod,_VECT,_SEQ__VECT,&deg2rad_e_refv);
  const define_alias_gen(alias_deg2rad_e,_SYMB,0,&deg2rad_e_symb);
  const gen & deg2rad_e = *(const gen *)&alias_deg2rad_e;


  // 0 = -pi, 12=0, 24=pi
  static const alias_gen * const table_cos_alias[trig_deno+1]={
    &alias_minus_one,&alias_minus_cos_pi_12,&alias_minus_sqrt3_2,&alias_minus_sqrt2_2,&alias_minus_one_half,&alias_minus_sin_pi_12,
    &alias_zero,&alias_sin_pi_12,&alias_plus_one_half,&alias_plus_sqrt2_2,&alias_plus_sqrt3_2,&alias_cos_pi_12,
    &alias_plus_one,&alias_cos_pi_12,&alias_plus_sqrt3_2,&alias_plus_sqrt2_2,&alias_plus_one_half,&alias_sin_pi_12,
    &alias_zero,&alias_minus_sin_pi_12,&alias_minus_one_half,&alias_minus_sqrt2_2,&alias_minus_sqrt3_2,&alias_minus_cos_pi_12,
    &alias_minus_one
  };
  const gen * const * table_cos= (const gen **) table_cos_alias;
  static const alias_gen * const table_tan_alias[trig_deno/2+1]={
    &alias_zero,&alias_tan_pi_12,&alias_plus_sqrt3_3,&alias_plus_one,&alias_plus_sqrt3,&alias_tan_5pi_12,
    &alias_unsigned_inf,&alias_minus_tan_5pi_12,&alias_minus_sqrt3,&alias_minus_one,&alias_minus_sqrt3_3,&alias_minus_tan_pi_12,
    &alias_zero
  };
  const gen * const * table_tan = (const gen **) table_tan_alias;

#endif // GIAC_GENERIC_CONSTANTS

  const alias_type reim_op_alias[]={(alias_type)&__inv,(alias_type)&__exp,(alias_type)&__cos,(alias_type)&__sin,(alias_type)&__tan,(alias_type)&__cosh,(alias_type)&__sinh,(alias_type)&__tanh,(alias_type)&__atan,(alias_type)&__lnGamma_minus,(alias_type)&__Gamma,(alias_type)&__Psi_minus_ln,(alias_type)&__Psi,(alias_type)&__Zeta,(alias_type)&__Eta,(alias_type)&__sign,(alias_type)&__erf,0};
  const unary_function_ptr * const reim_op=(const unary_function_ptr * const)reim_op_alias;
  // for subst.cc
  const alias_type sincostan_tab_alias[]={(alias_type)&__sin,(alias_type)&__cos,(alias_type)&__tan,0};
  const unary_function_ptr * const sincostan_tab=(const unary_function_ptr * const) sincostan_tab_alias;

  const alias_type asinacosatan_tab_alias[] = {(alias_type)&__asin,(alias_type)&__acos,(alias_type)&__atan,0};
  const unary_function_ptr * const asinacosatan_tab=(const unary_function_ptr * const) asinacosatan_tab_alias;

  const alias_type sinhcoshtanh_tab_alias[]={alias_at_sinh,alias_at_cosh,alias_at_tanh,0};
  const unary_function_ptr * const sinhcoshtanh_tab=(const unary_function_ptr * const)sinhcoshtanh_tab_alias;

  const alias_type sinhcoshtanhinv_tab_alias[]={(alias_type)&__sinh,(alias_type)&__cosh,(alias_type)&__tanh,(alias_type)&__inv,0};
  const unary_function_ptr * const sinhcoshtanhinv_tab=(const unary_function_ptr * const)sinhcoshtanhinv_tab_alias;

  const alias_type sincostansinhcoshtanh_tab_alias[]={(alias_type)&__sin,(alias_type)&__cos,(alias_type)&__tan,(alias_type)&__sinh,(alias_type)&__cosh,(alias_type)&__tanh,0};
  const unary_function_ptr * const sincostansinhcoshtanh_tab=(const unary_function_ptr * const)sincostansinhcoshtanh_tab_alias;

  // vector<unary_function_ptr> sincostan_v(sincostan_tab,sincostan_tab+3);
  // vector<unary_function_ptr> asinacosatan_v(asinacosatan_tab,asinacosatan_tab+3);
  // vector<unary_function_ptr> sinhcoshtanh_v(sinhcoshtanh_tab,sinhcoshtanh_tab+3);
  // vector <unary_function_ptr> sincostansinhcoshtanh_v(merge(sincostan_v,sinhcoshtanh_v));
  const alias_type sign_floor_ceil_round_tab_alias[]={(alias_type)&__sign,(alias_type)&__floor,(alias_type)&__ceil,(alias_type)&__round,(alias_type)&__sum,0};
  const unary_function_ptr * const sign_floor_ceil_round_tab=(const unary_function_ptr *const )sign_floor_ceil_round_tab_alias;

  // vector<unary_function_ptr> sign_floor_ceil_round_v(sign_floor_ceil_round_tab,sign_floor_ceil_round_tab+5);
  const alias_type exp_tab_alias[]={(const alias_type)&__exp,0};
  const unary_function_ptr * const exp_tab=(const unary_function_ptr * const)exp_tab_alias;

  const alias_type tan_tab_alias[]={(const alias_type)&__tan,0};
  const unary_function_ptr * const tan_tab=(const unary_function_ptr * const)tan_tab_alias;

  const alias_type asin_tab_alias[]={(const alias_type)&__asin,0};
  const unary_function_ptr * const asin_tab=(const unary_function_ptr * const)asin_tab_alias;

  const alias_type acos_tab_alias[]={(const alias_type)&__acos,0};
  const unary_function_ptr * const acos_tab=(const unary_function_ptr * const)acos_tab_alias;

  const alias_type atan_tab_alias[]={(const alias_type)&__atan,0};
  const unary_function_ptr * const atan_tab=(const unary_function_ptr * const)atan_tab_alias;

  const alias_type pow_tab_alias[]={(const alias_type)&__pow,0};
  const unary_function_ptr * const pow_tab=(const unary_function_ptr * const)pow_tab_alias;

  const alias_type Heaviside_tab_alias[]={alias_at_Heaviside,0};
  const unary_function_ptr * const Heaviside_tab=(const unary_function_ptr * const)Heaviside_tab_alias;

  const alias_type invpowtan_tab_alias[]={alias_at_inv,alias_at_pow,alias_at_tan,0};
  const unary_function_ptr * const invpowtan_tab=(const unary_function_ptr * const) invpowtan_tab_alias;

  const gen_op_context halftan_tab[]={sin2tan2,cos2tan2,tan2tan2,0};
  const gen_op_context hyp2exp_tab[]={sinh2exp,cosh2exp,tanh2exp,0};
  const gen_op_context hypinv2exp_tab[]={sinh2exp,cosh2exp,tanh2exp,inv_test_exp,0};
  const gen_op_context trig2exp_tab[]={sin2exp,cos2exp,tan2exp,0};
  const gen_op_context atrig2ln_tab[]={asin2ln,acos2ln,atan2ln,0};
  // vector< gen_op_context > halftan_v(halftan_tab,halftan_tab+3);
  // vector< gen_op_context > hyp2exp_v(hyp2exp_tab,hyp2exp_tab+3);
  // vector< gen_op_context > trig2exp_v(trig2exp_tab,trig2exp_tab+3);
  const gen_op_context halftan_hyp2exp_tab[]={sin2tan2,cos2tan2,tan2tan2,sinh2exp,cosh2exp,tanh2exp,0};
  const gen_op_context exp2sincos_tab[]={exp2sincos,0};
  const gen_op_context tan2sincos_tab[]={tantosincos,0};
  const gen_op_context tan2sincos2_tab[]={tantosincos2,0};
  const gen_op_context tan2cossin2_tab[]={tantocossin2,0};
  const gen_op_context asin2acos_tab[]={asintoacos,0};
  const gen_op_context asin2atan_tab[]={asintoatan,0};
  const gen_op_context acos2asin_tab[]={acostoasin,0};
  const gen_op_context acos2atan_tab[]={acostoatan,0};
  const gen_op_context atan2asin_tab[]={atantoasin,0};
  const gen_op_context atan2acos_tab[]={atantoacos,0};
  // vector< gen_op_context > atrig2ln_v(atrig2ln_tab,atrig2ln_tab+3);
  const gen_op_context trigcos_tab[]={trigcospow,0};
  const gen_op_context trigsin_tab[]={trigsinpow,0};
  const gen_op_context trigtan_tab[]={trigtanpow,0};
  const gen_op_context powexpand_tab[]={powtopowexpand,0};
  const gen_op_context exp2power_tab[]={exptopower,0};
  const alias_type gamma_tab_alias[]={alias_at_Gamma,0};
  const unary_function_ptr * const gamma_tab=(const unary_function_ptr * const)gamma_tab_alias;

  const gen_op_context gamma2factorial_tab[]={gammatofactorial,0};
  const alias_type factorial_tab_alias[]={alias_at_factorial,0};
  const unary_function_ptr * const factorial_tab=(const unary_function_ptr * const)factorial_tab_alias;

  const gen_op_context factorial2gamma_tab[]={factorialtogamma,0};

  // for integration
  const alias_type  primitive_tab_op_alias[]={ (const alias_type)&__sin, (const alias_type)&__cos, (const alias_type)&__tan, (const alias_type)&__exp, (const alias_type)&__sinh, (const alias_type)&__cosh, (const alias_type)&__tanh, (const alias_type)&__asin, (const alias_type)&__acos, (const alias_type)&__atan, (const alias_type)&__ln,0};
  const unary_function_ptr * const primitive_tab_op=(const unary_function_ptr * const)primitive_tab_op_alias;
  const alias_type inverse_tab_op_alias[]={ (const alias_type)&__asin, (const alias_type)&__acos, (const alias_type)&__atan, (const alias_type)&__ln, (const alias_type)&__asinh, (const alias_type)&__acos, (const alias_type)&__atanh, (const alias_type)&__erf, (const alias_type)&__erfc, (const alias_type)&__Ei, (const alias_type)&__Si, (const alias_type)&__Ci,0};
  const unary_function_ptr * const inverse_tab_op=(const unary_function_ptr * const)inverse_tab_op_alias;

  const alias_type  analytic_sommets_alias[]={ (const alias_type)&__plus, (const alias_type)&__prod, (const alias_type)&__neg, (const alias_type)&__inv, (const alias_type)&__pow, (const alias_type)&__sin, (const alias_type)&__cos, (const alias_type)&__tan, (const alias_type)&__exp, (const alias_type)&__sinh, (const alias_type)&__cosh, (const alias_type)&__tanh, (const alias_type)&__asin, (const alias_type)&__acos, (const alias_type)&__atan, (const alias_type)&__asinh, (const alias_type)&__atanh, (const alias_type)&__acosh, (const alias_type)&__ln, (const alias_type)&__sqrt,0};  
  const unary_function_ptr * const analytic_sommets=(const unary_function_ptr * const)analytic_sommets_alias;
  // test if g is < > <= >=, 
  const alias_type  inequality_tab_alias[]={ (const alias_type)&__equal, (const alias_type)&__inferieur_strict, (const alias_type)&__inferieur_egal, (const alias_type)&__different, (const alias_type)&__superieur_strict, (const alias_type)&__superieur_egal,0};
  const unary_function_ptr * const inequality_tab=(const unary_function_ptr * const)inequality_tab_alias;
  // if you add functions to solve_fcns, modify the second argument of solve_fcns_v to reflect the number of functions in the array
  const alias_type  solve_fcns_tab_alias[]={  (const alias_type)&__exp, (const alias_type)&__ln, (const alias_type)&__sin, (const alias_type)&__cos, (const alias_type)&__tan, (const alias_type)&__asin, (const alias_type)&__acos, (const alias_type)&__atan, (const alias_type)&__sinh, (const alias_type)&__cosh, (const alias_type)&__tanh, (const alias_type)&__asinh, (const alias_type)&__acosh, (const alias_type)&__atanh,0};
  const unary_function_ptr * const solve_fcns_tab = (const unary_function_ptr * const)solve_fcns_tab_alias;

  const alias_type limit_tab_alias[]={(const alias_type)&__Gamma,(const alias_type)&__Psi,(const alias_type)&__erf,(const alias_type)&__Si,(const alias_type)&__Ci,(const alias_type)&__Ei,0};
  const unary_function_ptr * const limit_tab = (const unary_function_ptr * const) limit_tab_alias;
  const gen_op_context limit_replace [] = {Gamma_replace,Psi_replace,erf_replace,Si_replace,Ci_replace,Ei_replace,0};

  // vector<unary_function_ptr> inequality_sommets(inequality_tab,inequality_tab+sizeof(inequality_tab)/sizeof(unary_function_ptr));
  int is_inequality(const gen & g){
    if (g.type!=_SYMB)
      return false;
    return equalposcomp(inequality_tab,g._SYMBptr->sommet);
  }


  string unquote(const string & s){
    int l=s.size();
    if (l>2 && s[0]=='"' && s[l-1]=='"')
      return s.substr(1,l-2);
    else
      return s;
  }

  ostream & operator << (ostream & os,const alias_ref_vecteur & v){
#ifdef IMMEDIATE_VECTOR
    os << &v << ":" << *(gen *)v.begin_immediate_vect << "," << *(gen*) (v.begin_immediate_vect+1);
#else
    os << &v ;
#endif
    return os;
  }

  void fonction_bidon(){
#ifndef GIAC_GENERIC_CONSTANTS
    ofstream of("log");
    of << gen_inv_2 << endl;
    of <<  alias_cst_two_pi_tab << " " <<  cst_two_pi_refv << endl;
    of <<  alias_cst_pi_over_2_tab << " " <<  cst_pi_over_2_refv << endl;
    of <<  alias_plus_sqrt3_tab << " " <<  plus_sqrt3_refv << endl;
    of <<  alias_plus_sqrt2_tab << " " <<  plus_sqrt2_refv << endl;
    of <<  alias_plus_sqrt6_tab << " " <<  plus_sqrt6_refv << endl;
    of <<  alias_minus_sqrt3_2_tab << " " <<  minus_sqrt3_2_refv << endl;
    of <<  alias_minus_sqrt2_2_tab << " " <<  minus_sqrt2_2_refv << endl;
    of <<  alias_minus_sqrt3_3_tab << " " <<  minus_sqrt3_3_refv << endl;
    of <<  alias_plus_sqrt3_2_tab << " " <<  plus_sqrt3_2_refv << endl;
    of <<  alias_plus_sqrt2_2_tab << " " <<  plus_sqrt2_2_refv << endl;
    of <<  alias_plus_sqrt3_3_tab << " " <<  plus_sqrt3_3_refv << endl;
    of <<  alias_cos_pi_12_4_tab << " " <<  cos_pi_12_4_refv << endl;
    of <<  alias_cos_pi_12_tab << " " <<  cos_pi_12_refv << endl;
    of <<  alias_minus_cos_pi_12_4_tab << " " <<  minus_cos_pi_12_4_refv << endl;
    of <<  alias_minus_cos_pi_12_tab << " " <<  minus_cos_pi_12_refv << endl;
    of <<  alias_sin_pi_12_4_tab << " " <<  sin_pi_12_4_refv << endl;
    of <<  alias_sin_pi_12_tab << " " <<  sin_pi_12_refv << endl;
    of <<  alias_minus_sin_pi_12_4_tab << " " <<  minus_sin_pi_12_4_refv << endl;
    of <<  alias_minus_sin_pi_12_tab << " " <<  minus_sin_pi_12_refv << endl;
    of <<  alias_tan_pi_12_tab << " " <<  tan_pi_12_refv << endl;
    of <<  alias_tan_5pi_12_tab << " " <<  tan_5pi_12_refv << endl;
    of <<  alias_rad2deg_e_tab << " " <<  rad2deg_e_refv << endl;
    of <<  alias_deg2rad_e_tab << " " <<  deg2rad_e_refv << endl;
    of << plus_inf << " ";
    of << minus_inf << " ";
    of << plus_one_half << " ";
    of << minus_one_half << " ";
    of << plus_sqrt3 << " ";
    of << plus_sqrt2 << " ";
    of << plus_sqrt6 << " ";
    of << minus_sqrt2 << " ";
    of << minus_sqrt3 << " ";
    of << minus_sqrt6 << " ";
    of << minus_sqrt3_2 << " ";
    of << minus_sqrt2_2 << " ";
    of << minus_sqrt3_3 << " ";
    of << plus_sqrt3_2 << " " ;
    of << plus_sqrt2_2 << " ";
    of << plus_sqrt3_3 << " ";
    of << cos_pi_12 << " ";
    of << minus_cos_pi_12 << " ";
    of << sin_pi_12 << " ";
    of << minus_sin_pi_12 << " ";
    of << tan_pi_12 << " ";
    of << tan_5pi_12 << " ";
    of << minus_tan_pi_12 << " ";
    of << minus_tan_5pi_12 << " " << endl;
    of << cst_two_pi << " " ;
    of << cst_pi_over_2 << " ";
    of << cst_inv_pi << " ";
    of << cst_inv_180 << " " << endl;
    of << rad2deg_e << " " ;
    of << deg2rad_e << " " << endl;
#endif
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
