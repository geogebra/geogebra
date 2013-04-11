// -*- mode:C++ ; compile-command: "g++ -I.. -g -c isom.cc " -*- 
#include "giacPCH.h"

/*
 *  Copyright (C) 2001,7 R. De Graeve, Institut Fourier, 38402 St Martin d'Heres
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
#include "isom.h"
#include "gen.h"
#include "vecteur.h"
#include "derive.h"
#include "subst.h"
#include "usual.h"
#include "symbolic.h"
#include "sym2poly.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  vecteur isom(const vecteur & M,GIAC_CONTEXT){
    gen errcode=checkanglemode(contextptr);
    if (is_undef(errcode)) return vecteur(1,errcode);
    int n;
    n=M.size();
    vecteur I;
    // for (int i=0;i<n;i++){
    //vecteur li(n);
    //li[i]=1;
    //I.push_back(li);
    //}
    I=midn(n);
    gen b;
    if (recursive_normal(mtran(M)*M,contextptr)==I){
      b=mdet(M,contextptr);
    } 
    else {
      b=gen(0);
    }
    if (is_zero(b)) { 
      return(makevecteur(b));
    }
    if (M==-I) {
      //on a une symetrie centrale
      return(makevecteur(cst_pi,b));
    }
    if ((n==2) && (b==1)) {
      //M est une rotation plane
      gen t;
      if (is_strictly_positive(M[1][0],contextptr)) {
	t=acos(M[0][0],contextptr);
      }
      else {
	t=-acos(M[0][0],contextptr);
      }
      return(makevecteur(t,b));
    }
    if (n==2) {
      //M est une symetrie par rapport a la droite de vecteur normal v[0]
      vecteur v;
      mker(addvecteur(M,I),v,contextptr);
      return(makevecteur(v[0],b));
    }
    if (M.size()==3) {
      vecteur v;
      //v[0] est l'axe de la rotation si b=1
      //v[0] est la normale au plan de symetrie si b=-1 ou v=R3 tout gen
      if (!mker(subvecteur(M,multvecteur(b,I)),v,contextptr))
	return vecteur(1,gendimerr(contextptr));
      vecteur nn(3);
      nn[0]=v[0][0]; nn[1]=v[0][1]; nn[2]=v[0][2];
      vecteur w;
      if  (is_zero(b+1)) {
	//w est _VECTose de vect qui engendre le plan de symetrie 
	if (!mker(subvecteur(M,I),w,contextptr))
	  return vecteur(1,gendimerr(contextptr));
      }
      else {
	w=v;
      }
      //w contient les vecteurs invariants par M
      if (w.size()==2){
	//on a une symetrie plane
	return(makevecteur(nn,b));
      }
      if (w.size()!=2){
	//on a une rotation(b=1) ou le produit rotation*symetrie (b=-1)
	gen t;
	vecteur u(3);
	//2*cos(theta)+b=trace(M)
	t=rdiv(M[0][0]+M[1][1]+M[2][2]-b,2,contextptr);
	//on cherche le signe de theta (t=cos(theta)), u est orth a nn 
	if ((nn[0] !=0)||(nn[1] !=0)) {
	  u[0]=-nn[1];
	  u[1]=nn[0];
	  u[2]=0;
	}
	else {
	  u[0]=1;
	  u[1]=0;
	  u[2]=0;
	}
	vecteur A;
	A.push_back(nn);
	A.push_back(u);
	//A est la matrice formee par nn u M*u
	A.push_back(multmatvecteur(M,u));
	gen s;
	s=mdet(A,contextptr);
	//s est du signe de sin(theta) 
	if (is_positive(s,contextptr)) { 
	  //si s est >=0 l'angle est acos(t)
	  return(makevecteur(nn,acos(t,contextptr),b));
	}
	else {
	  //si s<0 l'angle est -acos(t)
	  return(makevecteur(nn,-acos(t,contextptr),b));
	}
      }
    }
    return 0;
  }
  static gen symb_isom(const gen & args){
    return symbolic(at_isom,args);
  }
  gen _isom(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!ckmatrix(args))
      return symb_isom(args);
    return isom(*args._VECTptr,contextptr);
  }
  static const char _isom_s []="isom";
  static define_unary_function_eval (__isom,&_isom,_isom_s);
  define_unary_function_ptr5( at_isom ,alias_at_isom,&__isom,0,true);

  static int mkisom_teste(gen& n,int b, int & d1){
    int d;
    //d est la valeur de teste c'est la dim de l'espace du mkisom (d=2 ou >=3 )
    //d>3 pour faire des isometries orthogonales par rapport a un hyperplan
    if (n.type==_VECT){
      // n est un vecteur et d1 est la dimension du vecteur n 
      //si n n'est pas un vecteur il le devient! (cf else ...donc d1>=1)
      vecteur e=*(n._VECTptr);
      d1=e.size();
      if (d1>=3) {
	d=d1;
      }
      else{
	if (d1==1){
	  if (b==1){d=2;}else {d=3;}
	}
	else{
	  if (n[0].type==_VECT){d=3;}else{d=2;}
	}
      }
    }
    else{
      //ds ce cas n n'est pas un vecteur: il le devient! 
      n=makevecteur(n);
      d1=1;
      if (b==1) {d=2;}else {d=3;}  
    }
    return(d);
  }

  vecteur mkisom(const gen & n_orig,int b,GIAC_CONTEXT) {
    checkanglemode(contextptr); 
    //n=les elements caracteristiques de l'isometrie et b=+1(rot) ou b=-1
    int d;
    int d1;
    gen n(n_orig);
    d=mkisom_teste(n,b,d1);
    //d=2 pour les isometries de R2 et d=3 pour celles de R3;
    //d1=dimension de n (n est devenu un vecteur)
    if (d==2) {
      if (b==1){
	gen theta;
	theta=n[0];
	vecteur M2;
	vecteur li(2); 
	li[0]=cos(theta,contextptr); 
	li[1]=-sin(theta,contextptr);
	M2.push_back(li);
	li[0]=sin(theta,contextptr);
	li[1]=cos(theta,contextptr);
	M2.push_back(li);
	return(M2);
      }
      else {
	//if (b==-1){
	gen a=n[0];
	gen b=n[1];
	vecteur M2;
	vecteur li(2); 
	li[0]=rdiv(b*b-a*a,a*a+b*b,contextptr);
	li[1]=-rdiv(gen(2)*a*b,a*a+b*b,contextptr);
	M2.push_back(li);
	li[0]=-rdiv(gen(2)*a*b,a*a+b*b,contextptr);
	li[1]=-rdiv(b*b-a*a,a*a+b*b,contextptr);
	M2.push_back(li);
	return(M2);
	//}
      }
    }  
    if (d>=3) {
      vecteur S;
      vecteur R;
      if (d1==1){
	//on a une symetrie point
	vecteur I;
	I=midn(d);
	return negvecteur(I);
      }
      vecteur nn(d);
      //on a une symetrie plan ou une rotation ou le produit rotation symetrie
      if (d1>=3){
	//on a une symetrie plan et nn=n est normal au plan
	//gen norme2=dotvecteur(n,n);
	nn=*(n._VECTptr);
      }
      else {
	//on a une rot d'axe nn=n[0] ou rot*sym d'axe nn=n[0] et plan orth a nn
	nn=*(n[0])._VECTptr;
      } 
      vecteur ntn(d);
      //ntn est la matrice d*d egale a : nn*transpose(nn)/(nn*nn)
      gen norme2=dotvecteur(nn,nn);
      //nnn est le vecteur nn divise par sa norme au carre
      vecteur nnn=divvecteur(nn, norme2);
      for (int i=0;i<d;i++){
	ntn[i]=multvecteur(nn[i],nnn);
      }
      //ntn[1]=multvecteur(nn[1],nnn);
      //ntn[2]=multvecteur(nn[2],nnn);
      if (b==-1){
	//S=I3-2*nn*transpose(nn)/(nn*nn)
	S=*(recursive_normal(midn(d)-multvecteur(2,ntn),contextptr)._VECTptr);
      }
      if (d1>=3) {return(S);}  
      if (d1==2){
	//on a une rotation si b=1
	vecteur A;
	//A a comme vecteurs colonnes : produitvectoriel(nnn,ei)
	vecteur li(3);
	li[0]=0;
	li[1]=-nnn[2];
	li[2]=nnn[1];
	A.push_back(li);
	li[0]=nnn[2];
	li[1]=0;
	li[2]=-nnn[0];
	A.push_back(li);
	li[0]=-nnn[1];
	li[1]=nnn[0];
	li[2]=0;
	A.push_back(li);
	// A*sqrt(norme2) pour avoir la matrice prod vect(n,ei) avec norme(n)=1;
	A=multvecteur(sqrt(norme2,contextptr),A);
	//cout<<A<<endl;
	gen theta=n[1];
	R=*(recursive_normal(multvecteur(cos(theta,contextptr),midn(3))+multvecteur(1-cos(theta,contextptr),ntn)+multvecteur(sin(theta,contextptr),A),contextptr)._VECTptr); 
      }
      if (b==1) { 
	return(R);
      }
      else {
	return *(recursive_normal(mmult(S,R),0)._VECTptr); 
      }
    }
    return 0;
  }
  static gen symb_mkisom(const gen & args){
    return symbolic(at_mkisom,args);
  }
  gen _mkisom(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_mkisom(args);
    int s=args._VECTptr->size();
    if (s!=2)
      return gendimerr();
    if (args._VECTptr->back().type==_INT_){
      gen n=args._VECTptr->front();
      int b=args._VECTptr->back().val;
      return mkisom(n,b,contextptr);
    }
    return symb_mkisom(args);
  }
  static const char _mkisom_s []="mkisom";
  static define_unary_function_eval (__mkisom,&_mkisom,_mkisom_s);
  define_unary_function_ptr5( at_mkisom ,alias_at_mkisom,&__mkisom,0,true);



#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

