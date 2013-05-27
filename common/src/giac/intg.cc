// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c intg.cc -DHAVE_CONFIG_H -DIN_GIAC" -*-
#include "giacPCH.h"
// #define LOGINT

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
    int s=p.size();
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
    gen res=eval(subst(e,x,newx,false,contextptr),1,contextptr);
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
      return symbolic(at_pow,e);
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
    return is_zero(derive(e,x,contextptr)); 
  }

  // return true if e=a*x+b
  bool is_linear_wrt(const gen & e,const gen &x,gen & a,gen & b,GIAC_CONTEXT){
    a=derive(e,x,contextptr);
    if (is_undef(a) || !is_constant_wrt(a,x,contextptr))
      return false;
    if (x*a==e)
      b=0;
    else
      b=ratnormal(e-a*x);
    return true;
  }

  // return true if e=a*x+b
  bool is_quadratic_wrt(const gen & e,const gen &x,gen & a,gen & b,gen & c,GIAC_CONTEXT){
    gen tmp=derive(e,x,contextptr);
    if (is_undef(tmp) || !is_linear_wrt(tmp,x,a,b,contextptr))
      return false;
    a=ratnormal(rdiv(a,plus_two,contextptr));
    c=ratnormal(e-a*x*x-b*x);
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

  void decompose_prod(const vecteur & arg,const gen & x,vecteur & non_constant,gen & prod_constant,GIAC_CONTEXT){
    non_constant.clear();
    prod_constant=plus_one;
    const_iterateur it=arg.begin(),itend=arg.end();
    for (;it!=itend;++it){
      if (is_constant_wrt(*it,x,contextptr))
	prod_constant=prod_constant*(*it);
      else
	non_constant.push_back(*it);
    }
  }

  // applies linearity of f. + & neg are distributed as well as * with respect
  // to terms that are constant w.r.t. x
  // e is assumed to be a scalar
  gen linear_apply(const gen & e,const gen & x,gen & remains, GIAC_CONTEXT, gen (* f)(const gen &,const gen &,gen &,const context *)){
    if (is_constant_wrt(e,x,contextptr) || (e==x) )
      return f(e,x,remains,contextptr);
    // e must be of type _SYMB
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
      decompose_prod(*arg._VECTptr,x,non_constant,prod_constant,contextptr);
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
    else
      return symbolic(at_ln,x);
  }

  // eval N at X=e with e=x*exp(i*dephasage*pi/n)/(X-e)+conj and integrate
  static gen substconj_(const gen & N,const gen & X,const gen & x,const gen & dephasage,bool residue_only,GIAC_CONTEXT){
    gen c=cos(dephasage,contextptr);
    gen s=sin(dephasage,contextptr);
    gen e=x*(c+cst_i*s);
    gen b=subst(N,X,e,false,contextptr),rb,ib;
    reim(b,rb,ib,contextptr);
    gen N2=normal(-2*ib,contextptr); // same
    if (residue_only)
      return N2*sign(s*x,contextptr);
    gen res=normal(rb,contextptr)*symbolic(at_ln,pow(X,2)+ratnormal(-2*c*x)*X+x.squarenorm(contextptr)); 
    gen atanterm=symbolic(at_atan,(X-c*x)/(s*x));
    if (X.is_symb_of_sommet(at_tan))
      atanterm += cst_pi*sign(s*x,contextptr)*symbolic(at_floor,X._SYMBptr->feuille/cst_pi+plus_one_half);
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
    if (is_zero(c))
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
    if (is_zero(b))
      return undef;
    c=_floor(b,contextptr);
    if (c.type==_FLOAT_)
      c=get_int(c._FLOAT_val);
    if (!has_evalf(a,aa,1,contextptr)){
      if (c.type==_INT_ && c==b && c.val %2 ==0)
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
  static bool integrate_deno_length_2(const polynome & num,const vecteur & v,const vecteur & l,const vecteur & lprime,gen & res,bool residue_only,GIAC_CONTEXT){
    if (v.size()<2)
      return false;
    const_iterateur it=v.begin()+1,itend=v.end()-1;
    for (;it!=itend;++it){
      if (!is_zero(*it))
	break;
    }
    int n=v.size()-1,d=it-v.begin(),deg;
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
	if (is_zero(delta))
	  return false;
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
    }
    gen c=normal(-b/a,contextptr);
    if (d!=n && n%d==0){ 
      // rescale and check cyclotomic
      gen tw=pow(*it/v.front(),-n/d)*v.back();
      if (tw.type!=_INT_)
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
    }
    else {
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
    int n=v.size();
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
    int ns=n.size(); int nd=d.size();
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

  static gen integrate_rational(const gen & e, const gen & x, gen & remains_to_integrate,gen & xvar,GIAC_CONTEXT);

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
    int n=Q.size()-1;
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

  static gen integrate_linearizable(const gen & e,const gen & gen_x,gen & remains_to_integrate,GIAC_CONTEXT){
    // exp linearization
    vecteur vexp;
    gen res;
    const identificateur & id_x=*gen_x._IDNTptr;
    lin(e,vexp,contextptr); // vexp = coeff, arg of exponential
    const_iterateur it=vexp.begin(),itend=vexp.end();
    for (;it!=itend;){
      // trig linearization
      vecteur vtrig;
      gen coeff=*it;
      ++it; // it -> on the arg of the exp that must be linear
      gen rex2,rea,reb,reaxb=*it;
      ++it;
      if (!is_quadratic_wrt(reaxb,gen_x,rex2,rea,reb,contextptr)){
	remains_to_integrate = remains_to_integrate + coeff*exp(reaxb,contextptr);
	continue;
      }
      if (!is_zero(rex2)){
	if (is_zero(im(rex2,contextptr)) && is_positive(-rex2,contextptr)){
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
	  int les_vars=les_var.size();
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
	  int vs=in_coeffnumv.size()-1;
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
	  lcmdeno(vres,vresden,contextptr);
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
	// check polynomial
	const vecteur vx2=lvarxpow(coeff,gen_x);
	if ( (vx2.size()>1) || ( (!vx2.empty()) && (vx2.front()!=gen_x))  || ( (imaxb.type==_SYMB) && !is_linear_wrt(imaxb._SYMBptr->feuille,gen_x,ima,imb,contextptr) ) ){
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
	    res= res + exp(reb,contextptr)*integrate_rational(coeff,gen_x,tmprem,xvar,contextptr);
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
	int les_vars=les_var.size();
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
    if (is_zero(im(e,contextptr)) &&has_i(res)){
      remains_to_integrate=re(remains_to_integrate,contextptr);
      res=ratnormal(re(res,contextptr));
    }
    return res;
  } // end linearizable

  static bool integrate_sqrt(gen & e,const gen & gen_x,const vecteur & rvar,gen & res,gen & remains_to_integrate,GIAC_CONTEXT){ // x and a power
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
	tmpres=linear_integrate(tmpe,gen_x,tmprem,contextptr);
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
	tmpres=linear_integrate(tmpe,gen_x,tmprem,contextptr);
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
	alpha=integrate_rational(alpha/den,gen_x,remains_to_integrate,xvar,contextptr);
	if (is_undef(alpha)){
	  res=alpha;
	  return true;
	}
	/* Instead we should factor argument in den 
	   FIXME in usual.cc diff of ln should expand * and / and rm abs
	   write y=argument, P=beta
	   we want to integrate P*sqrt(y)/den=(P*y)/den* y^(-1/2)
	   let den=y^l*D where D is prime with y
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
	gen Py=(P*Q+C[0]*yl)/g;
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
	  tmpv=vecteur(1,tmpv); // change 3/1/2013 for int(sqrt(1+x^2)/(-2*x^2))
	  // res= gensizeerr(contextptr);
	  // return true;
	}
	vecteur colP=*tmpv._VECTptr;
	int n=colP.size()-1;
	tmpv=_e2r(makesequence(y,gen_x),contextptr);
	if (tmpv.type!=_VECT){
	  res= gensizeerr(contextptr);
	  return true;
	}
	int k=tmpv._VECTptr->size()-1;
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
	  col0=linsolve(sys,colP,contextptr);
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
	    tmpe=ratnormal(complex_subst(tmpe,gen_x,rdiv(m*m-c,b-plus_two*sqrta*m,contextptr),contextptr));
	    tmpres=linear_integrate(tmpe,m,tmprem,contextptr);
	    remains_to_integrate=remains_to_integrate+complex_subst(plus_two*tmprem,m,sqrt(argument,contextptr)-sqrta*gen_x,contextptr);
	    res= alpha+complex_subst(plus_two*tmpres,m,sqrt(argument,contextptr)-sqrta*gen_x,contextptr);
	    return true;
	  }
	  else {
	    gen D=sqrt_noabs(b*b-gen(4)*a*c,contextptr);
	    /*
	      ( *	D=sqrt(b^2-4ac)                                    * )
	      ( * 	a<0 and D>0 ->	x=[D*2u/[1+u^2]-b]/2a		   * )
	      ( *			u=-[D-2*sqrt[-a]*sqrt[y]]/[2ax+b]   * )
	      ( *			dx/sqrt[y]=2*du/[sqrt[-a]*[1+u^2]] * )
	    */
	    gen sqrta(sqrt(-a,contextptr));
	    identificateur id_u(" u");
	    gen u(id_u),uu(u);
	    gen uasx=rdiv(plus_two*sqrta*sqrt(argument,contextptr)-D,plus_two*a*gen_x+b,contextptr);
	    gen sqrty=rdiv(D-(plus_two*a*gen_x+b)*u,plus_two*sqrta,contextptr);
	    tmpe=eval(rdiv(complex_subst(e*sqrt(argument,contextptr),argument,pow(sqrty,2),contextptr),1+u*u,contextptr),1,contextptr);
	    tmpe=complex_subst(tmpe,gen_x,rdiv(rdiv(plus_two*u*D,1+u*u,contextptr)-b,plus_two*a,contextptr),contextptr);
	    tmpres=integrate_rational(tmpe,u,tmprem,uu,contextptr);
	    // sqrt(a*x^2+b*x+c) -> a*[(x+b/2/a)^2-(D/a)^2]
	    // -> asin(a*x+b/2)
	    vecteur vin(makevecteur(u,symbolic(at_atan,u))),vout(makevecteur(uasx,inv(2,contextptr)*asin(ratnormal((-2*a*gen_x-b)/abs(D,contextptr)),contextptr)));
	    remains_to_integrate=remains_to_integrate+complex_subst(rdiv(plus_two,sqrta,contextptr)*tmprem,vin,vout,contextptr);
	    res=alpha+complex_subst(rdiv(plus_two,sqrta,contextptr)*tmpres,vin,vout,contextptr);
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

  static gen integrate_piecewise(gen& e,const gen & piece,const gen & gen_x,gen & remains_to_integrate,GIAC_CONTEXT){
    gen & piecef=piece._SYMBptr->feuille;
    if (piecef.type!=_VECT){
      e=subst(e,piece,piecef,false,contextptr);
      return integrate_id_rem(e,gen_x,remains_to_integrate,contextptr);
    }
    vecteur piecev=*piecef._VECTptr,remainsv(piecev);
    int nargs=piecev.size();
    bool addremains=false;
    for (int i=0;i<nargs/2;++i){
      remainsv[2*i+1]=0;
      piecev[2*i+1]=integrate_id_rem(piecev[2*i+1],gen_x,remainsv[2*i+1],contextptr);
      addremains = addremains || !is_zero(remainsv[2*i+1]);
    }
    if (nargs%2){
      remainsv[nargs-1]=0;
      piecev[nargs-1]=integrate_id_rem(piecev[nargs-1],gen_x,remainsv[nargs-1],contextptr);
      addremains = addremains || !is_zero(remainsv[nargs-1]);
	}
    if (addremains)
      remains_to_integrate=symbolic(at_piecewise,gen(remainsv,_SEQ__VECT));
    return symbolic(at_piecewise,gen(piecev,_SEQ__VECT));
    // FIXME: make the antiderivative continuous
  }

  static gen integrate_trig_fraction(gen & e,const gen & gen_x,vecteur & var,const gen & coeff_trig,int trig_fraction,gen& remains_to_integrate,GIAC_CONTEXT){
    const_iterateur vart=var.begin(),vartend=var.end();
    vecteur substout;
    gen a,b,coeff_cst;
    is_linear_wrt(vart->_SYMBptr->feuille,gen_x,a,b,contextptr);
    coeff_cst=ratnormal(rdiv(a,coeff_trig,contextptr))*b;
    // express all angles in vart as n*(coeff_trig*x+coeff_cst)+angle=a*x+b, 
    // t=coeff_trig*x+coeff_cst
    for (;vart!=vartend;++vart){
      is_linear_wrt(vart->_SYMBptr->feuille,gen_x,a,b,contextptr);
      gen n=ratnormal(rdiv(a,coeff_trig,contextptr));
      if (n.type!=_INT_) return gensizeerr(gettext("trig_fraction"));
      gen angle=ratnormal(b-n*coeff_cst);
      substout.push_back(symbolic(vart->_SYMBptr->sommet,n*gen_x+angle));
    }
    gen f=complex_subst(e,var,substout,contextptr); // should be divided by coeff_trig
    f=_texpand(f,contextptr);
    gen tmprem,tmpres;
    if (trig_fraction==4){ // everything depends on exp(x)
      f=complex_subst(f,exp(gen_x,contextptr),gen_x,contextptr)*inv(gen_x,contextptr);
      tmpres=linear_integrate(f,gen_x,tmprem,contextptr);
      gen expx=exp(coeff_trig*gen_x+coeff_cst,contextptr);
      remains_to_integrate = inv(coeff_trig,contextptr)*complex_subst(tmprem,gen_x,expx,contextptr);
      return inv(coeff_trig,contextptr)*complex_subst(tmpres,gen_x,expx,contextptr);
    }
    f=halftan(f,contextptr); // now everything depends on tan(x/2)
    // t=tan(x/2), dt=1/2(1+t^2)*dx
    gen xsur2=rdiv(coeff_trig*gen_x+coeff_cst,plus_two,contextptr);
    gen tanxsur2=tan(xsur2,contextptr);
    f=complex_subst(f,tan(rdiv(gen_x,plus_two,contextptr),contextptr),gen_x,contextptr)*inv(plus_one+pow(gen_x,2),contextptr);
    vecteur vf(1,gen_x);
    rlvarx(f,gen_x,vf);
    if (vf.size()<=1)
      tmpres=integrate_rational(f,gen_x,tmprem,tanxsur2,contextptr);
    else {
      tmpres=linear_integrate(f,gen_x,tmprem,contextptr);
      tmpres=complex_subst(tmpres,gen_x,tanxsur2,contextptr);
      tmprem=complex_subst(tmprem,gen_x,tanxsur2,contextptr);
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
    int s=l.front()._VECTptr->size();
    if (!s){
      l.erase(l.begin());
      s=l.front()._VECTptr->size();
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
    if (integrate_deno_length_2(num,vtmp,l,lprime,value,true,contextptr)){
      value=ratnormal(value)*cst_pi;
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
	  if (!integrate_deno_length_2(c,vtmp,l,lprime,value,true,contextptr))
	    return false;
	}
	break ;
      }
    }
    value=ratnormal(value)*cst_pi;
    return true;
  }

  static gen integrate_rational_end(vector< pf<gen> >::iterator & it,vector< pf<gen> >::const_iterator & itend,const gen & x,const gen & xvar,const vecteur & l,const vecteur & lprime,const polynome & ipnum,const polynome & ipden,const gen & ratpart,gen & remains_to_integrate,GIAC_CONTEXT){
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
	  sqrtdelta=normalize_sqrt(sqrt(-Delta,contextptr),contextptr);
	  atannum=atannum*gen(2);
	  simplify(atannum,alpha);
	  gen tmpatan=ratnormal(rdiv(deuxaxplusb,sqrtdelta,contextptr));
	  gen residue;
	  if (tmpatan.is_symb_of_sommet(at_tan))
	    tmpatan=tmpatan._SYMBptr->feuille;
	  else {
	    tmpatan=atan(tmpatan,contextptr);
	    if (xvar.is_symb_of_sommet(at_tan)){
	      // add residue
	      residue=r2e(it->fact.derivative().derivative(),l,contextptr);
	      residue=cst_pi*sign(residue,contextptr)*_floor((xvar._SYMBptr->feuille/cst_pi+plus_one_half),contextptr);
	    }
	    else {
	      // if xvar has a singularity at 0 e.g. xvar =x+1/x or x-1/x, 
	      // add the residue at 0
	      if (xvar.type!=_IDNT){
		residue=ratnormal(limit(tmpatan,*x._IDNTptr,0,-1,contextptr)-limit(tmpatan,*x._IDNTptr,0,1,contextptr));
		residue=residue*sign(x,contextptr)/2;
	      }
	    }
	  }
	  if (!angle_radian(contextptr))
	    tmpatan=tmpatan*deg2rad_e;
	  tmpatan += residue;
	  lnpart=lnpart+rdiv(r2e(atannum,lprime,contextptr),(r2e(alpha,lprime,contextptr))*sqrtdelta,contextptr)*tmpatan;
	} // end else uselof
	break; 
      default: // divide a*it->num =b*it->den.derivative()+c 
	it->num.TPseudoDivRem(it->den.derivative(),b,c,a);
	// remaining pf
	if (!c.coord.empty()){
	  vecteur vtmp=polynome2poly1(a*it->den,1);
	  if (!integrate_deno_length_2(c,vtmp,l,lprime,lnpart,false,contextptr))
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
  static gen integrate_rational(const gen & e, const gen & x, gen & remains_to_integrate,gen & xvar,GIAC_CONTEXT){
    if (x.type!=_IDNT) return gensizeerr(contextptr); // see limit
    const vecteur & varx=lvarx(e,x);
    int varxs=varx.size();
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
    int s=l.front()._VECTptr->size();
    if (!s){
      l.erase(l.begin());
      s=l.front()._VECTptr->size();
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
      if (!integrate_deno_length_2(num,vtmp,l,lprime,r,false,contextptr))
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
	xvar=symbolic(at_cos,ratnormal(2*xvar._SYMBptr->feuille));
	xvar=(1-xvar)/(1+xvar);
	aa/=2;
      }
      xvar=pow(xvar,aa);
      return integrate_rational(r2e(fraction(num,den),l,contextptr),x,remains_to_integrate,xvar,contextptr);
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
	  xvar=symbolic(at_sin,ratnormal(2*xvar._SYMBptr->feuille));
	}
	else {
	  xvar=xvar+inv(xvar,contextptr);
	}
	num=poly12polynome(N,1,num.dim);
	den=poly12polynome(D,1,den.dim);
	return integrate_rational(r2e(fraction(num,den),l,contextptr),x,remains_to_integrate,xvar,contextptr);
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
	  xvar=symbolic(at_tan,ratnormal(2*xvar._SYMBptr->feuille));
	}
	else
	  xvar=xvar-inv(xvar,contextptr);
	num=poly12polynome(N,1);
	den=poly12polynome(D,1);
	simplify(num,den);
	return integrate_rational(r2e(fraction(num,den),l,contextptr),x,remains_to_integrate,xvar,contextptr);
      }
    }
    l.front()._VECTptr->front()=xvar;
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
      // factor(single.den,p_content,vden,false,withsqrt(contextptr),complex_mode(contextptr));
      gen extra_div=1;
      factor(single.den,p_content,vden,false,false,false,1,extra_div);
      partfrac(single.num,single.den,vden,finaldecomp,temp,tmp);
    }
    it=finaldecomp.begin();
    itend=finaldecomp.end();
    return integrate_rational_end(it,itend,x,xvar,l,lprime,ipnum,ipden,r2sym(intdecomp,l,contextptr),remains_to_integrate,contextptr);
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
    else
      return -cos(x,contextptr)*gen(180)/cst_pi;
  }

  static gen int_cos(const gen & x,GIAC_CONTEXT){
    if (angle_radian(contextptr))
      return sin(x,contextptr);
    else
      return sin(x,contextptr)*gen(180)/cst_pi;
  }

  static gen int_tan(const gen & x,GIAC_CONTEXT){
    gen g=-lnabs(cos(x,contextptr),contextptr);
    if (angle_radian(contextptr))
      return g;
    else
      return g*gen(180)/cst_pi;
  }

  static gen int_tanh(const gen & x,GIAC_CONTEXT){
    return -ln(cosh(x,contextptr),contextptr);
  }

  static gen int_asin(const gen & x,GIAC_CONTEXT){
    if (angle_radian(contextptr))
      return x*asin(x,contextptr)+sqrt(1-pow(x,2),contextptr);
    else
      return x*asin(x,contextptr)*deg2rad_e+sqrt(1-pow(x,2),contextptr);
  }

  static gen int_acos(const gen & x,GIAC_CONTEXT){
    if (angle_radian(contextptr))
      return x*acos(x,contextptr)-sqrt(1-pow(x,2),contextptr);
    else
      return x*acos(x,contextptr)*deg2rad_e-sqrt(1-pow(x,2),contextptr);
  }

  static gen int_atan(const gen & x,GIAC_CONTEXT){
    if (angle_radian(contextptr)) 
      return x*atan(x,contextptr)-rdiv(ln(pow(x,2)+1,contextptr),plus_two,contextptr);
    else
      return x*atan(x,contextptr)*deg2rad_e-rdiv(ln(pow(x,2)+1,contextptr),plus_two,contextptr);
  }

  static const gen_op_context primitive_tab_primitive[]={giac::int_sin,giac::int_cos,giac::int_tan,giac::int_exp,giac::int_sinh,giac::int_cosh,giac::int_tanh,giac::int_asin,giac::int_acos,giac::int_atan,giac::xln_x};

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
      fx=res;
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
	decompose_prod(*u._SYMBptr->feuille._VECTptr,gen_x,non_constant,alpha,contextptr);
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
	    return false;
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
      e=integrate_id_rem(e,x,remains_to_integrate,contextptr);
      remains_to_integrate=quotesubst(remains_to_integrate,x,x_orig,contextptr);
      return quotesubst(e,x,x_orig,contextptr);
    }
    return integrate_id_rem(e_orig,x_orig,remains_to_integrate,contextptr);
  }

  static bool integrate_step0(gen & e,const gen & gen_x,vecteur & l1,vecteur & m1,gen & res,gen & remains_to_integrate,GIAC_CONTEXT){
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
    res=integrate_id_rem(e,gen_x,remains_to_integrate,contextptr);
    gen resadd;
    if (is_undef(res)) return true;
    // check what happens when si==0
    for (int j=0;j<i;++j){
      gen val=l4[j],a,b,r;
      if (val.is_symb_of_sommet(at_sign)){
	if (is_linear_wrt(val._SYMBptr->feuille,gen_x,a,b,contextptr)){
	  r=-b/a;
	  vecteur l5(l4);
	  l5[j]=1;
	  gen limsup=subst(res,l3,l5,false,contextptr);
	  l5[j]=-1;
	  gen liminf=subst(res,l3,l5,false,contextptr);
	  gen tmp=ratnormal((limit(liminf,id_x,r,-1,contextptr)-limit(limsup,id_x,r,1,contextptr))/2)*val;
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

  static bool detect_inv_trigln(gen & e,vecteur & rvar,const gen & gen_x,gen & res,gen & remains_to_integrate,GIAC_CONTEXT){
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
	// make the change of var ln[ax+b]=t -> x=rdiv(exp(t)-b,a)
	gen tmprem,tmpres,tmpe,xt,dxt;
	xt=rdiv(symbolic(inverse_sommet,gen_x)-b,a,contextptr);
	dxt=derive(xt,gen_x,contextptr);
	if (is_undef(dxt)){
	  res=dxt;
	  return true;
	}
	vecteur substin(makevecteur(gen_x,*rvt));
	vecteur substout(makevecteur(xt,gen_x));
	tmpe=complex_subst(e,substin,substout,contextptr)*dxt;
	tmpres=linear_integrate(tmpe,gen_x,tmprem,contextptr);
	remains_to_integrate=complex_subst(rdiv(tmprem,dxt,contextptr),gen_x,*rvt,contextptr);
	tmpres=_texpand(tmpres,contextptr);
	res=complex_subst(tmpres,substout,substin,contextptr);
	return true;
      }
    }
    return false;
  }

  gen integrate_id_rem(const gen & e_orig,const gen & gen_x,gen & remains_to_integrate,GIAC_CONTEXT){
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate id_rem ") << e_orig << endl;
#endif
    remains_to_integrate=0;
    gen e(e_orig);
    // Step -3: replace when by piecewise
    e=when2piecewise(e,contextptr);
    e=Heavisidetosign(e,contextptr);
    if (is_constant_wrt(e,gen_x,contextptr))
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
	return integrate_piecewise(e,piece,gen_x,remains_to_integrate,contextptr);
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
      if (integrate_step0(e,gen_x,l1,m1,res,remains_to_integrate,contextptr))
	return res;
    }
    // Step1: detection of some unary_op[linear fcn]
    unary_function_ptr u=e._SYMBptr->sommet;
    gen f=e._SYMBptr->feuille,a,b;
    // particular case for ^, _FUNCnd arg must be constant
    if ( (u==at_pow) && is_constant_wrt(f._VECTptr->back(),gen_x,contextptr) && is_linear_wrt(f._VECTptr->front(),gen_x,a,b,contextptr) ){
      b=f._VECTptr->back();
      if (is_minus_one(b))
	return rdiv(lnabs(f._VECTptr->front(),contextptr),a,contextptr);
      return rdiv(pow(f._VECTptr->front(),b+plus_one,contextptr),a*(b+plus_one),contextptr);
    }
    if ( (u==at_surd) && is_constant_wrt(f._VECTptr->back(),gen_x,contextptr) && is_linear_wrt(f._VECTptr->front(),gen_x,a,b,contextptr) ){
      b=f._VECTptr->back();
      if (is_minus_one(b))
	return rdiv(lnabs(f._VECTptr->front(),contextptr),a,contextptr);
      return gen_x*symbolic(at_surd,f)/(a+a/b);
    }
    if ( (u==at_NTHROOT) && is_constant_wrt(f._VECTptr->front(),gen_x,contextptr) && is_linear_wrt(f._VECTptr->back(),gen_x,a,b,contextptr) ){
      b=f._VECTptr->front();
      if (is_minus_one(b))
	return rdiv(lnabs(f._VECTptr->back(),contextptr),a,contextptr);
      return gen_x*symbolic(at_NTHROOT,f)/(a+a/b);
    }
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate step 1 ") << e << endl;
#endif
    // unary op only
    int s=equalposcomp(primitive_tab_op,u);
    if (s && is_linear_wrt(f,gen_x,a,b,contextptr) )
      return rdiv(primitive_tab_primitive[s-1](f,contextptr),a,contextptr);

    // Step2: detection of f(u)*u' 
    vecteur v(1,gen_x);
    gen fu,fx;
    rlvarx(e,gen_x,v);
    vecteur rvar=v;
    int rvarsize=v.size();
    if (rvarsize<TRY_FU_UPRIME){ // otherwise no hope
      const_iterateur it=v.begin(),itend=v.end();
      ++it; // don't try x!
      for (;it!=itend;++it){
	gen df=derive(*it,gen_x,contextptr);
	gen tmprem;
	fu=ratnormal(rdiv(e,df,contextptr));
	if (is_undef(fu) && is_zero(ratnormal(df))){
	  // *it is constant -> find the value
	  tmprem=subst(*it,gen_x,zero,false,contextptr);
	  e=subst(e,*it,tmprem,false,contextptr);
	  return integrate_id_rem(e,gen_x,remains_to_integrate,contextptr);
	}
	if (is_undef(fu) || is_inf(fu))
	  continue;
	if (is_rewritable_as_f_of(fu,*it,fx,gen_x,contextptr)){
	  e=linear_integrate(fx,gen_x,tmprem,contextptr);
	  remains_to_integrate=remains_to_integrate+complex_subst(tmprem,gen_x,*it,contextptr)*df;
	  return complex_subst(e,gen_x,*it,contextptr);
	}
	if (it->type!=_SYMB)
	  continue;
	f=it->_SYMBptr->feuille;
	if ( (f.type==_VECT) && (!f._VECTptr->empty()) )
	  f=f._VECTptr->front();
	if (f.type!=_SYMB)
	  continue;
	if (is_linear_wrt(f,gen_x,a,b,contextptr))
	  continue;
	df=derive(f,gen_x,contextptr);
	fu=ratnormal(rdiv(e,df,contextptr));
	if (is_rewritable_as_f_of(fu,f,fx,gen_x,contextptr)){
	  e=linear_integrate(fx,gen_x,tmprem,contextptr);
	  remains_to_integrate=remains_to_integrate+complex_subst(tmprem,gen_x,f,contextptr)*df;
	  return complex_subst(e,gen_x,f,contextptr);
	}	  
      }
    }
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate step 2 ") << e << endl;
#endif
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
      gen tmprem,u=linear_integrate(*vt,gen_x,tmprem,contextptr);
      if (is_undef(u) || !is_zero(tmprem))
	continue;
      if (!est_puissance){
	vecteur vv(v);
	vv.erase(vv.begin()+i,vv.begin()+i+1);
	fu=symbolic(at_prod,gen(vv,_SEQ__VECT));
      }
      if (is_rewritable_as_f_of(fu,u,fx,gen_x,contextptr)){
	e=linear_integrate(fx,gen_x,tmprem,contextptr);
	remains_to_integrate=remains_to_integrate+complex_subst(tmprem,gen_x,u,contextptr)*derive(u,gen_x,contextptr);
	return complex_subst(e,gen_x,u,contextptr);
      }
    }
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate step 3 ") << e << endl;
#endif
    // Step3: rational fraction?
    if (rvarsize==1){
      gen xvar(gen_x);
      return integrate_rational(e,gen_x,remains_to_integrate,xvar,contextptr);
    }

    // square roots
    if ( (rvarsize==2) && (rvar.back().type==_SYMB) && (rvar.back()._SYMBptr->sommet==at_pow) ){
      if (integrate_sqrt(e,gen_x,rvar,res,remains_to_integrate,contextptr))
	return res;
    }
    // detection of inv of trig or ln of a linear expression
    if (detect_inv_trigln(e,rvar,gen_x,res,remains_to_integrate,contextptr))
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
	if (lvarx(ibpe,gen_x)==vecteur(1,gen_x)){
	  gen tmpres,tmprem,tmpprimitive,tmp,xvar(gen_x);
	  tmpprimitive=integrate_rational(ibpe,gen_x,tmp,xvar,contextptr);
	  if (is_zero(tmp) && lvarx(tmpprimitive,gen_x)==vecteur(1,gen_x)){
	    tmpres=tmpprimitive*derive(*ibp,gen_x,contextptr);
	    tmpres=recursive_normal(tmpres,true,contextptr);
	    tmpres=linear_integrate(tmpres,gen_x,tmprem,contextptr);
	    remains_to_integrate=-tmprem;
	    return tmpprimitive*(*ibp)-tmpres;
	  }
	}
      }
    }
    else { // check for u'=1
      int test;
      if ( (e._SYMBptr->sommet==at_pow) && (e._SYMBptr->feuille._VECTptr->front().type==_SYMB) && (e._SYMBptr->feuille._VECTptr->back().type==_INT_) && (e._SYMBptr->feuille._VECTptr->back().val>0) )
	test=equalposcomp(inverse_tab_op,e._SYMBptr->feuille._VECTptr->front()._SYMBptr->sommet);
      else
	test=equalposcomp(inverse_tab_op,e._SYMBptr->sommet);
      if (test){
	gen tmpres,tmprem;
	tmpres=linear_integrate(gen_x*derive(e,gen_x,contextptr),gen_x,tmprem,contextptr);
	remains_to_integrate=-tmprem;
	return gen_x*e-tmpres;
      }
    }

    // rewrite inv(exp)
    vector<const unary_function_ptr *> vsubstin(1,at_inv);
    vector<gen_op_context> vsubstout(1,invexptoexpneg);
    e=subst(e,vsubstin,vsubstout,false,contextptr);
    // detection of denominator=independent of x
    v=lvarxwithinv(e,gen_x,contextptr);
    // search for nop (for nop[inv])
    if (!has_nop_var(v))
      return integrate_linearizable(e,gen_x,remains_to_integrate,contextptr);

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
	  gen quotient=ratnormal(rdiv(a,coeff_trig,contextptr));
	  if (quotient.type==_INT_)
	    continue;
	  if ( (quotient.type==_FRAC) && (quotient._FRACptr->num.type==_INT_) && (quotient._FRACptr->den.type==_INT_) ){
	    coeff_trig=ratnormal(rdiv(coeff_trig,quotient._FRACptr->den,contextptr));
	    continue;
	  }
	  if ( (quotient.type==_SYMB) && (quotient._SYMBptr->sommet==at_inv) && (quotient._SYMBptr->feuille.type==_INT_)){
	    coeff_trig=ratnormal(rdiv(coeff_trig,quotient._SYMBptr->feuille,contextptr));
	    continue;
	  }
	  trig_fraction=false;
	}
      } // end if (trig_fraction)
    }
    if (trig_fraction)
      return integrate_trig_fraction(e,gen_x,var,coeff_trig,trig_fraction,remains_to_integrate,contextptr);
    // finish by calling the Risch algorithm
    return risch(e,*gen_x._IDNTptr,remains_to_integrate,contextptr);
  }

  gen linear_integrate(const gen & e,const gen & x,gen & remains_to_integrate,GIAC_CONTEXT){
    gen ee(normalize_sqrt(e,contextptr));
    return linear_apply(ee,x,remains_to_integrate,contextptr,integrate_gen_rem);
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
    gen res=_simplifier(linear_integrate(ee,x,remains_to_integrate,contextptr),contextptr);
    if (is_zero(remains_to_integrate))
      return res;
    else
      return res+symbolic(at_integrate,gen(makevecteur(remains_to_integrate,x),_SEQ__VECT));
  }

  static gen integrate0(const gen & e,const identificateur & x,gen & remains_to_integrate,GIAC_CONTEXT){
    if (e.type==_VECT){
      vecteur w;
      vecteur::const_iterator it=e._VECTptr->begin(),itend=e._VECTptr->end();
      for (;it!=itend;++it)
	w.push_back(integrate_id(*it,x,contextptr));
      return w;
    }
    gen ee=rewrite_hyper(e,contextptr),tmprem;
    gen res=linear_integrate(ee,x,tmprem,contextptr);
    remains_to_integrate=remains_to_integrate+tmprem;
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
    if ( (s==2) && (v[1].type==_SYMB) && (v[1]._SYMBptr->sommet==at_equal || v[1]._SYMBptr->sommet==at_same)){
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

  // "unary" version
  gen _integrate(const gen & args,GIAC_CONTEXT){
#ifdef LOGINT
    *logptr(contextptr) << gettext("integrate begin") << endl;
#endif
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    if (v.size()==1){
      gen a,b;
      if (is_algebraic_program(args,a,b) && a.type!=_VECT)
	return symbolic(at_program,makesequence(a,0,_integrate(gen(makevecteur(b,a),_SEQ__VECT),contextptr)));
      if (calc_mode(contextptr)==1)
	v.push_back(ggb_var(v.front()));
      else
	v.push_back(vx_var);
    }
    int s=v.size();
    if (!adjust_int_sum_arg(v,s))
      return gensizeerr(contextptr);
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
    }
    bool v0evaled=false;
    if (s>=4){ // take care of boundaries when evaluating
      gen xval=x.eval(1,contextptr);
      gen a(v[2]),b(v[3]);
      if (evalf_double(a,1,contextptr).type==_DOUBLE_ && evalf_double(b,1,contextptr).type==_DOUBLE_){
	if (is_greater(v[2],v[3],contextptr)){
	  a=v[3]; b=v[2];
	}
	giac_assume(symb_and(symb_superieur_egal(x,a),symb_inferieur_egal(x,b)),contextptr);
	v[0]=eval(v[0],eval_level(contextptr),contextptr); 
	v0evaled=true;
	sto(xval,x,contextptr);
      }
    }
    if (!v0evaled)
      v[0]=eval(v[0],eval_level(contextptr),contextptr); 
    if (x._IDNTptr->quoted)
      *x._IDNTptr->quoted=quoted;    
    if (s>4 || (approx_mode(contextptr) && (s==4)) ){
      v[1]=x;
      return _romberg(gen(v,_SEQ__VECT),contextptr);
    }
    gen rem,borne_inf,borne_sup,res;
    if (s==4){
      if ( (has_num_coeff(v[0]) ||
	    v[2].type==_FLOAT_ || v[2].type==_DOUBLE_ || v[2].type==_REAL ||
	    v[3].type==_FLOAT_ || v[3].type==_DOUBLE_ || v[3].type==_REAL) &&
	   lidnt(v[0])==vecteur(1,v[1])
	   )
	return _romberg(gen(makevecteur(v[0],v[1],v[2],v[3]),_SEQ__VECT),contextptr);
      borne_inf=v[2];
      borne_sup=v[3];
      v[0]=ceil2floor(v[0],contextptr);
      vecteur lfloor(lop(v[0],at_floor));
      lfloor=lvarx(lfloor,x);
      if (!lfloor.empty()){
	gen a,b,l,cond=lfloor.front()._SYMBptr->feuille,tmp;
	if (lvarx(cond,x).size()>1 || !is_linear_wrt(cond,x,a,b,contextptr) )
	  return gensizeerr(gettext("Floor definite integration: can only handle linear < or > condition"));
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
	  if (ctrl_c) { 
	    interrupted = true; ctrl_c=false;
	    gensizeerr(gettext("Stopped by user interruption."),res);
	    return res;
	  }
	  if (is_undef(res))
	    return res;
	}
	tmp=quotesubst(v[0],lfloor.front(),n1,contextptr);
	res += _integrate(makesequence(tmp,x,cur,borne_sup),contextptr);
	return res;
      }
      v[0]=when2piecewise(v[0],contextptr);
      vecteur lpiece(lop(v[0],at_piecewise));
      lpiece=lvarx(lpiece,x);
      if (!lpiece.empty()){
	gen piece=lpiece.front();
	if (!piece.is_symb_of_sommet(at_piecewise))
	  return gensizeerr(contextptr);
	gen piecef=piece._SYMBptr->feuille;
	if (piecef.type!=_VECT || piecef._VECTptr->size()<2)
	  return gensizeerr(contextptr);
	vecteur & piecev = *piecef._VECTptr;
	// check conditions: they must be linear wrt x
	int vs=piecev.size();
	for (int i=0;i<vs/2;++i){
	  bool unable=true;
	  gen cond=piecev[2*i];
	  if (cond.is_symb_of_sommet(at_equal) || cond.is_symb_of_sommet(at_same)){
	    *logptr(contextptr) << gettext("Assuming false condition ") << cond << endl;
	    continue;
	  }
	  if (cond.is_symb_of_sommet(at_different)){
	    *logptr(contextptr) << gettext("Assuming true condition ") << cond << endl;
	    v[0]=quotesubst(v[0],piece,piecev[2*i+1],contextptr);
	    res += _integrate(gen(makevecteur(v[0],x,borne_inf,borne_sup),_SEQ__VECT),contextptr);
	    return res;
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
	  if (unable || !is_linear_wrt(cond,x,a,b,contextptr))
	    return gensizeerr(gettext("Piecewise definite integration: can only handle linear < or > condition"));
	  // check if a*x+b>0 on [borne_inf,borne_sup]
	  l=-b/a;
	  bool positif=ck_is_greater(a,0,contextptr);
	  if ( positif?ck_is_greater(borne_inf,l,contextptr):ck_is_greater(l,borne_sup,contextptr)){ 
	    // the condition is met on the whole interval
	    // replace piecewise globally by v[2*i+1]
	    v[0]=quotesubst(v[0],piece,piecev[2*i+1],contextptr);
	    res += _integrate(gen(makevecteur(v[0],x,borne_inf,borne_sup),_SEQ__VECT),contextptr);
	    return res;
	  }
	  if (positif?!ck_is_greater(l,borne_sup,contextptr):!ck_is_greater(borne_inf,l,contextptr)){
	    gen tmp=quotesubst(v[0],piece,piecev[2*i+1],contextptr);
	    res += _integrate(gen(makevecteur(tmp,x,borne_inf,l),_SEQ__VECT),contextptr);
	    borne_inf=l;
	  }
	}
	if (vs%2){
	  v[0]=quotesubst(v[0],piece,piecev[vs-1],contextptr);
	  res += _integrate(gen(makevecteur(v[0],x,borne_inf,borne_sup),_SEQ__VECT),contextptr);
	}
	return res;
      } // end piecewise
      if (intgab(v[0],x,borne_inf,borne_sup,res,contextptr)){
	// additional check for singularities in ggb mode
	if (calc_mode(contextptr)==1 || abs_calc_mode(contextptr)==38){
	  bool ordonne=is_greater(borne_sup,borne_inf,contextptr);
	  vecteur sp=protect_find_singularities(v[0],*x._IDNTptr,false,contextptr);
	  int sps=sp.size();
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
    gen primitive=integrate0( v[0],*x._IDNTptr,rem,contextptr);
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
    if (ordonne)
      res=limit(primitive,*x._IDNTptr,borne_sup,-1,contextptr)-limit(primitive,*x._IDNTptr,borne_inf,1,contextptr);
    else {
      if ( (desordonne=is_greater(borne_inf,borne_sup,contextptr) ))
	res=limit(primitive,*x._IDNTptr,borne_sup,1,contextptr)-limit(primitive,*x._IDNTptr,borne_inf,-1,contextptr) ;
      else
	res=limit(primitive,*x._IDNTptr,borne_sup,0,contextptr)-limit(primitive,*x._IDNTptr,borne_inf,0,contextptr);
    }
    vecteur sp=protect_find_singularities(primitive,*x._IDNTptr,false,contextptr);
    // FIXME if v depends on an integer parameter, find values in inf,sup
    int sps=sp.size();
    for (int i=0;i<sps;i++){
      if ( (ordonne && is_strictly_greater(sp[i],borne_inf,contextptr) && is_strictly_greater(borne_sup,sp[i],contextptr) ) || 
	   (desordonne && is_strictly_greater(sp[i],borne_sup,contextptr) && is_strictly_greater(borne_inf,sp[i],contextptr) )
	   )
	res += limit(primitive,*x._IDNTptr,sp[i],-1,contextptr)-limit(primitive,*x._IDNTptr,sp[i],1,contextptr);
    }
    if (!is_zero(rem)){
      if (is_inf(res))
	return symbolic(at_integrate,gen(makevecteur(v[0],x,v[2],v[3]),_SEQ__VECT));
      res = res + symbolic(at_integrate,gen(makevecteur(rem,x,v[2],v[3]),_SEQ__VECT));
    }
    return res;
  }
  static const char _integrate_s []="integrate";
  static string texprintasintegrate(const gen & g,const char * s_orig,GIAC_CONTEXT){
    string s("\\int ");
    if (g.type!=_VECT)
      return s+gen2tex(g,contextptr);
    vecteur v(*g._VECTptr);
    int l(v.size());
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

  gen romberg(const gen & f0,const gen & x0,const gen & a,const gen &b,const gen & eps,int nmax,GIAC_CONTEXT){
    gen x(x0),f(f0);
    if (x.type!=_IDNT){
      x=identificateur(" x");
      f=subst(f,x0,x,false,contextptr);
    }
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
	fy=limit(f,*x._IDNTptr,exact(y,contextptr),0,contextptr);
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
	    fy=limit(f,*x._IDNTptr,exact(y,contextptr),0,contextptr);
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
	old_line=cur_line;
      }
      if (calc_mode(contextptr)==1)
	return undef;
      *logptr(contextptr) << gettext("Unable to find numeric integral using Romberg method, returning the last computed line of approximations") << endl;
      cur_line=makevecteur(old_line.back(),cur_line.back());
      return cur_line;
      return rombergo(f,x,a,b,nmax,contextptr);
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
	  fy=limit(f,*x._IDNTptr,exact(y,contextptr),0,contextptr);
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
      old_line=cur_line;
    }
    if (calc_mode(contextptr)==1)
      return undef;
    *logptr(contextptr) << gettext("Unable to find numeric integral using Romberg method, returning the last computed line of approximations") << endl;
    cur_line=makevecteur(old_line.back(),cur_line.back());
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
  gen _romberg(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return symbolic(at_romberg,args);
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
      if (x.is_symb_of_sommet(at_equal)){
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
      return _romberg(makesequence(f,x,a,b),contextptr);
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
    return romberg(f,x,a,b,eps,n,contextptr);
  }
  static const char _romberg_s []="romberg";
  static define_unary_function_eval (__romberg,&_romberg,_romberg_s);
  define_unary_function_ptr5( at_romberg ,alias_at_romberg,&__romberg,0,true);

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
      int n=Q.size();
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
    int s1=v1.size(),s2=v2.size();
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
      lcmdeno(P,den,contextptr);
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
    int vdim=v.size();
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
	lcmdeno(constante,const_den,contextptr);
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
	int decal=jt->mult-dec.size();
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

  vecteur decalage(const polynome & A,const polynome & B){
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
    polynome pres=*resu._POLYptr;
    // Make the list of the positive integer roots k in t of the resultant
    return iroots(pres);
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
    int d=racines.size();
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
	gen ydeg=(vr[q-1]-vq[q-1])/qq;
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
    m=mrref(m,contextptr);
    vecteur res(y+1);
    for (int i=0;i<=y;++i){
      if (is_zero(m[i][i]))
	return false;
      res[i]=m[i][y+1]/m[i][i];
    }
    lcmdeno(res,deno,contextptr);
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
    if (!loptab(v,sincostan_tab).empty())
      return false;
    // if v contains a non linear exp abort
    int vs=v.size();
    gen a,b;
    for (int i=0;i<vs;++i){
      if (v[i].is_symb_of_sommet(at_exp) && !is_linear_wrt(v[i]._SYMBptr->feuille,x,a,b,contextptr))
	return false;
    }
    gen ratio=simplify(subst(e,x,x+1,false,contextptr)/e,contextptr);
    v=lvarx(makevecteur(ratio,x),x);
    if ( (v.size()!=1) || (v.front()!=x) ){
      ratio=simplify(_texpand(ratio,contextptr),contextptr);
      v=lvarx(makevecteur(ratio,x),x);
      if ( (v.size()!=1) || (v.front()!=x) )
	return false;
    }
    lvar(ratio,v);
    int s=v.size();
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
    int s=v.size();
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
    int s=newv.size();
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
    int s=v.size();
    if (!s)
      return isprod?1:0;
    int debut=1,fin=s;
    if (v[0].type==_VECT && g.subtype==_SEQ__VECT && s>1 && v[1].type==_INT_){
      debut=giacmax(1,v[1].val);
      if (s>2 && v[2].type==_INT_)
	fin=v[2].val;
      v=*v[0]._VECTptr;
      s=v.size();
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
      if (type==1)
	return prodsum(v,true);
      if (type==2)
	return prodsum(v,false);
    }
    gen debut=v[2],fin=v[3];
    if (is_greater(abs(fin-debut),LIST_SIZE_LIMIT,contextptr))
      return gendimerr(contextptr);
    vecteur res;
    if (is_strictly_greater(debut,fin,contextptr)){
      if (is_positive(step,contextptr))
	step=-step;
      for (;!ctrl_c && is_greater(debut,fin,contextptr);debut=debut+step){
	tmp=quotesubst(v[0],v[1],debut,contextptr);
	tmp=quotesubst(eval(tmp,contextptr),v[1],debut,contextptr);
#ifdef RTOS_THREADX
	tmp=evalf(tmp,1,contextptr);
#endif
	res.push_back(tmp);
      }
    }
    else {
      if (is_positive(-step,contextptr))
	step=-step;
      for (;!ctrl_c && is_greater(fin,debut,contextptr);debut=debut+step){
	tmp=quotesubst(v[0],v[1],debut,contextptr);
	tmp=quotesubst(eval(tmp,contextptr),v[1],debut,contextptr);
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
      int protect=bind(vecteur(1,debut),localvar,newcontextptr);
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
    int s=v.size();
    if (s<2)
      return false; // setsizeerr(contextptr);
    if (v[0].is_symb_of_sommet(at_quote))
      v[0]=v[0]._SYMBptr->feuille;
    if (v[1].type!=_IDNT){
      if (v[1].is_symb_of_sommet(at_equal) && v[1]._SYMBptr->feuille.type==_VECT){
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
    maple_sum_product_unquote(v,contextptr);
    int s=v.size();
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
      is_integral(v[2]);
      is_integral(v[3]);
      if (is_zero(v[2]-v[3]-1))
	return zero;
      if (is_positive(v[2]-v[3]-1,contextptr))
	return -_sum(gen(makevecteur(v[0],v[1],v[3]+1,v[2]-1),_SEQ__VECT),contextptr);
      if (is_strictly_positive(-v[2],contextptr) && is_positive(-v[3],contextptr)){
	gen tmp=quotesubst(v[0],v[1],-v[1],contextptr);
	return _sum(gen(makevecteur(tmp,v[1],-v[3],-v[2]),args.subtype),contextptr);
      }
      if (v[2].type==_INT_ && v[3].type==_INT_ && absint(v[3].val-v[2].val)<max_sum_add(contextptr))
	return ratnormal(seqprod(v,2,contextptr));
    }
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
    if ( (x.type!=_INT_)  || (x.val<0))// Should add bernoulli polynomials 
      return gensizeerr(gettext("bernoulli"));
    int n=x.val;
    if (!n)
      return plus_one;
    if (n==1)
      return minus_one_half;
    if (n%2)
      return zero;
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
    return rdiv(-b,n+1,context0);
  }
  gen _bernoulli(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
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
      cerr << err.what() << endl;
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
      cerr << err.what() << endl;
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
    unsigned dim=v.size();
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
    int dim=y0v.size();
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
    double * y=new double[dim];
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
#ifdef HAVE_LIBGSL
    if (!iscomplex && t0_e.type==_DOUBLE_ && t1_e.type==_DOUBLE_ && is_zero(im(tmp,contextptr))){
      double t0=t0_e._DOUBLE_val;
      double t1=t1_e._DOUBLE_val;
      bool time_reverse=(t1<t0);
      double t=t0;
      if (time_reverse){
	t0=-t0;
	t1=-t1;
      }
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
	  cerr << nstep << ":" << t << ",y5=" << double2vecteur(y,dim) << endl;
	if (return_curve)  {
	  if ( (t-oldt)> tstep/2 || t==t1){
	    oldt=t;
	    if (time_reverse)
	      resv.push_back(makevecteur(-t,double2vecteur(y,dim)));
	    else
	      resv.push_back(makevecteur(t,double2vecteur(y,dim)));
	  }
	  for (int i=0;i<dim;++i){
	    // cerr << y[i] << endl;
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
    vecteur y_final5(dim),y_final4(dim);
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
	for (int k=0;k<j;k++){
	  gen bak=butcher_a[butcher_a_shift+k];
	  const vecteur & bkk=(*butcher_k[k]._VECTptr);
	  for (int i=0;i<dim;++i){
	    yt1[i] += bak*bkk[i];
	  }
	}
	butcher_a_shift += j;
	yt1[dim]=yt[dim]+butcher_c[j]*dt;
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
	  y_final4[i] += bb4j*bkj[i];
	}
      }
      // accept or reject current step and compute dt
      double err=rk_error(y_final4,y_final5,yt,contextptr);
      gen hopt=.9*tstep*pow(tolerance/err,.2,contextptr);
      if (debug_infolevel>5)
	cerr << nstep << ":" << t_e << ",y5=" << y_final5 << ",y4=" << y_final4 << " " << tstep << " hopt=" << hopt << " err=" << err << endl;
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
	    // cerr << y[i] << endl;
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
    int vs=v.size();
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
    vs=v.size();
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
    if (x.is_symb_of_sommet(at_equal))
      x=x._SYMBptr->feuille[0];
    if (w.size()>=5)
      X=symb_equal(x,symb_interval(w[3],w[4]));
    if (X.is_symb_of_sommet(at_equal) && X._SYMBptr->feuille[1].is_symb_of_sommet(at_interval)){
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
    F += u*v;
    if (bound)
      F = preval(F,x,a,b,contextptr);    
    return makevecteur(F,normal(-u*derive(v,x,contextptr),contextptr));
  }
  static const char _ibpdv_s []="ibpdv";
  static define_unary_function_eval (__ibpdv,&_ibpdv,_ibpdv_s);
  define_unary_function_ptr5( at_ibpdv ,alias_at_ibpdv,&__ibpdv,0,true);

  gen fourier_an(const gen & f,const gen & x,const gen & T,const gen & n,const gen & a,GIAC_CONTEXT){
    gen primi,iT=inv(T,contextptr);
    gen omega=2*cst_pi*iT;
    primi=_integrate(gen(makevecteur(f*cos(omega*n*x,contextptr),x,a,ratnormal(a+T)),_SEQ__VECT),contextptr);
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
    primi=_integrate(gen(makevecteur(f*sin(omega*n*x,contextptr),x,a,ratnormal(a+T)),_SEQ__VECT),contextptr);
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
    primi=_integrate(gen(makevecteur(f*exp(-cst_i*omega*n*x,contextptr),x,a,ratnormal(a+T)),_SEQ__VECT),contextptr);
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

  

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

