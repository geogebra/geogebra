// -*- mode:C++ ; compile-command: "g++ -I.. -I../include -g -c -Wall -DHAVE_CONFIG_H -DIN_GIAC csturm.cc" -*-
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
#include <cmath>
#include <stdexcept>
#include <map>
#include "gen.h"
#include "csturm.h"
#include "vecteur.h"
#include "modpoly.h"
#include "unary.h"
#include "symbolic.h"
#include "usual.h"
#include "sym2poly.h"
#include "solve.h"
#include "prog.h"
#include "subst.h"
#include "permu.h"
#include "series.h"
#include "alg_ext.h"
#include "ti89.h"
#include "plot.h"
#include "modfactor.h"
#include"giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // compute Sturm sequence of r0 and r1,
  // returns gcd (without content)
  // and compute list of quotients, coeffP, coeffR
  // such that coeffR*r_(k+2) = Q_k*r_(k+1) - coeffP_k*r_k
  gen csturm_seq(modpoly & r0,modpoly & r1,vecteur & listquo,vecteur & coeffP, vecteur & coeffR,GIAC_CONTEXT){
    listquo.clear();
    coeffP.clear();
    coeffR.clear();
    if (r0.empty())
      return r1;
    if (r1.empty())
      return r0;
    gen tmp;
    lcmdeno(r0,tmp,contextptr);
    if (ck_is_positive(-tmp,contextptr))
      r0=-r0;
    r0=r0/abs(lgcd(r0),contextptr);
    lcmdeno(r1,tmp,contextptr);
    if (ck_is_positive(-tmp,contextptr))
      r1=-r1;
    r1=r1/abs(lgcd(r0),contextptr);
    // set auxiliary constants g and h to 1
    gen g(1),h(1);
    modpoly a(r0),b(r1),quo,r;
    gen b0(1);
    for (int loop_counter=0;;++loop_counter){
      int m=a.size()-1;
      int n=b.size()-1;
      int ddeg=m-n; // should be 1 generically
      if (!n) { // if b is constant, gcd=1
	return 1;
      }
      b0=b.front();
      if (b.front().type==_VECT) {
	// ddeg should be even if b0 is a _POLY1
	if (ddeg%2==0)
	  *logptr(contextptr) << gettext("Singular parametric Sturm sequence ") << a << "/" << b << endl;
      }
      else
	b0=abs(b.front(),contextptr); 
      coeffP.push_back(pow(b0,ddeg+1));
      DivRem(coeffP.back()*a,b,0,quo,r);
      listquo.push_back(quo);
      coeffR.push_back(g*pow(h,ddeg));
      if (r.empty()){
	return b/abs(lgcd(b),contextptr);
      }
      // remainder is non 0, loop continue: a <- b
      a=b;
      // now divides r by g*h^(m-n) and change sign, result is the new b
      b= -r/coeffR.back();
      g=b0;
      h=pow(b0,ddeg)/pow(h,ddeg-1);
    } // end while loop
  }

  static gen csturm_horner(const modpoly & p,const gen & a){
    if (p.size()==1 && p.front().type==_POLY && p.front()._POLYptr->dim==1){
      // patch for "sparse modpoly"
      vector< monomial<gen> >::const_iterator it=p.front()._POLYptr->coord.begin(),itend=p.front()._POLYptr->coord.end();
      gen res=0,anum=a,aden=1,den=1;
      int oldpui=0,pui;
      if (a.type==_FRAC){
	anum=a._FRACptr->num;
	aden=a._FRACptr->den;
      }
      for (;it!=itend;++it){
	pui=it->index.front();
	if (oldpui){
	  res = res * pow(anum,oldpui-pui,context0);
	  den = den * pow(aden,oldpui-pui,context0);
	}
	res += it->value*den;
	oldpui=pui;
      }
      if (oldpui){
	res = res *pow(a,oldpui,context0);
	den = den *pow(a,oldpui,context0);
      }
      return res/den;
    }
    else
      return horner(p,a);
  }

  static int csturm_vertex_ab(const modpoly & r0,const modpoly & r1,const vecteur & listquo,const vecteur & coeffP, const vecteur & coeffR,const gen & a,int start,GIAC_CONTEXT){
    int n=listquo.size(),j,k,res=0;
    vecteur R(n+2);
    R[0]=csturm_horner(r0,a);
    R[1]=csturm_horner(r1,a);
    for (j=0;j<n;j++)
      R[j+2]=(-coeffP[j]*R[j]+R[j+1]*horner(listquo[j],a))/coeffR[j];
    // signes
    for (j=start;j<n+2;j++){
      if (R[j]!=0) break;
    }
    for (k=j+1;k<n+2;k++){
      if (is_zero(R[k])) continue;
      if (fastsign(R[j],context0)*fastsign(R[k],context0)<0 
	  // is_positive(-R[j]*R[k],contextptr)
	  ){
	res++;
	j=k;
      }
    }
    return res;
  }

#if 0
  // compute vertex index at a (==0 unless s(a)==0) 
  static int csturm_vertex_a(const modpoly & s,const modpoly & r,const gen & a,int direction,GIAC_CONTEXT){
    int j;
    modpoly s1,s2;
    gen sa=horner(s,a,0,s1);
    if (!is_zero(sa)) return 0;
    for (j=1;;j++){
      sa=horner(s1,a,0,s2);
      if (!is_zero(sa))
	break;
      s1=s2;
    }
    if (direction==1) j=0;
    gen tmp=sign(sa,contextptr)*sign(horner(r,a),contextptr);
    return tmp.val*((j%2)?-1:1);
  }
#endif

  void change_scale(modpoly & p,const gen & l){
    int n=p.size();
    gen lton(l);
    for (int i=n-2;i>=0;--i){
      p[i] = p[i] * lton;
      lton = lton * l;
    }
  }

  void back_change_scale(modpoly & p,const gen & l){
    int n=p.size();
    gen lton(l);
    for (int i=n-2;i>=0;--i){
      p[i] = p[i] / lton;
      lton = lton * l;
    }
  }

  // p(x)->p(a*x+b)
  modpoly linear_changevar(const modpoly & p,const gen & a,const gen & b){
    modpoly res(taylor(p,b));
    change_scale(res,a);
    return res;
  }

  // p(a*x+b)->p(x)
  // t=a*x+b -> pgcd(t)=g((t-b)/a)
  modpoly inv_linear_changevar(const modpoly & p,const gen & a,const gen & b){
    gen A=inv(a,context0); 
    gen B=-b/a;
    modpoly res(taylor(p,B));
    change_scale(res,A);
    return res;
  }

  // Find roots of R, S=R' at precision eps, returns number of roots
  // if eps==0 does not compute intervals for roots
  static int csturm_realroots(const modpoly & S,const modpoly & R,const vecteur & listquo,const vecteur & coeffP, const vecteur & coeffR,const gen & a,const gen & b,const gen & t0, const gen & t1,vecteur & realroots,double eps,GIAC_CONTEXT){
    int n1=csturm_vertex_ab(S,R,listquo,coeffP,coeffR,t0,1,contextptr);
    int n2=csturm_vertex_ab(S,R,listquo,coeffP,coeffR,t1,1,contextptr);
    int n=(n2-n1);
    if (!eps || !n)
      return n;
    /* disabled localization of roots, do isolation of roots instead
    if (is_strictly_greater(eps,(t1-t0)*abs(b,contextptr),contextptr)){
      realroots.push_back(makevecteur(makevecteur(a+t0*b,a+t1*b),n));
      return n;
    }
    */
    if (n==1){
      gen T0=t0,T1=t1,T2;
      int s0=fastsign(csturm_horner(R,T0),contextptr);
      // int s1=fastsign(csturm_horner(R,T1),contextptr);
      int s2;
      gen delta=evalf_double(log((T1-T0)*abs(b,contextptr)/eps,contextptr)/log(2.,contextptr),1,contextptr);
      if (delta.type!=_DOUBLE_){
	realroots=vecteur(1,gentypeerr(contextptr));
	return -2;
      }
      int nstep=int(delta._DOUBLE_val+1);
      for (int step=0;step<nstep;++step){
	T2=(T0+T1)/2;
	s2=fastsign(csturm_horner(R,T2),contextptr);
	if (!s2){
	  realroots.push_back(makevecteur(a+T2*b,n));
	  return n;
	}
	if (s2==s0)
	  T0=T2;
	else
	  T1=T2;
      }
      realroots.push_back(makevecteur(makevecteur(a+T0*b,a+T1*b),n));
      return n;
    }
    gen T0=t0,T1=t1,t01;
    for (;;){
      t01=(T0+T1)/2;
      int n01=csturm_vertex_ab(S,R,listquo,coeffP,coeffR,t01,1,contextptr);
      if (n01!=n1 && n01!=n2)
	break;
      if (n01==n1)
	T0=t01;
      else
	T1=t01;
    }
    if (csturm_realroots(S,R,listquo,coeffP,coeffR,a,b,T0,t01,realroots,eps,contextptr)==-2)
      return -2;
    if (csturm_realroots(S,R,listquo,coeffP,coeffR,a,b,t01,T1,realroots,eps,contextptr)==-2)
      return -2;
    return n;
  }

  // Find complex sturm sequence for P(a+(b-a)*x)
  // If P is "pseudo"-real on [a,b] and eps>0 put roots in [a,b]
  // at precision eps inside realroots
  // returns a,b,R,S,g,listquo,coeffP,coeffR
  // and typeseq=0 (complex Sturm) or 1 (limit)
  // If b-a is real and horiz_sturm is not empty, it tries to replace
  // the variable by im(a)*i in horiz_sturm and if no quotient in horiz_sturm
  // has a leading 0 coefficient, 
  // it returns im(a)*i,im(a)*i+1,R,S,g,listquo,coeffP,coeffR
  // If b-a is pure imaginary and vert_sturm is not empty, it tries to replace
  // the variable by re(a) and returns re(a),re(a)+i,R,S,g,listquo,coeffP,coeffR
  static vecteur csturm_segment_seq(const modpoly & P,const gen & a,const gen & b,vecteur & realroots,double eps,vecteur & horiz_sturm,vecteur & vert_sturm,GIAC_CONTEXT){
    // try with horiz_sturm and vert_sturm
    gen ab(b-a);
    // /*
    if (is_zero(re(ab,contextptr))){ // b-a is pure imaginary
      if (vert_sturm.empty()){
	gen A=gen(makevecteur(1,0),_POLY1__VECT);
	vert_sturm.push_back(undef);
	vecteur tmp;
	vert_sturm=csturm_segment_seq(P,A,A+cst_i,tmp,eps,horiz_sturm,vert_sturm,contextptr);
	if (is_undef(vert_sturm))
	  return vert_sturm;
      }
      if (vert_sturm.size()==9){
	vecteur res(vert_sturm);
	gen A=re(a,contextptr);
	res[0]=A; // re(a)
	res[1]=A+cst_i; // re(a)+i
	res[2]=apply1st(res[2],A,horner); // R 
	res[3]=apply1st(res[3],A,horner); // S
	res[4]=horner(res[4],A); // g
	vecteur tmp(*res[5]._VECTptr);
	int tmps=tmp.size();
	for (int j=0;j<tmps;++j)
	  tmp[j]=apply1st(tmp[j],A,horner); 
	res[5]=tmp; // listquo
	res[6]=apply1st(res[6],A,horner); // coeffP
	res[7]=apply1st(res[7],A,horner); // coeffR
	if (res[6].type==_VECT && !equalposcomp(*res[6]._VECTptr,0))
	  return res;
	else
	  cerr << "list of quotients is not regular" << endl;
      }
    }
    // */
    modpoly Q(taylor(P,a));
    change_scale(Q,b-a);
    // now x is in 0..1
    gen gtmp=apply(Q,re,contextptr);
    if (gtmp.type!=_VECT)
      return vecteur(1,gensizeerr(contextptr));
    modpoly R=trim(*gtmp._VECTptr,0);
    gtmp=apply(Q,im,contextptr);
    if (gtmp.type!=_VECT)
      return vecteur(1,gensizeerr(contextptr));
    modpoly S=trim(*gtmp._VECTptr,0);
    modpoly listquo,coeffP,coeffR;
    gen g=csturm_seq(S,R,listquo,coeffP,coeffR,contextptr);
    int typeseq=-1;
    if (debug_infolevel)
      *logptr(contextptr)  << "segment " << a << ".." << b << ", im/re:" << S << "|" << R << ", gcd:" << g << endl;
    if (g.type==_VECT && g._VECTptr->size()==P.size()){
      // if g==P (up to a constant), use real Sturm sequences
      if (debug_infolevel)
	*logptr(contextptr)  << "Real-kind roots: " << g << endl;
      R=*g._VECTptr;
      S=derivative(R);
      g=csturm_seq(S,R,listquo,coeffP,coeffR,contextptr);
      typeseq=csturm_realroots(S,R,listquo,coeffP,coeffR,a,b-a,0,1,realroots,eps,contextptr);
      if (typeseq==-2)
	return realroots;
    }
    if (g.type==_VECT)
      g=inv_linear_changevar(*g._VECTptr,b-a,a);
    vecteur res= makevecteur(a,b,R,S,g,listquo,coeffP,coeffR,typeseq);
    return res;
  }

  // index for segment a,b (2* number of roots when summed over a closed
  // polygon). Note that if S=ImP along the segment is 0 we remove
  // the roots on [a,b] using real Sturm sequences
  // If S=0 at a or b, this is simply ignored
  // Indeed the computed index is then the same as if S was of the
  // sign of R, and since R!=0 if S is 0 this is a property of the vertex
  // not of the segment (note that contrary to counting real roots
  // on an interval, S can vanish as many times as long as R keeps
  // the same sign, without modifying the algebraic number of Im=0
  // cuts if S has the same sign on both end)
  static int csturm_segment(const vecteur & seq,const gen & a,const gen & b,GIAC_CONTEXT){
    gen t0,t1;
    if (seq.size()!=9)
      return -(RAND_MAX/2);
    gen aseq=seq[0];
    gen bseq=seq[1];
    gen directeur=(b-a)/(bseq-aseq);
    t0=(a-aseq)/(bseq-aseq);
    if ( !is_zero(im(directeur,contextptr)) || !is_zero(im(t0,contextptr)) )
      return -(RAND_MAX/2);
    t0=re(t0,contextptr); // t0=normal(t0);
    t1=re(t0+directeur,contextptr); // t1=normal(t0+directeur);
    int signe=1;
    if (is_strictly_greater(t0,t1,contextptr)){
      signe=-1;
      swapgen(t0,t1);
    } 
    const modpoly & R=*seq[2]._VECTptr;
    const modpoly & S=*seq[3]._VECTptr;
    gen g=seq[4];
    const modpoly & listquo=*seq[5]._VECTptr;
    const modpoly & coeffP=*seq[6]._VECTptr;
    const modpoly & coeffR=*seq[7]._VECTptr;
    int debut=(seq[8].val==-1)?0:1;
    int tmp = csturm_vertex_ab(S,R,listquo,coeffP,coeffR,t0,debut,contextptr);
    int res = tmp;
    tmp = csturm_vertex_ab(S,R,listquo,coeffP,coeffR,t1,debut,contextptr);
    res -= tmp;
    // tmp = (-csturm_vertex_a(S,R,t0,1,contextptr)+csturm_vertex_a(S,R,t1,-1,contextptr));
    // res += tmp;
    res=(debut?1:signe)*res;
    if (debug_infolevel)
      *logptr(contextptr)  << "segment " << a << ".." << b << " index contribution " << res << endl;
    return res;
  }

  static bool csturm_square_seq(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,gen & pgcd,vecteur & realroots,double eps,vecteur & seq1,vecteur & seq2,vecteur & seq3,vecteur & seq4,vecteur & horiz_sturm,vecteur & vert_sturm,GIAC_CONTEXT){
    gen A=a0+cst_i*b0,B=a1+cst_i*b0;
    vecteur rroots;
    seq1=csturm_segment_seq(P,A,B,rroots,eps,horiz_sturm,vert_sturm,contextptr);
    if (is_undef(seq1))
      return false;
    pgcd=seq1[4];
    if (!is_one(pgcd)){
      return false;
    }
    A=a1+cst_i*b0; B=a1+cst_i*b1;
    seq2=csturm_segment_seq(P,A,B,rroots,eps,horiz_sturm,vert_sturm,contextptr);
    if (is_undef(seq2))
      return false;
    pgcd=seq2[4];
    if (!is_one(pgcd)){
      return false;
    }
    A=a1+cst_i*b1; B=a0+cst_i*b1;
    seq3=csturm_segment_seq(P,A,B,rroots,eps,horiz_sturm,vert_sturm,contextptr);
    if (is_undef(seq3))
      return false;
    pgcd=seq3[4];
    if (!is_one(pgcd)){
      return false;
    }
    A=a0+cst_i*b1; B=a0+cst_i*b0;
    seq4=csturm_segment_seq(P,A,B,rroots,eps,horiz_sturm,vert_sturm,contextptr);
    if (is_undef(seq4))
      return false;
    pgcd=seq4[4];
    if (!is_one(pgcd)){
      return false;
    }
    realroots=mergevecteur(realroots,rroots);
    return true;
  }

  // find 2* number of roots of P inside the square of vertex of affixes a,b
  // roots on the square are not counted. P must not vanish at the vertices.
  // The complex Sturm sequences must be known
  // returns -1 on error
  static int csturm_square(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,const vecteur & seq1,const vecteur & seq2,const vecteur & seq3,const vecteur & seq4,GIAC_CONTEXT){
    int ind,tmp;
    ind = 0;
    gen A=a0+cst_i*b0,B=a1+cst_i*b0;
    tmp = csturm_segment(seq1,A,B,contextptr);
    if (tmp==-(RAND_MAX/2))
      return -1;
    ind += tmp;
    A=a1+cst_i*b0; B=a1+cst_i*b1;
    tmp = csturm_segment(seq2,A,B,contextptr);
    if (tmp==-(RAND_MAX/2))
      return -1;
    ind += tmp;
    A=a1+cst_i*b1; B=a0+cst_i*b1;
    tmp = csturm_segment(seq3,A,B,contextptr);
    if (tmp==-(RAND_MAX/2))
      return -1;
    ind += tmp;
    A=a0+cst_i*b1; B=a0+cst_i*b0;
    tmp = csturm_segment(seq4,A,B,contextptr);
    if (tmp==-(RAND_MAX/2))
      return -1;
    ind += tmp;
    return ind;
  }

  static void csturm_normalize(modpoly & p,const gen & a0,const gen & b0,const gen & a1,const gen & b1,vecteur & roots){
    int n=p.size()-1;
    // Make sure that x->a+i*x does not return a multiple 
    // of a real polynomial with the multiple non real
    // If degree of p is even the multiple will be a real (because of lcoeff)
    if (n%2){
      // If degree is odd then look at q=p(x-a_{n-1}/n*an)
      // it has the same property
      // if its cst coeff is zero remove
      gen an=p.front();
      gen b=p[1];
      gen shift=-b/n/an;
      modpoly q(taylor(p,shift));
      gen q0;
      // remove valuation
      int qs=q.size();
      int n1=0;
      for (;qs>0;--qs,++n1){
	if (!is_zero(q0=q[qs-1]))
	  break;
      }
      if (is_zero(re(q0,context0))){
	q=cst_i*q;
	p=cst_i*p;
      }
      if (n1){
	q=modpoly(q.begin(),q.begin()+qs);
	gen a=re(shift,context0),b=im(shift,context0);
	if (is_greater(a,a0,context0) && is_greater(b,b0,context0) && is_greater(a1,a,context0) && is_greater(b1,b,context0))
	  roots.push_back(makevecteur(shift,n1));
	p=taylor(q,-shift);
      }
    }
  }

  void ab2a0b0a1b1(const gen & a,const gen & b,gen & a0,gen & b0,gen & a1,gen & b1,GIAC_CONTEXT){
    a0=re(a,contextptr); b0=im(a,contextptr);
    a1=re(b,contextptr); b1=im(b,contextptr);
    if (ck_is_greater(a0,a1,contextptr)) swapgen(a0,a1);
    if (ck_is_greater(b0,b1,contextptr)) swapgen(b0,b1);
  }

  // find 2* number of roots of P inside the square of vertex of affixes a,b
  // excluding those on the square
  // returns -1 on error
  int csturm_square(const gen & p,const gen & a,const gen & b,gen& pgcd,GIAC_CONTEXT){
    if (p.type==_POLY){
      int res=0;
      factorization f(sqff(*p._POLYptr));
      factorization::const_iterator it=f.begin(),itend=f.end();
      for (;it!=itend;++it){
	int tmp=csturm_square(polynome2poly1(it->fact),a,b,pgcd,contextptr);
	if (tmp==-1)
	  return -1;
	res += it->mult*tmp;
      }
      return res;
    }
    if (p.type!=_VECT)
      return 0;
    modpoly P=*p._VECTptr;
    vecteur realroots;
    gen a0,b0,a1,b1;
    ab2a0b0a1b1(a,b,a0,b0,a1,b1,contextptr);
    csturm_normalize(P,a0,b0,a1,b1,realroots);
    int evident=0;
    if (!realroots.empty()){
      gen r=realroots.front();
      if (r.type==_VECT && r._VECTptr->size()==2)
	r=r._VECTptr->front();
      gen rx=re(r,contextptr),ry=im(r,contextptr);
      if ( ( is_zero(ry) && (rx==a0 || rx==a1) ) ||
	   ( is_zero(rx) && (ry==b0 || ry==b1) ) )
	;
      else
	evident=1;
    }
    if (P.size()<2)
      return evident;
    vecteur seq1,seq2,seq3,seq4,horiz_seq,vert_seq;    
    if (!csturm_square_seq(P,a0,b0,a1,b1,pgcd,realroots,0.0,seq1,seq2,seq3,seq4,horiz_seq,vert_seq,contextptr)){
      if (pgcd.type!=_VECT)	      
	return -1;
      modpoly g=(*pgcd._VECTptr)/pgcd[0];
      // true factorization found, restart with each factor
      modpoly p1=P/g;
      int n1=csturm_square(p1,a,b,pgcd,contextptr);
      if (n1==-1)
	return -1;
      int n2=csturm_square(g,a,b,pgcd,contextptr);
      if (n2==-1)
	return -1;
      return evident+n1+n2;
    }
    int tmp=csturm_square(P,a0,b0,a1,b1,seq1,seq2,seq3,seq4,contextptr);
    if (tmp==-1)
      return tmp;
    return evident+tmp;
  }

  static void complex_roots(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,vecteur & realroots,vecteur & complexroots,double eps);

  static bool complex_roots_split(const modpoly & P,const gen & pgcd,const gen & a0,const gen & b0,const gen & a1,const gen & b1,vecteur & realroots,vecteur & complexroots,double eps){
    if (pgcd.type!=_VECT)
      return false;
    modpoly g=(*pgcd._VECTptr)/pgcd[0];
    // true factorization found, restart with each factor
    modpoly p1=P/g;
    csturm_normalize(p1,a0,b0,a1,b1,realroots);
    csturm_normalize(g,a0,b0,a1,b1,realroots);
    complex_roots(p1,a0,b0,a1,b1,realroots,complexroots,eps);
    complex_roots(g,a0,b0,a1,b1,realroots,complexroots,eps);
    return true;
  }

#if 0
  // check that arg is >=pi/8 (assumes im(g)>=0)
  static bool arg_geq_pi_8(const gen & g){
    gen gr=re(g,context0),gi=im(g,context0);
    if (is_positive(-gr,context0))
      return true;
    // ? gi/gr>=sqrt(2)-1
    gen r=gi/gr+1;
    if (is_positive(r*r-2,context0))
      return true;
    return false;
  }

  // is im(b/a)>=0, tested without quotient
  static bool arg_in_0_pi(const gen & a,const gen & b){
    gen A(a),B(b);
    if (A.type==_FRAC && is_integer(A._FRACptr->den) && is_positive(A._FRACptr->den,context0))
      A=A._FRACptr->num;
    if (B.type==_FRAC && is_integer(B._FRACptr->den) && is_positive(B._FRACptr->den,context0))
      B=B._FRACptr->num;
    gen c=re(A,context0)*im(B,context0)+re(B,context0)*im(A,context0);
    return is_positive(c,context0);
  }

  static gen hornerarg(const modpoly & p,const gen & x){
    if (p.empty())
      return 0;
    if (x.type!=_FRAC || !is_integer(x._FRACptr->den))
      return horner(p,x);
    fraction & f =*x._FRACptr;
    gen num=f.num,den=f.den,d=den;
    if (is_positive(-f.den,context0)){
      num=-num; den=-den; d=den;
    }
    modpoly::const_iterator it=p.begin(),itend=p.end();
    gen res(*it);
    ++it;
    if (it==itend)
      return res;
    for (;;){
      res=res*num+(*it)*d;
      ++it;
      if (it==itend)
	break;
      d=d*den;   
    }
    return res;
  }  

  // Find one complex root inside a0,b0->a1,b1, return false if not found
  static bool complex_1root(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,vecteur & complexroots,double eps){
    return false; // disabled since it is not faster!!
    int step,nstep =int(evalf_double(log(max(a1-a0,b1-b0,context0)/eps,context0)/log(2.0,context0),1,context0)._DOUBLE_val+0.5);
    if (nstep<4)
      return false;
    // First compute P at the 4 vertex and check whether P[vertex_n+1]/P[vertex_n] is in C^+
    gen P0=hornerarg(P,a0+cst_i*b0),P2=hornerarg(P,a1+cst_i*b0),
      P4=hornerarg(P,a1+cst_i*b1),P6=hornerarg(P,a0+cst_i*b1);
    if (!(arg_in_0_pi(P0,P2) && arg_in_0_pi(P2,P4) && arg_in_0_pi(P4,P6) && arg_in_0_pi(P6,P0)))
      return false;
    gen A0(a0),A2(a1),B0(b0),B2(b1),A1,B1;
    for (step=0;step<2*nstep;step++){
      A1=(A0+A2)/2;
      B1=(B0+B2)/2;
      gen P1=hornerarg(P,A1+cst_i*B0),P7=hornerarg(P,A0+cst_i*B1),P8=hornerarg(P,A1+cst_i*B1),P3,P5;
      bool found=false;
    /*
      P6(A0,B2) - P5(A1,B2) - P4(A2,B2)  
         |            |           |      
      P7(A0,B1) - P8(A1,B1) - P3(A2,B1) 
         |            |            |      
      P0(A0,B0) - P1(A1,B0) - P2(A2,B0)  
     */
      // ? P0, P1, P8, P7
      if (arg_in_0_pi(P0,P1) && arg_in_0_pi(P1,P8) && arg_in_0_pi(P8,P7) && arg_in_0_pi(P7,P0)){
	A2=A1;
	B2=B1;
	P2=P1;
	P4=P8;
	P6=P7;
	if (step<nstep)
	  continue;
	found=true;
      }
      if (!found){
	P3=hornerarg(P,A2+cst_i*B1);
	// ? P1, P2, P3, P8
	if (arg_in_0_pi(P1,P2) && arg_in_0_pi(P2,P3) && arg_in_0_pi(P3,P8) && arg_in_0_pi(P8,P1)){
	  A0=A1;
	  B2=B1;
	  P0=P1;
	  P4=P3;
	  P6=P8;
	  if (step<nstep)
	    continue;
	  found=true;
	}
      }
      if (!found){
	// P8, P3, P4, P5
	P5=hornerarg(P,A1+cst_i*B2);
	if (arg_in_0_pi(P8,P3) && arg_in_0_pi(P3,P4) && arg_in_0_pi(P4,P5) && arg_in_0_pi(P5,P8)){
	  A0=A1;
	  B0=B1;
	  P0=P8;
	  P2=P3;
	  P6=P5;
	  if (step<nstep)
	    continue;
	  found=true;
	}
      }
      if (!found){
	// P7 P8 P5 P6
	if (arg_in_0_pi(P7,P8) && arg_in_0_pi(P8,P5) && arg_in_0_pi(P5,P6) && arg_in_0_pi(P6,P7)){
	  A2=A1;
	  B0=B1;
	  P0=P7;
	  P2=P8;
	  P4=P5;
	  if (step<nstep)
	    continue;
	  found=true;
	}
      }
      if (!found)
	return false;
      // Final check that there is indeed a root inside rectangle
      // args must be >= pi/8 and degree of (P)*max square length/distance to original square <= pi/8
      gen dist=min(min(A0-a0,a1-A2,context0),min(B0-b0,b1-B2,context0),context0);
      if (is_zero(dist))
	continue;
      gen max_sq=max(A2-A0,B2-B0,context0);
      if (is_greater((int(P.size())-2)*max_sq/dist,cst_pi/8,context0))
	continue;
      gen r1=P2/P0, r2=P4/P2, r3=P6/P4, r4=P0/P6;
      if (arg_geq_pi_8(r1) && arg_geq_pi_8(r2) && arg_geq_pi_8(r3) && arg_geq_pi_8(r4)){
	complexroots.push_back(makevecteur(makevecteur(A0+cst_i*B0,A2+cst_i*B2),1));
	return true;
      }
    }
    return false;
  }
#endif

  void round2(gen & x,int n){
    gen deuxn=pow(plus_two,n,context0);
    x=_floor(x*deuxn+plus_one_half,context0)/deuxn;
  }

  void round2(gen & x,const gen & deuxn,GIAC_CONTEXT){
    if (x.type!=_FRAC)
      x=_floor(x*deuxn+plus_one_half,context0)/deuxn;
    else {
      gen n=x._FRACptr->num,d=x._FRACptr->den;
      if (d.type==_INT_){
	int di=d.val,ni=1;
	while (di>1){ di=di>>1; ni=ni<<1;}
	if (ni==d.val)
	  return;
      }
      n=n*deuxn+iquo(d,2);
      x=iquo(n,d)/deuxn;
    }
  }

  // Find one complex root inside a0,b0->a1,b1, return false if not found
  // algo: Newton method in exact mode starting from center
  bool newton_complex_1root(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,vecteur & complexroots,double eps){
    if (is_positive(a1-a0-0.01,context0) ||
	is_positive(b1-b0-0.01,context0))
      return false;
    gen x0=(a0+a1)/2+cst_i*(b0+b1)/2;
    modpoly Pprime=derivative(P);
    int n=int(-std::log(eps)/std::log(2.0)+.5); // for rounding
    gen eps2=pow(2,-(n+1),context0);
    for (int ii=0;ii<n;ii++){
      gen Pprimex0=horner(Pprime,x0);
      if (is_zero(Pprimex0,context0))
	return false;
      gen dx=horner(P,x0)/Pprimex0;
      gen absdx=dx*conj(dx,context0);
      x0=x0-dx;
      gen r=re(x0,context0),i=im(x0,context0);
      if (is_positive(a0-r,context0) || is_positive(r-a1,context0) || 
	  is_positive(b0-i,context0) || is_positive(i-b1,context0))
	return false;
      round2(r,n+4);
      round2(i,n+4);
      x0=r+cst_i*i;
      if (is_positive(absdx-eps2*eps2,context0))
	continue;
      // make a small square around x0 
      // and check that there is indeed a root inside
      gen A0=r-eps2;
      gen A1=r+eps2;
      gen B0=i-eps2;
      gen B1=i+eps2;
      gen tmp;
      if (csturm_square(P,A0+cst_i*B0,A1+cst_i*B1,tmp,context0)==2){
	complexroots.push_back(makevecteur(makevecteur(A0+cst_i*B0,A1+cst_i*B1),1));
	return true;
      }
    }
    return false;
  }

  // Find complex roots of P in a0,b0 -> a1,b1
  static int complex_roots(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,const vecteur & seq1,const vecteur & seq2,const vecteur & seq3,const vecteur & seq4,vecteur & realroots,vecteur & complexroots,double eps,vecteur & horiz_sturm,vecteur & vert_sturm){
    int n=csturm_square(P,a0,b0,a1,b1,seq1,seq2,seq3,seq4,context0);
    if (debug_infolevel && n)
      cerr << a0 << "," << b0 << ".." << a1 << "," << b1 << ":" << n/2 << endl;
    if (n<=0)
      return 2*n;
    if (eps<=0){
      *logptr(context0) << gettext("Bad precision, using 1e-12 instead of ")+print_DOUBLE_(eps,14) << endl;
      eps=1e-12;
    }
    if (is_strictly_greater(eps,a1-a0,context0) && is_strictly_greater(eps,b1-b0,context0)){
      gen r(makevecteur(a0+cst_i*b0,a1+cst_i*b1));
      complexroots.push_back(makevecteur(r,gen(n)/2));
      return n;
    }
    if (n==2 && newton_complex_1root(P,a0,b0,a1,b1,complexroots,eps))
      return n;
    gen a01=(a0+a1)/2,b01=(b0+b1)/2,pgcd;
    vecteur seqvert,seqhoriz;
    gen A=a0+cst_i*b01,B=a1+cst_i*b01;
    seqhoriz=csturm_segment_seq(P,A,B,realroots,eps,horiz_sturm,vert_sturm,context0);
    if (is_undef(seqhoriz)){
      realroots=seqhoriz;
      return -2;
    }
    pgcd=seqhoriz[4];
    if (is_one(pgcd)){
      A=a01+cst_i*b0; B=a01+cst_i*b1;
      seqvert=csturm_segment_seq(P,A,B,realroots,eps,horiz_sturm,vert_sturm,context0);
      if (is_undef(seqvert)){
	realroots=seqvert;
	return -2;
      }
      pgcd=seqvert[4];
    }
    if (!is_one(pgcd)){
      complex_roots_split(P,pgcd,a0,b0,a1,b1,realroots,complexroots,eps);
      return n;
    }
    /*
      (a0,b1)  - (a01,b1)  -  (a1,b1)         seq3                seq3
         |     n4   |     n3    |        seq4   n4      seqvert    n3     seq2
      (a0,b01) - (a01,b01) -  (a1,b01)        seqhoriz           seqhoriz
         |     n1   |     n2    |        seq4   n1      seqvert   n2      seq2
      (a0,b0)  - (a01,b0)  -  (a1,b0)         seq1                seq1
     */
    int n1=complex_roots(P,a0,b0,a01,b01,seq1,seqvert,seqhoriz,seq4,realroots,complexroots,eps,horiz_sturm,vert_sturm),nadd;
    if (n1==-2)
      return -2;
    if (n1==n)
      return n;
    n1 += (nadd=complex_roots(P,a01,b0,a1,b01,seq1,seq2,seqhoriz,seqvert,realroots,complexroots,eps,horiz_sturm,vert_sturm));
    if (nadd==-2)
      return -2;
    if (n1==n)
      return n;
    n1 += (nadd=complex_roots(P,a01,b01,a1,b1,seqhoriz,seq2,seq3,seqvert,realroots,complexroots,eps,horiz_sturm,vert_sturm));
    if (nadd==-2)
      return -2;
    if (n1==n)
      return n;
    n1 += (nadd=complex_roots(P,a0,b01,a01,b1,seqhoriz,seqvert,seq3,seq4,realroots,complexroots,eps,horiz_sturm,vert_sturm));
    if (nadd==-2)
      return -2;
    return n;
  }

  // Find complex roots of P in a0,b0 -> a1,b1
  static void complex_roots(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,vecteur & realroots,vecteur & complexroots,double eps){
    if (P.size()<2)
      return;
    vecteur Seq1,Seq2,Seq3,Seq4,horiz_sturm,vert_sturm;
    gen pgcd;
    if (!csturm_square_seq(P,a0,b0,a1,b1,pgcd,realroots,eps,Seq1,Seq2,Seq3,Seq4,horiz_sturm,vert_sturm,context0))
      complex_roots_split(P,pgcd,a0,b0,a1,b1,realroots,complexroots,eps);
    else
      complex_roots(P,a0,b0,a1,b1,Seq1,Seq2,Seq3,Seq4,realroots,complexroots,eps,horiz_sturm,vert_sturm);
  }

  // Find complex roots of P in a0,b0 -> a1,b1
  bool complex_roots(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,gen & pgcd,vecteur & roots,double eps){
    vecteur realroots,complexroots;
    complex_roots(P,a0,b0,a1,b1,realroots,complexroots,eps);
    if (is_undef(realroots))
      return false;
    roots=mergevecteur(roots,mergevecteur(realroots,complexroots));
    return true;
  }

  vecteur crationalroot(polynome & p,bool complexe){
    vectpoly v;
    int i=1;
    polynome qrem;
    environment * env= new environment;
    env->complexe=complexe || !is_zero(im(p,context0));
    vecteur w;
    if (!do_linearfind(p,env,qrem,v,w,i))
      w.clear();
    delete env;
    p=qrem;
    return w;
  }

  vecteur keep_in_rectangle(const vecteur & croots,const gen A0,const gen & B0,const gen & A1,const gen & B1,bool embed,GIAC_CONTEXT){
    vecteur roots;
    const_iterateur it=croots.begin(),itend=croots.end();
    for (;it!=itend;++it){
      gen a=re(*it,contextptr),b=im(*it,contextptr);
      if (is_greater(a,A0,contextptr)&&is_greater(A1,a,contextptr)&&is_greater(b,B0,contextptr)&&is_greater(B1,b,contextptr))
	roots.push_back(embed?makevecteur(*it,1):*it);
    }
    return roots;
  }

  // find roots of polynomial P at precision eps using complex Sturm sequences
  // P must have numeric coefficients, in Q[i]
  vecteur complex_roots(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,bool complexe,double eps){
    if (P.empty())
      return P;
    eps=std::abs(eps);
    bool aplati=(a0==a1) && (b0==b1);
    if (!aplati && complexe && (a0==a1 || b0==b1) )
      return vecteur(1,gensizeerr(gettext("Square is flat!")));
    gen A0(a0),B0(b0),A1(a1),B1(b1);
    // initial rectangle: |roots|< 1+ max(|a_i|)/|a_n|
    if (aplati){
      gen maxai=_max(*apply(P,abs,context0)._VECTptr,context0);
      gen tmp=1+maxai/abs(P.front(),context0);
      A0=-tmp;
      B0=-tmp;
      A1=tmp;
      B1=tmp;
    }
    gen tmp;
    modpoly p(*apply(P,exact,context0)._VECTptr);
    lcmdeno(p,tmp,context0);
    polynome pp(poly12polynome(p));
    if (!complexe){
      gen tmp=gcd(re(pp,context0),im(pp,context0));
      if (tmp.type!=_POLY)
	return vecteur(0);
      pp=*tmp._POLYptr;
    }
    vecteur croots=crationalroot(pp,complexe);
    vecteur roots=keep_in_rectangle(croots,A0,B0,A1,B1,true,context0);
    p=polynome2poly1(pp);
    gen an=p.front();
    if (!is_zero(im(an,context0)))
      p=conj(p.front(),context0)*p;
    if (!complexe){ // real root isolation
      modpoly R=p;
      modpoly S=derivative(R);
      vecteur listquo,coeffP,coeffR;
      csturm_seq(S,R,listquo,coeffP,coeffR,context0);
      // sparse polynomial patch
      if (pp.coord.size()<p.size()/10.){
	R=vecteur(1,poly12polynome(R,1,1));
	S=vecteur(1,poly12polynome(S,1,1));
      }
      csturm_realroots(S,R,listquo,coeffP,coeffR,0,1,A0,A1,roots,eps,context0);
      return roots;
    }
    csturm_normalize(p,A0,B0,A1,B1,roots);
    gen pgcd;
    if (!complex_roots(p,A0,B0,A1,B1,pgcd,roots,eps))
      return vecteur(1,gensizeerr(context0));
    return roots;
  }


  gen complexroot(const gen & g,bool complexe,GIAC_CONTEXT){
    vecteur v=gen2vecteur(g);
    bool use_vas=!complexe;
    bool isolation=false;
    if (!v.empty() && v[0]==at_sturm){
      use_vas=false;
      v.erase(v.begin());
    }
    if (v.empty())
      return gensizeerr(contextptr);
    if (v.size()<2){
      isolation=true;
      v.push_back(epsilon(contextptr));
    }
    gen p=v.front(),prec=evalf_double(v[1],1,contextptr);
    if (prec.type!=_DOUBLE_)
      return gentypeerr(contextptr);
    double eps=prec._DOUBLE_val;
    unsigned vs=v.size();
    gen A(0),B(0);
    if (vs>3){
      A=v[2];
      B=v[3];
    }
    gen a0=re(A,contextptr),b0=im(A,contextptr),a1=re(B,contextptr),b1=im(B,contextptr);
    if (is_greater(a0,a1,contextptr))
      swapgen(a0,a1);
    if (is_greater(b0,b1,contextptr))
      swapgen(b0,b1);
    vecteur vas_res;
    if (p.type==_VECT){
      if (use_vas && vas(*p._VECTptr,a0,a1,isolation?1e300:eps,vas_res,true,contextptr))
	return vas_res;
      return complex_roots(*p._VECTptr,a0,b0,a1,b1,complexe,eps);
    }
    if (use_vas && vas(symb2poly_num(v[0],contextptr),a0,a1,isolation?1e300:eps,vas_res,true,contextptr))
      return vas_res;
    vecteur l;
    lvar(p,l);
    if (l.size()!=1)
      return gentypeerr(contextptr);
    gen px=_e2r(makevecteur(p,l),contextptr);
    if (px.type==_FRAC)
      px=px._FRACptr->num;
    if (px.type!=_POLY)
      return vecteur(0);
    factorization f(sqff(*px._POLYptr));
    factorization::const_iterator it=f.begin(),itend=f.end();
    vecteur res;
    for (;it!=itend;++it){
      vecteur tmp=complex_roots(polynome2poly1(it->fact),a0,b0,a1,b1,complexe,eps);
      if (is_undef(tmp))
	return tmp;
      iterateur jt=tmp.begin(),jtend=tmp.end();
      for (;jt!=jtend;++jt){
	if (jt->type==_VECT && jt->_VECTptr->size()==2)
	  jt->_VECTptr->back()=it->mult*jt->_VECTptr->back();
      }
      res=mergevecteur(res,tmp);
    }
    return res;
  }

  gen _complexroot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return complexroot(g,true,contextptr);
  }
  static const char _complexroot_s []="complexroot";
  static define_unary_function_eval (__complexroot,&giac::_complexroot,_complexroot_s);
  define_unary_function_ptr5( at_complexroot ,alias_at_complexroot,&__complexroot,0,true);

  gen _realroot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return complexroot(g,false,contextptr);
  }
  static const char _realroot_s []="realroot";
  static define_unary_function_eval (__realroot,&giac::_realroot,_realroot_s);
  define_unary_function_ptr5( at_realroot ,alias_at_realroot,&__realroot,0,true);

  static vecteur crationalroot(const gen & g0,bool complexe){
    gen g(g0),a,b;
    if (g.type==_VECT){
      if (g.subtype==_SEQ__VECT){
	vecteur & tmp=*g._VECTptr;
	if (tmp.size()!=3)
	  return vecteur(1,gendimerr(context0));
	g=tmp[0];
	a=tmp[1];
	b=tmp[2];
      }
      else {
	g=poly12polynome(*g._VECTptr);
      }
    }
    gen a0,b0,a1,b1;
    ab2a0b0a1b1(a,b,a0,b0,a1,b1,context0);
    vecteur l;
    lvar(g,l);
    if (l.empty())
      return vecteur(0);
    if (l.size()!=1)
      return vecteur(1,gentypeerr(context0));
    gen px=_e2r(makevecteur(g,l),context0);
    if (px.type==_FRAC)
      px=px._FRACptr->num;
    if (px.type!=_POLY)
      return vecteur(0);
    factorization f(sqff(*px._POLYptr));
    factorization::const_iterator it=f.begin(),itend=f.end();
    vecteur res;
    for (;it!=itend;++it){
      polynome p=it->fact;
      vecteur tmp=crationalroot(p,complexe);
      res=mergevecteur(res,tmp);
    }
    if (a0!=a1 || b0!=b1)
      res=keep_in_rectangle(res,a0,b0,a1,b1,false,context0);
    return res;
  }
  gen _crationalroot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return crationalroot(g,true);
  }
  static const char _crationalroot_s []="crationalroot";
  static define_unary_function_eval (__crationalroot,&giac::_crationalroot,_crationalroot_s);
  define_unary_function_ptr5( at_crationalroot ,alias_at_crationalroot,&__crationalroot,0,true);

  gen _rationalroot(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return crationalroot(g,false);
  }
  static const char _rationalroot_s []="rationalroot";
  static define_unary_function_eval (__rationalroot,&giac::_rationalroot,_rationalroot_s);
  define_unary_function_ptr5( at_rationalroot ,alias_at_rationalroot,&__rationalroot,0,true);

  // convert numerator of g to a list
  vecteur symb2poly_num(const gen & g_,GIAC_CONTEXT){
    gen g(g_);
    if (g.type!=_VECT)
      g=makesequence(g,ggb_var(g));
    gen tmp=_symb2poly(g,contextptr);
    if (tmp.type==_FRAC)
      tmp=tmp._FRACptr->num;
    if (tmp.type!=_VECT)
      return vecteur(1,gensizeerr(contextptr));
    return *tmp._VECTptr;
  }
  // VAS implementation. Based on Xcas implementation by  Alkiviadis G. Akritas,
  // A first C++ implementation was written by Spyros Kehagias and others
  // but it was too close to the Xcas code 
  // This implementation is much faster, using basic data structures of giac
  // number of sign changes of the coefficients of P, returns -1 on error
  int variations(const modpoly & P,GIAC_CONTEXT){
    int res=0,n=P.size();
    if (!n)
      return -1;
    int previous=fastsign(P.front(),contextptr);
    if (previous==0)
      return -1;
    for (int i=1;i<n;i++){
      if (is_exactly_zero(P[i]))
	continue;
      int current=fastsign(P[i],contextptr);
      if (!current)
	return -1;
      if (current!=previous){
	++res;
	previous=current;
      }
    }
    return res;
  }

#ifndef M_LN2
#define M_LN2 0.6931471805599454
#endif

  // like (ln(n/d)+shift*ln2)/expo, but faster for large integers
  gen LMQ_evalf(const gen & n,const gen & d,double shift,int expo,GIAC_CONTEXT){
#ifndef USE_GMP_REPLACEMENTS
    if (is_integer(n) && is_integer(d)){
      long int nexp=0,dexp=0;
      double nmant,dmant;
      if (n.type==_INT_)
	nmant=n.val;
      else
	nmant=mpz_get_d_2exp (&nexp,*n._ZINTptr);
      if (d.type==_INT_)
	dmant=d.val;
      else
	dmant=mpz_get_d_2exp (&dexp,*d._ZINTptr);
      return ( std::log(-nmant/dmant) + (nexp-dexp+shift)*M_LN2 )/expo;
    }
#endif
    return ( ln(evalf(-n/d,1,contextptr),contextptr) + shift*M_LN2 )/gen(expo);
  }

  static bool compute_lnabsmantexpo(const vecteur & cl,vector<double> & cllnabsmant,vector<long int> & clexpo,vector<short int> & clsign,GIAC_CONTEXT){
    int k=cl.size();
    cllnabsmant.resize(k);
    clexpo.resize(k);
    clsign.resize(k);
    for (int i=0;i<k;++i){
      gen tmp=sign(cl[i],contextptr);
      if (tmp.type!=_INT_)
	return false;
      clsign[i]=tmp.val;
      double mant;
      long int expo=0;
      if (is_integer(cl[i])){
	if (cl[i].type==_ZINT){
#ifdef USE_GMP_REPLACEMENTS
	  mant=evalf_double(cl[i],1,contextptr)._DOUBLE_val;
#else
	  mant=mpz_get_d_2exp (&expo,*cl[i]._ZINTptr);
#endif
	}
	else mant=cl[i].val;
      }
      else {
	if (cl[i].type==_DOUBLE_){
	  mant=cl[i]._DOUBLE_val;
	}
	else {
	  mant=evalf_double(cl[i],1,contextptr)._DOUBLE_val;
	}
      }
      mant=std::log(std::abs(mant));
      cllnabsmant[i]=mant;
      clexpo[i]=expo;
    }
    return true;
  }


  gen posubLMQ(const modpoly & P,GIAC_CONTEXT){
    //---implements the Local_Max_Quadratic method (LMQ) to compute an
    //---upper bound on the values of the POSITIVE roots of p(x).
    
    //---Reference:"Linear and Quadratic Complexity Bounds on the Values of the 
    //---Positive Roots of Polynomials" by Alkiviadis G. Akritas. 
    //---Journal of Universal Computer Science, Vol. 15, No. 3, 523-537, 2009. 
    int k=P.size();
    if (k<=1)
      return 0;
    vecteur cl;
    if (is_strictly_positive(P.front(),contextptr))
      cl=P;
    else
      cl=-P;
    reverse(cl.begin(),cl.end());
    vector<double> cllnabsmant; 
    vector<long int> clexpo;
    vector<short int> clsign;
    if (!compute_lnabsmantexpo(cl,cllnabsmant,clexpo,clsign,contextptr))
      return gensizeerr(contextptr);
    gen tempmax=minus_inf;
    vector<int> timesused(k+1,1);
    for (int m=k-1;m>=1;--m){
      if (clsign[m-1]==-1){ // is_strictly_positive(-cl[m-1],contextptr)
	gen tempmin=plus_inf;
	for (int n=k;n>m;--n){
	  if (clsign[n-1]==1){ // is_strictly_positive(cl[n-1],contextptr)
	    gen temp= (cllnabsmant[m-1]-cllnabsmant[n-1] + (clexpo[m-1]-clexpo[n-1]+timesused[n-1])*M_LN2)/(n-m);// LMQ_evalf(cl[m-1],cl[n-1],timesused[n-1],n-m,contextptr);
	    // gen temp=pow(-cl[m-1]/cl[n-1]*pow(plus_two,timesused[n-1]),inv(n-m,contextptr),contextptr);
	    // temp=evalf(temp,1,contextptr);
	    ++timesused[n-1];
	    if (is_strictly_greater(tempmin,temp,contextptr))
	      tempmin=temp;
	  }
	}
	if (is_strictly_greater(tempmin,tempmax,contextptr))
	  tempmax=tempmin;
      }
    }
    return _ceil(65*exp(tempmax,contextptr)/64,contextptr); // small margin
  }

  gen _posubLMQ(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v;
    if (g.type==_VECT && g.subtype!=_SEQ__VECT)
      v=*g._VECTptr;
    else
      v=symb2poly_num(g,contextptr);
    return posubLMQ(v,contextptr);
  }
  static const char _posubLMQ_s []="posubLMQ";
  static define_unary_function_eval (__posubLMQ,&giac::_posubLMQ,_posubLMQ_s);
  define_unary_function_ptr5( at_posubLMQ ,alias_at_posubLMQ,&__posubLMQ,0,true);

  gen poslbdLMQ(const modpoly & P,GIAC_CONTEXT){
    //---implements the Local_Max_Quadratic method (LMQ) to compute a
    //---lower bound on the values of the POSITIVE roots of p(x).
    
    //---Reference:"Linear and Quadratic Complexity Bounds on the Values of the 
    //---Positive Roots of Polynomials" by Alkiviadis G. Akritas. 
    //---Journal of Universal Computer Science, Vol. 15, No. 3, 523-537, 2009. 
    int k=P.size();
    if (k<=1)
      return 0;
    vecteur cl(P);
    reverse(cl.begin(),cl.end());
    if (is_strictly_positive(-cl.front(),contextptr))
      cl=-cl;
    vector<double> cllnabsmant; 
    vector<long int> clexpo;
    vector<short int> clsign;
    if (!compute_lnabsmantexpo(cl,cllnabsmant,clexpo,clsign,contextptr))
      return gensizeerr(contextptr);
    gen tempmax=minus_inf;
    vector<int> timesused(k,1);
    for (int m=1;m<k;++m){
      if (clsign[m]==-1){ // is_strictly_positive(-cl[m],contextptr)
	gen tempmin=plus_inf;
	for (int n=0;n<m;++n){
	  if (clsign[n]==1){ // is_strictly_positive(cl[n],contextptr)
	    // gen temp=pow(-cl[m]/cl[n]*pow(plus_two,timesused[n]),inv(m-n,contextptr),contextptr);
	    // temp=evalf(temp,1,contextptr);
	    gen temp= (cllnabsmant[m]-cllnabsmant[n] + (clexpo[m]-clexpo[n]+timesused[n])*M_LN2)/(m-n);// LMQ_evalf(cl[m],cl[n],timesused[n],m-n,contextptr);
	    ++timesused[n];
	    if (is_strictly_greater(tempmin,temp,contextptr))
	      tempmin=temp;
	  }
	}
	if (is_strictly_greater(tempmin,tempmax,contextptr))
	  tempmax=tempmin;
      }
    }
    tempmax=tempmax/M_LN2;
    tempmax=-_ceil(tempmax,contextptr);
    tempmax=pow(plus_two,tempmax,contextptr);
    return tempmax; 
  }
  
  gen _poslbdLMQ(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v;
    if (g.type==_VECT && g.subtype!=_SEQ__VECT)
      v=*g._VECTptr;
    else
      v=symb2poly_num(g,contextptr);
    return poslbdLMQ(v,contextptr);
  }
  static const char _poslbdLMQ_s []="poslbdLMQ";
  static define_unary_function_eval (__poslbdLMQ,&giac::_poslbdLMQ,_poslbdLMQ_s);
  define_unary_function_ptr5( at_poslbdLMQ ,alias_at_poslbdLMQ,&__poslbdLMQ,0,true);

  vecteur makeinterval(const gen & a,const gen & b){
    if (is_strictly_greater(a,b,context0))
      return makevecteur(b,a);
    else
      return makevecteur(a,b);
  }

  bool vas_sort(const gen & a,const gen &b){
    gen a1(a),b1(b);
    if (a.type==_VECT && a._VECTptr->size()==2)
      a1=a._VECTptr->front();
    if (b.type==_VECT && b._VECTptr->size()==2)
      b1=b._VECTptr->front();
    return is_strictly_greater(b1,a1,context0);
  }

  // P is assumed to be squarefree and without rational roots
  // find roots of P((ax+b)/(cx+d))
  vecteur VAS_positive_roots(const modpoly & P,const gen & ap,const gen & bp,const gen & cp,const gen & dp,GIAC_CONTEXT){
    //---The steps below correspond to the steps described in the reference below.
    
    //---Reference:	"A Comparative Study of Two Real Root Isolation Methods" 
    //---by Alkiviadis G. Akritas and Adam W. Strzebonski. 
    //---Nonlinear Analysis: Modelling and Control, Vol. 10, No. 4, 297-304, 2005.
    vecteur res; // root isolation intervals
    vecteur intervals_to_process;
    // STEP 1
    int v0=variations(P,contextptr);
    if (!v0)
      return res;
    gen ub=posubLMQ(P,contextptr);
    if (v0==1)
      return vecteur(1,makeinterval(0,ap*ub));
    intervals_to_process.push_back(makevecteur(ap, bp, cp, dp, P,v0));

    // STEP 2
    while (!intervals_to_process.empty()){
      gen tmp=intervals_to_process.back();
      intervals_to_process.pop_back();
      if (tmp.type!=_VECT || tmp._VECTptr->size()!=6)
	return vecteur(1,gensizeerr("VAS interval"+tmp.print()));
      vecteur & tmpv=*tmp._VECTptr;
      gen a=tmpv[0],b=tmpv[1],c=tmpv[2],d=tmpv[3], genf=tmpv[4],genv=tmpv[5];
      if (genf.type!=_VECT || genv.type!=_INT_)
	return vecteur(1,gensizeerr("VAS interval"+tmp.print()));
      int v=genv.val;
      modpoly f = *genf._VECTptr;

      // STEP 3
      gen lb=poslbdLMQ(f,contextptr);

      // STEP 4
      if (is_strictly_greater(lb,16,contextptr)){
	change_scale(f,lb);
	a=lb*a; c=lb*c; lb=1;
      }
      
      // STEP 5
      if (is_greater(lb,1,contextptr)){
	// f=taylor(f,lb);
	change_scale(f,lb);
	f=taylor(f,1);
	back_change_scale(f,lb);
	b = lb*a + b; d = lb*c + d;
	if (is_zero(f.back())){
	  res.push_back(b/d);
	  f.pop_back();
	}
	v=variations(f,contextptr);
	if (!v)
	  continue;
	if (v==1){
	  if (!is_zero(c))
	    res.push_back(makeinterval(a/c,b/d));
	  else
	    res.push_back(makeinterval(b,b+a*posubLMQ(f,contextptr)));
	  continue;
	}
      }

      // STEP 6
      modpoly f1=taylor(f,1),f2;
      gen a1=a, b1=a+b, c1=c, d1=c+d;
      int r=0;
      if (is_zero(f1.back())){
	f1.pop_back();
	res.push_back(b1/d1);
	r=1;
      }
      int v1=variations(f1,contextptr);
      int v2=v-v1-r; 
      gen a2=b, b2=a+b, c2=d, d2=c+d;
      
      // STEP 7
      if (v2>1){
	f2=f;
	reverse(f2.begin(),f2.end());
	f2=taylor(f2,1);
	if (is_zero(f2.back()))
	  f2.pop_back();
	v2=variations(f2,contextptr);
      }

      // STEP 8
      if (v1<v2){
	swapgen(a1,a2);
	swapgen(b1,b2);
	swapgen(c1,c2);
	swapgen(d1,d2);
	swap(f1,f2);
	int i=v1; v1=v2; v2=i;
      }

      // STEP 9
      if (v1==0) continue;
      if (v1==1){
	if (!is_zero(c1))
	  res.push_back(makeinterval(a1/c1,b1/d1));
	else 
	  res.push_back(makeinterval(b1,b1 + a1*posubLMQ(f1,contextptr)));
      }
      else 
	intervals_to_process.push_back(makevecteur(a1,b1,c1,d1,f1,v1));

      // STEP 10
      if (v2==0) continue;
      if (v2==1){
	if (!is_zero(c2))
	  res.push_back(makeinterval(a2/c2,b2/d2));
	else 
	  res.push_back(makeinterval(b2,b2 + a2*posubLMQ(f2,contextptr)));
      }
      else 
	intervals_to_process.push_back(makevecteur(a2,b2,c2,d2,f2,v2));
    }
    sort(res.begin(),res.end(),vas_sort);
    return res;
  }

  gen _VAS_positive(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v;
    if (g.type==_VECT && g.subtype!=_SEQ__VECT)
      v=*g._VECTptr;
    else
      v=symb2poly_num(g,contextptr);
    return VAS_positive_roots(v,1,0,0,1,contextptr);
  }
  static const char _VAS_positive_s []="VAS_positive";
  static define_unary_function_eval (__VAS_positive,&giac::_VAS_positive,_VAS_positive_s);
  define_unary_function_ptr5( at_VAS_positive ,alias_at_VAS_positive,&__VAS_positive,0,true);

  // square-free factorization of p, then remove all exponents
  // optionally remove factors with even multiplicities
  modpoly remove_multiplicities(const modpoly & p,factorization & f,bool odd_only,GIAC_CONTEXT){
    vecteur res(1,1),tmp;
    polynome P;
    poly12polynome(p,1,P,1);
    P=P/lgcd(P);
    f=sqff(P);
    factorization::const_iterator it=f.begin(),itend=f.end();
    for (;it!=itend;++it){
      if (odd_only && it->mult%2==0)
	continue;
      polynome2poly1(it->fact,1,tmp);
      res=operator_times(res,tmp,0);
    }
    return res;
  }

  gen vas(const modpoly & p,GIAC_CONTEXT){
    vecteur v(p);
    vecteur res;
    bool has_zero=false;
    if (is_zero(v.back())){
      has_zero=true;
      v.pop_back();
    }
    res=VAS_positive_roots(v,1,0,0,1,contextptr);
    vecteur w(v);
    change_scale(w,-1);
    if (w.size()%2==0)
      w=-w;
    if (w==v){
      v=-res;
      reverse(v.begin(),v.end());
      iterateur it=v.begin(),itend=v.end();
      for (;it!=itend;++it){
	if (it->type==_VECT)
	  reverse(it->_VECTptr->begin(),it->_VECTptr->end());
      }
      if (has_zero)
	v.push_back(0);
      res=mergevecteur(v,res);
    }
    else {
      v=VAS_positive_roots(w,-1,0,0,1,contextptr);
      if (has_zero)
	v.push_back(0);
      res=mergevecteur(v,res);
    }
    return res;
  }
  gen _VAS(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v;
    if (g.type==_VECT && g.subtype!=_SEQ__VECT)
      v=*g._VECTptr;
    else
      v=symb2poly_num(g,contextptr);
    factorization f;
    v=remove_multiplicities(v,f,false,contextptr);
    return vas(v,contextptr);
  }
  static const char _VAS_s []="VAS";
  static define_unary_function_eval (__VAS,&giac::_VAS,_VAS_s);
  define_unary_function_ptr5( at_VAS ,alias_at_VAS,&__VAS,0,true);

  static const double bisection_newton_eps=1e-3;

  // returns true if a root of p is found by Newton method, such that res-eps>a0
  // res+eps<b0 and sign of p changes between res-eps and res+eps
  static bool bisection_newton(const modpoly & P,const modpoly & Pprime,const gen & a0,const gen & a1,gen & x0,double eps,gen & eps2,GIAC_CONTEXT){
    if (is_greater(bisection_newton_eps,x0-a0,contextptr) || is_greater(bisection_newton_eps,a1-x0,contextptr))
      return false; // bisection is faster if the root is too close to the isolation interval boundaries
    int n=int(-std::log(eps)/std::log(2.0)+.5); // for rounding
    eps2=pow(2,-(n+1),contextptr);
    gen deuxn4(pow(2,n+4,contextptr));
    round2(x0,deuxn4,contextptr);
    for (int ii=0;ii<n;ii++){
      gen Pprimex0=horner(Pprime,x0);
      if (is_zero(Pprimex0,contextptr))
	return false;
      gen dx=horner(P,x0)/Pprimex0;
      round2(dx,deuxn4,contextptr);
      x0=x0-dx;
      if (is_positive(a0-x0,contextptr) || is_positive(x0-a1,contextptr))
	return false;
      if (is_greater(abs(dx,contextptr),eps2,contextptr))
	continue;
      if (is_positive(-horner(P,x0-eps2)*horner(P,x0+eps2),contextptr))
	return true;
    }
    return false;
    
  }

  gen bisection(const modpoly & p,const gen & a0,const gen &b0,double eps,GIAC_CONTEXT){
    int nsteps=int(std::ceil(std::log(evalf_double(b0-a0,1,contextptr)._DOUBLE_val/eps)/M_LN2));
    int trynewtonstep=int(nsteps-std::log(bisection_newton_eps/eps)/M_LN2);
    gen a(a0),b(b0),m,eps2;
    modpoly dp=derivative(p);
    int s1=fastsign(horner(p,a),contextptr);
    if (s1==0)
      s1=fastsign(horner(dp,a),contextptr);
    int s2=fastsign(horner(p,b),contextptr);
    if (s2==0)
      s2=-fastsign(horner(dp,b),contextptr);
    if (s1==s2)
      return undef;
    for (int i=0;i<nsteps;++i){
      m=(a+b)/2;
      if (i==trynewtonstep && bisection_newton(p,dp,a0,b0,m,eps,eps2,contextptr))
	return makevecteur(m-eps2,m+eps2);
      int s=fastsign(horner(p,m),contextptr);
      if (s==0)
	return m;
      if (s==s1)
	a=m;
      else
	b=m;
    }
    return makevecteur(a,b);
  }

  int multiplicity(const factorization & f,const gen & interval,GIAC_CONTEXT){
    factorization::const_iterator it=f.begin(),itend=f.end();
    if (interval.type==_VECT && interval._VECTptr->size()==2){
      for (;it!=itend;++it){
	if (is_strictly_positive(-it->fact(interval._VECTptr->front())*it->fact(interval._VECTptr->back()),contextptr))
	  return it->mult;
      }
      for (it=f.begin();it!=itend;++it){
	if (is_positive(-it->fact(interval._VECTptr->front())*it->fact(interval._VECTptr->back()),contextptr))
	  return it->mult;
      }
    }
    else {
      for (;it!=itend;++it){
	if (is_zero(it->fact(interval)))
	  return it->mult;
      }
    }
    return 0;
  }

  static void add_vasres(vecteur & vasres,const gen & a,const gen & a0,const gen & b0,int mult,bool with_mult,GIAC_CONTEXT){
    if (a0==b0 || (is_greater(a,a0,contextptr) && is_greater(b0,a,contextptr)) )
      vasres.push_back(with_mult?gen(makevecteur(a,mult)):a);
  }

  // isolate and find real roots of P at precision eps between a and b
  // returns a list of intervals or of rationals
  bool vas(const modpoly & P,const gen & a0,const gen &b0,double eps,vecteur & vasres,bool with_mult,GIAC_CONTEXT){
    if (P.size()<=3){
      if (P.size()<2)
	return true;
      gen a(P[0]),b(P[1]);
      if (P.size()==2){
	a=-b/a;
	add_vasres(vasres,a,a0,b0,1,with_mult,contextptr);
	return true;
      }
      gen c(P[2]);
      gen delta=b*b-4*a*c;
      if (is_zero(delta)){
	a=-b/a/2;
	add_vasres(vasres,a,a0,b0,2,with_mult,contextptr);
	return true;	
      }
      if (is_positive(delta,contextptr)){
	delta=sqrt(delta,contextptr)/a/2;
	c=-b/a/2;
	add_vasres(vasres,c-delta,a0,b0,1,with_mult,contextptr);
	add_vasres(vasres,c+delta,a0,b0,1,with_mult,contextptr);
      }
      return true;
    }
    gen a(a0),b(b0);
    if (a==b){
      a=minus_inf;
      b=plus_inf;
    }
    // check and convert coeffs of P
    modpoly p(P);
    iterateur it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      *it=exact(*it,contextptr);
    }
    gen tmp;
    lcmdeno(p,tmp,contextptr);
    for (;it!=itend;++it){
      if (!is_integer(*it))
	return false;
    }
    p=divvecteur(p,lgcd(p));
    factorization f;
    p=remove_multiplicities(p,f,false,contextptr);
    tmp=vas(p,contextptr);
    if (tmp.type!=_VECT)
      return false;
    vecteur v=*tmp._VECTptr;
    // now improve precision by bisection 
    it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type!=_VECT){
	if (is_greater(*it,a,contextptr) && is_greater(b,*it,contextptr)){
	  if (with_mult){
	    int n=multiplicity(f,*it,contextptr);
	    vasres.push_back(makevecteur(*it,n));
	  }
	  else
	    vasres.push_back(*it);
	}
	continue;
      }
      if (it->_VECTptr->size()!=2)
	return false;
      gen A=it->_VECTptr->front(),B=it->_VECTptr->back();
      if (is_strictly_greater(a,B,contextptr) || is_strictly_greater(A,b,contextptr))
	continue;
      gen interval=bisection(p,max(A,a,contextptr),min(B,b,contextptr),eps,contextptr);
      if (is_undef(interval))
	continue;
      if (with_mult)
	vasres.push_back(makevecteur(interval,multiplicity(f,interval,contextptr)));
      else
	vasres.push_back( (interval.type==_VECT && interval._VECTptr->size()==2)?evalf((interval._VECTptr->front()+interval._VECTptr->back())/2,1,contextptr):interval);
    }
    return true;
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
