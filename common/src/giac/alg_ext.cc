// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -I../include -DHAVE_CONFIG_H -DIN_GIAC -g -c alg_ext.cc -Wall" -*-
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
#include <cstdlib>
#include <stdexcept>
#include <errno.h>
#include <map>
#include "gen.h"
#include "gausspol.h"
#include "identificateur.h"
#include "poly.h"
#include "usual.h"
#include "sym2poly.h"
#include "vecteur.h"
#include "modpoly.h"
#include "alg_ext.h"
#include "vecteur.h"
#include "solve.h"
#include "subst.h"
#include "plot.h"
#include "derive.h"
#include "ezgcd.h"
#include "prog.h"
#include "intg.h"
#include "csturm.h"
#include "lin.h"
#include "ti89.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  bool islesscomplex(const gen & a,const gen & b){
    if (a==b)
      return false;
    return a.islesscomplexthan(b);
  }
  typedef map<gen,gen,const std::pointer_to_binary_function < const gen &, const gen &, bool> > rootmap;
  static rootmap & symbolic_rootof_list(){
    static rootmap * ans= new rootmap(ptr_fun(islesscomplex));
    return *ans;
  }

  gen algebraic_EXTension(const gen & a,const gen & v){
    if (is_zero(a) )
      return a;
    if (a.type==_VECT){
      if (a._VECTptr->empty())
	return zero;
      if (a._VECTptr->size()==1)
	return a._VECTptr->front();
    }
    gen res;
#ifdef SMARTPTR64
    * ((longlong * ) &res) = longlong(new ref_algext) << 16;
#else
    res.__EXTptr=new ref_algext;
#endif
    res.type=_EXT;
    *(res._EXTptr+1) = v;
    if (a.type==_FRAC){
      *res._EXTptr = a._FRACptr->num;
      return fraction(res,a._FRACptr->den);
    }    
    *res._EXTptr = a;
    return res;
  }

  static gen in_select_root(const vecteur & a,GIAC_CONTEXT){
    if (a.empty() || is_undef(a))
      return undef;
    gen current(a.front());
    double max_re(re(current,contextptr).evalf_double(1,contextptr)._DOUBLE_val),max_im(im(current,contextptr).evalf_double(1,contextptr)._DOUBLE_val);
    const_iterateur it=a.begin(),itend=a.end();
    for (;it!=itend;++it){
      double cur_re(re(*it,contextptr).evalf_double(1,contextptr)._DOUBLE_val),cur_im(im(*it,contextptr).evalf_double(1,contextptr)._DOUBLE_val);
      if (cur_re > max_re ){
	current=*it;
	max_re=cur_re;
	max_im=cur_im;
      }
      else { // same argument
	if ( (cur_re == max_re) && (cur_im>max_im) ){
	  current=*it;
	  max_im=cur_im;
	}
      }
    }
    return current;
  }

  gen select_root(const vecteur & v,GIAC_CONTEXT){
    vecteur a=proot(v);
    return in_select_root(a,contextptr);
  }

  gen alg_evalf(const gen & a,const gen &b,GIAC_CONTEXT){
    if (a.type==_FRAC)
      return rdiv(alg_evalf(a._FRACptr->num,b,contextptr),alg_evalf(a._FRACptr->den,b,contextptr),contextptr);
    gen a1=a.evalf(1,contextptr),b1=b.evalf(1,contextptr);
    if (a1.type!=_VECT)
      return a1;
    if (b1.type!=_VECT)
      return algebraic_EXTension(a1,b1);
    gen r(select_root(*b1._VECTptr,contextptr));
    if (is_undef(r))
      return algebraic_EXTension(a1,b1);
    return horner(*a1._VECTptr,r);
  }

  gen ext_reduce(const gen & a, const gen & v){
    if (a.type==_FRAC)
      return fraction(ext_reduce(a._FRACptr->num,v),ext_reduce(a._FRACptr->den,v));
    if (a.type!=_VECT)
      return a;// algebraic_EXTension(a,v);
    if (a._VECTptr->empty())
      return zero;
    if (a._VECTptr->size()==1)
      return a._VECTptr->front();
    if (v.type==_VECT)
      return algebraic_EXTension((*a._VECTptr) % (*v._VECTptr),v);
    if (v.type==_FRAC)
        return horner(*a._VECTptr,*v._FRACptr);
    if (v.type!=_EXT)
      return gentypeerr(gettext("ext_reduce"));
    gen va=*v._EXTptr,vb=*(v._EXTptr+1);
    if (va.type==_FRAC)
        return ext_reduce(horner(*a._VECTptr,*va._FRACptr),vb);
    if (va.type!=_VECT){
      if (vb.type!=_VECT)  return gensizeerr(gettext("alg_ext.cc/ext_reduce"));
      return algebraic_EXTension( (*a._VECTptr) % (*vb._VECTptr),v);
    }
    return ext_reduce(horner(*a._VECTptr,gen(*va._VECTptr,_POLY1__VECT)),vb);
  }

  gen ext_reduce(const gen & e){
#ifdef DEBUG_SUPPORT
    if (e.type!=_EXT)  return gensizeerr(gettext("alg_ext.cc/ext_reduce"));
#endif    
    if ( (e._EXTptr->type==_VECT) && ((e._EXTptr+1)->type==_VECT) && 
	 (e._EXTptr->_VECTptr->size()<(e._EXTptr+1)->_VECTptr->size()) )
      return e;
    return ext_reduce(*(e._EXTptr),*(e._EXTptr+1));
  }

  static bool polynome2vecteur(const polynome & p,int na,int nb,vecteur & v){
    v=vecteur(na*nb,zero);
    int i,j;
    if (p.dim!=2){
#ifdef NO_STDEXCEPT
      return false;
#else
      setsizeerr(gettext("alg_ext.cc/polynome2vecteur"));
      return false;
#endif
    }
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it){
      i=it->index.front();
      j=it->index.back();
      // cerr << nb*(na-i-1)+nb-j-1 << " " << na*nb << endl;
      v[nb*(na-i-1)+nb-j-1]=it->value;
    }
    return true;
  }

  bool is_known_rootof(const vecteur & v,gen & symroot,GIAC_CONTEXT){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type!=_INT_)
	return false;
    }
    rootmap::iterator ritend=symbolic_rootof_list().end(),rit=symbolic_rootof_list().find(v);
    if (rit!=ritend){
      symroot=rit->second;
      return true;
    }
    if (v.size()==3){
      vecteur w;
      identificateur x(" x");
      in_solve(symb_horner(v,x),x,w,0,contextptr); 
      if (w.empty())
	return false;
      symroot=w.front();
      return true;
    }
    return false;
  }

  // replace _EXT == to ext by g in v
  static vecteur replace_ext(const vecteur & v,const vecteur &ext,const gen & g,GIAC_CONTEXT){
    vecteur res;
    const_iterateur it=v.begin(),itend=v.end();
    res.reserve(itend-it);
    for (;it!=itend;++it){
      gen numtmp=*it,dentmp=1;
      if (it->type==_FRAC){
	numtmp=it->_FRACptr->num;
	dentmp=it->_FRACptr->den;
      }
      // if numtmp is an ext, it must be the same ext as a
      if (numtmp.type==_EXT){
	if (*(numtmp._EXTptr+1)!=ext)
	  return vecteur(1,gensizeerr(gettext("Invalid _EXT in replace_ext")));
	res.push_back(horner(*numtmp._EXTptr,g)/dentmp);
      }
      else
	res.push_back(evalf_double(*it,1,contextptr));
    }
    return res;
  }

  // given theta1 and theta2 with minimal poly va and vb (inside gen ga and gb)
  // find k / Q[theta1+ k*theta2 ] contains theta1 and theta2
  // return the minimal poly of theta=theta1+k*theta2
  // and return in a and b theta1 and theta2 as ext (in terms of theta)
  gen common_minimal_POLY(const gen & ga,const gen & gb, gen & a,gen & b,int & k,GIAC_CONTEXT){
    const vecteur & va=*ga._VECTptr;
    const vecteur & vb=*gb._VECTptr;
    int na=va.size()-1,nb=vb.size()-1;
    if (nb==1){
      k=0;
      vecteur un(2,zero);
      un[0]=plus_one;
      gen vag(va);
      a=algebraic_EXTension(un,vag);
      gen tmp=-vb[1];
      if (tmp.type!=_POLY)
	b=tmp;
      else {
	if (tmp._POLYptr->coord.empty())
	  b=zero;
	else
	  b=tmp._POLYptr->coord.front().value;
      }
      return vag;
    }
    // create minimal polynomial of theta1/theta2 as 2-d polynomials
    // with main variable respectively a and b
    // (since pb is used for reduction after var reordering of p)
    polynome pa(2),pb(2);
    const_iterateur it=va.begin(),itend=va.end();
    for (int d=na;it!=itend;++it,--d){
      if (!is_zero(*it))
	pa.coord.push_back(monomial<gen>(*it,d,1,2)); // deg=d, var=1, dim=2
    }
    it=vb.begin(),itend=vb.end();
    int k_init=0;
    for (int d=nb;it!=itend;++it,--d){
      if (!is_zero(*it)){
	gen numtmp=*it,dentmp=1;
	if (it->type==_FRAC){
	  numtmp=it->_FRACptr->num;
	  dentmp=it->_FRACptr->den;
	}
	polynome pbadd(pb.dim);
	// if numtmp is an ext, it must be the same ext as a
	if (numtmp.type==_EXT){
	  pbadd=poly12polynome(*(numtmp._EXTptr->_VECTptr),1,1).untrunc1(d);
	  k_init=1;
	}
	else
	  pbadd.coord.push_back(monomial<gen>(numtmp,d,1,2));
	pb = pb + pbadd/dentmp;
      }
    }
    if (k_init){
      vecteur v1=*evalf_double(va,1,contextptr)._VECTptr;
      if (is_fully_numeric(v1)){
	// when theta2 depends on theta1, theta1+k*theta2 is not necessarily
	// the largest root, because the numeric value of v2 depends
	// on the selected root of v1
	// 
	// we should compute k*theta1+theta2 for a sufficiently large
	// value of k to insure largest root, e.g. 
	// this implies computing approx value of theta1 and theta2
	// 
	vecteur rac=real_proot(v1,1e-12,contextptr);
	if (!rac.empty() && !is_undef(rac)){
	  gen theta1=_max(rac,contextptr);
	  // replace _EXT in vb by r1 and evaluate numerically
	  vecteur v2=replace_ext(vb,va,theta1,contextptr);
	  if (!v2.empty() && is_undef(v2))
	    return v2.front();
	  // find largest root (i.e. theta2)
	  if (is_fully_numeric(v2)){
	    vecteur rac2=real_proot(v2,1e-12,contextptr);
	    if (!rac2.empty() && !is_undef(rac2)){
	      gen theta2=_max(rac2,contextptr);
	      int racs=rac.size();
	      for (int i=0;i<racs;++i){
		gen r1=rac[i],K;
		if (r1==theta1)
		  continue;
		v2=replace_ext(vb,va,r1,contextptr);
		if (!v2.empty() && is_undef(v2))
		  return v2.front();
#ifndef NO_STDEXCEPT
		try {
#endif
		  gen r2=_max(real_proot(v2,1e-12,contextptr),contextptr);
		  K=(r2-theta2)/(theta1-r1); // must be <= k
#ifndef NO_STDEXCEPT
		}
		catch (std::runtime_error & ){
		  K=0;
		}
#endif
		// so that r2-theta2 <= k*theta1-k*r1
		// or k*r1+r2 <= k*theta1+theta2
		K=_floor(K,0)+1;
		if (is_positive(K,contextptr) && K.type!=_INT_)
		  return gensizeerr(gettext("Unable to find common minimal polynomial"));
		k_init=std::max(k_init,K.val);
	      }
	    } // !rac2.empty
	  }
	} // if !rac.empty()
      } // fully_numeric
    }
    else
      ++k_init; // start with k=1 if theta2 does not depend on theta1
    matrice m;
    m.reserve(na*nb);
    for (k=k_init;;++k){
      polynome p(2);
      p.coord.push_back(monomial<gen>(1,2));
      polynome q(2);
      q.coord.push_back(monomial<gen>(k,1,1,2)); // k*a: deg=1, var=1, dim=2
      q.coord.push_back(monomial<gen>(1,1,2,2)); // b: deg=1, var=2
      // create the matrix
      // lines are 1, k*a+b, ..., (k*a+b)^(na*nb)
      // in terms of (columns)
      // a^(na-1)*b^(nb-1) ... a^(na-1) ... ab^(nb-1) ... ab a b^(nb-1) ... b 1
      m.clear();
      vecteur ligne;
      for (int j=0;j<=na*nb;++j){
	if (!polynome2vecteur(p,na,nb,ligne))
	  return gensizeerr(gettext("alg_ext.cc/polynome2vecteur"));
	// ligne.push_back(pow(theta,j));
	m.push_back(ligne);
	p=p*q;
	// permutation of indices order before making division by pb
	p.reorder(transposition(0,1,2));
	p=p%pb;
	p.reorder(transposition(0,1,2));
	// division by a after because b might depend on a
	p=p%pa;
      }
      // Add the lines corresponding to b and a (i.e. theta2, theta1)
      ligne=vecteur(na*nb);
      ligne[na*nb-2]=plus_one;
      m.push_back(ligne);
      ligne=vecteur(na*nb);
      ligne[na*nb-nb-1]=plus_one;
      m.push_back(ligne);
      // Transpose matrix
      // then we have the na*nb+3 columns 1, theta, ..., theta^(na*nb), b, a 
      // in terms of a basis (with na*nb coordinates)
      m=mtran(m);
      // reduce the matrix m to echelon form and test rank=na*nb
      // if ok break, else try another value of k
      matrice m_red;
      vecteur pivots;
      gen det;
      mrref(m,m_red,pivots,det,0,na*nb,0,na*nb+3,
	    /* fullreduction */1,0,true,1,0,
	    contextptr);
      m=m_red;
      // the reduced matrix m should have the form
      // * 0      ... 0 * * *
      // 0 *      ... 0 * * *
      //          ...
      // 0 0      ... 0 * * *
      // 0 0      ... 0 * * *
      // 0 0      ... ? * * *
      // with ? != 0, we check ?, if it is zero we try another value k
      vecteur v(m[na*nb-1]._VECTptr->begin(),m[na*nb-1]._VECTptr->end()-1);
      if (!is_zero__VECT(v,contextptr))
	break;
    }
    mdividebypivot(m);
    // add a -1 at the end of column na*nb (C convention, index starting at 0) 
    // to get the min poly
    vecteur v(na*nb+1);
    for (int i=0;i<na*nb;++i)
      v[i]=-m[i][na*nb];
    v[na*nb]=plus_one;
    reverse(v.begin(),v.end());
    // remove denominators
    gen e;
    lcmdeno(v,e,contextptr);
    // use column na*nb+1 to find b=theta2 in terms of theta
    vecteur w(na*nb);
    for (int i=0;i<na*nb;++i)
      w[i]=m[i][na*nb+1];
    reverse(w.begin(),w.end());
    w=trim(w,0);
    lcmdeno(w,e,contextptr);
    b=fraction(w,e);
    // to get a=theta1 we use column na*nb+2
    w=vecteur(na*nb);
    for (int i=0;i<na*nb;++i)
      w[i]=m[i][na*nb+2];
    reverse(w.begin(),w.end());
    w=trim(w,0);
    lcmdeno(w,e,contextptr);
    a=fraction(w,e);    
    // convert to algebraic extensions
    gen vg(v);
    b=algebraic_EXTension(b,vg);
    a=algebraic_EXTension(a,vg);
    // add v to the rootof_list
    rootmap::iterator ritend=symbolic_rootof_list().end(),rit=symbolic_rootof_list().find(v);
    if (rit==ritend){
      // should first check that va/vb are solvable poly
      gen gaa,gbb;
      if (is_known_rootof(va,gaa,contextptr) && is_known_rootof(vb,gbb,contextptr))
	symbolic_rootof_list()[v]=gaa +k*gbb;
    }
    return vg;
  }

  // assuming a is the extptr+1 of an ext, return the min pol of
  // theta generating the algebraic extension
  vecteur min_pol(gen & a){
    if (a.type==_VECT)
      return *a._VECTptr;
    else {    
      if ( (a.type!=_EXT) || ((a._EXTptr+1)->type!=_VECT) )  
	return vecteur(1,gensizeerr(gettext("alg_ext.cc/min_pol")));
      return *((a._EXTptr+1)->_VECTptr);
    }
  }

  // Find an evaluation point for p at b where pb=p[b] is squarefree
  bool find_good_eval(const polynome & F,polynome & Fb,vecteur & b){
    int Fdeg=F.lexsorted_degree(),nvars=b.size();
    gen Fg;
    int essai=0;
    for (;;++essai){
      Fb=peval_1(F,b,0);
      if (Fb.lexsorted_degree()==Fdeg && gcd(Fb,Fb.derivative()).lexsorted_degree()==0 ){
	return true;
      }
      b=vranm(nvars,0,0); // find another random point
    }
  }

  static void clean(gen & g);
  static void clean(polynome & p){
    vector< monomial<gen> >::iterator it=p.coord.begin(),itend=p.coord.end();
    for (;it!=itend;++it)
      clean(it->value);
  }

  static void clean(gen & g){
    if (g.is_symb_of_sommet(at_neg) && is_integer(g._SYMBptr->feuille))
      g=-g._SYMBptr->feuille;
    if (g.type==_POLY){
      clean(*g._POLYptr);
      return;
    }
    if (g.type==_VECT){
      iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it)
	clean(*it);
      return;      
    }
    if (g.type==_EXT){
      clean(*g._EXTptr);
      clean(*(g._EXTptr+1));
    }
  }

  // a and b are supposed to be *(_EXTptr+1) of some algebraic extension
  // common_EXT will return a new algebraic extension 
  // (suitable to be an extptr+1)
  // and will modify a and b to be ext of the returned common_EXT
  gen common_EXT(gen & a,gen & b,const vecteur * l,GIAC_CONTEXT){
    if (a==b)
      return a;
    // extract minimal polynomials
    gen a_orig(a),b_orig(b);
    gen a__VECT,b__VECT;
    if (a.type==_VECT)
      a__VECT=a;
    else {
      if ( (a.type!=_EXT) || ((a._EXTptr+1)->type!=_VECT) )  
	return gensizeerr(gettext("alg_ext.cc/common_EXT"));
      a__VECT=*(a._EXTptr+1);
    }
    if (b.type==_VECT)
      b__VECT=b;
    else {
      if ( (b.type!=_EXT) || ((b._EXTptr+1)->type!=_VECT) )  
	return gensizeerr(gettext("alg_ext.cc/common_EXT"));
      b__VECT=*(b._EXTptr+1);
    }
    int as=a__VECT._VECTptr->size(),bs=b__VECT._VECTptr->size();
    if (bs>as)
      return common_EXT(b,a,l,contextptr);
    if (as==3 && bs==3 && is_one(a[0]) && is_one(b[0]) && is_zero(a[1]) && is_zero(b[1]) && a[2]==-b[2]){ // sqrt(X) and sqrt(-X)
      b=algebraic_EXTension(makevecteur(cst_i,0),a);
      return a;
    }
    // reduce extension degree by factorizing b__VECT over Q[a]
    polynome p(poly12polynome(*b__VECT._VECTptr));
    polynome p_content(p.dim);
    factorization f;
    gen an,extra_div;
    ext_factor(p,algebraic_EXTension(a__VECT,a__VECT),an,p_content,f,false,extra_div);
    // now choose in the factorization which factor is relevant for b
    // this is done by approximation if possible
    // or by choosing the factor of lowest degree
    // this way we update b__VECT
    int min_deg=b__VECT._VECTptr->size();
    factorization::const_iterator f_it=f.begin(),f_itend=f.end();
    bool trouve=false;
    if (f_itend-f_it==1)
      trouve=true;
    vecteur racines;
    vector<double> real_racines;
    int innerdim=0;
    const_iterateur b_it=b__VECT._VECTptr->begin(),b_itend=b__VECT._VECTptr->end();
    for (;b_it!=b_itend;++b_it){
      if (b_it->type==_POLY)
	innerdim=b_it->_POLYptr->dim;
    }
    vecteur vb(innerdim);
    gen racine_max=undef;
    if (!trouve){
      // Change for multivariate polynomials p, added evaluation
      if (innerdim){
	polynome pb(1),px(unsplitmultivarpoly(p,innerdim));
	find_good_eval(px,pb,vb);
	*logptr(contextptr) << gettext("Warning, choice of an algebraic branch for root of a polynomial with parameters might be wrong. The choice is done for parameters value=0 if 0 is regular, otherwise randomly. Actual choice is ") << vb << endl;
	racines=proot(polynome2poly1(pb));
      }
      else
	racines=proot(*b__VECT._VECTptr); 
      if (is_undef(racines)) return gensizeerr(contextptr);
      // racines= list of approx roots if b__VECT is numeric
      // empty if not numeric
      racine_max=in_select_root(racines,contextptr);
    }
    if (!trouve && !is_undef(racine_max)){ // select root for b
      // now eval each factor over racine_max and choose the one with
      // minimal absolute value
      double min_abs=0;
      for (;f_it!=f_itend;++f_it){
	vecteur vtmp(polynome2poly1(f_it->fact));
	gen tmp;
	lcmdeno(vtmp,tmp,contextptr);
	int maxsave=max_sum_sqrt(contextptr);
	max_sum_sqrt(0,contextptr);
	if (innerdim)
	  tmp=r2sym(vtmp,vecteur(1,vb),contextptr);
	else
	  tmp=r2sym(vtmp,vecteur(1,vecteur(0)),contextptr);
	max_sum_sqrt(maxsave,contextptr);
	tmp=evalf(tmp,1,contextptr);
	gen f_racine_max(evalf_double(abs(horner(tmp,racine_max),contextptr),1,contextptr));
	if (f_racine_max.type!=_DOUBLE_)
	  continue;
	double current_evaluation=fabs(f_racine_max._DOUBLE_val);
	if (!trouve){
	  trouve=true;
	  min_abs=current_evaluation;
	  p=f_it->fact;
	}
	else {
	  if (min_abs>current_evaluation){
	    min_abs=current_evaluation;
	    p=f_it->fact;
	  }
	}
      }
    }
    if (!trouve) {
      for (;f_it!=f_itend;++f_it){
	if ( (b.type==_EXT) && is_zero(horner(polynome2poly1(f_it->fact,1),*b._EXTptr)) ){
	  p=f_it->fact;
	  break;
	}
	int d=f_it->fact.lexsorted_degree();
	if (d && (d<=min_deg)){
	  p=f_it->fact;
	  min_deg=d;
	}
      }
    } // end choose by degree
    clean(p);
    b__VECT=polynome2poly1(p/p.coord.front().value); // p must be monic (?)
    // _VECTute new minimal polynomial
    int k;    
    gen res1=common_minimal_POLY(a__VECT,b__VECT,a,b,k,contextptr);
    if ((a_orig.type==_EXT) && (b_orig.type==_EXT) && !is_undef(res1))
      return algebraic_EXTension(a_orig+gen(k)*b_orig,res1);
    else
      return res1;
  }

  gen ext_add(const gen & aa,const gen & bb,GIAC_CONTEXT){
    gen a(ext_reduce(aa)),b(ext_reduce(bb));
    if ( (a.type!=_EXT) || (b.type!=_EXT) )
      return a+b;
    if (*(a._EXTptr+1)==*(b._EXTptr+1)){
      if ( (a._EXTptr->type==_VECT) && (b._EXTptr->type==_VECT))
	return ext_reduce(*(a._EXTptr->_VECTptr)+ *(b._EXTptr->_VECTptr),*(a._EXTptr+1));
      else
	return ext_reduce(*a._EXTptr+*b._EXTptr,*(a._EXTptr+1));
    }
    gen c=common_EXT(*(a._EXTptr+1),*(b._EXTptr+1),0,contextptr);
    if (is_undef(c)) return c;
    // if c.type==_INT_/_ZINT, call ichinrem on a.extptr,b.extptr,...
    return ext_reduce(a)+ext_reduce(b);
  }

  gen ext_sub(const gen & a,const gen & b,GIAC_CONTEXT){
    if (*(a._EXTptr+1)==*(b._EXTptr+1)){
      if ( (a._EXTptr->type==_VECT) && (b._EXTptr->type==_VECT))
	return ext_reduce(*(a._EXTptr->_VECTptr)- *(b._EXTptr->_VECTptr),*(a._EXTptr+1));
      else
	return ext_reduce(*a._EXTptr-*b._EXTptr,*(a._EXTptr+1));
    }
    return ext_add(a,-b,contextptr);
  }

  gen ext_mul(const gen & aa,const gen & bb,GIAC_CONTEXT){
    gen a(ext_reduce(aa)),b(ext_reduce(bb));
    if ( (a.type!=_EXT) || (b.type!=_EXT) )
      return a*b;
    if (*(a._EXTptr+1)==*(b._EXTptr+1)){
      if ((a._EXTptr->type==_VECT) && (b._EXTptr->type==_VECT))
	return ext_reduce( *(a._EXTptr->_VECTptr) * *(b._EXTptr->_VECTptr),*(a._EXTptr+1));
      else
	return ext_reduce((*a._EXTptr)*(*b._EXTptr),*(a._EXTptr+1));
    }
    gen c=common_EXT(*(a._EXTptr+1),*(b._EXTptr+1),0,contextptr);
    if (is_undef(c)) return c;
    // if c.type==_INT_/_ZINT, call ichinrem on a._EXTptr,b._EXTptr,...
    return ext_reduce(a)*ext_reduce(b);
  }

  gen inv_EXT(const gen & aa){
    gen a(ext_reduce(aa));
    if (a.type==_FRAC){
      return a._FRACptr->den*inv_EXT(a._FRACptr->num);
    }
    if (a.type!=_EXT)
      return inv(a,context0);
    if (a._EXTptr->type==_VECT){
      vecteur u,v,d;
      egcd(*(a._EXTptr->_VECTptr),*((a._EXTptr+1)->_VECTptr),0,u,v,d);
      if (d.size()!=1)
	return gensizeerr(gettext("inv_EXT"));
      gen de=d.front(),du=u;
      simplify(du,de);
      return fraction(algebraic_EXTension(du,*(a._EXTptr+1)),de);
    }
    return gentypeerr(gettext("inv_EXT"));
  }

  static gen horner_rootof(const vecteur & p,const gen & g,GIAC_CONTEXT){
    if (g.type==_SYMB && g._SYMBptr->feuille.type==_VECT && 
	// false
	int(g._SYMBptr->feuille._VECTptr->size())>max_sum_sqrt(contextptr)
	)
      return symb_horner(p,g);
    const_iterateur it=p.begin(),itend=p.end();
    gen res;
    for (;it!=itend;++it){
      res=ratnormal(res*g+*it);
    }
    return ratnormal(res);
  }

  // rootof has 2 args: P(theta) and Pmin(theta)
  gen symb_rootof(const gen & p,const gen &pmin,GIAC_CONTEXT){
    if (p.type!=_VECT)
      return p;
    // first check that pmin is in the list of known rootof
    rootmap::iterator it=symbolic_rootof_list().find(pmin),itend=symbolic_rootof_list().end();
    if (it==itend)
      return symbolic(at_rootof,makevecteur(p,pmin));
    return horner_rootof(*p._VECTptr,it->second,contextptr);
    // return ratnormal(ratnormal(symb_horner(*p._VECTptr,it->second)));
  }
  gen rootof(const gen & e,GIAC_CONTEXT){
    if (e.type!=_VECT)
      return gentypeerr(gettext("rootof"));
    if (e._VECTptr->size()!=2)
      return gendimerr(gettext("rootof"));
    if (has_num_coeff(e))
      return approx_rootof(e,contextptr);
    if (!lop(lvar(e),at_pow).empty())
      return gensizeerr(gettext("Algebraic extensions not allowed in a rootof"));
    // should call factor before returning unevaluated rootof
    return symbolic(at_rootof,e);
  }
  gen approx_rootof(const gen & e,GIAC_CONTEXT){
    if ( (e.type!=_VECT) || (e._VECTptr->size()!=2) )
      return gensizeerr(contextptr);
    gen a=e._VECTptr->front(),b=e._VECTptr->back();
    return alg_evalf(a,b,contextptr);
  }
  /* statically in derive.cc
  static gen d1_rootof(const gen & args,GIAC_CONTEXT){
    return gentypeerr(contextptr);
    return zero;
  }
  static gen d2_rootof(const gen & args,GIAC_CONTEXT){
    return gentypeerr(contextptr);
    return zero;
  }
  define_unary_function_ptr( D1_rootof,alias_D1_rootof,new unary_function_eval(0,&d1_rootof,""));
  define_unary_function_ptr( D2_rootof,alias_D2_rootof,new unary_function_eval(0,&d2_rootof,""));
  static unary_function_ptr d_rootof(int i){
    if (i==1)
      return D1_rootof;
    if (i==2)
      return D2_rootof;
    return gensizeerr(contextptr);
    return 0;
  }
  partial_derivative_multiargs D_rootof(&d_rootof);
  */
  static const char _rootof_s []="rootof";
  static define_unary_function_eval (__rootof,&giac::rootof,_rootof_s);
  define_unary_function_ptr5( at_rootof ,alias_at_rootof,&__rootof,0,true);

  static vecteur sturm(const gen & g){
    if (g.type!=_POLY)
      return vecteur(1,g);
    polynome p(*g._POLYptr);
    polynome pl(lgcd(p));
    polynome pp=p/pl;
    polynome cont(p.dim);
    factorization f(sqff(pp));
    factorization::const_iterator it=f.begin(),itend=f.end();
    gen a=p.coord.front().value;
    for (;it!=itend;++it){
      if (it->mult %2)
	a=a/it->fact.coord.front().value;
    }
    vecteur v(1,pl.coord.empty()?a:a/pl.coord.front().value*pl);
    for (it=f.begin();it!=itend;++it){
      if (it->mult %2)
	v.push_back(sturm_seq(it->fact,cont));
    }
    return v;
  }
  vecteur sturm(const gen &g,const gen & x,GIAC_CONTEXT){
    if (g.type==_VECT)
      return vecteur(1,gensizeerr(contextptr));
    vecteur l;
    if (!is_zero(x))
      l.push_back(x);
    lvar(g,l);
    fraction fa(e2r(g,l,contextptr));
    gen n,d;
    fxnd(fa,n,d);
    vecteur v=mergevecteur(sturm(n),sturm(d));
    vecteur res,tmp,ll=cdr_VECT(l);
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type==_VECT){
	const_iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
	vecteur tmpres;
	tmpres.reserve(jtend-jt);
	for (;jt!=jtend;++jt){
	  if (jt->type==_POLY){
	    tmp=polynome2poly1(*(jt->_POLYptr),1);
	    tmpres.push_back(r2e(tmp,ll,contextptr));
	  }
	  else
	    tmpres.push_back(*jt);
	}
	res.push_back(tmpres);
      }
      else { // it->type != _VECT but we must convert anyway the cst coeff!
	if (it->type==_POLY){
	  gen tmpg=polynome2poly1(*(it->_POLYptr),1).front();
	  res.push_back(r2e(tmpg,ll,contextptr));
	}
	else
	  res.push_back(*it);
      }
    }
    return res;
  }
  // v is a sequence of dense polynomials
  // each poly is evaluated at a, then we count # of sign changes
  // ignoring zeros
  // The function modifies a sign variable according to the sign first
  // non-zero element of v
  static int number_of_sign_changes(const vecteur & v,const gen & a0,int & global_sign,GIAC_CONTEXT){
    gen a=exact(a0,contextptr);
    gen w=normal(apply1st(v,a,horner),contextptr);
    int previous_sign=0,current_sign,res=0;
    const_iterateur it=w._VECTptr->begin(),itend=w._VECTptr->end();
    for (;it!=itend;++it){
      if (is_exactly_zero(*it))
	continue;
      if (ck_is_strictly_positive(*it,contextptr))
	current_sign=1;
      else
	current_sign=-1;
      if (!previous_sign) {// assign first non-zero sign
	previous_sign=current_sign;
	global_sign = global_sign *current_sign;
      }
      if (previous_sign==current_sign)
	continue;
      ++res;
      previous_sign=current_sign;
    }
    return res;
  }
  static int sturmab(const gen & g,const gen & x,const gen & a,const gen & b,bool remove_b_root,GIAC_CONTEXT){
    if (g.type==_VECT){
#ifdef NO_STDEXCEPT
      return -2;
#else
      setsizeerr(contextptr);
#endif
    }
    if (ck_is_strictly_greater(a,b,contextptr))
      return sturmab(g,x,b,a,contextptr);
    int res=0,dontcare,global_sign=1;
    vecteur v=sturm(g,x,contextptr);
    if (is_undef(v))
      return -2;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type==_VECT){
	res += number_of_sign_changes(*it->_VECTptr,a,global_sign,contextptr)-number_of_sign_changes(*it->_VECTptr,b,dontcare,contextptr);
	if (remove_b_root && is_zero(horner(it->_VECTptr->front(),b)))
	  --res;
      }
      else {
	if (!ck_is_positive(*it,contextptr))
	  global_sign = -global_sign;
      }
    }
    if (res)
      return res;
    return (global_sign-1)/2;
  }
  int sturmab(const gen & g,const gen & x,const gen & a,const gen & b,GIAC_CONTEXT){
    return sturmab(g,x,a,b,false,contextptr);
  }
  gen _sturmab(const gen & g_orig,GIAC_CONTEXT){
    if ( g_orig.type==_STRNG && g_orig.subtype==-1) return  g_orig;
    if ( g_orig.type!=_VECT || g_orig._VECTptr->size()<3 )
      return gensizeerr(contextptr);
    vecteur v(*g_orig._VECTptr);
    int s=v.size();
    gen P(v[0]),x(vx_var),a,b;
    if (s==3){ a=v[1]; b=v[2]; } 
    else { 
      x=v[1]; a=v[2]; b=v[3]; 
      if (P.type==_VECT)
	*logptr(contextptr) << gettext("Warning: variable name ignored: ") << x << endl;
    }
    gen ai=im(a,contextptr);
    gen bi=im(b,contextptr);
    if (!is_zero(ai) || !is_zero(bi)){
      gen p=_e2r(gen(makevecteur(P,vecteur(1,x)),_SEQ__VECT),contextptr),n,d,g1,g2;
      if (is_undef(p)) return p;
      fxnd(p,n,d);
      int n1=csturm_square(n,a,b,g1,contextptr);
      int d1=csturm_square(d,a,b,g2,contextptr);
      if (n1==-1 || d1==-1)
	return gensizeerr(contextptr);
      return gen(n1)/2+cst_i*gen(d1)/2;
    }
    if (s==5 && v[4].type==_INT_)
      return sturmab(P,x,a,b,v[4].val!=0,contextptr);
    return sturmab(P,x,a,b,contextptr);
  }
  static const char _sturmab_s []="sturmab";
  static define_unary_function_eval (__sturmab,&giac::_sturmab,_sturmab_s);
  define_unary_function_ptr5( at_sturmab ,alias_at_sturmab,&__sturmab,0,true);

  gen _sturm(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || (g.type==_VECT && g.subtype!=_SEQ__VECT) )
      return sturm(g,zero,contextptr);
    vecteur & v = *g._VECTptr;
    int s=v.size();
    if (s==2)
      return sturm(v.front(),v.back(),contextptr);
    if (s==4)
      return _sturmab(g,contextptr);
    if (s==3){
      if (v[2].type!=_IDNT)
	return gensizeerr(contextptr);
      gen S=_e2r(gen(makevecteur(v[0],v[2]),_SEQ__VECT),contextptr);
      if (is_undef(S)) return S;
      gen R=_e2r(gen(makevecteur(v[1],v[2]),_SEQ__VECT),contextptr);
      if (is_undef(R)) return R;
      if (S.type==_FRAC)
	S=S._FRACptr->num;
      if (R.type==_FRAC)
	R=R._FRACptr->num;
      modpoly r0(gen2vecteur(S)),r1(gen2vecteur(R));
      vecteur listquo,coeffP,coeffR;
      gen pgcd=csturm_seq(r0,r1,listquo,coeffP,coeffR,contextptr);
      return makevecteur(r0,r1,pgcd,listquo,coeffP,coeffR);
    }
    return gendimerr(contextptr);
  }
  static const char _sturm_s []="sturm";
  static define_unary_function_eval (__sturm,&giac::_sturm,_sturm_s);
  define_unary_function_ptr5( at_sturm ,alias_at_sturm,&__sturm,0,true);

  gen _sturmseq(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // should return in the same format as maple
    return _sturm(g,contextptr);
  }
  static const char _sturmseq_s []="sturmseq";
  static define_unary_function_eval (__sturmseq,&giac::_sturmseq,_sturmseq_s);
  define_unary_function_ptr5( at_sturmseq ,alias_at_sturmseq,&__sturmseq,0,true);

  void recompute_minmax(const vecteur & w,const vecteur & range,const gen & expr,const gen & var,gen & resmin,gen & resmax,vecteur & xmin,vecteur & xmax,int direction,GIAC_CONTEXT){
    const_iterateur it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      if (ck_is_strictly_greater(*it,range[1],contextptr) || ck_is_strictly_greater(range[0],*it,contextptr))
	continue;
#ifdef NO_STDEXCEPT
      gen tmp=limit(expr,*var._IDNTptr,*it,direction,contextptr);
#else
      gen tmp;
      try {
	tmp=limit(expr,*var._IDNTptr,*it,direction,contextptr);
      } catch (std::runtime_error & err){
	tmp=undef;
      }
#endif
      if (is_undef(tmp) || tmp==unsigned_inf)
	continue;
      if (tmp==resmax && !equalposcomp(xmax,*it))
	xmax.push_back(*it);
      else {
	if (ck_is_strictly_greater(tmp,resmax,contextptr)){
	  resmax=tmp;
	  xmax=vecteur(1,*it);
	}
      }
      if (tmp==resmin && !equalposcomp(xmin,*it))
	xmin.push_back(*it);
      else {
	if (ck_is_strictly_greater(resmin,tmp,contextptr)){
	  resmin=tmp;
	  xmin=vecteur(1,*it);
	}
      }
    }
  }

  // minmax=0 both 1 min, 2 max, /3 =1 if return x instead of f(x)
  gen fminmax(const gen & g,int minmax,GIAC_CONTEXT){
    gen expr,var;
    vecteur v(gen2vecteur(g));
    if (v.size()==1)
      v.push_back(vx_var);
    if (v.size()!=2)
      return gensizeerr(contextptr);
    expr=v[0];
    var=v[1];
    if (expr.type==_SYMB){
      unary_function_ptr & u=expr._SYMBptr->sommet;
      if (u==at_exp || u==at_ln || u==at_atan || u==at_abs){
	gen tmp=fminmax(makevecteur(expr._SYMBptr->feuille,var),minmax,contextptr);
	if (is_undef(tmp))
	  return tmp;
	if (u==at_abs && tmp.type==_VECT && tmp._VECTptr->size()==2 ){
	  gen t1=tmp._VECTptr->front();
	  gen t2=tmp._VECTptr->back();
	  if (is_positive(t1,contextptr))
	    return tmp;
	  if (is_positive(-t2,contextptr)){
	    return gen(makevecteur(-t2,-t1),_LINE__VECT);
	  }
	  // t1<=0  t2>=0
	  if (is_greater(-t1,t2,contextptr))
	    return gen(makevecteur(0,-t1),_LINE__VECT);
	  else
	    return gen(makevecteur(0,t2),_LINE__VECT);
	}
	if (u==at_ln && tmp.type==_VECT && tmp._VECTptr->size()==2 && is_positive(-tmp._VECTptr->front(),contextptr) )
	  tmp._VECTptr->front()=zero;
	if (minmax/3)
	  return tmp;
	else
	  return u(tmp,contextptr); 
      }
    }
    bool do_find_range=true;
    vecteur range;
    if (var.is_symb_of_sommet(at_equal)){
      gen tmp=var._SYMBptr->feuille;
      if (tmp.type==_VECT && tmp._VECTptr->size()==2){
	gen varminmax=tmp._VECTptr->back();
	var=tmp._VECTptr->front();
	if (varminmax.is_symb_of_sommet(at_interval) && varminmax._SYMBptr->feuille.type==_VECT){
	  range=*varminmax._SYMBptr->feuille._VECTptr;
	  do_find_range=false;
	}
      }
    }
    if (var.type!=_IDNT)
      return gensizeerr(contextptr);
    if (do_find_range){
      find_range(var,range,contextptr);
      if (range.size()!=1 || range.front().type!=_VECT)
	return gensizeerr(gettext("Or condition not implemented"));
      range=*range.front()._VECTptr;
    }
    if (range.size()!=2)
      return gensizeerr(gettext("fminmax, range ")+gen(range).print(contextptr));
    if (range[0]==minus_inf || range[1]==plus_inf){
      // periodic function?
      vecteur w=lvarx(trig2exp(expr,contextptr),var);
      gen period=0;
      for (unsigned i=0;i<w.size();++i){
	if (!w[i].is_symb_of_sommet(at_exp)){
	  period=0;
	  break;
	}
	gen tmp=w[i]._SYMBptr->feuille,a,b;
	if (!is_linear_wrt(tmp,var,a,b,contextptr) || !is_zero(re(a,contextptr))){
	  period=0;
	  break;
	}
	if (is_zero(a))
	  continue;
	a=ratnormal(cst_two_pi/im(a,contextptr)); // current period
	if (is_zero(period))
	  period=a;
	else { // find common period (if it exists)
	  b=ratnormal(period/a);
	  if (b.type!=_INT_ && b.type!=_FRAC){
	    period=0;
	    break;
	  }
	  if (b.type==_FRAC) 
	    period=period*b._FRACptr->den;
	}
      }
      if (!is_zero(period)){
	if (w.size()>1) 
	  expr=simplify(expr,contextptr);
	if (range[0]==minus_inf){
	  if (range[1]==plus_inf){
	    range[1]=period/2;
	    range[0]=-range[1];
	  }
	  else
	    range[0]=range[1]-period;
	}
	else 
	  range[1]=range[0]+period;
      }
    }
    gen df(derive(expr,var,contextptr));
    if (is_undef(df))
      return df;
    gen savevar=var;
    if (var._IDNTptr->in_eval(1,var,savevar,contextptr))
      ;
    giac_assume(symbolic(at_and,makevecteur(symb_superieur_egal(var,range[0]),symb_inferieur_egal(var,range[1]))),contextptr);
    vecteur w=solve(df,var,2,contextptr);
    if (savevar==var)
      _purge(var,contextptr);
    else
      sto(savevar,var,contextptr);
    if (w.empty() && debug_infolevel)
      *logptr(contextptr) << gettext("Warning: ") << df << gettext("=0: no solution found") << endl;
    gen resmin=plus_inf;
    gen resmax=minus_inf;
    vecteur xmin,xmax;
    // Extrema
    recompute_minmax(w,range,expr,var,resmin,resmax,xmin,xmax,0,contextptr);
    // Limits at begin and end of range
    recompute_minmax(vecteur(1,range[0]),range,expr,var,resmin,resmax,xmin,xmax,1,contextptr);
    recompute_minmax(vecteur(1,range[1]),range,expr,var,resmin,resmax,xmin,xmax,-1,contextptr);
    // Singularities
    vecteur ws=find_singularities(expr,*var._IDNTptr,0,contextptr);
    int wss=ws.size();
    w.clear();
    for (int i=0;i<wss;++i){
      if (ws[i]!=range[0] && ws[i]!=range[1])
	w.push_back(ws[i]);
    }
    recompute_minmax(w,range,expr,var,resmin,resmax,xmin,xmax,1,contextptr);
    recompute_minmax(w,range,expr,var,resmin,resmax,xmin,xmax,-1,contextptr);
    if (minmax/3){
      if (minmax %3 ==1)
	return xmin;
      if (minmax %3 ==2)
	return xmax;
      return gen(makevecteur(xmin,xmax),_LINE__VECT);      
    }
    else {
      if (minmax %3 ==1)
	return resmin;
      if (minmax %3 ==2)
	return resmax;
      return gen(makevecteur(resmin,resmax),_LINE__VECT);
    }
  }
  bool is_constant_idnt(const gen & g); // FIXME -> prog.h
  // find extremals values of g
  // should be improved (currently return -1..1 for sin and cos
  int find_range(const gen & g,vecteur & a,GIAC_CONTEXT){
    if (g.type==_IDNT){
      gen g2=g._IDNTptr->eval(1,g,contextptr);
      if ((g2.type==_VECT) && (g2.subtype==_ASSUME__VECT)){
	vecteur v=*g2._VECTptr;
	if ( (v.size()==3) && (v.front()==vecteur(0) || v.front()==_DOUBLE_ || v.front()==_ZINT || v.front()==_SYMB || v.front()==0) && (v[1].type==_VECT)){
	  a=*v[1]._VECTptr;
	  return 1;
	}
	if (v.size()==1 && v.front()==_ZINT)
	  return 2;
      }
    }
    if (g.type==_SYMB){
#ifndef NO_STDEXCEPT
      try {
#endif
	if (g._SYMBptr->feuille.type==_SPOL1)
	  return 0;
	vecteur lv0(lvar(g._SYMBptr->feuille)),lv; // remove cst idnt
	for (unsigned i=0;i<lv0.size();++i){
	  if (!is_constant_idnt(lv0[i]))
	    lv.push_back(lv0[i]);
	}
	if (!lv.empty()){
	  gen res=fminmax(makevecteur(g,lv[0]),0,contextptr);
	  if (is_undef(res))
	    return 0;
	  a=vecteur(1,res);
	  return 1;
	}
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error & ){
      }
#endif
      unary_function_ptr s(g._SYMBptr->sommet);
      if ( (s==at_sin) || (s==at_cos) ){
	a=vecteur(1,gen(makevecteur(minus_one,plus_one),_LINE__VECT));
	return 1;
      }
    }
    a=vecteur(1,gen(makevecteur(minus_inf,plus_inf),_LINE__VECT));
    return 1;
  }

  bool is_sqrt(const gen & a,gen & arg){
    if (a.is_symb_of_sommet(at_sqrt)){
      arg=a._SYMBptr->feuille;
      return true;
    }
    if (!a.is_symb_of_sommet(at_pow))
      return false;
    gen & f = a._SYMBptr->feuille;
    if (f.type!=_VECT || f._VECTptr->size()!=2)
      return false;
    arg = f._VECTptr->front();
    gen & expo = f._VECTptr->back();
    if (expo.type!=_FRAC || !is_one(expo._FRACptr->num))
      return false;
    gen & d =expo._FRACptr->den;
    if (d.type!=_INT_ || d.val!=2)
      return false;
    return true;
  }

  static int insturmsign(const gen & g0,bool strict,GIAC_CONTEXT){
    gen g=recursive_normal(exact(g0,contextptr),contextptr);
    if (has_i(g))
      return 0;
    vecteur v(lvar(g));
    // search for a sqrt inside v: sign(a+b*sqrt(c))=
    // = sign(a) if a^2-c*b^2 > 0, 
    // = sign(b) if a^2-c*b^2 < 0
    int s=v.size();
    if (!s)
      return fastsign(g,contextptr);
    gen v0(v[0]);
    for (int i=0;i<s;++i){ // replace by first idnt with an assumption
      if (v[i].type==_IDNT && v[i]._IDNTptr->eval(1,v[i],contextptr).type!=_IDNT){
	v0=v[i];
      }
      gen a,b,c;
      if (is_sqrt(v[i],c)){
	identificateur x(" x");
	gen g1=subst(g,v[i],x,false,contextptr);
	if (is_linear_wrt(g1,x,b,a,contextptr)){
	  gen s=sign(a*b,contextptr);
	  if (is_one(s) && (s=sign(a,contextptr)).type==_INT_)
	    return s.val;
	  s=sign(a*a-c*b*b,contextptr);
	  if (s.type!=_INT_ || is_zero(s.val))
	    return 0;
	  s=(is_one(s))?sign(a,contextptr):sign(b,contextptr);
	  if (is_one(s))
	    return 1;
	  if (is_minus_one(s))
	    return -1;
	  return 0;
	}
      }
    }
    vecteur a;
    if (!find_range(v0,a,contextptr))
      return -2;
    int previous_sign=2,current_sign=0;
#ifndef NO_STDEXCEPT
    try {
#endif
      const_iterateur ita=a.begin(),itaend=a.end();
      for (;ita!=itaend;++ita){
	if ( (ita->type!=_VECT) || (ita->subtype!=_LINE__VECT) || (ita->_VECTptr->size()!=2) )
	  return 0;
	gen last(ita->_VECTptr->back());
	gen gg(g);
	identificateur idnttmp("t");
	gen testg(subst(g,v0,idnttmp,false,contextptr));
	if (is_zero(limit(testg,idnttmp,last,-1,contextptr))){
	  if (strict && (v0.is_symb_of_sommet(at_sin) || v0.is_symb_of_sommet(at_cos)))
	    return 0;
	  gen tmp=_fxnd(gg,contextptr);
	  if (tmp.type!=_VECT || tmp._VECTptr->size()!=2){
#ifdef NO_STDEXCEPT
	    return -2;
#else
	    setsizeerr(contextptr);
#endif
	  }
	  gen num=tmp._VECTptr->front(),den=tmp._VECTptr->back(),tmpden;
	  tmp=_e2r(makevecteur(num,v0),contextptr);
	  tmpden=_e2r(makevecteur(den,v0),contextptr);
	  if (is_undef(tmp) || is_undef(tmpden))
	    return -2;
	  if (is_inf(last) && tmpden.type==_VECT)
	    den=den/pow(v0,2*int(tmpden._VECTptr->size()/2));
	  tmp=gen2vecteur(tmp);
	  modpoly p(*tmp._VECTptr),q;
	  if (!is_inf(last)) {
	    while (is_zero(horner(p,last,0,q)))
	      p=-q;
	  }
	  gg=_r2e(gen(makevecteur(p,v0),_SEQ__VECT),contextptr)/den;
	}
	current_sign=sturmab(gg,v0,ita->_VECTptr->front(),last,true,contextptr);
	if (current_sign>0 || current_sign==-2)
	  return 0;
	if (previous_sign==2)
	  previous_sign=current_sign;
	if (previous_sign!=current_sign)
	  return 0;
      }
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & ){
      return 0;
    }
#endif
    return 2*current_sign+1;
  }

  int sturmsign(const gen & g0,bool strict,GIAC_CONTEXT){
    gen g=simplifier(g0,contextptr);
    // first check some operators inv, *, exp, sqrt
    if (g.is_symb_of_sommet(at_neg))
      return -sturmsign(g._SYMBptr->feuille,strict,contextptr);
    if (g.is_symb_of_sommet(at_inv))
      return sturmsign(g._SYMBptr->feuille,strict,contextptr);
    if (g.is_symb_of_sommet(at_exp))
      return 1;
    /* if (g.is_symb_of_sommet(at_pow) && g._SYMBptr->feuille[1]==plus_one_half)
       return 1; */
    if (g.is_symb_of_sommet(at_prod)){
      gen &f=g._SYMBptr->feuille;
      vecteur v(gen2vecteur(f));
      int s=v.size();
      vecteur w;
      int res=1,currentsign;
      // remove cst coeffs and exp/
      for (int i=0;i<s;++i){
	if (v[i].is_symb_of_sommet(at_sqrt) && sturmsign(v[i]._SYMBptr->feuille,strict,contextptr)==1)
	  continue;
	if ( (currentsign=fastsign(v[i],contextptr)) )
	  res *= currentsign;
	else
	  w.push_back(v[i]);
      }
      int tmp;
      switch (w.size()){
      case 0:
	return res;
      case 1:
	tmp=insturmsign(w.front(),strict,contextptr); return tmp==-2?-2:res*tmp;
      default:
	tmp=insturmsign(symbolic(at_prod,w),strict,contextptr); return tmp==-2?-2:res*tmp;
      }
    }
    return insturmsign(g,strict,contextptr);
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
