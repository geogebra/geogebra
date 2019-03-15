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
	    if (reel && is_zero(im(cst,contextptr)) && lop(part,at_integrate).empty())
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
      gen aa(a),bb(b),cc(c);
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
      } // end polynomial a,b,c
#ifndef USE_GMP_REPLACEMENTS
      a=aa; b=bb; c=cc;
      if (d==0 && lvarx(makevecteur(a,b,c),x,contextptr)==vecteur(1,x)){
	// if a,b,c are rationals and d==0, Kovacic
	gen k=_kovacicsols(makesequence(makevecteur(a,b,c),x),contextptr);
	if (k.type==_VECT && !k._VECTptr->empty()){
	  if (k._VECTptr->size()==2){
	    parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	    parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));	
	    result=parameters[parameters.size()-2]*k._VECTptr->front()+parameters[parameters.size()-1]*k._VECTptr->back();
	    return true;
	  }
	  if (k._VECTptr->size()==1){
	    // we have one solution Y, find an independent one as z*Y
	    gen Y=k._VECTptr->front();
	    // a*(zY)''+b*(zY)'+c*(zY)=0
	    // a*(z''Y+2z'Y')+b*(z'Y)=0
	    // z''*(aY)+z'*(2aY'+bY)=0
	    result=desolve_lin1(a*Y,2*a*derive(Y,x,contextptr)+b*Y,0,x,parameters,step_info,contextptr);
	    result=_integrate(makesequence(result,x),contextptr);
	    parameters.push_back(diffeq_constante(int(parameters.size()),contextptr));
	    result += parameters.back();
	    result = result*Y;
	    return true;
	  }
	}
      }
#endif
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
#ifdef GIAC_HAS_STO_38
    // HP Prime: if there is a M0-M9 identifier, this will not work
    vecteur fid(lidnt(f));
    for (unsigned i=0;i<fid.size();++i){
      gen fidi=fid[i];
      if (fidi.type==_IDNT){
	const char * ch=fidi._IDNTptr->id_name;
	if (strlen(ch)==2 && ch[0]=='M' && ch[1]>='0' && ch[1]<='9')
	  return gensizeerr("Home matrix variable "+ string(ch)+" not allowed. Store your matrix in a CAS variable first");
      }
    }
#endif
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
	    last_evaled_argptr(contextptr)=NULL;
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
	    last_evaled_argptr(contextptr)=NULL;
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

#ifndef USE_GMP_REPLACEMENTS
  // this code slice (c) 2018 Luka Marohnić 
  /* returns the coefficient of t in (fraction, Taylor or Laurent) expansion e */
  gen expansion_coeff(const gen &e,const gen &t,GIAC_CONTEXT) {
    gen ret(0),g;
    if (e.is_symb_of_sommet(at_plus) && e._SYMBptr->feuille.type==_VECT) {
      const vecteur &feu=*e._SYMBptr->feuille._VECTptr;
      for (const_iterateur it=feu.begin();it!=feu.end();++it) {
	g=_ratnormal(*it/t,contextptr);
	if (_evalf(g,contextptr).type==_DOUBLE_) {
	  ret=g;
	  break;
	}
      }
    } else {
      g=_ratnormal(e/t,contextptr);
      if (_evalf(g,contextptr).type==_DOUBLE_)
	ret=g;
    }
    return ret;
  }

  bool kovacic_iscase1(const vecteur &poles,int dinf) {
    if (dinf%2!=0 && dinf<=2)
      return false;
    for (const_iterateur it=poles.begin();it!=poles.end();++it) {
      int order=it->_VECTptr->back().val;
      if (order%2!=0 && order!=1)
	return false;
    }
    return true;
  }

  bool kovacic_iscase2(const vecteur &poles) {
    for (const_iterateur it=poles.begin();it!=poles.end();++it) {
      int order=it->_VECTptr->back().val;
      if (order==2 || (order>2 && order%2!=0))
	return true;
    }
    return false;
  }

  bool kovacic_iscase3(const gen &cpfr,const gen &x,const vecteur &poles,int dinf,GIAC_CONTEXT) {
    if (dinf<2)
      return false;
    vecteur alpha,beta;
    gen a,b,g(0),p;
    int d;
    for (const_iterateur it=poles.begin();it!=poles.end();++it) {
      p=it->_VECTptr->front();
      d=it->_VECTptr->back().val;
      if (d>2)
	return false;
      if (d>1) {
	alpha.push_back(a=expansion_coeff(cpfr,pow(x-p,-2),contextptr));
	g+=a;
	a=_eval(sqrt(1+4*a,contextptr),contextptr);
	if (!_numer(a,contextptr).is_integer() || !_denom(a,contextptr).is_integer())
	  return false;
      }
      beta.push_back(b=expansion_coeff(cpfr,_inv(x-p,contextptr),contextptr));
      g+=b*p;
    }
    g=_eval(sqrt(1+4*g,contextptr),contextptr);
    return is_zero(_ratnormal(_sum(beta,contextptr),contextptr)) &&
      _numer(g,contextptr).is_integer() && _denom(g,contextptr).is_integer();
  }

  void build_E_families(const gen_map &E,const vecteur &cv,vecteur &family,matrice &families) {
    int i=family.size();
    if (i>=int(cv.size()))
      return;
    const vecteur ev=*E.find(cv[i])->second._VECTptr;
    for (const_iterateur it=ev.begin();it!=ev.end();++it) {
      family.push_back(*it);
      if (family.size()==cv.size())
	families.push_back(family);
      else build_E_families(E,cv,family,families);
      family.pop_back();
    }
  }

  void create_identifiers(vecteur &vars,int n) {
    vars.reserve(n);
    stringstream ss;
    for (int i=0;i<n;++i) {
      ss.str("");
      ss << " cf" << i;
      vars.push_back(identificateur(ss.str().c_str()));
    }
  }

  gen strip_abs(const gen &g) {
    if (g.is_symb_of_sommet(at_abs))
      return g._SYMBptr->feuille;
    if (g.type==_SYMB) {
      gen args;
      if (g._SYMBptr->feuille.type==_VECT) {
	args=vecteur(0);
	const vecteur &feu=*g._SYMBptr->feuille._VECTptr;
	for (const_iterateur it=feu.begin();it!=feu.end();++it) {
	  args._VECTptr->push_back(strip_abs(*it));
	}
      } else args=strip_abs(g._SYMBptr->feuille);
      return symbolic(g._SYMBptr->sommet,args);
    }
    return g;
  }

  gen explnsimp(const gen &g,GIAC_CONTEXT) {
    gen e=expand(strip_abs(g),contextptr);
    e=symbolic(at_exp2pow,symbolic(at_expexpand,symbolic(at_lncollect,e)));
    return ratnormal(_lin(_eval(e,contextptr),contextptr),contextptr);
  }

  bool isroot(const gen &g,gen &deg,GIAC_CONTEXT) {
    if (!g.is_symb_of_sommet(at_pow))
      return false;
    const gen &pw=g._SYMBptr->feuille._VECTptr->at(1);
    return (deg=_inv(pw,contextptr)).is_integer() && !is_minus_one(deg);
  }

  void partialrad(const gen &g,const gen &deg,gen &outside,gen &inside,bool isdenom,GIAC_CONTEXT) {
    gen s;
    if (g.is_integer() && (s=_pow(makesequence(g,_inv(deg,contextptr)),contextptr)).is_integer()) {
      outside=outside*(isdenom?_inv(s,contextptr):s);
    } else if (g.is_symb_of_sommet(at_prod) && g._SYMBptr->feuille.type==_VECT) {
      vecteur &fv=*g._SYMBptr->feuille._VECTptr,qr;
      for (const_iterateur it=fv.begin();it!=fv.end();++it) {
	if (it->is_integer()) {
	  s=_pow(makesequence(*it,_inv(deg,contextptr)),contextptr);
	  outside=outside*(isdenom?_inv(s,contextptr):s);
	  continue;
	} else if (it->is_symb_of_sommet(at_pow)) {
	  const gen &pw=it->_SYMBptr->feuille._VECTptr->at(1);
	  const gen &b=it->_SYMBptr->feuille._VECTptr->front();
	  if (pw.is_integer()) {
	    qr=*_iquorem(makesequence(pw,deg),contextptr)._VECTptr;
	    if (isdenom) {
	      outside=outside/_pow(makesequence(b,qr.front()),contextptr);
	      inside=inside/_pow(makesequence(b,qr.back()),contextptr);
	    } else {
	      outside=outside*_pow(makesequence(b,qr.front()),contextptr);
	      inside=inside*_pow(makesequence(b,qr.back()),contextptr);
	    }
	    continue;
	  }
	} else if (it->is_symb_of_sommet(at_inv)) {
	  partialrad(it->_SYMBptr->feuille,deg,outside,inside,!isdenom,contextptr);
	  continue;
	}
	inside=inside*(isdenom?_inv(*it,contextptr):*it);
      }
    } else if (g.is_symb_of_sommet(at_inv))
      partialrad(g._SYMBptr->feuille,deg,outside,inside,!isdenom,contextptr);
    else inside=inside*(isdenom?_inv(g,contextptr):g);
  }

  gen radsimp(const gen &g,GIAC_CONTEXT) {
    gen deg;
    if (g.type==_VECT) {
      vecteur ret;
      for (const_iterateur it=g._VECTptr->begin();it!=g._VECTptr->end();++it) {
	ret.push_back(radsimp(*it,contextptr));
      }
      return change_subtype(ret,g.subtype);
    }
    if (isroot(g,deg,contextptr)) {
      gen radic=_collect(radsimp(g._SYMBptr->feuille._VECTptr->front(),contextptr),contextptr);
      gen inside(1),outside(1),inum;
      partialrad(radic,deg,outside,inside,false,contextptr);
      gen ideg=_inv(deg,contextptr);
      inside=_eval(symb_normal(inside),contextptr);
      if (!(inum=_eval(_pow(makesequence(_numer(inside,contextptr),ideg),contextptr),contextptr)).is_symb_of_sommet(at_pow) ||
	  inum._SYMBptr->feuille._VECTptr->at(1).is_integer())
	return _collect(outside,contextptr)*inum/_pow(makesequence(_collect(_denom(inside,contextptr),contextptr),ideg),contextptr);
      return _collect(outside,contextptr)*_pow(makesequence(_collect(inside,contextptr),ideg),contextptr);
    }
    if (g.is_symb_of_sommet(at_prod) && g._SYMBptr->feuille.type==_VECT) {
      vecteur &feu=*g._SYMBptr->feuille._VECTptr,degv;
      gen den(1);
      for (const_iterateur it=feu.begin();it!=feu.end();++it) {
	if (it->is_symb_of_sommet(at_pow)) {
	  gen dg=_inv(it->_SYMBptr->feuille._VECTptr->at(1),contextptr);
	  if (is_greater(dg,2,contextptr))
	    degv.push_back(dg);
	} else if (it->is_symb_of_sommet(at_inv))
	  den=den*it->_SYMBptr->feuille;
      }
      if (den.is_symb_of_sommet(at_prod) && den._SYMBptr->feuille.type==_VECT) {
	vecteur &dfeu=*den._SYMBptr->feuille._VECTptr;
	for (const_iterateur it=dfeu.begin();it!=dfeu.end();++it) {
	  if (it->is_symb_of_sommet(at_pow)) {
	    gen dg=_inv(it->_SYMBptr->feuille._VECTptr->at(1),contextptr);
	    if (is_greater(dg,2,contextptr))
	      degv.push_back(dg);
	  }
	}
      }
      gen gd=_lcm(degv,contextptr);
      gen p=_collect(ratnormal(pow(g,gd.val),contextptr),contextptr);
      if (p.is_symb_of_sommet(at_pow)) {
	gen ppw=p._SYMBptr->feuille._VECTptr->at(1);
	if (ppw.is_integer())
	  gd=_eval(gd/ppw,contextptr);
      }
      gen ret=_pow(makesequence(p,_inv(gd,contextptr)),contextptr);
      return isroot(ret,deg,contextptr)?radsimp(ret,contextptr):ratnormal(ret,contextptr);
    }
    if (g.type==_SYMB) {
      gen &feu=g._SYMBptr->feuille;
      if (feu.type==_VECT) {
	vecteur res;
	for (const_iterateur it=feu._VECTptr->begin();it!=feu._VECTptr->end();++it) {
	  res.push_back(radsimp(*it,contextptr));
	}
	return symbolic(g._SYMBptr->sommet,change_subtype(res,feu.subtype));
      }
      return symbolic(g._SYMBptr->sommet,radsimp(feu,contextptr));
    }
    return g;
  }

  vecteur strip_gcd(const vecteur &v,GIAC_CONTEXT) {
    gen g1=_gcd(_apply(makesequence(at_numer,v),contextptr),contextptr);
    gen g2=_gcd(_apply(makesequence(at_denom,v),contextptr),contextptr);
    return *_collect(_ratnormal(multvecteur(g2/g1,v),contextptr),contextptr)._VECTptr;
  }

  gen ratsimp_nonexp(const gen &g,GIAC_CONTEXT) {
    if (g.type==_VECT) {
      vecteur res;
      for (const_iterateur it=g._VECTptr->begin();it!=g._VECTptr->end();++it) {
	res.push_back(ratsimp_nonexp(*it,contextptr));
      }
      return change_subtype(res,g.subtype);
    }
    if (g.is_symb_of_sommet(at_plus) && g._SYMBptr->feuille==_VECT) {
      vecteur &terms=*g._SYMBptr->feuille._VECTptr;
      gen res(0);
      for (const_iterateur it=terms.begin();it!=terms.end();++it) {
	res+=ratsimp_nonexp(*it,contextptr);
      }
      return res;
    }
    if (g.is_symb_of_sommet(at_prod) && g._SYMBptr->feuille.type==_VECT) {
      vecteur &facs=*g._SYMBptr->feuille._VECTptr;
      gen e(1),ne(1);
      for (const_iterateur it=facs.begin();it!=facs.end();++it) {
	if (it->is_symb_of_sommet(at_exp))
	  e=*it*e;
	else ne=*it*ne;
      }
      return _ratnormal(ne,contextptr)*e;
    }
    return g;
  }

  gen fullsimp(const gen &g,GIAC_CONTEXT) {
    return ratsimp_nonexp(_collect(radsimp(explnsimp(exp(_ratnormal(g,contextptr),contextptr),
                                                     contextptr),contextptr),contextptr),contextptr);
  }

  /*
   * This routine solves the general homogeneous linear second-order ODE
   * y''=r(t)*y, where r is a non-constant rational function, using Kovacic's
   * algorithm (https://core.ac.uk/download/pdf/82509765.pdf). A list of
   * solutions is returned (possibly empty).
   */
  gen kovacicsols(const gen &r_orig,const gen &x,const gen &dy_coeff,GIAC_CONTEXT) {
    gen r=_ratnormal(r_orig,contextptr),inf=symbolic(at_plus,_IDNT_infinity());
    gen s=_numer(r,contextptr),t=_denom(r,contextptr),a,b,c,e,w=identificateur("omega_");
    int ds=_degree(makesequence(s,x),contextptr).val;
    int dt=_degree(makesequence(t,x),contextptr).val,dinf=dt-ds,order,nu;
    vecteur poles=*_roots(makesequence(t,x),contextptr)._VECTptr,solutions(0);
    gen cpfr=_cpartfrac(makesequence(r,x),contextptr);
    gen laur=_series(makesequence(r,x,inf,1,_POLY1__VECT),contextptr);
    bool success=false;
    if (kovacic_iscase1(poles,dinf)) {
      cerr << "Case 1 of Kovacic algorithm" << endl;
      /* step 1 */
      gen_map alpha_plus,alpha_minus,sqrt_r;
      gen alpha_inf_plus,alpha_inf_minus;
      for (const_iterateur it=poles.begin();it!=poles.end();++it) {
	c=it->_VECTptr->front();
	order=it->_VECTptr->back().val;
	if (order==1) {
	  sqrt_r[c]=0;
	  alpha_plus[c]=alpha_minus[c]=1;
	} else if (order==2) {
	  sqrt_r[c]=0;
	  b=expansion_coeff(cpfr,pow(x-c,-2),contextptr);
	  alpha_plus[c]=(1+sqrt(1+4*b,contextptr))/2;
	  alpha_minus[c]=(1-sqrt(1+4*b,contextptr))/2;
	} else if (order%2==0 && order>=4) {
	  nu=order/2;
	  e=_series(makesequence(sqrt(r,contextptr),x,c,1,_POLY1__VECT),contextptr);
	  for (int i=2;i<=nu;++i) {
	    gen cf=expansion_coeff(e,pow(x-c,-i),contextptr);
	    if (i==nu)
	      a=cf;
	    sqrt_r[c]+=cf/pow(x-c,i);
	  }
	  if (is_zero(a))
	    return false;
	  b=expansion_coeff(cpfr,pow(x-c,-nu-1),contextptr);
	  b-=expansion_coeff(_cpartfrac(makesequence(sq(sqrt_r[c]),x),contextptr),pow(x-c,-nu-1),contextptr);
	  alpha_plus[c]=(nu+b/a)/2;
	  alpha_minus[c]=(nu-b/a)/2;
	} else assert(false);
      }
      if (dinf>2) {
	sqrt_r[inf]=0;
	alpha_inf_plus=0;
	alpha_inf_minus=1;
      } else if (dinf==2) {
	sqrt_r[inf]=0;
	b=_lcoeff(makesequence(s,x),contextptr)/_lcoeff(makesequence(t,x),contextptr);
	alpha_inf_plus=(1+sqrt(1+4*b,contextptr))/2;
	alpha_inf_minus=(1-sqrt(1+4*b,contextptr))/2;
      } else if (dinf%2==0 && dinf<=0) {
	nu=-dinf/2;
	e=_series(makesequence(sqrt(r,contextptr),x,inf,1,_POLY1__VECT),contextptr);
	for (int i=0;i<=nu;++i) {
	  gen cf=expansion_coeff(e,pow(x,i),contextptr);
	  if (i==nu)
	    a=cf;
	  sqrt_r[inf]+=cf*pow(x,i);
	}
	if (is_zero(a))
	  return false;
	b=expansion_coeff(_propfrac(makesequence(r,x),contextptr),pow(x,nu-1),contextptr);
	b-=expansion_coeff(expand(sq(sqrt_r[inf]),contextptr),pow(x,nu-1),contextptr);
	alpha_inf_plus=(b/a-nu)/2;
	alpha_inf_minus=(-b/a-nu)/2;
      } else assert(false);
      /* step 2 */
      int np=poles.size()+1,N=(1<<np),sc,d,maxd=0; // N=std::pow(2.0,np)
      vecteur fam,cv,vars,v;
      gen fd,fw,P;
      for (gen_map::const_iterator it=alpha_plus.begin();it!=alpha_plus.end();++it) {
	cv.push_back(it->first);
      }
      for (int i=0;i<N;++i) {
	fd=(i & 1)!=0?alpha_inf_plus:alpha_inf_minus;
	for (int j=1;j<np;++j) {
	  fd-=(i & (1<<j))!=0?alpha_plus[cv[j-1]]:alpha_minus[cv[j-1]]; // i & (int)std::pow(2.0,j)
	}
	fd=_ratnormal(fd,contextptr);
	if (fd.is_integer() && is_positive(fd,contextptr)) {
	  fw=(i & 1)!=0?sqrt_r[inf]:-sqrt_r[inf];
	  for (int j=1;j<np;++j) {
	    sc=(i & (1<<j)); // i & (int)std::pow(2.0,j)
	    fw+=sc!=0?sqrt_r[cv[j-1]]:-sqrt_r[cv[j-1]];
	    fw+=(sc!=0?alpha_plus[cv[j-1]]:alpha_minus[cv[j-1]])/(x-cv[j-1]);
	  }
	  fam.push_back(makevecteur(fd,fw));
	  maxd=std::max(maxd,fd.val);
	}
      }
      /* step 3 */
      create_identifiers(vars,maxd);
      for (const_iterateur it=fam.begin();it!=fam.end() && !success;++it) {
	d=it->_VECTptr->front().val; fw=it->_VECTptr->back();
	v=vecteur(vars.begin(),vars.begin()+d);
	P=0;
	for (int i=0;i<d;++i) P+=v[i]*pow(x,i);
	P+=pow(x,d);
	e=_numer(_derive(makesequence(P,x,2),contextptr)+2*fw*_derive(makesequence(P,x),contextptr)+
		 (_derive(makesequence(fw,x),contextptr)+sq(fw)-r)*P,contextptr);
	gen lsol=_solve(makesequence(_coeff(makesequence(e,x),contextptr),v),contextptr);
	if (lsol.type==_VECT && !lsol._VECTptr->empty()) {
	  lsol=_subst(makesequence(lsol._VECTptr->front(),v,vecteur(d,0)),contextptr);
	  solutions.push_back(_subst(makesequence(P,v,lsol),contextptr)*
			      fullsimp(_int(makesequence(fw-dy_coeff/2,x),contextptr),contextptr));
	  success=true;
	}
      }
    }
    if (!success && kovacic_iscase2(poles)) {
      cerr << "Case 2 of Kovacic algorithm" << endl;
      /* step 1 */
      gen_map E;
      for (const_iterateur it=poles.begin();it!=poles.end();++it) {
	c=it->_VECTptr->front();
	order=it->_VECTptr->back().val;
	if (order==1)
	  E[c]=vecteur(1,4);
	else if (order==2) {
	  b=expansion_coeff(cpfr,pow(x-c,-2),contextptr);
	  E[c]=vecteur(0);
	  for (int k=-1;k<=1;++k) {
	    gen tmp=_eval(2+2*k*sqrt(1+4*b,contextptr),contextptr);
	    if (tmp.is_integer())
	      E[c]._VECTptr->push_back(tmp);
	  }
	} else if (order>2)
	  E[c]=vecteur(1,order);
      }
      vecteur Einf(0);
      if (dinf>2)
	Einf=makevecteur(0,2,4);
      else if (dinf==2) {
	b=expansion_coeff(laur,pow(x,-2),contextptr);
	for (int k=-1;k<=1;++k) {
	  gen tmp=_eval(2+2*k*sqrt(1+4*b,contextptr),contextptr);
	  if (tmp.is_integer())
	    Einf.push_back(tmp);
	}
      } else if (dinf<2)
	Einf.push_back(dinf);
      /* step 2 */
      vecteur family,families,fam,cv,vars,v;
      for (gen_map::const_iterator it=E.begin();it!=E.end();++it) {
	cv.push_back(it->first);
      }
      build_E_families(E,cv,family,families);
      int maxdeg=0,deg;
      for (const_iterateur it=Einf.begin();it!=Einf.end();++it) {
	if (families.empty()) {
	  e=*it/2;
	  if (e.is_integer() && is_positive(e,contextptr)) {
	    fam.push_back(makevecteur(e,0));
	    maxdeg=std::max(maxdeg,e.val);
	  }
	}
	for (const_iterateur jt=families.begin();jt!=families.end();++jt) {
	  gen th(0);
	  bool discard=is_one(_even(*it,contextptr));
	  const vecteur &fm=*(jt->_VECTptr);
	  for (const_iterateur kt=fm.begin();kt!=fm.end();++kt) {
	    th+=(*kt)/(x-cv[kt-fm.begin()]);
	    if (is_zero(_even(*kt,contextptr)))
	      discard=false;
	  }
	  //if (discard) continue;
	  e=_eval(*it-_sum(fm,contextptr),contextptr)/2;
	  if (e.is_integer() && is_positive(e,contextptr)) {
	    fam.push_back(makevecteur(e,th/2));
	    maxdeg=std::max(maxdeg,e.val);
	  }
	}
      }
      /* step 3 */
      create_identifiers(vars,maxdeg);
      gen P,th,dth;
      for (const_iterateur it=fam.begin();it!=fam.end() && !success;++it) {
	deg=it->_VECTptr->front().val; th=it->_VECTptr->back();
	v=vecteur(vars.begin(),vars.begin()+deg);
	P=0;
	for (int i=0;i<deg;++i) P+=v[i]*pow(x,i);
	P+=pow(x,deg);
	dth=_derive(makesequence(th,x),contextptr);
	e=_derive(makesequence(P,x,3),contextptr)+3*th*_derive(makesequence(P,x,2),contextptr)+
	  (3*sq(th)+3*dth-4*r)*_derive(makesequence(P,x),contextptr)+
	  (_derive(makesequence(th,x,2),contextptr)+3*th*dth+pow(th,3)-4*r*th-2*_derive(makesequence(r,x),contextptr))*P;
	e=_numer(e,contextptr);
	gen cfs=deg==0?undef:_solve(makesequence(_coeff(makesequence(e,x),contextptr),v),contextptr);
	if (deg==0?is_zero(e):(cfs.type==_VECT && !cfs._VECTptr->empty())) {
	  if (deg>0) {
	    cfs=_subst(makesequence(cfs._VECTptr->front(),v,vecteur(deg,0)),contextptr);
	    P=_subst(makesequence(P,v,cfs),contextptr);
	  }
	  gen ph=th+_derive(makesequence(P,x),contextptr)/P;
	  gen qsol=_solve(makesequence(symb_equal(sq(w)-w*ph+(_derive(makesequence(ph,x),contextptr)/2+sq(ph)/2-r),0),w),contextptr);
	  if (qsol.type==_VECT) for (const_iterateur jt=qsol._VECTptr->begin();jt!=qsol._VECTptr->end();++jt) {
	      solutions.push_back(fullsimp(_int(makesequence(*jt-dy_coeff/2,x),contextptr),contextptr));
	      success=true;
	    }
	}
      }
    }
    if (!success && kovacic_iscase3(cpfr,x,poles,dinf,contextptr)) {
      cerr << "Case 3 of Kovacic algorithm" << endl;
      vector<int> nv=vecteur_2_vector_int(makevecteur(4,6,12));
      for (vector<int>::const_iterator nt=nv.begin();nt!=nv.end();++nt) {
	int n=*nt;
	/* step 1 */
	gen_map E;
	for (const_iterateur it=poles.begin();it!=poles.end();++it) {
	  c=it->_VECTptr->front();
	  order=it->_VECTptr->back().val;
	  if (order==1)
	    E[c]=vecteur(1,12);
	  else if (order==2) {
	    a=expansion_coeff(cpfr,pow(x-c,-2),contextptr);
	    E[c]=vecteur(0);
	    for (int k=-n/2;k<=n/2;++k) {
	      gen tmp=_eval(6+k*(12/n)*sqrt(1+4*a,contextptr),contextptr);
	      if (tmp.is_integer())
		E[c]._VECTptr->push_back(tmp);
	    }
	  }
	}
	vecteur Einf(0);
	b=dinf>2?gen(0):expansion_coeff(laur,pow(x,-2),contextptr);
	for (int k=-n/2;k<=n/2;++k) {
	  gen tmp=_eval(6+k*(12/n)*sqrt(1+4*b,contextptr),contextptr);
	  if (tmp.is_integer())
	    Einf.push_back(tmp);
	}
	/* step 2 */
	vecteur family,families,fam,cv,vars,v;
	for (gen_map::const_iterator it=E.begin();it!=E.end();++it) {
	  cv.push_back(it->first);
	}
	build_E_families(E,cv,family,families);
	int maxdeg=0,deg;
	for (const_iterateur it=Einf.begin();it!=Einf.end();++it) {
	  if (families.empty()) {
	    e=*it*gen(n)/12;
	    if (e.is_integer() && is_positive(e,contextptr)) {
	      fam.push_back(makevecteur(e,0));
	      maxdeg=std::max(maxdeg,e.val);
	    }
	  }
	  for (const_iterateur jt=families.begin();jt!=families.end();++jt) {
	    gen th(0);
	    const vecteur &fm=*(jt->_VECTptr);
	    for (const_iterateur kt=fm.begin();kt!=fm.end();++kt) {
	      th+=(*kt)/(x-cv[kt-fm.begin()]);
	    }
	    e=gen(n)*_eval(*it-_sum(*jt,contextptr),contextptr)/12;
	    if (e.is_integer() && is_positive(e,contextptr)) {
	      fam.push_back(makevecteur(e,gen(n)*th/12));
	      maxdeg=std::max(maxdeg,e.val);
	    }
	  }
	}
	/* step 3 */
	gen S(1);
	for (const_iterateur it=cv.begin();it!=cv.end();++it) {
	  S=S*(x-*it);
	}
	gen dS=_derive(makesequence(S,x),contextptr),th;
	vecteur P(n+2,0);
	create_identifiers(vars,maxdeg);
	for (const_iterateur it=fam.begin();it!=fam.end();++it) {
	  deg=it->_VECTptr->front().val; th=it->_VECTptr->back();
	  v=vecteur(vars.begin(),vars.begin()+deg);
	  for (int i=0;i<deg;++i) P[n+1]-=v[i]*pow(x,i);
	  P[n+1]-=pow(x,deg);
	  P[n]=-S*(_derive(makesequence(P[n+1],x),contextptr)+th*P[n+1]),contextptr;
	  if (P[n].type==_SYMB) P[n]=_collect(P[n],contextptr);
	  for (int i=n;i-->0;) {
	    P[i]=-S*_derive(makesequence(P[i+1],x),contextptr)+((n-i)*dS-S*th)*P[i+1]-(n-i)*(i+1)*sq(S)*r*P[i+2];
	    if (P[i].type==_SYMB) P[i]=_collect(P[i],contextptr);
	  }
	  gen cfs;
	  if ((deg==0 && is_zero(P[0])) ||
	      (deg>0 && (cfs=_solve(makesequence(_coeff(makesequence(P[0],x),contextptr),v),contextptr)).type==_VECT &&
	       !cfs._VECTptr->empty())) {
	    if (deg>0) {
	      cfs=_subst(makesequence(cfs._VECTptr->front(),v,vecteur(deg,0)),contextptr);
	      P=*_subst(makesequence(P,v,cfs),contextptr)._VECTptr;
	    }
	    vecteur ac(n+1);
	    for (int i=0;i<=n;++i) {
	      ac[i]=_collect(pow(S,i)*P[i+1]/_factorial(n-i,contextptr),contextptr);
	    }
	    *logptr(contextptr) << "Warning: outputting the algebraic expression for ω" << endl;
	    ac=strip_gcd(ac,contextptr);
	    gen omg=pow(w,4)*ac[4]+pow(w,3)*ac[3]+pow(w,2)*ac[2]+w*ac[1]+ac[0];
	    if (!is_zero(dy_coeff)) {
	      vecteur C=*_coeff(makesequence(_subst(makesequence(omg,w,w+dy_coeff/2),contextptr),w),contextptr)._VECTptr;
	      for (int i=0;i<=n;++i) {
		ac[i]=_collect(_ratnormal(C[i],contextptr),contextptr);
	      }
	      ac=strip_gcd(ac,contextptr);
	      omg=pow(w,4)*ac[4]+pow(w,3)*ac[3]+pow(w,2)*ac[2]+w*ac[1]+ac[0];
	    }
	    return omg;
	  }
	}
      }
    }
    return solutions;
  }

  /*
   * Return the solution(s) of a second-order linear homogeneous ODE using
   * Kovacic's algorithm. The first argument is the ODE a(x)*y''+b(x)*y'+c(x)*y=0
   * itself, which may be given as an expression (left-hand side), an equation or
   * a list [a,b,c]. The functions a, b and c must be rational in x. The second
   * and third (both optional) arguments are the independent variable x and the
   * dependent variable y, respectively. By default, the symbols "x" and "y" are
   * used.
   */
  gen _kovacicsols(const gen &g,GIAC_CONTEXT) {
    if (g.type==_STRNG && g.subtype==-1) return g;
    gen x=identificateur("x"),y=identificateur("y"),eq,p(0),q(0),r(0);
    if (g.type!=_VECT || g.subtype!=_SEQ__VECT) {
      eq=g;
    } else if (g.subtype==_SEQ__VECT) {
      vecteur &gv=*g._VECTptr;
      if (gv.size()<2) return gensizeerr(contextptr);
      eq=gv.front();
      if (gv.size()==2) {
	if (eq.type==_VECT && gv.back().type==_IDNT)
	  x=gv.back();
	else if (gv.back().is_symb_of_sommet(at_of)) {
	  y=gv.back()._SYMBptr->feuille._VECTptr->front();
	  x=gv.back()._SYMBptr->feuille._VECTptr->back();
	} else return gensizeerr(contextptr);
      } else {
	if (eq.type==_VECT)
	  return gensizeerr(contextptr);
	x=gv[1];
	y=gv[2];
      }
      if (y.type!=_IDNT || x.type!=_IDNT)
	return gensizeerr(contextptr);
    }
    eq=idnteval(eq,contextptr);
    if (eq.type==_VECT) {
      vecteur &cfs=*eq._VECTptr;
      if (cfs.size()!=3)
	return gensizeerr(contextptr);
      p=cfs[1]; q=cfs[2]; r=cfs[0];
    } else if (eq.type==_SYMB) {
      gen dy=identificateur(" dy"),d2y=identificateur(" d2y"),yx=symb_of(y,x);
      gen diffy=symbolic(at_derive,y),diff2y=symbolic(at_derive,diffy);
      eq=_subst(makesequence(eq,makevecteur(_derive(makesequence(yx,x),contextptr),_derive(makesequence(yx,x,2),contextptr)),
			     makevecteur(dy,d2y)),contextptr);
      eq=_subst(makesequence(_subst(makesequence(_subst(makesequence(eq,diff2y,d2y),contextptr),diffy,dy),contextptr),yx,y),contextptr);
      if (eq.is_symb_of_sommet(at_equal))
	eq=equal2diff(eq);
      eq=expand(eq,contextptr);
      vecteur terms=eq.is_symb_of_sommet(at_plus) && eq._SYMBptr->feuille.type==_VECT?
	*eq._SYMBptr->feuille._VECTptr:vecteur(1,eq);
      gen tmp;
      vecteur yvars=makevecteur(y,dy,d2y);
      for (const_iterateur it=terms.begin();it!=terms.end();++it) {
	if (is_constant_wrt_vars(tmp=_ratnormal(*it/y,contextptr),yvars,contextptr))
	  q+=tmp;
	else if (is_constant_wrt_vars(tmp=_ratnormal(*it/dy,contextptr),yvars,contextptr))
	  p+=tmp;
	else if (is_constant_wrt_vars(tmp=_ratnormal(*it/d2y,contextptr),yvars,contextptr))
	  r+=tmp;
	else return gensizeerr(contextptr);
      }
    } else return gensizeerr(contextptr);
    if (is_zero(r)) // not a second order ODE
      return gensizeerr(contextptr);
    p=_ratnormal(p/r,contextptr);
    q=_ratnormal(q/r,contextptr);
    if (rlvarx(p,x).size()+rlvarx(q,x).size()>2) // p or q is not rational in x
      return gensizeerr(contextptr);
    /* solve the equation y''+p(x)*y'+q(x)*y=0, transform it first to z''=r(x)*z */
    r=sq(p)/4+_derive(makesequence(p,x),contextptr)/2-q;
    return kovacicsols(r,x,p,contextptr);
  }
  static const char _kovacicsols_s []="kovacicsols";
  static define_unary_function_eval (__kovacicsols,&_kovacicsols,_kovacicsols_s);
  define_unary_function_ptr5(at_kovacicsols,alias_at_kovacicsols,&__kovacicsols,_QUOTE_ARGUMENTS,true)

  /*
   * Return true iff the expression 'e' is constant with respect to
   * variables in 'vars'.
   */
  bool is_constant_wrt_vars(const gen &e,const vecteur &vars,GIAC_CONTEXT) {
    for (const_iterateur it=vars.begin();it!=vars.end();++it) {
      if (!is_constant_wrt(e,*it,contextptr))
	return false;
    }
    return true;
  }
  
  gen idnteval(const gen &g,GIAC_CONTEXT) {
    if (g.type==_IDNT)
      return _eval(g,contextptr);
    if (g.type==_SYMB) {
      gen &feu=g._SYMBptr->feuille;
      if (feu.type==_VECT) {
	vecteur v;
	for (const_iterateur it=feu._VECTptr->begin();it!=feu._VECTptr->end();++it) {
	  v.push_back(idnteval(*it,contextptr));
	}
	return symbolic(g._SYMBptr->sommet,change_subtype(v,feu.subtype));
      }
      return symbolic(g._SYMBptr->sommet,idnteval(feu,contextptr));
    }
    return g;
  }

#endif

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
