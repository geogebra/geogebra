module reddom;   % Reduction of domain elements.

% Author: Anthony C. Hearn.

% Copyright (c) 1989 The RAND Corporation.  All Rights Reserved.

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


fluid '(mv!-vars!*);

global '(!*xxx !*yyy);

% switch xxx,yyy;

!*xxx := !*yyy := t;


% Operations on domain elements.

symbolic smacro procedure domain!-!+(u,v); u+v;

symbolic smacro procedure domain!-!-(u,v); u-v;

symbolic smacro procedure domain!-!*(u,v); u*v;

symbolic smacro procedure domain!-divide(u,v); divide(u,v);


% Operations on domain element lists.

symbolic procedure mv!-domainlist!-!+(u,v);
   if null u then nil
    else domain!-!+(car u,car v) . mv!-domainlist!-!+(cdr u,cdr v);

symbolic procedure mv!-domainlist!-!-(u,v);
   if null u then nil
    else domain!-!-(car u,car v) . mv!-domainlist!-!-(cdr u,cdr v);

symbolic procedure mv!-domainlist!-!*(u,v);
   if null v then nil
    else domain!-!*(u,car v) . mv!-domainlist!-!*(u,cdr v);

% Procedures for actually reducing domain elements.

symbolic procedure reduce(u,v);
   % Reduce domain element list u with respect to an equal length domain
   % element list v.  We assume that v has been reduced to lowest terms.
   begin scalar weightlist,x;
      % Look for equal ratios of elements.
      x := u;
      IF !*YYY THEN
      x := reduce!-ratios(x,v);
      % Define weighting list.
      weightlist := set!-weights v;
      % Choose column elimination with lowest weight.
      IF !*XXX THEN
      x := reduce!-columns(x,v,weightlist);
      % Look for a reduction in weight of the expression.
      IF !*XXX THEN
      x := reduce!-weights(x,v,weightlist);
      return x
   end;

   symbolic procedure set!-weights v;
      % Define weights to be associated with the reduction test.
      % The current definition is pretty naive.
      begin integer n;
%         return reversip for each j in v collect (n := n+1)
          return reversip (0 . for each j in cdr v collect 1)
      end;

   symbolic procedure reduce!-ratios(u,v);
      begin scalar x;
         if null(x := red!-ratios1(u,v)) then return u;
         x := mv!-domainlist!-!-(mv!-domainlist!-!*(car x,u),
                                      mv!-domainlist!-!*(cdr x,v));
         return if reddom_zeros u >= reddom_zeros x then u
                 else reduce!-ratios(x,v)
      end;

      symbolic procedure reddom_zeros u;
         if null u then 0
          else if car u = 0 then 1+reddom_zeros cdr u
          else reddom_zeros cdr u;

      symbolic procedure red!-ratios1(u,v);
         u and (red!-ratios2(cdr u,cdr v,car u,car v)
                   or red!-ratios1(cdr u,cdr v));

         symbolic procedure red!-ratios2(u,v,u1,v1);
            % The remainder check is needed for the example
            % reduce('(3 0 -3 0 0 0 0),(2 -1 -2 -1 3 -1 1));
            begin integer n;
               return if null u then nil
                       else if (n := u1*car v) = v1*car u and n neq 0
                         and remainder(gcdn(v1,u1),v1)=0
                        then red!-lowest!-terms(v1,u1)
                       else red!-ratios2(cdr u,cdr v,u1,v1)
            end;

            symbolic procedure red!-lowest!-terms(u,v);
               begin scalar x;
                  if u<0 then <<u := -u; v := -v>>;
                  x := gcdn(u,v);
                  % We must have x = u from call in red-ratios2.  If
                  % not, something is awfully wrong.
                  if x neq u then errach list("red-lowest-terms",u,v);
                  return 1 . (v/x)
               end;

symbolic procedure reduce!-columns(u,v,weightlist);
   begin scalar w,x,y,z,z1;
      x := u;
      y := v;
      w := (u . red!-weight(u,weightlist));
   a: if null x then return car w
       else if car x=0 or car y=0 then nil
       else if cdr(z := domain!-divide(car x,car y))=0
        then <<z := mv!-domainlist!-!-(u,mv!-domainlist!-!*(car z,v));
               z1 := red!-weight(z,weightlist);
               if red!-weight!-less!-p(z1,cdr w)
                  and not more!-apartp(z . z1,w)
                 then w := (z . z1)>>;
      x := cdr x;
      y := cdr y;
      go to a
   end;

   symbolic procedure more!-apartp(u,v);
      cadr u=2 and cadr u=cadr v and cadar u=0 and cadar v neq 0;

   symbolic procedure reduce!-weights(u,v,weightlist);
      begin scalar success,x,y,z;
         x := red!-weight(u,weightlist);
      a: y := mv!-domainlist!-!+(u,v);
         z := red!-weight(y,weightlist);
         if red!-weight!-less!-p(z,x)
           then <<success := t; u := y; x := z; go to a>>;
         if success then return u;
      b: y := mv!-domainlist!-!-(u,v);
         z := red!-weight(y,weightlist);
         if red!-weight!-less!-p(z,x) then <<u := y; x := z; go to b>>;
         return u
      end;

      symbolic procedure red!-weight(u,weightlist);
         nonzero!-length u . red!-weight1(u,weightlist);

         symbolic procedure red!-weight1(u,weightlist);
            if null u then 0
             else abs car u*car weightlist
                     + red!-weight1(cdr u,cdr weightlist);

         symbolic procedure nonzero!-length u;
            if null u then 0
             else if car u=0 then nonzero!-length cdr u
             else add1 nonzero!-length cdr u;

      symbolic procedure red!-weight!-less!-p(u,v);
         if car u=car v then cdr u<cdr v else car u<car v;

endmodule;

end;
