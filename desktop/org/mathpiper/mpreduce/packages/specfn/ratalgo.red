module ratalgo;

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


% rational algorithm for FPS package

algebraic procedure Complexapart(f,x);

    begin scalar !*factor,!*complex;
        on factor,complex;
        x := pf(f,x);
        off factor,complex;
        return x;
   end;

algebraic procedure ratalgo(p,x);
     begin scalar afp,tt,S,ak,d,c,j,ss;
     afp := Complexapart(p,x);

     S:= 0; ak := 0;

     if symbolic !*traceFPS then write " Rational Algorithm applied";

     foreach tt in afp do
        << if freeof(tt,x) then S := S + tt else
           <<
           d := 1/tt;
           if not polynomq(d,x) then <<
                if symbolic !*traceFPS then write
                         " Rational Algorithm  failed";
                S := -1 >>;
          if not (S = -1) then
          <<
           d := d/lcof(d,x); c := d * tt;
           J := deg(d,x);
           d := expt(d,1/j);
           if not polynomq(d,x) then <<
                if symbolic !*traceFPS then write
                        " Rational Algorithm  failed";
                afp := {}; d :=12;
                S := -1 >>;
           if d = x then S := S + c/d^j  else
                <<
                ss := lcof(d,x);
                d := d /ss; c := c / ss;
                xk := x -d; c:= c*(-1)^j/xk ^j;
                ak  := ak +
                   c*simplify_factorial(factorial(j + k -1)/
                        factorial(k)/factorial(j-1))/xk^k;
        >> >> >> >>;
        if S = -1 then return (-1);
        return S := S + infsum(ak*x^k,k,0,infinity)
    end;

symbolic procedure fastpart(x,n);
        reval nth( x,n +1);

flag ('(fastpart fastlength),'opfn);

symbolic procedure fastlength(x);
        length (x) -1;

symbolic procedure auxcopy(u);
   if pairp u then cons (auxcopy car u, auxcopy cdr u) else u;

% for XR

if getd 'print_format then
        print_format('(pochhammer u v),'(!( u !) !_ v));

endmodule;

end;



