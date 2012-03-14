module sqfrnorm;

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


fluid '(!*pvar listofallsqrts);

global '(modevalcount);

modevalcount:=1;

exports sqfr!-norm2,res!-sqrt;

%symbolic procedure resultant(u,v);
%begin
%  scalar maxdeg,zeroes,ldegu,ldegv,m;
%  % we can have gone makemainvar on u and v;
%  ldegu:=ldeg u;
%  ldegv:=ldeg v;
%  maxdeg:=isub1 max2(ldegu,ldegv);
%  zeroes:=nlist(nil,maxdeg);
%  u:=remake(u,mvar u,ldegu);
%  v:=remake(v,mvar v,ldegv);
%  m:=nil;
%  ldegu:=isub1 ldegu;
%  ldegv:=isub1 ldegv;
%  for i:=0 step 1 until ldegv do
%    m:=append(ncdr(zeroes,maxdeg-ldegv+i),
%              append(u,ncdr(zeroes,maxdeg-i))).m;
%  for i:=0 step 1 until ldegu do
%    m:=append(ncdr(zeroes,maxdeg-ldegu+i),
%              append(v,ncdr(zeroes,maxdeg-i))).m;
%  return detqf m
%  end;

% symbolic procedure ncdr(l,n);
%   % we can use small integer arithmetic here.
%   if n=0 then l else ncdr(cdr l,isub1 n);

%symbolic procedure remake(u,v,w);
%% remakes u into a list of sf's representing its coefficients;
%if w iequal 0 then list u
%  else if (pairp u) and (mvar u eq v) and (ldeg u iequal w)
%    then (lc u).remake(red u,v,isub1 w)
%    else (nil ).remake(    u,v,isub1 w);

%fluid '(n); %needed for the mapcar;

%symbolic procedure detqf u;
%   %u is a square matrix standard form.
%%  %value is the determinant of u.
%%  %algorithm is expansion by minors of first row/column;
%   begin integer n;
%   scalar x,y,z;
%        if length u neq length car u then rederr "Non square matrix"
%         else if null cdr u then return caar u;
%        if length u < 3
%          then go to noopt;
%        % try to remove a row with only one non-zero in it;
%        z:=1;
%        x:=u;
%      loop:
%        n:=posnnonnull car x;
%        if n eq t
%          then return nil;
%        % special test for all null;
%        if n then <<
%          y:=nth(car x,n);
%          % next line is equivalent to:
%%           onne of n,z is even;
%          if evenp (n+z-1)
%            then y:=negf y;
%          u:=remove(u,z);
%          return !*multf(y,detqf remove2 u) >>;
%       x:=cdr x;
%       z:=z+1;
%       if x
%         then go to loop;
%     noopt:
%        x := u;
%        n := 1;                 %number of current row/column;
%        z := nil;
%        if nonnull car u < nonnullcar u
%         then go to row!-expand;
%        u:=mapcar(u,function cdr);
%    a:  if null x then return z;
%        y := caar x;
%        if null y then go to b
%         else if evenp n then y := negf y;
%        z := addf(!*multf(y,detqf remove(u,n)),z);
%    b:  x := cdr x;
%        n := iadd1 n;
%        go to a;
%      row!-expand:
%        u:=cdr u;
%        x:=car x;
%      aa:
%        if null x then return z;
%        y:=car x;
%        if null y
%          then go to bb
%          else if evenp n then y:=negf y;
%        z:=addf(!*multf(y,detqf remove2 u),z);
%      bb:
%        x:=cdr x;
%        n:=iadd1 n;
%        go to aa
%   end;
%
%
%symbolic procedure remove2 u;
%mapcar(u,function (lambda x;
%                    remove(x,n)));
%
%unfluid '(n);
%
%symbolic procedure nonnull u;
%if null u
%  then 0
%  else if null car u
%    then nonnull cdr u
%    else iadd1 (nonnull cdr u);
%
%
%symbolic procedure nonnullcar u;
%if null u
%  then 0
%  else if null caar u
%    then nonnullcar cdr u
%    else iadd1 (nonnullcar cdr u);
%
%
%
%symbolic procedure posnnonnull u;
%% returns t if u has no non-null elements
%% nil if more than one
%% else position of the first;
%begin
%  scalar n,x;
%  n:=1;
%loop:
%  if null u
%    then return
%      if x
%        then x
%        else t;
%  if car u
%    then if x
%      then return nil
%      else x:=n;
%  n:=iadd1 n;
%  u:=cdr u;
%  go to loop
%  end;


symbolic procedure res!-sqrt(u,a);
% Evaluates resultant of u ( as a poly in its mvar) and x**-a.
begin
  scalar x,n,v,k,l;
  x:=mvar u;
  n:=ldeg u;
  n:=quotient(n,2);
  v:=mkvect n;
  putv(v,0,1);
  for i:=1:n do
    putv(v,i,!*multf(a,getv(v,i-1)));
  % now substitute for x**2 in u leaving k*x+l.
  k:=l:=nil;
  while u do
    if mvar u neq x
      then <<
        l:=addf(l,u);
        u:=nil >>
      else <<
        if evenp ldeg u
          then l:=addf(l,!*multf(lc u,getv(v,(ldeg u)/2)))
          else k:=addf(k,!*multf(lc u,getv(v,(ldeg u -1)/2)));
        u:=red u >>;
  % now have k*x+l,x**2-a, giving l*l-a*k*k.
  return addf(!*multf(l,l),!*multf(negf a,multf(k,k)))
  end;


symbolic procedure sqfr!-norm2 (f,mvarf,a);
begin
  scalar u,w,aa,ff,resfn;
  resfn:='resultant;
  if eqcar(a,'sqrt)
    then <<
      resfn:='res!-sqrt;
      aa:=!*q2f simp argof a >>
    else rerror(algint,1,"Norms over transcendental extensions");
  f:=pvarsub(f,a,'! gerbil);
  w:=nil;
  if involvesf(f,'! gerbil) then goto l1;
increase:
  w:=addf(w,!*p2f mksp(a,1));
  f:=!*q2f algint!-subf(f,list(mvarf . list('plus,mvarf,
                                            list('minus,'! gerbil))));
l1:
  u:=apply2(resfn,makemainvar(f,'! gerbil),aa);
  ff:=nsqfrp(u,mvarf);
  if ff
    then go to increase;
  f:=!*q2f algint!-subf(f,list('! gerbil.a));
  % cannot use pvarsub since want to squash higher powers.
  return list(u,w,f)
  end;

symbolic procedure nsqfrp(u,v);
begin
  scalar w;
  w:=modeval(u,v);
  if w eq 'failed
    then go to normal;
  if atom w
    then go to normal;
  if ldegvar(w,v) neq ldegvar(u,v)
    then go to normal;
%  printc "Modular image is:";
%  printsf w;
  w:=gcdf(w,partialdiff(w,v));
%  printc "Answer is:";
%  printsf w;
  if w iequal 1
    then return nil;
normal;
  w:=gcdf(u,partialdiff(u,v));
  if involvesf(w,v)
    then return w
    else return nil
  end;

symbolic procedure ldegvar(u,v);
if atom u
  then 0
  else if mvar u eq v
    then ldeg u
    else if ordop(v,mvar u)
      then 0
      else max2(ldegvar(lc u,v),ldegvar(red u,v));


symbolic procedure modeval(u,v);
if atom u
  then u
  else if v eq mvar u
    then begin
      scalar w,x;
      w:=modeval(lc u,v);
      if w eq 'failed
        then return w;
      x:=modeval(red u,v);
      if x eq 'failed
        then return x;
      if null w
        then return x
        else return (lpow u .* w) .+ x
      end
    else begin
      scalar w,x;
      x:=mvar u;
      if not atom x
        then if dependsp(x,v)
          then return 'failed;
      x:=modevalvar x;
      if x eq 'failed
        then return x;
      w:=modeval(lc u,v);
      if w eq 'failed
        then return w;
      if x
        then w:=multf(w,exptf(x,ldeg u));
      x:=modeval(red u,v);
      if x eq 'failed
        then return x;
      return addf(w,x)
      end;


symbolic procedure modevalvar v;
   begin scalar w;
      if atom v
        then <<if (w := get(v,'modvalue)) then return w;
               put(v,'modvalue,modevalcount);
               modevalcount := modevalcount+1;
               return modevalcount-1>>
       else if car v neq 'sqrt
        then <<if !*tra then <<princ "Unexpected algebraic:"; print v>>;
               error1()>>
       else if numberp argof v then return (mksp(v,1) .* 1) .+ nil;
      w := modeval(!*q2f simp argof v,!*pvar);
      w := assoc(w,listofallsqrts);
      % The variable does not matter, since we know it does not depend.
      if w then return cdr w else return 'failed
   end;

% unglobal '(modevalcount);

endmodule;

end;
