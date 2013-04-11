// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c gauss.cc -Wall" -*- 
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
#include <fstream>
#include "gauss.h"
#include "vecteur.h"
#include "derive.h"
#include "subst.h"
#include "usual.h"
#include "sym2poly.h"
#include "solve.h"
#include "ti89.h"
#include "plot.h"
#include "misc.h"
#include "ifactor.h"
#include "prog.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  vecteur quad(int &b,const gen & q, const vecteur & x,GIAC_CONTEXT){
    //x=vecteur des variables, q=la fonction a tester,n=la dimension de x
    //b=2 si q est quadratique,=0,1 ou 3 si il y des termes d'ordre 0,1 ou 3
    //renvoie (la jacobienne de q)/2
    gen qs;
    gen dq;
    gen dqs;
    gen qdd;
    int n=x.size();
    
    vecteur A;
    //creation d'une matrice carree A d'ordre n
    for (int i=0;i<n;i++){
      vecteur li(n);
      A.push_back(li);
    }
    //A est un vecteur de vecteur=une matrice!
    //on met ds A :(la jacobienne de q)/2 
    for (int i=0;i<n;i++){
      for (int j=i;j<n;j++){
	qdd=derive(derive(q,x[i],contextptr),x[j],contextptr);
	qdd=recursive_normal(qdd,contextptr); 
	//cout<<i<<","<<j<<qdd<<endl;
	if (i==j){
	  (*A[i]._VECTptr)[i]=rdiv(qdd,2,contextptr);
	} 
	else {
	  (*A[i]._VECTptr)[j]=rdiv(qdd,2,contextptr);
	  (*A[j]._VECTptr)[i]=rdiv(qdd,2,contextptr);
	}
      }
    }
    //2*A=jacobienne de q
    //on calcule qs=q en zero
    //cout<<A<<endl;  
    qs=q;
    for (int i=0;i<n;i++){
      qs=subst(qs,x[i],0,false,contextptr);
    }
    //qs=la valeur de q en 0
    if (qs !=0){
      b=0;
      return(A);
    }
    //on regarde si il y des termes lineaires
    for (int j=0;j<n;j++){
      dq=derive(q,x[j],contextptr);
      dqs=dq;
      for (int i=0;i<n;i++){
	dqs=subst(dqs,x[i],0,false,contextptr);
      }
      //dqs=la diff de q en zero
      if (dqs!=0){
	b=1;
	return(A);
      }
    }
    for (int i=0;i<n;i++){
      for (int j=i;j<n;j++){
	for (int k=0;k<n;k++){
	  if (derive(A[i][j],x[k],contextptr)!=0){
	    b=3;
	    return(A);
	  }
	}
      }
    }
    b=2;
    //(*(A[1]._VECTptr))[0]=21;
    return(A);
  } 
  
  vecteur qxa(const gen &q,const vecteur & x,GIAC_CONTEXT){
    //transforme une forme quadratique en une matrice symetrique A
    //(les variables sont dans x)
    int d;
    //d nbre de variables
    d=x.size();
    // int da;
    //il faut verifier que q est quadratique
    vecteur A;
    int b;
    A=quad(b,q,x,contextptr);  
    if (b==2) {
      return(A);
    }
    else {
      return vecteur(1,gensizeerr(gettext("q is not quadratic")));
    }
    return 0;
  }

  static gen symb_q2a(const gen & args){
    return symbolic(at_q2a,args);
  }
  gen _q2a(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_q2a(args);
    int s=args._VECTptr->size();
    if (s!=2)
      return gendimerr(contextptr);
    if (args._VECTptr->back().type==_VECT)
      return qxa(args._VECTptr->front(),*args._VECTptr->back()._VECTptr,contextptr);
    return symb_q2a(args);
  }
  static const char _q2a_s []="q2a";
  static define_unary_function_eval (__q2a,&_q2a,_q2a_s);
  define_unary_function_ptr5( at_q2a ,alias_at_q2a,&__q2a,0,true);

  vecteur gauss(const gen & q, const vecteur & x, vecteur & D, vecteur & U, vecteur & P,GIAC_CONTEXT){
    int n=x.size();
    int b;
    gen u1;
    gen u2;
    gen q1; 
    gen l1;
    gen l2;
    vecteur R(1);
    vecteur A;
 
    vecteur I;
    vecteur L;
    for (int i=0;i<n;i++){
      vecteur li(n);
      A.push_back(li);
    }
    vecteur PR;
    for (int i=0;i<n-1;i++){
      vecteur li(n-1);
      PR.push_back(li);
    }
    vecteur PP;
    for (int i=0;i<n;i++){
      vecteur li(n);
      PP.push_back(li);
    } 
    for (int i=0;i<n;i++){
      vecteur li(n);
      li[i]=gen(1);
      I.push_back(li);
    }
    //n=x.size();
    //if (n==0){
    //R[0]=q; 
    //vecteur vide;
    //D=vide;
    //U=vide;
    //P=vide;
    //return(R);
    //}
  
    //si q n'est pas quadratique b<>2 et on retourne q
    A=quad(b,q,x,contextptr);
    if (b!=2){
      R[0]=q;
      vecteur vide;
      D=vide;
      U=vide;
      return(R);
    }
    //la forme q est quadratique de matrice A
    if (q==0) { 
      //R[0]=q;    
      vecteur vide(n);
      D=vide; 
      U=vide;
    
      P=I;
      return(vide);
    }
    if (n==1){
      gen q0=_factor(q,contextptr);
      R[0]=q0;
      vecteur un(1);
      un[0]=A[0][0];
      D=un;
      U=x;
      P=I;
      return(R);
    }
    int r;
    r=n;
    for (int i=n-1 ;i>=0;i--){
      if (A[i][i]!=0) {
	r=i;
      }
    }
    if (r!=n) {
      //il y a des termes carres
      u1=recursive_normal(rdiv(derive(q,x[r],contextptr),plus_two,contextptr),contextptr);
      q1=recursive_normal(q-rdiv(u1*u1,A[r][r],contextptr),contextptr);     
      vecteur y;
      //y contient les variables qui restent (on enleve x[r])	   
      for (int j=0;j<n;j++){
	if (j!=r){
	  y.push_back(x[j]);
	}
      }
      L=gauss(q1,y,D,U,PR,contextptr);
      //on rajoute 1/a_r_r sur la diagonale D
      R[0]=rdiv(1,A[r][r],contextptr);
      D=mergevecteur(R,D);
      //on rajoute u1 aux vecteurs constitue des formes lineaires
      //q= 1/a_r_r*(u1)^2+... 
      R[0]=u1; 
      U=mergevecteur(R,U);      
      //on _VECTlete la matrice PR de dim n-1 en la matrice PP de dim n
      //1iere ligne les coeff de u1 et rieme colonne doit avoir des 0
      for (int i=0;i<n;i++){
	(*PP[0]._VECTptr)[i]=recursive_normal(derive(u1,x[i],contextptr),contextptr); 
      }
      for (int i=1;i<n;i++){
	for (int j=0;j<r;j++){
	  (*PP[i]._VECTptr)[j]=PR[i-1][j];
	}
	for (int j=r+1;j<n;j++){
	  (*PP[i]._VECTptr)[j]=PR[i-1][j-1];
	}
      }
      P=PP;
      R[0]=rdiv(pow(u1,2),A[r][r],contextptr);
      return(mergevecteur(R,L));
    }
    //il n'y a pas de carres
    int r1;
    int r2;
    r1=0;
    r2=0;
    for (int i=n-2;i>=0;i--){
      for (int j=i+1;j>=1;j--){
	if (A[i][j]!=0) {
	  r1=i;
	  r2=j;
	}
      }
    }
    l1=rdiv(derive(q,x[r1],contextptr),2,contextptr);
    l2=rdiv(derive(q,x[r2],contextptr),2,contextptr);
    u1=recursive_normal(l1+l2,contextptr);
    u2=recursive_normal(l1-l2,contextptr);
    q1=recursive_normal(q-rdiv(plus_two*l1*l2,A[r1][r2],contextptr),contextptr);
    vecteur y;
    for (int j=0;j<n;j++){
      if ((j!=r1) && (j!=r2)) {
	y.push_back(x[j]);
      }
    }
    L=gauss(q1,y,D,U,PR,contextptr);
    //on rajoute 1/a_r1_r2 et -1/a_r1_r2 sur la diagonale D
    R[0]=rdiv(1,plus_two*A[r1][r2],contextptr);
    R.push_back(rdiv(-1,plus_two*A[r1][r2],contextptr));
    D=mergevecteur(R,D); 
    //on rajoute u1 et u2 au vecteur U constitue des formes lineaires
    //q= 1/a_r1_r2*(u1)^2 - 1/a_r1_r2*(u2)^2 + ... 
    R[0]=u1;
    R[1]=u2;
    U=mergevecteur(R,U);
    //on _VECTlete la matrice PR de dim n-2 en la matrice PP de dim n
    //1iere et 2ieme ligne les coeff de u1 et de u2 
    //r1ieme et r2ieme colonne doit avoir des 0
    for (int i=0;i<n;i++){
      (*PP[0]._VECTptr)[i]=recursive_normal(derive(u1,x[i],contextptr),contextptr);
      (*PP[1]._VECTptr)[i]=recursive_normal(derive(u2,x[i],contextptr),contextptr);
    }
    for (int i=2;i<n;i++){
      for (int j=0;j<r1;j++){
	(*PP[i]._VECTptr)[j]=PR[i-2][j];
      }
      for (int j=r1+1;j<r2;j++){
	(*PP[i]._VECTptr)[j]=PR[i-2][j-1];
      }
      for (int j=r2+1;j<n;j++){
	(*PP[i]._VECTptr)[j]=PR[i-2][j-2];
      }
    }	
    P=PP;
    R[0]=rdiv(pow(u1,2),plus_two*A[r1][r2],contextptr);
    R[1]=rdiv(-pow(u2,2),plus_two*A[r1][r2],contextptr);
    return(mergevecteur(R,L)); 
  } 

  vecteur gauss(const gen & q,const vecteur & x,GIAC_CONTEXT){
    vecteur D,U,P;
    return gauss(q,x,D,U,P,contextptr);
  }

  static gen symb_gauss(const gen & args){
    return symbolic(at_gauss,args);
  }
  gen _gauss(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_gauss(args);
    int s=args._VECTptr->size();
    if (s!=2)
      return gendimerr(contextptr);
    if (args._VECTptr->back().type==_VECT)
      return _plus(gauss(args._VECTptr->front(),*(args._VECTptr->back()._VECTptr),contextptr),contextptr);
    return symb_gauss(args);
  }
  static const char _gauss_s []="gauss";
  static define_unary_function_eval (__gauss,&_gauss,_gauss_s);
  define_unary_function_ptr5( at_gauss ,alias_at_gauss,&__gauss,0,true);

  gen axq(const vecteur &A,const vecteur & x,GIAC_CONTEXT){
    //transforme une matrice carree (symetrique) en la forme quadratique q
    //(les variables sont dans x)
    int d;
    //d nbre de variables
    d=x.size();
    int da;
    //il faut verifier que A est carree
    //A n'est pas forcement symetrique  
    da=A.size();
    if (!(is_squarematrix(A)) || (da!=d) ){
      return gensizeerr(gettext("Invalid dimension"));
    } 
    vecteur XL(1);
    XL=makevecteur(x);
    cout<<XL<<endl;
    vecteur XC;
    for (int i=0;i<d;i++) {
      vecteur elem;
      elem=makevecteur(x[i]);
      XC.push_back(elem);
    }
    vecteur QI(d);
    vecteur Q(1);
    QI=mmult(A,XC);
    Q=mmult(XL,QI);
    return(normal(Q[0][0],contextptr));
  }
  
  static gen symb_a2q(const gen & args){
    return symbolic(at_a2q,args);
  }
  gen _a2q(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symb_a2q(args);
    int s=args._VECTptr->size();
    if (s!=2)
      return gendimerr(contextptr);
    if ((args._VECTptr->front().type==_VECT) && (args._VECTptr->back().type==_VECT))
      return axq(*args._VECTptr->front()._VECTptr,*args._VECTptr->back()._VECTptr,contextptr);
    return symb_a2q(args);
  }
  static const char _a2q_s []="a2q";
  static define_unary_function_eval (__a2q,&_a2q,_a2q_s);
  define_unary_function_ptr5( at_a2q ,alias_at_a2q,&__a2q,0,true);

  vecteur qxac(const gen &q,const vecteur & x,GIAC_CONTEXT){
    //transforme une forme quadratique en une matrice symetrique A
    //(les variables sont dans x)
    int d;
    //d nbre de variables
    d=x.size();
    // int da;
    //il faut verifier que q est quadratique
    vecteur A;
    int b;
    A=quad(b,q,x,contextptr);  
    if (b==2) {
      return(A);
    }
    else {
      return vecteur(1,gensizeerr(gettext("q is not quadratic")));
    }
  }

  bool conique_reduite(const gen & equation_conique,const vecteur & nom_des_variables,gen & x0, gen & y0, vecteur & V0, vecteur &V1, gen & propre,gen & equation_reduite, vecteur & param_curves,GIAC_CONTEXT){
    gen q(remove_equal(equation_conique));
    vecteur x(nom_des_variables);
    if (x.size()!=2)
      return false; // setsizeerr(contextptr);
    identificateur iT(" T");
    x.push_back(iT);
    //n est le nombre de variables en geo. projective   
    int n=3; 
    //nom des nouvelles variables
    vecteur A;
    gen qp;
    qp=q;
    for (int i=0;i<n-1;i++){
      qp=subst(qp,x[i],rdiv(x[i],x[2],contextptr),false,contextptr);           
    }
    qp=recursive_normal(x[2]*x[2]*qp,contextptr);
    //qp est l'equation en projective qp est quadratique
    A=qxac(qp,x,contextptr);
    if (is_undef(A))
      return false;
    //q=ax^2+2bxy+cy^2+2dx+2ey+f
    gen a=A[0][0];
    gen b=A[0][1]; 
    gen c=A[1][1];
    gen d=A[0][2];
    gen e=A[1][2];
    gen f=A[2][2];
    //propre=(a*c-b*b)*f+d*(b*e-d*c)-e*(a*e-b*d);
    if ((a*c-b*b)*f+d*(b*e-d*c)-e*(a*e-b*d)!=0) {
      propre=1;
    } else {
      propre=0;
    }
    gen X0;
    gen Y0;
    gen vp0;
    gen vp1;
    V0=vecteur(2);
    V1=vecteur(2);
    gen norme;
    norme=normalize_sqrt(sqrt(a*a+b*b,contextptr),contextptr); 
    if (a*c-b*b==0) {
      gen coeffy2,coeffx,coeffcst;
      //on a une parabole ou 2dr//
      //vecteur propre V0 (vp0 =0) V1 (vp1=a+c)
      if (a!=0){ 
	//on calcule le nouveau repere origine=(x0,y0) base (V0,V1)
	//(X0,Y0) sont les coord du centre ds la base (V0,V1)
	vp0=0;
	vp1=a+c;
	V0[0]=rdiv(b,norme,contextptr);
	V0[1]=rdiv(-a,norme,contextptr);
	V1[0]=-V0[1]; V1[1]=V0[0];
	Y0=-rdiv(d*a+e*b,norme*vp1,contextptr);
	coeffy2=normal(a+c,contextptr);
	//cout<<"Y0="<<Y0<<endl;	
	coeffx=normal(2*(d*b-e*a)/norme,contextptr);
	if (coeffx!=0){
	  // parabole
	  X0=rdiv((d*a+e*b)*(d*a+e*b)-f*(a*a+b*b)*vp1,gen(2)*vp1*(d*b-e*a)*norme,contextptr);
	  equation_reduite=coeffy2*pow(x[1],2)+coeffx*x[0];
	} else { 
	  //si d*b-e*a==0 alors X0=0
	  coeffcst=normal(f-(d*a+e*b)*(d*a+e*b)/((a+c)*(a*a+b*b)),contextptr);
	  equation_reduite=coeffy2*pow(x[1],2)+coeffcst;
	  X0=0;
	}
      }
      else {
	// a==0 => b==0 (puisque a*c-b*b==0) et c!=0
	V0[0]=1; V0[1]=0;
	V1[0]=0; V1[1]=1;
	Y0=-rdiv(e,c,contextptr);
	if (d!=0){
	  X0=rdiv(e*e-f*c,gen(2)*d*c,contextptr);
	  coeffy2=c;
	  coeffx=normal(2*d,contextptr);
	  equation_reduite=c*pow(x[1],2)+coeffx*x[0];
	} else {
	  X0=0;
	  coeffy2=normal(c*c,contextptr);
	  coeffcst=normal(f*c-e*e,contextptr);
	  equation_reduite=coeffy2*pow(x[1],2)+coeffcst;
	}
      } 
      x0=normal(V0[0]*X0+V1[0]*Y0,contextptr);
      y0=normal(V0[1]*X0+V1[1]*Y0,contextptr);
      gen z0=x0+cst_i*y0;
      gen zV0=V0[0]+cst_i*V0[1];
      if (coeffx==0){
	// coeffy2*Y^2+coeffcst=0
	// 2 lines or empty
	gen coeff=-coeffcst/coeffy2;
	gen coeffs=sign(coeff,contextptr);
	if (is_minus_one(coeffs)){
#ifndef GIAC_HAS_STO_38
	  *logptr(contextptr) << gettext("Empty parabola") << endl;
#endif
	  return true;
	}
#ifndef GIAC_HAS_STO_38
	*logptr(contextptr) << gettext("2 parallel lines") << endl;
#endif
	gen Y0=normalize_sqrt(sqrt(coeff,contextptr),contextptr);
	// Y=Y0 : points (0,Y0), (1,Y0)
	gen zY0=cst_i*zV0*Y0;
	param_curves.push_back(gen(makevecteur(z0+zY0,z0+zV0+zY0),_LINE__VECT));
	param_curves.push_back(gen(makevecteur(z0-zY0,z0+zV0-zY0),_LINE__VECT));
      }
      else {
	// parabola coeffy2*Y^2+coeffx*X=0 -> X=-coeffy2/coeffx*Y^2
	// X+i*Y=-coeffy2/coeffx*Y^2+i*Y
	gen coeff=-coeffy2/coeffx;
#ifdef GIAC_HAS_STO_38
	gen t(vx_var);
#else
	gen t(t__IDNT_e);
#endif
	ck_parameter_t(contextptr);
	gen Z=coeff*t*t+cst_i*t;
	Z=z0+zV0*Z;
	param_curves.push_back(makevecteur(Z,t,-4,4,0.1));
      }
    } 
    else {
      // a*c-b*b!=0 => on a une conique a centre ou 2 dr concourantes
      // ellipse/hyperbole
      if (b==0){
	vp0=a;
	vp1=c;
	V0[0]=1; V0[1]=0;
	V1[0]=0; V1[1]=1;
      } else {
	//si b!=0
	gen delta;
	delta=(a-c)*(a-c)+4*b*b;
	delta=normalize_sqrt(sqrt(delta,contextptr),contextptr);
	vp0=ratnormal((a+c+delta)/2);
	vp1=ratnormal((a+c-delta)/2);
	gen normv1(normalize_sqrt(sqrt(b*b+(vp0-a)*(vp0-a),contextptr),contextptr));
	V0[0]=normal(b/normv1,contextptr); 
	V0[1]=normal((vp0-a)/normv1,contextptr);
	V1[0]=-V0[1]; V1[1]=V0[0];
      }
      //coord du centre
      x0=(-d*c+b*e)/(a*c-b*b);
      y0=(-a*e+d*b)/(a*c-b*b);
      gen z0=x0+cst_i*y0;
      gen zV0=V0[0]+cst_i*V0[1];
      gen coeffcst=normal(d*x0+e*y0+f,contextptr);
      equation_reduite=vp0*pow(x[0],2)+vp1*pow(x[1],2)+ coeffcst;  
      // parametric equations
      gen svp0(exact(sign(vp0,contextptr),contextptr)),
	svp1(exact(sign(vp1,contextptr),contextptr)),
	scoeffcst(exact(sign(coeffcst,contextptr),contextptr));
      if (svp0.type==_INT_ && svp1.type==_INT_ && scoeffcst.type==_INT_){
#ifdef GIAC_HAS_STO_38
	gen t(vx_var);
#else
	gen t(t__IDNT_e);
#endif
	ck_parameter_t(contextptr);
	int sprodvp = svp0.val * svp1.val;
	int sprodcoeff = svp0.val*scoeffcst.val;
	if (sprodvp>0){ // ellipse
	  if (is_zero(coeffcst)){
#ifndef GIAC_HAS_STO_38
	    *logptr(contextptr) << gettext("Ellipsis reduced to (") << x0 << "," << y0 << ")" << endl;
#endif
	    param_curves.push_back(z0);
	    return true;
	  }
	  if (sprodcoeff>0){
#ifndef GIAC_HAS_STO_38
	    *logptr(contextptr) << gettext("Empty ellipsis") << endl;
#endif
	    return true;
	  }
#ifndef GIAC_HAS_STO_38
	  *logptr(contextptr) << gettext("Ellipsis of center (") << x0 << "," << y0 << ")" << endl;
#endif
	  vp0=normalize_sqrt(sqrt(-coeffcst/vp0,contextptr),contextptr);
	  vp1=normalize_sqrt(sqrt(-coeffcst/vp1,contextptr),contextptr);
	  // (x[0]/vp0)^2 + (x[1]/vp1)^2 = 1
	  // => x[0]+i*x[1]=vp0*cos(t)+i*vp1*sin(t)
	  // => x+i*y = x0+i*y0 + V0*(x[0]+i*y[0])
	  gen tmp=evalf(vp0,1,contextptr)*symb_cos(t)+cst_i*evalf(vp1,1,contextptr)*symb_sin(t);
	  tmp=evalf(z0,1,contextptr)+evalf(zV0,1,contextptr)*tmp;
	  bool rad=angle_radian(contextptr);
	  param_curves.push_back(makevecteur(tmp,t,0,rad?cst_two_pi:360,rad?cst_two_pi/60:6));
	} else {
	  if (is_zero(coeffcst)){
	    // 2 secant lines at (x0,y0)
#ifndef GIAC_HAS_STO_38
	    *logptr(contextptr) << gettext("2 secant lines at (") << x0 << "," << y0 << ")" << endl;
#endif
	    // vp0*X^2+vp1*Y^2=0 => Y=+/-sqrt(-vp0/vp1)*X
	    gen directeur=normalize_sqrt(sqrt(-vp0/vp1,contextptr),contextptr);
	    param_curves.push_back(gen(makevecteur(z0,z0+zV0*(1+cst_i*directeur)),_LINE__VECT));
	    param_curves.push_back(gen(makevecteur(z0,z0+zV0*(1-cst_i*directeur)),_LINE__VECT));
	    return true;
	  }
	  // hyperbole
#ifndef GIAC_HAS_STO_38
	  *logptr(contextptr) << gettext("Hyperbola of center (") << x0 << "," << y0 << ")" << endl;
#endif
	  if (sprodcoeff<0)
	    vp0=-vp0;
	  else
	    vp1=-vp1;
	  vp0=normalize_sqrt(sqrt(coeffcst/vp0,contextptr),contextptr);
	  vp1=normalize_sqrt(sqrt(coeffcst/vp1,contextptr),contextptr);
	  gen tmp=evalf(vp0,1,contextptr)*symbolic(sprodcoeff<0?at_cosh:at_sinh,t)+cst_i*evalf(vp1,1,contextptr)*symbolic(sprodcoeff<0?at_sinh:at_cosh,t);
	  tmp=evalf(z0,1,contextptr)+evalf(zV0,1,contextptr)*tmp;
	  param_curves.push_back(makevecteur(tmp,t,-3,3,0.1));
	  tmp=(sprodcoeff<0?-1:1)*evalf(vp0,1,contextptr)*symbolic(sprodcoeff<0?at_cosh:at_sinh,t)+(sprodcoeff<0?1:-1)*cst_i*evalf(vp1,1,contextptr)*symbolic(sprodcoeff<0?at_sinh:at_cosh,t);
	  tmp=evalf(z0,1,contextptr)+evalf(zV0,1,contextptr)*tmp;
	  param_curves.push_back(makevecteur(tmp,t,-3,3,0.1));
	}
      }
    }
    return true;
  }  

#ifdef RTOS_THREADX
  bool quadrique_reduite(const gen & q,const vecteur & vxyz,gen & x,gen & y,gen & z,vecteur & u,vecteur & v,vecteur & w,vecteur & propre,gen & equation_reduite,vecteur & param_surface,vecteur & centre,bool numeric,GIAC_CONTEXT){
    return false;
  }
#else
  bool quadrique_reduite(const gen & q,const vecteur & vxyz,gen & x,gen & y,gen & z,vecteur & u,vecteur & v,vecteur & w,vecteur & propre,gen & equation_reduite,vecteur & param_surface,vecteur & centre,bool numeric,GIAC_CONTEXT){
    if (vxyz.size()!=3)
      return false; // setdimerr(contextptr);
    x=vxyz[0]; y=vxyz[1]; z=vxyz[2];
    identificateur idt("t");
    gen t(idt),upar("u",contextptr),vpar("v",contextptr);
    ck_parameter_u(contextptr);
    ck_parameter_v(contextptr);
    gen Q=normal(t*t*(subst(q,vxyz,makevecteur(x/t,y/t,z/t),false,contextptr)),contextptr); // homogeneize
    matrice A=qxa(Q,makevecteur(x,y,z,t),contextptr);
    if (is_undef(A))
      return false;
    if (numeric)
      A=*evalf_double(A,1,contextptr)._VECTptr;
    // unsigned r=_rank(A).val;
    matrice B=matrice_extract(A,0,0,3,3);
    if (is_undef(B)) return false;
    matrice C=makevecteur(A[0][3],A[1][3],A[2][3]);
    matrice P;
    egv(B,P,propre,contextptr,false,false,false);
    gen s1=propre[0],s2=propre[1],s3=propre[2];
    if (is_zero(s1)) s1=0;
    if (is_zero(s2)) s2=0;
    if (is_zero(s3)) s3=0;
    P=mtran(P);
    P[0]=normal(_normalize(P[0],contextptr),contextptr);
    P[1]=normal(_normalize(P[1],contextptr),contextptr);
    P[2]=normal(_normalize(P[2],contextptr),contextptr);
    u=*P[0]._VECTptr;
    v=*P[1]._VECTptr;
    w=*P[2]._VECTptr;
    if ( s1==0 && s2!=0 ){
      vecteur b(u);
      u=v; v=w; w=b;
      gen a(s1);
      s1=s2; s2=s3; s3=a;
    }
    if ( s2==0 && s3!=0 ){
      vecteur b=w;
      w=v; v=u; u=b;
      gen a=s3;
      s3=s2; s2=s1; s1=a;
    }
    gen s1g=evalf_double(sign(s1,contextptr),1,contextptr);
    gen s2g=evalf_double(sign(s2,contextptr),1,contextptr);
    gen s3g=evalf_double(sign(s3,contextptr),1,contextptr);
    if (s1g.type!=_DOUBLE_ || s2g.type!=_DOUBLE_ || s3g.type!=_DOUBLE_){
      *logptr(contextptr) << (gettext("Can't check sign ")+s1g.print(contextptr)+gettext(" or ")+s2g.print(contextptr)+gettext(" or ")+s3g.print(contextptr)) << endl;
      return false; 
    }
    int s1s=int(s1g._DOUBLE_val), s2s=int(s2g._DOUBLE_val), s3s=int(s3g._DOUBLE_val);
    if (s3!=0){ // hence s1!=0 && s2!=0
      if (s1s*s2s<0){
	if (s1s*s3s>0){ // exchange s2 and s3
	  swap(v,w);
	  swap(s2,s3);
	  swap(s2s,s3s);
	}
	else { // s1s and s2s not same sign, s1s and s3s not same sign
	  // therefore s2s and s3s have the same sign
	  vecteur b(u);
	  u=v; v=w; w=b;
	  gen a(s1); 
	  s1=s2; s2=s3; s3=a;
	  int as(s1s);
	  s1s=s2s; s2s=s3s; s3s=as;
	}
      }
      // now s1 and s2 have the same sign
    }
    P=mtran(makevecteur(u,v,w));
    vecteur CP=multvecteurmat(C,P);
    /* gen CPxyz=dotvecteur(CP,vxyz);
     gen c=normal(derive(CPxyz,vxyz),contextptr);
    if (c.type!=_VECT || c._VECTptr->size()!=3)
      return false;
    */
    gen c=normal(CP,contextptr),d;
    gen c1(c._VECTptr->front()),c2((*c._VECTptr)[1]),c3(c._VECTptr->back());
    gen ustep=_USTEP;
    ustep.subtype=_INT_PLOT;
    gen vstep=_VSTEP;
    vstep.subtype=_INT_PLOT;
    if (!is_zero(s1)){
      if (!is_zero(s2)){
	if (!is_zero(s3)){
	  gen tmp=normal(-c1/s1*u-c2/s2*v-c3/s3*w,contextptr);
	  if (tmp.type!=_VECT || tmp._VECTptr->size()!=3)
	    return false;
	  centre=*tmp._VECTptr;
	  d=normal(subst(q,vxyz,centre,false,contextptr),contextptr);
	  equation_reduite=s1*pow(x,2)+s2*pow(y,2)+s3*pow(z,2)+d;
	  gen dg=evalf_double(sign(d,contextptr),1,contextptr);
	  if (dg.type!=_DOUBLE_)
	    return false; // cksignerr(d);
	  int ds=int(dg._DOUBLE_val);
	  if (ds==0){ 
	    // if s3s*s1s>0 solution=1 point, else cone
	    if (s3s*s1s>0)
	      param_surface.push_back(centre);
	    else { // s1*x^2+s2*y^2=-s3*z^2 ->  x^2/a^2+y^2/b^2=z^2
	      gen a(sqrt(-s3/s1,contextptr)),b(sqrt(-s3/s2,contextptr));
	      gen eq=makevecteur(a*upar*symb_cos(vpar),b*upar*symb_sin(vpar),upar);
	      *logptr(contextptr) << gettext("Cone of center ") << centre << endl;
	      eq=centre+multmatvecteur(P,*eq._VECTptr);
	      gen ueq=symbolic(at_equal,makesequence(upar,symb_interval(-5,5)));
	      gen veq=symbolic(at_equal,makesequence(vpar,symb_interval(0,cst_two_pi)));
	      ustep=symb_equal(ustep,1./2);
	      vstep=symb_equal(vstep,cst_two_pi/20);
	      param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
	    }
	  } // end if ds==0
	  else { 
	    if (s1s*s3s>0){
	      if (s1s*ds>0)
		*logptr(contextptr) << gettext("Empty ellipsoid") << endl;
	      else {
		gen a=sqrt(-d/s1,contextptr),b=sqrt(-d/s2,contextptr),c=sqrt(-d/s3,contextptr);
		// x^2/a^2+y^2/b^2+z^2/c^2=1
		gen eq=makevecteur(a*symb_sin(upar)*symb_cos(vpar),b*symb_sin(upar)*symb_sin(vpar),c*symb_cos(upar));
		*logptr(contextptr) << gettext("Ellipsoid of center ") << centre << endl;
		eq=centre+multmatvecteur(P,*eq._VECTptr);
		gen ueq=symbolic(at_equal,makesequence(upar,symb_interval(0,cst_pi)));
		gen veq=symbolic(at_equal,makesequence(vpar,symb_interval(0,cst_two_pi)));
		ustep=symb_equal(ustep,cst_pi/20);
		vstep=symb_equal(vstep,cst_two_pi/20);
		param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
	      }
	    } // end s1 and s3 of same sign
	    else { // s1 and s2 have same sign, opposite to s3
	      if (s1s*ds>0){
		gen a=sqrt(d/s1,contextptr),b=sqrt(d/s2,contextptr),c=sqrt(-d/s3,contextptr);
		// x^2/a^2+y^2/b^2+1=z^2/c^2, hyperboloide, 2 nappes
		gen eq=makevecteur(a*symb_sinh(upar)*symb_cos(vpar),b*symb_sinh(upar)*symb_sin(vpar),c*symb_cosh(upar));
		eq=centre+multmatvecteur(P,*eq._VECTptr);
		*logptr(contextptr) << gettext("2-fold hyperboloid of center ") << centre << endl;
		gen ueq=symbolic(at_equal,makesequence(upar,symb_interval(0,3)));
		gen veq=symbolic(at_equal,makesequence(vpar,symb_interval(0,cst_two_pi)));
		ustep=symb_equal(ustep,3./20);
		vstep=symb_equal(vstep,cst_two_pi/20);
		param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
		eq=makevecteur(a*symb_sinh(upar)*symb_cos(vpar),b*symb_sinh(upar)*symb_sin(vpar),-c*symb_cosh(upar));
		eq=centre+multmatvecteur(P,*eq._VECTptr);
		param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
	      }
	      else { // s1, s2 opposite sign to s3,d
		gen a=sqrt(-d/s1,contextptr),b=sqrt(-d/s2,contextptr),c=sqrt(d/s3,contextptr);
		// x^2/a^2+y^2/b^2=z^2/c^2+1, hyperboloide, 2 nappes
		gen eq=makevecteur(a*symb_cosh(upar)*symb_cos(vpar),b*symb_cosh(upar)*symb_sin(vpar),c*symb_sinh(upar));
		*logptr(contextptr) << gettext("2-fold hyperboloid of center ") << centre << endl;
		eq=centre+multmatvecteur(P,*eq._VECTptr);
		gen ueq=symbolic(at_equal,makesequence(upar,symb_interval(-3,3)));
		gen veq=symbolic(at_equal,makesequence(vpar,symb_interval(0,cst_two_pi)));
		ustep=symb_equal(ustep,3./20);
		vstep=symb_equal(vstep,cst_two_pi/20);
		param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
	      }
	    }
	  }
	  return true;
	} // end if (s3!=0)
	// s3==0, s1!=0, s2!=0
	if (is_zero(c3)){
	  gen tmp=normal(multmatvecteur(P,makevecteur(-c1/s1,-c2/s2,0)),contextptr);
	  if (tmp.type!=_VECT || tmp._VECTptr->size()!=3) return false;
	  centre=*tmp._VECTptr;
	  gen d(normal(subst(q,vxyz,centre,false,contextptr),contextptr));
	  gen dg=evalf_double(sign(d,contextptr),1,contextptr);
	  if (dg.type!=_DOUBLE_)
	    return false; // cksignerr(d);
	  int ds=int(dg._DOUBLE_val);
	  equation_reduite=s1*pow(x,2)+s2*pow(y,2)+d;
	  if (s1s*s2s>0){ 
	    *logptr(contextptr) << gettext("Elliptic cylinder around ") << centre << endl;

	    if (is_zero(d)) // line (cylinder of radius 0)
	      param_surface.push_back(makevecteur(centre,centre+w));
	    else {
	      // elliptic cylinder (maybe empty)
	      if (s1s*ds<0){ // s1*x^2+s2*y^2=-d
		gen a(sqrt(-d/s1,contextptr)),b(sqrt(-d/s2,contextptr));
		gen eq=makevecteur(a*symb_cos(vpar),b*symb_sin(vpar),upar);
		eq=centre+multmatvecteur(P,*eq._VECTptr);
		gen ueq=symbolic(at_equal,makesequence(upar,symb_interval(-5,5)));
		gen veq=symbolic(at_equal,makesequence(vpar,symb_interval(0,cst_two_pi)));
		ustep=symb_equal(ustep,1./2);
		vstep=symb_equal(vstep,cst_two_pi/20);
		param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
	      }
	    }
	    return true;
	  } // end s1 and s2 of same sign
	  else { // s1 and s2 have opposite signs, s1*x^2+s2*y^2+d=0
	    if (is_zero(d)){ // 2 plans
	      *logptr(contextptr) << gettext("2 plans intersecting at ") << centre << endl;
	      gen n=u+sqrt(-s2/s1,contextptr)*v;
	      param_surface.push_back(symbolic(at_hyperplan,gen(makevecteur(n,centre),_SEQ__VECT)));
	      n=u-sqrt(-s2/s1,contextptr)*v;
	      param_surface.push_back(symbolic(at_hyperplan,gen(makevecteur(n,centre),_SEQ__VECT)));
	      return true;
	    }
	    else { // hyperbolic cylinder
	      *logptr(contextptr) << gettext("Hyperbolic cylinder around ") << centre << endl;
	      gen ueq=symbolic(at_equal,makesequence(upar,symb_interval(-5,5)));
	      gen veq=symbolic(at_equal,makesequence(vpar,symb_interval(-3,3)));
	      ustep=symb_equal(ustep,1./2);
	      vstep=symb_equal(vstep,0.3);
	      if (s1s*ds<0){ // x^2/(-d/s1) - y^2/(d/s2)=1
		gen a(sqrt(-d/s1,contextptr)),b(sqrt(d/s2,contextptr));
		gen eq=makevecteur(a*symb_cosh(vpar),b*symb_sinh(vpar),upar);
		eq=centre+multmatvecteur(P,*eq._VECTptr);
		param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
		eq=makevecteur(-a*symb_cosh(vpar),b*symb_sinh(vpar),upar);
		eq=centre+multmatvecteur(P,*eq._VECTptr);
		param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
		return true;
	      }
	      else { // x^2/(d/s1) - y^2/(-d/s2)=-1
		gen a(sqrt(d/s1,contextptr)),b(sqrt(-d/s2,contextptr));
		gen eq=makevecteur(a*symb_sinh(vpar),b*symb_cosh(vpar),upar);
		eq=centre+multmatvecteur(P,*eq._VECTptr);
		param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
		eq=makevecteur(a*symb_sinh(vpar),-b*symb_cosh(vpar),upar);
		eq=centre+multmatvecteur(P,*eq._VECTptr);
		param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
		return true;
	      }
	    }
	  }
	} // end c3==0
	else {
	  // s3==0, s1!=0, s2!=0, c3!=0
	  gen tmp=normal(-c1/s1*u-c2/s2*v,contextptr);
	  if (tmp.type!=_VECT) return false;
	  gen dred=subst(q,vxyz,*tmp._VECTptr,false,contextptr);
	  tmp=normal(tmp-dred/(2*c3)*w,contextptr);
	  if (tmp.type!=_VECT || tmp._VECTptr->size()!=3) return false;
	  centre=*tmp._VECTptr;
	  equation_reduite=s1*pow(x,2)+s2*pow(y,2)+2*c3*z;
	  // parametrization of s1*x^2+s2*y^2+2*c3*z=0
	  if (s1s*s2s>0){
	    *logptr(contextptr) << gettext("Elliptic paraboloid of center ") << centre << endl;
	    // if (s1s*s2s>0) x^2+y^2/(s1/s2)=-2*c3*z/s1
	    // x=u*cos(t), y=u*sqrt(s1/s2)*sin(t), z=-u^2*s1/2/c3
	    gen ueq=symbolic(at_equal,makesequence(upar,symb_interval(0,5)));
	    ustep=symb_equal(ustep,1./2);
	    gen veq=symbolic(at_equal,makesequence(vpar,symb_interval(0,cst_two_pi)));
	    vstep=symb_equal(vstep,cst_two_pi/20);
	    gen a(sqrt(s1/s2,contextptr)),b(-s1/2/c3);
	    gen eq=makevecteur(upar*symb_cos(vpar),a*upar*symb_sin(vpar),b*pow(upar,2));
	    eq=centre+multmatvecteur(P,*eq._VECTptr);
	    param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
	    return true;
	  }
	  else {
	    // if (s1s*s2s<0) x^2-y^2/(-s1/s2)=-2*c3*z/s1
	    *logptr(contextptr) << gettext("Hyperbolic paraboloid of center ") << centre << endl;
	    gen ueq=symbolic(at_equal,makesequence(upar,symb_interval(-3,3)));
	    ustep=symb_equal(ustep,0.3);
	    gen veq=symbolic(at_equal,makesequence(vpar,symb_interval(-3,3)));
	    vstep=symb_equal(vstep,0.3);
	    gen a(-s1/s2),b(s1/2/c3);
	    gen eq=makevecteur(upar,sqrt(a,contextptr)*vpar,b*(pow(vpar,2)-pow(upar,2)));
	    eq=centre+multmatvecteur(P,*eq._VECTptr);
	    param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
	    return true;
	  }
	}
      } // end if (!is_zero(s2))
      // here s3==0, s2==0, s1!=0
      gen tmp=normal(-c1/s1*u,contextptr);
      // gen tmp=normal(multmatvecteur(P,makevecteur(-c1/s1,0,0)),contextptr);
      if (tmp.type!=_VECT || tmp._VECTptr->size()!=3) return false;
      // gen tmp=normal(multmatvecteur(P,makevecteur(-c1/s1,0,0)),contextptr);
      if (!is_zero(c2) || !is_zero(c3)){
	gen c4=normalize_sqrt(sqrt(c2*c2+c3*c3,contextptr),contextptr);
	gen v1=normal(multmatvecteur(P,makevecteur(0,c3/c4,-c2/c4)),contextptr);
	gen w1=normal(multmatvecteur(P,makevecteur(0,c2/c4,c3/c4)),contextptr);
	gen dred=subst(q,vxyz,*tmp._VECTptr,false,contextptr);
	P=mtran(makevecteur(u,v1,w1));
	v=*v1._VECTptr; w=*w1._VECTptr;
	tmp=tmp+normal(-dred/(2*c4)*w1,contextptr);
	// tmp=tmp+normal(multmatvecteur(P,makevecteur(0,0,-d/(2*c4))),contextptr);
	if (tmp.type!=_VECT || tmp._VECTptr->size()!=3) return false;
	centre=*tmp._VECTptr;
	// ??? dred=normal(subst(q,vxyz,centre),contextptr);
	equation_reduite=s1*pow(x,2)+2*c4*z; // ???+dred;
	gen ueq=symbolic(at_equal,makesequence(upar,symb_interval(-5,5)));
	gen veq=symbolic(at_equal,makesequence(vpar,symb_interval(-5,5)));
	ustep=symb_equal(ustep,1./2);
	vstep=symb_equal(vstep,1./2);
	*logptr(contextptr) << gettext("Paraboloid cylinder") << endl;
	gen eq=makevecteur(upar,vpar,-s1*pow(upar,2)/2/c4);
	eq=centre+multmatvecteur(P,*eq._VECTptr);
	param_surface.push_back(makevecteur(eq,ueq,veq,ustep,vstep));
	return true;
      }
      else { // c2==0 and c3==0
	*logptr(contextptr) << gettext("2 parallel plans") << endl;
	centre=*tmp._VECTptr;
	gen dred=normal(subst(q,vxyz,centre,false,contextptr),contextptr);
	equation_reduite=s1*pow(x,2)+dred;
	if (is_zero(dred)){ // a single plan multiplicity 2
	  param_surface.push_back(symbolic(at_hyperplan,gen(makevecteur(u,centre),_SEQ__VECT)));
	}
	gen dg=evalf_double(sign(dred,contextptr),1,contextptr);
	if (dg.type!=_DOUBLE_)
	  return false; // cksignerr(d);
	int ds=int(dg._DOUBLE_val);
	if (s1s*ds<0){ // 2 plans x = +/- sqrt(-dred/s1)
	  gen a(sqrt(-dred/s1,contextptr));
	  param_surface.push_back(symbolic(at_hyperplan,gen(makevecteur(u,centre+a*u),_SEQ__VECT)));
	  param_surface.push_back(symbolic(at_hyperplan,gen(makevecteur(u,centre-a*u),_SEQ__VECT)));	  
	}
	return true;
      }
    } // end if !is_zero(s1)
    return false;
  }
#endif // RTOS_THREADX

  gen conique_quadrique_reduite(const gen & args,GIAC_CONTEXT,bool conique){
    vecteur v(gen2vecteur(args));
    int s=v.size();
    if (!s || s>4)
      return gendimerr(contextptr);
    if (s==4)
      v=makevecteur(v[0],makevecteur(v[1],v[2],v[3]));
    if (s==3)
      v=makevecteur(v[0],makevecteur(v[1],v[2]));
    if (s==1){
      v.push_back(conique?makevecteur(x__IDNT_e,y__IDNT_e):makevecteur(x__IDNT_e,y__IDNT_e,z__IDNT_e));
    }
    if (v[0].type==_SYMB && v[1].type==_VECT){
      gen x0,y0,z0,eq_reduite,propre;
      vecteur V0,V1,V2,param_curves,centre,proprev;
      if (v[1]._VECTptr->size()==3){
	quadrique_reduite(v[0],*v[1]._VECTptr,x0,y0,z0,V0,V1,V2,proprev,eq_reduite,param_curves,centre,false,contextptr);
	return makevecteur(centre,mtran(makevecteur(V0,V1,V2)),proprev,eq_reduite,param_curves);
      }
      else {
	if (!conique_reduite(v[0],*v[1]._VECTptr,x0,y0,V0,V1,propre,eq_reduite,param_curves,contextptr))
	  return gensizeerr(contextptr);
	return makevecteur(makevecteur(x0,y0),mtran(makevecteur(V0,V1)),propre,eq_reduite,param_curves);
      }
    }
    return gentypeerr(contextptr);
  }
  gen _conique_reduite(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return conique_quadrique_reduite(args,contextptr,true);
  }
  static const char _conique_reduite_s []="reduced_conic";
  static define_unary_function_eval (__conique_reduite,&_conique_reduite,_conique_reduite_s);
  define_unary_function_ptr5( at_conique_reduite ,alias_at_conique_reduite,&__conique_reduite,0,true);

  gen _quadrique_reduite(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return conique_quadrique_reduite(args,contextptr,false);
  }
  static const char _quadrique_reduite_s []="reduced_quadric";
  static define_unary_function_eval (__quadrique_reduite,&_quadrique_reduite,_quadrique_reduite_s);
  define_unary_function_ptr5( at_quadrique_reduite ,alias_at_quadrique_reduite,&__quadrique_reduite,0,true);


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

