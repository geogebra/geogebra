var $Ej='100%',_Ej='com.google.gwt.animation.client.',cFj='com.google.gwt.layout.client.',dFj='com.google.gwt.touch.client.',NEj='display',fFj='geogebra.web.gui.app.',WEj='zoom';function Bb(a){a.d=new Rb(a)}
function Cb(a){if(!a.g){return}a.p=a.i;a.f=null;a.g=false;a.i=false;if(LI(a.j)){a.j.Rc();a.j=null}a.Lc()}
function Db(a){return (1+zMh(3.141592653589793+a*3.141592653589793))/2}
function Eb(a,b){return a.g&&a.k==b}
function Fb(a){a.Oc(Db(0))}
function Gb(a,b,c){Hb(a,b,Ni(),c)}
function Hb(a,b,c,d){Cb(a);a.g=true;a.i=false;a.e=b;a.o=c;a.f=d;++a.k;a.d.Pc(Ni())}
function Ib(a,b){var c,d,e;c=a.k;d=b>=a.o+a.e;if(a.i&&!d){e=(b-a.o)/a.e;a.Oc(Db(e));return Eb(a,c)}if(!a.i&&b>=a.o){a.i=true;a.Nc();if(!Eb(a,c)){return false}}if(d){a.g=false;a.i=false;a.Mc();return false}return true}
function Jb(){Kb.call(this,Xb())}
function Kb(a){wb.call(this);Bb(this);this.n=a}
function rb(){}
_=rb.prototype=new sb;_.gC=function Lb(){return iJ};_.Lc=function Mb(){this.p&&this.Mc()};_.Mc=function Nb(){this.Oc(Db(1))};_.Nc=function Ob(){Fb(this)};_.e=-1;_.f=null;_.g=false;_.i=false;_.j=null;_.k=-1;_.n=null;_.o=-1;_.p=false;function Qb(){}
function Rb(a){this.b=a;wb.call(this);Qb()}
function Pb(){}
_=Rb.prototype=Pb.prototype=new sb;_.Pc=function Sb(a){Ib(this.b,a)?(this.b.j=this.b.n.Qc(this.b.d,this.b.f)):(this.b.j=null)};_.gC=function Tb(){return _I};_.b=null;function Vb(){}
function Wb(){wb.call(this);Vb()}
function Xb(){return dc(),cc}
function Ub(){}
_=Ub.prototype=new sb;_.gC=function Yb(){return hJ};function $b(){}
function _b(){wb.call(this);$b()}
function Zb(){}
_=Zb.prototype=new sb;_.gC=function ac(){return aJ};function dc(){dc=Xvj;var a;{a=new Tc;FI(a,6)&&(BI(a,6).Sc()||(a=new lc));cc=a}}
function ec(){}
function fc(){Wb.call(this);ec()}
function bc(){}
_=bc.prototype=new Ub;_.gC=function gc(){return gJ};_.cM={6:1};var cc=null;function ic(a){a.b=new B6b;a.c=new Gc(a)}
function jc(a,b){a.b.hl(b);a.b.jl()==0&&uc(a.c)}
function kc(a){var b,c,d,e,f,g;b=lI(wqb,{8:1,515:1,538:1},7,a.b.jl(),0);b=BI(a.b.ll(b),8);c=new Mi;for(e=b,f=0,g=e.length;f<g;++f){d=e[f];a.b.hl(d);Lc(d).Pc(Li(c))}a.b.jl()>0&&vc(a.c,GMh(5,16-Ki(c)))}
function lc(){fc.call(this);ic(this)}
function hc(){}
_=lc.prototype=hc.prototype=new bc;_.gC=function mc(){return dJ};_.Sc=function nc(){return true};_.Qc=function oc(a,b){var c;c=new Mc(this,a);this.b.cl(c);this.b.jl()==1&&vc(this.c,16);return c};_.cM={6:1};function Fc(){}
function Gc(a){sc();this.b=a;xc.call(this);Fc()}
function pc(){}
_=Gc.prototype=pc.prototype=new qc;_.gC=function Hc(){return bJ};_.Uc=function Ic(){kc(this.b)};_.cM={109:1};_.b=null;function Kc(){}
function Lc(a){return a.b}
function Mc(a,b){this.c=a;_b.call(this);Kc();this.b=b}
function Jc(){}
_=Mc.prototype=Jc.prototype=new Zb;_.Rc=function Nc(){jc(this.c,this)};_.gC=function Oc(){return cJ};_.cM={7:1};_.b=null;_.c=null;function Qc(){}
function Rc(a){$wnd.webkitCancelRequestAnimationFrame(a)}
function Sc(b,c){var d=b;var e=kAj(function(a){a=a||Ni();d.Pc(a)});return $wnd.webkitRequestAnimationFrame(e,c)}
function Tc(){fc.call(this);Qc()}
function Pc(){}
_=Tc.prototype=Pc.prototype=new bc;_.gC=function Uc(){return fJ};_.Sc=function Vc(){return !!($wnd.webkitRequestAnimationFrame&&$wnd.webkitCancelRequestAnimationFrame)};_.Qc=function Wc(a,b){var c;c=Sc(a,b);return new Zc(this,c)};_.cM={6:1};function Yc(){}
function Zc(a,b){this.c=a;_b.call(this);Yc();this.b=b}
function Xc(){}
_=Zc.prototype=Xc.prototype=new Zb;_.Rc=function $c(){Rc(this.b)};_.gC=function _c(){return eJ};_.b=0;_.c=null;_=dd.prototype;_.Yc=function Ad(){throw new dQh};function Jd(a,b){return Ld(a,b,jB())}
function Ld(a,b,c){return HB(Nd(a),c,b)}
function Pd(a){return a.r}
function Yd(a,b){a.r=b}
_=cd.prototype;_.ad=function ae(){return this};function Ji(a){a.b=Ni()}
function Ki(a){return Pi(Ni()-a.b)}
function Li(a){return a.b}
function Mi(){wb.call(this);Ji(this)}
function Pi(a){return a}
function Ii(){}
_=Mi.prototype=Ii.prototype=new sb;_.gC=function Oi(){return sJ};function uj(b,a){return b.join(a)}
function wj(b,a){b[b.length]=a}
function yj(b,a){b.length=a}
_=tl.prototype;_.sd=function Kl(a){this.b=Hl(this.b,dm(a));Bl(this)};_.td=function Ll(a){this.d=Hl(this.d,dm(a))};_.ud=function Ml(a,b){Cl(a,b)};function dm(a){return [a,false]}
function gn(a){return a.firstChild}
function ln(c,a,b){return c.insertBefore(a,b)}
function on(a){var b;b=kn(a);LI(b)&&nn(b,a)}
function wn(a){return a.clientHeight}
function Cn(a){return a.offsetHeight||0}
function En(a){return a.offsetWidth||0}
function Hn(a){return a.scrollHeight||0}
function Wn(a,b){(ho(),go).je(a,b)}
function Xn(b,a){b.scrollTop=a}
function lo(a,b){a.opacity=b}
function Co(a){return a.touches}
function Eo(a,b){a.scrollLeft=b}
function Ho(a){return a.pageX}
function Io(a){return a.pageY}
_=fo.prototype;_.je=function Xo(a,b){Eo(a,b)};_=ip.prototype;_.je=function zp(a,b){kp(a)&&(b+=Kn(a)-xn(a));Eo(a,b)};function Mp(a){return jo((ho(),go,a),gCj)}
function Np(a){return jo((ho(),go,a),MEj)}
function Wp(a){return wn(eq(a))}
function Xp(a){return xn(eq(a))}
function aq(a){return Hn(eq(a))}
function dq(a){return Kn(eq(a))}
function Cq(a){return Co((ho(),go,a))}
function Lq(a){Qq(a,hCj)}
function Mq(a){Qq(a,NEj)}
function Nq(a){Qq(a,oAj)}
function Oq(a){Qq(a,$Aj)}
function Pq(a){Qq(a,VAj)}
function Qq(a,b){er(a,b,tAj)}
function Rq(a){Qq(a,mBj)}
function Sq(a){Qq(a,_Aj)}
function Tq(a){Qq(a,qAj)}
function Xq(a,b,c){fr(a,hCj,b,c)}
function Yq(a,b){er(a,NEj,b.le())}
function _q(a,b,c){fr(a,'margin',b,c)}
function ar(a,b){lo((ho(),go,a),b)}
function br(a,b){er(a,fCj,b.le())}
function cr(a,b,c){fr(a,'padding',b,c)}
function dr(a,b){er(a,VAj,b.le())}
function hr(a,b,c){fr(a,b,c,(du(),cu))}
function ir(a,b,c){fr(a,mBj,b,c)}
function lr(a,b){er(a,'zIndex',b+tAj)}
function es(){es=Xvj;ds=new ms(aBj,0);as=new rs('BLOCK',1);bs=new ws('INLINE',2);cs=new Bs('INLINE_BLOCK',3);_r=oI(Fqb,{515:1,538:1},22,[ds,as,bs,cs])}
function fs(){}
function gs(a,b){sg.call(this,a,b);fs()}
function is(a){es();return Bg((Fs(),Es),a)}
function js(){es();return _r}
function $r(){}
_=$r.prototype=new lg;_.gC=function hs(){return $J};_.cM={22:1,26:1,515:1,525:1,527:1};var _r,as,bs,cs,ds;function ls(){}
function ms(a,b){gs.call(this,a,b);ls()}
function ks(){}
_=ms.prototype=ks.prototype=new $r;_.gC=function ns(){return WJ};_.le=function os(){return cBj};_.cM={22:1,26:1,515:1,525:1,527:1};function qs(){}
function rs(a,b){gs.call(this,a,b);qs()}
function ps(){}
_=rs.prototype=ps.prototype=new $r;_.gC=function ss(){return XJ};_.le=function ts(){return OEj};_.cM={22:1,26:1,515:1,525:1,527:1};function vs(){}
function ws(a,b){gs.call(this,a,b);vs()}
function us(){}
_=ws.prototype=us.prototype=new $r;_.gC=function xs(){return YJ};_.le=function ys(){return 'inline'};_.cM={22:1,26:1,515:1,525:1,527:1};function As(){}
function Bs(a,b){gs.call(this,a,b);As()}
function zs(){}
_=Bs.prototype=zs.prototype=new $r;_.gC=function Cs(){return ZJ};_.le=function Ds(){return 'inline-block'};_.cM={22:1,26:1,515:1,525:1,527:1};function Fs(){Fs=Xvj;Es=ug((es(),_r))}
var Es;function Ms(){Ms=Xvj;Ls=new Us('VISIBLE',0);Js=new Zs(bBj,1);Ks=new ct('SCROLL',2);Is=new ht(PEj,3);Hs=oI(Gqb,{515:1,538:1},28,[Ls,Js,Ks,Is])}
function Ns(){}
function Os(a,b){sg.call(this,a,b);Ns()}
function Qs(a){Ms();return Bg((lt(),kt),a)}
function Rs(){Ms();return Hs}
function Gs(){}
_=Gs.prototype=new lg;_.gC=function Ps(){return dK};_.cM={26:1,28:1,515:1,525:1,527:1};var Hs,Is,Js,Ks,Ls;function Ts(){}
function Us(a,b){Os.call(this,a,b);Ts()}
function Ss(){}
_=Us.prototype=Ss.prototype=new Gs;_.gC=function Vs(){return _J};_.le=function Ws(){return QEj};_.cM={26:1,28:1,515:1,525:1,527:1};function Ys(){}
function Zs(a,b){Os.call(this,a,b);Ys()}
function Xs(){}
_=Zs.prototype=Xs.prototype=new Gs;_.gC=function $s(){return aK};_.le=function _s(){return dBj};_.cM={26:1,28:1,515:1,525:1,527:1};function bt(){}
function ct(a,b){Os.call(this,a,b);bt()}
function at(){}
_=ct.prototype=at.prototype=new Gs;_.gC=function dt(){return bK};_.le=function et(){return TBj};_.cM={26:1,28:1,515:1,525:1,527:1};function gt(){}
function ht(a,b){Os.call(this,a,b);gt()}
function ft(){}
_=ht.prototype=ft.prototype=new Gs;_.gC=function it(){return cK};_.le=function jt(){return REj};_.cM={26:1,28:1,515:1,525:1,527:1};function lt(){lt=Xvj;kt=ug((Ms(),Hs))}
var kt;function st(){st=Xvj;rt=new At('STATIC',0);qt=new Ft('RELATIVE',1);ot=new Kt('ABSOLUTE',2);pt=new Pt(SEj,3);nt=oI(Hqb,{515:1,538:1},29,[rt,qt,ot,pt])}
function tt(){}
function ut(a,b){sg.call(this,a,b);tt()}
function wt(a){st();return Bg((Tt(),St),a)}
function xt(){st();return nt}
function mt(){}
_=mt.prototype=new lg;_.gC=function vt(){return iK};_.cM={26:1,29:1,515:1,525:1,527:1};var nt,ot,pt,qt,rt;function zt(){}
function At(a,b){ut.call(this,a,b);zt()}
function yt(){}
_=At.prototype=yt.prototype=new mt;_.gC=function Bt(){return eK};_.le=function Ct(){return 'static'};_.cM={26:1,29:1,515:1,525:1,527:1};function Et(){}
function Ft(a,b){ut.call(this,a,b);Et()}
function Dt(){}
_=Ft.prototype=Dt.prototype=new mt;_.gC=function Gt(){return fK};_.le=function Ht(){return eCj};_.cM={26:1,29:1,515:1,525:1,527:1};function Jt(){}
function Kt(a,b){ut.call(this,a,b);Jt()}
function It(){}
_=Kt.prototype=It.prototype=new mt;_.gC=function Lt(){return gK};_.le=function Mt(){return YAj};_.cM={26:1,29:1,515:1,525:1,527:1};function Ot(){}
function Pt(a,b){ut.call(this,a,b);Ot()}
function Nt(){}
_=Pt.prototype=Nt.prototype=new mt;_.gC=function Qt(){return hK};_.le=function Rt(){return WAj};_.cM={26:1,29:1,515:1,525:1,527:1};function Tt(){Tt=Xvj;St=ug((st(),nt))}
var St;function hv(){hv=Xvj;ev=bf(gf());fv=bf(gf());gv=bf(gf());cv=new pv}
function iv(a){hv();var b,c,d;d=null;if(vj(gv)!=0){b=uj(gv,tAj);c=(uv(),tv).pe(b);NI(gv,a)&&(d=c);yj(gv,0)}if(vj(ev)!=0){b=uj(ev,tAj);c=(uv(),tv).ne(b);NI(ev,a)&&(d=c);yj(ev,0)}if(vj(fv)!=0){b=uj(fv,tAj);c=(uv(),tv).oe(b);NI(fv,a)&&(d=c);yj(fv,0)}dv=false;return d}
function jv(a){hv();kv(a,false)}
function kv(a,b){wj(ev,a);lv(b)}
function lv(a){a?iv(null):mv()}
function mv(){if(!dv){dv=true;Oj().td(cv)}}
var cv,dv=false,ev,fv,gv;function ov(){}
function pv(){wb.call(this);ov()}
function nv(){}
_=pv.prototype=nv.prototype=new sb;_.xd=function qv(){(hv(),dv)&&iv(null)};_.gC=function rv(){return tK};function uv(){uv=Xvj;tv=new zv}
function vv(){}
function wv(a,b){var c;c=Np(gq());Vn(c,qDj,'text/css');yv(c,b);return c}
function xv(a){var b;if(MI(a.b)){b=Gq(_p(gq(),JAj),0);a.b=iq(b)}return a.b}
function yv(a,b){Un(a,b)}
function zv(){wb.call(this);vv()}
function sv(){}
_=zv.prototype=sv.prototype=new sb;_.gC=function Av(){return uK};_.ne=function Bv(a){var b;b=wv(this,a);en(xv(this),b);return b};_.oe=function Cv(a){return this.ne(a)};_.pe=function Dv(a){var b;b=wv(this,a);ln(xv(this),b,gn(this.b));return b};_.b=null;var tv;function Gv(a){return Ho((ho(),go,a))}
function Hv(a){return Io((ho(),go,a))}
function dA(a){return Cq(ew(a))}
function gA(){MI(aA)&&(aA=new EA);return aA.Qe()}
var aA=null;function CA(a){a.b=DA()}
function DA(){var a=document.createElement(uAj);a.setAttribute('ontouchstart','return;');return typeof a.ontouchstart==MAj}
function EA(){wb.call(this);CA(this)}
function BA(){}
_=EA.prototype=BA.prototype=new sb;_.gC=function FA(){return TK};_.Qe=function GA(){return this.b};function dB(a){return a.b}
function jB(){MI(aB)&&(aB=new _w);return aB}
function xB(){}
function yB(a,b){b.Ve(a)}
function zB(a,b){Zv.call(this);xB();this,a;this,b}
function BB(a,b,c){var d;if(LI(wB)){d=new zB(b,c);a.dd(d)}}
function EB(){MI(wB)&&(wB=new _w);return wB}
function vB(){}
_=zB.prototype=vB.prototype=new Kv;_.se=function AB(a){yB(this,BI(a,72))};_.te=function CB(){return wB};_.gC=function DB(){return ZK};var wB=null;function hub(a){a.c=new jvb;a.d=new B6b}
function iub(a,b,c){var d,e,f;d=c.j*lub(a,c.k,false);e=c.n*lub(a,c.o,false);f=c.Y*lub(a,c.Z,false);if(c.r&&!c.v){c.r=false;if(c.A){c.w=true;c.E=(b-(d+f))/lub(a,c.O,false)}else{c.y=true;c.G=(b-(d+e))/lub(a,c.S,false)}}else if(c.A&&!c.y){c.A=false;if(c.r){c.w=true;c.E=(b-(d+f))/lub(a,c.O,false)}else{c.v=true;c.D=(b-(e+f))/lub(a,c.M,false)}}else if(c.s&&!c.w){c.s=false;if(c.A){c.v=true;c.D=(b-(e+f))/lub(a,c.M,false)}else{c.y=true;c.G=(b-(d+e))/lub(a,c.S,false)}}c.r=c.v;c.s=c.w;c.A=c.y;c.k=c.M;c.o=c.O;c.Z=c.S}
function jub(a,b,c){var d,e,f;f=c.T*lub(a,c.U,true);d=c.b*lub(a,c.c,true);e=c.g*lub(a,c.i,true);if(c.z&&!c.x){c.z=false;if(c.q){c.t=true;c.B=(b-(f+e))/lub(a,c.I,true)}else{c.u=true;c.C=(b-(f+d))/lub(a,c.K,true)}}else if(c.q&&!c.u){c.q=false;if(c.z){c.t=true;c.B=(b-(f+e))/lub(a,c.I,true)}else{c.x=true;c.F=(b-(d+e))/lub(a,c.Q,true)}}else if(c.p&&!c.t){c.p=false;if(c.q){c.x=true;c.F=(b-(d+e))/lub(a,c.Q,true)}else{c.u=true;c.C=(b-(f+d))/lub(a,c.K,true)}}c.z=c.x;c.p=c.t;c.q=c.u;c.U=c.Q;c.c=c.I;c.i=c.K}
function kub(a,b,c,d){var e,f;e=cvb(a.c,a.e,b,c);f=new Yub(a,e,b,d);a.d.cl(f);return f}
function lub(a,b,c){return fvb(a.c,a.e,b,c)}
function mub(a){nub(a,0)}
function nub(a,b){oub(a,b,null)}
function oub(a,b,c){var d,e,f,g;LI(a.b)&&Cb(a.b);if(b==0){for(e=a.d.lf();e.Dg();){d=BI(e.Eg(),92);d.j=d.D=d.L;d.T=d.F=d.P;d.n=d.E=d.N;d.b=d.B=d.H;d.Y=d.G=d.R;d.g=d.C=d.J;d.r=d.v;d.z=d.x;d.s=d.w;d.p=d.t;d.A=d.y;d.q=d.u;d.k=d.M;d.U=d.Q;d.o=d.O;d.c=d.I;d.Z=d.S;d.i=d.K;a.c.If(d)}evb();LI(c)&&c.Gf();return}g=xn(a.e);f=wn(a.e);for(e=a.d.lf();e.Dg();){d=BI(e.Eg(),92);iub(a,g,d);jub(a,f,d)}a.b=new xub(a,c);Gb(a.b,b,a.e)}
function pub(a){hvb()}
function qub(a){a.c.Jf(a.e)}
function rub(a,b){ivb(b.e,b.d);a.d.hl(b)}
function sub(a){wb.call(this);hub(this);this.e=a;gvb(this.c,a)}
function gub(){}
_=sub.prototype=gub.prototype=new sb;_.gC=function tub(){return OL};_.b=null;_.e=null;function vub(){}
function wub(a){a.b.b=null;mub(a.b);LI(a.c)&&a.c.Gf()}
function xub(a,b){this.b=a;this.c=b;Jb.call(this);vub()}
function uub(){}
_=xub.prototype=uub.prototype=new rb;_.gC=function yub(){return KL};_.Lc=function zub(){wub(this)};_.Mc=function Aub(){wub(this)};_.Oc=function Bub(a){var b,c;for(c=this.b.d.lf();c.Dg();){b=BI(c.Eg(),92);b.v&&(b.j=b.D+(b.L-b.D)*a);b.w&&(b.n=b.E+(b.N-b.E)*a);b.x&&(b.T=b.F+(b.P-b.F)*a);b.t&&(b.b=b.B+(b.H-b.B)*a);b.y&&(b.Y=b.G+(b.R-b.G)*a);b.u&&(b.g=b.C+(b.J-b.C)*a);this.b.c.If(b);LI(this.c)&&this.c.Hf(b,a)}evb()};_.b=null;_.c=null;function Hub(){Hub=Xvj;Eub=new Jub('BEGIN',0);Fub=new Jub('END',1);Gub=new Jub('STRETCH',2);Dub=oI(Mqb,{515:1,538:1},91,[Eub,Fub,Gub])}
function Iub(){}
function Jub(a,b){sg.call(this,a,b);Iub()}
function Lub(a){Hub();return Bg((Oub(),Nub),a)}
function Mub(){Hub();return Dub}
function Cub(){}
_=Jub.prototype=Cub.prototype=new lg;_.gC=function Kub(){return LL};_.cM={91:1,515:1,525:1,527:1};var Dub,Eub,Fub,Gub;function Oub(){Oub=Xvj;Nub=ug((Hub(),Dub))}
var Nub;function Qub(a){a.M=(du(),cu);a.Q=(du(),cu);a.O=(du(),cu);a.I=(du(),cu);a.f=(Hub(),Gub);a.W=(Hub(),Gub)}
function Rub(a){return a.V}
function Sub(a,b,c,d,e){a.t=a.u=true;a.x=false;a.H=b;a.J=d;a.I=c;a.K=e}
function Tub(a,b,c,d,e){a.v=a.w=true;a.y=false;a.L=b;a.N=d;a.M=c;a.O=e}
function Uub(a,b,c,d,e){a.v=a.y=true;a.w=false;a.L=b;a.R=d;a.M=c;a.S=e}
function Vub(a,b,c,d,e){a.w=a.y=true;a.v=false;a.N=b;a.R=d;a.O=c;a.S=e}
function Wub(a,b,c,d,e){a.x=a.t=true;a.u=false;a.P=b;a.H=d;a.Q=c;a.I=e}
function Xub(a,b,c,d,e){a.x=a.u=true;a.t=false;a.P=b;a.J=d;a.Q=c;a.K=e}
function Yub(a,b,c,d){this,a;wb.call(this);Qub(this);this.e=b;this.d=c;this.V=d}
function Pub(){}
_=Yub.prototype=Pub.prototype=new sb;_.gC=function Zub(){return ML};_.cM={92:1};_.b=0;_.c=null;_.d=null;_.e=null;_.g=0;_.i=null;_.j=0;_.k=null;_.n=0;_.o=null;_.p=false;_.q=false;_.r=false;_.s=false;_.t=true;_.u=false;_.v=true;_.w=true;_.x=true;_.y=false;_.z=false;_.A=false;_.B=0;_.C=0;_.D=0;_.E=0;_.F=0;_.G=0;_.H=0;_.J=0;_.K=null;_.L=0;_.N=0;_.P=0;_.R=0;_.S=null;_.T=0;_.U=null;_.V=null;_.X=true;_.Y=0;_.Z=null;function avb(){avb=Xvj;_ub=kvb((du(),Wt),(du(),Wt));en(Tp(gq()),_ub)}
function bvb(){}
function cvb(a,b,c,d){var e,f;f=Ip(gq());en(f,c);dr(Mn(f),(st(),ot));br(Mn(f),(Ms(),Js));dvb(c);e=null;LI(d)&&(e=kn(d));ln(b,f,e);return f}
function dvb(a){var b;b=Mn(a);dr(b,(st(),ot));$q(b,0,(du(),cu));jr(b,0,(du(),cu));ir(b,0,(du(),cu));Xq(b,0,(du(),cu))}
function evb(){}
function fvb(a,b,c,d){if(MI(c)){return 1}switch(qg(c)){case 1:return (d?wn(b):xn(b))/100;case 2:return En(a.b)/10;case 3:return Cn(a.b)/10;case 7:return En(_ub)*0.1;case 8:return En(_ub)*0.01;case 6:return En(_ub)*0.254;case 4:return En(_ub)*0.00353;case 5:return En(_ub)*0.0423;default:case 0:return 1;}}
function gvb(a,b){dr(Mn(b),(st(),qt));en(b,a.b=kvb((du(),Xt),(du(),Yt)))}
function hvb(){}
function ivb(a,b){var c;on(a);NI(kn(b),a)&&on(b);c=Mn(b);Pq(c);Oq(c);Sq(c);Tq(c);Nq(c)}
function jvb(){avb();wb.call(this);bvb()}
function kvb(a,b){var c,d;c=Ip(gq());Tn(c,TEj);d=Mn(c);dr(d,(st(),ot));lr(d,-32767);jr(d,-20,b);kr(d,10,a);Zq(d,10,b);return c}
function $ub(){}
_=jvb.prototype=$ub.prototype=new sb;_.gC=function lvb(){return NL};_.If=function mvb(a){var b;b=Mn(a.e);a.X?Mq(b):Yq(b,(es(),ds));er(b,$Aj,a.r?a.j+a.k.me():tAj);er(b,_Aj,a.z?a.T+a.U.me():tAj);er(b,mBj,a.s?a.n+a.o.me():tAj);er(b,hCj,a.p?a.b+a.c.me():tAj);er(b,qAj,a.A?a.Y+a.Z.me():tAj);er(b,oAj,a.q?a.g+a.i.me():tAj);b=Mn(a.d);switch(qg(a.f)){case 0:$q(b,0,(du(),cu));Rq(b);break;case 1:Oq(b);ir(b,0,(du(),cu));break;case 2:$q(b,0,(du(),cu));ir(b,0,(du(),cu));}switch(qg(a.W)){case 0:jr(b,0,(du(),cu));Lq(b);break;case 1:Sq(b);Xq(b,0,(du(),cu));break;case 2:jr(b,0,(du(),cu));Xq(b,0,(du(),cu));}};_.Jf=function nvb(a){};_.b=null;var _ub=null;function axb(){}
function bxb(a,b,c,d){var e,f,g;g=a*b;if(c>=0){e=FMh(0,c-d);g=IMh(g,e)}else{f=IMh(0,c+d);g=FMh(g,f)}return g}
function cxb(){wb.call(this);axb()}
function _wb(){}
_=cxb.prototype=_wb.prototype=new sb;_.Qf=function dxb(a,b){return new rxb(a,b)};_.gC=function exb(){return ZL};_.Rf=function fxb(a){var b,c,d,e,f,g,i,j,k,n,o,p;e=jxb(a);p=ixb(a);f=kxb(a);n=mxb(a);b=KMh(0.9993,p);g=e*5.0E-4;j=bxb(vxb(f),b,vxb(n),g);k=bxb(wxb(f),b,wxb(n),g);i=new Axb(j,k);qxb(a,i);d=jxb(a);c=yxb(i,new Axb(d,d));o=lxb(a);pxb(a,zxb(o,c));if(rMh(vxb(i))<0.02&&rMh(wxb(i))<0.02){return false}return true};function hxb(a){}
function ixb(a){return a.b}
function jxb(a){return a.c}
function kxb(a){return a.d}
function lxb(a){return a.e}
function mxb(a){return a.f}
function nxb(a,b){a.b=b}
function oxb(a,b){a.c=b}
function pxb(a,b){a.e=b}
function qxb(a,b){a.f=b}
function rxb(a,b){wb.call(this);hxb(this);this,a;this.d=b;this.e=new Bxb(a);this.f=new Bxb(b)}
function gxb(){}
_=rxb.prototype=gxb.prototype=new sb;_.gC=function sxb(){return $L};_.b=0;_.c=0;_.d=null;_.e=null;_.f=null;function uxb(){}
function vxb(a){return a.b}
function wxb(a){return a.c}
function xxb(a,b){return new Axb(a.b-b.b,a.c-b.c)}
function yxb(a,b){return new Axb(a.b*b.b,a.c*b.c)}
function zxb(a,b){return new Axb(a.b+b.b,a.c+b.c)}
function Axb(a,b){wb.call(this);uxb();this.b=a;this.c=b}
function Bxb(a){Axb.call(this,a.b,a.c)}
function txb(){}
_=Bxb.prototype=Axb.prototype=txb.prototype=new sb;_.eQ=function Cxb(a){var b;if(!FI(a,99)){return false}b=BI(a,99);return this.b==b.b&&this.c==b.c};_.gC=function Dxb(){return _L};_.hC=function Exb(){return UI(this.b)^UI(this.c)};_.tS=function Fxb(){return 'Point('+this.b+UEj+this.c+xDj};_.cM={99:1};_.b=0;_.c=0;function Ixb(a){a.e=new B6b;a.f=new fzb;a.n=new fzb;a.k=new fzb;a.r=new B6b;a.j=new Zyb(a)}
function Jxb(a,b){var c,d;d=dzb(b)-dzb(a);if(d<=0){return null}c=xxb(czb(a),czb(b));return new Axb(vxb(c)/d,wxb(c)/d)}
function Kxb(a){a.s=false;a.d=false;a.i=null}
function Lxb(a){var b;b=dA(a);return nj(b)>0?mj(b,0):null}
function Mxb(a){return new Axb(a.t.qg(),a.t.wg())}
function Nxb(a,b){var c,d,e;e=xxb(a,b);c=rMh(vxb(e));d=rMh(wxb(e));return c<=25&&d<=25}
function Oxb(a,b){if(LI(czb(a.k))){return Nxb(b,czb(a.k))}return false}
function Pxb(a,b){var c,d,e,f;c=Ni();f=false;for(e=a.r.lf();e.Dg();){d=BI(e.Eg(),100);if(c-dzb(d)<=2500&&Nxb(b,czb(d))){f=true;break}}return f}
function Qxb(a){return LI(a.i)}
function Rxb(a){var b;if(MI(a.g)){return}b=Jxb(a.n,a.f);if(LI(b)){a.i=new Pyb(a,b);Oj().ud(a.i,16)}}
function Sxb(a){var b,c;c=xxb(a.q,czb(a.f));b=zxb(a.p,c);ayb(a,b)}
function Txb(){}
function Uxb(a,b){Vxb(a,b)}
function Vxb(a,b){if(!a.s){return}a.s=false;if(a.d){a.d=false;Rxb(a)}}
function Wxb(a,b){var c,d,e,f,g,i,j,k,n,o,p,q,r;if(!a.s){return}j=Lxb(b);k=new Axb(Gv(j),Hv(j));n=Ni();ezb(a.f,k,n);if(!a.d){e=xxb(k,a.q);c=rMh(vxb(e));d=rMh(wxb(e));if(c>5||d>5){ezb(a.k,czb(a.n),dzb(a.n));if(c>d){i=a.t.qg();g=a.t.sg();f=a.t.rg();if(vxb(e)<0&&f<=i){Kxb(a);return}else if(vxb(e)>0&&g>=i){Kxb(a);return}}else{r=a.t.wg();q=a.t.vg();p=a.t.ug();if(wxb(e)<0&&p<=r){Kxb(a);return}else if(wxb(e)>0&&q>=r){Kxb(a);return}}a.d=true;Txb()}}fw(b);if(a.d){Sxb(a);o=n-dzb(a.n);if(o>200&&LI(a.o)){ezb(a.n,czb(a.o),dzb(a.o));a.o=null}else o>100&&MI(a.o)&&(a.o=new gzb(k,n))}}
function Xxb(a,b){var c,d;ezb(a.k,null,0);if(a.s){return}d=Lxb(b);a.q=new Axb(Gv(d),Hv(d));c=Ni();ezb(a.n,a.q,c);ezb(a.f,a.q,c);a.o=null;if(Qxb(a)){a.r.cl(new gzb(a.q,c));Oj().ud(a.j,2500)}a.p=Mxb(a);Kxb(a);a.s=true}
function Yxb(a){if(LI(a.b)){a.b.Xe();a.b=null}}
function Zxb(a){if(LI(a.c)){a.c.Xe();a.c=null}}
function $xb(a,b){a.g=b;MI(b)&&(a.i=null)}
function _xb(a,b){var c,d;if(NI(a.t,b)){return}Kxb(a);for(d=a.e.lf();d.Dg();){c=BI(d.Eg(),77);c.Xe()}a.e.el();Yxb(a);Zxb(a);a.t=b;if(LI(b)){b.ad().ed()&&byb(a);a.b=Jd(b.ad(),new kyb(a));a.e.cl(Kd(b.ad(),new pyb(a),$A()));a.e.cl(Kd(b.ad(),new uyb(a),QA()));a.e.cl(Kd(b.ad(),new zyb(a),AA()));a.e.cl(Kd(b.ad(),new Eyb(a),qA()))}}
function ayb(a,b){a.t.tg(UI(vxb(b)));a.t.xg(UI(wxb(b)))}
function byb(a){Zxb(a);a.c=qAb(new Jyb(a))}
function cyb(){wb.call(this);Ixb(this);$xb(this,new cxb)}
function dyb(){return hyb()?new cyb:null}
function eyb(a){var b;b=dyb();LI(b)&&_xb(b,a);return b}
function gyb(){var a=navigator.userAgent.toLowerCase();return /android ([3-9]+)\.([0-9]+)/.exec(a)!=null}
function hyb(){MI(Hxb)&&(Hxb=XJh(gA()&&!gyb()));return NJh(Hxb)}
function Gxb(){}
_=cyb.prototype=Gxb.prototype=new sb;_.gC=function fyb(){return kM};_.b=null;_.c=null;_.d=false;_.g=null;_.i=null;_.o=null;_.p=null;_.q=null;_.s=false;_.t=null;var Hxb=null;function jyb(){}
function kyb(a){this.b=a;wb.call(this);jyb()}
function iyb(){}
_=kyb.prototype=iyb.prototype=new sb;_.gC=function lyb(){return aM};_.Te=function myb(a){dB(a)?byb(this.b):Zxb(this.b)};_.cM={67:1,75:1};_.b=null;function oyb(){}
function pyb(a){this.b=a;wb.call(this);oyb()}
function nyb(){}
_=pyb.prototype=nyb.prototype=new sb;_.gC=function qyb(){return bM};_.Se=function ryb(a){Xxb(this.b,a)};_.cM={66:1,75:1};_.b=null;function tyb(){}
function uyb(a){this.b=a;wb.call(this);tyb()}
function syb(){}
_=uyb.prototype=syb.prototype=new sb;_.gC=function vyb(){return cM};_.Re=function wyb(a){Wxb(this.b,a)};_.cM={65:1,75:1};_.b=null;function yyb(){}
function zyb(a){this.b=a;wb.call(this);yyb()}
function xyb(){}
_=zyb.prototype=xyb.prototype=new sb;_.gC=function Ayb(){return dM};_.Pe=function Byb(a){Vxb(this.b,a)};_.cM={64:1,75:1};_.b=null;function Dyb(){}
function Eyb(a){this.b=a;wb.call(this);Dyb()}
function Cyb(){}
_=Eyb.prototype=Cyb.prototype=new sb;_.gC=function Fyb(){return eM};_.Oe=function Gyb(a){Uxb(this.b,a)};_.cM={63:1,75:1};_.b=null;function Iyb(){}
function Jyb(a){this.b=a;wb.call(this);Iyb()}
function Hyb(){}
_=Jyb.prototype=Hyb.prototype=new sb;_.gC=function Kyb(){return fM};_.Sf=function Lyb(a){var b;if(1==EAb(a)){b=new Axb(tq(DAb(a)),uq(DAb(a)));if(Oxb(this.b,b)||Pxb(this.b,b)){BAb(a);Fq(DAb(a));Eq(DAb(a))}}};_.cM={75:1,104:1};_.b=null;function Nyb(a){a.b=new Mi;a.c=Mxb(a.f)}
function Oyb(a){if(LI(a.g)){a.g.Xe();a.g=null}NI(a,a.f.i)&&(a.f.i=null)}
function Pyb(a,b){this.f=a;wb.call(this);Nyb(this);this.e=a.g.Qf(this.c,b);this.g=sBb(new Uyb(this))}
function Myb(){}
_=Pyb.prototype=Myb.prototype=new sb;_.wd=function Qyb(){var a,b,c,d,e,f,g,i;if(OI(this,this.f.i)){Oyb(this);return false}a=Ki(this.b);oxb(this.e,a-this.d);this.d=a;nxb(this.e,a);e=this.f.g.Rf(this.e);e||Oyb(this);ayb(this.f,lxb(this.e));d=UI(vxb(lxb(this.e)));c=this.f.t.sg();b=this.f.t.rg();g=this.f.t.vg();f=this.f.t.ug();i=UI(wxb(lxb(this.e)));if((f<=i||g>=i)&&(b<=d||c>=d)){Oyb(this);return false}return e};_.gC=function Ryb(){return hM};_.d=0;_.e=null;_.f=null;_.g=null;function Tyb(){}
function Uyb(a){this.b=a;wb.call(this);Tyb()}
function Syb(){}
_=Uyb.prototype=Syb.prototype=new sb;_.gC=function Vyb(){return gM};_.Ve=function Wyb(a){Oyb(this.b)};_.cM={72:1,75:1};_.b=null;function Yyb(){}
function Zyb(a){this.b=a;wb.call(this);Yyb()}
function Xyb(){}
_=Zyb.prototype=Xyb.prototype=new sb;_.wd=function $yb(){var a,b,c;a=Ni();b=this.b.r.lf();while(b.Dg()){c=BI(b.Eg(),100);a-dzb(c)>=2500&&b.Fg()}return !this.b.r.uh()};_.gC=function _yb(){return iM};_.b=null;function bzb(){}
function czb(a){return a.b}
function dzb(a){return a.c}
function ezb(a,b,c){a.b=b;a.c=c}
function fzb(){wb.call(this);bzb()}
function gzb(a,b){wb.call(this);bzb();ezb(this,a,b)}
function azb(){}
_=gzb.prototype=fzb.prototype=azb.prototype=new sb;_.gC=function hzb(){return jM};_.cM={100:1};_.b=null;_.c=0;function Hzb(){lzb();bCb(jzb)}
function pAb(a){return yzb(a)}
function qAb(a){Hzb();OAb();if(MI(oAb)){oAb=new LB(null,true);zAb=new JAb}return HB(oAb,yAb,a)}
function rAb(a){return a}
function uAb(a){Jzb(bf(a))}
function vAb(a){Lzb(bf(a))}
function wAb(a,b){Nzb(a,b)}
function AAb(a){}
function BAb(a){a.b=true}
function CAb(a,b){b.Sf(a);zAb.d=false}
function DAb(a){return a.e}
function EAb(a){return pAb(rAb(DAb(a)))}
function FAb(a){return a.b}
function GAb(a){return a.c}
function HAb(a){Yv(a);a.b=false;a.c=false;a.d=true;a.e=null}
function IAb(a,b){a.e=b}
function JAb(){Zv.call(this);AAb(this)}
function OAb(){MI(yAb)&&(yAb=new _w);return yAb}
function xAb(){}
_=JAb.prototype=xAb.prototype=new Kv;_.se=function KAb(a){CAb(this,BI(a,104))};_.te=function MAb(){return yAb};_.gC=function NAb(){return oM};_.Wf=function PAb(){return FAb(this)};_.Xf=function QAb(){return GAb(this)};_.ue=function RAb(){HAb(this)};_.Yf=function SAb(a){IAb(this,a)};_.b=false;_.c=false;_.d=false;_.e=null;function sBb(a){pBb();BBb();CBb();return rBb(EB(),a)}
function wBb(){pBb();return Wp(gq())}
function xBb(){pBb();return Xp(gq())}
function CBb(){if(Wi()&&!oBb){lBb.jg();oBb=true}}
function FBb(){pBb();var a,b;if(oBb){b=xBb();a=wBb();if(nBb!=b||mBb!=a){nBb=b;mBb=a;BB(yBb(),b,a)}}}
var mBb=0,nBb=0,oBb=false;_=LCb.prototype;_.jg=function QCb(){var b=$wnd.onresize;$wnd.onresize=kAj(function(a){try{FBb()}finally{b&&b(a)}})};function nEb(){nEb=Xvj;ed();new CEb}
function oEb(){}
function pEb(a,b){var c;if(LI(a.b)){throw new wLh('Composite.initWidget() may only be called once.')}FI(b,136)&&(a,BI(b,136));Xd(b);c=jd(b);md(a,c);kHb(c)&&gHb(hHb(c),a);a.b=b;Zd(b,a)}
function qEb(a){if(LI(a.b)){return a.b.ed()}return false}
function rEb(a){if(!Rd(a)){$d(a.b,a.p);a.p=-1}a.b.fd();Nzb(jd(a),a);a.jd();gB(a,true)}
function sEb(a,b){Td(a,b);a.b.gd(b)}
function tEb(){nEb();_d.call(this);oEb()}
function mEb(){}
_=mEb.prototype=new cd;_.gC=function uEb(){return JM};_.ed=function vEb(){return qEb(this)};_.fd=function wEb(){rEb(this)};_.gd=function xEb(a){sEb(this,a)};_.hd=function yEb(){try{this.kd();gB(this,false)}finally{this.b.hd()}};_.Yc=function zEb(){md(this,this.b.Yc());return jd(this)};_.cM={70:1,78:1,105:1,133:1,136:1,137:1,164:1,166:1};_.b=null;function BEb(){}
function CEb(){wb.call(this);BEb()}
function AEb(){}
_=CEb.prototype=AEb.prototype=new sb;_.gC=function DEb(){return IM};function REb(){}
function SEb(a,b){a.ng(b,(vFb(),oFb),0,null)}
function TEb(a,b,c){a.ng(b,(vFb(),sFb),c,null)}
function UEb(a,b,c){a.ng(b,(vFb(),tFb),c,null)}
function VEb(a,b,c){a.ng(b,(vFb(),uFb),c,null)}
function WEb(a,b){XEb(a,b,null)}
function XEb(a,b,c){HFb(a.g,b,c)}
function YEb(){}
function ZEb(a){var b,c,d,e,f,g,i,j;g=0;j=0;i=0;b=0;for(d=dDb(a).lf();d.Dg();){c=BI(d.Eg(),166);e=BI(Pd(c),121);f=e.c;switch(qg(bFb(e.b))){case 0:Tub(f,g,a.i,i,a.i);Xub(f,j,a.i,e.d,a.i);j+=e.d;break;case 2:Tub(f,g,a.i,i,a.i);Sub(f,b,a.i,e.d,a.i);b+=e.d;break;case 3:Wub(f,j,a.i,b,a.i);Uub(f,g,a.i,e.d,a.i);g+=e.d;break;case 1:Wub(f,j,a.i,b,a.i);Vub(f,i,a.i,e.d,a.i);i+=e.d;break;case 4:Tub(f,g,a.i,i,a.i);Wub(f,j,a.i,b,a.i);}}a.e=g+i;a.d=j+b}
function $Eb(a){GFb(a.g);ZEb(a);mub(a.f);dFb(a)}
function _Eb(a){return wn(jd(a))/lub(a.f,a.i,true)-a.d}
function aFb(a){return xn(jd(a))/lub(a.f,a.i,false)-a.e}
function bFb(a){if(NI(a,(vFb(),rFb))){return nF(qF())?(vFb(),pFb):(vFb(),uFb)}else if(NI(a,(vFb(),qFb))){return nF(qF())?(vFb(),uFb):(vFb(),pFb)}return a}
function cFb(a,b,c,d,e){var f,g,i,j;YEb();Xd(b);f=dDb(a);if(MI(e)){cLb(f,b)}else{i=eLb(f,e);fLb(f,b,i)}NI(c,(vFb(),oFb))&&(a.c=b);j=kub(a.f,jd(b),LI(e)?jd(e):null,b);g=new SFb(c,d,j);Yd(b,g);VCb(a,b);WEb(a,0)}
function dFb(a){var b,c;for(c=dDb(a).lf();c.Dg();){b=BI(c.Eg(),166);FI(b,150)&&BI(b,150).og()}}
function eFb(a,b){var c,d;d=jDb(a,b);if(d){NI(b,a.c)&&(a.c=null);c=BI(Pd(b),121);rub(a.f,c.c)}return d}
function fFb(a){ed();kDb.call(this);REb();this.i=a;md(this,Ip(gq()));this.f=new sub(jd(this));this.g=new NFb(this,this.f)}
function QEb(){}
_=fFb.prototype=QEb.prototype=new SCb;_.gC=function gFb(){return OM};_.ng=function hFb(a,b,c,d){cFb(this,a,b,c,d)};_.jd=function iFb(){pub(this.f)};_.og=function jFb(){dFb(this)};_.kd=function kFb(){qub(this.f)};_.kg=function lFb(a){return eFb(this,a)};_.cM={70:1,78:1,105:1,133:1,134:1,137:1,150:1,164:1,166:1};_.c=null;_.d=0;_.e=0;_.f=null;_.g=null;_.i=null;function vFb(){vFb=Xvj;sFb=new xFb('NORTH',0);pFb=new xFb('EAST',1);tFb=new xFb('SOUTH',2);uFb=new xFb('WEST',3);oFb=new xFb(VEj,4);rFb=new xFb('LINE_START',5);qFb=new xFb('LINE_END',6);nFb=oI(Oqb,{515:1,538:1},120,[sFb,pFb,tFb,uFb,oFb,rFb,qFb])}
function wFb(){}
function xFb(a,b){sg.call(this,a,b);wFb()}
function zFb(a){vFb();return Bg((CFb(),BFb),a)}
function AFb(){vFb();return nFb}
function mFb(){}
_=xFb.prototype=mFb.prototype=new lg;_.gC=function yFb(){return LM};_.cM={120:1,515:1,525:1,527:1};var nFb,oFb,pFb,qFb,rFb,sFb,tFb,uFb;function CFb(){CFb=Xvj;BFb=ug((vFb(),nFb))}
var BFb;function FFb(){}
function GFb(a){a.d=true}
function HFb(a,b,c){a.e=b;a.c=c;a.d=false;if(!a.g){a.g=true;Oj().td(a)}}
function IFb(a){wb.call(this);FFb();this.f=a}
function EFb(){}
_=EFb.prototype=new sb;_.pg=function JFb(){};_.xd=function KFb(){this.g=false;if(this.d){return}this.pg();oub(this.f,this.e,new bHb(this))};_.gC=function LFb(){return YM};_.c=null;_.d=false;_.e=0;_.f=null;_.g=false;function MFb(){}
function NFb(a,b){this.b=a;IFb.call(this,b);MFb()}
function DFb(){}
_=NFb.prototype=DFb.prototype=new EFb;_.pg=function OFb(){ZEb(this.b)};_.gC=function PFb(){return MM};_.b=null;function RFb(){}
function SFb(a,b,c){wb.call(this);RFb();this.b=a;this.d=b;this.c=c}
function QFb(){}
_=SFb.prototype=QFb.prototype=new sb;_.gC=function TFb(){return NM};_.cM={121:1};_.b=null;_.c=null;_.d=0;function ZFb(a){ed();_Fb.call(this,a?Mp(gq()):Ip(gq()),a)}
function eGb(a,b){MEb(a.c,b,false);YFb(a)}
function fGb(){bGb();ZFb.call(this,false);cGb();rd(this,'gwt-Label')}
_=fGb.prototype=VFb.prototype;function WGb(a,b){a.b=b}
function aHb(){}
function bHb(a){this.b=a;wb.call(this);aHb()}
function _Gb(){}
_=bHb.prototype=_Gb.prototype=new sb;_.gC=function cHb(){return XM};_.Gf=function dHb(){LI(this.b.c)&&this.b.c.Gf()};_.Hf=function eHb(a,b){var c;c=BI(Rub(a),166);FI(c,150)&&BI(c,150).og();LI(this.b.c)&&this.b.c.Hf(a,b)};_.b=null;function gHb(b,a){b.__gwt_resolve=iHb(a)}
function hHb(a){return a}
function iHb(a){return function(){this.__gwt_resolve=jHb;return a.Yc()}}
function jHb(){throw 'A PotentialElement cannot be resolved twice.'}
function kHb(b){try{return !!b&&!!b.__gwt_resolve}catch(a){return false}}
function UHb(){}
function VHb(a,b){return a.zg(b)?0:Kn(b)-xn(b)}
function WHb(a,b){return a.zg(b)?xn(b)-Kn(b):0}
function XHb(){wb.call(this);UHb()}
function YHb(){MI(THb)&&(THb=new XHb);return THb}
function SHb(){}
_=XHb.prototype=SHb.prototype=new sb;_.gC=function ZHb(){return cN};_.yg=function $Hb(a,b){};_.zg=function _Hb(a){var b=$doc.defaultView.getComputedStyle(a,null);return b.getPropertyValue(UAj)==TAj};var THb=null;function pIb(){}
function qIb(a){return VHb(YHb(),vIb(a))}
function rIb(a){return Hn(vIb(a))-wn(vIb(a))}
function sIb(a){return WHb(YHb(),vIb(a))}
function tIb(){return 0}
function uIb(a){return Jn(vIb(a))}
function vIb(a){return a.c}
function wIb(a){yIb(a,false);dr(Mn(a.c),(st(),qt));dr(Mn(a.b),(st(),qt));er(Mn(a.c),WEj,XEj);er(Mn(a.b),WEj,XEj);BIb(a,false);YHb().yg(a.c,a.b)}
function xIb(a){return MI(a.d)}
function yIb(a,b){br(Mn(vIb(a)),b?(Ms(),Ks):(Ms(),Is))}
function zIb(a,b){Wn(vIb(a),b)}
function AIb(a,b){Xn(vIb(a),b)}
function BIb(a,b){if(b==xIb(a)){return b}if(b){_xb(a.d,null);a.d=null}else{a.d=eyb(a)}return xIb(a)}
function CIb(a,b){AIb(a,b)}
function DIb(){ed();hIb.call(this);pIb();this.c=jd(this);this.b=bf(Ip(gq()));en(this.c,this.b);wIb(this)}
function aIb(){}
_=DIb.prototype=aIb.prototype=new bIb;_.gC=function EIb(){return dN};_.Ag=function FIb(){return this.b};_.qg=function GIb(){return In(vIb(this))};_.rg=function HIb(){return qIb(this)};_.ug=function IIb(){return rIb(this)};_.sg=function JIb(){return sIb(this)};_.vg=function KIb(){return tIb()};_.wg=function LIb(){return uIb(this)};_.fd=function MIb(){Sd(this);wAb(vIb(this),this)};_.hd=function NIb(){wAb(vIb(this),null);Ud(this)};_.og=function OIb(){var a;a=this.Bg();LI(a)&&FI(a,150)&&BI(a,150).og()};_.Zc=function PIb(a){od(this,a)};_.tg=function QIb(a){zIb(this,a)};_.xg=function RIb(a){CIb(this,a)};_._c=function SIb(a){sd(this,a)};_.cM={70:1,78:1,105:1,133:1,134:1,137:1,150:1,164:1,166:1};_.b=null;_.c=null;_.d=null;function bJb(){}
function cJb(a,b,c){var d,e;d=BI(Pd(b),121);e=null;switch(qg(bFb(d.b))){case 3:e=new rJb(a,b,false);break;case 1:e=new rJb(a,b,true);break;case 0:e=new FJb(a,b,false);break;case 2:e=new FJb(a,b,true);}cFb(a,e,d.b,a.b,c)}
function dJb(){ed();eJb.call(this,8)}
function eJb(a){fFb.call(this,(du(),cu));bJb();this.b=a;rd(this,'gwt-SplitLayoutPanel');if(MI(aJb)){aJb=Ip(gq());dr(Mn(aJb),(st(),ot));jr(Mn(aJb),0,(du(),cu));$q(Mn(aJb),0,(du(),cu));_q(Mn(aJb),0,(du(),cu));cr(Mn(aJb),0,(du(),cu));Wq(Mn(aJb),0,(du(),cu));er(Mn(aJb),YEj,ZEj);ar(Mn(aJb),0)}}
function _Ib(){}
_=dJb.prototype=_Ib.prototype=new QEb;_.gC=function fJb(){return kN};_.ng=function gJb(a,b,c,d){cFb(this,a,b,c,d);OI(b,(vFb(),oFb))&&cJb(this,a,d)};_.kg=function hJb(a){var b;b=gDb(this,a);if(eFb(this,a)){b<fDb(this)&&eFb(this,eDb(this,b));return true}return false};_.cM={70:1,78:1,105:1,133:1,134:1,137:1,150:1,164:1,166:1};_.b=0;var aJb=null;function kJb(){}
function lJb(a){var b;b=a.Hg();if(a.j!=b){a.j=b;a.c=b}return FMh(BI(Pd(a.k),121).d+a.c,0)}
function mJb(a,b){var c,d;d=lJb(a);b>d&&(b=d);b<a.e&&(b=a.e);c=BI(Pd(a.k),121);if(b==c.d){return}a.c+=c.d-b;c.d=b;if(MI(a.d)){a.d=new AJb(a);Oj().sd(a.d)}}
function nJb(a,b,c){this.n=a;_d.call(this);kJb();this.k=b;this.i=c;md(this,Ip(gq()));$d(this,78)}
function jJb(){}
_=jJb.prototype=new cd;_.gC=function oJb(){return iN};_.gd=function pJb(a){var b,c,d;switch(pAb(a)){case 4:this.f=true;d=GMh(xBb(),dq(gq()));b=GMh(wBb(),aq(gq()));Zq(Mn(aJb),b,(du(),cu));kr(Mn(aJb),d,(du(),cu));en(Tp(gq()),aJb);this.g=this.Ig(a)-this.Gg();vAb(jd(this));Eq(a);break;case 8:this.f=false;on(aJb);uAb(jd(this));Eq(a);break;case 64:if(this.f){this.i?(c=this.Jg()+this.Kg()-this.Ig(a)-this.g):(c=this.Ig(a)-this.Jg()-this.g);mJb(this,c);Eq(a)}}};_.cM={70:1,78:1,105:1,133:1,137:1,154:1,164:1,166:1};_.c=0;_.d=null;_.e=0;_.f=false;_.g=0;_.i=false;_.j=0;_.k=null;_.n=null;function qJb(){}
function rJb(a,b,c){this.b=a;nJb.call(this,a,b,c);qJb();hr(Mn(jd(this)),qAj,a.b);rd(this,'gwt-SplitLayoutPanel-HDragger')}
function iJb(){}
_=rJb.prototype=iJb.prototype=new jJb;_.Gg=function sJb(){return hd(this)};_.Hg=function tJb(){return aFb(this.b)};_.gC=function uJb(){return gN};_.Ig=function vJb(a){return tq(a)};_.Jg=function wJb(){return hd(this.k)};_.Kg=function xJb(){return this.k.Wc()};_.cM={70:1,78:1,105:1,133:1,137:1,154:1,164:1,166:1};_.b=null;function zJb(){}
function AJb(a){this.b=a;wb.call(this);zJb()}
function yJb(){}
_=AJb.prototype=yJb.prototype=new sb;_.xd=function BJb(){this.b.d=null;$Eb(this.b.n)};_.gC=function CJb(){return hN};_.cM={102:1};_.b=null;function EJb(){}
function FJb(a,b,c){this.b=a;nJb.call(this,a,b,c);EJb();hr(Mn(jd(this)),oAj,a.b);rd(this,'gwt-SplitLayoutPanel-VDragger')}
function DJb(){}
_=FJb.prototype=DJb.prototype=new jJb;_.Gg=function GJb(){return id(this)};_.Hg=function HJb(){return _Eb(this.b)};_.gC=function IJb(){return jN};_.Ig=function JJb(a){return uq(a)};_.Jg=function KJb(){return id(this.k)};_.Kg=function LJb(){return this.k.Vc()};_.cM={70:1,78:1,105:1,133:1,137:1,154:1,164:1,166:1};_.b=null;_=VEg.prototype;_.rd=function $Eg(){var a;a=new kWg;en(yHb(),jd(a))};function iWg(){iWg=Xvj;nEb();hWg=new pWg}
function jWg(){}
function kWg(){iWg();tEb.call(this);jWg();pEb(this,BI(hWg.Tf(this),166))}
function gWg(){}
_=kWg.prototype=gWg.prototype=new mEb;_.gC=function lWg(){return c9};_.cM={70:1,78:1,105:1,133:1,136:1,137:1,164:1,166:1,487:1};var hWg;function nWg(){}
function oWg(){var a,b,c,d,e,f,g,i,j,k;a=new zWg;a.cA();d=new fGb;e=new fGb;f=new fGb;i=new DIb;j=new dJb;g=new fGb;k=new gKb;c=new YGb;b=new fFb((du(),au));eGb(d,'Header');TEb(b,d,21.3);eGb(e,'Equation Editor');VEb(j,e,128);eGb(f,'Graphics view');dIb(i,f);SEb(j,i);WGb(c,(xGb(),tGb));eGb(g,'Command line');TGb(c,g);TGb(c,k);UEb(b,c,10);SEb(b,j);b.Zc($Ej);b._c($Ej);a.cA().Kf();return b}
function pWg(){wb.call(this);nWg()}
function mWg(){}
_=pWg.prototype=mWg.prototype=new sb;_.Tf=function qWg(a){return oWg(BI(a,487))};_.gC=function rWg(){return b9};function vWg(){vWg=Xvj;tWg=new zWg}
function wWg(){}
function xWg(){return JWg()}
function yWg(a){uWg=new FWg(a)}
function zWg(){vWg();wb.call(this);wWg()}
function sWg(){}
_=zWg.prototype=sWg.prototype=new sb;_.gC=function AWg(){return a9};_.cA=function BWg(){return xWg()};var tWg,uWg=null;function DWg(){}
function EWg(){return '.GALL4V3CEI{font-weight:bold;}'}
function FWg(a){this,a;wb.call(this);DWg()}
function CWg(){}
_=FWg.prototype=CWg.prototype=new sb;_.Kf=function GWg(){if(!this.b){this.b=true;jv(EWg());return true}return false};_.gC=function HWg(){return _8};_.cM={94:1};_.b=false;function IWg(){IWg=Xvj;yWg((vWg(),tWg))}
function JWg(){IWg();return vWg(),uWg}
var iJ=IKh(_Ej,aFj,'rb',jbb),_I=IKh(_Ej,'Animation$1','Pb',jbb),hJ=IKh(_Ej,'AnimationScheduler','Ub',jbb),aJ=IKh(_Ej,'AnimationScheduler$AnimationHandle','Zb',jbb),gJ=IKh(_Ej,'AnimationSchedulerImpl','bc',hJ),dJ=IKh(_Ej,'AnimationSchedulerImplTimer','hc',gJ),cJ=IKh(_Ej,'AnimationSchedulerImplTimer$AnimationHandleImpl','Jc',aJ),wqb=HKh('[Lcom.google.gwt.animation.client.','AnimationSchedulerImplTimer$AnimationHandleImpl;','aI',cJ),bJ=IKh(_Ej,'AnimationSchedulerImplTimer$1','pc',qM),fJ=IKh(_Ej,'AnimationSchedulerImplWebkit','Pc',gJ),eJ=IKh(_Ej,'AnimationSchedulerImplWebkit$AnimationHandleImpl','Xc',aJ),sJ=IKh(LDj,'Duration','Ii',jbb),$J=JKh(QDj,'Style$Display','$r',$ab,js,is),Fqb=HKh(VDj,'Style$Display;','aI',$J),WJ=JKh(QDj,'Style$Display$1','ks',$J,null,null),XJ=JKh(QDj,'Style$Display$2','ps',$J,null,null),YJ=JKh(QDj,'Style$Display$3','us',$J,null,null),ZJ=JKh(QDj,'Style$Display$4','zs',$J,null,null),dK=JKh(QDj,'Style$Overflow','Gs',$ab,Rs,Qs),Gqb=HKh(VDj,'Style$Overflow;','aI',dK),_J=JKh(QDj,'Style$Overflow$1','Ss',dK,null,null),aK=JKh(QDj,'Style$Overflow$2','Xs',dK,null,null),bK=JKh(QDj,'Style$Overflow$3','at',dK,null,null),cK=JKh(QDj,'Style$Overflow$4','ft',dK,null,null),iK=JKh(QDj,'Style$Position','mt',$ab,xt,wt),Hqb=HKh(VDj,'Style$Position;','aI',iK),eK=JKh(QDj,'Style$Position$1','yt',iK,null,null),fK=JKh(QDj,'Style$Position$2','Dt',iK,null,null),gK=JKh(QDj,'Style$Position$3','It',iK,null,null),hK=JKh(QDj,'Style$Position$4','Nt',iK,null,null),tK=IKh(QDj,'StyleInjector$1','nv',jbb),uK=IKh(QDj,'StyleInjector$StyleInjectorImpl','sv',jbb),TK=IKh(bFj,'TouchEvent$TouchSupportDetector','BA',jbb),ZK=IKh(YDj,'ResizeEvent','vB',_K),OL=IKh(cFj,'Layout','gub',jbb),KL=IKh(cFj,'Layout$1','uub',iJ),LL=JKh(cFj,'Layout$Alignment','Cub',$ab,Mub,Lub),Mqb=HKh('[Lcom.google.gwt.layout.client.','Layout$Alignment;','aI',LL),ML=IKh(cFj,'Layout$Layer','Pub',jbb),NL=IKh(cFj,'LayoutImpl','$ub',jbb),ZL=IKh(dFj,'DefaultMomentum','_wb',jbb),$L=IKh(dFj,'Momentum$State','gxb',jbb),_L=IKh(dFj,eFj,'txb',jbb),kM=IKh(dFj,'TouchScroller','Gxb',jbb),aM=IKh(dFj,'TouchScroller$1','iyb',jbb),bM=IKh(dFj,'TouchScroller$2','nyb',jbb),cM=IKh(dFj,'TouchScroller$3','syb',jbb),dM=IKh(dFj,'TouchScroller$4','xyb',jbb),eM=IKh(dFj,'TouchScroller$5','Cyb',jbb),fM=IKh(dFj,'TouchScroller$6','Hyb',jbb),hM=IKh(dFj,'TouchScroller$MomentumCommand','Myb',jbb),gM=IKh(dFj,'TouchScroller$MomentumCommand$1','Syb',jbb),iM=IKh(dFj,'TouchScroller$MomentumTouchRemovalCommand','Xyb',jbb),jM=IKh(dFj,'TouchScroller$TemporalPoint','azb',jbb),oM=IKh(DDj,'Event$NativePreviewEvent','xAb',_K),JM=IKh(FDj,'Composite','mEb',zN),IM=IKh(FDj,'Composite_HTMLTemplatesImpl','AEb',jbb),OM=IKh(FDj,'DockLayoutPanel','QEb',HM),LM=JKh(FDj,'DockLayoutPanel$Direction','mFb',$ab,AFb,zFb),Oqb=HKh(fEj,'DockLayoutPanel$Direction;','aI',LM),YM=IKh(FDj,'LayoutCommand','EFb',jbb),MM=IKh(FDj,'DockLayoutPanel$DockAnimateCommand','DFb',YM),NM=IKh(FDj,'DockLayoutPanel$LayoutData','QFb',jbb),XM=IKh(FDj,'LayoutCommand$1','_Gb',jbb),cN=IKh(FDj,'ScrollImpl','SHb',jbb),dN=IKh(FDj,'ScrollPanel','aIb',fN),kN=IKh(FDj,'SplitLayoutPanel','_Ib',OM),iN=IKh(FDj,'SplitLayoutPanel$Splitter','jJb',zN),gN=IKh(FDj,'SplitLayoutPanel$HSplitter','iJb',iN),hN=IKh(FDj,'SplitLayoutPanel$Splitter$1','yJb',jbb),jN=IKh(FDj,'SplitLayoutPanel$VSplitter','DJb',iN),c9=IKh(fFj,'GeoGebraAppFrame','gWg',JM),b9=IKh(fFj,'GeoGebraAppFrame_GeoGebraAppFrameUiBinderImpl','mWg',jbb),a9=IKh(fFj,'GeoGebraAppFrame_GeoGebraAppFrameUiBinderImpl_GenBundle_default_InlineClientBundleGenerator','sWg',jbb),_8=IKh(fFj,'GeoGebraAppFrame_GeoGebraAppFrameUiBinderImpl_GenBundle_default_InlineClientBundleGenerator$1','CWg',jbb);kAj(nk)(1);