module maxmin;  % Support for generalized MAX and MIN.

% Author: F.J. Wright, QMW, London (fjw@maths.qmw.ac.uk) 7/7/90.

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


% Provide support for the MAX and MIN functions to allow:-
%     any number domain;
%     any symbolic arguments remain so;
%     nested algebraic-level lists of arguments;
% and to discard redundant nested max and min.
% The Lisp functions max and min are not affected.

% Revision : W. Neun, ZIB Berlin, 25/6/94
%  added handling of  max(n,n+1,n-1) => n+1

put('max, 'simpfn, 'simpmax);

symbolic procedure simpmax u;
   S_simpmaxmin('max, function evalgreaterp, u, nil);

put('min, 'simpfn, 'simpmin);

symbolic procedure simpmin u;
   S_simpmaxmin('min, function evallessp, u, nil);

flag('(max min),'listargp);

symbolic smacro procedure maxmin_difflist(u,v);
 for each uu in u collect reval list('difference,uu ,v);


symbolic procedure S_simpmaxmin(maxmin, relation, u,rec);
   begin scalar arglist, arglistp, mval, x;
      if null u then return nil ./ 1;   % 0 returned for empty args.
      arglistp := arglist := list nil;  % Dummy car with cdr to rplacd.
      for each val in flattenmaxmin(maxmin, revlis u) do
         if atom denr(x := simp!* val)
            and (atom numr x or car numr x memq '(!:rd!: !:rn!:))
         % extremize numerical args:
           then (if null mval or apply2(relation,val, mval)
                   then mval := val)
         else
         % successively append symbolic args efficiently:
            << rplacd(arglistp, list val); arglistp := cdr arglistp >>;
      arglist := cdr arglist;  % Discard dummy car
      % Put any numerical extreme value at head of arg list:
      if mval then arglist := mval . arglist;
      % If more than one arg then keep as a max or min:
      if cdr arglist and rec then
                return !*kk2f(maxmin . !*trim arglist) ./ 1;
      if cdr arglist then
        if length cdr arglist >= 1 and
           not eqcar(prepsq(mval :=S_simpmaxmin(maxmin,relation,
                        maxmin_difflist(arglist,car arglist),T)),maxmin)
           then return addsq(mval,simp!* car arglist)
           else return !*kk2f(maxmin . !*trim arglist) ./ 1;
      % Otherwise just return the single (extreme) value:
      return simp car arglist
   end;  % simpmaxmin

symbolic procedure !*trim u;
   % Trim repeated elements from u.
   if null u then nil
    else if car u member cdr u then !*trim cdr u
    else car u . !*trim cdr u;

symbolic procedure flattenmaxmin(maxmin, u);
   % Flatten algebraic-mode lists and already recursively simplified
   % calls of max/min as appropriate.
   for each el in u join
      if atom el then list el
      else if car el eq 'list then flattenmaxmin(maxmin, cdr el)
      else if car el eq maxmin then cdr el
      else if car el='MAT then for each r in cdr el join r
      else list el;

endmodule;

end;
