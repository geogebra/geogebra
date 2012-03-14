module hilbertp;% Computing Hilbert Polynomial from the Hilbert series.

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


symbolic procedure newhilbi(bas,var,vars);
 begin scalar baslt,n,u,grad,h,joa,a,ii,dim0,varx,dmode!*,!*modular;
 % Extract leading terms .
 baslt:= for each p in cdr bas  collect
 << u:=hgspliteval list(p,vars);cadr u >>;
 % Replace non atomic elements in the varlist by gensyms .
 for each x in cdr vars do
  if(pairp x)then
    baslt:=cdr subeval {{'equal,x,gensym()},' list . baslt};
 varx:=!*a2f var;
%%%%%%%%%%%%%%%
 if not(cdaar varx =1 and cdar varx =1 and null cdr varx)then
   <<terpri();
     prin2 "***** a value of >" ;prin2 var;prin2 "< has been set;";
     terpri();
     var:=gensym();varx:=!*a2f var;
     prin2 "***** >";prin2 var;prin2 "< is selected as variable.";
     terpri()>>;
%%%%%%%%%%%%%%%
 % Compute the Hilbertseries .
 joa:=hilbsereval list(' list . baslt,var);
 % Get the Hilbert polynomial .
 grad:=deg(joa,var);
 a:=for i:=0 : grad collect coeffn(joa,var,i);
 n:= length cdr vars;
% dim0:=( for i:=1 : n product(var + i)) /(for i:=1 : n product i);
 dim0:=1;
 for i:=1 : n do dim0:=multf(addd(i,varx),dim0);
 dim0:=multsq(dim0 ./ 1,1 ./(for i:=1 : n product i));
 h:=multsq(car a ./ 1,dim0);
 a:=cdr a;
 ii:=0;
 while a do
 << dim0:=multsq(dim0,addf(varx,numr simp(minus ii))
                        ./ addf(varx,numr simp(n - ii)));
  ii:=ii + 1;
  if not(car a = 0)then h:=addsq(h,multsq(car a ./ 1,dim0));
  a:=cdr a  >>;
 return mk!*sq h end;

symbolic procedure psnewhilbi u;
begin scalar zz,pl,vl;pl:=reval car u;
 if cdr u then vl:=listeval(cadr u,nil);
 zz:='list.groebnervars(cdr pl,vl);
 return  newhilbi(pl,'x,zz)end;

put('hilbertpolynomial,'psopfn,'psnewhilbi);

symbolic procedure hgspliteval pars;
% A variant of Gsplit from grinterf.red.
% Split a polynomial into leading monomial and reductum.
begin scalar vars,x,u,v,w,oldorder,!*factor,!*exp;
 integer n,pcount!*;!*exp:=t;
 n:=length pars;
 u:=reval car pars;
 v:=if n > 1 then reval cadr pars else nil;
 u:={'list,u};
 w:=for each j in groerevlist u
  collect if eqexpr j then !*eqn2a j else j;
 vars:=groebnervars(w,v);
 if not vars then   vdperr ' hilbertpolynomial;
 oldorder:=vdpinit vars;
 w:=a2vdp car w;
 if vdpzero!? w then x:=w else
%     <<x:=vdpfmon(vdplbc w,vdpevlmon w);
 <<x:=vdpfmon('(1 . 1),vdpevlmon w);w:=vdpred w>>;
 w:={' list,vdp2a x,vdp2a w};
 setkorder oldorder;
 return w end;

% Simple Array access method for one- and two-dimensional arrays .
% NO check against misusage is done !

% Usage:   Rar:=makeRarray list dim1;Rar:=makeRarray {dim1,dim2};
%          val:=getRarray(Rar,ind1);val:=getrarray(Rar,ind1,ind2);
%          putRarray(Rar,ind1,val); PutRarray(Rar,in1,ind2,val);

% For two dimensional array access only !

macro procedure functionindex2 u;
begin scalar dims,ind1,ind2;
 dims:=cadr u;ind1:=caddr u;ind2:=cadddr u;
 return         %%%%((ind1 #- 1) #* cadr dims) #+ ind2;
  {'iplus2,ind2,{'itimes2,{'cadr,dims},
                              {'iplus2,ind1,-1}}} end;

macro procedure getrarray u;
begin scalar arry,inds;
 arry:=cadr u;inds:=cddr u;
 if length inds = 1 then
  return  {'getv,{'cdr,arry},car inds}
  else return {'getv,{'cdr,arry},
                    'functionIndex2.{'car,arry}.inds} end;

symbolic procedure makerarray dims;
begin scalar u,n;
 n:=for each i in dims product i;
 u:=mkvect n;return dims . u end;

macro procedure putrarray u;
begin scalar arry,inds,val;
 arry:=cadr u;
 inds:=cddr u;
 val:=nth(u,length u);   % PSL: lastcar u;
 if length inds = 2 then
  return  {'putv,{'cdr,arry},car inds,val}
  else return {'putv,{'cdr,arry},'functionindex2 .
               {' car,arry}.car inds.cadr inds.nil,val} end;

symbolic procedure hilbertzerodimp(nrall,n,rarray);
begin integer i,k,count,vicount;
 while(( i:=i+1)<= nrall and count < n)do
 begin vicount:=1;
  for k:=1 : n do
   if(getrarray(rarray,i,k)= 0)then vicount:=vicount + 1;
  if vicount = n  then count:=count + 1;
  end;return count = n end;

symbolic procedure groezerodim!?(f,n);
begin scalar explist,a;integer r;
           %explist:= list( vev(lt(f1)),...,vev(lt(fr)));
 explist:= for each fi in f collect vdpevlmon fi;
 r:= length f;
 a:=makerarray {r,n};
 for i:=1 step 1 until r do
  for k:=1 step 1 until n do
   putrarray(a,i,k,nth(nth(explist,i),k));
 return hilbertzerodimp(r,n,a)end;

symbolic procedure gzerodimeval u;
begin scalar vl;
 if cdr u then vl:=reval cadr u;return gzerodim1(reval car u,vl)end;

put('gzerodim!?,'psopfn,'gzerodimeval);

symbolic procedure gzerodim1(u,v);
begin scalar vars,w,oldorder;
 w:=for each j in getrlist u
  collect if eqexpr j then !*eqn2a j else j;
 if null w then rerror(groebnr2,21,"empty list in hilbertpolynomial");
 vars:=groebnervars(w,v);
 oldorder:=vdpinit vars;
 w:=for each j in w collect f2vdp numr simp j;
 w:=groezerodim!?(w,length vars);
 setkorder oldorder;
 return if w then newhilbi(u,'x,'list.v)else nil end;

symbolic procedure gbtest g;
% Test,if the given set of polynomials is a Groebner basis .
% Only fast to compute plausilbility test .
begin scalar fredu,g1,r,s;
 g:=vdplsort g;
        % Make abbreviated version of g .
 g1:= for each p in g collect
 <<r:=vdpred p;
  if vdpzero!? r then p else
               vdpsum(vdpfmon(vdplbc p,vdpevlmon p),
                        vdpfmon(vdplbc r,vdpevlmon r))>>;
 while g1 do
 <<for each p in cdr g1 do
     if not groebbuchcrit4t(vdpevlmon car g1,vdpevlmon p)then
     << s:=groebspolynom(car g1,p);
      if not vdpzero!? s and
         null groebsearchinlist(vdpevlmon s,cddr g1)
          then rerror(groebnr2,22,
             "****** Not a Groebner basis wrt current ordering")>>;
      if groebsearchinlist(vdpevlmon car g1,cdr g1)then fredu:=t;
      g1:=cdr g1>>;
 if fredu then
 <<terpri!* t;
  prin2t "WARNING: system is not a fully reduced Groebner basis";
  prin2t "with current term ordering">> end;

endmodule;; end;
