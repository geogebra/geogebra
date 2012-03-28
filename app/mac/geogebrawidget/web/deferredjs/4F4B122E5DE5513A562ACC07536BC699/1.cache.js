var MDj='100%',NDj='com.google.gwt.animation.client.',QDj='com.google.gwt.layout.client.',RDj='com.google.gwt.touch.client.',yDj='display',TDj='geogebra.web.gui.app.',IDj='zoom';function Bb(a){a.d=new Rb(a)}
function Cb(a){if(!a.g){return}a.p=a.i;a.f=null;a.g=false;a.i=false;if(iI(a.j)){a.j.Rc();a.j=null}a.Lc()}
function Db(a){return (1+oLh(3.141592653589793+a*3.141592653589793))/2}
function Eb(a,b){return a.g&&a.k==b}
function Fb(a){a.Oc(Db(0))}
function Gb(a,b,c){Hb(a,b,Ai(),c)}
function Hb(a,b,c,d){Cb(a);a.g=true;a.i=false;a.e=b;a.o=c;a.f=d;++a.k;a.d.Pc(Ai())}
function Ib(a,b){var c,d,e;c=a.k;d=b>=a.o+a.e;if(a.i&&!d){e=(b-a.o)/a.e;a.Oc(Db(e));return Eb(a,c)}if(!a.i&&b>=a.o){a.i=true;a.Nc();if(!Eb(a,c)){return false}}if(d){a.g=false;a.i=false;a.Mc();return false}return true}
function Jb(){Kb.call(this,Xb())}
function Kb(a){wb.call(this);Bb(this);this.n=a}
function rb(){}
_=rb.prototype=new sb;_.gC=function Lb(){return FI};_.Lc=function Mb(){this.p&&this.Mc()};_.Mc=function Nb(){this.Oc(Db(1))};_.Nc=function Ob(){Fb(this)};_.e=-1;_.f=null;_.g=false;_.i=false;_.j=null;_.k=-1;_.n=null;_.o=-1;_.p=false;function Qb(){}
function Rb(a){this.b=a;wb.call(this);Qb()}
function Pb(){}
_=Rb.prototype=Pb.prototype=new sb;_.Pc=function Sb(a){Ib(this.b,a)?(this.b.j=this.b.n.Qc(this.b.d,this.b.f)):(this.b.j=null)};_.gC=function Tb(){return yI};_.b=null;function Vb(){}
function Wb(){wb.call(this);Vb()}
function Xb(){return dc(),cc}
function Ub(){}
_=Ub.prototype=new sb;_.gC=function Yb(){return EI};function $b(){}
function _b(){wb.call(this);$b()}
function Zb(){}
_=Zb.prototype=new sb;_.gC=function ac(){return zI};function dc(){dc=Muj;var a;{a=new lc;cI(a,6)&&($H(a,6).Sc()||(a=new lc));cc=a}}
function ec(){}
function fc(){Wb.call(this);ec()}
function bc(){}
_=bc.prototype=new Ub;_.gC=function gc(){return DI};_.cM={6:1};var cc=null;function ic(a){a.b=new q5b;a.c=new Gc(a)}
function jc(a,b){a.b.hl(b);a.b.jl()==0&&uc(a.c)}
function kc(a){var b,c,d,e,f,g;b=KH(Opb,{8:1,515:1,538:1},7,a.b.jl(),0);b=$H(a.b.ll(b),8);c=new zi;for(e=b,f=0,g=e.length;f<g;++f){d=e[f];a.b.hl(d);Lc(d).Pc(yi(c))}a.b.jl()>0&&vc(a.c,vLh(5,16-xi(c)))}
function lc(){fc.call(this);ic(this)}
function hc(){}
_=lc.prototype=hc.prototype=new bc;_.gC=function mc(){return CI};_.Sc=function nc(){return true};_.Qc=function oc(a,b){var c;c=new Mc(this,a);this.b.cl(c);this.b.jl()==1&&vc(this.c,16);return c};_.cM={6:1};function Fc(){}
function Gc(a){sc();this.b=a;xc.call(this);Fc()}
function pc(){}
_=Gc.prototype=pc.prototype=new qc;_.gC=function Hc(){return AI};_.Uc=function Ic(){kc(this.b)};_.cM={109:1};_.b=null;function Kc(){}
function Lc(a){return a.b}
function Mc(a,b){this.c=a;_b.call(this);Kc();this.b=b}
function Jc(){}
_=Mc.prototype=Jc.prototype=new Zb;_.Rc=function Nc(){jc(this.c,this)};_.gC=function Oc(){return BI};_.cM={7:1};_.b=null;_.c=null;_=Sc.prototype;_.Yc=function nd(){throw new UOh};function wd(a,b){return yd(a,b,IA())}
function yd(a,b,c){return eB(Ad(a),c,b)}
function Cd(a){return a.r}
function Ld(a,b){a.r=b}
_=Rc.prototype;_.ad=function Pd(){return this};function wi(a){a.b=Ai()}
function xi(a){return Ci(Ai()-a.b)}
function yi(a){return a.b}
function zi(){wb.call(this);wi(this)}
function Ci(a){return a}
function vi(){}
_=zi.prototype=vi.prototype=new sb;_.gC=function Bi(){return PI};function hj(b,a){return b.join(a)}
function jj(b,a){b[b.length]=a}
function lj(b,a){b.length=a}
_=gl.prototype;_.sd=function xl(a){this.b=ul(this.b,Sl(a));ol(this)};_.td=function yl(a){this.d=ul(this.d,Sl(a))};_.ud=function zl(a,b){pl(a,b)};function Sl(a){return [a,false]}
function Wm(a){return a.firstChild}
function Zm(c,a,b){return c.insertBefore(a,b)}
function an(a){var b;b=Ym(a);iI(b)&&_m(b,a)}
function jn(a){return a.clientHeight}
function kn(a){return a.clientWidth}
function pn(a){return a.offsetHeight||0}
function rn(a){return a.offsetWidth||0}
function un(a){return a.scrollHeight||0}
function xn(a){return a.scrollWidth||0}
function Jn(a,b){(Vn(),Un).je(a,b)}
function Kn(b,a){b.scrollTop=a}
function Zn(a,b){a.opacity=b}
function po(a){return a.touches}
function ro(a,b){a.scrollLeft=b}
function uo(a){return a.pageX}
function vo(a){return a.pageY}
_=Tn.prototype;_.je=function Ko(a,b){ro(a,b)};function jp(a){return Xn((Vn(),Un,a),UAj)}
function kp(a){return Xn((Vn(),Un,a),xDj)}
function tp(a){return jn(Dp(a))}
function up(a){return kn(Dp(a))}
function zp(a){return un(Dp(a))}
function Cp(a){return xn(Dp(a))}
function _p(a){return po((Vn(),Un,a))}
function iq(a){nq(a,VAj)}
function jq(a){nq(a,yDj)}
function kq(a){nq(a,dzj)}
function lq(a){nq(a,Kzj)}
function mq(a){nq(a,QAj)}
function nq(a,b){Dq(a,b,izj)}
function oq(a){nq(a,Zzj)}
function pq(a){nq(a,Lzj)}
function qq(a){nq(a,fzj)}
function uq(a,b,c){Eq(a,VAj,b,c)}
function vq(a,b){Dq(a,yDj,b.le())}
function yq(a,b,c){Eq(a,'margin',b,c)}
function zq(a,b){Zn((Vn(),Un,a),b)}
function Aq(a,b){Dq(a,TAj,b.le())}
function Bq(a,b,c){Eq(a,'padding',b,c)}
function Cq(a,b){Dq(a,QAj,b.le())}
function Gq(a,b,c){Eq(a,b,c,(Ct(),Bt))}
function Hq(a,b,c){Eq(a,Zzj,b,c)}
function Kq(a,b){Dq(a,'zIndex',b+izj)}
function Dr(){Dr=Muj;Cr=new Lr(Mzj,0);zr=new Qr('BLOCK',1);Ar=new Vr('INLINE',2);Br=new $r('INLINE_BLOCK',3);yr=NH(Xpb,{515:1,538:1},22,[Cr,zr,Ar,Br])}
function Er(){}
function Fr(a,b){fg.call(this,a,b);Er()}
function Hr(a){Dr();return og((cs(),bs),a)}
function Ir(){Dr();return yr}
function xr(){}
_=xr.prototype=new $f;_.gC=function Gr(){return uJ};_.cM={22:1,26:1,515:1,525:1,527:1};var yr,zr,Ar,Br,Cr;function Kr(){}
function Lr(a,b){Fr.call(this,a,b);Kr()}
function Jr(){}
_=Lr.prototype=Jr.prototype=new xr;_.gC=function Mr(){return qJ};_.le=function Nr(){return Ozj};_.cM={22:1,26:1,515:1,525:1,527:1};function Pr(){}
function Qr(a,b){Fr.call(this,a,b);Pr()}
function Or(){}
_=Qr.prototype=Or.prototype=new xr;_.gC=function Rr(){return rJ};_.le=function Sr(){return zDj};_.cM={22:1,26:1,515:1,525:1,527:1};function Ur(){}
function Vr(a,b){Fr.call(this,a,b);Ur()}
function Tr(){}
_=Vr.prototype=Tr.prototype=new xr;_.gC=function Wr(){return sJ};_.le=function Xr(){return 'inline'};_.cM={22:1,26:1,515:1,525:1,527:1};function Zr(){}
function $r(a,b){Fr.call(this,a,b);Zr()}
function Yr(){}
_=$r.prototype=Yr.prototype=new xr;_.gC=function _r(){return tJ};_.le=function as(){return 'inline-block'};_.cM={22:1,26:1,515:1,525:1,527:1};function cs(){cs=Muj;bs=hg((Dr(),yr))}
var bs;function js(){js=Muj;is=new rs('VISIBLE',0);gs=new ws(Nzj,1);hs=new Bs('SCROLL',2);fs=new Gs(ADj,3);es=NH(Ypb,{515:1,538:1},28,[is,gs,hs,fs])}
function ks(){}
function ls(a,b){fg.call(this,a,b);ks()}
function ns(a){js();return og((Ks(),Js),a)}
function os(){js();return es}
function ds(){}
_=ds.prototype=new $f;_.gC=function ms(){return zJ};_.cM={26:1,28:1,515:1,525:1,527:1};var es,fs,gs,hs,is;function qs(){}
function rs(a,b){ls.call(this,a,b);qs()}
function ps(){}
_=rs.prototype=ps.prototype=new ds;_.gC=function ss(){return vJ};_.le=function ts(){return BDj};_.cM={26:1,28:1,515:1,525:1,527:1};function vs(){}
function ws(a,b){ls.call(this,a,b);vs()}
function us(){}
_=ws.prototype=us.prototype=new ds;_.gC=function xs(){return wJ};_.le=function ys(){return Pzj};_.cM={26:1,28:1,515:1,525:1,527:1};function As(){}
function Bs(a,b){ls.call(this,a,b);As()}
function zs(){}
_=Bs.prototype=zs.prototype=new ds;_.gC=function Cs(){return xJ};_.le=function Ds(){return EAj};_.cM={26:1,28:1,515:1,525:1,527:1};function Fs(){}
function Gs(a,b){ls.call(this,a,b);Fs()}
function Es(){}
_=Gs.prototype=Es.prototype=new ds;_.gC=function Hs(){return yJ};_.le=function Is(){return CDj};_.cM={26:1,28:1,515:1,525:1,527:1};function Ks(){Ks=Muj;Js=hg((js(),es))}
var Js;function Rs(){Rs=Muj;Qs=new Zs('STATIC',0);Ps=new ct('RELATIVE',1);Ns=new ht('ABSOLUTE',2);Os=new mt(DDj,3);Ms=NH(Zpb,{515:1,538:1},29,[Qs,Ps,Ns,Os])}
function Ss(){}
function Ts(a,b){fg.call(this,a,b);Ss()}
function Vs(a){Rs();return og((qt(),pt),a)}
function Ws(){Rs();return Ms}
function Ls(){}
_=Ls.prototype=new $f;_.gC=function Us(){return EJ};_.cM={26:1,29:1,515:1,525:1,527:1};var Ms,Ns,Os,Ps,Qs;function Ys(){}
function Zs(a,b){Ts.call(this,a,b);Ys()}
function Xs(){}
_=Zs.prototype=Xs.prototype=new Ls;_.gC=function $s(){return AJ};_.le=function _s(){return 'static'};_.cM={26:1,29:1,515:1,525:1,527:1};function bt(){}
function ct(a,b){Ts.call(this,a,b);bt()}
function at(){}
_=ct.prototype=at.prototype=new Ls;_.gC=function dt(){return BJ};_.le=function et(){return SAj};_.cM={26:1,29:1,515:1,525:1,527:1};function gt(){}
function ht(a,b){Ts.call(this,a,b);gt()}
function ft(){}
_=ht.prototype=ft.prototype=new Ls;_.gC=function it(){return CJ};_.le=function jt(){return RAj};_.cM={26:1,29:1,515:1,525:1,527:1};function lt(){}
function mt(a,b){Ts.call(this,a,b);lt()}
function kt(){}
_=mt.prototype=kt.prototype=new Ls;_.gC=function nt(){return DJ};_.le=function ot(){return EDj};_.cM={26:1,29:1,515:1,525:1,527:1};function qt(){qt=Muj;pt=hg((Rs(),Ms))}
var pt;function Gu(){Gu=Muj;Du=Qe(Ve());Eu=Qe(Ve());Fu=Qe(Ve());Bu=new Ou}
function Hu(a){Gu();var b,c,d;d=null;if(ij(Fu)!=0){b=hj(Fu,izj);c=(Tu(),Su).pe(b);kI(Fu,a)&&(d=c);lj(Fu,0)}if(ij(Du)!=0){b=hj(Du,izj);c=(Tu(),Su).ne(b);kI(Du,a)&&(d=c);lj(Du,0)}if(ij(Eu)!=0){b=hj(Eu,izj);c=(Tu(),Su).oe(b);kI(Eu,a)&&(d=c);lj(Eu,0)}Cu=false;return d}
function Iu(a){Gu();Ju(a,false)}
function Ju(a,b){jj(Du,a);Ku(b)}
function Ku(a){a?Hu(null):Lu()}
function Lu(){if(!Cu){Cu=true;Bj().td(Bu)}}
var Bu,Cu=false,Du,Eu,Fu;function Nu(){}
function Ou(){wb.call(this);Nu()}
function Mu(){}
_=Ou.prototype=Mu.prototype=new sb;_.xd=function Pu(){(Gu(),Cu)&&Hu(null)};_.gC=function Qu(){return PJ};function Tu(){Tu=Muj;Su=new Yu}
function Uu(){}
function Vu(a,b){var c;c=kp(Fp());In(c,cCj,'text/css');Xu(c,b);return c}
function Wu(a){var b;if(jI(a.b)){b=dq(yp(Fp(),yzj),0);a.b=Hp(b)}return a.b}
function Xu(a,b){Hn(a,b)}
function Yu(){wb.call(this);Uu()}
function Ru(){}
_=Yu.prototype=Ru.prototype=new sb;_.gC=function Zu(){return QJ};_.ne=function $u(a){var b;b=Vu(this,a);Um(Wu(this),b);return b};_.oe=function _u(a){return this.ne(a)};_.pe=function av(a){var b;b=Vu(this,a);Zm(Wu(this),b,Wm(this.b));return b};_.b=null;var Su;function dv(a){return uo((Vn(),Un,a))}
function ev(a){return vo((Vn(),Un,a))}
function Cz(a){return _p(Dv(a))}
function Fz(){jI(zz)&&(zz=new bA);return zz.Qe()}
var zz=null;function _z(a){a.b=aA()}
function aA(){var a=document.createElement(jzj);a.setAttribute('ontouchstart','return;');return typeof a.ontouchstart==Bzj}
function bA(){wb.call(this);_z(this)}
function $z(){}
_=bA.prototype=$z.prototype=new sb;_.gC=function cA(){return nK};_.Qe=function dA(){return this.b};function CA(a){return a.b}
function IA(){jI(zA)&&(zA=new yw);return zA}
function WA(){}
function XA(a,b){b.Ve(a)}
function YA(a,b){wv.call(this);WA();this,a;this,b}
function $A(a,b,c){var d;if(iI(VA)){d=new YA(b,c);a.dd(d)}}
function bB(){jI(VA)&&(VA=new yw);return VA}
function UA(){}
_=YA.prototype=UA.prototype=new hv;_.se=function ZA(a){XA(this,$H(a,72))};_.te=function _A(){return VA};_.gC=function aB(){return tK};var VA=null;function ztb(a){a.c=new Bub;a.d=new q5b}
function Atb(a,b,c){var d,e,f;d=c.j*Dtb(a,c.k,false);e=c.n*Dtb(a,c.o,false);f=c.Y*Dtb(a,c.Z,false);if(c.r&&!c.v){c.r=false;if(c.A){c.w=true;c.E=(b-(d+f))/Dtb(a,c.O,false)}else{c.y=true;c.G=(b-(d+e))/Dtb(a,c.S,false)}}else if(c.A&&!c.y){c.A=false;if(c.r){c.w=true;c.E=(b-(d+f))/Dtb(a,c.O,false)}else{c.v=true;c.D=(b-(e+f))/Dtb(a,c.M,false)}}else if(c.s&&!c.w){c.s=false;if(c.A){c.v=true;c.D=(b-(e+f))/Dtb(a,c.M,false)}else{c.y=true;c.G=(b-(d+e))/Dtb(a,c.S,false)}}c.r=c.v;c.s=c.w;c.A=c.y;c.k=c.M;c.o=c.O;c.Z=c.S}
function Btb(a,b,c){var d,e,f;f=c.T*Dtb(a,c.U,true);d=c.b*Dtb(a,c.c,true);e=c.g*Dtb(a,c.i,true);if(c.z&&!c.x){c.z=false;if(c.q){c.t=true;c.B=(b-(f+e))/Dtb(a,c.I,true)}else{c.u=true;c.C=(b-(f+d))/Dtb(a,c.K,true)}}else if(c.q&&!c.u){c.q=false;if(c.z){c.t=true;c.B=(b-(f+e))/Dtb(a,c.I,true)}else{c.x=true;c.F=(b-(d+e))/Dtb(a,c.Q,true)}}else if(c.p&&!c.t){c.p=false;if(c.q){c.x=true;c.F=(b-(d+e))/Dtb(a,c.Q,true)}else{c.u=true;c.C=(b-(f+d))/Dtb(a,c.K,true)}}c.z=c.x;c.p=c.t;c.q=c.u;c.U=c.Q;c.c=c.I;c.i=c.K}
function Ctb(a,b,c,d){var e,f;e=uub(a.c,a.e,b,c);f=new oub(a,e,b,d);a.d.cl(f);return f}
function Dtb(a,b,c){return xub(a.c,a.e,b,c)}
function Etb(a){Ftb(a,0)}
function Ftb(a,b){Gtb(a,b,null)}
function Gtb(a,b,c){var d,e,f,g;iI(a.b)&&Cb(a.b);if(b==0){for(e=a.d.lf();e.Dg();){d=$H(e.Eg(),92);d.j=d.D=d.L;d.T=d.F=d.P;d.n=d.E=d.N;d.b=d.B=d.H;d.Y=d.G=d.R;d.g=d.C=d.J;d.r=d.v;d.z=d.x;d.s=d.w;d.p=d.t;d.A=d.y;d.q=d.u;d.k=d.M;d.U=d.Q;d.o=d.O;d.c=d.I;d.Z=d.S;d.i=d.K;a.c.If(d)}wub();iI(c)&&c.Gf();return}g=kn(a.e);f=jn(a.e);for(e=a.d.lf();e.Dg();){d=$H(e.Eg(),92);Atb(a,g,d);Btb(a,f,d)}a.b=new Ptb(a,c);Gb(a.b,b,a.e)}
function Htb(a){zub()}
function Itb(a){a.c.Jf(a.e)}
function Jtb(a,b){Aub(b.e,b.d);a.d.hl(b)}
function Ktb(a){wb.call(this);ztb(this);this.e=a;yub(this.c,a)}
function ytb(){}
_=Ktb.prototype=ytb.prototype=new sb;_.gC=function Ltb(){return iL};_.b=null;_.e=null;function Ntb(){}
function Otb(a){a.b.b=null;Etb(a.b);iI(a.c)&&a.c.Gf()}
function Ptb(a,b){this.b=a;this.c=b;Jb.call(this);Ntb()}
function Mtb(){}
_=Ptb.prototype=Mtb.prototype=new rb;_.gC=function Qtb(){return eL};_.Lc=function Rtb(){Otb(this)};_.Mc=function Stb(){Otb(this)};_.Oc=function Ttb(a){var b,c;for(c=this.b.d.lf();c.Dg();){b=$H(c.Eg(),92);b.v&&(b.j=b.D+(b.L-b.D)*a);b.w&&(b.n=b.E+(b.N-b.E)*a);b.x&&(b.T=b.F+(b.P-b.F)*a);b.t&&(b.b=b.B+(b.H-b.B)*a);b.y&&(b.Y=b.G+(b.R-b.G)*a);b.u&&(b.g=b.C+(b.J-b.C)*a);this.b.c.If(b);iI(this.c)&&this.c.Hf(b,a)}wub()};_.b=null;_.c=null;function Ztb(){Ztb=Muj;Wtb=new _tb('BEGIN',0);Xtb=new _tb('END',1);Ytb=new _tb('STRETCH',2);Vtb=NH(cqb,{515:1,538:1},91,[Wtb,Xtb,Ytb])}
function $tb(){}
function _tb(a,b){fg.call(this,a,b);$tb()}
function bub(a){Ztb();return og((eub(),dub),a)}
function cub(){Ztb();return Vtb}
function Utb(){}
_=_tb.prototype=Utb.prototype=new $f;_.gC=function aub(){return fL};_.cM={91:1,515:1,525:1,527:1};var Vtb,Wtb,Xtb,Ytb;function eub(){eub=Muj;dub=hg((Ztb(),Vtb))}
var dub;function gub(a){a.M=(Ct(),Bt);a.Q=(Ct(),Bt);a.O=(Ct(),Bt);a.I=(Ct(),Bt);a.f=(Ztb(),Ytb);a.W=(Ztb(),Ytb)}
function hub(a){return a.V}
function iub(a,b,c,d,e){a.t=a.u=true;a.x=false;a.H=b;a.J=d;a.I=c;a.K=e}
function jub(a,b,c,d,e){a.v=a.w=true;a.y=false;a.L=b;a.N=d;a.M=c;a.O=e}
function kub(a,b,c,d,e){a.v=a.y=true;a.w=false;a.L=b;a.R=d;a.M=c;a.S=e}
function lub(a,b,c,d,e){a.w=a.y=true;a.v=false;a.N=b;a.R=d;a.O=c;a.S=e}
function mub(a,b,c,d,e){a.x=a.t=true;a.u=false;a.P=b;a.H=d;a.Q=c;a.I=e}
function nub(a,b,c,d,e){a.x=a.u=true;a.t=false;a.P=b;a.J=d;a.Q=c;a.K=e}
function oub(a,b,c,d){this,a;wb.call(this);gub(this);this.e=b;this.d=c;this.V=d}
function fub(){}
_=oub.prototype=fub.prototype=new sb;_.gC=function pub(){return gL};_.cM={92:1};_.b=0;_.c=null;_.d=null;_.e=null;_.g=0;_.i=null;_.j=0;_.k=null;_.n=0;_.o=null;_.p=false;_.q=false;_.r=false;_.s=false;_.t=true;_.u=false;_.v=true;_.w=true;_.x=true;_.y=false;_.z=false;_.A=false;_.B=0;_.C=0;_.D=0;_.E=0;_.F=0;_.G=0;_.H=0;_.J=0;_.K=null;_.L=0;_.N=0;_.P=0;_.R=0;_.S=null;_.T=0;_.U=null;_.V=null;_.X=true;_.Y=0;_.Z=null;function sub(){sub=Muj;rub=Cub((Ct(),tt),(Ct(),tt));Um(qp(Fp()),rub)}
function tub(){}
function uub(a,b,c,d){var e,f;f=fp(Fp());Um(f,c);Cq(zn(f),(Rs(),Ns));Aq(zn(f),(js(),gs));vub(c);e=null;iI(d)&&(e=Ym(d));Zm(b,f,e);return f}
function vub(a){var b;b=zn(a);Cq(b,(Rs(),Ns));xq(b,0,(Ct(),Bt));Iq(b,0,(Ct(),Bt));Hq(b,0,(Ct(),Bt));uq(b,0,(Ct(),Bt))}
function wub(){}
function xub(a,b,c,d){if(jI(c)){return 1}switch(dg(c)){case 1:return (d?jn(b):kn(b))/100;case 2:return rn(a.b)/10;case 3:return pn(a.b)/10;case 7:return rn(rub)*0.1;case 8:return rn(rub)*0.01;case 6:return rn(rub)*0.254;case 4:return rn(rub)*0.00353;case 5:return rn(rub)*0.0423;default:case 0:return 1;}}
function yub(a,b){Cq(zn(b),(Rs(),Ps));Um(b,a.b=Cub((Ct(),ut),(Ct(),vt)))}
function zub(){}
function Aub(a,b){var c;an(a);kI(Ym(b),a)&&an(b);c=zn(b);mq(c);lq(c);pq(c);qq(c);kq(c)}
function Bub(){sub();wb.call(this);tub()}
function Cub(a,b){var c,d;c=fp(Fp());Gn(c,FDj);d=zn(c);Cq(d,(Rs(),Ns));Kq(d,-32767);Iq(d,-20,b);Jq(d,10,a);wq(d,10,b);return c}
function qub(){}
_=Bub.prototype=qub.prototype=new sb;_.gC=function Dub(){return hL};_.If=function Eub(a){var b;b=zn(a.e);a.X?jq(b):vq(b,(Dr(),Cr));Dq(b,Kzj,a.r?a.j+a.k.me():izj);Dq(b,Lzj,a.z?a.T+a.U.me():izj);Dq(b,Zzj,a.s?a.n+a.o.me():izj);Dq(b,VAj,a.p?a.b+a.c.me():izj);Dq(b,fzj,a.A?a.Y+a.Z.me():izj);Dq(b,dzj,a.q?a.g+a.i.me():izj);b=zn(a.d);switch(dg(a.f)){case 0:xq(b,0,(Ct(),Bt));oq(b);break;case 1:lq(b);Hq(b,0,(Ct(),Bt));break;case 2:xq(b,0,(Ct(),Bt));Hq(b,0,(Ct(),Bt));}switch(dg(a.W)){case 0:Iq(b,0,(Ct(),Bt));iq(b);break;case 1:pq(b);uq(b,0,(Ct(),Bt));break;case 2:Iq(b,0,(Ct(),Bt));uq(b,0,(Ct(),Bt));}};_.Jf=function Fub(a){};_.b=null;var rub=null;function hwb(){}
function iwb(a,b,c,d){var e,f,g;g=a*b;if(c>=0){e=uLh(0,c-d);g=xLh(g,e)}else{f=xLh(0,c+d);g=uLh(g,f)}return g}
function jwb(){wb.call(this);hwb()}
function gwb(){}
_=jwb.prototype=gwb.prototype=new sb;_.Qf=function kwb(a,b){return new ywb(a,b)};_.gC=function lwb(){return sL};_.Rf=function mwb(a){var b,c,d,e,f,g,i,j,k,n,o,p;e=qwb(a);p=pwb(a);f=rwb(a);n=twb(a);b=zLh(0.9993,p);g=e*5.0E-4;j=iwb(Cwb(f),b,Cwb(n),g);k=iwb(Dwb(f),b,Dwb(n),g);i=new Hwb(j,k);xwb(a,i);d=qwb(a);c=Fwb(i,new Hwb(d,d));o=swb(a);wwb(a,Gwb(o,c));if(gLh(Cwb(i))<0.02&&gLh(Dwb(i))<0.02){return false}return true};function owb(a){}
function pwb(a){return a.b}
function qwb(a){return a.c}
function rwb(a){return a.d}
function swb(a){return a.e}
function twb(a){return a.f}
function uwb(a,b){a.b=b}
function vwb(a,b){a.c=b}
function wwb(a,b){a.e=b}
function xwb(a,b){a.f=b}
function ywb(a,b){wb.call(this);owb(this);this,a;this.d=b;this.e=new Iwb(a);this.f=new Iwb(b)}
function nwb(){}
_=ywb.prototype=nwb.prototype=new sb;_.gC=function zwb(){return tL};_.b=0;_.c=0;_.d=null;_.e=null;_.f=null;function Bwb(){}
function Cwb(a){return a.b}
function Dwb(a){return a.c}
function Ewb(a,b){return new Hwb(a.b-b.b,a.c-b.c)}
function Fwb(a,b){return new Hwb(a.b*b.b,a.c*b.c)}
function Gwb(a,b){return new Hwb(a.b+b.b,a.c+b.c)}
function Hwb(a,b){wb.call(this);Bwb();this.b=a;this.c=b}
function Iwb(a){Hwb.call(this,a.b,a.c)}
function Awb(){}
_=Iwb.prototype=Hwb.prototype=Awb.prototype=new sb;_.eQ=function Jwb(a){var b;if(!cI(a,99)){return false}b=$H(a,99);return this.b==b.b&&this.c==b.c};_.gC=function Kwb(){return uL};_.hC=function Lwb(){return rI(this.b)^rI(this.c)};_.tS=function Mwb(){return 'Point('+this.b+GDj+this.c+jCj};_.cM={99:1};_.b=0;_.c=0;function Pwb(a){a.e=new q5b;a.f=new myb;a.n=new myb;a.k=new myb;a.r=new q5b;a.j=new eyb(a)}
function Qwb(a,b){var c,d;d=kyb(b)-kyb(a);if(d<=0){return null}c=Ewb(jyb(a),jyb(b));return new Hwb(Cwb(c)/d,Dwb(c)/d)}
function Rwb(a){a.s=false;a.d=false;a.i=null}
function Swb(a){var b;b=Cz(a);return aj(b)>0?_i(b,0):null}
function Twb(a){return new Hwb(a.t.qg(),a.t.wg())}
function Uwb(a,b){var c,d,e;e=Ewb(a,b);c=gLh(Cwb(e));d=gLh(Dwb(e));return c<=25&&d<=25}
function Vwb(a,b){if(iI(jyb(a.k))){return Uwb(b,jyb(a.k))}return false}
function Wwb(a,b){var c,d,e,f;c=Ai();f=false;for(e=a.r.lf();e.Dg();){d=$H(e.Eg(),100);if(c-kyb(d)<=2500&&Uwb(b,jyb(d))){f=true;break}}return f}
function Xwb(a){return iI(a.i)}
function Ywb(a){var b;if(jI(a.g)){return}b=Qwb(a.n,a.f);if(iI(b)){a.i=new Wxb(a,b);Bj().ud(a.i,16)}}
function Zwb(a){var b,c;c=Ewb(a.q,jyb(a.f));b=Gwb(a.p,c);hxb(a,b)}
function $wb(){}
function _wb(a,b){axb(a,b)}
function axb(a,b){if(!a.s){return}a.s=false;if(a.d){a.d=false;Ywb(a)}}
function bxb(a,b){var c,d,e,f,g,i,j,k,n,o,p,q,r;if(!a.s){return}j=Swb(b);k=new Hwb(dv(j),ev(j));n=Ai();lyb(a.f,k,n);if(!a.d){e=Ewb(k,a.q);c=gLh(Cwb(e));d=gLh(Dwb(e));if(c>5||d>5){lyb(a.k,jyb(a.n),kyb(a.n));if(c>d){i=a.t.qg();g=a.t.sg();f=a.t.rg();if(Cwb(e)<0&&f<=i){Rwb(a);return}else if(Cwb(e)>0&&g>=i){Rwb(a);return}}else{r=a.t.wg();q=a.t.vg();p=a.t.ug();if(Dwb(e)<0&&p<=r){Rwb(a);return}else if(Dwb(e)>0&&q>=r){Rwb(a);return}}a.d=true;$wb()}}Ev(b);if(a.d){Zwb(a);o=n-kyb(a.n);if(o>200&&iI(a.o)){lyb(a.n,jyb(a.o),kyb(a.o));a.o=null}else o>100&&jI(a.o)&&(a.o=new nyb(k,n))}}
function cxb(a,b){var c,d;lyb(a.k,null,0);if(a.s){return}d=Swb(b);a.q=new Hwb(dv(d),ev(d));c=Ai();lyb(a.n,a.q,c);lyb(a.f,a.q,c);a.o=null;if(Xwb(a)){a.r.cl(new nyb(a.q,c));Bj().ud(a.j,2500)}a.p=Twb(a);Rwb(a);a.s=true}
function dxb(a){if(iI(a.b)){a.b.Xe();a.b=null}}
function exb(a){if(iI(a.c)){a.c.Xe();a.c=null}}
function fxb(a,b){a.g=b;jI(b)&&(a.i=null)}
function gxb(a,b){var c,d;if(kI(a.t,b)){return}Rwb(a);for(d=a.e.lf();d.Dg();){c=$H(d.Eg(),77);c.Xe()}a.e.el();dxb(a);exb(a);a.t=b;if(iI(b)){b.ad().ed()&&ixb(a);a.b=wd(b.ad(),new rxb(a));a.e.cl(xd(b.ad(),new wxb(a),xA()));a.e.cl(xd(b.ad(),new Bxb(a),nA()));a.e.cl(xd(b.ad(),new Gxb(a),Zz()));a.e.cl(xd(b.ad(),new Lxb(a),Pz()))}}
function hxb(a,b){a.t.tg(rI(Cwb(b)));a.t.xg(rI(Dwb(b)))}
function ixb(a){exb(a);a.c=xzb(new Qxb(a))}
function jxb(){wb.call(this);Pwb(this);fxb(this,new jwb)}
function kxb(){return oxb()?new jxb:null}
function lxb(a){var b;b=kxb();iI(b)&&gxb(b,a);return b}
function nxb(){var a=navigator.userAgent.toLowerCase();return /android ([3-9]+)\.([0-9]+)/.exec(a)!=null}
function oxb(){jI(Owb)&&(Owb=MIh(Fz()&&!nxb()));return CIh(Owb)}
function Nwb(){}
_=jxb.prototype=Nwb.prototype=new sb;_.gC=function mxb(){return FL};_.b=null;_.c=null;_.d=false;_.g=null;_.i=null;_.o=null;_.p=null;_.q=null;_.s=false;_.t=null;var Owb=null;function qxb(){}
function rxb(a){this.b=a;wb.call(this);qxb()}
function pxb(){}
_=rxb.prototype=pxb.prototype=new sb;_.gC=function sxb(){return vL};_.Te=function txb(a){CA(a)?ixb(this.b):exb(this.b)};_.cM={67:1,75:1};_.b=null;function vxb(){}
function wxb(a){this.b=a;wb.call(this);vxb()}
function uxb(){}
_=wxb.prototype=uxb.prototype=new sb;_.gC=function xxb(){return wL};_.Se=function yxb(a){cxb(this.b,a)};_.cM={66:1,75:1};_.b=null;function Axb(){}
function Bxb(a){this.b=a;wb.call(this);Axb()}
function zxb(){}
_=Bxb.prototype=zxb.prototype=new sb;_.gC=function Cxb(){return xL};_.Re=function Dxb(a){bxb(this.b,a)};_.cM={65:1,75:1};_.b=null;function Fxb(){}
function Gxb(a){this.b=a;wb.call(this);Fxb()}
function Exb(){}
_=Gxb.prototype=Exb.prototype=new sb;_.gC=function Hxb(){return yL};_.Pe=function Ixb(a){axb(this.b,a)};_.cM={64:1,75:1};_.b=null;function Kxb(){}
function Lxb(a){this.b=a;wb.call(this);Kxb()}
function Jxb(){}
_=Lxb.prototype=Jxb.prototype=new sb;_.gC=function Mxb(){return zL};_.Oe=function Nxb(a){_wb(this.b,a)};_.cM={63:1,75:1};_.b=null;function Pxb(){}
function Qxb(a){this.b=a;wb.call(this);Pxb()}
function Oxb(){}
_=Qxb.prototype=Oxb.prototype=new sb;_.gC=function Rxb(){return AL};_.Sf=function Sxb(a){var b;if(1==Lzb(a)){b=new Hwb(Sp(Kzb(a)),Tp(Kzb(a)));if(Vwb(this.b,b)||Wwb(this.b,b)){Izb(a);cq(Kzb(a));bq(Kzb(a))}}};_.cM={75:1,104:1};_.b=null;function Uxb(a){a.b=new zi;a.c=Twb(a.f)}
function Vxb(a){if(iI(a.g)){a.g.Xe();a.g=null}kI(a,a.f.i)&&(a.f.i=null)}
function Wxb(a,b){this.f=a;wb.call(this);Uxb(this);this.e=a.g.Qf(this.c,b);this.g=zAb(new _xb(this))}
function Txb(){}
_=Wxb.prototype=Txb.prototype=new sb;_.wd=function Xxb(){var a,b,c,d,e,f,g,i;if(lI(this,this.f.i)){Vxb(this);return false}a=xi(this.b);vwb(this.e,a-this.d);this.d=a;uwb(this.e,a);e=this.f.g.Rf(this.e);e||Vxb(this);hxb(this.f,swb(this.e));d=rI(Cwb(swb(this.e)));c=this.f.t.sg();b=this.f.t.rg();g=this.f.t.vg();f=this.f.t.ug();i=rI(Dwb(swb(this.e)));if((f<=i||g>=i)&&(b<=d||c>=d)){Vxb(this);return false}return e};_.gC=function Yxb(){return CL};_.d=0;_.e=null;_.f=null;_.g=null;function $xb(){}
function _xb(a){this.b=a;wb.call(this);$xb()}
function Zxb(){}
_=_xb.prototype=Zxb.prototype=new sb;_.gC=function ayb(){return BL};_.Ve=function byb(a){Vxb(this.b)};_.cM={72:1,75:1};_.b=null;function dyb(){}
function eyb(a){this.b=a;wb.call(this);dyb()}
function cyb(){}
_=eyb.prototype=cyb.prototype=new sb;_.wd=function fyb(){var a,b,c;a=Ai();b=this.b.r.lf();while(b.Dg()){c=$H(b.Eg(),100);a-kyb(c)>=2500&&b.Fg()}return !this.b.r.uh()};_.gC=function gyb(){return DL};_.b=null;function iyb(){}
function jyb(a){return a.b}
function kyb(a){return a.c}
function lyb(a,b,c){a.b=b;a.c=c}
function myb(){wb.call(this);iyb()}
function nyb(a,b){wb.call(this);iyb();lyb(this,a,b)}
function hyb(){}
_=nyb.prototype=myb.prototype=hyb.prototype=new sb;_.gC=function oyb(){return EL};_.cM={100:1};_.b=null;_.c=0;function Oyb(){syb();iBb(qyb)}
function wzb(a){return Fyb(a)}
function xzb(a){Oyb();Vzb();if(jI(vzb)){vzb=new iB(null,true);Gzb=new Qzb}return eB(vzb,Fzb,a)}
function yzb(a){return a}
function Bzb(a){Qyb(Qe(a))}
function Czb(a){Syb(Qe(a))}
function Dzb(a,b){Uyb(a,b)}
function Hzb(a){}
function Izb(a){a.b=true}
function Jzb(a,b){b.Sf(a);Gzb.d=false}
function Kzb(a){return a.e}
function Lzb(a){return wzb(yzb(Kzb(a)))}
function Mzb(a){return a.b}
function Nzb(a){return a.c}
function Ozb(a){vv(a);a.b=false;a.c=false;a.d=true;a.e=null}
function Pzb(a,b){a.e=b}
function Qzb(){wv.call(this);Hzb(this)}
function Vzb(){jI(Fzb)&&(Fzb=new yw);return Fzb}
function Ezb(){}
_=Qzb.prototype=Ezb.prototype=new hv;_.se=function Rzb(a){Jzb(this,$H(a,104))};_.te=function Tzb(){return Fzb};_.gC=function Uzb(){return JL};_.Wf=function Wzb(){return Mzb(this)};_.Xf=function Xzb(){return Nzb(this)};_.ue=function Yzb(){Ozb(this)};_.Yf=function Zzb(a){Pzb(this,a)};_.b=false;_.c=false;_.d=false;_.e=null;function zAb(a){wAb();IAb();JAb();return yAb(bB(),a)}
function DAb(){wAb();return tp(Fp())}
function EAb(){wAb();return up(Fp())}
function JAb(){if(Ji()&&!vAb){sAb.jg();vAb=true}}
function MAb(){wAb();var a,b;if(vAb){b=EAb();a=DAb();if(uAb!=b||tAb!=a){uAb=b;tAb=a;$A(FAb(),b,a)}}}
var tAb=0,uAb=0,vAb=false;_=PBb.prototype;_.jg=function UBb(){var b=$wnd.onresize;$wnd.onresize=_yj(function(a){try{MAb()}finally{b&&b(a)}})};function rDb(){rDb=Muj;Tc();new GDb}
function sDb(){}
function tDb(a,b){var c;if(iI(a.b)){throw new lKh('Composite.initWidget() may only be called once.')}cI(b,136)&&(a,$H(b,136));Kd(b);c=Yc(b);_c(a,c);oGb(c)&&kGb(lGb(c),a);a.b=b;Md(b,a)}
function uDb(a){if(iI(a.b)){return a.b.ed()}return false}
function vDb(a){if(!Ed(a)){Nd(a.b,a.p);a.p=-1}a.b.fd();Uyb(Yc(a),a);a.jd();FA(a,true)}
function wDb(a,b){Gd(a,b);a.b.gd(b)}
function xDb(){rDb();Od.call(this);sDb()}
function qDb(){}
_=qDb.prototype=new Rc;_.gC=function yDb(){return bM};_.ed=function zDb(){return uDb(this)};_.fd=function ADb(){vDb(this)};_.gd=function BDb(a){wDb(this,a)};_.hd=function CDb(){try{this.kd();FA(this,false)}finally{this.b.hd()}};_.Yc=function DDb(){_c(this,this.b.Yc());return Yc(this)};_.cM={70:1,78:1,105:1,133:1,136:1,137:1,164:1,166:1};_.b=null;function FDb(){}
function GDb(){wb.call(this);FDb()}
function EDb(){}
_=GDb.prototype=EDb.prototype=new sb;_.gC=function HDb(){return aM};function VDb(){}
function WDb(a,b){a.ng(b,(zEb(),sEb),0,null)}
function XDb(a,b,c){a.ng(b,(zEb(),wEb),c,null)}
function YDb(a,b,c){a.ng(b,(zEb(),xEb),c,null)}
function ZDb(a,b,c){a.ng(b,(zEb(),yEb),c,null)}
function $Db(a,b){_Db(a,b,null)}
function _Db(a,b,c){LEb(a.g,b,c)}
function aEb(){}
function bEb(a){var b,c,d,e,f,g,i,j;g=0;j=0;i=0;b=0;for(d=hCb(a).lf();d.Dg();){c=$H(d.Eg(),166);e=$H(Cd(c),121);f=e.c;switch(dg(fEb(e.b))){case 0:jub(f,g,a.i,i,a.i);nub(f,j,a.i,e.d,a.i);j+=e.d;break;case 2:jub(f,g,a.i,i,a.i);iub(f,b,a.i,e.d,a.i);b+=e.d;break;case 3:mub(f,j,a.i,b,a.i);kub(f,g,a.i,e.d,a.i);g+=e.d;break;case 1:mub(f,j,a.i,b,a.i);lub(f,i,a.i,e.d,a.i);i+=e.d;break;case 4:jub(f,g,a.i,i,a.i);mub(f,j,a.i,b,a.i);}}a.e=g+i;a.d=j+b}
function cEb(a){KEb(a.g);bEb(a);Etb(a.f);hEb(a)}
function dEb(a){return jn(Yc(a))/Dtb(a.f,a.i,true)-a.d}
function eEb(a){return kn(Yc(a))/Dtb(a.f,a.i,false)-a.e}
function fEb(a){if(kI(a,(zEb(),vEb))){return ME(PE())?(zEb(),tEb):(zEb(),yEb)}else if(kI(a,(zEb(),uEb))){return ME(PE())?(zEb(),yEb):(zEb(),tEb)}return a}
function gEb(a,b,c,d,e){var f,g,i,j;aEb();Kd(b);f=hCb(a);if(jI(e)){gKb(f,b)}else{i=iKb(f,e);jKb(f,b,i)}kI(c,(zEb(),sEb))&&(a.c=b);j=Ctb(a.f,Yc(b),iI(e)?Yc(e):null,b);g=new WEb(c,d,j);Ld(b,g);ZBb(a,b);$Db(a,0)}
function hEb(a){var b,c;for(c=hCb(a).lf();c.Dg();){b=$H(c.Eg(),166);cI(b,150)&&$H(b,150).og()}}
function iEb(a,b){var c,d;d=nCb(a,b);if(d){kI(b,a.c)&&(a.c=null);c=$H(Cd(b),121);Jtb(a.f,c.c)}return d}
function jEb(a){Tc();oCb.call(this);VDb();this.i=a;_c(this,fp(Fp()));this.f=new Ktb(Yc(this));this.g=new REb(this,this.f)}
function UDb(){}
_=jEb.prototype=UDb.prototype=new WBb;_.gC=function kEb(){return gM};_.ng=function lEb(a,b,c,d){gEb(this,a,b,c,d)};_.jd=function mEb(){Htb(this.f)};_.og=function nEb(){hEb(this)};_.kd=function oEb(){Itb(this.f)};_.kg=function pEb(a){return iEb(this,a)};_.cM={70:1,78:1,105:1,133:1,134:1,137:1,150:1,164:1,166:1};_.c=null;_.d=0;_.e=0;_.f=null;_.g=null;_.i=null;function zEb(){zEb=Muj;wEb=new BEb('NORTH',0);tEb=new BEb('EAST',1);xEb=new BEb('SOUTH',2);yEb=new BEb('WEST',3);sEb=new BEb(HDj,4);vEb=new BEb('LINE_START',5);uEb=new BEb('LINE_END',6);rEb=NH(eqb,{515:1,538:1},120,[wEb,tEb,xEb,yEb,sEb,vEb,uEb])}
function AEb(){}
function BEb(a,b){fg.call(this,a,b);AEb()}
function DEb(a){zEb();return og((GEb(),FEb),a)}
function EEb(){zEb();return rEb}
function qEb(){}
_=BEb.prototype=qEb.prototype=new $f;_.gC=function CEb(){return dM};_.cM={120:1,515:1,525:1,527:1};var rEb,sEb,tEb,uEb,vEb,wEb,xEb,yEb;function GEb(){GEb=Muj;FEb=hg((zEb(),rEb))}
var FEb;function JEb(){}
function KEb(a){a.d=true}
function LEb(a,b,c){a.e=b;a.c=c;a.d=false;if(!a.g){a.g=true;Bj().td(a)}}
function MEb(a){wb.call(this);JEb();this.f=a}
function IEb(){}
_=IEb.prototype=new sb;_.pg=function NEb(){};_.xd=function OEb(){this.g=false;if(this.d){return}this.pg();Gtb(this.f,this.e,new fGb(this))};_.gC=function PEb(){return qM};_.c=null;_.d=false;_.e=0;_.f=null;_.g=false;function QEb(){}
function REb(a,b){this.b=a;MEb.call(this,b);QEb()}
function HEb(){}
_=REb.prototype=HEb.prototype=new IEb;_.pg=function SEb(){bEb(this.b)};_.gC=function TEb(){return eM};_.b=null;function VEb(){}
function WEb(a,b,c){wb.call(this);VEb();this.b=a;this.d=b;this.c=c}
function UEb(){}
_=WEb.prototype=UEb.prototype=new sb;_.gC=function XEb(){return fM};_.cM={121:1};_.b=null;_.c=null;_.d=0;function bFb(a){Tc();dFb.call(this,a?jp(Fp()):fp(Fp()),a)}
function iFb(a,b){QDb(a.c,b,false);aFb(a)}
function jFb(){fFb();bFb.call(this,false);gFb();ed(this,'gwt-Label')}
_=jFb.prototype=ZEb.prototype;function $Fb(a,b){a.b=b}
function eGb(){}
function fGb(a){this.b=a;wb.call(this);eGb()}
function dGb(){}
_=fGb.prototype=dGb.prototype=new sb;_.gC=function gGb(){return pM};_.Gf=function hGb(){iI(this.b.c)&&this.b.c.Gf()};_.Hf=function iGb(a,b){var c;c=$H(hub(a),166);cI(c,150)&&$H(c,150).og();iI(this.b.c)&&this.b.c.Hf(a,b)};_.b=null;function kGb(b,a){b.__gwt_resolve=mGb(a)}
function lGb(a){return a}
function mGb(a){return function(){this.__gwt_resolve=nGb;return a.Yc()}}
function nGb(){throw 'A PotentialElement cannot be resolved twice.'}
function oGb(b){try{return !!b&&!!b.__gwt_resolve}catch(a){return false}}
function YGb(){}
function ZGb(a,b){return a.zg(b)?0:xn(b)-kn(b)}
function $Gb(a,b){return a.zg(b)?kn(b)-xn(b):0}
function _Gb(){wb.call(this);YGb()}
function aHb(){jI(XGb)&&(XGb=new _Gb);return XGb}
function WGb(){}
_=_Gb.prototype=WGb.prototype=new sb;_.gC=function bHb(){return wM};_.yg=function cHb(a,b){};_.zg=function dHb(a){var b=$doc.defaultView.getComputedStyle(a,null);return b.getPropertyValue('direction')==Vzj};var XGb=null;function tHb(){}
function uHb(a){return ZGb(aHb(),zHb(a))}
function vHb(a){return un(zHb(a))-jn(zHb(a))}
function wHb(a){return $Gb(aHb(),zHb(a))}
function xHb(){return 0}
function yHb(a){return wn(zHb(a))}
function zHb(a){return a.c}
function AHb(a){CHb(a,false);Cq(zn(a.c),(Rs(),Ps));Cq(zn(a.b),(Rs(),Ps));Dq(zn(a.c),IDj,JDj);Dq(zn(a.b),IDj,JDj);FHb(a,false);aHb().yg(a.c,a.b)}
function BHb(a){return jI(a.d)}
function CHb(a,b){Aq(zn(zHb(a)),b?(js(),hs):(js(),fs))}
function DHb(a,b){Jn(zHb(a),b)}
function EHb(a,b){Kn(zHb(a),b)}
function FHb(a,b){if(b==BHb(a)){return b}if(b){gxb(a.d,null);a.d=null}else{a.d=lxb(a)}return BHb(a)}
function GHb(a,b){EHb(a,b)}
function HHb(){Tc();lHb.call(this);tHb();this.c=Yc(this);this.b=Qe(fp(Fp()));Um(this.c,this.b);AHb(this)}
function eHb(){}
_=HHb.prototype=eHb.prototype=new fHb;_.gC=function IHb(){return xM};_.Ag=function JHb(){return this.b};_.qg=function KHb(){return vn(zHb(this))};_.rg=function LHb(){return uHb(this)};_.ug=function MHb(){return vHb(this)};_.sg=function NHb(){return wHb(this)};_.vg=function OHb(){return xHb()};_.wg=function PHb(){return yHb(this)};_.fd=function QHb(){Fd(this);Dzb(zHb(this),this)};_.hd=function RHb(){Dzb(zHb(this),null);Hd(this)};_.og=function SHb(){var a;a=this.Bg();iI(a)&&cI(a,150)&&$H(a,150).og()};_.Zc=function THb(a){bd(this,a)};_.tg=function UHb(a){DHb(this,a)};_.xg=function VHb(a){GHb(this,a)};_._c=function WHb(a){fd(this,a)};_.cM={70:1,78:1,105:1,133:1,134:1,137:1,150:1,164:1,166:1};_.b=null;_.c=null;_.d=null;function fIb(){}
function gIb(a,b,c){var d,e;d=$H(Cd(b),121);e=null;switch(dg(fEb(d.b))){case 3:e=new vIb(a,b,false);break;case 1:e=new vIb(a,b,true);break;case 0:e=new JIb(a,b,false);break;case 2:e=new JIb(a,b,true);}gEb(a,e,d.b,a.b,c)}
function hIb(){Tc();iIb.call(this,8)}
function iIb(a){jEb.call(this,(Ct(),Bt));fIb();this.b=a;ed(this,'gwt-SplitLayoutPanel');if(jI(eIb)){eIb=fp(Fp());Cq(zn(eIb),(Rs(),Ns));Iq(zn(eIb),0,(Ct(),Bt));xq(zn(eIb),0,(Ct(),Bt));yq(zn(eIb),0,(Ct(),Bt));Bq(zn(eIb),0,(Ct(),Bt));tq(zn(eIb),0,(Ct(),Bt));Dq(zn(eIb),KDj,LDj);zq(zn(eIb),0)}}
function dIb(){}
_=hIb.prototype=dIb.prototype=new UDb;_.gC=function jIb(){return EM};_.ng=function kIb(a,b,c,d){gEb(this,a,b,c,d);lI(b,(zEb(),sEb))&&gIb(this,a,d)};_.kg=function lIb(a){var b;b=kCb(this,a);if(iEb(this,a)){b<jCb(this)&&iEb(this,iCb(this,b));return true}return false};_.cM={70:1,78:1,105:1,133:1,134:1,137:1,150:1,164:1,166:1};_.b=0;var eIb=null;function oIb(){}
function pIb(a){var b;b=a.Hg();if(a.j!=b){a.j=b;a.c=b}return uLh($H(Cd(a.k),121).d+a.c,0)}
function qIb(a,b){var c,d;d=pIb(a);b>d&&(b=d);b<a.e&&(b=a.e);c=$H(Cd(a.k),121);if(b==c.d){return}a.c+=c.d-b;c.d=b;if(jI(a.d)){a.d=new EIb(a);Bj().sd(a.d)}}
function rIb(a,b,c){this.n=a;Od.call(this);oIb();this.k=b;this.i=c;_c(this,fp(Fp()));Nd(this,78)}
function nIb(){}
_=nIb.prototype=new Rc;_.gC=function sIb(){return CM};_.gd=function tIb(a){var b,c,d;switch(wzb(a)){case 4:this.f=true;d=vLh(EAb(),Cp(Fp()));b=vLh(DAb(),zp(Fp()));wq(zn(eIb),b,(Ct(),Bt));Jq(zn(eIb),d,(Ct(),Bt));Um(qp(Fp()),eIb);this.g=this.Ig(a)-this.Gg();Czb(Yc(this));bq(a);break;case 8:this.f=false;an(eIb);Bzb(Yc(this));bq(a);break;case 64:if(this.f){this.i?(c=this.Jg()+this.Kg()-this.Ig(a)-this.g):(c=this.Ig(a)-this.Jg()-this.g);qIb(this,c);bq(a)}}};_.cM={70:1,78:1,105:1,133:1,137:1,154:1,164:1,166:1};_.c=0;_.d=null;_.e=0;_.f=false;_.g=0;_.i=false;_.j=0;_.k=null;_.n=null;function uIb(){}
function vIb(a,b,c){this.b=a;rIb.call(this,a,b,c);uIb();Gq(zn(Yc(this)),fzj,a.b);ed(this,'gwt-SplitLayoutPanel-HDragger')}
function mIb(){}
_=vIb.prototype=mIb.prototype=new nIb;_.Gg=function wIb(){return Wc(this)};_.Hg=function xIb(){return eEb(this.b)};_.gC=function yIb(){return AM};_.Ig=function zIb(a){return Sp(a)};_.Jg=function AIb(){return Wc(this.k)};_.Kg=function BIb(){return this.k.Wc()};_.cM={70:1,78:1,105:1,133:1,137:1,154:1,164:1,166:1};_.b=null;function DIb(){}
function EIb(a){this.b=a;wb.call(this);DIb()}
function CIb(){}
_=EIb.prototype=CIb.prototype=new sb;_.xd=function FIb(){this.b.d=null;cEb(this.b.n)};_.gC=function GIb(){return BM};_.cM={102:1};_.b=null;function IIb(){}
function JIb(a,b,c){this.b=a;rIb.call(this,a,b,c);IIb();Gq(zn(Yc(this)),dzj,a.b);ed(this,'gwt-SplitLayoutPanel-VDragger')}
function HIb(){}
_=JIb.prototype=HIb.prototype=new nIb;_.Gg=function KIb(){return Xc(this)};_.Hg=function LIb(){return dEb(this.b)};_.gC=function MIb(){return DM};_.Ig=function NIb(a){return Tp(a)};_.Jg=function OIb(){return Xc(this.k)};_.Kg=function PIb(){return this.k.Vc()};_.cM={70:1,78:1,105:1,133:1,137:1,154:1,164:1,166:1};_.b=null;_=KDg.prototype;_.rd=function PDg(){var a;a=new _Ug;Um(CGb(),Yc(a))};function ZUg(){ZUg=Muj;rDb();YUg=new eVg}
function $Ug(){}
function _Ug(){ZUg();xDb.call(this);$Ug();tDb(this,$H(YUg.Tf(this),166))}
function XUg(){}
_=_Ug.prototype=XUg.prototype=new qDb;_.gC=function aVg(){return u8};_.cM={70:1,78:1,105:1,133:1,136:1,137:1,164:1,166:1,487:1};var YUg;function cVg(){}
function dVg(){var a,b,c,d,e,f,g,i,j,k;a=new oVg;a.cA();d=new jFb;e=new jFb;f=new jFb;i=new HHb;j=new hIb;g=new jFb;k=new kJb;c=new aGb;b=new jEb((Ct(),zt));iFb(d,'Header');XDb(b,d,21.3);iFb(e,'Equation Editor');ZDb(j,e,128);iFb(f,'Graphics view');hHb(i,f);WDb(j,i);$Fb(c,(BFb(),xFb));iFb(g,'Command line');XFb(c,g);XFb(c,k);YDb(b,c,10);WDb(b,j);b.Zc(MDj);b._c(MDj);a.cA().Kf();return b}
function eVg(){wb.call(this);cVg()}
function bVg(){}
_=eVg.prototype=bVg.prototype=new sb;_.Tf=function fVg(a){return dVg($H(a,487))};_.gC=function gVg(){return t8};function kVg(){kVg=Muj;iVg=new oVg}
function lVg(){}
function mVg(){return yVg()}
function nVg(a){jVg=new uVg(a)}
function oVg(){kVg();wb.call(this);lVg()}
function hVg(){}
_=oVg.prototype=hVg.prototype=new sb;_.gC=function pVg(){return s8};_.cA=function qVg(){return mVg()};var iVg,jVg=null;function sVg(){}
function tVg(){return '.GALL4V3CEI{font-weight:bold;}'}
function uVg(a){this,a;wb.call(this);sVg()}
function rVg(){}
_=uVg.prototype=rVg.prototype=new sb;_.Kf=function vVg(){if(!this.b){this.b=true;Iu(tVg());return true}return false};_.gC=function wVg(){return r8};_.cM={94:1};_.b=false;function xVg(){xVg=Muj;nVg((kVg(),iVg))}
function yVg(){xVg();return kVg(),jVg}
var FI=xJh(NDj,ODj,'rb',Bab),yI=xJh(NDj,'Animation$1','Pb',Bab),EI=xJh(NDj,'AnimationScheduler','Ub',Bab),zI=xJh(NDj,'AnimationScheduler$AnimationHandle','Zb',Bab),DI=xJh(NDj,'AnimationSchedulerImpl','bc',EI),CI=xJh(NDj,'AnimationSchedulerImplTimer','hc',DI),BI=xJh(NDj,'AnimationSchedulerImplTimer$AnimationHandleImpl','Jc',zI),Opb=wJh('[Lcom.google.gwt.animation.client.','AnimationSchedulerImplTimer$AnimationHandleImpl;','zH',BI),AI=xJh(NDj,'AnimationSchedulerImplTimer$1','pc',LL),PI=xJh(xCj,'Duration','vi',Bab),uJ=yJh(CCj,'Style$Display','xr',qab,Ir,Hr),Xpb=wJh(GCj,'Style$Display;','zH',uJ),qJ=yJh(CCj,'Style$Display$1','Jr',uJ,null,null),rJ=yJh(CCj,'Style$Display$2','Or',uJ,null,null),sJ=yJh(CCj,'Style$Display$3','Tr',uJ,null,null),tJ=yJh(CCj,'Style$Display$4','Yr',uJ,null,null),zJ=yJh(CCj,'Style$Overflow','ds',qab,os,ns),Ypb=wJh(GCj,'Style$Overflow;','zH',zJ),vJ=yJh(CCj,'Style$Overflow$1','ps',zJ,null,null),wJ=yJh(CCj,'Style$Overflow$2','us',zJ,null,null),xJ=yJh(CCj,'Style$Overflow$3','zs',zJ,null,null),yJ=yJh(CCj,'Style$Overflow$4','Es',zJ,null,null),EJ=yJh(CCj,'Style$Position','Ls',qab,Ws,Vs),Zpb=wJh(GCj,'Style$Position;','zH',EJ),AJ=yJh(CCj,'Style$Position$1','Xs',EJ,null,null),BJ=yJh(CCj,'Style$Position$2','at',EJ,null,null),CJ=yJh(CCj,'Style$Position$3','ft',EJ,null,null),DJ=yJh(CCj,'Style$Position$4','kt',EJ,null,null),PJ=xJh(CCj,'StyleInjector$1','Mu',Bab),QJ=xJh(CCj,'StyleInjector$StyleInjectorImpl','Ru',Bab),nK=xJh(PDj,'TouchEvent$TouchSupportDetector','$z',Bab),tK=xJh(JCj,'ResizeEvent','UA',vK),iL=xJh(QDj,'Layout','ytb',Bab),eL=xJh(QDj,'Layout$1','Mtb',FI),fL=yJh(QDj,'Layout$Alignment','Utb',qab,cub,bub),cqb=wJh('[Lcom.google.gwt.layout.client.','Layout$Alignment;','zH',fL),gL=xJh(QDj,'Layout$Layer','fub',Bab),hL=xJh(QDj,'LayoutImpl','qub',Bab),sL=xJh(RDj,'DefaultMomentum','gwb',Bab),tL=xJh(RDj,'Momentum$State','nwb',Bab),uL=xJh(RDj,SDj,'Awb',Bab),FL=xJh(RDj,'TouchScroller','Nwb',Bab),vL=xJh(RDj,'TouchScroller$1','pxb',Bab),wL=xJh(RDj,'TouchScroller$2','uxb',Bab),xL=xJh(RDj,'TouchScroller$3','zxb',Bab),yL=xJh(RDj,'TouchScroller$4','Exb',Bab),zL=xJh(RDj,'TouchScroller$5','Jxb',Bab),AL=xJh(RDj,'TouchScroller$6','Oxb',Bab),CL=xJh(RDj,'TouchScroller$MomentumCommand','Txb',Bab),BL=xJh(RDj,'TouchScroller$MomentumCommand$1','Zxb',Bab),DL=xJh(RDj,'TouchScroller$MomentumTouchRemovalCommand','cyb',Bab),EL=xJh(RDj,'TouchScroller$TemporalPoint','hyb',Bab),JL=xJh(pCj,'Event$NativePreviewEvent','Ezb',vK),bM=xJh(rCj,'Composite','qDb',TM),aM=xJh(rCj,'Composite_HTMLTemplatesImpl','EDb',Bab),gM=xJh(rCj,'DockLayoutPanel','UDb',_L),dM=yJh(rCj,'DockLayoutPanel$Direction','qEb',qab,EEb,DEb),eqb=wJh(SCj,'DockLayoutPanel$Direction;','zH',dM),qM=xJh(rCj,'LayoutCommand','IEb',Bab),eM=xJh(rCj,'DockLayoutPanel$DockAnimateCommand','HEb',qM),fM=xJh(rCj,'DockLayoutPanel$LayoutData','UEb',Bab),pM=xJh(rCj,'LayoutCommand$1','dGb',Bab),wM=xJh(rCj,'ScrollImpl','WGb',Bab),xM=xJh(rCj,'ScrollPanel','eHb',zM),EM=xJh(rCj,'SplitLayoutPanel','dIb',gM),CM=xJh(rCj,'SplitLayoutPanel$Splitter','nIb',TM),AM=xJh(rCj,'SplitLayoutPanel$HSplitter','mIb',CM),BM=xJh(rCj,'SplitLayoutPanel$Splitter$1','CIb',Bab),DM=xJh(rCj,'SplitLayoutPanel$VSplitter','HIb',CM),u8=xJh(TDj,'GeoGebraAppFrame','XUg',bM),t8=xJh(TDj,'GeoGebraAppFrame_GeoGebraAppFrameUiBinderImpl','bVg',Bab),s8=xJh(TDj,'GeoGebraAppFrame_GeoGebraAppFrameUiBinderImpl_GenBundle_default_InlineClientBundleGenerator','hVg',Bab),r8=xJh(TDj,'GeoGebraAppFrame_GeoGebraAppFrameUiBinderImpl_GenBundle_default_InlineClientBundleGenerator$1','rVg',Bab);_yj(ak)(1);