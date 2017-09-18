/* -*- mode:C++ ; compile-command: "g++ -I.. -I../include -DHAVE_CONFIG_H -DIN_GIAC -DGIAC_GENERIC_CONSTANTS -fno-strict-aliasing -g -c misc.cc -Wall" -*- */
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
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
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
#include "sparse.h"
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
    int s=int(v.size());
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
    int s=int(x.size());
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
    int s=int(v.size());
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
    if (v0.type!=_VECT && v1.type==_VECT){
      gen tmp=v1;
      v1=_apply(makesequence(v0,v1),contextptr);
      v0=tmp;
    }
    if (v1.type!=_VECT && v0.type==_VECT)
      v1=_apply(makesequence(v1,v0),contextptr);
    if ( (v0.type!=_VECT) || (v1.type!=_VECT) )
      return gensizeerr(contextptr);
    vecteur & vx =*v0._VECTptr;
    vecteur & vy=*v1._VECTptr;
    s=int(vx.size());
    if (!s || vy.size()!=unsigned(s))
      return gendimerr(contextptr);
    // Using divided difference instead of the theoretical formula
    if (x.type==_VECT && x._VECTptr->empty()){
      vecteur res;
      interpolate(vx,vy,res,0);
      return res;
    }
    vecteur w=divided_differences(vx,vy);
    if (x==at_lagrange)
      return w;
    gen pi(1),res(w[s-1]);
    for (int i=s-2;i>=0;--i){
      res = res*(x-vx[i])+w[i];
      if (i%100==99) // otherwise segfault 
	res=ratnormal(res,contextptr);
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
    int s=int(v.size());
    if (s<2)
      return gentoofewargs("");
    gen e(v[0]),l(v[1]);
    if (e.type<=_POLY) return e;
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
    if ( !is_equal(arg))
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
    int r=int(mr.size());
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
    if (is_zero(args))
      //grad
      return angle_radian(contextptr)?cst_pi_over_2:(angle_degree(contextptr)?90:100);
#if 0
    if (abs_calc_mode(contextptr)==38)
      return cst_pi_over_2-atan(args,contextptr);
#endif
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
    if (is_equal(x))
      x=x._SYMBptr->feuille[0];
    if (w.size()>=5)
      X=symb_equal(x,symb_interval(w[3],w[4]));
    if (is_equal(X) && X._SYMBptr->feuille[1].is_symb_of_sommet(at_interval)){
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
    if (bound)
      F += preval(u*v,x,a,b,contextptr);
    else
      F += u*v;      
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
    if (args.type==_VECT && args.subtype==_SEQ__VECT && args._VECTptr->size()==2){
      gen p=evalf_double(args._VECTptr->back(),1,contextptr);
      if (p.type==_DOUBLE_ && p._DOUBLE_val>0){
	double eps=epsilon(contextptr);
	epsilon(p._DOUBLE_val,contextptr);
	gen res=epsilon2zero(args._VECTptr->front(),contextptr);
	epsilon(eps,contextptr);
	return res;
      }
    }
    return epsilon2zero(args,contextptr);
  }    
  static const char _epsilon2zero_s []="epsilon2zero";
  static define_unary_function_eval (__epsilon2zero,&_epsilon2zero,_epsilon2zero_s);
  define_unary_function_ptr5( at_epsilon2zero ,alias_at_epsilon2zero,&__epsilon2zero,0,true);

  gen _suppress(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_suppress,args);
    vecteur & v=*args._VECTptr;
    if (v.size()==3 && v[1].type==_INT_ && v[2].type==_INT_){
      int i1=v[1].val-(xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38);
      int i2=v[2].val-(xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38);
      if (i1 >i2 || i1<0 || i2 < 0)
	return gendimerr(contextptr);
      if (v[0].type==_VECT){
	vecteur w=*v[0]._VECTptr;
	if (i1>=int(w.size()) || i2>=int(w.size()))
	  return gendimerr(contextptr);
	return gen(mergevecteur(vecteur(w.begin(),w.begin()+i1),vecteur(w.begin()+i2+1,w.end())),v[0].subtype);
      }
      if (v[0].type==_STRNG){
	string s=*v[0]._STRNGptr;
	if (i1>=int(s.size()) || i2>=int(s.size()))
	  return gendimerr(contextptr);
	return string2gen(s.substr(0,i1)+s.substr(i2+1,s.size()-i2-1),false);
      }
      return gensizeerr(contextptr);
    }
    if (v.size()!=2)
      return gentypeerr(contextptr);
    gen l=v.front(),i=v.back();
    int ii=0;
    if (i.type==_VECT){
      i=sortad(*i._VECTptr,false,contextptr);
      if (i.type==_VECT){
	const_iterateur it=i._VECTptr->begin(),itend=i._VECTptr->end();
	for (;it!=itend;++it){
	  l=_suppress(makesequence(l,*it),contextptr);
	}
	return l;
      }
    }
    if (i.type==_INT_ )
      ii=i.val-(xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38);
    if (l.type==_STRNG){
      string res;
      string & s=*l._STRNGptr;
      int n=int(s.size());
      if (i.type==_INT_ && ii>=0 && ii<n)
	res=s.substr(0,ii)+s.substr(ii+1,n-ii-1);
      if (i.type==_STRNG){
	string & remove=*i._STRNGptr;
	int removen=int(remove.size());
	for (int j=0;j<n;++j){
	  int k=int(remove.find(s[j]));
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

  gen _insert(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    if (v.size()!=3)
      return gensizeerr(contextptr);
    gen i=v[1];
    if (!is_integral(i) || i.type!=_INT_) 
      return gensizeerr(contextptr);
    int ii=i.val-(xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38);
    if (v[0].type==_VECT){
      vecteur w=*v[0]._VECTptr;
      if (ii<0 || ii>int(w.size()))
	return gendimerr(contextptr);
      w.insert(w.begin()+ii,v[2]);
      return gen(w,v[0].subtype);
    }
    if (v[0].type==_STRNG){
      string s=*v[0]._STRNGptr;
      if (ii<0 || ii>int(s.size()))
	return gendimerr(contextptr);
      string add=(v[2].type==_STRNG)?*v[2]._STRNGptr:v[2].print(contextptr);
      s=s.substr(0,ii)+add+s.substr(ii,s.size()-ii);
      return string2gen(s,false);
    }
    return gensizeerr(contextptr);
  }    
  static const char _insert_s []="insert";
  static define_unary_function_eval (__insert,&_insert,_insert_s);
  define_unary_function_ptr5( at_insert ,alias_at_insert,&__insert,0,true);

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
      int s=int(v.size());
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

  int sum_degree(const index_m & v1,int vars){
    int i=0;
    for (index_t::const_iterator it=v1.begin();it!=v1.end() && it!=v1.begin()+vars;++it)
      i=i+(*it);
    return(i);
  }

  int total_degree(const polynome & p,int vars) {
    std::vector< monomial<gen> >::const_iterator it=p.coord.begin();
    std::vector< monomial<gen> >::const_iterator it_end=p.coord.end();
    int res=0;
    for (;it!=it_end;++it){
      int temp=sum_degree(it->index,vars);
      if (res<temp)
	res=temp;
    }
    return res;
  }


  gen _degree_(const gen & args,bool total,GIAC_CONTEXT){
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
      int s=int(v.size());
      if ( (args.subtype==_POLY1__VECT) || (s!=2) || (v[1].type!=_IDNT && v[1].type!=_VECT) )
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
    if (x.type==_VECT){
      if (total){
	int deg=0;
	if (aad.type==_POLY)
	  deg -= total_degree(*aad._POLYptr,int(x._VECTptr->size()));
	if (aan.type==_POLY)
	  deg += total_degree(*aan._POLYptr,int(x._VECTptr->size()));
	return deg;
      }
      int s=int(x._VECTptr->size());
      vecteur res(s);
      for (int i=0;i<s;++i){
	int deg=0;
	if (aad.type==_POLY)
	  deg -= aad._POLYptr->degree(i);;
	if (aan.type!=_POLY)
	  res[i]=deg;
	else
	  res[i]=deg+aan._POLYptr->degree(i);
      }
      return res;
    }
    int deg=0;
    if ( (aad.type==_POLY) && (aad._POLYptr->lexsorted_degree() ) )
      deg -= aad._POLYptr->lexsorted_degree();;
    if (aan.type!=_POLY)
      return deg;
    return deg+aan._POLYptr->lexsorted_degree();
  }    
  gen _degree(const gen & args,GIAC_CONTEXT){
    return _degree_(args,false,contextptr);
  }
  static const char _degree_s []="degree";
  static define_unary_function_eval (__degree,&_degree,_degree_s);
  define_unary_function_ptr5( at_degree ,alias_at_degree,&__degree,0,true);

  gen _total_degree(const gen & args,GIAC_CONTEXT){
    return _degree_(args,true,contextptr);
  }
  static const char _total_degree_s []="total_degree";
  static define_unary_function_eval (__total_degree,&_total_degree,_total_degree_s);
  define_unary_function_ptr5( at_total_degree ,alias_at_total_degree,&__total_degree,0,true);

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
      s=int(v.size());
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
    int s=int(v.size());
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
      int s=int(v.size());
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
      s=int(v.size());
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
    int nvar=int(w.size()); // number of var w.r.t. which we truncate
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
    int s=int(v.size()/2);
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
    int s=int(v.size());
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
    int n=int(b.size()-a.size())+v[2].val;
    for (int i=0;i<n;++i)
      a.push_back(zero);
    vecteur quo,rem;
    environment * env=new environment;
    DivRem(a,b,env,quo,rem);
    delete env;
    reverse(quo.begin(),quo.end());
    gen res(vecteur2polynome(quo,int(lv.size())));
    res=rdiv(res*bad,aad,contextptr);
    return r2e(res,lv,contextptr);
  }

  static const char _divpc_s []="divpc";
  static define_unary_function_eval (__divpc,&_divpc,_divpc_s);
  define_unary_function_ptr5( at_divpc ,alias_at_divpc,&__divpc,0,true);

  gen _ptayl(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen p,q,x;
    if (args.type!=_VECT){
      p=_POLY1__VECT;
      p.subtype=_INT_MAPLECONVERSION;
      return _series(makesequence(args,p),contextptr);
    }
    vecteur v=*args._VECTptr;
    int s=int(v.size());
    if (s<2)
      return gensizeerr(contextptr);
    if (s>3 || v[1].is_symb_of_sommet(at_equal) || (s==3 && v[2].type==_INT_)){
      p=_POLY1__VECT;
      p.subtype=_INT_MAPLECONVERSION;
      v.push_back(p);
      return _series(gen(v,_SEQ__VECT),contextptr);
    }
    p=v.front();
    q=v[1];
    if (p.type==_VECT)
      return taylor(*p._VECTptr,q,0);
    if (s==2)
      x=vx_var;
    else 
      x=v.back();
    if (is_integral(x)){
      p=_POLY1__VECT;
      p.subtype=_INT_MAPLECONVERSION;
      v.push_back(p);
      return _series(makesequence(gen(v,_SEQ__VECT)),contextptr);
    }
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
    return r2e(vecteur2polynome(res,int(lv.size())),lv,contextptr)/r2e(aad,lv,contextptr);
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
      gen gf=evalf_double(g._VECTptr->back(),1,contextptr);
      if (is_integral(gf))
	return gen2continued_fraction(g._VECTptr->front(),gf.val,contextptr);
      if (gf.type==_DOUBLE_){
	eps=gf._DOUBLE_val;
	g=evalf_double(g._VECTptr->front(),1,contextptr);
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
    vecteur v =(*g._VECTptr);
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
      int s=int(lv.size());
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
    int s=int(m.size());
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

  static void change_scale2(vecteur & v,const gen & g){
    gen l(g);
    for (unsigned i=1;i<v.size();++i){
      v[i]=v[i]/l;
      l=g*l;
    }
  }

  /*
  gen exptorootof(const gen & g,GIAC_CONTEXT){
    gen h=ratnormal(g/cst_two_pi/cst_i,contextptr);
    if (h.type!=_FRAC || h._FRACptr->num.type!=_INT_ || h._FRACptr->den.type!=_INT_)
      return symbolic(at_exp,g);
    int n=h._FRACptr->num.val,d=h._FRACptr->den.val;
    n=n%d;
    if (d<0){ d=-d; n=-n; }
    vecteur v=cyclotomic(d);
    vecteur w(absint(n)+1);
    w[0]=1;
    w=w%v;
    h=symbolic(at_rootof,makesequence(w,v));
    if (n>0)
      return h;
    return inv(h,contextptr);
  }
  const gen_op_context exp2rootof_tab[]={exptorootof,0};
  gen exp2rootof(const gen & g,GIAC_CONTEXT){
    return subst(g,exp_tab,exp2rootof_tab,false,contextptr);
  }
  gen _exp2rootof(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_exp2rootof(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_exp2rootof,contextptr);
    return exp2rootof(args,contextptr);
  }
  static const char _exp2rootof_s []="exp2rootof";
  static define_unary_function_eval (__exp2rootof,&giac::_exp2rootof,_exp2rootof_s);
  define_unary_function_ptr5( at_exp2rootof ,alias_at_exp2rootof,&__exp2rootof,0,true);
  */

  static gen pmin(const matrice & m,GIAC_CONTEXT){
    int s=int(m.size());
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
      return gen(t/lgcd(*t._VECTptr),_POLY1__VECT);
    else
      return t;
  }
  gen _pmin(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (is_squarematrix(g)){
      matrice &m =*g._VECTptr;
      vecteur w;
      gen p=m[0][0];
      if (p.type==_USER){
	std_matrix<gen> M;
	matrice2std_matrix_gen(m,M);
	mod_pcar(M,w,true);
	return gen(w,_POLY1__VECT);
      }
      if (p.type==_MOD && (p._MODptr+1)->type==_INT_){
	gen mg=unmod(m);
	if (mg.type==_VECT){
	  matrice M=*mg._VECTptr;
	  vector< vector<int> > N;
	  int modulo=(p._MODptr+1)->val;
	  bool krylov=true;
	  vector<int> res;
	  if (mod_pcar(M,N,modulo,krylov,res,contextptr,true)){
	    vector_int2vecteur(res,w);
	    return makemod(gen(w,_POLY1__VECT),modulo);
	    // environment env; w=modularize(w,modulo,&env);
	    // return gen(w,_POLY1__VECT);
	  }
	}
      }
      if (is_integer_matrice(m)){
	w=mpcar_int(m,true,contextptr,true);
	return gen(w,_POLY1__VECT);
      }
      if (poly_pcar_interp(m,w,true,contextptr))
	return gen(w,_POLY1__VECT);
      if (proba_epsilon(contextptr) && probabilistic_pmin(m,w,true,contextptr))
	return gen(w,_POLY1__VECT);
      return pmin(m,contextptr);
    }
    if (is_integer(g) || g.type==_MOD)
      return gen(makevecteur(1,-g),_POLY1__VECT);
    // if (g.type==_FRAC) return gen(makevecteur(g._FRACptr->den,-g._FRACptr->num),_POLY1__VECT);
    if (is_cinteger(g) && g.type==_CPLX){
      gen a=*g._CPLXptr,b=*(g._CPLXptr+1);
      // z=(a+i*b), (z-a)^2=-b^2
      return gen(makevecteur(1,-2*a,a*a+b*b),_POLY1__VECT);
    }
    if (g.type==_USER){
#ifndef NO_RTTI
      if (galois_field * gf=dynamic_cast<galois_field *>(g._USERptr)){
	if (gf->a.type!=_VECT || gf->P.type!=_VECT || !is_integer(gf->p))
	  return gensizeerr("Bad GF element");
	environment env;
	env.modulo=gf->p;
	env.pn=env.modulo;
	env.moduloon=true;
	// compute 1,a,a^2,...,a^n in lines then transpose and find ker
	int n=int(gf->P._VECTptr->size())-1;
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
#endif
    }
    if (g.type==_EXT)
      return minimal_polynomial(g,true,contextptr);
    if (g.type!=_VECT){
      gen g_(g);
      //if (!lop(g_,at_exp).empty())
      g_=cossinexp2rootof(g_,contextptr);
      vecteur v=alg_lvar(g_);
      if (v.size()==1 && v.front().type==_VECT && v.front()._VECTptr->empty()){
	gen tmp=e2r(g_,v,contextptr);
	gen d=1;
	if (tmp.type==_FRAC){
	  d=tmp._FRACptr->den;
	  tmp=tmp._FRACptr->num;
	  if (d.type==_CPLX){
	    tmp=tmp*conj(d,contextptr);
	    d=d*conj(d,contextptr);
	  }
	}
	if (tmp.type==_POLY && tmp._POLYptr->dim==0)
	  tmp=tmp._POLYptr->coord.front().value;
	if (tmp.type==_EXT){
	  if (has_i(*tmp._EXTptr)){
	    gen r,i;
	    reim(tmp,r,i,contextptr);
	    tmp=r+algebraic_EXTension(makevecteur(1,0),makevecteur(1,0,1))*i;
	    while (tmp.type==_FRAC){
	      d=d*tmp._FRACptr->den;
	      tmp=tmp._FRACptr->num;
	    }
	  }
	  tmp=minimal_polynomial(tmp,true,contextptr);
	  if (tmp.type!=_VECT)
	    return gensizeerr(contextptr);
	  vecteur v=*tmp._VECTptr;
	  change_scale2(v,d);
	  return gen(v,_POLY1__VECT);
	}
      }
    }
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return symbolic(at_pmin,g);
    vecteur & v(*g._VECTptr);
    if (!is_squarematrix(v.front())){
      gen res=_pmin(v.front(),contextptr);
      if (res.type==_VECT)
	return symb_horner(*res._VECTptr,v.back());
      return gensizeerr(contextptr);
    }
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
    int s=int(fv.size());
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
    unsigned int s=unsigned(fv.size());
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
    if (g._VECTptr->size()==2 && g._VECTptr->front().type==_VECT && g._VECTptr->back()==at_vector){
      aplatir(*g._VECTptr->front()._VECTptr,v);
      return l2norm(v,contextptr);      
    }
    if (ckmatrix(g)){
      gen tmp=_SVL(g,contextptr);
      if (tmp.type==_VECT && tmp._VECTptr->size()==2 && tmp._VECTptr->back().type==_VECT) 
	tmp=tmp._VECTptr->back();
      return _max(tmp,contextptr);
    }
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

  gen l1norm(const vecteur & v,GIAC_CONTEXT){
    gen res;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      res=res+linfnorm(*it,contextptr);
    return res;
  }

  gen _l1norm(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=remove_at_pnt(g0);
    if (g.type==_VECT && g.subtype==_VECTOR__VECT)
      g=vector2vecteur(*g._VECTptr);
    if (g.type!=_VECT)
      return linfnorm(g,contextptr);
    if (g._VECTptr->size()==2 && g._VECTptr->front().type==_VECT && g._VECTptr->back()==at_vector){
      vecteur v;
      aplatir(*g._VECTptr->front()._VECTptr,v);
      return l1norm(v,contextptr);      
    }
    if (ckmatrix(g))
      return _rowNorm(mtran(*g._VECTptr),contextptr);
    return l1norm(*g._VECTptr,contextptr);
  }
  static const char _l1norm_s []="l1norm";
  static define_unary_function_eval (__l1norm,&_l1norm,_l1norm_s);
  define_unary_function_ptr5( at_l1norm ,alias_at_l1norm,&__l1norm,0,true);

  gen _linfnorm(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=remove_at_pnt(g0);
    if (g.type==_VECT && g.subtype==_VECTOR__VECT)
      g=vector2vecteur(*g._VECTptr);
    if (g.type!=_VECT)
      return linfnorm(g,contextptr);
    if (g._VECTptr->size()==2 && g._VECTptr->front().type==_VECT && g._VECTptr->back()==at_vector){
      vecteur v;
      aplatir(*g._VECTptr->front()._VECTptr,v);
      return linfnorm(v,contextptr);      
    }
    if (ckmatrix(g))
      return _rowNorm(g,contextptr);
    return linfnorm(*g._VECTptr,contextptr);
  }
  static const char _linfnorm_s []="linfnorm";
  static define_unary_function_eval (__linfnorm,&_linfnorm,_linfnorm_s);
  define_unary_function_ptr5( at_linfnorm ,alias_at_linfnorm,&__linfnorm,0,true);

  gen _frobenius_norm(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=remove_at_pnt(g0);
    if (g.type==_VECT && g.subtype==_VECTOR__VECT)
      g=vector2vecteur(*g._VECTptr);
    vecteur v;
    if (ckmatrix(g))
      aplatir(*g._VECTptr,v);
    else
      v=*g._VECTptr;
    return l2norm(v,contextptr);
  }
  static const char _frobenius_norm_s []="frobenius_norm";
  static define_unary_function_eval (__frobenius_norm,&_frobenius_norm,_frobenius_norm_s);
  define_unary_function_ptr5( at_frobenius_norm ,alias_at_frobenius_norm,&__frobenius_norm,0,true);

  gen _matrix_norm(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    if (g0.type!=_VECT || g0._VECTptr->empty())
      return gentypeerr(contextptr);
    if (g0._VECTptr->back()==0){
      gen g=g0._VECTptr->front();
      if (!ckmatrix(g))
	return _linfnorm(g,contextptr);
      vecteur & v =*g._VECTptr;
      gen res=0;
      for (unsigned i=0;i<v.size();++i){
	res=max(res,linfnorm(v[i],contextptr),contextptr);
      }
      return res;
    }
    if (g0._VECTptr->back()==1)
      return _l1norm(g0._VECTptr->front(),contextptr);
    if (g0._VECTptr->back()==2)
      return _l2norm(g0._VECTptr->front(),contextptr);
    if (is_inf(g0._VECTptr->back()))
      return _linfnorm(g0._VECTptr->front(),contextptr);
    return _frobenius_norm(g0,contextptr);
  }
  static const char _matrix_norm_s []="matrix_norm";
  static define_unary_function_eval (__matrix_norm,&_matrix_norm,_matrix_norm_s);
  define_unary_function_ptr5( at_matrix_norm ,alias_at_matrix_norm,&__matrix_norm,0,true);

  gen _dotprod(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if ( (g.type!=_VECT) || (g._VECTptr->size()!=2))
      return gentypeerr(contextptr);
    vecteur v=*g._VECTptr;
    if (v[0].type==_VECT && v[1].type==_VECT)
      return scalarproduct(*v[0]._VECTptr,*v[1]._VECTptr,contextptr);
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
    int l=int(v.size());
    if (l==2 && ckmatrix(v[0])){
      if (v[1]==at_left){
	matrice m=*v[0]._VECTptr,res;
	int n=int(m.size());
	res.reserve(n);
	for (int i=0;i<n;++i){
	  vecteur v=*m[i]._VECTptr;
	  int s=int(v.size());
	  for (int j=i+1;j<s;++j)
	    v[j]=0;
	  res.push_back(v);
	}
	return res;
      }
      if (v[1]==at_right){
	matrice m=*v[0]._VECTptr,res;
	int n=int(m.size());
	res.reserve(n);
	for (int i=0;i<n;++i){
	  vecteur v=*m[i]._VECTptr;
	  for (int j=0;j<i;++j)
	    v[j]=0;
	  res.push_back(v);
	}
	return res;
      }
      if (v[1]==at_lu){
	matrice m=*v[0]._VECTptr,resl,resu,diag;
	int n=int(m.size());
	resl.reserve(n); resu.reserve(n);
	for (int i=0;i<n;++i){
	  vecteur v=*m[i]._VECTptr;
	  diag.push_back(v[i]);
	  for (int j=0;j<=i;++j)
	    v[j]=0;
	  resu.push_back(v);
	  v=*m[i]._VECTptr;
	  int s=int(v.size());
	  for (int j=i;j<s;++j)
	    v[j]=0;
	  resl.push_back(v);
	}
	return makesequence(resl,diag,resu);
      }
    }
    if (l==3 && v[0].type==_VECT && v[1].type==_VECT && v[2].type==_VECT && v[0]._VECTptr->size()+1==v[1]._VECTptr->size() && v[0]._VECTptr->size()==v[2]._VECTptr->size() ){
      vecteur & l=*v[0]._VECTptr;
      vecteur & d=*v[1]._VECTptr;
      vecteur & u=*v[2]._VECTptr;
      int n=int(d.size());
      matrice res(n);
      for (int i=0;i<n;++i){
	vecteur w(n);
	if (i)
	  w[i-1]=l[i-1];
	w[i]=d[i];
	if (i<n-1)
	  w[i+1]=u[i];
	res[i]=w;
      }
      return res;
    }
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
    l=int(v.size());
    matrice res;
    if (l && ckmatrix(v.front()) ){
      int s=0,r=0;
      for (int i=0;i<l;++i){
	if (!is_squarematrix(v[i]))
	  return gentypeerr(contextptr);
	s += int(v[i]._VECTptr->size());
      }
      for (int i=0;i<l;++i){
	vecteur & current=*v[i]._VECTptr;
	int c=int(current.size());
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
    if (!v.empty() && v[0].type==_POLY)
      v.insert(v.begin()+1,vecteur(0));
    int s=int(v.size());
    if (s==2 && v[1].is_symb_of_sommet(at_pow)){
      gen & f = v[1]._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2 && f._VECTptr->back().type==_INT_){
	v[1]=f._VECTptr->front();
	service=f._VECTptr->back().val;
      }
    }
    if (s>=2 && v[1].type==_VECT){
      vecteur l(*v[1]._VECTptr);
      int outerdim=int(l.size());
      lvar(v[0],l);
      int innerdim=int(l.size())-outerdim;
      fraction f(1);
      if (v[0].type==_POLY)
	f.num=v[0];
      else
	f=sym2r(v[0],l,contextptr);
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
	if (service==-4)
	  return is_integer(f.num)?f.num:plus_one;
	return gensizeerr(contextptr);
      }
      if (service==-4)
	return Tcontent(*f.num._POLYptr);
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
      case -4:
	return (is_integer(f)?f:plus_one)/(is_integer(deno)?deno:plus_one);
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
    case -4:
      f=_lgcd(f,contextptr);
      return _icontent(makesequence(f,lvar(f)),contextptr)/(is_integer(deno)?deno:plus_one);
    }
    vecteur & w=*f._VECTptr;
    int ss=int(w.size());
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

  gen _icontent(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return primpartcontent(g,-4,contextptr);
  }
  static const char _icontent_s []="icontent";
  static define_unary_function_eval (__icontent,&_icontent,_icontent_s);
  define_unary_function_ptr5( at_icontent ,alias_at_icontent,&__icontent,0,true);

  gen _coeff(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT && !g._VECTptr->empty() && 
	(g._VECTptr->back().type==_INT_ || g._VECTptr->back().type==_DOUBLE_)){
      vecteur v=*g._VECTptr;
      is_integral(v.back());
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
    gen_sort_f(w.begin(),w.end(),first_ascend_sort);
    w=mtran(w);
    // w[0]=data, w[1]=frequencies
    vecteur data=*w[0]._VECTptr;
    vecteur freq=*w[1]._VECTptr;
    gen sigma=d*prodsum(freq,false);
    if (is_undef(sigma)) return sigma;
    int s=int(freq.size());
    gen partial_sum;
    for (int i=0;i<s;++i){
      partial_sum=partial_sum+freq[i];
      if (!is_zero(partial_sum) && is_strictly_greater(partial_sum,sigma,contextptr))
	return data[i];
      if (partial_sum==sigma && i<s)
	return (i==s-1 || (calc_mode(contextptr)!=1 && abs_calc_mode(contextptr)!=38) )?data[i]:(data[i]+data[i+1])/2;
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
	islesscomplexthanf_sort(v.begin(),v.end());
	return v[int(std::ceil(v.size()/2.0))-1]; // v[(v.size()-1)/4];
      }
      matrice mt=mtran(ascsort(mtran(vecteur(1,v)),true));
      if ( (calc_mode(contextptr)==1 || abs_calc_mode(contextptr)==38) && !v.empty() && !(v.size()%2))
	return (mt[v.size()/2][0]+mt[v.size()/2-1][0])/2;
      return mt[int(std::ceil(v.size()/2.0))-1][0];
    }
    else
      v=ascsort(v,true);
    v=mtran(v);
    if ( (calc_mode(contextptr)==1 || abs_calc_mode(contextptr)==38) && !v.empty() && !(v.size()%2))
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
	islesscomplexthanf_sort(v.begin(),v.end());
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
	islesscomplexthanf_sort(v.begin(),v.end());
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
    if (v.size()<2 || v.front().type!=_VECT)
      return gensizeerr(contextptr);
    if (g.type==_VECT && g.subtype==_SEQ__VECT && v.size()==3){
      gen tmp=evalf_double(v.back(),1,contextptr);
      if (tmp.type!=_DOUBLE_)
	return gensizeerr(contextptr);
      double d=tmp._DOUBLE_val;
      if (d<=0 || d>=1)
	return gendimerr(contextptr);
      return freq_quantile(makevecteur(v[0],v[1]),d,contextptr);
    }
    if (v.size()!=2)
      return gensizeerr(contextptr);
    bool vect=v.back().type==_VECT;
    vecteur w=gen2vecteur(v.back()),res;
    v=*v.front()._VECTptr;
    bool matrix=true;
    if (!ckmatrix(v)){
      matrix=false;
      if (!is_fully_numeric(evalf(v,1,contextptr))){
	islesscomplexthanf_sort(v.begin(),v.end());
	for (unsigned j=0;j<w.size();++j){
	  gen tmp=evalf_double(w[j],1,contextptr);
	  if (tmp.type!=_DOUBLE_ || tmp._DOUBLE_val<=0 || tmp._DOUBLE_val>=1)
	    res.push_back(undef);
	  else
	    res.push_back(v[int(std::ceil(tmp._DOUBLE_val*v.size()))-1]);
	}
	return vect?res:res.front(); 
      }
      v=ascsort(mtran(vecteur(1,v)),true);
    }
    else
      v=ascsort(v,true);
    v=mtran(v);
    for (unsigned j=0;j<w.size();++j){
      gen tmp=evalf_double(w[j],1,contextptr);
      if (tmp.type!=_DOUBLE_ || tmp._DOUBLE_val<=0 || tmp._DOUBLE_val>=1)
	res.push_back(undef);
      else {
	gen data=v[int(std::ceil(tmp._DOUBLE_val*v.size()))-1];
	if (!matrix && data.type==_VECT && data._VECTptr->size()==1)
	  data=data._VECTptr->front();
	res.push_back(data);
      }
    }
    return vect?res:res.front();
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
	islesscomplexthanf_sort(v.begin(),v.end());
	int s=int(v.size());
	return makevecteur(v[0],v[int(std::ceil(s/4.))-1],v[int(std::ceil(s/2.))-1],v[int(std::ceil(3*s/4.))-1],v[s-1]);
      }
      v=ascsort(mtran(vecteur(1,v)),true);
    }
    else
      v=ascsort(v,true);
    v=mtran(v);
    int s=int(v.size());
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
	if (is_equal(tmp)){
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
    int s=int(v.size());
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
    int ls=int(legendes.size());
    vecteur affichages(gen2vecteur(attributs[0]));
    int as=int(affichages.size());
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
    int sv=int(v.size());
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
    int n=int(v1.size());
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
      if (s.type!=_VECT && is_greater(1,s,contextptr) && withstddev==2)
	*logptr(contextptr) << "stddevp called with N<=1, perhaps you are misusing this command with frequencies" << endl;
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
    int s=int(v.size());
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
    int nd=is_distribution(g);
    if (g.type==_SYMB && nd){
      gen f=g._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==1)
	f=f._VECTptr->front();
      int s=f.type==_VECT?int(f._VECTptr->size()):1;
      if (s!=distrib_nargs(nd))
	return gensizeerr(contextptr);
      if (nd==1)
	return f[0];
      if (nd==2)
	return f[0]*f[1];
      if (nd==3)
	return f[0]*(1-f[1])/f[1];
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
      if (nd==12)
	return inv(f[0],contextptr);
      if (nd==13)
	return (f[1]+f[0])/2;
      if (nd==14)
	return inv(f[0],contextptr);
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
      int s=f.type==_VECT?int(f._VECTptr->size()):1;
      if (s!=distrib_nargs(nd))
	return gensizeerr(contextptr);
      if (nd==1)
	return f[1];
      if (nd==2)
	return sqrt(f[0]*f[1]*(1-f[1]),contextptr);
      if (nd==3)
	return sqrt(f[0]*(1-f[1]),contextptr)/f[1];
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
      if (nd==12)
	return sqrt(1-f[0],contextptr)/f[0];
      if (nd==13)
	return (f[1]-f[0])*sqrt(3,contextptr)/6;
      if (nd==14)
	return inv(f[0],contextptr);
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

  vecteur genpoint2vecteur(const gen & g,GIAC_CONTEXT){
    vecteur v(gen2vecteur(g));
    for (unsigned i=0;i<v.size();++i){
      gen & tmp = v[i];
      if (tmp.is_symb_of_sommet(at_pnt))
	tmp=complex2vecteur(remove_at_pnt(tmp),contextptr);
    }
    return v;
  }

  static vecteur covariance_correlation(const gen & g,const gen & u1,const gen & u2,int xcol,int ycol,int freqcol,GIAC_CONTEXT){
    if (is_undef(g))
      return makevecteur(g,g);
    vecteur v(genpoint2vecteur(g,contextptr));
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
      int s=int(it->_VECTptr->size());
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
      int s=int(v.size());
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
      gv=genpoint2vecteur(g,contextptr);
      if (!ckmatrix(gv) || gv._VECTptr->empty()){
	gv=gensizeerr(contextptr);
	return;
      }
      if (gv._VECTptr->front()._VECTptr->size()>2)
	freqcol=2;
      if (gv._VECTptr->front()._VECTptr->front().type==_STRNG)
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
    if (g.type==_REAL)
      return _milieu(g,contextptr);
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
      int ws=int(w.size());
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
    vecteur v(genpoint2vecteur(g,contextptr));
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
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen last=_floor(g._VECTptr->back(),contextptr);
    if (last.type!=_INT_)
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
    int s=int(v.size());
    if (s<2 || s>3)
      return gendimerr(contextptr);
    gen data=v[0];
    if (data.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & w=*data._VECTptr;
    int n=int(w.size());
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
    int s=int(v.size());
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
    islesscomplexthanf_sort(M.begin(),M.end());
    reverse(M.begin(),M.end());
    int Ms=int(M.size());
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
      int s=int(v.front()._VECTptr->size());
      if (s==1)
	w=*evalf_double(mtran(v)[0],1,contextptr)._VECTptr;
      else
	return vector<double>(0);
    }
    else
      w=*evalf_double(v,1,contextptr)._VECTptr;
    // vector will be sorted keeping only DOUBLE data
    int s=int(w.size());
    vector<double> w1;
    w1.reserve(s);
    for (int i=0;i<s;++i){
      if (w[i].type==_DOUBLE_)
	w1.push_back(w[i]._DOUBLE_val);
    }
    sort(w1.begin(),w1.end());
    s=int(w1.size());
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
	gen_sort_f(tmp.begin(),tmp.end(),first_ascend_sort);
	tmp=mtran(tmp);
	vecteur tmpval=*evalf_double(tmp[0],1,contextptr)._VECTptr;
	vecteur tmpeff=*tmp[1]._VECTptr;
	if (tmpval.front().type!=_DOUBLE_ || tmpval.back().type!=_DOUBLE_)
	  return vecteur(1,undef);
	double kbegin=std::floor((tmpval.front()._DOUBLE_val-class_minimum)/class_size);
	double kend=std::floor((tmpval.back()._DOUBLE_val-class_minimum)/class_size);
	int s=int(tmpval.size()),i=0;
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
    double kbegin=std::floor((w1.front()-class_minimum)/class_size);
    double kend=std::floor((w1.back()-class_minimum)/class_size);
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
    int s=int(intervalles.size());
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
    if (!with_class_min || debut<=-1e307)
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
      if (it+1!=itend){
	g=evalf_double(*(it+1),1,contextptr);
	if (g.type!=_DOUBLE_)
	  return vecteur(1,gensizeerr(contextptr));
	fin=(milieu+g._DOUBLE_val)/2;
      }
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
    if (class_size<=0){
      // find class_minimum and class_size from data and number of classes
      int nc=int(class_minimum); // arg passed is the number of classes
      vector<double> w=prepare_effectifs(v,contextptr);
      if (w.size()<2)
	return gensizeerr(contextptr);
      class_minimum=w.front();
      class_size=((w.back()-w.front())*(1+1e-12))/nc;
    }
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
    int s=int(w1.size());
    if (!s)
      return gendimerr(contextptr);
    // class_min + k*class_size <= mini hence k
    double kbegin=std::floor((w1.front()-class_minimum)/class_size);
    double kend=std::floor((w1.back()-class_minimum)/class_size);
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
    if (g.type==_SYMB && is_distribution(g)){
      vecteur v(gen2vecteur(g._SYMBptr->feuille));
      v.insert(v.begin(),g._SYMBptr->sommet);
      return _histogram(gen(v,_SEQ__VECT),contextptr);
    }
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur args;
    if (g.subtype==_SEQ__VECT)
      args=*g._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(args,attributs,contextptr);
    args=vecteur(args.begin(),args.begin()+s);
    int nd;
    if (s>=1 && (nd=is_distribution(args[0]))){
      if (args[0].type==_SYMB){
	vecteur tmp(gen2vecteur(args[0]._SYMBptr->feuille));
	for (unsigned i=0;i<tmp.size();++i)
	  args.insert(args.begin()+1+i,tmp[i]); // inefficient ...
	args[0]=args[0]._SYMBptr->sommet;
	s+=int(tmp.size());
      }
      gen a,b;
      if (distrib_support(nd,a,b,true) || s!=distrib_nargs(nd)+1)
	return gensizeerr(contextptr);
      args.push_back(vx_var);
      gen res;
      if (args[0].type==_FUNC)
	res=symbolic(*args[0]._FUNCptr,gen(vecteur(args.begin()+1,args.end()),_SEQ__VECT));
      else
	res=args[0](gen(vecteur(args.begin()+1,args.end()),_SEQ__VECT),contextptr);
      if (nd==2) // binomial
	b=args[1];
      if (a.type!=_INT_ || !is_integral(b) || b.type!=_INT_ || b.val<=0)
	return gensizeerr(contextptr);
      int A=a.val,B=b.val;
      vecteur v;
      for (int i=A;i<=B;++i){
	gen y=subst(res,vx_var,i,false,contextptr);
	vecteur w=makevecteur(i-.5,i+.5,i+.5+cst_i*y,i-.5+cst_i*y);
	w.push_back(w.front());
	v.push_back(pnt_attrib(gen(w,_GROUP__VECT),attributs,contextptr));
      }
      return v;
    }
    if (s>=2){
      if (args[0].type!=_VECT)
	return gensizeerr(contextptr);
      vecteur data=*args[0]._VECTptr;
      if (data.empty())
	return gensizeerr(contextptr);
      if (data.front().type==_VECT && data.front()._VECTptr->size()==1 && ckmatrix(data))
	data=*mtran(data).front()._VECTptr;
      gen arg1=evalf_double(args[1],1,contextptr);
      if (ckmatrix(data)&&arg1.type==_DOUBLE_){ // [ [center, effectif] ... ], min
	data=mtran(data); // 1st line = all centers
	if (data.size()!=2)
	  return gensizeerr(contextptr);
	data[0]=centres2intervalles(*data[0]._VECTptr,arg1._DOUBLE_val,true,contextptr);
	if (is_undef(data[0]))
	  return gensizeerr(contextptr);
	data=mtran(data);
	gen g=data[0][0];
	if (g.is_symb_of_sommet(at_interval) && g._SYMBptr->feuille.type==_VECT && g._SYMBptr->feuille._VECTptr->size()==2){
	  gen g1=g._SYMBptr->feuille._VECTptr->front();
	  g1=evalf_double(g1,1,contextptr);
	  gen g2=g._SYMBptr->feuille._VECTptr->back();
	  g2=evalf_double(g2,1,contextptr);
	  if (g1.type==_DOUBLE_ && g2.type==_DOUBLE_)
	    return histogram(data,g1._DOUBLE_val,(g2-g1)._DOUBLE_val,attributs,contextptr);
	}
	return histogram(data,0.0,0.0,attributs,contextptr);
      }
      if (s==3){
	gen arg2=evalf_double(args[2],1,contextptr);
	if (arg1.type==_DOUBLE_ && arg2.type==_DOUBLE_)
	  return histogram(data,arg1._DOUBLE_val,arg2._DOUBLE_val,attributs,contextptr);
      }
      if (s==2 && is_integral(arg1) && arg1.type==_INT_ && arg1.val>0)
	return histogram(data,arg1.val,0.0,attributs,contextptr);
      if (s==2 && args[1].type==_VECT)
	return _histogram(gen(makevecteur(mtran(args),-1.1e307),_SEQ__VECT),contextptr);
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
	return histogram(data,0.0,1e-14,attributs,contextptr);
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
    double total=double(w.size());
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
    double class_min=class_minimum;//,class_s=class_size;
    if (g0.type==_VECT && g0.subtype==_SEQ__VECT && g0._VECTptr->size()==2){
      vecteur v = *g._VECTptr;
      gen tmp=evalf_double(v[1],1,contextptr);
      if (tmp.type!=_DOUBLE_) {
	if (ckmatrix(g)){
	  // if (!v[0]._VECTptr->front().is_symb_of_sommet(at_interval)) v[0]=centres2intervalles(*v[0]._VECTptr,-1.1e307,true,contextptr);
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
    int s=int(m[0]._VECTptr->size());
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
      bool interv=false;
      for (;it!=itend;++it){
	vecteur & v = *it->_VECTptr;
	gen tmp=evalf_double(v[k],1,contextptr);
	if (tmp.type!=_DOUBLE_)
	  return gensizeerr(contextptr);
	// class_s = tmp._DOUBLE_val - x;
	n = n + (x=tmp._DOUBLE_val) ;
	if (v.front().is_symb_of_sommet(at_interval)){
	  interv=true;
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
      vecteur respnt;
      vector<xeff>::const_iterator jt=veff.begin(),jtend=veff.end();
      double cumul=0,oldcumul=0;
      for (;jt!=jtend;++jt){
	cumul += jt->eff/n ;
	if (!interv)
	  res.push_back(gen(jt->x)+cst_i*gen(oldcumul));
	res.push_back(gen(jt->x)+cst_i*gen(cumul));
	oldcumul=cumul;
	respnt.push_back(symb_pnt(gen(jt->x)+cst_i*gen(cumul),k+_POINT_WIDTH_2,contextptr));
      }
      ans.push_back(symb_pnt(gen(res,_GROUP__VECT),k,contextptr));
      ans.push_back(respnt);
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
      int s=int(args.size());
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
      int l=int(g[1]._VECTptr->size());
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
	int v0s=int(v0.size());
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
    s=int(v.size());
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
  static gen scatterplot(const gen & g,int mode,GIAC_CONTEXT){
    bool polygone=bool(mode&1),scatter=bool(mode&2),bar=bool(mode &4);
    vecteur v(gen2vecteur(g));
    vecteur attr(1,default_color(contextptr));
    int s=read_attributs(v,attr,contextptr);
    if (s==1 && ckmatrix(v.front()))
      v=*v.front()._VECTptr;
    else
      v=vecteur(v.begin(),v.begin()+s);
    if (g.type==_VECT && s==2 && g.subtype==_SEQ__VECT){
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
      int s=int(res.size());
      for (int i=0;i<s;++i){
	res[i]=symb_pnt(res[i],attributs[0],contextptr);
      }
      return gen(res,_SEQ__VECT);
    }
    if (!ckmatrix(v)||v.empty() || (ncol=unsigned(vf._VECTptr->size()))<2)
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
	  if (is_equal(tmp))
	    read_attributs(vecteur(1,tmp),attributs,contextptr);
	  else {
	    if (polygone)
	      res.push_back(it->_VECTptr->front()+cst_i*tmp);
	    if (scatter)
	      vres.push_back(symb_pnt_name(it->_VECTptr->front()+cst_i*tmp,attributs[0],string2gen(( (it==v.begin() && !polygone) ?gen2string(attributs[1]):""),false),contextptr));
	    if (bar)
	      vres.push_back(symb_segment(it->_VECTptr->front(),it->_VECTptr->front()+cst_i*tmp,attributs,_GROUP__VECT,contextptr));
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
    return scatterplot(g,2,contextptr);
  }
  static const char _scatterplot_s []="scatterplot";
static define_unary_function_eval (__scatterplot,&_scatterplot,_scatterplot_s);
  define_unary_function_ptr5( at_scatterplot ,alias_at_scatterplot,&__scatterplot,0,true);

  static const char _nuage_points_s []="nuage_points";
static define_unary_function_eval (__nuage_points,&_scatterplot,_nuage_points_s);
  define_unary_function_ptr5( at_nuage_points ,alias_at_nuage_points,&__nuage_points,0,true);

  gen _polygonplot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return scatterplot(g,1,contextptr);
  }
  static const char _polygonplot_s []="polygonplot";
static define_unary_function_eval (__polygonplot,&_polygonplot,_polygonplot_s);
  define_unary_function_ptr5( at_polygonplot ,alias_at_polygonplot,&__polygonplot,0,true);

  static const char _ligne_polygonale_s []="ligne_polygonale";
static define_unary_function_eval (__ligne_polygonale,&_polygonplot,_ligne_polygonale_s);
  define_unary_function_ptr5( at_ligne_polygonale ,alias_at_ligne_polygonale,&__ligne_polygonale,0,true);

  gen _polygonscatterplot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return scatterplot(g,3,contextptr);
  }
  static const char _polygonscatterplot_s []="polygonscatterplot";
static define_unary_function_eval (__polygonscatterplot,&_polygonscatterplot,_polygonscatterplot_s);
  define_unary_function_ptr5( at_polygonscatterplot ,alias_at_polygonscatterplot,&__polygonscatterplot,0,true);

  static const char _ligne_polygonale_pointee_s []="ligne_polygonale_pointee";
static define_unary_function_eval (__ligne_polygonale_pointee,&_polygonscatterplot,_ligne_polygonale_pointee_s);
  define_unary_function_ptr5( at_ligne_polygonale_pointee ,alias_at_ligne_polygonale_pointee,&__ligne_polygonale_pointee,0,true);

  gen _batons(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return scatterplot(g,4,contextptr);
  }
  static const char _batons_s []="batons";
static define_unary_function_eval (__batons,&_batons,_batons_s);
  define_unary_function_ptr5( at_batons ,alias_at_batons,&__batons,0,true);

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
      int ts=int(tmp.size());
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
  gen _diagramme_batons(const gen & g_,GIAC_CONTEXT){
    gen g(g_);
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur vals,names,attributs,res;
    double largeur=.4;
    if (g.type==_VECT && g.subtype==_SEQ__VECT){
      vecteur v=*g._VECTptr;
      for (unsigned i=0;i<v.size();++i){
	if (v[i].is_symb_of_sommet(at_equal) && v[i]._SYMBptr->feuille.type==_VECT && v[i]._SYMBptr->feuille._VECTptr->front()==at_size){
	  gen tmp=v[i]._SYMBptr->feuille._VECTptr->back();
	  tmp=evalf_double(tmp,1,contextptr);
	  if (tmp.type!=_DOUBLE_ || tmp._DOUBLE_val<=0 || tmp._DOUBLE_val>1)
	    return gensizeerr(contextptr);
	  largeur=tmp._DOUBLE_val;
	  v.erase(v.begin()+i);
	  break;
	}
      }
      if (v.size()==1)
	g=v.front();
      else
	g=gen(v,_SEQ__VECT);
    }
    largeur /=2;
    gen errcode=read_camembert_args(g,vals,names,attributs,contextptr);
    if (is_undef(errcode)) return errcode;
    vecteur attr(gen2vecteur(attributs[0]));
    int ncamemberts=int(vals.size()),s=int(vals.front()._VECTptr->size()),t=int(attr.size());
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
	gen tmp(makevecteur(xy+i+largeur+cst_i*Vals[i],xy+i+largeur,xy+i-largeur,xy+i-largeur+cst_i*Vals[i],xy+i+largeur+cst_i*Vals[i]),_LINE__VECT);
	res.push_back(symb_pnt_name(tmp,i<t?attr[i]:((i==7?0:i) | _FILL_POLYGON | _QUADRANT2),names[i],contextptr));
      }
    }
    return res;
  }
  static const char _diagramme_batons_s []="bar_plot";
  static define_unary_function_eval (__diagramme_batons,&_diagramme_batons,_diagramme_batons_s);
  define_unary_function_ptr5( at_diagramme_batons ,alias_at_diagramme_batons,&__diagramme_batons,0,true);

  static const char _diagrammebatons_s []="barplot";
  static define_unary_function_eval (__diagrammebatons,&_diagramme_batons,_diagrammebatons_s);
  define_unary_function_ptr5( at_diagrammebatons ,alias_at_diagrammebatons,&__diagrammebatons,0,true);

  gen _camembert(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur vals,names,attributs,res;
    gen errcode=read_camembert_args(g,vals,names,attributs,contextptr);
    if (is_undef(errcode)) return errcode;
    vecteur attr(gen2vecteur(attributs[0]));
    int ncamemberts=int(vals.size()),s=int(vals.front()._VECTptr->size()),t=int(attr.size());
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
	  sprintfdouble(ss,"%.4g",da100);
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
    int s=int(l0.size());
    for (int i=0;i<s;++i){
      if (l0[i].type==_VECT)
	l=mergevecteur(l,*l0[i]._VECTptr);
      else
	l.push_back(l0[i]);
    }
    s=int(l.size());
    if (s<=3){
#if 0
      if (abs_calc_mode(contextptr)==38)
	return _polygone(l,contextptr);
#endif
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
    gen_sort_f(ls.begin(),ls.end(),graham_sort_function);
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
#if 0
    if (abs_calc_mode(contextptr)==38)
      return _polygone(res,contextptr);
#endif
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
    int nr=int(m.size());
    int nc=int(m.front()._VECTptr->size());
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
	int counter=nr-1;
	for (int i=0;i<nc-1 && counter>0;++i){
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
	  if (cur_col>=0 && is_one(mt[i][cur_col])){ // BUGFIX by Luka Marohnić: a proper check for idn line
	    --counter;
	    bfs[i]=mt[nc-1][cur_col];
	  }
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
      int add=int(m.size());
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
    int n=int(x.size())-1;
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
      v=*ratnormal(taylor(v,x[i+1]-x[i]),contextptr)._VECTptr;
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
    pol=*ratnormal(subst(pol,inconnu,zf,false,contextptr),contextptr)._VECTptr;
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
      if (is_equal(tmp) && tmp._SYMBptr->feuille.type==_VECT && tmp._SYMBptr->feuille._VECTptr->size()==2){
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
	int s1=s-1;
	for (;s1>0;--s1){
	  if (v[s1].type!=_INT_)
	    break;
	}
	gen graph=funcplotfunc(gen(vecteur(v.begin(),v.begin()+s1+1),_SEQ__VECT),false,contextptr); // must be a graph of fcn
	if (is_undef(graph))
	  return graph;
	// extract polygon
	gen graphe=remove_at_pnt(graph);
	if (graphe.type==_VECT && graphe._VECTptr->size()==2)
	  graphe=symbolic(at_curve,makesequence(v.front(),graphe));
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
		attributs[1]=string2gen(_gaussquad(gen(makevecteur(v[0],v[1]),_SEQ__VECT),contextptr).print(contextptr),false);
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
      add_language(args.val,contextptr);
      return 1;
    }
    if (args.type==_STRNG){
      string s=*args._STRNGptr;
      s=s.substr(0,2);
      int i=string2lang(s);
      if (i){
	add_language(i,contextptr);
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
      remove_language(args.val,contextptr);
      return 1;
    }
    if (args.type==_STRNG){
      string s=*args._STRNGptr;
      s=s.substr(0,2);
      int i=string2lang(s);
      if (i){
	remove_language(i,contextptr);
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

  gen _set_language(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_INT_)
      return undef;
#if 0
    static int i=0;
    if (language(contextptr)==args.val){
      ++i;
      return string2gen("ans("+print_INT_(i)+")= ",false);
    }
#endif
    gen res=string2gen(set_language(args.val,contextptr),false);
    return res;
  }
  static const char _set_language_s []="set_language";
static define_unary_function_eval (__set_language,&_set_language,_set_language_s);
  define_unary_function_ptr5( at_set_language ,alias_at_set_language,&__set_language,0,true);

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
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
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
    if (!is_zero(g-vecteur(ms,1))){
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
	  s+=char('A'+i);
      }
      gen mii=m[i][i];
      if (mii.type==_DOUBLE_)
	mii=_round(makesequence(mii,3),contextptr);
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
	if (mij.type==_DOUBLE_)
	  mij=_round(makesequence(mij,3),contextptr);
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

  gen _flatten1(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT) return gensizeerr(contextptr);
    vecteur res;
    aplatir(*args._VECTptr,res,false);
    return res;
  }
  static const char _flatten1_s []="flatten1";
  static define_unary_function_eval (__flatten1,&_flatten1,_flatten1_s);
  define_unary_function_ptr5( at_flatten1 ,alias_at_flatten1,&__flatten1,0,true);

  bool has_undef_stringerr(const gen & g,std::string & err){
    if (g.type==_STRNG && g.subtype==-1){
      err=*g._STRNGptr;
      return true;
    }
    if (g.type==_VECT){
      unsigned s=unsigned(g._VECTptr->size());
      for (unsigned i=0;i<s;++i){
	if (has_undef_stringerr((*g._VECTptr)[i],err))
	  return true;
      }
      return false;
    }
    if (g.type==_POLY){
      unsigned s=unsigned(g._POLYptr->coord.size());
      for (unsigned i=0;i<s;++i){
	if (has_undef_stringerr(g._POLYptr->coord[i].value,err))
	  return true;
      }
      return false;
    }
    if (g.type==_SYMB)
      return has_undef_stringerr(g._SYMBptr->feuille,err);
    return false;
  }

  gen _caseval(const gen & args,GIAC_CONTEXT){
#ifdef TIMEOUT
    caseval_begin=time(0);
#endif
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_STRNG){
      gen g=protecteval(args,1,contextptr);
      string err;
      if (has_undef_stringerr(g,err)){
	err = "GIAC_ERROR: "+err;
	g=string2gen(err,false);
	g.subtype=-1;
      }
      return g;
    }
    if (*args._STRNGptr=="init geogebra")
      init_geogebra(1,contextptr);
    if (*args._STRNGptr=="close geogebra")
      init_geogebra(0,contextptr);
#ifdef TIMEOUT
    if (args._STRNGptr->size()>8 && args._STRNGptr->substr(0,8)=="timeout "){
      string t=args._STRNGptr->substr(8,args._STRNGptr->size()-8);
      double f=atof(t.c_str());
      if (f>=0 && f<24*60){
	caseval_maxtime=f;
	caseval_n=0;
	caseval_mod=10;
	return string2gen("Max eval time set to "+gen(f).print(),false);
      }
    }
    if (args._STRNGptr->size()>8 && args._STRNGptr->substr(0,8)=="ckevery "){
      string t=args._STRNGptr->substr(8,args._STRNGptr->size()-8);
      int f=atoi(t.c_str());
      if (f>0 && f<1e6){
	caseval_mod=f;
	return string2gen("Check every "+gen(f).print(),false);
      }
    }
#endif
    return string2gen(caseval(args._STRNGptr->c_str()),false);
  }
  static const char _caseval_s []="caseval";
  static define_unary_function_eval (__caseval,&_caseval,_caseval_s);
  define_unary_function_ptr5( at_caseval ,alias_at_caseval,&__caseval,_QUOTE_ARGUMENTS,true);

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

  gen conjugate_gradient(const matrice & A,const vecteur & b_orig,const vecteur & x0,double eps,int maxiter,GIAC_CONTEXT){
    int n=int(A.size());
    vecteur b=subvecteur(b_orig,multmatvecteur(A,x0));
    vecteur xk(x0);
    vecteur rk(b),pk(b);
    gen rk2=scalarproduct(rk,rk,contextptr);
    vecteur Apk(n),tmp(n);
    for (int k=1;k<=maxiter;++k){
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

  // Ax=b where A=D+B, Dx_{n+1}=b-B*x_n
  gen jacobi_linsolve(const matrice & A,const vecteur & b_orig,const vecteur & x0,double eps,int maxiter,GIAC_CONTEXT){
    int n=int(A.size());
    matrice B(A);
    vecteur D(n);
    vecteur b=*evalf_double(b_orig,1,contextptr)._VECTptr;
    for (int i=0;i<n;++i){
      vecteur Ai=*evalf(A[i],1,contextptr)._VECTptr;
      D[i]=Ai[i];
      Ai[i]=0;
      B[i]=Ai;
    }
    vecteur tmp(n),xn(x0),prev(n);
    gen bn=l2norm(b,contextptr);
    for (int i=0;i<maxiter;++i){
      prev=xn;
      multmatvecteur(B,xn,tmp);
      subvecteur(b,tmp,xn);
      iterateur jt=xn.begin(),jtend=xn.end(),dt=D.begin();
      for (;jt!=jtend;++jt){
	*jt=*jt / *dt;
      }
      gen g=l2norm(xn-prev,contextptr)/bn;
      if (is_greater(eps,g,contextptr))
	return xn;
    }
    *logptr(contextptr) << gettext("Warning! Leaving Jacobi iterative algorithm after maximal number of iterations. Check that your matrix is diagonal dominant.") << endl;
    return xn;    
  }
  
  // Ax=b where A=L+D+U, (D+L)x_{n+1}=b-U*x_n (Gauss-Seidel for omega==1)
  // or (L+D/omega)*x_{n+1}=b-(U+D*(1-1/omega))*x_n
  gen gauss_seidel_linsolve(const matrice & A,const vecteur & b_orig,const vecteur & x0,double omega,double eps,int maxiter,GIAC_CONTEXT){
    int n=int(A.size());
    double invomega=1/omega;
    matrice L(n),U(n);
    vecteur b=*evalf_double(b_orig,1,contextptr)._VECTptr;
    for (int i=0;i<n;++i){
      vecteur Ai=*evalf(A[i],1,contextptr)._VECTptr;
      L[i]=vecteur(Ai.begin(),Ai.begin()+i); 
      L[i]._VECTptr->reserve(n);
      L[i]._VECTptr->push_back(invomega*Ai[i]);
      for (int j=i+1;j<n;++j) L[i]._VECTptr->push_back(0.0);
      vecteur tmp(i+1,0.0);
      tmp[i]=(1-invomega)*Ai[i];
      U[i]=mergevecteur(tmp,vecteur(Ai.begin()+i+1,Ai.end()));
    }
    vecteur tmp(n),xn(x0),prev(n);
    gen bn=l2norm(b,contextptr);
    for (int i=0;i<maxiter;++i){
      prev=xn;
      multmatvecteur(U,xn,tmp);
      subvecteur(b,tmp,tmp);
      linsolve_l(L,tmp,xn);
      gen g=l2norm(xn-prev,contextptr)/bn;
      if (is_greater(eps,g,contextptr))
	return xn;
    }
    *logptr(contextptr) << gettext("Warning! Leaving Gauss-Seidel iterative algorithm after maximal number of iterations. Check that your matrix is diagonal dominant.") << endl;
    return xn;    
  }
  
  // params: matrix A, vector b, optional init value x0, optional precision eps
  gen iterative_solver(const gen & args,int method,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()<2)
      return gensizeerr(contextptr);
    vecteur v = *args._VECTptr;
    double omega=1.0;
    if (!v.empty() && v[0].type!=_VECT && v[0].type!=_MAP){
      gen v0=evalf_double(v[0],1,contextptr);
      if (v0.type!=_DOUBLE_)
	return gensizeerr("Bad omega value or bad first argument value");
      omega=v0._DOUBLE_val;
      if (omega<=0)
	omega=epsilon(contextptr);
      if (omega>=2)
	omega=2-epsilon(contextptr);
      v.erase(v.begin());
    }
    int s=int(v.size());
    gen A=v[0];
    gen b=v[1];
    bool creux=A.type==_MAP && b.type==_VECT;
    int n;
    if (creux)
      n=int(b._VECTptr->size());
    else {
      if (!is_squarematrix(A) || b.type!=_VECT)
	return gensizeerr(contextptr);
      n=int(A._VECTptr->size());
      if (n!=int(b._VECTptr->size()))
	return gensizeerr(contextptr);
    }
    vecteur x0(n);
    gen eps; gen niter(-1);
    if (s>=3){
      if (v[2].type==_VECT){
	if (int(v[2]._VECTptr->size())!=n)
	  return gensizeerr(contextptr);
	x0=*v[2]._VECTptr;
	if (s>3){
	  eps=v[3];
	  if (s>4)
	    niter=v[4];
	}
      }
      else {
	eps=v[2];
	if (s>3)
	  niter=v[3];
      }
    }
    if (is_greater(eps,1,contextptr))
      swapgen(eps,niter);
    if (niter==-1){
      switch (method){
      case 1: case 2:
	niter=SOLVER_MAX_ITERATE*n;
	break;
      case 4:
	niter=n;
	break;
      default:
	niter=n;
      }
    }
    eps=evalf_double(eps,1,contextptr);
    if (eps.type!=_DOUBLE_ || eps._DOUBLE_val < 0 || eps._DOUBLE_val>=1)
      return gentypeerr(contextptr);
    if (!is_integral(niter) || niter.val<1)
      return gentypeerr(contextptr);
    if (method==1)
      return creux?sparse_jacobi_linsolve(*A._MAPptr,*b._VECTptr,x0,eps._DOUBLE_val,niter.val,contextptr):jacobi_linsolve(*A._VECTptr,*b._VECTptr,x0,eps._DOUBLE_val,niter.val,contextptr);
    if (method==2)
      return creux?sparse_gauss_seidel_linsolve(*A._MAPptr,*b._VECTptr,x0,omega,eps._DOUBLE_val,niter.val,contextptr):gauss_seidel_linsolve(*A._VECTptr,*b._VECTptr,x0,omega,eps._DOUBLE_val,niter.val,contextptr);
    if (method==4)
      return creux?sparse_conjugate_gradient(*A._MAPptr,*b._VECTptr,x0,eps._DOUBLE_val,niter.val,contextptr):conjugate_gradient(*A._VECTptr,*b._VECTptr,x0,eps._DOUBLE_val,niter.val,contextptr);
    return gensizeerr(contextptr);
  }
  // params: matrix A, vector b, optional init value x0, optional precision eps
  gen _conjugate_gradient(const gen & args,GIAC_CONTEXT){
    return iterative_solver(args,4,contextptr);
  }
  static const char _conjugate_gradient_s []="conjugate_gradient";
  static define_unary_function_eval (__conjugate_gradient,&_conjugate_gradient,_conjugate_gradient_s);
  define_unary_function_ptr5( at_conjugate_gradient ,alias_at_conjugate_gradient,&__conjugate_gradient,0,true);

  gen _jacobi_linsolve(const gen & args,GIAC_CONTEXT){
    return iterative_solver(args,1,contextptr);
  }
  static const char _jacobi_linsolve_s []="jacobi_linsolve";
  static define_unary_function_eval (__jacobi_linsolve,&_jacobi_linsolve,_jacobi_linsolve_s);
  define_unary_function_ptr5( at_jacobi_linsolve ,alias_at_jacobi_linsolve,&__jacobi_linsolve,0,true);

  gen _gauss_seidel_linsolve(const gen & args,GIAC_CONTEXT){
    return iterative_solver(args,2,contextptr);
  }
  static const char _gauss_seidel_linsolve_s []="gauss_seidel_linsolve";
  static define_unary_function_eval (__gauss_seidel_linsolve,&_gauss_seidel_linsolve,_gauss_seidel_linsolve_s);
  define_unary_function_ptr5( at_gauss_seidel_linsolve ,alias_at_gauss_seidel_linsolve,&__gauss_seidel_linsolve,0,true);

  gen _subtype(const gen & args,GIAC_CONTEXT){
    if (args.type==_INT_ && args.subtype==0)
      return change_subtype(0,_INT_TYPE);
    if (args.type==_ZINT && args.subtype==0)
      return change_subtype(2,_INT_TYPE);
    if (args.type==_DOUBLE_)
      return change_subtype(1,_INT_TYPE);
    if (args.type==_REAL)
      return change_subtype(3,_INT_TYPE);
    return args.subtype;
  }
  static const char _subtype_s []="subtype";
  static define_unary_function_eval (__subtype,&_subtype,_subtype_s);
  define_unary_function_ptr5( at_subtype ,alias_at_subtype,&__subtype,0,true);

  // Graph utilities
  // convert matrice of probability to matrice of booleans
  // m[i][j]!=0 means there is a link from i to j
  bool proba2adjacence(const matrice & m,vector< vector<unsigned> >& v,bool check,GIAC_CONTEXT){
    if (!is_integer_matrice(m) && !is_zero(1-_plus(m.front(),contextptr),contextptr)){
      if (!check)
	return false;
      return proba2adjacence(mtran(m),v,false,contextptr);
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
    return true;
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

  void classify_scc(const vector< vector<unsigned> > & G,vector< vector<unsigned> > & SCC, vector< vector<unsigned> > & SCCrec,vector< vector<unsigned> > & SCCtrans){
    // Look at each SCC: if it has all outgoing edges going to the same component, 
    // then this is a recurrent positive, and we can compute the invariant probability
    if (SCC.empty())
      tarjan(G,SCC);
    for (unsigned i=0;i<SCC.size();++i){
      const vector<unsigned> & SCCi=SCC[i];
      vector<bool> in(G.size(),false);
      for (unsigned j=0;j<SCCi.size();++j){
	in[SCCi[j]]=true;
      }
      bool recurrent=true;
      for (unsigned j=0;recurrent && j<SCCi.size();++j){
	unsigned source=SCCi[j];
	const vector<unsigned> & targetv=G[source];
	for (unsigned k=0;recurrent && k<targetv.size();++k){
	  unsigned Gsk=targetv[k];
	  unsigned l=k*32;
	  for (;Gsk;++l,Gsk/=2){
	    if (Gsk %2 && !in[l]){
	      recurrent=false;
	      break;
	    }
	  }
	}
      }
      if (recurrent)
	SCCrec.push_back(SCCi);
      else
	SCCtrans.push_back(SCCi);
    } // end loop on strong connected components
  }

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
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!is_squarematrix(args))
      return gensizeerr(contextptr);
    vector< vector<unsigned> > G,GRAPH_SCC;
    if (!proba2adjacence(*args._VECTptr,G,true,contextptr))
      return gensizeerr(contextptr);
    tarjan(G,GRAPH_SCC);
    matrice m;
    matrix_unsigned2matrice(GRAPH_SCC,m);
    return m;
  }
  static const char _graph_scc_s []="graph_scc";
  static define_unary_function_eval (__graph_scc,&_graph_scc,_graph_scc_s);
  define_unary_function_ptr5( at_graph_scc ,alias_at_graph_scc,&__graph_scc,0,true);

  void extract_submatrix(const matrice & M,const vector<unsigned> & v,matrice & m){
    m.reserve(v.size());
    vecteur current(v.size());
    for (unsigned j=0;j<v.size();++j){
      vector<unsigned>::const_iterator it=v.begin(),itend=v.end();
      const_iterateur jt=M[v[j]]._VECTptr->begin();
      iterateur kt=current.begin();
      for (;it!=itend;++kt,++it)
	*kt=*(jt+*it);
      m.push_back(current);
    }
  }

  // check that g is a stochastic right or left matrix
  // if so set M to the matrix with sum of rows=1
  bool is_stochastic(const gen & g,matrice & M,GIAC_CONTEXT){
    if (!is_squarematrix(g))
      return false;
    gen gd=evalf_double(g,1,contextptr);
    if (!is_fully_numeric(gd))
      return false;
    M=*g._VECTptr;
    int ms=int(M.size());
    for (int i=0;i<ms;++i){
      const vecteur & v=*M[i]._VECTptr;
      for (int j=0;j<ms;++j){
	if (is_strictly_greater(0,v[j],contextptr))
	  return false;
      }
    }
    gen sg=_sum(_tran(g,contextptr),contextptr);
    if (!is_zero(sg-vecteur(ms,1),contextptr)){
      M=mtran(M);
      sg=_sum(g,contextptr);
      if (!is_zero(sg-vecteur(ms,1),contextptr))
	return false;
    }
    return true;
  }

  // returns
  // -> recurrent states: a list of at least one list: 
  //                      each sublist is a strongly connected component
  // -> invariant probability state (1-eigenstate) for each recurrent loop
  // -> transient states: a list of lists, each sublist is strongly connected
  // -> final probability: starting from each site, probability to end up
  //    in any of the invariant probability state
  gen _markov(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen g;
    double eps(epsilon(contextptr));
    if (args.type==_VECT && args.subtype==_SEQ__VECT && args._VECTptr->size()>=2){
      g=evalf_double(args._VECTptr->back(),1,contextptr);
      if (g.type!=_DOUBLE_)
	return gensizeerr(contextptr);
      eps=g._DOUBLE_val;
      g=args._VECTptr->front();
    }
    else
      g=args;
    matrice M;
    if (!is_stochastic(g,M,contextptr))
      return gensizeerr("Not a stochastic matrix!");
    int ms=int(M.size());
    vector< vector<unsigned> > G,GRAPH_SCC,SCCrec,SCCtrans;
    proba2adjacence(M,G,true,contextptr);
    classify_scc(G,GRAPH_SCC,SCCrec,SCCtrans);
    matrice mrec,mtrans,meigen;
    matrix_unsigned2matrice(SCCrec,mrec);
    matrix_unsigned2matrice(SCCtrans,mtrans);
    // Find eigenstate 1 for each component of SCCrec
    for (unsigned i=0;i<SCCrec.size();++i){
      vector<unsigned> v=SCCrec[i];
      // extract corresponding submatrix from M
      matrice m;
      sort(v.begin(),v.end());
      if (v.size()==M.size())
	m=M;
      else 
	extract_submatrix(M,v,m);
      m=mtran(m); // find standard linear algebra 1-eigenvector
      vecteur w,z;
      if (is_exact(m)){
	vecteur k;
	mker(subvecteur(m,midn(int(m.size()))),k,contextptr);
	//k=negvecteur(k);
	if (k.size()==1 && k.front().type==_VECT){
	  // if dim Ker(m-idn)>1 should find a vector with all coordinate >0
	  z=divvecteur(*k.front()._VECTptr,prodsum(k.front(),false));
	}
      }
      if (z.empty()){
	w=vecteur(m.size(),evalf(1,1,contextptr)/int(m.size())); // initial guess
	for (;;){
	  multmatvecteur(m,w,z);
	  if (is_greater(eps,l1norm(w-z,contextptr),contextptr))
	    break;
	  swap(w,z);
	}
      }
      if (v.size()==M.size())
	meigen.push_back(z);
      else {
	w.clear();
	unsigned pos=0;
	for (unsigned j=0;j<v.size();++j){
	  for (;pos<v[j];++pos)
	    w.push_back(0);
	  w.push_back(z[j]);
	  ++pos;
	}
	for (;pos<M.size();++pos)
	  w.push_back(0);
	meigen.push_back(w);
      }
    }
    int nrec=int(meigen.size());
    if (nrec==1)
      return makesequence(mrec,meigen,mtrans,vecteur(ms,vecteur(1,1)));
    // For each initial pure state, find probability to end in 
    // the recurrents states from meigen
    M=mtran(M); // linear algebra iteration v->M*v
    matrice mfinal; // will have nrec columns
    for (unsigned i=0;int(i)<ms;++i){
      vecteur line;
      line.reserve(nrec);
      // start at state i
      // speedup: first look if i is in a recurrent strong component 
      // if so the final state is the recurrent strong component eigenstate
      for (unsigned j=0;j<SCCrec.size();++j){
	if (equalposcomp(SCCrec[j],i)){
	  line=vecteur(nrec,0);
	  line[j]=1;
	  break;
	}
      }
      if (!line.empty()){
	mfinal.push_back(line);
	continue;
      }
      // otherwise iterate starting from 1 at position i
      vecteur w(ms),z(ms);
      w[i]=1;
      for (;;){
	multmatvecteur(M,w,z);
	if (is_greater(eps,l1norm(w-z,contextptr),contextptr))
	  break;
	swap(w,z);
      }
      // find z as a linear combination of the vectors of meigen
      for (unsigned j=0;j<meigen.size();++j){
	const vecteur & cur=*meigen[j]._VECTptr;
	// find the largest component of mcur
	int pos=0;
	gen maxcur=0;
	for (unsigned k=0;k<cur.size();++k){
	  if (is_strictly_greater(cur[k],maxcur,contextptr)){
	    maxcur=cur[k];
	    pos=k;
	  }
	}
	// find coefficient
	line.push_back(z[pos]/cur[pos]);
      }
      mfinal.push_back(line);
    }
    return makesequence(mrec,meigen,mtrans,mfinal);
  }
  static const char _markov_s []="markov";
  static define_unary_function_eval (__markov,&_markov,_markov_s);
  define_unary_function_ptr5( at_markov ,alias_at_markov,&__markov,0,true);

  // random iterations for a Markov chain of transition matrix M, initial state i,
  // number of iterations n
  // randmarkov(M,i,n) returns the list of n+1 states starting at i
  // randmarkov(M,[i1,..,ip],b) returns the matrix of p rows, each row is
  //   the list of n+1 states starting at ip
  // randmarkov([n1,..,np],nt) make a random Markov transition matrix
  // with p recurrent loops of size n1,...,np and nt transient states
  gen _randmarkov(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur v = *args._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int vs=read_attributs(v,attributs,contextptr);
    if (vs<2 || vs>4)
      return gensizeerr(contextptr);
    bool plot=int(args._VECTptr->size())>vs;
    bool polygon=true;
    if (vs==4) {
      if (v[3]==at_plot || v[3]==at_polygonplot || v[3]==at_scatterplot){
	if (v[3]==at_scatterplot)
	  polygon=true;
	plot=true;
      }
      else
	return gensizeerr(contextptr);
    }
    if (vs==2){
      is_integral(v[1]); 
      if (v[1].type!=_INT_ || v[1].val<0)
	return gensizeerr(contextptr);
      vecteur w=gen2vecteur(v[0]);
      if (!is_integer_vecteur(w))
	return gensizeerr(contextptr);
      unsigned ws=unsigned(w.size()),n=0;
      vector<unsigned> W(ws),Wc(ws+1);
      for (unsigned i=0;i<ws;++i){
	if (w[i].type!=_INT_ || w[i].val<=0)
	  return gendimerr(contextptr);
	n += (W[i]=w[i].val);
	Wc[i+1]=Wc[i]+W[i];
      }
      int nt=v[1].val,nnt=n+nt;
      if (nnt*nnt>LIST_SIZE_LIMIT)
	return gendimerr(contextptr);
      matrice res(nnt);
      int pos=0; // position in W
      // first lines (recurrent states)
      int cur=Wc[0],next=Wc[1];
      for (int i=0;i<int(n);++i){
	if (i>=next){
	  ++pos;
	  cur=next;
	  next=Wc[pos+1];
	}
	vecteur line(nnt);
	// create Wc[pos] zeros
	// then Wc[pos+1]-Wc[pos] probabilities
	for (int j=cur;j<next;++j){
	  line[j]=giac_rand(contextptr)/(rand_max2+1.0);
	}
	res[i]=divvecteur(line,prodsum(line,false));
      }
      // transient states
      for (int i=n;i<nnt;++i){
	vecteur line(nnt);
	for (int j=0;j<nnt;++j){
	  line[j]=giac_rand(contextptr)/(rand_max2+1.0);
	}
	res[i]=divvecteur(line,prodsum(line,false));
      }
      return res;
    }
    vecteur v1=gen2vecteur(v[1]);
    if (!is_integer_vecteur(v1))
      return gensizeerr();
    is_integral(v[2]);
    if (v[2].type!=_INT_ || v[2].val<0)
      return gensizeerr(contextptr);
    int n=v[2].val;
    gen g=v[0];
    matrice M;
    if (!is_stochastic(g,M,contextptr))
      return gensizeerr("Not a stochastic matrix!");
    int shift=0;
    if (xcas_mode(contextptr) || abs_calc_mode(contextptr)==38)
      shift=1;
    vector<unsigned> start(v1.size());
    for (unsigned i=0;i<v1.size();++i){
      int pos=v1[i].val-shift;
      if (pos<0 || pos>=int(M.size()))
	return gendimerr(contextptr);
      start[i]=pos;
    }
    // find cumulated frequencies for each row
    matrix_double Mcumul(int(M.size()));
    for (unsigned I=0;I<Mcumul.size();++I){
      const vecteur & v=*M[I]._VECTptr;
      vector<giac_double> vcumul(v.size()+1);
      vcumul[0]=0;
      for (unsigned j=1;j<=v.size();++j){
	vcumul[j] = vcumul[j-1]+evalf_double(v[j-1],1,contextptr)._DOUBLE_val;
      }
      Mcumul[I]=vcumul;
    }
    // iterate
    matrice res;
    vecteur line1;
    for (int j=0;j<=int(n);++j){
      line1.push_back(j);
    }
    for (unsigned pos=0;pos<start.size();++pos){
      int i=start[pos];
      vecteur line(1,i);
      for (int j=0;j<n;++j){
	double d=giac_rand(contextptr)/(rand_max2+1.0);
	if (i>int(Mcumul.size()))
	  return gendimerr(contextptr);	
	int pos=dichotomy(Mcumul[i],d);
	if (pos==-1)
	  return gendimerr(contextptr);
	i=pos;
	line.push_back(i+shift);
      }
      res.push_back(line);
    }
    if (v[1].type==_INT_){
      if (plot){
	gen tmp=makesequence(line1,res.front());
	if (polygon)
	  return _polygonscatterplot(tmp,contextptr);
	else
	  return _scatterplot(tmp,contextptr);
      }
      return res.front();
    }
    if (plot){
      res.insert(res.begin(),line1);
      if (polygon)
	return _polygonplot(_tran(res,contextptr),contextptr);
      else
	return _scatterplot(_tran(res,contextptr),contextptr);
    }
    return res;
  }
  static const char _randmarkov_s []="randmarkov";
  static define_unary_function_eval (__randmarkov,&_randmarkov,_randmarkov_s);
  define_unary_function_ptr5( at_randmarkov ,alias_at_randmarkov,&__randmarkov,0,true);

  gen _is_polynomial(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v;
    if (args.type==_VECT && args.subtype!=_SEQ__VECT)
      v=vecteur(1,args);
    else
      v=gen2vecteur(args);
    if (v.empty())
      return gensizeerr(contextptr);
    if (v.size()==1)
      v.push_back(ggb_var(args));
    gen tmp=apply(v,equal2diff);
    vecteur lv=lvarxwithinv(tmp,v[1],contextptr);
    gen res=lv.size()<2?1:0;
    res.subtype=_INT_BOOLEAN;
    return res;
  }
  static const char _is_polynomial_s []="is_polynomial";
  static define_unary_function_eval (__is_polynomial,&_is_polynomial,_is_polynomial_s);
  define_unary_function_ptr5( at_is_polynomial ,alias_at_is_polynomial,&__is_polynomial,0,true);

  // find positions of object in list or first position of substring in string
  gen _find(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v = gen2vecteur(args);
    if (v.size()!=2)
      return gensizeerr(contextptr);
    const gen a=v.front();
    int shift=xcas_mode(contextptr)>0 || abs_calc_mode(contextptr)==38;
    if (a.type==_STRNG){
      if (v.back().type!=_STRNG)
	return gensizeerr(contextptr);
      const string s=*v.back()._STRNGptr;
      vecteur res;
      int pos=0;
      for (;;++pos){
	pos=int(s.find(*a._STRNGptr,pos));
	if (pos<0 || pos>=int(s.size()))
	  break;
	res.push_back(pos+shift);
      }
      return res;
    }
    if (v.back().type!=_VECT)
      return gensizeerr(contextptr);
    const vecteur & w =*v.back()._VECTptr;
    int s=int(w.size());
    vecteur res;
    for (int i=0;i<s;++i){
      if (a==w[i])
	res.push_back(i+shift);
    }
    return res;
  }
  static const char _find_s []="find";
  static define_unary_function_eval (__find,&_find,_find_s);
  define_unary_function_ptr5( at_find ,alias_at_find,&__find,0,true);

  gen _dayofweek(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->size()!=3)
      return gensizeerr(contextptr);
    vecteur & v = *args._VECTptr;
    gen d=v[0],m=v[1],a=v[2];
    if (!is_integral(d) && !is_integral(m) && !is_integral(a))
      return gensizeerr(contextptr);
    int D=d.val,M=m.val,A=a.val;
    if (D<1 || D>31 || M<1 || M>12)
      return gensizeerr(contextptr);
    int x=A;
    if (M<3) x--;
    int y=(23*M)/9+D+4+A+x/4-x/100+x/400;
    if (M<3) y=y%7; else y=(y-2)%7;
    return y;
  }
  static const char _dayofweek_s []="dayofweek";
  static define_unary_function_eval (__dayofweek,&_dayofweek,_dayofweek_s);
  define_unary_function_ptr5( at_dayofweek ,alias_at_dayofweek,&__dayofweek,0,true);

  gen _evalfa(const gen & args,GIAC_CONTEXT){
    vecteur v(lop(args,at_rootof));
    gen w=evalf(v,1,contextptr);
    return subst(args,v,w,false,contextptr);
  }
  static const char _evalfa_s []="evalfa";
  static define_unary_function_eval (__evalfa,&_evalfa,_evalfa_s);
  define_unary_function_ptr5( at_evalfa ,alias_at_evalfa,&__evalfa,0,true);

  gen _linspace(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->size()<2) return gensizeerr(contextptr);
    int n=100;
    vecteur v = *args._VECTptr;
    gen start=v[0],stop=v[1];
    if (v.size()>2){
      gen N=v[2];
      if (!is_integral(N) || N.val<2)
	return gendimerr(contextptr);
      n=N.val;
    }
    gen step=(stop-start)/(n-1);
    vecteur w(n);
    for (int i=0;i<n;++i){
      w[i]=start+i*step;
    }
    return w;
  }
  static const char _linspace_s []="linspace";
  static define_unary_function_eval (__linspace,&_linspace,_linspace_s);
  define_unary_function_ptr5( at_linspace ,alias_at_linspace,&__linspace,0,true);

  gen _Li(const gen & args,GIAC_CONTEXT){
    return _Ei(ln(args,contextptr),contextptr);
  }
  static const char _Li_s []="Li";
  static define_unary_function_eval (__Li,&_Li,_Li_s);
  define_unary_function_ptr5( at_Li ,alias_at_Li,&__Li,0,true);

  gen _coth(const gen & args,GIAC_CONTEXT){
    return inv(tanh(args,contextptr),contextptr);
  }
  static const char _coth_s []="coth";
  static define_unary_function_eval (__coth,&_coth,_coth_s);
  define_unary_function_ptr5( at_coth ,alias_at_coth,&__coth,0,true);

  gen _atan2(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    if (//&& args.subtype==_SEQ__VECT 
	args._VECTptr->size()==2)
      return arg(args._VECTptr->back()+cst_i*args._VECTptr->front(),contextptr);
    return gensizeerr(contextptr); //apply(args,_atan2,contextptr);
  }
  static const char _atan2_s []="atan2";
  static define_unary_function_eval (__atan2,&_atan2,_atan2_s);
  define_unary_function_ptr5( at_atan2 ,alias_at_atan2,&__atan2,0,true);

  gen _acoth(const gen & args,GIAC_CONTEXT){
    return atanh(inv(args,contextptr),contextptr);
  }
  static const char _acoth_s []="acoth";
  static define_unary_function_eval (__acoth,&_acoth,_acoth_s);
  define_unary_function_ptr5( at_acoth ,alias_at_acoth,&__acoth,0,true);

  gen _add_autosimplify(const gen & args,GIAC_CONTEXT){
    return eval(add_autosimplify(args,contextptr),eval_level(contextptr),contextptr);
  }
  static const char _add_autosimplify_s []="add_autosimplify";
  static define_unary_function_eval (__add_autosimplify,&_add_autosimplify,_add_autosimplify_s);
  define_unary_function_ptr5( at_add_autosimplify ,alias_at_add_autosimplify,&__add_autosimplify,_QUOTE_ARGUMENTS,true);


#if 0
  // Small graphs, not tested
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

  // step by step utilities

  bool is_periodic(const gen & f,const gen & x,gen & periode,GIAC_CONTEXT){
    periode=0;
    vecteur vx=lvarx(f,x);
    for (unsigned i=0;i<vx.size();++i){
      if (vx[i].type!=_SYMB || (vx[i]._SYMBptr->sommet!=at_exp && vx[i]._SYMBptr->sommet!=at_sin && vx[i]._SYMBptr->sommet!=at_cos && vx[i]._SYMBptr->sommet!=at_tan))
	return false;
    }
    gen g=_lin(trig2exp(f,contextptr),contextptr);
    vecteur v;
    rlvarx(g,x,v);
    islesscomplexthanf_sort(v.begin(),v.end());
    int i,s=int(v.size());
    if (s<2)
      return false;
    gen a,b,v0,alpha,beta,alphacur,betacur,gof,periodecur;
    for (i=1;i<s;++i){
      if (!v[i].is_symb_of_sommet(at_exp))
	return false;
      v0=v[i];
      gen v0arg=v0._SYMBptr->feuille;
      if (is_linear_wrt(v0arg,x,alphacur,betacur,contextptr)){ 
	periodecur=normal(alphacur/cst_i,contextptr);
	if (!is_zero(im(periodecur,contextptr)))
	  return false;
	periode=gcd(periode,periodecur,contextptr);
      }
      else
	return false;
    }
    periode=ratnormal(cst_two_pi/periode);
    return !is_zero(periode);
  }

  bool in_domain(const gen & df,const gen &x,const gen & x0,GIAC_CONTEXT){
    if (df==x)
      return true;
    if (df.type==_VECT){
      const vecteur v=*df._VECTptr;
      for (int i=0;i<int(v.size());++i){
	if (in_domain(v[i],x,x0,contextptr))
	  return true;
      }
      return false;
    }
    gen g=eval(subst(df,x,x0,false,contextptr),1,contextptr);
    return is_one(g);
  }

  // convert series expansion f at x=x0 to polynomial Taylor expansion
  // a is set to the predominant non constant monomial coefficient
  // (i.e. start from end first non 0)
  bool convert_polynom(const gen & f,const gen & x,const gen & x0,vecteur & v,gen & a,int &order,GIAC_CONTEXT){
    v.clear();
    vecteur l(lop(f,at_order_size));
    vecteur lp(l.size(),zero);
    gen g=subst(f,l,lp,false,contextptr);
    l=vecteur(1,x);
    lp=vecteur(1,x+x0);
    g=subst(g,l,lp,false,contextptr);
    lvar(g,l);
    gen temp=e2r(g,l,contextptr);
    if (is_zero(temp))
      return true;
    l.erase(l.begin());
    gen res;
    gen tmp2(polynome2poly1(temp,1));
    res=l.empty()?tmp2:((tmp2.type==_FRAC && tmp2._FRACptr->den.type==_VECT && tmp2._FRACptr->den._VECTptr->size()>1)?gen(fraction(r2e(tmp2._FRACptr->num,l,contextptr),r2e(tmp2._FRACptr->den,l,contextptr))):r2e(tmp2,l,contextptr));
    if (res.type==_FRAC && res._FRACptr->num.type==_VECT && res._FRACptr->den.type<_POLY){
      res=inv(res._FRACptr->den,contextptr)*res._FRACptr->num;
    }
    if (res.type!=_VECT)
      return false;
    v=*res._VECTptr;
    order=0;
    for (int i=int(v.size())-2;i>=0;--i){
      if (v[i]!=0){
	a=v[i];
	order=int(v.size())-i-1;
	break;
      }
    }
    return true;
  }

  static gen write_legende(const gen & g,bool exactlegende,GIAC_CONTEXT){
    if (exactlegende)
      return symb_equal(at_legende,g);
    int digits=decimal_digits(contextptr);
    decimal_digits(3,contextptr);
    gen res=evalf(g,1,contextptr);
    res=string2gen(res.print(contextptr),false);
    res=symb_equal(at_legende,res);
    decimal_digits(digits,contextptr);
    return res;
  }
  
  vecteur endpoints(const gen & g){
    vecteur res;
    if (g.type==_VECT){
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it)
	res=mergevecteur(res,endpoints(*it));
      return res;
    }
    if (g.type!=_SYMB)
      return res;
    if (g._SYMBptr->sommet==at_and || g._SYMBptr->sommet==at_ou)
      return endpoints(g._SYMBptr->feuille);
    if (is_inequation(g) || g._SYMBptr->sommet==at_different || g._SYMBptr->sommet==at_equal)
      return vecteur(1,g._SYMBptr->feuille[1]);
    return res;
  }

  int step_param_(const gen & f,const gen & g,const gen & t,gen & tmin,gen&tmax,vecteur & poi,vecteur & tvi,bool printtvi,bool exactlegende,GIAC_CONTEXT){
    if (t.type!=_IDNT)
      return 0;
    gprintf("====================\nParametric plot (%gen,%gen), variable %gen",makevecteur(f,g,t),1,contextptr);
    gen periodef,periodeg,periode;
    if (is_periodic(f,t,periodef,contextptr) && is_periodic(g,t,periodeg,contextptr)){
      periode=gcd(periodef,periodeg,contextptr);
      if (is_greater(tmax-tmin,periode,contextptr)){
	tmin=normal(-periode/2,contextptr);
	tmax=normal(periode/2,contextptr);
      }
    }
    int eof=0,eog=0;
    if (tmin==-tmax && (eof=is_even_odd(f,t,contextptr)) && (eog=is_even_odd(g,t,contextptr))){
      if (eof==1){
	if (eog==1)
	  gprintf("Even functions.",vecteur(0),1,contextptr);
	else
	  gprintf("Even function %gen, odd function %gen. Reflection Ox",makevecteur(f,g),1,contextptr);
      }
      else {
	if (eog==1)
	  gprintf("Odd function %gen, even function %gen. Reflection Oy",makevecteur(f,g),1,contextptr);
	else
	  gprintf("Odd functions. Center O",vecteur(0),1,contextptr);
      }
      tmin=0;
    }
    gen tmin0=ratnormal(tmin,contextptr),tmax0=ratnormal(tmax,contextptr);
    vecteur lv=lidnt(evalf(f,1,contextptr));
    if (lv.empty())
      return 1;
    if (lv.size()!=1 || lv.front()!=t)
      return 0;
    gen fg=symbolic(at_nop,makesequence(f,g));
    gen df=domain(fg,t,0,contextptr);
    gprintf("Domain %gen",vecteur(1,df),1,contextptr);
    gen df1=domain(fg,t,1,contextptr); // singular values only
    if (df1.type!=_VECT){
      gensizeerr("Unable to find singular points");
      return 0;
    }
    // Singularities
    vecteur sing,crit;
    identificateur xid=*t._IDNTptr;
    iterateur it=df1._VECTptr->begin(),itend=df1._VECTptr->end();
    for (;it!=itend;++it){
      if (is_greater(*it,tmin,contextptr) && is_greater(tmax,*it,contextptr)){
	sing.push_back(*it);
      }
    }
    // Extremas
    int st=step_infolevel(contextptr); 
    step_infolevel(0,contextptr);
    gen f1=_factor(derive(f,t,contextptr),contextptr),g1=_factor(derive(g,t,contextptr),contextptr);
    gen f2=derive(f1,t,contextptr),g2=derive(g1,t,contextptr);
    gen conv=f1*g2-f2*g1;
    gen tval=eval(t,1,contextptr);
    giac_assume(symb_and(symb_superieur_egal(t,tmin),symb_inferieur_egal(t,tmax)),contextptr);
    int cm=calc_mode(contextptr);
    calc_mode(-38,contextptr); // avoid rootof
    gen cx=recursive_normal(solve(f1,t,periode==0?2:0,contextptr),contextptr);
    gen cy=recursive_normal(solve(g1,t,periode==0?2:0,contextptr),contextptr);
    gen cc=recursive_normal(solve(conv,t,periode==0?2:0,contextptr),contextptr);
    calc_mode(cm,contextptr); // avoid rootof
    if (t!=tval)
      sto(tval,t,contextptr);
    step_infolevel(st,contextptr);
    if (cx.type!=_VECT || cy.type!=_VECT){
      *logptr(contextptr) << "Unable to find critical points" << endl;
      purgenoassume(t,contextptr);
      return 0;
    }
    vecteur c=mergevecteur(*cx._VECTptr,*cy._VECTptr),infl;
    if (cc.type==_VECT){
      infl=*cc._VECTptr;
      c=mergevecteur(c,infl);
    }
    else
      *logptr(contextptr) << "Unable to find inflection points" << endl;
    for (int i=0;i<int(infl.size());++i)
      infl[i]=ratnormal(infl[i],contextptr);
    for (int i=0;i<int(c.size());++i)
      c[i]=ratnormal(c[i],contextptr);
    comprim(c);
    if (!lidnt(evalf(c,1,contextptr)).empty()){
      *logptr(contextptr) << "Infinite number of critical points. Try with optional argument " << t << "=tmin..tmax" << endl;
      purgenoassume(t,contextptr);
      return 0;
    }
    it=c.begin();itend=c.end();
    for (;it!=itend;++it){
      if (!lop(*it,at_rootof).empty())
	*it=re(evalf(*it,1,contextptr),contextptr);
      *it=recursive_normal(*it,contextptr);
      if (in_domain(df,t,*it,contextptr) && is_greater(*it,tmin,contextptr) && is_greater(tmax,*it,contextptr)){
	crit.push_back(*it);
	gen fx=limit(f,xid,*it,0,contextptr);
	fx=recursive_normal(fx,contextptr);
	gen gx=limit(g,xid,*it,0,contextptr);
	gx=recursive_normal(gx,contextptr);
	gen ax,ay;
	bool singp=equalposcomp(*cx._VECTptr,*it) && equalposcomp(*cy._VECTptr,*it);
	if (singp){
	  // singular point, find tangent (and kind?)
	  /* ax=limit(f2,xid,*it,0,contextptr);
	  ax=recursive_normal(ax,contextptr);
	  ay=limit(g2,xid,*it,0,contextptr);
	  ay=recursive_normal(ay,contextptr); */
	  int ordre=5;
	  vecteur vx,vy;
	  int ox=0,oy=0,o1=0,o2=0;
	  while (ordre<=20 && o1==0){
	    // series expansion
	    if (!convert_polynom(series(f,xid,*it,ordre,contextptr),xid,*it,vx,ax,ox,contextptr))
	      break;
	    if (!convert_polynom(series(g,xid,*it,ordre,contextptr),xid,*it,vy,ay,oy,contextptr))
	      break;
	    o1=ox;
	    if (ox<oy)
	      ay=0;
	    if (oy<ox){
	      ax=0;
	      o1=oy;
	    }
	    if (o1){
	      // find cusp kind / type de rebroussement
	      reverse(vx.begin(),vx.end());
	      reverse(vy.begin(),vy.end());
	      while (vx.size()<vy.size())
		vx.push_back(0);
	      while (vy.size()<vx.size())
		vy.push_back(0);
	      o2=o1+1;
	      int vs=int(vx.size());
	      for (;o2<vs;++o2){
		gen determinant=simplify(vx[o1]*vy[o2]-vx[o2]*vy[o1],contextptr);
		if (!is_zero(determinant))
		  break;
	      }
	      if (o2==vs)
		o1=0;
	    }
	    ordre *= 2;
	  }
	  gprintf("Singular point %gen, point %gen direction %gen kind (%gen,%gen)\nTaylor expansions %gen",makevecteur(symb_equal(t__IDNT_e,*it),makevecteur(fx,gx),makevecteur(ax,ay),o1,o2,makevecteur(vx,vy)),1,contextptr);
	  gprintf(" \n",vecteur(0),1,contextptr);
	}
	else {
	  ax=limit(f1,xid,*it,0,contextptr);
	  ay=limit(g1,xid,*it,0,contextptr);
	  ax=recursive_normal(ax,contextptr);
	  ay=recursive_normal(ay,contextptr);
	}
	gen n=sqrt(ax*evalf(ax,1,contextptr)+ay*ay,contextptr);
	if (!is_undef(fx) && !is_inf(fx) && !is_undef(gx) && !is_inf(gx)){
	  gen pnt=_point(makesequence(fx,gx,write_legende(makevecteur(fx,gx),exactlegende,contextptr),symb_equal(at_couleur,equalposcomp(infl,*it)?_RED:_MAGENTA)),contextptr);
	  poi.push_back(pnt);	
	  if (singp){
	    vecteur ve=makevecteur(_point(makesequence(fx,gx),contextptr),makevecteur(ax/n,ay/n),symb_equal(at_couleur,_BLUE));
	    ve.push_back(write_legende(makevecteur(ax,ay),exactlegende,contextptr));
	    gen vv=_vector(gen(ve,_SEQ__VECT),contextptr);
	    poi.push_back(vv);
	  }
	}	
      }
    }
    if (tmin==minus_inf && !equalposcomp(sing,minus_inf)){
      if (in_domain(df,t,tmin,contextptr))
	sing.push_back(tmin);
      tmin=plus_inf;
    }
    if (tmax==plus_inf && !equalposcomp(sing,plus_inf)){
      if (in_domain(df,t,tmax,contextptr))
	sing.push_back(tmax);
      tmax=minus_inf;
    }
    it=crit.begin();itend=crit.end();
    for (;it!=itend;++it){
      if (!is_inf(*it)){ 
	if (is_greater(tmin,*it,contextptr))
	  tmin=*it;
	if (is_greater(*it,tmax,contextptr))
	  tmax=*it;
      }
    }
    it=infl.begin();itend=infl.end();
    for (;it!=itend;++it){
      if (!is_inf(*it)){ 
	if (is_greater(tmin,*it,contextptr))
	  tmin=*it;
	if (is_greater(*it,tmax,contextptr))
	  tmax=*it;
      }
    }
    // asymptotes
    gen xmin(plus_inf),xmax(minus_inf),ymin(plus_inf),ymax(minus_inf);
    it=sing.begin();itend=sing.end();
    for (;it!=itend;++it){
      if (!is_inf(*it)){
	if (is_greater(tmin,*it,contextptr))
	  tmin=*it;
	if (is_greater(*it,tmax,contextptr))
	  tmax=*it;	
      }
      gen fx=limit(f,xid,*it,0,contextptr);
      fx=recursive_normal(fx,contextptr);
      if (!is_inf(fx) && !lidnt(evalf(fx,1,contextptr)).empty()) continue;
      gen fy=limit(g,xid,*it,0,contextptr);
      fy=recursive_normal(fy,contextptr);
      if (!is_inf(fy) && !lidnt(evalf(fy,1,contextptr)).empty()) continue;
      if (is_inf(fx)){
	if (!is_inf(fy)){
	  gen equ=symb_equal(y__IDNT_e,fy);
	  if (is_greater(ymin,fy,contextptr))
	    ymin=fy;
	  if (is_greater(fy,ymax,contextptr))
	    ymax=fy;
	  gprintf("Horizontal asymptote at %gen : %gen",makevecteur(*it,equ),1,contextptr);
	  gen dr=_droite(makesequence(equ,write_legende(equ,exactlegende,contextptr),symb_equal(at_couleur,_RED)),contextptr);
	  if (!equalposcomp(poi,dr))
	    poi.push_back(dr);
	  continue;
	}
	gen a=limit(g/f,xid,*it,0,contextptr);
	a=recursive_normal(a,contextptr);
	if (is_undef(a)) continue;
	if (is_inf(a)){
	  gprintf("Vertical parabolic asymptote at %gen",vecteur(1,*it),1,contextptr);
	  continue;
	}
	else
	  if (!lidnt(evalf(a,1,contextptr)).empty()) continue;
	if (is_zero(a)){
	  gprintf("Horizontal parabolic asymptote at %gen",vecteur(1,*it),1,contextptr);
	  continue;
	}
	gen b=limit(g-a*f,xid,*it,0,contextptr);
	b=recursive_normal(b,contextptr);
	if (is_undef(b)) continue;
	if (is_inf(b)){
	  gprintf("Parabolic asymptote direction at %gen: %gen",makevecteur(*it,symb_equal(y__IDNT_e,a*x__IDNT_e)),1,contextptr);
	  continue;
	}
	else
	  if (!lidnt(evalf(b,1,contextptr)).empty()) continue;
	gen equ=symb_equal(y__IDNT_e,a*x__IDNT_e+b);
	gprintf("Asymptote at %gen: %gen",makevecteur(*it,equ),1,contextptr);
	gen dr=_droite(makesequence(equ,write_legende(equ,exactlegende,contextptr),symb_equal(at_couleur,_RED)),contextptr);
	if (!equalposcomp(poi,dr))
	  poi.push_back(dr);
	continue;
      }
      if (is_inf(fy)){
	gen equ=symb_equal(x__IDNT_e,fx);
	if (is_greater(xmin,fx,contextptr))
	  xmin=fx;
	if (is_greater(fx,xmax,contextptr))
	  xmax=fx;
	gprintf("Vertical asymptote at %gen: %gen",makevecteur(*it,equ),1,contextptr);
	gen dr=_droite(makesequence(equ,write_legende(equ,exactlegende,contextptr),symb_equal(at_couleur,_RED)),contextptr);
	if (!equalposcomp(poi,dr))
	  poi.push_back(dr);
	continue;
      }
    }
    for (int i=0;i<int(sing.size());++i)
      sing[i]=ratnormal(sing[i],contextptr);
    for (int i=0;i<int(crit.size());++i)
      crit[i]=ratnormal(crit[i],contextptr);
    vecteur tvx=mergevecteur(sing,crit);
    if (in_domain(df,t,tmin0,contextptr))
      tvx.insert(tvx.begin(),tmin0);
    if (in_domain(df,t,tmax0,contextptr))
      tvx.push_back(tmax0);
    // add endpoints of df
    vecteur ep=endpoints(df);
    for (size_t i=0;i<ep.size();++i){
      if (is_greater(ep[i],tmin0,contextptr) && is_greater(tmax0,ep[i],contextptr) && in_domain(df,t,ep[i],contextptr))
	tvx.push_back(ep[i]);
    }
    comprim(tvx);
    gen tmp=_sort(tvx,contextptr);
    if (tmp.type!=_VECT){
      purgenoassume(t,contextptr);
      return 0;
    }
    tvx=*tmp._VECTptr;
    int pos=equalposcomp(tvx,minus_inf);
    if (pos){
      tvx.erase(tvx.begin()+pos-1);
      tvx.insert(tvx.begin(),minus_inf);
    }
    pos=equalposcomp(tvx,plus_inf);
    if (pos){
      tvx.erase(tvx.begin()+pos-1);
      tvx.push_back(plus_inf);
    }
    gen nextt=tvx.front();
    vecteur tvit=makevecteur(t,nextt);
    gen x=limit(f,xid,nextt,1,contextptr);
    if (!is_inf(x) && is_greater(xmin,x,contextptr))
      xmin=x;
    if (!is_inf(x) && is_greater(x,xmax,contextptr))
      xmax=x;
    gen y=limit(g,xid,nextt,1,contextptr);
    if (!is_inf(y) && is_greater(ymin,y,contextptr))
      ymin=y;
    if (!is_inf(y) && is_greater(y,ymax,contextptr))
      ymax=y;
    vecteur tvif=makevecteur(symb_equal(x__IDNT_e,f),x);
    vecteur tvig=makevecteur(symb_equal(y__IDNT_e,g),y);
    gen nothing=string2gen(" ",false);
    vecteur tvidf=makevecteur(symb_equal(symbolic(at_derive,x__IDNT_e),f1),limit(f1,xid,nextt,1,contextptr));
    vecteur tvidg=makevecteur(symb_equal(symbolic(at_derive,y__IDNT_e),g1),limit(g1,xid,nextt,1,contextptr));
    vecteur tviconv=makevecteur(symbolic(at_derive,x__IDNT_e)*symbolic(at_derive,symbolic(at_derive,y__IDNT_e))-symbolic(at_derive,y__IDNT_e)*symbolic(at_derive,symbolic(at_derive,x__IDNT_e)),limit(conv,xid,nextt,1,contextptr));
    int tvs=int(tvx.size());
    for (int i=1;i<tvs;++i){
      gen curt=nextt,dfx,dgx,convt;
      nextt=tvx[i];
      tvit.push_back(nothing);
      if (is_inf(nextt) && is_inf(curt)){
	dfx=limit(f1,xid,0,0,contextptr);
	dgx=limit(g1,xid,0,0,contextptr);
	convt=limit(conv,xid,0,0,contextptr);	
      }
      else {
	if (curt==minus_inf){
	  dfx=limit(f1,xid,nextt-1,0,contextptr);
	  dgx=limit(g1,xid,nextt-1,0,contextptr);
	  convt=limit(conv,xid,nextt-1,0,contextptr);
	}
	else {
	  if (nextt==plus_inf){
	    dfx=limit(f1,xid,curt+1,0,contextptr);
	    dgx=limit(g1,xid,curt+1,0,contextptr);
	    convt=limit(conv,xid,curt+1,0,contextptr);
	  }
	  else {
	    dfx=limit(f1,xid,(curt+nextt)/2,0,contextptr);
	    dgx=limit(g1,xid,(curt+nextt)/2,0,contextptr);
	    convt=limit(conv,xid,(curt+nextt)/2,0,contextptr);
	  }
	}
      }
      if (is_zero(dfx) || is_zero(dgx)){
	purgenoassume(t,contextptr);
	return 0;
      }
      if (is_strictly_positive(dfx,contextptr)){
#if defined NSPIRE || defined NSPIRE_NEWLIB || defined HAVE_WINT_T
	tvif.push_back(string2gen("↑",false));
#else
	tvif.push_back(string2gen("↗",false));
#endif
	tvidf.push_back(string2gen("+",false));
      }
      else {
#if defined NSPIRE || defined NSPIRE_NEWLIB || defined HAVE_WINT_T
	tvif.push_back(string2gen("↓",false));
#else
	tvif.push_back(string2gen("↘",false));
#endif
	tvidf.push_back(string2gen("-",false));
      }
      if (is_strictly_positive(convt,contextptr))
	tviconv.push_back(string2gen("convex",false));
      else
	tviconv.push_back(string2gen("concav",false));
      if (is_strictly_positive(dgx,contextptr)){
#if defined NSPIRE || defined NSPIRE_NEWLIB || defined HAVE_WINT_T
	tvig.push_back(string2gen("↑",false));
#else
	tvig.push_back(string2gen("↗",false));
#endif
	tvidg.push_back(string2gen("+",false));
      }
      else {
#if defined NSPIRE || defined NSPIRE_NEWLIB || defined HAVE_WINT_T
	tvig.push_back(string2gen("↓",false));
#else
	tvig.push_back(string2gen("↘",false));
#endif
	tvidg.push_back(string2gen("-",false));
      }
      if (i<tvs-1 && equalposcomp(sing,nextt)){
	x=limit(f,xid,nextt,-1,contextptr);
	x=recursive_normal(x,contextptr);
	if (!is_inf(x) && is_greater(xmin,x,contextptr))
	  xmin=x;
	if (!is_inf(x) && is_greater(x,xmax,contextptr))
	  xmax=x;
	y=limit(g,xid,nextt,-1,contextptr);
	y=recursive_normal(y,contextptr);
	if (!is_inf(y) && is_greater(ymin,y,contextptr))
	  ymin=y;
	if (!is_inf(y) && is_greater(y,ymax,contextptr))
	  ymax=y;
	tvit.push_back(nextt);
	tvif.push_back(x);
	tvig.push_back(y);
	tvidf.push_back(nothing);
	tvidg.push_back(nothing);
	tviconv.push_back(nothing);
	gen x=limit(f,xid,nextt,1,contextptr);
	x=recursive_normal(x,contextptr);
	if (!is_inf(x) && is_greater(xmin,x,contextptr))
	  xmin=x;
	if (!is_inf(x) && is_greater(x,xmax,contextptr))
	  xmax=x;
	y=limit(g,xid,nextt,1,contextptr);
	y=recursive_normal(y,contextptr);
	if (!is_inf(y) && is_greater(ymin,y,contextptr))
	  ymin=y;
	if (!is_inf(y) && is_greater(y,ymax,contextptr))
	  ymax=y;
	tvit.push_back(nextt);
	tvif.push_back(x);
	tvig.push_back(y);
	tvidf.push_back(nothing);
	tvidg.push_back(nothing);
	tviconv.push_back(nothing);
      }
      else {
	gen x=limit(f,xid,nextt,-1,contextptr);
	x=recursive_normal(x,contextptr);
	if (!is_inf(x) && is_greater(xmin,x,contextptr))
	  xmin=x;
	if (!is_inf(x) && is_greater(x,xmax,contextptr))
	  xmax=x;
	y=limit(g,xid,nextt,-1,contextptr);
	y=recursive_normal(y,contextptr);
	if (!is_inf(y) && is_greater(ymin,y,contextptr))
	  ymin=y;
	if (!is_inf(y) && is_greater(y,ymax,contextptr))
	  ymax=y;
	tvit.push_back(nextt);
	tvif.push_back(x);
	tvig.push_back(y);
	y=limit(f1,xid,nextt,-1,contextptr);
	y=recursive_normal(y,contextptr);
	tvidf.push_back(y);
	y=limit(g1,xid,nextt,-1,contextptr);
	y=recursive_normal(y,contextptr);
	tvidg.push_back(y);
	if (equalposcomp(infl,nextt)) y=0;
	else {
	  y=limit(conv,xid,nextt,-1,contextptr);
	  y=recursive_normal(y,contextptr);
	}
	tviconv.push_back(y);
      }
    }
    tvi=makevecteur(tvit,tvif,tvidf,tvig,tvidg,tviconv);
    gen xscale=xmax-xmin;
    if (is_inf(xscale) || xscale==0)
      xscale=gnuplot_xmax-gnuplot_xmin;
    if (eof==2){
      xmax=max(xmax,-xmin,contextptr);
      xmin=-xmax;
    }
    if (eog==2){
      ymax=max(ymax,-ymin,contextptr);
      ymin=-ymax;
    }
    if (eof && eog)
      tmin=-tmax;
    if (periode==0){
      gen tscale=tmax-tmin;
      tmax += tscale/2;
      tmin -= tscale/2;
    }
    if (tmax==tmin){
      tmin=gnuplot_tmin;
      tmax=gnuplot_tmax;
    }
    gen glx(_GL_X);
    glx.subtype=_INT_PLOT;
    glx=symb_equal(glx,symb_interval(xmin-xscale/2,xmax+xscale/2));
    poi.insert(poi.begin(),glx);
    gen yscale=ymax-ymin;
    if (is_inf(yscale) || yscale==0){
      yscale=gnuplot_ymax-gnuplot_ymin;
      ymax=gnuplot_ymax;
      ymin=gnuplot_ymin;
    }
    if (eog==2){
      ymax=max(ymax,-ymin,contextptr);
      ymin=-ymax;
    }
    gen gly(_GL_Y);
    gly.subtype=_INT_PLOT;
    gly=symb_equal(gly,symb_interval(ymin-yscale/2,ymax+yscale/2));
    poi.insert(poi.begin(),gly);
    gprintf("Variations (%gen,%gen)\n%gen",makevecteur(f,g,tvi),1,contextptr);
#ifndef EMCC
    if (printtvi && step_infolevel(contextptr)==0)
      *logptr(contextptr) << tvi << endl;
#endif
    // finished!
    purgenoassume(t,contextptr);
    return 1 + (periode!=1);
  }

  int step_param(const gen & f,const gen & g,const gen & t,gen & tmin,gen&tmax,vecteur & poi,vecteur & tvi,bool printtvi,bool exactlegende,GIAC_CONTEXT){
    bool c=complex_mode(contextptr); int st=step_infolevel(contextptr),s=0;
    if (t==x__IDNT_e || t==y__IDNT_e)
      *logptr(contextptr) << "Warning, using x or y as variable in parametric plot may lead to confusion!" << endl;
    step_infolevel(0,contextptr);
#ifdef NO_STDEXCEPT
    s=step_param_(f,g,t,tmin,tmax,poi,tvi,printtvi,exactlegende,contextptr);
#else
    try {
      s=step_param_(f,g,t,tmin,tmax,poi,tvi,printtvi,exactlegende,contextptr);
    } catch(std::runtime_error & e){ s=0;}
#endif
    complex_mode(c,contextptr);
    step_infolevel(st,contextptr);
    return s;
  }

  // x->f in xmin..xmax
  // pass -inf and inf by default.
  // poi will contain point of interest: asymptotes and extremas
  // xmin and xmax will be set to values containing all points in poi
  int step_func_(const gen & f,const gen & x,gen & xmin,gen&xmax,vecteur & poi,vecteur & tvi,gen& periode,vecteur & asym,vecteur & parab,vecteur & crit,vecteur & infl,bool printtvi,bool exactlegende,GIAC_CONTEXT,bool do_inflex){
    if (x.type!=_IDNT)
      return 0;
    gprintf("====================\nFunction plot %gen, variable %gen",makevecteur(f,x),1,contextptr);
    if (is_periodic(f,x,periode,contextptr)){
      gprintf("Periodic function T=%gen",vecteur(1,periode),1,contextptr);
      if (is_greater(xmax-xmin,periode,contextptr)){
	xmin=normal(-periode/2,contextptr);
	xmax=normal(periode/2,contextptr);
      }
    }
    int eo=0;
    if (xmin==-xmax && (eo=is_even_odd(f,x,contextptr))){
      if (eo==1)
	gprintf("Even function %gen. Reflection Oy",vecteur(1,f),1,contextptr);
      else
	gprintf("Odd function %gen. Center O",vecteur(1,f),1,contextptr);
      xmin=0;
    }
    gen xmin0=ratnormal(xmin,contextptr),xmax0=ratnormal(xmax,contextptr);
    vecteur lv=lidnt(evalf(f,1,contextptr));
    if (lv.empty())
      return 1;
    if (lv.size()!=1 || lv.front()!=x)
      return 0;
    gen df=domain(f,x,0,contextptr);
    gprintf("Domain %gen",vecteur(1,df),1,contextptr);
    gen df1=domain(f,x,1,contextptr); // singular values only
    if (df1.type!=_VECT){
      gensizeerr("Unable to find singular points");
      return 0;
    }
    // Asymptotes
    vecteur sing;
    identificateur xid=*x._IDNTptr;
    iterateur it=df1._VECTptr->begin(),itend=df1._VECTptr->end();
    for (;it!=itend;++it){
      if (is_greater(*it,xmin,contextptr) && is_greater(xmax,*it,contextptr)){
	sing.push_back(*it);
      }
    }
    // Extremas
    int st=step_infolevel(contextptr);
    step_infolevel(0,contextptr);
    gen f1=_factor(derive(f,x,contextptr),contextptr);
    gen f2=derive(f1,x,contextptr);
#if 1
    gen xval=eval(x,1,contextptr);
    giac_assume(symb_and(symb_superieur_egal(x,xmin),symb_inferieur_egal(x,xmax)),contextptr);
    int cm=calc_mode(contextptr);
    calc_mode(-38,contextptr); // avoid rootof
    gen c1=solve(f1,x,periode==0?2:0,contextptr);
    gen c2=(!do_inflex || is_zero(f2))?gen(vecteur(0)):solve(f2,x,periode==0?2:0,contextptr),c(c1);
    calc_mode(cm,contextptr);
    step_infolevel(st,contextptr);
    if (x!=xval)
      sto(xval,x,contextptr);
    if (c1.type!=_VECT){
      *logptr(contextptr) << "Unable to find critical points" << endl;
      return 0;
    }
    if (c2.type==_VECT){
      infl=*c2._VECTptr;
      c=gen(mergevecteur(gen2vecteur(c1),infl));
    }
    else
      *logptr(contextptr) << "Unable to find convexity" << endl;
    // if (c.type==_VECT && c._VECTptr->empty()) c=_fsolve(makesequence(f,x),contextptr);
#else
    gen c=critical(makesequence(f,x),false,contextptr);
    step_infolevel(st,contextptr);
    if (c.type!=_VECT){
      *logptr(contextptr) << "Unable to find critical points" << endl;
      purgenoassume(x,contextptr);
      return 0;
    }
#endif
    if (!lidnt(evalf(c,1,contextptr)).empty()){
      *logptr(contextptr) << "Infinite number of critical points. Try with optional argument " << x << "=xmin..xmax" << endl;
      purgenoassume(x,contextptr);
      return 0;
    }
    it=c._VECTptr->begin();itend=c._VECTptr->end();
    for (;it!=itend;++it){
      if (!lop(*it,at_rootof).empty())
	*it=re(evalf(*it,1,contextptr),contextptr);
      if (in_domain(df,x,*it,contextptr) && is_greater(*it,xmin,contextptr) && is_greater(xmax,*it,contextptr)){
	crit.push_back(*it);
	gen fx=limit(f,xid,*it,0,contextptr);
	fx=recursive_normal(fx,contextptr);
	if (!is_undef(fx) && !is_inf(fx)){
	  if (1 || exactlegende)
	    poi.push_back(_point(makesequence(*it,fx,write_legende(makevecteur(*it,fx),exactlegende,contextptr),symb_equal(at_couleur,equalposcomp(infl,*it)?_GREEN:_MAGENTA)),contextptr));
	  else {
	    gen abscisse=evalf_double(*it,1,contextptr);
	    gen ordonnee=evalf_double(fx,1,contextptr);
	    if (abscisse.type==_DOUBLE_ && ordonnee.type==_DOUBLE_)
	      poi.push_back(_point(makesequence(*it,fx,write_legende(string2gen(print_DOUBLE_(abscisse._DOUBLE_val,3)+","+print_DOUBLE_(ordonnee._DOUBLE_val,3),false),exactlegende,contextptr),symb_equal(at_couleur,_MAGENTA)),contextptr));
	  }
	}
      }
    }
    if (xmin==minus_inf && !equalposcomp(sing,minus_inf)){
      if (in_domain(df,x,xmin,contextptr))
	sing.push_back(xmin);
      xmin=plus_inf;
    }
    if (xmax==plus_inf && !equalposcomp(sing,plus_inf)){
      if (in_domain(df,x,xmax,contextptr))
	sing.push_back(xmax);
      xmax=minus_inf;
    }
    it=crit.begin();itend=crit.end();
    for (;it!=itend;++it){
      if (!is_inf(*it)){ 
	if (is_greater(xmin,*it,contextptr))
	  xmin=*it;
	if (is_greater(*it,xmax,contextptr))
	  xmax=*it;
      }
    }
    it=sing.begin();itend=sing.end();
    for (;it!=itend;++it){
      gen equ;
      if (!is_inf(*it)){ // vertical
	if (is_greater(xmin,*it,contextptr))
	  xmin=*it;
	if (is_greater(*it,xmax,contextptr))
	  xmax=*it;
	gen l=limit(f,xid,*it,1,contextptr);
	l=recursive_normal(l,contextptr);
	if (is_inf(l)){
	  equ=symb_equal(x__IDNT_e,*it);
	  asym.push_back(makevecteur(*it,equ));
	  gprintf("Vertical asymptote %gen",vecteur(1,equ),1,contextptr);
	  poi.push_back(_droite(makesequence(*it,*it+cst_i,write_legende(equ,exactlegende,contextptr),symb_equal(at_couleur,_RED)),contextptr));
	  if (eo && *it!=0){
	    equ=symb_equal(x__IDNT_e,-*it);
	    asym.push_back(makevecteur(-*it,equ));
	    gprintf("Symmetric vertical asymptote %gen",vecteur(1,equ),1,contextptr);
	    poi.push_back(_droite(makesequence(-*it,-*it+cst_i,write_legende(equ,exactlegende,contextptr),symb_equal(at_couleur,_RED)),contextptr));
	  }
	}
	continue;
      }
      gen l=limit(f,xid,*it,0,contextptr);
      l=recursive_normal(l,contextptr);
      if (is_undef(l)) continue;
      if (!is_inf(l)){
	if (!lidnt(evalf(l,1,contextptr)).empty()) continue;
	equ=symb_equal(y__IDNT_e,l);
	asym.push_back(makevecteur(*it,equ));
	gprintf("Horizontal asymptote %gen",vecteur(1,equ),1,contextptr);
	gen dr=_droite(makesequence(l*cst_i,l*cst_i+1,write_legende(equ,exactlegende,contextptr),symb_equal(at_couleur,_RED)),contextptr);
	if (!equalposcomp(poi,dr))
	  poi.push_back(dr);
	if (eo==2 && *it!=0 && l!=0){
	  equ=symb_equal(y__IDNT_e,l);
	  asym.push_back(makevecteur(-*it,-equ));
	  gprintf("Symmetric horizontal asymptote %gen",vecteur(1,-equ),1,contextptr);
	  dr=_droite(makesequence(-l*cst_i,-l*cst_i+1,write_legende(equ,exactlegende,contextptr),symb_equal(at_couleur,_RED)),contextptr);
	  if (!equalposcomp(poi,dr))
	    poi.push_back(dr);
	}
	continue;
      }
      gen a=limit(f/x,xid,*it,0,contextptr);
      a=recursive_normal(a,contextptr);
      if (is_undef(a)) continue;
      if (is_inf(a)){
	parab.push_back(makevecteur(*it,a));
	gprintf("Vertical parabolic asymptote at %gen",vecteur(1,*it),1,contextptr);
	continue;
      }
      else
	if (!lidnt(evalf(a,1,contextptr)).empty()) continue;
      if (is_zero(a)){
	parab.push_back(makevecteur(*it,0));
	gprintf("Horizontal parabolic asymptote at %gen",vecteur(1,*it),1,contextptr);
	continue;
      }
      gen b=limit(f-a*x,xid,*it,0,contextptr);
      b=recursive_normal(b,contextptr);
      if (is_undef(b)) continue;
      // avoid bounded_function
      if (is_inf(b)){
	parab.push_back(makevecteur(*it,a));
	gprintf("Parabolic asymptote direction %gen at infinity",vecteur(1,symb_equal(y__IDNT_e,a*x__IDNT_e)),1,contextptr);
	continue;
      }
      else
	if (!lidnt(evalf(b,1,contextptr)).empty()) continue;
      equ=symb_equal(y__IDNT_e,a*x__IDNT_e+b);
      asym.push_back(makevecteur(*it,equ));
      gprintf("Asymptote %gen",vecteur(1,equ),1,contextptr);
      gen dr=_droite(makesequence(equ,write_legende(equ,exactlegende,contextptr),symb_equal(at_couleur,_RED)),contextptr);
      if (!equalposcomp(poi,dr))
	poi.push_back(dr);
      if (eo && *it!=0){
	if (eo==1)
	  equ=symb_equal(y__IDNT_e,-a*x__IDNT_e+b);
	else
	  equ=symb_equal(y__IDNT_e,a*x__IDNT_e-b);
	asym.push_back(makevecteur(*it,equ));
	gprintf("Symmetric asymptote %gen",vecteur(1,equ),1,contextptr);
	gen dr=_droite(makesequence(equ,write_legende(equ,exactlegende,contextptr),symb_equal(at_couleur,_RED)),contextptr);
	if (!equalposcomp(poi,dr))
	  poi.push_back(dr);
      }
    }
    // merge sing and crit, add xmin0, xmax0, build variation matrix
    for (int i=0;i<int(sing.size());++i)
      sing[i]=ratnormal(sing[i],contextptr);
    for (int i=0;i<int(crit.size());++i)
      crit[i]=ratnormal(crit[i],contextptr);
    vecteur tvx=mergevecteur(sing,crit);
    if (in_domain(df,x,xmin0,contextptr))
      tvx.insert(tvx.begin(),xmin0);
    if (in_domain(df,x,xmax0,contextptr))
      tvx.push_back(xmax0);
    // add endpoints of df
    vecteur ep=endpoints(df);
    for (size_t i=0;i<ep.size();++i){
      if (is_greater(ep[i],xmin0,contextptr) && is_greater(xmax0,ep[i],contextptr) && in_domain(df,x,ep[i],contextptr))
	tvx.push_back(ep[i]);
    }
    comprim(tvx);
    gen tmp=_sort(tvx,contextptr);
    if (tmp.type!=_VECT){
      purgenoassume(x,contextptr);
      return 0;
    }
    tvx=*tmp._VECTptr;
    int pos=equalposcomp(tvx,minus_inf);
    if (pos){
      tvx.erase(tvx.begin()+pos-1);
      tvx.insert(tvx.begin(),minus_inf);
    }
    pos=equalposcomp(tvx,plus_inf);
    if (pos){
      tvx.erase(tvx.begin()+pos-1);
      tvx.push_back(plus_inf);
    }
    gen nextx=tvx.front();
    vecteur tvix=makevecteur(x,nextx);
    gen y=limit(f,xid,nextx,1,contextptr),ymin(plus_inf),ymax(minus_inf);
    if (!is_inf(y) && is_greater(ymin,y,contextptr))
      ymin=y;
    if (!is_inf(y) && is_greater(y,ymax,contextptr))
      ymax=y;
    vecteur tvif=makevecteur(symb_equal(y__IDNT_e,f),y);
    gen nothing=string2gen(" ",false);
    vecteur tvidf=makevecteur(symb_equal(symbolic(at_derive,y__IDNT_e),f1),limit(f1,xid,nextx,1,contextptr));
    vecteur tvidf2=makevecteur(symbolic(at_derive,symbolic(at_derive,y__IDNT_e)),limit(f2,xid,nextx,1,contextptr));
    int tvs=int(tvx.size());
    for (int i=1;i<tvs;++i){
      gen curx=nextx,dfx,df2;
      nextx=tvx[i];
      tvix.push_back(nothing);
      if (is_inf(nextx) && is_inf(curx)){
	dfx=limit(f1,xid,0,0,contextptr);
	df2=limit(f2,xid,0,0,contextptr);
      }
      else {
	if (curx==minus_inf){
	  dfx=limit(f1,xid,nextx-1,0,contextptr);
	  df2=limit(f2,xid,nextx-1,0,contextptr);
	}
	else {
	  if (nextx==plus_inf){
	    dfx=limit(f1,xid,curx+1,0,contextptr);
	    df2=limit(f2,xid,curx+1,0,contextptr);
	  }
	  else {
	    gen m=(curx+nextx)/2;
	    if (in_domain(df,x,m,contextptr)){
	      dfx=limit(f1,xid,m,0,contextptr);
	      df2=limit(f2,xid,m,0,contextptr);
	    }
	    else dfx=df2=undef;
	  }
	}
      }
      if (is_zero(dfx)){
	purgenoassume(x,contextptr);
	return 0;
      }
      if (is_undef(dfx)){
	tvif.push_back(string2gen("X",false));
	tvidf.push_back(string2gen("X",false));
      }
      else {
	if (is_strictly_positive(dfx,contextptr)){
#if defined NSPIRE || defined NSPIRE_NEWLIB || defined HAVE_WINT_T
	  tvif.push_back(string2gen("↑",false));
#else
	  tvif.push_back(string2gen("↗",false));
#endif
	  tvidf.push_back(string2gen("+",false));
	}
	else {
#if defined NSPIRE || defined NSPIRE_NEWLIB || defined HAVE_WINT_T
	  tvif.push_back(string2gen("↓",false));
#else
	  tvif.push_back(string2gen("↘",false));
#endif
	  tvidf.push_back(string2gen("-",false));
	}
      }
      if (do_inflex){
	if (is_undef(df2))
	  tvidf2.push_back(string2gen("X",false));
	else {
	  if (is_strictly_positive(df2,contextptr)){
	    tvidf2.push_back(string2gen("convex",false));
	  }
	  else {
	    tvidf2.push_back(string2gen("concav",false));
	  }
	}
      }
      if (i<tvs-1 && equalposcomp(sing,nextx)){
	y=limit(f,xid,nextx,-1,contextptr);
	y=recursive_normal(y,contextptr);
	if (!is_inf(y) && is_greater(ymin,y,contextptr))
	  ymin=y;
	if (!is_inf(y) && is_greater(y,ymax,contextptr))
	  ymax=y;
	tvix.push_back(nextx);
	tvif.push_back(y);
	tvidf.push_back(string2gen("||",false));
	if (do_inflex) tvidf2.push_back(string2gen("||",false));
	y=limit(f,xid,nextx,1,contextptr);
	y=recursive_normal(y,contextptr);
	if (!is_inf(y) && is_greater(ymin,y,contextptr))
	  ymin=y;
	if (!is_inf(y) && is_greater(y,ymax,contextptr))
	  ymax=y;
	tvix.push_back(nextx);
	tvif.push_back(y);
	tvidf.push_back(string2gen("||",false));
	if (do_inflex) tvidf2.push_back(string2gen("||",false));
      }
      else {
	y=limit(f,xid,nextx,-1,contextptr);
	y=recursive_normal(y,contextptr);
	if (!is_inf(y) && is_greater(ymin,y,contextptr))
	  ymin=y;
	if (!is_inf(y) && is_greater(y,ymax,contextptr))
	  ymax=y;
	tvix.push_back(nextx);
	tvif.push_back(y);
	y=limit(f1,xid,nextx,-1,contextptr);
	y=recursive_normal(y,contextptr);
	tvidf.push_back(y);
	y=limit(f2,xid,nextx,-1,contextptr);
	y=recursive_normal(y,contextptr);
	if (do_inflex) tvidf2.push_back(y);
      }
    }
    tvi=makevecteur(tvix,tvif,tvidf);
    if (do_inflex) tvi.push_back(tvidf2);
    gen yscale=ymax-ymin;
    if (is_inf(yscale) || yscale==0){
      yscale=xmax-xmin;
      ymax=gnuplot_ymax;
      ymin=gnuplot_ymin;
    }
    if (is_inf(yscale) || yscale==0){
      yscale=gnuplot_ymax-gnuplot_ymin;
      ymax=gnuplot_ymax;
      ymin=gnuplot_ymin;
    }
    if (eo){
      xmax=max(xmax,-xmin,contextptr);
      xmin=-xmax;
      if (eo==2){
	ymax=max(ymax,-ymin,contextptr);
	ymin=-ymax;
      }
    }
    gen gly(_GL_Y);
    gly.subtype=_INT_PLOT;
    gly=symb_equal(gly,symb_interval(ymin-yscale/2,ymax+yscale/2));
    poi.insert(poi.begin(),gly);
    gprintf("Variations %gen\n%gen",makevecteur(f,tvi),1,contextptr);
#ifndef EMCC
    if (printtvi && step_infolevel(contextptr)==0)
      *logptr(contextptr) << tvi << endl;
#endif
    // finished!
    purgenoassume(x,contextptr);
    return 1 + (periode!=0);
  }

  int step_func(const gen & f,const gen & x,gen & xmin,gen&xmax,vecteur & poi,vecteur & tvi,gen & periode,vecteur & asym,vecteur & parab,vecteur & crit,vecteur & inflex,bool printtvi,bool exactlegende,GIAC_CONTEXT,bool do_inflex){
    bool c=complex_mode(contextptr); int st=step_infolevel(contextptr),s=0;
    step_infolevel(0,contextptr);
#ifdef NO_STDEXCEPT
    s=step_func_(f,x,xmin,xmax,poi,tvi,periode,asym,parab,crit,inflex,printtvi,exactlegende,contextptr,do_inflex);
#else
    try {
      s=step_func_(f,x,xmin,xmax,poi,tvi,periode,asym,parab,crit,inflex,printtvi,exactlegende,contextptr,do_inflex);
    } catch (std::runtime_error & e){s=0;}
#endif
    complex_mode(c,contextptr);
    step_infolevel(st,contextptr);
    return s;
  }

  gen _tabvar(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(g.type==_VECT && g.subtype==_SEQ__VECT?*g._VECTptr:vecteur(1,g));
    int s=int(v.size());
#ifdef EMCC
    int plot=1;
#else
    int plot=0;
#endif
    bool return_tabvar=false,return_equation=false,return_coordonnees=false,do_inflex=true;
    for (int i=0;i<s;++i){
      if (v[i]==at_plot){
	plot=2;
	v.erase(v.begin()+i);
	--s; --i; continue;
      }
      if (v[i]==at_tabvar){
	return_tabvar=true;
	v.erase(v.begin()+i);
	--s; --i; continue;
      }
      if (v[i]==at_equation){
	return_equation=true;
	v.erase(v.begin()+i);
	--s; --i; continue;
      }
      if (v[i]==at_coordonnees){
	return_coordonnees=true;
	v.erase(v.begin()+i);
	--s; --i; continue;
      }
      if (v[i]==at_derive){
	do_inflex=false;
	v.erase(v.begin()+i);
	--s; --i; continue;
      }
    }
    bool exactlegende=false;
    if (s>1 && v[s-1]==at_exact){
      exactlegende=true;
      v.pop_back();
      --s;
    }
    if (s==2 && v[1].type==_SYMB && v[1]._SYMBptr->sommet!=at_equal)
      v=makevecteur(v,ggb_var(v));
    if (s==1){
      v.push_back(ggb_var(g));
      ++s;
    }
    if (s<2)
      return gensizeerr(contextptr);
    gen f=v[0];
    gen x=v[1];
    int s0=2;
    gen xmin(minus_inf),xmax(plus_inf);
    bool default_interval=true;
    if (x.is_symb_of_sommet(at_equal)){
      gen g=x._SYMBptr->feuille;
      if (g.type!=_VECT || g._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      x=g._VECTptr->front();
      g=g._VECTptr->back();
      if (g.is_symb_of_sommet(at_interval)){
	xmin=g._SYMBptr->feuille[0];
	xmax=g._SYMBptr->feuille[1];
	default_interval=(xmin==minus_inf && xmax==plus_inf);
      }
    }
    else {
      if (s>=4){
	xmin=v[2];
	xmax=v[3];
	default_interval=(xmin==minus_inf && xmax==plus_inf);
	s0=4;
      }
      if (s==2 && x.type!=_IDNT)
	return _tabvar(makevecteur(f,x),contextptr);
    }
    vecteur tvi,poi;
    bool param=f.type==_VECT && f._VECTptr->size()==2;
    int periodic=0;
    if (param)
      periodic=step_param(f._VECTptr->front(),f._VECTptr->back(),x,xmin,xmax,poi,tvi,false,exactlegende,contextptr);
    else {
      gen periode; vecteur asym,parab,crit,inflex;
      periodic=step_func(f,x,xmin,xmax,poi,tvi,periode,asym,parab,crit,inflex,false,exactlegende,contextptr,do_inflex);
    }
    // round floats in tvi
    for (int i=0;i<int(tvi.size());++i){
      gen tmp=tvi[i];
      if (tmp.type==_VECT){
	vecteur v=*tmp._VECTptr;
	for (int j=0;j<int(v.size());++j){
	  if (v[j].type==_DOUBLE_)
	    v[j]=_round(makesequence(v[j],3),contextptr);
	}
	tvi[i]=gen(v,tmp.subtype);
      }
    }
    if (periodic==0)
      return undef;
    if (return_tabvar)
      return tvi;
    if (return_equation)
      return _equation(poi,contextptr);
    if (return_coordonnees)
      return _coordonnees(poi,contextptr);
    gen scale=(gnuplot_xmax-gnuplot_xmin)/5.0;
    gen m=xmin,M=xmax;
    if (is_inf(m))
      m=gnuplot_xmin;
    if (is_inf(M))
      M=gnuplot_xmax;
    if (m!=M)
      scale=(M-m)/3.0;
    if (xmin!=xmax && (periodic==2 || !default_interval) ){
      m=m-0.009*scale; M=M+0.01*scale;
    }
    else {
      m=m-0.973456*scale; M=M+1.018546*scale;
    }
    x=symb_equal(x,symb_interval(m,M));
    vecteur w=makevecteur(f,x);
    for (;s0<s;++s0){
      w.push_back(v[s0]);
    }
    gen p;
    if (param)
      p=paramplotparam(gen(w,_SEQ__VECT),false,contextptr);
    else
      p=funcplotfunc(gen(w,_SEQ__VECT),false,contextptr);
    if (plot){
      poi=mergevecteur(poi,gen2vecteur(p));
      if (plot==2)
	return gen(poi,_SEQ__VECT);
      if (plot==1)
	return tvi; // gprintf("%gen",makevecteur(gen(poi,_SEQ__VECT)),1,contextptr);
    }
    *logptr(contextptr) << (param?"plotparam(":"plotfunc(") << gen(w,_SEQ__VECT) << ')' <<"\nInside Xcas you can see the function with Cfg>Show>DispG." <<  endl;
    return tvi;
  }
  static const char _tabvar_s []="tabvar";
  static define_unary_function_eval (__tabvar,&_tabvar,_tabvar_s);
  define_unary_function_ptr5( at_tabvar ,alias_at_tabvar,&__tabvar,0,true);

  gen _printf(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args.subtype!=_SEQ__VECT){
      gprintf("%gen",vecteur(1,args),contextptr);
      return 1;
    }
    vecteur v=*args._VECTptr;
    if (v.empty() || v.front().type!=_STRNG)
      return 0;
    string s=*v.front()._STRNGptr;
    v.erase(v.begin());
    gprintf(s,v,contextptr);
    return 1;
  }
  static const char _printf_s []="printf";
  static define_unary_function_eval (__printf,&_printf,_printf_s);
  define_unary_function_ptr5( at_printf ,alias_at_printf,&__printf,0,true);

  gen _sech(const gen & args,GIAC_CONTEXT){
    return inv(cosh(args,contextptr),contextptr);
  }
  static const char _sech_s []="sech";
  static define_unary_function_eval (__sech,&_sech,_sech_s);
  define_unary_function_ptr5( at_sech ,alias_at_sech,&__sech,0,true);

  gen _csch(const gen & args,GIAC_CONTEXT){
    return inv(sinh(args,contextptr),contextptr);
  }
  static const char _csch_s []="csch";
  static define_unary_function_eval (__csch,&_csch,_csch_s);
  define_unary_function_ptr5( at_csch ,alias_at_csch,&__csch,0,true);

  // ggb function for latitude of a 3-d point
  // was ggbalt(x):=when(type(x)==DOM_IDENT,altsymb(x),when(x[0]=='pnt',when(is3dpoint(x),atan2(x[1][2],sqrt(x[1][0]^2+x[1][1]^2)),0),?))
  gen _ggbalt(const gen & args,GIAC_CONTEXT){
    if (args.type==_IDNT)
      return symbolic(at_ggbalt,args);
    if (args.is_symb_of_sommet(at_pnt)){
      gen x=remove_at_pnt(args);
      if (x.type==_VECT && x.subtype==_POINT__VECT && x._VECTptr->size()==3 ){
	vecteur v=*x._VECTptr;
	return arg(sqrt(pow(v[0],2,contextptr)+pow(v[1],2,contextptr),contextptr)+cst_i*v[2],contextptr);
      }
      if (args.type==_SYMB && equalposcomp(not_point_sommets,args._SYMBptr->sommet))
	return undef;
      return 0;
    }
    return undef;
  }
  static const char _ggbalt_s []="ggbalt";
  static define_unary_function_eval (__ggbalt,&_ggbalt,_ggbalt_s);
  define_unary_function_ptr5( at_ggbalt ,alias_at_ggbalt,&__ggbalt,0,true);

  // ggbsort(x):=when(length(x)==0,{},when(type(x[0])==DOM_LIST,x,sort(x)))
  gen _ggbsort(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->empty() || args._VECTptr->front().type==_VECT) return args;
    return _sort(args,contextptr);
  }
  static const char _ggbsort_s []="ggbsort";
  static define_unary_function_eval (__ggbsort,&_ggbsort,_ggbsort_s);
  define_unary_function_ptr5( at_ggbsort ,alias_at_ggbsort,&__ggbsort,0,true);

  int charx2int(char c){
    if (c>='0' && c<='9') return c-'0';
    if (c>='a' && c<='z') return c-'a'+10;
    if (c>='A' && c<='Z') return c-'A'+10;
    return -1;
  }

  string html_filter(const string & s){
    int ss=s.size();
    string res;
    bool semi=false;
    for (int i=0;i<ss;++i){
      char c=s[i];
      if (i<ss-2 && c=='%'){
	c = char(charx2int(s[i+1])*16+charx2int(s[i+2]));
	i += 2;
      }
      if (c==';') 
	semi=true;
      else {
	if (c!=' ')
	  semi=false;
      }
      res += c;
    }
    if (!semi)
      res += ';';
    return res;
  }

  // translate HTML Xcas for Firefox link to a giac list of commands
  string link2giac(const string & s,GIAC_CONTEXT){
    string res;
    // find # position, then create normal line for +, slider for *
    int pos=s.find('#'),L=s.size();
    if (pos>0 && pos<L){
      bool finished=false;
      while (!finished){
	int nextpos=s.find('&',pos+1);
	if (nextpos > L){
	  nextpos=L;
	  finished=true;
	}
	if (nextpos<pos+2)
	  break;
	string txt=s.substr(pos+2,nextpos-pos-2);
	txt=html_filter(txt);
	if (s[pos+1]=='*'){
	  gen g(txt,contextptr);
	  if (g.type==_VECT && g._VECTptr->size()>=5){
	    txt="assume("+g[0].print(contextptr)+"=["+g[1].print(contextptr)+","+g[2].print(contextptr)+","+g[3].print(contextptr)+","+g[4].print(contextptr)+"])";
	  }
	}
	res += txt;
	pos=nextpos;
      }
    }
    return res;
  }

  gen _link2giac(const gen & args,GIAC_CONTEXT){
    if (args.type!=_STRNG)
      return gensizeerr(contextptr);
    return string2gen(link2giac(*args._STRNGptr,contextptr),false);
  }
  static const char _link2giac_s []="link2giac";
  static define_unary_function_eval (__link2giac,&_link2giac,_link2giac_s);
  define_unary_function_ptr5( at_link2giac ,alias_at_link2giac,&__link2giac,0,true);

  gen _range(const gen & args,GIAC_CONTEXT){
    gen g(args);
    if (is_integral(g) && g.type==_INT_ && g.val>=0){
      int n=g.val;
      vecteur v(n);
      for (int i=0;i<n;++i)
	v[i]=i;
      return v;
    }
    if (g.type==_VECT && g._VECTptr->size()>=2){
      gen a=g._VECTptr->front(),b=(*g._VECTptr)[1],c=1;
      if (g._VECTptr->size()==3)
	c=g._VECTptr->back();
      if (is_integral(a) && is_integral(b) && is_integral(c)){
	int A=a.val,B=b.val,C=c.val;
	if ( (A<=B && C>0) || (A>=B && C<0)){
	  int s=std::ceil(double(B-A)/C);
	  vecteur w(s);
	  for (int i=0;i<s;++i)
	    w[i]=A+i*C;
	  return w;
	}
      }
    }
    return gensizeerr(contextptr);
  }
  static const char _range_s []="range";
  static define_unary_function_eval (__range,&_range,_range_s);
  define_unary_function_ptr5( at_range ,alias_at_range,&__range,0,true);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
