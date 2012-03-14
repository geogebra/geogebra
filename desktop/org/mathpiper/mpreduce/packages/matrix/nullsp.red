module nullsp;  % Compute the nullspace (basis vectors) of a matrix.

% Author: Herbert Melenk <melenk@sc.zib-berlin.de>.

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


% Algorithm:  Rational Gaussian elimination with standard qutotients.

put('nullspace,'psopfn,'nullspace!-eval);

symbolic procedure nullspace!-eval u;
   % interface for the nullspace calculation.
   begin scalar v,n,matinput;
     v := reval car u;
     if eqcar(v,'MAT) then
        <<matinput:=t; v := cdr v>>
     else if eqcar(v,'LIST) then
      v := for each row in cdr v collect
        if not eqcar(row,'LIST) then typerr ("matrix",u) else
        <<row := cdr row;
          if null n then n := length row else
          if n neq length row
            then rerror(matrix,15,"lists not in matrix shape");
          row>> else rerror(matrix,16,"Not a matrix");
     v := nullspace!-alg v;
     return 'list . for each vect in v collect
         if matinput then 'MAT . for each x in vect collect list x
           else 'LIST . vect;
   end;

symbolic procedure nullspace!-alg(m);
   % "M" is a Matrix, encoded as list of lists(=rows) of algebraic
   % expressions.
   % Result is the basis of the kernel of M in the same encoding.
   begin scalar mp,vars,rvars,r,res,oldorder; integer n;
     n := length car m;
     vars := for i:=1:n collect gensym();
     rvars := reverse vars;
     oldorder := setkorder rvars;
     mp := for each row in m collect
     <<r := nil ./ 1;
      for each col in pair(vars,row) do
         r := addsq(r,simp list('times,car col,cdr col));
      r>>;
     res := nullspace!-elim(mp,rvars);
     setkorder oldorder;
     return reverse for each q in res collect
       for each x in vars collect
        cdr atsoc(x,q);
   end;

symbolic procedure nullspace!-elim(m,vars);
   % "M" is a matrix encoded as list of linear polnomials (sq's) in
   % the variables "vars". The current korder cooresponds to vars.
   % Result is a basis for the null space of the matrix, encoded
   % as list of vectors, where each vector is an alist over vars.
   % A rational Gaussian elimination is performed and unit vectors
   % are substituted for the remaining unrestricted variables.
  begin scalar c,s,x,w,arbvars,depvars,row,res,break;
     while vars and not break do
     <<for each p in m do
        if domainp numr p then if numr p then break:=t %unsolvable
                                else m:=delete(p,m);
       if not break then
       <<x:=car vars; vars:=cdr vars; row:=nil;
            % select row with x as leading variable.
         for each p in m do
           if null row and mvar numr p = x then row:=p;
            % if none, then x is a free variable.
        if null row then arbvars:=x.arbvars else
        <<m:=delete(row,m);
          c:=multsq(negf denr row ./1, 1 ./ lc numr row);
          row := multsq(row,c);
            % collect formula for x,
          depvars := (x . (red numr row ./ denr row)) . depvars;
            % and perform elimination with this row.
          m:=for each p in m collect
          <<if mvar numr p=x then
              <<p:=addsq(p, multsq(row,lc numr p ./ denr p));
                 % Simplification for non-numeric coefficients.
                if not domainp (w:=numr p) and not domainp lc w then
                    p:=subs2!* p>>;
            p>>;
        >>;
      >>;
    >>;
    if break then return nil;
         % Construct solutions by assigning unit vectors to the
         % free variables and perform backsubstitution.
    for each x in arbvars do
    << s := for each y in arbvars collect
          (y . if y=x then 1 else 0);
       c := 1;
       for each y in depvars do
       << s := (car y . prepsq (w:=subsq(cdr y,s))) . s;
          c := lcm!*(c,denr w)
       >>;
       if not(c=1) then <<c:=prepf c;
         s:=for each q in s collect car q.reval{'times,cdr q,c}>>;
       res := s . res;
    >>;
    return res;
  end;

endmodule;

end;
