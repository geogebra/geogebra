// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c intgab.cc -DHAVE_CONFIG_H -DIN_GIAC" -*-
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
#include "vector.h"
#include <cmath>
#include <cstdlib>
#include "sym2poly.h"
#include "usual.h"
#include "intgab.h"
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
#include "intg.h"
#include "desolve.h"
#include "alg_ext.h"
#include "misc.h"
#include "maple.h"
#include "rpn.h"
#include "giacintl.h"
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

  // check whether an expression is meromorphic 
  // returns -1 if x is not an IDNT
  // return 2 if rational
  // return 3 if rational fraction of x, sin(a*x+b), cos(a*x+b)
  // return 4 if rational fraction of x, exp(a*x+b) where re(a)!=0
  // return 5 if ln(P)*A==rational fraction + B==rational fraction
  // TODO: 6 if exp(a[0]*x^2+a[1]*x+a[2])*b+P, P,b =rational, a[0]<0
  int is_meromorphic(const gen & g,const gen & x,gen & a,gen & b,gen & P,GIAC_CONTEXT){
    if (x.type!=_IDNT)
      return -1;
    if (g.type<=_IDNT)
      return 2;
    if (g.type==_VECT){
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	if (!is_meromorphic(*it,x,a,b,P,contextptr))
	  return 0;
      }
      return 1;
    }
    if (g.type==_SYMB){
      vecteur v;
      rlvarx(g,x,v);
      sort(v.begin(),v.end(),islesscomplexthanf);
      if (v==vecteur(1,x))
	return 2;
      if (v.size()<2 || v[1].type!=_SYMB)
	return 0;
      P=v[1]._SYMBptr->feuille;
      if (v.size()==2) {
	if (v[1].is_symb_of_sommet(at_exp)){ 
	  if (is_linear_wrt(P,x,a,b,contextptr)){
	    if (is_zero(re(a,contextptr))){
	      a=a/cst_i;
	      b=b/cst_i;
	      return 3;
	    }
	    return 4;
	  }
	  identificateur t(" t");
	  gen tt(t);
	  gen g2=subst(g,v[1],tt,false,contextptr),bcst;
	  if (is_linear_wrt(g2,t,b,bcst,contextptr)){
	    gen A,B,C;
	    if (is_quadratic_wrt(P,x,A,B,C,contextptr) && is_strictly_positive(-A,contextptr)){
	      a=makevecteur(A,B,C);
	      P=bcst;
	      return 6;
	    }
	  }
	}
	if ( (v[1].is_symb_of_sommet(at_cos) || v[1].is_symb_of_sommet(at_sin)) && is_linear_wrt(P,x,a,b,contextptr) && is_zero(im(a,contextptr)) )
	  return 3;
	if (v[1].is_symb_of_sommet(at_ln)){
	  identificateur t(" t");
	  gen tt(t);
	  gen g2=subst(g,v[1],tt,false,contextptr);
	  if (is_linear_wrt(g2,t,a,b,contextptr))
	    return 5;
	}
      }
      if (v.size()==3){
	if (v[1].is_symb_of_sommet(at_cos) && v[2].is_symb_of_sommet(at_sin)){
	  gen v2f=v[2]._SYMBptr->feuille;
	  if (P==v2f && is_linear_wrt(P,x,a,b,contextptr) && is_zero(im(a,contextptr)))
	    return 3;
	}
      }
      const_iterateur it=v.begin(),itend=v.end();
      for (;it!=itend;++it){
	if (it->type==_IDNT)
	  continue;
	if (it->type!=_SYMB)
	  return 0;
	unary_function_ptr * u=&it->_SYMBptr->sommet;
	if (u==at_sin || u==at_cos || u==at_tan || u==at_exp || u==at_sinh || u==at_cosh || u==at_tanh){
	  // the argument must not have singularities
	  if (find_singularities(it->_SYMBptr->feuille,*x._IDNTptr,1,contextptr).empty())
	    continue;
	}
	return 0;
      }
      return 1;
    }
    return 0;
  }

  int fast_is_even_odd(const gen & f,const gen & x,GIAC_CONTEXT){
    if (f==x) // x is odd
      return 2;
    if (f.type==_VECT){
      vecteur & v =*f._VECTptr;
      const_iterateur it=v.begin(),itend=v.end();
      int res=0,current;
      for (;it!=itend;++it){
	if (! (current=fast_is_even_odd(*it,x,contextptr)) )
	  return 0;
	if (!res)
	  res=current;
	if (res!=current)
	  return 0;
      }
      return res;
    }
    if (f.type!=_SYMB) // constant is even
      return 1;
    gen & ff=f._SYMBptr->feuille;
    int res;
    const unary_function_ptr & u = f._SYMBptr->sommet;
    if (u==at_pow && ff.type==_VECT && ff._VECTptr->size()==2){
      gen ff2=ff._VECTptr->back();
      if (ff2.type==_INT_){
	gen ff1=ff._VECTptr->front();
	res=fast_is_even_odd(ff1,x,contextptr);
	if (res<2)
	  return res;
	return (ff2.val%2)?2:1;
      }
      return 0;
    }
    res=fast_is_even_odd(ff,x,contextptr);
    if (res<2)
      return res;
    // ff is odd
    if (u==at_plus || u==at_neg || u==at_inv || u==at_sin || u==at_tan || u==at_sinh || u==at_tanh || u==at_atan || u==at_atanh)
      return res;
    if (u==at_prod){
      if (ff.type!=_VECT || (ff._VECTptr->size()%2))
	return res;
      return 1;
    }
    if (u==at_cos || u==at_cosh || u==at_abs)
      return 1;
    return 0;
  }

  // 0 none, 1 even, 2 odd
  int is_even_odd(const gen & f,const gen & x,GIAC_CONTEXT){
    int res=fast_is_even_odd(f,x,contextptr);
    if (res)
      return res;
    gen f1=f,f2=subst(f,x,-x,false,contextptr);
    if (lvar(f)==vecteur(1,x)){ // rational case
      if (is_zero(normal(f1-f2,contextptr)))
	return 1;
      if (is_zero(normal(f1+f2,contextptr)))
	return 2;
      return 0;
    }
    f1=normal(_texpand(f1,contextptr),contextptr);
    f2=normal(_texpand(f2,contextptr),contextptr);
    if (f1==f2)
      return 1;
    if (is_zero(f1+f2))
      return 2;
    return 0;
  }

  // residue of g at x=a
  gen residue(const gen & g_,const gen & x,const gen & a,GIAC_CONTEXT){
    if (x.type!=_IDNT)
      return gensizeerr(contextptr);
    gen g=_pow2exp(tan2sincos(g_,contextptr),contextptr);
    for (int ordre=2;ordre<max_series_expansion_order;ordre=2*ordre){
      sparse_poly1 s=series__SPOL1(g,*x._IDNTptr,a,ordre,0,contextptr);
      int n=s.size();
      if (n && (is_undef(s[0].coeff) || is_undef(s[0].exponent)))
	continue; // stop the loop, try a larger order
      for (int i=0;i<n;++i){
	gen e1=s[i].exponent+1;
	if (is_strictly_positive(e1,contextptr)){
	  gen res=s[i].coeff;
	  if (is_zero(derive(res,x,contextptr)))
	    return 0;
	  return gensizeerr(gettext("Non holomorphic function ")+g.print(contextptr)+" at "+x.print(contextptr)+"="+a.print(contextptr));
	}
	if (is_undef(s[i].coeff))
	  break; // stop the loop, try a larger order
	if (is_zero(e1)){
	  gen res=s[i].coeff;
	  if (is_zero(derive(res,x,contextptr)))
	    return res;
	  return gensizeerr(gettext("Non holomorphic function ")+g.print(contextptr)+" at "+x.print(contextptr)+"="+a.print(contextptr));
	}
	if (i==n-1) // exact expansion
	  return 0;
      }
    }
    return genmaxordererr(contextptr);
  }
  gen _residue(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT) return gensizeerr(contextptr);
    vecteur v(*args._VECTptr);
    int s=v.size();
    if (s==2){
      if (v[1].is_symb_of_sommet(at_equal)){
	vecteur & w=*v[1]._SYMBptr->feuille._VECTptr;
	v.push_back(w[1]);
	v[1]=w[0];
      }
      else 
	return gensizeerr(gettext("Syntax residue(expr,x=a)"));
      ++s;
    }
    if (s<3)
      return gensizeerr(contextptr);
    return residue(v[0],v[1],v[2],contextptr);
  } 
  static const char _residue_s []="residue";
  static define_unary_function_eval (__residue,&_residue,_residue_s);
  define_unary_function_ptr5( at_residue ,alias_at_residue,&__residue,0,true);

  vecteur singular(const gen & g,const gen & x,GIAC_CONTEXT){
    // FIXME handle set of variables
    if (x.type!=_IDNT)
      return vecteur(1,gentypeerr(contextptr));
    vecteur res=find_singularities(g,*x._IDNTptr,1,contextptr);
    vecteur res2;
    const_iterateur it=res.begin(),itend=res.end();
    for (;it!=itend;++it){
      if (!equalposcomp(res2,*it))
	res2.push_back(*it);
    }
    return res2;
  }
  gen _singular(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    int s=v.size();
    if (s==1){
      v.push_back(vx_var);
      ++s;
    }
    if (s<2)
      return gensizeerr(contextptr);
    return singular(v[0],v[1],contextptr);
  } 
  static const char _singular_s []="singular";
  static define_unary_function_eval (__singular,&_singular,_singular_s);
  define_unary_function_ptr5( at_singular ,alias_at_singular,&__singular,0,true);

  bool assume_t_in_ab(gen & t,const gen & a,const gen & b,bool exclude_a,bool exclude_b,GIAC_CONTEXT){
    vecteur v_interval(1,gen(makevecteur(a,b),_LINE__VECT));
    vecteur v_excluded;
    if (exclude_a)
      v_excluded.push_back(a);
    if (exclude_b)
      v_excluded.push_back(b);
    return !is_undef(sto(gen(makevecteur(gen(_DOUBLE_).change_subtype(1),v_interval,v_excluded),_ASSUME__VECT),t,contextptr));
  }

  // reduce g, a rational fraction wrt to x, to a sqff lnpart
  // and adds the non sqff integrated part to ratpart
  static bool intreduce(const gen & e,const gen & x,gen & lnpart,gen & ratpart,GIAC_CONTEXT){
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
    if (r_num.type==_EXT){
      return false;
    }
    if (r_den.type!=_POLY){
      l.front()._VECTptr->front()=x;
      lnpart=0;
      if (r_num.type==_POLY)
	ratpart=rdiv(r2e(r_num._POLYptr->integrate(),l,contextptr),r2sym(r_den,l,contextptr),contextptr);
      else 
	ratpart=e*x;
      return true;
    }
    polynome den(*r_den._POLYptr),num(s);
    if (r_num.type==_POLY)
      num=*r_num._POLYptr;
    else
      num=polynome(r_num,s);
    l.front()._VECTptr->front()=x;
    polynome p_content(lgcd(den));
    factorization vden(sqff(den/p_content)); // first square-free factorization
    vector< pf<gen> > pfde_VECT;
    polynome ipnum(s),ipden(s),temp(s),tmp(s);
    partfrac(num,den,vden,pfde_VECT,ipnum,ipden);
    vector< pf<gen> >::iterator it=pfde_VECT.begin();
    vector< pf<gen> >::const_iterator itend=pfde_VECT.end();
    vector< pf<gen> > ratpartv;
    for (;it!=itend;++it){
      pf<gen> single(intreduce_pf(*it,ratpartv));
      lnpart += r2e(single.num,l,contextptr)/r2e(single.den,l,contextptr);
      // FIXME: add ratpartv to ratpart
    }
    return true;
  }

  static bool intgab_sincos(const gen & g,const gen & x,const gen & a,const gen & b,gen & A, gen & B,gen & res,GIAC_CONTEXT){
    gen g1=trig2exp(g,contextptr);
    // write it as a rational fraction of x,X=exp(i*(A*x+b))
    identificateur Xid(" X");
    gen X(Xid),expx(exp(cst_i*(A*x+B),contextptr));
    g1=subst(g1,expx,X,false,contextptr);
    if (is_positive(-A,contextptr))
      g1=subst(g1,X,inv(X,contextptr),false,contextptr);
    // Separable variables?
    vecteur f=factors(g1,x,contextptr); // Factor then split factors
    gen xfact(plus_one),Xfact(plus_one);
    if (separate_variables(f,Xid,x,Xfact,xfact,contextptr)){
      // xfact must be a proper fraction
      if (!is_zero(limit(xfact,*x._IDNTptr,plus_inf,1,contextptr))){
	res=undef;
	return true;
      }
      // Xfact must be rewritten as a generalized polynomial part
      // + a rational part N/D, the roots of D are in pairs r, 1/conj(r)
      // if there are roots with norm = 1, D vanishes inf. times on R
      // hence the integral is undef
      // we select all roots with norm > 1 and take the corresp. part of
      // the partial fraction expansion, we have
      // N/D=2*re(true_poly_part)+2*re(n/d)-re(n(0)/d(0))
      // where d has no poles in C^+ and X tends to 0 at inf in C^+
      vecteur vX(1,X);
      lvar(Xfact,vX);
      gen ND=sym2r(Xfact,vX,contextptr);
      gen N,D;
      fxnd(ND,N,D);
      polynome Np,Dp,Q,R;
      if (D.type!=_POLY)
	return false;
      Dp=*D._POLYptr;
      if (N.type==_POLY)
	Np=*N._POLYptr;
      else
	Np=polynome(N,1);
      Np.TDivRem(Dp,Q,R);
      int Qd=Q.degree(0);
      // Q is the true poly part
      if (Qd){
	// Dp must be divisible by Qd
	int Dval=Dp.valuation(0);
	if (Dval!=Qd)
	  return false; // setsizeerr();
	index_t decal(vX.size());
	decal[0]=-Dval;
	Dp=Dp.shift(decal);
	polynome XQd(gen(1),vX.size()),U(vX.size()),V(vX.size()),C(vX.size());
	decal[0]=Dval;
	XQd=XQd.shift(decal);
	Tabcuv(Dp,XQd,R,U,V,C); // C*Np=Dp*U+X^Dval*V
	// Np/(Dp*X^Dval)=V/Dp/C+...
	R=V;
	Dp=Dp*C;
      }
      if (!Q.coord.empty() && Q.coord.back().index.is_zero())
	Q.coord.back().value=Q.coord.back().value/2;
      // R/Dp is the true fractional part, Q is the true poly. part
      // now check roots of norm=1 
      gen dp=r2sym(Dp,vX,contextptr);
      identificateur XXi(" x"),XYi(" y");
      gen XX(XXi),XY(XYi);
      dp=subst(dp,X,XX+cst_i*XY,false,contextptr);
      dp=_resultant(gen(makevecteur(dp,XX*XX+XY*XY-1,XY),_SEQ__VECT),contextptr);
      if (is_undef(dp)) return false;
      dp=gcd(re(dp,contextptr),im(dp,contextptr));
      vecteur vdp=factors(dp,XX,contextptr);
      int vdps=vdp.size();
      for (int i=0;i<vdps;i+=2){
	if (sturmab(vdp[i],XX,-1,1,contextptr)>0){
	  res=undef;
	  return true;
	}
      }
      // ok, now find roots of norm>1
      factorization fd;
      polynome Dp_content;
      gen extra_div=1;
      if (!factor(Dp,Dp_content,fd,false,true,true,1,extra_div) || extra_div!=1){
	*logptr(contextptr) << gettext("Unable to factor ") << r2sym(Dp,vX,contextptr) << endl;
	res=undef;
	return true;
      }
      // check that each factor has degree 1
      polynome D1(gen(1),vX.size());
      factorization::const_iterator f_it=fd.begin(),f_itend=fd.end();
      for (;f_it!=f_itend;++f_it){
	if (f_it->fact.degree(0)>1){
	  *logptr(contextptr) << gettext("Unable to factor ") << r2sym(f_it->fact,vX,contextptr) << endl;
	  res=undef;
	  return true;
	}
	if (f_it->fact.coord.size()==2){
	  gen f1=f_it->fact.coord.front().value;
	  gen f2=f_it->fact.coord.back().value;
	  gen f21=r2sym(-f2/f1,vecteur(0),contextptr);
	  if (is_positive(abs(f21,contextptr)-1,contextptr))
	    D1=D1*pow(f_it->fact,f_it->mult);
	}
      } // end f_it
      // keep only those roots
      polynome D2,tmp,U,V,C;
      Dp.TDivRem(D1,D2,tmp);
      Tabcuv(D1,D2,R,U,V,C); // R/D=(D1*U+D2*V)/(C*D1*D2) -> V/(C*D1)
      Dp=C*D1;
      R=V; // back to integrating 2*Re(R/Dp)
      // find R/Dp at 0, real part should be substracted
      vecteur Rv(polynome2poly1(R,1)),Dv(polynome2poly1(Dp,1)),Qv(polynome2poly1(Q,1));
      vecteur vX1(vX.begin()+1,vX.end());
      Rv=*r2sym(Rv,vX1,contextptr)._VECTptr;
      Dv=*r2sym(Dv,vX1,contextptr)._VECTptr;
      Qv=*r2sym(Qv,vX1,contextptr)._VECTptr;
      gen correc=re(Rv.back()/Dv.back(),contextptr);
      xfact=xfact*(2*(horner(Rv,expx)/horner(Dv,expx)+horner(Qv,expx))-correc);
      res=0;
      vecteur v=singular(xfact,x,contextptr);
      if (!v.empty() && is_undef(v.front()))
	return false;
      int s=v.size();
      for (int i=0;i<s;++i){
	gen coeff=0,vi=v[i];
	if (is_real(vi,contextptr)){
	  // check that xfact has a finite limit
	  gen test=limit(g,*x._IDNTptr,vi,0,contextptr);
	  if (is_inf(test)){
	    res=undef;
	    return true;
	  }
	  coeff=1;
	}
	else {
	  if (ck_is_positive(im(v[i],contextptr),contextptr))
	    coeff=2;
	}
	if (!is_zero(coeff)){
	  coeff=coeff*residue(xfact,x,vi,contextptr);
	  if (is_undef(coeff))
	    return false;
	  res=res+re(coeff*cst_i*cst_pi,contextptr);
	}
      }
      res = normal(res,contextptr);
      return true;
    } // end if separate_variables
    return false;
  } 

  static int nvars_depend_x(const vecteur & v,const gen & x){
    int res=0;
    for (unsigned int i=0;i<v.size();i++){
      if (contains(v[i],x))
	++res;
    }
    return res;
  }

  bool intgab_r(const gen & g0,const gen & x,const gen & a,const gen & b,bool rational,gen & res,GIAC_CONTEXT){
    gen g(g0);
    // check if g may be integrated using the residue formula
    gen A,B,P;
    int typeint=rational?2:is_meromorphic(g,x,A,B,P,contextptr);
    if (typeint==5){ 
      // A*ln(P(x))+B, A/B/P rational fractions
      bool estreel=is_zero(im(A,contextptr))&&is_zero(im(P,contextptr));
      vecteur lv(1,x);
      lvar(P,lv);
      int lvs=lv.size();
      for (int i=0;i<lvs;++i){
	gen tmp=derive(lv[i],x,contextptr);
	if (!is_zero(tmp) && !is_one(tmp))
	  return false;
      }
      gen resB;
      if (!intgab(B,x,a,b,resB,contextptr))
	return false;
      gen Af=sym2r(A,lv,contextptr),Anum,Aden;
      fxnd(Af,Anum,Aden);
      int numdeg=0;
      if (Anum.type==_POLY)
	numdeg=Anum._POLYptr->lexsorted_degree();
      int dendeg=0;
      if (Aden.type==_POLY)
	dendeg=Aden._POLYptr->lexsorted_degree();
      if (numdeg>=dendeg-1)
	return false;
      // A must have non real roots
      vecteur rA=singular(A,x,contextptr);
      if (!rA.empty() && is_undef(rA))
	return false;
      vecteur Pv=factors(P,x,contextptr);
      int Pvs=Pv.size()/2;
      for (int Pi=0;Pi<Pvs;++Pi){
	gen somme_residus=0;
	P=Pv[2*Pi];
	int cur_mult=Pv[2*Pi+1].val;
	if (!equalposcomp(lidnt(P),x)){
	  gen resA;
	  if (!intgab(A,x,a,b,resA,contextptr))
	    return false;
	  res += cur_mult*resA*ln(P,contextptr);
	  continue;
	}
	vecteur rP=singular(symbolic(at_ln,P),x,contextptr);
	if (!rP.empty() && is_undef(rP))
	  return false;
	// compute constant coeff of P
	gen P2=sym2r(P,lv,contextptr),N,D;
	fxnd(P2,N,D);
	gen lncst=ln(_lcoeff(gen(makevecteur(r2sym(N,lv,contextptr),x),_SEQ__VECT),contextptr)/_lcoeff(gen(makevecteur(r2sym(D,lv,contextptr),x),_SEQ__VECT),contextptr),contextptr);
	// roots and poles of P are sorted: for im>0, contour is C-
	// for im<=0, contour is C+, 
	// for roots of P take +residue(ln(x-r)*A)
	// for poles of P take -residue(ln(x-r)*A)
	int rAs=rA.size(),rPs=rP.size();
	for (int i=0;i<rAs;++i){
	  gen racineA=rA[i];
	  bool residucplus=ck_is_positive(im(racineA,contextptr),contextptr);
	  if (residucplus && !is_zero(lncst))
	    somme_residus += lncst*residue(A,*x._IDNTptr,racineA,contextptr);
	  if (is_undef(somme_residus))
	    return false;
	  for (int j=0;j<rPs;++j){
	    gen racineP=rP[j];
	    gen imracineP=im(racineP,contextptr);
	    bool racinePcplus=ck_is_strictly_positive(imracineP,contextptr);
	    if (racinePcplus){ // contour is C- for this pole/root of ln
	      if (!estreel && !residucplus){
		gen tmp=residue(A*ln(x-racineP,contextptr),*x._IDNTptr,racineA,contextptr);
		if (is_undef(tmp))
		  return false;
		somme_residus -= tmp;
	      }
	    }
	    else { // contour is C+ for this pole/root of ln
	      if (residucplus){
		gen tmp=residue(A*ln(x-racineP,contextptr),*x._IDNTptr,racineA,contextptr);
		if (is_undef(tmp))
		  return false;
		if (estreel && !is_zero(imracineP))
		  tmp=2*cst_i*im(tmp,contextptr);
		somme_residus += tmp;
	      }
	    }
	  }
	}
	somme_residus=normal(2*cur_mult*cst_pi*cst_i*somme_residus,contextptr);
	res= somme_residus+resB;
      }
      return true;
    }
    if (typeint==4){ 
      // rational fraction of x and exp(A*x+B)
      // the exp part is periodic if x -> x+2*i*pi/A
      // if the rat frac of x and rat frac of exp are separate
      // the problem is to find a function such that
      // f(.+2*i*pi/A)-f(.)=ratfrac(x)
      identificateur Xid(" X");
      gen X(Xid),expx(symb_exp(A*x+B));
      gen g1=subst(g,expx,X,false,contextptr);
      // Separable variables?
      vecteur f=factors(g1,x,contextptr); // Factor then split factors
      gen xfact(plus_one),Xfact(plus_one),T(2*cst_i*cst_pi/A);
      gen imT(im(T,contextptr));
      if (separate_variables(f,x,Xid,xfact,Xfact,contextptr)){
	// rescale xfact, in order to find a discrete antiderivative
	gen xfactscaled=subst(xfact,x,x*T,false,contextptr),remains_to_sum,xfactint;
	if (!rational_sum(xfactscaled,x,xfactint,remains_to_sum,
			  /* psi allowed */ true,contextptr))
	  return false;
	// psi function has poles at 0,-1,-2,...
	// all have Laurent series -1/(x-pole)
	xfactint=ratnormal(subst(xfactint,x,x/T,false,contextptr));
	// now int()==contour_integral of xfactint*Xfact over rectangle
	// -inf .. + inf -> inf+T ..-inf+T ->
	// just compute all residues at poles where im is in [0,im(T)]
	// for xfactint, poles are the same as the poles of xfact translated
	// for Xfact, find pole in X then take A*x+B=ln(pole in X)
	vecteur rA=singular(xfact,x,contextptr);
	vecteur rP=singular(Xfact,X,contextptr);
	if (is_undef(rA) || is_undef(rP))
	  return false;
	gen tmp=xfactint*subst(Xfact,X,expx,false,contextptr);
	gen somme_residus;
	int rAs=rA.size(),rPs=rP.size();
	for (int i=0;i<rAs;++i){
	  gen rac=rA[i];
	  // adjust imaginary part
	  gen imrac=im(rac,contextptr);
	  gen k=_floor(imrac/imT,contextptr);
	  rac -= k*T;
	  if (is_positive(k,contextptr))
	    somme_residus += residue(tmp,*x._IDNTptr,rac,contextptr);
	  if (is_undef(somme_residus))
	    return false;
	}
	for (int i=0;i<rPs;++i){
	  gen rac=(ln(rP[i],contextptr)-B)/A;
	  // adjust imaginary part between 0 and im(T)
	  gen imrac=im(rac,contextptr);
	  gen k=_floor(imrac/imT,contextptr);
	  rac -= k*T;
	  somme_residus += residue(tmp,*x._IDNTptr,rac,contextptr);
	  if (is_undef(somme_residus)) return false;
	}
	res=normal(-2*cst_pi*cst_i*somme_residus,contextptr); 
	// - because the integration on inf+T..-inf+T is done backward
	return true;
      }
      return false;
    }
    if (typeint==3){ 
      // rational fraction of x and sin(A*x+B)|cos(A*x+B) or exp(i*(A*x+B))
      gen img=im(g,contextptr);
      img=simplify(img,contextptr);
      if (!is_zero(img)){
	gen resre,resim;
	gen reg=simplify(re(g,contextptr),contextptr);
	if (!intgab_sincos(reg,x,a,b,A,B,resre,contextptr))
	  return false;
	if (!intgab_sincos(img,x,a,b,A,B,resim,contextptr))
	  return false;
	res=resre+cst_i*resim;
	return true;
      }
      return intgab_sincos(g,x,a,b,A,B,res,contextptr);
    }
    if (typeint==2){ 
      // FIXME replace by if (typeint>0) but should handle transc. func.
      // correctly...
#ifndef NO_STDEXCEPT
      try {
#endif
	gen gl,glim;
	identificateur t(" t"),r(" r");
	gen gt(t),gr(r),geff(g);
	if (typeint==2){
	  // replace g by the log part of g
	  if (intgab_ratfrac(g,x,res,contextptr)){
	    return true;
	  }
	  gen ratpart,lnpart;
	  if (!intreduce(g,x,lnpart,ratpart,contextptr))
	    return false;
	  gl=limit(g,*x._IDNTptr,plus_inf,1,contextptr);
	  geff=lnpart;
	}
	else {
	  // has limit 0 at infinity in the upper or lower half plane
	  // replace x by r*exp(i.t), assume(t in ]0,pi[ or ]-pi,0[)
	  // and look for limit(r*g)
	  glim=gr*subst(g,x,gr*symbolic(at_exp,cst_i*t),false,contextptr);
	  if (!assume_t_in_ab(gt,0,cst_pi,true,true,contextptr))
	    return false;
	  gl=limit(glim,r,plus_inf,1,contextptr);
	} 
	if (is_zero(gl)){ // use upper half plan
	  res=0;
	  vecteur v=singular(geff,x,contextptr);
	  if (is_undef(v))
	    return false;
	  int s=v.size(),nresidue=0;
	  for (int i=0;i<s;++i){
	    if (is_real(v[i],contextptr)){
	      res=undef; // singularity on the real axis
	      *logptr(contextptr) << gettext("Warning: pole at ") << v[i] << endl;
	      _purge(gt,contextptr);
	      return false;
	    }
	    if (is_positive(im(v[i],contextptr),contextptr)){
	      nresidue++;
	      res=res+residue(geff,x,v[i],contextptr);
	      if (is_undef(res)) return false;
	    }
	  }
	  if (s && !nresidue) // e.g. int(1/(x^2+a^2),x,0,inf)
	    return false;
	  res = normal(2*cst_i*cst_pi*res,contextptr);
	  _purge(gt,contextptr);
	  return true;
	}
	if (typeint==2){
	  gen gl2=limit(g,*x._IDNTptr,minus_inf,1,contextptr);
	  if (is_strictly_positive(gl,contextptr) && is_strictly_positive(gl2,contextptr))
	    res=plus_inf;
	  else {
	    if (is_strictly_positive(-gl,contextptr) && is_strictly_positive(-gl2,contextptr))
	      res=minus_inf;
	    else
	      res=undef;
	  }
	  _purge(gt,contextptr);
	  return true;
	}
	if (!assume_t_in_ab(gt,-cst_pi,0,true,true,contextptr))
	  return false;
	gl=limit(glim,r,plus_inf,1,contextptr);
	if (is_zero(gl)){ // use lower half plan
	  res=0;
	  vecteur v=singular(g,x,contextptr);
	  if (is_undef(v))
	    return false;
	  int s=v.size();
	  for (int i=0;i<s;++i){
	    if (is_real(v[i],contextptr)){
	      res=undef; // singularity on the real axis
	      _purge(gt,contextptr);
	      return true;
	    }
	    if (is_positive(-im(v[i],contextptr),contextptr)){
	      res=res+residue(g,x,v[i],contextptr);
	      if (is_undef(res)) return false;
	    }
	  }
	  res = normal(2*cst_i*cst_pi*res,contextptr);
	  _purge(gt,contextptr);
	  return true;
	}
#ifndef NO_STDEXCEPT
      } catch (...){
	return false;
      }
#endif
    }
    return false;
  }

  // if true put int(g,x=a..b) into res
  bool intgab(const gen & g0,const gen & x,const gen & a,const gen & b,gen & res,GIAC_CONTEXT){
    if (is_zero(g0)){
      res=zero;
      return true;
    }
    if (a==unsigned_inf || b==unsigned_inf){
      *logptr(contextptr) << gettext("Please use +infinity or -infinity since infinity is unsigned") << endl;
      return false;
    }
    if (is_strictly_greater(a,b,contextptr)){
      bool bo=intgab(g0,x,b,a,res,contextptr);
      res=-res;
      return bo;
    }
    if (equalposcomp(lidnt(a),x) || equalposcomp(lidnt(b),x))
      return false;
    // detect Dirac
    vecteur v=lop(g0,at_Dirac);
    if (!v.empty()){
      gen A,B,a0,b0;
      identificateur t(" tsumab");
      gen h=quotesubst(g0,v.front(),t,contextptr);
      if (!is_linear_wrt(h,t,A,B,contextptr))
	return false;
      gen heav=v.front()._SYMBptr->feuille;
      if (heav.type==_VECT && heav._VECTptr->size()==2 && heav._VECTptr->back().type==_INT_ ){
	int diracorder=heav._VECTptr->back().val;
	if (diracorder<0){
	  *logptr(contextptr) << gettext("Negative second Dirac argument") << endl;
	  return false;
	}
	A=derive(A,x,diracorder,contextptr);
	if (is_undef(A))
	  return false;
	if (diracorder%2)
	  A=-A;
	heav=heav._VECTptr->front();
      }
      if (!is_linear_wrt(heav,x,a0,b0,contextptr) || is_zero(a0))
	return false;
      if (!intgab(B,x,a,b,res,contextptr))
	return false;
      gen c=-b0/a0;
      if (ck_is_greater(c,a,contextptr) && ck_is_greater(b,c,contextptr))
	res += quotesubst(A,x,c,contextptr);
      else
	*logptr(contextptr) << gettext("Warning, Dirac function outside summation interval") << endl;
      return true;
    }
    if (a==b){
      res=0;
      return true;
    }
    gen g=hyp2exp(g0,contextptr);
    vecteur lvarg = lvar(g);
    bool rational = lvarg==vecteur(1,x);
    if (!rational){
      int s1=nvars_depend_x(loptab(g,sincostan_tab),x);
      // rewrite cos/sin/tan if more than 1 available, 
      // do not rewrite atan/asin/acos
      g=tsimplify_noexpln(g,s1,0,contextptr); 
    }
    // FIXME should check integrability at -/+inf
    if (a==minus_inf){
      if (b==plus_inf){
	int ieo=is_even_odd(g,x,contextptr);
	if (ieo==2){
	  res=0;
	  return true;
	}
	if (intgab_r(g,x,a,b,rational,res,contextptr))
	  return true;
	if (ieo==1){
	  // simplify g on 0..inf
	  assumesymbolic(symb_superieur_egal(x,0),0,contextptr);
	  gen g1=eval(g,1,contextptr);
	  _purge(x,contextptr);
	  if (g1!=eval(g,1,contextptr)){
	    res=2*_integrate(makesequence(g1,x,0,plus_inf),contextptr);
	    return true;
	  }
	}
	return false;
      }
      // subst x by x+b, check parity: even ->0 , odd -> 1/2 int(-inf,+inf)
      gen gb=subst(g,x,x+b,false,contextptr);
      int eo=is_even_odd(gb,x,contextptr);
      if (eo==1){
	if ( (rational && intgab_ratfrac(gb,x,res,contextptr)) ||
	     intgab(gb,x,a,plus_inf,res,contextptr) ){
	  if (!is_inf(res))
	    res=ratnormal(res/2);
	  return true;
	}
      }
      if (eo==2){
	res=0;
	return true;
      }
      vecteur v;
      rlvarx(gb,x,v);
      int vs=v.size();
      for (int i=0;i<vs;++i){
	if (v[i].is_symb_of_sommet(at_ln)){
	  gen f=v[i]._SYMBptr->feuille,a,b;
	  // if f is a*x make the change of var x=-exp(t)
	  if (is_linear_wrt(f,x,a,b,contextptr) && is_zero(b)){
	    vecteur vin=makevecteur(v[i],x);
	    vecteur vout=makevecteur(ln(-a,contextptr)+x,-exp(x,contextptr));
	    gb=quotesubst(gb,vin,vout,contextptr)*exp(x,contextptr);
	    return intgab(gb,x,minus_inf,plus_inf,res,contextptr);
	  }
	}
      }
      return false;
    }
    if (b==plus_inf){
      gen ga=subst(g,x,x+a,false,contextptr);
      int eo=is_even_odd(ga,x,contextptr);
      if (eo==1){ 
	if ( (rational && intgab_ratfrac(ga,x,res,contextptr)) ||
	     intgab(ga,x,minus_inf,plus_inf,res,contextptr) ){
	  if (!is_inf(res)) 
	    res=ratnormal(res/2);
	  return true;
	}
      }
      vecteur v;
      rlvarx(ga,x,v);
      int vs=v.size();
      for (int i=0;i<vs;++i){
	if (v[i].is_symb_of_sommet(at_ln)){
	  gen f=v[i]._SYMBptr->feuille,a,b;
	  // if f is a*x make the change of var x=exp(t)
	  if (is_linear_wrt(f,x,a,b,contextptr) && is_zero(b)){
	    vecteur vin=makevecteur(v[i],x);
	    vecteur vout=makevecteur(ln(a,contextptr)+x,exp(x,contextptr));
	    ga=quotesubst(ga,vin,vout,contextptr)*exp(x,contextptr);
	    return intgab(ga,x,minus_inf,plus_inf,res,contextptr);
	  }
	}
      }
      return false;
    }
    gen gab=subst(g0,x,b,false,contextptr)-subst(g0,x,a,false,contextptr);
    gen gabd;
    if (!has_evalf(gab,gabd,1,contextptr) || is_zero(gabd))
      gab=simplify(gab,contextptr);
    gen gm=subst(g0,x,b,false,contextptr)+subst(g0,x,a,false,contextptr);
    if (!has_evalf(gm,gabd,1,contextptr) || is_zero(gabd))
      gm=simplify(gm,contextptr);
    int eo=0;
    if (is_zero(gab) || is_zero(gm) ){
      gm=subst(g,x,x+(a+b)/2,false,contextptr);
      eo=is_even_odd(gm,x,contextptr);
    }
    if (eo==2){
#if 0 // set to 1 if you want to check for singularities before returning 0
      vecteur sp=find_singularities(g,*x._IDNTptr,false,contextptr);
      for (int i=0;i<sp.size();++i){
	if (is_greater(sp[i],a,contextptr) && is_greater(b,sp[i],contextptr))
	  return false;
      }
#endif
      res=0;
      return true;
    }
    // check periodicity
    gm=gab;
    if (is_zero(gm)){
      // do it only if g0 depends on potentially periodical functions exp/sin/cos/tan
      vecteur vx=lvarx(g0,x);
      for (unsigned i=0;i<vx.size();++i){
	if (vx[i].type!=_SYMB || (vx[i]._SYMBptr->sommet!=at_exp && vx[i]._SYMBptr->sommet!=at_sin && vx[i]._SYMBptr->sommet!=at_cos && vx[i]._SYMBptr->sommet!=at_tan))
	  gm=1;
      }
      if (is_zero(gm)){
	gm=subst(g0,x,x+(b-a),false,contextptr);
	gm=simplify(gm-g0,contextptr);
      }
    }
#ifndef NO_STDEXCEPT
    try {
#endif
      if (is_zero(gm)){
	// try to rewrite g as a function of exp(2*i*pi*x/(b-a))
	g=_lin(trig2exp(g0,contextptr),contextptr);
	vecteur v;
	rlvarx(g,x,v);
	sort(v.begin(),v.end(),islesscomplexthanf);
	int i,s=v.size();
	if (s>=2){
	  gen v0,alpha,beta,alphacur,betacur,gof,periode,periodecur;
	  for (i=0;i<s;++i){
	    if (v[i].is_symb_of_sommet(at_exp)){
	      v0=v[i];
	      gen v0arg=v0._SYMBptr->feuille;
	      if (is_linear_wrt(v0arg,x,alphacur,betacur,contextptr) && is_integer( (periodecur=normal(alphacur*(b-a)/cst_two_pi/cst_i,contextptr)) )){
		periode=gcd(periode,periodecur);
	      }
	    }
	  }
	  if (!is_zero(periode)){
	    alpha=normal(periode*cst_two_pi/(b-a)*cst_i,contextptr);
	    if (is_zero(re(alpha,contextptr))){
	      beta=normal(betacur*alpha/alphacur,contextptr);
	      gen radius=exp(re(beta,contextptr),contextptr);
	      // vO=exp(alpha*x+beta) -> x=(ln(v0)-beta)/alpha
	      vecteur vin=makevecteur(x);
	      vecteur vout=makevecteur((ln(x,contextptr)-beta)/alpha);
	      gof=quotesubst(g,vin,vout,contextptr);
	      gof=_exp2pow(gof/x,contextptr);
	      gof=recursive_normal(gof,contextptr);
	      // Sucess! Now integration of gof on the unit circle using residues
	      *logptr(contextptr) << gettext("Searching int of ") << gof << gettext(" where ") << x << gettext(" is on the unit circle, using residues") << endl;
	      vecteur w=singular(gof,x,contextptr);
	      if (is_undef(w))
		return false;
	      int s=w.size();
	      gen somme_residues=0;
	      for (int i=0;i<s;++i){
		gen wabs=normal(w[i]*conj(w[i],contextptr),contextptr);
		gen tmpresidue=residue(gof,x,w[i],contextptr);
		if (is_undef(tmpresidue))
		  return false;
		if ((wabs==radius) 
		    // && !is_zero(tmpresidue) // commented otherwise int(1/cos(x)^2,x,0,pi) returns 0
		    ){
		  res = unsigned_inf;
		  return true;
		}
		if (is_strictly_greater(radius,wabs,contextptr)){
		  somme_residues = somme_residues + residue(gof,x,w[i],contextptr);
		  if (is_undef(somme_residues))
		    return false;
		}
	      }
	      res = ratnormal(normal(periode*cst_two_pi/alpha*cst_i,contextptr)*somme_residues); 
	      return true;
	    } // end if(is_zero(re(alpha)))
	  }
	}
      }
#ifndef NO_STDEXCEPT
    } catch (std::runtime_error & ){
    }
#endif
    return false;
  }

  static gen quotesubstcheck(const gen & g,const gen & x,const gen & i,const vecteur & v,GIAC_CONTEXT){
    if (!equalposcomp(v,i))
      return quotesubst(g,x,i,contextptr);
    return 0;
  }

  // from csturm.cc
  vecteur crationalroot(polynome & p,bool complexe);

  // check whether P has only integer roots
  bool is_admissible_poly(const polynome & P,int & deg,polynome & lcoeff,vecteur & roots,GIAC_CONTEXT){
    lcoeff=Tfirstcoeff(P);
    index_t degs=P.degree();
    deg=degs[0];
    for (unsigned int i=1;i<degs.size();++i){
      if (degs[i])
	return false;
    }
    polynome PP=poly12polynome(polynome2poly1(P));
    polynome P1=PP.derivative();
    if (gcd(PP,P1).degree(0)>0)
      return false;
    roots.clear();
    if (deg<1)
      return true;
    roots=crationalroot(PP,false);
    roots=*_sort(roots,contextptr)._VECTptr;
    if (int(roots.size())!=deg)
      return false;
    return true;
  }

  // tmp1=sum_k a_k x^k, find sum_k a_k/s_k x^k
  // where s_x=product(k-decals[i],i) and decals[i] is rationnal
  // if decals[i] is an integer, multiply by x^(-1-decals[i]), int
  // and mult by x^decals[i]
  static bool in_sumab_int(gen & tmp1,const gen & gx,const vecteur & decals,const gen & lcoeff,GIAC_CONTEXT){
    int nstep=decals.size();
    gen coeff=lcoeff;
    gen remains;
    for (int i=0;i<nstep;++i){
      if (decals[i].type==_FRAC){
	gen n=decals[i]._FRACptr->num;
	gen d=decals[i]._FRACptr->den;
	// coeff*(k-n/d)=coeff/d*(d*k-n)
	coeff = coeff/d;
	// sum a_k/(d*k-n)*gx^k
	// set gx=X^d : sum_ a_k/(d*k-n)*X^(d*k)
	tmp1=subst(tmp1,gx,pow(gx,d,contextptr),false,contextptr);
	tmp1=tmp1*pow(gx,-1-n,contextptr);
	tmp1=integrate_id_rem(tmp1,gx,remains,contextptr);
	if (is_undef(tmp1)) return false;
	tmp1=tmp1-limit(tmp1,*gx._IDNTptr,0,1,contextptr);
	tmp1=ratnormal(tmp1*pow(gx,n,contextptr));
	tmp1=ratnormal(subst(tmp1,gx,pow(gx,inv(d,contextptr),contextptr),false,contextptr));
      }
      else {
	tmp1=tmp1*pow(gx,-1-decals[i],contextptr);
	tmp1=integrate_id_rem(tmp1,gx,remains,contextptr);
	if (is_undef(tmp1)) return false;
	tmp1=tmp1-limit(tmp1,*gx._IDNTptr,0,1,contextptr);
	tmp1=tmp1*pow(gx,decals[i],contextptr);
      }
      if (!is_zero(remains))
	return false;
    }
    tmp1=tmp1/coeff;
    return true;
    // do_lnabs(b,contextptr);
  }

  static bool sumab_int(gen & tmp1,const gen & gx,const vecteur & decals,const gen & lcoeff,GIAC_CONTEXT){
    bool b=do_lnabs(contextptr);
    do_lnabs(false,contextptr);
    // bool c=complex_mode(contextptr);
    // complex_mode(true,contextptr);
    bool bres=in_sumab_int(tmp1,gx,decals,lcoeff,contextptr);
    do_lnabs(b,contextptr);
    // complex_mode(c,contextptr);
    return bres;
  }

  static bool sumab_ps(const polynome & Q,const polynome & R,const vecteur & v,const gen & a,const gen & x,const gen & g,bool est_reel,const polynome & p,const polynome & s,gen & res,GIAC_CONTEXT){
    // p corresponds to derivation, s to integration
    // cerr << "p=" << p << " s=" << s << " Q=" << Q << " R=" << R << endl;
    // Q must be independant of x
    // If R is independant of x we use the geometric series
    // If R=x-integer the exponential (must change bounds by integer)
    // If R=2x(2x+1) sinh/cosh etc.
    if (Q.degree(0)==0){
      // count "integrations" step in s
      int intstep;
      vecteur decals;
      polynome lcoeffs;
      if (is_admissible_poly(s,intstep,lcoeffs,decals,contextptr)){
	gen lcoeff=r2e(lcoeffs,v,contextptr);
	identificateur idx(" sumw"); // identificateur idx(" x"); // 
	gen gx(idx);
	if (!assume_t_in_ab(gx,0,1,true,true,contextptr))
	  return false;
	// R must be the product of degree(R) consecutive terms
	int r=R.degree(0);
	if (r==1){
	  vecteur Rv=iroots(R);
	  if (Rv.size()!=1){
	    _purge(gx,contextptr);
	    return false;
	  }
	  gen R0=Rv[0];
	  if (is_strictly_greater(R0,a,contextptr)){
	    res=undef;
	    _purge(gx,contextptr);
	    return true;
	  }
	  // Q=Q/R.coord.front().value;
	  index_t ind=R.coord.front().index.iref();
	  ind[0]=0;
	  gen Qg=r2e(Q,v,contextptr)/r2e(polynome(monomial<gen>(R.coord.front().value,ind)),v,contextptr);
	  // (g|x=a)*s(a)/p(a)/Q^(a-R0)/(a-R0)!*sum(p(n)/s(n)*Q^(n-R0)/(n-R0)!,n=a..inf);
	  // first compute sum(p(n)*Q^(n-R0)/(n-R0)!,n=R0..inf)
	  // = sum(p(n+R0)*Q^(n)/n!,n=0..inf)
	  int d=p.degree(0);
	  gen Pg=r2e(p,v,contextptr);
	  vecteur vx(d+1),vy(d+1);
	  for (int i=0;i<=d;++i){
	    vx[i]=i;
	    vy[i]=ratnormal(subst(Pg,x,R0+i,false,contextptr));
	  }
	  vecteur w=divided_differences(vx,vy);
	  // p(n+R0)=w[0]+w[1]*n+w[2]*n*(n-1)+...
	  // hence the sum is exp(Q)*(w[0]+w[1]*Q+...)
	  reverse(w.begin(),w.end());
	  gen tmp1=symb_horner(w,gx)*exp(gx,contextptr),remains;
	  // substract sum(p(n)*Q^(n-R0)/(n-R0)!,n=R0..a-1)
	  for (int n=R0.val;n<a.val;++n){
	    tmp1 -= subst(Pg,x,n,false,contextptr)*pow(Qg,n-R0.val)/factorial(n-R0.val);
	  }
	  // Integration step
	  decals = subvecteur(decals,vecteur(decals.size(),R0));
	  if (sumab_int(tmp1,gx,decals,lcoeff,contextptr)){
	    gen coeffa=limit(g*r2e(s,v,contextptr)/r2e(p,v,contextptr),*x._IDNTptr,a,1,contextptr);
	    res=limit(coeffa*tmp1,*gx._IDNTptr,Qg,1,contextptr)/pow(Qg,a-R0,contextptr);
	    if (est_reel)
	      res=re(res,contextptr);
	    res=ratnormal(res);
	    _purge(gx,contextptr);
	    return true;
	  }
	}
	if (r>=2){
	  polynome Rc=lgcd(R);
	  vecteur Rv=polynome2poly1(R/Rc,1);
	  // Rv should be a multiple of (r*x-R0)*(r*x-(R0+1))*...
	  // -Rv[1]/Rv[0]= sum of roots = r*R0 + sum(j,j=0..r-1)
	  // R0 = -Rv[1]/Rv[0] - (r-1)/2
	  gen R0=-Rv[1]/Rv[0]-gen(r-1)/gen(2);
	  if (R0.type!=_INT_){
	    _purge(gx,contextptr);
	    return false;
	  }
	  // check that Rv = cst*product(r*x-(R0+j),j=0..r-1)
	  vecteur test(1,1);
	  for (int j=0;j<r;++j){
	    test=operator_times(test,makevecteur(r,-(R0+j)),0);
	  }
	  if (Rv[0]*test!=test[0]*Rv){
	    _purge(gx,contextptr);
	    return false;
	  }
	  int r0=R0.val;
	  if (r0>r*a.val){
	    res=undef;
	    _purge(gx,contextptr);
	    return true;
	  }
	  Rc=Rv[0]/pow(gen(r),r)*Rc;
	  gen Qg=r2e(Q,v,contextptr)/r2e(Rc,v,contextptr);
	  if (r==2)
	    Qg=sqrt(Qg,contextptr);
	  else
	    Qg=pow(Qg,inv(gen(r),contextptr),contextptr);
	  // (g|x=a)*s(a)/p(a)/Qg^(r*a-R0)/(r*a-R0)!*sum(p(n)/s(n)*Qg^(r*n-R0)/(r*n-R0)!,n=a..inf);
	  gen coeffa=limit(g*r2e(s,v,contextptr)/r2e(p,v,contextptr)/pow(Qg,r*a-R0,contextptr)/factorial(r*a.val-r0),*x._IDNTptr,a,1,contextptr);
	  // Set k=r*n-R0 and compute sum((p/s)((k+R0)/r)*X^k/k!,k=r*a-R0..inf)
	  int d=p.degree(0);
	  gen Pg=r2e(p,v,contextptr);
	  vecteur vx(d+1),vy(d+1);
	  for (int i=0;i<=d;++i){
	    vx[i]=i;
	    vy[i]=ratnormal(subst(Pg,x,(i+R0)/r,false,contextptr));
	  }
	  vecteur w=divided_differences(vx,vy);
	  reverse(w.begin(),w.end());
	  gen tmp=symb_horner(w,gx)*exp(gx,contextptr),remains;
	  // substract sum(...,k=0..r*a-R0-1)
	  for (int k=0;k<r*a.val-r0;++k){
	    tmp -= subst(Pg,x,(k+R0)/r,false,contextptr)*pow(Qg,k)/factorial(k);
	  }
	  // then keep terms which are = -R0 mod r = N
	  // for example if r=2 and R0 even, keep even terms
	  // that is (f(X)+f(-X))/2
	  // more generally take 
	  // 1/r*sum(f(X*exp(2i pi*k/r))*exp(-2i pi*k*N/r),k=0..r-1)
	  int N= -r0 % r;
	  gen tmp1=0,tmpadd;
	  for (int k=0;k<r;++k){
	    tmpadd = subst(tmp,gx,gx*exp(2*k*cst_i*cst_pi/r,contextptr),false,contextptr)*exp(-2*k*N*cst_i*cst_pi/r,contextptr);
	    tmp1 += tmpadd;
	  }
	  tmp1=tmp1/r;
	  // Integration step
	  if (sumab_int(tmp1,gx,decals,lcoeff,contextptr)){
	    res=limit(coeffa*tmp1,*gx._IDNTptr,Qg,1,contextptr);
	    if (est_reel)
	      res=re(res,contextptr);
	    res=ratnormal(res);
	    _purge(gx,contextptr);
	    return true;
	  }
	}
	if (!r){
	  // (g|x=a)*s(a)/p(a)/Q^a*sum(p(n)/s(n)*X^n)|X=Q, will work 
	  // first compute sum(p(n)*X^n)
	  // then multiply by X^sdecal and integrate intstep times
	  // then subst X by Q
	  gen tmp=r2e(p,v,contextptr)*pow(gx,x,contextptr);
	  gen remains,tmp1=sum(tmp,x,remains,contextptr);
	  if (!is_zero(remains) || is_undef(tmp1)){
	    *logptr(contextptr) << gettext("Unable to sum ")+remains.print(contextptr) << endl;
	    return false;
	  }
	  tmp1=-subst(tmp1,x,a,false,contextptr);
	  // bool b=do_lnabs(contextptr);
	  // do_lnabs(false,contextptr);
	  if (sumab_int(tmp1,gx,decals,lcoeff,contextptr)){
	    gen Qg=r2e(Q,v,contextptr)/r2e(R,v,contextptr);
	    gen coeffa=limit(g*r2e(s,v,contextptr)/r2e(p,v,contextptr),*x._IDNTptr,a,1,contextptr);
	    res=limit(coeffa*tmp1,*gx._IDNTptr,Qg,1,contextptr)/pow(Qg,a,contextptr);
	    if (est_reel)
	      res=re(res,contextptr);
	    res=ratnormal(res);
	    _purge(gx,contextptr);
	    return true;
	  }
	}
      }
    }
    return false;
  }

  static bool in_sumab(const gen & g,const gen & x,const gen & a_orig,const gen & b_orig,gen & res,bool testi,bool dopartfrac,GIAC_CONTEXT){
    if (x.type!=_IDNT || angle_radian(contextptr)==false)
      return false;
    if (is_zero(g)){
      res=zero;
      return true;
    }
    if (dopartfrac){
      gen gp=_partfrac(gen(makevecteur(g,*x._IDNTptr),_SEQ__VECT),contextptr);
      if (gp.is_symb_of_sommet(at_plus) && gp._SYMBptr->feuille.type==_VECT){
	vecteur vp=*gp._SYMBptr->feuille._VECTptr;
	res=0;
	int i=0;
	for (;i<vp.size();++i){
	  gen resi;
	  if (!in_sumab(vp[i],x,a_orig,b_orig,resi,testi,false,contextptr))
	    break;
	  res += resi;
	}
	if (i==vp.size())
	  return true;
      }
    }
    vecteur v=lvarx(g,x);
    v=loptab(v,sincostan_tab);
    bool est_reel=testi?!has_i(g):true; 
    if (!v.empty()){
      gen w=trig2exp(v,contextptr);
      vecteur vexp;
      lin(subst(g,v,*w._VECTptr,true,contextptr),vexp,contextptr);
      const_iterateur it=vexp.begin(),itend=vexp.end();
      for (;it!=itend;){
	gen coeff=*it,tmp;
	++it; // it -> on the arg of the exp that must be linear
	gen axb=coeff*symbolic(at_exp,*it);
	++it;
	if (!sumab(axb,*x._IDNTptr,a_orig,b_orig,tmp,!est_reel,contextptr)){
	  return false;
	}
	res += tmp;
      }
      return true;
    }
    v.clear();
    polynome p,q,r;
    gen a(a_orig),b(b_orig);
    if (!est_reel || complex_mode(contextptr)){
      bool b=complex_mode(contextptr);
      complex_mode(contextptr)=false;
      gen reg(g),img(0),reres,imres;
      if (!est_reel){
	reg=re(g,contextptr),img=im(g,contextptr);
      }
      if (!in_sumab(reg,x,a_orig,b_orig,reres,false,false,contextptr) || !in_sumab(img,x,a_orig,b_orig,imres,false,false,contextptr)){
	complex_mode(contextptr)=b;
	return false;
      }
      complex_mode(contextptr)=b;
      res=reres+cst_i*imres;
      return true;
    }
    bool Hyper=is_hypergeometric(g,*x._IDNTptr,v,p,q,r,contextptr);
    if (Hyper){
      // Newton binomial: sum_{x=a}^{b} comb(b-a,x-a)*p^x = (p+1)^(b-a)*p^a
      // n=b-a
      // comb(n,x+1-a)*p^(x+1)/comb(n,x-a)/p^x 
      //   = p*(x-a)!*(n-x+a)!/(x+1-a)!/(n-x-1+a)!=p*(n-x+a)/(x+1-a)
      // q=(-qa)*(n-x+a)=(-qa)*(b-x), r=ra*(x-a) -> -q/qa+r/ra=n
      // can be generallized with j-unitroots to
      // sum_{x=a}^{b} comb(b-a,j*x-j*a)*p^x
      // 
      gen Q=r2sym(q,v,contextptr),R=r2sym(r,v,contextptr),Qa,Qb,Ra,Rb;
      gen n=b-a;
      if (is_linear_wrt(Q,x,Qa,Qb,contextptr) && is_linear_wrt(R,x,Ra,Rb,contextptr)){
	// Q/R=(Qa*x+Qb)/(Ra*x+Rb)=(-Qa/Ra)*(-x-Qb/Qa)/(x+Rb/Ra)
	gen trueb=normal(-Qb/Qa,contextptr);
	gen truea=normal(-Rb/Ra,contextptr);
	gen truen=normal(trueb-truea,contextptr);
	gen diffa=normal(a-truea,contextptr);
	gen diffb=normal(b-trueb,contextptr);
	if (diffa.type==_INT_ && diffb.type==_INT_){
	  gen P=r2sym(p,v,contextptr);
	  if (p.lexsorted_degree()==0){
	    res = simplify(pow(P*(-Qa/Ra)+1,truen,contextptr)*subst(g,x,truea,false,contextptr),contextptr);
	    if (diffb.val>0){ // b>trueb: add sum(g,x,trueb+1,b-1)
	      for (int i=0;i<diffb.val;++i)
		res += simplify(subst(g,x,trueb+1+i,false,contextptr),contextptr);
	    }
	    else { // b<=trueb substract sum(g,x,b+1,trueb)
	      for (int i=0;i<-diffb.val;++i)
		res -= simplify(subst(g,x,b+1+i,false,contextptr),contextptr); 
	    }
	    if (diffa.val>0){ // a>truea : substract sum(g,x,truea,a-1)
	      for (int i=0;i<diffa.val;++i)
		res -= simplify(subst(g,x,truea+i,false,contextptr),contextptr);
	    }
	    else { // a<=truea: add sum(g,x,a,truea-1)
	      for (int i=0;i<-diffa.val;++i)
		res += simplify(subst(g,x,a+i,false,contextptr),contextptr);
	    }
	    return true;
	  }
	  // recompute the sum by repeted division, first divide g by P
	  // then divide P by R, by R-1, by R-2, etc.
	  gen gsurP=ratnormal(g/P),prod=1;
	  while (!is_zero(P)){
	    gen tmp=_quorem(gen(makevecteur(P,R,x),_SEQ__VECT),contextptr),tmpres;
	    if (tmp.type!=_VECT || tmp._VECTptr->size()!=2)
	      return false; // setsizeerr();
	    if (!in_sumab(gsurP*prod,x,a_orig,b_orig,tmpres,false,false,contextptr))
	      return false;
	    res += tmp._VECTptr->back()*tmpres;
	    prod = prod*R;
	    R=subst(R,x,x-1,false,contextptr); // R=R-1;
	    P=tmp._VECTptr->front();
	  }
	  return true;
	}
      }
    }
    if (!is_inf(a) && !is_inf(b))
      return false;
    if (b==plus_inf && a.type==_INT_ && Hyper){
      polynome s,Q,R;
      // limit of q/r at infinity must be 0
      gen test=r2e(q,v,contextptr)/r2e(r,v,contextptr);
      test=test*test-1;
      if (is_zero(test)){
	res=_limit(gen(makevecteur(g,x,plus_inf),_SEQ__VECT),contextptr);
	if (!is_zero(res)){
	  return is_inf(res);
	}
      }
      if (is_strictly_positive(test,contextptr)){
	res=_limit(gen(makevecteur(g,x,plus_inf),_SEQ__VECT),contextptr);
	return true;
      }
      r=taylor(r,1); 
      // r(x)/q(x)=s(x+1)/s(x)*R(x)/Q(x+1)
      AB2PQR(r,q,s,R,Q);
      R=taylor(R,-1);
      simplify(p,s);
      // IMPROVE: make a partial fraction decomposition of p(n)/s(n)
      // [could also make ln return ln(1-x) instead of ln(x-1)]
      return sumab_ps(Q,R,v,a,x,g,est_reel,p,s,res,contextptr);
    }
    gen A,B,P;
    int type=is_meromorphic(g,x,A,B,P,contextptr);
    int even=is_even_odd(g,x,contextptr);
    bool complete=(a==minus_inf && b==plus_inf);
    if (type==2){
      if (!is_zero(limit(g,*x._IDNTptr,plus_inf,0,contextptr))){
	res=undef;
	return true;
      }
      if ( complete || even==1){
	if (!complete){
	  if (a==minus_inf){
	    a=-b;
	    b=plus_inf;
	  }
	  if (a.type!=_INT_)
	    return false;
	}
	int ai=a.val;
	// find the value of int(cos(pi*x)/sin(pi*x)*g,circle(0,R))
	vecteur v=singular(g,x,contextptr);
	if (is_undef(v))
	  return false;
	gen g2=g*cst_pi*symb_cos(cst_pi*x)/symb_sin(cst_pi*x);
	// if root is integer it must not be inside a..b
	// the sum of residues of v + sum(g,n=-inf..inf, n not in v) will be 0
	gen somme_residus;
	int vs=v.size();
	gen correc=0;
	for (int i=0;i<vs;++i){
	  gen vi=v[i];
	  if (is_integer(vi)){
	    if (is_greater(vi,a,contextptr)){
	      res=undef;
	      return true;
	    }
	  }
	  somme_residus += residue(g2,x,vi,contextptr);
	  if (is_undef(somme_residus))
	    return false;
	}
	if (complete)
	  res=-somme_residus;
	else { // even fraction
	  if (ai>0){
	    for (int i=1;i<ai;++i)
	      correc -= quotesubstcheck(g,x,i,v,contextptr);
	  }
	  if (ai<=0){
	    for (int i=0;i>=ai;--i)
	      correc += quotesubstcheck(g,x,i,v,contextptr);
	  }
	  res=(-quotesubstcheck(g,x,0,v,contextptr)-somme_residus)/2;
	}
	res=correc+res;
	res=recursive_normal(trig2exp(res,contextptr),contextptr);
	return true;
      }
    }
    return false;
  }

  // if true put int(g,x=a..b) into res
  // a or b must be +/-infinity and a<b
  bool sumab(const gen & g,const gen & x,const gen & a_orig,const gen & b_orig,gen & res,bool testi,GIAC_CONTEXT){
    if (x.type!=_IDNT)
      return false;
    if (g.is_symb_of_sommet(at_plus)){
      vecteur argv=gen2vecteur(g._SYMBptr->feuille);
      int args=argv.size(),i;
      res=0;
      gen tmp;
      for (i=0;i<args;++i){
	if (!sumab(argv[i],x,a_orig,b_orig,tmp,testi,contextptr))
	  break;
	res += tmp;
      }
      if (i==args)
	return true;
    }
    // detect when
    vecteur v=lop(g,at_when);
    if (!v.empty()){
      gen A,B,a,b;
      identificateur t(" tsumab");
      gen h=quotesubst(g,v.front(),t,contextptr);
      if (!is_linear_wrt(h,t,A,B,contextptr))
	return false;
      gen heav=v.front()._SYMBptr->feuille;
      if (heav.type==_VECT && heav._VECTptr->size()==3){
	B=B+A*heav._VECTptr->back();
	A=A*((*heav._VECTptr)[1]-heav._VECTptr->back());
	heav=heav._VECTptr->front();
      }
      else return false; // setsizeerr();
      if (!is_linear_wrt(heav,x,a,b,contextptr) || is_zero(a))
	return false;
      if (!sumab(B,x,a_orig,b_orig,res,testi,contextptr))
	return false;
      gen c=-b/a;
      if (ck_is_greater(c,a_orig,contextptr) && ck_is_greater(b_orig,c,contextptr))
	res += quotesubst(A,x,c,contextptr);
      else
	*logptr(contextptr) << gettext("Warning, Dirac function outside summation interval") << endl;
      return true;
    }
    // detect Heaviside 
    v=lop(g,at_Heaviside);
    if (v.empty())
      return in_sumab(g,x,a_orig,b_orig,res,testi,true /* do partfrac */,contextptr);
    gen A,B,a,b;
    identificateur t(" tsumab");
    gen h=quotesubst(g,v.front(),t,contextptr);
    if (!is_linear_wrt(h,t,A,B,contextptr))
      return false;
    // A*Heaviside()+B
    gen heav=v.front()._SYMBptr->feuille;
    if (!is_linear_wrt(heav,x,a,b,contextptr) || is_zero(a))
      return false;
    if (!sumab(B,x,a_orig,b_orig,res,testi,contextptr))
      return false;
    // for A additional condition a*x+b>=0 : if a>0 x>=-b/a else x<=-b/a
    gen c=-b/a;
    gen newa,newb;
    if (ck_is_positive(a,contextptr)){
      if (ck_is_greater(a_orig,c,contextptr)){
	newa=a_orig; newb=b_orig;
      }
      else {
	if (ck_is_greater(b_orig,c,contextptr)){
	  newa=_ceil(c,contextptr);
	  newb=b_orig;
	}
	else
	  return true;
      }
    }
    else {
      if (ck_is_greater(a_orig,c,contextptr))
	return true;
      if (ck_is_greater(b_orig,c,contextptr)){
	newa=a_orig;
	newb=_floor(c,contextptr);
      }
      else {
	newa=a_orig;
	newb=b_orig;
      }
    }
    gen sumA;
    if (!sumab(A,x,newa,newb,sumA,testi,contextptr))
      return false;
    res += sumA;
    return true;
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

