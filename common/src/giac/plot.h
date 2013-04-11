/* -*- mode:C++ ; compile-command: "g++ -I.. -g -c plot.cc" -*- */
#ifndef _GIAC_PLOT_H
#define _GIAC_PLOT_H
/*
 *  Copyright (C) 2000 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
#include <stdexcept>
#include <cmath>
#include <cstdlib>
#include <iostream>
#include <sstream>
#include "gen.h"
#include "plot3d.h"
#ifdef HAVE_SIGNAL_H
#include <signal.h>
#endif
#include <stdio.h>
// #include <stdiostream.h>
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif
#ifdef HAVE_SYS_TYPES_H
#include <sys/types.h>
#endif
#ifdef HAVE_SYS_TIME_H
#include <sys/time.h>
#else
#define clock_t int
#define clock() 0
#endif
#ifndef HAVE_NO_SYS_RESOURCE_WAIT_H
#include <sys/resource.h>
#include <sys/wait.h>
#endif

#ifdef HAVE_LIBFLTK
#include <FL/Enumerations.H>
#else
#ifndef GIAC_HAS_STO_38
enum Fl_Color {	// standard colors
  FL_BLACK		= 0,
  FL_RED		= 1,
  FL_GREEN		= 2,
  FL_YELLOW		= 3,
  FL_BLUE		= 4,
  FL_MAGENTA		= 5,
  FL_CYAN		= 6,
  FL_WHITE		= 7,
  FL_INACTIVE_COLOR	= 8,
  FL_SELECTION_COLOR	= 15,

  FL_FREE_COLOR		= 16,
  FL_NUM_FREE_COLOR	= 16,

  FL_GRAY_RAMP		= 32,

  // boxtypes limit themselves to these colors so whole ramp is not allocated:
  FL_GRAY0		= 32,	// 'A'
  FL_DARK3		= 39,	// 'H'
  FL_DARK2		= 45,   // 'N'
  FL_DARK1		= 47,	// 'P'
  FL_GRAY		= 49,	// 'R' default color
  FL_LIGHT1		= 50,	// 'S'
  FL_LIGHT2		= 52,	// 'U'
  FL_LIGHT3		= 54,	// 'W'

  FL_COLOR_CUBE		= 56
};
#else // GIAC_HAS_STO_38
#include "../../graphics/colors.h"
enum Fl_Color {
  FL_BLACK = ColorBlack,
  FL_WHITE = ColorWhite,
  FL_RED = ColorRed, // grey1
  FL_YELLOW = ColorYellow, // grey1
  FL_CYAN = ColorCyan, // grey2
  FL_BLUE = ColorBlue, // grey2
  FL_GREEN  = ColorGreen,
  FL_MAGENTA = ColorMagenta,
  FL_TRANSPARENT = ColorTransparent, // transparent
  FL_DARK1		= ColorDarkGrey,	
  FL_GRAY		= ColorGrey,	
  FL_LIGHT1		= ColorWhite,	

};

#endif // GIAC_HAS_STO_38
#endif // FLTK

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // File "Out.txt" for gnuwince
#ifdef GNUWINCE
  extern std::ofstream * outptr;
#endif
  extern int LEGENDE_SIZE;
  extern int COORD_SIZE;
  extern int PARAM_STEP;
  extern int gnuplot_pixels_per_eval;
  extern double gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,gnuplot_zmin,gnuplot_zmax,gnuplot_tmin,gnuplot_tmax,gnuplot_tstep,global_window_xmin,global_window_xmax,global_window_ymin,global_window_ymax,x_tick,y_tick; // ranges
  extern double class_minimum,class_size; // histogram
  extern bool autoscale;
  extern bool has_gnuplot;

  std::string print_DOUBLE_(double d,unsigned ndigits);
  gen makecomplex(const gen & a,const gen &b);

  void local_sto_double(double value,const identificateur & i,GIAC_CONTEXT);
  void local_sto_double_increment(double value,const identificateur & i,GIAC_CONTEXT);
  vecteur quote_eval(const vecteur & v,const vecteur & quoted,GIAC_CONTEXT);

  // if v is the vector argument of pnt, get_style returns attributs and
  // set legende to the legende to be displayed
  vecteur get_style(const vecteur & v,std::string & legende);
  // for a point nothing, segment/line/vect->1st point
  // circle/sphere->diam
  gen get_point(const gen & g,int n,GIAC_CONTEXT);
  gen readvar(const gen & g);
  void read_tmintmaxtstep(vecteur & vargs,gen & t,int vstart,double &tmin,double & tmax,double &tstep,bool & tminmax_defined,bool & tstep_defined,GIAC_CONTEXT);
  int read_attributs(const vecteur & v,vecteur & attributs,GIAC_CONTEXT);
  void read_option(const vecteur & v,double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,vecteur & attributs, int & nstep,int & jstep,int & kstep,GIAC_CONTEXT);
  gen curve_surface_apply(const gen & elem,const gen & b,gen (* func) (const gen &, const gen &,const context *),GIAC_CONTEXT);
  gen apply3d(const gen & e1, const gen & e2,const context * contextptr, gen (* f) (const gen &, const gen &,const context *) );

  gen _point3d(const gen & args,GIAC_CONTEXT);
  gen _point2d(const gen & args,GIAC_CONTEXT);
  gen mkrand2d3d(int dim,int nargs,gen (* f)(const gen &,const context *),GIAC_CONTEXT);
  gen droite_by_equation(const vecteur & v,bool est_plan,GIAC_CONTEXT);
  // given 2 points e and f return equation of line e,f as coeffs a,b,c
  bool point2abc(const gen & e,const gen & f,gen & a,gen & b,gen & c,GIAC_CONTEXT);
  gen abs_norm(const gen & g,GIAC_CONTEXT);
  gen abs_norm2(const gen & g,GIAC_CONTEXT);
  gen dotvecteur(const gen & a,const gen & b,GIAC_CONTEXT);
  bool check3dpoint(const gen & g);
  vecteur remove_not_in_segment(const gen & a,const gen & b,int subtype,const vecteur & v,GIAC_CONTEXT);
  vecteur interpolygone(const vecteur & p,const gen & bb,GIAC_CONTEXT);
  vecteur interdroitecercle(const gen & a,const gen &b,GIAC_CONTEXT);
  vecteur inter2cercles_or_spheres(const gen & centre_a,const gen & rayon_a2,const gen & centre_b,const gen & rayon_b2,bool a2d,GIAC_CONTEXT);
  vecteur curveintercircle(const gen & curve,const gen &circle,bool iscircle,GIAC_CONTEXT);

  bool set_turtle_state(const vecteur & v,GIAC_CONTEXT);
  gen turtle2gen(const logo_turtle & turtle);
  vecteur turtlevect2vecteur(const std::vector<logo_turtle> & v);
  std::vector<logo_turtle> vecteur2turtlevect(const vecteur & v);
  logo_turtle vecteur2turtle(const vecteur & v);
  gen _avance(const gen & g,GIAC_CONTEXT);
  gen _recule(const gen & g,GIAC_CONTEXT);
  gen _position(const gen & g,GIAC_CONTEXT);
  gen _cap(const gen & g,GIAC_CONTEXT);
  gen _tourne_droite(const gen & g,GIAC_CONTEXT);
  gen _tourne_gauche(const gen & g,GIAC_CONTEXT);
  gen _leve_crayon(const gen & g,GIAC_CONTEXT);
  gen _baisse_crayon(const gen & g,GIAC_CONTEXT);
  gen _ecris(const gen & g,GIAC_CONTEXT);
  gen _signe(const gen & g,GIAC_CONTEXT);
  gen _saute(const gen & g,GIAC_CONTEXT);
  gen _pas_de_cote(const gen & g,GIAC_CONTEXT);
  gen _cache_tortue(const gen & g,GIAC_CONTEXT);
  gen _montre_tortue(const gen & g,GIAC_CONTEXT);
  gen _debut_enregistrement(const gen & g0,GIAC_CONTEXT);
  gen _fin_enregistrement(const gen & g0,GIAC_CONTEXT);
  gen _repete(const gen & g,GIAC_CONTEXT);
  gen _crayon(const gen & g,GIAC_CONTEXT);
  gen _efface(const gen & g,GIAC_CONTEXT);
  gen _vers(const gen & g,GIAC_CONTEXT);
  gen _rond(const gen & g,GIAC_CONTEXT);
  gen _disque(const gen & g,GIAC_CONTEXT);
  gen _disque_centre(const gen & g,GIAC_CONTEXT);
  gen _polygone_rempli(const gen & g,GIAC_CONTEXT);
  gen _rectangle_plein(const gen & g,GIAC_CONTEXT);
  gen _triangle_plein(const gen & g,GIAC_CONTEXT);
  gen _dessine_tortue(const gen & g,GIAC_CONTEXT);

  gen _arc(const gen & args,GIAC_CONTEXT);
  typedef gen (* propriete)(const gen & g,GIAC_CONTEXT);
  gen _est(const gen & args,const propriete & f,GIAC_CONTEXT);

  vecteur plotpreprocess(const gen & args,GIAC_CONTEXT);
  vecteur gen2vecteur(const gen & arg);
  bool chk_double_interval(const gen & g,double & inf,double & sup,GIAC_CONTEXT);
  bool readrange(const gen & g,double defaultxmin,double defaultxmax,gen & x, double & xmin, double & xmax,GIAC_CONTEXT);
  void ck_parameter_x(GIAC_CONTEXT);
  void ck_parameter_y(GIAC_CONTEXT);
  void ck_parameter_z(GIAC_CONTEXT);
  void ck_parameter_t(GIAC_CONTEXT);
  void ck_parameter_u(GIAC_CONTEXT);
  void ck_parameter_v(GIAC_CONTEXT);
  void ck_parameter(const gen & ,GIAC_CONTEXT);

  void autoname_plus_plus(std::string & autoname);
  int erase3d();
  gen _erase3d(const gen & args,GIAC_CONTEXT);
  int erase_pos(GIAC_CONTEXT);
  int erase_pos(int current,GIAC_CONTEXT);
  bool is_segment(const gen & e);
  gen remove_at_pnt(const gen & e);
  gen remove_sto(const gen & e);
  vecteur selection2vecteur(const std::vector<int> & selected,GIAC_CONTEXT);
  vecteur selection2vecteureval(const std::vector<int> & selected,GIAC_CONTEXT);
  // find best int in selected (and modify selected)
  // p is the pointed mouse point, eps the precision
  // try_perp=-1 if no try of perp line, =an history_position otherwise
  bool find_best(std::vector<int> & selected,const gen & p,double eps,int try_perp_history_pos,int & pnt_pos,int & history_position,gen & res,GIAC_CONTEXT);
  int findfirstcercle(const vecteur & v);
  int findfirstpoint(const vecteur & v);

  extern bool fastcurveprint;
  void rewrite_with_t_real(gen & eq,const gen & t,GIAC_CONTEXT);
  extern const int _GROUP__VECT_subtype[];
  void streamcopy(FILE * source,FILE * target);
#if !defined VISUALC && !defined __MINGW_H
  int set_nonblock_flag (int desc, int value);
  int set_cloexec_flag (int desc, int value);
#endif
  // runs gnuplot if necessary, returns the FD of the pipe to write to gnuplot
#ifdef WITH_GNUPLOT
  // extern int plot_instructionsh,plot_instructionsw;
  extern std::string PICTautoname;
  void PICTautoname_plus_plus();
  extern std::string gnuplot_name; // name of the program gnuplot
  extern std::string gnuplot_filename; // name of files where we save plots
  extern int gnuplot_fileno; // current index in save plot files
  int run_gnuplot(int & r);
  void gnuplot_wait(int handle,FILE * gnuplot_out_readstream,int ngwait=0);
  FILE * open_gnuplot(bool & clrplot,FILE * & gnuplot_out_readstream,int & r);
  // Plot a function or a set of functions
  void reset_gnuplot_hidden3d(FILE * stream);
  extern bool gnuplot_do_splot;
  std::string gnuplot_traduit(const gen & g);
  void win9x_gnuplot(FILE * stream);
  extern int gnuplot_wait_times;
  void kill_gnuplot();
  extern bool gnuplot_hidden3d,gnuplot_pm3d;
  void gnuplot_set_hidden3d(bool hidden);
  void gnuplot_set_pm3d(bool b);
  /* following obsolete declaration that will be remove in the future
   when all fork/child etc. will be removed */
  extern vecteur plot_instructions;
#endif
  int gnuplot_show_pnt(const symbolic & e,GIAC_CONTEXT);

  // return parametrization for a parametric curve and translate
  // ellipsis/hyperbola to a rational parametrization
  // m will contain the complex depending on gen_t 
  bool find_curve_parametrization(const gen & geo_obj,gen & m,const gen & gen_t,double T,gen & tmin,gen & tmax,gen & tstep,GIAC_CONTEXT);
  // test if a point f is on a parametric curve e
  // compute t if true
  bool on(const gen & e_orig,const gen & f,gen & t,GIAC_CONTEXT);

  gen plotfunc(const gen & f,const gen & vars,const vecteur & attributs,bool clrplot,double function_xmin,double function_xmax,double function_ymin,double function_ymax,double function_zmin, double function_zmax,int nstep,int jstep,bool showeq,GIAC_CONTEXT);
  // return a vector of values with simple decimal representation
  // between xmin/xmax or including xmin/xmax (if bounds is true)
  vecteur ticks(double xmin,double xmax,bool bounds);
  gen plotcontour(const gen & f0,bool contour,GIAC_CONTEXT);
  gen plot_array(const std::vector< std::vector< double> > & fij,int imax,int jmax,double xmin,double xmax,double dx,double ymin,double ymax,double dy,const vecteur & lz,const vecteur & attributs,bool contour,GIAC_CONTEXT);
  bool latex_replot(FILE * stream,const std::string & s);
  bool png_replot(int i);
  bool png_replot(const std::string & s);
  bool terminal_replot(const char * terminal,int i,const char * file_extension);
  bool terminal_replot(const char * terminal,const std::string & s);
  gen approx_area(const gen & f,const gen & x,const gen & a,const gen &b,int n,int method,GIAC_CONTEXT);
  gen _aire(const gen & args,GIAC_CONTEXT);
  gen _perimetre(const gen & args,GIAC_CONTEXT);
  gen funcplotfunc(const gen & args,bool densityplot,const context * contextptr);
  gen _plotfunc(const gen &,GIAC_CONTEXT);
  gen _funcplot(const gen & args,const context * contextptr);
  gen _plotdensity(const gen & args,const context * contextptr);
  extern const unary_function_ptr * const  at_plotfunc;
  extern const unary_function_ptr * const  at_funcplot;
  // gen _plot(const gen &);
  extern const unary_function_ptr * const  at_plot;
  gen remove_at_pnt(const gen & e);

  gen _erase(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_erase;

  gen _pixon(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pixon;

  gen _pixoff(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pixoff;

  gen _droite(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_droite;
  gen _slope(const gen & args,GIAC_CONTEXT);

  gen _demi_droite(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_demi_droite;

  gen _segment(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_segment;

  extern const unary_function_ptr * const  at_inter_unique;
  extern const unary_function_ptr * const  at_polygone_ouvert;

  // segment x->y with attributs c
  gen symb_segment(const gen & x,const gen & y,const vecteur & ,int ,GIAC_CONTEXT);
  // point x color c name nom
  gen symb_pnt_name(const gen & x,const gen & c,const gen & nom,GIAC_CONTEXT);
  // point x and color c
  gen symb_pnt(const gen & x,const gen & c,GIAC_CONTEXT);
  gen pnt_attrib(const gen & point,const vecteur & attributs,GIAC_CONTEXT);
  // point x with default color FL_BLACK
  gen symb_pnt(const gen & x,GIAC_CONTEXT);
  gen _pnt(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pnt;
  extern const unary_function_ptr * const  at_animation;
  gen _animation(const gen & args,GIAC_CONTEXT);
  int animations(const gen & g); // number of animations inside g
  gen get_animation_pnt(const gen & g,int pos);

  gen _point(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_point;

  gen _affixe(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_affixe;

  gen _abscisse(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_abscisse;

  gen _ordonnee(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ordonnee;

  gen _cote(const gen & args,GIAC_CONTEXT);
  gen _coordonnees(const gen & args,GIAC_CONTEXT);
  gen _coordonnees_polaires(const gen & args,GIAC_CONTEXT);
  gen _coordonnees_rectangulaires(const gen & args,GIAC_CONTEXT);
  gen _point_polaire(const gen & args,GIAC_CONTEXT);

  gen _cercle(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_cercle;
  bool centre_rayon(const gen & cercle,gen & centre,gen & rayon,bool absrayon, GIAC_CONTEXT);
  gen _centre(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_centre;
  gen _rayon(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_rayon;

  gen _milieu(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_milieu;

  gen _mediatrice(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_mediatrice;

  gen bissectrice(const gen & args,bool interieur,GIAC_CONTEXT);
  gen _bissectrice(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_bissectrice;

  gen _exbissectrice(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_exbissectrice;

  gen _mediane(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_mediane;

  gen _circonscrit(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_circonscrit;
  gen _orthocentre(const gen & arg_orig,GIAC_CONTEXT);

  gen _inscrit(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_inscrit;

  gen _exinscrit(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_exinscrit;

  gen _isobarycentre(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_isobarycentre;
  gen _barycentre(const gen & args,GIAC_CONTEXT);

  // if suppl is true return in 3-d an object of dim'=3-dim, else dim'=dim
  gen perpendiculaire(const gen & args,bool suppl,GIAC_CONTEXT);
  gen _perpendiculaire(const gen & args,GIAC_CONTEXT);
  gen _orthogonal(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_perpendiculaire;
  gen _mediatrice(const gen & args,GIAC_CONTEXT);

  gen _parallele(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_parallele;

  gen distance2pp(const gen & ee,const gen & ff,GIAC_CONTEXT);
  gen distance2(const gen & f1,const gen & f2,GIAC_CONTEXT);
  gen _longueur2(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_longueur2;

  gen longueur(const gen & f1,const gen & f2,GIAC_CONTEXT);
  gen _longueur(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_longueur;

  gen angle(const gen & f1,const gen & f2,GIAC_CONTEXT);
  gen _angle(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_angle;

  gen scalar_product(const gen & a,const gen & b,GIAC_CONTEXT);
  // return t such that tb+(1-t)a ? ta+(1-t)b is the projection of c on [a,b] 
  gen projection(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT);
  // projection of p on a parametric curve
  // e=symb_cercle or line, returns t
  gen projection(const gen & e,const gen & p,GIAC_CONTEXT);
  gen parameter2point(const vecteur & v,GIAC_CONTEXT);
  gen cercle2curve(const gen & f,GIAC_CONTEXT);
  gen line2curve(const gen & f);
  
  std::vector<int> nearest_point(const vecteur & v,const gen & p,double eps,GIAC_CONTEXT);

  vecteur inter(const gen & a,const gen & b,GIAC_CONTEXT);

  gen _click(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_click;
  class unary_function_eval;
#ifdef RTOS_THREADX
  extern const alias_unary_function_eval __click;
#else
  extern unary_function_eval __click;
#endif

  gen _element(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_element;

  gen _as_function_of(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_as_function_of;

  gen _lieu(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_lieu;

  gen _head(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_head;

  gen _tail(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tail;

  gen _sommets(const gen & args,GIAC_CONTEXT);
  gen _sommets_abca(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_sommets;

  gen _symetrie(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_symetrie;

  gen _rotation(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_rotation;

  gen _projection(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_projection;

  gen _homothetie(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_homothetie;
  gen _est_coplanaire(const gen & args,GIAC_CONTEXT);

  gen _est_aligne(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_aligne;

  gen _est_cocyclique(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_cocyclique;

  gen _est_parallele(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_parallele;

  gen _est_perpendiculaire(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_perpendiculaire;

  gen _est_element(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_element;

  gen _inversion(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_inversion;

  gen _similitude(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_similitude;

  gen translation(const gen & a,const gen & bb,GIAC_CONTEXT);
  gen _translation(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_translation;

  gen _curve(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_curve;

  gen plotparam(const gen & f,const gen & vars,const vecteur & attributs,bool densityplot,double function_xmin,double function_xmax,double function_ymin,double function_ymax,double function_tmin, double function_tmax,double function_tstep,const context * contextptr);
  gen _plotparam(const gen & args,GIAC_CONTEXT);
  gen _paramplot(const gen & args,const context * contextptr);
  extern const unary_function_ptr * const  at_plotparam;
  extern const unary_function_ptr * const  at_paramplot;
  gen paramplotparam(const gen & args,bool clrplot,const context * contextptr);
  gen _plot(const gen & g,const context * contextptr);

  gen _plotpolar(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_plotpolar;
  extern const unary_function_ptr * const  at_polarplot;

  gen _parameq(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_parameq;

  gen _equation(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_equation;
  gen equation_homogene(const gen & eq,GIAC_CONTEXT);

  gen _tangent(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tangent;

  gen _ellipse(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ellipse;

  gen _hyperbole(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_hyperbole;

  gen _parabole(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_parabole;
  gen _conique(const gen & args,GIAC_CONTEXT);

  gen _legende(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_legende;
  gen _distanceat(const gen & args,GIAC_CONTEXT);
  gen _distanceatraw(const gen & args,GIAC_CONTEXT);
  gen _areaatraw(const gen & args,GIAC_CONTEXT);
  gen _areaat(const gen & args,GIAC_CONTEXT);
  gen _slopeatraw(const gen & args,GIAC_CONTEXT);
  gen _slopeat(const gen & args,GIAC_CONTEXT);
  gen _perimeterat(const gen & args,GIAC_CONTEXT);
  gen _perimeteratraw(const gen & args,GIAC_CONTEXT);
  gen _extract_measure(const gen & valeur,GIAC_CONTEXT);
  gen _angleat(const gen & args,GIAC_CONTEXT);
  gen _angleatraw(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_distanceat;
  extern const unary_function_ptr * const  at_distanceatraw;
  extern const unary_function_ptr * const  at_angleat;
  extern const unary_function_ptr * const  at_angleatraw;
  extern const unary_function_ptr * const  at_areaatraw;
  extern const unary_function_ptr * const  at_areaat;
  extern const unary_function_ptr * const  at_perimeteratraw;
  extern const unary_function_ptr * const  at_perimeterat;
  extern const unary_function_ptr * const  at_slopeatraw;
  extern const unary_function_ptr * const  at_slopeat;
  extern const unary_function_ptr * const  at_extract_measure;

  gen _couleur(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_couleur;
  extern const unary_function_ptr * const  at_display ;

  gen _parameter(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_parameter;

  gen _hauteur(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_hauteur;

  gen _triangle(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_triangle;

  gen _triangle_rectangle(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_triangle_rectangle;

  gen _triangle_isocele(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_triangle_isocele;

  gen _triangle_equilateral(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_triangle_equilateral;

  gen _parallelogramme(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_parallelogramme;

  extern const unary_function_ptr * const  at_isopolygone;
  gen _isopolygone(const gen & args,GIAC_CONTEXT);
  gen _carre(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_carre;
  gen _hexagone(const gen & args,GIAC_CONTEXT);

  gen _quadrilatere(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_quadrilatere;

  gen _rectangle(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_rectangle;

  gen _losange(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_losange;

  gen _polygone(const gen & args,GIAC_CONTEXT);
  gen _polygone_ouvert(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_polygone;
  gen _bissectrice(const gen & args,GIAC_CONTEXT);
  gen _exbissectrice(const gen & args,GIAC_CONTEXT);

  gen plotfield(const gen & xp,const gen & yp,const gen & x,const gen & y,double xmin,double xmax,double xstep,double ymin,double ymax,double ystep,double scaling,vecteur & attributs,bool normalize,const context * contextptr);
  gen _plotfield(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_plotfield;
  extern const unary_function_ptr * const  at_fieldplot;

  gen _interactive_plotode(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_interactive_plotode;
  extern const unary_function_ptr * const  at_interactive_odeplot;

  gen _plotode(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_plotode;
  extern const unary_function_ptr * const  at_odeplot;

  std::ostream & archive(std::ostream & os,const gen & e,GIAC_CONTEXT);
  gen unarchive(std::istream & is,GIAC_CONTEXT);
  gen archive_session(bool save_history,std::ostream & os,GIAC_CONTEXT);
  gen archive_session(bool save_history,const std::string & s,GIAC_CONTEXT);
  std::string archive_session(bool save_history,GIAC_CONTEXT);
  gen unarchive_session(std::istream & is,int level, const gen & replace,GIAC_CONTEXT);
  gen unarchive_session(const std::string & s,int level, const gen & replace,GIAC_CONTEXT);
  gen unarchive_session_string(const std::string & s,int level, const gen & replace,GIAC_CONTEXT);

  gen _archive(bool save_history,const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_archive;

  gen _unarchive(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_unarchive;

  bool geo_setup(const vecteur & w,GIAC_CONTEXT);
  gen xyztrange(double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,double tmin,double tmax,double wxmin,double wxmax,double wymin, double wymax, int axes,double class_minimum,double class_size,bool gnuplot_hidden3d,bool gnuplot_pm3d);
  gen _xyztrange(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_xyztrange;
#ifndef RTOS_THREADX
  extern unary_function_eval __xyztrange;
#endif

  gen _switch_axes(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_switch_axes;

  int find_plotseq_args(const gen & args,gen & expr,gen & x,double & x0d,double & xmin,double & xmax,int & niter,vecteur & attributs,GIAC_CONTEXT);
  gen plotseq(const gen& f,const gen&x,double x0,double xmin,double xmax,int niter,const vecteur & attributs,const context * contextptr);
  gen _plotseq(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_plotseq;
  extern const unary_function_ptr * const  at_seqplot;

  gen plotimplicit(const gen& f_orig,const gen&x,const gen & y,double xmin,double xmax,double ymin,double ymax,int nxstep,int nystep,double eps,const vecteur & attributs,bool unfactored,const context * contextptr);
  gen _plotimplicit(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_plotimplicit;
  extern const unary_function_ptr * const  at_implicitplot;

  gen _plotcontour(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_plotcontour;
  extern const unary_function_ptr * const  at_contourplot;
  gen _plotinequation(const gen & f0,GIAC_CONTEXT);
  gen _inter_droite(const gen & args,GIAC_CONTEXT);
  void papier_lignes(vecteur & res,double xmin,double xmax,double ymin,double ymax,double angle,double deltax,double deltay,double pente,const vecteur & attributs,GIAC_CONTEXT);
  gen _dot_paper(const gen & args,GIAC_CONTEXT);
  gen _grid_paper(const gen & args,GIAC_CONTEXT);
  gen _triangle_paper(const gen & args,GIAC_CONTEXT);
  gen _line_paper(const gen & args,GIAC_CONTEXT);

  gen _bitmap(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_bitmap;

  gen _Pictsize(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Pictsize;

  gen _plot_style(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_plot_style ;

  gen _DrawInv(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_DrawInv ;

  gen _Graph(const gen & g,GIAC_CONTEXT);
  gen _DrwCtour(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Graph;
  extern const unary_function_ptr * const  at_DrawFunc;
  extern const unary_function_ptr * const  at_DrawParm;
  extern const unary_function_ptr * const  at_DrawPol;
  extern const unary_function_ptr * const  at_DrwCtour;
  extern const unary_function_ptr * const  at_arc;

  int est_isocele(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT);
  gen _est_isocele(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_isocele;

  bool est_equilateral(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT);
  gen _est_equilateral(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_equilateral;

  bool est_carre(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT);
  gen _est_carre(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_carre;

  int est_losange(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT);
  gen _est_losange(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_losange;

  int est_parallelogramme(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT);
  gen _est_parallelogramme(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_parallelogramme;

  int est_rect(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT);
  gen _est_rectangle(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_rectangle;
 
  gen _est_harmonique(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_harmonique;
  
  gen _div_harmonique(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_div_harmonique;
  
  gen _point_div(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_point_div;
  
  gen _birapport(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_birapport;

  gen _est_harmonique(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_harmonique;

  gen _div_harmonique(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_div_harmonique;

  gen _conj_harmonique(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_conj_harmonique;

  gen _conj_harmoniques(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_conj_harmoniques;

  gen _point_div(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_point_div;

  gen _birapport(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_birapport;

  gen _puissance(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_puissance;

  gen _axe_radical(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_axe_radical;

  gen _polaire(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_polaire;

  gen _pole(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pole;

  gen _polaire_reciproque(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_polaire_reciproque;

  gen _est_orthogonal(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_orthogonal;

  gen _est_conjugue(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_conjugue;

  gen _est_faisceau_cercle(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_faisceau_cercle;
  //=1 si 3 cercles ont meme axe radical,2 si concentriques et 0 sinon
  int est_faisceau_cercle(const gen & c1,const gen & R1,const gen & c2,const gen & R2,const gen & c3,const gen & R3,GIAC_CONTEXT);

  gen _est_faisceau_droite(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_est_faisceau_droite;
  int est_faisceau_droite(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT);

  gen _enveloppe(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_enveloppe;

  int graph_output_type(const giac::gen & g);
  gen put_attributs(const gen & lieu_geo,const vecteur & attributs,GIAC_CONTEXT);
  vecteur seq2vecteur(const gen & g);

  int est_aligne(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT);
  bool est_coplanaire(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT);
  bool est_cocyclique(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT);
  // True if a=coeff*b
  bool est_parallele_vecteur(const vecteur & a,const vecteur &b,gen & coeff,GIAC_CONTEXT);
  bool est_parallele_vecteur(const vecteur & a,const vecteur &b,GIAC_CONTEXT);
  bool est_parallele(const gen & a,const gen & b,GIAC_CONTEXT);
  bool est_perpendiculaire(const gen & a,const gen & b,GIAC_CONTEXT);
  // check if a belongs to b, a must be a complex, b a line or circle or curve
  int est_element(const gen & a_orig,const gen & b_orig,GIAC_CONTEXT);
  bool est_carre(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT);
  int est_isocele(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT);
  bool est_equilateral(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT);
  int est_trianglerect(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT);
  //teste si deux cercles C1 centre c1 rayon R1 et C2  centre c2 rayon R2
  //sont orthogonaux
  bool est_orthogonal(const gen & c1,const gen & R1,const gen & c2,const gen & R2,GIAC_CONTEXT);
  //teste si 4 points forment une division harmonique
  bool est_harmonique(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT);

  gen _vector(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_vector;
  gen _hyperplan(const gen & args,GIAC_CONTEXT);
  gen _hypersphere(const gen & args,GIAC_CONTEXT);
  gen hypersurface(const gen & args,const gen & equation,const gen & vars);
  gen _hypersurface(const gen & args,GIAC_CONTEXT);
  gen _Ox_2d_unit_vector(const gen & args,GIAC_CONTEXT);
  gen _Oy_2d_unit_vector(const gen & args,GIAC_CONTEXT);
  gen _frame_2d(const gen & args,GIAC_CONTEXT);
  gen _Ox_3d_unit_vector(const gen & args,GIAC_CONTEXT);
  gen _Oy_3d_unit_vector(const gen & args,GIAC_CONTEXT);
  gen _Oz_3d_unit_vector(const gen & args,GIAC_CONTEXT);
  gen _frame_3d(const gen & args,GIAC_CONTEXT);
  symbolic symb_curve(const gen & source,const gen & plot);
  extern const unary_function_ptr * const  at_Bezier;


#if defined(GIAC_GENERIC_CONSTANTS) || (defined(VISUALC) && !defined(RTOS_THREADX)) || defined(__x86_64__)
  extern unary_function_ptr point_sommet_tab_op[];
  extern unary_function_ptr nosplit_polygon_function[];
  extern unary_function_ptr measure_functions[];
  extern unary_function_ptr transformation_functions[];
  extern unary_function_ptr plot_sommets[];
  extern unary_function_ptr not_point_sommets[];
  extern unary_function_ptr notexprint_plot_sommets[];
  extern unary_function_ptr implicittex_plot_sommets[];

#else
  extern const unary_function_ptr * const  point_sommet_tab_op;
  extern const unary_function_ptr * const  nosplit_polygon_function;
  extern const unary_function_ptr * const  measure_functions;
  extern const unary_function_ptr * const  transformation_functions;
  extern const unary_function_ptr * const  plot_sommets;
  extern const unary_function_ptr * const  not_point_sommets;
  extern const unary_function_ptr * const  notexprint_plot_sommets;
  extern const unary_function_ptr * const  implicittex_plot_sommets;
#endif

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_PLOT_H
