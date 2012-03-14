module fourdom; % Domain definitions for angles and fourier series

% Author: John Fitch 1991.

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


global '(domainlist!*);

domainlist!*:=union('(!:fs!:),domainlist!*);

put('fourier,'tag,'!:fs!:);
put('!:fs!:,'dname,'fourier);
flag('(!:fs!:),'field); %% Should be ring really
put('!:fs!:,'i2d,'i2fourier);
put('!:fs!:,'minusp,'fs!:minusp!:);
put('!:fs!:,'plus,'fs!:plus!:);
put('!:fs!:,'times,'fs!:times!:);
put('!:fs!:, 'expt,'fs!:expt!:);
put('!:fs!:,'difference,'fs!:difference!:);
put('!:fs!:,'quotient,'fs!:quotient!:);
put('!:fs!:, 'divide, 'fs!:divide!:);
put('!:fs!:, 'gcd, 'fs!:gcd!:);
put('!:fs!:,'zerop,'fs!:zerop!:);
put('!:fs!:,'onep,'fs!:onep!:);
put('!:fs!:,'prepfn,'fs!:prepfn!:);
put('!:fs!:,'specprn,'fs!:prin!:);
put('!:fs!:,'prifn,'fs!:prin!:);
put('!:fs!:,'intequivfn,'fs!:intequiv!:);
flag('(!:fs!:),'ratmode);
% conversion functions

put('!:fs!:,'!:mod!:,mkdmoderr('!:fs!:,'!:mod!:));
% put('!:fs!:,'!:gi!:,mkdmoderr('!:fs!:,'!:gi!:));
% put('!:fs!:,'!:rn!:,mkdmoderr('!:fs!:,'!:rn!:));
put('!:rn!:,'!:fs!:,'!*d2fourier);
put('!:ft!:,'!:fs!:,'cdr);
put('!:gi!:,'!:fs!:,'!*d2fourier);
put('!:gf!:,'!:fs!:,'!*d2fourier);

put('expt, '!:fs!:, 'fs!:expt!:);

% Conversion functions

symbolic procedure i2fourier u;
  if dmode!*='!:fs!: then !*d2fourier u else u;

symbolic procedure !*d2fourier u;
if null u then nil else
begin scalar fourier;
      fourier:=mkvect 3;
      fs!:set!-coeff(fourier,(u . 1));
      fs!:set!-fn(fourier,'cos);
      fs!:set!-angle(fourier,fs!:make!-nullangle());
      fs!:set!-next(fourier,nil);
     return get('fourier,'tag) . fourier
end;

symbolic procedure !*sq2fourier u;
if null car u then nil else
begin scalar fourier;
      fourier:=mkvect 3;
      fs!:set!-coeff(fourier,u);
      fs!:set!-fn(fourier,'cos);
      fs!:set!-angle(fourier,fs!:make!-nullangle());
      fs!:set!-next(fourier,nil);
     return get('fourier,'tag) . fourier
end;

symbolic procedure fs!:minusp!:(x); fs!:minusp cdr x;

symbolic procedure fs!:minusp x;
if null x then nil else
   if null fs!:next x then minusf car fs!:coeff x
   else fs!:minusp fs!:next x;

%% Basic algebraic operations

symbolic procedure fs!:times!:(x,y);
% This function seems to be called with numeric values as well
   if null x then nil else if null y then nil
   else if numberp y
    then get('fourier,'tag) . fs!:timescoeff(y ./ 1, cdr x)
   else if numberp x
    then get('fourier,'tag) . fs!:timescoeff(x ./ 1, cdr y)
   else if not eqcar(x, get('fourier,'tag)) then
        get('fourier,'tag) . fs!:timescoeff(x,cdr y)
   else if not eqcar(y, get('fourier,'tag)) then
        get('fourier,'tag) . fs!:timescoeff(y,cdr x)
   else get('fourier,'tag) . fs!:times(cdr x, cdr y);

symbolic procedure fs!:timescoeff(x, y);
if null y then nil
   else begin scalar ans, coeff;
      coeff := multsq(x,fs!:coeff y);
      if coeff = '(nil . 1) then <<
        print "zero in times";
        return fs!:timescoeff(x, fs!:next y) >>;
      ans := mkvect 3;
      fs!:set!-coeff(ans,coeff);
      fs!:set!-fn(ans,fs!:fn y);
      fs!:set!-angle(ans,fs!:angle y);
      fs!:set!-next(ans, fs!:timescoeff(x, fs!:next y));
      return ans
   end;

symbolic procedure fs!:times(x,y);
if null x then nil else if null y then nil else
begin scalar ans;
        ans := fs!:timesterm(x, y);
        return fs!:plus(ans, fs!:times(fs!:next  x, y));
end;

symbolic procedure fs!:timesterm(x,y);
% Treat x as a term and y as a tree
if null y then nil else if null x then nil else
begin scalar ans;
        ans := fs!:timestermterm(x,y);
        return fs!:plus(ans, fs!:timesterm(x, fs!:next y));
end;

symbolic procedure fs!:timestermterm(x,y);
% x and y are terms.  Generate the two answer terms.
begin scalar sum, diff, ans, xv, yv, coeff;
        sum := mkvect 7;
        xv := fs!:angle x;
        yv := fs!:angle y;
        for i:=0:7 do putv!.unsafe(sum,i,
                                 getv!.unsafe(xv,i)+getv!.unsafe(yv,i));
        diff := mkvect 7;
        for i:=0:7 do putv!.unsafe(diff,i,
                                 getv!.unsafe(xv,i)-getv!.unsafe(yv,i));
        coeff := multsq(fs!:coeff x, fs!:coeff y);
        coeff := multsq(coeff, '(1 . 2));
        if null car coeff then return nil;
        if fs!:fn x = 'sin then
            if fs!:fn y = 'sin then
                % sin x*sin y => [-cos(x+y)+cos(x-y)]/2
                return fs!:plus(make!-term('cos, sum, negsq coeff),
                                make!-term('cos,diff, coeff))
            else % fs!:fn y = 'cos
                % sin x * cos y => [sin(x+y)+sin(x-y)]/2
                return fs!:plus(make!-term('sin, sum, coeff),
                                make!-term('sin, diff,coeff))
        else % fs!:fn x='cos
            if fs!:fn y = 'sin then
                % cos x*sin y => [sin(x+y)-sin(x-y)]/2
                return fs!:plus(make!-term('sin, sum, coeff),
                                make!-term('sin,diff, negsq coeff))
            else % fs!:fn y = 'cos
                % cos x * cos y => [cos(x+y)+cos(x-y)]/2
                return fs!:plus(make!-term('cos, sum, coeff),
                                make!-term('cos, diff,coeff))

end;

symbolic procedure fs!:expt!:(x,n);
begin scalar ans, xx;
    ans := cdr !*d2fourier 1;
    x := cdr x;
    for i:=1:n do ans := fs!:times(ans,x);
    return get('fourier,'tag) . ans;
end;

symbolic procedure make!-term(fn, ang, coeff);
begin scalar fourier, sign, i;
      sign := 0;
      i:=0;
top:  if getv!.unsafe(ang,i)<0 then sign := -1
      else if getv!.unsafe(ang,i)>0 then sign := 1
      else if i=7 then <<
        if fn ='sin then return nil >>
      else << i := i #+ 1; goto top >>;
      fourier:=mkvect 3;
      if sign = 1 or fn = 'cos then fs!:set!-coeff(fourier,coeff)
      else fs!:set!-coeff(fourier, multsq('(-1 . 1), coeff));
      fs!:set!-fn(fourier,fn);
      if sign = -1 then << sign := mkvect 7;
        for i:=0:7 do putv!.unsafe(sign,i,-getv!.unsafe(ang,i));
        ang := sign
      >>;
      fs!:set!-angle(fourier,ang);
      fs!:set!-next(fourier,nil);
     return fourier
end;

symbolic procedure fs!:quotient!:(x,y);
if numberp y then fs!:times!:(x, !*sq2fourier (1 ./ y))
else rerror(fourier, 98, "Unimplemented");

symbolic procedure fs!:divide!:(x,y);
rerror(fourier, 98, "Unimplemented");

symbolic procedure fs!:gcd!:(x,y);
rerror(fourier, 98, "Unimplemented");

symbolic procedure fs!:difference!:(x,y);
   fs!:plus!:(x, fs!:negate!: y);

symbolic procedure fs!:negate!: x;
  get('fourier,'tag) . fs!:negate cdr x;

symbolic procedure fs!:negate x;
   if null x then nil
   else begin scalar ans;
      ans := mkvect 3;
      fs!:set!-coeff(ans,negsq fs!:coeff x);
      fs!:set!-fn(ans,fs!:fn x);
      fs!:set!-angle(ans,fs!:angle x);
      fs!:set!-next(ans, fs!:negate fs!:next x);
      return ans
   end;

symbolic procedure fs!:zerop!:(u);
  null u or
  (not numberp u and
   null cdr u or
   (null fs!:next cdr u and
   ((numberp v and zerop v) where v=fs!:coeff cdr u)));

symbolic procedure fs!:onep!:(u); fs!:onep cdr u;

symbolic procedure fs!:onep u;
  null fs!:next u and
  onep fs!:coeff u and fs!:null!-angle u and fs!:fn(u) = 'cos;

symbolic procedure fs!:prepfn!:(x); x;

symbolic procedure simpfs u; u;

put('!:fs!:,'simpfn,'simpfs);

%% PRINTING FUNCTIONS

%% We have all the usual problems of unit coefficients, and zero angles

smacro procedure zeroterm x; fs!:coeff x = '(nil  . 1);

symbolic procedure fs!:prin!:(x);
  << prin2!* "["; fs!:prin cdr x; prin2!* "]" >>;

symbolic procedure fs!:prin x;
   if null x then prin2!* " 0 " else <<
   while x do <<
     fs!:prin1 x;
     x := fs!:next x;
     if x then prin2!* " + "
   >>
>>;

symbolic procedure fs!:prin1 x;
begin scalar first, u, v;
   first := t;
   if not(fs!:coeff x = '(1 . 1)) then <<
      prin2!* "("; sqprint fs!:coeff x;
      prin2!* ")" >>;
   if not(fs!:null!-angle x) then <<
     prin2!* fs!:fn x;
     prin2!* "[";
     u := fs!:angle x;
     for i:=0:7 do
         if not((v := getv!.unsafe(u,i)) = 0) then <<
            if v<0 then << first := t; prin2!* "-"; v := -v >>;
            if not first then prin2!* "+";
            if not(v=1) then prin2!* v;
            first := nil;
            prin2!* getv!.unsafe(fourier!-name!*, i)
     >>;
     prin2!* "]"
  >>
  else if fs!:coeff x = '(1 . 1) then prin2!* "1"
end;

symbolic procedure fs!:intequiv!:(u);
   null fs!:next x and
   fs!:null!-angle x and
   fs!:fn(x) = 'cos and
   fixp car fs!:coeff x and
   cdr fs!:coeff x = 1
        where x = cdr u;

endmodule;

end;
