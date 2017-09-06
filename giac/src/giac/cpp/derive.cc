/* -*- mode:C++ ; compile-command: "g++-3.4 -DHAVE_CONFIG_H -I. -I..  -DIN_GIAC -g -c derive.cc" -*- */
#include "giacPCH.h"
/*
 *  Copyright (C) 2000,14 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#include "derive.h"
#include "usual.h"
#include "symbolic.h"
#include "unary.h"
#include "poly.h"
#include "sym2poly.h" // for equalposcomp
#include "tex.h"
#include "prog.h"
#include "intg.h"
#include "subst.h"
#include "plot.h"
#include "modpoly.h"
#include "moyal.h"
#include "alg_ext.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

   gen eval_before_diff(const gen & expr,const gen & variable,GIAC_CONTEXT){
    identificateur tmp_x_id(" eval_before_diff_x");
    gen tmp_x(tmp_x_id);
    gen res=subst(expr,variable,tmp_x,false,contextptr); // replace variable by a non affected identifier
    gen save_vx_var=vx_var;
    if (variable==vx_var) vx_var=tmp_x;
    int m=calc_mode(contextptr);
    calc_mode(0,contextptr);
    res=eval(res,1,contextptr); // eval res (all identifiers except X will be replaced by their values)
    res=eval(res,1,contextptr); // eval res (all identifiers except X will be replaced by their values)
    calc_mode(m,contextptr);
    vx_var=save_vx_var;
    res=subst(res,tmp_x,variable,false,contextptr);
    return res;
  }

  bool depend(const gen & g,const identificateur & i){
    if (g.type==_IDNT)
      return *g._IDNTptr==i;
    if (g.type==_SYMB)
      return depend(g._SYMBptr->feuille,i);
    if (g.type!=_VECT)
      return false;
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it){
      if (depend(*it,i))
	return true;
    }
    return false;
  }

  static int count_noncst(const gen & g,const identificateur & i){
    if (g.type!=_VECT)
      return depend(g,i)?1:0;
    int res=0;
    for (unsigned j=0;j<g._VECTptr->size();++j){
      if (depend((*g._VECTptr)[j],i))
	++res;
    }
    return res;
  }

  static gen derive_SYMB(const gen &g_orig,const identificateur & i,GIAC_CONTEXT){
    const symbolic & s = *g_orig._SYMBptr;
    if (s.sommet==at_pnt){
      gen f=g_orig._SYMBptr->feuille;
      if (f.type==_VECT && !f._VECTptr->empty()){
	vecteur v=*f._VECTptr;
	v[0]=derive(v[0],i,contextptr);
	f=gen(v,f.subtype);
	return symbolic(at_pnt,f);
      }
    }
    // if s does not depend on i return 0
    if (!depend(g_orig,i))
      return zero;
    // rational operators are treated first for efficiency
    if (s.sommet==at_plus){
      bool do_step=step_infolevel(contextptr)>1 && count_noncst(s.feuille,i)>1;
      if (do_step)
	gprintf(gettext("Derivative of %gen apply linearity: (u+v+...)'=u'+v'+..."),makevecteur(s),contextptr);
      if (s.feuille.type!=_VECT)
	return derive(s.feuille,i,contextptr);
      vecteur::const_iterator iti=s.feuille._VECTptr->begin(),itend=s.feuille._VECTptr->end();
      int taille=int(itend-iti);
      if (taille==2)
	return derive(*iti,i,contextptr)+derive(*(iti+1),i,contextptr);
      vecteur v;
      v.reserve(taille);
      gen e;
      for (;iti!=itend;++iti){
	e=derive(*iti,i,contextptr);
	if (is_undef(e))
	  return e;
	if (!is_zero(e))
	  v.push_back(e);
      }
      if (v.size()==1)
	return v.front();
      if (v.empty())
	return zero;
      gen res=_plus(gen(v,_SEQ__VECT),contextptr); // symbolic(at_plus,v);
      if (do_step)
	gprintf(gettext("Hence derivative of %gen by linearity is %gen"),makevecteur(g_orig,res),contextptr);
      return res;
    }
    if (s.sommet==at_prod){
      bool do_step=step_infolevel(contextptr)>1 && count_noncst(s.feuille,i)>1;
      if (s.feuille.type==_VECT && s.feuille._VECTptr->size()==2 && s.feuille._VECTptr->back().is_symb_of_sommet(at_inv) && !is_constant_wrt(s.feuille._VECTptr->back()._SYMBptr->feuille,i,contextptr)){
	gen u=s.feuille._VECTptr->front(),v=s.feuille._VECTptr->back()._SYMBptr->feuille;
	if (do_step)
	  gprintf(gettext("Derivative of %gen/%gen, a quotient: (u/v)'=(u'*v-u*v')/v^2"),makevecteur(u,v),contextptr);
	return (derive(u,i,contextptr)*v-u*derive(v,i,contextptr))/pow(v,2,contextptr);
      }
      if (do_step)
	gprintf(gettext("Derivative of %gen, apply product rule: (u*v*...)'=u'*v*...+u*v'*...+..."),makevecteur(s),contextptr);
      if (s.feuille.type!=_VECT)
	return derive(s.feuille,i,contextptr);
      vecteur::const_iterator itbegin=s.feuille._VECTptr->begin(),itj,iti,itend=s.feuille._VECTptr->end();
      int taille=int(itend-itbegin);
      // does not work because of is_linear_wrt e.g. for cos(3*pi/4)
      // if (taille==2) return derive(*itbegin,i,contextptr)*(*(itbegin+1))+(*itbegin)*derive(*(itbegin+1),i,contextptr);
      vecteur v,w;
      v.reserve(taille);
      w.reserve(taille);
      gen e;
      for (iti=itbegin;iti!=itend;++iti){
	w.clear();
	e=derive(*iti,i,contextptr);
	if (is_undef(e))
	  return e;
	if (!is_zero(e)){
	  for (itj=itbegin;itj!=iti;++itj)
	    w.push_back(*itj);
	  w.push_back(e);
	  ++itj;
	  for (;itj!=itend;++itj)
	    w.push_back(*itj);
	  v.push_back(_prod(w,contextptr));
	}
      }
      if (v.size()==1)
	return v.front();
      if (v.empty())
	return zero;
      gen res=symbolic(at_plus,gen(v,_SEQ__VECT));
      if (do_step)
	gprintf(gettext("Hence derivative of %gen by product rule is %gen"),makevecteur(g_orig,res),contextptr);
      return res;
    }
    if (s.sommet==at_neg)
      return -derive(s.feuille,i,contextptr);
    if (s.sommet==at_pow){
      if (s.feuille.type!=_VECT || s.feuille._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      gen base = s.feuille._VECTptr->front(),exponent=s.feuille._VECTptr->back();
      if (step_infolevel(contextptr)>1){
	if (is_constant_wrt(exponent,i,contextptr))
	  gprintf(gettext("Derivative of a power: (%gen)'=(%gen)*(%gen)'*%gen"),makevecteur(symb_pow(base,exponent),exponent,base,symb_pow(base,exponent-1)),contextptr);
	else
	  gprintf(gettext("Derivative of a power: (%gen)'=%gen*(%gen)'*ln(%gen)+(%gen)*(%gen)'*%gen"),makevecteur(symb_pow(base,exponent),symb_pow(base,exponent),exponent,base,exponent,base,symb_pow(base,exponent-1)),contextptr);
      }
      gen dbase=derive(base,i,contextptr),dexponent=derive(exponent,i,contextptr);
      // diff(base^exponent)=diff(exp(exponent*ln(base)))
      // =base^exponent*diff(exponent)*ln(base)+base^(exponent-1)*exponent*diff(base)
      gen expm1=exponent+gen(-1);
      if (is_zero(dexponent))
	return exponent*dbase*pow(base,expm1,contextptr);
      return dexponent*ln(base,contextptr)*s+exponent*dbase*pow(base,expm1,contextptr);
    }
    if (s.sommet==at_inv){
      if (step_infolevel(contextptr)>1)
	gprintf(gettext("Derivative of inv(u)=-u'/u^2 with u=%gen"),makevecteur(s.feuille),contextptr);
      if (s.feuille.is_symb_of_sommet(at_pow)){
	gen & f = s.feuille._SYMBptr->feuille;
	if (f.type==_VECT && f._VECTptr->size()==2)
	  return derive(symb_pow(f._VECTptr->front(),-f._VECTptr->back()),i,contextptr);
      }
      return rdiv(-derive(s.feuille,i,contextptr),pow(s.feuille,2),contextptr);
    }
    if (equalposcomp(inequality_tab,s.sommet))
      return 0;
    if (s.sommet==at_fsolve && s.feuille.type==_VECT && s.feuille._VECTptr->size()>=2){
      vecteur v=*s.feuille._VECTptr;
      if (v[1].is_symb_of_sommet(at_equal) && v[1]._SYMBptr->feuille.type==_VECT && !v[1]._SYMBptr->feuille._VECTptr->empty())
	v[1]=v[1]._SYMBptr->feuille._VECTptr->front();
      gen eq=remove_equal(v[0]),y=v[1],x=i; // fsolve(eq(x,y),y) -> y(x), dy/dx=-(deq/dx)/(deq/dy)
      gen res=-derive(eq,x,contextptr)/derive(eq,y,contextptr);
      res=subst(res,y,s,false,contextptr);
      return res;
    }
    if (s.sommet==at_rootof){
      gen f=s.feuille;
      if (f.type==_VECT && f._VECTptr->size()==2 && f._VECTptr->front().type==_VECT && f._VECTptr->back().type==_VECT){
	vecteur P=*f._VECTptr->front()._VECTptr;
	vecteur Q=*f._VECTptr->back()._VECTptr;
	// d/dx(P(y))=(dP/dx)(y) + (dP/dy) (dy/dx)
	//           =(dP/dx)(y) - (dP/dy) (dQ/dx)/(dQ/dy)
	// where y dependency is in the list polynomial P/Q
	gen Px=trim(*derive(P,i,contextptr)._VECTptr,0);
	Px=symb_rootof(Px,Q,contextptr);
	gen Qx=trim(*derive(Q,i,contextptr)._VECTptr,0);
	Qx=symb_rootof(Qx,Q,contextptr);
	gen Py=derivative(P);
	Py=symb_rootof(Py,Q,contextptr);
	gen Qy=derivative(Q);
	Qy=symb_rootof(Qy,Q,contextptr);
	gen res=Px-(Py*Qx)/Qy;
	res=normal(res,contextptr);
	return res;
      }
      return gensizeerr(gettext("Derivative of rootof currently not handled"));
    }
    if (step_infolevel(contextptr)>1 && s.feuille.type!=_VECT){
      if (s.feuille==i){
	int save_step=step_infolevel(contextptr);
	step_infolevel(contextptr)=0;
	gen der=derive_SYMB(g_orig,i,contextptr);
	step_infolevel(contextptr)=save_step;
	gprintf(gettext("Derivative of elementary function %gen is %gen"),makevecteur(g_orig,der),contextptr);
      }
      else
	gprintf(gettext("Derivative of a composition: (%gen)'=(%gen)'*f'(%gen) where f=%gen"),makevecteur(g_orig,s.feuille,s.feuille,s.sommet),contextptr);
    }
    if (s.sommet==at_UTPT){
      if (s.feuille.type!=_VECT || s.feuille._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      gen & arg=s.feuille._VECTptr->back();
      return -derive(arg,i,contextptr)*_student(s.feuille,contextptr);
    }
    if (s.sommet==at_UTPC){
      if (s.feuille.type!=_VECT || s.feuille._VECTptr->size()!=2)
	return gensizeerr(contextptr);
      gen & arg=s.feuille._VECTptr->back();
      return -derive(arg,i,contextptr)*_chisquare(s.feuille,contextptr);
    }
    if (s.sommet==at_UTPF){
      if (s.feuille.type!=_VECT || s.feuille._VECTptr->size()!=3)
	return gensizeerr(contextptr);
      gen & arg=s.feuille._VECTptr->back();
      return -derive(arg,i,contextptr)*_snedecor(s.feuille,contextptr);
    }
    if (s.sommet==at_program){
      return gensizeerr(gettext("Expecting an expression, not a function"));
    }
    if (s.sommet==at_ln){ 
      if (s.feuille.is_symb_of_sommet(at_abs) )
	return rdiv(derive(s.feuille._SYMBptr->feuille,i,contextptr),s.feuille._SYMBptr->feuille,contextptr);
      if (s.feuille.is_symb_of_sommet(at_inv))
	return -derive(symbolic(at_ln,s.feuille._SYMBptr->feuille),i,contextptr);
      if (s.feuille.is_symb_of_sommet(at_prod)){
	gen res;
	const gen &f=s.feuille._SYMBptr->feuille;
	if (f.type==_VECT){
	  const_iterateur it=f._VECTptr->begin(),itend=f._VECTptr->end();
	  for (;it!=itend;++it)
	    res=res+derive(symbolic(at_ln,*it),i,contextptr);
	  return res;
	}
      }
    }
    if (s.feuille.type==_VECT){
      vecteur v=*s.feuille._VECTptr;
      int vs=int(v.size());
      if (vs>=3 && (s.sommet==at_ifte || s.sommet==at_when) ){
	for (int j=1;j<vs;++j){
	  gen & tmp=v[j];
	  tmp=derive(tmp,i,contextptr); // v[j]=derive(v[j],i,contextptr);
	  // if (is_undef(tmp)) return tmp; 
	  // commented otherwise diff(when(x<0,x^2+3,undef)) returns undef
	}
	return symbolic(s.sommet,gen(v,s.feuille.subtype));
      }
      if (s.sommet==at_piecewise){
	for (int j=0;j<vs/2;++j){
	  gen & tmp=v[2*j+1];
	  tmp=derive(tmp,i,contextptr); // v[2*j+1]=derive(v[2*j+1],i,contextptr);
	}
	if (vs%2){
	  gen & tmp=v[vs-1];
	  tmp=derive(tmp,i,contextptr); // v[vs-1]=derive(v[vs-1],i,contextptr);
	}
	return symbolic(s.sommet,gen(v,s.feuille.subtype));
      }
      if (vs==2 && s.sommet==at_NTHROOT){
	gen base = v[1],exponent=inv(v[0],contextptr);
	gen dbase=derive(base,i,contextptr),dexponent=derive(exponent,i,contextptr);
	// diff(base^exponent)=diff(exp(exponent*ln(base)))
	// =base^exponent*diff(exponent)*ln(base)+base^(exponent-1)*exponent*diff(base)
	if (is_zero(dexponent))
	  return exponent*dbase*s/v[1];
	return dexponent*ln(base,contextptr)*s+exponent*dbase*s/v[1];
      }
      if (vs>=3 && s.sommet==at_Beta){
	gen v0=v[0],v1=v[1],v2=v[2],v3=v[3]; 
	if (!is_zero(derive(v0,i,contextptr)) || !is_zero(derive(v1,i,contextptr)) )
	  return gensizeerr("diff of incomplete beta with respect to non constant 1st or 2nd arg not implemented");
	// diff/v2 of int_0^v2 t^(v0-1)*(1-t)^(v1-1) dt
	gen tmp=pow(v2,v0-1,contextptr)*pow(1-v2,v1-1,contextptr)*derive(v2,i,contextptr);
	if (vs==4){
	  if (is_one(v3))
	    return tmp/Beta(v0,v1,contextptr);
	  return gensizeerr(contextptr);
	  gen v3p=derive(v3,i,contextptr);
	  if (!is_zero(v3p))
	    return tmp-pow(v3,v0-1,contextptr)*pow(1-v3,v1-1,contextptr)*v3p;
	}
	return tmp;
      }
      if (vs==4 && s.sommet==at_sum){
	gen v0=v[0],v1=v[1],v2=v[2],v3=v[3];
	if (!is_zero(derive(v1,i,contextptr)) || !is_zero(derive(v2,i,contextptr)) || ! is_zero(derive(v3,i,contextptr)) )
	  return gensizeerr("diff of sum with boundaries or mute variable depending on differentiation variable");
	if (is_inf(v2) || is_inf(v3))
	  *logptr(contextptr) << "Warning, assuming derivative commutes with infinite sum" << endl;
	return _sum(makesequence(derive(v0,i,contextptr),v1,v2,v3),contextptr);
      }
      if ( (vs==2 || (vs==3 && is_zero(v[2]))) && (s.sommet==at_upper_incomplete_gamma || s.sommet==at_lower_incomplete_gamma || s.sommet==at_Gamma)){
	gen v0=v[0],v1=v[1]; 
	if (!is_zero(derive(v0,i,contextptr)))
	  return gensizeerr("diff of incomplete gamma with respect to non constant 1st arg not implemented");
	// diff(int_v1^inf exp(-t)*t^(v0-1) dt)
	gen tmp1=exp(-v1,contextptr)*pow(v1,v0-1,contextptr)*derive(v1,i,contextptr);
	return (s.sommet==at_lower_incomplete_gamma)?tmp1:-tmp1;
      }
      if (vs==3 && (s.sommet==at_upper_incomplete_gamma || s.sommet==at_lower_incomplete_gamma || s.sommet==at_Gamma)){
	return derive(symbolic(s.sommet,makesequence(v[0],v[1]))/symbolic(at_Gamma,v[0]),i,contextptr);
      }
    }
    // now look at other operators, first onearg operator
    if (s.sommet.ptr()->D){
      if (s.feuille.type!=_VECT)
	return derive(s.feuille,i,contextptr)*(*s.sommet.ptr()->D)(1)(s.feuille,contextptr);
      // multiargs operators
      int taille=int(s.feuille._VECTptr->size());
      vecteur v;
      v.reserve(taille);
      vecteur::const_iterator iti=s.feuille._VECTptr->begin(),itend=s.feuille._VECTptr->end();
      gen e;
      for (int j=1;iti!=itend;++iti,++j){
	e=derive(*iti,i,contextptr);
	if (is_undef(e))
	  return e;
	if (!is_zero(e))
	  v.push_back(e*(*s.sommet.ptr()->D)(j)(s.feuille,contextptr));
      }
      if (v.size()==1)
	return v.front();
      if (v.empty())
	return zero;
      return symbolic(at_plus,gen(v,_SEQ__VECT));
    }
    // integrate
    if (s.sommet==at_integrate || s.sommet==at_HPINT){
      if (s.feuille.type!=_VECT)
	return s.feuille;
      vecteur v=*s.feuille._VECTptr;
      int nargs=int(v.size());
      if (nargs<=1)
	return s.feuille;
      if (nargs==2 && is_equal(v[1])){
	gen v1f=v[1]._SYMBptr->feuille;
	if (v1f.type==_VECT && v1f._VECTptr->size()==2){
	  gen v1f1=v1f._VECTptr->front();
	  gen v1f2=v1f._VECTptr->back();
	  v[1]=v1f1;
	  if (v1f2.is_symb_of_sommet(at_interval)){
	    v.push_back(v1f2._SYMBptr->feuille._VECTptr->front());
	    v.push_back(v1f2._SYMBptr->feuille._VECTptr->back());
	    nargs=4;
	  }
	}
      }
      gen res,newint;
      if (v[1]==i)
	res=v[0];
      else {
	res=subst(v[0],v[1],i,false,contextptr);
	newint=derive(v[0],i,contextptr);	 
	if (nargs<4)
	  newint=integrate_gen(newint,v[1],contextptr);
      }
      if (nargs==2)
	return res+newint;
      if (nargs==3)
	return derive(v[2],i,contextptr)*subst(res,i,v[2],false,contextptr);
      if (nargs==4){
	gen a3=derive(v[3],i,contextptr);
	gen b3=is_zero(a3)?zero:limit(res,i,v[3],-1,contextptr);
	gen a2=derive(v[2],i,contextptr);
	gen b2=is_zero(a2)?zero:limit(res,i,v[2],1,contextptr);
	return a3*b3-a2*b2+_integrate(gen(makevecteur(newint,v[1],v[2],v[3]),_SEQ__VECT),contextptr);
      }
      return gensizeerr(contextptr);
    }
    if (s.sommet==at_of && s.feuille.type==_VECT && s.feuille._VECTptr->size()==2){
      // assuming we do not have an index in a list or matrix!
      gen f=s.feuille._VECTptr->front();
      gen arg=s.feuille._VECTptr->back();
      gen darg=derive(arg,i,contextptr);
      if (!is_one(darg)){
	if (darg.type==_VECT){
	  gen res=0;
	  for (int i=0;i<int(darg._VECTptr->size());++i){
	    gen fprime=symbolic(at_derive,makesequence(f,i));
	    res += darg[i]*symbolic(at_of,makesequence(fprime,arg));
	  }
	  return res;
	}
	// f(arg)'=arg'*f'(arg)
	gen fprime=symbolic(at_derive,f);
	return darg*symbolic(at_of,makesequence(fprime,arg));
      }
    }
    // multi derivative and multi-indice derivatives
    if (s.sommet==at_derive){
      if (s.feuille.type!=_VECT)
	return symbolic(at_derive,gen(makevecteur(s.feuille,vx_var,2),_SEQ__VECT));
      if (s.feuille._VECTptr->size()==2){ // derive(f,x)
	gen othervar=(*s.feuille._VECTptr)[1];
	if (othervar.type!=_IDNT) return gensizeerr(gettext("derive.cc/derive_SYMB"));
	if (*othervar._IDNTptr==i){ // _FUNCnd derivative
	  vecteur res(*s.feuille._VECTptr);
	  symbolic sprime(s);
	  res.push_back(2);
	  return symbolic(at_derive,gen(res,_SEQ__VECT));
	}
	else {
	  vecteur var;
	  var.push_back(othervar);
	  var.push_back(i);
	  vecteur nderiv;
	  nderiv.push_back(1);
	  nderiv.push_back(1);
	  return symbolic(at_derive,gen(makevecteur((*s.feuille._VECTptr)[0],var,nderiv),_SEQ__VECT));
	}
      }
      else { // derive(f,x,n)
	if (s.feuille._VECTptr->size()!=3)  return gensizeerr(gettext("derive.cc/derive_SYMB"));
	gen othervar=(*s.feuille._VECTptr)[1];
	if (othervar.type==_IDNT){
	  if (*othervar._IDNTptr==i){ // n+1 derivative
	    vecteur vprime=(*s.feuille._VECTptr);
	    vprime[2] += 1;
	    return symbolic(s.sommet,gen(vprime,_SEQ__VECT));
	  }
	  else {
	    vecteur var;
	    var.push_back(othervar);
	    var.push_back(i);
	    vecteur nderiv;
	    nderiv.push_back((*s.feuille._VECTptr)[2]);
	    nderiv.push_back(1);
	    return symbolic(at_derive,gen(makevecteur((*s.feuille._VECTptr)[0],var,nderiv),_SEQ__VECT));
	  }
	} // end if othervar.type==_IDNT
	else { // othervar.type must be _VECT
	  if (othervar.type!=_VECT)  return gensizeerr(gettext("derive.cc/derive_SYMB"));
	  gen nder((*s.feuille._VECTptr)[2]);
	  if (nder.type!=_VECT ||
	      nder._VECTptr->size()!=othervar._VECTptr->size())  return gensizeerr(gettext("derive.cc/derive_SYMB"));
	  vecteur nderiv(*nder._VECTptr);
	  int pos=equalposcomp(*othervar._VECTptr,i);
	  if (pos){
	    nderiv[pos-1]=nderiv[pos-1]+1;
	  }
	  else {
	    othervar._VECTptr->push_back(i);
	    nderiv.push_back(1);
	  }
	  return symbolic(at_derive,gen(makevecteur((*s.feuille._VECTptr)[0],othervar,nderiv),_SEQ__VECT));
	}
      }
    }
    if (s.sommet==at_re || s.sommet==at_im || s.sommet==at_conj){
      return s.sommet(derive(s.feuille,i,contextptr),contextptr);
    }
    // no info about derivative
    return symbolic(at_derive,gen(makevecteur(s,i),_SEQ__VECT));
    //i.dbgprint();
    //s.dbgprint();
  }

  static gen derive_VECT(const vecteur & v,const identificateur & i,GIAC_CONTEXT){
    vecteur w;
    w.reserve(v.size());
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      gen tmp=derive(*it,i,contextptr);
      if (is_undef(tmp))
	return tmp;
      w.push_back(tmp);
    }
    return w;
  }

  gen derive(const gen & e,const identificateur & i,GIAC_CONTEXT){
    if (abs_calc_mode(contextptr)==38 && i.id_name[0]>='A' && i.id_name[0]<='Z'){
      identificateur tmp("xdiff");
      gen ee=subst(e,i,tmp,true,contextptr);
      ee=eval(ee,1,contextptr);
      ee=subst(ee,i,tmp,true,contextptr);
      ee=derive(ee,tmp,contextptr);
      ee=subst(ee,tmp,i,true,contextptr);
      return ee;
    }
    switch (e.type){
    case _INT_: case _DOUBLE_: case _ZINT: case _CPLX: case _MOD: case _REAL: case _USER: case _FLOAT_:
      return 0;
    case _IDNT:
      if (is_undef(e))
	return e;
      if (*e._IDNTptr==i)
	return 1;
      else
	return 0;
    case _SYMB:
      return derive_SYMB(e,i,contextptr);
    case _VECT: {
      gen res=derive_VECT(*e._VECTptr,i,contextptr);
      if (res.type==_VECT) res.subtype=e.subtype;
      return res;
    }
    case _FRAC:
      return fraction(derive(e._FRACptr->num,i,contextptr)*e._FRACptr->den-(e._FRACptr->num)*derive(e._FRACptr->den,i,contextptr),e._FRACptr->den);
    default:
      return gentypeerr(contextptr);
    }
    return 0;
  }

  static gen _VECTderive(const gen & e,const vecteur & v,GIAC_CONTEXT){
    vecteur w;
    w.reserve(v.size());
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      gen tmp=derive(e,*it,contextptr);
      if (is_undef(tmp))
	return tmp;
      w.push_back(tmp);
    }
    return w;    
  }

  static gen derivesymb(const gen& e,const gen & var,GIAC_CONTEXT){
    identificateur x(" x");
    gen xx(x);
    gen f=subst(e,var,xx,false,contextptr);
    f=derive(f,x,contextptr);
    f=subst(f,xx,var,false,contextptr);
    return f;
  }
  gen derive(const gen & e,const gen & vars,GIAC_CONTEXT){
    //  cout << e << " " << vars << endl;
    if (is_equal(e))
      return symb_equal(derive(e._SYMBptr->feuille[0],vars,contextptr),
			derive(e._SYMBptr->feuille[1],vars,contextptr));
    switch (vars.type){
    case _INT_:
      return symbolic(at_derive,makesequence(e,vars));
    case _IDNT:
      return derive(e,*vars._IDNTptr,contextptr);
    case _VECT:
      return _VECTderive(e,*vars._VECTptr,contextptr);
    case _SYMB:
      return derivesymb(e,vars,contextptr);
    default:
      return gensizeerr(contextptr);
    }
    return 0;
  }

  gen derive(const gen & e,const gen & vars,const gen & nderiv,GIAC_CONTEXT){
    if (is_equal(e))
      return symb_equal(derive(e._SYMBptr->feuille[0],vars,nderiv,contextptr),
			derive(e._SYMBptr->feuille[1],vars,nderiv,contextptr));
    if (nderiv.type==_INT_){
      int n=nderiv.val;
      gen ecopie(e),eprime(e);
      int j=1;
      for (;j<=n;++j){
	eprime=ratnormal(derive(ecopie,vars,contextptr),contextptr);
	if (is_undef(eprime))
	  return eprime;
	if ( (eprime.type==_SYMB) && (eprime._SYMBptr->sommet==at_derive))
	  break;
	ecopie=eprime;
      }
      if (j==n+1)
	return eprime;
      return symbolic(at_derive,gen(makevecteur(ecopie,vars,n+1-j),_SEQ__VECT));
    }
    // multi-index derivation
    if (nderiv.type!=_VECT ||
	vars.type!=_VECT)  return gensizeerr(gettext("derive.cc/derive"));
    int s=int(nderiv._VECTptr->size());
    if (s!=signed(vars._VECTptr->size()))  return gensizeerr(gettext("derive.cc/derive"));
    int j=0;
    gen ecopie(e);
    for (;j<s;++j){
      ecopie=derive(ecopie,(*vars._VECTptr)[j],(*nderiv._VECTptr)[j],contextptr);
    }
    return ecopie;
  }  

  symbolic symb_derive(const gen & a,const gen & b){
    return symbolic(at_derive,gen(makevecteur(a,b),_SEQ__VECT));
  }

  gen symb_derive(const gen & a,const gen & b,const gen &c){
    if (is_zero(c))
      return a;
    if (is_one(c))
      return symb_derive(a,b);
    return symbolic(at_derive,gen(makevecteur(a,b,c),_SEQ__VECT));
  }

  gen _derive(const gen & args,GIAC_CONTEXT){
    if (args.type==_STRNG && args.subtype==-1) return  args;
    if (is_equal(args))
      return apply_to_equal(args,_derive,contextptr);
#ifndef NSPIRE
    if (calc_mode(contextptr)==1 && args.type!=_VECT)
      return _derive(makesequence(args,ggb_var(args)),contextptr);
#endif
    vecteur v;
    if (args.type==_VECT && args.subtype==_POLY1__VECT)
      return gen(derivative(*args._VECTptr),_POLY1__VECT);
    if (args.type==_VECT)
      v=plotpreprocess(gen(*args._VECTptr,_SEQ__VECT),contextptr);
    else
      v=plotpreprocess(makesequence(args,vx_var),contextptr);
    if (v.size()>1 && v[1].is_symb_of_sommet(at_unquote))
      v[1]=eval(v[1],1,contextptr);
    if (is_undef(v))
      return v;
    if (step_infolevel(contextptr) && v.size()==2 && v[0].type==_SYMB)
      gprintf(step_derive_header,gettext("===== Derive %gen with respect to %gen ====="),makevecteur(v[0],v[1]),contextptr);
    gen var,res;
    if (args.type!=_VECT && is_algebraic_program(v[0],var,res)){
      if (var.type==_VECT && var.subtype==_SEQ__VECT && var._VECTptr->size()==1)
	var=var._VECTptr->front();
      res=derive(res,var,contextptr);
      return symbolic(at_program,makesequence(var,0,res));
    }
    int s=int(v.size());
    if (s==2){
      if (v[1].type==_VECT && v[1].subtype==_SEQ__VECT){
	vecteur & w=*v[1]._VECTptr;
	int ss=int(w.size());
	gen res=v[0];
	for (int i=0;i<ss;++i)
	  res=ratnormal(derive(res,w[i],contextptr),contextptr);
	return res;
      }
      if (v[0].type==_SPOL1){
	sparse_poly1 res=*v[0]._SPOL1ptr;
	sparse_poly1::iterator it=res.begin(),itend=res.end();
	for (;it!=itend;++it){
	  gen e=it->exponent;
	  it->coeff=it->coeff*e;
	  it->exponent=e-1;
	}
	return res;
      }
      if (args.type!=_VECT && v[0].type==_VECT && v[0].subtype==_POLY1__VECT)
	return gen(derivative(*v[0]._VECTptr),_POLY1__VECT);
      return derive(v[0],v[1],contextptr);
    }
    if (s==3 && (v[2].type==_INT_ || (v[2].type==_VECT && v[2].subtype!=_SEQ__VECT)) )
      return derive( v[0],v[1],v[2],contextptr);    
    if (s<3)
      return gensizeerr(contextptr);
    if (s>=3 && v.back().is_symb_of_sommet(at_equal)){
      gen v_=gen(vecteur(v.begin(),v.end()-1),_SEQ__VECT);
      v_=_derive(v_,contextptr);
      return _subst(makesequence(v_,v.back()),contextptr);
    }
    const_iterateur it=v.begin()+1,itend=v.end();
    res=v[0];
    for (;it!=itend;++it)
      res=ratnormal(_derive(gen(makevecteur(res,*it),_SEQ__VECT),contextptr),contextptr);
    return res;
  }
  // "unary" version
  gen step_derive(const gen & args,GIAC_CONTEXT){
    if (step_infolevel(contextptr))
      ++step_infolevel(contextptr);
    gen res;
#ifndef NO_STDEXCEPT
    try {
      res=_derive(args,contextptr);
    } catch (std::runtime_error & e){
      res=string2gen(e.what(),false);
      res.subtype=-1;
    }
#else
    res=_derive(args,contextptr);
#endif
    if (step_infolevel(contextptr))
      --step_infolevel(contextptr);
    return res;
  }
  static const char _derive_s []="diff";
  static string printasderive(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT){
      if (need_parenthesis(feuille))
	return "("+feuille.print()+")'";
      return feuille.print()+"'";
    }
    return sommetstr+("("+feuille.print(contextptr)+")");
  }
  static string texprintasderive(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT)
      return gen2tex(feuille,contextptr)+"'";
    return "\\frac{\\partial \\left("+gen2tex(feuille._VECTptr->front(),contextptr)+"\\right)}{\\partial "+gen2tex(feuille._VECTptr->back(),contextptr)+"}";
  }
  static define_unary_function_eval4_quoted (__derive,&step_derive,_derive_s,printasderive,texprintasderive);
  define_unary_function_ptr5( at_derive ,alias_at_derive,&__derive,_QUOTE_ARGUMENTS,true);

  gen _grad(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    if (args._VECTptr->size()==3){
      gen opt=args._VECTptr->back();
      if (opt.is_symb_of_sommet(at_equal) && (opt._SYMBptr->feuille[0]==at_coordonnees || (opt._SYMBptr->feuille[0].type==_INT_ && opt._SYMBptr->feuille[0].val==_COORDS))){
	gen coord=(*args._VECTptr)[1];
	gen res=_derive(makesequence(args._VECTptr->front(),coord),contextptr);
	if (res.type==_VECT){
	  vecteur resv=*res._VECTptr;
	  if (opt._SYMBptr->feuille[1]==at_sphere && resv.size()==3){
	    resv[1]=resv[1]/coord[0];
	    resv[2]=resv[2]/(coord[0]*sin(coord[1],contextptr));
	    return resv;
	  }
	  if (opt._SYMBptr->feuille[1]==at_cylindre && resv.size()>=2){
	    resv[1]=resv[1]/coord[0];
	    return resv;
	  }
	}
      }
    }
    if (args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    return _derive(args,contextptr);
  }
  static const char _grad_s []="grad";
  static define_unary_function_eval_quoted (__grad,&_grad,_grad_s);
  define_unary_function_ptr5( at_grad ,alias_at_grad,&__grad,_QUOTE_ARGUMENTS,true);

  gen critical(const gen & g,bool extrema_only,GIAC_CONTEXT){
    gen arg,var;
    if (g.type!=_VECT){
      arg=g;
      var=ggb_var(arg);
    }
    else {
      if (g.subtype!=_SEQ__VECT || g._VECTptr->size()<2)
	return gensizeerr(contextptr);
      arg=g._VECTptr->front();
      var=(*g._VECTptr)[1];
    }
    int savestep=step_infolevel(contextptr);
    gprintf(gettext("===== Critical points for %gen ====="),makevecteur(arg),contextptr);
    step_infolevel(contextptr)=0;
    gen d=_derive(makesequence(arg,var),contextptr);
    gen deq=_equal(makesequence(d,0*var),contextptr);
    // *logptr(contextptr) << "Critical points for "<< arg <<": solving " << deq << " with respect to " << var << endl;
    int c=calc_mode(contextptr);
    calc_mode(0,contextptr);
    gen s=_solve(makesequence(deq,var),contextptr);
    step_infolevel(contextptr)=savestep;
    gprintf(step_extrema1,gettext("Derivative of %gen with respect to %gen is %gen\nSolving %gen with respect to %gen answer %gen"),makevecteur(arg,var,d,deq,var,s.type==_VECT?change_subtype(s,_SEQ__VECT):s),contextptr);
    calc_mode(c,contextptr);
    vecteur ls=lidnt(s);
    for (int i=0;i<int(ls.size());++i){
      if (ls[i]==var || (var.type==_VECT && equalposcomp(*var._VECTptr,ls[i])))
	return gensizeerr("solve error while finding critical points");
    }
    if (s.type==_VECT){
      vecteur res;
      step_infolevel(contextptr)=0;
      gen d2=_derive(makesequence(d,var),contextptr);
      step_infolevel(contextptr)=savestep;
      gprintf(step_extrema2,gettext("Hessian at %gen : %gen"),makevecteur(var,d2),contextptr);
      // *logptr(contextptr) << "Hessian " << d2 << endl;
      vecteur v=*s._VECTptr;
      int vs=int(v.size());
      for (int i=0;i<vs;++i){
	gen g=simplify(subst(d2,var,v[i],false,contextptr),contextptr);
	gprintf(step_extrema3,gettext("Hessian at %gen : %gen"),makevecteur(v[i],g),contextptr);
	// *logptr(contextptr) << "Hessian at " << v[i] << ": " << g << endl;
	if (ckmatrix(g)){
	  g=evalf(g,1,contextptr);
	  if (g.type!=_VECT || !is_numericm(*g._VECTptr,true)){
	    gprintf(step_extrema4,gettext("%gen critical point (unknown type)"),makevecteur(v[i]),contextptr);
	    // *logptr(contextptr) << v[i] << " critical point (unknown type)" << endl;
	    continue;
	  }
	  vecteur w=megvl(*g._VECTptr,contextptr);
	  if (!ckmatrix(w)){
	    gprintf(step_extrema4,gettext("%gen critical point (unknown type)"),makevecteur(v[i]),contextptr);
	    // *logptr(contextptr) << v[i] << " critical point (unknown type)" << endl;
	    continue;
	  }
	  int j=0,ws=int(w.size());
	  for (;j<ws;++j){
	    if (is_zero(w[0][0])){
	      gprintf(step_extrema5,gettext("%gen critical point (0 as eigenvalue) %gen"),makevecteur(v[i],_diag(w,contextptr)),contextptr);
	      // *logptr(contextptr) << v[i] << " critical point (0 as eigenvalue) " << _diag(w,contextptr) << endl;
	      break;
	    }
	    if (is_positive(-w[0][0]*w[j][j],contextptr)){
	      gprintf(step_extrema5,gettext("%gen is a saddle point (2 eigenvalues with opposite sign) %gen"),makevecteur(v[i],_diag(w,contextptr)),contextptr);
	      // *logptr(contextptr) << v[i] << " saddle point (2 eigenvalues with opposite sign) " << _diag(w,contextptr) << endl;
	      break;
	    }
	  }
	  if (j==ws){
	    res.push_back(v[i]);
	    if (is_positive(w[0][0],contextptr)){
	      gprintf(step_extrema6,gettext("%gen is a local minimum %gen"),makevecteur(v[i],_diag(w,contextptr)),contextptr);
	      // *logptr(contextptr) << v[i] << " local minimum " << _diag(w,contextptr) << endl;
	    }
	    else {
	      gprintf(step_extrema6,gettext("%gen is a local maximum %gen"),makevecteur(v[i],_diag(w,contextptr)),contextptr);
	      // *logptr(contextptr) << v[i] << " local maximum " << _diag(w,contextptr) << endl;
	    }
	  }
	  continue;
	} // end multi-dimension
	int d=2;
	gen curd=d2;
	if (is_zero(g)){
	  for (++d;d< NEWTON_DEFAULT_ITERATION;++d){
	    curd=ratnormal(_derive(makesequence(curd,var),contextptr),contextptr);
	    g=simplify(subst(curd,var,v[i],false,contextptr),contextptr);
	    if (!is_zero(g))
	      break;
	  }
	}
	if (d%2==0 && is_strictly_positive(g,contextptr)){
	  gprintf(step_extrema7,gettext("%gen is a local minimum"),makevecteur(v[i]),contextptr);
	  // *logptr(contextptr) << v[i] << " local minimum" << endl;
	  res.push_back(v[i]);
	  continue;
	}
	if (d%2==0 && is_strictly_positive(-g,contextptr)){
	  gprintf(step_extrema7,gettext("%gen is a local maximum"),makevecteur(v[i]),contextptr);
	  // *logptr(contextptr) << v[i] << " local maximum" << endl;
	  res.push_back(v[i]);
	  continue;
	}
	if (d==NEWTON_DEFAULT_ITERATION){
	  gprintf(step_extrema4,gettext("%gen is a critical point (unknown type)"),makevecteur(v[i]),contextptr);
	  //*logptr(contextptr) << v[i] << " critical point (unknown type)" << endl;
	}
	else {
	  gprintf(step_extrema8,gettext("%gen is an inflection point"),makevecteur(v[i]),contextptr);
	  // *logptr(contextptr) << v[i] << " inflection point" << endl;
	}
      }
      if (extrema_only)
	return res;
      else
	return s;
    }
    else
      return s;
  }

#if defined USE_GMP_REPLACEMENTS || defined GIAC_GGB
  gen _extrema(const gen & g,GIAC_CONTEXT){
    return critical(g,true,contextptr);
  }
  static const char _extrema_s []="extrema";
  static define_unary_function_eval_quoted (__extrema,&_extrema,_extrema_s);
  define_unary_function_ptr5( at_extrema ,alias_at_extrema,&__extrema,_QUOTE_ARGUMENTS,true);
#endif

  gen _critical(const gen & g,GIAC_CONTEXT){
    return critical(g,false,contextptr);
  }
  static const char _critical_s []="critical";
  static define_unary_function_eval_quoted (__critical,&_critical,_critical_s);
  define_unary_function_ptr5( at_critical ,alias_at_critical,&__critical,_QUOTE_ARGUMENTS,true);

  // FIXME: This should not use any temporary identifier
  // Should define the identity operator and write again all rules here
  // NB: It requires all D operators for functions to be functions!
  gen _function_diff(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.is_symb_of_sommet(at_function_diff)){
      gen & f = g._SYMBptr->feuille;
      return symbolic(at_of,makesequence(gen(symbolic(at_composepow,makesequence(at_function_diff,2))),f));
    }
    if (g.is_symb_of_sommet(at_of)){
      gen & f = g._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2){
	gen & f1=f._VECTptr->front();
	gen & f2=f._VECTptr->back();
	if (f1.is_symb_of_sommet(at_composepow)){
	  gen & f1f=f1._SYMBptr->feuille;
	  if (f1f.type==_VECT && f1f._VECTptr->size()==2 && f1f._VECTptr->front()==at_function_diff){
	    return symbolic(at_of,makesequence(gen(symbolic(at_composepow,makesequence(at_function_diff,f1f._VECTptr->back()+1))),f2));
	  }
	}
      }
    }
    identificateur _tmpi(" _x");
    gen _tmp(_tmpi);    
    gen dg(derive(g(_tmp,contextptr),_tmp,contextptr));
    if (lop(dg,at_derive).empty()){
      identificateur tmpi(" x");
      gen tmp(tmpi);
      gen res=symb_program(tmp,zero,quotesubst(dg,_tmp,tmp,contextptr),contextptr);
      return res;
    }
    return symbolic(at_function_diff,g);
  }
  static const char _function_diff_s []="function_diff";
  static define_unary_function_eval (__function_diff,&_function_diff,_function_diff_s);
  define_unary_function_ptr5( at_function_diff ,alias_at_function_diff,&__function_diff,0,true);

  static const char _fonction_derivee_s []="fonction_derivee";
  static define_unary_function_eval (__fonction_derivee,&_function_diff,_fonction_derivee_s);
  define_unary_function_ptr5( at_fonction_derivee ,alias_at_fonction_derivee,&__fonction_derivee,0,true);

  gen _implicit_diff(const gen & args,GIAC_CONTEXT){
    if (is_undef(args)) return args;
    if (args.type!=_VECT || (args._VECTptr->size()!=3 && args._VECTptr->size()!=4))
      return gensizeerr(contextptr);
    int ndiff=1;
    if (args._VECTptr->size()==4){
      gen g=args._VECTptr->back();
      if (!is_integral(g) || g.type!=_INT_ || g.val<1)
	return gensizeerr(contextptr);
      ndiff=g.val;
    }
    gen eq(remove_equal(args._VECTptr->front())),x((*args._VECTptr)[1]),y((*args._VECTptr)[2]);
    gen yprime=-derive(eq,x,contextptr)/derive(eq,y,contextptr);
    if (ndiff==1)
      return yprime;
    gen yn=yprime;
    for (int n=2;n<=ndiff;++n){
      yn=ratnormal(derive(yn,x,contextptr)+derive(yn,y,contextptr)*yprime,contextptr);
    }
    return yn;
  }
  static const char _implicit_diff_s []="implicit_diff";
  static define_unary_function_eval (__implicit_diff,&_implicit_diff,_implicit_diff_s);
  define_unary_function_ptr5( at_implicit_diff ,alias_at_implicit_diff,&__implicit_diff,0,true);

  // mode==0 for domain, ==1 for singular values
  void domain(const gen & f,const gen & x,vecteur & eqs,vecteur &excluded,int mode,GIAC_CONTEXT){
    vecteur v=lvarxwithinv(f,x,contextptr);
    lvar(f,v);
    for (int i=0;i<int(v.size());++i){
      gen g=v[i];
      if (g.is_symb_of_sommet(at_nop))
	g=g._SYMBptr->feuille;
      if (is_constant_wrt(g,x,contextptr))
	continue;
      if (g.type!=_SYMB)
	continue;
      gen gf=g._SYMBptr->feuille;
      domain(gf,x,eqs,excluded,mode,contextptr);
      unary_function_ptr & u=g._SYMBptr->sommet;
      if (u==at_inv || u==at_Ei || (mode==1 && (u==at_ln || u==at_log10 || u==at_Ci))){
	excluded=mergevecteur(excluded,gen2vecteur(_solve(makesequence(symb_equal(gf,0),x),contextptr)));
	continue;
      }
      if (u==at_pow){
	if (mode==1){
	  excluded=mergevecteur(excluded,gen2vecteur(_solve(makesequence(symb_equal(gf[0],0),x),contextptr)));
	  continue;
	}
	if (is_greater(gf[1],0,contextptr))
	  eqs.push_back(symb_superieur_egal(gf[0],0));
	else
	  eqs.push_back(symb_superieur_strict(gf[0],0));
	continue;
      }
      if (u==at_ln || u==at_log10 || u==at_Ci){
	eqs.push_back(symb_superieur_strict(gf,0));
	continue;
      }
      if (u==at_acosh){
	if (mode==1)
	  excluded=mergevecteur(excluded,gen2vecteur(_solve(makesequence(symb_equal(gf,0),x),contextptr)));
	else
	  eqs.push_back(symb_superieur_egal(gf,1));
	continue;
      }
      if (u==at_asin || u==at_acos || u==at_atanh){
	if (mode==1)
	  excluded=mergevecteur(excluded,gen2vecteur(_solve(makesequence(symb_equal(pow(gf,2,contextptr),0),x),contextptr)));
	else
	  eqs.push_back(symb_inferieur_egal(pow(gf,2,contextptr),1));
	continue;
      }
      if (u==at_tan){
	excluded=mergevecteur(excluded,gen2vecteur(_solve(makesequence(symb_equal(symb_cos(gf),0),x),contextptr)));
	continue;
      }
      if (u==at_sin || u==at_cos || u==at_exp || u==at_atan)
	continue;
      if (u==at_sinh || u==at_cosh || u==at_tanh)
	continue;
      if (u==at_floor || u==at_ceil || u==at_round || u==at_abs || u==at_sign || u==at_max || u==at_min)
	continue;
      *logptr(contextptr) << g << " function not supported, doing like if it was defined" << endl;
    }
  }
  gen domain(const gen & f,const gen & x,int mode,GIAC_CONTEXT){
    // domain of expression f with respect to variable x
    if (x.type!=_IDNT){
      gen domainx(identificateur("domainx"));
      return domain(subst(f,x,domainx,false,contextptr),domainx,mode,contextptr);
    }
    vecteur eqs,excluded,res;
    bool b=complex_mode(contextptr);
    complex_mode(false,contextptr);
#ifndef NO_STDEXCEPT
    try {
#endif
      domain(f,x,eqs,excluded,mode,contextptr);
      res=gen2vecteur(_solve(makesequence(eqs,x),contextptr));
#ifndef NO_STDEXCEPT
    } catch (std::runtime_error & e ) { *logptr(contextptr) << e.what() << endl;}
#endif
    complex_mode(b,contextptr);
    comprim(excluded);
    if (mode==1)
      return excluded;
    if (excluded.empty())
      return res.size()==1?res.front():res;
    vecteur tmp;
    for (int i=0;i<int(excluded.size());++i){
      tmp.push_back(symbolic(at_different,makesequence(x,excluded[i])));
    }
    if (res.size()==1 && res.front()==x)
      return tmp.size()==1?tmp.front():symbolic(at_and,gen(tmp,_SEQ__VECT));
    else {
      // check if excluded values are solutions inside res
      for (int i=0;i<int(res.size());++i){
	for (int j=0;j<int(excluded.size());++j){
	  gen resi=subst(res[i],x,excluded[j],false,contextptr);
	  resi=eval(resi,1,contextptr);
	  if (is_zero(resi))
	    continue;
	  if (!res[i].is_symb_of_sommet(at_and)){
	    res[i]=symbolic(at_and,makesequence(res[i],tmp));
	    continue;
	  }
	  vecteur v=gen2vecteur(res[i]._SYMBptr->feuille);
	  v.push_back(tmp[j]);
	  res[i]=symbolic(at_and,gen(v,_SEQ__VECT));
	}
      }
      return res;
    }
    // not reached
    if (res.size()==1){
      tmp.insert(tmp.begin(),res.front());
      return symbolic(at_and,gen(tmp,_SEQ__VECT));
    }
    tmp.insert(tmp.begin(),symbolic(at_ou,gen(res,_SEQ__VECT)));
    return symbolic(at_and,gen(tmp,_SEQ__VECT));
  }
  gen _domain(const gen & args,GIAC_CONTEXT){
    if (is_undef(args)) return args;
    if (args.type!=_VECT || args.subtype!=_SEQ__VECT)
      return domain(args,vx_var,0,contextptr);
    vecteur v=*args._VECTptr;
    if (v.size()<2)
      return gensizeerr(contextptr);
    if (is_integral(v[1]))
      v.insert(v.begin()+1,vx_var);
    if (v.size()==2)
      v.push_back(0);
    if (v[2].type!=_INT_)
      return gensizeerr(contextptr);
    return domain(v[0],v[1],v[2].val,contextptr);
  }
  static const char _domain_s []="domain";
  static define_unary_function_eval (__domain,&_domain,_domain_s);
  define_unary_function_ptr5( at_domain ,alias_at_domain,&__domain,0,true);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
