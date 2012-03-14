module kronf;   % Kronecker factorization of univariate forms.

% Author: Anthony C. Hearn.

% Based on code first written by Mary Ann Moore and Arthur C. Norman.

% Copyright (c) 1987 The RAND Corporation. All rights reserved.

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


% exports linfacf,quadfacf;

% imports zfactor;

% Note that only linear and quadratic factors are found here.

symbolic procedure linfacf u; trykrf(u,'(0 1));

symbolic procedure quadfacf u; trykrf(u,'(-1 0 1));

symbolic procedure trykrf(u,points);
   % Look for factor of u by evaluation at points and interpolation.
   % Return (fac . cofac), with fac = nil if none found,
   % and cofac = nil if nothing worthwhile is left.
   begin scalar attempt,mv,values;
      if null u then return nil . nil
       else if length points > ldeg u then return nil . u;
      % Degree is too small to find factors.
      mv := mvar u;
      values := for each j in points collect subuf(j,u);
      if 0 member values
        then <<attempt := ((mv .** 1) .* 1) . -1;   % mv - 1
               return attempt . quotf(u,attempt)>>;
      values := for each j in values collect dfactors j;
      values := for each j in values
                   collect append(j,for each k in j collect !:minus k);
      attempt := search4facf(u,values,nil);
      if null attempt then attempt := nil . u;
      return attempt
   end;

symbolic procedure subuf(u,v);
   % Substitute integer u for main variable in univariate polynomial v.
   % Return an integer or a structured domain element.
   begin scalar z;
      if u=0 then u := nil;
      z := nil;
      while v do
         if domainp v then <<z := adddm!*(v,z); v := nil>>
          else <<if u then z := adddm!*(multdm!*(u**ldeg v,lc v),z);
                 % we should do better here.
                 v := red v>>;
      return if null z then 0 else z
   end;

symbolic procedure adddm!*(u,v);
   % Adds two domain elements u and v, returning a standard form.
   if null u then v else if null v then u else adddm(u,v);

symbolic procedure multdm!*(u,v);
   % Multiplies two domain elements u and v, returning a standard form.
   if null u or null v then nil else multdm(u,v);

symbolic procedure dfactors n;
   % Produces a list of all (positive) factors of the domain element n.
   begin scalar x;
      if n=0 then return list 0
       else if n=1 then return list 1
       else if !:minusp n then n := !:minus n;
      return if not atom n
        then if (x := get(car n,'factorfn))
               then combinationtimes apply1(x,n)
              else list n
       else combinationtimes zfactor n
   end;

symbolic procedure combinationtimes fl;
   if null fl then list 1
    else begin scalar n,c,res,pr;
        n := caar fl;
        c := cdar fl;
        pr := combinationtimes cdr fl;
        while c>=0 do <<res := putin(expt(n,c),pr,res); c := c-1>>;
        return res
    end;

symbolic procedure putin(n,l,w);
   if null l then w else putin(n,cdr l,(n*car l) . w);

symbolic procedure search4facf(u,values,cv);
   % combinatorial search for factors. cv gets current value set.
   if null values then tryfactorf(u,cv)
    else begin scalar q,w;
      w := car values;
 loop: if null w then return nil;   % no factor found
      q := search4facf(u,cdr values,car w . cv);
      if null q then <<w := cdr w; go to loop>>;
      return q
    end;

symbolic procedure tryfactorf(u,cv);
   % Tests if cv represents a factor of u.
   % For the time being, does not work on structured domain elements.
   begin scalar w;
      if null atomlis cv then return nil;
      if null cddr cv then w := linethroughf(cadr cv,car cv,mvar u)
       else w := quadthroughf(caddr cv,cadr cv,car cv,mvar u);
      if w eq 'failed or null (u := quotf(u,w)) then return nil
       else return w . u
   end;

symbolic procedure linethroughf(y0,y1,mv);
   begin scalar x;
      x := y1-y0;
      if x=0 then return 'failed
       else if x<0 then <<x:= -x; y0 := -y0>>;
       return if y0 = 0 or gcdn(x,y0) neq 1 then 'failed
               else (mv .** 1) .* x .+ y0
   end;

symbolic procedure quadthroughf(ym1,y0,y1,mv);
   begin scalar x,y,z;
      x := divide(ym1+y1,2);
      if cdr x=0 then x := car x-y0 else return 'failed;
      if x=0 then return 'failed;
      z := y0;
      y := divide(y1-ym1,2);
      if cdr y=0 then y := car y else return 'failed;
      if gcdn(x,gcdn(y,z)) neq 1 then return 'failed;
      if x<0 then <<x := -x; y := -y; z := -z>>;
      if z=0 then return 'failed
       else if y=0 then return ((mv .** 2) .* x) .+ z
       else return ((mv .** 2) .* x) .+ (((mv .** 1) .* y) .+ z)
   end;

endmodule;

end;


