/* -*- mode:C++ ; compile-command: "g++-3.4 -I. -I.. -I../include -g -c ezgcd.cc -DHAVE_CONFIG_H -DIN_GIAC" -*- */

#include "giacPCH.h"
/*  Multivariate GCD for large data not covered by the heuristic GCD algo
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
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
using namespace std;
#include "threaded.h"
#include "ezgcd.h"
#include "sym2poly.h"
#include "gausspol.h"
#include "modpoly.h"
#include "monomial.h"
#include "derive.h"
#include "subst.h"
#include "solve.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  static void add_dim(monomial<gen> & m,int d){
    index_t i(m.index.iref());
    for (int j=0;j<d;++j)
      i.push_back(0);
    m.index=i;
  }

  void change_dim(polynome & p,int dim){
    vector< monomial<gen> >::iterator it=p.coord.begin(),itend=p.coord.end();
    if (p.dim>=dim){
      p.dim=dim;
      for (;it!=itend;++it){
	it->index=index_t(it->index.begin(),it->index.begin()+dim);
      }
      return;
    }
    int delta_dim=dim-p.dim;
    p.dim=dim;
    for (;it!=itend;++it)
      add_dim(*it,delta_dim);
  }

  // returns q such that p=q [degree] and q has only terms of degree<degree
  // p=q[N] means that p-q vanishes at v at order N
  static polynome reduce(const polynome & p,const vecteur & v,int degree){
    int vsize=int(v.size());
    if (!vsize)
      return p;
    if (v==vecteur(vsize)){ 
      // trivial reduction, remove all terms of total deg >= degree
      polynome res(p.dim);
      vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
      for (;it!=itend;++it){
	if (total_degree(it->index)<degree)
	  res.coord.push_back(*it);
      }
      return res;
    }
    if (degree<=1){
      gen res=peval(p,v,0);
      if (is_zero(res))
	return polynome(p.dim);
      if (res.type==_POLY){
	polynome resp(*res._POLYptr);
	change_dim(resp,p.dim);
	return resp;
      }
      else
	return polynome(monomial<gen>(res,0,p.dim));
    }
    polynome pcur(p);
    polynome y(monomial<gen>(plus_one,1,1,p.dim));
    if (!is_zero(v.front()))
      y.coord.push_back(monomial<gen>(-v.front(),0,1,p.dim));
    polynome quo(y.dim),rem(y.dim);
    pcur.TDivRem1(y,quo,rem);
    rem=reduce(rem.trunc1(),vecteur(v.begin()+1,v.end()),degree);
    quo=reduce(quo,v,degree-1);
    return quo*y+rem.untrunc1();
  }

  static void reduce_poly(const polynome & p,const vecteur & v,int degree,polynome & res){
    res.coord.clear();
    res.dim=p.dim;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    if (is_zero(v)){
      index_t::const_iterator jt,jtend;
      int otherdeg;
      for (;it!=itend;++it){
	jt=it->index.begin()+1;
	jtend=it->index.end();
	for (otherdeg=0;jt!=jtend;++jt){
	  otherdeg += *jt;
	}
	if (otherdeg<degree)
	  res.coord.push_back(*it);
      }
    }
    else {
      for (;it!=itend;){
	int d=it->index.front();
	polynome tmp(Tnextcoeff<gen>(it,itend));
	res=res+reduce(tmp,v,degree).untrunc1(d);
      }
    }
  }

  // Same as reduce but do it for every coefficient of p with
  // respect to the main variable
  polynome reduce_poly(const polynome & p,const vecteur & v,int degree){
    polynome res(p.dim);
    reduce_poly(p,v,degree,res);
    return res;
  }

  // reduce_divrem does a mixed division: euclidean w.r.t. the first var
  // and ascending power of X-v for the other vars
  // FIXME: this implementation does not work currently, except if other
  // depends only on the first var
  static bool reduce_divrem2(const polynome & a,const polynome & other,const vecteur & v,int n,polynome & quo,polynome & rem,bool allowrational=false) {
    int asize=int(a.coord.size());
    if (!asize){
      quo=a;
      rem=a; 
      return true;
    }
    int bsize=int(other.coord.size());
    if (bsize==0) {
#ifdef NO_STDEXCEPT
      return false;
#else
      setsizeerr(gettext("ezgcd.cc/reduce_divrem2"));
#endif
    }
    index_m a_max = a.coord.front().index;
    index_m b_max = other.coord.front().index;
    quo.coord.clear();
    quo.dim=a.dim;
    rem.dim=a.dim;
    if ( (bsize==1) && (b_max==b_max*0) ){
      rem.coord.clear();
      gen b=other.coord.front().value;
      if (is_one(b))
	quo = a ;
      else {
	std::vector< monomial<gen> >::const_iterator itend=a.coord.end();
	for (std::vector< monomial<gen> >::const_iterator it=a.coord.begin();it!=itend;++it)
	  quo.coord.push_back(monomial<gen>(rdiv(it->value,b,context0),it->index)); 
      }
      return true;
    }
    rem=a;
    if ( ! (a_max>=b_max) ){
      // test that the first power of a_max is < to that of b_max
      return (a_max.front()<b_max.front());
    }
    gen b(other.coord.front().value);
    while (a_max >= b_max){
      // errors should be trapped here and false returned if error occured
      gen q(rdiv(rem.coord.front().value,b,context0));
      if (!allowrational){
	if ( has_denominator(q) || 
	     (!is_zero(q*b - rem.coord.front().value)) )
	  return false;
      }
      // end error trapping
      quo.coord.push_back(monomial<gen>(q,a_max-b_max));
      tensor<gen> temp;
      reduce_poly(other.shift(a_max-b_max,q),v,n,temp);
      rem = rem-temp;
      if (rem.coord.size())
	a_max=rem.coord.front().index;
      else
	break;
    }
    return(true);    
  }

  bool reduce_divrem(const polynome & a,const polynome & other,const vecteur & v,int n,polynome & quo,polynome & rem) {
    quo.coord.clear();
    quo.dim=a.dim;
    rem.dim=a.dim;
    // if ( (a.dim<=1) || (a.coord.empty()) )
      return reduce_divrem2(a,other,v,n,quo,rem);
#if 0
    std::vector< monomial<gen> >::const_iterator it=other.coord.begin();
    int bdeg=it->index.front(),rdeg;
    tensor<gen> b0(Tnextcoeff<gen>(it,other.coord.end()));
    tensor<gen> r(a),q(b0.dim);
    while ( (rdeg=r.lexsorted_degree()) >=bdeg){
      it=r.coord.begin();
      tensor<gen> a0(Tnextcoeff<gen>(it,r.coord.end())),tmp(a0.dim);
      // FIXME: should make ascending power division
      if (!reduce_divrem(a0,b0,v,n,q,tmp) || !tmp.coord.empty())
	return false;
      q=q.untrunc1(rdeg-bdeg);
      quo=quo+q;
      r=r-reduce_poly(q*other,v,n);
      if (r.coord.empty())
	return true;
    }
    return true;
#endif
  }

  // increment last index in v up to k,
  // if last index is k-1
  // while index[size-pos] is k-pos increment pos
  // if pos reaches size return false (not possible anymore)
  // else increment index[size-pos] and set following ones to prev+1
  static bool next(vector<int> & v,int dim,int k){
    ++v.back();
    if (v.back()!=k)
      return true;
    int pos=2;
    for (;pos<=dim;++pos){
      if (v[dim-pos]!=k-pos)
	break;
    }
    if (pos>dim)
      return false;
    ++v[dim-pos];
    for (--pos;pos>0;--pos){
      v[dim-pos]=v[dim-pos-1]+1;
    }
    return true;
  }

  // pcur(x1,...,xk,0,...,0) 
  static void peval_xk_xn_zero(const polynome & pcur,int k,polynome & pcurx1x2){
    pcurx1x2.coord.clear();
    int dim=pcur.dim;
    pcurx1x2.dim=dim;
    vector< monomial<gen> >::const_iterator it=pcur.coord.begin(),itend=pcur.coord.end();
    for (;it!=itend;++it){
      int j=k;
      index_t::const_iterator i = it->index.begin()+j;
      for (;j<dim;++j,++i){
	if (*i)
	  break;
      }
      if (j==dim)
	pcurx1x2.coord.push_back(*it);
    }
  }

  // pcur(x1,...,xk,0,...,0) 
  static void truncate_xk_xn(polynome & pcur,int k){
    vector< monomial<gen> >::iterator it=pcur.coord.begin(),itend=pcur.coord.end();
    for (;it!=itend;++it){
      it->index=index_t(it->index.begin(),it->index.begin()+k);
    }
    pcur.dim=k;
  }

  static void untruncate_xk_xn(polynome & pcur,int dim){
    vector< monomial<gen> >::iterator it=pcur.coord.begin(),itend=pcur.coord.end();
    for (;it!=itend;++it){
      index_t i (dim);
      i=it->index.iref();
      for (int j=int(i.size());j<dim;++j)
	i.push_back(0);
      it->index = i;
    }
    pcur.dim=dim;
  }

  gen _coeff(const gen &,GIAC_CONTEXT);

  bool try_sparse_factor(const polynome & pcur,const factorization & v,int mult,factorization & f){
    /* Try sparse factorization 
       lcoeff(pcur,x1)^#factors-1 * pcur = product_#factors P_i
       where P_i has lcoeff(pcur,x1) as leading coeff in x1
       and same non zeros coeffs pattern as the factors of Fb
    */
    // count number of unknowns
    factorization::const_iterator vit=v.begin(),vitend=v.end();
    int unknowns=0;
    for (;vit!=vitend;++vit){
      if (vit->mult>1)
	break; // return false might be more appropriate here
      unknowns += int(vit->fact.coord.size())-1; // lcoeff is known
    }
    if (unknowns>=giacmax(5,pcur.lexsorted_degree()/2) || unknowns==0)
      return false;
    polynome lcp(Tfirstcoeff(pcur));
    int dim=pcur.dim;
    vecteur lv(dim);
    for (int i=0;i<dim;++i){
      lv[i]=identificateur("x"+print_INT_(i));
    }
    gen mainvar(lv.front());
    gen lc=r2sym(lcp,lv,context0);
    vecteur la(unknowns);
    for (int i=0;i<unknowns;++i){
      la[i]=identificateur("a"+print_INT_(i));
    }
    vecteur la_val(la);
    int pos=0;
    // build product(P_i)
    gen product(1);
    vecteur Pis;
    for (vit=v.begin();vit!=vitend;++vit){
      const polynome & fact = vit->fact;
      vector< monomial<gen> >::const_iterator it=fact.coord.begin(),itend=fact.coord.end();
      gen Pi=lc*pow(mainvar,it->index.front());
      for (++it;it!=itend;++it){
	if (pos>=la.size()) 
	  return false;
	Pi += la[pos]*pow(mainvar,it->index.front());
	++pos;
      }
      Pis.push_back(Pi);
      product = product * Pi;
    }
    product=product-r2sym(pcur,lv,context0)*pow(lc,int(Pis.size())-1,context0);
    // solve equation wrt la
    gen systemeg=_coeff(gen(makevecteur(product,mainvar),_SEQ__VECT),context0);
    if (systemeg.type!=_VECT)
      return false;
    vecteur syst;
    const_iterateur it=systemeg._VECTptr->begin(),itend=systemeg._VECTptr->end();
    for (++it;it!=itend;++it){
      if (!is_zero(*it))
	syst.push_back(*it);
    }
    // to solve syst wrt la, we search all linear equations
    // if none return false, otherwise solve system, subst 
    while (!syst.empty()){
      int N=int(syst.size());
      vecteur linear;
      for (int i=0;i<N;++i){
	gen d1=derive(syst[i],la,context0);
	if (is_zero(derive(d1,la,context0)))
	  linear.push_back(syst[i]);
      }
      if (linear.empty())
	return false;
      vecteur indet(lv);
      lvar(linear,indet);
      indet=vecteur(indet.begin()+lv.size(),indet.end());
      vecteur sols=linsolve(linear,indet,context0);
      if (sols.size()!=indet.size() || is_undef(sols) || sols.empty())
	return false;
      la_val=subst(la_val,indet,sols,false,context0);
      gen tmp=recursive_normal(subst(syst,indet,sols,false,context0),context0);
      if (tmp.type!=_VECT)
	return false;
      syst.clear();
      const_iterateur it=tmp._VECTptr->begin(),itend=tmp._VECTptr->end();
      for (;it!=itend;++it){
	if (!is_zero(*it))
	  syst.push_back(*it);
      }
    }
    // subst la values
    Pis=subst(Pis,la,la_val,false,context0);
    for (unsigned int i=0;i<Pis.size();++i){
      gen tmp=sym2r(Pis[i],lv,context0),num,den;
      fxnd(tmp,num,den);
      if (num.type!=_POLY)
	return false;
      const polynome & N=*num._POLYptr;
      f.push_back(facteur<polynome>(N/lgcd(N),mult));
    }
    return true;
  }

  // pcur(x,x1,x2,...) with [x1,x2,...]=[t^n1,t^n2,...]
  void eval_tn(const polynome & pcur,const index_t & n,polynome & pt){
    pt.dim=2;
    pt.coord.clear();
    pt.coord.reserve(pcur.coord.size());
    vector< monomial<gen> >::const_iterator it=pcur.coord.begin(),itend=pcur.coord.end();
    index_t cur(2);
    for (;it!=itend;++it){
      const index_t & i=it->index.iref();
      index_t::const_iterator jt=i.begin(),jtend=i.end();
      index_t::const_iterator nt=n.begin();
      cur[0]=*jt;
      int curn=0;
      for (++jt;jt!=jtend;++jt,++nt)
	curn += (*jt)*(*nt);
      cur[1]=curn;
      pt.coord.push_back(monomial<gen>(it->value,cur));
    }
    pt.tsort();
  }

  // return true if none of the coefficients of p with same 1st degree are the same
  bool x_degrees(const polynome & p,vector<int> & d){
    d.clear();
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    int prev=-1;
    vecteur v;
    for (;it!=itend;++it){
      int cur=it->index.iref().front();
      if (cur!=prev){
	v=vecteur(1,it->value);
	d.push_back(cur);
	prev=cur;
      }
      else {
	if (equalposcomp(v,it->value))
	  return false;
	v.push_back(it->value);
      }
    }
    return true;
  }

  bool lex_or_coeff_sort(const monomial<gen> & a,const monomial<gen> & b){
    if (a.index.front()!=b.index.front())
      return a.index.front()>b.index.front();
    return is_strictly_greater(a.value,b.value,context0);
  }

  bool try_sparse_factor_bi(polynome & pcur,int mult,factorization & f){
    int dim=pcur.dim;
    if (dim<=2)
      return false;
    /* Try sparse factorization using bivariate images of a factor of
       pcur(x,x1,x2,...) with [x1,x2,...]=[t^n1,t^n2,...]
       where n1,n2,...=1,1,... then 2,1,... then 1,2,...
    */
    polynome lcp(Tfirstcoeff(pcur)),lcpt;
    polynome pt,ptcont;
    index_t n(dim-1,1);
    for (;;){
      eval_tn(pcur,n,pt);
      pt=pt/Tlgcd(pt);
      eval_tn(lcp,n,lcpt);
#if POLY_SPARSE_BI
      factorization ft;
      gen extra_div_t;
      factor(pt,ptcont,ft,false,false,false,1,extra_div_t);
      if (ft.size()==1){
	f.push_back(facteur<polynome>(pcur,mult));
	return true;
      }
      factorization::const_iterator vit=ft.begin(),vitend=ft.end();
#else
      vecteur lv(makevecteur(vx_var,gen("t",context0)));
      gen dbg=_poly2symb(makesequence(pt,lv),context0);
      dbg=_factors(dbg,context0) ;
      if (dbg.type!=_VECT) return false;
      vecteur v=*dbg._VECTptr;
      if (v.size()==2){
	f.push_back(facteur<polynome>(pcur,mult));
	return true;
      }
      iterateur vit=v.begin(),vitend=v.end();
#endif
      // factor must be distinct from other factors 
      // by one of the degrees in x
      // select which factor will be reconstructed: 
      // multby=lcpt/lcoeff(factor of ft) must be as simple as possible
      // Once selected, the factor will be normalized by * by multby
      vector<int> seldegs;
      polynome multby,selp;
      for (;vit!=vitend;++vit){
#if POLY_SPARSE_BI
	if (vit->mult>1) break;
	const polynome & p=vit->fact;
#else
	++vit;
	if (*vit!=1) break;
	gen pg=_symb2poly(makesequence(*(vit-1),lv),context0);
	if (pg.type!=_POLY) break;
	const polynome & p = *pg._POLYptr;
#endif
	index_t D=p.degree();
	double ratio=p.coord.size()/(double(D[0])*D[1]);
	if (ratio>0.2)
	  return false;
	vector<int> degs;
	bool b=x_degrees(p,degs);
	if (degs==seldegs) break;
	polynome multbynew=lcpt/Tfirstcoeff(p);
	if (seldegs.empty() || (b && multbynew.coord.size()<multby.coord.size())){
	  if (!b){ 
	    // some coeffs are the same, dilate randomly 
	    // using -1, 1, 2, -2
	    vecteur lv(dim);
	    for (int i=0;i<dim;++i){
	      lv[i]=identificateur("x"+print_INT_(i));
	    }
	    gen pcurg=_poly2symb(makesequence(pcur,lv),context0);
	    vecteur lw(lv);
	    vecteur dilate=vranm(dim,4,context0);
	    for (int k=1;k<dim;++k){
	      int c=dilate[k].val;
	      switch (c){
	      case 0:
		dilate[k]=2;
		break;
	      case 1: case 2:
		dilate[k]=-1;
		break;
	      case 3:
		dilate[k]=2;
		break;
	      }
	    }
	    for (int k=1;k<dim;++k)
	      lw[k]=dilate[k]*lv[k];
	    pcurg=subst(pcurg,lv,lw,false,context0);
	    pcurg=_symb2poly(makesequence(pcurg,lv),context0);
	    if (pcurg.type!=_POLY)
	      return false;
	    polynome pcur_dilated=*pcurg._POLYptr;
	    factorization f_dilated;
	    if (!try_sparse_factor_bi(pcur_dilated,mult,f_dilated))
	      return false;
	    factorization::const_iterator fit=f_dilated.begin(),fitend=f_dilated.end();
	    for (;fit!=fitend;++fit){
	      pcurg=_poly2symb(makesequence(fit->fact,lv),context0);
	      for (int k=1;k<dim;++k)
		lw[k]=lv[k]/dilate[k];
	      pcurg=subst(pcurg,lv,lw,false,context0);
	      pcurg=_symb2poly(makesequence(pcurg,lv),context0);
	      if (pcurg.type!=_POLY)
		return false;
	      f.push_back(facteur<polynome>(*pcurg._POLYptr,fit->mult));
	    }
	    return true;
	  }
	  seldegs=degs;
	  multby=multbynew;
	  selp=multby*p;
	}
      }
      if (vit!=vitend){
	++n[0];
	if (n[0]>=4)
	  return false;
	continue;
      }
      // we will deduce x1^ in monomials by comparing with the same factor
      // of the bivariate factorization with n1=2 instead of n1=1
      // then x2^ with n1=1 and n2=2
      // If one bivariate image has less monomials than another one it is an unlucky n, use another one
      // If one bivariate image has more monomials, then we must throw everything and restart with this bivariate image
      // Once all monomials are done we should get a factor of pcur 
      // by extracting the primitive part of this factor
      sort(selp.coord.begin(),selp.coord.end(),lex_or_coeff_sort);
      polynome curp,recon(selp); recon.dim=pcur.dim;
      int increment=1,i=0;
      for (;i<n.size();){
	index_t n1(n);
	n1[i] += increment;
	int ni=n[i],n1i=n1[i];
	eval_tn(pcur,n1,pt);
	pt=pt/Tlgcd(pt);
#if POLY_SPARSE_BI
	factor(pt,ptcont,ft,false,false,false,1,extra_div_t);
	vit=ft.begin();vitend=ft.end();
#else
	dbg=_poly2symb(makesequence(pt,lv),context0);
	dbg=_factors(dbg,context0) ;
	if (dbg.type!=_VECT) return false;
	v=*dbg._VECTptr;
	iterateur vit=v.begin(),vitend=v.end();
#endif
	// lcoeff normalization
	eval_tn(lcp,n1,lcpt);
	// serch in factorization for seldeg x-degree pattern
	curp.coord.clear();
	for (;vit!=vitend;++vit){
#if POLY_SPARSE_BU
	  if (vit->mult>1){vit=vitend;} break;
	  const polynome & p=vit->fact;
#else
	  ++vit;
	  if (*vit!=1) break;
	  gen pg=_symb2poly(makesequence(*(vit-1),lv),context0);
	  if (pg.type!=_POLY) break;
	  const polynome & p = *pg._POLYptr;
#endif
	  vector<int> degs;
	  if (!x_degrees(p,degs)) break;
	  if (degs==seldegs){
	    curp=lcpt/Tfirstcoeff(p)*p;
	    break;
	  }
	}
	if (vit==vitend || curp.coord.empty()) break; // not found or not sqrfree
	// compare with selp
	if (curp.coord.size()<selp.coord.size()){ // unlucky
	  ++increment;
	  if (increment>3)
	    break;
	  continue;
	}
	sort(curp.coord.begin(),curp.coord.end(),lex_or_coeff_sort);
	if (curp.coord.size()>selp.coord.size()){
	  // selp was unlucky, restart
	  recon=selp=curp;
	  n=n1;
	  break;
	}
	// selp and curp size match, now compare monomial by monomial
	// and extract x[i] exponent in recon
	vector< monomial<gen> >::iterator rt=recon.coord.begin(),rtend=recon.coord.end(),st=selp.coord.begin(),ct=curp.coord.begin();
	for (;rt!=rtend;++rt,++st,++ct){
	  if (st->index[0]!=ct->index[0])
	    break;
	  int idx0=st->index[1];
	  int idx1=ct->index[1];
	  index_t I=rt->index.iref();
	  int delta=(idx1-idx0)/(n1i-ni);
	  if (i==0)
	    I[1]=delta;
	  else
	    I.push_back(delta);
	  if (i==n.size()-2){
	    for (int j=0;j<=i;++j){
	      idx1 -= I[j+1]*n1[j];
	    }
	    I.push_back(idx1/n1[i+1]);
	  }
	  rt->index=I;
	}
	if (rt!=rtend)
	  break;
	increment=1;
	if (i==n.size()-2) ++i;
	++i;
      }
      if (i<n.size()){
	// restart search
	++n[i];
	if (n[i]>=4)
	  return false;
	continue;
      }
      recon.tsort();
      // divide by reconstructed factor and restart factorization
      recon=recon/Tlgcd(recon);
      polynome quo,rem;
      if (!pcur.TDivRem(recon,quo,rem,false) || !is_zero(rem))
	return false;
      f.push_back(facteur<polynome>(recon,mult));
      pcur=quo;
      return try_sparse_factor_bi(pcur,mult,f);
    } // end endless for
  }

  void poly_truncate(const polynome & q,polynome & q1,int j){
    q1.coord.clear();
    vector< monomial<gen> >::const_iterator jt=q.coord.begin(),jtend=q.coord.end();
    for (;jt!=jtend;++jt){
      if (jt->index.total_degree()<j)
	q1.coord.push_back(*jt);
    }
  }

  // multiply keep only if total degree < maxdeg
  void mulpoly_truncate(const polynome & p,const polynome & q,polynome &res,int maxdeg){
    res.coord.clear();
    int dim=p.dim;
    polynome p1(dim),q1(dim),tmp(dim);
    for (int i=0;i<maxdeg;++i){
      // p1 total degree i of p, q1 total degree<maxdeg-i of q
      int j=maxdeg-i;
      // create p1 and q1
      p1.coord.clear();
      vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
      for (;it!=itend;++it){
	if (it->index.total_degree()==i)
	  p1.coord.push_back(*it);
      }
      poly_truncate(q,q1,j);
      // multiply, 
      mulpoly(p1,q1,tmp,0);
      // add to res
      p1.coord.clear();
      tmp.TAdd(res,p1);
      p1.coord.swap(res.coord);
    }
  }
	  
  // keep only monomials of total_degree==j without first degree
  void poly_truncate1(const polynome & q,polynome & q1,int j){
    q1.coord.clear();
    vector< monomial<gen> >::const_iterator it=q.coord.begin(),itend=q.coord.end();
    index_t::const_iterator jt,jtend;
    for (;it!=itend;++it){
      jt=it->index.begin()+1;
      jtend=it->index.end();
      int otherdeg;
      for (otherdeg=*jt,++jt;jt!=jtend;++jt){
	otherdeg += *jt;
      }
      if (otherdeg==j)
	q1.coord.push_back(*it);
    }
  }

  void other_deg(const polynome & p,vector<int> & pdeg){
    pdeg.reserve(p.coord.size()); pdeg.clear();
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      index_t::const_iterator jt,jtend;
      jt=it->index.begin()+1;
      //jtend=jt+dim-1;
      jtend=it->index.end();
      int otherdeg;
      for (otherdeg=*jt,++jt;jt<jtend;++jt){
	otherdeg += *jt;
      }
      pdeg.push_back(otherdeg);
    }
  }

  // multiply keep only if total degree excluding 1st deg == maxdeg
  void mulpoly_truncate1(const polynome & p,const polynome & q,polynome &res,int deg,polynome & p1,polynome & q1,polynome & tmp,vector<int> & pdeg,vector<int> & qdeg){
    bool eq=deg>=0;
    int maxdeg=eq?deg:-deg;
    res.coord.clear();
    int dim=p.dim;
    int ps=int(p.coord.size()),qs=int(q.coord.size());
    p1.coord.reserve(ps);
    other_deg(p,pdeg);
    other_deg(q,qdeg);
    const vector< monomial<gen> > & pcoord=p.coord;
    const vector< monomial<gen> > & qcoord=q.coord;
    for (int i=0;i<=maxdeg;++i){
      // p1 total degree <=i of p or ==i if deg>0, 
      // q1 total degree==maxdeg-i of q
      int j=maxdeg-i;
      // create p1 and q1
      p1.coord.clear();
      for (int k=0;k<ps;++k){
	int otherdeg=pdeg[k];
	if (eq?otherdeg==i:otherdeg<=i)
	  p1.coord.push_back(pcoord[k]);
      }
      q1.coord.clear();
      for (int k=0;k<qs;++k){
	if (qdeg[k]==j)
	  q1.coord.push_back(qcoord[k]);
      }
      // multiply, 
      mulpoly(p1,q1,tmp,0);
      // add to res
      p1.coord.clear();
      tmp.TAdd(res,p1);
      p1.coord.swap(res.coord);
    }
  }

	  
  bool try_hensel_lift_factor(const polynome & pcur,const polynome & F0,const factorization & v0,int mult,factorization & f){
    int dim=pcur.dim;
    int s=int(v0.size());
    polynome lcp(Tfirstcoeff(pcur));
    if (lcp.coord.back().index.back()!=0)
      return false;
    gen lcpb=lcp.coord.back().value;
    vector<polynome> lcoeffs(s,lcp);
    bool lcoeff_known=false;
    factorization::const_iterator F0it=v0.begin(),F0itend=v0.end();
    vector<modpoly> F0fact;
    for (;F0it!=F0itend;++F0it){
      if (F0it->mult>1)
	break;
      F0fact.push_back(modularize(F0it->fact,0,0));
      if (is_undef(F0fact.back()))
	return false;
    }
    if (pcur.dim>2 && lcp.coord.size()>1){
      // try bivariate factorization to compute a priori the leadings coefficients
      // that is factor pcur(x1,x2,0,...,0) = product p_i(x1,x2)
      // then factor lcoeff(pcur)(x2,x3,..,xn) = product q_j(x2,..,xn)
      // the lcoeff(pcur) corresponding to lcoeff(p_i)(x2) divides
      // the product of the q_j such that either q_j(x2,0,...,0) is constant
      // or gcd(q_j(x2,0,...,0),lcoeff(p_i)(x2)) is not constant
      // then we know multiples of the lcoeffs, we can therefore replace s, F0it, F0itend, lcoeffs
      polynome pcurx1x2;
      peval_xk_xn_zero(pcur,2,pcurx1x2);
      factorization fx1x2,flcoeff;
      vector<polynome> flcoeff0;
      polynome pcurx1x2cont=lgcd(pcurx1x2);
      gen extra_div=1;
      // if pcurx1x2cont is not 1, the following code may fail
      // example f:=3/5*a^2*b^2*c^2+129/20*a^2*b*c^3+18/5*a^2*b*c^2*d-1443/40*a^2*b*c^2+387/10*a^2*c^3*d-387*a^2*c^3-9/20*a^2*c^2*d+9/2*a^2*c^2-273/5*a*b^2*c^2*d+3/5*a*b^2*c^2-11739/20*a*b*c^3*d+129/20*a*b*c^3-18/5*a*b*c^2*d^2+1857/40*a*b*c^2*d-1443/40*a*b*c^2-387/10*a*c^3*d^2+4257/10*a*c^3*d-387*a*c^3+9/20*a*c^2*d^2-99/20*a*c^2*d+9/2*a*c^2+54*b^2*c^2*d^2-54*b^2*c^2*d+1161/2*b*c^3*d^2-1161/2*b*c^3*d-27/4*b*c^2*d^2+27/4*b*c^2*d; factor(f);
      if (
	  // is_one(pcurx1x2cont)
	  is_zero(pcurx1x2cont.lexsorted_degree())
	  ){
	truncate_xk_xn(pcurx1x2,2);
	if (lgcd(pcurx1x2).coord.size()>1)
	  return false;
	if (!factor(pcurx1x2,pcurx1x2cont,fx1x2,true,false,false,1,extra_div) || extra_div!=1)
	  return false;
	// fx1x2 contains factorization of pcur(x1,x2,0,...0)
	// now find factorization of lcoeff(pcur)(x2,...,xn)
	polynome pcur_lcoeff(Tfirstcoeff(pcur)),pcur_lcoeffcont,pcur_lcoeff_sqfftest;
	peval_xk_xn_zero(pcur_lcoeff,2,pcur_lcoeff_sqfftest);
	pcur_lcoeff_sqfftest=pcur_lcoeff_sqfftest.trunc1();
	// if (gcd(pcur_lcoeff_sqfftest,pcur_lcoeff_sqfftest.derivative()).lexsorted_degree()) return false;
	if (!factor(pcur_lcoeff.trunc1(),pcur_lcoeffcont,flcoeff,false,false,false,1,extra_div) || extra_div!=1)
	  return false;
	factorization::iterator jt=flcoeff.begin(),jtend=flcoeff.end();
	polynome constante(pcur_lcoeffcont.untrunc1()*pcurx1x2cont),tmp;
	for (;jt!=jtend;++jt){
	  jt->fact=jt->fact.untrunc1();
	  peval_xk_xn_zero(jt->fact,2,tmp); // should only depend on x2
	  if (Tis_constant(tmp))
	    constante=constante*pow(jt->fact,jt->mult);
	  //else 
	  flcoeff0.push_back(tmp);
	} // flcoeff0 contains the list of factors of lcoeff(pcur) evaled at 0
	F0it=fx1x2.begin();
	F0itend=fx1x2.end();
	s=int(F0itend-F0it);
	F0fact.clear();
	lcoeffs.clear();
	modpoly piF(1,1);
	for (;F0it!=F0itend;++F0it){
	  if (F0it->mult>1)
	    break;
	  polynome p (F0it->fact); // depends on x1 and x2
	  untruncate_xk_xn(p,dim);
	  peval_xk_xn_zero(p,1,tmp); // make x2=0
	  truncate_xk_xn(tmp,1);
	  modpoly Fi(modularize(tmp,0,0));
	  if (is_undef(Fi))
	    return false;
	  if (gcd(piF,Fi,0).size()>1)
	    return false;
	  piF=piF*Fi;
	  F0fact.push_back(Fi);
	  // corresponding lcoeff
	  p=Tfirstcoeff(p);
	  polynome tmp2=constante;
	  for (jt=flcoeff.begin(),jtend=flcoeff.end();jt!=jtend;++jt){
	    for (int m=jt->mult;m>0;--m){
	      polynome G(flcoeff0[jt-flcoeff.begin()]);
	      if (Tis_constant(simplify(p,G)))
		break;
	      else {
		tmp2 = tmp2 * jt->fact;
		// mark jt->fact as used
		--jt->mult;
	      }
	    }
	  }
	  lcoeffs.push_back(tmp2);
	}
	lcoeff_known=true;
      } // if is_one(pcurx1x2cont)
    }
    if (F0it!=F0itend)
      return false;
    // ok each factor of F0=pcur|0 is square free, they are prime together
    // if lcp has too much terms it will take too long, because
    // we must multiply by product(lcoeffs)/lcp
    // next check was >100 but then heuristic factorization fails
    // (should also depends on the size of the coeffs and number of variables...)
    if (!lcoeff_known && pow(lcp,s-1).coord.size()>1000)
      return false;
    // we will lift pcur*product(lcoeffs)/lcp = product_i F0fact[i]*lcoeffs[i](b)/lcoeff(F0fact[i])
    for (int i=0;i<s;++i){
      gen lcoeff=F0fact[i].front();
      mulmodpoly(F0fact[i],lcoeffs[i].coord.back().value/lcoeff,F0fact[i]);
    }
    polynome pcur_adjusted(pcur);
    if (!is_one(lcp)){
      if (lcoeff_known){
	polynome temp(lcoeffs[0]);
	for (int i=1;i<s;++i){
	  temp = temp * lcoeffs[i];
	}
	temp = temp / lcp;
	pcur_adjusted = pcur_adjusted * temp;
      }
      else {
	for (int i=1;i<s;++i){
	  pcur_adjusted =pcur_adjusted*lcoeffs[i];
	}
      }
    }
    // Perhaps the check on lcp should be made here with pcur_adjusted
    vector<modpoly> u;
    if (!egcd(F0fact,0,u))
      return false;// sum_j U_j * product_{i \neq j} F0fact_i = 1
    // factor out common deno
    // sum_j U_j * product_{i \neq j} F0fact_i = D
    vecteur den(s);
    gen D(1);
    for (int i=0;i<s;++i){
      lcmdeno(u[i],den[i],context0);
      D=lcm(D,den[i]);
    }
    for (int i=0;i<s;++i)
      mulmodpoly(u[i],D/den[i],u[i]);
    vector<polynome> P(s),P0(s),U(s);
    vecteur b(pcur_adjusted.dim-1);
    for (int i=0;i<s;++i){
      U[i].dim=pcur_adjusted.dim;
      P[i].dim=pcur_adjusted.dim;
      P0[i].dim=pcur_adjusted.dim;
      modpoly::const_iterator it=F0fact[i].begin(),itend=F0fact[i].end();
      int deg=int(itend-it)-1;
      P[i]=lcoeffs[i].trunc1().untrunc1(deg);
      for (int n=0;it!=itend;++it,++n){
	if (!is_zero(*it)){
	  if (n)
	    P[i].coord.push_back(monomial<gen>(*it,deg-n,1,pcur_adjusted.dim));
	  P0[i].coord.push_back(monomial<gen>(*it,deg-n,1,pcur_adjusted.dim));
	}
      }
      U[i].dim=pcur_adjusted.dim;
      it=u[i].begin(); itend=u[i].end();
      deg=int(itend-it)-1;
      for (int n=0;it!=itend;++it,++n){
	if (!is_zero(*it))
	  U[i].coord.push_back(monomial<gen>(*it,deg-n,1,pcur_adjusted.dim));
      }
      // CERR << Tcontent(U[i]) << endl;
    }
    polynome quo(dim),rem(dim),tmp(dim);
    // we have now pcur_adjusted = product P_i + O(total_degree>=1)
    int Total=pcur_adjusted.total_degree();
    // lift to pcur_adjusted = product P_i + O(total_degree>=k+1)
    // for deg from 1 to total_degree(pcur_adjusted)
    // P_i += (pcur_adjusted-product P_i) * U_j mod total_degree(k+1)
#if 1 // def EZGCD_DEGONLY
    if (is_zero(b)){
      polynome tmp4(dim),tmp5(dim),tmp6(dim),prod(dim);
      vector<int> tmpi1,tmpi2;
      for (int deg=1;deg<=Total;++deg){
	prod=P[s-2];
	for (int i=s-3;i>=0;--i){
	  // reduce_poly(prod * P[i],b,deg+1,prod); // keep up to deg
	  tmp.coord.clear();
	  mulpoly_truncate1(prod,P[i],tmp,-deg,tmp4,tmp5,tmp6,tmpi1,tmpi2);
	  prod.coord.swap(tmp.coord);
	  //if (prod!=prod1) CERR << "err " << deg << endl;
	} // end loop on i
	mulpoly_truncate1(prod,P[s-1],tmp,deg,tmp4,tmp5,tmp6,tmpi1,tmpi2);
	prod.coord.swap(tmp.coord);
	poly_truncate1(pcur_adjusted,tmp,deg);
	prod = tmp - prod;
	if (prod.coord.empty()){
	  // check total degrees
	  int tdeg=0;
	  for (int i=0;i<s;++i)
	    tdeg += P[i].total_degree();
	  if (tdeg==Total){ 
	    if (deg!=Total){
	      prod=P[s-1];
	      for (int i=s-2;i>=0;--i){
		// prod = prod * P[i]; 
		tmp.coord.clear();
		mulpoly(prod,P[i],tmp,0);
		prod.coord.swap(tmp.coord);
	      }
	      // N.B. prod==pcur_adjusted does not always work!
	      if ((prod-pcur_adjusted).coord.empty())
		deg=Total;
	    }
	    if (deg==Total){
	      for (int i=0;i<s;++i){
		f.push_back(facteur<polynome>(P[i]/lgcd(P[i]),mult));
	      }
	      return true;
	    }
	  }
	  continue;
	}
	//CERR << Tcontent(prod) << endl;
	for (int i=0;i<s;++i){
	  // U[i] depends only on 1st var no need to reduce
	  mulpoly(prod,U[i],rem,0);
	  //CERR << "deg " << deg << " " << Tcontent(rem) << endl;
	  if (!divrem1(rem,P0[i],quo,tmp,0) && !rem.TDivRem1(P0[i],quo,tmp,true,0))
	    return false;
	  rem.coord.swap(tmp.coord); // poly_truncate1(tmp,rem,deg);
	  // divide by D
	  vector< monomial<gen> >::const_iterator r1=rem.coord.begin(),r2=rem.coord.end();
	  Div<gen>(r1,r2,D,rem.coord);
	  P[i] = P[i] + rem;
	}
      }
    } // end if (is_zero(b))
    else
#endif
    for (int deg=1;deg<=Total;++deg){
      polynome prod(P[s-1]);
      for (int i=s-2;i>=0;--i){
	// reduce_poly(prod * P[i],b,deg+1,prod); // keep up to deg
	tmp.coord.clear();
	mulpoly(prod,P[i],tmp,0);
	reduce_poly(tmp,b,deg+1,prod);
      }
      prod = reduce_poly(pcur_adjusted,b,deg+1) - prod;
      if (prod.coord.empty()){
	// check total degrees
	int tdeg=0;
	for (int i=0;i<s;++i)
	  tdeg += P[i].total_degree();
	if (tdeg==Total){ 
	  if (deg!=Total){
	    prod=P[0];
	    for (int i=1;i<s;++i){
	      // prod = prod * P[i]; 
	      tmp.coord.clear();
	      mulpoly(prod,P[i],tmp,0);
	      swap(tmp,prod);
	    }
	    // N.B. prod==pcur_adjusted does not always work!
	    if ((prod-pcur_adjusted).coord.empty())
	      deg=Total;
	  }
	  if (deg==Total){
	    for (int i=0;i<s;++i){
	      f.push_back(facteur<polynome>(P[i]/lgcd(P[i]),mult));
	    }
	    return true;
	  }
	}
	continue;
      }
      //CERR << Tcontent(prod) << endl;
      for (int i=0;i<s;++i){
	// U[i] depends only on 1st var no need to reduce
	mulpoly(prod,U[i],rem,0);
	//CERR << "deg " << deg << " " << Tcontent(rem) << endl;
	if (!divrem1(rem,P0[i],quo,tmp,0) && !rem.TDivRem1(P0[i],quo,tmp,true,0))
	  return false;
	reduce_poly(tmp,b,deg+1,rem);
	// divide by D
	vector< monomial<gen> >::const_iterator r1=rem.coord.begin(),r2=rem.coord.end();
	Div<gen>(r1,r2,D,rem.coord);
	P[i] = P[i] + rem;
      }
    } // end for
    // FIXME combine factors 
    if (s==2){
      f.push_back(facteur<polynome>(pcur,mult));
      return true;
    }
    int nfact=s;
    index_t pcur_deg(pcur_adjusted.degree());
    vector<int> test(1);
    for (int k=1;k<=nfact/2;){
      if (debug_infolevel)
	COUT << CLOCK() << "Testing combination of " << k << " factors" << endl;
      // FIXME check on cst coeff
      if (1){
	polynome prodP(P[test[0]]);
	for (int i=1;i<k;++i){
	  mulpoly(prodP,P[test[i]],rem,0);
	  reduce_poly(rem,b,Total+1,prodP);
	}
	if (divrem1(pcur_adjusted,prodP,quo,rem,1) && rem.coord.empty()){
	  // factor found
	  pcur_adjusted=quo;
	  f.push_back(facteur<polynome>(prodP/lgcd(prodP),mult));
	  for (int i=k-1;i>=0;--i){
	    P.erase(P.begin()+test[i]);
	  }
	  nfact -= k;
	  for (int i=0;i<k;++i)
	    test[i]=i;
	  continue;
	}
      }
      if (!next(test,k,nfact)){
	++k;
	test=vector<int>(k);
	for (int i=0;i<k;++i)
	  test[i]=i;
      }
    }
    f.push_back(facteur<polynome>(pcur_adjusted/lgcd(pcur_adjusted),mult));
    return true;
  }

  // find u,v,d s.t. u*p+v*q=d by Hensel lift
  bool try_hensel_egcd(const polynome & p,const polynome & q,polynome &u,polynome &v,polynome & d){
    // check # of variables
    //if (p.dim<=1 || p.dim!=q.dim)
      return false;
    // check that 0 is a good evaluation point (same degree, gcd==1)
    vecteur b(1,0);
    polynome p0(1),q0(1);
    find_good_eval(p,q,p0,q0,b,(debug_infolevel>=2));
    if (!is_zero(b))
      return false;
    int pdeg=p.lexsorted_degree(),qdeg=q.lexsorted_degree();
    if (p0.lexsorted_degree()!=pdeg || q0.lexsorted_degree()!=qdeg)
      return false;
    gen g=gcd(pdeg,qdeg);
    if (g.type==_POLY && g._POLYptr->lexsorted_degree())
      return false;
    // Bezout at other variables==0
    polynome u0(1),v0(1),d0(1);
    egcd(p0,q0,u0,v0,d0); // d0 must be constant
    // now p*u0+q*v0-d0=O(1) where O(k) means of order >= k wrt other variables
    // p*uk+q*vk-d0=O(k) -> p*(uk+uk1)+q*(v+vk1)-d0=O(k+1)
    // with uk1 and vk1=O(k+1)
    // we have p0*uk1+q0*vk1=d0-p*uk-q*vk=yk
    // hence uk1=yk*u0/d0 % q0, vk1=yk*v0/d0 % p0
    // rational (Pade-like) reconstruction uk=fraction of polynomials 
    // with max degree wrt other variables <=k/2
    // once both fractions corresp. to uk and vk stabilizes, check identity
  }

  // Hensel linear or quadratic lift
  // FIXME Quadratic lift currently works only if lcp is constant
  // Lift the equality p(b)=qb*rb [where b is a vecteur like for peval
  // assumed to have p.dim-1 coordinates] to p=q*r mod (X-b)^deg
  // Assuming that lcoeff(q)=lcp, lcoeff(r)=lcp, lcoeff(p)=lcp^2
  // If you want to find factors of a poly P such that P(b)=Qb*Rb, 
  // if lcp is the leading coeff of P
  // then p=P*lcp, qb=Qb*lcp(b)/lcoeff(Qb), rb=Rb*lcp(b)/lcoeff(Rb)
  bool hensel_lift(const polynome & p, const polynome & lcp, const polynome & qb, const polynome & rb, const vecteur & b,polynome & q, polynome & r,bool linear_lift,double maxop){
    if (maxop)
      linear_lift=true; // otherwise please adjust number of operations to do!
    double nop=0;
    int dim=p.dim;
    int deg=total_degree(p);
    if ( (qb.dim!=1) || (rb.dim!=1) || (dim==1) ){
#ifdef NO_STDEXCEPT
      return false;
#else
      setsizeerr(gettext("Bad dimension for qb or rb or b or degrees"));
#endif
    }
    polynome qu(1),ru(1),qbd(1);
    egcd(qb,rb,qu,ru,qbd);
    if (!Tis_constant(qbd)){
#ifdef NO_STDEXCEPT
      return false;
#else
      setsizeerr(gettext("qb and rb not prime together!"));
#endif
    }
    gen qrd(qbd.coord.front().value);
    // now we have qu*qb+ru*rb=qrd with 1-d polynomials
    change_dim(qu,dim);
    change_dim(ru,dim);
    // adjust dim & leading coeff of q and r by removing current leading coeff
    // and replace by lcp
    q=qb;
    r=rb;
    change_dim(q,dim);
    change_dim(r,dim);
    polynome q0(q),r0(r);
    index_t qshift(q.dim);
    qshift[0]=q.lexsorted_degree();
    q=q+(lcp-Tfirstcoeff<gen>(q)).shift(qshift);
    qshift[0]=r.lexsorted_degree();
    r=r+(lcp-Tfirstcoeff<gen>(r)).shift(qshift);    
    polynome p_qr(dim);
    for (int n=1;;){
      // qu*q+ru*r=qrd [n] (it's exact at the loop begin)
      // p=q*r [n] where [n] means of total valuation >= n
      // at the beginning n=1
      // enhanced at order 2*n by adding q',r' of valuation >=n
      // p-(q+q')*(r+r')=p-q*r - (r'q+q'r)-q'*r'
      // hence if we put r', q' such that p-q*r=(r'q+q'r) [2n]
      // we are done. Since p-q*r is of order [n], we get the solution
      // r'=qu*(p-qr)/qrd and q'=ru*(p-qr)/qrd
      if (debug_infolevel)
	CERR << "// Hensel " << n << " -> " << deg << endl;
      if (n>deg)
	return false;
      if (linear_lift)
	++n;
      else
	n=2*n;
      if (maxop>0){
	nop += double(q.coord.size())*r.coord.size();
	if (debug_infolevel)
	  CERR << "EZGCD " << nop << ":" << maxop << endl;
	if (nop>maxop)
	  return false;
      }
      p_qr=reduce_poly(p-q*r,b,deg);
      if (is_zero(p_qr))
	return true;
      if (n>deg)
	n=deg;
      p_qr=reduce_poly(p_qr,b,n);
      polynome qprime(reduce_poly(ru*p_qr,b,n)),qquo(qprime.dim),qrem(qprime.dim);
      polynome rprime(reduce_poly(qu*p_qr,b,n)),rquo(rprime.dim),rrem(qprime.dim);
      // reduction of qprime and rprime with respect to the main variable
      // we know that
      // (*) degree(p_qr) < degree(qr)
      // where degree is the degree wrt the main variable
      // since the leading coeffs of q and r are still adjusted
      // Then there is a unique solution to (*) with
      // degree(qprime)<degree(q), degree(rprime)<degree(r)
      if (linear_lift){
	reduce_divrem(qprime,q0,b,n,qquo,qrem);
	reduce_divrem(rprime,r0,b,n,rquo,rrem);
      }
      else {
	reduce_divrem(qprime,q,b,n,qquo,qrem);
	reduce_divrem(rprime,r,b,n,rquo,rrem);
      }
      // reduction of qprime and rprime with respect to the other variables
      // maybe we should check that q and r below have integer coeff
      q=q+inv(qrd,context0)*qrem; 
      r=r+inv(qrd,context0)*rrem;
      if (!linear_lift && (n<=deg/2)){
	// Now we modify qu and ru so that
	// (qu+qu')*q+(ru+ru')*r=qrd [2n]
	// therefore qu'*q+ru'*r=qrd-(qu*q+ru*r) [2n]
	// hence qu'= qu*[qrd-(qu*q+ru*r)]/qrd, ru'=ru*[qrd-(qu*q+ru*r)]/qrd
	p_qr=polynome(monomial<gen>(qrd,0,dim))-reduce_poly(qu*q+ru*r,b,n);
	qprime=reduce_poly(qu*p_qr,b,n);
	rprime=reduce_poly(ru*p_qr,b,n);
	reduce_divrem(qprime,r,b,n,qquo,qrem);
	reduce_divrem(rprime,q,b,n,rquo,rrem);
	qu=qu+inv(qrd,context0)*qrem; // should check that qu and ru have integer coeff
	ru=ru+inv(qrd,context0)*rrem;
      }
    }
  }

  // Replace the last coordinates of p with b instead of the first
  gen peval_back(const polynome & p,const vecteur & b){
    int pdim=p.dim,bdim=int(b.size());
    vector<int> cycle(pdim);
    int deltad=pdim-bdim;
    for (int i=0;i<bdim;++i)
      cycle[i]=i+deltad;
    for (int i=bdim;i<pdim;++i)
      cycle[i]=i-bdim;
    polynome pp(p);
    pp.reorder(cycle);
    int save=debug_infolevel;
    if (debug_infolevel)
      --debug_infolevel;
    gen res(peval(pp,b,0));
    debug_infolevel=save;
    return res;
  }

  polynome peval_1(const polynome & p,const vecteur &v,const gen & mod){
#if defined(NO_STDEXCEPT) && !defined(RTOS_THREADX) && !defined(VISUALC) && !defined(NUMWORKS)
    assert(p.dim==signed(v.size()+1));
#else
    if (p.dim!=signed(v.size()+1))
      setsizeerr(gettext("peval_1"));
#endif
    polynome res(1);
    index_t i(1);
    std::vector< monomial<gen> >::const_iterator it=p.coord.begin();
    std::vector< monomial<gen> >::const_iterator itend=p.coord.end();
    for (;it!=itend;){
      i[0]=it->index.front();
      polynome pactuel(Tnextcoeff<gen>(it,itend));
      gen g(peval(pactuel,v,mod));
      if ( (g.type==_POLY) && (g._POLYptr->dim==0) )
	g=g._POLYptr->coord.empty()?0:g._POLYptr->coord.front().value;
      if (!is_zero(g))
	res.coord.push_back(monomial<gen>(g,i));
    }
    return res;
  }

  polynome unmodularize(const vector<int> & a){
    if (a.empty())
      return polynome(1);
    polynome res(1);
    vector< monomial<gen> > & v=res.coord;
    index_t i;
    int deg=int(a.size())-1;
    i.push_back(deg);
    vector<int>::const_iterator it=a.begin();
    vector<int>::const_iterator itend=a.end();
    for (;it!=itend;++it,--i[0]){
      if (*it)
	v.push_back(monomial<gen>(*it,i));
    }
    return res;
  }

  static bool convert_from_truncate(const vector< T_unsigned<int,hashgcd_U> > & p,hashgcd_U var,polynome & P){
    P.dim=1;
    P.coord.clear();
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    P.coord.reserve(itend-it);
    index_t i(1);
    for (;it!=itend;++it){
      i.front()=it->u/var;
      P.coord.push_back(monomial<gen>(gen(it->g),i));
    }
    return true;
  }

  // return true if a good eval point has been found
  bool find_good_eval(const polynome & F,const polynome & G,polynome & Fb,polynome & Gb,vecteur & b,bool debuglog,const gen & mod){
    int Fdeg=int(F.lexsorted_degree()),Gdeg=int(G.lexsorted_degree()),nvars=int(b.size());
    gen Fg,Gg;
    int essai=0;
    int dim=F.dim;
    if ( //false &&
	mod.type==_INT_ && mod.val){
      int modulo=mod.val;
      std::vector<hashgcd_U> vars(dim);
      vector< T_unsigned<int,hashgcd_U> > f,g,fb,gb;
      index_t d(dim);
      if (convert(F,G,d,vars,f,g,modulo)){
	vector<int> bi(dim-1);
	vecteur2vector_int(b,modulo,bi);
	for (;;++essai){
	  if (modulo && essai>modulo)
	    return false;
	  peval_x2_xn<int,hashgcd_U>(f,bi,vars,fb,modulo);
	  if (&F==&G)
	    gb=fb;
	  else
	    peval_x2_xn(g,bi,vars,gb,modulo);	  
	  if (!fb.empty() && !gb.empty() && int(fb.front().u/vars.front())==Fdeg && int(gb.front().u/vars.front())==Gdeg){
	    // convert back fb and gb and return true
	    convert_from_truncate(fb,vars.front(),Fb);
	    convert_from_truncate(gb,vars.front(),Gb);
	    return true;
	  }
	  for (int i=0;i<dim-1;++i)
	    bi[i]=std_rand() % modulo;
	}
      }
    }
    for (;;++essai){
      if (!is_zero(mod) && essai>mod.val)
	return false;
      if (debuglog)
	CERR << "Find_good_eval " << CLOCK() << " " << b << endl;
      Fb=peval_1(F,b,mod);
      if (debuglog)
	CERR << "Fb= " << CLOCK() << " " << gen(Fb) << endl;
      if (&F==&G)
	Gb=Fb;
      else {
	Gb=peval_1(G,b,mod);
      }
      if (debuglog)
	CERR << "Gb= " << CLOCK() << " " << gen(Gb) << endl;
      if ( (Fb.lexsorted_degree()==Fdeg) && (Gb.lexsorted_degree()==Gdeg) ){
	if (debuglog)
	  CERR << "FOUND good eval" << CLOCK() << " " << b << endl;
	return true;
      }
      b=vranm(nvars,0,0); // find another random point
    }
  }

  // It is probably required that 0 is a good evaluation point to
  // have an efficient algorithm
  // max_gcddeg is used when ezgcd was not successfull to find
  // the gcd even with 2 evaluations leading to the same gcd degree
  // in this case ezgcd calls itself with a bound on the gcd degree
  // is_sqff is true if we know that F_orig or G_orig is squarefree
  // is_primitive is true if F_orig and G_orig is primitive
  bool ezgcd(const polynome & F_orig,const polynome & G_orig,polynome & GCD,bool is_sqff,bool is_primitive,int max_gcddeg,double maxop){
    if (debug_infolevel)
      CERR << "// Starting EZGCD dimension " << F_orig.dim << endl;
    if (F_orig.dim<2){
#ifdef NO_STDEXCEPT
      return false;
#else
      setsizeerr(gettext("Args must be multivariate polynomials"));
#endif
    }
    int Fdeg=F_orig.lexsorted_degree(),Gdeg=G_orig.lexsorted_degree();
    polynome F(F_orig.dim),G(F_orig.dim),cF(F_orig.dim),cG(F_orig.dim),cFG(F_orig.dim);
    if (is_primitive){
      cFG=polynome(monomial<gen>(plus_one,0,F_orig.dim));
      cF=cFG;
      cG=cFG;
      F=F_orig;
      G=G_orig;
    }
    else {
      cF=Tlgcd(F_orig);
      cG=Tlgcd(G_orig);
      cFG=gcd(cF.trunc1(),cG.trunc1()).untrunc1();
      F=F_orig/cF;
      G=G_orig/cG;
    }
    if (Tis_constant(F) || Tis_constant(G) ){
      GCD=cFG;
      return true;
    }
    polynome lcF(Tfirstcoeff(F)),lcG(Tfirstcoeff(G));
    double nop=double(lcF.coord.size())*double(F.coord.size())+double(lcG.coord.size())*double(G.coord.size());
    if (maxop>0){
      if (maxop<nop/10)
	return false;
    }
    vecteur b(F.dim-1);
    polynome Fb(1),Gb(1),Db(1);
    int old_gcddeg;
    for (;;){
      if (debug_infolevel)
	CERR << "// Back to EZGCD dimension " << F_orig.dim << endl;
      find_good_eval(F,G,Fb,Gb,b);
      Db=gcd(Fb,Gb);
      old_gcddeg=Db.lexsorted_degree();
      if (debug_infolevel)
	CERR << "// Eval at " << b << " gcd  degree " << old_gcddeg << endl;
      if (!old_gcddeg){
	GCD=cFG;
	return true;
      }
      if ( (!max_gcddeg) || (old_gcddeg<max_gcddeg) )
	break;
    }
    polynome new_Fb(1),new_Gb(1),quo(F.dim),rem(F.dim);
    for (;;){
      vecteur new_b(vranm(F.dim-1,0,0));
      find_good_eval(F,G,new_Fb,new_Gb,new_b);
      if (b==new_b)
	continue;
      polynome new_Db(gcd(new_Fb,new_Gb));
      int new_gcddeg=new_Db.lexsorted_degree();
      if (debug_infolevel)
	CERR << "// Eval at " << new_b << " gcd  degree " << new_gcddeg << endl;
      if (!new_gcddeg){
	GCD=cFG;
	return true;
      }
      if (new_gcddeg>old_gcddeg) // bad evaluation point
	continue;
      if (new_gcddeg==old_gcddeg) // might be a good guess!
	break;
      old_gcddeg=new_gcddeg;
      Db=new_Db;
      Fb=new_Fb;
      Gb=new_Gb;
      b=new_b;
    }
    // Found two times the same degree, try to lift!
    if ( (Fdeg<=Gdeg) && (old_gcddeg==Fdeg) ){
      if (G.TDivRem1(F,quo,rem) && rem.coord.empty()){
	GCD= F*cFG;
	return true;
      }
    }
    if ( (Gdeg<Fdeg) && (old_gcddeg==Gdeg)  ){
      if (G.TDivRem1(F,quo,rem) && rem.coord.empty()){
	GCD=G*cFG;
	return true;
      }
    }
    if (debug_infolevel)
      CERR << "// EZGCD degree " << old_gcddeg << endl;
    if ( (old_gcddeg==Fdeg) || (old_gcddeg==Gdeg) )
      return false;
    // this algo is fast if 0 is a good eval & the degree of the gcd is small
    if (!is_zero(b))
      return false;
    //if ( (old_gcddeg>4) && (old_gcddeg>Fdeg/4) && (old_gcddeg>Gdeg/4) )
    //  return false;
    polynome cofacteur(Fb/Db);
    if (Tis_constant(gcd(cofacteur,Db))){
      // lift Fb/Db *Db, more precisely insure that lc of each factor
      // is lcF(b)
      gen lcFb(peval_back(lcF,b));
      if (lcFb.type==_POLY)
	lcFb=lcFb._POLYptr->coord.front().value;
      Db=(lcFb*Db)/Db.coord.front().value;
      cofacteur=(lcFb*cofacteur)/cofacteur.coord.front().value;
      polynome liftF(F*lcF);
      polynome D(F_orig.dim),cofacteur_F(F_orig.dim),quo,rem;
      if (hensel_lift(liftF,lcF,cofacteur,Db,b,cofacteur_F,D,!Tis_constant(lcF),maxop) ){
	D=D/Tlgcd(D);
	if (F.TDivRem1(D,quo,rem) && is_zero(rem) && G.TDivRem1(D,quo,rem) && is_zero(rem)){
	  GCD=D*cFG;
	  return true;
	}
      }
      return false;
    }
    cofacteur=Gb/Db;
    if (Tis_constant(gcd(cofacteur,Db))){
      // lift Gb/Db *Db, more precisely insure that lc of each factor
      // is lcG(b)
      gen lcGb(peval_back(lcG,b));
      if (lcGb.type==_POLY)
	lcGb=lcGb._POLYptr->coord.front().value;
      Db=(lcGb*Db)/Db.coord.front().value;
      cofacteur=(lcGb*cofacteur)/cofacteur.coord.front().value;
      polynome liftG(G*lcG);
      polynome D(G_orig.dim),cofacteur_G(G_orig.dim),quo,rem;
      if (hensel_lift(liftG,lcG,cofacteur,Db,b,cofacteur_G,D,!Tis_constant(lcG),maxop) ){
	D=D/Tlgcd(D);
	if (F.TDivRem1(D,quo,rem) && is_zero(rem) && G.TDivRem1(D,quo,rem) && is_zero(rem)){
	  GCD=D*cFG;
	  return true;
	}
      }
      return false;
    }
    // FIXME find an integer j such that (F+jG)/D_b is coprime with D_b
    return false;
  }

  // algorithm=0 for HEUGCD, 1 for PRS, 2 for EZGCD, 3 for MODGCD
  static gen heugcd_psrgcd_ezgcd_modgcd(const gen & args,int algorithm,GIAC_CONTEXT){
    vecteur & v=*args._VECTptr;
    gen p1(v[0]),p2(v[1]),n1,n2,d1,d2;
    vecteur lv;
    if ( (v.size()==3) && (v[2].type==_VECT) )
      lv=*v[2]._VECTptr;
    lvar(p1,lv);
    lvar(p2,lv);
    p1=e2r(p1,lv,contextptr);
    fxnd(p1,n1,d1);
    p2=e2r(p2,lv,contextptr);
    fxnd(p2,n2,d2);
    gen res,np_simp,nq_simp,d_content;
    polynome p,q,p_gcd;
    if ( (n1.type!=_POLY) || (n2.type!=_POLY) )
      res=gcd(n1,n2,contextptr);
    else {
      polynome pres;
      bool result=false;
      switch(algorithm){
      case 0:
	p_gcd.dim=n1._POLYptr->dim;
	result=gcdheu(*n1._POLYptr,*n2._POLYptr,p,np_simp,q,nq_simp,p_gcd,d_content,true);
	pres=p_gcd*d_content;
	break;
      case 1:
	pres=gcdpsr(*n1._POLYptr,*n2._POLYptr);
	result=true;
	break;
      case 2:
	result=ezgcd(*n1._POLYptr,*n2._POLYptr,pres);
	break;
      case 3:
	result=gcd_modular_algo(*n1._POLYptr,*n2._POLYptr,pres,false);
	break;
      }
      if (result)
	res=pres;
      else
	return gensizeerr(gettext("GCD not successfull"));
    }
    return r2e(res,lv,contextptr);
  }

  gen _ezgcd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return symbolic(at_ezgcd,args);
    return heugcd_psrgcd_ezgcd_modgcd(args,2,contextptr);
  }
  static const char _ezgcd_s []="ezgcd";
  static define_unary_function_eval (__ezgcd,&_ezgcd,_ezgcd_s);
  define_unary_function_ptr5( at_ezgcd ,alias_at_ezgcd,&__ezgcd,0,true);
  
  gen _modgcd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return symbolic(at_modgcd,args);
    return heugcd_psrgcd_ezgcd_modgcd(args,3,contextptr);
  }
  static const char _modgcd_s []="modgcd";
  static define_unary_function_eval (__modgcd,&_modgcd,_modgcd_s);
  define_unary_function_ptr5( at_modgcd ,alias_at_modgcd,&__modgcd,0,true);
  
  gen _heugcd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return symbolic(at_heugcd,args);
    return heugcd_psrgcd_ezgcd_modgcd(args,0,contextptr);
  }
  static const char _heugcd_s []="heugcd";
  static define_unary_function_eval (__heugcd,&_heugcd,_heugcd_s);
  define_unary_function_ptr5( at_heugcd ,alias_at_heugcd,&__heugcd,0,true);

  gen _psrgcd(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return symbolic(at_psrgcd,args);
    return heugcd_psrgcd_ezgcd_modgcd(args,1,contextptr);
  }
  static const char _psrgcd_s []="psrgcd";
  static define_unary_function_eval (__psrgcd,&_psrgcd,_psrgcd_s);
  define_unary_function_ptr5( at_psrgcd ,alias_at_psrgcd,&__psrgcd,0,true);


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
