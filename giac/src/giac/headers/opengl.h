#ifndef _OPENGL_H
#define _OPENGL_H
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#ifndef IN_GIAC
#include <giac/giac.h>
#else
#include "giac.h"
#endif
#include <fstream>
#include <string>
#include <stdio.h>

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  class Xcas_config_type {
  public:
    int fontsize;
    int help_fontsize;
    double window_xmin,window_xmax,window_ymin,window_ymax,window_zmin,window_zmax;
    bool ortho,autoscale;
#ifdef IPAQ
    Xcas_config_type():fontsize(14),help_fontsize(14),window_xmin(-4),window_xmax(4),window_ymin(-5),window_ymax(5),window_zmin(-5),window_zmax(5),ortho(false),autoscale(true) {};
#else
    Xcas_config_type():fontsize(14),help_fontsize(14),window_xmin(-5),window_xmax(5),window_ymin(-5),window_ymax(5),window_zmin(-5),window_zmax(5),ortho(false),autoscale(true) {};
#endif
    Xcas_config_type(int f,int hf,double x,double X,double y,double Y,bool _ortho):fontsize(f),help_fontsize(hf),window_xmin(x),window_xmax(X),window_ymin(y),window_ymax(Y),window_zmin(-5),window_zmax(5),ortho(_ortho),autoscale(true) {}
    Xcas_config_type(int f,int hf,double x,double X,double y,double Y,double z,double Z,bool _ortho):fontsize(f),help_fontsize(hf),window_xmin(x),window_xmax(X),window_ymin(y),window_ymax(Y),window_zmin(z),window_zmax(Z),ortho(_ortho),autoscale(true) {}
  };
  extern Xcas_config_type Xcas_config;

  std::string printstring(const gen & g,GIAC_CONTEXT);
  std::string print_DOUBLE_(double d);

  struct window_xyz { 
    double xmin,xmax,ymin,ymax,zmin,zmax; 
    window_xyz():xmin(-5),xmax(5),ymin(-5),ymax(5),zmin(-5),zmax(5) {};
    window_xyz(double x,double X,double y,double Y,double z,double Z):xmin(x),xmax(X),ymin(y),ymax(Y),zmin(z),zmax(Z) {};
  };

  // quaternion struct for more intuitive rotations
  struct quaternion_double {
    double w,x,y,z;
    quaternion_double():w(1),x(0),y(0),z(0) {};
    quaternion_double(double theta_x,double theta_y,double theta_z);
    quaternion_double(double _w,double _x,double _y,double _z):w(_w),x(_x),y(_y),z(_z) {};
    double norm2() const { return w*w+x*x+y*y+z*z;}
  };

  quaternion_double operator * (const quaternion_double & q,const quaternion_double & q2);

  void get_axis_angle_deg(const quaternion_double & q,double &x,double &y,double & z, double &theta); // q must be a quaternion of unit norm, theta is in deg

  // Euler angle in degrees
  quaternion_double euler_deg_to_quaternion_double(double a,double b,double c);

  std::ostream & operator << (std::ostream & os,const quaternion_double & q);


  // translate giac GL constant to open GL constant
  unsigned gl_translate(unsigned i);
  // utilities for matrix 4x4 represented as a double[16] 
  // written in columns
  void mult4(double * colmat,double * vect,double * res);
  void mult4(double * colmat,float * vect,double * res);
  void mult4(double * c,double k,double * res);
  double det4(double * c);
  void inv4(double * c,double * res);
  // return in i and j the distance to the BOTTOM LEFT of the window
  // use window()->h()-j for the FLTK coordinates in this window
  void dim32dim2(double * view,double * proj,double * model,double x0,double y0,double z0,double & i,double & j,double & depth);
  // quaternion for the rotation of axis (x,y,z) angle theta
  quaternion_double rotation_2_quaternion_double(double x, double y, double z,double theta);

  class Opengl {
  public:
    int push_i,push_j,current_i,current_j,cursor_point_type; // position of mouse push/drag
  protected:
    int w_,h_,labelsize_;
    double push_depth,current_depth;
    bool pushed; // true when mode==0 and push has occured
    bool in_area; // true if the mouse is in the area, updated by handle
    int mode; // 0 pointer, 1 1-arg, 2 2-args, etc.
    // plot_tmp=function_tmp(args_tmp) or function_final(args_tmp)
    // depends whether args.tmp.size()==mode
    giac::gen function_tmp,function_final,args_push; 
    giac::vecteur args_tmp; // WARNING should only contain numeric value
    unsigned args_tmp_push_size;
  public:
    const giac::context * contextptr;
    std::vector<std::string> args_help;
    bool no_handle; // disable mouse handling
    bool show_mouse_on_object; // FL_MOVE always handled or not
    unsigned display_mode ; 
    // bit0=1 plot_instructions, bit1=1 animations_instruction
    // bit2=1 glFrustum/glOrtho, bit3=1 GL_LIGHTING, bit4=1 GL_FLAT, 
    // bit5=GL_BLEND, bit6=trace, bit7=1 move frame disabled
    // bit8=1 framebox, bit9=1 triedre
    // bit10=1 logplot 2d x, bit11=1 logplot 2d y, bit12=1 reserved for logplot 3d z
    double window_xmin,window_xmax,window_ymin,window_ymax,window_zmin,window_zmax;
    std::vector<window_xyz> history;
    int history_pos;
    quaternion_double q;
    int legende_size;
    double ylegende;
    // Fl_Tile * mouse_param_group ;
    std::string title,x_axis_name,x_axis_unit,y_axis_name,y_axis_unit,z_axis_name,z_axis_unit,fcnfield,fcnvars;
    int npixels; // max # of pixels distance for click
    int show_axes,show_names;
    giac::vecteur plot_instructions,animation_instructions,trace_instructions ;
    double animation_dt; // rate for animated plot
    bool paused;
    bool twodim;
    double ipos,jpos,depthpos;
    //struct timeval animation_last; // clock value at last display
    int animation_instructions_pos;
    int rotanim_type,rotanim_danim,rotanim_nstep;
    double rotanim_rx,rotanim_ry,rotanim_rz,rotanim_tstep;
    int last_event;
    double x_tick,y_tick,z_tick;
    int couleur; // used for new point creation in geometry
    bool approx; // exact or approx click mouse?
    std::vector<int> selected; // all items selected in plot_instructions
    giac::gen drag_original_value,drag_name;
    int hp_pos; // Position in hp for modification
    bool moving,moving_frame;
    // 3-d light information
    float light_x[8],light_y[8],light_z[8],light_w[8];
    float light_diffuse_r[8],light_diffuse_g[8],light_diffuse_b[8],light_diffuse_a[8];
    float light_specular_r[8],light_specular_g[8],light_specular_b[8],light_specular_a[8];
    float light_ambient_r[8],light_ambient_g[8],light_ambient_b[8],light_ambient_a[8];
    float light_spot_x[8],light_spot_y[8],light_spot_z[8],light_spot_w[8];
    float light_spot_exponent[8],light_spot_cutoff[8];
    float light_0[8],light_1[8],light_2[8];
    bool light_on[8];
    int ntheta,nphi; // default discretization params for sphere drawing
    //std::pair<Fl_Image *,Fl_Image *> * background_image; // 2-d only
    int x_axis_color,y_axis_color,z_axis_color;
    Opengl(int w__,int h__,double xmin,double xmax,double ymin,double ymax,double zmin,double zmax,double ortho);
    Opengl(int w__,int h__);
    double find_eps(); // find value of a small real wrt the current graph
    void update_infos(const giac::gen & g);
    void autoname_plus_plus();
    virtual void zoom(double d);
    virtual void zoomx(double d,bool round=false);
    virtual void zoomy(double d,bool round=false);
    virtual void zoomz(double d,bool round=false);
    virtual void orthonormalize();
    virtual void autoscale(bool fullview=false);
    virtual void up(double d);
    virtual void down(double d);
    virtual void up_z(double d);
    virtual void down_z(double d);
    virtual void left(double d);
    virtual void right(double d);
    virtual void move_cfg(int i); // moves forward/backward in cfg history
    virtual void push_cfg(); // save current config
    virtual void clear_cfg(); // reset history config
    virtual void reset_light(unsigned i);
    virtual void set_axes(int b);
    void copy(const Opengl & gr);
    virtual int handle(int);
    virtual int in_handle(int);
    virtual void redraw();
    virtual void labelsize(int);
    virtual int labelsize() const;
    virtual int x() const;
    virtual int y() const;
    virtual int w() const;
    virtual int h() const;
    int handle_mouse(int);
    int handle_keyboard(int);
    giac::vecteur selection2vecteur(const std::vector<int> & v);
    void set_mode(const giac::gen & f_tmp,const giac::gen & f_final,int m);
    virtual void find_xyz(double i,double j,double depth,double & x,double & y,double & z);
    virtual ~Opengl();
    std::string current_config();
    void adjust_cursor_point_type();
    void glRasterPos3d(double d1,double d2,double d3);
    void resize(int w__,int h__){w_=w__; h_=h__; }
  };


  class Opengl3d:public Opengl {
  public:
    Opengl3d(int w__,int h__);
    virtual ~Opengl3d();
    double theta_z,theta_x,theta_y; // rotations
    double delta_theta; // rotation increment
    int draw_mode; // for sphere drawing
    // void * glcontext;
    // save values of the projection and modelview matrices
    double proj[16],model[16],proj_inv[16],model_inv[16]; 
    double view[4];
    int dragi,dragj;
    bool push_in_area;
    double depth;
    bool below_depth_hidden;
    virtual void draw();
    virtual void orthonormalize();
    void display(); 
    // internally callled by draw, maybe multiple times when printing
    // virtual int in_handle(int event);
    void indraw(const giac::vecteur & v);
    void indraw(const giac::gen & g);
    void legende_draw(const giac::gen & g,const std::string & s,int mode);
    void draw_string(const std::string & s);
    // i,j,z -> x,y
    virtual void find_xyz(double i,double j,double depth,double & x,double & y,double & z)  ;
    // x,y,z -> FLTK coordinates i,j
    void find_ij(double x,double y,double z,double & i,double & j,double & depth) ;
    void current_normal(double & a,double &b,double &c) ;
    void normal2plan(double & a,double &b,double &c);
  };

  struct Opengl3dcfg {
    int webglhandle;
    int w,h;
    double window_xmin,window_xmax,window_ymin,window_ymax,window_zmin,window_zmax;
    quaternion_double q;
    double theta_z,theta_x,theta_y; // rotations
    giac::vecteur plot_instructions;
    bool twodim;
    Opengl3dcfg(const Opengl3d * ptr);
    void load(Opengl3d * ptr) const;
    void store(const Opengl3d * ptr) ;
  };

  extern Opengl3d * openglptr;
  extern std::vector<Opengl3dcfg> v3d;
  void sdl_loop();
  int giac_renderer(const char * ch);
  int giac_gen_renderer(const gen & g,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GRAPH3D_H
