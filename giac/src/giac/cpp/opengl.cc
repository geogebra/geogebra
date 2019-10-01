// -*- mode:C++ ; compile-command: "emcc opengl.cc -I. -I.. -DHAVE_CONFIG_H -DIN_GIAC -DGIAC_GENERIC_CONSTANTS -DNO_STDEXCEPT -Os -s ALLOW_MEMORY_GROWTH=1 -s LEGACY_GL_EMULATION=1" -*-
#ifndef GIAC_GGB

#include "opengl.h"
/*
 *  Copyright (C) 2006,2014 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#ifdef EMCC_GLUT
#include <GL/glut.h>
#else
#include "SDL/SDL.h"
#include "SDL/SDL_opengl.h"
#include <emscripten.h>
//#include <html5.h>
#endif

#include <fstream>
#include "vector.h"
#include <algorithm>
#include <fcntl.h>
#include <cmath>
#include <time.h> // for nanosleep
#include <stdio.h>
#include <dirent.h>
#include <sys/stat.h> // auto-recovery function
#include "path.h"
#ifndef IN_GIAC
#include <giac/misc.h>
#else
#include "misc.h"
#endif

#ifndef HAVE_PNG_H
#undef HAVE_LIBPNG
#endif
#ifdef HAVE_LIBPNG
#include <png.h>
#endif

using namespace std;
using namespace giac;

#ifdef EMCC
// missing from emscripten
void glPointSize(GLint){ }
void glColorMaterial(GLenum,GLint){}
void glVertex3d(GLdouble d1,GLdouble d2,GLdouble d3){ glVertex3f(d1,d2,d3); }
void glLightModeli(GLenum,GLint) {}
void glLightModelf(GLenum,GLfloat) {}
void glGetDoublev(GLenum i,GLdouble * d){
  float f[16]; glGetFloatv(i,f);
  for (int i=0;i<16;++i) d[i]=f[i];
}
// glRasterPos3d(GLdouble d1,GLdouble d2,GLdouble d3) is not defined
// so we define it as a member of Opengl
void glGetMaterialfv(GLenum,GLenum,GLfloat *){}
void glLineStipple(GLint,GLushort){}
void glMaterialf(GLenum,GLenum,GLfloat){}
void glNormal3d(GLdouble d1,GLdouble d2,GLdouble d3){ glNormal3f(d1,d2,d3); }
void glGetLightfv(GLenum,GLenum,GLfloat *){}
void glClipPlane(GLenum,const GLdouble *){}
#endif
#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  Xcas_config_type Xcas_config;
  
  string print_DOUBLE_(double d){
    char s[256];
#ifdef IPAQ
    sprintf(s,"%.4g",d);
#else
    sprintf(s,"%.5g",d);
#endif
    return s;
  }

  // from Graph.cc
  bool do_helpon=true;

  static double pow10(double d){
    return std::pow(10.,d);
  }

  void xcas_color(int color,bool dim3){
    switch (color){
    case FL_RED:
      glColor3f(1,0,0);
      return;
    case FL_BLACK:
      glColor3f(0,0,0);
      return;
    case FL_WHITE:
      glColor3f(1,1,1);
      return;
    case FL_BLUE:
      glColor3f(0,0,1);
      return;
    case FL_GREEN:
      glColor3f(0,1,0);
      return;
    case FL_CYAN:
      glColor3f(0,1,1);
      return;
    case FL_MAGENTA:
      glColor3f(1,0,1);
      return;
    case FL_YELLOW:
      glColor3f(1,1,0);
      return;
    }
    int r,g,b;
    arc_en_ciel(color % 0x7e,r,g,b);
    glColor3f(r/255.,g/255.,b/255.);
  }

  inline int Min(int i,int j) {return i>j?j:i;}

  inline int Max(int i,int j) {return i>j?i:j;}

  quaternion_double::quaternion_double(double theta_x,double theta_y,double theta_z) { 
    *this=euler_deg_to_quaternion_double(theta_x,theta_y,theta_z); 
  }

  quaternion_double euler_deg_to_quaternion_double(double a,double b,double c){
    double phi=a*M_PI/180, theta=b*M_PI/180, psi=c*M_PI/180;
    double c1 = std::cos(phi/2);
    double s1 = std::sin(phi/2);
    double c2 = std::cos(theta/2);
    double s2 = std::sin(theta/2);
    double c3 = std::cos(psi/2);
    double s3 = std::sin(psi/2);
    double c1c2 = c1*c2;
    double s1s2 = s1*s2;
    double w =c1c2*c3 - s1s2*s3;
    double x =c1c2*s3 + s1s2*c3;
    double y =s1*c2*c3 + c1*s2*s3;
    double z =c1*s2*c3 - s1*c2*s3;
    return quaternion_double(w,x,y,z);
  }

  void quaternion_double_to_euler_deg(const quaternion_double & q,double & phi,double & theta, double & psi){
    double test = q.x*q.y + q.z*q.w;
    if (test > 0.499) { // singularity at north pole
      phi = 2 * atan2(q.x,q.w) * 180/M_PI;
      theta = 90; 
      psi = 0;
      return;
    }
    if (test < -0.499) { // singularity at south pole
      phi = -2 * atan2(q.x,q.w) * 180/M_PI;
      theta = - 90;
      psi = 0;
      return;
    }
    double sqx = q.x*q.x;
    double sqy = q.y*q.y;
    double sqz = q.z*q.z;
    phi = atan2(2*q.y*q.w-2*q.x*q.z , 1 - 2*sqy - 2*sqz) * 180/M_PI;
    theta = std::asin(2*test) * 180/M_PI;
    psi = atan2(2*q.x*q.w-2*q.y*q.z , 1 - 2*sqx - 2*sqz) * 180/M_PI;
  }

  quaternion_double operator * (const quaternion_double & q1,const quaternion_double & q2){ 
    double z=q1.w*q2.z+q2.w*q1.z+q1.x*q2.y-q2.x*q1.y;
    double x=q1.w*q2.x+q2.w*q1.x+q1.y*q2.z-q2.y*q1.z;
    double y=q1.w*q2.y+q2.w*q1.y+q1.z*q2.x-q2.z*q1.x;
    double w=q1.w*q2.w-q1.x*q2.x-q1.y*q2.y-q1.z*q2.z;
    return quaternion_double(w,x,y,z);
  }

  // q must be a unit
  void get_axis_angle_deg(const quaternion_double & q,double &x,double &y,double & z, double &theta){
    double scale=1-q.w*q.w;
    if (scale>1e-6){
      scale=std::sqrt(scale);
      theta=2*std::acos(q.w)*180/M_PI;
      x=q.x/scale;
      y=q.y/scale;
      z=q.z/scale;
    }
    else {
      x=0; y=0; z=1;
      theta=0;
    }
  }

  ostream & operator << (ostream & os,const quaternion_double & q){
    return os << q.w << "+" << q.x << "i+" << q.y << "j+" << q.z << "k";
  }

  void Opengl::update_infos(const gen & g){
    if (g.type==_VECT && g.subtype==_GRAPH__VECT){
      show_axes=false;
      orthonormalize();
    }
    if (g.is_symb_of_sommet(at_equal)){
      // detect a title or a x/y-axis name
      gen & f = g._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2){
	gen & optname = f._VECTptr->front();
	gen & optvalue= f._VECTptr->back();
	if (optname==at_legende && optvalue.type==_VECT){
	  vecteur & optv=(*optvalue._VECTptr);
	  int optvs=optv.size();
	  if (optvs>=1)
	    x_axis_unit=printstring(optv[0],contextptr);
	  if (optvs>=2)
	    y_axis_unit=printstring(optv[1],contextptr);
	  if (optvs>=3)
	    z_axis_unit=printstring(optv[2],contextptr);
	}
	if (optname.type==_INT_ && optname.subtype == _INT_PLOT){ 
#if 0
	  if (optname.val==_GL_TEXTURE){
	    if (optvalue.type==_VECT && optvalue._VECTptr->size()==2 && optvalue._VECTptr->front().type==_STRNG && is_undef(optvalue._VECTptr->back())){
	      // reload cached image
	      optvalue=optvalue._VECTptr->front();
	      std::map<std::string,std::pair<Fl_Image *,Fl_Image*> *>::iterator it,itend=texture2d_cache.end();
	      it=texture2d_cache.find(optvalue._STRNGptr->c_str());
	      if (it!=itend){
		std::pair<Fl_Image *,Fl_Image*> * old= it->second;
		delete old;
		texture2d_cache.erase(it);
	      }
	      get_texture2d(*optvalue._STRNGptr,background_image);
	    }
	    else {
	      if (optvalue.type==_STRNG){
		get_texture2d(*optvalue._STRNGptr,background_image);
	      }
	      else {
		background_image=0;
	      }
	    }
	  }
#endif
	  if (optname.val==_TITLE )
	    title=printstring(optvalue,contextptr);
	  if (optname.val==_AXES){
	    if (optvalue.type==_INT_)
	      show_axes=optvalue.val;
	  }
	  if (optname.val==_LABELS && optvalue.type==_VECT){
	    vecteur & optv=(*optvalue._VECTptr);
	    int optvs=optv.size();
	    if (optvs>=1)
	      x_axis_name=printstring(optv[0],contextptr);
	    if (optvs>=2)
	      y_axis_name=printstring(optv[1],contextptr);
	    if (optvs>=3)
	      z_axis_name=printstring(optv[2],contextptr);
	  }
	  if (optname.val==_GL_ORTHO && optvalue==1)
	    orthonormalize();
	  if (optname.val==_GL_X_AXIS_COLOR && optvalue.type==_INT_)
	    x_axis_color=optvalue.val;
	  if (optname.val==_GL_Y_AXIS_COLOR && optvalue.type==_INT_)
	    y_axis_color=optvalue.val;
	  if (optname.val==_GL_Z_AXIS_COLOR && optvalue.type==_INT_)
	    z_axis_color=optvalue.val;
	  if (optname.val>=_GL_X && optname.val<=_GL_Z && optvalue.is_symb_of_sommet(at_interval)){
	    gen optvf=evalf_double(optvalue._SYMBptr->feuille,1,contextptr);
	    if (optvf.type==_VECT && optvf._VECTptr->size()==2){
	      gen a=optvf._VECTptr->front();
	      gen b=optvf._VECTptr->back();
	      if (a.type==_DOUBLE_ && b.type==_DOUBLE_){
		switch (optname.val){
		case _GL_X:
		  window_xmin=a._DOUBLE_val;
		  window_xmax=b._DOUBLE_val;
		  break;
		case _GL_Y:
		  window_ymin=a._DOUBLE_val;
		  window_ymax=b._DOUBLE_val;
		  break;
		case _GL_Z:
		  window_zmin=a._DOUBLE_val;
		  window_zmax=b._DOUBLE_val;
		  break;
		}
	      }
	    }
	  }
	  gen optvalf=evalf_double(optvalue,1,contextptr);
	  if (optname.val==_GL_XTICK && optvalf.type==_DOUBLE_)
	    x_tick=optvalf._DOUBLE_val;
	  if (optname.val==_GL_YTICK && optvalf.type==_DOUBLE_)
	    y_tick=optvalf._DOUBLE_val;
	  if (optname.val==_GL_ZTICK && optvalf.type==_DOUBLE_)
	    z_tick=optvalf._DOUBLE_val;
	  if (optname.val==_GL_ANIMATE && optvalf.type==_DOUBLE_)
	    animation_dt=optvalf._DOUBLE_val;
	  if (optname.val==_GL_SHOWAXES && optvalue.type==_INT_)
	    show_axes=optvalue.val;
	  if (optname.val==_GL_SHOWNAMES && optvalue.type==_INT_)
	    show_names=optvalue.val;
	  if (optname.val>=_GL_X_AXIS_NAME && optname.val<=_GL_Z_AXIS_UNIT && optvalue.type==_STRNG){
	    if (optname.val==_GL_X_AXIS_NAME) x_axis_name=*optvalue._STRNGptr;
	    if (optname.val==_GL_Y_AXIS_NAME) y_axis_name=*optvalue._STRNGptr;
	    if (optname.val==_GL_Z_AXIS_NAME) z_axis_name=*optvalue._STRNGptr;
	    if (optname.val==_GL_X_AXIS_UNIT) x_axis_unit=*optvalue._STRNGptr;
	    if (optname.val==_GL_Y_AXIS_UNIT) y_axis_unit=*optvalue._STRNGptr;
	    if (optname.val==_GL_Z_AXIS_UNIT) z_axis_unit=*optvalue._STRNGptr;
	  }
	  if (optname.val==_GL_QUATERNION && optvalf.type==_VECT && optvalf._VECTptr->size()==4){
	    vecteur & optvalv=*optvalf._VECTptr;
	    if (optvalv[0].type==_DOUBLE_ && optvalv[1].type==_DOUBLE_ && 
		optvalv[2].type==_DOUBLE_ && optvalv[3].type==_DOUBLE_){
	      q.x=optvalv[0]._DOUBLE_val;
	      q.y=optvalv[1]._DOUBLE_val;
	      q.z=optvalv[2]._DOUBLE_val;
	      q.w=optvalv[3]._DOUBLE_val;
	    }
	  }
	  if (optname.val==_GL_LOGX && optvalue.type==_INT_){
	    display_mode &= (0xffff ^ 0x400);
	    if (optvalue.val)
	      display_mode |= 0x400;
	  }
	  if (optname.val==_GL_LOGY && optvalue.type==_INT_){
	    display_mode &= (0xffff ^ 0x800);
	    if (optvalue.val)
	      display_mode |= 0x800;
	  }
	  if (optname.val==_GL_LOGZ && optvalue.type==_INT_){
	    display_mode &= (0xffff ^ 0x1000);
	    if (optvalue.val)
	      display_mode |= 0x1000;
	  }
	  if (dynamic_cast<Opengl3d *>(this)){
	    if (optname.val==_GL_ROTATION_AXIS && optvalf.type==_VECT && optvalf._VECTptr->size()==3){
	      vecteur & optvalv=*optvalf._VECTptr;
	      if (optvalv[0].type==_DOUBLE_ && optvalv[1].type==_DOUBLE_ && 
		  optvalv[2].type==_DOUBLE_ ){
		rotanim_rx=optvalv[0]._DOUBLE_val;
		rotanim_ry=optvalv[1]._DOUBLE_val;
		rotanim_rz=optvalv[2]._DOUBLE_val;	
	      }      
	    }
	    if (optname.val==_GL_FLAT && optvalue.type==_INT_){
	      display_mode &= (0xffff ^ 0x10);
	      if (optvalue.val)
		display_mode |= 0x10;
	    }
	    if (optname.val==_GL_LIGHT && optvalue.type==_INT_){
	      display_mode &= (0xffff ^ 0x8);
	      if (optvalue.val)
		display_mode |= 0x8;
	    }
	    if (optname.val==_GL_PERSPECTIVE && optvalue.type==_INT_){
	      display_mode &= (0xffff ^ 0x4);
	      if (!optvalue.val)
		display_mode |= 0x4;
	    }
	    // GL_LIGHT_MODEL_COLOR_CONTROL=GL_SEPARATE_SPECULAR_COLOR ||  GL_SINGLE_COLOR
#ifndef WIN32
	    if (optname.val==_GL_LIGHT_MODEL_COLOR_CONTROL && optvalue.type==_INT_)
	      glLightModeli(GL_LIGHT_MODEL_COLOR_CONTROL,optvalue.val);
	    /* GL_LIGHT_MODEL_LOCAL_VIEWER=floating-point value that spec-
	       ifies how specular reflection angles are computed.  If params
	       is 0 (or 0.0),  specular  reflection  angles  take  the  view
	       direction  to  be  parallel to and in the direction of the -z
	       axis, regardless of the location of the vertex in eye coordi-
	       nates.  Otherwise, specular reflections are computed from the
	       origin of the eye coordinate system.  The initial value is 0. */
	    if (optname.val==_GL_LIGHT_MODEL_LOCAL_VIEWER){
	      if (optvalf.type==_DOUBLE_)
		glLightModelf(GL_LIGHT_MODEL_LOCAL_VIEWER,optvalf._DOUBLE_val);
	    }
#endif
#ifdef HAVE_LIBFLTK_GL
	    /* GL_LIGHT_MODEL_TWO_SIDE = true /false */
	    if (optname.val==_GL_LIGHT_MODEL_TWO_SIDE && optvalue.type==_INT_){
	      glLightModeli(GL_LIGHT_MODEL_TWO_SIDE,optvalue.val);
	    }
	    /* GL_LIGHT_MODEL_AMBIENT=[r,g,b,a] */
	    if (optname.val==_GL_LIGHT_MODEL_AMBIENT && optvalf.type==_VECT && optvalf._VECTptr->size()==4){
	      vecteur & w=*optvalf._VECTptr;
	      GLfloat tab[4]={w[0]._DOUBLE_val,w[1]._DOUBLE_val,w[2]._DOUBLE_val,w[3]._DOUBLE_val};
	      glLightModelfv(GL_LIGHT_MODEL_AMBIENT,tab);
	    }
	    // gl_blend=[d,s] 
	    // habituellement gl_blend=[gl_src_alpha,gl_one_minus_src_alpha]
	    if (optname.val==_GL_BLEND){
	      if (is_zero(optvalue)){
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
	      }
	      else {
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		if (optvalue.type==_VECT && optvalue._VECTptr->size()==2)
		  glBlendFunc(optvalue._VECTptr->front().val,optvalue._VECTptr->back().val);
		if (is_minus_one(optvalue))
		  glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
	      }
	    }
#endif
	    // gl_light0=[option1=value1,...]
	    if (optname.val>=_GL_LIGHT0 && optname.val<=_GL_LIGHT7 && optvalue.type==_VECT){
	      int j=optname.val-_GL_LIGHT0;
	      // reset light0+j
	      light_x[j]=0;light_y[j]=0;light_z[j]=0;light_w[j]=1;
	      float di=j?0:1;
	      light_diffuse_r[j]=di;light_diffuse_g[j]=di;light_diffuse_b[j]=di;light_diffuse_a[j]=di;
	      light_specular_r[j]=di;light_specular_g[j]=di;light_specular_b[j]=di;light_specular_a[j]=di;
	      light_ambient_r[j]=0;light_ambient_g[j]=0;light_ambient_b[j]=0;light_ambient_a[j]=1;
	      light_spot_x[j]=0;light_spot_y[j]=0;light_spot_z[j]=-1;light_spot_w[j]=0;
	      light_spot_exponent[j]=0;light_spot_cutoff[j]=180;
	      light_0[j]=1;light_1[j]=0;light_2[j]=0;
	      vecteur & optv=*optvalue._VECTptr;
	      for (unsigned i=0;i<optv.size();++i){
		gen & optg = optv[i];
		if ( (optg.is_symb_of_sommet(at_equal) || optg.is_symb_of_sommet(at_same) )  && optg._SYMBptr->feuille.type==_VECT && g._SYMBptr->feuille._VECTptr->size()==2){
		  gen & optgname = optg._SYMBptr->feuille._VECTptr->front();
		  gen optgval = evalf_double(optg._SYMBptr->feuille._VECTptr->back(),1,contextptr);
		  bool vect4=optgval.type==_VECT && optgval._VECTptr->size()==4;
		  vecteur xyzw;
		  if (vect4)
		    xyzw=*optgval._VECTptr;
		  switch (optgname.val){
		  case _GL_AMBIENT:
		    light_ambient_r[j]=xyzw[0]._DOUBLE_val;
		    light_ambient_g[j]=xyzw[1]._DOUBLE_val;
		    light_ambient_b[j]=xyzw[2]._DOUBLE_val;
		    light_ambient_a[j]=xyzw[3]._DOUBLE_val;
		    break;
		  case _GL_SPECULAR:
		    light_specular_r[j]=xyzw[0]._DOUBLE_val;
		    light_specular_g[j]=xyzw[1]._DOUBLE_val;
		    light_specular_b[j]=xyzw[2]._DOUBLE_val;
		    light_specular_a[j]=xyzw[3]._DOUBLE_val;
		    break;
		  case _GL_DIFFUSE:
		    light_diffuse_r[j]=xyzw[0]._DOUBLE_val;
		    light_diffuse_g[j]=xyzw[1]._DOUBLE_val;
		    light_diffuse_b[j]=xyzw[2]._DOUBLE_val;
		    light_diffuse_a[j]=xyzw[3]._DOUBLE_val;
		    break;
		  case _GL_POSITION:
		    light_x[j]=xyzw[0]._DOUBLE_val;
		    light_y[j]=xyzw[1]._DOUBLE_val;
		    light_z[j]=xyzw[2]._DOUBLE_val;
		    light_w[j]=xyzw[3]._DOUBLE_val;
		    break;
		  case _GL_SPOT_DIRECTION:
		    light_spot_x[j]=xyzw[0]._DOUBLE_val;
		    light_spot_y[j]=xyzw[1]._DOUBLE_val;
		    light_spot_z[j]=xyzw[2]._DOUBLE_val;
		    light_spot_w[j]=xyzw[3]._DOUBLE_val;
		    break;
		  case _GL_SPOT_EXPONENT:
		    light_spot_exponent[j]=optgval._DOUBLE_val;
		    break;
		  case _GL_SPOT_CUTOFF:
		    light_spot_cutoff[j]=optgval._DOUBLE_val;
		    break;
		  case _GL_CONSTANT_ATTENUATION:
		    light_0[j]=optgval._DOUBLE_val;
		    break;
		  case _GL_LINEAR_ATTENUATION:
		    light_1[j]=optgval._DOUBLE_val;
		    break;
		  case _GL_QUADRATIC_ATTENUATION:
		    light_2[j]=optgval._DOUBLE_val;
		    break;
		  }
		}
		;
	      } // end for i
	    }
	  } // end opengl options
	}
      }
    }
    if (g.type==_VECT){
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it)
	update_infos(*it);
    }
  }

  void Opengl::move_cfg(int i){
    if (history.empty()) return;
    int j=i+history_pos;
    int s=history.size();
    if (j>s) j=s;
    if (j<1) j=1;
    history_pos=j;
    window_xyz & h = history[j-1];
    window_xmin=h.xmin;
    window_xmax=h.xmax;
    window_ymin=h.ymin;
    window_ymax=h.ymax;
    window_zmin=h.zmin;
    window_zmax=h.zmax;
  }

  void Opengl::push_cfg(){
    int s=history.size();
    if (history_pos<s && history_pos>=0){
      history.erase(history.begin()+history_pos,history.end());
    }
    history.push_back(window_xyz(window_xmin,window_xmax,window_ymin,window_ymax,window_zmin,window_zmax));
    history_pos=history.size();
  }

  void Opengl::clear_cfg(){
    history_pos=0;
    history.clear();
  }

  void Opengl::find_xyz(double i,double j,double k,double &x,double&y,double &z){
    x=i; y=j; z=k;
  }

  static void cb_Opengl_Autoscale(Opengl * gr , void*) {
    if (gr)
      gr->autoscale(false);
  }

  static void cb_Opengl_AutoscaleFull(Opengl * gr , void*) {
    if (gr)
      gr->autoscale(true);
  }

  static void cb_Opengl_Orthonormalize(Opengl * gr , void*) {
    if (gr)
      gr->orthonormalize();
  }

  static void cb_Opengl_Next(Opengl * gr , void*) {
    if (gr)
      gr->move_cfg(1);
  }

  static void cb_Opengl_Previous(Opengl * gr , void*) {
    if (gr)
      gr->move_cfg(-1);
  }

  static void cb_Opengl_Zoomout(Opengl * gr , void*) {
    if (gr)
      gr->zoom(1.414);
  }

  static void cb_Opengl_Zoomin(Opengl * gr , void*) {
    if (gr)
      gr->zoom(0.707);
  }

  static void cb_Opengl_Pause(Opengl * gr , void*) {
    if (gr)
      gr->paused=true;
  }

  static void cb_Opengl_Stop(Opengl * gr , void*) {
    if (gr){
      gr->animation_dt=0;
      gr->animation_instructions_pos=0;
    }
  }

  static void cb_Opengl_Restart(Opengl * gr , void*) {
    if (gr)
      gr->paused=false;
  }

  static void cb_Opengl_Faster(Opengl * gr , void*) {
    if (gr){
      if (gr->animation_dt)
	gr->animation_dt /= 2;
      else
	gr->animation_dt = 0.2;
    }
  }

  static void cb_Opengl_Slower(Opengl * gr , void*) {
    if (gr){
      if (gr->animation_dt)
	gr->animation_dt *= 2;
      else
	gr->animation_dt = 0.2;
    }
  }

  static void cb_Opengl_hide(Opengl * gr , void*) {
    if (Opengl3d * gr3 = dynamic_cast<Opengl3d *>(gr)){
      gr3->below_depth_hidden=true;
      gr3->redraw();
    }
  }

  static void cb_Opengl_show(Opengl * gr , void*) {
    if (Opengl3d * gr3 = dynamic_cast<Opengl3d *>(gr)){
      gr3->below_depth_hidden=false;
      gr3->redraw();
    }
  }

  static void cb_Opengl_startview(Opengl * gr , void*) {
    if (Opengl3d * gr3 = dynamic_cast<Opengl3d *>(gr)){
      gr3->theta_x=-13;
      gr3->theta_y=-95;
      gr3->theta_z=-110; 
      gr3->q=euler_deg_to_quaternion_double(gr3->theta_z,gr3->theta_x,gr3->theta_y);
      gr3->redraw();
    }
  }

  static void cb_Opengl_xview(Opengl * gr , void*) {
    if (Opengl3d * gr3 = dynamic_cast<Opengl3d *>(gr)){
      gr3->theta_x=0;
      gr3->theta_y=-90;
      gr3->theta_z=-90;      
      gr3->q=euler_deg_to_quaternion_double(gr3->theta_z,gr3->theta_x,gr3->theta_y);
      gr3->redraw();
    }
  }

  static void cb_Opengl_yview(Opengl * gr , void*) {
    if (Opengl3d * gr3 = dynamic_cast<Opengl3d *>(gr)){
      gr3->theta_x=0;
      gr3->theta_y=-90;
      gr3->theta_z=0;      
      gr3->q=euler_deg_to_quaternion_double(gr3->theta_z,gr3->theta_x,gr3->theta_y);
      gr3->redraw();
    }
  }

  static void cb_Opengl_zview(Opengl * gr , void*) {
    if (Opengl3d * gr3 = dynamic_cast<Opengl3d *>(gr)){
      gr3->theta_x=0;
      gr3->theta_y=0;
      gr3->theta_z=0;      
      gr3->q=euler_deg_to_quaternion_double(gr3->theta_z,gr3->theta_x,gr3->theta_y);
      gr3->redraw();
    }
  }

  static void cb_Opengl_mouse_plan(Opengl * gr , void*) {
    if (Opengl3d * gr3d = dynamic_cast<Opengl3d *>(gr)){
      double a,b,c;
      gr3d->current_normal(a,b,c);
      gr3d->normal2plan(a,b,c); // divides a,b,c by dx^2,...
      double x0,y0,z0,t0;
      gr3d->find_xyz(gr3d->x()+gr3d->w()/2,gr3d->y()+gr3d->h()/2,gr3d->depth,x0,y0,z0);
      t0=a*x0+b*y0+c*z0;
      if (std::abs(t0)<std::abs(gr3d->window_zmax-gr3d->window_zmin)/1000)
	t0=0;
      string s="plan("+print_DOUBLE_(a)+"*x+"+print_DOUBLE_(b)+"*y+"+print_DOUBLE_(c)+"*z="+print_DOUBLE_(t0)+")";
      //in_Xcas_input_char(Fl::focus(),s,' ');
    }
  }

  // image of (x,y,z) by rotation around axis r(rx,ry,rz) of angle theta
  void rotate(double rx,double ry,double rz,double theta,double x,double y,double z,double & X,double & Y,double & Z){
    /*
    quaternion_double q=rotation_2_quaternion_double(rx,ry,rz,theta);
    quaternion_double qx(x,y,z,0);
    quaternion_double qX=conj(q)*qx*q;
    */
    // r(rx,ry,rz) the axis, v(x,y,z) projects on w=a*r with a such that
    // w.r=a*r.r=v.r
    double r2=rx*rx+ry*ry+rz*rz;
    double r=std::sqrt(r2);
    double a=(rx*x+ry*y+rz*z)/r2;
    // v=w+V, w remains stable, V=v-w=v-a*r rotates
    // Rv=w+RV, where RV=cos(theta)*V+sin(theta)*(r cross V)/sqrt(r2)
    double Vx=x-a*rx,Vy=y-a*ry,Vz=z-a*rz;
    // cross product of k with V
    double kVx=ry*Vz-rz*Vy, kVy=rz*Vx-rx*Vz,kVz=rx*Vy-ry*Vx;
    double c=std::cos(theta),s=std::sin(theta);
    X=a*rx+c*Vx+s*kVx/r;
    Y=a*ry+c*Vy+s*kVy/r;
    Z=a*rz+c*Vz+s*kVz/r;
  }

  Opengl::Opengl(int w__,int h__,double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,double ortho):
    w_(w__),h_(h__),
    pushed(false),
    show_mouse_on_object(false),
    mode(255),args_tmp_push_size(0),no_handle(false),
    display_mode(0x45),
    window_xmin(xmin),window_xmax(xmax),window_ymin(ymin),window_ymax(ymax),window_zmin(zmin),window_zmax(zmax),history_pos(0),
    ylegende(2.5),
    npixels(8),
    show_axes(1),show_names(1),
    paused(true),twodim(false),
    ipos(0),jpos(0),depthpos(0),
    last_event(0),x_tick(1.0),y_tick(1.0),couleur(0),approx(true),moving(false),moving_frame(false),ntheta(24),nphi(18) {
    push_cfg();
    legende_size=giac::LEGENDE_SIZE;
    x_axis_color=FL_RED;
    y_axis_color=FL_GREEN;
    z_axis_color=FL_BLUE;
    current_i=current_j=RAND_MAX;
    in_area=false;
  }

  Opengl::Opengl(int w__,int h__):
    w_(w__),h_(h__),
    pushed(false),
    show_mouse_on_object(false),
    display_mode(0x45),
    mode(255),args_tmp_push_size(0),no_handle(false),
    window_xmin(Xcas_config.window_xmin),window_xmax(Xcas_config.window_xmax),window_ymin(Xcas_config.window_ymin),window_ymax(Xcas_config.window_ymax),window_zmin(Xcas_config.window_zmin),window_zmax(Xcas_config.window_zmax),history_pos(0),
    ylegende(2.5),
    npixels(8),
    show_axes(1),show_names(1),
    paused(true),twodim(false),
    ipos(0),jpos(0),depthpos(0),
    last_event(0),x_tick(1.0),y_tick(1.0),couleur(0),approx(true),hp_pos(-1),moving(false),moving_frame(false),ntheta(24),nphi(18) { 
    legende_size=giac::LEGENDE_SIZE;
    push_cfg();
    x_axis_color=FL_RED;
    y_axis_color=FL_GREEN;
    z_axis_color=FL_BLUE;
  }

  Opengl::~Opengl(){
  }

  int Opengl::x() const { return 0; }
  int Opengl::y() const { return 0; }
  int Opengl::w() const { return w_; }
  int Opengl::h() const { return h_; }

  double find_tick(double dx){
    double res=std::pow(10.0,std::floor(std::log10(std::abs(dx))));
    int nticks=int(dx/res);
    if (nticks<4)
      res/=5;
    else {
      if (nticks<8)
	res/=2;
    }
    return res;
  }

  std::string Opengl::current_config(){
    string res="gl_quaternion=[";
    res += print_DOUBLE_(q.x);
    res += ",";
    res += print_DOUBLE_(q.y);
    res += ",";
    res += print_DOUBLE_(q.z);
    res += ",";
    res += print_DOUBLE_(q.w);
    res += "]";
    return res;
  }

  void Opengl::reset_light(unsigned i){
    light_on[i]=!i;
    light_x[i]=0;light_y[i]=0;light_z[i]=1;light_w[i]=0;
    float di=i?0:1;
    light_diffuse_r[i]=di;light_diffuse_g[i]=di;light_diffuse_b[i]=di;light_diffuse_a[i]=di;
    light_specular_r[i]=di;light_specular_g[i]=di;light_specular_b[i]=di;light_specular_a[i]=di;
    light_ambient_r[i]=0;light_ambient_g[i]=0;light_ambient_b[i]=0;light_ambient_a[i]=1;
    light_spot_x[i]=0;light_spot_y[i]=0;light_spot_z[i]=-1;light_spot_w[i]=0;
    light_spot_exponent[i]=0;light_spot_cutoff[i]=180;
    light_0[i]=1;light_1[i]=0;light_2[i]=0;
  }

  
  // round to 3 decimals
  double setup_round(double x){
    if (x<0)
      return -setup_round(-x);
    if (x<1e-300)
      return 0;
    int n=int(std::floor(std::log10(x)+.5)); // round to nearest
    x=int(std::floor(x*std::pow(10.0,3.0-n)+.5));
    x=x*std::pow(10.0,n-3.0);
    return x;
  }

  void Opengl::autoscale(bool fullview){
    if (!plot_instructions.empty()){
      // Find the largest and lowest x/y/z in objects (except lines/plans)
      vector<double> vx,vy,vz;
      int s;
      bool ortho=autoscaleg(plot_instructions,vx,vy,vz,contextptr);
      autoscaleminmax(vx,window_xmin,window_xmax,fullview);
      if (display_mode & 0x400){
	if (window_xmin<=0){
	  if (vx[0]<=0)
	    window_xmin=-309;
	  else
	    window_xmin=std::log10(vx[0]);
	}
	else
	  window_xmin=std::log10(window_xmin);
	if (window_xmax<=0)
	  window_xmax=-300;
	else
	  window_xmax=std::log10(window_xmax);
      }
      zoomx(1.0);
      autoscaleminmax(vy,window_ymin,window_ymax,fullview);
      if (display_mode & 0x800){
	if (window_ymin<=0){
	  if (vy[0]<=0)
	    window_ymin=-309;
	  else
	    window_ymin=std::log10(vy[0]);
	}
	else
	  window_ymin=std::log10(window_ymin);
	if (window_ymax<=0)
	  window_ymax=-300;
	else
	  window_ymax=std::log10(window_ymax);
      }
      zoomy(1.0);
      autoscaleminmax(vz,window_zmin,window_zmax,fullview);
      zoomz(1.0);
      if (ortho)
	orthonormalize();
    }
    y_tick=find_tick(window_ymax-window_ymin);
    redraw();
    push_cfg();
  }

  void Opengl::zoomx(double d,bool round){
    double x_center=(window_xmin+window_xmax)/2;
    double dx=(window_xmax-window_xmin);
    if (dx==0)
      dx=gnuplot_xmax-gnuplot_xmin;
    dx *= d/2;
    x_tick = find_tick(dx);
    window_xmin = x_center - dx;
    if (round) 
      window_xmin=int( window_xmin/x_tick -1)*x_tick;
    window_xmax = x_center + dx;
    if (round)
      window_xmax=int( window_xmax/x_tick +1)*x_tick;
  }

  void Opengl::zoomy(double d,bool round){
    double y_center=(window_ymin+window_ymax)/2;
    double dy=(window_ymax-window_ymin);
    if (dy==0)
      dy=gnuplot_ymax-gnuplot_ymin;
    dy *= d/2;
    y_tick = find_tick(dy);
    window_ymin = y_center - dy;
    if (round)
      window_ymin=int( window_ymin/y_tick -1)*y_tick;
    window_ymax = y_center + dy;
    if (round)
      window_ymax=int( window_ymax/y_tick +1)*y_tick;
  }

  void Opengl::zoomz(double d,bool round){
    double z_center=(window_zmin+window_zmax)/2;
    double dz=(window_zmax-window_zmin);
    if (dz==0)
      dz=gnuplot_zmax-gnuplot_zmin;
    dz *= d/2;
    z_tick=find_tick(dz);
    window_zmin = z_center - dz;
    if (round)
      window_zmin=int(window_zmin/z_tick -1)*z_tick;
    window_zmax = z_center + dz;
    if (round)
      window_zmax=int(window_zmax/z_tick +1)*z_tick;
  }


  void Opengl::zoom(double d){ 
    zoomx(d);
    zoomy(d);
    zoomz(d);
    push_cfg();
  }


  void Opengl::orthonormalize(){ 
    // don't do anything in base class
  }

  void Opengl::labelsize(int i){
    labelsize_=i;
  }

  int Opengl::labelsize() const{
    return labelsize_;
  }

  void Opengl::redraw(){
  }
  
  void Opengl::up(double d){ 
    window_ymin += d;
    window_ymax += d;
    push_cfg();
  }

  void Opengl::down(double d){ 
    window_ymin -= d;
    window_ymax -= d;
    push_cfg();
  }

  void Opengl::up_z(double d){ 
    window_zmin += d;
    window_zmax += d;
    push_cfg();
  }

  void Opengl::down_z(double d){ 
    window_zmin -= d;
    window_zmax -= d;
    push_cfg();
  }

  void Opengl::left(double d){ 
    window_xmin -= d;
    window_xmax -= d;
    push_cfg();
  }

  void Opengl::right(double d){ 
    window_xmin += d;
    window_xmax += d;
    push_cfg();
  }

  void Opengl::set_axes(int b){ 
    show_axes = b;
  }

  void Opengl::copy(const Opengl & gr){
    window_xmin=gr.window_xmin;
    window_xmax=gr.window_xmax;
    window_ymin=gr.window_ymin;
    window_ymax=gr.window_ymax;
    window_zmin=gr.window_zmin;
    window_zmax=gr.window_zmax;
    npixels=gr.npixels;
    show_axes=gr.show_axes;
    show_names=gr.show_names;
    history = gr.history;
    labelsize(gr.labelsize());
    q=gr.q;
    display_mode=gr.display_mode;
    // copy lights
    for (int i=0;i<8;++i){
      light_on[i]=gr.light_on[i];
      light_x[i]=gr.light_x[i];
      light_y[i]=gr.light_y[i];
      light_z[i]=gr.light_z[i];
      light_w[i]=gr.light_w[i];;
      light_diffuse_r[i]=gr.light_diffuse_r[i];
      light_diffuse_g[i]=gr.light_diffuse_g[i];
      light_diffuse_b[i]=gr.light_diffuse_b[i];
      light_diffuse_a[i]=gr.light_diffuse_a[i];
      light_specular_r[i]=gr.light_specular_r[i];
      light_specular_g[i]=gr.light_specular_g[i];
      light_specular_b[i]=gr.light_specular_b[i];
      light_specular_a[i]=gr.light_specular_a[i];
      light_ambient_r[i]=gr.light_ambient_r[i];
      light_ambient_g[i]=gr.light_ambient_g[i];
      light_ambient_b[i]=gr.light_ambient_b[i];
      light_ambient_a[i]=gr.light_ambient_a[i];
      light_spot_x[i]=gr.light_spot_x[i];
      light_spot_y[i]=gr.light_spot_y[i];
      light_spot_z[i]=gr.light_spot_z[i];
      light_spot_w[i]=gr.light_spot_w[i];
      light_spot_exponent[i]=gr.light_spot_exponent[i];
      light_spot_cutoff[i]=gr.light_spot_cutoff[i];
      light_0[i]=gr.light_0[i];
      light_1[i]=gr.light_1[i];
      light_2[i]=gr.light_2[i];
    }
    ntheta=gr.ntheta;
    nphi=gr.nphi;
  }

  int round(double d){
    int res=int(floor(d+0.5));
    int maxpixels=10000; // maximal number of horizontal or vertical pixels
    if (d>maxpixels)
      return maxpixels;
    if (d<-maxpixels)
      return -maxpixels;
    return res;
  }

  string printsemi(GIAC_CONTEXT){
    if (xcas_mode(contextptr)==3)
      return "§";
    else
      return ";";
  }


  string cas_recalc_name(){
    if (getenv("XCAS_TMP"))
      return getenv("XCAS_TMP")+("/#c#"+print_INT_(parent_id));
#ifdef WIN32
    return "#c#"+print_INT_(parent_id);
#endif
#ifdef IPAQ
    return "/tmp/#c#"+print_INT_(parent_id);
#endif
    return home_directory()+"#c#"+print_INT_(parent_id);
  }

  void Opengl::adjust_cursor_point_type(){
    if (abs_calc_mode(contextptr)==38){
      double newx,newy,newz;
      find_xyz(current_i,current_j,current_depth,newx,newy,newz);
      int pos=-1;
      gen orig;
      //gen res=Opengl::geometry_round(newx,newy,newz,find_eps(),orig,pos);
      cursor_point_type=pos>=0?6:3;
    }
  }

  gen geometry_round_numeric(double x,double y,double eps,bool approx){
    return approx?gen(x,y):exact_double(x,eps)+cst_i*exact_double(y,eps);
  }

  gen geometry_round_numeric(double x,double y,double z,double eps,bool approx){
    return approx?makevecteur(x,y,z):makevecteur(exact_double(x,eps),exact_double(y,eps),exact_double(z,eps));
  }

  void round3(double & x,double xmin,double xmax){
    double dx=std::abs(xmax-xmin);
    double logdx=std::log10(dx);
    int ndec=int(logdx)-4;
    double xpow=std::pow(10.0,ndec);
    int newx=int(x/xpow);
    x=newx*xpow;
  }

  bool is_numeric(const gen & a);
  bool is_numeric(const vecteur & v){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (!is_numeric(*it))
	return false;
    }
    return true;
  }

  bool is_numeric(const gen & a){
    switch (a.type){
    case _DOUBLE_: case _INT_: case _ZINT: case _REAL:
      return true;
    case _CPLX:
      return is_numeric(*a._CPLXptr) && is_numeric(*(a._CPLXptr+1));
    case _VECT:
      return is_numeric(*a._VECTptr);
    case _FRAC:
      return is_numeric(a._FRACptr->num) && is_numeric(a._FRACptr->den);
    case _SYMB:
      if (a.is_symb_of_sommet(at_prod) || a.is_symb_of_sommet(at_inv) || a.is_symb_of_sommet(at_neg) || a.is_symb_of_sommet(at_plus))
	return is_numeric(a._SYMBptr->feuille);
    default:
      return false;
    }
  }

  double Opengl::find_eps(){
    double dx=window_xmax-window_xmin;
    double dy=window_ymax-window_ymin;
    double dz=window_zmax-window_zmin;
    double eps,epsx,epsy;
    int L=h()>w()?w():h();
    Opengl3d * gr3d=dynamic_cast<Opengl3d *>(this);
    epsx=(npixels*dx)/(gr3d?L:w());
    epsy=(npixels*dy)/(gr3d?L:h());
    eps=(epsx<epsy)?epsy:epsx;
    if (gr3d && dz>dy && dz >dx){
      eps=npixels*dz/L;
      eps *= 2;
    }
    return eps;
  }

  int Opengl::handle(int event){
    if (no_handle)
      return 0;
#ifdef HAVE_LIBPTHREAD
    // cerr << "handle lock" << endl;
    int locked=pthread_mutex_trylock(&interactive_mutex);
    if (locked)
      return 0;
#endif
    no_handle=true;
    bool b=io_graph(contextptr);
    io_graph(false,contextptr);
    int res=in_handle(event);
    io_graph(b,contextptr);
    no_handle=false;
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_unlock(&interactive_mutex);
    // cerr << "handle unlock" << endl;
#endif
    return res;
  }

  int Opengl::handle_keyboard(int event){
#if 1
    return 1;
#else
    if (event==FL_KEYBOARD){
      // Should bring this event to the current input in the parent() group
      switch (Fl::event_key()){
      case FL_Escape: case FL_BackSpace: case FL_Tab: case FL_Enter: 
      case FL_Print: case FL_Scroll_Lock: case FL_Pause: case FL_Insert: 
      case FL_Home: case FL_Delete: case FL_End: 
      case FL_Shift_L: case FL_Shift_R: case FL_Control_L: 
      case FL_Control_R: case FL_Caps_Lock: case FL_Alt_L: case FL_Alt_R: 
      case FL_Meta_L: case FL_Meta_R: case FL_Menu: case FL_Num_Lock: 
      case FL_KP_Enter:	
	return 1;
      case FL_Left:
	left((window_xmax-window_xmin)/10);
	return 1;
      case FL_Up:
	up((window_ymax-window_ymin)/10);
	return 1;
      case FL_Right: 
	right((window_xmax-window_xmin)/10);
	return 1;
      case FL_Down: 
	down((window_ymax-window_ymin)/10);
	return 1;
      case FL_Page_Up:
	up_z((window_zmax-window_zmin)/10);
	return 1;
      case FL_Page_Down:
	down_z((window_zmax-window_zmin)/10);
	return 1;	
      default:
	char ch=Fl::event_text()?Fl::event_text()[0]:0;
	switch (ch){
	case '-':
	  zoom(1.414);
	  return 1;
	case '+':
	  zoom(0.707);
	  return 1;
	case 'A': case 'a':
	  autoscale(false);
	  return 1;
	case 'V': case 'v':
	  autoscale(true);
	  return 1;
	case 'R': case 'r':
	  oxyz_rotate(this,rotanim_type,rotanim_nstep,rotanim_tstep,rotanim_danim,rotanim_rx,rotanim_ry,rotanim_rz);
	  return 1;
	case 'P': case 'p':
	  paused=!paused;
	  return 1;
	case 'N': case 'n': case 'F': case 'f':
	  animation_instructions_pos++;
	  redraw();
	  return 1;
	case 'B': case 'b':
	  animation_instructions_pos--;
	  redraw();
	  return 1;
	case 'C': case 'c': /* screen capture */
	  if (Opengl3d * gr3 = dynamic_cast<Opengl3d *>(this)){
	    char * filename = file_chooser(gettext("Export to PNG file"),"*.png","session.png");
	    if(!filename) return 1;
	    gr3->opengl2png(filename);
	    return 1;
	  }
	}	
      }
    }
    return 0;
#endif
  }

  int Opengl::in_handle(int event){
    int res=handle_keyboard(event);
    return res?res:handle_mouse(event);
  }

  vecteur Opengl::selection2vecteur(const vector<int> & v){
    int n=v.size();
    vecteur res(n);
    for (int i=0;i<n;++i){
      res[i]=plot_instructions[v[i]];
    }
    return res;
  }

  int findfirstclosedcurve(const vecteur & v){
    int s=v.size();
    for (int i=0;i<s;++i){
      gen g=remove_at_pnt(v[i]);
      if (g.is_symb_of_sommet(at_cercle))
	return i;
      if (g.type==_VECT && g.subtype==_GROUP__VECT){
	vecteur & w=*g._VECTptr;
	if (!w.empty() && w.front()==w.back())
	  return i;
      }
    }
    return -1;
  }

  gen int2color(int couleur_){
    gen col;
    if (couleur_){
      gen tmp;
      int val;
      vecteur colv;
      if ( (val=(couleur_ & 0x0000ffff))){
	tmp=val;
	tmp.subtype=_INT_COLOR;
	colv.push_back(tmp);
      }
      if ((val =(couleur_ & 0x00070000))){
	tmp=val;
	tmp.subtype=_INT_COLOR;
	colv.push_back(tmp);
      }
      if ((val =(couleur_ & 0x00380000))){
	tmp=val;
	tmp.subtype=_INT_COLOR;
	colv.push_back(tmp);
      }
      if ((val =(couleur_ & 0x01c00000))){
	tmp=val;
	tmp.subtype=_INT_COLOR;
	colv.push_back(tmp);
      }
      if ((val =(couleur_ & 0x0e000000))){
	tmp=val;
	tmp.subtype=_INT_COLOR;
	colv.push_back(tmp);
      }
      if ((val =(couleur_ & 0x30000000))){
	tmp=val;
	tmp.subtype=_INT_COLOR;
	colv.push_back(tmp);
      }
      if ((val =(couleur_ & 0x40000000))){
	tmp=val;
	tmp.subtype=_INT_COLOR;
	colv.push_back(tmp);
      }
      if ((val =(couleur_ & 0x80000000))){
	tmp=val;
	tmp.subtype=_INT_COLOR;
	colv.push_back(tmp);
      }
      if (colv.size()==1)
	col=colv.front();
      else
	col=symbolic(at_plus,gen(colv,_SEQ__VECT));
    }
    return col;
  }

  std::string print_color(int couleur){
    return int2color(couleur).print(context0);
  }

  giac::gen add_attributs(const giac::gen & g,int couleur_,GIAC_CONTEXT) {
    if (g.type!=_SYMB)
      return g;
    gen & f=g._SYMBptr->feuille;
    if (g._SYMBptr->sommet==at_couleur && f.type==_VECT && !f._VECTptr->empty()){
      gen col=couleur_;
      col.subtype=_INT_COLOR;
      vecteur v(*f._VECTptr);
      v.back()=col;
      return symbolic(at_couleur,gen(v,_SEQ__VECT));
    }
    if (couleur_==default_color(contextptr))
      return g;
    if (g._SYMBptr->sommet==at_of){
      gen col=couleur_;
      col.subtype=_INT_COLOR;
      return symbolic(at_couleur,gen(makevecteur(g,col),_SEQ__VECT));
    }
    vecteur v =gen2vecteur(f);
    gen col=int2color(couleur_);
    v.push_back(symbolic(at_equal,gen(makevecteur(at_display,col),_SEQ__VECT)));
    return symbolic(g._SYMBptr->sommet,(v.size()==1 && f.type!=_VECT)?f:gen(v,f.type==_VECT?f.subtype:_SEQ__VECT));
  }

  void Opengl::set_mode(const giac::gen & f_tmp,const giac::gen & f_final,int m){
    if (mode>=-1){
      pushed=false;
      moving=moving_frame=false;
      history_pos=-1;
      mode=m;
      function_final=f_final;
      function_tmp=f_tmp;
      args_tmp.clear();
    }
  }

  void find_dxdy(const string & legendes,int labelpos,int labelsize,int & dx,int & dy){
    int l=labelsize*legendes.size()/2;//int(fl_width(legendes.c_str()));
    dx=3;
    dy=1;
    switch (labelpos){
    case 1:
      dx=-l-3;
      break;
    case 2:
      dx=-l-3;
      dy=labelsize-2;
      break;
    case 3:
      dy=labelsize-2;
      break;
    }
  }

  
  string printstring(const gen & g,GIAC_CONTEXT){
    if (g.type==_STRNG)
      return *g._STRNGptr;
    return g.print(contextptr);
  }

  /*
  void Opengl::find_title_plot(gen & title_tmp,gen & plot_tmp){
    if (in_area && mode && !args_tmp.empty()){
      if (args_tmp.size()>=2){
	gen function=(mode==int(args_tmp.size()))?function_final:function_tmp;
	if (function.type==_FUNC){
	  bool dim2=dynamic_cast<Graph2d *>(this);
	  vecteur args2=args_tmp;
	  if ( *function._FUNCptr==(dim2?at_cercle:at_sphere)){
	    gen argv1;
	    try {
	      argv1=evalf(args_tmp.back(),1,contextptr);
	      argv1=evalf_double(argv1,1,contextptr);
	    }
	    catch (std::runtime_error & e){
	      argv1=undef;
	    }
	    if (argv1.is_symb_of_sommet(at_pnt) ||argv1.type==_IDNT){
	      argv1=remove_at_pnt(argv1);
	      if ( (argv1.type==_VECT && argv1.subtype==_POINT__VECT) || argv1.type==_CPLX || argv1.type==_IDNT)
		args2.back()=args_tmp.back()-args_tmp.front();
	    }
	  }
	  if (function==at_ellipse)
	    ;
	  title_tmp=gen(args2,_SEQ__VECT);
	  bool b=approx_mode(contextptr);
	  if (!b)
	    approx_mode(true,contextptr);
	  plot_tmp=symbolic(*function._FUNCptr,title_tmp);
	  if (!lidnt(title_tmp).empty())
	    ; // cerr << plot_tmp << endl;
	  bool bb=io_graph(contextptr);
	  int locked=0;
	  if (bb){
#ifdef HAVE_LIBPTHREAD
	    // cerr << "plot title lock" << endl;
	    locked=pthread_mutex_trylock(&interactive_mutex);
#endif
	    if (!locked)
	      io_graph(false,contextptr);
	  }
	  plot_tmp=protecteval(plot_tmp,1,contextptr);
	  if (bb && !locked){
	    io_graph(bb,contextptr);
#ifdef HAVE_LIBPTHREAD
	    pthread_mutex_unlock(&interactive_mutex);
	    // cerr << "plot title unlock" << endl;
#endif
	  }
	  if (!b)
	    approx_mode(false,contextptr);	
	} // end function.type==_FUNC
	else
	  title_tmp=gen(args_tmp,_SEQ__VECT);
      } // end size()>=2
      else	
	title_tmp=args_tmp;
    }
  }
  */
  
  void Opengl::autoname_plus_plus(){
    string s=autoname(contextptr);
    giac::autoname_plus_plus(s);
    autoname(s,contextptr);
  }
  int contrast(Fl_Color c){
    if (c<=7)
      return 7-c;
    if (c>=8 && c<0x10)
      return 7;
    if (c>=0x10 && c<0x50)
      return 0xf8;
    if (c & 0x4)
      return 0;
    return 7;
  }
  
  // from Graph3d.cc

  double giac_max(double i,double j){
    return i>j?i:j;
  }

  quaternion_double rotation_2_quaternion_double(double x, double y, double z,double theta){
    double t=theta*M_PI/180;
    double qx,qy,qz,qw,s=std::sin(t/2),c=std::cos(t/2);
    qx=x*s;
    qy=y*s;
    qz=z*s;
    qw=c;
    double n=std::sqrt(qx*qx+qy*qy+qz*qz+qw*qw);
    return quaternion_double(qw/n,qx/n,qy/n,qz/n);
  }

  Opengl3d::Opengl3d(int w__,int h__): 
    Opengl(w__,h__),
    theta_z(-110),theta_x(-13),theta_y(-95),
    delta_theta(5),draw_mode(GL_QUADS),//glcontext(0),
    dragi(0),dragj(0),push_in_area(false),depth(0),below_depth_hidden(false) {
    // end();
    // mode=0;
    display_mode |= 0x80;
    display_mode |= 0x200;
    couleur=_POINT_WIDTH_5;
    q=euler_deg_to_quaternion_double(theta_z,theta_x,theta_y);
    // 8 light initialization
    for (int i=0;i<8;++i)
      reset_light(i);
  }

  // t angle in radians -> r,g,b
  void arc_en_ciel(double t,int & r,int & g,int &b){
    int k=int(t/2/M_PI*126);
    arc_en_ciel(k,r,g,b);
  }

  bool get_glvertex(const vecteur & v,double & d1,double & d2,double & d3,double realiscmplx,double zmin,GIAC_CONTEXT){
    if (v.size()==3){
      gen tmp;
      tmp=evalf_double(v[0],2,contextptr);
      if (tmp.type!=_DOUBLE_) return false;
      d1=tmp._DOUBLE_val;
      tmp=evalf_double(v[1],2,contextptr);
      if (tmp.type!=_DOUBLE_) return false;
      d2=tmp._DOUBLE_val;
      tmp=evalf_double(v[2],2,contextptr);
      if (realiscmplx){
	double arg=0;
	if (realiscmplx<0){
	  d3=evalf_double(im(tmp,contextptr),2,contextptr)._DOUBLE_val-zmin;
	  arg=-d3*realiscmplx;
	}
	else {
	  if (tmp.type==_DOUBLE_){
	    d3=tmp._DOUBLE_val;
	    if (d3<0){
	      arg=M_PI;
	      d3=-d3;
	    }
	  }
	  else {
	    if (tmp.type==_CPLX && tmp._CPLXptr->type==_DOUBLE_ && (tmp._CPLXptr+1)->type==_DOUBLE_){
	      double r=tmp._CPLXptr->_DOUBLE_val;
	      double i=(tmp._CPLXptr+1)->_DOUBLE_val;
	      arg=std::atan2(i,r);
	      d3=std::sqrt(r*r+i*i);
	    }
	    else 
	      return false;
	  }
	} // end realiscmplx>0
	// set color corresponding to argument
	int r,g,b;
	arc_en_ciel(arg,r,g,b);
	glColor3f(r/255.,g/255.,b/255.);
	// glColor4i(r,g,b,int(std::log(d3+1)));
      } // end if (realiscmplx)
      else {
	if (tmp.type!=_DOUBLE_) return false;
	d3=tmp._DOUBLE_val;
      }
      return true;
    }
    return false;
  }

  bool get_glvertex(const gen & g,double & d1,double & d2,double & d3,double realiscmplx,double zmin,GIAC_CONTEXT){
    if (g.type!=_VECT)
      return false;
    return get_glvertex(*g._VECTptr,d1,d2,d3,realiscmplx,zmin,contextptr);
  }

  bool glvertex(const vecteur & v,double realiscmplx,double zmin,GIAC_CONTEXT){
    double d1,d2,d3;
    if (get_glvertex(v,d1,d2,d3,realiscmplx,zmin,contextptr)){
      glVertex3d(d1,d2,d3);
      return true;
    }
    return false;
  }

  // draw s at g with mode= 0 (upper right), 1, 2 or 3
  void Opengl3d::legende_draw(const gen & g,const string & s,int mode){
    // COUT << "legende_draw " << g << " " << s << endl;
    gen gf=evalf_double(g,1,contextptr);
    if (gf.type==_VECT && gf._VECTptr->size()==3){
      double Ax=gf[0]._DOUBLE_val;
      double Ay=gf[1]._DOUBLE_val;
      double Az=gf[2]._DOUBLE_val;
      double Ai,Aj,Ad;
      int di=3,dj=1;
      giac::find_dxdy(s,mode,labelsize(),di,dj);
      find_ij(Ax,Ay,Az,Ai,Aj,Ad);
      find_xyz(Ai+di,Aj+dj,Ad,Ax,Ay,Az);
      glRasterPos3d(Ax,Ay,Az);
      // string s1(s);
      // cst_greek_translate(s1);
      // draw_string(s1);
      draw_string(s);
    }
  }

  void glnormal(const vecteur & v){
    if (v.size()==3){
      double d1=evalf_double(v[0],2,context0)._DOUBLE_val;
      double d2=evalf_double(v[1],2,context0)._DOUBLE_val;
      double d3=evalf_double(v[2],2,context0)._DOUBLE_val;
      glNormal3d(d1,d2,d3);
    }
  }

  void glnormal(double d1,double d2,double d3,double e1,double e2,double e3,double f1,double f2,double f3){
    double de1(e1-d1),de2(e2-d2),de3(e3-d3),df1(f1-d1),df2(f2-d2),df3(f3-d3);
    glNormal3d(de2*df3-de3*df2,de3*df1-de1*df3,de1*df2-de2*df1);
  }

  void gltranslate(const vecteur & v){
    if (v.size()==3){
      double d1=evalf_double(v[0],2,context0)._DOUBLE_val;
      double d2=evalf_double(v[1],2,context0)._DOUBLE_val;
      double d3=evalf_double(v[2],2,context0)._DOUBLE_val;
      glTranslated(d1,d2,d3);
    }
  }


  void iso3d(const double &i1,const double &i2,const double &i3,const double &j1,const double &j2,const double &j3,const double &k1,const double &k2,const double &k3,double & x,double & y,double & z){
    double X=x,Y=y,Z=z;
    x=i1*X+j1*Y+k1*Z;
    y=i2*X+j2*Y+k2*Z;
    z=i3*X+j3*Y+k3*Z;
  }


#define LI 64
#define LH 64
  GLubyte image[LI][LH][3];

  void makeImage(void) {
    int i,j,c;
    for( i = 0 ; i < LI ; i++ ) {
      for( j = 0 ; j < LH ; j++ ) {
	c = (((i&0x8)==0)^
	     ((j&0x8)==0))*255;
	image[i][j][0] =(GLubyte) c;
	image[i][j][1] =(GLubyte) c;
	image[i][j][2] =(GLubyte) c; } }
  }

  bool test_enable_texture(void * texture){
#if 1
    return false;
#else
    if (!texture)
      return false;
    int depth=-1;
    if (texture->count()==1)
      depth=texture->d();
    if (depth==3 || depth==4){
      // define texture
      // makeImage();
      char * ptr=(char *)texture->data()[0];
      /*
      int W=texture->w(),H=texture->h();
      for (int y=0;y<H;++y){
	for (int x=0;x<W;x++)
	  cerr << unsigned(*(ptr+(x+y*W)*depth)) << " " << unsigned(*(ptr+(x+y*W)*depth+1)) << " " << unsigned(*(ptr+(x+y*W)*depth+2)) << ", ";
	cerr << endl;
      }
      */
      // texture->w() and texture->h() must be a power of 2!!
      glTexImage2D(GL_TEXTURE_2D,0,depth,
		   // LI,LH,
		   texture->w(),texture->h(),
		   0,
		   // GL_RGB,
		   (depth==3?GL_RGB:GL_RGBA),
		   GL_UNSIGNED_BYTE,
		   // &image[0][0][0]);
		   ptr);
      // not periodically
      glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP);
      glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP); 
      // adjust to nearest 
      glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,
		      // GL_NEAREST);
		      GL_LINEAR);
      glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,
		      // GL_NEAREST);
		      GL_LINEAR);
      //
      glTexEnvf(GL_TEXTURE_ENV,GL_TEXTURE_ENV_MODE,
		GL_MODULATE);
      //GL_REPLACE);
      glEnable(GL_TEXTURE_2D);
      return true;
    }
    return false;
#endif
  }

  
  // Sphere centered at center, radius radius, i,j,k orthonormal, ntheta/nphi
  // number of subdivisions
  // mode=GL_QUADS for example
  void glsphere(const vecteur & center,const gen & radius,const vecteur & i0,const vecteur & j0,const vecteur & k0,int ntheta,int nphi,int mode,void * texture,GIAC_CONTEXT){
    test_enable_texture(texture);
    double c1=evalf_double(center[0],1,contextptr)._DOUBLE_val; // center
    double c2=evalf_double(center[1],1,contextptr)._DOUBLE_val;
    double c3=evalf_double(center[2],1,contextptr)._DOUBLE_val;
    double r=evalf_double(radius,1,contextptr)._DOUBLE_val;
    double i1=evalf_double(i0[0],1,contextptr)._DOUBLE_val;
    double i2=evalf_double(i0[1],1,contextptr)._DOUBLE_val;
    double i3=evalf_double(i0[2],1,contextptr)._DOUBLE_val;
    double j1=evalf_double(j0[0],1,contextptr)._DOUBLE_val;
    double j2=evalf_double(j0[1],1,contextptr)._DOUBLE_val;
    double j3=evalf_double(j0[2],1,contextptr)._DOUBLE_val;
    double k1=evalf_double(k0[0],1,contextptr)._DOUBLE_val;
    double k2=evalf_double(k0[1],1,contextptr)._DOUBLE_val;
    double k3=evalf_double(k0[2],1,contextptr)._DOUBLE_val;
    double dtheta=2*M_PI/ntheta; // longitude
    double dphi=M_PI/nphi; // latitude
    double jsurtheta=0,isurphi=0,djsurtheta=1.0/ntheta,disurphi=1.0/nphi;
    double parallele1[ntheta+1],parallele2[ntheta+1],parallele3[ntheta+1];
    double x,y,z,oldx,oldy,oldz,X,Z;
    // Set initial parallel to the North pole
    for (int j=0;j<=ntheta;++j){
      parallele1[j]=k1; parallele2[j]=k2; parallele3[j]=k3;
    }
    // parallele1/2/3 contains the coordinate of the previous parallel
    for (int i=1;i<=nphi;i++,isurphi+=disurphi){
      // longitude theta=0, latitude phi=i*dphi
      X=std::sin(i*dphi); Z=std::cos(i*dphi); 
      oldx=X; oldy=0; oldz=Z;
      iso3d(i1,i2,i3,j1,j2,j3,k1,k2,k3,oldx,oldy,oldz);
      double * par1j=parallele1,* par2j=parallele2,*par3j=parallele3;
      jsurtheta=0;
      if (mode==GL_QUADS){
	glBegin(GL_QUAD_STRIP);
	for (int j=0;j<=ntheta;++par1j,++par2j,++par3j,jsurtheta+=djsurtheta){
	  glNormal3d(*par1j,*par2j,*par3j);
	  if (texture)
	    glTexCoord2f(jsurtheta,isurphi);
	  glVertex3d(c1+r*(*par1j),c2+r*(*par2j),c3+r*(*par3j));
	  if (texture)
	    glTexCoord2f(jsurtheta,isurphi+disurphi);
	  glVertex3d(c1+r*oldx,c2+r*oldy,c3+r*oldz);
	  *par1j=oldx; *par2j=oldy; *par3j=oldz;
	  // theta=j*dtheta, phi=i*dphi
	  ++j;
	  x=X*std::cos(j*dtheta); y=X*std::sin(j*dtheta); z=Z;
	  iso3d(i1,i2,i3,j1,j2,j3,k1,k2,k3,x,y,z);
	  oldx=x; oldy=y; oldz=z;
	}
	glEnd();
      }
      else 
      {
	for (int j=0;j<ntheta;){
	  glBegin(mode);
	  glNormal3d(parallele1[j],parallele2[j],parallele3[j]);
	  if (texture)
	    glTexCoord2f(double(j)/(ntheta),double(i-1)/(nphi));
	  glVertex3d(c1+r*parallele1[j],c2+r*parallele2[j],c3+r*parallele3[j]);
	  if (texture)
	    glTexCoord2f(double(j)/(ntheta),double(i)/(nphi));
	  glVertex3d(c1+r*oldx,c2+r*oldy,c3+r*oldz);
	  parallele1[j]=oldx; parallele2[j]=oldy; parallele3[j]=oldz;
	  ++j;
	  // theta=j*dtheta, phi=i*dphi
	  x=X*std::cos(j*dtheta); y=X*std::sin(j*dtheta); z=Z;
	  iso3d(i1,i2,i3,j1,j2,j3,k1,k2,k3,x,y,z);
	  if (texture)
	    glTexCoord2f(double(j+1)/(ntheta),double(i-1)/(nphi));
	  glVertex3d(c1+r*x,c2+r*y,c3+r*z);
	  if (texture)
	    glTexCoord2f(double(j+1)/(ntheta),double(i)/(nphi));
	  glVertex3d(c1+r*parallele1[j],c2+r*parallele2[j],c3+r*parallele3[j]);
	  glEnd();
	  oldx=x; oldy=y; oldz=z;
	}
      }
      parallele1[ntheta]=oldx; parallele2[ntheta]=oldy; parallele3[ntheta]=oldz;
    }
    if (texture)
      glDisable(GL_TEXTURE_2D);
  }

  // if all z values of surfaceg are pure imaginary return a negative int
  // else return 1
  bool find_zscale(const gen & surface,double & zmin,double & zmax){
    if (surface.type!=_VECT)
      return true;
    if (surface.subtype!=_POLYEDRE__VECT && surface.subtype!=_GROUP__VECT && surface._VECTptr->size()==3){
      if (!is_zero(re(surface._VECTptr->back(),context0)))
	return false;
      gen s3=evalf_double(surface._VECTptr->back()/cst_i,2,context0);
      if (s3.type==_DOUBLE_){
	double s3d = s3._DOUBLE_val;
	if (s3d<zmin) zmin=s3d;
	if (s3d>zmax) zmax=s3d;
      }
      return true;
    }
    const_iterateur it=surface._VECTptr->begin(),itend=surface._VECTptr->end();
    for (;it!=itend;++it){
      if (!find_zscale(*it,zmin,zmax))
	return false;
    }
    return true;
  }

  void glsurface(const gen & surfaceg,int draw_mode,void * texture,GIAC_CONTEXT){
    if (!ckmatrix(surfaceg,true))
      return;
    test_enable_texture(texture);
    double realiscmplx=has_i(surfaceg),zmin=1e300,zmax=-1e300;
    matrice & surface = *surfaceg._VECTptr;
    if (realiscmplx && !texture){
      if (find_zscale(surface,zmin,zmax))
	realiscmplx=2*M_PI/(zmin-zmax);
    }
    int n=surface.size();
    if (surfaceg.subtype==_POLYEDRE__VECT){
      // implicit surface drawing with the given triangulation
      if (draw_mode==GL_QUADS || draw_mode==GL_TRIANGLES){
	glBegin(GL_TRIANGLES);
	for (int i=0;i<n;++i){
	  vecteur & v=*surface[i]._VECTptr;
	  const_iterateur it=v.begin();
	  double d1,d2,d3,e1,e2,e3,f1,f2,f3;
	  if (get_glvertex(*it,d1,d2,d3,realiscmplx,zmin,contextptr) &&
	      get_glvertex(*(it+1),e1,e2,e3,realiscmplx,zmin,contextptr) &&
	      get_glvertex(*(it+2),f1,f2,f3,realiscmplx,zmin,contextptr)){
	    glnormal(d1,d2,d3,e1,e2,e3,f1,f2,f3);
	    glVertex3d(d1,d2,d3);
	    glVertex3d(e1,e2,e3);
	    glVertex3d(f1,f2,f3);
	  }
	}
	glEnd();
      }
      else {
	glBegin(draw_mode);
	for (int i=0;i<n;++i){
	  vecteur & v=*surface[i]._VECTptr;
	  const_iterateur it=v.begin();
	  double d1,d2,d3,e1,e2,e3,f1,f2,f3;
	  if (get_glvertex(*it,d1,d2,d3,realiscmplx,zmin,contextptr) &&
	      get_glvertex(*(it+1),e1,e2,e3,realiscmplx,zmin,contextptr) &&
	      get_glvertex(*(it+2),f1,f2,f3,realiscmplx,zmin,contextptr)){
	    glnormal(d1,d2,d3,e1,e2,e3,f1,f2,f3);
	    glVertex3d(d1,d2,d3);
	    glVertex3d(e1,e2,e3);
	    glVertex3d(f1,f2,f3);
	    glVertex3d(d1,d2,d3);	    
	  }
	}
	glEnd();
      }
      if (texture)
	glDisable(GL_TEXTURE_2D);
      return;
    }
    if (surface.front()._VECTptr->size()<2){
      if (texture)
	glDisable(GL_TEXTURE_2D);
      return;
    }
    gen a,b,c,d;
    double xt=0,yt,dxt=double(1)/n,dyt;
    for (int i=1;i<n;++i,xt+=dxt){
      const vecteur & vprec=*surface[i-1]._VECTptr;
      const vecteur & v=*surface[i]._VECTptr;
      const_iterateur itprec=vprec.begin(),it=v.begin(),itend=v.end();
      yt=0; dyt=double(1)/(itend-it);
      a=*itprec; b=*it;
      double a1,a2,a3,b1,b2,b3,c1,c2,c3,d1,d2,d3;
      /*
      if (draw_mode==GL_QUADS){
	get_glvertex(b,b1,b2,b3,realiscmplx,zmin,contextptr);
	glBegin(GL_QUAD_STRIP);
	for (;it!=itend;yt+=dyt){
	  a=*itprec;
	  get_glvertex(a,a1,a2,a3,realiscmplx,zmin,contextptr);
	  ++it; ++itprec;
	  if (it==itend){
	    if (texture)
	      glTexCoord2f(xt,yt);
	    glVertex3f(a1,a2,a3);
	    glVertex3f(b1,b2,b3);
	    break;
	  }
	  b=*it;
	  get_glvertex(b,c1,c2,c3,realiscmplx,zmin,contextptr);
	  if (texture)
	    glTexCoord2f(xt,yt);
	  glnormal(a1,a2,a3,b1,b2,b3,c1,c2,c3);
	  glVertex3f(a1,a2,a3);
	  glVertex3f(b1,b2,b3);
	  b1=c1; b2=c2; b3=c3;
	}
	glEnd();
      }
      else 
      */
      {
	get_glvertex(a,a1,a2,a3,realiscmplx,zmin,contextptr);
	get_glvertex(b,b1,b2,b3,realiscmplx,zmin,contextptr);
	++it; ++itprec;
	for (;it!=itend;++it,++itprec,yt+=dyt){
	  c = *itprec;
	  d = *it;
	  get_glvertex(c,c1,c2,c3,realiscmplx,zmin,contextptr);
	  get_glvertex(d,d1,d2,d3,realiscmplx,zmin,contextptr);
	  glBegin(draw_mode);
	  if (texture)
	    glTexCoord2f(xt,yt);
	  glnormal(a1,a2,a3,b1,b2,b3,c1,c2,c3);
	  glVertex3f(a1,a2,a3); // itprec
	  glVertex3f(b1,b2,b3); // it
	  glVertex3f(d1,d2,d3); // it
	  glVertex3f(c1,c2,c3); // itprec
	  glEnd();
	  if (draw_mode==GL_QUADS){
	    glnormal(a1,a2,a3,c1,c2,c3,b1,b2,b3);
	    glBegin(draw_mode);
	    glVertex3f(a1,a2,a3);
	    glVertex3f(c1,c2,c3);
	    glVertex3f(d1,d2,d3);
	    glVertex3f(b1,b2,b3);
	    glEnd();
	  }
	  a=c; a1=c1; a2=c2; a3=c3;
	  b=d; b1=d1; b2=d2; b3=d3;
	}
      }
    }
    if (texture)
      glDisable(GL_TEXTURE_2D);
  }

  // surface without grid evaluation, should not happen!
  void glsurface(const vecteur & point,const gen & uv,double umin,double umax,double vmin,double vmax,int nu,int nv,int draw_mode,GIAC_CONTEXT){
    double u=umin,v=vmin,deltau=(umax-umin)/nu,deltav=(vmax-vmin)/nv;
    vecteur prevline(nv+1); //gen prevline[nv+1];//,line[nv+1];
    vecteur curuv(2);
    gen old,current;
    curuv[0]=u;
    for (int j=0;j<=nv;j++,v += deltav){
      curuv[1] = v;
      prevline[j]=subst(point,uv,curuv,false,contextptr);
    }
    u += deltau;
    for (int i=1;i<=nu;i++,u+=deltau){
      v=vmin;
      curuv = makevecteur(u,v);
      v += deltav;
      old = subst(point,uv,curuv,false,contextptr);
      for (int j=0;j<nv;v +=deltav){
	glBegin(draw_mode);
	curuv[1] = v;
	current = subst(point,uv,curuv,false,contextptr);
	glvertex(*prevline[j]._VECTptr,0,0,contextptr);
	prevline[j]=old;
	glvertex(*old._VECTptr,0,0,contextptr);
	++j;
	glvertex(*current._VECTptr,0,0,contextptr);
	glvertex(*prevline[j]._VECTptr,0,0,contextptr);
	old=current;
	glEnd();
      }
      prevline[nv]=old;
      /* for (int j=0;j<=nv;++j)
	 cerr << prevline[j] << " " ;
	cerr << endl;
      */
    }
  }

  unsigned int line_stipple(unsigned int i){
    switch (i){
    case 1: case 4:
      return 0xf0f0;
    case 2: case 5:
      return 0xcccc;
    case 3: case 6:
      return 0xaaaa;
    default:
      return 0xffff;
    }
  }

  bool is_approx_zero(const gen & dP,double window_xmin,double window_xmax,double window_ymin,double window_ymax,double window_zmin,double window_zmax){
    bool closed=false;
    if (dP.type==_VECT && dP._VECTptr->size()==3){
      closed=true;
      double dPx=evalf_double(dP[0],2,context0)._DOUBLE_val;
      if (fabs(dPx)>(window_xmax-window_xmin)*1e-6)
	closed=false;
      double dPy=evalf_double(dP[1],2,context0)._DOUBLE_val;
      if (fabs(dPy)>(window_ymax-window_ymin)*1e-6)
	closed=false;
      double dPz=evalf_double(dP[2],2,context0)._DOUBLE_val;
      if (fabs(dPz)>(window_zmax-window_zmin)*1e-6)
	closed=false;
    }
    return closed;
  }

  // translate giac GL constant to open GL constant
  unsigned gl_translate(unsigned i){
    switch (i){
    case _GL_LIGHT0:
      return GL_LIGHT0;
    case _GL_LIGHT1:
      return GL_LIGHT1;
    case _GL_LIGHT2:
      return GL_LIGHT2;
    case _GL_LIGHT3:
      return GL_LIGHT3;
    case _GL_LIGHT4:
      return GL_LIGHT4;
    case _GL_LIGHT5:
      return GL_LIGHT5;
    case _GL_AMBIENT:
      return GL_AMBIENT;
    case _GL_SPECULAR:
      return GL_SPECULAR;
    case _GL_DIFFUSE:
      return GL_DIFFUSE;
    case _GL_POSITION:
      return GL_POSITION;
    case _GL_SPOT_DIRECTION:
      return GL_SPOT_DIRECTION;
    case _GL_SPOT_EXPONENT:
      return GL_SPOT_EXPONENT;
    case _GL_SPOT_CUTOFF:
      return GL_SPOT_CUTOFF;
    case _GL_CONSTANT_ATTENUATION:
      return GL_CONSTANT_ATTENUATION;
    case _GL_LINEAR_ATTENUATION:
      return GL_LINEAR_ATTENUATION;
    case _GL_QUADRATIC_ATTENUATION:
      return GL_QUADRATIC_ATTENUATION;
    case _GL_LIGHT_MODEL_AMBIENT:
      return GL_LIGHT_MODEL_AMBIENT;
    case _GL_LIGHT_MODEL_LOCAL_VIEWER:
      return GL_LIGHT_MODEL_LOCAL_VIEWER;
    case _GL_LIGHT_MODEL_TWO_SIDE:
      return GL_LIGHT_MODEL_TWO_SIDE;
#ifndef WIN32
    case _GL_LIGHT_MODEL_COLOR_CONTROL:
      return GL_LIGHT_MODEL_COLOR_CONTROL;
#endif
    case _GL_SMOOTH:
      return GL_SMOOTH;
    case _GL_FLAT:
      return GL_FLAT;
    case _GL_SHININESS:
      return GL_SHININESS;
    case _GL_FRONT:
      return GL_FRONT;
    case _GL_BACK:
      return GL_BACK;
    case _GL_FRONT_AND_BACK:
      return GL_FRONT_AND_BACK;
    case _GL_AMBIENT_AND_DIFFUSE:
      return GL_AMBIENT_AND_DIFFUSE;
    case _GL_EMISSION:
      return GL_EMISSION;
#ifndef WIN32
    case _GL_SEPARATE_SPECULAR_COLOR:
      return GL_SEPARATE_SPECULAR_COLOR;
    case _GL_SINGLE_COLOR:
      return GL_SINGLE_COLOR;
#endif
    case _GL_BLEND:
      return GL_BLEND;
    case _GL_SRC_ALPHA:
      return GL_SRC_ALPHA;
    case _GL_ONE_MINUS_SRC_ALPHA:
      return GL_ONE_MINUS_SRC_ALPHA;
    case _GL_COLOR_INDEXES:
      return GL_COLOR_INDEXES;
    }
    cerr << "No GL equivalent for " << i << endl;
    return i;
  }

  void tran4(double * colmat){
    giac::swapdouble(colmat[1],colmat[4]);
    giac::swapdouble(colmat[2],colmat[8]);
    giac::swapdouble(colmat[3],colmat[12]);
    giac::swapdouble(colmat[6],colmat[9]);
    giac::swapdouble(colmat[7],colmat[13]);
    giac::swapdouble(colmat[11],colmat[14]);    
  }

  void get_texture(const gen & attrv1,void * & texture){
#if 0
    // set texture
    if (attrv1.type==_STRNG){
      std::map<std::string,Fl_Image *>::const_iterator it,itend=texture_cache.end();
      it=texture_cache.find(*attrv1._STRNGptr);
      if (it!=itend){
	texture=it->second;
	// texture->uncache();
      }
      else {
	texture=Fl_Shared_Image::get(attrv1._STRNGptr->c_str());
	if (texture){
	  int W=texture->w(),H=texture->h();
	  // take a power of 2 near w/h
	  W=1 << min(int(std::log(double(W))/std::log(2.)+.5),8);
	  H=1 << min(int(std::log(double(H))/std::log(2.)+.5),8);
	  texture=texture->copy(W,H);
	  texture_cache[*attrv1._STRNGptr]=texture;
	}
      }
    }
#endif
  }

  int gen2int(const gen & g){
    if (g.type==_INT_)
      return g.val;
    if (g.type==_DOUBLE_)
      return int(g._DOUBLE_val);
    setsizeerr(gettext("Unable to convert to int")+g.print());
    return -1;
  }

  void Opengl3d::indraw(const giac::gen & g){
    if (g.type==_VECT)
      indraw(*g._VECTptr);
    if (g.is_symb_of_sommet(at_animation)){
      indraw(get_animation_pnt(g,animation_instructions_pos));
      return;
    }
    if (!g.is_symb_of_sommet(at_pnt)){
      update_infos(g);
      return;
    }
    gen & f=g._SYMBptr->feuille;
    if (f.type!=_VECT)
      return;
    vecteur & v = *f._VECTptr;
    gen v0=v[0];
    v0=evalf(v0,1,contextptr);
    bool est_hyperplan=v0.is_symb_of_sommet(at_hyperplan);
    string legende;
    vecteur style(get_style(v,legende));
    int styles=style.size();
    // color
    bool hidden_name = false;
    int ensemble_attributs=style.front().val;
    if (style.front().type==_ZINT){
      ensemble_attributs = mpz_get_si(*style.front()._ZINTptr);
      hidden_name=true;
    }
    else
      hidden_name=ensemble_attributs<0;
    int couleur=ensemble_attributs & 0x0000ff;
    int width           =(ensemble_attributs & 0x00070000) >> 16; // 3 bits
    int epaisseur_point =((ensemble_attributs & 0x00380000) >> 19)+1; // 3 bits
    int type_line       =(ensemble_attributs & 0x01c00000) >> 22; // 3 bits
    int type_point      =(ensemble_attributs & 0x0e000000) >> 25; // 3 bits
    int labelpos        =(ensemble_attributs & 0x30000000) >> 28; // 2 bits
    bool fill_polygon   =(ensemble_attributs & 0x40000000) >> 30;
    hidden_name = hidden_name || legende.empty();
    void * texture=0;
    glLineWidth(width+1);
#if 0
    // FIXME line_stipple disabled because of printing
    glLineStipple(1,line_stipple(type_line));
    glPointSize(epaisseur_point);
    if (styles<=2){
      glEnable(GL_COLOR_MATERIAL);
      glColorMaterial(GL_FRONT,GL_AMBIENT_AND_DIFFUSE);
    }
    else 
      glDisable(GL_COLOR_MATERIAL);
#ifndef EMCC
    GLfloat tab[4]={0,0,0,1};
    glMaterialfv(GL_FRONT_AND_BACK,GL_EMISSION,tab);
    glMaterialf(GL_FRONT_AND_BACK,GL_SHININESS,50);
    glMaterialfv(GL_FRONT_AND_BACK,GL_SPECULAR,tab);
    GLfloat tab1[4]={0.2,0.2,0.2,1};
    glMaterialfv(GL_FRONT_AND_BACK,GL_AMBIENT,tab1);
    GLfloat tab2[4]={0.8,0.8,0.8,1};
    glMaterialfv(GL_FRONT_AND_BACK,GL_DIFFUSE,tab2);
#endif
    if (est_hyperplan){
      glPolygonMode(GL_FRONT_AND_BACK,fill_polygon?GL_FILL:GL_LINE);
    }
    else {
      glPolygonMode(GL_FRONT_AND_BACK,fill_polygon?GL_FILL:GL_LINE);
      // glPolygonMode(GL_BACK,GL_POINT);
      // glMaterialfv(GL_BACK,GL_EMISSION,tab);
      // glMaterialfv(GL_BACK,GL_SPECULAR,tab);
      // glMaterialfv(GL_BACK,GL_AMBIENT_AND_DIFFUSE,tab);
    }
    // glGetFloatv(GL_CURRENT_COLOR,tab);

    // set other style attributs : 
    /* material = [gl_front|gl_back|gl_front_and_back,gl_shininess,valeur] or
       material = [gl_front|gl_back|gl_front_and_back, GL_AMBIENT| GL_DIFFUSE |
       GL_SPECULAR | GL_EMISSION | GL_AMBIENT_AND_DIFFUSE | GL_COLOR_INDEXES,
      [r,g,b,a] ]
      last arg is [ambient,diffuse,specular] for GL_COLOR_INDEXES
      or
      material=[gl_texture,"image_filename",...]
    */
    for (int i=2;i<styles;++i){
      gen & attr=style[i];
      if (attr.type==_VECT){
	vecteur & attrv=*attr._VECTptr;
	if (attrv.size()>=2 && attrv.front().type==_INT_ && attrv.front().val==_GL_TEXTURE){
	  get_texture(attrv[1],texture);
	  continue;
	}
	if (attrv.size()==2 && attrv.front().type==_INT_ && attrv.front().val==_GL_MATERIAL){
	  gen attrm =evalf_double(attrv.back(),1,contextptr);
	  if (debug_infolevel)
	    cerr << "Setting material " << attrm << endl;
	  if (attrm.type==_VECT && attrm._VECTptr->size()<=3 ){
	    gen attrv0=attrv.back()._VECTptr->front();
	    if (attrv0.type==_INT_ && attrv0.val==_GL_TEXTURE){
	      gen attrv1=(*attrv.back()._VECTptr)[1];
	      get_texture(attrv1,texture);
	      continue;
	    }
	    vecteur & attrmv = *attrm._VECTptr;
	    if (attrmv.back().type==_VECT && attrmv.back()._VECTptr->size()==4){
	      vecteur & w=*attrmv.back()._VECTptr;
	      GLfloat tab[4]={w[0]._DOUBLE_val,w[1]._DOUBLE_val,w[2]._DOUBLE_val,w[3]._DOUBLE_val};
	      glMaterialfv(gl_translate(gen2int(attrmv[0])),gl_translate(gen2int(attrmv[1])),tab);
	      continue;
	    }
	    if (attrmv.back().type==_VECT && attrmv.back()._VECTptr->size()==3){
	      vecteur & w=*attrmv.back()._VECTptr;
	      GLfloat tab[3]={w[0]._DOUBLE_val,w[1]._DOUBLE_val,w[2]._DOUBLE_val};
	      glMaterialfv(gl_translate(gen2int(attrmv[0])),GL_COLOR_INDEXES,tab);
	      continue;
	    }
	    if (attrmv.back().type==_DOUBLE_ && attrmv[1]._DOUBLE_val==_GL_SHININESS){
	      // glMaterialf(gl_translate(gen2int(attrmv[0])),GL_SHININESS,float(attrmv[2]._DOUBLE_val));
	      glMaterialf(GL_FRONT_AND_BACK,GL_SHININESS,float(attrmv[2]._DOUBLE_val));
	      continue;
	    }
	  }
	}
      }
    }
    if (texture){
      fill_polygon=true;
      couleur=FL_WHITE;
      glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
    }
#endif
    bool hidden_line = fill_polygon && (width==7 || (display_mode & 0x8) || texture );
    xcas_color(couleur,true);
    if (debug_infolevel){
      cerr << "opengl displaying " << g << endl;
      GLint b;
      GLfloat posf[4],direcf[4],ambient[4],diffuse[4],specular[4],emission[4],shini[1];
      GLfloat expo,cutoff;
      double pos[4],direc[4];
      glGetIntegerv(GL_BLEND,&b);
      cerr << "blend " << b << endl;
      for (int i=0;i<8;++i){
	glGetIntegerv(GL_LIGHT0+i,&b);
	if (b){
	  glGetLightfv(GL_LIGHT0+i,GL_SPOT_EXPONENT,&expo);
	  glGetLightfv(GL_LIGHT0+i,GL_SPOT_CUTOFF,&cutoff);
	  glGetLightfv(GL_LIGHT0+i,GL_POSITION,posf);
	  glGetLightfv(GL_LIGHT0+i,GL_SPOT_DIRECTION,direcf);
	  direcf[3]=0;
	  mult4(model_inv,posf,pos);
	  tran4(model);
	  mult4(model,direcf,direc);
	  tran4(model);
	  glGetLightfv(GL_LIGHT0+i,GL_AMBIENT,ambient);
	  glGetLightfv(GL_LIGHT0+i,GL_DIFFUSE,diffuse);
	  glGetLightfv(GL_LIGHT0+i,GL_SPECULAR,specular);
	  cerr << "light " << i << ": " <<
	    " pos " << pos[0] << "," << pos[1] << "," << pos[2] << "," << pos[3] << 
	    " dir " << direc[0] << "," << direc[1] << "," << direc[2] << "," << direc[3] << 
	    " ambient " << ambient[0] << "," << ambient[1] << "," << ambient[2] << "," << ambient[3] << 
	    " diffuse " << diffuse[0] << "," << diffuse[1] << "," << diffuse[2] << "," << diffuse[3] << 
	    " specular " << specular[0] << "," << specular[1] << "," << specular[2] << "," << specular[3] << 
	    " exponent " << expo << " cutoff " << cutoff << endl;
	}
      }
      // material colors
      glGetMaterialfv(GL_FRONT,GL_AMBIENT,ambient);
      glGetMaterialfv(GL_FRONT,GL_DIFFUSE,diffuse);
      glGetMaterialfv(GL_FRONT,GL_SPECULAR,specular);
      glGetMaterialfv(GL_FRONT,GL_EMISSION,emission);
      glGetMaterialfv(GL_FRONT,GL_SHININESS,shini);
      cerr << "front " << ": " <<
	" ambient " << ambient[0] << "," << ambient[1] << "," << ambient[2] << "," << ambient[3] << 
	" diffuse " << diffuse[0] << "," << diffuse[1] << "," << diffuse[2] << "," << diffuse[3] << 
	" specular " << specular[0] << "," << specular[1] << "," << specular[2] << "," << specular[3] << 
	" emission " << emission[0] << "," << emission[1] << "," << emission[2] << "," << emission[3] << 
	" shininess " << shini[0] << endl;
      glGetMaterialfv(GL_BACK,GL_AMBIENT,ambient);
      glGetMaterialfv(GL_BACK,GL_DIFFUSE,diffuse);
      glGetMaterialfv(GL_BACK,GL_SPECULAR,specular);
      glGetMaterialfv(GL_BACK,GL_EMISSION,emission);
      glGetMaterialfv(GL_BACK,GL_SHININESS,shini);
      cerr << "back " << ": " <<
	" ambient " << ambient[0] << "," << ambient[1] << "," << ambient[2] << "," << ambient[3] << 
	" diffuse " << diffuse[0] << "," << diffuse[1] << "," << diffuse[2] << "," << diffuse[3] << 
	" specular " << specular[0] << "," << specular[1] << "," << specular[2] << "," << specular[3] << 
	" emission " << emission[0] << "," << emission[1] << "," << emission[2] << "," << emission[3] << 
	" shininess " << shini[0] << endl;	
    }
    if (est_hyperplan){
      vecteur P,n;
      if (!hyperplan_normal_point(v0,n,P))
	return;
      P=*evalf_double(P,1,contextptr)._VECTptr;
      vecteur Porig(P);
      double n1=evalf_double(n[0],1,contextptr)._DOUBLE_val;
      double n2=evalf_double(n[1],1,contextptr)._DOUBLE_val;
      double n3=evalf_double(n[2],1,contextptr)._DOUBLE_val;
      if (fill_polygon){
	glNormal3d(n1,n2,n3);
	if (std::abs(n1)>=std::abs(n2) && std::abs(n1)>=std::abs(n3)){
	  // x=a*y+b*z+c
	  double a=-n2/n1, b=-n3/n1;
	  double c=evalf_double(P[0]-a*P[1]-b*P[2],1,contextptr)._DOUBLE_val;
	  double dy=(window_ymax-window_ymin)/10;
	  double dz=(window_zmax-window_zmin)/10;
	  for (int j=0;j<10;++j){
	    double y=window_ymin+j*dy;
	    for (int k=0;k<10;++k){
	      double z=window_zmin+k*dz;
	      double x=a*y+b*z+c;
	      if (x>window_xmax || x<window_xmin)
		continue;
	      glBegin(GL_QUADS);
	      glVertex3d(x,y,z);
	      glVertex3d(x+a*dy,y+dy,z);
	      glVertex3d(x+a*dy+b*dz,y+dy,z+dz);
	      glVertex3d(x+b*dz,y,z+dz);
	      glEnd();
	    }
	  }
	}
	if (std::abs(n2)>std::abs(n1) && std::abs(n2)>=std::abs(n3)){
	  // y=a*x+b*z+c
	  double a=-n1/n2, b=-n3/n2;
	  double c=evalf_double(P[1]-a*P[0]-b*P[2],1,contextptr)._DOUBLE_val;
	  double dx=(window_xmax-window_xmin)/10;
	  double dz=(window_zmax-window_zmin)/10;
	  for (int j=0;j<10;++j){
	    double x=window_xmin+j*dx;
	    for (int k=0;k<10;++k){
	      double z=window_zmin+k*dz;
	      double y=a*x+b*z+c;
	      if (y>window_ymax || y<window_ymin)
		continue;
	      glBegin(GL_QUADS);
	      glVertex3d(x,y,z);
	      glVertex3d(x+dx,y+a*dx,z);
	      glVertex3d(x+dx,y+a*dx+b*dz,z+dz);
	      glVertex3d(x,y+b*dz,z+dz);
	      glEnd();
	    }
	  }
	}
	if (std::abs(n3)>std::abs(n1) && std::abs(n3)>std::abs(n2)){
	  // z=a*x+b*y+c
	  double a=-n1/n3, b=-n2/n3;
	  double c=evalf_double(P[2]-a*P[0]-b*P[1],1,contextptr)._DOUBLE_val;
	  double dx=(window_xmax-window_xmin)/10;
	  double dy=(window_ymax-window_ymin)/10;
	  for (int j=0;j<10;++j){
	    double x=window_xmin+j*dx;
	    for (int k=0;k<10;++k){
	      double y=window_ymin+k*dy;
	      double z=a*x+b*y+c;
	      if (z>window_zmax || z<window_zmin)
		continue;
	      glBegin(GL_QUADS);
	      glVertex3d(x,y,z);
	      glVertex3d(x+dx,y,z+a*dx);
	      glVertex3d(x+dx,y+dy,z+a*dx+b*dy);
	      glVertex3d(x,y+dy,z+b*dy);
	      glEnd();
	    }
	  }
	}
      } // end fill_polygon
      else { // use equation
	glNormal3d(n1,n2,n3);
	if (std::abs(n1)>=std::abs(n2) && std::abs(n1)>=std::abs(n3)){
	  // x=a*y+b*z+c
	  double a=-n2/n1, b=-n3/n1;
	  double c=evalf_double(P[0]-a*P[1]-b*P[2],1,contextptr)._DOUBLE_val;
	  double dy=(window_ymax-window_ymin)/10;
	  for (int j=0;j<=10;++j){
	    double y=window_ymin+j*dy;
	    glBegin(GL_LINES);
	    glVertex3d(a*y+b*window_zmin+c,y,window_zmin);
	    glVertex3d(a*y+b*window_zmax+c,y,window_zmax);
	    glEnd();
	  }
	  double dz=(window_zmax-window_zmin)/10;
	  for (int j=0;j<=10;++j){
	    double z=window_zmin+j*dz;
	    glBegin(GL_LINES);
	    glVertex3d(a*window_ymin+b*z+c,window_ymin,z);
	    glVertex3d(a*window_ymax+b*z+c,window_ymax,z);
	    glEnd();
	  }
	}
	if (std::abs(n2)>std::abs(n1) && std::abs(n2)>=std::abs(n3)){
	  // y=a*x+b*z+c
	  double a=-n1/n2, b=-n3/n2;
	  double c=evalf_double(P[1]-a*P[0]-b*P[2],1,contextptr)._DOUBLE_val;
	  double dx=(window_xmax-window_xmin)/10;
	  for (int j=0;j<=10;++j){
	    double x=window_xmin+j*dx;
	    glBegin(GL_LINES);
	    glVertex3d(x,a*x+b*window_zmin+c,window_zmin);
	    glVertex3d(x,a*x+b*window_zmax+c,window_zmax);
	    glEnd();
	  }
	  double dz=(window_zmax-window_zmin)/10;
	  for (int j=0;j<=10;++j){
	    double z=window_zmin+j*dz;
	    glBegin(GL_LINES);
	    glVertex3d(window_xmin,a*window_xmin+b*z+c,z);
	    glVertex3d(window_xmax,a*window_xmax+b*z+c,z);
	    glEnd();
	  }
	}
	if (std::abs(n3)>std::abs(n1) && std::abs(n3)>std::abs(n2)){
	  // z=a*x+b*y+c
	  double a=-n1/n3, b=-n2/n3;
	  double c=evalf_double(P[2]-a*P[0]-b*P[1],1,contextptr)._DOUBLE_val;
	  double dx=(window_xmax-window_xmin)/10;
	  for (int j=0;j<=10;++j){
	    double x=window_xmin+j*dx;
	    glBegin(GL_LINES);
	    glVertex3d(x,window_ymin,a*x+b*window_ymin+c);
	    glVertex3d(x,window_ymax,a*x+b*window_ymax+c);
	    glEnd();
	  }
	  double dy=(window_ymax-window_ymin)/10;
	  for (int j=0;j<=10;++j){
	    double y=window_ymin+j*dy;
	    glBegin(GL_LINES);
	    glVertex3d(window_xmin,y,a*window_xmin+b*y+c);
	    glVertex3d(window_xmax,y,a*window_xmax+b*y+c);
	    glEnd();
	  }
	}
      }
      if (!hidden_name && show_names) 
	legende_draw(Porig,legende,labelpos);
      return;
    }
    if (v0.is_symb_of_sommet(at_hypersphere)){
      gen & f=v0._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()>=2){
	vecteur & v=*f._VECTptr;
	// Check that center is a 3-d point
	if (v.front().type==_VECT && v.front()._VECTptr->size()==3){
	  // Radius
	  gen r=v[1];
	  if (r.type==_VECT && r._VECTptr->size()==3)
	    r=l2norm(*r._VECTptr,contextptr); 
	  // Direction of axis, parallels and meridiens
	  vecteur dir1(makevecteur(1,0,0)),dir2(makevecteur(0,1,0)),dir3(makevecteur(0,0,1));
	  if (v.size()>=3 && v[2].type==_VECT && v[2]._VECTptr->size()==3){
	    dir3=*v[2]._VECTptr;
	    dir3=divvecteur(dir3,sqrt(dotvecteur(dir3,dir3),contextptr));
	    if (v.size()>=4 && v[3].type==_VECT && v[3]._VECTptr->size()==3){
	      dir1=*v[3]._VECTptr;
	      dir1=divvecteur(dir1,sqrt(dotvecteur(dir1,dir1),contextptr));
	    }
	    else {
	      if (!is_zero(dir3[0]) || !is_zero(dir3[1]) ){
		dir1=makevecteur(-dir3[1],dir3[0],0);
		dir1=divvecteur(dir1,sqrt(dotvecteur(dir1,dir1),contextptr));
	      }
	    }
	    dir2=cross(dir3,dir1,contextptr);
	  }
	  // optional discretisation info for drawing
	  if (v.size()>=5 && v[4].type==_INT_){
	    nphi=giac_max(absint(v[4].val),3);
	    ntheta=ntheta;
	  }
	  if (v.size()>=6 && v[5].type==_INT_){
	    ntheta=giac_max(absint(v[5].val),3);
	  }
	  // Now make the sphere
	  if (fill_polygon){
	    glsphere(*v.front()._VECTptr,r,dir1,dir2,dir3,ntheta,nphi,GL_QUADS,texture,contextptr);
	  }
	  if (!hidden_line){
	    if (fill_polygon)
	      glColor3f(0,0,0);
	    glsphere(*v.front()._VECTptr,r,dir1,dir2,dir3,giacmin(ntheta,36),giacmin(nphi,36),GL_LINE_LOOP,texture,contextptr);
	    xcas_color(couleur,true);
	  }
	  if (!hidden_name && show_names) legende_draw(v.front(),legende,labelpos);
	}
      }
      return;
    }
    if (v0.is_symb_of_sommet(at_curve) && v0._SYMBptr->feuille.type==_VECT && !v0._SYMBptr->feuille._VECTptr->empty()){
      gen f = v0._SYMBptr->feuille._VECTptr->front();
      // f = vect[ pnt,var,xmin,xmax ]
      if (f.type==_VECT && f._VECTptr->size()>=4){
	vecteur vf = *f._VECTptr;
	if (vf.size()>4){
	  gen poly=vf[4];
	  if (ckmatrix(poly)){
	    const_iterateur it=poly._VECTptr->begin(),itend=poly._VECTptr->end();
	    bool closed=fill_polygon && !(display_mode & 0x8); // ?is_approx_zero(poly._VECTptr->back()-poly._VECTptr->front(),window_xmin,window_xmax,window_ymin,window_ymax,window_zmin,window_zmax):false;
	    if (it->_VECTptr->size()==3){
	      glBegin(closed?GL_POLYGON:GL_LINE_STRIP);
	      // if (closed) ++it;
	      for (;it!=itend;++it){
		if (!glvertex(*it->_VECTptr,0,0,contextptr)){
		  glEnd();
		  glBegin(closed?GL_POLYGON:GL_LINE_STRIP);
		}
	      }
	      glEnd();
	      if (!hidden_name && show_names && !poly._VECTptr->empty()) legende_draw(poly._VECTptr->front(),legende,labelpos);
	      return ;
	    }
	  }
	}
	gen point=vf[0];
	gen var=vf[1];
	gen mini=vf[2];
	gen maxi=vf[3];
	bool closed=false;
	if (fill_polygon && !(display_mode & 0x8) )
	  closed=is_approx_zero(subst(point,var,mini,false,contextptr)-subst(point,var,maxi,false,contextptr),window_xmin,window_xmax,window_ymin,window_ymax,window_zmin,window_zmax);
	int n=nphi*10;
	gen delta=(maxi-mini)/n;
	COUT << "rendering curve " << vf << " " << delta << " " << n << endl;
	for (int i=closed?1:0;i<n;++i){
	  glBegin(GL_LINES);
	  gen tmp=subst(point,var,mini,false,contextptr);
	  mini = mini + delta;
	  if (tmp.type==_VECT && glvertex(*tmp._VECTptr,0,0,contextptr)){
	    gen tmp1=subst(point,var,mini,false,contextptr);
	    if (tmp1.type==_VECT)
	      glvertex(*tmp1._VECTptr,0,0,contextptr);
	    else {
	      glvertex(*tmp._VECTptr,0,0,contextptr);
	      glEnd();
	      COUT << "rendering err0 " << point << " " << var << " " << mini << " -> " << tmp << endl;
	      return;
	    }
	  }
	  else {
	    glvertex(makevecteur(0,0,0),0,0,contextptr);
	    glvertex(makevecteur(0,0,0),0,0,contextptr);
	    glEnd();
	    COUT << "rendering err1 " << point << " " << var << " " << mini << " -> " << tmp << endl;
	    return;
	  }
	  glEnd();
	}
	if (!hidden_name && show_names) legende_draw(mini,legende,labelpos);
      }
      return;
    }
    if (v0.is_symb_of_sommet(at_hypersurface)){
      gen & f = v0._SYMBptr->feuille;
      if (f.type!=_VECT || f._VECTptr->size()<3)
	return;
      gen & tmp = f._VECTptr->front();
      if (tmp.type!=_VECT || tmp._VECTptr->size()<4)
	return;
      if (tmp._VECTptr->size()>4){
	if (!fill_polygon)
	  glsurface((*tmp._VECTptr)[4],GL_LINE_LOOP,texture,contextptr);
	else {
	  glsurface((*tmp._VECTptr)[4],GL_QUADS,texture,contextptr);
	  // glLineWidth(width+2);
	  if (!hidden_line){
	    glColor3f(0,0,0);
	    glsurface((*tmp._VECTptr)[4],GL_LINE_LOOP,texture,contextptr);
	    xcas_color(couleur,true);
	  }
	}
	// if (!hidden_name && show_names) legende_draw(legende.c_str());
	return;
      }
      gen point = tmp._VECTptr->front(); // [x(u,v),y(u,v),z(u,v)]
      gen vars = (*tmp._VECTptr)[1]; // [u,v]
      gen mini = (*tmp._VECTptr)[2]; // [umin,vmin]
      gen maxi = (*tmp._VECTptr)[3]; // [umax,vmax]
      if (!check3dpoint(point) || 
	  vars.type!=_VECT || vars._VECTptr->size()!=2 
	  || mini.type!=_VECT || mini._VECTptr->size()!=2 
	  || maxi.type!=_VECT || maxi._VECTptr->size()!=2 )
	return;
      double umin=evalf_double(mini._VECTptr->front(),1,contextptr)._DOUBLE_val;
      double vmin=evalf_double(mini._VECTptr->back(),1,contextptr)._DOUBLE_val;
      double umax=evalf_double(maxi._VECTptr->front(),1,contextptr)._DOUBLE_val;
      double vmax=evalf_double(maxi._VECTptr->back(),1,contextptr)._DOUBLE_val;
      if (fill_polygon){
	glsurface(*point._VECTptr,vars,umin,umax,vmin,vmax,ntheta,nphi,GL_QUADS,contextptr);
	if (!hidden_line){
	  glColor3f(0,0,0);
	  glsurface(*point._VECTptr,vars,umin,umax,vmin,vmax,ntheta,nphi,GL_LINE_LOOP,contextptr);
	  xcas_color(couleur,true);
	}
      }
      else
	glsurface(*point._VECTptr,vars,umin,umax,vmin,vmax,ntheta,nphi,GL_LINE_LOOP,contextptr);
      // if (!hidden_name && show_names) legende_draw(?,legende,labelpos);
      return;
    }
    if (v0.type==_VECT && v0.subtype==_POINT__VECT && v0._VECTptr->size()==3 ){
      gen A(evalf_double(v0,1,contextptr));
      if (A.type==_VECT && A._VECTptr->size()==3 && type_point!=4){
	double xA=A._VECTptr->front()._DOUBLE_val;
	double yA=(*A._VECTptr)[1]._DOUBLE_val;
	double zA=A._VECTptr->back()._DOUBLE_val;
	double iA,jA,depthA;
	find_ij(xA,yA,zA,iA,jA,depthA);
	// COUT << "point " << type_point << "," << xA << "," << yA << "," << zA << "," << iA << "," << jA << "," << depthA << endl;
	glLineWidth(1+epaisseur_point/2);
	switch(type_point){ 
	case 0:
	  glBegin(GL_LINES);
	  find_xyz(iA-epaisseur_point,jA-epaisseur_point,depthA,xA,yA,zA);
	  // COUT << xA << "," << yA << "," << zA << endl;
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA+epaisseur_point,jA+epaisseur_point,depthA,xA,yA,zA);
	  // COUT << xA << "," << yA << "," << zA << endl;
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA-epaisseur_point,jA+epaisseur_point,depthA,xA,yA,zA);
	  // COUT << xA << "," << yA << "," << zA << endl;
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA+epaisseur_point,jA-epaisseur_point,depthA,xA,yA,zA);
	  // COUT << xA << "," << yA << "," << zA << endl;
	  glVertex3d(xA,yA,zA);
	  glEnd();
	  break;
	case 1:
	  // 1 losange, 
	  glBegin(GL_LINE_LOOP);
	  find_xyz(iA-epaisseur_point,jA,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA,jA+epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA+epaisseur_point,jA,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA,jA-epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  glEnd();
	  break;
	case 2:	  // 2 croix verticale, 
	  glBegin(GL_LINES);
	  find_xyz(iA,jA-epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA,jA+epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA-epaisseur_point,jA,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA+epaisseur_point,jA,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  glEnd();
	  break;
	case 3: // 3 carre. 
	  glBegin(GL_LINE_LOOP);
	  find_xyz(iA-epaisseur_point,jA-epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA+epaisseur_point,jA-epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA+epaisseur_point,jA+epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA-epaisseur_point,jA+epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  glEnd();
	  break;
	case 5: // 5 triangle, 
	  glBegin(GL_LINE_LOOP);
	  find_xyz(iA+epaisseur_point,jA-epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA+epaisseur_point,jA+epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA-epaisseur_point,jA,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  glEnd();
	  break;
	case 6:  // 6 etoile, 
	  glBegin(GL_LINES);
	  find_xyz(iA,jA-epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA,jA+epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA-epaisseur_point/2,jA+epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA+epaisseur_point/2,jA-epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA-epaisseur_point/2,jA-epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  find_xyz(iA+epaisseur_point/2,jA+epaisseur_point,depthA,xA,yA,zA);
	  glVertex3d(xA,yA,zA);
	  glEnd();
	  break;
	default: 	  // 7 point
	  glBegin(GL_POINTS);
	  glvertex(*v0._VECTptr,0,0,contextptr);
	  glEnd();
	}
	glLineWidth(width+1);
      }
      if (!hidden_name && show_names) 
	legende_draw(v0,legende,labelpos);
      return;
    }
    if (v0.type==_VECT && v0.subtype!=_POINT__VECT){
      vecteur & vv0=*v0._VECTptr;
      vecteur w,lastpnt;
      if (v0.subtype==_POLYEDRE__VECT){
	// each element of v is a face 
	const_iterateur it=vv0.begin(),itend=vv0.end();
	for (;it!=itend;++it){
	  if (it->type==_VECT){ 
	    w = *evalf_double(*it,1,contextptr)._VECTptr;
	    int s=w.size();
	    if (s<3)
	      continue;
	    double d1,d2,d3,e1,e2,e3,f1,f2,f3;	    
	    if (get_glvertex(w[0],d1,d2,d3,0,0,contextptr) &&
		get_glvertex(w[1],e1,e2,e3,0,0,contextptr) &&
		get_glvertex(w[2],f1,f2,f3,0,0,contextptr) ){
	      glnormal(d1,d2,d3,e1,e2,e3,f1,f2,f3);
	      if (fill_polygon){
		glBegin(GL_POLYGON);
		glVertex3d(d1,d2,d3);
		glVertex3d(e1,e2,e3);
		glVertex3d(f1,f2,f3);
		for (int j=3;j<s;++j){
		  if (w[j].type!=_VECT || w[j]._VECTptr->size()!=3)
		    glvertex(vecteur(3,0),0,0,contextptr);
		  else 
		    glvertex( lastpnt=*w[j]._VECTptr,0,0,contextptr );
		}
		glEnd();
		xcas_color(couleur,true);
		// FIXME?? back face
		/*
		glnormal(f1,f2,f3,e1,e2,e3,d1,d2,d3);
		glBegin(GL_POLYGON);
		for (int j=s-1;j>=3;--j){
		  if (w[j].type!=_VECT || w[j]._VECTptr->size()!=3)
		    glvertex(vecteur(3,0),0,0,contextptr);
		  else 
		    glvertex( *w[j]._VECTptr ,0,0,contextptr);
		}
		glVertex3d(f1,f2,f3);
		glVertex3d(e1,e2,e3);
		glVertex3d(d1,d2,d3);
		glEnd();
		*/
	      }
	    }
	  }
	}
	for (it=vv0.begin();it!=itend;++it){
	  if (it->type==_VECT){ 
	    w = *evalf_double(*it,1,contextptr)._VECTptr;
	    int s=w.size();
	    if (s<3)
	      continue;
	    double d1,d2,d3,e1,e2,e3,f1,f2,f3;	    
	    if (get_glvertex(w[0],d1,d2,d3,0,0,contextptr) &&
		get_glvertex(w[1],e1,e2,e3,0,0,contextptr) &&
		get_glvertex(w[2],f1,f2,f3,0,0,contextptr) ){
	      glnormal(d1,d2,d3,e1,e2,e3,f1,f2,f3);
	      if (!hidden_line){
		glColor3f(0,0,0);
		glBegin(GL_LINE_LOOP);
		glVertex3d(d1,d2,d3);
		glVertex3d(e1,e2,e3);
		glVertex3d(f1,f2,f3);
		for (int j=3;j<s;++j){
		  if (w[j].type!=_VECT || w[j]._VECTptr->size()!=3)
		    glvertex(vecteur(3,0),0,0,contextptr);
		  else 
		    glvertex( lastpnt=*w[j]._VECTptr,0,0,contextptr );
		}
		glEnd();
		xcas_color(couleur,true);
	      }
	    }
	  } // end it->type==_VECT
	} // end for (;it!=itend;)
	if (!hidden_name && show_names) legende_draw(lastpnt,legende,labelpos);
	return;
      } // end polyedre
      int s=vv0.size();
      if (s==2 && check3dpoint(vv0.front()) && check3dpoint(vv0.back()) ){
	// segment, half-line, vector or line
	vecteur A(*evalf_double(vv0.front(),1,contextptr)._VECTptr),B(*evalf_double(vv0.back(),1,contextptr)._VECTptr);
	vecteur dir(subvecteur(B,A));
	double lambda=1,nu;
	double d1=evalf_double(dir[0],1,contextptr)._DOUBLE_val;
	double d2=evalf_double(dir[1],1,contextptr)._DOUBLE_val;
	double d3=evalf_double(dir[2],1,contextptr)._DOUBLE_val;
	double nd=std::sqrt(d1*d1+d2*d2+d3*d3);
	if (std::abs(d1)>=std::abs(d2) && std::abs(d1)>=std::abs(d3)){
	  nu=(window_xmin-evalf_double(A[0],1,contextptr)._DOUBLE_val)/d1;
	}
	if (std::abs(d2)>std::abs(d1) && std::abs(d2)>=std::abs(d3)){
	  nu=(window_ymin-evalf_double(A[1],1,contextptr)._DOUBLE_val)/d2;
	}
	if (std::abs(d3)>std::abs(d1) && std::abs(d3)>std::abs(d2)){
	  nu=(window_zmin-evalf_double(A[2],1,contextptr)._DOUBLE_val)/d3;
	}
	if (std::abs(d1)>=0.1*nd)
	  lambda=giac_max(lambda,(window_xmax-window_xmin)/std::abs(d1));
	if (std::abs(d2)>=0.1*nd)
	  lambda=giac_max(lambda,(window_ymax-window_ymin)/std::abs(d2));
	if (std::abs(d3)>=0.1*nd)
	  lambda=giac_max(lambda,(window_ymax-window_ymin)/std::abs(d3));
	lambda *= 2;
	glBegin(GL_LINE_STRIP);
	if (v0.subtype==_LINE__VECT){
	  // move A in clip
	  A=addvecteur(A,multvecteur(nu,dir));
	  B=subvecteur(B,multvecteur(nu,dir));
	  glvertex(subvecteur(A,multvecteur(lambda,dir)),0,0,contextptr);
	  glvertex(addvecteur(A,multvecteur(lambda,dir)),0,0,contextptr);
	}
	else {
	  glvertex(A,0,0,contextptr);
	  if (v0.subtype==_HALFLINE__VECT)
	    glvertex(addvecteur(A,multvecteur(lambda,dir)),0,0,contextptr);
	  else
	    glvertex(B,0,0,contextptr);
	}
	glEnd();
	if (v0.subtype==_VECTOR__VECT){
	  double xB=evalf_double(B[0],1,contextptr)._DOUBLE_val;
	  double yB=evalf_double(B[1],1,contextptr)._DOUBLE_val;
	  double zB=evalf_double(B[2],1,contextptr)._DOUBLE_val;
	  double xA=evalf_double(A[0],1,contextptr)._DOUBLE_val;
	  double yA=evalf_double(A[1],1,contextptr)._DOUBLE_val;
	  double zA=evalf_double(A[2],1,contextptr)._DOUBLE_val;
	  /* 2-d code */
	  double iA,jA,depthA,iB,jB,depthB,di,dj,dij;
	  find_ij(xB,yB,zB,iB,jB,depthB);
	  find_ij(xA,yA,zA,iA,jA,depthA);
	  di=iA-iB; dj=jA-jB;
	  dij=std::sqrt(di*di+dj*dj);
	  if (dij){
	    dij /= giacmin(5,int(dij/10))+width;
	    di/=dij;
	    dj/=dij;
	    double dip=-dj,djp=di;
	    di*=std::sqrt(3.0);
	    dj*=std::sqrt(3.0);
	    double iC=iB+di+dip,jC=jB+dj+djp;
	    double iD=iB+di-dip,jD=jB+dj-djp;
	    double xC,yC,zC,xD,yD,zD;
	    find_xyz(iC,jC,depthB,xC,yC,zC);
	    find_xyz(iD,jD,depthB,xD,yD,zD);
	    glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
	    glBegin(GL_POLYGON);
	    glVertex3d(xB,yB,zB);
	    glVertex3d(xC,yC,zC);
	    glVertex3d(xD,yD,zD);
	    glEnd();
	  }
	}
	if (!hidden_name && show_names && v0.subtype!=_GROUP__VECT) 
	  legende_draw(multvecteur(0.5,addvecteur(A,B)),legende,labelpos);
	return;
      }
      if (s>2){ // polygon
	bool closed=vv0.front()==vv0.back();
	double d1,d2,d3,e1,e2,e3,f1,f2,f3;	    
	if (get_glvertex(vv0[0],d1,d2,d3,0,0,contextptr) &&
	    get_glvertex(vv0[1],e1,e2,e3,0,0,contextptr) &&
	    get_glvertex(vv0[2],f1,f2,f3,0,0,contextptr) ){
	  if (test_enable_texture(texture) && s<=5 && closed){
	    glBegin(GL_QUADS);
	    glnormal(d1,d2,d3,e1,e2,e3,f1,f2,f3);
	    glTexCoord2f(0,0);
	    glVertex3d(d1,d2,d3);
	    glTexCoord2f(0,1);
	    glVertex3d(e1,e2,e3);
	    glTexCoord2f(1,1);
	    glVertex3d(f1,f2,f3);
	    if (s==5){
	      glTexCoord2f(1,0);
	      glvertex(*vv0[3]._VECTptr,0,0,contextptr);
	    }
	    else
	      glVertex3d(d1,d2,d3);
	    glEnd();
	    glDisable(GL_TEXTURE_2D);
	  }
	  else {
	    // glBegin(closed?GL_POLYGON:GL_LINE_STRIP);
	    const_iterateur it=vv0.begin(),itend=vv0.end();
	    for (;it!=itend;++it){
	      //COUT << *it << endl;
	      if (it+1==itend)
		break;
	      if (check3dpoint(*it) && check3dpoint(*(it+1))){
		glBegin(GL_LINES);
		glnormal(d1,d2,d3,e1,e2,e3,f1,f2,f3);
		glvertex(*it->_VECTptr,0,0,contextptr);
		glvertex(*(it+1)->_VECTptr,0,0,contextptr);
		glEnd();
	      }
	    }
	  }
	  if (!hidden_name && show_names && !vv0.empty()) 
	    legende_draw(vv0.front(),legende,labelpos);
	}
	return;
      }
    }
  }

  void Opengl3d::indraw(const vecteur & v){
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      indraw(*it);
  }

  void mult4(double * colmat,double * vect,double * res){
    res[0]=colmat[0]*vect[0]+colmat[4]*vect[1]+colmat[8]*vect[2]+colmat[12]*vect[3];
    res[1]=colmat[1]*vect[0]+colmat[5]*vect[1]+colmat[9]*vect[2]+colmat[13]*vect[3];
    res[2]=colmat[2]*vect[0]+colmat[6]*vect[1]+colmat[10]*vect[2]+colmat[14]*vect[3];
    res[3]=colmat[3]*vect[0]+colmat[7]*vect[1]+colmat[11]*vect[2]+colmat[15]*vect[3];
  }

  void mult4(double * colmat,float * vect,double * res){
    res[0]=colmat[0]*vect[0]+colmat[4]*vect[1]+colmat[8]*vect[2]+colmat[12]*vect[3];
    res[1]=colmat[1]*vect[0]+colmat[5]*vect[1]+colmat[9]*vect[2]+colmat[13]*vect[3];
    res[2]=colmat[2]*vect[0]+colmat[6]*vect[1]+colmat[10]*vect[2]+colmat[14]*vect[3];
    res[3]=colmat[3]*vect[0]+colmat[7]*vect[1]+colmat[11]*vect[2]+colmat[15]*vect[3];
  }

  void mult4(double * c,double k,double * res){
    for (int i=0;i<16;i++)
      res[i]=k*c[i];
  }
  
  double det4(double * c){
    return c[0]*c[5]*c[10]*c[15]-c[0]*c[5]*c[14]*c[11]-c[0]*c[9]*c[6]*c[15]+c[0]*c[9]*c[14]*c[7]+c[0]*c[13]*c[6]*c[11]-c[0]*c[13]*c[10]*c[7]-c[4]*c[1]*c[10]*c[15]+c[4]*c[1]*c[14]*c[11]+c[4]*c[9]*c[2]*c[15]-c[4]*c[9]*c[14]*c[3]-c[4]*c[13]*c[2]*c[11]+c[4]*c[13]*c[10]*c[3]+c[8]*c[1]*c[6]*c[15]-c[8]*c[1]*c[14]*c[7]-c[8]*c[5]*c[2]*c[15]+c[8]*c[5]*c[14]*c[3]+c[8]*c[13]*c[2]*c[7]-c[8]*c[13]*c[6]*c[3]-c[12]*c[1]*c[6]*c[11]+c[12]*c[1]*c[10]*c[7]+c[12]*c[5]*c[2]*c[11]-c[12]*c[5]*c[10]*c[3]-c[12]*c[9]*c[2]*c[7]+c[12]*c[9]*c[6]*c[3];
  }

  void inv4(double * c,double * res){
    res[0]=c[5]*c[10]*c[15]-c[5]*c[14]*c[11]-c[10]*c[7]*c[13]-c[15]*c[9]*c[6]+c[14]*c[9]*c[7]+c[11]*c[6]*c[13];
    res[1]=-c[1]*c[10]*c[15]+c[1]*c[14]*c[11]+c[10]*c[3]*c[13]+c[15]*c[9]*c[2]-c[14]*c[9]*c[3]-c[11]*c[2]*c[13];
    res[2]=c[1]*c[6]*c[15]-c[1]*c[14]*c[7]-c[6]*c[3]*c[13]-c[15]*c[5]*c[2]+c[14]*c[5]*c[3]+c[7]*c[2]*c[13];
    res[3]=-c[1]*c[6]*c[11]+c[1]*c[10]*c[7]+c[6]*c[3]*c[9]+c[11]*c[5]*c[2]-c[10]*c[5]*c[3]-c[7]*c[2]*c[9];
    res[4]=-c[4]*c[10]*c[15]+c[4]*c[14]*c[11]+c[10]*c[7]*c[12]+c[15]*c[8]*c[6]-c[14]*c[8]*c[7]-c[11]*c[6]*c[12];
    res[5]=c[0]*c[10]*c[15]-c[0]*c[14]*c[11]-c[10]*c[3]*c[12]-c[15]*c[8]*c[2]+c[14]*c[8]*c[3]+c[11]*c[2]*c[12];
    res[6]=-c[0]*c[6]*c[15]+c[0]*c[14]*c[7]+c[6]*c[3]*c[12]+c[15]*c[4]*c[2]-c[14]*c[4]*c[3]-c[7]*c[2]*c[12];
    res[7]=c[0]*c[6]*c[11]-c[0]*c[10]*c[7]-c[6]*c[3]*c[8]-c[11]*c[4]*c[2]+c[10]*c[4]*c[3]+c[7]*c[2]*c[8];
    res[8]=c[4]*c[9]*c[15]-c[4]*c[13]*c[11]-c[9]*c[7]*c[12]-c[15]*c[8]*c[5]+c[13]*c[8]*c[7]+c[11]*c[5]*c[12];
    res[9]=-c[0]*c[9]*c[15]+c[0]*c[13]*c[11]+c[9]*c[3]*c[12]+c[15]*c[8]*c[1]-c[13]*c[8]*c[3]-c[11]*c[1]*c[12];
    res[10]=c[0]*c[5]*c[15]-c[0]*c[13]*c[7]-c[5]*c[3]*c[12]-c[15]*c[4]*c[1]+c[13]*c[4]*c[3]+c[7]*c[1]*c[12];
    res[11]=-c[0]*c[5]*c[11]+c[0]*c[9]*c[7]+c[5]*c[3]*c[8]+c[11]*c[4]*c[1]-c[9]*c[4]*c[3]-c[7]*c[1]*c[8];
    res[12]=-c[4]*c[9]*c[14]+c[4]*c[13]*c[10]+c[9]*c[6]*c[12]+c[14]*c[8]*c[5]-c[13]*c[8]*c[6]-c[10]*c[5]*c[12];
    res[13]=c[0]*c[9]*c[14]-c[0]*c[13]*c[10]-c[9]*c[2]*c[12]-c[14]*c[8]*c[1]+c[13]*c[8]*c[2]+c[10]*c[1]*c[12];
    res[14]=-c[0]*c[5]*c[14]+c[0]*c[13]*c[6]+c[5]*c[2]*c[12]+c[14]*c[4]*c[1]-c[13]*c[4]*c[2]-c[6]*c[1]*c[12];
    res[15]=c[0]*c[5]*c[10]-c[0]*c[9]*c[6]-c[5]*c[2]*c[8]-c[10]*c[4]*c[1]+c[9]*c[4]*c[2]+c[6]*c[1]*c[8];
    double det=det4(c);
    mult4(res,1/det,res);
  }

  void dim32dim2(double * view,double * proj,double * model,double x0,double y0,double z0,double & i,double & j,double & dept){
    double vect[4]={x0,y0,z0,1},res1[4],res2[4];
    mult4(model,vect,res1);
    mult4(proj,res1,res2);
    i=res2[0]/res2[3]; // x and y are in [-1..1]
    j=res2[1]/res2[3];
    dept=res2[2]/res2[3];
    i=view[0]+(i+1)*view[2]/2;
    j=view[1]+(j+1)*view[3]/2;
    // x and y are the distance to the BOTTOM LEFT of the window
  }

  void Opengl::glRasterPos3d(double d1,double d2,double d3){
    if (Opengl3d * ptr=dynamic_cast<Opengl3d *>(this))
      dim32dim2(ptr->view,ptr->proj,ptr->model,d1,d2,d3,ipos,jpos,depthpos);
  }
  
  void dim22dim3(double * view,double * proj_inv,double * model_inv,double i,double j,double depth_,double & x,double & y,double & z){
    i=(i-view[0])*2/view[2]-1;
    j=(j-view[1])*2/view[3]-1;
    double res2[4]={i,j,depth_,1},res1[4],vect[4];
    mult4(proj_inv,res2,res1);
    mult4(model_inv,res1,vect);
    x=vect[0]/vect[3];
    y=vect[1]/vect[3];
    z=vect[2]/vect[3];
  }

  void Opengl3d::find_ij(double x,double y,double z,double & i,double & j,double & depth_) {
    dim32dim2(view,proj,model,x,y,z,i,j,depth_);
#ifdef __APPLE__
    j=this->y()+h()-j;
    i=i+this->x();
#else
    j=h()-j;
#endif
    // cout << i << " " << j <<  endl;
  }

  void Opengl3d::find_xyz(double i,double j,double depth_,double & x,double & y,double & z) {
#ifdef __APPLE__
    j=this->y()+h()-j;
    i=i-this->x();
#else
    j=h()-j;
#endif
    dim22dim3(view,proj_inv,model_inv,i,j,depth_,x,y,z);
  }

  double sqrt3over2=std::sqrt(double(3.0))/2;

  bool find_xmin_dx(double x0,double x1,double & xmin,double & dx){
    if (x0>=x1)
      return false;
    double x0x1=x1-x0;
    dx=std::pow(10,std::floor(std::log10(x0x1)));
    if (x0x1/dx>6)
      dx *= 2;    
    if (x0x1/dx<1.3)
      dx /= 5;
    if (x0x1/dx<3)
      dx /=2;
    if (!dx)
      return false;
    xmin=std::ceil(x0/dx)*dx;
    return true;
  }

  /* font data for drawing text borrowed from freeglut */

typedef struct tagSFG_StrokeVertex SFG_StrokeVertex;
struct tagSFG_StrokeVertex
{
    GLfloat         X, Y;
};

typedef struct tagSFG_StrokeStrip SFG_StrokeStrip;
struct tagSFG_StrokeStrip
{
    int             Number;
    const SFG_StrokeVertex* Vertices;
};

typedef struct tagSFG_StrokeChar SFG_StrokeChar;
struct tagSFG_StrokeChar
{
    GLfloat         Right;
    int             Number;
    const SFG_StrokeStrip* Strips;
};

typedef struct tagSFG_StrokeFont SFG_StrokeFont;
struct tagSFG_StrokeFont
{
    char*           Name;                       /* The source font name      */
    int             Quantity;                   /* Number of chars in font   */
    GLfloat         Height;                     /* Height of the characters  */
    const SFG_StrokeChar** Characters;          /* The characters mapping    */
};

#include "freeglut_stroke_roman.c"  
/*
 * Draw a stroke character
 */
void freeglutStrokeCharacter( int character )
{
    const SFG_StrokeChar *schar;
    const SFG_StrokeStrip *strip;
    int i, j;

    schar = fgStrokeRoman.Characters[ character ];
    if (!schar) return;
    strip = schar->Strips;

    for( i = 0; i < schar->Number; i++, strip++ )
    {
        glBegin( GL_LINE_STRIP );
        for( j = 0; j < strip->Number; j++ )
	  glVertex3f( strip->Vertices[ j ].X, strip->Vertices[ j ].Y,0);
        glEnd( );
	glBegin( GL_POINTS );
        for( j = 0; j < strip->Number; j++ )
	  glVertex3f( strip->Vertices[ j ].X, strip->Vertices[ j ].Y, 0 );
	glEnd( );
    }
    glTranslatef( schar->Right, 0.0, 0.0 );
}


  void Opengl3d::draw_string(const string & s){
    //COUT << "draw_string position " << ipos-w()/2. << "," << jpos-h()/2. << " " << s << endl;
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();   
    glLoadIdentity();    
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();   
    glLoadIdentity();
    glScalef(.2/w(),.2/h(),1.0);
    glTranslatef(10*(ipos-w()/2.),10*(jpos-h()/2.),0);
    for (unsigned i=0;i<s.size();++i){
      freeglutStrokeCharacter(s[i]);
    }
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();    
    glMatrixMode(GL_MODELVIEW);
    glPopMatrix();    
  }

  void normalize(double & a,double &b,double &c){
    double n=std::sqrt(a*a+b*b+c*c);
    a /= n;
    b /= n;
    c /= n;
  }

  void Opengl3d::normal2plan(double & a,double &b,double &c){
    a /= std::pow(window_xmax-window_xmin,2);
    b /= std::pow(window_ymax-window_ymin,2);
    c /= std::pow(window_zmax-window_zmin,2);
    normalize(a,b,c);
    if (std::abs(a)<=1e-3){
      a=0;
      if (std::abs(b)<=1e-3){
	b=0;
	c=1;
      }
      else {
	gen gcb(float2rational(c/b,1e-3,contextptr));
	gen cbn,cbd;
	fxnd(gcb,cbn,cbd);
	if (cbn.type==_INT_ && cbd.type==_INT_ &&  std::abs(double(cbn.val)/cbd.val-c/a)<1e-2){
	  b=cbn.val;
	  c=cbd.val;
	}
      }
    }
    else {
      gen gba(float2rational(b/a,1e-3,contextptr));
      gen ban,bad,can,cad;
      fxnd(gba,ban,bad);
      if (ban.type==_INT_ && bad.type==_INT_  && std::abs(double(ban.val)/bad.val-b/a)<1e-2){
	gen gca(float2rational(c/a,1e-3,contextptr));
	fxnd(gca,can,cad);
	if (can.type==_INT_ && cad.type==_INT_ &&  std::abs(double(can.val)/cad.val-c/a)<1e-2){
	  int g=gcd(cad.val,bad.val);
	  // b/a=ban/bad=ban*(cad/g)/ppcm
	  int ai,bi,ci;
	  ai=(cad.val*bad.val/g);
	  bi=(ban.val*cad.val/g);
	  ci=(can.val*bad.val/g);
	  if (std::abs(ai)<=13 && std::abs(bi)<=13 && std::abs(ci)<13){
	    a=ai; b=bi; c=ci;
	  }
	}
      }
    }
  }

  void Opengl3d::current_normal(double & a,double & b,double & c) {
    double res1[4]={0,0,1,1},vect[4];
    mult4(model_inv,res1,vect);
    a=vect[0]/vect[3]-(window_xmax+window_xmin)/2;
    b=vect[1]/vect[3]-(window_ymax+window_ymin)/2;
    c=vect[2]/vect[3]-(window_zmax+window_zmin)/2;
    if (std::abs(a)<1e-3*(window_xmax-window_xmin))
      a=0;
    if (std::abs(b)<1e-3*(window_ymax-window_ymin))
      b=0;
    if (std::abs(c)<1e-3*(window_zmax-window_zmin))
      c=0;
  }

  void round0(double & x,double xmin,double xmax){
    if (std::abs(x)<1e-3*(xmax-xmin))
      x=0;
  }

  // clipping not supported by EMCC
  void Opengl3d::display(){
    if (twodim){
      window_zmin=-1;
      window_zmax=1;
    }
    glEnable(GL_NORMALIZE);
    glEnable(GL_LINE_STIPPLE);
    glEnable(GL_POLYGON_OFFSET_FILL);
    glPolygonOffset(1.0,1.0);
    glEnable(GL_CLIP_PLANE0);
    glEnable(GL_CLIP_PLANE1);
    glEnable(GL_CLIP_PLANE2);
    glEnable(GL_CLIP_PLANE3);
    glEnable(GL_CLIP_PLANE4);
    glEnable(GL_CLIP_PLANE5);
    // cout << glIsEnabled(GL_CLIP_PLANE0) << endl;
    if (!twodim){
      glDepthFunc(GL_LESS);
      glEnable(GL_DEPTH_TEST);
    }
    glShadeModel((display_mode & 0x10)?GL_FLAT:GL_SMOOTH);
    bool lighting=display_mode & 0x8;
    if (lighting)
      glClearColor(0, 0, 0, 0);
    else
      glClearColor(1, 1, 1, 1);
    // clear the color and depth buffer
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);      
    // view transformations
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();   
    glLoadIdentity();
    bool notperspective=display_mode & 0x4;
    if (!twodim){
      if (notperspective)
	// glFrustum(-sqrt3over2,sqrt3over2,-sqrt3over2,sqrt3over2,sqrt3over2,3*sqrt3over2);
	glOrtho(-sqrt3over2,sqrt3over2,-sqrt3over2,sqrt3over2,-sqrt3over2,sqrt3over2);
      else
	glFrustum(-0.5,0.5,-0.5,0.5,sqrt3over2,3*sqrt3over2);
    }
    // put the visualisation cube inside above visualisation
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();   
    glLoadIdentity();
    double dx=(window_xmax-window_xmin),dy=(window_ymax-window_ymin),dz=(window_zmax-window_zmin);
    if (dx==0) { dx=1; ++window_xmax; }
    if (dy==0) { dy=1; ++window_ymax; }
    if (dz==0) { dz=1; ++window_zmax; }
    if (twodim){
      glScaled(1.9/dx,1.9/dy,1/dz);
    } else {
      double x,y,z,theta;
      get_axis_angle_deg( (dragi || dragj)?(quaternion_double(dragi*180/h(),0,0)*rotation_2_quaternion_double(1,0,0,dragj*180/w())*q):q,x,y,z,theta);
      // cerr << theta << " " << x << "," << y << "," << z << endl;
      if (!notperspective)
	glTranslated(0,0,-2*sqrt3over2);
      glRotated(theta,x,y,z);
      glScaled(1/dx,1/dy,1/dz);
    }
    glTranslated(-(window_xmin+window_xmax)/2,-(window_ymin+window_ymax)/2,-(window_zmin+window_zmax)/2);
    // glRotated(theta_y,0,0,1);
    double plan0[]={1,0,0,0.5};
    double plan1[]={-1,0,0,0.5};
    double plan2[]={0,1,0,0.5};
    double plan3[]={0,-1,0,0.5};
    double plan4[]={0,0,1,0.5};
    double plan5[]={0,0,-1,0.5};
    plan0[3]=-window_xmin+dx/256;
    plan1[3]=window_xmax+dx/256;
    plan2[3]=-window_ymin+dy/256;
    plan3[3]=window_ymax+dy/256;
    plan4[3]=-window_zmin+dz/256;
    plan5[3]=window_zmax+dz/256;
    glGetDoublev(GL_PROJECTION_MATRIX,proj); // projection matrix in columns
    /*
    for (int i=0;i<15;++i)
      proj[i]=0;
    proj[0]=1/sqrt3over2;
    proj[5]=proj[0];
    proj[10]=-proj[0];
    proj[15]=1;
    */
    inv4(proj,proj_inv);
    glGetDoublev(GL_MODELVIEW_MATRIX,model); // modelview matrix in columns
    inv4(model,model_inv);
    glGetDoublev(GL_VIEWPORT,view);
    if (debug_infolevel>=2){
      double check[16];
      mult4(model,&model_inv[0],&check[0]);
      mult4(model,&model_inv[4],&check[4]);
      mult4(model,&model_inv[8],&check[8]);
      mult4(model,&model_inv[12],&check[12]);
      for (int i=0;i<16;++i){
	cout << model[i] << ",";
	if (i%4==3) cout << endl;
      }
      cout << endl;
      for (int i=0;i<16;++i){
	cout << model_inv[i] << ",";
	if (i%4==3) cout << endl;
      }
      cout << endl;
      for (int i=0;i<16;++i){
	cout << check[i] << ",";
	if (i%4==3) cout << endl;
      }
      cout << endl;
    }
    // drax
    bool fbox=(display_mode & 0x100);
    bool triedre=(display_mode & 0x200);
    glLineStipple(1,0xffff);
    glLineWidth(1);
    //gl_color(FL_RED);
    glColor3f(1,0,0);
    if (show_axes || triedre){
      glLineWidth(3);
      glBegin(GL_LINES);
      glVertex3d(0,0,0);
      glVertex3d(1,0,0);
      glEnd();
    }
    if (show_axes){
      glLineWidth(1);
      glBegin(GL_LINES);
      if (twodim)
	glVertex3d(window_xmin,0,0);
      else
	glVertex3d(0,0,0);
      glVertex3d(window_xmax,0,0);
      glEnd();
    }
    if (show_axes){
      glBegin(GL_LINES);
      glVertex3d(window_xmin,window_ymin,window_zmin);
      glVertex3d(window_xmax,window_ymin,window_zmin);
      glVertex3d(window_xmin,window_ymax,window_zmin);
      glVertex3d(window_xmax,window_ymax,window_zmin);
      if (!twodim){
	glVertex3d(window_xmin,window_ymin,window_zmax);
	glVertex3d(window_xmax,window_ymin,window_zmax);
	glVertex3d(window_xmin,window_ymax,window_zmax);
	glVertex3d(window_xmax,window_ymax,window_zmax);
      }
      glEnd();
    }
    // gl_color(FL_GREEN);
    glColor3f(0,1,0);
    if (show_axes || triedre){
      glLineWidth(3);
      glBegin(GL_LINES);
      glVertex3d(0,0,0);
      glVertex3d(0,1,0);
      glEnd();
    }
    if (show_axes){
      glLineWidth(1);
      glBegin(GL_LINES);
      if (twodim)
	glVertex3d(0,window_ymin,0);
      else
	glVertex3d(0,0,0);
      glVertex3d(0,window_ymax,0);
      glEnd();
    }
    if (show_axes){
      glBegin(GL_LINES);
      glVertex3d(window_xmin,window_ymin,window_zmin);
      glVertex3d(window_xmin,window_ymax,window_zmin);
      glVertex3d(window_xmax,window_ymin,window_zmin);
      glVertex3d(window_xmax,window_ymax,window_zmin);
      if (!twodim){
	glVertex3d(window_xmin,window_ymin,window_zmax);
	glVertex3d(window_xmin,window_ymax,window_zmax);
	glVertex3d(window_xmax,window_ymin,window_zmax);
	glVertex3d(window_xmax,window_ymax,window_zmax);
      }
      glEnd();
    }
    // gl_color(FL_BLUE);
    glColor3f(0,0,1);
    if (!twodim && (show_axes || triedre)){
      glLineWidth(3);
      glBegin(GL_LINES);
      glVertex3d(0,0,0);
      glVertex3d(0,0,1);
      glEnd();
    }
    if (show_axes && !twodim){
      glLineWidth(1);
      glBegin(GL_LINES);
      glVertex3d(0,0,0);
      glVertex3d(0,0,window_zmax);
      glEnd();
    }  
    if (show_axes && !twodim){
      glBegin(GL_LINES);
      glVertex3d(window_xmin,window_ymin,window_zmin);
      glVertex3d(window_xmin,window_ymin,window_zmax);
      glVertex3d(window_xmax,window_ymax,window_zmin);
      glVertex3d(window_xmax,window_ymax,window_zmax);
      glVertex3d(window_xmin,window_ymax,window_zmin);
      glVertex3d(window_xmin,window_ymax,window_zmax);
      glVertex3d(window_xmax,window_ymin,window_zmin);
      glVertex3d(window_xmax,window_ymin,window_zmax);
      glEnd();
    }
    if(show_axes){ // maillage
      glColor3f(1,0,0);
      glRasterPos3d(1,0,0);
      draw_string(x_axis_name.empty()?"x":x_axis_name);
      glColor3f(0,1,0);
      glRasterPos3d(0,1,0);
      draw_string(y_axis_name.empty()?"y":y_axis_name);
      if (!twodim){
	glColor3f(0,0,1);
	glRasterPos3d(0,0,1);
	draw_string(z_axis_name.empty()?"z":z_axis_name);
      }
      if (fbox || twodim){
	double xmin,dx,x,ymin,dy,y,zmin,dz,z;
	find_xmin_dx(window_xmin,window_xmax,xmin,dx);
	find_xmin_dx(window_ymin,window_ymax,ymin,dy);
	find_xmin_dx(window_zmin,window_zmax,zmin,dz);
	// COUT << "maillage " << xmin << " " << dx << " " << ymin << " " << dy << endl;
	glLineStipple(1,0x3333);
	//gl_color(FL_CYAN);
	glColor3f(0,1,1);
	if (twodim){
	  double taille=30;
	  for (y=ymin;y<=window_ymax;y+=dy){
	    for (x=xmin;x<=window_xmax;x+=dx){
#if 0
	      glBegin(GL_LINE_LOOP);
	      glVertex3d(x-dx/taille,y-dy/taille,-1);
	      glVertex3d(x+dx/taille,y-dy/taille,-1);
	      glVertex3d(x+dx/taille,y+dy/taille,-1);
	      glVertex3d(x-dx/taille,y+dy/taille,-1);
	      glEnd();
#else
	      glBegin(GL_LINES);
	      glVertex3d(x-dx/taille,y,-1);
	      glVertex3d(x+dx/taille,y,-1);
	      glVertex3d(x,y+dy/taille,-1);
	      glVertex3d(x,y-dy/taille,-1);
	      glEnd();
#endif
	    }
	  }
	}
	else {
	  for (z=zmin;z<=window_zmax;z+=2*dz){
	    for (y=ymin;y<=window_ymax;y+=2*dy){
	      glBegin(GL_LINES);
	      glVertex3d(window_xmin,y,z);
	      glVertex3d(window_xmax,y,z);
	      glEnd();
	    }
	  }
	  for (z=zmin;z<=window_zmax;z+=2*dz){
	    for (x=xmin;x<=window_xmax;x+=2*dx){
	      glBegin(GL_LINES);
	      glVertex3d(x,window_ymin,z);
	      glVertex3d(x,window_ymax,z);
	      glEnd();
	    }
	  }
	  for (x=xmin;x<=window_xmax;x+=2*dx){
	    for (y=ymin;y<=window_ymax;y+=2*dy){
	      glBegin(GL_LINES);
	      glVertex3d(x,y,window_zmin);
	      glVertex3d(x,y,window_zmax);
	      glEnd();
	    }
	  }
	}	  
	glColor3f(1,0,1);
	glPointSize(3);
	for (x=xmin;x<=window_xmax;x+=dx){
	  round0(x,window_xmin,window_xmax);
	  glBegin(GL_POINTS);
	  glVertex3d(x,window_ymin,window_zmin);
	  glEnd();
	  glRasterPos3d(x,window_ymin,window_zmin);
	  string tmps=giac::print_DOUBLE_(x,2)+x_axis_unit;
	  draw_string(tmps);
	}
	for (y=ymin;y<=window_ymax;y+=dy){
	  round0(y,window_ymin,window_ymax);
	  glBegin(GL_POINTS);
	  glVertex3d(window_xmin,y,window_zmin);
	  glEnd();
	  glRasterPos3d(window_xmin,y,window_zmin);
	  string tmps=giac::print_DOUBLE_(y,2)+y_axis_unit;
	  draw_string(tmps);
	}
	if (!twodim){
	  for (z=zmin;z<=window_zmax;z+=dz){
	    round0(z,window_zmin,window_zmax);
	    glBegin(GL_POINTS);
	    glVertex3d(window_xmin,window_ymin,z);
	    glEnd();
	    glRasterPos3d(window_xmin,window_ymin,z);
	    string tmps=giac::print_DOUBLE_(z,2)+z_axis_unit;
	    draw_string(tmps);
	  }
	}
      }
    }
    glClipPlane(GL_CLIP_PLANE0,plan0);
    glClipPlane(GL_CLIP_PLANE1,plan1);
    glClipPlane(GL_CLIP_PLANE2,plan2);
    glClipPlane(GL_CLIP_PLANE3,plan3);
    glClipPlane(GL_CLIP_PLANE4,plan4);
    glClipPlane(GL_CLIP_PLANE5,plan5);
    // mouse plan
    double normal_a,normal_b,normal_c;
    current_normal(normal_a,normal_b,normal_c);
    normal2plan(normal_a,normal_b,normal_c);
    double plan_x0,plan_y0,plan_z0,plan_t0;
    find_xyz(0,0,depth,plan_x0,plan_y0,plan_z0);
    plan_t0=normal_a*plan_x0+normal_b*plan_y0+normal_c*plan_z0;
    if (std::abs(plan_t0)<std::abs(window_zmax-window_zmin)/1000)
      plan_t0=0;
    if (!twodim && show_axes){
      glColor3f(1,1,0);
      glLineWidth(2);
      double xa,ya,za,xb,yb,zb;
      // show mouse plan intersections with the faces of the clip planes
      // example with the face z=Z:
      // a*x+b*y=plan_t0-normal_c*Z
      // get the 4 intersections, keep only the valid ones
      glLineStipple(1,0x3333);
      if (is_clipped(normal_a,window_xmin,window_xmax,normal_b,window_ymin,window_ymax,plan_t0-normal_c*window_zmin,xa,ya,xb,yb)){
	glBegin(GL_LINE_STRIP);
	glVertex3d(xa,ya,window_zmin+dz/256);
	glVertex3d(xb,yb,window_zmin+dz/256);
	glEnd();
      }
      if (is_clipped(normal_a,window_xmin,window_xmax,normal_b,window_ymin,window_ymax,plan_t0-normal_c*window_zmax,xa,ya,xb,yb)){
	glBegin(GL_LINE_STRIP);
	glVertex3d(xa,ya,window_zmax-dz/256);
	glVertex3d(xb,yb,window_zmax-dz/256);
	glEnd();
      }
      if (is_clipped(normal_a,window_xmin,window_xmax,normal_c,window_zmin,window_zmax,plan_t0-normal_b*window_ymin,xa,za,xb,zb)){
	glBegin(GL_LINE_STRIP);
	glVertex3d(xa,window_ymin+dy/256,za);
	glVertex3d(xb,window_ymin+dy/256,zb);
	glEnd();
      }
      if (is_clipped(normal_a,window_xmin,window_xmax,normal_c,window_zmin,window_zmax,plan_t0-normal_b*window_ymax,xa,za,xb,zb)){
	glBegin(GL_LINE_STRIP);
	glVertex3d(xa,window_ymax-dy/256,za);
	glVertex3d(xb,window_ymax-dy/256,zb);
	glEnd();
      }
      if (is_clipped(normal_b,window_ymin,window_ymax,normal_c,window_zmin,window_zmax,plan_t0-normal_a*window_xmin,ya,za,yb,zb)){
	glBegin(GL_LINE_STRIP);
	glVertex3d(window_xmin+dx/256,ya,za);
	glVertex3d(window_xmin+dx/256,yb,zb);
	glEnd();
      }
      if (is_clipped(normal_b,window_ymin,window_ymax,normal_c,window_zmin,window_zmax,plan_t0-normal_a*window_xmax,ya,za,yb,zb)){
	glBegin(GL_LINE_STRIP);
	glVertex3d(window_xmax-dx/256,ya,za);
	glVertex3d(window_xmax-dx/256,yb,zb);
	glEnd();
      }
      // same for window_zmax, etc.
      glLineWidth(1);
      glLineStipple(1,0xffff);
    }
    if (lighting){
      /*
      GLfloat mat_specular[] = { 1.0,1.0,1.0,1.0 };
      GLfloat mat_shininess[] = { 50.0 };
      glMaterialfv(GL_FRONT,GL_SPECULAR,mat_specular);
      glMaterialfv(GL_FRONT,GL_SHININESS,mat_shininess);
      */
      glEnable(GL_LIGHTING);
      glEnable(GL_CULL_FACE);
      glEnable(GL_COLOR_MATERIAL);
      static GLfloat l_pos[8][4],l_dir[8][3],ambient[8][4],diffuse[8][4],specular[8][4];
      for (int i=0;i<8;++i){
	if (!light_on[i]){
	  glDisable(GL_LIGHT0+i);
	  continue;
	}
	glEnable(GL_LIGHT0+i);
	l_pos[i][0]=light_x[i];
	l_pos[i][1]=light_y[i];
	l_pos[i][2]=light_z[i];
	l_pos[i][3]=light_w[i];
	glLightfv(GL_LIGHT0+i,GL_POSITION,l_pos[i]);
	l_dir[i][0]=light_spot_x[i];
	l_dir[i][1]=light_spot_y[i];
	l_dir[i][2]=light_spot_z[i];
	glLightfv(GL_LIGHT0+i,GL_SPOT_DIRECTION,l_dir[i]);
#if 0
	glLightf(GL_LIGHT0+i,GL_SPOT_EXPONENT,light_spot_exponent[i]);
	glLightf(GL_LIGHT0+i,GL_SPOT_CUTOFF,light_spot_cutoff[i]);
	glLightf(GL_LIGHT0+i,GL_CONSTANT_ATTENUATION,light_0[i]);
	glLightf(GL_LIGHT0+i,GL_LINEAR_ATTENUATION,light_1[i]);
	glLightf(GL_LIGHT0+i,GL_QUADRATIC_ATTENUATION,light_2[i]);
#endif
	ambient[i][0]=light_ambient_r[i];
	ambient[i][1]=light_ambient_g[i];
	ambient[i][2]=light_ambient_b[i];
	ambient[i][3]=light_ambient_a[i];
	glLightfv(GL_LIGHT0+i,GL_AMBIENT,ambient[i]);
	diffuse[i][0]=light_diffuse_r[i];
	diffuse[i][1]=light_diffuse_g[i];
	diffuse[i][2]=light_diffuse_b[i];
	diffuse[i][3]=light_diffuse_a[i];
	glLightfv(GL_LIGHT0+i,GL_DIFFUSE,diffuse[i]);
	specular[i][0]=light_specular_r[i];
	specular[i][1]=light_specular_g[i];
	specular[i][2]=light_specular_b[i];
	specular[i][3]=light_specular_a[i];
	glLightfv(GL_LIGHT0+i,GL_SPECULAR,specular[i]);
	if (debug_infolevel>=2){
	  GLfloat posf[4],direcf[4],ambient[4],diffuse[4],specular[4];
	  GLfloat expo,cutoff;
	  double pos[4],direc[4];
	  glGetLightfv(GL_LIGHT0+i,GL_SPOT_EXPONENT,&expo);
	  glGetLightfv(GL_LIGHT0+i,GL_SPOT_CUTOFF,&cutoff);
	  glGetLightfv(GL_LIGHT0+i,GL_POSITION,posf);
	  glGetLightfv(GL_LIGHT0+i,GL_SPOT_DIRECTION,direcf);
	  direcf[3]=0;
	  mult4(model_inv,posf,pos);
	  tran4(model);
	  mult4(model,direcf,direc);
	  tran4(model);
	  glGetLightfv(GL_LIGHT0+i,GL_AMBIENT,ambient);
	  glGetLightfv(GL_LIGHT0+i,GL_DIFFUSE,diffuse);
	  glGetLightfv(GL_LIGHT0+i,GL_SPECULAR,specular);
	  cerr << "light " << i << ": " <<
	    " pos " << pos[0] << "," << pos[1] << "," << pos[2] << "," << pos[3] << 
	    " dir " << direc[0] << "," << direc[1] << "," << direc[2] << "," << direc[3] << 
	    " ambient " << ambient[0] << "," << ambient[1] << "," << ambient[2] << "," << ambient[3] << 
	    " diffuse " << diffuse[0] << "," << diffuse[1] << "," << diffuse[2] << "," << diffuse[3] << 
	    " specular " << specular[0] << "," << specular[1] << "," << specular[2] << "," << specular[3] << 
	    " exponent " << expo << " cutoff " << cutoff << endl;
	}
      }
    }
    if (!twodim && (display_mode & 0x20)){
      glDisable(GL_DEPTH_TEST);
      glEnable(GL_BLEND);
      glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
    }
    // now draw each object
    if ( (display_mode & 2) && !animation_instructions.empty())
      indraw(animation_instructions[animation_instructions_pos % animation_instructions.size()]);
    if ( display_mode & 0x40 )
      indraw(trace_instructions);
    if (display_mode & 1)
      indraw(plot_instructions);
    if (display_mode & 0x8){
      glDisable(GL_LIGHTING);
      for (int i=0;i<8;++i)
	glDisable(GL_LIGHT0+i);
    }
    if (!twodim){
      glEnable(GL_DEPTH_TEST);
      glDisable(GL_BLEND);
    }
    gen plot_tmp,title_tmp;
    //find_title_plot(title_tmp,plot_tmp,contextptr);
    //indraw(plot_tmp);
    if (mode==1 && pushed && push_in_area && in_area){
      // draw segment between push and current
      glColor3f(1,0,0);
      double x,y,z;
      glBegin(GL_LINES);
      find_xyz(push_i,push_j,push_depth,x,y,z);
      glVertex3d(x,y,z);
      find_xyz(current_i,current_j,current_depth,x,y,z);
      glVertex3d(x,y,z);
      glEnd();
    }
    glDisable(GL_CLIP_PLANE0);
    glDisable(GL_CLIP_PLANE1);
    glDisable(GL_CLIP_PLANE2);
    glDisable(GL_CLIP_PLANE3);
    glDisable(GL_CLIP_PLANE4);
    glDisable(GL_CLIP_PLANE5);
    /*
    if( show_axes){
      gl_color((display_mode & 0x8)?FL_WHITE:FL_BLACK);
      glRasterPos3d(window_xmin,window_ymin,window_zmin);
      string tmps=giac::print_DOUBLE_(window_xmin,2)+","+giac::print_DOUBLE_(window_ymin,2)+","+giac::print_DOUBLE_(window_zmin,2);
      draw_string(tmps);
      glRasterPos3d(window_xmax,window_ymax,window_zmax);
      tmps=giac::print_DOUBLE_(window_xmax,2)+","+giac::print_DOUBLE_(window_ymax,2)+","+giac::print_DOUBLE_(window_zmax,2);
      draw_string(tmps);
    }
    */
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glGetDoublev(GL_PROJECTION_MATRIX,proj); // projection matrix in columns
    inv4(proj,proj_inv);
    glGetDoublev(GL_MODELVIEW_MATRIX,model); // modelview matrix in columns
    inv4(model,model_inv);
    glGetDoublev(GL_VIEWPORT,view);
    if (display_mode & 0x8)
      glColor3f(0,0,0);
    else
      glColor3f(1,1,1);
    double td=title.size()*labelsize()/2; // fl_width(title.c_str());
    glRasterPos3d(-0.4*td/w(),-1,depth-0.001);
    string mytitle(title);
    if (!is_zero(title_tmp) && function_final.type==_FUNC)
      mytitle=gen(symbolic(*function_final._FUNCptr,title_tmp)).print(contextptr);
    if (!mytitle.empty())
      draw_string(mytitle);
    glRasterPos3d(-1,-1,depth-0.001);
    if (!args_help.empty() && args_tmp.size()<= args_help.size()){
      draw_string(gettext("Click ")+args_help[giacmax(1,args_tmp.size())-1]);
    }
    glRasterPos3d(-0.98,0.87,depth-0.001);
    if (0 && show_axes && !twodim){
      glColor3f(0,0,0);
      string tmps=gettext("mouse plan ")+giac::print_DOUBLE_(normal_a,3)+"x+"+giac::print_DOUBLE_(normal_b,3)+"y+"+giac::print_DOUBLE_(normal_c,3)+"z="+ giac::print_DOUBLE_(plan_t0,3);
      // cerr << tmps << endl;
      draw_string(tmps); // +" Z="+giac::print_DOUBLE_(-depth,3));
    }
    if (below_depth_hidden){
      /* current mouse position
	double i=Fl::event_x();
	double j=Fl::event_y();
	j=window()->h()-j;
	i=(i-view[0])*2/view[2]-1;
	j=(j-view[1])*2/view[3]-1;
      */
      glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
      xcas_color((display_mode & 0x8)?FL_WHITE:FL_BLACK,true);
      glBegin(GL_POLYGON);
      glVertex3d(-1,-1,depth);
      glVertex3d(-1,1,depth);
      glVertex3d(1,1,depth);
      glVertex3d(1,-1,depth);
      glEnd();
    }
    if (!twodim)
      glDisable(GL_DEPTH_TEST);
    glMatrixMode(GL_MODELVIEW);
    glPopMatrix();
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();
    glFlush();
    glFinish();
    // COUT << "display end 4" << endl;
  }

  // if printing is true, we call gl2ps to make an eps file
  void Opengl3d::draw(){
    // cerr << "graph3d" << endl;
    int locked=0;
#ifdef HAVE_LIBPTHREAD
    locked=pthread_mutex_trylock(&interactive_mutex);
#endif
    bool b,block;
    if (!locked){
      b=io_graph(contextptr);
      io_graph(contextptr)=false;
      block=block_signal;
      block_signal=true;
    }
    
    //glEnable(GL_SCISSOR_TEST);
    //glScissor(clip_x, win->h()-clip_y-clip_h, clip_w, clip_h); // lower left
    // glViewport(clip_x, win->h()-clip_y-clip_h, clip_w, clip_h); // lower left
    glViewport(0,0, w(), h()); // lower left
    //gl_font(FL_HELVETICA,labelsize());
    // GLint viewport[4];
    // glGetIntergerv(GL_VIEWPORT,viewport);
    //fl_push_clip(clip_x,clip_y,clip_w,clip_h);
    display();
    if (!locked){
      block_signal=block;
      io_graph(contextptr)=b;
#ifdef HAVE_LIBPTHREAD
      pthread_mutex_unlock(&interactive_mutex);
#endif
    }
  }

  Opengl3d::~Opengl3d(){ 
  }

#if 0
  int Opengl3d::in_handle(int event){
    int res=Opengl::in_handle(event);
    if (event==FL_FOCUS){
      if (!paused)
	--animation_instructions_pos;
      redraw();
    }
    if (event==FL_FOCUS || event==FL_UNFOCUS)
      return 1;
    if (event==FL_KEYBOARD){
      theta_z -= int(theta_z/360)*360;
      theta_x -= int(theta_x/360)*360;
      theta_y -= int(theta_y/360)*360;
      switch (Fl::event_text()?Fl::event_text()[0]:0){
      case 'y':
	theta_z += delta_theta;
	q=q*euler_deg_to_quaternion_double(delta_theta,0,0);
	redraw();
	return 1;
      case 'z':
	theta_x += delta_theta;
	q=q*euler_deg_to_quaternion_double(0,delta_theta,0);
	redraw();
	return 1;
      case 'x':
	theta_y += delta_theta;
	q=q*euler_deg_to_quaternion_double(0,0,delta_theta);
	redraw();
	return 1;
      case 'Y':
	theta_z -= delta_theta;
	q=q*euler_deg_to_quaternion_double(-delta_theta,0,0);
	redraw();
	return 1;
      case 'Z':
	theta_x -= delta_theta;
	q=q*euler_deg_to_quaternion_double(0,-delta_theta,0);
	redraw();
	return 1;
      case 'X':
	theta_y -= delta_theta;
	q=q*euler_deg_to_quaternion_double(0,0,-delta_theta);
	redraw();
	return 1;
      case 'u': case 'f':
	depth += 0.01;
	mouse_position->redraw();
	redraw();
	return 1;
      case 'd': case 'n':
	depth -= 0.01;
	mouse_position->redraw();
	redraw();
	return 1;
      case 'h': case 'H': // hide below depth
	below_depth_hidden=true;
	redraw();
	return 1;
      case 's': case 'S': // show below depth
	below_depth_hidden=false;
	redraw();
	return 1;
      }
    }
    current_i=Fl::event_x();
    current_j=Fl::event_y();
    current_depth=depth;
    double x,y,z;
    find_xyz(current_i,current_j,current_depth,x,y,z);
    in_area=(x>=window_xmin) && (x<=window_xmax) &&
      (y>=window_ymin) && (y<=window_ymax) && 
      (z>=window_zmin) && (z<=window_zmax);
    if (event==FL_MOUSEWHEEL){
      if (!Fl::event_inside(this))
	return 0;
      if (show_axes){ // checking in_area seems to difficult, especially if mouse plan is not viewed
	depth = int(1000*(depth-Fl::e_dy *0.01)+.5)/1000.0;
	mouse_position->redraw();
	redraw();
	return 1;
      }
      else {
	if (Fl::e_dy<0)
	  zoom(0.8);
	else
	  zoom(1.25);
	return 1;
      }
    }
    if ( (event==FL_PUSH || push_in_area))
      ;
    else
      in_area=false;
    if (event==FL_PUSH){
      if (this!=Fl::focus()){
	Fl::focus(this);
	handle(FL_FOCUS);
      }
      push_i=current_i;
      push_j=current_j;
      push_depth=current_depth;
      push_in_area = in_area;
      pushed = true;
      return 1;
    }
    if (!(display_mode & 0x80) && push_in_area && (event==FL_DRAG || event==FL_RELEASE)){
      double x1,y1,z1,x2,y2,z2;
      find_xyz(current_i,current_j,current_depth,x1,y1,z1);
      find_xyz(push_i,push_j,push_depth,x2,y2,z2);
      double newx=x1-x2, newy=y1-y2, newz=z1-z2;
      round3(newx,window_xmin,window_xmax);
      round3(newy,window_ymin,window_ymax);      
      round3(newz,window_zmin,window_zmax);
      window_xmin -= newx;
      window_xmax -= newx;
      window_ymin -= newy;
      window_ymax -= newy;
      window_zmin -= newz;
      window_zmax -= newz;
      push_i = current_i;
      push_j = current_j;
      redraw();
      if (event==FL_RELEASE)
	pushed=false;
      return 1;
    }
    if (event==FL_DRAG){
      if (push_in_area)
	return 0;
      dragi=current_i-push_i;
      dragj=current_j-push_j;
      redraw();
      return 1;
    }
    if (event==FL_RELEASE){
      pushed = false;
      if (push_in_area)
	return 0;
      dragi=current_i-push_i;
      dragj=current_j-push_j;
      if (paused && absint(dragi)<4 && absint(dragj)<4)
	++animation_instructions_pos;
      else
	q=quaternion_double(dragi*180/h(),0,0)*rotation_2_quaternion_double(1,0,0,dragj*180/w())*q;
      dragi=dragj=0;
      redraw();
      return 1;
    }
    return res;
  }
#endif

  // set evryone to x
  void Opengl3d::orthonormalize(){
    if (twodim){ 
      double ratio=double(w())/h();
      double dx=(window_xmax-window_xmin)/ratio;
      double dy=(window_ymax-window_ymin);
      double x=(window_xmax+window_xmin)/2;
      double y=(window_ymax+window_ymin)/2;
      if (dx>dy){
	window_ymin=y-dx/2;
	window_ymax=y+dx/2;
      }
      else {
	window_xmin=x-ratio*dy/2;
	window_xmax=x+ratio*dy/2;
      }
    }
    else {
      window_ymax=window_xmax;
      window_ymin=window_xmin;
      window_zmax=window_xmax;
      window_zmin=window_xmin;
    }
    redraw();
  }

#if 0
  void Opengl3d::geometry_round(double x,double y,double z,double eps,gen & tmp) {
    tmp= geometry_round_numeric(x,y,z,eps,approx);
    if (tmp.type==_VECT)
      tmp.subtype = _POINT__VECT;
    selected=nearest_point(plot_instructions,tmp,eps,contextptr);
    // if there is a point inside selected, stop there, 
    // otherwise find a point that is near the line
    int pos=findfirstpoint(selection2vecteur(selected));
    if (pos<0){
      // line passing through tmp      
      double a,b,c;
      double i,j,k;
      find_ij(x,y,z,i,j,k);
      k--;
      find_xyz(i,j,k,a,b,c);
      gen line(makevecteur(tmp,makevecteur(a,b,c)),_LINE__VECT);
      /*
	current_normal(a,b,c);
	vecteur v(makevecteur(a,b,c));
	gen line(makevecteur(tmp,tmp+v),_LINE__VECT);
      */
      vector<int> sel2=nearest_point(plot_instructions,line,eps,contextptr);
      pos=findfirstpoint(selection2vecteur(sel2));
      if (pos>=0){
	selected.insert(selected.begin(),sel2[pos]);
      }
      else {
	vector<int>::const_iterator it=sel2.begin(),itend=sel2.end();
	for (;it!=itend;++it)
	  selected.push_back(*it);
      }
      if (selected.empty()){
	// add hyperplans
	const_iterateur it=plot_instructions.begin(),itend=plot_instructions.end();
	for (int pos=0;it!=itend;++it,++pos){
	  gen tmp=remove_at_pnt(*it);
	  if (tmp.is_symb_of_sommet(at_hyperplan)){
	    vecteur v=interdroitehyperplan(line,tmp,contextptr);
	    if (!v.empty() && !is_undef(v.front())){
	      gen inters=evalf_double(remove_at_pnt(v.front()),1,contextptr);
	      if (inters.type==_VECT && inters._VECTptr->size()==3){
		vecteur & xyz=*inters._VECTptr;
		if (xyz[0].type==_DOUBLE_ && xyz[1].type==_DOUBLE_ && xyz[2].type==_DOUBLE_){
		  double x=xyz[0]._DOUBLE_val;
		  double y=xyz[1]._DOUBLE_val;
		  double z=xyz[2]._DOUBLE_val;
		  if (x>=window_xmin && x<=window_xmax &&
		      y>=window_ymin && y<=window_ymax &&
		      z>=window_zmin && z<=window_zmax)
		    selected.push_back(pos);
		}
	      }
	    }
	  }
	}
      }
    }
  }
#endif

  void Opengl3dcfg::store(const Opengl3d * ptr)  {
    q=ptr->q;
    window_xmin=ptr->window_xmin;
    window_xmax=ptr->window_xmax;
    window_ymin=ptr->window_ymin;
    window_ymax=ptr->window_ymax;
    window_zmin=ptr->window_zmin;
    window_zmax=ptr->window_zmax;
    theta_x=ptr->theta_x;
    theta_y=ptr->theta_y;
    theta_z=ptr->theta_z;
    twodim=ptr->twodim;
    plot_instructions=ptr->plot_instructions;
    w=ptr->w();
    h=ptr->h();
  }
  
  Opengl3dcfg::Opengl3dcfg(const Opengl3d * ptr){
    webglhandle=0;
    store(ptr);
  }

  void Opengl3dcfg::load(Opengl3d * ptr) const {
    ptr->q=q;
    ptr->window_xmin=window_xmin;
    ptr->window_xmax=window_xmax;
    ptr->window_ymin=window_ymin;
    ptr->window_ymax=window_ymax;
    ptr->window_zmin=window_zmin;
    ptr->window_zmax=window_zmax;
    ptr->theta_x=theta_x;
    ptr->theta_y=theta_y;
    ptr->theta_z=theta_z;
    ptr->twodim=twodim;
    ptr->plot_instructions=plot_instructions;
    ptr->resize(w,h);
  }
  
  int keys[1000];
  Opengl3d * openglptr=0; 
  vector<Opengl3dcfg> v3d;
  bool pushed=false;
  void sdl_loop() {
    SDL_EnableUNICODE( 1 );
    SDL_Event event;
    while (SDL_PollEvent(&event)) {
      switch(event.type) {
      case SDL_MOUSEMOTION: {
        SDL_MouseMotionEvent *m = (SDL_MouseMotionEvent*)&event;
        // printf("motion: %d,%d  %d,%d\n", m->x, m->y, m->xrel, m->yrel);
	if (!openglptr->twodim && pushed){
	  // COUT << "pushed " << m->xrel << "," << m->yrel << endl;
	  openglptr->q=quaternion_double(m->xrel,0,0)*rotation_2_quaternion_double(m->yrel,0,0,1)*openglptr->q;
	  openglptr->draw();	      
	}
        break;
      }
      case SDL_MOUSEBUTTONDOWN: {
	pushed=true;
        SDL_MouseButtonEvent *m = (SDL_MouseButtonEvent*)&event;
        // printf("button down: %d,%d  %d,%d\n", m->button, m->state, m->x, m->y);
        break;
      }
      case SDL_MOUSEBUTTONUP: {
	pushed=false;
        SDL_MouseButtonEvent *m = (SDL_MouseButtonEvent*)&event;
        // printf("button up: %d,%d  %d,%d\n", m->button, m->state, m->x, m->y);
        break;
      }
      case SDL_KEYDOWN:
        if (!keys[event.key.keysym.sym]) {
          keys[event.key.keysym.sym] = 1;
          //printf("key down: sym %d scancode %d\n", event.key.keysym.sym, event.key.keysym.scancode);
        }
        break;
      case SDL_KEYUP:
        if (keys[event.key.keysym.sym]) {
          keys[event.key.keysym.sym] = 0;
          //printf("key up: sym %d scancode %d\n", event.key.keysym.sym, event.key.keysym.scancode);
	  if (event.key.keysym.sym=='q' || event.key.keysym.sym=='Q' || event.key.keysym.sym==SDLK_ESCAPE){
	    // deleting openglptr does not work, don't know why...
	    //if (openglptr){ delete openglptr; openglptr=0;}
	    emscripten_cancel_main_loop();
	    SDL_Quit();
	  }
	  if (openglptr){
	    switch (event.key.keysym.sym){
	    case SDLK_MINUS: case SDLK_UNDERSCORE:
	      openglptr->zoom(1.414);
	      openglptr->draw();
	      break;
	    case SDLK_PLUS: case SDLK_EQUALS:
	      openglptr->zoom(0.707);
	      openglptr->draw();
	      break;
	    case 'a': case 'A':
	      openglptr->autoscale(true);
	      openglptr->draw();
	      break;
	    case SDLK_LEFT: case 'l': case 'L':
	      openglptr->q=quaternion_double(-1,0,0)*openglptr->q;
	      openglptr->draw();	      
	      break;
	    case SDLK_RIGHT: case 'r': case 'R':
	      openglptr->q=quaternion_double(1,0,0)*openglptr->q;
	      openglptr->draw();	      
	      break;
	    case SDLK_UP: case 'u': case 'U':
	      openglptr->q=rotation_2_quaternion_double(-1,0,0,1)*openglptr->q;
	      openglptr->draw();
	      break;
	    case SDLK_DOWN: case 'd': case 'D':
	      openglptr->q=rotation_2_quaternion_double(1,0,0,1)*openglptr->q;
	      openglptr->draw();
	      break;
	    }
	  }
        }
        break;
      }
    }
  }
  
  int init_screen(int & w,int & h,int no){
    int fs;
    static int oldno=-RAND_MAX;
    if (oldno==no)
      return 0;
    oldno=no;
    if (no==-1)
      emscripten_get_canvas_size(&w, &h, &fs);
    // COUT << "init_screen " << w << " " << h << endl;
    if (w==0) w=400;
    if (h==0) h=250;
    if ( SDL_Init(SDL_INIT_VIDEO) != 0 ) {
      printf("Unable to initialize SDL: %s\n", SDL_GetError());
      return 1; // "unable to init SDL";
    }
    SDL_GL_SetAttribute( SDL_GL_DOUBLEBUFFER, 1 ); // *new*
    //COUT << "glinit " << no << endl;
    int handle=glinit( w, h, 16, SDL_OPENGL ,no);
    if (no>=0 && no<v3d.size())
      v3d[no].webglhandle=handle;
    return 0;
  }
  
int giac_renderer(const char * ch){
    // COUT << "giac_renderer " << ch << endl;
    int i=0,s=strlen(ch),w=400,h=250,fs=1,no=-1;
    if (s==0) return -1;
    char cmd=' ';
    if (*ch<'0' || *ch>'9'){
      cmd=*ch;
      ++ch;
      --s;
    }
    for (;i<s;++i){
      if (ch[i]<'0' || ch[i]>'9')
	return -1;
    }
    if (!openglptr)
      openglptr = new Opengl3d (400,250);
    //openglptr->contextptr=contextptr;
    if (s){
      no=atoi(ch)-1;
      // COUT << no << " " << v3d.size() << endl;
      if (no>=v3d.size() || no<0)
	return -1;
      //int ctx=emscripten_webgl_get_current_context();
      v3d[no].load(openglptr);
      //emscripten_webgl_make_context_current(ctx);
    }
    if (openglptr){
      // COUT << "cmd " << cmd << " no " << no << endl;
      switch (cmd){
      case 'q':
	// emscripten_cancel_main_loop();
	// SDL_Quit();
	break;
      case ' ':
	fs=init_screen(w,h,no);
	if (fs==0){
	  openglptr->draw();
          SDL_GL_SwapBuffers();
	  SDL_Quit();
	}
	break;
      case '-':
	fs=init_screen(w,h,no);
	if (fs==0){
	  openglptr->zoom(1.414);
	  openglptr->draw();
          SDL_GL_SwapBuffers();
	  SDL_Quit();
	}
	break;
      case '+':
	fs=init_screen(w,h,no);
	if (fs==0){
	  openglptr->zoom(0.707);
	  openglptr->draw();
          SDL_GL_SwapBuffers();
	  SDL_Quit();
	}
	break;
      case 'a':
	fs=init_screen(w,h,no);
	if (fs==0){
	  openglptr->autoscale(true);
	  openglptr->draw();
          SDL_GL_SwapBuffers();
	  SDL_Quit();
	}
	break;
      case 'l':
	fs=init_screen(w,h,no);
	if (fs==0){
	  openglptr->q=quaternion_double(-1,0,0)*openglptr->q;
	  openglptr->draw();
          SDL_GL_SwapBuffers();
	  SDL_Quit();
	}
	break;
      case 'r':
	fs=init_screen(w,h,no);
	if (fs==0){
	  openglptr->q=quaternion_double(1,0,0)*openglptr->q;
	  openglptr->draw();
          SDL_GL_SwapBuffers();
	  SDL_Quit();
	}
	break;
      case 'u':
	fs=init_screen(w,h,no);
	if (fs==0){
	  openglptr->q=rotation_2_quaternion_double(-1,0,0,1)*openglptr->q;
	  openglptr->draw();
          SDL_GL_SwapBuffers();
	  SDL_Quit();
	}
	break;
      case 'd':
	fs=init_screen(w,h,no);
	if (fs==0){
	  openglptr->q=rotation_2_quaternion_double(1,0,0,1)*openglptr->q;
	  openglptr->draw();
          SDL_GL_SwapBuffers();
	  SDL_Quit();
	}
	break;	
      }
      if (no>=0)
	v3d[no].store(openglptr);
      return 0;
    }
    context C;
    gen g(ch,&C);
    return giac_gen_renderer(g,&C);
  }

  int giac_gen_renderer(const gen & g_,GIAC_CONTEXT){
    gen g=g_;
    gen last=g;
    while (last.type==_VECT && !last._VECTptr->empty())
      last=last._VECTptr->back();
    if (calc_mode(contextptr)!=1 && last.is_symb_of_sommet(at_pnt)){
      int w=0,h=0,fs;
      if (!openglptr)
	openglptr = new Opengl3d (400,250);
      openglptr->contextptr=contextptr;
      fs=init_screen(w,h,-1);
      if (fs)
	return fs;
      v3d.push_back(openglptr);
      // emscripten_set_canvas_size(640, 480);
      if (is3d(g)){
	if (openglptr->twodim){
	  openglptr->theta_z=-110;
	  openglptr->theta_x=-13;
	  openglptr->theta_y=-95;
	  openglptr->q=euler_deg_to_quaternion_double(-110,-13,-95);
	}
	openglptr->twodim=false;
	openglptr->plot_instructions=vecteur(1,g);
	openglptr->autoscale(true); // full view
	openglptr->draw();
	//COUT << "reset g end" << endl;
      }
      else {
	openglptr->twodim=true;
	openglptr->theta_x=0;
	openglptr->theta_y=0;
	openglptr->theta_z=0;      
	openglptr->q=euler_deg_to_quaternion_double(0,0,0);
	openglptr->plot_instructions=vecteur(1,g);
	openglptr->autoscale(true); // full view, autoscale with 2-d objects
	openglptr->plot_instructions=vecteur(1,convert3d(g,contextptr)); // draw in 3-d
	openglptr->draw();
	//g.type=0;
      }
      v3d.back().store(openglptr);
      SDL_GL_SwapBuffers();
      // emscripten_cancel_main_loop();
      // emscripten_set_main_loop(sdl_loop, 0, 0);
      SDL_Quit();
      // WARNING: library_sdl.js SDL_Quit() is buggy, it should be
      /*
	SDL_Quit: function() {
	_SDL_AudioQuit();
	var keyboardListeningElement = Module['keyboardListeningElement'] || document;
	keyboardListeningElement.removeEventListener("keydown", SDL.receiveEvent);
	keyboardListeningElement.removeEventListener("keyup", SDL.receiveEvent);
	keyboardListeningElement.removeEventListener("keypress", SDL.receiveEvent);
	Module.print('SDL_Quit called (and ignored)');
	},
      */
    }
    return v3d.size();
  }
  
#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#ifdef EMCC
#ifdef GIAC_GGB
const char * gettext(const char * s) { 
  return s;
}
#else // GIAC_GGB
#include "aspen_translate.h"
bool tri2(const char4 & a,const char4 & b){
  int res= strcmp(a[0],b[0]);
  return res<0;
}

int giac2aspen(int lang){
  switch (lang){
  case 0: case 2:
    return 1;
  case 1:
    return 3;
  case 3:
    return 5;
  case 6:
    return 7;
  case 8:
    return 2;
  case 5:
    return 4;
  }
  return 0;
}

const char * gettext(const char * s) { 
  int lang=language(context0); 
  // 0 and 2 english 1 french 3 sp 4 el 5 de 6 it 7 tr 8 zh 9 pt
  lang=giac2aspen(lang);
  char4 s4={s};
  std::pair<char4 * const,char4 *const> pp=equal_range(aspen_giac_translations,aspen_giac_translations+aspen_giac_records,s4,tri2);
  if (pp.first!=pp.second && 
      pp.second!=aspen_giac_translations+aspen_giac_records &&
      (*pp.first)[lang]){
    return (*pp.first)[lang];
  }
  return s;
}
#endif // GIAC_GGB
#endif // EMCC

#endif // ndef GIAC_GGB
