/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c desolve.cc  -DHAVE_CONFIG_H -DIN_GIAC" -*- */
#include "giacPCH.h"
/*
 *  Copyright (C) 2000, 2014 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
    // workaround for desolve(diff(y)*sin(x)=y*ln(y),x,y);
    // otherwise it returns ln(-1-cos(x))
    bool save_cv=complex_variables(contextptr);
    complex_variables(false,contextptr);
    gen res=integrate_gen(e,x,contextptr);
    if (lop(res,at_abs).empty() && lop(res,at_floor).empty()){
      complex_variables(save_cv,contextptr);
      return res;
    }
    bool save_do_lnabs=do_lnabs(contextptr);
    do_lnabs(false,contextptr);
    res=integrate_gen(e,x,contextptr);
    do_lnabs(save_do_lnabs,contextptr);
    complex_variables(save_cv,contextptr);
    return res;
  }

  gen gen_t(const vecteur & v,GIAC_CONTEXT){
#ifdef GIAC_HAS_STO_38
    identificateur id_t("t38_");
#else
    identificateur id_t(" t");
#endif
    gen tmp_t,t=t__IDNT;
    t=t._IDNTptr->eval(1,tmp_t,contextptr);
    if (t!=t__IDNT || equalposcomp(lidnt(v),t__IDNT))
      t=id_t;
    return t;
  }

  gen laplace(const gen & f0,const gen & x,const gen & s,GIAC_CONTEXT){
    if (x.type!=_IDNT)
      return gensizeerr(contextptr);
    if (f0.type==_VECT){
      vecteur v=*f0._VECTptr;
      for (int i=0;i<int(v.size());++i){
	v[i]=laplace(v[i],x,s,contextptr);
      }
      return gen(v,f0.subtype);
    }
    gen t(s);
    if (s==x){
#ifdef GIAC_HAS_STO_38
      t=identificateur("s38_");
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
    purgenoassume(t,contextptr);
    if (s==x)
      res=subst(res,t,x,false,contextptr);
    return ratnormal(res,contextptr);
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
    
  static gen _laplace_(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT)
      return laplace(args,vx_var,vx_var,contextptr);
    vecteur & v=*args._VECTptr;
    int s=int(v.size());
    if (s==2)
      return laplace( v[0],v[1],v[1],contextptr);
    if (s!=3)
      return gensizeerr(contextptr);
    return laplace( v[0],v[1],v[2],contextptr);    
  }
  // "unary" version
  gen _laplace(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    bool b=approx_mode(contextptr);
    approx_mode(false,contextptr);
#ifndef NSPIRE
    my_ostream * ptr=logptr(contextptr);
    logptr(0,contextptr);
    gen res=_laplace_(args,contextptr);
    logptr(ptr,contextptr);
#else
    gen res=_laplace_(exact(args,contextptr),contextptr);
#endif
    approx_mode(b,contextptr);
    if (b || has_num_coeff(args))
      res=simplifier(evalf(res,1,contextptr),contextptr);
    return res;
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
      int varxs=int(varx.size());
      if (!varxs){ // Dirac function
	res += coeff*exp(expb,contextptr)*symbolic(at_Dirac,laplace_var+expa);
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
      int s=int(l.front()._VECTptr->size());
      if (!s){
	l.erase(l.begin());
	s=int(l.front()._VECTptr->size());
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
	    gen racine=ratnormal(sqrtdelta/gen(2)/r2e(a,lprime,contextptr),contextptr);
	    lnpart=lnpart+rdiv(r2e(d,lprime,contextptr),r2e(lnpartden,lprime,contextptr),contextptr)*cosh(racine*laplace_var,contextptr)*exppart;
	    gen aa=ratnormal(r2e(atannum,lprime,contextptr)/r2e(alpha,lprime,contextptr)/sqrtdelta,contextptr);
	    lnpart=lnpart+aa*sinh(racine*laplace_var,contextptr)*exppart;
	  }
	  else {
	    sqrtdelta=normal(sqrt(r2e(-delta,lprime,contextptr),contextptr),contextptr);
	    gen racine=ratnormal(sqrtdelta/gen(2)/r2e(a,lprime,contextptr),contextptr);
	    lnpart=lnpart+rdiv(r2e(d,lprime,contextptr),r2e(lnpartden,lprime,contextptr),contextptr)*cos(racine*laplace_var,contextptr)*exppart;
	    gen aa=ratnormal(r2e(atannum,lprime,contextptr)/r2e(alpha,lprime,contextptr)/sqrtdelta,contextptr);
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
      int nums=int(ipnumv.size());
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
    if (has_num_coeff(f))
      return ilaplace(exact(f,contextptr),x,s,contextptr);
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
    int s=int(v.size());
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
#if 0 // def NSPIRE
    if (i<5){
      const char * tab[]={"o","p","q","r","s"};
      return gen(tab[i],contextptr);
    }
#endif
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
  static bool is_linear_diffeq(const gen & f_orig,const gen & x,const gen & y,int order,vecteur & v,int step_info,GIAC_CONTEXT){
    v.clear();
    gen f(f_orig),a,b,cur_y(y);
    gen t=gen_t(makevecteur(x,y,f_orig),contextptr);
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
    if (step_info && v.size()>3)
      gprintf("Linear differential equation of coefficients %gen\nsecond member %gen",makevecteur(vecteur(v.begin(),v.end()-1),-v.back()),step_info,contextptr);
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

  static gen in_desolve_with_conditions(const vecteur & v_,const gen & x,const gen & y,const gen & solution_generale,const vecteur & parameters,const gen & f,int step_info,GIAC_CONTEXT){
    gen yy(y);
    vecteur v(v_);
    if (yy.type!=_IDNT)
      yy=function_of(y,x);
    if (is_undef(yy))
      return yy;
    // special handling for systems
    if (solution_generale.type==_VECT && v.size()==2){
      gen init=v[1],point=0;
      if (init.is_symb_of_sommet(at_equal) && init._SYMBptr->feuille.type==_VECT&& init._SYMBptr->feuille._VECTptr->size()>=2){
	point=(*init._SYMBptr->feuille._VECTptr)[0];
	init=(*init._SYMBptr->feuille._VECTptr)[1];
	if (!point.is_symb_of_sommet(at_of) || point._SYMBptr->feuille.type!=_VECT || point._SYMBptr->feuille._VECTptr->size()<2 || point._SYMBptr->feuille._VECTptr->front()!=y)
	  return gensizeerr("Bad initial condition");
	point=(*point._SYMBptr->feuille._VECTptr)[1];
      }
      gen systeme=subst(solution_generale,x,point,false,contextptr)-init;
      gen s=_solve(makesequence(systeme,parameters),contextptr);
      if (s.type!=_VECT)
	return gensizeerr("Bad initial condition");
      vecteur res;
      for (unsigned i=0;i<s._VECTptr->size();++i){
	gen tmp=subst(solution_generale,parameters,s[i],false,contextptr);
	tmp=ratnormal(tmp,contextptr);
	res.push_back(tmp);
      }
      return res;
    }
    if (solution_generale.type==_VECT)
      *logptr(contextptr) << gettext("Boundary conditions for parametric curve not implemented") << endl;
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
    int save_xcas_mode=xcas_mode(contextptr);
    xcas_mode(contextptr)=0;
    int save_calc_mode=calc_mode(contextptr);
    calc_mode(contextptr)=0;
    vecteur parameters_solutions=*_solve(gen(makevecteur(conditions,parameters),_SEQ__VECT),contextptr)._VECTptr;
    if (step_info)
      gprintf("General solution %gen\nSolving initial conditions\n%gen\nunknowns %gen\nSolutions %gen",makevecteur(solution_generale,conditions,parameters,parameters_solutions),step_info,contextptr);
    xcas_mode(contextptr)=save_xcas_mode;
    calc_mode(contextptr)=save_calc_mode;
    // replace _c0..._cn-1 in solution_generale
    it=parameters_solutions.begin(),itend=parameters_solutions.end();
    vecteur res;
    for (;it!=itend;++it){
      gen solgen=eval(subst(solution_generale,parameters,*it,false,contextptr),eval_level(contextptr),contextptr);
      // check if f is valid at points where conditions hold (3rd column of yvar)
      gen solgenchk=eval(subst(f,y,solgen,false,contextptr),1,contextptr);
      bool ok=true;
      for (unsigned i=0;i<yvar.size();++i){
	gen tmp=subst(solgenchk,x,yvar[i][2],false,contextptr);
	if (lidnt(tmp).empty() && !is_zero(simplify(tmp,contextptr))){
	  ok=false;
	  break;
	}
      }
      if (ok)
	res.push_back(solgen);
    }
    if (res.size()==1) 
      return res.front();
    return res;
  }

  static gen desolve_with_conditions(const vecteur & v,const gen & x,const gen & y,gen & f,int step_info,GIAC_CONTEXT){
    if (v.empty())
      return gensizeerr(contextptr);
    int ordre;
    vecteur parameters;
    gen solution_generale(desolve_f(v.front(),x,y,ordre,parameters,f,step_info,contextptr));
    if (solution_generale.type!=_VECT) 
      return in_desolve_with_conditions(v,x,y,solution_generale,parameters,f,step_info,contextptr);
    solution_generale.subtype=0; // otherwise desolve([y'=[[1,2],[2,1]]*y+[x,x+1],y(0)=[1,2]]) fails on the Prime (?)
    if (parameters.empty())
      return solution_generale;
    iterateur it=solution_generale._VECTptr->begin(),itend=solution_generale._VECTptr->end();
    vecteur res;
    res.reserve(itend-it);
    for (;it!=itend;++it){
      if (it->type==_VECT) it->subtype=0;
      gen tmp=in_desolve_with_conditions(v,x,y,*it,parameters,f,step_info,contextptr);
      if (is_undef(tmp))
	return tmp;
      if (tmp.type==_VECT)
	res=mergevecteur(res,*tmp._VECTptr);
      else
	res.push_back(tmp);
    }
    return res;
  }

  static gen desolve_with_conditions(const vecteur & v,const gen & x,const gen & y,gen & f,GIAC_CONTEXT){
    int st=step_infolevel(contextptr);
    step_infolevel(0,contextptr);
    gen res=desolve_with_conditions(v,x,y,f,st,contextptr);
    step_infolevel(st,contextptr);
    return res;
  }

  // f must be a vector obtained using factors
  // x, y are 2 idnt
  // xfact and yfact should be initialized to 1
  // return true if f=xfact*yfact where xfact depends on x and yfact on y only
  bool separate_variables(const gen & f,const gen & x,const gen & y,gen & xfact,gen & yfact,int step_info,GIAC_CONTEXT){
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
    if (step_info)
      gprintf("Separable variables d%gen/%gen=%gen*d%gen",makevecteur(y,yfact,xfact,x),step_info,contextptr);
    return true;
  }

  bool separate_variables(const gen & f,const gen & x,const gen & y,gen & xfact,gen & yfact,GIAC_CONTEXT){
    return separate_variables(f,x,y,xfact,yfact,step_infolevel(contextptr),contextptr);
  }

  void ggb_varxy(const gen & f_orig,gen & vx,gen & vy,GIAC_CONTEXT){
    vecteur lv=lidnt(f_orig);
    vx=vx_var;
    vy=y__IDNT_e;
#if 0
    if (calc_mode(contextptr)==1){
      vx=gen("ggbtmpvarx",contextptr);
      vy=gen("ggbtmpvary",contextptr);
    }
#endif
    for (unsigned i=0;i<lv.size();++i){
      string s=lv[i].print(contextptr);
      char c=s[s.size()-1];
      if (c=='x')
	vx=lv[i];
      if (c=='y')
	vy=lv[i];
    }
  }

  static gen desolve_cleanup(const gen & i,const gen & x,GIAC_CONTEXT){
    if (i.is_symb_of_sommet(at_prod)){
      gen f=i._SYMBptr->feuille;
      if (f.type==_VECT){
	vecteur w;
	for (int j=0;j<f._VECTptr->size();++j){
	  gen tmp=desolve_cleanup((*f._VECTptr)[j],x,contextptr);
	  if (!is_one(tmp))
	    w.push_back(tmp);
	}
	return _prod(w,contextptr);
      }
    }
    if (i.is_symb_of_sommet(at_abs) || i.is_symb_of_sommet(at_neg))
      return desolve_cleanup(i._SYMBptr->feuille,x,contextptr);
    if (is_zero(derive(i,x,contextptr)))
      return 1;
    return i;
  }

  // solve linear diff eq of order 1 a*y'+b*y+c=0
  static gen desolve_lin1(const gen &a,const gen &b,const gen & c,const gen & x,vecteur & parameters,int step_info,GIAC_CONTEXT){
    if (step_info)
      gprintf("Linear differential equation of order 1 a*y'+b*y+c=0\na=%gen, b=%gen, c=%gen",makevecteur(a,b,c),step_info,contextptr);
    if (a.type==_VECT){
      // y'+inv(a)*b(x)*y+inv(a)*c(x)=0
      // take laplace transform
      // p*Y-Y(0)+bsura*Y+csura=0
      // (p+bsura)*Y=Y(0)-csura
      int n=int(a._VECTptr->size());
      if (!ckmatrix(a) || !ckmatrix(b))
	return gensizeerr(contextptr);
      gen inva=inv(a,contextptr);
      gen bsura=inva*b,csura,cl;
      if (!is_zero(derive(bsura,x,contextptr)))
	return gensizeerr("Non constant linear differential system");
      if (c.type==_VECT){
	vecteur & cv=*c._VECTptr;
	for (unsigned i=0;i<cv.size();++i){
	  if (cv[i].type==_VECT && cv[i]._VECTptr->size()==1)
	    cv[i]=cv[i]._VECTptr->front();
	}
	csura=inva*c;
	cl=_laplace(makesequence(csura,x,x),contextptr);
      }
      else {
	if (!is_zero(c))
	  return gensizeerr("Invalid second member");
	cl=vecteur(n);
      }
      if (cl.type!=_VECT || int(cl._VECTptr->size())!=n)
	return gensizeerr("Invalid second member");	    
      for (int i=0;i<n;++i){
	parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	(*cl._VECTptr)[i] = parameters.back()- (*cl._VECTptr)[i];
      }
      cl=inv(bsura+x,contextptr)*cl;
      cl=ilaplace(cl,x,x,contextptr);
      return vecteur(1,ratnormal(cl,contextptr));
    }
    gen i=integrate_without_lnabs(rdiv(b,a,contextptr),x,contextptr);
    i=normal(lnexpand(i,contextptr),contextptr);
    i=exp(i,contextptr);
    if (step_info)
      gprintf("Homogeneous solution C/%gen",makevecteur(i),step_info,contextptr);
    i=expexpand(i,contextptr);
    i=simplify(i,contextptr);
    // cleanup general solution: remove cst factors and absolute values
    i=desolve_cleanup(i,x,contextptr);
    gen C=integrate_without_lnabs(ratnormal(rdiv(-c,a,contextptr)*i,contextptr),x,contextptr);
    if (step_info && C!=0)
      gprintf("Particuliar solution %gen",makevecteur(C),step_info,contextptr);
    parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
    gen res=ratnormal(_lin((C+parameters.back())/i,contextptr),contextptr);
    if (step_info)
      gprintf("General solution %gen",makevecteur(res),step_info,contextptr);
    return res;
  }

  bool desolve_linn(const gen & x,const gen & y,const gen & t,int n,vecteur & v,vecteur & parameters,gen & result,int step_info,GIAC_CONTEXT){
    // 1st order
    if (n==1){ // a(x)*y'+b(x)*y+c(x)=0
      // y'/y=-b/a -> y=C(x)exp(-int(b/a)) and a(x)*C'*exp()+c(x)=0
      gen & a=v[0];
      gen & b=v[1];
      gen & c=v[2];
      if (ckmatrix(a)){
	if (c.type!=_VECT && is_zero(c))
	  c=c*a;
	c=_tran(c,contextptr)[int(a._VECTptr->size())-1];
      }
      result=desolve_lin1(a,b,c,x,parameters,step_info,contextptr);
      return true;
    }
    // cst coeff?
    gen cst=v.back();
    v.pop_back();
    if (derive(v,x,contextptr)==vecteur(n+1,zero)){
      if (step_info)
	gprintf("Linear differential equation with constant coefficients\nOrder %gen, coefficients %gen",makevecteur(n,v),step_info,contextptr);
      // Yes!
      // simpler general solution for small order generic lin diffeq with cst coeff/squarefree case
      if (n<=3){
	vecteur rac=solve(horner(v,x,contextptr),x,1,contextptr);
	comprim(rac);
	if (n==2 && rac.size()==1){
	  parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	  parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	  gen sol = exp(rac.front()*x,contextptr)*(parameters[parameters.size()-2]*x+parameters.back());
	  if (step_info)
	    gprintf("Homogeneous solution %gen",makevecteur(sol),step_info,contextptr);
	  bool b=calc_mode(contextptr)==1;
	  if (b)
	    calc_mode(0,contextptr);
	  gen part=_integrate(makesequence(-cst/v.front()*exp(-rac.front()*x,contextptr),x),contextptr)*x+_integrate(makesequence(cst/v.front()*x*exp(-rac.front()*x,contextptr),x),contextptr);
	  if (step_info)
	    gprintf("Particuliar solution %gen",makevecteur(part),step_info,contextptr);
	  if (b)
	    calc_mode(1,contextptr);
	  part=simplify(part*exp(rac.front()*x,contextptr),contextptr);
	  result=sol+part;
	  if (step_info)
	    gprintf("General solution %gen",makevecteur(result),step_info,contextptr);
	  return true;
	}
	if (int(rac.size())==n){
	  gen sol; bool reel=true;
	  for (int j=0;j<n;){
	    if (j<n-1 && is_zero(ratnormal(rac[j]-conj(rac[j+1],contextptr),contextptr),contextptr)){
	      gen racr,raci;
	      reim(rac[j],racr,raci,contextptr);
	      if (is_strictly_positive(-raci,contextptr))
		raci=-raci;
	      parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	      parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	      sol += exp(racr*x,contextptr)*(parameters[parameters.size()-2]*cos(raci*x,contextptr)+parameters[parameters.size()-1]*sin(raci*x,contextptr));
	      j+=2;
	      continue;
	    }
	    if (reel && !is_zero(im(rac[j],contextptr)))
	      reel=false;
	    parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	    sol += parameters.back()*exp(rac[j]*x,contextptr);
	    j++;
	  }
	  if (step_info)
	    gprintf("Homogeneous solution %gen",makevecteur(sol),step_info,contextptr);
	  if (derive(cst,x,contextptr)==0 && !is_zero(v.back())){
	    result=sol-cst/v.back();
	    return true;
	  }
	  // variation des constantes
	  gen M_=_vandermonde(rac,contextptr),part=0;
	  if (ckmatrix(M_)){
	    matrice M=*M_._VECTptr;
	    vecteur c(n);
	    c[n-1]=-_trig2exp(cst,contextptr)/v.front();
	    c=linsolve(mtran(M),c,contextptr);
	    for (unsigned i=0;i<c.size();++i){
	      bool b=calc_mode(contextptr)==1;
	      if (b)
		calc_mode(0,contextptr);
	      gen tmp=_lin(c[i]*exp(-rac[i]*x,contextptr),contextptr);
	      tmp = _integrate(makesequence(tmp,x),contextptr);
	      part += _lin(tmp*exp(rac[i]*x,contextptr),contextptr);
	      if (b)
		calc_mode(1,contextptr);
	    }
	    if (reel && is_zero(im(cst,contextptr)))
	      part=re(part,contextptr);
	    //part=recursive_ratnormal(part,contextptr);
	    part=simplify(part,contextptr);
	  }
	  if (step_info)
	    gprintf("Particuliar solution %gen",makevecteur(part),step_info,contextptr);
	  result=sol+part;
	  return true;
	}
      } // end n<=3
      gen laplace_cst=_laplace(makesequence(-cst,x,t),contextptr);
      if (!is_undef(laplace_cst)){
	vecteur lopei=mergevecteur(lop(laplace_cst,at_Ei),lop(laplace_cst,at_integrate));
	if (lopei.empty()){
	  gen arbitrary,tmp;
	  for (int i=n-1;i>=0;--i){
	    parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	    tmp=tmp*t+parameters.back();
	    arbitrary=arbitrary+v[i]*tmp;
	  }
	  arbitrary=(laplace_cst+arbitrary)/symb_horner(v,t);
	  arbitrary=ilaplace(arbitrary,t,x,contextptr);
	  result=arbitrary;
	  return true;
	}
      }
    }
    if (n==2){ // a(x)*y''+b(x)*y'+c(x)*y+d(x)=0
      gen & a=v[0];
      gen & b=v[1];
      gen & c=v[2];
      gen & d=cst;
      gen u=-b/a,V=-c/a,w=-d/a,
	k=simplify(u*u/4-derive(u,x,contextptr)/2+V,contextptr);
      // y''=u*y'+V*y+w  (with u,V,w functions of x)
      // Pseudo-code from fhub on HP Museum Forum
      /* 
	 k:=u^2/4-u'/2+V 
	 if k==const or k*x^2=const then 
	 if k=const 
	 then s:=x; t:=e^(int(u,x)/2); 
	 else u:=u*x+1; k:=u^2/4+V*x^2; s:=ln(x); t:=x^(u/2); 
	 endif;
	 if k=0 then u:=t*s; V:=t; 
	 elseif k>0 then u:=t*e^(sqrt(k)*s); V:=t*e^(-sqrt(k)*s); 
	 else u:=t*cos(sqrt(-k)*s); V:=t*sin(sqrt(-k)*s); 
	 endif;
	 w:=w/(u*V'-V*u'); w:=V*int(u*w,x)-u*int(V*w,x);
	 solution: y=c1*u+c2*V+w 
	 endif
      */
      bool cst=is_zero(derive(k,x,contextptr));
      bool x2=is_zero(derive(ratnormal(u*x,contextptr),x,contextptr)) && is_zero(derive(ratnormal(v*x*x,contextptr),x,contextptr));
      if (cst || x2){
	gen s,t;
	if (cst){
	  s=x; 
	  t=simplify(exp(integrate_without_lnabs(u,x,contextptr)/2,contextptr),contextptr);
	}
	else {
	  u=u*x+1; 
	  u=simplify(u,contextptr);
	  k=simplify(u*u/4+V*x*x,contextptr); 
	  s=ln(x,contextptr); t=pow(x,u/2,contextptr);
	}
	if (is_zero(k)){
	  u=t*s; V=t;
	}
	else {
	  if (is_strictly_positive(-k,contextptr)){
	    gen tmp=sqrt(-k,contextptr)*s;
	    u=t*cos(tmp,contextptr); 
	    V=t*sin(tmp,contextptr);
	  }
	  else {
	    if (s.is_symb_of_sommet(at_ln)){
	      gen tmp=pow(s._SYMBptr->feuille,sqrt(k,contextptr),contextptr);
	      u=t*tmp;
	      V=t/tmp;
	    }
	    else {
	      gen tmp=sqrt(k,contextptr)*s;
	      u=t*exp(tmp,contextptr); 
	      V=t*exp(-tmp,contextptr); 
	    }
	  }
	}
	w=simplify(w/(u*derive(V,x,contextptr)-V*derive(u,x,contextptr)),contextptr); 
	w=V*integrate_without_lnabs(u*w,x,contextptr)-
	  u*integrate_without_lnabs(V*w,x,contextptr);
	parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));	
	result=w+parameters[parameters.size()-2]*u+parameters[parameters.size()-1]*V;
	return true;
      }
      // IMPROVE: if a, b, c are polynomials, search for a polynomial solution
      // of the homogeneous equation, if found we can solve the diffeq
      if (lvarxwithinv(makevecteur(a,b,c),x,contextptr)==vecteur(1,x)){
	vecteur l=vecteur(1,x);
	gen a0(a),b0(b);
	a=_coeff(makesequence(a,x),contextptr);
	b=_coeff(makesequence(b,x),contextptr);
	c=_coeff(makesequence(c,x),contextptr);
	if (a.type==_VECT && b.type==_VECT && c.type==_VECT){
	  int A=int(a._VECTptr->size())-1,B=int(b._VECTptr->size())-1,C=int(c._VECTptr->size())-1,N=-1;
	  if (C==B-1){
	    gen n=-c._VECTptr->front()/b._VECTptr->front();
	    if (n.type==_INT_ && n.val>N){
	      if (A-2<C || n==1)
		N=n.val;
	    }
	    if (A-2==C){
	      // a*n*(n-1)+b*n+c=a*n^2+(b-1)*n+c=0
	      gen aa=a._VECTptr->front(),bb=b._VECTptr->front()-1,cc=c._VECTptr->front();
	      gen delta=(sqrt(bb*bb-4*aa*cc,contextptr)+bb)/2;
	      if (delta.type==_INT_ && delta.val>N)
		N=delta.val;
	    }
	  }
	  if (A-2==B-1 && C<B-1){
	    gen n=-b._VECTptr->front()/a._VECTptr->front()+1;
	    if (n.type==_INT_ && n.val>N)
	      N=n.val;
	  }
	  if (C==A-2 && B-1<C){
	    gen delta=(1+sqrt(1+4*c._VECTptr->front()/a._VECTptr->front(),contextptr))/2;
	    if (delta.type==_INT_ && delta.val>N)
	      N=delta.val;
	  }
	  if (N>=0){
	    int nrows=giacmax(giacmax(B,C+1),N==1?0:A)+N;
	    // search a solution sum(y_k*x*k,k,0,N)
	    matrice m(nrows);
	    for (int i=0;i<nrows;++i)
	      m[i]=vecteur(N+1);
	    // a*y''
	    for (int i=0;i<a._VECTptr->size();++i){
	      int j=int(a._VECTptr->size())-i-1;
	      for (int k=2;k<=N;++k){
		(*m[j+k-2]._VECTptr)[k] += k*(k-1)*a[i];
	      }
	    }
	    // b*y'
	    for (int i=0;i<b._VECTptr->size();++i){
	      int j=int(b._VECTptr->size())-i-1;
	      for (int k=1;k<=N;++k){
		(*m[j+k-1]._VECTptr)[k] += k*b[i];
	      }
	    }
	    // c*y
	    for (int i=0;i<c._VECTptr->size();++i){
	      int j=int(c._VECTptr->size())-i-1;
	      for (int k=0;k<=N;++k){
		(*m[j+k]._VECTptr)[k] += c[i];
	      }
	    }
	    m=mker(m,contextptr);
	    if (!m.empty()){
	      gen sol=m.front();
	      if (sol.type==_VECT){
		vecteur v=*sol._VECTptr;
		reverse(v.begin(),v.end());
		sol=symb_horner(-v,x);
		*logptr(contextptr) << "Polynomial solution found " << sol << endl;
		// now solve equation a*y''+b*y'+c*y+d=0 with y=sol*z
		// a*sol*z''+(2*a*sol'+b*sol)*z'=d
		gen res=desolve_lin1(a0*sol,2*a0*derive(sol,x,contextptr)+b0*sol,d,x,parameters,step_info,contextptr);
		res=_integrate(makesequence(res,x),contextptr);
		parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
		res += parameters.back();
		res=res*sol;
		result=res;
		return true;
	      }
	    }
	  }
	}
      }
    } // end 2nd order eqdiff
    return false;
  }

  gen desolve_f(const gen & f_orig,const gen & x_orig,const gen & y_orig,int & ordre,vecteur & parameters,gen & fres,int step_info,GIAC_CONTEXT){
    // if x_orig.type==_VECT || y_orig.type==_VECT, they should be evaled
    if (x_orig.type!=_VECT && eval(x_orig,1,contextptr)!=x_orig)
      return gensizeerr("Independant variable assigned. Run purge("+x_orig.print(contextptr)+")\n");
    if (y_orig.type!=_VECT && eval(y_orig,1,contextptr)!=y_orig)
      return gensizeerr("Dependant variable assigned. Run purge("+y_orig.print(contextptr)+")\n");
    gen x(x_orig);
    if ( (x_orig.type==_VECT) && (x_orig._VECTptr->size()==1) )
      x=x_orig._VECTptr->front();
    if (x.type!=_IDNT){
      gen vx,vy;
      ggb_varxy(f_orig,vx,vy,contextptr);
      if (x_orig.type==_VECT)
	return desolve_with_conditions(makevecteur(f_orig,x_orig,y_orig),vx,vy,fres,step_info,contextptr);
      else
	return desolve_with_conditions(makevecteur(f_orig,makevecteur(x_orig,y_orig)),vx,vy,fres,step_info,contextptr);
    }
    if (y_orig.type==_VECT) // FIXME: differential system
      return gensizeerr(contextptr);
    gen f=remove_and(f_orig,at_and);
    if (f.type==_VECT){
      vecteur fv=*f._VECTptr;
      return desolve_with_conditions(fv,x,y_orig,fres,step_info,contextptr);
    }
    gen y(y_orig),yof(y_orig),partic(undef);
    if (y_orig.is_symb_of_sommet(at_equal)){
      // particular solution provided
      y=y_orig._SYMBptr->feuille[0];
      partic=eval(y_orig._SYMBptr->feuille[1],1,contextptr);
    }
    if (y.type==_IDNT){
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
    if (ckmatrix(f)){
      vecteur v = *f._VECTptr;
      for (int i=0;i<v.size();++i){
	v[i].subtype=0;
      }
      f=v;
    }
    calc_mode(save,contextptr);
    fres=f=quotesubst(f,yof,y,contextptr);
    vx_var=save_vx;
    // Here f= f(derive(y,x),y) for a 1st order equation
    int n=diffeq_order(f,y);
    if (n==0)
      return solve(f,y,0,contextptr);
    if (n<=0)
      return gensizeerr(contextptr);
    vecteur v;
    gen t=gen_t(makevecteur(x,y,f),contextptr);
    if (is_linear_diffeq(f,x,y,n,v,step_info,contextptr)){
      gen result;
      if (n>1 && !is_undef(partic)){
	// reduce order by one
	vecteur s(n,partic);
	for (int i=1;i<n;++i){
	  s[i]=derive(s[i-1],x,contextptr);
	}
	vecteur w(n+1);
	w[n]=v[n+1]; // cst coeff
	for (int l=0;l<n;++l){
	  gen tmp=0;
	  for (int j=0;j<=l;++j){
	    tmp += v[j]*comb(n-j,l-j)*s[l-j];
	  }
	  w[l]=tmp;
	}
	if (desolve_linn(x,y,t,n-1,w,parameters,result,step_info,contextptr)){
	  result=integrate_without_lnabs(result,x,contextptr);
	  parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	  result = partic*(result+parameters.back());
	  return result;
	}
      }
      if (desolve_linn(x,y,t,n,v,parameters,result,step_info,contextptr))
	return result;
    }
    vecteur substin(n);
    vecteur substout(n);
    for (int i=0;i<n;++i){
      substin[i]=symb_derive(y,x,i+1);
      substout[i]=identificateur(" y"+print_INT_(i));
    }
    gen ff=quotesubst(f,substin,substout,contextptr);
    if (is_zero(derive(ff,y,contextptr))){ // y incomplete
      if (step_info)
	gprintf("y-incomplete",vecteur(0),step_info,contextptr);
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
      parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
      f=quotesubst(f,symb_derive(y,x),t,contextptr);
      // f is an expression of x,y,t where t stands for y'
      gen fa,fb,fc,fd,faa,fab;
      // Test for Lagrange/Clairault-like eqdiff, 
      if (x.type==_IDNT && y.type==_IDNT && is_linear_wrt(f,y,fc,fd,contextptr) && is_linear_wrt(fd,x,fa,fb,contextptr)){
	// Clairault: fa must be cst*t and fc must be cst (must simplify fa and fc)
	// f=y*fc+(fa*x+fb)
	fd=gcd(fc,fa);
	fa=normal(fa/fd,contextptr); fb=normal(fb/fd,contextptr); fc=normal(fc/fd,contextptr); 	
	if (is_linear_wrt(fa,t,faa,fab,contextptr) && is_zero(fab) && derive(faa,makevecteur(x,y,t),contextptr)==vecteur(3,0) && derive(fc,makevecteur(x,y,t),contextptr)==vecteur(3,0) && derive(fb,makevecteur(x,y),contextptr)==vecteur(2,0)){
	  // 0=f=fc*y+fd = fc*y+fa*x+fb = fc*y+faa*x*y'+fb
	  // -> y=-faa/fc*x*y' -fb/fc
	  if (is_one(ratnormal(-faa/fc,contextptr))){
	    if (step_info)
	      gprintf("Order 1 Clairault differential equation",vecteur(0),step_info,contextptr);
	    // y=x*y'-fb/fc
	    gen fm=ratnormal(-fb/fc,contextptr);
	    gen fmp=derive(fm,t,contextptr);
	    sol.push_back(parameters.back()*x+subst(fm,t,parameters.back(),false,contextptr));
	    sol.push_back(makevecteur(-fmp,-t*fmp+fm));
	    return sol;
	  }
	}
	// Lagrange-> fa/fb/fc dependent de t uniquement, if fb==0 -> separate var or homogeneous
	if (is_zero(derive(makevecteur(fa,fb,fc),x,contextptr)) && !is_zero(fb)){
	  if (step_info)
	    gprintf("Order 1 Lagrange differential equation",vecteur(0),step_info,contextptr);
	  // y+fa/fc*x+fb/fc=0
	  fa=fa/fc; fb=fb/fc;
	  // y+fa*x+fb=0
	  // t=dy/dx, dy/dt=t*dx/dt => t*dx/dt+fa'*x+fb'+fa*dx/dt
	  // linear equation 1st order (fa+t)*dx/dt+fa'*x+fb'=0
	  gen res=desolve_lin1(fa+t,derive(fa,t,contextptr),derive(fb,t,contextptr),t,parameters,step_info,contextptr);
	  vecteur sing(solve(t+fa,t,3,contextptr));
	  for (int i=0;i<int(sing.size());++i){
	    sing[i]=subst(-fa*x-fb,t,sing[i],false,contextptr);
	  }
	  // should deparametrize like for homogeneous if possible
#ifdef NO_STDEXCEPT	  
	  vecteur newsol=solve(res-x,*t._IDNTptr,3,contextptr);
	  if (is_undef(newsol)){
	    newsol.clear();
	    *logptr(contextptr) << "Unable to solve implicit equation "<< res-x << "=0 in " << t << endl;
	  }
#else
	  vecteur newsol;
	  try {
	    newsol=solve(res-x,*t._IDNTptr,3,contextptr);
	  } catch(std::runtime_error & err){
	    newsol.clear();
	    *logptr(contextptr) << "Unable to solve implicit equation "<< res-x << "=0 in " << t << endl;
	  }
#endif
	  if (newsol.empty())
	    sing.push_back(makevecteur(res,-fa*res-fb));
	  else {
	    for (int i=0;i<int(newsol.size());++i){
	      sing.push_back(subst(-fa*x-fb,t,newsol[i],false,contextptr));
	    }
	  }
	  return sing;
	}
      } // end Lagrange-Clairault
      vecteur v(solve(f,t,3,contextptr)); // now solve y'=v[i](y)
      const_iterateur it=v.begin(),itend=v.end();
      for (;it!=itend;++it){
	// Separable variables?
	f=factors(*it,x,contextptr); // Factor then split factors
        gen xfact(plus_one),yfact(plus_one);
	if (separate_variables(f,x,y,xfact,yfact,step_info,contextptr)){ // y'/yfact=xfact
	  gen pr=integrate_without_lnabs(inv(yfact,contextptr),y,contextptr);
#if 1
	  vecteur prv=lop(lvarx(pr,y),at_ln);
	  gen pra,prb;
	  if (!prv.empty() && prv[0].is_symb_of_sommet(at_ln) && is_linear_wrt(pr,prv[0],pra,prb,contextptr)){
	    pr=_lncollect(pra*(symbolic(at_ln,parameters.back()*prv[0]._SYMBptr->feuille))+prb,contextptr);
	  }
	  else
	    pr=parameters.back()+pr;
#else	  
	  if (has_op(pr,*at_ln))
	    pr=_lncollect(pr,contextptr); // hack to solve y'=y*(1-y)
	  if (pr.is_symb_of_sommet(at_ln))
	    pr=symbolic(at_ln,parameters.back()*pr._SYMBptr->feuille);
	  else
	    pr=parameters.back()+pr;
#endif
	  gen implicitsol=pr-integrate_without_lnabs(xfact,x,contextptr);
#ifdef NO_STDEXCEPT	  
	  vecteur newsol=solve(implicitsol,*y._IDNTptr,3,contextptr);
	  if (is_undef(newsol)){
	    newsol.clear();
	    *logptr(contextptr) << "Unable to solve implicit equation "<< implicitsol << "=0 in " << y << endl;
	  }
#else
	  vecteur newsol;
	  int cm=calc_mode(contextptr);
	  calc_mode(0,contextptr);
	  try {
	    newsol=solve(implicitsol,*y._IDNTptr,3,contextptr);
	  } catch(std::runtime_error & err){
	    newsol.clear();
	    *logptr(contextptr) << "Unable to solve implicit equation "<< implicitsol << "=0 in " << y << endl;
	  }
	  calc_mode(cm,contextptr);
#endif
	  sol=mergevecteur(sol,newsol);
	  continue;
	} // end separate variables
	if (is_zero(derive(*it,x,contextptr))){ // x incomplete
	  if (step_info)
	    gprintf("Order 1 x-incomplete differential equation",vecteur(0),step_info,contextptr);
	  if (debug_infolevel)
	    *logptr(contextptr) << gettext("Incomplete") << endl;
	  gen pr=integrate_without_lnabs(inv(*it,contextptr),y,contextptr)+parameters.back();
	  sol=mergevecteur(sol,solve(pr-x,*y._IDNTptr,3,contextptr));
	  continue;
	}
	// check for a linear substitution -> like an x incomplete
	fa=derive(*it,x,contextptr); fb=derive(*it,y,contextptr);
	fc=simplify(fa/fb,contextptr);
	if (is_zero(derive(fc,x,contextptr)) && is_zero(derive(fc,y,contextptr))){
	  gen eff=subst(*it,y,y-fc*x,false,contextptr); // does not depend on x
	  gen pr=integrate_without_lnabs(inv(eff+fc,contextptr),y,contextptr)+parameters.back();
	  pr=subst(pr,y,y+fc*x,false,contextptr);
	  vecteur l1=lop(lvarx(pr,y),at_floor);
	  if (!l1.empty()){
	    vecteur l2(l1.size());
	    pr=subst(pr,l1,l2,false,contextptr);
	  }
	  vecteur sol1=solve(pr-x,*y._IDNTptr,3,contextptr);
	  sol=mergevecteur(sol,sol1);
	  continue;
	}
	// homogeneous?
	gen tplus(t);
	gen tmpsto=sto(doubleassume_and(vecteur(2,0),0,1,false,contextptr),tplus,contextptr);
	if (is_undef(tmpsto))
	  return tmpsto;
	f=quotesubst(*it,makevecteur(x,y),makevecteur(tplus*x,tplus*y),contextptr);
	f=recursive_normal(f-*it,contextptr);
	purgenoassume(tplus,contextptr);
	if (is_zero(f)){
	  if (step_info)
	    gprintf("Order 1 Homogeneous differential equation",vecteur(0),step_info,contextptr);
	  if (debug_infolevel)
	    *logptr(contextptr) << gettext("Homogeneous differential equation") << endl;
	  tmpsto=sto(doubleassume_and(vecteur(2,0),0,1,false,contextptr),x,contextptr);
	  if (is_undef(tmpsto))
	    return tmpsto;
	  f=recursive_normal(quotesubst(*it,y,tplus*x,contextptr)-tplus,contextptr);
	  purgenoassume(x,contextptr);
	  // y=tx -> t'x=f
	  // Singular solutions f(t)=0
	  vecteur singuliere(multvecteur(x,solve(f,t,complex_mode(contextptr) + 2,contextptr)));
	  sol=mergevecteur(sol,singuliere);
	  // Non singular: t'/f(t)=1/x
	  gen pr=parameters.back()*_simplify(exp(integrate_without_lnabs(inv(f,contextptr),t,contextptr),contextptr),contextptr);
	  // Try to find t in x=pr
	  vecteur v=protect_solve(x-pr,*t._IDNTptr,1,contextptr);
	  if (!v.empty() && !is_undef(v)){
	    *logptr(contextptr) << "solve(" << pr << "=" << x << "," << t << ") returned " << v << ".\nIf solutions were missed consider paramplot(" << makevecteur(pr,t*pr) << "," << t << ")" << endl;
	    for (unsigned j=0;j<v.size();++j){
	      sol.push_back(x*v[j]);
	    }
	  }
	  else
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
	  if (step_info)
	    gprintf("Order 1 Integrating factor %gen",makevecteur(P),step_info,contextptr);
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
	  if (step_info)
	    gprintf("Order 1 Integrating factor %gen",makevecteur(P),step_info,contextptr);
	  F=F+integrate_without_lnabs(normal(P*N-derive(F,y,contextptr),contextptr),y,contextptr)+diffeq_constante(int(parameters.size()),contextptr);
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
	if (separate_variables(f,x,y,xfact,yfact,step_info,contextptr)){
	  // xfact should be (2^k-2)*b(x) and yfact=y^k
	  if ( (yfact.type==_SYMB) && (yfact._SYMBptr->sommet==at_pow) &&
	       (yfact._SYMBptr->feuille._VECTptr->front()==y) ){
	    if (step_info)
	      gprintf("Order 1 Bernoulli differential equation",vecteur(0),step_info,contextptr);
	    gen k=yfact._SYMBptr->feuille._VECTptr->back();
	    gen B=normal(xfact/(pow(plus_two,k,contextptr)-plus_two),contextptr);
	    gen A=normal((*it-B*pow(y,k,contextptr))/y,contextptr);
	    gen b=(k-1)*A;
	    gen c=(k-1)*B;
	    gen i=simplify(integrate_without_lnabs(b,x,contextptr),contextptr);
	    gen C=integrate_without_lnabs(-c*exp(i,contextptr),x,contextptr);
	    f= (C+parameters.back())*exp(-i,contextptr);
	    gen sol1=pow(f,inv(1-k,contextptr),contextptr);
	    sol.push_back(sol1);
	    // FIXME: we should add other roots of unity in complex mode
	    if (k.type==_INT_ && k.val %2)
	      sol.push_back(-sol1);
	  }
	}
	// Ricatti f=*it quadratic in y
	gen P,Q,R;
	if (is_quadratic_wrt(*it,y,R,Q,P,contextptr)){
	  if (step_info)
	    gprintf("Order 1 Riccati differential equation",vecteur(0),step_info,contextptr);
	  gen result;
	  // y'=P+Q*y+R*y^2=q0+q1*y+q2*y^2
	  if (!is_undef(partic)){
	    // z'+(q1+2*q2*partic)*z+q2=0
	    result=desolve_lin1(1,Q+2*R*partic,R,x,parameters,step_info,contextptr);
	    return makevecteur(partic,partic+inv(result,contextptr));
	  }
	  // let y=-1/(R*F)*dF/dx, then F''-(1/R*R'+Q)*F'+R*P*F=0
	  vecteur v(makevecteur(1,-normal(Q+derive(R,x,contextptr)/R,contextptr),normal(R*P,contextptr),0));
	  if (desolve_linn(x,y,t,2,v,parameters,result,step_info,contextptr)){
	    result=lnexpand(ln(result,contextptr),contextptr);
	    result=-derive(result,x,contextptr)/R;
	    result=ratnormal(result,contextptr);
	    gen lastp=parameters.back();
	    parameters.pop_back();
	    gen partic=subst(result,lastp,0,false,contextptr);
	    partic=ratnormal(partic,contextptr);
	    result=subst(result,lastp,1,false,contextptr);
	    result=ratnormal(result,contextptr);
	    //result=-derive(result,x,contextptr)/(R*result);
	    return makevecteur(partic,result);
	  }
	}
      } // end for (;it!=itend;)
      return sol;
    } // end if n==1
    if (n==2){
      // y''=f(y,y'), set u=y' -> u'=f(y,u)/u
      gen der1=substout[0],der2=substout[1];
      gen soly2=_cSolve(makesequence(symb_equal(ff,0),der2),contextptr);
      vecteur paramsave=parameters;
      if (soly2.type==_VECT && !is_undef(soly2)){
	vecteur sol;
	const vecteur & soly2v = *soly2._VECTptr;
	for (unsigned i=0;i<soly2v.size();++i){
	  gen soly2c=soly2v[i];
	  gen a,b,c;
	  if (is_quadratic_wrt(soly2c,der1,a,b,c,contextptr)
	      && is_zero(c) && is_zero(derive(a,x,contextptr)) 
	      && is_zero(derive(b,y,contextptr)) ){
	    parameters=paramsave;
	    parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	    gen usolj=parameters.back()*exp(integrate_without_lnabs(b,x,contextptr),contextptr)*exp(integrate_without_lnabs(a,y,contextptr),contextptr);
	    gen ysol=desolve(symb_equal(symbolic(at_derive,makesequence(y,x)),usolj),x,y,ordre,parameters,contextptr);
	    if (is_undef(ysol))
	      return unable_to_solve_diffeq();
	    sol=mergevecteur(sol,gen2vecteur(ysol));
	    continue;
	  }
	  if (is_zero(derive(soly2c,x,contextptr))){ // x-incomplete
	    if (step_info)
	      gprintf("Order 2 x-incomplete differential equation",vecteur(0),step_info,contextptr);
	    // desolve(u'=soly2c/der1,y,u)
	    parameters=paramsave;
	    gen usol=desolve(symb_equal(symbolic(at_derive,makesequence(der1,y)),soly2c/der1),y,der1,ordre,parameters,contextptr);
	    if (is_undef(usol))
	      return unable_to_solve_diffeq();
	    if (usol.type!=_VECT)
	      usol=vecteur(1,usol);
	    vecteur paramsavein=parameters;
	    for (unsigned j=0;j<usol._VECTptr->size();++j){
	      parameters=paramsavein;
	      gen usolj=(*usol._VECTptr)[j];
	      gen ysol=desolve(symb_equal(symbolic(at_derive,makesequence(y,x)),usolj),x,y,ordre,parameters,contextptr);
	      if (is_undef(ysol))
		return unable_to_solve_diffeq();
	      sol=mergevecteur(sol,gen2vecteur(ysol));
	    }
	    continue;
	  } // end x-incomplete
	  gen res(string2gen(gettext("Unable to solve differential equation"),false));
	  res.subtype=1;
	  sol.push_back(res);
	} 
	ordre=2;
	return sol;
      }
    }
    return unable_to_solve_diffeq();
  }
  gen ggbputinlist(const gen & g,GIAC_CONTEXT){
    if (g.type==_VECT || calc_mode(contextptr)!=1)
      return g;
    return makevecteur(g);
  }
  static gen point2vecteur(const gen & g_,GIAC_CONTEXT){
    if (!g_.is_symb_of_sommet(at_point))
      return g_;
    gen g=g_._SYMBptr->feuille;
    gen x,y;
    if (g.type==_VECT){
      if (g._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      x=g._VECTptr->front();
      y=g._VECTptr->back();
    }
    else
      reim(g,x,y,contextptr);
    g=makevecteur(x,y);
    return g;
  }
  // "unary" version
  gen _desolve(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    int ordre;
    vecteur parameters;
    if (args.type!=_VECT || args.subtype!=_SEQ__VECT || (!args._VECTptr->empty() && is_equal(args._VECTptr->back()) && args._VECTptr->back()._SYMBptr->feuille[0].type!=_IDNT)){
      // guess x and y
      vecteur lv(lop(args,at_of));
      vecteur f;
      if (lv.size()>=1 && lv[0]._SYMBptr->feuille.type==_VECT && (f=*lv[0]._SYMBptr->feuille._VECTptr).size()==2){
	if (f[1].type==_IDNT || f[1].is_symb_of_sommet(at_at)){
	  return desolve(args,f[1],f[0],ordre,parameters,contextptr);
	}
      }
      gen vx,vy;
      lv=lidnt(evalf(args,1,contextptr));
      if (lv.size()==2){
	vx=lv[0];
	vy=lv[1];
	lv=lvar(apply(args,equal2diff));
	lv=lop(lv,at_derive);
	lv=lidnt(lv);
	if (lv.size()==1 && vx==lv.front())
	  swapgen(vx,vy);
	return _desolve(makesequence(args,vx,vy),contextptr);
      }
      ggb_varxy(args,vx,vy,contextptr);
      return _desolve(makesequence(args,vx,vy),contextptr);
    }
    vecteur v=*args._VECTptr;
    int s=int(v.size());
    for (int i=0;i<s;++i){
      v[i]=apply(v[i],point2vecteur,contextptr);
    }
    if (s==3 && v[1].type==_VECT && v[2].type==_VECT)
      swapgen(v[1],v[2]);
    if (s==2 && v[1].type==_VECT && v[1]._VECTptr->size()==2){
      gen a=eval(v[1]._VECTptr->front(),1,contextptr);
      gen b=eval(v[1]._VECTptr->back(),1,contextptr);
      v[1]=a;
      v.insert(v.begin()+2,b);
      ++s;
    }
    if (s==2){
      if ( (v[1].type==_SYMB && v[1]._SYMBptr->sommet==at_of && v[1]._SYMBptr->feuille.type==_VECT &&v [1]._SYMBptr->feuille._VECTptr->size()==2 ) )
	return desolve(v[0],(*v[1]._SYMBptr->feuille._VECTptr)[1],(*v[1]._SYMBptr->feuille._VECTptr)[0],ordre,parameters,contextptr);
      return ggbputinlist(desolve( v[0],vx_var,v[1],ordre,parameters,contextptr),contextptr);
    }
    gen f;
    if (s==4)
      return ggbputinlist(desolve_with_conditions(makevecteur(v[0],v[3]),v[1],v[2],f,contextptr),contextptr);
    if (s==5)
      return ggbputinlist(desolve_with_conditions(makevecteur(v[0],v[3],v[4]),v[1],v[2],f,contextptr),contextptr);
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
      t=identificateur("z38_");
#else
      t=identificateur(" tztrans");
#endif
    }
    if (!assume_t_in_ab(t,plus_inf,plus_inf,true,true,contextptr))
      return gensizeerr(contextptr);
    gen tmp=expand(f*pow(t,-x,contextptr),contextptr);
    gen res=_sum(gen(makevecteur(tmp,x,0,plus_inf),_SEQ__VECT),contextptr);
    purgenoassume(t,contextptr);
    if (s==x)
      res=subst(res,t,x,false,contextptr);
    return ratnormal(res,contextptr);
  }

  gen desolve(const gen & f_orig,const gen & x_orig,const gen & y_orig,int & ordre,vecteur & parameters,GIAC_CONTEXT){
    gen f;
    gen x(x_orig),y(y_orig);
    if (x.is_symb_of_sommet(at_unquote))
      x=eval(x,1,contextptr);
    if (y.is_symb_of_sommet(at_unquote))
      y=eval(y,1,contextptr);
    int st=step_infolevel(contextptr);
    step_infolevel(0,contextptr);
    gen res=desolve_f(f_orig,x,y,ordre,parameters,f,st,contextptr);
    step_infolevel(st,contextptr);
    return res;
  }

  // "unary" version
  gen _ztrans(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return ztrans(args,vx_var,vx_var,contextptr);
    vecteur & v=*args._VECTptr;
    int s=int(v.size());
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
      t=identificateur("s38_");
#else
      t=identificateur(" tinvztrans");
#endif
    }
    vecteur varx(lvarx(f,x));
    int varxs=int(varx.size());
    gen res;
    if (varxs==0)
      res=f*_Kronecker(t,contextptr);
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
      int dim=int(l.front()._VECTptr->size());
      if (!dim){
	l.erase(l.begin());
	dim=int(l.front()._VECTptr->size());
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
	    res0 += r2e(vnum[i],lprime,contextptr)*symbolic(at_Kronecker,s-i); // symb_when(symb_equal(s,i),1,0) will not be handled correctly by ztrans
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
    res=ratnormal(res,contextptr);
    // replace discrete Kronecker by Heaviside in some very simple situations
    vecteur vD=lop(res,at_Kronecker);
    gen A,B,a,b;
    if (vD.size()==1 && is_linear_wrt(res,vD.front(),A,B,contextptr) && is_linear_wrt(vD.front()._SYMBptr->feuille,s,a,b,contextptr)){
      // res==A*Kronecker(a*x+b)+B
      if (is_one(a) && is_zero(b)){
	gen B0=subst(B,s,0,false,contextptr);
	if (is_zero(ratnormal(B0+A,contextptr)))
	  res=B*symbolic(at_Heaviside,s-1);
      }
    }
    return res;
  }
  
  gen _invztrans(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return invztrans(args,vx_var,vx_var,contextptr);
    vecteur & v=*args._VECTptr;
    int s=int(v.size());
    if (s==2)
      return invztrans( v[0],v[1],v[1],contextptr);
    if (s!=3)
      return gensizeerr(contextptr);
    return invztrans( v[0],v[1],v[2],contextptr);        
  }
  static const char _invztrans_s []="invztrans";
  static define_unary_function_eval (__invztrans,&_invztrans,_invztrans_s);
  define_unary_function_ptr5( at_invztrans ,alias_at_invztrans,&__invztrans,0,true);

  gen _Kronecker(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return args;
    if (args.type==_VECT) 
      return apply(args,_Kronecker,contextptr);
    if (!is_integer(args))
      return symbolic(at_Kronecker,args);
    if (is_zero(args))
      return 1;
    else
      return 0;
  }
  static const char _Kronecker_s []="Kronecker";
  static define_unary_function_eval (__Kronecker,&_Kronecker,_Kronecker_s);
  define_unary_function_ptr5( at_Kronecker ,alias_at_Kronecker,&__Kronecker,0,true);


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
