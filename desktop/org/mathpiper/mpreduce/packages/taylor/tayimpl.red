module tayimpl;

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


%*****************************************************************
%
%      Functions for computing Taylor expansions of implicit
%       or inverse functions
%
%*****************************************************************


exports implicit_taylor, inverse_taylor;

imports

% from the REDUCE kernel:
        !*f2q, !*n2f, diffsq, errorp, errorset!*, invsq, mkquote,
        mk!*sq, mvar, negsq, numr, quotsq, typerr, simp!*,

% from the header module:
        has!-taylor!*, make!-Taylor!*, Taylor!-kernel!-sq!-p,
        TayMakeCoeff,

% from module taybasic:
        addtaylor, multtaylor, multtaylorsq,

% from module taydiff:
        difftaylor,

% from module tayexpnd:
        taylorexpand,

% from module taysubst:
        subsubtaylor;


symbolic procedure implicit_taylor(f,x,y,x0,y0,n);
%   if not fixp n or n < 0 then typerr(n,"expansion order") else
   begin scalar x,l,!*tayexpanding!*;
     f := simp!* f;
     if not null numr subsq(f,{x . x0,y . y0})
       then Taylor!-error('implicit_taylor,
              "      Input expression non-zero at given point");
     !*tayexpanding!* := t;
     l := {'implicit_taylor1,
            mkquote f,
            mkquote x,
            mkquote y,
            mkquote x0,
            mkquote y0,
            mkquote n};
     x := errorset!*(l,!*trtaylor);
     if not errorp x then return car x
      else Taylor!-error('implicit_taylor,nil)
   end;

symbolic procedure implicit_taylor1(f,x,y,x0,y0,n);
   begin scalar ft,fn,f1,g;
    if n <= 0
      then return make!-Taylor!*({TayMakeCoeff({{0}},simp!* y0)},
                                 {{{x},x0,n,n+1}},nil,nil);
    ft := quotsq(negsq diffsq(f,x),diffsq(f,y));
    f1 := taylorexpand(ft,{{{x},x0,n,n+1}});
    if not Taylor!-kernel!-sq!-p f1 then typerr(f,"implicit function");
    fn := f1 := mvar numr f1;
    g := {TayMakeCoeff({{1}},simp!* subsubtaylor({x . x0,y . y0},f1)),
          TayMakeCoeff({{0}},simp!* y0)};
    for i := 2 : n do
      <<fn := multtaylorsq(
                addtaylor(difftaylor(fn,x),
                          multtaylor(difftaylor(fn,y),f1)),
              invsq !*f2q !*n2f i);
        g := TayMakeCoeff({{i}},
                          simp!* subsubtaylor({x . x0,y . y0},fn))
              . g>>;
    return construct!-Taylor!*(reversip g,x,x0,n)
  end;

symbolic operator implicit_taylor;

symbolic procedure construct!-Taylor!*(cfl,x,x0,n);
   if not has!-Taylor!* cfl
     then make!-Taylor!*(cfl,{{{x},x0,n,n+1}},nil,nil)
    else mk!*sq
           taylorexpand(simp!* prepTaylor!*1(cfl,{{{x},x0,n,n+1}},nil),
                        {{{x},x0,n,n+1}});

symbolic operator implicit_taylor;

symbolic procedure inverse_taylor(f,y,x,y0,n);
   begin scalar x,l,!*tayexpanding!*;
     !*tayexpanding!* := t;
     l := {'inverse_taylor1,
            mkquote simp!* f,
            mkquote x,
            mkquote y,
            mkquote subeval {{'replaceby,y,y0},f},
            mkquote y0,
            mkquote n};
     x := errorset!*(l,!*trtaylor);
     if not errorp x then return car x
      else Taylor!-error('inverse_taylor,nil)
   end;

symbolic procedure inverse_taylor1(f,x,y,x0,y0,n);
   begin scalar fn,f1,g;
    if n < 0 then n := 0;
    f1 := taylorexpand(invsq diffsq(f,y),{{{y},y0,n,n+1}});
    if not Taylor!-kernel!-sq!-p f1 then typerr(f,"implicit function");
    fn := f1 := mvar numr f1;
    g := {TayMakeCoeff({{1}},simp!* subsubtaylor({y . y0},f1)),
          TayMakeCoeff({{0}},simp!* y0)};
    for i := 2 : n do
      <<fn := multtaylorsq(multtaylor(difftaylor(fn,y),f1),
                           invsq !*f2q !*n2f i);
        g := TayMakeCoeff({{i}},simp!* subsubtaylor({y . y0},fn)) . g>>;
    return construct!-Taylor!*(reversip g,x,x0,n)
  end;

symbolic operator inverse_taylor;

endmodule;

end;
