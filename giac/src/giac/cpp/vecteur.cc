// -*- mode:C++ ; compile-command: "g++ -I. -I.. -I../include -g -c vecteur.cc -fno-strict-aliasing -DGIAC_GENERIC_CONSTANTS -DHAVE_CONFIG_H -DIN_GIAC" -*-
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
#include <cmath>
#include <stdexcept>
#include <map>
#include <iostream>
#ifdef HAVE_SSTREAM
#include <sstream>
#else
#include <strstream>
#endif
#include "gen.h"
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
#include "plot.h"
#include "misc.h"
#include "ti89.h"
#include "csturm.h"
#include "sparse.h"
#include "modfactor.h"
#include "quater.h"
#include "giacintl.h"
#ifdef HAVE_LIBGSL
#include <gsl/gsl_linalg.h>
#include <gsl/gsl_eigen.h>
#include <gsl/gsl_poly.h>
#endif

// Apple has the Accelerate framework for lapack if you did not install Atlas/lapack
// (link with -framewrok Accelerate)
// it is not used by default because the Accelerate version is slower 
// than the current Atlas, at least on OSX.6, and is also slower than giac built-in

#if !defined(APPLE_SMART) && !defined(DONT_USE_LIBLAPLACK) 
#if defined __APPLE__ && !defined(HAVE_LIBLAPACK) && !defined(USE_GMP_REPLACEMENTS)
#define HAVE_LIBLAPACK
#endif
// for pocketcas compat.
#if defined(HAVE_LIBCLAPACK) && !defined(HAVE_LIBLAPACK)
#define HAVE_LIBLAPACK
#endif
#endif // APPLE_SMART

// Note that Atlas is slower than built-in for real matrices diago for n < about 1000
// and complex matrices diago for n<300
// the global variable CALL_LAPACK is set to 1111 by default
// can be modified from icas/xcas using lapack_limit() or shell variable GIAC_LAPACK
// #undef HAVE_LIBLAPACK

#ifdef HAVE_LIBLAPACK
#include <f2c.h>
#include <clapack.h>
#undef abs
#undef min
#endif

#if defined __i386__ && !defined PIC && !defined __APPLE__ && !defined _I386_
//#define _I386_
// commented because it will fail with -O2 optimizations under gcc >= 4.3 
// on Ubuntu 11.04 in Mac VirtualBox
#endif

#ifdef USTL
namespace ustl {
  inline bool operator > (const giac::index_t & a,const giac::index_t & b){ 
    if (a.size()!=b.size()) 
      return a.size()>b.size();
    return !giac::all_inf_equal(a,b);
  }
  inline bool operator < (const giac::index_t & a,const giac::index_t & b){ 
    if (a.size()!=b.size()) 
      return a.size()<b.size();
    return !giac::all_sup_equal(a,b);
  }
}
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  unsigned nbits(const gen & g){
    if (g.type==_INT_)
      return sizeinbase2(g.val>0?g.val:-g.val);
    else 
      return mpz_sizeinbase(*g._ZINTptr,2);
  }

#if defined(GIAC_HAS_STO_38) && defined(VISUALC)
  static const int rand_max=2147483647;
#else
  static const int rand_max=RAND_MAX;
#endif

#ifdef _I386_
  // a->a+b*c mod m
  inline void mod(int & a,int b,int c,int m){
    if (c){
      asm volatile("testl %%ebx,%%ebx\n\t" /* sign bit=1 if negative */
		   "jns .Lok%=\n\t"
		   "addl %%edi,%%ebx\n" /* a+=m*/
		   ".Lok%=:\t"
		   "imull %%ecx; \n\t" /* b*c in edx:eax */
		   "addl %%ebx,%%eax; \n\t" /* b*c+a */
		   "adcl $0x0,%%edx; \n\t" /* b*c+a carry */
		   "idivl %%edi; \n\t"
		   :"=d"(a)
		   :"a"(b),"b"(a),"c"(c),"D"(m)
		   );
    }
  }

  // a->a+b*c mod m
  inline int smod(int a,int b,int c,int m){
    if (c){
      if (a<0) a+=m;
      asm volatile("imull %%ecx; \n\t" /* b*c in edx:eax */
		   "addl %%ebx,%%eax; \n\t" /* b*c+a */
		   "adcl $0x0,%%edx; \n\t" /* b*c+a carry */
		   "idivl %%edi; \n\t"
		   :"=d"(a)
		   :"a"(b),"b"(a),"c"(c),"D"(m)
		   );
    }
    return a;
  }
#else
  // a->a+b*c mod m
  inline void mod(int & a,int b,int c,int m){
    a = (a + longlong(b)*c)%m;
  }

  // a->a+b*c mod m
  inline int smod(int a,int b,int c,int m){
    return (a + longlong(b)*c)%m;
  }

#endif

  vecteur makevecteur(const gen & a,const gen & b){
    vecteur v(2);
    v[0]=a;
    v[1]=b;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c){
    vecteur v(3);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    return v;
  }

  vecteur makevecteur(const gen & a){
    return vecteur(1,a);
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d){
    vecteur v(4);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e){
    vecteur v(5);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f){
    vecteur v(6);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g){
    vecteur v(7);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h){
    vecteur v(8);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    v[7]=h;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i){
    vecteur v(9);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    v[7]=h;
    v[8]=i;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i,const gen &j){
    vecteur v(10);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    v[7]=h;
    v[8]=i;
    v[9]=j;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i,const gen &j,const gen & k){
    vecteur v(11);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    v[7]=h;
    v[8]=i;
    v[9]=j;
    v[10]=k;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i,const gen &j,const gen & k,const gen & l){
    vecteur v(12);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    v[7]=h;
    v[8]=i;
    v[9]=j;
    v[10]=k;
    v[11]=l;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i,const gen &j,const gen & k,const gen & l,const gen & m){
    vecteur v(13);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    v[7]=h;
    v[8]=i;
    v[9]=j;
    v[10]=k;
    v[11]=l;
    v[12]=m;
    return v;
  }

  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i,const gen &j,const gen & k,const gen & l,const gen & m,const gen& n){
    vecteur v(14);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    v[7]=h;
    v[8]=i;
    v[9]=j;
    v[10]=k;
    v[11]=l;
    v[12]=m;
    v[13]=n;
    return v;
  }

  gen makesequence(const gen & a){
    return gen(vecteur(1,a),_SEQ__VECT);
  }

  gen makesequence(const gen & a,const gen & b){
    vecteur v(2);
    v[0]=a;
    v[1]=b;
    return gen(v,_SEQ__VECT);
  }

  gen makesequence(const gen & a,const gen & b,const gen & c){
    vecteur v(3);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    return gen(v,_SEQ__VECT);
  }

  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d){
    vecteur v(4);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    return gen(v,_SEQ__VECT);
  }

  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e){
    vecteur v(5);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    return gen(v,_SEQ__VECT);
  }

  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f){
    vecteur v(6);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    return gen(v,_SEQ__VECT);
  }

  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g){
    vecteur v(7);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    return gen(v,_SEQ__VECT);
  }

  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h){
    vecteur v(8);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    v[7]=h;
    return gen(v,_SEQ__VECT);
  }

  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i){
    vecteur v(9);
    v[0]=a;
    v[1]=b;
    v[2]=c;
    v[3]=d;
    v[4]=e;
    v[5]=f;
    v[6]=g;
    v[7]=h;
    v[8]=i;
    return gen(v,_SEQ__VECT);
  }

  ref_vecteur * makenewvecteur(const gen & a){
    return new_ref_vecteur(vecteur(1,a));
  }

  ref_vecteur * makenewvecteur(const gen & a,const gen & b){
    ref_vecteur *vptr=new_ref_vecteur(0);
    vptr->v.reserve(2);
    vptr->v.push_back(a);
    vptr->v.push_back(b);
    return vptr;
  }

  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c){
    ref_vecteur * vptr=new_ref_vecteur(0);
    vptr->v.reserve(3);
    vptr->v.push_back(a);
    vptr->v.push_back(b);
    vptr->v.push_back(c);
    return vptr;
  }

  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d){
    ref_vecteur * vptr=new_ref_vecteur(0);
    vptr->v.reserve(4);
    vptr->v.push_back(a);
    vptr->v.push_back(b);
    vptr->v.push_back(c);
    vptr->v.push_back(d);
    return vptr;
  }

  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e){
    ref_vecteur * vptr=new_ref_vecteur(0);
    vptr->v.reserve(5);
    vptr->v.push_back(a);
    vptr->v.push_back(b);
    vptr->v.push_back(c);
    vptr->v.push_back(d);
    vptr->v.push_back(e);
    return vptr;
  }

  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f){
    ref_vecteur * vptr=new_ref_vecteur(0);
    vptr->v.reserve(6);
    vptr->v.push_back(a);
    vptr->v.push_back(b);
    vptr->v.push_back(c);
    vptr->v.push_back(d);
    vptr->v.push_back(e);
    vptr->v.push_back(f);
    return vptr;
  }

  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g){
    ref_vecteur * vptr=new_ref_vecteur(0);
    vptr->v.reserve(7);
    vptr->v.push_back(a);
    vptr->v.push_back(b);
    vptr->v.push_back(c);
    vptr->v.push_back(d);
    vptr->v.push_back(e);
    vptr->v.push_back(f);
    vptr->v.push_back(g);
    return vptr;
  }

  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h){
    ref_vecteur * vptr=new_ref_vecteur(0);
    vptr->v.reserve(8);
    vptr->v.push_back(a);
    vptr->v.push_back(b);
    vptr->v.push_back(c);
    vptr->v.push_back(d);
    vptr->v.push_back(e);
    vptr->v.push_back(f);
    vptr->v.push_back(g);
    vptr->v.push_back(h);
    return vptr;
  }

  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i){
    ref_vecteur * vptr=new_ref_vecteur(0);
    vptr->v.reserve(9);
    vptr->v.push_back(a);
    vptr->v.push_back(b);
    vptr->v.push_back(c);
    vptr->v.push_back(d);
    vptr->v.push_back(e);
    vptr->v.push_back(f);
    vptr->v.push_back(g);
    vptr->v.push_back(h);
    vptr->v.push_back(i);
    return vptr;
  }

  // make a matrix with free rows 
  // (i.e. it is possible to modify the answer in place)
  matrice makefreematrice(const matrice & m){
    matrice res(m);
    int s=int(m.size());
    for (int i=0;i<s;++i){
      if (m[i].type==_VECT){
	res[i]=makefreematrice(*m[i]._VECTptr);
      }
    }
    return res;
  }

  void transpose_double(const matrix_double & a,int r0,int r1,int c0,int c1,matrix_double & at){
    int L=int(a.size()),C=int(a.front().size());
    if (r0<0) r0=0;
    if (r1<=r0)
      r1=L;
    if (c1<0) c1=0;
    if (c1<=c0)
      c1=C;
    if (r1>L) r1=L;
    if (c1>C) c1=C;
    L=r1-r0; C=c1-c0;
    at.resize(C);
    for (int i=0;i<C;++i)
      at[i].resize(L);
    for (int i=0;i<L;++i){
      const vector<giac_double> & ai=a[r0+i];
      for (int j=0;j<C;++j){
	at[j][i]=ai[c0+j];
      }
    }
  }

  // square matrix inplace transpose
  void transpose_double(matrix_double &P){
    int Ps=int(P.size());
    for (int i=0;i<Ps;++i){
      for (int j=0;j<i;++j){
	giac_double t=P[i][j];
	P[i][j]=P[j][i];
	P[j][i]=t;
      }
    }
  }
  int alphaposcell(const string & s,int & r){
    int ss=int(s.size());
    r=0;
    int i=0;
    for (;i<ss;++i){
      if ( (s[i]>='A') && (s[i]<='Z') )
	r=r*26+(s[i]-'A')+1;
      else {
	if ( (s[i]>='a') && (s[i]<='q') )
	  r=r*26+(s[i]-'a')+1;
	else
	  break;
      }
    }
    --r;
    return i;
  }

  bool iscell(const gen & g,int & r,int & c,GIAC_CONTEXT){
    if (g.type!=_IDNT)
      return false;
    const string & s=g._IDNTptr->name();
    int ss=int(s.size());
    if (ss<2)
      return false;
    int i=alphaposcell(s,r);
    if (!i || (i==ss) )
      return false;
    c=0;
    for (;i<ss;++i){
      if ( (s[i]>='0') && (s[i]<='9') )
	c=c*10+(s[i]-'0');
      else
	break;
    }
    if (xcas_mode(contextptr))
      --c;
    return (i==ss);
  }

  // find all identifiers in g, check if they are of the form
  // Alpha_number, replace them by spread(i,j) if this is the case
  gen spread_convert(const gen & g,int g_row,int g_col,GIAC_CONTEXT){
    // relative cell
    vecteur l(gen2vecteur(_lname(g,contextptr)));
    const_iterateur it=l.begin(),itend=l.end();
    vecteur sub_in,sub_out;
    int r,c;
    for (;it!=itend;++it){
      if (iscell(*it,c,r,contextptr)){
	sub_in.push_back(*it);
	sub_out.push_back(symbolic(at_cell,makevecteur(makevecteur(r-g_row),makevecteur(c-g_col))));
      }
    }
    // absolute cell
    l=lop(g,at_dollar);
    itend=l.end();
    for (it=l.begin();it!=itend;++it){
      gen & f=it->_SYMBptr->feuille;
      // CERR << "absolute cell "<< f << endl;
      if ( (f.type!=_VECT) ){
	if (iscell(f,c,r,contextptr)){
	  sub_in.push_back(*it);
	  sub_out.push_back(symbolic(at_cell,makevecteur(makevecteur(r-g_row),c)));
	}
	continue;
      }
      vecteur & v=*f._VECTptr;
      if (v.size()==2){
	gen & a=v.front();
	gen & b=v.back();
	// CERR << "absolute cell "<< a << " " << b <<endl;
	if (b.type!=_INT_)
	  continue;
	if (xcas_mode(contextptr))
	  r=b.val-1;
	else
	  r=b.val;
	if (a.type==_IDNT){
	  const string & chaine=a._IDNTptr->name();
	  int i=alphaposcell(chaine,c);
	  if (i==signed(chaine.size())){
	    sub_in.push_back(*it);
	    sub_out.push_back(symbolic(at_cell,makevecteur(r,makevecteur(c-g_col))));
	  }
	  continue;
	}
	if ( (a.type==_SYMB) && (a._SYMBptr->sommet==at_dollar) && (a._SYMBptr->feuille.type==_IDNT) ){
	  const string & chaine = a._SYMBptr->feuille._IDNTptr->name();
	  int i=alphaposcell(chaine,c);
	  if (i==signed(chaine.size())){
	    sub_in.push_back(*it);
	    sub_out.push_back(symbolic(at_cell,makevecteur(r,c)));
	  }
	}
      }
    }
    if (sub_in.empty())
      return g;
    gen tmp(quotesubst(g,sub_in,sub_out,contextptr));
    tmp.subtype=_SPREAD__SYMB;
    return tmp;
  }


  string printcell(const vecteur & v,GIAC_CONTEXT){
    // CERR << "printcell" << printcell_current_row << " " << printcell_current_col << " " << v << endl;
    string debut,tmp,fin;
    int i;
    // Note: in popular spreadsheet, the column index comes before the row
    // Therefore we translate v.back before v.front
    if (v.back().type==_INT_){
      i=v.back().val;
      debut="$";
    }
    else 
      i=v.back()._VECTptr->front().val+printcell_current_col(contextptr);
    if (i<0)
      return print_INT_(i);
    for(int j=0;;++j){
      tmp=char('A'+i%26-(j!=0))+tmp;
      i=i/26;
      if (!i)
	break;
    }
    debut=debut+tmp;
    if (v.front().type==_INT_){
      i=v.front().val;
      debut=debut+"$";
    }
    else 
      i=v.front()._VECTptr->front().val+printcell_current_row(contextptr);
    if (xcas_mode(contextptr))
      ++i;
    if (i<0)
      return debut+print_INT_(i);
    for (;;){
      fin=char('0'+i%10)+fin;
      i=i/10;
      if (!i)
	break;
    }
    return debut+fin;
  }

  string printascell(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) )
      return sommetstr+("("+feuille.print(contextptr)+")");
    return printcell(*feuille._VECTptr,contextptr);
  }
  gen _cell(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) )
      return gensizeerr(contextptr);
    return symbolic(at_cell,args);
  }
  static const char _cell_s []="cell";
  static define_unary_function_eval2 (__cell,&_cell,_cell_s,&printascell);
  define_unary_function_ptr5( at_cell ,alias_at_cell,&__cell,0,true);

  static void lcell(const gen & g,vecteur & res){
    if (g.type==_VECT){
      if (g.subtype==_CELL__VECT){
	if (res.empty())
	  res=*g._VECTptr;
	else { // assumes g._VECTptr has much more elements than res
	  vecteur tmp=res;
	  res=*g._VECTptr;
	  const_iterateur it=tmp.begin(),itend=tmp.end();
	  for (;it!=itend;++it){
	    if (!equalposcomp(res,*it))
	      res.push_back(*it);
	  }
	}
      }
      else {
	const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
	for (;it!=itend;++it)
	  lcell(*it,res);
      }
    }
    if (g.type==_SYMB){
      if (g._SYMBptr->sommet==at_cell || g._SYMBptr->sommet==at_deuxpoints){
	if (!equalposcomp(res,g))
	  res.push_back(g);
      }
      else
	lcell(g._SYMBptr->feuille,res);
    }
  }

  vecteur lcell(const gen & g){
    vecteur res;
    lcell(g,res);
    return res;
  }

  // given g=cell() or its argument at row i, column j 
  // return 0 if not a cell, 1 if a cell, then compute r and c s.t. g refers to (r,c), 
  // return 2 if g is e.g. A1:B4 compute ref of A1 and B4
  int cell2pos(const gen & g,int i,int j,int & r,int & c,int & r2,int & c2){
    if (g.is_symb_of_sommet(at_deuxpoints) && g._SYMBptr->feuille.type==_VECT ){
      vecteur & gf=*g._SYMBptr->feuille._VECTptr;
      if (gf.size()!=2)
	return 0;
      int r1,c1;
      if (cell2pos(gf[0],i,j,r,c,r1,c1)==1 && cell2pos(gf[1],i,j,r2,c2,r1,c1)==1)
	return 2;
      return 0;
    }
    vecteur v;
    if ( (g.type==_SYMB) && (g._SYMBptr->sommet==at_cell))
      v=*g._SYMBptr->feuille._VECTptr;
    else {
      if ( (g.type!=_VECT) || (g._VECTptr->size()!=2) )
	return 0;
      v=*g._VECTptr;
    }
    if (v.front().type==_INT_)
      r=v.front().val;
    else
      r=i+v.front()._VECTptr->front().val;
    if (v.back().type==_INT_)
      c=v.back().val;
    else
      c=j+v.back()._VECTptr->front().val;
    return 1;
  }

  // return cell(r,c) argument at (i,j) with same absolute/relative addressing
  // as g
  gen pos2cell(const gen & g,int i,int j,int r,int c,int r2,int c2){
    if (g.is_symb_of_sommet(at_deuxpoints) && g._SYMBptr->feuille.type==_VECT){
      vecteur & gf=*g._SYMBptr->feuille._VECTptr;
      if (gf.size()!=2)
	return gensizeerr(gettext("pos2cell"));
      return symbolic(at_deuxpoints,makevecteur(pos2cell(gf[0],i,j,r,c,r,c),pos2cell(gf[1],i,j,r2,c2,r2,c2)));
    }
    vecteur v;
    if ( (g.type==_SYMB) && (g._SYMBptr->sommet==at_cell))
      v=*g._SYMBptr->feuille._VECTptr;
    else {
      if ( (g.type!=_VECT) || (g._VECTptr->size()!=2) )
	return gensizeerr(gettext("pos2cell"));
      v=*g._VECTptr;
    }
    vecteur w(2);
    if (v.front().type==_INT_)
      w.front()=r;
    else
      w.front()=vecteur(1,r-i);
    if (v.back().type==_INT_)
      w.back()=c;
    else
      w.back()=vecteur(1,c-j);
    return _cell(w,context0);
  }

  gen freecopy(const gen & g){
    if (g.type!=_VECT)
      return g;
    else
      return *g._VECTptr;
  }

  // insert nrows/ncols of fill in m, e.g. fill= [0,0,2] for a spreadsheet
  // or ["","",2] or 0 for a matrix
  matrice matrice_insert(const matrice & m,int insert_row,int insert_col,int nrows,int ncols,const gen & fill_,GIAC_CONTEXT){
    int r,c,cell_r,cell_c;
    int decal_i=0,decal_j;
    mdims(m,r,c);
    gen fill(fill_);
    if (is_undef(fill)){
      if (m[0][0].type==_VECT)
	fill=makevecteur(string2gen("",false),string2gen("",false),2);
      else
	fill=0;
    }
    matrice res;
    res.reserve(r+nrows);
    // i,j position in the old matrix; i+decal_i,i+decal_j in the new
    for (int i=0;i<r;++i){
      vecteur tmp;
      tmp.reserve(c+ncols);
      if (i==insert_row){ // insert nrows of fill
	for (int j=0;j<nrows;++j){
	  // we must recreate the line each time to have a free line
	  for (int k=0;k<c+ncols;++k)
	    tmp.push_back(freecopy(fill));
	  res.push_back(tmp); 
	  tmp.clear();
	}
	decal_i=nrows;
      }
      decal_j=0;
      for (int j=0;j<c;++j){
	if (j==insert_col){
	  for (int k=0;k<ncols;k++)
	    tmp.push_back(freecopy(fill));
	  decal_j=ncols;
	}
	gen g=m[i][j];
	// find all cells in g
	vecteur sub_in(lcell(g[0])),sub_out;
	if (sub_in.empty()){
	  tmp.push_back(g);
	  continue;
	}
	const_iterateur it=sub_in.begin(),itend=sub_in.end();
	for (;it!=itend;++it){
	  int cell_r2,cell_c2,type=cell2pos(*it,i,j,cell_r,cell_c,cell_r2,cell_c2);
	  if (type){
	    if (cell_r>=insert_row)
	      cell_r += nrows;
	    if (cell_c>=insert_col)
	      cell_c += ncols;
	    if (cell_r2>=insert_row)
	      cell_r2 += nrows;
	    if (cell_c2>=insert_col)
	      cell_c2 += ncols;
	    sub_out.push_back(pos2cell(*it,i+decal_i,j+decal_j,cell_r,cell_c,cell_r2,cell_c2));
	  }
	  else
	    sub_out.push_back(*it);
	}
	g=quotesubst(g,sub_in,sub_out,contextptr);
	if (g.type==_VECT && !g._VECTptr->empty())
	  g._VECTptr->front().subtype=m[i][j][0].subtype;
	tmp.push_back(g);
      } // end for j
      res.push_back(tmp);
    } // end for i
    return res;
  }

  // erase nrows/ncols
  matrice matrice_erase(const matrice & m,int insert_row,int insert_col,int nrows,int ncols,GIAC_CONTEXT){
    int r,c,cell_r,cell_c;
    int decal_i=0,decal_j;
    mdims(m,r,c);
    matrice res;
    if ( (r<=nrows) || (c<=ncols) )
      return res;
    res.reserve(r-nrows);
    for (int i=0;i<r;++i){
      if (i==insert_row){
	i+=nrows;
	if (i>=r)
	  break;
	decal_i=nrows;
      }
      vecteur tmp;
      tmp.reserve(c-ncols);
      decal_j=0;
      for (int j=0;j<c;++j){
	if (j==insert_col){
	  j+=ncols;
	  if (j>=c)
	    break;
	  decal_j=ncols;
	}
	gen g=m[i][j];
	// find all cells in g
	vecteur sub_in(lcell(g)),sub_out;
	if (sub_in.empty()){
	  tmp.push_back(g);
	  continue;
	}
	const_iterateur it=sub_in.begin(),itend=sub_in.end();
	for (;it!=itend;++it){
	  int cell_r2,cell_c2,type=cell2pos(*it,i,j,cell_r,cell_c,cell_r2,cell_c2);
	  if (type){
	    if (cell_r>=insert_row)
	      cell_r -= nrows;
	    if (cell_c>=insert_col)
	      cell_c -= ncols;
	    if (cell_r2>=insert_row)
	      cell_r2 -= nrows;
	    if (cell_c2>=insert_col)
	      cell_c2 -= ncols;
	    sub_out.push_back(pos2cell(*it,i-decal_i,j-decal_j,cell_r,cell_c,cell_r2,cell_c2));
	  }
	  else
	    sub_out.push_back(*it);
	}
	tmp.push_back(quotesubst(g,sub_in,sub_out,contextptr));
      } // end for j
      res.push_back(tmp);
    } // end for i
    return res;
  }

  // extract submatrix
  matrice matrice_extract(const matrice & m,int insert_row,int insert_col,int nrows,int ncols){
    if ( (!nrows) || (!ncols))
      return vecteur(1,vecteur(1,gensizeerr(gettext("matrice_extract"))));
    int mr,mc;
    mdims(m,mr,mc);
    if (mr>insert_row+nrows)
      mr=insert_row+nrows;
    if (mc>insert_col+ncols)
      mc=insert_col+ncols;
    matrice res;
    res.reserve(nrows);
    for (int i=insert_row;i<mr;++i){
      const_iterateur it=m[i]._VECTptr->begin();
      res.push_back(vecteur(it+insert_col,it+mc));
    }
    return res;
  }

  // convert m to a spreadsheet matrix if necessary
  // each cell must be a vector of length 3: v[0] is the formula
  // v[1] is the value and v[2] is 0 (not evaluated), 1 (in eval), 2 (evaled)
  void makespreadsheetmatrice(matrice & m,GIAC_CONTEXT){
    int nr=int(m.size());
    if (!nr)
      return ;
    int nc=int(m.front()._VECTptr->size());
    // prepare each cell
    for (int i=0;i<nr;++i){
      gen & g=m[i];
      if (g.type!=_VECT)
	g=vecteur(nc,g);
      vecteur & v=*g._VECTptr;
      for (int j=0;j<nc;++j){
	vecteur w;
	if ((v[j].type==_VECT) && (v[j].subtype==0))
	  w=*v[j]._VECTptr;
	else
	  w=vecteur(2,v[j]);
	int s=int(w.size());
	if (s>3)
	  w=vecteur(w.begin(),w.begin()+3);
	if (s<1)
	  w.push_back(zero);
	if (s<3)
	  w.push_back(zero);
	if (s<2)
	  w.push_back(w.front());
	/* if (w[2].type!=_INT_)
	   w[2]=0; */
	w[0]=spread_convert(w[0],i,j,contextptr);
	v[j]=w;
      }
    }
    return ;
  }

  matrice extractmatricefromsheet(const matrice & m){
    int I=int(m.size());
    if (!I)
      return m;
    int J=int(m.front()._VECTptr->size());
    matrice res(I);
    for (int i=0;i<I;++i){
      vecteur & v=*m[i]._VECTptr;
      vecteur tmp(J);
      for (int j=0;j<J;++j){
	if ( (v[j].type==_VECT) && (v[j]._VECTptr->size()==3) )
	  tmp[j]=(*v[j]._VECTptr)[1];
	else
	  tmp[j]=v[j];
      }
      res[i]=tmp;
    }
    return res;
  }

  static gen evaldeuxpoints(const gen & args,const matrice *mptr,int cr,int cc,int & x,int & y,int & X,int & Y,GIAC_CONTEXT){
    if (args.is_symb_of_sommet(at_deuxpoints))
      return evaldeuxpoints(args._SYMBptr->feuille,mptr,cr,cc,x,y,X,Y,contextptr);
    if (args.type==_VECT && args._VECTptr->size()==2){
      vecteur & w=*args._VECTptr;
      if (!mptr){
	if (w[0].is_symb_of_sommet(at_cell) && w[1].is_symb_of_sommet(at_cell)){
	  if (w[0]._SYMBptr->feuille.type!=_VECT || w[0]._SYMBptr->feuille._VECTptr->size()!=2 || w[1]._SYMBptr->feuille.type!=_VECT || w[1]._SYMBptr->feuille._VECTptr->size()!=2 )
	    return gensizeerr(gettext("Bad cell"));
	  vecteur & w0=*w[0]._SYMBptr->feuille._VECTptr;
	  vecteur & w1=*w[1]._SYMBptr->feuille._VECTptr;
	  // Take absolute types for the returned list
	  int xm,xM,ym,yM;
	  if (w0[0].type==_VECT) 
	    xm=w0[0]._VECTptr->front().val+cr;
	  else 
	    xm=w0[0].val;
	  if (w0[1].type==_VECT) 
	    ym=w0[1]._VECTptr->front().val+cc;
	  else 
	    ym=w0[1].val;
	  // BUG 
	  if (w1[0].type==_VECT) 
	    xM=w1[0]._VECTptr->front().val+cr;
	  else 
	    xM=w1[0].val;
	  if (w1[1].type==_VECT) 
	    yM=w1[1]._VECTptr->front().val+cc;
	  else 
	    yM=w1[1].val;
	  x=giacmin(xm,xM); X=giacmax(xm,xM); y=giacmin(ym,yM); Y=giacmax(ym,yM);
	  return 1;
	}
	return 0;
      } // end if (!mptr)
      int nrows=int(mptr->size());
      if (X>=nrows)
	X=nrows-1;
      int ncols=nrows?int(mptr->front()._VECTptr->size()):0;
      if (Y>=ncols)
	Y=ncols-1;
      ref_vecteur * resptr=new_ref_vecteur(0);
      resptr->v.reserve((X-x+1)*(Y-y+1));
      ref_vecteur * vptr=0;
      for (int x0=x;x0<=X;++x0){
#ifdef SMARTPTR64
	vptr=((ref_vecteur*)(* (ulonglong *) &(*mptr)[x0] >> 16));
#else
	vptr=(*mptr)[x0].__VECTptr;
#endif
	for (int y0=y;y0<=Y;++y0){
	  const gen & tmp=(vptr->v)[y0][1];
	  if (tmp.type!=_STRNG || !tmp._STRNGptr->empty())
	    resptr->v.push_back(tmp);
	}
      }
      return resptr;
    }
    return mptr?symbolic(at_deuxpoints,args):zero;
  }

  // find all spread(i,j) that are in m[m_row][m_col], eval them recursively
  static gen spread_eval(matrice & m,int m_row,int m_col,GIAC_CONTEXT){
    control_c();
    if (interrupted){
      *logptr(contextptr) << gettext("Interrupted ") << m_row << " " << m_col << endl;
      return undef;
    }
    const gen & g=m[m_row][m_col][0];
    if (g.type!=_SYMB && g.type!=_VECT)
      return protecteval(g,eval_level(contextptr),contextptr);
    int & mr =spread_Row(contextptr);
    mr=m_row;
    int & mc=spread_Col(contextptr);
    mc=m_col;
    // printcell_current_row(contextptr)=m_row; printcell_current_col(contextptr)=m_col;
    vecteur v;
    lcell(g,v);
    if (v.empty()){
      gen temp=g;
      if (temp.type==_SYMB && temp.subtype==_SPREAD__SYMB)
	temp.subtype=0;
      return protecteval(temp,eval_level(contextptr),contextptr);
    }
    vecteur sub_in,sub_out;
    const_iterateur it=v.begin(),itend=v.end();
    int i,j,ms=int(m.size()),ws,x,y,X,Y;
    for (;it!=itend;++it){
      if (it->_SYMBptr->sommet==at_deuxpoints){
	if (is_one(evaldeuxpoints(*it,0,m_row,m_col,x,y,X,Y,contextptr))){
	  for (i=x;i<ms && i<=X;++i){
	    vecteur & w=*m[i]._VECTptr;
	    ws=int(w.size());
	    for (j=y;j<ws && j<=Y;++j){
	      vecteur & wj=*w[j]._VECTptr;
	      if (wj.back().val==1)
		return string2gen("Recursive eval",false);
	      if (wj.back().val==0){
		wj.back().val=1;
		wj[1]=spread_eval(m,i,j,contextptr);
		if (interrupted)
		  return undef;
		wj.back().val=2;
	      }
	    }
	  }
	  sub_in.push_back(*it);
	  sub_out.push_back(evaldeuxpoints(*it,&m,m_row,m_col,x,y,X,Y,contextptr));
	}
      } // end at_deuxpoints
      else {
	gen & gi=it->_SYMBptr->feuille._VECTptr->front();
	gen & gj=it->_SYMBptr->feuille._VECTptr->back();
	if (gi.type==_INT_)
	  i=gi.val;
	else
	  i=m_row+gi._VECTptr->front().val;
	if (gj.type==_INT_)
	  j=gj.val;
	else
	  j=m_col+gj._VECTptr->front().val;
	if ( i>=0 && i<ms ){
	  vecteur & w=*m[i]._VECTptr;
	  if ( j>=0 && j<signed(w.size()) ){
	    vecteur & wj=*w[j]._VECTptr;
	    if (wj.back().val==1)
	      return string2gen("Recursive eval",false);
	    if (wj.back().val==0){
	      wj.back().val=1;
	      wj[1]=spread_eval(m,i,j,contextptr);
	      if (interrupted)
		return undef;
	      wj.back().val=2;
	    }
	    sub_in.push_back(*it);
	    sub_out.push_back(wj[1]);
	  }
	}
      } // end at_cell
    }
    // replace evaled cell in g
    // if (sub_in.size()>=1000)
    //  CERR << endl;
    gen temp(quotesubst(g,sub_in,sub_out,contextptr));
    if (temp.type==_SYMB && temp.subtype==_SPREAD__SYMB)
      temp.subtype=0;
    // Avoid answers that are too complex
    if (temp.type==_SYMB && taille(temp,4000)>4000){
      CERR << gettext("Spreadsheet matrix argument max size 4000 exceeded") <<endl;
      temp=undeferr(gettext("Spreadsheet matrix argument max size 4000 exceeded"));
    }
    mr=m_row;
    mc=m_col;
    const gen & res=protecteval(temp,eval_level(contextptr),contextptr);
    return res;
  }

  // evaluate a matrix representing a spreadsheet
  // m must be a spreadsheet matrix (see above)
  // lc will contain the list of cell dependances of m
  void spread_eval(matrice & m,GIAC_CONTEXT){
    interrupted=false;
    int nr=int(m.size());
    if (!nr)
      return;
    int nc=int(m.front()._VECTptr->size());
    // prepare for evaluation, compute list of cell and set eval flag to 0
    for (int i=0;i<nr;++i){
      vecteur & v=*m[i]._VECTptr;
      for (int j=0;j<nc;++j){
	vecteur & w=*v[j]._VECTptr;
	if (w.front().type<=_POLY){
	  w[1]=w[0];
	  w[2].val=2;
	}
	else {
	  w[2].val=0;
	}
      }
    }
    // eval
    for (int i=0;!interrupted && i<nr;++i){
      vecteur & v=*m[i]._VECTptr;
      for (int j=0;!interrupted && j<nc;++j){
	vecteur & w=*v[j]._VECTptr;
	if (w[2].val==2)
	  continue;
	w[2].val=1;
#ifndef NO_STDEXCEPT
	try {
#endif
	  w[1]=spread_eval(m,i,j,contextptr);
#ifndef NO_STDEXCEPT
	}
	catch (std::runtime_error & e){
	  w[1]=string2gen(e.what(),false);
	}
#endif
	w[2].val=2;
      }
    }
    spread_Row(-1,contextptr);
    spread_Col(-1,contextptr);
    if (interrupted)
      *logptr(contextptr) << gettext("Spreadsheet evaluation interrupted") << endl;
  }

  vecteur mergevecteur(const vecteur & a,const vecteur & b){
    if (is_undef(a)) return a;
    if (is_undef(b)) return b;
    int as=int(a.size());
    int bs=int(b.size());
    vecteur v;
    v.reserve(as+bs);
    vecteur::const_iterator it=a.begin(),itend=a.end();
    for (;it!=itend;++it)
      v.push_back(*it);
    it=b.begin();itend=b.end();
    for (;it!=itend;++it)
      v.push_back(*it);
    return v;
  }

  vecteur mergeset(const vecteur & a,const vecteur & b){
    if (is_undef(a)) return a;
    if (is_undef(b)) return b;
    if (a.empty())
      return b;
    vecteur v(a);
    vecteur::const_iterator it=b.begin(),itend=b.end();
    if ( (itend-it)>std::log(double(a.size()))){
      v.reserve(a.size()+(itend-it));
      for (;it!=itend;++it)
	v.push_back(*it);
      islesscomplexthanf_sort(v.begin(),v.end());
      vecteur res(1,v.front());
      res.reserve(v.size());
      it=v.begin()+1,itend=v.end();
      for (;it!=itend;++it){
	if (*it!=res.back())
	  res.push_back(*it);
      }
      return res;
    }
    for (;it!=itend;++it){
      if (!equalposcomp(v,*it))
	v.push_back(*it);
    }
    return v;
  }

  gen makesuite(const gen & a){
    if ( (a.type==_VECT) && (a.subtype==_SEQ__VECT) )
      return a;
    else 
      return gen(vecteur(1,a),_SEQ__VECT);
  }
  
  gen makesuite_inplace(const gen & a,const gen & b){
    if (a.type!=_VECT || a.subtype!=_VECT || (b.type==_VECT && b.subtype==_SEQ__VECT))
      return makesuite(a,b);
    a._VECTptr->push_back(b);
    return a;
  }

  gen makesuite(const gen & a,const gen & b){
    if ( (a.type==_VECT) && (a.subtype==_SEQ__VECT) ){
      if ( (b.type==_VECT) && (b.subtype==_SEQ__VECT) )
	return gen(mergevecteur(*a._VECTptr,*b._VECTptr),_SEQ__VECT);
      else {
	vecteur va=*a._VECTptr;
	va.push_back(b);
	return gen(va,_SEQ__VECT);
      }
    }
    else {
      if ( (b.type==_VECT) && (b.subtype==_SEQ__VECT) ){
	vecteur vb=*b._VECTptr;
	vb.insert(vb.begin(),a);
	return gen(vb,_SEQ__VECT);
      }
      else
	return gen(makevecteur(a,b),_SEQ__VECT);
    }
  }

  // gluing is done line1 of a with line1 of b and so on
  // look at mergevecteur too
  matrice mergematrice(const matrice & a,const matrice & b){
    if (a.empty())
      return b;
    if (b.empty())
      return a;
    const_iterateur ita=a.begin(),itaend=a.end();
    const_iterateur itb=b.begin(),itbend=b.end();
    matrice res;
    res.reserve(itaend-ita);
    if (itaend-ita!=itbend-itb){
      if (debug_infolevel<1)
	return vecteur(1,vecteur(1,gendimerr(gettext("mergematrice"))));
      if (debug_infolevel<1){
	res.dbgprint();
	std_matrix<gen> M;
	matrice2std_matrix_gen(res,M);
	M.dbgprint();
      }
      return vecteur(1,vecteur(1,gendimerr(gettext("mergematrice"))));
    }
    for (;ita!=itaend;++ita,++itb){
      if (ita->type!=_VECT || itb->type!=_VECT)
	return vecteur(1,vecteur(1,gensizeerr(gettext("mergematrice"))));
      res.push_back(mergevecteur(*ita->_VECTptr,*itb->_VECTptr));
    }
    return res;
  }
  
  static complex_double horner(const vector< complex_double > & v, const complex_double & c){
    vector< complex_double > :: const_iterator it=v.begin(),itend=v.end();
    complex_double res(0);
    for (;it!=itend;++it){
      res *= c;
      res += *it;
    }
    // COUT << v << "(" << c << ")" << "=" << res << endl;
    return res;
  }

  // find a root of a polynomial with float coeffs
  gen a_root(const vecteur & v,const complex_double & c0,double eps){
    if (v.empty())
      return gentypeerr(gettext("a_root"));
    vector< complex_double > v_d,dv_d;
    const_iterateur it=v.begin(),itend=v.end();
    int deg=int(itend-it)-1;
    if (deg==0)
      return gensizeerr(gettext("a_root"));
    if (deg==1)
      return -rdiv(v.back(),v.front(),context0);
    if (deg==2){ // use 2nd order equation formula
      return (-v[1]+sqrt(v[1]*v[1]-4*v[0]*v[2],context0))/(2*v[0]); // ok
    }
    v_d.reserve(deg+1);
    dv_d.reserve(deg);
    for (int d=deg;it!=itend;++it,--d){
      gen temp=it->evalf_double(1,context0); // ok
      if (temp.type==_DOUBLE_)
	v_d.push_back(temp._DOUBLE_val);
      else {
	if (temp.type!=_CPLX)
	  return undef;
	v_d.push_back(complex_double(temp._CPLXptr->_DOUBLE_val,(temp._CPLXptr+1)->_DOUBLE_val));
      }
    }
    // Preconditionning, x->x*lambda
    // a_n x^n + .. + a_0 = a_n*lambda^n x^n + a_[n-1]*lambda^(n-1)*x^(n-1) + 
    // = a_n*lambda^n * ( x^n + a_[n-1]/a_n/lambda * x^(n-1) +
    //                    +  a_[n-2]/a_n/lambda^2 * x^(n-1) + ...)
    // take the largest ratio (a_[n-d]/a_n)^(1/d) for lambda
    double ratio=0.0,tmpratio;
    for (int d=1;d<=deg;++d){
      tmpratio=std::pow(abs(v_d[d]/v_d[0]),1.0/d);
      if (tmpratio>ratio)
	ratio=tmpratio;
    }
    double logratio=std::log(ratio);
    if (debug_infolevel)
      CERR << "balance ratio " << ratio << endl;
    bool real0=v_d[0].imag()==0;
    // Recompute coefficients
    for (int d=1;d<=deg;++d){
      bool real=real0 && v_d[d].imag()==0;
      v_d[d]=std::exp(std::log(v_d[d]/v_d[0])-d*logratio);
      if (real)
	v_d[d]=v_d[d].real();
    }
    v_d[0]=1;
    for (int d=0;d<deg;++d)
      dv_d.push_back(v_d[d]*(double)(deg-d)) ;
#ifndef __APPLE__
    if (debug_infolevel>2)
      COUT << "Aroot init " << c0 << " after renormalization: " << v_d << endl << "Diff " << dv_d << endl;
#endif
    // newton method with prefactor
    complex_double c(c0),newc,fc,newfc,fprimec,rapport;    
    double prefact=1.0;
    int maxloop=SOLVER_MAX_ITERATE;
    for (double j=1;j<1024;j=2*j,maxloop=(maxloop*3)/2){ // max 10 loop
      double prefactmult=0.5;
      fc=horner(v_d,c);
      for (int i=maxloop; i;--i){
	fprimec=horner(dv_d,c);
	if (fprimec==complex_double(0,0))
	  break;
	rapport=fc/fprimec;
	if (abs(rapport)>1/eps) // denominator not invertible -> start elsewhere
	  break;
	newc=c-prefact*rapport;
	if (newc==c){
	  if (abs(fc)<eps)
	    return gen(real(newc)*ratio,imag(newc)*ratio);
	  break;
	}
	newfc=horner(v_d,newc);
#ifndef __APPLE__
	if (debug_infolevel>2)
	  CERR << "proot (j=" << j << "i=" << i << "), z'=" << newc << " f(z')=" << newfc << " f(z)=" << fc << " " << prefact << endl;
#endif
	if (abs(rapport)<eps)
	  return gen(real(newc)*ratio,imag(newc)*ratio);
	if (abs(newfc)>abs(fc)){
	  prefact=prefact*prefactmult;
	  // prefactmult = std::max(0.1,prefactmult*prefactmult);
	}
	else { 
	  prefactmult=0.5;
	  c=newc;
	  fc=newfc;
	  if (prefact>0.9)
	    prefact=1;
	  else
	    prefact=prefact*1.1;
	}
      }
      // c=complex_double(rand()*j/RAND_MAX,rand()*j/RAND_MAX);
      c=complex_double(std_rand()*1.0/RAND_MAX,std_rand()*1.0/RAND_MAX);
    }
    CERR << "proot error "+gen(v).print() << endl;
    return c;
  }

  matrice companion(const vecteur & w){
    vecteur v(w);
    if (!is_one(v.front()))
      v=divvecteur(v,v.front());
    int s=int(v.size())-1;
    if (s<=0)
      return vecteur(1,gendimerr());
    matrice m;
    m.reserve(s);
    for (int i=0;i<s;++i){
      vecteur w(s);
      w[s-1]=-v[s-i];
      if (i>0)
	w[i-1]=plus_one;
      m.push_back(w);
    }
    return m;
  }

  bool eigenval2(std_matrix<gen> & H,int n2,gen & l1, gen & l2,GIAC_CONTEXT){
    gen a=H[n2-2][n2-2],b=H[n2-2][n2-1],c=H[n2-1][n2-2],d=H[n2-1][n2-1];
    gen delta=a*a-2*a*d+d*d+4*b*c;
    bool save=complex_mode(contextptr);
    complex_mode(true,contextptr);
    delta=sqrt(delta,contextptr);
    complex_mode(save,contextptr);
    l1=(a+d+delta)/2;
    l2=(a+d-delta)/2;
    return is_zero(im(l1,contextptr)) && is_zero(im(l2,contextptr));
  }

  static void minmax(const vector<double> & lnval,const vector<int> &lndeg,double shift,double & maxval,int & maxdeg,double & minval,int & mindeg){
    maxdeg=0; maxval=0; mindeg=0; minval=0;
    for (unsigned i=0;i<lnval.size();++i){
      double val=lnval[i]-shift*lndeg[i];
      if (val>maxval){
	maxdeg=lndeg[i];
	maxval=val;
      }
      if (val<minval){
	mindeg=lndeg[i];
	minval=val;
      }
    }
  }

  static gen balance(vecteur &v,double & eps,GIAC_CONTEXT){
    // Preconditionning, x->x*lambda
    // a_n x^n + .. + a_0 = a_n*lambda^n x^n + a_[n-1]*lambda^(n-1)*x^(n-1) + 
    // = a_n*lambda^n * ( x^n + a_[n-1]/a_n/lambda * x^(n-1) +
    //                    +  a_[n-2]/a_n/lambda^2 * x^(n-1) + ...)
    double lneps=std::log(eps);
    int nbits=int(-3.3*lneps);
    gen ratio=0,tmpratio;
    int deg=int(v.size())-1;
    gen v0=abs(v[0],contextptr),lnv0=ln(v0,contextptr),lnv0d=evalf_double(accurate_evalf(ln(v0,contextptr),60),1,contextptr);
    if (lnv0d.type!=_DOUBLE_)
      return 1;
    vector<double> lnval; vector<int> lndeg;
    for (int d=1;d<=deg;++d){
      gen vd=abs(v[d],contextptr);
      if (!is_zero(vd)){
	ratio=evalf_double(accurate_evalf(ln(abs(vd,contextptr),contextptr),60)-lnv0,1,contextptr);
	if (ratio.type!=_DOUBLE_)
	  return 1;
	if (is_greater(ratio,d*lneps,contextptr)){
	  lnval.push_back(ratio._DOUBLE_val);
	  lndeg.push_back(d);
	}
	else
	  v[d]=0;
      }
    }
    // search largest/smallest value in lnval
    int maxdeg=0,mindeg=0;
    double maxval=0,minval=0;
    minmax(lnval,lndeg,0,maxval,maxdeg,minval,mindeg);
    // maxval-maxdeg*logratio=minval-mindeg*logratio
    if (mindeg==maxdeg || maxval==minval)
      return 1;
    double a=0,b=(maxval-minval)/(maxdeg-mindeg),c=0,best=0;
    // seach the best value between a and b
    double fa=maxval-minval,fbest=fa;
    int N=100;
    double step=(b-a)/N;
    for (int i=1;i<N;++i){
      c += step;
      minmax(lnval,lndeg,c,maxval,maxdeg,minval,mindeg);
      double fc=maxval-minval;
      if (fc>=fbest)
	break;
      fbest=fc;
      best=c;
    }
    gen bestg=accurate_evalf(gen(best),90);
    // adjust precision (number of bits)
    gen maxv;
    for (unsigned i=0;i<v.size();++i){
      gen tmp=abs(exp(-int(i)*bestg-lnv0,contextptr)*v[i],contextptr);
      if (is_greater(tmp,maxv,contextptr))
	maxv=tmp;
    }
    double eps1=1/(evalf_double(maxv,1,contextptr)._DOUBLE_val);
    if (debug_infolevel)
      CERR << "proot coefficients ratio " << eps1 << endl;
    if (eps1<eps){
      eps=eps1;
      nbits=int(-3.2*std::log(eps));
    }
    if (eps<1e-14)
      bestg=accurate_evalf(bestg,nbits);
    else
      bestg=accurate_evalf(bestg,90);
    // Recompute coefficients
    for (int d=0;d<=deg;++d){
      v[d]=exp(-d*bestg-lnv0,contextptr)*v[d];
    }
    return exp(bestg,contextptr);
  }

  giac_double linfnorm(const vector<giac_double> & v){
    giac_double res=0;
    vector<giac_double>::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      giac_double tmp=std::abs(*it);
      if (tmp>res) res=tmp;
    }
    return res;
  }

  giac_double linfnorm(const matrix_double & v){
    giac_double res=0;
    matrix_double::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      giac_double tmp=linfnorm(*it);
      if (tmp>res) res=tmp;
    }
    return res;
  }

  // linfnorm for diag(d)*M*diag(d)^-1
  giac_double linfnorm(const matrix_double & M,const vector<giac_double> & d){
    giac_double res=0;
    matrix_double::const_iterator it=M.begin(),itend=M.end();
    int i,j;
    for (i=0;it!=itend;++i,++it){
      vector<giac_double>::const_iterator jt=it->begin(),jtend=it->end();
      for (j=0;jt!=jtend;++j,++jt){
	int tmp=int(d[i]*(*jt)/d[j]);
	if (tmp<0) tmp=-tmp;
	if (tmp>res)
	  res=tmp;
      }
    }
    return res;
  }

  bool diagonal_mult(const vector<giac_double> & d,bool invert,const vector<giac_double> & source,vector<giac_double> & target){
    int n=int(d.size());
    if (source.size()!=n) return false;
    target.resize(n);
    if (invert){
      for (int i=0;i<n;++i)
	target[i]=source[i]/d[i];
    }
    else {
      for (int i=0;i<n;++i)
	target[i]=d[i]*source[i];
    }
    return true;
  }

  void rand_1(vector<giac_double> & z){
    int n=int(z.size());
    for (int i=0;i<n;i++){
      z[i]=(rand()<=RAND_MAX/2)?1:-1;
    }
  }

  // Balancing sparse matrices for computing eigenvalues, Tzu-Yi Chen, James W. Demme, Linear Algebra and its Application, 309 (2000) 261–287
  bool balance_krylov(const matrix_double & H,vector<giac_double> & d,int niter,double cutoff){
    int n=int(H.size());
    if (!n || n!=H.front().size())
      return false;
    d=vector<giac_double>(n,1);
    vector<giac_double> z(n,1),z1(n),z2(n),p(n),r(n);
    rand_1(z);
    multmatvecteur(H,z,z1);
    giac_double Hinf=linfnorm(z1);
    matrix_double Htran;
    transpose_double(H,0,n,0,n,Htran);
    for (int j=0;j<niter;++j){
      // z=random vector of +/-1
      rand_1(z);
      // p:=D*H*D^-1*z
      diagonal_mult(d,true,z,z1);
      multmatvecteur(H,z1,z2);
      diagonal_mult(d,false,z2,p);
      // r:=tran(D*H*D^-1)*z=D^-1*tran(H)*D*z
      diagonal_mult(d,false,z,z1);
      multmatvecteur(Htran,z1,z2);
      diagonal_mult(d,true,z2,r);
      for (int i=0;i<n;++i){
	if (std::abs(p[i])>cutoff*Hinf && r[i]!=0)
	  d[i]=d[i]*std::sqrt(std::abs(r[i]/p[i]));
      }
    }
    return true;
  }

  static bool schur_eigenvalues(matrix_double & H1,vecteur & res,double eps,GIAC_CONTEXT){
    int dim=int(H1.size());
    if (debug_infolevel>2){
      if (dim)
	*logptr(contextptr) << "0: " << H1[0][0] << H1[0][1] << endl;
      if (dim>1)
	*logptr(contextptr) << "1: " << H1[1][0] << H1[1][1] << endl;
      for (int i=2;i<dim;++i){
	*logptr(contextptr) << i << ": " << string(i-2,'*') << H1[i][i-2] << "," << H1[i][i-1] << "=" << H1[i][i] << "=";
	if (i<dim-1)
	  *logptr(contextptr) << H1[i][i+1] ;
	*logptr(contextptr) << endl;
      }
    }
    bool ans=true;
    // read eigenvalues on diagonal of H, using subdiagonal for complex pairs
    for (int i=0;i<dim;++i){
      if (i<dim-1 && std::sqrt(eps)>std::abs(H1[i+1][i])){
	if (dim*eps<std::abs(H1[i+1][i]) && (i==0 || dim*eps<std::abs(H1[i][i-1]))){
#ifndef GIAC_HAS_STO_38
	  *logptr(contextptr) << gettext("schur row ") << i+1 << " " << H1[i+1][i] << endl;
#endif
	  ans=false;
	}
	// subdiagonal element is 0 -> diagonal element is an eigenvalue
	res.push_back(double(H1[i][i]));
	continue;
      }
      if (i==dim-1 && std::sqrt(eps)>std::abs(H1[i][i-1])){
	if (dim*eps<std::abs(H1[i][i-1])){
#ifndef GIAC_HAS_STO_38
	  *logptr(contextptr) << gettext("schur row ") << i << " " << H1[i][i-1] << endl;
#endif
	  // ans=false; // 
	}
	// subdiagonal element is 0 -> diagonal element is an eigenvalue
	res.push_back(double(H1[i][i]));
	if (debug_infolevel>2)
	  CERR << "Francis algorithm Success " << res << endl;
	return ans;
      }
      // non-0, next one must be 0
      double test=0;
      if (i<dim-2)
	test=std::abs(H1[i+2][i+1])/(std::abs(H1[i+1][i+1])+std::abs(H1[i][i])+std::abs(H1[i+1][i])+std::abs(H1[i][i+1]));
      if (i<dim-2 && dim*eps<test){
#ifndef GIAC_HAS_STO_38
	*logptr(contextptr) << gettext("schur row ") << i+2 << " " << H1[i+2][i+1] << endl;
#endif
	ans=false;
	if (std::sqrt(eps)<test)
	  continue;
      }
      if (i==dim-1){
	res.push_back(double(H1[i][i]));	
	return true;
      }
      giac_double l1,l2;
      if (eigenval2(H1,i+2,l1,l2)){
	res.push_back(double(l1));
	res.push_back(double(l2));
	// CERR << "2 real " << res << endl;
      }
      else {
	res.push_back(gen(double(l1),double(l2)));
	res.push_back(gen(double(l1),-double(l2)));
	// CERR << "2 cplx " << res << endl;
      }
      ++i;
    }
    return ans;
  }
    
  bool matrice2std_matrix_complex_double(const matrice & m,matrix_complex_double & M,bool nomulti=false){
    int n=int(m.size()),c;
    gen g;
    M.resize(n);
    for (int i=0;i<n;++i){
      const vecteur & mi=*m[i]._VECTptr;
      c=int(mi.size());
      std::vector<complex_double> & v =M[i];
      v.clear();
      v.reserve(c);
      const_iterateur it=mi.begin(),itend=mi.end();
      for (;it!=itend;++it){
	if (nomulti && (it->type==_REAL || (it->type==_CPLX && it->_CPLXptr->type==_REAL)))
	  return false;
	g=evalf_double(*it,1,context0);
	if (g.type==_CPLX && g._CPLXptr->type==_DOUBLE_ && (g._CPLXptr+1)->type==_DOUBLE_){
	  v.push_back(complex_double(g._CPLXptr->_DOUBLE_val,(g._CPLXptr+1)->_DOUBLE_val));
	  continue;
	}
	if (g.type!=_DOUBLE_)
	  return false;
	v.push_back(g._DOUBLE_val);
      }
    }
    return true;
  }

  void std_matrix_gen2matrice_destroy(std_matrix<gen> & M,matrice & m){
    int n=int(M.size());
    m.clear();
    m.reserve(n);
    for (int i=0;i<n;++i){
      m.push_back(new ref_vecteur(0));
      m.back()._VECTptr->swap(M[i]);
    }
  }

  static bool proot_real1(const vecteur & v,double eps,int rprec,vecteur & res,GIAC_CONTEXT){
    if (v.size()<2)
      return false;
    matrice m(companion(v)),md;
    int dim=int(m.size());
    matrice I(midn(dim));
    std_matrix<gen> H,P;
    matrix_double H1,P1;
    matrice2std_matrix_gen(m,H);
    matrice2std_matrix_gen(I,P);
    if (eps>=1e-15 && std_matrix_gen2std_matrix_giac_double(H,H1,true)){
      std_matrix_gen2std_matrix_giac_double(P,P1,true);
      if (lapack_schur(H1,P1,false,res))
	return true;
#if 0
      return balanced_eigenvalues(H1,res,2*SOLVER_MAX_ITERATE,eps,true,contextptr);
#else
      bool ans=francis_schur(H1,0,dim,P1,2*SOLVER_MAX_ITERATE,eps,true,false);
      // CERR << P << endl << H1 << endl;
      return ans && schur_eigenvalues(H1,res,eps,contextptr);
#endif
    }
    matrix_complex_double H2,P2;
    if (matrice2std_matrix_complex_double(m,H2,
					  //false
					   true /* no multi precision */
					  )){
      if (eps<1e-13) eps=1e-13;
      if (debug_infolevel>2)
	H2.dbgprint();
      matrice2std_matrix_complex_double(I,P2);
      bool ans=francis_schur(H2,0,dim,P2,2*SOLVER_MAX_ITERATE,eps,true,false);
      res.clear();
      for (unsigned i=0;i<H2.size();++i){
	if (i+1<H2.size()){
	  double d1=abs(H2[i+1][i]);
	  double d2=dim*eps*(abs(H2[i][i])+abs(H2[i+1][i+1]));
	  if (d1>d2)
	    ans=false;
	}
	complex_double c=H2[i][i];
	// 3e-14 is approx 2^(-45) the number of bits of double in a gen
	if (std::abs(real(c))<3e-14*std::abs(imag(c)))
	  c=complex_double(0,imag(c));
	if (std::abs(imag(c))<3e-14*std::abs(real(c)))
	  res.push_back(real(c));
	else
	  res.push_back(c);
      }
      return ans;
    }
#ifdef HAVE_LIBLAPACK
    // vecteur eigenvals;
    if (eps>=1e-13 && lapack_schur(H,P,false,res,contextptr))
      return true;
#endif
#if 0
    // Here we precompute P in simple precision
    // then start computation with inv(P)*H*P computed with current precision
    // -> disabled since it is slower...
    std_matrix<gen> Hf;
    matrice2std_matrix_gen(m,Hf);
    if (std_matrix_gen2std_matrix_giac_double(Hf,H1,true)){
      std_matrix_gen2std_matrix_giac_double(P,P1,true);
      bool ans=francis_schur(H1,0,dim,P1,2*SOLVER_MAX_ITERATE,1e-13,true,true);
      if (ans){
	std_matrix_giac_double2std_matrix_gen(P1,P);
	matrice p;
	std_matrix_gen2matrice_destroy(P,p);
	p=accurate_evalf(p,int(-3.2*std::log10(eps)));
	matrice pinv=minv(p,contextptr);
	matrice tmp,h;
	mmult(p,m,tmp);
	mmult(tmp,pinv,h);
	matrice2std_matrix_gen(h,H);
	matrice2std_matrix_gen(p,P);	
      }
    }
#endif
    bool complex_schur=false;
    for (unsigned i=0;!complex_schur && i<H.size();++i){
      for (unsigned j=0;j<H[i].size();++j){
	if (H[i][j].type==_CPLX)
	  complex_schur=true;
      }
    }
    if (!francis_schur(H,0,dim,P,2*SOLVER_MAX_ITERATE,dim*eps,false,complex_schur,false,false,contextptr))
      hessenberg_schur(H,P,2*SOLVER_MAX_ITERATE,dim*eps,contextptr);
    if (1){ // FIXME check that H is ok
      eps=dim*dim*eps;
      // read eigenvalues on diagonal of H, using subdiagonal for complex pairs
      for (int i=0;i<dim;++i){
	if (i<dim-1 && is_greater(eps,abs(H[i+1][i],contextptr),contextptr)){
	  // subdiagonal element is 0 -> diagonal element is an eigenvalue
	  res.push_back(H[i][i]);
	  continue;
	}
	if (i==dim-1 && is_greater(eps,abs(H[i][i-1],contextptr),contextptr)){
	  // subdiagonal element is 0 -> diagonal element is an eigenvalue
	  res.push_back(H[i][i]);
	  if (debug_infolevel>2)
	    CERR << "Francis algorithm Success " << res << endl;
	  return true;
	}
	// non-0, next one must be 0
	if (i<dim-2 && !is_greater(eps,abs(H[i+2][i+1],contextptr),contextptr))
	  return false;
	if (i==dim-1)
	  return false;
	gen l1,l2;
	eigenval2(H,i+2,l1,l2,contextptr);
	res.push_back(l1);
	res.push_back(l2);
	++i;
      }
      if (debug_infolevel>2)
	CERR << "Francis algorithm Success " << res << endl;
      return true;
    }
    // old code using GSL
#ifdef HAVE_LIBGSL
    int vsize=v.size();
    int deg2=2*(v.size()-1);
    double *a=new double[vsize];
    for (int j=0;j<vsize;j++){      
      a[vsize-1-j]=evalf_double(v[j],1,contextptr)._DOUBLE_val;
    }
    double *z=new double[deg2];
    gsl_poly_complex_workspace * w = gsl_poly_complex_workspace_alloc (vsize);
    int gsl=gsl_poly_complex_solve (a, vsize, w, z);
    gsl_poly_complex_workspace_free (w);
    if (gsl!=GSL_SUCCESS){
      delete [] a; delete [] z;
      return false;
    }
    for (int j=0;j<deg2;j+=2){
      res.push_back(gen(z[j],z[j+1]));
    }
    delete [] a; delete [] z;
    return true;
#else
    return false;
#endif // HAVE_LIBGSL
  }

  static bool in_proot(const vecteur & w,double & eps,int & rprec,vecteur & res,bool isolaterealroot,GIAC_CONTEXT){
#ifdef EMCC
    if (eps<1e-300)
      eps=1e-11;
    return proot_real1(w,eps,rprec,res,contextptr);
#endif
    // new code using francis_schur
    // if (has_num_coeff(w))
      isolaterealroot=false; // eliminating real roots is not stable enough
    vecteur v(w);
    gen prefact(1);
    double save_eps=eps;
    prefact=balance(v,eps,contextptr);
    // look if setting the barycenter of roots to be 0 is a good idea
    gen shift(-v[1]/v[0]/int(v.size()+1));
    vecteur vt=taylor(v,shift,0);
    gen maxv,maxvt;
    for (unsigned i=0;i<v.size();++i){
      gen tmp;
      tmp=abs(v[i],contextptr);
      if (is_greater(tmp,maxv,contextptr))
	maxv=tmp;
      tmp=abs(vt[i],contextptr);
      if (is_greater(tmp,maxvt,contextptr))
	maxvt=tmp;
    }
    if (is_greater(maxvt,maxv,contextptr))
      shift=0;
    else {
      eps=save_eps;
      double eps1=1/(evalf_double(maxvt,1,contextptr)._DOUBLE_val);
      if (debug_infolevel)
	CERR << "proot after shift: coefficients ratio " << eps1 << endl;
      if (eps1<eps)
	eps=eps1;
      v=vt;
    }
#if 0 // longfloat conversion does not work correctly or vect2GEN in pari.cc
    if (eps<1e-14 && pari_polroots(v,res,14,contextptr)){
      for (unsigned i=0;i<res.size();++i)
	res[i] += shift;
      res=multvecteur(prefact,res);
      return true;
    }
#else
    if (eps<1e-14 && pari_polroots(w,res,rprec,contextptr)){
      return true;
    }
#endif
    if (eps<1e-14 && isolaterealroot){
      // first try to isolate real roots
      gen epsg=pow(plus_two,-int(w.size())-50,contextptr);
      gen rr=complexroot(makesequence(w,epsg),false,contextptr);
      if (rr.type==_VECT && !rr._VECTptr->empty()){
	vecteur rrv=*rr._VECTptr;
	unsigned i=0;
	for (;i<rrv.size();++i){
	  if (rrv[i].type!=_VECT || rr[i]._VECTptr->size()!=2)
	    break;
	  rrv[i]=rrv[i]._VECTptr->front();
	  if (rrv[i].type==_VECT && rrv[i]._VECTptr->size()==2)
	    rrv[i]=(rrv[i][0]+rrv[i][1])/2;
	  if (rrv[i].type==_REAL)
	    rrv[i]=_milieu(rrv[i],contextptr);
	  else
	    rrv[i]=accurate_evalf(rrv[i],int(w.size())+50);
	}
	if (i==rrv.size()){
	  rr=_pcoeff(rrv,contextptr);
	  if (rr.type==_VECT){
	    v=operator_div(w,*rr._VECTptr,0);
	    double epseff=save_eps;
	    if (in_proot(v,epseff,rprec,res,false,contextptr)){
	      res=mergevecteur(rrv,res);
	      return true;
	    }
	  }
	}
      }
    }
    if (debug_infolevel)
      CERR << "proot, setting epsilon = " << eps << " for " << w << endl;
    if (eps<1e-13)
      rprec=int((1-std::log10(eps))*3.2);
    // extract 0 as approx root
    unsigned mult0=0;
    while (!v.empty() && is_zero(v.back())){
      ++mult0;
      v.pop_back();
    }
    bool ans=proot_real1(v,eps,rprec,res,contextptr);
    for (unsigned i=0;i<res.size();++i)
      res[i] += shift;
    for (unsigned i=0;i<mult0;++i)
      res.push_back(shift);
    res=multvecteur(prefact,res);
    return ans;
  }

#if 0
  static bool improve_root(const vecteur & v,gen & r,int nbits,int rprec){
    int vsize=v.size();
    int deg=vsize-1;
    vecteur cur_v(v);
    double ratiod=0.0,tmpratio;
    for (int d=1;d<=deg;++d){
      tmpratio=std::pow(evalf_double(abs(cur_v[d]/cur_v[0]),1,context0)._DOUBLE_val,1.0/d);
      if (tmpratio>ratiod)
	ratiod=tmpratio;
    }
    gen ratio=accurate_evalf(gen(ratiod),nbits);
    if (ratiod>10 || ratiod<0.1){
      gen logratio=log(ratio,context0);
      if (debug_infolevel)
	CERR << ratio << endl;
      // Recompute coefficients
      for (int d=1;d<=deg;++d){
	cur_v[d]=cur_v[d]/cur_v[0]*exp(-d*logratio,context0);
      }
      cur_v[0]=1;
    }
    else
      ratio=1;
    vecteur dcur_v=derivative(cur_v);
    int j=1;
    gen prefact=accurate_evalf(plus_one,nbits);
    gen oldval,newval,newr,dr,fprimer;
    r=r/ratio;
    oldval=horner(cur_v,r);
    for (;j<SOLVER_MAX_ITERATE*vsize;j++){
      if (!(j%vsize)){
	if (is_zero(im(r,context0),context0))
	  r=r*accurate_evalf(gen(1.,1e-2),nbits);
	// random restart
	else
	  r=accurate_evalf(j/vsize*complex_double(std_rand()*1.0/RAND_MAX,std::rand()*1.0/RAND_MAX),nbits);
	oldval=horner(cur_v,r);
	prefact=accurate_evalf(plus_one,nbits);
      }
      fprimer=horner(dcur_v,r);
      dr=oldval/fprimer;
      newr=r-prefact*dr;
      if (is_positive(-rprec-ln(abs(dr)/abs(r),context0)/std::log(2.0),context0)){
	r=ratio*newr;
	return true;
      }
      newval=horner(cur_v,newr);
      if (is_positive(abs(newval,context0)-abs(oldval,context0),context0)){
	prefact=prefact/2;
      }
      else {
	r=newr;
	oldval=newval;
	prefact=prefact*accurate_evalf(gen(1.1),nbits);
	if (is_positive(prefact-1,context0))
	  prefact=accurate_evalf(plus_one,nbits);
      }
    }
    return false;
  }
#endif

  bool is_exact(const vecteur & v){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (!is_exact(*it))
	return false;
    }
    return true;
  }

  bool is_exact(const gen & g){
    switch (g.type){
    case _DOUBLE_: case _REAL: case _FLOAT_: return false;
    case _CPLX:
      return is_exact(*g._CPLXptr) && is_exact(*(g._CPLXptr+1));
    case _VECT:
      return is_exact(*g._VECTptr);
    default:
      return true;
    }
  }

  gen dkw_prod(const vecteur & z,int j){
    gen zj=z[j],prod(1);
    for (int i=0;i<z.size();++i){
      if (i!=j)
	prod=prod*(zj-z[i]);
    }
    return prod;
  }

  // Durand-Kerner-Weierstrass iteration for accurate roots of polynomial v
  // using companion matrix Schur eigenvalues as initial guess in Z
  bool dkw(const vecteur & v,vecteur & racines,int nbits,double eps){
    vecteur z(accurate_evalf(racines,nbits));
    int deg=int(z.size());
    if (deg+1!=v.size())
      return false;
    bool reel=is_zero(im(v,context0));
    for (int i=0;reel && i<deg;++i){
      if (is_zero(im(racines[i],context0)))
	continue;
      if (i<deg-1 && is_zero(im(racines[i]+racines[i+1],context0))){
	++i;
	continue;
      }
      reel=false;
    }
    vecteur w(deg);
    for (int i=0;i<SOLVER_MAX_ITERATE;++i){
      for (int j=0;j<deg;++j){
	w[j]=horner(v,z[j])/dkw_prod(z,j);
	if (reel){
	  if (is_zero(im(z[j],context0)))
	    w[j]=re(w[j],context0);
	  else {
	    if (j<deg-1){
	      w[j+1]=conj(w[j],context0);
	      ++j;
	    }
	  }
	}
      }
      z=z-w;
      gen n=l2norm(w,context0);
      if (is_greater(eps/deg,n,context0)){
	int epsbits=int(std::ceil(-std::log(eps)/std::log(2.0)));
	if (epsbits<48){
	  for (int i=0;i<deg;++i)
	    racines[i]=evalf_double(z[i],1,context0);
	}
	else
	  racines=accurate_evalf(z,epsbits);
	return true;
      }
    }
    return false;
  }

  static vecteur proot(const vecteur & v,double & eps,int & rprec,bool ck_exact){
    int vsize=int(v.size());
    int deg=vsize-1;
    if (vsize<2)
      return vecteur(0);
    if (vsize==2)
      return vecteur(1,rprec<=50?evalf(-v[1]/v[0],1,context0):accurate_evalf(-v[1]/v[0],rprec)); // ok
    if (vsize==3 && !is_exactly_zero(v.back())){
      gen b2=accurate_evalf(-v[1]/2,rprec);
      gen delta=accurate_evalf(b2*b2-v[0]*v[2],rprec); // ok
      gen r1,r2;
      if (is_positive(b2,context0)){
	r1=b2+sqrt(delta,context0);
	r2=r1/v[0];
	r1=v[2]/r1;
      }
      else {
	r2=b2-sqrt(delta,context0);
	r1=r2/v[0];
	r2=v[2]/r2;
      }
      return makevecteur(r1,r2);
    }
    // check for 0
    if (v.back()==0){
      vecteur res=proot(vecteur(v.begin(),v.end()-1),eps,rprec,ck_exact);
      res.push_back(0);
      return res;
    }
    if (vsize%2 && v[1]==0){
      // check for composition with a power of X
      int gcddeg=0;
      for (int vi=2;vi<vsize;++vi){
	if (v[vi]!=0)
	  gcddeg=gcd(gcddeg,vi);
	if (gcddeg==1)
	  break;
      }
      if (gcddeg>1){
	vecteur vd;
	for (int i=0;i<vsize;i+=gcddeg){
	  vd.push_back(v[i]);
	}
	vecteur resd=proot(vd,eps,rprec,ck_exact),res;
	vecteur expj;
	for (int j=0;j<gcddeg;++j){
	  gen tmp=exp(j*cst_two_pi*cst_i/gcddeg,context0);
	  if (rprec<=50)
	    expj.push_back(evalf_double(tmp,1,context0));
	  else
	    expj.push_back(accurate_evalf(tmp,rprec));
	}
	for (int i=0;i<int(resd.size());++i){
	  gen r=pow(resd[i],inv(gcddeg,context0),context0);
	  for (int j=0;j<gcddeg;++j){
	    gen tmp=r*expj[j];
	    res.push_back(tmp);
	  }
	}
	return res;
      }
    }
    // now check if the input is exact if there are multiple roots
    if (ck_exact && is_exact(v)){
#if 1
      vecteur res;
      if (int(v.size())<PROOT_FACTOR_MAXDEG){
	gen g=symb_horner(v,vx_var);
	vecteur vv=factors(g,vx_var,context0);
	for (unsigned i=0;i<vv.size()-1;i+=2){
	  gen vi=vv[i];
	  vi=_e2r(makevecteur(vi,vx_var),context0);
	  if (vi.type==_VECT && vv[i+1].type==_INT_){
#if 1 // ndef HAVE_LIBPARI
	    gen norme=linfnorm(vi,context0);
	    if (norme.type==_ZINT){
	      rprec=giacmax(rprec,mpz_sizeinbase(*norme._ZINTptr,2));
	      eps=std::pow(2.0,-rprec);
	      if (eps==0) eps=1e-300;
	    }
#endif
	    int mult=vv[i+1].val;
	    vecteur current=proot(*vi._VECTptr,eps,rprec,false);
	    for (unsigned j=0;j<current.size();++j){
	      for (int k=0;k<mult;++k){
		res.push_back(current[j]);
	      }
	    }
	  }
	}
	return res;
      }
      polynome V;
      poly12polynome(v,1,V);
      factorization f=sqff(V);
      if (f.size()==1 && f.front().mult==1)
	return proot(accurate_evalf(v,rprec),eps,rprec,false);
      factorization::const_iterator it=f.begin(),itend=f.end();
      for (;it!=itend;++it){
	polynome pcur=it->fact;
	int n=it->mult;
	vecteur vcur;
	polynome2poly1(pcur,1,vcur);
	vecteur vf=accurate_evalf(vcur,rprec);
	vecteur current=proot(vf,eps,rprec,false);
	for (unsigned j=0;j<current.size();++j){
	  for (int k=0;k<n;++k){
	    res.push_back(current[j]);
	  }
	}
      }
      return res;
#else // without multiplicities
      modpoly p=derivative(v),res;
      res=gcd(v,p,0);
      res=operator_div(v,res,0);
      gen tmp=evalf(res,1,context0);
      if (tmp.type!=_VECT || is_undef(tmp))
	return res;
      return proot(*tmp._VECTptr,eps,rprec);
#endif
    }
    else {
      if (!is_numericv(v,1))
	return vecteur(0);
    }
    context ct;
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_lock(&context_list_mutex);
#endif
    context_list().pop_back();
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_unlock(&context_list_mutex);
#endif
    context * contextptr=&ct;
    epsilon(contextptr)=eps;
    bool add_conjugate=is_zero(im(v,contextptr),contextptr); // ok
    vecteur res,crystalball;
    bool cache=proot_cached(v,eps,crystalball);
    // CERR << v << " " << crystalball << endl;
    if (cache)
      return crystalball;
    cache=true;
    // call pari if degree is large
    if (
	0 && v.size()>=64 && 
	pari_polroots(accurate_evalf(v,rprec),crystalball,giacmax(rprec,53),contextptr) && !is_undef(crystalball)){
      proot_cache(v,eps,crystalball);
      return crystalball;
    }
#ifdef HAVE_LIBMPFR
    int nbits = 2*(rprec+vsize);
    vecteur v_accurate(accurate_evalf(v,nbits));
    v_accurate=divvecteur(v_accurate,v_accurate.front());
    // compute roots with companion matrix
    bool precis=true;
    if (crystalball.empty() && !in_proot(v,eps,rprec,crystalball,true,contextptr)){
      // initial guess not precise enough, DKW disabled
      if (0 && int(crystalball.size())==deg && dkw(v_accurate,crystalball,nbits,eps)){
	proot_cache(v,eps,crystalball);
	return crystalball;
      }
      precis=false;
      if (crystalball.size()!=v.size()-1)
	CERR << "Francis algorithm failure for" << v << endl;
      else
	CERR << "Francis algorithm not precise enough for" << v << endl;
    }
    int epsbits=-std::log(eps)/std::log(2.);
    if (precis && int(crystalball.size())==deg 
#ifndef EMCC
	&& dkw(v_accurate,crystalball,nbits,eps)
#endif
	){
      proot_cache(v,eps,crystalball);
      return crystalball;
    }
    if ( (rprec<50 || rprec<epsbits+3) && int(crystalball.size())==deg){
      vecteur dv(derivative(v_accurate));
      vector<short int> done(deg);
      for (int j=0;j<deg;++j){
	if (done[j])
	  continue;
	// find nearest root
	gen cur=crystalball[j],mindist=plus_inf,mindist2=plus_inf;
	vector<double> distances(deg);
	int k2=-1,k3=-1;
	for (int k=0;k<deg;k++){
	  if (k==j) continue;
	  gen curdist=abs(cur-crystalball[k],contextptr);
	  distances[k]=evalf_double(curdist,1,contextptr)._DOUBLE_val;
	  if (is_strictly_greater(mindist,curdist,contextptr)){
	    mindist2=mindist;
	    k3=k2;
	    mindist=curdist;
	    k2=k;
	  }
	}
	gen tmp=accurate_evalf(crystalball[j],nbits);
	gen decal=0;
	for (unsigned k=0;int(k)<SOLVER_MAX_ITERATE;++k){
	  gen num=horner(v_accurate,tmp),den=horner(dv,tmp),ratio=num/den;
	  decal += ratio;
	  gen prec=abs(ratio,contextptr);
	  if (is_greater(eps*deg*10,prec,contextptr)){
	    done[j]=1;
	    tmp -= ratio;
	    num=horner(v_accurate,tmp);
	    den=horner(dv,tmp);
	    ratio=num/den;
	    prec=abs(ratio,contextptr);
	    int precbits=60;
	    if (is_exactly_zero(prec))
	      precbits=2*epsbits;
	    else
	      precbits=_floor(-ln(prec,contextptr)/std::log(2.0),contextptr).val;
	    if (precbits>2*epsbits)
	      precbits=2*epsbits;
	    if (precbits<=48)
	      crystalball[j]=evalf_double(tmp,1,contextptr);
	    else
	      crystalball[j] =accurate_evalf(tmp,precbits);
	    if (debug_infolevel)
	      CERR << "Root " << j << " " << crystalball[j] << endl;
	    break;
	  }
	  if (is_greater(2.5*abs(decal,contextptr),mindist,contextptr)){
	    // if decal is small wrt mindist2 
	    // we have roots that are almost equal 
	    // sort distance, and find a cluster of roots around
	    vector<double> dists(distances);
	    sort(distances.begin(),distances.end());
	    unsigned dd=1; double coeff=2.0;
	    for (;dd<distances.size()-1;++dd){
	      if (distances[dd+1]>=coeff*distances[dd])
		break;
	      coeff *= .9;
	    }
#if 1
	    if (dd<=distances.size()/3){
	      vector<int> positions; vecteur roots;
	      for (unsigned i=0;i<dists.size();++i){
		if (done[i])
		  continue;
		if (dists[i]<=distances[dd]){
		  positions.push_back(i);
		  roots.push_back(accurate_evalf(crystalball[i],nbits));
		  if (i+1<dists.size() && add_conjugate && !is_exactly_zero(im(crystalball[i],contextptr))){
		    positions.push_back(i+1);
		    roots.push_back(accurate_evalf(crystalball[i+1],nbits));
		    ++i;
		  }
		}
	      }
	      if (roots.size()>=10)
		k=SOLVER_MAX_ITERATE;
	      if (debug_infolevel)
		CERR << CLOCK() << "Entering generalized Bairstow " << dd << " roots " << positions << endl;
	      vecteur current=pcoeff(roots),dcurrent;
	      for (;int(k)<SOLVER_MAX_ITERATE;++k){
		modpoly Q,R,dR;
		DivRem(v_accurate,current,0,Q,R);
		// find partial derivatives
		matrice m;
		for (unsigned i=0;i<roots.size();++i){
		  dR=Q % current;
		  if (dR.size()<roots.size())
		    dR=mergevecteur(vecteur(roots.size()-dR.size()),dR);
		  m.push_back(dR);
		  Q.push_back(0); // multiply Q by x for next partial derivative
		}
		// invert jacobian matrix
		reverse(m.begin(),m.end());
		m=mtran(m);
		while (R.size()<m.size())
		  R.insert(R.begin(),accurate_evalf(zero,nbits));
		// solve system
		dcurrent=linsolve(m,R,contextptr);
		vecteur dcurrentv=lidnt(dcurrent);
		if (!dcurrentv.empty()){
		  if (debug_infolevel)
		    CERR << "non invertible jacobian" << endl;
		  break;
		}
		dcurrent.insert(dcurrent.begin(),0);
		// termination test
		gen ck=0;
		for (unsigned i=1;i<dcurrent.size();++i){
		  if (!is_exactly_zero(current[i]))
		    ck+=abs(dcurrent[i]/current[i],contextptr);
		}
		current=addvecteur(current,dcurrent);
		if (is_greater(eps,ck,contextptr))
		  break;
	      }
	      if (int(k)>=SOLVER_MAX_ITERATE){
		CERR << "Unable to isolate roots number "<< positions << endl << accurate_evalf(roots,50) << endl;
		for (unsigned i=0;i<positions.size();++i)
		  done[positions[i]]=-1;
		cache=false;
		break;
	      }
	      else {
		// proot recursive call, and stores roots
		// check if current has a multiple root up to precision eps
		int curdeg=current.size()-1;
		gen multi=-current[1]/(curdeg*current[0]);
		roots=vecteur(curdeg,multi);
		vecteur test=pcoeff(roots);
		test=subvecteur(current,test);
		gen testn=l2norm(test,contextptr);
		if (curdeg>1 && is_greater(eps,testn,contextptr)){
		  int precbits=48;
		  if (testn!=0)
		    precbits=_floor(-ln(testn,contextptr)/std::log(2.0),contextptr).val;
		  multi=accurate_evalf(multi,precbits);
		  for (unsigned i=0;i<positions.size();++i){
		    done[positions[i]]=1;
		    crystalball[positions[i]] =multi;
		  }
		}
		else {
		  double eps1=std::pow(2.0,-nbits);
		  roots=proot(current,eps1,nbits);
		  for (unsigned i=0;i<positions.size();++i){
		    // -> Set precision
		    roots[i]=accurate_evalf(roots[i],nbits);
		    num=horner(v_accurate,roots[i]);
		    den=horner(dv,roots[i]);
		    ratio=num/den;
		    prec=abs(ratio,contextptr);
		    int precbits=60;
		    if (is_exactly_zero(prec))
		      precbits=2*epsbits;
		    else
		      precbits=_floor(-ln(prec,contextptr)/std::log(2.0),contextptr).val;
		    if (precbits>2*epsbits)
		      precbits=2*epsbits;
		    done[positions[i]]=1;
		    if (precbits<=48)
		      crystalball[positions[i]]=evalf_double(roots[i],1,contextptr);
		    else
		      crystalball[positions[i]] =accurate_evalf(roots[i],precbits);
		  }
		}
		break;
	      }
	    }
#else
	    // the second one is crystalball[k2]
	    if (is_greater(mindist2,3*abs(ratio,contextptr),contextptr)){
	      if (debug_infolevel)
		CERR << "Entering Bairstow " << j << " " << k2 << endl;
	      tmp=accurate_evalf(crystalball[j],nbits);
	      if (crystalball[j]==conj(crystalball[j+1],contextptr)) k2=j+1;
	      gen tmp2=accurate_evalf(crystalball[k2],nbits);
	      modpoly current(3,1); current[1]=-tmp-tmp2; current[2]=tmp*tmp2;
	      for (;k<SOLVER_MAX_ITERATE;++k){
		modpoly Q,R,dsR,dpR;
		DivRem(v_accurate,current,0,Q,R);
		dpR=Q % current;
		if (dpR.empty() || is_zero(dpR.back()))
		  break;
		Q.push_back(0);
		dsR=Q % current;
		if (dsR.empty() || is_zero(dsR.back()))
		  break;
		gen A,B,C(dsR.back()),D(dpR.back()),R0,R1;
		if (dpR.size()==2)
		  B=dpR[0];
		if (dsR.size()==2)
		  A=dsR[0];
		gen delta=A*D-B*C;
		if (is_zero(delta))
		  break;
		if (!R.empty()){
		  R1=R.back();
		  if (R.size()==2)
		    R0=R.front();
		}
		gen dc1=(D*R0-B*R1)/delta,dc2=(A*R1-C*R0)/delta;
		current[1] += dc1;
		current[2] += dc2;
		if (is_greater(eps*deg*10,abs(dc1/current[1],contextptr)+abs(dc2/current[2],contextptr),contextptr)){
		  // recompute crystalball[j]/k2 and tmp/tmp2
		  gen s=current[1],p=current[2];
		  delta=s*s-4*p;
		  delta=sqrt(delta,contextptr);
		  if (is_positive(s,contextptr)){
		    tmp=(-s-delta)/2; 
		    tmp2=p/tmp; 
		  }
		  else {
		    tmp2=(-s+delta)/2; 
		    tmp=p/tmp2; 
		  }
		  decal=0;
		  ratio=0;
		  if (eps<1e-14){
		    crystalball[j]=accurate_evalf(tmp,-3.2*std::log(eps));
		    crystalball[k2]=accurate_evalf(tmp2,-3.2*std::log(eps));
		  }
		  else {
		    crystalball[j]=evalf_double(tmp,1,contextptr);
		    crystalball[k2]=evalf_double(tmp2,1,contextptr);
		  }
		  break;
		}
	      }
	    }	    
#endif
	    if (is_greater(3*abs(decal,contextptr),mindist,contextptr)){
	      cache=false;
	      done[j]=false;
	      CERR << "Bad conditionned root j= " << j << " value " << crystalball[j] << " ratio " << evalf_double(abs(ratio,contextptr),1,contextptr) << " mindist " << mindist << endl;
	      break;
	    }
	  }
	  tmp -= ratio;
	}
      }
      if (!cache && pari_polroots(v,crystalball,14,contextptr))
	cache=true;
      if (0 && !cache){ // could be improved via Hensel lifting
	vecteur good;
	for (unsigned i=0;i<crystalball.size();++i){
	  if (done[i]==1)
	    good.push_back(crystalball[i]);
	}
	good=pcoeff(good);
	vecteur rem=operator_div(v,good,0);
	if (rem.size()<=crystalball.size()/2){
	  rem=*_proot(rem,contextptr)._VECTptr;
	  CERR << rem << endl;
	}
      }
      if (cache)
	proot_cache(v,eps,crystalball);
      return crystalball;
    } // if rprec<50 ...

#else // HAVE_LIBMPFR
    int nbits=45;
    rprec = 37;
    vecteur v_accurate(*evalf_double(v,1,contextptr)._VECTptr);
    if (crystalball.empty()){
      in_proot(v,eps,rprec,crystalball,true,contextptr);
      // CERR << crystalball << endl;
      proot_cache(v,eps,crystalball);
    }
    return crystalball;
    // GSL call is much faster but not very accurate
    //if (eps<1e-5)
    //  eps=1e-5;
#endif //HAVE_LIBMPFR
    vecteur dv_accurate(derivative(v_accurate));
    gen r,vr,dr;
    vecteur cur_v(v_accurate),dcur_v(dv_accurate),new_v;
    for (int i=0;;++i,eps*=1.1){
      if (cur_v.size()<2)
	return res;
      // gen scale=linfnorm(cur_v);
      // r=a_root(cur_v,0,scale.evalf_double(1,contextptr)._DOUBLE_val*eps); // ok
      if (!crystalball.empty()){
	r=crystalball.back();
	crystalball.pop_back();
      }
      else
	r=a_root(*evalf_double(cur_v,1,contextptr)._VECTptr,0,eps); // ok
      if (debug_infolevel)
	CERR << "Approx float root " << r << endl;
      if (is_undef(r))
	return res;
      r=accurate_evalf(r,nbits);
      int j=1;
      gen prefact=accurate_evalf(plus_one,nbits);
      gen oldval,newval,newr,fprimer;
      oldval=horner(cur_v,r);
      int vsize2=vsize*(1+nbits/48);
      for (;j<SOLVER_MAX_ITERATE*vsize2;j++){
	if (!(j%vsize2)){
	  if (is_zero(im(r,contextptr),contextptr))
	    r=r*accurate_evalf(gen(1.,1e-2),nbits);
	  // random restart
	  else
	    r=accurate_evalf(j/vsize*complex_double(std::rand()*1.0/RAND_MAX,std::rand()*1.0/RAND_MAX),nbits);
	  oldval=horner(cur_v,r);
	  prefact=accurate_evalf(plus_one,nbits);
	}
	fprimer=horner(dcur_v,r);
	dr=oldval/fprimer;
	newr=r-prefact*dr;
	if (is_zero(dr) || is_positive(-rprec-ln(abs(dr)/abs(r),contextptr)/std::log(2.0),contextptr)){
	  r=newr;
	  break;
	}
	newval=horner(cur_v,newr);
	if (is_strictly_positive(abs(newval,contextptr)-abs(oldval,contextptr),contextptr)){
	  prefact=prefact/2;
	}
	else {
	  r=newr;
	  oldval=newval;
	  prefact=prefact*accurate_evalf(gen(1.1),nbits);
	  if (is_positive(prefact-1,contextptr))
	    prefact=accurate_evalf(plus_one,nbits);
	}
      }
      for (j=0;j<vsize;j++){
	dr=horner(v_accurate,r)/horner(dv_accurate,r);
	r=r-dr;
	if (is_zero(dr) || is_positive(-rprec-ln(abs(dr)/abs(r),contextptr)/std::log(2.0),contextptr))
	  break;
      }
      if (j==vsize)
	return vecteur(1,gensizeerr(gettext("Proot error : no root found for ")+gen(v).print(contextptr)));
      if (debug_infolevel)
	CERR << "Root found " << evalf_double(r,1,contextptr) << endl;
      if (add_conjugate && is_greater(abs(im(r,contextptr),contextptr),eps,contextptr) ){ // ok
	res.push_back(rprec<53?evalf_double(conj(r,contextptr),1,contextptr):conj(accurate_evalf(r,rprec),contextptr)); // ok
	if (!crystalball.empty()){
	  gen rcrystal=crystalball.back();
	  if (is_greater(1e-5,abs(rcrystal-res.back(),contextptr),contextptr))
	    crystalball.pop_back();
	}	
	vr=horner(cur_v,r,0,new_v);
	horner(new_v,conj(r,contextptr),0,cur_v); // ok
	cur_v=*(re(cur_v,contextptr)._VECTptr); // ok
      }
      else {
	if (add_conjugate)
	  r=re(r,contextptr);
	vr=horner(cur_v,r,0,new_v);
	cur_v=new_v;
      }
      res.push_back(rprec<53?evalf_double(r,1,contextptr):accurate_evalf(r,rprec)); // ok
      dcur_v=derivative(cur_v);
    } // end i loop
  }

  vecteur proot(const vecteur & v,double & eps,int & rprec){
    return proot(v,eps,rprec,true);
  }

  vecteur proot(const vecteur & v,double eps){
    int rprec=45;
    return proot(v,eps,rprec);
  }

  vecteur real_proot(const vecteur & v,double eps,GIAC_CONTEXT){
#if 1
    gen r(complexroot(makesequence(v,eps),false,contextptr));
    if (r.type!=_VECT) return vecteur(1,undef);
    const vecteur &w = *r._VECTptr;
    if (is_undef(w)) return w;
    int nbits=int(1-3.2*std::log(eps));
    vecteur res;
    const_iterateur it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      if (it->type==_VECT && it->_VECTptr->size()==2){
	gen tmp=it->_VECTptr->front();
	if (tmp.type==_VECT){
	  tmp=(tmp._VECTptr->front()+tmp._VECTptr->back())/2;
	  if (eps<1e-14)
	    tmp=accurate_evalf(tmp,nbits);
	  else
	    tmp=evalf_double(tmp,1,contextptr);
	}
	res.push_back(tmp);
      }
    }
    return res;
#else
    vecteur w(proot(v,eps));
    if (is_undef(w)) return w;
    vecteur res;
    const_iterateur it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      if (is_real(*it,contextptr))
	res.push_back(*it);
    }
    return res;
#endif
  }

  // eps is defined using the norm of v
  vecteur proot(const vecteur & v){
    double eps=1e-12; 
    // this should take care of precision inside v!
    return proot(v,eps);
  }

  gen _proot(const gen & v,GIAC_CONTEXT){
    if ( v.type==_STRNG && v.subtype==-1) return  v;
    if (v.type!=_VECT)
      return _proot(makesequence(v,ggb_var(v)),contextptr);
    if (v._VECTptr->empty())
      return v;
    vecteur w=*v._VECTptr;
    int digits=decimal_digits(contextptr);
    double eps=epsilon(contextptr);
    if (v.subtype==_SEQ__VECT && w.back().type==_INT_){
      digits=giacmax(w.back().val,14);
      eps=std::pow(0.1,double(digits));
      w.pop_back();
    }
    if (w.size()==1)
      w.push_back(ggb_var(w[0]));
    if (w.size()==2 && w[1].type==_IDNT){
      gen tmp=_e2r(gen(w,_SEQ__VECT),contextptr);
      if (is_undef(tmp)) return tmp;
      if (tmp.type==_FRAC)
	tmp=tmp._FRACptr->num;
      if (tmp.type!=_VECT)
	return vecteur(0);
      w=*tmp._VECTptr;
    }
    for (unsigned i=0;i<w.size();++i){
      gen tmp=evalf(w[i],1,contextptr);
      if (tmp.type>_REAL && tmp.type!=_FLOAT_ && tmp.type!=_CPLX)
	return gensizeerr(contextptr);
    }
    int rprec(int(digits*3.3));
    return _sorta(proot(w,eps,rprec),contextptr);
  }
  gen symb_proot(const gen & e) {
    return symbolic(at_proot,e);
  }
  static const char _proot_s []="proot";
  static define_unary_function_eval (__proot,&giac::_proot,_proot_s);
  define_unary_function_ptr5( at_proot ,alias_at_proot,&__proot,0,true);

  vecteur pcoeff(const vecteur & v){
    vecteur w(1,plus_one),new_w,somme;
    gen a,b;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type==_CPLX && it+1!=itend && is_zero(*it-conj(*(it+1),context0))){
	a=re(*it,context0);
	b=im(*it,context0);
	b=a*a+b*b;
	a=-2*a;
	w=w*makevecteur(1,a,b);
	++it;
	continue;
      }
      new_w=w;
      new_w.push_back(zero); // new_w=w*x
      mulmodpoly(w,-(*it),w); // w = -w*root
      addmodpoly(new_w,w,somme);
      w=somme;
    }
    return w;
  }
  gen _pcoeff(const gen & v,GIAC_CONTEXT){
    if ( v.type==_STRNG && v.subtype==-1) return  v;
    if (v.type!=_VECT)
      return symb_pcoeff(v);
    return gen(pcoeff(*v._VECTptr),_POLY1__VECT);
  }
  gen symb_pcoeff(const gen & e) {
    return symbolic(at_pcoeff,e);
  }
  static const char _pcoeff_s []="pcoeff";
  static define_unary_function_eval (__pcoeff,&giac::_pcoeff,_pcoeff_s);
  define_unary_function_ptr5( at_pcoeff ,alias_at_pcoeff,&__pcoeff,0,true);

  gen _peval(const gen & e,GIAC_CONTEXT){
    if ( e.type==_STRNG && e.subtype==-1) return  e;
    if (e.type!=_VECT)
      return gentypeerr(contextptr);
    vecteur & args=*e._VECTptr;
    if ( (args.size()==2) && (args.front().type==_VECT) )
      return horner(*(args.front()._VECTptr),args.back());
    if ( (args.size()!=3) || (args[1].type!=_VECT) || (args[2].type!=_VECT) )
      return gentypeerr(contextptr);
    gen pol(args.front());
    vecteur vars(*args[1]._VECTptr);
    vecteur vals(*args[2]._VECTptr);
    if (vars.size()!=vals.size())
      return gendimerr(contextptr);
    for (int i=0;i<signed(vars.size());++i){
      if (vars[i].type!=_IDNT)
	return gensizeerr(contextptr);
    }
    // convert to internal form: 
    // now put vars at the beginning of the list of variables
    vecteur lv(vars);
    lvar(e,lv);
    vecteur lv1(lv.begin()+vars.size(),lv.end());
    pol=sym2r(pol,lv,contextptr);
    gen polnum,polden;
    fxnd(pol,polnum,polden);
    for (int i=0;i<signed(vals.size());++i){
      if (debug_infolevel)
	CERR << "// Peval conversion of var " << i << " " << CLOCK() << endl;
      vals[i]=e2r(vals[i],lv1,contextptr);
    }
    if (debug_infolevel)
      CERR << "// Peval conversion to internal form completed " << CLOCK() << endl;
    if (polnum.type==_POLY)
      polnum=peval(*polnum._POLYptr,vals,0);
    if (polden.type==_POLY)
      polden=peval(*polden._POLYptr,vals,0);
    pol=rdiv(polnum,polden,contextptr);
    return r2sym(pol,lv1,contextptr);
  }
  gen symb_peval(const gen & arg1,const gen & arg2) {
    return symbolic(at_peval,makesequence(arg1,arg2));
  }
  static const char _peval_s []="peval";
  static define_unary_function_eval (__peval,&giac::_peval,_peval_s);
  define_unary_function_ptr5( at_peval ,alias_at_peval,&__peval,0,true);
  
  int vrows(const vecteur & a){
    return int(a.size());
  }

  // addvecteur is different from addmodpoly if a and b have != sizes
  // because it always start adding at the beginning of a and b
  void addvecteur(const vecteur & a,const vecteur & b,vecteur & res){
    if (&b==&res && &b!=&a){
      addvecteur(b,a,res);
      return ;
    }
    vecteur::const_iterator itb=b.begin(), itbend=b.end();
    if (&a==&res){ // in-place addition
      vecteur::iterator ita=res.begin(), itaend=res.end();
      for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
	*ita=*ita+*itb;
      }
      for (;itb!=itbend;++itb)
	res.push_back(*itb);
      return;
    }
    vecteur::const_iterator ita=a.begin(), itaend=a.end();
    res.clear();
    res.reserve(giacmax(int(itbend-itb),int(itaend-ita)));
    for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
      res.push_back(*ita+*itb);
    }
    for (;ita!=itaend;++ita)
      res.push_back(*ita);
    for (;itb!=itbend;++itb)
      res.push_back(*itb);
  }

  // subvecteur is different from submodpoly if a and b have != sizes
  // because it always start substr. at the beginning of a and b
  void subvecteur(const vecteur & a,const vecteur & b,vecteur & res){
    if (&b==&res){
      vecteur::const_iterator ita=a.begin(), itaend=a.end();
      vecteur::iterator itb=res.begin(), itbend=res.end();
      for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
	*itb=*ita-*itb;
      }
      for (;ita!=itaend;++ita)
	res.push_back(*ita);
      return;
    }
    vecteur::const_iterator itb=b.begin(), itbend=b.end();
    if (&a==&res){ // in-place substract
      vecteur::iterator ita=res.begin(), itaend=res.end();
      for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
	operator_minus_eq(*ita,*itb,context0);
      }
      for (;itb!=itbend;++itb)
	res.push_back(-*itb);
      return;
    }
    vecteur::const_iterator ita=a.begin(), itaend=a.end();
    res.clear();
    res.reserve(giacmax(int(itbend-itb),int(itaend-ita)));
    for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
      res.push_back(*ita-*itb);
    }
    for (;ita!=itaend;++ita)
      res.push_back(*ita);
    for (;itb!=itbend;++itb)
      res.push_back(-*itb);
  }

  vecteur addvecteur(const vecteur & a,const vecteur & b){
    vecteur res;
    addvecteur(a,b,res);
    return res;
  }

  vecteur subvecteur(const vecteur & a,const vecteur & b){
    vecteur res;
    subvecteur(a,b,res);
    return res;
  }

  vecteur negvecteur(const vecteur & v){
    vecteur w;
    negmodpoly(v,w);
    return w;
  }

  gen dotvecteur(const vecteur & a,const vecteur & b){
    vecteur::const_iterator ita=a.begin(), itaend=a.end();
    vecteur::const_iterator itb=b.begin(), itbend=b.end();
    if (ita==itaend || itb==itbend) return 0;
    gen res,tmp;
    //if (ita->type==_VECT && itb->type==_VECT && dotvecteur_interp(a,b,res)) return res;
    //if (0 && itaend-ita>10 && itbend-itb>10 && ita->type==_POLY && itb->type==_POLY && dotvecteur_interp(a,b,res)) return res;
    for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
      type_operator_times((*ita),(*itb),tmp);
      res += tmp;
    }
    return res;
  }

  gen dotvecteur(const gen & g1,const gen & g2){
    gen a=remove_at_pnt(g1);
    gen b=remove_at_pnt(g2);
    if (a.type!=_VECT || b.type!=_VECT)
      return gensizeerr(gettext("dotvector"));
    if (a.subtype==_VECTOR__VECT)
      return dotvecteur(vector2vecteur(*a._VECTptr),b);
    if (b.subtype==_VECTOR__VECT)
      return dotvecteur(a,vector2vecteur(*b._VECTptr));
    return dotvecteur(*a._VECTptr,*b._VECTptr);
  }

  void multvecteur(const gen & a,const vecteur & b,vecteur & res){
    if (b.empty()){
      res.clear();
      return;
    }
    if (b.front().type==_VECT && ckmatrix(b)){
      vecteur temp;
      if (&b==&res){
	iterateur it=res.begin(),itend=res.end();
	for (;it!=itend;++it){
	  if (it->type==_VECT)
	    multvecteur(a,*it->_VECTptr,*it->_VECTptr);
	  else
	    *it = a*(*it);
	}
	return;
      }
      const_iterateur it=b.begin(),itend=b.end();
      res.clear();
      res.reserve(itend-it);
      for (;it!=itend;++it){
	if (it->type==_VECT){
	  multvecteur(a,*it->_VECTptr,temp);
	  res.push_back(temp);
	}
	else
	  res.push_back(a*(*it));
      }
      return;
    }
    if (is_zero(a,context0)){
      if (&b==&res){
	iterateur it=res.begin(),itend=res.end();
	for (;it!=itend;++it)
	  *it=(*it)*zero;
      }
      else {
	const_iterateur it=b.begin(),itend=b.end();
	res.clear();
	res.reserve(itend-it);
	for (;it!=itend;++it)
	  res.push_back((*it)*zero);
      }
    }
    else {
      mulmodpoly(b,a,0,res);
    }
  }

  vecteur multvecteur(const gen & a,const vecteur & b){
    vecteur res;
    multvecteur(a,b,res);
    return res;
  }

  void divvecteur(const vecteur & b,const gen & a,vecteur & res){
    if (b.empty()){
      res.clear();
      return;
    }
    if (&b==&res){
      if (is_one(a))
	return;
      iterateur it=res.begin(),itend=res.end();
      mpz_t tmpz;
      mpz_init(tmpz);
      for (;it!=itend;++it){
	if (it->type==_VECT){
	  vecteur temp;
	  divvecteur(*it->_VECTptr,a,*it->_VECTptr);
	}
	else {
#ifndef USE_GMP_REPLACEMENTS
	  if (it->type==_ZINT && a.type==_ZINT && it->ref_count()==1){
	    my_mpz_gcd(tmpz,*it->_ZINTptr,*a._ZINTptr);
	    if (mpz_cmp_ui(tmpz,1)==0)
	      *it=fraction(*it,a);
	    else {
	      mpz_divexact(*it->_ZINTptr,*it->_ZINTptr,tmpz);
	      ref_mpz_t * den=new ref_mpz_t;
	      mpz_divexact(den->z,*a._ZINTptr,tmpz);
	      *it = fraction(*it,den);
	    }
	  }
	  else
#endif
	    *it=rdiv(*it,a,context0);
	}
      }
      mpz_clear(tmpz);
      return;
    }
    if (b.front().type==_VECT && ckmatrix(b)){
      const_iterateur it=b.begin(),itend=b.end();
      res.clear();
      res.reserve(itend-it);
      for (;it!=itend;++it){
	if (it->type==_VECT){
	  vecteur temp;
	  divvecteur(*it->_VECTptr,a,temp);
	  res.push_back(temp);
	}
	else
	  res.push_back(rdiv(*it,a,context0));
      }
      return;
    }
    divmodpoly(b,a,res);
  }

  vecteur divvecteur(const vecteur & b,const gen & a){
    vecteur res;
    divvecteur(b,a,res);
    return res;
  }

  void multmatvecteur(const matrice & a,const vecteur & b,vecteur & res){
    vecteur::const_iterator ita=a.begin(), itaend=a.end();
    res.clear();
    res.reserve(itaend-ita);
    for (;ita!=itaend;++ita)
      res.push_back(dotvecteur(*(ita->_VECTptr),b));
  }

  vecteur multmatvecteur(const matrice & a,const vecteur & b){
    vecteur res;
    multmatvecteur(a,b,res);
    return res;
  }

  void multvecteurmat(const vecteur & a,const matrice & b,vecteur & res){
    matrice btran;
    mtran(b,btran);
    multmatvecteur(btran,a,res);
  }

  vecteur multvecteurmat(const vecteur & a,const matrice & b){
    vecteur res;
    multvecteurmat(a,b,res);
    return res;
  }

  gen ckmultmatvecteur(const vecteur & a,const vecteur & b){
    if (ckmatrix(a)){
      if (ckmatrix(b)){
	matrice res;
	if (!mmultck(a,b,res))
	  return gendimerr("");
	return _simplifier(res,context0);
      }
      // matrice * vecteur
      vecteur res;
      if (a.front()._VECTptr->size()!=b.size())
	return gendimerr(gettext("dotvecteur"));
      multmatvecteur(a,b,res);
      return _simplifier(res,context0);
    }
    if (ckmatrix(b)){
      vecteur res;
      multvecteurmat(a,b,res);
      return _simplifier(res,context0);
    }
    if (xcas_mode(context0)==3)
      return apply(a,b,prod);
    return dotvecteur(a,b);
  }

  // *********************
  // ***   Matrices    ***
  // *********************

  bool ckmatrix(const matrice & a,bool allow_embedded_vect){
    vecteur::const_iterator it=a.begin(),itend=a.end();
    if (itend==it)
      return false;
    int s=-1;
    int cur_s;
    for (;it!=itend;++it){
      if (it->type!=_VECT)
	return false;
      cur_s=int(it->_VECTptr->size());
      if (!cur_s)
	return false;
      if (s<0)
	s = cur_s;
      else {
	if (s!=cur_s)
	  return false;
	if (s && (it->_VECTptr->front().type==_VECT && it->_VECTptr->front().subtype!=_POLY1__VECT) && !allow_embedded_vect)
	  return false;
      }
    }
    return true;
  }

  bool ckmatrix(const matrice & a){
    return ckmatrix(a,false);
  }

  bool ckmatrix(const gen & a,bool allow_embedded_vect){
    if (a.type!=_VECT)
      return false;
    return ckmatrix(*a._VECTptr,allow_embedded_vect);
  }

  bool ckmatrix(const gen & a){
    return ckmatrix(a,false);
  }

  bool is_squarematrix(const matrice & a){
    if (!ckmatrix(a))
      return false;
    return a.size()==a.front()._VECTptr->size();
  }

  bool is_squarematrix(const gen & a){
    if (!ckmatrix(a))
      return false;
    return a._VECTptr->size()==a._VECTptr->front()._VECTptr->size();
  }

  bool is_fully_numeric(const vecteur & v, int withfracint){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (!is_fully_numeric(*it, withfracint))
	return false;
    }
    return true;
  }

  bool is_fully_numeric(const gen & a, int withfracint){
    switch (a.type){
    case _DOUBLE_: case _FLOAT_:
      return true;
    case _REAL:
      return true;
    case _CPLX:
      return is_fully_numeric(*a._CPLXptr, withfracint) && is_fully_numeric(*(a._CPLXptr+1), withfracint);
    case _VECT:
      return is_fully_numeric(*a._VECTptr, withfracint);
    case _IDNT:
      return strcmp(a._IDNTptr->id_name,"pi")==0;
    case _INT_:
    case _ZINT:
      return withfracint & num_mask_withint;
    case _FRAC:
      return (withfracint & num_mask_withfrac) && is_fully_numeric(a._FRACptr->num,withfracint) && is_fully_numeric(a._FRACptr->den,withfracint);
    default:
      return false;
    }
  }

  int mrows(const matrice & a){
    return int(a.size());
  }

  int mcols(const matrice & a){
    return int(a.begin()->_VECTptr->size());
  }

  void mdims(const matrice &m,int & r,int & c){
    r=int(m.size());
    c=0;
    if (r){
      const gen & g=m.front();
      if (g.type==_VECT)
	c=int(g._VECTptr->size());
    }
  }

  void mtran(const matrice & a,matrice & res,int ncolres){
    if (!ckmatrix(a,true)){
      res=vecteur(1,vecteur(ncolres,gensizeerr("Unable to tranpose")));
      return;
    }
    vecteur::const_iterator it=a.begin(),itend=a.end();
    int n=int(itend-it); // nrows of a = ncols of res if ncolres was 0
    res.clear();
    if (!n)
      return;
    if (!ncolres)
      ncolres=n;
    int c=int(it->_VECTptr->size()); // ncols of a = rows of res
    res.reserve(c);
    // find begin of each row
#if 1 // def VISUALC
    vecteur::const_iterator * itr=new vecteur::const_iterator[ncolres];
#else
    vecteur::const_iterator itr[ncolres];
#endif
    vecteur::const_iterator * itrend= itr+ncolres;
    vecteur::const_iterator * itrcur;
    int i;
    for (i=0;(i<n) && (it!=itend);++it,++i)
      itr[i]=it->_VECTptr->begin();
    for (;(i<ncolres) ;++i)
#if 1 // def VISUALC
      * (int *) &itr[i]=0;
#else
      itr[i]=(vecteur::const_iterator) NULL;
#endif
    // make current row of res with currents elements of itr[]
    for (int j=0;j<c;++j){
      gen cr=new_ref_vecteur(0);
      vecteur & cur_row=*cr._VECTptr;
      cur_row.clear();
      cur_row.reserve(ncolres);
      for (itrcur=itr;itrcur!=itrend;++itrcur){
	if
#if 1 // def VISUALC
	  (* (int *)itrcur!=0)
#else
	  (*itrcur!=(vecteur::const_iterator)NULL)
#endif
	    {
	      cur_row.push_back(**itrcur);
	      ++(*itrcur);
	    }
	else
	  cur_row.push_back(0);
      }
      res.push_back(cr);
    }
#if 1 // def VISUALC
    delete [] itr;
#endif
  }

#ifndef GIAC_HAS_STO_38
  #define GIAC_DETBLOCK // current bloc implementation for det is slower

  void negate_int(vector< vector<int> > & Nblock){
    int imax=Nblock.size();
    for (int i=0;i<imax;++i){
      vector<int>::iterator it=Nblock[i].begin(),itend=Nblock[i].end();
      for (;it!=itend;++it){
	*it=-*it;
      }
    }
  }

  void tran_int(const vector< vector<int> > & a,vector< vector<int> > & res,int r1=0,int r2=0,int c1=0,int c2=0){
    vector< vector<int> >::const_iterator it=a.begin()+r1,itend=r2>r1?it+(r2-r1):a.end();
    int ncolres=itend-it; // nrows of a = ncols of res 
    if (!ncolres){
      res.clear();
      return;
    }
    int c=c2>c1?c2-c1:it->size(); // ncols of a = rows of res
    res.resize(c);
    // find begin of each row
#if defined( VISUALC ) || defined( BESTA_OS ) || defined(EMCC) || defined(__clang__)
    vector<int>::const_iterator * itr=(vector<int>::const_iterator *)alloca(ncolres*sizeof(vector<int>::const_iterator));
#else
    vector<int>::const_iterator itr[ncolres];
#endif
    vector<int>::const_iterator * itrend= itr+ncolres;
    vector<int>::const_iterator * itrcur;
    int i;
    for (i=0;it!=itend;++it,++i)
      itr[i]=it->begin()+c1;
    // make current row of res with currents elements of itr[]
    for (int j=0;j<c;++j){
      vector<int> & cur_row = res[j]; 
      cur_row.clear();
      cur_row.reserve(ncolres);
      for (itrcur=itr;itrcur!=itrend;++itrcur){
	cur_row.push_back(**itrcur);
	++(*itrcur);
      }
    }
  }
#endif

  matrice mtran(const matrice & a){
    matrice res;
    mtran(a,res);
    return res;
  }

  gen _tran(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type==_MAP){
      gen_map res;
      gen g(res);
      sparse_trn(*a._MAPptr,*g._MAPptr,false,contextptr);
      return g;
    }
    vecteur v;
    if (!ckmatrix(a)){
      if (a.type==_VECT && !a._VECTptr->empty())
	v=vecteur(1,a);
      else
	return symb_tran(a);
    }
    else
      v=*a._VECTptr;
    matrice res;
    mtran(v,res);
    return gen(res,_MATRIX__VECT);
  }
  static const char _tran_s []="tran";
  static define_unary_function_eval (__tran,&giac::_tran,_tran_s);
  define_unary_function_ptr5( at_tran ,alias_at_tran,&__tran,0,true);
  
  bool matrice2std_matrix_double(const matrice & m,matrix_double & M,bool nomulti=false){
    if (debug_infolevel)
      CERR << CLOCK() << " converting to double" << endl;
    int n=int(m.size()),c;
    gen g;
    M.resize(n);
    for (int i=0;i<n;++i){
      const vecteur & mi=*m[i]._VECTptr;
      c=int(mi.size());
      std::vector<giac_double> & v =M[i];
      v.clear();
      v.reserve(c);
      const_iterateur it=mi.begin(),itend=mi.end();
      for (;it!=itend;++it){
	if (it->type==_DOUBLE_){
	  v.push_back(it->_DOUBLE_val);
	  continue;
	}
	if (nomulti && it->type==_REAL)
	  return false;
	g=evalf(*it,1,context0);
	if (g.type==_FLOAT_){
	  v.push_back(get_double(g._FLOAT_val));
	  continue;
	}
	if (g.type!=_DOUBLE_)
	  return false;
	v.push_back(g._DOUBLE_val);
      }
    }
    return true;
  }

  giac_double dotvecteur(const std::vector<giac_double> & a,const std::vector<giac_double> & b){
    std::vector<giac_double>::const_iterator ita=a.begin(), itaend=a.end();
    std::vector<giac_double>::const_iterator itb=b.begin(), itbend=b.end();
    giac_double res=0;
    for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
      res += (*ita)*(*itb);
    }
    return res;
  }

  giac_double dotvecteur_double(const std::vector<giac_double> & a,const std::vector<giac_double> & b){
    return dotvecteur(a,b);
  }

  // H*w->v, assumes correct sizes (v already initialized)
  void multmatvecteur(const matrix_double & H,const std::vector<giac_double> & w,vector<giac_double> & v){
    unsigned n=unsigned(H.size());
    for (unsigned j=0;j<n;++j){
      vector<giac_double>::const_iterator it=H[j].begin(),itend=H[j].end(),jt=w.begin();
      giac_double res=0.0;
      for (;it!=itend;++jt,++it)
	res += (*it)*(*jt);
      v[j]=res;
    }
  }

  complex_double dotvecteur(const std::vector<complex_double> & a,const std::vector<complex_double> & b){
    std::vector<complex_double>::const_iterator ita=a.begin(), itaend=a.end();
    std::vector<complex_double>::const_iterator itb=b.begin(), itbend=b.end();
    complex_double res=0;
    for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
      res += (*ita)*(*itb);
    }
    return res;
  }

  void smod_inplace(matrice & res,const gen & pi_p){
#ifndef USE_GMP_REPLACEMENTS
    if (pi_p.type==_ZINT && ckmatrix(res)){
      mpz_t tmpz;
      mpz_init(tmpz);
      for (unsigned i=0;i<res.size();++i){
	iterateur it=res[i]._VECTptr->begin(),itend=res[i]._VECTptr->end();
	for (;it!=itend;++it){
	  if (it->type!=_ZINT) // already smod-ed!
	    continue;
	  if (it->ref_count()!=1)
	    *it=smod(*it,pi_p);
	  if (mpz_cmp_ui(*it->_ZINTptr,0)==1){
	    mpz_sub(tmpz,*it->_ZINTptr,*pi_p._ZINTptr);
	    mpz_neg(tmpz,tmpz);
	    if (mpz_cmp(*it->_ZINTptr,tmpz)>0){
	      mpz_neg(tmpz,tmpz);
	      mpz_swap(tmpz,*it->_ZINTptr);
	    }
	  }
	  else {
	    mpz_add(tmpz,*it->_ZINTptr,*pi_p._ZINTptr);
	    mpz_neg(tmpz,tmpz);
	    if (mpz_cmp(*it->_ZINTptr,tmpz)<0){
	      mpz_neg(tmpz,tmpz);
	      mpz_swap(tmpz,*it->_ZINTptr);
	    }
	  }
	}
      }
      mpz_clear(tmpz);
    }
    else
#endif // USE_GMP_REPLACEMENTS
      res=smod(res,pi_p);
  }

  void uncoerce(gen & g,unsigned prealloc) ;
  void uncoerce(vecteur & v,unsigned prealloc){
    iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      uncoerce(*it,prealloc);
  }

  void uncoerce(gen & g,unsigned prealloc) {
    if (g.type==_INT_){
      int tmp =g.val;
#ifdef SMARTPTR64
      * ((ulonglong * ) &g) = ulonglong(new ref_mpz_t(prealloc)) << 16;
#else
      g.__ZINTptr = new ref_mpz_t(prealloc);
#endif
      g.type=_ZINT;
      mpz_set_si(*g._ZINTptr,tmp); 
    }
    else {
      if (g.type==_VECT)
	uncoerce(*g._VECTptr,prealloc);
    }
  }
  
#if 1 // ndef GIAC_HAS_STO_38
  const int mmult_double_blocksize=45; // 2*45^2*sizeof(double)= a little less than 32K
  int mmult_int_blocksize=60; // 2*60^2*sizeof(int)= a little less than 32K
  gen _blockmatrix_mult_size(const gen & args,GIAC_CONTEXT){
    if (args.type==_VECT && args._VECTptr->empty())
      return mmult_int_blocksize;
    if (args.type!=_INT_ || args.val<1)
      return gensizeerr(contextptr);
    return mmult_int_blocksize=args.val;
  }
  static const char _blockmatrix_mult_size_s []="blockmatrix_mult_size";
  static define_unary_function_eval (__blockmatrix_mult_size,&_blockmatrix_mult_size,_blockmatrix_mult_size_s);
  define_unary_function_ptr5( at_blockmatrix_mult_size ,alias_at_blockmatrix_mult_size,&__blockmatrix_mult_size,0,true);


  // multiply a[a0..a1,i0+delta..i1+delta] with bloc btran[b0..b1,i0..i1] 
  // and adds or subtracts to c[a0+c0..a1+c0,b0+c1..b1+c1]  
  void mmult_double_block(const matrix_double & A,int a0,int a1,const matrix_double & Btran,int b0,int b1,matrix_double & C,int c0,int c1,int i0,int i1,int delta=0,bool add=true){
    for (int a=a0;a<a1;++a){
      const vector<giac_double> & Aa=A[a];
      vector<giac_double> & Ca=C[a+c0];
      matrix_double::const_iterator it=Btran.begin()+b0,itend=Btran.begin()+b1-5;
      vector<giac_double>::iterator jt=Ca.begin()+b0+c1;
      for (;it<=itend;){
	giac_double t0=0.0,t1=0.0,t2=0.0,t3=0.0,t4=0.0;
	const giac_double * i=&Aa[i0+delta], *iend=i+(i1-i0);
	const giac_double *j0=&(*it)[i0];++it; 
	const giac_double *j1=&(*it)[i0];++it;
	const giac_double *j2=&(*it)[i0];++it;
	const giac_double *j3=&(*it)[i0];++it;
	const giac_double *j4=&(*it)[i0];++it;
#if 1	
	for (;i<iend-4;j0+=5,j1+=5,j2+=5,j3+=5,j4+=5,i+=5){
	  giac_double u = *i;
	  t0 += u*(*j0);
	  t1 += u*(*j1);
	  t2 += u*(*j2);
	  t3 += u*(*j3);
	  t4 += u*(*j4);
	  u = i[1];
	  t0 += u*(j0[1]);
	  t1 += u*(j1[1]);
	  t2 += u*(j2[1]);
	  t3 += u*(j3[1]);
	  t4 += u*(j4[1]);
	  u = i[2];
	  t0 += u*(j0[2]);
	  t1 += u*(j1[2]);
	  t2 += u*(j2[2]);
	  t3 += u*(j3[2]);
	  t4 += u*(j4[2]);
	  u = i[3];
	  t0 += u*(j0[3]);
	  t1 += u*(j1[3]);
	  t2 += u*(j2[3]);
	  t3 += u*(j3[3]);
	  t4 += u*(j4[3]);
	  u = i[4];
	  t0 += u*(j0[4]);
	  t1 += u*(j1[4]);
	  t2 += u*(j2[4]);
	  t3 += u*(j3[4]);
	  t4 += u*(j4[4]);
	}
#endif
	for (;i<iend;++j0,++j1,++j2,++j3,++j4,++i){
	  giac_double u = *i;
	  t0 += u*(*j0);
	  t1 += u*(*j1);
	  t2 += u*(*j2);
	  t3 += u*(*j3);
	  t4 += u*(*j4);
	}
	if (add){
	  *jt+=t0; ++jt;
	  *jt+=t1; ++jt;
	  *jt+=t2; ++jt;
	  *jt+=t3; ++jt;
	  *jt+=t4; ++jt;
	}
	else {
	  *jt-=t0; ++jt;
	  *jt-=t1; ++jt;
	  *jt-=t2; ++jt;
	  *jt-=t3; ++jt;
	  *jt-=t4; ++jt;
	}
      }
      itend +=5;
      for (;it<itend;++it){
	giac_double t=0.0;
	const giac_double * i=&Aa[i0+delta], *iend=i+(i1-i0), *j=&(*it)[i0];
	for (;i<iend;++j,++i)
	  t += (*i)*(*j);
	if (add){
	  *jt+=t; 
	}
	else {
	  *jt-=t;
	}
	++jt;
      }
    }
  }

  // multiply bloc a[a0..a1,i0+delta..i1+delta] with bloc b[b0..b1,i0..i1] 
  // and adds or subtracts to c[a0+c0..a1+c0,b0+c1..b1+c1]
  // computation is done modulo p (if p==0 no reduction)
  // assumes that a and b are reduced mod p and (i1-i0+1)*p^2 < 2^63
  static void mmult_mod_block(const vector< vector<int> > & A,int a0,int a1,const vector< vector<int> > & Btran,int b0,int b1,vector< vector<int> > & C,int c0,int c1,int i0,int i1,int p,int delta=0,bool add=true){
    for (int a=a0;a<a1;++a){
      const vector<int> & Aa=A[a];
      vector<int> & Ca=C[a+c0];
      vector< vector<int> >::const_iterator it=Btran.begin()+b0,itend=Btran.begin()+b1-6;
      vector<int>::iterator jt=Ca.begin()+b0+c1;
      for (;it<=itend;){
	longlong t0=0,t1=0,t2=0,t3=0,t4=0,t5=0;
	const int * i=&Aa[i0+delta], *iend=i+(i1-i0);
	const int *j0=&(*it)[i0];++it; 
	const int *j1=&(*it)[i0];++it;
	const int *j2=&(*it)[i0];++it;
	const int *j3=&(*it)[i0];++it;
	const int *j4=&(*it)[i0];++it;
	const int *j5=&(*it)[i0];++it;
	for (;i<iend-5;j0+=6,j1+=6,j2+=6,j3+=6,j4+=6,j5+=6,i+=6){
	  longlong u = *i;
	  t0 += u*(*j0);
	  t1 += u*(*j1);
	  t2 += u*(*j2);
	  t3 += u*(*j3);
	  t4 += u*(*j4);
	  t5 += u*(*j5);
	  u = i[1];
	  t0 += u*(j0[1]);
	  t1 += u*(j1[1]);
	  t2 += u*(j2[1]);
	  t3 += u*(j3[1]);
	  t4 += u*(j4[1]);
	  t5 += u*(j5[1]);
	  u = i[2];
	  t0 += u*(j0[2]);
	  t1 += u*(j1[2]);
	  t2 += u*(j2[2]);
	  t3 += u*(j3[2]);
	  t4 += u*(j4[2]);
	  t5 += u*(j5[2]);
	  u = i[3];
	  t0 += u*(j0[3]);
	  t1 += u*(j1[3]);
	  t2 += u*(j2[3]);
	  t3 += u*(j3[3]);
	  t4 += u*(j4[3]);
	  t5 += u*(j5[3]);
	  u = i[4];
	  t0 += u*(j0[4]);
	  t1 += u*(j1[4]);
	  t2 += u*(j2[4]);
	  t3 += u*(j3[4]);
	  t4 += u*(j4[4]);
	  t5 += u*(j5[4]);
	  u = i[5];
	  t0 += u*(j0[5]);
	  t1 += u*(j1[5]);
	  t2 += u*(j2[5]);
	  t3 += u*(j3[5]);
	  t4 += u*(j4[5]);
	  t5 += u*(j5[5]);
	}
	for (;i<iend;++j0,++j1,++j2,++j3,++j4,++j5,++i){
	  longlong u = *i;
	  t0 += u*(*j0);
	  t1 += u*(*j1);
	  t2 += u*(*j2);
	  t3 += u*(*j3);
	  t4 += u*(*j4);
	  t5 += u*(*j5);
	}
	if (add){
	  if (p){
	    *jt = (*jt+t0)%p; ++jt;
	    *jt = (*jt+t1)%p; ++jt;
	    *jt = (*jt+t2)%p; ++jt;
	    *jt = (*jt+t3)%p; ++jt;
	    *jt = (*jt+t4)%p; ++jt;
	    *jt = (*jt+t5)%p; ++jt;
	  }
	  else {
	    *jt+=t0; ++jt;
	    *jt+=t1; ++jt;
	    *jt+=t2; ++jt;
	    *jt+=t3; ++jt;
	    *jt+=t4; ++jt;
	    *jt+=t5; ++jt;
	  }
	}
	else {
	  if (p){
	    *jt = (*jt-t0)%p; ++jt;
	    *jt = (*jt-t1)%p; ++jt;
	    *jt = (*jt-t2)%p; ++jt;
	    *jt = (*jt-t3)%p; ++jt;
	    *jt = (*jt-t4)%p; ++jt;
	    *jt = (*jt-t5)%p; ++jt;
	  }
	  else {
	    *jt-=t0; ++jt;
	    *jt-=t1; ++jt;
	    *jt-=t2; ++jt;
	    *jt-=t3; ++jt;
	    *jt-=t4; ++jt;
	    *jt-=t5; ++jt;
	  }
	}
      }
      itend +=6;
      for (;it<itend;++it){
	longlong t=0;
	const int * i=&Aa[i0+delta], *iend=i+(i1-i0), *j=&(*it)[i0];
	for (;i<iend;++j,++i)
	  t += longlong(*i)*(*j);
	if (add){
	  if (p)
	    *jt = (*jt+t)%p; 
	  else
	    *jt += t;
	}
	else {
	  if (p)
	    *jt = (*jt-t)%p; 
	  else
	    *jt -= t;
	}
	++jt;
      }
    }
  }


  // matrix multiplication mod p: C[c0..,c1..] += A[Ar0..Ar1,Ac0..Ac1]*B
  // B is represented by Btran, take B[Br0..Br1,Bc0] if Br1>Br0
  // or -= if add=false
  void in_mmult_mod(const vector< vector<int> > & A,const vector< vector<int> > & Btran,vector< vector<int> > & C,int c0,int c1,int p,int Ar0,int Ar1,int Ac0,int Ac1,bool add,int Br0=0,int Br1=0,int Bc0=0){	
    int resrows=Ar1>Ar0?Ar1-Ar0:A.size(),rescols=Btran.size();
    if (Br1>Br0)
      rescols=Br1-Br0;
    else
      Br0=0;
    int n=Ac1>Ac0?Ac1-Ac0:A.front().size();
    for (int i=0;i<n;i+=mmult_int_blocksize){
      int iend=i+mmult_int_blocksize;
      if (iend>n)
	iend=n;
      for (int k=0;k<resrows;k+=mmult_int_blocksize){
	int kend=k+mmult_int_blocksize;
	if (kend>resrows)
	  kend=resrows;
	for (int j=0;j<rescols;j+=mmult_int_blocksize){
	  int jend=j+mmult_int_blocksize;
	  if (jend>rescols)
	    jend=rescols;
	  mmult_mod_block(A,k+Ar0,kend+Ar0,Btran,Br0+j,Br0+jend,C,c0-Ar0,c1-Br0,Bc0+i,Bc0+iend,p,Ac0-Bc0,add);
	}
      }
    }
  }

  struct thread_mmult_double_t {
    const matrix_double *a,*btran;
    matrix_double *c;
    int k,kend,n,rescols,Ar0,Br0,Ac0,Bc0,c0,c1;
    bool add;
  };

  void * do_thread_mmult_double(void * ptr_){
    thread_mmult_double_t * ptr=(thread_mmult_double_t *) ptr_;
    const matrix_double & a=*ptr->a;
    const matrix_double & btran=*ptr->btran;
    matrix_double & c=*ptr->c;
    int kstart=ptr->k,resrows=ptr->kend,n=ptr->n,rescols=ptr->rescols;
    int Ar0=ptr->Ar0,Br0=ptr->Br0,Ac0=ptr->Ac0,Bc0=ptr->Bc0,c0=ptr->c0,c1=ptr->c1;
    if (kstart>=resrows)
      return ptr;
    for (int k=kstart;k<resrows;k+=mmult_double_blocksize){
      int kend=k+mmult_double_blocksize;
      if (kend>resrows)
	kend=resrows;
      for (int i=0;i<n;i+=mmult_double_blocksize){
	int iend=i+mmult_double_blocksize;
	if (iend>n)
	  iend=n;
	for (int j=0;j<rescols;j+=mmult_double_blocksize){
	  int jend=j+mmult_double_blocksize;
	  if (jend>rescols)
	    jend=rescols;
	  mmult_double_block(a,k+Ar0,kend+Ar0,btran,Br0+j,Br0+jend,c,c0-Ar0,c1-Br0,Bc0+i,Bc0+iend,Ac0-Bc0,ptr->add);
	}
      }
    }
    return ptr;
  }

  // C +-= A[Ar0..Ar1-1,Ac0..Ac1-1]*B[Br0..Br1,Bc0], where B is given by Btran 
  void in_mmult_double(const matrix_double & A,const matrix_double & Btran,matrix_double & C,int c0,int c1,int Ar0,int Ar1,int Ac0,int Ac1,bool add,int Br0=0,int Br1=0,int Bc0=0){	
    int resrows=Ar1>Ar0?Ar1-Ar0:A.size(),rescols=Btran.size();
    if (Br1>Br0)
      rescols=Br1-Br0;
    else
      Br0=0;
    int n=Ac1>Ac0?Ac1-Ac0:A.front().size();
#ifdef HAVE_LIBPTHREAD
    int nthreads=threads_allowed?threads:1;
    if (nthreads>1){
      pthread_t tab[nthreads];
      thread_mmult_double_t multdparam[nthreads];
      for (int j=0;j<nthreads;++j){
	thread_mmult_double_t tmp={&A,&Btran,&C,0,0,n,rescols,Ar0,Br0,Ac0,Bc0,c0,c1,add};
	multdparam[j]=tmp;
      }
      int kstep=int(std::ceil(resrows/double(nthreads))),k=0;
      for (int j=0;j<nthreads;++j){
	multdparam[j].k=k;
	k += kstep;
	if (k>resrows)
	  k=resrows;
	multdparam[j].kend=k;
	bool res=true;
	if (j<nthreads-1)
	  res=pthread_create(&tab[j],(pthread_attr_t *) NULL,do_thread_mmult_double,(void *) &multdparam[j]);
	if (res)
	  do_thread_mmult_double((void *)&multdparam[j]);
      }
      for (int j=0;j<nthreads;++j){
	void * ptr=(void *)&nthreads; // non-zero initialisation
	if (j<nthreads-1)
	  pthread_join(tab[j],&ptr);
      }
      return ;
    } // end nthreads
#endif // PTHREAD

    for (int i=0;i<n;i+=mmult_double_blocksize){
      int iend=i+mmult_double_blocksize;
      if (iend>n)
	iend=n;
      for (int k=0;k<resrows;k+=mmult_double_blocksize){
	int kend=k+mmult_double_blocksize;
	if (kend>resrows)
	  kend=resrows;
	for (int j=0;j<rescols;j+=mmult_double_blocksize){
	  int jend=j+mmult_double_blocksize;
	  if (jend>rescols)
	    jend=rescols;
	  mmult_double_block(A,k+Ar0,kend+Ar0,Btran,Br0+j,Br0+jend,C,c0-Ar0,c1-Br0,Bc0+i,Bc0+iend,Ac0-Bc0,add);
	}
      }
    }
  }

  int linfnorm(const vector<int> & v){
    int n=0,cur;
    vector<int>::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      cur = *it;
      if (cur>=-n && cur<=n)
	continue;
      if (cur<0)
	n=-cur;
      else
	n=cur;
    }
    return n;
  }

  int linfnorm(const vector< vector<int> > & A){
    int n=0,a=A.size();
    for (int i=0;i<a;++i){
      n=giacmax(n,linfnorm(A[i]));
    }
    return n;
  }

  // matrix multiplication mod p: C = A*B
  // B is given by Btransposed, Ac1-Ac0 or ncols(A) should be = to nrows(B)=ncols(Btran)
  void mmult_mod(const vector< vector<int> > & A,const vector< vector<int> > & Btran,vector< vector<int> > & C,int p,int Ar0,int Ar1,int Ac0,int Ac1,int Brbeg,int Brend,int Bcbeg,int Crbeg,int Ccbeg,bool add){
    int resrows,rescols;
    resrows=Ar1>Ar0?Ar1-Ar0:A.size();
    rescols=Brend>Brbeg?Brend-Brbeg:Btran.size();
    int Acols=Ac1>Ac0?Ac1-Ac0:(A.empty()?0:A.front().size());
    // we must resize otherwise mmult_mod calls in smallmodrref do not adjust matrix sizes correctly
    if (!add){
      // if (C.size()<resrows+Crbeg)
      C.resize(resrows+Crbeg);
      for (int i=0;i<resrows;++i){
	// if (C[Crbeg+i].size()<Ccbeg+rescols)
	C[Crbeg+i].resize(Ccbeg+rescols);
	fill(C[Crbeg+i].begin()+Ccbeg,C[Crbeg+i].begin()+Ccbeg+rescols,0);
      }
    }
    // before enabling strassen_mod for p=0, check A and Btran inf norms!
    if (
	//0 && 
	resrows>strassen_limit && rescols >strassen_limit && Acols>strassen_limit && Crbeg==0 && Ccbeg==0){
      if (p!=0){
	strassen_mod(false,true,A,Btran,C,p,Ar0,Ar1,Ac0,Ac1,Brbeg,Brend,Bcbeg);
	return;
      }
      int ainf=linfnorm(A), binf=linfnorm(Btran);
      double nstep=std::ceil(std::log(giacmin(resrows,rescols)/double(strassen_limit))/std::log(2.0));
      if (ainf*nstep*binf*nstep<RAND_MAX){
	strassen_mod(false,true,A,Btran,C,p,Ar0,Ar1,Ac0,Ac1,Brbeg,Brend,Bcbeg);
	return;
      }
    }
    in_mmult_mod(A,Btran,C,Crbeg,Ccbeg,p,Ar0,Ar1,Ac0,Ac1,true,Brbeg,Brend,Bcbeg);
  }

  // Improve if &B==&C && defaults param
  void add_mod(bool add,const vector< vector<int> > & A,const vector< vector<int> > & B,vector< vector<int> > & C,int p,int Ar0=0,int Ar1=0,int Ac0=0,int Ac1=0,int Br0=0,int Bc0=0,int Cr0=0,int Cc0=0){
    if (Ar1<=Ar0) Ar1=A.size()+Ar0;
    if (!A.empty() && Ac1<=Ac0) Ac1=A.front().size()+Ac0;
    vector< vector<int> >::const_iterator at=A.begin()+Ar0,atend=A.begin()+Ar1,bt=B.begin()+Br0;
    if (&B!=&C && int(C.size())<Cr0+Ar1-Ar0)
      C.resize(Cr0+Ar1-Ar0);
    vector< vector<int> >::iterator ct=C.begin()+Cr0;
    for (;at!=atend;++ct,++bt,++at){
      const vector<int> & ai=*at;
      const vector<int> & bi=*bt;
      vector<int> & ci=*ct;
      if (&B!=&C && int(ci.size())<Cc0+Ac1-Ac0)
	ci.resize(Cc0+Ac1-Ac0);
      vector<int>::const_iterator it=ai.begin()+Ac0,itend=ai.begin()+Ac1,jt=bi.begin()+Bc0;
      vector<int>::iterator kt=ci.begin()+Cc0;
      if (p){
	if (!add && &B==&C){
	  for (;it!=itend;++kt,++it)
	    *kt =(*kt+longlong(*it))%p;
	  continue;
	}
	if (add){
	  for (;it!=itend;++kt,++jt,++it)
	    *kt =(*kt+longlong(*it)+*jt)%p;
	}
	else {
	  for (;it!=itend;++kt,++jt,++it)
	    *kt=(*it+*jt)%p;
	}
      }
      else {
	if (!add && &B==&C){
	  for (;it!=itend;++kt,++it)
	    *kt += *it;
	  continue;
	}
	if (add){
	  for (;it!=itend;++kt,++jt,++it)
	    *kt += *it+*jt;
	}
	else {
	  for (;it!=itend;++kt,++jt,++it)
	    *kt=(*it+*jt);
	}
      }
    }
  }


  void sub_mod(const vector< vector<int> > & A,const vector< vector<int> > & B,vector< vector<int> > & C,int p,int Ar0=0,int Ar1=0,int Ac0=0,int Ac1=0,int Br0=0,int Bc0=0,int Cr0=0,int Cc0=0){
    if (Ar1<=Ar0) Ar1=A.size()+Ar0;
    if (!A.empty() && Ac1<=Ac0) Ac1=A.front().size()+Ac0;
    vector< vector<int> >::const_iterator at=A.begin()+Ar0,atend=A.begin()+Ar1,bt=B.begin()+Br0;
    if (int(C.size())<Cr0+Ar1-Ar0)
      C.resize(Cr0+Ar1-Ar0);
    vector< vector<int> >::iterator ct=C.begin()+Cr0;
    for (;at!=atend;++ct,++bt,++at){
      const vector<int> & ai=*at;
      const vector<int> & bi=*bt;
      vector<int> & ci=*ct;
      if (int(ci.size())<Cc0+Ac1-Ac0)
	ci.resize(Cc0+Ac1-Ac0);
      vector<int>::const_iterator it=ai.begin()+Ac0,itend=ai.begin()+Ac1,jt=bi.begin()+Bc0;
      vector<int>::iterator kt=ci.begin()+Cc0;
      if (p){
	for (;it!=itend;++kt,++jt,++it)
	  *kt=(*it-*jt)%p;
      }
      else {
	for (;it!=itend;++kt,++jt,++it)
	  *kt=(*it-*jt);
      }
    }
  }

  void mod(vector<int> & A,int p){
    unsigned a=A.size();
    for (unsigned i=0;i<a;++i)
      A[i] %= p;
  }

  void mod (vector< vector<int> > & A,int p){
    unsigned a=A.size();
    for (unsigned i=0;i<a;++i)
      mod(A[i], p);
  }

  // Strassen multiplication, work in progress
  // find A*B, B is given by Btran (transposed)
  // ncols(A)=nrows(B)=ncols(Btran)
  // answer size: nrows(A),ncols(B)=nrows(B)
  // reduce should be set to false by default, true means we can do + without reduction
  int strassen_limit=180; 
  gen _strassen_limit(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=evalf_double(g0,1,contextptr);
    if (g.type!=_DOUBLE_)
      return strassen_limit;
    return strassen_limit=int(g._DOUBLE_val);
  }
  static const char _strassen_limit_s []="strassen_limit";
  static define_unary_function_eval (__strassen_limit,&_strassen_limit,_strassen_limit_s);
  define_unary_function_ptr5( at_strassen_limit ,alias_at_strassen_limit,&__strassen_limit,0,true);

  gen _lapack_limit(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=evalf_double(g0,1,contextptr);
    if (g.type!=_DOUBLE_)
      return CALL_LAPACK;
    return CALL_LAPACK=int(g._DOUBLE_val);
  }
  static const char _lapack_limit_s []="lapack_limit";
  static define_unary_function_eval (__lapack_limit,&_lapack_limit,_lapack_limit_s);
  define_unary_function_ptr5( at_lapack_limit ,alias_at_lapack_limit,&__lapack_limit,0,true);

  static void alloc(vector<vector<int> > &v,int ac,int r){
    for (unsigned i=0;i<v.size();++i){
      v[i].reserve(r);
      v[i].resize(ac);
    }
  }

  // skip_reduce has not been tested yet. Needs to change inversion algorithm
  // and use half block instead of block of size 60
  void strassen_mod(bool skip_reduce,bool add,const vector< vector<int> > & A,const vector< vector<int> > & Btran,vector< vector<int> > & C,int p,int arbeg,int arend,int acbeg,int acend,int brbeg,int brend,int bcbeg){
    if (A.empty() || Btran.empty())
      return;
    int a,ac,b;
    if (arend>arbeg){
      a=arend-arbeg;
    }
    else {
      arend=a=A.size();
    }
    if (acend>acbeg){
      ac=acend-acbeg;
    }
    else {
      acend=ac=A.front().size();
    }
    if (brend>brbeg){
      b=brend-brbeg;
    }
    else {
      brend=b=Btran.size();
    }
    // ac should be equal to number of lines of B=bc
    if (a<=strassen_limit || ac<=strassen_limit ||
	b<=strassen_limit){
      if (p && skip_reduce){
	if (arbeg==0 && arend==int(A.size())){
	  vector< vector<int> > A_(A);
	  mod(A_,p);
	  if (brbeg==0 && brend==int(Btran.size())){
	    vector< vector<int> > Btran_(Btran);
	    mod(Btran_,p);
	    mmult_mod(A_,Btran_,C,p,arbeg,arend,acbeg,acend,brbeg,brend,bcbeg);
	  }
	  else 
	    mmult_mod(A_,Btran,C,p,arbeg,arend,acbeg,acend,brbeg,brend,bcbeg);
	}
	else {
	  if (brbeg==0 && brend==int(Btran.size())){
	    vector< vector<int> > Btran_(Btran);
	    mod(Btran_,p);
	    mmult_mod(A,Btran_,C,p,arbeg,arend,acbeg,acend,brbeg,brend,bcbeg);
	  }
	  else 
	    mmult_mod(A,Btran,C,p,arbeg,arend,acbeg,acend,brbeg,brend,bcbeg);
	}
      }
      else
	mmult_mod(A,Btran,C,p,arbeg,arend,acbeg,acend,brbeg,brend,bcbeg);
      return;
    }
    if (debug_infolevel>2)
      CERR << CLOCK() << "Strassen begin " << a << "," << ac << "," << b << endl;
    // if all +/- in recursion fit in an int, 
    // s and t computations can be done mod 0, provided we reduce mod p just above
    if (p && !skip_reduce){
      int n1=giacmin(a,giacmin(ac,b))/strassen_limit;
      if (2*(sizeinbase2(n1)-1)+sizeinbase2(p)<32) 
	skip_reduce=true;
    }
    if (ac%2 || a%2 || b%2){ // add missing 0 to get even dimensions
      vector< vector<int> > A_(a+1),Btran_(b+1);
      int ac_=ac;
      if (ac%2)
	++ac_;
      for (int i=0;i<a;++i){
	A_[i]=vector<int>(A[arbeg+i].begin()+acbeg,A[arbeg+i].begin()+acbeg+ac);
	if (ac%2)
	  A_[i].push_back(0);
      }
      for (int i=0;i<b;++i){
	Btran_[i]=vector<int>(Btran[brbeg+i].begin()+bcbeg,Btran[brbeg+i].begin()+bcbeg+ac);
	if (ac%2)
	  Btran_[i].push_back(0);
      }
      if (a%2==0)
	A_.pop_back();
      else
	A_[a]=vector<int>(ac);
      if (b%2==0)
	Btran_.pop_back();
      else
	Btran_[b]=vector<int>(ac);
      strassen_mod(skip_reduce,add,A_,Btran_,C,p);
      if (a%2)
	C.pop_back();
      if (b%2){
	for (int i=0;i<a;++i)
	  C[i].pop_back();
      }
      return;
    }
    if (int(C.size())!=a)
      C.resize(a);
    for (unsigned i=0;i<C.size();++i){
      if (int(C[i].size())!=b)
	C[i].resize(b);
    }
    a/=2; ac/=2; b/=2;
    // s1=a21+a22
    int acb=giacmax(ac,b);
    vector< vector<int> > s1(a); alloc(s1,ac,acb); 
    // vector< vector<int> > s1(a,vector<int>(ac));
    add_mod(false,A,A,s1,skip_reduce?0:p,arbeg+a,arbeg+2*a,acbeg,acbeg+ac,arbeg+a,acbeg+ac);
    // s2=s1-a11
    vector< vector<int> > s2(a); alloc(s2,ac,acb); 
    // vector< vector<int> > s2(a,vector<int>(ac));
    sub_mod(s1,A,s2,skip_reduce?0:p,0,a,0,ac,arbeg,acbeg);
    // s3=a11-a21
    vector< vector<int> > s3(a); alloc(s3,ac,acb); 
    // vector< vector<int> > s3(a,vector<int>(ac));
    sub_mod(A,A,s3,skip_reduce?0:p,arbeg,arbeg+a,acbeg,acbeg+ac,arbeg+a,acbeg);
    // s4=a12-s2
    vector< vector<int> > s4(a); alloc(s4,ac,acb); 
    // vector< vector<int> > s4(a,vector<int>(ac));
    sub_mod(A,s2,s4,skip_reduce?0:p,arbeg,arbeg+a,acbeg+ac,acbeg+2*ac,0,0);
    // t1=b12-b11=btran21-btran11
    vector< vector<int> > t1; t1.reserve(giacmax(a,b)); t1.resize(b); alloc(t1,ac,acb);
    // vector< vector<int> > t1(b,vector<int>(ac));
    sub_mod(Btran,Btran,t1,skip_reduce?0:p,brbeg+b,brbeg+2*b,bcbeg,bcbeg+ac,brbeg,bcbeg);
    // t2=b22-t1=btran22-t1
    vector< vector<int> > t2(b,vector<int>(ac));
    sub_mod(Btran,t1,t2,skip_reduce?0:p,brbeg+b,brbeg+2*b,bcbeg+ac,bcbeg+2*ac,0,0);
    // t3=b22-b12=btran22-btran21
    vector< vector<int> > t3(b,vector<int>(ac));
    sub_mod(Btran,Btran,t3,skip_reduce?0:p,brbeg+b,brbeg+2*b,bcbeg+ac,bcbeg+2*ac,brbeg+b,bcbeg);
    // t4=b21-t2=btran12-t2
    vector< vector<int> > t4(b,vector<int>(ac));
    sub_mod(Btran,t2,t4,skip_reduce?0:p,brbeg,brbeg+b,bcbeg+ac,bcbeg+2*ac,0,0);
    if (debug_infolevel>2)
      CERR << CLOCK() << "Strassen recurse " << a << "," << ac << "," << b << endl;
    // p3=s1*t1
    vector< vector<int> > p3(a,vector<int>(b));
    strassen_mod(skip_reduce,false,s1,t1,p3,p);
    // p4=s2*t2
    alloc(s1,b,b); vector< vector<int> > & p4=s1;
    // vector< vector<int> > p4(a,vector<int>(b));
    strassen_mod(skip_reduce,false,s2,t2,p4,p);
    // p5=s3*t3
    alloc(s2,b,b); vector< vector<int> > & p5=s2;
    // vector< vector<int> > p5(a,vector<int>(b));
    strassen_mod(skip_reduce,false,s3,t3,p5,p);
    // p6=s4*b22
    alloc(s3,b,b); vector< vector<int> > & p6=s3;
    // vector< vector<int> > p6(a,vector<int>(b));
    strassen_mod(skip_reduce,false,s4,Btran,p6,p,0,0,0,0,brbeg+b,brbeg+2*b,bcbeg+ac);
    // p7=a22*t4
    alloc(s4,b,b); vector< vector<int> > & p7=s4;
    // vector< vector<int> > p7(a,vector<int>(b));
    strassen_mod(skip_reduce,false,A,t4,p7,p,arbeg+a,arbeg+2*a,acbeg+ac,acbeg+2*ac,0,b,0);
    // p2=a12*b21=a12*btran12
    t1.resize(a); alloc(t1,b,b); vector< vector<int> > & p2=t1;
    // vector< vector<int> > p2(a,vector<int>(b));
    strassen_mod(skip_reduce,false,A,Btran,p2,p,arbeg,arbeg+a,acbeg+ac,acbeg+2*ac,brbeg,brbeg+b,bcbeg+ac);
    // p1=a11*b11
    t2.resize(a); alloc(t2,b,b); vector< vector<int> > & p1=t2;
    // vector< vector<int> > p1(a,vector<int>(b));
    strassen_mod(skip_reduce,false,A,Btran,p1,p,arbeg,arbeg+a,acbeg,acbeg+ac,brbeg,brbeg+b,bcbeg);
    t3.clear(); 
    t4.clear();
    if (debug_infolevel>2)
      CERR << CLOCK() << "Strassen final add " << a << "," << ac << "," << b << endl;
    // c11=u1=p1+p2
    add_mod(add,p1,p2,C,p);
    // u2=p1+p4 stored in p4
    add_mod(false,p1,p4,p4,skip_reduce?0:p); // mod 0 since not used directly
    // u3=u2+p5 stored in p5
    add_mod(false,p4,p5,p5,skip_reduce?0:p); 
    // c21=u4=u3+p7
    add_mod(add,p5,p7,C,p,0,0,0,0,0,0,a,0);
    // c22=u5=u3+p3
    add_mod(add,p5,p3,C,p,0,0,0,0,0,0,a,b);
    // u6=u2+p3 stored in p3
    add_mod(false,p4,p3,p3,skip_reduce?0:p);
    // c12=u7=u6+p6
    add_mod(add,p3,p6,C,p,0,0,0,0,0,0,0,b);
    p1.clear(); 
    p2.clear(); 
    p3.clear(); 
    p4.clear(); 
    p5.clear(); 
    p6.clear(); 
    p7.clear();
    if (debug_infolevel>2)
      CERR << CLOCK() << "Strassen end " << a << "," << ac << "," << b << endl;
  }

  // Find x=a mod amod and =b mod bmod
  // We have x=a+A*amod=b+B*Bmod
  // hence A*amod-B*bmod=b-a
  // let u*amod+v*bmod=1
  // then A=(b-a)*u is a solution
  // hence x=a+(b-a)*u*amod mod (amod*bmod) is the solution
  // hence x=a+((b-a)*u mod bmod)*amod
  static bool ichinrem_inplace(matrice & a,const matrice &b,const gen & amod, int bmod,int fullreduction){
    gen U,v,d;
    egcd(amod,bmod,U,v,d);
    if (!is_one(d) || U.type!=_ZINT)
      return false;
    int u=mpz_get_si(*U._ZINTptr);
    longlong q;
    for (unsigned i=0;i<a.size();++i){
      gen * ai = &a[i]._VECTptr->front(), * aiend=ai+a[i]._VECTptr->size();
      gen * bi = &b[i]._VECTptr->front();
      if (fullreduction==2){
	q=smod(bi[i]-ai[i],bmod).val;
	q=smod(q*u,bmod); // (q*u) % bmod ;
	ai[i] += int(q)*amod;
	ai += a.size();
	bi += a.size();
      }
      for (;ai!=aiend;++bi,++ai){
	q=longlong(bi->val)-(ai->type==_INT_?ai->val:modulo(*ai->_ZINTptr,bmod));
	q=smod(q*u,bmod); // (q*u) % bmod;
	if (amod.type==_ZINT && ai->type==_ZINT){
	  if (q>=0)
	    mpz_addmul_ui(*ai->_ZINTptr,*amod._ZINTptr,int(q));
	  else
	    mpz_submul_ui(*ai->_ZINTptr,*amod._ZINTptr,-int(q));
	}
	else
	  *ai += int(q)*amod;
      }
    }
    return true;
  }

  struct thread_mmult_mod_t {
    int p;
    const matrice *a,*btran;
    vector< vector<int> > *ai,*btrani,*ci;
  };

  void * thread_mmult_mod(void * ptr_){
    thread_mmult_mod_t * ptr = (thread_mmult_mod_t *) ptr_;
    vecteur2vectvector_int(*ptr->a,ptr->p,*ptr->ai); 
    vecteur2vectvector_int(*ptr->btran,ptr->p,*ptr->btrani);
    mmult_mod(*ptr->ai,*ptr->btrani,*ptr->ci,ptr->p);
    return ptr;
  }

  // a and btran must have integer coefficients
  // matrix multiplication using modular reconstruction
  bool mmult_int(const matrice & a,const matrice & btran,matrice & c){
    if (debug_infolevel>2)
      CERR << CLOCK() << " begin mmult_int" << endl;
    int n=a.front()._VECTptr->size();
    gen ainf=linfnorm(a,context0),binf=linfnorm(btran,context0),resinf=n*ainf*binf;
    if (debug_infolevel>2)
      CERR << CLOCK() << " after linfnorm" << endl;
    double nsteps=nbits(resinf);
    int resrows=mrows(a);
    int rescols=mrows(btran);
    vector< vector<int> > ai(resrows,vector<int>(n));
    vector< vector<int> > btrani(rescols,vector<int>(n));
    vector< vector<int> > ci(resrows,vector<int>(rescols));
    // ||res||_inf <= ||a||_inf * ||b||_inf*n
    if (resinf.type==_INT_){
      vecteur2vectvector_int(a,0,ai); vecteur2vectvector_int(btran,0,btrani);
      mmult_mod(ai,btrani,ci,0);
      vectvector_int2vecteur(ci,c);
      if (debug_infolevel>2)
	CERR << CLOCK() << " end mmult_int" << endl;
      return true;
    }
    double a2=nbits(ainf),b2=nbits(binf);
    if ( (a2<128) || (b2<128) ||
	 (a2/b2<1.4 && b2/a2<1.4) ) {
      // non modular multiplication, using mpz_addmul
      c=vecteur(resrows);
      for (int i=0;i<resrows;++i){
	vecteur resi(rescols);
	for (int j=0;j<rescols;++j){
	  gen tmp;
	  tmp.uncoerce();
	  const_iterateur it=a[i]._VECTptr->begin(),itend=a[i]._VECTptr->end(),jt=btran[j]._VECTptr->begin();
	  for (;it!=itend;++jt,++it){
	    if (it->type==_INT_){
	      if (jt->type==_INT_){
		longlong x=longlong(it->val)*jt->val;
#if defined x86_64 && !defined(WIN64) //fred
		if (x>=0)
		  mpz_add_ui(*tmp._ZINTptr,*tmp._ZINTptr,x);
		else
		  mpz_sub_ui(*tmp._ZINTptr,*tmp._ZINTptr,-x);
#else
		tmp += gen(x);
		tmp.uncoerce();
#endif
	      }
	      else {
		if (it->val>0)
		  mpz_addmul_ui(*tmp._ZINTptr,*jt->_ZINTptr,it->val);
		else
		  mpz_submul_ui(*tmp._ZINTptr,*jt->_ZINTptr,-it->val);
	      }
	    } // end it->type==_INT_
	    else {
	      if (jt->type==_INT_){
		if (jt->val>0)
		  mpz_addmul_ui(*tmp._ZINTptr,*it->_ZINTptr,jt->val);
		else
		  mpz_submul_ui(*tmp._ZINTptr,*it->_ZINTptr,-jt->val);
	      }
	      else 
		mpz_addmul(*tmp._ZINTptr,*it->_ZINTptr,*jt->_ZINTptr);
	    }
	  }
	  if (mpz_sizeinbase(*tmp._ZINTptr,2)>=31)
	    resi[j]=*tmp._ZINTptr;
	  else
	    resi[j]=int(mpz_get_si(*tmp._ZINTptr));
	}
	c[i]=resi;
      }
      if (debug_infolevel>2)
	CERR << CLOCK() << " end mmult_int" << endl;
      return true;
    }
    double p0=3037000500./std::sqrt(double(n))/5.; // so that p0^2*rows(a)<2^63
    nsteps = nsteps/std::log(p0)*std::log(2.0);
    gen p=int(p0),pi_p(1);
    matrice cmod;
    int i=0,j=0;
#ifdef HAVE_LIBPTHREAD
    int nthreads=threads_allowed?threads:1;
    if (nthreads>1){
      pthread_t tab[nthreads-1];
#ifdef __clang__
      vector< vector<int> > *tabai = (vector< vector<int> > *)alloca(nthreads*sizeof(vector< vector<int> >)),
	*tabbtrani = (vector< vector<int> > *)alloca(nthreads*sizeof(vector< vector<int> >)),
	*tabci = (vector< vector<int> > *)alloca(nthreads*sizeof(vector< vector<int> >));
#else
      vector< vector<int> > tabai[nthreads],tabbtrani[nthreads],tabci[nthreads];
#endif
      thread_mmult_mod_t multmodparam[nthreads];
      for (int k=0;k<nthreads;++k){
	thread_mmult_mod_t tmp={0,&a,&btran,&tabai[k],&tabbtrani[k],&tabci[k]};
	multmodparam[k]=tmp;
      }
      for (;i<=(nsteps/nthreads)*nthreads;){
	for (j=0;j<nthreads;++j){
	  p=nextprime(p+1);
	  multmodparam[j].p=p.val;
	  bool res=true;
	  if (j<nthreads-1)
	    res=pthread_create(&tab[j],(pthread_attr_t *) NULL,thread_mmult_mod,(void *) &multmodparam[j]);
	  if (res)
	    thread_mmult_mod((void *)&multmodparam[j]);
	}
	for (j=0;j<nthreads;++j){
	  void * ptr=(void *)&nthreads; // non-zero initialisation
	  if (j<nthreads-1)
	    pthread_join(tab[j],&ptr);
	  if (ptr){
	    if (i==0)
	      vectvector_int2vecteur(*multmodparam[j].ci,c);
	    else {
	      vectvector_int2vecteur(*multmodparam[j].ci,cmod);
	      ichinrem_inplace(c,cmod,pi_p,multmodparam[j].p,0 /* fullreduction */);
	    }
	    ++i;
	    pi_p=multmodparam[j].p*pi_p;
	  } // end if(ptr)
	} // end loop on j
      } // end loop on i
    } // end if nthreads>1
#endif
    // finish
    for (;i<=nsteps;++i){
      p=nextprime(p+1);
      vecteur2vectvector_int(a,p.val,ai); vecteur2vectvector_int(btran,p.val,btrani);
      mmult_mod(ai,btrani,ci,p.val);
      if (i==0)
	vectvector_int2vecteur(ci,c);
      else {
	vectvector_int2vecteur(ci,cmod);
	ichinrem_inplace(c,cmod,pi_p,p.val,0 /* fullreduction */);
      }
      pi_p=p*pi_p;
    }
    smod_inplace(c,pi_p);
    return true;
  }


  // ad*b->c where b is given by it's tranposed btrand
  void mmult_double(const matrix_double & ad,const matrix_double & btrand,matrix_double & c){
    int n=ad.front().size();
    int resrows=ad.size();
    int rescols=btrand.size();
    if (c.empty())
      c=matrix_double(resrows,vector<giac_double>(rescols));
    else {
      c.resize(resrows);
      for (int i=0;i<resrows;++i)
	c[i].resize(rescols);
    }
#ifdef HAVE_LIBPTHREAD
    int nthreads=threads_allowed?threads:1;
    if (nthreads>1){
      pthread_t tab[nthreads-1];
      thread_mmult_double_t multdparam[nthreads];
      for (int j=0;j<nthreads;++j){
	thread_mmult_double_t tmp={&ad,&btrand,&c,0,0,n,rescols,0,0,0,0,0,0,true};
	multdparam[j]=tmp;
      }
      int kstep=int(std::ceil(resrows/double(nthreads))),k=0;
      for (int j=0;j<nthreads;++j){
	multdparam[j].k=k;
	k += kstep;
	if (k>resrows)
	  k=resrows;
	multdparam[j].kend=k;
	bool res=true;
	if (j<nthreads-1)
	  res=pthread_create(&tab[j],(pthread_attr_t *) NULL,do_thread_mmult_double,(void *) &multdparam[j]);
	if (res)
	  do_thread_mmult_double((void *)&multdparam[j]);
      }
      for (int j=0;j<nthreads;++j){
	void * ptr=(void *)&nthreads; // non-zero initialisation
	if (j<nthreads-1)
	  pthread_join(tab[j],&ptr);
      }
      return ;
    } // end nthreads
#endif // PTHREAD
    for (int i=0;i<n;i+=mmult_double_blocksize){
      int iend=i+mmult_double_blocksize;
      if (iend>n)
	iend=n;
      for (int k=0;k<resrows;k+=mmult_double_blocksize){
	int kend=k+mmult_double_blocksize;
	if (kend>resrows)
	  kend=resrows;
	for (int j=0;j<rescols;j+=mmult_double_blocksize){
	  int jend=j+mmult_double_blocksize;
	  if (jend>rescols)
	    jend=rescols;
	  mmult_double_block(ad,k,kend,btrand,j,jend,c,0,0,i,iend);
	}
      }
    }
  }

#endif // GIAC_HAS_STO_38

  bool mmult_double(const matrice & a,const matrice & btran,matrice & res){
    matrix_double ad,btrand;
    if (matrice2std_matrix_double(a,ad,true) && matrice2std_matrix_double(btran,btrand,true)){
      int resrows=mrows(a);
      int rescols=mrows(btran);
#if 1 // ndef GIAC_HAS_STO_38
      int n;
      if (!ad.empty() && resrows>=2*mmult_double_blocksize && rescols>=2*mmult_double_blocksize && (n=ad.front().size())>=mmult_double_blocksize){
	if (n>=CALL_LAPACK && resrows>=CALL_LAPACK && rescols>=CALL_LAPACK){
#ifdef HAVE_LIBLAPACK
	  /*
	   *       DGEMM(TRANSA,TRANSB,M,N,K,ALPHA,A,LDA,B,LDB,BETA,C,LDC)
	   * 
	   *       .. Scalar Arguments ..
	   *       DOUBLE PRECISION ALPHA,BETA
	   *       INTEGER K,LDA,LDB,LDC,M,N
	   *       CHARACTER TRANSA,TRANSB
	   *       ..
	   *       .. Array Arguments ..
	   *       DOUBLE PRECISION A(LDA,*),B(LDB,*),C(LDC,*)
	   *       ..
	   *> DGEMM  performs one of the matrix-matrix operations
	   *>
	   *>    C := alpha*op( A )*op( B ) + beta*C,
	   *>
	   *> where  op( X ) is one of
	   *>
	   *>    op( X ) = X   or   op( X ) = X**T,
	   *>
	   *> alpha and beta are scalars, and A, B and C are matrices, with op( A )
	   *> an m by k matrix,  op( B )  a  k by n matrix and  C an m by n matrix.
	   *  Arguments:
	   *  ==========
	   *>          TRANSA is CHARACTER*1 'T' for transpose, 'N' for normal
	   *>          TRANSB is CHARACTER*1
	   *>          M is INTEGER
	   *>           On entry,  M  specifies  the number  of rows  of the  matrix
	   *>           op( A )  and of the  matrix  C.  M  must  be at least  zero.
	   *>          N is INTEGER
	   *>           On entry,  N  specifies the number  of columns of the matrix
	   *>           op( B ) and the number of columns of the matrix C. N must be
	   *>           at least zero.
	   *>          K is INTEGER
	   *>           On entry,  K  specifies  the number of columns of the matrix
	   *>           op( A ) and the number of rows of the matrix op( B ). K must
	   *>           be at least  zero.
	   *>          ALPHA is DOUBLE PRECISION.
	   *>           On entry, ALPHA specifies the scalar alpha.
	   *>          A is DOUBLE PRECISION array of DIMENSION ( LDA, ka ), where ka is
	   *>           k  when  TRANSA = 'N' or 'n',  and is  m  otherwise.
	   *>           Before entry with  TRANSA = 'N' or 'n',  the leading  m by k
	   *>           part of the array  A  must contain the matrix  A,  otherwise
	   *>           the leading  k by m  part of the array  A  must contain  the
	   *>           matrix A.
	   *>          LDA is INTEGER
	   *>           On entry, LDA specifies the first dimension of A as declared
	   *>           in the calling (sub) program. When  TRANSA = 'N' or 'n' then
	   *>           LDA must be at least  max( 1, m ), otherwise  LDA must be at
	   *>          B is DOUBLE PRECISION array of DIMENSION ( LDB, kb ), where kb is
	   *>           n  when  TRANSB = 'N' or 'n',  and is  k  otherwise.
	   *>           Before entry with  TRANSB = 'N' or 'n',  the leading  k by n
	   *>           part of the array  B  must contain the matrix  B,  otherwise
	   *>           the leading  n by k  part of the array  B  must contain  the
	   *>           matrix B.
	   *>          LDB is INTEGER
	   *>           On entry, LDB specifies the first dimension of B as declared
	   *>           in the calling (sub) program. When  TRANSB = 'N' or 'n' then
	   *>           LDB must be at least  max( 1, k ), otherwise  LDB must be at
	   *>           least  max( 1, n ).
	   *>          BETA is DOUBLE PRECISION.
	   *>           On entry,  BETA  specifies the scalar  beta.  When  BETA  is
	   *>           supplied as zero then C need not be set on input.
	   *>          C is DOUBLE PRECISION array of DIMENSION ( LDC, n ).
	   *>           Before entry, the leading  m by n  part of the array  C must
	   *>           contain the matrix  C,  except when  beta  is zero, in which
	   *>           case C need not be set on entry.
	   *>           On exit, the array  C  is overwritten by the  m by n  matrix
	   *>           ( alpha*op( A )*op( B ) + beta*C ).
	   *>          LDC is INTEGER
	   *>           On entry, LDC specifies the first dimension of C as declared
	   *>           in  the  calling  (sub)  program.   LDC  must  be  at  least
	   *>           max( 1, m ).
	   */
	  integer M(resrows),N(rescols),K(n);
	  double * A = new double[resrows*n];
	  double * B = new double[rescols*n];
	  double * C = new double[resrows*rescols];
	  matrice2lapack(a,A,context0);
	  matrice2lapack(btran,B,context0);
	  double alpha=1.0;
	  double beta=0.0;
	  dgemm_((char*)"N",(char*)"T",&M,&N,&K,&alpha,A,/*LDA*/&M,B,/*LDB*/&N,&beta,C,/*LDC*/&M);
	  lapack2matrice(C,resrows,rescols,res);
	  delete [] A; delete [] B; delete [] C;
	  return true;
#endif // LAPACK
	} // if n>LAPACK_CALL ...
	matrix_double c(resrows,vector<giac_double>(rescols));
	mmult_double(ad,btrand,c);
	std_matrix<gen> cg;
	std_matrix_giac_double2std_matrix_gen(c,cg);
	std_matrix_gen2matrice_destroy(cg,res);
	return true;
      }
#endif // GIAC_HAS_STO38
      matrix_double::const_iterator ita=ad.begin(),itaend=ad.end();
      matrix_double::const_iterator itbbeg=btrand.begin(),itb,itbend=btrand.end();
      res.clear();
      res.reserve(resrows);
      for (;ita!=itaend;++ita){
	res.push_back(new_ref_vecteur(rescols));
	gen * cur = &res.back()._VECTptr->front();
	for (itb=itbbeg;itb!=itbend;++cur,++itb){
	  giac_double res=0.0;
	  const giac_double * i=&ita->front(),*iend=i+ita->size(),*j=&itb->front();
	  for (;i!=iend;++j,++i){
	    res += (*i)*(*j);
	  }
	  *cur=double(res);
	  // *cur=double(dotvecteur(*ita,*itb));
	}
      }
      return true;
    }
    matrix_complex_double zad,zbtrand;
    if (!matrice2std_matrix_complex_double(a,zad,true) || !matrice2std_matrix_complex_double(btran,zbtrand,true))
      return false;
    matrix_complex_double::const_iterator ita=zad.begin(),itaend=zad.end();
    matrix_complex_double::const_iterator itbbeg=zbtrand.begin(),itb,itbend=zbtrand.end();
    int resrows=mrows(a);
    int rescols=mrows(btran);
    res.clear();
    res.reserve(resrows);
    for (;ita!=itaend;++ita){
      res.push_back(new_ref_vecteur(rescols));
      vecteur & cur_row=*res.back()._VECTptr;
      for (itb=itbbeg;itb!=itbend;++itb)
	cur_row[itb-itbbeg]=dotvecteur(*ita,*itb);
    }
    return true;
  }

  bool fracvect(const vecteur & v){
    for (unsigned i=0;i<v.size();++i){
      if (!is_cinteger(v[i]) && v[i].type!=_FRAC)
	return false;
    }
    return true;
  }

  double matrix_density(const matrice & a){
    int z=0,c=0;
    const_iterateur it=a.begin(),itend=a.end();
    for (;it!=itend;++it){
      if (it->type!=_VECT){
	if (is_zero(*it)) ++z;
	++c;
	continue;
      }
      const_iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
      for (;jt!=jtend;++jt){
	if (is_zero(*jt)) ++z;
	++c;
      }
    }
    return (c-z)/double(c);
  }

  void mmult(const matrice & a_,const matrice & b,matrice & res){
    matrice btran;
    if (debug_infolevel>2)
      CERR << CLOCK() << " mmult begin" << endl;
    mtran(b,btran);
    mmult_atranb(a_,btran,res);
  }
  void mmult_atranb(const matrice & a_,const matrice & btran,matrice & res){
    // now make the (dotvecteur) product of row i of a with rows of btran to get
    // row i of res
#ifndef GIAC_HAS_STO_38
    if (is_integer_matrice(a_) && is_integer_matrice(btran) && mmult_int(a_,btran,res))
      return;
    vector< vector<int> > A,Btran; int p=0;
    if (is_mod_matrice(a_,A,p) && is_mod_matrice(btran,Btran,p)){
      vector< vector<int> > C;
      mmult_mod(A,Btran,C,p);
      vectvector_int2vecteur(C,res);
      res=*makemod(res,p)._VECTptr;
      return;
    }
    int at=a_.front()[0].type,bt=btran.front()[0].type;
    // if ( ( (at==_POLY && bt==_POLY) || (at==_VECT && bt==_VECT) ) && mmult_interp(a_,btran,res) ) return ;
#endif
#if 1 // ndef BCD // creating a copy of the matrices take too much memory and slows down on Aspen
    if ( (has_num_coeff(a_) || has_num_coeff(btran)) && mmult_double(a_,btran,res))
      return;
#endif
    if (debug_infolevel>2)
      CERR << CLOCK() << " find lcm deno" << endl;
    matrice a(a_);
    vecteur adeno(a.size(),1),bdeno(btran.size(),1);
    for (unsigned i=0;i<a.size();++i){
      a[i]=*a[i]._VECTptr;
      if (fracvect(*a[i]._VECTptr))
	lcmdeno(*a[i]._VECTptr,adeno[i],context0);
    }
    for (unsigned i=0;i<btran.size();++i){
      if (fracvect(*btran[i]._VECTptr))
	lcmdeno(*btran[i]._VECTptr,bdeno[i],context0);
    }
    if (debug_infolevel>2)
      CERR << CLOCK() << " lcm deno done" << endl;
#if !defined(GIAC_HAS_STO_38) && !defined(EMCC)
    if (
	//a.front()._VECTptr->size()>=7 &&
	is_integer_matrice(a) && is_integer_matrice(btran) && mmult_int(a,btran,res)
	)
      ;
    else 
#endif
      {
	vecteur::const_iterator ita=a.begin(),itaend=a.end();
	vecteur::const_iterator itbbeg=btran.begin(),itb,itbend=btran.end();
	int resrows=mrows(a);
	int rescols=mrows(btran);
	res.clear();
	res.reserve(resrows);
	double a_density=matrix_density(a),b_density=matrix_density(btran);
	if (a_density*b_density>0.1){
	  /* code for dense matrices */
	   vecteur cur_row;
	   for (;ita!=itaend;++ita){
	     cur_row.clear();
	     cur_row.reserve(rescols);
	     for (itb=itbbeg;itb!=itbend;++itb)
	       cur_row.push_back(dotvecteur(*(ita->_VECTptr),*(itb->_VECTptr)));
	     res.push_back(cur_row);
	   }
	} // end dense matrices
	else {
	  int s=int(btran.size());
	  gen tmp;
	  const_iterateur it,itend;
	  vector<const_iterateur> itbb(s);
	  iterateur itc;
	  for (;ita!=itaend;++ita){
	    vecteur c(rescols,zero);
	    it=ita->_VECTptr->begin();
	    itend=ita->_VECTptr->end();
	    itb=itbbeg;
	    for (int i=0;i<s;++i,++itb)
	      itbb[i]=itb->_VECTptr->begin();
	    for (;it!=itend;++it){
	      const gen & acur=*it;
	      if (is_zero(acur,context0)){
		int p=1;
		++it;
		for (; (it!=itend) && is_zero(*it,context0);++it,++p){
		}
		if (it==itend)
		  break;
		else
		  --it;
		for (int i=0;i<s;++i)
		  itbb[i]+=p;
	      }
	      else {
		itc=c.begin();
		gen tmp;
		for (int i=0;i<s;++itc,++(itbb[i]),++i){
		  type_operator_times(acur, *(itbb[i]),tmp);
		  *itc += tmp;
		}
	      }
	    }
	    res.push_back(c);
	  }
	} // end sparse matrices
      } // end #endif else
    for (unsigned i=0;i<adeno.size();++i){
      vecteur & v=*res[i]._VECTptr;
      for (unsigned j=0;j<bdeno.size();++j){
	v[j] = v[j]/(adeno[i]*bdeno[j]);
      }
    }
    // if (!res1.empty() && res1!=res) CERR << "err" << endl;
  }

  matrice mmult(const matrice & a,const matrice & b){
    matrice res;
    mmult(a,b,res);
    return res;
  }

  bool mmultck(const matrice & a, const matrice & b,matrice & res){
    if (mcols(a)!=mrows(b))
      return false; 
    mmult(a,b,res);
    return true;
  }

  matrice mmultck(const matrice & a, const matrice & b){
    matrice res;
    if (!mmultck(a,b,res))
      return vecteur(1,vecteur(1,gendimerr(gettext("mmultck"))));
    return res;
  }

  gen mtrace(const matrice & a){
    gen res(0);
    vecteur::const_iterator it=a.begin(),itend=a.end();
    for (int i=0;it!=itend;++it,++i)
      res = res + (*it)[i];
    return res;
  }
  
  gen ckmtrace(const gen & a,GIAC_CONTEXT){
    if (!is_squarematrix(a))
      return symbolic(at_trace,a); // gendimerr(contextptr); required to keep trace for geometry
    return mtrace(*a._VECTptr);
  }
  static const char _trace_s []="trace";
  static define_unary_function_eval (__trace,&giac::ckmtrace,_trace_s);
  define_unary_function_ptr5( at_trace ,alias_at_trace,&__trace,0,true);

  gen common_deno(const vecteur & v){
    const_iterateur it=v.begin(),itend=v.end();
    gen lcm_deno(1);
    for (;it!=itend;++it){
      if (it->type==_FRAC)
	lcm_deno=rdiv(lcm_deno,gcd(lcm_deno,it->_FRACptr->den),context0)*(it->_FRACptr->den);
    }
    return lcm_deno;
  }

  static gen common_num(const vecteur & v){
    const_iterateur it=v.begin(),itend=v.end();
    gen gcd_num(0);
    for (;it!=itend;++it){
      if (it->type!=_FRAC)
	gcd_num=gcd(gcd_num,*it);
    }
    return gcd_num;
  }

  static inline gen trim(const gen & a,const gen & b,double eps){
    if (eps && a.type==_DOUBLE_ && b.type==_DOUBLE_ &&
	fabs(a._DOUBLE_val)<eps*fabs(b._DOUBLE_val)) 
      return 0;
    else
      return a;
  }

  gen exact_div(const gen & a,const gen & b){
    if (a.type==_POLY && b.type==_POLY){
      polynome *quoptr=new polynome, rem;
      if (!divrem1(*a._POLYptr,*b._POLYptr,*quoptr,rem,2)) 
	CERR << "bad quo("+a.print()+","+b.print()+")" << endl;
      gen res= *quoptr;
      // if (!is_zero(a-b*res))
      //	CERR << "Bad division" << endl;
      return res;
#if 0
      polynome quo;
      if (!a._POLYptr->Texactquotient(*b._POLYptr,quo))
	CERR << "bad quo("+a.print()+","+b.print()+")" << endl;
      return quo;
#endif
    }
    return rdiv(a,b,context0);
  }

  // v=(c1*v1+c2*v2)/c
  // Set cstart to 0, or to c+1 for lu decomposition
  void linear_combination(const gen & c1,const vecteur & v1,const gen & c2,const vecteur & v2,const gen & c,const gen & cinv,vecteur & v,double eps,int cstart){
    if (!is_one(cinv)){
      if (cinv.type==_FRAC)
	linear_combination(c1*cinv._FRACptr->num,v1,c2*cinv._FRACptr->num,v2,cinv._FRACptr->den,1,v,eps,cstart);
      else
	linear_combination(c1*cinv,v1,c2*cinv,v2,1,1,v,eps,cstart);
      return;
    }
    const_iterateur it1=v1.begin()+cstart,it1end=v1.end(),it2=v2.begin()+cstart;
    iterateur jt1=v.begin()+cstart;
#ifdef DEBUG_SUPPORT
    if (it1end-it1!=v2.end()-it2)
      setdimerr();
#endif
    if (it2==jt1){
      linear_combination(c2,v2,c1,v1,c,1,v,eps,cstart);
      return;
    }
    if (it1==jt1){
      if (is_one(c)){
	for (;jt1!=it1end;++jt1,++it2){
	  *jt1=trim(c1*(*jt1)+c2*(*it2),c1,eps);
	}
      }
      else {
	int t=0;
	if (c1.type==c2.type){
	  t=c1.type;
	  if (t==_EXT && *(c1._EXTptr+1)!=*(c2._EXTptr+1))
	    t=0;
	}
	for (;jt1!=it1end;++jt1,++it2){
#ifndef USE_GMP_REPLACEMENTS
	  if (t==_ZINT && jt1->type==_ZINT && c.type==_ZINT && it2->type==_ZINT && jt1->ref_count()==1){
	    mpz_mul(*jt1->_ZINTptr,*jt1->_ZINTptr,*c1._ZINTptr);
	    mpz_addmul(*jt1->_ZINTptr,*it2->_ZINTptr,*c2._ZINTptr);
	    mpz_divexact(*jt1->_ZINTptr,*jt1->_ZINTptr,*c._ZINTptr);
	    if (mpz_sizeinbase(*jt1->_ZINTptr,2)<31)
	      *jt1=int(mpz_get_si(*jt1->_ZINTptr));
	    continue;
	  }
#endif
	  if (t==_EXT && jt1->type==_EXT && it2->type==_EXT && *(jt1->_EXTptr+1)==*(c1._EXTptr+1) && *(it2->_EXTptr+1)==*(c1._EXTptr+1)){
	    gen tmp=change_subtype(*c1._EXTptr,_POLY1__VECT)*(*jt1->_EXTptr)+change_subtype(*c2._EXTptr,_POLY1__VECT)*(*it2->_EXTptr);
	    tmp=ext_reduce(tmp,*(c1._EXTptr+1));
	    *jt1=exact_div(tmp,c);
	    continue;
	  }
	  *jt1=trim(exact_div(c1*(*jt1)+c2*(*it2),c),c1,eps);
	}
      }
      return;
    }
    v.clear();
    v.reserve(it1end-it1);
    if (is_one(c)){
      for (;it1!=it1end;++it1,++it2)
	v.push_back(trim(c1*(*it1)+c2*(*it2),c1,eps));
    }
    else {
      for (;it1!=it1end;++it1,++it2)
	v.push_back(trim(exact_div(c1*(*it1)+c2*(*it2),c),c1,eps));
    }
  }

  // v1=v1+c2*v2 smod modulo
  void modlinear_combination(vecteur & v1,const gen & c2,const vecteur & v2,const gen & modulo,int cstart,int cend){
    if (!is_exactly_zero(c2)){
      iterateur it1=v1.begin()+cstart,it1end=v1.end();
      if (cend && cend>=cstart && cend<it1end-v1.begin())
	it1end=v1.begin()+cend;
      const_iterateur it2=v2.begin()+cstart;
      for (;it1!=it1end;++it1,++it2)
	*it1=smod((*it1)+c2*(*it2),modulo);
    }
  }

  // v=c1*v1+c2*v2 
  void double_linear_combination(double c1,const std::vector<giac_double> & v1,double c2,const std::vector<giac_double> & v2,std::vector<giac_double> & v,int cstart){
    std::vector<giac_double>::const_iterator it1=v1.begin()+cstart,it1end=v1.end();
    std::vector<giac_double>::const_iterator it2=v2.begin()+cstart;
    std::vector<giac_double>::iterator it=v.begin()+cstart;
    for (;it1!=it1end;++it,++it1,++it2)
      *it = c1*(*it1)+c2*(*it2);
  }

  // v1=v1+c2*v2 
  void double_linear_combination(std::vector<giac_double> & v1,double c2,const std::vector<giac_double> & v2,int cstart,int cend){
    if (c2){
      std::vector<giac_double>::iterator it1=v1.begin()+cstart,it1end=v1.end();
      if (cend && cend>=cstart && cend<it1end-v1.begin())
	it1end=v1.begin()+cend;
      std::vector<giac_double>::const_iterator it2=v2.begin()+cstart;
      for (;it1!=it1end;++it1,++it2)
	*it1 += c2*(*it2);
    }
  }

#ifndef GIAC_HAS_STO_38
  // v1 += c1*w, v2 += c2*w, v3 += c3*w, v4 += c4*w; 
  void double_multilinear_combination(std::vector<giac_double> & v1,giac_double c1,std::vector<giac_double> & v2,giac_double c2,std::vector<giac_double> & v3,giac_double c3,std::vector<giac_double> & v4,giac_double c4,const std::vector<giac_double> & w,int cstart,int cend){
    if (!c1 && !c2 && !c3 && !c4)
      return;
    std::vector<giac_double>::iterator it1=v1.begin()+cstart,it1end=v1.end(),it2=v2.begin()+cstart,it3=v3.begin()+cstart,it4=v4.begin()+cstart;
    if (cend && cend>=cstart && cend<it1end-v1.begin())
      it1end=v1.begin()+cend;
    std::vector<giac_double>::const_iterator jt=w.begin()+cstart;
    for (;it1!=it1end;++jt,++it4,++it3,++it2,++it1){
      giac_double tmp=*jt;
      *it1 += c1*tmp;
      *it2 += c2*tmp;
      *it3 += c3*tmp;
      *it4 += c4*tmp;
    }
  }

#ifdef PSEUDO_MOD
  inline int pseudo_quo(longlong x,int p,unsigned invp,unsigned nbits){
    longlong q=(((x>>nbits)*invp)>>(nbits));
    longlong y = x-q*p;
    while (y>=p){
      ++q; y-=p;
    }
    while (y<=-p){
      --q; y+=p;
    }
    return q;
  }
  // find pseudo remainder of x mod p, 2^nbits>=p>2^(nbits-1)
  // assumes invp=2^(2*nbits)/p+1 has been precomputed 
  // and abs(x)<2^(31+nbits)
  // |remainder| <= max(2^nbits,|x|*p/2^(2nbits)), <=2*p if |x|<=p^2
  inline int pseudo_mod(longlong x,int p,unsigned invp,unsigned nbits){
#if 1 // def INT128
    //if ( x - (((x>>nbits)*invp)>>(nbits))*p != int(x - (((x>>nbits)*invp)>>(nbits))*p)){ CERR << "erreur " << x << " " << p << endl; exit(1); }
    return x - (((x>>nbits)*invp)>>(nbits))*p;
#else
    // longlong X=x;
    ulonglong mask= x>>63;
    x ^= mask; // clear sign
    int y = x - (((x>>nbits)*invp)>>(nbits))*p;
    // int z=y;
    y ^= ((unsigned) mask);
    // if ((y-X)%p) CERR << "error" << x << endl;
    // if (y<=-p || y>=p) 
    //  CERR << "error " << y << " " << p << endl;
    return y;
#endif
  }

  // a <- (a+b*c) mod or smod p
  inline void pseudo_mod(int & a,int b,int c,int p,unsigned invp,unsigned nbits){
    a=pseudo_mod(a+((longlong)b)*c,p,invp,nbits);
  }

  // n==nbits, for |x|<=p^2, 2^n>=p>2^(n-1), returns |remainder|<=p
  // assumes invp=floor(2^(2n)/p)+1, 2^(2n)/p < invp <= 2^(2n)/p+1
  // for x>0, x/2^n-1 < floor(x/2^n) <= x/2^n hence we have 
  // x/p-2^n/p < floor((x/2^n)*invp)/2^n <= x/p+x/2^(2n)
  // therefore -x*p/2^(2n) <= x-(floor((x/2^n)*invp)/2^n)*p < 2^n
  // the remainder is at most p*(2^n/p)< 2*p if x is positive
  // number of operations 5+4+4 (instead of 5 for pseudomod)
  inline int pseudo_mod_reduced(longlong x,int p,unsigned invp,unsigned nbits){
    int y=x - (((x>>nbits)*invp)>>(nbits))*p;
    y -= ((y-p)>>31)*p;
    return y+((p-y)>>31)*p;
  }
#endif // PSEUDO_MOD

  // v1 += c1*w % p, v2 += c2*w %p, v3 += c3*w % p, v4 += c4*w % p; 
  // v1 += c1*w % p, v2 += c2*w %p, v3 += c3*w % p, v4 += c4*w % p; 
  void int_multilinear_combination(std::vector<int> & v1,int c1,std::vector<int> & v2,int c2,std::vector<int> & v3,int c3,std::vector<int> & v4,int c4,const std::vector<int> & w,int p,int cstart,int cend){
    c1 %=p; c2 %=p; c3 %=p; c4 %=p;
    int * it1=&*(v1.begin()+cstart),*it1end=&*(v1.end()),*it2=&*(v2.begin()+cstart),*it3=&*(v3.begin()+cstart),*it4=&*(v4.begin()+cstart),*it1_;
    if (cend && cend>=cstart && cend<it1end-&v1.front())
      it1end=&*(v1.begin()+cend);
    it1_=it1-4;
    const int * jt=&*(w.begin()+cstart);
#ifdef PSEUDO_MOD
    if (p<(1<<29) 
	// && p>=(1<<16)
	){
      int nbits=sizeinbase2(p); 
      unsigned invp=((1ULL<<(2*nbits)))/p+1;
      for (;it1<=it1_;){
	int tmp=*jt;
	pseudo_mod(*it1,c1,tmp,p,invp,nbits);
	pseudo_mod(*it2,c2,tmp,p,invp,nbits);
	pseudo_mod(*it3,c3,tmp,p,invp,nbits);
	pseudo_mod(*it4,c4,tmp,p,invp,nbits);
	tmp=jt[1];
	pseudo_mod(it1[1],c1,tmp,p,invp,nbits);
	pseudo_mod(it2[1],c2,tmp,p,invp,nbits);
	pseudo_mod(it3[1],c3,tmp,p,invp,nbits);
	pseudo_mod(it4[1],c4,tmp,p,invp,nbits);
	tmp=jt[2];
	pseudo_mod(it1[2],c1,tmp,p,invp,nbits);
	pseudo_mod(it2[2],c2,tmp,p,invp,nbits);
	pseudo_mod(it3[2],c3,tmp,p,invp,nbits);
	pseudo_mod(it4[2],c4,tmp,p,invp,nbits);
	tmp=jt[3];
	pseudo_mod(it1[3],c1,tmp,p,invp,nbits);
	pseudo_mod(it2[3],c2,tmp,p,invp,nbits);
	pseudo_mod(it3[3],c3,tmp,p,invp,nbits);
	pseudo_mod(it4[3],c4,tmp,p,invp,nbits);
	jt+=4;it4+=4;it3+=4;it2+=4;it1+=4;
      }
      for (;it1!=it1end;++jt,++it4,++it3,++it2,++it1){
	int tmp=*jt;
	pseudo_mod(*it1,c1,tmp,p,invp,nbits);
	pseudo_mod(*it2,c2,tmp,p,invp,nbits);
	pseudo_mod(*it3,c3,tmp,p,invp,nbits);
	pseudo_mod(*it4,c4,tmp,p,invp,nbits);
      }
    }
    else
#endif // PSEUDO_MOD
      {
	for (;it1<=it1_;){
	  int tmp=*jt;
	  *it1 = (*it1+longlong(c1)*tmp)%p;
	  *it2 = (*it2+longlong(c2)*tmp)%p;
	  *it3 = (*it3+longlong(c3)*tmp)%p;
	  *it4 = (*it4+longlong(c4)*tmp)%p;
	  ++jt;++it4;++it3;++it2;++it1;
	  tmp=*jt;
	  *it1 = (*it1+longlong(c1)*tmp)%p;
	  *it2 = (*it2+longlong(c2)*tmp)%p;
	  *it3 = (*it3+longlong(c3)*tmp)%p;
	  *it4 = (*it4+longlong(c4)*tmp)%p;
	  ++jt;++it4;++it3;++it2;++it1;
	  tmp=*jt;
	  *it1 = (*it1+longlong(c1)*tmp)%p;
	  *it2 = (*it2+longlong(c2)*tmp)%p;
	  *it3 = (*it3+longlong(c3)*tmp)%p;
	  *it4 = (*it4+longlong(c4)*tmp)%p;
	  ++jt;++it4;++it3;++it2;++it1;
	  tmp=*jt;
	  *it1 = (*it1+longlong(c1)*tmp)%p;
	  *it2 = (*it2+longlong(c2)*tmp)%p;
	  *it3 = (*it3+longlong(c3)*tmp)%p;
	  *it4 = (*it4+longlong(c4)*tmp)%p;
	  ++jt;++it4;++it3;++it2;++it1;
	}
	for (;it1!=it1end;++jt,++it4,++it3,++it2,++it1){
	  int tmp=*jt;
	  *it1 = (*it1+longlong(c1)*tmp)%p;
	  *it2 = (*it2+longlong(c2)*tmp)%p;
	  *it3 = (*it3+longlong(c3)*tmp)%p;
	  *it4 = (*it4+longlong(c4)*tmp)%p;
	}
      }
  }

  void LL_multilinear_combination(std::vector<longlong> & v1,int c1,std::vector<longlong> & v2,int c2,std::vector<longlong> & v3,int c3,std::vector<longlong> & v4,int c4,const std::vector<longlong> & w,int p,int cstart,int cend){
    c1 %=p; c2 %=p; c3 %=p; c4 %=p;
    longlong * it1=&*(v1.begin()+cstart),*it1end=&*(v1.end()),*it2=&*(v2.begin()+cstart),*it3=&*(v3.begin()+cstart),*it4=&*(v4.begin()+cstart),*it1_;
    if (cend && cend>=cstart && cend<it1end-&v1.front())
      it1end=&*(v1.begin()+cend);
    it1_=it1-4;
    const longlong * jt=&*(w.begin()+cstart);
    for (;it1<=it1_;it1+=4,it2+=4,it3+=4,it4+=4,jt+=4){
      longlong tmp=*jt;
      *it1 += c1*tmp;
      *it2 += c2*tmp;
      *it3 += c3*tmp;
      *it4 += c4*tmp;
      tmp=jt[1];
      it1[1] += c1*tmp;
      it2[1] += c2*tmp;
      it3[1] += c3*tmp;
      it4[1] += c4*tmp;
      tmp=jt[2];
      it1[2] += c1*tmp;
      it2[2] += c2*tmp;
      it3[2] += c3*tmp;
      it4[2] += c4*tmp;
      tmp=jt[3];
      it1[3] += c1*tmp;
      it2[3] += c2*tmp;
      it3[3] += c3*tmp;
      it4[3] += c4*tmp;
    }
    for (;it1!=it1end;++jt,++it4,++it3,++it2,++it1){
      longlong tmp=*jt;
      *it1 += c1*tmp;
      *it2 += c2*tmp;
      *it3 += c3*tmp;
      *it4 += c4*tmp;
    }
  }

  bool find_multi_linear_combination(vector< vector<int> > & N,int l0,int & l1,int &l2,int &l3,int pivotcol,int lexcluded,int lmax){
    if (l0>=lmax-3)
      return false;
    l1=l0+1;
    for (;l1<lmax;++l1){
      if (l1!=lexcluded && !N[l1].empty() && N[l1][pivotcol])
	break;
    }
    if (l1>=lmax-2)
      return false;
    l2=l1+1;
    for (;l2<lmax;++l2){
      if (l2!=lexcluded && !N[l2].empty() && N[l2][pivotcol])
	break;
    }
    if (l2>=lmax-1)
      return false;
    l3=l2+1;
    for (;l3<lmax;++l3){
      if (l3!=lexcluded && !N[l3].empty() && N[l3][pivotcol])
	break;
    }
    return l3<lmax;
  }

  bool find_multi_linear_combination(vector< vector<longlong> > & N,int l0,int & l1,int &l2,int &l3,int pivotcol,int lexcluded,int lmax){
    if (l0>=lmax-3)
      return false;
    vector<longlong> * ptr;
    l1=l0+1;
    for (;l1<lmax;++l1){
      if (l1!=lexcluded && !(ptr=&N[l1])->empty() && (*ptr)[pivotcol])
	break;
    }
    if (l1>=lmax-2)
      return false;
    l2=l1+1;
    for (;l2<lmax;++l2){
      if (l2!=lexcluded && !(ptr=&N[l2])->empty() && (*ptr)[pivotcol])
	break;
    }
    if (l2>=lmax-1)
      return false;
    l3=l2+1;
    for (;l3<lmax;++l3){
      if (l3!=lexcluded && !(ptr=&N[l3])->empty() && (*ptr)[pivotcol])
	break;
    }
    return l3<lmax;
  }

  struct thread_double_lu2inv_t {
    matrix_double * m;
    int i,end,n;
    vector<int> * startshift,*lastnon0posv;
  };

  void * do_thread_double_linv(void * ptr){
    thread_double_lu2inv_t * p = (thread_double_lu2inv_t *) ptr;
    matrix_double & m =*p->m;
    int i=p->i;
    int end=p->end;
    int n=p->n;
    vector<int> * startshift=p->startshift;
    // first step compute l^-1 this is done by the recurrence: l*a=y: 
    // a1=y1, a2=y2-l_21*a1, ..., ak=yk-sum_{j=1..k-1}(l_kj*aj)
    // if y=(0,..,0,1,0,...0), 
    // a0=..=a_{i-1}=0 and we start at equation k=i+1 and sum_{j=i...}
    // n^3/6 operations
    for (;i<=end-4;i+=4){
      giac_double * col0=&m[i][n],* col1=&m[i+1][n],* col2=&m[i+2][n],* col3=&m[i+3][n];
      for (int k=0;k<i+4;++k){
	col0[k]=0.0;
	col1[k]=0.0;
	col2[k]=0.0;
	col3[k]=0.0;
      }
      col0[i]=1.0;
      col0[i+1]=-m[i+1][i];
      col0[i+2]=-m[i+2][i]-col0[i+1]*m[i+2][i+1];
      col0[i+3]=-m[i+3][i]-col0[i+1]*m[i+3][i+1]-col0[i+2]*m[i+3][i+2];
      col1[i+1]=1.0;
      col1[i+2]=-m[i+2][i+1];
      col1[i+3]=-m[i+3][i+1]-col1[i+2]*m[i+3][i+2];
      col2[i+2]=1.0;
      col2[i+3]=-m[i+3][i+2];
      col3[i+3]=1.0;
      for (int k=i+4;k<n;++k){
	giac_double res0=0.0,res1=0.0,res2=0.0,res3=0.0;
	giac_double * mkj=&m[k][i],*col0j=col0+i,*col1j=col1+i,*col2j=col2+i,*col3j=col3+i,*col0end=col0+k;
	// skip leading 0 in l rows
	if (startshift){
	  int shift=(*startshift)[k]-i;
	  if (shift>0){
	    mkj += shift;
	    col0j += shift;
	    col1j += shift;
	    col2j += shift;
	    col3j += shift;
	  }
	}
	for (;col0j<col0end;++mkj,++col3j,++col2j,++col1j,++col0j){
	  giac_double tmp=(*mkj);
	  if (!tmp) continue;
	  res0 -= tmp*(*col0j); 
	  res1 -= tmp*(*col1j); 
	  res2 -= tmp*(*col2j); 
	  res3 -= tmp*(*col3j); 
	}
	*col0j=res0;
	*col1j=res1;
	*col2j=res2;
	*col3j=res3;
      }
    }
    for (;i<end;++i){
      giac_double * col=&m[i][n];
      for (int k=0;k<i;++k)
	col[k]=0.0;
      col[i]=1.0;
      for (int k=i+1;k<n;++k){
	giac_double res=0.0;
	giac_double * mkj=&m[k][i],*colj=col+i,*colend=col+k;
	for (;colj<colend;++mkj,++colj)
	  res -= (*mkj)*(*colj); 
	*colend=res;
      }
    }
    return ptr;
  }

  int invd_blocksize=170;
  gen _invd_blocksize(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=evalf_double(g0,1,contextptr);
    if (g.type!=_DOUBLE_)
      return invd_blocksize;
    return invd_blocksize=int(g._DOUBLE_val);
  }
  static const char _invd_blocksize_s []="invd_blocksize";
  static define_unary_function_eval (__invd_blocksize,&_invd_blocksize,_invd_blocksize_s);
  define_unary_function_ptr5( at_invd_blocksize ,alias_at_invd_blocksize,&__invd_blocksize,0,true);


    // second step, solve u*inverse=l^-1, columns of l^-1 are rows of m starting at col n
    // we compute a column of inverse by solving the system: 
    // u*col(inverse)=corresponding row of l^-1, and overwrite the row of l^-1 by solution
    // u*[x0,..,xn-1]=[a0,...,an]
    // x_{n-1}=a_{n-1}/u_{n-1,n-1}
    // x_{n-2}=(a_{n-2}-u_{n-2,n-1}*x_{n-1})/u_{n-2,n-2}
    // ...
    // x_k=(a_{k}-sum_{j=k+1..n-1} u_{k,j}x_j)/u_{k,k}
    // n^3/2 operations
    // the first i loop is unrolling
  void * do_thread_double_lu2inv(void * ptr){
    thread_double_lu2inv_t * p = (thread_double_lu2inv_t *) ptr;
    matrix_double & m =*p->m;
    int i=p->i;
    int end=p->end;
    int n=p->n;
    vector<int> * lastnon0posv=p->lastnon0posv;
    if (n<=200 || invd_blocksize<=1){
      for (;i<=end-6;i+=6){
	giac_double * col0=&m[i][n],* col1=&m[i+1][n],* col2=&m[i+2][n],* col3=&m[i+3][n],* col4=&m[i+4][n],* col5=&m[i+5][n];
	for (int k=n-1;k>=0;--k){
	  giac_double res0=col0[k],res1=col1[k],res2=col2[k],res3=col3[k],res4=col4[k],res5=col5[k];
	  int lastnon0pos=n-1;
	  if (lastnon0posv)
	    lastnon0pos=(*lastnon0posv)[k];
	  giac_double * mkj=&m[k][lastnon0pos],*col0j=col0+lastnon0pos,*colend=col0+k,*col1j=col1+lastnon0pos,*col2j=col2+lastnon0pos,*col3j=col3+lastnon0pos,*col4j=col4+lastnon0pos,*col5j=col5+lastnon0pos;
	  for (;col0j>colend;--mkj,--col5j,--col4j,--col3j,--col2j,--col1j,--col0j){
	    giac_double tmp=*mkj;
	    if (!tmp) continue; 
	    res0 -= tmp*(*col0j);
	    res1 -= tmp*(*col1j);
	    res2 -= tmp*(*col2j);
	    res3 -= tmp*(*col3j);
	    res4 -= tmp*(*col4j);
	    res5 -= tmp*(*col5j);
	  }
	  giac_double tmp=*mkj;
	  *col0j=res0/tmp;
	  *col1j=res1/tmp;
	  *col2j=res2/tmp;
	  *col3j=res3/tmp;
	  *col4j=res4/tmp;
	  *col5j=res5/tmp;
	}
      }
      for (;i<end;i++){
	giac_double * col=&m[i][n];
	for (int k=n-1;k>=0;--k){
	  giac_double res=col[k];
	  giac_double * mkj=&m[k][n-1],*colj=col+n-1,*colend=col+k;
	  for (;colj>colend;--mkj,--colj){
	    res -= (*mkj)*(*colj);
	  }
	  *colj=res/(*mkj);
	}
      }
    }
    else { // invd_blocsize>1
      int istart=i,iend=end;
      int cstart=n-1,cend;
      for (;cstart>=0;cstart=cend-1){
	cend=cstart-invd_blocksize+1;
	if (cend<0) cend=0;
	// solve unknowns from cstart to cend
	i=istart;
	for (;i<=iend-6;i+=6){
	  giac_double * col0=&m[i][n],* col1=&m[i+1][n],* col2=&m[i+2][n],* col3=&m[i+3][n],* col4=&m[i+4][n],* col5=&m[i+5][n];
	  for (int k=cstart;k>=cend;--k){
	    giac_double res0=col0[k],res1=col1[k],res2=col2[k],res3=col3[k],res4=col4[k],res5=col5[k];
	    int lastnon0pos=cstart;
	    if (lastnon0posv)
	      lastnon0pos=giacmin(cstart,(*lastnon0posv)[k]);
	    giac_double * mkj=&m[k][lastnon0pos],*col0j=col0+lastnon0pos,*colend=col0+k,*col1j=col1+lastnon0pos,*col2j=col2+lastnon0pos,*col3j=col3+lastnon0pos,*col4j=col4+lastnon0pos,*col5j=col5+lastnon0pos;
	    for (;col0j>colend;--mkj,--col5j,--col4j,--col3j,--col2j,--col1j,--col0j){
	      giac_double tmp=*mkj;
	      if (!tmp) continue;
	      res0 -= tmp*(*col0j);
	      res1 -= tmp*(*col1j);
	      res2 -= tmp*(*col2j);
	      res3 -= tmp*(*col3j);
	      res4 -= tmp*(*col4j);
	      res5 -= tmp*(*col5j);
	    }
	    *col0j=res0/(*mkj);
	    *col1j=res1/(*mkj);
	    *col2j=res2/(*mkj);
	    *col3j=res3/(*mkj);
	    *col4j=res4/(*mkj);
	    *col5j=res5/(*mkj);
	  }
	}
	for (;i<iend;i++){
	  giac_double * col=&m[i][n];
	  for (int k=cstart;k>=cend;--k){
	    giac_double res=col[k];
	    giac_double * mkj=&m[k][cstart],*colj=col+cstart,*colend=col+k;
	    for (;colj>colend;--mkj,--colj){
	      res -= (*mkj)*(*colj);
	    }
	    *colj=res/(*mkj);
	  }
	}
	if (!cend) break;
	// use computed values
	int kstart=cend-1,kend;
	for (;kstart>=0;kstart=kend-1){
	  kend=kstart-invd_blocksize+1;
	  if (kend<0) kend=0;
	  i=istart;
	  for (;i<=iend-6;i+=6){
	    giac_double * col0=&m[i][n],* col1=&m[i+1][n],* col2=&m[i+2][n],* col3=&m[i+3][n],* col4=&m[i+4][n],* col5=&m[i+5][n];
	    for (int k=kstart;k>=kend;--k){
	      giac_double res0=col0[k],res1=col1[k],res2=col2[k],res3=col3[k],res4=col4[k],res5=col5[k];
	      giac_double * mkj=&m[k][cstart],*col0j=col0+cstart,*colend=col0+cend,*col1j=col1+cstart,*col2j=col2+cstart,*col3j=col3+cstart,*col4j=col4+cstart,*col5j=col5+cstart;
	      for (;col0j>=colend;--mkj,--col5j,--col4j,--col3j,--col2j,--col1j,--col0j){
		giac_double tmp=*mkj;
		if (!tmp) continue;
		res0 -= tmp*(*col0j);
		res1 -= tmp*(*col1j);
		res2 -= tmp*(*col2j);
		res3 -= tmp*(*col3j);
		res4 -= tmp*(*col4j);
		res5 -= tmp*(*col5j);
	      }
	      col0[k]=res0;
	      col1[k]=res1;
	      col2[k]=res2;
	      col3[k]=res3;
	      col4[k]=res4;
	      col5[k]=res5;
	    }
	  }
	  for (;i<iend;i++){
	    giac_double * col=&m[i][n];
	    for (int k=kstart;k>=kend;--k){
	      giac_double res=col[k];
	      giac_double * mkj=&m[k][cstart],*colj=col+cstart,*colend=col+cend;
	      for (;colj>=colend;--mkj,--colj){
		res -= (*mkj)*(*colj);
	      }
	      col[k]=res;
	    }
	  }
	}
      } // cstart loop
    } // else invd_blocsize
    return ptr;
  }

  // if m is in lu form (first n columns), compute l^-1, then solve u*inverse=l^-1
  void double_lu2inv(matrix_double & m,const vector<int> & permu){
    int n=permu.size();
    vector<int> perm=perminv(permu);
    if (debug_infolevel)
      CERR << CLOCK() << " lu2inv begin n=" << n << endl;
    bool done=false;
    // detect leading 0 in l part of m (speedup for band matrices)
    vector<int> startshiftv(n),lastnon0posv(n,n-1);
    for (int i=0;i<n;++i){
      int j=0;
      vector<double> & mi=m[i];
      for (;j<i;++j){
	if (mi[j])
	  break;
      }
      startshiftv[i]=j;
      j=n-1;
      for (;j>i;--j){
	if (mi[j])
	  break;
      }
      lastnon0posv[i]=j;
    }
#ifdef HAVE_LIBPTHREAD      
    int nthreads=threads_allowed?threads:1;
    if (nthreads>1 && n>40){
      pthread_t tab[nthreads-1];
      thread_double_lu2inv_t param[nthreads];
      int rstep=int(std::ceil(n/double(nthreads))),rstart=0,rend;
      for (int j=0;j<nthreads;++j){
	rend=rstart+rstep;
	if (rend>n) rend=n;
	thread_double_lu2inv_t tmp={&m,rstart,rend,n,&startshiftv,0};
	param[j]=tmp;
	rstart=rend;
	bool res=true;
	if (j<nthreads-1)
	  res=pthread_create(&tab[j],(pthread_attr_t *) NULL,do_thread_double_linv,(void *) &param[j]);
	if (res)
	  do_thread_double_linv((void *)&param[j]);
      }
      for (int j=0;j<nthreads;++j){
	void * ptr=(void *)&nthreads; // non-zero initialisation
	if (j<nthreads-1)
	  pthread_join(tab[j],&ptr);
      }
      done=true;
    }
#endif
    if (!done){
      thread_double_lu2inv_t tmp={&m,0,n,n,&startshiftv,0};
      do_thread_double_linv((void*)&tmp);
    }
    if (debug_infolevel)
      CERR << CLOCK() << " solving u*inv=l^-1" << endl;
    done=false;
#ifdef HAVE_LIBPTHREAD      
    if (nthreads>1 && n>40){
      pthread_t tab[nthreads-1];
      thread_double_lu2inv_t param[nthreads];
      int rstep=int(std::ceil(n/double(nthreads))),rstart=0,rend;
      for (int j=0;j<nthreads;++j){
	rend=rstart+rstep;
	if (rend>n) rend=n;
	thread_double_lu2inv_t tmp={&m,rstart,rend,n,0,&lastnon0posv};
	param[j]=tmp;
	rstart=rend;
	bool res=true;
	if (j<nthreads-1)
	  res=pthread_create(&tab[j],(pthread_attr_t *) NULL,do_thread_double_lu2inv,(void *) &param[j]);
	if (res)
	  do_thread_double_lu2inv((void *)&param[j]);
      }
      for (int j=0;j<nthreads;++j){
	void * ptr=(void *)&nthreads; // non-zero initialisation
	if (j<nthreads-1)
	  pthread_join(tab[j],&ptr);
      }
      done=true;
    }
#endif
    if (!done){
      thread_double_lu2inv_t tmp={&m,0,n,n,0,&lastnon0posv};
      do_thread_double_lu2inv((void*)&tmp);
    }
    // transpose, copy to first part, clear second part
    int twon=2*n;
    for (int i=0;i<n;++i){
      vector<giac_double> & mi=m[i];
      for (int j=n+i;j<twon;++j){
	swap_giac_double(mi[j],m[j-n][i+n]);
      }
      for (int j=0;j<n;++j)
	mi[j]=mi[perm[j]+n];
      mi.erase(mi.begin()+n,mi.end());
    }
    if (debug_infolevel)
      CERR << CLOCK() << " end lu2inv" << endl;
  }

  // int_linsolve_l and int_linsolve_u could be faster by solving simultaneously for
  // say 4 values of y

  // solve triangular system l*a=y where l is the lower part of a lu decomp in m[l..][c..]
  void int_linsolve_l(const vector< vector<int> > & m,int l,int c,const vector<int> & y,vector<int> & a,int p){
    // l*a=y: a1=y1, a2=y2-m_21*a1, ..., ak=yk-sum_{j=1..k-1}(m_kj*aj)
    int n=y.size();
    a.resize(n);
    int * astart=&a[0];
    *astart=y[0];
    for (int k=1;k<n;++k){
      const int * mkj=&m[k+l][c];
      int *aj=astart,*ak=astart+k;
      longlong res=y[k];
      for (;aj<ak;++mkj,++aj)
	res -= longlong(*mkj)*(*aj); 
      *ak=res % p;
    }
  }

  // solve triangular system l*a=y where l is the lower part of a lu decomp in m
  void int_linsolve_l4(const vector< vector<int> > & m,int l,int c,const vector<int> & y0,const vector<int> & y1,const vector<int> & y2,const vector<int> & y3,vector<int> & a0,vector<int> & a1,vector<int> & a2,vector<int> & a3,int p){
    int n=y0.size();
    a0.resize(n);
    a1.resize(n);
    a2.resize(n);
    a3.resize(n);
    int * a0start=&a0[0];
    *a0start=y0[0];
    int * a1start=&a1[0];
    *a1start=y1[0];
    int * a2start=&a2[0];
    *a2start=y2[0];
    int * a3start=&a3[0];
    *a3start=y3[0];
    for (int k=1;k<n;++k){
      const int * mkj=&m[k+l][c];
      int *a0j=a0start,*a0k=a0start+k;
      int *a1j=a1start,*a1k=a1start+k;;
      int *a2j=a2start,*a2k=a2start+k;;
      int *a3j=a3start,*a3k=a3start+k;;
      longlong res0=y0[k];
      longlong res1=y1[k];
      longlong res2=y2[k];
      longlong res3=y3[k];
      for (;a0j<a0k;++mkj,++a0j,++a1j,++a2j,++a3j){
	longlong tmp=*mkj;
	if (!tmp) continue;
	res0 -= tmp*(*a0j); 
	res1 -= tmp*(*a1j); 
	res2 -= tmp*(*a2j); 
	res3 -= tmp*(*a3j); 
      }
      *a0k=res0 % p;
      *a1k=res1 % p;
      *a2k=res2 % p;
      *a3k=res3 % p;
    }
  }

  // solve triangular system l*a=y where l is the lower part of a lu decomp in m[l..][c..]
  void double_linsolve_l(const matrix_double & m,int l,int c,const vector<double> & y,vector<double> & a){
    // l*a=y: a1=y1, a2=y2-m_21*a1, ..., ak=yk-sum_{j=1..k-1}(m_kj*aj)
    int n=y.size();
    a.resize(n);
    double * astart=&a[0];
    *astart=y[0];
    for (int k=1;k<n;++k){
      const double * mkj=&m[k+l][c];
      double *aj=astart,*ak=astart+k;
      double res=y[k];
      for (;aj<ak;++mkj,++aj)
	res -= (*mkj)*(*aj); 
      *ak=res;
    }
  }

  // solve triangular system l*a=y where l is the lower part of a lu decomp in m
  void double_linsolve_l4(const matrix_double & m,int l,int c,const vector<double> & y0,const vector<double> & y1,const vector<double> & y2,const vector<double> & y3,vector<double> & a0,vector<double> & a1,vector<double> & a2,vector<double> & a3){
    int n=y0.size();
    a0.resize(n);
    a1.resize(n);
    a2.resize(n);
    a3.resize(n);
    double * a0start=&a0[0];
    *a0start=y0[0];
    double * a1start=&a1[0];
    *a1start=y1[0];
    double * a2start=&a2[0];
    *a2start=y2[0];
    double * a3start=&a3[0];
    *a3start=y3[0];
    for (int k=1;k<n;++k){
      const double * mkj=&m[k+l][c];
      double *a0j=a0start,*a0k=a0start+k;
      double *a1j=a1start,*a1k=a1start+k;;
      double *a2j=a2start,*a2k=a2start+k;;
      double *a3j=a3start,*a3k=a3start+k;;
      double res0=y0[k];
      double res1=y1[k];
      double res2=y2[k];
      double res3=y3[k];
      for (;a0j<a0k;++mkj,++a0j,++a1j,++a2j,++a3j){
	double tmp=*mkj;
	if (!tmp) continue;
	res0 -= tmp*(*a0j); 
	res1 -= tmp*(*a1j); 
	res2 -= tmp*(*a2j); 
	res3 -= tmp*(*a3j); 
      }
      *a0k=res0 ;
      *a1k=res1 ;
      *a2k=res2 ;
      *a3k=res3 ;
    }
  }

  /*
  // solve triangular system L*A=Y where L is the lower part of a lu decomp in m[m_l..][m_c..]
  // Y are the columns of y[y_l..y_l+n-1,y_c...y_c+n-1]
  // A is written to a[a_l...a_l+n-1,a_c...a_c+n-1]
  // size of system is n, number of systems is s
  // &y may be equal to &a
  void int_linsolve_l(const vector< vector<int> > & m,int m_l,int m_c,const vector< vector<int> > & y,int y_l,int y_c,vector< vector<int> > & a,int a_l,int a_c,int s,int n,int p){
    if (n<60 || s<60 || s<n/2+30){ // vector by vector call
      vector<int> y0,y1,y2,y3,a0,a1,a2,a3;
      int i=0;
      for (;i<n-4;i+=4){
	// solve 4 systems
      }
      for (;i<n;++i){
	// solve remaining systems
      }
      return;
    }
    if (&y!=&a){ // first copy portion of y (Y3,Y4) to portion of a (A3,A4)
      // a=y;
    }
    vector< vector<int> > tmp;
    // bloc solve L=[[L1,0],[L2,L3]], A=[[A1,A2],[A3,A4]]
    // L*A=[[L1*A1,L1*A2],[L3*A3+L2*A1,L3*A4+L2*A2]]==[[Y1,Y2],[Y3,Y4]]
    // hence A1 and A2 are computed by recursive call L1*A1=Y1, L1*A2=Y2
    // then L3*A3=Y3-L2*A1, L3*A4=Y4-L2*A2
    // the intermediate Y3-L2*A1 and Y4-L2*A2 will be written in A3 and A4
    int n2=n/2,n3=n-n2,s3=s-n2;
    int_linsolve_l(m,m_l,m_c,a,a_l,a_c,a,a_l,a_c,n2,n2,p); // find A1
    int_linsolve_l(m,m_l,m_c,a,a_l+n2,a_c,a,a_l+n2,a_c,s3,n2,p); // find A2
    // transpose A1 to tmp
    // A3 -= L2*A1
    in_mmult_mod(m,tmp,a,a_l+n2,a_c,p,m_l+n2,m_l+n,m_c,m_c+n2,false);
    // find A3
    int_linsolve_l(m,m_l+n2,m_c+n2,a,a_l+n2,a_c,a,a_l+n2,a_c,n2,n3,p);
    // transpose A2 to tmp
    // A4 -= L2*A2
    in_mmult_mod(m,tmp,a,a_l+n2,a_c+n2,p,m_l+n2,m_l+n,m_c,m_c+n2,false);
    // find A4
    int_linsolve_l(m,m_l+n2,m_c+n2,a,a_l+n2,a_c+n2,a,a_l+n2,a_c+n2,s3,n3,p);
  }
  */

  // solve triangular system a*u=y where u is the upper part of a lu decomp in m
  // (like int_linsolve_l with transposed u)
  // the answer is temporarily stored in a vector<longlong> that should be coerced
  // to a vector<int> and put at the right place
  void int_linsolve_u(const vector< vector<int> > & m,int l,int c,const vector<int> & y,vector<longlong> & a,int p){
    // a_1=y_1/m_11, a_2=(y_2-m_12*a_1)/m_22, , ak=(yk-sum_{j<k} m_jk*aj)/m_kk
    int n=y.size();
    // initialize a to y
    longlong * astart=&a[0], * aend=astart+n;
    for (int i=0;i<n;++i)
      a[i]=y[i];
    for (int j=0;j<n;++j){
      // at step j, aj is known
      longlong * ak =astart+j; // it's aj
      longlong & aj=*ak;
      const int * mjk=&m[j+l][j+c];
      aj = ((aj % p) * invmod(*mjk,p)) %p;
      // aj is now computed, substract m_jk*aj from ak for all k>j
      for (++mjk,++ak;ak<aend;++mjk,++ak){
	*ak -= *mjk*aj;
      }
    }
  }

  void int_linsolve_u4(const vector< vector<int> > & m,int l,int c,const vector<int> & y0,const vector<int> & y1,const vector<int> & y2,const vector<int> & y3,vector<longlong> & a0,vector<longlong> & a1,vector<longlong> & a2,vector<longlong> & a3,int p){
    // a_1=y_1/m_11, a_2=(y_2-m_12*a_1)/m_22, , ak=(yk-sum_{j<k} m_jk*aj)/m_kk
    int n=y0.size();
    // initialize a to y
    longlong * a0start=&a0[0], * a0end=a0start+n;
    longlong * a1start=&a1[0];//, * a1end=a1start+n;
    longlong * a2start=&a2[0];//, * a2end=a2start+n;
    longlong * a3start=&a3[0];//, * a3end=a3start+n;
    for (int i=0;i<n;++i){
      a0[i]=y0[i];
      a1[i]=y1[i];
      a2[i]=y2[i];
      a3[i]=y3[i];
    }
    for (int j=0;j<n;++j){
      // at step j, aj is known
      longlong * a0k =a0start+j; // it's aj
      longlong & a0j=*a0k;
      longlong * a1k =a1start+j; // it's aj
      longlong & a1j=*a1k;
      longlong * a2k =a2start+j; // it's aj
      longlong & a2j=*a2k;
      longlong * a3k =a3start+j; // it's aj
      longlong & a3j=*a3k;
      const int * mjk=&m[j+l][j+c];
      int tmp=invmod(*mjk,p);
      a0j = ((a0j % p) * tmp) %p;
      a1j = ((a1j % p) * tmp) %p;
      a2j = ((a2j % p) * tmp) %p;
      a3j = ((a3j % p) * tmp) %p;
      // aj is now computed, substract m_jk*aj from ak for all k>j
      for (++mjk,++a0k,++a1k,++a2k,++a3k;a0k<a0end;++mjk,++a0k,++a1k,++a2k,++a3k){
	tmp=*mjk;
	if (!tmp) continue;
	*a0k -= tmp*a0j;
	*a1k -= tmp*a1j;
	*a2k -= tmp*a2j;
	*a3k -= tmp*a3j;
      }
    }
  }

  void double_linsolve_u(const matrix_double & m,int l,int c,const vector<double> & y,vector<double> & a){
    // a_1=y_1/m_11, a_2=(y_2-m_12*a_1)/m_22, , ak=(yk-sum_{j<k} m_jk*aj)/m_kk
    int n=y.size();
    // initialize a to y
    double * astart=&a[0], * aend=astart+n;
    for (int i=0;i<n;++i)
      a[i]=y[i];
    for (int j=0;j<n;++j){
      // at step j, aj is known
      double * ak =astart+j; // it's aj
      double & aj=*ak;
      const double * mjk=&m[j+l][j+c];
      aj = (aj) / (*mjk);
      // aj is now computed, substract m_jk*aj from ak for all k>j
      for (++mjk,++ak;ak<aend;++mjk,++ak){
	*ak -= *mjk*aj;
      }
    }
  }

  void double_linsolve_u4(const matrix_double & m,int l,int c,const vector<double> & y0,const vector<double> & y1,const vector<double> & y2,const vector<double> & y3,vector<double> & a0,vector<double> & a1,vector<double> & a2,vector<double> & a3){
    // a_1=y_1/m_11, a_2=(y_2-m_12*a_1)/m_22, , ak=(yk-sum_{j<k} m_jk*aj)/m_kk
    int n=y0.size();
    // initialize a to y
    double * a0start=&a0[0], * a0end=a0start+n;
    double * a1start=&a1[0];//, * a1end=a1start+n;
    double * a2start=&a2[0];//, * a2end=a2start+n;
    double * a3start=&a3[0];//, * a3end=a3start+n;
    for (int i=0;i<n;++i){
      a0[i]=y0[i];
      a1[i]=y1[i];
      a2[i]=y2[i];
      a3[i]=y3[i];
    }
    for (int j=0;j<n;++j){
      // at step j, aj is known
      double * a0k =a0start+j; // it's aj
      double & a0j=*a0k;
      double * a1k =a1start+j; // it's aj
      double & a1j=*a1k;
      double * a2k =a2start+j; // it's aj
      double & a2j=*a2k;
      double * a3k =a3start+j; // it's aj
      double & a3j=*a3k;
      const double * mjk=&m[j+l][j+c];
      double tmp=1/(*mjk);
      a0j = ((a0j) * tmp) ;
      a1j = ((a1j) * tmp) ;
      a2j = ((a2j) * tmp) ;
      a3j = ((a3j) * tmp) ;
      // aj is now computed, substract m_jk*aj from ak for all k>j
      for (++mjk,++a0k,++a1k,++a2k,++a3k;a0k<a0end;++mjk,++a0k,++a1k,++a2k,++a3k){
	tmp=*mjk;
	if (!tmp) continue;
	*a0k -= tmp*a0j;
	*a1k -= tmp*a1j;
	*a2k -= tmp*a2j;
	*a3k -= tmp*a3j;
      }
    }
  }

  // if m is in lu form (first n columns), compute l^-1 mod p, then solve u*inverse=l^-1
  void int_lu2inv(vector< vector<int> > & m,int p,const vector<int> & permu){
    int n=permu.size();
#if defined( VISUALC ) || defined( BESTA_OS )
    int * perm=(int *)alloca(n*sizeof(int)); // perminv(permu);
#else
    int perm[n];
#endif
    for (int j=0;j<n;j++){
      perm[permu[j]]=j;
    }    
    if (debug_infolevel)
      CERR << CLOCK() << " lu2inv begin n=" << n << endl;
    // first step compute l^-1 this is done by the recurrence: l*a=y: 
    // a1=y1, a2=y2-l_21*a1, ..., ak=yk-sum_{j=1..k-1}(l_kj*aj)
    // if y=(0,..,0,1,0,...0), 
    // a0=..=a_{i-1}=0 and we start at equation k=i+1 and sum_{j=i...}
    // n^3/6 operations
    int i=0;
    for (;i<=n-4;i+=4){
      int * col0=&m[i][n],* col1=&m[i+1][n],* col2=&m[i+2][n],* col3=&m[i+3][n];
      for (int k=0;k<i+4;++k){
	col0[k]=0;
	col1[k]=0;
	col2[k]=0;
	col3[k]=0;
      }
      col0[i]=1;
      col0[i+1]=-m[i+1][i];
      col0[i+2]=(-m[i+2][i]-longlong(col0[i+1])*m[i+2][i+1])%p;
      col0[i+3]=(-m[i+3][i]-longlong(col0[i+1])*m[i+3][i+1]-longlong(col0[i+2])*m[i+3][i+2])%p;
      col1[i+1]=1;
      col1[i+2]=-m[i+2][i+1];
      col1[i+3]=(-m[i+3][i+1]-longlong(col1[i+2])*m[i+3][i+2])%p;
      col2[i+2]=1;
      col2[i+3]=-m[i+3][i+2];
      col3[i+3]=1;
      for (int k=i+4;k<n;++k){
	longlong res0=0,res1=0,res2=0,res3=0;
	int * mkj=&m[k][i],*col0j=col0+i,*col1j=col1+i,*col2j=col2+i,*col3j=col3+i,*col0end=col0+k;
	for (;col0j<col0end;++mkj,++col3j,++col2j,++col1j,++col0j){
	  longlong tmp=(*mkj);
	  if (!tmp) continue;
	  res0 -= tmp*(*col0j); 
	  res1 -= tmp*(*col1j); 
	  res2 -= tmp*(*col2j); 
	  res3 -= tmp*(*col3j); 
	}
	*col0j=res0 % p;
	*col1j=res1 % p;
	*col2j=res2 % p;
	*col3j=res3 % p;
      }
    }
    for (;i<n;++i){
      int * col=&m[i][n];
      for (int k=0;k<i;++k)
	col[k]=0;
      col[i]=1;
      for (int k=i+1;k<n;++k){
	longlong res=0;
	int * mkj=&m[k][i],*colj=col+i,*colend=col+k;
	for (;colj<colend;++mkj,++colj)
	  res -= longlong(*mkj)*(*colj); 
	*colend=res % p;
      }
    }
    if (debug_infolevel)
      CERR << CLOCK() << " solving u*inv=l^-1" << endl;
    // second step, solve u*inverse=l^-1, columns of l^-1 are rows of m starting at col n
    // we compute a column of inverse by solving the system: 
    // u*col(inverse)=corresponding row of l^-1, and overwrite the row of l^-1 by solution
    // u*[x0,..,xn-1]=[a0,...,an]
    // x_{n-1}=a_{n-1}/u_{n-1,n-1}
    // x_{n-2}=(a_{n-2}-u_{n-2,n-1}*x_{n-1})/u_{n-2,n-2}
    // ...
    // x_k=(a_{k}-sum_{j=k+1..n-1} u_{k,j}x_j)/u_{k,k}
    // n^3/2 operations
    // the first i loop is unrolling
    i=0;
    for (;i<=n-4;i+=4){
      int * col0=&m[i][n],* col1=&m[i+1][n],* col2=&m[i+2][n],* col3=&m[i+3][n];
      for (int k=n-1;k>=0;--k){
	longlong res0=col0[k],res1=col1[k],res2=col2[k],res3=col3[k];
	int * mkj=&m[k][n-1],*col0j=col0+n-1,*colend=col0+k,*col1j=col1+n-1,*col2j=col2+n-1,*col3j=col3+n-1;
	for (;col0j>colend;--mkj,--col3j,--col2j,--col1j,--col0j){
	  longlong tmp=*mkj;
	  if (!tmp) continue;
	  res0 -= tmp*(*col0j);
	  res1 -= tmp*(*col1j);
	  res2 -= tmp*(*col2j);
	  res3 -= tmp*(*col3j);
	}
	int tmp=invmod(*mkj,p);
	*col0j=((res0%p)*tmp)%p;
	*col1j=((res1%p)*tmp)%p;
	*col2j=((res2%p)*tmp)%p;
	*col3j=((res3%p)*tmp)%p;
      }
    }
    for (;i<n;i++){
      int * col=&m[i][n];
      for (int k=n-1;k>=0;--k){
	longlong res=col[k];
	int * mkj=&m[k][n-1],*colj=col+n-1,*colend=col+k;
	for (;colj>colend;--mkj,--colj){
	  res -= longlong(*mkj)*(*colj);
	}
	*colj=((res % p)*invmod(*mkj,p))%p;
      }
    }
    // transpose, copy to first part, clear second part
    int twon=2*n;
    for (int i=0;i<n;++i){
      vector<int> & mi=m[i];
      for (int j=n+i;j<twon;++j){
	swapint(mi[j],m[j-n][i+n]);
      }
      for (int j=0;j<n;++j)
	mi[j]=mi[perm[j]+n];
      mi.erase(mi.begin()+n,mi.end());
    }
    if (debug_infolevel)
      CERR << CLOCK() << " end lu2inv" << endl;
  }

#endif // GIAC_HAS_STO_38


  int dotvecteur(const vecteur & a,const vecteur & b,int modulo){
    vecteur::const_iterator ita=a.begin(), itaend=a.end();
    vecteur::const_iterator itb=b.begin();
    int res=0;
    for (;ita!=itaend;++ita,++itb){
#ifdef _I386_
      mod(res,ita->val,itb->val,modulo);
#else
      res = (res + longlong(ita->val)*itb->val) % modulo; 
#endif
    }
    return res;
  }

  void multmatvecteur(const matrice & a,const vecteur & b,vecteur & res,int modulo){
    vecteur::const_iterator ita=a.begin(), itaend=a.end();
    res.clear();
    res.reserve(itaend-ita);
    for (;ita!=itaend;++ita)
      res.push_back(dotvecteur(*(ita->_VECTptr),b,modulo));
  }

  // v1=v1+c2*v2 smod modulo
  void modlinear_combination(vector<int> & v1,int c2,
			     const vector<int> & v2,int modulo,int cstart,int cend,bool pseudo){
    if (c2){
      vector<int>::iterator it1=v1.begin()+cstart,it1end=v1.end(),it1_;
      if (cend && cend>=cstart && cend<it1end-v1.begin())
	it1end=v1.begin()+cend;
      it1_=it1end-4;
      vector<int>::const_iterator it2=v2.begin()+cstart;
#if defined(PSEUDO_MOD) && !(defined(VISUALC) || defined (BESTA_OS) || defined(OSX) || defined(OSXIOS) || defined(FIR_LINUX) || defined(FIR_ANDROID) || defined(ANDROID))
      c2 %= modulo;
      if (pseudo && (modulo<(1<<29) 
		     // && modulo>=(1<<16)
		     )){
	int nbits=sizeinbase2(modulo);
	unsigned invmodulo=((1ULL<<(2*nbits)))/modulo+1;
	for (;it1!=it1end;++it1,++it2)
	  pseudo_mod(*it1,c2,*it2,modulo,invmodulo,nbits);
      }
      else
#endif // PSEUDO_MOD
	{
	  //longlong C2=c2;
	  for (;it1<it1_;){
#ifdef _I386_
	    // *it1=( (*it1) + (longlong) c2*(*it2)) % modulo ; // replace smod
	    mod(*it1,c2,*it2,modulo);
	    ++it1;
	    ++it2;
	    mod(*it1,c2,*it2,modulo);
	    ++it1;
	    ++it2;
	    mod(*it1,c2,*it2,modulo);
	    ++it1;
	    ++it2;
	    mod(*it1,c2,*it2,modulo);
	    ++it1;
	    ++it2;
#else
	    *it1=( (*it1) + longlong(c2)*(*it2)) % modulo ; // replace smod
	    ++it1;
	    ++it2;
	    *it1=( (*it1) + longlong(c2)*(*it2)) % modulo ; 
	    ++it1;
	    ++it2;
	    *it1=( (*it1) + longlong(c2)*(*it2)) % modulo ; 
	    ++it1;
	    ++it2;
	    *it1=( (*it1) + longlong(c2)*(*it2)) % modulo ; 
	    ++it1;
	    ++it2;
#endif
	  }
	  for (;it1!=it1end;++it1,++it2){
#ifdef _I386_
	    // *it1=( (*it1) + (longlong) c2*(*it2)) % modulo ; // replace smod
	    mod(*it1,c2,*it2,modulo);
#else
	    *it1=( (*it1) + longlong(c2)*(*it2)) % modulo ; // replace smod
#endif
	  }
	}
    }
  }

  // v1=v1+c2*v2
  void modlinear_combination(vector<longlong> & v1,int c2,const vector<longlong> & v2,int modulo,int cstart,int cend){
    if (c2){
      longlong * it1=&v1.front()+cstart,*it1end=&v1.front()+v1.size(),*it1_;
      if (cend && cend>=cstart && cend<it1end-&v1.front())
	it1end=&v1.front()+cend;
      it1_=it1end-4;
      const longlong * it2=&v2.front()+cstart;
      for (;it1<=it1_;it1+=4,it2+=4){
	*it1 += c2*(*it2);
	it1[1] += c2*it2[1];
	it1[2] += c2*it2[2];
	it1[3] += c2*it2[3];
      }
      for (;it1!=it1end;++it1,++it2){
	*it1 += c2*(*it2);
      }
    }
  }

  void matrice2std_matrix_gen(const matrice & m,std_matrix<gen> & M){
    int n=int(m.size());
    M.clear();
    M.reserve(n);
    for (int i=0;i<n;++i)
      M.push_back(*m[i]._VECTptr);
  }

  void std_matrix_gen2matrice(const std_matrix<gen> & M,matrice & m){
    int n=int(M.size());
    m.clear();
    m.reserve(n);
    for (int i=0;i<n;++i)
      m.push_back(M[i]);
  }

  bool vecteur2index(const vecteur & v,index_t & i){
    i.clear();
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type!=_INT_)
	return false;
      i.push_back(it->val);
    }
    return true;
  }

  static void print_debug_info(const gen & pivot){
    if ( (pivot.type==_POLY) && !pivot._POLYptr->coord.empty())
      CERR << "poly(" << sum_degree(pivot._POLYptr->coord.front().index) << "," << pivot._POLYptr->coord.size() << ") ";
    else
      CERR << pivot << " ";
  }

  bool is_mod_vecteur(const vecteur & m,vector<int> & v,int & p){
    v.clear();
    v.reserve(m.size());
    const_iterateur it=m.begin(),itend=m.end();
    for (;it!=itend;++it){
      if (it->type==_MOD){
	if (!p)
	  p=(it->_MODptr+1)->val;
	if (*(it->_MODptr+1)!=p)
	  return false;
	v.push_back(it->_MODptr->val);
	continue;
      }
      if (it->is_symb_of_sommet(at_normalmod)){
	const gen & f=it->_SYMBptr->feuille;
	if (f.type!=_VECT || f._VECTptr->size()!=2 || f._VECTptr->front().type!=_INT_ || f._VECTptr->back().type!=_INT_)
	  return false;
	if (!p)
	  p=f._VECTptr->back().val;
	if (f._VECTptr->back().val!=p)
	  return false;
	v.push_back(f._VECTptr->front().val);
	continue;
      }
      if (it->type!=_INT_) return false;
      v.push_back(it->val);
    }
    return true;
  }

  bool is_mod_matrice(const matrice & m,vector< vector<int> > & M,int & p){
    const_iterateur it=m.begin(),itend=m.end();
    M.clear();
    M.reserve(m.size());
    for (;it!=itend;++it){
      M.push_back(vector<int>(0));
      if (it->type!=_VECT || !is_mod_vecteur(*it->_VECTptr,M.back(),p)) 
	return false;
    }
    return true;
  }

  bool is_integer_vecteur(const vecteur & m,bool intonly){
    const_iterateur it=m.begin(),itend=m.end();
    for (;it!=itend;++it){
      if (it->type==_INT_) continue;
      if (intonly) return false;
      if (it->type==_ZINT) continue;
      if (it->type==_CPLX && is_integer(*it->_CPLXptr) && is_exactly_zero(*(it->_CPLXptr+1))) continue;
      return false;
      // if (!is_integer(*it)) return false;
    }
    return true;
  }

  bool is_integer_matrice(const matrice & m,bool intonly){
    const_iterateur it=m.begin(),itend=m.end();
    for (;it!=itend;++it)
      if (it->type!=_VECT || !is_integer_vecteur(*it->_VECTptr,intonly)) return false;
    return true;
  }

  bool is_fraction_vecteur(const vecteur & m){
    const_iterateur it=m.begin(),itend=m.end();
    for (;it!=itend;++it)
      if (it->type!=_FRAC && !is_integer(*it)) return false;
    return true;
  }

  bool is_fraction_matrice(const matrice & m){
    const_iterateur it=m.begin(),itend=m.end();
    for (;it!=itend;++it)
      if (it->type!=_VECT || !is_fraction_vecteur(*it->_VECTptr)) return false;
    return true;
  }

  gen modproduct(const vecteur & v, const gen & modulo){
    const_iterateur it=v.begin(),itend=v.end();
    gen res(1);
    for (;it!=itend;++it){
      res = smod(res * (*it),modulo);
    }
    return res;
  }

  static gen untrunc1(const gen & g){
    if (g.type==_FRAC)
      return fraction(untrunc1(g._FRACptr->num),untrunc1(g._FRACptr->den));
    return g.type==_POLY?g._POLYptr->untrunc1():g;
  }

  vecteur fracmod(const vecteur & v,const gen & modulo){
    const_iterateur it=v.begin(),itend=v.end();
    vecteur res;
    res.reserve(itend-it);
    for (;it!=itend;++it){
      if (it->type==_VECT)
	res.push_back(fracmod(*it->_VECTptr,modulo));
      else
	res.push_back(fracmod(*it,modulo));
    }
    return res;
  }

  // Find next prime not dividing primewith
  gen nextp(const gen &p0,const gen & primewith){
    for (gen p=p0;;){
      p=nextprime(p+1);
      if (is_one(gcd(primewith,p))) // keep p prime with invariant factor (divisor of det)
	return p;
    }
  }

  struct thread_modrref_t {
    const matrice * aptr;
    vector< vector<int> > * Nptr;
    matrice * resptr;
    vecteur * pivotsptr;
    smallmodrref_temp_t * workptr;
    gen det,mult_by_det_mod_p;
    int l,lmax,c,cmax,fullreduction,dont_swap_below,Modulo,carac,rref_or_det_or_lu;
    bool inverting,no_initial_mod,success;
  };

  void * thread_modrref(void * ptr_){
    thread_modrref_t * ptr = (thread_modrref_t *)(ptr_);
    ptr->success=in_modrref(*ptr->aptr, *ptr->Nptr,*ptr->resptr, *ptr->pivotsptr, ptr->det,ptr->l, ptr->lmax, ptr->c,ptr->cmax,ptr->fullreduction,ptr->dont_swap_below,ptr->Modulo,ptr->carac,ptr->rref_or_det_or_lu,ptr->mult_by_det_mod_p,ptr->inverting,ptr->no_initial_mod,ptr->workptr);
    return ptr;
  }

#ifndef CLOCKS_PER_SEC
#define CLOCKS_PER_SEC 1e6
#endif
  
#ifndef GIAC_HAS_STO_38
  static int mrref_int(const matrice & a, matrice & res, vecteur & pivots, gen & det,int l, int lmax, int c,int cmax,
			int fullreduction,int dont_swap_below,bool convert_internal,int algorithm,int rref_or_det_or_lu,
			int modular,vector<int> & permutation,
			GIAC_CONTEXT){
    gen linfa=linfnorm(a,contextptr);
    unsigned as=a.size();//,a0s=a.front()._VECTptr->size();
    res.clear(); // insure that res will be build properly
    // Modular algorithm for matrix integer reduction
    // Find Hadamard bound
    if (debug_infolevel>1)
      CERR << "rref padic/modular " << CLOCK() << endl;
    bool inverting=fullreduction==2;
    gen h2=4*square_hadamard_bound(a),h20=h2;
    if (debug_infolevel>1)
      CERR << "rref padic hadamard done " << CLOCK() << endl;
    gen p,det_mod_p,pi_p;
    int done=0;
    bool failure=false;
    gen factdet(1); // find a divisor of the determinant
    // by solving a random linear system having a as matrix
    // using a p-adic method 
#if 1 // def _I386_
    double p0=3037000500./std::sqrt(double(as))/5.; // so that p0^2*rows(a)<2^63
#else
    double p0=46340./std::sqrt(double(as))/5.; // so that p0^2*rows(a)<2^31
#endif
    gen ainf=linfnorm(a,context0);
    if (is_zero(ainf)){
      res=a; det=0; return 1;
    }
    if (ainf.type==_INT_){ // insure that ||a||_inf*p*rows(a)<2^63
      double p1=((((ulonglong) 1)<<63)/ainf.val)/as;
      if (p1<p0)
	p0=p1*0.99; // since we make a nextprime...
    }
    else { // insure that p^2*rows(a)*(2+ln(||a||_inf)/ln(p))<2^63
      double n=std::ceil(mpz_sizeinbase(*ainf._ZINTptr,2)/21.); // assumes p>2^21
      double p1=std::sqrt((1ULL << 62)/(n+2)/as);
      if (p1<(1<<21))
	failure=true;
      if (p1<p0)
	p0=p1*.9;
    }
    p=nextprime(int(p0));
    vector< vector<int> > N;
    if (!failure && modular==2){ // rref is like linsolve
      matrice A(mtran(a));
      vecteur b=*A.back()._VECTptr,x;
      A.pop_back();
      A=mtran(A);
      int done=padic_linsolve(A,b,x,p,det,h2);
      if (done>0){
	res=midn(as);
	res.push_back(x);
	res=mtran(res);
	return 1;
      }
      failure=true;
    }
    if (!failure && (as>=GIAC_PADIC || algorithm==RREF_PADIC)){
      vecteur b(vranm(as,8,contextptr)),resb;
      // reconstruct (at most) 12 components of res for lcm
      // this should give the last invariant factor (estimated proba 0.998)
      if ( (done=padic_linsolve(a,b,resb,p,det,h2,inverting?12:6)) ){ 
	if (done==-1){
	  det=0;
	  return 1;
	}
	lcmdeno(resb,factdet,contextptr);
	if (debug_infolevel>2)
	  CERR << "lif=" << factdet << endl;
	h2=iquo(h2,factdet*factdet)+1;
	det=smod(det*invmod(factdet,p),p);
	pi_p=p;
      }
    }
#ifdef GIAC_DETBLOCK
    p=nextp(int(536870923./std::sqrt(double(mmult_int_blocksize))),factdet);
#else
    p=nextp(536870923,factdet); 
#endif
#ifdef HAVE_LIBPTHREAD
    // initialize/alloc nthreads-1 copies of N, res, pivots
    int nthreads=threads_allowed?threads:1;
    pthread_t tab[nthreads-1];
#ifdef __clang__
    vector< vector<int> > *Nptr = (vector< vector<int> > *)alloca((nthreads-1)*sizeof(vector< vector<int> >));
    matrice *resptr = (matrice *)alloca((nthreads-1)*sizeof(matrice));
    vecteur *pivotsptr = (vecteur *)alloca((nthreads-1)*sizeof(vecteur));
    smallmodrref_temp_t *work = (smallmodrref_temp_t *)alloca(nthreads*sizeof(smallmodrref_temp_t));
#else
    vector< vector<int> > Nptr[nthreads-1];
    matrice resptr[nthreads-1];
    vecteur pivotsptr[nthreads-1];
    smallmodrref_temp_t work[nthreads];
#endif
    for (int i=0;i<nthreads;++i){
#ifdef __clang__
      new (&work[i]) smallmodrref_temp_t();
#endif
      work[i].Ainv=vector< vector<int> >(mmult_int_blocksize,vector<int>(2*mmult_int_blocksize));
      work[i].Ainvtran=vector< vector<int> >(mmult_int_blocksize,vector<int>(mmult_int_blocksize));
      work[i].CAinv=vector< vector<int> >(mmult_int_blocksize,vector<int>(mmult_int_blocksize));
      work[i].pivblock.reserve(mmult_int_blocksize+1);
    }
#ifdef __clang__
    thread_modrref_t *modrrefparam = (thread_modrref_t *)alloca((nthreads-1)*sizeof(thread_modrref_t));
#else
    thread_modrref_t modrrefparam[nthreads-1];
#endif
    for (int i=0;i<nthreads-1;++i){
      Nptr[i]=vector< vector<int> >(a.size(),vector<int>(cmax));
      resptr[i]=matrice(a.size());
      for (unsigned j=0;j<a.size();j++)
	resptr[i][j]=vecteur(cmax);
      pivotsptr[i]=pivots;
      pivotsptr[i].reserve(a.size());
      thread_modrref_t tmp={&a,&Nptr[i],&resptr[i],&pivotsptr[i],&work[i],0,1,l,lmax,c,cmax,fullreduction,dont_swap_below,0,-1,rref_or_det_or_lu,inverting,false,false};
      modrrefparam[i]=tmp;
    }
#endif
    if (!failure){
      double proba=1.0;
      if (!done){
	pi_p=p;
	if (!in_modrref(a,N,res,pivots,det,l,lmax,c,cmax,
			0 /* fullreduction */,dont_swap_below,p.val,-1/* carac*/,1 /* det */,1 /* mult by 1*/,false/* inverting */,true/* no initial mod */,
#ifdef HAVE_LIBPTHREAD
			&work[nthreads-1]
#else
			0
#endif
			)
	    )
	  return 0;
      }
      // First find det to avoid bad primes
      int initial_clock=CLOCK();
      int dbglevel=debug_infolevel;
      for (;is_strictly_greater(h2,pi_p*pi_p,contextptr);){
#ifdef HAVE_LIBPTHREAD
	for (int j=0;j<nthreads-1;j++){
	  p=nextp(p+1,factdet);
	  modrrefparam[j].cmax=cmax;
	  modrrefparam[j].Modulo=p.val;
	  modrrefparam[j].fullreduction=0;
	  modrrefparam[j].rref_or_det_or_lu=1;
	  modrrefparam[j].inverting=false;
	  modrrefparam[j].no_initial_mod=true;
	  modrrefparam[j].mult_by_det_mod_p=1;
	  bool res=pthread_create(&tab[j],(pthread_attr_t *) NULL,thread_modrref,(void *) &modrrefparam[j]);
	  if (res)
	    thread_modrref((void *)&modrrefparam[j]);	    
	}
#endif
	p=nextp(p+1,factdet);
	gen current_estimate=evalf_double(_evalf(gen(makevecteur(200*ln(pi_p,contextptr)/ln(h2,contextptr),20),_SEQ__VECT),contextptr),1,contextptr);
	if (as>10 && dbglevel<2 && CLOCK()-initial_clock>min_proba_time*CLOCKS_PER_SEC)
	  dbglevel=2;
	if (as>10 && dbglevel>1){
	  CERR << CLOCK() << " detrref, % done " << current_estimate << ", prime " << p << (proba<1e-10?" stable":" unstable");
	  if (dbglevel>3)
	    CERR << ", det/lif=" << det ;
	  CERR << endl;
	}
	if (!in_modrref(a,N,res,pivots,det_mod_p,l,lmax,c,cmax,
			0 /* fullreduction */,dont_swap_below,p.val,-1/*carac*/,1 /* det */,1 /* mult by 1*/,false /* inverting */,true/* no initial mod */,
#ifdef HAVE_LIBPTHREAD
			&work[nthreads-1]
#else
			0
#endif
			)){
	  // FIXME clean launched threads
	  return 0;
	}
	if (dbglevel>2)
	  CERR << CLOCK() << " end rref " << endl;
#ifdef HAVE_LIBPTHREAD
	// get back launched mod det
	for (int j=0;j<nthreads-1;++j){
	  void * ptr;
	  pthread_join(tab[j],&ptr);
	  if (ptr && modrrefparam[j].success){
	    gen tmpp=modrrefparam[j].Modulo;
	    gen tmpdet_mod_p=smod(modrrefparam[j].det*invmod(factdet,tmpp),tmpp);
	    gen old_det=det;
	    det=ichinrem(det,tmpdet_mod_p,pi_p,tmpp);
	    pi_p=pi_p*tmpp;
	    if (old_det==det)
	      proba=proba/evalf_double(p,1,contextptr)._DOUBLE_val;
	    else
	      proba=1.0;
	  }
	}
#endif
	det_mod_p=smod(det_mod_p*invmod(factdet,p),p);
	gen old_det=det;
	det=ichinrem(det,det_mod_p,pi_p,p);
	pi_p=pi_p*p;
	if (old_det==det)
	  proba=proba/evalf_double(p,1,contextptr)._DOUBLE_val;
	else
	  proba=1.0;
	if (proba<proba_epsilon(contextptr) && is_greater(70,current_estimate,contextptr) && CLOCK()-initial_clock>min_proba_time*CLOCKS_PER_SEC)
	  break;
      } // end loop h2>pi_p^2
      det=smod(det,pi_p)*factdet;
      if (rref_or_det_or_lu==1){
	if (is_strictly_greater(h2,pi_p*pi_p,contextptr))
	  *logptr(contextptr) << gettext("Probabilistic algorithm for determinant\n(run proba_epsilon:=0 for a deterministic answer, this is slower).\nError probability is less than ") << proba << endl;
	return 1;
      }
      h2=h20;
      if (is_zero(det,contextptr))
	failure=true;
    }
    if (!failure){
      // Improve: currently permutation should always be the idn for lu
      // instead of by det (det works for rref)
      if (rref_or_det_or_lu==2){
	rref_or_det_or_lu=3;
	h2=h2*h2; // need to square for LU decomp (rational reconstruction)
      }
      if (inverting){
	fullreduction=0;
	rref_or_det_or_lu=2;
	cmax=lmax;
	p=nextprime(p+1);
      }
      else {
	// Now do the reduction again, avoiding bad primes
#if 1
	p=536870923;
#else
	p=36007;
#endif
      }
      gen q;
      while (is_zero(irem(det,p,q),contextptr))
	p=nextprime(p+1);
      pi_p=p;
      gen det1;
      if (!in_modrref(a,N,res,pivots,det1,l,lmax,c,cmax,
		      fullreduction,dont_swap_below,p.val,-1/*carac*/,rref_or_det_or_lu,(inverting || rref_or_det_or_lu==0)?det:1,true /* inverting */,true/* no initial mod */,0/*workptr*/))
	return 0;
#if 1
      // uncoerce elements of res and prealloc size of integers
      // might perhaps improve chinese remaindering by divide and conquer?
      unsigned prealloc=h2.type==_ZINT?mpz_sizeinbase(*h2._ZINTptr,2)/2:128;
      for (unsigned i=0;i<res.size();++i){
	iterateur it=res[i]._VECTptr->begin(),itend=res[i]._VECTptr->end();
	for (;it!=itend;++it)
	  uncoerce(*it,prealloc);
      }
#endif
      // Multiply res by product of pivots in order to have the det
      // as initial non-zero element of each line after the reduction
      // if (rref_or_det_or_lu==0) res=smod(multvecteur(det,res),p);
      matrice res_mod_p,pivots_mod_p;
      for (;is_strictly_greater(h2,pi_p*pi_p,contextptr);){
#ifdef HAVE_LIBPTHREAD
	for (int j=0;j<nthreads-1;j++){
	  p=nextprime(p+1);	    
	  while (is_zero(irem(det,p,q),contextptr))
	    p=nextprime(p+1);
	  if (p.type!=_INT_)
	    break;
	  modrrefparam[j].cmax=cmax;
	  modrrefparam[j].Modulo=p.val;
	  modrrefparam[j].fullreduction=fullreduction;
	  modrrefparam[j].rref_or_det_or_lu=rref_or_det_or_lu;
	  modrrefparam[j].inverting=true;
	  modrrefparam[j].no_initial_mod=true;
	  gen tmp=(inverting || rref_or_det_or_lu==0)?det:1;
	  modrrefparam[j].mult_by_det_mod_p=tmp;
	  bool res=pthread_create(&tab[j],(pthread_attr_t *) NULL,thread_modrref,(void *) &modrrefparam[j]);
	  if (res)
	    thread_modrref((void *)&modrrefparam[j]);	    
	}
#endif
	p=nextprime(p+1);
	while (is_zero(irem(det,p,q),contextptr))
	  p=nextprime(p+1);
	if (p.type!=_INT_)
	  break;
	if (!in_modrref(a,N,res_mod_p,pivots_mod_p,det_mod_p,l,lmax,c,cmax,
			fullreduction,dont_swap_below,p.val,-1/*carac*/,rref_or_det_or_lu,(inverting || rref_or_det_or_lu==0)?det:1,true /* inverting */,true/* no initial mod */,0/*workptr*/))
	  return 0;
#ifdef HAVE_LIBPTHREAD
	// get back launched mod det
	for (int j=0;j<nthreads-1;++j){
	  void * ptr;
	  pthread_join(tab[j],&ptr);
	  if (ptr && modrrefparam[j].success){
	    if (rref_or_det_or_lu==3 && is_zero(det_mod_p,contextptr)){
	      continue;
	    }
	    gen tmpp=modrrefparam[j].Modulo;
	    ichinrem_inplace(res,*modrrefparam[j].resptr,pi_p,tmpp.val,fullreduction);
	    if (fullreduction!=2 && !inverting)
	      pivots=*ichinrem(gen(pivots),gen(*modrrefparam[j].pivotsptr),pi_p,tmpp)._VECTptr;
	    pi_p=pi_p*tmpp;
	  }
	}
#endif
	if (as>10 && debug_infolevel>1)
	  CERR << CLOCK() << " modrref, % done " << evalf_double(_evalf(gen(makevecteur(200*ln(pi_p,contextptr)/ln(h2,contextptr),20),_SEQ__VECT),contextptr),1,contextptr)<< ", prime " << p << endl;
	if (rref_or_det_or_lu==3){
	  if (is_zero(det_mod_p,contextptr))
	    continue;
	}
	/*
	  else {
	  multvecteur(smod(det,p),res_mod_p,res_mod_p);
	  smod(res_mod_p,p,res_mod_p);
	  // res_mod_p=smod(multvecteur(smod(det,p),res_mod_p),p);
	  }
	*/
#if 0
	res=*ichinrem(gen(res),gen(res_mod_p),pi_p,p)._VECTptr;
#else
	ichinrem_inplace(res,res_mod_p,pi_p,p.val,fullreduction);
#endif
	if (fullreduction!=2 && !inverting)
	  pivots=*ichinrem(gen(pivots),gen(pivots_mod_p),pi_p,p)._VECTptr;
	pi_p=pi_p*p;
	if (inverting){
	  // early termination if abs(det*2)<pi_p and linfnorm(res)*linfnorm(original_matrix)*size*2<pi_p
	  // smod_inplace(res,pi_p);
	  if (is_greater(pi_p,2*abs(det,contextptr),contextptr) && is_greater(pi_p,2*linfnorm(res,contextptr)*linfa,contextptr)){
	    if (debug_infolevel>2)
	      *logptr(contextptr) << CLOCK() << gettext(" Early termination") << endl;
	    break;
	  }
	}
      } // end for loop on primes
      if (p.type==_INT_){
	// there is a bug in libtommath when multiplying a _ZINT by an int
	// because used memory might grow by 2, not only by 1
	// in bn_mp_mul_d.c
	smod_inplace(res,pi_p);
	if (inverting){
	  // This step could perhaps be a little faster if we keep
	  // the last invariant factor (as computed by the p-adic algorihtm)
	  // since the denominator is likely (about 6/pi^2) to be the lif
	  // therefore we could compute divisor=det/lif and test divisibility
	  // of res by divisor
	  if (debug_infolevel>2)
	    *logptr(contextptr) << CLOCK() << gettext(" dividing by determinant") << endl;
	  divvecteur(res,det,res);
	  if (debug_infolevel>2)
	    *logptr(contextptr) << CLOCK() << gettext(" end dividing by determinant") << endl;
	}
	else
	  pivots=smod(pivots,pi_p);
	if (rref_or_det_or_lu==3) // rational reconstruction
	  res=fracmod(res,pi_p);
	if (rref_or_det_or_lu==2 || rref_or_det_or_lu == 3){
	  vecteur P;
	  vector_int2vecteur(permutation,P);
	  pivots.push_back(P);
	}
	return inverting?2:1;
      } // end if p.type==_INT_
    } // end if !failure
    return -1;
  } // end modular/padic algorithm
#endif // GIAC_HAS_STO_38

  // find lvar after doing halftan/tsimplify
  void alg_lvar_halftan_tsimplify(vecteur & res,vecteur & lv,GIAC_CONTEXT){
    lv=alg_lvar(res);
    if (!lv.empty() && lv.front().type==_VECT && lv.front()._VECTptr->size()>1){
      vecteur lw=*halftan(lv.front(),contextptr)._VECTptr;
      if (lvar(lw).size()<lv.front()._VECTptr->size()){
	res=*subst(gen(res),lv.front(),lw,false,contextptr)._VECTptr;
	lv=alg_lvar(res);
      }
      if (!lv.empty() && lv.front().type==_VECT && lv.front()._VECTptr->size()>1){
	lw=*tsimplify(lv.front(),contextptr)._VECTptr;
	if (lvar(lw).size()<lv.front()._VECTptr->size()){
	  res=*subst(gen(res),lv.front(),lw,false,contextptr)._VECTptr;
	  lv=alg_lvar(res);
	}
      }
    }
  }

  int rref_reduce(std_matrix<gen> &M,vecteur & pivots,vector<int> & permutation,gen & det,gen &detnum,int algorithm,int l,int lmax,int c,int cmax,int dont_swap_below,int rref_or_det_or_lu,int fullreduction,double eps,bool step_rref,const vecteur &lv,bool convert_internal,GIAC_CONTEXT){
    int linit=l;
    gen bareiss (1),invbareiss(1),pivot,temp;
    int pivotline,pivotcol;
    matrice res;
    int status=2;
    for (;(l<lmax) && (c<cmax);){
#ifdef TIMEOUT
      control_c();
#endif
      if (ctrl_c || interrupted)
	return 0;
      if ( (!fullreduction) && (l==lmax-1) )
	break;
      if (debug_infolevel>2)
	CERR <<  "// mrref line " << l << ":" << CLOCK() <<endl;
      pivot=M[l][c];
      if (debug_infolevel>2){
	CERR << "// ";
	print_debug_info(pivot);
      }
      pivotline=l;
      pivotcol=c;
      if (l<dont_swap_below){ // scan current line for the best pivot available
	for (int ctemp=c+1;ctemp<cmax;++ctemp){
	  temp=M[l][ctemp];
	  if (debug_infolevel>2)
	    print_debug_info(temp);
	  if (!is_zero(temp,contextptr) && temp.islesscomplexthan(pivot)){
	    pivot=temp;
	    pivotcol=ctemp;
	  }
	}	
      }
      else {      // scan M current column for the best pivot available
	if (rref_or_det_or_lu == 3){ // LU without line permutation
	  if (is_zero(pivot,contextptr)){
	    det = 0;
	    vecteur P;
	    vector_int2vecteur(permutation,P);
	    pivots.push_back(P);
	    return 1;
	  }
	}
	else {
	  for (int ltemp=l+1;ltemp<lmax;++ltemp){
	    temp=M[ltemp][c];
	    if (debug_infolevel>2)
	      print_debug_info(temp);
	    if (!is_zero(temp,contextptr) && temp.islesscomplexthan(pivot)){
	      pivot=temp;
	      pivotline=ltemp;
	    }
	  }
	}
      }
      if (debug_infolevel>2)
	CERR << endl;
      //COUT << M << endl << pivot << endl;
      if (!is_zero(pivot,contextptr)){
	if (step_rref){
	  std_matrix_gen2matrice(M,res);
	  gen pivot1=pivot;
	  if (convert_internal){
	    res = *r2sym(res,lv,contextptr)._VECTptr;
	    pivot1 = r2sym(pivot1,lv,contextptr);
	  }
	  gprintf(step_rrefpivot,gettext("%gen\nReduce column %gen with pivot %gen at row %gen"),makevecteur(res,c+1,pivot1,pivotline+1),contextptr);
	}
	// exchange lines if needed
	if (l!=pivotline){
	  if (step_rref){
	    std_matrix_gen2matrice(M,res);
	    if (convert_internal)
	      res = *r2sym(res,lv,contextptr)._VECTptr;
	    gprintf(step_rrefexchange,gettext("Exchange row %gen and row %gen"),makevecteur(l+1,pivotline+1),contextptr);
	  }
	  swap(M[l],M[pivotline]);
	  swap(permutation[l],permutation[pivotline]);
	  // temp = M[l];
	  // M[l] = M[pivotline];
	  // M[pivotline] = temp;
	  detnum = -detnum;
	}
	// make the reduction
	if (fullreduction){ // should be done after for efficiency
	  for (int ltemp=linit;ltemp<lmax;++ltemp){
	    if (debug_infolevel>=2)
	      CERR << "// " << l << "," << ltemp << " "<< endl;
	    if (step_rref && l!=ltemp){
	      std_matrix_gen2matrice(M,res);
	      gen coeff1=pivot,coeff2=M[ltemp][pivotcol];
	      if (algorithm==RREF_GAUSS_JORDAN){
		coeff1=1; coeff2=coeff2/pivot;
	      }
	      if (convert_internal){
		res = *r2sym(res,lv,contextptr)._VECTptr;
		coeff1=r2sym(coeff1,lv,contextptr);
		coeff2=r2sym(coeff2,lv,contextptr);
	      }
	      gprintf(step_rrefpivot0,gettext("L%gen <- (%gen)*L%gen-(%gen)*L%gen on %gen"),makevecteur(ltemp+1,coeff1,ltemp+1,coeff2,l+1,res),contextptr);
	    }
	    if (ltemp!=l){
	      if (algorithm!=RREF_GAUSS_JORDAN) // M[ltemp] = rdiv( pivot * M[ltemp] - M[ltemp][pivotcol]* M[l], bareiss);
		linear_combination(pivot,M[ltemp],-M[ltemp][pivotcol],M[l],bareiss,invbareiss,M[ltemp],eps,0);
	      else // M[ltemp]=M[ltemp]-rdiv(M[ltemp][pivotcol],pivot)*M[l];
		linear_combination(plus_one,M[ltemp],-rdiv(M[ltemp][pivotcol],pivot,contextptr),M[l],plus_one,plus_one,M[ltemp],eps,0);
	    }
	  }
	}
	else { // subdiagonal reduction
	  for (int ltemp=l+1;ltemp<lmax;++ltemp){
	    if (debug_infolevel>=2)
	      CERR << "// " << l << "," << ltemp << " "<< endl;
	    if (step_rref){
	      std_matrix_gen2matrice(M,res);
	      gen coeff1=pivot,coeff2=M[ltemp][pivotcol];
	      if (algorithm==RREF_GAUSS_JORDAN){
		coeff1=1; coeff2=coeff2/pivot;
	      }
	      if (convert_internal){
		res = *r2sym(res,lv,contextptr)._VECTptr;
		coeff1=r2sym(coeff1,lv,contextptr);
		coeff2=r2sym(coeff2,lv,contextptr);
	      }
	      gprintf(step_rrefpivot0,gettext("L%gen <- (%gen)*L%gen-(%gen)*L%gen on %gen"),makevecteur(ltemp+1,coeff1,ltemp+1,coeff2,l+1,res),contextptr);
	    }
	    if (algorithm!=RREF_GAUSS_JORDAN)
	      linear_combination(pivot,M[ltemp],-M[ltemp][pivotcol],M[l],bareiss,invbareiss,M[ltemp],eps,(c+1)*(rref_or_det_or_lu>0));
	    else {
	      gen coeff=M[ltemp][pivotcol]/pivot;
	      linear_combination(plus_one,M[ltemp],-coeff,M[l],plus_one,plus_one,M[ltemp],eps,(c+1)*(rref_or_det_or_lu>0));
	      if (rref_or_det_or_lu==2 || rref_or_det_or_lu == 3){
		M[ltemp][pivotcol]=0;
		M[ltemp][l]=coeff; // pivotcol replaced by l
	      }
	    }
	  }
	  if (rref_or_det_or_lu==1 && algorithm!=RREF_GAUSS_JORDAN) {
	    if (debug_infolevel>2)
	      CERR << "//mrref clear line " << l << endl;
	    // clear pivot line to save memory
	    M[l].clear();
	  }
	} // end else
	// COUT << M << endl;
	// increment column number if swap was allowed
	if (l>=dont_swap_below)
	  ++c;
	// increment line number since reduction has been done
	++l;	  
	// multiply det
	// set new bareiss for next reduction round
	if (algorithm!=RREF_GAUSS_JORDAN){
	  bareiss=pivot;
	  if (bareiss.type==_EXT || bareiss.type==_USER)
	    invbareiss=inv(bareiss,contextptr);
	  else
	    invbareiss=1;
	}
	// save pivot for annulation test purposes
	if (rref_or_det_or_lu!=1){
	  if (convert_internal)
	    pivots.push_back(r2sym(pivot,lv,contextptr));
	  else
	    pivots.push_back(pivot);
	  if (debug_infolevel>2)
	    CERR << pivots.back() << endl;
	}
      }
      else { // if pivot is 0 increment either the line or the col
	status=3;
	if (rref_or_det_or_lu==1){
	  det=0;
	  return 1;
	}
	if (l>=dont_swap_below)
	  c++;
	else
	  l++;
      }
    } // end for reduction loop
    return status;
  }

  // row reduction from line l and column c to line lmax and column cmax
  // lmax and cmax are not included
  // line are numbered starting from 0
  // if fullreduction is false, reduction occurs under the diagonal only
  // if dont_swap_below !=0, for line numers < dont_swap_below
  // the pivot is searched in the line instead of the column
  // hence no line swap occur
  // convert_internal=false if we do not want conversion to rational fractions
  // algorithm=0 Gauss-Jordan, 1 guess, 2 Bareiss, 3 modular, 4 p-adic, 5 interp
  // rref_or_det_or_lu = 0 for rref, 1 for det, 2 for lu, 
  // 3 for lu without pemutation
  int mrref(const matrice & a, matrice & res, vecteur & pivots, gen & det,int l, int lmax, int c,int cmax,
	    int fullreduction_,int dont_swap_below,bool convert_internal,int algorithm_,int rref_or_det_or_lu,
	    GIAC_CONTEXT){
    if (!ckmatrix(a))
      return 0;
    double eps=epsilon(contextptr);
    unsigned as=unsigned(a.size()),a0s=unsigned(a.front()._VECTptr->size());
    bool step_rref=false;
    int algorithm=algorithm_;
    int fullreduction=fullreduction_;
    if (fullreduction<0)
      fullreduction=-fullreduction_;
    if (algorithm==RREF_GUESS && step_infolevel(contextptr) && as<5 && a0s<7){
      algorithm=RREF_GAUSS_JORDAN;
      step_rref=true;
    }
    int modular=(algorithm==RREF_MODULAR || algorithm==RREF_PADIC);
    // NOTE for integer matrices
    // p-adic is in n^3*log(nA)^2 where ||a||<=A
    // multi-modular is in n^3*(n+log(nA))*log(nA)
    // Bareiss is in n^3*M(n*log(nA)) where M is multiplication time
    // => for small A and large n p-adic,
    // but for large A and small n, Bareiss is faster
    if (fullreduction_>=0 && algorithm==RREF_GUESS && rref_or_det_or_lu==0 && as>10 && as==a0s-1 && int(as)==lmax && int(a0s)==cmax)
      modular=2;
    if (algorithm==RREF_GUESS && rref_or_det_or_lu<0){
      modular=1;
      rref_or_det_or_lu=-rref_or_det_or_lu;
    }
    if (rref_or_det_or_lu==2 || rref_or_det_or_lu == 3){ // LU decomposition
      algorithm=RREF_GAUSS_JORDAN;
      dont_swap_below=0;
      convert_internal=false;
      fullreduction=0;
    }
    vector<int> permutation(lmax);
    for (int i=0;i<lmax;++i)
      permutation[i]=i;
#ifndef GIAC_HAS_STO_38
    // modular algorithm
    if ( ( (algorithm==RREF_GUESS && (
				      fullreduction==2 || 
				      rref_or_det_or_lu==1)) || modular ) && is_integer_matrice(a) && as<=a0s && as>=20){
      int Res=mrref_int(a,res,pivots,det,l,lmax,c,cmax,fullreduction,dont_swap_below,convert_internal,algorithm,rref_or_det_or_lu,modular,permutation,contextptr);
      if (Res>=0)
	return Res;
    }
#if 1 // modular algo not fast enough and p-adic already used 
    if ( as>=GIAC_PADIC && (rref_or_det_or_lu==1 || fullreduction==2) && (algorithm==RREF_GUESS || modular ) && is_fraction_matrice(a)){
      res=a;
      gen detden=1;
      vecteur lcms(as);
      for (unsigned i=0;i<as;++i){
	gen lcm_deno=common_deno(*res[i]._VECTptr);
	res[i]=lcm_deno*res[i];
	detden=detden*lcm_deno;
	lcms[i]=lcm_deno;
      }
      matrice res_;
      int ok=mrref(res,res_,pivots,det,l,lmax,c,cmax,fullreduction,dont_swap_below,convert_internal,algorithm,rref_or_det_or_lu,contextptr);
      det=det/detden;
      swap(res,res_);
      if (ok==2){ 
	// res*diag(lcms)*N=identity, hence N^-1=res*diag(lcms), adjust columns of res
	for (unsigned i=0;i<as;++i){
	  vecteur & resi=*res[i]._VECTptr;
	  for (unsigned j=0;j<as;++j){
	    resi[j] = resi[j]*lcms[j];
	  }
	}
	return ok;
      }
      if (rref_or_det_or_lu!=2)
	return ok;
      // adjust denominators for lu decomposition
    }
#endif // modular algo for matrices with coeff in Q
#endif // GIAC_HAS_STO_38
    gen tmp=a.front();
    if (lidnt(a).empty() && tmp.type==_VECT && !tmp._VECTptr->empty()){
      tmp=tmp._VECTptr->front();
      if (tmp.type==_MOD){
	gen modulo=*(tmp._MODptr+1);
	vecteur unmoda=*unmod(a)._VECTptr;
	if (!modrref(unmoda,res,pivots,det,l,lmax,c,cmax,
		     fullreduction,dont_swap_below,modulo,true/*ckprime*/,rref_or_det_or_lu)){
	  if (!mrref(unmoda,res,pivots,det,l,lmax,c,cmax,
		     fullreduction,dont_swap_below,convert_internal,algorithm,rref_or_det_or_lu,contextptr))
	    return 0;
	}
	res=*makemod(res,modulo)._VECTptr;
	// keep the permutation without makemod
	if (!pivots.empty()){
	  gen last=pivots.back();
	  pivots.pop_back();
	  pivots=*makemod(pivots,modulo)._VECTptr;
	  pivots.push_back(last);
	}
	det=makemod(det,modulo);
	return 1;
      }
    }
    int linit=l;//,previous_l=l;
    vecteur lv;
    bool num_mat=has_num_coeff(a);
    if (num_mat){
      if (is_fully_numeric(a))
	res=a;
      else {
	res=*evalf_VECT(a,0,1,contextptr)._VECTptr;
	num_mat=is_fully_numeric(res);
      }
      if (algorithm==RREF_GUESS)
	algorithm=RREF_LAGRANGE;
#if 1 // ndef BCD
      matrix_double N;
      if (eps>=1e-16 && num_mat && matrice2std_matrix_double(res,N,true)){
	// specialization for double
	double ddet;
	vector<int> maxrankcols;
	doublerref(N,pivots,permutation,maxrankcols,ddet,l,lmax,c,cmax,fullreduction,dont_swap_below,rref_or_det_or_lu,eps);
	if (rref_or_det_or_lu!=1){
	  std_matrix<gen> RES;
	  std_matrix_giac_double2std_matrix_gen(N,RES);
	  std_matrix_gen2matrice_destroy(RES,res);
	}
	det=ddet;
	if (rref_or_det_or_lu==2 || rref_or_det_or_lu == 3){
	  vecteur P;
	  vector_int2vecteur(permutation,P);
	  pivots.push_back(P);
	}
#ifdef GIAC_HAS_STO_38
	return 1;
#else
	return fullreduction==2?2:1;
#endif
      }
#endif
    }
    else
      res=a;
    //if (debug_infolevel) CERR << CLOCK()*1e-6 << " convert internal" << endl;
    if (convert_internal){
      // convert a to internal form
      alg_lvar_halftan_tsimplify(res,lv,contextptr);
      res = *(e2r(res,lv,contextptr)._VECTptr);
      if (lv.size()==1 && lv.front().type==_VECT && lv.front()._VECTptr->empty()){
	// cleanup res
	int i=0;
	for (;i<res.size();++i){
	  if (res[i].type==_VECT && res[i].ref_count()==1){
	    vecteur & resi=*res[i]._VECTptr;
	    int j=0;
	    for (;j<resi.size();++j){
	      gen resij=resi[j];
	      if (resij.type<_POLY)
		continue;
	      if (resij.type==_FRAC && resij._FRACptr->den.type<=_ZINT)
		resij=resij._FRACptr->num;
	      if (resij.type==_POLY && resij._POLYptr->dim==0 && resij._POLYptr->coord.size()==1){
		gen tmp=resij._POLYptr->coord.front().value;
		if (tmp.type<_POLY)
		  continue;
		if (tmp.type==_EXT && tmp._EXTptr->type==_VECT && (tmp._EXTptr+1)->type==_VECT && is_integer_vecteur(*tmp._EXTptr->_VECTptr) && is_integer_vecteur(*(tmp._EXTptr+1)->_VECTptr))
		  continue;
	      }
	      break;
	    }
	    if (j!=resi.size())
	      break;
	  }
	  else break;
	}
	if (i==res.size()){
	  for (i=0;i<res.size();++i){
	    if (res[i].type==_VECT && res[i].ref_count()==1){
	      vecteur & resi=*res[i]._VECTptr;
	      for (int j=0;j<resi.size();++j){
		gen & resij=resi[j];
		if (resij.type==_FRAC && resij._FRACptr->den.type<=_ZINT){
		  gen tmp=resij._FRACptr->num;
		  if (tmp.type==_POLY && tmp._POLYptr->dim==0 && tmp._POLYptr->coord.size()==1)
		    resij=fraction(tmp._POLYptr->coord.front().value,resij._FRACptr->den);
		}
		if (resij.type==_POLY && resij._POLYptr->dim==0 && resij._POLYptr->coord.size()==1)
		  resij=resij._POLYptr->coord.front().value;
	      }
	    }
	  }
	}
      }
    }
    //if (debug_infolevel) CERR << CLOCK()*1e-6 << " end convert internal" << endl;
    int lvs=int(lv.size());
    // COUT << res << endl;
    gen lcm_deno,gcd_num;
    gen detnum = plus_one;
    gen detden = plus_one;
    if (algorithm!=RREF_GAUSS_JORDAN){
      // remove common denominator of each line (fraction-free elim)
      iterateur it=res.begin(),itend=res.end();
      for (;it!=itend;++it){
	if (num_mat){ // divide each line by max coeff in abs value
	  lcm_deno=linfnorm(*it,contextptr);
	  detnum=lcm_deno*detnum;
	  multvecteur(inv(lcm_deno,contextptr),*it->_VECTptr,*it->_VECTptr);
	}
	else { // non num mat
	  lcm_deno=common_deno(*it->_VECTptr);
	  if (!is_one(lcm_deno)){
	    iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
	    for (;jt!=jtend;++jt){
	      if (jt->type==_FRAC){
		gen nm(jt->_FRACptr->num);
		gen dn(jt->_FRACptr->den);
		// *jt -> lcmdeno* (nm/dn) = nm * tmp/dn
		gen tmp(lcm_deno);
		simplify(tmp,dn);
		if (dn.type<=_CPLX){
		  *jt=nm*tmp/dn;
		  continue;
		}
		if (dn.type==_POLY){
		  *jt=nm*tmp/dn._POLYptr->coord.front().value;
		  continue;
		}
		return 0; // settypeerr();
	      }
	      else
		*jt=(*jt) * lcm_deno;
	    }
	    detden = detden * lcm_deno;
	  }
	  gcd_num=common_num(*it->_VECTptr);
	  if (!is_zero(gcd_num,contextptr))
	    *it=rdiv(*it,gcd_num,contextptr);
	  detnum=detnum*gcd_num;
	} // end else (non num mat)
      } // end for (;it!=itend;)
      // check if res is integer or polynomial
      if (lvs==1 && lv.front().type==_VECT && lv.front()._VECTptr->empty() && (rref_or_det_or_lu==1 || modular ) && is_integer_matrice(res) && as<=a0s){
	matrice res1;
	if (!mrref(res,res1,pivots,det,l,lmax,c,cmax,fullreduction,dont_swap_below,false,algorithm,rref_or_det_or_lu,contextptr))
	  return 0;
	res=res1;
	det=detnum*det/detden;
	if (convert_internal)
	  det=r2sym(det,lv,contextptr);
	if (rref_or_det_or_lu==2 || rref_or_det_or_lu == 3){
	  vecteur P;
	  vector_int2vecteur(permutation,P);
	  pivots.push_back(P);
	}
	return 1;
      }
      bool fullreductionafter=rref_or_det_or_lu==0 && dont_swap_below==0 && c==0 && linit==0 && cmax>=lmax && step_infolevel(contextptr)==0 && fullreduction && algorithm!=RREF_GAUSS_JORDAN; // insure all pivots are normalized to be = to the determinant
      if ( ( (rref_or_det_or_lu==1 && as==a0s ) || fullreductionafter) 
	   && as>4 
	   && algorithm==RREF_GUESS && ( (convert_internal && lvs==1 && lv.front().type==_VECT) || res.front()._VECTptr->front().type==_POLY) ){
	// guess if Bareiss or Lagrange interpolation is faster
	// Bareiss depends on the total degree, Lagrange on partial degrees
	// gather line/columns statistics
	int polydim=res.front()._VECTptr->front().type==_POLY?res.front()._VECTptr->front()._POLYptr->dim:int(lv.front()._VECTptr->size());
	index_t col_totaldeg(as);
	vector< index_t > col_partialdeg(as,index_t(polydim));
	int maxtotaldeg=0,summaxtotaldeg=0;
	if (polydim){
	  index_t summaxdeg(polydim);
	  for (unsigned int i=0;i<as;++i){
	    index_t maxdeg(polydim);
	    for (unsigned int j=0;j<as;++j){
	      const gen & tmp = (*res[i]._VECTptr)[j];
	      if (tmp.type==_POLY){
		const index_t & degij=tmp._POLYptr->degree();
		maxdeg=index_lcm(degij,maxdeg);
		col_partialdeg[j]=index_lcm(degij,col_partialdeg[j]);
		int totaldeg=tmp._POLYptr->total_degree();
		if (maxtotaldeg<totaldeg)
		  maxtotaldeg=totaldeg;
		if (col_totaldeg[j]<totaldeg)
		  col_totaldeg[j]=totaldeg;
	      }
	    }
	    summaxtotaldeg += maxtotaldeg;
	    summaxdeg=summaxdeg+maxdeg;
	  }
	  maxtotaldeg=std::min(summaxtotaldeg,int(total_degree(col_totaldeg)));
	  index_t col_sumpartialdeg(polydim);
	  for (unsigned int j=0;j<as;++j)
	    col_sumpartialdeg = col_sumpartialdeg+col_partialdeg[j];
	  for (int i=0;i<polydim;++i){
	    summaxdeg[i]=std::min(summaxdeg[i],col_sumpartialdeg[i]);
	  }
	  if (debug_infolevel>2)
	    CERR << "Total degree " << maxtotaldeg << ", partial degrees " << summaxdeg << endl;
	  // Now modify algorithm to RREF_LAGRANGE if it's faster
	  double lagrange_time=std::pow(double(as),2)*(as*10+160); 
	  // coeffs of as*.+. are guess
	  for (int j=0;j<polydim;j++){
	    lagrange_time *= (summaxdeg[j]+1);
	  }
	  double bareiss_time=0;
	  // time is almost proportionnal to sum( comb(maxtotaldeg*j/as+polydim,polydim)^2, j=1..as-1)
	  for (unsigned int j=1;j<as;++j){
	    int tmpdeg=int(double(maxtotaldeg*j)/as+.5);
	    double tmp = evalf_double(comb(tmpdeg+polydim,polydim),1,contextptr)._DOUBLE_val;
	    tmp = tmp*tmp*std::log(tmp)*(as-j);
	    bareiss_time += 4*tmp; // 1 for *, 1 for /
	  }
	  bareiss_time *= as; // take account of the size of the coefficients
	  if (debug_infolevel>2)
	    CERR << "lagrange " << lagrange_time << " bareiss " << bareiss_time << endl;
	  if (lagrange_time<bareiss_time){
	    algorithm=RREF_LAGRANGE;
	  }
	} // end if (polydim)
      }
      if ( algorithm==RREF_LAGRANGE && ( (rref_or_det_or_lu==1 && as==a0s) || fullreductionafter ) ){
	vecteur lva=lvar(a);
	if ( (!convert_internal && lva.empty()) || (lvs==1 && lv.front()==lva) ){
	  // find degrees wrt main variable
	  int polydim=0;
	  int totaldeg=0;
	  vector<int> maxdegj(a0s);
	  for (unsigned int i=0;i<as;++i){
	    int maxdegi=0;
	    for (unsigned int j=0;j<a0s;++j){
	      gen & tmp = (*res[i]._VECTptr)[j];
	      if (tmp.type==_POLY){
		polydim=tmp._POLYptr->dim;
		const int & curdeg=tmp._POLYptr->lexsorted_degree();
		if (curdeg>maxdegi)
		  maxdegi=tmp._POLYptr->lexsorted_degree();
		if (curdeg>maxdegj[j])
		  maxdegj[j]=curdeg;
		tmp=polynome2poly1(tmp,1);
	      }
	    }
	    totaldeg+=maxdegi;
	  }
	  if (polydim){
	    if (debug_infolevel)
	      CERR << CLOCK()*1e-6 << " det: begin interp" << endl;
	    totaldeg=std::min(totaldeg,total_degree(maxdegj));
	    int shift=totaldeg/2;
	    proba_epsilon(contextptr) /= totaldeg;
	    vecteur X(totaldeg+1),Y(totaldeg+1),Z(totaldeg+1);
	    int x=0;
	    for (;x<=totaldeg;++x){
	      int realx=x-shift;
	      X[x]=realx;
	      vecteur resx;
	      resx.reserve(totaldeg+1);
	      if (debug_infolevel)
		CERR << CLOCK()*1e-6 << " det: begin horner" << endl;
	      for (unsigned int i=0;i<as;++i){
		vecteur resxi;
		resxi.reserve(a0s); // was (totaldeg+1);
		for (unsigned int j=0;j<a0s;++j){
		  const gen & tmp = (*res[i]._VECTptr)[j];
		  resxi.push_back(horner(tmp,realx));
		}
		resx.push_back(resxi);
	      }
	      if (debug_infolevel)
		CERR << CLOCK()*1e-6 << " det: end horner" << endl;
	      matrice res1;
	      if (!mrref(resx,res1,pivots,det,l,lmax,c,cmax,-fullreduction,dont_swap_below,false,algorithm_,rref_or_det_or_lu,contextptr))
		return 0;
	      Y[x]=det;
	      if (fullreduction){
		if (is_zero(det) )
		  break;
		// check diagonal coefficients of res1, they must be == det
		for (int i=0;i<res1.size();++i){
		  vecteur & res1i=*res1[i]._VECTptr;
		  if (res1i[i]==det)
		    continue;
		  if (res1i[i]==-det)
		    res1[i]=-res1[i];
		  else
		    res1[i]=(det/res1i[i])*res1[i];
		}
		// extract right submatrix
		res1=mtran(res1);
		Z[x]=vecteur(res1.begin()+lmax,res1.end());
	      } // if (fullreduction)
	    } // end for x
	    if (x==totaldeg+1){
	      proba_epsilon(contextptr) *= totaldeg;
	      if (debug_infolevel)
		CERR << CLOCK()*1e-6 << " det: divided diff" << endl;
	      // Lagrange interpolation
	      vecteur L=divided_differences(X,Y);
	      if (debug_infolevel)
		CERR << CLOCK()*1e-6 << " det: end divided diff" << endl;
	      det=untrunc1(L[totaldeg]);
	      monomial<gen> mtmp(1,1,polydim);
	      gen xpoly=polynome(mtmp);
	      for (int i=totaldeg-1;i>=0;--i){
		det = det*(xpoly-untrunc1(X[i]))+untrunc1(L[i]);
	      }
	      det=det*detnum/detden;
	      if (convert_internal)
		det=r2sym(det,lva,contextptr);
	      if (debug_infolevel)
		CERR << CLOCK()*1e-6 << " det: end interp" << endl;
	      if (fullreduction){
		vecteur R,RR;
		interpolate(X,Z,R,0);
		polymat2matpoly(R,RR);
		// apply poly12polynome in elements of R
		for (int i=0;i<RR.size();++i){
		  if (RR[i].type!=_VECT)
		    continue;
		  vecteur & w=*RR[i]._VECTptr;
		  for (int j=0;j<w.size();++j){
		    if (w[j].type==_VECT){
		      w[j]=poly12polynome(*w[j]._VECTptr,1,polydim);
		    }
		    if (convert_internal)
		      w[j]=r2sym(w[j],lva,contextptr);
		  }
		}
		vecteur R0=midn(lmax);
		for (int i=0;i<R0.size();++i){
		  (*R0[i]._VECTptr)[i]=det;
		}
		R=mergevecteur(R0,RR);
		res=mtran(R);
	      }
	      return 1;
	    } // if interpolation ok (x==totaldeg+1)
	    else { // back convert from poly1 to polynome
	      for (unsigned int i=0;i<as;++i){
		for (unsigned int j=0;j<a0s;++j){
		  gen & tmp = (*res[i]._VECTptr)[j];
		  if (tmp.type==_VECT){
		    tmp=poly12polynome(*tmp._VECTptr,1,polydim);
		  }
		}
	      }
	    }
	  } // end if polydim
	}
      }
    }

    std_matrix<gen> M;
    matrice2std_matrix_gen(res,M);
    // vecteur vtemp;
    pivots.clear();
    pivots.reserve(cmax-c);
    bool fullreductionafter=rref_or_det_or_lu==0 && dont_swap_below==0 && cmax-c>=lmax-linit && step_infolevel(contextptr)==0 && fullreduction && algorithm!=RREF_GAUSS_JORDAN;
    gen detnumsave=detnum;
    int status=rref_reduce(M,pivots,permutation,det,detnum,algorithm,l,lmax,c,cmax,dont_swap_below,rref_or_det_or_lu,(fullreductionafter?0:fullreduction),eps,step_rref,lv,convert_internal,contextptr);
    if (status!=2 && status!=3)
      return status;
    if (fullreductionafter){ 
      det=M[lmax-1][c-linit+lmax-1];
      if (status==3 || det==0){ 
	// not Cramer like, re-reduce, 
	pivots.clear();
	matrice2std_matrix_gen(res,M); det=detnum=detnumsave;// this should be commented but some outputs are more complicated
	rref_reduce(M,pivots,permutation,det,detnum,algorithm,l,lmax,c,cmax,dont_swap_below,rref_or_det_or_lu,fullreduction,eps,step_rref,lv,convert_internal,contextptr);
      }
      else {
	// back row reduction to echelon form for Cramer like system
	vecteur & Mlast=M[lmax-1];
	int shift=c-linit;
	gen d=Mlast[shift+lmax-1];
	for (l=lmax-2;l>=linit;--l){
	  vecteur Mlcopy(M[l]);
	  vecteur & Ml=M[l];
	  // Ll <- a_{n-1,n-1}*Ll-(a_{l,n-1}*L_{n-1}-a_{l,n-2}*L_{n-2}...)
	  // multvecteur(d,Ml,Ml); // should be done from shift+lmax
	  for (int j=shift+lmax;j<cmax;++j){
	    gen & Mlj=Ml[j];
	    Mlj = d*Mlj;
	  }
	  for (int j=lmax-1;j>=l+1;--j){
	    linear_combination(plus_one,Ml,-Mlcopy[shift+j],M[j],plus_one,1,Ml,eps,shift+lmax);
	  }
	  for (int j=shift+l+1;j<shift+lmax;++j)
	    Ml[j]=0;
	  Ml[shift+l]=d;
	  for (int j=shift+lmax;j<cmax;++j){
	    Ml[j]=exact_div(Ml[j],Mlcopy[shift+l]);
	  }
	}
      }
    } // end if fullreductionafter
    if (debug_infolevel>2)
      CERR << "// mrref reduction end:" << CLOCK() << endl;
    if (step_rref){
      std_matrix_gen2matrice(M,res);
      if (convert_internal)
	res = *r2sym(res,lv,contextptr)._VECTptr;
      gprintf(step_rrefend,gettext("End reduction %gen"),makevecteur(res),contextptr);
    }
    if (algorithm!=RREF_GAUSS_JORDAN){
      int last=giacmin(lmax,cmax);
      det=M[last-1][last-1];
      if ( (debug_infolevel>2) && (det.type==_POLY) )
	CERR << "// polynomial size " << det._POLYptr->coord.size() << endl;
      if (rref_or_det_or_lu==1) // erase last line of the matrix
	M[lmax-1].clear();
      det=rdiv(det*detnum,detden,contextptr);
      if (convert_internal)
	det=r2sym(det,lv,contextptr);
      // CERR << det << endl;
    }
    else {
      // adjust determinant by multiplication by all diagonal coeffs
      for (int i=linit;i<lmax && i<cmax;++i)
	detnum = detnum * M[i][i];
      det = rdiv(detnum,detden,contextptr);
      if (convert_internal)
	det = r2sym(det,lv,contextptr);
    }
    std_matrix_gen2matrice_destroy(M,res);
    if (convert_internal)
      res = *(r2sym (res,lv,contextptr)._VECTptr);
    if (rref_or_det_or_lu==2 || rref_or_det_or_lu == 3){
      vecteur P;
      vector_int2vecteur(permutation,P);
      pivots.push_back(P);
    }
    if (debug_infolevel>2)
      CERR << "// mrref end:" << CLOCK() << " " << M << endl;
    return 1;
  }

  // convert a to vector< vector<int> > with modular reduction (if modulo!=0)
  void vect_vecteur_2_vect_vector_int(const std_matrix<gen> & M,int modulo,vector< vector<int> > & N){
    int Msize=int(M.size());
    N.clear();
    N.reserve(Msize);
    for (int k=0;k<Msize;k++){
      const vecteur & v = M[k];
      const_iterateur it=v.begin(),itend=v.end();
      vector<int> vi(itend-it);
      vector<int>::iterator jt=vi.begin();
      for (;it!=itend;++jt,++it){
	if (!modulo)
	  *jt=it->val;
	else
	  *jt=smod(*it,modulo).val;
      }
      N.push_back(vi);
    }
  }

  void vect_vector_int_2_vect_vecteur(const vector< vector<int> > & N,std_matrix<gen> & M){
    // Back convert N to M
    int Msize=int(N.size());
    M = std_matrix<gen>(Msize);
    for (int k=0;k<Msize;k++){
      const vector<int> & v = N[k];
      vector<int>::const_iterator it=v.begin(),itend=v.end();
      vecteur vi(itend-it);
      iterateur jt=vi.begin();
      for (;it!=itend;++jt,++it){
	*jt=*it;
      }
      M[k]=vi;
    }
  }

  //transforme un vecteur en vector<int>  
  void vecteur2vector_int(const vecteur & v,int m,vector<int> & res){
    vecteur::const_iterator it=v.begin(),itend=v.end();
    res.clear();
    if (m==0) {
      res.resize(itend-it);
      int * jt=&res.front();
      for (;it!=itend;++it,++jt){
	if (it->type==_MOD)
	  *jt=it->_MODptr->val;
	else
	  *jt=it->val; 
      }
      return;
    }
    res.reserve(itend-it);
    if (m<0)
      m=-m;
    for (;it!=itend;++it){
      if (it->type==_MOD)
	res.push_back(it->_MODptr->val);
      else {
	int r=it->type==_ZINT?modulo(*it->_ZINTptr,m):(it->val % m);
	r += (unsigned(r)>>31)*m; // make positive
	r -= (unsigned((m>>1)-r)>>31)*m;
	res.push_back(r);// res.push_back(smod((*it),m).val); 
      }
    }
  } 

  bool vecteur2vectvector_int(const vecteur & v,int modulo,vector< vector<int> > & res){
    vecteur::const_iterator it=v.begin(),itend=v.end();
    res.resize(itend-it);
    for (int i=0;it!=itend;++i,++it){
      if (it->type!=_VECT)
	return false;
      vecteur2vector_int(*it->_VECTptr,modulo,res[i]);
    }
    return true;
  }

  void vector_int2vecteur(const vector<int> & v,vecteur & res){
    //transforme un vector<int> en vecteur 
    vector<int>::const_iterator it=v.begin(),itend=v.end();
    res.resize(itend-it);
    for (iterateur jt=res.begin();it!=itend;++jt,++it)
      *jt=*it;
  } 

  void vectvector_int2vecteur(const vector< vector<int> > & v,vecteur & res){
    //transforme un vector< vector<int> > en vecteur  
    int s=int(v.size());
    res.resize(s);
    for (int i=0;i<s;++i){
      if (res[i].type!=_VECT)
	res[i]=new ref_vecteur;
      vector_int2vecteur(v[i],*res[i]._VECTptr);
    }
  }

  int dotvector_int(const vector<int> & v,const vector<int> & w,int modulo){
    vector<int>::const_iterator it=v.begin(),itend=v.end(),it1,jt=w.begin();
    unsigned n=unsigned(itend-it);
    if ( ((longlong(modulo)*modulo)/RAND_MAX)*n>RAND_MAX){
      int res=0;
      for (;it!=itend;++jt,++it){
#ifdef _I386_
	mod(res,*it,*jt,modulo);
#else
	res = (res + longlong(*it)*(*jt))% modulo;
#endif
      }
      return smod(res,modulo) ;
    }
    longlong res=0;
    it1 = it + ((n>>2) <<2);
    for (;it!=it1;){
      res += (longlong (*it))*(*jt);
      ++jt; ++it;
      res += (longlong (*it))*(*jt);
      ++jt; ++it;
      res += (longlong (*it))*(*jt);
      ++jt; ++it;
      res += (longlong (*it))*(*jt);
      ++jt; ++it;
    }
    for (;it!=itend;++jt,++it){
      res += (longlong (*it))*(*jt);
    }
    return smod(res,modulo) ;
  }

  void dotvector_int(const vector<int> & v0,const vector<int> & v1,const vector<int> & v2,const vector<int> & v3,const vector<int> & w,longlong &res0,longlong & res1,longlong & res2,longlong & res3){
    vector<int>::const_iterator it=w.begin(),itend=w.end(),it1,jt0=v0.begin(),jt1=v1.begin(),jt2=v2.begin(),jt3=v3.begin();
    unsigned n=unsigned(itend-it);
    res0=res1=res2=res3=0;
    it1 = itend -4;
    for (;it<=it1;jt0+=4,jt1+=4,jt2+=4,jt3+=4,it+=4){
      longlong tmp0=it[0],tmp1=it[1],tmp2=it[2],tmp3=it[3];
      res0 += tmp0*jt0[0]+tmp1*jt0[1]+tmp2*jt0[2]+tmp3*jt0[3];
      res1 += tmp0*jt1[0]+tmp1*jt1[1]+tmp2*jt1[2]+tmp3*jt1[3];
      res2 += tmp0*jt2[0]+tmp1*jt2[1]+tmp2*jt2[2]+tmp3*jt2[3];
      res3 += tmp0*jt3[0]+tmp1*jt3[1]+tmp2*jt3[2]+tmp3*jt3[3];
    }
    for (;it!=itend;++jt0,++jt1,++jt2,++jt3,++it){
      longlong tmp=*it;
      res0 += tmp*(*jt0);
      res1 += tmp*(*jt1);
      res2 += tmp*(*jt2);
      res3 += tmp*(*jt3);
    }
  }

  bool multvectvector_int_vector_int(const vector< vector<int> > & M,const vector<int> & v,int modulo,vector<int> & Mv){
    unsigned n=unsigned(M.size());
    Mv.clear();
    if (!n)
      return true;
    if (M.front().size()!=v.size())
      return false; 
    Mv.reserve(n);
    vector< vector<int> >::const_iterator it=M.begin(),itend=M.end();
#if 1
    if ( ((longlong(modulo)*modulo)/RAND_MAX)*n<=RAND_MAX){
      itend-=4;
      longlong l0,l1,l2,l3;
      for (;it<=itend;it+=4){
	dotvector_int(it[0],it[1],it[2],it[3],v,l0,l1,l2,l3);
	Mv.push_back(smod(l0,modulo));
	Mv.push_back(smod(l1,modulo));
	Mv.push_back(smod(l2,modulo));
	Mv.push_back(smod(l3,modulo));
      }
      itend+=4;
    }
#endif    
    for (;it!=itend;++it){
      Mv.push_back(dotvector_int(*it,v,modulo));
    }
    return true;
  }

  void tran_vect_vector_int(const vector< vector<int> > & N,vector< vector<int> > & tN){
    tN.clear();
    unsigned r=unsigned(N.size());
    if (!r)
      return;
    unsigned c=unsigned(N.front().size());
    tN.reserve(c);
    for (unsigned int i=0;i<c;++i){
      vector<int> current;
      current.reserve(r);
      for (unsigned int j=0;j<r;++j){
	current.push_back(N[j][i]);
      }
      tN.push_back(current);
    }
  }

  void apply_permutation(const vector<int> & permutation,const vector<int> &x,vector<int> & y){
    unsigned n=unsigned(x.size());
    y.clear();
    y.reserve(n);
    for (unsigned int i=0;i<n;++i)
      y.push_back(x[permutation[i]]);
  }

  /*
  vector<int> perminv(const vector<int> & p);
  // solve LU x= b (permutation P)
  void smallsolvelu(const vector< vector<int> > & LU,const vector<int> & P,const vector<int> & b,vector<int> & x,int modulo){
    unsigned n=P.size();
    vector<int> bp(n),y(n);
    apply_permutation(P,b,bp);
    // solve U y=bp
    for (int i=n-1;i>=0;--i){
      // y[i]=LU[i,i]^(-1)*(bp[i]-sum(j>i)LU[i,j]*y[j])
      int res=0;
      const vector<int> & li=LU[i];
      for (int j=i+1;j<n;++j)
	mod(res,li[j],y[j],modulo);
      y[i]=(invmod(li[i],modulo)*longlong(bp[i]-res))%modulo;
    }
    // solve L bp = y
    for (int i=0;i<n;++i){
      // bp[i]=(y[i]-sum(j<i)LU[i,j]*bp[j])
      int res=0;
      const vector<int> & li=LU[i];
      for (int j=0;j<i;++j)
	mod(res,li[j],y[j],modulo);
      y[i]=longlong(bp[i]-res)%modulo;
    }
    // reorder bp
    apply_permutation(perminv(P),bp,x);
  }
  */

  void makepositive(vector< vector<int> > & N,int l,int lmax,int c,int cmax,int modulo){
    for (int L=l;L<lmax;++L){
      vector<int> & NL=N[L];
      if (NL.empty()) continue;
      for (int C=c+(L-l);C<cmax;++C){
	int & i=NL[C];
	i -= (i>>31)*modulo;
      }
    }
  }    

#if 1
  void smallmodrref_lower(vector< vector<int> > & N,int lstart,int l,int lmax,int c,int cmax,const vector<int> & pivots,int modulo,bool debuginfo){
    int ps=int(pivots.size());
    longlong modulo2=longlong(modulo)*modulo;
    bool convertpos= double(modulo2)*ps >= 9.22e18;
    if (convertpos)
      makepositive(N,lstart,lmax,c,cmax,modulo);
    vector<longlong> buffer(cmax);
    for (int L=l;L<lmax;++L){
      if (debuginfo){
	if (L%10==9){ CERR << "+"; CERR.flush();}
	if (L%500==499){ CERR << CLOCK() << " remaining " << lmax-L << endl; }
      }
      // copy line to 64 bits buffer
      vector<int> & NL=N[L];
      if (NL.empty()) continue;
      for (int C=c;C<cmax;++C)
	buffer[C]=NL[C];
      // substract lines in pivots[k].first from column pivots[k].second to cmax
      for (int line=lstart;line<lstart+ps;++line){
	int col=pivots[line-lstart];
	if (col<0) continue;
	vector<int> & Nline=N[line];
	if (Nline.empty()){
	  CERR << "rref_lower Bad matrix "<< lmax << "x" << cmax << " l" << line << " c" << col << endl;
	  continue;
	}
	if (Nline[col]!=1){
	  Nline[col] %= modulo;
	  if (Nline[col]!=1){
	    CERR << "rref_lower Bad matrix "<< lmax << "x" << cmax << " l" << line << " c" << col << " " << Nline[col] << endl;
	    continue;
	  }
	}
	longlong coeff=buffer[col];
	if (!coeff) continue;
	coeff %= modulo;
	if (!coeff) continue;
	buffer[col]=0;
	if (convertpos){
	  int C=col+1;
	  longlong * buf=&buffer[C];
	  longlong * bufend=&buffer[cmax]-8;
	  const int * nline=&Nline[C];
	  for (;buf<=bufend;buf+=8,nline+=8){
	    longlong x,y;
	    x=buf[0]; x -= coeff*nline[0]; x -= (x>>63)*modulo2; buf[0]=x; 
	    y=buf[1]; y -= coeff*nline[1]; y -= (y>>63)*modulo2; buf[1]=y; 
	    x=buf[2]; x -= coeff*nline[2]; x -= (x>>63)*modulo2; buf[2]=x; 
	    y=buf[3]; y -= coeff*nline[3]; y -= (y>>63)*modulo2; buf[3]=y; 
	    x=buf[4]; x -= coeff*nline[4]; x -= (x>>63)*modulo2; buf[4]=x; 
	    y=buf[5]; y -= coeff*nline[5]; y -= (y>>63)*modulo2; buf[5]=y; 
	    x=buf[6]; x -= coeff*nline[6]; x -= (x>>63)*modulo2; buf[6]=x; 
	    y=buf[7]; y -= coeff*nline[7]; y -= (y>>63)*modulo2; buf[7]=y; 
	  }
	  for (C+=int(buf-&buffer[C]);C<cmax;++C){
	    longlong & b=buffer[C] ;
	    longlong x = b;
	    x -= coeff*Nline[C];   
	    x -= (x>>63)*modulo2;
	    b=x;
	  }
	}
	else {
	  int C=col+1;
	  longlong * buf=&buffer[C];
	  longlong * bufend=&buffer[cmax]-8;
	  const int * nline=&Nline[C];
	  for (;buf<=bufend;buf+=8,nline+=8){
	    buf[0] -= coeff*nline[0];
	    buf[1] -= coeff*nline[1];
	    buf[2] -= coeff*nline[2];
	    buf[3] -= coeff*nline[3];
	    buf[4] -= coeff*nline[4];
	    buf[5] -= coeff*nline[5];
	    buf[6] -= coeff*nline[6];
	    buf[7] -= coeff*nline[7];
	  }
	  for (C+=int(buf-&buffer[C]);C<cmax;++C){
	    buffer[C] -= coeff*Nline[C];   
	  }
	}
      }
      // copy back buffer to N[l]
      for (int C=c;C<cmax;++C){
	longlong x=buffer[C];
	if (x) 
	  NL[C]=x % modulo;
	else
	  NL[C]=0;
      } 
    } // end loop on L
  }

#else
  // lower row reduction of N from l to lmax using already reduced lines
  // with column slicing, not kept seems slower 
  void smallmodrref_lower(vector< vector<int> > & N,int lstart,int l,int lmax,int c,int cmax,const vector<int> & pivots,int modulo,bool debuginfo){
    int ps=int(pivots.size());
    if (!ps) return;
    longlong modulo2=longlong(modulo)*modulo;
    bool convertpos= double(modulo2)*ps >= 9.22e18;
    if (convertpos)
      makepositive(N,lstart,lmax,c,cmax,modulo);
    vector<longlong> buffer(cmax);
    // slice in columns
    // this requires a first pass with effcmax=pivot.back()+1 (last col)
    // where linear combination coefficients are stored (in N)
    // then linear combinations are done using stored coefficients
    int effcmin,effcmax=giacmin(pivots.back()+1,cmax);
    if (cmax-effcmax<16)
      effcmax=cmax;
    for (int L=l;L<lmax;++L){
      // copy line to 64 bits buffer
      vector<int> & NL=N[L];
      if (NL.empty()) continue;
      for (int C=c;C<effcmax;++C)
	buffer[C]=NL[C];
      // substract lines in pivots[k].first from column pivots[k].second to cmax
      for (int line=lstart;line<lstart+ps;++line){
	int col=pivots[line-lstart];
	if (col<0) continue;
	vector<int> & Nline=N[line];
	if (Nline.empty()){
	  CERR << "rref_lower Bad matrix "<< lmax << "x" << cmax << " l" << line << " c" << col << endl;
	  continue;
	}
	if (Nline[col]!=1){
	  Nline[col] %= modulo;
	  if (Nline[col]!=1){
	    CERR << "rref_lower Bad matrix "<< lmax << "x" << cmax << " l" << line << " c" << col << " " << Nline[col] << endl;
	    continue;
	  }
	}
	longlong coeff=buffer[col];
	if (coeff) 
	  coeff %= modulo;
	buffer[col]=coeff;
	if (!coeff) continue;
	if (convertpos){
	  int C=col+1;
	  for (;C<effcmax;++C){
	    longlong & b=buffer[C] ;
	    longlong x = b;
	    x -= coeff*Nline[C];   
	    x -= (x>>63)*modulo2;
	    b=x;
	  }
	}
	else {
	  int C=col+1;
	  for (;C<effcmax;++C){
	    buffer[C] -= coeff*Nline[C];   
	  }
	}
      }
      // copy back buffer to N[l]
      for (int C=c;C<effcmax;++C){
	longlong x=buffer[C];
	if (x) 
	  NL[C]=x % modulo;
	else
	  NL[C]=0;
      } 
    }
    // slice: second pass for remaining columns
    effcmin=effcmax;
    int nslice=std::ceil(ps*double(cmax-effcmin)*sizeof(int)/32768); // lower matrix size/L1 cache size
    if (nslice<=0) nslice=1;
    int slicestep=std::ceil((cmax-effcmin)/nslice);
    if (slicestep<16) slicestep=16;
    for (;effcmin<cmax;effcmin=effcmax){
      effcmax=giacmin(effcmin+slicestep,cmax);
      for (int L=l;L<lmax;++L){
	vector<int> & NL=N[L];
	if (NL.empty()) continue;
	for (int C=effcmin;C<effcmax;++C)
	  buffer[C]=NL[C];
	for (int line=lstart;line<lstart+ps;++line){
	  int col=pivots[line-lstart];
	  if (col<0) continue;
	  vector<int> & Nline=N[line];
	  longlong coeff=NL[col];
	  if (!coeff) continue;
	  if (convertpos){
	    for (int C=effcmin;C<effcmax;++C){
	      longlong & b=buffer[C] ;
	      longlong x = b;
	      x -= coeff*Nline[C];   
	      x -= (x>>63)*modulo2;
	      b=x;
	    }
	  }
	  else {
	    for (int C=effcmin;C<effcmax;++C){
	      buffer[C] -= coeff*Nline[C];   
	    }
	  }
	} // end loop on l
	// copy back buffer to N[l]
	for (int C=effcmin;C<effcmax;++C){
	  longlong x=buffer[C];
	  if (x) 
	    NL[C]=x % modulo;
	  else
	    NL[C]=0;
	} 
      } // end loop on L
    } // end slicing
    // reset stored linear combination coeffs to 0
    for (int L=l;L<lmax;++L){
      vector<int> & NL=N[L];
      if (NL.empty()) continue;
      for (int i=0;i<pivots.size();++i){
	int c=pivots[i];
	if (c<0) continue;
	NL[c]=0;
      }
    }
  }
#endif

  // find pivot columns in submatrix N[l..lmax-1,c..cmax-1]
  void smallmodrref_lower_pivots(vector< vector<int> > & N,int l,int lmax,int c,int cmax,vector<int> & pivots,int modulo){
    pivots.clear();
    int L=l,C=c,k;
    for (;L<lmax && C<cmax;){
      // N is assumed to be already partially reduced
      vector<int> & NL=N[L];
      if (NL.empty()){
	pivots.push_back(-1);
	++L;
	continue;
      }
      for (k=C;k<cmax;++k){
	if (NL[k]){
	  pivots.push_back(k);
	  ++L; ++C;
	  break;
	}
      }
      if (k==cmax){
	pivots.push_back(-1); ++L;
      }
    }
    while (!pivots.empty() && pivots.back()==-1)
      pivots.pop_back();
  }
  
  void free_null_lines(vector< vector<int> > & N,int l,int lmax,int c,int cmax){
    if (c==0){
      for (int L=lmax-1;L>=l;--L){
	vector<int> & NL=N[L];
	if (NL.empty()) continue;
	if (NL.size()!=cmax) break;
	int C;
	for (C=cmax-1;C>=c;--C){
	  if (NL[C]) break;
	}
	if (C>=c) break;
	NL.clear();
      }
    }
  }

  // finish full row reduction to echelon form if N is upper triangular
  // this is done from lmax-1 to l
  void smallmodrref_upper(vector< vector<int> > & N,int l,int lmax,int c,int cmax,int modulo){
    // desalloc null lines
    free_null_lines(N,l,lmax,c,cmax);
    longlong modulo2=longlong(modulo)*modulo;
    bool convertpos= double(modulo2)*(lmax-l) >= 9.22e18;
    if (convertpos){
      makepositive(N,l,lmax,c,cmax,modulo);
    }
    vector< pair<int,int> > pivots;
    vector<longlong> buffer(cmax);
    for (int L=lmax-1;L>=l;--L){
      vector<int> & NL=N[L];
      if (NL.empty()) continue;
      if (debug_infolevel>1){
	if (L%10==9){ CERR << "+"; CERR.flush();}
	if (L%500==499){ CERR << CLOCK() << " remaining " << l-L << endl; }
      }
      if (!pivots.empty()){
	// reduce line N[L]
	// copy line to a 64 bits buffer
	for (int C=c;C<cmax;++C)
	  buffer[C]=NL[C];
	// substract lines in pivots[k].first from column pivots[k].second to cmax
	int ps=int(pivots.size());
	for (int k=0;k<ps;++k){
	  int line=pivots[k].first;
	  const vector<int> & Nline=N[line];
	  int col=pivots[k].second;
	  longlong coeff=NL[col]; // or buffer[col]
	  if (!coeff) continue;
	  buffer[col]=0;
	  // we could skip pivots columns here, but if they are not contiguous
	  // this would take too much time
	  if (convertpos){
#if 0
	    longlong * ptr=&buffer.front()+col+1, *ptrend=&buffer.front()+cmax,*ptrend_=ptrend-4;
	    const int *nptr=&Nline.front()+col+1;
	    for (;ptr<ptrend;++ptr,++nptr){
	      longlong x=*ptr ;
	      x -= coeff*(*nptr);   
	      x -= (x>>63)*modulo2;
	      *ptr=x;
	    }
#else
	    int C=col+1;
	    for (;C<cmax;++C){
	      longlong & b=buffer[C] ;
	      longlong x = b;
	      x -= coeff*Nline[C];   
	      x -= (x>>63)*modulo2;
	      b=x;
	    }
#endif
	  }
	  else {
	    int C=col+1;
	    for (;C<cmax;++C){
	      buffer[C] -= coeff*Nline[C];   
	    }
	  }
	}
	// copy back buffer to N[l]
	for (int C=c;C<cmax;++C){
	  longlong x=buffer[C];
	  if (x) 
	    NL[C]=x % modulo;
	  else
	    NL[C]=0;
	}
      } // end if pivots.empty()
      // search pivot in N[L] starting column c+L-l to cmax
      for (int C=c+(L-l);C<cmax;++C){
	if (NL[C]){
	  if (NL[C]!=1)
	    CERR << "rref_upper Bad matrix "<< lmax << "x" << cmax << endl;
	  pivots.push_back(pair<int,int>(L,C));
	  break;
	}
      }
    }
#if 0
    for (int L=l;L<lmax;++L){
      vector<int> & NL=N[L];
      for (int C=c+(L-l);C<cmax;++C){
	int & i=NL[C];
	i = smod(i,modulo);
      }
    }
#endif
  }

  void do_modular_reduction(vector< vector<int> > & N,int l,int pivotcol,int pivotval,int linit,int lmax,int c,int effcmax,int rref_or_det_or_lu,int modulo){
#ifndef GIAC_HAS_STO_38
    int l1,l2,l3;
#endif
    bool ludecomp=rref_or_det_or_lu>=2;
    for (int ltemp=linit;ltemp<lmax;++ltemp){
      if (ltemp==l || N[ltemp].empty() || !N[ltemp][pivotcol])
	continue;
#ifndef GIAC_HAS_STO_38
      if (!ludecomp && find_multi_linear_combination(N,ltemp,l1,l2,l3,pivotcol,l,lmax)){
	int_multilinear_combination(N[ltemp],-N[ltemp][pivotcol],N[l1],-N[l1][pivotcol],N[l2],-N[l2][pivotcol],N[l3],-N[l3][pivotcol],N[l],modulo,c,effcmax);
	ltemp = l3;
	continue;
      }
      if (ludecomp && ltemp<=lmax-4 && !N[ltemp+1].empty() && N[ltemp+1][pivotcol] && !N[ltemp+2].empty() && N[ltemp+2][pivotcol] && !N[ltemp+3].empty() && N[ltemp+3][pivotcol]){
	
	N[ltemp][pivotcol]= (N[ltemp][pivotcol]*longlong(pivotval)) % modulo;
	N[ltemp+1][pivotcol]= (N[ltemp+1][pivotcol]*longlong(pivotval)) % modulo;
	N[ltemp+2][pivotcol]= (N[ltemp+2][pivotcol]*longlong(pivotval)) % modulo;
	N[ltemp+3][pivotcol]= (N[ltemp+3][pivotcol]*longlong(pivotval)) % modulo;
	int_multilinear_combination(N[ltemp],-N[ltemp][pivotcol],N[ltemp+1],-N[ltemp+1][pivotcol],N[ltemp+2],-N[ltemp+2][pivotcol],N[ltemp+3],-N[ltemp+3][pivotcol],N[l],modulo,(rref_or_det_or_lu>0)?(c+1):c,effcmax);
	ltemp+= (4-1);
	continue;
      }
#endif
      if (ludecomp) 
	N[ltemp][pivotcol]= (N[ltemp][pivotcol]*longlong(pivotval)) % modulo;
      modlinear_combination(N[ltemp],-N[ltemp][pivotcol],N[l],modulo,(rref_or_det_or_lu>0)?(c+1):c,effcmax,true /* pseudomod */);
    }
  }

  void LL_modular_reduction(vector< vector<longlong> > & N,int l,int pivotcol,int pivotval,int linit,int lmax,int c,int effcmax,int rref_or_det_or_lu,int modulo){
#ifndef GIAC_HAS_STO_38
    int l1,l2,l3;
#endif
    bool ludecomp=rref_or_det_or_lu>=2;
    for (int ltemp=linit;ltemp<lmax;++ltemp){
      if (ltemp==l || N[ltemp].empty() || !N[ltemp][pivotcol])
	continue;
#ifndef GIAC_HAS_STO_38
      if (!ludecomp && find_multi_linear_combination(N,ltemp,l1,l2,l3,pivotcol,l,lmax)){
	int coeff0=(N[ltemp][pivotcol] %= modulo);
	int coeff1=(N[l1][pivotcol] %= modulo);
	int coeff2=(N[l2][pivotcol] %= modulo);
	int coeff3=(N[l3][pivotcol] %= modulo);
	if (rref_or_det_or_lu==1){
	  coeff0 = (coeff0*longlong(pivotval)) % modulo;
	  coeff1 = (coeff1*longlong(pivotval)) % modulo;
	  coeff2 = (coeff2*longlong(pivotval)) % modulo;
	  coeff3 = (coeff3*longlong(pivotval)) % modulo;
	}
	LL_multilinear_combination(N[ltemp],-coeff0,N[l1],-coeff1,N[l2],-coeff2,N[l3],-coeff3,N[l],modulo,c,effcmax);
	ltemp = l3;
	continue;
      }
#endif
      int coeff;
      if (ludecomp) {
	int tmp=N[ltemp][pivotcol] % modulo;
	coeff = int(N[ltemp][pivotcol] = (longlong(tmp)*pivotval) % modulo);
      }
      else {
	coeff = (N[ltemp][pivotcol] %= modulo);
	if (rref_or_det_or_lu==1)
	  coeff = (coeff * longlong(pivotval))%modulo;
      }
      modlinear_combination(N[ltemp],-coeff,N[l],modulo,(rref_or_det_or_lu>0)?(c+1):c,effcmax);
    }
  }

  struct thread_modular_reduction_t {
    vector< vector<int> > * Nptr;
    vector<int> * pivotcols;
    int l,pivotcol,pivotval,linit,lmax,c,effcmax,rref_or_det_or_lu,modulo;
    bool debuginfo;
  };

  void * do_thread_modular_reduction(void * ptr_){
    thread_modular_reduction_t * ptr=(thread_modular_reduction_t *) ptr_;
    do_modular_reduction(*ptr->Nptr,ptr->l,ptr->pivotcol,ptr->pivotval,ptr->linit,ptr->lmax,ptr->c,ptr->effcmax,ptr->rref_or_det_or_lu,ptr->modulo);
    return ptr;
  }
    
  void * do_thread_lower_reduction(void * ptr_){
    thread_modular_reduction_t * ptr=(thread_modular_reduction_t *) ptr_;
    smallmodrref_lower(*ptr->Nptr,ptr->linit,ptr->l,ptr->lmax,ptr->c,ptr->effcmax,*ptr->pivotcols,ptr->modulo,ptr->debuginfo);
    return ptr;
  }

  // attempt to speedup smallmodrref by using longlong intermediate matrix
  // modulo is assumed prime if carac==-1 
  // or compute det (rref_or_det_or_lu==1) and modulo==carac^k with carac prime
  bool LLsmallmodrref(vector< vector<int> > & Nint,int l_,int lmax_,int c_,int cmax_,vecteur & pivots,vector<int> & permutation,vector<int> & maxrankcols,longlong & idet,int fullreduction,int dont_swap_below,int modulo,int carac,int rref_or_det_or_lu){
    // return false;
    bool inverting=fullreduction==2;
    int L=lmax_-l_,C=cmax_-c_;
    // copy Nint matrix
    vector< vector<longlong> > N(L);
    for (int i=0;i<L;++i){
      const vector<int> & source=Nint[l_+i];
      if (source.empty()) continue;
      vector<longlong> & target=N[i];
      target.resize(C);
      for (int j=0;j<C;++j)
	target[j]=source[c_+j];
    }
    // reduce N, reflect line permutations for empty lines in Nint
    int l=0,lmax=L,c=0,cmax=C,pivotline,pivotcol,pivot,temp;
    bool noswap;
    for (;l<lmax && c<cmax;){
      pivot = N[l].empty()?0:(N[l][c] %= modulo);
      if (rref_or_det_or_lu==3 && !pivot){
	idet=0;
	return true;
      }
      if ( rref_or_det_or_lu==1 && l==lmax-1 ){
	idet = (idet * pivot) % modulo ;
	break;
      }
      pivotline=l;
      pivotcol=c;
      if ( (carac==-1?pivot:(pivot % carac))==0 ){ // scan current line
	noswap=false;
	if (l<dont_swap_below){ 
	  for (int ctemp=c+1;ctemp<cmax;++ctemp){
	    temp = N[l].empty()?0:(N[l][ctemp] %= modulo);
	    if (carac==-1?temp:(temp % carac)){
	      pivot=smod(temp,modulo);
	      pivotcol=ctemp;
	      break;
	    }
	  }
	}
	else {      // scan N current column for the best pivot available
	  for (int ltemp=l+1;ltemp<lmax;++ltemp){
	    temp = N[ltemp].empty()?0:(N[ltemp][c] %= modulo);
	    if (carac==-1?temp:(temp % carac)){
	      pivot=smod(temp,modulo);
	      pivotline=ltemp;
	      break;
	    }
	  }
	}
      } // end if is_zero(pivot), 
      if ( (carac==-1?pivot:(pivot % carac))==0 ){
	if (carac>0){
	  if (rref_or_det_or_lu!=1)
	    return false;
	  bool not0=false;
	  // divide column by carac, multiply det by carac and retry
	  idet = (longlong(idet)*carac) % modulo;
	  for (int L=l;L<lmax;++L){
	    if (!N[L].empty()){ 
	      if (N[L][c] /= carac)
		not0=true;
	    }
	  }
	  if (not0)
	    continue;
	}
	idet = 0;
	if (rref_or_det_or_lu==1)
	  break;
	if (l>=dont_swap_below)
	  c++;
	else
	  l++;
	continue;
      }
      // true pivot found on line or column
      if (debug_infolevel>1){
	if (l%10==9){ CERR << "+"; CERR.flush();}
	if (l%500==499){ CERR << CLOCK() << " remaining " << lmax-l << endl; }
      }
      maxrankcols.push_back(c_+c);
      if (l!=pivotline){
	swap(N[l],N[pivotline]);
	swap(Nint[l_+l],Nint[l_+pivotline]);
	swap(permutation[l_+l],permutation[l_+pivotline]);
	pivotline=l;
	idet = -idet;
      }
      // save pivot for annulation test purposes
      if (rref_or_det_or_lu!=1)
	pivots.push_back(pivot);
      // invert pivot. If pivot==1 we might optimize but only if allow_bloc is true
      temp=invmod(pivot,modulo);
      // multiply det
      idet = (idet * pivot) % modulo ;
      if (fullreduction || rref_or_det_or_lu<1){ // not LU decomp
	vector<longlong>::iterator it=N[pivotline].begin()+c,itend=N[pivotline].end();
	for (;it!=itend;++it){
	  longlong tmp=*it;
	  if (!tmp) continue;
	  tmp %= modulo;
	  tmp=(temp * tmp)%modulo;
	  *it=tmp; // *it=smod_adjust(tmp,modulo);
	}
      }
      else {
	// reduce remainder of line pivotline
	vector<longlong>::iterator it=N[pivotline].begin()+c,itend=N[pivotline].end();
	for (;it!=itend;++it){
	  longlong tmp=*it;
	  if (!tmp) continue;
	  *it=tmp%modulo;
	}
      }
      // if there are 0 at the end, ignore them in linear combination
      int effcmax=(fullreduction && inverting && noswap)?c+lmax:cmax-1;
      const std::vector<longlong> & Npiv=N[l];
      for (;effcmax>=c;--effcmax){
	if (Npiv[effcmax])
	  break;
      }
      ++effcmax;
      int effl=fullreduction?0:l+1;
      LL_modular_reduction(N,l,pivotcol,temp,effl,lmax,c,effcmax,rref_or_det_or_lu,modulo);
      // increment column number if swap was allowed
      if (l>=dont_swap_below)
	++c;
      // increment line number since reduction has been done
      ++l;	  	
    } // for (l<lmax && c<cmax)
    // back copy into Nint
    if (rref_or_det_or_lu!=1){
      for (int i=0;i<L;++i){
	const vector<longlong> & source=N[i];
	vector<int> & target=Nint[l_+i];
	if (source.empty()){ 
	  if (!target.empty())
	    CERR << "inconsistency" << endl;
	  continue;
	}
	else {
	  if (target.empty())
	    CERR << "inconsistency" << endl;
	}
	for (int j=0;j<C;++j)
	  target[c_+j]=smod(source[j],modulo);
      }
    }
    return true;
  }
    
  // if dont_swap_below !=0, for line numers < dont_swap_below
  // the pivot is searched in the line instead of the column
  // hence no line swap occur
  // rref_or_det_or_lu = 0 for rref, 1 for det, 2 for lu, 
  // 3 for lu without permutation
  // fullreduction=0 or 1, use 2 if the right part of a is idn
  void smallmodrref(int nthreads,vector< vector<int> > & N,vecteur & pivots,vector<int> & permutation,vector<int> & maxrankcols,longlong & idet,int l, int lmax, int c,int cmax,int fullreduction,int dont_swap_below,int modulo,int rref_or_det_or_lu,bool reset,smallmodrref_temp_t * workptr,bool allow_block,int carac){
    bool inverting=fullreduction==2;
    int linit=l;//,previous_l=l;
    // Reduction
    int pivot,temp=0;
    // vecteur vtemp;
    int pivotline,pivotcol=0;
    if (reset){
      idet=1;
      pivots.clear();
      pivots.reserve(cmax-c);
      permutation.clear();
      maxrankcols.clear();
      for (int i=0;i<lmax;++i)
	permutation.push_back(i);
    }
    int ilmax=lmax;
    if (allow_block){
      for (int i=0;i<lmax-1;){
      if (N[lmax-1].empty()){
      --lmax;
      continue;
      }
      if (N[i].empty()){
	swap(N[i],N[lmax-1]);
	swap(permutation[i],permutation[lmax-1]);
	--lmax;
      }
      ++i;
    }
    }
    if (debug_infolevel>2)
      CERR << CLOCK() << " Effective number of rows " << lmax << "/" << ilmax << endl;
    bool noswap=true;
    smallmodrref_temp_t * tmpptr = workptr;
#ifndef GIAC_HAS_STO_38 
    if (allow_block && (rref_or_det_or_lu==0 
			//|| rref_or_det_or_lu==1
			) && dont_swap_below==0 ){ 
      if (
	  //lmax-l>=4 && cmax-c>=4 
	  lmax-l>=2.5*mmult_int_blocksize && cmax-c>=2.5*mmult_int_blocksize
	){
	// this is not as fast as block reduction for dense matrices
	// a sparsness test could be useful
	// reduce first half
	int halfl=(lmax-l)/2,effl=l+halfl;
	if (debug_infolevel>2)
	  CERR << CLOCK() << " rref begin " << lmax-l << "x" << cmax-c << endl;
	smallmodrref(nthreads,N,pivots,permutation,maxrankcols,idet,l,l+halfl,c,cmax,0/*fullreduction*/,0,modulo,0,false,workptr,true,carac);
	// use first half for second half
	vector<int> pivotcols;
	smallmodrref_lower_pivots(N,l,effl,c,cmax,pivotcols,modulo);
	bool reduction_done=false;
	if (debug_infolevel>2)
	  CERR << CLOCK() << " rref_lower begin " << effl << ".." << lmax << "/" << c << ".." << cmax << endl;
	// CERR << pivotcols << endl;
#ifdef HAVE_LIBPTHREAD
	if (nthreads>1 && double(lmax-effl)*(cmax-c)>1e5){
	  pthread_t tab[64];
	  thread_modular_reduction_t redparam[64];
	  if (nthreads>64) nthreads=64;
	  for (int j=0;j<nthreads;++j){
	    thread_modular_reduction_t tmp={&N,&pivotcols,effl,pivotcol,temp,l,lmax,c,cmax,rref_or_det_or_lu,modulo,j==0 && debug_infolevel>2};
	    redparam[j]=tmp;
	  }
	  int kstep=int(std::ceil((lmax-effl)/double(nthreads))),k=effl;
	  for (int j=0;j<nthreads;++j){
	    redparam[j].l=k;
	    k += kstep;
	    if (k>lmax)
	      k=lmax;
	    redparam[j].lmax=k;
	    bool res=true;
	    if (j<nthreads-1)
	      res=pthread_create(&tab[j],(pthread_attr_t *) NULL,do_thread_lower_reduction,(void *) &redparam[j]);
	    if (res)
	      do_thread_lower_reduction((void *)&redparam[j]);
	  }
	  for (int j=0;j<nthreads;++j){
	    void * ptr=(void *)&nthreads; // non-zero initialisation
	    if (j<nthreads-1)
	      pthread_join(tab[j],&ptr);
	  }
	  reduction_done=true;
	}
#endif
	if (!reduction_done)
	  smallmodrref_lower(N,l,effl,lmax,c,cmax,pivotcols,modulo,debug_infolevel>2);
	if (debug_infolevel>2)
	  CERR << CLOCK() << " rref_lower end " << effl << ".." << lmax << "/" << c << ".." << cmax << endl;
	// reduce second half
	//cerr << N <<endl;
	smallmodrref(nthreads,N,pivots,permutation,maxrankcols,idet,l+halfl,lmax,c+(idet && rref_or_det_or_lu==1?halfl:0),cmax,0/*fullreduction*/,0,modulo,0,false,workptr,true,carac);
	if (debug_infolevel>2)
	  CERR << CLOCK() << " rref end " << lmax-l << "x" << cmax-c << endl;
	//cerr << N <<endl;
#if 1
	// finish reduction with permutations only
	int L=l,C=c,r;
	for (;L<lmax && C<cmax;){
	  for (r=L;r<lmax;++r){
	    if (!N[r].empty() && N[r][C])
	      break;
	  }
	  if (r==lmax){ 
	    ++C; continue;
	  }
	  if (r>L){
	    if (N[r][C]!=1)
	      COUT << "erreur" << N[r][C] << endl;
	    swap(N[r],N[L]);
	    swap(permutation[r],permutation[L]);
	    idet=-idet;
	    // swap(pivots[r],pivots[L]);
	  }
	  ++L; ++C;
	}
	if (fullreduction)
	  smallmodrref_upper(N,l,lmax,c,cmax,modulo);
	return;
      } else nthreads=1;
#endif
    }
    bool blocktest=allow_block && rref_or_det_or_lu!=0 && lmax-l>=2*mmult_int_blocksize && cmax-c>=2*mmult_int_blocksize;
    if (blocktest){
      // count 0 in N[l->lmax][c->cmax]
      // if matrix is sparse, then block operations is not faster
      double count=0;
      for (int i=l;i<lmax;++i){
	if (N[i].empty()){ blocktest=false; break; }
	vector<int>::const_iterator it=N[l].begin()+c,itend=N[l].begin()+giacmin(cmax,N[l].size());
	for (;it!=itend;++it){
	  if (!*it)
	    ++count;
	}
      }
      count=(count/(lmax-l)/(cmax-c));
      if (count>.8)
	blocktest=false;
    }
    if (!workptr){
      if (blocktest)
	tmpptr = new smallmodrref_temp_t;
      else
	tmpptr=0;
    }
    if (//0 &&
	rref_or_det_or_lu==2 && 
	giacmax(lmax-l,cmax-c)*double(modulo)*modulo<(1ULL << 63) &&
	blocktest
	){
      // diag(P1,P2)*[[A,B],[C,D]]=[[L1,0],[L3,L2]]*[[U1,U3],[0,U2]]
      // hence P1*A=L1*U1, recursive call will determine L1, U1 and P1
      // if A is not invertible, failure (keep a copy of A in case)
      // line swaps corresponding to P1 will replace inplace B by P1*B
      // then P1*B=L1*U3, determine columns of U3 by int_linsolve_l, replace B with U3
      // keep columns of U3 in lines for later use in matrix product
      // P2*C=L3*U1, hence P2^-1*L3 is determined by int_linsolve_u, replace C with P2^-1*L3
      // P2*D=L3*U3+L2*U2 -> P2*(D-P2^-1*L3*U3)=L2*U2
      // substract P2^-1*L3*U3 from D and recursive call to lu will determine P2, L2 and U2
      // (line swaps will replace inplace P2^-1*L3 by L3)
      int taille=giacmin(lmax-l,cmax-c)/2;
      if (debug_infolevel>2)
	CERR << CLOCK() << " recursive call mod " << modulo << " size " << taille << endl;
      tmpptr->Ainv.resize(cmax-c-taille);
      tmpptr->y.resize(taille);
      tmpptr->y1.resize(taille);
      tmpptr->y2.resize(taille);
      tmpptr->y3.resize(taille);
      tmpptr->z.resize(taille);
      tmpptr->z1.resize(taille);
      tmpptr->z2.resize(taille);
      tmpptr->z3.resize(taille);
      for (int i=0;i<taille;i++){
	int * source=&N[l+i][c];
	tmpptr->Ainv[i].resize(taille);
	vector<int>::iterator it=tmpptr->Ainv[i].begin(),itend=tmpptr->Ainv[i].end();
	for (;it!=itend;++source,++it)
	  *it=*source;
      }
      smallmodrref(nthreads,N,pivots,permutation,maxrankcols,idet,l,l+taille,c,c+taille,false,false,modulo,2,false,0,true,carac);
      if (!idet){
	// restore N from tmpptr->Ainv
	for (int i=0;i<taille;++i){
	  int * target=&N[l+i][c];
	  vector<int>::const_iterator it=tmpptr->Ainv[i].begin(),itend=tmpptr->Ainv[i].end();
	  for (;it!=itend;++target,++it)
	    *target = *it;
	}
      }
      else {
	// find U3: L1*U3=P1*B, cmax-c-taille systems to solve, each has taille unknowns
	int i=0;
	for (;i<=cmax-c-taille-4;i+=4){
	  for (int j=0;j<taille;j++){
	    tmpptr->y[j]=N[l+j][i+c+taille];
	    tmpptr->y1[j]=N[l+j][i+1+c+taille];
	    tmpptr->y2[j]=N[l+j][i+2+c+taille];
	    tmpptr->y3[j]=N[l+j][i+3+c+taille];
	  }
	  int_linsolve_l4(N,l,c,tmpptr->y,tmpptr->y1,tmpptr->y2,tmpptr->y3,tmpptr->Ainv[i],tmpptr->Ainv[i+1],tmpptr->Ainv[i+2],tmpptr->Ainv[i+3],modulo);
	  // copy into N
	  for (int j=0;j<taille;j++){
	    N[l+j][i+c+taille]=tmpptr->Ainv[i][j];
	    N[l+j][i+1+c+taille]=tmpptr->Ainv[i+1][j];
	    N[l+j][i+2+c+taille]=tmpptr->Ainv[i+2][j];
	    N[l+j][i+3+c+taille]=tmpptr->Ainv[i+3][j];
	  }
	}
	for (;i<cmax-c-taille;++i){
	  for (int j=0;j<taille;j++){
	    tmpptr->y[j]=N[l+j][i+c+taille];
	  }
	  int_linsolve_l(N,l,c,tmpptr->y,tmpptr->Ainv[i],modulo);
	  // copy into N
	  for (int j=0;j<taille;j++)
	    N[l+j][i+c+taille]=tmpptr->Ainv[i][j];
	}
	// find P2^-1*L3: P2^-1*L3*U1=C, lmax-l-taille systems to solve, each with taille unknowns
	for (i=0;i<=lmax-l-taille-4;i+=4){
	  for (int j=0;j<taille;j++){
	    tmpptr->y[j]=N[i+l+taille][c+j];
	    tmpptr->y1[j]=N[i+1+l+taille][c+j];
	    tmpptr->y2[j]=N[i+2+l+taille][c+j];
	    tmpptr->y3[j]=N[i+3+l+taille][c+j];
	  }	  
	  int_linsolve_u4(N,l,c,tmpptr->y,tmpptr->y1,tmpptr->y2,tmpptr->y3,tmpptr->z,tmpptr->z1,tmpptr->z2,tmpptr->z3,modulo);
	  for (int j=0;j<taille;j++){
	    N[i+l+taille][c+j]=tmpptr->z[j];
	    N[i+1+l+taille][c+j]=tmpptr->z1[j];
	    N[i+2+l+taille][c+j]=tmpptr->z2[j];
	    N[i+3+l+taille][c+j]=tmpptr->z3[j];
	  }	  	  
	}	
	for (;i<lmax-l-taille;++i){
	  for (int j=0;j<taille;j++){
	    tmpptr->y[j]=N[i+l+taille][c+j];
	  }	  
	  int_linsolve_u(N,l,c,tmpptr->y,tmpptr->z,modulo);
	  for (int j=0;j<taille;j++){
	    N[i+l+taille][c+j]=tmpptr->z[j];
	  }	  	  
	}	
	// substract L3*U3 from D
	in_mmult_mod(N,tmpptr->Ainv,N,l+taille,c+taille,modulo,l+taille,lmax,c,c+taille,false);
	// final lu decomposition
	smallmodrref(nthreads,N,pivots,permutation,maxrankcols,idet,l+taille,lmax,c+taille,cmax,false,false,modulo,2,false,0,true,carac);
	if (debug_infolevel>2)
	  CERR << CLOCK() << " end recursive call mod " << modulo << " size " << taille << endl;
	// matrice dbg;
	// vectvector_int2vecteur(N,dbg);
	// CERR << smod(dbg,modulo) << endl;
	if (!workptr && tmpptr)
	  delete tmpptr;
	return;
      } // end else idet==0
    }
#endif // GIAC_HAS_STO_38
#ifdef GIAC_DETBLOCK
    int det_blocksize=mmult_int_blocksize;
    bool tryblock=blocktest && rref_or_det_or_lu==1 && giacmax(mmult_int_blocksize,det_blocksize)*double(modulo)*modulo<((1ULL << 63)) && lmax-l>=3*det_blocksize && cmax-c>=3*det_blocksize;
    // commented because it's slower...
    // if (tryblock) det_blocksize=giacmin((lmax-l)/3,(cmax-c)/3);
    if (tmpptr){
      tmpptr->Ainvtran.resize(det_blocksize);
      tmpptr->Ainv.resize(det_blocksize);
    }
#endif
    for (;(l<lmax) && (c<cmax);){
#ifdef GIAC_DETBLOCK
      if (tryblock &&lmax-l>=3*det_blocksize && cmax-c>=3*det_blocksize && l % det_blocksize==0 && c % det_blocksize==0){
	// try to invert block of size det_blocksize
	for (int i=0;i<det_blocksize;++i){
	  tmpptr->Ainv[i].reserve(2*det_blocksize);
	  tmpptr->Ainv[i].resize(det_blocksize);
	  int * Ai=&tmpptr->Ainv[i][0];
	  int * Ni=&N[l+i][c], *Niend=Ni+det_blocksize;
	  for (;Ni!=Niend;++Ai,++Ni)
	    *Ai=*Ni;
	}
	// lu
	tmpptr->permblock.clear(); tmpptr->maxrankblock.clear(); tmpptr->pivblock.clear();
	longlong idetblock;
	if (debug_infolevel>2)
	  CERR << CLOCK() << "block reduction mod " << modulo << " size " << det_blocksize << " " << workptr << endl;
	smallmodrref(nthreads,tmpptr->Ainv,tmpptr->pivblock,tmpptr->permblock,tmpptr->maxrankblock,idetblock,0,det_blocksize,0,det_blocksize,0,false,modulo,2,true,0,true,carac);
	if (idetblock){
	  idet = ((idetblock % modulo)*idet)%modulo;
	  int_lu2inv(tmpptr->Ainv,modulo,tmpptr->permblock);
	  // [[A^-1,0],[-C*A^-1,I]] * [[A,B],[C,D]] = [[I,A^-1*B],[0,-C*A^-1*B+D]]
	  // first compute -C*A^-1
	  // transpose Ainv and negate
	  tran_int(tmpptr->Ainv,tmpptr->Ainvtran);
	  negate_int(tmpptr->Ainvtran);
	  mmult_mod(N,tmpptr->Ainvtran,tmpptr->CAinv,modulo,l+det_blocksize,lmax,c,c+det_blocksize);
	  // transpose B
	  tran_int(N,tmpptr->Ainv,l,l+det_blocksize,c+det_blocksize,cmax);
	  // D += CAinv*B
	  l += det_blocksize;
	  c += det_blocksize;
#if 0
	  mmult_mod(tmpptr->CAinv,tmpptr->Ainv,N,modulo,0,0,0,0,0,0,0,l,c,true); 
#else
	  in_mmult_mod(tmpptr->CAinv,tmpptr->Ainv,N,l,c,modulo,0,0,0,0,true);
#endif
	  continue;
	}
      } // end tryblock
#endif // GIAC_DETBLOCK
      // normal Gauss reduction
      if (
	  (carac>0 || (lmax-l>=32 && cmax-c>=32) ) && (lmax-l)*double(modulo)*double(modulo)<(1ULL<<63) &&
	  //double(lmax-l)*(cmax-c)*sizeof(longlong)<128e3 &&
	  LLsmallmodrref(N,l,lmax,c,cmax,pivots,permutation,maxrankcols,idet,fullreduction,dont_swap_below,modulo,carac,rref_or_det_or_lu)){
	break;
      }
      pivot = N[l].empty()?0:(N[l][c] %= modulo);
      if (rref_or_det_or_lu==3 && !pivot){
	idet=0;
	if (!workptr && tmpptr)
	  delete tmpptr;
	return;
      }
      if ( rref_or_det_or_lu==1 && l==lmax-1 ){
	idet = (idet * pivot) % modulo ;
	break;
      }
      pivotline=l;
      pivotcol=c;
      if (!pivot){ // scan current line
	noswap=false;
	if (l<dont_swap_below){ 
	  for (int ctemp=c+1;ctemp<cmax;++ctemp){
	    temp = N[l].empty()?0:(N[l][ctemp] %= modulo);
	    if (temp){
	      pivot=smod(temp,modulo);
	      pivotcol=ctemp;
	      break;
	    }
	  }
	}
	else {      // scan N current column for the best pivot available
	  for (int ltemp=l+1;ltemp<lmax;++ltemp){
	    temp = N[ltemp].empty()?0:(N[ltemp][c] %= modulo);
	    if (debug_infolevel>2)
	      print_debug_info(temp);
	    if (temp){
	      pivot=smod(temp,modulo);
	      pivotline=ltemp;
	      break;
	    }
	  }
	}
      } // end if is_zero(pivot), true pivot found on line or column
      if (pivot){
	if (debug_infolevel>1){
	  if (l%10==9){ CERR << "+"; CERR.flush();}
	  if (l%500==499){ CERR << CLOCK() << " remaining " << lmax-l << endl; }
	}
	maxrankcols.push_back(c);
	if (l!=pivotline){
	  swap(N[l],N[pivotline]);
	  swap(permutation[l],permutation[pivotline]);
	  pivotline=l;
	  idet = -idet;
	}
	// save pivot for annulation test purposes
	if (rref_or_det_or_lu!=1)
	  pivots.push_back(pivot);
	// invert pivot. If pivot==1 we might optimize but only if allow_bloc is true
	if (0 && pivot==1 && allow_block)
	  temp=1; // can not be activated because pseudo-mod expect reducing line to be smaller than p
	else {
	  temp=invmod(pivot,modulo);
	  // multiply det
	  idet = (idet * pivot) % modulo ;
	  if (fullreduction || rref_or_det_or_lu<2){ // not LU decomp
	    vector<int>::iterator it=N[pivotline].begin()+c,itend=N[pivotline].end();
	    for (;it!=itend;++it){
	      int tmp=*it;
	      if (!tmp) continue;
	      tmp=(longlong(temp) * tmp)%modulo;
	      *it=smod_adjust(tmp,modulo);
	    }
	  }
	}
	// if there are 0 at the end, ignore them in linear combination
	int effcmax=(fullreduction && inverting && noswap)?c+lmax:cmax-1;
	const std::vector<int> & Npiv=N[l];
	for (;effcmax>=c;--effcmax){
	  if (Npiv[effcmax])
	    break;
	}
	++effcmax;
	// make the reduction
	bool do_reduction=true;
	int effl=fullreduction?linit:l+1;
#ifdef HAVE_LIBPTHREAD
	if (nthreads>1 && double(lmax-effl)*(effcmax-c)>1e5){
	  pthread_t tab[64];
	  thread_modular_reduction_t redparam[64];
	  if (nthreads>64) nthreads=64;
	  for (int j=0;j<nthreads;++j){
	    thread_modular_reduction_t tmp={&N,0,l,pivotcol,temp,linit,lmax,c,effcmax,rref_or_det_or_lu,modulo,j==0 && debug_infolevel>2};
	    redparam[j]=tmp;
	  }
	  int kstep=int(std::ceil((lmax-effl)/double(nthreads))),k=effl;
	  for (int j=0;j<nthreads;++j){
	    redparam[j].linit=k;
	    k += kstep;
	    if (k>lmax)
	      k=lmax;
	    redparam[j].lmax=k;
	    bool res=true;
	    if (j<nthreads-1)
	      res=pthread_create(&tab[j],(pthread_attr_t *) NULL,do_thread_modular_reduction,(void *) &redparam[j]);
	    if (res)
	      do_thread_modular_reduction((void *)&redparam[j]);
	  }
	  for (int j=0;j<nthreads;++j){
	    void * ptr=(void *)&nthreads; // non-zero initialisation
	    if (j<nthreads-1)
	      pthread_join(tab[j],&ptr);
	  }
	  do_reduction=false;
	}
#endif
	if (do_reduction)
	  do_modular_reduction(N,l,pivotcol,temp,effl,lmax,c,effcmax,rref_or_det_or_lu,modulo);
	// increment column number if swap was allowed
	if (l>=dont_swap_below)
	  ++c;
	// increment line number since reduction has been done
	++l;	  
      } // end if (!is_zero(pivot)
      else { // if pivot is 0 increment either the line or the col
	idet = 0;
	if (rref_or_det_or_lu==1){
	  if (!workptr && tmpptr)
	    delete tmpptr;
	  return;
	}
	if (l>=dont_swap_below)
	  c++;
	else
	  l++;
      }
    } // end for reduction loop
    if (rref_or_det_or_lu!=1){
      for (int i=0;i<lmax;i++){
	if (N[i].empty())
	  continue;
	int * Ni=&N[i][0], * Niend= Ni+cmax; // vector<int> & Ni=N[i];
	if (rref_or_det_or_lu==2)
	  Ni += i;
	for (;Ni!=Niend;++Ni){
	  if (*Ni){
#if 1
	    *Ni=smod(*Ni,modulo);
#else
	    longlong r = *Ni % modulo;
	    if ( (r<<1) > modulo){
	      *Ni = r-modulo;
	      continue;
	    }
	    if ( (r<<1) > -modulo)
	      *Ni = r;
	    else
	      *Ni = r-modulo;
#endif
	  }
	}
      }
    }
    if (!workptr && tmpptr)
      delete tmpptr;
  }


  struct doublerref_temp_t {
    matrix_double Ainvtran,Ainv,CAinv;
    std::vector<int> permblock,maxrankblock; 
    vecteur pivblock; 
    std::vector<double> y,y1,y2,y3;
    std::vector<double> z,z1,z2,z3;
  };
  // if dont_swap_below !=0, for line numers < dont_swap_below
  // the pivot is searched in the line instead of the column
  // hence no line swap occur
  // rref_or_det_or_lu = 0 for rref, 1 for det, 2 for lu, 
  // 3 for lu without permutation
  // fullreduction=0 or 1, use 2 if the right part of a is idn
  void in_doublerref(matrix_double & N,vecteur & pivots,vector<int> & permutation,vector<int> & maxrankcols,double & idet,int l, int lmax, int c,int cmax,int fullreduction,int dont_swap_below,double eps,int rref_or_det_or_lu,bool reset,doublerref_temp_t * workptr){
    if (debug_infolevel)
      CERR << CLOCK() << " doublerref begin " << l << endl;
    bool use_cstart=!c;
    bool inverting=fullreduction==2;
    // alternative for inverting large matrices
    // [[A,B],[C,D]]^-1=[[E,F],[G,H]]
    // H=(D-C*A^-1*B)^-1
    // G=-H*C*A^-1
    // F=-A^-1*B*H
    // E=A^-1-F*C*A^-1=A^-1-A^-1*B*G=A^-1-A^-1*B*(D-C*A^-1*B)^-1*C*A^-1
    // indeed A*E+B*G=I, A*F+B*H=0, C*E+D*G, C*F+D*H
    // compute A^-1, then 3 products A^-1*B, C*A^-1, D-C*A^-1*B, 
    // inverse, two products for F and G, and one for E
#ifndef GIAC_HAS_STO_38
    if (inverting){
      fullreduction=0;
      rref_or_det_or_lu=2;
      cmax=lmax;
    }
#endif
    int linit=l;//,previous_l=l;
    // Reduction
    double pivot,temp;
     // vecteur vtemp;
    int pivotline,pivotcol;
    if (reset){
      idet=1;
      pivots.clear();
      pivots.reserve(cmax-c);
      permutation.clear();
      maxrankcols.clear();
      for (int i=0;i<lmax;++i)
	permutation.push_back(i);
    }
    bool noswap=true;
    double epspivot=(eps<1e-13)?1e-13:eps;
    doublerref_temp_t * tmpptr=workptr;
#ifndef GIAC_HAS_STO_38 
    bool blocktest=lmax-l>=2*mmult_double_blocksize && cmax-c>=2*mmult_double_blocksize;
    if (blocktest){
      // count 0 in N[l->lmax][c->cmax]
      // if matrix is sparse, then block operations is not faster
      double count=0;
      for (int i=l;i<lmax;++i){
	vector<giac_double>::const_iterator it=N[l].begin()+c,itend=N[l].begin()+cmax;
	for (;it!=itend;++it){
	  if (!*it)
	    ++count;
	}
      }
      count=(count/(lmax-l)/(cmax-c));
      if (count>.8)
	blocktest=false;
    }
    // block operation with double has lower precision, 
    // because pivot absolute value is smaller than with the full matrix
    if (!workptr){
      if (blocktest)
	tmpptr = new doublerref_temp_t;
      else
	tmpptr=0;
    }
    if (//0 &&
	rref_or_det_or_lu==2 && blocktest
	){
      // diag(P1,P2)*[[A,B],[C,D]]=[[L1,0],[L3,L2]]*[[U1,U3],[0,U2]]
      // hence P1*A=L1*U1, recursive call will determine L1, U1 and P1
      // if A is not invertible, failure (keep a copy of A in case)
      // line swaps corresponding to P1 will replace inplace B by P1*B
      // then P1*B=L1*U3, determine columns of U3 by int_linsolve_l, replace B with U3
      // keep columns of U3 in lines for later use in matrix product
      // P2*C=L3*U1, hence P2^-1*L3 is determined by int_linsolve_u, replace C with P2^-1*L3
      // P2*D=L3*U3+L2*U2 -> P2*(D-P2^-1*L3*U3)=L2*U2
      // substract P2^-1*L3*U3 from D and recursive call to lu will determine P2, L2 and U2
      // (line swaps will replace inplace P2^-1*L3 by L3)
      int taille=mmult_double_blocksize;
      if (debug_infolevel>2)
	CERR << CLOCK() << " recursive call double size " << taille << endl;
      tmpptr->y.resize(taille);
      tmpptr->y1.resize(taille);
      tmpptr->y2.resize(taille);
      tmpptr->y3.resize(taille);
      tmpptr->z.resize(taille);
      tmpptr->z1.resize(taille);
      tmpptr->z2.resize(taille);
      tmpptr->z3.resize(taille);
      tmpptr->Ainv.resize(cmax-c-taille);
      for (int i=0;i<taille;i++){
	double * source=&N[l+i][c];
	tmpptr->Ainv[i].resize(taille);
	vector<double>::iterator it=tmpptr->Ainv[i].begin(),itend=tmpptr->Ainv[i].end();
	for (;it!=itend;++source,++it)
	  *it=*source;
      }
      in_doublerref(N,pivots,permutation,maxrankcols,idet,l,lmax,c,c+taille,false,false,eps,2,false,0);
      // find U3: L1*U3=P1*B, cmax-c-taille systems to solve, each has taille unknowns
      int i=0;
      for (;i<=cmax-c-taille-4;i+=4){
	for (int j=0;j<taille;j++){
	  tmpptr->y[j]=N[l+j][i+c+taille];
	  tmpptr->y1[j]=N[l+j][i+1+c+taille];
	  tmpptr->y2[j]=N[l+j][i+2+c+taille];
	  tmpptr->y3[j]=N[l+j][i+3+c+taille];
	}
	double_linsolve_l4(N,l,c,tmpptr->y,tmpptr->y1,tmpptr->y2,tmpptr->y3,tmpptr->Ainv[i],tmpptr->Ainv[i+1],tmpptr->Ainv[i+2],tmpptr->Ainv[i+3]);
	// copy into N
	for (int j=0;j<taille;j++){
	  N[l+j][i+c+taille]=tmpptr->Ainv[i][j];
	  N[l+j][i+1+c+taille]=tmpptr->Ainv[i+1][j];
	  N[l+j][i+2+c+taille]=tmpptr->Ainv[i+2][j];
	  N[l+j][i+3+c+taille]=tmpptr->Ainv[i+3][j];
	}
      }
      for (;i<cmax-c-taille;++i){
	for (int j=0;j<taille;j++){
	  tmpptr->y[j]=N[l+j][i+c+taille];
	}
	double_linsolve_l(N,l,c,tmpptr->y,tmpptr->Ainv[i]);
	// copy into N
	for (int j=0;j<taille;j++)
	  N[l+j][i+c+taille]=tmpptr->Ainv[i][j];
      }
      // substract L3*U3 from D
      in_mmult_double(N,tmpptr->Ainv,N,l+taille,c+taille,l+taille,lmax,c,c+taille,false);
      // final lu decomposition
      in_doublerref(N,pivots,permutation,maxrankcols,idet,l+taille,lmax,c+taille,cmax,false,false,eps,2,false,0);
      if (debug_infolevel>2)
	CERR << CLOCK() << " end recursive call double size " << taille << endl;
      // matrice dbg;
      // vectvector_int2vecteur(N,dbg);
      // CERR << smod(dbg,modulo) << endl;
      if (!workptr && tmpptr)
	delete tmpptr;
#ifndef GIAC_HAS_STO_38
      if (inverting)
	double_lu2inv(N,permutation);
#endif
      return;
    }
#endif // GIAC_HAS_STO_38
    for (;(l<lmax) && (c<cmax);){
      pivot=N[l][c];
      if (std::abs(pivot)<epspivot)
	pivot=N[l][c]=0;
      if (rref_or_det_or_lu==3 && !pivot){
	idet=0;
	return;
      }
      if ( rref_or_det_or_lu==1 && l==lmax-1 ){
	idet = (idet * pivot);
	break;
      }
      pivotline=l;
      pivotcol=c;
      noswap=false;
      if (l<dont_swap_below){ 
	for (int ctemp=c+1;ctemp<cmax;++ctemp){
	  temp=N[l][ctemp];
	  if (std::abs(temp)<epspivot)
	    temp=N[l][ctemp]=0;
	  if (std::abs(temp)>std::abs(pivot)){
	    pivot=temp;
	    pivotcol=ctemp;
	  }
	}
      }
      else {      // scan N current column for the best pivot available
	for (int ltemp=l+1;ltemp<lmax;++ltemp){
	  temp=N[ltemp][c];
	  if (std::abs(temp)<epspivot)
	    temp=N[ltemp][c]=0;
	  if (debug_infolevel>3)
	    print_debug_info(temp);
	  if (std::abs(temp)>std::abs(pivot)){
	    pivot=temp;
	    pivotline=ltemp;
	  }
	}
      }
      if (pivot){
	epspivot=std::abs(eps*pivot);
	maxrankcols.push_back(c);
	if (l!=pivotline){
	  swap(N[l],N[pivotline]);
	  swap(permutation[l],permutation[pivotline]);
	  pivotline=l;
	  idet = -idet;
	}
	// save pivot for annulation test purposes
	if (rref_or_det_or_lu!=1)
	  pivots.push_back(pivot);
	// invert pivot 
	temp=1./pivot;
	// multiply det
	idet = idet * pivot ;
	if (fullreduction || rref_or_det_or_lu<2){ // not LU decomp
	  std::vector<giac_double>::iterator it=N[pivotline].begin(),itend=N[pivotline].end();
	  for (;it!=itend;++it){
	    *it /= pivot;
	  }
	}
	// if there are 0 at the end, ignore them in linear combination
	int effcmax=cmax-1;
	const std::vector<giac_double> & Npiv=N[pivotline];
	for (;effcmax>=c;--effcmax){
	  if (Npiv[effcmax])
	    break;
	}
	++effcmax;
	if (fullreduction && inverting && noswap)
	  effcmax=giacmax(effcmax,c+1+lmax);
	// make the reduction
	if (fullreduction){
	  for (int ltemp=linit;ltemp<lmax;++ltemp){
	    if (ltemp==l)
	      continue;
#ifndef GIAC_HAS_STO_38
	    if ( ((ltemp<=l-4) || (ltemp>l && ltemp<=lmax-4))){
	      double_multilinear_combination(N[ltemp],-N[ltemp][pivotcol],N[ltemp+1],-N[ltemp+1][pivotcol],N[ltemp+2],-N[ltemp+2][pivotcol],N[ltemp+3],-N[ltemp+3][pivotcol],N[l],(use_cstart?c:cmax),effcmax);
	      ltemp+= (4-1);
	    }
	    else
#endif
	      double_linear_combination(N[ltemp],-N[ltemp][pivotcol],N[l],(use_cstart?c:cmax),effcmax);
	  }
	}
	else {
	  for (int ltemp=l+1;ltemp<lmax;++ltemp){
	    if (rref_or_det_or_lu>=2) // LU decomp
	      N[ltemp][pivotcol] *= temp;
#ifndef GIAC_HAS_STO_38
	    if (ltemp<lmax-4){
	      if (rref_or_det_or_lu>=2){ // LU decomp
		N[ltemp+1][pivotcol] *=temp;
		N[ltemp+2][pivotcol] *=temp;
		N[ltemp+3][pivotcol] *=temp;
	      }
	      double_multilinear_combination(N[ltemp],-N[ltemp][pivotcol],N[ltemp+1],-N[ltemp+1][pivotcol],N[ltemp+2],-N[ltemp+2][pivotcol],N[ltemp+3],-N[ltemp+3][pivotcol],N[l],(rref_or_det_or_lu>0)?(c+1):(use_cstart?c:cmax),effcmax);
	      ltemp+= (4-1);
	    }
	    else
#endif
	      double_linear_combination(N[ltemp],-N[ltemp][pivotcol],N[l],(rref_or_det_or_lu>0)?(c+1):(use_cstart?c:cmax),effcmax);
	  }
	} // end else
	  // increment column number if swap was allowed
	if (l>=dont_swap_below)
	  ++c;
	// increment line number since reduction has been done
	++l;	  
      } // end if (!is_zero(pivot)
      else { // if pivot is 0 increment either the line or the col
	idet = 0;
	if (rref_or_det_or_lu==1)
	  return;
	if (l>=dont_swap_below)
	  c++;
	else
	  l++;
      }
    } // end for reduction loop
#ifndef GIAC_HAS_STO_38
    if (inverting){
      double_lu2inv(N,permutation);
      // double_lu2inv_inplace(N,permutation);
    }
#endif
  }

  void doublerref(matrix_double & N,vecteur & pivots,vector<int> & permutation,vector<int> & maxrankcols,double & idet,int l, int lmax, int c,int cmax,int fullreduction,int dont_swap_below,int rref_or_det_or_lu,double eps){
    in_doublerref(N,pivots,permutation,maxrankcols,idet,l,lmax,c,cmax,fullreduction,dont_swap_below,eps,rref_or_det_or_lu,true,0);
  }

  bool in_modrref(const matrice & a, vector< vector<int> > & N,matrice & res, vecteur & pivots, gen & det,int l, int lmax, int c,int cmax,int fullreduction,int dont_swap_below,int Modulo,int carac,int rref_or_det_or_lu,const gen & mult_by_det_mod_p,bool inverting,bool no_initial_mod,smallmodrref_temp_t * workptr){
#ifndef GIAC_HAS_STO_38
    if (no_initial_mod){
      const_iterateur it=a.begin(),itend=a.end();
      N.resize(itend-it);
      vector< vector<int> >::iterator kt=N.begin();
      for (;it!=itend;++kt,++it){
#if 1
	vecteur2vector_int(*it->_VECTptr,Modulo,*kt);
#else
	const_iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
	kt->resize(jtend-jt);
	vector<int>::iterator lt=kt->begin();
	for (;jt!=jtend;++lt,++jt){
	  if (jt->type==_INT_)
	    *lt=jt->val;
	  else
	    *lt=smod(*jt,Modulo).val;
	}
#endif
      }
    }
    else 
#endif
      {
	if (!vecteur2vectvector_int(a,Modulo,N))
	  return false;
      }
    longlong idet=1;
    vector<int> permutation,maxrankcol;
    if (debug_infolevel>2)
      CERR << CLOCK() << " begin smallmodrref " << endl;
    smallmodrref(1,N,pivots,permutation,maxrankcol,idet,l,lmax,c,cmax,fullreduction,dont_swap_below,Modulo,rref_or_det_or_lu,true,workptr,true,carac);
#ifndef GIAC_HAS_STO_38
    if (inverting){
      int_lu2inv(N,Modulo,permutation);
      // matrice dbg;
      // vectvector_int2vecteur(N,dbg);
      // CERR << a << "*" << smod(dbg,Modulo) << " % " << Modulo << endl;
    }
#endif
    if (debug_infolevel>2)
      CERR << CLOCK() << " rref done smallmodrref " << endl;
    det = smod(longlong(idet),Modulo);
    if (!is_one(mult_by_det_mod_p)){
      idet=smod(mult_by_det_mod_p,Modulo).val;
      for (unsigned i=0;i<N.size();++i){
	vector<int>::iterator it=N[i].begin(),itend=N[i].end();
	for (;it!=itend;++it){
	  *it = idet*(*it)%Modulo;
	}
      }
    }
    if (rref_or_det_or_lu!=1)
      vectvector_int2vecteur(N,res);
    if (debug_infolevel>2)
      CERR << CLOCK() << " end smallmodrref " << endl;
    if (rref_or_det_or_lu==2 && !inverting){
      vecteur P;
      vector_int2vecteur(permutation,P);
      pivots.push_back(P);
    }
    return true;
  }

  // if dont_swap_below !=0, for line numers < dont_swap_below
  // the pivot is searched in the line instead of the column
  // hence no line swap occur
  // rref_or_det_or_lu = 0 for rref, 1 for det, 2 for lu, 
  // 3 for lu without permutation
  // fullreduction=0 or 1, use 2 if the right part of a is idn
  bool modrref(const matrice & a, matrice & res, vecteur & pivots, gen & det,int l, int lmax, int c,int cmax,int fullreduction,int dont_swap_below,const gen & modulo,bool ckprime,int rref_or_det_or_lu){
    if (ckprime && !is_probab_prime_p(modulo)){
      if (rref_or_det_or_lu==1){ 
	vecteur v=ifactors(modulo,context0);
	if (v.size()<2)
	  return false;
	gen currentp=pow(v[0],v[1],context0);
	vector< vector<int> > N;
	if (currentp.type==_INT_){
	  res.clear(); N.clear();
	  if (!in_modrref(a,N,res,pivots,det,l,lmax,c,cmax,fullreduction,dont_swap_below,currentp.val,v[0].val /* carac */,rref_or_det_or_lu,1,false /* inverting */,false/* no initial mod */,0/*workptr*/))
	    return false;
	}
	else {
	  if ( v[1]!=1 || !modrref(a,res,pivots,det,l,lmax,c,cmax,fullreduction,dont_swap_below,v[0],false,rref_or_det_or_lu))
	    return false;
	}
	gen pip=currentp;
	for (int i=2;i<v.size();i+=2){
	  currentp=pow(v[i],v[i+1],context0);
	  gen curdet;
	  if (currentp.type==_INT_){
	    res.clear(); N.clear();
	    if (!in_modrref(a,N,res,pivots,curdet,l,lmax,c,cmax,fullreduction,dont_swap_below,currentp.val,v[i].val /* carac */,rref_or_det_or_lu,1,false /* inverting */,false/* no initial mod */,0/*workptr*/))
	      return false;
	  }
	  else {
	    if (v[i+1]!=1)
	      return false;
	    if (!modrref(a,res,pivots,curdet,l,lmax,c,cmax,fullreduction,dont_swap_below,v[i],false,rref_or_det_or_lu))
	      return false;
	  }
	  det=ichinrem(det,curdet,pip,v[i]);
	  pip=pip*v[i];
	}
	return true;
      }
      CERR << "Non prime modulo. Reduction mail fail" << endl;
    }
    if (modulo.type==_INT_ && 
#if 0 // ndef _I386_
	modulo.val<46340 &&
#endif
	is_fraction_matrice(a) ){ // Small mod reduction
      res.clear();
      vector< vector<int> > N;
      return in_modrref(a,N,res,pivots,det,l,lmax,c,cmax,fullreduction,dont_swap_below,modulo.val,-1/*carac*/,rref_or_det_or_lu,1,false /* inverting */,false/* no initial mod */,0/*workptr*/);
    }
    // bool use_cstart=!c;
    // bool inverting=fullreduction==2;
    det = 1;
    int linit=l;//,previous_l=l;
    vecteur lv;
    // Large mod reduction (coeff do not fit in an int)
    res=a;
    // COUT << res << endl;
    std_matrix<gen> M;
    matrice2std_matrix_gen(res,M);
    gen pivot,temp;
    // vecteur vtemp;
    int pivotline,pivotcol;
    pivots.clear();
    pivots.reserve(cmax-c);
    for (;(l<lmax) && (c<cmax);){
      if ( (!fullreduction) && (l==lmax-1) ){
	det = smod(det*M[l][c],modulo);
	break;
      }
      pivot=M[l][c];
      pivotline=l;
      pivotcol=c;
      if (is_exactly_zero(pivot)){ // scan current line
	if (rref_or_det_or_lu==3){
	  det=0;
	  return true;
	}
	if (l<dont_swap_below){ 
	  for (int ctemp=c+1;ctemp<cmax;++ctemp){
	    temp=M[l][ctemp];
	    if (!is_exactly_zero(temp)){
	      pivot=temp;
	      pivotcol=ctemp;
	      break;
	    }
	  }
	}
	else {      // scan M current column for the best pivot available
	  for (int ltemp=l+1;ltemp<lmax;++ltemp){
	    temp=M[ltemp][c];
	    if (debug_infolevel>2)
	      print_debug_info(temp);
	    if (!is_exactly_zero(temp)){
	      pivot=temp;
	      pivotline=ltemp;
	      break;
	    }
	  }
	}
      } // end if is_zero(pivot), true pivot found on line or column
      if (!is_exactly_zero(pivot)){
	if (l!=pivotline){
	  swap(M[l],M[pivotline]);
	  det = -det;
	}
	det = smod(det*pivot,modulo);
	// save pivot for annulation test purposes
	if (rref_or_det_or_lu!=1)
	  pivots.push_back(pivot);
	// invert pivot 
	temp=invmod(pivot,modulo);
	if (fullreduction || rref_or_det_or_lu<2){
	  iterateur it=M[pivotline].begin(),itend=M[pivotline].end();
	  for (;it!=itend;++it)
	    *it=smod(temp * *it,modulo);
	}
	// make the reduction
	if (fullreduction){
	  for (int ltemp=linit;ltemp<lmax;++ltemp){
	    if (ltemp!=l)
	      modlinear_combination(M[ltemp],-M[ltemp][pivotcol],M[l],modulo,0);
	  }
	}
	else {
	  for (int ltemp=l+1;ltemp<lmax;++ltemp){
	    if (rref_or_det_or_lu>=2)
	      M[ltemp][pivotcol]=smod(M[ltemp][pivotcol]*temp,modulo);
	    modlinear_combination(M[ltemp],-M[ltemp][pivotcol],M[l],modulo,(c+1)*(rref_or_det_or_lu>0));
	  }
	} // end else
	// increment column number if swap was allowed
	if (l>=dont_swap_below)
	  ++c;
	// increment line number since reduction has been done
	++l;	  
      } // end if (!is_zero(pivot)
      else { // if pivot is 0 increment either the line or the col
	det = 0;
	if (rref_or_det_or_lu==1)
	  return true;
	if (l>=dont_swap_below)
	  c++;
	else
	  l++;
      }
    } // end for reduction loop
    std_matrix_gen2matrice_destroy(M,res);
    return true;
  }

  bool mrref(const matrice & a, matrice & res, vecteur & pivots, gen & det,GIAC_CONTEXT){
    return mrref(a,res,pivots,det,0,int(a.size()),0,int(a.front()._VECTptr->size()),
		 /* fullreduction */ 1,0,true,1,0,
	  contextptr)!=0;
  }

  bool modrref(const matrice & a, matrice & res, vecteur & pivots, gen & det,const gen& modulo){
    return modrref(a,res,pivots,det,0,int(a.size()),0,int(a.front()._VECTptr->size()),
		   true /* full reduction */,0 /* dont_swap_below*/,modulo,true /*ckprime*/,0 /* rref */);
  }

  // add identity matrix, modifies arref in place
  void add_identity(matrice & arref){
    int s=int(arref.size());
    vecteur v;
    gen un(1),zero(0);
    if (!arref.empty() && has_num_coeff(arref)){
      gen tmp=arref.front()._VECTptr->front();
      if (is_zero(tmp))
	tmp= tmp+1;
      un=tmp/tmp;
      zero=tmp-tmp;
    }
    for (int i=0;i<s;++i){
      gen tmp=new ref_vecteur(2*s,zero);
      iterateur it=tmp._VECTptr->begin(),jt=arref[i]._VECTptr->begin(),jtend=jt+s;
      for (;jt!=jtend;++it,++jt)
	*it=*jt;
      it+=i;
      *it=un;
      arref[i] = tmp;
    }
  }

  // add identity matrix, modifies arref in place
  void add_identity(vector< vector<int> > & arref){
    int s=int(arref.size());
    for (int i=0;i<s;++i){
      vector<int> & v= arref[i];
      v.reserve(2*s);
      for (int j=0;j<s;++j)
	v.push_back(i==j);
    }
  }

  bool remove_identity(matrice & res,GIAC_CONTEXT){
    int s=int(res.size());
    // "shrink" res
    for (int i=0;i<s;++i){
      vecteur v = *res[i]._VECTptr;
      if (is_zero(v[i],context0))
	return false;
      gen tmp=new ref_vecteur(v.begin()+s,v.end());
      divvecteur(*tmp._VECTptr,v[i],*tmp._VECTptr);
      res[i] = normal(tmp,contextptr);
    }
    return true;
  }

  bool remove_identity(matrice & res){
    return remove_identity(res,context0);
  }

  bool remove_identity(vector< vector<int> > & res,int modulo){
    int s=int(res.size());
    // "shrink" res
    for (int i=0;i<s;++i){
      vector<int> & v = res[i];
      if (!v[i])
	return false;
      longlong inv=invmod(v[i],modulo);
      v = vector<int>(v.begin()+s,v.end());
      for (int j=0;j<s;++j){
	longlong tmp=v[j]*inv;
	v[j] = tmp % modulo;
      }
    }
    return true;
  }

  bool modinv(const matrice & a,matrice & res,const gen & modulo,gen & det_mod_p){
    if (modulo.type==_INT_ && a.size()*double(modulo.val)*modulo.val< 4e18){
      vector< vector<int> > ai,resi;
      longlong det_mod_pi;
      vecteur2vectvector_int(a,modulo.val,ai); 
      if (!smallmodinv(ai,resi,modulo.val,det_mod_pi))
	return false;
      det_mod_p=det_mod_pi;
      vectvector_int2vecteur(resi,res);
      return true;
    }
    matrice arref = a;
    add_identity(arref);
    int s=int(a.size());
    vecteur pivots;
    if (!modrref(arref,res,pivots,det_mod_p,0,s,0,2*s,
		 2/* full reduction*/,0/*dont_swap_below*/,modulo,true/*ckprime*/,0/* rref */))
      return false;
    return remove_identity(res);
  }

  bool smallmodinv(const vector< vector<int> > & a,vector< vector<int> > & res,int modulo,longlong & det_mod_p){
    res = a;
    add_identity(res);
    int s=int(a.size());
    vecteur pivots;
    vector<int> permutation,rankcols;
#ifndef GIAC_HAS_STO_38
    smallmodrref(1,res,pivots,permutation,rankcols,det_mod_p,0,s,0,s,
		 0,false,modulo,2,true,0,true,-1);
    if (det_mod_p==0)
      return false;
    int_lu2inv(res,modulo,permutation);
    return true;
#else
    smallmodrref(1,res,pivots,permutation,rankcols,det_mod_p,0,s,0,2*s,
		 2/* full reduction*/,0/*dont_swap_below*/,modulo,0/* rref */,true,0,false,-1);
    return remove_identity(res,modulo);
#endif
  }

  // works if |v|^2,|w|^2<2^31
  static gen dotvecteur_int(const vecteur & a,const vecteur & b,bool smallint,int p){
    vecteur::const_iterator ita=a.begin(), itaend=a.end();
    vecteur::const_iterator itb=b.begin(), itbend=b.end();
    if (smallint) {
      longlong res=0;
      for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
	res += longlong (ita->val)*(itb->val);
      }
      return p?(res %p):res;
    }
    ref_mpz_t * e = new ref_mpz_t;
    mpz_set_ui(e->z,0);
    gen tmp;
    for (;(ita!=itaend)&&(itb!=itbend);++ita,++itb){
#ifdef USE_GMP_REPLACEMENTS
      type_operator_times(*ita,*itb,tmp);
      if (tmp.type==_INT_){
	if (tmp.val<0)
	  mpz_sub_ui(e->z,e->z,-tmp.val);
	else
	  mpz_add_ui(e->z,e->z,tmp.val);
      }
      else 
	mpz_add(e->z,e->z,*tmp._ZINTptr);
#else
      if (ita->type==_ZINT){
	if (itb->type==_ZINT)
	  mpz_addmul(e->z,*ita->_ZINTptr,*itb->_ZINTptr);
	else {
	  if (itb->val>0)
	    mpz_addmul_ui(e->z,*ita->_ZINTptr,itb->val);
	  else
	    mpz_submul_ui(e->z,*ita->_ZINTptr,-itb->val);
	}
      }
      else {
	if (itb->type==_ZINT){
	  if (ita->val>0)
	    mpz_addmul_ui(e->z,*itb->_ZINTptr,ita->val);
	  else
	    mpz_submul_ui(e->z,*itb->_ZINTptr,-ita->val);
	}
	else {
#ifdef INT128
	  longlong tmp=longlong(ita->val)*(itb->val);
	  if (tmp>0)
	    mpz_add_ui(e->z,e->z,tmp);
	  else
	    mpz_sub_ui(e->z,e->z,-tmp);
#else
	  type_operator_times(*ita,*itb,tmp);
	  if (tmp.type==_INT_){
	    if (tmp.val<0)
	      mpz_sub_ui(e->z,e->z,-tmp.val);
	    else
	      mpz_add_ui(e->z,e->z,tmp.val);
	  }
	  else 
	    mpz_add(e->z,e->z,*tmp._ZINTptr);
#endif
	}
      }
#endif
    }
    if (p){
      tmp=modulo(e->z,p);
      delete e;
      return tmp;
    }
    else
      return e;
  }

#ifndef GIAC_HAS_STO_38
  longlong dotvecteur_int(const vector<int> & a,const vector<int> & b){
    vector<int>::const_iterator ita=a.begin(), itaend=a.end(),itb=b.begin();
    longlong res=0;
    for (;ita!=itaend;++itb,++ita)
      res += longlong(*ita)*(*itb);
    return res;
  }

  void dotvecteur_int_(vector< vector<int> >::const_iterator at,const vector<int> & b,vector<longlong> & res){
    vector<int>::const_iterator ita0=at->begin(), ita0end=at->end();
    vector<int>::const_iterator ita1=(at+1)->begin();
    vector<int>::const_iterator ita2=(at+2)->begin();
    vector<int>::const_iterator ita3=(at+3)->begin();
    vector<int>::const_iterator itb=b.begin();
    longlong res0=0,res1=0,res2=0,res3=0;
    for (;ita0!=ita0end;++itb,++ita3,++ita2,++ita1,++ita0){
      int tmp=*itb;
      res0 += ((longlong) *ita0)*tmp;
      res1 += ((longlong) *ita1)*tmp;
      res2 += ((longlong) *ita2)*tmp;
      res3 += ((longlong) *ita3)*tmp;
    }
    // if (res0!=dotvecteur_int(*at,b) || res1!=dotvecteur_int(*(at+1),b) || res2!=dotvecteur_int(*(at+2),b) || res3!=dotvecteur_int(*(at+3),b)) CERR << "erreur" << endl;
    res.push_back(res0);
    res.push_back(res1);
    res.push_back(res2);
    res.push_back(res3);
  }

  // a += b;
  vector<longlong> & addvecteur_longlong(vector<longlong> & a,const vector<longlong> & b){
    vector<longlong>::iterator ita=a.begin();
    vector<longlong>::const_iterator itaend=a.end(),itb=b.begin();
    for (;ita!=itaend;++itb,++ita)
      *ita += *itb;
    return a;
  }

  // a -= b;
  vector<longlong> & subvecteur_longlong(vector<longlong> & a,const vector<longlong> & b){
    vector<longlong>::iterator ita=a.begin();
    vector<longlong>::const_iterator itaend=a.end(),itb=b.begin();
    for (;ita!=itaend;++itb,++ita)
      *ita -= *itb;
    return a;
  }

  void multmatvecteur_int(const vector< vector<int> > & a,const vector<int> & b,vector<longlong> & res){
    vector< vector<int> >::const_iterator ita=a.begin(), itaend=a.end();
    res.clear();
    res.reserve(itaend-ita);
    for (;ita<=itaend-4;ita+=4)
      dotvecteur_int_(ita,b,res);
    for (;ita!=itaend;++ita)
      res.push_back(dotvecteur_int(*ita,b));
  }

  static void adjust(gen * & yptr,longlong res,int p){
    if (yptr->type==_INT_){
      longlong resp=(yptr->val-res)/p;
      if (resp==int(resp))
	yptr->val=int(resp);
      else
	*yptr=resp;
    }
    else {
#if defined(INT128) && !defined(USE_GMP_REPLACEMENTS)
      if (res>0)
	mpz_sub_ui(*yptr->_ZINTptr,*yptr->_ZINTptr,res);
      else
	mpz_add_ui(*yptr->_ZINTptr,*yptr->_ZINTptr,-res);
#else
      *yptr -= res;
#endif
#ifdef USE_GMP_REPLACEMENTS
      *yptr = *yptr/p;
#else
      if (yptr->type==_ZINT)
	mpz_divexact_ui(*yptr->_ZINTptr,*yptr->_ZINTptr,p);
      else
	yptr->val /= p;
#endif
    }
    ++yptr;
  }
  // if *yptr is !=0, instead of assigning *it we do *it=(*it-result)/p
  static void dotvecteur_int_(vector< vector<int> >::const_iterator at,const vecteur & b,iterateur it,int p,gen * & yptr){
    vector<int>::const_iterator ita0=at->begin(), ita0end=at->end();
    vector<int>::const_iterator ita1=(at+1)->begin();
    vector<int>::const_iterator ita2=(at+2)->begin();
    vector<int>::const_iterator ita3=(at+3)->begin();
    vecteur::const_iterator itb=b.begin();
    longlong res0=0,res1=0,res2=0,res3=0;
    for (;ita0!=ita0end;++itb,++ita3,++ita2,++ita1,++ita0){
      int tmp=itb->val;
      res0 += ((longlong) *ita0)*tmp;
      res1 += ((longlong) *ita1)*tmp;
      res2 += ((longlong) *ita2)*tmp;
      res3 += ((longlong) *ita3)*tmp;
    }
    if (yptr){
      adjust(yptr,res0,p);
      adjust(yptr,res1,p);
      adjust(yptr,res2,p);
      adjust(yptr,res3,p);
    }
    else {
      *it=p?res0 % p:res0;
      *(it+1)=p?res1 % p:res1;
      *(it+2)=p?res2 % p:res2;
      *(it+3)=p?res3 % p:res3;
    }
  }

  static gen dotvecteur_int_(const vector<int> & a,const vecteur & b,int p,gen * & yptr){
    vector<int>::const_iterator ita=a.begin(), itaend=a.end();
    vecteur::const_iterator itb=b.begin();
    longlong res=0;
    for (;ita!=itaend;++ita,++itb){
      res += ((longlong) *ita)*(itb->val);
    }
    if (yptr){
      adjust(yptr,res,p);
      return p;
    }
    else
      return p?res % p:res;
  }
#endif

  static void multmatvecteur_int(const matrice & a,const vector< vector<int> > & A,const vecteur & b,bool smallint,vecteur & res,int p,gen *yptr){
#ifndef GIAC_HAS_STO_38
    if (smallint){
#if 0
      int nbits=sizeinbase2(p); 
      unsigned pseudoinv=((1ULL<<(2*nbits)))/p+1;
#endif
      vector< vector<int> >::const_iterator ita=A.begin(), itaend=A.end(),ita4=itaend-4;
      res.resize(itaend-ita);
      iterateur itres=res.begin();
      for (;ita<ita4;itres+=4,ita+=4){
	dotvecteur_int_(ita,b,itres,p,yptr);
      }
      for (;ita!=itaend;++itres,++ita){
	// if (dotvecteur_int(*ita,b)!=dotvecteur_int(*a[ita-A.begin()]._VECTptr,b,true))
	// CERR << "erreur" << endl;
	*itres=dotvecteur_int_(*ita,b,p,yptr);
      }
    }
    else
#endif 
      {
	vecteur::const_iterator ita=a.begin(), itaend=a.end();
	res.resize(itaend-ita);
	iterateur itres=res.begin();
	for (;ita!=itaend;++itres,++ita)
	  *itres=dotvecteur_int(*(ita->_VECTptr),b,smallint,p);
      }
  }

  // res += pn*x
  void add_multvecteur(vecteur & res,const gen & pn,const vecteur & x){
    iterateur it=res.begin(),itend=res.end();
    const_iterateur jt=x.begin();
    for (;it!=itend;++jt,++it){
#ifdef USE_GMP_REPLACEMENTS
      *it += pn*(*jt);
#else
      if (it->type==_ZINT && it->ref_count()==1 && pn.type==_ZINT){
	if (jt->type==_INT_){
	  if (jt->val>0)
	    mpz_addmul_ui(*it->_ZINTptr,*pn._ZINTptr,jt->val);
	  else
	    mpz_submul_ui(*it->_ZINTptr,*pn._ZINTptr,-jt->val);
	}
	else
	  mpz_addmul(*it->_ZINTptr,*pn._ZINTptr,*jt->_ZINTptr);	  
      }
      else
	*it += pn*(*jt);
#endif
    }
  }

  // solve a*x=b where a and b have integer coeffs 
  // using a p-adic algorithm, n is the precision required
  // c is the inverse of a mod p
  // reconstruct is the number of components of x we want to compute, or 0 if compute all
  // NB: on Z[i], should use a prime such that -1 has a square root.
  vecteur padic_linsolve_c(const matrice & a,const vecteur & b,const matrice & c,unsigned n,const gen & p,unsigned reconstruct){
    unsigned bsize=unsigned(b.size()),asize=unsigned(a.size()); // should be the same
    if (reconstruct && reconstruct<bsize) bsize=reconstruct;
    vecteur res(bsize),y(b),x,tmp; // initialize y_0=b
    int resbits=n*nbits(p);
    uncoerce(res,resbits);
    int smallint = 0;
    if (p.type==_INT_ && 
	((ulonglong) p.val*p.val < ((ulonglong) 1 << 63)/asize ))
      smallint=1;
    vector< vector<int> > A,C;
    gen pn=1;
    if (smallint){
#ifndef GIAC_HAS_STO_38
      vecteur2vectvector_int(c,0,C);
#endif
      gen ainf=linfnorm(a,context0); 
      gen binf=linfnorm(b,context0);
      if (ainf.type==_INT_ && unsigned(ainf.val)< (((ulonglong) 1 << 63)/asize)/p.val){
	smallint=2; // ainf*x_n can be computed using longlong
#ifndef GIAC_HAS_STO_38
	vecteur2vectvector_int(a,0,A);
#endif
	if (binf.type==_INT_)
	  smallint=3;
      }
#if !defined(USE_GMP_REPLACEMENTS) 
      if (ainf.type==_ZINT
	  // && binf.type==_INT_ && binf.val<p.val // FIXME: temporary workaround
	  ){
	int k,kprime;
	for (k=1;is_greater(ainf,pn,context0);++k)
	  pn=pn*p;
	pn=p;
	for (kprime=1;is_greater(binf,pn,context0);++kprime)
	  pn=pn*p;
	if ((k+2)*double(p.val)*asize*p.val<(1ULL<<63)){
	  // A=sum_{i=0}^{k-1} A_i p^i, ||A_i||<p, find x=sum_{i=0}^n x_i p^i such that
	  // Ax=B mod p^{n+1}, ||x_i||<p
	  // Ax mod p^{n+1} = sum_{i=0}^n p^i* sum_{j=0}^{k-1} A_j*x_{i-j}
	  // hence the p^i term is 
	  // B_i = A_0 x_i + sum_{j=1}^{k-1} A_j*x_{i-j} mod p + carries of at most 2 terms
	  // hence x_i=C* know terms (keep carries and also compute A_0*x_i carries)
	  // where C=A0^-1 mod p
	  // CERR << "Fixme: implement faster p-adic" << endl;
	  vector< vector< vector<int> > > A(k,vector< vector<int> >(asize,vector<int>(asize)));
	  vector< vector<int> > B(kprime,vector<int>(asize)),X(n,vector<int>(asize));
	  // write a and b in basis p
	  mpz_t tmpz,tmpr;
	  mpz_init(tmpz); mpz_init(tmpr);
	  for (unsigned i=0;i<asize;++i){
	    vecteur & ai=*a[i]._VECTptr;
	    for (unsigned j=0;j<asize;++j){
	      gen g=ai[j];
	      if (g.type==_ZINT)
		mpz_set(tmpz,*g._ZINTptr);
	      else
		mpz_set_si(tmpz,g.val);
	      for (int l=0;l<k;++l){
		mpz_tdiv_qr_ui(tmpz,tmpr,tmpz,p.val);
		A[l][i][j]=mpz_get_si(tmpr);
	      }
	    }
	  }
	  for (unsigned i=0;i<bsize;++i){
	    gen g=b[i];
	    if (g.type==_ZINT)
	      mpz_set(tmpz,*g._ZINTptr);
	    else
	      mpz_set_si(tmpz,g.val);
	    for (int l=0;l<kprime;++l){
	      mpz_tdiv_qr_ui(tmpz,tmpr,tmpz,p.val);
	      B[l][i]=mpz_get_si(tmpr);
	    }
	  }
	  vector<int> carry1(asize),carry2(asize),value(asize);
	  vector<longlong> restmp(asize),tmp(asize);
	  for (unsigned i=0;i<n;++i){
	    // compute sum_{j=1}^{k-1} A_j*x_{i-j}, take care of carries
	    for (unsigned j=0;j<asize;++j){
	      restmp[j]=carry1[j];
	      carry1[j]=carry2[j];
	    }
	    if (int(i)<kprime){
	      for (unsigned j=0;j<asize;++j){
		restmp[j] += B[i][j]; 
		// ?adjust carry1
		// carry1[j] += restmp[j] / p.val;
		// restmp[j] %= p.val;
	      }
	      // CERR << restmp << endl;
	    }
	    for (int j=1;j<k && j<=int(i);++j){
	      multmatvecteur_int(A[j],X[i-j],tmp);
	      subvecteur_longlong(restmp,tmp);
	    }
	    for (unsigned j=0;j<asize;++j){
	      value[j]=restmp[j] % p.val;
	    }
	    // value of x_i
	    multmatvecteur_int(C,value,tmp);
	    for (unsigned j=0;j<asize;++j){
	      int x = tmp[j] % p.val;
	      if (x<0)
		x += p.val;
	      X[i][j] = x;
	    }
	    // adjust tmp by substracting A[0]*X[i]
	    multmatvecteur_int(A[0],X[i],tmp);
	    subvecteur_longlong(restmp,tmp);
	    // compute carries
	    for (unsigned j=0;j<asize;++j){
	      longlong l=restmp[j];
	      // if (l % p.val) CERR << "error " << endl;
	      l /= p.val;
	      longlong c=carry1[j] + (l % p.val);
	      carry1[j] = c % p.val;
	      carry2[j] = l/ p.val+c/p.val;
	      // if (absint(carry2[j])>=p.val) CERR << "error " << endl;
	    }
	  } // end loop on i
	  pn=pow(p,int(n));
	  // construct res from X[n-1]...X[0]
	  for (unsigned l=0;l<bsize;++l){
	    mpz_set_si(tmpz,0);
	    for (int i=n-1;i>=0;--i){
	      mpz_mul_ui(tmpz,tmpz,p.val);
	      int x=X[i][l];
	      if (x>0)
		mpz_add_ui(tmpz,tmpz,x);
	      else
		mpz_sub_ui(tmpz,tmpz,-x);
	    }
	    mpz_set(*res[l]._ZINTptr,tmpz);
	    res[l]=smod(res[l],pn);
	  }
	  mpz_clear(tmpz); mpz_clear(tmpr);
	  // multmatvecteur(a,res,y);
	  // CERR << smod(y,pn) << endl;
	  return res;
	} // end ainf value eligible
      } // end ainf.type==_ZINT
#endif
    }
    for (unsigned i=0;i<n;++i){
      smod(y,p,tmp);
      if (debug_infolevel>2)
	CERR << CLOCK() << " padic mult A^-1 mod p*y step " << i << endl;
      multmatvecteur_int(c,C,tmp,smallint!=0,x,smallint?p.val:0,NULL);
      if (!smallint) smod(x,p,x); // x_{n}=c*y_n mod p
      if (debug_infolevel>2)
	CERR << CLOCK() << " padic mult A *x step " << i << endl;
      if (smallint==3)
	multmatvecteur_int(a,A,x,true,tmp,p.val,&y.front());
      else {
	// y_{n+1}=(y_n-Ax_n)/p, |x_n|<=p
	// A*x_n computation requires n^2 multiplications in log(Ainf)*log(p) time
	// optimization if p is large: compute y_n=p*q+r, then y_{n+1}=q+(r-Ax_n)/p
	// where (r-Ax_n)/p can be computed using modular arithmetic
	// and the majoration ||(r-A*x_n)/p|| <= Ainf*n
	// n^2*log(Ainf*n) operations, reconstruction is O(n*...)
	// According to Chen and Storjohann tests the best choice is
	// p=product of l primes
	// l=(2 or 1)*log2(Ainf*n)
	// total time: l*n^3 (initial inversions) + n^2*log(Ainf*n)*log(hadamard)/l
	// vs 1 prime: n^3+n^2*log(Ainf)*log(hadamard)
	// conclusion: optimization is only interesting if the constant before inversion n^3 is small compared to constant before n^2 matrix*vector multiplication
	multmatvecteur_int(a,A,x,smallint>=2,tmp,0,NULL);
	if (debug_infolevel>2)
	  CERR << CLOCK() << " padic adjust y step " << i << endl;
	subvecteur(y,tmp,y);
#ifdef USE_GMP_REPLACEMENTS
	divvecteur(y,p,y); 
#else
	iterateur it=y.begin(),itend=y.end();
	if (p.type==_INT_){
	  for (;it!=itend;++it){
	    if (it->type==_ZINT) // assumes that p>0
	      mpz_divexact_ui(*it->_ZINTptr,*it->_ZINTptr,p.val);
	    else
	      it->val /= p.val;
	  }
	}
	else {
	  for (;it!=itend;++it){
	    if (it->type==_ZINT)
	      mpz_divexact(*it->_ZINTptr,*it->_ZINTptr,*p._ZINTptr);
	    // otherwise *it is of type _INT_ and divisible by p of type _ZINT hence it's 0
	  }
	}
#endif
      }
      if (debug_infolevel>2)
	CERR << CLOCK() << " padic adjust res step " << i << endl;
      // should use below on Z[i]
      // x=smod(multmatvecteur(c,y),p); // x_{n+1}=c*y_n mod p
      // y=divvecteur(subvecteur(y,multmatvecteur(a,x)),p); // y_{n+1}=(y_n-Ax_n)/p
#if 0
      multvecteur(pn,x,x);
      addvecteur(res,x,res);
#else
      add_multvecteur(res,pn,x);
#endif
      pn=pn*p;
    }
    return res;
  }  
  
  bool iszero(const vector<int> & p){
    vector<int>::const_iterator it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      if (*it)
	return false;
    }
    return true;    
  }
  
  static void extract(const vector< vector<int> > & source,const vector<int> & lines,int rang,vector< vector<int> > & target,vector< vector<int> > & excluded){
    int s=int(lines.size()),i;
    target.clear();
    target.reserve(rang);
    for (i=0;i<rang;++i){
      const vector<int> & si=source[lines[i]];
      if (iszero(si))
	break;
      target.push_back(si);
    }
    for (;i<s;++i)
      excluded.push_back(source[lines[i]]);
  }

  static void extract1(const matrice & source,const vector<int> & lines,int rang,matrice & target,vector<vecteur> & excluded){
    int s=int(lines.size()),i;
    target.clear();
    target.reserve(rang);
    for (i=0;i<rang;++i){
      const vecteur & si=*source[lines[i]]._VECTptr;
      if (is_zero(si,context0))
	break;
      target.push_back(si);
    }
    for (;i<s;++i)
      excluded.push_back(*source[lines[i]]._VECTptr);
  }

  static void extract2(const matrice & source,const vector<int> & lines,int rang,matrice & target,vector<vecteur> & k_excluded,vector<int> & k_excluded_col){
    int s=int(source.size()),i,j=0;
    target.clear();
    target.reserve(rang);
    for (i=0;i<rang;++i,++j){
      int li=lines[i];
      for (;j<li;++j){
	k_excluded.push_back(*source[j]._VECTptr);
	k_excluded_col.push_back(j);
      }
      const vecteur & si=*source[li]._VECTptr;
      target.push_back(si);
    }
    for (;j<s;++j){
      k_excluded.push_back(*source[j]._VECTptr);
      k_excluded_col.push_back(j);
    }
  }

  static gen init_modulo(int n,double logbound){
#if 1 // def _I386_
    double pinit= double(longlong(1) << 60);
    pinit /=n ;
    pinit = std::sqrt(pinit);
    pinit -= 3*logbound; // keep enough primes satisfying p^2*n<2^63
    return nextprime(int(pinit)); 
#else
    return 36007;
#endif
  }

  // a is a matrix with integer coeffs
  // find p such that a mod p has the same rank
  // rankline and rankcols are the lines/cols used for the submatrix
  // asub of max rank, ainv is the inverse of asub mod p
  // return -1 or the rank
  int padic_linsolve_prepare(const matrice & a,gen & p,vector<int> & ranklines, vector<int> & rankcols,matrice & asub,matrice & ainv,vecteur & compat,vecteur & kernel){
    if (!is_integer_matrice(a))
      return -1;
    vector< vector<int> > N;
    if (!vecteur2vectvector_int(a,p.val,N))
      return -1;
    int nrows=int(N.size());
    int ncols=int(N.front().size());
    int n0=giacmax(ncols,nrows);
    gen h2=4*square_hadamard_bound(a);
    gen B=evalf_double(linfnorm(a,context0),0,context0);
    double Bd=B._DOUBLE_val;
    double logbound=n0*(std::log10(double(n0))/2+std::log10(Bd));
    if (is_exactly_zero(p) || p.type!=_INT_)
      p=init_modulo(n0,logbound);
    for (;;){ // break loop as soon as a good p is found
      matrice atmp;
      vector< vector<int> > Ntmp,Ninv,Nsub,Nmaxrank,Nexcluded;
      vector<vecteur> excluded,k_excluded;
      Nsub=N;
      int nstep=int(giacmin(nrows,ncols)*std::log(evalf_double(h2,1,context0)._DOUBLE_val)/2/std::log(double(p.val)))+1;
      vecteur pivots;
      longlong idet;
      smallmodrref(1,Nsub,pivots,ranklines,rankcols,idet,0,nrows,0,ncols,0 /* fullreduction*/,0 /* dont_swap_below */,p.val,0 /* rref_or_det_or_lu */,true,0,true,-1);
      int rang=int(rankcols.size());
      /* extract maxrank submatrix */
      extract(N,ranklines,rang,Ntmp,Nexcluded);
      tran_vect_vector_int(Ntmp,Nsub);
      extract1(a,ranklines,rang,atmp,excluded);
      mtran(atmp,asub);
      // next call does not change excluded 
      extract(Nsub,rankcols,rang,Ntmp,Nexcluded);
      tran_vect_vector_int(Ntmp,Nsub);  
      vector<int> k_excluded_col;
      extract2(asub,rankcols,rang,atmp,k_excluded,k_excluded_col);
      mtran(atmp,asub);  
      // now asub contains the invertible matrix
      // we must truncate excluded, then rewrite each line of excluded
      // as a linear combination of lines of Np
      int es=int(excluded.size());
      for (int i=0;i<es;++i){
	vecteur & v=excluded[i];
	vecteur tmp=v;
	v.clear();
	for (int j=0;j<rang;++j)
	  v.push_back(tmp[rankcols[j]]);
      }
      /* invert Nsub in Ninv */
      longlong det;
      if (!smallmodinv(Nsub,Ninv,p.val,det))
	break; // should not happen!
      /* find compatibility with the lines of a that are not in Nmaxrank */
      vectvector_int2vecteur(Ninv,ainv);
      tran_vect_vector_int(Ninv,Ntmp);  
      matrice c;
      vectvector_int2vecteur(Ntmp,c);
      int i;
      gen pn=pow(p,nstep,context0);
      for (i=0;i<es;++i){
	/* p-adic lift of each compatibility equation */
	vecteur current,cond(nrows);
	cond[ranklines[rang+i]]=-1;
	current=padic_linsolve_c(atmp,excluded[i],c,nstep,p);
	int cs=int(current.size());
	for (int j=0;j<cs;++j)
	  current[j]=fracmod(current[j],pn);
	/* rewrite using ranklines */
	for (int j=0;j<rang;++j)
	  cond[ranklines[j]]=current[j];
	/* check that it is correct with the original matrix */
	if (!is_zero(multvecteurmat(cond,a),context0))
	  break;
	compat.push_back(cond);
      }
      if (i==es){
	/* compute kernel using k_excluded */
	es=int(k_excluded.size());
	for (i=0;i<es;++i){
	  /* p-adic lift of each kernel element basis */
	  vecteur current,cond(ncols);
	  cond[k_excluded_col[i]]=-1;
	  current=padic_linsolve_c(asub,k_excluded[i],ainv,nstep,p);
	  int cs=int(current.size());
	  for (int j=0;j<cs;++j)
	    current[j]=fracmod(current[j],pn);
	  /* rewrite using ranklines */
	  for (int j=0;j<rang;++j)
	    cond[rankcols[j]]=current[j];
	  kernel.push_back(cond);
	}
	return rang;
      }
      /* bad modulo */
      p=nextprime(p+1);
    }
    return -1;
  }

  // solve
  bool padic_linsolve_solve(const matrice & a,const gen & p,const vector<int> & ranklines,const vector<int> & rankcols,const matrice & asub,const matrice & ainv,const vecteur & compat,const vecteur & b,vecteur & sol){
    // first check that b verifies compat
    int es=int(compat.size());
    for (int i=0;i<es;++i){
      if (!is_exactly_zero(dotvecteur(compat[i],b))){
	return false;
      }
    }
    /* padic solve asub*x=part of b (ranklines) */
    int rang=int(asub.size());
    vecteur newb(rang);
    for (int i=0;i<rang;++i)
      newb[i]=b[ranklines[i]];
    gen h2=4*square_hadamard_bound(asub)*l2norm2(newb);
    int nstep=int(rang*std::log(evalf_double(h2,1,context0)._DOUBLE_val)/2/std::log(double(p.val)))+1;
    vecteur res=padic_linsolve_c(asub,newb,ainv,nstep,p);
    gen pn=pow(p,nstep,context0);
    int ress=int(res.size());
    for (int i=0;i<ress;++i)
      res[i]=fracmod(res[i],pn);
    /* find x (using rankcols) */
    int xs=int(a.front()._VECTptr->size());
    sol=vecteur(xs);
    for (int i=0;i<rang;++i){
      sol[rankcols[i]]=res[i];
    }
    return true;
  }

  gen _padic_linsolve(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->empty())
      return gensizeerr(contextptr);
    if (g.subtype==_SEQ__VECT && g._VECTptr->size()==2){
      gen a=g._VECTptr->front();
      gen b=g._VECTptr->back();
      if (!ckmatrix(a) || b.type!=_VECT)
	return gensizeerr(contextptr);
      if (a._VECTptr->front()._VECTptr->size()!=b._VECTptr->size())
	return gendimerr(contextptr);
      matrice & A=*a._VECTptr;
      gen p;
      matrice asub,ainv;
      vecteur compat,kernel;
      vector<int> ranklines,rankcols;
      if (!padic_linsolve_prepare(A,p,ranklines,rankcols,asub,ainv,compat,kernel))
	return gensizeerr(gettext("Unable to find a modulus to solve"));
      vecteur & B=*b._VECTptr;
      vecteur res;
      if (padic_linsolve_solve(A,p,ranklines,rankcols,asub,ainv,compat,B,res))
	return makevecteur(res,kernel);
      else
	return gensizeerr(gettext("Incompatible system"));
    }
    return gensizeerr(contextptr);
  }
  static const char _padic_linsolve_s []="padic_linsolve";
  static define_unary_function_eval (__padic_linsolve,&giac::_padic_linsolve,_padic_linsolve_s);
  define_unary_function_ptr5( at_padic_linsolve ,alias_at_padic_linsolve,&__padic_linsolve,0,true);

  // solve A*x=b where a and b have integer coeffs using a p-adic algorithm 
  // (ignoring extra columns of A)
  // lcmdeno of the answer may be used to give an estimate of the 
  // least divisor element of A if b is random
  // returns 0 if no invertible found, -1 if det==0, 1 otherwise
  int padic_linsolve(const matrice & A,const vecteur & b,vecteur & res,gen & p,gen & det_mod_p,gen & h2,unsigned reconstruct,int maxtry){
    // first find p such that a mod p is invertible
    // find a bound on the num/den of x
    // let c=(a mod p)^(-1)
    matrice a(A);
    if (A.size()!=A.front()._VECTptr->size()){
      for (unsigned i=0;i<a.size();++i){
	a[i]=new ref_vecteur(a[i]._VECTptr->begin(),a[i]._VECTptr->begin()+a.size());
      }
    }
    matrice c;
    matrice ab(a);
    ab.push_back(b);
    if (is_exactly_zero(p))
      p=36007;
    if (is_zero(h2))
      h2=4*square_hadamard_bound(ab);
    gen pip(1);
    if (debug_infolevel>2)
      CERR << "Modinv begin " << CLOCK() << endl;
    for (int tryinv=0;;++tryinv){
      if (modinv(a,c,p,det_mod_p))
	break;
      pip=pip*p;
      if (tryinv>maxtry)
	return 0;
      if (is_strictly_greater(pip*pip,h2,context0)) // ok
	return -1;
      p=nextprime(p+1);
    }
    if (debug_infolevel>2)
      CERR << "Modinv end " << CLOCK() << endl;
    unsigned n=1;
    gen pn=p;
    while (is_strictly_greater(h2,pn,context0)){ // ok
      ++n;
      pn = pn * p;
    }
    gen sqrtpn=isqrt(pn); // (pow(gen(p),int(n/2),context0)-1)/2;
    vecteur resp=padic_linsolve_c(a,b,c,n,p,reconstruct);
    if (debug_infolevel>2)
      CERR << "Padic end " << CLOCK() << endl;
    // rational reconstruction
    unsigned s=unsigned(resp.size());
    if (reconstruct)
      s=std::min(s,reconstruct);
    res.clear();
    res.reserve(s);
    gen lcmdeno(1);
    for (unsigned j=0;j<s;++j){
      if (j){
	gen tmp=smod(lcmdeno*resp[j],pn);
	if (is_strictly_positive(sqrtpn+tmp,context0) && is_strictly_positive(sqrtpn-tmp,context0)){
	  if (0){ // debug
	    gen tmp1=tmp/lcmdeno,tmp2=fracmod(resp[j],pn);
	    if (tmp1!=tmp2)
	      CERR << "err" << endl;
	  }
	  res.push_back(tmp/lcmdeno);
	  continue;
	}
      }
      res.push_back(fracmod(resp[j],pn));
      if (res.back().type==_FRAC)
	lcmdeno=lcm(lcmdeno,res.back()._FRACptr->den);
    }
    if (debug_infolevel>2)
      CERR << "Padic end rational reconstruction " << CLOCK() << endl;
    if (0 && A.size()==res.size()){ // debug
      vecteur tmp(multvecteur(lcmdeno,res));
      tmp=multmatvecteur(A,tmp);
      CERR << tmp << multvecteur(lcmdeno,b) << endl;
    }
    return 1;    
  }

  matrice mrref(const matrice & a,GIAC_CONTEXT){
    if (a.empty())
      return vecteur(vecteur(1,gendimerr(contextptr)));
    gen det;
    vecteur pivots;
    matrice res;
    if (!mrref(a,res,pivots,det,0,int(a.size()),0,int(a.front()._VECTptr->size()),
	  /* fullreduction */1,0,true,1,0,
	       contextptr))
      return vecteur(1,vecteur(1,gendimerr(contextptr)));
    return res;
  }

  bool read_reduction_options(const gen & a_orig,matrice & a,bool & convert_internal,int & algorithm,bool & minor_det,bool & keep_pivot,int & last_col){
    convert_internal=true;
    algorithm=RREF_GUESS;
    minor_det=false;
    keep_pivot=false;
    last_col=-1;
    if (ckmatrix(a_orig)){
      a=*a_orig._VECTptr;
    }
    else { // rref with options
      if (a_orig.type!=_VECT)
	return false;
      vecteur v=*a_orig._VECTptr;
      int s=int(v.size());
      if (s<=3 && v[0].is_symb_of_sommet(at_pnt)){
	for (int i=0;i<s;++i){
	  v[i]=remove_at_pnt(v[i]);
	  if (v[i].subtype==_VECTOR__VECT && v[i]._VECTptr->size()==2)
	    v[i]=v[i]._VECTptr->back()-v[i]._VECTptr->front();
	  if (v[i].type!=_VECT){
	    gen a,b;
	    reim(v[i],a,b,context0);
	    v[i]=makevecteur(a,b);
	  }
	}
	if (ckmatrix(v))
	  return read_reduction_options(v,a,convert_internal,algorithm,minor_det,keep_pivot,last_col);
      }
      if (!s || !ckmatrix(v[0]))
	return false;
      a=*v[0]._VECTptr;
      for (int i=1;i<s;++i){
	if (v[i]==at_lagrange)
	  algorithm=RREF_LAGRANGE;
	if (v[i]==at_irem)
	  algorithm=RREF_MODULAR;
	if (v[i]==at_linsolve)
	  algorithm=RREF_PADIC;
	if (v[i].type==_INT_){
	  if (v[i].subtype==_INT_SOLVER){
	    switch (v[i].val){
	    case _RATIONAL_DET:
	      convert_internal=false;
	      algorithm=RREF_GAUSS_JORDAN;
	      break;
	    case _BAREISS:
	      algorithm=RREF_BAREISS;
	      break;
	    case _KEEP_PIVOT:
	      keep_pivot=true;
	      break;
	    case _MINOR_DET:
	      minor_det=true;
	    }
	  }
	  else
	    last_col=v[i].val;
	}
      }
    }
    return true;
  }
  gen _rref(const gen & a_orig,GIAC_CONTEXT) {
    if ( a_orig.type==_STRNG && a_orig.subtype==-1) return  a_orig;
    matrice a;
    bool convert_internal,minor_det,keep_pivot;
    int algorithm,last_col;
    if (!read_reduction_options(a_orig,a,convert_internal,algorithm,minor_det,keep_pivot,last_col))
      return gensizeerr(contextptr);
    if (minor_det)
      return gensizeerr(gettext("minor_det option applies only to det"));
    gen det;
    vecteur pivots;
    matrice res;
    int ncols=int(a.front()._VECTptr->size());
    if (last_col>=0)
      ncols=giacmin(ncols,last_col);
    if (!mrref(a,res,pivots,det,0,int(a.size()),0,ncols,
	  /* fullreduction */1,0,convert_internal,algorithm,0,
	       contextptr))
      return gendimerr(contextptr);
    if (!keep_pivot){
      mdividebypivot(res,ncols);
    }
    if (res.front().type==_VECT && res.front()._VECTptr->front().type==_MOD)
      return res;
    return ratnormal(res,contextptr);
  }
  static const char _rref_s []="rref";
  static define_unary_function_eval (__rref,&giac::_rref,_rref_s);
  define_unary_function_ptr5( at_rref ,alias_at_rref,&__rref,0,true);

  // returns 0 if all elements are 0
  static gen first_non_zero(const vecteur & v,int lastcol){
    vecteur::const_iterator it=v.begin(),itend=v.end();
    if (itend-it>lastcol)
      itend=it+lastcol;
    for (;it!=itend;++it){
      if (!is_zero(*it,context0))
	return *it;
    }
    return 0;
  }

  void mdividebypivot(matrice & a,int lastcol){
    if (lastcol==-1)
      lastcol=int(a.front()._VECTptr->size());
    if (lastcol==-2)
      lastcol=int(a.front()._VECTptr->size())-1;
    if (lastcol<0)
      lastcol=0;
    vecteur::const_iterator ita=a.begin(),itaend=a.end();
    gen pivot;
    for (;ita!=itaend;++ita){
      pivot=first_non_zero(*(ita->_VECTptr),lastcol);
      if (!is_zero(pivot,context0))
	divvecteur(*(ita->_VECTptr),pivot,*(ita->_VECTptr));
    }
  }

  void midn(int n,matrice & res){
    if (n<=0 || longlong(n)*n>LIST_SIZE_LIMIT){
      res= vecteur(1,vecteur(1,gendimerr(gettext("idn"))));
      return ;
    }
    res.clear();
    res.reserve(n);
    vecteur v;
    for (int i=0;i<n;++i){
      res.push_back(new ref_vecteur(n));
      (*res[i]._VECTptr)[i]=1;
    }
  }

  matrice midn(int n){
    matrice res;
    midn(n,res);
    return res;
  }

  gen _idn(const gen & e,GIAC_CONTEXT) {
    if ( e.type==_STRNG && e.subtype==-1) return  e;
    matrice res;
    if (e.type==_INT_)
      midn(e.val,res);
    else {
      if (e.type==_DOUBLE_)
	midn(int(e._DOUBLE_val),res);
      else {
	if ((e.type==_VECT) && is_squarematrix(*e._VECTptr))
	  midn(int(e._VECTptr->size()),res);
	else
	  return gensizeerr(contextptr);
      }
    }
    return gen(res,_MATRIX__VECT);
  }
  static const char _idn_s []="idn";
  static define_unary_function_eval (__idn,&giac::_idn,_idn_s);
  define_unary_function_ptr5( at_idn ,alias_at_idn,&__idn,0,true);

  // find index i of x in v that is i such that v[i] <= x < v[i+1]
  // where v[-1]=-inf, and v[v.size()]=+inf
  int dichotomy(const vector<giac_double> & v,double x){
    int s=int(v.size());
    if (x<v[0])
      return -1;
    if (x>=v[s-1])
      return s-1;
    int a=0, b=s-1; // v[a] <= x < v[b]
    while (b-a>1){
      int c=(a+b)/2;
      if (x>=v[c])
	a=c;
      else
	b=c;
    }
    return a;
  }

  void vranm(int n,const gen & F,vecteur & res,GIAC_CONTEXT){
    gen f(F);
    if (F.type==_USER)
      f=symbolic(at_rand,F);
    n=giacmax(1,n);
    if (n>LIST_SIZE_LIMIT)
      setstabilityerr();
    res.reserve(n);
    if (is_zero(f,contextptr)){
      for (int i=0;i<n;++i){
	res.push_back((int) (2*randrange*giac_rand(contextptr)/(rand_max2+1.0)-randrange));
      }
      return;
    }
    if (is_integer(f)){
      if (f.type==_INT_){
	int t=f.val;
	if (t<0){
	  for (int i=0;i<n;++i)
	    res.push_back((int) (2*t*(giac_rand(contextptr)/(rand_max2+1.0))-t)	);
	  return;
	}
#if 0
	int add=(xcas_mode(contextptr)==3 || abs_calc_mode(contextptr)==38);
	for (int i=0;i<n;++i)
	  res.push_back(add+(int) t*(giac_rand(contextptr)/(rand_max2+1.0)));
	return;
#endif
      }
      for (int i=0;i<n;++i)
	res.push_back(_rand(f,contextptr));
      return;
    }
    if (f.type==_MOD){
      gen fm=*(f._MODptr+1);
      vranm(n,fm,res,contextptr);
      for (int i=0;i<n;++i)
	res[i]=makemod(res[i],fm);
      return;
    }
    if (f.type==_VECT){
      const vecteur & v = *f._VECTptr;
      int s=int(v.size());
      for (int i=0;i<n;++i){
	double d=giac_rand(contextptr)*double(s)/(rand_max2+1.0);
	res.push_back(v[int(d)]);
      }
      return;
    }
    if (f.is_symb_of_sommet(at_interval) && f._SYMBptr->feuille.type==_VECT){
      gen x=evalf(f._SYMBptr->feuille._VECTptr->front(),1,contextptr),y=evalf(f._SYMBptr->feuille._VECTptr->back(),1,contextptr);
      if (x.type==_DOUBLE_ && y.type==_DOUBLE_){
	double xd=x._DOUBLE_val,yd=y._DOUBLE_val;
	double scale=(yd-xd)/(rand_max2+1.0);
	for (int i=0;i<n;++i){
	  double xr= giac_rand(contextptr)*scale+xd;
	  res.push_back(xr);
	}
	return;
      }
      for (int i=0;i<n;++i)
	res.push_back(rand_interval(*f._SYMBptr->feuille._VECTptr,false,contextptr));
      return;
    }
    if (f==at_uniform || f==at_uniformd){
      for (int i=0;i<n;++i)
	res.push_back(giac_rand(contextptr)/(rand_max2+1.0));
      return;
    }
    if (f.is_symb_of_sommet(at_uniform) ||f.is_symb_of_sommet(at_uniformd) ){
      f=evalf_double(f._SYMBptr->feuille,1,contextptr);
      if (f.type!=_VECT || f._VECTptr->size()!=2 || f._VECTptr->front().type!=_DOUBLE_ || f._VECTptr->back().type!=_DOUBLE_){
	res=vecteur(1,gensizeerr(contextptr));
	return ;
      }
      double a=f._VECTptr->front()._DOUBLE_val,b=f._VECTptr->back()._DOUBLE_val,c=b-a;
      for (int i=0;i<n;++i)
	res.push_back(a+c*giac_rand(contextptr)/(rand_max2+1.0));
      return;      
    }
    if (f.is_symb_of_sommet(at_poisson) ||f.is_symb_of_sommet(at_POISSON) ){
      f=evalf_double(f._SYMBptr->feuille,1,contextptr);
      if (f.type!=_DOUBLE_ || f._DOUBLE_val<=0){
	res=vecteur(1,gensizeerr(contextptr));
	return ;
      }
      double lambda=f._DOUBLE_val;
      int Nv=int(2*lambda+53); // insure that poisson_cdf(lambda,Nv)==1 up to double precision
      if (Nv*n>5*lambda+n*std::ceil(std::log(double(Nv))/std::log(2.0))){
	vector<giac_double> tableau(Nv+1);
	long_double cumul=0;
	long_double current; 
	for (int k=0;k<Nv;++k){
	  // recompute current from time to time
	  if (k>>5==0)
	    current=std::exp(-lambda+k*std::log(lambda)-lngamma(k+1));
	  cumul += current;
	  tableau[k+1] = cumul;
	  current *= lambda/(k+1);
	}
	for (int i=0;i<n;++i){
	  res.push_back(dichotomy(tableau,double(giac_rand(contextptr))/rand_max2));
	}
	return;
      }
      for (int i=0;i<n;++i)
	res.push_back(randpoisson(lambda,contextptr));
      return;     
    }
    if (f.is_symb_of_sommet(at_exp) || f.is_symb_of_sommet(at_EXP) || f.is_symb_of_sommet(at_randexp) || f.is_symb_of_sommet(at_exponential) || f.is_symb_of_sommet(at_exponentiald)){
      f=evalf_double(f._SYMBptr->feuille,1,contextptr);
      if (f.type!=_DOUBLE_ || f._DOUBLE_val<=0){
	res=vecteur(1,gensizeerr(contextptr));
	return;
      }
      double lambda=f._DOUBLE_val;
      for (int i=0;i<n;++i)
	res.push_back(gen(-std::log(1-giac_rand(contextptr)/(rand_max2+1.0))/lambda));
      return;     
    }
    if (f.is_symb_of_sommet(at_geometric) || f.is_symb_of_sommet(at_randgeometric)){
      f=evalf_double(f._SYMBptr->feuille,1,contextptr);
      if (f.type!=_DOUBLE_ || f._DOUBLE_val<=0){
	res=vecteur(1,gensizeerr(contextptr));
	return;
      }
      double lambda=std::log(1-f._DOUBLE_val);
      for (int i=0;i<n;++i)
	res.push_back(int(std::ceil(std::log(1-giac_rand(contextptr)/(rand_max2+1.0))/lambda)));
      return;     
    }
    if (f==at_normald || f==at_NORMALD || f==at_normal || f==at_randNorm || f==at_randnormald)
      f=symbolic(at_normald,makesequence(0,1));
    if ( (f.is_symb_of_sommet(at_normald) || f.is_symb_of_sommet(at_NORMALD) || f.is_symb_of_sommet(at_normal) || f.is_symb_of_sommet(at_randNorm) || f.is_symb_of_sommet(at_randnormald)) && f._SYMBptr->feuille.type==_VECT && f._SYMBptr->feuille._VECTptr->size()==2 ){
      gen M=evalf_double(f._SYMBptr->feuille._VECTptr->front(),1,contextptr);
      f=evalf_double(f._SYMBptr->feuille._VECTptr->back(),1,contextptr);
      if (is_squarematrix(f)){
	int dim=int(f._VECTptr->size());
	vecteur w(dim);
	for (int i=0;i<n;++i){
	  for (int j=0;j<dim;++j){
	    double u=giac_rand(contextptr)/(rand_max2+1.0);
	    double d=giac_rand(contextptr)/(rand_max2+1.0);
	    w[j]=std::sqrt(-2*std::log(u))*std::cos(2*M_PI*d);
	  }
	  res.push_back(M+multmatvecteur(*f._VECTptr,w));
	}
	return;     
      }
      if (M.type!=_DOUBLE_ || f.type!=_DOUBLE_ || f._DOUBLE_val<=0 ){
	res=vecteur(1,gensizeerr(contextptr));
	return;
      }
      double m=M._DOUBLE_val,sigma=f._DOUBLE_val;
      for (int i=0;i<n;++i){
	double u=giac_rand(contextptr)/(rand_max2+1.0);
	double d=giac_rand(contextptr)/(rand_max2+1.0);
	res.push_back(m+sigma*std::sqrt(-2*std::log(u))*std::cos(2*M_PI*d));
      }
      return;     
    }
    if ( (f.is_symb_of_sommet(at_fisher) || f.is_symb_of_sommet(at_fisherd) || f.is_symb_of_sommet(at_randfisherd)
	  || f.is_symb_of_sommet(at_snedecor) 
	  || f.is_symb_of_sommet(at_randfisher)) && f._SYMBptr->feuille.type==_VECT && f._SYMBptr->feuille._VECTptr->size()==2 ){
      gen g1(f._SYMBptr->feuille._VECTptr->front()),g2(f._SYMBptr->feuille._VECTptr->back());
      if ( is_integral(g1) && g1.type==_INT_ && g1.val>0 && g1.val<=1000 && is_integral(g2) && g2.type==_INT_ && g2.val>0 && g2.val<=1000){
	int k1=g1.val,k2=g2.val;
	for (int i=0;i<n;++i)
	  res.push_back(randchisquare(k1,contextptr)/k1/(randchisquare(k2,contextptr)/k2));
	return ;
      }
    }
    if ( (f.is_symb_of_sommet(at_chisquare) || f.is_symb_of_sommet(at_randchisquare) ||
	  f.is_symb_of_sommet(at_chisquared) || f.is_symb_of_sommet(at_randchisquared) ) && f._SYMBptr->feuille.type==_INT_ && f._SYMBptr->feuille.val>0 && f._SYMBptr->feuille.val<=1000){
      int k=f._SYMBptr->feuille.val;
      for (int i=0;i<n;++i)
	res.push_back(randchisquare(k,contextptr));
      return;     
    }
    if (f==at_cauchy || f==at_cauchyd){
      for (int i=0;i<n;++i)
	res.push_back(std::tan(M_PI*giac_rand(contextptr)/(rand_max2+1.0)-.5));
      return;
    }
      if ( (f.is_symb_of_sommet(at_cauchy) || f.is_symb_of_sommet(at_cauchyd) ) && f._SYMBptr->feuille.type==_VECT && f._SYMBptr->feuille._VECTptr->size()==2 ){
      gen g1(f._SYMBptr->feuille._VECTptr->front()),g2(f._SYMBptr->feuille._VECTptr->back());
      g1=evalf_double(g1,1,contextptr);
      g2=evalf_double(g2,1,contextptr);
      if (g1.type!=_DOUBLE_ || g2.type!=_DOUBLE_){
	res=vecteur(1,gensizeerr(contextptr));
	return;
      }
      //double d1=g1._DOUBLE_val,d2=g2._DOUBLE_val;
      for (int i=0;i<n;++i)
	res.push_back(std::tan(M_PI*giac_rand(contextptr)/(rand_max2+1.0)-.5)*g2+g1);
      return;
    }
    if ( f.is_symb_of_sommet(at_student) || f.is_symb_of_sommet(at_randstudent) ||
	 f.is_symb_of_sommet(at_studentd) || f.is_symb_of_sommet(at_randstudentd)){
      if (f._SYMBptr->feuille.type==_INT_ && f._SYMBptr->feuille.val>0 && f._SYMBptr->feuille.val<=1000){
	int k=f._SYMBptr->feuille.val;
	for (int i=0;i<n;++i)
	  res.push_back(randstudent(k,contextptr));
	return; 
      }
      res= vecteur(1,gensizeerr(contextptr));
      return;
    }
    if (f.is_symb_of_sommet(at_multinomial) && f._SYMBptr->feuille.type==_VECT){
      gen P=f._SYMBptr->feuille;
      vecteur val;
      if (P._VECTptr->size()==2 && P._VECTptr->front().type==_VECT){
	if (P._VECTptr->back().type!=_VECT || P._VECTptr->front()._VECTptr->size()!=P._VECTptr->back()._VECTptr->size()){
	  res=vecteur(1,gensizeerr(contextptr));
	  return;
	}
	val=*P._VECTptr->back()._VECTptr;
	P=P._VECTptr->front();
      }
      if (!is_zero(1-_sum(P,contextptr))){
	res=vecteur(1,gensizeerr(contextptr));
	return;
      }
      const vecteur & v=*P._VECTptr;
      // cdf of probabilities
      unsigned vs=unsigned(v.size());
      vector<giac_double> tableau(vs+1);
      vector<int> eff(vs);
      if (!val.empty())
	res.reserve(n);
      gen g=evalf_double(v[0],1,contextptr);
      if (g.type!=_DOUBLE_){
	res=vecteur(1,gensizeerr(contextptr));
	return;
      }
      tableau[1]=g._DOUBLE_val*rand_max2;
      for (unsigned i=1;i<vs;++i){
	g=evalf_double(v[i],1,contextptr);
	if (g.type!=_DOUBLE_){
	  res=vecteur(1,gensizeerr(contextptr));
	  return;
	}
	tableau[i+1]=g._DOUBLE_val*rand_max2+tableau[i];
      }
      // generate n random values, count them if val=0 
      for (unsigned i=0;int(i)<n;++i){
	int j=dichotomy(tableau,giac_rand(contextptr));
	if (j>=int(vs))
	  j=vs;
	if (val.empty())
	  ++eff[j];
	else 
	  res.push_back(val[j]);
      }
      if (val.empty())
	vector_int2vecteur(eff,res);
      return;
    }
    if ( (f.is_symb_of_sommet(at_binomial) || f.is_symb_of_sommet(at_BINOMIAL))
	 && f._SYMBptr->feuille.type==_VECT && f._SYMBptr->feuille._VECTptr->size()==2){
      gen N=f._SYMBptr->feuille._VECTptr->front();
      f=evalf_double(f._SYMBptr->feuille._VECTptr->back(),1,contextptr);
      if (!is_integral(N) || N.type!=_INT_ || N.val<=0 || f.type!=_DOUBLE_ || f._DOUBLE_val<=0 || f._DOUBLE_val>=1){
	res= vecteur(1,gensizeerr(contextptr));
	return;
      }
      double p=f._DOUBLE_val;
      int Nv=N.val;
      if (Nv==1){
	int seuil=int(rand_max2*p);
	for (int i=0;i<n;++i){
	  res.push_back(giac_rand(contextptr)<=seuil);
	}
	return;
      }
      // computation time is proportionnal to Nv*n with the sum of n randoms value 0/1
      // other idea compute once binomial_cdf(Nv,k,p) for k in [0..Nv]
      // then find position of random value in the list: this costs Nv+n*ceil(log2(Nv)) operations
      if (double(Nv)*n>5*Nv+n*std::ceil(std::log(double(Nv))/std::log(2.0))){
	vector<giac_double> tableau(Nv+1);
	long_double cumul=0;
	long_double current; // =std::pow(1-p,Nv);
	for (int k=0;k<Nv;++k){
	  // recompute current from time to time
	  if (k%32==0)
	    current=std::exp(lngamma(Nv+1)-lngamma(k+1)-lngamma(Nv-k+1)+k*std::log(p)+(Nv-k)*std::log(1-p));
	  cumul += current;
	  tableau[k+1] = cumul;
	  current *= p*(Nv-k)/(k+1)/(1-p); 
	}
	for (int i=0;i<n;++i){
	  res.push_back(dichotomy(tableau,double(giac_rand(contextptr))/rand_max2));
	}
	return;
      }
      if (Nv>1000){
	for (int i=0;i<n;++i)
	  res.push_back(binomial_icdf(Nv,p,double(giac_rand(contextptr))/rand_max2,contextptr));
      }
      else {
	p *= rand_max2;
	for (int i=0;i<n;++i){
	  int ok=0;	  
	  for (int j=0;j<Nv;++j){
	    if (giac_rand(contextptr)<=p)
	      ok++;
	  }
	  res.push_back(ok);
	}
      }
      return;     
    }
    if (f.is_symb_of_sommet(at_program)){
      for (int i=0;i<n;++i)
	res.push_back(f(vecteur(0),contextptr));
      return;
    }
    if (f.is_symb_of_sommet(at_rootof)){
      gen ff=f._SYMBptr->feuille;
      if (ff.type==_VECT && !ff._VECTptr->empty()){
	ff=ff._VECTptr->back();
	if (ff.type==_VECT && !ff._VECTptr->empty()){
	  int d=int(ff._VECTptr->size())-1;
	  for (int i=0;i<n;++i){
	    gen g=vranm(d,0,contextptr);
	    res.push_back(symb_rootof(g,ff,contextptr));
	  }
	  return;
	}
      }
    }
    for (int i=0;i<n;++i)
      res.push_back(eval(f,eval_level(contextptr),contextptr));
  }

  vecteur vranm(int n,const gen & F,GIAC_CONTEXT){
    vecteur res;
    vranm(n,F,res,contextptr);
    return res;
  }

  matrice mranm(int n,int m,const gen & f,GIAC_CONTEXT){
    n=giacmax(1,n);
    m=giacmax(1,m);
    if (longlong(n)*m>LIST_SIZE_LIMIT)
      setstabilityerr();
    matrice res;
    res.reserve(n);
    for (int i=0;i<n;++i){
      res.push_back(vecteur(0));
      vranm(m,f,*res[i]._VECTptr,contextptr);
    }
    return res;
  }

  gen _ranm(const gen & e,GIAC_CONTEXT){
    if ( e.type==_STRNG && e.subtype==-1) return  e;
    int n=0,m=0;
    switch (e.type){
    case _INT_:
      return vranm(e.val,zero,contextptr);
    case _DOUBLE_:
      return vranm(int(e._DOUBLE_val),zero,contextptr);
    case _VECT:
      if (e._VECTptr->size()==1)
	return _ranm(e._VECTptr->front(),contextptr);
      if (e._VECTptr->size()>=2){
	if (e._VECTptr->front().type==_INT_)
	  n=e._VECTptr->front().val;
	else {
	  if (e._VECTptr->front().type==_DOUBLE_)
	    n=int(e._VECTptr->front()._DOUBLE_val);
	  else
	    return gensizeerr(contextptr);
	}
	if ((*e._VECTptr)[1].type==_INT_)
	  m=(*e._VECTptr)[1].val;
	else {
	  if ((*e._VECTptr)[1].type==_DOUBLE_)
	    m=int((*e._VECTptr)[1]._DOUBLE_val);
	  else
	    return _randvector(e,contextptr); // try vector instead of gensizeerr(contextptr);
	}
	if (e._VECTptr->size()==3)
	  return gen(mranm(n,m,e._VECTptr->back(),contextptr),_MATRIX__VECT);
	if (e._VECTptr->size()==4){
	  gen loi=(*e._VECTptr)[2];
	  if (loi.type==_INT_ && e._VECTptr->back().type==_INT_){
	    // random integer vector in interval
	    int a=loi.val,b=e._VECTptr->back().val;
	    matrice M(n);
	    for (int j=0;j<n;++j){
	      gen res=vecteur(m);
	      for (int k=0;k<m;++k){
		(*res._VECTptr)[k]=(a+int((b-a+1)*(giac_rand(contextptr)/(rand_max2+1.0))));
	      }
	      M[j]=res;
	    }
	    return gen(M,_MATRIX__VECT);
	  }
	  if (loi.type==_FUNC){
	    if (loi==at_multinomial)
	      loi=symbolic(at_multinomial,e._VECTptr->back());
	    else
	      loi=loi(e._VECTptr->back(),contextptr);
	  }
	  else
	    loi=symb_of(loi,e._VECTptr->back());
	  return gen(mranm(n,m,loi,contextptr),_MATRIX__VECT);
	}
	if (e._VECTptr->size()>4){
	  gen loi=(*e._VECTptr)[2];
	  if (loi.type==_FUNC){
	    if (loi==at_multinomial)
	      loi=symbolic(at_multinomial,gen(vecteur(e._VECTptr->begin()+3,e._VECTptr->end()),_SEQ__VECT));
	    else
	      loi=loi(gen(vecteur(e._VECTptr->begin()+3,e._VECTptr->end()),_SEQ__VECT),contextptr);
	  }
	  else
	    loi=symb_of(loi,gen(vecteur(e._VECTptr->begin()+3,e._VECTptr->end()),_SEQ__VECT));
	  return gen(mranm(n,m,loi,contextptr),_MATRIX__VECT);
	}
	return gen(mranm(n,m,0,contextptr),_MATRIX__VECT);
      }
    default:
      return gensizeerr(contextptr);
    }
    return undef;
  }
  static const char _ranm_s []="ranm";
  static define_unary_function_eval (__ranm,&giac::_ranm,_ranm_s);
  define_unary_function_ptr5( at_ranm ,alias_at_ranm,&__ranm,0,true);

  gen _randvector(const gen & e,GIAC_CONTEXT){
    if ( e.type==_STRNG && e.subtype==-1) return  e;
    int n=0;
    switch (e.type){
    case _INT_:
      return vranm(e.val,zero,contextptr);
    case _DOUBLE_:
      return vranm(int(e._DOUBLE_val),zero,contextptr);
    case _VECT:
      if (e._VECTptr->size()==1)
	return _randvector(e._VECTptr->front(),contextptr);
      if (e._VECTptr->size()>=2){
	if (e._VECTptr->front().type==_INT_)
	  n=e._VECTptr->front().val;
	else {
	  if (e._VECTptr->front().type==_DOUBLE_)
	    n=int(e._VECTptr->front()._DOUBLE_val);
	  else
	    return gensizeerr(contextptr);
	}
	gen loi=(*e._VECTptr)[1];
	gen res(vecteur(0));
	if (e._VECTptr->size()==3){
	  if (loi.type==_INT_ && e._VECTptr->back().type==_INT_){
	    // random integer vector in interval
	    int a=loi.val,b=e._VECTptr->back().val;
	    res._VECTptr->reserve(n);
	    for (int j=0;j<n;++j){
	      res._VECTptr->push_back(a+int((b-a+1)*(giac_rand(contextptr)/(rand_max2+1.0))));
	    }
	    return res;
	  } 
	  if (loi.type==_FUNC){
	    if (loi==at_multinomial)
	      loi=symbolic(at_multinomial,e._VECTptr->back());
	    else
	      loi=loi(e._VECTptr->back(),contextptr);
	  }
	  else
	    loi=symb_of(loi,e._VECTptr->back());
	}
	if (e._VECTptr->size()>3){
	  if (loi.type==_FUNC){
	    if (loi==at_multinomial)
	      loi=symbolic(at_multinomial,gen(vecteur(e._VECTptr->begin()+2,e._VECTptr->end()),_SEQ__VECT));
	    else 
	      loi=loi(gen(vecteur(e._VECTptr->begin()+2,e._VECTptr->end()),_SEQ__VECT),contextptr);
	  }
	  else
	    loi=symb_of(loi,gen(vecteur(e._VECTptr->begin()+2,e._VECTptr->end()),_SEQ__VECT));
	}
	vranm(n,loi,*res._VECTptr,contextptr);
	return res;
      }
    default:
      return gensizeerr(contextptr);
    }
    return undef;
  }
  static const char _randvector_s []="randvector";
  static define_unary_function_eval (__randvector,&giac::_randvector,_randvector_s);
  define_unary_function_ptr5( at_randvector ,alias_at_randvector,&__randvector,0,true);

  static const char _ranv_s []="ranv";
  static define_unary_function_eval (__ranv,&giac::_randvector,_ranv_s);
  define_unary_function_ptr5( at_ranv ,alias_at_ranv,&__ranv,0,true);

#ifdef HAVE_LIBLAPACK
  bool matrix2zlapack(const std_matrix<gen> & m,doublef2c_complex * A,GIAC_CONTEXT){
    std_matrix<gen>::const_iterator it=m.begin(),itend=m.end();
    gen g;
    int rows=itend-it;
    for (int i = 0; it!=itend; ++i,++it){
      const_iterateur jt=it->begin(),jtend=it->end();
      for (int j = 0; jt!=jtend;++j, ++jt){
	g=evalf_double(*jt,1,contextptr);
	if (g.type==_DOUBLE_){
	  A[i + j * rows].r=g._DOUBLE_val;
	  A[i + j * rows].i=0;
	  continue;
	}
	if (g.type==_CPLX && g._CPLXptr->type==_DOUBLE_ && (*g._CPLXptr+1).type==_DOUBLE_){
	  A[i + j * rows].r = g._CPLXptr->_DOUBLE_val;
	  A[i + j * rows].i = (g._CPLXptr+1)->_DOUBLE_val;
	}
	else 
	  return false;
      }
    }
    return true;
  }

  bool matrice2zlapack(const matrice & m,doublef2c_complex * A,GIAC_CONTEXT){
    const_iterateur it=m.begin(),itend=m.end();
    gen g;
    int rows=itend-it;
    for (int i = 0; it!=itend; ++i,++it){
      if (it->type!=_VECT)
	return false;
      const_iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
      for (int j = 0; jt!=jtend;++j, ++jt){
	g=evalf_double(*jt,1,contextptr);
	if (g.type==_DOUBLE_){
	  A[i + j * rows].r=g._DOUBLE_val;
	  A[i + j * rows].i=0;
	  continue;
	}
	if (g.type==_CPLX && g._CPLXptr->type==_DOUBLE_ && (*g._CPLXptr+1).type==_DOUBLE_){
	  A[i + j * rows].r = g._CPLXptr->_DOUBLE_val;
	  A[i + j * rows].i = (g._CPLXptr+1)->_DOUBLE_val;
	}
	else 
	  return false;
      }
    }
    return true;
  }

  void zlapack2matrix(doublef2c_complex * A,unsigned rows,unsigned cols,std_matrix<gen> & R){
    R.resize(rows);
    for (unsigned i=0;i<rows;++i){
      vecteur r(cols);
      for (unsigned j=0;j<cols;++j)
	r[j] = gen(A[i + j * rows].r,A[i + j * rows].i);
      R[i]=r;
    }
  }

  void zlapack2matrice(doublef2c_complex * A,unsigned rows,unsigned cols,matrice & R){
    R.resize(rows);
    for (unsigned i=0;i<rows;++i){
      vecteur r(cols);
      for (unsigned j=0;j<cols;++j)
	r[j] = gen(A[i + j * rows].r,A[i + j * rows].i);
      R[i]=r;
    }
  }
#endif

  bool minv(const matrice & a,matrice & res,bool convert_internal,int algorithm,GIAC_CONTEXT){
#ifdef HAVE_LIBLAPACK
    if (is_squarematrix(a) && is_fully_numeric(a) && int(a.size())>=CALL_LAPACK){
      integer N,LDA,INFO,LWORK;
      int n=a.size();
      LDA=n; N=n; LWORK=N*N;
      integer * IPIV=new integer[n];
      if (is_zero(im(a,contextptr))){
	double * A = new double[N*N];
	matrice2lapack(a,A,contextptr);
	dgetrf_( &N, &N, A, &LDA, IPIV, &INFO );
	if (INFO){
	  delete [] IPIV;
	  delete [] A;
	  return false;
	}
	double * WORK=new double [LWORK];
	/* DGETRI( N, A, LDA, IPIV, WORK, LWORK, INFO ) */
	dgetri_(&N,A,&LDA,IPIV,WORK,&LWORK,&INFO);
	delete [] IPIV; delete [] WORK;
	if (INFO){
	  delete [] A; 
	  return false;
	}
	lapack2matrice(A,N,N,res);
	delete [] A; 
	return true;
      }
      doublef2c_complex * A = new doublef2c_complex[N*N];
      matrice2zlapack(a,A,contextptr);
      zgetrf_( &N, &N, A, &LDA, IPIV, &INFO );
      if (INFO){
	delete [] IPIV;
	delete [] A;
	return false;
      }
      doublef2c_complex * WORK=new doublef2c_complex [LWORK];
      /* ZGETRI( N, A, LDA, IPIV, WORK, LWORK, INFO ) */
      zgetri_(&N,A,&LDA,IPIV,WORK,&LWORK,&INFO);
      delete [] IPIV; delete [] WORK;
      if (INFO){
	delete [] A; 
	return false;
      }
      zlapack2matrice(A,N,N,res);
      delete [] A; 
      return true;
    }
#endif
    if (debug_infolevel)
      CERR << CLOCK() << " matrix inv begin" << endl;
    matrice arref = a;
    add_identity(arref);
    if (debug_infolevel)
      CERR << CLOCK() << " identity added" << endl;
    int s=int(a.size());
    gen det;
    vecteur pivots;
    int ok=mrref(arref,res,pivots,det,0,s,0,2*s,
		 /* fullreduction */2,0,convert_internal,algorithm,0,
		 contextptr);
    if (!ok)
      return false;
    if (debug_infolevel)
      CERR << CLOCK() << " remove identity" << endl;
    if (ok!=2 && !remove_identity(res,contextptr))
      return false;
    if (debug_infolevel)
      CERR << CLOCK() << " end matrix inv" << endl;
    return true;
  }

  matrice minv(const matrice & a,GIAC_CONTEXT){
    matrice res;
    if (!minv(a,res,/*convert_internal */true,/* algorithm */ 1,contextptr))
      return vecteur(1,vecteur(1,gensizeerr(gettext("Not invertible"))));
    return res;
  }

  gen det_minor(const matrice & a,vecteur lv,bool convert_internal,GIAC_CONTEXT){
    int n=int(a.size());
    if (n==1)
      return a.front()._VECTptr->front();
    std_matrix<gen> A;
    if (convert_internal){
      lv=alg_lvar(a);
      matrice2std_matrix_gen(*(e2r(a,lv,contextptr)._VECTptr),A);
    }
    else
      matrice2std_matrix_gen(a,A);
    gen deno(1);
    for (int i=0;i<n;++i){
      gen ppcm(1);
      for (int j=0;j<n;++j){
	if (A[i][j].type==_FRAC)
	  ppcm=lcm(ppcm,A[i][j]._FRACptr->den);
      }
      if (!is_one(ppcm)){
	for (int j=0;j<n;++j){
	  A[i][j]=A[i][j]*ppcm;
	}
	deno=deno*ppcm;
      }
    }
    index_t index(n),mineur_index(n);
    map< index_t, gen > tab_mineurs,old_tab;
    // int s=int(std::exp(lgamma(n+1)-2*lgamma(n/2+1)))+1;
    // init: compute 2*2 determinants lines i,j columns 1,2
    gen res;
    for (int i=0;i<n;++i){
      index[0]=i;
      for (int j=i+1;j<n;++j){
	index[1]=j;
	res=A[i][0]*A[j][1]-A[i][1]*A[j][0];
	tab_mineurs[index]=res;
      }
    }
    // compute all possibles i*i det with columns 0..i using (i-1)*(i-1) det
    for (int i=2;i<n;++i){
      if (debug_infolevel>2)
	CERR << "// Computing " << i+1 << "*" << i+1 << "minors " << CLOCK() << endl;
      swap(old_tab,tab_mineurs);
      tab_mineurs.clear();
      // initialize index
      for (int j=0;j<=i;++j)
	index[j]=j;
      // computation loop
      for (;;){
	res=zero;
	for (int j=0;j<=i;++j){
	  // make mineur without line index[j]
	  for (int k=0;k<=i;++k){
	    if (k==j)
	      continue;
	    if (k>j)
	      mineur_index[k-1]=index[k];
	    else
	      mineur_index[k]=index[k];
	  }
	  if ((i+j)%2)
	    res = res-A[index[j]][i]*old_tab[mineur_index];
	  else
	    res = res+A[index[j]][i]*old_tab[mineur_index];
	}
	tab_mineurs[index]=res;
	// increment index and test for breaking loop
	int j=i;
	for (;j>=0;--j){
	  ++index[j];
	  if (index[j]!=n+j-i)
	    break;
	}
	if (j<0)
	  break;
	for (;j<i;++j)
	  index[j+1]=index[j]+1;
      }
    }
    if (debug_infolevel>2)
      CERR << "// Computation done " << CLOCK() << endl;
    res = res/deno;
    if (convert_internal)
      return r2sym(res,lv,contextptr);
    else
      return res;
  }

  // determinant by expanding wrt last column
  gen det_minor(const matrice & a,bool convert_internal,GIAC_CONTEXT){
    vecteur lv;
    return det_minor(a,lv,convert_internal,contextptr);
  }

  gen _det_minor(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (!is_squarematrix(a)){
      if (a.type==_VECT && a._VECTptr->size()==2 && is_squarematrix(a._VECTptr->front())){
	vecteur v(1,a._VECTptr->back());
	return det_minor(*a._VECTptr->front()._VECTptr,v,true,contextptr);
      }
      return symbolic(at_det_minor,a);
    }
    return det_minor(*a._VECTptr,true,contextptr);
  }
  static const char _det_minor_s []="det_minor";
  static define_unary_function_eval (__det_minor,(const gen_op_context)giac::_det_minor,_det_minor_s);
  define_unary_function_ptr5( at_det_minor ,alias_at_det_minor,&__det_minor,0,true);

  gen mdet(const matrice & a,GIAC_CONTEXT){
    if (!is_squarematrix(a))
      return gendimerr(contextptr);
    vecteur pivots;
    matrice res;
    gen determinant;
    int s=int(a.size());
    if (!mrref(a,res,pivots,determinant,0,s,0,s,
	  /* fullreduction */0,0,true,1/* guess algorithm */,1/* determinant */,
	       contextptr))
      return gendimerr(contextptr);
    return determinant;
  }

  gen _det(const gen & a_orig,GIAC_CONTEXT){
    if ( a_orig.type==_STRNG && a_orig.subtype==-1) return  a_orig;
    matrice a;
    bool convert_internal,minor_det,keep_pivot;
    int algorithm,last_col;
    if (!read_reduction_options(a_orig,a,convert_internal,algorithm,minor_det,keep_pivot,last_col))
      return gensizeerr(contextptr);
    if (keep_pivot)
      return gensizeerr(gettext("Option keep_pivot not applicable"));
    if (minor_det)
      return det_minor(a,convert_internal,contextptr);
    if (!is_squarematrix(a))
      *logptr(contextptr) << gettext("Warning: non-square matrix!") << endl;
    vecteur pivots;
    matrice res;
    gen determinant;
    int s=int(a.size());
    if (!mrref(a,res,pivots,determinant,0,s,0,s,
	  /* fullreduction */0,0,convert_internal,algorithm,1/* det */,
	       contextptr))
      return gendimerr(contextptr);
    return determinant;
  }
  static const char _det_s []="det";
  static define_unary_function_eval (__det,&giac::_det,_det_s);
  define_unary_function_ptr5( at_det ,alias_at_det,&__det,0,true);

  // Find minimal poly by trying with 3 random vectors
  bool probabilistic_pmin(const matrice & m,vecteur & w,bool check,GIAC_CONTEXT){
    int n=int(m.size());
    modpoly p;
    for (int i=0;i<3;++i){
      vecteur v(vranm(n,0,0));
      // /* Old algorithm
      matrice temp(1,v);
      for (int j=0;j<n;++j){
	v=multmatvecteur(m,v);
	temp.push_back(v);
      }
      temp=mtran(temp);
      temp=mker(temp,contextptr);
      if (temp.empty() || is_undef(temp))
	return false; // setsizeerr();
      w=-*temp.front()._VECTptr;
      reverse(w.begin(),w.end());
      w=trim(w,0);
      // */
      /*
      // New algorithm using A^(2n-1)v and Pade
      vecteur temp(1,v[0]);
      for (int j=1;j<2*n;++j){
	v=multmatvecteur(m,v);
	temp.push_back(v[0]);
      }
      w=reverse_rsolve(temp,false);
      // End new algorithù
      */
      if (signed(w.size())!=n+1 && !p.empty())
	w=lcm(w,p,0);
      p=w;
      if (signed(w.size())==n+1){
	w=w/w.front();
	return true;
      }
    }
    if (!check)
      return false;
    gen res=horner(w,m);
    return is_zero(res,contextptr);
  }

  // Reduction to Hessenberg form, see e.g. Cohen algorithm 2.2.9
  // (with C array indices)
  // integer modulo case
  void mhessenberg(vector< vector<int> > & H,vector< vector<int> > & P,int modulo,bool compute_P){
    int t,u,n=int(H.size());
    vecteur vtemp;
    for (int m=0;m<n-2;++m){
      if (debug_infolevel>=5)
	CERR << "// hessenberg reduction line " << m << endl;
      // check for a non zero coeff in the column m below ligne m+1
      int i=m+1;
      for (;i<n;++i){
	t=H[i][m];
	if (t)
	  break;
      }
      if (i==n) //not found
	continue;
      t=invmod(t,modulo);
      // permutation of lines m+1 and i and columns m+1 and i
      if (i>m+1){
	H[i].swap(H[m+1]);
	if (compute_P)
	  P[i].swap(P[m+1]);
	for (int j=0;j<n;++j){
	  swapint(H[j][i],H[j][m+1]);
	  // tmp=H[j][i]; H[j][i]=H[j][m+1]; H[j][m+1]=tmp;
	}
      }
      // now coeff at line m+1 column m is H[m+1][m]=t!=0
      // creation of zeros in column m+1, lines i=m+2 and below
      vector<int> & Hmp1=H[m+1];
      for (i=m+2;i<n;++i){
	// line operation
	vector<int> & Hi=H[i];
	u=((longlong) t*Hi[m])%modulo;
	if (!u){ 
	  //CERR << "zero " << m << " " << i << endl;
	  continue;
	}
	if (debug_infolevel>3)
	  CERR << "// i=" << i << " " << u <<endl;
	modlinear_combination(Hi,-u,Hmp1,modulo,m,0,false); // H[i]=H[i]-u*H[m+1]; COULD START at m
	// column operation
	for (int j=0;j<n;++j){
	  vector<int> & Hj=H[j];
#ifdef _I386_
	  mod(Hj[m+1],u,Hj[i],modulo);
#else
	  int * ptr=&Hj[m+1];
	  *ptr=(*ptr+longlong(u)*Hj[i])%modulo;
#endif
	}
	if (compute_P)
	  modlinear_combination(P[i],-u,P[m+1],modulo,0,0,false); // P[i]=P[i]-u*P[m+1];
      }
    }
  }

  // Hessenberg reduction, P is not orthogonal
  // P^(-1)*H*P = original
  void hessenberg(std_matrix<gen> & H,std_matrix<gen> & P,GIAC_CONTEXT){
    int n=int(H.size());
    gen t,tabs,u,tmp;
    vecteur vtemp;
    for (int m=0;m<n-2;++m){
      if (debug_infolevel>=5)
	CERR << "// hessenberg reduction line " << m << endl;
      // check for a non zero coeff in the column m below ligne m+1
      int i=m+1;
      gen pivot=0;
      int pivotline=0;
      for (;i<n;++i){
	t=H[i][m];
	tabs=abs(t,contextptr);
	if (is_strictly_greater(tabs,pivot,contextptr)){
	  pivotline=i;
	  pivot=tabs;
	}
      }
      if (is_zero(pivot)) //not found
	continue;
      i=pivotline;
      t=H[i][m];
      // permutation of lines m+1 and i and columns m+1 and i
      /*
      if (i>m+1){
	for (int j=0;j<n;++j){
	  tmp=H[i][j];
	  H[i][j]=H[m+1][j];
	  H[m+1][j]=tmp;
	}
	for (int j=0;j<n;++j){
	  tmp=H[j][i];
	  H[j][i]=H[j][m+1];
	  H[j][m+1]=tmp;
	}
      }
      */
      if (i>m+1){
	swap(H[i],H[m+1]);
	swap(P[i],P[m+1]);
	for (int j=0;j<n;++j){
	  vecteur & Hj=H[j];
	  swapgen(Hj[i],Hj[m+1]);
	}
      }
      // now coeff at line m+1 column m is H[m+1][m]=t!=0
      // creation of zeros in column m+1, lines i=m+2 and below
      for (i=m+2;i<n;++i){
	// line operation
	u=rdiv(H[i][m],t,contextptr);
	if (debug_infolevel>2)
	  CERR << "// i=" << i << " " << u <<endl;
	linear_combination(plus_one,H[i],-u,H[m+1],plus_one,1,vtemp,1e-12,0); // H[i]=H[i]-u*H[m+1];
	swap(H[i],vtemp);
	linear_combination(plus_one,P[i],-u,P[m+1],plus_one,1,vtemp,1e-12,0); // H[i]=H[i]-u*H[m+1];
	swap(P[i],vtemp);
	// column operation
	for (int j=0;j<n;++j){
	  vecteur & Hj=H[j];
	  tmp=Hj[m+1]+u*Hj[i];
	  Hj[m+1]=tmp;
	}
      }
    }
  }

  // a*A+c*C->A
  // c*A-a*C->C
  void bi_linear_combination_AC(giac_double a,vector<giac_double> & A,giac_double c,vector<giac_double> & C,int cstart=0,int cend=-1){
    giac_double * Aptr=&A.front()+cstart;
    giac_double * Cptr=&C.front()+cstart,* Cend=&C.front()+(cend<0?C.size():cend);
    for (;Cptr!=Cend;++Aptr,++Cptr){
      giac_double tmp=a*(*Aptr)+c*(*Cptr);
      *Cptr=c*(*Aptr)-a*(*Cptr);
      *Aptr=tmp;
    }
  }

  // a*A+c*C->C
  // c*A-a*C->A
  void bi_linear_combination_CA(giac_double a,vector<giac_double> & A,giac_double c,vector<giac_double> & C,int cstart,int cend){
    giac_double * Aptr=&A.front()+cstart;
    giac_double * Cptr=&C.front()+cstart,* Cend=Cptr+(cend-cstart);
    Cend-=4;
    for (;Cptr<=Cend;){
      giac_double tmp;
      tmp=c*(*Aptr)-a*(*Cptr);
      *Cptr=a*(*Aptr)+c*(*Cptr);
      *Aptr=tmp;
      ++Aptr;++Cptr;
      // 1
      tmp=c*(*Aptr)-a*(*Cptr);
      *Cptr=a*(*Aptr)+c*(*Cptr);
      *Aptr=tmp;
      ++Aptr;++Cptr;
      //2
      tmp=c*(*Aptr)-a*(*Cptr);
      *Cptr=a*(*Aptr)+c*(*Cptr);
      *Aptr=tmp;
      ++Aptr;++Cptr;
      //3
      tmp=c*(*Aptr)-a*(*Cptr);
      *Cptr=a*(*Aptr)+c*(*Cptr);
      *Aptr=tmp;
      ++Aptr;++Cptr;
    }
    Cend+=4;
    for (;Cptr<Cend;){
      giac_double tmp=c*(*Aptr)-a*(*Cptr);
      *Cptr=a*(*Aptr)+c*(*Cptr);
      *Aptr=tmp;
      ++Aptr;++Cptr;
    }
  }

  void tri_linear_combination(giac_double c11,vector<giac_double> & x1,giac_double c12,vector<giac_double> & x2,giac_double c13,vector<giac_double> &x3,giac_double c22,giac_double c23,giac_double c33,int cstart=0,int cend=-1){
    vector<giac_double>::iterator it1=x1.begin()+cstart,it2=x2.begin()+cstart,it3=x3.begin()+cstart,it3end=cend<0?x3.end():x3.begin()+cend;
    for (;it3!=it3end;++it1,++it2,++it3){
      giac_double d1=*it1,d2=*it2,d3=*it3;
      *it1=c11*d1+c12*d2+c13*d3;
      *it2=c12*d1+c22*d2+c23*d3;
      *it3=c13*d1+c23*d2+c33*d3;
    }
  }

  // First a*A+b*B->B and b*A-a*B->A
  // Then aprime*C+bprime*B->B and bprime*C-aprime*B->C
  void tri_linear_combination(giac_double a,vector<giac_double> & A,giac_double b,vector<giac_double> & B,giac_double aprime,vector<giac_double> & C,giac_double bprime,int cstart,int cend=0){
    giac_double * Aptr=&A.front()+cstart, *Cptr=&C.front()+cstart;
    giac_double * Bptr=&B.front()+cstart,* Bend=Bptr+(cend<=0?(B.size()-cstart):cend-cstart);
    Bend-=8;
    for (;Bptr<=Bend;){ // 3 read/3 write for 1
      giac_double tmp1=*Aptr,tmp2=*Bptr;
      *Aptr=b*tmp1-a*tmp2;
      tmp2=a*tmp1+b*tmp2;
      tmp1=*Cptr;
      *Bptr=aprime*tmp1+bprime*tmp2;
      *Cptr=bprime*tmp1-aprime*tmp2;
      ++Aptr; ++Bptr; ++Cptr;
      // 1
      tmp1=*Aptr; tmp2=*Bptr;
      *Aptr=b*tmp1-a*tmp2;
      tmp2=a*tmp1+b*tmp2;
      tmp1=*Cptr;
      *Bptr=aprime*tmp1+bprime*tmp2;
      *Cptr=bprime*tmp1-aprime*tmp2;
      ++Aptr; ++Bptr; ++Cptr;
      // 2
      tmp1=*Aptr; tmp2=*Bptr;
      *Aptr=b*tmp1-a*tmp2;
      tmp2=a*tmp1+b*tmp2;
      tmp1=*Cptr;
      *Bptr=aprime*tmp1+bprime*tmp2;
      *Cptr=bprime*tmp1-aprime*tmp2;
      ++Aptr; ++Bptr; ++Cptr;
      // 3
      tmp1=*Aptr; tmp2=*Bptr;
      *Aptr=b*tmp1-a*tmp2;
      tmp2=a*tmp1+b*tmp2;
      tmp1=*Cptr;
      *Bptr=aprime*tmp1+bprime*tmp2;
      *Cptr=bprime*tmp1-aprime*tmp2;
      ++Aptr; ++Bptr; ++Cptr;
      // 4
      tmp1=*Aptr; tmp2=*Bptr;
      *Aptr=b*tmp1-a*tmp2;
      tmp2=a*tmp1+b*tmp2;
      tmp1=*Cptr;
      *Bptr=aprime*tmp1+bprime*tmp2;
      *Cptr=bprime*tmp1-aprime*tmp2;
      ++Aptr; ++Bptr; ++Cptr;
      // 5
      tmp1=*Aptr; tmp2=*Bptr;
      *Aptr=b*tmp1-a*tmp2;
      tmp2=a*tmp1+b*tmp2;
      tmp1=*Cptr;
      *Bptr=aprime*tmp1+bprime*tmp2;
      *Cptr=bprime*tmp1-aprime*tmp2;
      ++Aptr; ++Bptr; ++Cptr;
      // 6
      tmp1=*Aptr; tmp2=*Bptr;
      *Aptr=b*tmp1-a*tmp2;
      tmp2=a*tmp1+b*tmp2;
      tmp1=*Cptr;
      *Bptr=aprime*tmp1+bprime*tmp2;
      *Cptr=bprime*tmp1-aprime*tmp2;
      ++Aptr; ++Bptr; ++Cptr;
      // 7
      tmp1=*Aptr; tmp2=*Bptr;
      *Aptr=b*tmp1-a*tmp2;
      tmp2=a*tmp1+b*tmp2;
      tmp1=*Cptr;
      *Bptr=aprime*tmp1+bprime*tmp2;
      *Cptr=bprime*tmp1-aprime*tmp2;
      ++Aptr; ++Bptr; ++Cptr;
    }
    Bend+=8;
    for (;Bptr<Bend;++Cptr,++Aptr,++Bptr){
      giac_double tmp1=*Aptr,tmp2=*Bptr;
      *Aptr=b*tmp1-a*tmp2;
      tmp2=a*tmp1+b*tmp2;
      tmp1=*Cptr;
      *Bptr=aprime*tmp1+bprime*tmp2;
      *Cptr=bprime*tmp1-aprime*tmp2;
    }
  }

  bool is_identity(const matrix_double & P){
    int r=int(P.size());
    int c=int(P.front().size());
    if (r!=c)
      return false;
    for (int i=0;i<r;++i){
      const vector<giac_double> v=P[i];
      if (v[i]!=1)
	return false;
      int j=0;
      for (;j<i;++j){
	if (v[j])
	  return false;
      }
      for (++j;j<r;++j){
	if (v[j])
	  return false;
      }
    }
    return true;
  }

  void double_idn(matrix_double & P){
    int cP=int(P.size());
    for (int i=0;i<cP;++i){
      vector<giac_double> & Pi=P[i];
      if (Pi.size()!=cP) Pi.resize(cP);
      vector<giac_double>::iterator it=Pi.begin(),itend=Pi.end();
      for (;it!=itend;++it)
	*it=0;
      Pi[i]=1;
    }
  }

  void qr_givens_p(matrix_double & P,int Pstart,int Pend,int cstart,int n,int lastcol,const vector<giac_double> & coeffs){
    int pos=int(coeffs.size());
    // m-cstart must be < Pend, otherwise nothing to do
    for (int m=lastcol-1;m>=cstart;--m){
      for (;m>=Pend+cstart;--m){
	pos -= 2*(n-1-(m-cstart));
      }
      for (int i=n-1;i>m-cstart;--i){
	// line operation
	--pos;
	double un=-coeffs[pos];
	--pos;
	double tn=coeffs[pos];
	if (un==0)
	  continue;
	if (i>m-cstart+1){
	  double U=-coeffs[pos-1];
	  double T=coeffs[pos-2];
	  if (U!=0){
	    tri_linear_combination(un,P[i],tn,P[m-cstart],U,P[i-1],T,giacmax(m-cstart,Pstart),Pend);
	    --i;
	    pos-=2;
	    continue;
	  }
	}
	bi_linear_combination_CA(un,P[i],tn,P[m-cstart],giacmax(m-cstart,Pstart),Pend); // givens_linear_combination(un,P[i],tn,P[m],m); 
      }
    } // end for m
  }

  struct thread_givens_p_t {
    matrix_double *Pptr;
    int Pstart,Pend,cstart,n,lastcol;
    const vector<giac_double> * coeffsptr;
  };

  void * do_thread_qr_givens_p(void * ptr_){
    thread_givens_p_t * ptr=(thread_givens_p_t *)ptr_;
    qr_givens_p(*ptr->Pptr,ptr->Pstart,ptr->Pend,ptr->cstart,ptr->n,ptr->lastcol,*ptr->coeffsptr);
    return ptr;
  }

  // QR reduction, Q=P is orthogonal and should be initialized to identity
  // P*H=original if tranpose is false, H=P*original otherwise, Givens method
  // H[rstart..n+rstart-1] n rows, c cols -> Q=nxn matrix and R n rows, c cols
  void qr_givens(matrix_double & H,int rstart,matrix_double & P,bool computeP,bool Pidn,bool transpose,int cstart=0,int cend=0,bool recurse=true){
    int n=int(H.size())-rstart,c=int(H.front().size()),cP=int(P.front().size());
    if (cstart>=c) return;
    if (cend<=0) cend=c;
#ifndef GIAC_HAS_STO_38
    if (recurse && n>=c && cend-cstart>200){
      // if cstart, cend !=0, block-recursive version 
      // H n rows, c1+c2 cols, n>=c1+c2, H=[A1|A2]=Q*[[R11,R12],[0,R22]]
      // A1 and A2 have n rows and c1, c2 columns
      // first step A1=Q1*[[R11],[0]] recursive call, 
      // R11 c1 rows, c1 cols, R12 c1 rows, n-c1 cols, R22 c2 rows, n-c1 cols
      // tran(Q1)*A2=[[R12],[A22]]
      // A22=Q2*R22
      // [A1|A2]=Q1*[[R11,R12],[0,A22]]=Q1*[[Id,0],[0,Q2]]*[[R11,R12],[0,R22]]
      // tran(Q)=[[Id,0],[0,tran(Q2)]]*tran(Q1)
      // If tran(Q1)=[[Q11],[Q12]] then tran(Q)=[[Q11],[tran(Q2)*Q12]]
      // Q12 has n-c1 rows, Q2 has n-c2 rows
      int c1=(cend-cstart)/2,c2=cend-cstart-c1;
      qr_givens(H,rstart,P,true,true,false,cstart,cstart+c1,/* recurse*/ false); // P is Q1
      transpose_double(P); // P is tran(Q1)
      // R11 is in place in H, R21=0 also
      // temporary storage to compute tran(Q1)*A2
      // tranA2 c2 rows, n cols
      matrix_double tranA2; tranA2.reserve(giacmax(c2,n-c1));
      transpose_double(H,rstart,rstart+n,cstart+c1,cend,tranA2);
      matrix_double R(n,vector<giac_double>(n-c1)); 
      mmult_double(P,tranA2,R); // R n rows, c2 cols, n-c1 cols reserved for later use as tranQ12
#if 0
      // QR on A22
      matrix_double A22(R.begin()+c1,R.end()); // A22 n-c1 rows, c2 cols
      matrix_double Q2(P.begin()+c1,P.end());
      qr_givens(A22,0,Q2,computeP,false,true,0,0,false);
      // work on columns c1 to cend of H
      for (int i=0;i<c1;++i){
	std::copy(R[i].begin(),R[i].end(),H[i].begin()+c1);
      }
      for (int i=0;i<n-c1;++i){
	std::copy(A22[i].begin(),A22[i].end(),H[i+c1].begin()+c1);
      }
      for (int i=0;i<n-c1;++i)
	swap(Q2[i],P[i+c1]);
#else
      // QR on A22 stored in rows c1..n-1 of R
      // matrix_double Q2(n-c1,vector<giac_double>(n-c1)); 
      matrix_double & Q2 =tranA2; Q2.resize(n-c1);
      double_idn(Q2);
      qr_givens(R,c1,Q2,computeP,true,false,0,0,/* recurse */true);
      for (int i=0;i<n;++i){
	std::copy(R[i].begin(),R[i].end(),H[rstart+i].begin()+c1);
      }
      // P is tran(Q1), Q12
      transpose_double(Q2);
      matrix_double tmp;
      transpose_double(P,c1,n,0,0,R); // R as tranQ12: n rows, n-c1 cols
      // tran(Q2)*Q12
      mmult_double(Q2,R,tmp); // tmp n-c1 rows, n cols
      for (int i=0;i<n-c1;++i){
	swap(tmp[i],P[i+c1]);
      }
      if (!transpose)
	transpose_double(P);
#endif
      return;
    }
#endif // GIAC_HAS_STO_38
    int lastcol=std::min(n,cend);
    double t,tn,u,un,norme;
    vector<double> coeffs; coeffs.reserve(lastcol*(2*n-lastcol));
    if (debug_infolevel)
      CERR << CLOCK() << " givens start" << endl;
    for (int m=cstart;m<lastcol;++m){
      if (debug_infolevel>=5)
	CERR << "// Givens reduction H line " << m << endl;
      int i;
      // creation of zeros in lines i=m+1 and below
      for (i=m-cstart+1;i<n;++i){
	// line operation
	t=H[m-cstart+rstart][m];
	u=H[i+rstart][m];
	if (u==0){
	  coeffs.push_back(t);
	  coeffs.push_back(u);
	  continue;
	}
	norme=hypot(u,t);
	un=u/norme; tn=t/norme; 
	coeffs.push_back(tn);
	coeffs.push_back(un);
	if (debug_infolevel>=6)
	  CERR << "// i=" << i << " " << u <<endl;
	if (i+1<n){
	  double T=un*u+tn*t, U=H[i+rstart+1][m];
	  if (U!=0){
	    norme=hypot(U,T);
	    U/=norme; T/=norme;
	    coeffs.push_back(T);
	    coeffs.push_back(U);
	    tri_linear_combination(un,H[i+rstart],tn,H[m-cstart+rstart],U,H[i+rstart+1],T,m,cend);
	    ++i;
	    continue;
	  }
	}
	// H[m]=un*H[i]+tn*H[m] and H[i]=tn*H[i]-un*H[m];
	bi_linear_combination_CA(un,H[i+rstart],tn,H[m-cstart+rstart],m,cend); // givens_linear_combination(un,H[i],tn,H[m],m); 
      }
    }
    if (!computeP)
      return;
    if (debug_infolevel)
      CERR << CLOCK() << " givens compute P" << endl;
    if (Pidn){
      // assumes P=idn at begin, transpose the product, reverse order
      bool done=false;
#ifdef HAVE_LIBPTHREAD
      int nthreads=threads_allowed?threads:1;
      if (nthreads>1){
	pthread_t tab[nthreads];
	thread_givens_p_t multdparam[nthreads];
	for (int j=0;j<nthreads;++j){
	  thread_givens_p_t tmp={&P,0,0,cstart,n,lastcol,&coeffs};
	  multdparam[j]=tmp;
	}
	int slicesize=cP/nthreads+1;
	int Pstart=0,Pend=0;
	for (int j=0;j<nthreads;++j){
	  Pstart=Pend;
	  Pend = Pstart+slicesize;
	  if (Pend>=cP) 
	    Pend=cP;
	  multdparam[j].Pstart=Pstart;
	  multdparam[j].Pend=Pend;
	  bool res=true;
	  if (j<nthreads-1)
	    res=pthread_create(&tab[j],(pthread_attr_t *) NULL,do_thread_qr_givens_p,(void *) &multdparam[j]);
	  if (res)
	    do_thread_qr_givens_p((void *)&multdparam[j]);
	}
	for (int j=0;j<nthreads;++j){
	  void * ptr=(void *)&nthreads; // non-zero initialisation
	  if (j<nthreads-1)
	    pthread_join(tab[j],&ptr);
	}
	done=true;
      } // end nthreads
#endif // PTHREAD
      if (!done){
	// slicing is slower
	int nslice=1; // nslice=cP/128+1;
	int slicesize=cP/nslice+1;
	int Pstart=0,Pend=0;
	for (;Pstart<cP;Pstart=Pend){
	  Pend = Pstart+slicesize;
	  if (Pend>=cP) 
	    Pend=cP;
	  qr_givens_p(P,Pstart,Pend,cstart,n,lastcol,coeffs);
	} // end slice
      }
      if (transpose)
	transpose_double(P);
    }
    else {
      int pos=0;
      for (int m=cstart;m<lastcol;++m){
	if (debug_infolevel>=5)
	  CERR << "// Givens reduction P line " << m << endl;
	int i=m;
	for (i=m+1;i<n;++i){
	  // line operation
	  tn=coeffs[pos];
	  ++pos;
	  un=coeffs[pos];
	  ++pos;
	  if (un==0)
	    continue;
	  if (i+1<n){
	    t=coeffs[pos];
	    u=coeffs[pos+1];
	    if (u!=0){
	      tri_linear_combination(un,P[i],tn,P[m-cstart],u,P[i+1],t,0,cP);
	      pos+=2;
	      ++i;
	      continue;
	    }
	  }
	  bi_linear_combination_CA(un,P[i],tn,P[m],0,cP); // givens_linear_combination(un,P[i],tn,P[m],0); 
	}
      }
      if (!transpose)
	transpose_double(P);
    }
    if (debug_infolevel)
      CERR << CLOCK() << " givens end" << endl;
  }

  // IMPROVE: don't do operations with 0
  void qr_rq(std_matrix<gen> & H,std_matrix<gen> & P,const gen & shift,int n,int & nitershift0,GIAC_CONTEXT){
    gen t,tn,tc,tabs,uabs,t2,u,un,uc,tmp1,tmp2,norme;
    int n_orig=int(H.size());
    vecteur v1,v2,TN(n_orig),UN(n_orig);
    if (is_zero(shift)){
      nitershift0++;
    }
    else{
      for (int i=0;i<n_orig;++i){
	H[i][i] -= shift;
      }
    }
    // H -> H-shift*identity
    for (int m=0;m<n-1;++m){
      // reduce coeff line m+1, col m
      t=H[m][m];
      // if (is_zero(t)) *logptr(contextptr) << "qr iteration: 0 on diagonal");
      int i=m+1;
      u=H[i][m];
      // now coeff at line m+1 column m is H[m+1][m]=t
      // creation of zeros in column m+1, lines i=m+2 and below
      // normalization of t and u such that t is real positive
      tabs=abs(t,contextptr);
      uabs=abs(u,contextptr);
      if (is_strictly_greater(uabs/tabs,1,contextptr))
	t2=uabs/u;
      else
	t2=tabs/t;
      t=t*t2; 
      u=u*t2;
      // compute unitary matrix coefficients
      tc=conj(t,contextptr);
      uc=conj(u,contextptr);
      norme=sqrt(re(u*uc+t*tc,contextptr),contextptr);
      un=u/norme; tn=t/norme; uc=conj(un,contextptr);	tc=conj(tn,contextptr); 
      // line operation
      // H[m]=uc*H[i]+tc*H[m] and H[i]=tn*H[i]-un*H[m];
      linear_combination(uc,H[i],tc,H[m],plus_one,1,v1,1e-12,0); 
      linear_combination(tn,H[i],-un,H[m],plus_one,1,v2,1e-12,0); 
      swap(H[m],v1);
      swap(H[i],v2);
      linear_combination(uc,P[i],tc,P[m],plus_one,1,v1,1e-12,0); 
      linear_combination(tn,P[i],-un,P[m],plus_one,1,v2,1e-12,0); 
      swap(P[m],v1);
      swap(P[i],v2);
      TN[m]=tn;
      UN[m]=un;
    } // end for m
    for (int m=0;m<n-1;++m){
      tn=TN[m];
      un=UN[m];
      tc=conj(tn,contextptr);
      uc=conj(un,contextptr);
      // column operation
      // int nmax=n_orig>m+3?m+3:n_orig;
      for (int j=0;j<n_orig;++j){
	vecteur & Hj=H[j];
	gen & Hjm=Hj[m];
	gen & Hjm1=Hj[m+1];
	tmp1=tn*Hjm+un*Hjm1;
	tmp2=-uc*Hjm+tc*Hjm1;
	Hjm=tmp1;
	Hjm1=tmp2;
      }
    }
    if (!is_zero(shift)){
      for (int i=0;i<n_orig;++i){
	H[i][i] += shift;
      }
    }
  }

  void re(std_matrix<gen> & H,int n,GIAC_CONTEXT){
    for (int i=0;i<n;i++){
      for (int j=0;j<n;j++){
	H[i][j]=re(H[i][j],contextptr);
      }
    }
  }

  bool convert(const vecteur & v,vector<giac_double> & v1,bool crunch){
    int n=int(v.size());
    v1.clear();
    v1.reserve(n);
    for (int i=0;i<n;++i){
      if (v[i].type==_INT_){
	v1.push_back(v[i].val);
	continue;
      }
      if (v[i].type==_FLOAT_){
	v1.push_back(get_double(v[i]._FLOAT_val));
	continue;
      }
      if (v[i].type==_ZINT){
	v1.push_back(mpz_get_d(*v[i]._ZINTptr));
	continue;
      }
#ifdef HAVE_LIBMPFR
      if (crunch && v[i].type==_REAL){
	v1.push_back(mpfr_get_d(v[i]._REALptr->inf,GMP_RNDN));
	continue;
      }
      if (crunch && v[i].type==_FRAC){
	gen g=accurate_evalf(v[i],60);
	if (g.type!=_REAL)
	  return false;
	v1.push_back(mpfr_get_d(g._REALptr->inf,GMP_RNDN));
	continue;
      }
#else
      if (crunch && v[i].type==_FRAC){
	gen g=evalf_double(v[i],1,context0);
	if (g.type!=_DOUBLE_)
	  return false;
	v1.push_back(g._DOUBLE_val);
	continue;
      }
#endif
      if (v[i].type!=_DOUBLE_)
	return false;
      v1.push_back(v[i]._DOUBLE_val);
    }
    return true;
  }

  bool convert(const vecteur & v,vector< complex_double > & v1,bool crunch){
    int n=int(v.size());
    v1.clear();
    v1.reserve(n);
    for (int i=0;i<n;++i){
      if (v[i].type==_INT_){
	v1.push_back(double(v[i].val));
	continue;
      }
      if (v[i].type==_FLOAT_){
	v1.push_back(get_double(v[i]._FLOAT_val));
	continue;
      }
      if (v[i].type==_CPLX){
	gen r=evalf_double(*v[i]._CPLXptr,1,context0);
	gen im=evalf_double(*(v[i]._CPLXptr+1),1,context0);
	if (r.type!=_DOUBLE_ || im.type!=_DOUBLE_)
	  return false;
	v1.push_back(complex_double(r._DOUBLE_val,im._DOUBLE_val));
	continue;
      }
      if (v[i].type!=_DOUBLE_)
	return false;
      v1.push_back(v[i]._DOUBLE_val);
    }
    return true;
  }

  bool std_matrix_gen2std_matrix_giac_double(const std_matrix<gen> & H,matrix_double & H1,bool crunch){
    int n=int(H.size());
    H1.resize(n);
    for (int i=0;i<n;++i){
      if (!convert(H[i],H1[i],crunch))
	return false;
    }
    return true;
  }

  bool std_matrix_gen2std_matrix_complex_double(const std_matrix<gen> & H,matrix_complex_double & H1,bool crunch){
    int n=int(H.size());
    H1.resize(n);
    for (int i=0;i<n;++i){
      if (!convert(H[i],H1[i],crunch))
	return false;
    }
    return true;
  }

  bool convert(const vector<giac_double> & v,vecteur & v1){
    int n=int(v.size());
#if 0
    v1.clear();
    v1.reserve(n);
    for (int i=0;i<n;++i){
      v1.push_back(double(v[i]));
    }
#else
    v1.resize(n);
    for (int i=0;i<n;++i){
      v1[i]=double(v[i]);
    }
#endif
    return true;
  }

  bool convert(const vector<complex_double> & v,vecteur & v1){
    int n=int(v.size());
    v1.resize(n);
    for (int i=0;i<n;++i){
      v1[i]=gen(v[i].real(),v[i].imag());
    }
    return true;
  }

  bool std_matrix_giac_double2std_matrix_gen(const matrix_double & H,std_matrix<gen> & H1){
    int n=int(H.size());
    H1.resize(n);
    for (int i=0;i<n;++i){
      if (!convert(H[i],H1[i]))
	return false;
    }
    return true;
  }

  bool std_matrix_complex_double2std_matrix_gen(const matrix_complex_double & H,std_matrix<gen> & H1){
    int n=int(H.size());
    H1.resize(n);
    for (int i=0;i<n;++i){
      if (!convert(H[i],H1[i]))
	return false;
    }
    return true;
  }

  void hessenberg_ortho(std_matrix<gen> & H,std_matrix<gen> & P,GIAC_CONTEXT){
    hessenberg_ortho(H,P,-1,-1,true,0,0.0,contextptr);
  }

  // v=(c1*v1+c2*v2), begin at cstart
  void linear_combination(const gen & c1,const vecteur & v1,const gen & c2,const vecteur & v2,vecteur & v,int cstart,double eps){
    eps=0;
    if (cstart<0)
      cstart=0;
    const_iterateur it1=v1.begin()+cstart,it1end=v1.end(),it2=v2.begin()+cstart;
    iterateur jt1=v.begin()+cstart;
#ifdef DEBUG_SUPPORT
    if (it1end-it1!=v2.end()-it2)
      setdimerr();
#endif
    if (it2==jt1){
      linear_combination(c2,v2,c1,v1,v,cstart,eps);
      return;
    }
    if (it1==jt1){
      for (;jt1!=it1end;++jt1,++it2){
	*jt1=trim(c1*(*jt1)+c2*(*it2),c1,eps);
      }
      return;
    }
    if (int(v.size())==it1end-it1){
      jt1=v.begin();
      for (int i=0;i<cstart;++i,++jt1)
	*jt1=0;
      for (;it1!=it1end;++it1,++it2,++jt1)
	*jt1=trim(c1*(*it1)+c2*(*it2),c1,eps);
      return;
    }
    v.clear();
    v.reserve(it1end-it1);
    for (int i=0;i<cstart;++i)
      v.push_back(0);
    for (;it1!=it1end;++it1,++it2)
      v.push_back(trim(c1*(*it1)+c2*(*it2),c1,eps));
  }

  matrice H0;

  void dbg_schur(const std_matrix<gen> & H,const std_matrix<gen> & P){
    matrice Hg,Pg;
    std_matrix_gen2matrice(H,Hg);
    std_matrix_gen2matrice(P,Pg);
    matrice res=mmult(mtran(Pg),Hg);
    res=mmult(res,Pg);
    gen t=subvecteur(res,H0);
    gen t1=_max(_abs(t,context0),context0);
    if (t1._DOUBLE_val>1e-5)
      CERR << "Error" << endl;
  }


  // Hessenberg reduction, P is orthogonal and should be initialized to identity
  // trn(P)*H*P=original
  // already_zero is either <=0 or an integer such that H[i][j]==0 if i>j+already_zero
  // (already_zero==1 if H is hessenberg, ==3 for Francis algorithm)
  void hessenberg_ortho(std_matrix<gen> & H,std_matrix<gen> & P,int firstrow,int n,bool compute_P,int already_zero,double eps,GIAC_CONTEXT){
    double eps_save(epsilon(contextptr));
    epsilon(eps,contextptr);
    int nH=int(H.size());
    if (n<0 || n>nH) 
      n=nH;
    if (firstrow<0 || firstrow>n)
      firstrow=0;
    gen t,tn,tc,tabs,u,un,uc,tmp1,tmp2,norme;
    vecteur v1(nH),v2(nH),TN(n,1),UN(n);
    for (int m=firstrow;m<n-2;++m){
      if (debug_infolevel>=5)
	CERR << "// hessenberg reduction line " << m << endl;
      // check for a non zero coeff in the column m below ligne m+1
      int i=m+1;
      gen pivot=0;
      int pivotline=0;
      int nend=n;
      if (already_zero && i+already_zero<n)
	nend=i+already_zero;
      for (;i<nend;++i){
	t=H[i][m];
	tabs=abs(t,contextptr);
	if (is_strictly_greater(tabs,pivot,contextptr)){
	  pivotline=i;
	  pivot=tabs;
	}
      }
      if (is_zero(pivot,contextptr)) //not found
	continue;
      i=pivotline;
      // exchange line and columns
      if (i>m+1){
	swap(H[i],H[m+1]);
	if (compute_P)
	  swap(P[i],P[m+1]);
	for (int j=0;j<n;++j){
	  vecteur & Hj=H[j];
	  swapgen(Hj[i],Hj[m+1]);
	}
      }
      // now coeff at line m+1 column m is H[m+1][m]=t!=0
      // creation of zeros in column m+1, lines i=m+2 and below
      // if (firstrow==100) dbg_schur(H,P);
      int nprime=n;
      for (i=m+2;i<nend;++i){
	// line operation
	t=H[m+1][m];
	u=H[i][m];
	// CERR << t << " " << u << endl;
	uc=conj(u,contextptr);
	tc=conj(t,contextptr);
	norme=sqrt(u*uc+t*tc,contextptr);
	un=u/norme; tn=t/norme; uc=conj(un,contextptr);	tc=conj(tn,contextptr); 
	if (is_zero(un,contextptr)){
	  UN[i]=0;
	  continue;
	}
	if (debug_infolevel>=3)
	  CERR << "// i=" << i << " " << u <<endl;
	// H[m+1]=tc*H[m+1]+uc*H[i] and H[i]=tn*H[i]-un*H[m+1];
	linear_combination(uc,H[i],tc,H[m+1],v1,0,0.0); 
	linear_combination(tn,H[i],-un,H[m+1],v2,0,0.0); 
	swap(H[m+1],v1);
	swap(H[i],v2);
	if (compute_P){
	  linear_combination(uc,P[i],tc,P[m+1],v1,0,0.0); 
	  linear_combination(tn,P[i],-un,P[m+1],v2,0,0.0); 
	  swap(P[m+1],v1);
	  swap(P[i],v2);
	}
	TN[i]=tn;
	UN[i]=un;
      }
      for (i=m+2;i<nprime;++i){ 
	un=UN[i];
	if (is_zero(un,contextptr))
	  continue;
	tn=TN[i];
	tc=conj(tn,contextptr);
	uc=conj(un,contextptr);
	// column operation
	for (int j=0;j<nH;++j){
	  vecteur & Hj=H[j];
	  tmp1=tn*Hj[m+1]+un*Hj[i];
	  tmp2=-uc*Hj[m+1]+tc*Hj[i];
	  Hj[m+1]=tmp1;
	  Hj[i]=tmp2;
	}
      }
      // if (firstrow==100) dbg_schur(H,P);
    }
    // make 0 below subdiagonal (i<nH all matrix, i<n only relevant lines/column)
    for (int i=2;i<n;i++){
      iterateur it=H[i].begin(),itend=it+i-1; // or min(i-1,n);
      for (;it!=itend;++it){
	if (debug_infolevel>2 && abs(*it,contextptr)>1e-10)
	  CERR << "Precision " << i << " " << *it << endl;
	*it=0;
      }
    }
    epsilon(eps_save,contextptr);
  }

#ifdef HAVE_LIBMPFR
  gen tri_linear_combination(const gen & c1,const gen & x1,const gen & c2,const gen & x2,const gen & c3,const gen & x3,mpfr_t & tmp1,mpfr_t & tmp2){
    if (c1.type!=_REAL || x1.type!=_REAL || c2.type!=_REAL ||x2.type!=_REAL ||  c3.type!=_REAL || x3.type!=_REAL)
      return c1*x1+c2*x2+c3*x3;
    mpfr_set_prec(tmp1,mpfr_get_prec(c1._REALptr->inf));
    mpfr_set_prec(tmp2,mpfr_get_prec(c1._REALptr->inf));
    mpfr_mul(tmp1,c1._REALptr->inf,x1._REALptr->inf,GMP_RNDD);
    mpfr_mul(tmp2,c2._REALptr->inf,x2._REALptr->inf,GMP_RNDD);
    mpfr_add(tmp1,tmp1,tmp2,GMP_RNDD);
    mpfr_mul(tmp2,c3._REALptr->inf,x3._REALptr->inf,GMP_RNDD);
    mpfr_add(tmp1,tmp1,tmp2,GMP_RNDD);
    return real_object(tmp1);
  }
#endif

  void tri_linear_combination(const gen & c1,const vecteur & x1,const gen & c2,const vecteur & x2,const gen & c3,const vecteur & x3,vecteur & y){
    const_iterateur it1=x1.begin(),it2=x2.begin(),it3=x3.begin(),it3end=x3.end();
    iterateur jt=y.begin();
#ifdef HAVE_LIBMPFR // not significantly faster...
    if (c1.type==_REAL && c2.type==_REAL && c3.type==_REAL){
      mpfr_t tmp1,tmp2;
      mpfr_init2(tmp1,mpfr_get_prec(c1._REALptr->inf));
      mpfr_init2(tmp2,mpfr_get_prec(c1._REALptr->inf));
      for (;it3!=it3end;++jt,++it1,++it2,++it3){
	if (it1->type==_REAL && it2->type==_REAL && it3->type==_REAL){
	  mpfr_mul(tmp1,c1._REALptr->inf,it1->_REALptr->inf,GMP_RNDD);
	  mpfr_mul(tmp2,c2._REALptr->inf,it2->_REALptr->inf,GMP_RNDD);
	  mpfr_add(tmp1,tmp1,tmp2,GMP_RNDD);
	  mpfr_mul(tmp2,c3._REALptr->inf,it3->_REALptr->inf,GMP_RNDD);
	  mpfr_add(tmp1,tmp1,tmp2,GMP_RNDD);
	  *jt=real_object(tmp1);
	}
	else
	  *jt=c1*(*it1)+c2*(*it2)+c3*(*it3);
      }
      mpfr_clear(tmp1);
      mpfr_clear(tmp2);
      return;
    }
#endif
    for (;it3!=it3end;++jt,++it1,++it2,++it3){
      *jt=c1*(*it1)+c2*(*it2)+c3*(*it3);
    }
  }

  void francis_schur_iterate(std_matrix<gen> & H,double eps,const gen & l1,int n_orig,int n1,int n2,std_matrix<gen> & P,bool compute_P,GIAC_CONTEXT){
    // compute (H-l1) on n1-th basis vector
    gen x=H[n1][n1]-l1,y=H[n1+1][n1];
    // make x real
    gen xr,xi,yr,yi;
    reim(x,xr,xi,contextptr);
    reim(y,yr,yi,contextptr);
    x = sqrt(xr*xr+xi*xi,contextptr);
    if (x==0) return;
    // gen xy = gen(xr/x,-xi/x); y=y*xy;
    y = gen((yr*xr+yi*xi)/x,(yi*xr-yr*xi)/x); 
    reim(y,yr,yi,contextptr);
    gen xy=sqrt(x*x+yr*yr+yi*yi,contextptr);
    // normalize eigenvector
    x = x/xy; y = y/xy;	
    // compute reflection matrix such that Q*[1,0]=[x,y]
    // hence column 1 is [x,y] and column2 is [conj(y),-x]
    // apply Q on H and P: line operations on H and P
    gen c11=x, c12=conj(y,contextptr),
      c21=y, c22=-x,tmp1,tmp2;
    vecteur v1(n_orig),v2(n_orig);
    linear_combination(c11,H[n1],c12,H[n1+1],v1,0,0.0);
    linear_combination(c21,H[n1],c22,H[n1+1],v2,0,0.0);
    swap(H[n1],v1);
    swap(H[n1+1],v2);
    if (compute_P){
      linear_combination(c11,P[n1],c12,P[n1+1],v1,0,0.0);
      linear_combination(c21,P[n1],c22,P[n1+1],v2,0,0.0);
      swap(P[n1],v1);
      swap(P[n1+1],v2);
    }
    // now columns operations on H (not on P)
    for (int j=0;j<n_orig;++j){
      vecteur & Hj=H[j];
      gen & Hjm1=Hj[n1];
      gen & Hjm2=Hj[n1+1];
      tmp1=Hjm1*c11+Hjm2*c21;
      tmp2=Hjm1*c12+Hjm2*c22;
      Hjm1=tmp1;
      Hjm2=tmp2;
    }
  }

  void francis_schur_iterate_real(std_matrix<gen> & H,int n_orig,int n1,int n2,std_matrix<gen> & P,bool compute_P,GIAC_CONTEXT){
    vecteur v1(n_orig),v2(n_orig),v3(n_orig);
    gen tmp1,tmp2,tmp3;
    gen s,p; // s=l1+l2, p=l1*l2
    s=H[n2-2][n2-2]+H[n2-1][n2-1];
    p=H[n2-2][n2-2]*H[n2-1][n2-1]-H[n2-1][n2-2]*H[n2-2][n2-1];
    // compute (H-l2)(H-l1)=(H-s)*H+p on n1-th basis vector (if n1==0, on [1,0,...,0])
    gen ha=H[n1][n1],hb=H[n1][n1+1],
      hd=H[n1+1][n1],he=H[n1+1][n1+1],
      hh=H[n1+2][n1+1];
    gen x=hb*hd+ha*(ha-s)+p,y=hd*(he-s+ha),z=hd*hh;
    // normalize, substract [1,0,0] and normalize again
    gen xyz=sqrt(x*conj(x,contextptr)+y*conj(y,contextptr)+z*conj(z,contextptr),contextptr);
    // if x/xyz is near 1, improve precision:
    // x/xyz-1 = ((x/xyz)^2-1)/(x/xyz+1)=-((y/xyz)^2+(z/xyz)^2)/(x/xyz+1)
    x=x/xyz; y=y/xyz; z=z/xyz;
    if (fabs(evalf_double(re(x,contextptr)-1,1,contextptr)._DOUBLE_val)<0.5)
      x=-(y*y+z*z)/(x+1);
    else
      x-=1;
    xyz=sqrt(x*conj(x,contextptr)+y*conj(y,contextptr)+z*conj(z,contextptr),contextptr);
    x=x/xyz; y=y/xyz; z=z/xyz;
    // compute reflection matrix let n=[[x],[y],[z]] trn(n)=conj([[x,y,z]])
    // Q=idn(3)-2*n*trn(n);
    // i.e. [[ 1-2x*conj(x), -2x*conj(y),   -2x*conj(z)  ],
    //       [ -2*y*conj(x), 1-2*y*conj(y), -2*y*conj(z) ],
    //       [ -2*z*conj(x), -2*z*conj(y),  1-2*z*conj(z)]]
    // apply Q on H and P: line operations on H and P
    gen c11=1-2*x*conj(x,contextptr),c12=-2*x*conj(y,contextptr),c13=-2*x*conj(z,contextptr);
    gen c21=-2*y*conj(x,contextptr),c22=1-2*y*conj(y,contextptr),c23=-2*y*conj(z,contextptr);
    gen c31=-2*z*conj(x,contextptr),c32=-2*z*conj(y,contextptr),c33=1-2*z*conj(z,contextptr);
    // CERR << "[[" << c11 <<"," << c12 << "," << c13 << "],[" <<  c21 <<"," << c22 << "," << c23 << "],[" << c31 <<"," << c32 << "," << c33 << "]]" << endl;
    tri_linear_combination(c11,H[n1],c12,H[n1+1],c13,H[n1+2],v1);
    tri_linear_combination(c21,H[n1],c22,H[n1+1],c23,H[n1+2],v2);
    tri_linear_combination(c31,H[n1],c32,H[n1+1],c33,H[n1+2],v3);
    swap(H[n1],v1);
    swap(H[n1+1],v2);
    swap(H[n1+2],v3);
#ifdef HAVE_LIBMPFR
    mpfr_t tmpf1,tmpf2; mpfr_init(tmpf1); mpfr_init(tmpf2);
#endif
    // now columns operations on H (not on P)
    for (int j=0;j<n_orig;++j){
      vecteur & Hj=H[j];
      gen & Hjm1=Hj[n1];
      gen & Hjm2=Hj[n1+1];
      gen & Hjm3=Hj[n1+2];
#ifdef HAVE_LIBMPFR
      tmp1=tri_linear_combination(Hjm1,c11,Hjm2,c21,Hjm3,c31,tmpf1,tmpf2);
      tmp2=tri_linear_combination(Hjm1,c12,Hjm2,c22,Hjm3,c32,tmpf1,tmpf2);
      tmp3=tri_linear_combination(Hjm1,c13,Hjm2,c23,Hjm3,c33,tmpf1,tmpf2);
#else
      tmp1=Hjm1*c11+Hjm2*c21+Hjm3*c31;
      tmp2=Hjm1*c12+Hjm2*c22+Hjm3*c32;
      tmp3=Hjm1*c13+Hjm2*c23+Hjm3*c33;
#endif
      Hjm1=tmp1;
      Hjm2=tmp2;
      Hjm3=tmp3;
    }
#ifdef HAVE_LIBMPFR
    mpfr_clear(tmpf1); mpfr_clear(tmpf2);
#endif
    // CERR << H << endl;
    if (compute_P){
      tri_linear_combination(c11,P[n1],c12,P[n1+1],c13,P[n1+2],v1);
      tri_linear_combination(c21,P[n1],c22,P[n1+1],c23,P[n1+2],v2);
      tri_linear_combination(c31,P[n1],c32,P[n1+1],c33,P[n1+2],v3);
      swap(P[n1],v1);
      swap(P[n1+1],v2);
      swap(P[n1+2],v3);
    }
  }
  
  // Francis algorithm on submatrix rows and columns n1..n2-1
  // Invariant: trn(P)*H*P=orig matrix
  bool francis_schur(std_matrix<gen> & H,int n1,int n2,std_matrix<gen> & P,int maxiter,double eps,bool is_hessenberg,bool complex_schur,bool compute_P,bool no_lapack,GIAC_CONTEXT){
    vecteur eigenv;
    if (n1==0 && eps>1e-15 && !no_lapack && lapack_schur(H,P,compute_P,eigenv,contextptr))
      return true;
    int n_orig=int(H.size());//,nitershift0=0;
    if (!is_hessenberg){
      std_matrix_gen2matrice(H,H0);
      hessenberg_ortho(H,P,0,n_orig,compute_P,0,0.0,contextptr); // insure Hessenberg form (on the whole matrix)
    }
    if (n2-n1<=1)
      return true; // nothing to do
    if (n2-n1==2){ // 2x2 submatrix, we know how to diagonalize
      gen l1,l2;
      if (eigenval2(H,n2,l1,l2,contextptr) || complex_schur){
	// choose l1 or l2 depending on H[n1][n1]-l1, H[n1][n1+1]
	if (is_greater(abs(H[n1][n1]-l1,contextptr),abs(H[n1][n1]-l2,contextptr),contextptr))
	  francis_schur_iterate(H,eps,l1,n_orig,n1,n2,P,compute_P,contextptr);
	else
	  francis_schur_iterate(H,eps,l2,n_orig,n1,n2,P,compute_P,contextptr);
      }
      return true;
    }
    for (int niter=0;n2-n1>2 && niter<maxiter;niter++){
      // make 0 below subdiagonal
      for (int i=2;i<n_orig;i++){
	vecteur & Hi=H[i];
	for (int j=0;j<i-1;j++){
	  Hi[j]=0;
	}
      }
      if (debug_infolevel>=2)
	CERR << CLOCK() << " qr iteration number " << niter << " " << endl;
      if (debug_infolevel>=5)
	H.dbgprint();
      // check if one subdiagonal element is sufficiently small, if so 
      // we can increase n1 or decrease n2 or split
      for (int i=n1;i<n2-1;++i){
	gen ratio=abs(H[i+1][i]/H[i][i],contextptr);
	ratio=evalf_double(ratio,1,contextptr);
	if (ratio.type==_DOUBLE_ && fabs(ratio._DOUBLE_val)<eps){
	  if (debug_infolevel>2)
	    CERR << "Francis split " << n1 << " " << i+1 << " " << n2 << endl;
	  // submatrices n1..i and i+1..n2-1
	  if (!francis_schur(H,n1,i+1,P,maxiter,eps,true,complex_schur,compute_P,true,contextptr))
	    return false;
	  return francis_schur(H,i+1,n2,P,maxiter,eps,true,complex_schur,compute_P,true,contextptr);
	}
      }
      // now H is proper hessenberg (indices n1 to n2-1)
      // find eigenvalues l1 and l2 of last 2x2 matrix, they will be taken as shfits
      // FIXME for complex matrices, direct reflection with eigenvector of l1 or l2
      if (complex_schur){
	gen l1,l2;
	l1=H[n2-1][n2-1];
	if (n2-n1>=2){
	  // take the closest eigenvalue of the last 2*2 block 
	  eigenval2(H,n2,l1,l2,contextptr);
	  if (is_greater(abs(l1-H[n2-1][n2-1],contextptr),abs(l2-H[n2-1][n2-1],contextptr),contextptr))
	    l1=l2;
	}
	//  FIXME? if H[n1][n1]-l1 is almost zero and H[n1][n1+1] also -> precision problem
	francis_schur_iterate(H,eps,l1,n_orig,n1,n2,P,compute_P,contextptr);
      }
      else
	francis_schur_iterate_real(H,n_orig,n1,n2,P,compute_P,contextptr);
      if (n1==100)
	dbg_schur(H,P);
      // CERR << H << endl;
      // chase the bulge: Hessenberg reduction on 2 subdiagonals
      hessenberg_ortho(H,P,n1,n2,compute_P,3,0.0,contextptr); // <- improve
    } // end for loop on niter
    return false;
  }

  // trn(P)*H*P=orig matrix
  void hessenberg_schur(std_matrix<gen> & H,std_matrix<gen> & P,int maxiter,double eps,GIAC_CONTEXT){
    int n_orig=int(H.size()),n=n_orig,nitershift0=0;
    bool real=true,is_double=true; 
    for (int i=0;real && i<n;i++){
      vecteur &Hi=H[i];
      for (int j=0;j<n;j++){
	gen Hij=Hi[j];
	if (is_double){
	  if (Hij.type==_DOUBLE_ || Hij.type==_FLOAT_)
	    continue;
	  if (Hij.type==_CPLX && 
	      (Hij._CPLXptr->type==_DOUBLE_ || Hij._CPLXptr->type==_FLOAT_)
	      && ((Hij._CPLXptr+1)->type==_DOUBLE_ || (Hij._CPLXptr+1)->type==_FLOAT_)
	      )
	    ;
	  else
	    is_double=false;
	}
	if (!is_zero(im(Hij,contextptr))){
	  real=false;
	  if (!is_double)
	    break;
	}
      }
    }
    if (is_double){
      if (real){
	matrix_double H1,P1;
	std_matrix_gen2std_matrix_giac_double(H,H1,true);
	std_matrix_gen2std_matrix_giac_double(P,P1,true);
	francis_schur(H1,0,n_orig,P1,maxiter,eps,false,true);
	std_matrix_giac_double2std_matrix_gen(P1,P);
	std_matrix_giac_double2std_matrix_gen(H1,H);
      }
      else {
	matrix_complex_double H1,P1;
	std_matrix_gen2std_matrix_complex_double(H,H1,true);
	std_matrix_gen2std_matrix_complex_double(P,P1,true);
	francis_schur(H1,0,n_orig,P1,maxiter,eps,false,true);
	std_matrix_complex_double2std_matrix_gen(P1,P);
	std_matrix_complex_double2std_matrix_gen(H1,H);
      }
      return;
    }
    else {
      if (francis_schur(H,0,n_orig,P,maxiter,std::sqrt(double(n_orig))*eps,false,!real,true,true,contextptr)){
	return ;
      }
    }
    hessenberg_ortho(H,P,contextptr); // insure
    // make 0 below subdiagonal
    for (int i=2;i<n_orig;i++){
      for (int j=0;j<i-1;j++){
	H[i][j]=0;
      }
    }
    gen shift=0,ratio,oldratio=0,maxi,absmaxi,tmp,abstmp;
    vecteur SHIFT;
    for (int niter=0;n>1 && niter<maxiter;niter++){
      if (debug_infolevel>=2)
	CERR << CLOCK() << " qr iteration number " << niter << endl;
      shift=0;
      gen test=abs(H[n-1][n-2],contextptr);
      ratio=test/abs(H[n-1][n-1],contextptr);
      bool Small=is_strictly_greater(0.01,ratio,contextptr);
      if (Small)
	shift=H[n-1][n-1];
      else {
	if (n==2 || is_strictly_greater(0.01,(ratio=abs(H[n-2][n-3]/H[n-2][n-2],contextptr)),contextptr)){
	  // define shift according to the smallest eigenvalues 
	  // of the last 2x2 submatrix bloc
	  gen a=H[n-2][n-2],b=H[n-2][n-1],c=H[n-1][n-2],d=H[n-1][n-1];
	  gen delta=a*a-2*a*d+d*d+4*b*c;
	  if (real && n==2 && is_strictly_positive(-delta,0))
	    break;
	  delta=sqrt(delta,contextptr);
	  gen l1=(a+d+delta)/2,l2=(a+d-delta)/2;
	  if (is_strictly_greater(abs(l1,contextptr),abs(l2,contextptr),contextptr))
	    shift=l2;
	  else
	    shift=l1;
	}
	else {
	  if (niter>=maxiter/4)
	    shift=H[n-1][n-1]/2;
	}
      }
      oldratio=ratio;
      qr_rq(H,P,shift,n,nitershift0,contextptr);
      if (real && !is_zero(im(shift,contextptr)))
	SHIFT.push_back(shift);
      test=abs(H[n-1][n-2],contextptr);
      ratio=test/abs(H[n-1][n-1],contextptr);
      if (is_strictly_greater(gen(eps)/oldratio,1,contextptr) && is_strictly_greater(gen(eps)/ratio,1,contextptr)){
	// eigenvalue has been found
	niter=0;
	oldratio=0;
	n--;
	if (real && !SHIFT.empty()){
	  // int ni=SHIFT.size();
	  for(int i=0;i<(int)SHIFT.size();++i)
	    qr_rq(H,P,conj(SHIFT[i],contextptr),n,nitershift0,contextptr);
	  for (int i=0;i<n-1;i++){
	    vecteur & Pi=P[i];
	    maxi=Pi.front();
	    absmaxi=abs(maxi,contextptr);
	    for (int j=1;j<n-1;j++){
	      tmp=Pi[j];
	      abstmp=abs(tmp,contextptr);
	      if (abstmp>absmaxi){
		absmaxi=abstmp;
		maxi=tmp;
	      }
	    }
	    tmp=absmaxi/maxi;
	    multvecteur(tmp,Pi,Pi);
	    multvecteur(tmp,H[i],H[i]);
	    tmp=maxi/absmaxi;
	    for (int j=0;j<n_orig;j++){
	      gen & Hji= H[j][i];
	      Hji = tmp*Hji;
	    }
	  }
	  re(H,n-1,contextptr); re(P,n-1,contextptr);
	  n--;
	  SHIFT.clear();
	} // end if (real)
      } // end eigenvalue detected
    } // end loop on n for 0 subdiagonal elements
  }

  // Reduction to Hessenberg form, see e.g. Cohen algorithm 2.2.9
  // (with C array indices)
  // general case
  // if modulo==-1 Schur reduction up to precision eps and maxiterations maxiter
  // if modulo<-1, using orthogonal/unitary matrices
  bool mhessenberg(const matrice & M,matrice & h,matrice & p,int modulo,int maxiter,double eps,GIAC_CONTEXT){
    int n=int(M.size());
    if (!n || n!=mcols(M))
      return false; // setdimerr();
    bool modularize=!modulo && M[0][0].type==_MOD && (M[0][0]._MODptr+1)->type==_INT_;
    if (modularize)
      modulo=(M[0][0]._MODptr+1)->val;
    if (modulo>0){
      vector< vector<int> > H;
      if (!vecteur2vectvector_int(M,modulo,H))
	return false;
      vector< vector<int> > P;
      if (!vecteur2vectvector_int(midn(n),modulo,P))
	return false;
      mhessenberg(H,P,modulo,true);
      vectvector_int2vecteur(H,h);
      vectvector_int2vecteur(P,p);
      if (modularize){
	h=*makemod(h,modulo)._VECTptr;
	p=*makemod(p,modulo)._VECTptr;
      }
      return true;
    }
    std_matrix<gen> H,P(n,vecteur(n));
    for (int i=0;i<n;++i)
      P[i][i]=1;
    if (modulo<0){
#ifdef HAVE_LIBMPFR
      matrice2std_matrix_gen(*evalf(gen(M),1,contextptr)._VECTptr,H);
#else
      matrice2std_matrix_gen(*evalf_double(gen(M),1,contextptr)._VECTptr,H);
#endif
    }
    else
      matrice2std_matrix_gen(M,H);
    if (modulo==-1)
      hessenberg_schur(H,P,maxiter,eps,contextptr);
    else {
      if (modulo<0)
	hessenberg_ortho(H,P,contextptr);
      else
	hessenberg(H,P,contextptr);
    }
    // store result
    std_matrix_gen2matrice_destroy(H,h);
    std_matrix_gen2matrice_destroy(P,p);
    return true;
  }
  gen _hessenberg(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g(g0);
    int modulo=0;
    double eps=epsilon(contextptr);
    int maxiter=500;
    if (g.type==_VECT && g._VECTptr->size()>=2 && g.subtype==_SEQ__VECT){
      vecteur & v = *g._VECTptr;
      gen v1=v[1];
      if (v1.type==_INT_)
	modulo=v1.val;
      else {
	v1=evalf_double(v1,1,contextptr);
	if (v1.type==_DOUBLE_){
	  modulo=-1;
	  eps=v1._DOUBLE_val;
	  if (v.size()>2 && v[2].type==_INT_)
	    maxiter=v[2].val;
	}
      }
      g=v.front();
    }
    if (!is_squarematrix(g))
      return symbolic(at_hessenberg,g);
    matrice m(*g._VECTptr),h,p;
    if (!mhessenberg(m,h,p,modulo,maxiter,eps,contextptr))
      return gensizeerr(contextptr);
    if (modulo<0)
      return makesequence(_trn(p,contextptr),h); // p,h such that p*h*p^-1=orig
    else
      return makesequence(inv(p,contextptr),h); // p,h such that p*h*p^-1=orig
  }
  static const char _hessenberg_s []="hessenberg";
  static define_unary_function_eval (__hessenberg,&giac::_hessenberg,_hessenberg_s);
  define_unary_function_ptr5( at_hessenberg ,alias_at_hessenberg,&__hessenberg,0,true);

  int trace(const vector< vector<int> > & N,int modulo){
    longlong res=0;
    int n=int(N.size());
    for (int i=0;i<n;++i){
      res += N[i][i];
    }
    return res%modulo;
  }

  // Danilevsky algorithm
  // kind of row reduction to companion matrix
  // returns charpoly or minpoly 
  void mod_pcar(std_matrix<gen> & N,vecteur & res,bool compute_pmin){
    int n=int(N.size());
    if (n==1){
      res.resize(2);
      res[0]=1;
      res[1]=-N[0][0];
    }
    vecteur v(n),w(n);
    for (int k=0;k<n-1;++k){
      // search "pivot" on line k
      for (int j=k+1;j<n;++j){
	if (!is_zero(N[k][j])){
	  // swap columns and lines k+1 and j
	  if (j>k+1){
	    for (int i=k;i<n;++i)
	      swapgen(N[i][k+1],N[i][j]);
	    N[k+1].swap(N[j]);
	  }
	  break;
	}
      }
      v=N[k];
      gen akk1=v[k+1];
      if (is_zero(akk1)){
	// degenerate case, split N in two parts
	vecteur part1(k+2),part2;
	part1[0]=1;
	for (int i=0;i<=k;++i){
	  part1[i+1]=-N[k][k-i];
	}
	std_matrix<gen> N2(n-1-k);
	for (int i=k+1;i<n;++i)
	  N2[i-1-k]=vecteur(N[i].begin()+k+1,N[i].end());
	mod_pcar(N2,part2,compute_pmin);
	if (compute_pmin && part1==part2){
	  res.swap(part1);
	  return;
	}
	if (compute_pmin)
	  res=lcm(part1,part2,0);
	else
	  res=operator_times(part1,part2,0);
	return;
      }
      // multiply right by identity with line k+1 replaced by 
      // -N[k]/a_{k,k+1} except on diagonal 1/a_{k,k+1} 
      // this will replace line k by 0...010...0 (1 at column k+1)
      gen invakk1=inv(akk1,context0);
      for (int i=0;i<n;++i)
	w[i]=(-invakk1*v[i]);
      w[k+1]=invakk1;
      // column operations
      for (int l=k;l<n;++l){
	vecteur & Nl=N[l];
	gen Nlk1=Nl[k+1];
	for (int j=0;j<=k;++j){
	  gen & Nlj=Nl[j];
	  Nlj=Nlj+w[j]*Nlk1;
	}
	Nl[k+1]=invakk1*Nlk1;
	for (int j=k+2;j<n;++j){
	  gen & Nlj=Nl[j];
	  Nlj=Nlj+w[j]*Nlk1;
	}
      }
      // multiply left by identity with line k+1 replaced by original N[k]
      // line operations L_{k+1}=sum a_{k,i} L_i
      for (int j=0;j<n;++j){
	gen coeff(0);
	if (j>=1 && j<=k+1)
	  coeff=v[j-1];
	for (int i=k+1;i<n;++i){
	  coeff += v[i]*N[i][j];
	}
	N[k+1][j]=coeff;
      }
    }
    // get charpoly
    res.resize(n+1);
    res[0]=1;
    for (int i=0;i<n;++i)
      res[1+i]=-N[n-1][n-1-i];
  }

  // Danilevsky algorithm
  // kind of row reduction to companion matrix
  // returns charpoly or minpoly 
  void mod_pcar(vector< vector<int> > & N,int modulo,vector<int> & res,bool compute_pmin){
    int n=int(N.size());
    if (n==1){
      res.resize(2);
      res[0]=1;
      res[1]=-N[0][0];
    }
    bool pseudo=false;
#ifdef PSEUDO_MOD
    pseudo=(modulo<(1<<29)) && (2*modulo*double(modulo)*n<(1ULL<<63));
    int nbits=sizeinbase2(modulo); 
    unsigned invp=((1ULL<<(2*nbits)))/modulo+1;
#endif
    vector<int> v(n),w(n);
    for (int k=0;k<n-1;++k){
      // search "pivot" on line k
      for (int j=k+1;j<n;++j){
	if (N[k][j]){
	  // swap columns and lines k+1 and j
	  if (j>k+1){
	    for (int i=k;i<n;++i)
	      swapint(N[i][k+1],N[i][j]);
	    N[k+1].swap(N[j]);
	  }
	  break;
	}
      }
      v=N[k];
      int akk1=v[k+1];
      if (akk1==0){
	// degenerate case, split N in two parts
	vector<int> part1(k+2),part2;
	part1[0]=1;
	for (int i=0;i<=k;++i){
	  part1[i+1]=smod(-N[k][k-i],modulo);
	}
	vector< vector<int> > N2(n-1-k);
	for (int i=k+1;i<n;++i)
	  N2[i-1-k]=vector<int>(N[i].begin()+k+1,N[i].end());
	mod_pcar(N2,modulo,part2,compute_pmin);
	if (compute_pmin && part1==part2){
	  res.swap(part1);
	  return;
	}
	if (compute_pmin){
	  modpoly p1,p2,p12;
	  vector_int2vecteur(part1,p1);
	  vector_int2vecteur(part2,p2);
	  environment env;
	  env.modulo=modulo; env.moduloon=true;
	  p12=lcm(p1,p2,&env);
	  vecteur2vector_int(p12,modulo,res);
	}
	else
	  mulsmall(part1.begin(),part1.end(),part2.begin(),part2.end(),modulo,res);
	return;
      }
      // multiply right by identity with line k+1 replaced by 
      // -N[k]/a_{k,k+1} except on diagonal 1/a_{k,k+1} 
      // this will replace line k by 0...010...0 (1 at column k+1)
      longlong invakk1=invmod(akk1,modulo);
      for (int i=0;i<n;++i)
	w[i]=(-invakk1*v[i])%modulo;
      w[k+1]=int(invakk1);
      if (debug_infolevel)
	CERR << CLOCK()*1e-6 << " column" << k << endl;
      // column operations
#if 1
      for (int l=k;l<n;++l){
	int * Nlj=&N[l].front();
	int * wj=&w.front();
	int * wend=wj+k;
	longlong Nlk1=Nlj[k+1];
	if (pseudo){
#ifdef PSEUDO_MOD
	  for (;wj<=wend;++wj,++Nlj){
	    *Nlj=pseudo_mod(*Nlj+(*wj)*Nlk1,modulo,invp,nbits);
	  }
#endif
	}
	else {
	  for (;wj<=wend;++wj,++Nlj){
	    *Nlj=(*Nlj+(*wj)*Nlk1)%modulo;
	  }
	}
	*Nlj=(invakk1*Nlk1)%modulo;
	++wj;++Nlj;
	wend += (n-k);
	if (pseudo){
#ifdef PSEUDO_MOD
	  for (;wj<wend;++wj,++Nlj){
	    *Nlj=pseudo_mod(*Nlj+(*wj)*Nlk1,modulo,invp,nbits);
	  }
#endif
	}
	else {
	  for (;wj<wend;++wj,++Nlj){
	    *Nlj=(*Nlj+(*wj)*Nlk1)%modulo;
	  }
	}
      }
#else
      for (int l=k;l<n;++l){
	vector<int> & Nl=N[l];
	longlong Nlk1=Nl[k+1];
	for (int j=0;j<=k;++j){
	  int & Nlj=Nl[j];
	  Nlj=(Nlj+w[j]*Nlk1)%modulo;
	}
	Nl[k+1]=(invakk1*Nlk1)%modulo;
	for (int j=k+2;j<n;++j){
	  int & Nlj=Nl[j];
	  Nlj=(Nlj+w[j]*Nlk1)%modulo;
	}
      }
#endif
      if (debug_infolevel)
	CERR << CLOCK()*1e-6 << " line" << k << endl;
#if 1
      // multiply left by identity with line k+1 replaced by original N[k]
      // line operations L_{k+1}=sum a_{k,i} L_i
      int j=0;
      for (;j<=n-4;j+=4){
	longlong coeff0=0,coeff1=0,coeff2=0,coeff3=0;
	if (j>=1 && j-1<=k)
	  coeff0=v[j-1];
	if (j<=k)
	  coeff1=v[j];
	if (j+1<=k)
	  coeff2=v[j+1];
	if (j+2<=k)
	  coeff3=v[j+2];
	vector<int> * Ni=&N[k+1];
	int * vi=&v[k+1], * viend=vi+(n-k-1);
	// NOTE: should take % for large modulo
	for (;vi<viend;++vi,++Ni){
	  longlong V=*vi;
	  int * Nij=&(*Ni)[j];
	  coeff0 += V*Nij[0];
	  coeff1 += V*Nij[1];
	  coeff2 += V*Nij[2];
	  coeff3 += V*Nij[3];
	}
	N[k+1][j]=coeff0%modulo;
	N[k+1][j+1]=coeff1%modulo;
	N[k+1][j+2]=coeff2%modulo;
	N[k+1][j+3]=coeff3%modulo;
      }
      for (;j<n;++j){
	longlong coeff=0;
	if (j>=1 && j<=k+1)
	  coeff=v[j-1];
	vector<int> * Ni=&N[k+1];
	int * vi=&v[k+1], * viend=vi+(n-k-1);
	for (;vi<viend;++vi,++Ni){
	  coeff += longlong(*vi)*(*Ni)[j];
	}
	N[k+1][j]=coeff%modulo;
      }
#else
      // multiply left by identity with line k+1 replaced by original N[k]
      // line operations L_{k+1}=sum a_{k,i} L_i
      for (int j=0;j<n;++j){
	longlong coeff=0;
	if (j>=1 && j<=k+1)
	  coeff=v[j-1];
	for (int i=k+1;i<n;++i){
	  coeff += longlong(v[i])*N[i][j];
	}
	N[k+1][j]=coeff%modulo;
      }
#endif
    }
    // get charpoly
    res.resize(n+1);
    res[0]=1;
    for (int i=0;i<n;++i)
      res[1+i]=smod(-N[n-1][n-1-i],modulo);
  }

  bool mod_pcar(vector< vector<int> > & N,int modulo,bool & krylov,vector<int> & res,GIAC_CONTEXT,bool compute_pmin){
    int n=int(N.size());
    if (krylov){ // try Krylov pmin
      vector< vector<int> > temp(n+1),ttemp; 
      vector<int> & t0=temp[0];
      t0.reserve(n);
      for (int i=0;i<n;++i)
	t0.push_back(std::rand()%modulo);
      // for very very large matrices (10^7 entries?) 
      // it might be faster to compute
      // N, N^2, (N^2)^2, etc. and compute Nv, N^2(v,Nv), N^4(v,Nv,N^2v,N^3v)...
      for (int j=0;j<n;++j){
	if (!multvectvector_int_vector_int(N,temp[j],modulo,temp[j+1]))
	  return false;
      }
      if (debug_infolevel>2)
	CERR << CLOCK() << " Charpoly mod " << modulo << " tran " << endl;
      tran_vect_vector_int(temp,ttemp);
      vecteur pivots;
      longlong det;
      vector<int> permutation,maxrankcol;
      if (debug_infolevel>2)
	CERR << CLOCK() << " Charpoly mod " << modulo << " rref " << endl;
      smallmodrref(1,ttemp,pivots,permutation,maxrankcol,det,0,n,0,n+1,false/*full reduction */,0,modulo,2/* LU */,true,0,true,-1);
      if (debug_infolevel>2)
	CERR << CLOCK() << " Charpoly mod " << modulo << " det=" << det << " " << endl;
      // If rank==n-1 extract the min polynomial and find charpoly using the trace
      // if det==0 && rank<n-1 we will use Hessenberg
      // we could use recursive method
      // permute lines and columns of N with permutation
      // P*N*P^t =[[N11 N12]
      //           [N21 N22]] where N11 is rank*rank
      // where ttemp=K, P*K=L*U, L=[[L11,0],[L21,Id]] L11 rankxrank
      // find charpoly of N22-L21*L11^-1*N12
      int rank=det?n:(ttemp[n-2][n-2]?n-1:0);
      if (
	  // false 
	  det || rank==n-1
	  ){ 
	// U*charpol=last column
	for (int i=rank-1;i>=0;--i){
	  // charpol[i]=LU[i,i]^(-1)*(bp[i]-sum(j>i)LU[i,j]*charpol[j])
	  int res=0;
	  vector<int> & li=ttemp[i];
	  for (int j=i+1;j<rank;++j)
	    mod(res,li[j],ttemp[j][rank],modulo);
	  if (li[i]==0){ rank=0; break; }
	  li[rank]=(invmod(li[i],modulo)*longlong(li[rank]-res))%modulo;
	}
	// the last column is the min poly
	res.resize(rank+1);
	for (int i=0;i<rank;++i)
	  res[rank-i]=smod(-ttemp[i][rank],modulo);
	res[0]=1;
	if (rank==n)
	  return true;
	if (rank==n-1){
	  if (compute_pmin)
	    return true;
	  vector<int> resx=res;
	  resx.push_back(0);
	  longlong t=trace(N,modulo)+res[1]; 
	  // res[1]=-sum eigenvals, trace=sum eigenvals with multiplicities
	  for (int i=0;i<=rank;++i)
	    resx[i+1] = smod(resx[i+1]-t*res[i],modulo);
	  res.swap(resx);
	  // CERR << res << endl;
	  return true;
	}
      }
      else {
	krylov=false;
	if (debug_infolevel>2)
	  CERR << CLOCK() << " Singular, calling Hessenberg " << endl;
      }
    }
#if 1 // Danilevsky is faster than Hessenberg but slower than Krylov
    if (n*double(n)*modulo<double(1ULL<<63)){
      mod_pcar(N,modulo,res,compute_pmin);
      return true;
    }
#endif
    if (compute_pmin)
      return false;
    mhessenberg(N,N,modulo,false); // Hessenberg reduction, don't compute P
    if (debug_infolevel>2)
      CERR << CLOCK() << " Hessenberg reduced" << endl;
    vector<int> P0(1,1),P1; 
    P0.reserve(n+1); P1.reserve(n+1);
    vector< vector<int> > P;
    P.reserve(n+1);
    P.push_back(P0);
    for (int m=1;m<=n;++m){
      longlong n=N[m-1][m-1];
      P1=P0;
      P1.push_back(0);
      for (int j=0;j<P0.size();++j){
	P1[j+1] = (P1[j+1]-n*P0[j])%modulo;
      }
      P1.swap(P0);
      longlong t=1;
      for (int i=1;i<m;++i){
	t=(t*N[m-i][m-i-1])%modulo;
	longlong f=(t*N[m-i-1][m-1])%modulo;
	const vector<int> & pmi=P[m-i-1];
	int delta=int(P0.size()-pmi.size());
	int * target=&P0[delta];
	const int * ptr=&pmi[0], * ptrend=ptr+pmi.size();
	for (;ptr!=ptrend;++target,++ptr){
	  *target = (*target-f*(*ptr))%modulo;
	}
	// for (int j=0;j<pmi.size();++j) P0[j+delta]=(P0[j+delta]-f*pmi[j])%modulo;
      }
      P.push_back(P0);
    }
#if 1
    // CERR << P0 << endl;
    res=P0;
#else
    modpoly p0(1,plus_one);
    modpoly pX(2,plus_one);
    vector< modpoly > p(1,p0);
    environment env;
    env.moduloon=true; env.modulo=modulo;
    for (int m=1;m<=n;++m){
      pX[1]=-N[m-1][m-1];
      p0=operator_times(pX,p0,&env);
      longlong t=1;
      for (int i=1;i<m;++i){
	t=(t*N[m-i][m-i-1])%modulo;
	p0=p0-operator_times(gen(int((t*N[m-i-1][m-1])%modulo)),p[m-i-1],&env);
      }
      p.push_back(p0);
    }
    vecteur2vector_int(p0,modulo,res);
#endif
    // dbgtmp=p0;
    if (debug_infolevel>2)
      CERR << CLOCK() <<" Hessenberg charpoly " << endl;
    return true;
  }
    
  bool mod_pcar(const matrice & A,vector< vector<int> > & N,int modulo,bool & krylov,vector<int> & res,GIAC_CONTEXT,bool compute_pmin){
    if (debug_infolevel>2)
      CERR << CLOCK() << " Charpoly mod " << modulo << " A*v" << endl;
    if (!vecteur2vectvector_int(A,modulo,N))
      return false;
    return mod_pcar(N,modulo,krylov,res,contextptr,compute_pmin);
  }

  vecteur mpcar_int(const matrice & A,bool krylov,GIAC_CONTEXT,bool compute_pmin){
    int n=int(A.size());
    gen B=evalf_double(linfnorm(A,contextptr),0,contextptr);
    double Bd=B._DOUBLE_val;
    if (!Bd){
      modpoly charpol(n+1);
      charpol[0]=1;
      return charpol;
    }
    if (n>20){
      matrix_double H;
      if (matrice2std_matrix_double(A,H)){
	vector<double> d;
	// improve eigenvalues estimate
	balance_krylov(H,d,5,1e-8);
	giac_double Hd=linfnorm(H,d);
	if (Hd<Bd)
	  Bd=Hd;
      }
    }
    // max value of any coeff in the charpoly
    // max eigenval is <= sqrt(n)||A|| hence bound is in n (log(B)+log(n)/2)
    // we must add combinatorial (n k)<=2^n
    // or optimize comb(n,k)*(sqrt(n)||A||)^n
    // gives k<=n/(1+1/sqrt(n)B)
    double logbound=n/(1+1.0/std::sqrt(double(n))/Bd)*(std::log10(double(n))/2+std::log10(Bd))+n*std::log10(2.0),testvalue;
    double proba=proba_epsilon(contextptr),currentprob=1;
    gen currentp(init_modulo(n,logbound));
    gen pip(currentp);
    double pipd=std::log10(pip.val/2+1.0);
    vector<int> modpcar;
    vector< vector<int> > N;
    if (!mod_pcar(A,N,currentp.val,krylov,modpcar,contextptr,compute_pmin))
      return vecteur(1,gensizeerr(contextptr));
    modpoly charpol;
    vector_int2vecteur(modpcar,charpol);
    int initial_clock=CLOCK();
    int dbglevel=debug_infolevel;
    for (;pipd < (testvalue=logbound*charpol.size()/(n+1.0));){
      if (currentprob < proba &&  pipd<testvalue/1.33 && CLOCK()-initial_clock>min_proba_time*CLOCKS_PER_SEC)
	break;
      if (n>10 && dbglevel<2 && CLOCK()-initial_clock>60*CLOCKS_PER_SEC)
	dbglevel=2;
      if (dbglevel>1)
	CERR << CLOCK() << " " << 100*pipd/testvalue << " % done" << (currentprob<proba?", stable.":", unstable.")<< endl;
      currentp=nextprime(currentp.val+2);
      if (!mod_pcar(A,N,currentp.val,krylov,modpcar,contextptr,compute_pmin))
	return vecteur(1,gensizeerr(contextptr));
      if (modpcar.size()<charpol.size())
	continue;
      if (modpcar.size()>charpol.size()){
	vector_int2vecteur(modpcar,charpol);
	pip=currentp;
	continue;
      }
      bool stable;
      int tmp;
      if (pip.type==_ZINT && (tmp=ichinrem_inplace(charpol,modpcar,pip,currentp.val)) ){
	stable=tmp==2;
      } else {
	modpoly newcharpol,currentcharpol;
	vector_int2vecteur(modpcar,currentcharpol);
	newcharpol=ichinrem(charpol,currentcharpol,pip,currentp);
	stable=newcharpol==charpol;
	charpol.swap(newcharpol);
      }
      if (stable)
	currentprob=currentprob/currentp.val;
      else 
	currentprob=1.0;
      pip=pip*currentp;
      pipd += std::log10(double(currentp.val));
    }
    if (pipd<testvalue)
      *logptr(contextptr) << gettext("Probabilistic answer. Run proba_epsilon:=0 for a certified result. Error <") << proba << endl;
    return charpol;
  } // end if (is_integer_matrix)

  dense_POLY1 mpcar_hessenberg(const matrice & A,int modulo,GIAC_CONTEXT){
    int n=int(A.size());
    modpoly dbgtmp;
    bool krylov=true;
    if (modulo){
      vector<int> res; modpoly RES;
      vector< vector<int> > N;
      if (!mod_pcar(A,N,modulo,krylov,res,contextptr,false))
	return vecteur(1,gensizeerr("Non integer cell in matrix"));
      vector_int2vecteur(res,RES);
      return RES;
    }
    if (is_integer_matrice(A))
      return mpcar_int(A,krylov,contextptr,false);
    matrice H,P;
    if (!mhessenberg(A,H,P,modulo,500,1e-10,contextptr))
      return vecteur(1,gensizeerr(contextptr));
    if (modulo)
      H=*makemod(H,modulo)._VECTptr;
    dense_POLY1 p0(1,plus_one),pX(2,plus_one);
    vector< dense_POLY1 > p(1,p0);
    for (int m=1;m<=n;++m){
      pX[1]=-H[m-1][m-1];
      p0=pX*p0;
      gen t(plus_one);
      for (int i=1;i<m;++i){
	t=t*H[m-i][m-i-1];
	p0=p0-t*H[m-i-1][m-1]*p[m-i-1];
      }
      p.push_back(p0);
    }
    // if (!is_zero(dbgtmp-p0)) CERR << dbgtmp-p0 << endl;
    return p0;
  }
  gen _pcar_hessenberg(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!is_squarematrix(g)){
      if (g.type==_VECT && g._VECTptr->size()==2){
	gen m=g._VECTptr->front(),x=g._VECTptr->back();
	if (is_squarematrix(m))
	  return symb_horner(mpcar_hessenberg(*m._VECTptr,0,contextptr),x);
      }
      return symbolic(at_pcar_hessenberg,g);
    }
    matrice m(*g._VECTptr);
    return mpcar_hessenberg(m,0,contextptr);
  }
  static const char _pcar_hessenberg_s []="pcar_hessenberg";
  static define_unary_function_eval (__pcar_hessenberg,&giac::_pcar_hessenberg,_pcar_hessenberg_s);
  define_unary_function_ptr5( at_pcar_hessenberg ,alias_at_pcar_hessenberg,&__pcar_hessenberg,0,true);


  // Fadeev algorithm to compute the char poly of a matrix
  // B is a vector of matrices
  // the returned value is the vector of coeff of the char poly
  // see modpoly.h for polynomial operations on vecteur
  dense_POLY1 mpcar(const matrice & a,vecteur & Bv,bool compute_Bv,bool convert_internal,GIAC_CONTEXT){
    int n=int(a.size());
    gen modulo,fieldpmin;
    if (n && has_gf_coeff(a,modulo,fieldpmin)){
      gen tmp=_pcar(a,contextptr);
      if (tmp.type!=_VECT)
	return vecteur(1,gensizeerr(contextptr));
      vecteur P=*tmp._VECTptr;
      // do Horner to compute Bv
      if (compute_Bv){
	horner(P,a,0,Bv);
	Bv[0]=midn(n);
      }
      return P;      
    }
    if (n && a[0]._VECTptr->front().type==_MOD){
      vecteur P(mpcar_hessenberg(a,0,contextptr));
      // do Horner to compute Bv
      if (compute_Bv){
	horner(P,a,0,Bv);
	Bv[0]=midn(n);
      }
      return P;
    }
    matrice A,Bi,Ai,I,lv;
    if (convert_internal){
      // convert a to internal form
      lv=alg_lvar(a);
      A = *(e2r(a,lv,contextptr)._VECTptr);
    }
    else
      A=a;
    midn(n,I);
    Bi=I; // B0=Id
    Bv.push_back(Bi); 
    vecteur P;
    gen pk;
    P.push_back(1); // p0= 1
    for (int i=1;i<=n;++i){
      // for polynomial coefficients interpolate?
      mmult(A,Bi,Ai); // Ai = A*Bi
      pk = rdiv(-mtrace(Ai),i,contextptr); 
      P.push_back(convert_internal?r2e(pk,lv,contextptr):pk);
      addvecteur( Ai,multvecteur(pk,I),Bi); // Bi = Ai+pk*I
      // COUT << i << ":" << Bi << endl;
      if (i!=n)
	Bv.push_back(convert_internal?r2e(Bi,lv,contextptr):Bi);
    }
    return P;
  }

  dense_POLY1 mpcar(const matrice & a,vecteur & Bv,bool compute_Bv,GIAC_CONTEXT){
    return mpcar(a,Bv,compute_Bv,false,contextptr);
  }

  gen _lagrange(const gen & g,GIAC_CONTEXT);

  static gen pcar_interp(const matrice & a,gen & g,GIAC_CONTEXT){
    vecteur res;
    if (poly_pcar_interp(a,res,false,contextptr)){
      if (g.type==_VECT)
	return res;
      return symb_horner(res,g);
    }
    int m=int(a.size());
    vecteur v1,v2,I(midn(m));
    int shift=-m/2;
    for (int j=0;j<=m;++j){
      v1.push_back(j-shift);
      v2.push_back(mdet(addvecteur(a,multvecteur(shift-j,I)),contextptr));
    }
    return _lagrange(makevecteur(v1,v2,g),contextptr);
  }

  gen _pcar(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    vecteur Bv;
    matrice M;
    gen b(undef);
    if (!is_squarematrix(a)){
      if (a.type!=_VECT)
	return symb_pcar(a);
      vecteur v=*a._VECTptr;
      int s=int(v.size());
      if (s<2 || !is_squarematrix(v.front()))
	return gensizeerr(contextptr);
      matrice &m=*v.front()._VECTptr;
      if (v.back().type==_INT_ && v.back().val==_FADEEV){
	vecteur res=mpcar(m,Bv,false,true,contextptr);
	return s==2?res:symb_horner(res,v[1]);
      }
      if (v.back()==at_pmin && probabilistic_pmin(m,Bv,false,contextptr))
	return s==2?Bv:symb_horner(Bv,v[1]); 
      if (v.back()==at_lagrange || v.back()==at_interp)
	return pcar_interp(m,s==2?vx_var:v[1],contextptr);
      if (v.back()==at_hessenberg || v.back()==at_pcar_hessenberg){
	Bv=mpcar_hessenberg(m,0,contextptr);
	return s==2?Bv:symb_horner(Bv,v[1]);
      }
      b=v[1];
      M=m;
    }
    else
      M=*a._VECTptr;
    // int n=M.size();
    // search for the best algorithm
    gen p=M[0][0];
    if (p.type==_USER){
      std_matrix<gen> m; vecteur w;
      matrice2std_matrix_gen(M,m);
      mod_pcar(m,w,true);
      if (is_undef(b))
	return gen(w,_POLY1__VECT);
      return symb_horner(w,b);	
    }
    if (p.type==_MOD && (p._MODptr+1)->type==_INT_){
      gen mg=unmod(M);
      if (mg.type==_VECT){
	matrice M1=*mg._VECTptr;
	vector< vector<int> > N;
	int modulo=(p._MODptr+1)->val;
	bool krylov=true;
	vector<int> res;
	if (mod_pcar(M1,N,modulo,krylov,res,contextptr,false)){
	  vecteur w;
	  vector_int2vecteur(res,w);
	  w=*makemod(w,modulo)._VECTptr;
	  if (is_undef(b))
	    return gen(w,_POLY1__VECT);
	  return symb_horner(w,b);	
	}
      }
    }
    if (is_integer_matrice(M)){
      vecteur res=mpcar_hessenberg(M,0,contextptr);
      if (is_undef(b))
	return gen(res,_POLY1__VECT);
      return symb_horner(res,b);	
    }
    if (is_fraction_matrice(M)){
      gen res=pcar_interp(M,is_undef(b)?vx_var:b,contextptr);
      return is_undef(b)?_e2r(res,contextptr):res;
    }
    vecteur res;
    if (poly_pcar_interp(M,res,false,contextptr))
      return res;
    res=mpcar(M,Bv,false,true,contextptr);
    if (is_undef(b))
      return res;
    return symb_horner(res,b);
  }
  static const char _pcar_s []="pcar";
  static define_unary_function_eval (__pcar,&giac::_pcar,_pcar_s);
  define_unary_function_ptr5( at_pcar ,alias_at_pcar,&__pcar,0,true);

#if 0
  static vecteur polymat2matpoly(const vecteur & v){
    if (v.empty() || v.front().type!=_VECT)
      return vecteur(1,gensizeerr(gettext("polymat2matpoly")));
    int l,c,s=v.size();
    mdims(*v.front()._VECTptr,l,c);
    vecteur mat;
    mat.reserve(l);
    for (int i=0;i<l;++i){
      vecteur ligne;
      ligne.reserve(c);
      for (int j=0;j<c;++j){
	bool trim=true;
	vecteur res;
	res.reserve(s);
	for (int k=0;k<s;++k){
	  gen g=v[k];
	  gen tmp=g[i][j];
	  if (trim && is_zero(tmp,context0))
	    continue;
	  trim=false;
	  res.push_back(tmp);
	}
	ligne.push_back(gen(res,_POLY__VECT));
      }
      mat.push_back(ligne);
    }
    return mat;
  }
#endif

  static vecteur polymat2mat(const vecteur & v){
    if (v.empty()) 
      return v;
    if (v.front().type!=_VECT)
      return vecteur(1,gensizeerr(gettext("polymat2mat")));
    int l,c,s=int(v.size());
    vecteur w(v);
    for (int i=0;i<s;++i)
      w[i]=mtran(*v[i]._VECTptr);
    mdims(*v.front()._VECTptr,l,c);
    vecteur mat;
    mat.reserve(l*s);
    for (int k=0;k<s;++k){
      gen & g=w[k];
      for (int i=0;i<l;++i){
	mat.push_back(g[i]);
      }
    }
    return mat;
  }

  // dot product of a[0..a.size()-1] and b[pos..pos+a.size()-1]
  gen generalized_dotvecteur(const vecteur & a,const vecteur & b,int pos){
    vecteur::const_iterator ita=a.begin(), itaend=a.end();
    vecteur::const_iterator itb=b.begin()+pos;
    gen res;
    for (;(ita!=itaend);++ita,++itb){
      res = res + (*ita)*(*itb);
    }
    return res;
  }
  
  vecteur generalized_multmatvecteur(const matrice & a,const vecteur & b){
    vecteur::const_iterator ita=a.begin(), itaend=a.end();
    int s=int(b.size());
    int n=int(itaend-ita); // number of vectors stored in b=s/n
    vecteur res;
    res.reserve(s);
    for (int i=0;i<s;i+=n){
      for (ita=a.begin();ita!=itaend;++ita){
	res.push_back(generalized_dotvecteur(*(ita->_VECTptr),b,i));
      }
    }
    return res;
  }

  // [almost] rational jordan block
  matrice rat_jordan_block(const vecteur & v,int n,bool pseudo){
    if (n<1)
      return vecteur(1,gendimerr(gettext("rat_jordan_block")));
    int s=int(v.size())-1;
    // Size of the matrix is s*n
    vecteur ligne(s*n,zero);
    std_matrix<gen> M(s*n,ligne);
    for (int i=0;i<n;++i){
      // Fill the block-diagonal part with companion block
      for (int j=0;j<s;++j){
	M[i*s+j][i*s+s-1]=-v[s-j];
	if (j>0)
	  M[i*s+j][i*s+j-1]=plus_one;
      }
      // Fill the upper diagonal with idn or a single 1
      if (i!=n-1){
	if (pseudo)
	  M[i*s][i*s+s+s-1]=1;
	else {
	  for (int j=0;j<s;++j){
	    M[i*s+j][i*s+s+j]=1;
	  }
	}
      }
    }
    matrice res;
    std_matrix_gen2matrice_destroy(M,res);
    return res;
  }

  gen _rat_jordan_block(const gen &args,GIAC_CONTEXT){
    if (args.type==_STRNG && args.subtype==-1) return args;
    if (args.type==_VECT && args._VECTptr->size()==3){
      vecteur & v=*args._VECTptr;
      gen Px=_e2r(makevecteur(v[0],v[1]),contextptr);
      if (Px.type==_VECT && v[2].type==_INT_){
	int n=v[2].val;
	return rat_jordan_block(*Px._VECTptr,absint(n),n<0);
      }
    }
    return gensizeerr(contextptr);
  }
  static const char _rat_jordan_block_s []="rat_jordan_block";
  static define_unary_function_eval (__rat_jordan_block,&giac::_rat_jordan_block,_rat_jordan_block_s);
  define_unary_function_ptr5( at_rat_jordan_block ,alias_at_rat_jordan_block,&__rat_jordan_block,0,true);

  matrice pseudo_rat_to_rat(const vecteur & v,int n){
    if (n<1)
      return vecteur(1,gendimerr(gettext("pseudo_rat_ro_rat")));
    matrice A(rat_jordan_block(v,n,true));
    if (is_undef(A)) return A;
    // lines of A are initial v
    vecteur q(v);
    int d=int(q.size())-1; // degree of the polynomial
    matrice res(midn(n*d));
    reverse(q.begin(),q.end());
    for (int j=1;j<n;++j){
      // compute Q(A) v_{j,0}
      vecteur QAvj0(n*d);
      for (int l=1;l<=d;++l){
	int mmax=giacmin(l,j);
	for (int m=1;m<=mmax;++m){
	  QAvj0=addvecteur(QAvj0,multvecteur(q[l]*comb((unsigned long) l,(unsigned long)m),*res[(j-m)*d+(l-m)]._VECTptr));
	}
      }
      // shift
      vecteur vj0=mergevecteur(vecteur(d),vecteur(QAvj0.begin(),QAvj0.begin()+(n-1)*d));
      // replace in res
      res[j*d]=vj0;
      // compute images by A, ..., A^[d-1]
      for (int l=1;l<d;++l){
	vj0=multmatvecteur(A,vj0);
	vecteur tmp(vj0);
	int mmax=giacmin(l,j);
	for (int m=1;m<=mmax;++m)
	  tmp=subvecteur(tmp,multvecteur(comb((unsigned long) l,(unsigned long) m),*res[(j-m)*d+(l-m)]._VECTptr));
	res[j*d+l]=tmp;
      }
    }
    return res;
  }

  // input trn(p)*d*p=original matrix, d upper triangular
  // output p*d*inv(p)=original matrix, d diagonal
  bool schur_eigenvectors(matrice &p,matrice & d,double eps,GIAC_CONTEXT){
    bool ans=true;
    int dim=int(p.size());
    matrice m(midn(dim)); 
    // columns of m are the vector of the basis of the Schur decomposition
    // in terms of the eigenvector
    for (int k=1;k<dim;++k){
      // compute column k of m
      for (int j=0;j<k;++j){
	gen tmp=0;
	for (int i=0;i<k;++i){
	  tmp += d[i][k]*m[j][i];
	}
	if (!is_zero(tmp)) 
	  tmp = tmp/(d[j][j]-d[k][k]);
	(*m[j]._VECTptr)[k]=tmp;
      }
    }
    m=minv(m,contextptr);
    if (is_undef(m)) 
      return false;
    p=mmult(*_trn(p,contextptr)._VECTptr,m);
    // set d to its diagonal
    for (int i=0;i<dim;++i){
      vecteur & di= *d[i]._VECTptr;
      for (int j=0;j<dim;++j){
	if (j==i) continue;
#ifndef GIAC_HAS_STO_38
	if (ans && j==i-1 && is_greater(abs(di[j]/di[j+1],contextptr),eps,contextptr)){
	  *logptr(contextptr) << gettext("Low accuracy for Schur row ") << j << " " << d[i] << endl;
	  ans=false;
	}
#endif
	di[j]=0;
      }
    }
    return ans;
  }

  void matrice_double2lapack(const matrix_double & H,double * A){
    matrix_double::const_iterator it=H.begin(),itend=H.end();
    int rows=int(itend-it);
    for (int i = 0; it!=itend; ++i,++it){
      const vector<giac_double> & v =*it;
      vector<giac_double>::const_iterator jt=v.begin(),jtend=v.end();
      for (int j = 0; jt!=jtend;++j, ++jt){
	A[i + j * rows] = *jt;
      }
    }
  }

  void lapack2matrice_double(double * A, int rows,int cols,matrix_double & R){
    R.resize(rows);
    for (int i=0;i<rows;++i){
      vector<giac_double> & r=R[i];
      r.resize(cols);
      for (int j=0;j<cols;++j)
	r[j] = A[i + j * rows];
    }
  }

  void transpose_square_matrix(matrix_double & R){
    int n=int(R.size());
    for (int i=0;i<n;++i){
      for (int j=0;j<i;++j){
	giac_double tmp=R[i][j];
	R[i][j]=R[j][i];
	R[j][i]=tmp;
      }
    }
  }

#ifdef HAVE_LIBLAPACK
  bool lapack_schur(matrix_double & H,matrix_double & P,bool compute_P,vecteur & eigenvalues){
    if (!CAN_USE_LAPACK || int(H.size())<CALL_LAPACK)
      return false;
    /* int dgees_(char *jobvs, char *sort, L_fp select, integer *n, 
       doublereal *a, integer *lda, integer *sdim, doublereal *wr, 
       doublereal *wi, doublereal *vs, integer *ldvs, doublereal *work, 
       integer *lwork, logical *bwork, integer *info)
       jobvs="n" or "v" (compute Schur vectors)
       sort="n" or "s" (sort eigenvals), 
       select(real,imag) should return true if eigenval is selected
       n order of matrix A (==size(A))
       a input/output matrix (Schur form on output)
       lda leading dimension of A >= max(1,n)
       sdim (output) =0 if sort=="n" or number of eigenval selected if =="s" 
       wr and wi (output) contains real and imaginary part of eigenvals
       vs (output) if jobs=="v: contains the orthogonal matrix
       ldvs leading dimension of vs (>=n if jobvs=="v")
       lwork >=3*n, dimension of the work array (should be larger for good results)
       bwork array of dimension n
       info ==0 success, <0 bad arg value, >0 runtime error
    */
    integer n=H.size(),sdim,ldvs=n,lwork=max(20,n)*n,info;
    doublef2c_real * Hlapack=new doublef2c_real[n*n], * Plapack=new doublef2c_real[n*n];
    doublef2c_real * Wreal=new doublef2c_real[n], * Wimag=new doublef2c_real[n], * work=new doublef2c_real[lwork];
    logical * bwork=new logical[n];
    char ch[2]={0,0};
    ch[0]=(compute_P?'v':'n');
    matrice_double2lapack(H,Hlapack);
    char ch2[]="n";
    dgees_(ch,ch2,0,&n,
	   Hlapack,&n,&sdim,Wreal,
	   Wimag,Plapack,&ldvs,work,&lwork,
	   bwork,&info);
    lapack2matrice_double(Hlapack,n,n,H);
    if (compute_P){
      lapack2matrice_double(Plapack,n,n,P);
      transpose_square_matrix(P);
    }
    delete [] Hlapack; delete [] Plapack;
    eigenvalues.resize(n);
    for (int i=0;i<n;++i)
      eigenvalues[i]=gen(Wreal[i],Wimag[i]);
    delete [] Wreal; delete [] Wimag; delete [] work;
    delete [] bwork;
    return info==0;
  }
  
  bool lapack_schur(std_matrix<gen> & H,std_matrix<gen> & P,bool compute_P,vecteur & eigenvalues,GIAC_CONTEXT){
    if (!CAN_USE_LAPACK)
      return false;
  /* int zgees_(char *jobvs, char *sort, L_fp select, integer *n, 
     doublecomplex *a, integer *lda, integer *sdim, doublecomplex *w, 
     doublecomplex *vs, integer *ldvs, doublecomplex *work, integer *lwork, 
     doublereal *rwork, logical *bwork, integer *info) 
       jobvs="n" or "v" (compute Schur vectors)
       sort="n" or "s" (sort eigenvals), 
       select(real,imag) should return true if eigenval is selected
       n order of matrix A (==size(A))
       a input/output matrix (Schur form on output)
       lda leading dimension of A >= max(1,n)
       sdim (output) =0 if sort=="n" or number of eigenval selected if =="s" 
       w (output) contains eigenvals
       vs (output) if jobs=="v: contains the orthogonal matrix
       ldvs leading dimension of vs (>=n if jobvs=="v")
       lwork >=3*n, dimension of the work array (should be larger for good results)
       bwork array of dimension n
       info ==0 success, <0 bad arg value, >0 runtime error
    */
    integer n=H.size(),sdim,ldvs=n,lwork=max(20,n)*n,info;
    doublef2c_complex * Hlapack=new doublef2c_complex[n*n], * Plapack=new doublef2c_complex[n*n];
    doublef2c_complex * W=new doublef2c_complex[n], * work=new doublef2c_complex[lwork];
    doublef2c_real * rwork=new doublef2c_real[lwork];
    logical * bwork=new logical[n];
    matrix2zlapack(H,Hlapack,contextptr);
    char ch[2]={0,0};
    ch[0]=(compute_P?'v':'n');
    char ch2[]="n";
    zgees_(ch,ch2,0,&n,
	   Hlapack,&n,&sdim,W,
	   Plapack,&ldvs,work,&lwork,
	   rwork,bwork,&info);
    zlapack2matrix(Hlapack,n,n,H);
    if (compute_P){
      zlapack2matrix(Plapack,n,n,P);
      P=P.transconjugate();
    }
    delete [] Hlapack; delete [] Plapack;
    eigenvalues.resize(n);
    for (int i=0;i<n;++i)
      eigenvalues[i]=gen(W[i].r,W[i].i);
    delete [] W; delete [] work; delete [] rwork;
    delete [] bwork;
    return info==0;
  }
#else

  bool lapack_schur(matrix_double & H,matrix_double & P,bool compute_P,vecteur & eigenvalues){
    return false;
  }

  bool lapack_schur(std_matrix<gen> & H,std_matrix<gen> & P,bool compute_P,vecteur & eigenvalues,GIAC_CONTEXT){
    return false;
  }
#endif // HAVE_LIBLAPACK

  // if jordan is false, errors for non diagonalizable matrices
  // if jordan is true, d is a matrix, not a vector
  bool egv(const matrice & m0,matrice & p,vecteur & d, GIAC_CONTEXT,bool jordan,bool rational_jordan_form,bool eigenvalues_only){
    matrice m=m0;
    if (m.size()==1){
      p=vecteur(1,vecteur(1,1));
      if (jordan)
	d=m;
      else
	d=*m.front()._VECTptr;
      return true;
    }
    if (has_num_coeff(m)){
      gen g=evalf(m,1,contextptr);
      if (g.type==_VECT)
	m=*g._VECTptr;
    }
    bool numeric_matrix=is_fully_numeric(m);
    bool sym=(m==mtran(*conj(m,contextptr)._VECTptr));
    double eps=epsilon(contextptr);
    if (eps<1e-15) eps=1e-15;
    // check for symmetric numeric matrix
    if (numeric_matrix){
#ifdef HAVE_LIBLAPACK
      if ( !is_zero(im(m,contextptr),contextptr) ){ 
	// complex matrix, try hermitian
	if (m==conj(mtran(m),contextptr)){
	  // call to zheev
	  // ZHEEV( JOBZ, UPLO, N, A, LDA, W, WORK, LWORK, RWORK,INFO)
	  char JOBZ=eigenvalues_only?'N':'V';
	  char UPLO='U';
	  integer N=m.size();
	  doublef2c_complex * A=new doublef2c_complex[N*N];
	  matrice2zlapack(m,A,contextptr);
	  integer LDA=N;
	  double * W = new double[N];
	  integer LWORK=max(10,N)*N,INFO;
	  doublef2c_complex * WORK=new doublef2c_complex[LWORK];
	  double * RWORK=new double[LWORK];
	  zheev_( &JOBZ, &UPLO, &N, A, &LDA, W, WORK, &LWORK, RWORK,&INFO );
	  delete [] WORK; delete [] RWORK;
	  if (INFO){ 
	    delete [] A; delete [] W;  return false;
	  }
	  d.resize(N);
	  for (int i=0;i<N;++i){
	    if (jordan){
	      vecteur v(N);
	      v[i]=W[i];
	      d[i]=v;
	    }
	    else
	      d[i]=W[i];
	  }
	  zlapack2matrice(A,N,N,p);
	  delete [] A; delete [] W;
	  return true;
	}
	if (int(m.size())>=CALL_LAPACK){
	  // call to zgeev
	  // ZGEEV( JOBVL, JOBVR, N, A, LDA, W, VL, LDVL, VR, LDVR,WORK, LWORK, RWORK, INFO )
	  char JOBVL=eigenvalues_only?'N':'V';
	  char JOBVR=eigenvalues_only?'N':'V';
	  integer N=m.size();
	  doublef2c_complex * A=new doublef2c_complex[N*N];
	  matrice2zlapack(m,A,contextptr);
	  integer LDA=N;
	  doublef2c_complex * W=new doublef2c_complex[N];
	  doublef2c_complex * VL=new doublef2c_complex[N*N];
	  doublef2c_complex * VR=new doublef2c_complex[N*N];
	  integer LDVL=N,LDVR=N,LWORK=max(10,N)*N,INFO;
	  double * RWORK=new double[LWORK];
	  doublef2c_complex * WORK=new doublef2c_complex[LWORK];
	  zgeev_(&JOBVL, &JOBVR, &N, A, &LDA, W, VL, &LDVL, VR,&LDVR, WORK, &LWORK, RWORK,&INFO );
	  delete [] WORK; delete [] RWORK; delete [] A; delete [] VL; 
	  if (INFO){
	    delete [] W; delete [] VR; return false;
	  }
	  zlapack2matrice(VR,N,N,p);
	  d.resize(N);
	  for (int i=0;i<N;++i){
	    if (jordan){
	      vecteur v(N);
	      v[i]=gen(W[i].r,W[i].i);
	      d[i]=v;
	    }
	    else
	      d[i]=gen(W[i].r,W[i].i);
	  }
	  delete [] W; delete [] VR; 
	  return true;
	}
      }
      if (sym){ // call to dsyev
	// DSYEV( JOBZ, UPLO, N, A, LDA, W, WORK, LWORK, INFO )
	char JOBZ=eigenvalues_only?'N':'V';
	char UPLO='U';
	integer N=m.size();
	double * A=new double[N*N];
	matrice2lapack(m,A,contextptr);
	integer LDA=N;
	double * W = new double[N];
	integer LWORK=max(10,N)*N,INFO;
	double * WORK=new double[LWORK];
	dsyev_( &JOBZ, &UPLO, &N, A, &LDA, W, WORK, &LWORK, &INFO );
	delete [] WORK;
	if (INFO){ 
	  delete [] A; delete [] W;  return false;
	}
	d.resize(N);
	for (int i=0;i<N;++i){
	  if (jordan){
	    vecteur v(N);
	    v[i]=W[i];
	    d[i]=v;
	  }
	  else
	    d[i]=W[i];
	}
	lapack2matrice(A,N,N,p);
	delete [] A; delete [] W;
	return true;
      }
      if (int(m.size())>=CALL_LAPACK){
	// call to dgeev
	// DGEEV( JOBVL, JOBVR, N, A, LDA, WR, WI, VL, LDVL, VR,LDVR, WORK, LWORK, INFO )
	char JOBVL=eigenvalues_only?'N':'V';
	char JOBVR=eigenvalues_only?'N':'V';
	integer N=m.size();
	double * A=new double[N*N];
	matrice2lapack(m,A,contextptr);
	integer LDA=N;
	double * WR=new double[N];
	double * WI=new double[N];
	double * VL=new double[N*N];
	double * VR=new double[N*N];
	integer LDVL=N,LDVR=N,LWORK=max(10,N)*N,INFO=0;
	double * WORK=new double[LWORK];
	dgeev_(&JOBVL, &JOBVR, &N, A, &LDA, WR, WI, VL, &LDVL, VR,&LDVR, WORK, &LWORK, &INFO );
	delete [] WORK; delete [] A; delete [] VL;
	if (INFO){
	  delete [] WR; delete [] WI; delete [] VR; return false;
	}
	lapack2matrice(VR,N,N,p);
	p=mtran(p);
	d.resize(N);
	for (int i=0;i<N;++i){
	  if (jordan){
	    vecteur v(N);
	    v[i]=gen(WR[i],WI[i]);
	    d[i]=v;
	    if (WI[i]!=0 && i!=N-1){
	      v[i]=0;
	      v[i+1]=gen(WR[i],-WI[i]);
	      d[i+1]=v;
	    }
	  }
	  else {
	    d[i]=gen(WR[i],WI[i]);
	    if (WI[i]!=0 && i!=N-1)
	      d[i+1]=gen(WR[i],-WI[i]);
	  }
	  if (WI[i]==0 || i==N-1)
	    continue;
	  gen tmp=p[i]+cst_i*p[i+1];
	  p[i]=tmp;
	  p[i+1]=conj(tmp,contextptr);
	  ++i;
	}
	p=mtran(p);
	delete [] WR; delete [] WI; delete [] VR;
	return true;
      }
#endif // HAVE_LIBLAPACK
#ifdef HAVE_LIBGSL
      if (sym){
	gsl_matrix * a=matrice2gsl_matrix(m,contextptr);
	int s=a->size1;
	gsl_matrix * eigenvectors= gsl_matrix_alloc(s,s);
	gsl_vector * eigenvalues =gsl_vector_alloc(s);
	gsl_eigen_symmv_workspace * w=gsl_eigen_symmv_alloc(s);
	gsl_eigen_symmv (a, eigenvalues,eigenvectors,w);
	gsl_eigen_symmv_free(w);
	p=gsl_matrix2matrice(eigenvectors);
	d=gsl_vector2vecteur(eigenvalues);
	if (jordan){
	  for (int i=0;i<s;++i){
	    vecteur tmp(s);
	    tmp[i]=d[i];
	    d[i]=tmp;
	  }
	}
	gsl_matrix_free(eigenvectors);
	gsl_vector_free(eigenvalues);
	return true;
      } // end sym. matrix
#endif // HAVE_LIBGSL
      std_matrix<gen> H,P;
      matrice2std_matrix_gen(m,H);
      int dim(int(H.size()));
      matrice pid(midn(dim));
      matrice2std_matrix_gen(pid,P);
      matrix_double H1,P1;
      if (!eigenvalues_only){
	std_matrix_gen2std_matrix_giac_double(P,P1,false);
      }
      if (std_matrix_gen2std_matrix_giac_double(H,H1,false)){
	bool ans=francis_schur(H1,0,dim,P1,2*SOLVER_MAX_ITERATE,eps,false,!eigenvalues_only);
	if (eigenvalues_only){
	  vecteur res;
	  ans = ans && schur_eigenvalues(H1,res,eps,contextptr);
	  if (!jordan)
	    d=res;
	  else {
	    gen tmp=_diag(res,contextptr);
	    if (tmp.type!=_VECT)
	      return false;
	    d=*tmp._VECTptr;
	  }
	  return ans;
	}
	else {
	  std_matrix_giac_double2std_matrix_gen(H1,H);
	  std_matrix_giac_double2std_matrix_gen(P1,P);
	  // finish Schur with complex entries
	  ans=francis_schur(H,0,dim,P,2*SOLVER_MAX_ITERATE,eps,true,true,true,true,contextptr);
	  std_matrix_gen2matrice_destroy(P,p);
	  std_matrix_gen2matrice_destroy(H,d);
	  if (abs_calc_mode(contextptr)==38)
	    return ans && schur_eigenvectors(p,d,eps,contextptr);
	  schur_eigenvectors(p,d,eps,contextptr);
	  return ans;
	}
      }
      else {
	matrix_complex_double H2;
	bool ans;
	if (matrice2std_matrix_complex_double(m,H2)){
	  matrix_complex_double P2;
	  matrice2std_matrix_complex_double(pid,P2);
	  ans=francis_schur(H2,0,dim,P2,SOLVER_MAX_ITERATE,eps,false,true);
	  std_matrix_complex_double2std_matrix_gen(P2,P);
	  std_matrix_complex_double2std_matrix_gen(H2,H);
	}
	else
	  ans=francis_schur(H,0,dim,P,SOLVER_MAX_ITERATE,dim*eps,false,true,true,true,contextptr);
	std_matrix_gen2matrice_destroy(P,p);
	std_matrix_gen2matrice_destroy(H,d);
	return ans && schur_eigenvectors(p,d,eps,contextptr);
      }
    } // end if (numeric_matrix)
    int taille=int(m.size());
    vecteur lv;
    alg_lvar_halftan_tsimplify(m,lv,contextptr);
    numeric_matrix=has_num_coeff(m) && is_fully_numeric(evalf(m,1,contextptr));
    matrice mr=*(e2r(numeric_matrix?exact(m,contextptr):m,lv,contextptr)._VECTptr); // convert to internal form
    // vecteur lv;
    // matrice mr = m;
    matrice m_adj;
    vecteur p_car;
    p_car=mpcar(mr,m_adj,true,contextptr);
    p_car=common_deno(p_car)*p_car; // remove denominators
    // extension handling
    gen modulo,fieldpmin;
    if (has_mod_coeff(p_car,modulo)){
      modpoly pc=*unmod(p_car)._VECTptr;
      vector< facteur<modpoly> > vpc; vector<modpoly> qmat;
      environment env;
      env.modulo=modulo; env.moduloon=true; env.pn=modulo;
      if (ddf(pc,qmat,&env,vpc)){
	int extdeg=1;
	for (int j=0;j<int(vpc.size());++j){
	  extdeg=lcm(extdeg,vpc[j].mult).val;
	}
	if (extdeg>1){
	  *logptr(contextptr) << "Creating splitting field extension GF(" << modulo << "," << extdeg << ")" << endl;
	  gen tmp=_galois_field(makesequence(modulo,extdeg),contextptr);
	  tmp=tmp[plus_two];
	  tmp=eval(tmp[2],1,contextptr); // field generator
	  p_car=tmp*p_car;
	}
      }
      else
	*logptr(contextptr) << "Warning! Automatic extension not implemented. You can try to diagonalize the matrix * a non trivial element of GF(" << modulo << ",lcm of degrees of factor(" << symb_horner(p_car,vx_var) << "))" <<  endl;
    }
    if (has_gf_coeff(p_car,modulo,fieldpmin)){
      factorization f;
      gen res=gf_list()[pow(modulo,gfsize(fieldpmin),contextptr)].g;
      if (galois_field * ptr=dynamic_cast<galois_field *>(res._USERptr)){
	polynome P(1);
	poly12polynome(p_car,1,P,1);
	res=ptr->polyfactor(P,f);
	int extdeg=1;
	for (int i=0;i<int(f.size());++i){
	  extdeg=lcm(extdeg,f[i].fact.lexsorted_degree()).val;
	}
	if (extdeg>1){
	  extdeg *= gfsize(fieldpmin);
	  *logptr(contextptr) << "Creating splitting field extension GF(" << modulo << "," << extdeg << ")" << endl;
	  gen tmp=_galois_field(makesequence(modulo,extdeg),contextptr);
	  tmp=tmp[plus_two];
	  tmp=eval(tmp[2],1,contextptr); // field generator
	  p_car=tmp*p_car;
	}
      }
    }
    // factorizes p_car
    factorization f;
    polynome ppcar(poly1_2_polynome(p_car,1));
    polynome p_content(ppcar.dim);
    gen extra_div=1;
    if (!factor(ppcar,p_content,f,false,rational_jordan_form?false:withsqrt(contextptr),
		//false,
		complex_mode(contextptr),
		1,extra_div))
      return false;
    // insure that extra extensions created in factor are reduced inside m_adj
    //clean_ext_reduce(m_adj);
    factorization::const_iterator f_it=f.begin(),f_itend=f.end();
    int total_char_found=0;
    for (;f_it!=f_itend;++f_it){
      // find roots of it->fact
      // works currently only for 1st order factors
      // vecteur v=solve(f_it->fact);
      vecteur v;
      const polynome & itfact=f_it->fact;
      vecteur w=polynome2poly1(itfact,1);
      int s=int(w.size());
      if (s<2)
	continue;
      if (s==2)
	v.push_back(rdiv(-w.back(),w.front(),contextptr));
      if (is_undef(v))
	return false;
      gen x;
      vecteur cur_m_adj(m_adj),cur_lv(lv),new_m_adj,char_m;
      if (s>=3 && rational_jordan_form){
	int mult=f_it->mult;
	int qdeg=s-1;
	int n=mult*qdeg; // number of vectors to find
	// Divide cur_m_adj by w f_it->mult times
	// Collect the remainders matrices in C
	vecteur C,quo,rem;
	int char_line=0,char_found=0,cycle_size=mult; 
	for (int i=0;i<mult;++i){
	  DivRem(cur_m_adj,w,0,quo,rem);
	  // rem is a polynomial made of matrices
	  // we convert it to a matrix (explode the polys)
	  if (rem.empty()){
	    --cycle_size;
	  }
	  else {
	    C=mergematrice(C,polymat2mat(rem));
	    if (is_undef(C)) return false;
	  }
	  cur_m_adj=quo;
	}
	// char_line is the line where the reduction begins
	vecteur Ccopy(C),pivots;
	gen det;
	for (;char_found<n;){
	  // Reduce
	  if (!mrref(Ccopy,C,pivots,det,0,int(Ccopy.size()),0,taille,
		/* fullreduction */1,char_line,true,1,0,
		     contextptr))
	    return false;
	  // Extract a non-0 line at char_line
	  vecteur line=*C[char_line]._VECTptr;
	  if (is_zero(vecteur(line.begin(),line.begin()+taille),contextptr)){
	    // Keep lines 0 to char_line-1, remove last taille columns
	    Ccopy=mtran(vecteur(C.begin(),C.begin()+char_line));
	    if (signed(Ccopy.size())<taille)
	      return false; // setdimerr();
	    vecteur debut(Ccopy.begin(),Ccopy.end()-taille);
	    debut=mtran(debut);
	    // Cut first taille columns of the remainder of the matrix
	    Ccopy=mtran(vecteur(C.begin()+char_line,C.end()));
	    if (signed(Ccopy.size())<taille)
	      return false; // setdimerr();
	    vecteur fin(Ccopy.begin()+taille,Ccopy.end());
	    fin=mtran(fin);
	    Ccopy=mergevecteur(debut,fin);
	    --cycle_size;
	    continue;
	  }
	  Ccopy=vecteur(C.begin(),C.begin()+char_line);
	  // make a bloc with line and A, A^2, ..., A^[qdeg-1]*line
	  // and put them into Ccopy and in ptmp
	  vecteur ptmp;
	  for (int i=0;i<qdeg;++i){
	    Ccopy.push_back(line);
	    ptmp.push_back(line);
	    line=generalized_multmatvecteur(mr,line);
	  }
	  // finish Ccopy by copying the remaining lines of C
	  const_iterateur ittmp=C.begin()+char_line+1,ittmpend=C.end();
	  for (;ittmp!=ittmpend;++ittmp)
	    Ccopy.push_back(*ittmp);
	  // update d (with a ratjord bloc) 
	  int taille_bloc=qdeg*cycle_size;
	  matrice tmp=mtran(rat_jordan_block(w,cycle_size,false));
	  tmp=mergematrice(vecteur(qdeg*cycle_size,vecteur(total_char_found)),tmp);
	  tmp=mergematrice(tmp,vecteur(qdeg*cycle_size,vecteur(taille-total_char_found-taille_bloc)));
	  if (is_undef(tmp)) return false;
	  d=mergevecteur(d,tmp);
	  // update p with ptmp 
	  matrice padd;
	  for (int j=0;j<cycle_size;++j){
	    for (int i=0;i<qdeg;++i){
	      vecteur & ptmpi=*ptmp[i]._VECTptr;
	      padd.push_back(vecteur(ptmpi.begin()+taille*j,ptmpi.begin()+taille*(j+1)));
	    }  
	  }
	  matrice AA(pseudo_rat_to_rat(w,cycle_size));
	  if (is_undef(AA)) return false;
	  padd=mmult(AA,padd);
	  p=mergevecteur(p,padd);
	  char_found += taille_bloc;
	  total_char_found += taille_bloc;
	  char_line += cycle_size;
	}
	continue;
      } // end if s>=3 and rational_jordan_form
      if (s>=3){ // recompute cur_m_adj using new extensions
	cur_m_adj=*r2sym(m_adj,lv,contextptr)._VECTptr;
	identificateur tmpx(" x");
	vecteur ww(w.size());
	for (unsigned i=0;i<w.size();++i)
	  ww[i]=r2e(w[i],lv,contextptr);
	gen wwx=horner(ww,tmpx);
	v=solve(wwx,tmpx,complex_mode(contextptr),contextptr); 
	v=*apply(v,recursive_normal,contextptr)._VECTptr;
	if (v.size()!=w.size()-1){
	  gen m0num=evalf(m0,1,contextptr);
	  if (m0num.type==_VECT 
	      && is_numericm(*m0num._VECTptr)
	      // && lidnt(m0num).empty()
	      ){
	    *logptr(contextptr) << gettext("Unable to find exact eigenvalues. Trying approx") << endl;
	    return egv(*m0num._VECTptr,p,d,contextptr,jordan,false,eigenvalues_only);
	  }
	}
	// compute new lv and update v and m_adj accordingly
	cur_lv=alg_lvar(v);
	alg_lvar(cur_m_adj,cur_lv);
	cur_m_adj=*(e2r(cur_m_adj,cur_lv,contextptr)._VECTptr);
	v=*(e2r(v,cur_lv,contextptr)._VECTptr);
      }
      const_iterateur it=v.begin(),itend=v.end();
      gen cur_m;
      for (;it!=itend;++it){
	vecteur cur_m_adjx(cur_m_adj);
	char_m.clear();
	int n=f_it->mult;
	x=r2sym(*it,cur_lv,contextptr);
	if (eigenvalues_only && !jordan){
	  d=mergevecteur(d,vecteur(n,x));
	  total_char_found +=n;
	  continue;
	}
	// compute Taylor expansion of m_adj at roots of it->fact
	// at order n-1
	for (;;){
	  --n;
	  if (n){
	    cur_m=horner(cur_m_adjx,*it,0,new_m_adj);
	    if (char_m.empty())
	      char_m=mtran(*cur_m._VECTptr);
	    else
	      char_m=mergematrice(char_m,mtran(*cur_m._VECTptr));
	    if (is_undef(char_m) || (!jordan && !is_zero(cur_m,contextptr)) ){
#ifndef NO_STDEXCEPT
	      throw(std::runtime_error("Not diagonalizable at eigenvalue "+x.print()));
#endif
	      return false;
	    }
	    cur_m_adjx=new_m_adj;
	  }
	  else {
	    cur_m=horner(cur_m_adjx,*it);
	    char_m=mergematrice(char_m,mtran(*cur_m._VECTptr));
	    if (is_undef(char_m)) return false;
	    break;
	  }
	}
	n=f_it->mult;
	if (n==1){ 
	  char_m=mtran(*cur_m._VECTptr);
	  iterateur ct=char_m.begin(),ctend=char_m.end();
	  for (;ct!=ctend;++ct){
	    if (!is_zero(*ct,contextptr))
	      break;
	  }
	  if (ct==ctend)
	    return false; // setsizeerr(gettext("egv/jordan bug"));
	  // FIXME take 1st non-0 col as eigenvector
	  *ct=*ct/lgcd(*ct->_VECTptr);
	  gen eigenvector=r2sym(*ct,cur_lv,contextptr);
	  if (is_fully_numeric(eigenvector) || numeric_matrix)
	    eigenvector=_normalize(eigenvector,contextptr);
	  p.push_back(eigenvector);
	  if (jordan){
	    vecteur vegv(taille,zero);
	    if (total_char_found>taille)
	      return false; // setsizeerr(gettext("Bug in egv/jordan"));
	    vegv[total_char_found]=x;
	    d.push_back(vegv);
	  }
	  else
	    d.push_back(x);
	  ++total_char_found;
	  continue;
	}
	if (jordan){
	  // back to external form
	  char_m=*r2sym(char_m,cur_lv,contextptr)._VECTptr;
	  int egv_found=0;
	  int char_found=0;
	  vecteur char_m_copy(char_m),pivots;
	  gen det;
	  for (;char_found<n;){ 
	    if (!mrref(char_m_copy,char_m,pivots,det,0,taille,0,taille,
		  /* fullreduction */1,egv_found,true,1,0,
		       contextptr))
	      return false;
	    if (sym )
	      char_m=gramschmidt(char_m,false,contextptr);
	    char_m_copy.clear();
	    // extract non-0 lines starting from line number egv_found
	    vecteur vegv;
	    int j=0;
	    for (;j<egv_found;++j)
	      char_m_copy.push_back(vecteur(char_m[j]._VECTptr->begin(),char_m[j]._VECTptr->end()-taille));
	    for (;j<taille;++j){
	      vegv=vecteur( char_m[j]._VECTptr->begin(),char_m[j]._VECTptr->begin()+taille);
	      if (is_zero(vegv,contextptr) || (numeric_matrix && evalf(abs(vegv,contextptr),1,contextptr)._DOUBLE_val<10*taille*epsilon(contextptr)) ) 
		break;
	      // cycle found! 
	      // update char_m_copy with all the cycle except first vector
	      char_m_copy.push_back(vecteur(char_m[j]._VECTptr->begin(),char_m[j]._VECTptr->end()-taille));
	      // Store cycle
	      const_iterateur c_it=char_m[j]._VECTptr->begin(),c_itend=char_m[j]._VECTptr->end();
	      for (;c_it!=c_itend;c_it+=taille){
		p.push_back(vecteur(c_it,c_it+taille)); // char vector
		// update d
		vegv=vecteur(taille,zero);
		if (total_char_found>=taille)
		  return false; // setsizeerr(gettext("Bug in egv/jordan"));
		if (c_it==char_m[j]._VECTptr->begin()){
		  vegv[total_char_found]=x;
		  ++egv_found;
		}
		else {
		  vegv[total_char_found-1]=1;
		  vegv[total_char_found]=x;
		}
		++char_found;
		++total_char_found;
		d.push_back(vegv);
	      }
	    }
	    for (;j<taille;++j){
	      char_m_copy.push_back(vecteur(char_m[j]._VECTptr->begin()+taille,char_m[j]._VECTptr->end()));
	    }
	  }
	} // end if (jordan)
	else {
	  d=mergevecteur(d,vecteur(n,x));
	  // back to external form
	  cur_m=r2sym(cur_m,cur_lv,contextptr);
	  // column reduction
	  matrice m_egv=mrref(mtran(*cur_m._VECTptr),contextptr);
	  if (sym){
	    // orthonormalize basis
	    m_egv=gramschmidt(matrice(m_egv.begin(),m_egv.begin()+f_it->mult),false,contextptr);
	  }
	  // non zero rows of cur_m are eigenvectors
	  const_iterateur m_it=m_egv.begin(),m_itend=m_egv.end();
	  for (; m_it!=m_itend;++m_it){
	    if (!is_zero(*m_it,contextptr))
	      p.push_back(*m_it);
	  }
	}
      }
    } // end for factorization
    if (!p.empty()){
      if (!eigenvalues_only)
	p=mtran(p);
      if (jordan)
	d=mtran(d);
    }
    return true;
  }
  matrice megv(const matrice & e,GIAC_CONTEXT){
    matrice m;
    vecteur d;
    bool b=complex_mode(contextptr);
    complex_mode(true,contextptr);
    if (!egv(e,m,d,contextptr,false,false,false))
      *logptr(contextptr) << gettext("Low accuracy or not diagonalizable at some eigenvalue. Try jordan if the matrix is exact.") << endl;
    complex_mode(b,contextptr);
    return m;
  }

  gen symb_egv(const gen & a){
    return symbolic(at_egv,a);
  }
  gen _egv(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (!is_squarematrix(a)){
      if (a.type==_VECT)
	return gendimerr(contextptr);
      return symb_egv(a);
    }
    return megv(*a._VECTptr,contextptr);
  }
  static const char _egv_s []="egv";
  static define_unary_function_eval (__egv,&giac::_egv,_egv_s);
  define_unary_function_ptr5( at_egv ,alias_at_egv,&__egv,0,true);


  vecteur megvl(const matrice & e,GIAC_CONTEXT){
    matrice m;
    vecteur d;
    bool b=complex_mode(contextptr);
    complex_mode(true,contextptr);
    if (!egv(e,m,d,contextptr,true,false,true))
      *logptr(contextptr) << gettext("Low accuracy") << endl;
    complex_mode(b,contextptr);
    return d;
  }
  gen symb_egvl(const gen & a){
    return symbolic(at_egvl,a);
  }
  gen _egvl(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (!is_squarematrix(a))
      return gendimerr(contextptr);
    return megvl(*a._VECTptr,contextptr);
  }
  static const char _egvl_s []="egvl";
  static define_unary_function_eval (__egvl,&giac::_egvl,_egvl_s);
  define_unary_function_ptr5( at_egvl ,alias_at_egvl,&__egvl,0,true);

  vecteur mjordan(const matrice & e,bool rational_jordan,GIAC_CONTEXT){
    matrice m;
    vecteur d;
    if (!egv(e,m,d,contextptr,true,rational_jordan,false))
      *logptr(contextptr) << gettext("Low accuracy") << endl;
    return makevecteur(m,d);
  }
  gen symb_jordan(const gen & a){
    return symbolic(at_jordan,a);
  }
  gen jordan(const gen & a,bool rational_jordan,GIAC_CONTEXT){
    if (a.type==_VECT && a.subtype==_SEQ__VECT && a._VECTptr->size()==2 && is_squarematrix(a._VECTptr->front()) ){
      vecteur v(mjordan(*a._VECTptr->front()._VECTptr,rational_jordan,contextptr));
      if (is_undef(v))
	return v;
      gen tmpsto=sto(v[0],a._VECTptr->back(),contextptr);
      if (is_undef(tmpsto)) return tmpsto;
      return v[1];
    }
    if (!is_squarematrix(a))
      return symb_jordan(a);
    vecteur v(mjordan(*a._VECTptr,rational_jordan,contextptr));
    if (is_undef(v))
      return v;
    if (xcas_mode(contextptr)==1)
      return v[1];
    else
      return gen(v,_SEQ__VECT);
  }

  gen _jordan(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    bool mode=complex_mode(contextptr);
    complex_mode(true,contextptr);
    gen res=jordan(a,false,contextptr);
    complex_mode(mode,contextptr);
    return res;
  }
  static const char _jordan_s []="jordan";
  static define_unary_function_eval (__jordan,&giac::_jordan,_jordan_s);
  define_unary_function_ptr5( at_jordan ,alias_at_jordan,&__jordan,0,true);

  gen _rat_jordan(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    return jordan(a,true,contextptr);
  }
  static const char _rat_jordan_s []="rat_jordan";
  static define_unary_function_eval (__rat_jordan,&giac::_rat_jordan,_rat_jordan_s);
  define_unary_function_ptr5( at_rat_jordan ,alias_at_rat_jordan,&__rat_jordan,0,true);

  matrice diagonal_apply(const gen & g,const gen & x,const matrice & m,GIAC_CONTEXT){
    if (!is_squarematrix(m))
      return vecteur(1,gensizeerr(contextptr));
    int n=int(m.size());
    matrice res;
    for (int i=0;i<n;++i){
      vecteur v=*m[i]._VECTptr;
      gen tmp=subst(g,x,v[i],false,contextptr);
      if (is_undef(tmp))
	tmp=subst(g,x,v[i],true,contextptr);
      v[i]=tmp;
      res.push_back(v);
    }
    return res;
  }

  matrice analytic_apply(const gen &ux,const gen & x,const matrice & m,GIAC_CONTEXT){
    if (!is_squarematrix(m))
      return vecteur(1,gensizeerr(contextptr));
    int n=int(m.size());
    matrice p,d,N,v(n),D;
    bool cplx=complex_mode(contextptr),sqrtb=withsqrt(contextptr);
    complex_mode(true,contextptr);
    withsqrt(true,contextptr);
    if (!egv(m,p,d,contextptr,true,false,false))
      return vecteur(1,gensizeerr(contextptr));
    complex_mode(cplx,contextptr);
    withsqrt(sqrtb,contextptr);
    if (int(p.size())!=n)
      return vecteur(1,gensizeerr(gettext("Unable to find all eigenvalues")));
    // search for distance of 1st non-zero non-diagonal element
    int dist=0;
    for (int i=0;i<n;++i){
      for (int j=0;j<n;++j){
	const gen & g=d[i][j];
	if (!is_zero(g,contextptr) && i!=j)
	  dist=giacmax(dist,n-absint(i-j));
	if (i==j)
	  v[j]=g;
	else
	  v[j]=zero;
      }
      D.push_back(v);
    }
    identificateur y(" y");
    if (!dist) {// u(d) should be replaced with applying u to elements of d
      d=diagonal_apply(ux,x,d,contextptr); 
      if (is_undef(d)) return d;
      return mmult(mmult(p,d),minv(p,contextptr));
    }
    N=subvecteur(d,D);
    vecteur pol;
    if (!taylor(ux,x,y,dist,pol,contextptr)) 
      return vecteur(1,gensizeerr(ux.print()+gettext(" is not analytic")));
    if (is_undef(pol.back()))
      pol.pop_back();
    reverse(pol.begin(),pol.end());
    // subst y with D (i.e. diagonal element by diagonal element)
    int pols=int(pol.size());
    for (int i=0;i<pols;++i){
      if (is_undef( (pol[i]=diagonal_apply(pol[i],y,D,contextptr)) ))
	return gen2vecteur(pol[i]);
    }
    gen res=horner(pol,N);
    if (res.type!=_VECT)
      return vecteur(1,gensizeerr(contextptr));
    d=mmult(p,*res._VECTptr);
    d=mmult(d,minv(p,contextptr));
    return d;
  }

  matrice analytic_apply(const unary_function_ptr *u,const matrice & m,GIAC_CONTEXT){
    identificateur x(" x");
    gen ux=(*u)(x,contextptr);
    return analytic_apply(ux,x,m,contextptr);
  }

  // return a vector which elements are the basis of the ker of a
  bool mker(const matrice & a,vecteur & v,int algorithm,GIAC_CONTEXT){
    v.clear();
    gen det;
    vecteur pivots;
    matrice res;
    if (!mrref(a,res,pivots,det,0,int(a.size()),0,int(a.front()._VECTptr->size()),
	  /* fullreduction */1,0,true,algorithm,0,
	       contextptr))
      return false;
    mdividebypivot(res);
    // put zero lines in res at their proper place, so that
    // non zero pivot are on the diagonal
    int s=int(res.size()),c=int(res.front()._VECTptr->size());
    matrice newres;
    newres.reserve(s);
    matrice::const_iterator it=res.begin(),itend=res.end();
    int i;
    for (i=0;(i<c) && (it!=itend);++i){
      if (it->_VECTptr->empty() || is_zero(((*(it->_VECTptr))[i]),contextptr)){
	newres.push_back(vecteur(c,zero));
      }
      else {
	newres.push_back(*it);
	++it;
      }
    }
    for (;i<c;++i)
      newres.push_back(vecteur(c,zero));
    // now tranpose newres & resize, keep the ith line if it's ith coeff is 0
    // replace 0 by -1 to get an element of the basis
    matrice restran;
    mtran(newres,restran,int(res.front()._VECTptr->size()));
    it=restran.begin();
    itend=restran.end();
    bool modular=!pivots.empty() && pivots.front().type==_MOD;
    for (int i=0;it!=itend;++it,++i){
      if (is_zero((*(it->_VECTptr))[i],contextptr)){
	(*(it->_VECTptr))[i]=modular?makemod(-1,*(pivots.front()._MODptr+1)):-1;
	v.push_back(*it);
      }
    }
    return true;
  }

  bool mker(const matrice & a,vecteur & v,GIAC_CONTEXT){
    return mker(a,v,1,contextptr);
  }

  vecteur mker(const matrice & a,GIAC_CONTEXT){
    vecteur v;
    if (!mker(a,v,contextptr))
      return vecteur(1,gendimerr(contextptr));
    return v;
  }
  gen _ker(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (!ckmatrix(a))
      return symb_ker(a);
    vecteur v;
    if (!mker(*a._VECTptr,v,contextptr))
      return vecteur(1,gendimerr(contextptr));
    return v;    
  }
  static const char _ker_s []="ker";
  static define_unary_function_eval (__ker,&giac::_ker,_ker_s);
  define_unary_function_ptr5( at_ker ,alias_at_ker,&__ker,0,true);

  bool mimage(const matrice & a, vecteur & v,GIAC_CONTEXT){
    matrice atran;
    mtran(a,atran);
    v.clear();
    gen det;
    vecteur pivots;
    matrice res;
    if (!mrref(atran,res,pivots,det,0,int(atran.size()),0,int(atran.front()._VECTptr->size()),
	  /* fullreduction */1,0,true,1,0,
	       contextptr))
      return false;
    matrice::const_iterator it=res.begin(),itend=res.end();
    for (int i=0;it!=itend;++it,++i){
      if (!is_zero(*(it),contextptr))
	v.push_back(*it);
    }
    return true;
  }

  vecteur mimage(const matrice & a,GIAC_CONTEXT){
    vecteur v;
    if (!mimage(a,v,contextptr))
      return vecteur(1,gendimerr(contextptr));
    return v;
  }

  gen _image(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (!ckmatrix(a))
      return symb_image(a);
    vecteur v;
    if (!mimage(*a._VECTptr,v,contextptr))
      return gensizeerr(contextptr);
    return v;    
  }
  static const char _image_s []="image";
  static define_unary_function_eval (__image,&giac::_image,_image_s);
  define_unary_function_ptr5( at_image ,alias_at_image,&__image,0,true);

  vecteur cross(const vecteur & v_orig,const vecteur & w_orig,GIAC_CONTEXT){
    vecteur v(v_orig),w(w_orig);
    int s1=int(v.size()),s2=int(w.size());
    bool vmat=ckmatrix(v),wmat=ckmatrix(w);
    if (vmat){
      if (s1!=1)
	v=mtran(v);
      v=*v.front()._VECTptr;
      s1=int(v.size());
    }
    if (wmat){
      if (s2!=1)
	w=mtran(w);
      w=*w.front()._VECTptr;
      s2=int(w.size());
    }
    if (s1==2){
      v.push_back(0);
      ++s1;
    }
    if (s2==2){
      w.push_back(0);
      ++s2;
    }
    if (s1!=3 || s2!=3)
      return vecteur(1,gendimerr(gettext("cross")));
    vecteur res;
    res.push_back(operator_times(v[1],w[2],contextptr)-operator_times(v[2],w[1],contextptr));
    res.push_back(operator_times(v[2],w[0],contextptr)-operator_times(v[0],w[2],contextptr));
    res.push_back(operator_times(v[0],w[1],contextptr)-operator_times(v[1],w[0],contextptr));
    if (vmat && wmat)
      return mtran(vecteur(1,res));
    return res;
  }
  /*
  vecteur cross(const vecteur & v_orig,const vecteur & w_orig){
    return cross(v_orig,w_orig,context0);
  }
  */
  gen symb_cross(const gen & arg1,const gen & arg2){
    return symbolic(at_cross,makesequence(arg1,arg2));
  }
  gen symb_cross(const gen & args){
    return symbolic(at_cross,args);
  }
  gen complex2vecteur(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT){
      gen x,y;
      reim(g,x,y,contextptr);
      return makevecteur(x,y);
    }
    return g;
  }
    
  gen cross(const gen & a,const gen & b,GIAC_CONTEXT){
    gen g1=remove_at_pnt(a);
    if (a.type==_VECT && a.subtype==_GGB__VECT)
      g1=a;
    gen g2=remove_at_pnt(b);
    if (b.type==_VECT && b.subtype==_GGB__VECT)
      g2=b;
    if (g1.type!=_VECT || g2.type!=_VECT){
      g1=complex2vecteur(g1,contextptr);      
      g2=complex2vecteur(g2,contextptr);
      if (g1._VECTptr->size()==2 && g2._VECTptr->size()==2)
	return g1._VECTptr->front()*g2._VECTptr->back()-g1._VECTptr->back()*g2._VECTptr->front();
      if (g1._VECTptr->size()==2)
	g1=makevecteur(g1._VECTptr->front(),g1._VECTptr->back(),0);
      if (g2._VECTptr->size()==2)
	g2=makevecteur(g2._VECTptr->front(),g2._VECTptr->back(),0);
    }
    if (is_undef(g1) || g1.type!=_VECT || is_undef(g2) || g2.type!=_VECT)
      return gensizeerr(gettext("cross"));
    if (g1.subtype==_VECTOR__VECT && g2.subtype==_VECTOR__VECT)
      return _vector(cross(vector2vecteur(*g1._VECTptr),g2,contextptr),contextptr);
    if (g1.subtype==_VECTOR__VECT)
      return cross(vector2vecteur(*g1._VECTptr),g2,contextptr);
    if (g2.subtype==_VECTOR__VECT)
      return cross(g1,vector2vecteur(*g2._VECTptr),contextptr);
    if (g1._VECTptr->size()==2 && g2._VECTptr->size()==2 && calc_mode(contextptr)==1)
      return g1._VECTptr->front()*g2._VECTptr->back()-g1._VECTptr->back()*g2._VECTptr->front();
    return cross(*g1._VECTptr,*g2._VECTptr,contextptr);
  }
  /*
  gen cross(const gen & a,const gen & b){
    return cross(a,b,context0);
  }
  */
  gen _cross(const gen &args,GIAC_CONTEXT){
    if (args.type==_STRNG && args.subtype==-1) return args;
    if (args.type!=_VECT)
      return symb_cross(args);
    if (args._VECTptr->size()!=2)
      return gendimerr(contextptr);
    gen res=cross(args._VECTptr->front(),args._VECTptr->back(),contextptr);
    if (res.type==_VECT)
      res.subtype=args._VECTptr->front().subtype;
    return res;
  }
  static const char _cross_s []="cross";
  string texprintascross(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    return texprintsommetasoperator(feuille," \\wedge ",contextptr);
  }
  static define_unary_function_eval4 (__cross,&giac::_cross,_cross_s,0,texprintascross);
  define_unary_function_ptr5( at_cross ,alias_at_cross,&__cross,0,true);

  static string printassize(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    string res(sommetstr);
    if (xcas_mode(contextptr)>0)
      res="nops";
    return res+"("+feuille.print(contextptr)+")";
  }
  gen symb_size(const gen & args){
    return symbolic(at_size,args);
  }
  gen _size(const gen &args,GIAC_CONTEXT){
    if (args.type==_STRNG && args.subtype==-1) return args;
    if (args.type==_STRNG)
      return (int) args._STRNGptr->size();
    if (args.type==_SYMB){
      if (args._SYMBptr->feuille.type==_VECT)
	return (int) args._SYMBptr->feuille._VECTptr->size();
      else
	return 1;
    }
    if (args.type==_POLY)
      return int(args._POLYptr->coord.size());
    if (args.type!=_VECT)
      return 1;
    return (int) args._VECTptr->size();
  }
  static const char _size_s []="size";
  static define_unary_function_eval2 (__size,&giac::_size,_size_s,&printassize);
  define_unary_function_ptr5( at_size ,alias_at_size,&__size,0,true);

#ifdef HAVE_LIBGSL  

  int vecteur2gsl_vector(const_iterateur it,const_iterateur itend,gsl_vector * w,GIAC_CONTEXT){
#ifdef DEBUG_SUPPORT
    if (itend-it!=signed(w->size))
      setsizeerr(gettext("vecteur.cc vecteur2gsl_vector"));
#endif
    gen g;
    int res=GSL_SUCCESS;
    for (int i = 0; it!=itend; ++i,++it){
      g=it->evalf(1,contextptr);
      if (g.type==_DOUBLE_)
	gsl_vector_set (w, i, g._DOUBLE_val);
      else {
	gsl_vector_set (w, i, nan());	
	res=!GSL_SUCCESS;
      }
    }
    return res;
  }

  int vecteur2gsl_vector(const vecteur & v,gsl_vector * w,GIAC_CONTEXT){
    const_iterateur it=v.begin(),itend=v.end();
    return vecteur2gsl_vector(it,itend,w,contextptr);
  }
  // this function allocate all space needed for the gsl_vector
  gsl_vector * vecteur2gsl_vector(const vecteur & v,GIAC_CONTEXT){
    const_iterateur it=v.begin(),itend=v.end();
    gsl_vector * w = gsl_vector_alloc (itend-it);
    vecteur2gsl_vector(it,itend,w,contextptr);
    return w;
  }

  // this function does not deallocate the gsl vector
  // call gsl_vector_free(v) for this
  vecteur gsl_vector2vecteur(const gsl_vector * v){
    vecteur res;
    int s=v->size;
    res.reserve(s);
    for (int i=0;i<s;++i)
      res.push_back(gsl_vector_get(v,i));
    return res;
  }

  int matrice2gsl_matrix(const matrice & m,gsl_matrix * w,GIAC_CONTEXT){
    int s1=w->size1,s2=w->size2;
#ifdef DEBUG_SUPPORT
    ckmatrix(m);
    if (mrows(m)!=s1 || mcols(m)!=s2)
      setdimerr();
#endif
    gen g;
    const_iterateur it=m.begin(),itend=m.end();
    int res=GSL_SUCCESS;
    for (int i = 0; it!=itend; ++i,++it){
      if (it->type!=_VECT)
	res=!GSL_SUCCESS;
      vecteur & v =*it->_VECTptr;
      const_iterateur jt=v.begin(),jtend=v.end();
      for (int j=0;jt!=jtend;++j,++jt){
	g=evalf(*jt,1,contextptr);
	if (g.type==_DOUBLE_)
	  gsl_matrix_set(w,i,j,g._DOUBLE_val);
	else {
	  res=!GSL_SUCCESS;
	  gsl_matrix_set(w,i,j,nan());	  
	}
      }
    }
    return res;
  }

  // this function allocate all space needed for the gsl_matrix
  gsl_matrix * matrice2gsl_matrix(const matrice & m,GIAC_CONTEXT){
    int n1=mrows(m),n2=mcols(m);
    gsl_matrix * w = gsl_matrix_alloc (n1,n2);
    matrice2gsl_matrix(m,w,contextptr);
    return w;
  }
  
  // this function does not deallocate the gsl vector
  // call gsl_matrix_free(v) for this
  matrice gsl_matrix2matrice(const gsl_matrix * v){
    matrice res;
    int s1=v->size1,s2=v->size2;
    res.reserve(s1);
    for (int i=0;i<s1;++i){
      vecteur tmp;
      tmp.reserve(s2);
      for (int j=0;j<s2;++j){
	tmp.push_back(gsl_matrix_get(v,i,j));
      }
      res.push_back(tmp);
    }
    return res;
  }

  vecteur gsl_permutation2vecteur(const gsl_permutation * p,GIAC_CONTEXT){
    int s=p->size;
    vecteur res(s);
    for (int i=0;i<s;++i)
      res[i]=(int)gsl_permutation_get(p,i)+(xcas_mode(contextptr)?1:0);
    return res;
  }
#endif // HAVE_LIBGSL

  bool mlu(const matrice & a0,vecteur & P,matrice & L,matrice & U,GIAC_CONTEXT){
    matrice a(a0);
    bool modular=false;
    if (!ckmatrix(a)){ // activate non-square matrix (instead of is_squarematrix)
      if (a.front().type==_VECT && !a.front()._VECTptr->empty() && (a.back()==at_irem || a.back()==at_ichinrem)){
	modular=true;
	a=*a.front()._VECTptr;
      }
      // if (!is_squarematrix(a)) return false; // setsizeerr(gettext("Expecting a square matrix"));
    }
    gen det;
    vecteur pivots;
    matrice res;
    int s=int(a.size()),C=int(a.front()._VECTptr->size());
    if (!mrref(a,res,pivots,det,0,s,0,C,
	  /* fullreduction */0,0,false,(modular?3:0) /* algorithm */,2 /* lu */,
	       contextptr))
      return false;
    if (pivots.empty())
      return false;
    gen tmp=pivots.back();
    if (tmp.type!=_VECT)
      return false; // setsizeerr();
    P=*tmp._VECTptr;
    // Make L and U from res
    L.reserve(s); U.reserve(s);
    for (int i=0;i<s;++i){
      vecteur & v=*res[i]._VECTptr;
      L.push_back(new ref_vecteur(s));
      vecteur & wl=*L.back()._VECTptr;
      for (int j=0;j<i && j<C;++j){ // L part
	wl[j]=v[j];
      }
      wl[i]=1;
      U.push_back(new ref_vecteur(C));
      vecteur & wu=*U.back()._VECTptr;
      for (int j=i;j<C;++j){ // U part
	wu[j]=v[j];
      }
    }
    return true;
  }

  // in: l= r rows, c cols
  // out: l= r,r and u=r rows, c cols
  void splitlu(matrice & l,matrice & u){
    u=l;
    int r,c;
    mdims(l,r,c);
    for (int i=0;i<r;++i){
      vecteur li=*l[i]._VECTptr;
      li.resize(r);
      vecteur & ui=*u[i]._VECTptr;
      for (int j=0;j<i;++j){
	ui[j]=0;
      }
      li[i]=1;
      for (int j=i+1;j<r;++j){
	li[j]=0;
      }
      l[i]=li;
    }
  }

  gen lu(const gen &args,GIAC_CONTEXT){
    if (args.type==_MAP){
      gen_map l_,u_; vector<int> permutation;
      gen l(l_),u(u_);
      if (!sparse_lu(*args._MAPptr,permutation,*l._MAPptr,*u._MAPptr))
	return gensizeerr(contextptr);
      vecteur P;
      vector_int2vecteur(permutation,P);
      return makevecteur(P,l,u);
    }
    matrice L,U,P;
    if (abs_calc_mode(contextptr)!=38){
#ifdef HAVE_LIBLAPACK
      if (ckmatrix(args) && is_fully_numeric(args) && int(args._VECTptr->size())>=CALL_LAPACK){
      
	/* DGETRF( M, N, A, LDA, IPIV, INFO ), ZGETRF( M, N, A, LDA, IPIV, INFO )
	 *  Purpose
	 *  =======
	 *
	 *  DGETRF/ZGETRF computes an LU factorization of a general M-by-N matrix A
	 *  using partial pivoting with row interchanges.
	 *
	 *  The factorization has the form
	 *     A = P * L * U
	 *  where P is a permutation matrix, L is lower triangular with unit
	 *  diagonal elements (lower trapezoidal if m > n), and U is upper
	 *  triangular (upper trapezoidal if m < n).
	 *
	 *  This is the right-looking Level 3 BLAS version of the algorithm.
	 *
	 *  Arguments
	 *  =========
	 *
	 *  M       (input) INTEGER
	 *          The number of rows of the matrix A.  M >= 0.
	 *
	 *  N       (input) INTEGER
	 *          The number of columns of the matrix A.  N >= 0.
	 *
	 *  A       (input/output) DOUBLE PRECISION/COMPLEX array, dimension (LDA,N)
	 *          On entry, the M-by-N matrix to be factored.
	 *          On exit, the factors L and U from the factorization
	 *          A = P*L*U; the unit diagonal elements of L are not stored.
	 *
	 *  LDA     (input) INTEGER
	 *          The leading dimension of the array A.  LDA >= max(1,M).
	 *
	 *  IPIV    (output) INTEGER array, dimension (min(M,N))
	 *          The pivot indices; for 1 <= i <= min(M,N), row i of the
	 *          matrix was interchanged with row IPIV(i).
	 */
	integer M,N,LDA,INFO;
	int m,n;
	mdims(*args._VECTptr,m,n);
	int mn=giacmin(m,n);
	M=m; N=n;
	LDA=M;
	integer * IPIV=new integer[mn];
	for (int i=0;i<mn;++i)
	  P.push_back(i+1);
	if (is_zero(im(args,contextptr))){
	  double * A = new double[M*N];
	  matrice2lapack(*args._VECTptr,A,contextptr);
	  dgetrf_( &M, &N, A, &LDA, IPIV, &INFO );
	  if (INFO){
	    delete [] IPIV;
	    delete [] A;
	    return gensizeerr(gettext("LAPACK LU error"));
	  }
	  lapack2matrice(A,M,N,L);
	  // get U from upper part of L and clear
	  splitlu(L,U);
	  // get P
	  for (int i=1;i<mn;++i){
	    if (IPIV[i-1]!=i)
	      swapgen(P[i-1],P[IPIV[i-1]-1]);
	  }
	  if (!xcas_mode(contextptr)){
	    for (int i=0;i<mn;++i)
	      P[i] -= 1;
	  }
	  delete [] IPIV;
	  delete [] A;
	  return gen(makevecteur(P,L,U),_SEQ__VECT);
	}
	doublef2c_complex * A = new doublef2c_complex[M*N];
	matrice2zlapack(*args._VECTptr,A,contextptr);
	zgetrf_( &M, &N, A, &LDA, IPIV, &INFO );
	if (INFO){
	  delete [] IPIV;
	  delete [] A;
	  return gensizeerr(gettext("LAPACK LU error"));
	}
	zlapack2matrice(A,M,N,L);
	// get U from upper part of L and clear
	splitlu(L,U);
	// get P
	for (int i=1;i<mn;++i){
	  if (IPIV[i-1]!=i)
	    swapgen(P[i-1],P[IPIV[i-1]-1]);
	}
	if (!xcas_mode(contextptr)){
	  for (int i=0;i<mn;++i)
	    P[i] -= 1;
	}
	delete [] IPIV;
	delete [] A;
	return gen(makevecteur(P,L,U),_SEQ__VECT);
      } // end lapack call
#endif
#ifdef HAVE_LIBGSL
    bool gsl_lu = 0 && is_fully_numeric(args) && is_zero(im(args,contextptr),contextptr);
    if (gsl_lu){
      if (!is_squarematrix(args))
	return gensizeerr(gettext("Expecting a square matrix"));
      gsl_matrix * m=matrice2gsl_matrix(*args._VECTptr,contextptr);
      int s1=m->size1;
      gsl_permutation * p=gsl_permutation_alloc (s1);
      int sign;
      gsl_linalg_LU_decomp (m, p,&sign);
      P=gsl_permutation2vecteur(p,contextptr);
      L.reserve(s1);
      U.reserve(s1);
      // get L and U
      for (int i=0;i<s1;++i){
	vecteur l(s1),u(s1);
	for (int j=0;j<i;++j){
	  l[j]=gsl_matrix_get(m,i,j);
	}
	l[i]=1.0;
	for (int j=i;j<s1;++j){
	  u[j]=gsl_matrix_get(m,i,j);
	}
	L.push_back(l);
	U.push_back(u);
      }
      gsl_permutation_free(p);
      gsl_matrix_free(m);
      return gen(makevecteur(P,L,U),_SEQ__VECT);
    }
#endif // HAVE_LIBGSL
    } // end abs_calc_mode!=38
    if (args.type!=_VECT)
      return gentypeerr(contextptr);
    // Giac LU decomposition
    if (!mlu(*args._VECTptr,P,L,U,contextptr))
      return gendimerr(contextptr);
    if (xcas_mode(contextptr) || abs_calc_mode(contextptr)==38){
      int s=int(P.size());
      for (int i=0;i<s;++i){
	P[i]=P[i]+1;
      }
    }
    return gen(makevecteur(P,L,U),_SEQ__VECT);
  }
  static const char _lu_s []="lu";
  static define_unary_function_eval (__lu,&giac::lu,_lu_s);
  define_unary_function_ptr5( at_lu ,alias_at_lu,&__lu,0,true);

  bool matrice2lapack(const matrice & m,double * A,GIAC_CONTEXT){
    const_iterateur it=m.begin(),itend=m.end();
    gen g;
    int rows=int(itend-it);
    for (int i = 0; it!=itend; ++i,++it){
      if (it->type!=_VECT)
	return false;
      vecteur & v =*it->_VECTptr;
      const_iterateur jt=v.begin(),jtend=v.end();
      for (int j = 0; jt!=jtend;++j, ++jt){
	g=evalf_double(*jt,1,contextptr);
	if (g.type==_DOUBLE_)
	  A[i + j * rows] = g._DOUBLE_val;
	else 
	  return false;
      }
    }
    return true;
  }

  void lapack2matrice(double * A,unsigned rows,unsigned cols,matrice & R){
    R.reserve(rows);
    for (unsigned i=0;i<rows;++i){
      gen tmp(new ref_vecteur(cols));
      vecteur &r=*tmp._VECTptr;
      for (unsigned j=0;j<cols;++j)
	r[j] = A[i + j * rows];
      R.push_back(tmp);
    }
  }

  gen qr(const gen &args_orig,GIAC_CONTEXT){
    gen args;
    int method=0; // use -1 to check built-in qr
#if !defined(HAVE_LIBLAPACK) || !defined (HAVE_LIBGSL)
    method=-3;
#endif
    if ( (args_orig.type==_VECT) && (args_orig._VECTptr->size()==2) && (args_orig._VECTptr->back().type==_INT_)){
      args=args_orig._VECTptr->front();
      method=args_orig._VECTptr->back().val;
    }
    else
      args=args_orig;
    if (!ckmatrix(args))
      return symbolic(at_qr,args);
    int rows = mrows(*args._VECTptr), cols = mcols(*args._VECTptr);
    if (rows < cols)
      method=-3;
    // if (!is_zero(im(args,contextptr),contextptr)) return gensizeerr(gettext("Complex entry!"));
    bool cplx=false;
    if (method<0 || !is_fully_numeric(evalf_double(args,1,contextptr)) || (cplx=!is_zero(im(args,contextptr),contextptr)) ){
      matrice r;
      if (is_fully_numeric(args)){ 
	// qr decomposition using rotations, numerically stable
	// but not suited to exact computations
	matrice h=*args._VECTptr,p(midn(int(h.size())));
	std_matrix<gen> H,P;
	matrice2std_matrix_gen(h,H);
	matrice2std_matrix_gen(p,P);
	qr_ortho(H,P,true,contextptr);
	std_matrix_gen2matrice_destroy(H,h);
	std_matrix_gen2matrice_destroy(P,p);
	if (method<=-3)
	  return makevecteur(_trn(p,contextptr),h);
	else
	  return makevecteur(_trn(p,contextptr),h,midn(int(h.size())));
      }
      // qr decomposition using GramSchmidt (not numerically stable)
      matrice res(gramschmidt(*_trn(args,contextptr)._VECTptr,r,cplx || method==-1 || method==-3,contextptr));
      if (method<=-3)
	return gen(makevecteur(_trn(res,contextptr),r),_SEQ__VECT);
      else
	return gen(makevecteur(_trn(res,contextptr),r,midn(int(r.size()))),_SEQ__VECT);
    }
#ifdef HAVE_LIBLAPACK
    if (!CAN_USE_LAPACK
	|| dgeqrf_ == NULL
	|| dorgqr_ == NULL)
      return gensizeerr(gettext("LAPACK not available"));
    
    const matrice &m = *args._VECTptr;
    integer info;
    double *A = new double[rows * cols];
    if (!matrice2lapack(m,A,contextptr))
      return gensizeerr(gettext("Lapack conversion error"));
    double *tau = new double[cols];
    integer lwork = -1;
    double worktmp;
    if (!is_zero(im(args,contextptr))){
      // complex QR decomposition, currently disabled above
    }
    // first call to determine optimum work vector size lwork
    dgeqrf_(&rows, &cols, A, &rows, tau, &worktmp, &lwork, &info);
    if (info){
      delete [] A;
      delete [] tau;
      return gensizeerr(gettext("LAPACK error ") + (-info));
    }
    
    lwork = (int)worktmp;
    double *work = new double[lwork];
    
    // second call, computes the QR-decomposition
    dgeqrf_(&rows, &cols, A, &rows, tau, work, &lwork, &info);
    if (info){
      delete [] A;
      delete [] tau;
      delete [] work;
      return gensizeerr(gettext("LAPACK error ") + (-info));
    }
    
    // load R from the upper right part of A
    matrice R;
    R.reserve(rows);
    for (int i=0;i<rows;++i){
      vecteur r(cols);
      for (int j=i;j<cols;++j)
	r[j] = A[i + j * rows];
      R.push_back(r);
    }
    
    // compute Q and store it in A
    dorgqr_(&rows, &cols, &cols, A, &rows, tau, work, &lwork, &info);
    delete [] tau;
    delete [] work;
    if (info){
      delete [] A;
      return gensizeerr(gettext("LAPACK error ") + (-info));
    }
    
    // load Q from A
    matrice Q;
    Q.reserve(rows);
    for (int i=0;i<rows;++i){
      vecteur q(cols);
      for (int j=0;j<cols;++j)
	q[j] = A[i + j * rows];
      Q.push_back(q);
    }
    
    delete [] A;
    
    // I prefer to get Q and R returned. Your mileage may vary.
    return gen(makevecteur(Q,R),_SEQ__VECT);
#endif // HAVE_LIBLAPACK
#ifdef HAVE_LIBGSL
    {
    gsl_matrix * m=matrice2gsl_matrix(*args._VECTptr,contextptr);
    int s1=m->size1,s2=m->size2;
    gsl_vector * tau=gsl_vector_alloc(giacmin(s1,s2));
    gsl_linalg_QR_decomp (m,tau);
    matrice R;
    R.reserve(s1);
    // get R
    for (int i=0;i<s1;++i){
      vecteur r(s2);
      for (int j=i;j<s2;++j){
	r[j]=gsl_matrix_get(m,i,j);
      }
      R.push_back(r);
    }
    // get the list of tau_i,v_i
    vecteur Q;
    for (int i=0;i<signed(tau->size);++i){
      vecteur tmp(m->size2);
      tmp[i]=1.0;
      for (int j=i+1;j<signed(m->size2);++j)
	tmp[j]=gsl_matrix_get(m,j,i);
      Q.push_back(makevecteur(gsl_vector_get(tau,i),tmp));
    }
    gsl_vector_free(tau);
    gsl_matrix_free(m);
    return gen(makevecteur(Q,R),_SEQ__VECT);
    //return R;
    }
#endif // HAVE_LIBGSL

    return symbolic(at_qr,args);
  }
  static const char _qr_s []="qr";
  static define_unary_function_eval (__qr,&giac::qr,_qr_s);
  define_unary_function_ptr5( at_qr ,alias_at_qr,&__qr,0,true);

  matrice thrownulllines(const matrice & res){
    int i=int(res.size())-1;
    for (;i>=0;--i){
      if (!is_zero(res[i],context0))
	break;
    }
    return vecteur(res.begin(),res.begin()+i+1);
  }
  gen _basis(const gen &args,GIAC_CONTEXT){
    if (args.type==_STRNG && args.subtype==-1) return args;
    if (!ckmatrix(args))
      return symbolic(at_basis,args);
    matrice res=mrref(*args._VECTptr,contextptr);
    return gen(thrownulllines(res),_SET__VECT);
  }
  static const char _basis_s []="basis";
  static define_unary_function_eval (__basis,&giac::_basis,_basis_s);
  define_unary_function_ptr5( at_basis ,alias_at_basis,&__basis,0,true);

  void sylvester(const vecteur & v1,const vecteur & v2,matrice & res){
    int m=int(v1.size())-1;
    int n=int(v2.size())-1;
    if (m<0 || n<0){
      res.clear(); return;
    }
    res.resize(m+n);
    for (int i=0;i<n;++i){
      res[i]=new ref_vecteur(m+n);
      vecteur & w=*res[i]._VECTptr;
      for (int j=0;j<=m;++j)
	w[i+j]=v1[j];
    }
    for (int i=0;i<m;++i){
      res[n+i]=new ref_vecteur(m+n);
      vecteur & w=*res[n+i]._VECTptr;
      for (int j=0;j<=n;++j)
	w[i+j]=v2[j];
    }
  }

  // Sylvester matrix, in lines line0=v1 0...0, line1=0 v1 0...0, etc.
  matrice sylvester(const vecteur & v1,const vecteur & v2){
    matrice res;
    sylvester(v1,v2,res);
    return res;
  }

  gen _sylvester(const gen &args,GIAC_CONTEXT){
    if (args.type==_STRNG && args.subtype==-1) return args;
    if (args.type!=_VECT || args._VECTptr->size()<2)
      return gensizeerr(contextptr);
    vecteur & v = *args._VECTptr;
    gen x(vx_var);
    if (v.size()>2)
      x=v[2];
    gen p1(_e2r(makesequence(v[0],x),contextptr));
    gen p2(_e2r(makesequence(v[1],x),contextptr));
    if (p1.type==_FRAC)
      p1=inv(p1._FRACptr->den,contextptr)*p1._FRACptr->num;
    if (p2.type==_FRAC)
      p2=inv(p2._FRACptr->den,contextptr)*p2._FRACptr->num;
    if (p1.type!=_VECT || p2.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v1 =*p1._VECTptr;
    vecteur & v2 =*p2._VECTptr;
    return sylvester(v1,v2);
  }
  static const char _sylvester_s []="sylvester";
  static define_unary_function_eval (__sylvester,&giac::_sylvester,_sylvester_s);
  define_unary_function_ptr5( at_sylvester ,alias_at_sylvester,&__sylvester,0,true);

  gen _ibasis(const gen &args,GIAC_CONTEXT){
    if (args.type==_STRNG && args.subtype==-1) return args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=2) )
      return symbolic(at_basis,args);
    gen g=args._VECTptr->front(),h=args._VECTptr->back();
    if (!ckmatrix(g) || !ckmatrix(h))
      return gensizeerr(contextptr);
    vecteur & v1=*g._VECTptr;
    vecteur & v2=*h._VECTptr;
    if (v1.empty() || v2.empty())
      return vecteur(0);
    vecteur v=mker(mtran(mergevecteur(v1,v2)),contextptr);
    if (is_undef(v)) return v;
    // if v is not empty compute each corresponding vector of the basis
    int s=int(v1.size());
    int l=int(v1.front()._VECTptr->size());
    matrice res;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      vecteur tmp(l);
      vecteur & i=*it->_VECTptr;
      for (int j=0;j<s;++j)
	tmp=addvecteur(tmp,multvecteur(i[j],*v1[j]._VECTptr));
      res.push_back(tmp);
    }
    return gen(thrownulllines(mrref(res,contextptr)),_SET__VECT);
  }
  static const char _ibasis_s []="ibasis";
  static define_unary_function_eval (__ibasis,&giac::_ibasis,_ibasis_s);
  define_unary_function_ptr5( at_ibasis ,alias_at_ibasis,&__ibasis,0,true);

  void sort_eigenvals(matrice & p,matrice & d,bool ascend,GIAC_CONTEXT){
    matrice pt; mtran(p,pt);
    vecteur D; D.reserve(d.size());
    for (int i=0;i<int(d.size());++i){
      gen tmp=makevecteur(d[i][i],pt[i]);
      D.push_back(tmp);
    }
    gen_sort_f_context(D.begin(),D.end(),complex_sort,contextptr);
    if (!ascend)
      reverse(D.begin(),D.end());
    for (int i=0;i<int(D.size());++i){
      gen tmp=D[i];
      (*d[i]._VECTptr)[i]=tmp[0];
      pt[i]=tmp[1];
    }
    mtran(pt,p);
  }

  gen _svd(const gen &args_orig,GIAC_CONTEXT){
    if (args_orig.type==_STRNG && args_orig.subtype==-1) return args_orig;
    gen args;
    int method=0; // use -1 to check built-in svd, -2 for svl (singular values only) 
    if ( (args_orig.type==_VECT) && (args_orig._VECTptr->size()==2) && (args_orig._VECTptr->back().type==_INT_)){
      args=args_orig._VECTptr->front();
      method=args_orig._VECTptr->back().val;
    }
    else
      args=args_orig;
    if (!ckmatrix(args))
      return symbolic(at_svd,args);
    // if (!is_zero(im(args,contextptr),contextptr)) return gensizeerr(gettext("Complex entry!"));
    if (!has_num_coeff(args))
      *logptr(contextptr) << gettext("Warning: svd is implemented for numeric matrices") << endl;
    gen argsf=args;
    bool real=is_zero(im(argsf,contextptr));
    if (real && method>=0 && is_fully_numeric( (argsf=evalf_double(args,1,contextptr)) )){
#ifdef HAVE_LIBLAPACK
      if (!CAN_USE_LAPACK
	  || dgeqrf_ == NULL
	  || dorgqr_ == NULL)
	return gensizeerr(gettext("LAPACK not available"));
      
      const matrice &m = *args._VECTptr;
      gen g;
      // const_iterateur it=m.begin(),itend=m.end();
      integer rows = mrows(m), cols = mcols(m);
      if (rows < cols)
	return gendimerr(contextptr);
      double *A = new double[rows * cols];
      matrice2lapack(m,A,contextptr);
      integer info;
      char jobU = 'A', jobVT = 'A';
      double *S = new double[cols];
      double *U = new double[rows * rows];
      double *VT = new double[cols * cols];
      integer lwork = -1;
      double worktmp;
      
      // first call to determine optimum work vector size lwork
      dgesvd_(&jobU, &jobVT, &rows, &cols, A, &rows, S, U, &rows, VT, &cols, &worktmp, &lwork, &info);
      if (info){
	delete [] A;
	delete [] S;
	delete [] U;
	delete [] VT;
	return gensizeerr(gettext("LAPACK error ") + (-info));
      }
      
      lwork = (int)worktmp;
      double *work = new double[lwork];
      
      // second call, computes the SVD
      dgesvd_(&jobU, &jobVT, &rows, &cols, A, &rows, S, U, &rows, VT, &cols, work, &lwork, &info);
      if (info){
	delete [] A;
	delete [] S;
	delete [] U;
	delete [] VT;
	delete [] work;
	setsizeerr(gettext("LAPACK error ") + (-info));
      }
      
      // load S
      vecteur s(cols);
      for (int j=0;j<cols;++j)
	s[j] = S[j];
      
      // load U
      matrice mU;
      lapack2matrice(U,rows,rows,mU);
      
      // load VT
      matrice mVT;
      lapack2matrice(VT,cols,cols,mVT);
      
      delete [] A;
      delete [] S;
      delete [] U;
      delete [] VT;
      delete [] work;
      
      return gen(makevecteur(mU,s,mtran(mVT)),_SEQ__VECT);
#endif // HAVE_LIBLAPACK
#ifdef HAVE_LIBGSL
      if (1){   
	gsl_matrix * u=matrice2gsl_matrix(*args._VECTptr,contextptr);
	int s1=u->size1,s2=u->size2;
	gsl_vector * work=gsl_vector_alloc (s1);
	gsl_matrix * v=gsl_matrix_alloc(s2,s2);
	gsl_vector * s=gsl_vector_alloc(s1);
	gsl_matrix * x=gsl_matrix_alloc(s1,s1);
	switch(method){
	case _GOLUB_REINSCH_MOD_DECOMP:
	  gsl_linalg_SV_decomp_mod(u,x,v,s,work);
	  break;
	case _JACOBI_DECOMP:
	  gsl_linalg_SV_decomp_jacobi(u,v,s);
	  break;
	default:
	  gsl_linalg_SV_decomp (u, v,s,work);
	  break;
	}
	gsl_vector_free(work);
	gsl_matrix_free(x);
	matrice U(gsl_matrix2matrice(u)),S(gsl_vector2vecteur(s)),V(gsl_matrix2matrice(v)); // A=U*S*tran(V)
	gsl_matrix_free(u);
	gsl_matrix_free(v);
	gsl_vector_free(s);
	return gen(makevecteur(U,S,V),_SEQ__VECT); // M=U*diag(S)*tran(V)
      }
#endif // HAVE_LIBGSL
    }
    // non numeric code/also for complex
    if (!ckmatrix(argsf))
      return gensizeerr(contextptr);
    if (!lidnt(argsf).empty())
      *logptr(contextptr) << "Warning: SVD for symbolic matrix may fail!" << endl;
    matrice M=*argsf._VECTptr;
    bool transposed=M.size()<M.front()._VECTptr->size();
    if (transposed){
      gen tM=_trn(M,contextptr);
      if (!ckmatrix(tM))
	return gensizeerr(contextptr);
      M=*tM._VECTptr;
    }
    matrice tMM,p,d,Mp,invs,u;     vecteur svl;
    gen tMg=_trn(M,contextptr); // mtrn(*args._VECTptr,tm);
    if (!ckmatrix(tMg))
      return gensizeerr(contextptr);
    const matrice & tM=*tMg._VECTptr;
    if (M==tM){
      if (!egv(M,p,d,contextptr,true,false,false))
	return gensizeerr(contextptr);
      mtran(p,u);
      for (unsigned i=0;i<d.size();++i){
	vecteur vi=*d[i]._VECTptr;
	gen & di=vi[i];
	di=re(di,contextptr);
	if (is_strictly_positive(-di,contextptr))
	  u[i]=-u[i];
	svl.push_back(abs(di,contextptr));
      }
      if (method==-2)
	return svl;
      return gen(makevecteur(mtran(u),svl,p),_SEQ__VECT);
    }
    mmult(tM,M,tMM);
    if (!egv(tMM,p,d,contextptr,true,false,false))
      return gensizeerr(contextptr);
    // put 0 egvl at the beginning
    sort_eigenvals(p,d,true,contextptr);
    // should reorder eigenvalue (decreasing order)
    int s=int(d.size());
    gen svdmax2=d[s-1][s-1];
    gen eps=epsilon(contextptr);
    int smallsvl=0;
#if 1
    gen smalleps=(s*s)*eps*svdmax2;
#else
    for (int i=0;i<s-1;++i){
      if (is_greater(sqrt(eps,contextptr)*svdmax2,d[i][i],contextptr))
	++smallsvl;
      else
	break;
    }
    gen smalleps=s*pow(eps,inv(smallsvl?smallsvl:1,contextptr),contextptr)*svdmax2;
#endif
    for (int i=0;i<s;++i){
      vecteur vi=*d[i]._VECTptr;
      gen & di=vi[i];
      di=re(di,contextptr);
      // replace this value by 0 if it is small
      if (is_greater(smalleps,di,contextptr)) {
	di=0.0; smallsvl++;
      }
      di=sqrt(di,contextptr);
      svl.push_back(di);
      d[i]=vi;
    }
     if (smallsvl)
       *logptr(contextptr) << "Warning, ill-conditionned matrix, " << smallsvl << " small singular values were replaced by 0. Result is probably wrong." << endl;    
    if (method==-2){
      if (transposed){
	int add0=int(M.size()-M.front()._VECTptr->size());
	for (int i=0;i<add0;++i)
	  svl.push_back(0);
      }
      return svl;
    }
    mmult(M,p,Mp);
#if 0
    invs=d;
    for (int i=0;i<s;++i){
      invs[i]=*d[i]._VECTptr;
      gen & tmp=(*invs[i]._VECTptr)[i];
      tmp=inv(tmp,contextptr);
    }
    mmult(Mp,invs,u); 
    int complete=u.size()-u.front()._VECTptr->size();
    if (complete>0){
      // complete u to a unitary matrix by adding columns
      matrice tu;
      unsigned n=u.size();
      // take random vectors from canonical basis
      while (1){
	tu=*_trn(u,contextptr)._VECTptr;
	vector<int> v(n);
	for (unsigned i=0;i<n;++i)
	  v[i]=i;
	for (int i=0;i<complete;++i){
	  int j=int((double(std::rand())*v.size())/RAND_MAX);
	  vecteur tmp(n);
	  tmp[v[j]]=1;
	  tu.push_back(tmp);
	  v.erase(v.begin()+j);
	}
	gen uqr=qr(makesequence(_trn(tu,contextptr),-1),contextptr);
	if (uqr.type==_VECT && uqr._VECTptr->size()>=2 && is_squarematrix(uqr._VECTptr->front()) &&is_squarematrix((*uqr._VECTptr)[1]) ){
	  u=*uqr._VECTptr->front()._VECTptr;
	  tu=*_trn(u,contextptr)._VECTptr;
	  vecteur r=*(*uqr._VECTptr)[1]._VECTptr;
	  for (unsigned i=0;i<n;++i){
	    tu[i]=divvecteur(*tu[i]._VECTptr,r[i][i]);
	  }
	  u=*_trn(tu,contextptr)._VECTptr;
	  break;
	}
      }
    }
#else
    // M=u*s*trn(q), u and q unitary => tM*M=q*s^2*trn(q)
    // here tM*M=p*d^2*trn(p) so q=p is known, and u*s=M*q
    // since s is diagonal, u is obtained by dividing columns j of Mp by s[j]
    mtran(Mp,u);
    for (int i=0;i<s;++i){
      gen tmp=(*d[i]._VECTptr)[i];
      if (is_zero(tmp,contextptr)){ //is_greater(1e-8,tmp/(s*svdmax),contextptr)){
	tmp=l2norm(*u[i]._VECTptr,contextptr);
      }
      tmp=inv(tmp,contextptr);
      u[i]=tmp*u[i];
    }
    reverse(u.begin(),u.end()); // put 0 SVD at the end
    mtran(u,Mp);
    // qr call required if 0 is a singular value
    gen tmp=qr(makesequence(Mp,-1),contextptr);
    if (tmp.type!=_VECT || tmp._VECTptr->size()!=3 || !ckmatrix(tmp._VECTptr->front()) || !ckmatrix(tmp[1]))
      return gensizeerr(contextptr);
    u=*tmp[0]._VECTptr;
    mtran(u,Mp);
    u=*tmp[1]._VECTptr;
    for (unsigned i=0;i<unsigned(u.size()) && int(i)<s;++i){
      if (is_strictly_positive(-u[i][i],contextptr))
	Mp[i]=-Mp[i];
    }
    reverse(Mp.begin(),Mp.begin()+s);
    mtran(Mp,u);
#endif
    if (transposed)
      return gen(makevecteur(p,svl,u),_SEQ__VECT); 
    return gen(makevecteur(u,svl,p),_SEQ__VECT); 
  }
  static const char _svd_s []="svd";
  static define_unary_function_eval (__svd,&giac::_svd,_svd_s);
  define_unary_function_ptr5( at_svd ,alias_at_svd,&__svd,0,true);

  gen _cholesky(const gen &_args,GIAC_CONTEXT){
    if (_args.type==_STRNG && _args.subtype==-1) return _args;
    if (!is_squarematrix(_args))
      return gensizeerr(contextptr);
    gen args;
    if (_args==_trn(_args,contextptr))
      args=_args;
    else
      args=(_args+_trn(_args,contextptr))/2;
#ifdef HAVE_LIBGSL
    if (is_fully_numeric(args) && is_zero(im(args,contextptr),contextptr)){
      gsl_matrix * m=matrice2gsl_matrix(*args._VECTptr,contextptr);
      int s1=m->size1;
      int i=gsl_linalg_cholesky_decomp (m);
      if (i==GSL_EDOM)
	return gensizeerr(gettext("Non positive definite"));
      // clear upper part
      for (i=0;i<s1;++i){
	for (int j=i+1;j<s1;++j)
	  gsl_matrix_set(m,i,j,0.0);
      }
      matrice LL(gsl_matrix2matrice(m));
      gsl_matrix_free(m);
      return LL;
    }
#endif // HAVE_LIBGSL
    matrice &A=*args._VECTptr;
    int n=int(A.size()),j,k,l;
    std_matrix<gen> C(n,vecteur(n));
    for (j=0;j<n;j++) {
      gen s;
      for (l=j;l<n;l++) {
	s=0;
	for (k=0;k<j;k++) {
	  if (is_zero(C[k][k],contextptr)) 
	    return gensizeerr(gettext("Not invertible matrice"));
	  //if (is_strictly_positive(-C[k][k])) setsizeerr(gettext("Not a positive define matrice"));
	  s=s+C[l][k]*conj(C[j][k],contextptr)/C[k][k];
	}
	C[l][j]=ratnormal(A[l][j]-s,contextptr);
      }
    }
    for (k=0;k<n;k++) {
      gen c=normal(inv(sqrt(C[k][k],contextptr),contextptr),contextptr);
      for (j=k;j<n;j++) {
	C[j][k]=C[j][k]*c;
      }
    }
    matrice Cmat;
    std_matrix_gen2matrice_destroy(C,Cmat);
    return Cmat;
/*
    matrice & A = *args._VECTptr;
    int n=A.size(),j,k,l;
    // Use LU decomposition without line permutation
    matrice LU,pivots;
    gen det;
    mrref(A,LU,pivots,det,0,n,0,n,false,0,false,false,3,contextptr);
    if (is_zero(det)) return gensizeerr("Not a positive defined matrix");
    matrice D,L;
    for (int i=0;i<n;++i){
      vecteur v(n);
      v[i]=sqrt(LU[i][i]);
      D.push_back(v);
      vecteur w(n);
      w[i]=1;
      for (j=0;j<i;j++)
	w[j]=LU[i][j];
      L.push_back(w);
    }
    return ckmultmatvecteur(L,D);
*/
    /*
    std_matrix<gen> C(n,vecteur(n));
    for (j=0;j<n;++j){
      gen s;
      for (k=0;k<j;++k){
	s=s+pow(C[j][k],2);
      }
      gen c2=A[j][j]-s;
      if (is_strictly_positive(-c2,contextptr))
	return gensizeerr(contextptr"Not a positive defined matrix");
      gen c=normal(sqrt(c2,contextptr),contextptr);
      C[j][j]=c;
      for (l=j+1;l<n;++l){
	s=0;
	for (k=0;k<j;++k)
	  s=s+C[l][k]*C[j][k];
	C[l][j]=normal((A[l][j]-s)/c,contextptr);
      }
    }
    matrice Cmat;
    std_matrix_gen2matrice(C,Cmat);
    return Cmat;
    */
  }
  static const char _cholesky_s []="cholesky";
  static define_unary_function_eval (__cholesky,&giac::_cholesky,_cholesky_s);
  define_unary_function_ptr5( at_cholesky ,alias_at_cholesky,&__cholesky,0,true);

  gen l2norm(const vecteur & v,GIAC_CONTEXT){
    const_iterateur it=v.begin(),itend=v.end();
    gen res,r,i;
    for (;it!=itend;++it){
      reim(*it,r,i,contextptr);
      res += r*r+i*i;
    }
    return sqrt(res,contextptr);
  }

  matrice gramschmidt(const matrice & m,matrice & r,bool normalize,GIAC_CONTEXT){
    r.clear();
    vecteur v(m);
    int s=int(v.size());
    if (!s)
      return v;
    vecteur sc(1,dotvecteur(*conj(v[0],contextptr)._VECTptr,*v[0]._VECTptr));
    if (is_zero(sc.back()))
      return v;
    vecteur rcol0(s);
    rcol0[0]=1;
    r.push_back(rcol0);
    for (int i=1;i<s;++i){
      gen cl,coeff;
      vecteur rcol(s);
      rcol[i]=1;
      for (int j=0;j<i;++j){
	coeff=rdiv(dotvecteur(*conj(v[j],contextptr)._VECTptr,*v[i]._VECTptr),sc[j],contextptr);
	cl=cl+coeff*v[j];
	rcol[j]=coeff;
      }
      v[i]=v[i]-cl;
      sc.push_back(dotvecteur(*conj(v[i],contextptr)._VECTptr,*v[i]._VECTptr));
      r.push_back(rcol);
      if (is_zero(sc.back(),contextptr))
	break;
    }
    r=mtran(*conj(r,contextptr)._VECTptr); // transconjugate
    if (normalize){
      gen coeff;
      for (int i=0;i<s;++i){
	if (is_zero(sc[i],contextptr))
	  break;
	coeff=sc[i]=sqrt(sc[i],contextptr);
	v[i]=rdiv(v[i],coeff,contextptr);
      }
      for (int i=0;i<s;++i){
	if (is_zero(sc[i],contextptr))
	  break;
	r[i]=sc[i]*r[i];
      }
    }
    return v;
  }

  matrice gramschmidt(const matrice & m,bool normalize,GIAC_CONTEXT){
    matrice r;
    return gramschmidt(m,r,normalize,contextptr);
  }

  // lll decomposition of M, returns S such that S=A*M=L*O
  // L is lower and O is orthogonal
  matrice lll(const matrice & M,matrice & L,matrice & O,matrice &A,GIAC_CONTEXT){
    if (!ckmatrix(M))
      return vecteur(1,gensizeerr(contextptr));
    matrice res(M);
    int n=int(res.size());
    if (!n)
      return res;
    int c=int(res[0]._VECTptr->size());
    if (c<n)
      return vecteur(1,gendimerr(contextptr));
    A=midn(c);
    A=vecteur(A.begin(),A.begin()+n);
    int k=0;
    for (;k<n;){
      if (!k){ // push first vector
	vecteur tmp(c);
	tmp[0]=1;
	L.push_back(tmp);
	O.push_back(res.front());
	++k;
	continue;
      }
      // Find new vector in L,O
      vecteur tmp(c);
      gen Otmp(res[k]);
      for (int j=0;j<k;++j){
	// tmp[j]=dotvecteur(res[j],res[k])/dotvecteur(res[j],res[j]);
	tmp[j]=dotvecteur(conj(O[j],contextptr),Otmp)/dotvecteur(conj(O[j],contextptr),O[j]);
	Otmp=subvecteur(*Otmp._VECTptr,multvecteur(tmp[j],*O[j]._VECTptr));
      }
      tmp[k]=1;
      L.push_back(tmp);
      O.push_back(Otmp);
      // Compare norm of O[k] and O[k-1]
      for (int j=k-1;j>=0;--j){
	gen alpha=dotvecteur(conj(O[j],contextptr),res[k])/dotvecteur(conj(O[j],contextptr),O[j]);
	alpha=_round(alpha,contextptr);
	res[k]=subvecteur(*res[k]._VECTptr,multvecteur(alpha,*res[j]._VECTptr));
	A[k]=subvecteur(*A[k]._VECTptr,multvecteur(alpha,*A[j]._VECTptr));
	L[k]=subvecteur(*L[k]._VECTptr,multvecteur(alpha,*L[j]._VECTptr));
      }
      gen lastalpha=dotvecteur(conj(O[k-1],contextptr),res[k])/dotvecteur(conj(O[k-1],contextptr),O[k-1]);
      if (ck_is_greater(dotvecteur(conj(O[k],contextptr),O[k]),(gen(3)/4-lastalpha*lastalpha)*dotvecteur(conj(O[k-1],contextptr),O[k-1]),contextptr)){
	// Ok, continue the reduction
	++k;
      }
      else {
	swapgen(res[k],res[k-1]);
	swapgen(A[k],A[k-1]);
	--k;
	L.pop_back();
	L.pop_back();
	O.pop_back();
	O.pop_back();
      }
    }
    return res;
  }
  matrice lll(const matrice & m,GIAC_CONTEXT){
    matrice L,O,A;
    return lll(m,L,O,A,contextptr);
  }
  gen _lll(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    matrice L,O,A;
    matrice S=lll(*g._VECTptr,L,O,A,contextptr);
    return gen(makevecteur(S,A,L,O),_SEQ__VECT);
  }
  static const char _lll_s []="lll";
  static define_unary_function_eval (__lll,&_lll,_lll_s);
  define_unary_function_ptr5( at_lll ,alias_at_lll,&__lll,0,true);

  // Utilities for Hermite and Smith normal forms
  static gen rem(const gen & p,const gen & q,environment * env){
    if (is_zero(p))
      return p;
    if (!env)
      return smod(p,q);
    if (p.type==_POLY || q.type==_POLY){
      if (p.type!=_POLY)
	return p;
      if (q.type!=_POLY)
	return 0;
      return *p._POLYptr % *q._POLYptr;
    }
    vecteur R=operator_mod(gen2vecteur(p),gen2vecteur(q),env);
    if (R.size()==1)
      return R.front();
    else
      return gen(R,_POLY1__VECT);
  }

  static gen quo(const gen & p,const gen & q,environment * env){
    if (is_zero(p))
      return p;
    if (!env)
      return (p-smod(p,q))/q;
    if (p.type==_POLY || q.type==_POLY){
      if (p.type!=_POLY)
	return zero;
      if (q.type!=_POLY)
	return q;
      return *p._POLYptr % *q._POLYptr; // FIXME mod computation
    }
    vecteur Q=operator_div(gen2vecteur(p),gen2vecteur(q),env);
    if (Q.size()==1)
      return Q.front();
    else
      return gen(Q,_POLY1__VECT);
  }

  // w=(c1*v1+c2*v2)/c smod modulo
  void modlinear_combination(const gen & c1,const vecteur & v1,const gen & c2,const vecteur & v2,const gen & c,vecteur & w,environment * env,int cstart,int cend){
    const_iterateur it1=v1.begin()+cstart,it1end=v1.end();
    if (cend && cend>=cstart && cend<it1end-v1.begin())
      it1end=v1.begin()+cend;
    const_iterateur it2=v2.begin()+cstart;
    iterateur jt=w.begin()+cstart;
    gen modulo=env->modulo;
    for (;it1!=it1end;++it1,++it2,++jt){
      *jt=smod(c1*(*it1)+c2*(*it2),modulo);
      *jt=quo(*jt,c,env);
    }
  }

  static void egcd(const gen & a,const gen & b,gen & u,gen & v,gen & d,environment * env){
    if (!env){
      egcd(a,b,u,v,d);
      return ;
    }
    if (is_zero(a)){
      u=0; v=1; d=b; return;
    }
    if (is_zero(b)){
      u=1; v=0; d=a; return;
    }
    if (a.type==_POLY || b.type==_POLY){
      if (a.type!=_POLY){
	if (env && env->moduloon){
	  d=1;
	  u=invmod(a,env->modulo);
	}
	else {
	  d=a;
	  u=plus_one;
	}
	v=zero;
	return;
      }
      if (b.type!=_POLY){
	if (env && env->moduloon){
	  d=1;
	  v=invmod(b,env->modulo);
	}
	else {
	  d=b;
	  v=plus_one;
	}
	u=zero;
	return;
      }
      polynome U,V,D;
      egcd(*a._POLYptr,*b._POLYptr,U,V,D);
      u=U; v=V; d=D;
      return;
    }
    if (a.type!=_VECT){
      if (env && env->moduloon){
	d=1;
	u=invmod(a,env->modulo);
      }
      else {
	d=a;
	u=plus_one;
      }
      v=zero;
      return;
    }
    if (b.type!=_VECT){
      if (env && env->moduloon){
	d=1;
	v=invmod(b,env->modulo);
      }
      else {
	d=b;
	v=plus_one;
      }
      u=zero;
      return;
    }
    modpoly U,V,D;
    egcd(*a._VECTptr,*b._VECTptr,env,U,V,D);
    if (U.size()==1)
      u=U.front();
    else
      u=gen(U,_POLY1__VECT); 
    if (V.size()==1)
      v=V.front();
    else
      v=gen(V,_POLY1__VECT); 
    if (D.size()==1)
      d=D.front();
    else
      d=gen(D,_POLY1__VECT);
  }

  // degree + 1 for poly, abs for integer, 1 otherwise
  static gen smith_deg(const gen & a,environment * env,GIAC_CONTEXT){
    if (!env)
      return abs(a,contextptr); 
    if (a.type!=_VECT)
      return is_zero(a,contextptr)?zero:plus_one;
    return int(a._VECTptr->size());
  }


  // If Aorig has integer coefficients, hermite
  // finds U and A such that A=U*Aorig with U invertible in Z and A
  // is upper triangular, with non zero coeff || <= |pivot|/2
  bool hermite(const std_matrix<gen> & Aorig,std_matrix<gen> & U,std_matrix<gen> & A,environment * env,GIAC_CONTEXT){
    A=Aorig;
    int n=int(A.size());
    if (!n) return false;
    int m=int(A.front().size());
    matrice2std_matrix_gen(midn(n),U);
    gen u,v,d;
    vecteur B1(n),B2(m);
    int i0=0;
    for (int j=0;j<m ;j++ ){
      // Find non zero entry of smallest abs value in column j
      int k=-1;
      gen min_val=plus_inf,tmp,q;
      for (int i=i0;i<n;++i){
	tmp=smith_deg(A[i][j],env,contextptr);
	if (!is_zero(tmp,contextptr) && is_strictly_greater(min_val,tmp,contextptr)){
	  k=i;
	  min_val=tmp;
	}
      }
      if (k>=0 && !is_zero(min_val,contextptr)){
	if (i0!=k){ // Exchange lines i0 and k in A and U
	  swap(A[i0],A[k]);
	  swap(U[i0],U[k]);
	}
	for (int i=n-1;i>=0;--i){
	  if (i==i0 || is_zero(A[i][j],contextptr) )
	    continue;
	  if (i<i0){
	    // Above diag do: L_i <- L_i - q*L_j
	    q=quo(A[i][j],A[i0][j],env);
	    linear_combination(plus_one,U[i],-q,U[i0],plus_one,1,U[i],0.0,0);
	    linear_combination(plus_one,A[i],-q,A[i0],plus_one,1,A[i],0.0,0);
	  }
	  else {
	    // Below diag: we use Bezout u*a+v*b=d where a=coeff, b="pivot"
	    // L_i0 <- v*L_i0 + u*L_i
	    // L_i <- (-a * L_i0 + b * L_i)/d
	    // This transformation is Z-invertible since det=(u*a+b*v)/d=1
	    // it will cancel the leading coeff of L_i
	    // We should use the smallest possible |u| and |v|
	    gen a = A[i][j];
	    gen b = A[i0][j];
	    egcd(a,b,u,v,d,env);
	    if (env && env->moduloon){
	      modlinear_combination(v,U[i0],u,U[i],plus_one,B1,env,0,0);
	      modlinear_combination(-a,U[i0],b,U[i],d,U[i],env,0,0);
	      modlinear_combination(v,A[i0],u,A[i],plus_one,B2,env,0,0);
	      modlinear_combination(-a,A[i0],b,A[i],d,A[i],env,0,0);
	    }
	    else {
	      linear_combination(v,U[i0],u,U[i],plus_one,1,B1,0.0,0);
	      linear_combination(-a,U[i0],b,U[i],d,1,U[i],0.0,0);
	      linear_combination(v,A[i0],u,A[i],plus_one,1,B2,0.0,0);
	      linear_combination(-a,A[i0],b,A[i],d,1,A[i],0.0,0);
	    }
	    U[i0]=B1;
	    A[i0]=B2;	    
	  }
	} // end for (column reduced)
	// CERR << A << endl;
	if (!env && is_strictly_positive(-A[i0][i0],contextptr)){ 
	  A[i0]=-A[i0];
	  U[i0]=-U[i0];
	}
	++i0;
      }
    }
    return true;
  }

  // fonction ihermite
  // Forme normale de Hermite pour une matrice a coeff entiers
  // effectue la reduction sous forme echelonnee (de type Gauss)
  // d'une matrice d'entiers en utilisant uniquement des operations
  // de lignes inversibles dans les entiers, en d'autres termes si A0
  // est la matrice originale, on calcule une matrice U inversible dans Z
  // et une matrice A triangulaire superieure telles que
  //   A = U*A0
  // De plus les coefficients au-dessus de la diagonale de A sont en module
  // inferieurs au pivot de la colonne /2 .
  // exemple
  // A0:=[[9,-36,30], [-36,192,-180], [30,-180,180]];
  // U,A:=ihermite(A0);
  // U*A0-A (renvoie 0)
  // det(U) = 1 donc on passe aussi de A a A0 uniquement avec des
  // manipulations de ligne a coeffs entiers
  // Application: calcul d'une Z-base d'un noyau
  // Soit M la matrice dont on cherche le noyau
  // U,A:=ihermite(transpose(M)) -> A=U*transpose(M)
  // -> transpose(A)=M*transpose(U)
  // les colonnes nulles de transpose(A) correspondent aux colonnes 
  // de transpose(U) dans Ker(M) -> les lignes nulles de A aux lignes de U
  // dans le noyau. 
  // Exemple: M:=[[1,2,3],[4,5,6],[7,8,9]]
  // U,A:=ihermite(M) renvoie
  // [[-3,1,0],[4,-1,0],[-1,2,-1]],[[1,-1,-3],[0,3,6],[0,0,0]]
  // A[2]==0 donc base de Ker(M) composee de U[2], on a bien
  // M*U[2]==0

  bool ihermite(const matrice & Aorig, matrice & U,matrice & A,GIAC_CONTEXT){
    std_matrix<gen> aorig,u,a;
    matrice2std_matrix_gen(Aorig,aorig);
    if (!hermite(aorig,u,a,0,contextptr))
      return false;
    std_matrix_gen2matrice_destroy(u,U);
    std_matrix_gen2matrice_destroy(a,A);
    return true;
  }

  gen _ihermite(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    if (!is_integer_matrice(*g._VECTptr))
      return gensizeerr(gettext("Integer matrix expected"));
    matrice U,A;
    if (!ihermite(*g._VECTptr,U,A,contextptr))
      return gensizeerr(contextptr);
    // if (abs_calc_mode(contextptr)==38) return makevecteur(U,A);
    return gen(makevecteur(U,A),_SEQ__VECT);
  }
  static const char _ihermite_s []="ihermite";
  static define_unary_function_eval (__ihermite,&_ihermite,_ihermite_s);
  define_unary_function_ptr5( at_ihermite ,alias_at_ihermite,&__ihermite,0,true);

  // A=U*Aorig*V, U and V Z-invertible, A diagonal, A[i,i] divides A[i+1,i+1]
  bool smith(const std_matrix<gen> & Aorig,std_matrix<gen> & U,std_matrix<gen> & A,std_matrix<gen> & V,environment * env,GIAC_CONTEXT){
    A=Aorig;
    int n=int(A.size());
    if (!n) return false; // setsizeerr();
    int m=int(A.front().size());
    matrice2std_matrix_gen(midn(n),U);
    matrice2std_matrix_gen(midn(m),V);
    // FIXME: possible improvement if only A is computed
    // do ihermite, compute det, 
    // and make computations below mod 2*det
    // It is also possible at increment step to divide by the pivot
    // the remaining coeffs of the matrix (and multiply back later)
    gen u,v,d;
    vecteur B1(n),B2(m);
    int i0=0,j0=0; // row below i0 and col below j0 done
    for (;j0<m && i0<n; ){
      bool increment=true;
      if (j0<m){
	// Find non zero entry of smallest abs value in column j0
	int k=-1;
	gen min_val=plus_inf,tmp,q;
	for (int i=i0;i<n;++i){
	  tmp=smith_deg(A[i][j0],env,contextptr);
	  if (!is_zero(tmp,contextptr) && is_strictly_greater(min_val,tmp,contextptr)){
	    k=i;
	    min_val=tmp;
	  }
	}
	if (k>=0 && !is_zero(min_val,contextptr)){
	  if (i0!=k){ // Exchange lines i0 and k in A and U
	    swap(A[i0],A[k]);
	    swap(U[i0],U[k]);
	  }
	  for (int i=n-1;i>i0;--i){
	    if (is_zero(A[i][j0],contextptr) )
	      continue;
	    increment=false;
	    // we use Bezout u*a+v*b=d where a=coeff, b="pivot"
	    // L_i0 <- v*L_i0 + u*L_i
	    // L_i <- (-a * L_i0 + b * L_i)/d
	    // This transformation is Z-invertible since det=(U*a+b*v)/d=1
	    // it will cancel the leading coeff of L_i
	    // We should use the smallest possible |u| and |v|
	    gen a = A[i][j0];
	    gen b = A[i0][j0];
	    egcd(b,a,v,u,d,env);
	    if (env && env->moduloon){
	      modlinear_combination(v,U[i0],u,U[i],plus_one,B1,env,0,0);
	      modlinear_combination(-a,U[i0],b,U[i],d,U[i],env,0,0);
	      modlinear_combination(v,A[i0],u,A[i],plus_one,B2,env,0,0);
	      modlinear_combination(-a,A[i0],b,A[i],d,A[i],env,0,0);
	    }
	    else {
	      linear_combination(v,U[i0],u,U[i],plus_one,1,B1,0.0,0);
	      linear_combination(-a,U[i0],b,U[i],d,1,U[i],0.0,0);
	      linear_combination(v,A[i0],u,A[i],plus_one,1,B2,0.0,0);
	      linear_combination(-a,A[i0],b,A[i],d,1,A[i],0.0,0);
	    }
	    U[i0]=B1;
	    A[i0]=B2;	    
	  } // end for (row reduced)
	  if (!env && is_strictly_positive(-A[i0][j0],contextptr)){
	    A[i0]=-A[i0];
	    U[i0]=-U[i0];
	  }
	} // end if k>=0 && !is_zero(min_val)
      } // end if (j0<m)
      if (i0<n){
	// Column reduction
	A=A.transpose();
	// Find non zero entry of smallest abs value in transposed col i0
	int k=-1;
	gen min_val=plus_inf,tmp,q;
	for (int i=j0;i<m;++i){
	  tmp=smith_deg(A[i][i0],env,contextptr);
	  if (!is_zero(tmp,contextptr) && is_strictly_greater(min_val,tmp,contextptr)){
	    k=i;
	    min_val=tmp;
	  }
	}
	if (k>=0 && !is_zero(min_val,contextptr)){
	  if (j0!=k){ // Exchange transposed rows j0 and k in A and V
	    swap(A[j0],A[k]);
	    swap(V[j0],V[k]);
	  }
	  for (int i=m-1;i>j0;--i){
	    if (is_zero(A[i][i0],contextptr) )
	      continue;
	    increment=false;
	    // we use Bezout u*a+v*b=d where a=coeff, b="pivot"
	    // L_j0 <- v*L_j0 + u*L_i
	    // L_i <- (-a * L_j0 + b * L_i)/d
	    // This transformation is Z-invertible since det=(U*a+b*v)/d=1
	    // it will cancel the leading coeff of L_i
	    // We should use the smallest possible |u| and |v|
	    gen a = A[i][i0];
	    gen b = A[j0][i0];
	    egcd(b,a,v,u,d,env);
	    if (env && env->moduloon){
	      modlinear_combination(v,V[j0],u,V[i],plus_one,B2,env,0,0);
	      modlinear_combination(-a,V[j0],b,V[i],d,V[i],env,0,0);
	      modlinear_combination(v,A[j0],u,A[i],plus_one,B1,env,0,0);
	      modlinear_combination(-a,A[j0],b,A[i],d,A[i],env,0,0);
	    }
	    else {
	      linear_combination(v,V[j0],u,V[i],plus_one,1,B2,0.0,0);
	      linear_combination(-a,V[j0],b,V[i],d,1,V[i],0.0,0);
	      linear_combination(v,A[j0],u,A[i],plus_one,1,B1,0.0,0);
	      linear_combination(-a,A[j0],b,A[i],d,1,A[i],0.0,0);
	    }
	    V[j0]=B2;
	    A[j0]=B1;	    
	  } // end for (row reduced)
	  if (!env && is_strictly_positive(-A[j0][i0],contextptr)){
	    A[j0]=-A[j0];
	    V[j0]=-V[j0];
	  }
	} // end if (k>=0 && !is_zero(min_val) )
	// End column reduction
	A=A.transpose();
      } // end if (i0<n)
      // Now check that all remaining elements are divisible by A[i0][j0]
      // otherwise replace A[i0] by A[i0]+A[i]
      if (i0<n && j0<m){
	gen pivot=A[i0][j0];
	int i=i0+1;
	for (;i<n;++i){
	  int j=j0+1;
	  for (;j<m;++j){
	    if (!is_zero(rem(A[i][j],pivot,env),contextptr))
	      break;
	  }
	  if (j!=m)
	    break;
	}
	if (i!=n){
	  increment=false;
	  A[i0]=addvecteur(A[i0],A[i]);
	  U[i0]=addvecteur(U[i0],U[i]);
	}
      }
      if (increment){
	++i0;
	++j0;
      }
    } // end for (;j0<m && i0<n;)
    V=V.transpose();
    return true;
  }

  bool ismith(const matrice & Aorig, matrice & U,matrice & A,matrice & V,GIAC_CONTEXT){
    std_matrix<gen> aorig,u,a,v;
    matrice2std_matrix_gen(Aorig,aorig);
    if (!smith(aorig,u,a,v,0,contextptr))
      return false;
    std_matrix_gen2matrice_destroy(u,U);
    std_matrix_gen2matrice_destroy(a,A);
    std_matrix_gen2matrice_destroy(v,V);
    return true;
  }

  gen _ismith(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    if (!is_integer_matrice(*g._VECTptr))
      return gensizeerr(gettext("Integer matrix expected"));
    matrice U,A,V;
    if (!ismith(*g._VECTptr,U,A,V,contextptr))
      return gensizeerr(contextptr);
    return gen(makevecteur(U,A,V),_SEQ__VECT);
  }
  static const char _ismith_s []="ismith";
  static define_unary_function_eval (__ismith,&_ismith,_ismith_s);
  define_unary_function_ptr5( at_ismith ,alias_at_ismith,&__ismith,0,true);

  //   ismith, calcule la forme normale de Smith d'une
  //   matrice, A0 a coefficients entiers
  //   U,A,V := ismith(A0);
  //   calcule U,V Z-inversibles et A=U*Aorig*V, A est diagonale avec
  //   A[i,i] divise A[i+1,i+1]
  //   Les A[i,i] s'appellent diviseurs elementaires et permettent entre
  //   autre de trouver la structure des groupes abeliens de type fini

  // FIXME: Hermite and Smith normal form, same code except for smod/iquo/egcd
  // For polynomials use egcd(a,b,env,u,v,d)

  // Read a CSV file (comma separated) with separator, newline, end of file
  // decsep = decimal separator (, -> .)
#ifndef NSPIRE
  matrice csv2gen(istream & i,char sep,char nl,char decsep,char eof,GIAC_CONTEXT){
    // return vecteur(1,gensizeerr(contextptr));
    vecteur res,line;
    size_t nrows=0,ncols=0;
    char c;
    string s;
    bool ok=true;
    for (;ok && i;){
      c=i.get();
      if (i.eof() || c==eof){
	if (s.empty())
	  break;
	ok=false;
	c=nl;
      }
      if (c=='%')
	c=' ';
      if (c==sep || c==nl){
	// remove spaces at beginning of s
	while (!s.empty() && s[0]==' ')
	  s=s.substr(1,s.size()-1);
	// if sep==' ' remove spaces in i
	if (sep==' '){
	  char c2;
	  for (;;){
	    c2=i.get();
	    if (i.eof() || c2!=' '){
	      i.putback(c2);
	      break;
	    }
	  }
	}
	// if 1st char is = or digit parse, else string
	int ss=int(s.size());
	if (s.empty())
	  line.push_back(string2gen(s,false));
	else {
	  if (ss>2 && s[0]=='"' && s[1]=='=' && s[ss-1]=='"'){
	    s=s.substr(1,ss-2);
	    ss -= 2;
	  }
#ifdef NO_STDEXCEPT
	  if (s[0]=='=' || s[0]=='-'){
	    line.push_back(gen(s,contextptr));
	  }
	  else {
	    if (s[0]==decsep ||(s[0]>='0' && s[0]<='9')){
	      line.push_back(gen(s,contextptr));
	    }
	    else
	      line.push_back(string2gen(s,s[0]=='"'));
	  }
#else
	  try {
	    if (s[0]=='=' || s[0]=='-'){
	      line.push_back(gen(s,contextptr));
	    }
	    else {
	      if (s[0]==decsep ||(s[0]>='0' && s[0]<='9')){
		line.push_back(gen(s,contextptr));
	      }
	      else
		line.push_back(string2gen(s,s[0]=='"'));
	    }
	  } catch (std::runtime_error & e){
	    line.push_back(string2gen(e.what(),false));
	  }
#endif
	}
	s="";
	if (c==nl){
	  res.push_back(line);
	  ncols=giacmax(int(ncols),int(line.size()));
	  line.clear();
	  nrows++;
	  continue;
	}
      } // end if c==sep || nl
      else  {
	if (c==decsep)
	  s += '.';
	else
	  s += c;
      }
    } // end reading stream
    // now make a matrix from res
    for (unsigned j=0;j<nrows;j++){
      res[j]=mergevecteur(*res[j]._VECTptr,vecteur(ncols-res[j]._VECTptr->size(),0));
    }
    return res;
  }

  gen _csv2gen(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    char sep(';'),nl('\n'),eof(0),decsep(',');
    gen tmp,gs;
    bool isfile=true;
    int s=0;
    if (g.type==_VECT && !g._VECTptr->empty()){
      gs=g._VECTptr->front();
      s=int(g._VECTptr->size());
      tmp=g[s-1];
      if (tmp==at_string){
	isfile=false;
	--s;
      }
      if (s>1){
	tmp=g[1];
	if (tmp.type==_STRNG && !tmp._STRNGptr->empty())
	  sep=(*tmp._STRNGptr)[0];
      }
      if (s>2){
	tmp=g[2];
	if (tmp.type==_STRNG && !tmp._STRNGptr->empty())
	  nl=(*tmp._STRNGptr)[0];
      }
      if (s>3){
	tmp=g[3];
	if (tmp.type==_STRNG && !tmp._STRNGptr->empty())
	  decsep=(*tmp._STRNGptr)[0];
      }
      if (s>4){
	tmp=g[4];
	if (tmp.type==_STRNG && !tmp._STRNGptr->empty())
	  eof=(*tmp._STRNGptr)[0];
      }
    }
    else
      gs=g;
    if (gs.type!=_STRNG)
      return gensizeerr(gettext("Expecting file name to convert"));
    string file=*gs._STRNGptr;
    if (isfile){
      ifstream i(file.c_str());
      return csv2gen(i,sep,nl,decsep,eof,contextptr);
    }
    else {
      // count [ ]
      int open=0,close=0,sp=0;
      for (size_t i=0;i<file.size();++i){
	if (file[i]=='[')
	  ++open;
	if (file[i]==']')
	  ++close;
	if (file[i]==' ')
	  ++sp;
      }
      if (file.size()<=20 && sp==0)
	return eval(gen(file,contextptr),1,contextptr);
      if (open>=2 && absint(open-close)<=1)
	return gen(file,contextptr);
#ifdef HAVE_SSTREAM
      istringstream i(file.c_str());
#else
      istrstream i(file.c_str());
#endif
      if (s==1) // guess
	csv_guess(file.c_str(),file.size(),sep,nl,decsep);
      return csv2gen(i,sep,nl,decsep,eof,contextptr);
    }
  }
  static const char _csv2gen_s []="csv2gen";
  static define_unary_function_eval (__csv2gen,&_csv2gen,_csv2gen_s);
  define_unary_function_ptr5( at_csv2gen ,alias_at_csv2gen,&__csv2gen,0,true);

#endif
  matrice matpow(const matrice & m,const gen & n,GIAC_CONTEXT){
    identificateur x("x");
    gen ux=symbolic(at_pow,gen(makevecteur(x,n),_SEQ__VECT));
    return analytic_apply(ux,x,m,contextptr);
  }

      // FIXME: pow should not always call egv stuff
  gen _matpow(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type==_VECT && a._VECTptr->size()==2 && ckmatrix(a._VECTptr->front()))
      return matpow(*a._VECTptr->front()._VECTptr,a._VECTptr->back(),contextptr);
    return gensizeerr(contextptr);
  }
  static const char _matpow_s []="matpow";
  static define_unary_function_eval (__matpow,&giac::_matpow,_matpow_s);
  define_unary_function_ptr5( at_matpow ,alias_at_matpow,&__matpow,0,true);

  // EIGENVALUES for double coeff
  bool eigenval2(matrix_double & H,int n2,giac_double & l1, giac_double & l2){
    giac_double a=H[n2-2][n2-2],b=H[n2-2][n2-1],c=H[n2-1][n2-2],d=H[n2-1][n2-1];
    giac_double delta=a*a-2*a*d+d*d+4*b*c;
    if (delta<0){
      l1=(a+d)/2;
      l2=std::sqrt(-delta)/2;
      if (debug_infolevel>2)
	CERR << "eigenval2([[" << a << "," << b << "],[" << c << "," << d << "]], delta=" << delta << " re " << l1 << " im " << l2 << endl;
      return false;
    }
    delta=std::sqrt(delta);
    l1=(a+d+delta)/2;
    l2=(a+d-delta)/2;
    if (debug_infolevel>2)
      CERR << "eigenval2([[" << a << "," << b << "],[" << c << "," << d << "]], delta=" << delta << " , " << l1 << " and " << l2 << endl;
    return true;
  }

  static inline giac_double trim(giac_double a,giac_double b,giac_double eps){
    if (eps && std::abs(a)<eps*std::abs(b)) 
      return 0;
    else
      return a;
  }

  // v=(c1*v1+c2*v2), begin at cstart
  void linear_combination(giac_double c1,const vector<giac_double> & v1,giac_double c2,const vector<giac_double> & v2,vector<giac_double> & v,int cstart,double eps){
    eps=0;
    if (cstart<0)
      cstart=0;
    vector<giac_double>::const_iterator it1=v1.begin()+cstart,it1end=v1.end(),it2=v2.begin()+cstart;
    vector<giac_double>::iterator jt1=v.begin()+cstart;
#ifdef DEBUG_SUPPORT
    if (it1end-it1!=v2.end()-it2)
      setdimerr();
#endif
    if (it2==jt1){
      linear_combination(c2,v2,c1,v1,v,cstart,eps);
      return;
    }
    if (it1==jt1){
      for (;jt1!=it1end;++jt1,++it2){
	*jt1=c1*(*jt1)+c2*(*it2); // trim(c1*(*jt1)+c2*(*it2),c1,eps);
      }
      return;
    }
    if (int(v.size())==it1end-it1){
      jt1=v.begin();
      for (int i=0;i<cstart;++i,++jt1)
	*jt1=0;
      for (;it1!=it1end;++it1,++it2,++jt1)
	*jt1=c1*(*it1)+c2*(*it2); // trim(c1*(*it1)+c2*(*it2),c1,eps);
      return;
    }
    v.clear();
    v.reserve(it1end-it1);
    for (int i=0;i<cstart;++i)
      v.push_back(0);
    for (;it1!=it1end;++it1,++it2)
      v.push_back(c1*(*it1)+c2*(*it2)); // trim(c1*(*it1)+c2*(*it2),c1,eps);
  }

  // exchange line i and m1 of H and P, exchange colums i and m1 of H
  // assumes i>m1
  void exchange(matrix_double & H,matrix_double &P,bool compute_P,int i,int m1,int already_zero){
    if (debug_infolevel>2)
      CERR << CLOCK() << " exchange" << endl;
    H[i].swap(H[m1]);
    if (compute_P)
      P[i].swap(P[m1]);
    int n=int(H.size()),nstop=n;
    if (already_zero){
      nstop=i+already_zero+1;
      if (nstop>n)
	nstop=n;
    }
    for (matrix_double::iterator it=H.begin(),itend=it+nstop;it!=itend;++it){
      giac_double * Hj=&it->front();
      swap_giac_double(Hj[i],Hj[m1]);
    }
  }

#if 0
  void linear_combination(giac_double a,const vector<giac_double> & A,giac_double b,const vector<giac_double> & B,vector<giac_double> & C,int cstart){
    const giac_double * Aptr=&A.front()+cstart, * Bptr=&B.front()+cstart;
    giac_double * Cptr=&C.front()+cstart,* Cend=Cptr+(C.size()-cstart);
    for (;Cptr!=Cend;++Aptr,++Bptr,++Cptr){
      *Cptr=a*(*Aptr)+b*(*Bptr);
    }
  }

  void linear_combination(giac_double a,const vector<giac_double> & A,giac_double b,vector<giac_double> & C,int cstart){
    const giac_double * Aptr=&A.front()+cstart;
    giac_double * Cptr=&C.front()+cstart,* Cend=Cptr+(C.size()-cstart);
    for (;Cptr!=Cend;++Aptr,++Cptr){
      *Cptr=a*(*Aptr)+b*(*Cptr);
    }
  }
#endif

  // H*w->v and w*H->vprime, assumes correct sizes (v already initialized)
  // assumes w[0]=w[1]=...=w[k-1]=0
  void householder_mult2(const matrix_double & H,const std::vector<giac_double> & w,vector<giac_double> & v,vector<giac_double> & vprime,int k,bool is_k_hessenberg){
    int n=int(H.size());
    for (int j=0;j<n;++j)
      vprime[j]=0;
    int j=0;
    for (;j<=n-4;j+=4){
      const giac_double * H0jk=&H[j].front(),*H0jk1=H0jk+k,*H0jkend=H0jk+n,*wk=&w.front()+k;
      const giac_double * H1jk=&H[j+1].front();
      const giac_double * H2jk=&H[j+2].front();
      const giac_double * H3jk=&H[j+3].front();
      giac_double *vprimek=&vprime.front();
      giac_double res0=0.0,res1=0.0,res2=0.0,res3=0.0,wj0=w[j],wj1=w[j+1],wj2=w[j+2],wj3=w[j+3];
      if (is_k_hessenberg && k){
	H0jk +=k-1;
	H1jk +=k-1;
	H2jk +=k-1;
	H3jk +=k-1;
	vprimek +=k-1;
      }
      for (;H0jk<H0jk1;++vprimek,++H0jk,++H1jk,++H2jk,++H3jk){
	*vprimek += wj0*(*H0jk)+wj1*(*H1jk)+wj2*(*H2jk)+wj3*(*H3jk);;
      }
#if 1
      H0jkend -= 8;
      for (;H0jk<=H0jkend;){
	giac_double tmp0=(*H0jk),tmp1=(*H1jk),tmp2(*H2jk),tmp3(*H3jk),tmp(*wk);
	*vprimek += wj0*tmp0+wj1*tmp1+wj2*tmp2+wj3*tmp3;
	res0 += tmp0*tmp; res1 += tmp1*tmp; res2 += tmp2*tmp; res3 += tmp3*tmp;
	++wk,++vprimek,++H3jk,++H2jk,++H1jk,++H0jk;
	// 1
	tmp0=*H0jk; tmp1=*H1jk; tmp2=*H2jk; tmp3=*H3jk; tmp=*wk;
	*vprimek += wj0*tmp0+wj1*tmp1+wj2*tmp2+wj3*tmp3;
	res0 += tmp0*tmp; res1 += tmp1*tmp; res2 += tmp2*tmp; res3 += tmp3*tmp;
	++wk,++vprimek,++H3jk,++H2jk,++H1jk,++H0jk;
	// 2
	tmp0=*H0jk; tmp1=*H1jk; tmp2=*H2jk; tmp3=*H3jk; tmp=*wk;
	*vprimek += wj0*tmp0+wj1*tmp1+wj2*tmp2+wj3*tmp3;
	res0 += tmp0*tmp; res1 += tmp1*tmp; res2 += tmp2*tmp; res3 += tmp3*tmp;
	++wk,++vprimek,++H3jk,++H2jk,++H1jk,++H0jk;
	// 3
	tmp0=*H0jk; tmp1=*H1jk; tmp2=*H2jk; tmp3=*H3jk; tmp=*wk;
	*vprimek += wj0*tmp0+wj1*tmp1+wj2*tmp2+wj3*tmp3;
	res0 += tmp0*tmp; res1 += tmp1*tmp; res2 += tmp2*tmp; res3 += tmp3*tmp;
	++wk,++vprimek,++H3jk,++H2jk,++H1jk,++H0jk;
	// 4
	tmp0=*H0jk; tmp1=*H1jk; tmp2=*H2jk; tmp3=*H3jk; tmp=*wk;
	*vprimek += wj0*tmp0+wj1*tmp1+wj2*tmp2+wj3*tmp3;
	res0 += tmp0*tmp; res1 += tmp1*tmp; res2 += tmp2*tmp; res3 += tmp3*tmp;
	++wk,++vprimek,++H3jk,++H2jk,++H1jk,++H0jk;
	// 5
	tmp0=*H0jk; tmp1=*H1jk; tmp2=*H2jk; tmp3=*H3jk; tmp=*wk;
	*vprimek += wj0*tmp0+wj1*tmp1+wj2*tmp2+wj3*tmp3;
	res0 += tmp0*tmp; res1 += tmp1*tmp; res2 += tmp2*tmp; res3 += tmp3*tmp;
	++wk,++vprimek,++H3jk,++H2jk,++H1jk,++H0jk;
	// 6
	tmp0=*H0jk; tmp1=*H1jk; tmp2=*H2jk; tmp3=*H3jk; tmp=*wk;
	*vprimek += wj0*tmp0+wj1*tmp1+wj2*tmp2+wj3*tmp3;
	res0 += tmp0*tmp; res1 += tmp1*tmp; res2 += tmp2*tmp; res3 += tmp3*tmp;
	++wk,++vprimek,++H3jk,++H2jk,++H1jk,++H0jk;
	// 7
	tmp0=*H0jk; tmp1=*H1jk; tmp2=*H2jk; tmp3=*H3jk; tmp=*wk;
	*vprimek += wj0*tmp0+wj1*tmp1+wj2*tmp2+wj3*tmp3;
	res0 += tmp0*tmp; res1 += tmp1*tmp; res2 += tmp2*tmp; res3 += tmp3*tmp;
	++wk,++vprimek,++H3jk,++H2jk,++H1jk,++H0jk;
      }
      H0jkend += 8;
#endif
      for (;H0jk!=H0jkend;++wk,++vprimek,++H3jk,++H2jk,++H1jk,++H0jk){
	giac_double tmp0=(*H0jk),tmp1=(*H1jk),tmp2(*H2jk),tmp3(*H3jk),tmp(*wk);
	*vprimek += wj0*tmp0+wj1*tmp1+wj2*tmp2+wj3*tmp3;
	res0 += tmp0*tmp; res1 += tmp1*tmp; res2 += tmp2*tmp; res3 += tmp3*tmp;
      }
      v[j]=res0;
      v[j+1]=res1;
      v[j+2]=res2;
      v[j+3]=res3;
    }
    for (;j<n;++j){
      const giac_double * H0jk=&H[j].front(),*H0jk1=H0jk+k,*H0jkend=H0jk+n,*wk=&w.front()+k;
      giac_double *vprimek=&vprime.front();
      giac_double res=0.0,wj0=w[j];
      if (is_k_hessenberg && k){
	H0jk +=k-1;
	vprimek +=k-1;
      }
      for (;H0jk<H0jk1;++vprimek,++H0jk){
	*vprimek += wj0*(*H0jk);
      }
      for (;H0jk!=H0jkend;++wk,++vprimek,++H0jk){
	register giac_double tmp0=(*H0jk);
	*vprimek += wj0*tmp0;
	res += tmp0*(*wk);
      }
      v[j]=res;
    }
  }


  // H*w->v, assumes correct sizes (v already initialized)
  // assumes w[0]=w[1]=...=w[k-1]=0
  void householder_mult(const matrix_double & H,const std::vector<giac_double> & w,vector<giac_double> & v,int k){
    int n=int(H.size());
    for (int j=0;j<n;++j){
      vector<giac_double>::const_iterator it=H[j].begin()+k,itend=H[j].end(),jt=w.begin()+k;
      giac_double res=0.0;
      for (;it!=itend;++jt,++it)
	res += (*it)*(*jt);
      v[j]=res;
    }
  }

  // w*H->v, assumes correct sizes (v already initialized)
  // assumes w[0]=w[1]=...=w[k-1]=0
  void householder_mult(const std::vector<giac_double> & w,const matrix_double & H,vector<giac_double> & v,int k,bool is_k_hessenberg,int jstart,int jend,int deltarow=0,int cstart=0,int cend=0){
    int n=int(H.size())-deltarow;
    if (cend<=cstart)
      cend=int(H.front().size());
    int c=cend-cstart;
    v.resize(c);
    for (int j=0;j<c;++j)
      v[j]=0;
    int j=jstart; // at least k
#if 1
    // if H is hessenberg up to column k, we can start at H[j][k-1] instead of H[j][0]
    for (;j<=jend-8;j+=8){
      giac_double wj0=w[j],wj1=w[j+1],wj2=w[j+2],wj3=w[j+3],wj4=w[j+4],wj5=w[j+5],wj6=w[j+6],wj7=w[j+7];
      const giac_double * Hjk0=&H[j+deltarow][cstart],* Hjk1=&H[j+deltarow+1][cstart],* Hjk2=&H[j+deltarow+2][cstart],* Hjk3=&H[j+deltarow+3][cstart],* Hjk4=&H[j+deltarow+4][cstart],* Hjk5=&H[j+deltarow+5][cstart],* Hjk6=&H[j+deltarow+6][cstart],* Hjk7=&H[j+deltarow+7][cstart];
      giac_double * vk=&v.front(),*vkend=vk+c;
      // if H is hessenberg up to column k, we can start at H[j][k-1] instead of H[j][0]
      if (is_k_hessenberg && k){
	Hjk0 += k-1;
	Hjk1 += k-1;
	Hjk2 += k-1;
	Hjk3 += k-1;
	Hjk4 += k-1;
	Hjk5 += k-1;
	Hjk6 += k-1;
	Hjk7 += k-1;
	vk += k-1;
      }
      vkend -= 8;
      for (;vk<=vkend;){
	*vk += wj0*(*Hjk0)+wj1*(*Hjk1)+wj2*(*Hjk2)+wj3*(*Hjk3)+wj4*(*Hjk4)+wj5*(*Hjk5)+wj6*(*Hjk6)+wj7*(*Hjk7);;
	++vk; ++Hjk0; ++Hjk1; ++Hjk2; ++Hjk3; ++Hjk4; ++Hjk5; ++Hjk6; ++Hjk7;
	// 1
	*vk += wj0*(*Hjk0)+wj1*(*Hjk1)+wj2*(*Hjk2)+wj3*(*Hjk3)+wj4*(*Hjk4)+wj5*(*Hjk5)+wj6*(*Hjk6)+wj7*(*Hjk7);;
	++vk; ++Hjk0; ++Hjk1; ++Hjk2; ++Hjk3; ++Hjk4; ++Hjk5; ++Hjk6; ++Hjk7;
	// 2
	*vk += wj0*(*Hjk0)+wj1*(*Hjk1)+wj2*(*Hjk2)+wj3*(*Hjk3)+wj4*(*Hjk4)+wj5*(*Hjk5)+wj6*(*Hjk6)+wj7*(*Hjk7);;
	++vk; ++Hjk0; ++Hjk1; ++Hjk2; ++Hjk3; ++Hjk4; ++Hjk5; ++Hjk6; ++Hjk7;
	// 3
	*vk += wj0*(*Hjk0)+wj1*(*Hjk1)+wj2*(*Hjk2)+wj3*(*Hjk3)+wj4*(*Hjk4)+wj5*(*Hjk5)+wj6*(*Hjk6)+wj7*(*Hjk7);;
	++vk; ++Hjk0; ++Hjk1; ++Hjk2; ++Hjk3; ++Hjk4; ++Hjk5; ++Hjk6; ++Hjk7;
	// 4
	*vk += wj0*(*Hjk0)+wj1*(*Hjk1)+wj2*(*Hjk2)+wj3*(*Hjk3)+wj4*(*Hjk4)+wj5*(*Hjk5)+wj6*(*Hjk6)+wj7*(*Hjk7);;
	++vk; ++Hjk0; ++Hjk1; ++Hjk2; ++Hjk3; ++Hjk4; ++Hjk5; ++Hjk6; ++Hjk7;
	// 5
	*vk += wj0*(*Hjk0)+wj1*(*Hjk1)+wj2*(*Hjk2)+wj3*(*Hjk3)+wj4*(*Hjk4)+wj5*(*Hjk5)+wj6*(*Hjk6)+wj7*(*Hjk7);;
	++vk; ++Hjk0; ++Hjk1; ++Hjk2; ++Hjk3; ++Hjk4; ++Hjk5; ++Hjk6; ++Hjk7;
	// 6
	*vk += wj0*(*Hjk0)+wj1*(*Hjk1)+wj2*(*Hjk2)+wj3*(*Hjk3)+wj4*(*Hjk4)+wj5*(*Hjk5)+wj6*(*Hjk6)+wj7*(*Hjk7);;
	++vk; ++Hjk0; ++Hjk1; ++Hjk2; ++Hjk3; ++Hjk4; ++Hjk5; ++Hjk6; ++Hjk7;
	// 7
	*vk += wj0*(*Hjk0)+wj1*(*Hjk1)+wj2*(*Hjk2)+wj3*(*Hjk3)+wj4*(*Hjk4)+wj5*(*Hjk5)+wj6*(*Hjk6)+wj7*(*Hjk7);;
	++vk; ++Hjk0; ++Hjk1; ++Hjk2; ++Hjk3; ++Hjk4; ++Hjk5; ++Hjk6; ++Hjk7;
      }
      vkend += 8;
      for (;vk!=vkend;++Hjk0,++Hjk1,++Hjk2,++Hjk3,++Hjk4,++Hjk5,++Hjk6,++Hjk7,++vk){
	*vk += wj0*(*Hjk0)+wj1*(*Hjk1)+wj2*(*Hjk2)+wj3*(*Hjk3)+wj4*(*Hjk4)+wj5*(*Hjk5)+wj6*(*Hjk6)+wj7*(*Hjk7);;
      }
    }
#endif
    for (;j<jend;j++){
      giac_double wj=w[j];
      const giac_double * Hjk=&H[j+deltarow][cstart];
      giac_double * vk=&v.front(),*vkend=vk+c;
      // if H is hessenberg up to column k, start at H[j][k-1] instead of H[j][0]
      if (is_k_hessenberg && k){
	Hjk += k-1;
	vk += k-1;
      }
      for (;vk!=vkend;++Hjk,++vk){
	*vk += wj*(*Hjk);
      }
    }
  }

  // w*H->v, assumes correct sizes (v already initialized)
  // assumes w[0]=w[1]=...=w[k-1]=0
  void householder_mult(const std::vector<giac_double> & w,const matrix_double & H,vector<giac_double> & v,int k,bool is_k_hessenberg){
    int n=int(H.size());
    householder_mult(w,H,v,k,is_k_hessenberg,k,n);
  }

  // P -> P - 2 w qprime
  // not used except possibly for 1 reflector if P is initially identity
  void hessenberg_p_pass1(matrix_double & P,const vector<giac_double> & w,const vector<giac_double> & qprime,int j,int jend,int n,int deltarow=0,int deltacol=0){
    int qstart=0;
    for (;qstart<n;++qstart){
      if (qprime[qstart])
	break;
    }
    //qstart=0;
    for (;j<=jend-3;j+=3){
      const giac_double wj0=2*w[j], wj1=2*w[j+1], wj2=2*w[j+2];
      giac_double vk;//, wj3=2*w[j+3]
      giac_double * Pj0k=&P[j+deltarow][qstart+deltacol],* Pj1k=&P[j+deltarow+1][qstart+deltacol],* Pj2k=&P[j+deltarow+2][qstart+deltacol],*Pj0kend=Pj0k+(n-qstart);
      const giac_double *vprimek=&qprime[qstart];
#if 1
      Pj0kend -= 8;
      for (;Pj0k<Pj0kend;){
	vk=*vprimek;
	*Pj0k -= wj0*vk;
	*Pj1k -= wj1*vk;
	*Pj2k -= wj2*vk;
	// *Pj3k -= wj3*vk;
	++Pj0k;++Pj1k;++Pj2k;++vprimek; // ++Pj3k;
	// 1
	vk=*vprimek;
	*Pj0k -= wj0*vk;
	*Pj1k -= wj1*vk;
	*Pj2k -= wj2*vk;
	// *Pj3k -= wj3*vk;
	++Pj0k;++Pj1k;++Pj2k;++vprimek; // ++Pj3k;
	// 2
	vk=*vprimek;
	*Pj0k -= wj0*vk;
	*Pj1k -= wj1*vk;
	*Pj2k -= wj2*vk;
	// *Pj3k -= wj3*vk;
	++Pj0k;++Pj1k;++Pj2k;;++vprimek; // ++Pj3k
	// 3
	vk=*vprimek;
	*Pj0k -= wj0*vk;
	*Pj1k -= wj1*vk;
	*Pj2k -= wj2*vk;
	// *Pj3k -= wj3*vk;
	++Pj0k;++Pj1k;++Pj2k;++vprimek; // ++Pj3k;
	// 4
	vk=*vprimek;
	*Pj0k -= wj0*vk;
	*Pj1k -= wj1*vk;
	*Pj2k -= wj2*vk;
	// *Pj3k -= wj3*vk;
	++Pj0k;++Pj1k;++Pj2k;++vprimek; // ++Pj3k;
	// 5
	vk=*vprimek;
	*Pj0k -= wj0*vk;
	*Pj1k -= wj1*vk;
	*Pj2k -= wj2*vk;
	// *Pj3k -= wj3*vk;
	++Pj0k;++Pj1k;++Pj2k;++vprimek; // ++Pj3k;
	// 6
	vk=*vprimek;
	*Pj0k -= wj0*vk;
	*Pj1k -= wj1*vk;
	*Pj2k -= wj2*vk;
	// *Pj3k -= wj3*vk;
	++Pj0k;++Pj1k;++Pj2k;;++vprimek;//++Pj3k
	// 7
	vk=*vprimek;
	*Pj0k -= wj0*vk;
	*Pj1k -= wj1*vk;
	*Pj2k -= wj2*vk;
	// *Pj3k -= wj3*vk;
	++Pj0k;++Pj1k;++Pj2k;++vprimek; //++Pj3k;
      }
      Pj0kend += 8;
#endif
      for (;Pj0k<Pj0kend;){
	vk=*vprimek;
	*Pj0k -= wj0*vk;
	*Pj1k -= wj1*vk;
	*Pj2k -= wj2*vk;
	// *Pj3k -= wj3*vk;
	++Pj0k;++Pj1k;++Pj2k;++vprimek;//++Pj3k;
      }
    }
    for (;j<jend;++j){
      giac_double wj=2*w[j];
      giac_double * Pjk=&P[j+deltarow][qstart+deltacol],*Pjkend=Pjk+(n-qstart);
      const giac_double *vprimek=&qprime[qstart];
      for (;Pjk!=Pjkend;++vprimek,++Pjk){
	*Pjk -= wj*(*vprimek);
      }
    } 
  }

  // H*w->v, assumes correct sizes 
  // assumes w[0]=w[1]=...=w[k-1]=0 and H is identity except if rows and col >=k
  void householder_idn_mult(const matrix_double & H,const std::vector<giac_double> & w,vector<giac_double> & v,int k){
    v.resize(w.size());
    int n=int(H.size());
    std::copy(w.begin(),w.begin()+k,v.begin());
    int j=k;
    for (;j<=n-4;j+=4){
      vector<giac_double>::const_iterator it0=H[j].begin()+k,it1=H[j+1].begin()+k,it2=H[j+2].begin()+k,it3=H[j+3].begin()+k,itend=H[j].end(),jt=w.begin()+k;
      giac_double res0=0.0,res1=0.0,res2=0.0,res3=0.0;
      for (;it0!=itend;++jt,++it3,++it2,++it1,++it0){
	giac_double tmp=*jt;
	res0 += (*it0)*tmp;
	res1 += (*it1)*tmp;
	res2 += (*it2)*tmp;
	res3 += (*it3)*tmp;
      }
      v[j]=res0;
      v[j+1]=res1;
      v[j+2]=res2;
      v[j+3]=res3;
    }
    for (;j<n;++j){
      vector<giac_double>::const_iterator it=H[j].begin()+k,itend=H[j].end(),jt=w.begin()+k;
      giac_double res=0.0;
      for (;it!=itend;++jt,++it)
	res += (*it)*(*jt);
      v[j]=res;
    }
  }

  // P*wi->Pwi, assumes correct sizes 
  // assumes w[0]=w[1]=...=w[k-1]=0 and P is identity except if rows and col >=k
  void householder_idnt_mult2(const matrix_double & P,const std::vector<giac_double> & w1,vector<giac_double> & w2,vector<giac_double> & Pw1,vector<giac_double> & Pw2,int k){
    Pw1.resize(w1.size());
    Pw2.resize(w2.size());
    int n=int(P.size());
    std::copy(w1.begin(),w1.begin()+k,Pw1.begin());
    std::copy(w2.begin(),w2.begin()+k,Pw2.begin());
    int j=k;
#if 1
    for (;j<=n-2;j+=2){
      vector<giac_double>::const_iterator it0=P[j].begin()+k,it1=P[j+1].begin()+k,itend=P[j].end(),jt1=w1.begin()+k,jt2=w2.begin()+k;
      giac_double res10=0.0,res11=0.0,res20=0.0,res21=0.0;
      for (;it0!=itend;++jt1,++jt2,++it1,++it0){
	giac_double j1=*jt1,j2=*jt2,i0=*it0,i1=*it1;
	res10 += j1*i0;
	res11 += j1*i1;
	res20 += j2*i0;
	res21 += j2*i1;
      }
      Pw1[j]=res10;
      Pw1[j+1]=res11;
      Pw2[j]=res20;
      Pw2[j+1]=res21;
    }
#endif
    for (;j<n;++j){
      vector<giac_double>::const_iterator it=P[j].begin()+k,itend=P[j].end(),jt1=w1.begin()+k,jt2=w2.begin()+k;
      giac_double res1=0.0,res2=0.0;
      for (;it!=itend;++jt1,++jt2,++it){
	res1 += (*it)*(*jt1);
	res2 += (*it)*(*jt2);
      }
      Pw1[j]=res1;
      Pw2[j]=res2;
    }
  }

  // P->P-Pw1 w1* - Pw2 w2*
  void hessenberg_idnt_2p(matrix_double & P,const std::vector<giac_double> & Pw1,vector<giac_double> & Pw2,vector<giac_double> & w1,vector<giac_double> & w2){
    int qstart=0;
    int n=int(P.size()),jend=n;
    for (;qstart<n;++qstart){
      if (w1[qstart] || w2[qstart] || Pw1[qstart] || Pw2[qstart])
	break;
    }
    int j=qstart;
#if 1
    for (;j<=jend-2;j+=2){
      const giac_double wj10=2*Pw1[j], wj11=2*Pw1[j+1],wj20=2*Pw2[j],wj21=2*Pw2[j+1];
      giac_double * Pj0k=&P[j][qstart],* Pj1k=&P[j+1][qstart],*Pj0kend=Pj0k+(n-qstart);
      const giac_double *w1ptr=&w1[qstart], * w2ptr=&w2[qstart];
      for (;Pj0k<Pj0kend;){
	giac_double w1k=*w1ptr,w2k=*w2ptr;
	*Pj0k -= (wj10*w1k+wj20*w2k);
	*Pj1k -= (wj11*w1k+wj21*w2k);
	++Pj0k;++Pj1k;++w1ptr,++w2ptr;
      }
    }
#endif
    for (;j<jend;++j){
      giac_double wj1=2*Pw1[j],wj2=2*Pw2[j];
      giac_double * Pjk=&P[j][qstart],*Pjkend=Pjk+(n-qstart);
      const giac_double *w1ptr=&w1[qstart],*w2ptr=&w2[qstart];
      for (;Pjk!=Pjkend;){
	*Pjk -= (wj1*(*w1ptr)+wj2*(*w2ptr));
	++w1ptr; ++w2ptr;++Pjk;
      }
    } 
  }

  void qr_householder(matrix_double & H,int rstart,matrix_double & P,bool computeP,bool Pidn,bool transpose,int cstart=0,int cend=0,bool recurse=true,bool thin=true){
    // Let R be a Householder reflection with respect to w (normalized)
    // R=I-2 w w*
    // Then R H = H - 2 w (w*H) and R P = P - 2 w (w*P)
    // Application to qr
    // step k (k<=n-1), reduce column k by the reflector that swaps 
    // the vector of column k row k to n scaled
    // with the k-th canonical vector
    // Ex. k=0, reflector swaps (a00,a10,...,an-10) with (1,0,0...) scaled
    // let alpha=sign(a00)*sqrt(sum_j=0..n-1 aj0^2), 
    // swap u1=(a00,a10,...,an-10) u2=(alpha,0,...)
    // therefore w=(u1+u2) and normalize after
    // w=((a00+alpha),a10,...,an-10)
    // ||w||^2=(a00+alpha)^2+a10^2+...+an-10^2
    //        =(a00+alpha)^2+alpha^2-a00^2=2*alpha*(alpha+a00)
    // divide by r=sqrt(2*alpha*(alpha+a00))
    // For k=m, add m to all indices
    int n=int(H.size())-rstart,c=int(H.front().size()),cP=int(P.front().size());
    if (cstart>=c) return;
    if (cend<=0) cend=c;
    if (n<2)
      return;
    int lastcol=std::min(n,cend);
    if (debug_infolevel)
      CERR << CLOCK() << " Householder, computing H" << endl;
#ifndef GIAC_HAS_STO_38
    if (recurse && n>=c && cend-cstart>200){
      if (n<2*(cend-cstart)) 
	thin=false;
      // if cstart, cend !=0, block-recursive version 
      // H n rows, c1+c2 cols, n>=c1+c2, H=[A1|A2]=Q*[[R11,R12],[0,R22]]
      // A1 and A2 have n rows and c1, c2 columns
      // first step A1=Q1*[[R11],[0]] recursive call, 
      // R11 c1 rows, c1 cols, R12 c1 rows, n-c1 cols, R22 c2 rows, n-c1 cols
      // tran(Q1)*A2=[[R12],[A22]]
      // A22=Q2*R22
      // [A1|A2]=Q1*[[R11,R12],[0,A22]]=Q1*[[Id,0],[0,Q2]]*[[R11,R12],[0,R22]]
      // tran(Q)=[[Id,0],[0,tran(Q2)]]*tran(Q1)
      // If tran(Q1)=[[Q11],[Q12]] then tran(Q)=[[Q11],[tran(Q2)*Q12]]
      // Q12 has n-c1 rows, Q2 has n-c2 rows
      int c1=(cend-cstart)/2,c2=cend-cstart-c1;
      qr_householder(H,rstart,P,true,true,true,cstart,cstart+c1,/* recurse*/ false,/* thin */false); // P is Q1
      //transpose_double(P); // P is tran(Q1)
      // R11 is in place in H, R21=0 also
      // temporary storage to compute tran(Q1)*A2
      // tranA2 c2 rows, n cols
      matrix_double tranA2; tranA2.reserve(giacmax(c2,n-c1));
      transpose_double(H,rstart,rstart+n,cstart+c1,cend,tranA2);
      matrix_double R(n,vector<giac_double>(n-c1)); 
      mmult_double(P,tranA2,R); // R n rows, c2 cols, n-c1 cols reserved for later use as tranQ12
      // QR on A22 stored in rows c1..n-1 of R
      // matrix_double Q2(n-c1,vector<giac_double>(n-c1)); 
      matrix_double & Q2 =tranA2; Q2.resize(n-c1);
      if (thin){
	qr_householder(R,c1,Q2,false,true,true,0,0,/* recurse */true,thin);
      }
      else {
	double_idn(Q2);
	qr_householder(R,c1,Q2,computeP,true,true,0,0,/* recurse */true,/* thin */false);
	// transpose_double(Q2);
      }
      for (int i=0;i<n;++i){
	std::copy(R[i].begin(),R[i].end(),H[rstart+i].begin()+c1);
      }
      if (!thin){
	// P is tran(Q1), Q12
	matrix_double tmp;
	transpose_double(P,c1,n,0,0,R); // R as tranQ12: n rows, n-c1 cols
	// tran(Q2)*Q12
	mmult_double(Q2,R,tmp); // tmp n-c1 rows, n cols
	for (int i=0;i<n-c1;++i){
	  swap(tmp[i],P[i+c1]);
	}
      }
      if (!transpose)
	transpose_double(P);
      if (debug_infolevel)
	CERR << CLOCK() << " Householder end" << endl;
      return;
    }
#endif // GIAC_HAS_STO_38
    vector<giac_double> w(n),q(cend-cstart);
    // save w to compute P all at once at the end, this could also be done
    // inside the lower diagonal bloc of H
    vector<giac_double> Pw((lastcol*(2*n-lastcol+1))/2); 
    int nreflectors=0;
    giac_double * Pwptr=&Pw.front();
    for (int m=cstart;m<lastcol;++m){
      giac_double alpha=0;
      for (int j=m;j<n;++j){
	giac_double Hjm=H[j-cstart+rstart][m];
	alpha += Hjm*Hjm;
      }
      alpha=std::sqrt(alpha);
      giac_double Hmm=H[m-cstart+rstart][m];
      if (alpha<=1e-15*std::abs(Hmm)){
	Pwptr += n-m;
	continue;
      }
      if (Hmm<0)
	alpha=-alpha;
      giac_double r=std::sqrt(2*alpha*(alpha+Hmm));
      *Pwptr=w[m]=(Hmm+alpha)/r;
      ++Pwptr;
      for (int j=m+1;j<n;++Pwptr,++j){
	*Pwptr=w[j]=H[j-cstart+rstart][m]/r;
      }
      ++nreflectors;
      householder_mult(w,H,q,m,true,m,n,rstart,cstart,cend);
      hessenberg_p_pass1(H,w,q,m,n,cend-cstart,rstart,cstart);
    }
    if (computeP){
      if (debug_infolevel)
	CERR << CLOCK() << " Householder, computing P" << endl;
      Pwptr=&Pw.front();
      if (Pidn){
	// IMPROVE: if P is identity at the beginning, it is faster
	// to compute (I-w_n w_n*) ... (I-w_1 w_1*) from left to right than
	// starting from the right 
	// Indeed w_k has k first coord=0, therefore (I-w_n w_n*) ... (I-w_k w_k*) 
	// has only rows and columns k..n different from identity
	// sum(k^2,k,1,n)=n^3/3 compared to sum(k*n,k,1,n)=n^3/2 operations
	// (and also less cache misses)
	int m=nreflectors-1;
	vector<giac_double> w1(n),w2(n),Pw1(n),Pw2(n);
	for (;m>=1;m-=2){
	  // 2 operations P(I-w1w1*)(I-w2w2*)=P-2Pw1(w1*-2<w1|w2>w2*)-2Pw2 w2*
	  for (int i=0;i<m-1;++i)
	    w1[i]=w2[i]=0;
	  w1[m-1]=0;
	  Pwptr=&Pw[((m-1)*(2*n-m+2))/2];
	  for (int i=m-1;i<n;++Pwptr,++i)
	    w2[i]=*Pwptr;
	  for (int i=m;i<n;++Pwptr,++i)
	    w1[i]=*Pwptr;	  
	  householder_idnt_mult2(P,w1,w2,Pw1,Pw2,m-1);
	  double w1w2=2*dotvecteur(w1,w2);
	  for (unsigned i=0;i<w1.size();++i){
	    w1[i]-=w1w2*w2[i];
	  }
	  hessenberg_idnt_2p(P,Pw1,Pw2,w1,w2);
	}
	for (;m>=0;--m){
	  for (int i=0;i<m;++i)
	    w[i]=0;	  
	  Pwptr=&Pw[0]; // m==0 here!
	  for (int i=m;i<n;++Pwptr,++i)
	    w[i]=*Pwptr;
	  // householder_mult(P,w,qprime,m+1); 
	  householder_idn_mult(P,w,q,m);
	  hessenberg_p_pass1(P,q,w,m,n,n);
	}
	if (debug_infolevel)
	  CERR << CLOCK() << " Householder end" << endl;
	if (!transpose)
	  transpose_double(P);
	return;
      } // end P==identity
      for (int m=0;m<n-1;++m){
	for (int i=0;i<m;++i)
	  w[i]=0;
	for (int i=m;i<n;++Pwptr,++i)
	  w[i]=*Pwptr;
	householder_mult(w,P,q,m,false);
	hessenberg_p_pass1(P,w,q,m,n,n);
      }
    }
    if (debug_infolevel)
      CERR << CLOCK() << " Householder end" << endl;
    if (!transpose)
      transpose_double(P);
  }

  // QR reduction, P is orthogonal and should be initialized to identity
  // trn(P)*H=original, Givens method
  void qr_ortho(std_matrix<gen> & H,std_matrix<gen> & P,bool computeP,GIAC_CONTEXT){
    matrix_double H1;
    if (epsilon(contextptr)>=1e-15 && std_matrix_gen2std_matrix_giac_double(H,H1,true)){
      matrix_double P1;
      std_matrix_gen2std_matrix_giac_double(P,P1,true);
      // count 0 in H under the diagonal
      // if less than 20% Householder else Givens
      int count1=0,count2=0,L=int(H.size()),C=int(H.front().size());
      for (int i=1;i<L;++i){
	const vector<giac_double> & Hi=H1[i];
	for (int j=0;j<i && j<C;++count2,++j){
	  if (Hi[j]==0.0)
	    ++count1;
	}
      }
      if (count1<=0.2*count2)
	qr_householder(H1,0,P1,computeP,true,true,0,0,/* recurse */ true,/* thin */false); 
      else 
	qr_givens(H1,0,P1,computeP,true,true,0,0,threads>1);
      std_matrix_giac_double2std_matrix_gen(P1,P);
      std_matrix_giac_double2std_matrix_gen(H1,H);
      return;
    }
    int n=int(H.size()),lastcol=std::min(n-1,int(H.front().size()));
    gen t,tn,tc,tabs,u,un,uc,tmp1,tmp2,norme;
    vecteur v1,v2;
    for (int m=0;m<lastcol;++m){
      if (debug_infolevel>=5)
	CERR << "// Givens reduction line " << m << endl;
      // check for a non zero coeff in the column m below ligne m
      int i=m;
      gen pivot=0;
      int pivotline=0;
      for (;i<n;++i){
	t=H[i][m];
	tabs=abs(t,contextptr);
	if (is_strictly_greater(tabs,pivot,contextptr)){
	  pivotline=i;
	  pivot=tabs;
	}
      }
      if (is_zero(pivot)) //not found
	continue;
      i=pivotline;
      // exchange lines 
      if (i>m){
	swap(H[i],H[m]);
	swap(P[i],P[m]);
      }
      // now coeff at line m column m is H[m][m]=t!=0
      // creation of zeros in lines i=m+1 and below
      for (i=m+1;i<n;++i){
	// line operation
	t=H[m][m];
	if (is_zero(t)){
	  swap(H[i],H[m]);
	  swap(P[i],P[m]);
	  t=H[m][m];
	}
	u=H[i][m];
	if (is_zero(u))
	  continue;
	uc=conj(u,contextptr);
	tc=conj(t,contextptr);
	norme=sqrt(u*uc+t*tc,contextptr);
	un=u/norme; tn=t/norme; uc=conj(un,contextptr);	tc=conj(tn,contextptr); 
	if (debug_infolevel>=3)
	  CERR << "// i=" << i << " " << u <<endl;
	// H[m]=un*H[i]+tn*H[m] and H[i]=tn*H[i]-un*H[m];
	linear_combination(uc,H[i],tc,H[m],plus_one,1,v1,1e-12,0); 
	linear_combination(tn,H[i],-un,H[m],plus_one,1,v2,1e-12,0); 
	swap(H[m],v1);
	swap(H[i],v2);
	linear_combination(uc,P[i],tc,P[m],plus_one,1,v1,1e-12,0); 
	linear_combination(tn,P[i],-un,P[m],plus_one,1,v2,1e-12,0); 
	swap(P[m],v1);
	swap(P[i],v2);
      }
    }
  }

  void hessenberg_householder(matrix_double & H,matrix_double & P,bool compute_P){
    // Let R be a Householder reflection with respect to w (normalized)
    // R=I-2 w w*
    // Then R H R = H - 2 w q' -2 q w*
    // where v = Hw and q = v- scalar_product(w,v) w
    // and v'=wH and q'=v'-scalar_product(v',w) w*
    // # operations: 2n+2n^2 *, n+2n^2+,
    // And R P = P - 2 w (w*P)
    // # operations: 2n^2 *, 2n^2 +
    // Application to Hessenberg
    // step k (k<=n-2), reduce column k by the reflector that swaps 
    // the vector of column k row k+1 to n scaled
    // with the k+1-th canonical vector
    // Ex. k=0, reflector swaps (0,a10,...,an-10) with (0,1,0...) scaled
    // let alpha=sign(a10)*sqrt(sum_j=1..n-1 aj0^2), 
    // swap u1=(0,a10,...,an-10) u2=(0,alpha,0,...)
    // therefore w=(u1+u2) and normalize after
    // w=(0,(a10+alpha),a20,...,a-10)
    // ||w||^2=(a10+alpha)^2+a20^2+...+an-10^2
    //        =(a10+alpha)^2+alpha^2-a10^2=2*alpha*(alpha+a10)
    // divide by r=sqrt(2*alpha*(alpha+a10))
    // For k=m, add m to all indices
    int n=int(H.size());
    if (n<3)
      return;
    vector<giac_double> w(n),q(n),qprime(n);
    vector<giac_double> Pw((n*(n-1))/2); // save w to compute P all at once at the end
    giac_double * Pwptr=&Pw.front();
    for (int m=0;m<n-2;++m){
      giac_double alpha=0;
      for (int j=m+1;j<n;++j){
	giac_double Hjm=H[j][m];
	alpha += Hjm*Hjm;
      }
      alpha=std::sqrt(alpha);
      if (alpha<=1e-15*std::abs(H[m][m])){
	Pwptr += n-m-1;
	continue;
      }
      if (H[m+1][m]<0)
	alpha=-alpha;
      giac_double r=std::sqrt(2*alpha*(alpha+H[m+1][m]));
      w[m]=0;
      *Pwptr=w[m+1]=(H[m+1][m]+alpha)/r;
      ++Pwptr;
      for (int j=m+2;j<n;++Pwptr,++j){
	*Pwptr=w[j]=H[j][m]/r;
      }
#if 1
      householder_mult2(H,w,q,qprime,m+1,true);
#else
      householder_mult(H,w,q,m+1);
      householder_mult(w,H,qprime,m+1,true);
#endif
      giac_double sp=dotvecteur(w,q);
      for (int j=0;j<n;++j){
	q[j] -= sp*w[j];
	qprime[j] -= sp*w[j];
      }
      // adjust H
      int j=0;
      for (;j<=m-2;j+=3){
	giac_double qj=2*q[j],qj1=2*q[j+1],qj2=2*q[j+2];
	giac_double * Hjk=&H[j][m+1],* Hjk1=&H[j+1][m+1],* Hjk2=&H[j+2][m+1],*wk=&w[m+1],*wkend=wk+(n-m-1);
	for (;wk!=wkend;++Hjk,++Hjk1,++Hjk2,++wk){
	  giac_double tmp=*wk;
	  (*Hjk) -= qj* tmp;
	  (*Hjk1) -= qj1* tmp;
	  (*Hjk2) -= qj2* tmp;
	}
      }
      for (;j<=n-3;j+=3){
	giac_double wj0=2*w[j],wj1=2*w[j+1],qj0=2*q[j],qj1=2*q[j+1], wj2=2*w[j+2],qj2=2*q[j+2],tmpq,tmpw;
	giac_double * Hj0k=&H[j][m],*Hj1k=&H[j+1][m],*Hj2k=&H[j+2][m],*wk=&w[m],*wkend=wk+(n-m),*qprimek=&qprime[m];
	wkend-=8;
	for (;wk<=wkend;){
	  tmpq=*qprimek; tmpw=*wk; 
	  (*Hj0k) -= wj0*tmpq+qj0*tmpw;
	  (*Hj1k) -= wj1*tmpq+qj1*tmpw;
	  (*Hj2k) -= wj2*tmpq+qj2*tmpw;
	  ++Hj0k,++Hj1k;++Hj2k;++qprimek,++wk;
	  // 1
	  tmpq=*qprimek; tmpw=*wk; 
	  (*Hj0k) -= wj0*tmpq+qj0*tmpw;
	  (*Hj1k) -= wj1*tmpq+qj1*tmpw;
	  (*Hj2k) -= wj2*tmpq+qj2*tmpw;
	  ++Hj0k,++Hj1k;++Hj2k;++qprimek,++wk;
	  // 2
	  tmpq=*qprimek; tmpw=*wk; 
	  (*Hj0k) -= wj0*tmpq+qj0*tmpw;
	  (*Hj1k) -= wj1*tmpq+qj1*tmpw;
	  (*Hj2k) -= wj2*tmpq+qj2*tmpw;
	  ++Hj0k,++Hj1k;++Hj2k;++qprimek,++wk;
	  // 3
	  tmpq=*qprimek; tmpw=*wk; 
	  (*Hj0k) -= wj0*tmpq+qj0*tmpw;
	  (*Hj1k) -= wj1*tmpq+qj1*tmpw;
	  (*Hj2k) -= wj2*tmpq+qj2*tmpw;
	  ++Hj0k,++Hj1k;++Hj2k;++qprimek,++wk;
	  // 4
	  tmpq=*qprimek; tmpw=*wk; 
	  (*Hj0k) -= wj0*tmpq+qj0*tmpw;
	  (*Hj1k) -= wj1*tmpq+qj1*tmpw;
	  (*Hj2k) -= wj2*tmpq+qj2*tmpw;
	  ++Hj0k,++Hj1k;++Hj2k;++qprimek,++wk;
	  // 5
	  tmpq=*qprimek; tmpw=*wk; 
	  (*Hj0k) -= wj0*tmpq+qj0*tmpw;
	  (*Hj1k) -= wj1*tmpq+qj1*tmpw;
	  (*Hj2k) -= wj2*tmpq+qj2*tmpw;
	  ++Hj0k,++Hj1k;++Hj2k;++qprimek,++wk;
	  // 6
	  tmpq=*qprimek; tmpw=*wk; 
	  (*Hj0k) -= wj0*tmpq+qj0*tmpw;
	  (*Hj1k) -= wj1*tmpq+qj1*tmpw;
	  (*Hj2k) -= wj2*tmpq+qj2*tmpw;
	  ++Hj0k,++Hj1k;++Hj2k;++qprimek,++wk;
	  // 7
	  tmpq=*qprimek; tmpw=*wk; 
	  (*Hj0k) -= wj0*tmpq+qj0*tmpw;
	  (*Hj1k) -= wj1*tmpq+qj1*tmpw;
	  (*Hj2k) -= wj2*tmpq+qj2*tmpw;
	  ++Hj0k,++Hj1k;++Hj2k;++qprimek,++wk;
	}
	wkend+=8;
	for (;wk!=wkend;){
	  tmpq=*qprimek; tmpw=*wk; 
	  (*Hj0k) -= wj0*tmpq+qj0*tmpw;
	  (*Hj1k) -= wj1*tmpq+qj1*tmpw;
	  (*Hj2k) -= wj2*tmpq+qj2*tmpw;
	  ++Hj0k,++Hj1k;++Hj2k;++qprimek,++wk;
	}
      }
      for (;j<n;++j){
	giac_double wj=2*w[j],qj=2*q[j];
	giac_double * Hjk=&H[j][m],*wk=&w[m],*wkend=wk+(n-m),*qprimek=&qprime[m];
	for (;wk!=wkend;++Hjk,++qprimek,++wk){
	  (*Hjk) -= wj*(*qprimek)+qj*(*wk);
	}
      }
    }
    if (compute_P){
      if (debug_infolevel)
	CERR << CLOCK() << " Householder, computing P" << endl;
      if (is_identity(P)){
	// IMPROVE: if P is identity at the beginning, it is probably faster
	// to compute (I-w_n w_n*) ... (I-w_1 w_1*) from left to right than
	// starting from the right like now
	// Indeed w_k has k first coord=0, therefore (I-w_n w_n*) ... (I-w_k w_k*) 
	// has only rows and columns k..n different from identity
	// sum(k^2,k,1,n)=n^3/3 compared to sum(k*n,k,1,n)=n^3/2 operations
	// (and also less cache misses)
	int m=n-3;
	vector<giac_double> w1(n),w2(n),Pw1(n),Pw2(n);
	for (;m>=1;m-=2){
	  // 2 operations P(I-w1w1*)(I-w2w2*)=P-2Pw1(w1*-2<w1|w2>w2*)-2Pw2 w2*
	  for (int i=0;i<m;++i)
	    w1[i]=w2[i]=0;
	  w1[m]=0;
	  Pwptr=&Pw[((m-1)*(2*n-(m-1)-1))/2];
	  for (int i=m;i<n;++Pwptr,++i)
	    w2[i]=*Pwptr;
	  for (int i=m+1;i<n;++Pwptr,++i)
	    w1[i]=*Pwptr;	  
	  householder_idnt_mult2(P,w1,w2,Pw1,Pw2,m);
	  double w1w2=2*dotvecteur(w1,w2);
	  for (unsigned i=0;i<w1.size();++i){
	    w1[i]-=w1w2*w2[i];
	  }
	  hessenberg_idnt_2p(P,Pw1,Pw2,w1,w2);
	}
	for (;m>=0;--m){
	  for (int i=0;i<=m;++i)
	    w[i]=0;	  
	  Pwptr=&Pw[(m*(2*n-m-1))/2];
	  for (int i=m+1;i<n;++Pwptr,++i)
	    w[i]=*Pwptr;
	  // householder_mult(P,w,qprime,m+1); 
	  householder_idn_mult(P,w,qprime,m+1);
	  hessenberg_p_pass1(P,qprime,w,m+1,n,n);
	}
	return;
      }
      Pwptr=&Pw.front();
      for (int m=0;m<n-2;++m){
	for (int i=0;i<=m;++i)
	  w[i]=0;
	for (int i=m+1;i<n;++Pwptr,++i)
	  w[i]=*Pwptr;
	householder_mult(w,P,qprime,m+1,false);
	int jstart=m+1;
	bool done=false;
	if (!done)
	  hessenberg_p_pass1(P,w,qprime,jstart,n,n);
      }
    }
  }

  struct thread_hessenberg_p_t {
    matrix_double * P;
    vector<double> * oper;
    int cstart,cend;
  };

  void bi_tri_linear_combination(giac_double u,giac_double t,giac_double u2,giac_double t2,giac_double U,giac_double T,giac_double U2,giac_double T2,vector<giac_double> & p1,vector<giac_double> & p2,vector<giac_double> & p3,vector<giac_double> & P3,int cstart,int cend){
    // p1=t*p1+u*p2 && p2=t*p2-u*p1 //bi_linear_combination_CA(u,H[m+2],t,H[m+1],m,nH); 
    // then p1=t2*p1+u2*p3 && p3=t2*p3-u2*p1 // bi_linear_combination_CA(u2,H[m+3],t2,H[m+1],m,nH);
    // then same with uppercase
    // 8 read (4 always at the same location)/4 write for 2 
    vector<giac_double>::iterator p1p=p1.begin()+cstart,p1e=cend<=0?p1.end():p1.begin()+cend,p2p=p2.begin()+cstart,p3p=p3.begin()+cstart,P3p=P3.begin()+cstart;
    p1e-=2;
    for (;p1p<=p1e;){
      giac_double p1_,p2_,p3_;
      p1_=*p1p;
      p2_=*p2p;
      p3_=t*p1_+u*p2_;
      p1_=t*p2_-u*p1_; // stored in p2_ renamed p1_ for second stage
      p2_=*p3p;
      *p1p=t2*p3_+u2*p2_;
      p2_=t2*p2_-u2*p3_; // stored in p3_ renamed p2_ for second stage
      p3_=*P3p;
      *p3p=T*p2_-U*p1_; // stored in p2 alias p3p at second stage
      p2_=T*p1_+U*p2_;
      *p2p=T2*p2_+U2*p3_;
      *P3p=T2*p3_-U2*p2_; 
      ++P3p,++p3p,++p2p,++p1p;
      p1_=*p1p;
      p2_=*p2p;
      p3_=t*p1_+u*p2_;
      p1_=t*p2_-u*p1_; // stored in p2_ renamed p1_ for second stage
      p2_=*p3p;
      *p1p=t2*p3_+u2*p2_;
      p2_=t2*p2_-u2*p3_; // stored in p3_ renamed p2_ for second stage
      p3_=*P3p;
      *p3p=T*p2_-U*p1_; // stored in p2 alias p3p at second stage
      p2_=T*p1_+U*p2_;
      *p2p=T2*p2_+U2*p3_;
      *P3p=T2*p3_-U2*p2_; 
      ++P3p,++p3p,++p2p,++p1p;
    }
    p1e+=2;
    for (;p1p!=p1e;){
      giac_double p1_,p2_,p3_;
      p1_=*p1p;
      p2_=*p2p;
      p3_=t*p1_+u*p2_;
      p1_=t*p2_-u*p1_; // stored in p2_ renamed p1_ for second stage
      p2_=*p3p;
      *p1p=t2*p3_+u2*p2_;
      p2_=t2*p2_-u2*p3_; // stored in p3_ renamed p2_ for second stage
      p3_=*P3p;
      *p3p=T*p2_-U*p1_; // stored in p2 alias p3p at second stage
      p2_=T*p1_+U*p2_;
      *p2p=T2*p2_+U2*p3_;
      *P3p=T2*p3_-U2*p2_; 
      ++P3p,++p3p,++p2p,++p1p;
    }
  }

  // called from schur in pass2 (after initial reduction to Hessenberg)
  void do_hessenberg_p(matrix_double &P,vector<giac_double> & oper,int cstart,int cend){
    int opindex=0;
    while (opindex<=int(oper.size())-3){
      int optype=int(oper[opindex]); // 3 for 3 lines per op, 2 for 2 lines per op
      if (optype!=oper[opindex])
	gensizeerr("Internal error");
      ++opindex;
      int firstrow=int(oper[opindex]);
      if (firstrow!=oper[opindex])
	gensizeerr("Internal error");
      ++opindex;
      int n=int(oper[opindex]);   
      if (n!=oper[opindex])
	gensizeerr("Internal error");
      ++opindex;
      int m=firstrow;
      if (optype==-2){
#if 1
	bi_linear_combination_AC(oper[opindex],P[firstrow],oper[opindex+1],P[n],cstart,cend);
#else
	bi_linear_combination_CA(oper[opindex],P[firstrow],oper[opindex+1],P[n],cstart,cend);
	// must swap from cstart to cend only!
	vector<giac_double> & Pf=P[firstrow];
	vector<giac_double> & Pn=P[n];
	for (unsigned j=cstart;j<cend;++j){
	  giac_double tmp=Pf[j];
	  Pf[j]=Pn[j]; 
	  Pn[j]=tmp;
	}
#endif
	opindex+=2;
	continue;
      }
      if (optype==2){
	for (int m=firstrow;m<n-2;opindex+=2,++m)
	  bi_linear_combination_CA(oper[opindex],P[m+2],oper[opindex+1],P[m+1],cstart,cend);
	continue;
      }
      if (optype==-3){
	tri_linear_combination(oper[opindex],P[n],oper[opindex+1],P[n+1],oper[opindex+2],P[n+2],oper[opindex+3],oper[opindex+4],oper[opindex+5],cstart,cend);
	opindex+=6;
	continue;
      }
      if (optype!=3)
	gensizeerr("Internal error in do_hessenberg_p");
      if (int(oper.size())-opindex<4*(n-2-m))
	gensizeerr("Internal error in do_hessenberg_p");	
      if (debug_infolevel>2){
	CERR << "flushing optype=3 " << m << " " << n ;
	if (debug_infolevel>3)
	  CERR << ":" << vector<giac_double>(&oper[opindex],&oper[opindex+4*(n-2-m)]);
	CERR << endl;  // << " on " << P << endl;
      }
#if 0
      for (;m<n-4;opindex+=8,m+=2){
	if (oper[opindex]==0 && oper[opindex+1]==0)
	  break;
	bi_tri_linear_combination(oper[opindex],oper[opindex+1],oper[opindex+2],oper[opindex+3],oper[opindex+4],oper[opindex+5],oper[opindex+6],oper[opindex+7],P[m+1],P[m+2],P[m+3],P[m+4],cstart,cend);
      }
#endif
      for (;m<n-2;opindex+=4,++m){
	giac_double u=oper[opindex];
	giac_double t=oper[opindex+1];
	if (u==0 && t==0)
	  continue;
	giac_double u2=oper[opindex+2];
	giac_double t2=oper[opindex+3];
	// P[m+1]=t*P[m+1]+u*P[m+2] && P[m+2]=t*P[m+2]-u*P[m+1] // bi_linear_combination_CA(u,H[m+2],t,H[m+1],m,nH); 
	// then P[m+1]=t2*P[m+1]+u2*P[m+3] && P[m+3]=-u2*P[m+1]+t2*P[m+3] // bi_linear_combination_CA(u2,H[m+3],t2,H[m+1],m,nH);
	if (m!=n-3)
	  tri_linear_combination(u,P[m+2],t,P[m+1],u2,P[m+3],t2,cstart,cend);
	else 
	  bi_linear_combination_CA(u,P[m+2],t,P[m+1],cstart,cend);
      }
    }
  }

  void * do_hessenberg_p(void *ptr){
    thread_hessenberg_p_t * p=(thread_hessenberg_p_t *)(ptr);
    matrix_double & P = *p->P;
    vector<double> & oper = *p->oper;
    int cstart=p->cstart;
    int cend=p->cend;
    do_hessenberg_p(P,oper,cstart,cend);
    return ptr;
#if 0
    // cut P in smaller slices? (cache) 
    double slicesize=P.size()*double(cend-cstart);
    if (slicesize<6e4)
      do_hessenberg_p(P,oper,cstart,cend);
    else {
      int cstep=int(std::ceil(6e4/P.size())),cstop;
      for (;cstart<cend;cstart=cstop){
	cstop=cstart+cstep;
	if (cstop>cend)
	  cstop=cend;
	do_hessenberg_p(P,oper,cstart,cstop);
      }
    }
    return ptr;
#endif
  }

  void hessenberg_ortho3_flush_p(matrix_double & P,bool compute_P,vector<giac_double> & oper,bool force_flush){
    if (oper.empty()) return;
    if (!compute_P){
      oper.clear();
      return;
    }
    if (!force_flush){
      if (oper.size()<1000 || oper.size()<P.size()*(P.size()/5.))
	return;
    }
    if (debug_infolevel>2)
      CERR << CLOCK() << "hessenberg_ortho3 compute P, flush size " << oper.size() << endl;
    int nH=int(P.size());
    int cstart=0,cstep=nH;
#ifdef HAVE_LIBPTHREAD      
    int cend,nthreads=threads_allowed?threads:1;
    if (nthreads>1 && nH*oper.size()>1e6){
      pthread_t tab[nthreads-1];
      thread_hessenberg_p_t hessenbergparam[nthreads];
      cstep=int(std::ceil(cstep/double(nthreads)));
      for (int j=0;j<nthreads;++j){
	cend=cstart+cstep;
	if (cend>nH) cend=nH;
	thread_hessenberg_p_t tmp={&P,&oper,cstart,cend};
	hessenbergparam[j]=tmp;
	cstart=cend;
	bool res=true;
	if (j<nthreads-1)
	  res=pthread_create(&tab[j],(pthread_attr_t *) NULL,do_hessenberg_p,(void *) &hessenbergparam[j]);
	if (res)
	  do_hessenberg_p((void *)&hessenbergparam[j]);
      }
      for (int j=0;j<nthreads;++j){
	void * ptr=(void *)&nthreads; // non-zero initialisation
	if (j<nthreads-1)
	  pthread_join(tab[j],&ptr);
      }
      oper.clear();
      if (debug_infolevel>2)
	CERR << CLOCK() << "hessenberg_ortho3 end compute P " << endl;
      return;
    }
#endif
    thread_hessenberg_p_t tmp={&P,&oper,0,nH};
    do_hessenberg_p((void *)&tmp);
    if (debug_infolevel>2)
      CERR << CLOCK() << "hessenberg_ortho3 end compute P" << endl;
    oper.clear();
  }

  void hessenberg_ortho(matrix_double & H,matrix_double & P,int firstrow,int n,bool compute_P,int already_zero,vector<giac_double> & oper){
    matrix_double::iterator Hbegin=H.begin();
    // vector<giac_double>::iterator Hiterator,Hjmptr,Hjiptr;
    int nH=int(H.size());
    if (n<0 || n>nH) 
      n=nH;
    if (firstrow<0 || firstrow>n)
      firstrow=0;
    if (already_zero==2){
      oper.push_back(2);
      oper.push_back(firstrow);
      oper.push_back(n);
    }
    giac_double t,u,norme;
    for (int m=firstrow;m<n-2;++m){
      if (debug_infolevel>=5)
	CERR << "// hessenberg reduction line " << m << endl;
      // check for a non zero coeff in the column m below ligne m+1
      int i=m+1;
      int nend=i+already_zero;
      if (nend>n) nend=n;
      for (i=m+2;i<nend;++i){
	u=H[i][m];
	if (u==0){
	  //CERR << "u=0"<<endl;
	  if (compute_P &&already_zero==2){
	    oper.push_back(1);
	    oper.push_back(0);
	  }
	  continue;
	}
	// line operation
	t=H[m+1][m];
	norme=std::sqrt(u*u+t*t);
	u=u/norme; t=t/norme; 
	if (debug_infolevel>=5)
	  CERR << "// i=" << i << " " << u <<endl;
	// H[m+1]=un*H[i]+tn*H[m+1] and H[i]=tn*H[i]-un*H[m+1];
	bi_linear_combination_CA(u,H[i],t,H[m+1],m,nH);
	// column operation:
	int nstop=nend+already_zero-1;
	if (nstop>nH)
	  nstop=nH;
	matrix_double::iterator Hjptr=H.begin(),Hjend=Hjptr+nstop;
	for (;Hjptr!=Hjend;++Hjptr){
	  giac_double *Hj=&Hjptr->front();
	  giac_double &Hjm=Hj[m+1];
	  giac_double & Hji=Hj[i];
	  giac_double tmp=Hji;
	  Hji=-u*Hjm+t*tmp;
	  Hjm=t*Hjm+u*tmp;
	}
	if (compute_P){
	  if (already_zero==2){
	    oper.push_back(u);
	    oper.push_back(t);
	  }
	  else {
	    hessenberg_ortho3_flush_p(P,compute_P,oper,true);
	    bi_linear_combination_CA(u,P[i],t,P[m+1],0,nH);
	  }
	}
      } // for i=m+2...
    } // for int m=firstrow ...
    if (debug_infolevel>2)
      CERR << CLOCK() << " hessenberg_ortho clean subdiagonal begin" << endl;
    // make 0 below subdiagonal (i<nH all matrix, i<n only relevant lines/column)
    int nstop=already_zero?n:nH;
    for (int i=2;i<nstop;i++){
      vector<giac_double>::iterator it=H[i].begin(),itend=it+i-1; // or min(i-1,n);
      for (;it!=itend;++it){
	*it=0;
      }
    }
    if (debug_infolevel>2)
      CERR << CLOCK() << " hessenberg_ortho clean subdiagonal end" << endl;
    hessenberg_ortho3_flush_p(P,compute_P,oper,false);
  }

  void hessenberg_ortho3(matrix_double & H,matrix_double & P,int firstrow,int n,bool compute_P,vector<giac_double> & oper){
    int nH=int(H.size());
    if (n<0 || n>nH) 
      n=nH;
    if (firstrow<0 || firstrow>n)
      firstrow=0;
    giac_double t,u,t2,u2,norme;
    // vector<giac_double> oper(4*(n-2-firstrow));
    oper.reserve(oper.size()+3+4*(n-2-firstrow));
    oper.push_back(3); // number of lines involved
    oper.push_back(firstrow);
    oper.push_back(n);
    int opstart=int(oper.size());
    for (int m=firstrow;m<n-2;++m){
      // check for a non zero coeff in the column m line m+1 and m+2
      int nstop=m+6;
      if (nstop>nH)
	nstop=nH;
      t=H[m+1][m];
      u=H[m+2][m];
      t2=1; u2=0;
      // line operation
      norme=std::sqrt(u*u+t*t);
      if (norme==0){//<=1e-16*std::abs(H[m][m])){
	CERR << m << " " << n-3 << endl;
	u=0; t=1; norme=0;
	if (m==n-3) {
	  oper.push_back(u);
	  oper.push_back(t);
	  oper.push_back(u2);
	  oper.push_back(t2);
	  break;
	}
      }
      else {
	u /= norme; t /= norme; 
      }
      if (m==n-3){
	// H[m+2]=u*H[m+2]+t*H[m+1] and H[m+1]=t*H[m+2]-u*H[m+1];
	// if (m!=n-3) CERR << m << " " << n-3 << endl;
	bi_linear_combination_CA(u,H[m+2],t,H[m+1],m,nH);
	H[m+2][m]=0;
	for (int j=m+1;j<nstop;++j){
	  vector<giac_double> & Hj=H[j];
	  giac_double & Hjm=Hj[m+1];
	  giac_double & Hj1=Hj[m+2];
	  giac_double tmp=t*Hjm+u*Hj1;
	  Hj1=t*Hj1-u*Hjm; 
	  Hjm=tmp;
	}
      }
      else {
	t2=norme;
	u2=H[m+3][m];
	norme=std::sqrt(u2*u2+t2*t2);
	u2 /= norme; t2 /= norme; 
#if 1
	tri_linear_combination(u,H[m+2],t,H[m+1],u2,H[m+3],t2,m);
#else
	bi_linear_combination_CA(u,H[m+2],t,H[m+1],m,nH);
	bi_linear_combination_CA(u2,H[m+3],t2,H[m+1],m,nH);
#endif
	H[m+2][m]=0;
	H[m+3][m]=0;
	// column operation lines m+1 to nstop(=m+6), cols. m+1/2/3
	matrix_double::iterator Hjptr=H.begin(),Hjend=Hjptr+nstop;
	for (Hjptr+=m+1;Hjptr!=Hjend;++Hjptr){
	  giac_double * Hj=&(*Hjptr)[m+1];
	  giac_double tmp0=*Hj;
	  ++Hj;
	  giac_double tmp1=*Hj;
	  *Hj=-u*tmp0+t*tmp1;
	  ++Hj;
	  tmp0=t*tmp0+u*tmp1;
	  tmp1=*Hj;
	  *Hj=-u2*tmp0+t2*tmp1;
	  Hj[-2]=t2*tmp0+u2*tmp1; 
	}
      }
      oper.push_back(u);
      oper.push_back(t);
      oper.push_back(u2);
      oper.push_back(t2);
    } // for int m=firstrow ...
    // finish work on H columns
    int j=0,opindex;
#if 1
    for (;j<n-4;j+=3){
      giac_double * Hj=&H[j].front(),*Hjend=Hj+n-2,*Hj1=&H[j+1].front(),*Hj2=&H[j+2].front();
      opindex=opstart;
      if (j>firstrow){
	int decal=(j-firstrow);
	Hj += decal;
	Hj1 += decal;
	Hj2 += decal;
	opindex += 4*decal;
      }
      Hj += firstrow; 
      Hj1 += firstrow; 
      Hj2 += firstrow; 
      // line j, column j (or firstrow), do operations from index j 
      // (previous operations from oper were done before)
      giac_double *opptr=&oper[opindex];
      ++Hj; ++Hj1; ++Hj2;
      if (Hj+1>=Hjend)
	break;
      giac_double t0mp0=*Hj,t0mp1=Hj[1],t0mpa,t0mpb,U,T;
      if (j>=firstrow){
	U=*opptr;
	++opptr;
	T=*opptr;
	++opptr;
	t0mpa=-U*t0mp0+T*t0mp1; // Hj[1] after 1st oper
	t0mp0=T*t0mp0+U*t0mp1; // Hj[0] after 1st oper
	t0mpb=Hj[2];
	U=*opptr;
	++opptr;
	T=*opptr;
	++opptr;
	t0mp1=-U*t0mp0+T*t0mpb; // Hj[2] after 2nd oper (in tmp1 for next iter)
	*Hj=T*t0mp0+U*t0mpb; // Hj[0] after 2nd oper, stored
	t0mp0=t0mpa; // for next iteration
	++Hj; ++Hj1; ++Hj2;
      }
      giac_double t1mp0=*Hj1,t1mp1=Hj1[1],t1mpa,t1mpb;
      if (j+1>=firstrow){
	U=*opptr;
	++opptr;
	T=*opptr;
	++opptr;
	t0mpa=-U*t0mp0+T*t0mp1; // Hj[1] after 1st oper
	t0mp0=T*t0mp0+U*t0mp1; // Hj[0] after 1st oper
	t1mpa=-U*t1mp0+T*t1mp1; 
	t1mp0=T*t1mp0+U*t1mp1; 
	t0mpb=Hj[2];
	t1mpb=Hj1[2];
	U=*opptr;
	++opptr;
	T=*opptr;
	++opptr;
	t0mp1=-U*t0mp0+T*t0mpb; // Hj[2] after 2nd oper (in t0mp1 for next iter)
	*Hj=T*t0mp0+U*t0mpb; // Hj[0] after 2nd oper, stored
	t0mp0=t0mpa; // for next iteration
	++Hj;
	t1mp1=-U*t1mp0+T*t1mpb;
	*Hj1=T*t1mp0+U*t1mpb;
	t1mp0=t1mpa; 
	++Hj1;
	++Hj2;
      }
      giac_double t2mp0=*Hj2,t2mp1=Hj2[1],t2mpa,t2mpb;
      for (;;){
	U=*opptr;
	++opptr;
	T=*opptr;
	++opptr;
	t0mpa=-U*t0mp0+T*t0mp1; // Hj[1] after 1st oper
	t0mp0=T*t0mp0+U*t0mp1; // Hj[0] after 1st oper
	t1mpa=-U*t1mp0+T*t1mp1; 
	t1mp0=T*t1mp0+U*t1mp1; 
	t2mpa=-U*t2mp0+T*t2mp1; 
	t2mp0=T*t2mp0+U*t2mp1; 
	if (Hj==Hjend) break;
	t0mpb=Hj[2];
	t1mpb=Hj1[2];
	t2mpb=Hj2[2];
	U=*opptr;
	++opptr;
	T=*opptr;
	++opptr;
	t0mp1=-U*t0mp0+T*t0mpb; // Hj[2] after 2nd oper (in t0mp1 for next iter)
	*Hj=T*t0mp0+U*t0mpb; // Hj[0] after 2nd oper, stored
	t0mp0=t0mpa; // for next iteration
	++Hj;
	t1mp1=-U*t1mp0+T*t1mpb;
	*Hj1=T*t1mp0+U*t1mpb;
	t1mp0=t1mpa; 
	++Hj1;
	t2mp1=-U*t2mp0+T*t2mpb;
	*Hj2=T*t2mp0+U*t2mpb;
	t2mp0=t2mpa; 
	++Hj2;
      }
      *Hj=t0mp0;
      Hj[1]=t0mpa;
      *Hj1=t1mp0;
      Hj1[1]=t1mpa;
      *Hj2=t2mp0;
      Hj2[1]=t2mpa;
    }
#endif
    for (;j<n-2;++j){
      giac_double * Hj=&H[j].front(),*Hjend=Hj+n;
      opindex=opstart;
      if (j>firstrow){
	int decal=(j-firstrow);
	Hj += decal;
	opindex += 4*decal;
      }
      Hj += firstrow; 
      // line j, column j (or firstrow), do operations from index j 
      // (previous operations from oper were done before)
      giac_double *opptr=&oper[opindex];
      ++Hj;
      giac_double tmp0=*Hj,tmp1=Hj[1],tmpa;
      for (Hjend-=2;;){
	giac_double tmpb,U,T;
	U=*opptr;
	++opptr;
	T=*opptr;
	++opptr;
	tmpa=-U*tmp0+T*tmp1; // Hj[1] after 1st oper
	tmp0=T*tmp0+U*tmp1; // Hj[0] after 1st oper
	if (Hj==Hjend) break;
	tmpb=Hj[2];
	U=*opptr;
	++opptr;
	T=*opptr;
	++opptr;
	tmp1=-U*tmp0+T*tmpb; // Hj[2] after 2nd oper (in tmp1 for next iter)
	*Hj=T*tmp0+U*tmpb; // Hj[0] after 2nd oper, stored
	tmp0=tmpa; // for next iteration
	++Hj;
      }
      *Hj=tmp0;
      Hj[1]=tmpa;
    }
    hessenberg_ortho3_flush_p(P,compute_P,oper,false);
  }

  // Hessenberg reduction, P is orthogonal and should be initialized to identity
  // trn(P)*H*P=original
  // already_zero is either <=0 or an integer such that H[i][j]==0 if i>j+already_zero
  // (already_zero==1 if H is hessenberg, ==2 or 3 for Francis algorithm)
  void tri_linear_combination(const giac_double & c1,const vector<giac_double> & x1,const giac_double & c2,const vector<giac_double> & x2,const giac_double & c3,const vector<giac_double> & x3,vector<giac_double> & y){
    vector<giac_double>::const_iterator it1=x1.begin(),it2=x2.begin(),it3=x3.begin(),it3end=x3.end();
    vector<giac_double>::iterator jt=y.begin();
    for (;it3!=it3end;++jt,++it1,++it2,++it3){
      *jt=c1*(*it1)+c2*(*it2)+c3*(*it3);
    }
  }

#ifdef NSPIRE
  template<class T>
  nio::ios_base<T> & operator << (nio::ios_base<T> & os,const vector<giac_double> & m){
    int s=int(m.size());
    os << "[";
    for (int i=0;i<s;++i){
      os << m[i] ;
      if (i==s
    }
    return "]" << os;
  }

  template<class T>
  nio::ios_base<T> & operator << (nio::ios_base<T> & os,const matrix_double & m){
    int s=int(m.size());
    for (int i=0;i<s;++i)
      os << m[i] << endl;
    return os;
  }

  void matrix_double::dbgprint() const { COUT << *this << std::endl; }

  template<class T>
  nio::ios_base<T> & operator << (nio::ios_base<T> & os,const vector< complex_double > & m){
    int s=int(m.size());
    for (int i=0;i<s;++i)
      os << m[i] << " ";
    return os;
  }

  template<class T>
  nio::ios_base<T> & operator << (nio::ios_base<T> & os,const matrix_complex_double & m){
    int s=int(m.size());
    for (int i=0;i<s;++i)
      os << m[i] << endl;
    return os;
  }

#else

  ostream & operator << (ostream & os,const vector<giac_double> & m){
    int s=int(m.size());
    os << "[";
    for (int i=0;i<s;++i){
      os << m[i]; 
      if (i+1!=s)
	os << ",";
    }
    return os << "]";
  }

  ostream & operator << (ostream & os,const matrix_double & m){
    int s=int(m.size());
    os << "[";
    for (int i=0;i<s;++i){
      os << m[i] ;
      if (i+1!=s)
	os << ",";
      os << endl;
    }
    return os << "]";
  }

  void matrix_double::dbgprint() const { COUT << *this << std::endl; }

  ostream & operator << (ostream & os,const vector< complex_double > & m){
    int s=int(m.size());
    for (int i=0;i<s;++i)
      os << m[i] << " ";
    return os;
  }

  ostream & operator << (ostream & os,const matrix_complex_double & m){
    int s=int(m.size());
    for (int i=0;i<s;++i)
      os << m[i] << endl;
    return os;
  }

#endif // NSPIRE

  void matrix_complex_double::dbgprint() const { COUT << *this << std::endl; }

  void francis_iterate1(matrix_double & H,int n1,int n2,matrix_double & P,double eps,bool compute_P,giac_double l1,bool finish,vector<giac_double> & oper){
    if (debug_infolevel>2)
      CERR << CLOCK() << " iterate1 " << n1 << " " << n2 << endl;
    int n_orig=int(H.size());
    giac_double x,y;
    if (finish){
      // [[a,b],[c,d]] -> [b,l1-a] or [l1-d,c] as first eigenvector
      giac_double a=H[n2-2][n2-2],b=H[n2-2][n2-1],c=H[n2-1][n2-2],d=H[n2-1][n2-1];
      giac_double l1a=l1-a,l1d=l1-d;
      if (std::abs(l1a)>std::abs(l1d)){
	x=b; y=l1a;
      }
      else {
	x=l1d; y=c;
      }
    }
    else 
      x=H[n1][n1]-l1,y=H[n1+1][n1];
    giac_double xy=std::sqrt(x*x+y*y),tmp1;
    if (xy==0) return;
    // normalize 
    x = x/xy; y = y/xy;
    // apply Q on H and P: line operations on H and P
    bi_linear_combination_AC(x,H[n1],y,H[n1+1]);
    if (compute_P){
      oper.push_back(-2);
      oper.push_back(n1);
      oper.push_back(n1+1);
      oper.push_back(x);
      oper.push_back(y);	    
      hessenberg_ortho3_flush_p(P,compute_P,oper,false);
      // bi_linear_combination_AC(x,P[n1],y,P[n1+1]);
    }
    // now columns operations on H (not on P)
    for (int j=0;j<n_orig;++j){
      vector<giac_double> & Hj=H[j];
      giac_double & Hjm1=Hj[n1];
      giac_double & Hjm2=Hj[n1+1];
      tmp1=Hjm1*x+Hjm2*y; // tmp1=Hjm1*c11+Hjm2*c21;
      Hjm2=Hjm1*y-Hjm2*x; // tmp2=Hjm1*c12+Hjm2*c22;
      Hjm1=tmp1;
    }
    if (debug_infolevel>2)
      CERR << CLOCK() << " iterate1 hessenberg " << n1 << " " << n2 << endl;
    hessenberg_ortho(H,P,n1,n2,compute_P,2,oper); 
  }

  // declaration for recursive use
  bool in_francis_schur(matrix_double & H,int n1,int n2,matrix_double & P,int maxiter,double eps,bool compute_P,matrix_double & Haux,matrix_double & T,bool in_recursion,vector<giac_double> & oper);

  void do_francis_iterate2(matrix_double & H,int n1,int n2,giac_double s,giac_double p,matrix_double & P,bool compute_P,vector<giac_double> & oper){
    // compute (H-l2)(H-l1)=(H-s)*H+p on n1-th basis vector (if n1==0, on [1,0,...,0])
    giac_double ha=H[n1][n1],hb=H[n1][n1+1],
      hd=H[n1+1][n1],he=H[n1+1][n1+1],
      hh=H[n1+2][n1+1];
    giac_double x=hb*hd+ha*(ha-s)+p,y=hd*(he-s+ha),z=hd*hh,c11,c12,c13,c21,c22,c23,c31,c32,c33;
    if (x>0){
      x=-x; y=-y; z=-z;
    }
    giac_double xyz=std::sqrt(x*x+y*y+z*z),xm1;
    c11=x=x/xyz; c12=c21=y=y/xyz; c13=c31=z=z/xyz;
    xm1=1-x;      
    c22=(x*x+z*z-x)/xm1;
    c33=(x*x+y*y-x)/xm1;
    c32=c23=-(y*z)/xm1;
    // NB for complex coeffs, once x is real the matrix is
    // [[x,  conj(y),       conj(z)       ]
    //  [y,  1-|y|^2/xm1,   -conj(y)*z/xm1]
    //  [z, -y*conj(z)/xm1, 1-|z|^2/xm1   ]]
    // CERR << "[[" << c11 <<"," << c12 << "," << c13 << "],[" <<  c21 <<"," << c22 << "," << c23 << "],[" << c31 <<"," << c32 << "," << c33 << "]]" << endl;
    // columns operations on H (not on P)
    // since H is tridiagonal, H[j][n1+2]==0 if j=>n1+4
    int nend=int(H.size());
    if (n1+4<nend)
      nend=n1+4;
    for (int j=0;j<nend;++j){
      vector<giac_double> & Hj=H[j];
      giac_double & Hjm1=Hj[n1];
      giac_double & Hjm2=Hj[n1+1];
      giac_double & Hjm3=Hj[n1+2];
      giac_double tmp1=Hjm1*c11+Hjm2*c21+Hjm3*c31;
      giac_double tmp2=Hjm1*c12+Hjm2*c22+Hjm3*c32;
      Hjm3=Hjm1*c13+Hjm2*c23+Hjm3*c33;
      Hjm1=tmp1;
      Hjm2=tmp2;
    }
    // line operations on H
    tri_linear_combination(c11,H[n1],c12,H[n1+1],c13,H[n1+2],c22,c23,c33);
    // tri_linear_combination(c11,H[n1],c12,H[n1+1],c13,H[n1+2],v1);
    // tri_linear_combination(c21,H[n1],c22,H[n1+1],c23,H[n1+2],v2);
    // tri_linear_combination(c31,H[n1],c32,H[n1+1],c33,H[n1+2],H[n1+2]);
    // H[n1].swap(v1);
    // H[n1+1].swap(v2);
    // H[n1+2].swap(v3);
    // CERR << H << endl;
    if (compute_P){
      oper.push_back(-3);
      oper.push_back(n1);
      oper.push_back(n1);
      oper.push_back(c11);
      oper.push_back(c12);
      oper.push_back(c13);
      oper.push_back(c22);
      oper.push_back(c23);
      oper.push_back(c33);
      hessenberg_ortho3_flush_p(P,compute_P,oper,false);
      // tri_linear_combination(c11,P[n1],c12,P[n1+1],c13,P[n1+2],c22,c23,c33);
      // tri_linear_combination(c11,P[n1],c12,P[n1+1],c13,P[n1+2],v1);
      // tri_linear_combination(c21,P[n1],c22,P[n1+1],c23,P[n1+2],v2);
      // tri_linear_combination(c31,P[n1],c32,P[n1+1],c33,P[n1+2],P[n1+2]);
      // P[n1].swap(v1);
      // P[n1+1].swap(v2);
    }
    // CERR << H << endl;
    // chase the bulge: Hessenberg reduction on 2 subdiagonals
    if (debug_infolevel>2)
      CERR << CLOCK() << " iterate2 hessenberg " << n1 << " " << n2 << endl;
    hessenberg_ortho3(H,P,n1,n2,compute_P,oper); 
  }

  // #define GIAC_SCHUR_RECURSE_ALL 

  // oper is a work register for hessenberg reduction
  void francis_iterate2(matrix_double & H,int n1,int n2,matrix_double & P,double eps,bool compute_P,matrix_double & Haux,matrix_double & T,bool in_recursion,vector<giac_double> & oper){
    // now H is proper hessenberg (indices n1 to n2-1)
    if (debug_infolevel>2)
      CERR << CLOCK() << " iterate2 " << n1 << " " << n2 << endl;
    giac_double s,p; // s=sum of shifts, p=product
    giac_double ok=std::abs(H[n2-1][n2-2]/H[n2-1][n2-1]);
    if (
#ifdef GIAC_HAS_STO38 // otherwise p:=48*x*(1+x)^60 -(1+x)^60 +1; proot(p) crashes
	0 &&
#endif
	!in_recursion && H.size()>=50){
      // search for a small coeff on the subdiagonal in the last elements
      int k=-1,ksmallest=-1;
      const double limite=0.5;giac_double savetest,smallest=-1;
      if (ok<limite)
	k=n2-1;
      else
	ok=limite;
      for (int k0=n2-2;k0>n2-200 && k0>(0.2*n1+0.8*n2)
	     ;--k0
	   ){
	giac_double test0=std::abs(H[k0][k0-1]/H[k0-1][k0-1]);
	if (smallest<0 || test0<smallest){	
	  smallest=test0;
	  ksmallest=k0;
	}
	if (test0<ok){
	  k=k0;
	  ok=test0;
	  savetest=test0;
	  if (test0<1e-7){
	    if (debug_infolevel>2)
	      CERR << CLOCK() << " small subdiag. element found " << test0 << " line " << k << endl;
	    break;
	  }
	}
	// testing from n1 to k-1 is slower...
	int k1=n1+n2-k0;
	if (0 && k1<k0 && k1>n1){
	  giac_double test1=std::abs(H[k1][k1-1]/H[k1-1][k1-1]);
	  if (test1<ok){
	    k=k1;
	    ok=test1;
	    savetest=test1;
	    if (test1<1e-7){
	      if (debug_infolevel>2)
		CERR << CLOCK() << " small subdiag. element found " << test1 << " line " << k << endl;
	      break;
	    }
	  }
	}
	ok *= 1.06;
	if (ok>limite)
	  ok=limite;
      }
      if (0 && k==-1){
	k=ksmallest;
	if (debug_infolevel)
	  CERR << "No split found, using " << k << endl;
      }
      if (k==n2-1){ // was <= std::sqrt(eps)
	francis_iterate1(H,n1,n2,P,eps,compute_P,H[n2-1][n2-1],false,oper);
	return;
      }
      if (k>n1+2 && k<n2-2){
	// 1 or 2 eigenvalues of the submatrix k..n2-1 or n1..k-1 will be taken as shift
	unsigned d=n2-k;
	bool n1k=false;
	if (k-n1<int(d)){
	  d=k-n1;
	  n1k=true;
	}
	T.resize(d);
	matrix_double TP;
	for (unsigned i=0;i<d;++i){
	  T[i].swap(Haux[i]);
	  T[i].clear();
	}
	// copy submatrix
	for (unsigned i=0;i<d;i++){
	  T[i].reserve(d);
	  if (n1k){
	    for (unsigned j=0;j<d;j++){
	      T[i].push_back(H[n1+i][n1+j]);
	    }
	  }
	  else {
	    for (unsigned j=0;j<d;j++){
	      T[i].push_back(H[k+i][k+j]);
	    }
	  }
	}
	if (debug_infolevel>2 && d>=3){
	  if (n1k)
	    CERR << CLOCK() << " ok=" << ok << ", test=" << savetest << ", recursive call dim " << d << " n1 " << n1 <<" on ... [" << T[d-2][d-3] << "," << T[d-2][d-2] << "," << T[d-2][d-1] << " ],[" << T[d-1][d-2] << "," << T[d-1][d-1] << "]" << endl; 
	  else 
	    CERR << CLOCK() << " ok=" << ok << ", test=" << savetest << ", recursive call dim " << d << " n2 " << n2 <<" on ... [" << T[d-2][d-3] << "," << T[d-2][d-2] << "," << T[d-2][d-1] << " ],[" << T[d-1][d-2] << "," << T[d-1][d-1] << "]" << endl;
	}
	int save_debug_infolevel=debug_infolevel;
	debug_infolevel=0;
	// schur it
	vector<giac_double> oper_recursive;
	if(in_francis_schur(T,0,d,TP,25,eps,false /* TP not computed*/,Haux,T,true /* in_recursion */,oper_recursive)){
	  debug_infolevel=save_debug_infolevel;
	  if (debug_infolevel>2){
	    CERR << CLOCK() << " end recursive call on ... [" << T[d-2][d-3] << "," << T[d-2][d-2] << "," << T[d-2][d-1] << " ][0," << T[d-1][d-2] << "," << T[d-1][d-1] << "]" << endl;
	    if (debug_infolevel>3){
	      CERR << "success subdiag. " ;
	      for (unsigned i=1;i<d;++i)
		CERR << T[i][i-1] << ",";
	      CERR << endl;
	    }
	  }
#ifdef GIAC_SCHUR_RECURSE_ALL
	  for (int k=d-1;k>=2;){
	    if (std::abs(T[k-1][k-2])>1e-5){
	      francis_iterate1(H,n1,n2,P,eps,compute_P,T[k][k],false,oper);
	      // if (std::abs(H[n2-2][n2-1])>1e-5) break;
	      k--; 
	      continue;
	    }
	    s=T[k-1][k-1]+T[k][k];
	    p=T[k-1][k-1]*T[k][k]-T[k][k-1]*T[k-1][k];
	    do_francis_iterate2(H,n1,n2,s,p,P,compute_P,oper);
	    // if (std::abs(H[n2-3][n2-2])>1e-5) break;
	    k-=2;
	  }
#else
	  if (std::abs(T[d-2][d-3])>1e-5){
	    francis_iterate1(H,n1,n2,P,eps,compute_P,T[d-1][d-1],false,oper);
	    for (unsigned i=0;i<T.size();++i){
	      Haux[i].swap(T[i]);
	    }
	    return;
	  }
	  k=d-1;
	  s=T[k-1][k-1]+T[k][k];
	  p=T[k-1][k-1]*T[k][k]-T[k][k-1]*T[k-1][k];
	  do_francis_iterate2(H,n1,n2,s,p,P,compute_P,oper);
#endif
	  return;
	} // end recursive call
	else {
	  if (debug_infolevel>2){
	    CERR << CLOCK() << " recursive call failure" << endl;
	    if (debug_infolevel>3){
	      CERR << "failure subdiag. " ;
	      for (unsigned i=1;i<d;++i)
		CERR << T[i][i-1] << ",";
	      CERR << endl;
	    }
	  }
	}
	for (unsigned i=0;i<T.size();++i){
	  Haux[i].swap(T[i]);
	}
	if (debug_infolevel>2)
	  CERR << CLOCK() << " swapped " << endl;
      } // if k>=n1 && k<n2-2
    } // end if (!in_recursion && H.size()>=50)
    else { 
      if (ok<1e-2){
	francis_iterate1(H,n1,n2,P,eps,compute_P,H[n2-1][n2-1],false,oper);
	return;
      }
    }
    // find eigenvalues l1 and l2 of last 2x2 matrix, they will be taken as shfits
    s=H[n2-2][n2-2]+H[n2-1][n2-1];
    p=H[n2-2][n2-2]*H[n2-1][n2-1]-H[n2-1][n2-2]*H[n2-2][n2-1];
    if (s==int(s) && p==int(p))
      s=s*(1+(100*eps*giac_rand(context0)/rand_max2));
    // CERR << p << " " << s << " " << eps << endl << std::abs(H[n2-2][n2-2]) << " " << std::abs(H[n2-1][n2-1]) << endl;
    if (p==s*s/4 || (std::abs(H[n2-2][n2-2])<eps &&std::abs(H[n2-1][n2-1])<eps) ){
      // multiple root 
      s += giac_rand(context0)*(H[n2-1][n2-2]+std::sqrt(std::abs(p)))/rand_max2;
      // CERR << "new s " << s << endl;
    }
    do_francis_iterate2(H,n1,n2,s,p,P,compute_P,oper);
  }

  bool in_francis_schur(matrix_double & H,int n1,int n2,matrix_double & P,int maxiter,double eps,bool compute_P,matrix_double & Haux,matrix_double & T,bool in_recursion,vector<giac_double> & oper){
    if (n2-n1<=1)
      return true; // nothing to do
    if (n2-n1==2){ // 2x2 submatrix, we know how to diagonalize
      giac_double l1,l2;
      if (eigenval2(H,n2,l1,l2)){
	francis_iterate1(H,n1,n2,P,eps,compute_P,l1,true,oper);
      }
      return true;
    }
    for (int niter=0;n2-n1>2 && niter<maxiter;niter++){
      if (debug_infolevel>=2)
	CERR << CLOCK() << " qr iteration number " << niter << " " << endl;
      if (debug_infolevel>=5)
	H.dbgprint();
      // check if one subdiagonal element is sufficiently small, if so 
      // we can increase n1 or decrease n2 or split
      giac_double ratio,coeff=1;
      if (niter>maxiter-3)
	coeff=100;
      if (debug_infolevel>2)
	CERR << CLOCK() << " first ratios ";
      for (int i=n2-2;i>=n1;--i){
      // for (int i=n1;i<=n2-2;++i){
	ratio=std::abs(H[i+1][i])/(std::abs(H[i][i])+(i<n2-2?std::abs(H[i+2][i+1]):0));
	if (debug_infolevel>2 && i>n2-25)
	  CERR << ratio << " ";
	if (ratio<coeff*eps){ 
	  // do a final iteration if i==n2-2 or n2-3? does not improve much precision
	  // if (i==n2-3) francis_iterate2(H,n1,n2,P,eps,compute_P,Haux,T,in_recursion,oper);
	  // submatrices n1..i and i+1..n2-1
	  if (debug_infolevel>2)
	    CERR << endl << CLOCK() << " Francis split double " << giacmin((i+1)-n1,n2-(i+1)) << " [" << n1 << " " << i+1 << " " << n2 << "]" << endl;
#ifdef GIAC_SCHUR_RECURSE_ALL
	  if (!in_francis_schur(H,n1,i+1,P,maxiter,eps,compute_P,Haux,T,in_recursion,oper)){
	    in_francis_schur(H,i+1,n2,P,maxiter,eps,compute_P,Haux,T,in_recursion,oper);
	    return false;
	  }
#else
	  if (in_recursion && n2-(i+1)<=2) 
	    return true;
	  if (!in_recursion && !in_francis_schur(H,n1,i+1,P,maxiter,eps,compute_P,Haux,T,in_recursion,oper)){
	    in_francis_schur(H,i+1,n2,P,maxiter,eps,compute_P,Haux,T,in_recursion,oper);
	    return false;
	  }
#endif
	  return in_francis_schur(H,i+1,n2,P,maxiter,eps,compute_P,Haux,T,in_recursion,oper);
	}
	if (i<=n1+1 && ratio<std::sqrt(eps)){
	  if (debug_infolevel>3)
	    CERR << "splitable from begin " << n1 << "," << n2 << endl;
	  // exchange lines/columns n1/n2-1
	  // exchange(H,P,compute_P,n1,n2-1);
	  // break;
	}
	// IMPROVE: in that case we should iterate_n using the eigenvalues of the
	// submatrix i+1..n2-1
      }
      if (debug_infolevel>2)
	CERR << endl;
      francis_iterate2(H,n1,n2,P,eps,compute_P,Haux,T,in_recursion,oper);
    } // end for loop on niter
    return false;
  }

  // Francis algo on H after balance
  bool balanced_eigenvalues(matrix_double & H,vecteur & res,int maxiter,double eps,bool is_hessenberg,GIAC_CONTEXT){
    vector<giac_double> d;
    if (!balance_krylov(H,d,5,1e-8))
      return false;
    int n1=0,n2=int(H.size());
    // compute d*H*d^-1: H_jk <- d_jj*H_jk/d_kk
    for (int j=n1;j<n2;++j){
      vector<giac_double> & Hj=H[j];
      for (int k=n1;k<n2;++k){
	Hj[k]=d[j]*Hj[k]/d[k];
      }
    }
    // schur on d*H*d^-1
    matrix_double P;
    if (!francis_schur(H,n1,n2,P,maxiter,eps,is_hessenberg,false))
      return false;
    // Invariant if compute_P was true trn(P)*d*H*d^-1*P=orig matrix
    // same eigenvalues, but different eigenvectors
    return schur_eigenvalues(H,res,eps,contextptr);
  }

  // Francis algorithm on submatrix rows and columns n1..n2-1
  // Invariant: trn(P)*H*P=orig matrix, complex_schur not used for giac_double coeffs
  bool francis_schur(matrix_double & H,int n1,int n2,matrix_double & P,int maxiter,double eps,bool is_hessenberg,bool compute_P){
    vecteur eigenv;
    if (n1==0 
	// && n2<400 
	&& lapack_schur(H,P,compute_P,eigenv))
      return true;
#ifdef VISUALC // tested on 100 rand poly of degree 10, should work...
    // return false; 
#endif
    // int n_orig=H.size();//,nitershift0=0;
    if (!is_hessenberg){
      if (debug_infolevel>0)
	CERR << CLOCK() << " start hessenberg real n=" << H.size() << endl;
#if 1
      hessenberg_householder(H,P,compute_P);
#else
      hessenberg_ortho(H,P,0,n_orig,compute_P,0); // insure Hessenberg form (on the whole matrix)
#endif
      if (debug_infolevel>0)
	CERR << CLOCK() << " hessenberg real done" <<endl;
    }
    matrix_double Haux(n2/2),T(n2/2);
    vector<giac_double> oper;
    oper.reserve(P.size()*(P.size()/10+4)+3);
    // adjust maxiter for large matrices
    if (H.size()>=50)
      maxiter=(maxiter*int(H.size()))/50;
    bool res=in_francis_schur(H,n1,n2,P,maxiter,eps,compute_P,Haux,T,false,oper);
    if (compute_P)
      hessenberg_ortho3_flush_p(P,compute_P,oper,true);
    if (debug_infolevel>0)
      CERR << CLOCK() << " schur real done" <<endl;
    return res;
  }

  // conj(a)*A+conj(c)*C->C
  // c*A-a*C->A
  void bi_linear_combination( complex_double  a,vector< complex_double > & A, complex_double  c,vector< complex_double > & C,int cstart,int cend){
    complex_double  * Aptr=&A.front()+cstart;
    complex_double  * Cptr=&C.front()+cstart,* Cend=Cptr+(cend-cstart);
    complex_double ac=conj(a),cc=conj(c);
    for (;Cptr!=Cend;++Aptr,++Cptr){
      complex_double  tmp=c*(*Aptr)-a*(*Cptr);
      *Cptr=ac*(*Aptr)+cc*(*Cptr);
      *Aptr=tmp;
    }
  }

  void hessenberg_ortho(matrix_complex_double & H,matrix_complex_double & P,int firstrow,int n,bool compute_P,int already_zero){
    int nH=int(H.size());
    if (n<0 || n>nH) 
      n=nH;
    if (firstrow<0 || firstrow>n)
      firstrow=0;
    complex_double  t,u,tc,uc;
    double norme;
    for (int m=firstrow;m<n-2;++m){
      if (debug_infolevel>=5)
	CERR << "// hessenberg reduction line " << m << endl;
      // if initial Hessenberg check for a non zero coeff in the column m below ligne m+1
      int i=m+1;
      int nend=n;
      if (already_zero){
	if (i+already_zero<n)
	  nend=i+already_zero;
      }
      else {
	double pivot=0;
	int pivotline=0;
	for (;i<nend;++i){
	  double t=abs(H[i][m]);
	  if (t>pivot){
	    pivotline=i;
	    pivot=t;
	  }
	}
	if (pivot==0)
	  continue;
	i=pivotline;
	// exchange line and columns
	if (i>m+1){
	  swap(H[i],H[m+1]);
	  if (compute_P)
	    swap(P[i],P[m+1]);
	  for (int j=0;j<n;++j){
	    vector< complex_double > & Hj=H[j];
#ifdef VISUALC
	    complex<double> cc=Hj[i];
	    Hj[i]=Hj[m+1];
	    Hj[m+1]=cc;
#else
	    swap< complex_double >(Hj[i],Hj[m+1]);
#endif
	  }
	}
      }
      // now coeff at line m+1 column m is H[m+1][m]=t!=0
      for (i=m+2;i<nend;++i){
	u=H[i][m];
	if (u==0)
	  continue;
	// line operation
	t=H[m+1][m];
	norme=std::sqrt(norm(u)+norm(t));
	u=u/norme; t=t/norme;
	uc=conj(u); tc=conj(t);
	if (debug_infolevel>=5)
	  CERR << "// i=" << i << " " << u <<endl;
	// H[m+1]=uc*H[i]+tc*H[m+1] and H[i]=t*H[i]-u*H[m+1];
	bi_linear_combination(u,H[i],t,H[m+1],m,nH);
	// column operation:
	int nstop=already_zero?nend+already_zero-1:nH;
	if (nstop>nH)
	  nstop=nH;
	matrix_complex_double::iterator Hjptr=H.begin(),Hjend=Hjptr+nstop;
	for (;Hjptr!=Hjend;++Hjptr){
	  complex_double  *Hj=&Hjptr->front();
	  complex_double  Hjm=Hj[m+1],Hji=Hj[i];
	  Hj[i]=-uc*Hjm+tc*Hji;
	  Hj[m+1]=t*Hjm+u*Hji;
	}
	if (compute_P){
	  bi_linear_combination(u,P[i],t,P[m+1],0,nH);
	}
      } // for i=m+2...
    } // for int m=firstrow ...
  }

  // a*A+c*C->A
  // c*A-a*C->C
  void bi_linear_combination(double a,vector< complex_double > & A,complex_double c,vector< complex_double > & C){
    complex_double * Aptr=&A.front();
    complex_double * Cptr=&C.front(),* Cend=Cptr+C.size();
    complex_double cc=conj(c);
    for (;Cptr!=Cend;++Aptr,++Cptr){
      complex_double tmp=a*(*Aptr)+cc*(*Cptr);
      *Cptr=c*(*Aptr)-a*(*Cptr);
      *Aptr=tmp;
    }
  }

  double complex_abs(const complex_double & c){
#ifdef EMCC
    double r=c.real(),i=c.imag();
    r=std::sqrt(r*r+i*i);
    return r;
#else
    return std::abs(c);
#endif
  }

  void francis_iterate1(matrix_complex_double & H,int n1,int n2,matrix_complex_double & P,double eps,bool compute_P,complex_double l1,bool finish){
    if (debug_infolevel>2)
      CERR << CLOCK() << " iterate1 " << n1 << " " << n2 << endl;
    int n_orig=int(H.size());
    complex_double x,y,yc;
    if (finish){
      // [[a,b],[c,d]] -> [b,l1-a] or [l1-d,c] as first eigenvector
      complex_double a=H[n2-2][n2-2],b=H[n2-2][n2-1],c=H[n2-1][n2-2],d=H[n2-1][n2-1];
      complex_double l1a=l1-a,l1d=l1-d;
      if (complex_abs(l1a)>complex_abs(l1d)){
	x=b; y=l1a;
      }
      else {
	x=l1d; y=c;
      }
    }
    else {
      x=H[n1][n1]-l1,y=H[n1+1][n1];
      if (std::abs(x)<eps && std::abs(y-1.0)<eps){
	x = double(giac_rand(context0))/rand_max2;
      }
    }
    // make x real
    double xr=real(x),xi=imag(x),yr=real(y),yi=imag(y),X;
    X = std::sqrt(xr*xr+xi*xi);
    if (X!=0){
      // gen xy = gen(xr/x,-xi/x); y=y*xy;
      y = complex_double ((yr*xr+yi*xi)/X,(yi*xr-yr*xi)/X); 
      yr=real(y); yi=imag(y);
    }
    double xy=std::sqrt(X*X+yr*yr+yi*yi);
    // normalize eigenvector
    X = X/xy; y = y/xy;	yc=conj(y);
    // compute reflection matrix such that Q*[1,0]=[x,y]
    // hence column 1 is [x,y] and column2 is [conj(y),-x]
    // apply Q on H and P: line operations on H and P
    // complex_double c11=x, c12=conj(y,contextptr),
    //                 c21=y, c22=-x;
    // apply Q on H and P: line operations on H and P
    bi_linear_combination(X,H[n1],y,H[n1+1]);
    if (compute_P)
      bi_linear_combination(X,P[n1],y,P[n1+1]);
    // now columns operations on H (not on P)
    for (int j=0;j<n_orig;++j){
      vector< complex_double > & Hj=H[j];
      complex_double & Hjm1=Hj[n1];
      complex_double & Hjm2=Hj[n1+1];
      complex_double tmp1=Hjm1*X+Hjm2*y; // tmp1=Hjm1*c11+Hjm2*c21;
      Hjm2=Hjm1*yc-Hjm2*X; // tmp2=Hjm1*c12+Hjm2*c22;
      Hjm1=tmp1;
    }
    if (debug_infolevel>2)
      CERR << CLOCK() << " iterate1 hessenberg " << n1 << " " << n2 << endl;
    hessenberg_ortho(H,P,n1,n2,compute_P,2); 
  }

  bool in_francis_schur(matrix_complex_double & H,int n1,int n2,matrix_complex_double & P,int maxiter,double eps,bool compute_P,matrix_complex_double & Haux,bool only_one);

  void francis_iterate2(matrix_complex_double & H,int n1,int n2,matrix_complex_double & P,double eps,bool compute_P,matrix_complex_double & Haux,bool only_one){
    // int n_orig(H.size());
    // now H is proper hessenberg (indices n1 to n2-1)
    if (debug_infolevel>2)
      CERR << CLOCK() << " iterate2 " << n1 << " " << n2 << endl;
    complex_double s=H[n2-1][n2-1]; 
    double ok=complex_abs(H[n2-1][n2-2])/complex_abs(H[n2-1][n2-1]);
    if (!only_one && H.size()>=50){
      // search for a small coeff on the subdiagonal in the last elements
      int k=-1;
      if (ok<0.5)
	k=n2-1;
      else
	ok=0.5;
      for (int k0=n2-2;k0>(n1+n2)/2 
	     // && (n2-k0)*(n2-k0)<H.size()
	     ;--k0
	     //,ok*=0.99
	   ){
	double test=1.79769313486e+308;
	test=complex_abs(H[k0][k0-1])/complex_abs(H[k0-1][k0-1]);
	if (test<ok){
	  k=k0;
	  ok=test;
	}
      }
      if (k>=n1 && k<n2-2){
	// 1 eigenvalue of the submatrix k..n2-1 will be taken as shift
	unsigned d=n2-k;
	matrix_complex_double T(d),TP;
	for (unsigned i=0;i<d;++i){
	  T[i].swap(Haux[i]);
	  T[i].clear();
	}
	// copy submatrix
	for (unsigned i=0;i<d;i++){
	  T[i].reserve(d);
	  for (unsigned j=0;j<d;j++){
	    T[i].push_back(H[k+i][k+j]);
	  }
	}
	if (debug_infolevel>2)
	  CERR << CLOCK() << " recursive call dim " << d << " on ... [" << T[d-2][d-3] << "," << T[d-2][d-2] << "," << T[d-2][d-1] << " ][" << T[d-1][d-2] << "," << T[d-1][d-1] << "]" << endl;
	int save_debug_infolevel=debug_infolevel;
	debug_infolevel=0;
	// schur it
	if(in_francis_schur(T,0,d,TP,25,eps,false,Haux,true)){
	  debug_infolevel=save_debug_infolevel;
	  if (debug_infolevel>2)
	    CERR << CLOCK() << " end recursive call on ... [" << T[d-2][d-3] << "," << T[d-2][d-2] << "," << T[d-2][d-1] << " ][" << T[d-1][d-2] << "," << T[d-1][d-1] << "]" << endl;
	  s=T[d-1][d-1];
	}
	for (unsigned i=0;i<T.size();++i){
	  Haux[i].swap(T[i]);
	}
      }
    } // if (!only_one  && H.size()>=50)
    else {
      if (debug_infolevel>2)
	CERR << "ok " << ok << endl;
      if (n2-n1==2 ||(ok>1e-1 && n2-n1>2 && complex_abs(H[n2-2][n2-3])<1e-2*complex_abs(H[n2-2][n2-2]))){
	complex_double a=H[n2-2][n2-2],b=H[n2-2][n2-1],c=H[n2-1][n2-2],d=H[n2-1][n2-1];
	complex_double delta=a*a-2.0*a*d+d*d+4.0*b*c;
	if (debug_infolevel>2)
	  CERR << "delta " << delta << endl;
#ifdef EMCC
	delta=std::exp(std::log(delta)/2.0);
#else
	delta=sqrt(delta);
#endif
	if (debug_infolevel>2)
	  CERR << "delta " << delta << endl;
	complex_double l1=(a+d+delta)/2.0;
	complex_double l2=(a+d-delta)/2.0;
	s=l1;
      }
    }
    francis_iterate1(H,n1,n2,P,eps,compute_P,s,false);
  }

  // EIGENVALUES for double coeff
  bool eigenval2(matrix_complex_double & H,int n2,complex_double & l1, complex_double & l2){
    complex_double a=H[n2-2][n2-2],b=H[n2-2][n2-1],c=H[n2-1][n2-2],d=H[n2-1][n2-1];
    complex_double delta=a*a-complex_double(2)*a*d+d*d+complex_double(4)*b*c;
    if (debug_infolevel>2)
      CERR << "eigenval2([[" << a << "," << b << "],[" << c << "," << d << "]], delta=" << delta << endl;
    delta=std::sqrt(delta);
    l1=(a+d+delta)/complex_double(2);
    l2=(a+d-delta)/complex_double(2);
    return true;
  }

  bool in_francis_schur(matrix_complex_double & H,int n1,int n2,matrix_complex_double & P,int maxiter,double eps,bool compute_P,matrix_complex_double & Haux,bool only_one){
    if (debug_infolevel>0)
      CERR << " francis complex " << H << endl << n1 << " " << n2 << " " << maxiter << " " << eps << endl;
    if (n2-n1<=1)
      return true; // nothing to do
    if (n2-n1==2){ // 2x2 submatrix, we know how to diagonalize
      complex_double l1,l2;
      if (eigenval2(H,n2,l1,l2)){
	francis_iterate1(H,n1,n2,P,eps,compute_P,l1,true);
      }
      return true;
    }
    for (int niter=0;n2-n1>1 && niter<maxiter;niter++){
      // check if one subdiagonal element is sufficiently small, if so 
      // we can increase n1 or decrease n2 or split
      if (debug_infolevel>2)
	CERR << "niter "<< niter << " " << H << endl;
      double ratio,coeff=1;
      if (niter>maxiter-3)
	coeff=100;
      for (int i=n2-2;i>=n1;--i){
	ratio=complex_abs(H[i+1][i])/complex_abs(H[i][i]);
	if (debug_infolevel>2 && i>n2-25)
	  CERR << ratio << " ";
	if (ratio<coeff*eps){ 
	  // do a final iteration if i==n2-2 or n2-3? does not improve much precision
	  // if (i>=n2-3) francis_iterate2(H,n1,n2,P,eps,true,complex_schur,compute_P,v1,v2);
	  // submatrices n1..i and i+1..n2-1
	  if (debug_infolevel>2)
	    CERR << endl << CLOCK() << " Francis split complex " << giacmin((i+1)-n1,n2-(i+1)) << " [" << n1 << " " << i+1 << " " << n2 << "]" << endl;
	  if (only_one && n2-(i+1)<=2)
	    return true;
	  if (!only_one && !in_francis_schur(H,n1,i+1,P,maxiter,eps,compute_P,Haux,only_one)){
	    in_francis_schur(H,i+1,n2,P,maxiter,eps,compute_P,Haux,only_one);
	    return false;
	  }
	  return in_francis_schur(H,i+1,n2,P,maxiter,eps,compute_P,Haux,only_one);
	}
      }
      if (debug_infolevel>2)
	CERR << endl;
      francis_iterate2(H,n1,n2,P,eps,compute_P,Haux,only_one);
    } // end for loop on niter
    return false;
  }

  // Francis algorithm on submatrix rows and columns n1..n2-1
  // Invariant: trn(P)*H*P=orig matrix, complex_schur not used for giac_double coeffs
  bool francis_schur(matrix_complex_double & H,int n1,int n2,matrix_complex_double & P,int maxiter,double eps,bool is_hessenberg,bool compute_P){
    vecteur eigenv;
    int n_orig=int(H.size());//,nitershift0=0;
    if (!is_hessenberg){
      if (debug_infolevel>0)
	CERR << CLOCK() << " start hessenberg complex n=" << H.size() << endl;
#if 0 // FIXME do it for complex
      hessenberg_householder(H,P,compute_P);
#else
      hessenberg_ortho(H,P,0,n_orig,compute_P,0); // insure Hessenberg form (on the whole matrix)
#endif
      if (debug_infolevel>0)
	CERR << CLOCK() << " hessenberg complex done" <<endl;
    }
    matrix_complex_double Haux(n2/2);
    return in_francis_schur(H,n1,n2,P,maxiter,eps,compute_P,Haux,false);
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
