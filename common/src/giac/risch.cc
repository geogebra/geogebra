// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c risch.cc -DHAVE_CONFIG_H -DIN_GIAC" -*-
#include "giacPCH.h"
/*
 *  Copyright (C) 2003,7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#include "misc.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  static bool in_risch(const gen & e,const identificateur & x,const vecteur & v,const gen & allowed_lnarg,gen & prim,gen & lncoeff,gen & remains_to_integrate,GIAC_CONTEXT);
  static bool risch_poly_part(const vecteur & e,int shift,const identificateur & x,const vecteur & v,const gen & allowed_lnarg,gen & prim,gen & lncoeff,gen & remains_to_integrate,GIAC_CONTEXT);
  static bool risch_desolve(const gen & f,const gen & g,const identificateur & x,const vecteur & v,gen & y,bool f_is_derivative,GIAC_CONTEXT);

  // returns true & the tower of extension if g is elementary 
  // false otherwise
  static bool risch_tower(const identificateur & x,gen &g, vecteur & v,GIAC_CONTEXT){
    g=tsimplify(pow2expln(g,x,contextptr),contextptr);
    v=rlvarx(g,x);
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (*it==x)
	continue;
      if (!it->is_symb_of_sommet(at_exp) && (!it->is_symb_of_sommet(at_ln)) )
	return false;
    }
    reverse(v.begin(),v.end()); // most complex var at the beginning
    return true;
  }

  // Compute the derivative of a poly or fraction
  // The derivative wrt to x_i (i-th index) is the i-th element of v
  // The last element of v should normally be 1 (derivative of x)
  static fraction diff(const polynome & f,const vecteur & v){
    int s=v.size();
    if (f.dim<s)
      return fraction(gensizeerr(gettext("Risch diff dimension")));
    fraction res(zero);
    std::vector< monomial<gen> >::const_iterator it=f.coord.begin(),itend=f.coord.end();
    for (;it!=itend;++it){
      index_t i= it->index.iref();
      fraction tmp(zero);
      for (int n=0;n<s;++n){
	if (i[n]){
	  --i[n];
	  if (v[n].type==_FRAC)
	    tmp=tmp+gen(polynome(monomial<gen>(i[n]+1,i)))*(*v[n]._FRACptr);
	  else
	    tmp=tmp+fraction(gen(polynome(monomial<gen>(i[n]+1,i)))*v[n]);
	  ++i[n];
	}
      }
      res=res+it->value*tmp;
    }
    return res;
  }

  
  static polynome rothstein_trager_resultant(const polynome & num,const polynome & den,const vecteur & vl,polynome & p1,GIAC_CONTEXT){
    int s=num.dim;
    fraction denprime=diff(den,vl);
    if (is_undef(denprime.num)){
      return gen2poly(denprime.num,s);
    }
    p1=num*gen2poly(denprime.den,s);
    p1=p1.untrunc1();
    polynome p2(monomial<gen>(plus_one,1,s+1));
    p2=p2*gen2poly(denprime.num,s).untrunc1();
    p1=p1-p2;
    p2=den.untrunc1();
    // exchange var 1 (parameter t) and 2 (top tower variable)
    vector<int> i=transposition(0,1,s+1);
    p1.reorder(i);
    // Change sign of p1 if first coeff is negative
    if (is_positive(-p1.coord.front().value,context0)) // ok
      p1=-p1;
    p2.reorder(i);
    polynome pres=Tresultant<gen>(p1,p2),pcontent(s);
    p1.reorder(i);
    return pres.trunc1(); // pres top var is the parameter t
  }

  /*
  fraction diff(const fraction & f,const vecteur & v){
    if (f.num.type!=_POLY){
      if (f.den.type!=_POLY)
	return zero;
      return -diff(*f.den._POLYptr,v)*fraction(f.num,f.den*f.den);
    }
    polynome & num = *f.num._POLYptr;
    if (f.den.type!=_POLY)
      return diff(*f.num._POLYptr,v)/f.num;
    polynome & den = *f.den._POLYptr;
    return diff(num,v)*f.den-diff(den,v)*fraction(f.num,f.den*f.den);
  }
  */

  // diff(n/d*exp(a*x))
  static bool diff(const polynome & n,const polynome & d,const gen & a,const vecteur & v,polynome & resn,polynome & resd){
    int s=n.dim;
    fraction nprime(diff(n,v)),dprime(diff(d,v));
    if (is_undef(nprime.num) || is_undef(dprime.num))
      return false;
    polynome nn(gen2polynome(nprime.num,s)),nd(gen2polynome(nprime.den,s));
    polynome dn(gen2polynome(dprime.num,s)),dd(gen2polynome(dprime.den,s));
    // ((n/d)*exp(a*x))'=(n'*d-d'*n)/d^2+a*n/d = (nn/nd*d-dn/dd*n)/d^2+a*n/d
    resn = nn*d*dd-dn*n*nd;
    resd = d*d*dd*nd;
    if (!is_zero(a)){
      // resn/resd + a*n/d = (resn*aden*d+resd*anum*n)/(d*resd)
      gen num,den;
      fxnd(a,num,den);
      polynome anum=gen2poly(num,resd.dim),aden=gen2poly(den,resd.dim);
      resn = resn*aden*d + resd*anum*n;
      resd = resd*d*aden;
    }
    simplify(resn,resd);
    return true;
  }


  static bool rischde_simplify(polynome & R,polynome & S, polynome & T){
    polynome lcmdeno=gcd(gcd(R,S),T);
    R=R/lcmdeno;
    S=S/lcmdeno;
    T=T/lcmdeno;
    // if gcd(R,S) does not divide T then there is no solution
    lcmdeno=gcd(R,S);
    return lcmdeno.lexsorted_degree()==0;
  }

  static bool spde_x(const polynome & S0,const polynome & R0,const polynome & T0,const vecteur & vdiff,polynome & N1,polynome & N2){ // Solve S*N+R*N'=T, R constant poly
    gen con=gcd(Tcontent(S0),gcd(Tcontent(R0),Tcontent(T0)));
    polynome S(S0/con),R(R0/con),T(T0/con);
    int s=T.dim;
    if (T.coord.empty()){
      N1=T;
      N2=gen2poly(plus_one,s);
      return true;
    }
    if (T.lexsorted_degree()<S.lexsorted_degree())
      return false;
    polynome quo(s),rem(s),a(s);
    T.TPseudoDivRem(S,quo,rem,a); 
    // a*T=quo*S+rem -> quo/a is the quotient T/S, 
    // it has the same high degree polynomial part as N
    // We set N=quo/a+ M, where M satisfies
    // S*M+R*M'=T-S*(quo/a)-R*(quo/a)'
    // or a*dNden*(S*M+R*M')= a*dNden*(T-S*(quo/a)-R*(quo/a)')
    // Let R*(quo/a)'=dNnum/dNden
    // newS=a*dNden*S, newR=a*dNden*R, newT=a*dNden*T-S*quo*dNden-a*dNnum
    fraction quo_a=fraction(quo,a).normal();
    // fraction dN(diff(quo,vdiff)/a-(diff(a,vdiff)*quo)/(a*a));
    fraction tmp1(diff(quo,vdiff));
    tmp1.den=a*tmp1.den;
    fraction tmp2(diff(a,vdiff));
    if (is_undef(tmp1.num) || is_undef(tmp2.num))
      return false;
    tmp2.num=tmp2.num*quo;
    tmp2.den=tmp2.den*a*a;
    fraction dN((tmp1-tmp2).normal());
    polynome dNnum(R*gen2poly(dN.num,s)),dNden(gen2poly(dN.den,s));
    polynome newT(T*dNden*a-quo*S*dNden-a*dNnum);
    if (!spde_x(a*dNden*S,a*dNden*R,newT,vdiff,N1,N2))
      return false;
    // M=N1/N2 hence N=quo/a+M=quo/a+N1/N2
    fraction Nres=quo_a+fraction(N1,N2);
    N1=gen2poly(Nres.num,s);
    N2=gen2poly(Nres.den,s);
    return true;
  }

  static bool SPDE(const polynome &R0,const polynome & S0,const polynome & T0,const identificateur & x,const vecteur & v,const vecteur & vdiff,const vecteur & lv,int ydeg, gen & prim,GIAC_CONTEXT){
    // SPDE algorithm to reduce R to a constant polynomial wrt to Z
    // this will also reduce ydeg, initial equation is  Ry'+Sy=T
    // Principe: if degree(R)>0, find U and V s.t. RU+SV=T and deg(V)<deg(R)
    // Then y = V + R*z and y'=U - S*z
    // hence V'+Rz'+R'z=U-S*z -> R z' + (R' - S) z = U - V'
    if (T0.coord.empty()){
      prim=zero;
      return true;
    }
    polynome R(R0),S(S0),T(T0);
    int s=lv.size();
    if (ydeg<0 || !rischde_simplify(R,S,T))
      return false;
    int r=R.lexsorted_degree();
    if (R.lexsorted_degree()){
      polynome U(s),V(s),c(s);
      // U,V / R*U+S*V=c*T, c cst wrt main var, degV<degR
      Tabcuv<gen>(S,R,T,V,U,c); 
      fraction dR(diff(R,vdiff)),dV(diff(V,vdiff));
      if (is_undef(dR.num)||is_undef(dV.num))
	return false;
      dV.den=dV.den*c;
      fraction tmpfrac=fraction(V,c*c)*diff(c,vdiff);
      if (is_undef(tmpfrac.num))
	return false;
      dV=dV-tmpfrac; // now dV=(V/c)'
      polynome dRnum(gen2poly(dR.num,s)),dRden(gen2poly(dR.den,s)),dVnum(gen2poly(dV.num,s)),dVden(gen2poly(dV.den,s));
      polynome newR(R*dRden*dVden),newS((S*dRden+dRnum)*dVden),newT((((U*dVden)/c)-dVnum)*dRden);
      ydeg=ydeg-r;
      if(!SPDE(newR,newS,newT,x,v,vdiff,lv,ydeg,prim,contextptr))
	return false;
      prim=r2sym(R,lv,contextptr)*prim+r2sym(V,lv,contextptr)/r2sym(c,lv,contextptr);
      return true;
    }
    // degree(R)=0, Final resolution  
    gen Z=v.front();
    if (Z==x || S.lexsorted_degree()){ // S*N+N'=T
      polynome N1,N2;
      if (!spde_x(S,R,T,vdiff,N1,N2))
	return false;
      prim=r2sym(N1,lv,contextptr)/r2sym(N2,lv,contextptr);
      return true;
    }
    vecteur v1(v.begin()+1,v.end());
    vecteur lv1(lv.begin()+1,lv.end());
    vecteur t=polynome2poly1(T,1);
    t=*r2sym(t,lv1,contextptr)._VECTptr;
    gen rr=r2sym(R,lv,contextptr);
    t=divvecteur(t,rr);
    if (S.coord.empty()){ // solve y'=T -> polynomial part with 1 less var 
      gen lncoeff,remains;
      return risch_poly_part(t,0,x,v1,plus_one,prim,lncoeff,remains,contextptr);
    }
    // for each power solve a risch de with 1 less var
    if (ydeg<signed(t.size())-1 || Z.type!=_SYMB)
      return false;
    // t=mergevecteur(vecteur(ydeg-t.size()-1),t);
    gen z=Z._SYMBptr->feuille;
    gen dz=derive(z,x,contextptr);
    if (is_undef(dz))
      return false;
    gen b=r2sym(S,lv,contextptr)/rr;
    int tdeg=t.size()-1;
    gen previous,sol;
    bool ok;
    for (int k=tdeg;k>=0;--k){
      if (Z.is_symb_of_sommet(at_exp))
	ok=risch_desolve(k*dz+b,t[tdeg-k],x,v1,sol,false,contextptr);
      else
	ok=risch_desolve(b,t[tdeg-k]-(k+1)*previous*dz/z,x,v1,sol,false,contextptr);
      if (!ok)
	return false;
      prim=prim+sol*pow(Z,k);
      previous=sol;
    }
    return true;
  }

  // solve y'+f*y=g in rational fraction of v
  // returns true and y or false
  static bool risch_desolve(const gen & f,const gen & g,const identificateur & x,const vecteur & v,gen & y,bool f_is_derivative,GIAC_CONTEXT){
    gen Z=v.front();
    int s=v.size();
    vecteur lv(lvar(v));
    vecteur v1(v.begin()+1,v.end());
    vecteur vdiff(s);
    for (int i=0;i<s;++i){
      vdiff[i]=derive(v[i],x,contextptr);
      if (is_undef(vdiff[i]))
	return false;
    }
    lvar(vdiff,lv);
    lvar(f,lv);
    lvar(g,lv);
    vecteur lv1(lv.begin()+1,lv.end());
    int ss=lv.size();
    for (int i=0;i<s;++i)
      vdiff[i]=sym2r(vdiff[i],lv,contextptr);
    // identificateur Zi(" Z");
    // gen Ze(Zi);
    // gen fZ=quotesubst(f,Z,Ze);
    fraction ff(sym2r(f,lv,contextptr));
    polynome fden(gen2poly(ff.den,ss)),fnum(gen2poly(ff.num,ss)),D(gen2poly(plus_one,ss));
    polynome fdenred(fden);
    // compute denominator of y
    fraction gg(sym2r(g,lv,contextptr));
    polynome gden(gen2poly(gg.den,ss)),gnum(gen2poly(gg.num,ss));
    polynome gdenred(gden);
    if (Z.is_symb_of_sommet(at_exp)){
      // if Z is an exp, eliminate it in fden/gden and compute Z in D
      int fdenv=fden.valuation(0),fnumv=fnum.valuation(0),gdenv=gden.valuation(0),gnumv=gnum.valuation(0);
      index_t ii(ss);
      ii[0]=-fdenv;
      fdenred=fdenred.shift(ii);
      ii[0]=-gdenv;
      gdenred=gdenred.shift(ii);
      int alpha=0,beta=fnumv-fdenv,gamma=gnumv-gdenv;
      if (gamma<0){
	alpha=gamma;
	if (beta<=0){
	  if (beta!=0)
	    alpha=giacmin(0,gamma-beta);
	  else { // possible cancellation case depend of cst coeff of f
	    vecteur vtmp(polynome2poly1(fnum,1));
	    gen f0=r2sym(vtmp[0],lv1,contextptr);
	    vtmp=polynome2poly1(fdenred,1);
	    f0=f0/r2sym(vtmp[0],lv1,contextptr);
	    gen lnc,prim,remains;
	    if (in_risch(f0,x,v1,Z._SYMBptr->feuille,prim,lnc,remains,contextptr)&&lnc.type==_INT_)
	      alpha=giacmin(lnc.val,alpha);
	  }
	}
      }
      D=polynome(monomial<gen>(plus_one,-alpha,1,ss)); // Z^(-alpha)
    }
    if (!f_is_derivative){ 
      // Fixme: eliminate residues in fden -> new fdenred
      // Find degree 1 factors of fdenred
      polynome tmpy(fdenred.derivative()),tmpw(fdenred);
      polynome tmpc(simplify(tmpy,tmpw));
      tmpy=tmpy-tmpw.derivative();
      polynome f1=simplify(tmpw,tmpy);
      if (f1.lexsorted_degree()){ /// FIXME
	polynome f1cofact(fdenred/f1);
	polynome N1(ss),N2(ss),c(ss);
	Tabcuv<gen>(f1,f1cofact,fnum,N1,N2,c); // fnum/fdenred=N2/f1+...
	// find resultant_Z(N-t f1' , f1)
	polynome p1(ss);
	polynome pres=rothstein_trager_resultant(N2,f1,vdiff,p1,contextptr);
	// for each negative integer root of pres, multiply D
	// find linear factors of pres -> FIXME does not work
	factorization vden;
	gen extra_div=1;
	factor(pres,N1,vden,false,false,false,1,extra_div);
	factorization::const_iterator f_it=vden.begin(),f_itend=vden.end();
	// bool ok=true;
	for (;f_it!=f_itend;++f_it){
	  int deg=f_it->fact.lexsorted_degree();
	  if (deg!=1)
	    continue;
	  // extract the root
	  vecteur vtmp=polynome2poly1(f_it->fact,1);
	  gen root=-r2sym(vtmp.back()/vtmp.front(),lv1,contextptr);
	  if (root.type==_INT_ && root.val<0){
	    identificateur t(" t");
	    gen tmp1=r2sym(p1,mergevecteur(vecteur(1,t),lv),contextptr);
	    tmp1=subst(tmp1,t,root,false,contextptr);
	    polynome p1subst(gen2poly(sym2r(tmp1,lv,contextptr),ss));
	    p1subst=gcd(p1subst,f1);
	    D=D*pow(p1subst,-root.val);
	  }
	}
      }
    }
    polynome c(gcd(fdenred,gdenred));
    D=D*gcd(gdenred,gdenred.derivative())/gcd(c,c.derivative());
    // y'+f*y=g -> new equation is Ry'+Sy=T, compute R=D,S=fD-D',T=gD^2
    fraction dD(diff(D,vdiff));
    if (is_undef(dD.num))
      return false;
    polynome dDnum(gen2poly(dD.num,ss)),dDden(gen2poly(dD.den,ss));
    // then multiply by the lcm of denominators of S and T
    // simplify by gcd of R, S, T
    polynome lcmdeno(gden*fden/gcd(gden,fden)*dDden);
    polynome R(D*lcmdeno),S(fnum*(lcmdeno/fden)*D-dDnum*(lcmdeno/dDden)),T(D*D*gnum*(lcmdeno/gden));
    if (!rischde_simplify(R,S,T))
      return false;
    int rd=R.lexsorted_degree();
    int sd=S.lexsorted_degree();
    int td=T.lexsorted_degree();
    polynome Rr(Tfirstcoeff<gen>(R)),Ss(Tfirstcoeff<gen>(S));
    // compute max possible degree of y: it depends on Z type
    int ydeg=td-sd;
    gen expshift=plus_one; // multiplicative change of variable
    if (Z==x){
      ydeg=td-giacmax(rd-1,sd);
      if (rd-1==sd){ // test whether S_s/R_r is an integer
	gen n=Ss.coord.front().value/Rr.coord.front().value;
	if (n.type==_INT_ && n.val>ydeg && (Ss-n*Rr).coord.empty())
	  ydeg=giacmax(ydeg,n.val);
      }
    }
    else {
      if (Z.type!=_SYMB)
	return false;
      gen z=Z._SYMBptr->feuille;
      ydeg=td-giacmax(rd,sd);
      if (Z.is_symb_of_sommet(at_exp)){
	if (rd==sd){ // test whether int S_s/R_r is elementary with n*z coeff
	  gen lnc,prim,remains,tmp=r2sym(Rr,lv,contextptr)/r2sym(Ss,lv,contextptr);
	  if (in_risch(tmp,x,v1,z,prim,lnc,remains,contextptr)&&lnc.type==_INT_)
	    ydeg=giacmax(lnc.val,ydeg);
	}
      }
      else {
	if (rd==sd+1){
	  gen lnc,prim,remains,tmp=r2sym(Rr,lv,contextptr)/r2sym(Ss,lv,contextptr);
	  // test whether int S_s/R_r is elementary with exp also elementary
	  if (in_risch(tmp,x,v1,plus_one,prim,lnc,remains,contextptr)){
	    prim=tsimplify(exp(prim,contextptr),contextptr);
	    vecteur lv2(lv);
	    lvar(prim,lv2);
	    if (lv2==lv){ // the exp is elementary, change variables
	      expshift=prim;
	      fraction expshiftf(sym2r(prim,lv,contextptr));
	      polynome expnum(gen2poly(expshiftf.num,ss)),expden(gen2poly(expshiftf.den,ss));
	      R=R*Rr*expden;
	      S=(S*Rr-R*Ss)*expden;
	      // sd=S.lexsorted_degree();
	      T=(T*Rr)*expnum;
	    }
	  }
	}
      }
    }
    bool ok=SPDE(R,S,T,x,v,vdiff,lv,ydeg,y,contextptr);
    y=y*expshift/r2sym(D,lv,contextptr);
    return ok;
  }

  static pf<gen> hermite_reduce(const pf<gen> & p_cst,const gen & a,const vecteur & v_derivatives,const vecteur & lv,gen & prim,GIAC_CONTEXT){
    pf<gen> p(p_cst);
    if (p.mult<=0){
      prim=gensizeerr(gettext("risch.cc/hermite_reduce"));
      return p;
    }
    if (p.mult==1)
      return p_cst;
    gen expax=exp(r2sym(a,lv,contextptr)*lv.front(),contextptr);
    fraction pprime=diff(p.fact,v_derivatives);
    if (is_undef(pprime.num)){
      prim=pprime.num;
      return p;
    }
    int s=lv.size();
    polynome fprime=gen2poly(pprime.num,s),fprimeden=gen2poly(pprime.den,s);
    polynome d(s),u(s),v(s),C(s);
    polynome resnum(s),resden(plus_one,s),numtemp(s),dentemp(s);
    Tegcdpsr(fprime,p.fact,v,u,d); // f*u+f'.num*v=d
    polynome usave(u),vsave(v),pdensave(s);
    // reduce p.den to the cofactor
    p.den=p.den/pow(p.fact,p.mult);
    // now we are integrating p.num/(p.den*f^p.mult)
    while (p.mult>1){
      pdensave=p.den;
      Tegcdtoabcuv(fprime,p.fact,p.num,v,u,d,C); // f*u+f'.num*v=C*p.num
      v=v*fprimeden; // f*u+f'*v=C*p.num
      // p.num/(p.den*f^p.mult)=(f*u+f'*v)/(C*p.den*f^p.mult)
      p.mult--;
      // int(f'/f^(p.mult+1) * v/(C*p.den)*exp(a*x) ) 
      // = 1/p.mult*[-1/f^(p.mult)*v/(C*p.den)*exp(a*x) 
      //   + int(1/f^(p.mult)*(v/C*p.den*exp(a*x))')] 
      // update non integrated term
      if (!diff(v,C*p.den,a,v_derivatives,numtemp,dentemp)){
	prim=gensizeerr(gettext("risch.cc/hermite_reduce"));
	return p;
      }
      dentemp=dentemp*p.mult;
      Tfracadd<gen>(numtemp,dentemp,u,C*p.den,p.num,p.den);
      simplify(p.num,p.den);
      // update integrated term
      pdensave=-C*pdensave;
      TsimplifybyTlgcd(pdensave,v);
      pdensave=pdensave*pow(p.fact,p.mult)*p.mult;
      Tfracadd<gen>(resnum,resden,v,pdensave,numtemp,dentemp);
      resnum=numtemp;
      resden=dentemp;
      // finished?
      if (p.mult==1)
	break;
      // restore Bezout coeffs
      u=usave;
      v=vsave;
    }
    prim=prim+r2sym(resnum,lv,contextptr)/r2sym(resden,lv,contextptr)*expax;
    // restore the factor in p.den
    p.den=p.den*p.fact;
    return pf<gen>(p);
  }

  // int(exp(a*x)*coeff,x) where coeff is a rational fraction wrt x
  // polynomial part: recursive call of int
  // remaining part: reduce to square free denom by int by part
  // partial fraction decomp -> Ei
  // Ei with imaginary arguments -> Ei(i*t)=i*(-pi/2)+i*Si(x)+Ci(x)
  static bool integrate_ei(const gen & a,const gen & coeff,const identificateur & x,gen & prim,gen & remains_to_integrate,GIAC_CONTEXT){
    gen expax=exp(a*x,contextptr),ima,rea=re(a,contextptr);
    bool imaneg;
    if (is_zero(rea)){
      ima=im(a,contextptr);
      imaneg=is_positive(-ima,contextptr);
    }
    vecteur l(1,x);
    lvar(coeff,l);
    lvar(a,l);
    int s=l.size();
    vecteur l1(l.begin()+1,l.end());
    gen r=e2r(coeff,l,contextptr),ar=e2r(a,l,contextptr);
    // cout << "Int " << r << endl;
    gen r_num,r_den;
    fxnd(r,r_num,r_den);
    if (r_num.type==_EXT)
      return false;
    polynome den(gen2poly(r_den,s)),num(gen2poly(r_num,s));
    polynome p_content(lgcd(den));
    // Square-free factorization
    factorization vden(sqff(den/p_content)); 
    vector< pf<gen> > pfdecomp;
    polynome ipnum(s),ipden(s);
    gen ipshift;
    partfrac(num,den,vden,pfdecomp,ipnum,ipden);
    // int( ipnum/ipden*exp(a*x),x)
    int save=calc_mode(contextptr);
    calc_mode(0,contextptr);
    prim += _integrate(gen(makevecteur(expax*r2sym(ipnum/ipden,l,contextptr),x),_SEQ__VECT),contextptr);
    calc_mode(save,contextptr);
    if (is_undef(prim)) return false;
    // Hermite reduction 
    vector< pf<gen> >::iterator it=pfdecomp.begin();
    vector< pf<gen> >::const_iterator itend=pfdecomp.end();
    for (;it!=itend;++it){
      pf<gen> tmp(*it);
      if (it->mult>1){
	vecteur vl(1,1);
	tmp=hermite_reduce(*it,ar,vl,l,prim,contextptr);
	if (is_undef(prim))
	  return false;
      }
      if (is_zero(tmp.num))
	continue;
      // ei part for it->num/it->den
      vecteur itnum=polynome2poly1(tmp.num,1);
      vecteur itden=derivative(polynome2poly1(tmp.den,1));
      factorization vden;
      gen extra_div=1;
      factor(tmp.fact,p_content,vden,false,true,true,1,extra_div); // complex+sqrt ok
      factorization::const_iterator f_it=vden.begin(),f_itend=vden.end();
      gen add_prim;
      // bool ok=true;
      for (;f_it!=f_itend;++f_it){
	int deg=f_it->fact.lexsorted_degree();
	if (!deg)
	  continue;
	if (deg!=1){
	  remains_to_integrate=remains_to_integrate+expax*r2sym(it->num,l,contextptr)/r2sym(it->den,l,contextptr);
	  break;
	}
	// extract the root
	vecteur vtmp=polynome2poly1(f_it->fact,1);
	gen root=-vtmp.back()/vtmp.front();
	gen rootn=horner(itnum,root);
	gen rootd=horner(itden,root);
	root = r2sym(root,l1,contextptr);
	if (is_zero(im(root,contextptr)) && is_zero(rea)){
	  prim += (cos(ima*root,contextptr)+cst_i*sin(ima*root,contextptr))*(r2sym(rootn/rootd,l1,contextptr)*(symbolic(at_Ci,(imaneg?(-ima):ima)*(x-root))+(imaneg?(-cst_i):cst_i)*symbolic(at_Si,(imaneg?(-ima):ima)*(x-root))));
	}
	else
	  prim += r2sym(rootn/rootd,l1,contextptr)*exp(a*root,contextptr)*symbolic(at_Ei,a*(x-root));
      }
    }
    return true;
  }

  // e is assumed to be a (generallized) poly wrt the top var of v
  // shift is 0 for a poly or the max Laurent exponent for a generallized poly
  static bool risch_poly_part(const vecteur & e,int shift,const identificateur & x,const vecteur & v,const gen & allowed_lnarg,gen & prim,gen & lncoeff,gen & remains_to_integrate,GIAC_CONTEXT){
    if (v.size()==1){ // the shift must be 1 for integration of a polynomial
      vecteur tmp=e;
      reverse(tmp.begin(),tmp.end());
      tmp=integrate(tmp,1);
      if (is_undef(tmp))
	return false;
      reverse(tmp.begin(),tmp.end());
      tmp.push_back(zero);
      prim=prim+symb_horner(tmp,x);
      return true;
    } 
    int s=e.size(); // degree is s-1
    vecteur v1(v.begin()+1,v.end());
    gen X=v.front();
    if (X.is_symb_of_sommet(at_ln)){
      gen dX=ratnormal(derive(X,x,contextptr));
      if (is_undef(dX))
	return false;
      // log extension
      vecteur eprim(s+1);
      gen lnc,remains;
      for (int j=s-1;j>0;--j){
	// eprim[j] ' + (j+1) eprim[j+1] * v.front()' = e[j]
	if (!in_risch(e[s-1-j]-(j+1)*eprim[s-1-j]*dX,x,v1,X._SYMBptr->feuille,eprim[s-j],lnc,remains,contextptr)){
	  remains_to_integrate=remains_to_integrate+symb_horner(e,X);
	  return false;
	}
	eprim[s-1-j]=eprim[s-1-j]+rdiv(lnc,j+1,contextptr);
      }
      gen prim_add;
      bool ok=in_risch(e[s-1]-eprim[s-1]*dX,x,v1,zero,prim_add,lnc,remains,contextptr);
      prim=prim+prim_add+symb_horner(eprim,X);
      remains_to_integrate=remains_to_integrate+remains;
      return ok;
    }
    // exp extension: we have to solve a Risch diff equation for each power
    gen prim_add,remains;
    // exp extension
    vecteur eprim(s);
    if (!X.is_symb_of_sommet(at_exp))
      return false;
    gen dY=derive(X._SYMBptr->feuille,x,contextptr);
    if (is_undef(dY))
      return false;
    for (int j=s-1;j>=0;--j){
      if (j+shift==0){
	bool ok=in_risch(e[s-1-j],x,v1,allowed_lnarg,prim_add,lncoeff,remains,contextptr);
	prim=prim+prim_add;
	remains_to_integrate=remains_to_integrate+remains;
	if (!ok &&!is_zero(allowed_lnarg))
	  return ok;      
      }
      else {
	if (!risch_desolve((j+shift)*dY,e[s-1-j],x,v1,eprim[s-1-j],true,contextptr)) {
	  gen coeff=e[s-1-j],pui=j+shift;
	  gen a,b,c,d;
	  if (is_zero(allowed_lnarg)&&is_linear_wrt(X._SYMBptr->feuille,x,a,b,contextptr) && lvarx(coeff,x)==vecteur(1,x) && integrate_ei(pui*a,coeff,x,c,d,contextptr)){
	    // add to prim int(exp(pui*(a*x+b))*coeff) 
	    // expressed with Ei
	    prim += exp(pui*b,contextptr)*c;
	    remains_to_integrate += exp(pui*b,contextptr)*d;
	    continue;
	  }
	  remains_to_integrate=remains_to_integrate+coeff*pow(X,pui,contextptr);
	  eprim[s-1-j]=0;
	  if (!is_zero(allowed_lnarg))
	    return false;
	}
      }
    }
    prim=prim+symb_horner(eprim,X)*pow(X,shift);
    return true;
  }

  // Inner Risch algorithm call, v is the tower extension
  // allowed_lnarg is zero if all ln are allowed or the arg
  // of the allowed ln if only 1 ln is allowed
  // lncoeff will contain this coeff if it's the case
  // prim is the antiderivative
  // returns false if only 1 ln allowed and no elementary integral found 
  static bool in_risch(const gen & e,const identificateur & x,const vecteur & v,const gen & allowed_lnarg,gen & prim,gen & lncoeff,gen & remains_to_integrate,GIAC_CONTEXT){
    prim=zero;
    lncoeff=zero;
    remains_to_integrate= zero;
    int vs=v.size();
    vecteur l(v);
    // add top non-x vars
    lvar(e,l);
    gen diffv=derive(v,x,contextptr);
    if (is_undef(diffv) || diffv.type!=_VECT)
      return false;
    vecteur vx=*diffv._VECTptr;
    lvar(vx,l);
    vecteur vl;
    int s=l.size();
    for (int i=0;i<vs;++i)
      vl.push_back(e2r(vx[i],l,contextptr));
    vecteur l1(l.begin()+1,l.end());
    gen r=e2r(e,l,contextptr);
    // cout << "Int " << r << endl;
    gen r_num,r_den;
    fxnd(r,r_num,r_den);
    if (r_num.type==_EXT){
      remains_to_integrate= e;
      return false;
    }
    polynome den(gen2poly(r_den,s)),num(gen2poly(r_num,s));
    polynome p_content(lgcd(den));
    // Square-free factorization
    // FIXME: if ex[ extension, should always treat pole 0 apart
    factorization vden;
    int zeromult=den.coord.back().index.front();
    if (zeromult){
      index_t sh(den.dim);
      sh[0]=-zeromult;
      polynome dens=den.shift(sh);
      vden=sqff(dens/p_content); 
      vden.push_back(facteur<polynome>(polynome(monomial<gen>(1,1,den.dim)),zeromult));
    }
    else
      vden=sqff(den/p_content); 
    vector< pf<gen> > pfdecomp;
    polynome ipnum(s),ipden(s);
    gen ipshift;
    partfrac(num,den,vden,pfdecomp,ipnum,ipden);
    // Hermite/ln reduction and 0 isolation for exp extensions
    vector< pf<gen> >::iterator it=pfdecomp.begin();
    vector< pf<gen> >::const_iterator itend=pfdecomp.end();
    for (;it!=itend;++it){
      if (v.front().is_symb_of_sommet(at_exp) && it->fact.coord.size()==1){
	// generalized polynomial part, fact must be 1 [1,0,0...]
	index_t i(s);
	i.front()=-it->mult;
	vecteur tmp=polynome2poly1(it->num,1);
	tmp=*r2sym(tmp,l1,contextptr)._VECTptr;
	tmp=divvecteur(tmp,r2sym(it->den.shift(i),l,contextptr));
	if (!risch_poly_part(tmp,-it->mult,x,v,allowed_lnarg,prim,lncoeff,remains_to_integrate,contextptr) && !is_zero(allowed_lnarg))
	  return false;
      }
      else { // Hermite reduce it->num/it->den
	pf<gen> tmp(*it);
	if (it->mult>1){
	  tmp=hermite_reduce(*it,0,vl,l,prim,contextptr);
	  if (is_undef(prim))
	    return false;
	}
	if (is_zero(tmp.num))
	  continue;
	if (!is_zero(allowed_lnarg)){ // if only 1 log allowed
	  if (pfdecomp.size()>1)
	    return false;
	  // compute u'/u, must be a multiple of it->num/it->den
	  lncoeff=normal(allowed_lnarg*r2sym(it->num,l,contextptr)/(derive(allowed_lnarg,x,contextptr)*r2sym(it->den,l,contextptr)),contextptr);
	  return is_zero(derive(lncoeff,x,contextptr));
	}
	// Logarithmic part: compute resultant of num - t * den
	polynome p1(s);
	polynome pres=rothstein_trager_resultant(tmp.num,tmp.den,vl,p1,contextptr);
	// Factorization, should return 1st order factor independant of
	// the tower variables
	factorization vden;
	gen extra_div=1;
	factor(pres,p_content,vden,false,withsqrt(contextptr),true,1,extra_div);
	factorization::const_iterator f_it=vden.begin(),f_itend=vden.end();
	gen add_prim;
	bool ok=true;
	for (;f_it!=f_itend;++f_it){
	  int deg=f_it->fact.lexsorted_degree();
	  if (!deg)
	    continue;
	  if (deg!=1){
	    ok=false;
	    remains_to_integrate=remains_to_integrate+r2sym(it->num,l,contextptr)/r2sym(it->den,l,contextptr);
	    break;
	  }
	  // extract the root
	  vecteur vtmp=polynome2poly1(f_it->fact,1);
	  gen root=-r2sym(vtmp.back()/vtmp.front(),l1,contextptr);
	  if (!is_zero(derive(root,x,contextptr))){
	    ok=false;
	    // remains_to_integrate=remains_to_integrate+r2sym(it->num,l,contextptr)/r2sym(it->den,l,contextptr); 
	    remains_to_integrate=remains_to_integrate+r2sym(tmp.num,l,contextptr)/r2sym(tmp.den,l,contextptr);
	    break;
	  }
	  identificateur t(" t");
	  gen tmp1=r2sym(p1,mergevecteur(vecteur(1,t),l),contextptr);
	  tmp1=subst(tmp1,t,root,false,contextptr);
	  gen tmparg=_gcd(makevecteur(recursive_normal(tmp1,contextptr),recursive_normal(r2sym(it->fact,l,contextptr),contextptr)),contextptr); 
	  add_prim=add_prim+root*ln(tmparg,contextptr);
	  // If tmparg is of maximal degree, this will change the integral
	  // part of the fraction by root*_quo(tmparg',tmparg,X)
	  ipshift=ipshift-root*_quo(makevecteur(derive(tmparg,x,contextptr),tmparg,recursive_normal(v.front(),contextptr)),contextptr);
	  if (is_undef(ipshift))
	    return false;
	}
	if (ok)
	  prim=prim+add_prim;
      } // end else case (non 0 pole, ie Hermite reduction)
    } // end for (all denominateurs)
    // ipnum/ipden = the polynomial part -> integrate it
    simplify(ipnum,ipden);
    vecteur tmp=polynome2poly1(ipnum,1);
    tmp=*r2sym(tmp,l1,contextptr)._VECTptr;
    tmp=divvecteur(tmp,r2sym(ipden,l,contextptr));
    if (tmp.empty())
      tmp=vecteur(1,ipshift);
    else
      tmp[0]=tmp[0]+ipshift;
    if (!risch_poly_part(tmp,0,x,v,allowed_lnarg,prim,lncoeff,remains_to_integrate,contextptr) && !is_zero(allowed_lnarg))
      return false;
    return true;
  }


  static gen risch_lin(const gen & e_orig,const identificateur & x,gen & remains_to_integrate,GIAC_CONTEXT){
    vecteur v;
    gen e=e_orig;
    if (!risch_tower(x,e,v,contextptr)){
      remains_to_integrate=e_orig;
      return zero;
    }
    if (v.empty())
      return e*x;
    gen prim,lncoeff;
    in_risch(e,x,v,zero,prim,lncoeff,remains_to_integrate,contextptr);
    vector<const unary_function_ptr *> SiCi(1,at_Si);
    SiCi.push_back(at_Ci);
    if (!lop(prim,at_Si).empty()){
      prim=recursive_normal(prim,contextptr);
      if (has_i(prim)){
	prim=_exp2trig(prim,contextptr);
	prim=recursive_normal(prim,contextptr);
      }
    }
    if (is_zero(prim))
      remains_to_integrate=e_orig;
    return prim;
  }

  gen risch(const gen & e_orig,const identificateur & x,gen & remains_to_integrate,GIAC_CONTEXT){
#if 0 // def GIAC_HAS_STO_38
    remains_to_integrate=e_orig;
    return 0;
#endif
    vecteur vexp;
    lin(trig2exp(e_orig,contextptr),vexp,contextptr);
    const_iterateur it=vexp.begin(),itend=vexp.end();
    gen rem,remsum,res;
    for (;it!=itend;){
      gen coeff=*it;
      ++it; 
      gen expo=*it;
      ++it;
      res += risch_lin(coeff*exp(expo,contextptr),x,rem,contextptr);
      remsum += rem;
    }
    res += risch_lin(remsum,x,remains_to_integrate,contextptr);
    vector<const unary_function_ptr *> SiCiexp(1,at_Si);
    SiCiexp.push_back(at_Ci);
    SiCiexp.push_back(at_exp);
    if (!lop(res,SiCiexp).empty()){
      res=recursive_normal(res,contextptr);
      if (!has_i(e_orig) && has_i(res)){
	res=_exp2trig(res,contextptr);
	res=recursive_normal(res,contextptr);
	if (has_i(res))
	  res=recursive_normal(re(halftan(res,contextptr),contextptr),contextptr);
      }
    }
    return res;
  }
  
  gen _risch(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return _risch(vecteur(1,g),contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s>2)
      return _integrate(g,contextptr);
    gen tmp;
    gen var=x__IDNT_e;
    if (s==2 && v.back().type==_IDNT)
      var=v.back();
    gen res=risch(v.front(),*var._IDNTptr,tmp,contextptr);
    if (is_zero(tmp))
      return res;
    return res+symbolic(at_integrate,makevecteur(tmp,var)); 
  }
  static const char _risch_s []="risch";
  static define_unary_function_eval (__risch,&_risch,_risch_s);
  define_unary_function_ptr5( at_risch ,alias_at_risch ,&__risch,0,true);

  // integer roots of a polynomial
  vecteur iroots(const polynome & p){
    int s=p.dim;
    vecteur zerozero(s-1);
    vecteur P0(polynome2poly1(p/lgcd(p),1));
    vecteur P(P0);
    // eval every coeff at (0,...,0)
    int d=P.size();
    for (int i=0;i<d;++i){
      if (P[i].type==_POLY)
	P[i]=peval(*P[i]._POLYptr,zerozero,0);
    }
    // now search the integer roots of this polynomial
    polynome p0(poly12polynome(P,1,1));
    polynome p1=p0.derivative();
    p1=gcd(p0,p1);
    p0=p0/p1; // p0 is now squarefree with the same roots as initial p0
    // check that all coeffs are integer, if not call normal factorizatio
    vector< monomial<gen> >::const_iterator it=p0.coord.begin(),itend=p0.coord.end();
    vecteur res;
    for (;it!=itend;++it){
#ifndef HAVE_LIBNTL // with LIBNTL, linearfind does nothing!
      if (!is_integer(it->value)){
#endif
	factorization vden;
	gen extra_div=1;
	factor(p0,p1,vden,false,false,false,1,extra_div);
	factorization::const_iterator f_it=vden.begin(),f_itend=vden.end();
	// bool ok=true;
	for (;f_it!=f_itend;++f_it){
	  int deg=f_it->fact.lexsorted_degree();
	  if (deg!=1)
	    continue;
	  // extract the root
	  vecteur vtmp=polynome2poly1(f_it->fact,1);
	  gen root=-vtmp.back()/vtmp.front();
	  if (root.type==_INT_)
	    res.push_back(root);
	}
	return res;
#ifndef HAVE_LIBNTL
      }
#endif
    }
    environment * env=new environment;
    polynome temp(1);
    vectpoly v;
    int ithprime=1;
    if (!linearfind(p0,env,temp,v,ithprime)) // FIXME??
      res.clear();// int bound=
    delete env;
    d=v.size();
    for (int i=0;i<d;++i){
      vecteur tmpv=polynome2poly1(v[i]);
      if (tmpv.size()!=2)
	continue;
      gen g=-tmpv[1]/tmpv[0];
      if (g.type!=_INT_)
	continue;
      gen tmp=horner(P0,g);
      if (is_zero(tmp))
	res.push_back(g);
    }
    return res;
  }
#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

