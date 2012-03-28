var yGj='100%',zGj='com.google.gwt.animation.client.',CGj='com.google.gwt.layout.client.',DGj='com.google.gwt.touch.client.',kGj='display',FGj='geogebra.web.gui.app.',tGj='onresize',uGj='zoom';function Bb(a){a.c=new Rb(a)}
function Cb(a){if(!a.f){return}a.o=a.g;a.e=null;a.f=false;a.g=false;if(cJ(a.i)){a.i.Qc();a.i=null}a.Kc()}
function Db(a){return (1+_Nh(3.141592653589793+a*3.141592653589793))/2}
function Eb(a,b){return a.f&&a.j==b}
function Fb(a){a.Nc(Db(0))}
function Gb(a,b,c){Hb(a,b,Ai(),c)}
function Hb(a,b,c,d){Cb(a);a.f=true;a.g=false;a.d=b;a.n=c;a.e=d;++a.j;a.c.Oc(Ai())}
function Ib(a,b){var c,d,e;c=a.j;d=b>=a.n+a.d;if(a.g&&!d){e=(b-a.n)/a.d;a.Nc(Db(e));return Eb(a,c)}if(!a.g&&b>=a.n){a.g=true;a.Mc();if(!Eb(a,c)){return false}}if(d){a.f=false;a.g=false;a.Lc();return false}return true}
function Jb(){Kb.call(this,Xb())}
function Kb(a){wb.call(this);Bb(this);this.k=a}
function rb(){}
_=rb.prototype=new sb;_.gC=function Lb(){return zJ};_.Kc=function Mb(){this.o&&this.Lc()};_.Lc=function Nb(){this.Nc(Db(1))};_.Mc=function Ob(){Fb(this)};_.d=-1;_.e=null;_.f=false;_.g=false;_.i=null;_.j=-1;_.k=null;_.n=-1;_.o=false;function Qb(){}
function Rb(a){this.a=a;wb.call(this);Qb()}
function Pb(){}
_=Rb.prototype=Pb.prototype=new sb;_.Oc=function Sb(a){Ib(this.a,a)?(this.a.i=this.a.k.Pc(this.a.c,this.a.e)):(this.a.i=null)};_.gC=function Tb(){return sJ};_.a=null;function Vb(){}
function Wb(){wb.call(this);Vb()}
function Xb(){return dc(),cc}
function Ub(){}
_=Ub.prototype=new sb;_.gC=function Yb(){return yJ};function $b(){}
function _b(){wb.call(this);$b()}
function Zb(){}
_=Zb.prototype=new sb;_.gC=function ac(){return tJ};function dc(){dc=xxj;var a;{a=new lc;YI(a,6)&&(UI(a,6).Rc()||(a=new lc));cc=a}}
function ec(){}
function fc(){Wb.call(this);ec()}
function bc(){}
_=bc.prototype=new Ub;_.gC=function gc(){return xJ};_.cM={6:1};var cc=null;function ic(a){a.a=new b8b;a.b=new Gc(a)}
function jc(a,b){a.a.kl(b);a.a.ml()==0&&uc(a.b)}
function kc(a){var b,c,d,e,f,g;b=EI(Tqb,{8:1,515:1,538:1},7,a.a.ml(),0);b=UI(a.a.ol(b),8);c=new zi;for(e=b,f=0,g=e.length;f<g;++f){d=e[f];a.a.kl(d);Lc(d).Oc(yi(c))}a.a.ml()>0&&vc(a.b,gOh(5,16-xi(c)))}
function lc(){fc.call(this);ic(this)}
function hc(){}
_=lc.prototype=hc.prototype=new bc;_.gC=function mc(){return wJ};_.Rc=function nc(){return true};_.Pc=function oc(a,b){var c;c=new Mc(this,a);this.a.fl(c);this.a.ml()==1&&vc(this.b,16);return c};_.cM={6:1};function Fc(){}
function Gc(a){sc();this.a=a;xc.call(this);Fc()}
function pc(){}
_=Gc.prototype=pc.prototype=new qc;_.gC=function Hc(){return uJ};_.Tc=function Ic(){kc(this.a)};_.cM={109:1};_.a=null;function Kc(){}
function Lc(a){return a.a}
function Mc(a,b){this.b=a;_b.call(this);Kc();this.a=b}
function Jc(){}
_=Mc.prototype=Jc.prototype=new Zb;_.Qc=function Nc(){jc(this.b,this)};_.gC=function Oc(){return vJ};_.cM={7:1};_.a=null;_.b=null;_=Sc.prototype;_.Xc=function nd(){throw new FRh};function wd(a,b){return yd(a,b,CB())}
function yd(a,b,c){return $B(Ad(a),c,b)}
function Cd(a){return a.q}
function Ld(a,b){a.q=b}
_=Rc.prototype;_._c=function Pd(){return this};function wi(a){a.a=Ai()}
function xi(a){return Ci(Ai()-a.a)}
function yi(a){return a.a}
function zi(){wb.call(this);wi(this)}
function Ci(a){return a}
function vi(){}
_=zi.prototype=vi.prototype=new sb;_.gC=function Bi(){return JJ};function hj(b,a){return b.join(a)}
function jj(b,a){b[b.length]=a}
function kj(b,a){b.length=a}
_=fl.prototype;_.rd=function wl(a){this.a=tl(this.a,Rl(a));nl(this)};_.sd=function xl(a){this.c=tl(this.c,Rl(a))};_.td=function yl(a,b){ol(a,b)};function Rl(a){return [a,false]}
function Im(a){return a.childNodes}
function Jm(a){return a.firstChild}
function Lm(a){return a.nodeType}
function Om(c,a,b){return c.insertBefore(a,b)}
function Rm(a){var b;b=Nm(a);cJ(b)&&Qm(b,a)}
function Um(a,b){(Ln(),Kn).Ud(a,b)}
function $m(a){return a.clientHeight}
function en(a){return a.offsetHeight||0}
function gn(a){return a.offsetWidth||0}
function kn(a){return a.scrollHeight||0}
function zn(a,b){(Ln(),Kn).je(a,b)}
function An(b,a){b.scrollTop=a}
function Pn(a,b){a.opacity=b}
function fo(a){return a.touches}
function ho(a,b){a.scrollLeft=b}
function ko(a){return a.pageX}
function lo(a){return a.pageY}
_=Jn.prototype;_.je=function Bo(a,b){ho(a,b)};_=Fo.prototype;_.Sd=function Io(a,b,c,d){var e=a.createEvent('HTMLEvents');e.initEvent(b,c,d);return e};_.Ud=function Jo(a,b){a.dispatchEvent(b)};_=Eo.prototype;_.je=function hp(a,b){To(a)&&(b+=nn(a)-_m(a));ho(a,b)};function pp(a,b){a.scrollLeft=b}
_=Do.prototype;_.je=function yp(a,b){To(a)&&(b=-b);pp(a,b)};function Ep(a,b,c,d){return (Ln(),Kn).Sd(a,b,c,d)}
function Jp(a){return Ep(a,rDj,false,false)}
function Kp(a){return Nn((Ln(),Kn,a),FDj)}
function Lp(a){return Nn((Ln(),Kn,a),jGj)}
function Up(a){return $m(cq(a))}
function Vp(a){return _m(cq(a))}
function $p(a){return kn(cq(a))}
function bq(a){return nn(cq(a))}
function Aq(a){return fo((Ln(),Kn,a))}
function Jq(a){Oq(a,GDj)}
function Kq(a){Oq(a,kGj)}
function Lq(a){Oq(a,QBj)}
function Mq(a){Oq(a,xCj)}
function Nq(a){Oq(a,sCj)}
function Oq(a,b){fr(a,b,VBj)}
function Pq(a){Oq(a,LCj)}
function Qq(a){Oq(a,yCj)}
function Rq(a){Oq(a,SBj)}
function Sq(a){return Tq(a,kGj)}
function Tq(a,b){Iq();return Uq(a,b)}
function Uq(b,a){return b[a]}
function Yq(a,b,c){gr(a,GDj,b,c)}
function Zq(a,b){fr(a,kGj,b.le())}
function ar(a,b,c){gr(a,'margin',b,c)}
function br(a,b){Pn((Ln(),Kn,a),b)}
function cr(a,b){fr(a,EDj,b.le())}
function dr(a,b,c){gr(a,'padding',b,c)}
function er(a,b){fr(a,sCj,b.le())}
function ir(a,b,c){gr(a,b,c,(eu(),du))}
function jr(a,b,c){gr(a,LCj,b,c)}
function mr(a,b){fr(a,'zIndex',b+VBj)}
function fs(){fs=xxj;es=new ns(zCj,0);bs=new ss('BLOCK',1);cs=new xs('INLINE',2);ds=new Cs('INLINE_BLOCK',3);as=HI(arb,{515:1,538:1},22,[es,bs,cs,ds])}
function gs(){}
function hs(a,b){fg.call(this,a,b);gs()}
function js(a){fs();return og((Gs(),Fs),a)}
function ks(){fs();return as}
function _r(){}
_=_r.prototype=new $f;_.gC=function is(){return oK};_.cM={22:1,26:1,515:1,525:1,527:1};var as,bs,cs,ds,es;function ms(){}
function ns(a,b){hs.call(this,a,b);ms()}
function ls(){}
_=ns.prototype=ls.prototype=new _r;_.gC=function os(){return kK};_.le=function ps(){return BCj};_.cM={22:1,26:1,515:1,525:1,527:1};function rs(){}
function ss(a,b){hs.call(this,a,b);rs()}
function qs(){}
_=ss.prototype=qs.prototype=new _r;_.gC=function ts(){return lK};_.le=function us(){return lGj};_.cM={22:1,26:1,515:1,525:1,527:1};function ws(){}
function xs(a,b){hs.call(this,a,b);ws()}
function vs(){}
_=xs.prototype=vs.prototype=new _r;_.gC=function ys(){return mK};_.le=function zs(){return 'inline'};_.cM={22:1,26:1,515:1,525:1,527:1};function Bs(){}
function Cs(a,b){hs.call(this,a,b);Bs()}
function As(){}
_=Cs.prototype=As.prototype=new _r;_.gC=function Ds(){return nK};_.le=function Es(){return 'inline-block'};_.cM={22:1,26:1,515:1,525:1,527:1};function Gs(){Gs=xxj;Fs=hg((fs(),as))}
var Fs;function Ns(){Ns=xxj;Ms=new Vs('VISIBLE',0);Ks=new $s(ACj,1);Ls=new dt('SCROLL',2);Js=new it(mGj,3);Is=HI(brb,{515:1,538:1},28,[Ms,Ks,Ls,Js])}
function Os(){}
function Ps(a,b){fg.call(this,a,b);Os()}
function Rs(a){Ns();return og((mt(),lt),a)}
function Ss(){Ns();return Is}
function Hs(){}
_=Hs.prototype=new $f;_.gC=function Qs(){return tK};_.cM={26:1,28:1,515:1,525:1,527:1};var Is,Js,Ks,Ls,Ms;function Us(){}
function Vs(a,b){Ps.call(this,a,b);Us()}
function Ts(){}
_=Vs.prototype=Ts.prototype=new Hs;_.gC=function Ws(){return pK};_.le=function Xs(){return nGj};_.cM={26:1,28:1,515:1,525:1,527:1};function Zs(){}
function $s(a,b){Ps.call(this,a,b);Zs()}
function Ys(){}
_=$s.prototype=Ys.prototype=new Hs;_.gC=function _s(){return qK};_.le=function at(){return CCj};_.cM={26:1,28:1,515:1,525:1,527:1};function ct(){}
function dt(a,b){Ps.call(this,a,b);ct()}
function bt(){}
_=dt.prototype=bt.prototype=new Hs;_.gC=function et(){return rK};_.le=function ft(){return rDj};_.cM={26:1,28:1,515:1,525:1,527:1};function ht(){}
function it(a,b){Ps.call(this,a,b);ht()}
function gt(){}
_=it.prototype=gt.prototype=new Hs;_.gC=function jt(){return sK};_.le=function kt(){return oGj};_.cM={26:1,28:1,515:1,525:1,527:1};function mt(){mt=xxj;lt=hg((Ns(),Is))}
var lt;function tt(){tt=xxj;st=new Bt('STATIC',0);rt=new Gt('RELATIVE',1);pt=new Lt('ABSOLUTE',2);qt=new Qt(pGj,3);ot=HI(crb,{515:1,538:1},29,[st,rt,pt,qt])}
function ut(){}
function vt(a,b){fg.call(this,a,b);ut()}
function xt(a){tt();return og((Ut(),Tt),a)}
function yt(){tt();return ot}
function nt(){}
_=nt.prototype=new $f;_.gC=function wt(){return yK};_.cM={26:1,29:1,515:1,525:1,527:1};var ot,pt,qt,rt,st;function At(){}
function Bt(a,b){vt.call(this,a,b);At()}
function zt(){}
_=Bt.prototype=zt.prototype=new nt;_.gC=function Ct(){return uK};_.le=function Dt(){return 'static'};_.cM={26:1,29:1,515:1,525:1,527:1};function Ft(){}
function Gt(a,b){vt.call(this,a,b);Ft()}
function Et(){}
_=Gt.prototype=Et.prototype=new nt;_.gC=function Ht(){return vK};_.le=function It(){return DDj};_.cM={26:1,29:1,515:1,525:1,527:1};function Kt(){}
function Lt(a,b){vt.call(this,a,b);Kt()}
function Jt(){}
_=Lt.prototype=Jt.prototype=new nt;_.gC=function Mt(){return wK};_.le=function Nt(){return vCj};_.cM={26:1,29:1,515:1,525:1,527:1};function Pt(){}
function Qt(a,b){vt.call(this,a,b);Pt()}
function Ot(){}
_=Qt.prototype=Ot.prototype=new nt;_.gC=function Rt(){return xK};_.le=function St(){return tCj};_.cM={26:1,29:1,515:1,525:1,527:1};function Ut(){Ut=xxj;Tt=hg((tt(),ot))}
var Tt;function dv(b,a){b.cssText=a}
function jv(){jv=xxj;gv=Qe(Ve());hv=Qe(Ve());iv=Qe(Ve());ev=new rv}
function kv(a){jv();var b,c,d;d=null;if(ij(iv)!=0){b=hj(iv,VBj);c=(wv(),vv).pe(b);eJ(iv,a)&&(d=c);kj(iv,0)}if(ij(gv)!=0){b=hj(gv,VBj);c=(wv(),vv).ne(b);eJ(gv,a)&&(d=c);kj(gv,0)}if(ij(hv)!=0){b=hj(hv,VBj);c=(wv(),vv).oe(b);eJ(hv,a)&&(d=c);kj(hv,0)}fv=false;return d}
function lv(a){jv();mv(a,false)}
function mv(a,b){jj(gv,a);nv(b)}
function nv(a){a?kv(null):ov()}
function ov(){if(!fv){fv=true;Aj().sd(ev)}}
var ev,fv=false,gv,hv,iv;function qv(){}
function rv(){wb.call(this);qv()}
function pv(){}
_=rv.prototype=pv.prototype=new sb;_.wd=function sv(){(jv(),fv)&&kv(null)};_.gC=function tv(){return JK};function wv(){wv=xxj;vv=new Pv}
function xv(){}
function yv(a,b){var c;c=Lp(eq());yn(c,PEj,'text/css');Av(c,b);return c}
function zv(a){var b;if(dJ(a.a)){b=Eq(Zp(eq(),jCj),0);a.a=gq(b)}return a.a}
function Av(a,b){xn(a,b)}
function Bv(){wv();wb.call(this);xv()}
function uv(){}
_=uv.prototype=new sb;_.gC=function Cv(){return LK};_.ne=function Dv(a){var b;b=yv(this,a);Gm(zv(this),b);return b};_.oe=function Ev(a){return this.ne(a)};_.pe=function Fv(a){var b;b=yv(this,a);Om(zv(this),b,Jm(this.a));return b};_.a=null;var vv;function Iv(){Iv=xxj;wv();30;Hv=EI(Rqb,{512:1,515:1},-1,30,1)}
function Jv(){}
function Kv(a,b){a.cssText+=b}
function Lv(a,b,c,d){var e;e=Sv(b);d?Kv(e,c):Ov(e,c);return e}
function Mv(){return $doc.createStyleSheet()}
function Nv(a,b){var c;c=Mv();dv(c,b);return c}
function Ov(a,b){a.cssText=b+a.cssText}
function Pv(){Iv();Bv.call(this);Jv()}
function Rv(){return $doc.styleSheets.length}
function Sv(a){return $doc.styleSheets[a]}
function Tv(a){return $doc.styleSheets[a].cssText.length}
function Gv(){}
_=Pv.prototype=Gv.prototype=new uv;_.gC=function Qv(){return KK};_.ne=function Uv(a){var b,c,d,e,f;d=Rv();if(d<30){return Nv(this,a)}else{f=2147483647;e=-1;for(b=0;b<d;++b){c=Hv[b];c==0&&(c=Hv[b]=Tv(b));if(c<=f){f=c;e=b}}Hv[e]+=fPh(a);return Lv(this,e,a,true)}};_.oe=function Vv(a){var b;b=Rv();if(b==0){return Nv(this,a)}return Lv(this,b-1,a,true)};_.pe=function Wv(a){if(Rv()==0){return Nv(this,a)}return Lv(this,0,a,false)};var Hv;function Zv(a){return ko((Ln(),Kn,a))}
function $v(a){return lo((Ln(),Kn,a))}
function wA(a){return Aq(xw(a))}
function zA(){dJ(tA)&&(tA=new XA);return tA.Qe()}
var tA=null;function VA(a){a.a=WA()}
function WA(){var a=document.createElement(WBj);a.setAttribute('ontouchstart','return;');return typeof a.ontouchstart==mCj}
function XA(){wb.call(this);VA(this)}
function UA(){}
_=XA.prototype=UA.prototype=new sb;_.gC=function YA(){return iL};_.Qe=function ZA(){return this.a};function wB(a){return a.a}
function CB(){dJ(tB)&&(tB=new sx);return tB}
function QB(){}
function RB(a,b){b.Ve(a)}
function SB(a,b){qw.call(this);QB();this,a;this,b}
function UB(a,b,c){var d;if(cJ(PB)){d=new SB(b,c);a.cd(d)}}
function XB(){dJ(PB)&&(PB=new sx);return PB}
function OB(){}
_=SB.prototype=OB.prototype=new bw;_.se=function TB(a){RB(this,UI(a,72))};_.te=function VB(){return PB};_.gC=function WB(){return oL};var PB=null;function Eub(a){a.b=new Svb;a.c=new b8b}
function Fub(a,b,c){var d,e,f;d=c.i*Iub(a,c.j,false);e=c.k*Iub(a,c.n,false);f=c.X*Iub(a,c.Y,false);if(c.q&&!c.u){c.q=false;if(c.z){c.v=true;c.D=(b-(d+f))/Iub(a,c.N,false)}else{c.x=true;c.F=(b-(d+e))/Iub(a,c.R,false)}}else if(c.z&&!c.x){c.z=false;if(c.q){c.v=true;c.D=(b-(d+f))/Iub(a,c.N,false)}else{c.u=true;c.C=(b-(e+f))/Iub(a,c.L,false)}}else if(c.r&&!c.v){c.r=false;if(c.z){c.u=true;c.C=(b-(e+f))/Iub(a,c.L,false)}else{c.x=true;c.F=(b-(d+e))/Iub(a,c.R,false)}}c.q=c.u;c.r=c.v;c.z=c.x;c.j=c.L;c.n=c.N;c.Y=c.R}
function Gub(a,b,c){var d,e,f;f=c.S*Iub(a,c.T,true);d=c.a*Iub(a,c.b,true);e=c.f*Iub(a,c.g,true);if(c.y&&!c.w){c.y=false;if(c.p){c.s=true;c.A=(b-(f+e))/Iub(a,c.H,true)}else{c.t=true;c.B=(b-(f+d))/Iub(a,c.J,true)}}else if(c.p&&!c.t){c.p=false;if(c.y){c.s=true;c.A=(b-(f+e))/Iub(a,c.H,true)}else{c.w=true;c.E=(b-(d+e))/Iub(a,c.P,true)}}else if(c.o&&!c.s){c.o=false;if(c.p){c.w=true;c.E=(b-(d+e))/Iub(a,c.P,true)}else{c.t=true;c.B=(b-(f+d))/Iub(a,c.J,true)}}c.y=c.w;c.o=c.s;c.p=c.t;c.T=c.P;c.b=c.H;c.g=c.J}
function Hub(a,b,c,d){var e,f;e=Avb(a.b,a.d,b,c);f=new uvb(a,e,b,d);a.c.fl(f);return f}
function Iub(a,b,c){return Dvb(a.b,a.d,b,c)}
function Jub(a){Kub(a,0)}
function Kub(a,b){Lub(a,b,null)}
function Lub(a,b,c){var d,e,f,g;cJ(a.a)&&Cb(a.a);if(b==0){for(e=a.c.lf();e.Fg();){d=UI(e.Gg(),92);d.i=d.C=d.K;d.S=d.E=d.O;d.k=d.D=d.M;d.a=d.A=d.G;d.X=d.F=d.Q;d.f=d.B=d.I;d.q=d.u;d.y=d.w;d.r=d.v;d.o=d.s;d.z=d.x;d.p=d.t;d.j=d.L;d.T=d.P;d.n=d.N;d.b=d.H;d.Y=d.R;d.g=d.J;a.b.If(d)}Cvb();cJ(c)&&c.Gf();return}g=_m(a.d);f=$m(a.d);for(e=a.c.lf();e.Fg();){d=UI(e.Gg(),92);Fub(a,g,d);Gub(a,f,d)}a.a=new Uub(a,c);Gb(a.a,b,a.d)}
function Mub(a){Fvb()}
function Nub(a){a.b.Jf(a.d)}
function Oub(a,b){Gvb(b.d,b.c);a.c.kl(b)}
function Pub(a){wb.call(this);Eub(this);this.d=a;Evb(this.b,a)}
function Dub(){}
_=Pub.prototype=Dub.prototype=new sb;_.gC=function Qub(){return eM};_.a=null;_.d=null;function Sub(){}
function Tub(a){a.a.a=null;Jub(a.a);cJ(a.b)&&a.b.Gf()}
function Uub(a,b){this.a=a;this.b=b;Jb.call(this);Sub()}
function Rub(){}
_=Uub.prototype=Rub.prototype=new rb;_.gC=function Vub(){return _L};_.Kc=function Wub(){Tub(this)};_.Lc=function Xub(){Tub(this)};_.Nc=function Yub(a){var b,c;for(c=this.a.c.lf();c.Fg();){b=UI(c.Gg(),92);b.u&&(b.i=b.C+(b.K-b.C)*a);b.v&&(b.k=b.D+(b.M-b.D)*a);b.w&&(b.S=b.E+(b.O-b.E)*a);b.s&&(b.a=b.A+(b.G-b.A)*a);b.x&&(b.X=b.F+(b.Q-b.F)*a);b.t&&(b.f=b.B+(b.I-b.B)*a);this.a.b.If(b);cJ(this.b)&&this.b.Hf(b,a)}Cvb()};_.a=null;_.b=null;function cvb(){cvb=xxj;_ub=new evb('BEGIN',0);avb=new evb('END',1);bvb=new evb('STRETCH',2);$ub=HI(hrb,{515:1,538:1},91,[_ub,avb,bvb])}
function dvb(){}
function evb(a,b){fg.call(this,a,b);dvb()}
function gvb(a){cvb();return og((jvb(),ivb),a)}
function hvb(){cvb();return $ub}
function Zub(){}
_=evb.prototype=Zub.prototype=new $f;_.gC=function fvb(){return aM};_.cM={91:1,515:1,525:1,527:1};var $ub,_ub,avb,bvb;function jvb(){jvb=xxj;ivb=hg((cvb(),$ub))}
var ivb;function lvb(a){a.L=(eu(),du);a.P=(eu(),du);a.N=(eu(),du);a.H=(eu(),du);a.e=(cvb(),bvb);a.V=(cvb(),bvb)}
function mvb(a){return a.d}
function nvb(a){return a.U}
function ovb(a,b,c,d,e){a.s=a.t=true;a.w=false;a.G=b;a.I=d;a.H=c;a.J=e}
function pvb(a,b,c,d,e){a.u=a.v=true;a.x=false;a.K=b;a.M=d;a.L=c;a.N=e}
function qvb(a,b,c,d,e){a.u=a.x=true;a.v=false;a.K=b;a.Q=d;a.L=c;a.R=e}
function rvb(a,b,c,d,e){a.v=a.x=true;a.u=false;a.M=b;a.Q=d;a.N=c;a.R=e}
function svb(a,b,c,d,e){a.w=a.s=true;a.t=false;a.O=b;a.G=d;a.P=c;a.H=e}
function tvb(a,b,c,d,e){a.w=a.t=true;a.s=false;a.O=b;a.I=d;a.P=c;a.J=e}
function uvb(a,b,c,d){this,a;wb.call(this);lvb(this);this.d=b;this.c=c;this.U=d}
function kvb(){}
_=uvb.prototype=kvb.prototype=new sb;_.gC=function vvb(){return bM};_.cM={92:1};_.a=0;_.b=null;_.c=null;_.d=null;_.f=0;_.g=null;_.i=0;_.j=null;_.k=0;_.n=null;_.o=false;_.p=false;_.q=false;_.r=false;_.s=true;_.t=false;_.u=true;_.v=true;_.w=true;_.x=false;_.y=false;_.z=false;_.A=0;_.B=0;_.C=0;_.D=0;_.E=0;_.F=0;_.G=0;_.I=0;_.J=null;_.K=0;_.M=0;_.O=0;_.Q=0;_.R=null;_.S=0;_.T=null;_.U=null;_.W=true;_.X=0;_.Y=null;function yvb(){yvb=xxj;xvb=Ivb((eu(),Xt),(eu(),Xt));Gm(Rp(eq()),xvb)}
function zvb(){}
function Avb(a,b,c,d){var e,f;f=Dp(eq());Gm(f,c);er(pn(f),(tt(),pt));cr(pn(f),(Ns(),Ks));Bvb(c);e=null;cJ(d)&&(e=Nm(d));Om(b,f,e);return f}
function Bvb(a){var b;b=pn(a);er(b,(tt(),pt));_q(b,0,(eu(),du));kr(b,0,(eu(),du));jr(b,0,(eu(),du));Yq(b,0,(eu(),du))}
function Cvb(){}
function Dvb(a,b,c,d){if(dJ(c)){return 1}switch(dg(c)){case 1:return (d?$m(b):_m(b))/100;case 2:return gn(a.a)/10;case 3:return en(a.a)/10;case 7:return gn(xvb)*0.1;case 8:return gn(xvb)*0.01;case 6:return gn(xvb)*0.254;case 4:return gn(xvb)*0.00353;case 5:return gn(xvb)*0.0423;default:case 0:return 1;}}
function Evb(a,b){er(pn(b),(tt(),rt));Gm(b,a.a=Ivb((eu(),Yt),(eu(),Zt)))}
function Fvb(){}
function Gvb(a,b){var c;Rm(a);eJ(Nm(b),a)&&Rm(b);c=pn(b);Nq(c);Mq(c);Qq(c);Rq(c);Lq(c)}
function Hvb(){wb.call(this);zvb()}
function Ivb(a,b){var c,d;c=Dp(eq());wn(c,qGj);d=pn(c);er(d,(tt(),pt));mr(d,-32767);kr(d,-20,b);lr(d,10,a);$q(d,10,b);return c}
function wvb(){}
_=wvb.prototype=new sb;_.gC=function Jvb(){return dM};_.If=function Kvb(a){var b;b=pn(a.d);a.W?Kq(b):Zq(b,(fs(),es));fr(b,xCj,a.q?a.i+a.j.me():VBj);fr(b,yCj,a.y?a.S+a.T.me():VBj);fr(b,LCj,a.r?a.k+a.n.me():VBj);fr(b,GDj,a.o?a.a+a.b.me():VBj);fr(b,SBj,a.z?a.X+a.Y.me():VBj);fr(b,QBj,a.p?a.f+a.g.me():VBj);b=pn(a.c);switch(dg(a.e)){case 0:_q(b,0,(eu(),du));Pq(b);break;case 1:Mq(b);jr(b,0,(eu(),du));break;case 2:_q(b,0,(eu(),du));jr(b,0,(eu(),du));}switch(dg(a.V)){case 0:kr(b,0,(eu(),du));Jq(b);break;case 1:Qq(b);Yq(b,0,(eu(),du));break;case 2:kr(b,0,(eu(),du));Yq(b,0,(eu(),du));}};_.Jf=function Lvb(a){};_.a=null;var xvb=null;function Nvb(){}
function Ovb(a,b){var c,d;d=pn(b.d);Xvb(b.d,b);if(b.W){c=Sq(d);Kq(d);fPh(c)>0&&Rvb(a,b.d)}else{Zq(d,(fs(),es))}b.q?Qvb(a,b,xCj,b.i,b.j,false,false):Mq(d);b.r?Qvb(a,b,LCj,b.k,b.n,false,false):Pq(d);b.y?Qvb(a,b,yCj,b.S,b.T,true,false):Qq(d);b.o?Qvb(a,b,GDj,b.a,b.b,true,false):Jq(d);b.z?Qvb(a,b,SBj,b.X,b.Y,false,true):Rq(d);b.p?Qvb(a,b,QBj,b.f,b.g,true,true):Lq(d);d=pn(b.c);switch(dg(b.e)){case 0:_q(d,0,(eu(),du));Pq(d);break;case 1:Mq(d);jr(d,0,(eu(),du));break;case 2:_q(d,0,(eu(),du));jr(d,0,(eu(),du));}switch(dg(b.V)){case 0:kr(d,0,(eu(),du));Jq(d);break;case 1:Qq(d);Yq(d,0,(eu(),du));break;case 2:kr(d,0,(eu(),du));Yq(d,0,(eu(),du));}}
function Pvb(a){for(var b=0;b<a.childNodes.length;++b){var c=a.childNodes[b];if(c.__layer){c.__layer=null}}}
function Qvb(a,b,c,d,e,f,g){switch(dg(e)){case 0:case 1:break;default:d=d*Dvb(a,b.d,e,f);d=lJ(d+0.5);e=(eu(),du);}g&&(d<0&&(d=0));gr(pn(mvb(b)),c,d,e)}
function Rvb(a,b){var c,d,e,f;d=Uvb(b);cJ(d)&&Ovb(a,d);f=Im(b);for(c=0;c<Fq(f);++c){e=Eq(f,c);Lm(e)==1&&Rvb(a,Qe(e))}}
function Svb(){yvb();Hvb.call(this);Nvb()}
function Uvb(a){return a.__layer}
function Xvb(a,b){a.__layer=b}
function Mvb(){}
_=Svb.prototype=Mvb.prototype=new wvb;_.gC=function Tvb(){return cM};_.If=function Vvb(a){Ovb(this,a)};_.Jf=function Wvb(a){Pvb(a)};function Kxb(){}
function Lxb(a,b,c,d){var e,f,g;g=a*b;if(c>=0){e=fOh(0,c-d);g=iOh(g,e)}else{f=iOh(0,c+d);g=fOh(g,f)}return g}
function Mxb(){wb.call(this);Kxb()}
function Jxb(){}
_=Mxb.prototype=Jxb.prototype=new sb;_.Qf=function Nxb(a,b){return new _xb(a,b)};_.gC=function Oxb(){return pM};_.Rf=function Pxb(a){var b,c,d,e,f,g,i,j,k,n,o,p;e=Txb(a);p=Sxb(a);f=Uxb(a);n=Wxb(a);b=kOh(0.9993,p);g=e*5.0E-4;j=Lxb(dyb(f),b,dyb(n),g);k=Lxb(eyb(f),b,eyb(n),g);i=new iyb(j,k);$xb(a,i);d=Txb(a);c=gyb(i,new iyb(d,d));o=Vxb(a);Zxb(a,hyb(o,c));if(TNh(dyb(i))<0.02&&TNh(eyb(i))<0.02){return false}return true};function Rxb(a){}
function Sxb(a){return a.a}
function Txb(a){return a.b}
function Uxb(a){return a.c}
function Vxb(a){return a.d}
function Wxb(a){return a.e}
function Xxb(a,b){a.a=b}
function Yxb(a,b){a.b=b}
function Zxb(a,b){a.d=b}
function $xb(a,b){a.e=b}
function _xb(a,b){wb.call(this);Rxb(this);this,a;this.c=b;this.d=new jyb(a);this.e=new jyb(b)}
function Qxb(){}
_=_xb.prototype=Qxb.prototype=new sb;_.gC=function ayb(){return qM};_.a=0;_.b=0;_.c=null;_.d=null;_.e=null;function cyb(){}
function dyb(a){return a.a}
function eyb(a){return a.b}
function fyb(a,b){return new iyb(a.a-b.a,a.b-b.b)}
function gyb(a,b){return new iyb(a.a*b.a,a.b*b.b)}
function hyb(a,b){return new iyb(a.a+b.a,a.b+b.b)}
function iyb(a,b){wb.call(this);cyb();this.a=a;this.b=b}
function jyb(a){iyb.call(this,a.a,a.b)}
function byb(){}
_=jyb.prototype=iyb.prototype=byb.prototype=new sb;_.eQ=function kyb(a){var b;if(!YI(a,99)){return false}b=UI(a,99);return this.a==b.a&&this.b==b.b};_.gC=function lyb(){return rM};_.hC=function myb(){return lJ(this.a)^lJ(this.b)};_.tS=function nyb(){return 'Point('+this.a+rGj+this.b+WEj};_.cM={99:1};_.a=0;_.b=0;function qyb(a){a.d=new b8b;a.e=new Pzb;a.k=new Pzb;a.j=new Pzb;a.q=new b8b;a.i=new Hzb(a)}
function ryb(a,b){var c,d;d=Nzb(b)-Nzb(a);if(d<=0){return null}c=fyb(Mzb(a),Mzb(b));return new iyb(dyb(c)/d,eyb(c)/d)}
function syb(a){a.r=false;a.c=false;a.g=null}
function tyb(a){var b;b=wA(a);return aj(b)>0?_i(b,0):null}
function uyb(a){return new iyb(a.s.sg(),a.s.yg())}
function vyb(a,b){var c,d,e;e=fyb(a,b);c=TNh(dyb(e));d=TNh(eyb(e));return c<=25&&d<=25}
function wyb(a,b){if(cJ(Mzb(a.j))){return vyb(b,Mzb(a.j))}return false}
function xyb(a,b){var c,d,e,f;c=Ai();f=false;for(e=a.q.lf();e.Fg();){d=UI(e.Gg(),100);if(c-Nzb(d)<=2500&&vyb(b,Mzb(d))){f=true;break}}return f}
function yyb(a){return cJ(a.g)}
function zyb(a){var b;if(dJ(a.f)){return}b=ryb(a.k,a.e);if(cJ(b)){a.g=new xzb(a,b);Aj().td(a.g,16)}}
function Ayb(a){var b,c;c=fyb(a.p,Mzb(a.e));b=hyb(a.o,c);Kyb(a,b)}
function Byb(){}
function Cyb(a,b){Dyb(a,b)}
function Dyb(a,b){if(!a.r){return}a.r=false;if(a.c){a.c=false;zyb(a)}}
function Eyb(a,b){var c,d,e,f,g,i,j,k,n,o,p,q,r;if(!a.r){return}j=tyb(b);k=new iyb(Zv(j),$v(j));n=Ai();Ozb(a.e,k,n);if(!a.c){e=fyb(k,a.p);c=TNh(dyb(e));d=TNh(eyb(e));if(c>5||d>5){Ozb(a.j,Mzb(a.k),Nzb(a.k));if(c>d){i=a.s.sg();g=a.s.ug();f=a.s.tg();if(dyb(e)<0&&f<=i){syb(a);return}else if(dyb(e)>0&&g>=i){syb(a);return}}else{r=a.s.yg();q=a.s.xg();p=a.s.wg();if(eyb(e)<0&&p<=r){syb(a);return}else if(eyb(e)>0&&q>=r){syb(a);return}}a.c=true;Byb()}}yw(b);if(a.c){Ayb(a);o=n-Nzb(a.k);if(o>200&&cJ(a.n)){Ozb(a.k,Mzb(a.n),Nzb(a.n));a.n=null}else o>100&&dJ(a.n)&&(a.n=new Qzb(k,n))}}
function Fyb(a,b){var c,d;Ozb(a.j,null,0);if(a.r){return}d=tyb(b);a.p=new iyb(Zv(d),$v(d));c=Ai();Ozb(a.k,a.p,c);Ozb(a.e,a.p,c);a.n=null;if(yyb(a)){a.q.fl(new Qzb(a.p,c));Aj().td(a.i,2500)}a.o=uyb(a);syb(a);a.r=true}
function Gyb(a){if(cJ(a.a)){a.a.Xe();a.a=null}}
function Hyb(a){if(cJ(a.b)){a.b.Xe();a.b=null}}
function Iyb(a,b){a.f=b;dJ(b)&&(a.g=null)}
function Jyb(a,b){var c,d;if(eJ(a.s,b)){return}syb(a);for(d=a.d.lf();d.Fg();){c=UI(d.Gg(),77);c.Xe()}a.d.hl();Gyb(a);Hyb(a);a.s=b;if(cJ(b)){b._c().dd()&&Lyb(a);a.a=wd(b._c(),new Uyb(a));a.d.fl(xd(b._c(),new Zyb(a),rB()));a.d.fl(xd(b._c(),new czb(a),hB()));a.d.fl(xd(b._c(),new hzb(a),TA()));a.d.fl(xd(b._c(),new mzb(a),JA()))}}
function Kyb(a,b){a.s.vg(lJ(dyb(b)));a.s.zg(lJ(eyb(b)))}
function Lyb(a){Hyb(a);a.b=$Ab(new rzb(a))}
function Myb(){wb.call(this);qyb(this);Iyb(this,new Mxb)}
function Nyb(){return Ryb()?new Myb:null}
function Oyb(a){var b;b=Nyb();cJ(b)&&Jyb(b,a);return b}
function Qyb(){var a=navigator.userAgent.toLowerCase();return /android ([3-9]+)\.([0-9]+)/.exec(a)!=null}
function Ryb(){dJ(pyb)&&(pyb=xLh(zA()&&!Qyb()));return nLh(pyb)}
function oyb(){}
_=Myb.prototype=oyb.prototype=new sb;_.gC=function Pyb(){return CM};_.a=null;_.b=null;_.c=false;_.f=null;_.g=null;_.n=null;_.o=null;_.p=null;_.r=false;_.s=null;var pyb=null;function Tyb(){}
function Uyb(a){this.a=a;wb.call(this);Tyb()}
function Syb(){}
_=Uyb.prototype=Syb.prototype=new sb;_.gC=function Vyb(){return sM};_.Te=function Wyb(a){wB(a)?Lyb(this.a):Hyb(this.a)};_.cM={67:1,75:1};_.a=null;function Yyb(){}
function Zyb(a){this.a=a;wb.call(this);Yyb()}
function Xyb(){}
_=Zyb.prototype=Xyb.prototype=new sb;_.gC=function $yb(){return tM};_.Se=function _yb(a){Fyb(this.a,a)};_.cM={66:1,75:1};_.a=null;function bzb(){}
function czb(a){this.a=a;wb.call(this);bzb()}
function azb(){}
_=czb.prototype=azb.prototype=new sb;_.gC=function dzb(){return uM};_.Re=function ezb(a){Eyb(this.a,a)};_.cM={65:1,75:1};_.a=null;function gzb(){}
function hzb(a){this.a=a;wb.call(this);gzb()}
function fzb(){}
_=hzb.prototype=fzb.prototype=new sb;_.gC=function izb(){return vM};_.Pe=function jzb(a){Dyb(this.a,a)};_.cM={64:1,75:1};_.a=null;function lzb(){}
function mzb(a){this.a=a;wb.call(this);lzb()}
function kzb(){}
_=mzb.prototype=kzb.prototype=new sb;_.gC=function nzb(){return wM};_.Oe=function ozb(a){Cyb(this.a,a)};_.cM={63:1,75:1};_.a=null;function qzb(){}
function rzb(a){this.a=a;wb.call(this);qzb()}
function pzb(){}
_=rzb.prototype=pzb.prototype=new sb;_.gC=function szb(){return xM};_.Sf=function tzb(a){var b;if(1==mBb(a)){b=new iyb(rq(lBb(a)),sq(lBb(a)));if(wyb(this.a,b)||xyb(this.a,b)){jBb(a);Dq(lBb(a));Cq(lBb(a))}}};_.cM={75:1,104:1};_.a=null;function vzb(a){a.a=new zi;a.b=uyb(a.e)}
function wzb(a){if(cJ(a.f)){a.f.Xe();a.f=null}eJ(a,a.e.g)&&(a.e.g=null)}
function xzb(a,b){this.e=a;wb.call(this);vzb(this);this.d=a.f.Qf(this.b,b);this.f=aCb(new Czb(this))}
function uzb(){}
_=xzb.prototype=uzb.prototype=new sb;_.vd=function yzb(){var a,b,c,d,e,f,g,i;if(fJ(this,this.e.g)){wzb(this);return false}a=xi(this.a);Yxb(this.d,a-this.c);this.c=a;Xxb(this.d,a);e=this.e.f.Rf(this.d);e||wzb(this);Kyb(this.e,Vxb(this.d));d=lJ(dyb(Vxb(this.d)));c=this.e.s.ug();b=this.e.s.tg();g=this.e.s.xg();f=this.e.s.wg();i=lJ(eyb(Vxb(this.d)));if((f<=i||g>=i)&&(b<=d||c>=d)){wzb(this);return false}return e};_.gC=function zzb(){return zM};_.c=0;_.d=null;_.e=null;_.f=null;function Bzb(){}
function Czb(a){this.a=a;wb.call(this);Bzb()}
function Azb(){}
_=Czb.prototype=Azb.prototype=new sb;_.gC=function Dzb(){return yM};_.Ve=function Ezb(a){wzb(this.a)};_.cM={72:1,75:1};_.a=null;function Gzb(){}
function Hzb(a){this.a=a;wb.call(this);Gzb()}
function Fzb(){}
_=Hzb.prototype=Fzb.prototype=new sb;_.vd=function Izb(){var a,b,c;a=Ai();b=this.a.q.lf();while(b.Fg()){c=UI(b.Gg(),100);a-Nzb(c)>=2500&&b.Hg()}return !this.a.q.xh()};_.gC=function Jzb(){return AM};_.a=null;function Lzb(){}
function Mzb(a){return a.a}
function Nzb(a){return a.b}
function Ozb(a,b,c){a.a=b;a.b=c}
function Pzb(){wb.call(this);Lzb()}
function Qzb(a,b){wb.call(this);Lzb();Ozb(this,a,b)}
function Kzb(){}
_=Qzb.prototype=Pzb.prototype=Kzb.prototype=new sb;_.gC=function Rzb(){return BM};_.cM={100:1};_.a=null;_.b=0;function pAb(){Vzb();LCb(Tzb)}
function ZAb(a){return gAb(a)}
function $Ab(a){pAb();wBb();if(dJ(YAb)){YAb=new cC(null,true);hBb=new rBb}return $B(YAb,gBb,a)}
function _Ab(a){return a}
function cBb(a){rAb(Qe(a))}
function dBb(a){tAb(Qe(a))}
function eBb(a,b){vAb(a,b)}
function iBb(a){}
function jBb(a){a.a=true}
function kBb(a,b){b.Sf(a);hBb.c=false}
function lBb(a){return a.d}
function mBb(a){return ZAb(_Ab(lBb(a)))}
function nBb(a){return a.a}
function oBb(a){return a.b}
function pBb(a){pw(a);a.a=false;a.b=false;a.c=true;a.d=null}
function qBb(a,b){a.d=b}
function rBb(){qw.call(this);iBb(this)}
function wBb(){dJ(gBb)&&(gBb=new sx);return gBb}
function fBb(){}
_=rBb.prototype=fBb.prototype=new bw;_.se=function sBb(a){kBb(this,UI(a,104))};_.te=function uBb(){return gBb};_.gC=function vBb(){return GM};_.Wf=function xBb(){return nBb(this)};_.Xf=function yBb(){return oBb(this)};_.ue=function zBb(){pBb(this)};_.Yf=function ABb(a){qBb(this,a)};_.a=false;_.b=false;_.c=false;_.d=null;function aCb(a){ZBb();jCb();kCb();return _Bb(XB(),a)}
function eCb(){ZBb();return Up(eq())}
function fCb(){ZBb();return Vp(eq())}
function kCb(){if(Ji()&&!YBb){VBb.jg();YBb=true}}
function nCb(){ZBb();var a,b;if(YBb){b=fCb();a=eCb();if(XBb!=b||WBb!=a){XBb=b;WBb=a;UB(gCb(),b,a)}}}
var WBb=0,XBb=0,YBb=false;_=wDb.prototype;_.jg=function BDb(){var b=$wnd.onresize;$wnd.onresize=MBj(function(a){try{nCb()}finally{b&&b(a)}})};function GDb(){$wnd.__gwt_initWindowResizeHandler(MBj(nCb))}
_=CDb.prototype;_.jg=function KDb(){EDb((WDb(),VDb).lg().$e(),new SDb(this))};function RDb(){}
function SDb(a){this.a=a;wb.call(this);RDb()}
function QDb(){}
_=SDb.prototype=QDb.prototype=new sb;_.wd=function TDb(){GDb()};_.gC=function UDb(){return SM};_.cM={102:1};_.a=null;function dEb(){return wEb()}
function eEb(a){$Db=new qEb(a)}
_=XDb.prototype;_.lg=function iEb(){return dEb()};var $Db=null;function pEb(){}
function qEb(a){this,a;wb.call(this);pEb()}
function oEb(){}
_=qEb.prototype=oEb.prototype=new sb;_.gC=function rEb(){return UM};_.$e=function sEb(){return "function __gwt_initWindowResizeHandler(resize) {\n  var wnd = window, oldOnResize = wnd.onresize;\n  \n  wnd.onresize = function(evt) {\n    try {\n      resize();\n    } finally {\n      oldOnResize && oldOnResize(evt);\n    }\n  };\n  \n  // Remove the reference once we've initialize the handler\n  wnd.__gwt_initWindowResizeHandler = undefined;\n}\n"};_.cM={94:1};function vEb(){vEb=xxj;eEb((_Db(),YDb))}
function wEb(){vEb();return _Db(),$Db}
function VFb(){VFb=xxj;Tc();new iGb}
function WFb(){}
function XFb(a,b){var c;if(cJ(a.a)){throw new YMh('Composite.initWidget() may only be called once.')}YI(b,136)&&(a,UI(b,136));Kd(b);c=Yc(b);_c(a,c);SIb(c)&&OIb(PIb(c),a);a.a=b;Md(b,a)}
function YFb(a){if(cJ(a.a)){return a.a.dd()}return false}
function ZFb(a){if(!Ed(a)){Nd(a.a,a.o);a.o=-1}a.a.ed();vAb(Yc(a),a);a.hd();zB(a,true)}
function $Fb(a,b){Gd(a,b);a.a.fd(b)}
function _Fb(){VFb();Od.call(this);WFb()}
function UFb(){}
_=UFb.prototype=new Rc;_.gC=function aGb(){return fN};_.dd=function bGb(){return YFb(this)};_.ed=function cGb(){ZFb(this)};_.fd=function dGb(a){$Fb(this,a)};_.gd=function eGb(){try{this.jd();zB(this,false)}finally{this.a.gd()}};_.Xc=function fGb(){_c(this,this.a.Xc());return Yc(this)};_.cM={70:1,78:1,105:1,133:1,136:1,137:1,164:1,166:1};_.a=null;function hGb(){}
function iGb(){wb.call(this);hGb()}
function gGb(){}
_=iGb.prototype=gGb.prototype=new sb;_.gC=function jGb(){return eN};function xGb(){}
function yGb(a,b){a.pg(b,(bHb(),WGb),0,null)}
function zGb(a,b,c){a.pg(b,(bHb(),$Gb),c,null)}
function AGb(a,b,c){a.pg(b,(bHb(),_Gb),c,null)}
function BGb(a,b,c){a.pg(b,(bHb(),aHb),c,null)}
function CGb(a,b){DGb(a,b,null)}
function DGb(a,b,c){nHb(a.f,b,c)}
function EGb(){}
function FGb(a){var b,c,d,e,f,g,i,j;g=0;j=0;i=0;b=0;for(d=LEb(a).lf();d.Fg();){c=UI(d.Gg(),166);e=UI(Cd(c),121);f=e.b;switch(dg(JGb(e.a))){case 0:pvb(f,g,a.g,i,a.g);tvb(f,j,a.g,e.c,a.g);j+=e.c;break;case 2:pvb(f,g,a.g,i,a.g);ovb(f,b,a.g,e.c,a.g);b+=e.c;break;case 3:svb(f,j,a.g,b,a.g);qvb(f,g,a.g,e.c,a.g);g+=e.c;break;case 1:svb(f,j,a.g,b,a.g);rvb(f,i,a.g,e.c,a.g);i+=e.c;break;case 4:pvb(f,g,a.g,i,a.g);svb(f,j,a.g,b,a.g);}}a.d=g+i;a.c=j+b}
function GGb(a){mHb(a.f);FGb(a);Jub(a.e);LGb(a)}
function HGb(a){return $m(Yc(a))/Iub(a.e,a.g,true)-a.c}
function IGb(a){return _m(Yc(a))/Iub(a.e,a.g,false)-a.d}
function JGb(a){if(eJ(a,(bHb(),ZGb))){return GF(JF())?(bHb(),XGb):(bHb(),aHb)}else if(eJ(a,(bHb(),YGb))){return GF(JF())?(bHb(),aHb):(bHb(),XGb)}return a}
function KGb(a,b,c,d,e){var f,g,i,j;EGb();Kd(b);f=LEb(a);if(dJ(e)){RMb(f,b)}else{i=TMb(f,e);UMb(f,b,i)}eJ(c,(bHb(),WGb))&&(a.b=b);j=Hub(a.e,Yc(b),cJ(e)?Yc(e):null,b);g=new yHb(c,d,j);Ld(b,g);BEb(a,b);CGb(a,0)}
function LGb(a){var b,c;for(c=LEb(a).lf();c.Fg();){b=UI(c.Gg(),166);YI(b,150)&&UI(b,150).qg()}}
function MGb(a,b){var c,d;d=REb(a,b);if(d){eJ(b,a.b)&&(a.b=null);c=UI(Cd(b),121);Oub(a.e,c.b)}return d}
function NGb(a){Tc();SEb.call(this);xGb();this.g=a;_c(this,Dp(eq()));this.e=new Pub(Yc(this));this.f=new tHb(this,this.e)}
function wGb(){}
_=NGb.prototype=wGb.prototype=new yEb;_.gC=function OGb(){return kN};_.pg=function PGb(a,b,c,d){KGb(this,a,b,c,d)};_.hd=function QGb(){Mub(this.e)};_.qg=function RGb(){LGb(this)};_.jd=function SGb(){Nub(this.e)};_.mg=function TGb(a){return MGb(this,a)};_.cM={70:1,78:1,105:1,133:1,134:1,137:1,150:1,164:1,166:1};_.b=null;_.c=0;_.d=0;_.e=null;_.f=null;_.g=null;function bHb(){bHb=xxj;$Gb=new dHb('NORTH',0);XGb=new dHb('EAST',1);_Gb=new dHb('SOUTH',2);aHb=new dHb('WEST',3);WGb=new dHb(sGj,4);ZGb=new dHb('LINE_START',5);YGb=new dHb('LINE_END',6);VGb=HI(jrb,{515:1,538:1},120,[$Gb,XGb,_Gb,aHb,WGb,ZGb,YGb])}
function cHb(){}
function dHb(a,b){fg.call(this,a,b);cHb()}
function fHb(a){bHb();return og((iHb(),hHb),a)}
function gHb(){bHb();return VGb}
function UGb(){}
_=dHb.prototype=UGb.prototype=new $f;_.gC=function eHb(){return hN};_.cM={120:1,515:1,525:1,527:1};var VGb,WGb,XGb,YGb,ZGb,$Gb,_Gb,aHb;function iHb(){iHb=xxj;hHb=hg((bHb(),VGb))}
var hHb;function lHb(){}
function mHb(a){a.c=true}
function nHb(a,b,c){a.d=b;a.b=c;a.c=false;if(!a.f){a.f=true;Aj().sd(a)}}
function oHb(a){wb.call(this);lHb();this.e=a}
function kHb(){}
_=kHb.prototype=new sb;_.rg=function pHb(){};_.wd=function qHb(){this.f=false;if(this.c){return}this.rg();Lub(this.e,this.d,new JIb(this))};_.gC=function rHb(){return uN};_.b=null;_.c=false;_.d=0;_.e=null;_.f=false;function sHb(){}
function tHb(a,b){this.a=a;oHb.call(this,b);sHb()}
function jHb(){}
_=tHb.prototype=jHb.prototype=new kHb;_.rg=function uHb(){FGb(this.a)};_.gC=function vHb(){return iN};_.a=null;function xHb(){}
function yHb(a,b,c){wb.call(this);xHb();this.a=a;this.c=b;this.b=c}
function wHb(){}
_=yHb.prototype=wHb.prototype=new sb;_.gC=function zHb(){return jN};_.cM={121:1};_.a=null;_.b=null;_.c=0;function FHb(a){Tc();HHb.call(this,a?Kp(eq()):Dp(eq()),a)}
function MHb(a,b){sGb(a.b,b,false);EHb(a)}
function NHb(){JHb();FHb.call(this,false);KHb();ed(this,'gwt-Label')}
_=NHb.prototype=BHb.prototype;function CIb(a,b){a.a=b}
function IIb(){}
function JIb(a){this.a=a;wb.call(this);IIb()}
function HIb(){}
_=JIb.prototype=HIb.prototype=new sb;_.gC=function KIb(){return tN};_.Gf=function LIb(){cJ(this.a.b)&&this.a.b.Gf()};_.Hf=function MIb(a,b){var c;c=UI(nvb(a),166);YI(c,150)&&UI(c,150).qg();cJ(this.a.b)&&this.a.b.Hf(a,b)};_.a=null;function OIb(b,a){b.__gwt_resolve=QIb(a)}
function PIb(a){return a}
function QIb(a){return function(){this.__gwt_resolve=RIb;return a.Xc()}}
function RIb(){throw 'A PotentialElement cannot be resolved twice.'}
function SIb(b){try{return !!b&&!!b.__gwt_resolve}catch(a){return false}}
function AJb(){}
function BJb(a,b){return a.Bg(b)?0:nn(b)-_m(b)}
function CJb(a,b){return a.Bg(b)?_m(b)-nn(b):0}
function DJb(){wb.call(this);AJb()}
function EJb(){dJ(zJb)&&(zJb=new KJb);return zJb}
function yJb(){}
_=yJb.prototype=new sb;_.gC=function FJb(){return BN};_.Ag=function GJb(a,b){};_.Bg=function HJb(a){var b=$doc.defaultView.getComputedStyle(a,null);return b.getPropertyValue(rCj)==qCj};var zJb=null;function JJb(){}
function KJb(){DJb.call(this);JJb()}
function OJb(a){Um(a,Jp(eq()))}
function IJb(){}
_=KJb.prototype=IJb.prototype=new yJb;_.gC=function LJb(){return AN};_.Ag=function MJb(a,b){var c=a;c.__lastScrollTop=c.__lastScrollLeft=0;var d=MBj(function(){c.__lastScrollTop=c.scrollTop;c.__lastScrollLeft=c.scrollLeft});a.attachEvent('onscroll',d);var e=MBj(function(){setTimeout(MBj(function(){if(c.scrollTop!=c.__lastScrollTop||c.scrollLeft!=c.__lastScrollLeft){d();OJb(c)}}),1)});a.attachEvent(tGj,e);b.attachEvent(tGj,e)};_.Bg=function NJb(a){return a.currentStyle.direction==qCj};function cKb(){}
function dKb(a){return BJb(EJb(),iKb(a))}
function eKb(a){return kn(iKb(a))-$m(iKb(a))}
function fKb(a){return CJb(EJb(),iKb(a))}
function gKb(){return 0}
function hKb(a){return mn(iKb(a))}
function iKb(a){return a.b}
function jKb(a){lKb(a,false);er(pn(a.b),(tt(),rt));er(pn(a.a),(tt(),rt));fr(pn(a.b),uGj,vGj);fr(pn(a.a),uGj,vGj);oKb(a,false);EJb().Ag(a.b,a.a)}
function kKb(a){return dJ(a.c)}
function lKb(a,b){cr(pn(iKb(a)),b?(Ns(),Ls):(Ns(),Js))}
function mKb(a,b){zn(iKb(a),b)}
function nKb(a,b){An(iKb(a),b)}
function oKb(a,b){if(b==kKb(a)){return b}if(b){Jyb(a.c,null);a.c=null}else{a.c=Oyb(a)}return kKb(a)}
function pKb(a,b){nKb(a,b)}
function qKb(){Tc();WJb.call(this);cKb();this.b=Yc(this);this.a=Qe(Dp(eq()));Gm(this.b,this.a);jKb(this)}
function PJb(){}
_=qKb.prototype=PJb.prototype=new QJb;_.gC=function rKb(){return CN};_.Cg=function sKb(){return this.a};_.sg=function tKb(){return ln(iKb(this))};_.tg=function uKb(){return dKb(this)};_.wg=function vKb(){return eKb(this)};_.ug=function wKb(){return fKb(this)};_.xg=function xKb(){return gKb()};_.yg=function yKb(){return hKb(this)};_.ed=function zKb(){Fd(this);eBb(iKb(this),this)};_.gd=function AKb(){eBb(iKb(this),null);Hd(this)};_.qg=function BKb(){var a;a=this.Dg();cJ(a)&&YI(a,150)&&UI(a,150).qg()};_.Yc=function CKb(a){bd(this,a)};_.vg=function DKb(a){mKb(this,a)};_.zg=function EKb(a){pKb(this,a)};_.$c=function FKb(a){fd(this,a)};_.cM={70:1,78:1,105:1,133:1,134:1,137:1,150:1,164:1,166:1};_.a=null;_.b=null;_.c=null;function QKb(){}
function RKb(a,b,c){var d,e;d=UI(Cd(b),121);e=null;switch(dg(JGb(d.a))){case 3:e=new eLb(a,b,false);break;case 1:e=new eLb(a,b,true);break;case 0:e=new sLb(a,b,false);break;case 2:e=new sLb(a,b,true);}KGb(a,e,d.a,a.a,c)}
function SKb(){Tc();TKb.call(this,8)}
function TKb(a){NGb.call(this,(eu(),du));QKb();this.a=a;ed(this,'gwt-SplitLayoutPanel');if(dJ(PKb)){PKb=Dp(eq());er(pn(PKb),(tt(),pt));kr(pn(PKb),0,(eu(),du));_q(pn(PKb),0,(eu(),du));ar(pn(PKb),0,(eu(),du));dr(pn(PKb),0,(eu(),du));Xq(pn(PKb),0,(eu(),du));fr(pn(PKb),wGj,xGj);br(pn(PKb),0)}}
function OKb(){}
_=SKb.prototype=OKb.prototype=new wGb;_.gC=function UKb(){return JN};_.pg=function VKb(a,b,c,d){KGb(this,a,b,c,d);fJ(b,(bHb(),WGb))&&RKb(this,a,d)};_.mg=function WKb(a){var b;b=OEb(this,a);if(MGb(this,a)){b<NEb(this)&&MGb(this,MEb(this,b));return true}return false};_.cM={70:1,78:1,105:1,133:1,134:1,137:1,150:1,164:1,166:1};_.a=0;var PKb=null;function ZKb(){}
function $Kb(a){var b;b=a.Jg();if(a.i!=b){a.i=b;a.b=b}return fOh(UI(Cd(a.j),121).c+a.b,0)}
function _Kb(a,b){var c,d;d=$Kb(a);b>d&&(b=d);b<a.d&&(b=a.d);c=UI(Cd(a.j),121);if(b==c.c){return}a.b+=c.c-b;c.c=b;if(dJ(a.c)){a.c=new nLb(a);Aj().rd(a.c)}}
function aLb(a,b,c){this.k=a;Od.call(this);ZKb();this.j=b;this.g=c;_c(this,Dp(eq()));Nd(this,78)}
function YKb(){}
_=YKb.prototype=new Rc;_.gC=function bLb(){return HN};_.fd=function cLb(a){var b,c,d;switch(ZAb(a)){case 4:this.e=true;d=gOh(fCb(),bq(eq()));b=gOh(eCb(),$p(eq()));$q(pn(PKb),b,(eu(),du));lr(pn(PKb),d,(eu(),du));Gm(Rp(eq()),PKb);this.f=this.Kg(a)-this.Ig();dBb(Yc(this));Cq(a);break;case 8:this.e=false;Rm(PKb);cBb(Yc(this));Cq(a);break;case 64:if(this.e){this.g?(c=this.Lg()+this.Mg()-this.Kg(a)-this.f):(c=this.Kg(a)-this.Lg()-this.f);_Kb(this,c);Cq(a)}}};_.cM={70:1,78:1,105:1,133:1,137:1,154:1,164:1,166:1};_.b=0;_.c=null;_.d=0;_.e=false;_.f=0;_.g=false;_.i=0;_.j=null;_.k=null;function dLb(){}
function eLb(a,b,c){this.a=a;aLb.call(this,a,b,c);dLb();ir(pn(Yc(this)),SBj,a.a);ed(this,'gwt-SplitLayoutPanel-HDragger')}
function XKb(){}
_=eLb.prototype=XKb.prototype=new YKb;_.Ig=function fLb(){return Wc(this)};_.Jg=function gLb(){return IGb(this.a)};_.gC=function hLb(){return FN};_.Kg=function iLb(a){return rq(a)};_.Lg=function jLb(){return Wc(this.j)};_.Mg=function kLb(){return this.j.Vc()};_.cM={70:1,78:1,105:1,133:1,137:1,154:1,164:1,166:1};_.a=null;function mLb(){}
function nLb(a){this.a=a;wb.call(this);mLb()}
function lLb(){}
_=nLb.prototype=lLb.prototype=new sb;_.wd=function oLb(){this.a.c=null;GGb(this.a.k)};_.gC=function pLb(){return GN};_.cM={102:1};_.a=null;function rLb(){}
function sLb(a,b,c){this.a=a;aLb.call(this,a,b,c);rLb();ir(pn(Yc(this)),QBj,a.a);ed(this,'gwt-SplitLayoutPanel-VDragger')}
function qLb(){}
_=sLb.prototype=qLb.prototype=new YKb;_.Ig=function tLb(){return Xc(this)};_.Jg=function uLb(){return HGb(this.a)};_.gC=function vLb(){return IN};_.Kg=function wLb(a){return sq(a)};_.Lg=function xLb(){return Xc(this.j)};_.Mg=function yLb(){return this.j.Uc()};_.cM={70:1,78:1,105:1,133:1,137:1,154:1,164:1,166:1};_.a=null;_=vGg.prototype;_.qd=function AGg(){var a;a=new MXg;Gm(eJb(),Yc(a))};function KXg(){KXg=xxj;VFb();JXg=new RXg}
function LXg(){}
function MXg(){KXg();_Fb.call(this);LXg();XFb(this,UI(JXg.Tf(this),166))}
function IXg(){}
_=MXg.prototype=IXg.prototype=new UFb;_.gC=function NXg(){return z9};_.cM={70:1,78:1,105:1,133:1,136:1,137:1,164:1,166:1,487:1};var JXg;function PXg(){}
function QXg(){var a,b,c,d,e,f,g,i,j,k;a=new _Xg;a.fA();d=new NHb;e=new NHb;f=new NHb;i=new qKb;j=new SKb;g=new NHb;k=new VLb;c=new EIb;b=new NGb((eu(),bu));MHb(d,'Header');zGb(b,d,21.3);MHb(e,'Equation Editor');BGb(j,e,128);MHb(f,'Graphics view');SJb(i,f);yGb(j,i);CIb(c,(dIb(),_Hb));MHb(g,'Command line');zIb(c,g);zIb(c,k);AGb(b,c,10);yGb(b,j);b.Yc(yGj);b.$c(yGj);a.fA().Kf();return b}
function RXg(){wb.call(this);PXg()}
function OXg(){}
_=RXg.prototype=OXg.prototype=new sb;_.Tf=function SXg(a){return QXg(UI(a,487))};_.gC=function TXg(){return y9};function XXg(){XXg=xxj;VXg=new _Xg}
function YXg(){}
function ZXg(){return jYg()}
function $Xg(a){WXg=new fYg(a)}
function _Xg(){XXg();wb.call(this);YXg()}
function UXg(){}
_=_Xg.prototype=UXg.prototype=new sb;_.gC=function aYg(){return x9};_.fA=function bYg(){return ZXg()};var VXg,WXg=null;function dYg(){}
function eYg(){return '.GALL4V3CEI{font-weight:bold;}'}
function fYg(a){this,a;wb.call(this);dYg()}
function cYg(){}
_=fYg.prototype=cYg.prototype=new sb;_.Kf=function gYg(){if(!this.a){this.a=true;lv(eYg());return true}return false};_.gC=function hYg(){return w9};_.cM={94:1};_.a=false;function iYg(){iYg=xxj;$Xg((XXg(),VXg))}
function jYg(){iYg();return XXg(),WXg}
var zJ=iMh(zGj,AGj,'rb',Gbb),sJ=iMh(zGj,'Animation$1','Pb',Gbb),yJ=iMh(zGj,'AnimationScheduler','Ub',Gbb),tJ=iMh(zGj,'AnimationScheduler$AnimationHandle','Zb',Gbb),xJ=iMh(zGj,'AnimationSchedulerImpl','bc',yJ),wJ=iMh(zGj,'AnimationSchedulerImplTimer','hc',xJ),vJ=iMh(zGj,'AnimationSchedulerImplTimer$AnimationHandleImpl','Jc',tJ),Tqb=hMh('[Lcom.google.gwt.animation.client.','AnimationSchedulerImplTimer$AnimationHandleImpl;','tI',vJ),uJ=iMh(zGj,'AnimationSchedulerImplTimer$1','pc',IM),JJ=iMh(iFj,'Duration','vi',Gbb),oK=jMh(nFj,'Style$Display','_r',vbb,ks,js),arb=hMh(sFj,'Style$Display;','tI',oK),kK=jMh(nFj,'Style$Display$1','ls',oK,null,null),lK=jMh(nFj,'Style$Display$2','qs',oK,null,null),mK=jMh(nFj,'Style$Display$3','vs',oK,null,null),nK=jMh(nFj,'Style$Display$4','As',oK,null,null),tK=jMh(nFj,'Style$Overflow','Hs',vbb,Ss,Rs),brb=hMh(sFj,'Style$Overflow;','tI',tK),pK=jMh(nFj,'Style$Overflow$1','Ts',tK,null,null),qK=jMh(nFj,'Style$Overflow$2','Ys',tK,null,null),rK=jMh(nFj,'Style$Overflow$3','bt',tK,null,null),sK=jMh(nFj,'Style$Overflow$4','gt',tK,null,null),yK=jMh(nFj,'Style$Position','nt',vbb,yt,xt),crb=hMh(sFj,'Style$Position;','tI',yK),uK=jMh(nFj,'Style$Position$1','zt',yK,null,null),vK=jMh(nFj,'Style$Position$2','Et',yK,null,null),wK=jMh(nFj,'Style$Position$3','Jt',yK,null,null),xK=jMh(nFj,'Style$Position$4','Ot',yK,null,null),JK=iMh(nFj,'StyleInjector$1','pv',Gbb),LK=iMh(nFj,'StyleInjector$StyleInjectorImpl','uv',Gbb),KK=iMh(nFj,'StyleInjector$StyleInjectorImplIE','Gv',LK),iL=iMh(BGj,'TouchEvent$TouchSupportDetector','UA',Gbb),oL=iMh(vFj,'ResizeEvent','OB',qL),eM=iMh(CGj,'Layout','Dub',Gbb),_L=iMh(CGj,'Layout$1','Rub',zJ),aM=jMh(CGj,'Layout$Alignment','Zub',vbb,hvb,gvb),hrb=hMh('[Lcom.google.gwt.layout.client.','Layout$Alignment;','tI',aM),bM=iMh(CGj,'Layout$Layer','kvb',Gbb),dM=iMh(CGj,'LayoutImpl','wvb',Gbb),cM=iMh(CGj,'LayoutImplIE8','Mvb',dM),pM=iMh(DGj,'DefaultMomentum','Jxb',Gbb),qM=iMh(DGj,'Momentum$State','Qxb',Gbb),rM=iMh(DGj,EGj,'byb',Gbb),CM=iMh(DGj,'TouchScroller','oyb',Gbb),sM=iMh(DGj,'TouchScroller$1','Syb',Gbb),tM=iMh(DGj,'TouchScroller$2','Xyb',Gbb),uM=iMh(DGj,'TouchScroller$3','azb',Gbb),vM=iMh(DGj,'TouchScroller$4','fzb',Gbb),wM=iMh(DGj,'TouchScroller$5','kzb',Gbb),xM=iMh(DGj,'TouchScroller$6','pzb',Gbb),zM=iMh(DGj,'TouchScroller$MomentumCommand','uzb',Gbb),yM=iMh(DGj,'TouchScroller$MomentumCommand$1','Azb',Gbb),AM=iMh(DGj,'TouchScroller$MomentumTouchRemovalCommand','Fzb',Gbb),BM=iMh(DGj,'TouchScroller$TemporalPoint','Kzb',Gbb),GM=iMh(aFj,'Event$NativePreviewEvent','fBb',qL),SM=iMh(DFj,'WindowImplIE$2','QDb',Gbb),UM=iMh(DFj,'WindowImplIE_Resources_default_InlineClientBundleGenerator$2','oEb',Gbb),fN=iMh(cFj,'Composite','UFb',YN),eN=iMh(cFj,'Composite_HTMLTemplatesImpl','gGb',Gbb),kN=iMh(cFj,'DockLayoutPanel','wGb',dN),hN=jMh(cFj,'DockLayoutPanel$Direction','UGb',vbb,gHb,fHb),jrb=hMh(EFj,'DockLayoutPanel$Direction;','tI',hN),uN=iMh(cFj,'LayoutCommand','kHb',Gbb),iN=iMh(cFj,'DockLayoutPanel$DockAnimateCommand','jHb',uN),jN=iMh(cFj,'DockLayoutPanel$LayoutData','wHb',Gbb),tN=iMh(cFj,'LayoutCommand$1','HIb',Gbb),BN=iMh(cFj,'ScrollImpl','yJb',Gbb),AN=iMh(cFj,'ScrollImpl$ScrollImplTrident','IJb',BN),CN=iMh(cFj,'ScrollPanel','PJb',EN),JN=iMh(cFj,'SplitLayoutPanel','OKb',kN),HN=iMh(cFj,'SplitLayoutPanel$Splitter','YKb',YN),FN=iMh(cFj,'SplitLayoutPanel$HSplitter','XKb',HN),GN=iMh(cFj,'SplitLayoutPanel$Splitter$1','lLb',Gbb),IN=iMh(cFj,'SplitLayoutPanel$VSplitter','qLb',HN),z9=iMh(FGj,'GeoGebraAppFrame','IXg',fN),y9=iMh(FGj,'GeoGebraAppFrame_GeoGebraAppFrameUiBinderImpl','OXg',Gbb),x9=iMh(FGj,'GeoGebraAppFrame_GeoGebraAppFrameUiBinderImpl_GenBundle_default_InlineClientBundleGenerator','UXg',Gbb),w9=iMh(FGj,'GeoGebraAppFrame_GeoGebraAppFrameUiBinderImpl_GenBundle_default_InlineClientBundleGenerator$1','cYg',Gbb);MBj(_j)(1);