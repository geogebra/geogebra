/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c desolve.cc  -DHAVE_CONFIG_H -DIN_GIAC" -*- */
#include "giacPCH.h"
/*
 *  Copyright (C) 2000, 2007 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#include "desolve.h"
#include "derive.h"
#include "intg.h"
#include "subst.h"
#include "usual.h"
#include "symbolic.h"
#include "unary.h"
#include "poly.h"
#include "sym2poly.h" // for equalposcomp
#include "tex.h"
#include "modpoly.h"
#include "series.h"
#include "solve.h"
#include "ifactor.h"
#include "prog.h"
#include "rpn.h"
#include "lin.h"
#include "intgab.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  gen integrate_without_lnabs(const gen & e,const gen & x,GIAC_CONTEXT){
    bool save_do_lnabs=do_lnabs(contextptr);
    do_lnabs(false,contextptr);
    gen res=integrate_gen(e,x,contextptr);
    do_lnabs(save_do_lnabs,contextptr);
    return res;
  }

  gen laplace(const gen & f0,const gen & x,const gen & s,GIAC_CONTEXT){
    if (x.type!=_IDNT)
      return gensizeerr(contextptr);
    gen t(s);
    if (s==x){
#ifdef GIAC_HAS_STO_38
      t=identificateur("s");
#else
      t=identificateur(" t");
#endif
    }
    // check for negative powers of x in f
    gen f(f0);
    vecteur v(1,x);
    lvar(f,v);
    fraction ff=sym2r(f,v,contextptr);
    gen ffden=ff.den;
    int n=0;
    if (ffden.type==_POLY){
      polynome & ffdenp = *ffden._POLYptr;
      if (!ffdenp.coord.empty() && (n=ffdenp.coord.back().index.front()) ){
	// multiply by (-1)^n*x^n, do laplace, then integrate n times answer
	index_t idxt(v.size());
	idxt.front()=-n;
	ff=fraction(ff.num,ffden._POLYptr->shift(idxt));
	f=r2sym(ff,v,contextptr);
	if (n%2)
	  f=-f;
      }
    }
    if (!assume_t_in_ab(t,plus_inf,plus_inf,true,true,contextptr))
      return gensizeerr(contextptr);
    gen res=_integrate(gen(makevecteur(f*exp(-t*x,contextptr),x,0,plus_inf),_SEQ__VECT),contextptr);
    for (int i=1;i<=n;++i){
      if (is_undef(res))
	return res;
      res = _integrate(gen(makevecteur(res,t,0,t),_SEQ__VECT),contextptr);
      res += _integrate(gen(makevecteur(f/pow(-x,i),x,0,plus_inf),_SEQ__VECT),contextptr);
    }
    _purge(t,contextptr);
    if (s==x)
      res=subst(res,t,x,false,contextptr);
    return ratnormal(res);
    /*
    gen remains,res=integrate(f*exp(-t*x,contextptr),*x._IDNTptr,remains,contextptr);
    res=subst(-res,x,zero,false,contextptr);
    if (s==x)
      res=subst(res,t,x,false,contextptr);
    if (!is_zero(remains))
      res = res +symbolic(at_integrate,gen(makevecteur(remains,x,0,plus_inf),_SEQ__VECT));
    return res;
    */
  }
    
  // "unary" version
  gen _laplace(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return laplace(args,vx_var,vx_var,contextptr);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s==2)
      return laplace( v[0],v[1],v[1],contextptr);
    if (s!=3)
      return gensizeerr(contextptr);
    return laplace( v[0],v[1],v[2],contextptr);    
  }
  static const char _laplace_s []="laplace";
  static define_unary_function_eval (__laplace,&_laplace,_laplace_s);
  define_unary_function_ptr5( at_laplace ,alias_at_laplace,&__laplace,0,true);

  polynome cstcoeff(const polynome & p){
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      if (it->index.front()==0)
	break;
    }
    return polynome(p.dim,vector< monomial<gen> >(it,itend));
  }

  // reduction of a fraction with multiple poles to single poles by integration
  // by part, use the relation
  // ilaplace(P'/P^(k+1))=laplacevar/k*ilaplace(1/P^k)
  pf<gen> laplace_reduce_pf(const pf<gen> & p_cst, tensor<gen> & laplacevar ){
    pf<gen> p(p_cst);
    assert(p.mult>0);
    if (p.mult==1)
      return p_cst;
    tensor<gen> fprime=p.fact.derivative();
    tensor<gen> d(fprime.dim),C(fprime.dim),u(fprime.dim),v(fprime.dim);
    egcdpsr(p.fact,fprime,u,v,d); // f*u+f'*v=d
    tensor<gen> usave(u),vsave(v);
    // int initial_mult=p.mult-1;
    while (p.mult>1){
      egcdtoabcuv(p.fact,fprime,p.num,u,v,d,C);
      p.mult--;
      p.den=(p.den/p.fact)*C*gen(p.mult);
      p.num=u*gen(p.mult)+v.derivative()+v*laplacevar;
      if ( (p.mult % 5)==1) // simplify from time to time
	TsimplifybyTlgcd(p.num,p.den);
      if (p.mult==1)
	break;
      u=usave;
      v=vsave;
    }
    return pf<gen>(p);
  }

  static gen pf_ilaplace(const gen & e0,const gen & x, gen & remains,GIAC_CONTEXT){
    vecteur vexp;
    gen res;
    lin(e0,vexp,contextptr); // vexp = coeff, arg of exponential
    const_iterateur it=vexp.begin(),itend=vexp.end();
    remains=0;
    for (;it!=itend;){
      gen coeff=*it;
      ++it;
      gen axb=*it,expa,expb;
      ++it;
      gen e=coeff*exp(axb,contextptr);
      if (!is_linear_wrt(axb,x,expa,expb,contextptr)){
	remains += e;
	continue;
      }
      if (is_strictly_positive(expa,contextptr))
	*logptr(contextptr) << gettext("Warning, exponential x coeff is positive ") << expa << endl;
      vecteur varx(lvarx(coeff,x));
      int varxs=varx.size();
      if (!varxs){ // Dirac function
	res += e*symbolic(at_Dirac,x);
	continue;
      }
      if ( (varxs>1) || (varx.front()!=x) ) {
	remains += e;
	continue;
      }
      vecteur l;
      l.push_back(x); // insure x is the main var
      l.push_back(laplace_var); // s var as second var
      l=vecteur(1,l);
      alg_lvar(makevecteur(coeff,axb),l);
      gen glap=e2r(laplace_var,l,contextptr);
      if (glap.type!=_POLY)  return gensizeerr(gettext("desolve.cc/pf_ilaplace"));
      int s=l.front()._VECTptr->size();
      if (!s){
	l.erase(l.begin());
	s=l.front()._VECTptr->size();
      }
      gen r=e2r(coeff,l,contextptr);
      gen r_num,r_den;
      fxnd(r,r_num,r_den);
      if (r_num.type==_EXT){
	remains += e;
	continue;
      }
      if (r_den.type!=_POLY){ 
	remains += e;
	continue;
      }
      polynome den(*r_den._POLYptr),num(s);
      if (r_num.type==_POLY)
	num=*r_num._POLYptr;
      else
	num=polynome(r_num,s);
      polynome p_content(lgcd(den));
      factorization vden(sqff(den/p_content)); // first square-free factorization
      vector< pf<gen> > pfde_VECT;
      polynome ipnum(s),ipden(s),temp(s),tmp(s);
      partfrac(num,den,vden,pfde_VECT,ipnum,ipden);
      vector< pf<gen> >::iterator it=pfde_VECT.begin();
      vector< pf<gen> >::const_iterator itend=pfde_VECT.end();
      vector< pf<gen> > rest,finalde_VECT;
      for (;it!=itend;++it){
	pf<gen> single(laplace_reduce_pf(*it,*glap._POLYptr));
	gen extra_div=1;
	factor(single.den,p_content,vden,false,withsqrt(contextptr),complex_mode(contextptr),1,extra_div);
	partfrac(single.num,single.den,vden,finalde_VECT,temp,tmp);
      }
      it=finalde_VECT.begin();
      itend=finalde_VECT.end();
      gen lnpart(0),deuxaxplusb,sqrtdelta,exppart;
      polynome a(s),b(s),c(s);
      polynome d(s),E(s),lnpartden(s);
      polynome delta(s),atannum(s),alpha(s);
      vecteur lprime(l);
      if (lprime.front().type!=_VECT)  return gensizeerr(gettext("desolve.cc/pf_ilaplace"));
      lprime.front()=cdr_VECT(*(lprime.front()._VECTptr));
      bool uselog;
      for (;it!=itend;++it){
	int deg=it->fact.lexsorted_degree();
	switch (deg) {
	case 1: // 1st order
	  findde(it->den,a,b);	
	  lnpart=lnpart+rdiv(r2e(it->num,l,contextptr),r2e(firstcoeff(a),lprime,contextptr),contextptr)*exp(r2e(rdiv(-b,a,contextptr),lprime,contextptr)*laplace_var,contextptr);
	  break; 
	case 2: // 2nd order
	  findabcdelta(it->fact,a,b,c,delta);
	  exppart=exp(r2e(rdiv(-b,gen(2)*a,contextptr),lprime,contextptr)*laplace_var,contextptr);
	  uselog=is_positive(delta);
	  alpha=(it->den/it->fact).trunc1()*a;
	  findde(it->num,d,E);
	  atannum=a*E*gen(2)-b*d;
	  // cos part d/alpha*ln(fact)
	  lnpartden=alpha;
	  simplify(d,lnpartden);
	  if (uselog){
	    sqrtdelta=normal(sqrt(r2e(delta,lprime,contextptr),contextptr),contextptr);
	    gen racine=ratnormal(sqrtdelta/gen(2)/r2e(a,lprime,contextptr));
	    lnpart=lnpart+rdiv(r2e(d,lprime,contextptr),r2e(lnpartden,lprime,contextptr),contextptr)*cosh(racine*laplace_var,contextptr)*exppart;
	    gen aa=ratnormal(r2e(atannum,lprime,contextptr)/r2e(alpha,lprime,contextptr)/sqrtdelta);
	    lnpart=lnpart+aa*sinh(racine*laplace_var,contextptr)*exppart;
	  }
	  else {
	    sqrtdelta=normal(sqrt(r2e(-delta,lprime,contextptr),contextptr),contextptr);
	    gen racine=ratnormal(sqrtdelta/gen(2)/r2e(a,lprime,contextptr));
	    lnpart=lnpart+rdiv(r2e(d,lprime,contextptr),r2e(lnpartden,lprime,contextptr),contextptr)*cos(racine*laplace_var,contextptr)*exppart;
	    gen aa=ratnormal(r2e(atannum,lprime,contextptr)/r2e(alpha,lprime,contextptr)/sqrtdelta);
	    lnpart=lnpart+aa*sin(racine*laplace_var,contextptr)*exppart;
	  }
	  break; 
	default:
	  rest.push_back(pf<gen>(it->num,it->den,it->fact,1));
	  break ;
	}
      }
      vecteur ipnumv=polynome2poly1(ipnum,1);
      gen deno=r2e(ipden,l,contextptr);
      int nums=ipnumv.size();
      for (int i=0;i<nums;++i){
	gen tmp = rdiv(r2e(ipnumv[i],lprime,contextptr),deno,contextptr);
	tmp = tmp*symbolic(at_Dirac,(i==nums-1)?laplace_var:gen(makevecteur(laplace_var,nums-1-i),_SEQ__VECT));
	res += tmp;
      }
      remains += r2sym(rest,l,contextptr)*exp(axb,contextptr);
      if (is_zero(expa))
	res += lnpart*exp(expb,contextptr);
      else
	res += quotesubst(lnpart,laplace_var,laplace_var+expa,contextptr)*exp(expb,contextptr)*_Heaviside(laplace_var+expa,contextptr);
    }
    return res;
  }

  gen ilaplace(const gen & f,const gen & x,const gen & s,GIAC_CONTEXT){
    if (x.type!=_IDNT)
      return gensizeerr(contextptr);
    gen remains,res=linear_apply(f,x,remains,contextptr,pf_ilaplace);
    res=subst(res,laplace_var,s,false,contextptr);
    if (!is_zero(remains))
      res=res+symbolic(at_ilaplace,makevecteur(remains,x,s));
    return res;
  }
  // "unary" version
  gen _ilaplace(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return ilaplace(args,vx_var,vx_var,contextptr);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s==2)
      return ilaplace( v[0],v[1],v[1],contextptr);
    if (s!=3)
      return gensizeerr(contextptr);
    return ilaplace( v[0],v[1],v[2],contextptr);
  }
  static const char _ilaplace_s []="ilaplace";
  static define_unary_function_eval (__ilaplace,&_ilaplace,_ilaplace_s);
  define_unary_function_ptr5( at_ilaplace ,alias_at_ilaplace,&__ilaplace,0,true);

  static const char _invlaplace_s []="invlaplace";
  static define_unary_function_eval (__invlaplace,&_ilaplace,_invlaplace_s);
  define_unary_function_ptr5( at_invlaplace ,alias_at_invlaplace,&__invlaplace,0,true);

  static gen unable_to_solve_diffeq(){
    return gensizeerr(gettext("Unable to solve differential equation"));
  }

  gen diffeq_constante(int i,GIAC_CONTEXT){
#ifdef GIAC_HAS_STO_38
    string s("G_"+print_INT_(i));
#else
    string s("c_"+print_INT_(i));
#endif
    return gen(s,contextptr);
  }

  // return -1 if f does not depend on y
  static int diffeq_order(const gen & f,const gen & y){
    vecteur ydepend(rlvarx(f,y));
    const_iterateur it=ydepend.begin(),itend=ydepend.end();
    // since we did a recursive lvar we dismiss all variables except
    // if they begin with derive
    int n=-1;
    for (;it!=itend;++it){
      if (*it==y)
	n=giacmax(n,0);
      if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_derive) ){
	gen & g=it->_SYMBptr->feuille;
	int m=-1,nder=1;
	if ( (g.type==_VECT) && (!g._VECTptr->empty()) ){
	  m=diffeq_order(g._VECTptr->front(),y);
	  if (g._VECTptr->size()==3){
	    gen & gg=g._VECTptr->back();
	    if (gg.type==_INT_)
	      nder=gg.val;
	  }
	}
	else 
	  m=diffeq_order(g,y);
	if (m>=0)
	  n=giacmax(n,m+nder);
      }
    }
    return n;
  }

  // true if f is a linear differential equation
  // & returns the coefficient in v in descending order
  // v has size order+2 with last term=cst coeff of the diff equation
  static bool is_linear_diffeq(const gen & f_orig,const gen & x,const gen & y,int order,vecteur & v,GIAC_CONTEXT){
    v.clear();
    gen f(f_orig),a,b,cur_y(y);
#ifdef GIAC_HAS_STO_38
    identificateur t("t");
#else
    identificateur t(" t");
#endif
    for (int i=0;i<=order;++i){
      gen ftmp(quotesubst(f,cur_y,t,contextptr));
      if (!is_linear_wrt(eval(ftmp,eval_level(contextptr),contextptr),t,a,b,contextptr))
	return false;
      if (!rlvarx(a,y).empty())
	return false;
      if (!i)
	v.push_back(b);
      v.push_back(a);
      cur_y=symb_derive(y,x,i+1);
    }
    reverse(v.begin(),v.end());
    return true;
  }

  static bool find_n_derivatives_function(const gen & f,const gen & x,int & nder,gen & fonction){
    if ( (f.type!=_SYMB) || (f._SYMBptr->sommet!=at_derive) ){
      nder=0;
      fonction=f;
      return true;
    }
    if (f._SYMBptr->feuille.type!=_VECT){
      if (!find_n_derivatives_function(f._SYMBptr->feuille,x,nder,fonction))
	return false;
      ++nder;
      return true;
    }
    vecteur & v=*f._SYMBptr->feuille._VECTptr;
    if ( (v.size()>1) && (v[1]!=x) )
      return false; // setsizeerr(contextptr);
    if (!find_n_derivatives_function(v[0],x,nder,fonction))
      return false;
    if ( (v.size()==3) && (v[2].type==_INT_) )
      nder += v[2].val;
    else
      nder += 1;
    return true;
  }

  static gen function_of(const gen & y_orig,const gen & x_orig){
    if ( (y_orig.type!=_SYMB) || (y_orig._SYMBptr->sommet!=at_of) )
      return gensizeerr(gettext("function_of"));
    vecteur & v =*y_orig._SYMBptr->feuille._VECTptr;
    if ( (v[1]!=x_orig) || (v[0].type!=_IDNT) )
      return gensizeerr(gettext("function_of"));
    return v[0];
  }

  static gen in_desolve_with_conditions(const vecteur & v_,const gen & x,const gen & y,const gen & solution_generale,const vecteur & parameters,GIAC_CONTEXT){
    gen yy(y);
    vecteur v(v_);
    if (yy.type!=_IDNT)
      yy=function_of(y,x);
    if (is_undef(yy))
      return yy;
    // solve boundary conditions
    iterateur jt=v.begin()+1,jtend=v.end();
    for (unsigned ndiff=0;jt!=jtend;++ndiff,++jt){
      if (jt->type==_VECT && jt->_VECTptr->size()==2){
	if (ndiff)
	  *jt=symbolic(at_of,makesequence(symbolic(at_derive,makesequence(y,x,int(ndiff))),jt->_VECTptr->front()))-jt->_VECTptr->back();
	else
	  *jt=symbolic(at_of,makesequence(y,jt->_VECTptr->front()))-jt->_VECTptr->back();
      }
    }
    const_iterateur it=v.begin()+1,itend=v.end();
    vecteur conditions(remove_equal(it,itend));
    if (conditions.empty())
      return solution_generale;
    // conditions must be in terms of y(value) or derivatives
    vecteur condvar(rlvarx(conditions,yy));
    vecteur yvar; // will contain triplet (var,n,x) n=nth derivative, x point
    it=condvar.begin(),itend=condvar.end();
    int maxnder=0;
    for (;it!=itend;++it){
      if ( (it->type!=_SYMB) || (it->_SYMBptr->sommet!=at_of) )
	continue;
      vecteur & w=*it->_SYMBptr->feuille._VECTptr;
      int nder;
      gen fonction;
      if (!find_n_derivatives_function(w[0],x,nder,fonction))
	return gensizeerr(contextptr);
      if (fonction==y){
	if ( (w[1].type==_VECT) && (!w[1]._VECTptr->empty()))
	  yvar.push_back(makevecteur(*it,nder,w[1]._VECTptr->front()));
	else
	  yvar.push_back(makevecteur(*it,nder,w[1]));
      }
      if (nder>maxnder)
	maxnder=nder;
    }
    // compute all derivatives of the general solution
    vecteur derivatives(1,solution_generale);
    gen current=solution_generale;
    for (int i=1;i<=maxnder;++i){
      current=derive(current,x,contextptr);
      derivatives.push_back(current);
    }
    // evaluate at points of yvar making substition vectors
    it=yvar.begin(),itend=yvar.end();
    vecteur substin,substout;
    for (;it!=itend;++it){
      vecteur & w=*it->_VECTptr;
      substin.push_back(w[0]);
      substout.push_back(subst(derivatives[w[1].val],x,w[2],false,contextptr));
    }
    // replace in conditions
    conditions=*eval(subst(conditions,substin,substout,false,contextptr),eval_level(contextptr),contextptr)._VECTptr;
    // solve system over _c0..._cn-1
    bool save_xcas_mode=xcas_mode(contextptr);
    xcas_mode(contextptr)=0;
    bool save_calc_mode=calc_mode(contextptr);
    calc_mode(contextptr)=0;
    vecteur parameters_solutions=*_solve(gen(makevecteur(conditions,parameters),_SEQ__VECT),contextptr)._VECTptr;
    xcas_mode(contextptr)=save_xcas_mode;
    calc_mode(contextptr)=save_calc_mode;
    // replace _c0..._cn-1 in solution_generale
    it=parameters_solutions.begin(),itend=parameters_solutions.end();
    vecteur res;
    for (;it!=itend;++it){
      res.push_back(eval(subst(solution_generale,parameters,*it,false,contextptr),eval_level(contextptr),contextptr));
    }
    if (res.size()==1) 
      return res.front();
    return res;
  }
  static gen desolve_with_conditions(const vecteur & v,const gen & x,const gen & y,GIAC_CONTEXT){
    if (v.empty())
      return gensizeerr(contextptr);
    int ordre;
    vecteur parameters;
    gen solution_generale(desolve(v.front(),x,y,ordre,parameters,contextptr));
    if (solution_generale.type!=_VECT) 
      return in_desolve_with_conditions(v,x,y,solution_generale,parameters,contextptr);
    const_iterateur it=solution_generale._VECTptr->begin(),itend=solution_generale._VECTptr->end();
    vecteur res;
    res.reserve(itend-it);
    for (;it!=itend;++it){
      if (it->type==_VECT)
	*logptr(contextptr) << gettext("Boundary conditions for parametric curve not implemented") << endl;
      gen tmp=in_desolve_with_conditions(v,x,y,*it,parameters,contextptr);
      if (is_undef(tmp))
	return tmp;
      if (tmp.type==_VECT)
	res=mergevecteur(res,*tmp._VECTptr);
      else
	res.push_back(tmp);
    }
    return res;
  }

  // f must be a vector obtained using factors
  // x, y are 2 idnt
  // xfact and yfact should be initialized to 1
  // return true if f=xfact*yfact where xfact depends on x and yfact on y only
  bool separate_variables(const gen & f,const gen & x,const gen & y,gen & xfact,gen & yfact,GIAC_CONTEXT){
    const_iterateur jt=f._VECTptr->begin(),jtend=f._VECTptr->end();
    for (;jt!=jtend;jt+=2){
      vecteur tmp(*_lname(*jt,contextptr)._VECTptr);
      if (equalposcomp(tmp,y)){
	if (equalposcomp(tmp,x))
	  return false;
	yfact=yfact*pow(*jt,*(jt+1),contextptr);
      }
      else
	xfact=xfact*pow(*jt,*(jt+1),contextptr);
    }
    return true;
  }

  void ggb_varxy(const gen & f_orig,gen & vx,gen & vy,GIAC_CONTEXT){
    vecteur lv=lidnt(f_orig);
    vx=vx_var;
    vy=y__IDNT_e;
    if (calc_mode(contextptr)==1){
      vx=gen("ggbtmpvarx",contextptr);
      vy=gen("ggbtmpvary",contextptr);
    }
    for (unsigned i=0;i<lv.size();++i){
      string s=lv[i].print(contextptr);
      char c=s[s.size()-1];
      if (c=='x')
	vx=lv[i];
      if (c=='y')
	vy=lv[i];
    }
  }

  gen desolve(const gen & f_orig,const gen & x_orig,const gen & y_orig,int & ordre,vecteur & parameters,GIAC_CONTEXT){
    gen x(x_orig);
    if ( (x_orig.type==_VECT) && (x_orig._VECTptr->size()==1) )
      x=x_orig._VECTptr->front();
    if (x.type!=_IDNT){
      gen vx,vy;
      ggb_varxy(f_orig,vx,vy,contextptr);
      if (x_orig.type==_VECT)
	return desolve_with_conditions(makevecteur(f_orig,x_orig,y_orig),vx,vy,contextptr);
      else
	return desolve_with_conditions(makevecteur(f_orig,makevecteur(x_orig,y_orig)),vx,vy,contextptr);
    }
    if (y_orig.type==_VECT) // FIXME: differential system
      return gensizeerr(contextptr);
    gen f(remove_and(f_orig,at_and));
    if (f.type==_VECT)
      return desolve_with_conditions(*f._VECTptr,x,y_orig,contextptr);
    parameters.clear();
    gen y(y_orig),yof(y_orig);
    if (y_orig.type==_IDNT){
      yof=symb_of(y,gen(vecteur(1,x),_SEQ__VECT));
      f=quotesubst(f,yof,y,contextptr);
      f=quotesubst(f,y,yof,contextptr);
    }
    else 
      y=function_of(y_orig,x);
    if (is_undef(y))
      return y;
    gen save_vx=vx_var;
    vx_var=x;
    int save=calc_mode(contextptr);
    calc_mode(0,contextptr);
    f=remove_equal(eval(f,eval_level(contextptr),contextptr));
    calc_mode(save,contextptr);
    f=quotesubst(f,yof,y,contextptr);
    vx_var=save_vx;
    // Here f= f(derive(y,x),y) for a 1st order equation
    int n=diffeq_order(f,y);
    if (n<=0)
      return gensizeerr(contextptr);
    vecteur v;
#ifdef GIAC_HAS_STO_38
    identificateur t("t");
#else
    identificateur t(" t");
#endif
    if (is_linear_diffeq(f,x,y,n,v,contextptr)){
      // 1st order
      if (n==1){ // a(x)*y'+b(x)*y+c(x)=0
	// y'/y=-b/a -> y=C(x)exp(-int(b/a)) and a(x)*C'*exp()+c(x)=0
	gen & a=v[0];
	gen & b=v[1];
	gen & c=v[2];
	gen i=simplify(exp(integrate_without_lnabs(rdiv(b,a,contextptr),x,contextptr),contextptr),contextptr);
	gen C=integrate_without_lnabs(ratnormal(rdiv(-c,a,contextptr)*i),x,contextptr);
	parameters.push_back(diffeq_constante(0,contextptr));
	return ratnormal(_lin((C+parameters.front())/i,contextptr));
      }
      // cst coeff?
      gen cst=v.back();
      v.pop_back();
      if (derive(v,x,contextptr)==vecteur(n+1,zero)){
	// Yes!
	gen laplace_cst=laplace(-cst,x,t,contextptr);
	if (is_undef(laplace_cst))
	  return laplace_cst;
	gen arbitrary,tmp;
	for (int i=n-1;i>=0;--i){
	  parameters.push_back(diffeq_constante(n-1-i,contextptr));
	  tmp=tmp*t+parameters.back();
	  arbitrary=arbitrary+v[i]*tmp;
	}
	arbitrary=(laplace_cst+arbitrary)/symb_horner(v,t);
	arbitrary=ilaplace(arbitrary,t,x,contextptr);
	return arbitrary;
      }
    }
    vecteur substin(n);
    vecteur substout(n);
    for (int i=0;i<n;++i){
      substin[i]=symb_derive(y,x,i+1);
      substout[i]=identificateur(" y"+print_INT_(i));
    }
    gen ff=quotesubst(f,substin,substout,contextptr);
    if (is_zero(derive(ff,y,contextptr))){ // y incomplete
      for (int i=0;i<n;++i){
	substout[i]=symb_derive(y,x,i);
      }
      f=quotesubst(f,substin,substout,contextptr);
      int tmp;
      gen sol=desolve(f,x,y,tmp,parameters,contextptr);
      if (is_undef(sol)) return sol;
      parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
      gen p(parameters.back());
      if (sol.type==_VECT)
	p=vecteur(sol._VECTptr->size(),p);
      sol=integrate_without_lnabs(sol,x,contextptr)+p;
      return sol;
    }
    if (n==1) { // 1st order 
      vecteur sol;
      parameters.push_back(diffeq_constante(0,contextptr));
      f=quotesubst(f,symb_derive(y,x),t,contextptr);
      // f is an expression of x,y,t where t stands for y'
      gen fa,fb,fc,fd,faa,fab;
      // Test for Clairault-like eqdiff
      if (x.type==_IDNT && y.type==_IDNT && is_linear_wrt(f,y,fc,fd,contextptr) && is_linear_wrt(fd,x,fa,fb,contextptr)){
	// fa must be cst*t and fc must be cst
	if (is_linear_wrt(fa,t,faa,fab,contextptr) && is_zero(fab) && derive(faa,makevecteur(x,y,t),contextptr)==vecteur(3,0) && derive(fc,makevecteur(x,y,t),contextptr)==vecteur(3,0) && derive(fb,makevecteur(x,y),contextptr)==vecteur(2,0)){
	  // 0=f=fc*y+fd = fc*y+fa*x+fb = fc*y+faa*x*y'+fb
	  // -> y=-faa/fc*x*y' -fb/fc
	  if (is_one(ratnormal(-faa/fc))){
	    // y=x*y'-fb/fc
	    gen fm=ratnormal(-fb/fc);
	    gen fmp=derive(fm,t,contextptr);
	    sol.push_back(parameters.back()*x+subst(fm,t,parameters.back(),false,contextptr));
	    sol.push_back(makevecteur(-fmp,-t*fmp+fm));
	    return sol;
	  }
	}
      }
      vecteur v(solve(f,t,3,contextptr)); // now solve y'=v[i](y)
      const_iterateur it=v.begin(),itend=v.end();
      for (;it!=itend;++it){
	// Separable variables?
	f=factors(*it,x,contextptr); // Factor then split factors
        gen xfact(plus_one),yfact(plus_one);
	if (separate_variables(f,x,y,xfact,yfact,contextptr)){ // y'/yfact=xfact
	  gen pr=parameters.back()+integrate_without_lnabs(inv(yfact,contextptr),y,contextptr);
	  sol=mergevecteur(sol,solve(pr-integrate_without_lnabs(xfact,x,contextptr),*y._IDNTptr,3,contextptr));
	  continue;
	}
	if (is_zero(derive(*it,x,contextptr))){ // x incomplete
	  if (debug_infolevel)
	    *logptr(contextptr) << gettext("Incomplete") << endl;
	  gen pr=integrate_without_lnabs(inv(*it,contextptr),y,contextptr)+parameters.back();
	  sol=mergevecteur(sol,solve(pr-x,*y._IDNTptr,3,contextptr));
	  continue;
	}
	// homogeneous?
	gen tplus(t);
	gen tmpsto=sto(doubleassume_and(vecteur(2,0),0,1,false,contextptr),tplus,contextptr);
	if (is_undef(tmpsto))
	  return tmpsto;
	f=quotesubst(*it,makevecteur(x,y),makevecteur(tplus*x,tplus*y),contextptr);
	f=normal(f-*it,contextptr);
	if (is_zero(f)){
	  *logptr(contextptr) << gettext("Homogeneous") << endl;
	  tmpsto=sto(doubleassume_and(vecteur(2,0),0,1,false,contextptr),x,contextptr);
	  if (is_undef(tmpsto))
	    return tmpsto;
	  f=normal(quotesubst(*it,y,tplus*x,contextptr)-tplus,contextptr);
	  _purge(x,contextptr);
	  // y=tx -> t'x=f
	  // Singular solutions f(t)=0
	  vecteur singuliere(multvecteur(x,solve(f,t,3,contextptr)));
	  sol=mergevecteur(sol,singuliere);
	  // Non singular: t'/f(t)=1/x
	  gen pr=parameters.back()*_simplify(exp(integrate_without_lnabs(inv(f,contextptr),t,contextptr),contextptr),contextptr);
	  sol.push_back(gen(makevecteur(pr,t*pr),_CURVE__VECT));
	  continue;
	}
	// exact? y'=*it=f(x,y) -> N dy + M dx=0 where -M/N=y'
	gen M,N;
	f=_fxnd(*it,contextptr);
	M=-f[0];
	N=f[1];
	// find an integrating factor P such that d_x(P*N)=d_y(P*M)
	// If P depends on x then N*d_x(P)+Pd_x(N)=Pd_y(M) -> 
	// d_x(P)/P=(d_y(M)-d_x(N))/N should depend on x only
	// If P depends on y then P d_x(N)=Pd_y(M)+Md_y(P)
	// d_y(P)/P=(d_x(N)-d_y(M))/M
	// Then solve P*Ndy+P*Mdx=dF
	f=normal((derive(M,y,contextptr)-derive(N,x,contextptr))/N,contextptr);
	if (is_zero(derive(f,y,contextptr))){
	  gen P=simplify(exp(integrate_without_lnabs(f,x,contextptr),contextptr),contextptr);
	  // D_y(F)=P*N
	  gen F=P*integrate_without_lnabs(N,y,contextptr);
	  // D_x(F)=P*M
	  parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	  F=F+integrate_without_lnabs(normal(P*M-derive(F,x,contextptr),contextptr),x,contextptr)+parameters.back();
	  sol=mergevecteur(sol,solve(F,*y._IDNTptr,3,contextptr));
	  continue;
	}
	f=normal((derive(N,x,contextptr)-derive(M,y,contextptr))/M,contextptr);
	if (is_zero(derive(f,x,contextptr))){
	  gen P=simplify(exp(integrate_without_lnabs(f,y,contextptr),contextptr),contextptr);
	  gen F=P*integrate_without_lnabs(M,x,contextptr);
	  // D_y(F)=P*N
	  F=F+integrate_without_lnabs(normal(P*N-derive(F,y,contextptr),contextptr),y,contextptr)+diffeq_constante(0,contextptr);
	  sol=mergevecteur(sol,solve(F,*y._IDNTptr,3,contextptr));
	  continue;
	}
	// Bernoulli?
	// y'=a(x)*y+b(x)*y^k
	// Let z=y^(1-k)
	// z'=(1-k)*y^(-k)*y'=(1-k)*[a(x)*z+b(x)]
	// Solve for z then for y
	f=subst(*it,y,2*y,false,contextptr);
	f=factors(f-2*(*it),vx_var,contextptr); // should be (2^k-2)*b(x)*y^k
	xfact=plus_one;
	yfact=plus_one;
	if (separate_variables(f,x,y,xfact,yfact,contextptr)){
	  // xfact should be (2^k-2)*b(x) and yfact=y^k
	  if ( (yfact.type==_SYMB) && (yfact._SYMBptr->sommet==at_pow) &&
	       (yfact._SYMBptr->feuille._VECTptr->front()==y) ){
	    gen k=yfact._SYMBptr->feuille._VECTptr->back();
	    gen B=normal(xfact/(pow(plus_two,k,contextptr)-plus_two),contextptr);
	    gen A=normal((*it-B*pow(y,k,contextptr))/y,contextptr);
	    gen b=(k-1)*A;
	    gen c=(k-1)*B;
	    gen i=simplify(integrate_without_lnabs(b,x,contextptr),contextptr);
	    gen C=integrate_without_lnabs(-c*exp(i,contextptr),x,contextptr);
	    f= (C+parameters.front())*exp(-i,contextptr);
	    gen sol1=pow(f,inv(1-k,contextptr),contextptr);
	    sol.push_back(sol1);
	    // FIXME: we should add other roots of unity in complex mode
	    if (k.type==_INT_ && k.val %2)
	      sol.push_back(-sol1);
	  }
	}
      }
      return sol;
    }
    return unable_to_solve_diffeq();
  }
  gen ggbputinlist(const gen & g,GIAC_CONTEXT){
    if (g.type==_VECT || calc_mode(contextptr)!=1)
      return g;
    return makevecteur(g);
  }
  // "unary" version
  gen _desolve(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    int ordre;
    vecteur parameters;
    if (args.type!=_VECT || (!args._VECTptr->empty() && args._VECTptr->back().is_symb_of_sommet(at_equal)) ){
      // guess x and y
      vecteur lv(lop(args,at_of));
      vecteur f;
      if (lv.size()>=1 && lv[0]._SYMBptr->feuille.type==_VECT && (f=*lv[0]._SYMBptr->feuille._VECTptr).size()==2)
	return desolve(args,f[1],f[0],ordre,parameters,contextptr);
      gen vx,vy;
      ggb_varxy(args,vx,vy,contextptr);
      return _desolve(makesequence(args,vx,vy),contextptr);
    }
    vecteur v=*args._VECTptr;
    int s=v.size();
    if (s==3 && v[1].type==_VECT && v[2].type==_VECT)
      swapgen(v[1],v[2]);
    if (s==2 && v[1].type==_VECT && v[1]._VECTptr->size()==2){
      gen a=v[1]._VECTptr->front();
      gen b=v[1]._VECTptr->back();
      v[1]=a;
      v.insert(v.begin()+2,b);
      ++s;
    }
    if (s==2){
      if ( (v[1].type==_SYMB && v[1]._SYMBptr->sommet==at_of && v[1]._SYMBptr->feuille.type==_VECT &&v [1]._SYMBptr->feuille._VECTptr->size()==2 ) )
	return desolve(v[0],(*v[1]._SYMBptr->feuille._VECTptr)[1],(*v[1]._SYMBptr->feuille._VECTptr)[0],ordre,parameters,contextptr);
      return ggbputinlist(desolve( v[0],vx_var,v[1],ordre,parameters,contextptr),contextptr);
    }
    if (s==4)
      return ggbputinlist(desolve_with_conditions(makevecteur(v[0],v[3]),v[1],v[2],contextptr),contextptr);
    if (s==5)
      return ggbputinlist(desolve_with_conditions(makevecteur(v[0],v[3],v[4]),v[1],v[2],contextptr),contextptr);
    if (s!=3)
      return gensizeerr(contextptr);
    return ggbputinlist(desolve( v[0],v[1],v[2],ordre,parameters,contextptr),contextptr);    
  }
  static const char _desolve_s []="desolve";
  static define_unary_function_eval (__desolve,&_desolve,_desolve_s);
  define_unary_function_ptr5( at_desolve ,alias_at_desolve,&__desolve,1,true);

  static const char _dsolve_s []="dsolve";
  static define_unary_function_eval_quoted (__dsolve,&_desolve,_dsolve_s);
  define_unary_function_ptr5( at_dsolve ,alias_at_dsolve,&__dsolve,_QUOTE_ARGUMENTS,true);

  gen ztrans(const gen & f,const gen & x,const gen & s,GIAC_CONTEXT){
    if (x.type!=_IDNT)
      return gensizeerr(contextptr);
    gen t(s);
    if (s==x){
#ifdef GIAC_HAS_STO_38
      t=identificateur("z");
#else
      t=identificateur(" tztrans");
#endif
    }
    if (!assume_t_in_ab(t,plus_inf,plus_inf,true,true,contextptr))
      return gensizeerr(contextptr);
    gen tmp=expand(f*pow(t,-x,contextptr),contextptr);
    gen res=_sum(gen(makevecteur(tmp,x,0,plus_inf),_SEQ__VECT),contextptr);
    _purge(t,contextptr);
    if (s==x)
      res=subst(res,t,x,false,contextptr);
    return ratnormal(res);
  }

  // "unary" version
  gen _ztrans(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return ztrans(args,vx_var,vx_var,contextptr);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s==2)
      return ztrans( v[0],v[1],v[1],contextptr);
    if (s!=3)
      return gensizeerr(contextptr);
    return ztrans( v[0],v[1],v[2],contextptr);    
  }
  static const char _ztrans_s []="ztrans";
  static define_unary_function_eval (__ztrans,&_ztrans,_ztrans_s);
  define_unary_function_ptr5( at_ztrans ,alias_at_ztrans,&__ztrans,0,true);

  static gen invztranserr(GIAC_CONTEXT){
    return gensizeerr(gettext("Inverse z-transform of non rational functions not implemented or unable to fully factor rational function"));
  }

  // limited to rational fractions
  gen invztrans(const gen & f,const gen & x,const gen & s,GIAC_CONTEXT){
    if (x.type!=_IDNT)
      return gensizeerr(contextptr);
    gen t(s);
    if (s==x){
#ifdef GIAC_HAS_STO_38
      t=identificateur("s");
#else
      t=identificateur(" tinvztrans");
#endif
    }
    vecteur varx(lvarx(f,x));
    int varxs=varx.size();
    gen res;
    if (varxs==0)
      res=f*symb_when(t,1,0);
    else {
      if (varxs>1)
	return invztranserr(contextptr);
      res=f/x;
      vecteur l;
      l.push_back(x); // insure x is the main var
      l.push_back(t); // s var as second var
      l=vecteur(1,l);
      alg_lvar(res,l);
      vecteur lprime(l);
      if (lprime.front().type!=_VECT)  return gensizeerr(gettext("desolve.cc/invztrans"));
      lprime.front()=cdr_VECT(*(lprime.front()._VECTptr));
      gen glap=e2r(s,l,contextptr);
      if (glap.type!=_POLY)  return gensizeerr(gettext("desolve.cc/invztrans"));
      int dim=l.front()._VECTptr->size();
      if (!dim){
	l.erase(l.begin());
	dim=l.front()._VECTptr->size();
      }
      gen r=e2r(res,l,contextptr);
      res=0;
      gen r_num,r_den;
      fxnd(r,r_num,r_den);
      if (r_num.type==_EXT)
	return invztranserr(contextptr);
      if (r_den.type!=_POLY)
	return invztranserr(contextptr);
      polynome den(*r_den._POLYptr),num(dim);
      if (r_num.type==_POLY)
	num=*r_num._POLYptr;
      else
	num=polynome(r_num,dim);
      polynome p_content(lgcd(den));
      den=den/p_content;
      factorization vden; gen an;
      gen extra_div;
      if (!cfactor(den,an,vden,true,extra_div))
	return invztranserr(contextptr);
      vector< pf<gen> > pfde_VECT;
      polynome ipnum(dim),ipden(dim);
      partfrac(num,den,vden,pfde_VECT,ipnum,ipden);
      if (!is_zero(ipnum))
	*logptr(contextptr) << gettext("Warning, z*argument has a non-zero integral part") << endl;
      vector< pf<gen> >::iterator it=pfde_VECT.begin();
      vector< pf<gen> >::const_iterator itend=pfde_VECT.end();
      gen a,A,B;
      polynome b,c;
      for (;it!=itend;++it){
	if (it->fact.lexsorted_degree()>1)
	  return invztranserr(contextptr);
	findde(it->fact,b,c);
	a=-gen(c)/gen(b); // pole
	B=r2e(Tfirstcoeff(it->den),l,contextptr);
	if (is_zero(a)){
	  int mult=it->mult;
	  gen res0;
	  vecteur vnum;
	  polynome2poly1(it->num,1,vnum);
	  for (int i=0;i<mult;++i){
	    res0 += r2e(vnum[i],lprime,contextptr)*symbolic(at_Dirac,s-i);
	  }
	  res += res0/B;
	}
	else {
	  // it->num/it->den in terms of 1/(z-a), a/(z-a)^2, a^2/(z-a)^3, etc.
	  gen cur=r2e(it->num,l,contextptr);
	  A=r2e(a,lprime,contextptr);
	  gen z_minus_a=x-A,res0;
	  for (int i=it->mult-1;i>=0;--i){
	    gen tmp=_quorem(makesequence(cur,z_minus_a,x),contextptr);
	    if (is_undef(tmp)) return tmp;
	    gen rem=tmp[1];
	    cur=tmp[0];
	    rem=rem/pow(A,i,contextptr)/factorial(i);
	    for (int j=0;j<i;++j)
	      rem = rem * (s-j);
	    res0 += rem;
	  }
	  res0 = res0 * pow(A,s,contextptr);
	  res += res0/B;
	}
      }
      res=res/r2e(p_content,l,contextptr);
    }
    if (s==x)
      res=subst(res,t,x,false,contextptr);
    return ratnormal(res);    
  }
  
  gen _invztrans(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return invztrans(args,vx_var,vx_var,contextptr);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s==2)
      return invztrans( v[0],v[1],v[1],contextptr);
    if (s!=3)
      return gensizeerr(contextptr);
    return invztrans( v[0],v[1],v[2],contextptr);        
  }
  static const char _invztrans_s []="invztrans";
  static define_unary_function_eval (__invztrans,&_invztrans,_invztrans_s);
  define_unary_function_ptr5( at_invztrans ,alias_at_invztrans,&__invztrans,0,true);


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
