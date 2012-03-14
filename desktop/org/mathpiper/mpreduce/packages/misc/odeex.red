%  A SIMPLE PROGRAM FOR COMPUTING SOLUTIONS OF ODES BY TAYLOR SERIES.

% Author: Andreas Strotmann <strotmann@rrz.uni-koeln.de>.

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


algebraic;

% 1. The simplest case.

% Compute the first N terms of the Taylor series of the solution of
% the explicit ordinary first order differential equation
% y' = f(x,y)
% in a neighborhood of x0 *if* f is holomorphic in x and y at (x0,y0).

PROCEDURE detaylor(f,x,y,x0,y0,N);

begin scalar wj, pot;
  wj:=f; pot:=x-x0;
  return ( y0+sub({x=x0,y=y0},f)*(x-x0) +
            for j:=2:n sum
              << wj:= df(wj,x)+f*df(wj,y);
                 pot := pot*(x-x0)/j;
               sub({x=x0,y=y0},wj)*pot>>);
end;

% Example: y'=xy

detaylor(x*y,x,y,0,1,5);

% 2. The general case.

% Vectors (= systems of ODEs) are encoded as lists.

% 2.1 Auxiliaries.

infix lplusl;

precedence lplusl,+;

procedure x lplusl y; % vector + vector
  begin scalar auxy;
    auxy:= y;
    return
     foreach xi in x collect <<auxy:= rest auxy; s>>
        where s= first auxy+ xi;
  end;

infix ltimesl;

precedence ltimesl,*;

procedure x ltimesl y; % vector * vector -> scalar
  begin scalar auxy;
    auxy:= y;
    return
     foreach xi in x sum <<auxy:= rest auxy; s>> where s=first auxy* xi;
  end;

infix ltimess;

precedence ltimess,*;

procedure x ltimess y; % vector * scalar  -> vector
  foreach xi in x collect y*xi;

% 2.2 The central procedure.

% Compute the first N terms of the Taylor series of the solution of
% the initial value problem
% (y1,...,yn)'=(f1(x,y1,...,yn), ... , fn(x,y1,...,yn))
%   such that  y1(x0)=y10, ..., yn(x0)=yn0
% for a system of explicit ordinary first order differential equations
% in a neighborhood of x0 *if* f is holomorphic in x and all the yi at
% (x0, y10,....,yn0).
%
% Input format:  flis={f1,...,fn},
%                Anfangswerte={x=x0, y1=y10,..., yn=yn0}
%
% NOTE: none of the yi may DEPEND on x (i.e., be symbols declared to
%       do so).
%       The yi MUST be symbols so DF can handle them.

procedure odetaylor(flis,Anfangswerte,N);
begin scalar pot,x,y,x0,y0,wj,res;
 % Split args (see comment above for format):
 x:=  lhs first Anfangswerte;
 x0:= rhs first Anfangswerte;
 y:=  for each gl in rest Anfangswerte collect lhs gl;
 y0:= for each gl in rest Anfangswerte collect rhs gl;
% Initialisations (= degree one of the taylor polynomial)
res:= y0 lplusl (sub(Anfangswerte,flis) ltimess (x-x0));
pot:= x-x0;
 wj:= flis;
% Main loop:
for j:=2:n do
   << wj:= foreach wij in wj
             collect df(wij,x) + (flis ltimesl
                                    foreach yk in y  % one row of the
                                                     % Jacobian
                                      collect df(wij,yk));  %of wj wrt y
% The above DFs should be PARTDFs, really. In REDUCE 3.4, maybe they
%    can...
     pot := pot*(x-x0)/j;
     res:= res lplusl (sub(Anfangswerte,wj) ltimess pot);
           %should be sub...
   >>;
 % DONE:
 return res;
end;

% Examples:

factor x;

% y''=-y.

odetaylor({yprime,-y}, {x=0,y=0,yprime=1}, 4);

% And something wild just for fun:

odetaylor({sin y2, cos(x*y1*y2)}, {x=0,y1=pi/2, y2=pi*7/4}, 4);

end;
