module afactor;

% Author: James H. Davenport.

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


fluid '(!*galois !*noextend !*sqfree !*trfield afactorvar listofnewsqrts
        monicpart varlist zlist sqrtlist sqrtflag indexlist);

switch trfield;   % traces the algebraic number field manipluation

exports afactor, jfactor;
imports exptf,ordop,!*multf,addf,makemainvar,algebraicsf,divsf,contents;
imports quotf!*,negf,sqfr!-norm2,prepf,algint!-subf,!*q2f;
imports printsf;

% internal!-fluid '(monicpart);

% This routine, and its subsidiaries, do factorization over algebraic
% extensions, by the Trager modification of van der Waerden's algorithm
% See SYMSAC '76.

symbolic procedure afactor(u,v);
  % Factorises U over the algebraics as a polynomial in V (=afactorvar).
begin
  scalar afactorvar,!*noextend,!*sqfree;
  % !*sqfree is known to be square free (from sqfr-norm).
  !*noextend:=t; % else we get recursion.
  afactorvar:=v;
  if !*trfield
    then <<
    princ "We must factorise the following over: ";
    for each u in listofnewsqrts do <<princ u; princ " " >>;
    terpri();
    printsf u >>;
  v:=algfactor u;
  if !*trfield then <<
    printc "factorizes as ";
    mapc(v,function printsf) >>;
  return v
  end;


symbolic procedure algfactor2(f,a);
if null a
  then for each u in cdr factorf f collect %car factorf is a constant
    if cdr u = 1
      then car u
      else interr "repeated factors found while processing algebraics"
  else if algebraicsf f
    then algfactor3(f,a)
    else begin
      scalar w;
      if !*trfield then <<
        princ "to be factorized over ";
        for each u in a do << princ u; princ " " >>;
        terpri();
        printsf f >>;
      if  (!*galois neq 2) and
          (numberp red f) and
          (not numberp argof car a)
        then return algfactor2(f,cdr a);
        % assumes we need never express a root of a number in terms of
        % non-numbers.
      w:=algfactor2(f,nil);
      if w and null cdr w then return algfactor3(f,a)
        else return 'partial . w
      end;


symbolic procedure algfactor3(f,a);
begin
  scalar ff,w,gg,h,p;
  w:=sqfr!-norm2(f,mvar f,car a);
  !*sqfree:=car w;
  w:=cdr w;
  ff:=algfactor2(!*sqfree,cdr a);
  if null ff then return list f         % does not factor.
   else if car ff eq 'partial then <<p := 'partial; ff := cdr ff>>;
  if null cdr ff then return list f;    % does not factor.
  a:=car a;
  gg:=cadr w;
  w:=list list(afactorvar,'plus,afactorvar,prepf car w);
  h:=for each u in ff
       collect (!*q2f algint!-subf(gcdinonevar(u,gg,afactorvar),w));
  if p eq 'partial then h := p.h;
  return h
  end;

symbolic procedure algfactor u;
begin
  scalar a,aa,z,w,monicpart;
  z:= makemainvar(u,afactorvar);
  if ldeg z iequal 1
    then return list u;
  z:=lc z;
  if z iequal 1
    then go to monic;
  if algebraicsf z
    then u:=!*multf(u,numr divsf(1,z));
    % this de-algebraicises the top coefficient.
  u:=quotf!*(u,contents(u,afactorvar));
  z:=makemainvar(u,afactorvar);
  if lc z neq 1
    then if lc z iequal -1
      then u:=negf u
      else <<
        w:=lc z;
        u:=makemonic z >>;
monic:
  aa:=listofnewsqrts;
  if algebraicsf u
    then go to normal;
  a:=cdr aa;
  % we need not try for the first one, since algfactor2
  % will do this for us.
  z:=t;
  while a and z do begin
    scalar alg,v;
    alg:=car a;
    a:=cdr a;
    v:=algfactor3(u,list alg);
    if null cdr v
      then return;
    if car v eq 'partial
      then v:=cdr v;
      % we do not mind if the factorization is only partial.
    a:=mapcan(v,function algfactor);
    z:=nil
    end;
  monicpart:=w;
  if null z
    then if null w
      then return a
      else return for each j in a collect demonise j;
normal:
  z:=algfactor2(u,aa);
  monicpart:=w;
  if null cdr z or (car z neq 'partial)
    then if null w
      then return z
      else return for each j in z collect demonise j;
  % does not factor.
  if null w
    then return mapcan(cdr z,function algfactor)
    else return for each u in z conc
                  algfactor demonise u;
  end;


symbolic procedure demonise u;
% Replaces afactorvar by afactorvar*monicpart in u.
if atom u
  then u
  else if afactorvar eq mvar u
    then addf(demonise red u,
              !*multf(lt u .+ nil,exptf(monicpart,ldeg u)))
    else if ordop(afactorvar,mvar u)
      then u
      else addf(demonise red u,
                !*multf(!*p2f lpow u,demonise lc u));

symbolic procedure gcdinonevar(u,v,x);
% Gcd of u and v, regarded as polynomials in x.
if null u
  then v
  else if null v
    then u
    else begin
      scalar u1,v1,z,w;
      u1:=stt(u,x);
      v1:=stt(v,x);
    loop:
      if (car u1) > (car v1)
        then go to ok;
      z:=u1;u1:=v1;v1:=z;
      z:=u;u:=v;v:=z;
    ok:
      if car v1 iequal 0
        then interr "Coprimeness in gcd";
      z:=gcdf(cdr u1,cdr v1);
      w:=quotf(cdr u1,z);
      if (car u1) neq (car v1)
        then w:=!*multf(w,!*p2f mksp(x,(car u1)-(car v1)));
      u:=addf(!*multf(v,w),
              !*multf(u,negf quotf(cdr v1,z)));
      if null u
        then return v;
      u1:=stt(u,x);
      go to loop
      end;

symbolic procedure makemonic u;
% U is a makemainvar'd polynomial.
begin
  scalar v,w,x,xx;
  v:=mvar u;
  x:=lc u;
  xx:=1;
  w:=!*p2f lpow u;% the monic term.
  u:=red u;
  for i:=(isub1 ldeg w) step -1 until 1 do begin
    if atom u
      then go to next;
    if mvar u neq v
      then go to next;
    if ldeg u iequal i
      then w:=addf(w,!*multf(lc u,
                     !*multf(!*p2f lpow u,xx)));
    u:=red u;
  next:
    xx:=!*multf(x,xx)
    end;
  w:=addf(w,!*multf(u,xx));
  return w
  end;

symbolic procedure jfactor(sf,var);
   % Algebraic integrator's sole interface to factorization.
   % except for a direct call to the true factoriser from
   % inside afactor
begin
  scalar varlist,zlist,indexlist,sqrtlist,sqrtflag;
  scalar prim,res,answer,u,v,x,y; % scalar var2
  prim:=jsqfree(sf,var);
  indexlist:=createindices zlist;
  while not null prim do <<
    x:=car prim;
    while not null x do <<
        y:=facbypp(car x,varlist);
        while not null y do <<
            res:=append(int!-fac car y,res);
            y:=cdr y >>;
        x:=cdr x >>;
    prim:=cdr prim >>;
  while res do <<
    if caar res eq 'log then <<
        u:=cdar res;
        u:=!*multsq(numr u ./ 1,1 ./ cdr stt(numr u,var));
        v:=denr u;
        while not atom v do v:=lc v;
        if  (numberp v) and (0> v)
          then u:=(negf numr u) ./ (negf denr u);
        if u neq '(1 . 1) then answer := u . answer>>
      else if caar res eq 'atan then nil
      % We can ignore this, since we also get LOG (U**2+1) as another
      % term.
      else interr "Unexpected term in jfactor";
    res:=cdr res >>;
  return answer
  end;

% unfluid '(monicpart);

endmodule;

end;
