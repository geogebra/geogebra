// -*- mode:C++ ; compile-command: "g++ -I.. -g -c intg.cc -fno-strict-aliasing -DGIAC_GENERIC_CONSTANTS -DHAVE_CONFIG_H -DIN_GIAC " -*-
#include "giacPCH.h"
// #define LOGINT

/*
 *  Copyright (C) 2000,2014 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
using namespace std;
#include <stdexcept>
#include "vector.h"
#include <cmath>
#include <cstdlib>
#include <limits>
#include "sym2poly.h"
#include "usual.h"
#include "intg.h"
#include "subst.h"
#include "derive.h"
#include "lin.h"
#include "vecteur.h"
#include "gausspol.h"
#include "plot.h"
#include "prog.h"
#include "modpoly.h"
#include "series.h"
#include "tex.h"
#include "ifactor.h"
#include "risch.h"
#include "solve.h"
#include "intgab.h"
#include "moyal.h"
#include "maple.h"
#include "rpn.h"
#include "giacintl.h"
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#ifdef HAVE_LIBGSL
#include <gsl/gsl_math.h>
#include <gsl/gsl_sf_gamma.h>
#include <gsl/gsl_sf_psi.h>
#include <gsl/gsl_sf_zeta.h>
#include <gsl/gsl_odeiv.h>
#include <gsl/gsl_errno.h>
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // Left redimension p to degree n, i.e. size n+1
  void lrdm(modpoly & p,int n){
    int s=int(p.size());
    if (n+1>s)
      p=mergevecteur(vecteur(n+1-s),p);
  }

  struct pf1 {
    vecteur num;
    vecteur den;
    vecteur fact;
    int mult; // den=cste*fact^mult
    pf1():num(0),den(makevecteur(1)),fact(makevecteur(1)),mult(1) {}
    pf1(const pf1 & a) : num(a.num),  den(a.den), fact(a.fact),mult(a.mult) {}
    pf1(const vecteur &n, const vecteur & d, const vecteur & f,int m) : num(n), den(d), fact(f), mult(m) {};
    pf1(const polynome & n,const polynome & d,const polynome & f,int m): num(polynome2poly1(n,1)),den(polynome2poly1(d,1)),fact(polynome2poly1(f,1)),mult(m) {}
  };

  gen complex_subst(const gen & e,const vecteur & substin,const vecteur & substout,GIAC_CONTEXT){
    bool save_complex_mode=complex_mode(contextptr);
    complex_mode(true,contextptr);
    bool save_eval_abs=eval_abs(contextptr);
    eval_abs(false,contextptr);
    gen res=simplifier(eval(subst(e,substin,substout,false,contextptr),1,contextptr),contextptr); 
    // eval is used since after subst * are not flattened
    complex_mode(save_complex_mode,contextptr);
    eval_abs(save_eval_abs,contextptr);
    return res;
  }

  gen complex_subst(const gen & e,const gen & x,const gen & newx,GIAC_CONTEXT){
    bool save_complex_mode=complex_mode(contextptr);
    complex_mode(true,contextptr);
    gen res=subst(e,x,newx,false,contextptr);
    res=eval(res,1,contextptr);
    complex_mode(save_complex_mode,contextptr);
    return res;
  }

  static bool has_nop_var(const vecteur & v){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (contains(*it,at_nop))
	return true;
    }
    return false;
  }

  static gen nop_inv(const gen & e,GIAC_CONTEXT){
    return symbolic(at_nop,gen(symbolic(at_inv,e)));
  }
  static gen nop_pow(const gen & e,GIAC_CONTEXT){
    if ( (e.type!=_VECT) || (e._VECTptr->size()!=2))
      return symbolic(at_pow,e);
    if ( (e._VECTptr->back().type!=_INT_) || (e._VECTptr->back().val>=0) || ( (e._VECTptr->front().type==_SYMB) && (e._VECTptr->front()._SYMBptr->sommet==at_exp) ) )
      return symbolic(at_pow,change_subtype(e,_SEQ__VECT));
    return nop_inv(symbolic(at_pow,gen(makevecteur(e._VECTptr->front(),-e._VECTptr->back()),_SEQ__VECT)),contextptr);
  }

  static gen sin_over_cos(const gen & e,GIAC_CONTEXT){
    return rdiv(symb_sin(e),symb_cos(e),contextptr);
  }
  const gen_op_context invpowtan2_tab[]={nop_inv,nop_pow,sin_over_cos,0};
  // remove nop if nop() does not contain x
  static gen remove_nop(const gen & g,const gen & x,GIAC_CONTEXT){
    if (g.type==_VECT){
      vecteur res(*g._VECTptr);
      iterateur it=res.begin(),itend=res.end();
      for (;it!=itend;++it){
	*it=remove_nop(*it,x,contextptr);
      }
      return gen(res,g.subtype);
    }
    if (g.type!=_SYMB)
      return g;
    if (g._SYMBptr->sommet!=at_nop)
      return symbolic(g._SYMBptr->sommet,remove_nop(g._SYMBptr->feuille,x,contextptr));
    if (is_zero(derive(g._SYMBptr->feuille,x,contextptr)))
      return g._SYMBptr->feuille;
    else
      return g;
  }
  vecteur lvarxwithinv(const gen &e,const gen & x,GIAC_CONTEXT){
    gen ee=subst(e,invpowtan_tab,invpowtan2_tab,false,contextptr);
    ee=remove_nop(ee,x,contextptr);
    vecteur v(lvarx(ee,x));
    return v; // to remove nop do a return *(eval(v)._VECTptr);
  }

  bool is_constant_wrt(const gen & e,const gen & x,GIAC_CONTEXT){
    if (e.type==_VECT){
      const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
      for (;it!=itend;++it){
	if (!is_constant_wrt(*it,x,contextptr))
	  return false;
      }
      return true;
    }
    if (e==x)
      return false;
    if (e.type!=_SYMB)
      return true;
    return is_exactly_zero(derive(e,x,contextptr)); 
  }

  // return true if e=a*x+b
  bool is_linear_wrt(const gen & e,const gen &x,gen & a,gen & b,GIAC_CONTEXT){
    a=derive(e,x,contextptr);
    if (is_undef(a) || !is_constant_wrt(a,x,contextptr))
      return false;
    if (x*a==e)
      b=0;
    else
      b=ratnormal(e-a*x,contextptr);
    return true;
  }

  // return true if e=a*x+b
  bool is_quadratic_wrt(const gen & e,const gen &x,gen & a,gen & b,gen & c,GIAC_CONTEXT){
    gen tmp=derive(e,x,contextptr);
    if (is_undef(tmp) || !is_linear_wrt(tmp,x,a,b,contextptr))
      return false;
    a=ratnormal(rdiv(a,plus_two,contextptr),contextptr);
    c=ratnormal(e-a*x*x-b*x,contextptr);
    return true;
  }

  void decompose_plus(const vecteur & arg,const gen & x,vecteur & non_constant,gen & plus_constant,GIAC_CONTEXT){
    non_constant.clear();
    plus_constant=zero;
    const_iterateur it=arg.begin(),itend=arg.end();
    for (;it!=itend;++it){
      if (is_constant_wrt(*it,x,contextptr))
	plus_constant=plus_constant+(*it);
      else
	non_constant.push_back(*it);
    }
  }

  void decompose_prod(const vecteur & arg,const gen & x,vecteur & non_constant,gen & prod_constant,bool signcst,GIAC_CONTEXT){
    non_constant.clear();
    prod_constant=plus_one;
    const_iterateur it=arg.begin(),itend=arg.end();
    for (;it!=itend;++it){
      gen tst=*it;
      if (!signcst && it->is_symb_of_sommet(at_sign))
	tst=it->_SYMBptr->feuille;
      if (is_constant_wrt(tst,x,contextptr))
	prod_constant=prod_constant*(*it);
      else
	non_constant.push_back(*it);
    }
  }

  gen extract_cst(gen & u,const gen & x,GIAC_CONTEXT){
    if (!u.is_symb_of_sommet(at_prod) || u._SYMBptr->feuille.type!=_VECT)
      return 1;
    vecteur non_constant; gen prod_constant=1;
    decompose_prod(*u._SYMBptr->feuille._VECTptr,x,non_constant,prod_constant,false,contextptr);
    if (non_constant.size()==0)
      u=1;
    if (non_constant.size()==1)
      u=non_constant.front();
    if (non_constant.size()>1)
      u=symbolic(at_prod,gen(non_constant,_SEQ__VECT));
    return prod_constant;
  }

  // applies linearity of f. + & neg are distributed as well as * with respect
  // to terms that are constant w.r.t. x
  // e is assumed to be a scalar
  gen linear_apply(const gen & e,const gen & x,gen & remains, GIAC_CONTEXT, gen (* f)(const gen &,const gen &,gen &,const context *)){
    if (is_constant_wrt(e,x,contextptr) || (e==x) )
      return f(e,x,remains,contextptr);
    // e must be of type _SYMB
    if (e.type==_VECT){
      vecteur v(*e._VECTptr);
      vecteur r(v.size());
      for (unsigned i=0;i<v.size();++i){
	v[i]=linear_apply(v[i],x,r[i],contextptr,f);
      }
      remains=r;
      return gen(v,e.subtype);
    }
    if (e.type!=_SYMB) return gensizeerr(gettext("in linear_apply"));
    unary_function_ptr u(e._SYMBptr->sommet);
    gen arg(e._SYMBptr->feuille);
    gen res;
    if (u==at_neg){
      res=-linear_apply(arg,x,remains,contextptr,f);
      remains=-remains;
      return res;
    } // end at_neg
    if (u==at_plus){
      if (arg.type!=_VECT)
	return linear_apply(arg,x,remains,contextptr,f);
      const_iterateur it=arg._VECTptr->begin(),itend=arg._VECTptr->end();
      for (gen tmp;it!=itend;++it){
	res = res + linear_apply(*it,x,tmp,contextptr,f);
	remains =remains + tmp;
      }
      return res;
    } // end at_plus
    if (u==at_prod){
      if (arg.type!=_VECT)
	return linear_apply(arg,x,remains,contextptr,f);
      // find all constant terms in the product
      vecteur non_constant;
      gen prod_constant;
      decompose_prod(*arg._VECTptr,x,non_constant,prod_constant,false,contextptr);
      if (non_constant.empty()) return gensizeerr(gettext("in linear_apply 2")); // otherwise the product would be constant
      if (non_constant.size()==1)
	res = linear_apply(non_constant.front(),x,remains,contextptr,f);
      else
	res = f(symbolic(at_prod,gen(non_constant,_SEQ__VECT)),x,remains,contextptr);
      remains = prod_constant * remains;
      return prod_constant * res;
    } // end at_prod
    return f(e,x,remains,contextptr);
  }

  gen lnabs(const gen & x,GIAC_CONTEXT){
    bool _lnabs=do_lnabs(contextptr);
    if (!complex_mode(contextptr) && _lnabs && !has_i(x))
      return ln(abs(x,contextptr),contextptr);
    else
      return ln(x,contextptr);
  }

  gen lnabs2(const gen & x,const gen & xvar,GIAC_CONTEXT){
    if (xvar.type!=_IDNT)
      return lnabs(x,contextptr);
    bool _lnabs=do_lnabs(contextptr);
    if (!complex_mode(contextptr) && _lnabs && !has_i(x)){
      return symbolic(at_ln,symbolic(at_abs,x));
    }
    else {
      if (is_positive(-x,contextptr))
	return symbolic(at_ln,-x);
      return symbolic(at_ln,x);
    }
  }

  // eval N at X=e with e=x*exp(i*dephasage*pi/n)/(X-e)+conj and integrate
  static gen substconj_(const gen & N,const gen & X,const gen & x,const gen & dephasage_,bool residue_only,GIAC_CONTEXT){
    int mode=angle_mode(contextptr);
    gen pi=cst_pi;
    gen dephasage(dephasage_);
    if (mode==1){
      dephasage=ratnormal(gen(180)/cst_pi*dephasage,contextptr);
      pi=180;
    }
    if (mode==2){
      dephasage=ratnormal(gen(200)/cst_pi*dephasage,contextptr);
      pi=200;
    }
    gen c=cos(dephasage,contextptr);
    gen s=sin(dephasage,contextptr);
    gen e=x*(c+cst_i*s);
    gen b=subst(N,X,e,false,contextptr),rb,ib;
    reim(b,rb,ib,contextptr);
    gen N2=normal(-2*ib,contextptr); // same
    if (residue_only)
      return N2*sign(s*x,contextptr);
    gen res=normal(rb,contextptr)*symbolic(at_ln,pow(X,2)+ratnormal(-2*c*x,contextptr)*X+x.squarenorm(contextptr)); 
    gen atanterm=pi/cst_pi*symbolic(at_atan,(X-c*x)/(s*x));
    if (X.is_symb_of_sommet(at_tan))
      atanterm += pi*sign(s*x,contextptr)*symbolic(at_floor,X._SYMBptr->feuille/pi+plus_one_half);
    res=res+N2*atanterm;
    return res;
  }

  static gen substconj(const gen & N,const gen & X,const gen & x,const gen & dephasage,bool residue_only,GIAC_CONTEXT){
    if (has_i(N)){
      gen Nr,Ni;
      reim(N,Nr,Ni,contextptr);
      return substconj_(Nr,X,x,dephasage,residue_only,contextptr)+cst_i*substconj_(Ni,X,x,dephasage,residue_only,contextptr);
    }
    return substconj_(N,X,x,dephasage,residue_only,contextptr);
  }

  gen surd(const gen & c,int n,GIAC_CONTEXT){
    if (is_exactly_zero(c))
      return c;
    if (n%2 && is_positive(-c,contextptr)){
      if (c.type==_FLOAT_)
	return -exp(ln(-c,contextptr)/n,contextptr);
      return -pow(-c,inv(n,contextptr),contextptr);
    }
    else {
      if (c.type==_FLOAT_)
	return exp(ln(c,contextptr)/n,contextptr);
      return pow(c,inv(n,contextptr),contextptr);
    }
  }

  gen _surd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen a=args._VECTptr->front(),aa,b=args._VECTptr->back(),c;
    if (is_equal(a)){
      gen a0=a._SYMBptr->feuille[0],a1=a._SYMBptr->feuille[1];
      return symbolic(at_equal,makesequence(_surd(makesequence(a0,b),contextptr),_surd(makesequence(a1,b),contextptr)));
    }
    if (is_undef(a)) return a;
    if (is_undef(b)) return b;
    if (is_inf(b)){
      if (is_inf(a) || is_zero(a))
	return undef;
      return 1;
    }
    if (is_zero(b))
      return undef;
    if (is_inf(a))
      return pow(a,inv(b,contextptr),contextptr);
    c=_floor(b,contextptr);
    if (c.type==_FLOAT_)
      c=get_int(c._FLOAT_val);
    if (!has_evalf(a,aa,1,contextptr)){
      if (c.type==_INT_ && c==b && (c.val %2 ==0 || (a.is_symb_of_sommet(at_pow) && a._SYMBptr->feuille[1].type==_INT_ && a._SYMBptr->feuille[1].val % c.val==0)) )
	return pow(a,inv(c,contextptr),contextptr);	
      return symbolic(at_NTHROOT,gen(makevecteur(b,a),_SEQ__VECT));
    }
    if (c.type==_INT_ && c==b)
      return surd(a,c.val,contextptr);
    else
      return pow(a,inv(b,contextptr),contextptr);
  }
  static const char _surd_s []="surd";
  static define_unary_function_eval (__surd,&_surd,_surd_s);
  define_unary_function_ptr5( at_surd ,alias_at_surd,&__surd,0,true);

  static gen makelnatan(const gen & N,const gen & X,const gen & c0,int n,bool residue_only,GIAC_CONTEXT){
    gen c(c0),res(0);
    if (n%2){
      if (is_positive(-c,contextptr))
	c=-pow(-c,inv(n,contextptr),contextptr);
      else
	c=pow(c,inv(n,contextptr),contextptr);
      if (!residue_only)
	res += subst(N,X,c,false,contextptr)*lnabs(X-c,contextptr);
      for (int i=1;i<=n/2;++i)
	res += substconj(N,X,c,gen(2*i)/n*cst_pi,residue_only,contextptr);
      return res;
    }
    if (is_positive(c,contextptr) ){
      if (n==2) 
	c=sqrt(c,contextptr);
      else
	c=pow(c,inv(n,contextptr),contextptr);
      if (!residue_only)
	res += normal(subst(N,X,c,false,contextptr),contextptr)*lnabs2(X-c,X,contextptr)+normal(subst(N,X,-c,false,contextptr),contextptr)*lnabs2(X+c,X,contextptr);
      for (int i=1;i<n/2;++i)
	res += substconj(N,X,c,gen(2*i)/n*cst_pi,residue_only,contextptr);
    }
    else {
      if (n==2) 
	c=sqrt(-c,contextptr);
      else
	c=pow(-c,inv(n,contextptr),contextptr);
      for (int i=0;i<n/2;++i)
	res += substconj(N,X,c,gen(2*i+1)/n*cst_pi,residue_only,contextptr);
    }
    return res;
  }

  // integration of cyclotomic-type denominators
  static bool integrate_deno_length_2(const polynome & num,const vecteur & v,const vecteur & l,const vecteur & lprime,gen & res,bool residue_only,int intmode,GIAC_CONTEXT){
    if (v.size()<2)
      return false;
    const_iterateur it=v.begin()+1,itend=v.end()-1;
    for (;it!=itend;++it){
      if (!is_zero(*it))
	break;
    }
    int n=int(v.size())-1,d=int(it-v.begin()),deg;
    gen X=l.front();
    if (X.type==_VECT)
      X=X._VECTptr->front();
    gen a=r2e(v.front(),lprime,contextptr);
    gen b=r2e(v.back(),lprime,contextptr);
    // check for deno of type a*x^2n + A*x^n + b
    // FIXME: improve some simplifications of sin/cos(asin()/k) and remove test d==2
    if (d==2 && 2*d==n){
      ++it;
      for (;it!=itend;++it){
	if (!is_zero(*it))
	  break;
      }
      if (it==itend){ // ok!
	gen c=b;
	b=r2e(v[d],lprime,contextptr);
	gen delta=b*b-4*a*c;
	if (is_positive(-delta,contextptr)) // FIXME was if (is_zero(delta))
	  return false;
	if ( (intmode &2)==0)
	  gprintf(step_ratfrac,gettext("Integration of a rational fraction with denominator %gen\nroots are obtained by solving the 2nd order equation %gen=0 then extracting nth-roots"),makevecteur(a*symb_pow(vx_var,2*n)+b*symb_pow(vx_var,n)+c,a*symb_pow(vx_var,2)+b*vx_var+c),contextptr);
	// int(num/(a*X^2n+b*X^n+c),X) = 
	// sum(x=rootof(deno),num*x/(+/-n*sqrt(delta))*ln(X-x))
	gen sqrtdelta=sqrt(delta,contextptr);
	gen c1=(-b-sqrtdelta)/2/a;
	gen c2=(-b+sqrtdelta)/2/a;
	gen N=r2e(num,l,contextptr)*X/d/sqrtdelta;
	if (is_zero(im(a,contextptr)) && is_zero(im(b,contextptr)) && is_zero(im(c,contextptr))){
	  if (is_positive(delta,contextptr)){
	    res += makelnatan(N/c2,X,c2,d,residue_only,contextptr);
	    res -= makelnatan(N/c1,X,c1,d,residue_only,contextptr);
	    return true;
	  }
	  else {
	    gen module=sqrt(c/a,contextptr);
	    gen argument=acos(normal(-b/a/2/module,contextptr),contextptr);
	    // roots are module^(1/d)*exp(i*argument/d)*exp(2*i*pi*k/d)
	    // for k=0..d-1and conjugates
	    gen moduled=pow(c/a,inv(n,contextptr),contextptr);
	    for (int i=0;i<d;++i)
	      res += substconj(N/c2,X,moduled,(argument+2*i*cst_pi)/d,residue_only,contextptr);
	  }
	  return true;
	}
	if (residue_only)
	  return true;
	gen c1s=surd(c1,d,contextptr);
	gen c2s=surd(c2,d,contextptr);
	for (int i=0;i<d;++i){
	  gen x=c1s*exp((2*i*cst_i*cst_pi)/d,contextptr);
	  res -= normal(subst(N,X,x,false,contextptr)/c1,contextptr)*ln(X-x,contextptr);
	  x=c2s*exp((2*i*cst_i*cst_pi)/d,contextptr);
	  res += normal(subst(N,X,x,false,contextptr)/c2,contextptr)*ln(X-x,contextptr);
	}
	return true;
      }
    } // end if d==2 and n==2d
    gen c=normal(-b/a,contextptr);
    if (n%d)
      return false;
    if (d!=n){ 
      // rescale and check cyclotomic
      gen tw=v.back()/pow(*it/v.front(),n/d);
      if (tw.type!=_INT_ && tw.type!=_POLY)
	return false;
      tw=r2e(v/v.front(),lprime,contextptr);
      if (tw.type!=_VECT)
	return false;
      vecteur w=*tw._VECTptr;
      vecteur w_copy=w;
      c=pow(r2e(*it/v.front(),lprime,contextptr),inv(d,contextptr),contextptr);
      iterateur jt=w.begin()+1,jtend=w.end();
      for (int k=1;jt!=jtend;++jt,++k){
	*jt=normal(*jt * pow(c,-k),contextptr);
	if (jt->type!=_INT_ && jt->type!=_POLY)
	  break;
      }
      deg=is_cyclotomic(w,epsilon(contextptr));
      if (!deg){
	w=w_copy;
	c=pow(r2e(-*it/v.front(),lprime,contextptr),inv(d,contextptr),contextptr);
	jt=w.begin()+1,jtend=w.end();
	for (int k=1;jt!=jtend;++jt,++k){
	  *jt=normal(*jt * pow(c,-k),contextptr);
	  if (jt->type!=_INT_ && jt->type!=_POLY)
	    break;
	}
	deg=is_cyclotomic(w,epsilon(contextptr));
      }
      if (!deg)
	return false;
      if ( (intmode &2)==0)
	gprintf(step_cyclotomic,gettext("Integrate rational fraction with denominator a cyclotomic polynomial, roots are primitive roots of %gen=0"),makevecteur(a*symb_pow(vx_var,deg)+b),contextptr);
      // int(num/(a*X^n+b),X)=sum(x=rootof(-b/a),num*x/(-n*b)*ln(X-x))
      vecteur vprime=derivative(v),V,Vprime,d;
      egcd(v,vprime,0,V,Vprime,d);
      if (d.size()!=1)
	return false;
      gen dd=d.front();
      // 1/vprime=Vprime/d      
      gen N=normal(_quorem(makesequence(r2e(num,l,contextptr)*horner(r2e(Vprime,lprime,contextptr),X),horner(r2e(v,lprime,contextptr),X),X),contextptr)[1]/r2e(dd,lprime,contextptr),contextptr);
      if (complex_mode(contextptr) && !residue_only){
	for (int i=1;i<deg;++i){
	  if (gcd(i,deg)!=1)
	    continue;
	  gen x=c*exp((2*i*cst_i*cst_pi)/deg,contextptr);
	  res += normal(subst(N,X,x,false,contextptr),contextptr)*ln(X-x,contextptr);
	}
      }
      else {
	for (int i=1;i<=deg/2;++i){
	  if (gcd(i,deg)!=1)
	    continue;
	  res += substconj(N,X,c,2*i*cst_pi/deg,residue_only,contextptr);
	}
      }
      return true;
    } // if (d!=n)
    else {
      if ( (intmode &2)==0)
	gprintf(step_nthroot,gettext("Integrate rational fraction with denominator %gen=0\nroots are deduced from nth-roots of unity"),makevecteur(a*symb_pow(vx_var,n)+b),contextptr);
      // int(num/(a*X^n+b),X)=sum(x=rootof(-b/a),num*x/(-n*b)*ln(X-x))
      gen N=r2e(num,l,contextptr)*X/(r2e(-n*b,l,contextptr));
      if (complex_mode(contextptr) && !residue_only){
	c=pow(c,inv(n,contextptr),contextptr);
	for (int i=0;i<n;++i){
	  gen x=c*exp((2*i*cst_i*cst_pi)/n,contextptr);
	  res += normal(subst(N,X,x,false,contextptr),contextptr)*ln(X-x,contextptr);
	}
	return true;
      }
      res += makelnatan(N,X,c,n,residue_only,contextptr);
      return true;
    }
  }

  // tests if v is symmetric or antisymmetric
  // if it is, compute res such that res[t+-1/t]=v/t^[deg(v)/2]
  static int is_symmetric(const vecteur & v,vecteur & res,bool sym){
    if (v.empty())
      return 0;
    int n=int(v.size());
    vecteur w;
    if (n%2)
      w=v;
    else {
      if (!is_zero(v[n-1]))
	return 0;
      w=vecteur(v.begin(),v.end()-1);
      --n;
    }
    vecteur w1(w);
    reverse(w1.begin(),w1.end());
    if (!sym){
      for (int i=1;i<n;i+=2){
	w1[i] = -w1[i];
      }
    }
    int rescoeff=0;
    if (w==w1)
      rescoeff=1;
    if (w==-w1)
      rescoeff=-1;
    if (!rescoeff)
      return 0;
    // if antisym, n/2 is the number of power of (t^2-1), check if it is odd
    if (!sym && (n/2)%2)
      rescoeff = -rescoeff;
    vecteur test(makevecteur(1,0,sym?1:-1)),q,r;
    res.clear();
    for (n/=2;n>0;n--){
      DivRem(w,powmod(test,n,0,0),0,q,r);
      if (q.size()>1)
	return 0;
      w=r.empty()?r:vecteur(r.begin(),r.end()-1);
      res.push_back(q.empty()?0:q.front());
    }
    if (w.empty())
      res.push_back(0); // was return 0;
    else
      res.push_back(w.front());
    return rescoeff;
  }

  // n/d(x) -> newn/newd(t) with x=a/t, 
  // if dx is true multiplies by dx/dt=-a/t^2
  static void xtoinvx(const gen & a,const modpoly & n,const modpoly & d,modpoly & newn, modpoly & newd,bool dx){
    int ns=int(n.size()); int nd=int(d.size());
    newn=vecteur(ns); newd=vecteur(nd);
    gen ad(1);
    for (int i=ns-1;i>=0;--i){
      newn[ns-1-i]=ad*n[i];
      ad = ad*a;
    }
    ad=1;
    for (int i=nd-1;i>=0;--i){
      newd[nd-1-i]=ad*d[i];
      ad = ad*a;
    }
    if (dx){
      newn=operator_times(-a,newn,0);
      ns+=2;
    }
    trim(newn,0);
    trim(newd,0);
    for (;ns>nd;--ns){
      newd.push_back(0);
    }
    for (;nd>ns;--nd){
      newn.push_back(0);
    }
  }

  static gen integrate_rational(const gen & e, const gen & x, gen & remains_to_integrate,gen & xvar,int intmode,GIAC_CONTEXT);

  static void solve_aPprime_plus_P(const gen & anum,const gen & aden,const vecteur & Q,vecteur & R,gen & Pden){
    // a P+P'=Q, a=anum/aden, on cherche P sous la forme R/Pden
    // On a (k+1)p_(k+1)+ anum/aden*p_k=q_k
    // Donc p_k=aden/anum*(q_k-(k+1)p_(k+1))
    // n=deg[Q], on a donc Pden=anum^(n+1), puis on cherche R=P*anum^(n+1)
    // on multiplie donc Q par anum^(n+1) S=Q*anum^(n+1)/a
    // on a aR+R'=aS
    // on a donc par ordre decr. r_(n+1)=0 
    // r_k= s_k - (k+1)*r_(k+1)/a
    // avec des divisions sans creation de denominateurs
    // par ex. P'+3P=x^2+5x+7 -> r_2=9, r_1=39, r_0=50, a=3, n=2, a^n=9
    R.clear();
    if (Q.empty()){
      Pden=plus_one;
      return;
    }
    int n=int(Q.size())-1;
    R.reserve(n+1);
    Pden=pow(anum,n);
    vecteur S;
    multvecteur(Pden*aden,Q,S);
    Pden=Pden*anum;
    const_iterateur it=S.begin(),itend=S.end();
    R.push_back(*it);
    ++it;
    for (int k=n-1;it!=itend;++it,--k){
      R.push_back(*it-rdiv(gen(k+1)*R.back()*aden,anum,context0));
    }
    // should simplify R with Pden
  }

  static gen integrate_linearizable(const gen & e,const gen & gen_x,gen & remains_to_integrate,int intmode,GIAC_CONTEXT){
    // exp linearization
    vecteur vexp;
    gen res;
    const identificateur & id_x=*gen_x._IDNTptr;
    lin(e,vexp,contextptr); // vexp = coeff, arg of exponential
    if ( (intmode &2)==0 ){
      gen tmp=unlin(vexp,contextptr);
      if (vexp.size()>2 || !is_zero(ratnormal(tmp-e,contextptr)))
	gprintf(step_linearizable,gettext("Integrate linearizable expression %gen -> %gen"),makevecteur(e,tmp),contextptr);
    }
    const_iterateur it=vexp.begin(),itend=vexp.end();
    for (;it!=itend;){
      // trig linearization
      vecteur vtrig;
      gen coeff=*it;
      ++it; // it -> on the arg of the exp that must be linear
      gen rex2,rea,reb,reaxb=*it;
      ++it;
      if (!is_quadratic_wrt(reaxb,gen_x,rex2,rea,reb,contextptr)){
	// IMPROVE using int(exp(-x^a))=1/a*igamma(1/a,x^a)
	vecteur lv=lvarxwithinv(makevecteur(reaxb,coeff),gen_x,contextptr);
	if (lv.size()==1){
	  gen C=_coeff(makesequence(reaxb,gen_x),contextptr);
	  if (C.type==_VECT && C._VECTptr->size()>2){
	    vecteur Cv=*C._VECTptr;
	    int n=int(Cv.size())-1;
	    gen c=Cv[0];
	    gen a=-Cv[1]/(n*c);
	    // must be c*(x-a)^n
	    if (C==_coeff(makesequence(c*pow(gen_x-a,n,contextptr),gen_x),contextptr) && ((n%2) || is_positive(-c,contextptr))){
	      // c=surd(c,n,contextptr);
	      C=_coeff(makesequence(coeff,gen_x),contextptr);
	      C=_ptayl(makesequence(C,a,gen_x),contextptr);
	      if (C.type==_VECT){
		c=-c;
		gen ca=surd(c,n,contextptr);
		Cv=*C._VECTptr;
		int m=int(Cv.size())-1;
		gen ires=0;
		// 1/n*igamma(1/n+b/n,c*x^n)'=x^b*exp(-c*x^n)*c^(b+1)/n
		for (int b=0;b<=m;++b){
		  ires += Cv[m-b]*_lower_incomplete_gamma(makesequence(gen(b+1)/gen(n),c*pow(gen_x-a,n)),contextptr)/pow(ca,b+1,contextptr);
		}
		if (n%2==0){		  
		  ires=ires*abs(gen_x,contextptr)/gen_x; // sign(gen_x,contextptr);
		}
		ires=ires/n;
		res += ires;
		continue;
	      }
	    }
	  }
	}
	remains_to_integrate = remains_to_integrate + coeff*exp(reaxb,contextptr);
	continue;
      }
      if (!is_zero(rex2)){
	if (1 
	    //&&is_zero(im(rex2,contextptr)) 
	    //&& is_positive(-rex2,contextptr)
	    ){
	  const vecteur & vx2=lvarxpow(coeff,gen_x);
	  if ( vx2.size()>1 || (!vx2.empty() && vx2.front()!=gen_x) ){
	    remains_to_integrate = remains_to_integrate + coeff*exp(reaxb,contextptr);
	    continue;
	  }
	  // int(exp(rex2*x^2+rea*x+reb)*P(x),x)
	  gen decal=rea/rex2/2;
	  gen cst=normal(reb-rex2*decal*decal,contextptr);
	  // exp(cst)*int(exp(rex2*(x+decal)^2)*P(x),x)
	  coeff=quotesubst(coeff,gen_x,gen_x-decal,contextptr);
	  // exp(cst)*subst(int(exp(rex2*x^2)*coeff(x),x),x,x+decal)
	  vecteur les_var(1,gen_x); // insure x is the main var
	  lvar(makevecteur(coeff,rex2),les_var);
	  int les_vars=int(les_var.size());
	  gen in_coeff,in_coeffnum,in_coeffden,ina;
	  in_coeff=e2r(coeff,les_var,contextptr);
	  ina=e2r(rex2,vecteur(les_var.begin()+1,les_var.end()),contextptr);
	  fxnd(in_coeff,in_coeffnum,in_coeffden);
	  vecteur in_coeffnumv;
	  if (in_coeffnum.type==_POLY)
	    in_coeffnumv=polynome2poly1(*in_coeffnum._POLYptr,1);
	  else
	    in_coeffnumv.push_back(in_coeffnum);
	  // now find int(exp(ina*x^2)*P(x)), coeffs of P are in in_coeffnumv
	  int vs=int(in_coeffnumv.size())-1;
	  vecteur vres(vs+1);
	  // integration by part to decrease vs
	  for (int i=vs;i>=1;--i){ 
	    // i is the degree of the term to integrate
	    gen tmp=in_coeffnumv[vs-i]/ina/2;
	    vres[vs-(i-1)]=tmp;
	    if (i>1)
	      in_coeffnumv[vs-(i-2)] -= (i-1)*tmp;
	  }
	  gen vresden;
	  lcmdeno(vres,vresden,contextptr); // lcmdeno_converted?
	  gen ppart=subst(r2e(poly12polynome(vres,1,les_vars),les_var,contextptr),gen_x,gen_x+decal,false,contextptr)/r2e(vresden,vecteur(les_var.begin()+1,les_var.end()),contextptr)*exp(reaxb,contextptr);
	  // add erf part from the last coeff vres[vs]
	  gen a=-rex2; // r2e(-ina,les_var,contextptr);
	  gen sqrta=sqrt_noabs(a,contextptr);
	  gen erfpart=r2e(in_coeffnumv[vs],cdr_VECT(les_var),contextptr)*symbolic(at_sqrt,cst_pi)/sqrta*exp(cst,contextptr)/2*_erf(sqrta*(gen_x+decal),contextptr);
	  res += (ppart + erfpart)/r2e(in_coeffden,les_var,contextptr);
	  continue;
	}
	remains_to_integrate = remains_to_integrate + coeff*exp(reaxb,contextptr);
	continue;
      }
      gen reai=im(rea,contextptr),rebi=im(reb,contextptr);
      if (!is_zero(reai) || !is_zero(rebi)){
	gen reaxbi=reai*gen_x+rebi;
	coeff=coeff*(cos(reaxbi,contextptr)+cst_i*sin(reaxbi,contextptr));
	rea=re(rea,contextptr);
	reb=re(reb,contextptr);
	reaxb=rea*gen_x+reb;
      }
      tlin(coeff,vtrig,contextptr); // vtrig = coeff , sin/cos(arg)/1
      if ( (intmode &2)==0 ){
	gen tmp=tunlin(vtrig,contextptr);
	if (vtrig.size()>2 || !is_zero(ratnormal(tmp-coeff,contextptr)))
	  gprintf(step_triglinearizable,gettext("Integrate trigonometric linearizable expression %gen -> %gen"),makevecteur(coeff,tmp),contextptr);
      }
      const_iterateur jt=vtrig.begin(),jtend=vtrig.end();
      for (;jt!=jtend;){
	// now check that each arg is linear and coeff polynomial
	coeff=*jt;
	++jt;
	gen ima,imb,imaxb=*jt;
	++jt;
	if (is_constant_wrt(imaxb,gen_x,contextptr)){
	  coeff = coeff*imaxb;
	  imaxb=1;
	}
	int trig_type=0; // 0 for 1, 1 for sin, 2 for cos
	if (imaxb.type==_SYMB){
	  if (imaxb._SYMBptr->sommet==at_sin)
	    trig_type=1;
	  if (imaxb._SYMBptr->sommet==at_cos)
	    trig_type=2;
	}
	else
	  imaxb=0;
	// check polynomial
	const vecteur vx2=lvarxpow(coeff,gen_x);
	bool coeffnotpoly=(vx2.size()>1) || ( (!vx2.empty()) && (vx2.front()!=gen_x));
	gen imc;
	bool quad=imaxb.type==_SYMB && is_quadratic_wrt(imaxb._SYMBptr->feuille,gen_x,ima,imb,imc,contextptr);
	if (!coeffnotpoly && quad && !is_zero(ima)){
	  imc=_trig2exp(coeff*imaxb,contextptr);
	  res += integrate_linearizable(imc,gen_x,remains_to_integrate,intmode,contextptr);
	  continue;
	}
	if ( coeffnotpoly || ( imaxb.type==_SYMB && !is_linear_wrt(imaxb._SYMBptr->feuille,gen_x,ima,imb,contextptr)) ) {
	  if (trig_type) imaxb=imaxb._SYMBptr->feuille;
	  gen tmp(plus_one);
	  if (trig_type==1)
	    tmp=sin(imaxb,contextptr);
	  if (trig_type==2)
	    tmp=cos(imaxb,contextptr);
	  remains_to_integrate = remains_to_integrate + coeff * exp(reaxb,contextptr) * tmp;
	  continue;
	}
	// everything OK coeff*exp(rea*x+reb)* 1/cos/sin(ima*x+imb)
	if (trig_type)
	  imaxb=imaxb._SYMBptr->feuille;
	else {
	  if (is_zero(rea)){
	    gen tmprem,xvar(gen_x);
	    res= res + exp(reb,contextptr)*integrate_rational(coeff,gen_x,tmprem,xvar,intmode,contextptr);
	    remains_to_integrate = remains_to_integrate+exp(reb,contextptr)*tmprem;
	    continue;
	  }
	}
	bool coeff_is_real=false;
	if (trig_type){
	  gen imcoeff=im(coeff,contextptr);
	  rewrite_with_t_real(imcoeff,gen_x,contextptr);
	  if (is_zero(imcoeff) && is_zero(im(rea,contextptr)) && is_zero(im(ima,contextptr)))
	    coeff_is_real=true;
	}
	// find vars of coeff,rea,reb,ima,imb
	vecteur les_var(1,gen_x); // insure x is the main var
	lvar(makevecteur(coeff,rea,ima),les_var);
	int les_vars=int(les_var.size());
	gen in_coeff,in_coeffnum,in_coeffden,in_rea,in_ima,in_anum,in_aden;
	in_coeff=e2r(coeff,les_var,contextptr);
	fxnd(in_coeff,in_coeffnum,in_coeffden);
	vecteur in_coeffnumv;
	if (in_coeffnum.type==_POLY)
	  in_coeffnumv=polynome2poly1(*in_coeffnum._POLYptr,1);
	else
	  in_coeffnumv.push_back(in_coeffnum);
	in_coeffden=firstcoefftrunc(in_coeffden);
	in_rea=firstcoefftrunc(e2r(rea,les_var,contextptr));
	in_ima=firstcoefftrunc(e2r(ima,les_var,contextptr));
	vecteur resnum;
	gen resden,resplus;
	fxnd(in_rea+cst_i*in_ima,in_anum,in_aden);
	solve_aPprime_plus_P(in_anum,in_aden,in_coeffnumv,resnum,resden);
	resplus=rdiv(r2e(poly12polynome(resnum,1,les_vars),les_var,contextptr),r2e(poly12polynome(vecteur(1,resden*in_coeffden),1,les_vars),les_var,contextptr),contextptr);
	if (step_infolevel(contextptr)){
	  gprintf(step_polyexp,gettext("Primitive of %gen is polynomial of same degree*same exponential %gen"),makevecteur(coeff*symb_exp(reaxb+cst_i*imaxb),resplus*symb_exp(reaxb+cst_i*imaxb)),contextptr);
	}
	if (!trig_type){
	  res = res + resplus*exp(reaxb,contextptr);
	  continue;
	}
	if (coeff_is_real){
	  gen resre=re(resplus,contextptr);
	  rewrite_with_t_real(resre,gen_x,contextptr);
	  gen resim=im(resplus,contextptr);
	  rewrite_with_t_real(resim,gen_x,contextptr);
	  if (trig_type==1)
	    res = res + exp(reaxb,contextptr)*(resim*cos(imaxb,contextptr)+resre*sin(imaxb,contextptr));
	  else
	    res = res + exp(reaxb,contextptr)*(resre*cos(imaxb,contextptr)-resim*sin(imaxb,contextptr));
	  continue;
	}
	fxnd(in_rea-cst_i*in_ima,in_anum,in_aden);
	solve_aPprime_plus_P(in_anum,in_aden,in_coeffnumv,resnum,resden);
	gen resmoins=rdiv(r2e(poly12polynome(resnum,1,les_vars),les_var,contextptr),r2e(poly12polynome(vecteur(1,resden*in_coeffden),1,les_vars),les_var,contextptr),contextptr);
	if (trig_type==1)
	  res = res + exp(reaxb,contextptr)*rdiv(resplus*exp(cst_i*imaxb,contextptr)-resmoins*exp(-cst_i*imaxb,contextptr),plus_two*cst_i,contextptr);
	else
	  res = res +  exp(reaxb,contextptr)*rdiv(resplus*exp(cst_i*imaxb,contextptr)+resmoins*exp(-cst_i*imaxb,contextptr),plus_two,contextptr);
      } // end for (jt)
    } // end for (it)
    gen tmp=remains_to_integrate;
    remains_to_integrate=0;
    res=res+risch(tmp,id_x,remains_to_integrate,contextptr);
    if (is_undef(res)){
      remains_to_integrate=e;
      return 0;
    }
    if (is_zero(im(e,contextptr)) &&has_i(res) && lop(res,at_erf).empty()){
      remains_to_integrate=re(remains_to_integrate,contextptr);
      res=ratnormal(re(res,contextptr),contextptr);
    }
    return res;
  } // end linearizable

  gen linear_integrate_nostep(const gen & e,const gen & x,gen & remains_to_integrate,int intmode,GIAC_CONTEXT);

  static bool integrate_sqrt(gen & e,const gen & gen_x,const vecteur & rvar,gen & res,gen & remains_to_integrate,int intmode,GIAC_CONTEXT){ // x and a power
    // subcase 1: power is a fraction of int
    // find rational parametrization if possible
    // subcase 2: 1st argument of power is linear, 2nd is constant && no inv
    gen argument=rvar.back()._SYMBptr->feuille._VECTptr->front();
    gen exposant=rvar.back()._SYMBptr->feuille._VECTptr->back();
    if ( (exposant.type==_FRAC) && (exposant._FRACptr->num.type==_INT_) && (exposant._FRACptr->den.type==_INT_) ){
      int d=exposant._FRACptr->den.val; // n=exposant._FRACptr->num.val,
      gen a,b,c,tmprem,tmpres,tmpe;
      if (is_linear_wrt(argument,gen_x,a,b,contextptr)){
	// argument=(ax+b)=t^d -> x=(t^d-a)/b and dx=d/a*t^(d-1)*dt
	vecteur substin(makevecteur(argument,gen_x));
	vecteur substout(makevecteur(pow(gen_x,d),rdiv(pow(gen_x,d)-b,a,contextptr)));
	tmpe=complex_subst(e,substin,substout,contextptr)*pow(gen_x,d-1);
	tmpres=linear_integrate_nostep(tmpe,gen_x,tmprem,intmode,contextptr);
	gen fnc_inverse=pow(a*gen_x+b,fraction(1,d),contextptr);
	remains_to_integrate=rdiv(d,a,contextptr)*complex_subst(tmprem,gen_x,fnc_inverse,contextptr);
	res=rdiv(d,a,contextptr)*complex_subst(tmpres,gen_x,fnc_inverse,contextptr);
	return true;
      }
      vecteur tmpv(1,gen_x);
      lvar(argument,tmpv);
      gen fr,fr_n,fr_d,ap,bp;
      fr=e2r(argument,tmpv,contextptr);
      fxnd(fr,fr_n,fr_d);
      fr_n=r2e(fr_n,tmpv,contextptr);
      fr_d=r2e(fr_d,tmpv,contextptr);
      if (is_linear_wrt(fr_n,gen_x,a,b,contextptr) && is_linear_wrt(fr_d,gen_x,ap,bp,contextptr) ){
	// argument=(a*x+b)/(ap*x+bp)=t^d
	// -> x=(bp*t^d-b)/(a-ap*t^d) 
	// -> dx= d*(b*ap-a*bp)*t^(d-1)/(a-ap*t^d)^2
	vecteur substin(makevecteur(argument,gen_x));
	vecteur substout(makevecteur(pow(gen_x,d),rdiv(bp*pow(gen_x,d)-b,a-ap*pow(gen_x,d),contextptr)));
	tmpe=complex_subst(e,substin,substout,contextptr)*rdiv(pow(gen_x,d-1),pow(a-ap*pow(gen_x,d),2),contextptr);
	tmpres=linear_integrate_nostep(tmpe,gen_x,tmprem,intmode,contextptr);
	gen fnc_inverse=pow(rdiv(a*gen_x+b,ap*gen_x+bp,contextptr),fraction(1,d),contextptr);
	gen tmp=gen(d)*(a*bp-b*ap);
	remains_to_integrate=tmp*complex_subst(tmprem,gen_x,fnc_inverse,contextptr);
	res=tmp*complex_subst(tmpres,gen_x,fnc_inverse,contextptr);
	return true;
      }
      /*   ( * 	2nd order: dispatch for y=ax^2+bx+c	           * )
	   ( * 	a>0	->	x=[m^2-c]/[b-2*sqrt[a]*m]          * )
	   ( *			m=sqrt[y]-sqrt[a]*x	           * )
	   ( * 			dx/sqrt[y]=2*dm/[b-2*sqrt[a]*m]	   * )
      */
      if (d==2 && is_constant_wrt(fr_d,gen_x,contextptr)){
	// write e as alpha+beta*sqrt(argument)
	identificateur tmpx(" x");
	gen e1=complex_subst(e,sqrt(argument,contextptr),tmpx,contextptr);
	vecteur lv(1,tmpx);
	lvar(e1,lv);
	gen e2=e2r(e1,lv,contextptr),num,den;
	fxnd(e2,num,den);
	den=r2e(den,lv,contextptr);
	num=r2e(num,lv,contextptr);
	// multiply denominator of e2 by conjugate 
	gen pmini=tmpx*tmpx-argument;
	gen C=_egcd(makesequence(den,pmini,tmpx),contextptr);
	if (is_undef(C)){
	  res=C;
	  return true;
	}
	num=_rem(makesequence(num*C[0],pmini,tmpx),contextptr);
	if (is_undef(num)){
	  res= num;
	  return true;
	}
	den=C[2];
	gen alpha,beta,xvar(gen_x);
	if (!is_linear_wrt(num,tmpx,beta,alpha,contextptr)){
	  res=gensizeerr(contextptr);
	  return true;
	}
	alpha=integrate_rational(alpha/den,gen_x,remains_to_integrate,xvar,intmode,contextptr);
	if (is_undef(alpha)){
	  res=alpha;
	  return true;
	}
	/* Instead we should factor argument in den 
	   FIXME in usual.cc diff of ln should expand * and / and rm abs
	   write y=argument, P=beta
	   we want to integrate P*sqrt(y)/den=(P*y)/den* y^(-1/2)
	   *IF* den=y^l*D where D is prime with y (not always true...)
	   P/Dy^l = P_y/y^l + P_D/D <--> P = P_y*D + P_D*y^l,
	   find P_D and P_y by Bezout, find
	   g = Q*D+R*y^l then Pg = P*Q*D + P*R*y^l hence
	   P_D = P*R mod D/g  and  P_y = P*Q /g + [P*R div D] *y^l /g 
	*/
	gen y=argument,P=beta,D=den; // P*y/den
	C=_quorem(makesequence(D,y,gen_x),contextptr);
	if (is_undef(C)){
	  res= C;
	  return true;
	}
	int l=0;
	if (is_zero(C[1])){ // P/(den/y)
	  D=C[0];
	  for (;;++l){
	    C=_quorem(makesequence(D,y,gen_x),contextptr);
	    if (is_undef(C)){
	      res=C;
	      return true;
	    }
	    if (!is_zero(C[1]))
	      break;
	    D=C[0];
	  }
	}
	else
	  P=P*y;
	gen yl=pow(y,l);
	C=_egcd(makesequence(D,yl,gen_x),contextptr);
	if (is_undef(C)){
	  res= C;
	  return true;
	}
	gen g=C[2],Q=C[0],R=C[1];
	C=_quorem(makesequence(P*R,D,gen_x),contextptr);
	if (is_undef(C)){
	  res=C;
	  return true;
	}
	gen PD=C[1]/g;
	// changed made for int(1/(sin(x)*sqrt(sin(2*x)^3)));
	C=_quorem(makesequence(P*Q+C[0]*yl,g,gen_x),contextptr);
	if (!is_zero(C[1]))
	  return false;
	gen Py=C[0];  
	// gen Py=(P*Q+C[0]*yl)/g;
	C=_quorem(makesequence(Py,y,gen_x),contextptr);
	if (is_undef(C)){
	  res= C; 
	  return true;
	}
	/*
	  int[ Py/y^l*y^-1/2 ] = Q*y^[1/2-l] + int[ C*y^-1/2 ]
	  degre[Py]=n, degre[y]=k, find Q degre[Q]=n+1-k and C degre[C]=k-2
	  so that Py = Q'*y + Q*y'*[1/2-l] + C y^l   
	  to do this we represent Q by a n+2-k-vector, C by a k-1-vector
	  gluing Q and C we get a n+1-vector that must be solution of a
	  n+1*n+1 linear system. Now we build the matrix of this system
	  The n+2-k first columns are				    
	  y'[1/2-l]  ...  x^alpha*y'*[1/2-l]+alpha*x^[alpha-1]*y ...    
	  The k-1 last columns are 
	  y^l  ...  x^beta*y^l 
	  Note 1: to avoid rational input in the matrix we multiply by 2
	  coef of Q and C are found in the reverse order
	  for l!=0 n is more precisely max[deg[Py],k[l+1]-2]
	  Note 2: at the end we integrate only (C+PD/D)*y^(-1/2)
	*/
	gen tmpv=_e2r(makesequence(Py,gen_x),contextptr);
	if (tmpv.type!=_VECT){
	  if (tmpv.type==_FRAC){
	    if (tmpv._FRACptr->den.type==_VECT){
	      if (tmpv._FRACptr->den._VECTptr->size()!=1){
		*logptr(contextptr) << "Internal error integrating sqrt" << endl;
		return false;
	      }
	      tmpv._FRACptr->den=tmpv._FRACptr->den._VECTptr->front();
	    }
	    if (tmpv._FRACptr->num.type==_VECT)
	      tmpv=multvecteur(inv(tmpv._FRACptr->den,contextptr),*tmpv._FRACptr->num._VECTptr);
	  }
	  if (tmpv.type!=_VECT)
	    tmpv=vecteur(1,tmpv); // change 3/1/2013 for int(sqrt(1+x^2)/(-2*x^2))
	  // res= gensizeerr(contextptr);
	  // return true;
	}
	vecteur colP=*tmpv._VECTptr;
	int n=int(colP.size())-1;
	tmpv=_e2r(makesequence(y,gen_x),contextptr);
	if (tmpv.type!=_VECT){
	  res= gensizeerr(contextptr);
	  return true;
	}
	int k=int(tmpv._VECTptr->size())-1;
	n=giacmax(n,k*(l+1)-2);
	n=giacmax(n,k-1);
	if (n){
	  lrdm(colP,n);
	  gen yprime=(1-2*l)*derive(y,gen_x,contextptr);
	  if (is_undef(yprime)){
	    res= yprime;
	    return true;
	  }
	  matrice sys;
	  tmpv=_e2r(makesequence(yprime,gen_x),contextptr);
	  if (tmpv.type!=_VECT){
	    res=gensizeerr(contextptr);
	    return true;
	  }
	  vecteur col0(*tmpv._VECTptr);
	  vecteur col(col0);
	  lrdm(col,n);
	  sys.push_back(col);
	  col0.push_back(zero);
	  tmpv=_e2r(makesequence(2*y,gen_x),contextptr);
	  if (tmpv.type!=_VECT){ 
	    res=gensizeerr(contextptr);
	    return true;
	  }
	  vecteur col1(*tmpv._VECTptr);
	  for (int i=1;i<n+2-k;++i){
	    col=col0+gen(i)*col1;
	    lrdm(col,n);
	    col0.push_back(zero);
	    col1.push_back(zero);
	    sys.push_back(col);
	  }
	  tmpv=_e2r(makesequence(2*yl,gen_x),contextptr);
	  if (tmpv.type!=_VECT){
	    res= gensizeerr(contextptr);
	    return true;
	  }
	  col0=*tmpv._VECTptr;
	  for (int i=0;i<k-1;++i){
	    col=col0;
	    lrdm(col,n);
	    sys.push_back(col);
	    col0.push_back(zero);
	  }
	  sys=mtran(sys);
	  int st=step_infolevel(contextptr);
	  step_infolevel(contextptr)=0;
	  col0=linsolve(sys,colP,contextptr);
	  step_infolevel(contextptr)=st;
	  if (!col0.empty() && is_undef(col0.front())){
	    res= col0.front();
	    return true;
	  }
	  reverse(col0.begin(),col0.end()); // C at the beginning, Q at the end
	  C=2*horner(vecteur(col0.begin(),col0.begin()+k-1),gen_x);
	  Q=2*horner(vecteur(col0.begin()+k-1,col0.end()),gen_x);
	  alpha=alpha+Q*sqrt(y,contextptr)/yl;
	}
	else
	  C=Py;
	e=(C+PD/D)/sqrt(argument,contextptr);
	if (is_quadratic_wrt(argument,gen_x,a,b,c,contextptr)){
	  if (!is_positive(-a,contextptr)){
	    gen sqrta(sqrt(a,contextptr));
	    identificateur id_m(" m");
	    gen m(id_m);
	    tmpe=eval(rdiv(complex_subst(e*sqrt(argument,contextptr),argument,pow(m+sqrta*gen_x,2),contextptr),b-plus_two*sqrta*m,contextptr),1,contextptr);
	    tmpe=ratnormal(complex_subst(tmpe,gen_x,rdiv(m*m-c,b-plus_two*sqrta*m,contextptr),contextptr),contextptr);
	    tmpres=linear_integrate_nostep(tmpe,m,tmprem,intmode,contextptr);
	    remains_to_integrate=remains_to_integrate+complex_subst(plus_two*tmprem,m,sqrt(argument,contextptr)-sqrta*gen_x,contextptr);
	    res= alpha+complex_subst(plus_two*tmpres,m,sqrt(argument,contextptr)-sqrta*gen_x,contextptr);
	    return true;
	  }
	  else {
	    gen D=sqrt_noabs(b*b-gen(4)*a*c,contextptr);
	    gen sD=sign(D,contextptr);
	    if (is_minus_one(sD)){
	      D=-D;
	      sD=1;
	    }
	    /*
	      ( *	D=sqrt(b^2-4ac)                                    * )
	      ( * 	a<0 and D>0 ->	x=[D*2u/[1+u^2]-b]/2a		   * )
	      ( *			u=[D-2*sqrt[-a]*sqrt[y]]/[2ax+b]   * )
	      ( *			dx/sqrt[y]=-2*du/[sqrt[-a]*[1+u^2]] * )
	    */
	    gen sqrta(sqrt(-a,contextptr));
	    identificateur id_u(" u");
	    gen u(id_u),uu(u);
	    gen uasx=rdiv(D-plus_two*sqrta*sqrt(argument,contextptr),plus_two*a*gen_x+b,contextptr);
	    tmpe=ratnormal(e*sqrt(argument,contextptr),contextptr);
	    tmpe=complex_subst(tmpe,gen_x,rdiv(rdiv(plus_two*u*D,1+u*u,contextptr)-b,plus_two*a,contextptr),contextptr);
	    tmpe=-rdiv(plus_two,sqrta,contextptr)*tmpe/(1+u*u);
	    tmpres=integrate_rational(tmpe,u,tmprem,uu,intmode,contextptr);
	    // sqrt(a*x^2+b*x+c) -> a*[(x+b/2/a)^2-(D/a)^2]
	    // -> asin(a*x+b/2)
	    vecteur vin(makevecteur(u,symbolic(at_atan,u))),vout(makevecteur(uasx,inv(-2,contextptr)*sD*asin(ratnormal((-2*a*gen_x-b)/D,contextptr),contextptr)));
	    remains_to_integrate=remains_to_integrate+complex_subst(tmprem,vin,vout,contextptr);
	    res=alpha+complex_subst(tmpres,vin,vout,contextptr);
	    return true;
	  }
	} // end sqrt of quadratic
	else {
	  remains_to_integrate=e;
	  res=alpha;
	  return true;
	}
      } // end if d==2
    } // end exposant=fraction of integers
    return false;
  } // end recusive var size==2 i.e. of integrate_sqrt 

  static gen integrate_piecewise(gen& e,const gen & piece,const gen & gen_x,gen & remains_to_integrate,GIAC_CONTEXT,int intmode){
    gen & piecef=piece._SYMBptr->feuille;
    if (piecef.type!=_VECT){
      e=subst(e,piece,piecef,false,contextptr);
      return integrate_id_rem(e,gen_x,remains_to_integrate,contextptr,intmode);
    }
    vecteur piecev=*piecef._VECTptr,remainsv(piecev);
    int nargs=int(piecev.size());
    bool addremains=false;
    for (int i=0;i<nargs/2;++i){
      remainsv[2*i+1]=0;
      gen tmp=subst(e,piece,piecev[2*i+1],false,contextptr);
      piecev[2*i+1]=integrate_id_rem(tmp,gen_x,remainsv[2*i+1],contextptr,intmode);
      addremains = addremains || !is_zero(remainsv[2*i+1]);
    }
    if (nargs%2){
      remainsv[nargs-1]=0;
      gen tmp=subst(e,piece,piecev[nargs-1],false,contextptr);
      piecev[nargs-1]=integrate_id_rem(tmp,gen_x,remainsv[nargs-1],contextptr,intmode);
      addremains = addremains || !is_zero(remainsv[nargs-1]);
    }
    if (addremains)
      remains_to_integrate=symbolic(at_piecewise,gen(remainsv,_SEQ__VECT));
    return symbolic(at_piecewise,gen(piecev,_SEQ__VECT));
    // FIXME: make the antiderivative continuous
  }

  static gen integrate_trig_fraction(gen & e,const gen & gen_x,vecteur & var,const gen & coeff_trig,int trig_fraction,gen& remains_to_integrate,int intmode,GIAC_CONTEXT){
    const_iterateur vart=var.begin(),vartend=var.end();
    vecteur substout;
    gen a,b,coeff_cst;
    is_linear_wrt(vart->_SYMBptr->feuille,gen_x,a,b,contextptr);
    coeff_cst=ratnormal(rdiv(a,coeff_trig,contextptr),contextptr)*b;
    // express all angles in vart as n*(coeff_trig*x+coeff_cst)+angle=a*x+b, 
    // t=coeff_trig*x+coeff_cst
    for (;vart!=vartend;++vart){
      is_linear_wrt(vart->_SYMBptr->feuille,gen_x,a,b,contextptr);
      gen n=ratnormal(rdiv(a,coeff_trig,contextptr),contextptr);
      if (n.type!=_INT_) return gensizeerr(gettext("trig_fraction"));
      gen angle=ratnormal(b-n*coeff_cst,contextptr);
      substout.push_back(symbolic(vart->_SYMBptr->sommet,n*gen_x+angle));
    }
    gen f=complex_subst(e,var,substout,contextptr); // should be divided by coeff_trig
    f=_texpand(f,contextptr);
    gen tmprem,tmpres;
    if (trig_fraction==4){ // everything depends on exp(x)
      f=complex_subst(f,exp(gen_x,contextptr),gen_x,contextptr)*inv(gen_x,contextptr);
      if ( (intmode &2)==0)
	gprintf(step_ratfracexp,gettext("Integrate rational fraction of exponential %gen by %gen change of variable, leading to integral of %gen"),makevecteur(e,exp(gen_x,contextptr),f),contextptr);
      tmpres=linear_integrate_nostep(f,gen_x,tmprem,intmode,contextptr);
      gen expx=exp(coeff_trig*gen_x+coeff_cst,contextptr);
      if ( (intmode & 2)==0)
	gprintf(step_backsubst,gettext("Back substitution %gen->%gen in %gen"),makevecteur(gen_x,expx,tmprem),contextptr);
      remains_to_integrate = inv(coeff_trig,contextptr)*complex_subst(tmprem,gen_x,expx,contextptr);
      return inv(coeff_trig,contextptr)*complex_subst(tmpres,gen_x,expx,contextptr);
    }
    f=halftan(f,contextptr); // now everything depends on tan(x/2)
    // t=tan(x/2), dt=1/2(1+t^2)*dx
    gen xsur2=rdiv(coeff_trig*gen_x+coeff_cst,plus_two,contextptr);
    gen tanxsur2=tan(xsur2,contextptr);
    f=complex_subst(f,tan(rdiv(gen_x,plus_two,contextptr),contextptr),gen_x,contextptr)*inv(plus_one+pow(gen_x,2),contextptr);
    if ( (intmode &2)==0)
      gprintf(step_ratfractrig,gettext("Integrate rational fraction of trigonometric %gen by %gen change of variable, leading to integral of %gen"),makevecteur(e,tanxsur2,f),contextptr);
    vecteur vf(1,gen_x);
    rlvarx(f,gen_x,vf);
    if (vf.size()<=1)
      tmpres=integrate_rational(f,gen_x,tmprem,tanxsur2,intmode,contextptr);
    else {
      tmpres=linear_integrate_nostep(f,gen_x,tmprem,intmode,contextptr);
      if ( (intmode & 2)==0)
	gprintf(step_backsubst,gettext("Back substitution %gen->%gen in %gen"),makevecteur(gen_x,tanxsur2,tmpres),contextptr);
      tmpres=complex_subst(tmpres,gen_x,tanxsur2,contextptr);
      // tmprem=complex_subst(tmprem,gen_x,tanxsur2,contextptr);
    }
    if (tmpres==0)
      remains_to_integrate = e;
    else
      remains_to_integrate = rdiv(plus_two,coeff_trig,contextptr)*tmprem*(1+pow(tanxsur2,2));
    return rdiv(plus_two,coeff_trig,contextptr)*tmpres;
  }

  // reduce g, a rational fraction wrt to x, to a sqff lnpart
  // and adds the non sqff integrated part to ratpart
  bool intgab_ratfrac(const gen & e,const gen & x,gen & value,GIAC_CONTEXT){
    vecteur l;
    l.push_back(x); // insure x is the main var
    l=vecteur(1,l);
    alg_lvar(e,l);
    int s=int(l.front()._VECTptr->size());
    if (!s){
      l.erase(l.begin());
      s=int(l.front()._VECTptr->size());
    }
    if (!s)
      return false;
    gen r=e2r(e,l,contextptr);
    gen r_num,r_den;
    fxnd(r,r_num,r_den);
    if (r_num.type==_EXT)
      return false;
    polynome num(s);
    if (r_num.type==_POLY)
      num=*r_num._POLYptr;
    else
      num=polynome(r_num,s);
    if (r_den.type!=_POLY){ // not convergent
      if (num.lexsorted_degree()%2)
	value=undef;
      else
	value=subst(r2e(r_num,l,contextptr),x,1,false,contextptr)/r2e(r_den,l,contextptr)*plus_inf;
      return true;
    }
    polynome den(*r_den._POLYptr);
    if (num.lexsorted_degree()>den.lexsorted_degree()-2){ // not convergent
      if ( (num.lexsorted_degree()-den.lexsorted_degree())%2 )
	value=undef;
      else
	value=subst(r2e(r_num,l,contextptr)/r2e(r_den,l,contextptr),x,1,false,contextptr)*plus_inf;
      return true;
    }
    l.front()._VECTptr->front()=x;
    vecteur lprime(l);
    if (lprime.front().type!=_VECT){ 
      value=gensizeerr(gettext("in intgab_rational"));
      return false;
    }
    lprime.front()=cdr_VECT(*(lprime.front()._VECTptr));
    // quick check for length 2 deno
    vecteur vtmp;
    polynome2poly1(den,1,vtmp);
    if (integrate_deno_length_2(num,vtmp,l,lprime,value,true,2/* no step info*/,contextptr)){
      value=ratnormal(value,contextptr)*cst_pi;
      return true;
    }
    polynome p_content(lgcd(den));
    factorization vden(sqff(den/p_content)); // first square-free factorization
    vector< pf<gen> > pfde_VECT;
    polynome ipnum(s),ipden(s),temp(s),tmp(s);
    partfrac(num,den,vden,pfde_VECT,ipnum,ipden);
    vector< pf<gen> >::iterator it=pfde_VECT.begin();
    vector< pf<gen> >::const_iterator itend=pfde_VECT.end();
    vector< pf<gen> > intdecomp,finaldecomp;
    for (;it!=itend;++it){
      pf<gen> single(intreduce_pf(*it,intdecomp,true));
      // Now final factorization for single.den, 
      // then compute single.num/single.den'(root) for roots with im>0
      // this is the residue
      // Example 1/(x^4+1) roots in C^+: exp(i*pi/4), exp(3*i*pi/4),
      // num/den'=1/4/x^3=-x/4 -> -1/4*exp(i*pi/4)-1/4*exp(3*i*pi/4)
      // -> -1/2*sin(pi/4)*i  [*2*i*pi -> sqrt(2)/2*pi]
      vden.clear();
      gen extra_div=1;
      factor(single.den,p_content,vden,false,false,false,1,extra_div);
      partfrac(single.num,single.den,vden,finaldecomp,temp,tmp);
    }
    it=finaldecomp.begin();
    itend=finaldecomp.end();
    gen lnpart(0),deuxaxplusb,sqrtdelta;
    polynome a(s),b(s),c(s);
    polynome d(s),E(s),lnpartden(s);
    polynome delta(s),atannum(s),alpha(s);
    for (;it!=itend;++it){
      int deg=it->fact.lexsorted_degree();
      // polynome & itnum=it->num;
      // polynome & itden=it->den;
      gen Delta;
      switch (deg) { 
      case 1: // 1st order
	value=undef;
	return true;
      case 2: // 2nd order
	findabcdelta(it->fact,a,b,c,delta);
	Delta=r2e(delta,lprime,contextptr);
	if (is_positive(Delta,contextptr)){
	  value=undef;
	  return true;
	}
	alpha=(it->den/it->fact).trunc1()*a*gen(2);
	findde(it->num,d,E);
	atannum=a*E*gen(2)-b*d;
	atannum=atannum*gen(2);
	simplify(atannum,alpha);
	sqrtdelta=normalize_sqrt(sqrt(-Delta,contextptr),contextptr);
	value += rdiv(r2e(atannum,lprime,contextptr),(r2e(alpha,lprime,contextptr))*sqrtdelta,contextptr);
	break; 
      default: // divide a*it->num =b*it->den.derivative()+c 
	it->num.TPseudoDivRem(it->den.derivative(),b,c,a);
	// remaining pf
	if (!c.coord.empty()){
	  vtmp=polynome2poly1(a*it->den,1);
	  if (!integrate_deno_length_2(c,vtmp,l,lprime,value,true,2/* no step info*/,contextptr))
	    return false;
	}
	break ;
      }
    }
    value=ratnormal(value,contextptr)*cst_pi;
    return true;
  }

  static gen integrate_rational_end(vector< pf<gen> >::iterator & it,vector< pf<gen> >::const_iterator & itend,const gen & x,const gen & xvar,const vecteur & l,const vecteur & lprime,const polynome & ipnum,const polynome & ipden,const gen & ratpart,gen & remains_to_integrate,int intmode,GIAC_CONTEXT){
    gen lnpart(0),deuxaxplusb,sqrtdelta;
    int s=ipnum.dim;
    polynome a(s),b(s),c(s);
    polynome d(s),E(s),lnpartden(s);
    polynome delta(s),atannum(s),alpha(s);
    bool uselog;
    remains_to_integrate=0;
    for (;it!=itend;++it){
      int deg=it->fact.lexsorted_degree();
      // polynome & itnum=it->num;
      // polynome & itden=it->den;
      gen Delta;
      switch (deg) { 
      case 1: // 1st order
	lnpart=lnpart+rdiv(r2e(it->num,l,contextptr),r2e(firstcoeff(it->den),l,contextptr),contextptr)*lnabs2(r2e(it->fact,l,contextptr),xvar,contextptr);
	break; 
      case 2: // 2nd order
	findabcdelta(it->fact,a,b,c,delta);
	Delta=r2e(delta,lprime,contextptr);
	uselog=is_positive(Delta,contextptr);
	alpha=(it->den/it->fact).trunc1()*a*gen(2);
	findde(it->num,d,E);
	atannum=a*E*gen(2)-b*d;
	// ln part d/alpha*ln(fact)
	lnpartden=alpha;
	simplify(d,lnpartden);
	lnpart=lnpart+rdiv(r2e(d,lprime,contextptr),r2e(lnpartden,lprime,contextptr),contextptr)*gen(uselog?lnabs2(r2e(it->fact,l,contextptr),xvar,contextptr):symbolic(at_ln,r2e(it->fact,l,contextptr)));
	// atan or _FUNCnd ln part
	deuxaxplusb=r2e(it->fact.derivative(),l,contextptr);
	if (uselog){
	  sqrtdelta=normalize_sqrt(sqrt(Delta,contextptr),contextptr);
	  simplify(atannum,alpha);
	  lnpart=lnpart+rdiv(r2e(atannum,lprime,contextptr),(r2e(alpha,lprime,contextptr))*sqrtdelta,contextptr)*lnabs2(rdiv(deuxaxplusb-sqrtdelta,deuxaxplusb+sqrtdelta,contextptr),xvar,contextptr);
	}
	else {
	  vecteur v=solve(x*x+Delta,x,0,contextptr);
	  if (v.size()==2 && !is_undef(v[0]) && !is_undef(v[1])){
	    if (is_positive(-v[0],contextptr))
	      sqrtdelta=v[1];
	    else
	      sqrtdelta=v[0];
	  }
	  else
	    sqrtdelta=normalize_sqrt(sqrt(-Delta,contextptr),contextptr);
	  atannum=atannum*gen(2);
	  simplify(atannum,alpha);
	  gen tmpatan=ratnormal(rdiv(deuxaxplusb,sqrtdelta,contextptr),contextptr);
	  gen residue;
	  if (tmpatan.is_symb_of_sommet(at_tan))
	    tmpatan=tmpatan._SYMBptr->feuille;
	  else {
	    // avoid floor if possible
	    // atan(beta*tan(theta)+gamma)+floor() for beta>0 and gamma>-1
	    // -> atan( cos(theta)*((beta-1)*sin(theta)+gamma*cos(theta))/
	    //          (cos(theta)^2+beta*sin(theta)^2+gamma*sin(theta)*cos()) )
	    gen beta,gamma;
	    if (  //0 && 
		  xvar.is_symb_of_sommet(at_tan) && is_linear_wrt(tmpatan,xvar,beta,gamma,contextptr) && is_strictly_greater(4*beta,gamma*gamma,contextptr) ){
	      gen argtan=ratnormal(2*xvar._SYMBptr->feuille,contextptr);
	      gen si=symbolic(at_sin,argtan),ci=symbolic(at_cos,argtan);
	      tmpatan=symbolic(at_atan,ratnormal(((beta-1)*si+gamma*(1+ci))/(1+beta+gamma*si+(1-beta)*ci),contextptr));
	      residue=xvar._SYMBptr->feuille;
	    }
	    else {
	      tmpatan=atan(tmpatan,contextptr);
	      if (xvar.is_symb_of_sommet(at_tan)){
		if (do_lnabs(contextptr)){
		  // add residue
		  residue=r2e(it->fact.derivative().derivative(),l,contextptr);
		  residue=cst_pi*sign(residue,contextptr)*_floor((xvar._SYMBptr->feuille/cst_pi+plus_one_half),contextptr);
		}
	      }
	      else {
		// if xvar has a singularity at 0 e.g. xvar =x+1/x or x-1/x, 
		// add the residue at 0
		if (xvar.type!=_IDNT){
		  // replacing tmpatan by atan(inv(tmpatan)) would avoid residue for int((x^2+1)/(x^4+3x^2+1)); but then it would not be continuous at 1 and -1
		  residue=ratnormal(limit(tmpatan,*x._IDNTptr,0,-1,contextptr)-limit(tmpatan,*x._IDNTptr,0,1,contextptr),contextptr);
		  residue=residue*sign(x,contextptr)/2;
		}
	      }
	    }
	  }
	  if (!angle_radian(contextptr)){
	    if (angle_degree(contextptr))
	      tmpatan=tmpatan*deg2rad_e;
	    //grad
	    else
	      tmpatan = tmpatan*grad2rad_e;
	  }
	  tmpatan += residue;
	  lnpart=lnpart+rdiv(r2e(atannum,lprime,contextptr),(r2e(alpha,lprime,contextptr))*sqrtdelta,contextptr)*tmpatan;
	} // end else uselof
	break; 
      default: // divide a*it->num =b*it->den.derivative()+c 
	it->num.TPseudoDivRem(it->den.derivative(),b,c,a);
	// remaining pf
	if (!c.coord.empty()){
	  vecteur vtmp=polynome2poly1(a*it->den,1);
	  if (!integrate_deno_length_2(c,vtmp,l,lprime,lnpart,false,intmode,contextptr))
	    remains_to_integrate += r2sym(vector< pf<gen> >(1,pf<gen>(c,a*it->den,it->fact,1)),l,contextptr);
	}
	// extract log part b/a*ln[fact]
	simplify(b,a);
	if (!is_zero(b))
	  lnpart=lnpart+rdiv(r2e(b,l,contextptr),r2e(a,l,contextptr),contextptr)*lnabs(r2e(it->fact,l,contextptr),contextptr);
	break ;
      }
    }
    return rdiv(r2e(ipnum.integrate(),l,contextptr),r2e(ipden,l,contextptr),contextptr)+ratpart+lnpart;
  }

  // integration of a rational fraction
  static gen integrate_rational(const gen & e, const gen & x, gen & remains_to_integrate,gen & xvar,int intmode,GIAC_CONTEXT){
    if (x.type!=_IDNT) return gensizeerr(contextptr); // see limit
    if (has_num_coeff(e)){
      gen ee=exact(e,contextptr);
      if (!has_num_coeff(ee)){
	ee=integrate_rational(ee,x,remains_to_integrate,xvar,intmode,contextptr);
	ee=evalf(ee,1,contextptr);
	remains_to_integrate=evalf(remains_to_integrate,1,contextptr);
	return ee;
      }
    }
    const vecteur & varx=lvarx(e,x);
    int varxs=int(varx.size());
    if (!varxs){
      remains_to_integrate=zero;
      return e*xvar;
    }
    if ( (varxs>1) || (varx.front()!=x) ) {
      remains_to_integrate = e;
      return zero;
    }
    vecteur l;
    l.push_back(x); // insure x is the main var
    l=vecteur(1,l);
    alg_lvar(e,l);
    vecteur l_orig=l;
    int s=int(l.front()._VECTptr->size());
    if (!s){
      l.erase(l.begin());
      s=int(l.front()._VECTptr->size());
    }
    if (!s)
      return gensizeerr(contextptr);
    vecteur lprime(l);
    if (lprime.front().type!=_VECT) return gensizeerr(gettext("in integrate_rational"));
    lprime.front()=cdr_VECT(*(lprime.front()._VECTptr));
    gen r=e2r(e,l,contextptr);
    // cout << "Int " << r << endl;
    gen r_num,r_den;
    fxnd(r,r_num,r_den);
    if (r_num.type==_EXT){
      remains_to_integrate=e;
      return zero;
    }
    if (r_den.type!=_POLY){
      l.front()._VECTptr->front()=xvar;
      if (r_num.type==_POLY)
	return rdiv(r2e(r_num._POLYptr->integrate(),l,contextptr),r2sym(r_den,l,contextptr),contextptr);
      else 
	return e*xvar;
    }
    polynome den(*r_den._POLYptr),num(s);
    if (r_num.type==_POLY)
      num=*r_num._POLYptr;
    else
      num=polynome(r_num,s);
    // cyclotomic-like polys
    vecteur vtmp;
    if (den.coord.size()==2 && den.lexsorted_degree()!=1 && num.lexsorted_degree() < den.lexsorted_degree() && den.coord.back().index.is_zero() && xvar.type==_IDNT){
      polynome2poly1(den,1,vtmp);
      r=0;
      if (!integrate_deno_length_2(num,vtmp,l,lprime,r,false,intmode,contextptr))
	remains_to_integrate=e;
      return r;
    }
    // check for a t=x^aa change of variable: a divides deg(num)+1-deg(den)
    // as well as all differences of degrees in the poly num and den
    int den_deg=den.lexsorted_degree(),num_deg=num.lexsorted_degree();
    int aa=num_deg+1-den_deg,precedent,actuel;
    vector< monomial<gen> > ::const_iterator num_it=num.coord.begin(),num_itend=num.coord.end();
    if (num_itend-num_it>1){
      precedent=num_it->index.front(); 
      ++num_it;
      for (;num_it!=num_itend;++num_it){
	actuel=num_it->index.front();
	aa=gcd(aa,actuel-precedent);
	precedent=actuel;
      }
    }
    num_it=den.coord.begin(),num_itend=den.coord.end();
    if (num_itend-num_it>1){
      precedent=num_it->index.front(); 
      ++num_it;
      for (;num_it!=num_itend;++num_it){
	actuel=num_it->index.front();
	aa=gcd(aa,actuel-precedent);
	precedent=actuel;
      }
    }
    if (!aa)
      aa=den_deg;
    if (aa>1){ // Apply
      if ( (intmode & 2)==0)
	gprintf(step_ratfracpow,gettext("Integrate rational fraction %gen, change of variable %gen"),makevecteur(e,symb_equal(x,symb_pow(x,aa))),contextptr);
      int k=0;
      k=den_deg % aa;
      // shift num and den by x^k
      index_t k1(num.dim);
      k1.front()=k+1;
      num=(num.shift(k1)).dividedegrees(aa);
      index_t ka(num.dim);
      ka.front()=k+aa;
      den=gen(aa)*(den.shift(ka)).dividedegrees(aa);
      if (!(aa%2) && xvar.is_symb_of_sommet(at_tan)){
	// t=tan(x)^2: c=cos(2x)=(1-t^2)/(1+t^2) -> t^2=(1-c)/(1+c)
	xvar=symbolic(at_cos,ratnormal(2*xvar._SYMBptr->feuille,contextptr));
	xvar=(1-xvar)/(1+xvar);
	aa/=2;
      }
      xvar=pow(xvar,aa);
      return integrate_rational(r2e(fraction(num,den),l,contextptr),x,remains_to_integrate,xvar,intmode,contextptr);
    }
    int den_val=den.valuation(0),num_val=num.valuation(0);
    if (den_deg+den_val==num_deg+num_val+2){
      // now detect pattern that simplifies trig fraction integration
      /* cos is already detected by x^aa above with aa=2
       *
       * sin: if the fraction, including dt, is invariant by t->v=1/t 
       * e.g (1-t^2)/(1+t^2)^2 dt = v^2(v^2-1)/(v^2+1)^2*( -1/v^2) dv
       * or [equivalent] tF(t) must change sign
       * let u=t+1/t, du=(1-1/t^2)dt, F(t)dt= F(t) * t^2/(t^2-1) du
       * e.g. t^2/(1+t^2)^2 du
       * N/(t^2-1) and D must be symm
       * 
       * tan: if the fraction, including dt, is invariant by t->-1/t 
       * u=t-1/t, du=(1+1/t^2)dt, F(t)dt= F(t) * t^2/(t^2+1) du
       * N/(t^2+1) and D must be antism.
       */
      vecteur Nsave,Dsave,N,D,test(makevecteur(1,0,-1)),q,r;
      polynome2poly1(num,1,N);
      if (num_val && num_val<signed(N.size()))
	N=vecteur(N.begin(),N.begin()+N.size()-num_val);
      polynome2poly1(den,1,D);
      if (den_val && den_val<signed(D.size()))
	D=vecteur(D.begin(),D.begin()+D.size()-den_val);
      Nsave=N; Dsave=D;
      bool type=false;
      if (N.size()>=test.size() && DivRem(N,test,0,q,r) && r.empty()){
	r=D;
	if (is_symmetric(q,N,true)*is_symmetric(r,D,true)==1){
	  // yes!
	  type = (xvar.type==_SYMB) || D.size()>1;
	}
      }
      if (!type) {
	N=Nsave; D=Dsave;
      }
      if (!type && D.size()>=test.size() && DivRem(D,test,0,q,r) && r.empty()){
	r=N;
	if (is_symmetric(r,N,true)*is_symmetric(q,D,true)==1){
	  // yes!
	  type = (xvar.type==_SYMB) || D.size()>1;
	  if (type) 
	    D=operator_times(D,makevecteur(1,0,-4),0);
	}
      }
      if (type){
	if (xvar.is_symb_of_sommet(at_tan)){
	  q=N; r=D;
	  xtoinvx(2,q,r,N,D,true);
	  xvar=symbolic(at_sin,ratnormal(2*xvar._SYMBptr->feuille,contextptr));
	}
	else {
	  xvar=xvar+inv(xvar,contextptr);
	}
	num=poly12polynome(N,1,num.dim);
	den=poly12polynome(D,1,den.dim);
	return integrate_rational(r2e(fraction(num,den),l,contextptr),x,remains_to_integrate,xvar,intmode,contextptr);
      }
      test[2]=1;
      N=Nsave; D=Dsave;
      if (N.size()>=test.size() && DivRem(N,test,0,q,r) && r.empty()){
	r=D;
	if (is_symmetric(q,N,false)*is_symmetric(r,D,false)==1){
	  // yes!
	  type = (xvar.type==_SYMB) || D.size()>1;
	}
      }
      if (!type){
	N=Nsave; D=Dsave;
      }
      if (!type && D.size()>=test.size() && DivRem(D,test,0,q,r) && r.empty()){
	r=N;
	if (is_symmetric(r,N,false)*is_symmetric(q,D,false)==1){
	  // yes!
	  type = (xvar.type==_SYMB && xvar._SYMBptr->sommet!=at_tan) || D.size()>1;
	  if (type)
	    D=operator_times(D,makevecteur(1,0,4),0);
	}
      }
      if (type){
	if (xvar.is_symb_of_sommet(at_tan)){
	  q=N; r=D;
	  xtoinvx(-2,q,r,N,D,true);
	  xvar=symbolic(at_tan,ratnormal(2*xvar._SYMBptr->feuille,contextptr));
	}
	else
	  xvar=xvar-inv(xvar,contextptr);
	num=poly12polynome(N,1);
	den=poly12polynome(D,1);
	for (;den.dim!=num.dim;){
	  vector< monomial<gen> >::iterator dt,dtend;
	  if (den.dim<num.dim){
	    dt=den.coord.begin();
	    dtend=den.coord.end();
	    ++den.dim;
	  }
	  else {
	    dt=num.coord.begin();
	    dtend=num.coord.end();
	    ++num.dim;
	  }
	  for (;dt!=dtend;++dt){
	    index_t::const_iterator it=dt->index.begin(),itend=dt->index.end();
	    index_m new_i(itend-it+1);
	    index_t::iterator newit=new_i.begin();    
	    for (;it!=itend;++newit,++it)
	      *newit=*it;
	    *newit=0;
	    dt->index=new_i;
	  }
	}
	simplify(num,den);
	return integrate_rational(r2e(fraction(num,den),l,contextptr),x,remains_to_integrate,xvar,intmode,contextptr);
      }
    }
    if ( (intmode & 2)==0)
      gprintf(step_ratfracsqrfree,gettext("Integrate rational fraction %gen"),makevecteur(_sqrfree(e,contextptr)),contextptr);
    vecteur lf=*l.front()._VECTptr;
    lf.front()=xvar;
    l.front()=lf;
    // l.front()._VECTptr->front()=xvar;
    polynome p_content(lgcd(den));
    factorization vden(sqff(den/p_content)); // first square-free factorization
    vector< pf<gen> > pfdecomp;
    polynome ipnum(s),ipden(s),temp(s),tmp(s);
    partfrac(num,den,vden,pfdecomp,ipnum,ipden);
    vector< pf<gen> >::iterator it=pfdecomp.begin();
    vector< pf<gen> >::const_iterator itend=pfdecomp.end();
    vector< pf<gen> > intdecomp,finaldecomp;
    for (;it!=itend;++it){
      const pf<gen> & single =intreduce_pf(*it,intdecomp);
      if ( (it->mult>1) && (intmode & 2)==0){
	gen fact1=pow(r2e(it->fact,l,contextptr),it->mult,contextptr);
	gen fact2=it->den/pow(it->fact,it->mult);
	gprintf(step_ratfrachermite,gettext("Partial fraction %gen -> Hermite reduction -> integrate squarefree part %gen"),makevecteur(inv(r2sym(fact2,l,contextptr),contextptr)*r2e(it->num,l,contextptr)/fact1,r2e(single.num,l,contextptr)/r2e(single.den,l,contextptr)),contextptr);
      }
      // factor(single.den,p_content,vden,false,withsqrt(contextptr),complex_mode(contextptr));
      gen extra_div=1;
      factor(single.den,p_content,vden,false,false,false,1,extra_div);
      partfrac(single.num,single.den,vden,finaldecomp,temp,tmp);
    }
    if ( (intmode & 2)==0)
      gprintf(step_ratfracfinal,gettext("Partial fraction integration of %gen"),makevecteur(r2sym(finaldecomp,l_orig,contextptr)),contextptr);
    it=finaldecomp.begin();
    itend=finaldecomp.end();
    gen ratpart=r2sym(intdecomp,l,contextptr);
    // should remove constants in ratpart
    gen tmp1=_fxnd(ratpart,contextptr);
    if (xvar.type==_IDNT && tmp1.type==_VECT && tmp1._VECTptr->size()==2){
      gen tmp2=_quorem(makesequence(tmp1._VECTptr->front(),tmp1._VECTptr->back(),xvar),contextptr);
      if (tmp2.type==_VECT && tmp2._VECTptr->size()==2){
	gen q=tmp2._VECTptr->front(),r=tmp2._VECTptr->back();
	gen C=subst(q,xvar,0,false,contextptr);
	if (!is_zero(C)){
	  q=ratnormal(q-C,contextptr);
	  tmp1=tmp1._VECTptr->back();
	  tmp1=_collect(tmp1,contextptr);
	  tmp1=r*inv(tmp1,contextptr);
	  ratpart=q+tmp1;
	}
      }
    }
    return integrate_rational_end(it,itend,x,xvar,l,lprime,ipnum,ipden,ratpart,remains_to_integrate,intmode,contextptr);
  }

  // integration of e when linear operations have been applied
  static gen xln_x(const gen & x,GIAC_CONTEXT){
    return x*ln(x,contextptr)-x;
  }

  static gen int_exp(const gen & x,GIAC_CONTEXT){
    return exp(x,contextptr);
  }

  static gen int_sinh(const gen & x,GIAC_CONTEXT){
    return cosh(x,contextptr);
  }

  static gen int_cosh(const gen & x,GIAC_CONTEXT){
    return sinh(x,contextptr);
  }

  static gen int_sin(const gen & x,GIAC_CONTEXT){
    if (angle_radian(contextptr))
      return -cos(x,contextptr);
    else if(angle_degree(contextptr))
      return -cos(x,contextptr)*gen(180)/cst_pi;
    //grad
    else
      return -cos(x, contextptr)*gen(200) / cst_pi;
  }

  static gen int_cos(const gen & x,GIAC_CONTEXT){
    if (angle_radian(contextptr))
      return sin(x,contextptr);
    else if(angle_degree(contextptr))
      return sin(x,contextptr)*gen(180)/cst_pi;
    //grad
    else
      return sin(x, contextptr)*gen(200) / cst_pi;
  }

  static gen int_tan(const gen & x,GIAC_CONTEXT){
    gen g=-lnabs(cos(x,contextptr),contextptr);
    if (angle_radian(contextptr))
      return g;
    else if(angle_degree(contextptr))
      return g*gen(180)/cst_pi;
    //grad
    else
      return g*gen(200) / cst_pi;
  }

  static gen int_tanh(const gen & x,GIAC_CONTEXT){
    return -ln(cosh(x,contextptr),contextptr);
  }

  static gen int_asin(const gen & x,GIAC_CONTEXT){
    if (angle_radian(contextptr))
      return x*asin(x,contextptr)+sqrt(1-pow(x,2),contextptr);
    else if(angle_degree(contextptr))
      return x*asin(x,contextptr)*deg2rad_e+sqrt(1-pow(x,2),contextptr);
    //grad
    else
      return x*asin(x, contextptr)*grad2rad_e + sqrt(1 - pow(x, 2), contextptr);
  }

  static gen int_acos(const gen & x,GIAC_CONTEXT){
    if (angle_radian(contextptr))
      return x*acos(x,contextptr)-sqrt(1-pow(x,2),contextptr);
    else if(angle_degree(contextptr))
      return x*acos(x,contextptr)*deg2rad_e-sqrt(1-pow(x,2),contextptr);
    //grad
    else
      return x*acos(x, contextptr)*grad2rad_e - sqrt(1 - pow(x, 2), contextptr);
  }

  static gen int_atan(const gen & x,GIAC_CONTEXT){
    if (angle_radian(contextptr)) 
      return x*atan(x,contextptr)-rdiv(ln(pow(x,2)+1,contextptr),plus_two,contextptr);
    else if(angle_degree(contextptr))
      return x*atan(x,contextptr)*deg2rad_e-rdiv(ln(pow(x,2)+1,contextptr),plus_two,contextptr);
    //grad
    else
      return x*atan(x, contextptr)*grad2rad_e - rdiv(ln(pow(x, 2) + 1, contextptr), plus_two, contextptr);
  }

  static gen int_asinh(const gen & x,GIAC_CONTEXT){
    return x*asinh(x,contextptr)-sqrt(pow(x,2)+1,contextptr);
  }

  static gen int_acosh(const gen & x,GIAC_CONTEXT){
    return x*acosh(x,contextptr)-sqrt(pow(x,2)-1,contextptr);
  }

  static gen int_atanh(const gen & x,GIAC_CONTEXT){
    return x*atan(x,contextptr)-rdiv(ln(abs(pow(x,2)-1,contextptr),contextptr),plus_two,contextptr);
  }

  static const gen_op_context primitive_tab_primitive[]={giac::int_sin,giac::int_cos,giac::int_tan,giac::int_exp,giac::int_sinh,giac::int_cosh,giac::int_tanh,giac::int_asin,giac::int_acos,giac::int_atan,giac::xln_x,giac::int_asinh,giac::int_acosh,giac::int_atanh};

#if 0
  static void insure_real_deno(gen & n,gen & d,GIAC_CONTEXT){
    gen i=im(d,contextptr),c=conj(d,contextptr);
    if (!is_zero(i)){
      n=n*c;
      d=d*c;
    }
  }
#endif

  static bool in_is_rewritable_as_f_of(const gen & fu,const gen & u,gen & fx,const gen & gen_x,GIAC_CONTEXT){
    if (fu.type==_VECT){
      vecteur res;
      const_iterateur it=fu._VECTptr->begin(),itend=fu._VECTptr->end();
      gen tmp;
      for (;it!=itend;++it){
	if (!is_rewritable_as_f_of(*it,u,tmp,gen_x,contextptr))
	  return false;
	res.push_back(tmp);
      }
      fx=gen(res,fu.subtype);
      return true;
    }
    if (fu.type==_IDNT){
      if (fu!=gen_x){
	fx=fu;
	return true;
      }
      return false;
    }
    if (fu.type!=_SYMB){
      fx=fu;
      return true;
    }
    // symbolic
    if (fu==u){
      fx=gen_x;
      return true;
    }
    // decompose
    unary_function_ptr s=fu._SYMBptr->sommet;
    gen f=fu._SYMBptr->feuille,tmpfx;
    if (in_is_rewritable_as_f_of(f,u,tmpfx,gen_x,contextptr)){
      fx=symbolic(s,tmpfx);
      return true;
    }
    // try special treatment for integral powers
    int fexp,uexp;
    if ( (u.type!=_SYMB) || (s!=at_pow) || (f._VECTptr->back().type!=_INT_) )
      return false;
    fexp=f._VECTptr->back().val;
    if ( (u._SYMBptr->sommet==at_pow) && (u._SYMBptr->feuille._VECTptr->back().type==_INT_) && (u._SYMBptr->feuille._VECTptr->front()==f._VECTptr->front()) ){
      uexp=u._SYMBptr->feuille._VECTptr->back().val;
      if (fexp%uexp)
	return false;
      fx=pow(gen_x,fexp/uexp);
      return true;
    }
    // trigonometric fcns to an even power
    f=f._VECTptr->front();
    if ( (fexp %2) || (f.type!=_SYMB) )
      return false;
    fexp=fexp/2;
    int ftrig=equalposcomp(primitive_tab_op,f._SYMBptr->sommet);
    if (!ftrig)
      return false;
    int utrig=equalposcomp(primitive_tab_op,u._SYMBptr->sommet);
    if (utrig==2 && u._SYMBptr->feuille==2*f._SYMBptr->feuille){
      // sin^2/cos^2/tan^2 in terms of cos(2x)
      switch (ftrig){
      case 1: // sin
	fx=(1-gen_x)/2;
	return true;
      case 2: // cos
	fx=(1+gen_x)/2;
	return true;
      case 3:
	fx=(1-gen_x)/(1+gen_x);
	return true;
      }
    }
    if (!utrig || f._SYMBptr->feuille!=u._SYMBptr->feuille)
      return false;
    switch (ftrig){
    case 1: // sin
      switch (utrig){
      case 2: // sin^2=1-cos^2
	fx=pow(1-pow(gen_x,2),fexp);
	return true;
      case 3: // sin^2=1-1/(tan^2+1)
	fx=pow(1-inv(pow(gen_x,2)+1,contextptr),fexp);
	return true;
      default:
	return false;
      }
    case 2: // cos
      switch (utrig){
      case 1: // cos^2=1-sin^2
	fx=pow(1-pow(gen_x,2),fexp);
	return true;
      case 3: // cos^2=1/(tan^2+1)
	fx=pow(pow(gen_x,2)+1,-fexp);
	return true;
      default:
	return false;
      }
    case 3: // tan
      switch (utrig){
      case 1: // tan^2=1/(1-sin^2)-1
	fx=pow(inv(1-pow(gen_x,-2),contextptr)-1,fexp);
	return true;
      case 2: // tan^2=1/cos^2-1
	fx=pow(pow(gen_x,-2)-1,fexp);
	return true;
      default:
	return false;
      }
    }
    return false;
  }

  // try to rewrite fu(x), function of x as a fonction of u(x), if possible 
  // return fx(x) such that fu(x)=fx(u(x))
  // FIXME: should detect u=pow(.,inv(n)) with n integer
  bool is_rewritable_as_f_of(const gen & fu,const gen & u,gen & fx,const gen & gen_x,GIAC_CONTEXT){
    gen a,b;
    if (is_linear_wrt(u,gen_x,a,b,contextptr)){
      fx=complex_subst(fu,gen_x,rdiv(gen_x-b,a,contextptr),contextptr);
      return false;// true?
    }
    // try first if u is a linear expression of something else
    if (u.type==_SYMB){
      if (u._SYMBptr->sommet==at_neg){
	gen tmpu=u._SYMBptr->feuille,tmpfx;
	if (!is_rewritable_as_f_of(fu,tmpu,tmpfx,gen_x,contextptr))
	  return false;
	fx=complex_subst(tmpfx,gen_x,-gen_x,contextptr);
	return true;
      }
      if (u._SYMBptr->sommet==at_pow){
	gen tmpu=u._SYMBptr->feuille,tmpfx;
	if (tmpu.type==_VECT && tmpu._VECTptr->size()==2){
	  gen expo=inv(tmpu._VECTptr->back(),contextptr);
	  tmpu=tmpu._VECTptr->front();
	  if (expo.type==_INT_){ 
	    if (is_linear_wrt(tmpu,gen_x,a,b,contextptr)){
	      fx=complex_subst(fu,gen_x,rdiv(pow(gen_x,expo,contextptr)-b,a,contextptr),contextptr);
	      return true;
	    }
	  }
	}
      }
      gen alpha;
      vecteur non_constant;
      if (u._SYMBptr->sommet==at_prod ){
	if (u._SYMBptr->feuille.type!=_VECT)
	  return is_rewritable_as_f_of(fu,u._SYMBptr->feuille,fx,gen_x,contextptr);
	decompose_prod(*u._SYMBptr->feuille._VECTptr,gen_x,non_constant,alpha,true,contextptr);
	if (non_constant.empty()) return false; // setsizeerr(gettext("in is_rewritable_as_f_of_f"));
	if (!is_one(alpha)){
	  gen tmpu,tmpfx;
	  tmpu=_prod(non_constant,contextptr);
	  if (!is_rewritable_as_f_of(fu,tmpu,tmpfx,gen_x,contextptr))
	    return false;
	  fx=complex_subst(tmpfx,gen_x,rdiv(gen_x,alpha,contextptr),contextptr);
	  return true;
	}
      }
      if (u._SYMBptr->sommet==at_plus){
	if (u._SYMBptr->feuille.type!=_VECT)
	  return is_rewritable_as_f_of(fu,u._SYMBptr->feuille,fx,gen_x,contextptr);
	decompose_plus(*u._SYMBptr->feuille._VECTptr,gen_x,non_constant,alpha,contextptr);
	if (non_constant.empty()) return false; // setsizeerr(gettext("in is_rewritable_as_f_of_f 2"));
	if (!is_zero(alpha)){
	  gen tmpu,tmpfx;
	  tmpu=_plus(non_constant,contextptr);
	  if (!is_rewritable_as_f_of(fu,tmpu,tmpfx,gen_x,contextptr))
	    return in_is_rewritable_as_f_of(fu,u,fx,gen_x,contextptr);;
	  fx=complex_subst(tmpfx,gen_x,gen_x-alpha,contextptr);
	  return true;
	}
      }
    }
    return in_is_rewritable_as_f_of(fu,u,fx,gen_x,contextptr);
  }

  gen firstcoefftrunc(const gen & e){
    if (e.type==_FRAC)
      return fraction(firstcoefftrunc(e._FRACptr->num),firstcoefftrunc(e._FRACptr->den));
    if (e.type==_POLY)
      return firstcoeff(*e._POLYptr).trunc1();
    return e;
  }

  // special version of lvarx that does not remove cst powers
  vecteur lvarxpow(const gen &e,const gen & x){
    const vecteur & v=lvar(e);
    vecteur res;
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (contains(*it,x))
	res.push_back(*it);
    }
    // do lvar again to comprim the first arg of ^
    return lvar(res);
  }

  gen invexptoexpneg(const gen& g,GIAC_CONTEXT){
    if (g.type==_SYMB && g._SYMBptr->sommet==at_exp)
      return exp(-g._SYMBptr->feuille,contextptr);
    else
      return symb_inv(g);
  }

  gen integrate_gen_rem(const gen & e_orig,const gen & x_orig,gen & remains_to_integrate,GIAC_CONTEXT){
    if (x_orig.type!=_IDNT){
      identificateur x(" x");
      gen e=subst(e_orig,x_orig,x,false,contextptr);
      e=integrate_id_rem(e,x,remains_to_integrate,contextptr,0);
      remains_to_integrate=quotesubst(remains_to_integrate,x,x_orig,contextptr);
      return quotesubst(e,x,x_orig,contextptr);
    }
    return integrate_id_rem(e_orig,x_orig,remains_to_integrate,contextptr,0);
  }

  static bool integrate_step0(gen & e,const gen & gen_x,vecteur & l1,vecteur & m1,gen & res,gen & remains_to_integrate,GIAC_CONTEXT,int intmode){
    const identificateur & id_x=*gen_x._IDNTptr;
    vecteur l2,m2,l3,l4;
    const_iterateur it=l1.begin(),itend=l1.end();
    int i=0;
    for (;it!=itend;++it,++i){
      gen tmp=it->_SYMBptr->feuille;
      identificateur tmpi(" s"+print_INT_(i));
      l2.push_back(tmpi*tmp);
      l3.push_back(tmpi);
      l4.push_back(symbolic(at_sign,tmp));
    }
    it=m1.begin(),itend=m1.end();
    for (;it!=itend;++it,++i){
      l1.push_back(*it);
      gen tmp=it->_SYMBptr->feuille;
      identificateur tmpi(" s"+print_INT_(i));
      l2.push_back(tmpi);
      l3.push_back(tmpi);
      l4.push_back(*it);
    }      
    *logptr(contextptr) << gettext("Warning, integration of abs or sign assumes constant sign by intervals (correct if the argument is real):\nCheck ") << l1 << endl;
    e=complex_subst(e,l1,l2,contextptr);
    res=integrate_id_rem(e,gen_x,remains_to_integrate,contextptr,intmode);
    gen resadd;
    if (is_undef(res)) return true;
    // check what happens when si==0
    for (int j=0;j<i;++j){
      gen val=l4[j],a,b,r;
      if (val.is_symb_of_sommet(at_sign)){
	if (is_linear_wrt(val._SYMBptr->feuille,gen_x,a,b,contextptr) && ((has_evalf(a,r,1,contextptr) && has_evalf(b,r,1,contextptr)) || lvar(res)==lidnt(res))){
	  r=-b/a;
	  vecteur l5(l4);
#if 1
	  l5[j]=1;
	  gen limsup=subst(res,l3,l5,false,contextptr);
	  l5[j]=-1;
	  gen liminf=subst(res,l3,l5,false,contextptr);
#else
	  l5[j]=1;
	  bool dolim=l3.size()==1 && l5.size()==1 && l3.front().type==_IDNT;
	  gen limsup=dolim?limit(res,*l3.front()._IDNTptr,l5.front(),0,contextptr):subst(res,l3,l5,false,contextptr);
	  l5[j]=-1;
	  gen liminf=dolim?limit(res,*l3.front()._IDNTptr,l5.front(),0,contextptr):subst(res,l3,l5,false,contextptr);
#endif
	  gen tmp=ratnormal((limit(liminf,id_x,r,-1,contextptr)-limit(limsup,id_x,r,1,contextptr))/2,contextptr)*val;
	  if (is_undef(tmp) || is_inf(tmp))
	    *logptr(contextptr) << gettext("Unable to cancel step at ")+r.print(contextptr) + " of " << limsup << "-" << liminf << endl;
	  else
	    resadd += tmp;
	}
	else
	  *logptr(contextptr) << gettext("Discontinuities at zeroes of ") << val._SYMBptr->feuille << " were not checked" << endl;
      }
    }
    remains_to_integrate=complex_subst(remains_to_integrate,l3,l4,contextptr);
    res=resadd+complex_subst(res,l3,l4,contextptr);
    return true;
  }

  static bool detect_inv_trigln(gen & e,vecteur & rvar,const gen & gen_x,gen & res,gen & remains_to_integrate,bool additional_check,int intmode,GIAC_CONTEXT){
    const_iterateur rvt=rvar.begin(),rvtend=rvar.end();
    for (;rvt!=rvtend;++rvt){
      if (rvt->type!=_SYMB)
	continue;
      int rvtt=equalposcomp(inverse_tab_op,rvt->_SYMBptr->sommet);
      if (!rvtt || rvtt==3 || rvtt==7) // exclude atan and atanh
	continue;
      rvtt=equalposcomp(primitive_tab_op,rvt->_SYMBptr->sommet);
      if (rvtt>7){
	unary_function_ptr inverse_sommet=primitive_tab_op[rvtt-8];
	gen feuille=rvt->_SYMBptr->feuille,a,b;
	if (!is_linear_wrt(feuille,gen_x,a,b,contextptr))
	  continue;
	if (additional_check){
	  // Additionaly check that e is polynomial wrt x
	  identificateur tmpidnt(" t");
	  gen tmpcheck=subst(e,*rvt,tmpidnt,false,contextptr);
	  vecteur vx2(rlvarx(tmpcheck,gen_x));
	  if ( vx2.size()>1)
	    continue;
	  if (vx2.size()){
	    lvar(tmpcheck,vx2);
	    fraction ftemp=sym2r(tmpcheck,vx2,contextptr);
	    if (ftemp.den.type==_POLY && ftemp.den._POLYptr->lexsorted_degree())
	      continue;
	  }
	}
	// make the change of var ln[ax+b]=t -> x=rdiv(exp(t)-b,a)
	gen tmprem,tmpres,tmpe,xt,dxt,sqrtxt;
	xt=rdiv(symbolic(inverse_sommet,gen_x)-b,a,contextptr);
	dxt=derive(xt,gen_x,contextptr);
	if (is_undef(dxt)){
	  res=dxt;
	  return true;
	}
	// should add sqrt(1-.^2)
	vecteur substin(makevecteur(gen_x,*rvt));
	vecteur substout(makevecteur(xt,gen_x));
	if ((rvtt==8 || rvtt==9)){
	  vecteur tmpv=lop(e,at_pow);
	  for (unsigned tmpi=0;tmpi<tmpv.size();++tmpi){
	    gen tmpvi=tmpv[tmpi]._SYMBptr->feuille;
	    if (tmpvi.type==_VECT && tmpvi._VECTptr->size()==2){
	      gen tmpvi0=tmpvi._VECTptr->front();
	      if (ratnormal(tmpvi0-1+pow(a*gen_x+b,2,contextptr),contextptr)==0){
		substin.push_back(tmpv[tmpi]);
		substout.push_back(pow(symbolic(rvtt==8?at_cos:at_sin,gen_x),2*tmpvi._VECTptr->back(),contextptr));
	      }
	    }
	  }
	}
	tmpe=ratnormal(complex_subst(e,substin,substout,contextptr)*dxt,contextptr);
	if ( (intmode & 2)==0)
	  gprintf(step_ratfracchgvar,gettext("Integrate %gen, change of variable %gen->%gen, new integral %gen"),makevecteur(e,gen_x,xt,tmpe),contextptr);
	tmpres=linear_integrate_nostep(tmpe,gen_x,tmprem,intmode,contextptr);
	if ( (intmode & 2)==0)
	  gprintf(step_backsubst,gettext("Back substitution %gen->%gen in %gen"),makevecteur(gen_x,*rvt,tmpres),contextptr);
	remains_to_integrate=complex_subst(rdiv(tmprem,dxt,contextptr),gen_x,*rvt,contextptr);
	// replace tan(asin/2) or tan(acos/2) and cos(asin) and sin(acos)
	if ((rvtt==8 || rvtt==9) && has_op(tmpres,*at_tan))
	  tmpres=tan2sincos2(tmpres,contextptr);
	tmpres=_texpand(tmpres,contextptr);
	res=complex_subst(tmpres,substout,substin,contextptr);
	return true;
      }
    }
    return false;
  }

  gen integrate_id_rem(const gen & e_orig,const gen & gen_x,gen & remains_to_integrate,GIAC_CONTEXT){
    return integrate_id_rem(e_orig,gen_x,remains_to_integrate,contextptr,0);
  }

  gen add_lnabs(const gen & g,GIAC_CONTEXT){
    return symbolic(at_ln,abs(g,contextptr));
  }

  void surd2pow(const gen & e,vecteur & subst1,vecteur & subst2,GIAC_CONTEXT){
    vecteur l1surd(lop(e,at_surd));
    vecteur l2surd(l1surd);
    for (unsigned i=0;i<l1surd.size();++i){
      gen & g=l2surd[i];
      if (g._SYMBptr->feuille.type==_VECT && g._SYMBptr->feuille._VECTptr->size()==2){
	vecteur gv=*g._SYMBptr->feuille._VECTptr;
	gv=makevecteur(gv[0],inv(gv[1],contextptr));
	g=_pow(gen(gv,_SEQ__VECT),contextptr);//symbolic(at_pow,gen(gv,_SEQ__VECT));
      }
    }
    vecteur l1NTHROOT(lop(e,at_NTHROOT));
    vecteur l2NTHROOT(l1NTHROOT);
    for (unsigned i=0;i<l1NTHROOT.size();++i){
      gen & g=l2NTHROOT[i];
      if (g._SYMBptr->feuille.type==_VECT && g._SYMBptr->feuille._VECTptr->size()==2){
	vecteur gv=*g._SYMBptr->feuille._VECTptr;
	gv=makevecteur(gv[1],inv(gv[0],contextptr));
	g=_pow(gen(gv,_SEQ__VECT),contextptr);//symbolic(at_pow,gen(gv,_SEQ__VECT));
      }
    }
    subst1=mergevecteur(l1surd,l1NTHROOT);
    subst2=mergevecteur(l2surd,l2NTHROOT);
    if (!subst1.empty())
      *logptr(contextptr) << gettext("Temporary replacing surd/NTHROOT by fractional powers") << endl;
  }

  bool is_elementary(const vecteur & v,const gen & x){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (*it==x)
	continue;
      if (!it->is_symb_of_sommet(at_exp) && (!it->is_symb_of_sommet(at_ln)) )
	return false;
    }
    return true;
  }

  // intmode bit 0 is used for sqrt int control, bit 1 control step/step info
  gen integrate_id_rem(const gen & e_orig,const gen & gen_x,gen & remains_to_integrate,GIAC_CONTEXT,int intmode){
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate id_rem ") << e_orig << endl;
#endif
    remains_to_integrate=0;
    gen e(e_orig);
    // Step -3: replace when by piecewise
    e=when2piecewise(e,contextptr);
    e=Heavisidetopiecewise(e,contextptr); // e=Heavisidetosign(e,contextptr);
    if (is_constant_wrt(e,gen_x,contextptr) && lop(e,at_sign).empty())
      return e*gen_x;
    if (e.type!=_SYMB) {
      remains_to_integrate=zero;
      if (e==gen_x)
	return rdiv(pow(e,2),plus_two,contextptr);
      else
	return e*gen_x;
    }
    // Step -2: piecewise
    vecteur lpiece(lop(e,at_piecewise));
    if (!lpiece.empty()) lpiece=lvarx(lpiece,gen_x);
    if (!lpiece.empty()){
      *logptr(contextptr) << gettext("Warning: piecewise indefinite integration does not return a continuous antiderivative") << endl;
      gen piece=lpiece.front();
      if (piece.is_symb_of_sommet(at_piecewise))
	return integrate_piecewise(e,piece,gen_x,remains_to_integrate,contextptr,intmode);
    }
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate step -2 ") << e << endl;
#endif
    // Step -1: replace ifte(a,b,c) by b+sign(a==0)*(c-b)
    // if a is A1>A2 or A1>=A2 condition, the sign(a==0) is replaced by (sign(A2-A1)+1)/2
    vecteur lwhen(lop(e,at_when));
    if (!lwhen.empty()) lwhen=lvarx(lwhen,gen_x);
    if (!lwhen.empty()){
      vecteur l2;
      const_iterateur it=lwhen.begin(),itend=lwhen.end();
      int i=0;
      for (;it!=itend;++it,++i){
	gen tmp=it->_SYMBptr->feuille,repl;
	if (tmp.type!=_VECT || tmp._VECTptr->size()!=3)
	  return gensizeerr(gettext("Bad when ")+it->print(contextptr));
	vecteur & whenargs = *tmp._VECTptr;
	tmp = whenargs[0];
	if ( (tmp.is_symb_of_sommet(at_superieur_strict) || 
	     tmp.is_symb_of_sommet(at_superieur_egal) ) &&
	     (repl=tmp._SYMBptr->feuille).type==_VECT && repl._VECTptr->size()==2){
	  repl=repl._VECTptr->back()-repl._VECTptr->front();
	  repl=(symbolic(at_sign,repl)+1)/2;
	}
	else {
	  repl=symbolic(at_same,gen(makevecteur(tmp,0),_SEQ__VECT));
	  repl=symbolic(at_sign,repl);
	}
	l2.push_back(whenargs[1]+repl*(whenargs[2]-whenargs[1]));
      }
      e=complex_subst(e,lwhen,l2,contextptr);      
    }
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate step 0 ") << e << endl;
#endif
    // Step 0: replace abs(var_dep_x) with sign*var_dep_x
    // and then sign() with a constant
    gen res;
    vecteur l1(lop(e,at_abs)),m1(lop(e,at_sign));
    if (!l1.empty()) l1=lvarx(l1,gen_x); 
    if (!m1.empty()) m1=lvarx(m1,gen_x);
    if (!l1.empty() || !m1.empty()){
      if (integrate_step0(e,gen_x,l1,m1,res,remains_to_integrate,contextptr,intmode))
	return res;
    }
    // Step1: detection of some unary_op[linear fcn]
    unary_function_ptr u=e._SYMBptr->sommet;
    gen f=e._SYMBptr->feuille,a,b;
    // particular case for ^, _FUNCnd arg must be constant
    if ( (u==at_pow) && is_constant_wrt(f._VECTptr->back(),gen_x,contextptr) && is_linear_wrt(f._VECTptr->front(),gen_x,a,b,contextptr) ){
      if ( (intmode & 2)==0)
	gprintf(step_linear,gettext("Integrate %gen, a linear expression u=%gen to a constant power n=%gen,\nIf n=-1 then ln(u)/a else u^(n+1)/((n+1)*%gen)"),makevecteur(e,a*gen_x+b,f._VECTptr->back(),a),contextptr);
      b=f._VECTptr->back();
      if (is_minus_one(b))
	return rdiv(lnabs(f._VECTptr->front(),contextptr),a,contextptr);
      return rdiv(pow(f._VECTptr->front(),b+plus_one,contextptr),a*(b+plus_one),contextptr);
    }
    if ( (u==at_surd) && is_constant_wrt(f._VECTptr->back(),gen_x,contextptr) && is_linear_wrt(f._VECTptr->front(),gen_x,a,b,contextptr) ){
      if ( (intmode & 2)==0)
	gprintf(step_linear,gettext("Integrate %gen, a linear expression u=%gen to a constant power n=1/%gen,\nIf n=-1 then ln(u)/a else u^(n+1)/((n+1)*%gen)"),makevecteur(e,a*gen_x+b,f._VECTptr->front(),a),contextptr);
      b=f._VECTptr->back();
      if (is_minus_one(b))
	return rdiv(lnabs(f._VECTptr->front(),contextptr),a,contextptr);
      return f._VECTptr->front()*symbolic(at_surd,f)/(a+a/b);
    }
    if ( (u==at_NTHROOT) && is_constant_wrt(f._VECTptr->front(),gen_x,contextptr) && is_linear_wrt(f._VECTptr->back(),gen_x,a,b,contextptr) ){
      if ( (intmode & 2)==0)
	gprintf(step_linear,gettext("Integrate %gen, a linear expression u=%gen to a constant power n=1/%gen,\nIf n=-1 then ln(u)/a else u^(n+1)/((n+1)*%gen)"),makevecteur(e,a*gen_x+b,f._VECTptr->front(),a),contextptr);
      b=f._VECTptr->front();
      if (is_minus_one(b))
	return rdiv(lnabs(f._VECTptr->back(),contextptr),a,contextptr);
      return f._VECTptr->back()*symbolic(at_NTHROOT,f)/(a+a/b);
    }
#if 1 // ndef EMCC // re-enabled Aug. 2015 for integrate(1/surd(x^2,3),x,-1,1)
    if (has_op(e,*at_surd) || has_op(e,*at_NTHROOT)){
      vecteur subst1,subst2;
      surd2pow(e,subst1,subst2,contextptr);
      gen g=subst(e,subst1,subst2,false,contextptr);
      g=integrate_id_rem(g,gen_x,remains_to_integrate,contextptr,intmode);
      remains_to_integrate=subst(remains_to_integrate,subst2,subst1,false,contextptr);
      g=subst(g,subst2,subst1,false,contextptr);
      return g;
    }
#endif
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate step 1 ") << e << endl;
#endif
    if (u==at_sum && f.type==_VECT && f._VECTptr->size()==4){
      vecteur & fv=*f._VECTptr;
      if (!is_zero(derive(fv[1],gen_x,contextptr)))
	return gensizeerr("Mute variable of sum depends on integration variable");
      if (!is_zero(derive(fv[2],gen_x,contextptr)) || !is_zero(derive(fv[3],gen_x,contextptr)) )
	return gensizeerr("Boundaries of sum depends on integration variables");
      if (is_inf(fv[2])||is_inf(fv[3]))
	*logptr(contextptr) << "Warning: assuming integration and sum commutes" << endl;
      gen res=integrate_id_rem(fv[0],gen_x,remains_to_integrate,contextptr,intmode);
      res=_sum(makesequence(res,fv[1],fv[2],fv[3]),contextptr);
      if (!is_zero(remains_to_integrate))
	remains_to_integrate=_sum(makesequence(remains_to_integrate,fv[1],fv[2],fv[3]),contextptr);
      return res;
    }
    // unary op only
    int s=equalposcomp(primitive_tab_op,u);
    if (s && is_linear_wrt(f,gen_x,a,b,contextptr) ){
      if ( (intmode & 2)==0)
	gprintf(step_funclinear,gettext("Integrate %gen: function %gen applied to a linear expression u=%gen, result %gen"),makevecteur(e,primitive_tab_op[s-1],a*gen_x+b,primitive_tab_primitive[s-1](a*gen_x+b,contextptr)/a),contextptr);      
      return rdiv(primitive_tab_primitive[s-1](f,contextptr),a,contextptr);
    }
    // Step2: detection of f(u)*u' 
    vecteur v(1,gen_x);
    rlvarx(e,gen_x,v);
    int rvarsize=int(v.size());
    if (rvarsize>1){
      gen e2=_texpand(e,contextptr);
      if (is_undef(e2))
	e2=e;
      vecteur v2(1,gen_x);
      rlvarx(e2,gen_x,v2);
      if (v2.size()<rvarsize){
	e=e2;
	v=v2;
	rvarsize=int(v2.size());
      }
    }
    vecteur rvar=v;
    gen fu,fx;
    if (rvarsize<=TRY_FU_UPRIME){ // otherwise no hope
      const_iterateur it=v.begin(),itend=v.end();
      ++it; // don't try x!
      for (;it!=itend;++it){
	if (it->is_symb_of_sommet(at_fsolve) || it->is_symb_of_sommet(at_equal))
	  continue;
	gen df=derive(*it,gen_x,contextptr);
	gen tmprem;
	fu=ratnormal(rdiv(e,df,contextptr),contextptr);
	fu=eval(fu,1,contextptr);
	if ((is_undef(fu) || is_inf(fu)) && is_zero(ratnormal(df,contextptr))){
	  // *it is constant -> find the value
	  tmprem=subst(*it,gen_x,zero,false,contextptr);
	  e=subst(e,*it,tmprem,false,contextptr);
	  return integrate_id_rem(e,gen_x,remains_to_integrate,contextptr,intmode | 2);
	}
	if (is_undef(fu) || is_inf(fu))
	  continue;
	if (it->is_symb_of_sommet(at_cos))
	  fu=_trigcos(tan2sincos(fu,contextptr),contextptr);
	if (it->is_symb_of_sommet(at_sin))
	  fu=_trigsin(tan2sincos(fu,contextptr),contextptr);
	if (it->is_symb_of_sommet(at_tan))
	  fu=_trigtan(fu,contextptr);
	bool tst=is_rewritable_as_f_of(fu,*it,fx,gen_x,contextptr);
	if (tst){
	  if (taille(fx,256)>taille(e,255)){
	    vecteur fxv=lvarx(fx,gen_x);
	    if (has_op(fxv,*at_ln) || has_op(fxv,*at_atan))
	      tst=false;
	  }
	}
	if (tst){
	  if ( (intmode & 2)==0)
	    gprintf(step_fuuprime,gettext("Integration of %gen: f(u)*u' where f=%gen->%gen and u=%gen"),makevecteur(e,gen_x,fx,*it),contextptr);
#if 0
	  // no abs, for integrate(cot(ln(x))/x,x), but has side effect...
	  // would be better to add implicit assumptions
	  bool save_do_lnabs=do_lnabs(contextptr);
	  do_lnabs(false,contextptr);
	  e=linear_integrate_nostep(fx,gen_x,tmprem,intmode,contextptr);
	  do_lnabs(save_do_lnabs,contextptr);
	  remains_to_integrate=remains_to_integrate+complex_subst(tmprem,gen_x,*it,contextptr)*df;
	  e=complex_subst(e,gen_x,*it,contextptr);
	  if (save_do_lnabs){
	    vector<const unary_function_ptr *> ln_tab(1,at_ln);
	    vector<gen_op_context> lnabs_tab(1,add_lnabs);
	    e=subst(e,ln_tab,lnabs_tab,true,contextptr);
	  }
	  return e;
#else
	  // ln() in integration should not be ln(abs()) if complex change of variable, example a:=-2/(2*i*exp(2*i*x)+2*i)*exp(2*i*x); b:=int(a); simplify(diff(b)-a);
	  bool b=do_lnabs(contextptr);
	  if (has_i(*it)) do_lnabs(false,contextptr);
	  e=linear_integrate_nostep(fx,gen_x,tmprem,intmode,contextptr);
	  do_lnabs(b,contextptr);
	  remains_to_integrate=remains_to_integrate+complex_subst(tmprem,gen_x,*it,contextptr)*df;
	  bool batan=atan_tan_no_floor(contextptr);
	  atan_tan_no_floor(true,contextptr);
	  e=complex_subst(e,gen_x,*it,contextptr);
	  atan_tan_no_floor(batan,contextptr);
	  // additional check for integrals like
	  // int(sqrt (1+x^(-2/3)),x,-1,0)
	  if (it->is_symb_of_sommet(at_pow)){
	    gen powarg=(*it)[1],powa,powb;
	    if (is_linear_wrt(powarg,gen_x,powa,powb,contextptr) && !is_zero(powa)){
	      gen powx=-powb/powa;
	      // check derivative at powx+-1
	      gen check=ratnormal(derive(e,gen_x,contextptr)/e_orig,contextptr);
	      gen chkplus=subst(check,gen_x,powx+1.0,false,contextptr);
	      gen chkminus=subst(check,gen_x,powx-1.0,false,contextptr);
	      bool tstplus=is_zero(chkplus+1,contextptr);		
	      bool tstminus=is_zero(chkminus+1,contextptr);		
	      if (tstplus){
		if (tstminus)
		  e=-e;
		else
		  e=-sign(gen_x,contextptr)*e;
	      }
	      else {
		if (tstminus)
		  e=sign(gen_x,contextptr)*e;
	      }
	    }
	  }
	  return e;
#endif
	}
	if (it->is_symb_of_sommet(at_pow)){
	  v[it-v.begin()]=powexpand(*it,contextptr);
	  bool tst=is_rewritable_as_f_of(powexpand(fu,contextptr),*it,fx,gen_x,contextptr);
	  if (tst){
	    if (taille(fx,256)>taille(e,255)){
	      vecteur fxv=lvarx(fx,gen_x);
	      if (has_op(fxv,*at_ln) || has_op(fxv,*at_atan))
		tst=false;
	    }
	  }
	  if (tst){
	    if ( (intmode & 2)==0)
	      gprintf(step_fuuprime,gettext("Integration of %gen: f(u)*u' where f=%gen->%gen and u=%gen"),makevecteur(e,gen_x,fx,*it),contextptr);
	    e=linear_integrate_nostep(fx,gen_x,tmprem,intmode,contextptr);
	    remains_to_integrate=remains_to_integrate+complex_subst(tmprem,gen_x,*it,contextptr)*df;
	    return complex_subst(e,gen_x,*it,contextptr);
	  }
	}
	if (it->type!=_SYMB)
	  continue;
	f=ratnormal(it->_SYMBptr->feuille,contextptr); 
	// ratnormal added otherwise infinite recursion for int(1/sin(x^-1))
	if ( (f.type==_VECT) && (!f._VECTptr->empty()) )
	  f=f._VECTptr->front();
	if (f.type!=_SYMB)
	  continue;
	if (is_linear_wrt(f,gen_x,a,b,contextptr))
	  continue;
	df=derive(f,gen_x,contextptr);
	fu=ratnormal(rdiv(e,df,contextptr),contextptr);
	if (is_rewritable_as_f_of(fu,f,fx,gen_x,contextptr)){
	  if ( (intmode & 2)==0)
	    gprintf(step_fuuprime,gettext("Integration of %gen: f(u)*u' where f=%gen->%gen and u=%gen"),makevecteur(e,gen_x,fx,f),contextptr);
	  e=linear_integrate_nostep(fx,gen_x,tmprem,intmode,contextptr);
	  remains_to_integrate=remains_to_integrate+complex_subst(tmprem,gen_x,f,contextptr)*df;
	  return complex_subst(e,gen_x,f,contextptr);
	}	  
      }
    }
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate step 2 ") << e << endl;
#endif
    if (e.type!=_SYMB){
      if (e==gen_x)
	return pow(gen_x,2,contextptr)/2;
      else
	return e*gen_x;
    }
    // try with argument of the product or of a power
    v.clear();
    bool est_puissance;
    if (e._SYMBptr->sommet==at_pow){
      v=vecteur(1,e._SYMBptr->feuille._VECTptr->front());
      fu=pow(e._SYMBptr->feuille._VECTptr->front(),e._SYMBptr->feuille._VECTptr->back()-plus_one,contextptr);
      est_puissance=true;
    }
    else {
      if ( (e._SYMBptr->sommet==at_prod) && (e._SYMBptr->feuille.type==_VECT))
	v=*e._SYMBptr->feuille._VECTptr;
      est_puissance=false;
    }
    const_iterateur vt=v.begin(),vtend=v.end();
    for (int i=0;(i<TRY_FU_UPRIME) && (vt!=vtend);++vt,++i){
      gen tmprem,u=linear_integrate_nostep(*vt,gen_x,tmprem,intmode|2,contextptr);
      if (is_undef(u) || !is_zero(tmprem)){
	gen tst=*vt;
	if (tst.is_symb_of_sommet(at_pow)){
	  gen vtbase=tst._SYMBptr->feuille[0],vtexpo=inv(tst._SYMBptr->feuille[1],contextptr);
	  if (vtexpo.type==_INT_ && vtexpo.val==4){
	    if (is_even_odd(e,gen_x,contextptr)==1){
	      gen tmp=complex_subst(e,gen_x,inv(gen_x,contextptr),contextptr);
	      gen root=complex_subst(tst,gen_x,inv(gen_x,contextptr),contextptr);
	      gen sroot=simplify(root,contextptr);
	      if (is_even_odd(sroot,gen_x,contextptr)){
		tmp=complex_subst(tmp,root,sroot,contextptr);
		tmp=-linear_integrate_nostep(tmp*pow(gen_x,-2,contextptr),gen_x,tmprem,intmode|2,contextptr);
		if (!is_undef(tmp) && is_zero(tmprem)){
		  tmp=simplifier(tmp,contextptr);
		  tmp=complex_subst(tmp,gen_x,inv(gen_x,contextptr),contextptr);
		  vecteur v=lop(tmp,at_pow);
		  vecteur w=gen2vecteur(simplify(v,contextptr));
		  tmp=complex_subst(tmp,v,w,contextptr);
		  return tmp;
		}
	      }
	    }
	  } 
	}
	continue;
      }
      if (!est_puissance){
	vecteur vv(v);
	vv.erase(vv.begin()+i,vv.begin()+i+1);
	fu=symbolic(at_prod,gen(vv,_SEQ__VECT));
      }
      gen cst=extract_cst(u,gen_x,contextptr);
      if (is_rewritable_as_f_of(fu,u,fx,gen_x,contextptr)){
	fx=cst*fx;
	if ( (intmode & 2)==0)
	  gprintf(step_fuuprime,gettext("Integration of %gen: f(u)*u' where f=%gen->%gen and u=%gen"),makevecteur(e,gen_x,fx,u),contextptr);
	e=linear_integrate_nostep(fx,gen_x,tmprem,intmode,contextptr);
	remains_to_integrate=remains_to_integrate+complex_subst(tmprem,gen_x,u,contextptr)*derive(u,gen_x,contextptr);
	return complex_subst(e,gen_x,u,contextptr);
      }
      if (vt->is_symb_of_sommet(at_pow)){
	gen vtbase=vt->_SYMBptr->feuille[0],vtexpo=vt->_SYMBptr->feuille[1];
	if (vtexpo.type==_INT_ && vtexpo.val %2){ 
	  // for example *vt=x^9, retry with *vt=x^4
	  u=linear_integrate_nostep(pow(vtbase,vtexpo.val/2,contextptr),gen_x,tmprem,intmode|2,contextptr);
	  if (is_undef(u) || !is_zero(tmprem))
	    continue;
	  if (!est_puissance){
	    vecteur vv(v);
	    vv.erase(vv.begin()+i,vv.begin()+i+1);
	    fu=symbolic(at_prod,gen(vv,_SEQ__VECT));
	  }
	  cst=extract_cst(u,gen_x,contextptr);
	  fu=fu*pow(vtbase,vtexpo.val-vtexpo.val/2,contextptr);
	  if (is_rewritable_as_f_of(fu,u,fx,gen_x,contextptr)){
	    fx=cst*fx;
	    if ( (intmode & 2)==0)
	      gprintf(step_fuuprime,gettext("Integration of %gen: f(u)*u' where f=%gen->%gen and u=%gen"),makevecteur(e,gen_x,fx,u),contextptr);
	    e=linear_integrate_nostep(fx,gen_x,tmprem,intmode,contextptr);
	    remains_to_integrate=remains_to_integrate+complex_subst(tmprem,gen_x,u,contextptr)*derive(u,gen_x,contextptr);
	    return complex_subst(e,gen_x,u,contextptr);
	  }	  
	}
      }
    }
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate step 3 ") << e << endl;
#endif
    // Step3: rational fraction?
    if (rvarsize==1){
      gen xvar(gen_x);
      return integrate_rational(e,gen_x,remains_to_integrate,xvar,intmode,contextptr);
    }

    // square roots
    if ( (rvarsize==2) && (rvar.back().type==_SYMB) && (rvar.back()._SYMBptr->sommet==at_pow) ){
      if (integrate_sqrt(e,gen_x,rvar,res,remains_to_integrate,intmode,contextptr)){
	if ( (intmode & 1)==0 && is_zero(res)){
	  // try again with x->1/x?
	  gen e2=normal(-complex_subst(e,gen_x,inv(gen_x,contextptr),contextptr)/gen_x/gen_x,contextptr);
	  gen remains_to_integrate2,res2=integrate_id_rem(e2,gen_x,remains_to_integrate2,contextptr,1);
	  if (!is_zero(res2)){
	    res=complex_subst(res2,gen_x,inv(gen_x,contextptr),contextptr);
	    remains_to_integrate=-complex_subst(remains_to_integrate2,gen_x,inv(gen_x,contextptr),contextptr)/gen_x/gen_x;
	    return res;
	  }
	}
	return res;
      }
    }
    // detection of inv of trig or ln of a linear expression
    if (detect_inv_trigln(e,rvar,gen_x,res,remains_to_integrate,true,intmode,contextptr))
      return res;

    // integration by part?
    if ( (e._SYMBptr->sommet==at_prod) && (e._SYMBptr->feuille.type==_VECT)){
      const_iterateur ibp=e._SYMBptr->feuille._VECTptr->begin(),ibpend=e._SYMBptr->feuille._VECTptr->end();
      for (int j=0;ibp!=ibpend;++ibp,++j){
	int test;
	if (ibp->type!=_SYMB)
	  continue;
	if ( (ibp->_SYMBptr->sommet==at_pow) &&
	     (ibp->_SYMBptr->feuille._VECTptr->front().type==_SYMB) &&  
	     (ibp->_SYMBptr->feuille._VECTptr->back().type==_INT_) && 
	     (ibp->_SYMBptr->feuille._VECTptr->back().val>0) )
	  test=equalposcomp(inverse_tab_op,ibp->_SYMBptr->feuille._VECTptr->front()._SYMBptr->sommet);
	else
	  test=equalposcomp(inverse_tab_op,ibp->_SYMBptr->sommet);
	if (!test)
	  continue;
	vecteur ibpv(*e._SYMBptr->feuille._VECTptr);
	ibpv.erase(ibpv.begin()+j);
	gen ibpe=_prod(ibpv,contextptr);
#if 1
	gen tmpres,tmprem,tmpprimitive,tmp;
	tmpprimitive=linear_integrate_nostep(ibpe,gen_x,tmp,intmode|2,contextptr);
	if (is_zero(tmp)){ 
	  vecteur tmpv=rlvarx(tmpprimitive,gen_x);
	  unsigned tmpi=0;
	  for (;tmpi<tmpv.size();++tmpi){
	    if (tmpv[tmpi].type==_SYMB && equalposcomp(inverse_tab_op,tmpv[tmpi]._SYMBptr->sommet))
	      break;
	  }
	  if (tmpi==tmpv.size()){
	    if ( (intmode & 2)==0)
	      gprintf(step_bypart,gettext("Integration of %gen: by part, u*v'=%gen*(%gen)'"),makevecteur(e,*ibp,tmpprimitive),contextptr);
	    tmpres=tmpprimitive*derive(*ibp,gen_x,contextptr);
	    tmpres=recursive_normal(tmpres,true,contextptr);
	    tmpres=linear_integrate_nostep(tmpres,gen_x,tmprem,intmode,contextptr);
	    remains_to_integrate=-tmprem;
	    return tmpprimitive*(*ibp)-tmpres;
	  }
	}
#else
	vecteur tmpv(1,gen_x);
	lvar(ibpe,tmpv);
	tmpv.erase(tmpv.begin());
	if (lvarx(tmpv,gen_x).empty()){
	  gen tmpres,tmprem,tmpprimitive,tmp,xvar(gen_x);
	  tmpprimitive=integrate_rational(ibpe,gen_x,tmp,xvar,intmode,contextptr);
	  if (is_zero(tmp) && lvarx(tmpprimitive,gen_x)==vecteur(1,gen_x)){
	    tmpres=tmpprimitive*derive(*ibp,gen_x,contextptr);
	    tmpres=recursive_normal(tmpres,true,contextptr);
	    tmpres=linear_integrate_nostep(tmpres,gen_x,tmprem,intmode,contextptr);
	    remains_to_integrate=-tmprem;
	    return tmpprimitive*(*ibp)-tmpres;
	  }
	}
#endif
      }
    }
    else { // check for u'=1
      int test;
      if ( (e._SYMBptr->sommet==at_pow) && (e._SYMBptr->feuille._VECTptr->front().type==_SYMB) && (e._SYMBptr->feuille._VECTptr->back().type==_INT_) && (e._SYMBptr->feuille._VECTptr->back().val>0) )
	test=equalposcomp(inverse_tab_op,e._SYMBptr->feuille._VECTptr->front()._SYMBptr->sommet);
      else
	test=equalposcomp(inverse_tab_op,e._SYMBptr->sommet);
      if (test){
	if ( (intmode & 2)==0)
	  gprintf(step_bypart1,gettext("Integration of %gen by part of u*v' where u=1 and v=%gen'"),makevecteur(e,e),contextptr);
	gen tmpres,tmprem;
	tmpres=normal(derive(e,gen_x,contextptr),contextptr);
	tmpres=linear_integrate_nostep(gen_x*tmpres,gen_x,tmprem,intmode,contextptr);
	if (!has_i(e) && has_i(tmpres)){
	  remains_to_integrate=e;
	  return 0;
	}
	remains_to_integrate=-tmprem;
	return gen_x*e-tmpres;
      }
    }
    // additional check on e for f:= x*(x + 1)*(2*x*(x - (2*x**3 + 2*x**2 + x + 1)*log(x + 1))*exp(3*x**2) + (x**2*exp(2*x**2) - log(x + 1)**2)**2)/((x + 1)*log(x + 1)**2 - (x**3 + x**2)*exp(2*x**2))**2
    if (!is_elementary(rvar,gen_x) && detect_inv_trigln(e,rvar,gen_x,res,remains_to_integrate,false,intmode,contextptr))
      return res;

    // rewrite inv(exp)
    vector<const unary_function_ptr *> vsubstin(1,at_inv);
    vector<gen_op_context> vsubstout(1,invexptoexpneg);
    e=subst(e,vsubstin,vsubstout,true,contextptr); // changed to true for numint of programs (otherwise e becomes undef)
    // detection of denominator=independent of x
    v=lvarxwithinv(e,gen_x,contextptr);
    // search for nop (for nop[inv])
    if (!has_nop_var(v)){
      // additional check for non integer powers
      v=lop(lvar(e),at_pow);
      vecteur vx=lvarx(v,gen_x);
      if (vx.empty() || vx==vecteur(1,gen_x))
	return integrate_linearizable(e,gen_x,remains_to_integrate,intmode,contextptr);
      // second try with ^ rewritten as exp(ln)
      gen etmp=pow2expln(e,contextptr);
      v=lvarxwithinv(etmp,gen_x,contextptr);
      // search for nop (for nop[inv])
      if (!has_nop_var(v)){
	// additional check for non integer powers
	v=lop(lvar(etmp),at_pow);
	vecteur vx=lvarx(v,gen_x);
	if (vx.empty() || vx==vecteur(1,gen_x))
	  return integrate_linearizable(etmp,gen_x,remains_to_integrate,intmode,contextptr);
      }
    }
    // trigonometric fraction (or exp _FRAC), rewrite all elemnts of rvar as
    // tan of the common half angle, i.e. tan([coeff_trig*x+b]/2)
    int trig_fraction=-1; 
    gen coeff_trig;
    vecteur var(lvarx(e,gen_x));
    const_iterateur vart=var.begin(),vartend=var.end();
    for (;vart!=vartend;++vart){
      if (vart->type!=_SYMB){
	trig_fraction=false;
	continue;
      }
      int vartt=equalposcomp(primitive_tab_op,vart->_SYMBptr->sommet);
      if ( (!vartt) || (vartt>4) )
	trig_fraction=0;
      if (trig_fraction==-1){
          trig_fraction=vartt;
      }
      else {
          if (trig_fraction==4){
              if (vartt!=4)
                  trig_fraction=0;
          }
          else {
              if (vartt==4)
                  trig_fraction=0;
          }
      }
      if (trig_fraction ){ // trig of linear?
	gen a,b;
	if (!is_linear_wrt(vart->_SYMBptr->feuille,gen_x,a,b,contextptr)){
	  trig_fraction=false;
	  continue;
	}
	if (is_zero(coeff_trig))
	  coeff_trig=a;
	else {
	  gen quotient=ratnormal(rdiv(a,coeff_trig,contextptr),contextptr);
	  if (quotient.type==_INT_)
	    continue;
	  if ( (quotient.type==_FRAC) && (quotient._FRACptr->num.type==_INT_) && (quotient._FRACptr->den.type==_INT_) ){
	    coeff_trig=ratnormal(rdiv(coeff_trig,quotient._FRACptr->den,contextptr),contextptr);
	    continue;
	  }
	  if ( (quotient.type==_SYMB) && (quotient._SYMBptr->sommet==at_inv) && (quotient._SYMBptr->feuille.type==_INT_)){
	    coeff_trig=ratnormal(rdiv(coeff_trig,quotient._SYMBptr->feuille,contextptr),contextptr);
	    continue;
	  }
	  trig_fraction=false;
	}
      } // end if (trig_fraction)
    }
    if (trig_fraction)
      return integrate_trig_fraction(e,gen_x,var,coeff_trig,trig_fraction,remains_to_integrate,intmode,contextptr);
    // finish by calling the Risch algorithm
    if ( (intmode & 2)==0)
      gprintf(step_risch,gettext("Integrate %gen, no heuristic found, running Risch algorithm"),makevecteur(e),contextptr);
    return risch(e,*gen_x._IDNTptr,remains_to_integrate,contextptr);
  }

  gen linear_integrate(const gen & e,const gen & x,gen & remains_to_integrate,GIAC_CONTEXT){
    gen ee(normalize_sqrt(e,contextptr));
    return linear_apply(ee,x,remains_to_integrate,contextptr,integrate_gen_rem);
  }

  gen linear_integrate_nostep(const gen & e,const gen & x,gen & remains_to_integrate,int intmode,GIAC_CONTEXT){
    int step_infolevelsave=step_infolevel(contextptr);
    if ((intmode & 2)==2) 
      step_infolevel(contextptr)=0;
    // temporarily remove assumptions by changing integration variable
    identificateur t("t_nostep");
    gen tt(t);
    gen ee=quotesubst(e,x,tt,contextptr);
    ee=normalize_sqrt(ee,contextptr);
    gen res=linear_apply(ee,tt,remains_to_integrate,contextptr,integrate_gen_rem);
    step_infolevel(contextptr)=step_infolevelsave;
    res=quotesubst(res,tt,x,contextptr);
    remains_to_integrate=quotesubst(remains_to_integrate,tt,x,contextptr);
    return res;
  }

  gen min2abs(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return symbolic(at_min,g);
    gen a=g._VECTptr->front(),b=g._VECTptr->back();
    return (a+b-abs(a-b,contextptr))/2;
  }

  gen max2abs(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return symbolic(at_min,g);
    gen a=g._VECTptr->front(),b=g._VECTptr->back();
    return (a+b+abs(a-b,contextptr))/2;
  }

  gen rewrite_minmax(const gen & e,bool quotesubst,GIAC_CONTEXT){
    vector<const unary_function_ptr *> vu;
    vu.push_back(at_min); 
    vu.push_back(at_max); 
    vector <gen_op_context> vv;
    vv.push_back(min2abs);
    vv.push_back(max2abs);
    return subst(e,vu,vv,quotesubst,contextptr);
  }

  gen integrate_id(const gen & e,const identificateur & x,GIAC_CONTEXT){
    if (e.type==_VECT){
      vecteur w;
      vecteur::const_iterator it=e._VECTptr->begin(),itend=e._VECTptr->end();
      for (;it!=itend;++it)
	w.push_back(integrate_id(*it,x,contextptr));
      return w;
    }
    gen remains_to_integrate;
    gen ee=rewrite_hyper(e,contextptr);
    ee=rewrite_minmax(ee,true,contextptr);
    gen res=_simplifier(linear_integrate(ee,x,remains_to_integrate,contextptr),contextptr);
    if (is_zero(remains_to_integrate))
      return res;
    else
      return res+symbolic(at_integrate,gen(makevecteur(remains_to_integrate,x),_SEQ__VECT));
  }

  static gen integrate0_(const gen & e,const identificateur & x,gen & remains_to_integrate,GIAC_CONTEXT){
    if (step_infolevel(contextptr))
      gprintf(step_integrate_header,gettext("===== Step/step primitive of %gen with respect to %gen ====="),makevecteur(e,x),contextptr);
    if (e.type==_VECT){
      vecteur w;
      vecteur::const_iterator it=e._VECTptr->begin(),itend=e._VECTptr->end();
      for (;it!=itend;++it)
	w.push_back(integrate_id(*it,x,contextptr));
      return w;
    }
    gen ee=rewrite_hyper(e,contextptr),tmprem;
    ee=rewrite_minmax(ee,true,contextptr);
    gen res=linear_integrate(ee,x,tmprem,contextptr);
    remains_to_integrate=remains_to_integrate+tmprem;
    if (step_infolevel(contextptr) && is_zero(remains_to_integrate))
      gprintf(gettext("Hence primitive of %gen with respect to %gen is %gen"),makevecteur(e,x,res),contextptr);
    return res;
  }

  static gen integrate0(const gen & e,const identificateur & x,gen & remains_to_integrate,GIAC_CONTEXT){
    bool b_acosh=keep_acosh_asinh(contextptr);
    keep_acosh_asinh(true,contextptr);
    gen res=integrate0_(e,x,remains_to_integrate,contextptr);
    keep_acosh_asinh(b_acosh,contextptr);
    return res;
  }

  gen integrate_gen(const gen & e,const gen & f,GIAC_CONTEXT){
    if (f.type!=_IDNT){
      identificateur x(" x");
      gen e1=subst(e,f,x,false,contextptr);
      return quotesubst(integrate_id(e1,x,contextptr),x,f,contextptr);
    }
    return integrate_id(e,*f._IDNTptr,contextptr);
  }

  bool adjust_int_sum_arg(vecteur & v,int & s){
    if (s<2)
      return false; // setsizeerr(contextptr);
    if ( (s==2) && (v[1].type==_SYMB) && (v[1]._SYMBptr->sommet==at_equal || v[1]._SYMBptr->sommet==at_equal2 || v[1]._SYMBptr->sommet==at_same)){
      v.push_back(v[1]._SYMBptr->feuille._VECTptr->back());
      v[1]=v[1]._SYMBptr->feuille._VECTptr->front();
      if ( (v[2].type!=_SYMB) || (v[2]._SYMBptr->sommet!=at_interval) )
	return false; // settypeerr(contextptr);
      v.push_back(v[2]._SYMBptr->feuille._VECTptr->back());
      v[2]=v[2]._SYMBptr->feuille._VECTptr->front();
      s=4;
    }
    return true;
  }

  static int ggb_intcounter=0;

  gen ck_int_numerically(const gen & f,const gen & x,const gen & a,const gen &b,const gen & exactvalue,GIAC_CONTEXT){
    if (is_inf(a) || is_inf(b))
      return exactvalue;
    gen tmp=evalf_double(exactvalue,1,contextptr);
#if defined HAVE_LIBMPFR && !defined NO_STDEXCEPT
    if ( (tmp.type==_DOUBLE_ || tmp.type==_CPLX) 
	 && !has_i(lop(exactvalue,at_erf)) // otherwise it's slow
	 ){
      try {
	tmp=evalf_double(accurate_evalf(exactvalue,256),1,contextptr);
      } catch (std::runtime_error & err){
      }
    }
#endif
    if (tmp.type!=_DOUBLE_ && tmp.type!=_CPLX)
      return exactvalue;
    if (debug_infolevel)
      *logptr(contextptr) << gettext("Checking exact value of integral with numeric approximation")<<endl;
    gen tmp2;
    if (!tegral(f,x,a,b,1e-6,(1<<10),tmp2,contextptr))
      return exactvalue;
    tmp2=evalf_double(tmp2,1,contextptr);
    if ( (tmp2.type!=_DOUBLE_ && tmp2.type!=_CPLX) || 
	 (abs(tmp,contextptr)._DOUBLE_val<1e-8 && abs(tmp2,contextptr)._DOUBLE_val<1e-8) || 
	 abs(tmp-tmp2,contextptr)._DOUBLE_val<=1e-3*abs(tmp2,contextptr)._DOUBLE_val
	 )
      return simplifier(exactvalue,contextptr);
    *logptr(contextptr) << gettext("Error while checking exact value with approximate value, returning both!") << endl;
    return makevecteur(exactvalue,tmp2);
  }

  void comprim(vecteur & v){
    vecteur w;
    for (unsigned i=0;i<v.size();++i){
      if (!equalposcomp(w,v[i]))
	w.push_back(v[i]);
    }
    v=w;
  }
#ifndef USE_GMP_REPLACEMENTS
  // small utility for ggb floats looking like fractions
  void ggb_num_coeff(gen & g){
    if (g.type!=_FRAC || g._FRACptr->den.type!=_ZINT)
      return;
    mpz_t t; mpz_init_set(t,*g._FRACptr->den._ZINTptr);
    while (mpz_divisible_ui_p(t,2)){
      mpz_divexact_ui(t,t,2);
      continue;
    }
    while (mpz_divisible_ui_p(t,5)){
      mpz_divexact_ui(t,t,5);
      continue;
    }
    if (mpz_cmp_ui(t,1)==0)
      g=evalf(g,1,context0);
    mpz_clear(t);
  }
#endif
  // "unary" version
  gen _integrate(const gen & args,GIAC_CONTEXT){
    if (complex_variables(contextptr))
      *logptr(contextptr) << gettext("Warning, complex variables is set, this can lead to fairly complex answers. It is recommended to switch off complex variables in the settings or by complex_variables:=0; and declare individual variables to be complex by e.g. assume(a,complex).") << endl;
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate begin") << endl;
#endif
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    if (v.size()==1){
      gen a,b,c=eval(args,1,contextptr);
      if (c.type==_SPOL1){
	sparse_poly1 res=*c._SPOL1ptr;
	sparse_poly1::iterator it=res.begin(),itend=res.end();
	for (;it!=itend;++it){
	  gen e=it->exponent+1;
	  if (e==0)
	    return sparse_poly1(1,monome(undef,undef));
	  it->coeff=it->coeff/e;
	  it->exponent=e;
	}
	return res;
      }
      if (c.type==_VECT && c.subtype==_POLY1__VECT){
	vecteur v=*c._VECTptr;
	reverse(v.begin(),v.end());
	v=integrate(v,1);
	reverse(v.begin(),v.end());      
	v.push_back(0);
	return gen(v,_POLY1__VECT);
      }
      if (is_algebraic_program(c,a,b) && a.type!=_VECT)
	return symbolic(at_program,makesequence(a,0,_integrate(gen(makevecteur(b,a),_SEQ__VECT),contextptr)));
      if (calc_mode(contextptr)==1)
	v.push_back(ggb_var(v.front()));
      else
	v.push_back(vx_var);
    }
    int s=int(v.size());
    if (!adjust_int_sum_arg(v,s))
      return gensizeerr(contextptr);
    if (s>=4 && complex_mode(contextptr)){
      complex_mode(false,contextptr);
      gen res=_integrate(args,contextptr);
      complex_mode(true,contextptr);
      return res;
    }
    if (s==1)
      return gentoofewargs("integrate");
    if (s==3){ 
      if (calc_mode(contextptr)!=1)
	// indefinite integration with constant of integration
	return _integrate(gen(makevecteur(v[0],v[1]),_SEQ__VECT),contextptr)+v[2];
      v.insert(v.begin()+1,ggb_var(v.front()));
      ++s;
    }
    if (s>6)
      return gentoomanyargs("integrate");
    gen x=v[1];
    if (x.is_symb_of_sommet(at_unquote))
      x=eval(x,1,contextptr);
    if (storcl_38 && x.type==_IDNT && storcl_38(x,0,x._IDNTptr->id_name,undef,false,contextptr,NULL,false)){
      identificateur t("t_");
      x=v[1];
      v[0]=quotesubst(v[0],x,t,contextptr);
      v[1]=t;
      gen res=_integrate(gen(v,_SEQ__VECT),contextptr);
      return quotesubst(res,t,x,contextptr);
    }
    if (x.type!=_IDNT){
      if (x.type<_IDNT)
	return gensizeerr(contextptr);
      if (abs_calc_mode(contextptr)==38 && x.type!=_SYMB)
	return gensizeerr(contextptr);
      if (x.type==_SYMB && x._SYMBptr->sommet!=at_of && x._SYMBptr->sommet!=at_at)
	return gensizeerr(contextptr);
      identificateur t(" t");
      v[0]=quotesubst(v[0],x,t,contextptr);
      v[1]=t;
      gen res=_integrate(gen(v,_SEQ__VECT),contextptr);
      return quotesubst(res,t,x,contextptr);
    }
    int quoted=0;
    if (x._IDNTptr->quoted){
      quoted=*x._IDNTptr->quoted;
      *x._IDNTptr->quoted=1;
    }
    for (int i=2;i<s;++i){
      v[i]=eval(v[i],eval_level(contextptr),contextptr);
      if (v[i].is_symb_of_sommet(at_pnt))
	return gensizeerr(contextptr);
    }
#if 0 // ndef USE_GMP_REPLACEMENTS
    if (calc_mode(contextptr)==1){
      if (s>2) 
	ggb_num_coeff(v[2]);
      if (s>3) 
	ggb_num_coeff(v[3]);
    }
#endif
    if (s>=4){ // take care of boundaries when evaluating
      if (v.back()==at_assume){
	--s;
	v.pop_back();
      }
      else {
	gen xval=x.eval(1,contextptr);
	gen a(v[2]),b(v[3]);
	if (evalf_double(a,1,contextptr).type==_DOUBLE_ && evalf_double(b,1,contextptr).type==_DOUBLE_){
	  bool neg=false;
	  if (is_greater(v[2],v[3],contextptr)){
	    a=v[3]; b=v[2];
	    neg=true;
	    v[2]=a; v[3]=b;
	  }
	  vecteur lv=lop(lvarx(v[0],v[1]),at_pow);
	  lv=mergevecteur(lv,lop(lvarx(v[0],v[1]),at_surd));
	  lv=mergevecteur(lv,lop(lvarx(v[0],v[1]),at_NTHROOT));
	  if (lv.size()==1 && v[1].type==_IDNT){
	    gen powarg=lv[0][1];
	    if (lv[0][0]==at_NTHROOT)
	      powarg=lv[0][2];
	    lv=protect_solve(powarg,*v[1]._IDNTptr,0,contextptr);
	    for (int i=0;i<int(lv.size());++i){
	      if (is_strictly_greater(lv[i],a,contextptr) && is_strictly_greater(b,lv[i],contextptr)){
		v[3]=lv[i];
		gen res1=_integrate(v,contextptr);
		v[3]=b;
		v[2]=lv[i];
		gen res2=_integrate(v,contextptr);
		res2=res1+res2;
		return neg?-res2:res2;
	      }
	    }
	  }
	  giac_assume(symb_and(symb_superieur_egal(x,a),symb_inferieur_egal(x,b)),contextptr);
	  v.push_back(at_assume);
	  gen res=_integrate(gen(v,_SEQ__VECT),contextptr);
	  sto(xval,x,contextptr);
	  return neg?-res:res;
	}
	if (is_greater(b,a,contextptr)){
	  giac_assume(symb_and(symb_superieur_egal(x,a),symb_inferieur_egal(x,b)),contextptr);
	  v.push_back(at_assume);
	  gen res=_integrate(gen(v,_SEQ__VECT),contextptr);
	  sto(xval,x,contextptr);
	  return res;
	}
	else {
	  if (is_greater(a,b,contextptr)){
	    giac_assume(symb_and(symb_superieur_egal(x,b),symb_inferieur_egal(x,a)),contextptr);
	    v.push_back(at_assume);
	    gen res=_integrate(gen(v,_SEQ__VECT),contextptr);
	    sto(xval,x,contextptr);
	    return res;
	  }
	}
      }
    }
    bool b_acosh=keep_acosh_asinh(contextptr);
    keep_acosh_asinh(true,contextptr);
#ifdef NO_STDEXCEPT
    if (contextptr && contextptr->quoted_global_vars && !is_assumed_real(x,contextptr)){
      contextptr->quoted_global_vars->push_back(x);
      gen tmp=eval(v[0],eval_level(contextptr),contextptr); 
      tmp=Heavisidetopiecewise(tmp,contextptr);
      if (!is_undef(tmp)) v[0]=tmp;
      contextptr->quoted_global_vars->pop_back();
    }
    else {
      gen tmp=eval(v[0],eval_level(contextptr),contextptr); 
      tmp=Heavisidetopiecewise(tmp,contextptr);
      if (!is_undef(tmp)) v[0]=tmp;
    }
#else
    try {
      if (contextptr && contextptr->quoted_global_vars && !is_assumed_real(x,contextptr)){
	contextptr->quoted_global_vars->push_back(x);
	gen tmp=eval(v[0],eval_level(contextptr),contextptr); 
	tmp=Heavisidetopiecewise(tmp,contextptr);
	if (!is_undef(tmp)) v[0]=tmp;
	contextptr->quoted_global_vars->pop_back();
      }
      else {
	gen tmp=eval(v[0],eval_level(contextptr),contextptr); 
	tmp=Heavisidetopiecewise(tmp,contextptr);
	if (!is_undef(tmp)) v[0]=tmp;
      }
    } catch (std::runtime_error & err){
      CERR << "Unable to eval " << v[0] << ": " << err.what() << endl;
    }
#endif
    keep_acosh_asinh(b_acosh,contextptr);
    if (x._IDNTptr->quoted)
      *x._IDNTptr->quoted=quoted;    
    if (s>4 || (approx_mode(contextptr) && (s==4)) ){
      v[1]=x;
      return _gaussquad(gen(v,_SEQ__VECT),contextptr);
    }
    gen rem,borne_inf,borne_sup,res,v0orig,aorig,borig;
    if (s==4){
      if ( (has_num_coeff(v[0]) ||
	    v[2].type==_FLOAT_ || v[2].type==_DOUBLE_ || v[2].type==_REAL ||
	    v[3].type==_FLOAT_ || v[3].type==_DOUBLE_ || v[3].type==_REAL)){
	vecteur ld=makevecteur(unsigned_inf,cst_pi);
	// should first remove mute variables inside embedded sum/int/fsolve
	lidnt(makevecteur(true_lidnt(v[0]),evalf_double(v[2],1,contextptr),evalf_double(v[3],1,contextptr)),ld,false);
	ld.erase(ld.begin());
	ld.erase(ld.begin());
	if (ld==vecteur(1,v[1]) || ld.empty())
	  return _gaussquad(gen(makevecteur(v[0],v[1],v[2],v[3]),_SEQ__VECT),contextptr);
      }
      v0orig=v[0];
      aorig=borne_inf=v[2];
      borig=borne_sup=v[3];
      if (borne_inf==borne_sup)
	return 0;
      v[0]=ceil2floor(v[0],contextptr,true);
      vecteur lfloor(lop(v[0],at_floor));
      lfloor=lvarx(lfloor,x);
      if (!lfloor.empty()){
	gen a,b,l,cond=lfloor.front()._SYMBptr->feuille,tmp;
	if (lvarx(cond,x).size()>1 || !is_linear_wrt(cond,x,a,b,contextptr) ){
	  *logptr(contextptr) << gettext("Floor definite integration: can only handle linear < or > condition") << endl;
	  if (!tegral(v0orig,x,aorig,borig,1e-12,(1<<10),res,contextptr))
	    return undef;
	  return res;
	}
	if (is_inf(borne_inf) || is_inf(borne_sup)){
	  *logptr(contextptr) << gettext("Floor definite integration: unable to handle infinite boundaries") << endl;
	}
	else {
	  // find integers of the form a*x+b in [borne_inf,borne_sup]
	  gen n1=_floor(a*borne_inf+b,contextptr);
	  // n1=a*x+b -> x=(n1-b)/a
	  gen stepx,stepn;
	  if (is_positive(a,contextptr)){
	    stepx=inv(a,contextptr);
	    stepn=1;
	  }
	  else {
	    stepx=-inv(a,contextptr);
	    stepn=-1;
	  }
	  gen cur=borne_inf,next=(n1+stepn-b)/a,res=0;
	  if (stepn==-1 && n1==a*borne_inf+b)
	    n1 -= 1;
	  for (;is_greater(borne_sup,next,contextptr); cur=next,next+=stepx,n1+=stepn){
	    tmp=quotesubst(v[0],lfloor.front(),n1,contextptr);
	    res += _integrate(makesequence(tmp,x,cur,next),contextptr);
#ifdef TIMEOUT
	    control_c();
#endif
	    if (ctrl_c || interrupted) { 
	      interrupted = true; ctrl_c=false;
	      gensizeerr(gettext("Stopped by user interruption."),res);
	      return res;
	    }
	    if (is_undef(res))
	      return res;
	  }
	  tmp=quotesubst(v[0],lfloor.front(),n1,contextptr);
	  res += _integrate(makesequence(tmp,x,cur,borne_sup),contextptr);
	  return ck_int_numerically(v0orig,x,aorig,borig,res,contextptr);
	}
      }
      v[0]=when2piecewise(v[0],contextptr);
      vecteur lpiece(lop(v[0],at_piecewise));
      lpiece=lvarx(lpiece,x);
      if (!lpiece.empty()){
	bool chsign=is_strictly_greater(borne_inf,borne_sup,contextptr);
	if (chsign)
	  swapgen(borne_inf,borne_sup);
	res=0;
	gen piece=lpiece.front();
	if (!piece.is_symb_of_sommet(at_piecewise))
	  return gensizeerr(contextptr);
	gen piecef=piece._SYMBptr->feuille;
	if (piecef.type!=_VECT || piecef._VECTptr->size()<2)
	  return gensizeerr(contextptr);
	vecteur & piecev = *piecef._VECTptr;
	// check conditions: they must be linear wrt x
	int vs=int(piecev.size());
	for (int i=0;i<vs/2;++i){
	  bool unable=true;
	  gen cond=piecev[2*i];
	  if (is_equal(cond) || cond.is_symb_of_sommet(at_same)){
	    *logptr(contextptr) << gettext("Assuming false condition ") << cond << endl;
	    continue;
	  }
	  if (cond.is_symb_of_sommet(at_different)){
	    *logptr(contextptr) << gettext("Assuming true condition ") << cond << endl;
	    v[0]=quotesubst(v[0],piece,piecev[2*i+1],contextptr);
	    res += _integrate(gen(makevecteur(v[0],x,borne_inf,borne_sup),_SEQ__VECT),contextptr);
	    return ck_int_numerically(v0orig,x,aorig,borig,(chsign?-res:res),contextptr);
	  }
	  if (cond.is_symb_of_sommet(at_superieur_strict) || cond.is_symb_of_sommet(at_superieur_egal)){
	    cond=cond._SYMBptr->feuille[0]-cond._SYMBptr->feuille[1];
	    unable=false;
	  }
	  if (cond.is_symb_of_sommet(at_inferieur_strict) || cond.is_symb_of_sommet(at_inferieur_egal)){
	    cond=cond._SYMBptr->feuille[1]-cond._SYMBptr->feuille[0];
	    unable=false;
	  }
	  gen a,b,l;
	  if (unable || !is_linear_wrt(cond,x,a,b,contextptr)){
	    *logptr(contextptr) << gettext("Piecewise definite integration: can only handle linear < or > condition") << endl;
	    if (!tegral(v0orig,x,aorig,borig,1e-12,(1<<10),res,contextptr))
	      return undef;
	    return res;
	  }
	  // check if a*x+b>0 on [borne_inf,borne_sup]
	  l=-b/a;
	  bool positif=ck_is_greater(a,0,contextptr);
	  gen tmp=quotesubst(v[0],piece,piecev[2*i+1],contextptr);
	  if (ck_is_greater(l,borne_sup,contextptr)){
	    // borne_inf < borne_sup <= l
	    if (positif) // test is false, continue
	      continue;
	    // test is true we can compute the integral
	    res += _integrate(gen(makevecteur(tmp,x,borne_inf,borne_sup),_SEQ__VECT),contextptr);
	    return ck_int_numerically(v0orig,x,aorig,borig,(chsign?-res:res),contextptr);
	  }
	  if (ck_is_greater(borne_inf,l,contextptr)){
	    // l <= borne_inf < borne_sup
	    if (!positif) // test is false, continue
	      continue;
	    // test is true we can compute the integral
	    res += _integrate(gen(makevecteur(tmp,x,borne_inf,borne_sup),_SEQ__VECT),contextptr);
	    return ck_int_numerically(v0orig,x,aorig,borig,(chsign?-res:res),contextptr);
	  }
	  // borne_inf<l<borne_sup
	  if (positif){
	    // compute integral between l and borne_sup
	    res += _integrate(gen(makevecteur(tmp,x,l,borne_sup),_SEQ__VECT),contextptr);
	    borne_sup=l; // continue with integral from borne_inf to l
	    continue;
	  }
	  // compute integral between borne_inf and l
	  res += _integrate(gen(makevecteur(tmp,x,borne_inf,l),_SEQ__VECT),contextptr);
	  borne_inf=l; // continue with integral from l to borne_sup
	}
	if (vs%2){
	  v[0]=quotesubst(v[0],piece,piecev[vs-1],contextptr);
	  res += _integrate(gen(makevecteur(v[0],x,borne_inf,borne_sup),_SEQ__VECT),contextptr);
	}
	return ck_int_numerically(v0orig,x,aorig,borig,(chsign?-res:res),contextptr); // return chsign?-res:res;
      } // end piecewise
      if (intgab(v[0],x,borne_inf,borne_sup,res,contextptr)){
	// additional check for singularities in ggb mode
	if (calc_mode(contextptr)==1 || abs_calc_mode(contextptr)==38){
	  bool ordonne=is_greater(borne_sup,borne_inf,contextptr);
	  vecteur sp=protect_find_singularities(v[0],*x._IDNTptr,false,contextptr);
	  int sps=int(sp.size());
	  for (int i=0;i<sps;i++){
	    if ( (ordonne && is_strictly_greater(sp[i],borne_inf,contextptr) && is_strictly_greater(borne_sup,sp[i],contextptr) ) || 
		 (!ordonne && is_strictly_greater(sp[i],borne_sup,contextptr) && is_strictly_greater(borne_inf,sp[i],contextptr) )
		 ){
	      if (!is_zero(limit(v[0]*(sp[i]-x),*x._IDNTptr,sp[i],0,contextptr)))
		return undef;
	    }   
	  }
	}
      	return res;
      }
    }
    gen primitive;
    // fast check if we are integrating over a period
    // if so we can shift integration to
    // simplify one of the functions
    if (s==4 && !is_inf(borne_sup) && !is_inf(borne_inf)){
      gen v0ab=subst(v[0],x,x+borne_sup-borne_inf,false,contextptr)-v[0];
      gen tmpv0ab=recursive_ratnormal(v0ab,contextptr);
      if (!contains(tmpv0ab,undef)) 
	v0ab=tmpv0ab;
      if (is_zero(v0ab)){
	vecteur l(rlvarx(v[0],x));
	unsigned i=0; gen a,b;
	for (;i<l.size();++i){
	  if (l[i].type==_SYMB && is_linear_wrt(l[i]._SYMBptr->feuille,x,a,b,contextptr) && !is_zero(a) && !is_zero(b))
	    break;
	}
	if (i<l.size()){
	  vecteur vin=makevecteur(x,l[i]),vout=makevecteur(x-b/a,l[i]._SYMBptr->sommet(a*x,contextptr));
	  v[0]=subst(v[0],vin,vout,false,contextptr);
	}
      }
    }
    if (has_num_coeff(v[0])){
      primitive=integrate0(exact(v[0],contextptr),*x._IDNTptr,rem,contextptr);
      primitive=evalf(primitive,1,contextptr);
      rem=evalf(rem,1,contextptr);
    }
    else 
      primitive=integrate0(v[0],*x._IDNTptr,rem,contextptr);
    if (s==2 && calc_mode(contextptr)==1){
      ++ggb_intcounter;
      primitive += diffeq_constante(ggb_intcounter,contextptr);
    }
    if (s==2){
      if (is_zero(rem))
	return primitive;
      return primitive + symbolic(at_integrate,gen(makevecteur(rem,x),_SEQ__VECT));
    }
    // here s==4
    bool ordonne=is_greater(borne_sup,borne_inf,contextptr);
    bool desordonne=false;
#ifdef NO_STDEXCEPT
    if (ordonne){
      gen xval=x.eval(1,contextptr);
      giac_assume(symb_and(symb_superieur_egal(x,borne_inf),symb_inferieur_egal(x,borne_sup)),contextptr);
      primitive=eval(primitive,1,contextptr);
      sto(xval,x,contextptr);
      res=limit(primitive,*x._IDNTptr,borne_sup,-1,contextptr)-limit(primitive,*x._IDNTptr,borne_inf,1,contextptr);
    }
    else {
      if ( (desordonne=is_greater(borne_inf,borne_sup,contextptr) )){
	gen xval=x.eval(1,contextptr);
	giac_assume(symb_and(symb_superieur_egal(x,borne_sup),symb_inferieur_egal(x,borne_inf)),contextptr);
	sto(xval,x,contextptr);
	res=limit(primitive,*x._IDNTptr,borne_sup,1,contextptr)-limit(primitive,*x._IDNTptr,borne_inf,-1,contextptr) ;
      }
      else
	res=limit(primitive,*x._IDNTptr,borne_sup,0,contextptr)-limit(primitive,*x._IDNTptr,borne_inf,0,contextptr);
    }
#else
    try {
      if (ordonne){
	gen xval=x.eval(1,contextptr);
	giac_assume(symb_and(symb_superieur_egal(x,borne_inf),symb_inferieur_egal(x,borne_sup)),contextptr);
	primitive=eval(primitive,1,contextptr);
	sto(xval,x,contextptr);
	gen rs=limit(primitive,*x._IDNTptr,borne_sup,-1,contextptr);
	gen ri=limit(primitive,*x._IDNTptr,borne_inf,1,contextptr);
	res=rs-ri;
      }
      else {
	if ( (desordonne=is_greater(borne_inf,borne_sup,contextptr) )){
	  gen xval=x.eval(1,contextptr);
	  giac_assume(symb_and(symb_superieur_egal(x,borne_sup),symb_inferieur_egal(x,borne_inf)),contextptr);
	  sto(xval,x,contextptr);
	  res=limit(primitive,*x._IDNTptr,borne_sup,1,contextptr)-limit(primitive,*x._IDNTptr,borne_inf,-1,contextptr) ;
	}
	else
	  res=limit(primitive,*x._IDNTptr,borne_sup,0,contextptr)-limit(primitive,*x._IDNTptr,borne_inf,0,contextptr);
      }
    } catch (std::runtime_error & e){
      *logptr(contextptr) << "Error trying to find limit of " << primitive << endl;
      return symbolic(at_integrate,makesequence(v[0],x,borne_inf,borne_sup));
    }
#endif
    if (is_undef(res)){
      if (res.type==_STRNG && abs_calc_mode(contextptr)==38)
	return res;
      res=subst(primitive,*x._IDNTptr,borne_sup,false,contextptr)-subst(primitive,*x._IDNTptr,borne_inf,false,contextptr);
    }
    vecteur sp;
    sp=lidnt(evalf(makevecteur(primitive,borne_inf,borne_sup),1,contextptr));
    if (sp.size()>1){
      *logptr(contextptr) << gettext("No check were made for singular points of antiderivative ")+primitive.print(contextptr)+gettext(" for definite integration in [")+borne_inf.print(contextptr)+","+borne_sup.print(contextptr)+"]" << endl ;
      sp.clear();
    }
    else {
      if ((is_inf(borne_inf) || evalf_double(borne_inf,1,contextptr).type==_DOUBLE_)
	  && (is_inf(borne_sup) || evalf_double(borne_sup,1,contextptr).type==_DOUBLE_)){
	gen xval=x.eval(1,contextptr);
	if (is_greater(borne_sup,borne_inf,contextptr))
	  giac_assume(symb_and(symb_superieur_egal(x,borne_inf),symb_inferieur_egal(x,borne_sup)),contextptr);
	else
	  giac_assume(symb_and(symb_superieur_egal(x,borne_sup),symb_inferieur_egal(x,borne_inf)),contextptr);
	sp=protect_find_singularities(primitive,*x._IDNTptr,2,contextptr);
	sto(xval,x,contextptr);
	if (!lidnt(evalf_double(sp,1,contextptr)).empty())
	  return gensizeerr("Unable to handle singularities of "+ primitive.print(contextptr)+" at "+gen(sp).print(contextptr));
      }
      else
	sp=protect_find_singularities(primitive,*x._IDNTptr,0,contextptr);
      if (is_undef(sp)){
	*logptr(contextptr) << gettext("Unable to find singular points of antiderivative") << endl ;
	if (!tegral(v0orig,x,aorig,borig,1e-12,(1<<10),res,contextptr))
	  return undef;
	return res;
      }
    }
    // FIXME if v depends on an integer parameter, find values in inf,sup
    comprim(sp);
    int sps=int(sp.size());
    for (int i=0;i<sps;i++){
      if (sp[i].type==_DOUBLE_ || sp[i].type==_REAL || has_op(sp[i],*at_rootof)){
	*logptr(contextptr) << gettext("Unable to handle approx. or algebraic extension singular point ")+sp[i].print(contextptr)+gettext(" of antiderivative");
	if (!tegral(v0orig,x,aorig,borig,1e-12,(1<<10),res,contextptr))
	  return undef;
	return res;
      }
      if ( (ordonne && is_strictly_greater(sp[i],borne_inf,contextptr) && is_strictly_greater(borne_sup,sp[i],contextptr) ) || 
	   (desordonne && is_strictly_greater(sp[i],borne_sup,contextptr) && is_strictly_greater(borne_inf,sp[i],contextptr) )
	   )
	res += limit(primitive,*x._IDNTptr,sp[i],-1,contextptr)-limit(primitive,*x._IDNTptr,sp[i],1,contextptr);
    }
    if (!is_zero(rem)){
      if (is_inf(res))
	return symbolic(at_integrate,gen(makevecteur(v[0],x,v[2],v[3]),_SEQ__VECT));
      if (ordonne || !desordonne)
	res = res + symbolic(at_integrate,gen(makevecteur(rem,x,v[2],v[3]),_SEQ__VECT));
      else
	res = res - symbolic(at_integrate,gen(makevecteur(rem,x,v[3],v[2]),_SEQ__VECT));
      return res;
    }
    return ck_int_numerically(v0orig,x,aorig,borig,res,contextptr);
  }
  static const char _integrate_s []="integrate";
  static string texprintasintegrate(const gen & g,const char * s_orig,GIAC_CONTEXT){
    string s("\\int ");
    if (g.type!=_VECT)
      return s+gen2tex(g,contextptr);
    vecteur v(*g._VECTptr);
    int l(int(v.size()));
    if (!l)
      return s;
    if (l==1)
      return s+gen2tex(v.front(),contextptr);
    if (l==2)
      return s+gen2tex(v.front(),contextptr)+"\\, d"+gen2tex(v.back(),contextptr);
    if (l==4)
      return s+"_{"+gen2tex(v[2],contextptr)+"}^{"+gen2tex(v[3],contextptr)+"}"+gen2tex(v.front(),contextptr)+"\\, d"+gen2tex(v[1],contextptr);
    return s;
  }
  static define_unary_function_eval4_quoted (__integrate,&_integrate,_integrate_s,0,&texprintasintegrate);
  define_unary_function_ptr5( at_integrate ,alias_at_integrate,&__integrate,_QUOTE_ARGUMENTS,true);

  // called by approx_area
  double rombergo(const gen & f0,const gen & x, const gen & a_orig, const gen & b_orig, int n,GIAC_CONTEXT){
    //f est l'expression a integrer, x le nom de la variable, a et b les bornes
    // n si on veut faire 2^n subdivisions
    gen f=eval(f0,1,context0); // otherwise int(1/sqrt(x),x,0,1) fails on 38
    vector<double> T(n+1);
    //ligne du triangle de romberg avec T(n)=aire avec "pts du milieu" 
    //avec 2^n subdivisions
    double  h;
    gen at;
    //at sert a faire les substitutions c'est un gen = au debut a f((a+b)/2)
    //et en cours de prog egal a f(am)
    gen a=a_orig,b=b_orig;
    a=a.evalf(1,contextptr).evalf_double(1,contextptr);
    b=b.evalf(1,contextptr).evalf_double(1,contextptr);
    h=b._DOUBLE_val-a._DOUBLE_val;
    if (h==0)
      return 0;
    //h est la longueur de la subdivision
    //T[0] est l'aire du premier rectangle "pt milieu" f((a+b)/2)*(b-a)
    //puis T[j] = aire des rectangles "pt milieu" pour 2^j subdivisions
    double pui4;
    for (int j=0;j<=n;j++){
      //chaque fois que j augmente de 1 on double le nombre de subdivisions
    
      double ss;
      //ss est la somme provenant des valeurs de f aux points am ainsi rajoutes
      ss=0;
      gen am;
    
      am=a+gen(h/2);
      if (is_exactly_zero(am-a)){
	n=j-1;
	break;
      }
      while (is_greater(b,am,contextptr)){
	at=subst(f,x,am,false,contextptr).evalf(1,contextptr);
	ss=ss+at._DOUBLE_val;
	am=am+gen(h);
      }
      //T[j] est la nouvelle valeur de l'aire calculee avec les "pts  milieu"
      T[j]=ss*h;
    
      h=h/2;
    }
    //pui4 est la valeur de 4^k
    pui4=1;
    for (int j=1;j<=n;j++){ 
      pui4=pui4*4;
      for (int k=0;k<=n-j;k++){
	//on calcule T[k] en appliquant la formule de rec. de romberg
	//avec T[j] qui contient a chaque etape l'integrale par les pts milieu
	T[k]=(pui4*T[k+1]-T[k])/(pui4-1);
     
      }
      //on vient de remplir la kieme ligne on recommence avec j=j+1
      //on doit calculer les "pts du milieu" pour le nouv. j et le mettre ds T[j]
    }
 

    //c'est donc T[0] la meilleur approx de l'integrale
    return(T[0]);
  }

  // Not linked currently
  double rombergt(const gen & f,const gen & x, const gen & a, const gen & b, int n,GIAC_CONTEXT){
    //f est l'expression a integrer, x le nom de la variable, a et b les bornes
    // n si on veut faire 2^n subdivisions
    vector<double> T(n+1);
    //ligne du triangle de romberg avec T(n)=trapezes avec 2^n subdivisions
    double  h;
    gen at;
    //at sert a faire les substitutions c'est un gen egal au debut a f(a)+f(b)
    //et en cours de prog egal a f(am)
    h=b.evalf(1,contextptr)._DOUBLE_val-a.evalf(1,contextptr)._DOUBLE_val;
    if (h==0)
      return 0;
    //h est la longueur de la subdivision
    at=subst(f,x,b,false,contextptr).evalf(1,contextptr)+subst(f,x,a,false,contextptr).evalf(1,contextptr);
    T[0]=at._DOUBLE_val*h/2;
    //T[0] est l'aire du premier trapeze (f(a)+f(b))*(b-a)/2
    //puis T[j] = aire des trapezes pour 2^j subdivisions
    double pui4;
    for (int j=1;j<=n;j++){
      //chaque fois que j augmente de 1 on double le nombre de subdivisions
      h=h/2;
      double ss;
      //ss est la somme provenant des valeurs de f aux points am ainsi rajoutes
      ss=0;
      gen am;
      am=a+gen(h); 
      if (is_exactly_zero(am-a)){
	n=j-1;
	break;
      }
      while (is_greater(b,am,contextptr)){
	at=subst(f,x,am,false,contextptr).evalf(1,contextptr);
	ss=ss+at._DOUBLE_val;
	am=am+gen(2*h);
      }
      //T[j] est la nouvelle valeur de l'aire des trapezes
      T[j]=T[j-1]/2+ss*h;
      //pui4 est la valeur de 4^k
      pui4=1;
      for (int k=j-1;k>=0;k--){
	pui4=pui4*4;
	//on calcule T[k] en appliquant la formule de rec. de romberg
	//avec T[j] qui contient a chaque etape l'integrale par les trapezes
	T[k]=(pui4*T[k+1]-T[k])/(pui4-1);
      }
      //on vient de remplir la kieme ligne on recommence avec j=j+1
      //on doit calculer les trapezes pour le nouveau j et le mettre ds T[j]
    }
    //c'est donc T[0] la meilleur approx de l'integrale
    return(T[0]);
  }

  // find approx value of int(f) using Gauss quadrature with s=15 (order 30)
  // returns approx value of int(f), of int(abs(f)) and error estimate
  // error estimated using embedded order 14 and 6 method as
  // err1=abs(i30-i14); err2=abs(i30-i6); err1*(err1/err2)^2
#if 0
  static bool tegral_util(const gen & f,const gen &x, const gen &a,const gen &b,gen & i30,gen & i30abs, gen &err,GIAC_CONTEXT){
    gen h=evalf_double(b-a,1,contextptr),i14,i6;
    int s30=15,s14=14,s6=6;
    long_double c30[]={0.60037409897572857552e-2,0.31363303799647047846e-1,0.75896708294786391900e-1,0.13779113431991497629,0.21451391369573057623,0.30292432646121831505,0.39940295300128273885,0.50000000000000000000,0.60059704699871726115,0.69707567353878168495,0.78548608630426942377,0.86220886568008502371,0.92410329170521360810,0.96863669620035295215,0.99399625901024271424};
    long_double b30[]={0.15376620998058634177e-1,0.35183023744054062355e-1,0.53579610233585967506e-1,0.69785338963077157224e-1,0.83134602908496966777e-1,0.93080500007781105513e-1,0.99215742663555788228e-1,0.10128912096278063644,0.99215742663555788228e-1,0.93080500007781105514e-1,0.83134602908496966777e-1,0.69785338963077157224e-1,0.53579610233585967507e-1,0.35183023744054062355e-1,0.15376620998058634177e-1};
    long_double b14[]={0.21474028217339757006e-1,0.14373155100418764102e-1,0.92599218105237092609e-1,0.11827741709315709983e-1,0.15847003639679458478,0.38429189419875016111e-2,0.19741290152890658991,0.19741290152890658991,0.38429189419875016111e-2,0.15847003639679458478,0.11827741709315709983e-1,0.92599218105237092608e-1,0.14373155100418764102e-1,0.21474028217339757006e-1};
    long_double b6[]={0.10715760948621577132,0.31130901929813818033e-1,0.36171148858397041065,0,0.36171148858397041065,0.31130901929813818033e-1,0.10715760948621577132};
    vecteur v30(15),v30abs(15);
    for (int i=0;i<15;i++){
      v30[i]=evalf_double(subst(f,x,a+double(c30[i])*h,false,contextptr),1,contextptr);
      v30abs[i]=_l2norm(v30[i],contextptr);
      if (v30abs[i].type!=_DOUBLE_)
	return false;
    }
    i30abs=i30=i14=i6=0;
    for (int i=0;i<15;i++){
      i30 += double(b30[i])*v30[i];
      i30abs += double(b30[i])*v30abs[i];
    }
    for (int i=0;i<=6;i++){
      i14 += double(b14[i])*v30[i];
    }
    for (int i=8;i<=14;i++){
      i14 += double(b14[i-1])*v30[i];
    }
    for (int i=1;i<15;i+=2){
      if (i==7)
	continue;
      i6 += double(b6[(i-1)/2])*v30[i];
    }
    i30 = i30*h;
    i30abs = i30abs*h;
    i14 = i14*h;
    i6 = i6*h;
    gen err1=_l2norm(i30-i14,contextptr);
    gen err2=_l2norm(i30-i6,contextptr);
    // check if err1 and err2 corresponds to errors in h^14 and h^6
    if (is_greater(abs(14./6.-ln(err1,contextptr)/ln(err2,contextptr)),.1,contextptr))
      err=err1;
    else {
      err=err1/err2;
      err=err1*(err*err);
    }
    return true;
  }
#else // using -1..1 scaling instead of 0..1
  static bool tegral_util(const gen & f,const gen &x, const gen &a,const gen &b,gen & i30,gen & i30abs, gen &err,GIAC_CONTEXT){
    gen h=evalf_double(b-a,1,contextptr),i14,i6;
    //int s30=15,s14=14,s6=6;
    long_double c30[]={-0.98799251802048542849,-0.93727339240070590430,-0.84820658341042721620,-0.72441773136017004742,-0.57097217260853884754,-0.39415134707756336990,-0.20119409399743452230,0.00000000000000000000,0.20119409399743452230,0.39415134707756336990,0.57097217260853884754,0.72441773136017004742,0.84820658341042721620,0.93727339240070590430,0.98799251802048542849};
    long_double b30[]={0.15376620998058634177e-1,0.35183023744054062355e-1,0.53579610233585967506e-1,0.69785338963077157224e-1,0.83134602908496966777e-1,0.93080500007781105513e-1,0.99215742663555788228e-1,0.10128912096278063644,0.99215742663555788228e-1,0.93080500007781105514e-1,0.83134602908496966777e-1,0.69785338963077157224e-1,0.53579610233585967507e-1,0.35183023744054062355e-1,0.15376620998058634177e-1};
    long_double b14[]={0.21474028217339757006e-1,0.14373155100418764102e-1,0.92599218105237092609e-1,0.11827741709315709983e-1,0.15847003639679458478,0.38429189419875016111e-2,0.19741290152890658991,0.19741290152890658991,0.38429189419875016111e-2,0.15847003639679458478,0.11827741709315709983e-1,0.92599218105237092608e-1,0.14373155100418764102e-1,0.21474028217339757006e-1};
    long_double b6[]={0.10715760948621577132,0.31130901929813818033e-1,0.36171148858397041065,0,0.36171148858397041065,0.31130901929813818033e-1,0.10715760948621577132};
    vecteur v30(15),v30abs(15);
    for (int i=0;i<15;i++){
      v30[i]=evalf_double(eval(subst(f,x,((a+b)+double(c30[i])*h)/2,false,contextptr),1,contextptr),1,contextptr);
      v30abs[i]=_l2norm(v30[i],contextptr);
      if (v30abs[i].type!=_DOUBLE_)
	return false;
    }
    i30abs=i30=i14=i6=0;
    for (int i=0;i<=7;i++){
      i30 += double(b30[i])*v30[i];
      if (i<7)
	i30 += double(b30[14-i])*v30[14-i];
      i30abs += double(b30[i])*v30abs[i];
      if (i<7)
	i30abs += double(b30[14-i])*v30abs[14-i];
    }
    for (int i=0;i<=6;i++){
      i14 += double(b14[i])*v30[i];
    }
    for (int i=8;i<=14;i++){
      i14 += double(b14[i-1])*v30[i];
    }
    for (int i=1;i<15;i+=2){
      if (i==7)
	continue;
      i6 += double(b6[(i-1)/2])*v30[i];
    }
    i30 = i30*h;
    i30abs = i30abs*h;
    i14 = i14*h;
    i6 = i6*h;
    gen err1=_l2norm(i30-i14,contextptr);
    gen err2=_l2norm(i30-i6,contextptr);
    if (is_exactly_zero(err1) || is_exactly_zero(err2))
      err=0;
    else {
      // check if err1 and err2 corresponds to errors in h^14 and h^6
      if (is_greater(abs(14./6.-ln(err1,contextptr)/ln(err2,contextptr)),.1,contextptr))
	err=err1;
      else {
	err=err1/err2;
	err=err1*(err*err);
      }
    }
    return true;
  }
#endif

  // nmax=max number of subdivisions (may be 1000 or more...)
  bool tegral(const gen & f,const gen & x,const gen & a_,const gen &b_,const gen & eps,int nmax,gen & value,GIAC_CONTEXT){
    gen a=evalf(a_,1,contextptr),b=evalf(b_,1,contextptr);
    // adaptive integration, cf. Hairer
    gen i30,i30abs,err,maxerr,ERR,I30ABS;
    int maxerrpos;
    if (!tegral_util(f,x,a,b,i30,i30abs,err,contextptr))
      return false;
    vecteur v(1,makevecteur(a,b,i30,i30abs,err));
    for (;int(v.size())<nmax;){
      // sum of errors, check for end
      i30=I30ABS=ERR=maxerr=0;
      maxerrpos=0;
      for (unsigned i=0;i<v.size();++i){
	if (v[i].type!=_VECT || v[i]._VECTptr->size()<5)
	  return false;
	vecteur w=*v[i]._VECTptr;
	i30 = i30+w[2]; // += does not work in emscripten
	I30ABS = I30ABS+w[3];
	ERR = ERR+w[4];
	if (is_strictly_greater(w[4],maxerr,contextptr)){
	  maxerrpos=i;
	  maxerr=w[4];
	}
      }
      value=i30;
      // minimal number of intervals added for int(frac(x),x,0,6.4)
      if (!is_undef(ERR) && is_greater(eps,ERR/I30ABS,contextptr)) 
	return true;
      // cut interval at maxerrpos in 2 parts
      vecteur & w = *v[maxerrpos]._VECTptr;
      gen A=w[0],B=w[1],C=(A+B)/2;
      if (A==C || B==C){
	// can not subdivise anymore
	if (is_greater(1e-4,ERR/I30ABS,contextptr)){
	  *logptr(contextptr) << "Low accuracy, error estimate " << ERR/I30ABS << "\nError might be underestimated if initial boundary was +/-infinity" << endl;
	  return true;
	}
	return false; 
      }
      if (!tegral_util(f,x,A,C,i30,i30abs,err,contextptr)){
	if (is_greater(1e-4,ERR/I30ABS,contextptr)){
	  *logptr(contextptr) << "Low accuracy, error estimate " << ERR/I30ABS << "\nError might be underestimated if initial boundary was +/-infinity" << endl;
	  return true;
	}
	return false;
      }
      v[maxerrpos]=makevecteur(A,C,i30,i30abs,err);
      if (!tegral_util(f,x,C,B,i30,i30abs,err,contextptr)){
	if (is_greater(1e-4,ERR/I30ABS,contextptr)){
	  *logptr(contextptr) << "Low accuracy, error estimate " << ERR/I30ABS << "\nError might be underestimated if initial boundary was +/-infinity" << endl;
	  return true;
	}
	return false;
      }
      v.push_back(makevecteur(C,B,i30,i30abs,err));
    }
    return false; // too many iterations
  }

  gen romberg(const gen & f0,const gen & x0,const gen & a,const gen &b,const gen & eps,int nmax,GIAC_CONTEXT){
    return evalf_int(f0,x0,a,b,eps,nmax,true,contextptr);
  }
  gen evalf_int(const gen & f0,const gen & x0,const gen & a,const gen &b,const gen & eps,int nmax,bool romberg_method,GIAC_CONTEXT){
    gen x(x0),f(f0);
    if (x.type!=_IDNT){
      x=identificateur(" x");
      f=subst(f,x0,x,false,contextptr);
    }
    gen value=undef;
    if (!romberg_method && tegral(f,x,a,b,eps,(1 << nmax),value,contextptr))
      return value;
    if (!romberg_method)
      *logptr(contextptr) << "Adaptive method failure, will try with Romberg, last approximation was " << value << endl;
    // a, b and eps should be evalf-ed, and eps>0
    gen h=b-a;
    vecteur old_line,cur_line;
#ifdef NO_STDEXCEPT
    old_line.push_back(evalf(h*(limit(f,*x._IDNTptr,a,1,contextptr)+limit(f,*x._IDNTptr,b,-1,contextptr))/2,eval_level(contextptr),contextptr));
#else
    try {
      old_line.push_back(evalf(h*(limit(f,*x._IDNTptr,a,1,contextptr)+limit(f,*x._IDNTptr,b,-1,contextptr))/2,eval_level(contextptr),contextptr));
    } catch (std::runtime_error & ){
      old_line=vecteur(1,undef);
    }
#endif
    if (is_inf(old_line[0])|| is_undef(old_line[0]) || !lop(old_line[0],at_bounded_function).empty()){
      // FIXME middle point in arbitrary precision
      *logptr(contextptr) << gettext("Infinity or undefined limit at bounds.\nUsing middle point Romberg method") << endl;
      gen y=(a+b)/2;
      gen fy=subst(f,x,y,false,contextptr);
      // Workaround for undefined middle point
      if (is_undef(fy) || is_inf(fy)){
	fy=limit(f,*x._IDNTptr,y,0,contextptr);
	if (is_undef(fy) || is_inf(fy))
	  return undef;
      }
      old_line=vecteur(1,fy*h);
      // At the i-th step of the loop compute the middle approx of the integral
      // and use old_line to compute cur_line
      nmax=int(2.*nmax/3.+0.5);
      int n=3;
      h=(b-a)/3;
      for (int i=0;i<nmax;++i){
	cur_line.clear();
	// compute trapeze
	gen y=a+h/2,sum;
	if (is_exactly_zero(y-a))
	  return old_line;
	for (int j=0;j<n;++j){
	  if (j%3==1){
	    y=y+h; // skip, already computed
	    continue;
	  }
	  gen fy=subst(f,x,y,false,contextptr);
	  // Workaround if fy undefined
	  if (is_undef(fy) || is_inf(fy)){
	    fy=limit(f,*x._IDNTptr,y,0,contextptr);
	    if (is_undef(fy) || is_inf(fy))
	      return undef;
	  }
	  sum=sum+evalf(fy,eval_level(contextptr),contextptr);
	  y=y+h;
	}
	cur_line.push_back(old_line[0]/3+sum*h); 
	h=h/3;
	n = 3*n ;
	gen pui9=1;
	for (int j=0;j<=i;++j){
	  pui9=9*pui9;
	  cur_line.push_back((pui9*cur_line[j]-old_line[j])/(pui9-1));
	}
	gen err=abs(old_line[i]-cur_line[i+1],contextptr);
	if (i>nmax/2 && (ck_is_greater(eps,err,contextptr)
			 || ck_is_greater(eps*abs(cur_line[i+1],contextptr),err,contextptr)) )
	  return (old_line[i]+cur_line[i+1])/2;
	if (i!=nmax-1)
	  old_line=cur_line;
      }
      if (calc_mode(contextptr)==1)
	return undef;
      *logptr(contextptr) << gettext("Unable to find numeric integral using Romberg method, returning the last approximations") << endl;
      cur_line=is_undef(value)?makevecteur(old_line.back(),cur_line.back()):makevecteur(cur_line.back(),value);
      return cur_line;
      // return rombergo(f,x,a,b,nmax,contextptr);
    }
    int n=1;
    // At the i-th step of the loop compute the trapeze approx of the integral
    // and use old_line to compute cur_line
    for (int i=0;i<nmax;++i){
      cur_line.clear();
      // compute trapeze
      gen y=a+h/2,sum;
      if (is_exactly_zero(y-a))
	return old_line;
      for (int j=0;j<n;++j){
	gen fy=subst(f,x,y,false,contextptr);
	// Workaround for romberg((1-cos(x))/x^2,x,-1,1)?
	if (is_undef(fy) || is_inf(fy)){
	  fy=limit(f,*x._IDNTptr,y,0,contextptr);
	  if (is_undef(fy) || is_inf(fy))
	    return undef;
	}
	sum=sum+evalf(fy,eval_level(contextptr),contextptr);
	y=y+h;
      }
      h=h/2;
      cur_line.push_back(old_line[0]/2+sum*h); 
      n = 2*n ;
      gen pui4=1;
      for (int j=0;j<=i;++j){
	pui4=4*pui4;
	cur_line.push_back((pui4*cur_line[j]-old_line[j])/(pui4-1));
      }
      gen err=abs(old_line[i]-cur_line[i+1],contextptr);
      if (i>nmax/2 && (ck_is_greater(eps,err,contextptr)
		       || ck_is_greater(eps*abs(cur_line[i+1],contextptr),err,contextptr)) )
	return (old_line[i]+cur_line[i+1])/2;
      if (i!=nmax-1)
	old_line=cur_line;
    }
    if (calc_mode(contextptr)==1)
      return undef;
    *logptr(contextptr) << gettext("Unable to find numeric integral using Romberg method, returning the last approximations") << endl;
    cur_line=is_undef(value)?makevecteur(old_line.back(),cur_line.back()):makevecteur(cur_line.back(),value);
    return cur_line;
  }
  gen ggb_var(const gen & f){
    vecteur l=lidnt(makevecteur(cst_pi,unsigned_inf,undef,f));
    l=vecteur(l.begin()+3,l.end());
    if (l.empty() || equalposcomp(l,vx_var))
      return vx_var;
    const_iterateur it=l.begin(),itend=l.end();
    for (;it!=itend;++it){
      string s=it->print(context0);
      if (s[s.size()-1]=='x')
	return *it;
    }
    return l.front();
  }
  gen intnum(const gen & args,bool romberg_method,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return gensizeerr(contextptr);
    const_iterateur it=args._VECTptr->begin(),itend=args._VECTptr->end();
    gen f=*it;
    ++it;
    gen x=*it,a,b;
    if (it<itend-1){
      ++it;
      a=*it;
      ++it;
      if (it<itend)
	b=*it;
      else {
	b=a;
	a=x;
	x=ggb_var(f);
	--it;
      }
    }
    else {
      bool ok=false;
      if (is_equal(x)){
	a=x._SYMBptr->feuille;
	if (a.type==_VECT && a._VECTptr->size()==2){
	  x=a._VECTptr->front();
	  a=a._VECTptr->back();
	  if (a.is_symb_of_sommet(at_interval)){
	    a=a._SYMBptr->feuille;
	    if (a.type==_VECT && a._VECTptr->size()==2){
	      b=a._VECTptr->back();
	      a=a._VECTptr->front();
	      ok=true;
	    }
	  }
	} 
      }
      if (!ok)
	return symbolic(at_integrate,args);
    }
    if (is_inf(a) || is_inf(b)){ // change of variables x=tan(t), t=atan(x)
      gen tanx(tan(x,contextptr));
      f=subst(f,x,tanx,false,contextptr)*(1+pow(tanx,2));
      a=atan(a,contextptr);
      b=atan(b,contextptr);
      gen res=intnum(makesequence(f,x,a,b),romberg_method,contextptr);
      if (!angle_radian(contextptr))
      {
	if(angle_degree(contextptr))
          res=deg2rad_d*res; 
        //grad
        else 
          res = grad2rad_d*res;
      }
      return res;
    }
    a=a.evalf(1,contextptr);
    b=b.evalf(1,contextptr);
    if (a.type==_FLOAT_) a=evalf_double(a,1,contextptr);
    if (b.type==_FLOAT_) b=evalf_double(b,1,contextptr);
    ++it;
    gen eps(epsilon(contextptr));
    int n=11;
    if (it!=itend){
      eps=evalf(abs(*it,contextptr),1,contextptr);
      ++it;
      if (it!=itend && it->type==_INT_)
	n=it->val;
    }
    if (eps.type!=_DOUBLE_ && eps.type!=_FLOAT_ && eps.type!=_REAL)
      eps=epsilon(contextptr);
    if ( x.type!=_IDNT || 
	 (a.type!=_DOUBLE_ && a.type!=_REAL) 
	 || (b.type!=_DOUBLE_ && b.type!=_REAL) 
	 )
      return symbolic(at_integrate,args);
    return evalf_int(f,x,a,b,eps,n,romberg_method,contextptr);
  }
  gen _romberg(const gen & args,GIAC_CONTEXT) {
    return intnum(args,true,contextptr);
  }
  static const char _romberg_s []="romberg";
  static define_unary_function_eval (__romberg,&_romberg,_romberg_s);
  define_unary_function_ptr5( at_romberg ,alias_at_romberg,&__romberg,0,true);

  gen _gaussquad(const gen & args,GIAC_CONTEXT) {
    return intnum(args,false,contextptr);
  }
  static const char _gaussquad_s []="gaussquad";
  static define_unary_function_eval (__gaussquad,&_gaussquad,_gaussquad_s);
  define_unary_function_ptr5( at_gaussquad ,alias_at_gaussquad,&__gaussquad,0,true);

/**********************************************************************
* Desc:		Solve P(x+1)-P(x)=Q(x)
* Algo:		degree of P=degree of Q+1, constant coeff 0
*		If P=Sigma a_k x^k then write linear system for a_k
*		Columns of the matrix of the system are lines
*		of the Pascal triangle without the first element
*		(since we must substract identity matrix to the triangle)
*		a_1 a_2 a_3 ... a_n+1		coeff of Q
*		1   1   1	1	X^0
*		0   2	3	n+1	X^1
*		0   0	3	...	X^2
*		0   0	0	n+1     X^n
**********************************************************************/
  static vecteur solveP_x_plus_1_minus_P_x(const vecteur & Q){
      vecteur v(1,plus_one);
      matrice m;
      int n=int(Q.size());
      for (int i=0;i<n;++i){
          v=pascal_next_line(v);
          vecteur v_copy(v);
	  if (i!=n-1){
	    v_copy[i+1]=zero;
	    for (int j=0;j<n-i-2;j++)
	      v_copy.push_back(zero);
	  }
	  else
	    v_copy.pop_back();
          m.push_back(v_copy);
      }
      vecteur Q_copy(Q);
      reverse(Q_copy.begin(),Q_copy.end());
      m.push_back(Q_copy);
      m=mtran(m);
          // reduce matrix, solution P are diag coeff in reverse order
      m=mrref(m,context0); // ok
      vecteur res(n+1);
      for (int i=0;i<n;++i)
          res[n-i-1]=rdiv(m[i][n],m[i][i],context0);
      return res;
      
  }

  // true if v2[x]=v1[x-n], false otherwise
  static bool is_shift_of(const vecteur & v1,const vecteur &v2,int & n){
    int s1=int(v1.size()),s2=int(v2.size());
    if (s1!=s2 || v1[0]!=v2[0])
      return false;
    if (s1<2)
      return false; // setsizeerr(contextptr);
    gen e=(v1[1]-v2[1])/v1[0];
    if (e.type!=_INT_)
      return false;
    if (e.val % (s1-1))
      return false;
    n=e.val / (s1-1);
    return v1==taylor(v2,n);
  }

  bool rational_sum(const gen & e,const gen & x,gen & res,gen& remains_to_sum,bool allow_psi,GIAC_CONTEXT){
    // first detect rational fraction
    vecteur v(lvarxpow(e,x));
    if (v.empty()){
      res = e*x;
      remains_to_sum = 0;
      return true;
    }
    if ( (v.size()!=1) || (v.front()!=x) ){
      remains_to_sum=e;
      return false;
    }
    lvar(e,v);
    gen r=e2r(e,v,contextptr),r_num,r_den;
    fxnd(r,r_num,r_den);
    if (r_num.type==_EXT){
      remains_to_sum=e;
      return false;
    }
    if ((r_den.type!=_POLY) || (!r_den._POLYptr->lexsorted_degree())){ // polynomial w.r.t. x
      vecteur Q;
      if (r_num.type!=_POLY){
	res=r_num*v.front()/r2e(r,v,contextptr);
	return true;
      }
      Q=polynome2poly1(*r_num._POLYptr,1);
      vecteur P(solveP_x_plus_1_minus_P_x(Q));
      gen den;
      lcmdeno(P,den,contextptr); // lcmdeno_converted?
      r_num=poly12polynome(P,1,r_num._POLYptr->dim);
      r=rdiv(r_num,r_den*den,contextptr);
      res=r2e(r,v,contextptr);
      return true;
    }
    // rational fraction wrt x
    int s=r_den._POLYptr->dim;
    polynome den(*r_den._POLYptr),num(s);
    if (r_num.type==_POLY)
      num=*r_num._POLYptr;
    else
      num=polynome(r_num,s);
    polynome p_content(s);
    // partial fraction decomposition
    factorization vden;
    gen extra_div=1;
    factor(den,p_content,vden,false,/* withsqrt */false,/* complex */ true,1,extra_div); 
    vector< pf<gen> > pfdecomp;
    polynome ipnum(s),ipden(s);
    partfrac(num,den,vden,pfdecomp,ipnum,ipden);
    // discrete antiderivative of integral part
    vecteur Q(polynome2poly1(ipnum,1));
    vecteur P(solveP_x_plus_1_minus_P_x(Q));
    ipnum=poly12polynome(P,1,ipnum.dim);
    r=rdiv(ipnum,ipden,contextptr);
    res=r2e(r,v,contextptr);
    int vdim=int(v.size());
    // detect integral shifted denominators
    vector<pf1> shiftfree_pfdecomp;
    vector< pf<gen> >::iterator it=pfdecomp.begin();
    vector< pf<gen> >::const_iterator itend=pfdecomp.end();
    for (;it!=itend;++it){
      vecteur it_fact(polynome2poly1(it->fact,1));
      vector<pf1>::iterator jt=shiftfree_pfdecomp.begin();
      vector<pf1>::const_iterator jtend=shiftfree_pfdecomp.end();
      int k;
      for (;jt!=jtend;++jt){
	if (is_shift_of(it_fact,jt->fact,k))
	  break;
      }
      if (jt==jtend)
	shiftfree_pfdecomp.push_back(pf1(it->num,it->den,it->fact,it->mult));
      else { // it_fact is the shift of jt->fact
	vecteur it_num(polynome2poly1(it->num,1)),it_den(polynome2poly1(it->den,1));
	// check which one has the highest multiplicity
	if (jt->mult<it->mult){ // we must swap to keep highest mult in *jt
	  std::swap(jt->num,it_num);
	  std::swap(jt->den,it_den);
	  std::swap(jt->fact,it_fact);
	  std::swap(jt->mult,it->mult);
	  k=-k;
	}
	// do the shift (this will modify jt->num and the result res)
	int decal;
	if (k<0){
	  decal=1;
	  k=-k;
	}
	else
	  decal=-1;
	for (int j=0;j<k;++j){
	  if (decal>0)
	    res=res-rdiv(r2e(poly12polynome(it_num,1,vdim),v,contextptr),r2e(poly12polynome(it_den,1,vdim),v,contextptr),contextptr);
	  it_num=taylor(it_num,decal);
	  it_den=taylor(it_den,decal);
	  if (decal<0)
	    res=res+rdiv(r2e(poly12polynome(it_num,1,vdim),v,contextptr),r2e(poly12polynome(it_den,1,vdim),v,contextptr),contextptr);
	}
	modpoly constante=jt->den/it_den;
	gen const_den;
	lcmdeno(constante,const_den,contextptr); // lcmdeno_converted?
	// should check if constante is a fraction
	jt->num=constante*it_num+const_den*jt->num;
	jt->den=const_den*jt->den;
      } // end else
    } // end for (;it!=itend;++it)
    // now add psi parts for every element of the shiftfree decomposition
    vector<pf1>::iterator jt=shiftfree_pfdecomp.begin();
    vector<pf1>::const_iterator jtend=shiftfree_pfdecomp.end();
    for (;jt!=jtend;++jt){
      vecteur & jtfact = jt->fact;
      // vecteur & jtnum = jt->num;
      vecteur & jtden = jt->den;
      if (jtfact.size()!=2){ // add to remains_to_sum
	remains_to_sum=remains_to_sum+rdiv(r2e(poly12polynome(jt->num,1,vdim),v,contextptr),r2e(poly12polynome(jt->den,1,vdim),v,contextptr),contextptr);
      }
      else {
	gen racine=-rdiv(jtfact.back(),jtfact.front(),contextptr);
	vecteur dec=taylor(jt->num,racine);
	vecteur vv=cdr_VECT(v);
	gen coeff(plus_one);
	int decal=int(jt->mult-dec.size());
	for (int i=0;i<jt->mult;++i){
	  if (i>=decal){
	    if (!allow_psi)
	      return false;
	    res=res+r2e(rdiv(dec[i-decal],coeff*jtden.front(),contextptr),vv,contextptr)*Psi(x-r2e(racine,vv,contextptr),i,contextptr);
	  }
	  coeff=gen(-i-1)*coeff;
	}
      }
    } // end for(;jt!=jtend;++jt)
    return true; // end non constant denominator
  } // end rational fraction or polynomial

  polynome taylor(const polynome & P,const gen & g){
    vecteur v(polynome2poly1(P,1));
    v=taylor(v,g);
    return poly12polynome(v,1,P.dim);
  }


  vecteur decalage_(const polynome & A,const polynome & B){
    int s=A.dim;
    // find integer roots of resultant of A(x),B(x+t) with respect to x
    vecteur l(s);
    for (int i=0;i<s;++i)
      l[i]=identificateur("x"+print_INT_(i));
    int adeg=A.lexsorted_degree();
    int bdeg=B.lexsorted_degree(); // total degree of B(x+t) is bdeg, total degree of A is adeg
    // therefore total degree of resultant is <= adeg*bdeg
    vecteur y(adeg*bdeg+1),x(adeg*bdeg+1);
    vecteur bb(polynome2poly1(B,1));
    for (int i=0;i<=adeg*bdeg;++i){
      x[i]=i;
      polynome b=poly12polynome(bb,1,s);
      y[i]=r2e(Tresultant<gen>(A,b),l,context0);
      bb=taylor(bb,1);
    }
    gen resu=_lagrange(makesequence(x,y,l.front()),context0);
    resu=e2r(resu,l,context0);
    if (resu.type!=_POLY)
      return vecteur(0);
    polynome pres=*resu._POLYptr;
    // Make the list of the positive integer roots k in t of the resultant
    return iroots(pres);
  }
  // IMPROVE: eval A and B at other variables to detect possible integer roots
  // then try gcd(A(x),B(x+t))
  vecteur decalage(const polynome & A,const polynome & B){
    int s=A.dim;
    if (s==1)
      return decalage_(A,B);
    vecteur l(s),L(s);
    for (int i=0;i<s;++i)
      l[i]=identificateur("x"+print_INT_(i));
    gen a=r2e(A,l,context0);
    gen b=r2e(B,l,context0);
    gen t=identificateur("t");
    gen r=_sylvester(makesequence(a,subst(b,l[0],l[0]+t,false,context0),l[0]),context0);
    L[0]=l[0];
    gen r0=_det(subst(r,l,L,false,context0),context0);
    if (is_zero(derive(r0,t,context0))){
      int essai=0;
      for (;essai<s;++essai){
	L=vranm(s,0,0); // find random evaluation
	L[0]=l[0];
	r0=_det(subst(r,l,L,false,context0),context0);
	if (!is_zero(derive(r0,t,context0)))
	  break;
      }
      if (essai==s)
	return decalage_(A,B);
    }
    r0=e2r(r0,vecteur(1,t),context0);
    if (r0.type!=_POLY)
      return decalage_(A,B);
    vecteur v=iroots(*r0._POLYptr);
    vecteur res;
    for (int i=0;i<v.size();++i){
      gen ti=v[i];
      gen bti=subst(b,l[0],l[0]+ti,false,context0);
      gen g=gcd(a,bti,context0);
      if (!is_zero(derive(g,l[0],context0)))
	res.push_back(ti);
    }
    return res;
  }

  // Write a fraction A/B as E[P]/P*Q/E[R] where E[P]=subst(P,x,x+1)
  // and Q and all positive shifts of R are prime together
  void AB2PQR(const polynome & A,const polynome & B,polynome & P,polynome & Q, polynome & R){
    int s=A.dim;
    // First find integer roots of resultant of A(x),B(x+t) with respect to x
#if 1
    std::vector< facteur< tensor<gen> > > vA(Tsqff_char0(A)),vB(Tsqff_char0(B));
    std::vector< facteur< tensor<gen> > >::const_iterator itA=vA.begin(),itAend=vA.end(),itB=vB.begin(),itBend=vB.end();
    vecteur racines;
    for (;itA!=itAend;++itA){
      for (;itB!=itBend;++itB){
	racines=mergeset(racines,decalage(itA->fact,itB->fact));
      }
    }
#else    
    polynome a(A.untrunc1()); // add the t parameter
    polynome b(B.untrunc1());
    // exchange var 1 (parameter t) and 2 (x variable)
    vector<int> i=transposition(0,1,s+1);
    a.reorder(i);
    b.reorder(i);
    // now translate b by t
    vecteur bb(polynome2poly1(b,1));
    polynome t(monomial<gen>(plus_one,1,1,s));
    bb=taylor(bb,t);
    b=poly12polynome(bb,1,s+1);
    polynome pres=Tresultant<gen>(a,b);
    pres=pres.trunc1();
    // Make the list of the positive integer roots k in t of the resultant
    vecteur racines(iroots(pres));
#endif
    // The algorithm begins with P0=1 Q0=A R0=B
    P=polynome(monomial<gen>(plus_one,s));
    Q=A;
    R=B;
    int d=int(racines.size());
    for (int i=0;i<d;++i){
      gen k=racines[i];
      if (k.type!=_INT_ || k.val<=0)
	continue;
      // Then compute Pi Qi Ri so that E[Pi]/Pi*Qi/Ri=A/B
      // for each positive integer root k 
      // gcd[Qi,E^k[Ri]]=Y!=1 then Qi=Y*Q_{i+1}, Ri=E^-k[Y]*R_{i+1}
      // hence Qi/Ri=Q_{i+1}/R_{i+1}* Y/E^[-k]Y 
      polynome Y=gcd(Q,taylor(R,k));
      Q=Q/Y;
      polynome Yk=taylor(Y,-k);
      R=R/Yk;
      // therefore P_{i+1}=Pi*E^[-k]Y*...*E^[-1]Y 
      for (int j=-k.val;j<0;++j){
	P=P*Yk;
	Yk=taylor(Yk,plus_one);
      }
    }
    // At the end R=E^[-1] R_i
    R=taylor(R,minus_one);
  }

  // Solve P = Q E[Y] - R Y for Y
  // return true if there is a solution Y (solution is more precisely Y/deno)
  bool gosper(const polynome & P,const polynome & Q,const polynome & R,polynome & Y,gen & deno,GIAC_CONTEXT){
    // First find degree of Y
    // if q>r then y=p-q, if q<r then y=p-r, (if y<0 return false)
    // if q==r then y=p-q or p (p only if same leading coeff in Q,R)
    int p=P.lexsorted_degree(),q=Q.lexsorted_degree(),r=R.lexsorted_degree(),y;
    vecteur vP(polynome2poly1(P,1)),vQ(polynome2poly1(Q,1)),vR(polynome2poly1(R,1));
    gen qq=Tfirstcoeff<gen>(Q),rr=Tfirstcoeff<gen>(R);
    if (q==r && qq==rr){ // cancellation
      ++p;
      y=p-giacmax(q,r);
      if (q>0){
	vecteur vq=polynome2poly1(Q,1),vr=polynome2poly1(R,1);
	gen ydeg=(vr[1]-vq[1])/qq;//gen ydeg=(vr[q-1]-vq[q-1])/qq;
	if (ydeg.type==_INT_ && ydeg.val>y){
	  y=ydeg.val;
	  p=y+q-1;
	}
      }
    }
    else
      y=p-giacmax(q,r);
    if (y<0)
      return false;
    // Then solve a linear system with p+1 equations and y+1 unknowns
    // (p+1 rows, y+1 columns)
    // built the matrix of the system column by column
    // the column i is (X+1)^i*Q-X^i*R
    vecteur v(1,plus_one); // this will contain (X+1)^i using pascal_next_line
    vecteur w(v); // this is X^i
    matrice m;
    for (int i=0;i<=y;++i){
      vecteur current=v*vQ-w*vR;
      // adjust current size to p
      lrdm(current,p);
      m.push_back(current);
      v=pascal_next_line(v);
      w.push_back(zero);
    }
    reverse(m.begin(),m.end()); // higher coeff at the beginning
    // last column is P
    lrdm(vP,p);
    m.push_back(vP);
    m=mtran(m);
    int st=step_infolevel(contextptr);
    step_infolevel(contextptr)=0;
    m=mrref(m,contextptr);
    step_infolevel(contextptr)=st;
    vecteur res(y+1);
    for (int i=0;i<=y;++i){
      if (is_zero(m[i][i]))
	return false;
      res[i]=m[i][y+1]/m[i][i];
    }
    lcmdeno(res,deno,contextptr); // lcmdeno_converted?
    Y=poly12polynome(res,1,P.dim);
    return p==y || is_zero(m[y+1]);
    // Or alternatively do a Rothstein-Trager like method if Q non constant
    // Let P = Q U + R V with deg(V)<deg(Q)
    // then Q(E(Y)-U)=R(Y+V)
    // hence Q divides Y+V
    // If we know that deg(Y)<deg(Q) then check that Y=-V is solution
    // Otherwise let Y+V=Qy
    // Then QE(Qy-V) -R(Qy-V)=P=QU+RV
    // hence E(Qy-V)-Ry=U
    // we are reduced to solve E(Q)y-Ry=U+E(V) with deg(y)=deg(Y)-deg(Q)
  }

  // Check for hypergeometric e, if true
  // write e(x+1)/e(x) as P(n+1)/P(n)*Q(x)/R(x+1) 
  bool is_hypergeometric(const gen & e,const identificateur &x,vecteur &v,polynome & P,polynome & Q,polynome & R,GIAC_CONTEXT){
    v=lvarx(e,x);
    if (!loptab(v,sincostan_tab).empty() || !loptab(v,asinacosatan_tab).empty() || !lop(v,at_Psi).empty())
      return false;
    // if v contains a non linear exp abort
    int vs=int(v.size());
    gen a,b;
    for (int i=0;i<vs;++i){
      if (v[i].is_symb_of_sommet(at_exp) && !is_linear_wrt(v[i]._SYMBptr->feuille,x,a,b,contextptr))
	return false;
    }
    gen ratio=simplify(subst(e,x,x+1,false,contextptr)/e,contextptr);
    if (is_undef(ratio))
      return false;
    v=lvarx(makevecteur(ratio,x),x);
    if ( (v.size()!=1) || (v.front()!=x) ){
      ratio=simplify(_texpand(ratio,contextptr),contextptr);
      v=lvarx(makevecteur(ratio,x),x);
      if ( (v.size()!=1) || (v.front()!=x) )
	return false;
    }
    lvar(ratio,v);
    for (unsigned i=1;i<v.size();++i){
      if (!is_zero(derive(v[i],x,contextptr)))
	return false;
    }
    int s=int(v.size());
    gen f=e2r(ratio,v,contextptr);
    polynome A(s),B(s);
    if (f.type==_FRAC){
      A=gen2poly(f._FRACptr->num,s);
      B=gen2poly(f._FRACptr->den,s);
    }
    else {
      A=gen2poly(f,s);
      B=gen2poly(plus_one,s);
    }
    AB2PQR(A,B,P,Q,R); // A/B as E[P]/P*Q/E[R]
    return true;
  }

  static gen inner_sum(const gen & e,const gen & x,gen & remains_to_sum,GIAC_CONTEXT){
    gen res;
    if (rational_sum(e,x,res,remains_to_sum,true,contextptr))
      return res;
    polynome P,Q,R;
    vecteur v;
    if (!is_hypergeometric(e,*x._IDNTptr,v,P,Q,R,contextptr)){
      remains_to_sum=e;
      return zero;
    }
    int s=int(v.size());
    gen deno;
    polynome Y(s);
    if (!gosper(P,Q,R,Y,deno,contextptr)){
      remains_to_sum=e;
      return zero;
    }
    remains_to_sum=zero;
    gen facteur=r2e(Y*R,v,contextptr)/r2e(P,v,contextptr)/r2e(deno,vecteur(v.begin()+1,v.end()),contextptr);
    return simplify(e*facteur,contextptr);
  }

  // discrete antiderivative
  gen sum(const gen & e,const gen & x,gen & remains_to_sum,GIAC_CONTEXT){
    if (x.type!=_IDNT)
      return gensizeerr(contextptr);
    vecteur v=lvarx(e,x);
    v=loptab(v,sincostan_tab);
    // keep only sincostan which are linear wrt x
    vecteur newv(v);
    v.clear();
    int s=int(newv.size());
    for (int i=0;i<s;++i){
      gen a,b;
      if (is_linear_wrt(newv[i]._SYMBptr->feuille,x,a,b,contextptr))
	v.push_back(newv[i]);
    }
    if (!v.empty()){
      gen w=trig2exp(v,contextptr);
      gen e1=_lin(subst(e,v,*w._VECTptr,true,contextptr),contextptr);
      return _simplify(_evalc(linear_apply(e1,x,remains_to_sum,contextptr,inner_sum),contextptr),contextptr); 
    }
    else
      return linear_apply(e,x,remains_to_sum,contextptr,inner_sum); 
  }
  
  // discrete antiderivative evaluated
  gen sum_loop(const gen & e,const gen & x,int i,int j,GIAC_CONTEXT){
    gen f(e),res;
    if (i>j){
      int tmp=j;
      j=i-1;
      i=tmp+1;
      f=-e;
    }
    for (;i<=j;++i){
      res=res+subst(f,x,i,false,contextptr).eval(eval_level(contextptr),contextptr);
    }
    return res;
  }

  gen sum(const gen & e,const gen & x,const gen & a,const gen &b,GIAC_CONTEXT){
    if ( (a.type==_INT_) && (b.type==_INT_) && (absint(b.val-a.val)<100) )
      return sum_loop(e,x,a.val,b.val,contextptr);
    gen res;
    if ( sumab(e,x,a,b,res,true,contextptr) )
      return res;
    gen remains_to_sum;
#ifdef EMCC
    res=sum(e,x,remains_to_sum,contextptr);
#else
    gen oldx=eval(x,1,contextptr),X(x);
    if (!assume_t_in_ab(X,a,b,false,false,contextptr))
      return gensizeerr(contextptr);
    res=sum(e,x,remains_to_sum,contextptr);
    sto(oldx,X,contextptr);
#endif
    gen tmp1=( (is_inf(b) && x.type==_IDNT)?limit(res,*x._IDNTptr,b,0,contextptr):subst(res,x,b+1,false,contextptr));
    gen tmp2=(is_inf(a) && x.type==_IDNT)?limit(res,*x._IDNTptr,a,0,contextptr):subst(res,x,a,false,contextptr);
    res=tmp1-tmp2;
    if (is_zero(remains_to_sum))
      return res;
    if ( (a.type==_INT_) && (b.type==_INT_) && (absint(b.val-a.val)<max_sum_add(contextptr)) )
      return res+sum_loop(remains_to_sum,x,a.val,b.val,contextptr);
    return symbolic(at_sum,gen(makevecteur(e,x,a,b),_SEQ__VECT));
  }
  
  gen prodsum(const gen & g,bool isprod){
    if (g.type!=_VECT)
      return gensizeerr(gettext("prodsum"));
    vecteur v=*g._VECTptr;
    int s=int(v.size());
    if (!s)
      return isprod?1:0;
    int debut=1,fin=s;
    if (v[0].type==_VECT && g.subtype==_SEQ__VECT && s>1 && v[1].type==_INT_){
      debut=giacmax(1,v[1].val);
      if (s>2 && v[2].type==_INT_)
	fin=v[2].val;
      v=*v[0]._VECTptr;
      s=int(v.size());
      fin=giacmin(s,fin);
    }
    gen res;
    if (isprod){
      res=plus_one;
      for (--debut;debut<fin;++debut){
	res=matrix_apply(res,v[debut],prod);
      }
    }
    else {
      for (--debut;debut<fin;++debut){
	res=matrix_apply(res,v[debut],somme);
      }
    }
    return res;
  }

#if 0
  static void local_sto(const gen & value,const identificateur & i,GIAC_CONTEXT){
    if (contextptr)
      (*contextptr->tabptr)[i.id_name]=value;
    else 
      i.localvalue->back()=value;
  }

  static void local_sto_increment(const gen & value,const identificateur & i,GIAC_CONTEXT){
    if (contextptr)
      (*contextptr->tabptr)[i.id_name] += value;
    else
      i.localvalue->back() += value;
  }

  static void local_sto_int(int value,const identificateur & i,GIAC_CONTEXT){
    if (contextptr)
      (*contextptr->tabptr)[i.id_name].val=value;
    else
      i.localvalue->back().val=value;
  }

  static void local_sto_int_increment(int value,const identificateur & i,GIAC_CONTEXT){
    if (contextptr)
      (*contextptr->tabptr)[i.id_name].val += value;
    else
      i.localvalue->back().val += value;
  }
#endif

  // type=0 for seq, 1 for prod, 2 for sum
  gen seqprod(const gen & g,int type,GIAC_CONTEXT){
    vecteur v(gen2vecteur(g));
    if (v.size()==1)
      v=gen2vecteur(eval(g,contextptr));
    if (v.size()<4){
      if (type==0 && v.size()==3 && v[2].type==_VECT){
	// for example seq(2^k,k,[1,2,5])
	gen f=_unapply(makesequence(v[0],v[1]),contextptr);
	return _map(makesequence(v[2],f),contextptr);
      }
      if (v.size()==3 && v[1].is_symb_of_sommet(at_equal) && v[1]._SYMBptr->feuille[1].is_symb_of_sommet(at_interval)){
	gen f=v[1]._SYMBptr->feuille;
	gen v1=f[0];
	gen v2=f[1]._SYMBptr->feuille[0],v3=f[1]._SYMBptr->feuille[1];
	return seqprod(makevecteur(v[0],v1,v2,v3,v[2]),type,contextptr);
      }
      if (v.size()==3 && !v[1].is_symb_of_sommet(at_equal) && g.subtype==_SEQ__VECT)
	return change_subtype(seqprod(gen(makevecteur(symb_interval(v[0],v[1]),v[2]),_SEQ__VECT),type,contextptr),0);
      if (type==0)
	return _dollar(g,contextptr);
      if (type==1)
	return prodsum(v,true);	
      if (type==2)
	return prodsum(v,false);
      return gentoofewargs("");
    }
    // v[1]=eval(v[1]);
    v[2]=eval(v[2],contextptr);
    v[3]=eval(v[3],contextptr);
    gen step=1;
    gen tmp;
    if (v.size()==5)
      step=eval(v[4],contextptr);
    if (is_zero(step))
      return gensizeerr(contextptr);
    if (!is_integral(v[3]) || !is_integral(v[2])){
      if (v.size()==4 && g.subtype==_SEQ__VECT)
	return gentypeerr(contextptr);
      if (type==1 && (g.subtype!=_SEQ__VECT || v.size()!=5))
	return prodsum(v,true);
      if (type==2 && (g.subtype!=_SEQ__VECT || v.size()!=5))
	return prodsum(v,false);
    }
    // This will not work if v[0] has auto-quoting functions inside
    // because arguments are not evaled, hence replacement of v[1] by value
    // is not done inside arguments.
    // Example Ya:=desolve([y'+x*y=0,y(0)=a]); seq(plot(Ya),a,1,3); 
    gen debut=v[2],fin=v[3];
    if (is_greater(abs(fin-debut),LIST_SIZE_LIMIT,contextptr))
      return gendimerr(contextptr);
    vecteur res;
    if (is_strictly_greater(debut,fin,contextptr)){
      if (is_positive(step,contextptr))
	step=-step;
      for (;!ctrl_c && !interrupted && is_greater(debut,fin,contextptr);debut=debut+step){
#ifdef TIMEOUT
	control_c();
#endif
	tmp=quotesubst(v[0],v[1],debut,contextptr);
	tmp=eval(tmp,contextptr);
	tmp=quotesubst(tmp,v[1],debut,contextptr);
#ifdef RTOS_THREADX
	tmp=evalf(tmp,1,contextptr);
#endif
	res.push_back(tmp);
      }
    }
    else {
      if (!is_greater(fin,debut,contextptr))
	return gensizeerr((gettext("Unable to sort boundaries ")+debut.print(contextptr))+(","+fin.print(contextptr)));
      if (is_positive(-step,contextptr))
	step=-step;
      for (;!ctrl_c && !interrupted && is_greater(fin,debut,contextptr);debut=debut+step){
#ifdef TIMEOUT
	control_c();
#endif
	tmp=quotesubst(v[0],v[1],debut,contextptr);
	tmp=eval(tmp,contextptr);
	tmp=quotesubst(tmp,v[1],debut,contextptr);
#ifdef RTOS_THREADX
	tmp=evalf(tmp,1,contextptr);
#endif
	res.push_back(tmp);
      }
    }
    if (type==1)
      return _prod(res,contextptr);
    if (type==2)
      return _plus(res,contextptr);
    return res;// return gen(res,_SEQ__VECT);
  }

#if 0
  static identificateur independant_identificateur(const gen & g){
    string xname(" x"+g.print(context0));
    identificateur x(xname);
    return x;
  }

  // type=0 for seq, 1 for prod, 2 for sum
  static gen seqprod2(const gen & g,int type,GIAC_CONTEXT){
    vecteur v(gen2vecteur(g));
    if (v.size()==1)
      v=gen2vecteur(eval(g,eval_level(contextptr),contextptr));
    if (v.size()<4){
      if (type==0)
	return _dollar(g,contextptr);
      if (type==1)
	return prodsum(v,true);	
      if (type==2)
	return prodsum(v,false);
      return gentoofewargs("");
    }
    // v[1]=eval(v[1]);
    v[2]=eval(v[2],eval_level(contextptr),contextptr);
    v[3]=eval(v[3],eval_level(contextptr),contextptr);
    gen step=1;
    gen tmp;
    if (v.size()==5)
      step=eval(v[4],eval_level(contextptr),contextptr);
    if (is_zero(step))
      return gensizeerr(contextptr);
    if (v[3].type!=_INT_ || v[2].type!=_INT_){
      if (type==1)
	return prodsum(v,true);
      if (type==2)
	return prodsum(v,false);
    }
    gen debut=v[2],fin=v[3];
    vecteur res;
    gen nstep=evalf_double((fin-debut)/step,1,contextptr);
    if (nstep.type!=_DOUBLE_)
      return gensizeerr(gettext("Bad step"));
    res.reserve(int(std::abs(nstep._DOUBLE_val))+1);
    identificateur x=independant_identificateur(v[0]);
    tmp=quotesubst(v[0],v[1],x,contextptr);
    gen tmpev=eval(tmp,eval_level(contextptr),contextptr);
    gen a,b;
    if (is_linear_wrt(tmpev,x,a,b,contextptr)){
      if (is_strictly_greater(debut,fin,contextptr)){
	if (is_positive(step,contextptr)) // correct pos step to -
	  step=-step;
	for (;is_greater(debut,fin,contextptr);debut+=step){
	  res.push_back(a*debut+b);
	}
      }
      else {
	if (is_positive(-step,contextptr)) // correct negative step to +
	  step=-step;
	if (step.type==_INT_){
	  int D=debut.val,F=fin.val,S=step.val;
	  for (;D<=F;D+=S){
	    res.push_back(D*a+b);	    
	  }
	}
	else {
	  for (;is_greater(fin,debut,contextptr);debut+=step){
	    res.push_back(a*debut+b);
	  }
	}
      }
    }
    else {
      int level=eval_level(contextptr);
      context * newcontextptr= (context *) contextptr;
      vecteur localvar(1,x);
      int protect=giac::bind(vecteur(1,debut),localvar,newcontextptr);
      if (is_strictly_greater(debut,fin,newcontextptr)){
	if (is_positive(step,newcontextptr)) // correct pos step to -
	  step=-step;
	if (step.type==_INT_){
	  int D=debut.val,F=fin.val,S=step.val;
	  for (;D>=F;D+=S){
	    res.push_back(tmp.eval(level,newcontextptr));
	    local_sto_int_increment(S,x,newcontextptr);
	  }
	}
	else {
	  for (;is_greater(debut,fin,newcontextptr);debut+=step){
	    local_sto(debut,x,newcontextptr);
	    res.push_back(tmp.eval(level,newcontextptr));
	  }
	}
      }
      else {
	if (is_positive(-step,newcontextptr)) // correct negative step to +
	  step=-step;
	if (step.type==_INT_){
	  int D=debut.val,F=fin.val,S=step.val;
	  for (;D<=F;D+=S){
	    res.push_back(tmp.eval(level,newcontextptr));
	    local_sto_int_increment(S,x,newcontextptr);
	  }
	}
	else {
	  for (;is_greater(fin,debut,newcontextptr);debut+=step){
	    local_sto(debut,x,newcontextptr);
	    res.push_back(tmp.eval(level,newcontextptr));
	  }
	}
      }
      leave(protect,localvar,newcontextptr);
    } // end is_linear
    if (type==1)
      return _prod(res,contextptr);
    if (type==2)
      return _plus(res,contextptr);
    return res;// return gen(res,_SEQ__VECT);
  }
#endif

  bool maple_sum_product_unquote(vecteur & v,GIAC_CONTEXT){
    bool res=false;
    int s=int(v.size());
    if (s<2)
      return false; // setsizeerr(contextptr);
    if (v[0].is_symb_of_sommet(at_quote))
      v[0]=v[0]._SYMBptr->feuille;
    if (v[1].type!=_IDNT){
      if (is_equal(v[1]) && v[1]._SYMBptr->feuille.type==_VECT){
	res=true;
	vecteur tmp =*v[1]._SYMBptr->feuille._VECTptr;
	if (tmp.size()==2){
	  if (tmp[0].is_symb_of_sommet(at_quote))
	    tmp[0]=tmp[0]._SYMBptr->feuille;
	  v[1]=symbolic(at_equal,gen(makevecteur(tmp[0],eval(tmp[1],eval_level(contextptr),contextptr)),_SEQ__VECT));
	}
      }
      else
	v[1]=eval(v[1],eval_level(contextptr),contextptr);
    }
    for (int i=2;i<s;++i)
      v[i]=eval(v[i],eval_level(contextptr),contextptr);
    return res;
  }

  gen _sum(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args.subtype!=_SEQ__VECT)
      return prodsum(args.eval(eval_level(contextptr),contextptr),false);
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return prodsum(args.eval(eval_level(contextptr),contextptr),false);
    vecteur v(*args._VECTptr);
    if (v.size()>1 && v[1].is_symb_of_sommet(at_unquote))
      v[1]=eval(v[1],1,contextptr);
    maple_sum_product_unquote(v,contextptr);
    int s=int(v.size());
    if (is_zero(ratnormal(v[0],contextptr)))
      return 0;
    if (!adjust_int_sum_arg(v,s))
      return gensizeerr(contextptr);
    if (v[1].type==_INT_){
      v[0]=eval(v[0],eval_level(contextptr),contextptr);
      return prodsum(v,false);
    }
    if (s==5)
      return seqprod(gen(v,_SEQ__VECT),2,contextptr);
    if (s==4) {
      if (v[1]==cst_i)
	return gensizeerr(gettext("i=sqrt(-1), please use a valid identifier name"));
      gen af=evalf_double(v[2],1,contextptr),bf=evalf_double(v[3],1,contextptr);
      if (v[1].type==_IDNT && (is_inf(af) || af.type==_DOUBLE_) && (is_inf(bf) || bf.type==_DOUBLE_)){
	vecteur w;
#ifndef NSPIRE
	my_ostream * ptr=logptr(contextptr);
	logptr(0,contextptr);
#endif
#ifdef NO_STDEXCEPT
	gen v0=eval(v[0],1,contextptr);
	if (is_undef(v0)) 
	  v0=v[0];
	if (!has_num_coeff(v0))
	  w=protect_find_singularities(v0,*v[1]._IDNTptr,0,contextptr);
#else
	  gen v0=v[0];
	try {
#ifndef EMCC
	  v0=eval(v[0],1,contextptr);
#endif
	  if (!has_num_coeff(v0))
	    w=protect_find_singularities(v0,*v[1]._IDNTptr,0,contextptr);
	} catch (std::runtime_error & e){
	  v0=v[0];
	}
#endif
#ifndef NSPIRE
	logptr(ptr,contextptr);
#endif
	for (unsigned i=0;i<w.size();++i){
	  if (is_integer(w[i]) && is_greater((v[3]-w[i])*(w[i]-v[2]),0,contextptr)) {
	    gen v0w=limit(v0,*v[1]._IDNTptr,w[i],0,contextptr);// gen v0w=subst(v0,v[1],w[i],false,contextptr);
	    if (is_undef(v0w) || is_inf(v0w))
	      return gensizeerr("Pole at "+w[i].print(contextptr));
	  }
	}
      }
      // test must be done twice for example for sum(sin(k),k,1,0)
      if (is_zero(v[2]-v[3]-1))
	return zero;
      bool numeval=(!is_integer(v[2]) && v[2].type!=_FRAC) || (!is_integer(v[3]) && v[3].type!=_FRAC) || approx_mode(contextptr);
      if (is_integral(v[2])){
	while (is_exactly_zero(subst(v[0],v[1],v[2],false,contextptr))){
	  if (v[2]==v[3])
	    return 0;
	  v[2]+=1;
	}
      }
      else {
	gen tmp;
	if (has_evalf(v[2],tmp,1,contextptr))
	    v[2]=_ceil(v[2],contextptr);
      }
      if (is_integral(v[3])){
	while (is_exactly_zero(subst(v[0],v[1],v[3],false,contextptr))){
	  if (v[2]==v[3])
	    return 0;
	  v[3]-=1;
	}
      }
      else {
	gen tmp;
	if (has_evalf(v[3],tmp,1,contextptr))
	    v[3]=_floor(v[3],contextptr);
      }
      if (is_zero(v[2]-v[3]-1))
	return zero;
      if (is_positive(v[2]-v[3]-1,contextptr))
	return -_sum(gen(makevecteur(v[0],v[1],v[3]+(numeval?gen(1.0):plus_one),v[2]-1),_SEQ__VECT),contextptr);
      if (is_strictly_positive(-v[2],contextptr) && is_positive(-v[3],contextptr)){
	gen tmp=quotesubst(v[0],v[1],-v[1],contextptr);
	return _sum(gen(makevecteur(tmp,v[1],-v[3],(numeval?evalf_double(-v[2],1,contextptr):-v[2])),args.subtype),contextptr);
      }
      if (v[2].type==_INT_ && v[3].type==_INT_ && absint(v[3].val-v[2].val)<max_sum_add(contextptr)){
	gen res=seqprod(v,2,contextptr);
	return (numeval || has_num_coeff(res))?evalf(res,1,contextptr):ratnormal(res,contextptr);
      }
    } // end if s==4
    const_iterateur it=v.begin(),itend=v.end();
    gen f=*it;
    ++it;
    gen x=*it;
    if (x.type==_IDNT){ 
      // quote x for evaluation of f
      if (contextptr && contextptr->quoted_global_vars){
	contextptr->quoted_global_vars->push_back(x);
	f=eval(f,eval_level(contextptr),contextptr);
	contextptr->quoted_global_vars->pop_back();
      }
      else {
	if (it->_IDNTptr->quoted){
	  int savequote=*it->_IDNTptr->quoted;
	  *it->_IDNTptr->quoted=1;
	  f=eval(f,eval_level(contextptr),contextptr);    
	  *it->_IDNTptr->quoted=savequote;
	}
	else
	  f=eval(f,eval_level(contextptr),contextptr);    
      }
    }
    ++it;
    if (it==itend){
      gen rem,res;
      res=sum(f,x,rem,contextptr);
      if (is_zero(rem))
	return res;
      else
	return res+symbolic(at_sum,makesequence(rem,x));
    }
    gen a=*it;
    ++it;
    if (it==itend)
      return prodsum(gen(v).eval(eval_level(contextptr),contextptr),false);
    gen b=*it;
    ++it;
    if ( (x.type!=_IDNT) )
      return prodsum(gen(v).eval(eval_level(contextptr),contextptr),false);
    return sum(f,x,a,b,contextptr);
  }

  static const char _somme_s []="somme";
  static define_unary_function_eval_quoted (__somme,&_sum,_somme_s);
  define_unary_function_ptr5( at_somme ,alias_at_somme,&__somme,_QUOTE_ARGUMENTS,true);

  // innert form
  gen _Sum(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return symbolic(at_sum,args);
  }  
  static const char _Sum_s []="Sum";
  static define_unary_function_eval_quoted (__Sum,&_Sum,_Sum_s);
  define_unary_function_ptr5( at_Sum ,alias_at_Sum,&__Sum,_QUOTE_ARGUMENTS,true);

  gen _wz_certificate(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen F,dF,G,n(n__IDNT_e),k(k__IDNT_e);
    if (args.type==_VECT){
      int s=args._VECTptr->size();
      const vecteur & v=*args._VECTptr;
      if (s==0 || s>4) return gensizeerr(contextptr);
      if (s==1) F=v[0];
      if (s==2) F=v[0]/v[1];
      if (s==3){ F=v[0]; n=v[1]; k=v[2]; }
      if (s==4){ F=v[0]/v[1]; n=v[2]; k=v[3]; }
    }
    else
      F=args;
    dF=simplify(subst(F,n,n+1,false,contextptr)-F,contextptr);
    G=_sum(makesequence(dF,k),contextptr);
    if (lop(G,at_sum).empty()){
      gen R=G/subst(F,k,k-1,false,contextptr);
      R=_eval(simplify(R,contextptr),contextptr);
      return _factor(R,contextptr);
    }
    return 0;
  }  
  static const char _wz_certificate_s []="wz_certificate";
  static define_unary_function_eval_quoted (__wz_certificate,&_wz_certificate,_wz_certificate_s);
  define_unary_function_ptr5( at_wz_certificate ,alias_at_wz_certificate,&__wz_certificate,0,true);

  // sum does also what maple add does
  /*
  gen _add(const gen & args,GIAC_CONTEXT) {
  if ( args.type==_STRNG && args.subtype==-1) return  args;
    int & elevel =eval_level(contextptr);
    int el=elevel;
    elevel=1;
    gen res;
    try {
      res=_sum(args,contextptr);
    }
    catch (std::runtime_error & e){
      elevel=el;
      throw(e);
    }
    elevel=el;
    return res;
  } 
  */ 
  static const char _add_s []="add";
  static define_unary_function_eval_quoted (__add,&_sum //&_add
			    ,_add_s);
  define_unary_function_ptr5( at_add ,alias_at_add,&__add,_QUOTE_ARGUMENTS,true);

  gen bernoulli(const gen & x){
    if (x.type==_VECT && x._VECTptr->size()==2){
      gen a=x._VECTptr->front(),y=x._VECTptr->back();
      if (a.type!=_INT_)
	return gensizeerr(gettext("bernoulli"));
      bool all=a.val<0;
      int n=absint(a.val);
      if (n==0)
	return plus_one;
      gen bi=bernoulli(-n);
      if (bi.type!=_VECT)
	return gensizeerr(gettext("bernoulli"));
      vecteur biv=*bi._VECTptr;
      if (biv.size()<n)
	biv.push_back(0);
      // bernoulli polynomials B_n=n*int(B_n-1)+bi[n]
      vecteur allv;
      vecteur cur(1,1);
      if (all)
	allv.push_back((y.type==_VECT?cur:plus_one));
      for (int i=1;i<=n;++i){
	cur=multvecteur(i,integrate(cur,1));
	cur.insert(cur.begin(),biv[i]);
	if (all){
	  vecteur tmp(cur);
	  reverse(tmp.begin(),tmp.end());
	  if (y.type==_VECT)
	    allv.push_back(tmp);
	  else
	    allv.push_back(symb_horner(tmp,y));
	}
      }
      reverse(cur.begin(),cur.end());
      return all?allv:(y.type==_VECT?cur:symb_horner(cur,y));
    }
    if (x.type!=_INT_)
      return gensizeerr(gettext("bernoulli"));
    bool all=x.val<0;
    int n=absint(x.val);
    if (!n)
      return plus_one;
    if (n==1)
      return all?vecteur(1,minus_one_half):minus_one_half;
    if (n%2){
      if (!all)
	return zero;
      --n;
    }
    gen a(plus_one);
    gen b(rdiv(1-n,plus_two,context0));
    vecteur bi(makevecteur(plus_one,minus_one_half));
    int i=2;
    for (; i< n-1; i+=2){
      // compute bernoulli(i)
      gen A=1;
      gen B=gen(1-i)/2;
      for (int j=2; j<i-1;j+=2){
	A=iquo( A*gen(i+3-j)*gen(i+2-j),(j-1)*j);
	B=B+A* bi[j];
      }
      bi.push_back(-B/gen(i+1));
      bi.push_back(0);
      a=iquo( (a*gen(n+3-i)*gen(n+2-i)),((i-1)*i));
      b=b+a* bi[i];
    }
    if (all){
      bi.push_back(rdiv(-b,n+1,context0));
      return bi;
    }
    return rdiv(-b,n+1,context0);
  }
  gen _bernoulli(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->size()==2 && args._VECTptr->back().type!=_INT_)
      return bernoulli(args);
    return apply(args,bernoulli);
  }
  static const char _bernoulli_s []="bernoulli";
  static define_unary_function_eval (__bernoulli,&_bernoulli,_bernoulli_s);
  define_unary_function_ptr5( at_bernoulli ,alias_at_bernoulli,&__bernoulli,0,true);

  vecteur double2vecteur(const double * y,int dim){
    vecteur ye;
    ye.reserve(dim);
    for (int i=0;i<dim;++i)
      ye.push_back(y[i]);
    return ye;
  }

#ifdef HAVE_LIBGSL
  struct odesolve_param {
    gen odesolve_t;
    vecteur odesolve_f,odesolve_ft,odesolve_y;
    matrice odesolve_fy;
    gsl_odeiv_system odesolve_system;
    const context * contextptr;
  };

  double m_undef = numeric_limits<double>::infinity();

  static int gsl_odesolve_function(double t, const double y[], double dydt[], void * params){
    odesolve_param * par =(odesolve_param *) params;
#ifndef NO_STDEXCEPT
    try{
#endif
      gen res=subst(par->odesolve_f,par->odesolve_t,t,false,par->contextptr);
      vecteur vtmp=double2vecteur(y,par->odesolve_system.dimension);
      res=subst(res,par->odesolve_y,vtmp,false,par->contextptr);
      res=res.evalf(1,par->contextptr);
      // store result
      if ( (res.type!=_VECT) || (res._VECTptr->size()!=par->odesolve_system.dimension)){
#ifdef NO_STDEXCEPT
	return 1; 
#else
	setsizeerr(par->contextptr);
#endif
      }
      const_iterateur it=res._VECTptr->begin(),itend=res._VECTptr->end();
      for (double * dydt_it=dydt;it!=itend;++it,++dydt_it){
	if (it->type==_DOUBLE_)
	  *dydt_it=it->_DOUBLE_val;
	else {
#ifdef NO_STDEXCEPT
	  return 1; 
#else
	  setsizeerr(par->contextptr);
#endif
	}
      }
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & err){
      CERR << err.what() << endl;
      int n=par->odesolve_system.dimension,i=0;
      for (double * dydt_it=dydt;i<n;++i,++dydt_it){
	*dydt_it=m_undef;
      }
    }
#endif
    return 0;
  }


  static int gsl_odesolve_jacobian (double t, const double y[], double * dfdy, double dfdt[], void * params){
    // compute
    odesolve_param * par =(odesolve_param *) params;
#ifndef NO_STDEXCEPT
    try {
#endif
      vecteur yv(double2vecteur(y,par->odesolve_system.dimension));
      gen res=subst(par->odesolve_ft,par->odesolve_t,t,false,par->contextptr);
      res=subst(res,par->odesolve_y,yv,false,par->contextptr).evalf(1,par->contextptr);
      // store result
      if ( (res.type!=_VECT) || (res._VECTptr->size()!=par->odesolve_system.dimension)){
#ifdef NO_STDEXCEPT
	return 1; 
#else
	setsizeerr(par->contextptr);
#endif
      }
      const_iterateur it=res._VECTptr->begin(),itend=res._VECTptr->end();
      for (double * dfdt_it=dfdt;it!=itend;++it,++dfdt_it){
	if (it->type==_DOUBLE_)
	  *dfdt_it=it->_DOUBLE_val;
	else {
#ifdef NO_STDEXCEPT
	  return 1; 
#else
	  setsizeerr(par->contextptr);
#endif
	}
      }
      res=subst(par->odesolve_fy,par->odesolve_t,t,false,par->contextptr);
      res=subst(res,par->odesolve_y,yv,false,par->contextptr).evalf(1,par->contextptr);
      // store result
      if ( (res.type!=_VECT) || (res._VECTptr->size()!=par->odesolve_system.dimension)){
#ifdef NO_STDEXCEPT
	return 1; 
#else
	setsizeerr(par->contextptr);
#endif
      }
      it=res._VECTptr->begin();
      itend=res._VECTptr->end();
      for (double * dfdy_it=dfdy;it!=itend;++it){
	if (it->type!=_VECT){
#ifdef NO_STDEXCEPT
	  return 1; 
#else
	  setsizeerr(par->contextptr);
#endif
      }
	const_iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
	for (;jt!=jtend;++jt,++dfdy_it){
	  if (jt->type==_DOUBLE_)
	    *dfdy_it=it->_DOUBLE_val;
	  else {
#ifdef NO_STDEXCEPT
	    return 1; 
#else
	    setsizeerr(par->contextptr);
#endif
	  }
	}
      }    
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & err){
      CERR << err.what() << endl;
      int n=par->odesolve_system.dimension,i=0;
      for (double * dydt_it=dfdt;i<n;++i,++dydt_it){
	*dydt_it=m_undef;
      }
      i=0;
      for (double * dydt_it=dfdy;i<n;++i,++dydt_it){
	*dydt_it=m_undef;
      }
    }
#endif
    return 0;
  }
#endif // HAVE_LIBGSL

  double rk_error(const vecteur & v,const vecteur & w_final,const vecteur & w_init,GIAC_CONTEXT){
    double err=0,derr;
    unsigned dim=unsigned(v.size());
    for (unsigned i=0;i<dim;++i){
      gen wf=w_final[i],wi=w_init[i];
      double wfa=abs(wf,contextptr)._DOUBLE_val; 
      double wia=abs(wi,contextptr)._DOUBLE_val;
      double sci=1+((wfa<wia)?wia:wfa);
      derr = abs(wf-v[i],contextptr)._DOUBLE_val/sci;
      derr *= derr;
      err += derr;
    }
    err = std::sqrt(err/dim);
    return err;
  }

  // solve dy/dt=f(t,y) with initial value y(t0)=y0 to final value t1
  // returns by default y[t1] or a vector of [t,y[t]]
  // if return_curve is true stop as soon as y is outside ymin,ymax
  // f is eitheir a prog (t,y) -> f(t,y) or a _VECT [f(t,y) t y]
  gen odesolve(const gen & t0orig,const gen & t1orig,const gen & f,const gen & y0orig,double tstep,bool return_curve,double * ymin,double * ymax,int maxstep,GIAC_CONTEXT){
    bool iscomplex=false; 
    // switch to false if GSL is installed or true to force using giac code for real ode
    gen t0_e=evalf_double(t0orig.evalf(1,contextptr),1,contextptr);
    gen t1_e=evalf_double(t1orig.evalf(1,contextptr),1,contextptr);
    // Now accept t0 and t1 complex!
    if ( (t0_e.type!=_DOUBLE_ && t0_e.type!=_CPLX)|| (t1_e.type!=_DOUBLE_ && t1_e.type!=_CPLX))
      return gensizeerr(contextptr);
    gen y0=evalf_double(y0orig.evalf(1,contextptr),1,contextptr);
    if (y0.type!=_VECT)
      y0=vecteur(1,y0);
    vecteur y0v=*y0._VECTptr;
    int dim=int(y0v.size());
    if (tstep==0){
      if (dim==2)
	tstep=(gnuplot_xmax-gnuplot_xmin)/100;
      else {
	if (return_curve && abs(t1_e,contextptr)._DOUBLE_val>1e300)
	  tstep=abs(t0_e,contextptr)._DOUBLE_val/100;
	else
	  tstep=abs(t1_e-t0_e,contextptr)._DOUBLE_val/100;
      }
    }
    if (tstep>abs(t1_e-t0_e,contextptr)._DOUBLE_val)
      tstep=abs(t1_e-t0_e,contextptr)._DOUBLE_val;
#if 1
    double * y =(double *)alloca(dim*sizeof(double));
#else
    double * y=new double[dim];
#endif
    for (int i=0;i<dim;i++){
      if (y0v[i].type!=_DOUBLE_ && y0v[i].type!=_CPLX)
	return gensizeerr(contextptr);
      if (y0v[i].type==_DOUBLE_)
	y[i]=y0v[i]._DOUBLE_val;
      else
	iscomplex=true;
    }
    identificateur t_id(" odesolve_t");
    vecteur yv;
    gen tmp;
    if (f.type==_VECT){
      vecteur tmpv(*f._VECTptr);
      if (tmpv.size()!=3)
	return gensizeerr(contextptr);
      tmp=tmpv[0];
      if (tmpv[1].type!=_IDNT)
	return gensizeerr(contextptr);
      t_id=*tmpv[1]._IDNTptr;
      if (tmpv[2].type!=_VECT){
	yv=vecteur(1,tmpv[2]);
      }
      else
	yv=*tmpv[2]._VECTptr;
      if (signed(yv.size())!=dim)
	return gendimerr(contextptr);
    }
    else {
      for (int i=0;i<dim;++i)
	yv.push_back(identificateur(" y"+print_INT_(i)));
      tmp=f(gen(makevecteur(t_id,yv),_SEQ__VECT),contextptr);
    }
    vecteur resv; // contains the curve
    if (return_curve)
      resv.push_back(makevecteur(t0_e,y0v));
#if 0 //def HAVE_LIBGSL
    if (!iscomplex && t0_e.type==_DOUBLE_ && t1_e.type==_DOUBLE_ && is_zero(im(tmp,contextptr))){
      double t0=t0_e._DOUBLE_val;
      double t1=t1_e._DOUBLE_val;
      bool time_reverse=(t1<t0);
      if (time_reverse){
	t0=-t0;
	t1=-t1;
      }
      double t=t0;
      if (time_reverse)
	tmp=-subst(tmp,t_id,-t_id,false,contextptr);
      vecteur odesolve_f;
      if (tmp.type!=_VECT) 
	odesolve_f=vecteur(1,tmp);
      else
	odesolve_f=*tmp._VECTptr;
      if (signed(odesolve_f.size())!=dim)
	return gendimerr(contextptr);
      // N.B.: GSL implementation uses Dormand-Prince method of orders 8/9
      // which is explicit and does not require Jacobian...
      gen diff1=derive(odesolve_f,yv,contextptr),diff2=derive(odesolve_f,t_id,contextptr);
      if (is_undef(diff1) || diff1.type!=_VECT || is_undef(diff2) || diff2.type!=_VECT)
	return diff1+diff2;
      odesolve_param * par=new odesolve_param;
      par->odesolve_t=t_id;
      par->odesolve_y=yv;
      par->contextptr=contextptr;
      par->odesolve_f=odesolve_f;
      par->odesolve_fy=*diff1._VECTptr;
      par->odesolve_ft=*diff2._VECTptr;
      par->odesolve_system.function=gsl_odesolve_function;
      par->odesolve_system.dimension=dim;
      par->odesolve_system.jacobian=gsl_odesolve_jacobian;
      par->odesolve_system.params=par;
      // GSL call
      const gsl_odeiv_step_type * T = gsl_odeiv_step_rk8pd;    
      gsl_odeiv_step * s   = gsl_odeiv_step_alloc (T, dim);
      gsl_odeiv_control * c = gsl_odeiv_control_y_new (1e-7, 1e-7);
      gsl_odeiv_evolve * e = gsl_odeiv_evolve_alloc (dim);
      double h;
      if (return_curve){
	h=fabs(t);
	if (h<1e-4)
	  h=1e-4;
      }
      else
	h=(t1-t)/1e4;
      double oldt=t0;
      bool do_while=true;
      for (int nstep=0;nstep<maxstep && do_while && t<t1;++nstep) {
	if (h>tstep)
	  h=tstep;
	int status = gsl_odeiv_evolve_apply (e, c, s,
					     &par->odesolve_system,
					     &t, t1, &h,
					     y);
	if (status != GSL_SUCCESS)
	  return gensizeerr(gettext("RK8 evolve not successfull"));
	if (debug_infolevel>5)
	  CERR << nstep << ":" << t << ",y5=" << double2vecteur(y,dim) << endl;
	if (return_curve)  {
	  if ( (t-oldt)> tstep/2 || t==t1){
	    oldt=t;
	    if (time_reverse)
	      resv.push_back(makevecteur(-t,double2vecteur(y,dim)));
	    else
	      resv.push_back(makevecteur(t,double2vecteur(y,dim)));
	  }
	  for (int i=0;i<dim;++i){
	    // CERR << y[i] << endl;
	    if ( ymin && ymax && ( y[i]<ymin[i] || y[i]>ymax[i]) )
	      do_while=false;
	  }
	}
      }
      gsl_odeiv_evolve_free(e);
      gsl_odeiv_control_free(c);
      gsl_odeiv_step_free(s);
      delete par;
      if (return_curve)
	return resv;
      else {
	if (t!=t1)
	  return makevecteur(t,double2vecteur(y,dim));
	return double2vecteur(y,dim);
      }
    }
#endif // HAVE_LIBGSL
    vecteur odesolve_f;
    if (tmp.type!=_VECT) 
      odesolve_f=vecteur(1,tmp);
    else
      odesolve_f=*tmp._VECTptr;
    if (signed(odesolve_f.size())!=dim)
      return gendimerr(contextptr);
    // solve vector ode y'=f(t,y) with respect to time variable t_id in t0..t1
    // f is stored in odesolve_f, symbolic y in yv, initial value in a double array y
    /* Butcher tableau for Dormand/Prince 4/5
       0      |
       1/5    | 1/5
       3/10   | 3/40 	      9/40
       4/5    | 44/45 	      −56/15 	  32/9
       8/9    | 19372/6561  −25360/2187  64448/6561 	−212/729
       1      | 9017/3168    −355/33 	 46732/5247 	49/176 	       −5103/18656
       1      | 35/384 	        0 	  500/1113 	125/192 	−2187/6784 	11/84 
       ===============================================================================	
       RK5    |  35/384 	0 	500/1113 	125/192 	−2187/6784 	11/84 	0
       RK4      5179/57600 	0 	7571/16695 	393/640 	−92097/339200 	187/2100 	1/40
       RK4 is used for computation of the tstep variable 
       RK4 error being estimated by |RK5-RK4|
       Step is determined by the following algorithm 
       (cf. Ernst Hairer http://www.unige.ch/~hairer/poly/chap3.pdf, p.67 in French)
       initialization: use h=tstep
       compute RK5_final and RK4_final, then 
       err=|| RK5-RK4 || = sqrt(1/dim*sum(((RK5[i]-RK4[i])/(1+max(RK5[i]_init,RK5[i]_final)))^2,i=1..dim))
       and hoptimal = 0.9*h*(tolerance/||RK5-RK4||)^(1/5)
       if (err<=hoptimal) then time += h; y_init=RK5_final; h=min(hoptimal,t_final-t_current)
       else h=hoptimal
     */
    gen tolerance=epsilon(contextptr)>1e-12?epsilon(contextptr):1e-12;
    vecteur yt(dim+1),ytvar(yv);
    for (int i=0;i<dim;++i)
      yt[i]=y0v[i];
    gen t_e(t0_e);
    yt[dim]=t_e;
    vecteur yt1(dim+1);
    ytvar.push_back(t_id);
    bool do_while=true;
    double butcher_c[]={0,0.2,0.3,4./5,8./9,1.,1.};
    double butcher_a[]={1./5,
			3./40,9./40,
			44./45,-56./15,32./9,
			19372./6561,-25360./2187,64448./6561,-212./729,
			9017./3168,-355./33,46732./5247,49./176,-5103./18656,
			35./384,0,500./1113,125./192,-2187./6784,11./84};
    // double butcher_b5[]={35./384,0,500./1113,125./192,-2187./6784,11./84,0};
    double butcher_b4[]={5179./57600,0,7571./16695,393./640,-92097./339200,187./2100,1./40};
    vecteur y_final5(yt.begin(),yt.begin()+dim),y_final4(dim);
    vecteur butcher_k(7);
    for (int i=0;i<7;++i)
      butcher_k[i]=vecteur(dim);
    vecteur firsteval=subst(odesolve_f,ytvar,yt,false,contextptr),lasteval;
    gen direction=t1_e-t0_e;
    double temps_total=abs(direction,contextptr)._DOUBLE_val,temps=0;
    direction=direction/temps_total;
    for (int nstep=0;do_while && nstep<maxstep && temps<temps_total;++nstep) {
      gen dt=tstep*direction;
      // compute next step
      vecteur & bk0=*butcher_k[0]._VECTptr;
      bk0=firsteval;
      if (is_undef(bk0))
	return bk0;
      multvecteur(dt,bk0,bk0);
      int butcher_a_shift=0;
      for (int j=1;j<=6;j++){
	// compute butcher_k[j]
	for (int i=0;i<dim;++i){
	  yt1[i]=yt[i];
	}
	if (dim==1){
	  gen & yt10=yt1[0];
	  for (int k=0;k<j;k++){
	    type_operator_plus_times(butcher_a[butcher_a_shift+k],butcher_k[k]._VECTptr->front(),yt10); 
	  }
	}
	else {
	  for (int k=0;k<j;k++){
	    gen bak=butcher_a[butcher_a_shift+k];
	    const vecteur & bkk=(*butcher_k[k]._VECTptr);
	    for (int i=0;i<dim;++i){
	      type_operator_plus_times(bak,bkk[i],yt1[i]); //yt1[i] += bak*bkk[i];
	    }
	  }
	}
	butcher_a_shift += j;
	yt1[dim]=yt[dim];
	type_operator_plus_times(butcher_c[j],dt,yt1[dim]);
	vecteur & bkj = *butcher_k[j]._VECTptr;
	if (j<6)
	  bkj=subst(odesolve_f,ytvar,yt1,false,contextptr);
	else
	  bkj=lasteval=subst(odesolve_f,ytvar,yt1,false,contextptr);
	if (is_undef(bkj))
	  return bkj;
	multvecteur(dt,bkj,bkj);
      }
      for (int i=0;i<dim;++i){
	y_final5[i]=yt1[i];
	y_final4[i]=yt[i];
      }
      for (int j=0;j<7;++j){
	vecteur & bkj=*butcher_k[j]._VECTptr;
	// gen bb5j=butcher_b5[j];
	gen bb4j=butcher_b4[j];
	for (int i=0;i<dim;i++){
	  // y_final5[i] += bb5j*bkj[i];
	  type_operator_plus_times(bb4j,bkj[i],y_final4[i]);//y_final4[i] += bb4j*bkj[i];
	}
      }
      // accept or reject current step and compute dt
      double err=rk_error(y_final4,y_final5,yt,contextptr);
      gen hopt=.9*tstep*pow(tolerance/err,.2,contextptr);
      if (err==0 || is_undef(hopt))
	break;
      if (debug_infolevel>5)
	CERR << nstep << ":" << t_e << ",y5=" << y_final5 << ",y4=" << y_final4 << " " << tstep << " hopt=" << hopt << " err=" << err << endl;
      if (is_strictly_greater(err,tolerance,contextptr)){
	// reject step
	tstep=hopt._DOUBLE_val;
      }
      else { // accept
	swap(firsteval,lasteval);
	for (int i=0;i<dim;++i)
	  yt[i]=y_final5[i];
	t_e += dt;
	yt[dim]=t_e;
	temps += tstep;
	tstep=abs(t1_e-t_e,contextptr)._DOUBLE_val;
	if (hopt._DOUBLE_val<tstep)
	  tstep=hopt._DOUBLE_val;
	if (return_curve)
	  resv.push_back(makevecteur(t_e,y_final5));
	if (!iscomplex){
	  // check boundaries for y_final5
	  for (int i=0;i<dim;++i){
	    // CERR << y[i] << endl;
	    if ( ymin && ymax && ( y_final5[i]._DOUBLE_val< ymin[i] || y_final5[i]._DOUBLE_val>ymax[i]) )
	      do_while=false;
	  }
	}
      }
    } // end integration loop
    if (return_curve)
      return resv;
    else {
      if (t_e!=t1_e)
	return makevecteur(t_e,y_final5);
      return y_final5;
    }    
  }
  // note that params is not used

  // standard format is expression,t=t0..t1,vars,init_values
  // also accepted 
  // t0..t1,function,init_values
  // expression,t,vars,init_values,t=tmin..tmax
  // expression,[t,vars],[t0,init_values],t1
  static gen odesolve(const vecteur & w,GIAC_CONTEXT){
    vecteur v(w);
    int vs=int(v.size());
    if (vs<3)
      return gendimerr(contextptr);
    // convert expression,[t,vars],[t0,init_values],t1
    gen t0t=v[0],t0,t1,f,t,y0;
    if (v[1].type==_VECT && v[2].type==_VECT && v[2]._VECTptr->size()==v[1]._VECTptr->size() && vs>3){
      if (v[1]._VECTptr->size()<2)
	return gendimerr(contextptr);
      t0=v[2]._VECTptr->front();
      t1=v[3];
      gen newv1=symbolic(at_equal,v[1]._VECTptr->front(),symb_interval(v[2]._VECTptr->front(),v[3]));
      gen newv2=vecteur(v[1]._VECTptr->begin()+1,v[1]._VECTptr->end());
      gen newv3=vecteur(v[2]._VECTptr->begin()+1,v[2]._VECTptr->end());
      v[1]=newv1;
      v[2]=newv2;
      v[3]=newv3;
    }
    int maxstep=1000,vstart=0;
    double tstep=0;
    if ( t0t.is_symb_of_sommet(at_interval)){ // functional form
      t0=t0t._SYMBptr->feuille._VECTptr->front(); 
      t1=t0t._SYMBptr->feuille._VECTptr->back(); 
      f=v[1];
      y0=v[2];
      vstart=3;
    }
    else { // expression,t=tmin..tmax,y,y0
      if (vs<4)
	return gentypeerr(contextptr);
      y0=v[3];
      gen t=readvar(v[1]);
      f=makevecteur(v[0],t,v[2]);
      bool tminmax_defined,tstep_defined;
      double tmin(-1e300),tmax(1e300);
      vstart=1;
      read_tmintmaxtstep(v,t,vstart,tmin,tmax,tstep,tminmax_defined,tstep_defined,contextptr);
      if (t0!=t1){
	if (tstep==0)
	  tstep=evalf_double(abs(t1-t0,contextptr),1,contextptr)._DOUBLE_val/30;
      }
      else {
	if (tmin>0 || tmax<0 || tmin>tmax || tstep<=0)
	  *logptr(contextptr) << gettext("Warning time reversal") << endl;
	t0=tmin;
	t1=tmax;
      }
      // if (tminmax_defined && tstep_defined) maxstep=2*int((tmax-tmin)/tstep)+1;
      // commented since the real step is used is smaller than tstep most of the time!
      vstart=3;
    }
    double ym[2]={gnuplot_xmin,gnuplot_ymin},yM[2]={gnuplot_xmin,gnuplot_ymin};
    double *ymin=0,*ymax=0;
    vs=int(v.size());
    bool curve=false;
    for (int i=vstart;i<vs;++i){
      if (readvar(v[i])==x__IDNT_e){
	if (readrange(v[i],gnuplot_xmin,gnuplot_xmax,v[i],ym[0],ym[1],contextptr)){
	  ymin=ym;
	  ymax=yM;
	  v.erase(v.begin()+i);
	  --vs;
	}
      }
      if (readvar(v[i])==y__IDNT_e){
	if (readrange(v[i],gnuplot_xmin,gnuplot_xmax,v[i],yM[0],yM[1],contextptr)){
	  ymin=ym;
	  ymax=yM;
	  v.erase(v.begin()+i);
	  --vs;
	}
      }
      if (v[i]==at_curve)
	curve=true;
    }
    return odesolve(t0,t1,f,y0,tstep,curve,ymin,ymax,maxstep,contextptr);
  }
  // odesolve(t0..t1,f,y0) or odesolve(f(t,y),t,y,t0,y0,t1)
  gen _odesolve(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<3 ) )
      return symbolic(at_odesolve,args);
    vecteur v(*args._VECTptr);
    return odesolve(v,contextptr);
  }
  static const char _odesolve_s []="odesolve";
  static define_unary_function_eval (__odesolve,&_odesolve,_odesolve_s);
  define_unary_function_ptr5( at_odesolve ,alias_at_odesolve,&__odesolve,0,true);

  gen preval(const gen & f,const gen & x,const gen & a,const gen & b,GIAC_CONTEXT){
    if (x.type!=_IDNT)
      return gentypeerr(contextptr);
    gen res;
    if (is_greater(b,a,contextptr))
      res=limit(f,*x._IDNTptr,b,-1,contextptr)-limit(f,*x._IDNTptr,a,1,contextptr);
    else {
      if (is_greater(a,b,contextptr))
	res=limit(f,*x._IDNTptr,b,1,contextptr)-limit(f,*x._IDNTptr,a,-1,contextptr) ;
      else
	res=limit(f,*x._IDNTptr,b,0,contextptr)-limit(f,*x._IDNTptr,a,0,contextptr);
    }
    return res;
  }

  // args=[u'*v,u] or [[F,u'*v],u] -> [F+u*v,-u*v']
  // a third argument would be the integration var
  // if u=cste returns F+integrate(u'*v,x)
  gen _ibpdv(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return symbolic(at_ibpdv,args);
    vecteur & w=*args._VECTptr;
    gen X(vx_var),x(vx_var),a,b;
    bool bound=false;
    if (w.size()>=3)
      x=X=w[2];
    if (is_equal(x))
      x=x._SYMBptr->feuille[0];
    if (w.size()>=5)
      X=symb_equal(x,symb_interval(w[3],w[4]));
    if (is_equal(X) && X._SYMBptr->feuille[1].is_symb_of_sommet(at_interval)){
      a=X._SYMBptr->feuille[1]._SYMBptr->feuille[0];
      b=X._SYMBptr->feuille[1]._SYMBptr->feuille[1];
      bound=true;
    }
    gen u(w[1]),v,uprimev,F;
    if (w.front().type==_VECT){
      vecteur & ww=*w.front()._VECTptr;
      if (ww.size()!=2)
	return gensizeerr(contextptr);
      F=ww.front();
      uprimev=ww.back();
    }
    else 
      uprimev=w.front();
    gen uprime(derive(u,x,contextptr));
    if (is_zero(uprime)){
      gen tmp=integrate_gen(uprimev,x,contextptr);
      if (bound)
	tmp=preval(tmp,x,a,b,contextptr);      
      return tmp+F;
    }
    v=normal(rdiv(uprimev,derive(u,x,contextptr),contextptr),contextptr);
    if (bound)
      F += preval(u*v,x,a,b,contextptr);    
    else
      F += u*v;
    return makevecteur(F,normal(-u*derive(v,x,contextptr),contextptr));
  }
  static const char _ibpdv_s []="ibpdv";
  static define_unary_function_eval (__ibpdv,&_ibpdv,_ibpdv_s);
  define_unary_function_ptr5( at_ibpdv ,alias_at_ibpdv,&__ibpdv,0,true);

  gen fourier_an(const gen & f,const gen & x,const gen & T,const gen & n,const gen & a,GIAC_CONTEXT){
    gen primi,iT=inv(T,contextptr);
    gen omega=2*cst_pi*iT;
    primi=_integrate(gen(makevecteur(f*cos(omega*n*x,contextptr),x,a,ratnormal(a+T,contextptr)),_SEQ__VECT),contextptr);
    gen an=iT*primi;
    if (n!=0) 
      an=2*an;
    return has_num_coeff(an)?an:recursive_normal(an,contextptr);
  }
  gen _fourier_an(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT) return gensizeerr(contextptr);
    vecteur v(*args._VECTptr);
    if (v.size()==4) v.push_back(0);
    if (v.size()!=5) return gensizeerr(contextptr);
    gen f=v[0],x=v[1],T=v[2],n=v[3],a=v[4];
    return fourier_an(f,x,T,n,a,contextptr);
  }
  static const char _fourier_an_s []="fourier_an";
  static define_unary_function_eval (__fourier_an,&_fourier_an,_fourier_an_s);
  define_unary_function_ptr5( at_fourier_an ,alias_at_fourier_an,&__fourier_an,0,true);


  gen fourier_bn(const gen & f,const gen & x,const gen & T,const gen & n,const gen & a,GIAC_CONTEXT){
    gen primi,iT=inv(T,contextptr);
    gen omega=2*cst_pi*iT;
    primi=_integrate(gen(makevecteur(f*sin(omega*n*x,contextptr),x,a,ratnormal(a+T,contextptr)),_SEQ__VECT),contextptr);
    gen an=2*iT*primi;
    return has_num_coeff(an)?an:recursive_normal(an,contextptr);
  }
  gen _fourier_bn(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT) return gensizeerr(contextptr);
    vecteur v(*args._VECTptr);
    if (v.size()==4) v.push_back(0);
    if (v.size()!=5) return gensizeerr(contextptr);
    gen f=v[0],x=v[1],T=v[2],n=v[3],a=v[4];
    return fourier_bn(f,x,T,n,a,contextptr);
  } 
  static const char _fourier_bn_s []="fourier_bn";
  static define_unary_function_eval (__fourier_bn,&_fourier_bn,_fourier_bn_s);
  define_unary_function_ptr5( at_fourier_bn ,alias_at_fourier_bn,&__fourier_bn,0,true);
  
  gen fourier_cn(const gen & f,const gen & x,const gen & T,const gen & n,const gen & a,GIAC_CONTEXT){
    gen primi,iT=inv(T,contextptr);
    gen omega=2*cst_pi*iT;
    primi=_integrate(gen(makevecteur(f*exp(-cst_i*omega*n*x,contextptr),x,a,ratnormal(a+T,contextptr)),_SEQ__VECT),contextptr);
    gen cn=iT*primi;
    return has_num_coeff(cn)?cn:recursive_normal(cn,contextptr);
  }
  gen _fourier_cn(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT) return gensizeerr(contextptr);
    vecteur v(*args._VECTptr);
    if (v.size()==4) v.push_back(0);
    if (v.size()!=5) return gensizeerr(contextptr);
    gen f=v[0],x=v[1],T=v[2],n=v[3],a=v[4];
    return fourier_cn(f,x,T,n,a,contextptr);
  } 

  static const char _fourier_cn_s []="fourier_cn";
  static define_unary_function_eval (__fourier_cn,&_fourier_cn,_fourier_cn_s);
  define_unary_function_ptr5( at_fourier_cn ,alias_at_fourier_cn,&__fourier_cn,0,true);

#ifndef USE_GMP_REPLACEMENTS
  // periodic by Luka Marohnić
  // example f:=periodic(x^2,x,-1,1); plot(f,x=-5..5)
  gen _periodic(const gen & g,GIAC_CONTEXT) {
    if (g.type==_STRNG && g.subtype==-1) return g;
    if (g.type!=_VECT || g.subtype!=_SEQ__VECT)
      return gentypeerr(contextptr);
    vecteur & gv = *g._VECTptr;
    if (gv.size()!=4 && gv.size()!=2)
      return gensizeerr(contextptr);
    gen & e=gv[0],x,a,b;
    if (e.type!=_SYMB)
      return gentypeerr(contextptr);
    vecteur vars(*_lname(e,contextptr)._VECTptr);
    if (vars.empty())
      return e;
    if (gv.size()==2) {
      if (!gv[1].is_symb_of_sommet(at_equal))
	return gentypeerr(contextptr);
      vecteur & fl=*gv[1]._SYMBptr->feuille._VECTptr;
      if ((x=fl[0]).type!=_IDNT || !fl[1].is_symb_of_sommet(at_interval))
	return gentypeerr(contextptr);
      vecteur & ab=*fl[1]._SYMBptr->feuille._VECTptr;
      a=ab[0];
      b=ab[1];
    }
    else {
      x=gv[1];
      if (x.type!=_IDNT)
	return gentypeerr(contextptr);
      if (find(vars.begin(),vars.end(),x)==vars.end())
	return e;
      a=gv[2];
      b=gv[3];
    }
    gen T(b-a);
    if (!is_strictly_positive(T,contextptr))
      return gentypeerr(contextptr);
    gen p(subst(e,x,x-T*_floor((x-a)/T,contextptr),false,contextptr));
    return p;// _unapply(makesequence(p,x),contextptr);
  }
  static const char _periodic_s []="periodic";
  static define_unary_function_eval (__periodic,&_periodic,_periodic_s);
  define_unary_function_ptr5(at_periodic,alias_at_periodic,&__periodic,0,true);  
#endif

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

