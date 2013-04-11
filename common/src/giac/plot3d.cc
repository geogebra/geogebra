/* -*- mode:C++ ; compile-command: "g++-3.4 -I. -I.. -I../include -g -c plot3d.cc -DIN_GIAC -DHAVE_CONFIG_H " -*- */
// NB: Using gnuplot optimally requires patching and recompiling gnuplot
// If you use the -DGNUPLOT_IO compile flag, you
// MUST compile gnuplot with interactive mode enabled, file src/plot.c
// line 448
/*
diff plot.c plot.c~
448c448
<     interactive = TRUE; // isatty(fileno(stdin));
---
>     interactive = isatty(fileno(stdin));
*/

/*
 *  Copyright (C) 2000/6 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
 *  implicitplot3d code adapted from 
 *  http://astronomy.swin.edu.au/~pbourke/modelling/polygonise 
 *  by Paul Bourke and  Cory Gene Bloyd
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

#include "giacPCH.h"

using namespace std;
#include <fstream>
#include <iomanip>
#include "vector.h"
#include <algorithm>
#include <cmath>

// C headers
#include <stdio.h>

// Giac headers
#include "gen.h"
#include "usual.h"
#include "plot.h"
#include "plot3d.h"
#include "prog.h"
#include "rpn.h"
#include "identificateur.h"
#include "subst.h"
#include "symbolic.h"
#include "derive.h"
#include "solve.h"
#include "intg.h"
#include "path.h"
#include "sym2poly.h"
#include "input_parser.h"
#include "input_lexer.h"
#include "ti89.h"
#include "isom.h"
#include "ifactor.h"
#include "gauss.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  gen do_point3d(const gen & g){
    gen tmp(g);
    if (tmp.type==_VECT)
      tmp.subtype=_POINT__VECT;
    return tmp;
  }

  vecteur rand_3d(){
    int i=rand(),j=rand(),k=rand();
    i=i/(RAND_MAX/10)-5;
    j=j/(RAND_MAX/10)-5;
    k=k/(RAND_MAX/10)-5;
    return makevecteur(i,j,k);
  }

  vecteur hyperplan_normal(const gen & g){
    vecteur n,P;
    if (!hyperplan_normal_point(g,n,P))
      return vecteur(3,gensizeerr(gettext("hyperplan_normal")));
    return n;
  }
  bool hyperplan_normal_point(const gen & g,vecteur & n,vecteur & P){
    gen h=remove_at_pnt(g);
    if (h.is_symb_of_sommet(at_hyperplan))
      h=h._SYMBptr->feuille;
    if (h.type!=_VECT || h._VECTptr->size()!=2 || h._VECTptr->front().type!=_VECT || h._VECTptr->back().type!=_VECT)
      return false; // setsizeerr(contextptr);
    n=*h._VECTptr->front()._VECTptr;
    P=*h._VECTptr->back()._VECTptr;
    return true;
  }

  gen _plan(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_INT_ || (args.type==_VECT && args._VECTptr->empty()) )
      return mkrand2d3d(3,3,_plan,contextptr);
    if (args.type==_SYMB)
      return droite_by_equation(vecteur(1,args),true,contextptr);
    if (args.type!=_VECT || args._VECTptr->size()<2)
      return gensizeerr(contextptr);
    vecteur v = *args._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    v=vecteur(v.begin(),v.begin()+s);
    if (!v.empty() && (v.front().type==_SYMB) && (v.front()._SYMBptr->sommet==at_equal))
      return droite_by_equation(*args._VECTptr,true,contextptr);
    if (s)     
      v[0]=remove_at_pnt(v[0]);
    if (s>1)
      v[1]=remove_at_pnt(v[1]);
    if (s==2){
      if (v[0].type==_VECT && v[0]._VECTptr->size()==2 && v[1].type==_VECT && v[1]._VECTptr->size()==2){
	// plane in space defined by 2 lines: must be parallel or secant
	gen A=v[0]._VECTptr->front(),B=v[0]._VECTptr->back(),
	  C=v[1]._VECTptr->front(),D=v[1]._VECTptr->back();
	if (!check3dpoint(A) || !check3dpoint(B) || !check3dpoint(C) || !check3dpoint(D))
	  return gensizeerr(contextptr);
	vecteur v1(subvecteur(*B._VECTptr,*A._VECTptr));
	vecteur v2(subvecteur(*D._VECTptr,*C._VECTptr));
	gen M,N,coeff;
	vecteur n;
	if (est_parallele_vecteur(v1,v2,coeff,contextptr))
	  return _plan(gen(makevecteur(A,B,C),_SEQ__VECT),contextptr);
	if (perpendiculaire_commune(v[0],v[1],M,N,n,contextptr) && is_zero(M-N))
	  return pnt_attrib(symbolic(at_hyperplan,gen(makevecteur(n,M),_SEQ__VECT)),attributs,contextptr);
	return gensizeerr(contextptr);
      }
      if (v[1].type==_VECT && v[1]._VECTptr->size()>=2){
	s++;
	v.push_back(v[1]._VECTptr->back());
	v[1]=v[1]._VECTptr->front();
      }
      else
	return pnt_attrib(symbolic(at_hyperplan,gen(v,args.subtype)),attributs,contextptr);
    }
    if (s==3){ 
      v[2]=remove_at_pnt(v[2]);
      if (v[0].type==_VECT && v[0]._VECTptr->size()==3 && v[1].type==_VECT && v[1]._VECTptr->size()==3 && v[2].type==_VECT && v[2]._VECTptr->size()==3){
      // given by 3 points, compute normal vector
	gen v1=v[1]-v[0];
	gen v2=v[2]-v[0];
	gen n=cross(*v1._VECTptr,*v2._VECTptr,contextptr);
	return  pnt_attrib(symbolic(at_hyperplan,gen(makevecteur(n,v[0]),_SEQ__VECT)),attributs,contextptr);
      }
    }
    return gensizeerr(contextptr);
  }
  static const char _plan_s []="plane";
  static define_unary_function_eval (__plan,&giac::_plan,_plan_s);
  define_unary_function_ptr5( at_plan ,alias_at_plan,&__plan,0,true);

  // args=center,radius
  gen _sphere(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    //    if ( (args.type==_SYMB) && (args._SYMBptr->sommet==at_equal) )
    //  return sphere_by_equation(vecteur(1,args),contextptr);
    if (args.type!=_VECT || args._VECTptr->size()<2)
      return gensizeerr(contextptr);
    gen errcode=checkanglemode(contextptr);
    if (is_undef(errcode)) return errcode;
    // if ((args._VECTptr->size()>=2) && (args._VECTptr->front().type==_SYMB) && (args._VECTptr->front()._SYMBptr->sommet==at_equal))
    //  return sphere_by_equation(*args._VECTptr,contextptr);
    vecteur v = *args._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    v=vecteur(v.begin(),v.begin()+s);
    v[0]=remove_at_pnt(v[0]);
    if (v[0].type!=_VECT)
      v[0]=gen(makevecteur(v[0],0,0),_POINT__VECT);
    v[1]=remove_at_pnt(v[1]);
    if (v[1].type==_VECT){
      gen tmp=v[1];
      if (v[1].subtype==_POINT__VECT){
	tmp=(v[1]-v[0])/2;
	if (tmp.type!=_VECT)
	  return gensizeerr(contextptr);
	v[0]=(v[0]+v[1])/2;
      }
      v[1]=l2norm(*tmp._VECTptr,contextptr);
    }
    else {
      if (is_strictly_positive(-v[1],contextptr))
	return gensizeerr(contextptr);
    }
    return pnt_attrib(symbolic(at_hypersphere,gen(v,args.subtype)),attributs,contextptr);
  }
  static const char _sphere_s []="sphere";
  static define_unary_function_eval (__sphere,&giac::_sphere,_sphere_s);
  define_unary_function_ptr5( at_sphere ,alias_at_sphere,&__sphere,0,true);

  static void option_adjust(int & nstep,int & jstep,int & kstep){
    if (nstep){
      jstep=int(std::sqrt(double(nstep)));
      kstep=int(std::sqrt(double(nstep)));
    }
    if (kstep<1)
      kstep=10;
    if (jstep<1)
      jstep=10;
  }

  gen cone(const gen & args,bool cone_complet,GIAC_CONTEXT){
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    double xmin=gnuplot_xmin,xmax=gnuplot_xmax,ymin=gnuplot_ymin,ymax=gnuplot_ymax,zmin=gnuplot_zmin,zmax=gnuplot_zmax;
    int jstep=0,kstep=0,nstep=0;
    vecteur vtmp;
    read_option(*args._VECTptr,xmin,xmax,ymin,ymax,zmin,zmax,vtmp,nstep,jstep,kstep,contextptr);
    option_adjust(nstep,jstep,kstep);
    if (s<3)
      return gensizeerr(contextptr);
    gen errcode=checkanglemode(contextptr);
    if (is_undef(errcode)) return errcode;
    ck_parameter_x(contextptr);
    ck_parameter_y(contextptr);
    ck_parameter_z(contextptr);
    vecteur v = *args._VECTptr;
    gen P=remove_at_pnt(v[0]),theta=v[2];
    if (v[1].type!=_VECT || P.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur xyz(makevecteur(x__IDNT_e,y__IDNT_e,z__IDNT_e));
    vecteur xyzP=subvecteur(xyz,*P._VECTptr);
    vecteur n=*v[1]._VECTptr,n1,n2;
    if (!normal3d(v[1],n1,n2))
      return gensizeerr(contextptr);
    n=divvecteur(n,abs_norm(n,contextptr));
    n1=divvecteur(n1,abs_norm(n1,contextptr));
    n2=divvecteur(n2,abs_norm(n2,contextptr));
    gen eq=normal(pow(dotvecteur(xyzP,n),2)*pow(sin(theta,contextptr),2)-(pow(dotvecteur(xyzP,n1),2)+pow(dotvecteur(xyzP,n2),2))*pow(cos(theta,contextptr),2),contextptr);
    gen M=P+u__IDNT_e*(cos(theta,contextptr)*n+sin(theta,contextptr)*(cos(v__IDNT_e,contextptr)*n1+sin(v__IDNT_e,contextptr)*n2));
    double uscale=gnuplot_tmax-gnuplot_tmin;
    bool cercles=false;
    if (s>3){
      gen tmp=evalf_double(v[3]/cos(theta,contextptr),eval_level(contextptr),contextptr);
      if (tmp.type==_DOUBLE_){
	uscale=tmp._DOUBLE_val;
	cercles=true;
      }
    }
    vecteur uv(makevecteur(u__IDNT_e,v__IDNT_e));
    gen res= plotparam3d(M,uv,xmin,xmax,ymin,ymax,zmin,zmax,cone_complet?-uscale:0,uscale,0,2*M_PI,false,false,attributs,uscale/jstep,M_PI/kstep,eq,xyz,contextptr);
    if (!cercles)
      return res;
    theta=evalf_double(theta,1,contextptr);
    if (theta.type!=_DOUBLE_)
      return res;
    double thetad=theta._DOUBLE_val;
    // add disque center P+uscale*cos(theta)*n, perp to n, r=uscale*sin(theta)
    vecteur vres(1,res);
    M=P+uscale*cos(theta,contextptr)*n+u__IDNT_e*(cos(v__IDNT_e,contextptr)*n1+sin(v__IDNT_e,contextptr)*n2);
    res=plotparam3d(M,uv,xmin,xmax,ymin,ymax,zmin,zmax,0,uscale*std::sin(thetad),0,2*M_PI,false,false,attributs,uscale*std::sin(thetad),M_PI/kstep,undef,xyz,contextptr);
    vres.push_back(res);
    if (cone_complet){
      M=P-uscale*cos(theta,contextptr)*n+u__IDNT_e*(cos(v__IDNT_e,contextptr)*n1+sin(v__IDNT_e,contextptr)*n2);
      res=plotparam3d(M,uv,xmin,xmax,ymin,ymax,zmin,zmax,0,uscale*std::sin(thetad),0,2*M_PI,false,false,attributs,uscale*std::sin(thetad),M_PI/kstep,undef,xyz,contextptr);
      vres.push_back(res);
    }
    return vres; // gen(vres,_SEQ__VECT);
  }
  // args=point, direction, angle 
  gen _cone(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return cone(args,true,contextptr);
  }
  static const char _cone_s []="cone";
  static define_unary_function_eval (__cone,&giac::_cone,_cone_s);
  define_unary_function_ptr5( at_cone ,alias_at_cone,&__cone,0,true);

  // args=point, direction, angle 
  gen _demi_cone(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return cone(args,false,contextptr);
  }
  static const char _demi_cone_s []="half_cone";
  static define_unary_function_eval (__demi_cone,&giac::_demi_cone,_demi_cone_s);
  define_unary_function_ptr5( at_demi_cone ,alias_at_demi_cone,&__demi_cone,0,true);

  // args=point, direction, radius 
  gen _cylindre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    double xmin=gnuplot_xmin,xmax=gnuplot_xmax,ymin=gnuplot_ymin,ymax=gnuplot_ymax,zmin=gnuplot_zmin,zmax=gnuplot_zmax;
    int jstep=0,kstep=0,nstep=0;
    vecteur vtmp;
    read_option(*args._VECTptr,xmin,xmax,ymin,ymax,zmin,zmax,vtmp,nstep,jstep,kstep,contextptr);
    option_adjust(nstep,jstep,kstep);
    if (s<3)
      return gensizeerr(contextptr);
    double uscale=gnuplot_tmax-gnuplot_tmin;
    bool cercles=false;
    vecteur v = *args._VECTptr;
    if (s>3){
      gen tmp=evalf_double(v[3],eval_level(contextptr),contextptr);
      if (tmp.type==_DOUBLE_){
	uscale=tmp._DOUBLE_val;
	cercles=true;
      }
    }
    gen errcode=checkanglemode(contextptr);
    if (is_undef(errcode)) return errcode;
    ck_parameter_x(contextptr);
    ck_parameter_y(contextptr);
    ck_parameter_z(contextptr);
    gen P=remove_at_pnt(v[0]),r=v[2];
    if (v[1].type!=_VECT || P.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur xyz(makevecteur(x__IDNT_e,y__IDNT_e,z__IDNT_e));
    vecteur xyzP=subvecteur(xyz,*P._VECTptr);
    vecteur n=*v[1]._VECTptr,n1,n2;
    if (!normal3d(v[1],n1,n2))
      return gensizeerr(contextptr);
    n=divvecteur(n,abs_norm(n,contextptr));
    n1=divvecteur(n1,abs_norm(n1,contextptr));
    n2=divvecteur(n2,abs_norm(n2,contextptr));
    gen M=P+u__IDNT_e*n+r*(cos(v__IDNT_e,contextptr)*n1+sin(v__IDNT_e,contextptr)*n2);
    gen eq=normal(pow(r,2)-(pow(dotvecteur(xyzP,n1),2)+pow(dotvecteur(xyzP,n2),2)),contextptr);
    vecteur uv(makevecteur(u__IDNT_e,v__IDNT_e));
    gen res=plotparam3d(M,uv,xmin,xmax,ymin,ymax,zmin,zmax,0,uscale,0,2*M_PI,false,false,attributs,uscale/jstep,M_PI/kstep,eq,xyz,contextptr);
    if (!cercles)
      return res;
    // add disque center P and P+uscale*n, perp to n, radius r
    r=evalf_double(r,1,contextptr);
    if (r.type!=_DOUBLE_)
      return res;
    double rd=r._DOUBLE_val;
    vecteur vres(1,res);
    M=P+u__IDNT_e*(cos(v__IDNT_e,contextptr)*n1+sin(v__IDNT_e,contextptr)*n2);
    res=plotparam3d(M,uv,xmin,xmax,ymin,ymax,zmin,zmax,0,rd,0,2*M_PI,false,false,attributs,rd,M_PI/kstep,undef,xyz,contextptr);
    vres.push_back(res);
    M=M+n*gen(uscale);
    res=plotparam3d(M,uv,xmin,xmax,ymin,ymax,zmin,zmax,0,rd,0,2*M_PI,false,false,attributs,rd,M_PI/kstep,undef,xyz,contextptr);
    vres.push_back(res);
    return vres; // gen(vres,_SEQ__VECT);
  }
  static const char _cylindre_s []="cylinder";
  static define_unary_function_eval (__cylindre,&giac::_cylindre,_cylindre_s);
  define_unary_function_ptr5( at_cylindre ,alias_at_cylindre,&__cylindre,0,true);

  // find the 2 points of d1 and d2 and a common normal vector to d1 d2
  bool perpendiculaire_commune(const gen & d1,const gen & d2,gen & M, gen & N,vecteur & n,GIAC_CONTEXT){
    gen D1=remove_at_pnt(d1);
    gen D2=remove_at_pnt(d2);
    if (D1.type!=_VECT || D1._VECTptr->size()!=2 || D2.type!=_VECT || D2._VECTptr->size()!=2)
      return false;
    gen & A=D1._VECTptr->front();
    gen & B=D1._VECTptr->back();
    gen & C=D2._VECTptr->front();
    gen & D=D2._VECTptr->back();
    if (!check3dpoint(A)){
      return false;
    }
    if (!check3dpoint(B))
      return false;
    if (!check3dpoint(C))
      return false;
    if (!check3dpoint(D))
      return false;
    vecteur v1(subvecteur(*B._VECTptr,*A._VECTptr));
    vecteur v2(subvecteur(*D._VECTptr,*C._VECTptr));
    n=*normal(cross(v1,v2,contextptr),contextptr)._VECTptr;
    if (is_zero(n))
      return false;
    // M=A+u*v1, N=C-v*v2, find u and v such that 
    // NM.v1=0 and NM.v2=0
    // where NM= -(C-A)+ u*v1+v*v2
    // Hence solve the system of matrix
    // v1.v1*u + v2.v1*v = (C-A).v1
    // v1.v2*u + v2.v2*v = (C-A).v2
    vecteur AC(subvecteur(*C._VECTptr,*A._VECTptr));
    gen v11(dotvecteur(v1,v1)),v22(dotvecteur(v2,v2)),v12(dotvecteur(v1,v2));
    gen AC1(dotvecteur(v1,AC)),AC2(dotvecteur(v2,AC));
    gen det(v11*v22-v12*v12);
    gen u= (v22*AC1-v12*AC2)/det,v=(v11*AC2-v12*AC1)/det;
    M=A+u*v1;
    N=C-v*v2;
    M.subtype=_POINT__VECT;
    N.subtype=_POINT__VECT;
    return true;
  }
  gen _perpendiculaire_commune(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2))
      return gensizeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    read_attributs(*args._VECTptr,attributs,contextptr);
    gen M,N;
    vecteur n;
    if (!perpendiculaire_commune(args._VECTptr->front(),args._VECTptr->back(),M,N,n,contextptr))
      return gensizeerr(gettext("Parellel lines"));
    return pnt_attrib(gen(makevecteur(M,N),_LINE__VECT),attributs,contextptr);
  }
  static const char _perpendiculaire_commune_s []="common_perpendicular";
  static define_unary_function_eval (__perpendiculaire_commune,&giac::_perpendiculaire_commune,_perpendiculaire_commune_s);
  define_unary_function_ptr5( at_perpendiculaire_commune ,alias_at_perpendiculaire_commune,&__perpendiculaire_commune,0,true);

  gen _polyedre(const gen & args,GIAC_CONTEXT);

  // Given a list of 3-d points, make a convex polyedre
  static vecteur polyedre(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT || g._VECTptr->size()<3)
      return vecteur(1,gensizeerr(contextptr));
    vecteur v =*g._VECTptr;
    // Construct faces: easy algorithm
    // Make all possibles plans with 3 points, find equation
    // Add to the face any point that is in the plane (eq=0)
    // All other points must have the same sign
    // Otherwise break and try next triple of points
    iterateur i=v.begin(),ie=v.end();
    for (;i!=ie;++i){
      *i = remove_at_pnt(*i);
      if (i->type!=_VECT || i->_VECTptr->size()!=3)
	return vecteur(1,gendimerr(contextptr));
    }
    vecteur faces;
    for (i=v.begin();i!=ie;++i){
      const_iterateur j=i+1;
      for (;j!=ie;++j){
	const_iterateur k=j+1;
	vecteur v1(subvecteur(*j->_VECTptr,*i->_VECTptr));
	for (;k!=ie;++k){
	  vecteur v2(subvecteur(*k->_VECTptr,*i->_VECTptr));
	  vecteur n(*normal(cross(v1,v2,contextptr),contextptr)._VECTptr);
	  if (is_zero(n))
	    continue;
	  const_iterateur l=v.begin();
	  gen s; // sign
	  gen eq,eqs;
	  vecteur currentface;
	  for (;l!=ie;++l){
	    if (l==i || l==j || l==k){
	      currentface.push_back(*l);
	      continue;
	    }
	    eq=normal(dotvecteur(n,subvecteur(*l->_VECTptr,*i->_VECTptr)),contextptr);
	    if (is_zero(eq)){
	      currentface.push_back(*l);
	      continue;
	    }
	    eqs=evalf_double(sign(eq,contextptr),1,contextptr);
	    if (eqs.type!=_DOUBLE_)
	      return vecteur(1,gensizeerr(gettext("Unable to check sign ")+eq.print(contextptr)));
	    if (is_zero(s))
	      s=eqs;
	    if (eqs!=s)
	      break;
	  } // end for l
	  if (l==ie)
	    faces.push_back(currentface);
	}
      }
    }
    return faces;
  }
  static gen polyedre_face(vecteur & v,const vecteur & attributs,GIAC_CONTEXT){
    iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type!=_VECT)
	return gensizeerr(gettext("Each element must be a face (vector of 3/4 points)"));
      vecteur w(*it->_VECTptr);
      int s=w.size();
      if (s<3)
	return gensizeerr(gettext("at least 3 points by face"));
      iterateur jt=w.begin(),jtend=w.end();
      for (;jt!=jtend;++jt){
	*jt=remove_at_pnt(*jt);
	if (jt->type==_VECT)
	  jt->subtype=_POINT__VECT;
      }
      *it=w;
      it->subtype=_GROUP__VECT;
    }
    return pnt_attrib(gen(v,_POLYEDRE__VECT),attributs,contextptr);
  }
  gen _polyedre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur v(*args._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    v=vecteur(v.begin(),v.begin()+s);
    if (!s)
      return gendimerr(contextptr);
    if (s==2){
      // Base, sommet
      gen base=remove_at_pnt(v.front());
      if (base.type!=_VECT)
	return gensizeerr(contextptr);
      gen & sommet=v.back();
      vecteur w=*base._VECTptr;
      vecteur nv;
      if (w.front()!=w.back())
	w.push_back(w.front());
      int s=w.size();
      if (s<3)
	return gendimerr(contextptr);
      for (int i=0;i<s;++i){
	nv.push_back(makevecteur(w[i],w[(i+1)%s],sommet));
      }
      nv.push_back(base);
      return polyedre_face(nv,attributs,contextptr);
    }
    gen g=remove_at_pnt(v.front());
    if (g.type==_VECT && g._VECTptr->size()==3 && remove_at_pnt(g._VECTptr->front()).type!=_VECT )
      v=polyedre(v,contextptr);
    return polyedre_face(v,attributs,contextptr);
  }
  static const char _polyedre_s []="polyhedron";
  static define_unary_function_eval (__polyedre,&giac::_polyedre,_polyedre_s);
  define_unary_function_ptr5( at_polyedre ,alias_at_polyedre,&__polyedre,0,true);

  gen _prisme(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int sv=read_attributs(v,attributs,contextptr);
    if (sv!=2)
      return gendimerr(contextptr);
    gen base=remove_at_pnt(v[0]), sommet=remove_at_pnt(v[1]);
    if (base.type!=_VECT || base._VECTptr->size()<2)
      return gensizeerr(contextptr);
    vecteur w = *base._VECTptr;
    gen x=sommet-w[0];
    int s=w.size();
    vecteur faces;
    for (int i=0;i<s;++i){
      faces.push_back(makevecteur(w[i],w[(i+1)%s],w[(i+1)%s]+x,w[i]+x));
    }
    faces.push_back(base);
    for (int i=0;i<s;++i)
      w[i]=w[i]+x;
    faces.push_back(w);
    return polyedre_face(faces,attributs,contextptr);
  }
  static const char _prisme_s []="prism";
  static define_unary_function_eval (__prisme,&giac::_prisme,_prisme_s);
  define_unary_function_ptr5( at_prisme ,alias_at_prisme,&__prisme,0,true);

  static gen parallelepipede4(const gen & A0,const gen & B0,const gen & C0,const gen & D0,const vecteur & attributs,GIAC_CONTEXT){
    gen A(A0),B(B0),C(C0),D(D0);
    A.subtype=_POINT__VECT;
    B.subtype=_POINT__VECT;
    C.subtype=_POINT__VECT;
    D.subtype=_POINT__VECT;
    gen AB=B-A,AC=C-A,AD=D-A;
    gen E=A+AB+AC,F=A+AC+AD,G=A+AB+AD,H=A+AB+AC+AD;
    E.subtype=_POINT__VECT;
    F.subtype=_POINT__VECT;
    G.subtype=_POINT__VECT;
    H.subtype=_POINT__VECT;
    vecteur res;
    // Face 1 A B // C E=A+AB+AC
    res.push_back(makevecteur(A,C,E,B));
    // Face 2 A C // D F=A+AC+AD
    res.push_back(makevecteur(A,D,F,C));
    // Face 3 A B // D G=A+AB+AD
    res.push_back(makevecteur(A,B,G,D));
    // Face 4 B E // G H
    res.push_back(makevecteur(B,E,H,G));
    // Face 5 C E // F H
    res.push_back(makevecteur(C,F,H,E));
    // Face 6 D G // F H
    res.push_back(makevecteur(D,G,H,F));
    return polyedre_face(res,attributs,contextptr);
  }
  gen _parallelepipede(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v(*args._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s!=4)
      return gendimerr(contextptr);
    gen A=remove_at_pnt(v[0]);
    gen B=remove_at_pnt(v[1]);
    gen C=remove_at_pnt(v[2]);
    gen D=remove_at_pnt(v[3]);
    return parallelepipede4(A,B,C,D,attributs,contextptr);
  }
  static const char _parallelepipede_s []="parallelepiped";
  static define_unary_function_eval (__parallelepipede,&giac::_parallelepipede,_parallelepipede_s);
  define_unary_function_ptr5( at_parallelepipede ,alias_at_parallelepipede,&__parallelepipede,0,true);

  static gen pyramide4(const gen & A0,const gen & B0,const gen & C0,const gen & D0,const vecteur & attributs,GIAC_CONTEXT){
    vecteur res;
    gen A(A0),B(B0),C(C0),D(D0);
    A.subtype=_POINT__VECT;
    B.subtype=_POINT__VECT;
    C.subtype=_POINT__VECT;
    D.subtype=_POINT__VECT;
    // Face 1 A B C
    res.push_back(makevecteur(A,B,C));
    // Face 2 A C D
    res.push_back(makevecteur(A,C,D));
    // Face 3 A B D
    res.push_back(makevecteur(A,B,D));
    // Face 4 B C D
    res.push_back(makevecteur(B,C,D));
    return polyedre_face(res,attributs,contextptr);
  }

  gen _pyramide(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur v(*args._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    v=vecteur(v.begin(),v.begin()+s);
    gen A=remove_at_pnt(v[0]);
    if (s==2){
      gen r=abs(v[1],contextptr);
      v[1]=A+r*gen(makevecteur(1,0,0));
      v.push_back(A+r*gen(makevecteur(plus_one_half,plus_sqrt3_2,0)));
      ++s;
    }
    gen B=remove_at_pnt(v[1]);
    gen C=remove_at_pnt(v[2]);
    if (s==3){ // tetraedre
      gen AB=B-A,AC=C-A;
      if (AB.type!=_VECT || AB._VECTptr->size()!=3 || AC.type!=_VECT || AC._VECTptr->size()!=3)
	return gensizeerr(contextptr);
      vecteur v1(*AB._VECTptr),v2(*AC._VECTptr);
      vecteur n(cross(v1,v2,contextptr));
      v2=cross(n,v1,contextptr);
      // Normalize
      gen a(dotvecteur(v1,v1));
      v2=multvecteur(sqrt(3*a/dotvecteur(v2,v2),contextptr),v2);
      C = A + divvecteur(v1,2) + divvecteur(v2,2) ; 
      n=  multvecteur(sqrt(2*a/3/dotvecteur(n,n),contextptr),n);
      gen D = A + divvecteur(v1,2) + divvecteur(v2,6) + n;
      return pyramide4(A,B,C,D,attributs,contextptr);
    }
    gen D=remove_at_pnt(v[3]);
    return pyramide4(A,B,C,D,attributs,contextptr);
  }
  static const char _pyramide_s []="pyramid";
  static define_unary_function_eval (__pyramide,&giac::_pyramide,_pyramide_s);
  define_unary_function_ptr5( at_pyramide ,alias_at_pyramide,&__pyramide,0,true);

  static const char _tetraedre_s []="tetrahedron";
  static define_unary_function_eval (__tetraedre,&giac::_pyramide,_tetraedre_s);
  define_unary_function_ptr5( at_tetraedre ,alias_at_tetraedre,&__tetraedre,0,true);
  
  // Find A,B,C,D such that AB=AC=AD and all are orthogonal
  static bool cube_octaedre(const gen & args,gen & A,gen & B,gen & C,gen & D,vecteur & attributs,GIAC_CONTEXT){
    if (args.type!=_VECT)
      return false; // gensizeerr(contextptr);
    vecteur &v(*args._VECTptr);
    int s=read_attributs(v,attributs,contextptr);
    if (s<2)
      return false; // gendimerr(contextptr);
    A=v[0];
    B=v[1];
    if (s==2){
      gen r=abs(B,contextptr);
      B=A+r*gen(makevecteur(r,0,0));
      C=A+r*gen(makevecteur(0,r,0));
    }
    else
      C=v[2];
    gen AB(B-A),AC(C-A);
    if (AB.type!=_VECT || AB._VECTptr->size()!=3 || AC.type!=_VECT || AC._VECTptr->size()!=3)
      return false; // gensizeerr(contextptr);
    // AB cross AC -> normal direction to ABC plan
    gen AB2(normal(scalar_product(AB,AB,contextptr),contextptr));
    if (is_undef(AB2))
      return false;
    vecteur AD=*normal(cross(*AB._VECTptr,*AC._VECTptr,contextptr),contextptr)._VECTptr;
    D=A+AD*sqrt(normal(AB2/dotvecteur(AD,AD),contextptr),contextptr);
    // binormal direction gives the 2nd direction AB, AE, AD
    vecteur AE=*normal(cross(AD,*AB._VECTptr,contextptr),contextptr)._VECTptr;
    C=A+AE*sqrt(normal(AB2/dotvecteur(AE,AE),contextptr),contextptr);
    A.subtype=_POINT__VECT;
    B.subtype=_POINT__VECT;
    C.subtype=_POINT__VECT;
    D.subtype=_POINT__VECT;
    return true;
  }

  gen _tetraedre_centre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen O,b,c,d;
    vecteur attributs(1,default_color(contextptr));
    if (!cube_octaedre(args,O,b,c,d,attributs,contextptr))
      return gensizeerr(contextptr);
    gen v1(normal(b-O,contextptr)),v2(normal(c-O,contextptr)),v3(normal(d-O,contextptr));
    gen A = b; // O + v1
    gen B = normal(O - v1/3 - sqrt(2,contextptr)*v2/3 - sqrt(6,contextptr)*v3/3,contextptr);
    gen C = normal(O - v1/3 - sqrt(2,contextptr)*v2/3 + sqrt(6,contextptr)*v3/3,contextptr);
    gen D = normal(O - v1/3 + 2*sqrt(2,contextptr)*v2/3,contextptr);
    return pyramide4(A,B,C,D,attributs,contextptr);
  }
  static const char _tetraedre_centre_s []="centered_tetrahedron";
  static define_unary_function_eval (__tetraedre_centre,&giac::_tetraedre_centre,_tetraedre_centre_s);
  define_unary_function_ptr5( at_tetraedre_centre ,alias_at_tetraedre_centre,&__tetraedre_centre,0,true);
  
  // args= 3 points A, B, C
  gen _cube(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen A,B,C,D;
    vecteur attributs(1,default_color(contextptr));
    if (!cube_octaedre(args,A,B,C,D,attributs,contextptr))
      return gensizeerr(contextptr);
    return parallelepipede4(A,B,C,D,attributs,contextptr);
  }
  static const char _cube_s []="cube";
  static define_unary_function_eval (__cube,&giac::_cube,_cube_s);
  define_unary_function_ptr5( at_cube ,alias_at_cube,&__cube,0,true);

  // args= center A, vertex B, point C such that ABC is a symmetry plan
  gen _cube_centre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()<3)
      return gensizeerr(contextptr);
    gen A,B,C,D;
    vecteur attributs(1,default_color(contextptr));
    if (!cube_octaedre(args,A,B,C,D,attributs,contextptr))
      return gensizeerr(contextptr);
    gen x = (B-A)/3;
    // Take C1=A+cos(theta)*(B-A)+sin(theta)*(C-A), cos(theta)=1/3
    gen C1 = normal(A+x+2*sqrt(2,contextptr)/3*(C-A),contextptr);
    gen D1 = normal(A+x-sqrt(2,contextptr)/3*(C-A)+sqrt(6,contextptr)/3*(D-A),contextptr);
    if (!cube_octaedre(makevecteur(B,D1,C1),A,B,C,D,attributs,contextptr))
      return gensizeerr(contextptr);
    return parallelepipede4(A,B,C,D,attributs,contextptr);
  }
  static const char _cube_centre_s []="centered_cube";
  static define_unary_function_eval (__cube_centre,&giac::_cube_centre,_cube_centre_s);
  define_unary_function_ptr5( at_cube_centre ,alias_at_cube_centre,&__cube_centre,0,true);

  // args= center A, point B, C such that ABC = symmetry plan with 4 vertices
  gen _octaedre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen A,B,C,D;
    vecteur attributs(1,default_color(contextptr));
    if (!cube_octaedre(args,A,B,C,D,attributs,contextptr))
      return gensizeerr(contextptr);
    // B, C, D are 3 vertices
    gen E,F,G;
    E = A - (B-A);
    F = A - (C-A);
    G = A - (D-A);
    vecteur faces;
    faces.push_back(makevecteur(B,C,D));
    faces.push_back(makevecteur(B,C,G));
    faces.push_back(makevecteur(B,F,D));
    faces.push_back(makevecteur(B,F,G));
    faces.push_back(makevecteur(E,C,D));
    faces.push_back(makevecteur(E,C,G));
    faces.push_back(makevecteur(E,F,D));
    faces.push_back(makevecteur(E,F,G));
    return polyedre_face(faces,attributs,contextptr);
  }
  static const char _octaedre_s []="octahedron";
  static define_unary_function_eval (__octaedre,&giac::_octaedre,_octaedre_s);
  define_unary_function_ptr5( at_octaedre ,alias_at_octaedre,&__octaedre,0,true);

  static void res_push(vecteur & res,gen * s, int i,int j,int k){
    res.push_back(makevecteur(s[i],s[j],s[k]));
  }
  // args= centre, sommet1, sommet2 (direction of)
  gen _icosaedre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    gen errcode=checkanglemode(contextptr);
    if (is_undef(errcode)) return errcode;
    vecteur & v = *args._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int sv=read_attributs(v,attributs,contextptr);
    if (sv!=3)
      return gendimerr(contextptr);
    gen s[12];
    gen centre=v[0],s1=v[1],s2=v[2];
    gen v1g(s1-centre),v2g(s2-centre);
    // Icosaedre=s1+symetric of s1 with respect to center
    // + 2* 5 points as a pentagon on 2 plans perpendicular to centre->s1
    // If the distance of the 2 // plans is 1 to the center
    // Then the 5 vertices are at distance 2 to the intersection axe/plan
    // (abscisse=+/-1, sqrt(y^2+z^2)=2)
    // and |s1-centre|=sqrt(5)
    if (v1g.type!=_VECT|| v2g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur v1(*v1g._VECTptr),v2(*v2g._VECTptr);
    vecteur n(cross(v1,v2,contextptr));
    v2=divvecteur(cross(n,v1,contextptr),sqrt(dotvecteur(n,n),contextptr)); 
    // norm=distance(centre,sommet)
    n=multvecteur(sqrt(dotvecteur(v1,v1)/dotvecteur(n,n),contextptr),n);
    // centre +/- (v1/sqrt(5) + 2/sqrt(5)*(cos(2*k*pi/5)*v2 +sin(2*k*pi/5)*n))
    s[0]=s1;
    s[11]=s1-multvecteur(2,v1);
    for (int i=0;i<5;++i){
      context ctmp;
      gen tmp = gen(1)/sqrt(5,contextptr)*(gen(v1) + 2*(cos(2*i*cst_pi/5,&ctmp)*gen(v2)+sin(2*i*cst_pi/5,&ctmp)*n));
      s[1+i] = centre + tmp;
      s[10-i] = centre - tmp;
    }
    // Make 5 faces s[0] with s[1+i], s[2+i] for i in 1..4 and with 5,1
    // 5 with s[11] with s[11-i], s[10-i] for i in 1..4 and with 
    // 1,7,8 + 1,7,2 + 2,6,7 + 2,6,3 + 3,10,6 + 3,10,4 + 4,9,10 + 4,9,5 + 5,8,9+ 5,8,1
    vecteur res;
    for (int i=1;i<5;++i){
      res_push(res,s,0,i,1+i); res_push(res,s,11,11-i,10-i);
    }
    res_push(res,s,0,5,1); res_push(res,s,11,6,10);
    res_push(res,s,1,7,8); res_push(res,s,1,7,2);
    res_push(res,s,2,6,7); res_push(res,s,2,6,3);
    res_push(res,s,3,10,6); res_push(res,s,3,10,4);
    res_push(res,s,4,9,10); res_push(res,s,4,9,5);
    res_push(res,s,5,8,9); res_push(res,s,5,8,1);
    return polyedre_face(res,attributs,contextptr);
  }
  static const char _icosaedre_s []="icosahedron";
  static define_unary_function_eval (__icosaedre,&giac::_icosaedre,_icosaedre_s);
  define_unary_function_ptr5( at_icosaedre ,alias_at_icosaedre,&__icosaedre,0,true);

  static void res_push(vecteur & res,gen * s, int i,int j,int k,int l,int m){
    res.push_back(makevecteur(s[i],s[j],s[k],s[l],s[m]));
  }
  // args= centre, sommet1, 3rd point defining a plan containing the axis
  // Example dodecaedre([0,0,0],[0,2,sqrt(5)/2+3/2],[0,0,1])
  gen _dodecaedre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    int sv=read_attributs(*args._VECTptr,attributs,contextptr);
    if (sv!=3)
      return gendimerr(contextptr);
    gen errcode=checkanglemode(contextptr);
    if (is_undef(errcode)) return errcode;
    vecteur v = *evalf(args,1,contextptr)._VECTptr;
    gen centre=v[0],s1=v[1],s2=v[2];
    gen v1g(s1-centre),v2g(s2-centre);
    if (v1g.type!=_VECT|| v2g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur v1(*v1g._VECTptr),v2(*v2g._VECTptr); 
    gen phi=evalf((sqrt(5,contextptr)+1)/2,1,0);
    gen r2(dotvecteur(v1,v1)); // r = |v1| = sqrt(6+3phi)*unit of length
    vecteur w2(cross(v1,v2,contextptr)); // y direction
    vecteur v3(cross(w2,v1,contextptr)); // v1,v3 contains the axis v1.v3=0
    // Now normalize w2 to 1 unit of length
    w2=multvecteur(sqrt(r2/dotvecteur(w2,w2)/(6+3*phi),contextptr),w2);
    v3=multvecteur(sqrt(r2/dotvecteur(v3,v3),contextptr),v3); // |v3|=|v1|=r
    gen w1=(2*gen(v1)-(phi+1)*gen(v3))/(6+3*phi); // |w1|=sqrt(6+3phi)*norm(v1)/(6+3phi)
    gen w3=((phi+1)*v1+2*gen(v3))/(6+3*phi); // = |w2|=1 unit of length
    // Dodecaedre at center 0. Edge length=sqrt(10-2*sqrt(5))
    // Golden ratio phi=(sqrt(5)+1)/2 (phi^2=phi+1)
    // Vertices at +/-(2*cos(2*pi/5),2*sin(2*pi/5),phi+1)
    // and +/-(2*phi*cos(2*pi/5),2*phi*sin(2*pi/5),phi-1)
    // Sphere radius ^2 = 4 + (phi+1)^2 = 6+3*phi=(15+3*sqrt(5))/2
    gen s[20];
    context ctmp;
    for (int i=0;i<5;++i){
      s[i]=centre+evalf(2*cos(2*i*cst_pi/5,&ctmp)*w1+2*sin(2*i*cst_pi/5,&ctmp)*w2+(phi+1)*w3,1,contextptr);
      s[15+i]=centre-s[i];
      s[5+i]=centre+evalf(2*phi*(cos(2*i*cst_pi/5,&ctmp)*w1+sin(2*i*cst_pi/5,&ctmp)*w2)+(phi-1)*w3,1,contextptr);
      s[10+i]=centre-s[5+i];
    }
    vecteur res;
    res_push(res,s,0,1,2,3,4); res_push(res,s,15,16,17,18,19);
    for (int i=0;i<5;++i){
      res_push(res,s,(i+1)%5,5+(i+1)%5,10+(3+i)%5,5+i,i);
      res_push(res,s,15+(i+1)%5,10+(i+1)%5,5+(3+i)%5,10+i,15+i);
    }
    return polyedre_face(res,attributs,contextptr);
  }
  static const char _dodecaedre_s []="dodecahedron";
  static define_unary_function_eval (__dodecaedre,&giac::_dodecaedre,_dodecaedre_s);
  define_unary_function_ptr5( at_dodecaedre ,alias_at_dodecaedre,&__dodecaedre,0,true);

  gen _aretes(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    bool tmp=show_point(contextptr);
    show_point(false,contextptr);
    gen g=remove_at_pnt(args);
    vecteur v(gen2vecteur(g));
    vecteur res;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (!ckmatrix(*it))
	return gensizeerr(contextptr);
      const_iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
      for (;jt+1!=jtend;++jt){
	res.push_back(_segment(makesequence(*jt,*(jt+1)),contextptr));
      }
      res.push_back(_segment(makesequence(*jt,it->_VECTptr->front()),contextptr));
    }
    show_point(tmp,contextptr);
    return res;
  }
  static const char _aretes_s []="line_segments";
  static define_unary_function_eval (__aretes,&giac::_aretes,_aretes_s);
  define_unary_function_ptr5( at_aretes ,alias_at_aretes,&__aretes,0,true);

  gen _faces(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return remove_at_pnt(args);
  }
  static const char _faces_s []="faces";
  static define_unary_function_eval (__faces,&giac::_faces,_faces_s);
  define_unary_function_ptr5( at_faces ,alias_at_faces,&__faces,0,true);

  static gen rotation3d(const gen & elem,const gen & b,GIAC_CONTEXT){
    if (elem.type==_VECT && elem._VECTptr->size()==2){
      gen A=elem._VECTptr->front();
      gen M=elem._VECTptr->back();
      gen res=A+M*(b-A);
      res.subtype=_POINT__VECT;
      return res;
    }
    return gensizeerr(contextptr);
  }

  gen similitude3d(const vecteur & centrev,const gen & angle,const gen & rapport,const gen & b,int symrot,GIAC_CONTEXT){
    if (centrev.size()!=2 || centrev.front().type!=_VECT || centrev.back().type!=_VECT)
      return gensizeerr(contextptr);
    vecteur A(*centrev.front()._VECTptr),B(*centrev.back()._VECTptr);
    vecteur AB(subvecteur(B,A));
    if (AB.size()!=3)
      return gendimerr(contextptr);
    // Find rotation matrix from isom.cc/h
    gen M=rapport*mkisom(makevecteur(AB,angle),symrot,contextptr);
    gen elem(makevecteur(A,M));
    if (b.type==_VECT)
      return symb_pnt(apply3d(elem,b,contextptr,rotation3d),default_color(contextptr),contextptr);
    if (b.is_symb_of_sommet(at_hypersphere)){
      gen c,r;
      centre_rayon(b,c,r,false,contextptr);
      c=A+M*(c-A);
      return _sphere(makesequence(c,r),contextptr);
    }
    if (b.is_symb_of_sommet(at_hyperplan)){
      vecteur n,P;
      if (!hyperplan_normal_point(b,n,P))
	return gensizeerr(contextptr);
      gen Pr=A+M*(P-A);
      gen nr=M*n;
      return _plan(makesequence(nr,Pr),contextptr);
    }
    return curve_surface_apply(elem,b,rotation3d,contextptr);
  }


  gen plotparam3d(const gen & f,const vecteur & vars,double function_xmin,double function_xmax, double function_ymin, double function_ymax,double function_zmin,double function_zmax,double function_umin,double function_umax,double function_vmin,double function_vmax,bool densityplot,bool f_autoscale,const vecteur & attributs,double ustep,double vstep,const gen & eq,const vecteur & eqvars,GIAC_CONTEXT){
    int color=default_color(contextptr);
    gen attribut=attributs.empty()?color:attributs[0];
    if (attribut.type==_INT_)
      color=attribut.val;
    identificateur u("u"),v("v");
    vecteur vsub(1,u);
    vsub.push_back(v);
    gen ff=subst(f,vars,vsub,false,contextptr);
    gen r=symbolic(at_plotparam,makesequence(f,vars));
    if (is_zero(derive(ff,v,contextptr))){
      vecteur res;
      double x=function_umin;
      double dx=ustep;
      int n=int((function_umax-function_umin)/ustep+.5);
      vecteur values;
      gen prec,cur,tmp;
      double Dx=function_xmax-function_xmin,Dy=function_ymax-function_ymin,Dz=function_zmax-function_zmin,fmin=function_umin;
      for (int i=0;i<=n;++i,x+=dx){
	cur=evalf_double(subst(f,vars[0],x,false,contextptr),1,contextptr);
	if (cur.type!=_VECT || cur._VECTptr->size()!=3)
	  continue;
	cur.subtype=_POINT__VECT;
	vecteur & curt=*cur._VECTptr;
	if (curt[0].type!=_DOUBLE_ || curt[1].type!=_DOUBLE_ || curt[2].type!=_DOUBLE_)
	  continue;
	bool joindre=true;
	if (prec.type==_VECT && prec._VECTptr->size()==3 && !values.empty()){
	  vecteur & precv=*prec._VECTptr;
	  double oldx=precv[0]._DOUBLE_val,oldy=precv[1]._DOUBLE_val,oldz=precv[2]._DOUBLE_val;
	  double curx=curt[0]._DOUBLE_val,cury=curt[1]._DOUBLE_val,curz=curt[2]._DOUBLE_val;
	  if (std::abs(curx-oldx)>Dx/10 || std::abs(cury-oldy)>Dy/10 || std::abs(curz-oldz)>Dz/10 ){
	    tmp=evalf_double(subst(f,vars[0],x+dx/2,false,contextptr),1,contextptr);
	    if (tmp.type!=_VECT || tmp._VECTptr->size()!=3)
	      continue;
	    tmp.subtype=_POINT__VECT;
	    vecteur & tmpv=*tmp._VECTptr;
	    double entrex=tmpv[0]._DOUBLE_val,entrey=tmpv[1]._DOUBLE_val,entrez=tmpv[2]._DOUBLE_val;
	    if ( (entrex-oldx)*(curx-entrex)<0 || (entrey-oldy)*(cury-entrey)<0 || (entrez-oldz)*(curz-entrez)<0 )
	      joindre=false;
	  }
	}
	if (joindre)
	  values.push_back(cur);
	else {
	  res.push_back(symb_pnt(symb_curve(gen(makevecteur(f,vars[0],fmin,x,gen(values,_GROUP__VECT)),_PNT__VECT),undef),color,contextptr));
	  fmin=x;
	  values.clear();
	  prec=0;
	}
	prec=cur;
      }
      r=symb_pnt(symb_curve(gen(makevecteur(f,vars[0],fmin,function_umax,gen(values,_GROUP__VECT)),_PNT__VECT),undef),color,contextptr);
      if (!res.empty()){
	res.push_back(r);
	r=res; // gen(res,_SEQ__VECT);
      }
    } // if is_zero(derive(ff,v))
    else {
      vecteur vals(2);
      double x=function_umin,y=function_vmin;
      double dx=ustep;
      double dy=vstep;
      int nu=int((function_umax-function_umin)/ustep+.5),nv=int((function_vmax-function_vmin)/vstep+.5);
      // Compute a grid of values
      vecteur values;
      for (int i=0;i<=nu;++i,x+=dx){
	y=function_vmin;
	vals[0]=x;
	vecteur tmp;
	for (int j=0;j<=nv;++j,y+=dy){
	  vals[1]=y;
	  gen tmppnt=evalf_double(subst(f,vars,vals,false,contextptr),1,contextptr);
	  if (tmppnt.type==_VECT)
	    tmppnt.subtype=_POINT__VECT;
	  tmp.push_back(tmppnt);
	}
	values.push_back(gen(tmp,_GROUP__VECT));
      }
      r=symb_pnt(hypersurface(gen(makevecteur(f,vars,makevecteur(function_umin,function_vmin),makevecteur(function_umax,function_vmax),gen(values,_GROUP__VECT)),_PNT__VECT),eq,eqvars),color,contextptr);
    }
#ifdef WITH_GNUPLOT
    int out_handle;
    bool clrplot=false;
    FILE * gnuplot_out_readstream,* stream = open_gnuplot(clrplot,gnuplot_out_readstream,out_handle);
#ifdef IPAQ
    fprintf(stream,"set samples 10\n");
    //    fprintf(stream,"show samples\n");
#endif
    r.subtype=gnuplot_fileno;
    reset_gnuplot_hidden3d(stream);
    if (debug_infolevel)
      printf("set urange [%g:%g]\n",function_umin,function_umax);
    fprintf(stream,"set urange [%g:%g]\n",function_umin,function_umax);
    if (debug_infolevel)
      printf("set vrange [%g:%g]\n",function_vmin,function_vmax);
    fprintf(stream,"set vrange [%g:%g]\n",function_vmin,function_vmax);
    if (!f_autoscale){
      if (debug_infolevel)
	printf("set xrange [%g:%g]\n",function_xmin,function_xmax);
      fprintf(stream,"set xrange [%g:%g]\n",function_xmin,function_xmax);
      if (debug_infolevel)
	printf("set yrange [%g:%g]\n",function_ymin,function_ymax);
      fprintf(stream,"set yrange [%g:%g]\n",function_ymin,function_ymax);
      if (debug_infolevel)
	printf("set zrange [%g:%g]\n",function_zmin,function_zmax);
      fprintf(stream,"set zrange [%g:%g]\n",function_zmin,function_zmax);
    }
    if (clrplot || gnuplot_do_splot){
      if (debug_infolevel)
	printf("%s","splot ");	
      fprintf(stream,"%s","splot ");
    }
    else {
      if (debug_infolevel)
	printf("%s","replot ");
      fprintf(stream,"%s","replot ");
    }
    gnuplot_do_splot=false;
    if (ff.type!=_VECT){
      if (debug_infolevel)
	printf("%s notitle\n",gnuplot_traduit(ff).c_str());
      fprintf(stream,"%s notitle\n",gnuplot_traduit(ff).c_str());
    }
    else {
      string tmp(gnuplot_traduit(ff));
      // cout << tmp.substr(1,tmp.size()-2) << endl;
      if (debug_infolevel)
	printf("%s notitle\n",tmp.substr(1,tmp.size()-2).c_str());
      fprintf(stream,"%s notitle\n",tmp.substr(1,tmp.size()-2).c_str());
    }
    win9x_gnuplot(stream);
    gnuplot_wait(out_handle,gnuplot_out_readstream,gnuplot_wait_times);
    ++gnuplot_fileno;
    // return gnuplot_fileno-1;
    return r;
#endif // GNUPLOT    
    return r;
  }

  bool normal3d(const gen & nn,vecteur & v1,vecteur & v2){
    if (nn.type!=_VECT || nn._VECTptr->size()!=3)
      return false;
    vecteur & n = *nn._VECTptr;
    if (is_zero(n[0]))
      v1=makevecteur(1,0,0);
    else
      v1=makevecteur(n[1],-n[0],0);
    v2=cross(n,v1,context0);
    return true;
  }

  gen hypersphere_equation(const gen & g,const vecteur & xyz){
    gen centre,rayon;
    if (!centre_rayon(g,centre,rayon,false,0) ||centre.type!=_VECT)
      return gensizeerr(gettext("hypersphere_equation"));
    vecteur & v=*centre._VECTptr;
    if (v.size()!=3)
      return gendimerr(gettext("hypersphere_equation"));
    vecteur xyzc(subvecteur(xyz,v));
    gen eq=ratnormal(dotvecteur(xyzc,xyzc)-pow(rayon,2));
    return eq;
  }
  vecteur hypersphere_parameq(const gen & g,const vecteur & st){
    gen centre,rayon;
    if (!centre_rayon(g,centre,rayon,false,0) ||centre.type!=_VECT)
      return vecteur(1,gensizeerr(gettext("hypersphere_parameq")));
    vecteur & v=*centre._VECTptr;
    if (v.size()!=3)
      return vecteur(1,gendimerr(gettext("hypersphere_parameq")));
    vecteur res(4);
    res[0]=centre+makevecteur(rayon*symb_cos(st[0])*symb_cos(st[1]),rayon*symb_cos(st[0])*symb_sin(st[1]),rayon*symb_sin(st[0]));
    res[1]=st;
    res[2]=makevecteur(-cst_pi_over_2,0);
    res[3]=makevecteur(cst_pi_over_2,cst_two_pi);
    return res;
  }

  gen hypersurface_equation(const gen & g,const vecteur & xyz,GIAC_CONTEXT){
    if (!g.is_symb_of_sommet(at_hypersurface))
      return gensizeerr(contextptr);
    gen & f=g._SYMBptr->feuille;
    if (f.type!=_VECT) return gensizeerr(contextptr);
    vecteur & fv=*f._VECTptr;
    if (fv.size()==3 && fv[1].type!=_VECT && fv[2].type==_VECT){
      gen eq=fv[1];
      if (is_undef(eq)){
	gen point=fv[0];
	if (point.type==_VECT && point._VECTptr->size()>=2){
	  gen f=(*point._VECTptr)[0];
	  gen vars=(*point._VECTptr)[1];
	  if (vars.type==_VECT && vars._VECTptr->size()==2 && f.type==_VECT && f._VECTptr->size()==3 && xyz.size()==3){
	    vecteur lv(*vars._VECTptr);
	    lvar(f,lv);
	    if (lv==vars){ // resultant -> eq
	      gen tmp1=_resultant(makesequence(f[0]-xyz[0],f[1]-xyz[1],vars[0]),contextptr);
	      if (is_undef(tmp1))
		return tmp1;
	      gen tmp2=_resultant(makesequence(f[0]-xyz[0],f[2]-xyz[2],vars[0]),contextptr);
	      if (is_undef(tmp2))
		return tmp2;
	      gen res=_resultant(makesequence(tmp1,tmp2,vars[1]),contextptr);
	      return res;
	    }
	  }
	}
      }
      return subst(fv[1],*fv[2]._VECTptr,xyz,false,contextptr);
    }
    return gensizeerr(gettext("Hypersurface w/o equation"));
  }

  // a must be a vector of length 2, b a symbolic
  vecteur interdroitehyperplan(const gen & a,const gen &b,GIAC_CONTEXT){ 
    if (a.type!=_VECT || b.type!=_SYMB || a._VECTptr->size()!=2)
      return vecteur(1,gensizeerr(contextptr));
    // D inter H
    gen A=a._VECTptr->front(),B=a._VECTptr->back(); // D=(AB)
    gen & f=b._SYMBptr->feuille;
    gen AB=B-A;
    if (f.type!=_VECT || f._VECTptr->size()!=2 )
      return vecteur(1,gensizeerr(contextptr));
    gen C=f._VECTptr->back(),Hn=f._VECTptr->front(); // H= C normal is n
    gen AC=C-A;
    if (Hn.type!=_VECT || AB.type!=_VECT || AC.type!=_VECT)
      return vecteur(1,gensizeerr(contextptr));
    vecteur v(*AB._VECTptr),n(*Hn._VECTptr);
    gen vn(normal(dotvecteur(v,n),contextptr));
    if (is_zero(vn)){ // D is parallel to H
      return vecteur(0); // FIXME should be D if D is in H
    }
    // H inter D = A + t*v, with t such that A+t*v-C is normal to n
    // Hence t=(C-A).n/(v.n)
    gen t=dotvecteur(*AC._VECTptr,n)/vn;
    gen M(_point(A+t*v,contextptr));
    return remove_not_in_segment(A,B,a.subtype,vecteur(1,M),contextptr);
  }

  // a hyperplan, b hypersphere
  vecteur interplansphere(const gen & a,const gen & b,GIAC_CONTEXT){
    gen cg,r;
    if (!centre_rayon(b,cg,r,false,contextptr)) return vecteur(1,gensizeerr(contextptr));
    if (cg.type!=_VECT || cg._VECTptr->size()!=3)
      return vecteur(1,gensizeerr(contextptr));
    vecteur c(*cg._VECTptr);
    vecteur n,p,res;
    vecteur attributs(1,default_color(contextptr));
    if (!hyperplan_normal_point(a,n,p))
      return vecteur(1,gensizeerr(contextptr));
    gen n2=dotvecteur(n,n),r2=ratnormal(r*r);
    // find x such that c+x*n must belong to a,
    // hence (c-p+x*n).n=0 -> x=(p-c).n/n.n
    gen x=dotvecteur(subvecteur(p,c),n)/n2;
    // if ||x*n||=r, one point c+x*n
    // if ||x*n||>r, empty
    // if ||x*n||<r, circle of radius sqrt(xn2-r2), 
    // centered at c+x*n inside the plan b
    gen xn2=ratnormal(x*x*n2);
    gen center=c+x*n;
    identificateur id(" plansphere");
    gen T__IDNT_e(id);
    if (xn2==r2)
      res.push_back(_point(center,contextptr));
    else {
      if (is_strictly_greater(r2,xn2,contextptr)){
	vecteur v1,v2;
	if (!normal3d(n,v1,v2))
	  return vecteur(1,gensizeerr(contextptr));
	gen v12(dotvecteur(v1,v1));
	gen v22(dotvecteur(v2,v2));
	v12=sqrt((r2-xn2)/v12,contextptr);
	v22=sqrt((r2-xn2)/v22,contextptr);
	res.push_back(plotparam3d(center+cos(T__IDNT_e,contextptr)*v12*v1+sin(T__IDNT_e,contextptr)*v22*v2,
				  makevecteur(T__IDNT_e,u__IDNT_e),
				  gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,gnuplot_zmin,gnuplot_zmax,
				  0,2*M_PI,0,0,false,false,attributs,M_PI/30,0,
				  undef /* FIXME: equation */,makevecteur(T__IDNT_e,u__IDNT_e),contextptr));
      }
    }
    return res;
  }

  static gen inter_solve(const gen & args,GIAC_CONTEXT){
    bool b=all_trig_sol(contextptr);
    all_trig_sol(false,contextptr);
    gen res=_solve(args,contextptr);
    all_trig_sol(b,contextptr);
    return res;
  }

  // a=hypersurface, b=hypersurface
  vecteur inter2hypersurface(const gen & a,const gen &b,GIAC_CONTEXT){ 
    gen & af=a._SYMBptr->feuille;
    gen & bf=b._SYMBptr->feuille;
    if (af.type!=_VECT || bf.type!=_VECT || af._VECTptr->empty() || bf._VECTptr->empty() )
      return vecteur(1,gensizeerr(contextptr));
    vecteur av=*af._VECTptr,bv=*bf._VECTptr; 
    bool aparam=av[0].type==_VECT,bparam=bv[0].type==_VECT;
    if (!aparam && bparam)
      return inter2hypersurface(b,a,contextptr);
    vecteur res;
    identificateur ids(" s"),idt(" t"),idu(" u"),idv(" v");
    gen s(ids),t(idt),u(idu),v(idv);
    if (aparam ){
      av=*av[0]._VECTptr;      
      gen A=subst(av[0],av[1],makevecteur(s,t),false,contextptr);
      if (bparam && bv.size()<3){
	bv=*bv[0]._VECTptr;      
	// av[0]=point on hypersurface a, av[1]=parameters
	// bv[]= same on b
	// Rename parameters so that they do not have the same name for a and b
	gen B=subst(bv[0],bv[1],makevecteur(u,v),false,contextptr);
	if (A.type!=_VECT || A._VECTptr->size()!=3 || B.type!=_VECT || B._VECTptr->size()!=3 )
	  return vecteur(1,gensizeerr(contextptr));
	// we have now to solve A[0]=B[0], A[1]=B[1], A[2]=B[2]
	// that should give us t,u,v as a function of s
	// then we will draw the parametric curve A with respect to s (t replaced)
	vecteur veq(makevecteur(A[0]-B[0],A[1]-B[1],A[2]-B[2]));
	gen sol=inter_solve(gen(makevecteur(veq,makevecteur(t,u,v)),_SEQ__VECT),contextptr);
	if (sol.type!=_VECT)
	  return vecteur(1,gensizeerr(contextptr));
	// for each element of sol, get the first component t, subst in A
	int nsol=sol._VECTptr->size();
	for (int i=0;i<nsol;i++){
	  if (sol[i].type==_VECT && sol[i]._VECTptr->size()==3){
	    gen As=ratnormal(subst(A,t,sol[i]._VECTptr->front(),false,contextptr));
	    // now make the parametric curves [A,s]
	    res.push_back(paramplotparam(gen(makevecteur(As,s),_SEQ__VECT),false,contextptr));
	  }
	}
      } // end both parametric
      else { 
	if (bv.size()<3)
	  return vecteur(1,gensizeerr(contextptr));
	// a parametric, b by equation bv[1] parameters in bv[2]
	// find curve equation with respect to s and t
	gen curveeq=subst(bv[1],bv[2],A,false,contextptr);
	bool swapped=is_zero(derive(curveeq,t,contextptr));
	if (swapped)
	  std::swap(s,t);
	gen sol=inter_solve(gen(makevecteur(symbolic(at_equal,makesequence(curveeq,0)),t),_SEQ__VECT),contextptr);
	if (sol.type!=_VECT)
	  return vecteur(1,gensizeerr(contextptr));
	if (!swapped && sol._VECTptr->empty()){
	  std::swap(s,t);
	  sol=inter_solve(gen(makevecteur(symbolic(at_equal,makesequence(curveeq,0)),t),_SEQ__VECT),contextptr);
	  if (sol.type!=_VECT)
	    return vecteur(1,gensizeerr(contextptr));
	}
	// for each element of sol, get the first component t, subst in A
	int nsol=sol._VECTptr->size();
	for (int i=0;i<nsol;i++){
	  gen As=ratnormal(subst(A,t,sol[i],false,contextptr));
	  gen smin=gnuplot_tmin,smax=gnuplot_tmax;
	  if (av.size()>=4 && av[2].type==_VECT && av[3].type==_VECT){
	    smin=av[2][swapped?1:0];
	    smax=av[3][swapped?1:0];
	  }
	  // now make the parametric curves [A,s]
	  res.push_back(paramplotparam(gen(makevecteur(As,symb_equal(s,symb_interval(smin,smax))),_SEQ__VECT),false,contextptr));
	}
      } // end b by equation
    } // end a parametric
    // both by equation
    return res;
  }

  // a=hypersurface, b=curve
  vecteur interhypersurfacecurve(const gen & a,const gen &b,GIAC_CONTEXT){ 
    gen & af=a._SYMBptr->feuille;
    gen & bf=b._SYMBptr->feuille;
    if (af.type!=_VECT || bf.type!=_VECT || af._VECTptr->empty() || bf._VECTptr->empty() )
      return vecteur(1,gensizeerr(contextptr));
    vecteur av=*af._VECTptr; 
    // av[0]=point on hypersurface, av[1]=parameters
    gen & bf0=bf._VECTptr->front();
    if (bf0.type!=_VECT || bf0._VECTptr->size()<2 || bf0._VECTptr->front().type!=_VECT)
      return vecteur(1,gensizeerr(contextptr));
    vecteur & bv=*bf0._VECTptr; // bv[0]=point on curve, bv[1]=parameter
    if (av.size()==3 && av[1].type!=_VECT && av[2].type==_VECT){
      // Hypersurface with an equation
      // av[1]=equation, av[2]=variables
      vecteur & vars=*av[2]._VECTptr;
      gen eq(subst(av[1],vars,*bv[0]._VECTptr,false,contextptr));
      vecteur sol;
#ifndef NO_STDEXCEPT
      try { 
#endif
	sol=solve(eq,bv[1],0,contextptr);
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error & ){
      }
#endif
      vecteur res;
      iterateur it=sol.begin(),itend=sol.end();
      for (;it!=itend;++it){
	res.push_back(_point(subst(bv[0],bv[1],*it,false,contextptr),contextptr));
      }
      return res;
    }
    // Hypersurface without equation (parametrized only)
    if (av.size()<2 || av[1].type!=_VECT)
      return vecteur(1,gensizeerr(contextptr));
    gen eq(av[0]-bv[0]);
    vecteur vars(*av[1]._VECTptr);
    vars.push_back(bv[1]);
    vecteur sol;
#ifndef NO_STDEXCEPT
    try {
#endif
      sol=solve(eq,vars,0,contextptr);
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & ){
      return vecteur(1,gensizeerr(contextptr));
    }
#endif
    vecteur res;
    iterateur it=sol.begin(),itend=sol.end();
    for (;it!=itend;++it){
      res.push_back(_point(subst(bv[0],bv[1],it->_VECTptr->back(),false,contextptr),contextptr));
    }
    return res;
  }

  gen hyperplan2hypersurface(const gen & g){
    if (!g.is_symb_of_sommet(at_hyperplan))
      return gensizeerr(gettext("hyperplan2hypersurface"));
    vecteur n,P;
    if (!hyperplan_normal_point(g,n,P))
      return gensizeerr(gettext("hyperplan2hypersurface"));
    if (n.size()!=3)
      return gendimerr(gettext("hyperplan2hypersurface"));
    vecteur xyz(makevecteur(x__IDNT_e,y__IDNT_e,z__IDNT_e));
    gen eq=dotvecteur(subvecteur(xyz,P),n);
    vecteur v1,v2;
    if (!normal3d(n,v1,v2))
      return gensizeerr(gettext("hyperplan2hypersurface"));
    vecteur parameq(makevecteur(addvecteur(P,addvecteur(multvecteur(u__IDNT,v1),multvecteur(v__IDNT,v2))),makevecteur(u__IDNT,v__IDNT)));
    return hypersurface(parameq,eq,xyz);
  }

  gen hypersphere2hypersurface(const gen & g){
    if (!g.is_symb_of_sommet(at_hypersphere))
      return gensizeerr(gettext("hypersphere2hypersurface"));
    vecteur xyz(makevecteur(x__IDNT_e,y__IDNT_e,z__IDNT_e));
    vecteur uv(makevecteur(u__IDNT_e,v__IDNT_e));
    return hypersurface(hypersphere_parameq(g,uv),hypersphere_equation(g,xyz),xyz);
  }

  // Currently works only if v is made of lines
  // For each line, get the intersections of the polygone ABCD with the line
  // If there are 2 intersections return a segment, else return a point or void
  vecteur remove_face(const vecteur & face,const vecteur & v,GIAC_CONTEXT){
    vecteur ABCD=face;
    if (ABCD.size()<3)
      return vecteur(1,gendimerr(contextptr));
    if (ABCD.back()!=ABCD.front())
      ABCD.push_back(ABCD.front());
    vecteur res;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      gen tmp=remove_at_pnt(*it);
      if (tmp.type!=_VECT || tmp._VECTptr->size()!=2)
	res.push_back(*it); // FIXME, arc of circles etc.
      vecteur v(interpolygone(ABCD,*it,contextptr));
      if (is_undef(v))
	return v;
      int s=v.size();
      if (!s)
	continue;
      if (s==1)
	res.push_back(v.front());
      if (s==2)
	res.push_back(symb_pnt(gen(makevecteur(remove_at_pnt(v.front()),remove_at_pnt(v.back())),_GROUP__VECT),contextptr));
    }
    return res;
  }

  static vecteur segments2polygone(const vecteur & v,GIAC_CONTEXT){
    vecteur polylines,other;
    int s=v.size();
    for (int i=0;i<s;++i){
      gen g=remove_at_pnt(v[i]);
      if (g.type!=_VECT || g._VECTptr->size()!=2)
	other.push_back(g);
      gen a=g._VECTptr->front(),b=g._VECTptr->back();
      int ps=polylines.size(),j=0;
      for (int j=0;j<ps;++j){
	if (polylines[j].type==_VECT && !polylines[j]._VECTptr->empty()){
	  vecteur w=*polylines[j]._VECTptr;
	  if (a==w.back()){
	    w.push_back(b);
	    polylines[j]=gen(w,_GROUP__VECT);
	    break;
	  }
	  if (b==w.back()){
	    w.push_back(a);
	    polylines[j]=gen(w,_GROUP__VECT);
	    break;
	  }
	  if (a==w.front()){
	    w.insert(w.begin(),b);
	    polylines[j]=gen(w,_GROUP__VECT);
	    break;
	  }
	  if (b==w.front()){
	    w.insert(w.begin(),a);
	    polylines[j]=gen(w,_GROUP__VECT);
	    break;
	  }
	}
      }
      if (j==ps)
	polylines.push_back(g);
    }
    s=polylines.size();
    for (int i=0;i<s;++i){
      other.push_back(symb_pnt(polylines[i],contextptr));
    }
    return other;
  }

  // Intersection polyedre with something
  // Find intersection of each face with something
  // For each face, find intersection of hyperplan with something
  vecteur interpolyedre(const vecteur & p,const gen & bb,GIAC_CONTEXT){
    vecteur res;
    const_iterateur it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      if (it->type!=_VECT)
	continue;
      vecteur v=*it->_VECTptr;
      int s=v.size();
      if (s<3)
	continue;
      gen AB(v[1]-v[0]),AC(v[2]-v[0]);
      if (AB.type!=_VECT || AB._VECTptr->size()!=3 || AC.type!=_VECT || AC._VECTptr->size()!=3)
	continue;
      vecteur n=cross(*AB._VECTptr,*AC._VECTptr,contextptr);
      gen tmp=symbolic(at_hyperplan,makesequence(n,v[0]));
      bool b=show_point(contextptr);
      show_point(false,contextptr);
      vecteur w(inter(tmp,bb,contextptr));
      show_point(b,contextptr);
      vecteur restmp=remove_face(*it->_VECTptr,w,contextptr);
      if (is_undef(restmp))
	return restmp;
      res=mergevecteur(res,restmp);
    }
    return segments2polygone(res,contextptr);
  }

  vecteur interhyperplan(const gen & p1,const gen & p2,GIAC_CONTEXT){
    vecteur P1,n1,P2,n2;
    if (!hyperplan_normal_point(p1,n1,P1) || !hyperplan_normal_point(p2,n2,P2))
      return vecteur(1,gensizeerr(contextptr));
    vecteur n=cross(n1,n2,contextptr); // direction of intersection
    vecteur n3=cross(n,n1,contextptr); // perpendicular to n1 hence in P1 and not // inter
    // Find a point on intersection: P1+t n3 -P2 perpendicular to n2
    // hence (P2-P1).n2=t n3.n2
    gen P=do_point3d(P1-scalar_product(P1-P2,n2,contextptr)/dotvecteur(n3,n2)*n3);
    gen Q=do_point3d(P+n);
    return makevecteur(symb_pnt(gen(makevecteur(P,Q),_LINE__VECT),contextptr));
  }


  // equation f -> geometric object g
  static bool equation2geo3d(const gen & f0,const gen & x,const gen & y,const gen & z,gen & g,double umin,double umax,double ustep,double vmin,double vmax,double vstep,bool numeric,const context * contextptr){
    gen f=_fxnd(remove_equal(f0),contextptr)._VECTptr->front();
    gen fx(derive(f,x,contextptr)),fy(derive(f,y,contextptr)),fz(derive(f,z,contextptr));
    bool fx0=is_zero(fx),fy0=is_zero(fy),fz0=is_zero(fz);
    if (fx0 && fy0 && fz0)
      return false;
    gen fxx(derive(fx,x,contextptr)),fxy(derive(fx,y,contextptr)),fyy(derive(fy,y,contextptr)),fxz(derive(fx,z,contextptr)),fyz(derive(fy,z,contextptr)),fzz(derive(fz,z,contextptr));
    if (is_undef(fx)||is_undef(fy) || is_undef(fz) || is_undef(fxx) || is_undef(fxy) || is_undef(fxz) || is_undef(fyy) || is_undef(fyz) || is_undef(fzz))
      return false;
    if ( is_zero(derive(fxx,x,contextptr)) && is_zero(derive(fxy,x,contextptr)) && is_zero(derive(fyy,x,contextptr)) && is_zero(derive(fxz,x,contextptr)) && is_zero(derive(fyz,x,contextptr)) && is_zero(derive(fzz,x,contextptr)) && 
	 is_zero(derive(fxx,y,contextptr)) && is_zero(derive(fxy,y,contextptr)) && is_zero(derive(fyy,y,contextptr)) && is_zero(derive(fxz,y,contextptr)) && is_zero(derive(fyz,y,contextptr)) && is_zero(derive(fzz,y,contextptr)) &&
	 is_zero(derive(fxx,z,contextptr)) && is_zero(derive(fxy,z,contextptr)) && is_zero(derive(fyy,z,contextptr)) && is_zero(derive(fxz,z,contextptr)) && is_zero(derive(fyz,z,contextptr)) && is_zero(derive(fzz,z,contextptr)) 
	 ){
      vecteur vxyz(makevecteur(x,y,z)),v0(3,0);
      gen c=ratnormal(subst(f,vxyz,v0,false,contextptr));
      fxx=ratnormal(fxx); fyy=ratnormal(fyy); fxy=ratnormal(fxy);
      fxz=ratnormal(fxz); fyz=ratnormal(fyz); fzz=ratnormal(fzz);
      if (is_zero(fxy) && is_zero(fxz) && is_zero(fyz)){
	if (is_zero(fxx) && is_zero(fyy) && is_zero(fzz)){
	  gen d=gcd(gcd(fx,fy),fz);
	  fx=normal(fx/d,contextptr); fy=normal(fy/d,contextptr); fz=normal(fz/d,contextptr); c=normal(c/d,contextptr);
	  vecteur n(makevecteur(fx,fy,fz));
	  // plan
	  if (!fx0){
	    gen tmp=makevecteur(ratnormal(-c/fx),0,0);
	    g=symbolic(at_hyperplan,makesequence(n,tmp));
	  }
	  else {
	    if (!fy0){
	      gen tmp=makevecteur(0,ratnormal(-c/fy),0);
	      g=symbolic(at_hyperplan,makesequence(n,tmp));
	    }
	    else {
	      gen tmp=makevecteur(0,0,ratnormal(-c/fz));
	      g=symbolic(at_hyperplan,makesequence(n,tmp));
	    }
	  }
	  return true;
	}
	// may check for a sphere (fxx=fyy=fzz)
      }
      // conique
      gen x0,y0,z0,equation_reduite;
      vecteur V0,V1,V2,param_curves,propre,centre;
      quadrique_reduite(f,vxyz,x0,y0,z0,V0,V1,V2,propre,equation_reduite,param_curves,centre,numeric,contextptr);
      vecteur res;
      int n=param_curves.size();
      for (int i=0;i<n;++i){
	gen & obj=param_curves[i];
	if (obj.type==_VECT){
	  vecteur & objv=*obj._VECTptr;
	  int s=objv.size();
	  if (s==2)
	    res.push_back(obj);
	  if (s==5){
	    gen tmp=paramplotparam(gen(objv,_SEQ__VECT),false,contextptr);
	    tmp=remove_at_pnt(tmp);
	    if (tmp.is_symb_of_sommet(at_hypersurface) && tmp._SYMBptr->feuille.type==_VECT){
	      vecteur tmpv=*tmp._SYMBptr->feuille._VECTptr;
	      if (tmpv.size()==3){
		tmpv[1]=f;
		tmpv[2]=vxyz;
		tmp=symbolic(at_hypersurface,gen(tmpv,_SEQ__VECT));
	      }
	    }
	    res.push_back(tmp);
	  }
	}
	else
	  res.push_back(obj);
      }
      g= (res.size()==1)? res.front() : res; // gen(res,_SEQ__VECT);
      return true;
    }
    return false;
  }

#if !defined(RTOS_THREADX) && !defined(EMCC)
  // 3-d implicit surface using the marching cube algorithm
    /* Adapted from http://astronomy.swin.edu.au/~pbourke/modelling/ 
       by Paul Bourke
       Given a grid cell and an isolevel, calculate the triangular
       facets required to represent the isosurface through the cell.
       Return the number of triangular facets, the array "triangles"
       will be loaded up with the vertices at most 5 triangular facets.
       0 will be returned if the grid cell is either totally above
       of totally below the isolevel.
    */
  int const edgeTable[256]={
    0x0  , 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c,
    0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03, 0xe09, 0xf00,
    0x190, 0x99 , 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c,
    0x99c, 0x895, 0xb9f, 0xa96, 0xd9a, 0xc93, 0xf99, 0xe90,
    0x230, 0x339, 0x33 , 0x13a, 0x636, 0x73f, 0x435, 0x53c,
    0xa3c, 0xb35, 0x83f, 0x936, 0xe3a, 0xf33, 0xc39, 0xd30,
    0x3a0, 0x2a9, 0x1a3, 0xaa , 0x7a6, 0x6af, 0x5a5, 0x4ac,
    0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0,
    0x460, 0x569, 0x663, 0x76a, 0x66 , 0x16f, 0x265, 0x36c,
    0xc6c, 0xd65, 0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60,
    0x5f0, 0x4f9, 0x7f3, 0x6fa, 0x1f6, 0xff , 0x3f5, 0x2fc,
    0xdfc, 0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3, 0xbf9, 0xaf0,
    0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x55 , 0x15c,
    0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950,
    0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5, 0xcc ,
    0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0,
    0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf, 0xec5, 0xfcc,
    0xcc , 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0,
    0x950, 0x859, 0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c,
    0x15c, 0x55 , 0x35f, 0x256, 0x55a, 0x453, 0x759, 0x650,
    0xaf0, 0xbf9, 0x8f3, 0x9fa, 0xef6, 0xfff, 0xcf5, 0xdfc,
    0x2fc, 0x3f5, 0xff , 0x1f6, 0x6fa, 0x7f3, 0x4f9, 0x5f0,
    0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c,
    0x36c, 0x265, 0x16f, 0x66 , 0x76a, 0x663, 0x569, 0x460,
    0xca0, 0xda9, 0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac,
    0x4ac, 0x5a5, 0x6af, 0x7a6, 0xaa , 0x1a3, 0x2a9, 0x3a0,
    0xd30, 0xc39, 0xf33, 0xe3a, 0x936, 0x83f, 0xb35, 0xa3c,
    0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x33 , 0x339, 0x230,
    0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c,
    0x69c, 0x795, 0x49f, 0x596, 0x29a, 0x393, 0x99 , 0x190,
    0xf00, 0xe09, 0xd03, 0xc0a, 0xb06, 0xa0f, 0x905, 0x80c,
    0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203, 0x109, 0x0   };
  int const triTable[256][16] =
    {{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1},
     {3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1},
     {3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1},
     {3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1},
     {9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1},
     {1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1},
     {9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
     {2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1},
     {8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1},
     {9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
     {4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1},
     {3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1},
     {1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1},
     {4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1},
     {4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1},
     {9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1},
     {1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
     {5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1},
     {2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1},
     {9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
     {0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
     {2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1},
     {10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1},
     {4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1},
     {5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1},
     {5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1},
     {9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1},
     {0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1},
     {1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1},
     {10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1},
     {8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1},
     {2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1},
     {7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1},
     {9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1},
     {2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1},
     {11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1},
     {9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1},
     {5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1},
     {11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1},
     {11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
     {1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1},
     {9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1},
     {5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1},
     {2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
     {0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
     {5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1},
     {6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1},
     {0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1},
     {3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1},
     {6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1},
     {5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1},
     {1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
     {10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1},
     {6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1},
     {1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1},
     {8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1},
     {7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1},
     {3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
     {5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1},
     {0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1},
     {9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1},
     {8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1},
     {5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1},
     {0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1},
     {6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1},
     {10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1},
     {10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1},
     {8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1},
     {1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1},
     {3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1},
     {0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1},
     {10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1},
     {0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1},
     {3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1},
     {6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1},
     {9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1},
     {8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1},
     {3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1},
     {6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1},
     {0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1},
     {10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1},
     {10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1},
     {1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1},
     {2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1},
     {7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1},
     {7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1},
     {2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1},
     {1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1},
     {11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1},
     {8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1},
     {0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1},
     {7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
     {10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
     {2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
     {6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1},
     {7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1},
     {2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1},
     {1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1},
     {10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1},
     {10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1},
     {0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1},
     {7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1},
     {6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1},
     {8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1},
     {9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1},
     {6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1},
     {1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1},
     {4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1},
     {10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1},
     {8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1},
     {0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1},
     {1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1},
     {8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1},
     {10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1},
     {4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1},
     {10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
     {5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
     {11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1},
     {9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
     {6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1},
     {7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1},
     {3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1},
     {7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1},
     {9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1},
     {3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1},
     {6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1},
     {9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1},
     {1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1},
     {4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1},
     {7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1},
     {6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1},
     {3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1},
     {0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1},
     {6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1},
     {1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1},
     {0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1},
     {11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1},
     {6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1},
     {5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1},
     {9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1},
     {1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1},
     {1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1},
     {10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1},
     {0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1},
     {5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1},
     {10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1},
     {11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1},
     {0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1},
     {9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1},
     {7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1},
     {2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1},
     {8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1},
     {9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1},
     {9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1},
     {1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1},
     {9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1},
     {9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1},
     {5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1},
     {0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1},
     {10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1},
     {2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1},
     {0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1},
     {0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1},
     {9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1},
     {5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1},
     {3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1},
     {5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1},
     {8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1},
     {0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1},
     {9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1},
     {0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1},
     {1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1},
     {3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1},
     {4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1},
     {9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1},
     {11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1},
     {11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1},
     {2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1},
     {9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1},
     {3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1},
     {1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1},
     {4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1},
     {4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1},
     {0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1},
     {3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1},
     {3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1},
     {0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1},
     {9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1},
     {1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
     {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}};

  struct GRIDCELL {
    vecteur p[8];
    double val[8];
  } ;

  static vecteur VertexInterp(double isolevel,const vecteur & p1,const vecteur & p2,double valp1,double valp2){
    double mu;

    if (std::abs(isolevel-valp1) < 0.00001)
      return(p1);
    if (std::abs(isolevel-valp2) < 0.00001)
      return(p2);
    if (std::abs(valp1-valp2) < 0.00001)
      return(p1);
    mu = (isolevel - valp1) / (valp2 - valp1);
    
    return addvecteur(p1,multvecteur(mu,subvecteur(p2,p1)));
  }

  static gen in_plotimplicit(const gen& f_orig,const gen&x,const gen & y,const gen & z,double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,int nxstep,int nystep,int nzstep,double eps,const vecteur & attributs,const context * contextptr){
    if (f_orig.is_symb_of_sommet(at_inv) || (is_zero(derive(f_orig,x,contextptr)) && is_zero(derive(f_orig,y,contextptr))) )
      return vecteur(0); // gen(vecteur(0),_SEQ__VECT);
    if (f_orig.is_symb_of_sommet(at_prod) && f_orig._SYMBptr->feuille.type==_VECT){
      vecteur res;
      vecteur & fv = *f_orig._SYMBptr->feuille._VECTptr;
      int s=fv.size();
      for (int i=0;i<s;++i){
	gen tmp=in_plotimplicit(fv[i],x,y,z,xmin,xmax,ymin,ymax,zmin,zmax,
				nxstep,nystep,nzstep,eps,attributs,contextptr);
	res=mergevecteur(res,gen2vecteur(tmp));
      }
      return res; // gen(res,_SEQ__VECT);
    }
    if (f_orig.is_symb_of_sommet(at_pow)){
      gen farg=f_orig._SYMBptr->feuille;
      if (farg.type==_VECT && farg._VECTptr->size()==2){
	gen arg=farg._VECTptr->front();
	gen expo=farg._VECTptr->back();
	if (ck_is_positive(expo,contextptr))
	  return in_plotimplicit(farg,x,y,z,xmin,xmax,ymin,ymax,zmin,zmax,nxstep,nystep,nzstep,eps,attributs,contextptr);
	else
	  return vecteur(0); // gen(vecteur(0),_SEQ__VECT);
      }
    }
    gen attribut=attributs.empty()?default_color(contextptr):attributs[0];
    gen lieu_geo;
    if (equation2geo3d(f_orig,x,y,z,lieu_geo,gnuplot_tmin,gnuplot_tmax,gnuplot_tstep,gnuplot_tmin,gnuplot_tmax,gnuplot_tstep,true,contextptr))
      return put_attributs(lieu_geo,attributs,contextptr);
    //return undef;
    double xstep=(xmax-xmin)/nxstep;
    double ystep=(ymax-ymin)/nystep;
    double zstep=(zmax-zmin)/nzstep;
    identificateur xloc(" xloc"),yloc(" yloc"),zloc(" zloc");
    vecteur xyz(makevecteur(x,y,z)),locvar(makevecteur(xloc,yloc,zloc));
    gen f=quotesubst(f_orig,xyz,locvar,contextptr).evalf(1,contextptr);
    vecteur localvar(makevecteur(xloc,yloc,zloc));
    context * newcontextptr=(context *) contextptr;
    int protect=bind(makevecteur(xmin,ymin,zmin),localvar,newcontextptr);
    vector< vector< vector<double> > > fxyz(nxstep+1,vector< vector<double> >(nystep+1,vector<double>(nzstep+1)));
    gen gtmp;
    // initialize each cell value of f
    local_sto_double(xmin,xloc,newcontextptr);
    // xloc.localvalue->back()._DOUBLE_val = xmin;
    for (int i=0;i<=nxstep;++i){
      local_sto_double(ymin,yloc,newcontextptr);
      // yloc.localvalue->back()._DOUBLE_val = ymin ;
      for (int j=0;j<=nystep;++j){
	local_sto_double(zmin,zloc,newcontextptr);
	// zloc.localvalue->back()._DOUBLE_val = zmin ;
	for (int k=0;k<=nzstep;++k){
	  gtmp=f.evalf_double(eval_level(contextptr),newcontextptr);
	  if (gtmp.type==_DOUBLE_)
	    fxyz[i][j][k]=gtmp._DOUBLE_val==0?1e-200:gtmp._DOUBLE_val;
	  else
	    fxyz[i][j][k]=0;
	  local_sto_double_increment(zstep,zloc,newcontextptr);
	  // zloc.localvalue->back()._DOUBLE_val += zstep;
	}
	local_sto_double_increment(ystep,yloc,newcontextptr);
	// yloc.localvalue->back()._DOUBLE_val += ystep;
      }
      local_sto_double_increment(xstep,xloc,newcontextptr);
      // xloc.localvalue->back()._DOUBLE_val += xstep;
    }
    leave(protect,localvar,newcontextptr);

    GRIDCELL grid;
    vecteur triangle(4),triangulation;
    double xcur=xmin,ycur,zcur;
    for (int i=0;i<nxstep;++i,xcur+=xstep){
      ycur=ymin;
      for (int j=0;j<nystep;++j,ycur+=ystep){
	zcur=zmin;
	for (int k=0;k<nzstep;++k,zcur+=zstep){
	  // Set current gridcell
	  grid.val[0]=fxyz[i][j][k];
	  grid.p[0]=makevecteur(xcur,ycur,zcur);
	  grid.val[1]=fxyz[i+1][j][k];
	  grid.p[1]=makevecteur(xcur+xstep,ycur,zcur);
	  grid.val[2]=fxyz[i+1][j][k+1];
	  grid.p[2]=makevecteur(xcur+xstep,ycur,zcur+zstep);
	  grid.val[3]=fxyz[i][j][k+1];
	  grid.p[3]=makevecteur(xcur,ycur,zcur+zstep);
	  grid.val[4]=fxyz[i][j+1][k];
	  grid.p[4]=makevecteur(xcur,ycur+ystep,zcur);
	  grid.val[5]=fxyz[i+1][j+1][k];
	  grid.p[5]=makevecteur(xcur+xstep,ycur+ystep,zcur);
	  grid.val[6]=fxyz[i+1][j+1][k+1];
	  grid.p[6]=makevecteur(xcur+xstep,ycur+ystep,zcur+zstep);
	  grid.val[7]=fxyz[i][j+1][k+1];
	  grid.p[7]=makevecteur(xcur,ycur+ystep,zcur+zstep);
	  
	  /*
	    Determine the index into the edge table which
	    tells us which vertices are inside of the surface
	  */
	  int cubeindex=0;
	  if (grid.val[0] < 0) cubeindex |= 1;
	  if (grid.val[1] < 0) cubeindex |= 2;
	  if (grid.val[2] < 0) cubeindex |= 4;
	  if (grid.val[3] < 0) cubeindex |= 8;
	  if (grid.val[4] < 0) cubeindex |= 16;
	  if (grid.val[5] < 0) cubeindex |= 32;
	  if (grid.val[6] < 0) cubeindex |= 64;
	  if (grid.val[7] < 0) cubeindex |= 128;
	  
	  /* Cube is entirely in/out of the surface */
	  if (edgeTable[cubeindex] == 0)
	    continue;
	  
	  vecteur vertlist[12];
	  /* Find the vertices where the surface intersects the cube */
	  if (edgeTable[cubeindex] & 1)
	    vertlist[0] =
	      VertexInterp(0,grid.p[0],grid.p[1],grid.val[0],grid.val[1]);
	  if (edgeTable[cubeindex] & 2)
	    vertlist[1] =
	      VertexInterp(0,grid.p[1],grid.p[2],grid.val[1],grid.val[2]);
	  if (edgeTable[cubeindex] & 4)
	    vertlist[2] =
	      VertexInterp(0,grid.p[2],grid.p[3],grid.val[2],grid.val[3]);
	  if (edgeTable[cubeindex] & 8)
	    vertlist[3] =
	      VertexInterp(0,grid.p[3],grid.p[0],grid.val[3],grid.val[0]);
	  if (edgeTable[cubeindex] & 16)
	    vertlist[4] =
	      VertexInterp(0,grid.p[4],grid.p[5],grid.val[4],grid.val[5]);
	  if (edgeTable[cubeindex] & 32)
	    vertlist[5] =
	      VertexInterp(0,grid.p[5],grid.p[6],grid.val[5],grid.val[6]);
	  if (edgeTable[cubeindex] & 64)
	    vertlist[6] =
	      VertexInterp(0,grid.p[6],grid.p[7],grid.val[6],grid.val[7]);
	  if (edgeTable[cubeindex] & 128)
	    vertlist[7] =
	      VertexInterp(0,grid.p[7],grid.p[4],grid.val[7],grid.val[4]);
	  if (edgeTable[cubeindex] & 256)
	    vertlist[8] =
	      VertexInterp(0,grid.p[0],grid.p[4],grid.val[0],grid.val[4]);
	  if (edgeTable[cubeindex] & 512)
	    vertlist[9] =
	      VertexInterp(0,grid.p[1],grid.p[5],grid.val[1],grid.val[5]);
	  if (edgeTable[cubeindex] & 1024)
	    vertlist[10] =
	      VertexInterp(0,grid.p[2],grid.p[6],grid.val[2],grid.val[6]);
	  if (edgeTable[cubeindex] & 2048)
	    vertlist[11] =
	      VertexInterp(0,grid.p[3],grid.p[7],grid.val[3],grid.val[7]);
	  
	  /* Create the triangles */
	  
	  for (int i=0;triTable[cubeindex][i]!=-1;i+=3) {
	    triangle[0]=gen(vertlist[triTable[cubeindex][i]],_POINT__VECT);
	    triangle[1]=gen(vertlist[triTable[cubeindex][i+1]],_POINT__VECT);
	    triangle[2]=gen(vertlist[triTable[cubeindex][i+2]],_POINT__VECT);
	    triangle[3]=triangle[0];
	    triangulation.push_back(gen(triangle,_GROUP__VECT));
	  }

	} // end for k
      } // end for j
    } // end for i

    // create hypersurface
    gen tmp=gen(makevecteur(undef,makevecteur(x,y,z),makevecteur(xmin,ymin,zmin),makevecteur(xmax,ymax,zmax),gen(triangulation,_POLYEDRE__VECT)),_PNT__VECT);
    gen r=symb_pnt(hypersurface(tmp,f,makevecteur(x,y,z)),attribut,contextptr);    
    return r;
  }
#else //RTOS_THREADX
  static gen in_plotimplicit(const gen& f_orig,const gen&x,const gen & y,const gen & z,double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,int nxstep,int nystep,int nzstep,double eps,const vecteur & attributs,const context * contextptr){
    return undef;
  }
#endif

  gen plotimplicit(const gen& f_orig,const gen&x,const gen & y,const gen & z,double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,int nxstep,int nystep,int nzstep,double eps,const vecteur & attributs,bool unfactored,const context * contextptr){
    if ( x.type!=_IDNT || y.type!=_IDNT || z.type!=_IDNT )
      return gensizeerr(gettext("Variables must be free"));
    if (!nystep || !nzstep){
      nxstep=int(std::sqrt(double(std::abs(nxstep))));
      nystep=nxstep;
      nzstep=nxstep;
    }
    gen ff(unfactored?f_orig:factor(f_orig,false,contextptr));
    return in_plotimplicit(ff,x,y,z,xmin,xmax,ymin,ymax,zmin,zmax,nxstep,nystep,nzstep,eps,attributs,contextptr);
  }

  // FIXME Move to plot.cc
  bool is3d(const gen & g){
    if (g.type==_VECT)
      return !g._VECTptr->empty() && is3d(g._VECTptr->back());
    if (g.is_symb_of_sommet(at_animation))
      return is3d(g._SYMBptr->feuille);
    if (!g.is_symb_of_sommet(at_pnt))
      return false;
    gen f =g._SYMBptr->feuille;
    if (f.type!=_VECT || f._VECTptr->empty())
      return false;
    f=f._VECTptr->front();
    if (f.type==_VECT){
      if (f.subtype==_POLYEDRE__VECT || f.subtype==_POINT__VECT)
	return true;
      if (f._VECTptr->size()==3 && f.subtype!=_GROUP__VECT && f.subtype!=_LINE__VECT && f.subtype!=_HALFLINE__VECT){
	vecteur & v =*f._VECTptr;
	return v[0].type!=_CPLX && v[1].type!=_CPLX && v[2].type!=_CPLX;
      }
      if (!f._VECTptr->empty())
	return check3dpoint(f._VECTptr->back());
    }
    if (f.type!=_SYMB)
      return false;
    if (f._SYMBptr->sommet==at_hyperplan || f._SYMBptr->sommet==at_hypersphere || f._SYMBptr->sommet == at_hypersurface)
      return true;
    if (f._SYMBptr->sommet==at_curve && f._SYMBptr->feuille.type==_VECT && !f._SYMBptr->feuille._VECTptr->empty()){
      // is it a 3-d curve?
      f = f._SYMBptr->feuille._VECTptr->front();
      // f = vect[ pnt,var,xmin,xmax ]
      if (f.type==_VECT && !f._VECTptr->empty())
	return check3dpoint(f._VECTptr->front());
    }
    return false;
  }

  gen _quadrique(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return _quadrique(gen(vecteur(1,args),_SEQ__VECT),contextptr);
    vecteur attributs(1,default_color(contextptr));
    vecteur & v =*args._VECTptr;
    int s=read_attributs(v,attributs,contextptr);
    if (!s) return gendimerr(contextptr);
    bool numeric=true;
    if (v[s-1]==at_exact){
      numeric=false;
      --s;
      if (!s) return gendimerr(contextptr);
    }
    gen g;
    if (s==1){
      if (equation2geo3d(v[0],x__IDNT_e,y__IDNT_e,z__IDNT_e,g,gnuplot_tmin,gnuplot_tmax,gnuplot_tstep,gnuplot_tmin,gnuplot_tmax,gnuplot_tstep,numeric,contextptr))
	return put_attributs(g,attributs,contextptr);
    }
    if (s<=5)
      return _plotimplicit(args,contextptr);
    if (s==9){ // defined by 9 points
      vecteur w(9),m(9),ligne(10);
      gen wx,wy,wz;
      // find m such that m*[a,b,c,d,e,f,g,h,i,j]=0 where 
      // a*x^2+b*x*y+c*y^2+d*x+e*y+f+g*z^2+h*z*x+i*z*y+j*z=0
      for (int i=0;i<9;++i){
	w[i]=remove_at_pnt(v[i]);
	if (w[i].type!=_VECT || w[i]._VECTptr->size()!=3)
	  return gensizeerr(contextptr);
	vecteur & wv = *w[i]._VECTptr;
	wx=wv[0];
	wy=wv[1];
	wz=wv[2];
	ligne[0]=wx*wx;
	ligne[1]=wx*wy;
	ligne[2]=wy*wy;
	ligne[3]=wx;
	ligne[4]=wy;
	ligne[5]=1;
	ligne[6]=wz*wz;
	ligne[7]=wz*wx;
	ligne[8]=wz*wy;
	ligne[9]=wz;
	m[i]=ligne;
      }
      // find ker(m)
      gen me=m; // exact(m,contextptr);
      vecteur base;
      if (me.type==_VECT)
	base=mker(*me._VECTptr,contextptr);
      if (is_undef(base) || base.empty() || base.front().type!=_VECT || base.front()._VECTptr->size()!=6)
	return gensizeerr(gettext("Bug in quadrique reducing ")+gen(m).print(contextptr));
      vecteur & res = *base.front()._VECTptr;
      identificateur x(" x"),y(" y"),z( "z");
      gen eq=res[0]*x*x+res[1]*x*y+res[2]*y*y+res[3]*x+res[4]*y+res[5]+res[6]*z*z+res[7]*z*x+res[8]*z*y+res[9]*z;
      if (equation2geo3d(eq,x,y,z,g,gnuplot_tmin,gnuplot_tmax,gnuplot_tstep,gnuplot_tmin,gnuplot_tmax,gnuplot_tstep,numeric,contextptr))
	return put_attributs(g,attributs,contextptr);
      else
	return gensizeerr(gettext("Bug in quadrique, equation ")+eq.print(contextptr));	
    }
    return gendimerr(contextptr);
  }
  static const char _quadrique_s []="quadric";
  static define_unary_function_eval (__quadrique,&giac::_quadrique,_quadrique_s);
  define_unary_function_ptr5( at_quadrique ,alias_at_quadrique,&__quadrique,0,true);

  bool est_cospherique(const gen & a,const gen & b,const gen & c,const gen & d,const gen & f,GIAC_CONTEXT){
    gen ab=b-a,ac=c-a,ad=d-a,af=f-a;
    if (is_zero(ab) || is_zero(ac) || is_zero(ad) || is_zero(af))
      return true;
    return est_coplanaire(a+ab/abs_norm2(ab,contextptr),a+ac/abs_norm2(ac,contextptr),a+ad/abs_norm2(ad,contextptr),a+af/abs_norm2(af,contextptr),contextptr);
  }
  gen _est_cospherique(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr ;
    int s=v.size();
    gen a(v[0]),b(undef),c(undef),d(undef);
    for (int i=1;i<s;++i){
      if (is_undef(b)){
	if (!is_zero(v[0]-v[i]))
	  b=v[i];
      }
      else {
	if (est_aligne(a,b,v[i],contextptr))
	  return 0;
	if (is_undef(c))
	  c=v[i];
	else {
	  if (est_cocyclique(a,b,c,v[i],contextptr))
	    continue;
	  if (is_undef(d))
	    d=v[i];
	  else {
	    if (!est_cospherique(a,b,c,d,v[i],contextptr))
	      return 0;
	  }
	}
      }
    }
    return 1;
  }
  static const char _est_cospherique_s []="is_cospherical";
  static define_unary_function_eval (__est_cospherique,&giac::_est_cospherique,_est_cospherique_s);
  define_unary_function_ptr5( at_est_cospherique ,alias_at_est_cospherique,&__est_cospherique,0,true);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
