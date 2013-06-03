/* -*- mode:C++ ; compile-command: "g++ -I. -I.. -I../include -g -c plot.cc -Wall -DIN_GIAC -DHAVE_CONFIG_H -DGIAC_GENERIC_CONSTANTS " -*- */
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
 *  Copyright (C) 2000/7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#ifndef __VISUALC__
#ifndef RTOS_THREADX
#ifndef BESTA_OS
#include <fcntl.h>
#endif
#endif
#endif // __VISUALC__

// Giac headers
#include "gen.h"
#include "usual.h"
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
#include "unary.h"
#include "plot.h"
#include "plot3d.h"
#include "ifactor.h"
#include "gauss.h"
#include "misc.h"
#include "lin.h"
#include "quater.h"
#include "giacintl.h"
#ifdef USE_GMP_REPLACEMENTS
#undef HAVE_GMPXX_H
#undef HAVE_LIBMPFR
#endif

extern const int BUFFER_SIZE;

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#ifdef IPAQ
  int LEGENDE_SIZE=40;
  int COORD_SIZE=26;
  int PARAM_STEP=18;
#else // IPAQ
  int LEGENDE_SIZE=100;
  int COORD_SIZE=30;
  int PARAM_STEP=20;
#endif // IPAQ

#ifdef GNUWINCE
  ofstream * outptr=0;
#endif
  // int plot_instructionsh=300,plot_instructionsw=300;


  const int _GROUP__VECT_subtype[]={_GROUP__VECT,_LINE__VECT,_HALFLINE__VECT,_VECTOR__VECT,_POLYEDRE__VECT,_POINT__VECT,0};
#ifdef IPAQ
  double gnuplot_xmin=-4,gnuplot_xmax=4,gnuplot_ymin=-4,gnuplot_ymax=4,gnuplot_zmin=-5,gnuplot_zmax=5,gnuplot_tmin=-6,gnuplot_tmax=6,gnuplot_tstep=0.3;
#else
  double gnuplot_xmin=-5,gnuplot_xmax=5,gnuplot_ymin=-5,gnuplot_ymax=5,gnuplot_zmin=-5,gnuplot_zmax=5,gnuplot_tmin=-6,gnuplot_tmax=6,gnuplot_tstep=0.3;
#endif
  double global_window_xmin(gnuplot_xmin),global_window_xmax(gnuplot_xmax),global_window_ymin(gnuplot_ymin),global_window_ymax(gnuplot_ymax);
  double x_tick(1.0),y_tick(1.0);
  double class_minimum(0.0),class_size(1.0);
#ifdef GIAC_HAS_STO_38
  int gnuplot_pixels_per_eval=128;
#else
  int gnuplot_pixels_per_eval=400;
#endif
  bool autoscale=true;

  bool has_gnuplot=true;

  bool fastcurveprint=false;

  static gen rand_complex(){
    int i=rand(),j=rand();
    i=i/(RAND_MAX/10)-5;
    j=j/(RAND_MAX/10)-5;
    return i+j*cst_i;
  }

  gen abs_norm(const gen & g,GIAC_CONTEXT){
    if (g.type==_VECT)
      return sqrt(dotvecteur(*g._VECTptr,*g._VECTptr),contextptr);
    else
      return abs(g,contextptr);
  }

  gen abs_norm(const gen & a,const gen & b,GIAC_CONTEXT){
    if (a.type==_VECT)
      return abs_norm(b-a,contextptr);
    gen ax,ay,bx,by;
    reim(a,ax,ay,contextptr);
    reim(b,bx,by,contextptr);
    bx -= ax;
    by -= ay;
    return sqrt(bx*bx+by*by,contextptr);
  }

  gen abs_norm2(const gen & g,GIAC_CONTEXT){
    if (g.type==_VECT)
      return dotvecteur(*g._VECTptr,*g._VECTptr);
    else
      return ratnormal(_lin(g*conj(g,contextptr),contextptr));
  }

  gen dotvecteur(const gen & a,const gen & b,GIAC_CONTEXT){
    return scalar_product(a,b,contextptr);
    /*
      if (a.type==_VECT)
      return dotvecteur(*a._VECTptr,*b._VECTptr);
      else
      return -im(a*conj(cst_i*b,contextptr),contextptr);    
    */
  }

  static gen set3derr(GIAC_CONTEXT){
    return gensizeerr(contextptr); // 2-d instruction
  }

  static void in_autoname_plus_plus(string & autoname){
    int labs=autoname.size();
    if (!labs){
      autoname="A";
      labs=1;
    }
#ifdef GIAC_HAS_STO_38
    int labsmin=1;
#else
    int labsmin=0;
#endif
    for (;labs>labsmin;--labs){
      char c=autoname[labs-1];
      if (c!='Z' && c!='z'){ 
	autoname[labs-1]=char(c+1);
	return;
      }
      autoname[labs-1]='A';
    }
    if (labs==labsmin)
      autoname += 'A';
  }

  void autoname_plus_plus(string & autoname){
#ifdef GIAC_HAS_STO_38
    if (autoname.size()==2 && autoname[0]=='G' && autoname[1]<'Z'){
      if (autoname[1]=='E')
	autoname[1]='G';
      else {
	if (autoname[1]=='Z')
	  autoname[1]='a';
	else
	  ++autoname[1];
      }
      return;
    }
#endif
    for (;;){
      in_autoname_plus_plus(autoname);
      if (gen(autoname,context0).type==_IDNT) // FIXME GIAC_CONTEXT
	break;
    }
  }

#ifdef WITH_GNUPLOT
  string PICTautoname("A");

  void PICTautoname_plus_plus(){
    autoname_plus_plus(PICTautoname);
  }
#endif

  // find last at_erase in history, return next position (or 0 if not found)
  int erase_pos(GIAC_CONTEXT){
    int s=history_out(contextptr).size();
    if (!s)
      return s;
    gen e;
    for (int i=s-1;i>=0;--i){
      e=history_out(contextptr)[i];
      if ( (e.type==_SYMB && (e._SYMBptr->sommet==at_erase 
			      // || equalposcomp(implicittex_plot_sommets,e._SYMBptr->sommet)
			      )) ||
	   (e.type==_FUNC && (*e._FUNCptr==at_erase 
			      // || equalposcomp(implicittex_plot_sommets,e._SYMBptr->sommet)
			      ) ) )
	return i+1;
    }
    return 0;
    // int i=history_out(contextptr).find_last_of(at_erase);
  }

  int erase_pos(int current,GIAC_CONTEXT){
    int s=history_out(contextptr).size();
    if (current>=s)
      current=s-1;
    if (!s)
      return s;
    gen e;
    for (int i=current;i>=0;--i){
      e=history_out(contextptr)[i];
      if ( (e.type==_SYMB && (e._SYMBptr->sommet==at_erase 
			      // || equalposcomp(implicittex_plot_sommets,e._SYMBptr->sommet)
			      )) ||
	   (e.type==_FUNC && (*e._FUNCptr==at_erase 
			      // || equalposcomp(implicittex_plot_sommets,e._SYMBptr->sommet)
			      ) ) )
	return i+1;
    }
    return 0;
    // int i=history_out(contextptr).find_last_of(at_erase);
  }


  gen remove_at_pnt(const gen & e){
    if (e.type==_VECT && e.subtype==_GGB__VECT){
      if (e._VECTptr->size()==2)
	return e._VECTptr->front()+cst_i*e._VECTptr->back();
      if (e._VECTptr->size()==3)
	return change_subtype(e,_POINT__VECT);
    }
    if (e.type==_SYMB && e._SYMBptr->sommet==at_pnt){
      gen & f = e._SYMBptr->feuille;
      if (f.type==_VECT){
	vecteur & v = *f._VECTptr;
	int s=v.size();
	if (s){
	  /*
	    if (s>1 && v[1].type==_VECT){
	    gen & v1=v[1];
	    if (v1.type==_VECT && v1._VECTptr->size()>1){
	    gen v12=(*v1._VECTptr)[1];
	    if (v12.type<=_CPLX)
	    return v12;
	    if (v12.type==_STRNG){
	    string s=*v12._STRNGptr;
	    if (!s.empty() && s[s.size()-1]==' '){
	    gen g(s,context0);
	    if (g.is_symb_of_sommet(at_equal))
	    return g._SYMBptr->feuille._VECTptr->back();
	    }
	    }
	    }
	    }
	  */
	  return v.front();
	}
      }
    }
    return e;
  }

  /*
    static gen unsubtype(const gen & g){
    return (g.type==_VECT)?gen(*g._VECTptr):g;
    }
  */

  gen remove_sto(const gen & e){
    if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_sto))
      return e._SYMBptr->feuille._VECTptr->back();
    else
      return e;
  }

  vecteur selection2vecteur(const vector<int> & selected,GIAC_CONTEXT){
    int i=erase_pos(contextptr);
    vecteur res;
    vector<int>::const_iterator it=selected.begin(),itend=selected.end();
    for (;it!=itend;++it){
      gen e=history_in(contextptr)[i+*it];
      if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_sto))
	res.push_back(e._SYMBptr->feuille._VECTptr->back());
      else
	res.push_back(e);
    }
    return res;
  }

  vecteur selection2vecteureval(const vector<int> & selected,GIAC_CONTEXT){
    int i=erase_pos(contextptr);
    vecteur res;
    vector<int>::const_iterator it=selected.begin(),itend=selected.end();
    for (;it!=itend;++it){
      gen e=history_out(contextptr)[i+*it];
      res.push_back(e);
    }
    return res;
  }

  bool is_segment(const gen & e){
    gen f=remove_at_pnt(e);
    if ( (f.type!=_VECT) || (f._VECTptr->size()!=2))
      return false;
    return true;
  }

  int findfirstpoint(const vecteur & v){
    const_iterateur it=v.begin(),itend=v.end();
    gen p;
    for (;it!=itend;++it){
      p=*it;
      if ( (p.type==_SYMB) && (p._SYMBptr->sommet==at_pnt)){
	p=p._SYMBptr->feuille._VECTptr->front();
	if ( (p.type!=_VECT || p.subtype==_POINT__VECT) && ( (p.type!=_SYMB) || (!equalposcomp(not_point_sommets,p._SYMBptr->sommet))) )
	  return it-v.begin();
      }
    }
    return -1;
  }

  int findfirstcercle(const vecteur & v){
    const_iterateur it=v.begin(),itend=v.end();
    gen p;
    for (;it!=itend;++it){
      p=*it;
      if ( (p.type==_SYMB) && (p._SYMBptr->sommet==at_pnt)){
	p=p._SYMBptr->feuille._VECTptr->front();
	if ( (p.type==_SYMB) && (p._SYMBptr->sommet==at_cercle) )
	  return it-v.begin();
      }
    }
    return -1;
  }

  symbolic symb_curve(const gen & source,const gen & plot){
    return symbolic(at_curve,gen(makevecteur(source,plot),_GROUP__VECT));
  }
  static symbolic symb_perpendiculaire(const gen & aa){
    gen a(aa);
    if (a.type==_VECT) a.subtype=_SEQ__VECT;
    return symbolic(at_perpendiculaire,a);
  }
  static symbolic symb_milieu(const gen & aa){
    gen a(aa);
    if (a.type==_VECT) a.subtype=_SEQ__VECT;
    return symbolic(at_milieu,a);
  }
  static symbolic symb_plotfunc(const gen & a,const gen & b){
    return symbolic(at_plotfunc,makesequence(a,b));
  }
  static gen setplotfuncerr(){
    return gensizeerr(gettext("Plotfunc: bad variable name"));
  }

  // find best int in selected (and modify selected)
  // p is the pointed mouse point, eps the precision
  // try_perp=-1 if no try of perp line, =an history_position otherwise
  bool find_best(vector<int> & selected,const gen & p,double eps,int try_perp_history_pos,int & pnt_pos,int & history_position,gen & res,GIAC_CONTEXT){
    vecteur v=selection2vecteur(selected,contextptr),w=selection2vecteureval(selected,contextptr);
    pnt_pos=findfirstpoint(w);
    if (pnt_pos<0){ // no point nearby, try to make one
      // try intersection or midpoint
      int nsegments=0,n1=-1,n2=-1;
      const_iterateur it=w.begin(),itend=w.end();
      for (;it!=itend;++it){
	if (is_segment(*it)){
	  ++nsegments;
	  n2=it-w.begin();
	  if (n1<0)
	    n1=n2;
	}
      }
      if (nsegments>=2){
	history_position=erase_pos(contextptr)+selected[n1];
	gen l1=remove_sto(history_in(contextptr)[history_position]);
	history_position=erase_pos(contextptr)+selected[n2];
	gen l2=remove_sto(history_in(contextptr)[history_position]);
	res = symbolic(at_head,symbolic(at_inter,makesequence(l1,l2)));
	selected=vector<int>(2);
	selected[0]=n1;
	selected[1]=n2;
	return true;
      }
      if (nsegments){ // near middle point?
	gen m=remove_at_pnt(_milieu(w[n1],0));
	if (!is_undef(m) && is_greater(eps,abs(p-m,contextptr),contextptr)){
	  history_position=erase_pos(contextptr)+selected[n1];
	  gen l1=remove_sto(history_in(contextptr)[history_position]);
	  res=symb_milieu(l1);
	  selected=vector<int>(1,n1);
	  return true;
	}
	// near perp?
	if (try_perp_history_pos>=0){
	  m=remove_at_pnt(history_out(contextptr)[try_perp_history_pos]);
	  gen pm=p-m;
	  gen wv=remove_at_pnt(w[n1]);
	  wv=wv._VECTptr->front()-wv._VECTptr->back();
	  if (is_greater(eps*abs(pm,contextptr)*abs(wv,contextptr),scalar_product(pm,wv,contextptr),contextptr)){
	    history_position=erase_pos(contextptr)+selected[n1];
	    gen l1=remove_sto(history_in(contextptr)[history_position]);
	    res=symb_perpendiculaire(makevecteur(remove_sto(history_in(contextptr)[try_perp_history_pos]),l1));
	    res=symbolic(at_head,symbolic(at_inter,makesequence(l1,res)));
	    selected=vector<int>(1,n1);
	    return true;
	  }
	}
      }
      // try a cercle
      pnt_pos=findfirstcercle(w);
      if (pnt_pos<0)
	return false;
      history_position=erase_pos(contextptr)+selected[pnt_pos];
      res=remove_sto(history_in(contextptr)[history_position]);
      return true;
    }
    else {
      history_position=erase_pos(contextptr)+selected[pnt_pos];
      res=remove_sto(history_in(contextptr)[history_position]);
      return true;
    }
  }

  void streamcopy(FILE * source,FILE * target){
    char c;
    for (;!feof(source);){
      c=fgetc(source);
      if (!feof(source))
	fputc(c,target);
    }
    fclose(source);
  }

#if !defined VISUALC && ! defined __MINGW_H && !defined BESTA_OS
  int set_nonblock_flag (int desc, int value){
    int oldflags = fcntl (desc, F_GETFL, 0);
    /* If reading the flags failed, return error indication now. */
    if (oldflags == -1)
      return -1;
    /* Set just the flag we want to set. */
    if (value != 0)
      oldflags |= O_NONBLOCK;
    else
      oldflags &= ~O_NONBLOCK;
    /* Store modified flag word in the descriptor. */
    return fcntl (desc, F_SETFL, oldflags);
  }

  int set_cloexec_flag (int desc, int value){
    int oldflags = fcntl (desc, F_GETFD, 0);
    /* If reading the flags failed, return error indication now.
       if (oldflags < 0)
       return oldflags;
       // Set just the flag we want to set. */
    if (value != 0)
      oldflags |= FD_CLOEXEC;
    else
      oldflags &= ~FD_CLOEXEC;
    /* Store modified flag word in the descriptor. */
    return fcntl (desc, F_SETFD, oldflags);
  }
#endif

#ifdef WITH_GNUPLOT
  vecteur plot_instructions;

#ifdef WIN32
  static bool win9x=true; // For win95/98/Me we can not pipe to gnuplot
#else
  static bool win9x=false;
#endif
  static pid_t gnuplot_pid=0;
  string gnuplot_name(giac_gnuplot_location);
  int gnuplot_fileno=0;
  bool gnuplot_do_splot=true;
  int gnuplot_in_pipe[2]={-1,-1},gnuplot_out_pipe[2]={-1,-1};
  FILE * gnuplot_out_readstream=0;

#ifdef IPAQ
  bool gnuplot_hidden3d=false;
#else
  bool gnuplot_hidden3d=true;
#endif
  bool gnuplot_pm3d=false;
  static int gnuplot_wait_times=5;
  string gnuplot_filename("session3d");

  bool ck_gnuplot(string & s){
    if (!access(s.c_str(),R_OK))
      return true;
    if (!access("/usr/local/bin/gnuplot",R_OK)){
      s="/usr/local/bin/gnuplot";
      return true;
    }
    if (!access("/usr/bin/gnuplot",R_OK)){
      s="/usr/bin/gnuplot";
      return true;
    }
    if (!access((xcasroot()+"gnuplot").c_str(),R_OK)){
      s=xcasroot()+"gnuplot";
      return true;
    }
    return false;
  }

  int run_gnuplot(int & r){
#ifdef WIN32
    has_gnuplot=false;
    return -1;
#ifdef GNUWINCE
#endif
    gnuplot_name=xcasroot()+"pgnuplot.exe";
#endif //WIN32
    if (!gnuplot_pid){ // start a gnuplot process
      if (!ck_gnuplot(gnuplot_name))
	{
	  has_gnuplot=false;
	  return -1;
	}
      if (pipe(gnuplot_in_pipe) || pipe(gnuplot_out_pipe))
	throw(std::runtime_error("Plot error: Unable to create pipe"));
      gnuplot_pid=fork();
      if (gnuplot_pid<(pid_t) 0)
	throw(std::runtime_error("Plot error: Unable to fork"));
      if (!gnuplot_pid){ // child process, redirect input/output
#ifndef GNUPLOT_IO
	int gnuplot_out=open("gnuplot.out",O_WRONLY | O_CREAT); 
	dup2(gnuplot_out,STDERR_FILENO);
	close(gnuplot_out);
#else
	dup2(gnuplot_out_pipe[1],STDERR_FILENO);
	close(gnuplot_out_pipe[1]);
	set_cloexec_flag(STDERR_FILENO,0);
#endif
	dup2(gnuplot_in_pipe[0],STDIN_FILENO);
	close(gnuplot_in_pipe[0]);
	// cout << errno << endl;
#ifdef IPAQ
	execlp("gnuplot","gnuplot -geometry 235x300 -noraise",0);
#else
	execlp("gnuplot","gnuplot -geometry 500x700 -noraise",0); // start gnuplot
#endif
	exit(1); // in case gnuplot is not found
      }
      else {
	usleep(10000);
	bool clrplot=false;
	FILE * stream = open_gnuplot(clrplot,gnuplot_out_readstream,r);
	gnuplot_out_readstream=fdopen(r,"r");
	gnuplot_wait(r,gnuplot_out_readstream,3);
	// close(gnuplot_out_pipe[1]);
	// close(gnuplot_in_pipe[0]);
      }
    }
    r=gnuplot_out_pipe[0];
    return gnuplot_in_pipe[1];
    return 0;
  }

  FILE * open_gnuplot(bool & clrplot,FILE * & gnuplot_out_r,int & r){
    if (win9x){
      r=-1;
#ifdef WIN32
      gnuplot_name="'"+xcasroot()+"wgnuplot.exe'"; // was xcasroot()+"wgnuplot.exe", 
#ifdef GNUWINCE 
      gnuplot_name="/gnuplot.exe";
#endif
#endif
      FILE * stream= fopen("gnuplot.txt","a");
      if (!stream)
	stream=fopen("gnuplot.txt","w");
      if (!stream)
	setsizeerr(gettext("Gnuplot write error"));
      return stream;
    }
    FILE * res=fdopen(run_gnuplot(r),"w");
    if (!res)
      setsizeerr(gettext("Gnuplot write error"));
#ifdef __APPLE__
    if (debug_infolevel)
      printf("set terminal aqua\n");
    fprintf(res,"set terminal aqua\n");
#endif
    // cerr << errno << endl;
    gnuplot_out_r = gnuplot_out_readstream;
    // cerr << r << " " << errno << endl;
    return res;
  }

  void terminal_stream_replot(const char * terminal,FILE * stream,const char * filename){
#ifdef IPAQ
    if (string(terminal)=="png")
      return;
#endif
#ifdef __APPLE__
    if (string(terminal)=="png")
      return;
#endif
    fprintf(stream,"set terminal %s\nset output \"%s\"\nreplot\nset output\n",terminal,filename);
    if (debug_infolevel)
      printf("set terminal %s\nset output \"%s\"\nreplot\nset output\n",terminal,filename);
#ifdef WIN32
    if (debug_infolevel)
      printf("set terminal windows\n"); 
    fprintf(stream,"set terminal windows\n");
#else
#ifdef __APPLE__
    if (debug_infolevel)
      printf("set terminal aqua\n");
    fprintf(stream,"set terminal aqua\n");
#else
    if (debug_infolevel)
      printf("set terminal x11\n"); 
    fprintf(stream,"set terminal x11\n");
#endif
#endif
    fflush(stream);
  }

  void terminal_stream_replot(const char * terminal,FILE * stream,int i,const char * file_extension){
    terminal_stream_replot(terminal,stream,(gnuplot_filename+print_INT_(i)+"."+string(file_extension)).c_str());
  }

  void gnuplot_wait(int handle,FILE * gnuplot_out_readstream,int ngwait){
#ifndef GNUPLOT_IO
    usleep(200000);
    return;
#endif
    // cerr << "gnuplot_wait " << handle << " " << gnuplot_out_readstream << " " << ngwait << endl;
    // wait for gnuplot to be quiet
    if (handle>0 && gnuplot_out_readstream){
      int i,ntry=500;
      for (;ntry;){
	usleep(10000);
	set_nonblock_flag(handle,1); 
	i=fgetc(gnuplot_out_readstream);
	// cerr << char(i) ;
	if (char(i)=='>')
	  --ngwait;
	set_nonblock_flag(handle,0); 
	if (i==EOF){
	  if (ngwait<=0)
	    break;
	  usleep(10000);
	  --ntry;
	}
	set_nonblock_flag(handle,1); 
	while (i!=EOF){
	  i=fgetc(gnuplot_out_readstream);
	  // cerr << char(i);
	  if (char(i)=='>')
	    --ngwait;
	}
	set_nonblock_flag(handle,0); 
      }
      // fclose(gnuplot_out_readstream);
      usleep(10000);
      // cerr << "gnuplot_wait end " << getpid() << " " <<clock() << endl;
    }
    else
      ;// cerr << "gnuplot wait no input" << endl;
  }

  bool terminal_replot(const char * terminal,int i,const char * file_extension){
    if (!has_gnuplot)
      return 0;
    if (win9x){
      FILE * stream2 =fopen("gnuplot.gp","w");
      FILE * stream =fopen("gnuplot.txt","r");
      streamcopy(stream,stream2);
      terminal_stream_replot(terminal,stream2,i,file_extension);
      system((gnuplot_name+" gnuplot.gp").c_str());
    }
    else {
      bool clrplot;
      int r;
      FILE * gnuplot_out_readstream,* stream = open_gnuplot(clrplot,gnuplot_out_readstream,r);
      terminal_stream_replot(terminal,stream,i,file_extension);
      gnuplot_wait(r,gnuplot_out_readstream,gnuplot_wait_times);
    }
    return true;
  }

  bool terminal_replot(const char * terminal,const string & s){
    if (!has_gnuplot)
      return 0;
#ifdef IPAQ
    if (string(terminal)=="png")
      return 0;
#endif
#ifdef __APPLE__
    if (string(terminal)=="png")
      return 0;
#endif
    if (win9x){
      FILE * stream = fopen("gnuplot.txt","r");
      FILE * stream2 =fopen("gnuplot.gp","w");
      streamcopy(stream,stream2);
      terminal_stream_replot(terminal,stream2,s.c_str());
      system((gnuplot_name+" gnuplot.gp").c_str());
    }
    else {
      bool clrplot;
      int r;
      FILE * gnuplot_out_readstream,* stream = open_gnuplot(clrplot,gnuplot_out_readstream,r);
      terminal_stream_replot(terminal,stream,s.c_str());
      gnuplot_wait(r,gnuplot_out_readstream,gnuplot_wait_times);
    }
    return true;
  }

  void win9x_gnuplot(FILE * stream){
    if (!win9x){
#ifndef IPAQ
#ifndef __APPLE__
      latex_replot(stream,(gnuplot_filename+print_INT_(gnuplot_fileno)+".tex").c_str());
      terminal_stream_replot("png",stream,gnuplot_fileno,"png");
#endif
#endif
      fflush(stream);
      return;
    }
    fflush(stream);
    fclose(stream);
#ifdef GNUWINCE
    (*outptr) << "// <plot> </plot>" << endl;
    return ;
#endif
    // Copy to a temporary gnuplot
    unlink("gnuplot.gp");
    FILE * stream2 = fopen("gnuplot.gp","w");
    stream = fopen("gnuplot.txt","r");
    streamcopy(stream,stream2);
    fprintf(stream2,"pause -1 \"Press RETURN when OK\"\n");
    latex_replot(stream2,(gnuplot_filename+print_INT_(gnuplot_fileno)+".tex").c_str());
    terminal_stream_replot("png",stream2,gnuplot_fileno,"png");
    fflush(stream2);
    fclose(stream2);
    system((gnuplot_name+" gnuplot.gp").c_str());
  }

  void kill_gnuplot(){
    if (gnuplot_pid){
      FILE *stream;
      stream = fdopen (gnuplot_in_pipe[1], "w");
      fprintf(stream,"\n\nq\n");
      if (fclose(stream))
	cerr << "Error closing gnuplot" << endl;
    }
  }

  bool png_replot(int i){
    return terminal_replot("png",i,"png");
  }

  bool png_replot(const string & s){
    return terminal_replot("png",s);
  }

  string gnuplot_traduit(const gen & g){
    string s(evalf(g,1,0).print());
    string f_s(s),ff_s;
    string::iterator it=f_s.begin(),itend=f_s.end();
    for (;it!=itend;++it){
      if (*it=='^')
	ff_s += "**";
      else
	ff_s += *it;
    }
    return  ff_s;
  }

#endif // WITH_GNUPLOT

  static void plotpreprocess(gen & g,vecteur & quoted,GIAC_CONTEXT){
    gen tmp=eval(g,contextptr);
    if (tmp.type==_IDNT){
      g=tmp;
      quoted=vecteur(1,tmp);
      return;
    }
    if (tmp.type==_VECT){
      bool done=true;
      const_iterateur it=tmp._VECTptr->begin(),itend=tmp._VECTptr->end();
      if (it!=itend){
	for (;it!=itend;++it){
	  if (it->type!=_IDNT && !it->is_symb_of_sommet(at_at))
	    break;
	}
	if (it==itend){
	  g=tmp;
	  quoted=*tmp._VECTptr;
	}
        else
	  done=false;
      }
      else
	done=false;
      if (!done){
	if (g.type==_VECT)
	  quoted=*g._VECTptr;
	else
	  quoted=vecteur(1,g);
      }
    }
    else {
      quoted=vecteur(1,g);
    }
  }

  vecteur quote_eval(const vecteur & v,const vecteur & quoted,GIAC_CONTEXT){
    /*
      vecteur l(quoted);
      lidnt(v,l);
      int qs=quoted.size();
      l=vecteur(l.begin()+qs,l.end());
      vecteur lnew=*eval(l,1,contextptr)._VECTptr;
      vecteur w=subst(v,l,lnew,true,contextptr);
      return w;
    */
    const_iterateur it=quoted.begin(),itend=quoted.end();
    vector<int> save;
    for (;it!=itend;++it){
      gen tmp=*it;
      if (tmp.is_symb_of_sommet(at_equal))
	tmp=tmp._SYMBptr->feuille._VECTptr->front();
      if (tmp.type!=_IDNT)
	save.push_back(-1);
      else {
	if (contextptr && contextptr->quoted_global_vars){
	  contextptr->quoted_global_vars->push_back(tmp);
	  save.push_back(0);
	}
	else {
	  if (tmp._IDNTptr->quoted){
	    save.push_back(*tmp._IDNTptr->quoted);
	    *tmp._IDNTptr->quoted=1;
	  }
	  else
	    save.push_back(0);
	}
      }
    }
    vecteur res(v);
    int s=v.size();
    for (int i=0;i<s;++i){
#ifndef NO_STDEXCEPT
      try {
#endif
	bool done=false;
	if (v[i].is_symb_of_sommet(at_prod) && v[i]._SYMBptr->feuille.type==_VECT){ // hack for polarplot using re(rho)
	  vecteur vi = *v[i]._SYMBptr->feuille._VECTptr;
	  if (!vi.empty() && vi.front().is_symb_of_sommet(at_re)){
	    vi.front()=vi.front()._SYMBptr->feuille;
	    gen tmp=eval(vi,contextptr);
	    if (tmp.type==_VECT){
	      vi=*tmp._VECTptr;
	      vi.front()=symbolic(at_re,vi.front());
	      res[i]=_prod(vi,contextptr);
	      done=true;
	    }
	  }
	}
	if (!done)
	  res[i]=eval(v[i],contextptr);
#ifndef NO_STDEXCEPT
      } catch (std::runtime_error & ){
	;  //    *logptr(contextptr) << e.what() << endl;
      }
#endif
    }
    it=quoted.begin();
    for (int i=0;it!=itend;++it,++i){
      if (save[i]>=0){
	if (contextptr && contextptr->quoted_global_vars)
	  contextptr->quoted_global_vars->pop_back();
	else {
	  gen tmp=*it;
	  if (tmp.is_symb_of_sommet(at_equal))
	    tmp=tmp._SYMBptr->feuille._VECTptr->front();
	  if (tmp.type==_IDNT && tmp._IDNTptr->quoted)
	    *tmp._IDNTptr->quoted=save[i]>0?save[i]:0;
	}
      }
    }
    return res;
  }

  // args -> vector
  // add vx_var if args is not a seq
  // evaluate v[1], if it's not an idnt or a vector of idnt keep v[1]
  // evaluate v with v[1] quoted
  vecteur plotpreprocess(const gen & args,GIAC_CONTEXT){
    vecteur v;
    if ((args.type!=_VECT) || (args.subtype!=_SEQ__VECT) )
      v=makevecteur(args,vx_var);
    else {
      v=*args._VECTptr;
      if (v.empty())
	return vecteur(1,gensizeerr(contextptr));
      if (v.size()==1)
	v.push_back(vx_var);
    }
    // find quoted variables from v[1]
    vecteur quoted;
    if ( v[1].type==_SYMB && (v[1]._SYMBptr->sommet==at_equal ||v[1]._SYMBptr->sommet==at_same ))
      plotpreprocess(v[1]._SYMBptr->feuille._VECTptr->front(),quoted,contextptr);
    else
      plotpreprocess(v[1],quoted,contextptr);
    return quote_eval(v,quoted,contextptr);
  }

  gen pnt_attrib(const gen & point,const vecteur & attributs,GIAC_CONTEXT){
    if (is_undef(point))
      return point;
    if (attributs.empty())
      return symb_pnt(point,default_color(contextptr),contextptr);
    int s=attributs.size();
    if (s==1)
      return symb_pnt(point,attributs[0],contextptr);
    if (s>=3)
      return symb_pnt_name(point,symbolic(at_couleur,attributs),attributs[1],contextptr);
    return symb_pnt_name(point,attributs[0],attributs[1],contextptr);
  }

  string print_DOUBLE_(double d,unsigned ndigits){
    char s[256];
    ndigits=ndigits<2?2:ndigits;
    ndigits=ndigits>15?15:ndigits;
    sprintfdouble(s,("%."+print_INT_(ndigits)+"g").c_str(),d);
    return s;
  }

  static const int arc_en_ciel_colors=105;

  inline int density(double z,double fmin,double fmax){
    // z -> 256+arc_en_ciel_colors*(z-fmin)/(fmax-fmin)
    if (z<fmin)
      return 256;
    if (z>fmax)
      return 256+arc_en_ciel_colors;
    return 256+int(arc_en_ciel_colors*(z-fmin)/(fmax-fmin));
  }

  // horizontal scale for colors
  static vecteur densityscale(double xmin,double xmax,double ymin,double ymax,double fmin, double fmax,int n,GIAC_CONTEXT){
    vecteur res;
    if (n<10)
      n=10;
    if (n>100)
      n=100;
    double dx=(xmax-xmin)/n;
    double x=xmin;
    for (int i=0;i<n;i++){
      gen A(x,ymin);
      gen B(x,ymax);
      x+=dx;
      gen C(x,ymax);
      gen D(x,ymin);
      vecteur attrib(1,256+int(i*double(arc_en_ciel_colors)/n)+_FILL_POLYGON+(i?_QUADRANT4:_QUADRANT2));
      if (!i)
	attrib.push_back(string2gen(print_DOUBLE_(fmin,4),false));
      if (i==n-1)
	attrib.push_back(string2gen(print_DOUBLE_(fmax,4),false));
      res.push_back(pnt_attrib(gen((i?makevecteur(D,A,B,C,D):makevecteur(B,C,D,A,B)),_GROUP__VECT),attrib,contextptr));
    }
    return res;
  }

  // return a vector of values with simple decimal representation
  // between xmin/xmax or including xmin/xmax (if bounds is true)
  vecteur ticks(double xmin,double xmax,bool bounds){
    if (xmax<xmin)
      swapdouble(xmin,xmax);
    double dx=xmax-xmin;
    vecteur res;
    if (dx==0)
      return res;
    double d=std::pow(10.0,std::floor(std::log10(dx)));
    if (dx<2*d)
      d=d/5;
    else {
      if (dx<5*d)
	d=d/2;
    }
    double x1=std::floor(xmin/d)*d;
    double x2=(bounds?std::ceil(xmax/d):std::floor(xmax/d))*d;
    for (double x=x1+(bounds?0:d);x<=x2;x+=d){
      if ( std::abs(x-int(x+.5))<1e-6*d)
	res.push_back(int(x+.5));
      else
	res.push_back(x);
    }
    return res;
  }

  // return a sequence of filled polygons with a color mapped from fmin..fmax
  // to 256..256+125 from a matrix of points
  // if regular is true, m is assumed to be equidistributed in x and y
  static vecteur density(const matrice & m,double fmin,double fmax,bool regular,GIAC_CONTEXT){
#ifdef RTOS_THREADX
    return vecteur(1,undef);
#else
    if (!ckmatrix(m,true))
      return vecteur(1,undef);
    int r=m.size(); // imax
    int c=m.front()._VECTptr->size(); // jmax
    if (regular){
      vector< vector< double > > fij;
      fij.reserve(r);
      for (int i=0;i<r;++i){
	vector<double> tmp;
	tmp.reserve(c);
	for (int j=0;j<c;++j){
	  tmp.push_back(evalf_double(m[i][j][2],eval_level(contextptr),contextptr)._DOUBLE_val);
	}
	fij.push_back(tmp);
      }
      double xmin,xmax,dx,ymin,ymax,dy;
      gen xymin=m[0][0];
      xmin=xymin[0]._DOUBLE_val;
      ymin=xymin[1]._DOUBLE_val;
      gen xymax=m[r-1][c-1];
      xmax=xymax[0]._DOUBLE_val;
      ymax=xymax[1]._DOUBLE_val;
      dx=(xmax-xmin)/(r-1);
      dy=(ymax-ymin)/(c-1);
      vecteur lz(arc_en_ciel_colors);
      double df=(fmax-fmin)/arc_en_ciel_colors;
      for (int i=0;i<arc_en_ciel_colors;++i)
	lz[i]=fmin+i*df;
      vecteur attr(arc_en_ciel_colors);
      for (int i=0;i<arc_en_ciel_colors;++i)
	attr[i]=_FILL_POLYGON+257+i;
      gen rect=pnt_attrib(gen(makevecteur(gen(xmin,ymin),gen(xmax,ymin),gen(xmax,ymax),gen(xmin,ymax),gen(xmin,ymin)),_GROUP__VECT),vecteur(1,_FILL_POLYGON+256),contextptr);
      vecteur niveaux=ticks(fmin,fmax,false);
      lz=mergevecteur(lz,niveaux);
      attr=mergevecteur(attr,vecteur(niveaux.size(),default_color(contextptr)));
      vecteur legendes=mergevecteur(vecteur(arc_en_ciel_colors,string2gen("",false)),niveaux);
      gen res=plot_array(fij,r,c,xmin,xmax,dx,ymin,ymax,dy,lz,makevecteur(attr,legendes),true,contextptr);
      if (res.type==_VECT){
	vecteur v = *res._VECTptr;
	v.insert(v.begin(),rect);
	return v;
      }
      return makevecteur(rect,res);
    }
    else {
      vecteur res;
      for (int i=1;i<r-1;++i){
	vecteur & prec = *m[i-1]._VECTptr;
	vecteur & cur = *m[i]._VECTptr;
	vecteur & next = *m[i+1]._VECTptr;
	for (int j=1;j<c-1;++j){
	  vecteur &m=*cur[j]._VECTptr;
	  gen M=m[0]+cst_i*m[1];
	  if (m[2].type==_DOUBLE_){
	    vecteur &c=*cur[j-1]._VECTptr;
	    vecteur &d=*cur[j+1]._VECTptr;
	    vecteur &a=*prec[j]._VECTptr;
	    vecteur &b=*next[j]._VECTptr;
	    gen x1=(a[0]+m[0])/2;
	    gen x2=(b[0]+m[0])/2;
	    gen y1=(d[1]+m[1])/2;
	    gen y2=(c[1]+m[1])/2;
	    gen A=x1+cst_i*y1;
	    gen B=x1+cst_i*y2;
	    gen C=x2+cst_i*y2;
	    gen D=x2+cst_i*y1;
	    int e=density(m[2]._DOUBLE_val,fmin,fmax);
	    res.push_back(pnt_attrib(gen(makevecteur(A,B,C,D,A),_GROUP__VECT),vecteur(1,e+_FILL_POLYGON),contextptr));
	  }
	}
      }
      return res;
    } // end not regular
#endif
  }

  void local_sto_double(double value,const identificateur & i,GIAC_CONTEXT){
    control_c();
    if (contextptr)
      (*contextptr->tabptr)[i.id_name]=value;
    else
      i.localvalue->back()=value;
  }

  void local_sto_double_increment(double value,const identificateur & i,GIAC_CONTEXT){
    control_c();
    if (contextptr)
      (*contextptr->tabptr)[i.id_name] += value;
    else
      i.localvalue->back() += value;
  }

  gen plotfunc(const gen & f,const gen & vars,const vecteur & attributs,bool densityplot,double function_xmin,double function_xmax,double function_ymin,double function_ymax,double function_zmin, double function_zmax,int nstep,int jstep,bool showeq,const context * contextptr){
    double step=(function_xmax-function_xmin)/nstep;
    if (step<=0 || (function_xmax-function_xmin)/step>1e5)
      return gensizeerr(gettext("Plotfunc: unable to discretize: xmin, xmax, step=")+print_DOUBLE_(function_xmin,12)+","+print_DOUBLE_(function_xmax,12)+","+print_DOUBLE_(step,12));
    vecteur res;
    int color=default_color(contextptr);
    gen attribut=attributs.empty()?color:attributs[0];
    if (attribut.type==_INT_)
      color=attribut.val;
    if (f.type==_VECT){ // multi-plot
      vecteur & vf=*f._VECTptr;
      unsigned s=vf.size();
      vecteur vattribut;
      if (attribut.type==_VECT)
	vattribut=gen2vecteur(attribut);
      for (unsigned j=0;j<s;++color,++j)
	vattribut.push_back(color);
      vecteur res;
      for (unsigned i=0;i<s;++i){
	vecteur cur_attributs(1,vattribut[i]);
	if (attributs.size()>1 && attributs[1].type==_VECT && attributs[1]._VECTptr->size()>i)
	  cur_attributs.push_back((*attributs[1]._VECTptr)[i]);
	gen tmp=plotfunc(vf[i],vars,cur_attributs,false,function_xmin,function_xmax,function_ymin,function_ymax,function_zmin,function_zmax,nstep,jstep,showeq,contextptr);
	if (tmp.type==_VECT) 
	  res=mergevecteur(res,*tmp._VECTptr);
	else
	  res.push_back(tmp);
      }
      return res; // gen(res,_SEQ__VECT);
    }
#ifndef GNUWINCE
    if (vars.type==_IDNT){ // function plot
      gen locvar(vars);
      locvar.subtype=0;
      gen y=quotesubst(f,vars,locvar,contextptr),yy;
      // gen y=f.evalf2double(),yy;
      double j,entrej,oldj=0,xmin=function_xmin,xmax=function_xmax+step/2;
      bool joindre;
      vecteur localvar(1,vars);
      context * newcontextptr= (context *) contextptr;
      int protect=bind(vecteur(1,xmin),localvar,newcontextptr);
      vecteur chemin;
      for (double i=xmin;i<xmax;i+= step){
	local_sto_double(i,*vars._IDNTptr,newcontextptr);
	// vars._IDNTptr->localvalue->back()._DOUBLE_val =i;
	yy=y.evalf2double(eval_level(contextptr),newcontextptr);
	if (yy.type!=_DOUBLE_){
	  if (!chemin.empty())
	    res.push_back(pnt_attrib(symb_curve(gen(makevecteur(vars+cst_i*f,vars,xmin,i,showeq),_PNT__VECT),gen(chemin,_GROUP__VECT)),attributs.empty()?color:attributs,contextptr));
	  xmin=i;
	  chemin.clear();
	  continue;
	}
	j=yy._DOUBLE_val;
	if (i!=xmin){
	  if (fabs(oldj-j)>(function_ymax-function_ymin)/5){ // try middle-pnt
	    local_sto_double_increment(-step/2,*vars._IDNTptr,newcontextptr);
	    // vars._IDNTptr->localvalue->back()._DOUBLE_val -= step/2;
	    yy=y.evalf2double(eval_level(contextptr),newcontextptr);
	    if (yy.type!=_DOUBLE_)
	      joindre=false;
	    else {
	      entrej=yy._DOUBLE_val;
	      if (j>oldj)
		joindre=(j>=entrej) && (entrej>=oldj);
	      else
		joindre=(j<=entrej) && (entrej<=oldj);
	    }
	    local_sto_double_increment(step/2,*vars._IDNTptr,newcontextptr);
	    // vars._IDNTptr->localvalue->back()._DOUBLE_val += step/2;
	  }
	  else
	    joindre=true;
	}
	else
	  joindre=false;
	if (joindre)
	  chemin.push_back(gen(i,j));
	else {
	  if (!chemin.empty())
	    res.push_back(pnt_attrib(symb_curve(gen(makevecteur(vars+cst_i*f,vars,xmin,i,showeq),_PNT__VECT),gen(chemin,_GROUP__VECT)),attributs.empty()?color:attributs,contextptr));
	  xmin=i;
	  chemin=vecteur(1,gen(i,j));
	}
	oldj=j;
      }
      if (!chemin.empty())
	res.push_back(pnt_attrib(symb_curve(gen(makevecteur(vars+cst_i*f,vars,xmin,xmax,showeq),_PNT__VECT),gen(chemin,_GROUP__VECT)),attributs.empty()?color:attributs,contextptr));
      leave(protect,localvar,newcontextptr);
#ifndef WIN32
      //      if (child_id)
      //	plot_instructions.push_back(res);
#endif // WIN32
      if (res.size()==1)
	return res.front();
      // gen e(res,_SEQ__VECT);
      return res; // e;
    } // end 1-var function plot
#endif
    ck_parameter_x(contextptr);
    ck_parameter_y(contextptr);
    int s=0;
    gen var1,var2;
    if (vars.type==_VECT){
      s=vars._VECTptr->size();
      if (s>2)
	s=0;
      if (s)
	var1=vars._VECTptr->front();
      if (s==2)
	var2=vars._VECTptr->back();
      if ((var1.type!=_IDNT) || (var2.type!=_IDNT))
	s=0;
    }
    if (vars.type==_IDNT){
      s=1;
      var1=vars;
    }
    if (!s)
      return gentypeerr(gettext("Plotfunc 2nd arg must be var or [var1,var2]"));
    gen ff=f; // f.evalf(eval_level(contextptr),contextptr);
    if (s==1)
      ff=makevecteur(t__IDNT_e,subst(ff,*var1._IDNTptr,t__IDNT_e,false,contextptr));
    if (s==2){
      ff=subst(ff,*var1._IDNTptr,u__IDNT_e,false,contextptr);
      ff=subst(ff,*var2._IDNTptr,v__IDNT_e,false,contextptr);
      ff=makevecteur(u__IDNT_e,v__IDNT_e,ff);
    }
    gen r=symb_plotfunc(f,vars);
    if (s==2){
      gen vars(makevecteur(var1,var2));
      vecteur vals(2);
      double x=function_xmin,y=function_ymin;
      int nu,nv;
      if (jstep){
	nu=nstep;
	nv=jstep;
      }
      else {
	nu=int(std::sqrt(double(nstep)));
	nv=int(std::sqrt(double(nstep)));
      }
      double dx=(function_xmax-function_xmin)/nu;
      double dy=(function_ymax-function_ymin)/nv;
      double fmin=1e300,fmax=-fmin;
      // Compute a grid of values
      vecteur values;
      for (int i=0;i<=nu;++i,x+=dx){
	y=function_ymin;
	vals[0]=x;
	vecteur tmp;
	for (int j=0;j<=nv;++j,y+=dy){
	  vals[1]=y;
	  gen fval=evalf_double(evalf(subst(f,vars,vals,false,contextptr),eval_level(contextptr),contextptr),1,contextptr);
	  if (fval.type==_DOUBLE_){
	    if (fval._DOUBLE_val<fmin)
	      fmin=fval._DOUBLE_val;
	    if (fval._DOUBLE_val>fmax)
	      fmax=fval._DOUBLE_val;
	  }
	  tmp.push_back(gen(makevecteur(x,y,fval),_POINT__VECT));
	}
	values.push_back(gen(tmp,_GROUP__VECT));
      }
      if (densityplot){
	if (function_zmin==function_zmax){
	  function_zmin=fmin;
	  function_zmax=fmax;
	}
	dy=(function_ymax-function_ymin)/10;
	dx=(function_xmax-function_xmin)/10;
	r=mergevecteur(density(values,function_zmin,function_zmax,true,contextptr),densityscale(function_xmin+dx,function_xmax-dx,function_ymin-dy,function_ymin-2*dy,function_zmin,function_zmax,20,contextptr));
      }
      else
	r=pnt_attrib(hypersurface(gen(makevecteur(makevecteur(var1,var2,f),makevecteur(var1,var2),makevecteur(function_xmin,function_ymin),makevecteur(function_xmax,function_ymax),gen(values,_GROUP__VECT)),_PNT__VECT),z__IDNT_e-f,makevecteur(var1,var2,z__IDNT_e)),attributs.empty()?color:attributs,contextptr);
    }
    if (!has_gnuplot)
      return r;
#ifdef WITH_GNUPLOT
    int out_handle;
    bool clrplot=false;
    FILE * gnuplot_out_readstream,* stream = open_gnuplot(clrplot,gnuplot_out_readstream,out_handle);
    int ng=0;
#ifdef IPAQ
    fprintf(stream,"set samples 10\n");
    ++ng;
    //    fprintf(stream,"show samples\n");
#endif
    r.subtype=gnuplot_fileno;
    reset_gnuplot_hidden3d(stream);
    ++ng;
    if (debug_infolevel)
      printf("set xrange [%g:%g]\n",function_xmin,function_xmax);
    fprintf(stream,"set xrange [%g:%g]\n",function_xmin,function_xmax);
    ++ng;
    if (s==2){
      if (debug_infolevel)
	printf("set urange [%g:%g]\n",function_xmin,function_xmax);
      fprintf(stream,"set urange [%g:%g]\n",function_xmin,function_xmax);
      ++ng;
      if (debug_infolevel)
	printf("set yrange [%g:%g]\n",function_ymin,function_ymax);
      fprintf(stream,"set yrange [%g:%g]\n",function_ymin,function_ymax);
      ++ng;
    }
    if ((s==2) || (!autoscale)){
      if (debug_infolevel)
	printf("set vrange [%g:%g]\n",function_ymin,function_ymax);
      fprintf(stream,"set vrange [%g:%g]\n",function_ymin,function_ymax);
      ++ng;
    }
    if (autoscale){
      if (debug_infolevel)
	printf("%s","set autoscale\n");
      fprintf(stream,"%s","set autoscale\n");
      ++ng;
    }
    if (clrplot || gnuplot_do_splot){
      gnuplot_do_splot=false;
      if (s==1){
	if (debug_infolevel)
	  printf("%s","plot ");	
	fprintf(stream,"%s","plot ");
      }
      else {
	if (!autoscale){
	  if (debug_infolevel)
	    printf("set zrange [%g:%g]\n",function_zmin,function_zmax);
	  fprintf(stream,"set zrange [%g:%g]\n",function_zmin,function_zmax);
	  ++ng;
	}
	if (debug_infolevel)
	  printf("%s","set view\n");
	fprintf(stream,"%s","set view\n");
	++ng;
	if (debug_infolevel)
	  printf("%s","splot ");
	fprintf(stream,"%s","splot ");
      }
    }
    else {
      if (debug_infolevel)
	printf("%s","replot ");
      fprintf(stream,"%s","replot ");
    }
    if (ff.type!=_VECT){
      if (debug_infolevel)
	printf("%s\n",gnuplot_traduit(ff).c_str());
      fprintf(stream,"%s\n",gnuplot_traduit(ff).c_str());
      ++ng;
    }
    else {
      string tmp(gnuplot_traduit(ff));
      // cout << tmp.substr(1,tmp.size()-2) << endl;
      if (debug_infolevel)
	printf("%s\n",tmp.substr(1,tmp.size()-2).c_str());
      fprintf(stream,"%s\n",tmp.substr(1,tmp.size()-2).c_str());
      ++ng;
    }
    win9x_gnuplot(stream);
    gnuplot_wait(out_handle,gnuplot_out_readstream,ng+gnuplot_wait_times);
    // usleep(50000);
    r.subtype=gnuplot_fileno,
      ++gnuplot_fileno;
    return r;
    //return gnuplot_fileno-1;
#endif
    return r;
  }

  // Note gnuplot 3.7.2 seems to have a bug
  // The unit is set to 0.1bp at the beginning and scale to 0.05
  // gnuplot 3.7.1 seems to work correctly
  bool latex_replot(FILE * stream,const string & s){
    if (!has_gnuplot)
      return 0;
#ifdef WITH_GNUPLOT
    // printf("%s\n%s\"%s\"\n%s\n%s\n","set terminal pslatex","set output ",s.c_str(),"replot","set output");
    if (debug_infolevel){
      printf("%s\n","set terminal pslatex");
      printf("%s","set output ");
      printf("\"%s",s.c_str());
      printf("\"\n%s\n%s\n","replot","set output");
    }
    fprintf(stream,"%s\n","set terminal pslatex");
    fprintf(stream,"%s","set output ");
    fprintf(stream,"\"%s",s.c_str());
    fprintf(stream,"\"\n%s\n%s\n","replot","set output");
#ifdef WIN32
    if (debug_infolevel)
      printf("%s\n","set terminal windows"); 
    fprintf(stream,"%s\n","set terminal windows");
#else
#ifdef __APPLE__
    if (debug_infolevel)
      printf("set terminal aqua\n");
    fprintf(stream,"set terminal aqua\n");
#else
    if (debug_infolevel)
      printf("%s\n","set terminal x11"); 
    fprintf(stream,"%s\n","set terminal x11");
#endif
#endif
    fflush(stream);
#endif
    return true;
  }

  static gen bit_and(const gen & a,unsigned mask){
    if (a.type==_INT_)
      return int(unsigned(a.val) & mask);
    if (a.type==_VECT){
      vecteur res;
      const_iterateur it=a._VECTptr->begin(),itend=a._VECTptr->end();
      for (;it!=itend;++it)
	res.push_back(bit_and(*it,mask));
      return res;
    }
    return a;
  }

  static gen bit_ori(const gen & a,unsigned mask){
    if (a.type==_INT_)
      return int(unsigned(a.val) | mask);
    if (a.type==_VECT){
      vecteur res;
      const_iterateur it=a._VECTptr->begin(),itend=a._VECTptr->end();
      for (;it!=itend;++it)
	res.push_back(bit_ori(*it,mask));
      return res;
    }
    return a;
  }

  static gen bit_orv(const gen & a,const vecteur & v){
    if (a.type==_INT_)
      return bit_ori(v,a.val);
    if (a.type==_VECT){
      vecteur res;
      const_iterateur it=a._VECTptr->begin(),itend=a._VECTptr->end(),jt=v.begin(),jtend=v.end();
      for (;it!=itend && jt!=jtend;++it,++jt){
	if (jt->type==_INT_)
	  res.push_back(bit_ori(*it,jt->val));
	else
	  res.push_back(*it);
      }
      for (;it!=itend;++it){
	res.push_back(*it);
      }
      return res;
    }
    return a;
  }

  vecteur get_style(const vecteur & v,string & legende){
    int s=v.size();
    vecteur style(1,int(FL_BLACK));
    if (s>=3)
      legende=gen2string(v[2]);
    if (s>1){
      gen f1(v[1]);
      if ( f1.type==_VECT && !f1._VECTptr->empty() )
	f1=f1._VECTptr->front();
      int typ=f1.type;
      if (typ==_INT_ || typ==_ZINT)
	style.front()=f1;
      if ( typ==_SYMB ){
	gen & f2 =f1._SYMBptr->feuille;
	if (f2.type==_VECT)
	  style=*f2._VECTptr;
	else
	  style.front()=f2;
      }
    }
    return style;
  }

  // read color like attributs and returns the first attribut index
  int read_attributs(const vecteur & v,vecteur & attributs,GIAC_CONTEXT){
    if (attributs.empty())
      attributs.push_back(default_color(contextptr));
    const_iterateur it=v.begin(),itend=v.end();
    int s=itend-it,smax(s);
    for (;it!=itend;++it){
      if (*it==at_normalize){
	s=it-v.begin();
	attributs.push_back(*it);
	continue;
      }
      if (it->type==_VECT){
	if (read_attributs(*it->_VECTptr,attributs,contextptr)!=int(it->_VECTptr->size()))
	  s=it-v.begin();
	continue;
      }
      if (!it->is_symb_of_sommet(at_equal))
	continue;
      gen & opt=it->_SYMBptr->feuille;
      if (opt.type!=_VECT || opt._VECTptr->size()!=2)
	continue;
      gen opt1=opt._VECTptr->front(),opt2=opt._VECTptr->back().eval(1,0);
      unsigned colormask=0xffff0000;
      if (opt1==at_couleur || opt1==at_display){
	opt1=_COLOR; opt1.subtype=_INT_COLOR;
	colormask=0xffffffff;
      }
      if (opt1==at_legende){
	opt1=_LEGEND;
	opt1.subtype=_INT_COLOR;
      }
      if (opt1.type==_DOUBLE_)
	opt1=gen(int(opt1._DOUBLE_val));
      if (opt2.type==_DOUBLE_ && opt1.val!=_LEGEND)
	opt2=gen(int(opt2._DOUBLE_val));
      if ( opt1.type!=_INT_ || opt1.subtype==0)
	continue;
      if (s==smax)
	s=it-v.begin();
      switch (opt1.val){
      case _COLOR:
	if (opt2.type==_INT_)
	  attributs[0]=bit_ori(bit_and(attributs[0],0xcfff0000),opt2.val);
	if (opt2.type==_VECT)
	  attributs[0]=bit_orv(bit_and(attributs[0],0xcfff0000),*opt2._VECTptr);
	break;
      case _STYLE:
	if (opt2==at_point)
	  attributs[0]=bit_ori(bit_and(attributs[0],0xfe3fffff),_DASHDOT_LINE);
	break;
      case _THICKNESS:
	attributs[0]=bit_and(attributs[0], 0xfff8ffff);
	attributs[0]=bit_ori(attributs[0],_LINE_WIDTH_2*(bit_and(opt2,0x7).val-1));
	break;
      case _LEGEND:
	if (attributs.size()>1)
	  attributs[1]=opt2;
	else
	  attributs.push_back(opt2);
	break;
      case _GL_OPTION:
	if (attributs.size()==1)
	  attributs.push_back(string2gen("",false));
	attributs.push_back(opt2);
      case _GL_MATERIAL: case _GL_TEXTURE:
	if (attributs.size()==1)
	  attributs.push_back(string2gen("",false));
	attributs.push_back(makevecteur(opt1,opt2));
      }
    }
    return s;
  }


  static void read_option(const vecteur & v,double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,vecteur & attributs, int & nstep,int & jstep,int & kstep,bool unfactored,GIAC_CONTEXT){
    read_attributs(v,attributs,contextptr);
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type==_VECT){
	read_option(*it->_VECTptr,xmin,xmax,ymin,ymax,zmin,zmax,attributs,nstep,jstep,kstep,contextptr);
	continue;
      }
      if (it->subtype==_INT_SOLVER && *it==_UNFACTORED)
	unfactored=true;
      if (!it->is_symb_of_sommet(at_equal))
	continue;
      gen & opt=it->_SYMBptr->feuille;
      if (opt.type!=_VECT || opt._VECTptr->size()!=2)
	continue;
      gen opt1=opt._VECTptr->front(),opt2=opt._VECTptr->back();
      if ( opt1.type!=_INT_)
	continue;
      switch (opt1.val){
      case _NSTEP:
	if (opt2.type==_INT_)
	  nstep=abs(opt2,context0).val; // ok
	break;
      case _XSTEP:
	opt2=evalf_double(abs(opt2,context0),2,context0); // ok
	if (opt2.type==_DOUBLE_){
	  nstep=int((xmax-xmin)/opt2._DOUBLE_val+.5);
	  if (!jstep)
	    jstep=nstep;
	}
	break;
      case _YSTEP:
	opt2=evalf_double(abs(opt2,context0),2,context0); // ok
	if (opt2.type==_DOUBLE_)
	  jstep=int((ymax-ymin)/opt2._DOUBLE_val+.5);
	break;
      case _ZSTEP:
	opt2=evalf_double(abs(opt2,context0),2,context0); // ok
	if (opt2.type==_DOUBLE_)
	  kstep=int((zmax-zmin)/opt2._DOUBLE_val+.5);
	break;
      }
    }
  }

  void read_option(const vecteur & v,double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,vecteur & attributs, int & nstep,int & jstep,int & kstep,GIAC_CONTEXT){
    bool unfactored=false;
    read_option(v,xmin,xmax,ymin,ymax,zmin,zmax,attributs,nstep,jstep,kstep,unfactored,contextptr);
  }

  static void read_option(const vecteur & v,double xmin,double xmax,double ymin,double ymax,vecteur & attributs, int & nstep,int & jstep,GIAC_CONTEXT){
    double zmin=gnuplot_zmin,zmax=gnuplot_zmax; int kstep=0;
    bool unfactored=false;
    read_option(v,xmin,xmax,ymin,ymax,zmin,zmax,attributs,nstep,jstep,kstep,unfactored,contextptr);
  }

  gen funcplotfunc(const gen & args,bool densityplot,const context * contextptr){
    double xmin=gnuplot_xmin,xmax=gnuplot_xmax,ymin=gnuplot_ymin,ymax=gnuplot_ymax,zmin=gnuplot_zmin,zmax=gnuplot_zmax;
    bool showeq=false;
    if (densityplot)
      zmin=zmax; // if z-range is not given, then fmin/fmax will be used 
    int nstep=gnuplot_pixels_per_eval,jstep=0;
    gen attribut=default_color(contextptr);
    vecteur vargs(plotpreprocess(args,contextptr));
    if (is_undef(vargs))
      return vargs;
    int s=vargs.size();
    for (int i=0;i<s;++i){
      if (vargs[i]==at_equation){
	showeq=true;
	vargs.erase(vargs.begin()+i);
	--s;
	break;
      }
    }
    if (s<1)
      return gensizeerr(contextptr);
    gen e1=vargs[1];
    bool newsyntax;
    if (e1.type!=_VECT){
      newsyntax=readrange(e1,gnuplot_xmin,gnuplot_xmax,e1,xmin,xmax,contextptr) && (e1.is_symb_of_sommet(at_equal) || s<=4);
    }
    else {
      if (e1._VECTptr->size()!=2)
	return setplotfuncerr();
      vecteur v(*e1._VECTptr);
      newsyntax=readrange(v[0],gnuplot_xmin,gnuplot_xmax,v[0],xmin,xmax,contextptr) && readrange(v[1],gnuplot_ymin,gnuplot_ymax,v[1],ymin,ymax,contextptr);
      if (newsyntax)
	e1=v;
    }
    if (!newsyntax){ // plotfunc(fonction,var,min,max[,zminmax,attribut])
      if (s<=3)
	return setplotfuncerr();
      gen e2=vargs[2];
      gen e3=vargs[3];
      if (e1.type==_VECT){
	if ((e2.type!=_VECT) || (e3.type!=_VECT) || (e2._VECTptr->size()!=2) || (e3._VECTptr->size()!=2))
	  return gentypeerr(gettext("Plotfunc: Range must be [xmin,ymin] or [xmax,ymax]"));
	gen e21=evalf_double(e2._VECTptr->front(),eval_level(contextptr),contextptr);
	gen e22=evalf_double(e2._VECTptr->back(),eval_level(contextptr),contextptr);
	gen e31=evalf_double(e3._VECTptr->front(),eval_level(contextptr),contextptr);
	gen e32=evalf_double(e3._VECTptr->back(),eval_level(contextptr),contextptr);
	if ((e21.type!=_DOUBLE_) || (e22.type!=_DOUBLE_) || (e31.type!=_DOUBLE_) || (e32.type!=_DOUBLE_))
	  return gentypeerr(gettext("Plotfunc: bad range value!"));
	xmin=e21._DOUBLE_val;
	ymin=e22._DOUBLE_val;
	xmax=e31._DOUBLE_val;
	ymax=e32._DOUBLE_val; 
	if (s>4){
	  gen e4=vargs[4];
	  if (e4.type==_VECT && e4._VECTptr->size()==2){
	    gen e41=evalf_double(e4._VECTptr->front(),eval_level(contextptr),contextptr);
	    gen e42=evalf_double(e4._VECTptr->back(),eval_level(contextptr),contextptr);
	    if (e41.type!=_DOUBLE_ || e42.type!=_DOUBLE_)
	      return gentypeerr(gettext("Plotfunc: bad range value!"));
	    zmin=e41._DOUBLE_val;
	    zmax=e42._DOUBLE_val;
	  }
	  if (s>5)
	    attribut=vargs[5];
	  if (s>6 && vargs[6].type==_INT_)
	    nstep=vargs[6].val;
	  if (s>7 && vargs[7].type==_INT_)
	    jstep=vargs[7].val;
	}
      }
      else {
	e2=e2.evalf_double(eval_level(contextptr),contextptr);
	e3=e3.evalf_double(eval_level(contextptr),contextptr);
	if ((e2.type!=_DOUBLE_) || (e3.type!=_DOUBLE_))
	  return gentypeerr(gettext("Plotfunc: bad range value!"));
	xmin=e2._DOUBLE_val;
	xmax=e3._DOUBLE_val;
	if (s>4)
	  attribut=vargs[4];
	if (s>5 && vargs[5].type==_INT_)
	  nstep=vargs[5].val;
	if (s>6 && vargs[6].type==_INT_)
	  jstep=vargs[6].val;
      }
      return plotfunc(vargs.front(),e1,vecteur(1,attribut),densityplot,xmin,xmax,ymin,ymax,zmin,zmax,nstep,jstep,showeq,contextptr);
    }
    // plotfunc(func,x=xmin..xmax[,zminmax,attribut])
    if (e1.type==_VECT && s>2){
      double z1,z2;
      if (readrange(vargs[2],gnuplot_zmin,gnuplot_zmax,vargs[2],z1,z2,contextptr)){
	zmin=z1; zmax=z2;
      }
    }
    vecteur attributs(1,attribut);
    read_option(vargs,xmin,xmax,ymin,ymax,attributs,nstep,jstep,contextptr);
    return plotfunc(vargs[0],e1,attributs,densityplot,xmin,xmax,ymin,ymax,zmin,zmax,nstep,jstep,showeq,contextptr);
  }
  gen _plotfunc(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return funcplotfunc(args,false,contextptr);
  }
  static const char _plotfunc_s []="plotfunc";
  static define_unary_function_eval_quoted (__plotfunc,&giac::_plotfunc,_plotfunc_s);
  define_unary_function_ptr5( at_plotfunc ,alias_at_plotfunc,&__plotfunc,_QUOTE_ARGUMENTS,true);

  gen _funcplot(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return funcplotfunc(args,false,contextptr);
  }
  static const char _funcplot_s []="funcplot"; // Same as plotfunc but with tex print
  static define_unary_function_eval_quoted (__funcplot,&giac::_funcplot,_funcplot_s);
  define_unary_function_ptr5( at_funcplot ,alias_at_funcplot,&__funcplot,_QUOTE_ARGUMENTS,true);

  gen _plotdensity(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return funcplotfunc(args,true,contextptr);
  }
  static const char _plotdensity_s []="plotdensity"; 
  static define_unary_function_eval_quoted (__plotdensity,&giac::_plotdensity,_plotdensity_s);
  define_unary_function_ptr5( at_plotdensity ,alias_at_plotdensity,&__plotdensity,_QUOTE_ARGUMENTS,true);

  static const char _densityplot_s []="densityplot"; 
  static define_unary_function_eval_quoted (__densityplot,&giac::_plotdensity,_densityplot_s);
  define_unary_function_ptr5( at_densityplot ,alias_at_densityplot,&__densityplot,_QUOTE_ARGUMENTS,true);

  bool chk_double_interval(const gen & g,double & inf,double & sup,GIAC_CONTEXT){
    gen h(g);
    if (!h.is_symb_of_sommet(at_interval))
      return false;
    h=h._SYMBptr->feuille;
    if (h.type!=_VECT || h._VECTptr->size()!=2)
      return false;
    gen h1=evalf_double(h._VECTptr->front(),1,contextptr);
    gen h2=evalf_double(h._VECTptr->back(),1,contextptr);
    if (h1.type!=_DOUBLE_  || h2.type!=_DOUBLE_ )
      return false;
    inf=h1._DOUBLE_val;
    sup=h2._DOUBLE_val;
    return true;
  }

  gen readvar(const gen & g){
    if (g.type==_IDNT)
      return g;
    if (!g.is_symb_of_sommet(at_equal))
      return undef;
    gen & f=g._SYMBptr->feuille;
    if (f.type!=_VECT || f._VECTptr->size()!=2)
      return undef;
    return f._VECTptr->front();
  }

  bool readrange(const gen & g,double defaultxmin,double defaultxmax,gen & x, double & xmin, double & xmax,GIAC_CONTEXT){
    xmin=defaultxmin;
    xmax=defaultxmax;
    if (g.type==_IDNT){
      x=g;
      return true;
    }
    if (g.is_symb_of_sommet(at_equal)){
      gen & f=g._SYMBptr->feuille;
      if (f.type!=_VECT)
	return false;
      vecteur & v=*f._VECTptr;
      if (v.size()!=2 || v[0].type!=_IDNT)
	return false;
      bool res= chk_double_interval(v[1],xmin,xmax,contextptr);
      x=v[0];
      return res;
    }
    return false;
  }
  static symbolic symb_erase(const gen & aa){
    gen a(aa);
    if (a.type==_VECT) a.subtype=_SEQ__VECT;
    return symbolic(at_erase,a);
  }
  gen _erase(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
#ifdef WITH_GNUPLOT
    plot_instructions.clear();
#endif
    __interactive.op(symbolic(at_erase,0),contextptr);
    return symb_erase(args);
  }
  static const char _erase_s []="erase";
  static define_unary_function_eval2 (__erase,&giac::_erase,_erase_s,&printasconstant);
  define_unary_function_ptr( at_erase ,alias_at_erase ,&__erase);

  int erase3d(){
#ifdef WITH_GNUPLOT
    if (gnuplot_do_splot)
      return gnuplot_fileno-1;
    int out_handle;
    bool clrplot;
    if (win9x){
      FILE * stream = fopen("gnuplot.txt","w");
      fputc(' ',stream);
      fflush(stream);
      fclose(stream);
    }
    else {
      FILE * gnuplot_out_readstream,* stream = open_gnuplot(clrplot,gnuplot_out_readstream,out_handle);
      if (show_axes(0))
	fprintf(stream,"unset arrow 1\nunset arrow 2\nunset arrow 3\n");
      fprintf(stream,"\nclear\n");
      fflush(stream);
      gnuplot_wait(out_handle,gnuplot_out_readstream);
      // WARNING never close the pipe! fclose(stream);
    }
    ++gnuplot_fileno;
    gnuplot_do_splot=true;
    return gnuplot_fileno-1;
#else
    return -1;
#endif
  }

  gen _erase3d(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return erase3d();
  }
  static const char _erase3d_s []="erase3d";
  static define_unary_function_eval (__erase3d,&giac::_erase3d,_erase3d_s);
  define_unary_function_ptr5( at_erase3d ,alias_at_erase3d,&__erase3d,0,true);

  // arg=real/complex or (real/complex,label) or same+color
  static gen pointonoff(const gen & args,const vecteur & attributs,GIAC_CONTEXT){
    gen e,a(args.evalf(eval_level(contextptr),contextptr)); 
    if ( (a.is_real(contextptr)) || (a.type==_CPLX) ){
      if (attributs.size()<=1)
	e=symb_pnt(args.eval(eval_level(contextptr),contextptr),attributs,contextptr);
      else
	e=symb_pnt_name(args.eval(eval_level(contextptr),contextptr),attributs[0],attributs[1],contextptr);
    }
    else { 
      if (args.type!=_VECT)
	return symb_pnt(args,attributs,contextptr);
      int s=args._VECTptr->size();
      if ( (s!=2) && (s!=3) )
	return gensizeerr(gettext("pointon"));
      gen x=args._VECTptr->front(),y=(*args._VECTptr)[1],c;
      if ( (s==3) || (y.type==_STRNG))
	e=symbolic(at_pnt,args);
      else 
	e=symb_pnt_name(x,attributs[0].val,y,contextptr);
    }
#if !defined(WIN32) && defined(WITH_GNUPLOT)
    if (child_id) plot_instructions.push_back(e);
#endif // WIN32
    return e;
  }

  // pixel (i,j,[color])
  gen _pixon(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    int s;
    if (args.type!=_VECT || (s=args._VECTptr->size())<2)
      return gensizeerr(contextptr);
    vecteur v(*args._VECTptr);
    if (s<3)
      v.push_back(default_color(contextptr));
    return symb_pnt(symbolic(at_pixon,gen(v,_SEQ__VECT)),0,contextptr);
  }
  static const char _pixon_s []="pixon";
  static define_unary_function_eval (__pixon,&giac::_pixon,_pixon_s);
  define_unary_function_ptr5( at_pixon ,alias_at_pixon,&__pixon,0,true);

  gen _pixoff(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur v(*args._VECTptr);
    v.push_back(int(FL_WHITE));
    return _pixon(gen(v,_SEQ__VECT),contextptr);
  }
  static const char _pixoff_s []="pixoff";
  static define_unary_function_eval (__pixoff,&giac::_pixoff,_pixoff_s);
  define_unary_function_ptr5( at_pixoff ,alias_at_pixoff,&__pixoff,0,true);

  static gen _droite_segment(const gen & args,int subtype,const vecteur & attributs,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen e;
    vecteur res(*args._VECTptr);
    iterateur it=res.begin(),itend=res.end();
    for (;it!=itend;++it){
      bool ispnt=it->is_symb_of_sommet(at_pnt);
      *it=remove_at_pnt(*it);
      if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_cercle)){
	gen centre,rayon;
	if (!centre_rayon(*it,centre,rayon,false,contextptr))
	  return gensizeerr(contextptr);// we don't care about rayon
	*it=centre;
      }
      if (it->type==_VECT){
	if (it->_VECTptr->size()==2){
	  if (ispnt)
	    *it=(it->_VECTptr->front()+it->_VECTptr->back())/2;
	  else {
	    *it=it->_VECTptr->front()+cst_i*it->_VECTptr->back();
	    if (it!=res.begin())
	      *it=*it+res[0];
	  }
	}
	else {
	  if (!ispnt && it!=res.begin() && it->subtype!=_POINT__VECT)
	    *it=*it+res[0];
	  it->subtype=_POINT__VECT;
	}
      }
    }
    if (res.size()==2 && is_zero(res.front()-res.back()) && subtype!=_GROUP__VECT && subtype!=_VECTOR__VECT)
      return undef;
    e=pnt_attrib(gen(res,subtype),attributs,contextptr);
    // ofstream pict("PICT",ios::app);
    // pict << " ," << endl << e ;
    // pict.close();
#if !defined(WIN32) && defined(WITH_GNUPLOT)
    if (child_id) plot_instructions.push_back(e);
#endif // WIN32
    return e;
  }

  gen droite_by_equation(const vecteur & v,bool est_plan,GIAC_CONTEXT){
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    gen eq(remove_equal(v[0])),x(x__IDNT_e),y(y__IDNT_e),z;
    if (s>=2 && v[1].is_symb_of_sommet(at_equal)){
      if (est_plan) // Only 1 cartesian eq for a 3-d plan
	return gensizeerr(contextptr);
      if (s<3) ck_parameter_x(contextptr); else x=v[2];
      if (s<4) ck_parameter_y(contextptr); else y=v[3];
      if (s<5){
	z=z__IDNT_e;
	ck_parameter_z(contextptr);
      }
      else
	z=v[4];
      if (x.type!=_IDNT || y.type!=_IDNT || z.type!=_IDNT)
	return gensizeerr(contextptr);
      vecteur vxyz(makevecteur(x,y,z));
      gen eq1(v[1]._SYMBptr->feuille._VECTptr->front()-v[1]._SYMBptr->feuille._VECTptr->back());
      gen v0(derive(eq,vxyz,contextptr)),v1(derive(eq1,vxyz,contextptr));
      if (is_undef(v0) || is_undef(v1))
	return v0+v1;
      gen A;
      if (!is_zero(derive(v0,vxyz,contextptr)) || !is_zero(derive(v1,vxyz,contextptr)) )
	return gensizeerr(gettext("Non linear equations"));
      vecteur directeur(*normal(cross(*v0._VECTptr,*v1._VECTptr,contextptr),contextptr)._VECTptr);
      if (is_zero(directeur))
	return gensizeerr(gettext("Parallel plans"));
      if (is_zero(directeur[2])){ // z is constant on the line
	if (is_zero(directeur[1])){ // fix x=0 and solve for y and z
	  gen sol=solve(makevecteur(subst(eq,x,0,false,contextptr),subst(eq1,x,0,false,contextptr)),makevecteur(y,z),0,contextptr);
	  if (sol.type!=_VECT || sol._VECTptr->size()!=1)
	    return gensizeerr(contextptr);
	  sol=sol._VECTptr->front();
	  A=gen(makevecteur(0,sol._VECTptr->front(),sol._VECTptr->back()),_POINT__VECT);
	}
	else {
	  // fix y=0 for A, solve
	  gen sol=solve(makevecteur(subst(eq,y,0,false,contextptr),subst(eq1,y,0,false,contextptr)),makevecteur(x,z),0,contextptr);
	  if (sol.type!=_VECT || sol._VECTptr->size()!=1)
	    return gensizeerr(contextptr);
	  sol=sol._VECTptr->front();
	  A=gen(makevecteur(sol._VECTptr->front(),0,sol._VECTptr->back()),_POINT__VECT);
	}
      }
      else { // Fix z=0
	gen sol=solve(makevecteur(subst(eq,z,0,false,contextptr),subst(eq1,z,0,false,contextptr)),makevecteur(x,y),0,contextptr);
	if (sol.type!=_VECT || sol._VECTptr->size()!=1)
	  return gensizeerr(contextptr);
	sol=sol._VECTptr->front();
	A=gen(makevecteur(sol._VECTptr->front(),sol._VECTptr->back(),0),_POINT__VECT);
      }
      gen B(A+directeur);
      return pnt_attrib(gen(makevecteur(A,B),_LINE__VECT),attributs,contextptr);
    }
    if (s<2) ck_parameter_x(contextptr); else x=v[1];
    if (s<3) ck_parameter_y(contextptr); else y=v[2];
    if (est_plan){
      if (s<4){
	z=z__IDNT_e;
	ck_parameter_z(contextptr);
      }
      else
	z=v[3];
    }
    if ( x.type!=_IDNT || y.type!=_IDNT || (est_plan && z.type!=_IDNT) )
      return gensizeerr(contextptr);
    gen eqx=normal(derive(eq,*x._IDNTptr,contextptr),contextptr);
    gen eqy=normal(derive(eq,*y._IDNTptr,contextptr),contextptr);
    gen eqz;
    if (est_plan)
      eqz=normal(derive(eq,*z._IDNTptr,contextptr),contextptr);
    if (is_undef(eqx)||is_undef(eqy)||is_undef(eqz))
      return eqx+eqy+eqz;
    vecteur eqxyz=makevecteur(eqx,eqy,eqz);
    // FIXME The test should be done with all derivatives
    if (!lvarx(eqxyz,x).empty() || !lvarx(eqxyz,y).empty() || (est_plan && !lvarx(eqxyz,z).empty()) )
      return gensizeerr(contextptr);
    if (is_zero(eqx) && is_zero(eqy) && is_zero(eqz) )
      return gensizeerr(contextptr);
    // equation: eqx*x+eqy*y+eqz*z+cte=0
    gen cte=subst(eq,makevecteur(x,y,z),vecteur(3,zero),false,contextptr),A,B;
    if (est_plan){
      B=makevecteur(eqx,eqy,eqz);
      if (is_zero(eqz)){
	if (is_zero(eqy))
	  A=gen(makevecteur(-cte/eqx,0,0),_POINT__VECT);
	else
	  A=gen(makevecteur(0,-cte/eqy,0),_POINT__VECT);
      }
      else
	A=gen(makevecteur(0,0,-cte/eqz),_POINT__VECT);
      A=ratnormal(A);
      return pnt_attrib(symbolic(at_hyperplan,gen(makevecteur(B,A),_SEQ__VECT)),attributs,contextptr);
    }
    // 2-d line
    if (is_zero(eqy)){
      eqx=-eqx;
      A=rdiv(cte,eqx,contextptr);
    }
    else
      A=cst_i*rdiv(-cte,eqy,contextptr);
    A=ratnormal(A);
    B=A + eqy - eqx*cst_i;
    B=ratnormal(B);
    gen e=pnt_attrib(gen(makevecteur(A,B),_LINE__VECT),attributs,contextptr);
    return e;
  }

  gen mkrand2d3d(int dim,int nargs,gen (* f)(const gen &,const context *),GIAC_CONTEXT){
    vecteur v;
    switch (dim){
    case 2:
      for (int i=0;i<nargs;++i)
	v.push_back(rand_complex());
      break;
    case 3:
      for (int i=0;i<nargs;++i)
	v.push_back(rand_3d());
      break;
    default:
      return gendimerr(contextptr);
    }
    return f(gen(v,_SEQ__VECT),contextptr);
  }

  // v[0] should be a vect
  static gen droite_parametric(const vecteur & v,const vecteur & attributs,GIAC_CONTEXT){
    if (v.size()<2 || v.front().type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v0=*v[0]._VECTptr;
    int s=v0.size();
    const gen & t=v[1];
    gen diffv0=derive(v0,t,contextptr);
    if (is_undef(diffv0) || diffv0.type!=_VECT)
      return diffv0;
    vecteur directeur(*diffv0._VECTptr);
    if (!is_zero(derive(directeur,t,contextptr)))
      return gensizeerr(gettext("Not a line!"));
    gen A(subst(v0,t,0,false,contextptr)),d(directeur);
    if (s==2){
      A=A[0]+cst_i*A[1];
      d=d[0]+cst_i*d[1];
    }
    else
      return _droite_segment(makevecteur(A,d),_LINE__VECT,attributs,contextptr);
    return _droite_segment(makevecteur(A,A+d),_LINE__VECT,attributs,contextptr);
  }

  gen _slope(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen g=remove_at_pnt(args);
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    g=g._VECTptr->front()-g._VECTptr->back();
    if (g.type==_VECT)
      return gentypeerr(gettext("2-d instruction"));
    return normal(im(g,contextptr)/re(g,contextptr),contextptr);
  }
  static const char _slope_s []="slope";
  static define_unary_function_eval (__slope,&giac::_slope,_slope_s);
  define_unary_function_ptr5( at_slope ,alias_at_slope,&__slope,0,true);

  vecteur eval_with_xy_quoted(const gen & args0,GIAC_CONTEXT){
    vecteur v(lidnt(args0));
    int xy=0, XY=0;
    for (unsigned i=0;i<v.size();++i){
      gen & g=v[i];
      if (g.type!=_IDNT || strlen(g._IDNTptr->id_name)!=1)
	continue;
      char ch=g._IDNTptr->id_name[0];
      if (ch=='x' || ch=='y')
	++xy;
      if (ch=='X' || ch=='Y')
	++XY;
    }
    if (xy || !XY){ // priority to x/y
      gen idx(identificateur("x")),idy(identificateur("y"));
      vecteur v(makevecteur(idx,idy));
      vecteur argv(makevecteur(args0,idx,idy));
      argv=quote_eval(argv,v,contextptr);
      return argv;
    }
    if (XY){
      gen idx(identificateur("X")),idy(identificateur("Y"));
      vecteur v(makevecteur(idx,idy));
      vecteur argv(makevecteur(args0,idx,idy));
      argv=quote_eval(argv,v,contextptr);
      return argv;
    }
    return vecteur(1,eval(args0,eval_level(contextptr),contextptr));
  }

  gen _droite(const gen & args0,GIAC_CONTEXT){
    if (is_undef(args0)) return args0;
    if (args0.type==_SYMB || args0.type==_IDNT){
      // eval args with x/y or X/Y quoted
      vecteur argv=eval_with_xy_quoted(args0,contextptr);
      return droite_by_equation(argv,false,contextptr);
    }
    gen args=eval(args0,eval_level(contextptr),contextptr);
    if (args.type==_INT_)
      return mkrand2d3d(args.val,2,_droite,contextptr);
    if (args.type!=_VECT)
      return gentypeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    if (s<1)
      return gendimerr(contextptr);
    gen & v0=args._VECTptr->front();
    if ( v0.type==_IDNT || (v0.type==_SYMB && v0._SYMBptr->sommet==at_equal))
      return droite_by_equation(*args._VECTptr,false,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    gen v1=(*args._VECTptr)[1];
    if (v1.is_symb_of_sommet(at_equal) && v1._SYMBptr->feuille.type==_VECT){
      vecteur & v1v=*v1._SYMBptr->feuille._VECTptr;
      if (v1v.size()==2 && v1v[0]==at_slope){
	v1=v0+(1+cst_i*v1v[1]);
      }
    }
    if ( v0.type==_VECT && v1.type!=_VECT && !v1.is_symb_of_sommet(at_pnt))
      return droite_parametric(*args._VECTptr,attributs,contextptr);
    if (v1.type==_VECT && v0.is_symb_of_sommet(at_pnt))
      return _parallele(args,contextptr);
    return _droite_segment(gen(makevecteur(v0,v1),_SEQ__VECT),_LINE__VECT,attributs,contextptr);
  }
  static const char _droite_s []="line";
  static define_unary_function_eval_quoted (__droite,&giac::_droite,_droite_s);
  define_unary_function_ptr5( at_droite ,alias_at_droite,&__droite,_QUOTE_ARGUMENTS,true);

  gen _demi_droite(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_INT_)
      return mkrand2d3d(args.val,2,_demi_droite,contextptr);
    if (args.type!=_VECT)
      return gentypeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    vecteur v = *args._VECTptr;
    gen seg=gen(makevecteur(v[0],v[1]),_SEQ__VECT);
    if (s==3){
      v[0]=remove_at_pnt(v[0]);
      // v[1]=remove_at_pnt(v[1]);
      vecteur w;
      w.push_back(eval(symb_sto(_point(v[0],contextptr),v[2]),contextptr));
      w.push_back(_droite_segment(seg,_HALFLINE__VECT,attributs,contextptr));
      return gen(w,_GROUP__VECT);
    }
    return _droite_segment(seg,_HALFLINE__VECT,attributs,contextptr);
  }
  static const char _demi_droite_s []="half_line";
  static define_unary_function_eval (__demi_droite,&giac::_demi_droite,_demi_droite_s);
  define_unary_function_ptr5( at_demi_droite ,alias_at_demi_droite,&__demi_droite,0,true);

  gen _vector(const gen & args,GIAC_CONTEXT){
    if ( is_undef(args)) return args;
    if (args.type!=_VECT || args.subtype!=_SEQ__VECT)
      return _vector(gen(vecteur(1,args),_SEQ__VECT),contextptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    vecteur v = *args._VECTptr;
    if (!s)
      return gendimerr(contextptr);
    if (s==1)
      v=makevecteur(0*v[0],v[0]);
    v[0]=remove_at_pnt(v[0]);
    if (v[1].type!=_VECT) {
      v[1]=remove_at_pnt(v[1]);
      if (v[0].type==_VECT && v[0]._VECTptr->size()==2)
	v[0]=v[0]._VECTptr->front()+cst_i*v[0]._VECTptr->back();
      if (v[1].type==_VECT && v[1].subtype==_VECTOR__VECT && v[1]._VECTptr->size()==2){
	vecteur & w=*v[1]._VECTptr;
	v[1]=v[0]+w[1]-w[0];
      }
      if (v[1].type==_VECT && v[1]._VECTptr->size()==2)
	v[1]=v[1]._VECTptr->front()+cst_i*v[1]._VECTptr->back();
    }
    gen seg=gen(makevecteur(v[0],v[1]),_SEQ__VECT);
    return _droite_segment(seg,_VECTOR__VECT,attributs,contextptr);
  }
  static const char _vector_s []="vector";
  static define_unary_function_eval (__vector,(const gen_op_context &)&giac::_vector,_vector_s);
  define_unary_function_ptr5( at_vector ,alias_at_vector,&__vector,0,true);

  gen _segment(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_INT_)
      return mkrand2d3d(args.val,2,_segment,contextptr);
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    vecteur v = *args._VECTptr;
    gen seg=gen(makevecteur(v[0],v[1]),_SEQ__VECT);
    if (s==4){
      v[0]=remove_at_pnt(v[0]);
      v[1]=remove_at_pnt(v[1]);
      gen name;
#ifndef NO_STDEXCEPT
      try {
#endif
	name=gen(v[2].print(contextptr)+v[3].print(contextptr),contextptr);
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error & ){
	name=undef;
      }
#endif
      vecteur w;
      if (v[2].type>=_IDNT)
	w.push_back(eval(symb_sto(_point(v[0],contextptr),v[2]),contextptr));
      if (v[3].type>=_IDNT)
	w.push_back(eval(symb_sto(_point(v[1],contextptr),v[3]),contextptr));
      if (name.type!=_IDNT)
	w.push_back(_droite_segment(seg,_GROUP__VECT,attributs,contextptr));
      else
	w.push_back(eval(symb_sto(_droite_segment(seg,_GROUP__VECT,attributs,contextptr),name),contextptr));
      return gen(w,_GROUP__VECT);
      // return _droite_segment(gen(makevecteur(v[0]+cst_i*v[1],v[2]+cst_i*v[3]),_SEQ__VECT),_GROUP__VECT);
    }
    return _droite_segment(seg,_GROUP__VECT,attributs,contextptr);
  }
  static const char _segment_s []="segment";
  static define_unary_function_eval_index (28,__segment,&giac::_segment,_segment_s);
  define_unary_function_ptr5( at_segment ,alias_at_segment,&__segment,0,true);

#ifdef WITH_GNUPLOT
  void gnuplot_set_hidden3d(bool hidden){
    gnuplot_hidden3d=hidden;
  }

  void gnuplot_set_pm3d(bool b){
    gnuplot_pm3d=b;
  }

  void show_3d_axes(FILE * stream){
    if (!show_axes(0))
      return;
    if (debug_infolevel)
      printf("set arrow 1 to %g,0,0 lt 1\nset arrow 2 to 0,%g,0 lt 2\nset arrow 3 to 0,0,%g lt 3\n",gnuplot_xmax,gnuplot_ymax,gnuplot_zmax);
    fprintf(stream,"set arrow 1 to %g,0,0 lt 1\nset arrow 2 to 0,%g,0 lt 2\nset arrow 3 to 0,0,%g lt 3\n",gnuplot_xmax,gnuplot_ymax,gnuplot_zmax);
  }

  void reset_gnuplot_hidden3d(FILE * stream){
#ifndef GNUWICE
    if (gnuplot_hidden3d){
      if (debug_infolevel)
	printf("\nset hidden3d nooffset\nset isosamples 16\n");
      fprintf(stream,"\nset hidden3d nooffset\nset isosamples 16\n");
    }
    else {
      if (debug_infolevel)
	printf("\nunset hidden3d nooffset\nset isosamples 10\n"); 
      fprintf(stream,"\nunset hidden3d nooffset\nset isosamples 10\n");
    }
#endif
    if (debug_infolevel)
      printf("set parametric\n");
    fprintf(stream,"set parametric\n");
    // Problems with history inclusion, see if it's a FLTK problem
#ifndef GNUWINCE // GNUWINCE gnuplot is 3.7, no pm3d
    if (gnuplot_pm3d){
      if (debug_infolevel)
	printf("set pm3d\n");
      fprintf(stream,"set pm3d\n");
    }
    else {
      if (debug_infolevel)
	printf("unset pm3d\n");
      fprintf(stream,"unset pm3d\n");
    }
#endif // GNUWINCE
  }

#endif

  int gnuplot_show_pnt(const symbolic & e,GIAC_CONTEXT){
#ifdef WITH_GNUPLOT
    if (!show_point(contextptr))
      return -1;
    gen f=e.feuille;
    vecteur fv(lidnt(f));
    vecteur fvs(*evalf(fv)._VECTptr);
    if (!lidnt(fvs).empty())
      return -1;
    f = subst(f,fv,fvs,false,contextptr);
    if (f.type==_VECT && !f._VECTptr->empty()){
      string legende;
      if (f._VECTptr->size()>=3){
	gen & g=(*f._VECTptr)[2];
	if (g.type==_STRNG)
	  legende=g.print(contextptr);
	else
	  legende='"'+g.print(contextptr)+'"';
	legende=" title "+legende;
      }
      // Plottable: 3-d points vector of length 3 with real coord. [,,] 
      // 3-d poly-line vector of arbitrary length with 3-d point or 2-d points
      // 3-d hyperplan (symb of sommet at_hyperplan, feuille=3-d pt + normal )
      // 3-d sphere (symb of sommet at_cercle and diameter = 3-d points)
      double les3=gnuplot_xmax-gnuplot_xmin+gnuplot_ymax-gnuplot_ymin+gnuplot_zmax-gnuplot_zmin,deltat=gnuplot_tmax-gnuplot_tmin,milieut=(gnuplot_tmax+gnuplot_tmin)/2;
      const gen & p=e.feuille._VECTptr->front();
      string tmpfilename("#xcas"+print_INT_(gnuplot_fileno)+".gnu");
      bool doplot=false,clrplot=false;
      int with_point;
      string splot;
      // splot if gnuplot_do_splot && !clrplot, replot otherwise
      identificateur _u("u"),_v("v");
      if (p.type==_VECT && p.subtype==_POINT__VECT && p._VECTptr->size()==3){
	vecteur & v=*p._VECTptr;
	splot=evalf(gen(v,_SEQ__VECT),1,contextptr).print(contextptr) +" with point";
	with_point=2;
	doplot=true;
      }
      if (p.type==_VECT && p.subtype!=_POINT__VECT){
	vecteur & v=*p._VECTptr;
	if (p.subtype==_POLYEDRE__VECT){
	  // each element of v is a face with 3 or 4 vertices
	  ofstream tmpfile(tmpfilename.c_str());
	  const_iterateur it=v.begin(),itend=v.end();
	  for (;it!=itend;++it){
	    if (it->type==_VECT){ 
	      vecteur w = * it->_VECTptr;
	      int s=w.size();
	      if (s%2){
		w.insert(w.begin()+s/2,w[s/2]);
		++s;
	      }
	      if (s>=4){
		for (int j=0;j<s/2;++j){
		  if (w[j].type!=_VECT || w[j]._VECTptr->size()!=3)
		    tmpfile << "0 0 0" <<endl;
		  else {
		    const vecteur & ww=*w[j]._VECTptr;
		    tmpfile << evalf(ww[0],1,contextptr) << " " << evalf(ww[1],1,contextptr) << " " << evalf(ww[2],1,contextptr) << endl;
		  }
		}
		tmpfile << endl;
		for (int j=s-1;j>=s/2;--j){
		  if (w[j].type!=_VECT || w[j]._VECTptr->size()!=3)
		    tmpfile << "0 0 0" <<endl;
		  else {
		    const vecteur & ww=*w[j]._VECTptr;
		    tmpfile << evalf(ww[0],1,contextptr) << " " << evalf(ww[1],1,contextptr) << " " << evalf(ww[2],1,contextptr) << endl;
		  }
		}
		doplot=true;
	      }
	    }
	    tmpfile << endl << endl; // next face
	  } // end for
	  with_point=0;
	  tmpfile.close();	  
	}
	else {
	  if (v.size()==2){ 
	    // Using arrow
	    // parametric plot of the line
	    gen coeff=evalf(v.back()-v.front(),1,contextptr);
	    if (coeff.type!=_VECT)
	      return -1;
	    gen d;
	    if (p.subtype==_GROUP__VECT || p.subtype==_VECTOR__VECT)
	      d=1.0;
	    else
	      d=2*les3/abs_norm(coeff,0);
	    coeff=(d/deltat)*coeff;
	    gen tmp_eq;
	    if (p.subtype==_GROUP__VECT || p.subtype==_VECTOR__VECT)
	      tmp_eq=v.front()+coeff*(_u-gnuplot_tmin);
	    else
	      tmp_eq=v.front()+coeff*(_u-milieut);
	    // add an epsilon to see lines on plans
	    tmp_eq=tmp_eq+1e-3*makevecteur(gnuplot_xmax-gnuplot_xmin,gnuplot_ymax-gnuplot_ymin,gnuplot_zmax-gnuplot_zmin);
	    if (tmp_eq.type==_VECT){
	      tmp_eq.subtype=_SEQ__VECT;
	      splot=evalf(tmp_eq,1,contextptr).print(contextptr);
	      doplot=true;
	      with_point=2;
	    }
	  }
	  else {
	    ofstream tmpfile(tmpfilename.c_str());
	    const_iterateur it=v.begin(),itend=v.end();
	    for (;it!=itend;++it){
	      if (it->type==_VECT && it->_VECTptr->size()==3){
		const vecteur & w = * it->_VECTptr;
		tmpfile << evalf(w[0],1,contextptr) << " " << evalf(w[1],1,contextptr) << " " << evalf(w[2],1,contextptr) << endl;
		doplot=true;
	      }
	    }
	    with_point=0;
	    tmpfile.close();
	  }
	} // end else polyedre
      } // end if (p.type==_VECT)
      if (p.is_symb_of_sommet(at_hyperplan)){
	vecteur P,n;
	if (!hyperplan_normal_point(p,n,P))
	  return false;
	with_point=2;
	vecteur v1,v2;
	if (!normal3d(n,v1,v2))
	  return false;
	gen v1c=evalf(v1,1,contextptr);
	v1c=(les3/2/deltat)*v1c/abs_norm(v1c,0);
	gen v2c=evalf(v2,1,contextptr);
	v2c=(les3/2/deltat)*v2c/abs_norm(v2c,0);
	gen par_eq=P+(_u-milieut)*v1c+(_v-milieut)*v2c;
	par_eq.subtype=_SEQ__VECT;
	splot=evalf(par_eq,1,contextptr).print(contextptr);
	doplot=true;
      }
      if (p.is_symb_of_sommet(at_hypersphere)){
	// The optional 3rd argument of hypersphere is used for a "directional"
	// plot. A fourth arg might be used later for half-sphere...
	gen & f=p._SYMBptr->feuille;
	if (f.type==_VECT && f._VECTptr->size()>=2){
	  vecteur & v=*f._VECTptr;
	  if (v.front().type==_VECT && v.front()._VECTptr->size()==3){
	    with_point=2;
	    gen r=v[1];
	    if (r.type==_VECT && r._VECTptr->size()==3)
	      r=l2norm(*r._VECTptr,contextptr);
	    gen uu=2*M_PI/deltat*_u;
	    gen vv=2*M_PI/deltat*_v;
	    vecteur dir1(makevecteur(1,0,0)),dir2(makevecteur(0,1,0)),dir3(makevecteur(0,0,1));
	    if (v.size()>=3 && v[2].type==_VECT && v[2]._VECTptr->size()==3){
	      dir3=*v[2]._VECTptr;
	      dir3=divvecteur(dir3,sqrt(dotvecteur(dir3,dir3),contextptr));
	      if (!is_zero(dir3[0]) || !is_zero(dir3[1]) ){
		dir1=makevecteur(-dir3[1],dir3[0],0);
		dir1=divvecteur(dir1,sqrt(dotvecteur(dir1,dir1),contextptr));
		dir2=cross(dir3,dir1,contextptr);
	      }
	    }
	    gen par_eq=v.front()+r*(cos(uu,contextptr)*(cos(vv,contextptr)*dir1+sin(vv,contextptr)*dir2)+sin(uu,contextptr)*dir3);
	    par_eq.subtype=_SEQ__VECT;
	    splot=evalf(par_eq,1,contextptr).print(contextptr)+" notitle";
	    doplot=true;
	  }
	}
      }
      if (doplot){
	int out_handle;
	FILE * gnuplot_out_readstream,* stream = open_gnuplot(clrplot,gnuplot_out_readstream,out_handle);
	reset_gnuplot_hidden3d(stream);
	if (debug_infolevel)
	  printf("set xrange [%g:%g]\n",gnuplot_xmin,gnuplot_xmax);
	fprintf(stream,"set xrange [%g:%g]\n",gnuplot_xmin,gnuplot_xmax);
	if (debug_infolevel)
	  printf("set yrange [%g:%g]\n",gnuplot_ymin,gnuplot_ymax);
	fprintf(stream,"set yrange [%g:%g]\n",gnuplot_ymin,gnuplot_ymax);
	if (debug_infolevel)
	  printf("set zrange [%g:%g]\n",gnuplot_zmin,gnuplot_zmax);
	fprintf(stream,"set zrange [%g:%g]\n",gnuplot_zmin,gnuplot_zmax);
	if (debug_infolevel)
	  printf("set urange [%g:%g]\n",gnuplot_tmin,gnuplot_tmax);
	fprintf(stream,"set urange [%g:%g]\n",gnuplot_tmin,gnuplot_tmax);
	if (debug_infolevel)
	  printf("set vrange [%g:%g]\n",gnuplot_tmin,gnuplot_tmax);
	fprintf(stream,"set vrange [%g:%g]\n",gnuplot_tmin,gnuplot_tmax);
	gnuplot_wait(out_handle,gnuplot_out_readstream);
	gnuplot_set_hidden3d(gnuplot_hidden3d);
	if (clrplot || gnuplot_do_splot){
	  show_3d_axes(stream);
	  if (show_axes(contextptr))
	    gnuplot_wait(out_handle,gnuplot_out_readstream);
	}
	if (clrplot || gnuplot_do_splot){
	  if (debug_infolevel)
	    printf("splot");
	  fprintf(stream,"splot");
	}
	else {
	  if (debug_infolevel)
	    printf("replot");
	  fprintf(stream,"replot");
	}
	if (with_point ==2 ){
	  if (debug_infolevel)
	    printf((" "+splot+legende +'\n').c_str());
	  fprintf(stream,(" "+splot+legende +'\n').c_str());
	}
	else {
	  if (debug_infolevel)
	    printf(" \"%s\" with ",tmpfilename.c_str());
	  fprintf(stream," \"%s\" with ",tmpfilename.c_str());
	  if (with_point){
	    if (debug_infolevel)
	      printf("point\n");
	    fprintf(stream,"point\n");
	  }
	  else {
	    if (debug_infolevel)
	      printf("line\n");
	    fprintf(stream,"line\n");
	  }
	}
	gnuplot_wait(out_handle,gnuplot_out_readstream);
	gnuplot_do_splot=false;
	win9x_gnuplot(stream);
	gnuplot_wait(out_handle,gnuplot_out_readstream,gnuplot_wait_times);
	++gnuplot_fileno;
	return gnuplot_fileno-1;
      }
    }
#endif
    return -1;
  }
  gen symb_pnt_name(const gen & x,const gen & c,const gen & nom,GIAC_CONTEXT){
    symbolic e=symbolic(at_pnt,gen(makevecteur(x,c,nom),_PNT__VECT));
    gen ee(e);
    ee.subtype=gnuplot_show_pnt(e,contextptr);
    if (io_graph(contextptr))
      __interactive.op(ee,contextptr);
    return ee;
  }
  gen symb_segment(const gen & x,const gen & y,const vecteur & c,int type,GIAC_CONTEXT){
    gen e;
    if (c.empty())
      e=symbolic(at_pnt,gen(makevecteur(gen(makevecteur(x,y),type),default_color(contextptr)),_PNT__VECT));
    if (c.size()==1 || is_zero(c[1]))
      e=symbolic(at_pnt,gen(makevecteur(gen(makevecteur(x,y),type),c[0]),_PNT__VECT));
    else
      e=symbolic(at_pnt,gen(makevecteur(gen(makevecteur(x,y),type),c[0],c[1]),_PNT__VECT));
    gen ee(e);
    ee.subtype=gnuplot_show_pnt(*e._SYMBptr,contextptr);
    if (io_graph(contextptr))
      __interactive.op(ee,contextptr);
    return ee;
  }
  gen symb_pnt(const gen & x,const gen & c,GIAC_CONTEXT){
    if (is_undef(x)) return x;
    gen ee = new_ref_symbolic(symbolic(at_pnt,gen(makenewvecteur(x,c),_PNT__VECT)));
#ifdef WITH_GNUPLOT
    ee.subtype=gnuplot_show_pnt(*ee._SYMBptr,contextptr);
#else
    ee.subtype=-1;
#endif
    if (io_graph(contextptr))
      __interactive.op(ee,contextptr);
    return ee;
  }
  gen symb_pnt(const gen & x,GIAC_CONTEXT){
    return symb_pnt(x,gen(0),contextptr); // 0 instead of FL_BLACK
  }
  gen _pnt(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ((args.type==_SYMB) && (args._SYMBptr->sommet==at_pnt))
      return args;
    if ( (args.type==_VECT) && (args._VECTptr->size()) ){
      vecteur v(*args._VECTptr);
      gen e=v.front();
      if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_pnt))
	return e;
      if (v.size()==3)
	v.pop_back();
      return symbolic(at_pnt,gen(v,_PNT__VECT));
    }
    return symbolic(at_pnt,args);
  }
  static const char _pnt_s []="pnt";
  static define_unary_function_eval_index (24,__pnt,&giac::_pnt,_pnt_s);
  define_unary_function_ptr5( at_pnt ,alias_at_pnt,&__pnt,0,true);

  bool centre_rayon(const gen & cercle,gen & centre,gen & rayon,bool absrayon,GIAC_CONTEXT){
    gen c=remove_at_pnt(cercle);
    if (c.is_symb_of_sommet(at_hypersphere)){
      gen & f=c._SYMBptr->feuille;
      if (f.type!=_VECT || f._VECTptr->size()!=2)
	return false; // setsizeerr(contextptr);
      centre=f._VECTptr->front();
      rayon=f._VECTptr->back();
      return true;
    }
    if ( (c.type!=_SYMB) || (c._SYMBptr->sommet!=at_cercle))
      return false;
    gen diam=remove_at_pnt(c._SYMBptr->feuille._VECTptr->front());
    if (diam.type!=_VECT)
      return false;
    gen a=remove_at_pnt(diam._VECTptr->front());
    gen b=remove_at_pnt(diam._VECTptr->back());
    centre=normal(rdiv(a+b,plus_two,contextptr),contextptr);
    rayon=rdiv(a-b,plus_two,contextptr);
    if (absrayon)
      rayon=abs(normal(rayon,contextptr),contextptr);
    return true;
  }

  // for a point nothing, segment/line/vect->1st point
  // circle/sphere->diam
  gen get_point(const gen & g,int n,GIAC_CONTEXT){
    gen tmp=remove_at_pnt(g);
    bool sphere=tmp.is_symb_of_sommet(at_hypersphere);
    if (tmp.is_symb_of_sommet(at_cercle) || sphere){
      gen c=remove_at_pnt(tmp);
      gen f=c._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()>=3)
	f=f._VECTptr->front();
      if (f.type!=_VECT || f._VECTptr->size()!=2)
	return undef;
      gen c1=f._VECTptr->front(),c2=f._VECTptr->back();
      if (n==0 && !sphere)
	return (c1+c2)/2;
      return c1;
    }
    if (tmp.is_symb_of_sommet(at_curve))
      return gensizeerr(contextptr);
    if (tmp.is_symb_of_sommet(at_hyperplan)){
      vecteur n,P;
      if (!hyperplan_normal_point(tmp,n,P))
	return gensizeerr(contextptr);
      return gen(P,_POINT__VECT);
    }
    if (tmp.type!=_VECT)
      return tmp;
    vecteur & v =*tmp._VECTptr;
    int s=v.size();
    if (tmp.subtype==_POINT__VECT || (tmp.subtype==0 && (s==2 || s==3)) ){
      if (s==2)
	return v[0]+cst_i*v[1];
      return tmp;
    }
    if (n>=s)
      n=s-1;
    if (!s)
      return undef;
    return v[n];
  }

  gen _point(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type==_SYMB) && (args._SYMBptr->sommet==at_pnt))
      return args;
    vecteur attributs(1,default_color(contextptr) | _QUADRANT3);
    if (args.type==_VECT) {
      int s=read_attributs(*args._VECTptr,attributs,contextptr);
      vecteur v(args._VECTptr->begin(),args._VECTptr->begin()+s);
      if (s<1)
	return gendimerr(contextptr);
      if (has_i(v)){
	for (int i=0;i<s;++i){
	  v[i]=pnt_attrib(v[i],attributs,contextptr);
	}
	if (s==1)
	  return v[0];
	return gen(v,_SEQ__VECT);
      }
      if (s==1){
	gen arg1=args._VECTptr->front();
	if (arg1.type==_VECT){
	  if (arg1._VECTptr->size()==2)
	    arg1=arg1._VECTptr->front()+cst_i*arg1._VECTptr->back();
	  else {
	    arg1=gen(*arg1._VECTptr,_POINT__VECT);
	  }
	}
	return pnt_attrib(arg1,attributs,contextptr);
      }
      if (s==2){
	if (args._VECTptr->front().type==_VECT || args._VECTptr->back().type==_VECT)
	  return gensizeerr(contextptr);
	return pointonoff(args._VECTptr->front()+cst_i*(*args._VECTptr)[1],attributs,contextptr);
      }
      return pnt_attrib(gen(v,_POINT__VECT),attributs,contextptr);
    }
    return pnt_attrib(args,attributs,contextptr);
  }
  static const char _point_s []="point";
  static define_unary_function_eval_index (26,__point,&giac::_point,_point_s);
  define_unary_function_ptr5( at_point ,alias_at_point,&__point,0,true);

  static gen rand_2d3d(bool espace){
    if (espace)
      return do_point3d(rand_3d());
    else
      return rand_complex();
  }
  static gen point2d3d(bool espace,const vecteur & attributs,GIAC_CONTEXT){
    return pnt_attrib(rand_2d3d(espace),attributs,contextptr);
  }
  static gen point2d3d(const gen & args,bool espace,GIAC_CONTEXT){
    vecteur v(gen2vecteur(args));
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    v=vecteur(v.begin(),v.begin()+s);
    if (v.empty())
      return point2d3d(espace,attributs,contextptr);
    vecteur w;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      w.push_back(symbolic(at_sto,makesequence(symbolic(at_pnt,gen(makevecteur(rand_2d3d(espace),attributs[0]),_PNT__VECT)),*it)));
    }
    return eval(w,contextptr);
  }
  gen _point3d(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return point2d3d(args,true,contextptr);
  }
  static const char _point3d_s []="point3d";
  static define_unary_function_eval_quoted (__point3d,&giac::_point3d,_point3d_s);
  define_unary_function_ptr5( at_point3d ,alias_at_point3d,&__point3d,_QUOTE_ARGUMENTS,true);

  gen _point2d(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return point2d3d(args,false,contextptr);
  }
  static const char _point2d_s []="point2d";
  static define_unary_function_eval_quoted (__point2d,&giac::_point2d,_point2d_s);
  define_unary_function_ptr5( at_point2d ,alias_at_point2d,&__point2d,_QUOTE_ARGUMENTS,true);

  gen _affixe(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->size()==2 && !args._VECTptr->front().is_symb_of_sommet(at_pnt))
      return args._VECTptr->front()+cst_i*args._VECTptr->back();
    if (args.type==_VECT)
      return apply(args,_affixe,contextptr);
    gen g=remove_at_pnt(args);
    if (g.type==_VECT && g.subtype==_VECTOR__VECT && g._VECTptr->size()==2)
      return g._VECTptr->back()-g._VECTptr->front();
    return g;
  }
  static const char _affixe_s []="affix";
  static define_unary_function_eval (__affixe,&giac::_affixe,_affixe_s);
  define_unary_function_ptr5( at_affixe ,alias_at_affixe,&__affixe,0,true);

  gen _abscisse(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->size()==2 && !args._VECTptr->front().is_symb_of_sommet(at_pnt))
      return args._VECTptr->front();
    if (args.type==_VECT&& args.subtype!=_POINT__VECT)
      return apply(args,contextptr,_abscisse);
    gen g=remove_at_pnt(args);
    if (g.type==_VECT && g._VECTptr->size()>=2){
      if (g.subtype==_VECTOR__VECT)
	return _abscisse(g._VECTptr->back()-g._VECTptr->front(),contextptr);
      return (*g._VECTptr)[0];
    }
    return re(g,contextptr);
  }
  static const char _abscisse_s []="abscissa";
  static define_unary_function_eval (__abscisse,&giac::_abscisse,_abscisse_s);
  define_unary_function_ptr5( at_abscisse ,alias_at_abscisse,&__abscisse,0,true);

  gen _ordonnee(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->size()==2 && !args._VECTptr->front().is_symb_of_sommet(at_pnt))
      return args._VECTptr->back();
    if (args.type==_VECT && args.subtype!=_POINT__VECT)
      return apply(args,contextptr,_ordonnee);
    gen g=remove_at_pnt(args);
    if (g.type==_VECT && g._VECTptr->size()>=2){
      if (g.subtype==_VECTOR__VECT)
	return _ordonnee(g._VECTptr->back()-g._VECTptr->front(),contextptr);
      return (*g._VECTptr)[1];
    }
    return im(g,contextptr);
  }
  static const char _ordonnee_s []="ordinate";
  static define_unary_function_eval (__ordonnee,&giac::_ordonnee,_ordonnee_s);
  define_unary_function_ptr5( at_ordonnee ,alias_at_ordonnee,&__ordonnee,0,true);

  gen _cote(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args.subtype!=_POINT__VECT)
      return apply(args,contextptr,_cote);
    gen g=remove_at_pnt(args);
    if (g.type==_VECT && g._VECTptr->size()>=3)
      return (*g._VECTptr)[2];
    return gensizeerr(gettext("3-d instruction"));
  }
  static const char _cote_s []="cote";
  static define_unary_function_eval (__cote,&giac::_cote,_cote_s);
  define_unary_function_ptr5( at_cote ,alias_at_cote,&__cote,0,true);

  gen coordonnees(const gen & args,bool in,GIAC_CONTEXT){  
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT){
      if (args._VECTptr->empty())
	return args;
      if (args.subtype==_POINT__VECT){
	gen g=args;
	g.subtype=calc_mode(contextptr)==1?_GGB__VECT:0;
	return g;
      }
      if (args._VECTptr->front().is_symb_of_sommet(at_equal)){
	vecteur v=*args._VECTptr,res;
	vecteur last(2);
	bool lastx=false;
	for (unsigned i=0;i<v.size();++i){
	  string args0s="x";
	  if (i<v.size() && v[i].is_symb_of_sommet(at_equal)){
	    args0s=v[i]._SYMBptr->feuille[0].print(contextptr);
	    v[i]=v[i]._SYMBptr->feuille[1];
	  }
	  if (args0s[args0s.size()-1]=='x'){
	    if (lastx)
	      res.push_back(gen(last,_GGB__VECT));
	    last[0]=v[i];
	    last[1]=0;
	    lastx=true;
	  }
	  if (args0s[args0s.size()-1]=='y'){
	    last[1]=v[i];
	    res.push_back(gen(last,_GGB__VECT));
	    lastx=false;
	  }
	}
	if (lastx)
	  res.push_back(gen(last,_GGB__VECT));
	return res;
      }
      if (args._VECTptr->size()==2 && args.subtype==_VECTOR__VECT){
	gen a = args._VECTptr->front();
	gen b = args._VECTptr->back();
	// if (!a.is_symb_of_sommet(at_pnt) && !b.is_symb_of_sommet(at_pnt))
	//  return makevecteur(a,b);
	a = remove_at_pnt(a);
	b = remove_at_pnt(b);
	if ( (a.type==_VECT && b.type!=_VECT) &&
	     (b.type==_VECT && a.type!=_VECT) )
	  return gentypeerr(contextptr);
	gen c=b-a;
	if (c.type==_VECT)
	  return c;
	return gen(makevecteur(re(c,contextptr),im(c,contextptr)),calc_mode(contextptr)==1?_GGB__VECT:0);
      }
      if (calc_mode(contextptr)==1 && args.subtype!=_GGB__VECT){
	vecteur res;
	iterateur it=args._VECTptr->begin(),itend=args._VECTptr->end();
	if (in && itend-it==2 && it->type!=_VECT) 
	  return change_subtype(args,_GGB__VECT);
	for (;it!=itend;++it){
	  res.push_back(coordonnees(*it,true,contextptr));
	}
	return res; // normal list here
      }
      return apply(args,contextptr,_coordonnees);
    }
    gen P=remove_at_pnt(args);
    if (P.type==_VECT){
      if (P.subtype==_VECTOR__VECT && P._VECTptr->size()==2){
	P=P._VECTptr->back()-P._VECTptr->front();
	if (P.type==_VECT)
	  P.subtype=_POINT__VECT;
	return coordonnees(P,true,contextptr);
      }
      if (P.subtype==_POINT__VECT)
	P.subtype=calc_mode(contextptr)==1?_GGB__VECT:0;
      return P;
    }
    return gen(makevecteur(re(P,contextptr),im(P,contextptr)),calc_mode(contextptr)==1?_GGB__VECT:0);
  }
  gen _coordonnees(const gen & args,GIAC_CONTEXT){
    return coordonnees(args,false,contextptr);
  }
  static const char _coordonnees_s []="coordinates";
  static define_unary_function_eval (__coordonnees,&giac::_coordonnees,_coordonnees_s);
  define_unary_function_ptr5( at_coordonnees ,alias_at_coordonnees,&__coordonnees,0,true);

  gen _coordonnees_polaires(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen c=args.is_symb_of_sommet(at_pnt)?_coordonnees(args,contextptr):args;
    if (c.type==_VECT && c._VECTptr->size()==2){
      gen a=c._VECTptr->front();
      gen b=c._VECTptr->back();
      if (a.type==_VECT && b.type==_VECT){
	gen tmp=a-b;
	if (tmp.type!=_VECT || tmp._VECTptr->size()!=2)
	  return gensizeerr(contextptr);
	a=tmp._VECTptr->front();
	b=tmp._VECTptr->back();
      }
      c=a+cst_i*b;
    }
    if (c.type==_VECT)
      return gensizeerr(contextptr);
    gen a=abs(c,contextptr);
    gen b=arg(c,contextptr);
    return makevecteur(a,b);
  }
  static const char _coordonnees_polaires_s []="polar_coordinates";
  static define_unary_function_eval (__coordonnees_polaires,&giac::_coordonnees_polaires,_coordonnees_polaires_s);
  define_unary_function_ptr5( at_coordonnees_polaires ,alias_at_coordonnees_polaires,&__coordonnees_polaires,0,true);

  gen _coordonnees_rectangulaires(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.is_symb_of_sommet(at_pnt))
      return _coordonnees(args,contextptr);
    if (args.type!=_VECT)
      return makevecteur(re(args,contextptr),im(args,contextptr));
    if (args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen a=args._VECTptr->front();
    gen b=args._VECTptr->back();
    return makevecteur(a*cos(b,contextptr),a*sin(b,contextptr));
  }
  static const char _coordonnees_rectangulaires_s []="rectangular_coordinates";
  static define_unary_function_eval (__coordonnees_rectangulaires,&giac::_coordonnees_rectangulaires,_coordonnees_rectangulaires_s);
  define_unary_function_ptr5( at_coordonnees_rectangulaires ,alias_at_coordonnees_rectangulaires,&__coordonnees_rectangulaires,0,true);

  gen _point_polaire(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen a=args._VECTptr->front();
    gen b=args._VECTptr->back();
    return _point(a*exp(cst_i*b,contextptr),contextptr);
  }
  static const char _point_polaire_s []="polar_point";
  static define_unary_function_eval (__point_polaire,&giac::_point_polaire,_point_polaire_s);
  define_unary_function_ptr5( at_point_polaire ,alias_at_point_polaire,&__point_polaire,0,true);

  static inline gen divide_by_2(const gen & ra,GIAC_CONTEXT){
    if (ra.type==_DOUBLE_ || (ra.type==_CPLX && (ra._CPLXptr->type==_DOUBLE_ || (ra._CPLXptr+1)->type==_DOUBLE_)) )
      return ra/gen(2.0);
    else
      return normal(rdiv(ra,plus_two,contextptr),contextptr);
  }

  // point + rayon or line
  gen _cercle(const gen & args,GIAC_CONTEXT){
    if (is_undef(args)) return args;
    // inert form (since cercle return itself with a pnt__vect arg)
    if (args.type==_VECT && args.subtype==_PNT__VECT) return symbolic(at_cercle,args); 
    vecteur v(gen2vecteur(args));
    if (v.empty())
      return gensizeerr(gettext("circle"));
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    // find diametre
    if (s==1){
      vecteur w=eval_with_xy_quoted(v.front(),contextptr);
      for (unsigned i=1;i<v.size();++i)
	w.push_back(v[i]);
      gen tmp=_plotimplicit(gen(w,_SEQ__VECT),contextptr);
      if (tmp.type==_VECT && tmp._VECTptr->size()==1)
	return tmp._VECTptr->front();
      else
	return tmp;
    }
    gen e=eval(v.front(),contextptr),diametre;
    bool diam=true;
    if (e.is_symb_of_sommet(at_equal) && e._SYMBptr->feuille.type==_VECT && e._SYMBptr->feuille._VECTptr->size()==2){
      if (e._SYMBptr->feuille._VECTptr->front()==at_centre){
	diam=false;
	e=e._SYMBptr->feuille._VECTptr->back();
      }
    }
    int narg=0;
    if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_pnt)){
      gen f=e._SYMBptr->feuille._VECTptr->front();
      if (f.type==_VECT && f._VECTptr->size()==2){
	diametre=f;
	narg=1;
      }
      else e=remove_at_pnt(e);
    }
    else {
      if ((e.type==_VECT) && (e._VECTptr->size()==2)){
	diametre=e;
	narg=1;
      }
    }
    if (!narg){
      if (s<2)
	return gensizeerr(gettext("circle"));
      gen f=eval(v[1],contextptr);
      if ((f.type==_SYMB) && (f._SYMBptr->sommet==at_pnt)){
	gen g=remove_at_pnt(f);
	if (g.type==_VECT && g._VECTptr->size()==2){
	  // e=center, g=line, project e on g
	  gen g1=g._VECTptr->front();
	  gen g2=g._VECTptr->back();
	  gen t=projection(g1,g2,e,contextptr);
	  if (is_undef(t)) return t;
	  g=g1+t*(g2-g1); // this is the projection
	  diametre=gen(makevecteur(e+(e-g),g),_GROUP__VECT);
	}
	else {
	  g=get_point(g,0,contextptr);
	  if (is_undef(g)) return g;
	  if (diam)
	    diametre=gen(makevecteur(e,g),_GROUP__VECT);
	  else
	    diametre=gen(makevecteur(e+(e-g),g),_GROUP__VECT);
	}
      }
      else {
	while (f.type==_VECT){
	  if (f._VECTptr->empty())
	    return gensizeerr(contextptr);
	  f=f._VECTptr->front();
	}
	if (e.type==_VECT)
	  return gensizeerr(contextptr);
	gen ee=e-f;
	gen ff=e+f;
	diametre=gen(makevecteur(ee,ff),_GROUP__VECT);
      }
      narg=2;
    }
    if (diametre.type!=_VECT || diametre._VECTptr->size()!=2 )
      return gensizeerr(contextptr);
    gen d0=get_point(diametre._VECTptr->front(),0,contextptr);
    gen d1=get_point((*diametre._VECTptr)[1],1,contextptr);
    if (is_undef(d0)) return d0;
    if (is_undef(d1)) return d1;
    gen ce,ra;
    if (d0.type==_VECT){     
      // 3-d circle, angles not allowed yet
      if (s!=1+narg)
	return set3derr(contextptr);
      // 3rd point defines the plan
      gen d2=get_point(remove_at_pnt(eval(v[narg],contextptr)),2,contextptr);
      if (is_undef(d2)) return d2;
      gen d02=d2-d0;
      ra=divide_by_2(d1-d0,contextptr);
      d02=cross(cross(ra,d02,contextptr),ra,contextptr); // normal in the same plan
      d02=sqrt(dotvecteur(ra,ra)/dotvecteur(d02,d02),contextptr)*d02; // normalized
      // Make a parametric plot
      identificateur t(" t"),u(" u");
      ce=divide_by_2(d0+d1,contextptr);
      return plotparam3d(ce+cos(t,contextptr)*ra+sin(t,contextptr)*d02,makevecteur(t,u),gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,gnuplot_zmin,gnuplot_zmax,0,2*M_PI,0,0,false,false,attributs,M_PI/30,0,undef,makevecteur(t,u),contextptr);
      // gen theta=(2*M_PI/(gnuplot_tmax-gnuplot_tmin))*t;
      // return paramplotparam(gen(makevecteur(ce+cos(theta,contextptr)*ra+sin(theta,contextptr)*d02,t),_SEQ__VECT),false,contextptr);
    }
    // find angles
    gen a1(zero),a2(cst_two_pi);
    if (s>1+narg){
      a1=eval(v[narg],contextptr);
      a2=eval(v[narg+1],contextptr);
    }
    gen res=pnt_attrib(new_ref_symbolic(symbolic(at_cercle,gen(makenewvecteur(diametre,a1,a2),_PNT__VECT))),attributs,contextptr);
    if (s<3+narg)
      return res;
    vecteur w(1,res);
    ce=divide_by_2(d0+d1,contextptr);
    ra=divide_by_2(d1-d0,contextptr);
    gen ga1=ce+ra*exp(cst_i*a1,contextptr);
    gen ga2=ce+ra*exp(cst_i*a2,contextptr);
    if (v[narg+2].type==_IDNT)
      w.push_back(eval(symb_sto(_point(ga1,contextptr),v[narg+2]),contextptr));
    if (s>3+narg)
      w.push_back(eval(symb_sto(_point(ga2,contextptr),v[narg+3]),contextptr));
    return  gen(w,_GROUP__VECT);
  }
  static const char _cercle_s []="circle";
  static define_unary_function_eval_quoted (__cercle,&giac::_cercle,_cercle_s);
  define_unary_function_ptr5( at_cercle ,alias_at_cercle,&__cercle,_QUOTE_ARGUMENTS,true);

  gen _centre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen a=args;
    if (a.type==_VECT && a.subtype==_SEQ__VECT && a._VECTptr->size()==1)
      a=a._VECTptr->front();
    a=remove_at_pnt(a);
    gen centre,rayon;
    if (!centre_rayon(a,centre,rayon,false,contextptr))
      return gensizeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    read_attributs(gen2vecteur(args),attributs,contextptr);
    return pnt_attrib(centre,attributs,contextptr);
  }
  static const char _centre_s []="center";
  static define_unary_function_eval (__centre,&giac::_centre,_centre_s);
  define_unary_function_ptr5( at_centre ,alias_at_centre,&__centre,0,true);

  gen _rayon(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen a=remove_at_pnt(args);
    gen centre,rayon;
    if (!centre_rayon(a,centre,rayon,true,contextptr))
      return false;
    return rayon;
  }
  static const char _rayon_s []="radius";
  static define_unary_function_eval (__rayon,&giac::_rayon,_rayon_s);
  define_unary_function_ptr5( at_rayon ,alias_at_rayon,&__rayon,0,true);

  gen _milieu(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    read_attributs(gen2vecteur(args),attributs,contextptr);
    gen e,f;
    if ( (args.type==_SYMB) && (args._SYMBptr->sommet==at_pnt)){
      e=remove_at_pnt(args);
      if ((e.type!=_VECT) || (e._VECTptr->size()!=2))
	return gensizeerr(gettext("milieu"));
      f=e._VECTptr->back();
      e=e._VECTptr->front();
    }
    else {
      if ( (args.type!=_VECT) || (args._VECTptr->size()<2))
	return symbolic(at_milieu,args);
      e=remove_at_pnt(args._VECTptr->front());
      f=remove_at_pnt((*args._VECTptr)[1]);
    }
    e=get_point(e,0,contextptr);
    f=get_point(f,1,contextptr);
    return pnt_attrib(e+(f-e)/2,attributs,contextptr);
  }
  static const char _milieu_s []="midpoint";
  static define_unary_function_eval (__milieu,&giac::_milieu,_milieu_s);
  define_unary_function_ptr5( at_milieu ,alias_at_milieu,&__milieu,0,true);

  // if suppl is true return in 3-d an object of dim'=3-dim, else dim'=dim
  gen perpendiculaire(const gen & args,bool suppl,GIAC_CONTEXT){
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2))
      return gensizeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    vecteur v(*args._VECTptr);
    int s=read_attributs(v,attributs,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    gen e=v.front(),f=v[1],M;
    e=remove_at_pnt(e);
    f=remove_at_pnt(f);
    if (e.is_symb_of_sommet(at_hyperplan))
      swapgen(e,f);
    e=get_point(e,0,contextptr);
    if (is_undef(e)) return e;
    if (f.is_symb_of_sommet(at_hyperplan)){ 
      // args=point/line, hyperplan
      vecteur n,P;
      if (!hyperplan_normal_point(f,n,P))
	return gensizeerr(contextptr);
      if (e.type==_VECT &&e._VECTptr->size()==2){ 
	// line/plan -> line or plan
	M=e._VECTptr->front();
	e=e._VECTptr->back()-e._VECTptr->front();
	if (suppl){ // -> plan defined by line and n
	  return pnt_attrib(symbolic(at_hyperplan,makesequence(cross(e,n,contextptr),M)),attributs,contextptr); // _plan(makevecteur(cross(e,n,contextptr),M),contextptr);
	}
	// line in plan orthogonal to e
	gen directeur(cross(e,n,contextptr));
	// base point M+ke s.t. (M-P+ke).n=0 -> k=(P-M).n/e.n
	gen k(dotvecteur(P-M,n)/dotvecteur(e,n));
	gen base=M+k*e;
	return symb_segment(base,base+directeur,attributs,_LINE__VECT,contextptr);
      }
      // point/plan -> line
      return pnt_attrib(gen(makevecteur(e,e+n),_LINE__VECT),attributs,contextptr);
    } // end f hyperplan
    if (f.type==_VECT && f._VECTptr->size()==2){ // args=point,line
      M=f._VECTptr->front();
      f=f._VECTptr->back()-M;
    }
    else { 
      if (f.type==_VECT && f._VECTptr->size()==3){
	M=e;
      }
      else {
	// args=point,point,point
	if (s!=3)
	  return gensizeerr(gettext("3 points expected"));
	M=f;
	f=remove_at_pnt(v[2])-f;
      }
    }
    // f is a complex or a vector normal to the perpendicular
    if (f.type==_VECT) {
      // 3-d point/line ->line or plan
      if (suppl)
	return pnt_attrib(symbolic(at_hyperplan,makesequence(f,e)),attributs,contextptr); // _plan(makevecteur(f,e),contextptr);
      // -> line, e in line, second point is M+kf s.t. (M-e+kf).f=0
      gen k=dotvecteur(e-M,f)/dotvecteur(f,f);
      return symb_segment(e,M+k*f,attributs,_LINE__VECT,contextptr);
    }
    f=cst_i*f; // 2-d point/line
    return symb_segment(e,e+f,attributs,_LINE__VECT,contextptr);
  }

  gen makecomplex(const gen & a,const gen &b){
    if ( (a.type>=_CPLX && a.type!=_FLOAT_) || (b.type>=_CPLX && b.type!=_FLOAT_) )
      return a+cst_i*b;
    else
      return gen(a,b);
  }
  gen _mediatrice(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    read_attributs(gen2vecteur(args),attributs,contextptr);
    gen e,f;
    if ( (args.type==_SYMB) && (args._SYMBptr->sommet==at_pnt)){
      e=remove_at_pnt(args);
      if ((e.type!=_VECT) || (e._VECTptr->size()!=2))
	return gensizeerr(gettext("midpoint"));
      f=e._VECTptr->back();
      e=e._VECTptr->front();
    }
    else {
      if ( (args.type!=_VECT) || (args._VECTptr->size()<2))
	return symbolic(at_mediatrice,args);
      e=remove_at_pnt(args._VECTptr->front());
      f=remove_at_pnt((*args._VECTptr)[1]);
    }
    e=get_point(e,0,contextptr);
    f=get_point(f,1,contextptr);
    if (is_undef(e)) return e;
    if (is_undef(f)) return f;
    if (e.type==_VECT && f.type==_VECT)
      return perpendiculaire(makevecteur((e+f)/2,f-e),true,contextptr);
    gen ex,ey,fx,fy,efx,efy,mx,my,ax,ay,bx,by;
    reim(e,ex,ey,contextptr); reim(f,fx,fy,contextptr);
    mx=(ex+fx)/2; my=(ey+fy)/2;
    efy=ex-fx; efx=fy-ey; // perpendicular to ef
    ax=mx-efx; ay=my-efy;
    bx=mx+efx; by=my+efy;
    return symb_segment(makecomplex(ax,ay),makecomplex(bx,by),attributs,_LINE__VECT,contextptr);
    /*
    gen direction=im(f-e,contextptr)-cst_i*re(f-e,contextptr);
    gen m=rdiv(e+f,plus_two);
    return symb_segment(m-direction,m+direction,attributs,_LINE__VECT,contextptr);
    */
  }
  static const char _mediatrice_s []="perpen_bisector";
  static define_unary_function_eval (__mediatrice,&giac::_mediatrice,_mediatrice_s);
  define_unary_function_ptr5( at_mediatrice ,alias_at_mediatrice,&__mediatrice,0,true);

  gen _perpendiculaire(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return perpendiculaire(args,false,contextptr);
  }
  static const char _perpendiculaire_s []="perpendicular";
  static define_unary_function_eval (__perpendiculaire,&giac::_perpendiculaire,_perpendiculaire_s);
  define_unary_function_ptr5( at_perpendiculaire ,alias_at_perpendiculaire,&__perpendiculaire,0,true);

  gen _orthogonal(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return perpendiculaire(args,true,contextptr);
  }
  static const char _orthogonal_s []="orthogonal";
  static define_unary_function_eval (__orthogonal,&giac::_orthogonal,_orthogonal_s);
  define_unary_function_ptr5( at_orthogonal ,alias_at_orthogonal,&__orthogonal,0,true);

  bool check3dpoint(const gen & g){
    return g.type==_VECT && g._VECTptr->size()==3;
  }

  gen _parallele(const gen & args,GIAC_CONTEXT){
    if ( is_undef(args)) return args;
    vecteur attributs(1,default_color(contextptr));
    if ( args.type!=_VECT )
      return symbolic(at_parallele,args);
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    if (s<2)
      return gentypeerr(contextptr);
    gen a=remove_at_pnt(args._VECTptr->front()),b((*args._VECTptr)[1]),d;
    a=get_point(a,0,contextptr);
    if (is_undef(a)) return a;
    if (!b.is_symb_of_sommet(at_pnt)){
      // b=vector or complex -> make a line
      b=symbolic(at_pnt,gen(makevecteur(gen(makevecteur(0,b),_LINE__VECT),attributs[0]),_PNT__VECT));
    }
    if (s>2){
      b=remove_at_pnt(b);
      d=remove_at_pnt(args._VECTptr->back());
      if (a.type==_VECT && a._VECTptr->size()==3 && b.type==_VECT && b._VECTptr->size()==2 && d.type==_VECT && d._VECTptr->size()==2){
	gen n=cross(b._VECTptr->front()-b._VECTptr->back(),d._VECTptr->front()-d._VECTptr->back(),contextptr);
	return pnt_attrib(symbolic(at_hyperplan,makesequence(n,a)),attributs,contextptr); // _plan(makevecteur(n,a),contextptr);	
      }
    }
    bool ahyper=a.is_symb_of_sommet(at_hyperplan);
    if (!ahyper && b.type==_VECT){
      if (b._VECTptr->size()==2)
	b=b._VECTptr->front()+cst_i*b._VECTptr->back();
      else
	if (a.type==_VECT)
	  a.subtype=_POINT__VECT;
      d=gen(makevecteur(zero,b),_LINE__VECT);
      b=a;
    }
    else {
      b=remove_at_pnt(b);
      if (ahyper)
	swapgen(a,b);
      if (b.is_symb_of_sommet(at_hyperplan)){ // hyperplan, point
	return pnt_attrib(symbolic(at_hyperplan,makesequence(hyperplan_normal(b),a)),attributs,contextptr); // _plan(makevecteur(hyperplan_normal(b),a),contextptr);
      }
      if (b.type==_VECT && b._VECTptr->size()==2 && b.subtype!=_POINT__VECT){ // b is a line 
	if (a.type==_VECT && a._VECTptr->size()==2 && a.subtype!=_POINT__VECT){ // a is a line -> d
	  gen M=a._VECTptr->front();
	  gen n=cross(M-a._VECTptr->back(),b._VECTptr->front()-b._VECTptr->back(),contextptr);
	  return pnt_attrib(symbolic(at_hyperplan,makesequence(n,M)),attributs,contextptr); // _plan(makevecteur(n,M),contextptr);
	}
	// b is a line, a is not a line
	d=b;
	b=a;
      }
      else // a is the line ->d , b the point
	d=a;
    }
    if ( d.type!=_VECT || d._VECTptr->size()!=2 || (b.type==_VECT && b.subtype!=_POINT__VECT && b._VECTptr->size()!=3) )
      return gensizeerr(contextptr);
    // parallel to d through b
    if (d.type==_VECT)
      d.subtype=_LINE__VECT;
    return pnt_attrib(d+(b-d._VECTptr->front()),attributs,contextptr);
  }
  static const char _parallele_s []="parallel";
  static define_unary_function_eval (__parallele,&giac::_parallele,_parallele_s);
  define_unary_function_ptr5( at_parallele ,alias_at_parallele,&__parallele,0,true);

  gen _triangle(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_triangle,args);
    vecteur attributs(1,default_color(contextptr));
    vecteur v(*args._VECTptr);
    int s=read_attributs(v,attributs,contextptr);
    if (s<3)
      return gendimerr(contextptr);
    gen a=remove_at_pnt(v.front()),b=remove_at_pnt(v[1]),c=remove_at_pnt(v[2]);
    a=get_point(a,0,contextptr);
    b=get_point(b,0,contextptr);
    c=get_point(c,0,contextptr);
    v=makevecteur(a,b,c,a);
    return pnt_attrib(gen(v,_GROUP__VECT),attributs,contextptr);
  }
  static const char _triangle_s []="triangle";
  static define_unary_function_eval (__triangle,&giac::_triangle,_triangle_s);
  define_unary_function_ptr5( at_triangle ,alias_at_triangle,&__triangle,0,true);

  gen _quadrilatere(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_quadrilatere,args);
    vecteur v(*args._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<4)
      return gendimerr(contextptr);
    gen a=remove_at_pnt(v.front()),b=remove_at_pnt(v[1]),c=remove_at_pnt(v[2]),d=remove_at_pnt(v[3]);
    a=get_point(a,0,contextptr);
    b=get_point(b,0,contextptr);
    c=get_point(c,0,contextptr);
    d=get_point(d,0,contextptr);
    return pnt_attrib(gen(makevecteur(a,b,c,d,a),_GROUP__VECT),attributs,contextptr);
  }
  static const char _quadrilatere_s []="quadrilateral";
  static define_unary_function_eval (__quadrilatere,&giac::_quadrilatere,_quadrilatere_s);
  define_unary_function_ptr5( at_quadrilatere ,alias_at_quadrilatere,&__quadrilatere,0,true);

  static void get_rectangle_args(const vecteur & v,gen & e,gen & f,gen & g,gen & tmp,GIAC_CONTEXT){
    e=remove_at_pnt(eval(v[0],contextptr));
    f=remove_at_pnt(eval(v[1],contextptr));
    e=remove_at_pnt(get_point(e,0,contextptr));
    f=remove_at_pnt(get_point(f,1,contextptr));
    gen v2=eval(v[2],contextptr);
    gen d=remove_at_pnt(v2);
    if (d.type==_VECT){
      gen eg,ef=f-e;
      if (d._VECTptr->size()==2){
	eg=remove_at_pnt(d._VECTptr->front())-e;
	tmp=d._VECTptr->back()*sqrt(dotvecteur(ef,ef),contextptr);
      }
      else {
	eg=d-e;
	tmp=sqrt(dotvecteur(eg,eg),contextptr);
      }
      eg=cross(cross(ef,eg,contextptr),ef,contextptr);
      tmp=tmp/sqrt(dotvecteur(eg,eg),contextptr)*eg;
    }
    else {
      gen ef=f-e;
      if (v2.is_symb_of_sommet(at_pnt)){
	tmp=projection(e,f,d,contextptr);
	tmp=d-tmp*ef-e;
      }
      else
	tmp=ef*d*cst_i;
    }
    g=e+tmp;
  }

  gen _triangle_rectangle(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_triangle_rectangle,args);
    vecteur v(*args._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<3)
      return gendimerr(contextptr);
    gen e,f,g,tmp;
    get_rectangle_args(v,e,f,g,tmp,contextptr);
    if (is_undef(e) || is_undef(f) || is_undef(g))
      return e+f+g;
    gen res=pnt_attrib(gen(makevecteur(e,f,g,e),_GROUP__VECT),attributs,contextptr);
    if (s==3)
      return res;
    vecteur w(1,res);
    w.push_back(eval(symb_sto(_point(g,contextptr),v[3]),contextptr));
    return gen(w,_GROUP__VECT);
  }
  static const char _triangle_rectangle_s []="right_triangle";
  static define_unary_function_eval_quoted (__triangle_rectangle,&giac::_triangle_rectangle,_triangle_rectangle_s);
  define_unary_function_ptr5( at_triangle_rectangle ,alias_at_triangle_rectangle,&__triangle_rectangle,_QUOTE_ARGUMENTS,true);

  gen _rectangle(const gen & args, GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_rectangle,args);
    vecteur v(*args._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<3)
      return gendimerr(contextptr);
    gen e,f,g,tmp;
    get_rectangle_args(v,e,f,g,tmp,contextptr);
    if (is_undef(e) || is_undef(f) || is_undef(g))
      return e+f+g;
    gen h=f+tmp;
    gen res=pnt_attrib(gen(makevecteur(e,f,h,g,e),_GROUP__VECT),attributs,contextptr);
    if (s==3)
      return res;
    vecteur w(1,res);
    w.push_back(eval(symb_sto(_point(g,contextptr),v[3]),contextptr));
    if (s>4)
      w.push_back(eval(symb_sto(_point(h,contextptr),v[4]),contextptr));
    return gen(w,_GROUP__VECT);
  }
  static const char _rectangle_s []="rectangle";
  static define_unary_function_eval_quoted (__rectangle,&giac::_rectangle,_rectangle_s);
  define_unary_function_ptr5( at_rectangle ,alias_at_rectangle,&__rectangle,_QUOTE_ARGUMENTS,true);

  gen _parallelogramme(const gen & args0,GIAC_CONTEXT){
    if ( args0.type==_STRNG && args0.subtype==-1) return  args0;
    gen args(args0);
    if (args.type!=_VECT)
      args=eval(args,1,contextptr);
    if (args.type!=_VECT)
      return symbolic(at_parallelogramme,args);
    vecteur v(*args._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<3)
      return gendimerr(contextptr);
    gen e=remove_at_pnt(eval(v[0],contextptr)),f=remove_at_pnt(eval(v[1],contextptr)),d=remove_at_pnt(eval(v[2],contextptr));
    e=remove_at_pnt(get_point(e,0,contextptr));
    f=remove_at_pnt(get_point(f,0,contextptr));
    d=remove_at_pnt(get_point(d,0,contextptr));
    gen g=(e-f)+d;
    if (is_undef(g)) return g;
    gen res=pnt_attrib(gen(makevecteur(e,f,d,g,e),_GROUP__VECT),attributs,contextptr);
    if (s==3)
      return res;
    vecteur w(1,res);
    w.push_back(eval(symb_sto(_point(g,contextptr),v[3]),contextptr));
    return gen(w,_GROUP__VECT);
  }
  static const char _parallelogramme_s []="parallelogram";
  static define_unary_function_eval_quoted (__parallelogramme,&giac::_parallelogramme,_parallelogramme_s);
  define_unary_function_ptr5( at_parallelogramme ,alias_at_parallelogramme,&__parallelogramme,_QUOTE_ARGUMENTS,true);

  static void get_losange_args(const vecteur &v,gen &e,gen &f,gen & g,GIAC_CONTEXT){
    e=remove_at_pnt(eval(v[0],contextptr));
    f=remove_at_pnt(eval(v[1],contextptr));
    e=remove_at_pnt(get_point(e,0,contextptr));
    f=remove_at_pnt(get_point(f,1,contextptr));
    gen angle=remove_at_pnt(eval(v[2],contextptr));
    if (angle.type==_VECT){ // In 3-d angle is not the angle!
      gen ef=f-e;
      if (angle._VECTptr->size()==2){
	gen eg=remove_at_pnt(angle._VECTptr->front())-e;
	angle=angle._VECTptr->back(); 
	eg=cross(cross(ef,eg,contextptr),ef,contextptr);
	eg=sqrt(dotvecteur(ef,ef)/dotvecteur(eg,eg),contextptr)*eg;
	g=e+ef*cos(angle,contextptr)+eg*sin(angle,contextptr);
      }
      else {
	gen eg=angle-e;
	g=e+sqrt(dotvecteur(ef,ef)/dotvecteur(eg,eg),contextptr)*eg;
      }
    }
    else
      g=e+(f-e)*exp(cst_i*angle,contextptr);
  }

  gen _triangle_isocele(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<3))
      return symbolic(at_triangle_isocele,args);
    vecteur v(*args._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<3)
      return gendimerr(contextptr);
    gen e,f,g;
    get_losange_args(v,e,f,g,contextptr);
    if (is_undef(e) || is_undef(f) || is_undef(g))
      return e+f+g;
    gen res=pnt_attrib(gen(makevecteur(e,f,g,e),_GROUP__VECT),attributs,contextptr);
    if (s==3)
      return res;
    vecteur w(1,res);
    w.push_back(eval(symb_sto(_point(g,contextptr),v[3]),contextptr));
    return gen(w,_GROUP__VECT);
  }
  static const char _triangle_isocele_s []="isosceles_triangle";
  static define_unary_function_eval_quoted (__triangle_isocele,&giac::_triangle_isocele,_triangle_isocele_s);
  define_unary_function_ptr5( at_triangle_isocele ,alias_at_triangle_isocele,&__triangle_isocele,_QUOTE_ARGUMENTS,true);

  gen _losange(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<3))
      return symbolic(at_losange,args);
    vecteur v(*args._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<3)
      return gendimerr(contextptr);
    gen e,f,g;
    get_losange_args(v,e,f,g,contextptr);
    if (is_undef(e) || is_undef(f) || is_undef(g))
      return e+f+g;
    gen h=g;
    g=h-e+f;
    gen res=pnt_attrib(gen(makevecteur(e,f,g,h,e),_GROUP__VECT),attributs,contextptr);
    if (s==3)
      return res;
    vecteur w(1,res);
    w.push_back(eval(symb_sto(_point(g,contextptr),v[3]),contextptr));
    if (s>4)
      w.push_back(eval(symb_sto(_point(h,contextptr),v[4]),contextptr));
    return gen(w,_GROUP__VECT);
  }
  static const char _losange_s []="rhombus";
  static define_unary_function_eval_quoted (__losange,&giac::_losange,_losange_s);
  define_unary_function_ptr5( at_losange ,alias_at_losange,&__losange,_QUOTE_ARGUMENTS,true);

  gen _triangle_equilateral(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2))
      return symbolic(at_triangle_equilateral,args);
    vecteur & v=*args._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    int dim=2;
    gen e=remove_at_pnt(eval(v[0],contextptr)),f=remove_at_pnt(eval(v[1],contextptr)),g;
    e=remove_at_pnt(get_point(e,0,contextptr));
    f=remove_at_pnt(get_point(f,1,contextptr));
    if (e.type==_VECT){
      dim=3;
      if (s==2)
	return set3derr(contextptr);
      g=remove_at_pnt(eval(v[2],contextptr));
      gen ef=f-e,eg=g-e;
      eg=cross(cross(ef,eg,contextptr),ef,contextptr);
      g=e+(ef+sqrt(3*dotvecteur(ef,ef)/dotvecteur(eg,eg),contextptr)*eg)/2;
    }
    else
      g=e+(f-e)*rdiv((plus_sqrt3*cst_i+plus_one),plus_two,contextptr);
    if (is_undef(g)) return g;
    gen res=pnt_attrib(gen(makevecteur(e,f,g,e),_GROUP__VECT),attributs,contextptr);
    if (s==dim)
      return res;
    vecteur w(1,res);
    w.push_back(eval(symb_sto(_point(g,contextptr),v[dim]),contextptr));
    return gen(w,_GROUP__VECT);
  }
  static const char _triangle_equilateral_s []="equilateral_triangle";
  static define_unary_function_eval_quoted (__triangle_equilateral,&giac::_triangle_equilateral,_triangle_equilateral_s);
  define_unary_function_ptr5( at_triangle_equilateral ,alias_at_triangle_equilateral,&__triangle_equilateral,_QUOTE_ARGUMENTS,true);

  // Args= Center A, point B, number of vertices
  gen _isopolygone(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen errcode=checkanglemode(contextptr);
    if (is_undef(errcode)) return errcode;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<3))
      return symbolic(at_isopolygone,args);
    vecteur & v=*args._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    gen e=remove_at_pnt(v[0]);
    gen f=remove_at_pnt(v[1]),g;
    e=remove_at_pnt(get_point(e,0,contextptr));
    f=remove_at_pnt(get_point(f,1,contextptr));
    gen ef=f-e,nd;
    if (is_undef(ef)) return ef;
    if (s>3){
      g=remove_at_pnt(v[2]);
      gen eg=g-e;
      nd=cross(cross(ef,eg,contextptr),ef,contextptr);
      nd=sqrt(ratnormal(dotvecteur(ef,ef)/dotvecteur(nd,nd)),contextptr)*nd;
    }
    else
      nd=cst_i*ef;
    gen gn=v[s-1];
    int n=gn.val;
    if (gn.type!=_INT_ || absint(n)<2)
      return gensizeerr(contextptr);  
    if (n>0){
      context tmp;
      gen c;
      c=(e+f)/2+nd/(2*tan(cst_pi/n,&tmp));
      f=e;
      e=c;
      ef=f-e;
      if (s>3){
	gen eg=g-e;
	nd=cross(cross(ef,eg,contextptr),ef,contextptr);
	nd=sqrt(ratnormal(dotvecteur(ef,ef)/dotvecteur(nd,nd)),contextptr)*nd;
      }
      else
	nd=cst_i*ef;
    }
    else
      n=-n;
    vecteur w;
    w.push_back(f);
    for (int i=1;i<n;++i){
      w.push_back(e+ef*cos(2*i*cst_pi/n,contextptr)+nd*sin(2*i*cst_pi/n,contextptr));
    }
    w.push_back(f);
    gen res=pnt_attrib(gen(w,_GROUP__VECT),attributs,contextptr);
    return res;
  }
  static const char _isopolygone_s []="isopolygon";
  static define_unary_function_eval (__isopolygone,&giac::_isopolygone,_isopolygone_s);
  define_unary_function_ptr5( at_isopolygone ,alias_at_isopolygone,&__isopolygone,0,true);

  gen _carre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2))
      return symbolic(at_carre,args);
    vecteur & v=*args._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    gen e=remove_at_pnt(eval(v[0],contextptr));
    gen f=remove_at_pnt(eval(v[1],contextptr));
    e=remove_at_pnt(get_point(e,0,contextptr));
    f=remove_at_pnt(get_point(f,1,contextptr));
    gen ef=f-e,g;
    if (is_undef(ef)) return ef;
    int dim=2;
    if (ef.type==_VECT){
      dim=3;
      if (s==2)
	return set3derr(contextptr);
      g=remove_at_pnt(eval(v[2],contextptr));
      gen eg=g-e;
      eg=cross(cross(ef,eg,contextptr),ef,contextptr);
      g=f+sqrt(dotvecteur(ef,ef)/dotvecteur(eg,eg),contextptr)*eg;
    }
    else
      g=f+ef*cst_i;
    gen h=g-ef;
    gen res=pnt_attrib(gen(makevecteur(e,f,g,h,e),_GROUP__VECT),attributs,contextptr);
    if (s==dim)
      return res;
    vecteur w(1,res);
    w.push_back(eval(symb_sto(_point(g,contextptr),v[dim]),contextptr));
    if (s>dim+1)
      w.push_back(eval(symb_sto(_point(h,contextptr),v[dim+1]),contextptr));
    return gen(w,_GROUP__VECT);
  }
  static const char _carre_s []="square";
  static define_unary_function_eval_quoted (__carre,&giac::_carre,_carre_s);
  define_unary_function_ptr5( at_carre ,alias_at_carre,&__carre,_QUOTE_ARGUMENTS,true);

  gen _hexagone(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    gen e=remove_at_pnt(eval(v[0],contextptr));
    gen f=remove_at_pnt(eval(v[1],contextptr));
    e=remove_at_pnt(get_point(e,0,contextptr));
    f=remove_at_pnt(get_point(f,1,contextptr));
    gen ef=f-e,ecenter,g,h,i,j;
    if (is_undef(ef)) return ef;
    int dim=2;
    if (ef.type==_VECT){
      dim=3;
      if (s==2)
	return set3derr(contextptr);
      ecenter=remove_at_pnt(eval(v[2],contextptr));
      gen eg=ecenter-e;
      eg=cross(cross(ef,eg,contextptr),ef,contextptr);
      ecenter=(ef+sqrt(3*dotvecteur(ef,ef)/dotvecteur(eg,eg),contextptr)*eg)/2;
    }
    else
      ecenter=ef*rdiv((plus_sqrt3*cst_i+plus_one),plus_two,contextptr);
    g=e+ecenter+ef;
    h=e+2*ecenter;
    i=h-ef;
    j=e+ecenter-ef;
    gen res=pnt_attrib(gen(makevecteur(e,f,g,h,i,j,e),_GROUP__VECT),attributs,contextptr);
    if (s==dim)
      return res;
    vecteur w(1,res);
    w.push_back(eval(symb_sto(_point(g,contextptr),v[dim]),contextptr));
    if (s>dim+1)
      w.push_back(eval(symb_sto(_point(h,contextptr),v[dim+1]),contextptr));
    if (s>dim+2)
      w.push_back(eval(symb_sto(_point(i,contextptr),v[dim+2]),contextptr));
    if (s>dim+3)
      w.push_back(eval(symb_sto(_point(j,contextptr),v[dim+3]),contextptr));
    return gen(w,_GROUP__VECT);
  }
  static const char _hexagone_s []="hexagon";
  static define_unary_function_eval_quoted (__hexagone,&giac::_hexagone,_hexagone_s);
  define_unary_function_ptr5( at_hexagone ,alias_at_hexagone,&__hexagone,_QUOTE_ARGUMENTS,true);

  static void polygonify(vecteur & v,GIAC_CONTEXT){
    int vs=v.size();
    for (int i=0;i<vs;++i){
      v[i]=get_point(v[i],0,contextptr);
      if (v[i].type==_VECT && v[i]._VECTptr->size()==2)
	v[i]=v[i]._VECTptr->front()+cst_i*v[i]._VECTptr->back();
    }
  }

  gen _polygone(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_polygone,args);
    vecteur v(*apply(args,remove_at_pnt)._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    v=vecteur(v.begin(),v.begin()+s);
    v.push_back(v.front());
    polygonify(v,contextptr);
    return pnt_attrib(gen(v,_GROUP__VECT),attributs,contextptr);
  }
  static const char _polygone_s []="polygon";
  static define_unary_function_eval (__polygone,&giac::_polygone,_polygone_s);
  define_unary_function_ptr5( at_polygone ,alias_at_polygone,&__polygone,0,true);

  gen _polygone_ouvert(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_polygone,args);
    vecteur v(*apply(args,remove_at_pnt)._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<2)
      return gendimerr(contextptr);
    v=vecteur(v.begin(),v.begin()+s);
    polygonify(v,contextptr);
    return pnt_attrib(gen(v,_GROUP__VECT),attributs,contextptr);
  }
  static const char _polygone_ouvert_s []="open_polygon";
  static define_unary_function_eval (__polygone_ouvert,&giac::_polygone_ouvert,_polygone_ouvert_s);
  define_unary_function_ptr5( at_polygone_ouvert ,alias_at_polygone_ouvert,&__polygone_ouvert,0,true);

  static bool gen23points(const gen & args,gen &e,gen &f,gen &g,vecteur & attributs,GIAC_CONTEXT){
    if (args.type!=_VECT) return false; // setsizeerr(gettext("plot.cc/gen23points"));
    vecteur v(*args._VECTptr);
    int s=read_attributs(v,attributs,contextptr);
    if (s<2)
      return false; // setdimerr(contextptr);
    e=v.front();f=v[1];
    e=remove_at_pnt(e);
    f=remove_at_pnt(f);
    e=remove_at_pnt(get_point(e,0,contextptr));
    if (f.type==_VECT && f.subtype!=_POINT__VECT){
      if (f._VECTptr->size()!=2)
	return false; // setsizeerr(gettext("gen23points"));
      g=f._VECTptr->back();
      f=f._VECTptr->front();
    }
    else {
      if (s!=3)
	return false; // setsizeerr(gettext("gen23points"));
      g=remove_at_pnt(v[2]);
    }
    f=remove_at_pnt(get_point(f,0,contextptr));
    g=remove_at_pnt(get_point(g,0,contextptr));
    return true;
  }

  gen _hauteur(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_hauteur,args);
    gen e,f,g;
    vecteur attributs(1,default_color(contextptr));
    if (!gen23points(args,e,f,g,attributs,contextptr))
      return gensizeerr(contextptr);
    if (f.type==_VECT && f._VECTptr->size()==3){
      // projection of e on f,g
      gen h=_projection(gen(makevecteur(_droite(gen(makevecteur(f,g),_SEQ__VECT),contextptr),e),_SEQ__VECT),contextptr);
      return symb_segment(e,h,attributs,_LINE__VECT,contextptr);
    }
    f=f-g;
    f=im(f,contextptr)-cst_i*re(f,contextptr);
    return symb_segment(e,e+f,attributs,_LINE__VECT,contextptr);
  }
  static const char _hauteur_s []="altitude";
  static define_unary_function_eval (__hauteur,&giac::_hauteur,_hauteur_s);
  define_unary_function_ptr5( at_hauteur ,alias_at_hauteur,&__hauteur,0,true);

  gen bissectrice(const gen & args,bool interieur,GIAC_CONTEXT){
    gen e,f,g;
    vecteur attributs(1,default_color(contextptr));
    if (!gen23points(args,e,f,g,attributs,contextptr))
      return gensizeerr(contextptr);
    // direction (f-e)+(g-e)*||f-e||/||g-e||
    if (interieur)
      return symb_segment(e,f+(g-e)*rdiv(abs_norm(f,e,contextptr),abs_norm(g,e,contextptr),contextptr),attributs,_LINE__VECT,contextptr);
    else
      return symb_segment(e,f-(g-e)*rdiv(abs_norm(f,e,contextptr),abs_norm(g,e,contextptr),contextptr),attributs,_LINE__VECT,contextptr);
  }

  gen _bissectrice(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2))
      return symbolic(at_bissectrice,args);
    return bissectrice(args,true,contextptr);
  }
  static const char _bissectrice_s []="bisector";
  static define_unary_function_eval (__bissectrice,&giac::_bissectrice,_bissectrice_s);
  define_unary_function_ptr5( at_bissectrice ,alias_at_bissectrice,&__bissectrice,0,true);

  gen _exbissectrice(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2))
      return symbolic(at_exbissectrice,args);
    return bissectrice(args,false,contextptr);
  }
  static const char _exbissectrice_s []="exbisector";
  static define_unary_function_eval (__exbissectrice,&giac::_exbissectrice,_exbissectrice_s);
  define_unary_function_ptr5( at_exbissectrice ,alias_at_exbissectrice,&__exbissectrice,0,true);

  gen _mediane(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args.subtype!=_SEQ__VECT) return _median(args,contextptr);
    unsigned s=0;
    if ( (args.type!=_VECT) || ((s=args._VECTptr->size())<2))
      return gensizeerr(contextptr);
    if (s==2 && args._VECTptr->front().type==_VECT && args._VECTptr->front()._VECTptr->size()>3) return _median(args,contextptr);
    gen e,f,g;
    vecteur attributs(1,default_color(contextptr));
    if (!gen23points(args,e,f,g,attributs,contextptr))
      return gensizeerr(contextptr);      
    // direction (f-e)+(g-e)*||f-e||/||g-e||
    return symb_segment(e,rdiv(f+g,plus_two,contextptr),attributs,_LINE__VECT,contextptr);
  }
  static const char _mediane_s []="median_line";
  static define_unary_function_eval (__mediane,&giac::_mediane,_mediane_s);
  define_unary_function_ptr5( at_mediane ,alias_at_mediane,&__mediane,0,true);

  // chk if g is a pnt with 3 pnts as first argument
  static gen chk_3pnt(const gen & g){
    if (!g.is_symb_of_sommet(at_pnt))
      return zero;
    gen & f=g._SYMBptr->feuille;
    if (f.type!=_VECT || f._VECTptr->empty())
      return zero;
    gen & g0=f._VECTptr->front();
    if (g0.type==_VECT && g0._VECTptr->size()>2)
      return vecteur(g0._VECTptr->begin(),g0._VECTptr->begin()+3);
    else
      return zero;
  }

  // True if a=coeff*b
  bool est_parallele_vecteur(const vecteur & a,const vecteur &b,gen & coeff,GIAC_CONTEXT){
    unsigned int s=a.size(),i,j;
    if (s!=b.size())
      return false; // setsizeerr(contextptr);
    for (i=0;i<s;++i){
      if (!is_zero(b[i]))
	break;
    }
    if (i==s){ // b==0
      coeff=unsigned_inf;
      return true;
    }
    coeff=a[i]/b[i];
    j=i;
    for (i=0;i<s;++i){
      if (i==j)
	continue;
      if (!is_zero(simplify(a[i]*b[j]-a[j]*b[i],contextptr)))
	return false;
    }
    return true;
  }
  bool est_parallele_vecteur(const vecteur & a,const vecteur &b,GIAC_CONTEXT){
    gen coeff;
    return est_parallele_vecteur(a,b,coeff,contextptr);
  }
  bool est_parallele(const gen & a,const gen & b,GIAC_CONTEXT){
    if (a.type==_VECT && b.type==_VECT){
      gen coeff;
      return est_parallele_vecteur(*a._VECTptr,*b._VECTptr,coeff,contextptr);
    }
    gen d(im(a*conj(b,contextptr),contextptr));
    return is_zero(simplify(d,contextptr));
  }
  int est_aligne(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT){
    return est_parallele(b-a,c-a,contextptr);
    // gen d(im((b-a)*conj(c-a,contextptr),contextptr));
    // return is_zero(d);
  }

  static vecteur inter2droites2(const gen & a1,const gen & a2,const gen & b1,const gen & b2,int asub,int bsub,GIAC_CONTEXT){
    // 2-d line intersection
    gen v(a2-a1),w(b2-b1);
    gen x=(b1-a1).re(contextptr),y=(b1-a1).im(contextptr),vx=v.re(contextptr),vy=v.im(contextptr);
    gen wx=w.re(contextptr),wy=w.im(contextptr);
    gen alpha=rdiv(wy*x-wx*y,wy*vx-wx*vy,contextptr);
    return remove_not_in_segment(b1,b2,bsub,remove_not_in_segment(a1,a2,asub,makevecteur(symb_pnt(normal(a1+alpha*v,contextptr),default_color(contextptr),contextptr)),contextptr),contextptr);
  }

  static vecteur inter2droites3(const gen & a1,const gen & a2,const gen & b1,const gen & b2,int asub,int bsub,GIAC_CONTEXT){
    // 3-d line intersection
    gen M,N;
    vecteur n;
    if (perpendiculaire_commune(makevecteur(a1,a2),makevecteur(b1,b2),M,N,n,contextptr)){
      if (is_zero(M-N))
	return remove_not_in_segment(b1,b2,bsub,remove_not_in_segment(a1,a2,asub,makevecteur(symb_pnt(M,default_color(contextptr),contextptr)),contextptr),contextptr);
    }
    return vecteur(0);
  }

  static gen inscrit_circonscrit(const gen & arg_orig,vecteur & attributs,GIAC_CONTEXT){
    gen args(arg_orig);
    if (args.type==_VECT){
      int s=read_attributs(*args._VECTptr,attributs,contextptr);
      if (s==1)
	args=args._VECTptr->front();
      else
	args=vecteur(args._VECTptr->begin(),args._VECTptr->begin()+s);
    }
    if ( (args.type!=_VECT) || (args._VECTptr->size()!=3)){
      gen tmp=chk_3pnt(args);
      if (is_zero(tmp))
	return gensizeerr(contextptr);
      else
	return tmp;
    }
    return args;
  }

  gen _circonscrit(const gen & arg_orig,GIAC_CONTEXT){
    if ( arg_orig.type==_STRNG && arg_orig.subtype==-1) return  arg_orig;
    vecteur attributs(1,default_color(contextptr));
    gen args(inscrit_circonscrit(arg_orig,attributs,contextptr));
    if (is_undef(args) || args.type!=_VECT || args._VECTptr->size()<3)
      return args;
    vecteur v(*args._VECTptr);
    gen e,f,g,h;
    e=remove_at_pnt(v[0]);
    f=remove_at_pnt(v[1]);
    g=remove_at_pnt(v[2]);
    e=remove_at_pnt(get_point(e,0,contextptr));
    f=remove_at_pnt(get_point(f,0,contextptr));
    g=remove_at_pnt(get_point(g,0,contextptr));
    if (est_aligne(e,f,g,contextptr) || is_undef(e) || is_undef(f) || is_undef(g))
      return undef;
    if (e.type==_VECT || f.type==_VECT || g.type==_VECT)
      return set3derr(contextptr);
    gen ef=(e+f)/2; gen fg=(f+g)/2;
    vecteur w(inter2droites2(ef,ef+cst_i*(f-e),fg,fg+cst_i*(g-f),_LINE__VECT,_LINE__VECT,contextptr)); 
    if (w.empty())
      return gensizeerr(contextptr);
    h=remove_at_pnt(w.front());
    return pnt_attrib(symbolic(at_cercle,gen(makevecteur(makevecteur(e,2*h-e),0,2*cst_pi),_PNT__VECT)),attributs,contextptr); 
    // was return _cercle(makevecteur(h,e-remove_at_pnt(h)),contextptr);
  }
  static const char _circonscrit_s []="circumcircle";
  static define_unary_function_eval (__circonscrit,&giac::_circonscrit,_circonscrit_s);
  define_unary_function_ptr5( at_circonscrit ,alias_at_circonscrit,&__circonscrit,0,true);

  gen _orthocentre(const gen & arg_orig,GIAC_CONTEXT){
    if ( arg_orig.type==_STRNG && arg_orig.subtype==-1) return  arg_orig;
    vecteur attributs(1,default_color(contextptr));
    gen args(inscrit_circonscrit(arg_orig,attributs,contextptr));
    if (is_undef(args) || args.type!=_VECT || args._VECTptr->size()<3)
      return args;
    vecteur v(*args._VECTptr);
    gen e,f,g,h;
    e=remove_at_pnt(v[0]);
    f=remove_at_pnt(v[1]);
    g=remove_at_pnt(v[2]);
    e=remove_at_pnt(get_point(e,0,contextptr));
    f=remove_at_pnt(get_point(f,0,contextptr));
    g=remove_at_pnt(get_point(g,0,contextptr));
    if (est_aligne(e,f,g,contextptr) || is_undef(e) || is_undef(f) || is_undef(g))
      return undef;
    if (e.type==_VECT || f.type==_VECT || g.type==_VECT)
      return set3derr(contextptr);
    gen e1=e+cst_i*(g-f);
    gen f1=f+cst_i*(e-g);
    vecteur w(inter2droites2(e,e1,f,f1,_LINE__VECT,_LINE__VECT,contextptr)); 
    if (w.empty())
      return gensizeerr(contextptr);
    h=remove_at_pnt(w.front());
    return pnt_attrib(h,attributs,contextptr); 
    // was return _cercle(makevecteur(h,e-remove_at_pnt(h)),contextptr);
  }
  static const char _orthocentre_s []="orthocenter";
  static define_unary_function_eval (__orthocentre,&giac::_orthocentre,_orthocentre_s);
  define_unary_function_ptr5( at_orthocentre ,alias_at_orthocentre,&__orthocentre,0,true);

  // given 2 points e and f return equation of line e,f as coeffs a,b,c
  bool point2abc(const gen & e,const gen & f,gen & a,gen & b,gen & c,GIAC_CONTEXT){
    gen tmp=f-e;
    if (tmp.type==_VECT)
      return false;
    a=im(tmp,contextptr);
    b=-re(tmp,contextptr);
    c=-a*re(e,contextptr)-b*im(e,contextptr);
    vecteur v(makevecteur(a,b,c));
    lcmdeno(v,c,contextptr);
    a=v[0];
    b=v[1];
    c=v[2];
    return true;
  }

  static gen inscrit(const gen & args,const vecteur & attributs,bool exinscrit,GIAC_CONTEXT){
    vecteur v(*args._VECTptr),w;
    // /* new code using barycentre A,a B,c C,c 
    gen A,B,C;
    A=remove_at_pnt(v[0]);
    B=remove_at_pnt(v[1]);
    C=remove_at_pnt(v[2]);
    A=remove_at_pnt(get_point(A,0,contextptr));
    B=remove_at_pnt(get_point(B,0,contextptr));
    C=remove_at_pnt(get_point(C,0,contextptr));
    if (est_aligne(A,B,C,contextptr) || is_undef(A) || is_undef(B) || is_undef(B))
      return undef;
    gen a2=distance2pp(B,C,contextptr),b2=distance2pp(C,A,contextptr),c2=distance2pp(A,B,contextptr);
    gen a=exinscrit?-sqrt(a2,contextptr):sqrt(a2,contextptr);
    gen b=sqrt(b2,contextptr);
    gen c=sqrt(c2,contextptr);
    gen I=normal((a*A+b*B+c*C)/(a+b+c),contextptr);
    gen AB(B-A), AC(C-A);
    gen surface=re(AB,contextptr)*im(AC,contextptr)-im(AB,contextptr)*re(AC,contextptr);
    gen r=normal(surface/(a+b+c),contextptr);
    return pnt_attrib(symbolic(at_cercle,gen(makevecteur(makevecteur(I-r,I+r),0,2*cst_pi),_PNT__VECT)),attributs,contextptr); 
    // was return _cercle(makevecteur(I,r),contextptr);       
    // end new code */
    // old code 
    /*
      gen e,f,g;
      e=remove_at_pnt(v[0]);
      f=remove_at_pnt(v[1]);
      g=remove_at_pnt(v[2]);
      // find equations of droite(e,f) droite(e,g) droite(f,g)
      gen a_ef,b_ef,c_ef,a_fg,b_fg,c_fg,a_ge,b_ge,c_ge;
      point2abc(e,f,a_ef,b_ef,c_ef,contextptr);
      point2abc(f,g,a_fg,b_fg,c_fg,contextptr);
      point2abc(e,g,a_ge,b_ge,c_ge,contextptr);
      // value of equations droite(e,f) at g, etc.
      gen efg,fge,gef;
      efg=a_ef*re(g,contextptr)+b_ef*im(g,contextptr)+c_ef;
      fge=a_fg*re(e,contextptr)+b_fg*im(e,contextptr)+c_fg;
      gef=a_ge*re(f,contextptr)+b_ge*im(f,contextptr)+c_ge;
      gen eq_ef,eq_fg,eq_ge;
      gen sqrtg(sign(efg,contextptr)*sqrt(a_ef*a_ef+b_ef*b_ef)),sqrte(sign(fge,contextptr)*sqrt(a_fg*a_fg+b_fg*b_fg)),sqrtf(sign(gef,contextptr)*sqrt(a_ge*a_ge+b_ge*b_ge));
      vecteur coeff_eq_ef(makevecteur(a_ef/sqrtg,b_ef/sqrtg,c_ef/sqrtg));
      vecteur coeff_eq_fg(makevecteur(a_fg/sqrte,b_fg/sqrte,c_fg/sqrte));
      vecteur coeff_eq_ge(makevecteur(a_ge/sqrtf,b_ge/sqrtf,c_ge/sqrtf));
      matrice m(makevecteur(coeff_eq_fg,coeff_eq_ge,coeff_eq_ef));
      vecteur lv(alg_lvar(m));
      m=*e2r(m,lv)._VECTptr;
      // system to solve is A*x+B*y+C=0 D*x+E*y+F=0
      gen A,B,C,D,E,F;
      if (exinscrit){
      A=m[0][0]+m[1][0];
      B=m[0][1]+m[1][1];
      C=m[0][2]+m[1][2];
      }
      else {
      A=m[0][0]-m[1][0];
      B=m[0][1]-m[1][1];
      C=m[0][2]-m[1][2];
      }
      D=m[1][0]-m[2][0];
      E=m[1][1]-m[2][1];
      F=m[1][2]-m[2][2];
      // Solution is :
      gen x=(B*F-E*C)/(-D*B+A*E),y=(A*F-D*C)/(-A*E+B*D);
      // Compute r
      gen r=r2sym(dotvecteur(*m[0]._VECTptr,makevecteur(x,y,1)),lv);
      gen xy=r2sym(x+cst_i*y,lv);
      return _cercle(makevecteur(xy,r),contextptr);
      // end old code     */
    /*
      if (exinscrit)
      w=inter(bissectrice(makevecteur(e,f,g),false),bissectrice(makevecteur(f,g,e)));
      else
      w=inter(bissectrice(makevecteur(e,f,g)),bissectrice(makevecteur(f,g,e)));
      if (w.empty())
      return gensizeerr(contextptr);
      h=w.front();
      ef=_droite(makevecteur(e,f));
      w= inter(ef,_perpendiculaire(makevecteur(h,ef))); // point on circle
      if (w.empty())
      return gensizeerr(contextptr);
      d=w.front();
      return _cercle(makevecteur(h,d-h));
    */
  }

  gen _inscrit(const gen & arg_orig,GIAC_CONTEXT){
    if ( arg_orig.type==_STRNG && arg_orig.subtype==-1) return  arg_orig;
    vecteur attributs(1,default_color(contextptr));
    gen args=inscrit_circonscrit(arg_orig,attributs,contextptr);
    if (is_undef(args) || args.type!=_VECT || args._VECTptr->size()<3)
      return args;
    return inscrit(args,attributs,false,contextptr);
  }
  static const char _inscrit_s []="incircle";
  static define_unary_function_eval (__inscrit,&giac::_inscrit,_inscrit_s);
  define_unary_function_ptr5( at_inscrit ,alias_at_inscrit,&__inscrit,0,true);

  gen _exinscrit(const gen & arg_orig,GIAC_CONTEXT){
    if ( arg_orig.type==_STRNG && arg_orig.subtype==-1) return  arg_orig;
    vecteur attributs(1,default_color(contextptr));
    gen args=inscrit_circonscrit(arg_orig,attributs,contextptr);
    if (is_undef(args) || args.type!=_VECT || args._VECTptr->size()<3)
      return args;
    return inscrit(args,attributs,true,contextptr);
  }
  static const char _exinscrit_s []="excircle";
  static define_unary_function_eval (__exinscrit,&giac::_exinscrit,_exinscrit_s);
  define_unary_function_ptr5( at_exinscrit ,alias_at_exinscrit,&__exinscrit,0,true);

  gen _isobarycentre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT){
      if (args.is_symb_of_sommet(at_pnt)){
	gen & f=args._SYMBptr->feuille;
	if (f.type!=_VECT)
	  return args;
	vecteur & v=*f._VECTptr;
	int s=v.size();
	if (!s)
	  return args;
	gen &g=v[0];
	if (g.type!=_VECT || (s=g._VECTptr->size())<2)
	  return args;
	gen sum;
	const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
	--itend;
	--s;
	for (;it!=itend;++it)
	  sum=sum+*it;
	return _point(sum/s,contextptr);
      }
      return _point(args,contextptr);
    }
    vecteur attributs(1,default_color(contextptr));
    const_iterateur it=args._VECTptr->begin(); // ,itend=args._VECTptr->end();
    int i=0;
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    gen sum;
    for (;i<s;++it,++i){
      sum = sum+remove_at_pnt(*it);
    }
    if (sum.type==_VECT)
      sum.subtype=_POINT__VECT;
    return pnt_attrib(rdiv(sum,i,contextptr),attributs,contextptr);
  }
  static const char _isobarycentre_s []="isobarycenter";
  static define_unary_function_eval (__isobarycentre,&giac::_isobarycentre,_isobarycentre_s);
  define_unary_function_ptr5( at_isobarycentre ,alias_at_isobarycentre,&__isobarycentre,0,true);

  static gen inbarycentre(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT) return gensizeerr(contextptr);
    const_iterateur it=args._VECTptr->begin(); // ,itend=args._VECTptr->end();
    int i=0;
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    gen sum,n;
    for (;i<s;++it,++i){
      if (it->type!=_VECT){
	sum = sum+remove_at_pnt(*it);
	n = n + 1;
      }
      else {
	vecteur & v = *it->_VECTptr;
	if (v.size()!=2)
	  return gensizeerr(contextptr);
	sum = sum + v.back()*remove_at_pnt(v.front());
	n = n + v.back();
      }
    }
    if (is_zero(n))
      return gensizeerr(gettext("Sum of coeff is 0"));
    sum=rdiv(sum,n,contextptr);
    if (sum.type==_VECT)
      sum.subtype=_POINT__VECT;
    return pnt_attrib(sum,attributs,contextptr);
  }
  gen _barycentre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->empty())
      return gensizeerr(contextptr);
    const_iterateur it=args._VECTptr->begin(),itend=args._VECTptr->end();
    if (itend-it==2 && ckmatrix(args)){
      vecteur & v=*(it+1)->_VECTptr;
      if (!v.front().is_symb_of_sommet(at_pnt))
	return inbarycentre(_tran(args,contextptr),contextptr);
    }
    return inbarycentre(args,contextptr);
  }

  static const char _barycentre_s []="barycenter";
  static define_unary_function_eval (__barycentre,&giac::_barycentre,_barycentre_s);
  define_unary_function_ptr5( at_barycentre ,alias_at_barycentre,&__barycentre,0,true);

  gen scalar_product(const gen & a0,const gen & b0,GIAC_CONTEXT){
    gen a=remove_at_pnt(a0);
    gen b=remove_at_pnt(b0);
    if (a.type==_VECT && b.type==_VECT)
      return dotvecteur(a,b);
    gen ax,ay; reim(a,ax,ay,contextptr);
    gen bx,by; reim(b,bx,by,contextptr);
    return ax*bx+ay*by;
    // gen res=re(a,contextptr)*re(b,contextptr)+im(a,contextptr)*im(b,contextptr);
    // return res;
  }
#if 0
  static gen normalized_scalar_product(const gen & a,const gen & b,GIAC_CONTEXT){
    if (a.type==_VECT && b.type==_VECT)
      return dotvecteur(*a._VECTptr,*b._VECTptr)/sqrt(dotvecteur(*a._VECTptr,*a._VECTptr)*dotvecteur(*b._VECTptr,*b._VECTptr),contextptr);
    gen res1=re(a,contextptr)*re(b,contextptr)+im(a,contextptr)*im(b,contextptr);
    gen res2=sqrt(a.squarenorm(contextptr)*b.squarenorm(contextptr),contextptr);
    return rdiv(res1,res2,contextptr);
  }
#endif
  // return t such that tb+(1-t)a is the projection of c on [a,b] 
  gen projection(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT){
    gen ax,ay,bx,by,cx,cy,abx,aby;
    reim(a,ax,ay,contextptr);
    reim(b,bx,by,contextptr);
    reim(c,cx,cy,contextptr);
    abx=ax-bx;aby=ay-by;
    gen num=(ax-cx)*abx+(ay-cy)*aby;
    gen den=abx*abx+aby*aby;
    // gen num=scalar_product(a-c,a-b,contextptr),den=scalar_product(b-a,b-a,contextptr);
    return rdiv(num,den,contextptr);
  }

  void rewrite_with_t_real(gen & eq,const gen & t,GIAC_CONTEXT){
    gen tx,ty; reim(t,tx,ty,contextptr);
    if (!is_zero(ty)){
      eq=subst(eq,ty,zero,false,contextptr);
      eq=subst(eq,tx,t,false,contextptr);
    }
  }

  // test if a point f is on a parametric curve e
  // compute t if true
  bool on(const gen & e_orig,const gen & f,gen & t,GIAC_CONTEXT){
    gen e;
    if ( (e_orig.type==_SYMB) && (e_orig._SYMBptr->sommet==at_curve))
      e=e_orig._SYMBptr->feuille._VECTptr->front();
    else
      e=e_orig;
    if ((e.type!=_VECT) || (e._VECTptr->size()<4) )
      return false; // settypeerr(gettext("on"));
    vecteur ee(*e._VECTptr);
    gen tt=ee[1];
    gen tmin=ee[2],tmax=ee[3];
    gen eq=re(ee[0],contextptr)-re(f,contextptr);
    rewrite_with_t_real(eq,tt,contextptr); 
    vecteur v(solve(eq,tt,0,contextptr));
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if ( is_zero(normal(subst(im(ee[0],contextptr),tt,*it,false,contextptr)-im(f,contextptr),contextptr)) && 
	   ck_is_greater(*it,tmin,contextptr) && ck_is_greater(tmax,*it,contextptr) ){
	t=*it;
	return true;
      }
    }
    return false;
  }
  // projection of a (true) point pp on a parametric curve
  // e=symb_cercle or line, returns t
  gen projection(const gen & e,const gen & pp,GIAC_CONTEXT){
    gen p=remove_at_pnt(pp);
    if (p.type==_SYMB){
      if ( (p._SYMBptr->sommet==at_cercle) || (p._SYMBptr->sommet==at_curve))
	return gensizeerr(contextptr);
    }
    if (p.type==_VECT)
      return gensizeerr(contextptr);
    gen sur=remove_at_pnt(e);
    if (e.type==_VECT && !e._VECTptr->empty())
      sur=remove_at_pnt(e._VECTptr->front());
    if ( (sur.type==_VECT) && (sur._VECTptr->size()>=2)){
      int s=sur._VECTptr->size();
      gen res,proj,dist;
      for (int i=0;i<s-1;++i){
	gen a=(*sur._VECTptr)[i];
	gen b=(*sur._VECTptr)[i+1];
	gen c=projection(a,b,p,contextptr);
	if (s>2){
	  if (is_positive(-c,contextptr))
	    c=0;
	  if (is_positive(c-1,contextptr))
	    c=1;
	}
	gen cur_proj=a+c*(b-a),cur_dist;
	if (!is_undef(c) && (i==0 || is_strictly_greater(dist,(cur_dist=distance2pp(p,cur_proj,contextptr)),contextptr))){
	  res=i?makevecteur(i,c):c;
	  proj=cur_proj;
	  dist=i?cur_dist:distance2pp(p,cur_proj,contextptr);
	}
      }
      return res;
    }
    if ( (sur.type==_SYMB) && (sur._SYMBptr->sommet==at_cercle) ){
      gen centre,rayon;
      if (!centre_rayon(sur,centre,rayon,false,contextptr))
	return gensizeerr(contextptr); // don't care about radius
      bool b=angle_radian(contextptr);
      angle_radian(true,contextptr);
      gen res=arg(p-centre,contextptr); 
      angle_radian(b,contextptr);
      return res;
    }
    // if p is on e return 0
    if ( (sur.type==_SYMB) && (sur._SYMBptr->sommet==at_curve) ){
      sur = sur._SYMBptr->feuille._VECTptr->front();
      gen t;
      // if (on(sur,p,t))
      // return zero;
      // find all orthogonalities tangeant/rayon 
      vecteur v(*sur._VECTptr);
      identificateur tt=*v[1]._IDNTptr;
      gen tangeant(derive(v[0],tt,contextptr));
      if (is_undef(tangeant))
	return tangeant;
      gen t_found=v[2];
#ifndef NO_STDEXCEPT
      try {
#endif
	gen eq=scalar_product(tangeant,p-v[0],contextptr);
	if (is_undef(eq)) return eq;
	// should expand and assume that tt is real
	rewrite_with_t_real(eq,v[1],contextptr);
	vecteur sol;
	if (has_num_coeff(eq)){
	  gen rep=re(p,contextptr);
	  // first try bisection near re(p) if re(v[0])==v[1]
	  if (re(v[0],contextptr)==v[1]){
	    gen dx=(gnuplot_xmax-gnuplot_xmin)/2; // or maybe v[3]-v[2]?
	    int iszero=-1;
	    vecteur sol1=bisection_solver(eq,v[1],rep-dx,rep+dx,iszero,contextptr);
	    // keep only solutions, no sign reversal
	    for (unsigned i=0;i<sol1.size();++i){
	      if (is_greater(1e-4,subst(eq,v[1],sol1[i],false,contextptr),contextptr))
		sol.push_back(sol1[i]);
	    }
	  }
	  if (sol.empty()){
	    vecteur eqv(makevecteur(eq,v[1],re(p,contextptr)));
	    sol=gen2vecteur(in_fsolve(eqv,contextptr));
	  }
	}
	else
	  sol=solve(eq,v[1],0,contextptr);
	sol.push_back(v[3]);
	// find smallest 
	const_iterateur it=sol.begin(),itend=sol.end();
	gen distance2_found=distance2pp(subst(v[0],v[1],v[2],false,contextptr),p,contextptr),cur_distance2;
	for (;it!=itend;++it){
	  if (!is_zero(it->im(contextptr)))
	    continue;
	  cur_distance2=distance2pp(subst(v[0],v[1],*it,false,contextptr),p,contextptr);
	  if (ck_is_greater(distance2_found,cur_distance2,contextptr)){
	    t_found=*it;
	    distance2_found=cur_distance2;
	  }
	} // end for
#ifndef NO_STDEXCEPT
      } // end try
      catch(std::runtime_error & ){ // could not solve
	// if curve is a function (plotfunc) return re(p)
	gen eq=re(v[0],contextptr);
	rewrite_with_t_real(eq,v[1],contextptr);
	vecteur sol(solve(eq-re(p,contextptr),v[1],0,contextptr));
	if (sol.empty())
	  return gensizeerr(contextptr);
	t_found=sol.front();
      }
#endif
      return t_found;
    }
    // unknown
    return undef;
  }

  // cercle to parametric curve
  gen cercle2curve(const gen & f,GIAC_CONTEXT){
    // ck_parameter_t();
    gen centre,rayon;
    if (!centre_rayon(f,centre,rayon,false,contextptr))
      return gensizeerr(contextptr); // don't care about radius sign
    return symb_curve(gen(makevecteur(centre+normal(rayon,contextptr)*symb_exp(cst_i*t__IDNT_e),t__IDNT_e,zero,cst_two_pi),_PNT__VECT),f);
  }

  // line to parametric curve
  gen line2curve(const gen & f){
    if ( (f.type!=_VECT) || (f._VECTptr->size()!=2))
      return gensizeerr(gettext("line2curve"));
    identificateur idt(" t");
    gen t(idt);
    gen A(f._VECTptr->front());
    gen B(f._VECTptr->back());
    gen tmin,tmax;
    if (f.subtype==_LINE__VECT){
      tmin=minus_inf;
      tmax=plus_inf;
    }
    else {
      tmin=zero;
      tmax=plus_one;
    }
    return symb_curve(gen(makevecteur(ratnormal(t*B+(1-t)*A),t,tmin,tmax),_PNT__VECT),f);
  }

  // square distance curve/curve
  static gen distance2cc(const gen & e,const gen & f,GIAC_CONTEXT){
    return gensizeerr(gettext("Distance curve/curve not implemented"));
  }
  static gen complex_abs3(const gen & f,GIAC_CONTEXT){
    if (f.type==_VECT && f.subtype==_POINT__VECT && f._VECTptr->size()==3){
      if (f._VECTptr->back().type==_CPLX){
	vecteur f1(*f._VECTptr);
	f1[2]=abs(f1[2],contextptr);
	return gen(f1,_POINT__VECT);
      }
    }
    return f;
  }

  // square distance curve/point or curve/curve
  // ee is a curve
  static gen distance2cp(const gen & ee,const gen & f0,GIAC_CONTEXT){
    gen f=complex_abs3(f0,contextptr);
    gen e=ee._SYMBptr->feuille._VECTptr->front();
    if ((f.type==_SYMB) && (f._SYMBptr->sommet==at_curve))
      return distance2cc(e,f._SYMBptr->feuille._VECTptr->front(),contextptr);
    if ((f.type==_SYMB) && (f._SYMBptr->sommet==at_cercle))
      return distance2cp(ee,cercle2curve(f,contextptr),contextptr);
    vecteur v(*e._VECTptr);
    gen projete=subst(v[0],v[1],projection(ee,f,contextptr),false,contextptr);
    return distance2pp(projete,f,contextptr);
  }

  // square distance line/point
  static gen distance2sp(const_iterateur it,const const_iterateur itend,const gen & p0,int subtype,GIAC_CONTEXT){
    gen res,newres,a,b,t,c,r,p=complex_abs3(p0,contextptr);
    bool is_cercle=centre_rayon(p,c,r,false,contextptr);
    a=*it;
    if (is_cercle && it+2==itend && subtype==_LINE__VECT){
      ++it;
      b=*it;
      t=projection(a,b,c,contextptr);
      if (is_undef(t)) return undef;
      gen pr=t*b+(1-t)*a; // projection of the center c
      gen d2=abs_norm2(pr-c,contextptr);
      gen r2=r*conj(r,contextptr);
      if (is_strictly_greater(d2,r2,contextptr))
	return pow(sqrt(d2,contextptr)-sqrt(r2,contextptr),2);
      return 0;
    }
    res=distance2pp(a,p,contextptr);
    ++it;
    for (;;){
      b=*it;
      if (is_cercle) // FIXME this is incorrect, see above for line__vect
	t=projection(a,b,c,contextptr);
      else
	t=projection(a,b,p,contextptr);
      if (is_undef(t)) return t;
      if (subtype==_LINE__VECT || (ck_is_positive(t,contextptr) && (subtype==_HALFLINE__VECT || ck_is_greater(1,t,contextptr))))
	newres=distance2pp(t*b+(1-t)*a,p,contextptr);
      else
	newres=distance2pp(b,p,contextptr);
      if (ck_is_greater(res,newres,contextptr))
	res=newres;
      ++it;
      if (it==itend)
	break;
      a=b;
    }
    return res;
  }

  // square distance point/point
  gen distance2pp(const gen & ee,const gen & ff,GIAC_CONTEXT){
    if (is_undef(ee) || is_undef(ff))
      return ee+ff;
    gen e(remove_at_pnt(ee));
    gen f(remove_at_pnt(ff));
    f=complex_abs3(f,contextptr);
    if (e.is_symb_of_sommet(at_hyperplan)){
      if (!f.is_symb_of_sommet(at_hyperplan))
	return distance2pp(f,e,contextptr);
      vecteur n1,P1,n2,P2;
      hyperplan_normal_point(e,n1,P1);
      hyperplan_normal_point(f,n2,P2);
      if (!est_parallele_vecteur(n1,n2,contextptr))
	return 0;
      return pow(dotvecteur(n1,subvecteur(P2,P1)),2,contextptr)/dotvecteur(n1,n1);
    }
    if (e.type==_VECT){
      if (e.subtype==_POINT__VECT || (f.type==_VECT && f.subtype==_POINT__VECT && e.subtype!=_LINE__VECT)){ // n-d point
	vecteur & ev = *e._VECTptr;
	if (f.type==_VECT && f.subtype==_POINT__VECT){ // square distance between 2 n-d points
	  vecteur ef(subvecteur(*f._VECTptr,ev));
	  return dotvecteur(ef,ef);
	}
	if (f.is_symb_of_sommet(at_hyperplan)){ // point to hyperplan
	  // get normal vector n and base point P of hyperplan
	  gen & ff=f._SYMBptr->feuille;
	  if (ff.type==_VECT && ff._VECTptr->size()==2){
	    gen & n = ff._VECTptr->front();
	    gen & P = ff._VECTptr->back();
	    // -> ((P-e).n)^2/n.n
	    if (P.type==_VECT && n.type==_VECT){
	      vecteur & nv = *n._VECTptr;
	      vecteur & Pv = *P._VECTptr;
	      return pow(dotvecteur(subvecteur(Pv,ev),nv),2,contextptr)/dotvecteur(nv,nv);
	    }
	  }
	}
	if (f.is_symb_of_sommet(at_hypersphere)){
	  // point to hypersphere: (abs_norm(point-center)-radius)^2
	  gen centre,rayon;
	  if (!centre_rayon(f,centre,rayon,true,contextptr))
	    return gensizeerr(contextptr);
	  gen delta=abs_norm(e-centre,contextptr)-rayon; 
	  return delta*delta;
	}
	if (f.type==_VECT && f._VECTptr->size()==2){ // point to line
	  gen & A=f._VECTptr->front();
	  gen & B=f._VECTptr->back();
	  if (A.type==_VECT && B.type==_VECT){
	    // M=A+t(B-A), find t s.t. B-A is orthogonal to e-M
	    // (B-A).e=(B-A).M=(B-A).A+t(B-A).(B-A)
	    // t=(B-A).(e-A)/(B-A).(B-A)
	    vecteur & vA=*A._VECTptr;
	    vecteur & vB=*B._VECTptr;
	    vecteur vAB(subvecteur(vB,vA));
	    vecteur veA(subvecteur(vA,ev));
	    gen t=-dotvecteur(veA,vAB)/dotvecteur(vAB,vAB);
	    // distance2 = (M-e).(M-e), M-e=A-e+t*(B-A)
	    vecteur veM(addvecteur(veA,multvecteur(t,vAB)));
	    t=dotvecteur(veM,veM);
	    // cerr << dotvecteur(veM,vAB)  << endl;
	    return  t;
	  }
	}
      } // end e of type n-d point
      if (e._VECTptr->size()==2){ // e of type line
	if (f.type!=_VECT){ // 2-d line/point
	  return distance2sp(e._VECTptr->begin(),e._VECTptr->end(),f,e.subtype,contextptr);
	}
	if (f.type==_VECT && f.subtype==_POINT__VECT)
	  return distance2pp(f,e,contextptr);
	if (f.type==_VECT && f._VECTptr->size()==2){ // line to line
	  gen M,N;
	  vecteur n;
	  if (e._VECTptr->front().type!=_VECT){ // 2-d
	    gen & A =e._VECTptr->front();
	    gen & B =e._VECTptr->back();
	    gen & C =f._VECTptr->front();
	    gen & D =f._VECTptr->back();
	    gen AB=B-A;
	    if (!est_parallele(AB,D-C,contextptr))
	      return 0;
	    n=makevecteur(im(AB,contextptr),re(AB,contextptr));
	    return pow(dotvecteur(n,makevecteur(re(C-A,contextptr),im(C-A,contextptr))),2,contextptr)/dotvecteur(n,n);
	  }
	  else { // 3-d 
	    if (perpendiculaire_commune(e,f,M,N,n,contextptr)){
	      gen MN(M-N);
	      if (MN.type==_VECT){
		vecteur & vMN=*MN._VECTptr;
		return dotvecteur(vMN,vMN);
	      }
	    }
	  } // end 3-d
	} // end f.type==_VECT && f._VECTptr->size()==2
      } // end e._VECTptr->size()==2
      return gensizeerr(contextptr); // undef; // setsizeerr(contextptr); 
      // commented setsizeerr otherwise lots of bad arg when loading a Figure
    } // end e.type==_VECT
    if (f.type==_VECT) // f is a line or a point
      return distance2pp(f,e,contextptr);
    if ((e.type==_SYMB) && (e._SYMBptr->sommet==at_curve))
      return distance2cp(e,f,contextptr);
    if ((f.type==_SYMB) && (f._SYMBptr->sommet==at_curve))
      return distance2cp(f,e,contextptr);
    if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_cercle)){
      if ( (f.type==_SYMB) && (f._SYMBptr->sommet==at_cercle)){
	gen centre1,rayon1,centre2,rayon2;
	if (!centre_rayon(e,centre1,rayon1,true,contextptr) || !centre_rayon(f,centre2,rayon2,true,contextptr))
	  return gensizeerr(contextptr);
	gen r=abs_norm(centre2-centre1,contextptr)-rayon1-rayon2;
	if (ck_is_positive(r,contextptr)) 
	  return r*r;
	else
	  return zero;
      }
      // cercle <-> point: (abs_norm(point-center)-radius)^2
      gen centre,rayon;
      if (!centre_rayon(e,centre,rayon,true,contextptr))
	return gensizeerr(contextptr);
      gen delta=abs_norm(f-centre,contextptr)-rayon; 
      return delta*delta;
    }
    if ( (f.type==_SYMB) && (f._SYMBptr->sommet==at_cercle))
      return distance2pp(f,e,contextptr);
#ifdef IPAQ
    gen r(re(e-f,contextptr)),i(im(e-f,contextptr));
    return r*r+i*i;
#else
    if (e.type==_CPLX && f.type==_CPLX){
      gen ref=*e._CPLXptr-*f._CPLXptr;
      gen ief=*(e._CPLXptr+1)-*(f._CPLXptr+1);
      return ref*ref+ief*ief;
    }
    gen ef=e-f;
    return pow(re(ef,contextptr),2)+pow(im(ef,contextptr),2);
#endif
  }

  gen distance2(const gen & f1,const gen & f2,GIAC_CONTEXT){
    gen e1(remove_at_pnt(f1)),e2(remove_at_pnt(f2));
    if (e1.type==_VECT && e1.subtype==_VECTOR__VECT)
      e1=vector2vecteur(*e1._VECTptr);
    if (e2.type==_VECT && e2.subtype==_VECTOR__VECT)
      e2=vector2vecteur(*e2._VECTptr);
    vecteur v1,v2;
    if (e1.type!=_VECT || e1.subtype==_POINT__VECT || e1.subtype==_LINE__VECT )
      v1=makevecteur(e1);
    else
      v1=*e1._VECTptr;
    if (e2.type!=_VECT || e2.subtype==_POINT__VECT || e2.subtype==_LINE__VECT)
      v2=makevecteur(e2);
    else
      v2=*e2._VECTptr;
    const_iterateur it=v1.begin(),itend=v1.end(),jt=v2.begin(),jtend=v2.end();
    if ( (itend==it+1) && (jtend==jt+1) )
      return distance2pp(*it,*jt,contextptr);
    if (itend==it+1) 
      return distance2sp(jt,jtend,*it,e2.subtype,contextptr);
    if (jtend==jt+1) 
      return distance2sp(it,itend,*jt,e1.subtype,contextptr);
    gen res=plus_inf,newres;
    for (;it!=itend;++it){
      for (jt=v2.begin();jt!=jtend;++jt){
	newres=distance2pp(*it,*jt,contextptr);
	if (ck_is_strictly_greater(res,newres,contextptr))
	  res=newres;
      }
    }
    return res;
  }
  gen _longueur2(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT || args.subtype!=_SEQ__VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen e1=args._VECTptr->front(),e2=args._VECTptr->back();
    if (e1.type==_VECT && e2.type==_VECT){
      vecteur e12=subvecteur(*e1._VECTptr,*e2._VECTptr);
      return dotvecteur(e12,e12);
    }
    return distance2(e1,e2,contextptr);
  }
  static const char _longueur2_s []="distance2";
  static define_unary_function_eval (__longueur2,&giac::_longueur2,_longueur2_s);
  define_unary_function_ptr5( at_longueur2 ,alias_at_longueur2,&__longueur2,0,true);

  gen _longueur(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return sqrt(_longueur2(args,contextptr),contextptr);
  }
  static const char _longueur_s []="distance";
  static define_unary_function_eval (__longueur,&giac::_longueur,_longueur_s);
  define_unary_function_ptr5( at_longueur ,alias_at_longueur,&__longueur,0,true);

  gen approx_area(const gen & f,const gen & x,const gen & a_,const gen &b_,int n,int method,GIAC_CONTEXT){
    gen a(a_),b(b_);
    if (a.is_symb_of_sommet(at_pnt))
      a=_abscisse(a,contextptr);
    if (b.is_symb_of_sommet(at_pnt))
      b=_abscisse(b,contextptr);
    gen dx=(b-a)/n,x0=a,xf=x0,fxf,A;
    if (method==_RECTANGLE_DROIT || method==_RECTANGLE_GAUCHE || method==_POINT_MILIEU){
      if (method==_RECTANGLE_DROIT)
	xf=a+dx;
      if (method==_POINT_MILIEU)
	xf=a+dx/2;
      for (int i=0;i<n;++i){
	fxf=evalf(quotesubst(f,x,xf,contextptr),1,contextptr);
	A=A+dx*fxf;
	xf=xf+dx;
      }
      return A;
    }
    if (method==_TRAPEZE){
      fxf=evalf(quotesubst(f,x,a,contextptr),1,contextptr);
      A=dx*fxf/2;
      xf=a+dx;
      for (int i=0;i<n-1;++i){
	fxf=evalf(quotesubst(f,x,xf,contextptr),1,contextptr);
	A=A+dx*fxf;
	xf=xf+dx;
      }
      fxf=evalf(quotesubst(f,x,b,contextptr),1,contextptr);
      A=A+dx*fxf/2;
      return A;
    }
    if (method==_SIMPSON){
      fxf=evalf(quotesubst(f,x,a,contextptr),1,contextptr);
      A = dx*fxf/6;
      xf = a+dx;
      x0 = a+dx/2;
      for (int i=0;i<n-1;++i){
	fxf=evalf(quotesubst(f,x,x0,contextptr),1,contextptr);
	A += 2*dx*fxf/3;
	fxf=evalf(quotesubst(f,x,xf,contextptr),1,contextptr);
	A += dx*fxf/3;
	x0 += dx;
	xf += dx;
      }
      fxf=evalf(quotesubst(f,x,x0,contextptr),1,contextptr);
      A += 2*dx*fxf/3;
      fxf=evalf(quotesubst(f,x,b,contextptr),1,contextptr);
      A += dx*fxf/6;
      return A;
    }
    if (method==_ROMBERGM)
      return rombergo(f,x,a,b,n,contextptr);
    return romberg(f,x,a,b,epsilon(contextptr),n,contextptr);
  }

  gen _aire(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && !args._VECTptr->empty() && args._VECTptr->front().is_symb_of_sommet(at_pnt)){
      gen res=0;
      for (unsigned i=0;i<args._VECTptr->size();++i)
	res += _aire((*args._VECTptr)[i],contextptr);
      return res;
    }
    gen g=remove_at_pnt(args);
    if (g.is_symb_of_sommet(at_cercle)){
      gen centre,rayon;
      if (!centre_rayon(g,centre,rayon,false,contextptr))
	return gensizeerr(contextptr);
      if (g._SYMBptr->feuille.type==_VECT && g._SYMBptr->feuille._VECTptr->size()>=3)
	return ratnormal(((*g._SYMBptr->feuille._VECTptr)[2]-(*g._SYMBptr->feuille._VECTptr)[1])*(rayon*conj(rayon,contextptr))/2);
      return cst_pi*ratnormal(rayon*conj(rayon,contextptr));
    }
    if (g.type!=_VECT || g.subtype==_POINT__VECT)
      return 0; // so that a single point has area 0
    vecteur v=*g._VECTptr;
    int s=v.size();
    // search for a numeric integration method
    if (s==2 && v[1].is_symb_of_sommet(at_equal)){
      gen f(v[0]),x(vx_var),tmp(v[1]),a,b;
      if (tmp.is_symb_of_sommet(at_equal) && tmp._SYMBptr->feuille.type==_VECT && tmp._SYMBptr->feuille._VECTptr->size()==2){
	x=tmp._SYMBptr->feuille[0];
	tmp=tmp._SYMBptr->feuille[1];
      }
      if (tmp.is_symb_of_sommet(at_interval) && tmp._SYMBptr->feuille.type==_VECT && tmp._SYMBptr->feuille._VECTptr->size()==2){
	a=tmp._SYMBptr->feuille[0];
	b=tmp._SYMBptr->feuille[1];
	return _integrate(gen(makevecteur(f,x,a,b),_SEQ__VECT),contextptr);
      }
    }
    if (s>3){
      for (int i=0;i<s;++i){
	if (v[i].type==_INT_ && v[i].subtype==_INT_SOLVER){
	  int method=v[i].val,n;
	  v.erase(v.begin()+i);
	  v[2]=_floor(v[2],contextptr);
	  if (v[2].type!=_INT_)
	    return gensizeerr(gettext("area(f(x),x=a..b,n,method])"));
	  n=v[2].val;
	  gen f(v[0]),x(vx_var),tmp(v[1]),a,b;
	  if (tmp.is_symb_of_sommet(at_equal) && tmp._SYMBptr->feuille.type==_VECT && tmp._SYMBptr->feuille._VECTptr->size()==2){
	    x=tmp._SYMBptr->feuille[0];
	    tmp=tmp._SYMBptr->feuille[1];
	  }
	  if (tmp.is_symb_of_sommet(at_interval) && tmp._SYMBptr->feuille.type==_VECT && tmp._SYMBptr->feuille._VECTptr->size()==2){
	    a=tmp._SYMBptr->feuille[0];
	    b=tmp._SYMBptr->feuille[1];
	  }
	  else
	    return gensizeerr(gettext("area(f(x),x=a..b,n,method])"));
	  return approx_area(f,x,a,b,n,method,contextptr);
	}
      }
    }
    if (s<3)
      return undef;
    if (v.front()!=v.back()){
      ++s;
      v.push_back(v.front());
    }
    gen res;
    for (int i=3;i<s;++i){
      gen cote1(v[i-2]-v[0]),cote2(v[i-1]-v[0]);
      if (cote1.type==_VECT && cote2.type==_VECT)
	res += l2norm(cross(*cote1._VECTptr,*cote2._VECTptr,contextptr),contextptr);
      else
	res += im(cote2,contextptr)*re(cote1,contextptr)-re(cote2,contextptr)*im(cote1,contextptr);
    }
    return recursive_normal(res/2,contextptr);
  }
  static const char _aire_s []="area";
  static define_unary_function_eval (__aire,&giac::_aire,_aire_s);
  define_unary_function_ptr5( at_aire ,alias_at_aire,&__aire,0,true);

  gen _perimetre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen g=remove_at_pnt(args);
    if (g.is_symb_of_sommet(at_cercle)){
      gen centre,rayon;
      if (!centre_rayon(g,centre,rayon,true,contextptr))
	return gensizeerr(contextptr);
      return recursive_normal(cst_two_pi*rayon,contextptr);
    }
    if (g.type!=_VECT)
      return undef;
    vecteur v=*g._VECTptr;
    int s=v.size();
    if (s<3)
      return undef;
    if (v.front()!=v.back()){
      ++s;
      v.push_back(v.front());
    }
    gen res;
    for (int i=0;i<s-1;++i){
      res = res + sqrt(distance2pp(v[i],v[i+1],contextptr),contextptr);
    }
    return recursive_normal(res,contextptr);
  }
  static const char _perimetre_s []="perimeter";
  static define_unary_function_eval (__perimetre,&giac::_perimetre,_perimetre_s);
  define_unary_function_ptr5( at_perimetre ,alias_at_perimetre,&__perimetre,0,true);

  // angle accepts an optionnal last argument as a string
  // for legends, if the string has a terminal =, the value of
  // the angle is added to the legend
  gen angle(const vecteur & v1,const vecteur & v2,GIAC_CONTEXT){
    return acos(simplify(dotvecteur(v1,v2)/sqrt(dotvecteur(v1,v1)*dotvecteur(v2,v2),contextptr),contextptr),contextptr);
  }

  gen _angle(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || args.subtype!=_SEQ__VECT || (args._VECTptr->size()<2))
      return arg(simplify(args,contextptr),contextptr);
    vecteur v(*args._VECTptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    v=vecteur(v.begin(),v.begin()+s);
    bool montrer=attributs.size()>1;
    string legende;
    if (montrer)
      legende=gen2string(attributs[1]);
    if (v.back().type==_STRNG){
      legende=*v.back()._STRNGptr;
      v.pop_back();
      montrer=true;
    }
    if (v.size()<2)
      return gensizeerr(contextptr);
    gen e=v.front(),f=v[1],g;
    e=remove_at_pnt(e);
    f=remove_at_pnt(f);
    bool tmp=angle_radian(contextptr);
    angle_radian(true,contextptr);
    gen res;
    if (e.is_symb_of_sommet(at_hyperplan)){
      swapgen(e,f);
    }
    if (f.is_symb_of_sommet(at_hyperplan)){
      vecteur nf=hyperplan_normal(f);
      if (e.is_symb_of_sommet(at_hyperplan)){
	vecteur ne=hyperplan_normal(e);
	res=angle(nf,ne,contextptr);	
      }
      else {
	if (e.type!=_VECT || e._VECTptr->size()!=2)
	  return gensizeerr(gettext("angle plan with unknown"));
	gen de=e._VECTptr->back()-e._VECTptr->front();
	if (de.type!=_VECT)
	  return gensizeerr(contextptr);
	res=angle(nf,*de._VECTptr,contextptr);
      }
    }
    else {
      if (e.type==_VECT && e._VECTptr->size()!=3){
	if ((e._VECTptr->size()!=2) || (f._VECTptr->size()!=2))
	  return gensizeerr(gettext("angle"));
	vecteur w=inter(v.front(),v[1],0); // context does not apply for lines
	if (w.empty())
	  return gensizeerr(gettext("Lines must intersect"));
	gen w0=remove_at_pnt(w.front());
	g=f._VECTptr->back();
	if (g==w0)
	  g=f._VECTptr->front();
	f=e._VECTptr->back();
	if (f==w0)
	  f=e._VECTptr->front();
	e=w0;
      }
      else {
	if (f.type==_VECT && f._VECTptr->size()!=3){
	  if (f._VECTptr->size()!=2)
	    return gensizeerr(gettext("angle"));
	  if (v.size()==3){
	    f=e+f._VECTptr->back()-f._VECTptr->front();
	    g=remove_at_pnt(v[2]);
	    if (g.type==_VECT && g._VECTptr->size()==2)
	      g=e+g._VECTptr->back()-g._VECTptr->front();
	  }
	  else {
	    g=f._VECTptr->back();
	    f=f._VECTptr->front();
	  }
	}
	else {
	  if (v.size()!=3)
	    return gensizeerr(gettext("angle"));
	  g=remove_at_pnt(v[2]);
	  if (g.type==_VECT && g._VECTptr->size()==2)
	    g=e+g._VECTptr->back()-g._VECTptr->front();
	}
      }
      if (e.type==_VECT && e._VECTptr->size()==3 && f.type==_VECT && f._VECTptr->size()==3 && g.type==_VECT && g._VECTptr->size()==3){
	vecteur v1(*(f-e)._VECTptr),v2(*(g-e)._VECTptr);
	res=angle(v1,v2,contextptr);
      }
      else
	res=arg(simplify(conj(f-e,contextptr)*(g-e),contextptr),contextptr);
    }
    gen c,c2;
    montrer = montrer && f.type!=_VECT;
    if (montrer){ 
      gen ef=(f-e)/5,eg=(g-e)/5;
      // Show the angle on the figure, using an arc of circle 
      if (is_zero(evalf(abs(res,contextptr)-cst_pi_over_2,1,contextptr))){
	gen ef_eg=abs(evalf(ef,1,contextptr)/evalf(eg,1,contextptr),contextptr);
	if (is_greater(ef_eg,0.2,contextptr) && is_greater(5.0,ef_eg,contextptr)){
	  ef_eg=sqrt(ef_eg,contextptr);
	  ef=ef/ef_eg;
	  eg=eg*ef_eg;
	}
	c=gen(makevecteur(e+ef,e+ef+eg,e+eg),_LINE__VECT);
      }
      else
	c=symbolic(at_cercle,gen(makevecteur(makevecteur(e-ef,e+ef),0,res,1),_PNT__VECT));
      c=symb_pnt(c,attributs[0],contextptr);
      c2=gen(makevecteur(f,e,g),_LINE__VECT);
      c2=symb_pnt(c2,attributs[0],contextptr);
    }
    if (!tmp){
      gen resd;
      if (has_evalf(res,resd,1,contextptr))
	res= rad2deg_d*resd;
      angle_radian(tmp,contextptr);
    }
    if (montrer && c.is_symb_of_sommet(at_pnt) && c._SYMBptr->feuille.type==_VECT && c._SYMBptr->feuille._VECTptr->size()==2){
      vecteur v=*c._SYMBptr->feuille._VECTptr;
      if (!legende.empty() && legende[legende.size()-1]=='=')
	v.push_back(string2gen(legende+res.print(contextptr),false));
      else
	v.push_back(string2gen(legende,false));
      c=symbolic(at_pnt,gen(v,_PNT__VECT));
      return gen(makevecteur(res,c,c2),_SEQ__VECT);
    }
    return res;
  }
  static const char _angle_s []="angle";
  static define_unary_function_eval (__angle,&giac::_angle,_angle_s);
  define_unary_function_ptr5( at_angle ,alias_at_angle,&__angle,0,true);

  static bool is_near(const gen & a,const gen & b0,double eps,GIAC_CONTEXT){
    gen b=b0;
    if (b.is_symb_of_sommet(at_hyperplan) || b.is_symb_of_sommet(at_animation))
      return false;
    if (b.type==_VECT){
      if (b.subtype==_VECTOR__VECT)
	b.subtype=0;
      if (b.subtype==_POLYEDRE__VECT){
	const_iterateur it=b._VECTptr->begin(),itend=b._VECTptr->end();
	for (;it!=itend;++it){
	  if (it->type==_VECT){ // *it is a face
	    const_iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
	    // *jt is a polygone
	    for (;jt!=jtend;++jt){
	      if (is_near(a,*jt,eps,contextptr))
		return true;
	    }
	  }
	}
	return false;
      }
      if (b.subtype!=_POINT__VECT && b._VECTptr->size()>6 ){
	const_iterateur it=b._VECTptr->begin(),itend=b._VECTptr->end();
	for (;it!=itend;++it){
	  if (is_near(a,*it,eps,contextptr))
	    return true;
	}
	return false;
      }
    }
    if (b.is_symb_of_sommet(at_curve) && b._SYMBptr->feuille.type==_VECT && b._SYMBptr->feuille._VECTptr->size()>1){
      gen tmp=(*b._SYMBptr->feuille._VECTptr)[1];
      return is_near(a,tmp,eps,contextptr);
    }
    if (b.is_symb_of_sommet(at_hypersurface) && b._SYMBptr->feuille.type==_VECT && b._SYMBptr->feuille._VECTptr->size()>=2){
      gen & b0=(*b._SYMBptr->feuille._VECTptr)[0];
      if (b0.type==_VECT && b0._VECTptr->size()>=5){
	gen & b04=(*b0._VECTptr)[4];
	if (b04.type==_VECT)
	  return is_near(a,b04,eps,contextptr);
      }
      return false;
    }
    gen d;
#ifndef NO_STDEXCEPT
    try {
#endif
      d=distance2(a,b,contextptr).evalf_double(eval_level(contextptr),contextptr); 
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & ){
      // cerr  << error.what() << endl;
      // *logptr(contextptr) << error.what() << endl;
      return false;
    }
#endif
    return d.type==_DOUBLE_ && d._DOUBLE_val<eps*eps;
  }

  // added protection
  // find neighboor points of p in a history vector v (distance < eps)
  vector<int> nearest_point(const vecteur & v,const gen & p,double eps,GIAC_CONTEXT){
    vector<int> res;
    gen pf=evalf(p,1,contextptr),qf;
    if (!lidnt(pf).empty())
      return res;
    const_iterateur it=v.begin(),itend=v.end();
#ifndef NO_STDEXCEPT
    try {
#endif
      for (int i=0;it!=itend;++it,++i){
	vecteur w=gen2vecteur(*it);
	const_iterateur jt=w.begin(),jtend=w.end();
	for (;jt!=jtend;++jt){
	  gen g=*jt;
	  if ( (g.type==_SYMB) && equalposcomp(plot_sommets,g._SYMBptr->sommet) && !g.is_symb_of_sommet(at_parameter)){
	    qf=remove_at_pnt(evalf(g,1,contextptr));
	    if ( (qf.is_symb_of_sommet(at_curve) || qf.is_symb_of_sommet(at_hypersurface) || lidnt(qf).empty()) && is_near(pf,qf,eps,contextptr)){
	      if (is_segment(qf))
		res.insert(res.begin(),i);
              else
		res.push_back(i);
	      break;
	    }
	  }
	}
      }
#ifndef NO_STDEXCEPT
    } catch (std::runtime_error & e){
      *logptr(contextptr) << e.what() << endl;
    }
#endif
    return res;
  }

#ifdef HAVE_SIGNAL_H_OLD
  extern bool signal_store; // if false then child sto is not signal to parent

  // used to signal an intermediate answer to the parent process
  gen _signal(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
#ifndef WIN32
    if (child_id) // must be a child process!
      return symbolic(at_signal,args);
#endif
    gen args_evaled;
    try {
      args_evaled=args.eval(1,contextptr);
    }
    catch (std::runtime_error & error ){
      args_evaled = string2gen('"'+string(error.what())+'"');
    }
#ifdef WIN32
    history_in(contextptr).push_back(symbolic(at_signal,args));
    history_out(contextptr).push_back(args_evaled);
#ifdef WITH_GNUPLOT
    plot_instructions.push_back(args_evaled);    
#endif
    return args_evaled;
#else // WIN32
    ofstream child_out(cas_sortie_name().c_str());
    archive(child_out,symbolic(at_signal,args),contextptr);
    archive(child_out,args_evaled,contextptr);
    child_out << messages_to_print << "ÿ" ;
    child_out.close();
    // cerr << "Signal reads " << res << endl;
    return wait_parent();
#endif // WIN32
  }
  static const char _signal_s []="signal";
  static define_unary_function_eval_quoted (__signal,&giac::_signal,_signal_s);
  define_unary_function_ptr5( at_signal ,alias_at_signal,&__signal,_QUOTE_ARGUMENTS,true);
#endif // HAVE_SIGNAL_H_OLD

  vecteur gen2vecteur(const gen & args){
    if (args.type==_VECT)
      return *args._VECTptr;
    else
      return vecteur(1,args);
  }
  // user input sent back to the parent process
  // Use a vector of format [message,default_value,variable,convert_to_string]
  gen _click(const gen & args,GIAC_CONTEXT){
    if (interactive_op_tab && interactive_op_tab[3])
      return interactive_op_tab[3](args,contextptr);
    if ( args.type==_STRNG && args.subtype==-1) return  args;
#ifdef HAVE_SIGNAL_H_OLD
    vecteur v(gen2vecteur(args));
    int vs=v.size();
    if (vs>1)
      v[1]=eval(v[1],contextptr);
    gen res;
#ifdef WIN32
    cout << "// " << args ;
    string s;
    cin >> s;
    if (vs==4)
      res=string2gen(s,false);
    else
      res=gen(s,contextptr);
#else
    if (child_id){ 
      cout << "// " << args;
      string s;
      cin >> s;
      if (vs==4)
	res=string2gen(s,false);
      else
	res=gen(s,contextptr);
    }
    else {
      ofstream child_out(cas_sortie_name().c_str());
      gen e(symbolic(at_click,args));
      // cerr << "Archiving " << e << endl;
      archive(child_out,e,contextptr);
      archive(child_out,e,contextptr);
      if ( (args.type==_VECT) && (args._VECTptr->empty()) )
	child_out << "User input requested\n" << "ÿ" ;
      else
	child_out << args << "ÿ" ;
      child_out.close();
      kill_and_wait_sigusr2();
      ifstream child_in(cas_entree_name().c_str());
      res= unarchive(child_in,contextptr);
      child_in.close();
      // cerr << "Click reads " << res << endl;
    }
#endif //WIN32
    if (vs>2 && !is_zero(v[2])){
      if (res.type==_VECT){
	if (vs==4 && res._VECTptr->size()==2){
	  vecteur tmp=*res._VECTptr;
	  if (tmp[1].is_symb_of_sommet(at_sto)){
	    gen f=tmp[1]._SYMBptr->feuille;
	    if (f.type==_VECT && f._VECTptr->size()==2){
	      tmp[1]=symb_sto(string2gen(f._VECTptr->front().print(contextptr),false),f._VECTptr->back());
	      *logptr(contextptr) << tmp[1] << endl;
	      res=tmp;
	    }
	  }
	}
	return eval(res,contextptr);
      }
      else
	return sto(res,v[2],contextptr); 
    }
    else
      return res;
#else // HAVE_SIGNAL_H_OLD
    return undef;
#endif 
  }
  static const char _click_s []="click";
#ifdef RTOS_THREADX
  define_unary_function_eval_index(1,__click,&giac::_click,_click_s);
  // const unary_function_eval __click(1,&giac::_click,_click_s);
#else
  unary_function_eval __click(1,&giac::_click,_click_s);
#endif
  define_unary_function_ptr5( at_click ,alias_at_click,&__click,_QUOTE_ARGUMENTS,true);

  static vecteur make_VECTifnot_VECT(const gen & e){
    if ((e.type==_VECT) && !e._VECTptr->empty())
      return *e._VECTptr;
    return makevecteur(e);
  }

  static gen in_parameter2point(const vecteur & v,GIAC_CONTEXT){
    // convert geometric object, parameter value to
    // a point on the geometric object
    gen res;
    if (v.size()<2) return gensizeerr(gettext("plot.cc/parameter2point"));
    gen t=v.back(); // was v[1], fixed for G:=plotfunc(1/t,t);element(G,t)
    gen v0=v[0];
    if (v0.type==_VECT && !v0._VECTptr->empty())
      v0=v0._VECTptr->front();
    gen geo_obj=remove_at_pnt(v0);
    gen attribut=default_color(contextptr);
    if (v0.is_symb_of_sommet(at_pnt) && v0._SYMBptr->feuille.type==_VECT && v0._SYMBptr->feuille._VECTptr->size()>1)
      attribut=(*v0._SYMBptr->feuille._VECTptr)[1];
    // geo_obj.type = _VECT (ligne brisee), _SYMB (at_cercle), symb_curve
    if (geo_obj.type==_VECT){
      vecteur ligne=*geo_obj._VECTptr;
      if (ligne.size()<2) return gensizeerr(gettext("plot.cc/parameter2point"));
      if (t.type!=_VECT){
	if ( (geo_obj.subtype==_GROUP__VECT || geo_obj.subtype==_HALFLINE__VECT) && is_positive(-t,contextptr))
	  t=0;
	if (geo_obj.subtype==_GROUP__VECT && is_greater(t,1,contextptr))
	  t=1;
	return symb_pnt(ligne[0]+t*(ligne[1]-ligne[0]),attribut,contextptr);
      }
      vecteur param=*t._VECTptr;
      if (param.size()<2) return gensizeerr(gettext("plot.cc/parameter2point"));
      if (param[0].type!=_INT_) return gensizeerr(gettext("plot.cc/parameter2point"));
      int n=param[0].val;
      if (n<0)
	n=0;
      if (n>=signed(ligne.size())-1)
	return symb_pnt(ligne.back(),attribut,contextptr);
      return symb_pnt(ligne[n]+param[1]*(ligne[n+1]-ligne[n]),attribut,contextptr);
    }
    if ( (geo_obj.type==_SYMB) && (geo_obj._SYMBptr->sommet==at_cercle)){
      gen centre,rayon;
      if (!centre_rayon(geo_obj,centre,rayon,true,contextptr))
	return gensizeerr(contextptr);
      return symb_pnt(centre+normal(rayon,contextptr)*exp(cst_i*t,contextptr),attribut,contextptr);
    }
    if ( (geo_obj.type==_SYMB) && (geo_obj._SYMBptr->sommet==at_curve)){
      vecteur w(*geo_obj._SYMBptr->feuille._VECTptr->front()._VECTptr);
      gen res=subst(w[0],w[1],t,false,contextptr);
      if (res.type==_VECT && res._VECTptr->size()==2)
	res=res._VECTptr->front()+cst_i*res._VECTptr->back();
      return res;
    }
    return res;
  }
  
  gen parameter2point(const vecteur & v,GIAC_CONTEXT){
    gen res=in_parameter2point(v,contextptr);
    if (res.type==_VECT && res._VECTptr->size()==3)
      res.subtype=_POINT__VECT;
    return res;
  }
  static gen element(const gen & args,vecteur & attributs,GIAC_CONTEXT){
    if ( args.type==_SYMB && args._SYMBptr->sommet==at_interval )
      return symbolic(at_parameter,args);
    if ( args.type==_VECT && args._VECTptr->size()>=2 ){
      vecteur & v=*args._VECTptr;
      if (v.front().type==_SYMB && v.front()._SYMBptr->sommet==at_interval){
	vecteur w=gen2vecteur(v.front()._SYMBptr->feuille);
	w=mergevecteur(w,vecteur(v.begin()+1,v.end()));
	return symbolic(at_parameter,gen(w,_SEQ__VECT));
      }
    }
    gen a(args);
    vecteur v(make_VECTifnot_VECT(a));
    if (args.type==_VECT){
      v=(*a._VECTptr);
      if (v.empty())
	return gensizeerr(contextptr);
      if ( (v.front().type==_VECT) && (v.front()._VECTptr->size()) )
	v.front()=v.front()._VECTptr->front();
      if ( (v.front().type!=_SYMB) || (v.front()._SYMBptr->sommet!=at_pnt))
	v=make_VECTifnot_VECT(v.front());
    }
    if (v.size()==1)
      v.push_back(plus_one_half);
    if (v[1].is_symb_of_sommet(at_pnt))
      v[1]=plus_one_half;
    gen s=remove_at_pnt(parameter2point(v,contextptr));
    if (is_undef(s))
      return s;
    gen color=default_color(contextptr);
    if (!attributs.empty())
      color=attributs[0];
    vecteur tmp=makevecteur(s,makevecteur(color,v));
    if (attributs.size()>1)
      tmp.push_back(attributs[1]);
    return symbolic(at_pnt,gen(tmp,_PNT__VECT)); // 0 instead of FL_BLACK
  }
  gen _element(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    gen g;
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    if (s==1 && (args.type!=_VECT || args.subtype==_SEQ__VECT))
      g=v.front();
    else
      g=gen(vecteur(v.begin(),v.begin()+s),_SEQ__VECT);
    return element(g,attributs,contextptr);
  }
  static const char _element_s []="element";
  static define_unary_function_eval (__element,&giac::_element,_element_s);
  define_unary_function_ptr5( at_element ,alias_at_element,&__element,0,true);

  // returns res as a local bloc
  // def_x is the definition of x as read from history
  static bool reeval_with_1arg_quoted(const gen & x,gen & res,gen & def_x,GIAC_CONTEXT){
    // cerr << x << " " << res << " " << history_in(contextptr) << endl;
    // find first occurence of storing something in x in the history
    def_x=undef;
    const_iterateur it0=history_in(contextptr).begin()-1,itend=history_in(contextptr).end(),itpos,it;
    it=itend-1;
    vecteur localvars;
    for (;;--it){
      if (it==it0)
	return false; // setsizeerr();
      // cerr << *it << " " << x << endl;
      if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_sto) ){
	if (it->_SYMBptr->feuille._VECTptr->back()==x){
	  def_x=it->_SYMBptr->feuille._VECTptr->front();
	  break;
	}
      }
    }
    ++it;
    itpos=it;
    // cerr << "Position " << itpos-history_in(contextptr).begin() << endl;
    for (;it!=itend;++it){
      // cerr << *it << " " << x << endl;
      if ( (it->type==_SYMB) && (it->_SYMBptr->sommet==at_sto) ){
	if (it->_SYMBptr->feuille._VECTptr->back()==res)
	  break;
	if (it->_SYMBptr->feuille._VECTptr->back().type==_IDNT)
	  localvars.push_back(it->_SYMBptr->feuille._VECTptr->back());
      }
    }
    if (it==itend)
      return false; // setsizeerr();
    ++it;
    vecteur prog(itpos,it);
    prog.back()=symbolic(at_return,(it-1)->_SYMBptr->feuille._VECTptr->front());
    res=symb_local(gen(localvars,_SEQ__VECT),prog,contextptr);
    return true;
  }
  
  gen _as_function_of(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( rpn_mode(contextptr) || (args.type!=_VECT) || (args._VECTptr->size()!=2) || (args._VECTptr->back().type!=_IDNT) )
      return symbolic(at_as_function_of,args);
    gen res=args._VECTptr->front();
    gen x=args._VECTptr->back(),def_x;
    if (!reeval_with_1arg_quoted(x,res,def_x,contextptr))
      return gensizeerr(contextptr);
    return symb_program(x,zero,res,contextptr);
  }
  static const char _as_function_of_s []="as_function_of";
  static define_unary_function_eval_quoted (__as_function_of,&giac::_as_function_of,_as_function_of_s);
  define_unary_function_ptr5( at_as_function_of ,alias_at_as_function_of,&__as_function_of,_QUOTE_ARGUMENTS,true);

  // equation f -> geometric object g
  static bool equation2geo2d(const gen & f0,const gen & x,const gen & y,gen & g,double tmin,double tmax,double tstep,const context * contextptr){
    gen f=_fxnd(remove_equal(f0),contextptr)._VECTptr->front();
    if (!lop(f,at_abs).empty() || !lop(f,at_sign).empty())
      return false;
    gen fx(derive(f,x,contextptr)),fy(derive(f,y,contextptr));
    bool fx0=is_zero(fx),fy0=is_zero(fy);
    if (fx0 && fy0)
      return false;
    gen fxx(derive(fx,x,contextptr)),fxy(derive(fx,y,contextptr)),fyy(derive(fy,y,contextptr));
    if (is_undef(fx)||is_undef(fy) || is_undef(fxx) || is_undef(fxy) || is_undef(fyy))
      return false;
    if (is_zero(derive(fxx,x,contextptr)) && is_zero(derive(fxy,x,contextptr)) && is_zero(derive(fyy,x,contextptr)) && is_zero(derive(fxx,y,contextptr)) && is_zero(derive(fxy,y,contextptr)) && is_zero(derive(fyy,y,contextptr)) ){
      vecteur vxy(makevecteur(x,y)),v0(2,0);
      gen c=ratnormal(subst(f,vxy,v0,false,contextptr));
      fxx=ratnormal(fxx); fyy=ratnormal(fyy);
      fxy=ratnormal(fxy);
      if (is_zero(fxy)){
	if (is_zero(fxx) && is_zero(fyy)){
	  gen d=gcd(fx,fy);
	  fx=normal(fx/d,contextptr); fy=normal(fy/d,contextptr); c=normal(c/d,contextptr);
#ifndef GIAC_HAS_STO_38
	  *logptr(contextptr) << gettext("Line ") << fx*x+fy*y+c<< "=0" << endl;
#endif
	  // line
	  if (fy0){
	    gen tmp=ratnormal(-c/fx);
	    g=gen(makevecteur(tmp,tmp+cst_i),_LINE__VECT);
	  }
	  else {
	    gen tmp=ratnormal(-c/fy);
	    g=gen(makevecteur(tmp*cst_i,tmp*cst_i+fy-fx*cst_i),_LINE__VECT);
	  }
	  return true;
	}
	if (is_zero(fxx-fyy)){
	  fx=ratnormal(subst(fx,vxy,v0,false,contextptr));
	  fy=ratnormal(subst(fy,vxy,v0,false,contextptr));
	  if (is_positive(-fxx,contextptr)){
	    fxx=-fxx; fx=-fx; fy=-fy; c=-c;
	  }
	  // f=fxx/2 (x^2 + y^2) + fx*x + fy*y + c=0
	  // x^2 + y^2 + 2fx/fxx*x + 2fy/fxx*y + 2c/fxx
	  // (x+fx/fxx)^2 + (y+fy/fxx)^2 = (fx^2+fy^2- 2c fxx)/fxx^2
	  gen centre=ratnormal(-(fx+cst_i*fy)/fxx);
	  gen rayon2( ratnormal((fx*fx+fy*fy-2*c*fxx)/(fxx*fxx)) );
	  if (is_strictly_positive(-rayon2,contextptr)){
	    g=vecteur(0);
	    return true;
	  }
	  gen rayon(normal(sqrt(rayon2,contextptr),contextptr));
	  g=symbolic(at_cercle,gen(makevecteur(gen(makevecteur(centre-rayon,centre+rayon),_GROUP__VECT),0,cst_two_pi),_PNT__VECT));
	  return true;
	}
      }
      // conique
      gen x0,y0,propre,equation_reduite;
      vecteur V0,V1,param_curves;
      if (!conique_reduite(f,makevecteur(x,y),x0,y0,V0,V1,propre,equation_reduite,param_curves,contextptr))
	return false;
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
	    res.push_back(remove_at_pnt(tmp));
	  }
	}
	else
	  res.push_back(obj);
      }
      g= (res.size()==1)? res.front() : gen(res,_SEQ__VECT);
      return true;
    }
    return false;
  }

  bool find_curve_parametrization(const gen & geo_obj,gen & m,const gen & gen_t,double T,gen & tmin,gen & tmax,bool tminmax_defined,GIAC_CONTEXT){
    if (gen_t.type!=_IDNT)
      return false;
    if (geo_obj.is_symb_of_sommet(at_cercle)){
      gen centre,rayon;
      if (!centre_rayon(geo_obj,centre,rayon,false,contextptr))
	return false;
      m=centre+rayon*(1+cst_i*gen_t)/(1-cst_i*gen_t);
      if (!tminmax_defined){
	tmin=-T;
	tmax=T;
      }
    }
    if (geo_obj.is_symb_of_sommet(at_curve)){
      gen fg=geo_obj._SYMBptr->feuille;
      if (fg.type==_VECT && !fg._VECTptr->empty()){
	fg=fg._VECTptr->front();
	if (fg.type==_VECT && fg._VECTptr->size()>3){
	  vecteur & fgv=*fg._VECTptr;
	  m=subst(fgv[0],fgv[1],gen_t,false,contextptr); 
	  if (!tminmax_defined){
	    tmin=fgv[2]; tmax=fgv[3];
	  }
	  // Check for a conic: parametrization with cos(t)/sin(t) or cosh(t)/sinh(t)
	  vecteur lv; 
	  rlvarx(m,gen_t,lv);
	  if (lv.size()==3){
	    lv=makevecteur(lv[0],lv[2]);
	    bool deg=(equalposcomp(lidnt(lv),cst_pi))!=0;
	    gen tg=gen_t;
	    if (deg)
	      tg=gen(180)/cst_pi*gen_t;
	    vecteur sincost(makevecteur(symb_sin(tg),symb_cos(tg)));
	    if (lv[0]==sincost[1])
	      lv=makevecteur(lv[1],lv[0]);
	    if (lv==sincost){
	      gen t2p1=(1+gen_t*gen_t);
	      m=subst(m,sincost,makevecteur(2*gen_t/t2p1,(1-gen_t*gen_t)/t2p1),false,contextptr);
	      if (!tminmax_defined){
		tmin=-T;
		tmax=T;
	      }
	    }
	    else {
	      sincost=makevecteur(symb_sinh(gen_t),symb_cosh(gen_t));
	      if (lv[0]==sincost[1])
		lv=makevecteur(lv[1],lv[0]);
	      if (lv==sincost){
		m=subst(m,sincost,makevecteur((gen_t-inv(gen_t,contextptr))/2,(gen_t+inv(gen_t,contextptr))/2),false,contextptr);
		if (!tminmax_defined){
		  tmin=-T;
		  tmax=T;
		}
	      }
	    }
	  }
	}
	else
	  return false;
      }
    }
    if (geo_obj.type==_VECT && geo_obj._VECTptr->size()==2){
      m=geo_obj._VECTptr->front()+gen_t*(geo_obj._VECTptr->back()-geo_obj._VECTptr->front());
      if (!tminmax_defined){
	tmin=0;
	tmax=1;
	switch (geo_obj.subtype){
	case _LINE__VECT:
	  tmin=-T;
	case _HALFLINE__VECT:
	  tmax=T;
	  break;
	}
      }
    }
    return true;
  }

  static bool doublify(const gen & tmin,const gen & tmax,double T,double & tmin_d,double & tmax_d,GIAC_CONTEXT){
    tmin_d=gnuplot_tmin;
    tmax_d=gnuplot_tmax;
    gen tmin1=evalf_double(tmin,1,contextptr), tmax1=evalf_double(tmax,1,contextptr);
    bool res=true;
    if (tmin1.type==_DOUBLE_)
      tmin_d=tmin1._DOUBLE_val;
    else
      res=false;
    if (tmax1.type==_DOUBLE_)
      tmax_d=tmax1._DOUBLE_val;
    else
      res=false;
    return res;
  }

  void read_tmintmaxtstep(vecteur & vargs,gen & t,int vstart,double &tmin,double & tmax,double &tstep,bool & tminmax_defined,bool & tstep_defined,GIAC_CONTEXT){
    tstep=gnuplot_tstep;
    tminmax_defined=false;
    tstep_defined=false;
    gen tmp;
    if (t.is_symb_of_sommet(at_equal)){
      readrange(t,gnuplot_tmin,gnuplot_tmax,tmp,tmin,tmax,contextptr);
      tminmax_defined=true;
      t=t._SYMBptr->feuille._VECTptr->front();
    }
    int vs=vargs.size();
    for (int i=vstart;i<vs;++i){
      if (readvar(vargs[i])==t){
	readrange(vargs[i],gnuplot_tmin,gnuplot_tmax,tmp,tmin,tmax,contextptr);
	tminmax_defined=true;
	vargs.erase(vargs.begin()+i);
	--vs;
	--i;
      }
      if (vargs[i].is_symb_of_sommet(at_equal) && vargs[i]._SYMBptr->feuille.type==_VECT && vargs[i]._SYMBptr->feuille._VECTptr->front().type==_INT_){
	gen n=vargs[i]._SYMBptr->feuille._VECTptr->back();
	if (vargs[i]._SYMBptr->feuille._VECTptr->front().val==_TSTEP){
	  n=evalf_double(n,1,contextptr);
	  tstep=std::abs(n._DOUBLE_val);
	  tstep_defined=true;
	  vargs.erase(vargs.begin()+i);
	  --vs;
	  --i;
	}
      }
    }
  }

  gen _lieu(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( rpn_mode(contextptr) || args.type!=_VECT  )
      return symbolic(at_lieu,args);
    vecteur attributs(1,default_color(contextptr));
    vecteur vargs=*args._VECTptr;
    int vs=read_attributs(vargs,attributs,contextptr);
    if (vs<2)
      return gendimerr(contextptr);
    vs=vargs.size();
    identificateur id_t(" t");
    gen gen_t(id_t),m,tmin,tmax,tmp,prog;
    double T=1e300;
    double tstep;
    bool tminmax_defined,tstep_defined;
    double Tmin,Tmax;
    gen tcopy(t__IDNT_e);
    read_tmintmaxtstep(vargs,tcopy,2,Tmin,Tmax,tstep,tminmax_defined,tstep_defined,contextptr);
    if (tminmax_defined){
      tmin=Tmin; tmax=Tmax;
    }
#ifdef HAVE_SIGNAL_H_OLD
    bool old_signal_store=signal_store;
    signal_store=false;
#endif
    bool old_io_graph=io_graph(contextptr);
    io_graph(false,contextptr);
#ifndef NO_STDEXCEPT
    try {
#endif
      gen res=vargs[0];
      gen x=vargs[1],def_x;
      vecteur w;
      // x must eval to a parametric element of an object
      gen tt=x.eval(1,contextptr),N;
      if (tt.type==_IDNT){
	// search back in history if tt is a parameter
	const_iterateur it0=history_out(contextptr).begin()-1,itend=history_out(contextptr).end(),it;
	it=itend-1;
	for (;;--it){
	  if (it==it0){ // no assume/element found for tt
	    N=res.eval(1,contextptr);
	    N=quotesubst(N,tt,gen_t,contextptr);
	    tmin=gnuplot_tmin;
	    tmax=gnuplot_tmax;
	    tstep=gnuplot_tstep;
	    break;
	  }
	  if (it->is_symb_of_sommet(at_parameter)){
	    vecteur v=*it->_SYMBptr->feuille._VECTptr;
	    if (v.size()>=4 && v[0]==tt){
	      tmin=v[1];
	      tmax=v[2];
	      if (!tstep_defined && v.size()>=5){
		tstep_defined=true;
		tstep=evalf_double(v[4],1,contextptr)._DOUBLE_val;
	      }
	      tminmax_defined=true;
	      N=res.eval(1,contextptr);
	      N=quotesubst(N,tt,gen_t,contextptr);
	      break;
	    }
	  }
	} // end for
      }
      else {
	if (!reeval_with_1arg_quoted(x,res,def_x,contextptr))
	  return gensizeerr(contextptr);
	prog=symb_program(x,zero,res,contextptr);
	if ( (def_x.type==_SYMB) && (def_x._SYMBptr->sommet==at_element) ){
	  gen e;
	  if (def_x._SYMBptr->feuille.type==_VECT) {
	    vecteur & v=*def_x._SYMBptr->feuille._VECTptr;
	    if ( !v.empty() )
	      e=v.front();
	  }
	  else
	    e=def_x._SYMBptr->feuille;
	  if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_interval)){
	    gen tmin=e._SYMBptr->feuille._VECTptr->front().evalf_double(1,contextptr);
	    gen tmax=e._SYMBptr->feuille._VECTptr->back().evalf_double(1,contextptr);
	    if ( (tmin.type==_DOUBLE_) && (tmax.type==_DOUBLE_) ){
	      // adjust tstep
#ifdef RTOS_THREADX
	      if (!tstep_defined)
		tstep=(tmax._DOUBLE_val-tmin._DOUBLE_val)/10;
#else
	      if (!tstep_defined)
		tstep=(tmax._DOUBLE_val-tmin._DOUBLE_val)/10;
#endif
	      vecteur p;
	      double tmin_d=tmin._DOUBLE_val,tmax_d=tmax._DOUBLE_val;
	      for (double t=tmin_d;t<tmax_d;t+=tstep){
		p.push_back(remove_at_pnt(prog(t,contextptr)));
	      }
#ifdef HAVE_SIGNAL_H_OLD
	      signal_store=old_signal_store;
#endif
	      io_graph(old_io_graph,contextptr);
	      return pnt_attrib(gen(p,_GROUP__VECT),attributs,contextptr);
	    }
	  }
	}
	// type checking
	if ( (tt.type!=_SYMB) || (tt._SYMBptr->sommet!=at_pnt) || (tt._SYMBptr->feuille.type!=_VECT) )
	  return gensizeerr(contextptr);
	vecteur v=*tt._SYMBptr->feuille._VECTptr;
	gen t0=v[1];
	if (t0.type!=_VECT)
	  return gensizeerr(contextptr);
	w=*t0._VECTptr;
	t0=w[1];
	if (t0.type!=_VECT)
	  return gensizeerr(contextptr);
	w=*t0._VECTptr; // w[0] is the parametric object=pnt( pnt[object,.] )
	gen geo_obj=remove_at_pnt(w[0]);
	// geo_obj.type = _VECT (ligne brisee), _SYMB (at_cercle), symb_curve
	// for lines, circles or rational curves try to find exact object eq
	// circle rational parametrization by center+radius*(1+it)/(1-it)
	if (!find_curve_parametrization(geo_obj,m,gen_t,T,tmin,tmax,tminmax_defined,contextptr))
	  return gensizeerr(gettext("Locus on an unknown parametric curve type"));
	if (!is_zero(m)){
	  gen tmpg=giac_assume(makevecteur(gen_t,at_real),contextptr);
	  if (is_undef(tmpg)) return tmpg;
	  gen M=symbolic(at_pnt,gen(makevecteur(m,0),_PNT__VECT));
	  if (tmin!=-T){
	    if (tmax!=T)
	      tmpg=giac_assume(symbolic(at_and,makevecteur(symb_superieur_egal(gen_t,tmin),
						      symb_inferieur_egal(gen_t,tmax)))
			  ,contextptr);
	    else
	      tmpg=giac_assume(symb_superieur_egal(gen_t,tmin),contextptr);
	  }
	  else {
	    if (tmax!=T)
	      tmpg=giac_assume(symb_inferieur_egal(gen_t,tmax),contextptr);
	  }
	  if (is_undef(tmpg)) return tmpg;
	  N=prog(M,contextptr);
	}
      }
      if (!is_zero(N)){
	N=ratnormal(exact(remove_at_pnt(N),contextptr));
	gen Nx,Ny,Nz;
	if (N.type==_VECT && N._VECTptr->size()==2){
	  // enveloppe, find equation of N as
	  // a(t)*x + b(t)*y + c(t) = 0
	  // M(t) = (x(t),y(t)) \in N + tangent at M(t) // N hence
	  // a(t)*x + b(t)*y + c(t) = 0
	  // a(t)*x'+ b(t)*y'=0 
	  // derive first equation: a'(t)*x+b'(t)*y+c'(t)=0
	  // d=(a*b'-a'*b)
	  // x=(b*c'-b'*c)/d, y=(a'*c-a*c')/d
	  Nx=N._VECTptr->front();
	  if (Nx.type==_VECT)
	    return gensizeerr(gettext("3-d enveloppe not implemented"));
	  gen ab(N._VECTptr->back()-Nx);
	  gen a=im(ab,contextptr);
	  // rewrite_with_t_real(a,gen_t,contextptr);
	  gen b=-re(ab,contextptr);
	  // rewrite_with_t_real(b,gen_t,contextptr);
	  gen c=-(a*re(Nx,contextptr)+b*im(Nx,contextptr));
	  // rewrite_with_t_real(c,gen_t,contextptr);
	  gen ap(derive(a,gen_t,contextptr)),bp(derive(b,gen_t,contextptr)),cp(derive(c,gen_t,contextptr));
	  if (is_undef(ap) || is_undef(bp) || is_undef(cp))
	    return ap+bp+cp;
	  gen d=a*bp-ap*b;
	  Nx=ratnormal((b*cp-bp*c)/d);
	  Ny=ratnormal((ap*c-a*cp)/d);
	  N=Nx+cst_i*Ny;
	}
	else {
	  if (N.type==_VECT && N._VECTptr->size()==3){
	    Nx=N._VECTptr->front();
	    Ny=(*N._VECTptr)[1];
	    Nz=N._VECTptr->back();
	  }
	  else {
	    Nx=re(N,contextptr);
	    // rewrite_with_t_real(Nx,gen_t,contextptr);
	    Ny=im(N,contextptr);
	    // rewrite_with_t_real(Ny,gen_t,contextptr);
	  }
	}
	_purge(gen_t,contextptr);
	if (lvarxpow(N,gen_t).size()==1){
	  // print resultant
	  gen x(identificateur("x")),y(identificateur("y")),z(identificateur("z"));
	  gen lieu_eq(undef),geoobj,lieu_geo;
	  if (is_zero(Nz)){
#if 1
	    lieu_eq=_resultant(makevecteur(x-Nx,y-Ny,gen_t),contextptr);
	    lieu_eq=_factor(lieu_eq,contextptr);
#else
	    gen tmp,numx,denx,numy,deny;
	    tmp=_fxnd(Nx,contextptr);
	    numx=tmp[0]; denx=tmp[1];
	    tmp=_fxnd(Ny,contextptr);
	    numy=tmp[0]; deny=tmp[1];
	    lieu_eq=_resultant(makevecteur(x*denx-numx,y*deny-numy,gen_t),contextptr);
	    lieu_eq=_factor(lieu_eq,contextptr);
#endif
#ifndef GIAC_HAS_STO_38
	    *logptr(contextptr) << gettext("Equation 0 = ") << lieu_eq << endl;
#endif
	  }
	  // FIXME: recognize 3-d locus
	  double tmin_d,tmax_d;
	  if (!doublify(tmin,tmax,T,tmin_d,tmax_d,contextptr))
	    return gensizeerr(contextptr);
	  double tstep_d=tstep_defined?tstep:(tmax_d-tmin_d)/80;
	  if (equation2geo2d(lieu_eq,x,y,geoobj,tmin_d,tmax_d,tstep_d,contextptr)){
	    vecteur lieu_vect;
	    if (geoobj.type==_VECT && geoobj.subtype==_SEQ__VECT)
	      lieu_vect=*geoobj._VECTptr;
	    else
	      lieu_vect=vecteur(1,geoobj);
	    const_iterateur it=lieu_vect.begin(),itend=lieu_vect.end();
	    vecteur resv;
	    for (;it!=itend;++it){
	      lieu_geo=*it;
	      // Nx,Ny is on lieu_geo
	      if (lieu_geo.type==_VECT && lieu_geo._VECTptr->size()==2){ 
		gen N1(lieu_geo._VECTptr->front()),N2(lieu_geo._VECTptr->back());
		int subt=_LINE__VECT;
		if (tmin!=-T){
		  N1=subst(Nx,gen_t,tmin,false,contextptr)+cst_i*subst(Ny,gen_t,tmin,false,contextptr);
		  subt=_HALFLINE__VECT;
		  if (tmax==T)
		    N2=subst(Nx,gen_t,tmin+1,false,contextptr)+cst_i*subst(Ny,gen_t,tmin+1,false,contextptr);
		}
		if (tmax!=T){
		  N2=subst(Nx,gen_t,tmax,false,contextptr)+cst_i*subst(Ny,gen_t,tmax,false,contextptr);
		  subt=(subt==_HALFLINE__VECT)?_GROUP__VECT:_HALFLINE__VECT;
		  if (tmax==-T)
		    N1=subst(Nx,gen_t,tmax-1,false,contextptr)+cst_i*subst(Ny,gen_t,tmax-1,false,contextptr);
		}
		resv.push_back(pnt_attrib(gen(makevecteur(N1,N2),subt),attributs,contextptr));
		continue;
	      }
	      if (lieu_geo.is_symb_of_sommet(at_cercle)){
		// FIXME: find arc of circle if tmin/tmax = +/-T
		resv.push_back(pnt_attrib(lieu_geo,attributs,contextptr));
		continue;
	      }
	      resv.push_back(pnt_attrib(lieu_geo,attributs,contextptr));
	    } // end for
	    if (resv.size()==1)
	      return resv.front();
	    return resv; // gen(resv,_SEQ__VECT);
	  } // end if equation2geo2d ...
	}
	if (tmax==T)
	  tmax=gnuplot_tmax;
	if (tmin==-T)
	  tmin=gnuplot_tmin;
	gen gtstep=tstep_defined?tstep:(tmax-tmin)/80;
	gen ntstep(_TSTEP);
	ntstep.subtype=_INT_PLOT;
	return _plotparam(gen(makevecteur(N,symb_equal(gen_t,symb_interval(tmin,tmax)),symb_equal(ntstep,gtstep)),_SEQ__VECT),contextptr);
      }
      if (w.size()<2)
	return gendimerr(contextptr);
      bool is_ligne_brisee=(w[1].type==_VECT);
      w[0]=w[0].evalf(1,contextptr);
      vecteur p;
      if (tmax==T)
	tmax=10;
      if (tmax==-T)
	tmin=-10;
      double tmin_d,tmax_d;
      if (!doublify(tmin,tmax,T,tmin_d,tmax_d,contextptr))
	return gensizeerr(contextptr);
      double tstep_d=tstep_defined?tstep:(tmax_d-tmin_d)/80;
      for (double t=tmin_d;t<=tmax_d;t+=tstep_d){
	if (is_ligne_brisee)
	  w[1]._VECTptr->back()=t;
	else
	  w[1]=t;
	gen tmp=prog(parameter2point(w,contextptr),contextptr);
	if (is_undef(tmp))
	  continue;
	// makes lieu easier if the image is a line intersection
	if (tmp.type==_VECT && tmp._VECTptr->size()==1)
	  tmp=tmp._VECTptr->front();
	p.push_back(remove_at_pnt(tmp));
      }
#ifdef HAVE_SIGNAL_H_OLD
      signal_store=old_signal_store;
#endif
      io_graph(old_io_graph,contextptr);
      return pnt_attrib(gen(p,_GROUP__VECT),attributs,contextptr);
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & e){
#ifdef HAVE_SIGNAL_H_OLD
      signal_store=old_signal_store;
#endif
      io_graph(old_io_graph,contextptr);
      throw(std::runtime_error(e.what()));
    }
#endif
  }
  static const char _lieu_s []="locus";
  static define_unary_function_eval_quoted (__lieu,&giac::_lieu,_lieu_s);
  define_unary_function_ptr5( at_lieu ,alias_at_lieu,&__lieu,_QUOTE_ARGUMENTS,true);

  gen _head(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_STRNG){
      string & s =*args._STRNGptr;
      int l=s.size();
      if (!l)
	return args;
      return string2gen(s.substr(0,1),false);
    }
    if (args.type!=_VECT)
      return args;
    if (args._VECTptr->size())
      return args._VECTptr->front();
    else
      return args;
  }
  static const char _head_s []="head";
  static define_unary_function_eval (__head,&giac::_head,_head_s);
  define_unary_function_ptr5( at_head ,alias_at_head,&__head,0,true);

  gen _tail(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_STRNG){
      string & s =*args._STRNGptr;
      int l=s.size();
      if (!l)
	return args;
      return string2gen(s.substr(1,l-1),false);
    }
    if (args.type!=_VECT)
      return vecteur(0);
    if (args._VECTptr->size()){
      const_iterateur it=args._VECTptr->begin(),itend=args._VECTptr->end();
      return gen(vecteur(it+1,itend),args.subtype);
    }
    else
      return args;
  }
  static const char _tail_s []="tail";
  static define_unary_function_eval (__tail,&giac::_tail,_tail_s);
  define_unary_function_ptr5( at_tail ,alias_at_tail,&__tail,0,true);

  // return the vector of affixes of vertices of a polygonal line
  // If popback is true and the polygonal line is closed
  // the last vertex is removed from the returned vector
  static vecteur sommet(const gen & args,bool popback=true){
    if (args.type==_VECT){
      if (args.subtype==_POINT__VECT)
	return vecteur(1,args);
      if (!args._VECTptr->empty() && args.subtype==0){
	vecteur v(*args._VECTptr);
	gen v0=remove_at_pnt(v[0]);
	if (v0.type==_VECT && v0.subtype!=_POINT__VECT)
	  return sommet(v0,popback);
      }
    }
    gen g=remove_at_pnt(args);
    if (g.is_symb_of_sommet(at_cercle)){
      gen & feuille=g._SYMBptr->feuille;
      if (feuille.type==_VECT && !feuille._VECTptr->empty()){
	g=feuille._VECTptr->front();
	return sommet(g,false);
      }
    }
    if (g.is_symb_of_sommet(at_hyperplan)){
      vecteur P,n;
      if (!hyperplan_normal_point(g,n,P))
	return vecteur(3,gensizeerr(gettext("sommet")));
      vecteur v1,v2;
      if (!normal3d(n,v1,v2))
	return vecteur(3,gensizeerr(gettext("sommet")));
      return makevecteur(P,v1,v2);
    }
    if (g.is_symb_of_sommet(at_hypersphere)){
      gen & feuille=g._SYMBptr->feuille;
      if (feuille.type==_VECT && !feuille._VECTptr->empty())
	return sommet(feuille._VECTptr->front(),false);
    }
    vecteur v(gen2vecteur(g));
    if (ckmatrix(v.front())){ // polyedre
      vecteur res;
      const_iterateur it=v.begin(),itend=v.end();
      for (;it!=itend;++it){
	if (!ckmatrix(*it))
	  return vecteur(1,gensizeerr(gettext("sommet")));
	const_iterateur jt=it->_VECTptr->begin(),jtend=it->_VECTptr->end();
	for (;jt!=jtend;++jt){
	  if (!equalposcomp(res,*jt))
	    res.push_back(*jt);
	}
      }
      return res;
    }
    if (popback && v.size()>1 && v.back()==v.front())
      v.pop_back();
    return v;
  }

  gen _sommets(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen g=sommet(args,true);
    if (is_undef(g))
      return g;
    bool tmp=show_point(contextptr);
    show_point(false,contextptr);
    g=apply(g,_point,contextptr);
    show_point(tmp,contextptr);
    return g;
  }
  static const char _sommets_s []="vertices";
  static define_unary_function_eval (__sommets,&giac::_sommets,_sommets_s);
  define_unary_function_ptr5( at_sommets ,alias_at_sommets,&__sommets,0,true);

  gen _sommets_abca(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen g=sommet(args,false);
    if (is_undef(g))
      return g;
    bool tmp=show_point(contextptr);
    show_point(false,contextptr);
    g=apply(g,_point,contextptr);
    show_point(tmp,contextptr);
    return g;
  }
  static const char _sommets_abca_s []="vertices_abca";
  static define_unary_function_eval (__sommets_abca,&giac::_sommets_abca,_sommets_abca_s);
  define_unary_function_ptr5( at_sommets_abca ,alias_at_sommets_abca,&__sommets_abca,0,true);

  static const char _sommets_abc_s []="vertices_abc";
  static define_unary_function_eval (__sommets_abc,&giac::_sommets,_sommets_abc_s);
  define_unary_function_ptr5( at_sommets_abc ,alias_at_sommets_abc,&__sommets_abc,0,true);

  static gen symetriepoint(const gen & a,const gen & b,GIAC_CONTEXT){
    gen res= 2*a-b;
    res.subtype=_POINT__VECT;
    return res;
  }

  static gen symetrieplan(const gen & a,const gen & b,GIAC_CONTEXT){
    if (a.type==_VECT && a._VECTptr->size()==3){
      vecteur & w(*a._VECTptr);
      vecteur n(*w[0]._VECTptr),P(*w[1]._VECTptr); 
      gen n2(w[2]);
      gen res= b-2*scalar_product(n,b-P,contextptr)/n2*n;
      if (is_undef(res)) return res;
      res.subtype=_POINT__VECT;
      return res;
    }
    return gensizeerr(contextptr);
  }

  gen apply3d(const gen & e1, const gen & e2,const context * contextptr, gen (* f) (const gen &, const gen &,const context *) ){
    if (is_undef(e2))
      return e2;
    if (e2.type!=_VECT || e2.subtype==_POINT__VECT)
      return f(e1,e2,contextptr);
    const_iterateur it=e2._VECTptr->begin(),itend=e2._VECTptr->end();
    vecteur v;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=apply3d(e1,*it,contextptr,f);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return gen(v,e2.subtype);
  }

  gen curve_surface_apply(const gen & elem,const gen & b,gen (* func) (const gen &, const gen &,const context *),GIAC_CONTEXT){
    if (func && 
	(b.is_symb_of_sommet(at_curve) || b.is_symb_of_sommet(at_hypersurface))
	&& b._SYMBptr->feuille.type==_VECT && b._SYMBptr->feuille._VECTptr->size()>=2){
      // f = vect[ pnt,var,xmin,xmax ]
      gen f= b._SYMBptr->feuille._VECTptr->front();
      gen g=(*b._SYMBptr->feuille._VECTptr)[1];
      if (f.type==_VECT && !f._VECTptr->empty()){
	vecteur fv=*f._VECTptr;
	fv[0]=func(elem,fv[0],contextptr);
	if (fv.size()>4)
	  fv[4]=apply3d(elem,fv[4],contextptr,func);
	f=gen(fv,f.subtype);
      }
      if (b.is_symb_of_sommet(at_curve)){
	g= apply3d(elem,g,contextptr,func);
	g=symb_curve(f,g);
      }
      else {
	if (b._SYMBptr->feuille._VECTptr->size()>=3){
	  gen vars=(*b._SYMBptr->feuille._VECTptr)[2];
	  gen fvars=func(elem,vars,contextptr);
	  // subst(g,fvars,vars); invert affine function
	  if (vars.type==_VECT){
	    gen difffvars=derive(fvars,*vars._VECTptr,contextptr);
	    if (is_undef(difffvars) || difffvars.type!=_VECT)
	      return difffvars;
	    vecteur dfunc=*difffvars._VECTptr;
	    if (is_zero(derive(dfunc,vars,contextptr))){
	      matrice m(minv(dfunc,contextptr));
	      if (is_undef(m)) return m;
	      gen y_minus_b=vars-func(elem,vecteur(vars._VECTptr->size()),contextptr);
	      fvars=m*y_minus_b;
	      g=subst(g,vars,fvars,false,contextptr);
	      g=hypersurface(f,g,vars);
	    }
	    else
	      g=hypersurface(f,undef,vars);
	  }
	  else
	    g=hypersurface(f,undef,vars);
	}
	else
	  g=hypersurface(f,undef,undef);
      }
      return symb_pnt(g,default_color(contextptr),contextptr);
    }
    return gensizeerr(contextptr);
  }

  // image of b wrt to the line defined by vector of affixe w and point w0
  static gen symetrie_droite(const gen & w,const gen & w0,const gen & b,GIAC_CONTEXT){
    if (b.type==_VECT){
      const_iterateur it=b._VECTptr->begin(),itend=b._VECTptr->end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it)
	res.push_back(symetrie_droite(w,w0,*it,contextptr));
      gen b1=gen(res,b.subtype);
      return b1;
    }
    return b-plus_two*w*rdiv(scalar_product(b-w0,w,contextptr),w.squarenorm(contextptr),contextptr);
  }

  static gen rotation(const gen & centre,const gen & angle,const gen & M,GIAC_CONTEXT){
    if (M.type==_CPLX && centre.type==_CPLX){
      gen Mx,My; reim(M,Mx,My,contextptr);
      gen Cx,Cy; reim(centre,Cx,Cy,contextptr);
      gen ca=cos(angle,contextptr);
      gen sa=sin(angle,contextptr);
      Mx -= Cx; My -= Cy;
      // (ca+i*sa)*(Mx+i*My)=ca*Mx-sa*My+i*(sa*Mx+ca*My)
      gen x=ca*Mx-sa*My,y=sa*Mx+ca*My;
      x += Cx;
      y += Cy;
      return makecomplex(x,y);
    }
    return centre+exp(cst_i*degtorad(angle,contextptr),contextptr)*(M-centre);
  }

  // 2-d r(centre,angle,M) or 3-d r(line,angle,M)
  static gen rotation(const vecteur & v,int s,GIAC_CONTEXT){
    if (s==3){
      gen centre=remove_at_pnt(v[0]);
      gen angle=v[1];
      gen b=v[2];
      if (b.type==_VECT){
	const_iterateur it=b._VECTptr->begin(),itend=b._VECTptr->end();
	vecteur res;
	res.reserve(itend-it);
	for (;it!=itend;++it){
	  res.push_back(rotation(makevecteur(centre,angle,*it),s,contextptr));
	}
	return gen(res,b.subtype);
      }
      b=remove_at_pnt(b);
      if (b.type==_VECT && b.subtype==_VECTOR__VECT && b._VECTptr->size()==2)
	return _vector(gen(makevecteur(rotation(makevecteur(centre,angle,b._VECTptr->front()),s,contextptr),rotation(makevecteur(centre,angle,b._VECTptr->back()),s,contextptr)),_SEQ__VECT),contextptr);
      if ( centre.type==_SYMB && (centre._SYMBptr->sommet==at_cercle) )
	return gensizeerr(contextptr);
      if (centre.is_symb_of_sommet(at_hyperplan)){
	vecteur n,P;
	if (!hyperplan_normal_point(centre,n,P))
	  return gensizeerr(contextptr);
	return similitude3d(makevecteur(P,P+n),angle,1,b,-1,contextptr);
      }
      if (centre.type!=_VECT) // 2-d rotation
	return symb_pnt(rotation(centre,angle,b,contextptr),default_color(contextptr),contextptr);
      return similitude3d(*centre._VECTptr,angle,1,b,1,contextptr);
    }
    if (s==2){
      vecteur w(v.begin(),v.begin()+s);
      w.push_back(x__IDNT_e);
      return symb_program(x__IDNT_e,zero,symbolic(at_rotation,gen(w,_SEQ__VECT)),contextptr);
    }
    return gendimerr(contextptr);
  }
  static gen symetrie(const gen & aa,const gen & bb,GIAC_CONTEXT){
    if (bb.type==_VECT)
      return apply2nd(aa,bb,contextptr,symetrie);
    gen a=remove_at_pnt(aa);
    if ( (a.type==_SYMB) && (a._SYMBptr->sommet==at_cercle) )
      return gensizeerr(contextptr);
    gen b=remove_at_pnt(bb);
    if (b.type==_VECT && b.subtype==_VECTOR__VECT && b._VECTptr->size()==2)
      return _vector(gen(makevecteur(symetrie(aa,b._VECTptr->front(),contextptr),symetrie(aa,b._VECTptr->back(),contextptr)),_SEQ__VECT),contextptr);
    if (a.is_symb_of_sommet(at_hyperplan)){ // symmetry of b wrt plan
      vecteur n,P;
      if (!hyperplan_normal_point(a,n,P))
	return gensizeerr(contextptr);
      gen n2(dotvecteur(n,n));
      gen elem=makevecteur(n,P,n2);
      if (b.type==_VECT)
	return symb_pnt(apply3d(elem,b,contextptr,symetrieplan),default_color(contextptr),contextptr);
      if (b.is_symb_of_sommet(at_hyperplan)){ // hyperplan wrt plan
	vecteur nb,Pb;
	if (!hyperplan_normal_point(b,nb,Pb))
	  return gensizeerr(contextptr);
	gen nbi=nb-dotvecteur(nb,n)/n2*n;
	gen Pbi=Pb-scalar_product(n,Pb-P,contextptr)/n2*n;
	if (is_undef(Pbi)) return Pbi;
	return _plan(makevecteur(nbi,Pbi),contextptr);
      }
      if (b.is_symb_of_sommet(at_hypersphere)){ // hypersphere wrt plan
	gen c,r;
	if (!centre_rayon(b,c,r,false,contextptr))
	  return gensizeerr(contextptr);
	return _sphere(makevecteur(c-scalar_product(n,c-P,contextptr)/n2*n,r),contextptr);
      }
      return curve_surface_apply(elem,b,symetrieplan,contextptr);
    }
    vecteur v;
    if (a.type!=_VECT) // 2-d point symmetry
      return symb_pnt(plus_two*a-b,default_color(contextptr),contextptr);
    v=*a._VECTptr;
    if (a.subtype==_POINT__VECT || v.size()==3){ // 3-d point symmetry
      if (b.type==_VECT){ // point3d, line, polygon or polyedre
	gen res=apply3d(a,b,contextptr,symetriepoint);
	return symb_pnt(res,default_color(contextptr),contextptr);
      }
      if (b.is_symb_of_sommet(at_hyperplan)){ // plan wrt point
	vecteur n,P;
	if (!hyperplan_normal_point(b,n,P))
	  return gensizeerr(contextptr);
	return _plan(makevecteur(n,2*a-P),contextptr);
      }
      if (b.is_symb_of_sommet(at_hypersphere)){ // sphere wrt point
	gen centre,rayon;
	if (!centre_rayon(b,centre,rayon,false,contextptr))
	  return gensizeerr(contextptr);
	return _sphere(makevecteur(2*a-centre,rayon),contextptr);
      }
      return curve_surface_apply(a,b,symetriepoint,contextptr);
    } // end 3-d point symmetry 
    if (v.size()!=2)
      return gensizeerr(contextptr);
    // line symmetry
    if (v[0].type==_VECT){ // 3-d line symmetry -> rotation of angle pi
      return rotation(makevecteur(aa,cst_pi,bb),3,contextptr);
    }
    // 2-d line symmetry
    gen w=cst_i*(v[1]-v[0]);
    if (b.is_symb_of_sommet(at_cercle)){
      gen bf=b._SYMBptr->feuille;
      if (bf.type!=_VECT || bf._VECTptr->size()<2)
	return gensizeerr(contextptr);
      vecteur vb=*bf._VECTptr;
      vb[0]=symetrie_droite(w,v[0],vb[0],contextptr);
      bf=gen(vb,bf.subtype);
      b=symbolic(at_cercle,bf);
      return symb_pnt(b,default_color(contextptr),contextptr);
    }
    if (b.is_symb_of_sommet(at_curve)){
      gen bf=b._SYMBptr->feuille;
      if (bf.type!=_VECT || bf._VECTptr->size()<2)
	return gensizeerr(contextptr);
      gen bf1=bf._VECTptr->front(),bf2=(*bf._VECTptr)[1];
      if (bf1.type==_VECT && !bf1._VECTptr->empty()){
	vecteur bf1v=*bf1._VECTptr;
	bf1v[0]=symetrie_droite(w,v[0],bf1v[0],contextptr);
	bf1=gen(bf1v,bf1.subtype);
      }
      if (bf2.type==_VECT){
	const_iterateur it=bf2._VECTptr->begin(),itend=bf2._VECTptr->end();
	vecteur res;
	res.reserve(itend-it);
	for (;it!=itend;++it){
	  res.push_back(symetrie_droite(w,v[0],*it,contextptr));
	}
	bf2=gen(res,bf2.subtype);
	bf=gen(makevecteur(bf1,bf2),bf.subtype);
	return symb_pnt(symbolic(at_curve,bf),default_color(contextptr),contextptr);
      }
    }
    return symb_pnt(symetrie_droite(w,v[0],b,contextptr),default_color(contextptr),contextptr);
  }

  vecteur seq2vecteur(const gen & g){
    if (g.type==_VECT && g.subtype==_SEQ__VECT)
      return *g._VECTptr;
    else
      return vecteur(1,g);
  }
  static gen symetrie(const vecteur & v,int s,GIAC_CONTEXT){
    if (s==2)
      return symetrie(v[0],v[1],contextptr);
    if (s==1){
      return symb_program(x__IDNT_e,zero,symbolic(at_symetrie,gen(makevecteur(v[0],x__IDNT_e),_SEQ__VECT)),contextptr);
    }
    return gentypeerr(contextptr);
  }
  gen _symetrie(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    return put_attributs(symetrie(v,s,contextptr),attributs,contextptr);
  }
  static const char _symetrie_s []="reflection";
  static define_unary_function_eval (__symetrie,&giac::_symetrie,_symetrie_s);
  define_unary_function_ptr5( at_symetrie ,alias_at_symetrie,&__symetrie,0,true);

  gen _rotation(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    return put_attributs(rotation(v,s,contextptr),attributs,contextptr);
  }
  static const char _rotation_s []="rotation";
  static define_unary_function_eval (__rotation,&giac::_rotation,_rotation_s);
  define_unary_function_ptr5( at_rotation ,alias_at_rotation,&__rotation,0,true);

  static gen projectionpoint(const gen & aa,const gen & bb,GIAC_CONTEXT){
    gen b=remove_at_pnt(bb);
    if (b.type==_VECT && b.subtype==_VECTOR__VECT && b._VECTptr->size()==2)
      return _vector(gen(makevecteur(projectionpoint(aa,b._VECTptr->front(),contextptr),projectionpoint(aa,b._VECTptr->back(),contextptr)),_SEQ__VECT),contextptr);
    if (aa.is_symb_of_sommet(at_hyperplan)){
      vecteur n,P;
      if (!hyperplan_normal_point(aa,n,P))
	return gensizeerr(contextptr);
      gen n2(dotvecteur(n,n));
      // projection of point and line on hyperplan
      if (b.type==_VECT && b._VECTptr->size()==2){ // line
	// project both points and return the line
	gen & A=b._VECTptr->front();
	gen & B=b._VECTptr->back();
	gen AP=A+scalar_product(P-A,n,contextptr)/n2*n;
	gen BP=B+scalar_product(P-B,n,contextptr)/n2*n;
	if (is_undef(AP)) return AP;
	if (is_undef(BP)) return BP;
	return symb_pnt(gen(makevecteur(AP,BP),b.subtype),contextptr);
      }
      // point: b -> M=b+tn such that MP is orthogonal to n
      // hence (b-P+tn).n=0 -> t=(P-b).n/n.n
      if (b.type!=_VECT)
	return gensizeerr(contextptr);
      gen tmp=b+scalar_product(P-b,n,contextptr)/n2*n;
      if (is_undef(tmp)) return tmp;
      return symb_pnt(do_point3d(tmp),contextptr);
    }
    if (aa.type==_VECT && aa._VECTptr->size()==2){ 
      // FIXME: for circle!
      // projection of a point b over a line AB
      gen & A=aa._VECTptr->front();
      gen & B=aa._VECTptr->back();
      gen v(B-A);
      // Find M such that M=A+t*(B-A) and (M-b).(B-A)=0
      // Hence (A-b).(B-A)+t(B-A)^2=0 -> t=(b-A).(B-A)/(B-A)^2
      gen t=scalar_product(b-A,v,contextptr)/scalar_product(v,v,contextptr);
      if (is_undef(t)) return t;
      return symb_pnt(A+t*v,default_color(contextptr),contextptr);
    }
    vecteur v;
    v.push_back(aa);
    gen tmpaa=projection(aa,b,contextptr);
    if (is_undef(tmpaa)) return tmpaa;
    v.push_back(tmpaa);
    return parameter2point(v,contextptr);
  }  
  
  static gen projection(const vecteur & v,int s,GIAC_CONTEXT){
    if (s==2){
      gen a=remove_at_pnt(v[0]);
      gen b=v[1];
      if (b.type==_VECT)
	return apply2nd(a,b,contextptr,projectionpoint);
      else 
	return projectionpoint(a,b,contextptr);
    }
    if (s==1)
      return symb_program(x__IDNT_e,zero,symbolic(at_projection,gen(makevecteur(v[0],x__IDNT_e),_SEQ__VECT)),contextptr);
    return gentypeerr(contextptr);
  }
  gen _projection(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    return put_attributs(projection(v,s,contextptr),attributs,contextptr);
  }
  static const char _projection_s []="projection";
  static define_unary_function_eval (__projection,&giac::_projection,_projection_s);
  define_unary_function_ptr5( at_projection ,alias_at_projection,&__projection,0,true);

  // h(centre,rapport,M)
  static gen homothetie(const vecteur & v,int s,GIAC_CONTEXT){
    if (s==3){
      gen centre=remove_at_pnt(v[0]);
      if ( centre.type==_SYMB && centre._SYMBptr->sommet==at_cercle )
	return gensizeerr(contextptr);
      gen rapport=v[1];
      gen b=v[2];
      if (b.type==_VECT){
	const_iterateur it=b._VECTptr->begin(),itend=b._VECTptr->end();
	vecteur res;
	res.reserve(itend-it);
	for (;it!=itend;++it)
	  res.push_back(homothetie(makevecteur(centre,rapport,*it),s,contextptr));
	return gen(res,_GROUP__VECT);
      }
      b=remove_at_pnt(b);
      if (b.type==_VECT && b.subtype==_VECTOR__VECT && b._VECTptr->size()==2)
	return _vector(gen(makevecteur(homothetie(makevecteur(centre,rapport,b._VECTptr->front()),s,contextptr),homothetie(makevecteur(centre,rapport,b._VECTptr->back()),s,contextptr)),_SEQ__VECT),contextptr);
      if (b.is_symb_of_sommet(at_hyperplan)){
	vecteur n,P;
	if (!hyperplan_normal_point(b,n,P))
	  return gensizeerr(contextptr);
	return _plan(makevecteur(n,centre+rapport*(P-centre)),contextptr);
      }
      if (b.is_symb_of_sommet(at_hypersphere)){
	gen c,r;
	if (!centre_rayon(b,c,r,false,contextptr))
	  return gensizeerr(contextptr);
	return _sphere(makevecteur(centre+rapport*(c-centre),rapport*r),contextptr);
      }
      return symb_pnt(centre+rapport*(b-centre),default_color(contextptr),contextptr);
    }
    if (s==2){
      vecteur w=makevecteur(v[0],v[1],x__IDNT_e);
      return symb_program(x__IDNT_e,zero,symbolic(at_homothetie,gen(w,_SEQ__VECT)),contextptr);
    }
    return gentypeerr(contextptr);
  }
  gen _homothetie(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    return put_attributs(homothetie(v,s,contextptr),attributs,contextptr);
  }
  static const char _homothetie_s []="homothety";
  static define_unary_function_eval (__homothetie,&giac::_homothetie,_homothetie_s);
  define_unary_function_ptr5( at_homothetie ,alias_at_homothetie,&__homothetie,0,true);

  // renvoie 2 si tous les points sont confondus
  // renvoie 1 si tous les points sont alignes et 0 sinon 
  gen _est_aligne(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen a;
    if (args.type!=_VECT) {
      a=remove_at_pnt(args);
      if (a.type==_VECT) return gensizeerr(contextptr);
      return 2;
    }
    int s=args._VECTptr->size();
    vecteur v(*args._VECTptr);
    if (s==1) {
      a=remove_at_pnt(v[0]);
      if (a.type==_VECT)
	return gensizeerr(contextptr);
      return 2;
    }
    if (s>=2){
      a=remove_at_pnt(v[0]);
      gen b=remove_at_pnt(v[1]);
      bool res;
      int i=2;
      while (a==b && i<s){
	b=remove_at_pnt(v[i]);
	i++;
      }
      //a==b tous les points sonts confondus sinon on a a!=b
      if (i==s){
	if (a==b) return(2);
	return(1);
      }
      //a!=b
      for (int j=i;j<s;j++){
	gen c=remove_at_pnt(v[j]);
	res=(est_aligne(a,b,c,contextptr))!=0;
	if (res==0) return 0; 
      }
      return res;
    }
    return symbolic(at_est_aligne,args);
  }
  static const char _est_aligne_s []="is_collinear";
  static define_unary_function_eval (__est_aligne,&giac::_est_aligne,_est_aligne_s);
  define_unary_function_ptr5( at_est_aligne ,alias_at_est_aligne,&__est_aligne,0,true);

  bool est_coplanaire(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT){
    if (a.type!=_VECT)
      return false; // setsizeerr(gettext("3-d instruction"));    
    gen n1(b-a),n2(c-a),n3(d-a);
    return is_zero(mdet(makevecteur(n1,n2,n3),contextptr));
  }

  static void add_if_not_colinear(vecteur & v,const gen & p,GIAC_CONTEXT){
    int s=v.size();
    bool add=true;
    if (s==1)
      add=!is_zero(v.front()-p);
    else {
      if (s==2)
	add=!est_aligne(v[0],v[1],p,contextptr);
    }
    if (add)
      v.push_back(p);
  }
  gen _est_coplanaire(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & w=*args._VECTptr,v;
    int s=w.size();
    for (int j=0;j<s;++j){
      gen tmp=remove_at_pnt(w[j]);
      if (tmp.type==_VECT && tmp._VECTptr->size()==2){
	add_if_not_colinear(v,tmp._VECTptr->front(),contextptr);
	add_if_not_colinear(v,tmp._VECTptr->back(),contextptr);
      }
      else {
	add_if_not_colinear(v,tmp,contextptr);
      }
    }
    int vs=v.size();
    for(int i=3;i<vs;++i){
      if (!est_coplanaire(v[0],v[1],v[2],v[i],contextptr))
	return false;
    }
    return true;
  }
  static const char _est_coplanaire_s []="is_coplanar";
  static define_unary_function_eval (__est_coplanaire,&giac::_est_coplanaire,_est_coplanaire_s);
  define_unary_function_ptr5( at_est_coplanaire ,alias_at_est_coplanaire,&__est_coplanaire,0,true);

  static gen gen2complex(const gen & a){
    if (a.type!=_VECT)
      return a;
    if (a._VECTptr->size()!=2)
      return gensizeerr(gettext("gen2complex"));
    return a._VECTptr->front()+cst_i*a._VECTptr->back();
  }

  bool est_cocyclique(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT){
    gen ab=b-a,ac=c-a,ad=d-a;
    if (is_zero(ab) || is_zero(ac) || is_zero(ad) )
      return true;
    if (a.type==_VECT && a._VECTptr->size()==3){
      if (!est_coplanaire(a,b,c,d,contextptr))
	return false;
      return (est_aligne(a+ab/abs_norm2(ab,contextptr),a+ac/abs_norm2(ac,contextptr),a+ad/abs_norm2(ad,contextptr),contextptr))!=0;
    }
    gen A(gen2complex(a)),B(gen2complex(b)),C(gen2complex(c)),D(gen2complex(d));
    gen e(im((B-A)*(C-D)*conj(C-A,contextptr)*conj(B-D,contextptr),contextptr));
    return is_zero(simplify(e,contextptr));
  }
  // renvoie 1 si tous les points sont cocycliques et 0 sinon
  gen _est_cocyclique(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return 3;
    int s=args._VECTptr->size();
    vecteur & v=*args._VECTptr ;
    gen a(v[0]),b(undef),c(undef);
    for (int i=1;i<s;++i){
      if (is_undef(b)){
	if (!is_zero(v[0]-v[i]))
	  b=v[i];
      }
      else {
	if (is_zero(v[i]-b) || is_zero(v[i]-a))
	  continue;
	if (est_aligne(a,b,v[i],contextptr))
	  return 0;
	if (is_undef(c))
	  c=v[i];
	else {
	  if (!est_cocyclique(a,b,c,v[i],contextptr))
	    return 0;
	}
      }
    }
    return 1;
  }
  static const char _est_cocyclique_s []="is_concyclic";
  static define_unary_function_eval (__est_cocyclique,&giac::_est_cocyclique,_est_cocyclique_s);
  define_unary_function_ptr5( at_est_cocyclique ,alias_at_est_cocyclique,&__est_cocyclique,0,true);

  gen _est_parallele(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ((args.type==_VECT) && (args._VECTptr->size()==2)){
      vecteur v(*args._VECTptr);
      gen a=remove_at_pnt(v[0]),b=remove_at_pnt(v[1]);
      bool av=a.type==_VECT && a._VECTptr->size()==2;
      bool bv=b.type==_VECT && b._VECTptr->size()==2;
      if (a.is_symb_of_sommet(at_hyperplan)){
	vecteur n(hyperplan_normal(a));
	if (b.is_symb_of_sommet(at_hyperplan))
	  return est_parallele_vecteur(n,hyperplan_normal(b),contextptr);
	if (bv)
	  return is_zero(simplify(scalar_product(n,b[0]-b[1],contextptr),contextptr));
      }
      if (b.is_symb_of_sommet(at_hyperplan) && av)
	return is_zero(simplify(scalar_product(hyperplan_normal(b),a[0]-a[1],contextptr),contextptr));
      if ( !av || !bv)
	return gensizeerr(contextptr);
      return est_parallele(a[0]-a[1],b[0]-b[1],contextptr);
    }
    return symbolic(at_est_parallele,args);
  }
  static const char _est_parallele_s []="is_parallel";
  static define_unary_function_eval (__est_parallele,&giac::_est_parallele,_est_parallele_s);
  define_unary_function_ptr5( at_est_parallele ,alias_at_est_parallele,&__est_parallele,0,true);

  bool est_perpendiculaire(const gen & a,const gen & b,GIAC_CONTEXT){
    gen d;
    if (a.type==_VECT && b.type==_VECT)
      d=dotvecteur(*a._VECTptr,*b._VECTptr);
    else
      d=re(a*conj(b,contextptr),contextptr);
    return is_zero(simplify(d,contextptr));
  }

  // check if a belongs to b, a must be a complex, b a line or circle or curve
  int est_element(const gen & a_orig,const gen & b_orig,GIAC_CONTEXT){
    gen a=remove_at_pnt(a_orig),b=remove_at_pnt(b_orig);
    if (b.type==_VECT && b.subtype<=_SET__VECT)
      return equalposcomp(*b._VECTptr,a);
    if (b.is_symb_of_sommet(at_cercle)){
      // check orthogonality with diameter
      gen diam=remove_at_pnt(b._SYMBptr->feuille._VECTptr->front());
      if (diam.type!=_VECT)
	return 0;
      gen c1=remove_at_pnt(diam._VECTptr->front());
      gen c2=remove_at_pnt(diam._VECTptr->back());
      return est_perpendiculaire(a-c1,a-c2,contextptr);
    }
    if (b.is_symb_of_sommet(at_hypersphere)){
      gen c,r;
      if (!centre_rayon(b,c,r,false,contextptr))
	return 0;
      return is_zero(simplify(pow(a-c,2)-pow(r,2),contextptr));
    }
    if (b.is_symb_of_sommet(at_hyperplan)){
      vecteur n,P;
      if (!hyperplan_normal_point(b,n,P))
	return 0;// gensizeerr(contextptr);
      return is_zero(simplify(scalar_product(a-P,n,contextptr),contextptr));
    }
    if (b.is_symb_of_sommet(at_curve)){
      gen t;
      return on(b,a,t,contextptr);
    }
    if (b.type==_VECT && b._VECTptr->size()==2){
      gen c1=remove_at_pnt(b._VECTptr->front());
      gen c2=remove_at_pnt(b._VECTptr->back());      
      return est_aligne(c1,c2,a,contextptr);
    }
    return 0; // FIXME implement est_element of a polygon 
  }
  gen _est_element(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ((args.type==_VECT) && (args._VECTptr->size()==2)){
      vecteur v(*args._VECTptr);
      gen a=v[0],b=v[1];
      return est_element(a,b,contextptr);
    }
    return symbolic(at_est_element,args);
  }
  static const char _est_element_s []="is_element";
  static define_unary_function_eval (__est_element,&giac::_est_element,_est_element_s);
  define_unary_function_ptr5( at_est_element ,alias_at_est_element,&__est_element,0,true);

  gen est_dans(const gen & a_,const gen &b_,GIAC_CONTEXT){
    gen b=remove_at_pnt(b_);
    gen M=remove_at_pnt(a_);
    if (M.type<=_CPLX || M.type==_SYMB || M.type==_IDNT || M.type==_FLOAT_ || M.type==_FRAC){
      if (b.is_symb_of_sommet(at_cercle)){
	gen c,r;
	centre_rayon(b,c,r,false,contextptr);
	gen Mc=(c-M).squarenorm(contextptr),r2=r.squarenorm(contextptr);
	return ck_is_greater(r2,Mc,contextptr);
      }
      if (b.type==_VECT && b._VECTptr->size()>=3 && is_zero(b._VECTptr->front()-b._VECTptr->back())){
	// point in a polygon?
	int n=0; // n is the number of intersections of halfline M direction 1 with polygon
	for (unsigned j=1;j<b._VECTptr->size();++j){
	  gen A=(*b._VECTptr)[j-1];
	  gen B=(*b._VECTptr)[j];
	  gen Ax,Ay,Bx,By,Mx,My;
	  reim(A,Ax,Ay,contextptr);
	  reim(B,Bx,By,contextptr);
	  reim(M,Mx,My,contextptr);
	  if (is_zero(recursive_normal(Ay-By,contextptr)))
	    continue;
	  if (ck_is_greater(Ay,By,contextptr)){
	    swapgen(Ax,Bx);
	    swapgen(Ay,By);
	  }
	  if (ck_is_greater(By,My,contextptr) && ck_is_strictly_greater(My,Ay,contextptr)){
	    gen t=(My-Ay)/(By-Ay);
	    gen alpha=recursive_normal((Ax-Mx)+t*(Bx-Ax),contextptr);
	    if (ck_is_greater(alpha,0,contextptr))
	      ++n;
	  }
	}
	return n%2;
      }
    }
    return gensizeerr(gettext("Not implemented or bad argument type/value"));
  }
  gen _est_dans(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ((args.type==_VECT) && (args._VECTptr->size()==2)){
      vecteur v(*args._VECTptr);
      gen a=v[0],b=v[1];
      return est_dans(a,b,contextptr);
    }
    return gensizeerr(contextptr);
  }
  static const char _est_dans_s []="is_inside";
  static define_unary_function_eval (__est_dans,&giac::_est_dans,_est_dans_s);
  define_unary_function_ptr5( at_est_dans ,alias_at_est_dans,&__est_dans,0,true);

  // adjust arguments and return true if b is an angle between a and c
  static bool arg_between(const gen & a,const gen &b,const gen & c,GIAC_CONTEXT){
    gen C=c;
    if (is_greater(a,c,contextptr))
      C=c+cst_two_pi;
    gen B=b;
    if (is_greater(a,b,contextptr))
      B=b+cst_two_pi;
    return is_greater(C,B,contextptr);
  }

  static gen inversion(const gen & centre,const gen & rapport,const gen & argument,GIAC_CONTEXT){
    gen b=remove_at_pnt(argument);
    // special care must be taken for circles and segments
    if (b.type==_VECT && b.subtype!=_POINT__VECT){
      if (b._VECTptr->size()!=2)
	return gensizeerr(gettext("not implemented"));
      // inversion of a line
      gen c=b._VECTptr->front(),d=b._VECTptr->back();
      if (est_aligne(centre,c,d,contextptr)){
	if (b.subtype==_LINE__VECT)
	  return b;
	return gen(makevecteur(inversion(centre,rapport,c,contextptr),inversion(centre,rapport,d,contextptr)),b.subtype);
      }
      // find the image of the projection of the center of inversion
      // the image of the line is the circle of diameter centre,image
      gen t=projection(c,d,centre,contextptr);
      if (is_undef(t)) return t;
      gen p=c+t*(d-c);
      gen image=inversion(centre,rapport,p,contextptr);
      if (is_undef(image)) return image;
      if (b.subtype==_LINE__VECT)
	return remove_at_pnt(_cercle(gen(makevecteur((centre+image)/2,(image-centre)/2),_SEQ__VECT),contextptr));
      p=(centre+image)/2; // center of circle
      c=inversion(centre,rapport,c,contextptr);
      c=arg(c-p,contextptr);
      d=inversion(centre,rapport,d,contextptr);
      d=arg(d-p,contextptr);
      gen e=arg(centre-p,contextptr),a1,a2;
      // Segment: choose c,d or d,c (should not contain e), halfline: choose c,e or e,c (should contain d)
      if (b.subtype==_HALFLINE__VECT){
	if (arg_between(c,d,e,contextptr)){
	  a1=c; a2=e;
	}
	else {
	  a1=e; a2=c;
	}
      }
      else {
	if (arg_between(c,e,d,contextptr)){
	  a1=d; a2=c;
	}
	else {
	  a1=c; a2=d;
	}
      }
      return remove_at_pnt(_cercle(gen(makevecteur(p,abs(image-centre,contextptr)/2,a1,a2),_SEQ__VECT),contextptr));
    }
    if ( (b.type!=_SYMB) || (!equalposcomp(plot_sommets,b._SYMBptr->sommet)) ){
      gen centre_b=b-centre;
      return centre+rapport/abs_norm2(centre_b,contextptr)*centre_b; 
      // return centre+rapport*inv(conj(b-centre,contextptr));
    }
    if (b._SYMBptr->sommet!=at_cercle)
      return gensizeerr(gettext("not implemented"));
    // inversion of a circle
    // if it contains the center of inversion it's a line, otherw. a circle
    gen c,r;
    if (!centre_rayon(b,c,r,true,contextptr))
      return gensizeerr(contextptr);
    if (is_zero(evalf_double(distance2pp(c,centre,contextptr)-r*r,1,contextptr))){
      // c-centre is the normal direction
      gen n(c-centre);
      n=im(n,contextptr)-cst_i*re(n,contextptr);
      // the line passes at the image of 2*centre-c
      gen pied(inversion(centre,rapport,plus_two*c-centre,contextptr));
      return remove_at_pnt(_droite(makevecteur(pied,pied+n),contextptr));
    }
    else {
      gen d(c-centre);
      r=rdiv(r,abs_norm(d,contextptr),contextptr)*d;
      gen d1(inversion(centre,rapport,c+r,contextptr));
      gen d2(inversion(centre,rapport,c-r,contextptr));
      if (is_undef(d2)) return d2;
      return remove_at_pnt(_cercle(makevecteur(d1,d2),contextptr));
    }
  }

  gen inversion(const vecteur & v,int s,GIAC_CONTEXT){
    if (s==3){
      gen centre=remove_at_pnt(v[0]);
      gen rapport=v[1];
      gen b=v[2];
      if ( (centre.type==_VECT && centre.subtype!=_POINT__VECT) || (centre.type==_SYMB && centre._SYMBptr->sommet==at_cercle) )
	return gensizeerr(contextptr);
      if (b.type==_VECT){
	const_iterateur it=b._VECTptr->begin(),itend=b._VECTptr->end();
	vecteur res;
	res.reserve(itend-it);
	for (;it!=itend;++it){
	  res.push_back(inversion(centre,rapport,*it,contextptr));
	}
	return gen(res,_GROUP__VECT);
      }
      return symb_pnt(inversion(centre,rapport,b,contextptr),default_color(contextptr),contextptr);
    }
    if (s==2){
      return symb_program(x__IDNT_e,zero,symbolic(at_inversion,gen(makevecteur(v[0],v[1],x__IDNT_e),_SEQ__VECT)),contextptr);      
    }
    return gentypeerr(contextptr);
  }
  gen _inversion(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    return put_attributs(inversion(v,s,contextptr),attributs,contextptr);
  }
  static const char _inversion_s []="inversion";
  static define_unary_function_eval (__inversion,&giac::_inversion,_inversion_s);
  define_unary_function_ptr5( at_inversion ,alias_at_inversion,&__inversion,0,true);

  // s(centre,rapport,angle,M)
  static gen similitude(const vecteur & v,int s,GIAC_CONTEXT){
    if (s==4){
      gen centre=remove_at_pnt(v[0]);
      if ( centre.type==_SYMB && centre._SYMBptr->sommet==at_cercle )
	return gensizeerr(contextptr);
      gen rapport=v[1];
      gen angle=v[2];
      gen b=v[3];
      if (b.type==_VECT){
	const_iterateur it=b._VECTptr->begin(),itend=b._VECTptr->end();
	vecteur res;
	res.reserve(itend-it);
	for (;it!=itend;++it){
	  res.push_back(similitude(makevecteur(centre,rapport,angle,*it),s,contextptr));
	}
	return gen(res,_GROUP__VECT);
      }
      if (centre.is_symb_of_sommet(at_hyperplan)){
	vecteur n,P;
	if (!hyperplan_normal_point(centre,n,P))
	  return gensizeerr(contextptr);
	return similitude3d(makevecteur(P,P+n),angle,rapport,b,-1,contextptr);
      }
      b=remove_at_pnt(b);
      if (b.type==_VECT && b.subtype==_VECTOR__VECT && b._VECTptr->size()==2)
	return _vector(gen(makevecteur(similitude(makevecteur(centre,rapport,angle,b._VECTptr->front()),s,contextptr),similitude(makevecteur(centre,rapport,angle,b._VECTptr->back()),s,contextptr)),_SEQ__VECT),contextptr);
      if (centre.type!=_VECT)
	return symb_pnt(centre+rapport*exp(cst_i*degtorad(angle,contextptr),contextptr)*(b-centre),default_color(contextptr),contextptr);
      return similitude3d(*centre._VECTptr,angle,rapport,b,1,contextptr);
    }
    if (s==3){
      vecteur w=makevecteur(v[0],v[1],v[2],x__IDNT_e);
      return symb_program(x__IDNT_e,zero,symbolic(at_similitude,gen(w,_SEQ__VECT)),contextptr);      
    }
    return gentypeerr(contextptr);
  }
  gen _similitude(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    return put_attributs(similitude(v,s,contextptr),attributs,contextptr);
  }
  static const char _similitude_s []="similarity";
  static define_unary_function_eval (__similitude,&giac::_similitude,_similitude_s);
  define_unary_function_ptr5( at_similitude ,alias_at_similitude,&__similitude,0,true);

  static gen translationpoint(const gen & a,const gen & b,GIAC_CONTEXT){
    return a+b;
  }
  
  gen translation(const gen & a,const gen & bb,GIAC_CONTEXT){
    gen elem(a);
    if (a.type==_VECT && a._VECTptr->size()==2){
      if (a.subtype==_LINE__VECT)
	elem=a._VECTptr->back()-a._VECTptr->front();
      else
	elem=a._VECTptr->front()+cst_i*a._VECTptr->back();
    }
    gen b=remove_at_pnt(bb);
    if (b.is_symb_of_sommet(at_hyperplan)){
      vecteur n,P;
      if (!hyperplan_normal_point(b,n,P))
	return gensizeerr(contextptr);
      return _plan(makevecteur(n,elem+P),contextptr);
    }
    if (b.is_symb_of_sommet(at_hypersphere)){
      gen c,r;
      if (!centre_rayon(b,c,r,false,contextptr))
	return gensizeerr(contextptr);
      return _sphere(makevecteur(elem+c,r),contextptr);
    }
    if (b.is_symb_of_sommet(at_parameter))
      return b;
    gen res;
    if (b.is_symb_of_sommet(at_hypersurface) || b.is_symb_of_sommet(at_curve))
      res=remove_at_pnt(curve_surface_apply(elem,b,translationpoint,contextptr));
    else
      res=apply3d(elem,b,contextptr,translationpoint);
    return symb_pnt(res,default_color(contextptr),contextptr);
  }

  static gen translation(const vecteur & v,int s,GIAC_CONTEXT){
    if (s==2){
      gen a=v[0];
      if (a.is_symb_of_sommet(at_pnt)){
	a=remove_at_pnt(a);
	if (a.type==_VECT && (a.subtype==_VECTOR__VECT || a.subtype==_GROUP__VECT) && a._VECTptr->size()==2){
	  return translation(makevecteur(a._VECTptr->back()-a._VECTptr->front(),v[1]),s,contextptr);
	}
	if (a.type!=_VECT || a.subtype!=_LINE__VECT)
	  return gensizeerr(gettext("First arg of translation should not be a point"));
      }
      if ( (a.type==_SYMB) && (a._SYMBptr->sommet==at_cercle) )
	return gensizeerr(contextptr);
      gen b=v[1];
      return apply2nd(a,b,contextptr,translation);
    }
    if (s==1){
      return symb_program(x__IDNT_e,zero,symbolic(at_translation,gen(makevecteur(v[0],x__IDNT_e),_SEQ__VECT)),contextptr);
    }
    return gentypeerr(contextptr);
  }
  gen _translation(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    return put_attributs(translation(v,s,contextptr),attributs,contextptr);
  }
  static const char _translation_s []="translation";
  static define_unary_function_eval (__translation,&giac::_translation,_translation_s);
  define_unary_function_ptr5( at_translation ,alias_at_translation,&__translation,0,true);

  // curve is the innert form of a parametric curve
  // source=[pnt,var], plot=ligne brisee
  static string printascurve(const gen & feuille,const char * sommetstr,GIAC_CONTEXT){
    if ( !fastcurveprint || (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) )
      return sommetstr+('('+feuille.print(contextptr)+')');
    return sommetstr+('('+feuille._VECTptr->front().print(contextptr)+",undef)");
  }
  /*
  static int sprintascurve(const gen & feuille,const char * sommetstr,string * sptr,GIAC_CONTEXT){
    int res=1+strlen(sommetstr);
    if (sptr)
      *sptr += '(';
    if ( !fastcurveprint || (feuille.type!=_VECT) || (feuille._VECTptr->size()!=2) ){
      res += feuille.sprint(sptr,contextptr);
      if (sptr)
	*sptr += ')';
      return res+1;
    }
    res += feuille._VECTptr->front().sprint(sptr,contextptr);
    if (sptr)
      *sptr += ",undef)";
    return res+7;    
  }
  */
  gen _curve(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return symbolic(at_curve,args);
  }
  static const char _curve_s []="curve";
  static define_unary_function_eval2 (__curve,&giac::_curve,_curve_s,&printascurve);
  define_unary_function_ptr5( at_curve ,alias_at_curve,&__curve,0,true);


  gen plotparam(const gen & f,const gen & vars,const vecteur & attributs,bool densityplot,double function_xmin,double function_xmax,double function_ymin,double function_ymax,double function_tmin, double function_tmax,double function_tstep,const context * contextptr){
    if (function_tstep<=0 || (function_tmax-function_tmin)/function_tstep>1e5)
      return gensizeerr(gettext("Plotparam: unable to discretize: tmin, tmax, tstep=")+print_DOUBLE_(function_tmin,12)+","+print_DOUBLE_(function_tmax,12)+","+print_DOUBLE_(function_tstep,12));
    gen fC(f);
    if (f.type==_VECT && f._VECTptr->size()==2)
      fC=f._VECTptr->front()+cst_i*f._VECTptr->back();
    gen attribut=attributs.empty()?default_color(contextptr):attributs[0];
    // bool save_approx_mode=approx_mode(contextptr);
    // approx_mode(true,contextptr);
    gen locvar(vars);
    locvar.subtype=0;
    gen xy=quotesubst(f,vars,locvar,contextptr),xy_,x_,y_;
    bool joindre;
    vecteur localvar(1,vars),res;
    context * newcontextptr=(context *) contextptr;
    int protect=bind(vecteur(1,function_tmin),localvar,newcontextptr);
    vecteur chemin;
    double i,j,oldi=0,oldj=0,entrei,entrej;
    double t=function_tmin;
    int nstep=int((function_tmax-function_tmin)/function_tstep+.5);
    // bool old_io_graph=io_graph(contextptr);
    // io_graph(false,contextptr);
    for (int count=0;count<=nstep;++count,t+= function_tstep){
      local_sto_double(t,*vars._IDNTptr,newcontextptr);
      // vars._IDNTptr->localvalue->back()._DOUBLE_val =t;
      if (xy.type==_VECT && xy._VECTptr->size()==2){
	x_=xy._VECTptr->front().evalf2double(eval_level(contextptr),newcontextptr);
	y_=xy._VECTptr->back().evalf2double(eval_level(contextptr),newcontextptr);
      }
      else {
	xy_=xy.evalf2double(eval_level(contextptr),newcontextptr);
	if (xy_.type==_VECT && xy_._VECTptr->size()==2){
	  x_=xy_._VECTptr->front();
	  y_=xy_._VECTptr->back();
	}
	else {
	  x_=re(xy_,newcontextptr);
	  y_=im(xy_,newcontextptr).evalf_double(eval_level(contextptr),newcontextptr);
	}
      }
      if ( (x_.type!=_DOUBLE_) || (y_.type!=_DOUBLE_) )
	continue;
      i=x_._DOUBLE_val;
      j=y_._DOUBLE_val;
      if (t!=function_tmin){
	if ( (fabs(oldj-j)>(function_ymax-function_ymin)/5) ||
	     (fabs(oldi-i)>(function_xmax-function_xmin)/5) ){
	  local_sto_double_increment(-function_tstep/2,*vars._IDNTptr,newcontextptr);
	  // vars._IDNTptr->localvalue->back()._DOUBLE_val -= function_tstep/2;
	  xy_=xy.evalf2double(eval_level(contextptr),newcontextptr);
	  if (xy_.type==_VECT && xy_._VECTptr->size()==2){
	    x_=xy_._VECTptr->front();
	    y_=xy_._VECTptr->back();
	  }
	  else {
	    x_=re(xy_,newcontextptr);
	    y_=im(xy_,newcontextptr);
	  }
	  if ( (x_.type!=_DOUBLE_) || (y_.type!=_DOUBLE_) )
	    joindre=false;
	  else {
	    entrei=x_._DOUBLE_val;
	    entrej=y_._DOUBLE_val;
	    if (j>oldj)
	      joindre=(j>=entrej) && (entrej>=oldj);
	    else
	      joindre=(j<=entrej) && (entrej<=oldj);
	    if (i>oldi)
	      joindre=joindre && (i>=entrei) && (entrei>=oldi);
	    else
	      joindre=joindre && (i<=entrei) && (entrei<=oldi);
	  }
	  local_sto_double_increment(-function_tstep/2,*vars._IDNTptr,newcontextptr);
	  // vars._IDNTptr->localvalue->back()._DOUBLE_val -= function_tstep/2;
	}
	else
	  joindre=true;
      } // if t!=function_tmin
      else
	joindre=true;
      if (joindre)
	chemin.push_back(gen(i,j));
      else {
	if (!chemin.empty())
	  res.push_back(symb_pnt(symb_curve(gen(makevecteur(fC,vars,function_tmin,t),_PNT__VECT),gen(chemin,_GROUP__VECT)),attribut,contextptr));
	function_tmin=t;
	chemin=vecteur(1,gen(i,j));
      }
      oldi=i;
      oldj=j;
    }
    if (!chemin.empty())
      res.push_back(symb_pnt(symb_curve(gen(makevecteur(fC,vars,function_tmin,function_tmax),_PNT__VECT),gen(chemin,_GROUP__VECT)),attribut,contextptr));
    leave(protect,localvar,newcontextptr);
    // io_graph(old_io_graph,contextptr);
#if !defined(WIN32) && defined(WITH_GNUPLOT)
    if (child_id) plot_instructions.push_back(res);
#endif // WIN32
    // approx_mode(save_approx_mode,contextptr);
    if (res.size()==1)
      return res.front();
    // gen e(res,_SEQ__VECT);
    return res; // e;
  }

  gen paramplotparam(const gen & args,bool densityplot,const context * contextptr){
    // args= [x(t)+i*y(t),t] should add a t interval
    bool f_autoscale=autoscale;
    if (args.type!=_VECT)
      return paramplotparam(gen(makevecteur(args,t__IDNT_e),_SEQ__VECT),densityplot,contextptr);
    vecteur vargs(plotpreprocess(args,contextptr));
    if (is_undef(vargs))
      return vargs;
    if (vargs.size()<2)
      return symbolic(at_plotparam,args);
    gen f=vargs.front();
    gen vars=vargs[1];
    /*
    if (f.type==_VECT && f._VECTptr->size()==2)
      f=f._VECTptr->front()+cst_i*f._VECTptr->back();
    */
    bool param2d=f.type!=_VECT || f._VECTptr->size()==2;
    if (param2d){
      if (vars.type==_VECT){
	if (vars._VECTptr->size()!=1)
	  return gensizeerr(contextptr);
	vars=vars._VECTptr->front();
      }
    }
    int s=vargs.size();
    double umin=gnuplot_tmin,vmin=gnuplot_tmin,umax=gnuplot_tmax,vmax=gnuplot_tmax,tmin=gnuplot_tmin,tmax=gnuplot_tmax,xmin=gnuplot_xmin,xmax=gnuplot_xmax,ymin=gnuplot_ymin,ymax=gnuplot_ymax,zmin=gnuplot_zmin,zmax=gnuplot_zmax,tstep=-1,ustep=-1,vstep=-1;
    if (param2d && s>=3){
      gen trange=vargs[2].evalf_double(eval_level(contextptr),contextptr);
      if ( (trange.type==_SYMB) && (trange._SYMBptr->sommet==at_interval)){
	tmin=evalf_double(trange._SYMBptr->feuille._VECTptr->front(),eval_level(contextptr),contextptr)._DOUBLE_val;
	tmax=evalf_double(trange._SYMBptr->feuille._VECTptr->back(),eval_level(contextptr),contextptr)._DOUBLE_val;
	if (s>3)
	  tstep=vargs[3].evalf_double(eval_level(contextptr),contextptr)._DOUBLE_val;
      }
      else {
	if (s>=4){
	  tmin=vargs[2].evalf_double(eval_level(contextptr),contextptr)._DOUBLE_val;
	  tmax=vargs[3].evalf_double(eval_level(contextptr),contextptr)._DOUBLE_val;
	  if (s>4)
	    tstep=vargs[4].evalf_double(eval_level(contextptr),contextptr)._DOUBLE_val;
	}
      }
      if (tmin>tmax )
	swapdouble(tmin,tmax);
    }
    gen attribut=default_color(contextptr);
    for (int i=1;i<s;++i){
#if 0 // def GIAC_HAS_STO_38
      if (readvar(vargs[i])==vx_var){
	f_autoscale=false;
	readrange(vargs[i],tmin,tmax,vargs[i],tmin,tmax,contextptr); 
	umin=tmin; umax=tmax;
      }
#endif
      if (readvar(vargs[i])==x__IDNT_e){
	f_autoscale=false;
	readrange(vargs[i],gnuplot_xmin,gnuplot_xmax,vargs[i],xmin,xmax,contextptr);
      }
      if (readvar(vargs[i])==y__IDNT_e){
	f_autoscale=false;
	readrange(vargs[i],gnuplot_ymin,gnuplot_ymax,vargs[i],ymin,ymax,contextptr);
      }
      if (readvar(vargs[i])==z__IDNT_e){
	f_autoscale=false;
	readrange(vargs[i],gnuplot_zmin,gnuplot_zmax,vargs[i],zmin,zmax,contextptr); 
      }
      if (readvar(vargs[i])==u__IDNT_e){
	f_autoscale=false;
	readrange(vargs[i],tmin,tmax,vargs[i],umin,umax,contextptr); 
      }
      if (readvar(vargs[i])==v__IDNT_e){
	f_autoscale=false;
	readrange(vargs[i],tmin,tmax,vargs[i],vmin,vmax,contextptr); 
      }
      if (readvar(vargs[i])==t__IDNT_e){
	f_autoscale=false;
	readrange(vargs[i],tmin,tmax,vargs[i],tmin,tmax,contextptr); 
	umin=tmin; umax=tmax;
      }
      if (vargs[i].is_symb_of_sommet(at_equal) && vargs[i]._SYMBptr->feuille.type==_VECT && vargs[i]._SYMBptr->feuille._VECTptr->front().type==_INT_){
	gen n=vargs[i]._SYMBptr->feuille._VECTptr->back();
	switch (vargs[i]._SYMBptr->feuille._VECTptr->front().val){
	case _NSTEP:
	  if (n.type!=_INT_) return gensizeerr(contextptr);
	  tstep=std::abs((tmax-tmin)/n.val);
	  ustep=(umax-umin)/int(std::sqrt(double(n.val)));
	  vstep=(vmax-vmin)/int(std::sqrt(double(n.val)));
	  break;
	case _TSTEP:
	  n=evalf_double(n,1,contextptr);
	  tstep=std::abs(n._DOUBLE_val);
	  ustep=std::abs(n._DOUBLE_val);
	  vstep=std::abs(n._DOUBLE_val);
	  break;
	case _USTEP:
	  n=evalf_double(n,1,contextptr);
	  ustep=std::abs(n._DOUBLE_val);
	  break;
	case _VSTEP:
	  n=evalf_double(n,1,contextptr);
	  vstep=std::abs(n._DOUBLE_val);
	  break;
	}
      }
    }
    vecteur attributs(1,attribut);
    s=read_attributs(vargs,attributs,contextptr);
    bool curve3d=false;
    if (f.type==_VECT && f._VECTptr->size()==3 && vars.type!=_VECT){ // 3-d curve
      if (s==4){
	if (vargs[2].is_symb_of_sommet(at_interval)){
	  vars=symbolic(at_equal,makesequence(vars,vargs[2]));
	  tstep=std::abs(evalf_double(vargs[3],1,contextptr)._DOUBLE_val);
	}
	else
	  vars=symbolic(at_equal,makesequence(vars,symbolic(at_interval,makesequence(vargs[2],vargs[3]))));
      }
      vars=makevecteur(vars,symbolic(at_equal,makesequence(v__IDNT_e,symbolic(at_interval,makesequence(vmin,vmax)))));
      curve3d=true;
    }
    if (tstep<0)
      tstep=(tmax-tmin)/gnuplot_pixels_per_eval;
    if (vars.type==_VECT && vars._VECTptr->size()>=2){
      vecteur v=*vars._VECTptr;
      if (v.size()!=2)
	return gensizeerr(contextptr);
      if (readrange(v[0],tmin,tmax,v[0],umin,umax,contextptr) && readrange(v[1],tmin,tmax,v[1],vmin,vmax,contextptr)){
	if (ustep<0)
	  ustep=(umax-umin)/(curve3d?gnuplot_pixels_per_eval:std::sqrt(double(gnuplot_pixels_per_eval)));
	if (vstep<0)
	  vstep=(vmax-vmin)/std::sqrt(double(gnuplot_pixels_per_eval));
	return plotparam3d(f,makevecteur(v[0],v[1]),xmin,xmax,ymin,ymax,zmin,zmax,umin,umax,vmin,vmax,densityplot,f_autoscale,attributs,ustep,vstep,undef,vecteur(0),contextptr);
      }
      return gensizeerr(contextptr);
    }
    if (!readrange(vars,tmin,tmax,vars,tmin,tmax,contextptr))
      return gensizeerr(gettext("2nd arg must be a free variable"));
    return plotparam(f,vars,attributs,densityplot,xmin,xmax,ymin,ymax,tmin,tmax,tstep,contextptr);
  }
  gen _plotparam(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return paramplotparam(args,true,contextptr);
  }
  static const char _plotparam_s []="plotparam";
  static define_unary_function_eval_quoted (__plotparam,&giac::_plotparam,_plotparam_s);
  define_unary_function_ptr5( at_plotparam ,alias_at_plotparam,&__plotparam,_QUOTE_ARGUMENTS,true);

  static const char _courbe_parametrique_s []="courbe_parametrique";
  static define_unary_function_eval_quoted (__courbe_parametrique,&giac::_plotparam,_courbe_parametrique_s);
  define_unary_function_ptr5( at_courbe_parametrique ,alias_at_courbe_parametrique,&__courbe_parametrique,_QUOTE_ARGUMENTS,true);

  gen _paramplot(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return paramplotparam(args,false,contextptr);
  }
  static const char _paramplot_s []="paramplot";
  static define_unary_function_eval_quoted (__paramplot,&giac::_paramplot,_paramplot_s);
  define_unary_function_ptr5( at_paramplot ,alias_at_paramplot,&__paramplot,_QUOTE_ARGUMENTS,true);

  static gen plotpoints(const vecteur & v,const vecteur & attributs,GIAC_CONTEXT){
    gen attribut=attributs.empty()?default_color(contextptr):attributs[0];
    vecteur w(v);
    iterateur it=w.begin(),itend=w.end();
    for (;it!=itend;++it){
      if (it->type!=_VECT || it->_VECTptr->size()!=2)
	return gensizeerr(contextptr);
      *it=it->_VECTptr->front()+cst_i*it->_VECTptr->back();
    }
    return symb_pnt(gen(w,_GROUP__VECT),attribut.val,contextptr); 
    // should change symb_pnt so that attribut is more generic than color
  }
  gen _plot(const gen & g,const context * contextptr){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return _plotfunc(g,contextptr);
    vecteur v=plotpreprocess(g,contextptr);
    if (is_undef(v))
      return v;
    int s=v.size();
    gen attribut=default_color(contextptr);
    vecteur attributs(1,attribut);
    if (g.subtype!=_SEQ__VECT && s==3 ){
      if (v[2].type==_IDNT)
	return plotparam(v[0]+cst_i*v[1],v[2],attributs,true,gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,gnuplot_tmin,gnuplot_tmax,gnuplot_tstep,contextptr);
      if (v[2].is_symb_of_sommet(at_equal)){ // parametric plot
	gen & gw=v[2]._SYMBptr->feuille;
	if (gw.type==_VECT && gw._VECTptr->size()==2){
	  gen gx=gw._VECTptr->front();
	  double inf,sup;
	  if (gx.type==_IDNT && chk_double_interval(gw._VECTptr->back(),inf,sup,contextptr))
	    return plotparam(v[0]+cst_i*v[1],gx,attributs,true,gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,inf,sup,gnuplot_tstep,contextptr);
	}
      }
    }
    if (s<1)
      return _plotfunc(g,contextptr);
    if (g.subtype!=_SEQ__VECT)
      return plotpoints(v,attributs,contextptr);
    double xmin=gnuplot_xmin,xmax=gnuplot_xmax,ymin=gnuplot_ymin,ymax=gnuplot_ymax,zmin=gnuplot_zmin,zmax=gnuplot_zmax;
    gen xvar=vx_var,yvar=y__IDNT_e;
    int nstep=gnuplot_pixels_per_eval;
    bool showeq=false;
    // bool clrplot=true;
    // parse options
    for (int i=1;i<s;++i){
      if (v[i]==at_equation){
	showeq=true;
	continue;
      }
      if (v[i].type==_IDNT){
	if (i==1)
	  xvar=v[1];
	if (i==2)
	  yvar=v[2];
      }
      if (i==1 && v[i].is_symb_of_sommet(at_interval)){
	identificateur tmp(" x");
	vecteur w(v);
	w[0]=v[0](tmp,contextptr);
	w[1]=symbolic(at_equal,makesequence(tmp,v[1]));
	return _plot(gen(w,_SEQ__VECT),contextptr);
      }
      if (!v[i].is_symb_of_sommet(at_equal))
	continue;
      gen & opt=v[i]._SYMBptr->feuille;
      if (opt.type!=_VECT || opt._VECTptr->size()!=2)
	continue;
      gen opt1=opt._VECTptr->front(),opt2=opt._VECTptr->back();
      double inf,sup;
      if ( opt1.type==_IDNT &&chk_double_interval(opt2,inf,sup,contextptr)){	
	if (i==1){
	  xvar=opt1;
	  xmin=inf;
	  xmax=sup;
	}
	if (i==2){
	  yvar=opt1;
	  ymin=inf;
	  ymax=sup;
	}
      }
    }
    int jstep,kstep;
    read_option(v,xmin,xmax,ymin,ymax,zmin,zmax,attributs,nstep,jstep,kstep,contextptr);
    if (v[0].type==_VECT && !v[0]._VECTptr->empty() && v[0]._VECTptr->front().type==_VECT)
      return plotpoints(*v[0]._VECTptr,attributs,contextptr);
    else
      return plotfunc(v[0],xvar,attributs,false,xmin,xmax,ymin,ymax,zmin,zmax,nstep,0,showeq,contextptr);
  }
  static const char _plot_s []="plot"; // FIXME use maple arguments
  static define_unary_function_eval_quoted (__plot,&giac::_plot,_plot_s);
  define_unary_function_ptr5( at_plot ,alias_at_plot,&__plot,_QUOTE_ARGUMENTS,true);

  static const char _graphe_s []="graphe";
  static define_unary_function_eval_quoted (__graphe,&giac::_plot,_graphe_s);
  define_unary_function_ptr5( at_graphe ,alias_at_graphe,&__graphe,_QUOTE_ARGUMENTS,true);

  gen _plotpolar(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    // args= [rho(theta),theta] should add a theta interval
    vecteur vargs(plotpreprocess(args,contextptr));
    if (is_undef(vargs))
      return vargs;
    gen rho=vargs.front();
    gen theta=vargs[1];
    if (theta.is_symb_of_sommet(at_equal))
      theta=theta._SYMBptr->feuille._VECTptr->front();
    if (theta.type!=_IDNT)
      return gensizeerr(gettext("2nd arg must be a free variable"));    
    // vargs.front()=symbolic(at_re,rho)*exp(cst_i*degtorad(theta,contextptr),contextptr);
    vargs.front()=makevecteur(rho*cos(degtorad(theta,contextptr),contextptr),rho*sin(degtorad(theta,contextptr),contextptr));
    return _plotparam(gen(vargs,_SEQ__VECT),contextptr);
  }
  static const char _plotpolar_s []="plotpolar";
  static define_unary_function_eval_quoted (__plotpolar,&giac::_plotpolar,_plotpolar_s);
  define_unary_function_ptr5( at_plotpolar ,alias_at_plotpolar,&__plotpolar,_QUOTE_ARGUMENTS,true);

  static const char _polarplot_s []="polarplot";
  static define_unary_function_eval_quoted (__polarplot,&giac::_plotpolar,_polarplot_s);
  define_unary_function_ptr5( at_polarplot ,alias_at_polarplot,&__polarplot,_QUOTE_ARGUMENTS,true);

  static const char _courbe_polaire_s []="courbe_polaire";
  static define_unary_function_eval_quoted (__courbe_polaire,&giac::_plotpolar,_courbe_polaire_s);
  define_unary_function_ptr5( at_courbe_polaire ,alias_at_courbe_polaire,&__courbe_polaire,_QUOTE_ARGUMENTS,true);

  void ck_parameter_x(GIAC_CONTEXT){
#if 1 // ndef GIAC_HAS_STO_38
    if (x__IDNT_e.evalf(1,contextptr)!=x__IDNT_e)
      *logptr(contextptr) << gettext("Variable x should be purged") << endl;
#endif
  }

  void ck_parameter_y(GIAC_CONTEXT){
#if 1 // ndef GIAC_HAS_STO_38
    if (y__IDNT_e.evalf(1,contextptr)!=y__IDNT_e)
      *logptr(contextptr) << gettext("Variable y should be purged") << endl;
#endif
  }

  void ck_parameter_z(GIAC_CONTEXT){
#if 1 // ndef GIAC_HAS_STO_38
    if (z__IDNT_e.evalf(1,contextptr)!=z__IDNT_e)
      *logptr(contextptr) << gettext("Variable z should be purged") << endl;
#endif
  }

  void ck_parameter(const gen & g,GIAC_CONTEXT){
#if 1 // ndef GIAC_HAS_STO_38
    if ( (g.type==_IDNT) && (g.evalf(1,contextptr)!=g) )
      *logptr(contextptr) << gettext("Variable ")+g.print(contextptr)+gettext(" should be purged") << endl;
#endif
  }

  void ck_parameter_t(GIAC_CONTEXT){
#if 1 // ndef GIAC_HAS_STO_38
    if (t__IDNT_e.evalf(1,contextptr)!=t__IDNT_e)
      *logptr(contextptr) << gettext("Variable t should be purged") << endl;
#endif
  }

  void ck_parameter_u(GIAC_CONTEXT){
#if 1 // ndef GIAC_HAS_STO_38
    if (u__IDNT_e.evalf(1,contextptr)!=u__IDNT_e)
      *logptr(contextptr) << gettext("Variable u should be purged") << endl;
#endif
  }

  void ck_parameter_v(GIAC_CONTEXT){
#if 1 // ndef GIAC_HAS_STO_38
    if (v__IDNT_e.evalf(1,contextptr)!=v__IDNT_e)
      *logptr(contextptr) << gettext("Variable v should be purged") << endl;
#endif
  }

  // Parametric equation
  gen _parameq(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v;
    int s=1;
    if (args.type!=_VECT){
      v=vecteur(1,args);
      v.push_back(t__IDNT_e);
    }
    else {
      s=args._VECTptr->size();
      if (s>=2 && (*args._VECTptr)[1].is_symb_of_sommet(at_pnt))
	return _parameq(args._VECTptr->front(),contextptr);
      if (s<2) 
	return gensizeerr(contextptr);
      v=*args._VECTptr;
    }
    // e= symb_curve or line or cercle
    gen e=remove_at_pnt(v.front());
    if (e.is_symb_of_sommet(at_hypersurface)){
      gen & f = e._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==3){
	gen & p=f._VECTptr->front();
	if (p.type==_VECT && !p._VECTptr->empty())
	  return p._VECTptr->front();
      }
      return undef;
    }
    gen t=v[1],u,par_eq;
    if (e.type==_SYMB && (e._SYMBptr->sommet==at_hyperplan || e._SYMBptr->sommet==at_hypersphere) ){
      if (s<2){
	ck_parameter_u(contextptr);
	t=u__IDNT_e;
      }
      if (s<3){
	ck_parameter_v(contextptr);
	u=v__IDNT_e;
      }
      else
	u=v[2];
      if (e._SYMBptr->sommet==at_hypersphere){
	gen centre,rayon;
	if (!centre_rayon(e,centre,rayon,false,contextptr) ||centre.type!=_VECT)
	  return gensizeerr(contextptr);
	vecteur & v=*centre._VECTptr;
	if (v.size()!=3)
	  return gendimerr(contextptr);
	par_eq=centre+makevecteur(rayon*cos(t,contextptr)*cos(u,contextptr),rayon*cos(t,contextptr)*sin(u,contextptr),rayon*sin(t,contextptr));
      }
      else {
	vecteur P,n;
	if (!hyperplan_normal_point(e,n,P))
	  return gensizeerr(contextptr);
	vecteur v1,v2;
	if (!normal3d(n,v1,v2))
	  return gensizeerr(contextptr);
	par_eq=t*v1+u*v2+P;
      }
      return par_eq;
    }
    if (s==1)
      ck_parameter_t(contextptr);
    if ((e.type==_SYMB) && (e._SYMBptr->sommet==at_cercle))
      e=cercle2curve(e,contextptr);
    if (e.type==_VECT)
      e=line2curve(e);
    if (is_undef(e))
      return e;
    if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_curve)){
      vecteur v=*e._SYMBptr->feuille._VECTptr->front()._VECTptr;
      gen tmp= subst(v[0],v[1],t,false,contextptr);
      if (tmp.type==_VECT)
	tmp.subtype=0;
      return tmp;
    }
    else
      return e;
  }
  static const char _parameq_s []="parameq";
  static define_unary_function_eval (__parameq,&giac::_parameq,_parameq_s);
  define_unary_function_ptr5( at_parameq ,alias_at_parameq,&__parameq,0,true);

  static gen rationalparam2equation(const gen & at_orig,const gen & t_orig,const gen &x,const gen & y,GIAC_CONTEXT){
    gen anum,aden,ax,ay;
    gen at=at_orig;
    gen t=t_orig;
    bool approx=has_num_coeff(at);
    if (approx)
      at=exact(at,contextptr);
    ax=re(at,contextptr);
    ay=im(at,contextptr);
    if (ax==t)
      return y-subst(ay,t,x,false,contextptr);
    if (t==x){
      t=identificateur(" t");
      at=subst(at,x,t,true,contextptr);
    }
    if (t==y){
      t=identificateur(" t");
      at=subst(at,y,t,true,contextptr);
    }
    vecteur v=lvar(ax);
    fraction f=e2r(ax,v,contextptr);
    fxnd(f,anum,aden);
    anum=r2e(anum,v,contextptr);
    aden=r2e(aden,v,contextptr);
    gen eq1=anum-x*aden;
    v=lvar(ay);
    f=e2r(ay,v,contextptr);
    fxnd(f,anum,aden);
    anum=r2e(anum,v,contextptr);
    aden=r2e(aden,v,contextptr);
    gen eq2=anum-y*aden;
    gen res=_resultant(makevecteur(eq1,eq2,t),contextptr);
    if (is_undef(res)) return res;
    if (approx)
      res=evalf(res,1,contextptr);
    v=lvar(res);
    f=e2r(res,v,contextptr);
    fxnd(f,anum,aden);
    if (anum.type!=_POLY)
      return res;
    if (approx)
      anum=anum/anum._POLYptr->coord.front().value;
    else
      ppz(*anum._POLYptr);
    res=r2e(anum,v,contextptr);
    return res;
  }
  
  static gen equation(const gen & arg,const gen & x,const gen & y, const gen & z,GIAC_CONTEXT){
    if (arg.type==_VECT){
      vecteur res;
      const_iterateur it=arg._VECTptr->begin(),itend=arg._VECTptr->end();
      for (;it!=itend;++it){
	res.push_back(equation(*it,x,y,z,contextptr));
      }
      return res;
    }
    gen e=remove_at_pnt(arg);
    vecteur vxyz(makevecteur(x,y,z));
    if (e.is_symb_of_sommet(at_hyperplan)){
      gen & f=e._SYMBptr->feuille;
      if (ckmatrix(f) && f._VECTptr->size()==2){
	return symbolic(at_equal,makesequence(normal(dotvecteur(*f._VECTptr->front()._VECTptr,subvecteur(vxyz,*f._VECTptr->back()._VECTptr)),contextptr),zero));
      }
    }
    if (e.is_symb_of_sommet(at_hypersphere)){
      gen f=hypersphere_equation(e,vxyz);
      return symbolic(at_equal,makesequence(f,zero));
    }
    if (e.is_symb_of_sommet(at_hypersurface)){
      gen f=hypersurface_equation(e,vxyz,contextptr);
      return symbolic(at_equal,makesequence(f,zero));
    }
    if ((e.type==_SYMB) && (e._SYMBptr->sommet==at_cercle)){
      gen centre,rayon;
      if (!centre_rayon(e,centre,rayon,false,contextptr))
	return gensizeerr(contextptr);
      rayon=normal(rayon,contextptr);
      return symbolic(at_equal,makesequence(pow(x-re(centre,contextptr),2)+pow(y-im(centre,contextptr),2),rayon*conj(rayon,contextptr)));
    }
    if ( (e.type==_VECT) && (e._VECTptr->size()==2) ){
      gen A=e._VECTptr->front();
      gen B=e._VECTptr->back();
      gen v=B-A;
      if (v.type==_VECT){ // 3-d line?
	vecteur & vv=*v._VECTptr;
	if (vv.size()!=3 || A.type!=_VECT || A._VECTptr->size()!=3)
	  return gensizeerr(contextptr);
	vecteur & vA=*A._VECTptr;
	if (is_zero(vv[0])&&is_zero(vv[1]))
	  return gen(makevecteur(symbolic(at_equal,makesequence(x,vA[0])),symbolic(at_equal,makesequence(y,vA[1]))),_SEQ__VECT);
	vecteur v1(makevecteur(vv[1],-vv[0],0));
	vecteur v2(cross(vv,v1,contextptr));
	vecteur xyz(subvecteur(vxyz,vA));
	return gen(makevecteur(symbolic(at_equal,makesequence(normal(dotvecteur(v1,xyz),contextptr),zero)),symbolic(at_equal,makesequence(normal(dotvecteur(v2,xyz),contextptr),zero))),_SEQ__VECT);
      }
      gen a=im(v,contextptr);
      gen b=-re(v,contextptr);
      gen d=gcd(a,b);
      a=normal(a/d,contextptr);
      b=normal(b/d,contextptr);
      gen c=a*re(A,contextptr)+b*im(A,contextptr);
      if (!is_zero(b))
	return symbolic(at_equal,makesequence(y,normal(-a/b,contextptr)*x+normal(c/b,contextptr)));
      return symbolic(at_equal,makesequence(a*x+b*y,c));
    }
    if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_curve)){
      vecteur v=*e._SYMBptr->feuille._VECTptr->front()._VECTptr; 
      if (v[1].type!=_IDNT)
	return gensizeerr(gettext("Wrong parameter type ")+v[1].print(contextptr));
      identificateur & id=*v[1]._IDNTptr;
      if (v[0].type!=_VECT){
	gen m,tmin,tmax;
	double T=1;
	if (find_curve_parametrization(e,m,v[1],T,tmin,tmax,false,contextptr))
	  v[0]=m;
	gen xt=re(v[0],contextptr);
	rewrite_with_t_real(xt,v[1],contextptr);
	gen yt=im(v[0],contextptr);
	rewrite_with_t_real(yt,v[1],contextptr);
	// if xt and yt are rational fractions of v[1], use the resultant
	if (lvarxpow(makevecteur(xt,yt),v[1]).size()<=1){
	  // return _resultant(makevecteur(xt-x,yt-y,v[1]),contextptr);
	  return rationalparam2equation(v[0],v[1],x,y,contextptr);
	}
	vecteur w(solve(xt-x,v[1],0,contextptr));
	if (w.empty())
	  return gensizeerr(gettext("Can't isolate"));
	yt=subst(yt,v[1],w.front(),false,contextptr);
	return symbolic(at_equal,makesequence(y,yt));
      }
      if (v[0].type==_VECT && v[0]._VECTptr->size()==3){
	// 3-d curve -> 2 equations
	vecteur & vv=*v[0]._VECTptr;
	gen xt=vv[0],yt=vv[1],zt=vv[2];
	if (lvarxpow(makevecteur(xt,yt,zt),v[1]).size()<=1){
	  return makevecteur(_resultant(makesequence(xt-x,zt-z,id),contextptr),_resultant(makesequence(yt-y,zt-z,id),contextptr));
	}
      }
      return gensizeerr(contextptr);
    }
    return e;
  }

  gen _equation(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2 || (*args._VECTptr)[1].type!=_IDNT){
      ck_parameter_x(contextptr);
      ck_parameter_y(contextptr);
      ck_parameter_z(contextptr);
      return equation(args,x__IDNT_e,y__IDNT_e,z__IDNT_e,contextptr);
    }
    vecteur v(*args._VECTptr);
    // e= symb_curve or line or cercle
    gen xy=v.back();
    if ( (xy.type!=_VECT) || (xy._VECTptr->size()<2))
      return gensizeerr(contextptr);
    vecteur & vxyz=*xy._VECTptr;
    gen x(vxyz[0]),y(vxyz[1]),z;
    if (vxyz.size()==3)
      z=vxyz[2];
    return equation(v.front(),x,y,z,contextptr);
  }
  static const char _equation_s []="equation";
  static define_unary_function_eval (__equation,&giac::_equation,_equation_s);
  define_unary_function_ptr5( at_equation ,alias_at_equation,&__equation,0,true);

  // If subtype is segment/vector or halfline remove
  // the element of v that are not in the segment/halfline/... ab
  vecteur remove_not_in_segment(const gen & a,const gen & b,int subtype,const vecteur & v,GIAC_CONTEXT){
    if (subtype==_LINE__VECT || is_undef(v))
      return v;
    gen ab(b-a);
    const_iterateur it=v.begin(),itend=v.end();
    vecteur res;
    for (;it!=itend;++it){
      if (is_undef(*it))
	continue;
      gen t=scalar_product(remove_at_pnt(*it)-a,ab,contextptr)/scalar_product(ab,ab,contextptr);
      if (is_undef(t)) continue;
      if (subtype==_HALFLINE__VECT){
	if (!is_strictly_positive(-t,contextptr))
	  res.push_back(*it);
      }
      else {
	if (!is_strictly_positive(-t,contextptr) && !is_strictly_positive(t-1,contextptr))
	  res.push_back(*it);
      }
    }
    return res;
  }

  vecteur remove_not_in_arc(const vecteur & v,const gen & g,GIAC_CONTEXT){
    if (is_undef(v))
      return v;
    if (!g.is_symb_of_sommet(at_cercle))
      return v;
    gen &f=g._SYMBptr->feuille;
    if (f.type!=_VECT || f._VECTptr->size()!=3)
      return v;
    vecteur & fv=*f._VECTptr;
    if (fv.front().type!=_VECT || fv.front()._VECTptr->size()!=2)
      return v;
    gen a=fv.front()._VECTptr->front(),b=fv.front()._VECTptr->back(),alpha=fv[1],beta=fv[2];
    if (is_greater(beta-alpha,cst_two_pi,contextptr))
      return v;
    alpha=alpha-_floor(alpha/cst_two_pi,contextptr)*cst_two_pi;
    beta=beta-_floor(beta/cst_two_pi,contextptr)*cst_two_pi;
    if (is_greater(alpha,beta,contextptr))
      beta+=cst_two_pi;
    gen centre=(b+a)/2,rayon=(b-a)/2;
    const_iterateur it=v.begin(),itend=v.end();
    vecteur res;
    for (;it!=itend;++it){
      gen gamma=arg((remove_at_pnt(*it)-centre)/rayon,contextptr);
      if (is_strictly_positive(-gamma,contextptr))
	gamma+=cst_two_pi;
      if (is_greater(gamma,alpha,contextptr) && is_greater(beta,gamma,contextptr))
	res.push_back(*it);
    }
    return res;
  }

  vecteur interdroitecercle(const gen & a,const gen &b,GIAC_CONTEXT){
    // D inter C
    gen centre,rayon;
    if (!centre_rayon(b,centre,rayon,true,contextptr))
      return vecteur(1,gensizeerr(contextptr));
    // projection of the center of C over D
    gen a1(a._VECTptr->front()),a2(a._VECTptr->back()); // D=(a1a2)
    if (a1.type==_VECT && b.is_symb_of_sommet(at_cercle))
      return vecteur(1,gensizeerr(gettext("3-d line/2-d circle")));
    if (a1.type!=_VECT && b.is_symb_of_sommet(at_hypersphere))
      return vecteur(1,gensizeerr(gettext("2-d line/sphere")));
    gen t(projection(a1,a2,centre,contextptr));
    if (is_undef(t))
      return vecteur(1,t);
    gen pr(t*a2+(1-t)*a1);
    gen a_pr(recursive_normal(pr-centre,contextptr));
    gen delta(abs_norm(a_pr,contextptr));
    if (ck_is_strictly_positive(recursive_normal(delta-rayon,contextptr),contextptr))
      return vecteur(0);
    gen a_pr_perp=a2-a1;
    a_pr_perp=rdiv(a_pr_perp,abs_norm(a_pr_perp,contextptr),contextptr);
    gen d_a(sqrt(pow(rayon,plus_two,contextptr)-pow(delta,plus_two,contextptr),contextptr));
    gen I1(recursive_normal(pr+d_a*a_pr_perp,contextptr)),I2(recursive_normal(pr-d_a*a_pr_perp,contextptr));
    if (I1.type==_VECT) 
      I1.subtype=_POINT__VECT;
    if (I2.type==_VECT) 
      I2.subtype=_POINT__VECT;
    return remove_not_in_arc(remove_not_in_segment(a1,a2,a.subtype,makevecteur(symb_pnt(I1,default_color(contextptr),contextptr),symb_pnt(I2,default_color(contextptr),contextptr)),contextptr),b,contextptr);
  }  


  vecteur interpolygone(const vecteur & p,const gen & bb,GIAC_CONTEXT){
    vecteur res;
    const_iterateur it=p.begin(),itend=p.end();
    if (itend-it<2)
      return res;
    for (++it;it!=itend;++it){
      gen tmp=symbolic(at_pnt,gen(makevecteur(gen(makevecteur(*(it-1),*it),_GROUP__VECT),0),_PNT__VECT));
      vecteur add(inter(tmp,bb,contextptr));
      const_iterateur jt=add.begin(),jtend=add.end();
      for (;jt!=jtend;++jt){
	if (!equalposcomp(res,*jt))
	  res.push_back(*jt);
      }
    }
    return res;
  }

  vecteur inter2cercles_or_spheres(const gen & centre_a,const gen & rayon_a2,const gen & centre_b,const gen & rayon_b2,bool a2d,GIAC_CONTEXT){
    gen ab(centre_b-centre_a);
    gen ab2(abs_norm2(ab,contextptr));
    gen ab2minusr2=ab2-rayon_a2-rayon_b2;
    if (is_strictly_greater(ab2minusr2,0,contextptr) && is_strictly_greater(pow(ab2minusr2,2)-4*rayon_a2*rayon_b2,0,contextptr) )
      return vecteur(0); // empty
    gen ababs(sqrt(ab2,contextptr));
    if (!a2d && (centre_a.type!=_VECT || centre_a._VECTptr->size()!=3 || centre_b.type!=_VECT || centre_b._VECTptr->size()!=3 ))
      return vecteur(1,gensizeerr(contextptr));
    // delta=ra^2-rb^2+AB^2
    gen delta=rayon_a2-rayon_b2+ab2;
    gen ab4=centre_a+delta/2/ab2*ab;
    gen d_perp(sqrt(4*ab2*rayon_a2-pow(delta,2),contextptr)/2/ab2);
    if (a2d){ // circle inter circle = 2 points (or 1)
      gen ab_perp(im(ab,contextptr)-cst_i*re(ab,contextptr));
      return makevecteur(symb_pnt(ratnormal(ab4+d_perp*ab_perp),default_color(contextptr),contextptr),symb_pnt(ratnormal(ab4-d_perp*ab_perp),default_color(contextptr),contextptr));
    }
    else { // 3-d sphere inter sphere = 2-d circle
      // we will draw a parametric curve in the plan perpendicular to ab
      // at ab4, with radius d_perp
      // Find 2 vectors normal to ab
      vecteur v1,v2;
      if (!normal3d(ab,v1,v2))
	return vecteur(1,gensizeerr(contextptr));
      d_perp=sqrt(rayon_a2-pow(delta,2)/4/ab2,contextptr);
      gen eq=ab4+d_perp/abs_norm(v1,contextptr)*symb_cos(vx_var)*v1+d_perp/abs_norm(v2,contextptr)*symb_sin(vx_var)*v2;
      return makevecteur(_plotparam(eq,contextptr));
    }
  }

  vecteur curveintercircle(const gen & curve,const gen &circle,bool iscircle,GIAC_CONTEXT){
    gen f=curve._SYMBptr->feuille;
    if (f.type!=_VECT || f._VECTptr->empty() || f._VECTptr->front().type!=_VECT)
      return vecteur(1,gensizeerr(contextptr));
    vecteur vf=*f._VECTptr->front()._VECTptr;
    gen m,tmin,tmax;
    double T=1e300;
    if (vf.size()<2 || vf[1].type!=_IDNT)
      return vecteur(1,gensizeerr(contextptr));
    if (find_curve_parametrization(curve,m,vf[1],T,tmin,tmax,false,contextptr)){
      vf[0]=m;
    }
    gen eq;
#ifndef NO_STDEXCEPT
    try {
#endif
      if (iscircle){
	gen centre,rayon;
	if (!centre_rayon(circle,centre,rayon,false,contextptr))
	  return vecteur(1,gensizeerr(contextptr)); // don't care about radius sign
	gen rec,imc,rer,imr,ref,imf;
	rec=re(centre,contextptr);
	imc=im(centre,contextptr);
	rer=re(rayon,contextptr);
	imr=im(rayon,contextptr);
	ref=re(vf[0],contextptr);
	imf=im(vf[0],contextptr);
	eq=pow(ref-rec,2,contextptr)+pow(imf-imc,2,contextptr)-rer*rer-imr*imr;
      }
      else { // line
	gen A(circle._VECTptr->front()),B(circle._VECTptr->back());
	gen reA,imA,reB,imB,ref,imf;
	reA=re(A,contextptr);
	imA=im(A,contextptr);
	reB=re(B,contextptr);
	imB=im(B,contextptr);
	ref=re(vf[0],contextptr);
	imf=im(vf[0],contextptr);
	eq=(reA-ref)*(imA-imB)-(imA-imf)*(reA-reB);
      }
      eq=ratnormal(eq);
      vecteur num,den;
      prod2frac(eq,num,den);
      eq=vecteur2prod(num);
      eq=normal(eq,contextptr);
      vecteur res=solve(eq,*vf[1]._IDNTptr,0,contextptr);
      int s=res.size();
      for (int i=0;i<s;++i)
	res[i]=symb_pnt(subst(vf[0],vf[1],res[i],false,contextptr),contextptr);
      return remove_not_in_arc(res,circle,contextptr);
#ifndef NO_STDEXCEPT
    } catch (std::runtime_error & ){
      *logptr(contextptr) << gettext("Unable to solve intersection equation ") << eq << endl;
      return makevecteur(symbolic(at_inter,makesequence(curve,circle)));
    }
#endif
  }

  static vecteur equationintercurve(const gen & at_orig,const gen & t,const gen & b,const gen & bu_orig,const gen & u,GIAC_CONTEXT){
    gen bu=bu_orig,at=at_orig;
    gen m,tmin,tmax; double T=1e300;
    if (find_curve_parametrization(b,m,u,T,tmin,tmax,false,contextptr)){
      bu=m;
    }
    bool approx=has_num_coeff(bu) || has_num_coeff(at);
    if (approx){
      bu=exact(bu,contextptr);
      at=exact(at,contextptr);
    }
    // at has a rational parametrization, find cartesian equation
    if (u.type!=_IDNT)
      return vecteur(1,gensizeerr(contextptr));
    gen x("x__"+print_INT_(rand()),contextptr),y("y__"+print_INT_(rand()),contextptr);
    gen eq=rationalparam2equation(at,t,x,y,contextptr),tmp;
    if (is_undef(eq))
      return vecteur(1,eq);
    // replace bu inside and solve for u
    eq=subst(eq,makevecteur(x,y),makevecteur(re(bu,contextptr),im(bu,contextptr)),false,contextptr);
    vecteur v=lvar(eq);
    fraction f=e2r(eq,v,contextptr);
    fxnd(f,eq,tmp);
    if (approx)
      eq=evalf(eq,1,contextptr);
    eq=r2e(eq,v,contextptr);
    vecteur res;
#ifndef NO_STDEXCEPT
    try {
#endif
      res=solve(eq,*u._IDNTptr,0,contextptr);
      iterateur it=res.begin(),itend=res.end();
      for (;it!=itend;++it){
	*it=subst(bu,u,*it,false,contextptr);
      }
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & ) {
    }
#endif
    return res;
  }

  vecteur inter(const gen & aa,const gen & bb,GIAC_CONTEXT){
    if (aa.type==_VECT){
      if (bb.type==_VECT){
	vecteur res;
	const_iterateur it=aa._VECTptr->begin(),itend=aa._VECTptr->end();
	for (;it!=itend;++it){
	  res=mergevecteur(res,inter(*it,bb,contextptr));
	}
	return res;
      }
      return inter(bb,aa,contextptr);
    }
    if (bb.type==_VECT){
      vecteur res;
      const_iterateur it=bb._VECTptr->begin(),itend=bb._VECTptr->end();
      for (;it!=itend;++it){
	res=mergevecteur(res,inter(aa,*it,contextptr));
      }
      return res;
    }
    gen a=remove_at_pnt(aa);
    gen b=remove_at_pnt(bb);
    bool asp=a.is_symb_of_sommet(at_hypersphere),bsp=b.is_symb_of_sommet(at_hypersphere);
    bool ac=a.is_symb_of_sommet(at_cercle),bc=b.is_symb_of_sommet(at_cercle);
    if (a.type==_VECT){
      if (a.subtype==_POLYEDRE__VECT)
	return interpolyedre(*a._VECTptr,bb,contextptr);
      int as=a._VECTptr->size();
      if (as>2)
	return interpolygone(*a._VECTptr,bb,contextptr);
      if (as==2){ 
	if (b.type==_VECT){
	  int bs=b._VECTptr->size();
	  if (bs>2)
	    return interpolygone(*b._VECTptr,aa,contextptr);
	  if (bs==2){ // D inter D'
	    gen a1=a._VECTptr->front(),a2=a._VECTptr->back(),b1=b._VECTptr->front(),b2=b._VECTptr->back();
	    gen v(a2-a1),w(b2-b1);
	    if (v.type==_VECT){
	      if (w.type!=_VECT || v._VECTptr->size()!=3 || w._VECTptr->size()!=3)
		return vecteur(1,gensizeerr(contextptr));
	      return inter2droites3(a1,a2,b1,b2,a.subtype,b.subtype,contextptr);
	    }
	    return inter2droites2(a1,a2,b1,b2,a.subtype,b.subtype,contextptr);
	  } // end bs==2
	} // end b.type==_VECT
	if (bc || bsp)
	  return interdroitecercle(a,b,contextptr);
	if (b.is_symb_of_sommet(at_hyperplan)) // n-d line inter hyperplan
	  return interdroitehyperplan(a,b,contextptr);
      } // end droite inter something (as==2)
    }
    if (b.type==_VECT) {
      if (b.subtype==_POLYEDRE__VECT)
	return interpolyedre(*b._VECTptr,aa,contextptr);
      int bs=b._VECTptr->size();
      if (bs>2)
	return interpolygone(*b._VECTptr,aa,contextptr);
      if (bs==2){
	if (a.is_symb_of_sommet(at_hyperplan))
	  return interdroitehyperplan(b,a,contextptr);      
	if (ac || asp)
	  return interdroitecercle(b,a,contextptr);
      }
    }
    if ( (ac && bc) || (asp && bsp) ){
      gen centre_a,rayon_a,centre_b,rayon_b;
      if (!centre_rayon(a,centre_a,rayon_a,false,contextptr) || !centre_rayon(b,centre_b,rayon_b,false,contextptr))
	return vecteur(1,gensizeerr(contextptr));
      vecteur res=inter2cercles_or_spheres(centre_a,abs_norm2(rayon_a,contextptr),centre_b,abs_norm2(rayon_b,contextptr),ac,contextptr);
      return (ac && bc)?remove_not_in_arc(remove_not_in_arc(res,a,contextptr),b,contextptr):res;
    }
    if (a.is_symb_of_sommet(at_curve) && bc)
      return curveintercircle(a,b,true,contextptr);
    if (b.is_symb_of_sommet(at_curve) && ac)
      return curveintercircle(b,a,true,contextptr);
    // Now replace line and circles by curve
    if ((a.type==_VECT) && (a._VECTptr->size()==2)){
      if (b.is_symb_of_sommet(at_curve))
	return curveintercircle(b,a,false,contextptr);
      a=line2curve(a);
    }
    if ((b.type==_VECT) && (b._VECTptr->size()==2)){
      if (a.is_symb_of_sommet(at_curve))
	return curveintercircle(a,b,false,contextptr);
      b=line2curve(b);
    }
    if (ac)
      a=cercle2curve(a,contextptr);
    if (bc)
      b=cercle2curve(b,contextptr);
    if (is_undef(a)||is_undef(b))
      return vecteur(1,a+b);
    if (a.is_symb_of_sommet(at_hyperplan)){
      if (b.is_symb_of_sommet(at_hyperplan))
	return interhyperplan(a,b,contextptr);
      if (b.is_symb_of_sommet(at_hypersphere))
	return interplansphere(a,b,contextptr);
      return inter(bb,aa,contextptr);
    }
    if (b.is_symb_of_sommet(at_hyperplan)){
      if (a.is_symb_of_sommet(at_hypersphere))
	return interplansphere(b,a,contextptr);      
      b=hyperplan2hypersurface(b);
      if (is_undef(b))
	return vecteur(1,b);
    }
    if (asp)
      a=hypersphere2hypersurface(a);
    if (is_undef(a))
      return vecteur(1,a);
    if (bsp)
      b=hypersphere2hypersurface(b);
    if (is_undef(b))
      return vecteur(1,b);
    if (a.is_symb_of_sommet(at_hypersurface) && b.is_symb_of_sommet(at_curve))
      return interhypersurfacecurve(a,b,contextptr);
    if (b.is_symb_of_sommet(at_hypersurface) && a.is_symb_of_sommet(at_curve))
      return interhypersurfacecurve(b,a,contextptr);
    if (a.is_symb_of_sommet(at_hypersurface) && b.is_symb_of_sommet(at_hypersurface))
      return inter2hypersurface(a,b,contextptr);
    if ( (a.type==_SYMB) && (a._SYMBptr->sommet==at_curve) && (b.type==_SYMB) && (b._SYMBptr->sommet==at_curve) ){ // curve inter curve
      gen fa,fb;
      if ((fa=a._SYMBptr->feuille).type!=_VECT || (fb=b._SYMBptr->feuille).type!=_VECT || fa._VECTptr->empty() || fb._VECTptr->empty() || fa._VECTptr->front().type!=_VECT || fb._VECTptr->front().type!=_VECT)
	return vecteur(1,gensizeerr(contextptr));
      vecteur va=*fa._VECTptr->front()._VECTptr;
      vecteur vb=*fb._VECTptr->front()._VECTptr;
      if (va.size()<2 || vb.size()<2 || va[1].type!=_IDNT || vb[1].type!=_IDNT)
	return vecteur(1,gensizeerr(contextptr));
      gen m,tmin,tmax; double T=1e300;
      if (find_curve_parametrization(ac?remove_at_pnt(aa):a,m,va[1],T,tmin,tmax,false,contextptr)){
	return equationintercurve(m,va[1],b,vb[0],vb[1],contextptr);
	va[0]=m;
      }
      if (find_curve_parametrization(bc?remove_at_pnt(bb):b,m,vb[1],T,tmin,tmax,false,contextptr)){
	return equationintercurve(m,vb[1],a,va[0],va[1],contextptr);
	vb[0]=m;
      }
      if (va[1]==vb[1]){
	gen newvb(va[1].print(contextptr)+print_INT_(rand()),contextptr);
	vb[0]=subst(vb[0],vb[1],newvb,true,contextptr);
	vb[1]=newvb;
      }
      gen xa=re(va[0],contextptr),ya=im(va[0],contextptr);
      rewrite_with_t_real(xa,va[1],contextptr);
      rewrite_with_t_real(ya,va[1],contextptr);
      gen xb=re(vb[0],contextptr),yb=im(vb[0],contextptr);
      rewrite_with_t_real(xb,vb[1],contextptr);
      rewrite_with_t_real(yb,vb[1],contextptr);
      gen eq=xa-xb;
      vecteur sol,res;
#ifndef NO_STDEXCEPT
      try {
#endif
	sol=solve(eq,*va[1]._IDNTptr,0,contextptr); 
	bool exchanged=sol.empty();
	if (exchanged)
	  sol=solve(eq,*vb[1]._IDNTptr,0,contextptr);
	const_iterateur it=sol.begin(),itend=sol.end();
	for (;it!=itend;++it){
	  eq=subst(yb-ya,(exchanged?vb:va)[1],*it,false,contextptr);
	  res=mergevecteur(res,solve(eq,*(exchanged?va:vb)[1]._IDNTptr,0,contextptr)); 
	}
	sol.clear();
	it=res.begin(); itend=res.end();
	for (;it!=itend;++it){
	  sol.push_back(symb_pnt(subst((exchanged?va:vb)[0],(exchanged?va:vb)[1],*it,false,contextptr),default_color(contextptr),contextptr));
	}
	return sol;
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error &){
	return makevecteur(symbolic(at_inter,makesequence(a,b)));
      }
#endif
    }
    return makevecteur(symbolic(at_inter,makesequence(a,b)));
  }

  // args=[curve, parameter] or element(curve)
  static gen tangent(const gen & args,GIAC_CONTEXT){
    gen arg=args;
    if (arg.type!=_VECT){
      // check if arg is an element of a curve
      if ( (arg.type==_SYMB) && (arg._SYMBptr->sommet==at_pnt)){
	arg=(*arg._SYMBptr->feuille._VECTptr)[1];
	if (arg.type==_VECT)
	  arg=(*arg._VECTptr)[1];
      }
    }
    if (arg.type!=_VECT || arg._VECTptr->empty())
      return gensizeerr(contextptr);
    vecteur & argv=*arg._VECTptr;
    int s=argv.size()-1;
    vecteur result,parameqs;
    gen t=argv.back();
    for (int i=0;i<s;++i){
      gen curve=remove_at_pnt(argv[i]);
      if (curve.type==_VECT && !curve._VECTptr->empty() && curve._VECTptr->back().is_symb_of_sommet(at_pnt))
	curve=remove_at_pnt(curve._VECTptr->front());
      gen p=_parameq(curve,contextptr);
      if (equalposcomp(parameqs,p))
	continue;
      if (curve.type==_VECT){
	result.push_back(symb_pnt(curve,contextptr));
	continue;
      }
      if ( (curve.type==_SYMB) && (curve._SYMBptr->sommet==at_cercle || curve._SYMBptr->sommet==at_hypersphere)){
	gen centre,rayon;
	if (!centre_rayon(curve,centre,rayon,false,contextptr))
	  continue;
	rayon=ratnormal(rayon);
	if ( t.type==_VECT || t.is_symb_of_sommet(at_pnt) || !is_zero(im(t,contextptr)) ){
	  // tangent to cercle via point
	  t=remove_at_pnt(t);
	  if (t.type==_SYMB && equalposcomp(plot_sommets,t._SYMBptr->sommet))
	    return gensizeerr(contextptr);
	  gen OA=t-centre;
	  gen rayonb2(normal(abs_norm2(OA,contextptr)-rayon*conj(rayon,contextptr),contextptr));
	  if (is_zero(rayonb2)){
	    if (OA.type==_VECT)
	      result.push_back(_plan(makesequence(OA,t),contextptr));
	    else
	      result.push_back(_droite(makesequence(t,t+im(OA,contextptr)-cst_i*re(OA,contextptr)),contextptr));
	    continue;
	  }
	  if (is_positive(-rayonb2,contextptr))
	    continue;
	  if (OA.type==_VECT)
	    return gensizeerr(contextptr);
	  vecteur v=inter2cercles_or_spheres(centre,rayon*conj(rayon,contextptr),t,rayonb2,true,contextptr); // inter(_cercle(makevecteur(symb_pnt(t,contextptr),sqrt(rayonb2)),contextptr),curve,contextptr);
	  if (!v.empty()){
	    if (is_undef(v.front()))
	      return v.front();
	    result.push_back(_droite(makesequence(t,v.front()),contextptr));
	    result.push_back(_droite(makesequence(t,v.back()),contextptr));
	  }
	}
	else {
	  t=centre+rayon*exp(cst_i*t,contextptr);
	  result.push_back(_perpendiculaire(makesequence(t,t,centre),contextptr));
	}
	continue;
      }
      if (curve.is_symb_of_sommet(at_hyperplan)){
	result.push_back(curve);
	continue;
      }
      if (curve.is_symb_of_sommet(at_hypersurface)){
	gen & curvef=curve._SYMBptr->feuille;
	if (curvef.type!=_VECT || curvef._VECTptr->size()<3)
	  return gensizeerr(contextptr);
	vecteur curvev=*curvef._VECTptr;
	gen eq(undef),vars;
	if (curvev[1].type!=_VECT){
	  eq=curvev[1];
	  vars=curvev[2];
	  curvev=*curvev[0]._VECTptr;
	}
	if (t.type==_VECT){ // parameters values
	  gen f=curvev[0];
	  vars=curvev[1];
	  gen fprime=derive(f,vars,contextptr);
	  if (is_undef(fprime))
	    return fprime;
	  fprime=ratnormal(subst(fprime,vars,t,false,contextptr));
	  t=ratnormal(subst(f,vars,t,false,contextptr));
	  // make hyperplan by t with normal orthogonal to fprime
	  if (!ckmatrix(fprime) || fprime._VECTptr->size()!=2)
	    return gensizeerr(contextptr);
	  vecteur n(cross(*fprime._VECTptr->front()._VECTptr,*fprime._VECTptr->back()._VECTptr,contextptr));
	  result.push_back(symbolic(at_hyperplan,makesequence(n,t)));
	  continue;
	}
	if (is_undef(eq))
	  return gensizeerr(gettext("Hypersurface w/o equation not implemented"));
	t=remove_at_pnt(t);
	// Check that the point is on the hypersurface (with the equation)
	gen res=simplify(subst(eq,vars,t,false,contextptr),contextptr);
	if (!is_zero(res))
	  return gensizeerr(gettext("f(t)!=0:")+res.print(contextptr));
	gen fprime(derive(eq,vars,contextptr));
	if (is_undef(fprime))
	  return fprime;
	fprime=ratnormal(subst(fprime,vars,t,false,contextptr));
	fprime=fprime/abs_norm(fprime,contextptr);
	result.push_back(symbolic(at_hyperplan,makesequence(fprime,t)));
	continue;
      }
      if ( (curve.type!=_SYMB) || (curve._SYMBptr->sommet!=at_curve))
	return gensizeerr(contextptr);
      vecteur v=*curve._SYMBptr->feuille._VECTptr->front()._VECTptr;
      gen direction(derive(v[0],*v[1]._IDNTptr,contextptr));
      if (is_undef(direction))
	return direction;
      if ( (t.type==_SYMB) && (t._SYMBptr->sommet==at_pnt)){
	if (t._SYMBptr->feuille[1].type==_VECT){
	  vecteur tv=*t._SYMBptr->feuille[1]._VECTptr;
	  if (tv.size()>=2){
	    gen M=tv[1];
	    if (M.type==_VECT && M._VECTptr->size()==2){
	      gen Mon=M._VECTptr->front();
	      gen Mt=M._VECTptr->back();
	      if (remove_at_pnt(Mon)==curve){
		direction=subst(direction,v[1],Mt,false,contextptr);
		M=t._SYMBptr->feuille[0]; // remove_at_pnt(t);
		return _droite_segment(gen(makevecteur(M,M+direction),_SEQ__VECT),_LINE__VECT,vecteur(1,default_color(contextptr)),contextptr);
	      }
	    }
	  }
	}
	gen M=remove_at_pnt(t);
	gen m,tmin,tmax;
	double T=1;
	if (v[1].type==_IDNT && find_curve_parametrization(curve,m,v[1],T,tmin,tmax,false,contextptr)){
	  if (re(m,contextptr)==v[1]){
	    gen r=re(M,contextptr);
	    // check that M is on the curve? disabled
	    if (true || is_zero(normal(M-subst(m,v[1],r,false,contextptr),contextptr))){
	      direction=subst(direction,v[1],r,false,contextptr);
	      result.push_back(_droite(makevecteur(M,M+direction),contextptr));
	      return result;
	    }
	  }
	  vecteur mv=rlvarx(m,v[1]);
	  if (mv.empty() || (mv.size()==1 && mv.front()==v[1])){
	    gen x(" x",contextptr),y(" y",contextptr);
	    gen eq=rationalparam2equation(m,v[1],x,y,contextptr);
	    if (is_undef(eq))
	      return eq;
	    // contact point N(x,y) must verify eq and M-N normal to diff(eq,x),diff(eq,y)
	    gen Mx=re(M,contextptr),My=im(M,contextptr);
	    gen eqx=derive(eq,x,contextptr),eqy=derive(eq,y,contextptr);
	    if (is_undef(eqx) || is_undef(eqy))
	      return eqx+eqy;
	    gen eq2=(Mx-x)*eqx+(My-y)*eqy;
	    vecteur sols=gsolve(makevecteur(eq,eq2),makevecteur(x,y),false,contextptr);
	    if (is_undef(sols))
	      return sols;
	    // build tangents
	    vecteur res;
	    const_iterateur it=sols.begin(),itend=sols.end();
	    for (;it!=itend;++it){
	      gen N=it->_VECTptr->front()+cst_i*it->_VECTptr->back();
	      if (!is_zero(recursive_normal(M-N,contextptr)))
		res.push_back(_droite(gen(makevecteur(M,N),_SEQ__VECT),contextptr));
	      else {
		gen dir=subst(eqy-cst_i*eqx,makevecteur(x,y),*it,false,contextptr);
		res.push_back(_droite(gen(makevecteur(N,N+dir),_SEQ__VECT),contextptr));
	      }
	    }
	    if (res.size()==1)
	      return res.front();
	    return res;
	  }
	} // end if (v[1].type==_IDNT ...)
	gen xt(re(v[0],contextptr)),yt(im(v[0],contextptr)),xp(re(direction,contextptr)),yp(im(direction,contextptr));
	gen var=v[1];
	rewrite_with_t_real(xt,var,contextptr);
	rewrite_with_t_real(yt,var,contextptr);
	rewrite_with_t_real(xp,var,contextptr);
	rewrite_with_t_real(yp,var,contextptr);
	// for plotfunc, check if var=re(M)
	gen test1=recursive_normal(subst(yt,var,re(M,contextptr),false,contextptr)-im(M,contextptr),contextptr),test2=recursive_normal(subst(xt,var,re(M,contextptr),false,contextptr)-re(M,contextptr),contextptr);
	if (is_zero(test1) && is_zero(test2)){
	  direction=subst(direction,var,re(M,contextptr),false,contextptr);
	  result.push_back(_droite(makevecteur(M,M+direction),contextptr));
	  continue;
	}
	vecteur sol;
	gen lambda=xp*(yt-im(M,contextptr))-yp*(xt-re(M,contextptr));
#ifndef NO_STDEXCEPT
	try {
#endif
	  // find t such that x'(t)*(M_y-y(t)) = y'(t)*(M_x-x(t))
	  sol=solve( lambda,*var._IDNTptr,0,contextptr);
#ifndef NO_STDEXCEPT
	}
	catch (std::runtime_error & error ){
	  *logptr(contextptr) << error.what() << endl;
	  sol.clear();
	}
#endif
	iterateur it=sol.begin(),itend=sol.end();
	for (;it!=itend;++it){
	  if (is_undef(*it))
	    return gensizeerr(gettext("Unable to solve"));
	  gen Mt=subst(v[0],var,*it,false,contextptr); // point on the curve
	  if (is_zero(normal(M-Mt,contextptr))){
	    direction=subst(direction,var,*it,false,contextptr);
	    result.push_back(_droite(makesequence(M,M+direction),contextptr));
	    continue;
	  }
	  result.push_back(_droite(makesequence(M,Mt),contextptr));
	}
	continue;
      }
      else {
	direction=subst(direction,v[1],t,false,contextptr);
	gen point(subst(v[0],v[1],t,false,contextptr));
	result.push_back(_droite(makesequence(point,point+direction),contextptr));
	continue;
      }
    }
    if (result.size()==1)
      return result.front();
    return gen(result,_GROUP__VECT);
  }
  gen _tangent(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    if (s==1)
      return put_attributs(tangent(v.front(),contextptr),attributs,contextptr);
    gen res=tangent(gen(vecteur(v.begin(),v.begin()+s),args.subtype),contextptr);
    if (res.type==_VECT && res._VECTptr->size()==1)
      res=res._VECTptr->front();
    return put_attributs(res,attributs,contextptr);
  }
  static const char _tangent_s []="tangent";
  static define_unary_function_eval (__tangent,&giac::_tangent,_tangent_s);
  define_unary_function_ptr5( at_tangent ,alias_at_tangent,&__tangent,0,true);

  static bool is_valid_point(const gen & g){
    if (is_undef(g))
      return false;
    if (g.type==_VECT){
      vecteur & v = *g._VECTptr;
      int s=v.size();
      if (s!=3)
	return false;
      if (v.front().type==_VECT)
	return false;
    }
    return true;
  }
  static bool foyers_a(const gen & args,gen & F1,gen & F2, gen & a,GIAC_CONTEXT){
    gen FF=remove_at_pnt(args._VECTptr->front());
    if (FF.type==_VECT && FF._VECTptr->size()!=3){
      if (FF._VECTptr->size()!=2)
	return false; // setsizeerr(contextptr);
      F1=FF._VECTptr->front();
      F2=FF._VECTptr->back();
    }
    else {
      if (args._VECTptr->size()!=3)
	return false; // setsizeerr(contextptr);
      F1=FF;
      F2=remove_at_pnt((*args._VECTptr)[1]);
    }
    a=args._VECTptr->back();
    a=remove_at_pnt(get_point(a,0,contextptr));
    F1=remove_at_pnt(get_point(F1,0,contextptr));
    F2=remove_at_pnt(get_point(F2,0,contextptr));
    if (!is_valid_point(a) || !is_valid_point(F1) || !is_valid_point(F2))
      return false;
    return true;
  }

  static gen xy2eitheta(const gen & x,const gen & y,GIAC_CONTEXT){
    if (is_zero(x))
      return cst_i;
    gen t=rdiv(y,x,contextptr);
    t=rdiv(plus_one+cst_i*t,sqrt(plus_one+t*t,contextptr),contextptr);
    if (!is_positive(x,contextptr)) 
      return -t;
    else
      return t;
  }

  gen _ellipse(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    ck_parameter_t(contextptr);
    if (args.type!=_VECT)
      return _plotimplicit(args,contextptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    if (s==1)
      return _plotimplicit(args,contextptr);
    // args=[F,F',M] or [F,F',a] / FM+F'M=2*a
    gen F1,F2,a,n;
    gen aorig=*(args._VECTptr->begin()+s-1);
    if (!foyers_a(vecteur(args._VECTptr->begin(),args._VECTptr->begin()+s),F1,F2,a,contextptr))
      return gensizeerr(contextptr);
    if ( aorig.is_symb_of_sommet(at_pnt) || a.type==_VECT || !is_zero(im(a,contextptr)) ){
      a=remove_at_pnt(get_point(remove_at_pnt(a),0,contextptr));
      if (is_undef(a)) return a;
      if (a.type==_VECT)
	n=a;
      a=rdiv(abs_norm(a-F1,contextptr)+abs_norm(a-F2,contextptr),plus_two,contextptr);
    }
    gen F1F2=F2-F1;
    gen O=rdiv(F1+F2,plus_two,contextptr);
    gen c2=rdiv(F1F2.squarenorm(contextptr),gen(4),contextptr);
    gen b=sqrt(a*a-c2,contextptr),res;
#if 0 // def GIAC_HAS_STO_38
    gen theta=vx_var;
#else
    gen theta=t__IDNT_e;
#endif
    if (!angle_radian(contextptr))
      theta=gen(180)/cst_pi*theta;
    if (n.type==_VECT){ // 3-d
      n=n-O;
      n=cross(cross(F1F2,n,contextptr),F1F2,contextptr);
      res=O+a*symb_cos(theta)*F1F2/abs_norm(F1F2,contextptr)+b*symb_sin(theta)*n/abs_norm(n,contextptr);
    }
    else { // 2-d
      gen xF1F2=re(F1F2,contextptr),yF1F2=im(F1F2,contextptr);
      gen eitheta(xy2eitheta(xF1F2,yF1F2,contextptr));
      res=eitheta*(a*symb_cos(theta)+b*cst_i*symb_sin(theta))+O;
      gen r,i;
      reim(res,r,i,contextptr);
      res=makevecteur(r,i);
    }
    gen ustep=_USTEP;
    ustep.subtype=_INT_PLOT;
    gen nstep=_NSTEP;
    nstep.subtype=_INT_PLOT;
#if 0 // def GIAC_HAS_STO_38
    return _paramplot(gen(makevecteur(res,symb_equal(vx_var,symb_interval(0,2*cst_pi)),symb_equal(nstep,60),symb_equal(ustep,M_PI/30),symbolic(at_equal,makesequence(at_display,attributs[0]))),_SEQ__VECT),contextptr);
#else
    return _paramplot(gen(makevecteur(res,symb_equal(t__IDNT_e,symb_interval(0,2*cst_pi)),symb_equal(nstep,60),symb_equal(ustep,M_PI/30),symbolic(at_equal,makesequence(at_display,attributs[0]))),_SEQ__VECT),contextptr);
#endif
  }
  static const char _ellipse_s []="ellipse";
  static define_unary_function_eval (__ellipse,&giac::_ellipse,_ellipse_s);
  define_unary_function_ptr5( at_ellipse ,alias_at_ellipse,&__ellipse,0,true);

  gen _hyperbole(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    ck_parameter_t(contextptr);
    if (args.type!=_VECT)
      return _plotimplicit(args,contextptr);
    // args=[F,F',M] or [F,F',a] / FM+F'M=2*a
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    if (s==1)
      return _plotimplicit(args,contextptr);
    gen F1,F2,a,n;
    gen aorig=*(args._VECTptr->begin()+s-1);
    if (!foyers_a(vecteur(args._VECTptr->begin(),args._VECTptr->begin()+s),F1,F2,a,contextptr))
      return gensizeerr(contextptr);
    if ( aorig.is_symb_of_sommet(at_pnt) || a.type==_VECT || !is_zero(im(a,contextptr)) ){
      a=remove_at_pnt(get_point(remove_at_pnt(a),0,contextptr));
      if (is_undef(a)) return a;
      if (a.type==_VECT)
	n=a;
      a=rdiv(abs_norm(a-F1,contextptr)-abs_norm(a-F2,contextptr),plus_two,contextptr);
    }
    gen F1F2=F2-F1;
    gen O=rdiv(F1+F2,plus_two,contextptr);
    gen c2=rdiv(F1F2.squarenorm(contextptr),gen(4),contextptr);
    gen b=sqrt(c2-a*a,contextptr),res,res1,res2;
#if 0 // def GIAC_HAS_STO_38
    gen theta=vx_var;
#else
    gen theta=t__IDNT_e;
#endif
    if (n.type==_VECT){
      n=n-O;
      n=cross(cross(F1F2,n,contextptr),F1F2,contextptr);
      res1=a*cosh(theta,contextptr)*F1F2/abs_norm(F1F2,contextptr)+b*sinh(theta,contextptr)*n/abs_norm(n,contextptr);
      res2=-a*cosh(theta,contextptr)*F1F2/abs_norm(F1F2,contextptr)+b*sinh(theta,contextptr)*n/abs_norm(n,contextptr);
    }
    else {
      gen xF1F2=re(F1F2,contextptr),yF1F2=im(F1F2,contextptr);
      gen eitheta(xy2eitheta(xF1F2,yF1F2,contextptr));
      res=eitheta*(a*cosh(theta,contextptr)+b*cst_i*sinh(theta,contextptr));
      gen r,i;
      reim(res+O,r,i,contextptr);
      res1=makevecteur(r,i);
      reim(-res+O,r,i,contextptr);
      res2=makevecteur(r,i);
    }
    gen ustep=_USTEP;
    ustep.subtype=_INT_PLOT;
    gen nstep=_NSTEP;
    nstep.subtype=_INT_PLOT;
#if 0 // def GIAC_HAS_STO_38
    return makevecteur(_paramplot(gen(makevecteur(res1,symb_equal(vx_var,symb_interval(-3,3)),symb_equal(nstep,60),symb_equal(ustep,0.1),symbolic(at_equal,makesequence(at_display,attributs[0]))),_SEQ__VECT),contextptr),_paramplot(gen(makevecteur(res2,symb_equal(vx_var,symb_interval(-3,3)),symb_equal(nstep,60),symb_equal(ustep,0.1),symbolic(at_equal,makesequence(at_display,attributs[0]))),_SEQ__VECT),contextptr)); // should be -inf..inf
#else
    return makevecteur(_paramplot(gen(makevecteur(res1,symb_equal(t__IDNT_e,symb_interval(-3,3)),symb_equal(nstep,60),symb_equal(ustep,0.1),symbolic(at_equal,makesequence(at_display,attributs[0]))),_SEQ__VECT),contextptr),_paramplot(gen(makevecteur(res2,symb_equal(t__IDNT_e,symb_interval(-3,3)),symb_equal(nstep,60),symb_equal(ustep,0.1),symbolic(at_equal,makesequence(at_display,attributs[0]))),_SEQ__VECT),contextptr)); // should be -inf..inf
#endif
  }
  static const char _hyperbole_s []="hyperbola";
  static define_unary_function_eval (__hyperbole,&giac::_hyperbole,_hyperbole_s);
  define_unary_function_ptr5( at_hyperbole ,alias_at_hyperbole,&__hyperbole,0,true);

  gen _parabole(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    ck_parameter_t(contextptr);
    vecteur attributs(1,default_color(contextptr));
    // args=[F,O] symmetry axe=OF (t+i*t^2)/OF
    // or args=[0,c] symmetry axe=Oy y=c*x^2 (t+i*t^2)/c or args=c
    gen O,F,c,eitheta=plus_one;
    vecteur v=gen2vecteur(args);
    int s=read_attributs(v,attributs,contextptr);
    v=vecteur(v.begin(),v.begin()+s);
    if (!s)
      return gendimerr(contextptr);
    if (s==1)
      return _plotimplicit(args,contextptr);
    F=remove_at_pnt(v.front());
    O=v[1];
    if ( O.is_symb_of_sommet(at_pnt) || !is_zero(im(O,contextptr)) ){
      gen OF=remove_at_pnt(O);
      if (OF.type==_VECT && OF._VECTptr->size()==2){
	// find the projection of F over O
	OF=projectionpoint(O,F,contextptr);
	OF=(remove_at_pnt(OF)+F)/2;
      }
      O=OF;
      OF=F-OF;
      c=abs_norm(OF,contextptr);
      if (OF.type!=_VECT){
	eitheta=(im(OF,contextptr)-cst_i*re(OF,contextptr))/c;
      }
      else {
	eitheta=remove_at_pnt(v[2])-O;
	eitheta=cross(cross(OF,eitheta,contextptr),OF,contextptr);
	eitheta=eitheta/abs_norm(eitheta,contextptr);
      }
    }
    else {
      c=O;
      O=F;
    }
    gen res;
#if 0 // def GIAC_HAS_STO_38
    gen theta=vx_var;
#else
    gen theta=t__IDNT_e;
#endif
    if (eitheta.type==_VECT){
      res=O+(F-O)/(4*c*abs_norm(F-O,contextptr))*pow(theta,2)+eitheta*theta;
    }
    else {
      res=O+eitheta*theta*(1+cst_i*theta/4/c);
      gen r,i;
      reim(res,r,i,contextptr);
      res=makevecteur(r,i);
    }
    gen ustep=_USTEP;
    ustep.subtype=_INT_PLOT;
    gen nstep=_NSTEP;
    nstep.subtype=_INT_PLOT;
#if 0 // def GIAC_HAS_STO_38
    res= _paramplot(gen(makevecteur(res,symb_equal(vx_var,symb_interval(-4,4)),symb_equal(nstep,60),symb_equal(ustep,0.15),symbolic(at_equal,makesequence(at_display,attributs[0]))),_SEQ__VECT),contextptr);
#else
    res= _paramplot(gen(makevecteur(res,symb_equal(t__IDNT_e,symb_interval(-4,4)),symb_equal(nstep,60),symb_equal(ustep,0.15),symbolic(at_equal,makesequence(at_display,attributs[0]))),_SEQ__VECT),contextptr);
#endif
    return res;
  }
  static const char _parabole_s []="parabola";
  static define_unary_function_eval (__parabole,&giac::_parabole,_parabole_s);
  define_unary_function_ptr5( at_parabole ,alias_at_parabole,&__parabole,0,true);

  gen put_attributs(const gen & lieu_g,const vecteur & attributs,GIAC_CONTEXT){
    if (is_undef(lieu_g))
      return lieu_g;
    if (lieu_g.is_symb_of_sommet(at_program))
      return lieu_g;
    gen lieu_geo=remove_at_pnt(lieu_g);
    if (!lieu_g.is_symb_of_sommet(at_pnt) && lieu_geo.type==_VECT && (lieu_geo.subtype<_LINE__VECT || lieu_geo.subtype>_HALFLINE__VECT) ){
      const_iterateur it=lieu_geo._VECTptr->begin(),itend=lieu_geo._VECTptr->end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it){
	res.push_back(put_attributs(*it,attributs,contextptr));
      }
      return gen(res,lieu_geo.subtype);
    }
    return pnt_attrib(lieu_geo,attributs,contextptr);
  }

  gen _conique(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return _plotimplicit(args,contextptr);
    vecteur attributs(1,default_color(contextptr));
    vecteur & v =*args._VECTptr;
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gendimerr(contextptr);
    if (s<=3)
      return _plotimplicit(args,contextptr);
    if (s==5){
      vecteur w(5),m(5),ligne(6);
      gen wx,wy;
      // find m such that m*[a,b,c,d,e,f]=0 where a*x^2+b*x*y+c*y^2+d*x+e*y+f=0
      for (int i=0;i<5;++i){
	w[i]=remove_at_pnt(v[i]);
	wx=re(w[i],contextptr);
	wy=im(w[i],contextptr);
	ligne[0]=wx*wx;
	ligne[1]=wx*wy;
	ligne[2]=wy*wy;
	ligne[3]=wx;
	ligne[4]=wy;
	ligne[5]=1;
	m[i]=ligne;
      }
      // find ker(m)
      gen me=m; // exact(m,contextptr);
      vecteur base;
      if (me.type==_VECT)
	base=mker(*me._VECTptr,contextptr);
      if (is_undef(base) || base.empty() || base.front().type!=_VECT || base.front()._VECTptr->size()!=6)
	return gensizeerr(gettext("Bug in conique reducing ")+gen(m).print(contextptr));
      vecteur & res = *base.front()._VECTptr;
      identificateur x(" x"),y(" y");
      gen eq=res[0]*x*x+res[1]*x*y+res[2]*y*y+res[3]*x+res[4]*y+res[5];
      gen g;
      if (equation2geo2d(eq,x,y,g,gnuplot_tmin,gnuplot_tmax,gnuplot_tstep,contextptr))
	return put_attributs(g,attributs,contextptr);
      else
	return gensizeerr(gettext("Bug in conique, equation ")+eq.print(contextptr));	
    }
    return gendimerr(contextptr);
  }
  static const char _conique_s []="conic";
  static define_unary_function_eval (__conique,&giac::_conique,_conique_s);
  define_unary_function_ptr5( at_conique ,alias_at_conique,&__conique,0,true);

  gen _legende(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    gen args=a.eval(1,contextptr);
    if ( (a.type==_SYMB) && (args.type!=_VECT)){
      vecteur v;
      gen e(a._SYMBptr->feuille);
      if ( (e.type==_VECT) && (!e._VECTptr->empty()) )
	e=e._VECTptr->front();
      v.push_back(e.eval(1,contextptr));
      v.push_back(args);
      args=v;
    }
    else {
      if ((args.type!=_VECT) || (args._VECTptr->size()<2))
	return symbolic(at_legende,args);
    }
    // gen decal=gen(5*(gnuplot_xmax-gnuplot_xmin)/plot_instructionsh+5*(gnuplot_ymax-gnuplot_ymin)/(plot_instructionsw-LEGENDE_SIZE))*cst_i;
    gen decal=0;
    int save_digits=decimal_digits(contextptr);
    int couleur=0;
    decimal_digits(contextptr)=3;
    string s;
    vecteur & v(*args._VECTptr);
    for (unsigned int i=1;i<v.size();++i){
      if (v[i].type==_STRNG)
	s += *v[i]._STRNGptr;
      else {
	if (v[i].type==_INT_ && v[i].subtype==_INT_COLOR){
	  couleur |= v[i].val;
	  continue;
	}
	if (v[i].is_symb_of_sommet(at_equal)){
	  gen & f = v[i]._SYMBptr->feuille;
	  if (f.type==_VECT && f._VECTptr->size()==2){
	    gen f2=evalf_double(f._VECTptr->back(),1,contextptr);
	    if (f._VECTptr->front()==at_couleur || f._VECTptr->front()==at_display){
	      if (f2.type==_DOUBLE_)
		couleur |= int(f2._DOUBLE_val);
	      if (f2.type==_INT_)
		couleur |= f2.val;
	      if (f2.type==_FLOAT_)
		couleur |= get_int(f2._FLOAT_val);
	      continue;
	    }
	  }
	}
	s += v[i].print(contextptr);
      }
    }
    decimal_digits(contextptr)=save_digits;
    gen position=v[0];
    if (position.type==_VECT && position._VECTptr->size()==2)
      return symb_pnt(symbolic(at_legende,makesequence(position,string2gen(s,false),couleur)),couleur,contextptr);
    position=remove_at_pnt(v[0]);
    if (position.type==_SYMB) {
      if (position._SYMBptr->sommet==at_cercle)
	position=position._SYMBptr->feuille;
      else 
	if (position._SYMBptr->sommet==at_curve){
	  vecteur v(*position._SYMBptr->feuille._VECTptr->front()._VECTptr);
	  position=subst(v[0],v[1],rdiv(v[2]+v[3],plus_two,contextptr),false,contextptr);
	}
    }
    if ( (position.type==_VECT) && (position._VECTptr->size()==2) ){
      position=rdiv(position._VECTptr->front()+position._VECTptr->back(),plus_two,contextptr);
    }
    gen sg=string2gen(s,false);
    if (v.size()<2)
      return symb_pnt_name(position+decal,couleur+_POINT_POINT,sg,contextptr);
    else
      return symb_pnt_name(position+decal,makevecteur(couleur+_POINT_POINT,v[1],sg),sg,contextptr);
  }
  static const char _legende_s []="legend";
  static define_unary_function_eval_quoted (__legende,&giac::_legende,_legende_s);
  define_unary_function_ptr5( at_legende ,alias_at_legende,&__legende,_QUOTE_ARGUMENTS,true);

  static void add_names(std::string & ss,const gen & v0,const gen & v1,GIAC_CONTEXT){
#ifdef GIAC_HAS_STO_38
    if (v0.type==_IDNT && v1.type==_IDNT){
      ss += v0._IDNTptr->id_name;
      if (strlen(v1._IDNTptr->id_name)==2 && v1._IDNTptr->id_name[0]=='G')
	ss += v1._IDNTptr->id_name[1];
      else
	ss += v1._IDNTptr->id_name;
    }
    else
      ss += v0.print(contextptr)+v1.print(contextptr);
#else
    ss += v0.print(contextptr)+v1.print(contextptr);
#endif
  }

  static void add_name(string & ss,const gen & v0,GIAC_CONTEXT){
#ifdef GIAC_HAS_STO_38
    if (v0.type==_IDNT && strlen(v0._IDNTptr->id_name)==2 && v0._IDNTptr->id_name[0]=='G')
      ss += v0._IDNTptr->id_name[1];
    else
      ss += v0.print(contextptr);
#else
    ss += v0.print(contextptr);
#endif
  }

  gen _distanceat(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT )
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    if (s<3)
      return gentypeerr(contextptr);
    gen l=sqrt(_longueur2(gen(makevecteur(eval(v[0],eval_level(contextptr),contextptr),eval(v[1],eval_level(contextptr),contextptr)),_SEQ__VECT),contextptr),contextptr);
    int save_digits=decimal_digits(contextptr);
    decimal_digits(contextptr)=3;
    string ss(1,'"');
    add_names(ss,v[0],v[1],contextptr);
    ss +='=';
    ss += l.print(contextptr);
    ss +=' ';
    ss +='"';
    decimal_digits(contextptr)=save_digits;
    l=string2gen(ss,false);
    vecteur w=makevecteur(v[2],l);
    for (int i=3; i<s;++i)
      w.push_back(v[i]);
    return _legende(gen(w,_SEQ__VECT),contextptr);
  }
  static const char _distanceat_s []="distanceat";
  static define_unary_function_eval_quoted (__distanceat,&_distanceat,_distanceat_s);
  define_unary_function_ptr5( at_distanceat ,alias_at_distanceat,&__distanceat,_QUOTE_ARGUMENTS,true);

  gen _distanceatraw(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT )
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    if (s<3)
      return gentypeerr(contextptr);
    gen l=sqrt(_longueur2(gen(makevecteur(v[0],v[1]),_SEQ__VECT),contextptr),contextptr);
    vecteur w=makevecteur(v[2],l);
    for (int i=3; i<s;++i)
      w.push_back(v[i]);
    return _legende(gen(w,_SEQ__VECT),contextptr);
  }
  static const char _distanceatraw_s []="distanceatraw";
  static define_unary_function_eval (__distanceatraw,&_distanceatraw,_distanceatraw_s);
  define_unary_function_ptr5( at_distanceatraw ,alias_at_distanceatraw,&__distanceatraw,0,true);

  gen _areaatraw(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT )
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    if (s<2)
      return gentypeerr(contextptr);
    gen l=_aire(v[0],contextptr);
    vecteur w=makevecteur(v[1],l);
    for (int i=2; i<s;++i)
      w.push_back(v[i]);
    return _legende(gen(w,_SEQ__VECT),contextptr);
  }
  static const char _areaatraw_s []="areaatraw";
  static define_unary_function_eval (__areaatraw,&_areaatraw,_areaatraw_s);
  define_unary_function_ptr5( at_areaatraw ,alias_at_areaatraw,&__areaatraw,0,true);

  gen _areaat(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT )
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    if (s<2)
      return gentypeerr(contextptr);
    gen l=_aire(eval(v[0],eval_level(contextptr),contextptr),contextptr);
    int save_digits=decimal_digits(contextptr);
    decimal_digits(contextptr)=3;
    string ss="\"a";
    add_name(ss,v[0],contextptr);
    ss +="="+l.print(contextptr)+" \"";
    // string ss="◼"+v[0].print(contextptr)+"="+l.print(contextptr)+" ";
    decimal_digits(contextptr)=save_digits;
    l=string2gen(ss,false);
    vecteur w=makevecteur(v[1],l);
    for (int i=2; i<s;++i)
      w.push_back(v[i]);
    return _legende(gen(w,_SEQ__VECT),contextptr);
  }
  static const char _areaat_s []="areaat";
  static define_unary_function_eval_quoted (__areaat,&_areaat,_areaat_s);
  define_unary_function_ptr5( at_areaat ,alias_at_areaat,&__areaat,_QUOTE_ARGUMENTS,true);

  gen _slopeatraw(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT )
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    if (s<2)
      return gentypeerr(contextptr);
    gen l=_slope(v[0],contextptr);
    vecteur w=makevecteur(v[1],l);
    for (int i=2; i<s;++i)
      w.push_back(v[i]);
    return _legende(gen(w,_SEQ__VECT),contextptr);
  }
  static const char _slopeatraw_s []="slopeatraw";
  static define_unary_function_eval (__slopeatraw,&_slopeatraw,_slopeatraw_s);
  define_unary_function_ptr5( at_slopeatraw ,alias_at_slopeatraw,&__slopeatraw,0,true);

  gen _slopeat(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT )
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    if (s<2)
      return gentypeerr(contextptr);
    gen l=_slope(eval(v[0],eval_level(contextptr),contextptr),contextptr);
    int save_digits=decimal_digits(contextptr);
    decimal_digits(contextptr)=3;
    // string ss="∡"+v[0].print(contextptr)+"="+l.print(contextptr)+" ";
    string ss="\"s";
    add_name(ss,v[0],contextptr);
    ss += "="+l.print(contextptr)+" \"";
    decimal_digits(contextptr)=save_digits;
    l=string2gen(ss,false);
    vecteur w=makevecteur(v[1],l);
    for (int i=2; i<s;++i)
      w.push_back(v[i]);
    return _legende(gen(w,_SEQ__VECT),contextptr);
  }
  static const char _slopeat_s []="slopeat";
  static define_unary_function_eval_quoted (__slopeat,&_slopeat,_slopeat_s);
  define_unary_function_ptr5( at_slopeat ,alias_at_slopeat,&__slopeat,_QUOTE_ARGUMENTS,true);

  gen _perimeterat(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT )
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    if (s<2)
      return gentypeerr(contextptr);
    gen l=_perimetre(eval(v[0],eval_level(contextptr),contextptr),contextptr);
    int save_digits=decimal_digits(contextptr);
    decimal_digits(contextptr)=3;
    string ss="\"p";
    add_name(ss,v[0],contextptr);
    ss += "="+l.print(contextptr)+" \"";
    // string ss="◻"+v[0].print(contextptr)+"="+l.print(contextptr)+" ";
    decimal_digits(contextptr)=save_digits;
    l=string2gen(ss,false);
    vecteur w=makevecteur(v[1],l);
    for (int i=2; i<s;++i)
      w.push_back(v[i]);
    return _legende(gen(w,_SEQ__VECT),contextptr);
  }
  static const char _perimeterat_s []="perimeterat";
  static define_unary_function_eval_quoted (__perimeterat,&_perimeterat,_perimeterat_s);
  define_unary_function_ptr5( at_perimeterat ,alias_at_perimeterat,&__perimeterat,_QUOTE_ARGUMENTS,true);

  gen _perimeteratraw(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT )
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    if (s<2)
      return gentypeerr(contextptr);
    gen l=_perimetre(v[0],contextptr);
    vecteur w=makevecteur(v[1],l);
    for (int i=2; i<s;++i)
      w.push_back(v[i]);
    return _legende(gen(w,_SEQ__VECT),contextptr);
  }
  static const char _perimeteratraw_s []="perimeteratraw";
  static define_unary_function_eval (__perimeteratraw,&_perimeteratraw,_perimeteratraw_s);
  define_unary_function_ptr5( at_perimeteratraw ,alias_at_perimeteratraw,&__perimeteratraw,0,true);

  gen _extract_measure(const gen & valeur,GIAC_CONTEXT){
    if ( valeur.type==_STRNG && valeur.subtype==-1) return  valeur;
    gen tmp=valeur;
    if (valeur.is_symb_of_sommet(at_pnt)){
      gen & valf = valeur._SYMBptr->feuille;
      if (valf.type==_VECT){
	vecteur & valv = *valf._VECTptr;
	int s=valv.size();
	if (s>1){
	  gen valv1=valv[1];
	  if (valv1.type==_VECT && valv1._VECTptr->size()>2){
	    tmp=(*valv1._VECTptr)[1];
	    if (tmp.type==_STRNG)
	      tmp=gen(*tmp._STRNGptr,contextptr);
	    if (tmp.is_symb_of_sommet(at_equal))
	      tmp=tmp._SYMBptr->feuille._VECTptr->back();
	  }
	}
      }
    }
    return tmp;
  }
  static const char _extract_measure_s []="extract_measure";
  static define_unary_function_eval (__extract_measure,&_extract_measure,_extract_measure_s);
  define_unary_function_ptr5( at_extract_measure ,alias_at_extract_measure,&__extract_measure,0,true);

  gen _angleat(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT )
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    if (s<4)
      return gentypeerr(contextptr);
    gen l=_angle(gen(makevecteur(eval(v[0],eval_level(contextptr),contextptr),eval(v[1],eval_level(contextptr),contextptr),eval(v[2],eval_level(contextptr),contextptr)),_SEQ__VECT),contextptr);
    int save_digits=decimal_digits(contextptr);
    decimal_digits(contextptr)=3;
    string ss="\"α";
    add_name(ss,v[0],contextptr);
    ss += "="+l.print(contextptr)+" \"";
    // string ss="∡"+v[0].print(contextptr)+"="+l.print(contextptr)+" ";
    decimal_digits(contextptr)=save_digits;
    l=string2gen(ss,false);
    vecteur w=makevecteur(v[3],l);
    for (int i=4; i<s;++i)
      w.push_back(v[i]);
    return _legende(gen(w,_SEQ__VECT),contextptr);
  }
  static const char _angleat_s []="angleat";
  static define_unary_function_eval_quoted (__angleat,&_angleat,_angleat_s);
  define_unary_function_ptr5( at_angleat ,alias_at_angleat,&__angleat,_QUOTE_ARGUMENTS,true);

  gen _angleatraw(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT )
      return gentypeerr(contextptr);
    vecteur v = *args._VECTptr;
    int s=v.size();
    if (s<4)
      return gentypeerr(contextptr);
    gen l=_angle(gen(makevecteur(v[0],v[1],v[2]),_SEQ__VECT),contextptr);
    vecteur w=makevecteur(v[3],l);
    for (int i=4; i<s;++i)
      w.push_back(v[i]);
    return _legende(gen(w,_SEQ__VECT),contextptr);
  }
  static const char _angleatraw_s []="angleatraw";
  static define_unary_function_eval (__angleatraw,&_angleatraw,_angleatraw_s);
  define_unary_function_ptr5( at_angleatraw ,alias_at_angleatraw,&__angleatraw,0,true);

  gen _couleur(const gen & a,GIAC_CONTEXT){
    if (is_undef(a)) return a;
    if (a.type==_INT_){
      int i=default_color(contextptr);
      default_color(a.val,contextptr);
      return i;
    }
    if ( (a.type!=_VECT) || (a._VECTptr->size()<2))
      return default_color(contextptr);
    gen c=a._VECTptr->back(),b;
    if (a._VECTptr->size()>2)
      b=vecteur(a._VECTptr->begin(),a._VECTptr->end()-1);
    else
      b=a._VECTptr->front();
    if (b.type==_VECT){
      const_iterateur it=b._VECTptr->begin(),itend=b._VECTptr->end();
      vecteur res;
      res.reserve(itend-it);
      for (;it!=itend;++it)
	res.push_back(_couleur(makevecteur(*it,c),contextptr));
      return gen(res,b.subtype);
    }
    if ( (b.type!=_SYMB) || (b._SYMBptr->sommet!=at_pnt))
      return symbolic(at_couleur,a);      
    vecteur v(*b._SYMBptr->feuille._VECTptr);
    v[1]=c;
    gen e=symbolic(at_pnt,gen(v,_PNT__VECT));
    if (io_graph(contextptr))
      __interactive.op(e,contextptr);    
    return e;
  }
  static const char _display_s []="display";
  static define_unary_function_eval_index (160,__display,&giac::_couleur,_display_s);
  define_unary_function_ptr5( at_display ,alias_at_display,&__display,0,true);

  static const char _couleur_s []="color";
  static define_unary_function_eval (__couleur,&giac::_couleur,_couleur_s);
  define_unary_function_ptr5( at_couleur ,alias_at_couleur,&__couleur,0,true);

  // innert instruction, used e.g. for a:=element(3..5)
  // returns parameter(a,3..5,4) and stores 4 in a
  gen _parameter(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<4))
      return gensizeerr(contextptr);
    return symbolic(at_parameter,args);
  }
  static const char _parameter_s []="parameter";
  static define_unary_function_eval (__parameter,&giac::_parameter,_parameter_s);
  define_unary_function_ptr5( at_parameter ,alias_at_parameter,&__parameter,0,true);

  gen plotfield(const gen & xp,const gen & yp,const gen & x,const gen & y,double xmin,double xmax,double xstep,double ymin,double ymax,double ystep,double scaling,vecteur & attributs,bool normalize,const context * contextptr){
    bool old_iograph=io_graph(contextptr);
    if (old_iograph){
#ifdef HAVE_LIBPTHREAD
      pthread_mutex_lock(&interactive_mutex);
#endif
      io_graph(false,contextptr);
#ifdef HAVE_LIBPTHREAD
      pthread_mutex_unlock(&interactive_mutex);
#endif
    }
    vecteur xy_v;
    xy_v.push_back(x);
    xy_v.push_back(y);
    gen xp_eval,yp_eval,xy(xy_v),origine;
    vecteur curxcury(2);
    vecteur res;
    double echelle,minxstepystep;
    if (xstep<ystep) minxstepystep=xstep; else minxstepystep=ystep;
    echelle=minxstepystep;
    for (double curx=xmin;curx<=xmax;curx+=scaling*xstep){
      curxcury[0]=curx;
      for (double cury=ymin;cury<=ymax;cury+=scaling*ystep){
	curxcury[1]=cury;
	xp_eval=subst(xp,xy,curxcury,false,contextptr).evalf2double(eval_level(contextptr),contextptr);
	yp_eval=subst(yp,xy,curxcury,false,contextptr).evalf2double(eval_level(contextptr),contextptr);
	if ((xp_eval.type==_DOUBLE_) && (yp_eval.type==_DOUBLE_)){
	  double xpd=xp_eval._DOUBLE_val,ypd=yp_eval._DOUBLE_val;
	  if (normalize){
	    echelle=minxstepystep/std::sqrt(xpd*xpd+ypd*ypd);
	    origine=gen(curx-xpd/2*echelle)+cst_i*gen(cury-ypd/2*echelle);
	  }
	  else {
	    origine=gen(curx)+cst_i*gen(cury);
	  }
	  res.push_back(pnt_attrib(gen(makevecteur(origine,origine+echelle*xp_eval+echelle*cst_i*yp_eval),is_one(xp)?_GROUP__VECT:_VECTOR__VECT),attributs,contextptr));
	}
      }
    }
    if (old_iograph){
#ifdef HAVE_LIBPTHREAD
      pthread_mutex_lock(&interactive_mutex);
#endif
      io_graph(true,contextptr);
#ifdef HAVE_LIBPTHREAD
      pthread_mutex_unlock(&interactive_mutex);
#endif
      iterateur it=res.begin(),itend=res.end();
      for (;it!=itend;++it)
	__interactive.op(*it,contextptr);
    }
    return gen(res,_GROUP__VECT);
  }

  static bool read_plotfield_args(const gen & args,gen & xp,gen & yp,gen & x,gen & y,double & xmin,double & xmax,double & xstep,double & ymin,double & ymax,double & ystep,vecteur & attributs,bool & normalize,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->size()<2)
      return false; // setsizeerr(contextptr);
    normalize=false;
    vecteur v(*args._VECTptr);
    int s=v.size();
    for (int i=0;i<s;++i){
      if (v[i]==at_normalize){
	normalize=true;
	v.erase(v.begin()+i);
	break;
      }
    }
    s=read_attributs(v,attributs,contextptr);
    v=vecteur(args._VECTptr->begin(),args._VECTptr->begin()+s);
    switch (s){
    case 0: case 1:
      return false; // setsizeerr(contextptr);
    case 2:
      if ( (v.back().type!=_VECT) || (v.back()._VECTptr->size()!=2) )
	return false; // setsizeerr(contextptr);
      x=v.back()._VECTptr->front();
      y=v.back()._VECTptr->back();
      yp=equal2diff(v.front());
      if (yp.type==_VECT){
	xp=yp._VECTptr->front();
	yp=yp._VECTptr->back();
      }
      else
	xp=plus_one;
      break;
    case 3:
      if (v[0].type!=_VECT){
	xp=plus_one;
	yp=equal2diff(v[0]);
      }
      else {
	if (v[0]._VECTptr->size()!=2)
	  return false; // setsizeerr(contextptr);
	xp=v[0]._VECTptr->front();
	yp=v[0]._VECTptr->back();
      }
      x=v[1];
      y=v[2];
      break;
    default:
      xp=v[0];
      yp=v[1];
      x=v[2];
      y=v[3];
    }
    int nstep=int(std::sqrt(double(gnuplot_pixels_per_eval)/2)),jstep=0,kstep=0;
    readrange(x,gnuplot_xmin,gnuplot_xmax,x,xmin,xmax,contextptr);
    readrange(y,gnuplot_xmin,gnuplot_xmax,y,ymin,ymax,contextptr);
    vecteur tmp;
    read_option(*args._VECTptr,xmin,xmax,ymin,ymax,gnuplot_zmin,gnuplot_zmax,tmp,nstep,jstep,kstep,contextptr);
    xstep=(xmax-xmin)/nstep;
    ystep=(ymax-ymin)/(jstep?jstep:nstep);
    return true;
  }
  // args=[dx/dt,dy/dt,x,y] or [dy/dx,x,y]
  // or [ [dx/dt,dy/dt], [x,y] ] or [ dy/dx, [x,y]]
  gen _plotfield(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs;
    gen xp,yp,x,y;
    double xmin,xmax,ymin,ymax,xstep,ystep;
    bool normalize;
    if (!read_plotfield_args(args,xp,yp,x,y,xmin,xmax,xstep,ymin,ymax,ystep,attributs,normalize,contextptr))
      return gensizeerr(contextptr);
    double scaling=2;
    return plotfield(xp,yp,x,y,xmin,xmax,xstep/scaling,ymin,ymax,ystep/scaling,scaling,attributs,normalize,contextptr);
  }
  static const char _plotfield_s []="plotfield";
  static define_unary_function_eval (__plotfield,&giac::_plotfield,_plotfield_s);
  define_unary_function_ptr5( at_plotfield ,alias_at_plotfield,&__plotfield,0,true);

  static const char _fieldplot_s []="fieldplot";
  static define_unary_function_eval (__fieldplot,&giac::_plotfield,_fieldplot_s);
  define_unary_function_ptr5( at_fieldplot ,alias_at_fieldplot,&__fieldplot,0,true);

  // plot solution of y'=f(x,y) [x0,y0] in current plot range
  // OR [x,y]'=f(t,x,y) t0 x0 y0 in current plot range
  // args = dy/dx, [x, y], [x0, y0]
  // OR [dx/dt, dy/dt], [t, x, y], [t0, x0, y0]
  static gen plotode(const vecteur & w,GIAC_CONTEXT){
    vecteur v(w);
    bool curve=true;
    gen fp=v[0];
    if (fp.type!=_VECT) // y'=f(x,y)
      fp=makevecteur(plus_one,fp);
    gen vars=v[1];
    bool dim3=vars.type==_VECT && vars._VECTptr->size()==3;
    gen init=remove_at_pnt(v[2]);
    if (init.type!=_VECT)
      init=makevecteur(re(init,contextptr),im(init,contextptr));
    gen t;
    int s;
    if (vars.type!=_VECT || (s=vars._VECTptr->size())<2 ) 
      return gensizeerr(contextptr);
    if (s==3){
      t=vars._VECTptr->front();
      vars=makevecteur(vars[1],vars[2]);
    }
    else {
      identificateur tt(" t");
      t=tt;
      if (s==2 && vars[0].is_symb_of_sommet(at_equal)){
	v.push_back(vars[0]);
	t=vars[0]._SYMBptr->feuille[0];
	vars=makevecteur(t,vars[1]);
      }
    }
    v[1]=vars;
    vecteur f=makevecteur(fp,(t.is_symb_of_sommet(at_equal)?t._SYMBptr->feuille._VECTptr->front():t),vars);
    if (init.type!=_VECT || (s=init._VECTptr->size())<2 ) 
      return gensizeerr(contextptr);
    vecteur & initv=*init._VECTptr;
    gen t0=initv.front();
    gen x0=t0;
    if (s==3)
      x0=initv[1];
    gen y0=makevecteur(x0,initv.back());
    double ym[2]={gnuplot_xmin,gnuplot_ymin},yM[2]={gnuplot_xmin,gnuplot_ymin};
    double * ymin = 0;
    double * ymax = 0;
    double tstep=0,tmin=-1e300,tmax=1e300;
    bool tminmax_defined,tstep_defined;
    read_tmintmaxtstep(v,t,3,tmin,tmax,tstep,tminmax_defined,tstep_defined,contextptr);
    if (tmin>0 || tmax<0 || tmin>tmax || tstep<=0)
      return gensizeerr(gettext("Time"));
    int maxstep=500;
    if (tminmax_defined && tstep_defined)
      maxstep=2*int((tmax-tmin)/tstep)+1;
    int vs=v.size();
    for (int i=3;i<vs;++i){
      if (readvar(v[i])==vars[0]){
	if (readrange(v[i],gnuplot_xmin,gnuplot_xmax,v[i],ym[0],yM[0],contextptr)){
	  ymin=ym;
	  ymax=yM;
	  v.erase(v.begin()+i);
	  --vs;
	}
      }
      if (readvar(v[i])==vars[1]){
	if (readrange(v[i],gnuplot_xmin,gnuplot_xmax,v[i],ym[1],yM[1],contextptr)){
	  ymin=ym;
	  ymax=yM;
	  v.erase(v.begin()+i);
	  --vs;
	}
      }
    }
    if (dim3 && vs>3)
      dim3=(v[3]!=at_plan);
    vecteur res1v,resv;
    if (tmin<0){
      gen res1=odesolve(t0,tmin,f,y0,tstep,curve,ymin,ymax,maxstep,contextptr);
      if (is_undef(res1)) return res1;
      res1v=*res1._VECTptr;
      std::reverse(res1v.begin(),res1v.end());
    }
    res1v.push_back(makevecteur(t0,y0));
    if (tmax>0){
      gen res2=odesolve(t0,tmax,f,y0,tstep,curve,ymin,ymax,maxstep,contextptr);
      if (is_undef(res2)) return res2;
      resv=mergevecteur(res1v,*res2._VECTptr);
    }
    else
      resv=res1v;
    // make the curve
    const_iterateur it=resv.begin(),itend=resv.end();
    vecteur res;
    res.reserve(itend-it);
    for (;it!=itend;++it){
      if (dim3)
	res.push_back(gen(makevecteur(it->_VECTptr->front(),it->_VECTptr->back()._VECTptr->front(),it->_VECTptr->back()._VECTptr->back()),_POINT__VECT));
      else
	res.push_back(it->_VECTptr->back()._VECTptr->front()+cst_i*it->_VECTptr->back()._VECTptr->back());
    }
    return symb_pnt(gen(res,_GROUP__VECT),contextptr);
  }
  gen _plotode(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (s<3)
      return gendimerr(contextptr);
    // v.erase(v.begin()+s,v.end());
    return put_attributs(plotode(v,contextptr),attributs,contextptr);
  }
  static const char _plotode_s []="plotode";
  static define_unary_function_eval (__plotode,&giac::_plotode,_plotode_s);
  define_unary_function_ptr5( at_plotode ,alias_at_plotode,&__plotode,0,true);

  static const char _odeplot_s []="odeplot";
  static define_unary_function_eval (__odeplot,&giac::_plotode,_odeplot_s);
  define_unary_function_ptr5( at_odeplot ,alias_at_odeplot,&__odeplot,0,true);

  // like plotode but the initial(s) condition(s) will be specified
  // by the user
  gen _interactive_plotode(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs;
    gen xp,yp,x,y;
    double xmin,xmax,ymin,ymax,xstep,ystep;
    bool normalize;
    if (!read_plotfield_args(args,xp,yp,x,y,xmin,xmax,xstep,ymin,ymax,ystep,attributs,normalize,contextptr))
      return gensizeerr(contextptr);
    // double scaling=3;
    vecteur res;
#if defined(WIN32) || !defined(HAVE_SIGNAL_H_OLD)
    res.push_back(_plotfield(args,contextptr));
#else
    if (thread_eval_status(contextptr)==1){
      res.push_back(_plotfield(args,contextptr));
      _DispG(0,contextptr);
    }
    else
      res.push_back(_signal(_plotfield(args,contextptr),contextptr));
#endif
    identificateur tt(" t");
    gen t(tt);
    vecteur vars=makevecteur(t,x,y);
    // if (is_one(xp)) vars[0]=symb_equal(t,symb_interval(xmin,xmax));
    vecteur f(makevecteur(xp,yp));
    gen xminmax=symb_equal(x,symb_interval(xmin,xmax));
    gen yminmax=symb_equal(y,symb_interval(ymin,ymax));
    for (;;){
      gen localisation=__click.op(vecteur(0),contextptr).evalf_double(1,contextptr);
      double x0,y0;
      if (localisation.type==_DOUBLE_){
	x0=localisation._DOUBLE_val;
	y0=0;
      }
      else {
	if ((localisation.type==_CPLX) && (localisation.subtype==3) ) {
	  x0=localisation._CPLXptr->_DOUBLE_val;
	  y0=(localisation._CPLXptr+1)->_DOUBLE_val;
	}
	else {
	  *logptr(contextptr) << gettext("End interactive_plotode") << endl;
	  break;
	}
      }
#if defined(WIN32) || !defined(HAVE_SIGNAL_H_OLD)
      res.push_back(_plotode(gen(makevecteur(f,vars,makevecteur(0,x0,y0),xminmax,yminmax,at_plan),_SEQ__VECT),contextptr));
#else
      if (thread_eval_status(contextptr)==1)
	res.push_back(_plotode(gen(makevecteur(f,vars,makevecteur(0,x0,y0),xminmax,yminmax,at_plan),_SEQ__VECT),contextptr));
      else
	res.push_back(_signal(_plotode(gen(makevecteur(f,vars,makevecteur(0,x0,y0),xminmax,yminmax,at_plan),_SEQ__VECT),contextptr),contextptr));
#endif
    }
    return res;
  }
  static const char _interactive_plotode_s []="interactive_plotode";
  static define_unary_function_eval (__interactive_plotode,&giac::_interactive_plotode,_interactive_plotode_s);
  define_unary_function_ptr5( at_interactive_plotode ,alias_at_interactive_plotode,&__interactive_plotode,0,true);

  static const char _interactive_odeplot_s []="interactive_odeplot";
  static define_unary_function_eval (__interactive_odeplot,&giac::_interactive_plotode,_interactive_odeplot_s);
  define_unary_function_ptr5( at_interactive_odeplot ,alias_at_interactive_odeplot,&__interactive_odeplot,0,true);

  static vecteur unarchive_VECT(istream & is,GIAC_CONTEXT){
    vecteur v;
    int taille;
    is >> taille;
    v.reserve(taille);
    for (int i=0;i<taille;++i)
      v.push_back(unarchive(is,contextptr));
    return v;
  }

  static gen unarchive_MAP(istream & is,GIAC_CONTEXT){
    gen_map m(ptr_fun(islesscomplexthanf));
    int taille;
    is >> taille;
    for (int i=0;i<taille;++i){
      gen f=unarchive(is,contextptr);
      gen s=unarchive(is,contextptr);
      m[f]=s;
    }
    return m;
  }

  static gen unarchive_FUNC(istream & is,GIAC_CONTEXT){
    int op;
    is >> op;
    unary_function_ptr u(*at_plus);
    if (op){
      if (op<0){
	string s;
	is >> s;
	if (debug_infolevel>20)
	  *logptr(contextptr) << s << endl;
	// read function from lexer_functions
	std::pair<charptr_gen *,charptr_gen *> p= equal_range(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),std::pair<const char *,gen>(s.c_str(),0),tri);
	if (p.first!=p.second && p.first!=builtin_lexer_functions_end())
	  return p.first->second;
	map_charptr_gen::const_iterator i = lexer_functions().find(s.c_str());
	if (i==lexer_functions().end()) // should be error
	  return undef;
	return i->second;
      }
      u=archive_function_tab()[op-1];
    }
    else {
      // read using the parser
      string s;
      is >> s;
      gen e;
#ifndef NO_STDEXCEPT
      try {
#endif
	e=gen(s,contextptr);
	if (is_undef(e))
	  e=string2gen(s,false);
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error & ){
	e=string2gen(s,false);
      }
#endif
      //cerr << s << " " <<e.type << endl;
      if (e.type!=_FUNC){
	if ( (e.type!=_SYMB) ){
	  cerr << "Unarchive error: "+e.print(contextptr) << endl;
	  return e;
	}
	if (e._SYMBptr->sommet.ptr()->printsommet==printastifunction)
	  return e._SYMBptr->sommet;
	return e._SYMBptr->feuille;
      }
      u=*e._FUNCptr;
    }
    return u;
  }

  static symbolic unarchive_SYMB(istream & is,GIAC_CONTEXT){
    gen e=unarchive_FUNC(is,contextptr);
    gen f=unarchive(is,contextptr);
    if (e.type==_FUNC)
      return symbolic(*e._FUNCptr,f);
    return symb_of(e,f);
  }

  static gen unarchivedefault(istream & is,GIAC_CONTEXT){
    string s;
    is >> s;
    return gen(s,contextptr); 
  }

  static gen unarchivestring(istream & is,GIAC_CONTEXT){
    int l;
    is >> l;
    string s;
    char c;
    for (;;){
      is.get(c);
      if (c=='|')
	break;
    }
    for (int i=0;i<l;++i){
      is.get(c);
      s +=c;
    }
    return string2gen(s,false);
  }

  /* old code: too slow for unarchive
     gen unarchiveidnt(istream & is,const vecteur & l,int t,GIAC_CONTEXT){
     char c;
     is >> c;
     string s;
     s +=c;
     for (int i=1;i<t;++i){
     is.get(c);
     s +=c;
     }
     gen res=gen(s,l,contextptr); 
     if (res.type!=_IDNT && s[0]=='_')
     res=find_or_make_symbol(s,false,contextptr);
     if (res.type!=_IDNT){
     string s1 ="~"+s;
     res=gen(s1,l,contextptr); 
     if (res.type!=_IDNT)
     res=find_or_make_symbol(s,false,contextptr);
     }
     return res;
     }
  */

  static gen unarchiveidnt(istream & is,int t,GIAC_CONTEXT){
    char c;
    is >> c;
    string s;
    s +=c;
    for (int i=1;i<t;++i){
      is.get(c);
      s +=c;
    }
    gen res=find_or_make_symbol(s,false,contextptr); // gen(s,l,contextptr); 
    if (res.type!=_IDNT){
      string s1 ="~"+s;
      res=find_or_make_symbol(s1,false,contextptr);
    }
    return res;
  }

  static gen unarchivereal(istream & is,GIAC_CONTEXT){
    unsigned int prec;
    is >> prec;
    string s;
    is >> s;
    if (!prec)
      return gen(s,contextptr); 
    return read_binary(s,prec);
  }

  gen unarchive(istream & is,GIAC_CONTEXT){
    if (is.eof())
      return gentypeerr(gettext("End of stream"));
    int type,val;
    double d;
    char ch;
    gen a,b;
    is >> type;
    if (is.eof())
      return undef;
    switch (type){
    case _INT_:
      is >> val >> type;
      a=val;
      a.subtype=type;
      return a;
    case _DOUBLE_:
      is.get(ch);
      is.read((char *)&d,sizeof(double));
      return d;
    case _CPLX:
      a=unarchive(is,contextptr);
      b=unarchive(is,contextptr);
      return a+cst_i*b;
    case _REAL:
      a=unarchivereal(is,contextptr);
      return a;
    case _FRAC:
      a=unarchive(is,contextptr);
      b=unarchive(is,contextptr);
      if (is_undef(a) || is_undef(b))
	return a+b;
      return fraction(a,b);
    case _VECT:
      is >> type;
      return gen(unarchive_VECT(is,contextptr),type);
    case _SYMB:
      is >> type;
      a=unarchive_SYMB(is,contextptr);
      if (!is_undef(a))
	a.subtype=type;
      return a;
    case _IDNT:
      is >> type;
      return unarchiveidnt(is,type,contextptr);
    case _FUNC:
      is >> type;
      a=unarchive_FUNC(is,contextptr);
      if (a.type==_FUNC)
	return gen(*a._FUNCptr,type);
      return a;
    case _STRNG:
      return unarchivestring(is,contextptr);
    case _MOD:
      a=unarchive(is,contextptr);
      b=unarchive(is,contextptr);
      if (is_undef(a) || is_undef(b))
	return a+b;
      return makemodquoted(a,b);
    case _MAP:
      return unarchive_MAP(is,contextptr);
    case _POINTER_:
      is >> type;
      if (type==_FL_WIDGET_POINTER && fl_widget_unarchive_function)
	return fl_widget_unarchive_function(is);
    default:
      return unarchivedefault(is,contextptr);
    }
  }

  static ostream & archive_FUNC(ostream & os,const unary_function_ptr & u,GIAC_CONTEXT){
    int i=archive_function_index(u); // equalposcomp(archive_function_tab,u);
    if (!i && has_special_syntax(u.ptr()->s)){
      os << "-1 " << u.ptr()->s << " ";
      return os;
    }
    os << i << " ";
    if (!i){
      string s;
#ifndef NO_STDEXCEPT
      try {
#endif
	if (u.ptr()->printsommet) 
	  s=(u.ptr()->printsommet(zero,u.ptr()->s,0));
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error & ){
	s=u.ptr()->s;
      }
#endif
      int l=s.size();
      if ( (l>4) && (s.substr(l-3,3)=="(0)") )
	os << s.substr(0,l-3) << " ";
      else
	os << gen(u) << " ";
    }
    return os;
  }

  static ostream & archive_SYMB(ostream & os,const symbolic & s,GIAC_CONTEXT){
    unary_function_ptr u=s.sommet;
    gen f=s.feuille;
    archive_FUNC(os,u,contextptr);
    archive(os,f,contextptr);
    return os;
  }

  static ostream & archive_VECT(ostream & os,const vecteur & v,GIAC_CONTEXT){
    const_iterateur it=v.begin(),itend=v.end();
    os << itend-it << " ";
    for (;it!=itend;++it)
      archive(os,*it,contextptr);
    return os;
  }

  static ostream & archive_MAP(ostream & os,const gen_map & v,GIAC_CONTEXT){
    gen_map::const_iterator it=v.begin(),itend=v.end();
    int i=0;
    for (;it!=itend;++it) 
      ++i;
    os << i << " ";
    for (it=v.begin();it!=itend;++it){
      archive(os,it->first,contextptr);
      os << " ";
      archive(os,it->second,contextptr);
    }
    return os;
  }

  static ostream & archive_IDNT(ostream & os,const identificateur & i,GIAC_CONTEXT){
    string s=i.print(contextptr);
    return os << s.size() << " " << s << " ";
  }

  ostream & archive(ostream & os,const gen & e,GIAC_CONTEXT){
    unsigned et=e.type;
    signed es=e.subtype;
    switch (et){
    case _INT_:
      return os << et << " " << e.val << " " << es << endl;
    case _DOUBLE_:
      if (my_isinf(e._DOUBLE_val) || my_isnan(e._DOUBLE_val) )
	return archive(os,gen(e.print(contextptr),contextptr),contextptr);
      os << et << " ";
#ifdef DOUBLEVAL
      os.write((char *)&(e._DOUBLE_val),sizeof(double));
#else
      os.write((char *)&e,sizeof(double));
#endif
      return os << endl;
    case _CPLX:
      os << et << " ";
      archive(os,*e._CPLXptr,contextptr);
      return archive(os,*(e._CPLXptr+1),contextptr);
    case _REAL:
      os << et << " ";
#ifdef HAVE_LIBMPFR
      os << mpfr_get_prec(e._REALptr->inf) << " ";
      return os << print_binary(*e._REALptr) << endl;
#else
      os << 0 << " ";
      return os << e.print(contextptr) << endl ;
#endif
    case _VECT:
      os << et << " " << es << " " ;
      return archive_VECT(os,*e._VECTptr,contextptr);
    case _SYMB:
      if (es==-1)
	os << et << " -1 ";
      else
	os << et << " "<< es << " ";
      return archive_SYMB(os,*e._SYMBptr,contextptr);
    case _IDNT:
      os << et << " ";
      return archive_IDNT(os,*e._IDNTptr,contextptr);
    case _FUNC:
      os << et << " " << es << " ";
      return archive_FUNC(os,*e._FUNCptr,contextptr);
    case _FRAC:
      os << et << " ";
      archive(os,e._FRACptr->num,contextptr);
      return archive(os,e._FRACptr->den,contextptr);
    case _STRNG:
      return os << et << " " << e._STRNGptr->size() << " |" << *e._STRNGptr << " ";
    case _MOD:
      os << et << " ";
      archive(os,*e._MODptr,contextptr); 
      os << " ";
      archive(os,*(e._MODptr+1),contextptr);
      return os << " ";
    case _MAP:
      os << et << " ";
      archive_MAP(os,*e._MAPptr,contextptr);
      return os << " ";
    case _POINTER_:
      if (es==_FL_WIDGET_POINTER && fl_widget_archive_function)
	return fl_widget_archive_function(os,e._POINTER_val);
      else
	return archive(os,string2gen("Done",false),contextptr);
    case _USER:
      {
	if (galois_field * gf=dynamic_cast<galois_field *>(e._USERptr)){
	  return os << et << "GF(" << gf->p << "," << gf->P << "," << gf->x << "," << gf->a << ")" << endl;
	}
      }
    default:
      return os << et << " " << e.print(contextptr) << endl;
    }
  }

  gen archive_session(bool save_history,ostream & os,GIAC_CONTEXT){
    os << "giac archive"<< endl;
    gen g(giac_current_status(save_history,contextptr));
    archive(os,g,contextptr);
    return g;
  }

  // Archive cas, geo setup, history_in(contextptr), history_out(contextptr), all variable values
  gen archive_session(bool save_history,const string & s,GIAC_CONTEXT){
#ifdef GIAC_BINARY_ARCHIVE
    FILE * f =fopen(s.c_str(),"w");
    fprintf(f,"%s","giac binarch\n");
    gen g(giac_current_status(save_history,contextptr));
    return archive_save(f,g,contextptr)?1:0;
#else
    ofstream os(s.c_str());
    return archive_session(save_history,os,contextptr);
#endif
  }

  std::string archive_session(bool save_history,GIAC_CONTEXT){
    ostringstream os;
    archive_session(save_history,os,contextptr);
    return os.str();
  }

  // Unarchive a session from archive named s
  // Replace one level of history by replace
  // Return 0 if not successfull, or a vector of remaining gen in the archive
  gen unarchive_session(istream & is,int level, const gen & replace,GIAC_CONTEXT){
#if defined BESTA_OS || defined VISUALC
    char * buf = ( char * )alloca( BUFFER_SIZE );
#else
    char buf[BUFFER_SIZE];
#endif
    is.getline(buf,BUFFER_SIZE,'\n');
    string bufs(buf);
    if (bufs!="giac archive")
      return 0;
    gen g=unarchive(is,contextptr);
    if (!is)
      return 0;
    if (!unarchive_session(g,level,replace,contextptr))
      return 0;
    vecteur res;
    while (!is.eof()){
      res.push_back(unarchive(is,contextptr));
    }
    return res;
  }

  gen unarchive_session(const string & s,int level, const gen & replace,GIAC_CONTEXT){
    FILE * f = fopen(s.c_str(),"r");
    char * buf = new char[101];
    fread(buf,sizeof(char),12,f);
    buf[12]=0;
    if (!strcmp(buf,"giac binarch")){
      fread(buf,sizeof(char),1,f); // FIXME 2 for windows?
      delete [] buf;
      gen g=archive_restore(f,contextptr);
      if (!unarchive_session(g,level,replace,contextptr))
	return 0;
      vecteur res;
      while (!feof(f)){
	res.push_back(archive_restore(f,contextptr));
      }
      return res;      
    }
    fclose(f);
    delete [] buf;
    ifstream is(s.c_str());
    if (!is)
      return false;
    return unarchive_session(is,level,replace,contextptr);
  }

  gen unarchive_session_string(const std::string & s,int level, const gen & replace,GIAC_CONTEXT){
    istringstream is(s);
    if (!is)
      return false;
    return unarchive_session(is,level,replace,contextptr);
  }

  gen _archive(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    if (args.type==_STRNG){ // archive session state
      return archive_session(true,*args._STRNGptr,contextptr);
    }
    int s;
    if ( args.type!=_VECT || (s=args._VECTptr->size())<2 )
      return gensizeerr(contextptr);
    gen a=args._VECTptr->front();
    gen b=(*args._VECTptr)[1];
    if (a.type!=_STRNG)
      return gensizeerr(contextptr);
    if (s==3){ // new binary archive format
      FILE * f=fopen(a._STRNGptr->c_str(),"w");
      if (!f)
	return gensizeerr(gettext("Unable to open file ")+a.print(contextptr));
      fprintf(f,"%s","-1  "); // header: type would be -1
      if (!archive_save(f,b,contextptr))
	return gensizeerr(gettext("Error writing ")+b.print(contextptr)+" in file "+a.print(contextptr));
      fclose(f);
      return b;
    }
    ofstream os(a._STRNGptr->c_str());
    archive(os,b,contextptr);
    return b;
  }
  static const char _archive_s []="archive";
  static define_unary_function_eval (__archive,(const gen_op_context)giac::_archive,_archive_s);
  define_unary_function_ptr5( at_archive ,alias_at_archive,&__archive,0,true);

  gen _unarchive(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_STRNG)
      return gensizeerr(contextptr);
    FILE * f = fopen(args._STRNGptr->c_str(),"r");
    char * buf = new char[101];
    fread(buf,sizeof(char),4,f);
    if (buf[0]=='-' && buf[1]=='1' && buf[2]==' '){
      delete [] buf;
      gen res=archive_restore(f,contextptr);
      return res;
    }
    fclose(f);
    ifstream is(args._STRNGptr->c_str());
    is.getline(buf,100,'\n');
    bool ar = (buf==string("giac archive") || buf==string("giac binarch"));
    delete [] buf;
    is.close();
    if (ar)
      return unarchive_session(*args._STRNGptr,-1,0,contextptr);
    ifstream is0(args._STRNGptr->c_str());
    return unarchive(is0,contextptr);
  }
  static const char _unarchive_s []="unarchive";
  static define_unary_function_eval (__unarchive,&giac::_unarchive,_unarchive_s);
  define_unary_function_ptr5( at_unarchive ,alias_at_unarchive,&__unarchive,0,true);

  bool geo_setup(const vecteur & w,GIAC_CONTEXT){
    if (w.size()<12)
      return false; // setsizeerr(contextptr);
    if (w.size()>12) {
      gen g=w[12];
      g=_floor(g,0);
      if (g.type!=_INT_)
	return false; // setsizeerr(contextptr);
      show_axes(g.val,contextptr);
    }
    if (w.size()>15) {
      gen g=w[15];
      g=_floor(g,0);
      if (g.type!=_INT_)
	return false; // setsizeerr(contextptr);
#ifdef WITH_GNUPLOT
      int i=g.val;
      gnuplot_hidden3d=(i %2)!=0;
      i =i/2;
      gnuplot_pm3d=(i%2)!=0;
#endif
    }
    gen tmpw=evalf_double(w,1,contextptr);
    if (tmpw.type!=_VECT)
      return false;
    vecteur v=*tmpw._VECTptr;
    if (v.size()>14) {
      if (v[13].type!=_DOUBLE_ || v[14].type!=_DOUBLE_)
	return false; // setsizeerr(contextptr);
      class_minimum=v[13]._DOUBLE_val;
      class_size=v[14]._DOUBLE_val;
    }
    for (int i=0;i<12;++i){
      if (v[i].type!=_DOUBLE_)
	return false; // setsizeerr(contextptr);
    }
    gnuplot_xmin=v[0]._DOUBLE_val;
    gnuplot_xmax=v[1]._DOUBLE_val;
    gnuplot_ymin=v[2]._DOUBLE_val;
    gnuplot_ymax=v[3]._DOUBLE_val;
    gnuplot_zmin=v[4]._DOUBLE_val;
    gnuplot_zmax=v[5]._DOUBLE_val;
    gnuplot_tmin=v[6]._DOUBLE_val;
    gnuplot_tmax=v[7]._DOUBLE_val;
    global_window_xmin=v[8]._DOUBLE_val;
    global_window_xmax=v[9]._DOUBLE_val;
    global_window_ymin=v[10]._DOUBLE_val;
    global_window_ymax=v[11]._DOUBLE_val;
    return true;
  }

  gen xyztrange(double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,double tmin,double tmax,double wxmin,double wxmax,double wymin, double wymax, int axes,double class_min,double class_size,bool gnuplot_hidden3d,bool gnuplot_pm3d){
    vecteur v;
    v.push_back(xmin);
    v.push_back(xmax);
    v.push_back(ymin);
    v.push_back(ymax);
    v.push_back(zmin);
    v.push_back(zmax);
    v.push_back(tmin);
    v.push_back(tmax);
    v.push_back(wxmin);
    v.push_back(wxmax);
    v.push_back(wymin);
    v.push_back(wymax);
    v.push_back(axes);
    v.push_back(class_min);
    v.push_back(class_size);
    v.push_back(int(gnuplot_hidden3d+2*gnuplot_pm3d));
    return symbolic(at_xyztrange,v);
  }
  gen _xyztrange(const gen & args,GIAC_CONTEXT){
    if (interactive_op_tab && interactive_op_tab[8])
      return interactive_op_tab[8](args,contextptr);
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()<4)
      return gensizeerr(contextptr);
    vecteur w=*args._VECTptr;
    int s=w.size();
    if (s<12){
      if (s<5) w.push_back(gnuplot_zmin);
      if (s<6) w.push_back(gnuplot_zmax);
      if (s<7) w.push_back(gnuplot_tmin);
      if (s<8) w.push_back(gnuplot_zmin);
      if (s<9) w.push_back(w[0]);
      if (s<10) w.push_back(w[1]);
      if (s<11) w.push_back(w[2]);
      if (s<12) w.push_back(w[3]);
    }
    if (!geo_setup(w,contextptr))
      return gensizeerr(contextptr);
#ifdef HAVE_SIGNAL_H_OLD
    if (!child_id){
      _signal(symbolic(at_quote,symbolic(at_xyztrange,w)),contextptr);
    }
#endif
    return args;
  }
  static const char _xyztrange_s []="xyztrange";
#ifdef RTOS_THREADX
  static define_unary_function_eval(__xyztrange,&_xyztrange,_xyztrange_s);
#else
  unary_function_eval __xyztrange(0,&giac::_xyztrange,_xyztrange_s);
#endif
  //unary_function_ptr at_xyztrange_ (&__xyztrange,0,true);
  //const unary_function_ptr * at_xyztrange=&at_xyztrange_;
  define_unary_function_ptr5( at_xyztrange ,alias_at_xyztrange,&__xyztrange,0,true);

  gen _switch_axes(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_INT_)
      show_axes(args.val,contextptr);
    else {
      if (show_axes(contextptr))
	show_axes(0,contextptr);
      else
	show_axes(1,contextptr);
    }
    return eval(xyztrange(gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,gnuplot_zmin,gnuplot_zmax,gnuplot_tmin,gnuplot_tmax,global_window_xmin,global_window_xmax,global_window_ymin,global_window_ymax,show_axes(contextptr),class_minimum,class_size,
#ifdef WITH_GNUPLOT
			  gnuplot_hidden3d,gnuplot_pm3d
#else
			  1,1
#endif
			  ),contextptr);
  }
  static const char _switch_axes_s []="switch_axes";
  static define_unary_function_eval (__switch_axes,&giac::_switch_axes,_switch_axes_s);
  define_unary_function_ptr5( at_switch_axes ,alias_at_switch_axes,&__switch_axes,0,true);

  gen plotseq(const gen& f,const gen&x,double x0,double xmin,double xmax,int niter,const vecteur & attributs,const context * contextptr){
    if (xmin>xmax)
      giac::swapdouble(xmin,xmax);
    vecteur res(2*niter+1);
    res[0]=x0;
    int j=1;
    gen newx0;
    double x1;
    for (int i=0;i<niter;++i){
      newx0=subst(f,x,x0,false,contextptr).evalf2double(eval_level(contextptr),contextptr);
      if (newx0.type!=_DOUBLE_)
	return gensizeerr(gettext("Bad iteration"));
      x1=newx0._DOUBLE_val;
      res[j]=gen(x0,x1);
      ++j;
      x0=x1;
      res[j]=gen(x0,x0);
      ++j;
    }
    vecteur g(gen2vecteur(_plotfunc(gen(makevecteur(f,symb_equal(x,symb_interval(xmin,xmax))),_SEQ__VECT),contextptr)));
    g.push_back(pnt_attrib(gen(makevecteur(gen(gnuplot_xmin,gnuplot_xmin),gen(gnuplot_xmax,gnuplot_xmax)),_LINE__VECT),attributs,contextptr));
#ifdef GIAC_HAS_STO_38
    int color=FL_BLACK;
#else
    int color=FL_MAGENTA;
#endif
    if (!attributs.empty())
      color = color | (attributs[0].val & 0xffff0000);
    g.push_back(symb_pnt(gen(res,_LINE__VECT),color,contextptr));
    g.push_back(symb_pnt(gen(makevecteur(gen(x0,x0),gen(x0)),_VECTOR__VECT), color | _DASH_LINE,contextptr));
    return g; // gen(g,_SEQ__VECT);
  }
  int find_plotseq_args(const gen & args,gen & expr,gen & x,double & x0d,double & xmin,double & xmax,int & niter,vecteur & attributs,GIAC_CONTEXT){
    vecteur v=gen2vecteur(args);
    attributs=vecteur(1,default_color(contextptr));
    int l=read_attributs(v,attributs,contextptr);
    if (l<2)
      v.push_back(0);
    expr=v[0];
    niter=30;
    gen x0;
    if (l>3){ // expr,var,x0,niter
      x=v[1];
      x0=v[2];
      if (v[3].type==_INT_)
	niter=absint(v[3].val);
      else
	return -2; // bad iteration
    }
    else {
      if (l>2){
	if (v[2].type==_INT_)
	  niter=absint(v[2].val);
	else
	  return -2;
      }
      if ( (v[1].type==_SYMB) && (v[1]._SYMBptr->sommet==at_equal) ){
	vecteur & w=*v[1]._SYMBptr->feuille._VECTptr;
	x=w[0];
	x0=w[1];
      }
      else {
	x=vx_var;
	x0=v[1];
      }
    }
    x0=evalf_double(x0,eval_level(contextptr),contextptr);
    xmin=gnuplot_xmin;xmax=gnuplot_xmax;
    if (x0.type==_VECT && x0._VECTptr->size()==3){
      vecteur & x0v=*x0._VECTptr;
      if (x0v[1].type!=_DOUBLE_ || x0v[2].type!=_DOUBLE_)
	return -3; // gensizeerr(gettext("Non numeric range value"));
      xmin=x0v[1]._DOUBLE_val;
      xmax=x0v[2]._DOUBLE_val;
      x0=remove_at_pnt(x0v[0]);
      x0=re(x0,contextptr);
    }
    if (x0.type!=_DOUBLE_)
      return -4; // 
    x0d=x0._DOUBLE_val;
    return 0;
  }

  // args=[expr,[var=]x0|[x0,xmin,xmax][,niter]]
  gen _plotseq(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen expr,var;
    double x0d,xmin,xmax;
    int niter;
    vecteur attributs;
    if (find_plotseq_args(args,expr,var,x0d,xmin,xmax,niter,attributs,contextptr)<0)
      return gentypeerr(contextptr);
    return plotseq(expr,var,x0d,xmin,xmax,niter,attributs,contextptr);
  }
  static const char _plotseq_s []="plotseq";
  static define_unary_function_eval (__plotseq,&giac::_plotseq,_plotseq_s);
  define_unary_function_ptr5( at_plotseq ,alias_at_plotseq,&__plotseq,0,true);

  static const char _graphe_suite_s []="graphe_suite";
  static define_unary_function_eval (__graphe_suite,&giac::_plotseq,_graphe_suite_s);
  define_unary_function_ptr5( at_graphe_suite ,alias_at_graphe_suite,&__graphe_suite,0,true);

  static const char _seqplot_s []="seqplot";
  static define_unary_function_eval (__seqplot,&giac::_plotseq,_seqplot_s);
  define_unary_function_ptr5( at_seqplot ,alias_at_seqplot,&__seqplot,0,true);

  static double l2norm(double x,double y){
    return std::sqrt(x*x+y*y);
  }

  static bool get_sol(gen & sol,GIAC_CONTEXT){
    if (is_undef(sol))
      return false;
    if (sol.type==_VECT && sol._VECTptr->size()==2)
      sol=(sol[0]+sol[1])/2;
    if (sol.type==_VECT && sol._VECTptr->size()==1)
      sol=sol[0];
    sol=evalf_double(sol,1,contextptr);
    return sol.type==_DOUBLE_;
  }
  
  // FIXME: this function is using absolute constants 0.1 and 0.2 for checking singular points, they should use better estimates depending on f_orig (search for dfxyabs2)
  static gen in_plotimplicit(const gen& f_orig,const gen&x,const gen & y,double xmin,double xmax,double ymin,double ymax,int nxstep,int nystep,double eps,const vecteur & attributs,const context * contextptr){
#ifdef RTOS_THREADX
    return undef;
#else
    if (f_orig.type==_VECT){
      vecteur & v = *f_orig._VECTptr,w;
      int vs=v.size();
      for (int i=0;i<vs;++i){
	w.push_back(in_plotimplicit(v[i],x,y,xmin,xmax,ymin,ymax,nxstep,nystep,eps,attributs,contextptr));
      }
      return gen(w,_SEQ__VECT);
    }
    if (f_orig.is_symb_of_sommet(at_inv) || (is_zero(derive(f_orig,x,contextptr)) && is_zero(derive(f_orig,y,contextptr))) )
      return vecteur(0); // gen(vecteur(0),_SEQ__VECT);
    if (f_orig.is_symb_of_sommet(at_prod) && f_orig._SYMBptr->feuille.type==_VECT){
      vecteur res;
      vecteur & fv = *f_orig._SYMBptr->feuille._VECTptr;
      int s=fv.size();
      for (int i=0;i<s;++i){
	gen tmp=in_plotimplicit(fv[i],x,y,xmin,xmax,ymin,ymax,nxstep,nystep,eps,attributs,contextptr);
	if (!is_undef(tmp))
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
	  return in_plotimplicit(farg,x,y,xmin,xmax,ymin,ymax,nxstep,nystep,eps,attributs,contextptr);
	else
	  return vecteur(0); // gen(vecteur(0),_SEQ__VECT);
      }
    }
    gen attribut=attributs.empty()?default_color(contextptr):attributs[0];
    gen lieu_geo;
    if (equation2geo2d(f_orig,x,y,lieu_geo,gnuplot_tmin,gnuplot_tmax,gnuplot_tstep,contextptr))
      return put_attributs(lieu_geo,attributs,contextptr);
    // make a lattice between gnuplot_xmin/gnuplot_xmax and ymin/ymax
    // find zeros of f inside each square and follow the branches
    bool is_regular=lop(f_orig,at_abs).empty() && lop(f_orig,at_sign).empty();
#ifndef WIN32
    bool old_iograph=io_graph(contextptr);
    if (thread_eval_status(contextptr)!=1)
      io_graph(false,contextptr);
#endif
#ifdef HAVE_LIBGSL //
    gsl_set_error_handler_off();
#endif //
    if (!nystep){
      nxstep=int(std::sqrt(double(std::abs(nxstep))));
      nystep=nxstep;
    }
    if (ulonglong(nxstep)*nystep>100*100){
      nxstep=100;
      nystep=100;
    }
    double xstep=(xmax-xmin)/nxstep;
    double ystep=(ymax-ymin)/nystep;
    identificateur xloc(" xloc"),yloc(" yloc");
    vecteur xy(makevecteur(x,y)),locvar(makevecteur(xloc,yloc));
    gen f=quotesubst(f_orig,xy,locvar,contextptr).evalf(1,contextptr);
    gen dfx=derive(f_orig,x,contextptr),dfy=derive(f_orig,y,contextptr);
    if (is_undef(dfx) || is_undef(dfy))
      return dfx+dfy;
    vecteur localvar(makevecteur(xloc,yloc));
    context * newcontextptr=(context *) contextptr;
    int protect=bind(makevecteur(xmin,ymin),localvar,newcontextptr);
    vector< vector<bool> > visited(nxstep+2,vector<bool>(nystep+2));
    // vector< vector<bool> > visited(nxstep+2,vector<bool>(nystep+2) );
    vector< vector<double> > 
      fxy(nxstep+1,vector<double>(nystep+1)),
      dfxorig(nxstep+1,vector<double>(nystep+1)),
      dfyorig(nxstep+1,vector<double>(nystep+1)),
      dfxyorig_abs(nxstep+1,vector<double>(nystep+1));
    vector< vector<double> > xorig(nxstep+1,vector<double>(nystep+1)),yorig(nxstep+1,vector<double>(nystep+1));
    gen gtmp;
    // initialize each cell to non visited
    local_sto_double(ymin,yloc,newcontextptr);
    // yloc.localvalue->back()._DOUBLE_val = ymin ;
    for (int i=0;i<=nxstep+1;++i){
      for (int j=0;j<=nystep+1;++j)
	visited[i][j]=false;
    }
    vecteur singular_points,singular_points_tangents,singular_points_directions;
    for (int i=0;i<=nxstep;++i){
      local_sto_double(ymin,yloc,newcontextptr);
      // yloc.localvalue->back()._DOUBLE_val = ymin;
      for (int j=0;j<=nystep;++j){
	gtmp=f.evalf2double(eval_level(contextptr),newcontextptr);
	if (gtmp.type==_DOUBLE_)
	  fxy[i][j]=gtmp._DOUBLE_val==0?1e-200:gtmp._DOUBLE_val;
	else
	  fxy[i][j]=0;
	local_sto_double_increment(ystep,yloc,newcontextptr);
	// yloc.localvalue->back()._DOUBLE_val += ystep;
      }
      local_sto_double_increment(xstep,xloc,newcontextptr);
      // xloc.localvalue->back()._DOUBLE_val += xstep;
    }
    leave(protect,localvar,newcontextptr);
    double xx=xmin,yy=ymin,tmp,xcurrent,ycurrent;
    vecteur xy1(xy);
    lvar(f_orig,xy1); // check for an algebraic curve
    if (xy1==xy){
      // Polynomial singular points: solve([f_orig,dfx,dfy],xy)
      singular_points=gsolve(*exact(makevecteur(f_orig,dfx,dfy),contextptr)._VECTptr,xy,false,contextptr);
      for (int k=0;k<int(singular_points.size());k++){
	gen sp=singular_points[k];
	gen spd=evalf_double(sp,1,contextptr);
	if (sp.type!=_VECT || sp._VECTptr->size()!=2 || spd[0].type!=_DOUBLE_ || spd[1].type!=_DOUBLE_ )
	  continue;
	int i=int((spd[0]._DOUBLE_val-xmin)/xstep);
	int j=int((spd[1]._DOUBLE_val-xmin)/xstep);
	if (i>=nxstep || j>=nystep)
	  continue;
	for (int ii=i-1;ii<i+1;++ii){
	  for (int jj=j-1;jj<j+1;++jj){
	    if ( ii>=0 && ii<nxstep && jj>=0 && jj<nystep )
	      visited[ii][jj]=true;
	  }
	}
	// find all tangents starting from sp
	for (int order=2;order<10;++order){
	  gen tays=series(subst(f_orig,xy,xy-sp,false,contextptr),xy,makevecteur(0,0),order,0,contextptr);
	  if (!is_zero(tays)){
	    singular_points_tangents.clear();
	    // non-zero homogeneous expansion
	    // find roots of taylor expansion
	    gen t(identificateur(" implicitplot"));
	    tays=subst(tays,xy,subvecteur(makevecteur(1,t),*sp._VECTptr),false,contextptr);
	    // search for a multiple root, if last_direction is near a multiple root
	    // of even multiplicity change last_direction sign
	    gen sqfftays=_quo(gen(makevecteur(tays,_gcd(gen(makevecteur(tays,derive(tays,t,contextptr)),_SEQ__VECT),contextptr),t),_SEQ__VECT),contextptr);
	    gen r=_proot(gen(makevecteur(sqfftays,t),_SEQ__VECT),contextptr);
	    *logptr(contextptr) << gettext("Near ") << sp << ", 1/epsilon^2*f(" << sp<< "+epsilon*[1,t])=" << subst(tays,t,t__IDNT_e,false,contextptr) << " roots " << r << endl;
	    if (r.type==_VECT){
	      int total=0;
	      for (unsigned kr=0;kr<r._VECTptr->size();++kr){
		// find multiplicity
		int mult=0;
		gen quo,tmp=tays;
		for (++total;;++mult,++total){
		  quo=_quorem(gen(makevecteur(tmp,t-r[kr],t),_SEQ__VECT),contextptr);
		  if (quo.type!=_VECT || quo._VECTptr->size()!=2 || !is_zero(quo._VECTptr->back()))
		    break;
		  tmp=quo._VECTptr->front();
		}
		if (!is_zero(im(r[kr],contextptr)))
		  continue;
		// add 2 half-tangents with slope r[kr]
		singular_points_tangents.push_back(makevecteur(i,j,spd,1,r[kr],mult));
		singular_points_tangents.push_back(makevecteur(i,j,spd,-1,-r[kr],mult));
	      } // end for kr
	      // add vertical half-tangents if any
	      if (total<order){
		singular_points_tangents.push_back(makevecteur(i,j,spd,0,1,order-total));
		singular_points_tangents.push_back(makevecteur(i,j,spd,0,-1,order-total));
	      }
	    } // end if (r.type==_VECT)
	    // dirc contains affixes of directions
	    vecteur dirc;
	    for (unsigned k=0;k<singular_points_tangents.size();++k){
	      gen tmp=singular_points_tangents[k];
	      if (tmp.type==_VECT && tmp._VECTptr->size()>=6){
		tmp=tmp[3]+cst_i*tmp[4];
		dirc.push_back(tmp);
	      }
	    }
	    for (int sing=0;sing<int(singular_points_tangents.size());++sing){
	      // replace tangents by directions
	      gen tmp=singular_points_tangents[sing];
	      if (tmp.type==_VECT && tmp._VECTptr->size()>=6){
		gen sp=(*tmp._VECTptr)[2];
		gen lastsing=gen(sp[0],sp[1]);
		double xcurrent=evalf_double(sp[0]+xstep/3*(*tmp._VECTptr)[3],1,contextptr)._DOUBLE_val;
		double ycurrent=evalf_double(sp[1]+xstep/3*(*tmp._VECTptr)[4],1,contextptr)._DOUBLE_val;
		// find solutions near half tangent (no more than mult)
		if (is_greater(abs(tmp[4],contextptr),abs(tmp[3],contextptr),contextptr)){
		  // search x
		  gen fx=subst(f_orig,y,ycurrent,false,contextptr);
		  int iszero=-1;
		  vecteur v=bisection_solver(fx,x,xcurrent-xstep,xcurrent+xstep,iszero,contextptr);
		  for (unsigned vi=0;vi<v.size();++vi){
		    gen sol=v[vi];
		    if (sol.type!=_DOUBLE_)
		      continue;
		    xcurrent=sol._DOUBLE_val;
		    sol=gen(xcurrent,ycurrent);
		    // check that sol-lastsing is in the same direction as tmp[3],tmp[4]
		    gen curdir=(sol-lastsing),tmpdir=tmp[3]+cst_i*tmp[4];
		    gen tst=abs(arg(curdir/tmpdir,contextptr),contextptr);
		    unsigned dir_i=0;
		    for (;dir_i<dirc.size();++dir_i){
		      // if (diri==sing) continue;
		      // if arg of quotient is larger in abs value than a quotient
		      // with another direction, then this direction is invalid
		      gen tst_i=abs(arg(curdir/dirc[dir_i],contextptr),contextptr);
		      if (is_strictly_greater(tst,tst_i,contextptr))
			break;
		    }
		    if (dir_i==dirc.size()){
		      singular_points_directions.push_back(makevecteur(i,j,lastsing,sol));
		    }
		  }
		}
		else { // search y
		  gen fy=subst(f_orig,x,xcurrent,false,contextptr);
		  int iszero=-1;
		  vecteur v=bisection_solver(fy,y,ycurrent-ystep,ycurrent+ystep,iszero,contextptr);
		  for (unsigned vi=0;vi<v.size();++vi){
		    gen sol=v[vi];
		    if (sol.type!=_DOUBLE_)
		      continue;
		    ycurrent=sol._DOUBLE_val;
		    sol=gen(xcurrent,ycurrent);
		    // check that sol-lastsing is in the same direction as tmp[3],tmp[4]
		    gen curdir=(sol-lastsing),tmpdir=tmp[3]+cst_i*tmp[4];
		    gen tst=abs(arg(curdir/tmpdir,contextptr),contextptr);
		    unsigned dir_i=0;
		    for (;dir_i<dirc.size();++dir_i){
		      // if (diri==sing) continue;
		      // if arg of quotient is larger in abs value than a quotient
		      // with another direction, then this direction is invalid
		      gen tst_i=abs(arg(curdir/dirc[dir_i],contextptr),contextptr);
		      if (is_strictly_greater(tst,tst_i,contextptr))
			break;
		    }
		    if (dir_i==dirc.size()){
		      singular_points_directions.push_back(makevecteur(i,j,lastsing,sol));
		    }
		  }
		}
	      }
	    }
	    break; // end the for loop on order
	  } // end if !is_zero(tays)
	} // end loop on order
      } // end if k<singular_points.size()
      if (!singular_points.empty())
	*logptr(contextptr) << gettext("Singular points directions: [cell_i, cell_j, singularity, next solution] ") << singular_points_directions << endl;
    }
    bool pathfound;
    vecteur res;
    int i=-1,j=nystep,sing=0;
    gen lastsing;
    bool was_not_singular;
    for (;;){ 
      pathfound=false;
      vecteur chemin;
      int iorig,jorig;
      bool chemin_ok=true;
      bool orig_sing=sing<int(singular_points_directions.size());
      gen lastsingdir;
      // First paths from singular points
      if (orig_sing){
	was_not_singular=false;
	gen tmp=singular_points_directions[sing];
	++sing;
	lastsing=tmp[2];
	chemin.push_back(lastsing);
	xcurrent=evalf_double(re(tmp[3],contextptr),1,contextptr)._DOUBLE_val;
	ycurrent=evalf_double(im(tmp[3],contextptr),1,contextptr)._DOUBLE_val;
	lastsingdir=xcurrent+cst_i*ycurrent;
	// set iorig,jorig
	iorig=int((xcurrent-xmin)/xstep); // FIXME? +.5
	jorig=int((ycurrent-ymin)/ystep); // 
	pathfound=true;
      }
      else {
	was_not_singular=true;
	lastsingdir=undef;
      }
      if (!pathfound){
	++j;
	if (j>nystep){
	j=0;
	  ++i;
	  if (i>nxstep)
	    break;
	  if (debug_infolevel)
	    cout << "// Implicitplot row " << i << endl;
	}
	xx=xmin+i*xstep;
	yy=ymin+j*ystep;
	// If cell has been visited -> done
	if ( visited[i][j] || ( 
			       ( (j && visited[i][j-1]) || visited[i][j+1]) && 
			       ( visited[i+1][j] || (i && visited[i-1][j])) 
				) )
	  continue;
	// now look for annulation from below or left
	tmp=j?fxy[i][j-1]*fxy[i][j]:1;
	if (tmp<0) {
	  pathfound=true;
	  // find an horizontal solution
	  xcurrent=xx;
	  ycurrent=yy;
	  gen fy=subst(f_orig,x,xcurrent,false,contextptr);
	  gen sol=_fsolve(gen(makevecteur(fy,y,makevecteur(ycurrent-ystep,ycurrent),_BISECTION_SOLVER,100*eps),_SEQ__VECT),contextptr);
	  if (!get_sol(sol,contextptr))
	    pathfound=false;
	  else
	    ycurrent=sol._DOUBLE_val;
	}
	else {
	  tmp=i?fxy[i-1][j]*fxy[i][j]:1;
	  if (tmp<0) {
	    pathfound=true;
	    // find a vertical solution
	    xcurrent=xx;
	    ycurrent=yy;
	    gen fx=subst(f_orig,y,ycurrent,false,contextptr);
	    gen sol=_fsolve(gen(makevecteur(fx,x,makevecteur(xcurrent-xstep,xcurrent),_BISECTION_SOLVER,100*eps),_SEQ__VECT),contextptr);
	    if (!get_sol(sol,contextptr))
	      pathfound=false;
	    else
	      xcurrent=sol._DOUBLE_val;
	  }
	}
	if (!pathfound)
	  continue;
	// Annulation found, let's add a path
	jorig=int((ycurrent-ymin)/ystep);
	iorig=int((xcurrent-xmin)/xstep);
	if (visited[iorig][jorig] || (
				      ( (jorig?visited[iorig][jorig-1]:false) || visited[iorig][jorig+1])  && 
				      ( (iorig?visited[iorig-1][jorig]:false) ||visited[iorig+1][jorig] ) 
				      )
	    )
	  continue;
      } // end if (!pathfound)
      chemin.push_back(gen(xcurrent,ycurrent));
      visited[iorig][jorig]=true;
      int icur,jcur,oldi=iorig,oldj=jorig;
      xorig[iorig][jorig]=xcurrent;
      yorig[iorig][jorig]=ycurrent;
      gtmp=subst(dfx,xy,makevecteur(xcurrent,ycurrent),false,contextptr).evalf2double(eval_level(contextptr),contextptr);
      if (gtmp.type==_DOUBLE_)
	dfxorig[iorig][jorig]=gtmp._DOUBLE_val;
      else
	dfxorig[iorig][jorig]=oldi?(fxy[oldi][oldj]-fxy[oldi-1][oldj])/xstep:(fxy[oldi+1][oldj]-fxy[oldi][oldj])/xstep;
      gtmp=subst(dfy,xy,makevecteur(xcurrent,ycurrent),false,contextptr).evalf2double(eval_level(contextptr),contextptr);
      if (gtmp.type==_DOUBLE_)
	dfyorig[iorig][jorig]=gtmp._DOUBLE_val;
      else
	dfyorig[iorig][jorig]=oldj?(fxy[oldi][oldj]-fxy[oldi][oldj-1])/ystep:(fxy[oldi][oldj+1]-fxy[oldi][oldj])/ystep;
      dfxyorig_abs[iorig][jorig]=l2norm(dfxorig[iorig][jorig],dfyorig[iorig][jorig]);
      int sign=1; // + for increasing y, - for decreasing y
      if (chemin.size()==2){
	gen tmp=chemin[1]-chemin[0],direction(dfyorig[iorig][jorig],-dfxorig[iorig][jorig]);
	if (is_greater(abs(arg(tmp/direction,contextptr),contextptr),cst_pi_over_2,contextptr))
	  sign=-1;
      }
      else {
	if ( (dfyorig[iorig][jorig]<-100*eps*dfxyorig_abs[iorig][jorig]) ||
	     ((dfyorig[iorig][jorig]<100*eps*dfxyorig_abs[iorig][jorig])&&
	      (dfxorig[iorig][jorig]>0)) )
	  sign=-1;
      }
      gen last_direction=0;
      bool change_sign=false;
      for (int count=0;count<nxstep*nystep;){
	vecteur xycurrent(makevecteur(xcurrent,ycurrent));
	double dfxcurrent,dfycurrent;
	gtmp=subst(dfx,xy,xycurrent,false,contextptr).evalf2double(eval_level(contextptr),contextptr);
	//bool use_newton=is_regular;
	bool use_newton=false; // FIXME?? Newton does not seem to work
	if (gtmp.type==_DOUBLE_)
	  dfxcurrent=gtmp._DOUBLE_val;
	else {
	  use_newton=false;
	  dfxcurrent=oldi?(fxy[oldi][oldj]-fxy[oldi-1][oldj])/xstep:(fxy[oldi+1][oldj]-fxy[oldi][oldj])/xstep;
	}
	gtmp=subst(dfy,xy,xycurrent,false,contextptr).evalf2double(eval_level(contextptr),contextptr);
	if (gtmp.type==_DOUBLE_)
	  dfycurrent=gtmp._DOUBLE_val;
	else {
	  dfycurrent=oldj?(fxy[oldi][oldj]-fxy[oldi][oldj-1])/ystep:(fxy[oldi][oldj+1]-fxy[oldi][oldj])/ystep;
	  use_newton=false;
	}
	if (sign==-1){
	  dfxcurrent=-dfxcurrent;
	  dfycurrent=-dfycurrent;
	}
	// (dfxcurrent,dfycurrent) is normal to the path
	// If it's near 0 we are near a singular point,
	// that we try to cross by using the same direction
	// Otherwise go to the next cell, and end if at original i,j
	// or at the border
	gen direction(dfycurrent,-dfxcurrent);
	double dfxycurrent_abs=l2norm(dfxcurrent,dfycurrent);
	// Improve eval of derivative at target point
	double dfxyabs2=dfxycurrent_abs;
	gen newxy=undef;
	if (was_not_singular){
	  double pascoeff=std::min(xstep,ystep)/std::sqrt(dfycurrent*dfycurrent+dfxcurrent*dfxcurrent);
	  gen pas=pascoeff*makevecteur(dfycurrent,-dfxcurrent);
	  newxy=xycurrent+pas;
	  gtmp=subst(dfx,xy,newxy,false,contextptr).evalf2double(eval_level(contextptr),contextptr);
	  if (gtmp.type==_DOUBLE_){
	    double d1=gtmp._DOUBLE_val;
	    gtmp=subst(dfy,xy,newxy,false,contextptr).evalf2double(eval_level(contextptr),contextptr);
	    if (gtmp.type==_DOUBLE_){
	      dfxyabs2=std::sqrt(gtmp._DOUBLE_val*gtmp._DOUBLE_val+d1*d1);
	    }
	  }
	}
	if (was_not_singular && dfxyabs2<0.1 && !is_zero(last_direction)){
	  // perhaps near a singular point
	  // compare precedent direction with singular_points_directions
	  gen sp=undef;
	  int k=0;
	  for (;k<int(singular_points.size());k++){
	    sp=singular_points[k];
	    if (sp.type==_VECT && sp._VECTptr->size()==2 && is_greater(2*xstep,abs(sp[0]-newxy[0],contextptr),contextptr) && is_greater(2*ystep,abs(sp[1]-newxy[1],contextptr),contextptr))
	      break;
	  }
	  if (k<int(singular_points.size())){
	    chemin.push_back(evalf_double(sp[0]+cst_i*sp[1],1,contextptr));
	    int pos=-1; 
	    gen theta=7;
	    // remove incoming direction from singular_points_direction
	    for (k=0;k<int(singular_points_directions.size());++k){
	      gen tmp=singular_points_directions[k];
	      if (chemin.back()-tmp[2]!=0)
		continue;
	      // compare directions
	      gen cur=abs(arg((tmp[2]-tmp[3])/last_direction,contextptr),contextptr);
	      if (is_greater(theta,cur,contextptr)){
		pos=k;
		theta=cur;
	      }
	    }
	    if (pos>=sing)
	      singular_points_directions.erase(singular_points_directions.begin()+pos);
	    else
	      *logptr(contextptr) << gettext("Bad branch, questionnable accuracy") << endl;
	    break; // singular points were already done
	  }
	  else { 
	    // FIXME: find a numerical singular point near this point 
	    // Find all branches by solving the equation on a circle of small radius
	    // centerd at the numerical singular point
	    // Add them to singular_points_directions
	  }
	  // otherwise continue in the same direction
	  direction=last_direction;
	  change_sign=true;
	}
	else {
	  if (dfxyabs2>=0.2)
	    was_not_singular=true;
	  if (change_sign){
	    sign=-sign;
	    direction=-direction;
	    change_sign=false;
	  }
	  direction=direction/dfxycurrent_abs;
	  last_direction=direction;
	}
	double deltax=evalf_double(re(direction,contextptr),eval_level(contextptr),contextptr)._DOUBLE_val,deltay=evalf_double(im(direction,contextptr),eval_level(contextptr),contextptr)._DOUBLE_val;
	double thestep=ystep/2;
	if (xstep<ystep)
	  thestep=xstep/2;
	xcurrent += thestep*deltax;
	ycurrent += thestep*deltay;
	gen sol,fy,fx;
	// Test for mostly horizontal tangeant
	if (fabs(deltay)<fabs(deltax)) {
	  fy=subst(f_orig,x,xcurrent,false,contextptr);
#if 0
	  int iszero=-1;
	  vecteur sol1=bisection_solver(fy,y,ycurrent-ystep,ycurrent+ystep,iszero,contextptr);
	  if (!sol1.empty()){
	    if (deltay<0){ // reorder sol1 if we have more than 1 sol (almost horizontal)
	      reverse(sol1.begin(),sol1.end());
	      for (unsigned k=0;k<sol1.size();--k){
		if (sol1[k].type==_DOUBLE_)
		  chemin.push_back(xcurrent+cst_i*sol1[k]);
	      }
	    }
	    sol=sol1.back();
	  }
	  else
	    sol=undef;
#else
	  if (is_positive(subst(fy,y,ycurrent-ystep,false,contextptr)*subst(fy,y,ycurrent+ystep,false,contextptr),contextptr) || use_newton){
	    sol=_fsolve(gen(makevecteur(fy,y,ycurrent,_NEWTON_SOLVER),_SEQ__VECT),contextptr);
	  }
	  else {
	    sol=_fsolve(gen(makevecteur(fy,y,makevecteur(ycurrent-ystep,ycurrent+ystep),_BISECTION_SOLVER,100*eps),_SEQ__VECT),contextptr);
	    get_sol(sol,contextptr);
	  }
#endif
	  if (sol.type==_DOUBLE_){
	    if (fabs(ycurrent-sol._DOUBLE_val)>2*ystep){
	      chemin_ok=false;
	      break;
	    }
	    else
	      ycurrent=sol._DOUBLE_val;
	  }
	  else {
	    *logptr(contextptr) << gettext("Warning! Could not loop or reach boundaries ") << fy << endl;
	    break;
	  }
	}
	else {
	  // recompute solution
	  fx=subst(f_orig,y,ycurrent,false,contextptr);
#if 0
	  int iszero=-1;
	  vecteur sol1=bisection_solver(fx,x,xcurrent-xstep,xcurrent+xstep,iszero,contextptr);
	  if (!sol1.empty()){
	    if (deltax<0){ // reorder sol1 if we have more than 1 sol (almost horizontal)
	      reverse(sol1.begin(),sol1.end());
	      for (unsigned k=0;k<sol1.size();--k){
		if (sol1[k].type==_DOUBLE_)
		  chemin.push_back(sol1[k]+cst_i*ycurrent);
	      }
	    }
	    sol=sol1.back();
	  }
	  else
	    sol=undef;
#else
	  if (is_positive(subst(fx,x,xcurrent-xstep,false,contextptr)*subst(fx,x,xcurrent+xstep,false,contextptr),contextptr) || use_newton){
	    sol=_fsolve(gen(makevecteur(fx,x,xcurrent,_NEWTON_SOLVER),_SEQ__VECT),contextptr);
	  }
	  else {
	    sol=_fsolve(gen(makevecteur(fx,x,makevecteur(xcurrent-xstep,xcurrent+xstep),_BISECTION_SOLVER,100*eps),_SEQ__VECT),contextptr);
	    get_sol(sol,contextptr);
	  }
#endif
	  if (sol.type==_DOUBLE_){
	    if (fabs(xcurrent-sol._DOUBLE_val)>2*xstep){
	      chemin_ok=false;
	      break;
	    }
	    else
	      xcurrent=sol._DOUBLE_val;
	  }
	  else {
	    *logptr(contextptr) << gettext("Warning! Could not loop or reach boundaries ") << fx << endl;
	    break;
	  }	    
	}
	chemin.push_back(gen(xcurrent,ycurrent));
	// check cell
	icur=int((xcurrent-xmin)/xstep);
	jcur=int((ycurrent-ymin)/ystep);
	if (icur<0 || icur>nxstep || jcur<0 || jcur>nystep ){
	  // try to reverse chemin
	  if (chemin.empty() || orig_sing)
	    break;
	  gen orig=chemin.front();
	  double x_orig=evalf_double(re(orig,contextptr),1,contextptr)._DOUBLE_val;
	  double y_orig=evalf_double(im(orig,contextptr),1,contextptr)._DOUBLE_val;
	  int i_orig=int((x_orig-xmin)/xstep);
	  int j_orig=int((y_orig-ymin)/ystep);
	  if (i_orig<0 || i_orig>nxstep || j_orig<0 || j_orig>nystep)
	    break;
	  // revert chemin and restart in reverse direction
	  reverse(chemin.begin(),chemin.end());
	  xcurrent=x_orig;
	  ycurrent=y_orig;
	  sign=-sign;
	  continue;
	}
	if ( (icur==oldi) && (jcur==oldj) ){
	  ++count;
	  continue;
	}
	if (visited[icur][jcur]){  
	  if (0.1*dfxyorig_abs[icur][jcur]*dfxycurrent_abs>fabs(dfxcurrent*dfyorig[icur][jcur]-dfycurrent*dfxorig[icur][jcur])){
	    if (count<2)
	      chemin_ok=false;
	    else {
	      // join to this point
	      if (is_greater(xstep,abs(re(chemin.front()-xycurrent,contextptr),contextptr),contextptr) && is_greater(ystep,abs(im(chemin.front()-xycurrent,contextptr),contextptr),contextptr) )
		chemin.push_back(chemin.front());
	      else
		chemin.push_back(gen(xorig[icur][jcur],yorig[icur][jcur]));
	    }
	    break;
	  }
	}
	else {
	  visited[icur][jcur]=true;
	  dfxorig[icur][jcur]=dfxcurrent;
	  dfyorig[icur][jcur]=dfycurrent;
	  xorig[icur][jcur]=xcurrent;
	  yorig[icur][jcur]=ycurrent;
	  if (debug_infolevel)
	    *logptr(contextptr)	<< icur << " " << jcur << " " << xcurrent << " " << ycurrent <<endl;	  
	  dfxyorig_abs[icur][jcur]=dfxycurrent_abs;
	}
	if (debug_infolevel)
	  *logptr(contextptr) << gettext("Implicitplot ") << icur << " " << jcur << endl;	  
	oldi=icur;
	oldj=jcur;
      }
      if (!chemin_ok)
	*logptr(contextptr) << gettext("Warning! Could not loop or reach boundaries ") << endl;
      res.push_back(symb_pnt(gen(chemin,_GROUP__VECT),attribut,contextptr));
    } // end for(;;)
#ifndef WIN32
#ifdef WITH_GNUPLOT
    if (child_id) plot_instructions.push_back(res);
#endif
    io_graph(old_iograph,contextptr);
#endif // WIN32
    return res; // gen(res,_SEQ__VECT);
    return zero;
#endif // RTOS_THREADX
  }

  gen plotimplicit(const gen& f_orig,const gen&x,const gen & y,double xmin,double xmax,double ymin,double ymax,int nxstep,int nystep,double eps,const vecteur & attributs,bool unfactored,const context * contextptr){
    if ( (x.type!=_IDNT) || (y.type!=_IDNT) )
      return gensizeerr(gettext("Variables must be free"));
    bool cplx=complex_mode(contextptr);
    if (cplx){
      complex_mode(false,contextptr);
      *logptr(contextptr) << gettext("Impliciplot: temporarily swtiching to real mode") << endl;
    }
    // factorization without sqrt
    gen ff(unfactored?f_orig:factor(f_orig,false,contextptr));
    gen res=in_plotimplicit(ff,x,y,xmin,xmax,ymin,ymax,nxstep,nystep,eps,attributs,contextptr);
    if (cplx)
      complex_mode(true,contextptr);
    return res;
  }

  gen _plotimplicit(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return plotimplicit(remove_equal(args),vx_var,y__IDNT_e,gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,20*gnuplot_pixels_per_eval,0,epsilon(contextptr),vecteur(1,default_color(contextptr)),false,contextptr);
    // vecteur v(plotpreprocess(args));
    vecteur v(*args._VECTptr);
    if (v.size()<2)
      return gensizeerr(contextptr);
    if (v[1].is_symb_of_sommet(at_equal) && v[1]._SYMBptr->feuille._VECTptr->front()==at_display)
      v.insert(v.begin()+1,makevecteur(x__IDNT_e,y__IDNT_e));
    if ( v[1].type==_VECT) {
      if (v[1]._VECTptr->size()==2 ){
	v.insert(v.begin()+2,v[1]._VECTptr->back());
	v[1]=v[1]._VECTptr->front();
	return _plotimplicit(v,contextptr);
      }
      if (v[1]._VECTptr->size()==3 ){
	v.insert(v.begin()+2,(*v[1]._VECTptr)[2]);
	v.insert(v.begin()+2,(*v[1]._VECTptr)[1]);
	v[1]=v[1]._VECTptr->front();
	return _plotimplicit(v,contextptr);
      }
    }
    int nstep=20*gnuplot_pixels_per_eval,jstep=0,kstep=0;
    double xmin,xmax,ymin,ymax,zmin,zmax;
    vecteur attributs(1,default_color(contextptr));
    gen x,y,z;
    readrange(v[1],gnuplot_xmin,gnuplot_xmax,x,xmin,xmax,contextptr);
    readrange(v[2],gnuplot_ymin,gnuplot_ymax,y,ymin,ymax,contextptr);
    bool dim3=v.size()>3;
    if (dim3)
      dim3=readrange(v[3],gnuplot_zmin,gnuplot_zmax,z,zmin,zmax,contextptr);
    else
      zmin=zmax=0.0;
    bool unfactored=false;
    read_option(v,xmin,xmax,ymin,ymax,zmin,zmax,attributs,nstep,jstep,kstep,unfactored,contextptr);
    if (dim3)
      return plotimplicit(remove_equal(v[0]),x,y,z,xmin,xmax,ymin,ymax,zmin,zmax,nstep,jstep,kstep,epsilon(contextptr),attributs,unfactored,contextptr);
    else
      return plotimplicit(remove_equal(v[0]),x,y,xmin,xmax,ymin,ymax,nstep,jstep,epsilon(contextptr),attributs,unfactored,contextptr);
  }
  static const char _plotimplicit_s []="plotimplicit";
  static define_unary_function_eval (__plotimplicit,&giac::_plotimplicit,_plotimplicit_s);
  define_unary_function_ptr5( at_plotimplicit ,alias_at_plotimplicit,&__plotimplicit,0,true);

  static const char _implicitplot_s []="implicitplot";
  static define_unary_function_eval (__implicitplot,&giac::_plotimplicit,_implicitplot_s);
  define_unary_function_ptr5( at_implicitplot ,alias_at_implicitplot,&__implicitplot,0,true);

  static bool is_approx0(const gen & a,double dx,double dy){
    if (a.type==_CPLX) 
      return (fabs(a._CPLXptr->_DOUBLE_val) < 1e-6*dx) && (fabs((a._CPLXptr+1)->_DOUBLE_val) < 1e-6*dy);
    if (a.type==_REAL)
      return (fabs(a._CPLXptr->_DOUBLE_val) < 1e-6*dx);
    return is_zero(a);
  }

#ifdef RTOS_THREADX
  gen plotcontour(const gen & f0,bool contour,GIAC_CONTEXT){
    return undef;
  }
#else
  // v is a list of polygon vertices, add [A,B] to it
  static void add_segment(vecteur & v,const gen & A,const gen & B,double dx,double dy){
    if (is_approx0(A-B,dx,dy))
      return;
    iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      gen & tmp = *it;
      if (tmp.type==_VECT && !tmp._VECTptr->empty()){
	gen & b =tmp._VECTptr->back();
	if (is_approx0(b-A,dx,dy)){
	  tmp._VECTptr->push_back(B);
	  break;
	}
	if (is_approx0(b-B,dx,dy)){
	  tmp._VECTptr->push_back(A);
	  break;
	}
	gen & a=tmp._VECTptr->front();
	if (is_approx0(a-A,dx,dy)){
	  tmp._VECTptr->insert(tmp._VECTptr->begin(),B);
	  break;
	}
	if (is_approx0(a-B,dx,dy)){
	  tmp._VECTptr->insert(tmp._VECTptr->begin(),A);
	  break;
	}
      }
    }
    if (it==itend)
      v.push_back(makevecteur(A,B));
  }

  static void glue_components(vecteur & v,double dx,double dy){
    int s=v.size();
    for (int i=0;i<s-1;++i){
      gen & cur = v[i];
      for (int j=i+1;j<s;++j){
	gen & next=v[j]; 
	if (cur.type==_VECT && next.type==_VECT && !cur._VECTptr->empty() && !next._VECTptr->empty()){
	  if (is_approx0(cur._VECTptr->front()-next._VECTptr->back(),dx,dy) || is_approx0(cur._VECTptr->front()-next._VECTptr->front(),dx,dy))
	    reverse(cur._VECTptr->begin(),cur._VECTptr->end());
	  if (is_approx0(cur._VECTptr->back()-next._VECTptr->back(),dx,dy))
	    reverse(next._VECTptr->begin(),next._VECTptr->end());
	  if (is_approx0(cur._VECTptr->back()-next._VECTptr->front(),dx,dy)){
	    // FIXME mergevecteur will repeat cur.back and next.front
	    v[i]=mergevecteur(*cur._VECTptr,*next._VECTptr);
	    v.erase(v.begin()+j);
	    j=i; // restart j loop
	    --s;
	  }
	} // endif
      } // end for j
    } // end for i
  }

  static void polygonify(vecteur & v,const vecteur & attributs,GIAC_CONTEXT){
    iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      gen & tmp=*it;
      if (tmp.type==_VECT && tmp._VECTptr->size()>1){
	tmp.subtype=_GROUP__VECT;
	*it=pnt_attrib(tmp,attributs,contextptr);
      }
    }
  }

  gen plot_array(const vector< vector< double> > & fij,int imax,int jmax,double xmin,double xmax,double dx,double ymin,double ymax,double dy,const vecteur & lz,const vecteur & attributs,bool contour,GIAC_CONTEXT){
    // do linear interpolation between points for levels
    // with a marching rectangle
    // if all 4 vertices values are > or < nothing added
    // else 3/1 -> one segment between 2 interpolated zeros
    // 2/2 
    // ++ __   +- + or \\   +- |
    // --      -+           +- |
    int nz=lz.size();
    vector<double> Z;
    for (int k=0;k<nz;k++){
      gen zg=evalf_double(lz[k],eval_level(contextptr),contextptr);
      if (zg.type==_DOUBLE_)
	Z.push_back(zg._DOUBLE_val);
    }
    nz=Z.size();
    vector<vecteur> res(nz);
    for (int i=0;i<imax-1;++i){
      double x=xmin+i*dx;
      for (int j=0;j<jmax-1;++j){
	double y=ymin+j*dy;
	double a=fij[i][j+1];
	double b=fij[i+1][j+1];
	double c=fij[i][j];
	double d=fij[i+1][j]; 
	double eps=1e-12;
	// a b (y+dy)
	// c d (y)
	for (int k=0;k<nz;k++){
	  double z=Z[k];
	  if (a==z)
	    a += z?z*eps:eps;
	  if (b==z)
	    b += z?z*eps:eps;
	  if (c==z)
	    c += z?z*eps:eps;
	  if (d==z)
	    d += z?z*eps:eps;
	  bool ab=(a-z)*(b-z)<=0,ca=(a-z)*(c-z)<=0,db=(d-z)*(b-z)<=0,cd=(d-z)*(c-z)<=0;
	  gen AB,CA,DB,CD;
	  // intercepts
	  if (ab)
	    AB=gen(x+(a-z)/(a-b)*dx,y+dy);
	  if (cd)
	    CD=gen(x+(c-z)/(c-d)*dx,y);
	  if (ca)
	    CA=gen(x,y+(c-z)/(c-a)*dy);
	  if (db)
	    DB=gen(x+dx,y+(d-z)/(d-b)*dy);
	  // diagonal
	  if (ab && ca){
	    add_segment(res[k],AB,CA,dx,dy);
	    ab=false;
	    ca=false;
	  }
	  if (ab && db){
	    add_segment(res[k],AB,DB,dx,dy);
	    ab=false;
	    db=false;
	  }
	  if (db && cd){
	    add_segment(res[k],DB,CD,dx,dy);
	    db=false;
	    cd=false;
	  }
	  if (ca && cd){
	    add_segment(res[k],CA,CD,dx,dy);
	    ca=false;
	    cd=false;
	  }
	  // horizontal
	  if (ab && cd)
	    add_segment(res[k],AB,CD,dx,dy);
	  // vertical
	  if (ca && db)
	    add_segment(res[k],CA,DB,dx,dy);
	}
      }
    }
    vecteur res0,attr(attributs);
    vecteur legendes,colors;
    if (attr.empty())
      attr.push_back(0);
    if (attributs.size()<2){
      attr.push_back(contour?lz:0);
    }
    if (attr[0].type==_VECT)
      colors=*attr[0]._VECTptr;
    else
      colors=vecteur(1,attr[0]);
    if (attr[1].type==_VECT)
      legendes=*attr[1]._VECTptr;
    int legs=legendes.size();
    int cols=colors.size();
    for (int k=0;k<nz;++k){
      attr[0]=colors[k<cols?k:0];
      if (!contour && attr[0].type==_INT_)
	attr[0].val=attr[0].val | _FILL_POLYGON;
      attr[1]=k<legs?legendes[k]:string2gen("",false);
      glue_components(res[k],dx,dy);
      if (attr[0].type==_INT_ && (attr[0].val & _FILL_POLYGON)){
	// now finsih gluing with the xmin/xmax/ymin/ymax border
	int ncomp=res[k].size();
	for (int n=0;n<ncomp;n++){
	  gen composante=res[k][n];
	  // look if begin and end of composante is at border
	  if (composante.type!=_VECT || composante._VECTptr->size()<2)
	    continue;
	  gen begin=composante._VECTptr->front(),end=composante._VECTptr->back();
	  if (is_approx0(begin-end,dx,dy)){
	    // look if + inside ok else we must break the composante
	    // to display outside instead of inside
	    // find the nearest point to xmin,ymin
	    vecteur cv=*composante._VECTptr;
	    gen xymin(xmin,ymin);
	    int cs=cv.size(),pos=0;
	    gen dmin=abs(begin-xymin,contextptr);
	    for (int i=1;i<cs;++i){
	      gen dcur=abs(cv[i]-xymin,contextptr);
	      if (is_strictly_positive(dmin-dcur,contextptr)){
		pos=i;
		dmin=dcur;
	      }
	    }
	    // make a little step from cv[pos] in direction of xymin
	    // and check sign of f
	    int itmp=int((re(cv[pos],contextptr)._DOUBLE_val-xmin)/dx-0.5);
	    int jtmp=int((im(cv[pos],contextptr)._DOUBLE_val-ymin)/dy-0.5);
	    if (fij[itmp][jtmp]>Z[k]){
	      // no luck, build the exterior
	      res[k][n]=mergevecteur(mergevecteur(vecteur(cv.begin(),cv.begin()+pos+1),makevecteur(xymin,gen(xmax,ymin),gen(xmax,ymax),gen(xmin,ymax),xymin)),vecteur(cv.begin()+pos,cv.end()));
	    }
	    continue;
	  }
	  double bx,by=0,ex,ey=0;
	  if (begin.type==_CPLX){
	    bx=begin._CPLXptr->_DOUBLE_val;
	    by=(begin._CPLXptr+1)->_DOUBLE_val;
	  } else {
	    if (begin.type==_DOUBLE_)
	      bx=begin._DOUBLE_val;
	    else
	      continue;
	  }
	  if (end.type==_CPLX){
	    ex=end._CPLXptr->_DOUBLE_val;
	    ey=(end._CPLXptr+1)->_DOUBLE_val;
	  } else {
	    if (end.type==_DOUBLE_)
	      ex=end._DOUBLE_val;
	    else
	      continue;
	  }
	  bool bxmin=fabs(bx-xmin)<1e-6*dx;
	  bool bxmax=fabs(bx-xmax)<1e-6*dx;
	  bool bymin=fabs(by-ymin)<1e-6*dy;
	  bool bymax=fabs(by-ymax)<1e-6*dy;
	  bool exmin=fabs(ex-xmin)<1e-6*dx;
	  bool exmax=fabs(ex-xmax)<1e-6*dx;
	  bool eymin=fabs(ey-ymin)<1e-6*dy;
	  bool eymax=fabs(ey-ymax)<1e-6*dy;
	  if ( (bxmin || bxmax || bymin || bymax) &&
	       (exmin || exmax || eymin || eymax) ){
	    bxmin=fabs(bx-xmin)<1.1*dx;
	    bxmax=fabs(bx-xmax)<1.1*dx;
	    bymin=fabs(by-ymin)<1.1*dy;
	    bymax=fabs(by-ymax)<1.1*dy;
	    exmin=fabs(ex-xmin)<1.1*dx;
	    exmax=fabs(ex-xmax)<1.1*dx;
	    eymin=fabs(ey-ymin)<1.1*dy;
	    eymax=fabs(ey-ymax)<1.1*dy;
	    int i,j,di,dj,ij,ijmax=2*(imax+jmax); // perimeter
	    vecteur coins;
	    // begin and end are on border, try to connect end
	    if (exmin || exmax){
	      // move y to the right, is it + ?
	      i=exmin?0:imax-1;
	      if (eymax){ // coin
		j=jmax-2;
		if (fij[i][j]>Z[k]){
		  dj=-1;
		  di=0;
		}
		else {
		  j=jmax-1;
		  i=exmin?1:imax-2;
		  di=exmin?1:-1;
		  dj=0;
		}
	      }
	      else {
		if (eymin){
		  j=1;
		  if (fij[i][j]>Z[k]){
		    dj=1;
		    di=0;
		  }
		  else {
		    j=0;
		    i=exmin?1:imax-2;
		    di=exmin?1:-1;
		    dj=0;
		  }
		}
		else { // not a coin
		  j=int((ey-ymin)/dy+.5);
		  // yes, increase j, no decrease j
		  dj=(fij[i][j+1]>Z[k])?1:-1;
		  j+=dj;
		  di=0;
		}
	      }
	    }
	    else {
	      i=int((ex-xmin)/dx+.5);
	      j=(eymin)?0:jmax-1;
	      di=(fij[i+1][j]>Z[k])?1:-1;
	      i+=di;
	      dj=0;
	    } // end if bx==xmin or bx==xmax
	    for (ij=0; ij<ijmax;j+=dj,i+=di,++ij){
	      if (fij[i][j]<Z[k]){
		break;
	      }
	      if (di){ 
		if (i==0 || i==imax-1){
		  coins.push_back(gen(xmin+i*dx,ymin+j*dy));
		  dj=j?-1:1;
		  di=0;
		}
	      }
	      else {
		if (j==0 || j==jmax-1){
		  coins.push_back(gen(xmin+i*dx,ymin+j*dy));
		  di=i?-1:1;
		  dj=0;
		}
	      }
	    } // end for
	    if (ij==ijmax)
	      continue; // everywhere > 0
	    // find component with begin or end near i,j
	    double e1x=xmin+i*dx,e1y=ymin+j*dy;
	    int m=n;
	    for (;m<ncomp;m++){
	      gen composante2=res[k][m];
	      // look if begin and end of composante are on border
	      if (composante2.type!=_VECT || composante2._VECTptr->size()<2)
		continue;
	      gen begin2=composante2._VECTptr->front(),end2=composante2._VECTptr->back();
	      double b2x,b2y,e2x,e2y;
	      if (begin2.type==_DOUBLE_){
		b2x=begin2._DOUBLE_val; b2y=0;
	      }
	      else {
		if (begin2.type!=_CPLX)
		  continue;
		b2x=begin2._CPLXptr->_DOUBLE_val;b2y=(begin2._CPLXptr+1)->_DOUBLE_val;
	      }
	      if (end2.type==_DOUBLE_){
		e2x=end2._DOUBLE_val; e2y=0;
	      }
	      else {
		if (end2.type!=_CPLX)
		  continue;
		e2x=end2._CPLXptr->_DOUBLE_val;
		e2y=(end2._CPLXptr+1)->_DOUBLE_val;
	      }
	      if (fabs(e1x-e2x)<=1.1*dx && fabs(e1y-e2y)<=1.1*dy){
		reverse(composante2._VECTptr->begin(),composante2._VECTptr->end());
		giac::swapdouble(b2x,e2x); 
		giac::swapdouble(b2y,e2y);
	      }
	      if (fabs(e1x-b2x)<=1.1*dx && fabs(e1y-b2y)<=1.1*dy){
		// found! glue res[k][n] with coins and res[k][m]
		vecteur tmp=mergevecteur(*composante._VECTptr,coins);
		if (n==m){
		  tmp.push_back(begin2);
		  res[k][n]=tmp;
		}
		else {
		  res[k][n]=mergevecteur(tmp,*composante2._VECTptr);
		  res[k].erase(res[k].begin()+m);
		  --ncomp;
		  --n;
		}
		break;
	      }
	    }
	  }
	} // end for n<=ncomp
      } // end if (contour || )
      polygonify(res[k],attr,contextptr);
      res0=mergevecteur(res0,res[k]);
    }
    return res0; // gen(res0,_SEQ__VECT);
  }
  gen plotcontour(const gen & f0,bool contour,GIAC_CONTEXT){
    vecteur v(gen2vecteur(f0));
    gen xvar=vx_var,yvar=y__IDNT_e;
    v=quote_eval(v,makevecteur(xvar,yvar),contextptr);
    gen attribut=default_color(contextptr);
    vecteur attributs(1,attribut);
    int s=read_attributs(v,attributs,contextptr);
    if (!s)
      return gensizeerr(contextptr);
    gen f=v[0];
    double xmin=gnuplot_xmin,xmax=gnuplot_xmax; 
    double ymin=gnuplot_ymin,ymax=gnuplot_ymax;
    double zmin=gnuplot_zmin,zmax=gnuplot_zmax;
    if (s>1){
      gen tmp(v[1]);
      if (tmp.type==_VECT && tmp._VECTptr->size()==2){
	readrange(tmp._VECTptr->front(),xmin,xmax,xvar,xmin,xmax,contextptr);
	readrange(tmp._VECTptr->back(),ymin,ymax,yvar,ymin,ymax,contextptr);
      }
    }
    vecteur lz;
    if (s>2){
      gen tmp(v[2]);
      if (tmp.type==_VECT && !tmp._VECTptr->empty())
	lz=*tmp._VECTptr;
    }
    else {
      if (contour){
	lz=vecteur(11);
	for (int i=0;i<11;++i)
	  lz[i]=i;
      }
      else
	lz=vecteur(1);
    }
    int imax=int(std::sqrt(double(gnuplot_pixels_per_eval)));
    int jmax=imax,kmax=0;
    vecteur vtmp;
    read_option(v,xmin,xmax,ymin,ymax,zmin,zmax,vtmp,imax,jmax,kmax,contextptr);
    double dx=(xmax-xmin)/imax,dy=(ymax-ymin)/jmax;
    ++imax; ++jmax;
    vector< vector<double> > fij;
    vecteur xy(makevecteur(xvar,yvar)),xyval(xy);
    // eval f from xmin to xmax, in jstep and ymin to ymax in kstep
    for (int i=0;i<imax;++i){
      vector<double> fi;
      xyval[0]=xmin+i*dx;
      for (int j=0;j<jmax;++j){
	xyval[1]=ymin+j*dy;
	gen f1=evalf_double(evalf(quotesubst(f,xy,xyval,contextptr),eval_level(contextptr),contextptr),eval_level(contextptr),contextptr);
	double zero=0.0;
	fi.push_back(f1.type==_DOUBLE_?f1._DOUBLE_val:0.0/zero);
      }
      fij.push_back(fi);
    }
    return plot_array(fij,imax,jmax,xmin,xmax,dx,ymin,ymax,dy,lz,attributs,contour,contextptr);
  }
#endif
  gen _plotcontour(const gen & f0,GIAC_CONTEXT){
    if ( f0.type==_STRNG && f0.subtype==-1) return  f0;
    return plotcontour(f0,true,contextptr);
  }
  static const char _plotcontour_s []="plotcontour";
  static define_unary_function_eval_quoted (__plotcontour,&giac::_plotcontour,_plotcontour_s);
  define_unary_function_ptr5( at_plotcontour ,alias_at_plotcontour,&__plotcontour,_QUOTE_ARGUMENTS,true);

  static const char _contourplot_s []="contourplot";
  static define_unary_function_eval_quoted (__contourplot,&giac::_plotcontour,_contourplot_s);
  define_unary_function_ptr5( at_contourplot ,alias_at_contourplot,&__contourplot,_QUOTE_ARGUMENTS,true);

  static gen inequation2equation(const gen & g){
    if (g.type==_VECT){
      vecteur res;
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it)
	res.push_back(inequation2equation(*it));
      return gen(res,g.subtype);
    }
    if (g.type==_SYMB && g._SYMBptr->feuille.type==_VECT && g._SYMBptr->feuille._VECTptr->size()==2){
      if (g._SYMBptr->sommet==at_inferieur_strict || g._SYMBptr->sommet==at_inferieur_egal)
	return g._SYMBptr->feuille._VECTptr->back()-g._SYMBptr->feuille._VECTptr->front();
      if (g._SYMBptr->sommet==at_superieur_strict || g._SYMBptr->sommet==at_superieur_egal || g._SYMBptr->sommet==at_equal )
	return g._SYMBptr->feuille._VECTptr->front()-g._SYMBptr->feuille._VECTptr->back();
    }
    return g;
  }

  // f0[0] is either a symbolic (draws f0[0]>=0) or a list (and)
  // if you want to draw or inequations, distribute and wrt to or
  // and make several plotinequation
  gen _plotinequation(const gen & f0,GIAC_CONTEXT){
    if ( f0.type==_STRNG && f0.subtype==-1) return  f0;
    vecteur v(gen2vecteur(f0));
    if (v.empty())
      return gensizeerr(contextptr);
    gen f=inequation2equation(v[0]);
    if (f.type==_VECT){
      f.subtype=_SEQ__VECT;
      f=symbolic(at_min,f);
    }
    v[0]=f;
    return plotcontour(v,false,contextptr);
  }
  static const char _plotinequation_s []="plotinequation";
  static define_unary_function_eval_quoted (__plotinequation,&giac::_plotinequation,_plotinequation_s);
  define_unary_function_ptr5( at_plotinequation ,alias_at_plotinequation,&__plotinequation,_QUOTE_ARGUMENTS,true);

  static const char _inequationplot_s []="inequationplot";
  static define_unary_function_eval_quoted (__inequationplot,&giac::_plotinequation,_inequationplot_s);
  define_unary_function_ptr5( at_inequationplot ,alias_at_inequationplot,&__inequationplot,_QUOTE_ARGUMENTS,true);

  gen _inter_droite(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gentypeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(*args._VECTptr,attributs,contextptr);
    if (s<2 || s>3)
      return gendimerr(contextptr);
    gen res=_inter(gen(makevecteur(args._VECTptr->front(),(*args._VECTptr)[1]),_SEQ__VECT),contextptr);
    if (res.type==_VECT && !res._VECTptr->empty()){
      if (s==3){ // either a point (find nearest point in res) or a list (find 1st point not in list
	vecteur v = *res._VECTptr;
	gen other=(*args._VECTptr)[2];
	if (other.type==_VECT){
	  vecteur & w = *other._VECTptr;
	  unsigned i,j,vs=v.size(),ws=w.size();
	  for (i=0;i<vs;++i){
	    for (j=0;j<ws;++j){
	      if (is_zero(evalf(recursive_normal(distance2(other[j],v[i],contextptr),contextptr),1,contextptr)))
		break;
	    }
	    if (j==ws){
	      res=v[i];
	      break;
	    }
	  }
	}
	else {
	  unsigned i,vs=v.size(); res=v[0];
	  gen mind=distance2(other,res,contextptr),curd;
	  for (i=1;i<vs;++i){
	    curd=distance2(other,v[i],contextptr);
	    if (is_strictly_greater(mind,curd,contextptr)){
	      res=v[i];
	      mind=curd;
	    }
	  }
	}
      }
      else
	res=res._VECTptr->front();
      if (res.is_symb_of_sommet(at_pnt) && res._SYMBptr->feuille.type==_VECT){
	vecteur v = *res._SYMBptr->feuille._VECTptr;
	if (v.size()>=2)
	  v[1]=attributs[0];
	if (v.size()>=3 && attributs.size()>=2)
	  v[2]=attributs[1];
	res=symbolic(at_pnt,gen(v,res._SYMBptr->feuille.subtype));
      }
      return res;
    }
    return undef;
  }    
  static const char _inter_droite_s []="line_inter";
  static define_unary_function_eval (__inter_droite,&_inter_droite,_inter_droite_s);
  define_unary_function_ptr5( at_inter_droite ,alias_at_inter_droite,&__inter_droite,0,true);

  static const char _inter_unique_s []="single_inter";
  static define_unary_function_eval (__inter_unique,&_inter_droite,_inter_unique_s);
  define_unary_function_ptr5( at_inter_unique ,alias_at_inter_unique,&__inter_unique,0,true);

  gen _bitmap(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return symb_pnt(symbolic(at_bitmap,args),0,contextptr);
  }
  static const char _bitmap_s []="bitmap";
  static define_unary_function_eval (__bitmap,&giac::_bitmap,_bitmap_s);
  define_unary_function_ptr5( at_bitmap ,alias_at_bitmap,&__bitmap,0,true);

  /*
    gen papier_pointe_quadrillage(const gen & args,bool quadrillage,GIAC_CONTEXT){
    double xmin=gnuplot_xmin,xmax=gnuplot_xmax,ymin=gnuplot_ymin,ymax=gnuplot_ymax;
    double deltax=(xmax-xmin)/20,deltay=(ymax-ymin)/20,angle=evalf_double(cst_pi/2,1,contextptr)._DOUBLE_val;
    vecteur attributs(1,default_color(contextptr));
    if (args.type==_VECT){
    vecteur & w=*args._VECTptr;
    int s=w.size();
    if (s>0){
    gen tmp=evalf_double(w[0],1,contextptr);
    if (tmp.type==_DOUBLE_)
    deltax=fabs(tmp._DOUBLE_val);
    }
    if (s>1){
    gen tmp=evalf_double(w[1],1,contextptr);
    if (tmp.type==_DOUBLE_)
    deltay=fabs(tmp._DOUBLE_val);
    }
    if (s>2){
    gen tmp=evalf_double(w[2],1,contextptr);
    if (tmp.type==_DOUBLE_){
    angle=tmp._DOUBLE_val;
    if (fabs(angle)<epsilon(contextptr))
    return gensizeerr(contextptr);
    }
    }
    int nstep=int((xmax-xmin)/deltax),kstep=int((ymax-ymin)/deltay);
    gen x,y;
    for (int i=0;i<s;++i){
    if (w[i].is_symb_of_sommet(at_equal)){
    if (w[i][1]==x__IDNT_e)
    readrange(w[i],gnuplot_xmin,gnuplot_xmax,x,xmin,xmax,contextptr);
    if (w[i][1]==y__IDNT_e)
    readrange(w[i],gnuplot_xmin,gnuplot_xmax,y,ymin,ymax,contextptr);
    }
    }
    read_option(w,xmin,xmax,ymin,ymax,attributs,nstep,kstep,contextptr);
    if (!nstep)
    nstep=20;
    deltax=(xmax-xmin)/nstep;
    if (!kstep)
    kstep=20;
    deltay=(ymax-ymin)/kstep;
    }
    deltax=(xmax-xmin)/std::floor(fabs((xmax-xmin)/deltax));
    deltay=(ymax-ymin)/std::floor(fabs((ymax-ymin)/deltay));
    if (!quadrillage){
    int color=attributs[0].val;
    color = (color & 0xffff )| (7<<25) | (1 << 19);
    attributs[0]=color;
    }
    vecteur res;
    double pente=std::max(fabs(std::tan(angle)),0.05);
    if (quadrillage){
    res.push_back(pnt_attrib(gen(makevecteur(xmin+cst_i*ymin,xmin+cst_i*ymax),_GROUP__VECT),attributs,contextptr));
    res.push_back(pnt_attrib(gen(makevecteur(xmax+cst_i*ymin,xmax+cst_i*ymax),_GROUP__VECT),attributs,contextptr));
    for (double y=ymin;y<=ymax+1e-12;y+=deltay){
    res.push_back(pnt_attrib(gen(makevecteur(xmin+cst_i*y,xmax+cst_i*y),_GROUP__VECT),attributs,contextptr));
    }
    for (double x=xmin;x<=xmax+1e-12;x+=deltax){
    // line (x,ymax) -> (xmin,y)
    double y=ymax+pente*(xmin-x);
    if (y>=ymin-1e-12)
    res.push_back(pnt_attrib(gen(makevecteur(xmin+cst_i*y,x+cst_i*ymax),_GROUP__VECT),attributs,contextptr));
    else
    res.push_back(pnt_attrib(gen(makevecteur(x-(ymax-ymin)/pente+cst_i*ymin,x+cst_i*ymax),_GROUP__VECT),attributs,contextptr));	  
    }
    for (double y=ymax;y>ymin;y-=pente*deltax){
    double x=xmax+(ymin-y)/pente;
    if (x>=xmin)
    res.push_back(pnt_attrib(gen(makevecteur(x+cst_i*ymin,xmax+cst_i*y),_GROUP__VECT),attributs,contextptr));
    else { // xmin,y1 -> xmax,y
    double y1=y+(xmin-xmax)*pente;
    res.push_back(pnt_attrib(gen(makevecteur(xmin+cst_i*y1,xmax+cst_i*y),_GROUP__VECT),attributs,contextptr));
    }
    }
    }
    else {
    for (double x=xmin;;x+=deltax){
    double X=x;
    double y=ymax;
    if (X>=xmax+1e-12){
    // find y for xmax 
    y=ymax-(X-xmax)*pente;
    if (y<ymin-1e-12)
    break;
    int ny=int(std::ceil((ymax-y)/deltay));
    y=ymax-ny*deltay;
    X=x-ny*deltay/pente;
    }
    // points of line (x,ymax) -> (xmin,y) or (x',ymin) with deltay
    for (;y>=ymin-1e-12 && X>=xmin-1e-12;y-=deltay,X-=deltay/pente){
    res.push_back(pnt_attrib(X+cst_i*y,attributs,contextptr));
    }
    }
    }
    return res; // gen(res,_SEQ__VECT);
    }
    gen _papier_pointe(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return papier_pointe_quadrillage(args,false,contextptr);
    }
    static const char _papier_pointe_s []="dot_paper";
    static define_unary_function_eval (__papier_pointe,&giac::_papier_pointe,_papier_pointe_s);
    define_unary_function_ptr5( at_papier_pointe ,alias_at_papier_pointe,&__papier_pointe,0,true);

    gen _papier_quadrille(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return papier_pointe_quadrillage(args,true,contextptr);
    }
    static const char _papier_quadrille_s []="grid_paper";
    static define_unary_function_eval (__papier_quadrille,&giac::_papier_quadrille,_papier_quadrille_s);
    define_unary_function_ptr5( at_papier_quadrille ,alias_at_papier_quadrille,&__papier_quadrille,0,true);
  */
  // code slice written by R. De Graeve (2010)
  void papier_lignes(vecteur & res,double xmin,double xmax,double ymin,double ymax,double angle,double deltax,double deltay,double pente,const vecteur & attributs,GIAC_CONTEXT){
    res.push_back(pnt_attrib(gen(makevecteur(xmin+ymin*cst_i,xmin+ymax*cst_i),_GROUP__VECT),attributs,contextptr)); 
    res.push_back(pnt_attrib(gen(makevecteur(xmax+ymax*cst_i,xmin+ymax*cst_i),_GROUP__VECT),attributs,contextptr));
    res.push_back(pnt_attrib(gen(makevecteur(xmax+ymin*cst_i,xmax+ymax*cst_i),_GROUP__VECT),attributs,contextptr));
    res.push_back(pnt_attrib(gen(makevecteur(xmax+ymin*cst_i,xmin+ymin*cst_i),_GROUP__VECT),attributs,contextptr));
    //const double cst_pi;
    double pi=evalf_double(cst_pi,1,contextptr)._DOUBLE_val;
    if (angle==pi/2){
      for (double x=xmin;x<=xmax;x+=deltax){
	res.push_back(pnt_attrib(gen(makevecteur(x+ymin*cst_i,x+ymax*cst_i),_GROUP__VECT),attributs,contextptr));
      }
      //return res;
    }
    double Y=(ymax-ymin)/pente;
    if (angle<pi/2){
      double q=std::floor(Y/deltax+1e-12);
      for (double x=xmin-q*deltax;x<=xmin;x+=deltax){
	double Y1=pente*(xmax-x)+ymin;
	double Y2=pente*(xmin-x)+ymin;
	double X1=(ymax-ymin)/pente+x;
	if (Y1<ymax){
	  res.push_back(pnt_attrib(gen(makevecteur(xmin+Y2*cst_i,xmax+Y1*cst_i),_GROUP__VECT),attributs,contextptr));
	}
	if (X1<xmax){
	  res.push_back(pnt_attrib(gen(makevecteur(xmin+Y2*cst_i,X1+ymax*cst_i),_GROUP__VECT),attributs,contextptr));
	}
      }
      for (double x=xmin;x<=xmax;x+=deltax){
	double Y1=pente*(xmax-x)+ymin;
	// double Y2=pente*(xmin-x)+ymin;
	double X1=(ymax-ymin)/pente+x;
	if (Y1<ymax){
	  res.push_back(pnt_attrib(gen(makevecteur(x+ymin*cst_i,xmax+Y1*cst_i),_GROUP__VECT),attributs,contextptr));
	}
	if (X1<xmax){
	  res.push_back(pnt_attrib(gen(makevecteur(x+ymin*cst_i,X1+ymax*cst_i),_GROUP__VECT),attributs,contextptr));
	}
      }
      //return res;
    }
    if (angle>pi/2){
      double x=xmin;
      while (x<=xmax){
	double Y1=pente*(xmin-x)+ymin;
	double X1=(ymax-ymin)/pente+x;
	if (Y1<ymax){
	  res.push_back(pnt_attrib(gen(makevecteur(x+ymin*cst_i,xmin+Y1*cst_i),_GROUP__VECT),attributs,contextptr));
	}
	if (X1>xmin){
	  res.push_back(pnt_attrib(gen(makevecteur(x+ymin*cst_i,X1+ymax*cst_i),_GROUP__VECT),attributs,contextptr));
	}
	x=x+deltax;
      }
      double q=std::ceil(Y/deltax-1e-12);
      while (x<=xmax-q*deltax){
	double Y1=pente*(xmin-x)+ymin;
	double Y2=pente*(xmax-x)+ymin;
	double X1=(ymax-ymin)/pente+x;
	if (Y1<ymax){
	  res.push_back(pnt_attrib(gen(makevecteur(xmax+Y2*cst_i,xmin+Y1*cst_i),_GROUP__VECT),attributs,contextptr));
	}
	if (X1>xmin){
	  res.push_back(pnt_attrib(gen(makevecteur(xmax+Y2*cst_i,X1+ymax*cst_i),_GROUP__VECT),attributs,contextptr));
	}
	x=x+deltax;
      }
      //return res;
    }
  }
  
  static gen Papier_pointe_quadrillage(const gen & args,int quadrillage,GIAC_CONTEXT){
    double xmin=gnuplot_xmin,xmax=gnuplot_xmax,ymin=gnuplot_ymin,ymax=gnuplot_ymax;
    double deltax=(xmax-xmin)/20,deltay=(ymax-ymin)/20,angle=evalf_double(cst_pi/2,1,contextptr)._DOUBLE_val;
    vecteur attributs(1,default_color(contextptr));
    if (args.type==_VECT){
      vecteur & w=*args._VECTptr;
      int s=w.size();
      if (s>0){
	gen tmp=evalf_double(w[0],1,contextptr);
	if (tmp.type==_DOUBLE_)
	  deltax=fabs(tmp._DOUBLE_val);
      }
      if (s>1){
	gen tmp=evalf_double(w[1],1,contextptr);
	if (tmp.type==_DOUBLE_){
	  angle=tmp._DOUBLE_val;
	  double pi=M_PI;
	  angle=angle-std::floor(angle/pi)*pi;
	  if (fabs(angle)<epsilon(contextptr) || fabs(pi-angle)<epsilon(contextptr))
	    return gensizeerr(contextptr);
	}
      }
      if (s>2){
	gen tmp=evalf_double(w[2],1,contextptr);
	if (tmp.type==_DOUBLE_)
	  deltay=fabs(tmp._DOUBLE_val);
      }
      //int nstep=int((xmax-xmin)/deltax),kstep=int((ymax-ymin)/deltay);
      gen x,y;
      for (int i=0;i<s;++i){
	if (w[i].is_symb_of_sommet(at_equal)){
	  if (w[i][1]==x__IDNT_e)
	    readrange(w[i],gnuplot_xmin,gnuplot_xmax,x,xmin,xmax,contextptr);
	  if (w[i][1]==y__IDNT_e)
	    readrange(w[i],gnuplot_xmin,gnuplot_xmax,y,ymin,ymax,contextptr);
	}
      }
      int n1,n2;
      read_option(w,xmin,xmax,ymin,ymax,attributs,n1,n2,contextptr);
      // if (!nstep)nstep=20;deltax=(xmax-xmin)/nstep;
      //if (!kstep) kstep=20;deltay=(ymax-ymin)/kstep;
    }

    // deltax=(xmax-xmin)/std::floor(fabs((xmax-xmin)/deltax));
    // deltay=(ymax-ymin)/std::floor(fabs((ymax-ymin)/deltay));
    if (quadrillage==2){
      int color=attributs[0].val;
      color = (color & 0xffff )| (7<<25) | (1 << 19);
      attributs[0]=color;
    }
    
    vecteur res;

    double pente=std::tan(angle);
    
    if (quadrillage==0 || quadrillage==1){
      for (double y=ymin;y<=ymax;y+=deltay){
	res.push_back(pnt_attrib(gen(makevecteur(xmin+y*cst_i,xmax+y*cst_i),_GROUP__VECT),attributs,contextptr));
      }
      //papier_lignes(res,xmin,xmax,ymin,ymax,0,deltax,deltay,pente,attributs,contextptr);
      papier_lignes(res,xmin,xmax,ymin,ymax,angle,deltax,deltay,pente,attributs,contextptr);
    }
    if (quadrillage==1){
      double u1=deltay/pente;
      if (u1-deltax==0) {angle=M_PI/2;}
      if (u1-deltax>0) {angle=std::atan(deltay/(u1-deltax)); }
      if (u1-deltax<0) {angle=std::atan(deltay/(u1-deltax))+M_PI;}
      papier_lignes(res,xmin,xmax,ymin,ymax,angle,deltax,deltay,std::tan(angle),attributs,contextptr);
      
    } // end if quadrillage== 1    
    if (quadrillage==2) {
      res.push_back(pnt_attrib(gen(makevecteur(xmin+ymin*cst_i,xmin+ymax*cst_i),_GROUP__VECT),attributs,contextptr)); 
      res.push_back(pnt_attrib(gen(makevecteur(xmax+ymax*cst_i,xmin+ymax*cst_i),_GROUP__VECT),attributs,contextptr));
      res.push_back(pnt_attrib(gen(makevecteur(xmax+ymin*cst_i,xmax+ymax*cst_i),_GROUP__VECT),attributs,contextptr));
      res.push_back(pnt_attrib(gen(makevecteur(xmax+ymin*cst_i,xmin+ymin*cst_i),_GROUP__VECT),attributs,contextptr));
      for (double y=ymin;y<=ymax;y+=deltay){
	double X=(y-ymin)/pente;
	int q=int(std::floor(X/deltax+1e-12));
	for (double x=xmin-q*deltax+X;x<xmax;x+=deltax){
	  res.push_back(pnt_attrib(x+y*cst_i,attributs,contextptr)); 
	}
      }
    }
    if (quadrillage==3) {
      papier_lignes(res,xmin,xmax,ymin,ymax,angle,deltax,deltay,pente,attributs,contextptr);
    }
    return res; // gen(res,_SEQ__VECT);
  }
  gen _dot_paper(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return Papier_pointe_quadrillage(args,2,contextptr);
  }
  //static const char _dot_paper_s []="papierp";
  static const char _dot_paper_s[]="dot_paper";
  static define_unary_function_eval (__dot_paper,&giac::_dot_paper,_dot_paper_s);
  define_unary_function_ptr5( at_dot_paper ,alias_at_dot_paper,&__dot_paper,0,true);
	
  gen _grid_paper(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return Papier_pointe_quadrillage(args,0,contextptr);
  }
  // static const char _grid_paper_s []="papierq";
  static const char _grid_paper_s[]= "grid_paper";
  static define_unary_function_eval (__grid_paper,&giac::_grid_paper,_grid_paper_s);
  define_unary_function_ptr5( at_grid_paper ,alias_at_grid_paper,&__grid_paper,0,true);
	
  gen _triangle_paper(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return Papier_pointe_quadrillage(args,1,contextptr);
  }
  // static const char _triangle_paper_s []="papiert";
  const char _triangle_paper_s[] ="triangle_paper";
  static define_unary_function_eval (__triangle_paper,&giac::_triangle_paper,_triangle_paper_s);
  define_unary_function_ptr5( at_triangle_paper ,alias_at_triangle_paper,&__triangle_paper,0,true);
	
  gen _line_paper(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return Papier_pointe_quadrillage(args,3,contextptr);
  }
  // static const char _line_paper_s []="papierl";
  static const char _line_paper_s[]="line_paper";
  static define_unary_function_eval (__line_paper,&giac::_line_paper,_line_paper_s);
  define_unary_function_ptr5( at_line_paper ,alias_at_line_paper,&__line_paper,0,true);
  // end of code slice written by R. De Graeve

  // inert function used to keep the attribute of a graphical object
  gen _plot_style(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return symbolic(at_plot_style,args);
  }
  static const char _plot_style_s []="plot_style";
  static define_unary_function_eval_index (114,__plot_style,&giac::_plot_style,_plot_style_s);
  define_unary_function_ptr5( at_plot_style ,alias_at_plot_style,&__plot_style,0,true);

  // Returns the current dimensions of the picture 
  // and adjust plot_instructionsw/h
  gen _Pictsize(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen res=__interactive.op(symbolic(at_Pictsize,args),contextptr);
    /*
      if (res.type==_VECT && res._VECTptr->size()==2){
      plot_instructionsw=res._VECTptr->front().val;
      plot_instructionsh=res._VECTptr->back().val;
      }
    */
    return res;
  }
  static const char _Pictsize_s []="Pictsize";
  static define_unary_function_eval (__Pictsize,&giac::_Pictsize,_Pictsize_s);
  define_unary_function_ptr5( at_Pictsize ,alias_at_Pictsize,&__Pictsize,0,true);


  // FIXME 394 should be T_LOGO, 340 T_RETURN
  //#define T_LOGO 394
  //#define T_RETURN 340
  gen _DrawInv(const gen & g,const context * contextptr){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // return _symetrie(makevecteur(_droite(makevecteur(0,1+cst_i)),_plotfunc(g)));
    gen y(g),x(vx_var);
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()==2 ){
      vecteur & v=*g._VECTptr;
      y=v[0];
      x=v[1];
    }
    return _plotparam(gen(makevecteur(y+cst_i*x,x,gnuplot_xmin,gnuplot_xmax),_SEQ__VECT),contextptr);
  }
  static const char _DrawInv_s []="DrawInv";
  static define_unary_function_eval2 (__DrawInv,&_DrawInv,_DrawInv_s,&printastifunction);
  define_unary_function_ptr5( at_DrawInv ,alias_at_DrawInv,&__DrawInv,0,T_RETURN); 

  gen _Graph(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return _plotfunc(g,contextptr);
    // FIXME: add a mode
    // vecteur v(gen2vecteur(g));
    
  }
  static const char _Graph_s []="Graph";
  static define_unary_function_eval2 (__Graph,&_Graph,_Graph_s,&printastifunction);
  define_unary_function_ptr5( at_Graph ,alias_at_Graph,&__Graph,0,T_RETURN);

  static const char _DrawFunc_s []="DrawFunc";
  static define_unary_function_eval2 (__DrawFunc,&_plotfunc,_DrawFunc_s,&printastifunction);
  define_unary_function_ptr5( at_DrawFunc ,alias_at_DrawFunc,&__DrawFunc,0,T_RETURN);

  static const char _DrawPol_s []="DrawPol";
  static define_unary_function_eval2 (__DrawPol,&_plotpolar,_DrawPol_s,&printastifunction);
  define_unary_function_ptr5( at_DrawPol ,alias_at_DrawPol,&__DrawPol,0,T_RETURN);

  static const char _DrawParm_s []="DrawParm";
  static define_unary_function_eval2 (__DrawParm,&_plotparam,_DrawParm_s,&printastifunction);
  define_unary_function_ptr5( at_DrawParm ,alias_at_DrawParm,&__DrawParm,0,T_RETURN);

  gen _DrwCtour(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // FIXME
    return undef;
  }
  static const char _DrwCtour_s []="DrwCtour";
  static define_unary_function_eval2 (__DrwCtour,&_plotcontour,_DrwCtour_s,&printastifunction);
  define_unary_function_ptr5( at_DrwCtour ,alias_at_DrwCtour,&__DrwCtour,0,T_RETURN);

  // should be print_string?
  std::string gen2string(const gen & g){
    if (g.type==_STRNG)
      return *g._STRNGptr;
    else
      return g.print(context0);
  }

#ifdef RTOS_THREADX
  logo_turtle vecteur2turtle(const vecteur & v){
    return logo_turtle();
  }

  static int turtle_status(const logo_turtle & turtle){
    return 0;
  }

  bool set_turtle_state(const vecteur & v,GIAC_CONTEXT){
    return false;
  }

  gen turtle2gen(const logo_turtle & turtle){
    return undef;
  }

  vecteur turtlevect2vecteur(const std::vector<logo_turtle> & v){
    return 0;
  }

  std::vector<logo_turtle> vecteur2turtlevect(const vecteur & v){
    return 0;
  }

  gen turtle_state(GIAC_CONTEXT){
    return undef;
  }

  static gen update_turtle_state(bool clrstring,GIAC_CONTEXT){
    return undef;
  }

  gen _avance(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _avance_s []="avance";
  static define_unary_function_eval2 (__avance,&_avance,_avance_s,&printastifunction);
  define_unary_function_ptr5( at_avance ,alias_at_avance,&__avance,0,T_LOGO);

  gen _recule(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _recule_s []="recule";
  static define_unary_function_eval2 (__recule,&_recule,_recule_s,&printastifunction);
  define_unary_function_ptr5( at_recule ,alias_at_recule,&__recule,0,T_LOGO);

  gen _position(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _position_s []="position";
  static define_unary_function_eval2 (__position,&_position,_position_s,&printastifunction);
  define_unary_function_ptr5( at_position ,alias_at_position,&__position,0,T_LOGO);

  gen _cap(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _cap_s []="cap";
  static define_unary_function_eval2 (__cap,&_cap,_cap_s,&printastifunction);
  define_unary_function_ptr5( at_cap ,alias_at_cap,&__cap,0,T_LOGO);

  gen _tourne_droite(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _tourne_droite_s []="tourne_droite";
  static define_unary_function_eval2 (__tourne_droite,&_tourne_droite,_tourne_droite_s,&printastifunction);
  define_unary_function_ptr5( at_tourne_droite ,alias_at_tourne_droite,&__tourne_droite,0,T_LOGO);

  gen _tourne_gauche(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _tourne_gauche_s []="tourne_gauche";
  static define_unary_function_eval2 (__tourne_gauche,&_tourne_gauche,_tourne_gauche_s,&printastifunction);
  define_unary_function_ptr5( at_tourne_gauche ,alias_at_tourne_gauche,&__tourne_gauche,0,T_LOGO);

  gen _leve_crayon(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _leve_crayon_s []="leve_crayon";
  static define_unary_function_eval2 (__leve_crayon,&_leve_crayon,_leve_crayon_s,&printastifunction);
  define_unary_function_ptr5( at_leve_crayon ,alias_at_leve_crayon,&__leve_crayon,0,T_LOGO);

  gen _baisse_crayon(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _baisse_crayon_s []="baisse_crayon";
  static define_unary_function_eval2 (__baisse_crayon,&_baisse_crayon,_baisse_crayon_s,&printastifunction);
  define_unary_function_ptr5( at_baisse_crayon ,alias_at_baisse_crayon,&__baisse_crayon,0,T_LOGO);

  gen _ecris(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _ecris_s []="ecris";
  static define_unary_function_eval2 (__ecris,&_ecris,_ecris_s,&printastifunction);
  define_unary_function_ptr5( at_ecris ,alias_at_ecris,&__ecris,0,T_LOGO);
  gen _signe(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _signe_s []="signe";
  static define_unary_function_eval2 (__signe,&_signe,_signe_s,&printastifunction);
  define_unary_function_ptr5( at_signe ,alias_at_signe,&__signe,0,T_LOGO);

  gen _saute(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _saute_s []="saute";
  static define_unary_function_eval2 (__saute,&_saute,_saute_s,&printastifunction);
  define_unary_function_ptr5( at_saute ,alias_at_saute,&__saute,0,T_LOGO);

  gen _pas_de_cote(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _pas_de_cote_s []="pas_de_cote";
  static define_unary_function_eval2 (__pas_de_cote,&_pas_de_cote,_pas_de_cote_s,&printastifunction);
  define_unary_function_ptr5( at_pas_de_cote ,alias_at_pas_de_cote,&__pas_de_cote,0,T_LOGO);
  gen _cache_tortue(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _cache_tortue_s []="cache_tortue";
  static define_unary_function_eval2 (__cache_tortue,&_cache_tortue,_cache_tortue_s,&printastifunction);
  define_unary_function_ptr5( at_cache_tortue ,alias_at_cache_tortue,&__cache_tortue,0,T_LOGO);

  gen _montre_tortue(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _montre_tortue_s []="montre_tortue";
  static define_unary_function_eval2 (__montre_tortue,&_montre_tortue,_montre_tortue_s,&printastifunction);
  define_unary_function_ptr5( at_montre_tortue ,alias_at_montre_tortue,&__montre_tortue,0,T_LOGO);

  gen _debut_enregistrement(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _debut_enregistrement_s []="debut_enregistrement";
  static define_unary_function_eval2 (__debut_enregistrement,&_debut_enregistrement,_debut_enregistrement_s,&printastifunction);
  define_unary_function_ptr5( at_debut_enregistrement ,alias_at_debut_enregistrement,&__debut_enregistrement,0,T_LOGO);

  gen _fin_enregistrement(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _fin_enregistrement_s []="fin_enregistrement";
  static define_unary_function_eval2 (__fin_enregistrement,&_fin_enregistrement,_fin_enregistrement_s,&printastifunction);
  define_unary_function_ptr5( at_fin_enregistrement ,alias_at_fin_enregistrement,&__fin_enregistrement,0,T_LOGO);

  gen _repete(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _repete_s []="repete";
  static define_unary_function_eval2 (__repete,&_repete,_repete_s,&printastifunction);
  define_unary_function_ptr5( at_repete ,alias_at_repete,&__repete,0,T_LOGO);

  gen _crayon(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _crayon_s []="crayon";
  static define_unary_function_eval2 (__crayon,&_crayon,_crayon_s,&printastifunction);
  define_unary_function_ptr5( at_crayon ,alias_at_crayon,&__crayon,0,T_LOGO);

  gen _efface(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _efface_s []="efface";
  static define_unary_function_eval2 (__efface,&_efface,_efface_s,&printastifunction);
  define_unary_function_ptr5( at_efface ,alias_at_efface,&__efface,0,T_LOGO);

  gen _vers(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _vers_s []="vers";
  static define_unary_function_eval2 (__vers,&_vers,_vers_s,&printastifunction);
  define_unary_function_ptr5( at_vers ,alias_at_vers,&__vers,0,T_LOGO);

  static int find_radius(const gen & g,int & r,int & theta2,bool & direct){
    return 0;
  }

  static void turtle_move(int r,int theta2,GIAC_CONTEXT){
  }

  gen _disque(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _disque_s []="disque";
  static define_unary_function_eval2 (__disque,&_disque,_disque_s,&printastifunction);
  define_unary_function_ptr5( at_disque ,alias_at_disque,&__disque,0,T_LOGO);

  gen _disque_centre(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _disque_centre_s []="disque_centre";
  static define_unary_function_eval2 (__disque_centre,&_disque_centre,_disque_centre_s,&printastifunction);
  define_unary_function_ptr5( at_disque_centre ,alias_at_disque_centre,&__disque_centre,0,T_LOGO);

  gen _rond(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _rond_s []="rond";
  static define_unary_function_eval2 (__rond,&_rond,_rond_s,&printastifunction);
  define_unary_function_ptr5( at_rond ,alias_at_rond,&__rond,0,T_LOGO);

  gen _polygone_rempli(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _polygone_rempli_s []="polygone_rempli";
  static define_unary_function_eval2 (__polygone_rempli,&_polygone_rempli,_polygone_rempli_s,&printastifunction);
  define_unary_function_ptr5( at_polygone_rempli ,alias_at_polygone_rempli,&__polygone_rempli,0,T_LOGO);

  gen _rectangle_plein(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _rectangle_plein_s []="rectangle_plein";
  static define_unary_function_eval2 (__rectangle_plein,&_rectangle_plein,_rectangle_plein_s,&printastifunction);
  define_unary_function_ptr5( at_rectangle_plein ,alias_at_rectangle_plein,&__rectangle_plein,0,T_LOGO);

  gen _triangle_plein(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _triangle_plein_s []="triangle_plein";
  static define_unary_function_eval2 (__triangle_plein,&_triangle_plein,_triangle_plein_s,&printastifunction);
  define_unary_function_ptr5( at_triangle_plein ,alias_at_triangle_plein,&__triangle_plein,0,T_LOGO);

  gen _dessine_tortue(const gen & g,GIAC_CONTEXT){
    return undef;
  }
  static const char _dessine_tortue_s []="dessine_tortue";
  static define_unary_function_eval2 (__dessine_tortue,&_dessine_tortue,_dessine_tortue_s,&printastifunction);
  define_unary_function_ptr5( at_dessine_tortue ,alias_at_dessine_tortue,&__dessine_tortue,0,T_LOGO);

#else
  logo_turtle vecteur2turtle(const vecteur & v){
    int s=v.size();
    if (s>=5 && v[0].type==_DOUBLE_ && v[1].type==_DOUBLE_ && v[2].type==_DOUBLE_ && v[3].type==_INT_ && v[4].type==_INT_ ){
      logo_turtle t;
      t.x=v[0]._DOUBLE_val;
      t.y=v[1]._DOUBLE_val;
      t.theta=v[2]._DOUBLE_val;
      int i=v[3].val;
      t.mark=(i%2)!=0;
      i=i >> 1;
      t.visible=(i%2)!=0;
      i=i >> 1;
      t.direct = (i%2)!=0;
      i=i >> 1;
      t.turtle_length = i & 0xff;
      i=i >> 8;
      t.color = i;
      t.radius = v[4].val;
      if (s>5 && v[5].type==_STRNG)
	t.s=*v[5]._STRNGptr;
      else
	t.s="";
      return t;
    }
#ifndef NO_STDEXCEPT
    setsizeerr(gettext("vecteur2turtle")); // FIXME
#endif
    return logo_turtle();
  }

  static int turtle_status(const logo_turtle & turtle){
    int status= (turtle.color << 11) | ( (turtle.turtle_length & 0xff) << 3) ;
    if (turtle.direct)
      status += 4;
    if (turtle.visible)
      status += 2;
    if (turtle.mark)
      status += 1;
    return status;
  }

  bool set_turtle_state(const vecteur & v,GIAC_CONTEXT){
    if (v.size()>=2 && v[0].type==_DOUBLE_ && v[1].type==_DOUBLE_){
      vecteur w(v);
      int s=w.size();
      if (s==2)
	w.push_back(turtle(contextptr).theta);
      if (s<4)
	w.push_back(turtle_status(turtle(contextptr)));
      if (s<5)
	w.push_back(0);
      if (w[2].type==_DOUBLE_ && w[3].type==_INT_ && w[4].type==_INT_){
	turtle(contextptr)=vecteur2turtle(w);
	turtle_stack(contextptr).push_back(turtle(contextptr));
	return true;
      }
    }
    return false;
  }

  gen turtle2gen(const logo_turtle & turtle){
    return gen(makevecteur(turtle.x,turtle.y,turtle.theta,turtle_status(turtle),turtle.radius,string2gen(turtle.s,false)),_LOGO__VECT);
  }

  vecteur turtlevect2vecteur(const std::vector<logo_turtle> & v){
    vecteur res;
    vector<logo_turtle>::const_iterator it=v.begin(),itend=v.end();
    res.reserve(itend-it);
    for (;it!=itend;++it)
      res.push_back(turtle2gen(*it));
    return res;
  }

  std::vector<logo_turtle> vecteur2turtlevect(const vecteur & v){
    std::vector<logo_turtle> res;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type==_VECT)
	res.push_back(vecteur2turtle(*it->_VECTptr));
    }
    return res;
  }

  gen turtle_state(GIAC_CONTEXT){
    return turtle2gen(turtle(contextptr));
  }

  static gen update_turtle_state(bool clrstring,GIAC_CONTEXT){
    if (clrstring)
      turtle(contextptr).s="";
    turtle(contextptr).theta = turtle(contextptr).theta - floor(turtle(contextptr).theta/360)*360;
    turtle_stack(contextptr).push_back(turtle(contextptr));
    gen res=turtle_state(contextptr);
    // update parent turtle state
    if (turtle_stack(contextptr).size()==1)
      __interactive.op(symbolic(at_pnt,-1),contextptr); // clear parent stack
    else { // code turtle
      __interactive.op(symbolic(at_pnt,res),contextptr);
    }
    return res;
  }

  gen _avance(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    double i;
    if (g.type!=_INT_){
      if (g.type==_VECT)
	i=turtle(contextptr).turtle_length;
      else {
	gen g1=evalf_double(g,1,contextptr);
	if (g1.type==_DOUBLE_)
	  i=g1._DOUBLE_val;
	else
	  return gensizeerr(contextptr);
      }
    }
    else
      i=g.val;
    turtle(contextptr).x += i * std::cos(turtle(contextptr).theta*deg2rad_d);
    turtle(contextptr).y += i * std::sin(turtle(contextptr).theta*deg2rad_d) ;
    turtle(contextptr).radius = 0;
    return update_turtle_state(true,contextptr);
  }
  static const char _avance_s []="avance";
  static define_unary_function_eval2 (__avance,&_avance,_avance_s,&printastifunction);
  define_unary_function_ptr5( at_avance ,alias_at_avance,&__avance,0,T_LOGO);

  gen _recule(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    if (g.type==_VECT)
      return _avance(-turtle(contextptr).turtle_length,contextptr);
    return _avance(-g,contextptr);
  }
  static const char _recule_s []="recule";
  static define_unary_function_eval2 (__recule,&_recule,_recule_s,&printastifunction);
  define_unary_function_ptr5( at_recule ,alias_at_recule,&__recule,0,T_LOGO);

  gen _position(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    if (g.type!=_VECT)
      return makevecteur(turtle(contextptr).x,turtle(contextptr).y);
    // return turtle_state();
    vecteur v = *g._VECTptr;
    int s=v.size();
    if (!s)
      return makevecteur(turtle(contextptr).x,turtle(contextptr).y);
    v[0]=evalf_double(v[0],1,contextptr);
    if (s>1)
      v[1]=evalf_double(v[1],1,contextptr);
    if (s>2)
      v[2]=evalf_double(v[2],1,contextptr); 
    if (set_turtle_state(v,contextptr))
      return update_turtle_state(true,contextptr);
    return zero;
  }
  static const char _position_s []="position";
  static define_unary_function_eval2 (__position,&_position,_position_s,&printastifunction);
  define_unary_function_ptr5( at_position ,alias_at_position,&__position,0,T_LOGO);

  gen _cap(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    gen gg=evalf_double(g,1,contextptr);
    if (gg.type!=_DOUBLE_)
      return turtle(contextptr).theta;
    turtle(contextptr).theta=gg._DOUBLE_val;
    turtle(contextptr).radius = 0;
    return update_turtle_state(true,contextptr);
  }
  static const char _cap_s []="cap";
  static define_unary_function_eval2 (__cap,&_cap,_cap_s,&printastifunction);
  define_unary_function_ptr5( at_cap ,alias_at_cap,&__cap,0,T_LOGO);

  gen _tourne_droite(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    if (g.type!=_INT_){
      if (g.type==_VECT)
	turtle(contextptr).theta -= 90;
      else {
	gen g1=evalf_double(g,1,contextptr);
	if (g1.type==_DOUBLE_)
	  turtle(contextptr).theta -= g1._DOUBLE_val;
	else
	  return gensizeerr(contextptr);
      }
    }
    else
      turtle(contextptr).theta -= g.val;
    turtle(contextptr).radius = 0;
    return update_turtle_state(true,contextptr);
  }
  static const char _tourne_droite_s []="tourne_droite";
  static define_unary_function_eval2 (__tourne_droite,&_tourne_droite,_tourne_droite_s,&printastifunction);
  define_unary_function_ptr5( at_tourne_droite ,alias_at_tourne_droite,&__tourne_droite,0,T_LOGO);

  gen _tourne_gauche(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    if (g.type==_VECT){
      turtle(contextptr).theta += 90;
      turtle(contextptr).radius = 0;
      return update_turtle_state(true,contextptr);
    }
    return _tourne_droite(-g,contextptr);
  }
  static const char _tourne_gauche_s []="tourne_gauche";
  static define_unary_function_eval2 (__tourne_gauche,&_tourne_gauche,_tourne_gauche_s,&printastifunction);
  define_unary_function_ptr5( at_tourne_gauche ,alias_at_tourne_gauche,&__tourne_gauche,0,T_LOGO);

  gen _leve_crayon(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    turtle(contextptr).mark = false;
    turtle(contextptr).radius = 0;
    return update_turtle_state(true,contextptr);
  }
  static const char _leve_crayon_s []="leve_crayon";
  static define_unary_function_eval2 (__leve_crayon,&_leve_crayon,_leve_crayon_s,&printastifunction);
  define_unary_function_ptr5( at_leve_crayon ,alias_at_leve_crayon,&__leve_crayon,0,T_LOGO);

  gen _baisse_crayon(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    turtle(contextptr).mark = true;
    turtle(contextptr).radius = 0;
    return update_turtle_state(true,contextptr);
  }
  static const char _baisse_crayon_s []="baisse_crayon";
  static define_unary_function_eval2 (__baisse_crayon,&_baisse_crayon,_baisse_crayon_s,&printastifunction);
  define_unary_function_ptr5( at_baisse_crayon ,alias_at_baisse_crayon,&__baisse_crayon,0,T_LOGO);

  gen _ecris(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    turtle(contextptr).radius=14;
    if (g.type==_VECT){ 
      vecteur & v =*g._VECTptr;
      int s=v.size();
      if (s==2 && v[1].type==_INT_){
	turtle(contextptr).radius=absint(v[1].val);
	turtle(contextptr).s=gen2string(v.front());
	return update_turtle_state(false,contextptr);
      }
      if (s==4 && v[1].type==_INT_ && v[2].type==_INT_ && v[3].type==_INT_){
	logo_turtle t=turtle(contextptr);
	_leve_crayon(0,contextptr);
	_position(makevecteur(v[2],v[3]),contextptr);
	turtle(contextptr).radius=absint(v[1].val);
	turtle(contextptr).s=gen2string(v.front());
	update_turtle_state(false,contextptr);
	turtle(contextptr)=t;
	return update_turtle_state(true,contextptr);
      }
    }
    turtle(contextptr).s=gen2string(g);
    return update_turtle_state(false,contextptr);
  }
  static const char _ecris_s []="ecris";
  static define_unary_function_eval2 (__ecris,&_ecris,_ecris_s,&printastifunction);
  define_unary_function_ptr5( at_ecris ,alias_at_ecris,&__ecris,0,T_LOGO);

  gen _signe(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    return _ecris(makevecteur(g,20,10,10),contextptr);
  }
  static const char _signe_s []="signe";
  static define_unary_function_eval2 (__signe,&_signe,_signe_s,&printastifunction);
  define_unary_function_ptr5( at_signe ,alias_at_signe,&__signe,0,T_LOGO);

  gen _saute(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    _leve_crayon(0,contextptr);
    _avance(g,contextptr);
    return _baisse_crayon(0,contextptr);
  }
  static const char _saute_s []="saute";
  static define_unary_function_eval2 (__saute,&_saute,_saute_s,&printastifunction);
  define_unary_function_ptr5( at_saute ,alias_at_saute,&__saute,0,T_LOGO);

  gen _pas_de_cote(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    _leve_crayon(0,contextptr);
    _tourne_droite(-90,contextptr);
    _avance(g,contextptr);
    _tourne_droite(90,contextptr);
    return _baisse_crayon(0,contextptr);
  }
  static const char _pas_de_cote_s []="pas_de_cote";
  static define_unary_function_eval2 (__pas_de_cote,&_pas_de_cote,_pas_de_cote_s,&printastifunction);
  define_unary_function_ptr5( at_pas_de_cote ,alias_at_pas_de_cote,&__pas_de_cote,0,T_LOGO);

  gen _cache_tortue(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    turtle(contextptr).visible=false;
    turtle(contextptr).radius = 0;
    return update_turtle_state(true,contextptr);
  }
  static const char _cache_tortue_s []="cache_tortue";
  static define_unary_function_eval2 (__cache_tortue,&_cache_tortue,_cache_tortue_s,&printastifunction);
  define_unary_function_ptr5( at_cache_tortue ,alias_at_cache_tortue,&__cache_tortue,0,T_LOGO);

  gen _montre_tortue(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    turtle(contextptr).visible=true;
    turtle(contextptr).radius = 0;
    return update_turtle_state(true,contextptr);
  }
  static const char _montre_tortue_s []="montre_tortue";
  static define_unary_function_eval2 (__montre_tortue,&_montre_tortue,_montre_tortue_s,&printastifunction);
  define_unary_function_ptr5( at_montre_tortue ,alias_at_montre_tortue,&__montre_tortue,0,T_LOGO);

  gen _debut_enregistrement(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g(g0);
    // logo instruction
    int nmax=10,n=0;
    for (;n<nmax && g.type!=_SYMB && g.type!=_IDNT;n++){
      g=__input.op(gen(makevecteur(string2gen(gettext("Give a name to the procedure, e.g. test"),false),identificateur(" logo_record_name")),_SEQ__VECT),contextptr);
      if (g.type==_VECT && g._VECTptr->size()==2)
	g=g._VECTptr->back();
    }
    if (g.type!=_SYMB && g.type!=_IDNT)
      return gensizeerr(gettext("Give a name to thr procedure, e.g. \"test\""));
    return g;
  }
  static const char _debut_enregistrement_s []="debut_enregistrement";
  static define_unary_function_eval2 (__debut_enregistrement,&_debut_enregistrement,_debut_enregistrement_s,&printastifunction);
  define_unary_function_ptr5( at_debut_enregistrement ,alias_at_debut_enregistrement,&__debut_enregistrement,0,T_LOGO);

  gen _fin_enregistrement(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g(g0);
    // logo instruction
    int nmax=10,n=0;
    for (;n<nmax && g.type!=_STRNG;n++){
      g=__input.op(gen(makevecteur(string2gen("Give a filename, e.g. \"test\"",false),identificateur(" logo_file_name")),_SEQ__VECT),contextptr);
      if (g.type==_VECT && g._VECTptr->size()==2)
	g=g._VECTptr->back();
    }
    if (g.type!=_STRNG)
      return gensizeerr(gettext("Give a filename, e.g. \"test\""));
    // Search for debut_enregistrement in history_out(contextptr)
    int s=history_in(contextptr).size(),i;
    for (i=s-1;i>=0;--i){
      if (history_in(contextptr)[i].is_symb_of_sommet(at_debut_enregistrement))
	break;
    }
    if (i<0)
      return gensizeerr(gettext("Instruction debut_enregistrement not found"));
    ofstream of(g._STRNGptr->c_str());
    if (i<int(history_out(contextptr).size()))
      of << history_out(contextptr)[i] << "():={" << endl;
    else
      of << history_in(contextptr)[i]._SYMBptr->feuille << "():={" << endl;
    for (++i;i<s-1;++i)
      of << " " << history_in(contextptr)[i] << ";" << endl;
    of << "}" << endl;
    return _read(g,contextptr);
  }
  static const char _fin_enregistrement_s []="fin_enregistrement";
  static define_unary_function_eval2 (__fin_enregistrement,&_fin_enregistrement,_fin_enregistrement_s,&printastifunction);
  define_unary_function_ptr5( at_fin_enregistrement ,alias_at_fin_enregistrement,&__fin_enregistrement,0,T_LOGO);

  gen _repete(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<2)
      return gensizeerr(contextptr);
    // logo instruction
    vecteur v = *g._VECTptr;
    v[0]=eval(v[0],contextptr);
    if (v.front().type!=_INT_)
      return gentypeerr(contextptr);
    gen prog=vecteur(v.begin()+1,v.end());
    int i=absint(v.front().val);
    gen res;
    for (int j=0;j<i;++j){
      res=eval(prog,contextptr);
    }
    return res;
  }
  static const char _repete_s []="repete";
  static define_unary_function_eval2_quoted (__repete,&_repete,_repete_s,&printastifunction);
  define_unary_function_ptr5( at_repete ,alias_at_repete,&__repete,_QUOTE_ARGUMENTS,T_RETURN);

  gen _crayon(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    if (g.type!=_INT_){
      gen res=turtle(contextptr).color;
      res.subtype=_INT_COLOR;
      return res;
    }
    turtle(contextptr).color=g.val;
    turtle(contextptr).radius = 0;
    return update_turtle_state(true,contextptr);
  }
  static const char _crayon_s []="crayon";
  static define_unary_function_eval2 (__crayon,&_crayon,_crayon_s,&printastifunction);
  define_unary_function_ptr5( at_crayon ,alias_at_crayon,&__crayon,0,T_LOGO);

  gen _efface(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_INT_){
      _crayon(int(FL_WHITE),contextptr);
      _recule(g,contextptr);
      return _crayon(0,contextptr);
    }
    // logo instruction
    turtle(contextptr) = logo_turtle();
    turtle_stack(contextptr).clear();
    return update_turtle_state(true,contextptr);
  }
  static const char _efface_s []="efface";
  static define_unary_function_eval2 (__efface,&_efface,_efface_s,&printastifunction);
  define_unary_function_ptr5( at_efface ,alias_at_efface,&__efface,0,T_LOGO);

  gen _vers(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen x=evalf_double(g._VECTptr->front(),1,contextptr),
      y=evalf_double(g._VECTptr->back(),1,contextptr);
    if (x.type!=_DOUBLE_ || y.type!=_DOUBLE_)
      return gensizeerr(contextptr);
    double xv=x._DOUBLE_val,yv=y._DOUBLE_val,xt=turtle(contextptr).x,yt=turtle(contextptr).y;
    double theta=atan2(yv-yt,xv-xt);
    return _cap(theta*180/M_PI,contextptr);
  }
  static const char _vers_s []="vers";
  static define_unary_function_eval2 (__vers,&_vers,_vers_s,&printastifunction);
  define_unary_function_ptr5( at_vers ,alias_at_vers,&__vers,0,T_LOGO);

  static int find_radius(const gen & g,int & r,int & theta2,bool & direct){
    int radius;
    direct=true;
    theta2 = 360 ;
    // logo instruction
    if (g.type==_VECT && !g._VECTptr->empty()){
      vecteur & v = *g._VECTptr;
      if (v.size()<2)
	return RAND_MAX; // setdimerr(contextptr);
      if (v[0].type==_INT_)
	r=v[0].val;
      else {
	gen v0=evalf_double(v[0],1,context0);
	if (v0.type==_DOUBLE_)
	  r=int(v0._DOUBLE_val+0.5);
	else 
	  return RAND_MAX; // setsizeerr(contextptr);
      }
      if (r<0){
	r=-r;
	direct=false;
      }
      int theta1;
      if (v[1].type==_DOUBLE_)
	theta1=int(v[1]._DOUBLE_val+0.5);
      else { 
	if (v[1].type==_INT_)
	  theta1=v[1].val;
	else return RAND_MAX; // setsizeerr(contextptr);
      }
      while (theta1<0)
	theta1 += 360;
      if (v.size()>=3){
	if (v[2].type==_DOUBLE_)
	  theta2 = int(v[2]._DOUBLE_val+0.5);
	else {
	  if (v[2].type==_INT_)
	    theta2 = v[2].val;
	  else return RAND_MAX; // setsizeerr(contextptr);
	}
	while (theta2<0)
	  theta2 += 360;
	radius = giacmin(r,512) | (giacmin(theta1,360) << 9) | (giacmin(theta2,360) << 18 );
      }
      else {// angle 1=0
	theta2 = theta1;
	if (theta2<0)
	  theta2 += 360;
	radius = giacmin(r,512) | (giacmin(theta2,360) << 18 );
      }
      return radius;
    }
    radius = 10;
    if (g.type==_INT_)
      radius= (r=g.val);
    if (g.type==_DOUBLE_)
      radius= (r=int(g._DOUBLE_val));
    if (radius<=0){
      radius = -radius;
      direct=false;
    }
    radius = giacmin(radius,512 )+(360 << 18) ; // 2nd angle = 360 degrees
    return radius;
  }

  static void turtle_move(int r,int theta2,GIAC_CONTEXT){
    double theta0;
    if (turtle(contextptr).direct)
      theta0=turtle(contextptr).theta-90;
    else {
      theta0=turtle(contextptr).theta+90;
      theta2=-theta2;
    }
    turtle(contextptr).x += r*(std::cos(M_PI/180*(theta2+theta0))-std::cos(M_PI/180*theta0));
    turtle(contextptr).y += r*(std::sin(M_PI/180*(theta2+theta0))-std::sin(M_PI/180*theta0));
    turtle(contextptr).theta = turtle(contextptr).theta+theta2 ;
    if (turtle(contextptr).theta<0)
      turtle(contextptr).theta += 360;
    if (turtle(contextptr).theta>360)
      turtle(contextptr).theta -= 360;
  }

  gen _rond(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int r,theta2,tmpr;
    tmpr=find_radius(g,r,theta2,turtle(contextptr).direct);
    if (tmpr==RAND_MAX)
      return gensizeerr(contextptr);
    turtle(contextptr).radius=tmpr;
    turtle_move(r,theta2,contextptr);
    return update_turtle_state(true,contextptr);
  }
  static const char _rond_s []="rond";
  static define_unary_function_eval2 (__rond,&_rond,_rond_s,&printastifunction);
  define_unary_function_ptr5( at_rond ,alias_at_rond,&__rond,0,T_LOGO);

  gen _disque(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int r,theta2,tmpr=find_radius(g,r,theta2,turtle(contextptr).direct);
    if (tmpr==RAND_MAX)
      return gensizeerr(contextptr);
    turtle(contextptr).radius=tmpr;
    turtle_move(r,theta2,contextptr);
    turtle(contextptr).radius += 1 << 27;
    return update_turtle_state(true,contextptr);
  }
  static const char _disque_s []="disque";
  static define_unary_function_eval2 (__disque,&_disque,_disque_s,&printastifunction);
  define_unary_function_ptr5( at_disque ,alias_at_disque,&__disque,0,T_LOGO);

  gen _disque_centre(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int r,theta2;
    bool direct;
    int radius=find_radius(g,r,theta2,direct);
    if (radius==RAND_MAX)
      return gensizeerr(contextptr);
    r=absint(r);
    _saute(r,contextptr);
    _tourne_gauche(direct?90:-90,contextptr);
    turtle(contextptr).radius = radius;
    turtle(contextptr).direct=direct;
    turtle_move(r,theta2,contextptr);
    turtle(contextptr).radius += 1 << 27;
    update_turtle_state(true,contextptr);
    _tourne_droite(direct?90:-90,contextptr);
    return _saute(-r,contextptr);
  }
  static const char _disque_centre_s []="disque_centre";
  static define_unary_function_eval2 (__disque_centre,&_disque_centre,_disque_centre_s,&printastifunction);
  define_unary_function_ptr5( at_disque_centre ,alias_at_disque_centre,&__disque_centre,0,T_LOGO);

  gen _polygone_rempli(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_INT_){
      turtle(contextptr).radius=-absint(g.val);
      if (turtle(contextptr).radius<-1)
	return update_turtle_state(true,contextptr);
    }
    return gensizeerr(gettext("Integer argument >= 2"));
  }
  static const char _polygone_rempli_s []="polygone_rempli";
  static define_unary_function_eval2 (__polygone_rempli,&_polygone_rempli,_polygone_rempli_s,&printastifunction);
  define_unary_function_ptr5( at_polygone_rempli ,alias_at_polygone_rempli,&__polygone_rempli,0,T_LOGO);

  gen _rectangle_plein(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen gx=g,gy=g;
    if (g.type==_VECT && g._VECTptr->size()==2){
      gx=g._VECTptr->front();
      gy=g._VECTptr->back();
    }
    for (int i=0;i<2;++i){
      _avance(gx,contextptr);
      _tourne_droite(-90,contextptr);
      _avance(gy,contextptr);
      _tourne_droite(-90,contextptr);
    }
    return _polygone_rempli(-8,contextptr);
  }
  static const char _rectangle_plein_s []="rectangle_plein";
  static define_unary_function_eval2 (__rectangle_plein,&_rectangle_plein,_rectangle_plein_s,&printastifunction);
  define_unary_function_ptr5( at_rectangle_plein ,alias_at_rectangle_plein,&__rectangle_plein,0,T_LOGO);

  gen _triangle_plein(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen gx=g,gy=g,gtheta=60;
    if (g.type==_VECT && g._VECTptr->size()>=2){
      vecteur & v=*g._VECTptr;
      gx=v.front();
      gy=v[1];
      gtheta=90;
      if (v.size()>2)
	gtheta=v[2];
    }
    logo_turtle t=turtle(contextptr);
    _avance(gx,contextptr);
    double save_x=turtle(contextptr).x,save_y=turtle(contextptr).y;
    _recule(gx,contextptr);
    _tourne_gauche(gtheta,contextptr);
    _avance(gy,contextptr);
    turtle(contextptr).x=save_x;
    turtle(contextptr).y=save_y;
    update_turtle_state(true,contextptr);
    turtle(contextptr)=t;
    turtle(contextptr).radius=0;
    update_turtle_state(true,contextptr);
    return _polygone_rempli(-3,contextptr);
  }
  static const char _triangle_plein_s []="triangle_plein";
  static define_unary_function_eval2 (__triangle_plein,&_triangle_plein,_triangle_plein_s,&printastifunction);
  define_unary_function_ptr5( at_triangle_plein ,alias_at_triangle_plein,&__triangle_plein,0,T_LOGO);

  gen _dessine_tortue(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // logo instruction
    /*
      _triangle_plein(makevecteur(17,5));
      _tourne_droite(90);
      _triangle_plein(makevecteur(5,17));
      return _tourne_droite(-90);
    */
    double save_x=turtle(contextptr).x,save_y=turtle(contextptr).y;
    _tourne_droite(90,contextptr);
    _avance(5,contextptr);
    _tourne_gauche(106,contextptr);
    _avance(18,contextptr);
    _tourne_gauche(148,contextptr);
    _avance(18,contextptr);
    _tourne_gauche(106,contextptr);
    _avance(5,contextptr);
    turtle(contextptr).x=save_x; turtle(contextptr).y=save_y;
    gen res=_tourne_gauche(90,contextptr);
    if (is_one(g))
      return res;
    return _polygone_rempli(-9,contextptr);
  }
  static const char _dessine_tortue_s []="dessine_tortue";
  static define_unary_function_eval2 (__dessine_tortue,&_dessine_tortue,_dessine_tortue_s,&printastifunction);
  define_unary_function_ptr5( at_dessine_tortue ,alias_at_dessine_tortue,&__dessine_tortue,0,T_LOGO);

#endif

  static const char _ramene_s []="ramene";
  static define_unary_function_eval2 (__ramene,&_read,_ramene_s,&printastifunction);
  define_unary_function_ptr5( at_ramene ,alias_at_ramene,&__ramene,0,T_LOGO);

  static const char _sauve_s []="sauve";
  static define_unary_function_eval2_quoted (__sauve,&_write,_sauve_s,&printastifunction);
  define_unary_function_ptr5( at_sauve ,alias_at_sauve,&__sauve,_QUOTE_ARGUMENTS,T_LOGO);


  static const char _hasard_s []="hasard";
  static define_unary_function_eval2 (__hasard,&_rand,_hasard_s,&printastifunction);
  define_unary_function_ptr5( at_hasard ,alias_at_hasard,&__hasard,0,T_LOGO);

  /* A FAIRE:
     traduction latex
  */
  gen _arc(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_arc,args);
    vecteur & v=*args._VECTptr;
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<3)
      return gendimerr(contextptr);
    gen e=remove_at_pnt(eval(v[0],contextptr));
    gen f=remove_at_pnt(eval(v[1],contextptr));
    gen g=eval(v[2],contextptr);
    gen c,diametre;
    if (g.is_symb_of_sommet(at_pnt)){
      g=remove_at_pnt(g);
      if (est_aligne(e,f,g,contextptr))
	return gensizeerr(gettext("Collinear points"));
      gen tmp=_circonscrit(gen(makevecteur(e,f,g),_SEQ__VECT),contextptr),tmp2,r;
      centre_rayon(tmp,c,r,false,contextptr);
      tmp=arg((f-c)/(e-c),contextptr);
      if (is_positive(tmp,contextptr))
	tmp2=tmp-cst_two_pi;
      else
	tmp2=tmp+cst_two_pi;
      r=arg((g-c)/(e-c),contextptr);
      if (is_positive(tmp*r,contextptr)&& is_greater(tmp/r,1,contextptr))
	g=tmp;
      else
	g=tmp2;
    }
    else {
      if (evalf_double(g,eval_level(contextptr),contextptr).type!=_DOUBLE_)
	return gensizeerr(contextptr);
      c=normal((e+f)/2+cst_i*(f-e)/(2*tan(g/2,contextptr)),contextptr);
    }
    diametre=gen(makevecteur(2*c-e,e),_GROUP__VECT);
    gen res=pnt_attrib(symbolic(at_cercle,gen((s==4 && v[3].type<_IDNT)?makevecteur(diametre,zero,g,v[3]):makevecteur(diametre,zero,g),_PNT__VECT)),attributs,contextptr);
    gen h=abs_norm(c-e,contextptr);
    if (s==3 || v[3].type<_IDNT)
      return res;
    vecteur w(1,res);
    w.push_back(eval(symb_sto(_point(c,contextptr),v[3]),contextptr));
    if (s>4)
      eval(symb_sto(h,v[4]),contextptr);
    return gen(w,_GROUP__VECT);
  }
  static const char _arc_s []="arc";
  static define_unary_function_eval_quoted (__arc,&giac::_arc,_arc_s);
  define_unary_function_ptr5( at_arc ,alias_at_arc,&__arc,_QUOTE_ARGUMENTS,true);

  gen _est(const gen & args,const propriete & f,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_SYMB)
      return zero;
    gen g=args._SYMBptr->feuille;
    if (g.type==_VECT){
      vecteur v=*g._VECTptr;
      if (!v.empty()){
	g=v.front();
	if (g.type==_VECT){
	  v=*g._VECTptr;
	  if (!v.empty() && v.front()==v.back()){
	    v.pop_back();
	    return f(gen(v,_SEQ__VECT),contextptr);
	  }
	}
      }
    }
    return zero;
  }

  int est_rect(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT){
    gen e(a-b+c-d);
    gen f(dotvecteur(d-a,b-a,contextptr));
    if (is_zero(simplify(e,contextptr)) && is_zero(simplify(f,contextptr))) {
      gen g(abs_norm2(a-b,contextptr));
      gen gg(abs_norm2(a-d,contextptr));
      if (is_zero(simplify(g-gg,contextptr))) 
	return 2;
      return 1;
    }
    return 0;
  }

  int est_losange(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT){
    gen e(a-b+c-d);
    gen f(dotvecteur(d-b,c-a,contextptr));
    if (is_zero(simplify(e,contextptr)) && is_zero(simplify(f,contextptr))) {
      if (is_zero(simplify(dotvecteur(d-a,b-a,contextptr),contextptr)))
	return 2;
      return 1;
    }
    return 0;
  }

  int est_parallelogramme(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT){
    gen e(a-b+c-d);
    if (is_zero(simplify(e,contextptr))) {
      gen g(dotvecteur(d-b,c-a,contextptr));
      gen h(dotvecteur(d-a,b-a,contextptr));
      if (is_zero(simplify(g,contextptr))){
	if (is_zero(simplify(h,contextptr)))
	  return 4;
	return 2;
      }
      if (is_zero(simplify(h,contextptr))) return 3;
      return 1;
    }
    return 0;
  }

  bool est_carre(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT){
    gen e(a-b+c-d);
    gen g(dotvecteur(d-b,c-a,contextptr));
    gen h(dotvecteur(d-a,b-a,contextptr));
    return is_zero(simplify(e,contextptr)) && is_zero(simplify(g,contextptr)) && is_zero(simplify(h,contextptr));
  }

  int est_isocele(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT){
    gen d(abs_norm2(b-a,contextptr));
    gen f(abs_norm2(c-a,contextptr));
    gen e(abs_norm2(b-c,contextptr));
    bool de=is_zero(simplify(d-e,contextptr));
    bool ef=is_zero(simplify(f-e,contextptr));
    bool fd=is_zero(simplify(f-d,contextptr));
    if (de && ef && fd)
      return 4;
    if (ef)
      return 3;
    if (fd)
      return 1;
    if (de)
      return 2;
    return 0;
  }

  bool est_equilateral(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT){
    gen d(abs_norm2(b-a,contextptr));
    gen f(abs_norm2(c-a,contextptr));
    gen e(abs_norm2(b-c,contextptr));
    return is_zero(simplify(d-f,contextptr)) && is_zero(simplify(e-f,contextptr));
  }

  int est_trianglerect(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT){
    gen d(dotvecteur(c-a,b-a,contextptr));
    gen e(dotvecteur(a-c,b-c,contextptr));
    gen f(dotvecteur(a-b,c-b,contextptr));
    if (is_zero(simplify(d,contextptr)))
      return 1;
    if (is_zero(simplify(e,contextptr)))
      return 3;
    if (is_zero(simplify(f,contextptr)))
      return 2;
    return 0;
  }
  
  gen _est_isocele(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.is_symb_of_sommet(at_pnt))
      return _est(args,_est_isocele,contextptr);
    vecteur v=sommet(args);
    if (v.size()==3){
      return est_isocele(remove_at_pnt(v[0]),remove_at_pnt(v[1]),remove_at_pnt(v[2]),contextptr);
    }
    return symbolic(at_est_isocele,args);
  }
  static const char _est_isocele_s []="is_isosceles";
  static define_unary_function_eval (__est_isocele,&giac::_est_isocele,_est_isocele_s);
  define_unary_function_ptr5( at_est_isocele ,alias_at_est_isocele,&__est_isocele,0,true);
  
  gen _est_equilateral(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.is_symb_of_sommet(at_pnt))
      return _est(args,_est_equilateral,contextptr);
    vecteur v=sommet(args);
    if (v.size()==3){
      return est_equilateral(remove_at_pnt(v[0]),remove_at_pnt(v[1]),remove_at_pnt(v[2]),contextptr);
    }
    return symbolic(at_est_equilateral,args);
  }
  static const char _est_equilateral_s []="is_equilateral";
  static define_unary_function_eval (__est_equilateral,&giac::_est_equilateral,_est_equilateral_s);
  define_unary_function_ptr5( at_est_equilateral ,alias_at_est_equilateral,&__est_equilateral,0,true);
  
  gen _est_parallelogramme(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.is_symb_of_sommet(at_pnt))
      return _est(args,_est_parallelogramme,contextptr);
    vecteur v=sommet(args);
    if (v.size()==4){
      return est_parallelogramme(remove_at_pnt(v[0]),remove_at_pnt(v[1]),remove_at_pnt(v[2]),remove_at_pnt(v[3]),contextptr);
    }
    return symbolic(at_est_parallelogramme,args);
  }
  static const char _est_parallelogramme_s []="is_parallelogram";
  static define_unary_function_eval (__est_parallelogramme,&giac::_est_parallelogramme,_est_parallelogramme_s);
  define_unary_function_ptr5( at_est_parallelogramme ,alias_at_est_parallelogramme,&__est_parallelogramme,0,true);

  gen _est_carre(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.is_symb_of_sommet(at_pnt))
      return _est(args,_est_carre,contextptr);
    vecteur v=sommet(args);
    if (v.size()==4){
      return est_carre(remove_at_pnt(v[0]),remove_at_pnt(v[1]),remove_at_pnt(v[2]),remove_at_pnt(v[3]),contextptr);
    }
    return symbolic(at_est_carre,args);
  }
  static const char _est_carre_s []="is_square";
  static define_unary_function_eval (__est_carre,&giac::_est_carre,_est_carre_s);
  define_unary_function_ptr5( at_est_carre ,alias_at_est_carre,&__est_carre,0,true);

  gen _est_losange(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.is_symb_of_sommet(at_pnt))
      return _est(args,_est_losange,contextptr);
    vecteur v=sommet(args);
    if (v.back()==v.front())
      v.pop_back();
    if (v.size()==4){
      return est_losange(remove_at_pnt(v[0]),remove_at_pnt(v[1]),remove_at_pnt(v[2]),remove_at_pnt(v[3]),contextptr);
    }
    return symbolic(at_est_losange,args);
  }
  static const char _est_losange_s []="is_rhombus";
  static define_unary_function_eval (__est_losange,&giac::_est_losange,_est_losange_s);
  define_unary_function_ptr5( at_est_losange ,alias_at_est_losange,&__est_losange,0,true);

  gen _est_rectangle(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.is_symb_of_sommet(at_pnt))
      return _est(args,_est_rectangle,contextptr);
    vecteur v=sommet(args);
    if (v.back()==v.front())
      v.pop_back();
    if (v.size()==3){
      return est_trianglerect(remove_at_pnt(v[0]),remove_at_pnt(v[1]),remove_at_pnt(v[2]),contextptr);
    }
    if (v.size()==4){
      return est_rect(remove_at_pnt(v[0]),remove_at_pnt(v[1]),remove_at_pnt(v[2]),remove_at_pnt(v[3]),contextptr);
    }
    return symbolic(at_est_rectangle,args);
  }
  static const char _est_rectangle_s []="is_rectangle";
  static define_unary_function_eval (__est_rectangle,&giac::_est_rectangle,_est_rectangle_s);
  define_unary_function_ptr5( at_est_rectangle ,alias_at_est_rectangle,&__est_rectangle,0,true);

  //=(c-a)*(d-b)/((c-b)*(d-a))= birapport de 4 complexes ou points a,b,c,d
  gen _birapport(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ((args.type==_VECT) && (args._VECTptr->size()==4)){
      vecteur v(*args._VECTptr);
      gen a(remove_at_pnt(v[0])),b(remove_at_pnt(v[1])),c(remove_at_pnt(v[2])),d(remove_at_pnt(v[3]));
      gen res =normal((c-a)*(d-b)/((c-b)*(d-a)),contextptr);
      return normal(res,contextptr);
    }
    return symbolic(at_birapport,args);
  }
  static const char _birapport_s []="cross_ratio";
  static define_unary_function_eval (__birapport,&giac::_birapport,_birapport_s);
  define_unary_function_ptr5( at_birapport ,alias_at_birapport,&__birapport,0,true);
  //puissance d'1 point A/ cercle C=d2-R2 (d=distance de A au centre(C))
  //parametre C,point ou para du cercle et point
  gen _puissance(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT) {
      vecteur v(*args._VECTptr);
      gen a,C;
      if (args._VECTptr->size()==3){
	C=_cercle(makevecteur(v[0],v[1]),contextptr);
	if (is_undef(C)) return C;
	a=remove_at_pnt(v[2]);
      }
      else {
	if (args._VECTptr->size()==2){
	  C=v[0];
	  a=remove_at_pnt(v[1]);
	}
	else
	  return gensizeerr(contextptr);
      }
      gen c,R;
      if (!centre_rayon(C,c,R,false,contextptr))
	return gensizeerr(contextptr);
      gen res =ratnormal(abs_norm2(c-a,contextptr)-abs_norm2(R,contextptr));
      return normal(res,contextptr);
    }
    return symbolic(at_puissance,args);
    //return gensizeerr(contextptr);
  }
  static const char _puissance_s []="powerpc";
  static define_unary_function_eval (__puissance,&giac::_puissance,_puissance_s);
  define_unary_function_ptr5( at_puissance ,alias_at_puissance,&__puissance,0,true);

  //axe radical de 2 cercles
  //parametres 2 cercles ou 4 parametres=param des 2 cercles
  gen _axe_radical(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT) {
      vecteur v(*args._VECTptr);
      gen C1,C2;
      if (args._VECTptr->size()==4){
	C1=_cercle(makesequence(v[0],v[1]),contextptr);
	C2=_cercle(makesequence(v[2],v[3]),contextptr);
      }
      else {
	if (args._VECTptr->size()==2){
	  C1=v[0];
	  C2=v[1];
	}
	else
	  return gensizeerr(contextptr);
      }
      if (is_undef(C1)) return C1;
      if (is_undef(C2)) return C2;
      gen c1,c2,R1,R2;
      if (!centre_rayon(C1,c1,R1,false,contextptr) ||
	  !centre_rayon(C2,c2,R2,false,contextptr) )
	return gensizeerr(contextptr);
      if (is_zero(c1-c2))
	return gensizeerr(gettext("Circle centers are identical"));
      gen k =ratnormal((abs_norm2(R1,contextptr)-abs_norm2(R2,contextptr))/abs_norm2(c1-c2,contextptr));
      gen H=ratnormal((c1+c2+k*(c2-c1))/2);
      gen K=ratnormal(H+cst_i*(c2-c1)); // FIXME 3-d
      return _droite(makesequence(normal(H,contextptr),normal(K,contextptr)),contextptr);
    }
    return symbolic(at_axe_radical,args);
    //return gensizeerr(contextptr);
  }
  static const char _axe_radical_s []="radical_axis";
  static define_unary_function_eval (__axe_radical,&giac::_axe_radical,_axe_radical_s);
  define_unary_function_ptr5( at_axe_radical ,alias_at_axe_radical,&__axe_radical,0,true);

  gen equation_homogene(const gen & eq,GIAC_CONTEXT){
    vecteur v(1,makevecteur(x__IDNT_e,y__IDNT_e,z__IDNT_e));
    alg_lvar(eq,v);
    gen v0=v.front();
    if (v0.type!=_VECT || v0._VECTptr->size()<3 || v0[0]!=x__IDNT_e || v0[1]!=y__IDNT_e || v0[2]!=z__IDNT_e)
      return gensizeerr(contextptr);
    // check that nothing else depends on x/y
    if (!is_zero(derive(v,x__IDNT_e,2,contextptr)) || 
	!is_zero(derive(v,y__IDNT_e,2,contextptr)) )
      return gensizeerr(contextptr);
    // homogeneize
    fraction feq(e2r(eq,v,contextptr));
    if (feq.num.type!=_POLY)
      return gensizeerr(contextptr);
    polynome p=*feq.num._POLYptr;
    // find total degree in x and y
    vector< monomial<gen> >::iterator it=p.coord.begin(),itend=p.coord.end();
    int xydeg=0,tmp;
    for (;it!=itend;++it){
      tmp=it->index[0]+it->index[1];
      if (tmp>xydeg)
	xydeg=tmp;
    }
    // adjust z degree
    for (it=p.coord.begin();it!=itend;++it){
      tmp=it->index[0]+it->index[1];
      if (tmp<xydeg)
	it->index[2]=xydeg-tmp; // note: in-place modif
    }
    return r2e(p,v,contextptr);
  }

  //renvoie une droite=la polaire d'un point A/ cercle C
  //parametres C,A ou param du cercle et A
  gen _polaire(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT) {
      vecteur v(*args._VECTptr);
      gen C,a;
      if (args._VECTptr->size()==3){
	gen b0=v[0], b1=v[1];
	if  ((b0.type!=_VECT)&& (b1.type!=_VECT)){
	  C=_cercle(makesequence(b0,b1),contextptr);
	  if (is_undef(C)) return C;
	  a=remove_at_pnt(v[2]);
	} 
	else 
	  return gensizeerr(contextptr);
      }
      else {
	if (args._VECTptr->size()==2){
	  C=v[0];
	  a=remove_at_pnt(v[1]);
	}
	else
	  return gensizeerr(contextptr);
      }
      gen c,R,a1,a2,p1,p2;
      if (!centre_rayon(C,c,R,false,contextptr)){
	gen eq=_equation(C,contextptr);
	if (is_undef(eq)) return eq;
	eq=equation_homogene(eq,contextptr);
	if (is_undef(eq)) return eq;
	gen x0=re(a,contextptr);
	gen y0=im(a,contextptr);
	// result is
	// D:=x0*diff(p(x,y,z),x)+y0*diff(p(x,y,z),y)+1*diff(p(x,y,z),z)
	gen res=x0*derive(eq,x__IDNT_e,contextptr)+y0*derive(eq,y__IDNT_e,contextptr)+derive(eq,z__IDNT_e,contextptr);
	if (is_undef(res))
	  return res;
	return _droite(recursive_normal(subst(res,z__IDNT_e,1,false,contextptr),contextptr),contextptr);
      }
      a1=re(a-c,contextptr);
      a2=im(a-c,contextptr);
      if (a1==0 && a2==0) return gensizeerr(contextptr);
      if (a1==0){
	p1=c+cst_i*(R*conj(R,contextptr))/a2;
	p2=1+c+cst_i*(R*conj(R,contextptr))/a2;
	return _droite(makesequence(normal(p1,contextptr),normal(p2,contextptr)),contextptr);
      }
      if (a2==0){
	p1=c+(R*conj(R,contextptr))/a1;
	p2=c+cst_i+(R*conj(R,contextptr))/a1;
	return _droite(makesequence(normal(p1,contextptr),normal(p2,contextptr)),contextptr);
      }
      p1=c+(R*conj(R,contextptr))/a1;
      p2=c+cst_i*(R*conj(R,contextptr))/a2;
      return _droite(makesequence(normal(p1,contextptr),normal(p2,contextptr)),contextptr);
    }
    return symbolic(at_polaire,args);
    //return gensizeerr(contextptr);
  }

  static const char _polaire_s []="polar";
  static define_unary_function_eval (__polaire,&giac::_polaire,_polaire_s);
  define_unary_function_ptr5( at_polaire ,alias_at_polaire,&__polaire,0,true);

  //renvoie un point= le pole d'une droite D par rapport a un cercle C
  //parametres C,D  
  gen _pole(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT) {
      vecteur v(*args._VECTptr);
      gen D,C;
      if (args._VECTptr->size()==3){
	C=_cercle(makesequence(v[0],v[1]),contextptr);
	if (is_undef(C)) return C;
	D=remove_at_pnt(v[2]);
      }
      else {
	if (args._VECTptr->size()==2){
	  C=v[0];
	  D=remove_at_pnt(v[1]);
	}
	else
	  return gensizeerr(contextptr);
      }
      gen c,R,A1,A2,a1,a2,k;
      if (!centre_rayon(C,c,R,false,contextptr)){
	gen eq=_equation(C,contextptr);
	eq=equation_homogene(eq,contextptr);
	if (is_undef(eq)) return eq;
	gen mat=qxa(eq,makevecteur(x__IDNT_e,y__IDNT_e,z__IDNT_e),contextptr);
	if (!ckmatrix(mat))
	  return gentypeerr(contextptr);
	matrice M=*mat._VECTptr;
	gen Ax=re(D[0],contextptr),Ay=im(D[0],contextptr);
	gen Bx=re(D[1],contextptr),By=im(D[1],contextptr);
	vecteur v=makevecteur(By-Ay,Ax-Bx,Bx*Ay-By*Ax);
	matrice Minv=minv(M,contextptr);
	if (is_undef(Minv)) return Minv;
	vecteur w=multmatvecteur(Minv,v);
	gen res=w[0]/w[2]+cst_i*w[1]/w[2];
	res=recursive_normal(res,contextptr);
	return symb_pnt(res,contextptr);
	return gensizeerr(contextptr);
      }
      A1=D[0]-c;
      A2=D[1]-c;
      a1=im(A1-A2,contextptr);
      a2=re(A2-A1,contextptr);
      k=im(A1*conj(A2,contextptr),contextptr);
      gen res =ratnormal(c+R*conj(R,contextptr)*(a1+cst_i*a2)/k);
      return symb_pnt(res,contextptr);
    }
    return  symbolic(at_pole,args);
    //return gensizeerr(contextptr);
  }
  static const char _pole_s []="pole";
  static define_unary_function_eval (__pole,&giac::_pole,_pole_s);
  define_unary_function_ptr5( at_pole ,alias_at_pole,&__pole,0,true);

  //renvoie un point (resp une droite) pole (resp polaire) de D/ cercle C
  //parametre C (ou param du cercle C), droite D (resp point D)   
  gen _polaire_reciproque(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur w(gen2vecteur(args));
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(w,attributs,contextptr);
    if (w.empty() || s!=2)
      return gensizeerr(gettext("reciprocation"));
    gen c=w[0],a=w[1];
    if (a.type==_VECT){
      const vecteur v=*a._VECTptr;
      const_iterateur it=v.begin(),itend=v.end();
      vecteur res;
      for (;it!=itend;++it){
	a=*it;
	a=remove_at_pnt(a);
	if (a.type==_VECT) 
	  res.push_back(put_attributs(_pole(gen(makevecteur(c,a),_SEQ__VECT),contextptr),attributs,contextptr));
	else
	  res.push_back(put_attributs(_polaire(gen(makevecteur(c,a),_SEQ__VECT),contextptr),attributs,contextptr));
      }
      return gen(res,a.subtype);
    }
    a=remove_at_pnt(a);
    if (a.type==_VECT) 
      return put_attributs(_pole(gen(makevecteur(c,a),_SEQ__VECT),contextptr),attributs,contextptr);
    return put_attributs(_polaire(gen(makevecteur(c,a),_SEQ__VECT),contextptr),attributs,contextptr);
  }
  static const char _polaire_reciproque_s []="reciprocation";
  static define_unary_function_eval (__polaire_reciproque,&giac::_polaire_reciproque,_polaire_reciproque_s);
  define_unary_function_ptr5( at_polaire_reciproque ,alias_at_polaire_reciproque,&__polaire_reciproque,0,true);

  //teste si deux cercles C1 centre c1 rayon R1 et C2  centre c2 rayon R2
  //sont orthogonaux
  bool est_orthogonal(const gen & c1,const gen & R1,const gen & c2,const gen & R2,GIAC_CONTEXT){
    gen res =simplify(-abs_norm2(R1,contextptr)-abs_norm2(R2,contextptr)+abs_norm2(c1-c2,contextptr),contextptr);
    return is_zero(res);
  }

  //teste si deux cercles C1 et C2 sont orthogonaux ou 
  //si 2 droites D1,D2 sont perpendiculaires
  //parametres C1, C2 (ou D1,D2 ou 2 vecteurs de 2 points),
  static gen est_orthogonal(const gen & args,bool perp,GIAC_CONTEXT){
    if ( args.type==_VECT && args._VECTptr->size()==2){
      vecteur v(*args._VECTptr);
      gen a=remove_at_pnt(v[0]),b=remove_at_pnt(v[1]);
      if (a.is_symb_of_sommet(at_hyperplan)){ 
	if (b.type==_VECT && b._VECTptr->size()==3)
	  return est_parallele_vecteur(hyperplan_normal(a),*b._VECTptr,contextptr);
	if (ckmatrix(b) && b._VECTptr->size()==2)
	  return est_parallele_vecteur(hyperplan_normal(a),*(b._VECTptr->back()-b._VECTptr->front())._VECTptr,contextptr);
	if (b.is_symb_of_sommet(at_hyperplan))
	  return is_zero(simplify(dotvecteur(hyperplan_normal(a),hyperplan_normal(b)),contextptr));
      }
      if (b.is_symb_of_sommet(at_hyperplan) && !a.is_symb_of_sommet(at_hyperplan))
	return _est_orthogonal(makesequence(b,a),contextptr);
      if ( a.type!=_VECT && b.type!=_VECT){
	//on a 2 cercles ou 2 spheres a et b
	gen c1,c2,R1,R2;
	if (!centre_rayon(a,c1,R1,false,contextptr) ||
	    !centre_rayon(b,c2,R2,false,contextptr))
	  return gensizeerr(contextptr);
	return est_orthogonal(c1,R1,c2,R2,contextptr);
      }
      if ( (a.type!=_VECT) || (a._VECTptr->size()!=2) || (b.type!=_VECT) || (b._VECTptr->size()!=2) )
	return gensizeerr(contextptr);     
      //on a 2 droites
      if (perp && a[0].type==_VECT && !est_coplanaire(a[0],a[1],b[0],b[1],contextptr))
	return false;
      return est_perpendiculaire(a[0]-a[1],b[0]-b[1],contextptr);
    }
    return symbolic(at_est_orthogonal,args);
  }
  
  gen _est_orthogonal(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return est_orthogonal(args,false,contextptr);
  }

  static const char _est_orthogonal_s []="is_orthogonal";
  static define_unary_function_eval (__est_orthogonal,&giac::_est_orthogonal,_est_orthogonal_s);
  define_unary_function_ptr5( at_est_orthogonal ,alias_at_est_orthogonal,&__est_orthogonal,0,true);
  
  gen _est_perpendiculaire(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return est_orthogonal(args,true,contextptr);
  }
  static const char _est_perpendiculaire_s []="is_perpendicular";
  static define_unary_function_eval (__est_perpendiculaire,&giac::_est_perpendiculaire,_est_perpendiculaire_s);
  define_unary_function_ptr5( at_est_perpendiculaire ,alias_at_est_perpendiculaire,&__est_perpendiculaire,0,true);


  //teste si 2 points (resp 2 droites) sont conj /cercle C
  //teste si 2 points (resp droites) sont conjugues /2 droites D1,D2
  //teste si 2 points sont conjugues /2 points
  //est_conjugue(C,D,A) ou est_conjugue(C,A,D)(C=cercle,D=droite,A=point)
  //est_conjugue(C,B,A) (C=cercle,B,A= 2 points)
  //est_conjugue(C,D3,D4) (C=cercle, D3,D4= 2 droites)
  //est_conjugue(D1,D2,B,A) (B,A 2 points)
  //est_conjugue(D1,D2,D3,A) ou est_conjugue(D1,D2,A,D3)(D3=droite,A= point)
  //est_conjugue(D1,D2,D3,D4) (D3,D4= 2 droites)
  //est_conjugue(P1,P2,B,A) (P1,P2,B,A= 4 points)
  gen _est_conjugue(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<3))
      return symbolic(at_est_conjugue,args);
    gen a,b,c,d;
    vecteur v(*args._VECTptr);
    a=remove_at_pnt(v[0]);
    b=remove_at_pnt(v[1]);
    int s=v.size();
    if (s==3) {
      c=remove_at_pnt(v[2]);
      //par rapport a un cercle
      if ((c.type!=_VECT) && (b.type!=_VECT)) // on a 2 points
	return _est_orthogonal(makesequence(v[0],_cercle(makesequence(v[1],v[2]),contextptr)),contextptr);
      if (b.type==_VECT){
	gen p=_pole(makesequence(v[0],v[1]),contextptr);
	if (is_undef(p)) return p;
	if (c.type!=_VECT)
	  return is_zero(c-remove_at_pnt(p));
	return est_element(remove_at_pnt(p),c,contextptr);       
      }
      if ((c.type==_VECT) && (b.type!=_VECT)){
	gen p=_pole(makesequence(v[0],c),contextptr);
	if (is_undef(p)) return p;
	return is_zero(b-remove_at_pnt(p));
      }
      return gensizeerr(contextptr);
    }
    if (s==4 && a.type==_VECT && b.type==_VECT ){
      gen dd=_conj_harmonique(makesequence(v[0],v[1],v[2]),contextptr);
      if (is_undef(dd)) return dd;
      d= remove_at_pnt(v[3]);
      dd=remove_at_pnt(dd);
      if (d.type!=_VECT)
	return est_element(d,dd,contextptr);
      return est_element(d[0],dd,contextptr) && est_element(d[1],dd,contextptr);    }
    if (s==4 && (b.type!=_VECT) && (a.type!=_VECT)){ // on a /2 points
      if ((c.type!=_VECT)&&(d.type!=_VECT))
	return _est_harmonique(args,contextptr);
      return gensizeerr(contextptr);
    }
    return 0;
  }

  static const char _est_conjugue_s []="is_conjugate";
  static define_unary_function_eval (__est_conjugue,&giac::_est_conjugue,_est_conjugue_s);
  define_unary_function_ptr5( at_est_conjugue ,alias_at_est_conjugue,&__est_conjugue,0,true);

  //teste si 4 points forment une division harmonique
  bool est_harmonique(const gen & a,const gen & b,const gen & c,const gen & d,GIAC_CONTEXT){
    if (est_aligne(a,b,c,contextptr) && est_aligne(a,b,d,contextptr)){
      gen e((c-a)/(c-b)+(d-a)/(d-b));
      return is_zero(simplify(e,contextptr));
    } else return false; // setsizeerr(contextptr);
    return 0;
  }
  gen _est_harmonique(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ((args.type==_VECT) && (args._VECTptr->size()==4)){
      vecteur v(*args._VECTptr);
      return est_harmonique(remove_at_pnt(v[0]),remove_at_pnt(v[1]),remove_at_pnt(v[2]),remove_at_pnt(v[3]),contextptr);
    }
    return symbolic(at_est_harmonique,args);
  }
  static const char _est_harmonique_s []="is_harmonic";
  static define_unary_function_eval (__est_harmonique,&giac::_est_harmonique,_est_harmonique_s);
  define_unary_function_ptr5( at_est_harmonique ,alias_at_est_harmonique,&__est_harmonique,0,true);

  //conj_harmonique(D1,D2,A)= la droite des conjugues de A par rapport a D1,D2
  //conj_harmonique(D1,D2,D3)=D4 (D1,D2,D3,D4)=-1 (D1,D2,D3 concourantes ou //)
  //point D=conj_harmonique(A,B,C) est le point tel que (A,B,C,D)=-1
  gen _conj_harmonique(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_conj_harmonique,args);
    //setsizeerr(contextptr);
    vecteur v(*args._VECTptr);
    gen d1,d2,a,D3,p1,p2,b;
    if (args._VECTptr->size()==3){
      bool est_point=false;
      d1=remove_at_pnt(v[0]);
      if (d1.type==_VECT){
	if (d1._VECTptr->size()!=2)
	  return gensizeerr(contextptr);
	d1=d1._VECTptr->back()-d1._VECTptr->front();
      }
      else
	est_point=true;
      d2=remove_at_pnt(v[1]);
      if (d2.type==_VECT){
	if (est_point || d2._VECTptr->size()!=2)
	  return gensizeerr(contextptr);
	d2=d2._VECTptr->front()-d2._VECTptr->back();
      }
      else {
	if (!est_point)
	  return gensizeerr(contextptr);
      }
      a=remove_at_pnt(v[2]);
      if (est_point){
	if (a.type==_VECT)
	  return gensizeerr(contextptr);
	gen e(im((a-d1)*conj(a-d2,contextptr),contextptr));
	if (! is_zero(simplify(e,contextptr)))
	  //les points ne sont pas alignes
	  return gensizeerr(contextptr);
	gen d=normal((d1*a+d2*a-2*d1*d2)/(2*a-d2-d1),contextptr);
	return symb_pnt(d,contextptr);
      }
      if (est_parallele(d1,d2,contextptr)) {
	if (a.type==_VECT){
	  if (a._VECTptr->size()!=2)
	    return gensizeerr(gettext("conj_harmonique"));
	  gen aa=a._VECTptr->front()-a._VECTptr->back();
	  if (est_parallele(d1,aa,contextptr))
	    a=a._VECTptr->front();
	  else return gensizeerr(gettext("conj_harmonique"));
	}
	D3=_perpendiculaire(makesequence(a,v[0]),contextptr);
	vecteur w1(inter(D3,v[0],contextptr));
	p1=remove_at_pnt(w1.front());
	vecteur w2(inter(D3,v[1],contextptr));
	p2=remove_at_pnt(w2.front());
	b=_conj_harmonique(makesequence(p1,p2,a),contextptr);
	return _parallele(makesequence(b,v[0]),contextptr);
      }
      else  {
	vecteur w1(inter(v[0],v[1],0));
	p1=remove_at_pnt(w1.front());
	if (a.type==_VECT){
	  if (a._VECTptr->size()!=2)
	    return gensizeerr(gettext("conj_harmonique"));
	  a=a._VECTptr->front()-a._VECTptr->back();
	  if (est_element(p1,v[2],contextptr))
	    a=p1+a; else  return gensizeerr(contextptr);
	}
	D3=_parallele(makesequence(a,v[0]),contextptr);
	if (is_undef(D3)) return D3;
	vecteur w2(inter(D3,v[1],0));
	p2=remove_at_pnt(w2.front());
	b=normal(2*p2-a,contextptr);
	gen res=_droite(makesequence(normal(p1,contextptr),b),contextptr);
	return res;
      }
    }
    else return gensizeerr(contextptr);
    return 0;
  }
  static const char _conj_harmonique_s []="harmonic_conjugate";
  static define_unary_function_eval (__conj_harmonique,&giac::_conj_harmonique,_conj_harmonique_s);
  define_unary_function_ptr5( at_conj_harmonique ,alias_at_conj_harmonique,&__conj_harmonique,0,true);

  //renvoie M point qui divise AB ds un rapport k(reel ou complexe)
  //mes alg(MA=k*MB) (z*a=k*(z-b)), parametres: A,B,k
  gen _point_div(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<3))
      return symbolic(at_point_div,args);
    vecteur v(*args._VECTptr);
    gen a=remove_at_pnt(eval(v[0],contextptr)),b=remove_at_pnt(eval(v[1],contextptr)),k=eval(v[2],contextptr);
    //if (! is_zero(im(k,contextptr))) return gensizeerr(contextptr);
    gen c;
    k=normal(1-k,contextptr);
    if (is_zero(k)) return gensizeerr(contextptr);
    //{c=unsigned_inf;}
    c=normal((a+(k-1)*b)/k,contextptr);
    return symb_pnt(c,contextptr);
  }
  static const char _point_div_s []="division_point";
  static define_unary_function_eval (__point_div,&giac::_point_div,_point_div_s);
  define_unary_function_ptr5( at_point_div ,alias_at_point_div,&__point_div,0,true);

  //=1 si 3 cercles ont meme axe radical,2 si concentriques et 0 sinon
  int est_faisceau_cercle(const gen & c1,const gen & R1,const gen & c2,const gen & R2,const gen & c3,const gen & R3,GIAC_CONTEXT){
    if (is_equal(makevecteur(c1,c2))){
      if (is_equal(makevecteur(c1,c3))) 
	return 2;
      else 
	return 0;
    } 
    if (is_equal(makevecteur(c1,c3)))
      return 0;
    //les centres sont distincts
    if (!est_aligne(c1,c2,c3,contextptr)) return 0;
    //les centres sont alignes
    gen v=_axe_radical(makesequence(_cercle(makesequence(c1,R1),contextptr),_cercle(makesequence(c2,R2),contextptr)),contextptr);
    gen w=_axe_radical(makesequence(_cercle(makesequence(c1,R1),contextptr),_cercle(makesequence(c3,R3),contextptr)),contextptr);
    v=remove_at_pnt(v);
    return est_element(v[0],w,contextptr) && est_element(v[1],w,contextptr);
  }

  //renvoie 3 si tous les cercles sont confondus
  //renvoie 2 si concentriques 
  //renvoie 1 si les cercles ont meme axe radical et 0 sinon
  gen _est_faisceau_cercle(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen c1,R1;
    if (args.type!=_VECT){
      if (!centre_rayon(args,c1,R1,false,contextptr))
	return gensizeerr(contextptr);
      return 3;
    }
    int s=args._VECTptr->size();
    vecteur v(*args._VECTptr);
    if (s==1) {
      if (!centre_rayon(v[0],c1,R1,false,contextptr))
	return gensizeerr(contextptr);
      return 3;
    }
    if (s>=2){
      gen c2,c3,R2,R3;
      if (!centre_rayon(v[0],c1,R1,false,contextptr) ||
	  !centre_rayon(v[1],c2,R2,false,contextptr))
	return gensizeerr(contextptr);
      c1=remove_at_pnt(c1);
      c2=remove_at_pnt(c2);
      int res;
      int i=2;
      while (c1==c2 && R1==R2 && i<s){
	if (!centre_rayon(v[i],c2,R2,false,contextptr))
	  return gensizeerr(contextptr);
	c2=remove_at_pnt(c2);
	i=i+1;
      }
      if (i==s){
	if (c1==c2 && R1==R2) return 3;
	if (c1==c2) return 2;
	return 1;
      }
      for (int j=i;j<s;j++){
	if (!centre_rayon(v[j],c3,R3,false,contextptr))
	  return gensizeerr(contextptr);
	res=est_faisceau_cercle(c1,R1,c2,R2,remove_at_pnt(c3),R3,contextptr);
	if (res==0) return 0; 
      }
      return res;
    }
    return symbolic(at_est_faisceau_cercle,args);
  }
  static const char _est_faisceau_cercle_s []="is_harmonic_circle_bundle";
  static define_unary_function_eval (__est_faisceau_cercle,&giac::_est_faisceau_cercle,_est_faisceau_cercle_s);
  define_unary_function_ptr5( at_est_faisceau_cercle ,alias_at_est_faisceau_cercle,&__est_faisceau_cercle,0,true);
  
  //renvoie 1 si les 2 droites sont confondues,2 si elles sont// 
  //et 0 sinon
  static int est_confondu_droite(const gen & a,const gen & b,GIAC_CONTEXT){
    gen d(im((a[0]-a[1])*conj(b[0]-b[1],contextptr),contextptr));
    if (is_zero(simplify(d,contextptr))) {
      if (est_element(b[0],_droite(a,contextptr),contextptr))
	return 1;
      else return 2;
    }
    return 0;
  }
  //renvoie 1 si les 3 droites sont concourantes,2 si elles sont// 
  //et 0 sinon qd a et b sont 2 droites differentes
  int est_faisceau_droite(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT){
    gen d(simplify(im((a[0]-a[1])*conj(b[0]-b[1],contextptr),contextptr),contextptr));
    gen e(simplify(im((a[0]-a[1])*conj(c[0]-c[1],contextptr),contextptr),contextptr));
    if (is_zero(d)) {
      if (is_zero(e))
	return 2;
      else return 0;
    }
    if (is_zero(e))
      return 0;
    //les 3 droites ne sont pas paralleles
    gen v=inter(_droite(a,contextptr),_droite(b,contextptr),0);
    //gen w =inter(_droite(a,contextptr),_droite(c,contextptr),0);
    if (v.type==_VECT && !v._VECTptr->empty() && est_element(v[0],_droite(c,contextptr),contextptr))
      return 1;
    else return 0;
  }

  //args contient au moins 3 droites
  //renvoie 3 si toutes les droites sont confondues
  //renvoie 2 si les droites sont //
  //renvoie 1 si les droites sont concourantes en 1 meme point
  //renvoie 0 sinon
  gen _est_faisceau_droite(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen a;
    if (args.type!=_VECT){
      a=remove_at_pnt(args);
      if ((a.type!=_VECT) || (a._VECTptr->size()!=2))
	return gensizeerr(contextptr);
      return 3; 
    }
    int s=args._VECTptr->size();
    vecteur v(*args._VECTptr);
    if (s==1) {
      a=remove_at_pnt(v[0]);
      if (a.type==_VECT)
	return gensizeerr(contextptr);
      return 3;
    } 
    if (s>=2){
      a=remove_at_pnt(v[0]);
      gen b=remove_at_pnt(v[1]);
      if ((a.type!=_VECT) || (a._VECTptr->size()!=2) || (b.type!=_VECT) || (b._VECTptr->size()!=2))
	return gensizeerr(contextptr); 
      int i=2;
      int res;
      while (est_confondu_droite(a,b,contextptr) && i<s){
	b=remove_at_pnt(v[i]);
	if ((b.type!=_VECT) || (b._VECTptr->size()!=2))
	  return gensizeerr(contextptr); 
	i=i+1;
      }
      if (i==s){
	res=est_confondu_droite(a,b,contextptr);
	if (res==1) return 3;
	if (res==0) return 1;
	return 2;
      }
      for (int j=i;j<s;j++){
	gen c=remove_at_pnt(v[j]);
	if ((c.type!=_VECT) || (c._VECTptr->size()!=2)) 
	  return gensizeerr(contextptr);
	res=est_faisceau_droite(a,b,c,contextptr);
	if (res==0) return 0; 
      }
      return res;
    }
    return symbolic(at_est_faisceau_droite,args);
  }
  static const char _est_faisceau_droite_s []="is_harmonic_line_bundle";
  static define_unary_function_eval (__est_faisceau_droite,&giac::_est_faisceau_droite,_est_faisceau_droite_s);
  define_unary_function_ptr5( at_est_faisceau_droite ,alias_at_est_faisceau_droite,&__est_faisceau_droite,0,true);

  //div_harmonique(A,B,C,D) remplit D tel que (A,B,C,D)=-1 
  //div_harmonique(D1,D2,A,D) remplit D tel que (D1,D2,A,D)=-1 
  //div_harmonique(D1,D2,D3,D) remplit D tel que (D1,D2,D3,D)=-1
  //et dessine les 4 points ou les 3 droites + le point ou les 4 droites
  gen _div_harmonique(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<4))
      return symbolic(at_div_harmonique,args);
    vecteur v(*args._VECTptr);
    gen d;
    gen v0=eval(v[0],contextptr),v1=eval(v[1],contextptr),v2=eval(v[2],contextptr);      
    d=_conj_harmonique(makesequence(v0,v1,v2),contextptr); 
    if (is_undef(d)) return d;
    if (v0.is_symb_of_sommet(at_pnt))
      v0=symb_pnt(v0,default_color(contextptr),contextptr);
    if (v1.is_symb_of_sommet(at_pnt))
      v1=symb_pnt(v0,default_color(contextptr),contextptr);
    if (v2.is_symb_of_sommet(at_pnt))
      v2=symb_pnt(v0,default_color(contextptr),contextptr);
    vecteur w=makevecteur(v0,v1,v2);
    w.push_back(eval(symb_sto(d,v[3]),contextptr));
    return gen(w,_GROUP__VECT);
    return gensizeerr(gettext("div_harmonique)"));
  }
  static const char _div_harmonique_s []="harmonic_division";
  static define_unary_function_eval_quoted (__div_harmonique,&giac::_div_harmonique,_div_harmonique_s);
  define_unary_function_ptr5( at_div_harmonique ,alias_at_div_harmonique,&__div_harmonique,_QUOTE_ARGUMENTS,true);

  //enveloppe(y+x*tan(t)-2*sin(t),t)
  //enveloppe(y+x*tan(t)-2*sin(t),[t])
  //enveloppe(y+x*tan(t)-2*sin(t),[x,y,t])
 
  gen _enveloppe(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2))
      return symbolic(at_enveloppe,args);
    vecteur vargs(*args._VECTptr);
    if (vargs[0].is_symb_of_sommet(at_pnt))
      vargs[0]=_equation(vargs[0],contextptr);
    gen equ=equal2diff(vargs[0]),var=vargs[1];
    //identificateur gen xvar=vx_var,yvar=y__IDNT_e;
    gen tvar,xvar,yvar;
    if (var.type!=_VECT){
      tvar=var;
      xvar=vx_var;
      yvar=y__IDNT_e;
    } else
      if (var._VECTptr->size()==1) {
	tvar=var[0];
	xvar=vx_var;
	yvar=y__IDNT_e;
      } else
	if (var._VECTptr->size()==3){
	  xvar=var[0];
	  if (xvar.type!=_IDNT)
	    return gentypeerr(contextptr);
	  yvar=var[1];
	  if (yvar.type!=_IDNT)
	    return gentypeerr(contextptr);
	  tvar=var[2];
	} else {
	  return gensizeerr(gettext("enveloppe"));
	}
    gen T;
    double tmin,tmax;
    readrange(tvar,gnuplot_tmin,gnuplot_tmax,T,tmin,tmax,contextptr);
    gen equder=derive(equ,T,contextptr);
    if (is_undef(equder))
      return equder;
    gen sol;
    sol=solve(makevecteur(equ,equder),makevecteur(xvar,yvar),0,contextptr);
    if (sol.type==_VECT){
      vecteur & v = *sol._VECTptr;
      vecteur res;
      int s=v.size();
      for (int i=0;i<s;++i){
	gen tmp=v[i];
	if (tmp.type==_VECT && tmp._VECTptr->size()==2){
	  tmp=tmp._VECTptr->front()+cst_i*tmp._VECTptr->back();
	  vargs[0]=tmp;
	  vargs[1]=tvar;
	  tmp=gen(vargs,_SEQ__VECT);
	  tmp=_paramplot(tmp,contextptr);
	  if (tmp.type==_VECT)
	    res=mergevecteur(res,*tmp._VECTptr);
	  else
	    res.push_back(tmp);
	}
      }
      return res; // gen(res,_SEQ__VECT);
    }
    return sol;
  }
    
  static const char _enveloppe_s []="envelope";
  static define_unary_function_eval (__enveloppe,&giac::_enveloppe,_enveloppe_s);
  define_unary_function_ptr5( at_enveloppe ,alias_at_enveloppe,&__enveloppe,0,true);

#ifdef WITH_GNUPLOT
  gen _gnuplot(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v(gen2vecteur(args));
    int s=v.size();
    bool clrplot;
    int out_handle;
    FILE * gnuplot_out_readstream,* stream = open_gnuplot(clrplot,gnuplot_out_readstream,out_handle);
    for (int i=0;i<s;++i){
      if (v[i].type==_STRNG)
	fprintf(stream,"%s\n",v[i]._STRNGptr->c_str());
    }
    win9x_gnuplot(stream);
    return 1;
  }
  static const char _gnuplot_s []="gnuplot";
  static define_unary_function_eval (__gnuplot,&giac::_gnuplot,_gnuplot_s);
  define_unary_function_ptr5( at_gnuplot ,alias_at_gnuplot,&__gnuplot,0,true);
#endif

  // 3-d functions that must be declared in plot.cc for plot_sommets definition

  // args=normal,point
  gen _hyperplan(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    return symbolic(at_hyperplan,args);
  }
  static const char _hyperplan_s []="hyperplan";
  static define_unary_function_eval (__hyperplan,&giac::_hyperplan,_hyperplan_s);
  define_unary_function_ptr5( at_hyperplan ,alias_at_hyperplan,&__hyperplan,0,true);

  // args=center,radius
  gen _hypersphere(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()<2)
      return gensizeerr(contextptr);
    return symbolic(at_hypersphere,args);
  }
  static const char _hypersphere_s []="hypersphere";
  static define_unary_function_eval (__hypersphere,&giac::_hypersphere,_hypersphere_s);
  define_unary_function_ptr5( at_hypersphere ,alias_at_hypersphere,&__hypersphere,0,true);

  gen hypersurface(const gen & args,const gen & equation,const gen & vars){
    return _hypersurface(gen(makevecteur(args,equation,vars),_GROUP__VECT),context0);
  }
  // Format of an hypersurface is
  // pnt_vect[ [parametric_point],[var1,var2],[min1,min2],[max1,max2],values ]
  // or pnt_vect[ undef,[x,y,z],[xmin,ymin,zmin], [xmax,ymax,zmax], values ]
  // cartesian equation
  // variables
  gen _hypersurface(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return symbolic(at_hypersurface,args);
  }
  static const char _hypersurface_s []="hypersurface";
  static define_unary_function_eval (__hypersurface,&giac::_hypersurface,_hypersurface_s);
  define_unary_function_ptr5( at_hypersurface ,alias_at_hypersurface,&__hypersurface,0,true);

  // 0 text, 2 2d, 3 3d
  int graph_output_type(const giac::gen & g){
    if (g.type==giac::_VECT && !g._VECTptr->empty())
      return graph_output_type(g._VECTptr->back());
    if (g.is_symb_of_sommet(at_animation))
      return graph_output_type(g._SYMBptr->feuille);
    if (g.is_symb_of_sommet(giac::at_pnt)){
      return giac::is3d(g)?3:2;
    }
    return 0;
  }

  gen _animation(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return symbolic(at_animation,args);
  }
  static const char _animation_s []="animation";
  static define_unary_function_eval (__animation,&giac::_animation,_animation_s);
  define_unary_function_ptr5( at_animation ,alias_at_animation,&__animation,0,true);

  gen get_animation_pnt(const gen & g,int pos){
    gen & f =g._SYMBptr->feuille;
    gen fi=f;
    if (f.type==_VECT){
      vecteur v=*f._VECTptr;
      int s=v.size();
      if (s){
	if (v[0].type==_INT_){
	  int n=absint(v[0].val);
	  if (!n)
	    n=1;
	  pos = pos/n;
	  if (s==2){ 
	    if (v[1].type==_VECT){
	      v=*v[1]._VECTptr;
	      s=v.size();
	    }
	  }
	  else {
	    v.erase(v.begin());
	    --s;
	  }
	}
	pos=pos%s;
	if (pos<0)
	  pos+=s;
	fi=v[pos];
      }
    }
    return fi;
  }

  int animations(const gen & g){
    if (g.is_symb_of_sommet(at_animation)){
      gen & f =g._SYMBptr->feuille;
      if (f.type!=_VECT)
	return 1;
      return f._VECTptr->size();
    }
    if (g.type!=_VECT)
      return 0;
    int res=0,tmp;
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it){
      tmp=animations(*it);
      if (tmp>res)
	res=tmp;
    }
    return res;
  }

  // 2-d unit_vector
  gen _Ox_2d_unit_vector(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v=makevecteur(_point(makevecteur(0,0),contextptr),_point(makevecteur(1,0),contextptr));
    if (args.type==_VECT)
      v=mergevecteur(v,*args._VECTptr);
    else
      v.push_back(args);
    return _vector(gen(v,_SEQ__VECT),contextptr);
  }
  static const char _Ox_2d_unit_vector_s []="Ox_2d_unit_vector";
  static define_unary_function_eval (__Ox_2d_unit_vector,&giac::_Ox_2d_unit_vector,_Ox_2d_unit_vector_s);
  define_unary_function_ptr5( at_Ox_2d_unit_vector ,alias_at_Ox_2d_unit_vector,&__Ox_2d_unit_vector,0,true);

  // 2-d unit_vector
  gen _Oy_2d_unit_vector(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v=makevecteur(_point(makevecteur(0,0),contextptr),_point(makevecteur(0,1),contextptr));
    if (args.type==_VECT)
      v=mergevecteur(v,*args._VECTptr);
    else
      v.push_back(args);
    return _vector(gen(v,_SEQ__VECT),contextptr);
  }
  static const char _Oy_2d_unit_vector_s []="Oy_2d_unit_vector";
  static define_unary_function_eval (__Oy_2d_unit_vector,&giac::_Oy_2d_unit_vector,_Oy_2d_unit_vector_s);
  define_unary_function_ptr5( at_Oy_2d_unit_vector ,alias_at_Oy_2d_unit_vector,&__Oy_2d_unit_vector,0,true);

  // 2-d frame
  gen _frame_2d(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v=makevecteur(_point(makevecteur(0,0),contextptr),_point(makevecteur(1,0),contextptr));
    if (args.type==_VECT)
      v=mergevecteur(v,*args._VECTptr);
    else
      v.push_back(args);
    vecteur res(1,_vector(gen(v,_SEQ__VECT),contextptr));
    v=makevecteur(_point(makevecteur(0,0),contextptr),_point(makevecteur(0,1),contextptr));
    if (args.type==_VECT)
      v=mergevecteur(v,*args._VECTptr);
    else
      v.push_back(args);
    res.push_back(_vector(gen(v,_SEQ__VECT),contextptr));
    return gen(res,_SEQ__VECT);
  }
  static const char _frame_2d_s []="frame_2d";
  static define_unary_function_eval (__frame_2d,&giac::_frame_2d,_frame_2d_s);
  define_unary_function_ptr5( at_frame_2d ,alias_at_frame_2d,&__frame_2d,0,true);

  // 3-d unit_vector
  gen _Ox_3d_unit_vector(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v=makevecteur(_point(makevecteur(0,0,0),contextptr),_point(makevecteur(1,0,0),contextptr));
    if (args.type==_VECT)
      v=mergevecteur(v,*args._VECTptr);
    else
      v.push_back(args);
    return _vector(gen(v,_SEQ__VECT),contextptr);
  }
  static const char _Ox_3d_unit_vector_s []="Ox_3d_unit_vector";
  static define_unary_function_eval (__Ox_3d_unit_vector,&giac::_Ox_3d_unit_vector,_Ox_3d_unit_vector_s);
  define_unary_function_ptr5( at_Ox_3d_unit_vector ,alias_at_Ox_3d_unit_vector,&__Ox_3d_unit_vector,0,true);

  // 3-d unit_vector
  gen _Oy_3d_unit_vector(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v=makevecteur(_point(makevecteur(0,0,0),contextptr),_point(makevecteur(0,1,0),contextptr));
    if (args.type==_VECT)
      v=mergevecteur(v,*args._VECTptr);
    else
      v.push_back(args);
    return _vector(gen(v,_SEQ__VECT),contextptr);
  }
  static const char _Oy_3d_unit_vector_s []="Oy_3d_unit_vector";
  static define_unary_function_eval (__Oy_3d_unit_vector,&giac::_Oy_3d_unit_vector,_Oy_3d_unit_vector_s);
  define_unary_function_ptr5( at_Oy_3d_unit_vector ,alias_at_Oy_3d_unit_vector,&__Oy_3d_unit_vector,0,true);

  // 3-d unit_vector
  gen _Oz_3d_unit_vector(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v=makevecteur(_point(makevecteur(0,0,0),contextptr),_point(makevecteur(0,0,1),contextptr));
    if (args.type==_VECT)
      v=mergevecteur(v,*args._VECTptr);
    else
      v.push_back(args);
    return _vector(gen(v,_SEQ__VECT),contextptr);
  }
  static const char _Oz_3d_unit_vector_s []="Oz_3d_unit_vector";
  static define_unary_function_eval (__Oz_3d_unit_vector,&giac::_Oz_3d_unit_vector,_Oz_3d_unit_vector_s);
  define_unary_function_ptr5( at_Oz_3d_unit_vector ,alias_at_Oz_3d_unit_vector,&__Oz_3d_unit_vector,0,true);

  // 3-d frame
  gen _frame_3d(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur v=makevecteur(_point(makevecteur(0,0,0),contextptr),_point(makevecteur(1,0,0),contextptr));
    vecteur res(1,_demi_droite(gen(v,_SEQ__VECT),contextptr));
    v.push_back(symb_equal(at_display,131073));
    v.push_back(symb_equal(at_legende,string2gen("x",false)));
    res.push_back(_vector(gen(v,_SEQ__VECT),contextptr));
    v=makevecteur(_point(makevecteur(0,0,0),contextptr),_point(makevecteur(0,1,0),contextptr));
    res.push_back(_demi_droite(gen(v,_SEQ__VECT),contextptr));
    v.push_back(symb_equal(at_display,131074));
    v.push_back(symb_equal(at_legende,string2gen("y",false)));
    res.push_back(_vector(gen(v,_SEQ__VECT),contextptr));
    v=makevecteur(_point(makevecteur(0,0,0),contextptr),_point(makevecteur(0,0,1),contextptr));
    res.push_back(_demi_droite(gen(v,_SEQ__VECT),contextptr));
    v.push_back(symb_equal(at_display,131076));
    v.push_back(symb_equal(at_legende,string2gen("z",false)));
    res.push_back(_vector(gen(v,_SEQ__VECT),contextptr));
    return gen(res,_SEQ__VECT);
  }
  static const char _frame_3d_s []="frame_3d";
  static define_unary_function_eval (__frame_3d,&giac::_frame_3d,_frame_3d_s);
  define_unary_function_ptr5( at_frame_3d ,alias_at_frame_3d,&__frame_3d,0,true);

  // moved from plot3d.cc for implicittex_plot_sommets_alias
  gen _plot3d(const gen & g,const context * contextptr){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<2)
      return symbolic(at_plot3d,g);
    vecteur v=*g._VECTptr;
    if (v.size()<3)
      v.push_back(v__IDNT_e);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    if (s<3)
      return gendimerr(contextptr);
    v=vecteur(v.begin(),v.begin()+s);
    if (v[0].type==_VECT){
      if (v[0]._VECTptr->size()!=3)
	return gendimerr(contextptr);
      double tmin,tmax,smin,smax;
      if (v[1].is_symb_of_sommet(at_interval)){
	if (!chk_double_interval(v[1],tmin,tmax,contextptr) || !chk_double_interval(v[2],smin,smax,contextptr))
	  return gensizeerr(contextptr);
	vecteur vars(makevecteur(s__IDNT_e,t__IDNT_e));
	return plotparam3d(v[0](gen(vars,_SEQ__VECT),contextptr),vars,gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,gnuplot_zmin,gnuplot_xmax,tmin,tmax,smin,smax,true,autoscale,attributs,gnuplot_tstep,gnuplot_tstep,undef,vecteur(0),contextptr);
      }
      else {
	if (!readrange(v[1],gnuplot_tmin,gnuplot_tmax,v[1],tmin,tmax,contextptr) || !readrange(v[2],gnuplot_tmin,gnuplot_tmax,v[2],smin,smax,contextptr))
	  return gensizeerr(contextptr);
	return plotparam3d(v[0],makevecteur(v[1],v[2]),gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,gnuplot_zmin,gnuplot_xmax,tmin,tmax,smin,smax,true,autoscale,attributs,gnuplot_tstep,gnuplot_tstep,undef,vecteur(0),contextptr);
      }
    }
    else {
      double xmin,xmax,ymin,ymax;
      if (v[1].is_symb_of_sommet(at_interval)){
	if (!chk_double_interval(v[1],xmin,xmax,contextptr) || !chk_double_interval(v[2],ymin,ymax,contextptr))
	  return gensizeerr(contextptr);
	gen vars(makevecteur(x__IDNT_e,y__IDNT_e),_SEQ__VECT);
	return plotfunc(v[0](vars,contextptr),vars,attributs,false,xmin,xmax,ymin,ymax,gnuplot_zmin,gnuplot_zmax,gnuplot_pixels_per_eval,0,false,contextptr);
      }
      else {
	if (!readrange(v[1],gnuplot_xmin,gnuplot_xmax,v[1],xmin,xmax,contextptr) || !readrange(v[2],gnuplot_ymin,gnuplot_ymax,v[2],ymin,ymax,contextptr))
	  return gensizeerr(contextptr);
	return plotfunc(v[0],makevecteur(v[1],v[2]),attributs,false,xmin,xmax,ymin,ymax,gnuplot_zmin,gnuplot_zmax,gnuplot_pixels_per_eval,0,false,contextptr);
      }
    }
  }
  static const char _plot3d_s []="plot3d";
  static define_unary_function_eval (__plot3d,&_plot3d,_plot3d_s);
  define_unary_function_ptr5( at_plot3d ,alias_at_plot3d,&__plot3d,0,true);

  static const char _graphe3d_s []="graphe3d";
  static define_unary_function_eval (__graphe3d,&_plot3d,_graphe3d_s);
  define_unary_function_ptr5( at_graphe3d ,alias_at_graphe3d,&__graphe3d,0,true);

  // moved from prog.cc, for nosplit_polygon_function_alias
  gen _inter(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur attributs(1,default_color(contextptr));
    vecteur v(seq2vecteur(args));
    int s=read_attributs(v,attributs,contextptr);
    if (s!=2 && s!=3)
      return gendimerr(contextptr);
    gen a=v[0],b=v[1];
    vecteur ww=inter(a,b,contextptr); 
    if (ww.size()==1 && ww.front().is_symb_of_sommet(at_inter)){
      // try equations (for ggb)
      if (a.is_symb_of_sommet(at_equal) ||b.is_symb_of_sommet(at_equal) ){
	vecteur syst=makevecteur(remove_equal(a),remove_equal(b));
	vecteur v=lidnt(syst);
	if (v.size()!=2)
	  return gensizeerr(contextptr);
	string v0s=v[0].print(contextptr);
	if (v0s[v0s.size()-1]!='x')
	  swapgen(v[0],v[1]);
#ifndef NO_STDEXCEPT
	try {
#endif
	  vecteur sol=solve(syst,v,0,contextptr); 
	  iterateur it=sol.begin(),itend=sol.end();
	  for (;it!=itend;++it){
	    *it=change_subtype(*it,_GGB__VECT);
	  }
	  return sol;
#ifndef NO_STDEXCEPT
	}
	catch (std::runtime_error &){
	  return makevecteur(symbolic(at_inter,makesequence(a,b)));
	}
#endif	
      }
      gen eq=a-b;
      gen x=ggb_var(eq);
      vecteur sol,res;
#ifndef NO_STDEXCEPT
      try {
#endif
	sol=solve(eq,*x._IDNTptr,0,contextptr); 
	iterateur it=sol.begin(),itend=sol.end();
	for (;it!=itend;++it){
	  *it=gen(makevecteur(*it,normal(subst(a,x,*it,false,contextptr),contextptr)),_GGB__VECT);
	}
	return sol;
#ifndef NO_STDEXCEPT
      }
      catch (std::runtime_error &){
	return makevecteur(symbolic(at_inter,makesequence(a,b)));
      }
#endif
    }
    vecteur w=remove_multiples(ww);
    if (s==3 && !w.empty()){
      int ws=w.size();
      a=w[0];
      gen c=v[2];
      gen d=distance2pp(a,c,contextptr);
      for (int i=1;i<ws;++i){
	gen dcur=distance2pp(w[i],c,contextptr);
	if (is_strictly_greater(d,dcur,contextptr)){
	  d=dcur;
	  a=w[i];
	}
      }
    }
    else
      a=gen(w,_GROUP__VECT);
    return put_attributs(a,attributs,contextptr);
  }
  static const char _inter_s []="inter";
  static define_unary_function_eval (__inter,&_inter,_inter_s);
  define_unary_function_ptr5( at_inter ,alias_at_inter,&__inter,0,true);

  gen _Bezier(const gen & args,GIAC_CONTEXT){
    return symbolic(at_Bezier,args);
  }
  static const char _Bezier_s []="Bezier";
  static define_unary_function_eval (__Bezier,&_Bezier,_Bezier_s);
  define_unary_function_ptr5( at_Bezier ,alias_at_Bezier,&__Bezier,0,true);

  gen _bezier(const gen & args,GIAC_CONTEXT){
    if (is_undef(args)) return args;
    vecteur v(gen2vecteur(args));
    if (v.empty())
      return gensizeerr(contextptr);
    vecteur attributs(1,default_color(contextptr));
    int s=read_attributs(v,attributs,contextptr);
    for (int i=0;i<s;++i)
      v[i]=remove_at_pnt(v[i]);
    return pnt_attrib(symbolic(at_Bezier,gen(vecteur(v.begin(),v.begin()+s),_GROUP__VECT)),attributs,contextptr);
  }
  static const char _bezier_s []="bezier";
  static define_unary_function_eval (__bezier,&_bezier,_bezier_s);
  define_unary_function_ptr5( at_bezier ,alias_at_bezier,&__bezier,0,true);

#if defined(GIAC_GENERIC_CONSTANTS) || (defined(VISUALC) && !defined(RTOS_THREADX)) || defined(__x86_64__)
  unary_function_ptr plot_sommets[]={*at_pnt,*at_parameter,*at_cercle,*at_curve,*at_animation,0};
  unary_function_ptr not_point_sommets[]={*at_cercle,*at_curve,*at_hyperplan,*at_hypersphere,*at_hypersurface,0};
  unary_function_ptr notexprint_plot_sommets[]={*at_funcplot,*at_paramplot,*at_polarplot,*at_implicitplot,*at_contourplot,*at_odeplot,*at_interactive_odeplot,*at_fieldplot,*at_seqplot,*at_ellipse,*at_hyperbole,*at_parabole,0};
  unary_function_ptr implicittex_plot_sommets[]={*at_plot,*at_plot3d,*at_plotfunc,*at_plotparam,*at_plotpolar,*at_plotimplicit,*at_plotcontour,*at_DrawInv,*at_DrawFunc,*at_DrawParm,*at_DrawPol,*at_DrwCtour,*at_plotode,*at_plotfield,*at_interactive_plotode,*at_plotseq,*at_Graph,0};
  unary_function_ptr point_sommet_tab_op[]={*at_point,*at_element,*at_inter_unique,*at_centre,*at_isobarycentre,*at_barycentre,0};
  unary_function_ptr nosplit_polygon_function[]={*at_inter_unique,*at_inter,*at_distanceat,*at_distanceatraw,*at_rotation,*at_projection,*at_symetrie,*at_polaire_reciproque,*at_areaat,*at_areaatraw,*at_perimeterat,*at_perimeteratraw,*at_slopeat,*at_slopeatraw,*at_tangent,*at_cercle,0}; 
  unary_function_ptr measure_functions[]={*at_angleat,*at_angleatraw,*at_areaat,*at_areaatraw,*at_perimeterat,*at_perimeteratraw,*at_slopeat,*at_slopeatraw,*at_distanceat,*at_distanceatraw,0};
  unary_function_ptr transformation_functions[]={*at_projection,*at_rotation,*at_translation,*at_homothetie,*at_similitude,*at_inversion,*at_symetrie,*at_polaire_reciproque,0};

#else
  const unsigned long plot_sommets_alias[]={(long)&__pnt,(long)&__parameter,(long)&__cercle,(long)&__curve,(long)&__animation,0};
  const unary_function_ptr * const plot_sommets = (const unary_function_ptr *) plot_sommets_alias;

  const unsigned long not_point_sommets_alias[]={(long)&__cercle,(long)&__curve,(long)&__hyperplan,(long)&__hypersphere,(long)&__hypersurface,0};
  const unary_function_ptr * const not_point_sommets = (const unary_function_ptr *) plot_sommets_alias;

  const unsigned long notexprint_plot_sommets_alias[]={(long)&__funcplot,(long)&__paramplot,(long)&__polarplot,(long)&__implicitplot,(long)&__contourplot,(long)&__odeplot,(long)&__interactive_odeplot,(long)&__fieldplot,(long)&__seqplot,(long)&__ellipse,(long)&__hyperbole,(long)&__parabole,0};
  const unary_function_ptr * const notexprint_plot_sommets = (const unary_function_ptr *) notexprint_plot_sommets_alias;

  const unsigned long implicittex_plot_sommets_alias[]={(long)&__plot,(long)&__plot3d,(long)&__plotfunc,(long)&__plotparam,(long)&__plotpolar,(long)&__plotimplicit,(long)&__plotcontour,(long)&__DrawInv,(long)&__DrawFunc,(long)&__DrawParm,(long)&__DrawPol,(long)&__DrwCtour,(long)&__plotode,(long)&__plotfield,(long)&__interactive_plotode,(long)&__plotseq,(long)&__Graph,0};
  const unary_function_ptr * const implicittex_plot_sommets = (const unary_function_ptr *) implicittex_plot_sommets_alias;

  const unsigned long point_sommet_tab_op_alias[]={(long)&__point,(long)&__element,(long)&__inter_unique,(long)&__centre,(long)&__isobarycentre,(long)&__barycentre,0};
  const unary_function_ptr * const point_sommet_tab_op = (const unary_function_ptr *) point_sommet_tab_op_alias;

  const unsigned long nosplit_polygon_function_alias[]={(long)&__inter_unique,(long)&__inter,(long)&__distanceatraw,(long)&__distanceat,(long)&__rotation,(long)&__projection,(long)&__symetrie,(long)&__polaire_reciproque,(long)&__areaat,(long)&__areaatraw,(long)&__perimeterat,(long)&__perimeteratraw,(long)&__slopeat,(long)&__slopeatraw,(long)&__tangent,(long)&__cercle,0};
  const unary_function_ptr * const nosplit_polygon_function = (const unary_function_ptr *) nosplit_polygon_function_alias;

  const unsigned long measure_functions_alias[]={(long)&__angleat,(long)&__angleatraw,(long)&__areaat,(long)&__areaatraw,(long)&__perimeterat,(long)&__perimeteratraw,(long)&__slopeat,(long)&__slopeatraw,(long)&__distanceat,(long)&__distanceatraw,0};
  const unary_function_ptr * const measure_functions = (const unary_function_ptr *) measure_functions_alias;

  const unsigned long transformation_functions_alias[]={(long)&__projection,(long)&__rotation,(long)&__translation,(long)&__homothetie,(long)&__similitude,(long)&__inversion,(long)&__symetrie,(long)&__polaire_reciproque,0};
  const unary_function_ptr * const transformation_functions = (const unary_function_ptr *) transformation_functions_alias;
#endif

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
