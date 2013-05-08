// -*- mode:C++ ; compile-command: "g++-3.4 -DHAVE_CONFIG_H -I. -I..  -DIN_GIAC -g -c sym2poly.cc" -*-
#include "giacPCH.h"
/*
 *  This file implements several functions that work on univariate and
 *  multivariate polynomials and rational functions.
 *  These functions include polynomial quotient and remainder, GCD and LCM
 *  computation, factorization and rational function normalization. */

/*
 *  Copyright (C) 2000,2007 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#include "gen.h"
#include "sym2poly.h"
#include "usual.h"
#include "unary.h"
#include "subst.h"
#include "modpoly.h"
#include "alg_ext.h"
#include "solve.h"
#include "input_parser.h"
#include "ezgcd.h"
#include "prog.h"
#include "ifactor.h"
#include "poly.h"
#include "plot.h"
#include "misc.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // "instantiate" debugging functions
  void dbgprint(const polynome &p){
    p.dbgprint();
  }

  void dbgprint(const gen & e){
    e.dbgprint();
  }

  vecteur cdr_VECT(const vecteur & l){
    if (l.empty())
      return vecteur(l);
    vecteur::const_iterator it=l.begin(),itend=l.end();
    vecteur res;
    ++it;
    for (;it!=itend;++it)
      res.push_back(*it);
    return vecteur(res);
  }

  //***************************
  // functions relative to lvar
  //***************************
  int equalposcomp(const vecteur & l,const gen & e){
    int n=1;
    for (vecteur::const_iterator it=l.begin();it!=l.end();++it){
      if ((*it)==e)
	return(n);
      else
	n++;
    }
    return(0);
  }

  void addtolvar(const gen & e, vecteur & l){
    if (equalposcomp(l,e))
      return;
    l.push_back(e);
  }

  static void lvar_symbolic(const gen & g, vecteur &l){
    const symbolic & s = *g._SYMBptr;
    if ( (s.sommet==at_plus) || (s.sommet==at_prod)){
      if (s.feuille.type!=_VECT){
	lvar(s.feuille,l);
	return;
      }
      vecteur::iterator it=s.feuille._VECTptr->begin(), itend=s.feuille._VECTptr->end();
      for (;it!=itend;++it)
	lvar(*it,l);
      return;
    }
    if ( (s.sommet==at_neg) || (s.sommet==at_inv) ){
      lvar(s.feuille,l);
      return;
    }
    if ( (s.sommet==at_pow) && ( (*s.feuille._VECTptr)[1].type==_INT_))
      lvar(s.feuille._VECTptr->front(),l);
    else
      addtolvar(g,l);
  }

  void lvar(const vecteur & v,vecteur & l){
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      lvar(*it,l);
  }

  void lvar(const sparse_poly1 & p,vecteur & l){
    sparse_poly1::const_iterator it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      lvar(it->coeff,l);
      // lvar(it->exponent,l);
    }
  }

  void lvar(const gen & e, vecteur & l) {
    switch (e.type){
    case _INT_: case _DOUBLE_: case _ZINT: case _CPLX: case _POLY: case _EXT: case _USER: case _REAL:
      return;
    case _IDNT:
      if (strcmp(e._IDNTptr->id_name,string_undef))
	addtolvar(e,l);
      return ;
    case _SYMB:
      lvar_symbolic(e,l);
      return ;
    case _VECT:
      lvar(*e._VECTptr,l);
      return ;
    case _FRAC:
      lvar(e._FRACptr->num,l);
      lvar(e._FRACptr->den,l);
      return;
    case _MOD:
      lvar(*e._MODptr,l);
      lvar(*(e._MODptr+1),l);
      return;
    case _SPOL1:
      lvar(*e._SPOL1ptr,l);
      return;
    default:
      return;
    }
  }
  
  vecteur lvar(const gen & e){
    vecteur l;
    lvar(e,l);
    return l;
  }

  gen symb_lvar(const gen & e){
    return symbolic(at_lvar,e);
  }
  gen cklvar(const gen & e,GIAC_CONTEXT){
    vecteur l;
    lvar(e,l);
    return l;
  }
  static const char _lvar_s []="lvar";
  static define_unary_function_eval (__lvar,&giac::cklvar,_lvar_s);
  define_unary_function_ptr5( at_lvar ,alias_at_lvar,&__lvar,0,true);

  static bool is_algebraic_EXTension(const gen & e){
    if (e.type==_EXT)
      return true;
    if (e.type!=_SYMB)
      return false;
    if ( (e._SYMBptr->sommet==at_sqrt) || (e._SYMBptr->sommet==at_rootof) )
      return true;
    if ( (e._SYMBptr->sommet==at_pow) && (e._SYMBptr->feuille._VECTptr->back().type==_FRAC) && (e._SYMBptr->feuille._VECTptr->back()._FRACptr->den.type==_INT_) &&(absint(e._SYMBptr->feuille._VECTptr->back()._FRACptr->den.val)<=MAX_ALG_EXT_ORDER_SIZE)  )
      return true;
    return false;
  }

  static gen algebraic_argument(const gen & e){
    if (e.type==_EXT)
      return makevecteur(*e._EXTptr,*(e._EXTptr+1));
    if (e.type!=_SYMB)
      return gensizeerr(gettext("sym2poly.cc/algebraic_argument"));
    if ((e._SYMBptr->sommet==at_sqrt) || (e._SYMBptr->sommet==at_rootof) )
      return e._SYMBptr->feuille;
    if ( (e._SYMBptr->sommet==at_pow) && (e._SYMBptr->feuille._VECTptr->back().type==_FRAC) && (e._SYMBptr->feuille._VECTptr->back()._FRACptr->den.type==_INT_) )
      return e._SYMBptr->feuille._VECTptr->front();
    return gentypeerr(gettext("algebraic_argument"));
  }

  static bool equalposmat(const matrice & m,const gen & e,int &i,int & j){
    i=0;
    const_iterateur it=m.begin(),itend=m.end(),jt,jtend;
    for (;it!=itend;++it,++i){
      if (*it==e){
	j=-1;
	return true;
      }
      else {
	if (it->type!=_VECT){
#ifdef NO_STDEXCEPT
	  return false;
#else
	  setsizeerr(gettext("sym2poly.cc/equalposmat"));
#endif
	}
	for (j=0,jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();jt!=jtend;++jt,++j)
	  if (*jt==e)
	    return true;
      }
    }
    return false;
  }

  static void addfirstrow(const gen & e,matrice & m){
    if (m.empty()){
      vecteur v(1,e);
      m.push_back(v);
    }
    else {
      if (m.front().type!=_VECT){
#ifdef NO_STDEXCEPT
	return;
#else
	setsizeerr(gettext("sym2poly.cc/addfirstrow"));
#endif
      }
      vecteur v(*m.front()._VECTptr);
      v.push_back(e);
      m.front()=v;
    }
  }

  static matrice ext_glue_matrices(const matrice & a,const matrice & b){
    if (a.size()>b.size())
      return ext_glue_matrices(b,a);
    if (b.empty() || a.empty() || (a==b))
      return b;
    int i,j;
    matrice res(b.begin()+1,b.end()); // all alg. extensions of b
    // look in a for vars that are not inside res
    matrice a_not_in_res;
    const_iterateur it=a.begin(),itend=a.end(),jt,jtend;
    for (;it!=itend;++it){
      vecteur temp;
      for (jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();jt!=jtend;++jt){
	if (!equalposmat(res,*jt,i,j))
	  temp.push_back(*jt);
      }
      if (it==a.begin() || (!temp.empty()) )
	a_not_in_res.push_back(temp);
    }
    // look in the first row of b for vars that are not inside a_not_in_res
    jt=b.begin()->_VECTptr->begin(),jtend=b.begin()->_VECTptr->end();
    for (;jt!=jtend;++jt){
      if (!equalposmat(a_not_in_res,*jt,i,j))
	addfirstrow(*jt,a_not_in_res);
    }
    return mergevecteur(a_not_in_res,res);
  }

  void alg_lvar(const sparse_poly1 & p,vecteur & l){
    sparse_poly1::const_iterator it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      alg_lvar(it->coeff,l);
      // lvar(it->exponent,l);
    }
  }

  // return true if there is at least one algebraic extension
  void alg_lvar(const gen & e,matrice & m){
    vecteur temp;
    lvar(e,temp);
    int i,j;
    // For each variable of temp, 
    // if not alg var look if still inside m else add it to the first line
    // else make a "merge"
    const_iterateur it=temp.begin(),itend=temp.end();
    for (;it!=itend;++it){
      if ( !is_algebraic_EXTension(*it) ){
	if (!equalposmat(m,*it,i,j)){
	  addfirstrow(*it,m);
	}
      }
      else { // *it is an algebraic extension!
	matrice ext_mat;
	vecteur v,vt;
	ext_mat.push_back(v);
	vt=alg_lvar(algebraic_argument(*it));
	int s=vt.size();
	if (s>1 || (s==1 && !vt.front()._VECTptr->empty()) )
	  ext_mat=mergevecteur(ext_mat,vt);
	m=ext_glue_matrices(ext_mat,m);
      }
    }
  }

  vecteur alg_lvar(const gen & e){
    vecteur l;
    l.push_back(l); // insure a null line inside the matrix of alg_lvar
    alg_lvar(e,l);
    return l;
  }

  gen symb_algvar(const gen & e){
    return symbolic(at_algvar,e);
  }
  gen ckalgvar(const gen & e,GIAC_CONTEXT){
    vecteur l;
    alg_lvar(e,l);
    return l;
  }
  static const char _algvar_s []="algvar";
  static define_unary_function_eval (__algvar,&giac::ckalgvar,_algvar_s);
  define_unary_function_ptr5( at_algvar ,alias_at_algvar,&__algvar,0,true);

  
  //***********************************************
  // functions relative to fractions for efficiency
  //***********************************************
  /*
  static fraction fpow(const fraction & p,const gen & n){
    if (n.type!=_INT_)
      setsizeerr(gettext("sym2poly.cc/fraction pow"));
    return pow(p,n.val);
  }
  */

  gen ref_polynome2gen(ref_polynome * refptr){
    if (refptr->t.coord.empty()){
      delete refptr;
      return 0;
    }
    if (refptr->t.coord.front().index.is_zero() && is_atomic(refptr->t.coord.front().value) ){
      gen res=refptr->t.coord.front().value;
      delete refptr;
      return res;
    }
    return refptr;
  }

  gen monomial2gen(const monomial<gen> & m){
    if (m.index.is_zero() && is_atomic(m.value) ){
      return m.value;
    }
    return new ref_polynome(m);
  }

  gen simplify3(gen & n,gen & d){
    if (is_one(n) || is_one(d))
      return plus_one;
    if (n.type==_EXT){
      gen n_EXT=*n._EXTptr;
      gen g=simplify(n_EXT,d);
      if (!is_one(g)) n=algebraic_EXTension(n_EXT,*(n._EXTptr+1));
      return g;
    }
    if ((n.type==_POLY) && (d.type==_POLY)){
      ref_polynome * pptr = new ref_polynome(n._POLYptr->dim);
      if (
#ifndef SMARTPTR64
	  n.__POLYptr->ref_count==1 && d.__POLYptr->ref_count==1
#else
	  ((ref_polynome*)(* (longlong *) &n >> 16))->ref_count ==1 &&
	  ((ref_polynome*)(* (longlong *) &d >> 16))->ref_count ==1	  
#endif
	  ){
	simplify(*n._POLYptr,*d._POLYptr,pptr->t);
	return pptr;
      }
      polynome np(*n._POLYptr),dp(*d._POLYptr);
      simplify(np,dp,pptr->t);
      gen tmpmult(plus_one);
      lcmdeno(pptr->t,tmpmult);
      if (tmpmult.type==_POLY)
	tmpmult=polynome(monomial<gen>(tmpmult,index_t(pptr->t.dim)));
      gen g;
      if (is_one(tmpmult))
	g=pptr;
      else
	g=fraction(tmpmult*pptr,tmpmult); // polynome(tmpmult,np.dim));
      // changed 4 nov. 2012 failure on f(x):=sqrt(x)/(x+1); F:=normal(f'(x));
      // Fix 9/2/2013: np and dp may have denominators if there are _EXT coeffs
      tmpmult=1;
      lcmdeno(np,tmpmult);
      lcmdeno(dp,tmpmult);
      if (tmpmult.type==_POLY)
	tmpmult=polynome(monomial<gen>(tmpmult,index_t(pptr->t.dim)));
      n=np*tmpmult;
      d=dp*tmpmult;
      return g*tmpmult;
    }
    if (n.type==_POLY) {
      polynome np(*n._POLYptr);
      gen l;
      vector< monomial<gen> > :: const_iterator it=np.coord.begin(),itend=np.coord.end();
      for (;it!=itend;++it)
	l=gcd(l,it->value);
      gen g=simplify3(l,d);
      np=np/g;
      n=np; 
      if (g.type>_DOUBLE_){
	// FIXME embedd g and d inside a polynomial like np was 
	polynome pg(np.dim);
	pg.coord.push_back(monomial<gen>(g,np.dim));
	g=pg;
	polynome pd(np.dim);
	pd.coord.push_back(monomial<gen>(d,np.dim));
	d=pd;
      }
      return g;
    }
    if (d.type==_POLY){
      polynome np(n,d._POLYptr->dim),dp(*d._POLYptr);
      polynome g(np.dim);
      g=simplify(np,dp);
      n=np;
      d=dp;
      return g;
    }
    gen g=gcd(n,d);
    n=n/g; // iquo(n,g);
    d=d/g; // iquo(d,g);
    return g;
  }

  static bool has_EXT(const gen & g){
    if (g.type==_EXT)
      return true;
    if (g.type!=_POLY)
      return false;
    polynome & p = *g._POLYptr;
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      if (has_EXT(it->value))
	return true;
    }
    return false;
  }

  static void _FRACadd(const gen & n1, const gen & d1,const gen & n2, const gen & d2, gen & num, gen & den){
    // cout << n1 << "/" << d1 << "+" << n2 << "/" << d2 << "=";
    if (is_one(d1)){
      num=n1*d2+n2;
      den=d2;
      return;
    }
    if (is_one(d2)){
      num=n2*d1+n1;
      den=d1;
      // cout << num << "/" << den << endl;
      return;
    }
    // n1/d1+n2/d2 with g=gcd(d1,d2), d1=d1g*g, d2=d2g*g is
    // (n1*d2g+n2*d1g)/g * 1/(d1g*d2g)
    gen d1g(d1),d2g(d2);
    den=simplify3(d1g,d2g);
    num=(n1*d2g+n2*d1g);
    simplify3(num,den);
    den=den*d1g*d2g;
  }

  static void _FRACmul(const gen & n1, const gen & d1,const gen & n2, const gen & d2, gen & num, gen & den){
    // cout << n1 << "/" << d1 << "*" << n2 << "/" << d2 << "=";
    if (is_one(d1)){
      num=n1;
      den=d2;
      simplify3(num,den);
      num=num*n2;
      // cout << num << "/" << den << endl;
      return;
    }
    if (is_one(d2)){
      num=n2;
      den=d1;
      simplify3(num,den);
      num=num*n1;
      // cout << num << "/" << den << endl;
      return;
    }
    num=n1;
    den=d2;
    simplify3(num,den);
    gen ntemp(n2),dtemp(d1);
    simplify3(ntemp,dtemp);
    num=num*ntemp;
    den=den*dtemp;
    // Further simplifications may occur with _EXT multiplications
    if (has_EXT(ntemp))
      simplify3(num,den);
    // cout << num << "/" << den << endl;
  }

  //**********************************
  // symbolic to tensor
  //**********************************
  static bool sym2radd (vecteur::const_iterator debut,vecteur::const_iterator fin,const vecteur &l,const vecteur & lv, const vecteur & lvnum,const vecteur & lvden, int l_size, gen & num, gen & den,GIAC_CONTEXT){
    bool totally_converted=true;
    if (fin-debut<4){
      gen n1,d1,n2,d2;
      num=zero;
      den=plus_one;
      for (;debut!=fin;++debut){
	totally_converted=totally_converted && sym2r(*debut,l,lv,lvnum,lvden,l_size,n1,d1,contextptr);
	n2=num;
	d2=den;
	_FRACadd(n1,d1,n2,d2,num,den);
      }
    }
    else {
      vecteur::const_iterator milieu=debut+(fin-debut)/2;
      gen n1,d1,n2,d2;
      totally_converted=totally_converted && sym2radd(debut,milieu,l,lv,lvnum,lvden,l_size,n1,d1,contextptr);
      totally_converted=totally_converted && sym2radd(milieu,fin,l,lv,lvnum,lvden,l_size,n2,d2,contextptr);
      _FRACadd(n1,d1,n2,d2,num,den);
    }
    return totally_converted;
  }

  /*
  static bool sym2rmulold (vecteur::const_iterator debut,vecteur::const_iterator fin,const vecteur &l, const vecteur & lv, const vecteur & lvnum,const vecteur & lvden,int l_size, gen & num, gen & den,GIAC_CONTEXT){
    bool totally_converted=true;
    // First check for a "normal" monomial
    gen coeff=plus_one;
    if (!l.empty()){ 
      bool embedd = l.front().type==_VECT ;
      vecteur l1;
      if (embedd)
	l1=*l.front()._VECTptr;
      else
	l1=l;
      gen tmp;
      int pui,pos;
      index_t i(l_size);
      for (;debut!=fin;++debut){
	if (debut->type<_IDNT)
	  coeff=coeff*(*debut);
	else {
	  if (debut->is_symb_of_sommet(at_pow) && debut->_SYMBptr->feuille._VECTptr->back().type==_INT_){
	    tmp=debut->_SYMBptr->feuille._VECTptr->front();
	    pui=debut->_SYMBptr->feuille._VECTptr->back().val;
	  }
	  else {
	    tmp=*debut;
	    pui=1;
	  }
	  if ( tmp.type==_IDNT && (pos=equalposcomp(l1,tmp)) && pui>=0){
	    i[pos-1] += pui;
	  }
	  else
	    break;
	}
      }
      if (!is_zero(coeff))
	coeff=monomial2gen(monomial<gen>(coeff,i));
    }
    if (fin-debut<4){
      gen n1,d1,n2,d2;
      num=coeff;
      den=plus_one;
      for (;debut!=fin;++debut){
	totally_converted=totally_converted && sym2r(*debut,l,lv,lvnum,lvden,l_size,n1,d1,contextptr);
	n2=num;
	d2=den;
	_FRACmul(n1,d1,n2,d2,num,den);
      }
    }
    else {
      vecteur::const_iterator milieu=debut+(fin-debut)/2;
      gen n1,d1,n2,d2;
      totally_converted=totally_converted && sym2rmulold(debut,milieu,l,lv,lvnum,lvden,l_size,n1,d1,contextptr);
      totally_converted=totally_converted && sym2rmulold(milieu,fin,l,lv,lvnum,lvden,l_size,n2,d2,contextptr);
      _FRACmul(n1,d1,n2,d2,num,den);
      simplify3(coeff,den);
      num=num*coeff;
    }
    return totally_converted;
  }
  */


  static bool sym2rmul (vecteur::const_iterator debut,vecteur::const_iterator fin,const vecteur &l, const vecteur & lv, const vecteur & lvnum,const vecteur & lvden,int l_size, gen & num, gen & den,GIAC_CONTEXT){
    bool totally_converted=true;
    // First check for a "normal" monomial
    gen coeffnum=plus_one,coeffden=plus_one,coeffn=plus_one,coeffd=plus_one;
    if (!l.empty()){ 
      bool embedd = l.front().type==_VECT ;
      const vecteur & l1 = embedd?*l.front()._VECTptr:l;
      gen tmp;
      int pui,pos;
      index_t inum(l_size),iden(l_size);
      for (;debut!=fin;++debut){
	if (debut->type<_IDNT)
	  coeffn=coeffn*(*debut);
	else {
	  if (debut->is_symb_of_sommet(at_inv)){
	    tmp=debut->_SYMBptr->feuille;
	    if (tmp.type<_IDNT){
	      coeffd=coeffd*tmp;
	      continue;
	    }
	    pui=-1;
	  }
	  else {
	    if (debut->is_symb_of_sommet(at_pow) && debut->_SYMBptr->feuille._VECTptr->back().type==_INT_){
	      tmp=debut->_SYMBptr->feuille._VECTptr->front();
	      pui=debut->_SYMBptr->feuille._VECTptr->back().val;
	    }
	    else {
	      tmp=*debut;
	      pui=1;
	    }
	  }
	  if ( (tmp.type==_IDNT || tmp.type==_SYMB) && (pos=equalposcomp(l1,tmp)) ){
	    if (pui>=0) 
	      inum[pos-1] += pui;
	    else
	      iden[pos-1] -= pui;
	  }
	  else
	    break;
	}
      }
      if (!is_zero(coeffn)){
	// simplify inum and iden
	bool hasnum=false,hasden=false;
	for (int i=0;i<l_size;i++){
	  if (inum[i]<iden[i]){
	    iden[i] -= inum[i];
	    inum[i] = 0;
	    hasden=true;
	  }
	  else {
	    inum[i] -= iden[i];
	    iden[i] = 0;
	    hasnum=true;
	  }
	}
	simplify3(coeffn,coeffd);
	if (hasnum)
	  coeffnum=monomial2gen(monomial<gen>(coeffn,inum));
	else
	  coeffnum=coeffn;
	if (hasden)
	  coeffden=monomial2gen(monomial<gen>(coeffd,iden));
	else
	  coeffden=coeffd;
      }
      else
	coeffnum=0;
    }
    if (fin-debut<4){
      num=coeffnum;
      den=coeffden;
      gen n1,d1,n2,d2;
      for (;debut!=fin;++debut){
	totally_converted=totally_converted && sym2r(*debut,l,lv,lvnum,lvden,l_size,n1,d1,contextptr);
	n2=num;
	d2=den;
	_FRACmul(n1,d1,n2,d2,num,den);
      }
    }
    else {
      vecteur::const_iterator milieu=debut+(fin-debut)/2;
      gen n1,d1,n2,d2,n3,d3;
      totally_converted=totally_converted && sym2rmul(debut,milieu,l,lv,lvnum,lvden,l_size,n1,d1,contextptr);
      totally_converted=totally_converted && sym2rmul(milieu,fin,l,lv,lvnum,lvden,l_size,n2,d2,contextptr);
      _FRACmul(n1,d1,n2,d2,n3,d3);
      _FRACmul(coeffnum,coeffden,n3,d3,num,den);}
    return totally_converted;
  }

  static bool sym2rxrootnum(vecteur & v,const vecteur & lv,gen & num,gen & tmpden,GIAC_CONTEXT){
    // make the polynomial X^d - num
    // check for irreducibility
    polynome p(poly12polynome(v));
    polynome p_content(p.dim);
    factorization f;
    gen extra_div=1;
    if (!factor(p,p_content,f,true,false,false,1,extra_div)){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("Can't check irreducibility extracting rootof"));
#endif
      return false;
    }
    // now we choose the factor of lowest degree of the factorization
    int lowest_degree=v.size(),deg;
    factorization::const_iterator f_it=f.begin(),f_itend=f.end();
    for (;f_it!=f_itend;++f_it){
      polynome irr_p(f_it->fact);
      deg=irr_p.lexsorted_degree();
      if (!deg)
	continue;
      if (deg==1){
	v=polynome2poly1(irr_p);
	lowest_degree=1;
	num=rdiv(-v.back(),v.front(),contextptr);
	// cerr << "xroot" << num << endl;
	gen numlv=r2sym(num,lv,contextptr);
	if (!lvar(numlv).empty())
	  *logptr(contextptr) << gettext("Warning, checking for positivity of a root depending of parameters might return wrong sign: ")<< numlv << endl;
	if (is_positive(numlv,contextptr))
	  break;
      }
      if (deg>=lowest_degree)
	continue;
      v=polynome2poly1(irr_p);
      lowest_degree=deg;
    }
    if (lowest_degree>1){
      // here we must check that num is not an extension!!
      if (num.type==_EXT){
	gen a=*(num._EXTptr+1),b;
	gen a__VECTg;
	if (a.type==_VECT)
	  a__VECTg=a;
	else {
	  if( a.type!=_EXT || (a._EXTptr+1)->type!=_VECT){
#ifndef NO_STDEXCEPT
	    setsizeerr(gettext("sym2poly.cc/sym2rxroot"));
#endif
	    return false;
	  }
	  a__VECTg=*(a._EXTptr+1);
	}
	int k;
	gen new_v=common_minimal_POLY(a__VECTg,v,a,b,k,contextptr);
	if (is_undef(new_v))
	  return false;
	*(num._EXTptr+1)=a;
	if (b.type==_FRAC){
	  num=b._FRACptr->num;
	  tmpden=tmpden*b._FRACptr->den;
	}
	else
	  num=b;
      }
      else {
	vecteur w(2);
	w[0]=plus_one;
	num=algebraic_EXTension(w,v);
      }
    }
    return true;
  }

  void fxnd(const gen & e,gen & num, gen & den){
    if (e.type==_FRAC){
      num=e._FRACptr->num;
      den=e._FRACptr->den;
    }
    else {
      num=e;
      den=plus_one;
    }
  }

  static factorization rsqff(const polynome & p){
    polynome s(lgcd(p));
    factorization f(sqff(p/s));
    if (p.dim==0){
      f.push_back(facteur<polynome>(p,1));
      return f;
    }
    if (p.dim==1){
      // adjust const coeff
      gen p1=1;
      factorization::iterator it=f.begin(),itend=f.end();
      for (;it!=itend;++it){
	p1 = p1*pow(it->fact.coord.front().value,it->mult);
      }
      p1=p.coord.front().value/p1;
      if (is_positive(-p1,context0)){ // ok
	for (it=f.begin();it!=itend;++it){
	  if (it->mult%2){
	    it->fact=-it->fact;
	    p1=-p1;
	    break;
	  }
	}
      }
      if (!is_one(p1))
	f.push_back(facteur<polynome>(polynome(p1,1),1));
      return f;
    }
    factorization ff(rsqff(s.trunc1()));
    factorization::const_iterator it=ff.begin(),itend=ff.end();
    for (;it!=itend;++it)
      f.push_back(facteur<polynome>(it->fact.untrunc1(),it->mult));
    return f;
  }

  void smaller_factor(vecteur & v){
    // factor v, select smaller factor
    polynome vp(1),vp_content;
    vp=poly12polynome(v,1);
    factorization vf;
    gen extra_div=1;
    if (factor(vp,vp_content,vf,true,false,false,1,extra_div) && vf.size()>1){
      polynome2poly1(vf.front().fact,1,v);
      for (int i=1;i<vf.size();++i){
	vecteur vi;
	polynome2poly1(vf[i].fact,1,vi);
	if (vi.size()<v.size())
	  v=vi;
      }
    }
  }
  
  static bool sym2rxroot(gen & num,gen & den,int n,int d,const vecteur & l,GIAC_CONTEXT){
    if (is_zero(num))
      return true;
    if (den.type==_CPLX){
      gen denr,deni,numr,numi;
      reim(den,denr,deni,contextptr);
      den=denr*denr+deni*deni;
      num=num*(denr-cst_i*deni);
      reim(num,numr,numi,contextptr);
      deni=gcd(gcd(numr,denr),numi);
      num=num/deni;
      den=den/deni;
    }
    if (is_positive(r2e(-den,l,contextptr),contextptr)){
      num=-num;
      den=-den;
    }
    bool sign_changed=false;
    if (d<0){
      n=-n;
      d=-d;
    }
    if (n<0){
      if (num.type==_EXT){
	gen temp(inv_EXT(num)*den);
	fxnd(temp,num,den);
      }
      else {
#if defined RTOS_THREADX || defined BESTA_OS
	gen g=num; num=den; den=g;
#else
	swap(num,den);
#endif
      }
      num=pow(num,-n)*pow(den,n*(1-d));
      den=pow(den,-n);
    }
    else {
      num=pow(num,n)*pow(den,n*(d-1));
      den=pow(den,n);
    }
    /* d==2 test makes normal(sqrt(1-x)) -> i*sqrt(x-1) 
       commented!, should common_minimal_poly detect [1,0...0,+/-same_poly]?
    if ( (d%2 || d=2) && ( 
			   (num.type==_POLY && is_positive(evalf_double(-num._POLYptr->coord.front().value,2,0),0))
		    ) ){
      num=-num;
      sign_changed=true;
    }
    */
    // compute number of cst polynomial in num and keep track of dims
    int embeddings=0;
    vector<int> embeddings_s;
    if (is_atomic(num)){
      const_iterateur it=l.begin(),itend=l.end();
      embeddings=itend-it;
      if (embeddings==1 && it->type==_VECT && it->_VECTptr->empty())
	embeddings=0;
      else {
	for (int j=0;j<embeddings;++it,++j){ 
	  if (it->type!=_VECT){
	    string s("sym2rxroot error num="+num.print(contextptr)+" den="+den.print(contextptr)+" l="+gen(l).print(contextptr));
	    cerr << s << endl;
#ifndef NO_STDEXCEPT
	    setsizeerr(s);
#endif
	    return false;
	  }
	  embeddings_s.push_back(it->_VECTptr->size());
	}
      }
    }
    else {
      for (;((num.type==_POLY) && (Tis_constant<gen>(*num._POLYptr)));++embeddings){
	embeddings_s.push_back(num._POLYptr->dim);
	num=num._POLYptr->coord.front().value;
      }
    }
    if (num.type==_FRAC){
      den=den*num._FRACptr->den;
      num=num._FRACptr->num*pow(num._FRACptr->den,d-1);
    }
    vecteur v(d+1,zero);
    gen tmpden=1; 
    if (num.type==_POLY){
      const factorization f=rsqff(*num._POLYptr);
      polynome S(plus_one,num._POLYptr->dim),D(plus_one,S.dim);
      factorization::const_iterator jt=f.begin(),jtend=f.end();
      for (;jt!=jtend;++jt){
	if (jt->mult%d)
	  S=S*pow(jt->fact,jt->mult % d);
	D=D*pow(jt->fact,jt->mult/d);
      }
      // Check sign of D
      vecteur Dl(l);
      if (embeddings && Dl[embeddings].type==_VECT)
	Dl=*Dl[embeddings]._VECTptr;
      if (!is_positive(r2e(D,Dl,contextptr),contextptr)){
	D=-D;
	if (d%2)
	  S=-S;
      }
      gen cont=ppz(S);
      gen simpl,doubl; bool pos;
      zint2simpldoublpos(cont,simpl,doubl,pos,d,contextptr);
      if (!pos) simpl=-simpl;
      num=simpl*S;
      D=doubl*D;
      v.front()=plus_one;
      v.back()=-num;
      sym2rxrootnum(v,vecteur(l.begin()+embeddings,l.end()),num,tmpden,contextptr);
      if (num.type==_EXT && num._EXTptr->type==_VECT){
	num=algebraic_EXTension(multvecteur(D,*(*num._EXTptr)._VECTptr),*(num._EXTptr+1));
      }
      else
	num=D*num;
    }
    else {
      v.front()=plus_one;
      v.back()=-num;
      if (is_integer(num)){
	gen simpl,doubl;
	bool pos;
	zint2simpldoublpos(num,simpl,doubl,pos,d,contextptr);
	v.back()=pos?-simpl:simpl;
	smaller_factor(v);
	if (is_minus_one(v.back()))
	  num=doubl;
	else
	  num=algebraic_EXTension(makevecteur(doubl,0),v);
      }
      else 
	sym2rxrootnum(v,vecteur(l.begin()+embeddings,l.end()),num,tmpden,contextptr);
    }
    // end else num integer
    // and eventually we embedd num 
    for (;embeddings;--embeddings){
      num=polynome(num,embeddings_s.back());
      tmpden=polynome(tmpden,embeddings_s.back());
      embeddings_s.pop_back();
    }
    if (sign_changed){
      if (d==2)
	num=cst_i*num;
      else
	num=-num;
    }
    den=den*tmpden;
    return true;
  }

  static bool sym2r (const symbolic &s,const vecteur &l, const vecteur & lv, const vecteur & lvnum,const vecteur & lvden, int l_size,gen & num,gen & den,GIAC_CONTEXT){
    if (s.sommet==at_plus){
      if (s.feuille.type!=_VECT){
	return sym2r(s.feuille,l,lv,lvnum,lvden,l_size,num,den,contextptr);
      }
      vecteur::iterator debut=s.feuille._VECTptr->begin();
      vecteur::iterator fin=s.feuille._VECTptr->end();
      return sym2radd(debut,fin,l,lv,lvnum,lvden,l_size,num,den,contextptr);
    }
    if (s.sommet==at_prod){
      vecteur::iterator debut=s.feuille._VECTptr->begin();
      vecteur::iterator fin=s.feuille._VECTptr->end();
      bool res=sym2rmul(debut,fin,l,lv,lvnum,lvden,l_size,num,den,contextptr);
      /*
      gen numtmp,dentmp;
      sym2rmulold(debut,fin,l,lv,lvnum,lvden,l_size,numtmp,dentmp,contextptr);
      if (numtmp!=num || dentmp!=den)
	return false;
      */
      return res;
    }
    if (s.sommet==at_neg){
      bool totally_converted=sym2r(s.feuille,l,lv,lvnum,lvden,l_size,num,den,contextptr);
      num=-num;
      return totally_converted;
    }
    if (s.sommet==at_inv){
      bool totally_converted=sym2r(s.feuille,l,lv,lvnum,lvden,l_size,num,den,contextptr);
      if (is_zero(num)){
	num=unsigned_inf;
	den=plus_one;
	return true;
      }
      if (num.type==_EXT){
	gen temp(inv_EXT(num)*den);
	fxnd(temp,num,den);
      }
      else {
	if ( (num.type==_POLY) && (num._POLYptr->dim==0) && (!num._POLYptr->coord.empty()) && (num._POLYptr->coord.front().value.type==_EXT) ){
	  gen temp(inv_EXT(num._POLYptr->coord.front().value));
	  if ( (den.type==_POLY) && (den._POLYptr->dim==0) && (!den._POLYptr->coord.empty()) )
	    temp=temp*den._POLYptr->coord.front().value;
	  else
	    temp=temp*den;
	  gen tempnum,tempden;
	  fxnd(temp,tempnum,tempden);
	  polynome tmpnum(0),tmpden(0);
	  tmpnum.coord.push_back(monomial<gen>(tempnum,index_t(0)));
	  tmpden.coord.push_back(monomial<gen>(tempden,index_t(0)));
	  num=tmpnum;
	  if (tempden.type==_POLY)
	    den=tmpden;
	  else
	    den=tempden;
	}
	else {
#if defined RTOS_THREADX || defined BESTA_OS
	  gen g=num; num=den; den=g;
#else
	  swap(num,den);
#endif
	}
      }
      return totally_converted;
    }
    if (s.sommet==at_pow){
      if ((*s.feuille._VECTptr)[1].type==_INT_) {
	bool totally_converted=sym2r(s.feuille._VECTptr->front(),l,lv,lvnum,lvden,l_size,num,den,contextptr);
	int n=(*s.feuille._VECTptr)[1].val;
	if (n<0){
	  if (num.type==_EXT){
	    gen temp(inv_EXT(num)*den);
	    fxnd(temp,num,den);
	  }
	  else {
#if defined RTOS_THREADX || defined BESTA_OS
	    gen g=num; num=den; den=g;
#else
	    swap(num,den);
#endif
	  }
	  num=pow(num,-n);
	  den=pow(den,-n);
	}
	else {
	  num=pow(num,n);
	  den=pow(den,n);
	}
	if (num.type==_EXT)
	  simplify3(num,den);
	return totally_converted;
      }
      if ((*s.feuille._VECTptr)[1].type==_FRAC) {
	fraction f=*((*s.feuille._VECTptr)[1]._FRACptr);
	if ( (f.num.type==_INT_) && (f.den.type==_INT_)){
	  bool totally_converted=sym2r(s.feuille._VECTptr->front(),l,lv,lvnum,lvden,l_size,num,den,contextptr);
	  if (!sym2rxroot(num,den,f.num.val,f.den.val,l,contextptr))
	    return false;
	  return totally_converted;
	}
      }
    }
    if (s.sommet==at_rootof){
      bool totally_converted=sym2r(s.feuille._VECTptr->front(),l,lv,lvnum,lvden,l_size,num,den,contextptr);
      gen pmin_num,pmin_den;
      totally_converted=totally_converted && sym2r(s.feuille._VECTptr->back(),l,lv,lvnum,lvden,l_size,pmin_num,pmin_den,contextptr);
      if (!is_one(pmin_den)){
#ifndef NO_STDEXCEPT
	setsizeerr(gettext("Minimal poly. in rootof must be fraction free"));
#endif
	return false;
      }
      int embeddings=0;
      vector<int> embeddings_s;
      // put out constant polynomials
      if (num.type!=_VECT || pmin_num.type!=_VECT)
	return totally_converted;
      vecteur vnum=*num._VECTptr,vpmin=*pmin_num._VECTptr;
      int s=l.size();
      for (;embeddings<s;){
	bool exitmainloop=false;
	iterateur it=vnum.begin(),itend=vnum.end();
	int i=0;
	for (;;++it){
	  if (it==itend){
	    if (i==0){
	      ++i;
	      it=vpmin.begin();
	      itend=vpmin.end();
	    }
	    else
	      break;
	  }
	  if (is_atomic(*it))
	    continue;
	  if (it->type==_POLY && Tis_constant<gen>(*it->_POLYptr))
	    continue;
	  exitmainloop=true;
	  break;
	}
	if (exitmainloop)
	  break;
	if (l[embeddings].type!=_VECT){
#ifndef NO_STDEXCEPT
	  setsizeerr(gettext("sym2poly.cc/bool sym2r (const"));
#endif
	  return false;
	}
	embeddings_s.push_back(l[embeddings]._VECTptr->size());
	++embeddings;
	for (it=vnum.begin(),itend=vnum.end(),i=0;;++it){
	  if (it==itend){
	    if (i==0){
	      ++i;
	      it=vpmin.begin();
	      itend=vpmin.end();
	    }
	    else
	      break;
	  }
	  if (it->type==_POLY)
	    *it=it->_POLYptr->coord.front().value;
	}
      }
      num=algebraic_EXTension(vnum,vpmin);
      for (;embeddings;--embeddings){
	num=polynome(num,embeddings_s.back());
	embeddings_s.pop_back();
      }
      return totally_converted;
    }
    num=s;
    den=plus_one;
    return false;
  }

  static bool sym2r (const fraction &f,const vecteur &l, const vecteur & lv, const vecteur & lvnum,const vecteur & lvden, int l_size,gen & num,gen & den,GIAC_CONTEXT){
    gen dent,numt;
    bool totally_converted=sym2r(f.num,l,lv,lvnum,lvden,l_size,num,dent,contextptr);
    totally_converted=totally_converted && sym2r(f.den,l,lv,lvnum,lvden,l_size,den,numt,contextptr);
    num=num*numt;
    den=den*dent;
    return totally_converted;
  }

  static bool sym2r (const vecteur &v,const vecteur &l, const vecteur & lv, const vecteur & lvnum,const vecteur & lvden, int l_size,gen & num,gen & den,GIAC_CONTEXT){
    den=plus_one;
    if (v.empty()){
      num=zero;
      return true;
    }
    bool totally_converted=true;
    gen lcmdeno=plus_one;
    const_iterateur it=v.begin(),itend=v.end();
    vecteur res,numv;
    res.reserve(2*(itend-it));
    numv.reserve(itend-it);
    for (;it!=itend;++it){
      totally_converted=totally_converted && sym2r(*it,l,lv,lvnum,lvden,l_size,num,den,contextptr);
      lcmdeno = lcm(lcmdeno,den);
      res.push_back(num);
      res.push_back(den);
    }
    for (it=res.begin(),itend=res.end();it!=itend;){
      num=*it;
      ++it;
      den=*it;
      ++it;
      numv.push_back(num*rdiv(lcmdeno,den,contextptr));
    }
    den=lcmdeno;
    num=numv;
    return totally_converted;
  }

  static bool sym2rmod (const gen * gptr,const vecteur &l, const vecteur & lv, const vecteur & lvnum,const vecteur & lvden, int l_size,gen & num,gen & den,GIAC_CONTEXT){
    gen modnum,modden,modulo;
    bool totally_converted=sym2r(*(gptr+1),l,lv,lvnum,lvden,l_size,modnum,modden,contextptr);
    modulo=rdiv(modnum,modden,contextptr);
    totally_converted=totally_converted && sym2r(*gptr,l,lv,lvnum,lvden,l_size,num,den,contextptr);
    num=makemod(num,modulo);
    den=makemod(den,modulo);
    return totally_converted;
  }

  bool sym2r (const gen &e,const vecteur &l, const vecteur & lv, const vecteur & lvnum,const vecteur & lvden, int l_size,gen & num,gen & den,GIAC_CONTEXT){
    int n;
    switch (e.type ) {
    case _INT_: case _DOUBLE_: case _ZINT: case _CPLX: case _REAL: case _FLOAT_:
      num=e;
      den=plus_one;
      return true; 
    case _IDNT: case _SYMB:
      n=equalposcomp(lv,e);
      if (n && (unsigned(n)<=lvnum.size())){
	num=lvnum[n-1];
	den=lvden[n-1];
	return true;
      }
      if ((!l.empty()) && (l.front().type==_VECT) ){
	int i,j;
	if (equalposmat(l,e,i,j)){
	  num=monomial2gen(monomial<gen>(gen(1),j+1,l[i]._VECTptr->size()));
	  for (int k=i-1;k>=0;--k)
	    num=monomial2gen(monomial<gen>(num,l[k]._VECTptr->size()));
	  den=plus_one;
	  return true;
	}
      }
      else {
	n=equalposcomp(l,e);
	if (n){
	  num=monomial2gen(monomial<gen>(gen(1),n,l_size));
	  den=plus_one;
	  return true;
	}
      }
      if (e.type!=_SYMB){
	num=e;
	den=plus_one;
	return true;
      }
      return sym2r(*e._SYMBptr,l,lv,lvnum,lvden,l_size,num,den,contextptr);
    case _FRAC:
      return sym2r(*e._FRACptr,l,lv,lvnum,lvden,l_size,num,den,contextptr);
    case _VECT:
      return sym2r(*e._VECTptr,l,lv,lvnum,lvden,l_size,num,den,contextptr);
    case _POLY: case _EXT:
      if ((!l.empty()) && (l.front().type==_VECT) ){
	num=e;
	for (int k=l.size()-1;k>=0;--k) // was l_size
	  num=monomial2gen(monomial<gen>(num,l[k]._VECTptr->size()));
	den=plus_one;
      }
      else {
	num=monomial2gen(monomial<gen>(e,l_size));
	den=plus_one;
      }
      return true;
    case _MOD:
      return sym2rmod(e._MODptr,l,lv,lvnum,lvden,l_size,num,den,contextptr);
    case _USER:
      num=e;
      den=plus_one;
      return true;
    default: 
#ifndef NO_STDEXCEPT
      settypeerr(gettext("sym2r type sym2poly.cc l.948"));
#endif
      return 0;
    }
    return 0;
  }

  static void extract_ext(const gen & g,gen & extracted,vector<int> & embeddings){
    if (g.type==_EXT){
      extracted= g;
      return;
    }
    if (g.type==_POLY && !g._POLYptr->coord.empty() && Tis_constant<gen>(*g._POLYptr)){
      embeddings.push_back(g._POLYptr->dim);
      extract_ext(g._POLYptr->coord.front().value,extracted,embeddings);
    }
    else
      extracted=0;
  }

  static bool check_ext(const gen & oldext,const gen & curext){
    if (oldext.type!=_VECT)
      return false;
    if (curext.type==_EXT)
      return oldext!=*(curext._EXTptr+1);
    if (curext.type==_FRAC)
      return check_ext(oldext,curext._FRACptr->num);
    return false;
  }

  // rewrite extension inside a in terms of a common rootof
  // extensions in a must be at the top level
  // alg_extin is the current list of already translated algebraic extension
  // alg_extout is the current list of corresponding values
  // w.r.t the common minpoly
  // return a in terms of the common ext
  static vecteur common_algext(const vecteur & a,const vecteur & l,GIAC_CONTEXT){
    const_iterateur it=a.begin(),itend=a.end();
    vecteur alg_extin,alg_extoutnum,alg_extoutden;
    for (;it!=itend;++it){
      if (it->type==_EXT)
	break;
    }
    if (itend==it)
      return a;
    alg_extin.push_back(*(it->_EXTptr+1));
    gen curext=*(it->_EXTptr+1);
    gen minpoly=curext;
    alg_extoutnum.push_back(makevecteur(1,0));
    alg_extoutden.push_back(1);
    ++it;
    for (;it!=itend;++it){
      if (it->type!=_EXT)
	continue;
      curext=*(it->_EXTptr+1);
      if (equalposcomp(alg_extin,curext))
	continue;
      // make common extension of curext and minpoly
      gen oldminpoly=minpoly;
      gen oldcurext=curext;
      minpoly=common_EXT(curext,oldminpoly,&l,contextptr);
      if (minpoly.type!=_VECT)
	return vecteur(1,gensizeerr(contextptr));
      if (minpoly._VECTptr->size()>MAX_COMMON_ALG_EXT_ORDER_SIZE)
	return vecteur(1,undef);
      // compute alg_extoutnum/den using newminpoly
      int s=alg_extin.size();
      for (int i=0;i<s;++i){
	if (alg_extoutnum[i].type!=_VECT)
	  return vecteur(1,gensizeerr(contextptr));
	if (oldminpoly.type==_FRAC){
	  gen resn,resd;
	  hornerfrac(*alg_extoutnum[i]._VECTptr,oldminpoly._FRACptr->num,oldminpoly._FRACptr->den,resn,resd);
	  alg_extoutden[i]=alg_extoutden[i]*resd;
	  if (resn.type!=_EXT || *(resn._EXTptr+1)!=minpoly)
	    return vecteur(1,gensizeerr(contextptr));
	  alg_extoutnum[i]=*resn._EXTptr;
	}
	else {
	  gen tmp=horner(*alg_extoutnum[i]._VECTptr,oldminpoly);
	  if (tmp.type!=_EXT || *(tmp._EXTptr+1)!=minpoly)
	    return vecteur(1,gensizeerr(contextptr));
	  alg_extoutnum[i]=*tmp._EXTptr;
	}
      }
      // add oldcurext/curext to the list
      alg_extin.push_back(oldcurext);
      if (curext.type==_FRAC){
	alg_extoutden.push_back(curext._FRACptr->den);
	curext=curext._FRACptr->num;
      }
      else
	alg_extoutden.push_back(1);
      if (curext.type!=_EXT || *(curext._EXTptr+1)!=minpoly)
	return vecteur(1,gensizeerr(contextptr));
      alg_extoutnum.push_back(*curext._EXTptr);
    }
    vecteur res;
    for (it=a.begin();it!=itend;++it){
      if (it->type!=_EXT)
	res.push_back(*it);
      int n=equalposcomp(alg_extin,*(it->_EXTptr+1));
      if (!n)
	return vecteur(1,gensizeerr(contextptr));
      --n;
      gen tmp=alg_extoutnum[n];
      if (tmp.type==_VECT)
	tmp.subtype=_POLY1__VECT;
      if (!is_one(alg_extoutden[n])){
	gen resn,resd;
	hornerfrac(*it->_EXTptr->_VECTptr,tmp,alg_extoutden[n],resn,resd);
	res.push_back(algebraic_EXTension(resn,minpoly)/resd);
      }
      else {
	tmp=horner(*it->_EXTptr,tmp);
	if (tmp.type!=_VECT)
	  res.push_back(tmp);
	else
	  res.push_back(algebraic_EXTension(tmp,minpoly));
      }
    }
    return res;
  }

  static bool compute_lv_lvnum_lvden(const vecteur & l,vecteur & lv,vecteur & lvnum,vecteur & lvden,bool & totally_converted,int l_size,GIAC_CONTEXT){
    gen num,den;
    // sort by ascending complexity
    iterateur it=lv.begin(),itend=lv.end();
    lvnum.reserve(itend-it);
    lvden.reserve(itend-it);
    sort(it,itend,symb_size_less);
    bool algext=false;
    // find num/den for each var of lv
    for (;it!=itend;++it){
      algext = algext || (it->type==_SYMB && (it->_SYMBptr->sommet==at_pow || it->_SYMBptr->sommet==at_rootof)) ;
      totally_converted = totally_converted && sym2r(*it,l,lv,lvnum,lvden,l_size,num,den,contextptr);
      lvnum.push_back(num);
      lvden.push_back(den);
    }
    if (!algext || lv.size()==1 || l==lv)
      return true;
    it=lv.begin();
    // create common extensions for rootofs
    // first find extensions and embeddings levels
    std::map< int,vecteur> extensions,newext;
    for (int i=0;it!=itend;++it,++i){
      if (it->is_symb_of_sommet(at_pow) || it->is_symb_of_sommet(at_rootof)){
	gen ext;
	vector<int> emb;
	extract_ext(lvnum[i],ext,emb);
	if (!is_zero(ext))
	  extensions[emb.size()].push_back(ext);
      }
    }
    // now rewrite each element of the list at each embedding level
    std::map< int,vecteur >::iterator jt=extensions.begin(),jtend=extensions.end();
    for (;jt!=jtend;++jt){
      if (is_undef( (newext[jt->first]=common_algext(jt->second,l,contextptr)) ))
	return false;
    }
    //stores cur
    it=lv.begin();
    for (int i=0;it!=itend;++it,++i){
      if (it->is_symb_of_sommet(at_pow) || it->is_symb_of_sommet(at_rootof)){
	gen ext;
	vector<int> emb;
	extract_ext(lvnum[i],ext,emb);
	if (is_zero(ext))
	  continue;
	int n=equalposcomp(extensions[emb.size()],ext);
	if (!n)
	  return false; // setsizeerr();
	gen tmp=newext[emb.size()],den=1;
	if (tmp.type!=_VECT || int(tmp._VECTptr->size())<n)
	  return false; // setsizeerr();
	tmp=(*tmp._VECTptr)[n-1];
	if (tmp.type==_FRAC){
	  den=tmp._FRACptr->den;
	  tmp=tmp._FRACptr->num;
	}
	for (;!emb.empty();){
	  tmp=polynome(tmp,emb.back());
	  den=polynome(den,emb.back());
	  emb.pop_back();
	}
	lvden[i]=lvden[i]*den;
	lvnum[i]=tmp;
      }
    }
    return true;
  }

  bool sym2r (const gen &e,const vecteur &l, int l_size,gen & num,gen & den,GIAC_CONTEXT){
    if (e.type<_POLY){
      num=e;
      den=plus_one;
      return true; 
    }
    if ( (e.type==_POLY) || (e.type==_EXT)){
      if ((!l.empty()) && (l.front().type==_VECT) ){
	num=e;
	for (int k=l.size()-1;k>=0;--k) // was l.size()
	  num=monomial2gen(monomial<gen>(num,l[k]._VECTptr->size()));
	den=plus_one;
      }
      else {
	num=monomial2gen(monomial<gen>(e,l_size));
	den=plus_one;
      }
      return true;
    }
    bool totally_converted=true;
    vecteur lv,lvnum,lvden;
    lvar(e,lv);
    if (!compute_lv_lvnum_lvden(l,lv,lvnum,lvden,totally_converted,l_size,contextptr)){
      num=undef;
      return false;
    }
    totally_converted =totally_converted && sym2r(e,l,lv,lvnum,lvden,l_size,num,den,contextptr);
    // If den is a _POLY, multiply den by the _EXT conjugate of it's lcoeff
    // FIXME this should be done recursively if the 1st coeff is a _POLY!
    if (den.type==_POLY && !den._POLYptr->coord.empty() && den._POLYptr->coord.front().value.type==_EXT){
      gen a = ext_reduce(den._POLYptr->coord.front().value);
      if (is_undef(a)) return false;
      if (a.type == _EXT && a._EXTptr->type==_VECT){
	vecteur u,v,d;
	egcd(*(a._EXTptr->_VECTptr),*((a._EXTptr+1)->_VECTptr),0,u,v,d);
	if (d.size()==1){
	  gen aconj=algebraic_EXTension(u,*(a._EXTptr+1));
	  aconj=polynome(aconj,den._POLYptr->coord.front().index.size());
	  num=aconj*num;
	  den=aconj*den;
	}
      }
    }
    return totally_converted;
  }

  fraction sym2r(const gen & e, const vecteur & l,GIAC_CONTEXT){
    int l_size;
    if (!l.empty() && l.front().type==_VECT)
      l_size=l.front()._VECTptr->size();
    else
      l_size=l.size();
    gen num,den;
    sym2r(e,l,l_size,num,den,contextptr);
    if (is_positive(-den,contextptr)) 
      return fraction(-num,-den);
    else
      return fraction(num,den);
  }

  // fraction / x -> fraction of vecteur
  gen e2r(const gen & e,const gen & x,GIAC_CONTEXT){
    vecteur l(1,x);
    lvar(e,l);
    gen r=polynome2poly1(e2r(e,l,contextptr),1);
    return r2e(r,cdr_VECT(l),contextptr);
  }

  gen e2r(const gen & e,const vecteur & l,GIAC_CONTEXT){
    if (e.type!=_VECT)
      return sym2r(e,l,contextptr);
    bool totally_converted=true;
    int l_size;
    if (!l.empty() && l.front().type==_VECT)
      l_size=l.front()._VECTptr->size();
    else
      l_size=l.size();
    gen num,den;
    vecteur lv,lvnum,lvden;
    lvar(e,lv);
    if (!compute_lv_lvnum_lvden(l,lv,lvnum,lvden,totally_converted,l_size,contextptr))
      return gensizeerr(contextptr);
    vecteur res;
    const_iterateur jt=e._VECTptr->begin(),jtend=e._VECTptr->end();
    for (;jt!=jtend;++jt){
      sym2r(*jt,l,lv,lvnum,lvden,l_size,num,den,contextptr);
      res.push_back(num/den);
    }
    return gen(res,e.subtype);
  }

  //**********************************
  // tensor to symbolic
  //**********************************

  static gen niceprod(ref_vecteur * res,bool negatif){
    gen tmp;
    if (res->v.size()!=1)
      tmp=symbolic(at_prod,gen(res,_SEQ__VECT));
    else {
      tmp=res->v.front();
      delete_ref_vecteur(res);
    }
    return negatif?-tmp:tmp;
  }

  gen r2sym(const gen & e,const index_m & i,const vecteur & l,GIAC_CONTEXT){
    if (is_undef(e))
      return e;
    if (i.size()!=l.size())
      return gensizeerr(gettext("sym2poly/r2sym(const gen & e,const index_m & i,const vecteur & l)"));
    vecteur::const_iterator l_it=l.begin();
    index_t::const_iterator it=i.begin(),itend=i.end();
    ref_vecteur * res=new_ref_vecteur(0);
    res->v.reserve(itend-it+1);
    bool negatif=false;
    if (!is_one(e) || (e.type==_MOD) ){
      if ( e.type<=_REAL && is_positive(-e,contextptr)){
	negatif=true;
	if (!is_minus_one(e))
	  res->v.push_back(-e);
      }
      else
	res->v.push_back(e);
    }
    for (;it!=itend;++it,++l_it){
      if ((*it))
	res->v.push_back(pow(*l_it,*it));
    }
    if (res->v.empty()){
      delete_ref_vecteur(res);
      return e;
    }
    return niceprod(res,negatif);
  }

  static gen r2sym2(const polynome & p, const vecteur & l,GIAC_CONTEXT){
    monomial_v::const_iterator it=p.coord.begin();
    monomial_v::const_iterator itend=p.coord.end();
    if (itend-it==1)
      return r2sym(it->value,it->index,l,contextptr);
    ref_vecteur * res=new_ref_vecteur(0);
    res->v.reserve(itend-it);
    for (;it!=itend;++it){
      res->v.push_back(r2sym(it->value,it->index,l,contextptr)) ;
    }
    if (res->v.size()==1){
      gen tmp=res->v.front();
      delete res;
      return tmp;
    }
    if (increasing_power(contextptr)) 
      reverse(res->v.begin(),res->v.end());
    return symbolic(at_plus,gen(res,_SEQ__VECT));
  }

  gen r2sym(const polynome & p, const vecteur & l,GIAC_CONTEXT){
    if (p.coord.empty())
      return zero;
    if (p.dim==0){
      return p.constant_term();
    }
    if (is_positive(-p.coord.front()))
      return -r2sym2(-p,l,contextptr);
    return r2sym2(p,l,contextptr);
  }

  gen r2sym(const fraction & f, const vecteur & l,GIAC_CONTEXT){
    if (f.den.type==_POLY && is_positive(-f.den._POLYptr->coord.front())){
      return rdiv(r2sym(-f.num,l,contextptr),r2sym(-f.den,l,contextptr),contextptr);
    }
    // workaround for e.g. normal(sqrt(3)*sqrt(15))
    gen fn=r2sym(f.num,l,contextptr);
    gen fd=r2sym(f.den,l,contextptr);
    if (fn.is_symb_of_sommet(at_prod) && fn._SYMBptr->feuille.type==_VECT && fd.type==_INT_ && absint(fd.val)>1){
      gen c=1;
      vecteur v(*fn._SYMBptr->feuille._VECTptr);
      fn=1;
      for (unsigned i=0;i<v.size();++i){
	if (v[i].type==_INT_){
	  c=c*v[i];
	  simplify(c,fd);
	}
	else
	  fn=fn*v[i];
      }
      fn=c*fn;
    }
    return rdiv(fn,fd,contextptr);
  }

  gen r2sym(const vecteur & v,const vecteur & l,GIAC_CONTEXT){
    const_iterateur it=v.begin(),itend=v.end();
    ref_vecteur * res=new_ref_vecteur(0);
    res->v.reserve(itend-it);
    for (;it!=itend;++it)
      res->v.push_back(r2sym(*it,l,contextptr));
    return res;
  }

  static gen r2sym(const gen & p, const const_iterateur & lt, const const_iterateur & ltend,GIAC_CONTEXT);

  static gen solve_deg2(const gen & p,const gen & pmin,const const_iterateur & lt, const const_iterateur & ltend,GIAC_CONTEXT){
    if ( p.type!=_VECT || pmin.type!=_VECT)
      return gensizeerr(gettext("sym2poly.cc/ckdeg2_rootof"));
    vecteur & w = *pmin._VECTptr;
    int s=w.size();
    if (s!=3)
      return symb_rootof(p,r2sym(pmin,lt,ltend,contextptr),contextptr);
    if (p._VECTptr->size()!=2)
      return gensizeerr(gettext("sym2poly.cc/ckdeg2_rootof"));  
    gen b_over_2=rdiv(w[1],plus_two,contextptr);
    if (is_zero(b_over_2))
      return p._VECTptr->front()*sqrt(r2sym(-w.back()/w.front(),lt,ltend,contextptr),contextptr)+p._VECTptr->back();
    gen x;
    if (b_over_2.type!=_FRAC){
      gen a=r2sym(w.front(),lt,ltend,contextptr);
      gen minus_b_over_2=r2sym(-b_over_2,lt,ltend,contextptr);
      gen delta_prime=r2sym(pow(b_over_2,2,contextptr)-w.front()*w.back(),lt,ltend,contextptr);
      x=rdiv(minus_b_over_2+sqrt(delta_prime,contextptr),a,contextptr);
    }
    else {
      gen two_a=r2sym(plus_two*w.front(),lt,ltend,contextptr);
      gen minus_b=r2sym(-w[1],lt,ltend,contextptr);
      gen delta=r2sym(w[1]*w[1]-gen(4)*w.front()*w.back(),lt,ltend,contextptr);
      x=rdiv(minus_b+sqrt(delta,contextptr),two_a,contextptr);
    }
    return p._VECTptr->front()*x+p._VECTptr->back();
  }

  /*
  static gen ckdeg2_rootof(const gen & p,const gen & pmin,GIAC_CONTEXT){
    if ( p.type!=_VECT || pmin.type!=_VECT)
      return gensizeerr(gettext("sym2poly.cc/ckdeg2_rootof"));
    if (pmin._VECTptr->size()!=3)
      return symb_rootof(p,pmin,contextptr);
    if (p._VECTptr->size()!=2)
      return gensizeerr(gettext("sym2poly.cc/ckdeg2_rootof"));   
    vecteur v;
    identificateur x(" x");
    in_solve(symb_horner(*pmin._VECTptr,x),x,v,1,contextptr); 
    if (v.empty())
      return gensizeerr(gettext("No root found for pmin"));
    return p._VECTptr->front()*v.front()+p._VECTptr->back();
  }
  */

  // check that an _EXT e is root of a second order poly
  // return 0 if not, 2 if pmin(e) is 2nd order, 1 otherwise
  // FIXME: if pmin has integer coeffs as well as e
  // translate so that we just need to find the sign
  // then we are reduced to find the sign of P(alpha) where alpha is the
  // largest real root of a polynomial Q
  // Using VAS [csturm.cc: gen complexroot(const gen & g,bool complexe,GIAC_CONTEXT)]
  // find isolation interval for roots of Q, select the largest one,
  // find isolation interval for roots of P, if they intersect with previous one
  // improve precision by dichotomy until sign is resolved
  static int is_root_of_deg2(const gen & e,vecteur & v){
    // find a,b,c such that a*e*e+b*e+c=0
#ifdef DEBUG_SUPPORT
    if (e.type!=_EXT || e._EXTptr->type!=_VECT)
      setsizeerr(gettext("sym2poly.cc/is_root_of_deg2"));
#endif // DEBUG_SUPPORT
    gen pmin=*(e._EXTptr+1);
    vecteur vpmin(min_pol(pmin));
    if (!vpmin.empty() && is_undef(vpmin))
      return 0;
    if (vpmin.size()==3)
      return 2;
    v.clear();
    gen e_square(e*e);
    if (e_square.type!=_EXT){ // b=0, a=1, e*e=-c
      v.push_back(plus_one);
      v.push_back(zero);
      v.push_back(-e_square);
      return 1;
    }
    if (e_square._EXTptr->type!=_VECT)
      return 0; // setsizeerr(gettext("sym2poly.cc/is_root_of_deg2"));
    int s=e._EXTptr->_VECTptr->size();      
    int s2=e_square._EXTptr->_VECTptr->size();
    if (s!=s2)
      return 0;
    gen b=-e_square._EXTptr->_VECTptr->front();
    gen a=e._EXTptr->_VECTptr->front();
    simplify(a,b);
    gen c=a*e_square+b*e;
    if (c.type==_EXT)
      return 0;
    v.push_back(a);
    v.push_back(b);
    v.push_back(-c);
    return true;
  }

  static gen randassume(const vecteur & boundaries,GIAC_CONTEXT){
    if (boundaries.empty())
      return 0;
    if (boundaries[0].type==_VECT)
      return randassume(*boundaries[0]._VECTptr,contextptr);
    if (boundaries.size()<2)
      return 0;
    gen a=boundaries[0],b=boundaries[1];
    if (a==minus_inf){
      if (b==plus_inf)
	return _rand(makesequence(-100,100),contextptr);
      return _rand(makesequence(b-100,b-1),contextptr);
    }
    if (b==plus_inf)
      return _rand(makesequence(a+1,a+100),contextptr);
    return _rand(boundaries,contextptr);
  }

  static void check_assume(vecteur & vzero,const vecteur & vassume,GIAC_CONTEXT){
    for (unsigned i=0;i<vzero.size();++i){
      gen assi=vassume[i];
      if (assi.type==_VECT && assi.subtype==_ASSUME__VECT && assi._VECTptr->size()==3){
	if((*assi._VECTptr)[1].type==_VECT && !(*assi._VECTptr)[1]._VECTptr->empty()){
	  vzero[i]=randassume(*(*assi._VECTptr)[1]._VECTptr,contextptr);
	}
      }
    }
  }
  
  static gen r2sym(const gen & p, const const_iterateur & lt, const const_iterateur & ltend,GIAC_CONTEXT){
    // Note that if p.type==_FRAC && p._FRACptr->num.type==_EXT
    // it might require another simplification with the denom
    if (p.type==_FRAC){
      gen res;
      if (p._FRACptr->den.type==_CPLX)
	res=rdiv(r2sym(p._FRACptr->num*conj(p._FRACptr->den,contextptr),lt,ltend,contextptr),r2sym(p._FRACptr->den*conj(p._FRACptr->den,contextptr),lt,ltend,contextptr),contextptr);
      else
	res=rdiv(r2sym(p._FRACptr->num,lt,ltend,contextptr),r2sym(p._FRACptr->den,lt,ltend,contextptr),contextptr);
      return (p._FRACptr->num.type==_EXT)?ratnormal(res):res;
    }
    if (p.type==_MOD)
      return makemodquoted(r2sym(*p._MODptr,lt,ltend,contextptr),r2sym(*(p._MODptr+1),lt,ltend,contextptr));
    if (p.type==_VECT){
      const_iterateur it=p._VECTptr->begin(),itend=p._VECTptr->end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it)
	res.push_back(r2sym(*it,lt,ltend,contextptr));
      return res;
    }
    if (p.type==_EXT){
      gen pp=ext_reduce(p);
      if (pp.type!=_EXT){
	if (is_undef(pp)) return pp;
	return r2sym(pp,lt,ltend,contextptr);
      }
      gen f=*(pp._EXTptr+1);
      if ((pp._EXTptr+1)->type!=_VECT) 
	f=min_pol(*(pp._EXTptr+1)); // f is a _VECT
      if (is_undef(f))
	return f;
      if (f._VECTptr->size()==3){
	return solve_deg2(r2sym(*(pp._EXTptr),lt,ltend,contextptr),f,lt,ltend,contextptr);
      }
      vecteur v;
      int t=is_root_of_deg2(pp,v);
      if (t==2){
	// return ckdeg2_rootof(r2sym(*(pp._EXTptr),lt,ltend,contextptr),r2sym(f,lt,ltend,contextptr),contextptr);
	return solve_deg2(r2sym(*(pp._EXTptr),lt,ltend,contextptr),f,lt,ltend,contextptr);
      }
      // if (t && f==v) t=0;
      if (t==1){
	vecteur w;
	identificateur x(" x");
	in_solve(symb_horner(*(r2sym(v,lt,ltend,contextptr)._VECTptr),x),x,w,1,contextptr); 
#ifndef NO_STDEXCEPT
	try {
#endif
	  vecteur vinit(lt,ltend);
	  if (lt!=ltend && vinit.front().type==_VECT)
	    vinit=*vinit.front()._VECTptr;
	  vecteur vassume(vinit);
	  for (unsigned i=0;i<vassume.size();++i){
	    if (vinit[i].type==_IDNT)
	      vinit[i]._IDNTptr->in_eval(0,vinit[i],vassume[i],contextptr);
	  }
	  gen vinitd;
	  vecteur vzero=vecteur(vinit.size());
	  bool tst=has_evalf(vinit,vinitd,1,contextptr);
	  if (tst)
	    vzero=*vinitd._VECTptr;
	  else 
	    check_assume(vzero,vassume,contextptr);
	  gen tmp0=r2sym(*pp._EXTptr,lt,ltend,contextptr);
	  gen tmp1=r2sym(f,lt,ltend,contextptr);
	  for (int ntry=0;ntry<10;++ntry,tst=false){
	    gen tmp00=subst(tmp0,vinit,vzero,false,contextptr);
	    gen tmp10=subst(tmp1,vinit,vzero,false,contextptr);
	    tmp00=algebraic_EXTension(tmp00,tmp10);
	    tmp00=tmp00.evalf_double(1,contextptr);
	    if (tmp00.type<=_CPLX){
	      gen tmp3=eval(subst(w.front(),vinit,vzero,false,contextptr),1,contextptr);
	      tmp3=tmp3.evalf_double(1,contextptr);
	      gen tmp2=eval(subst(w.back(),vinit,vzero,false,contextptr),1,contextptr);
	      tmp2=tmp2.evalf_double(1,contextptr);
	      if (tmp3.type<=_CPLX && tmp2.type<=_CPLX && tmp2!=tmp3){
		if (!vzero.empty() && !tst)
		  *logptr(contextptr) << gettext("Warning, choosing root of ") << f << " at parameters values " << vzero << endl;
		if (abs(tmp2-tmp00,contextptr)._DOUBLE_val<abs(tmp3-tmp00,contextptr)._DOUBLE_val)
		  return w.back();
		else
		  return w.front();
	      }
	    }
	    // tmp2 and tmp3 are identical or tmp0 is not real, retry
	    vzero=vranm(vzero.size(),0,contextptr);
	    check_assume(vzero,vassume,contextptr);
	  }
#ifndef NO_STDEXCEPT
	}
	catch (std::runtime_error & e){
	  cerr << "sym2poly exception caught " << e.what() << endl;
	}
#endif
	return w.front();
	/* gen tmp0=algebraic_EXTension(r2sym(*pp._EXTptr,lt,ltend),r2sym(f,lt,ltend)).evalf_double();
	if (tmp0.type==_DOUBLE_){
	  gen tmp1=evalf_double(w.front()),tmp2=evalf_double(w.back());
	  if (tmp1.type==_DOUBLE_ && tmp2.type==_DOUBLE_ && fabs((tmp2-tmp0)._DOUBLE_val)<fabs((tmp1-tmp0)._DOUBLE_val))
	    return w.back();
	    }
	    return w.front(); */
      }
      int s=f._VECTptr->size();
      v=vecteur(s,zero);
      v.front()=plus_one;
      v.back()=f._VECTptr->back();
      gen theta;
      if (f==v)
	theta=pow(r2sym(-v.back(),lt,ltend,contextptr),fraction(1,s-1),contextptr);
      else
	return symb_rootof(r2sym(*(pp._EXTptr),lt,ltend,contextptr),r2sym(f,lt,ltend,contextptr),contextptr);
      return symb_horner(*(r2sym(*(pp._EXTptr),lt,ltend,contextptr)._VECTptr),theta);
    }
    if ((p.type!=_POLY) || (lt==ltend))
      return p;
    if (p._POLYptr->coord.empty())
      return zero;
    if (p._POLYptr->dim==0){
      return r2sym(p._POLYptr->coord.front().value,lt+1,ltend,contextptr);
    }
    if (is_positive(-p,contextptr) && !is_positive(p,contextptr)) 
      return -r2sym(-p,lt,ltend,contextptr);
    monomial_v::const_iterator it=p._POLYptr->coord.begin();
    monomial_v::const_iterator itend=p._POLYptr->coord.end();
    if (itend==it)
      return zero;
    vecteur res;
    res.reserve(itend-it);
    for (;it!=itend;++it){
      res.push_back(r2sym(r2sym(it->value,lt+1,ltend,contextptr),it->index,*(lt->_VECTptr),contextptr)) ;
    }
    if (res.size()==1)
      return res.front();
    if (increasing_power(contextptr)) 
      reverse(res.begin(),res.end());
    return symbolic(at_plus,gen(res,_SEQ__VECT));
  }

  gen r2sym(const gen & p, const vecteur & l,GIAC_CONTEXT){
    if (p.type==_VECT){
      gen res=r2sym(*p._VECTptr,l,contextptr);
      if (res.type==_VECT)
	res.subtype=p.subtype;
      return res;
    }
    if ( (!l.empty()) && (l.front().type==_VECT) )
      return r2sym(p,l.begin(),l.end(),contextptr);
    if (p.type==_FRAC)
      return r2sym(*p._FRACptr,l,contextptr);
    if (p.type==_MOD)
      return makemodquoted(r2sym(*p._MODptr,l,contextptr),r2sym(*(p._MODptr+1),l,contextptr));
    if (p.type==_EXT){ 
      gen pp=ext_reduce(*(p._EXTptr),*(p._EXTptr+1));
      if (pp.type!=_EXT){
	if (is_undef(pp)) return pp;
	return r2sym(pp,l,contextptr);
      }
      gen f=*(pp._EXTptr+1);
      if ((pp._EXTptr+1)->type!=_VECT) 
	f=min_pol(*(pp._EXTptr+1)); // f is a _VECT
      if (is_undef(f))
	return f;
      vecteur v;
      int t=is_root_of_deg2(pp,v);
      if (f._VECTptr->size()==3){
	return solve_deg2(r2sym(*(pp._EXTptr),l,contextptr),f,l.begin(),l.end(),contextptr);
      }
      if (t==2){
	// return ckdeg2_rootof(r2sym(*(pp._EXTptr),l,contextptr),r2sym(f,l,contextptr),contextptr);
	return solve_deg2(r2sym(*(pp._EXTptr),l,contextptr),f,l.begin(),l.end(),contextptr);
      }
      if (t==1){
	vecteur w;
	identificateur x(" x");
	in_solve(symb_horner(*(r2sym(v,l,contextptr)._VECTptr),x),x,w,1,contextptr);
	// find the nearest root from pp
	gen ww,ppp,mindist,curdist;
	if (has_evalf(w,ww,1,contextptr) && has_evalf(pp,ppp,1,contextptr)){
	  int pos=0;
	  vecteur & www = *ww._VECTptr;
	  mindist=abs(www[0]-ppp,contextptr);
	  for (int i=1;i<(int)www.size();++i){
	    curdist=abs(www[i]-ppp,contextptr);
	    if (is_strictly_greater(mindist,curdist,contextptr)){
	      pos=i;
	      mindist=curdist;
	    }
	  }
	  return w[pos];
	}
	return w.front();
      }
      int s=f._VECTptr->size();
      v=vecteur(s,zero);
      v.front()=plus_one;
      v.back()=f._VECTptr->back();
      gen theta;
      if (f==v)
	theta=pow(r2sym(-v.back(),l,contextptr),fraction(1,s-1),contextptr);
      else
	return symb_rootof(r2sym(*(pp._EXTptr),l,contextptr),r2sym(f,l,contextptr),contextptr);
      return symb_horner(*(r2sym(*(pp._EXTptr),l,contextptr)._VECTptr),theta);
    }
    if (p.type==_POLY)
      return r2sym(*p._POLYptr,l,contextptr);
    return p;
  }

  gen r2e(const gen & p, const vecteur & l,GIAC_CONTEXT){
    return r2sym(p,l,contextptr);
  }

  // alias vecteur -> polynome / x
  gen r2e(const gen & r,const gen & x,GIAC_CONTEXT){
    if (r.type==_FRAC)
      return fraction(r2e(r._FRACptr->num,x,contextptr),r2e(r._FRACptr->den,x,contextptr));
    if (r.type==_VECT)
      return symb_horner(*r._VECTptr,x);
    else
      return r;
  }

  // convert factorization to symbolic form 
  gen r2sym(const factorization & vnum,const vecteur & l,GIAC_CONTEXT){
    gen resnum(1);
    factorization::const_iterator it=vnum.begin(),itend=vnum.end();
    for (;it!=itend;++it)
      resnum=resnum*pow(r2sym(gen(it->fact),l,contextptr),it->mult);
    return resnum;
  }

  // convert pfde_VECT to symbolic form 
  gen r2sym(const vector< pf<gen> > & pfde_VECT,const vecteur & l,GIAC_CONTEXT){
    gen res(0);
    vector< pf<gen> >::const_iterator it=pfde_VECT.begin(),itend=pfde_VECT.end();
    for (;it!=itend;++it){
      res=res+rdiv(r2sym(gen(it->num),l,contextptr),(r2sym(gen(it->den/pow(it->fact,it->mult)),l,contextptr)*pow(r2sym(gen(it->fact),l,contextptr),it->mult)),contextptr);
    }
    return res;
  }

  //*****************************
  /* Fonctions relatives to ex */
  //*****************************

  // special version of lop(.,at_pow) that removes integral powers
  static vecteur lop_pow(const gen & g){
    if (g.type==_SYMB) {
      if (g._SYMBptr->sommet==at_pow && g._SYMBptr->feuille._VECTptr->back().type!=_INT_) 
	return vecteur(1,g);
      else
	return lop_pow(g._SYMBptr->feuille);
    }
    if (g.type!=_VECT)
      return vecteur(0);
    vecteur res;
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it){
      if (it->is_symb_of_sommet(at_pow) && it->_SYMBptr->feuille._VECTptr->back().type==_INT_)
	continue;
      res=mergeset(res,lop_pow(*it));
    }
    return res;
  }

  gen normalize_sqrt(const gen & e,GIAC_CONTEXT){
    if (complex_mode(contextptr)) 
      return e;
    // remove multiple factors inside sqrt
    // vecteur l0=lop(e,at_pow),lin,lout;
    vecteur l0=lop_pow(e),lin,lout;
    const_iterateur it=l0.begin(),itend=l0.end();
    for (;it!=itend;++it){
      vecteur & arg=*it->_SYMBptr->feuille._VECTptr;
      gen g=arg[1],expnum,expden;
      if (g.type==_FRAC){
	expnum=g._FRACptr->num;
	expden=g._FRACptr->den;
      }
      else {
	if ( (g.type!=_SYMB) || (g._SYMBptr->sommet!=at_prod) )
	  continue;
	gen & arg1=g._SYMBptr->feuille;
	if (arg1.type!=_VECT)
	  continue;
	vecteur & v=*arg1._VECTptr;
	if ( (v.size()!=2) || (v[1].type!=_SYMB) || (v[1]._SYMBptr->sommet==at_inv) )
	  continue;
	expnum=v[0];
	expden=v[1]._SYMBptr->feuille;
      }
      if ( (expden.type!=_INT_) || (expden.val!=2 ) )
	continue;
      if (is_one(expnum) && is_integer(arg[0]))
	continue;
      vecteur lv(alg_lvar(arg[0]));
      gen a,num,den;
      a=e2r(arg[0],lv,contextptr);
      fxnd(a,num,den);
      gen nd=num*den;
      gen nover2=rdiv(expnum,plus_two,contextptr);      
      if (nd.type==_INT_ || nd.type==_ZINT){
	gen simpl,doubl;
	bool pos;
	zint2simpldoublpos(nd,simpl,doubl,pos,2,contextptr);
	if (!pos) simpl=-simpl;
	lin.push_back(*it);
	lout.push_back(pow(doubl/abs(r2e(den,lv,contextptr),contextptr),expnum,contextptr)*pow(simpl,nover2,contextptr));
	continue;
      }
      if (nd.type!=_POLY)
	continue;
      lin.push_back(*it);
      const factorization & f=rsqff(*nd._POLYptr);
      polynome s(plus_one,nd._POLYptr->dim),d(plus_one,s.dim);
      factorization::const_iterator jt=f.begin(),jtend=f.end();
      for (;jt!=jtend;++jt){
	if (jt->mult%2)
	  s=s*jt->fact;
	d=d*pow(jt->fact,jt->mult/2);
      }
      // Extract integer content of s
      gen cont=Tppz<gen>(s);
      gen simpl,doubl; bool pos;
      zint2simpldoublpos(cont,simpl,doubl,pos,2,contextptr);
      if (!pos) simpl=-simpl;
      simpl=r2e(simpl,lv,contextptr); // if simpl is not an integer
      lout.push_back(pow(simpl,nover2,contextptr)*pow(doubl,expnum,contextptr)*pow(recursive_normal(r2e(s,lv,contextptr),contextptr),nover2,contextptr)*pow(abs(r2e(d,lv,contextptr),contextptr),expnum,contextptr)*pow(abs(r2e(den,lv,contextptr),contextptr),-expnum,contextptr));
    }
    return subst(e,lin,lout,false,contextptr);
  }

  static bool has_embedded_fractions(const gen & g){
    if (g.type==_POLY){
      vector< monomial<gen> >::const_iterator it=g._POLYptr->coord.begin(),itend=g._POLYptr->coord.end();
      for (;it!=itend;++it){
	if (has_embedded_fractions(it->value))
	  return true;
      }
      return false;
    }
    if (g.type==_VECT){
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	if (has_embedded_fractions(*it))
	  return true;
      }
      return false;
    }
    if (g.type==_FRAC){
      if (is_one(g._FRACptr->den))
	return has_embedded_fractions(g._FRACptr->num);
      return true;
    }
    return false;
  }

  static bool sort_func(const gen & a,const gen & b){
    if (a.type!=b.type)
      return a.type<b.type;
    if (a.type==_IDNT)
      return strcmp(a._IDNTptr->id_name,b._IDNTptr->id_name)<0;
    if (a.type==_SYMB){
      int cmp=strcmp(a._SYMBptr->sommet.ptr()->s,b._SYMBptr->sommet.ptr()->s);
      if (cmp) return cmp<0;
    }
    return a.print(context0)<b.print(context0);
  }
  static vecteur sort1(const vecteur & l){
    vecteur res(l);
    sort(res.begin(),res.end(),sort_func);
    return res;
  }
  static void sort0(vecteur & l){
    iterateur it=l.begin(),itend=l.end();
    for (;it!=itend;++it){
      if (it->type==_VECT)
	*it=sort1(*it->_VECTptr);
    }
  }

  static bool do_mult_i(const gen & g){
    if (g.type==_CPLX && is_zero(*g._CPLXptr) && is_positive(*(g._CPLXptr+1),context0))
      return true;
    if (g.type==_POLY && do_mult_i(g._POLYptr->coord.front().value))
      return true;
    if (g.type!=_EXT)
      return false;
    if (g._EXTptr->type!=_VECT || g._EXTptr->_VECTptr->empty())
      return false;
    return do_mult_i(g._EXTptr->_VECTptr->front());
  }

  gen normal(const gen & e,bool distribute_div,GIAC_CONTEXT){
    // cout << e << endl;
    if (e.type==_VECT){
      vecteur res;
      for (const_iterateur it=e._VECTptr->begin();it!=e._VECTptr->end();++it)
	res.push_back(normal(*it,distribute_div,contextptr));
      return gen(res,e.subtype);
    }
    if (e.type==_FRAC){
      gen n=e._FRACptr->num;
      gen d=e._FRACptr->den;
      simplify(n,d);
      if (is_one(d))
	return n;
      if (is_minus_one(d))
	return -n;
      if (is_zero(d)){
	if (is_zero(n))
	  return undef;
	else
	  return unsigned_inf;
      }
      if (is_zero(n))
	return zero;
      return fraction(n,d);
    }
    if (e.type==_MOD){
      gen p=*e._MODptr;
      gen m=*(e._MODptr+1);
      vecteur l(lvar(m));
      if (l.empty()){
	l=lvar(p);
	p=e2r(p,l,contextptr);
	gen num,den;
	fxnd(p,num,den);
	num=makemod(num,m);
	if (!is_one(den))
	  den=makemod(den,m);
	p=rdiv(num,den,contextptr);
	return r2e(p,l,contextptr);
      }
      return _quorem(makesequence(p,m),contextptr)[1];
    }
    if (e.type!=_SYMB)
      return e;
    if (e._SYMBptr->sommet==at_equal){
      vecteur & v=*e._SYMBptr->feuille._VECTptr;
      return symbolic(at_equal,makesequence(normal(v.front(),distribute_div,contextptr),normal(v.back(),distribute_div,contextptr)));
    }
    if (is_inf(e) || is_undef(e) )
      return e;
    gen ee,tmp;
    matrice l;
    fraction f(0);
#ifndef NO_STDEXCEPT
    try {
#endif
      ee=normalize_sqrt(e,contextptr);
      l=alg_lvar(ee);
      sort0(l);
      tmp=e2r(ee,l,contextptr);
      if (is_undef(tmp)){
	*logptr(contextptr) << gettext("Unable to build a single algebraic extension for simplifying. Trying rational simplification only.") << endl;
	l=lvar(ee);
	tmp=e2r(ee,l,contextptr);	
      }
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & err){
      cerr << err.what() << endl;
      return e;
    }
#endif
    if (tmp.type==_FRAC){
      f.num=tmp._FRACptr->num;
      f.den=tmp._FRACptr->den;
    }
    else
      f=tmp;
    if (f.den.type==_CPLX){
      f.num=f.num*conj(f.den,contextptr);
      f.den=f.den*conj(f.den,contextptr);
    }
    if (do_mult_i(f.den)){
      f.num = -cst_i*f.num;
      f.den = -cst_i*f.den;
    }
    if (do_mult_i(-f.den)){
      f.num = cst_i*f.num;
      f.den = cst_i*f.den;
    }
    // search for embedded fractions
    if (has_embedded_fractions(f.num) || has_embedded_fractions(f.den))
      return normal(r2sym(f,l,contextptr),distribute_div,contextptr);
    if (distribute_div && f.num.type==_POLY && f.num._POLYptr->dim && f.den.type<_POLY)
      return r2sym(gen(*f.num._POLYptr/f.den),l,contextptr);
    if (!distribute_div){
      if (f.den.type==_POLY && is_positive(-f.den._POLYptr->coord.front())){
	f.num=-f.num;
	f.den=-f.den;
      }
      if (f.num.type==_POLY && !f.num._POLYptr->coord.empty() && is_positive(-f.num._POLYptr->coord.front())){
	f.num=-f.num;
	return symbolic(at_neg,r2sym(f,l,contextptr));
      }
    }
    ee=r2sym(f,l,contextptr);
    if (is_integer(f.den) && !is_one(f.den)){
      ee=ratnormal(ratnormal(ee)); // first ratnormal will expand sqrt()^
      // second will remove them
    }
    return ee;
  }

  gen normal(const gen & e,GIAC_CONTEXT){
    return normal(e,true,contextptr);
  }

  gen recursive_normal(const gen & e,bool distribute_div,GIAC_CONTEXT){
    if (e.type==_VECT)
      return apply(e,contextptr,(const gen_op_context) recursive_normal);
    gen e_copy(e); // was eval(e,1,contextptr)); 
    //recursive BUG F(x):=int(1/sqrt(1+t^2),t,0,x);u(x):=exp(x); F(u(x))
    if (e_copy.is_symb_of_sommet(at_pnt))
      e_copy=e;
    if (e_copy.type==_FRAC)
      return e_copy._FRACptr->normal();
    if (e_copy.type!=_SYMB && e_copy.type!=_MOD)
      return e_copy;
    if (is_inf(e_copy) || is_undef(e_copy) )
      return e_copy;
    vecteur l=lvar(e_copy);
    vecteur l_subst(l);
    iterateur it=l_subst.begin(),itend=l_subst.end();
    for (;it!=itend;++it){
      if (it->type!=_SYMB)
	continue;
      if (it->_SYMBptr->sommet!=at_pow){
	gen tmp=it->_SYMBptr->feuille;
	tmp=liste2symbolique(symbolique2liste(tmp,contextptr));
	tmp=recursive_normal(tmp,false,contextptr);
	*it=it->_SYMBptr->sommet(tmp,contextptr);
	continue;
      }
      if (it->_SYMBptr->feuille._VECTptr->front().type==_INT_ && it->_SYMBptr->feuille._VECTptr->back()==plus_one_half)
	continue;
      vecteur l=lvar(it->_SYMBptr->feuille._VECTptr->back());
      int l_size;
      if (!l.empty() && l.front().type==_VECT)
	l_size=l.front()._VECTptr->size();
      else
	l_size=l.size();
      gen f,f_num,f_den;
      f=e2r(it->_SYMBptr->feuille._VECTptr->back(),l,contextptr);
      fxnd(f,f_num,f_den);
      gen num=r2sym(f_num,l,contextptr),den=r2sym(f_den,l,contextptr);
      gen base=recursive_normal(it->_SYMBptr->feuille._VECTptr->front(),false,contextptr);
      if ( is_zero(base) || num.type!=_INT_ || den.type!=_INT_ )
	*it= pow(base,rdiv(num,den,contextptr),contextptr);
      else {
	if (den.val>1 && (is_integer(base) || base.type==_FRAC)){
	  // should also handle more general base.type 
	  vecteur vtmp(den.val+1);
	  vtmp[0]=1;
	  vtmp.back()=-pow(base,num.val % den.val,contextptr);
	  smaller_factor(vtmp);
	  gen tmp;
	  if (vtmp.size()>2)
	    tmp=r2e(algebraic_EXTension(makevecteur(1,0),vtmp),vecteur(1,vecteur(0)),contextptr);
	  else
	    tmp=-vtmp.back()/vtmp.front();
	  *it=pow(base,rdiv(num.val/den.val,den),contextptr)*tmp;
	}
	else
	  *it= pow(base, num.val /den.val,contextptr) *pow(base,rdiv(num.val%den.val,den),contextptr);
      }
    }
    if (l!=l_subst) 
      e_copy=subst(e_copy,l,l_subst,false,contextptr);
    // return global_eval(normal(e_copy),100);
    return normal(e_copy,distribute_div,contextptr);
    // removed eval since it eats neg(x-y)
    // eval(normal(e_copy,distribute_div),contextptr);
  }
  gen recursive_normal(const gen & e,GIAC_CONTEXT){
    gen var,res;
    res=recursive_normal(e,true,contextptr);
    return res;
  }

  gen _recursive_normal(const gen & e,GIAC_CONTEXT){
    gen var,res;
    if (e.is_symb_of_sommet(at_equal))
      return apply_to_equal(e,recursive_normal,contextptr); // symb_equal(_recursive_normal(equal2diff(e),contextptr),0);
    if (is_algebraic_program(e,var,res))
      return symbolic(at_program,makesequence(var,0,recursive_normal(res,contextptr)));
    res=recursive_normal(e,true,contextptr);
    return res;
  }

  gen ratnormal(const gen & e){
    // cout << e << endl;
    if (e.type==_VECT)
      return apply(e,ratnormal);
    if (e.type==_FRAC){
      gen n=e._FRACptr->num;
      gen d=e._FRACptr->den;
      simplify(n,d);
      if (is_one(d))
	return n;
      if (is_minus_one(d))
	return -n;
      if (is_zero(d)){
	if (is_zero(n))
	  return undef;
	else
	  return unsigned_inf;
      }
      if (is_zero(n))
	return zero;
      return fraction(n,d);
    }
    if (e.type!=_SYMB && e.type!=_MOD)
      return e;
    if (is_inf(e) || is_undef(e) )
      return e;
    matrice l; lvar(e,l);
    if (l.size()>1) l=sort1(l);
    gen fg=e2r(e,l,context0); // ok
    if (fg.type==_FRAC && fg._FRACptr->num.type==_FRAC){
      fraction f(fg._FRACptr->num._FRACptr->num,fg._FRACptr->den*fg._FRACptr->num._FRACptr->den);
      f.normal();
      return r2sym(f,l,context0); // ok
    }
    if (fg.type==_FRAC && fg._FRACptr->den.type==_CPLX){
      gen tmp=conj(fg._FRACptr->den,context0);
      fg._FRACptr->num = fg._FRACptr->num * tmp;
      fg._FRACptr->den = fg._FRACptr->den * tmp;
    }
    return r2sym(fg,l,context0);
  }
  gen rationalgcd(const gen & a, const gen & b){
    gen A,B,C,D;
    if (is_algebraic_program(a,A,B) && is_algebraic_program(b,C,D)){
      if (A==C)
	return symbolic(at_program,makesequence(A,0,gcd(B,D)));
      D=subst(D,C,A,false,context0);
      return symbolic(at_program,makesequence(A,0,gcd(B,D)));
    }
    vecteur l(alg_lvar(a));
    alg_lvar(b,l);
    fraction fa(e2r(a,l,context0)),fb(e2r(b,l,context0)); // ok
    if (debug_infolevel)
      cerr << "rational gcd begin " << clock() << endl;
    if (!is_one(fa.den) || !is_one(fb.den))
      cerr << "Warning gcd of fractions " << fa << " " << fb ;
    if (fa.num.type==_FRAC)
      fa.num=fa.num._FRACptr->num;
    if (fb.num.type==_FRAC)
      fb.num=fb.num._FRACptr->num;
    return r2sym(gcd(fa.num,fb.num),l,context0); // ok
  }

  static gen factor(const polynome & p,const vecteur &l,bool fixed_order,bool with_sqrt,gen divide_an_by,gen & extra_div,GIAC_CONTEXT);

  static gen factor_multivar(const polynome & p,const vecteur &l,bool fixed_order,bool with_sqrt,gen divide_an_by,gen & extra_div,GIAC_CONTEXT){
    polynome pp(p);
    int nvars=l.size();
    if (l.front().type==_VECT)
      nvars=l.front()._VECTptr->size();
    vector<int> deg(nvars);
    int mindeg=pp.lexsorted_degree();
    int posmin=0;
    for (int i=1;i<nvars;i++){
      int d=pp.degree(i);
      deg[i]=d;
      if (d<mindeg){
	posmin=i;
	mindeg=d;
      }
    }
    // move posmin variable at the beginning for p and l
    vecteur lp;
    if (posmin && !fixed_order){
      vecteur lptemp;
      pp.reorder(transposition(0,posmin,nvars));
      vecteur::const_iterator it;
      if (l.front().type==_VECT){
	it=l.front()._VECTptr->begin();
	++it;
	for (int i=1;i<nvars;i++,++it){
	  if (i==posmin){
	    if (lptemp.empty())
	      lptemp.push_back(*it);
	    else
	      lptemp.insert(lptemp.begin(),*it);
	    lptemp.push_back(l.front()._VECTptr->front());
	  }
	  else
	    lptemp.push_back(*it);
	}
	lp=l;
	lp[0]=lptemp;
      }
      else {
	it=l.begin();
	++it;
	for (int i=1;i<nvars;i++,++it){
	  if (i==posmin){
	    if (lp.empty())
	      lp.push_back(*it);
	    else
	      lp.insert(lp.begin(),*it);
	    lp.push_back(l.front());
	  }
	  else
	    lp.push_back(*it);
	}
      }
    }
    else
      lp=l;
    factorization v;
    polynome p_content(pp.dim);
    if (!factor(pp,p_content,v,false,with_sqrt,complex_mode(contextptr),divide_an_by,extra_div))
      return gentypeerr(gettext("Not implemented, e.g. for multivariate mod polynomials"));
    // factor p_content
    pp=p_content.trunc1();
    vecteur ll;
    if (lp.front().type==_VECT){
      ll=lp;
      ll[0]=cdr_VECT(*(ll[0]._VECTptr));
    }
    else
      ll=cdr_VECT(lp);
    gen tmp=factor(pp,ll,false,with_sqrt,1,extra_div,contextptr);
    return tmp*r2sym(v,lp,contextptr)/r2sym(extra_div,lp,contextptr);
  }

  static gen factor(const polynome & p,const vecteur &l,bool fixed_order,bool with_sqrt,gen divide_an_by,gen & extra_div,GIAC_CONTEXT){
    // find main var, make permutation on f.num, f.den and l
    //    cout << "Factor " << p << " " << l << endl;
    if (is_one(p))
      return 1;
    if (l.empty() )
      return r2sym(p,l,contextptr);
    if (!p.dim){
      gen tmp;
      if (l.front().type==_VECT)
	tmp=r2sym(p,l.begin(),l.end(),contextptr);
      else
	tmp=r2sym(p,l,contextptr);
      return ratfactor(tmp,with_sqrt,contextptr);
    }
    if (p.dim>1)
      return factor_multivar(p,l,fixed_order,with_sqrt,divide_an_by,extra_div,contextptr);
    factorization v;
    polynome p_content(p.dim);
    if (!factor(p,p_content,v,false,with_sqrt,complex_mode(contextptr),divide_an_by,extra_div))
      return gentypeerr(gettext("Not implemented, e.g. for multivariate mod polynomials"));
    // factor p_content
    if (is_one(p_content))
      return r2sym(v,l,contextptr)/r2sym(extra_div,l,contextptr);
    else {
      gen tmp(p_content);
      simplify(tmp,extra_div);
      if ( (tmp.type>_POLY || (tmp.type==_POLY && !tmp._POLYptr->coord.empty() && tmp._POLYptr->coord.front().value.type==_USER)) && 
	   !v.empty() && v.front().mult==1 && (v.size()==1 || v.front().fact.degree(0)==0)
	   ){ // for GF factorization
	v.front().fact = tmp.type==_POLY?(*tmp._POLYptr*v.front().fact):(tmp*v.front().fact);
	return r2sym(v,l,contextptr)/r2sym(extra_div,l,contextptr);
      }
      polynome pp=p_content.trunc1();
      vecteur ll;
      if (l.front().type==_VECT){
	ll=l;
	ll[0]=cdr_VECT(*(ll[0]._VECTptr));
      }
      else
	ll=cdr_VECT(l);
      tmp=factor(pp,ll,false,with_sqrt,1,extra_div,contextptr);
      return tmp*r2sym(v,l,contextptr)/r2sym(extra_div,l,contextptr);
      // return r2sym(tmp,l,contextptr)*r2sym(v,l,contextptr)/r2sym(extra_div,l,contextptr);
    }
  }

  static gen var_factor(const gen & e,const vecteur & l,bool fixed_order,bool with_sqrt,const gen & divide_an_by,GIAC_CONTEXT){
    if (e.type!=_POLY)
      return r2sym(e/divide_an_by,l,contextptr);
    gen extra_div=1;
    return factor(*e._POLYptr,l,fixed_order,with_sqrt,divide_an_by,extra_div,contextptr);
  }

  gen ordered_factor(const gen & ee,vecteur & l,bool with_sqrt,GIAC_CONTEXT){
    gen e=normalize_sqrt(ee,contextptr);
    alg_lvar(e,l);
    gen f_num,f_den,f;
    f=e2r(e,l,contextptr);
    fxnd(f,f_num,f_den);
    return rdiv(var_factor(f_num,l,true,with_sqrt,1,contextptr),var_factor(f_den,l,true,with_sqrt,1,contextptr));
  }

  gen factor(const gen & e,const identificateur & x,bool with_sqrt,GIAC_CONTEXT){
    if (e.type==_VECT){
      vecteur w;
      vecteur::const_iterator it=e._VECTptr->begin(),itend=e._VECTptr->end();
      for (;it!=itend;++it)
	w.push_back(factor(*it,x,with_sqrt,contextptr));
      return w;
    }
    vecteur l(1,vecteur(1,x)); // insure x is the main var
    return ordered_factor(e,l,with_sqrt,contextptr);
  }

  gen factor(const gen & ee,bool with_sqrt,const gen & divide_an_by,GIAC_CONTEXT){
    if (xcas_mode(contextptr)==3 && is_integer(ee))
      return _ifactor(ee,contextptr);
    gen e(ee);
    if (has_num_coeff(ee))
      e=e.evalf(1,contextptr);
    else
      e=normalize_sqrt(ee,contextptr);
    if (e.type==_VECT){
      vecteur w;
      vecteur::const_iterator it=e._VECTptr->begin(),itend=e._VECTptr->end();
      for (;it!=itend;++it)
	w.push_back(factor(*it,with_sqrt,divide_an_by,contextptr));
      return w;
    }
    vecteur l;
    alg_lvar(e,l);
    gen f_num,f_den,f,dnum,dden(1);
    f=e2r(e,l,contextptr);
    fxnd(f,f_num,f_den);
    if (has_EXT(f_num))
      simplify3(f_num,f_den);
    if (divide_an_by.type>=_IDNT){
      gen divide=e2r(divide_an_by,l,contextptr);
      fxnd(divide,dnum,dden);
      if (dnum.type==_POLY)
	dnum=dnum._POLYptr->coord.front().value;
      if (dden.type==_POLY)
	dden=dden._POLYptr->coord.front().value;
    }
    else {
      dnum=divide_an_by;
    }
    if (dden.type==_CPLX){
      gen tmp=conj(dden,contextptr);
      dnum=dnum*tmp;
      dden=dden*tmp;
      f_num=f_num*tmp;
      f_den=f_den*tmp;
    }
    gen N=var_factor(f_num,l,false,with_sqrt,dnum,contextptr);
    gen D=var_factor(f_den,l,false,with_sqrt,dden,contextptr);
    return rdiv(N,D);
  }

  gen factor(const gen & ee,bool with_sqrt,GIAC_CONTEXT){
    return factor(ee,with_sqrt,plus_one,contextptr);
  }

  gen ratfactor(const gen & ee,bool with_sqrt,GIAC_CONTEXT){
    gen e(normalize_sqrt(ee,contextptr));
    if (has_num_coeff(ee))
      e=e.evalf(1,contextptr);
    if (e.type==_VECT){
      vecteur w;
      vecteur::const_iterator it=e._VECTptr->begin(),itend=e._VECTptr->end();
      for (;it!=itend;++it)
	w.push_back(ratfactor(*it,with_sqrt,contextptr));
      return w;
    }
    vecteur l;
    lvar(e,l);
    gen f_num,f_den,f;
    f=e2r(e,l,contextptr);
    fxnd(f,f_num,f_den);
    if (with_sqrt)
      l=vecteur(1,l);
    gen tmp=rdiv(var_factor(f_num,l,false,with_sqrt,1,contextptr),var_factor(f_den,l,false,with_sqrt,1,contextptr));
    return tmp;
  }

  gen factor(const gen & ee,const gen & f,bool with_sqrt,GIAC_CONTEXT){
    if (ee.type==_VECT){
      vecteur & v=*ee._VECTptr;
      int s=v.size();
      vecteur res(s);
      for (int i=0;i<s;++i)
	res[i]=factor(v[i],f,with_sqrt,contextptr);
      return res;
    }
    gen e(ee);
    if (has_num_coeff(ee))
      e=e.evalf(1,contextptr);
    if (f.type==_IDNT)
      return factor(e,*f._IDNTptr,with_sqrt,contextptr);
    if (f.type==_VECT){
      // should check that *f._VECTptr is made only of atomic _SYMB
      return ordered_factor(e,*f._VECTptr,with_sqrt,contextptr);
    }
    return gentypeerr(contextptr);
  }

  gen factorcollect(const gen & args,bool with_sqrt,GIAC_CONTEXT){
    if (args.type!=_VECT)
      return factor(args,with_sqrt,contextptr);
    vecteur & v=*args._VECTptr;
    if (v.empty())
      return gensizeerr(contextptr);
    if (v.size()==1)
      return vecteur(1,factor(v.front(),with_sqrt,contextptr));
    if (args.subtype==_SEQ__VECT){
      if (v.size()>2)
	toomanyargs("factor");
      if (v.back().type!=_IDNT){ // FIXME could be improved!
	gen f=v.back();
	if (v.back().type==_VECT)
	  f=symbolic(at_prod,v.back());
	gen res=factor(v.front()*f,with_sqrt,f,contextptr);
	return res;
      }
      return factor(v.front(),v.back(),with_sqrt,contextptr);
    }
    int s=v.size();
    vecteur res(s);
    for (int i=0;i<s;++i)
      res[i]=factor(v[i],with_sqrt,contextptr);
    return res;
  }
  gen partfrac(const gen & e,const vecteur & l,bool with_sqrt,GIAC_CONTEXT){
    if (e.type==_VECT){
      vecteur w;
      vecteur::const_iterator it=e._VECTptr->begin(),itend=e._VECTptr->end();
      for (;it!=itend;++it)
	w.push_back(partfrac(*it,l,with_sqrt,contextptr));
      return w;
    }
    int l_size;
    gen xvar;
    if (!l.empty() && l.front().type==_VECT){
      l_size=l.front()._VECTptr->size();
      xvar=l.front();
    }
    else {
      l_size=l.size();
      xvar=l;
    }
    if (!l_size)
      return e;
    else
      xvar=xvar._VECTptr->front();
    gen r=e2r(e,l,contextptr);
    gen r_num,r_den;
    fxnd(r,r_num,r_den);
    if (r_den.type!=_POLY){
      if (r_num.type==_POLY)
	return rdiv(r2sym(r_num,l,contextptr),r2sym(r_den,l,contextptr));
      else 
	return e;
    }
    polynome f_den(*r_den._POLYptr),f_num(l_size);
    if (r_num.type==_POLY)
      f_num=*r_num._POLYptr;
    else
      f_num=polynome(r_num,l_size);
    factorization vden;
    polynome p_content(l_size);
    gen extra_div=1;
    factor(f_den,p_content,vden,false,with_sqrt,complex_mode(contextptr),1,extra_div);
    vector< pf<gen> > pfde_VECT;
    polynome ipnum(l_size),ipden(l_size);
    partfrac(f_num,f_den,vden,pfde_VECT,ipnum,ipden,!with_sqrt);
    gen res=rdiv(r2sym(gen(ipnum),l,contextptr),r2sym(gen(ipden),l,contextptr));
    vector< pf<gen> > ::const_iterator it=pfde_VECT.begin(),itend=pfde_VECT.end();
    for (;it!=itend;++it){
      const pf<gen> & current=*it;
      gen reste(r2e(gen(current.num),l,contextptr)),deno(r2e(gen(current.fact),l,contextptr));
      gen cur_deno(r2e(gen(it->den/(it->mult==1?it->fact:pow(it->fact,it->mult))),l,contextptr));
      if (current.mult==1)
	res += reste/cur_deno/deno;
      else {
	for (int i=0;i<current.mult;++i){
	  gen tmp(_quorem(makesequence(reste,deno,xvar),contextptr));
	  if (tmp.type!=_VECT)
	    return gensizeerr(contextptr);
	  vecteur & vtmp=*tmp._VECTptr;
	  reste=vtmp.front();
	  res=res+normal(vtmp.back()/cur_deno,contextptr)/pow(deno,current.mult-i);
	}
      }
    }
    return res; // +r2sym(pfde_VECT,l);
  }

  gen partfrac(const gen & e,const identificateur & x,bool with_sqrt,GIAC_CONTEXT){
    vecteur l;
    l.push_back(x); // insure x is the main var
    l=vecteur(1,l);
    alg_lvar(e,l);
    return partfrac(e,l,with_sqrt,contextptr);
  }

  gen partfrac(const gen & e,bool with_sqrt,GIAC_CONTEXT){
    vecteur l;
    alg_lvar(e,l);
    return partfrac(e,l,with_sqrt,contextptr);
  }

  gen partfrac(const gen & e,const gen & f,bool with_sqrt,GIAC_CONTEXT){
    if (f.type==_IDNT)
      return partfrac(e,*f._IDNTptr,with_sqrt,contextptr);
    if (f.type==_VECT){
      // should check that *f._VECTptr is made only of atomic _SYMB
      return partfrac(e,*f._VECTptr,with_sqrt,contextptr);
    }
    return gentypeerr(contextptr);
  }

  /* User functions */
  static gen symb2poly(const gen & fr,const gen & ba,GIAC_CONTEXT){
    if (fr.type==_VECT)
      return apply1st(fr,ba,contextptr,symb2poly);
    if (ba.type==_VECT)
      return e2r(fr,*(ba._VECTptr),contextptr);
    if (ba.type!=_IDNT && ba.type!=_SYMB)
      return gensizeerr(contextptr);
    vecteur l(1,ba);
    lvar(fr,l);
    gen temp=e2r(fr,l,contextptr);
    l.erase(l.begin());
    gen res;
    gen tmp2(polynome2poly1(temp,1));
    res=l.empty()?tmp2:r2e(tmp2,l,contextptr); // (tmp2.type==_FRAC?gen(fraction(r2e(tmp2._FRACptr->num,l,contextptr),r2e(tmp2._FRACptr->den,l,contextptr))):r2e(tmp2,l,contextptr));
    if (res.type==_VECT && calc_mode(contextptr)!=1)
      res.subtype=_POLY1__VECT;
    return res;
  }
  gen _e2r(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return _e2r(makesequence(args,vx_var),contextptr);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<2)
      return gendimerr(contextptr);
    gen res=v.front();
    for (int i=1;i<s;++i){
      res=symb2poly(res,v[i],contextptr);
    }
    if (res.type!=_VECT && res.type!=_FRAC && res.type!=_POLY) 
      return gen(vecteur(1,res),calc_mode(contextptr)!=1?_POLY1__VECT:0);
    else
      return res;
  }

  static const char _e2r_s []="e2r";
  symbolic symb_e2r(const gen & arg1,const gen & arg2){
    return symbolic(at_e2r,makesequence(arg1,arg2));
  }
  static define_unary_function_eval (__e2r,&giac::_e2r,_e2r_s);
  define_unary_function_ptr5( at_e2r ,alias_at_e2r,&__e2r,0,true);

  gen _r2e(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args.subtype!=_SEQ__VECT)
      return _r2e(gen(makevecteur(args,vx_var),_SEQ__VECT),contextptr);
    vecteur & v=*args._VECTptr;
    int s=v.size();
    if (s<2)
      return _r2e(gen(makevecteur(args,vx_var),_SEQ__VECT),contextptr);
    gen res=v[0];
    for (int i=1;i<s;++i){
      if (v[i].type==_VECT)
	res=r2e(res,*v[i]._VECTptr,contextptr);
      else {
	if (res.type==_VECT)
	  res=horner(*res._VECTptr,v[i]);
      }
    }
    return res;
  }

  static const char _r2e_s []="r2e";
  symbolic symb_r2e(const gen & arg1,const gen & arg2){
    return symbolic(at_r2e,makesequence(arg1,arg2));
  }
  static define_unary_function_eval (__r2e,&giac::_r2e,_r2e_s);
  define_unary_function_ptr5( at_r2e ,alias_at_r2e,&__r2e,0,true);

  static const char _normal_s []="normal";
  symbolic symb_normal(const gen & args){
    return symbolic(at_normal,args);
  }
  static define_unary_function_eval (__normal,(const gen_op_context)giac::_recursive_normal,_normal_s);
  define_unary_function_ptr5( at_normal ,alias_at_normal,&__normal,0,true);

  static const char _non_recursive_normal_s []="non_recursive_normal";
  symbolic symb_non_recursive_normal(const gen & args){
    return symbolic(at_non_recursive_normal,args);
  }
  static define_unary_function_eval (__non_recursive_normal,(const gen_op_context)giac::normal,_non_recursive_normal_s);
  define_unary_function_ptr5( at_non_recursive_normal ,alias_at_non_recursive_normal,&__non_recursive_normal,0,true);


  // guess if g is a program/function: 1 func, 0 unknown, -1 not func
  static int is_program(const gen & g){
#ifdef GIAC_HAS_STO_38
    if (g.type==_IDNT){
      const char * idname=g._IDNTptr->id_name;
      if (strlen(idname)==2 && (idname[0]=='F' || idname[0]=='R' || idname[0]=='X' || idname[0]=='Y'))
	return 1;
      else
	return -1;
    }
#endif
    if (g.type==_FUNC)
      return 1;
    if (g.type==_VECT){
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	int res=is_program(*it);
	if (res)
	  return res;
      }
      return 0;
    }
    if (g.type!=_SYMB)
      return 0;
    if (g.is_symb_of_sommet(at_of) && g._SYMBptr->feuille.type==_VECT){
      return is_program(g._SYMBptr->feuille._VECTptr->back());
    }
    if (g.is_symb_of_sommet(at_program))
      return 1;
    vecteur v(lvar(g));
    if (v.size()==1 && v.front()==g){
      if (g.type!=_SYMB)
	return 0;
      return is_program(g._SYMBptr->feuille);
    }
    for (unsigned i=0;i<v.size();++i){
      int res=is_program(v[i]);
      if (res)
	return res;
    }
    return 0;
  }

  bool guess_program(gen & g,GIAC_CONTEXT){
    if (is_program(g)!=1)
      return false;
    g=eval(g,1,contextptr);
    return true;
  }

  bool is_algebraic_program(const gen & g,gen & f1,gen & f3){
    if (g.type==_FUNC){
      identificateur _tmpi(" x");
      f1=_tmpi;    
      f3=g(f1,context0);
      return true;
    }
    if (!g.is_symb_of_sommet(at_program) || g._SYMBptr->feuille.type!=_VECT || g._SYMBptr->feuille._VECTptr->size()!=3){
      if (is_program(g)!=1)      
	return false;
      identificateur _tmpi(" x");
      f1=_tmpi;    
      f3=g(f1,context0);
      return true;      
    }
    f1=g._SYMBptr->feuille._VECTptr->front();
    if (f1.type==_VECT && f1.subtype==_SEQ__VECT && f1._VECTptr->size()==1)
      f1=f1._VECTptr->front();
    f3=g._SYMBptr->feuille._VECTptr->back();
    bool res= ((f3.type==_SYMB || f3.type==_IDNT) && !f3.is_symb_of_sommet(at_bloc));
    if (res)
      f3=eval(f3,1,context0); // eval operators like /
    return res;
  }
  static const char _factor_s []="factor";
  symbolic symb_factor(const gen & args){
    return symbolic(at_factor,args);
  }
  gen _factor(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_equal(args))
      return apply_to_equal(args,_factor,contextptr);
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_factor(res,contextptr)));
    if (xcas_mode(contextptr)==3)
      res=factorcollect(args,lvar(args).size()==1,contextptr);
    else
      res=factorcollect(args,withsqrt(contextptr),contextptr);
    return res;
  }
  static define_unary_function_eval (__factor,&giac::_factor,_factor_s);
  define_unary_function_ptr5( at_factor ,alias_at_factor,&__factor,0,true);

  static const char _collect_s []="collect";
  gen _collect(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_collect(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_collect,contextptr);
    res=factorcollect(args,false,contextptr);
    return res;
  }
  static define_unary_function_eval (__collect,&giac::_collect,_collect_s);
  define_unary_function_ptr5( at_collect ,alias_at_collect,&__collect,0,true);

  static const char _partfrac_s []="partfrac";
  symbolic symb_partfrac(const gen & args){
    return symbolic(at_partfrac,args);
  }
  gen _partfrac(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen var,res;
    if (is_algebraic_program(args,var,res))
      return symbolic(at_program,makesequence(var,0,_partfrac(res,contextptr)));
    if (is_equal(args))
      return apply_to_equal(args,_partfrac,contextptr);
    if (args.type!=_VECT)
      return partfrac(args,withsqrt(contextptr),contextptr);
    if (args._VECTptr->size()>2)
      return gentoomanyargs(_partfrac_s);
    return partfrac(args._VECTptr->front(),args._VECTptr->back(),withsqrt(contextptr),contextptr);
  }
  static define_unary_function_eval (__partfrac,&giac::_partfrac,_partfrac_s);
  define_unary_function_ptr5( at_partfrac ,alias_at_partfrac,&__partfrac,0,true);

  static const char _resultant_s []="resultant";
  symbolic symb_resultant(const gen & args){
    return symbolic(at_resultant,args);
  }
  gen _resultant(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gentypeerr(contextptr);
    vecteur v =*args._VECTptr;
    int s=v.size();
    if (s<2)
      toofewargs(_resultant_s);
    if (s==2) v.push_back(vx_var);
    if (v.back()==at_lagrange)
      return _det(gen(makevecteur(_sylvester(gen(vecteur(args._VECTptr->begin(),args._VECTptr->begin()+s-1),_SEQ__VECT),contextptr),at_lagrange),_SEQ__VECT),contextptr);
    if (v.size()>3)
      return gentoomanyargs(_resultant_s);
    if (v.back().type==_MOD)
      v.back()=*v.back()._MODptr;
    if (v.back().is_symb_of_sommet(at_prod)){
      const gen & f = v.back()._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2 && f._VECTptr->front().type==_MOD)
	v.back()=f._VECTptr->back();
    }
    gen x=v.back();
    vecteur l;
    l.push_back(x);
    l=vecteur(1,l);
    gen p1=v.front(),p2=v[1];
    alg_lvar(p1,l);
    alg_lvar(p2,l);
    int l_size;
    if (!l.empty() && l.front().type==_VECT)
      l_size=l.front()._VECTptr->size();
    else
      l_size=l.size();
    gen f1,f1_num,f1_den,f2,f2_num,f2_den;
    f1=e2r(makevecteur(p1,p2),l,contextptr);
    f2=f1[1];
    f1=f1[0];
    fxnd(f1,f1_num,f1_den);
    fxnd(f2,f2_num,f2_den);
    if ( (f1_num.type==_POLY) && (f2_num.type==_POLY))
      return r2sym(gen(resultant(*f1_num._POLYptr,*f2_num._POLYptr)),l,contextptr);
    return 1;
  }
  static define_unary_function_eval (__resultant,&giac::_resultant,_resultant_s);
  define_unary_function_ptr5( at_resultant ,alias_at_resultant,&__resultant,0,true);

  /* I/O on stream */
  void readargs_from_stream(istream & inf,vecteur & args,GIAC_CONTEXT){
    string to_parse;
    char c;
    for (bool notbackslash=true;;){
      inf.get(c);
      if (!inf)
	break;
      if ( notbackslash || (c!='\n') )
	to_parse +=c;
      else
	to_parse = to_parse.substr(0,to_parse.size()-1);
      notbackslash= (c!='\\');
    }
    gen e(to_parse,contextptr);
    if (e.type==_VECT)
      args=*e._VECTptr;
    else
      args=makevecteur(e);
  }

  gen read1arg_from_stream(istream & inf,GIAC_CONTEXT){
    string to_parse;
    char c;
    for (bool notbackslash=true;;){
      inf.get(c);
      if (!inf)
	break;
      if ( notbackslash || (c!='\n') )
	to_parse +=c;
      else
	to_parse = to_parse.substr(0,to_parse.size()-1);
      notbackslash= (c!='\\');
    }
    return gen(to_parse,contextptr);
  }

  void readargs(int ARGC, char *ARGV[],vecteur & args,GIAC_CONTEXT){
    // first initialize random generator for factorizations
    srand(0);
    //srand(time(NULL));
    string s;
    if (ARGC==1)
      readargs_from_stream(cin,args,contextptr);
    else {
      if (ARGC==2) {
	if (!secure_run 
#ifndef __MINGW_H
	    && access(ARGV[1],R_OK)==0
#endif
	    ){
	  ifstream inf(ARGV[1]);
	  readargs_from_stream(inf,args,contextptr);
	}
	else {
	  s=string(ARGV[1]);
	  gen e(s,contextptr);
	  args.push_back(e);
	}
      }
      else {
	vecteur v;
	for (int i=1;i<ARGC;i++){
	  s=string(ARGV[i]);
	  gen e(s,contextptr);
	  v.push_back(e);
	}
	args.push_back(v);
      }
    }
    if (args.empty())
      args.push_back(gentypeerr(contextptr));
  }

#if 0
  /* Functions below are not used anymore */
  // rewrite embedded fraction inside g as num/den
  static void reduce_alg_ext(const gen & g,gen & num,gen & den){
    num=g;
    den=plus_one;
    int embeddings=0;
    vector<int> embeddings_s;
    for (;((num.type==_POLY) && (Tis_constant<gen>(*num._POLYptr)));++embeddings){
      embeddings_s.push_back(num._POLYptr->dim);
      num=num._POLYptr->coord.front().value;
    }
    if (num.type==_EXT)
      num=ext_reduce(num);
    if (is_undef(num)) return;
    if (num.type==_FRAC){
      den=num._FRACptr->den;
      num=num._FRACptr->num;
    }
    /* else commented since ext_reduce above might reduce g
       else {
      num=g;
      return;
      } */
    for (;embeddings;--embeddings){
      num=polynome(num,embeddings_s.back());
      den=polynome(den,embeddings_s.back());
      embeddings_s.pop_back();
    }    
  }
#endif

  // check in g all ext1 ext and rewrite them with ext2 
  static void remove_ext_copies(gen & g,const gen & ext1,const gen & ext2){
    if (g.type==_POLY){
      vector<monomial<gen> >::iterator it=g._POLYptr->coord.begin(),itend=g._POLYptr->coord.end();
      for (;it!=itend;++it)
	remove_ext_copies(it->value,ext1,ext2);
      return;
    }
    if (g.type==_VECT){
      iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it)
	remove_ext_copies(*it,ext1,ext2);
    }
    if (g.type==_FRAC)
      remove_ext_copies(g._FRACptr->num,ext1,ext2);
    if (g.type==_EXT){
      gen & ext=*(g._EXTptr+1);
      if (ext==ext1)
	ext=ext2;
      else
	remove_ext_copies(ext,ext1,ext2);
    }
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

