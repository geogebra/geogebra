/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c misc.cc -DHAVE_CONFIG_H -DIN_GIAC" -*- */
#include "giacPCH.h"
/*
 *  Copyright (C) 2001, 2007 R. De Graeve, B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#include <fstream>
#include <string>
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
#include "input_parser.h"
#include "input_lexer.h"
#include "maple.h"
#include "quater.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  gen _scalar_product(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    return scalar_product(v[0],v[1],contextptr);
  }    
  static const char _scalar_product_s []="scalar_product";
  static define_unary_function_eval (__scalar_product,&_scalar_product,_scalar_product_s);
  define_unary_function_ptr5( at_scalar_product ,alias_at_scalar_product,&__scalar_product,0,true);

  static const char _dot_s []="dot";
  static define_unary_function_eval (__dot,&_scalar_product,_dot_s);
  define_unary_function_ptr5( at_dot ,alias_at_dot,&__dot,0,true);

  gen _compare(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    return v[0].islesscomplexthan(v[1]);
  }    
  static const char _compare_s []="compare";
  static define_unary_function_eval (__compare,&_compare,_compare_s);
  define_unary_function_ptr5( at_compare ,alias_at_compare,&__compare,0,true);

  gen _preval(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_preval,args);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<3)
      return gentoofewargs("");
    gen f(v[0]),x,a,b;
    a=v[1];
    b=v[2];
    if (s==3)
      x=vx_var;
    else 
      x=v[3];
    if (x.type!=_IDNT)
      return gentypeerr(contextptr);
    return preval(f,x,a,b,contextptr);
  }    
  static const char _preval_s []="preval";
  static define_unary_function_eval (__preval,&_preval,_preval_s);
  define_unary_function_ptr5( at_preval ,alias_at_preval,&__preval,0,true);

  vecteur divided_differences(const vecteur & x,const vecteur & y){
    vecteur res(y);
    int s=x.size();
    for (int k=1;k<s;++k){
      for (int j=s-1;j>=k;--j){
	res[j]=(res[j]-res[j-1])/(x[j]-x[j-k]);
      }
    }
    return res;
  }
  gen _lagrange(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_lagrange,args);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<2)
      return gentoofewargs("");
    gen v0(v[0]),v1(v[1]),x=vx_var;
    if (ckmatrix(v0) && v0._VECTptr->size()==2){
      x=v1;
      v1=v0._VECTptr->back();
      v0=v0._VECTptr->front();
    }
    if (s>=3)
      x=v[2];
    if ( (v0.type!=_VECT) || (v1.type!=_VECT) )
      return gensizeerr(contextptr);
    vecteur & vx =*v0._VECTptr;
    vecteur & vy=*v1._VECTptr;
    s=vx.size();
    if (!s || vy.size()!=unsigned(s))
      return gendimerr(contextptr);
    // Using divided difference instead of the theoretical formula
    if (x.type==_VECT && x._VECTptr->empty()){
      vecteur res;
      interpolate(vx,vy,res,0);
      return res;
    }
    vecteur w=divided_differences(vx,vy);
    gen pi(1),res(w[s-1]);
    for (int i=s-2;i>=0;--i){
      res = res*(x-vx[i])+w[i];
    }
    return res;
    /*
    gen res(zero);
    for (int i=0;i<s;++i){
      gen pix(plus_one),pix0(plus_one),x0(vx[i]);
      for (int j=0;j<s;++j){
	if (j==i)
	  continue;
	pix=pix*(x-vx[j]);
	pix0=pix0*(x0-vx[j]);
      }
      res=res+vy[i]*rdiv(pix,pix0);
    }
    return res;
    */
  }    
  static const char _lagrange_s []="lagrange";
  static define_unary_function_eval (__lagrange,&_lagrange,_lagrange_s);
  define_unary_function_ptr5( at_lagrange ,alias_at_lagrange,&__lagrange,0,true);

  gen _reorder(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_reorder,args);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<2)
      return gentoofewargs("");
    gen e(v[0]),l(v[1]);
    if (l.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur w(*l._VECTptr);
    lvar(e,w);
    e=e2r(e,w,contextptr);
    return r2e(e,w,contextptr);
  }    
  static const char _reorder_s []="reorder";
  static define_unary_function_eval (__reorder,&_reorder,_reorder_s);
  define_unary_function_ptr5( at_reorder ,alias_at_reorder,&__reorder,0,true);

  gen _adjoint_matrix(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_adjoint_matrix,args);
    matrice mr(*args._VECTptr);
    if (!is_squarematrix(mr))
      return gensizeerr(contextptr);
    matrice m_adj;
    vecteur p_car;
    p_car=mpcar(mr,m_adj,true,true,contextptr);
    return makevecteur(p_car,m_adj);
  }    
  static const char _adjoint_matrix_s []="adjoint_matrix";
  static define_unary_function_eval (__adjoint_matrix,&_adjoint_matrix,_adjoint_matrix_s);
  define_unary_function_ptr5( at_adjoint_matrix ,alias_at_adjoint_matrix,&__adjoint_matrix,0,true);

  gen _equal2diff(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return apply(args,equal2diff);
  }    
  static const char _equal2diff_s []="equal2diff";
  static define_unary_function_eval (__equal2diff,&_equal2diff,_equal2diff_s);
  define_unary_function_ptr5( at_equal2diff ,alias_at_equal2diff,&__equal2diff,0,true);

  static gen equal2list(const gen & arg){
    if ( (arg.type!=_SYMB) || (arg._SYMBptr->sommet!=at_equal))
      return makevecteur(arg,zero);
    return arg._SYMBptr->feuille;
  }
  gen _equal2list(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return apply(args,equal2list);
  }    
  static const char _equal2list_s []="equal2list";
  static define_unary_function_eval (__equal2list,&_equal2list,_equal2list_s);
  define_unary_function_ptr5( at_equal2list ,alias_at_equal2list,&__equal2list,0,true);

  gen _rank(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gentypeerr(contextptr); // return symbolic(at_adjoint_matrix,args);
    matrice mr(*args._VECTptr);
    if (!ckmatrix(mr))
      return gensizeerr(contextptr);
    mr=mrref(mr,contextptr);
    int r=mr.size();
    for (;r;--r){
      if (!is_zero(mr[r-1]))
	break;
    }
    return r;
  }    
  static const char _rank_s []="rank";
  static define_unary_function_eval (__rank,&_rank,_rank_s);
  define_unary_function_ptr5( at_rank ,alias_at_rank,&__rank,0,true);

  gen _sec(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return inv(cos(args,contextptr),contextptr);
  }    
  static const char _sec_s []="sec";
  static define_unary_function_eval (__sec,&_sec,_sec_s);
  define_unary_function_ptr5( at_sec ,alias_at_sec,&__sec,0,true);

  gen _csc(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return inv(sin(args,contextptr),contextptr);
  }    
  static const char _csc_s []="csc";
  static define_unary_function_eval (__csc,&_csc,_csc_s);
  define_unary_function_ptr5( at_csc ,alias_at_csc,&__csc,0,true);

  gen _cot(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return rdiv(cos(args,contextptr),sin(args,contextptr),contextptr);
  }    
  static const char _cot_s []="cot";
  static define_unary_function_eval (__cot,&_cot,_cot_s);
  define_unary_function_ptr5( at_cot ,alias_at_cot,&__cot,0,true);

  gen _asec(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return acos(inv(args,contextptr),contextptr);
  }    
  static const char _asec_s []="asec";
  static define_unary_function_eval (__asec,&_asec,_asec_s);
  define_unary_function_ptr5( at_asec ,alias_at_asec,&__asec,0,true);

  gen _acsc(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return asin(inv(args,contextptr),contextptr);
  }    
  static const char _acsc_s []="acsc";
  static define_unary_function_eval (__acsc,&_acsc,_acsc_s);
  define_unary_function_ptr5( at_acsc ,alias_at_acsc,&__acsc,0,true);

  gen _acot(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return atan(inv(args,contextptr),contextptr);
  }    
  static const char _acot_s []="acot";
  static define_unary_function_eval (__acot,&_acot,_acot_s);
  define_unary_function_ptr5( at_acot ,alias_at_acot,&__acot,0,true);

  // args=[u'*v,v] or [[F,u'*v],v] -> [F+u*v,-u*v']
  // a third argument would be the integration var
  // if v=0 returns F+integrate(u'*v,x)
  gen _ibpu(const gen & args,GIAC_CONTEXT) {
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return symbolic(at_ibpu,args);
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
    gen u,v(w[1]),uprimev,F;
    if (w.front().type==_VECT){
      vecteur & ww=*w.front()._VECTptr;
      if (ww.size()!=2)
	return gensizeerr(contextptr);
      F=ww.front();
      uprimev=ww.back();
    }
    else 
      uprimev=w.front();
    if (is_zero(v) || is_one(v)){
      gen tmp=integrate_gen(uprimev,x,contextptr);
      if (is_undef(tmp)) return tmp;
      if (bound)
	tmp=preval(tmp,x,a,b,contextptr);
      return tmp+F;
    }
    gen uprime(normal(rdiv(uprimev,v,contextptr),contextptr));
    u=integrate_gen(uprime,x,contextptr);
    if (is_undef(u)) return u;
    F += u*v;
    if (bound)
      F = preval(F,x,a,b,contextptr);
    return makevecteur(F,normal(-u*derive(v,x,contextptr),contextptr));
  }
  static const char _ibpu_s []="ibpu";
  static define_unary_function_eval (__ibpu,&_ibpu,_ibpu_s);
  define_unary_function_ptr5( at_ibpu ,alias_at_ibpu,&__ibpu,0,true);

  gen _changebase(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_changebase,args);
    vecteur & v=*args._VECTptr;
    if (v.size()!=2)
      return gentypeerr(contextptr);
    gen a=v.front(),p=v.back();
    if (!is_squarematrix(p))
      return gensizeerr(contextptr);
    return minv(*p._VECTptr,contextptr)*a*p;
  }    
  static const char _changebase_s []="changebase";
  static define_unary_function_eval (__changebase,&_changebase,_changebase_s);
  define_unary_function_ptr5( at_changebase ,alias_at_changebase,&__changebase,0,true);

  static gen epsilon2zero(const gen & g,GIAC_CONTEXT){
    switch (g.type){
    case _DOUBLE_:
      if (fabs(g._DOUBLE_val)<epsilon(contextptr))
	return zero;
      else
	return g;
    case _CPLX:
      return epsilon2zero(re(g,contextptr),contextptr)+cst_i*epsilon2zero(im(g,contextptr),contextptr);
    case _SYMB:
      return symbolic(g._SYMBptr->sommet,epsilon2zero(g._SYMBptr->feuille,contextptr));
    case _VECT:
      return apply(g,epsilon2zero,contextptr);
    default:
      return g;
    }
  }
  gen _epsilon2zero(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return epsilon2zero(args,contextptr);
  }    
  static const char _epsilon2zero_s []="epsilon2zero";
  static define_unary_function_eval_quoted (__epsilon2zero,&_epsilon2zero,_epsilon2zero_s);
  define_unary_function_ptr5( at_epsilon2zero ,alias_at_epsilon2zero,&__epsilon2zero,_QUOTE_ARGUMENTS,true);

  gen _suppress(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_suppress,args);
    vecteur & v=*args._VECTptr;
    if (v.size()!=2)
      return gentypeerr(contextptr);
    gen l=v.front(),i=v.back();
    int ii=0;
    if (i.type==_INT_ )
      ii=i.val-(xcas_mode(contextptr)!=0);
    if (l.type==_STRNG){
      string res;
      string & s=*l._STRNGptr;
      int n=s.size();
      if (i.type==_INT_ && ii>=0 && ii<n)
	res=s.substr(0,ii)+s.substr(ii+1,n-ii-1);
      if (i.type==_STRNG){
	string & remove=*i._STRNGptr;
	int removen=remove.size();
	for (int j=0;j<n;++j){
	  int k=remove.find(s[j]);
	  if (k<0 || k>=removen)
	    res += s[j];
	}
      }
      return string2gen(res,false);
    }
    if ( (l.type!=_VECT) || (i.type!=_INT_) )
      return gensizeerr(contextptr);
    const_iterateur it=l._VECTptr->begin(),itend=l._VECTptr->end();
    vecteur res;
    res.reserve(itend-it);
    for (int j=0;it!=itend;++it,++j){
      if (j!=ii)
	res.push_back(*it);
    }
    return gen(res,l.subtype);
  }    
  static const char _suppress_s []="suppress";
  static define_unary_function_eval (__suppress,&_suppress,_suppress_s);
  define_unary_function_ptr5( at_suppress ,alias_at_suppress,&__suppress,0,true);

  static int valuation(const polynome & p){
    if (p.coord.empty())
      return -1;
    return p.coord.back().index.front();
  }
  gen _valuation(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen p,x;
    if (args.type!=_VECT){
      x=vx_var;
      p=args;
    }
    else {
      vecteur & v=*args._VECTptr;
      int s=v.size();
      if (!s)
	return minus_inf;
      if ( (args.subtype==_POLY1__VECT) || (s!=2) || (v[1].type!=_IDNT) ){
	int j=s;
	for (;j;--j){
	  if (!is_zero(v[j-1]))
	    break;
	}
	return s-j;
      }
      x=v.back();
      p=v.front();
    }
    vecteur lv(1,x);
    lvar(p,lv);
    gen aa=e2r(p,lv,contextptr),aan,aad;
    if (is_zero(aa))
      return minus_inf;
    fxnd(aa,aan,aad);
    if ( (aad.type==_POLY) && (aad._POLYptr->lexsorted_degree() ) )
      return gensizeerr(contextptr);
    if (aan.type!=_POLY)
      return zero;
    int res=valuation(*aan._POLYptr);
    if (res==-1)
      return minus_inf;
    else
      return res;
  }    
  static const char _valuation_s []="valuation";
  static define_unary_function_eval (__valuation,&_valuation,_valuation_s);
  define_unary_function_ptr5( at_valuation ,alias_at_valuation,&__valuation,0,true);

  static const char _ldegree_s []="ldegree";
  static define_unary_function_eval (__ldegree,&_valuation,_ldegree_s);
  define_unary_function_ptr5( at_ldegree ,alias_at_ldegree,&__ldegree,0,true);

  gen _degree(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen p,x;
    if (args.type!=_VECT){
      p=args;
      if (calc_mode(contextptr)==1)
	x=ggb_var(p);
      else
	x=vx_var;
    }
    else {
      vecteur & v=*args._VECTptr;
      int s=v.size();
      if ( (args.subtype==_POLY1__VECT) || (s!=2) || (v[1].type!=_IDNT) )
	return s-1;
      x=v.back();
      p=v.front();
    }
    if (p.type==_POLY){
      if (x.type==_INT_ && x.val>=0 && x.val<p._POLYptr->dim)
	return p._POLYptr->degree(x.val);
      else {
	vecteur res(p._POLYptr->dim);
	index_t idx(p._POLYptr->degree());
	for (int i=0;i<p._POLYptr->dim;++i)
	  res[i]=idx[i];
	return res;
      }
    }
    vecteur lv(1,x);
    if (x.type==_VECT)
      lv=*x._VECTptr;
    lvar(p,lv);
    gen aa=e2r(p,lv,contextptr),aan,aad;
    if (is_zero(aa))
      return zero;
    fxnd(aa,aan,aad);
    int deg=0;
    if ( (aad.type==_POLY) && (aad._POLYptr->lexsorted_degree() ) )
      deg -= aad._POLYptr->lexsorted_degree();;
    if (aan.type!=_POLY)
      return deg;
    return deg+aan._POLYptr->lexsorted_degree();
  }    
  static const char _degree_s []="degree";
  static define_unary_function_eval (__degree,&_degree,_degree_s);
  define_unary_function_ptr5( at_degree ,alias_at_degree,&__degree,0,true);

  gen _lcoeff(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen x,p,order;
    int s=2;
    if (args.type!=_VECT){
      x=vx_var;
      p=args;
    }
    else {
      vecteur & v=*args._VECTptr;
      s=v.size();
      if (!s)
	return args;
      if ( (args.subtype!=_SEQ__VECT) || (s<2) )
	return v.front();
      x=v[1];
      p=v[0];
      if (s>2)
	order=v[2];
    }
    gen g=_e2r(makesequence(p,x),contextptr),n,d;
    fxnd(g,n,d);
    if (n.type!=_VECT){
      if (n.type==_POLY){
	polynome nlcoeff(*n._POLYptr);
	if (!nlcoeff.coord.empty()){
	  if (order.type==_INT_)
	    change_monomial_order(nlcoeff,order);
	  nlcoeff.coord.erase(nlcoeff.coord.begin()+1,nlcoeff.coord.end());
	}
	n=nlcoeff;
      }
      return _r2e(gen(makevecteur(n/d,x),_SEQ__VECT),contextptr);
    }
    return n._VECTptr->front()/d;
  }
  static const char _lcoeff_s []="lcoeff";
  static define_unary_function_eval (__lcoeff,&_lcoeff,_lcoeff_s);
  define_unary_function_ptr5( at_lcoeff ,alias_at_lcoeff,&__lcoeff,0,true);

  static gen tcoeff(const vecteur & v){
    int s=v.size();
    gen g;
    for (;s;--s){
      g=v[s-1];
      if (!is_zero(g))
	return g;
    }
    return zero;
  }
  gen _tcoeff(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen x,p;
    if (args.type!=_VECT){
      x=vx_var;
      p=args;
    }
    else {
      vecteur& v=*args._VECTptr;
      int s=v.size();
      if ( (args.subtype!=_SEQ__VECT) || (s!=2) || (v[1].type!=_IDNT) )
	return tcoeff(v);
      x=v[1];
      p=v[0];
    }
    gen g=_e2r(makesequence(p,x),contextptr),n,d;
    fxnd(g,n,d);
    if (n.type!=_VECT)
      return zero;
    return tcoeff(*n._VECTptr)/d;
  }
  static const char _tcoeff_s []="tcoeff";
  static define_unary_function_eval (__tcoeff,&_tcoeff,_tcoeff_s);
  define_unary_function_ptr5( at_tcoeff ,alias_at_tcoeff,&__tcoeff,0,true);

  static gen sqrfree(const gen & g,const vecteur & l,GIAC_CONTEXT){
    if (g.type!=_POLY)
      return r2sym(g,l,contextptr);
    factorization f(sqff(*g._POLYptr));
    factorization::const_iterator it=f.begin(),itend=f.end();
    gen res(plus_one);
    for (;it!=itend;++it)
      res=res*pow(r2e(it->fact,l,contextptr),it->mult);
    return res;
  }
  static vecteur sqrfree(const gen & g,const vecteur & l,int mult,GIAC_CONTEXT){
    vecteur res;
    if (g.type!=_POLY){
      if (is_one(g))
	return res;
      return vecteur(1,makevecteur(r2sym(g,l,contextptr),mult));
    }
    factorization f(sqff(*g._POLYptr));
    factorization::const_iterator it=f.begin(),itend=f.end();
    for (;it!=itend;++it){
      const polynome & p=it->fact;
      gen pg=r2e(p,l,contextptr);
      if (!is_one(pg))
	res.push_back(makevecteur(pg,mult*it->mult));
    }
    return res;
  }
  gen _sqrfree(const gen & args_,GIAC_CONTEXT){
    gen args(args_);
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    bool factors=false;
    if (args.type==_VECT){
      vecteur argv=*args._VECTptr;
      if (!argv.empty() && argv.back()==at_factors){
	factors=true;
	argv.pop_back();
	if (argv.size()==1)
	  args=argv.front();
	else
	  args=gen(argv,args.subtype);
      }
    }
    if (args.type==_VECT) // fixme take care of factors
      return apply(args,_sqrfree,contextptr);
    if (args.type!=_SYMB)
      return factors?makevecteur(args,1):args;
    gen a,b;
    if (is_algebraic_program(args,a,b)) // fixme take care of factors
      return symbolic(at_program,makesequence(a,0,_sqrfree(b,contextptr)));
    vecteur l(alg_lvar(args));
    gen g=e2r(args,l,contextptr);
    if (g.type==_FRAC){
      fraction f=*g._FRACptr;
      if (factors)
	return mergevecteur(sqrfree(f.num,l,1,contextptr),sqrfree(f.den,l,-1,contextptr));
      return sqrfree(f.num,l,contextptr)/sqrfree(f.den,l,contextptr);
    }
    else {
      if (factors)
	return sqrfree(g,l,1,contextptr);
      return sqrfree(g,l,contextptr);
    }
  }
  static const char _sqrfree_s []="sqrfree";
  static define_unary_function_eval (__sqrfree,&_sqrfree,_sqrfree_s);
  define_unary_function_ptr5( at_sqrfree ,alias_at_sqrfree,&__sqrfree,0,true);

  gen _truncate(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen e(args);
    int n,s=1;
    vecteur w(1,vx_var);
    gen gn(5);
    if (args.type==_VECT){
      vecteur & v=*args._VECTptr;
      s=v.size();
      if (s==0)
	return gensizeerr(contextptr);
      e=v[0];
      if (s==3){
	w=gen2vecteur(v[1]);
	gn=v[2];
      }
      else {
	if (s==2)
	  gn=v[1];
      }
    }
    if (gn.type!=_INT_)
      return gensizeerr(contextptr);
    n=gn.val;
    int nvar=w.size(); // number of var w.r.t. which we truncate
    vecteur l(lop(e,at_order_size));
    vecteur lp(l.size(),zero);
    e=subst(e,l,lp,false,contextptr);
    // FIXME if l not empty, adjust order of truncation using arg of order_size
    lvar(e,w);
    e=e2r(e,w,contextptr);
    gen num,den;
    fxnd(e,num,den);
    if ( (den.type==_POLY) && (den._POLYptr->lexsorted_degree() ) )
      return gensizeerr(contextptr);
    if (num.type==_POLY){
      vector< monomial<gen> >::const_iterator it=num._POLYptr->coord.begin(),itend=num._POLYptr->coord.end();
      vector< monomial<gen> > res;
      for (;it!=itend;++it){
	index_t::const_iterator i=it->index.begin();
	int deg=0;
	for (int j=0;j<nvar;++j,++i)
	  deg=deg+(*i);
	if (deg<=n)
	  res.push_back(*it);
      }
      num._POLYptr->coord=res;
    }
    return r2e(rdiv(num,den,contextptr),w,contextptr);
  }    
  static const char _truncate_s []="truncate";
  static define_unary_function_eval (__truncate,&_truncate,_truncate_s);
  define_unary_function_ptr5( at_truncate ,alias_at_truncate,&__truncate,0,true);

  gen _canonical_form(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen p,x,a,b,c;
    if (is_algebraic_program(args,a,b))
      return symbolic(at_program,makesequence(a,0,_canonical_form(gen(makevecteur(b,a[0]),_SEQ__VECT),contextptr)));
    if (args.type!=_VECT){
      p=args;
      x=ggb_var(p);
    }
    else {
      vecteur & v=*args._VECTptr;
      if (v.size()!=2)
	return gentypeerr(contextptr);
      p=v.front();
      x=v.back();
    }
    if (x.type!=_IDNT)
      return gentypeerr(contextptr);
    if (!is_quadratic_wrt(p,x,a,b,c,contextptr))
      return gensizeerr(contextptr);
    if (is_zero(a))
      return b*x+c;
    // a*x^2+b*x+c -> a*(x+b/(2*a))^2+(b^2-4*a*c)/(4*a)
    return a*pow(x+b/(2*a),2)+(4*a*c-pow(b,2))/(4*a);
  }    
  static const char _canonical_form_s []="canonical_form";
  static define_unary_function_eval (__canonical_form,&_canonical_form,_canonical_form_s);
  define_unary_function_ptr5( at_canonical_form ,alias_at_canonical_form,&__canonical_form,0,true);

  gen _taux_accroissement(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen p,x,a,b,c;
    if (args.type!=_VECT || args._VECTptr->size()<3)
      return gensizeerr(contextptr);
    vecteur v = *args._VECTptr;
    if (is_algebraic_program(v.front(),a,b)){
      return _taux_accroissement(gen(makevecteur(b,a[0],v[1],v[2]),_SEQ__VECT),contextptr);
      // return symbolic(at_program,makevecteur(v[1],0,_taux_accroissement(gen(makevecteur(b,a[0],v[1],v[2]),_SEQ__VECT),contextptr)));
    }
    if (v.size()<4)
      v.insert(v.begin()+1,vx_var);
    if (v[1].type!=_IDNT)
      return gentypeerr(contextptr);
    return (subst(v.front(),v[1],v[3],false,contextptr)-subst(v.front(),v[1],v[2],false,contextptr))/(v[3]-v[2]);
  }    
  static const char _taux_accroissement_s []="taux_accroissement";
  static define_unary_function_eval (__taux_accroissement,&_taux_accroissement,_taux_accroissement_s);
  define_unary_function_ptr5( at_taux_accroissement ,alias_at_taux_accroissement,&__taux_accroissement,0,true);

  gen _fcoeff(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen x;
    vecteur p;
    if (args.type!=_VECT)
      return symbolic(at_fcoeff,args);
    vecteur & v=*args._VECTptr;
    if ( (v.size()!=2) || (v.front().type!=_VECT) ){
      p=v;
      x=vx_var;
    }
    else {
      p=*v.front()._VECTptr;
      x=v.back();
    }
    if (x.type!=_IDNT)
      return gentypeerr(contextptr);    
    const_iterateur it=p.begin(),itend=p.end();
    if ( (itend-it)%2 )
      return gensizeerr(contextptr);
    gen res(plus_one);
    for (;it!=itend;it+=2){
      res=res*pow(x-*it,*(it+1),contextptr);
    }
    return res;
  }    
  static const char _fcoeff_s []="fcoeff";
  static define_unary_function_eval (__fcoeff,&_fcoeff,_fcoeff_s);
  define_unary_function_ptr5( at_fcoeff ,alias_at_fcoeff,&__fcoeff,0,true);

  static void addfactors(const gen & p,const gen & x,int mult,vecteur & res,GIAC_CONTEXT){
    vecteur v=factors(p,x,contextptr);
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;){
      vecteur w=solve(*it,x,1,contextptr);
      ++it;
      int n=it->val;
      ++it;
      const_iterateur jt=w.begin(),jtend=w.end();
      for (;jt!=jtend;++jt){
	res.push_back(*jt);
	res.push_back(n*mult);
      }
    }
  }    

  gen _froot(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen p,x;
    if (args.type!=_VECT){
      x=vx_var;
      p=args;
    }
    else {
      vecteur & v=*args._VECTptr;
      if (v.size()!=2)
	return gensizeerr(contextptr);
      x=v.back();
      if (x.type!=_IDNT)
	return gensizeerr(gettext("2nd arg"));
      p=v.front();
    }
    vecteur lv(lvar(p));
    gen aa=e2r(p,lv,contextptr),aan,aad;
    fxnd(aa,aan,aad);
    vecteur res;
    addfactors(r2e(aan,lv,contextptr),x,1,res,contextptr);
    addfactors(r2e(aad,lv,contextptr),x,-1,res,contextptr);
    return res;
  }

  static const char _froot_s []="froot";
  static define_unary_function_eval (__froot,&_froot,_froot_s);
  define_unary_function_ptr5( at_froot ,alias_at_froot,&__froot,0,true);

  gen _roots(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen r=_froot(g,contextptr);
    if (r.type!=_VECT || (r._VECTptr->size() % 2) )
      return gensizeerr(contextptr);
    vecteur & v = *r._VECTptr;
    vecteur res;
    int s=v.size()/2;
    for (int i=0;i<s;++i){
      if (v[2*i+1].val>0)
	res.push_back(makevecteur(v[2*i],v[2*i+1]));
    }
    return res;
  }
  static const char _roots_s []="roots";
  static define_unary_function_eval (__roots,&_roots,_roots_s);
  define_unary_function_ptr5( at_roots ,alias_at_roots,&__roots,0,true);

  gen _divpc(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen p,q,x;
    if (args.type!=_VECT)
      return symbolic(at_divpc,args);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<3)
      return gensizeerr(contextptr);
    p=v.front();
    q=v[1];
    if (v[2].type!=_INT_)
      return gensizeerr(contextptr);
    if (s==3)
      x=vx_var;
    else 
      x=v.back();
    vecteur lv(1,x);
    lvar(p,lv);
    lvar(q,lv);
    gen aa=e2r(p,lv,contextptr),aan,aad;
    fxnd(aa,aan,aad);
    gen ba=e2r(q,lv,contextptr),ban,bad;
    fxnd(ba,ban,bad);
    if ( ( aad.type==_POLY && aad._POLYptr->lexsorted_degree())
	 || (bad.type==_POLY && bad._POLYptr->lexsorted_degree())
	 )
      return gensizeerr(contextptr);
    if (ban.type!=_POLY)
      return r2e(rdiv(aan*bad,ban*aad,contextptr),lv,contextptr);
    vecteur a;
    if (aan.type==_POLY)
      a=polynome2poly1(*aan._POLYptr,1);
    else
      a=vecteur(1,aan);
    vecteur b=polynome2poly1(*ban._POLYptr,1);
    if (is_zero(b.back()))
      divisionby0err(q);
    reverse(a.begin(),a.end());
    reverse(b.begin(),b.end());
    int n=b.size()-a.size()+v[2].val;
    for (int i=0;i<n;++i)
      a.push_back(zero);
    vecteur quo,rem;
    environment * env=new environment;
    DivRem(a,b,env,quo,rem);
    delete env;
    reverse(quo.begin(),quo.end());
    gen res(vecteur2polynome(quo,lv.size()));
    res=rdiv(res*bad,aad,contextptr);
    return r2e(res,lv,contextptr);
  }

  static const char _divpc_s []="divpc";
  static define_unary_function_eval (__divpc,&_divpc,_divpc_s);
  define_unary_function_ptr5( at_divpc ,alias_at_divpc,&__divpc,0,true);

  gen _ptayl(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen p,q,x;
    if (args.type!=_VECT)
      return symbolic(at_ptayl,args);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<2)
      return gensizeerr(contextptr);
    p=v.front();
    q=v[1];
    if (p.type==_VECT)
      return taylor(*p._VECTptr,q,0);
    if (s==2)
      x=vx_var;
    else 
      x=v.back();
    if (!is_zero(derive(q,x,contextptr)))
      return gensizeerr(contextptr);
    vecteur lv(1,x);
    lvar(p,lv);
    lvar(q,lv);
    gen aa=e2r(p,lv,contextptr),aan,aad;
    fxnd(aa,aan,aad);
    if ( ( (aad.type==_POLY)&&(aad._POLYptr->lexsorted_degree()) )
	 )
      return gensizeerr(contextptr);
    if (aan.type!=_POLY)
      return p;
    gen ba=e2r(q,vecteur(lv.begin()+1,lv.end()),contextptr);
    vecteur a(polynome2poly1(*aan._POLYptr,1));
    vecteur res=taylor(a,ba,0);
    return r2e(vecteur2polynome(res,lv.size()),lv,contextptr)/r2e(aad,lv,contextptr);
  }

  static const char _ptayl_s []="ptayl";
  static define_unary_function_eval (__ptayl,&_ptayl,_ptayl_s);
  define_unary_function_ptr5( at_ptayl ,alias_at_ptayl,&__ptayl,0,true);
  
  vecteur gen2continued_fraction(const gen & g,int n,GIAC_CONTEXT){
    // Compute a vector of size n+1 with last element=remainder
    vecteur res,remain;
    gen tmp(g),f;
    int i=0,j;
    for (;i<n;++i){
      if ( (j=equalposcomp(remain,tmp)) ){
	// int s=remain.size();
	res.push_back(vecteur(res.begin()+j-1,res.end()));
	return res;
      }
      else
	remain.push_back(tmp);
      f=_floor(tmp,0);
      res.push_back(f);
      if (is_zero(tmp-f))
	return res;
      tmp=normal(inv(tmp-f,contextptr),contextptr);
    }
    res.push_back(tmp);
    return res;
  }
  gen _dfc(const gen & g_orig,GIAC_CONTEXT){
    if ( g_orig.type==_STRNG && g_orig.subtype==-1) return  g_orig;
    gen g=g_orig;
    if (g.type==_FRAC){
      gen tmp=_floor(g,contextptr);
      vecteur res(1,tmp);
      g -= tmp;
      for (;!is_zero(g);){
	g = inv(g,contextptr);
	tmp = _floor(g,contextptr);
	res.push_back(tmp);
	g -=tmp;
      }
      return res;
    }
    double eps=epsilon(contextptr);
    if (g.type==_VECT && g._VECTptr->size()==2){
      if (g._VECTptr->back().type==_DOUBLE_){
	eps=g._VECTptr->back()._DOUBLE_val;
	g=evalf_double(g._VECTptr->front(),1,contextptr);
      }
      else {
	if (g._VECTptr->back().type==_INT_)
	  return gen2continued_fraction(g._VECTptr->front(),g._VECTptr->back().val,contextptr);
      }
    }
    g=evalf_double(g,1,contextptr);
    if (g.type!=_DOUBLE_)
      return gensizeerr(contextptr);
    return vector_int_2_vecteur(float2continued_frac(g._DOUBLE_val,eps));
  }
  static const char _dfc_s []="dfc";
  static define_unary_function_eval (__dfc,&_dfc,_dfc_s);
  define_unary_function_ptr5( at_dfc ,alias_at_dfc,&__dfc,0,true);

  gen _dfc2f(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->empty())
      return gensizeerr(contextptr);
    vecteur & v (*g._VECTptr);
    gen res(v.back());
    if (v.back().type==_VECT){
      // represent a quadratic x=[... x], find equation
      identificateur tmp(" x");
      gen eq(tmp);
      const_iterateur it=v.back()._VECTptr->end()-1,itend=v.back()._VECTptr->begin()-1;
      for (;it!=itend;--it)
	eq=inv(eq,contextptr)+(*it);
      vecteur w=solve(eq-tmp,tmp,0,contextptr);
      gen ws=_sort(w,0);
      if (ws.type!=_VECT || ws._VECTptr->empty())
	return gensizeerr(contextptr);
      res=ws._VECTptr->back();
    }
    for (;;){
      v.pop_back();
      if (v.empty())
	return res;
      res=inv(res,contextptr);
      res=res+v.back();
    }
    // return continued_frac2gen(vecteur_2_vector_int(*g._VECTptr),nan(),epsilon);
  }
  static const char _dfc2f_s []="dfc2f";
  static define_unary_function_eval (__dfc2f,&_dfc2f,_dfc2f_s);
  define_unary_function_ptr5( at_dfc2f ,alias_at_dfc2f,&__dfc2f,0,true);

  gen float2rational(double d_orig,double eps,GIAC_CONTEXT){
    double d=d_orig;
    if (d<0)
      return -float2rational(-d,eps,contextptr);
    if (d>RAND_MAX)
      return d;    // reconstruct
    vector<int> v(float2continued_frac(d,eps));
    return continued_frac2gen(v,d_orig,eps,contextptr);
  }
  gen _float2rational(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    switch (g.type){
    case _DOUBLE_:
      return float2rational(g._DOUBLE_val,epsilon(contextptr),contextptr);
    case _REAL:
      return float2rational(evalf_double(g,1,contextptr)._DOUBLE_val,epsilon(contextptr),contextptr);
    case _CPLX:
      return _float2rational(re(g,contextptr),contextptr)+cst_i*_float2rational(im(g,contextptr),contextptr);
    case _SYMB:
      return symbolic(g._SYMBptr->sommet,_float2rational(g._SYMBptr->feuille,contextptr));
    case _VECT:
      return apply(g,_float2rational,contextptr);
    default:
      return g;
    }
  }
  static const char _float2rational_s []="float2rational";
  static define_unary_function_eval (__float2rational,&_float2rational,_float2rational_s);
  define_unary_function_ptr5( at_float2rational ,alias_at_float2rational,&__float2rational,0,true);

  gen _gramschmidt(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return symbolic(at_gramschmidt,g);
    vecteur & v(*g._VECTptr);
    if (ckmatrix(v))
      return gramschmidt(v,true,contextptr);
    if (v.size()==2){
      gen lvect=v[0];
      gen scalaire=v[1];
      if (lvect.type!=_VECT)
	return gensizeerr(contextptr);
      vecteur lv=*lvect._VECTptr;
      int s=lv.size();
      if (!s)
	return lv;
      vecteur sc(1,scalaire(gen(makevecteur(lv[0],lv[0]),_SEQ__VECT),contextptr));
      for (int i=1;i<s;++i){
	gen cl;
	for (int j=0;j<i;++j)
	  cl=cl+rdiv(scalaire(gen(makevecteur(lv[i],lv[j]),_SEQ__VECT),contextptr),sc[j],contextptr)*lv[j];
	lv[i]=lv[i]-cl;
	sc.push_back(scalaire(gen(makevecteur(lv[i],lv[i]),_SEQ__VECT),contextptr));
      }
      for (int i=0;i<s;++i)
	lv[i]=rdiv(lv[i],sqrt(sc[i],contextptr),contextptr);
      return lv;
    }
    return gensizeerr(contextptr);
  }
  static const char _gramschmidt_s []="gramschmidt";
  static define_unary_function_eval (__gramschmidt,&_gramschmidt,_gramschmidt_s);
  define_unary_function_ptr5( at_gramschmidt ,alias_at_gramschmidt,&__gramschmidt,0,true);

  void aplatir(const matrice & m,vecteur & v,bool full){
    int s=m.size();
    if (!full){
      v.clear();
      v.reserve(2*s);
    }
    const_iterateur it=m.begin(),itend=m.end(),jt,jtend;
    for (;it!=itend;++it){
      if (it->type!=_VECT || it->subtype==_GGB__VECT)
	v.push_back(*it);
      else {
	if (full){
	  aplatir(*it->_VECTptr,v,full);
	  continue;
	}
	jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
	for (;jt!=jtend;++jt)
	  v.push_back(*jt);
      }
    }
  }

  static gen pmin(const matrice & m,GIAC_CONTEXT){
    int s=m.size();
    matrice mpow(midn(s));
    matrice res;
    vecteur v;
    for (int i=0;i<=s;++i){
      aplatir(mpow,v);
      v.push_back(pow(vx_var,i));
      res.push_back(v);
      mpow=mmult(mpow,m);
    }
    matrice r;
    gen det;
    mrref(res,r,v,det,0,s+1,0,s*s,
	  /* fullreduction */0,1,true,1,0,
	  contextptr);
    // find 1st line with zeros (except in the last col)
    const_iterateur it=r.begin(),itend=r.end();
    for (;it!=itend;++it){
      if (is_zero(vecteur(it->_VECTptr->begin(),it->_VECTptr->end()-1)))
	break;
    }
    if (it==itend)
      return gensizeerr(contextptr);
    gen t= _e2r(makesequence(it->_VECTptr->back(),vx_var),contextptr);
    if (t.type==_VECT)
      return t/lgcd(*t._VECTptr);
    else
      return t;
  }
  gen _pmin(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (is_squarematrix(g)){
      matrice &m =*g._VECTptr;
      vecteur w;
      if (proba_epsilon(contextptr) && probabilistic_pmin(m,w,true,contextptr))
	return w;
      return pmin(m,contextptr);
    }
    if (is_integer(g) || g.type==_MOD)
      return makevecteur(1,g);
    if (g.type==_USER){
      if (galois_field * gf=dynamic_cast<galois_field *>(g._USERptr)){
	if (gf->a.type!=_VECT || gf->P.type!=_VECT || !is_integer(gf->p))
	  return gensizeerr("Bad GF element");
	environment env;
	env.modulo=gf->p;
	env.pn=env.modulo;
	env.moduloon=true;
	// compute 1,a,a^2,...,a^n in lines then transpose and find ker
	int n=gf->P._VECTptr->size()-1;
	vecteur & A=*gf->a._VECTptr;
	vecteur current(1,1),suivant,temp;
	matrice m(n+1);
	m[0]=vecteur(n);
	m[0]._VECTptr->front()=1; 
	// put constant term in first column (row) to avoid cancellation problems
	for (int i=1;i<=n;++i){
	  mulmodpoly(current,A,&env,temp);
	  suivant=operator_mod(temp,*gf->P._VECTptr,&env);
	  m[i]=new ref_vecteur(n);
	  for (unsigned j=0;j<suivant.size();++j){
	    (*m[i]._VECTptr)[j]=makemod(suivant[suivant.size()-1-j],gf->p);
	  }
	  swap(current,suivant);
	}
	vecteur noyau;
	m=mtran(m);
	mker(m,noyau,0,contextptr);
	if (noyau.empty() || noyau.front().type!=_VECT)
	  return gensizeerr("Internal error, no relation found");
	temp=*noyau.front()._VECTptr;
	for (;!temp.empty() && is_zero(temp.back());)
	  temp.pop_back();
	reverse(temp.begin(),temp.end());
	mulmodpoly(temp,inv(temp.front(),contextptr),0,temp);
	return gen(temp,_POLY1__VECT);
      }
    }
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return symbolic(at_pmin,g);
    vecteur & v(*g._VECTptr);
    if (!is_squarematrix(v.front()))
      return gensizeerr(contextptr);
    matrice &m=*v.front()._VECTptr;
    // probabilistic minimal polynomial
    vecteur w;
    if (proba_epsilon(contextptr) &&probabilistic_pmin(m,w,true,contextptr))
      return symb_horner(w,v.back());
    else
      return _r2e(gen(makevecteur(pmin(m,contextptr),v.back()),_SEQ__VECT),contextptr);
  }
  static const char _pmin_s []="pmin";
  static define_unary_function_eval (__pmin,&_pmin,_pmin_s);
  define_unary_function_ptr5( at_pmin ,alias_at_pmin,&__pmin,0,true);

  // a faire: vpotential, signtab
  gen _potential(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if ( (g.type!=_VECT) || (g._VECTptr->size()!=2) )
      return symbolic(at_potential,g);
    vecteur v(plotpreprocess(g,contextptr));
    if (is_undef(v))
      return v;
    gen f=v[0];
    gen x=v[1];
    if ( (f.type!=_VECT) || (x.type!=_VECT) )
      return gensizeerr(contextptr);
    vecteur & fv=*f._VECTptr;
    vecteur & xv=*x._VECTptr;
    int s=fv.size();
    if (unsigned(s)!=xv.size())
      return gendimerr(contextptr);
    for (int i=0;i<s;++i){
      for (int j=i+1;j<s;++j){
	if (!is_zero(simplify(derive(fv[i],xv[j],contextptr)-derive(fv[j],xv[i],contextptr),contextptr)))
	  return gensizeerr(gettext("Not a potential"));
      }
    }
    gen res;
    for (int i=0;i<s;++i){
      res=res+integrate_gen(simplify(fv[i]-derive(res,xv[i],contextptr),contextptr),xv[i],contextptr);
    }
    return res;
  }
  static const char _potential_s []="potential";
  static define_unary_function_eval_quoted (__potential,&_potential,_potential_s);
  define_unary_function_ptr5( at_potential ,alias_at_potential,&__potential,_QUOTE_ARGUMENTS,true);

  gen _vpotential(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if ( (g.type!=_VECT) || (g._VECTptr->size()!=2) )
      return symbolic(at_vpotential,g);
    vecteur v(plotpreprocess(g,contextptr));
    if (is_undef(v))
      return v;
    gen f=v[0];
    gen x=v[1];
    if ( (f.type!=_VECT) || (x.type!=_VECT) )
      return gensizeerr(contextptr);
    vecteur & fv=*f._VECTptr;
    vecteur & xv=*x._VECTptr;
    unsigned int s=fv.size();
    if ( (s!=3) || (s!=xv.size()) )
      return gendimerr(contextptr);
    if (!is_zero(simplify(_divergence(g,contextptr),contextptr)))
      return gensizeerr(gettext("Not a vector potential"));
    vecteur res(3);
    /* return A0=0, A1=int[B_2,x0], A2=-int[B_1,x0]+F(x1,x2)
     * where F=int[B0+d_2[int[B_2,x0]]+d_1[int[B_1,x0]],x1]
     * F does not depend on x0 since divergence[B]=0 */
    res[1]=integrate_gen(fv[2],xv[0],contextptr);
    res[2]=integrate_gen(fv[1],xv[0],contextptr);
    gen F=simplify(fv[0]+derive(res[1],xv[2],contextptr)+derive(res[2],xv[1],contextptr),contextptr);
    F=integrate_gen(F,xv[1],contextptr);
    res[2]=F-res[2];
    return res;
  }
  static const char _vpotential_s []="vpotential";
  static define_unary_function_eval_quoted (__vpotential,&_vpotential,_vpotential_s);
  define_unary_function_ptr5( at_vpotential ,alias_at_vpotential,&__vpotential,_QUOTE_ARGUMENTS,true);

  gen _poly2symb(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT && g.subtype!=_SEQ__VECT)
      return _r2e(gen(makevecteur(g,vx_var),_SEQ__VECT),contextptr);      
    return _r2e(g,contextptr);
  }
  static const char _poly2symb_s []="poly2symb";
  static define_unary_function_eval (__poly2symb,&_poly2symb,_poly2symb_s);
  define_unary_function_ptr5( at_poly2symb ,alias_at_poly2symb,&__poly2symb,0,true);

  gen _symb2poly(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return _e2r(g,contextptr);
  }
  static const char _symb2poly_s []="symb2poly";
  static define_unary_function_eval (__symb2poly,&_symb2poly,_symb2poly_s);
  define_unary_function_ptr5( at_symb2poly ,alias_at_symb2poly,&__symb2poly,0,true);

  gen _exp2trig(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return _sincos(g,contextptr);
  }
  static const char _exp2trig_s []="exp2trig";
  static define_unary_function_eval (__exp2trig,&_exp2trig,_exp2trig_s);
  define_unary_function_ptr5( at_exp2trig ,alias_at_exp2trig,&__exp2trig,0,true);

  gen _nrows(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!ckmatrix(g))
      return gensizeerr(contextptr);
    return int(g._VECTptr->size());
  }
  static const char _nrows_s []="nrows";
  static define_unary_function_eval (__nrows,&_nrows,_nrows_s);
  define_unary_function_ptr5( at_nrows ,alias_at_nrows,&__nrows,0,true);

  gen _ncols(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!ckmatrix(g))
      return gensizeerr(contextptr);
    if (g._VECTptr->empty())
      return zero;
    return int(g._VECTptr->front()._VECTptr->size());
  }
  static const char _ncols_s []="ncols";
  static define_unary_function_eval (__ncols,&_ncols,_ncols_s);
  define_unary_function_ptr5( at_ncols ,alias_at_ncols,&__ncols,0,true);

  gen _l2norm(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=remove_at_pnt(g0);
    if (g.type==_VECT && g.subtype==_VECTOR__VECT && g._VECTptr->size()==2)
      g=g._VECTptr->back()-g._VECTptr->front();
    if (g.type!=_VECT)
      return abs(g,contextptr);
    vecteur v;
    if (ckmatrix(g))
      aplatir(*g._VECTptr,v);
    else
      v=*g._VECTptr;
    return l2norm(v,contextptr);
  }
  static const char _l2norm_s []="l2norm";
  static define_unary_function_eval (__l2norm,&_l2norm,_l2norm_s);
  define_unary_function_ptr5( at_l2norm ,alias_at_l2norm,&__l2norm,0,true);

  static const char _norm_s []="norm";
  static define_unary_function_eval (__norm,&_l2norm,_norm_s);
  define_unary_function_ptr5( at_norm ,alias_at_norm,&__norm,0,true);

  gen _normalize(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    return a/_l2norm(a,contextptr);
  }
  static const char _normalize_s []="normalize";
  static define_unary_function_eval (__normalize,&_normalize,_normalize_s);
  define_unary_function_ptr5( at_normalize ,alias_at_normalize,&__normalize,0,true);

  static const char _randmatrix_s []="randmatrix";
  static define_unary_function_eval (__randmatrix,&_ranm,_randmatrix_s);
  define_unary_function_ptr5( at_randmatrix ,alias_at_randmatrix,&__randmatrix,0,true);

  extern const unary_function_ptr * const  at_lgcd;
  gen _lgcd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_lgcd,args);
    return lgcd(*args._VECTptr);
  }
  static const char _lgcd_s []="lgcd";
  static define_unary_function_eval (__lgcd,&_lgcd,_lgcd_s);
  define_unary_function_ptr5( at_lgcd ,alias_at_lgcd,&__lgcd,0,true);

  // synonyms
  gen _float(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return evalf(g,1,contextptr);
  }
  static const char _float_s []="float";
  static define_unary_function_eval (__float,&_float,_float_s);
  define_unary_function_ptr5( at_float ,alias_at_float,&__float,0,true);

  gen _hold(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return g;
  }
  static const char _hold_s []="hold";
  static define_unary_function_eval_quoted (__hold,&_hold,_hold_s);
  define_unary_function_ptr5( at_hold ,alias_at_hold,&__hold,_QUOTE_ARGUMENTS,true);

  gen _eigenvals(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!is_squarematrix(g))
      return gendimerr(contextptr);
    bool b=complex_mode(contextptr);
    complex_mode(true,contextptr);
    matrice m;
    vecteur d;
    if (!egv(*g._VECTptr,m,d,contextptr,false,false,true))
      *logptr(contextptr) << gettext("Low accuracy") << endl;
    complex_mode(b,contextptr);
    return gen(d,_SEQ__VECT);
  }
  static const char _eigenvals_s []="eigenvals";
  static define_unary_function_eval (__eigenvals,&_eigenvals,_eigenvals_s);
  define_unary_function_ptr5( at_eigenvals ,alias_at_eigenvals,&__eigenvals,0,true);

  static const char _giackernel_s []="kernel";
  static define_unary_function_eval (__giackernel,&_ker,_giackernel_s);
  define_unary_function_ptr5( at_kernel ,alias_at_kernel,&__giackernel,0,true);

  gen _eigenvects(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    bool b=complex_mode(contextptr);
    complex_mode(true,contextptr);
    gen res=_egv(g,contextptr);
    complex_mode(b,contextptr);
    return res;
  }
  static const char _eigenvects_s []="eigenvects";
  static define_unary_function_eval (__eigenvects,&_eigenvects,_eigenvects_s);
  define_unary_function_ptr5( at_eigenvects ,alias_at_eigenvects,&__eigenvects,0,true);

  static const char _eigenvalues_s []="eigenvalues";
  static define_unary_function_eval (__eigenvalues,&_eigenvals,_eigenvalues_s);
  define_unary_function_ptr5( at_eigenvalues ,alias_at_eigenvalues,&__eigenvalues,0,true);

  static const char _charpoly_s []="charpoly";
  static define_unary_function_eval (__charpoly,&giac::_pcar,_charpoly_s);
  define_unary_function_ptr5( at_charpoly ,alias_at_charpoly,&__charpoly,0,true);

  static const char _eigenvectors_s []="eigenvectors";
  static define_unary_function_eval (__eigenvectors,&_eigenvects,_eigenvectors_s);
  define_unary_function_ptr5( at_eigenvectors ,alias_at_eigenvectors,&__eigenvectors,0,true);

  static const char _rowdim_s []="rowdim";
  static define_unary_function_eval (__rowdim,&_nrows,_rowdim_s);
  define_unary_function_ptr5( at_rowdim ,alias_at_rowdim,&__rowdim,0,true);

  static const char _coldim_s []="coldim";
  static define_unary_function_eval (__coldim,&_ncols,_coldim_s);
  define_unary_function_ptr5( at_coldim ,alias_at_coldim,&__coldim,0,true);

  static const char _multiply_s []="multiply";
  static define_unary_function_eval (__multiply,&_prod,_multiply_s);
  define_unary_function_ptr5( at_multiply ,alias_at_multiply,&__multiply,0,true);

  /* Maple inert forms */
  gen _Gcd(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_gcd,g);
  }
  static const char _Gcd_s []="Gcd";
  static define_unary_function_eval (__Gcd,&_Gcd,_Gcd_s);
  define_unary_function_ptr5( at_Gcd ,alias_at_Gcd,&__Gcd,0,true);

  gen _Gcdex(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_gcdex,g);
  }
  static const char _Gcdex_s []="Gcdex";
  static define_unary_function_eval (__Gcdex,&_Gcdex,_Gcdex_s);
  define_unary_function_ptr5( at_Gcdex ,alias_at_Gcdex,&__Gcdex,0,true);

  gen _Factor(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_factor,g);
  }
  static const char _Factor_s []="Factor";
  static define_unary_function_eval (__Factor,&_Factor,_Factor_s);
  define_unary_function_ptr5( at_Factor ,alias_at_Factor,&__Factor,0,true);

  gen _Rref(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_rref,g);
  }
  static const char _Rref_s []="Rref";
  static define_unary_function_eval (__Rref,&_Rref,_Rref_s);
  define_unary_function_ptr5( at_Rref ,alias_at_Rref,&__Rref,0,true);

  gen _Rank(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_rank,g);
  }
  static const char _Rank_s []="Rank";
  static define_unary_function_eval (__Rank,&_Rank,_Rank_s);
  define_unary_function_ptr5( at_Rank ,alias_at_Rank,&__Rank,0,true);

  gen _Det(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_det,g);
  }
  static const char _Det_s []="Det";
  static define_unary_function_eval (__Det,&_Det,_Det_s);
  define_unary_function_ptr5( at_Det ,alias_at_Det,&__Det,0,true);

  gen _Quo(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_quo,g);
  }
  static const char _Quo_s []="Quo";
  static define_unary_function_eval (__Quo,&_Quo,_Quo_s);
  define_unary_function_ptr5( at_Quo ,alias_at_Quo,&__Quo,0,true);

  gen _Rem(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_rem,g);
  }
  static const char _Rem_s []="Rem";
  static define_unary_function_eval (__Rem,&_Rem,_Rem_s);
  define_unary_function_ptr5( at_Rem ,alias_at_Rem,&__Rem,0,true);

  gen _Int(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_integrate,g);
  }
  static const char _Int_s []="Int";
  static define_unary_function_eval (__Int,&_Int,_Int_s);
  define_unary_function_ptr5( at_Int ,alias_at_Int,&__Int,0,true);

  gen _divisors(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen res=_idivis(g,contextptr);
    if (res.type==_VECT) res.subtype=_SET__VECT;
    return res;
  }
  static const char _divisors_s []="divisors";
  static define_unary_function_eval (__divisors,&_divisors,_divisors_s);
  define_unary_function_ptr5( at_divisors ,alias_at_divisors,&__divisors,0,true);

  gen _maxnorm(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=remove_at_pnt(g0);
    if (g.type==_VECT && g.subtype==_VECTOR__VECT)
      g=vector2vecteur(*g._VECTptr);
    return linfnorm(g,contextptr);
  }
  static const char _maxnorm_s []="maxnorm";
  static define_unary_function_eval (__maxnorm,&_maxnorm,_maxnorm_s);
  define_unary_function_ptr5( at_maxnorm ,alias_at_maxnorm,&__maxnorm,0,true);

  gen _l1norm(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=remove_at_pnt(g0);
    if (g.type==_VECT && g.subtype==_VECTOR__VECT)
      g=vector2vecteur(*g._VECTptr);
    if (g.type!=_VECT)
      return linfnorm(g,contextptr);
    gen res;
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it)
      res=res+linfnorm(*it,contextptr);
    return res;
  }
  static const char _l1norm_s []="l1norm";
  static define_unary_function_eval (__l1norm,&_l1norm,_l1norm_s);
  define_unary_function_ptr5( at_l1norm ,alias_at_l1norm,&__l1norm,0,true);

  gen _dotprod(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if ( (g.type!=_VECT) || (g._VECTptr->size()!=2))
      return gentypeerr(contextptr);
    vecteur v=*g._VECTptr;
    return dotvecteur(v[0],v[1]);
  }
  static const char _dotprod_s []="dotprod";
  static define_unary_function_eval (__dotprod,&_dotprod,_dotprod_s);
  define_unary_function_ptr5( at_dotprod ,alias_at_dotprod,&__dotprod,0,true);

  static const char _crossproduct_s []="crossproduct";
  static define_unary_function_eval (__crossproduct,&_cross,_crossproduct_s);
  define_unary_function_ptr5( at_crossproduct ,alias_at_crossproduct,&__crossproduct,0,true);

  gen _diag(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->empty())
      return gensizeerr(contextptr);
    vecteur v=*g._VECTptr;
    int l=v.size();
    if (is_squarematrix(v)){
      vecteur res(l);
      for (int i=0;i<l;++i)
	res[i]=v[i][i];
      return res;
    }
    if (ckmatrix(v)){
      if (l==1 && v[0].type==_VECT){
	v=*v[0]._VECTptr;
      }
      else
	v=*mtran(v)[0]._VECTptr;
    }
    l=v.size();
    matrice res;
    if (l && ckmatrix(v.front()) ){
      int s=0,r=0;
      for (int i=0;i<l;++i){
	if (!is_squarematrix(v[i]))
	  return gentypeerr(contextptr);
	s += v[i]._VECTptr->size();
      }
      for (int i=0;i<l;++i){
	vecteur & current=*v[i]._VECTptr;
	int c=current.size();
	for (int j=0;j<c;++j){
	  vecteur tmp(r);
	  vecteur & currentj=*current[j]._VECTptr;
	  for (int k=0;k<c;++k){
	    tmp.push_back(currentj[k]);
	  }
	  for (int k=c+r;k<s;++k)
	    tmp.push_back(zero);
	  res.push_back(tmp);
	}
	r += c;
      }
      return res;
    }
    for (int i=0;i<l;++i){
      vecteur tmp(i);
      tmp.push_back(v[i]);
      res.push_back(mergevecteur(tmp,vecteur(l-1-i)));
    }
    return res;
  }
  static const char _diag_s []="diag";
  static define_unary_function_eval (__diag,&_diag,_diag_s);
  define_unary_function_ptr5( at_diag ,alias_at_diag,&__diag,0,true);

  static const char _BlockDiagonal_s []="BlockDiagonal";
  static define_unary_function_eval (__BlockDiagonal,&_diag,_BlockDiagonal_s);
  define_unary_function_ptr5( at_BlockDiagonal ,alias_at_BlockDiagonal,&__BlockDiagonal,0,true);

  gen _input(const gen & args,GIAC_CONTEXT){
    if (interactive_op_tab && interactive_op_tab[0])
      return interactive_op_tab[0](args,contextptr);
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return _input(args,false,contextptr);
  }
  static const char _input_s []="input";
#ifdef RTOS_THREADX
  // const unary_function_eval __input(0,(const gen_op_context)_input,_input_s);
  define_unary_function_eval(__input,(const gen_op_context)_input,_input_s);
#else
  unary_function_eval __input(0,(const gen_op_context)_input,_input_s);
#endif
  define_unary_function_ptr5( at_input ,alias_at_input,&__input,_QUOTE_ARGUMENTS,true);

  gen _textinput(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return _input(args,true,contextptr);
  }
  static const char _textinput_s []="textinput";
  static define_unary_function_eval_quoted (__textinput,&_textinput,_textinput_s);
  define_unary_function_ptr5( at_textinput ,alias_at_textinput,&__textinput,_QUOTE_ARGUMENTS,true);

  static const char _f2nd_s []="f2nd";
  static define_unary_function_eval (__f2nd,&_fxnd,_f2nd_s);
  define_unary_function_ptr5( at_f2nd ,alias_at_f2nd,&__f2nd,0,true);

  // service=-3 for content -2 for primpart -1 for coeff, degree for coeff
  static gen primpartcontent(const gen& g,int service,GIAC_CONTEXT){
    vecteur v;
    if (g.type==_VECT && g.subtype !=_SEQ__VECT){
      if (calc_mode(contextptr)==1)
	v=makevecteur(g,ggb_var(g));
      else
	v=makevecteur(g,vx_var);
    }
    else
      v=gen2vecteur(g);
    int s=v.size();
    if (s==2 && v[1].is_symb_of_sommet(at_pow)){
      gen & f = v[1]._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2 && f._VECTptr->back().type==_INT_){
	v[1]=f._VECTptr->front();
	service=f._VECTptr->back().val;
      }
    }
    if (s>=2 && v[1].type==_VECT){
      vecteur l(*v[1]._VECTptr);
      int outerdim=l.size();
      lvar(v[0],l);
      int innerdim=l.size()-outerdim;
      fraction f(sym2r(v[0],l,contextptr));
      vecteur ll(l.begin()+outerdim,l.end());
      if (f.num.type!=_POLY){
	if (service==-1){
	  gen res=r2e(v[0],l,contextptr);
	  if (s==3){
	    if (is_zero(v[2]))
	      return res;
	    else
	      return zero;
	  }
	  return makevecteur(res);
	}
	if (service==-2)
	  return r2e(inv(f.den,contextptr),l,contextptr);
	if (service==-3)
	  return r2e(f.num,l,contextptr);
	return gensizeerr(contextptr);
      }
      polynome & p_aplati=*f.num._POLYptr;
      polynome p=splitmultivarpoly(p_aplati,innerdim); 
      vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
      vecteur coeffs;
      coeffs.reserve(itend-it);
      for (;it!=itend;++it)
	coeffs.push_back(it->value);
      if (service==-1){
	gen gden=r2e(f.den,l,contextptr);
	if (s==3 && v[2].type==_VECT){
	  index_t ind;
	  if (!vecteur2index(*v[2]._VECTptr,ind))
	    return zero;
	  index_m i(ind);
	  it=p.coord.begin();
	  for (;it!=itend;++it){
	    if (it->index==i)
	      return r2e(it->value,ll,contextptr)/gden;
	  }
	  return zero;
	}
	return r2e(coeffs,ll,contextptr)/gden;
      }
      if (service==-2){
	p=p/_lgcd(coeffs,contextptr);
	p=unsplitmultivarpoly(p,innerdim);
	return r2e(p/f.den,l,contextptr);
      }
      if (service==-3)
	return r2e(_lgcd(coeffs,contextptr),ll,contextptr);
      return gensizeerr(contextptr);
    }
    if (s!=1 && s!=2)
      return gensizeerr(contextptr);
    gen x(vx_var);
    if (calc_mode(contextptr)==1)
      x=ggb_var(v[0]);
    if (s==2)
      x=v[1];
    gen f(_e2r(gen(makevecteur(v[0],x),_SEQ__VECT),contextptr));
    gen deno(1);
    if (f.type==_FRAC){
      deno=f._FRACptr->den;
      f=f._FRACptr->num;
    }
    if (f.type!=_VECT){
      switch(service){
      case -1:
	return makevecteur(f)/deno;
      case -2:
	return plus_one;
      case -3:
	return f;
      default:
	if (service>0)
	  return zero;
	else
	  return f;
      }
    }
    switch (service){
    case -1:
      return f/deno;
    case -2:
      return symb_horner(*f._VECTptr/_lgcd(f,contextptr),x);
    case -3:
      return _lgcd(f,contextptr)/deno;
    }
    vecteur & w=*f._VECTptr;
    int ss=w.size();
    if (service>=ss)
      return zero;
    return w[ss-service-1]/deno;
  }
  gen _primpart(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return primpartcontent(g,-2,contextptr);
  }
  static const char _primpart_s []="primpart";
  static define_unary_function_eval (__primpart,&_primpart,_primpart_s);
  define_unary_function_ptr5( at_primpart ,alias_at_primpart,&__primpart,0,true);

  gen _content(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return primpartcontent(g,-3,contextptr);
  }
  static const char _content_s []="content";
  static define_unary_function_eval (__content,&_content,_content_s);
  define_unary_function_ptr5( at_content ,alias_at_content,&__content,0,true);

  static const char _icontent_s []="icontent";
  static define_unary_function_eval (__icontent,&_content,_icontent_s);
  define_unary_function_ptr5( at_icontent ,alias_at_icontent,&__icontent,0,true);

  gen _coeff(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT && !g._VECTptr->empty() && g._VECTptr->back().type==_INT_){
      vecteur v=*g._VECTptr;
      int n=absint(v.back().val);
      v.pop_back();
      return primpartcontent(gen(v,g.subtype),n,contextptr);
    }
    if (xcas_mode(contextptr)==1 && g.type==_VECT && g._VECTptr->size()==2 && g._VECTptr->back().type==_IDNT){
      return primpartcontent(g,1,contextptr);
    }
    return primpartcontent(g,-1,contextptr);
  }
  static const char _coeff_s []="coeff";
  static define_unary_function_eval (__coeff,&_coeff,_coeff_s);
  define_unary_function_ptr5( at_coeff ,alias_at_coeff,&__coeff,0,true);

  static const char _coeffs_s []="coeffs";
  static define_unary_function_eval (__coeffs,&_coeff,_coeffs_s);
  define_unary_function_ptr5( at_coeffs ,alias_at_coeffs,&__coeffs,0,true);

  static const char _ichrem_s []="ichrem";
  static define_unary_function_eval (__ichrem,&_ichinrem,_ichrem_s);
  define_unary_function_ptr5( at_ichrem ,alias_at_ichrem,&__ichrem,0,true);

  gen _chrem(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!ckmatrix(g) || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    matrice m=mtran(*g._VECTptr);
    const_iterateur it=m.begin(),itend=m.end();
    if (it==itend)
      return gensizeerr(contextptr);
    gen res=*it;
    for (++it;it!=itend;++it){
      res=_ichinrem(makesequence(res,*it),contextptr);
    }
    return res;
  }
  static const char _chrem_s []="chrem";
  static define_unary_function_eval (__chrem,&_chrem,_chrem_s);
  define_unary_function_ptr5( at_chrem ,alias_at_chrem,&__chrem,0,true);

  gen _genpoly(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=3)
      return gentypeerr(contextptr);
    vecteur & v=*g._VECTptr;
    gen n=v[0],b=v[1],x=v[2];
    if (b.type!=_INT_ && b.type!=_ZINT)
      return gentypeerr(contextptr);
    b=abs(b,contextptr);
    if (is_zero(b)||is_one(b))
      return gensizeerr(contextptr);
    vecteur l(lvar(n));
    fraction f(e2r(n,l,contextptr));
    if (is_integer(f.num))
      f.num=pzadic(polynome(f.num,0),b);
    else {
      if (f.num.type==_POLY)
	f.num=pzadic(*f.num._POLYptr,b);
    }
    if (is_integer(f.den))
      f.den=pzadic(polynome(f.den,0),b);
    else {
      if (f.den.type==_POLY)
	f.den=pzadic(*f.den._POLYptr,b);
    }
    l.insert(l.begin(),x);
    return r2e(f,l,contextptr);
  }
  static const char _genpoly_s []="genpoly";
  static define_unary_function_eval (__genpoly,&_genpoly,_genpoly_s);
  define_unary_function_ptr5( at_genpoly ,alias_at_genpoly,&__genpoly,0,true);

  static gen freq_quantile(const matrice & v,double d,GIAC_CONTEXT){
    if (!ckmatrix(v))
      return undef;
    matrice w;
    if (v.size()==2)
      w=mtran(v);
    else
      w=v;
    if (w.front()._VECTptr->size()!=2)
      return undef;
    // Row Sort (using row 1)
    sort(w.begin(),w.end(),first_ascend_sort);
    w=mtran(w);
    // w[0]=data, w[1]=frequencies
    vecteur data=*w[0]._VECTptr;
    vecteur freq=*w[1]._VECTptr;
    gen sigma=d*prodsum(freq,false);
    if (is_undef(sigma)) return sigma;
    int s=freq.size();
    gen partial_sum;
    for (int i=0;i<s;++i){
      partial_sum=partial_sum+freq[i];
      if (!is_zero(partial_sum) && is_greater(partial_sum,sigma,contextptr))
	return data[i];
    }
    return undef;
  }
  gen _median(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (g.type==_VECT && g.subtype==_SEQ__VECT && v.size()==2)
      return freq_quantile(v,0.5,contextptr);
    if (!ckmatrix(v)){
      if (!is_fully_numeric(evalf(v,1,contextptr))){
	sort(v.begin(),v.end(),islesscomplexthanf);
	return v[int(std::ceil(v.size()/2.0))-1]; // v[(v.size()-1)/4];
      }
      matrice mt=mtran(ascsort(mtran(vecteur(1,v)),true));
      if (calc_mode(contextptr)==1 && !v.empty() && !(v.size()%2))
	return (mt[v.size()/2][0]+mt[v.size()/2-1][0])/2;
      return mt[int(std::ceil(v.size()/2.0))-1][0];
    }
    else
      v=ascsort(v,true);
    v=mtran(v);
    if (calc_mode(contextptr)==1 && !v.empty() && !(v.size()%2))
      return (v[v.size()/2]+v[v.size()/2-1])/2;
    return v[int(std::ceil(v.size()/2.0))-1]; // v[(v.size()-1)/2];
  }
  static const char _median_s []="median";
  static define_unary_function_eval(unary_median,&_median,_median_s);
  define_unary_function_ptr5( at_median ,alias_at_median,&unary_median,0,true);

  gen _quartile1(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (g.type==_VECT && g.subtype==_SEQ__VECT && v.size()==2)
      return freq_quantile(v,0.25,contextptr);
    if (!ckmatrix(v)){
      if (!is_fully_numeric(evalf(v,1,contextptr))){
	sort(v.begin(),v.end(),islesscomplexthanf);
	return v[int(std::ceil(v.size()/4.0))-1]; // v[(v.size()-1)/4];
      }
      return mtran(ascsort(mtran(vecteur(1,v)),true))[int(std::ceil(v.size()/4.0))-1][0];
    }
    else
      v=ascsort(v,true);
    v=mtran(v);
    return v[int(std::ceil(v.size()/4.0))-1]; // v[(v.size()-1)/4];
  }
  static const char _quartile1_s []="quartile1";
  static define_unary_function_eval(unary_quartile1,&_quartile1,_quartile1_s);
  define_unary_function_ptr5( at_quartile1 ,alias_at_quartile1,&unary_quartile1,0,true);

  gen _quartile3(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (g.type==_VECT && g.subtype==_SEQ__VECT && v.size()==2)
      return freq_quantile(v,0.75,contextptr);
    if (!ckmatrix(v)){
      if (!is_fully_numeric(evalf(v,1,contextptr))){
	sort(v.begin(),v.end(),islesscomplexthanf);
	return v[int(std::ceil(3*v.size()/4.0))-1]; // v[(v.size()-1)/4];
      }
      return mtran(ascsort(mtran(vecteur(1,v)),true))[int(std::ceil(3*v.size()/4.0))-1][0];
    }
    else
      v=ascsort(v,true);
    v=mtran(v);
    return v[int(std::ceil(3*v.size()/4.0))-1]; // v[(3*(v.size()-1))/4];
  }
  static const char _quartile3_s []="quartile3";
  static define_unary_function_eval(unary_quartile3,&_quartile3,_quartile3_s);
  define_unary_function_ptr5( at_quartile3 ,alias_at_quartile3,&unary_quartile3,0,true);

  gen _quantile(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.size()<2 || v.front().type!=_VECT || v.back().type!=_DOUBLE_)
      return gensizeerr(contextptr);
    double d=v.back()._DOUBLE_val;
    if (d<=0 || d>=1)
      return gendimerr(contextptr);
    if (g.type==_VECT && g.subtype==_SEQ__VECT && v.size()==3)
      return freq_quantile(makevecteur(v[0],v[1]),d,contextptr);
    v=*v.front()._VECTptr;
    if (!ckmatrix(v)){
      if (!is_fully_numeric(evalf(v,1,contextptr))){
	sort(v.begin(),v.end(),islesscomplexthanf);
	return v[int(std::ceil(d*v.size()))-1]; // v[(v.size()-1)/4];
      }
      v=ascsort(mtran(vecteur(1,v)),true);
    }
    else
      v=ascsort(v,true);
    v=mtran(v);
    return v[int(std::ceil(d*v.size()))-1];
  }
  static const char _quantile_s []="quantile";
  static define_unary_function_eval(unary_quantile,&_quantile,_quantile_s);
  define_unary_function_ptr5( at_quantile ,alias_at_quantile,&unary_quantile,0,true);

  gen _quartiles(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (g.type==_VECT && g.subtype==_SEQ__VECT && v.size()==2)
      return makevecteur(freq_quantile(v,0.0,contextptr),freq_quantile(v,0.25,contextptr),freq_quantile(v,0.5,contextptr),freq_quantile(v,0.75,contextptr),freq_quantile(v,1.0,contextptr));
    if (!ckmatrix(v)){
      if (!is_fully_numeric(evalf(v,1,contextptr))){
	sort(v.begin(),v.end(),islesscomplexthanf);
	int s=v.size();
	return makevecteur(v[0],v[int(std::ceil(s/4.))-1],v[int(std::ceil(s/2.))-1],v[int(std::ceil(3*s/4.))-1],v[s-1]);
      }
      v=ascsort(mtran(vecteur(1,v)),true);
    }
    else
      v=ascsort(v,true);
    v=mtran(v);
    int s=v.size();
    if (s==0)
      return gensizeerr(contextptr);
    return makevecteur(v[0],v[int(std::ceil(s/4.))-1],v[int(std::ceil(s/2.))-1],v[int(std::ceil(3*s/4.))-1],v[s-1]);
  }
  static const char _quartiles_s []="quartiles";
  static define_unary_function_eval(unary_quartiles,&_quartiles,_quartiles_s);
  define_unary_function_ptr5( at_quartiles ,alias_at_quartiles,&unary_quartiles,0,true);

  gen _moustache(const gen & g_orig,GIAC_CONTEXT){
    if ( g_orig.type==_STRNG && g_orig.subtype==-1) return  g_orig;
    vecteur attributs(1,default_color(contextptr));
    gen g(g_orig);
    bool horizontal=true;
    double ymin=global_window_ymin;
    double ymax=global_window_ymax;
    if (g.type==_VECT && g.subtype==_SEQ__VECT){
      vecteur v(*g._VECTptr);
      int s=read_attributs(v,attributs,contextptr);
      if (s>1){
	gen tmp=v[s-1];
	if (tmp.is_symb_of_sommet(at_equal)){
	  if (tmp._SYMBptr->feuille[0]==x__IDNT_e)
	    horizontal=false;
	  tmp=tmp._SYMBptr->feuille[1];
	}
	if (tmp.is_symb_of_sommet(at_interval)){
	  ymin=evalf_double(tmp._SYMBptr->feuille[0],1,contextptr)._DOUBLE_val;
	  ymax=evalf_double(tmp._SYMBptr->feuille[1],1,contextptr)._DOUBLE_val;
	  --s;
	}
      }
      if (s==1)
	g=v.front();
      else
	g=gen(vecteur(v.begin(),v.begin()+s),g_orig.subtype);
    }
    gen tmpp=_quartiles(g,contextptr);
    if (tmpp.type!=_VECT)
      return tmpp;
    vecteur v0(*tmpp._VECTptr),v;
    if (!ckmatrix(v0))
      v=vecteur(1,v0);
    else
      v=mtran(v0);
    // _Pictsize(0);
    int s=v.size();
    vecteur res;
    double xmin=gnuplot_xmin,xmax=gnuplot_xmax;
    gen tmpx=_min(v0[0],contextptr),tmpxx=_max(v0[4],contextptr);
    if (tmpx.type==_DOUBLE_)
      xmin=tmpx._DOUBLE_val;
    if (tmpxx.type==_DOUBLE_)
      xmax=tmpxx._DOUBLE_val;
    vecteur attr(attributs);
    vecteur legendes(1,string2gen("",false));
    if (attributs.size()>=2)
      legendes=gen2vecteur(attributs[1]);
    else
      attr.push_back(legendes);
    int ls=legendes.size();
    vecteur affichages(gen2vecteur(attributs[0]));
    int as=affichages.size();
    if (horizontal){
      double y_scale=(ymax-ymin)/(4*s);
      for (int i=0;i<s;++i){
	attr[0]=(i<as?affichages[i]:affichages[0]);
	attr[1]=(i<ls?legendes[i]:legendes[0]);
	double y_up=ymax-(4*i+1)*y_scale;
	double y_middle=ymax-(4*i+2)*y_scale;
	double y_down=ymax-(4*i+3)*y_scale;
	vecteur current=gen2vecteur(v[i]);
	if (current.size()!=5)
	  continue;
	// trait min -> 1er quartile
	res.push_back(symb_segment(current[0]+y_middle*cst_i,current[1]+y_middle*cst_i,attr,_GROUP__VECT,contextptr));
	// rectangle 
	res.push_back(pnt_attrib(gen(makevecteur(current[3]+y_down*cst_i,current[1]+y_down*cst_i,current[1]+y_up*cst_i,current[3]+y_up*cst_i,current[3]+y_down*cst_i),_GROUP__VECT),attr,contextptr));
	// mediane
	res.push_back(symb_segment(current[2]+y_down*cst_i,current[2]+y_up*cst_i,attr,_GROUP__VECT,contextptr));
	// trait 3eme quartile -> fin
	res.push_back(symb_segment(current[3]+y_middle*cst_i,current[4]+y_middle*cst_i,attr,_GROUP__VECT,contextptr));
      }
    } else { // vertical picture
      giac::swapdouble(xmin,ymin);
      giac::swapdouble(xmax,ymax);
      double x_scale=(xmax-xmin)/(4*s);
      for (int i=0;i<s;++i){
	attr[0]=(i<as?affichages[i]:affichages[0]);
	attr[1]=(i<ls?legendes[i]:legendes[0]);
	double x_up=xmax-(4*i+1)*x_scale;
	double x_middle=xmax-(4*i+2)*x_scale;
	double x_down=xmax-(4*i+3)*x_scale;
	vecteur current=gen2vecteur(v[i]);
	if (current.size()!=5)
	  continue;
	// trait min -> 1er quartile
	res.push_back(symb_segment(x_middle+current[0]*cst_i,current[1]*cst_i+x_middle,attr,_GROUP__VECT,contextptr));
	// rectangle
	res.push_back(pnt_attrib(gen(makevecteur(current[1]*cst_i+x_up,current[1]*cst_i+x_down,current[3]*cst_i+x_down,current[3]*cst_i+x_up,current[1]*cst_i+x_up),_GROUP__VECT),attr,contextptr));
	// mediane
	res.push_back(symb_segment(current[2]*cst_i+x_down,current[2]*cst_i+x_up,attr,_GROUP__VECT,contextptr));
	// trait 3eme quartile -> fin
	res.push_back(symb_segment(current[3]*cst_i+x_middle,current[4]*cst_i+x_middle,attr,_GROUP__VECT,contextptr));
      }
    }
    return gen(res,_SEQ__VECT);
  }
  static const char _moustache_s []="moustache";
  static define_unary_function_eval(unary_moustache,&_moustache,_moustache_s);
  define_unary_function_ptr5( at_moustache ,alias_at_moustache,&unary_moustache,0,true);

  static const char _boxwhisker_s []="boxwhisker";
  static define_unary_function_eval(unary_boxwhisker,&_moustache,_boxwhisker_s);
  define_unary_function_ptr5( at_boxwhisker ,alias_at_boxwhisker,&unary_boxwhisker,0,true);


  static gen stddevmean(const vecteur & v,int withstddev,int xcol,int freqcol,GIAC_CONTEXT){
    int sv=v.size();
    if (xcol>=sv || freqcol>=sv)
      return gendimerr(contextptr);
    if (v[xcol].type!=_VECT || v[freqcol].type!=_VECT)
      return gensizeerr(contextptr);
    vecteur v1(*v[xcol]._VECTptr),v2(*v[freqcol]._VECTptr);
    // if v1 is made of intervals replace by the center of these intervals
    iterateur it=v1.begin(),itend=v1.end();
    for (;it!=itend;++it){
      if (it->is_symb_of_sommet(at_interval)){
	gen & f=it->_SYMBptr->feuille;
	if (f.type==_VECT && f._VECTptr->size()==2)
	  *it=(f._VECTptr->front()+f._VECTptr->back())/2;
      }
    }
    if (ckmatrix(v1) ^ ckmatrix(v2))
      return gensizeerr(contextptr);
    int n=v1.size();
    if (unsigned(n)!=v2.size())
      return gensizeerr(contextptr);
    gen m,m2,s;
    for (int i=0;i<n;++i){
      s = s + v2[i];
      m = m + apply(v2[i],v1[i],prod);
      if (withstddev)
	m2 = m2 + apply(v2[i],apply(v1[i],v1[i],prod),prod);
    }
    m = apply(m,s,contextptr,rdiv);
    if (withstddev){
      m2=m2-apply(s,apply(m,m,prod),prod);
      m2=apply(m2,s-(withstddev==2),contextptr,rdiv);
      if (withstddev==3)
	return m2;
      return apply(m2,sqrt,contextptr);
    }
    else
      return m;
  }
  // withstddev=0 (mean), 1 (stddev divided by n), 2 (by n-1), 3 (variance)
  static gen stddevmean(const gen & g,int withstddev,GIAC_CONTEXT){
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s<2)
      return gensizeerr(contextptr);
    if (v[1].type!=_INT_)
      return stddevmean(v,withstddev,0,1,contextptr);
    if (v[0].type!=_VECT)
      return gensizeerr(contextptr);
    int xcol=v[1].val;
    int freqcol=xcol+1;
    if (s>2 && v[2].type==_INT_)
      freqcol=v[2].val;
    return stddevmean(mtran(*v[0]._VECTptr),withstddev,xcol,freqcol,contextptr);
  }

  gen _mean(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int nd;
    if (g.type==_SYMB && (nd=is_distribution(g))){
      gen f=g._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==1)
	f=f._VECTptr->front();
      int s=f.type==_VECT?f._VECTptr->size():1;
      if (s!=distrib_nargs(nd))
	return gensizeerr(contextptr);
      if (nd==1)
	return f[0];
      if (nd==2)
	return f[0]*f[1];
      if (nd==3)
	return f[0]*f[1]/(1-f[1]);
      if (nd==4 || nd==11)
	return f;
      if (nd==5)
	return (f.type<_IDNT && is_strictly_greater(1,f,contextptr))?undef:0;
      if (nd==6)
	return (f[1].type<_IDNT && is_greater(2,f[1],contextptr))?undef:f[1]/(f[1]-2);
      if (nd==8)
	return f[1]*Gamma(1+inv(f[0],contextptr),contextptr);
      if (nd==9)
	return f[0]/(f[0]+f[1]);
      if (nd==10)
	return f[0]/f[1];
      return undef;
    }
    if (g.type==_VECT && !g._VECTptr->empty() && g._VECTptr->front().type==_FUNC && (nd=is_distribution(g._VECTptr->front()))){
      return _mean(symbolic(*g._VECTptr->front()._FUNCptr,gen(vecteur(g._VECTptr->begin()+1,g._VECTptr->end()),_SEQ__VECT)),contextptr);
    }
    if (g.type==_VECT && g.subtype==_SEQ__VECT)
      return stddevmean(g,0,contextptr);
    vecteur v(gen2vecteur(g));
    if (!ckmatrix(v))
      return mean(mtran(vecteur(1,v)),true)[0];
    else
      v=mean(v,true);
    return v;
  }
  static const char _mean_s []="mean";
  static define_unary_function_eval (__mean,&_mean,_mean_s);
  define_unary_function_ptr5( at_mean ,alias_at_mean,&__mean,0,true);

  static const char _moyenne_s []="moyenne";
  static define_unary_function_eval (__moyenne,&_mean,_moyenne_s);
  define_unary_function_ptr5( at_moyenne ,alias_at_moyenne,&__moyenne,0,true);

  gen _stdDev(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT && g.subtype==_SEQ__VECT)
      return stddevmean(g,2,contextptr);
    vecteur v(gen2vecteur(g));
    if (!ckmatrix(v))
      return stddev(mtran(vecteur(1,v)),true,2)[0];
    else
      v=stddev(v,true,2);
    return v;
  }
  static const char _stdDev_s []="stdDev";
  static define_unary_function_eval (__stdDev,&_stdDev,_stdDev_s);
  define_unary_function_ptr5( at_stdDev ,alias_at_stdDev,&__stdDev,0,true);

  static const char _stddevp_s []="stddevp";
  static define_unary_function_eval (__stddevp,&_stdDev,_stddevp_s);
  define_unary_function_ptr5( at_stddevp ,alias_at_stddevp,&__stddevp,0,true);

  static const char _ecart_type_population_s []="ecart_type_population";
  static define_unary_function_eval (__ecart_type_population,&_stdDev,_ecart_type_population_s);
  define_unary_function_ptr5( at_ecart_type_population ,alias_at_ecart_type_population,&__ecart_type_population,0,true);

  gen _stddev(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int nd;
    if (g.type==_SYMB && (nd=is_distribution(g))){
      gen f=g._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==1)
	f=f._VECTptr->front();
      int s=f.type==_VECT?f._VECTptr->size():1;
      if (s!=distrib_nargs(nd))
	return gensizeerr(contextptr);
      if (nd==1)
	return f[1];
      if (nd==2)
	return sqrt(f[0]*f[1]*(1-f[1]),contextptr);
      if (nd==3)
	return sqrt(f[0]*f[1],contextptr)/(1-f[1]);
      if (nd==4)
	return sqrt(f,contextptr);
      if (nd==11)
	return sqrt(2*f,contextptr);
      if (nd==5){
	if (f.type<_IDNT && is_greater(1,f,contextptr)) return undef;
	if (f.type<_IDNT && is_greater(2,f,contextptr)) return plus_inf;
	return sqrt(f/(f-2),contextptr);
      }
      if (nd==6)
	return (f[1].type<_IDNT && is_greater(4,f[1],contextptr))?undef:f[1]/(f[1]-2)*sqrt(2*(f[0]+f[1]-2)/f[0]/(f[1]-4),contextptr);
      if (nd==8)
	return f[1]*sqrt(Gamma(1+gen(2)/f[0],contextptr)-pow(Gamma(1+gen(1)/f[0],contextptr),2,contextptr),contextptr);
      if (nd==9)
	return sqrt(f[0]*f[1]/(f[0]+f[1]+1),contextptr)/(f[0]+f[1]);
      if (nd==10)
	return sqrt(f[0],contextptr)/f[1];
      return undef;
    }
    if (g.type==_VECT && !g._VECTptr->empty() && g._VECTptr->front().type==_FUNC && (nd=is_distribution(g._VECTptr->front()))){
      return _stddev(symbolic(*g._VECTptr->front()._FUNCptr,gen(vecteur(g._VECTptr->begin()+1,g._VECTptr->end()),_SEQ__VECT)),contextptr);
    }
    if (g.type==_VECT && g.subtype==_SEQ__VECT)
      return stddevmean(g,1,contextptr);
    vecteur v(gen2vecteur(g));
    if (!ckmatrix(v))
      return stddev(mtran(vecteur(1,v)),true,1)[0];
    else
      v=stddev(v,true,1);
    return v;
  }
  static const char _stddev_s []="stddev";
  static define_unary_function_eval (__stddev,&_stddev,_stddev_s);
  define_unary_function_ptr5( at_stddev ,alias_at_stddev,&__stddev,0,true);

  static const char _ecart_type_s []="ecart_type";
  static define_unary_function_eval (__ecart_type,&_stddev,_ecart_type_s);
  define_unary_function_ptr5( at_ecart_type ,alias_at_ecart_type,&__ecart_type,0,true);

  gen _variance(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT && g.subtype==_SEQ__VECT)
      return stddevmean(g,3,contextptr);
    vecteur v(gen2vecteur(g));
    if (!ckmatrix(v))
      return stddev(mtran(vecteur(1,v)),true,3)[0];
    else
      v=stddev(v,true,3);
    return v;
  }
  static const char _variance_s []="variance";
static define_unary_function_eval (__variance,&_variance,_variance_s);
  define_unary_function_ptr5( at_variance ,alias_at_variance,&__variance,0,true);

  static vecteur covariance_correlation(const gen & g,const gen & u1,const gen & u2,int xcol,int ycol,int freqcol,GIAC_CONTEXT){
    if (is_undef(g))
      return makevecteur(g,g);
    vecteur v(gen2vecteur(g));
    if (!ckmatrix(v) || v.empty() || v.front()._VECTptr->size()<2)
      return makevecteur(undef,undef);
    gen sigmax,sigmay,sigmaxy,sigmax2,sigmay2,tmpx,tmpy,n,freq;
    if (freqcol<-1){
      // g is interpreted as a double-entry table with 1st col = x-values
      // 1st line=y-values, line/col is a frequency for this value of x/y
      int r,c;
      mdims(v,r,c);
      if (r<2 || c<2)
	return makevecteur(gendimerr(contextptr),gendimerr(contextptr));
      vecteur & vy=*v[0]._VECTptr;
      for (int i=1;i<r;++i){
	vecteur & w=*v[i]._VECTptr;
	gen & currentx=w[0];
	for (int j=1;j<c;++j){
	  gen & currenty=vy[j];
	  freq=w[j];
	  n=n+freq;
	  sigmax=sigmax+currentx*freq;
	  sigmax2=sigmax2+currentx*currentx*freq;
	  sigmay=sigmay+currenty*freq;
	  sigmay2=sigmay2+currenty*currenty*freq;
	  sigmaxy=sigmaxy+currentx*currenty*freq;
	}
      }
    }
    else {
      const_iterateur it=v.begin(),itend=v.end();
      int s=it->_VECTptr->size();
      if (xcol>=s || ycol>=s || freqcol >=s)
	return makevecteur(gendimerr(contextptr),gendimerr(contextptr));
      for (;it!=itend;++it){
	vecteur & w=*it->_VECTptr;
	if (u1.type==_FUNC)
	  tmpx=u1(w[xcol],contextptr);
	else
	  tmpx=w[xcol];
	if (u2.type==_FUNC)
	  tmpy=u2(w[ycol],contextptr);
	else
	  tmpy=w[ycol];
	if (freqcol>=0)
	  freq=w[freqcol];
	else
	  freq=plus_one;
	n = n+freq;
	sigmax = sigmax + tmpx*freq;
	sigmax2 = sigmax2 + tmpx*tmpx*freq;
	sigmay = sigmay + tmpy*freq;
	sigmay2 = sigmay2 + tmpy*tmpy*freq;
	sigmaxy = sigmaxy + tmpx*tmpy*freq;
      }
    }
    gen covariance=(n*sigmaxy-sigmax*sigmay)/(n*n);
    gen correlation=(n*sigmaxy-sigmax*sigmay)/sqrt((n*sigmax2-sigmax*sigmax)*(n*sigmay2-sigmay*sigmay),contextptr);
    return makevecteur(covariance,correlation);
  }
  
  static void find_xyfreq(const gen & g,gen & gv,int & xcol,int & ycol,int &freqcol,GIAC_CONTEXT){
    xcol=0;
    ycol=1;
    freqcol=-1;
    if (g.type==_VECT && g.subtype==_SEQ__VECT && !g._VECTptr->empty()){
      vecteur v=*g._VECTptr;
      if (v[0].type!=_VECT){
	gv=gensizeerr(contextptr);
	return;
      }
      int s=v.size();
      if (s==3 && v[1].type==_VECT){
	if (!ckmatrix(v[2]))
	  v[2]=_diag(v[2],contextptr);
	int n,c;
	mdims(*v[2]._VECTptr,n,c);
	if (unsigned(n)==v[0]._VECTptr->size() && unsigned(c)==v[1]._VECTptr->size()){
	  vecteur v0(*v[1]._VECTptr);
	  v0.insert(v0.begin(),zero);
	  matrice m(mtran(*v[2]._VECTptr));
	  m.insert(m.begin(),v[0]);
	  m=mtran(m);
	  m.insert(m.begin(),v0);
	  gv=m;
	  freqcol=-2;
	  return;
	}
      }
      if (s>1) {
	if (v[1].type==_INT_){
	  xcol=v[1].val;
	  if (xcol<0)
	    freqcol=-2;
	}
	else {
	  if (!ckmatrix(v))
	    gv=gensizeerr(contextptr);
	  else
	    gv=mtran(v);
	  return;
	}
      }
      if (s>2 && v[2].type==_INT_)
	ycol=v[2].val;
      if (s>3 && v[3].type==_INT_)
	freqcol=v[3].val;
      gv=v[0];
    }
    else {
      if (!ckmatrix(g) || g._VECTptr->empty()){
	gv=gensizeerr(contextptr);
	return;
      }
      gv=g;
      if (g._VECTptr->front()._VECTptr->size()>2)
	freqcol=2;
      if (g._VECTptr->front()._VECTptr->front().type==_STRNG)
	freqcol=-2;
    }
  }
  gen _covariance_correlation(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int xcol,ycol,freqcol;
    gen gv;
    find_xyfreq(g,gv,xcol,ycol,freqcol,contextptr);
    if (is_undef(gv)) return gv;
    return covariance_correlation(gv,zero,zero,xcol,ycol,freqcol,contextptr);      
  }
  static const char _covariance_correlation_s []="covariance_correlation";
static define_unary_function_eval (__covariance_correlation,&_covariance_correlation,_covariance_correlation_s);
  define_unary_function_ptr5( at_covariance_correlation ,alias_at_covariance_correlation,&__covariance_correlation,0,true);

  gen _covariance(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int xcol,ycol,freqcol;
    gen gv;
    find_xyfreq(g,gv,xcol,ycol,freqcol,contextptr);
    if (is_undef(gv)) return gv;
    return covariance_correlation(gv,zero,zero,xcol,ycol,freqcol,contextptr)[0];
  }
  static const char _covariance_s []="covariance";
static define_unary_function_eval (__covariance,&_covariance,_covariance_s);
  define_unary_function_ptr5( at_covariance ,alias_at_covariance,&__covariance,0,true);

  gen _correlation(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int xcol,ycol,freqcol;
    gen gv;
    find_xyfreq(g,gv,xcol,ycol,freqcol,contextptr);
    if (is_undef(gv)) return gv;
    return covariance_correlation(gv,zero,zero,xcol,ycol,freqcol,contextptr)[1];
  }
  static const char _correlation_s []="correlation";
static define_unary_function_eval (__correlation,&_correlation,_correlation_s);
  define_unary_function_ptr5( at_correlation ,alias_at_correlation,&__correlation,0,true);

  gen _interval2center(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT)
      return apply(g,_interval2center,contextptr);
    if (g.is_symb_of_sommet(at_interval)){
      gen & tmp=g._SYMBptr->feuille;
      if (tmp.type!=_VECT || tmp._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      vecteur & v=*tmp._VECTptr;
      return (v.front()+v.back())/2;
    }
    return g;
  }
  static const char _interval2center_s []="interval2center";
  static define_unary_function_eval (__interval2center,&_interval2center,_interval2center_s);
  define_unary_function_ptr5( at_interval2center ,alias_at_interval2center,&__interval2center,0,true);

  gen function_regression(const gen & g,const gen & u1,const gen & u2,gen & a,gen &b,double & xmin,double & xmax,gen & correl2,GIAC_CONTEXT){
    gen gv,freq;
    int xcol,ycol,freqcol;
    xmin=1e300;
    xmax=-xmin;
    find_xyfreq(g,gv,xcol,ycol,freqcol,contextptr);
    if (!ckmatrix(gv))
      return gensizeerr(contextptr);
    vecteur & v = *gv._VECTptr;
    gen n;
    gen sigmax,sigmay,sigmaxy,sigmax2,sigmay2,tmpx,tmpy;
    if (freqcol<-1){
      int r,c;
      mdims(v,r,c);
      if (r<2 || c<2)
	return gendimerr(contextptr);
      vecteur & vy=*v[0]._VECTptr;
      gen currentx,currenty;
      for (int i=1;i<r;++i){
	vecteur & w=*v[i]._VECTptr;
	gen tmpg=evalf_double(w[0],1,contextptr);
	if (tmpg.type==_DOUBLE_){
	  double tmp=tmpg._DOUBLE_val;
	  if (tmp<xmin)
	    xmin=tmp;
	  if (tmp>xmax)
	    xmax=tmp;
	}
	if (u1.type==_FUNC)
	  currentx=u1(w[0],contextptr);
	else
	  currentx=w[0];
	for (int j=1;j<c;++j){
	  if (u2.type==_FUNC)
	    currenty=u2(vy[j],contextptr);
	  else
	    currenty=vy[j];
	  currenty=_interval2center(currenty,contextptr);
	  if (is_undef(currenty))
	    return currenty;
	  freq=w[j];
	  n=n+freq;
	  sigmax=sigmax+currentx*freq;
	  sigmax2=sigmax2+currentx*currentx*freq;
	  sigmay=sigmay+currenty*freq;
	  sigmay2=sigmay2+currenty*currenty*freq;
	  sigmaxy=sigmaxy+currentx*currenty*freq;
	}
      }
    }
    else {
      const_iterateur it=v.begin(),itend=v.end();
      for (;it!=itend;++it){
	vecteur & w=*it->_VECTptr;
	gen tmpg=evalf_double(w[xcol],1,contextptr);
	if (tmpg.type==_DOUBLE_){
	  double tmp=tmpg._DOUBLE_val;
	  if (tmp<xmin)
	    xmin=tmp;
	  if (tmp>xmax)
	    xmax=tmp;
	}
	if (u1.type==_FUNC)
	  tmpx=u1(w[xcol],contextptr);
	else
	  tmpx=w[xcol];
	tmpx=_interval2center(tmpx,contextptr);
	if (is_undef(tmpx))
	  return tmpx;
	if (u2.type==_FUNC)
	  tmpy=u2(w[ycol],contextptr);
	else
	  tmpy=w[ycol];
	tmpy=_interval2center(tmpy,contextptr);
	if (is_undef(tmpy))
	  return tmpy;
	if (freqcol<0)
	  freq=plus_one;
	else
	  freq=w[freqcol];
	sigmax = sigmax + freq*tmpx;
	sigmax2 = sigmax2 + freq*tmpx*tmpx;
	sigmay = sigmay + freq*tmpy;
	sigmay2 = sigmay2 + freq*tmpy*tmpy;
	sigmaxy = sigmaxy + freq*tmpx*tmpy;
	n = n + freq;
      }
    }
    gen tmp=(n*sigmaxy-sigmax*sigmay);
    a=tmp/(n*sigmax2-sigmax*sigmax);
    b=(sigmay-a*sigmax)/n;
    correl2=(tmp*tmp)/(n*sigmax2-sigmax*sigmax)/(n*sigmay2-sigmay*sigmay);
    return makevecteur(sigmax,sigmay,n,sigmax2,sigmay2);
    // cerr << sigmax << " "<< sigmay << " " << sigmaxy << " " << n << " " << sigmax2 << " " << sigmay2 << endl;
  }

  static gen function_regression(const gen & g,const gen & u1,const gen & u2,GIAC_CONTEXT){
    gen a,b,correl2;
    double xmin,xmax;
    gen errcode=function_regression(g,u1,u2,a,b,xmin,xmax,correl2,contextptr);
    if (is_undef(errcode)) return errcode;
    return gen(makevecteur(a,b),_SEQ__VECT);
  }

  gen _linear_regression(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return function_regression(g,zero,zero,contextptr);
  }
  static const char _linear_regression_s []="linear_regression";
static define_unary_function_eval (__linear_regression,&_linear_regression,_linear_regression_s);
  define_unary_function_ptr5( at_linear_regression ,alias_at_linear_regression,&__linear_regression,0,true);


  gen _exponential_regression(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return exp(function_regression(g,zero,at_ln,contextptr),contextptr);
  }
  static const char _exponential_regression_s []="exponential_regression";
static define_unary_function_eval (__exponential_regression,&_exponential_regression,_exponential_regression_s);
  define_unary_function_ptr5( at_exponential_regression ,alias_at_exponential_regression,&__exponential_regression,0,true);

  gen _logarithmic_regression(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return function_regression(g,at_ln,zero,contextptr);
  }
  static const char _logarithmic_regression_s []="logarithmic_regression";
static define_unary_function_eval (__logarithmic_regression,&_logarithmic_regression,_logarithmic_regression_s);
  define_unary_function_ptr5( at_logarithmic_regression ,alias_at_logarithmic_regression,&__logarithmic_regression,0,true);

  gen _power_regression(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen res= function_regression(evalf(g,1,contextptr),at_ln,at_ln,contextptr);
    if (res.type==_VECT && res._VECTptr->size()==2){
      vecteur v(*res._VECTptr);
      v[1]=exp(v[1],contextptr);
      return gen(v,_SEQ__VECT);
    }
    return res;
  }
  static const char _power_regression_s []="power_regression";
static define_unary_function_eval (__power_regression,&_power_regression,_power_regression_s);
  define_unary_function_ptr5( at_power_regression ,alias_at_power_regression,&__power_regression,0,true);

  gen regression_plot_attributs(const gen & g,vecteur & attributs,bool & eq,bool & r,GIAC_CONTEXT){
    gen res=g;
    r=false; eq=false;
    if (g.type==_VECT && g.subtype==_SEQ__VECT){
      int n=read_attributs(*g._VECTptr,attributs,contextptr);
      vecteur v=vecteur(g._VECTptr->begin(),g._VECTptr->begin()+n);
      vecteur & w=*g._VECTptr;
      int ws=w.size();
      for (int i=0;i<ws;++i){
	if (w[i]==at_equation){
	  eq=true;
	  if (i<n){
	    v.erase(v.begin()+i);
	    --n;
	    --i;
	  }
	}
	if (w[i]==at_correlation){
	  r=true;
	  if (i<n){
	    v.erase(v.begin()+i);
	    --n;
	    --i;
	  }
	}
      }
      if (n==1)
	res=g._VECTptr->front();
      else
	res=gen(v,_SEQ__VECT);
    }
    else
      attributs=vecteur(1,default_color(contextptr));
    return res;
  }

  gen _linear_regression_plot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen a,b,correl2;
    double xmin,xmax;
    vecteur attributs;
    bool eq,r;
    gen G=regression_plot_attributs(g,attributs,eq,r,contextptr);
    gen errcode=function_regression(G,zero,zero,a,b,xmin,xmax,correl2,contextptr);
    if (is_undef(errcode)) return errcode;
    xmax += (xmax-xmin); 
    gen ad(evalf_double(a,1,contextptr)),bd(evalf_double(b,1,contextptr)),cd(evalf_double(correl2,1,contextptr));
    if (ad.type==_DOUBLE_ && bd.type==_DOUBLE_ && cd.type==_DOUBLE_){
      string eqs="y="+print_DOUBLE_(ad._DOUBLE_val,3)+"*x+"+print_DOUBLE_(bd._DOUBLE_val,3);
      string R2s=" , R2="+print_DOUBLE_(cd._DOUBLE_val,3);
      *logptr(contextptr) << eqs << R2s << endl;
      string s;
      if (eq)
	s += eqs;
      if (r)
	s += R2s;
      attributs.push_back(string2gen(s,false));
    }
    return put_attributs(_droite(makesequence(b*cst_i,1+(b+a)*cst_i),contextptr),attributs,contextptr);
  }
  static const char _linear_regression_plot_s []="linear_regression_plot";
static define_unary_function_eval (__linear_regression_plot,&_linear_regression_plot,_linear_regression_plot_s);
  define_unary_function_ptr5( at_linear_regression_plot ,alias_at_linear_regression_plot,&__linear_regression_plot,0,true);

  gen _exponential_regression_plot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen a,b,correl2;
    double xmin,xmax;
    vecteur attributs;
    bool eq,r;
    gen G=regression_plot_attributs(g,attributs,eq,r,contextptr);
    gen errcode=function_regression(G,zero,at_ln,a,b,xmin,xmax,correl2,contextptr);
    if (is_undef(errcode)) return errcode;
    gen ad(evalf_double(a,1,contextptr)),bd(evalf_double(b,1,contextptr)),cd(evalf_double(correl2,1,contextptr));
    if (ad.type==_DOUBLE_ && bd.type==_DOUBLE_ && cd.type==_DOUBLE_){
      string eqs="y="+print_DOUBLE_(std::exp(ad._DOUBLE_val),3)+"^x*"+print_DOUBLE_(std::exp(bd._DOUBLE_val),3);
      string R2s=" , R2="+print_DOUBLE_(cd._DOUBLE_val,3);
      *logptr(contextptr) << eqs << R2s << endl;
      string s;
      if (eq)
	s += eqs;
      if (r)
	s += R2s;
      attributs.push_back(string2gen(s,false));
    }
    return put_attributs(_plotfunc(gen(makevecteur(evalf(exp(b,contextptr),1,contextptr)*exp(a*vx_var,contextptr),symb_equal(vx_var,symb_interval(xmin,xmax))),_SEQ__VECT),contextptr),attributs,contextptr);
  }
  static const char _exponential_regression_plot_s []="exponential_regression_plot";
static define_unary_function_eval (__exponential_regression_plot,&_exponential_regression_plot,_exponential_regression_plot_s);
  define_unary_function_ptr5( at_exponential_regression_plot ,alias_at_exponential_regression_plot,&__exponential_regression_plot,0,true);

  gen _logarithmic_regression_plot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen a,b,correl2;
    double xmin,xmax;
    vecteur attributs;
    bool eq,r;
    gen G=regression_plot_attributs(g,attributs,eq,r,contextptr);
    gen errcode=function_regression(G,at_ln,zero,a,b,xmin,xmax,correl2,contextptr);
    if (is_undef(errcode)) return errcode;
    xmax += (xmax-xmin); 
    gen ad(evalf_double(a,1,contextptr)),bd(evalf_double(b,1,contextptr)),cd(evalf_double(correl2,1,contextptr));
    if (ad.type==_DOUBLE_ && bd.type==_DOUBLE_ && cd.type==_DOUBLE_){
      string eqs="y=ln("+print_DOUBLE_(ad._DOUBLE_val,3)+"*x+"+print_DOUBLE_(bd._DOUBLE_val,3)+")";
      string R2s=" , R2="+print_DOUBLE_(cd._DOUBLE_val,3);
      *logptr(contextptr) << eqs << R2s << endl;
      string s;
      if (eq)
	s += eqs;
      if (r)
	s += R2s;
      attributs.push_back(string2gen(s,false));
    }
    return put_attributs(_plotfunc(gen(makevecteur(a*ln(vx_var,contextptr)+b,symb_equal(vx_var,symb_interval(xmin,xmax))),_SEQ__VECT),contextptr),attributs,contextptr);
  }
  static const char _logarithmic_regression_plot_s []="logarithmic_regression_plot";
static define_unary_function_eval (__logarithmic_regression_plot,&_logarithmic_regression_plot,_logarithmic_regression_plot_s);
  define_unary_function_ptr5( at_logarithmic_regression_plot ,alias_at_logarithmic_regression_plot,&__logarithmic_regression_plot,0,true);

  gen _power_regression_plot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen a,b,correl2;
    double xmin,xmax;
    vecteur attributs;
    bool eq,r;
    gen G=regression_plot_attributs(g,attributs,eq,r,contextptr);
    gen errcode=function_regression(G,at_ln,at_ln,a,b,xmin,xmax,correl2,contextptr);
    if (is_undef(errcode)) return errcode;
    xmax += (xmax-xmin); 
    gen ad(evalf_double(a,1,contextptr)),bd(evalf_double(b,1,contextptr)),cd(evalf_double(correl2,1,contextptr));
    if (ad.type==_DOUBLE_ && bd.type==_DOUBLE_ && cd.type==_DOUBLE_){
      string eqs="y="+print_DOUBLE_(exp(bd,contextptr)._DOUBLE_val,3)+"*x^"+print_DOUBLE_(ad._DOUBLE_val,3);
      string R2s=" , R2="+print_DOUBLE_(cd._DOUBLE_val,3);
      *logptr(contextptr) << eqs << R2s << endl;
      string s;
      if (eq)
	s += eqs;
      if (r)
	s += R2s;
      attributs.push_back(string2gen(s,false));
    }
    return put_attributs(_plotfunc(gen(makevecteur(exp(b,contextptr)*pow(vx_var,a,contextptr),symb_equal(vx_var,symb_interval(xmin,xmax))),_SEQ__VECT),contextptr),attributs,contextptr);
  }
  static const char _power_regression_plot_s []="power_regression_plot";
static define_unary_function_eval (__power_regression_plot,&_power_regression_plot,_power_regression_plot_s);
  define_unary_function_ptr5( at_power_regression_plot ,alias_at_power_regression_plot,&__power_regression_plot,0,true);

  static gen polynomial_regression(const gen & g,int d,const gen & u1, const gen & u2,double & xmin, double & xmax,GIAC_CONTEXT){
    xmin=1e300,xmax=-xmin;
    vecteur v(gen2vecteur(g));
    if (!ckmatrix(v) || v.empty() || v.front()._VECTptr->size()<2)
      return undef;
    // use first and second column
    const_iterateur it=v.begin(),itend=v.end();
    // int n(itend-it);
    gen sigmax,sigmay,sigmaxy,sigmax2,sigmay2,tmpx,tmpxd,tmpy;
    vecteur xmoment(2*d+1),xymoment(d+1);
    for (;it!=itend;++it){
      vecteur & w=*it->_VECTptr;
      if (u1.type==_FUNC)
	tmpx=u1(w.front(),contextptr);
      else
	tmpx=w.front();
      tmpxd=evalf_double(tmpx,1,contextptr);
      if (tmpxd.type==_DOUBLE_){
	double tmpxdd=tmpxd._DOUBLE_val;
	if (tmpxdd<xmin)
	  xmin=tmpxdd;
	if (tmpxdd>xmax)
	  xmax=tmpxdd;
      }
      if (u2.type==_FUNC)
	tmpy=u2(w.back(),contextptr);
      else
	tmpy=w.back();
      xmoment[0]=xmoment[0]+1;
      xymoment[0]=xymoment[0]+tmpy;
      for (int i=1;i<=2*d;++i)
	xmoment[i]=xmoment[i]+pow(tmpx,i);
      for (int i=1;i<=d;++i)
	xymoment[i]=xymoment[i]+pow(tmpx,i)*tmpy;      
    }
    // make linear system
    matrice mat;
    for (int i=0;i<=d;++i){
      vecteur tmp;
      for (int j=d;j>=0;--j){
	tmp.push_back(xmoment[i+j]);
      }
      mat.push_back(tmp);
    }
    // return multmatvecteur(minv(mat,contextptr),xymoment); 
    return linsolve(mat,xymoment,contextptr);
  }
  static gen polynomial_regression(const gen & g,double & xmin,double & xmax,GIAC_CONTEXT){
    if (g.type==_VECT && g._VECTptr->size()==3){
      vecteur & v=*g._VECTptr;
      if (v[0].type==_VECT && v[1].type==_VECT && v[0]._VECTptr->size()==v[1]._VECTptr->size())
	return polynomial_regression(makevecteur(mtran(makevecteur(v[0],v[1])),v[2]),xmin,xmax,contextptr);
    }
    gen last=_floor(g._VECTptr->back(),contextptr);
    if (g.type!=_VECT || g._VECTptr->size()!=2 || last.type!=_INT_)
      return gensizeerr(contextptr);
    return polynomial_regression(g._VECTptr->front(),absint(last.val),zero,zero,xmin,xmax,contextptr);
  }
  gen _polynomial_regression(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    double xmin,xmax;
    return polynomial_regression(g,xmin,xmax,contextptr);
  }
  static const char _polynomial_regression_s []="polynomial_regression";
static define_unary_function_eval (__polynomial_regression,&_polynomial_regression,_polynomial_regression_s);
  define_unary_function_ptr5( at_polynomial_regression ,alias_at_polynomial_regression,&__polynomial_regression,0,true);
  gen _polynomial_regression_plot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    double xmin,xmax;
    vecteur attributs;
    bool eq,r;
    gen G=regression_plot_attributs(g,attributs,eq,r,contextptr);
    gen res=polynomial_regression(G,xmin,xmax,contextptr);
    if (is_undef(res)) return res;
    xmax += (xmax-xmin); 
    res=horner(res,vx_var);
    return put_attributs(_plotfunc(gen(makevecteur(res,symb_equal(vx_var,symb_interval(xmin,xmax))),_SEQ__VECT),contextptr),attributs,contextptr);
  }
  static const char _polynomial_regression_plot_s []="polynomial_regression_plot";
static define_unary_function_eval (__polynomial_regression_plot,&_polynomial_regression_plot,_polynomial_regression_plot_s);
  define_unary_function_ptr5( at_polynomial_regression_plot ,alias_at_polynomial_regression_plot,&__polynomial_regression_plot,0,true);

  // logistic_regression
  // Qt=instant production at time t
  // Pt=cumulative production at time t
  // arg1=Qt_1...Qt_n, arg2=t1..tn or t1, arg3=Pt_1
  // or arg1=Qt_1...Qt_n, arg2=t1..tn or t1, n>=10, using Pt_1=Qt_1/(1-tau)
  // where tau is fitted from the first 5 records 
  static gen logistic_regression(const gen & g,double & xmin,double & xmax,gen & r,GIAC_CONTEXT){
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v = *g._VECTptr;
    int s=v.size();
    if (s<2 || s>3)
      return gendimerr(contextptr);
    gen data=v[0];
    if (data.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & w=*data._VECTptr;
    int n=w.size();
    gen Pinit;
    if (s==2){
      if (n<20)
	return gendimerr(gettext("Guessing initial production requires more than 20 samples"));
      gen args=gen(makevecteur(makevecteur(0,1,2,3,4),ln(vecteur(w.begin(),w.begin()+5),contextptr)),_SEQ__VECT);
      gen res=_linear_regression(args,contextptr);
      if (res.type!=_VECT || res._VECTptr->size()!=2)
	return gentypeerr(contextptr);
      gen tmp=_correlation(evalf_double(args,1,contextptr),contextptr);
      if (tmp.type==_STRNG && tmp.subtype==-1) return  tmp;
      Pinit=w[0]/(exp(res._VECTptr->front(),contextptr)-1);
      *logptr(contextptr) << gettext("Initial cumulative estimated to ") << Pinit << endl << gettext("Correlation for 5 first years to estimate initial cumulative : ") << tmp << endl;
    }
    else
      Pinit=v[2];
    gen time=v[1],tinit,tend;
    if (time.is_symb_of_sommet(at_interval)){
      gen tmp=time._SYMBptr->feuille;
      if (tmp.type!=_VECT || tmp._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      tinit=tmp._VECTptr->front();
      tend=tmp._VECTptr->back();
    }
    else {
      tinit=time;
      tend=time+int(w.size())-1;
    }
    tinit=evalf_double(tinit,1,contextptr);
    tend=evalf_double(tend,1,contextptr);
    if (tinit.type!=_DOUBLE_ || tend.type!=_DOUBLE_)
      return gensizeerr(contextptr);
    xmin = tinit._DOUBLE_val;
    xmax = tend._DOUBLE_val;
    gen tscale=(tend+1-tinit)/n;
    // compute cumulated production
    vecteur cum(n),quot(n);
    cum[0]=Pinit+w[0];
    quot[0]=w[0]/cum[0];
    for (int i=1;i<n;++i){
      cum[i]=cum[i-1]+w[i];
      quot[i]=w[i]/cum[i];
    }
    // linear regression of quot vs cum
    gen args=gen(makevecteur(cum,quot),_SEQ__VECT);
    gen res=_linear_regression(args,contextptr);
    r=_correlation(args,contextptr);
    if (r.type==_STRNG && r.subtype==-1) return  r;
    if (res.type!=_VECT || res._VECTptr->size()!=2)
      return gendimerr(contextptr);
    gen a=res._VECTptr->front(),b=res._VECTptr->back(),urr=-b/a;
    *logptr(contextptr) << gettext("Pinstant=") << a << gettext("*Pcumul+") << b << endl << gettext("Correlation ") << r << gettext(", Estimated total P=") << urr << endl << gettext("Returning estimated Pcumul, Pinstant, Ptotal, Pinstantmax, tmax, R")<< endl;
    // y'/y=a*y+b -> y=urr/[1+exp(-b*(t-t0))]
    // urr/y-1=exp(-b*(t-t0))
    // -> -b*(t-t0) = ln(urr/y-1)
    vecteur lnurr(n),t(n);
    for (int i=0;i<n;++i){
      lnurr[i]=ln(urr/cum[i]-1,contextptr);
      t[i]=tinit+i*tscale;
    }
    args=gen(makevecteur(t,lnurr),_SEQ__VECT);
    res=_linear_regression(args,contextptr);
    if (res.type!=_VECT || res._VECTptr->size()!=2)
      return gendimerr(contextptr);
    gen b2=res._VECTptr->front(),bt0=res._VECTptr->back();
    return makevecteur(urr/(1+exp(b2*vx_var+bt0,contextptr)),urr*b/2/(1+cosh(b2*vx_var+bt0,contextptr)),urr,urr*b/4,-bt0/b2,r);
  }

  gen _logistic_regression(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    double xmin,xmax;
    gen r;
    return logistic_regression(g,xmin,xmax,r,contextptr);
  }
  static const char _logistic_regression_s []="logistic_regression";
static define_unary_function_eval (__logistic_regression,&_logistic_regression,_logistic_regression_s);
  define_unary_function_ptr5( at_logistic_regression ,alias_at_logistic_regression,&__logistic_regression,0,true);

  gen _logistic_regression_plot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    double xmin,xmax;
    vecteur attributs;
    bool eq,r;
    gen rcorr;
    gen G=regression_plot_attributs(g,attributs,eq,r,contextptr);
    gen res=logistic_regression(G,xmin,xmax,rcorr,contextptr);
    if (res.type==_STRNG && res.subtype==-1) return res;
    if (r){
      rcorr=rcorr*rcorr;
      string s = "R2="+rcorr.print(contextptr);
      attributs.push_back(string2gen(s,false));
    }
    xmax += (xmax-xmin); 
    if (res.type!=_VECT || res._VECTptr->empty())
      return gensizeerr(contextptr);
    res=res[1];
    return put_attributs(_plotfunc(gen(makevecteur(res,symb_equal(vx_var,symb_interval(xmin,xmax))),_SEQ__VECT),contextptr),attributs,contextptr);
  }
  static const char _logistic_regression_plot_s []="logistic_regression_plot";
static define_unary_function_eval (__logistic_regression_plot,&_logistic_regression_plot,_logistic_regression_plot_s);
  define_unary_function_ptr5( at_logistic_regression_plot ,alias_at_logistic_regression_plot,&__logistic_regression_plot,0,true);

  static gen gen_interpolate(const gen & g,int deg,GIAC_CONTEXT){
    // args = matrix with 2 rows (x,y), xmin, xmax, xstep -> matrix of [x,y]
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v =*g._VECTptr;
    int s=v.size();
    if (s<4)
      return gensizeerr(contextptr);
    gen m=evalf_double(v[0],1,contextptr),
      Xmin=evalf_double(v[1],1,contextptr),
      Xmax=evalf_double(v[2],1,contextptr),
      Xstep=evalf_double(v[3],1,contextptr);
    if (!ckmatrix(m) || m._VECTptr->size()!=2 || Xmin.type!=_DOUBLE_ || Xmax.type!=_DOUBLE_ || Xstep.type!=_DOUBLE_)
      return gensizeerr(contextptr);
    double xmin=Xmin._DOUBLE_val,xmax=Xmax._DOUBLE_val,xstep=std::abs(Xstep._DOUBLE_val);
    // sort x in m
    matrice M(mtran(*m._VECTptr)); // 2 cols
    sort(M.begin(),M.end(),islesscomplexthanf);
    reverse(M.begin(),M.end());
    int Ms=M.size();
    if (Ms<2)
      return gendimerr(contextptr);
    gen X0=M[0]._VECTptr->front(),X1=M[Ms-1]._VECTptr->front();
    if (X0.type!=_DOUBLE_ || X1.type!=_DOUBLE_)
      return gensizeerr(contextptr);
    double x0=X0._DOUBLE_val,x1=X1._DOUBLE_val;
    if (xmin<x0 || xmax>x1)
      return gensizeerr(gettext("Values out of range"));
    matrice res;
    int pos=0; 
    gen Mcur=Xmin,Mnext=M[1]._VECTptr->front(),
      Ycur=M[0]._VECTptr->back(),Ynext=M[1]._VECTptr->back();
    double ycur;
    if (deg==1){
      for (double xcur=xmin;xcur<=xmax;xcur+=xstep){
	// find interval containing xcur in matrix
	for (;;){
	  if (Mnext._DOUBLE_val>xcur)
	    break;
	  ++pos;
	  if (pos==Ms)
	    break;
	  Mcur=M[pos]._VECTptr->front();
	  Ycur=M[pos]._VECTptr->back();
	  if (pos!=Ms-1){
	    Mnext = M[pos+1]._VECTptr->front();
	    Ynext = M[pos+1]._VECTptr->back();
	  }
	}
	if (pos>=Ms-1){ // use Ycur
	  res.push_back(makevecteur(xcur,Ycur));
	}
	else {
	  ycur = Ycur._DOUBLE_val+(xcur-Mcur._DOUBLE_val)/(Mnext._DOUBLE_val-Mcur._DOUBLE_val)*(Ynext._DOUBLE_val-Ycur._DOUBLE_val);
	  res.push_back(makevecteur(xcur,ycur));
	}
      }
    }
    else {
      vecteur current(deg+1); // contains the current Taylor expansion
      current[deg]=Ycur;
      // find z=current[0]: z*(Mnext-Mcur)^deg+Ycur=Ynext
      current[0]=(Ynext-Ycur)/pow(Mnext-Mcur,deg);
      for (double xcur=xmin;xcur<=xmax;xcur+=xstep){
	if (xcur>Mnext._DOUBLE_val){ // translate current, modify current[0]
	  current=taylor(current,Mnext-Mcur);
	  current[0]=0;
	  Ycur=Ynext;
	  Mcur=Mnext;
	  ++pos;
	  if (pos<Ms-1){
	    Mnext = M[pos+1]._VECTptr->front();
	    Ynext = M[pos+1]._VECTptr->back();
	    current[0]=(Ynext-horner(current,Mnext-Mcur))/pow(Mnext-Mcur,deg);
	  }
	}
	ycur=horner(current,xcur-Mcur)._DOUBLE_val;
	res.push_back(makevecteur(xcur,ycur));
      }
    }
    return mtran(res);
  }
  gen _linear_interpolate(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return gen_interpolate(g,1,contextptr);
  }
  static const char _linear_interpolate_s []="linear_interpolate";
static define_unary_function_eval (__linear_interpolate,&_linear_interpolate,_linear_interpolate_s);
  define_unary_function_ptr5( at_linear_interpolate ,alias_at_linear_interpolate,&__linear_interpolate,0,true);

  gen _parabolic_interpolate(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return gen_interpolate(g,2,contextptr);
  }
  static const char _parabolic_interpolate_s []="parabolic_interpolate";
static define_unary_function_eval (__parabolic_interpolate,&_parabolic_interpolate,_parabolic_interpolate_s);
  define_unary_function_ptr5( at_parabolic_interpolate ,alias_at_parabolic_interpolate,&__parabolic_interpolate,0,true);

  static vector<double> prepare_effectifs(const vecteur & v,GIAC_CONTEXT){
    if (v.empty())
      return vector<double>(0);
    vecteur w;
    if (ckmatrix(v)){
      int s=v.front()._VECTptr->size();
      if (s==1)
	w=*evalf_double(mtran(v)[0],1,contextptr)._VECTptr;
      else
	return vector<double>(0);
    }
    else
      w=*evalf_double(v,1,contextptr)._VECTptr;
    // vector will be sorted keeping only DOUBLE data
    int s=w.size();
    vector<double> w1;
    w1.reserve(s);
    for (int i=0;i<s;++i){
      if (w[i].type==_DOUBLE_)
	w1.push_back(w[i]._DOUBLE_val);
    }
    sort(w1.begin(),w1.end());
    s=w1.size();
    if (!s)
      return vector<double>(0);
    return w1;
  }
  matrice effectifs(const vecteur & data,double class_minimum,double class_size,GIAC_CONTEXT){
    if (data.empty())
      return data;
    if (class_size<=0){
      *logptr(contextptr) << gettext("Invalid class size (replaced by 1) ") << class_size << endl;
      class_size=1;
    }
    vector<double>  w1;
    if (ckmatrix(data)){
      if (!data.empty() && data.front()._VECTptr->size()>1){
	matrice tmp=data;
	sort(tmp.begin(),tmp.end(),first_ascend_sort);
	tmp=mtran(tmp);
	vecteur tmpval=*evalf_double(tmp[0],1,contextptr)._VECTptr;
	vecteur tmpeff=*tmp[1]._VECTptr;
	if (tmpval.front().type!=_DOUBLE_ || tmpval.back().type!=_DOUBLE_)
	  return vecteur(1,undef);
	double kbegin=floor((tmpval.front()._DOUBLE_val-class_minimum)/class_size);
	double kend=floor((tmpval.back()._DOUBLE_val-class_minimum)/class_size);
	int s=tmpval.size(),i=0;
	vecteur res;
	for (;kbegin<=kend;++kbegin){
	  // count in this class
	  double min_class=kbegin*class_size+class_minimum;
	  double max_class=min_class+class_size;
	  gen effectif;
	  for (;i<s;effectif=effectif+tmpeff[i],++i){
	    if (tmpval[i].type!=_DOUBLE_)
	      return vecteur(1,undef);
	    if (tmpval[i]._DOUBLE_val>=max_class)
	      break;
	  }
	  res.push_back(makevecteur(symbolic(at_interval,makesequence(min_class,max_class)),effectif));
	}
	return res;
      }
      w1=prepare_effectifs(*mtran(data)[0]._VECTptr,contextptr);
    }
    else
      w1=prepare_effectifs(data,contextptr);
    if (w1.empty())
      return vecteur(1,undef);
    // class_min + k*class_size <= mini hence k
    double kbegin=floor((w1.front()-class_minimum)/class_size);
    double kend=floor((w1.back()-class_minimum)/class_size);
    if (kend-kbegin>LIST_SIZE_LIMIT)
      return vecteur(1,gendimerr("Too many classes"));
    vector<double>::const_iterator it=w1.begin(),itend=w1.end();
    vecteur res;
    for (;kbegin<=kend;++kbegin){
      // count in this class
      double min_class=kbegin*class_size+class_minimum;
      double max_class=min_class+class_size;
      int effectif=0;
      for (;it!=itend;++it,++effectif){
	if (*it>=max_class)
	  break;
      }
      res.push_back(makevecteur(symbolic(at_interval,makesequence(min_class,max_class)),effectif));
    }
    return res;
  }

  static matrice effectifs(const vecteur & data,const vecteur & intervalles,GIAC_CONTEXT){
    int s=intervalles.size();
    matrice res(s);
    vector<double> sorted_data;
    if (ckmatrix(data))
      sorted_data=prepare_effectifs(*mtran(data)[0]._VECTptr,contextptr);
    else
      sorted_data=prepare_effectifs(data,contextptr);
    if (sorted_data.empty())
      return vecteur(1,undef);
    vector<double>::const_iterator it=sorted_data.begin(),itend=sorted_data.end();
    for (int i=0;i<s;++i){
      gen cur_intervalle=intervalles[i];
      double debut,fin;
      if (!chk_double_interval(cur_intervalle,debut,fin,contextptr))
	return vecteur(1,undef);
      for (;it!=itend;++it){
	if (*it>=debut)
	  break;
      }
      int effectif=0;
      for (;it!=itend;++it,++effectif){
	if (*it>=fin)
	  break;
      }
      res[i]=makevecteur(cur_intervalle,effectif);
    }
    return res;
  }

  static vecteur centres2intervalles(const vecteur & centres,double class_min,bool with_class_min,GIAC_CONTEXT){
    if (centres.size()<2)
      return vecteur(1,gensizeerr(contextptr));
    double d0=evalf_double(centres[0],1,contextptr)._DOUBLE_val,d1=evalf_double(centres[1],1,contextptr)._DOUBLE_val;
    double debut=class_min;
    if (!with_class_min)
      debut=d0+(d0-d1)/2;
    vecteur res;
    const_iterateur it=centres.begin(),itend=centres.end();
    res.reserve(itend-it);
    for (;it!=itend;++it){
      gen g=evalf_double(*it,1,contextptr);
      if (g.type!=_DOUBLE_)
	return vecteur(1,gensizeerr(contextptr));
      double milieu=g._DOUBLE_val;
      double fin=milieu+(milieu-debut);
      if (fin<=debut)
	return vecteur(1,gensizeerr(contextptr));
      res.push_back(symb_interval(debut,fin));
      debut=fin;
    }
    return res;
  }

  gen _center2interval(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gentypeerr(contextptr);
    if (g.subtype==_SEQ__VECT && g._VECTptr->size()==2){
      vecteur & v=*g._VECTptr;
      gen c=evalf_double(v[1],1,contextptr);
      if (v[0].type!=_VECT || c.type!=_DOUBLE_)
	return gentypeerr(contextptr);
      return gen(centres2intervalles(*v[0]._VECTptr,c._DOUBLE_val,true,contextptr),_SEQ__VECT);
    }
    return gen(centres2intervalles(*g._VECTptr,0.0,false,contextptr),_SEQ__VECT);
  }
  static const char _center2interval_s []="center2interval";
static define_unary_function_eval (__center2interval,&_center2interval,_center2interval_s);
  define_unary_function_ptr5( at_center2interval ,alias_at_center2interval,&__center2interval,0,true);


  static gen histogram(const vecteur & v,double class_minimum,double class_size,const vecteur & attributs,GIAC_CONTEXT){
#ifndef WIN32
    bool old_iograph=io_graph(contextptr);
    io_graph(false,contextptr);
#endif
    if (ckmatrix(v) && !v.empty() && v.front()._VECTptr->size()==2){
      // matrix format is 2 columns 1st column=interval, 2nd column=frequency
      // OR value/frequencies
      // get total of population
      const_iterateur it=v.begin(),itend=v.end();
      double n=0;
      for (;it!=itend;++it)
	n += evalf_double(it->_VECTptr->back(),1,contextptr)._DOUBLE_val;
      // get surface
      gen g=v.front()._VECTptr->front();
      if (g.is_symb_of_sommet(at_interval)){
	g=g._SYMBptr->feuille;
	if (g.type!=_VECT || g._VECTptr->size()!=2)
	  return gentypeerr(contextptr);
	g=evalf_double(g._VECTptr->front(),1,contextptr);
      }
      else
	g=g-class_size/2;
      gen h=(itend-1)->_VECTptr->front();
      if (h.is_symb_of_sommet(at_interval)){
	h=h._SYMBptr->feuille;
	if (h.type!=_VECT || h._VECTptr->size()!=2)
	  return gentypeerr(contextptr);
	h=evalf_double(h._VECTptr->back(),1,contextptr);
      }
      else
	h=h+class_size/2;
      if (g.type!=_DOUBLE_ || h.type!=_DOUBLE_ || g._DOUBLE_val>=h._DOUBLE_val)
	return gensizeerr(contextptr);
      double inf,sup; // delta=h._DOUBLE_val-g._DOUBLE_val;
      it=v.begin();
      //  int nclass=itend-it;
      vecteur res;
      for (;it!=itend;++it){
	gen current=it->_VECTptr->front();
	if (current.is_symb_of_sommet(at_interval)){
	  if (!chk_double_interval(current,inf,sup,contextptr))
	    return gentypeerr(contextptr);
	}
	else {
	  gen tmp=evalf_double(current,1,contextptr);
	  if (tmp.type!=_DOUBLE_)
	    return gentypeerr(contextptr);
	  inf = tmp._DOUBLE_val -class_size/2;
	  sup = tmp._DOUBLE_val + class_size/2;
	}
	double height=1/(sup-inf);
	height=height*evalf_double(it->_VECTptr->back(),1,contextptr)._DOUBLE_val/n;
	gen mini(inf,height),maxi(sup,height);
	gen rectan(makevecteur(inf,sup,maxi,mini,inf),_LINE__VECT);
	res.push_back(pnt_attrib(rectan,attributs,contextptr));
	// res.push_back(_segment(makevecteur(inf,mini),contextptr));
	// res.push_back(_segment(makevecteur(mini,maxi),contextptr));
	// res.push_back(_segment(makevecteur(maxi,sup),contextptr));	    
      }
#ifndef WIN32
    io_graph(old_iograph,contextptr);
#endif
      return res;
    }
    vector<double>  w1=prepare_effectifs(v,contextptr);
    int s=w1.size();
    if (!s)
      return gendimerr(contextptr);
    // class_min + k*class_size <= mini hence k
    double kbegin=floor((w1.front()-class_minimum)/class_size);
    double kend=floor((w1.back()-class_minimum)/class_size);
    vector<double>::const_iterator it=w1.begin(),itend=w1.end();
    vecteur res;
    for (;kbegin<=kend;++kbegin){
      // count in this class
      double min_class=kbegin*class_size+class_minimum;
      double max_class=min_class+class_size;
      double effectif=0;
      for (;it!=itend;++it,++effectif){
	if (*it>=max_class)
	  break;
      }
      effectif /= s*class_size; // height of the class
      gen ming=min_class+gen(0.0,effectif);
      gen maxg=max_class+gen(0.0,effectif);
      gen rectan(makevecteur(min_class,max_class,maxg,ming,min_class),_LINE__VECT);
      res.push_back(pnt_attrib(rectan,attributs,contextptr));
      // res.push_back(_segment(makevecteur(min_class,ming),contextptr));
      // res.push_back(_segment(makevecteur(ming,maxg),contextptr));
      // res.push_back(_segment(makevecteur(maxg,max_class),contextptr));
    }
#ifndef WIN32
    io_graph(old_iograph,contextptr);
#endif
    return res; // gen(res,_SEQ__VECT);
  }
  gen _histogram(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur args;
    if (g.subtype==_SEQ__VECT)
      args=*g._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(args,attributs,contextptr);
    args=vecteur(args.begin(),args.begin()+s);
    if (s>=2){
      if (args[0].type!=_VECT)
	return gensizeerr(contextptr);
      vecteur data=*args[0]._VECTptr;
      gen arg1=evalf_double(args[1],1,contextptr);
      if (ckmatrix(data)&&arg1.type==_DOUBLE_){ // [ [center, effectif] ... ], min
	data=mtran(data); // 1st line = all centers
	if (data.size()!=2)
	  return gensizeerr(contextptr);
	data[0]=centres2intervalles(*data[0]._VECTptr,arg1._DOUBLE_val,true,contextptr);
	if (is_undef(data[0]))
	  return gensizeerr(contextptr);
	data=mtran(data);
	return histogram(data,0.0,0.0,attributs,contextptr);
      }
      if (s==3){
	gen arg2=evalf_double(args[2],1,contextptr);
	if (arg1.type==_DOUBLE_ && arg2.type==_DOUBLE_)
	  return histogram(data,arg1._DOUBLE_val,arg2._DOUBLE_val,attributs,contextptr);
      }
      if (s==2 && args[1].type==_VECT)
	return _histogram(gen(makevecteur(mtran(args),class_minimum),_SEQ__VECT),contextptr);
      return gensizeerr(contextptr);
    }
    if (s==1 && args.front().type==_VECT)
      args=*args.front()._VECTptr;
    else
      args=gen2vecteur(g);
    if (ckmatrix(args)){
      gen tmp=args[0];
      if (tmp._VECTptr->size()==2 && !tmp._VECTptr->front().is_symb_of_sommet(at_interval)){
	vecteur data=mtran(args); // 1st line = all centers
	if (data.size()!=2)
	  return gensizeerr(contextptr);
	data[0]=centres2intervalles(*data[0]._VECTptr,0,false,contextptr);
	if (is_undef(data[0]))
	  return gensizeerr(contextptr);
	data=mtran(data);
	return histogram(data,0.0,0.0,attributs,contextptr);
      }
    }
    return histogram(args,class_minimum,class_size,attributs,contextptr);
  }
  static const char _histogram_s []="histogram";
static define_unary_function_eval (__histogram,&_histogram,_histogram_s);
  define_unary_function_ptr5( at_histogram ,alias_at_histogram,&__histogram,0,true);

  struct xeff {
    double x;
    double eff;
    xeff(): x(0),eff(0) {}
    xeff(double x0,double eff0): x(x0),eff(eff0) {}
  };

  bool operator <(const xeff & a,const xeff & b){
    return a.x<b.x;
  }

  vecteur frequencies(const gen & v,GIAC_CONTEXT){
    gen g(_sort(v,contextptr));
    if (g.type!=_VECT)
      return vecteur(1,g);
    vecteur & w = *g._VECTptr;
    double total=w.size();
    vecteur res;
    gen current=w[0]; unsigned count=1;
    for (unsigned i=1;i<w.size();++i){
      if (w[i]!=current){
	res.push_back(makevecteur(current,count/total));
	current=w[i];
	count=0;
      }
      ++count;
    }
    res.push_back(makevecteur(current,count/total));
    return res;
  }
  gen _frequencies(const gen & g,GIAC_CONTEXT){
    gen h=evalf_double(g,1,contextptr);
    if (h.type!=_VECT || !is_numericv(*h._VECTptr) || h._VECTptr->empty())
      return gensizeerr(contextptr);
    return frequencies(g,contextptr);
  }
  static const char _frequencies_s []="frequencies";
  static define_unary_function_eval (__frequencies,&_frequencies,_frequencies_s);
  define_unary_function_ptr5( at_frequencies ,alias_at_frequencies,&__frequencies,0,true);

  gen _cumulated_frequencies(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen g0(g);
    double class_min=class_minimum,class_s=class_size;
    if (g0.type==_VECT && g0.subtype==_SEQ__VECT && g0._VECTptr->size()==2){
      vecteur v = *g._VECTptr;
      gen tmp=evalf_double(v[1],1,contextptr);
      if (tmp.type!=_DOUBLE_) {
	if (ckmatrix(g)){
	  if (!v[0]._VECTptr->front().is_symb_of_sommet(at_interval))
	    v[0]=centres2intervalles(*v[0]._VECTptr,class_min,true,contextptr);
	  if (is_undef(v[0]))
	    return gensizeerr(contextptr);
	  g0=mtran(v);
	}
      }
      else {
	g0=v[0];
	class_min=tmp._DOUBLE_val;
      }
    }
    if (!ckmatrix(g0)){
      gen h=evalf_double(g0,1,contextptr);
      if (h.type!=_VECT || !is_numericv(*h._VECTptr) || h._VECTptr->empty())
	return gensizeerr(contextptr);
      g0=frequencies(g0,contextptr);
    }
    // 1st column = values (or classes), 2nd column = effectif
    matrice m= *g0._VECTptr ;
    if (m.empty() || m[0]._VECTptr->size()<2)
      return gensizeerr(contextptr);
    int s=m[0]._VECTptr->size();
    vecteur ans;
    for (int k=1;k<s;++k){
      // compute total eff
      iterateur it=m.begin(),itend=m.end();
      vector<xeff> veff;
      double n=0,x=0;
      if (it !=itend && it->_VECTptr->front().is_symb_of_sommet(at_interval)){
	gen tmp=it->_VECTptr->front()._SYMBptr->feuille;
	if (tmp.type!=_VECT || tmp._VECTptr->size()!=2)
	  return gensizeerr(contextptr);
	else
	  tmp=tmp._VECTptr->front();
	tmp=evalf_double(tmp,1,contextptr);
	if (tmp.type!=_DOUBLE_)
	  return gensizeerr(contextptr);
	veff.push_back(xeff(tmp._DOUBLE_val,0));
      }
      else
	veff.push_back(xeff(class_min,0));
      for (;it!=itend;++it){
	vecteur & v = *it->_VECTptr;
	gen tmp=evalf_double(v[k],1,contextptr);
	if (tmp.type!=_DOUBLE_)
	  return gensizeerr(contextptr);
	// class_s = tmp._DOUBLE_val - x;
	n = n + (x=tmp._DOUBLE_val) ;
	if (v.front().is_symb_of_sommet(at_interval)){
	  tmp=v.front()._SYMBptr->feuille;
	  if (tmp.type!=_VECT || tmp._VECTptr->size()!=2)
	    return gensizeerr(contextptr);
	  else
	    tmp=tmp._VECTptr->back();
	}
	else
	  tmp=v.front(); // +class_s/2; // FIX 30/11/2012 for e.g. cumulated_frequencies([[1,0.3],[2,0.5],[3,0.2]])
	tmp=evalf_double(tmp,1,contextptr);
	if (tmp.type!=_DOUBLE_)
	  return gensizeerr(contextptr);
	veff.push_back(xeff(tmp._DOUBLE_val,x));
      }
      sort(veff.begin(),veff.end());
      vecteur res;
      vector<xeff>::const_iterator jt=veff.begin(),jtend=veff.end();
      double cumul=0;
      for (;jt!=jtend;++jt){
	cumul += jt->eff/n ;
	res.push_back(gen(jt->x)+cst_i*gen(cumul));
      }
      ans.push_back(symb_pnt(gen(res,_GROUP__VECT),k,contextptr));
    }
    return gen(ans,_SEQ__VECT);
  }
  static const char _cumulated_frequencies_s []="cumulated_frequencies";
  static define_unary_function_eval (__cumulated_frequencies,&_cumulated_frequencies,_cumulated_frequencies_s);
  define_unary_function_ptr5( at_cumulated_frequencies ,alias_at_cumulated_frequencies,&__cumulated_frequencies,0,true);

  // classes(vector or column matrix,begin of class, class size)
  // "      ( "                     ,list of intervals)
  // "      ( "                     ,list of centers,begin of 1st class)
  gen _classes(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT && g.subtype==_SEQ__VECT){
      vecteur & args=*g._VECTptr;
      int s=args.size();
      if (s<2)
	return gensizeerr(contextptr);
      if (args[0].type!=_VECT)
	return gensizeerr(contextptr);
      vecteur data=*args[0]._VECTptr;
      if (s==2 && args[1].type==_VECT){ // 2nd arg=list of intervals
	return effectifs(data,*args[1]._VECTptr,contextptr);
      }
      if (s==3){
	gen arg2=evalf_double(args[2],1,contextptr);
	gen arg1=evalf_double(args[1],1,contextptr);
	if (args[2].type==_VECT && arg1.type==_DOUBLE_){
	  vecteur tmp=centres2intervalles(*args[2]._VECTptr,0.0,false,contextptr);
	  if (is_undef(tmp))
	    return gensizeerr(contextptr);
	  return effectifs(data,tmp,contextptr);
	}
	if (arg1.type==_DOUBLE_ && arg2.type==_DOUBLE_)
	  return effectifs(data,arg1._DOUBLE_val,arg2._DOUBLE_val,contextptr);
      }
      return gensizeerr(contextptr);
    }
    vecteur v(gen2vecteur(g));
    return effectifs(v,class_minimum,class_size,contextptr);
  }
  static const char _classes_s []="classes";
  static define_unary_function_eval (__classes,&_classes,_classes_s);
  define_unary_function_ptr5( at_classes ,alias_at_classes,&__classes,0,true);

  static vecteur listplot(const gen & g,vecteur & attributs,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->empty())
      return vecteur(1,gensizeerr(contextptr));
    int s=read_attributs(*g._VECTptr,attributs,contextptr);
    vecteur v;
    if (g.subtype==_SEQ__VECT && s>=4 && g[1].type==_IDNT)
      return listplot(_seq(g,contextptr),attributs,contextptr);
    if (s>=2 && g._VECTptr->front().type<=_DOUBLE_ && g[1].type==_VECT){
      int l=g[1]._VECTptr->size();
      v=*g._VECTptr;
      v[0]=vecteur(l);
      double d=evalf_double(g._VECTptr->front(),1,contextptr)._DOUBLE_val;
      for (int j=0;j<l;++j){
	(*v[0]._VECTptr)[j]=j+d;
      }
      if (!ckmatrix(v))
	return vecteur(1,gendimerr(contextptr));
      v=mtran(v);
    }
    else {
      if (g._VECTptr->front().type==_VECT){
	vecteur & v0 = *g._VECTptr->front()._VECTptr;
	int v0s=v0.size();
	if (s==1)
	  v=v0;
	else {
	  if (v0s==1 && ckmatrix(g))
	    v=*mtran(*g._VECTptr).front()._VECTptr;
	  else
	    v=*g._VECTptr;
	}
      }
      else
	v=*g._VECTptr;
    }
    s=v.size();
    vecteur res;
    res.reserve(s);
    for (int i=0;i<s;++i){
      gen tmp=v[i];
      if (tmp.type==_VECT){
	if (tmp._VECTptr->size()==2)
	  res.push_back(tmp._VECTptr->front()+cst_i*tmp._VECTptr->back());
	else
	  return vecteur(1,gendimerr(contextptr));
      }
      else
	res.push_back(i+(xcas_mode(contextptr)?1:0)+cst_i*tmp);
    }
    return res;
  }

  gen _listplot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur attributs(1,default_color(contextptr));
    vecteur res=listplot(g,attributs,contextptr);
    if (is_undef(res) && !res.empty())
      return res.front();
    if (attributs.size()>1)
      return symb_pnt_name(gen(res,_GROUP__VECT),attributs[0],attributs[1],contextptr);
    else
      return symb_pnt(gen(res,_GROUP__VECT),attributs[0],contextptr);
  }
  static const char _listplot_s []="listplot";
static define_unary_function_eval (__listplot,&_listplot,_listplot_s);
  define_unary_function_ptr5( at_listplot ,alias_at_listplot,&__listplot,0,true);
  static const char _plotlist_s []="plotlist";
static define_unary_function_eval (__plotlist,&_listplot,_plotlist_s);
  define_unary_function_ptr5( at_plotlist ,alias_at_plotlist,&__plotlist,0,true);

  // [[x1 y1] [x2 y2] ...]
  static gen scatterplot(const gen & g,bool polygone,bool scatter,GIAC_CONTEXT){
    vecteur v(gen2vecteur(g));
    vecteur attr(1,default_color(contextptr));
    int s=read_attributs(v,attr,contextptr);
    if (s==1 && ckmatrix(v.front()))
      v=*v.front()._VECTptr;
    else
      v=vecteur(v.begin(),v.begin()+s);
    if (g.type==_VECT && g.subtype==_SEQ__VECT && s==2){
      if (!ckmatrix(v))
	return gensizeerr(contextptr); 
      v=mtran(v);
    }
    unsigned ncol=0;
    const gen & vf=v.front();
    if (vf.type!=_VECT){
      if (polygone)
	return _listplot(g,contextptr);
      vecteur attributs(1,default_color(contextptr));
      vecteur res=listplot(g,attributs,contextptr);
      int s=res.size();
      for (int i=0;i<s;++i){
	res[i]=symb_pnt(res[i],attributs[0],contextptr);
      }
      return gen(res,_SEQ__VECT);
    }
    if (!ckmatrix(v)||v.empty() || (ncol=vf._VECTptr->size())<2)
      return gensizeerr(contextptr);
    if (vf._VECTptr->front().type==_STRNG){
      if (attr.size()==1)
	attr.push_back(vecteur(vf._VECTptr->begin()+1,vf._VECTptr->end()));
      v.erase(v.begin());
    }
#ifndef WIN32
    bool old_iograph=io_graph(contextptr);
    io_graph(false,contextptr);
#endif
    const_iterateur it=v.begin(),itend=v.end();
    stable_sort(v.begin(),v.end(),first_ascend_sort);
    vecteur res;
    string nullstr;
    vecteur vres;
    for (unsigned j=1;j<ncol;++j){
      vecteur attributs(1,int(j<=FL_WHITE?j-1:j));
      attributs.push_back(string2gen("",false));
      if (!attr.empty()){ 
	if (ncol==2)
	  attributs[0]=attr[0];
	if (attr[0].type==_VECT && attr[0]._VECTptr->size()>=j)
	  attributs[0]=(*attr[0]._VECTptr)[j-1];
	if (attr.size()>1){
	  if (ncol==2)
	    attributs[1]=attr[1];
	  if (attr[1].type==_VECT && attr[1]._VECTptr->size()>=j)
	    attributs[1]=(*attr[1]._VECTptr)[j-1];
	}
      }
      res.clear();
      for (it=v.begin();it!=itend;++it){
	gen tmp=(*it->_VECTptr)[j];
	if (tmp.type==_STRNG && attributs[1].type==_STRNG && *attributs[1]._STRNGptr==nullstr)
	  attributs[1]=gen(*tmp._STRNGptr,contextptr);
	else {
	  if (tmp.is_symb_of_sommet(at_equal))
	    read_attributs(vecteur(1,tmp),attributs,contextptr);
	  else {
	    if (polygone)
	      res.push_back(it->_VECTptr->front()+cst_i*tmp);
	    if (scatter)
	      vres.push_back(symb_pnt_name(it->_VECTptr->front()+cst_i*tmp,attributs[0],string2gen(( (it==v.begin() && !polygone) ?gen2string(attributs[1]):""),false),contextptr));
	  }
	}
      }
      if (polygone)
	vres.push_back(symb_pnt_name(res,attributs[0],attributs[1],contextptr));
    }
#ifndef WIN32
    io_graph(old_iograph,contextptr);
#endif
    if (polygone && !scatter && ncol==2)
      return vres.front();
    return gen(vres,_SEQ__VECT);
  }
  gen _scatterplot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return scatterplot(g,false,true,contextptr);
  }
  static const char _scatterplot_s []="scatterplot";
static define_unary_function_eval (__scatterplot,&_scatterplot,_scatterplot_s);
  define_unary_function_ptr5( at_scatterplot ,alias_at_scatterplot,&__scatterplot,0,true);

  static const char _nuage_points_s []="nuage_points";
static define_unary_function_eval (__nuage_points,&_scatterplot,_nuage_points_s);
  define_unary_function_ptr5( at_nuage_points ,alias_at_nuage_points,&__nuage_points,0,true);

  gen _polygonplot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return scatterplot(g,true,false,contextptr);
  }
  static const char _polygonplot_s []="polygonplot";
static define_unary_function_eval (__polygonplot,&_polygonplot,_polygonplot_s);
  define_unary_function_ptr5( at_polygonplot ,alias_at_polygonplot,&__polygonplot,0,true);

  static const char _ligne_polygonale_s []="ligne_polygonale";
static define_unary_function_eval (__ligne_polygonale,&_polygonplot,_ligne_polygonale_s);
  define_unary_function_ptr5( at_ligne_polygonale ,alias_at_ligne_polygonale,&__ligne_polygonale,0,true);

  gen _polygonscatterplot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return scatterplot(g,true,true,contextptr);
  }
  static const char _polygonscatterplot_s []="polygonscatterplot";
static define_unary_function_eval (__polygonscatterplot,&_polygonscatterplot,_polygonscatterplot_s);
  define_unary_function_ptr5( at_polygonscatterplot ,alias_at_polygonscatterplot,&__polygonscatterplot,0,true);

  static const char _ligne_polygonale_pointee_s []="ligne_polygonale_pointee";
static define_unary_function_eval (__ligne_polygonale_pointee,&_polygonscatterplot,_ligne_polygonale_pointee_s);
  define_unary_function_ptr5( at_ligne_polygonale_pointee ,alias_at_ligne_polygonale_pointee,&__ligne_polygonale_pointee,0,true);

  static gen read_camembert_args(const gen & g,vecteur & vals,vecteur & names,vecteur & attributs,GIAC_CONTEXT){
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    attributs=vecteur(1,default_color(contextptr) | _FILL_POLYGON);
    int s=read_attributs(*g._VECTptr,attributs,contextptr);
    gen args=(s==1)?g._VECTptr->front():g;
    if (ckmatrix(args)){
      matrice tmp(*args._VECTptr);
      if (tmp.empty())
	return gendimerr(contextptr);
      if (tmp.size()!=2)
	tmp=mtran(tmp);
      int ts=tmp.size();
      if (ts<2)
	return gendimerr(contextptr);
      if (ts>2){
	// draw a camembert for each line
	// [ list_of_class_names camembert1_values camembert2_values etc. ]
	// camembertj_values may begin with a title string
	names=*tmp.front()._VECTptr;
	if (names.size()<2)
	  return gendimerr(contextptr);
	if (names[1].type!=_STRNG)
	  return gensizeerr(contextptr);
	vals=vecteur(tmp.begin()+1,tmp.end());
	return 0;
      }
      vals=*tmp[1]._VECTptr;
      names=*tmp[0]._VECTptr;
      if (vals.front().type==_STRNG)
	std::swap(vals,names);
      vals=vecteur(1,vals);
      return 0;
    }
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vals=*args._VECTptr;
    names=vecteur(vals.size(),string2gen("",false));
    vals=vecteur(1,vals);
    return 0;
  }

  // list of values or matrix with col1=list of legends, col2=list of values
  gen _diagramme_batons(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur vals,names,attributs,res;
    gen errcode=read_camembert_args(g,vals,names,attributs,contextptr);
    if (is_undef(errcode)) return errcode;
    vecteur attr(gen2vecteur(attributs[0]));
    int ncamemberts=vals.size(),s=vals.front()._VECTptr->size(),t=attr.size();
    if (t==1)
      t=0;
    for (int j=0;j<ncamemberts;j++){
      vecteur & Vals = *vals[j]._VECTptr;
      int i=0;
      gen xy=s*j;
      if (Vals[0].type==_STRNG){
	// add title
	res.push_back(symb_pnt_name(xy+.5-2*cst_i,_POINT_INVISIBLE,Vals[0],contextptr));
	++i;
      }
      for (;i<s;++i){
	gen tmp(makevecteur(xy+i+.4+cst_i*Vals[i],xy+i+.4,xy+i-.4,xy+i-.4+cst_i*Vals[i],xy+i+.4+cst_i*Vals[i]),_LINE__VECT);
	res.push_back(symb_pnt_name(tmp,i<t?attr[i]:((i==7?0:i) | _FILL_POLYGON | _QUADRANT2),names[i],contextptr));
      }
    }
    return res;
  }
  static const char _diagramme_batons_s []="bar_plot";
static define_unary_function_eval (__diagramme_batons,&_diagramme_batons,_diagramme_batons_s);
  define_unary_function_ptr5( at_diagramme_batons ,alias_at_diagramme_batons,&__diagramme_batons,0,true);

  gen _camembert(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur vals,names,attributs,res;
    gen errcode=read_camembert_args(g,vals,names,attributs,contextptr);
    if (is_undef(errcode)) return errcode;
    vecteur attr(gen2vecteur(attributs[0]));
    int ncamemberts=vals.size(),s=vals.front()._VECTptr->size(),t=attr.size();
    for (int j=0;j<ncamemberts;j++){
      gen xy=5*(j%4)-5*(j/4)*cst_i;
      gen diametre=makevecteur(-1+xy,1+xy);
      gen a(0),da;
      double da100;
      char ss[256];
      vecteur & Vals = *vals[j]._VECTptr;
      gen somme;
      int i=0,pos=0;;
      if (Vals[0].type==_STRNG){
	// add title
	res.push_back(symb_pnt_name(xy-1+2*cst_i,_POINT_INVISIBLE,Vals[0],contextptr));
	++i;
	somme=_plus(vecteur(Vals.begin()+1,Vals.end()),contextptr);
      }
      else
	somme=_plus(Vals,contextptr);
      for (;i<s;++i){
	if (ck_is_strictly_positive(-Vals[i],contextptr))
	  return gensizeerr(gettext("Negative value encoutered"));
	da=2*cst_pi*Vals[i]/somme;
	da100=evalf_double(100*Vals[i]/somme,1,contextptr)._DOUBLE_val;
	if (da100>0){        
#if 1 // ndef BESTA_OS // BP please comment, no sprintf avail?
	  sprintfdouble(ss,"%.4g",da100);
#endif
	  if (is_positive(a-cst_pi/2,contextptr))
	    pos=_QUADRANT2;
	  if (is_positive(a-cst_pi,contextptr))
	    pos=_QUADRANT3;
	  if (is_positive(a-3*cst_pi/2,contextptr))
	    pos=_QUADRANT4;
	  gen tmp=symbolic(at_cercle,gen(makevecteur(diametre,a,a+da),_PNT__VECT));
	  res.push_back(symb_pnt_name(tmp,i<t?attr[i]:(i%7 | _FILL_POLYGON | pos),string2gen(gen2string(names[i])+":"+string(ss)+"%",false),contextptr));
	  a=a+da;
	}
      }
    }
    return res;
  }
  static const char _camembert_s []="camembert";
static define_unary_function_eval (__camembert,&_camembert,_camembert_s);
  define_unary_function_ptr5( at_camembert ,alias_at_camembert,&__camembert,0,true);

  // Graham scan convex hull
 static bool graham_sort_function(const gen & a,const gen & b){
   if (a.type!=_VECT || b.type!=_VECT || a._VECTptr->size()!=3 || b._VECTptr->size()!=3){
#ifdef NO_STDEXCEPT
     return false; 
#else
     setsizeerr(gettext("graham_sort_function"));
#endif
   }
   vecteur & v=*a._VECTptr;
   vecteur & w=*b._VECTptr;
   return is_strictly_greater(w[1],v[1],context0) || (v[1]==w[1] && is_strictly_greater(w[2],v[2],context0)) ;
 }

  gen cross_prod(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT){
    gen ab=b-a,ac=c-a;
    gen A(re(ab,contextptr)),B(im(ab,contextptr)),C(re(ac,contextptr)),D(im(ac,contextptr));
    return A*D-B*C;
  }

  gen _convexhull(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr); 
    vecteur l0(*_affixe(g,contextptr)._VECTptr),l;
    int s=l0.size();
    for (int i=0;i<s;++i){
      if (l0[i].type==_VECT)
	l=mergevecteur(l,*l0[i]._VECTptr);
      else
	l.push_back(l0[i]);
    }
    s=l.size();
    if (s<=3){
      if (abs_calc_mode(contextptr)==38)
	return _polygone(l,contextptr);
      return l;
    }
    gen zmin=l[0],zcur;
    gen ymin=im(zmin,contextptr),ycur,xmin=re(zmin,contextptr),xcur;
    for (int j=1;j<s;++j){
      zcur=l[j]; ycur=im(zcur,contextptr); xcur=re(zcur,contextptr);
      if ( is_strictly_greater(ymin,ycur,contextptr) ||
	   (ycur==ymin && is_strictly_greater(xmin,xcur,contextptr)) ){
	zmin=zcur; ymin=ycur; xmin=xcur;
      }
    }
    vecteur ls;
    for (int j=0;j<s;++j){
      zcur=l[j];
      if (zcur!=zmin){
	ls.push_back(makevecteur(zcur,arg(zcur-zmin,contextptr),(zcur-zmin)*conj(zcur-zmin,contextptr)));
      }
    }
    sort(ls.begin(),ls.end(),graham_sort_function);
    vecteur res(makevecteur(zmin,ls[0][0]));
    int ress=2;
    gen o;
    for (int j=1;j<s-1;++j){
      zcur=ls[j][0];
      o=cross_prod(res[ress-2],res[ress-1],zcur,contextptr);
      if (is_zero(o))
	res[ress-1]=zcur;
      else {
	if (is_strictly_positive(o,contextptr)){
	  res.push_back(zcur);
	  ress++;
	}
	else {
	  while (!is_positive(o,contextptr) && ress>2){
	    res.pop_back();
	    ress--;
	    o=cross_prod(res[ress-2],res[ress-1],zcur,contextptr);
	  }
	  res.push_back(zcur);
	  ress++;
	}
      }
    }
    if (abs_calc_mode(contextptr)==38)
      return _polygone(res,contextptr);
    return gen(res,g.subtype);
  }
  static const char _convexhull_s []="convexhull";
static define_unary_function_eval (__convexhull,&_convexhull,_convexhull_s);
  define_unary_function_ptr5( at_convexhull ,alias_at_convexhull,&__convexhull,0,true);

#ifdef RTOS_THREADX
  gen _simplex_reduce(const gen & args,GIAC_CONTEXT){
    return undef;
  }
  static const char _simplex_reduce_s []="simplex_reduce";
static define_unary_function_eval (__simplex_reduce,&_simplex_reduce,_simplex_reduce_s);
  define_unary_function_ptr5( at_simplex_reduce ,alias_at_simplex_reduce,&__simplex_reduce,0,true);
#else
  // Simplex algorithm solving max c.x where constraints on x are in 
  // canonical form: A*x <= b with b>= 0
  // Variables are added to get [A|I] (x,x_slack) = b
  // 
  // Arguments:
  // m must contain an identity matrix in the n-1 first rows
  // like [A|I|b], to solve for b>=0, A*x<=b (I is for "slack" variables)
  // <variables d'ecarts ajoutees pour transformer <= en egalite >
  // last line (objective function row) [-c|0|0], maximize or minimize c.x
  // if the coefficients of the last row at the columns of the identity 
  // are not zero, step 0 will transform the last row to set them to 0.
  // max_pb is true for maximization and false for min
  // choose_first=true if we choose the first possible entering/outgoing index
  // 
  // Returns:
  // optimum will contain the max (min) value, if not +/-inf
  // bfs contains the coordinates of a solution
  // At the end of the algorithm we have [B^-1*A|B^-1|B^-1*b] for the
  // n-1 first row, and for last row [-c+c_B * B^-1*A | c_B*B^-1|c_B*B^-1*b]
  // where all coeffs of non-basic variables are + (for a max) and other are 0
  // Since the function to maximize + (last row) scalar (x,x_slack_variables)
  // = c_B*B^-1*b, it is not possible to improve c_B*B^-1*b
  // The reason is that the current solution has non-0 components
  // corresponding to the 0 value in the last row 
  // and 0 components corresponding to non-0 positive values in the last row
  // If we move one 0 component of the current solution, it must increase 
  // hence we have to decrease one of the coeff corresponding to non-0
  // positive coeffs in the last row, decreasing the value of the function
  // to maximize.
  // 
  // Not yet implemented: how to reduce any linear programming problem
  // to a feasible canonical matrix m
  // The idea is to add artificial variables (as many as there are equalities)
  // and maximize -sum(artificial variables) starting with all non
  // artificial variables equal to 0
  // If the max is not 0 there is no solution
  // otherwise all artificial variables are set to 0 and we have a
  // basic feasible solution to start with
  //
  // NB: If a coeff of the bfs is 0 we may cycle, using choose_first=true
  // will insure we do not cycle (Bland's rule)
  // another rule might be implemented by keeping somewhere all the
  // visited basis corresponding to the same max value
  matrice simplex_reduce(const matrice & m_orig,vecteur & bfs,gen & optimum,bool max_pb,bool choose_first,GIAC_CONTEXT){
    matrice m(m_orig);
    int nr=m.size();
    int nc=m.front()._VECTptr->size();
    if (nc<nr+1)
      return vecteur(1,gendimerr(contextptr));
    // Step 0 set the coefficients of the last row in the idn column to 0
    vecteur lastline(nr-1);
    matrice mt(mtran(m)); 
    for (int i=0;i<nc-1;++i){
      int cur_col=-1;
      vecteur & mti=*mt[i]._VECTptr;
      for (int j=0;j<nr-1;++j){
	if (is_zero(mti[j]))
	  continue;
	if (!is_one(mti[j]) || cur_col>=0){
	  cur_col=-1;
	  break; // not an idn line
	}
	cur_col=j;	    
      }
      if (cur_col>=0)
	lastline[cur_col]=mti[nr-1];
    }
    for (int i=0;i<nr-1;++i){
      if (!is_zero(lastline[i]))
	m[nr-1]=subvecteur(*m[nr-1]._VECTptr,multvecteur(lastline[i],*m[i]._VECTptr));
    }
    for (;;){
      // Step 1: find the most positive (min_pb) or negative (max_pb) 
      // coefficient of the objective function row (last row)
      // or choose the first + or - if choose_first is true
      gen mincoeff=0;
      int J=-1;
      vecteur &last=*m.back()._VECTptr;
      for (int j=0;j<nc-1;++j){
	if (is_strictly_greater((max_pb?mincoeff:last[j]),
				(max_pb?last[j]:mincoeff),contextptr)){
	  J=j;
	  mincoeff=last[j];
	  if (choose_first)
	    break;
	}
      }
      if (J==-1){ // Find bfs and optimum
	optimum=m[nr-1][nc-1];
	bfs=vecteur(nc-1);
	// Push back 0 or m[i][nc-1] if column is an identity column
	matrice mt=mtran(m);
	for (int i=0;i<nc-1;++i){
	  int cur_col=-1;
	  if (is_zero(mt[i][nr-1])){
	    for (int j=0;j<nr-1;++j){
	      if (is_zero(mt[i][j]))
		continue;
	      if (cur_col>=0){
		cur_col=-1;
		break; // not an idn line
	      }
	      cur_col=j;	    
	    }
	  }
	  if (cur_col>=0)
	    bfs[i]=mt[nc-1][cur_col];
	}
	return m;
      }
      int I=-1;
      mincoeff=plus_inf;
      gen ratio;
      // We will move the J-th variable from 0 to something positive
      // We have to find which variables govern how much positive xJ can be
      // -> find the smallest positive ratio 
      // and choose the 1st one if the smallest ratio is reached several times
      for (int i=0;i<nr-1;++i){
	gen m1(m[i][J]);
	if (is_strictly_positive(m1,contextptr) && 
	    is_strictly_greater(mincoeff,ratio=m[i][nc-1]/m1,contextptr)){
	  I=i;
	  mincoeff=ratio;
	}
      }
      if (I==-1){ // The function is not bounded since xJ can grow to +inf
	optimum=max_pb?plus_inf:minus_inf;
	return m;
      }
      if (is_zero(mincoeff)) // Bland's rule
	choose_first=true;
      // Pivot found, line I, column J, reduce matrix (Gauss-like)
      m[I]=divvecteur(*m[I]._VECTptr,m[I][J]);
      vecteur & pivot_v = *m[I]._VECTptr;
      gen a;
      for (int i=0;i<nr;++i){
	if (i==I)
	  continue;
	vecteur & v=*m[i]._VECTptr;
	a=v[J];
	for (int j=0;j<nc;++j){
	  v[j]=v[j]-a*pivot_v[j];
	}
      }
    }
  }

  // solve max(c.x) under Ax<=b, returns optimum value, solution x0
  // and reduced matrix
  gen _simplex_reduce(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    matrice m;
    vecteur v; 
    gen optimum;
    if (g.type ==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()==3){
      vecteur & gv =*g._VECTptr;
      if (gv[0].type!=_VECT || gv[1].type!=_VECT || gv[2].type!=_VECT)
	return gentypeerr(contextptr);
      m=*gv[0]._VECTptr;
      int add=m.size();
      m=mtran(m);
      m=mergevecteur(m,midn(add));
      m.push_back(gv[1]);
      if (!ckmatrix(m))
	return gendimerr(contextptr);
      m=mtran(m);
      m.push_back(mergevecteur(*(-gv[2])._VECTptr,vecteur(add+1,0)));
      if (!ckmatrix(m))
	return gendimerr(contextptr);
    }
    else {
      if (!ckmatrix(g))
	return gensizeerr(contextptr); 
      m=*g._VECTptr;
    }
    m=simplex_reduce(m,v,optimum,true,false,contextptr);
    if (is_undef(m) && !m.empty())
      return m.front();
    return gen(makevecteur(optimum,v,m),_SEQ__VECT);
  }
  static const char _simplex_reduce_s []="simplex_reduce";
static define_unary_function_eval (__simplex_reduce,&_simplex_reduce,_simplex_reduce_s);
  define_unary_function_ptr5( at_simplex_reduce ,alias_at_simplex_reduce,&__simplex_reduce,0,true);
#endif

  // natural_spline([x0,...,xn],[y0,...,yn],x,d)
  // -> spline of degree d, in C^{d-1}, with values yk at xk
  // and initial/final derivatives = 0 from order 1 to (d-1)/2, 
  // d-1 conditions
  // returns a list of n polynomials with respect to x
  // to get the value of the spline, find the right interval hence polynomial
  // and call horner(poly,value-xi)
  // x and d are optionnal, if not precised d is 3
  gen _spline(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<2)
      return gensizeerr(contextptr);
    vecteur w(*g._VECTptr);
    if (w.size()<3)
      w.push_back(vx_var);
    if (w.size()<4)
      w.push_back(3);
    gen X(w[0]), Y(w[1]), xvar(w[2]), dg(w[3]);
    if (dg.type!=_INT_ || dg.val<1 || X.type!=_VECT || Y.type!=_VECT)
      return gentypeerr(contextptr);
    vecteur &x=*X._VECTptr;
    vecteur &y=*Y._VECTptr;
    int n=x.size()-1;
    if (n<1 || y.size()!=unsigned(n+1))
      return gendimerr(contextptr);
    int d(dg.val);
    // create n unknowns, the values of the highest derivative coeff
    // at x0, ..., xn-1
    // and (d-1)/2 unknowns, the values of diff(spline,x$k) for k=1 to (d-1)/2
    vecteur z(n),f((d-1)/2),pol;
    for (int i=0;i<n;++i){
      z[i]=identificateur(" z"+print_INT_(i));
    }
    for (int i=0;i<(d-1)/2;++i){
      f[i]=identificateur(" f"+print_INT_(i));
    }
    // create vector of linear equations to solve for
    vecteur lineq;
    // create initial taylor polynomial form of the poly
    vecteur v(d+1);
    v[0]=z[0]; // z[0]*(x-x0)^d + f_(d-1)/2 * (x-x0)^{(d-1)/2} + ...+f_0*(x-x_0) + y0
    for (int i=1;i<=(d-1)/2;++i)
      v[d-i]=f[i-1];
    v[d]=y[0]; // all conditions at x0 are solved
    pol.push_back(v);
    for (int i=0;i<n;++i){   
      // move from xi to xi+1
      v=*ratnormal(taylor(v,x[i+1]-x[i]))._VECTptr;
      lineq.push_back(v[d]-y[i+1]);
      // new v changes by the first coeff of v only
      v[0]=(i==n-1)?0:z[i+1];
      pol.push_back(v);
    }
    // add conditions at x[n]
    for (int i=1;i<=(d-1)/2;++i){
      lineq.push_back(v[i]);
    }
    vecteur inconnu(mergevecteur(z,f));
    vecteur zf=linsolve(lineq,inconnu,contextptr);
    if (is_undef(zf)) return zf;
    pol.pop_back();
    pol=*ratnormal(subst(pol,inconnu,zf,false,contextptr))._VECTptr;
    for (int i=0;i<n;++i){
      if (pol[i].type==_VECT)
	pol[i]=symb_horner(*pol[i]._VECTptr,xvar-x[i]);
    }
    return pol;
  }
  static const char _spline_s []="spline";
static define_unary_function_eval (__spline,&_spline,_spline_s);
  define_unary_function_ptr5( at_spline ,alias_at_spline,&__spline,0,true);

  gen giac_bitand(const gen & a,const gen & b){
    register unsigned t=(a.type<< _DECALAGE) | b.type;
    if (!t)
      return( a.val & b.val);
    register ref_mpz_t * e;
    switch ( t ) {
    case _ZINT__ZINT:
      e = new ref_mpz_t;
      mpz_and(e->z,*a._ZINTptr,*b._ZINTptr);
      return e;
    case _INT___ZINT: 
      e = new ref_mpz_t;
      mpz_set_ui(e->z,a.val);
      mpz_and(e->z,e->z,*b._ZINTptr);
      return(e);
    case _ZINT__INT_:
      e = new ref_mpz_t;
      mpz_set_ui(e->z,b.val);
      mpz_and(e->z,*a._ZINTptr,e->z);
      return(e);
    }
    return symbolic(at_bitand,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen giac_bitor(const gen & a,const gen & b){
    register unsigned t=(a.type<< _DECALAGE) | b.type;
    if (!t)
      return( a.val | b.val);
    register ref_mpz_t * e;
    switch ( t ) {
    case _ZINT__ZINT:
      e = new ref_mpz_t;
      mpz_ior(e->z,*a._ZINTptr,*b._ZINTptr);
      return(e);
    case _INT___ZINT: 
      e = new ref_mpz_t;
      mpz_set_ui(e->z,a.val);
      mpz_ior(e->z,e->z,*b._ZINTptr);
      return(e);
    case _ZINT__INT_:
      e = new ref_mpz_t;
      mpz_set_ui(e->z,b.val);
      mpz_ior(e->z,*a._ZINTptr,e->z);
      return(e);
    }
    return symbolic(at_bitor,gen(makevecteur(a,b),_SEQ__VECT));
  }
  gen giac_bitxor(const gen & a,const gen & b){
    register unsigned t=(a.type<< _DECALAGE) | b.type;
    if (!t)
      return( a.val ^ b.val);
    register ref_mpz_t * e;
    switch ( t ) {
    case _ZINT__ZINT:
      e = new ref_mpz_t;
      mpz_xor(e->z,*a._ZINTptr,*b._ZINTptr);
      return(e);
    case _INT___ZINT: 
      e = new ref_mpz_t;
      mpz_set_ui(e->z,a.val);
      mpz_xor(e->z,e->z,*b._ZINTptr);
      return(e);
    case _ZINT__INT_:
      e = new ref_mpz_t;
      mpz_set_ui(e->z,b.val);
      mpz_xor(e->z,*a._ZINTptr,e->z);
      return(e);
    }
    return symbolic(at_bitxor,gen(makevecteur(a,b),_SEQ__VECT));
  }

  gen giac_hamdist(const gen & a,const gen & b){
    unsigned long t=(a.type<< _DECALAGE) | b.type;
    if (t==0){
      unsigned res=0;
      unsigned val=a.val ^ b.val;
      for (int i=0;i<31;++i){
	res += (val >>i) & 1; 
      }
      return int(res);
    }
    ref_mpz_t *  e = new ref_mpz_t;
    switch ( t ) {
      /*
    case 0:
      mpz_set_ui(e->z,a.val ^ b.val);
      t = mpz_popcount(e->z);
      break;
      */
    case _ZINT__ZINT:
      t=mpz_hamdist(*a._ZINTptr,*b._ZINTptr);
      break;
    case _INT___ZINT: 
      mpz_set_ui(e->z,a.val);
      t=mpz_hamdist(e->z,*b._ZINTptr);
      break;
    case _ZINT__INT_:
      mpz_set_ui(e->z,b.val);
      t=mpz_hamdist(*a._ZINTptr,e->z);
      break;
    default:
      delete e;
      return symbolic(at_hamdist,gen(makevecteur(a,b),_SEQ__VECT));
    }
    delete e;
    return longlong(t);
  }

  gen binop(const gen & g,gen (* f) (const gen &, const gen &)){
    if (g.type!=_VECT || g._VECTptr->empty())
      return gensizeerr(gettext("binop"));
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    gen res=*it;
    for (++it;it!=itend;++it){
      res=apply(res,*it,f);
    }
    return res;
  }
  gen _bitand(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return binop(g,giac_bitand);
  }
  static const char _bitand_s []="bitand";
static define_unary_function_eval (__bitand,&_bitand,_bitand_s);
  define_unary_function_ptr5( at_bitand ,alias_at_bitand,&__bitand,0,true);

  gen _bitor(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return binop(g,giac_bitor);
  }
  static const char _bitor_s []="bitor";
static define_unary_function_eval (__bitor,&_bitor,_bitor_s);
  define_unary_function_ptr5( at_bitor ,alias_at_bitor,&__bitor,0,true);

  gen _bitxor(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return binop(g,giac_bitxor);
  }
  static const char _bitxor_s []="bitxor";
static define_unary_function_eval (__bitxor,&_bitxor,_bitxor_s);
  define_unary_function_ptr5( at_bitxor ,alias_at_bitxor,&__bitxor,0,true);

  /*
  gen _bitnot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_INT_)
      return ~g.val;
    if (g.type==_ZINT){
      ref_mpz_t *  e = new ref_mpz_t;
      mpz_com(e->z,*g._ZINTptr);
      return e;
    }
    return gensizeerr();
  }
  static const char _bitnot_s []="bitnot";
  static define_unary_function_eval (__bitnot,&_bitnot,_bitnot_s);
  define_unary_function_ptr5( at_bitnot ,alias_at_bitnot,&__bitnot,0,true);
  */

  gen _hamdist(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return binop(g,giac_hamdist);
  }
  static const char _hamdist_s []="hamdist";
static define_unary_function_eval (__hamdist,&_hamdist,_hamdist_s);
  define_unary_function_ptr5( at_hamdist ,alias_at_hamdist,&__hamdist,0,true);


  // ploarea(polygone), plotarea(f(x),x=a..b), plotarea(f(x),x=a..b,n,method)
  // method=trapeze,point_milieu,rectangle_gauche,rectangle_droit
  gen _plotarea(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    vecteur attributs(default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gensizeerr(contextptr);
    if (attributs.size()<2)
      attributs.push_back(0);
    if (attributs[0].type==_INT_)
      attributs[0].val= attributs[0].val | _FILL_POLYGON;
    v[0]=remove_at_pnt(v[0]);
    if (v[0].type==_VECT){
      attributs[1]=_aire(v[0],contextptr);
      return pnt_attrib(v[0],attributs,contextptr);
    }
    if (s>=2 && v[0].type!=_VECT){
      gen tmp(v[1]),a,b,x(vx_var);
      if (tmp.is_symb_of_sommet(at_equal) && tmp._SYMBptr->feuille.type==_VECT && tmp._SYMBptr->feuille._VECTptr->size()==2){
	x=tmp._SYMBptr->feuille[0];
	tmp=tmp._SYMBptr->feuille[1];
      }
      if (tmp.is_symb_of_sommet(at_interval) && tmp._SYMBptr->feuille.type==_VECT && tmp._SYMBptr->feuille._VECTptr->size()==2){
	a=tmp._SYMBptr->feuille[0];
	b=tmp._SYMBptr->feuille[1];
      }
      else
	return gensizeerr(gettext("plotarea(f(x),x=a..b[,n,method])"));
      if (s>=2){
	gen graph=funcplotfunc(gen(vecteur(v.begin(),v.begin()+s),_SEQ__VECT),false,contextptr); // must be a graph of fcn
	if (is_undef(graph))
	  return graph;
	// extract polygon
	gen graphe=remove_at_pnt(graph);
	if (graphe.is_symb_of_sommet(at_curve) && graphe._SYMBptr->feuille.type==_VECT){
	  vecteur & graphev=*graphe._SYMBptr->feuille._VECTptr;
	  if (graphev.size()>1){
	    gen polyg=graphev[1];
	    if (polyg.type==_VECT){
	      if (s==2){
		// add verticals and horizontal
		vecteur res(*polyg._VECTptr);
		res.insert(res.begin(),a);
		res.insert(res.begin(),b);
		res.push_back(b);
		int nd=decimal_digits(contextptr);
		decimal_digits(3,contextptr);
		attributs[1]=string2gen(_romberg(gen(makevecteur(v[0],v[1]),_SEQ__VECT),contextptr).print(contextptr),false);
		decimal_digits(nd,contextptr);
		return pnt_attrib(gen(res,_GROUP__VECT),attributs,contextptr);
	      } // end s==2
	      if (s>=3)
		v[2]=_floor(v[2],contextptr);
	      if (s>=3 && v[2].type==_INT_){
		int n=v[2].val;
		if (n<1)
		  return gensizeerr(contextptr);
		vecteur res;
		res.push_back(b);
		res.push_back(a);
		gen dx=(b-a)/n,x0=a,xf=x0,fxf,f=v[0],A;
		int method=_TRAPEZE;
		if (s>=4 && v[3].type==_INT_)
		  method = v[3].val;
		if (method==_RECTANGLE_DROIT || method==_RECTANGLE_GAUCHE || method==_POINT_MILIEU){
		  if (method==_RECTANGLE_DROIT)
		    xf=x0+dx;
		  if (method==_POINT_MILIEU)
		    xf=x0+dx/2;
		  for (int i=0;i<n;++i){
		    fxf=evalf(quotesubst(f,x,xf,contextptr),1,contextptr);
		    A=A+dx*fxf;
		    res.push_back(x0+fxf*cst_i);
		    x0=x0+dx;
		    xf=xf+dx;
		    res.push_back(x0+fxf*cst_i);
		  }
		}
		if (method==_TRAPEZE){
		  fxf=evalf(quotesubst(f,x,xf,contextptr),1,contextptr);
		  A=dx*fxf/2;
		  res.push_back(xf+fxf*cst_i);
		  xf=x0+dx;
		  for (int i=0;i<n-1;++i){
		    fxf=evalf(quotesubst(f,x,xf,contextptr),1,contextptr);
		    A=A+dx*fxf;
		    res.push_back(xf+fxf*cst_i);
		    x0=x0+dx;
		    xf=xf+dx;
		  }
		  fxf=evalf(quotesubst(f,x,b,contextptr),1,contextptr);
		  A=A+dx*fxf/2;
		  res.push_back(b+fxf*cst_i);
		}
		res.push_back(b);
		int nd=decimal_digits(contextptr);
		decimal_digits(3,contextptr);
		attributs[1]=string2gen(A.print(contextptr),false);
		decimal_digits(nd,contextptr);
		return gen(makevecteur(gen(makevecteur(pnt_attrib(res,attributs,contextptr),graph),_SEQ__VECT),_couleur(makevecteur(graph,_RED+_DASH_LINE+_LINE_WIDTH_3),contextptr)),_SEQ__VECT);
	      } // end if (s>=3)
	    } // end polyg.type==_VECT
	  }
	}
      } // end s>=2
    }
    return gensizeerr(gettext(""));
  }
  static const char _plotarea_s []="plotarea";
  static define_unary_function_eval (__plotarea,&_plotarea,_plotarea_s);
  define_unary_function_ptr5( at_plotarea ,alias_at_plotarea,&__plotarea,0,true);

  static const char _areaplot_s []="areaplot";
  static define_unary_function_eval (__areaplot,&_plotarea,_areaplot_s);
  define_unary_function_ptr5( at_areaplot ,alias_at_areaplot,&__areaplot,0,true);

  gen _add_language(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_INT_){
      add_language(args.val);
      return 1;
    }
    if (args.type==_STRNG){
      string s=*args._STRNGptr;
      s=s.substr(0,2);
      int i=string2lang(s);
      if (i){
	add_language(i);
	return 1;
      }
    }
    return 0;
  }
  static const char _add_language_s []="add_language";
static define_unary_function_eval (__add_language,&_add_language,_add_language_s);
  define_unary_function_ptr5( at_add_language ,alias_at_add_language,&__add_language,0,true);

  gen _remove_language(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_INT_){
      remove_language(args.val);
      return 1;
    }
    if (args.type==_STRNG){
      string s=*args._STRNGptr;
      s=s.substr(0,2);
      int i=string2lang(s);
      if (i){
	remove_language(i);
	return 1;
      }
    }
    return 0;
  }
  static const char _remove_language_s []="remove_language";
static define_unary_function_eval (__remove_language,&_remove_language,_remove_language_s);
  define_unary_function_ptr5( at_remove_language ,alias_at_remove_language,&__remove_language,0,true);

  gen _show_language(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return vector_int_2_vecteur(lexer_localization_vector());
  }
  static const char _show_language_s []="show_language";
static define_unary_function_eval (__show_language,&_show_language,_show_language_s);
  define_unary_function_ptr5( at_show_language ,alias_at_show_language,&__show_language,0,true);

  gen _os_version(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
#ifdef WIN32
    return string2gen("win",false);
#else
#ifdef __APPLE__
    return string2gen("macos",false);
#else
    return string2gen("unix",false);
#endif
#endif
  }
  static const char _os_version_s []="os_version";
static define_unary_function_eval (__os_version,&_os_version,_os_version_s);
  define_unary_function_ptr5( at_os_version ,alias_at_os_version,&__os_version,0,true);

#ifndef GIAC_HAS_STO_38
  gen plotproba(const gen & args,const vecteur & positions,const vecteur & attributs,GIAC_CONTEXT){
    matrice m (*args._VECTptr);
    // check if there is a row of legende strings
    gen leg;
    if (!is_squarematrix(m)){
      if (!ckmatrix(m) || m.empty())
	return gensizeerr(contextptr);
      int r=m.size();
      int c=m[0]._VECTptr->size();
      if (c==r+1){
	m=mtran(m);
	c=r;
      }
      else {
	if (r!=c+1)
	  return gensizeerr(contextptr);
      }
      // first or last row?
      gen m00=m[0][0];
      if (m00.type==_IDNT || m00.type==_STRNG){
	leg=m.front();
	m=vecteur(m.begin()+1,m.end());
      }
      else {
	leg=m.back();
	m.pop_back();
      }
    }
    int ms=m.size();
    if (ms<2)
      return gendimerr(contextptr);
    // check if coeffs>=0 and sum coeffs = 1 on rows or on columns
    gen g=_sum(args,contextptr);
    if (g!=vecteur(ms,1)){
      m=mtran(m);
      ms=m.size();
      g=_sum(m,contextptr);
      if (!is_zero(g-vecteur(ms,1)))
	*logptr(contextptr) << gettext("Warning: not a graph matrix!") << endl;
    }
    // first make points, 
    vecteur l(ms),pos(ms),col(ms,_BLACK);
    switch (ms){
    case 2:
      l[0]=0.; pos[0]=_QUADRANT3;
      l[1]=1.; pos[1]=_QUADRANT4; col[1]=35;
      break;
    case 3:
      l[0]=0.0; pos[0]=_QUADRANT3;
      l[1]=1.0; pos[1]=_QUADRANT4;col[1]=35;
      l[2]=gen(0.5,std::sqrt(3.0)/2); pos[2]=_QUADRANT1; col[2]=11;
      break;
    case 4:
      l[0]=0.; pos[0]=_QUADRANT3;
      l[1]=1; pos[1]=_QUADRANT4;col[1]=35;
      l[2]=gen(0.5,0.5*std::sqrt(3.0));  pos[2]=_QUADRANT1; col[2]=11;
      l[3]=(l[1]+l[2])/3; // isobarycenter
      col[3]=58;
      break;
    case 5:
      l[0]=0.; pos[0]=_QUADRANT3;
      l[1]=3.; pos[1]=_QUADRANT4;col[1]=35;
      l[2]=gen(1.5,1.5*std::sqrt(3.0));  pos[2]=_QUADRANT1; col[2]=11;
      l[3]=gen(1.,.75); col[3]=58;
      l[4]=gen(2.,.75); col[4]=_MAGENTA;
      break;
    case 6:
      l[0]=0.; pos[0]=_QUADRANT3;
      l[1]=3.; pos[1]=_QUADRANT4;col[1]=35;
      l[2]=gen(1.5,1.5*std::sqrt(3.0));  pos[2]=_QUADRANT1; col[2]=11;
      l[3]=gen(1.,.5); col[3]=58;
      l[4]=gen(2.,.5); col[4]=_MAGENTA;
      l[5]=gen(1.5,1.36602540378); col[5]=220;
      break;
    default:
      l[0]=0.; pos[0]=_QUADRANT3;
      l[1]=3.; pos[1]=_QUADRANT4;col[1]=35;
      l[2]=gen(1.5,1.5*std::sqrt(3.0));  pos[2]=_QUADRANT1; col[2]=11;
      l[3]=gen(1.,.5); col[3]=58;
      l[4]=gen(2.,.5); col[4]=_MAGENTA;
      l[5]=gen(1.5,1.36602540378); col[5]=220;
      l[6]=gen(1.36,0.97); col[6]=_RED;
      break;
    }
    if (int(positions.size())==ms){
      vecteur tmp=positions;
      for (int i=0;i<ms;++i){
	tmp[i]=eval(tmp[i],1,contextptr);
	gen p=evalf_double(tmp[i],1,contextptr);
	tmp[i]=remove_at_pnt(p);
	if (tmp[i].type<_POLY){
	  l[i]=tmp[i];
	  // adjust color and position
	  if (p.is_symb_of_sommet(at_pnt) && p._SYMBptr->feuille.type==_VECT && p._SYMBptr->feuille._VECTptr->size()>1){
	    p=(*p._SYMBptr->feuille._VECTptr)[1];
	    if (p.type==_VECT && !p._VECTptr->empty())
	      p=p._VECTptr->front();
	    p=exact(p,contextptr);
	    if (p.type==_INT_){
	      if ((p.val & 0xffff)){
		pos[i]=0;
		col[i]=p.val;
	      }
	      else
		pos[i]=p.val;
	    }
	  }
	}
      }
    }
    else {
      if (ms>7)
	return gendimerr(contextptr);
    }
    if (!attributs.empty() && attributs[0].type==_VECT && int(attributs[0]._VECTptr->size())==ms)
      col=*attributs[0]._VECTptr;
    // then link if matrix cell is not 0
    vecteur res;
    res.reserve(2*ms*ms+ms);
    for (int i=0;i<ms;++i){
      string s;
      if (leg.type==_VECT && int(leg._VECTptr->size())>i)
	s=leg[i].print(contextptr);
      else {
	if (int(positions.size())>i && positions[i].type==_IDNT)
	  s = positions[i].print(contextptr);
	else
	  s+=('A'+i);
      }
      gen mii=m[i][i];
      if (!is_zero(mii))
	s += ':'+mii.print();
      gen legende=symb_equal(at_legende,string2gen(s,false));
      pos[i].subtype=_INT_PLOT;
      col[i].subtype=_INT_PLOT;
      gen aff=symb_equal(at_display,pos[i]+col[i]);
      res.push_back(_point(gen(makevecteur(l[i],legende,aff),_SEQ__VECT),contextptr));
    }
    for (int i=0;i<ms;++i){
      for (int j=0;j<ms;++j){
	if (i==j)
	  continue;
	gen mij=m[i][j];
	if (mij!=0){
	  gen legende=symb_equal(at_legende,mij);
	  gen aff=symb_equal(at_display,col[j]);
	  res.push_back(_arc(gen(makevecteur(l[i],l[j],0.4,2,legende,aff),_SEQ__VECT),contextptr));
	}
      }
    }
    return res;
  }

  // plotproba(matrix)
  // display a graph from a weight matrix
  gen _plotproba(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (!s || s>2 || (s==2 && v[1].type!=_VECT) )
      return gendimerr(contextptr);
    v.front()=eval(v.front(),1,contextptr);
    if (s==1)
      return plotproba(v.front(),vecteur(0),attributs,contextptr);
    return plotproba(v[0],*v[1]._VECTptr,attributs,contextptr);
  }
  static const char _plotproba_s []="plotproba";
  static define_unary_function_eval_quoted (__plotproba,&_plotproba,_plotproba_s);
  define_unary_function_ptr5( at_plotproba ,alias_at_plotproba,&__plotproba,_QUOTE_ARGUMENTS,true);
#endif

  gen _flatten(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT) return gensizeerr(contextptr);
    vecteur res;
    aplatir(*args._VECTptr,res,true);
    return res;
  }
  static const char _flatten_s []="flatten";
  static define_unary_function_eval (__flatten,&_flatten,_flatten_s);
  define_unary_function_ptr5( at_flatten ,alias_at_flatten,&__flatten,0,true);

  gen _caseval(const gen & args,GIAC_CONTEXT){
#ifdef TIMEOUT
    caseval_begin=time(0);
#endif
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_STRNG) return gensizeerr(contextptr);
    if (*args._STRNGptr=="init geogebra")
      init_geogebra(1,contextptr);
    if (*args._STRNGptr=="close geogebra")
      init_geogebra(0,contextptr);
    return string2gen(caseval(args._STRNGptr->c_str()),false);
  }
  static const char _caseval_s []="caseval";
  static define_unary_function_eval (__caseval,&_caseval,_caseval_s);
  define_unary_function_ptr5( at_caseval ,alias_at_caseval,&__caseval,0,true);

  gen scalarproduct(const vecteur & a,const vecteur & b,GIAC_CONTEXT){
    vecteur::const_iterator ita=a.begin(), itaend=a.end();
    vecteur::const_iterator itb=b.begin(), itbend=b.end();
    gen res,tmp;
    for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
      type_operator_times(conj(*ita,contextptr),(*itb),tmp);
      res += tmp;
    }
    return res;
  }

  gen conjugate_gradient(const matrice & A,const vecteur & b_orig,const vecteur & x0,double eps,GIAC_CONTEXT){
    int n=A.size();
    vecteur b=subvecteur(b_orig,multmatvecteur(A,x0));
    vecteur xk(x0);
    vecteur rk(b),pk(b);
    gen rk2=scalarproduct(rk,rk,contextptr);
    vecteur Apk(n),tmp(n);
    for (int k=1;k<=n;++k){
      multmatvecteur(A,pk,Apk);
      gen alphak=rk2/scalarproduct(pk,Apk,contextptr);
      multvecteur(alphak,pk,tmp);
      addvecteur(xk,tmp,xk);
      multvecteur(alphak,Apk,tmp);
      subvecteur(rk,tmp,rk);
      gen newrk2=scalarproduct(rk,rk,contextptr);
      if (is_greater(eps*eps,newrk2,contextptr))
	return xk;
      multvecteur(newrk2/rk2,pk,tmp);
      addvecteur(rk,tmp,pk);
      rk2=newrk2;
    }
    *logptr(contextptr) << gettext("Warning! Leaving conjugate gradient algorithm after dimension of matrix iterations. Check that your matrix is hermitian/symmetric definite.") << endl;
    return xk;
  }


  // params: matrix A, vector b, optional init value x0, optional precision eps
  gen _conjugate_gradient(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()<2)
      return gensizeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    gen A=v[0];
    gen b=v[1];
    if (!is_squarematrix(A) || b.type!=_VECT)
      return gensizeerr(contextptr);
    int n=A._VECTptr->size();
    if (n!=b._VECTptr->size())
      return gensizeerr(contextptr);
    vecteur x0(n);
    gen eps;
    if (s>=3){
      if (v[2].type==_VECT){
	if (v[2]._VECTptr->size()!=n)
	  return gensizeerr(contextptr);
	x0=*v[2]._VECTptr;
	if (s>3)
	  eps=v[3];
      }
      else
	eps=v[2];
    }
    eps=evalf_double(eps,1,contextptr);
    if (eps.type!=_DOUBLE_ || eps._DOUBLE_val < 0 || eps._DOUBLE_val>=1)
      return gentypeerr(contextptr);
    return conjugate_gradient(*A._VECTptr,*b._VECTptr,x0,eps._DOUBLE_val,contextptr);
  }
  static const char _conjugate_gradient_s []="conjugate_gradient";
  static define_unary_function_eval (__conjugate_gradient,&_conjugate_gradient,_conjugate_gradient_s);
  define_unary_function_ptr5( at_conjugate_gradient ,alias_at_conjugate_gradient,&__conjugate_gradient,0,true);

  gen _subtype(const gen & args,GIAC_CONTEXT){
    return args.subtype;
  }
  static const char _subtype_s []="subtype";
  static define_unary_function_eval (__subtype,&_subtype,_subtype_s);
  define_unary_function_ptr5( at_subtype ,alias_at_subtype,&__subtype,0,true);

  // Graph utilities
  // convert matrice of probability to matrice of booleans
  // m[i][j]!=0 means there is a link from i to j
  void proba2adjacence(const matrice & m,vector< vector<unsigned> >& v){
    if (!is_integer_matrice(m) && !is_one(_plus(m.front(),context0))){
      proba2adjacence(mtran(m),v);
      return;
    }
    int l,c;
    mdims(m,l,c);
    v.resize(l);
    for (int i=0;i<l;++i){
      vecteur & mi=*m[i]._VECTptr;
      vector<unsigned> & vi =v[i];
      vi.clear();
      vi.resize((c+31)/32);
      for (int j=0;j<c;++j){
	if (!is_zero(mi[j]))
	  vi[j/32] |= 1<<(j%32);
      }
    }
  }

  // For large graphs, use Tarjan algorithm 
  struct vertex {
    int index,lowlink;
    vertex():index(-1),lowlink(-1){}; // -1 means undefined
  };

  void strongconnect(const vector< vector<unsigned> > & G,vector<vertex> & V,int & index,vector<unsigned> & S,vector<bool> & inS,vector< vector<unsigned> > & SCC,unsigned v){
    V[v].index=index;
    V[v].lowlink=index;
    ++index;
    S.push_back(v);
    inS[v]=true;
    const vector<unsigned> & Gv=G[v];
    for (unsigned i=0;i<Gv.size();++i){
      unsigned Gvi=Gv[i];
      if (!Gvi)
	continue;
      for (unsigned j=0;Gvi && j<32;Gvi/=2, ++j){
	if (!(Gvi %2))
	  continue;
	unsigned w=i*32+j;
	if (V[w].index==-1){
	  // Successor w has not yet been visited; recurse on it
	  strongconnect(G,V,index,S,inS,SCC,w);
	  V[v].lowlink=giacmin(V[v].lowlink,V[w].lowlink);
	  continue;
	}
	if (inS[w]){
	  // successor of w is in stack S, hence is in the current SCC
	  V[v].lowlink=giacmin(V[v].lowlink,V[w].index);
	}
      }
    } // end for (visit all vertices connected to v)
    // If v is a root node, pop the stack and generate a strongly connected component
    if (V[v].lowlink==V[v].index){
      vector<unsigned> scc;
      for (;!S.empty();){
	scc.push_back(S.back());
	S.pop_back();
	inS[scc.back()]=false;
	if (scc.back()==v)
	  break;
      }
      SCC.push_back(scc);
    }
  }

  void tarjan(const vector< vector<unsigned> > & G,vector< vector<unsigned> > & SCC){
    vector<vertex> V(G.size());
    SCC.clear();
    vector<unsigned> S;
    S.reserve(G.size());
    vector<bool> inS(G.size(),false);
    int index=0;
    for (unsigned v=0;v<G.size();++v){
      if (V[v].index==-1)
	strongconnect(G,V,index,S,inS,SCC,v);
    }
  }

#if 0
  // Small graphs
  bool different(const vector<unsigned> & a,const vector<unsigned> & b,vector<int> & pos){
    pos.clear();
    int s=a.size();
    for (int i=0;i<s;++i){
      unsigned ai=a[i],bi=b[i];
      if (ai!=bi){
	int p=i*32;
	for (;ai&&bi;++p,ai/=2,bi/=2){
	  if ( ai%2 != bi%2 )
	    pos.push_back(p);
	}
      }
    }
    return !pos.empty();
  }

  // v[i][j]==true if i is connected to j
  // compute w such that w[i][j]==true if i is connected to j using a path of length >= 1
  // at the end, if w[i][i]=true then i is recurrent, else transient
  // i is recurrent positive if for all j w[i][j]=true => w[j][i]=true
  void connected(const vector< vector<unsigned> >& v,vector< vector<unsigned> > & w){
    int l=v.size();
    int c=v.front().size(); // number of columns = c*32
    w=v;
    vector<int> pos;
    for (int i=0;i<l;++i){
      // compute w[i]
      vector<unsigned> oldvi(c);
      vector<unsigned> curvi(w[i]);
      vector<unsigned> newvi(c);
      // oldvi[i/32] = 1 << (i%32); 
      for (;;){
	// find indices that differ between oldvi and curvi, 
	if (!different(oldvi,curvi,pos))
	  break;
	newvi=curvi;
	for (unsigned j=0;j<pos.size();++j){
	  // make an OR of curvi with w[pos[j]]
	  vector<unsigned>::const_iterator wit=w[pos[j]].begin();
	  vector<unsigned>::iterator newit=newvi.begin(),newitend=newvi.end();
	  for (;newit!=newitend;++wit,++newit){
	    *newit |= *wit;
	  }
	}
	oldvi=curvi;
	curvi=newvi;
      }
      w[i]=curvi;
    }
  }
#endif

  void vector_unsigned2vecteur(const vector<unsigned> & V,vecteur & v){
    v.clear();
    v.reserve(V.size());
    for (unsigned i=0;i<V.size();++i)
      v.push_back(int(V[i]));
  }

  void matrix_unsigned2matrice(const vector< vector<unsigned> > & M,matrice & m){
    m.clear();
    m.reserve(M.size());
    for (unsigned i=0;i<M.size();++i){
      vecteur v;
      vector_unsigned2vecteur(M[i],v);
      m.push_back(v);
    }
  }

  // Input matrix of adjacency or transition matrix
  // Output a list of strongly connected components
  gen _graph_scc(const gen & args,GIAC_CONTEXT){
    if (!is_squarematrix(args))
      return gensizeerr(contextptr);
    vector< vector<unsigned> > G,GRAPH_SCC;
    proba2adjacence(*args._VECTptr,G);
    tarjan(G,GRAPH_SCC);
    matrice m;
    matrix_unsigned2matrice(GRAPH_SCC,m);
    return m;
  }
  static const char _graph_scc_s []="graph_scc";
  static define_unary_function_eval (__graph_scc,&_graph_scc,_graph_scc_s);
  define_unary_function_ptr5( at_graph_scc ,alias_at_graph_scc,&__graph_scc,0,true);

  gen _classmarkov(const gen & args,GIAC_CONTEXT){
    return undef;
    if (!is_squarematrix(args))
      return gensizeerr(contextptr);
    vector< vector<unsigned> > G,GRAPH_SCC;
    proba2adjacence(*args._VECTptr,G);
    tarjan(G,GRAPH_SCC);
    // For a matrix of transition
    // Look at each component: if it has all outgoing edges going to the same component, 
    // then this is a recurrent positive, and we can compute the invariant probability
    // For the remaining components:
    // Init list of transient components to empty
    // Set doit to true
    // Loop on the remaining component
    // Set doit to false
    // If all incoming edges of the component are from transient add to transient 
    //    and set doit to true
    matrice m;
    matrix_unsigned2matrice(GRAPH_SCC,m);
    return m;
  }
  static const char _classmarkov_s []="classmarkov";
  static define_unary_function_eval (__classmarkov,&_classmarkov,_classmarkov_s);
  define_unary_function_ptr5( at_classmarkov ,alias_at_classmarkov,&__classmarkov,0,true);


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
