% ----------------------------------------------------------------------
% $Id: bcint.red 84 2009-02-07 07:53:22Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1999-2009 Andreas Dolzmann and Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
% 

lisp <<
   fluid '(bcint_rcsid!* bcint_copyright!*);
   bcint_rcsid!* := "$Id: bcint.red 84 2009-02-07 07:53:22Z thomas-sturm $";
   bcint_copyright!* := "Copyright (c) 1999-2009 A. Dolzmann and T. Sturm"
>>;

module bcint;
% Implementation of base coefficients using integers.

procedure bc_zero();
   0;

procedure bc_zero!?(u);
   eqn(u,0);

procedure bc_abs(u);
   abs u;

procedure bc_one!?(u);
   eqn(u,1);

procedure bc_2sq(u);
   simp u;

procedure bc_a2bc(u);
   % Converts the algebraic (kernel) u into a base coefficient.
   u;

procedure bc_fd(a);
   a;

procedure bc_neg(u);
   % Base coefficient negative. u is a base coefficient. bc_neg(u)
   % returns the negative of the base coefficient u, a base
   % coefficient.
   -u;

procedure bc_prod(a,b);
   if eqn(a,1) then
      b
   else if eqn(b,1) then
      a
   else
      times2(a,b);

procedure bc_quot(a,b);
   if eqn(b,1) then
      a
   else
      quotientx(a,b);

procedure bc_sum(a,b);
   % Base coefficient sum. u and v are base coefficients. bcsum(u,v)
   % calculates u+v and returns a base coefficient.
   if eqn(a,0) then
      b
   else if eqn(b,0) then
      a
   else
      plus2(a,b);

procedure bc_pmon(var,dg);
   % Parameter monomial.
   rederr "parametric coefficients not supported over bcint";

procedure bc_minus!?(u);
   % Boolean function. Returns true if u is a negative base coeff.
   u < 0;

procedure bc_2a(u);
   % Returns the prefix equivalent of the base coefficient u.
   u;

procedure bc_gcd(u,v);
   gcdn(u,v);

procedure bc_mkat(op,bc);
   {op,numr simp bc,nil};

procedure bc_dcont(bc);
   bc;

procedure bc_2d(bc);
   bc;

procedure bc_vars(bc);
   nil;

endmodule;  % bcint

end;  % of file
