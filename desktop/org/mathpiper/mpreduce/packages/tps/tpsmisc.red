module tpsmisc;
% Miscellaneous Support Functions added August 1993
% Author: Alan Barnes <barnesa@cs.aston.ac.uk>.

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


fluid '(gensym!-list);

% build a new copy of a power-series structure with no shared elements

symbolic procedure ps!:copy(ps);
begin scalar gensym!-list, new;
  new := ps!:copy1 ps;
  if gensym!-list then fix!-up!-links(ps,new);
  return new;
end;

symbolic procedure ps!:copy1 ps;
  if ps!:atom ps or car ps neq '!:ps!: then ps
  else begin scalar new, old, newexp;
     old := cdr ps;
     if idp old then <<
       new :=  gensym();
       gensym!-list := (eval old . new) . gensym!-list;
       return car ps . new >>;
     new := mkvect 7;
     for i := 0:7 do putv(new, i, getv(old, i));
     old := ps!:expression ps;
     new := car ps . new;
     if listp old then <<
       newexp := rator old . foreach arg in rands old collect
                                                       ps!:copy1 arg;
       ps!:set!-expression(new, newexp);
       if rator old = 'psgen then <<
          % terms are shared power series so need to be copied
          newexp := ps!:replace(rand1 old, rand1 newexp, ps!:terms ps);
          ps!:set!-terms(new, newexp)
       >>
     >>;
     return new;
   end;

symbolic procedure ps!:replace(p,q,terms);
   foreach term in terms collect (car term . ps!:copy2(cdr term,p,q));

symbolic procedure ps!:copy2(ps, p, q);
% copy series ps. If structure p is shared in ps,
% then q is shared in the copy
% this rigmarole avoids recomputation of terms of base series in PSGEN

  if ps!:atom ps or car ps neq '!:ps!: then ps
  else if ps = p then q
  else
   begin scalar new, old, newexp;
     old := cdr ps;     % this is always a vector (I hope!!)
     new := mkvect 7;
     for i := 0:7 do putv(new, i, getv(old, i));
     old := ps!:expression ps;
     new := car ps . new;
     if listp old then <<
       newexp := rator old . foreach arg in rands old collect
                                    ps!:copy2(arg,p,q);
       ps!:set!-expression(new, newexp);
     >>;
     return new;
   end;


symbolic procedure fix!-up!-links(p,q);
    if ps!:atom p or car p neq '!:ps!: then nil
    else
      begin scalar x, args1, args2;
        if (x :=assoc(cdr p,gensym!-list)) then set(cdr x, cdr q);
        if not idp cdr p then <<
          x := ps!:expression p;
          if listp x then <<
             args1 := cdr x;
             args2 := cdr ps!:expression q;
             while args1 do <<
               fix!-up!-links(car args1, car args2);
               args1 :=cdr args1; args2 := cdr args2
             >>
          >>
        >>;
      end;

put('pscopy,'simpfn,'simppscopy);

symbolic procedure simppscopy u;
  << u:=prepsqxx simp!* carx(u,'pscopy);
     if ps!:p u then simp!* ps!:copy u
     else typerr(u,"power series:  simppscopy")
  >>;

endmodule;

end;
