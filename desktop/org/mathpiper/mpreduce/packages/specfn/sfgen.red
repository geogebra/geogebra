module sfgen;   % Handy functions used by the special functions package.

% Author: Chris Cannam.

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


exports sq2bf!*,bfprin!:roundup,sf!*eval;

symbolic procedure sf!*assoc(compare,val,alist);
   (if null alist then nil
    else if not lispeval list(compare,car car alist,val)
               then car alist
               else sf!*assoc(compare,val,cdr alist));

symbolic procedure sf!*multi!*assoc!*comparator(compare,vals,entry);
   (if null entry
    then (null vals)
    else (not null vals)
      and (lispeval list(compare,list('quote,car vals),
                                                         list('quote,car entry)))
      and (sf!*multi!*assoc!*comparator(compare,cdr vals,cdr entry)));

symbolic procedure sf!*multi!*assoc(compare,vals,alist);
   (if null alist then nil
    else if sf!*multi!*assoc!*comparator(compare,vals,car car alist)
               then car alist
               else sf!*multi!*assoc(compare,vals,cdr alist));

symbolic procedure sf!*do!*eval(expression);
   begin scalar prepre, result;
      prepre := precision 0;
      precision (prepre + 3);
      result := aeval expression;
      precision prepre;
      return result;
   end;


% It's a finite state automaton!  It's a big nested if..then..else
% construct!  It's repulsive repetitive code!  It's easy to compile
% and run quickly!  It's longer than it needs to be!  It's all of
% this and more...!   (But at least it works.)

symbolic procedure sf!*eval(name,args);
   begin scalar part1, part2, result;
      args := cdr args;
      if (part1 := assoc((name . !*complex),sf!-alist))
      then if (part2 := sf!*assoc('lessp,c!:prec!:(),cdr part1))
           then if (result :=
                       sf!*multi!*assoc('evalequal,args,cdr part2))
                      then result := cdr result
                             else if !*savesfs
                                  then setq(cdr part2,
                          (args . (result := sf!*do!*eval(name . args)))
                                              . cdr part2)
                                  else result := sf!*do!*eval(name . args)
                 else if !*savesfs
                      then setq(cdr part1,
                                   (c!:prec!:()
                                      . list ((args .
                                                (result := sf!*do!*eval(name . args)))))
                                    . cdr part1)
                      else result := sf!*do!*eval(name . args)
      else if !*savesfs
                 then sf!-alist :=
                       ((name . !*complex) .
                               list ((c!:prec!:() .
                                    list ((args .
                                                (result := sf!*do!*eval(name . args))
                                               ))))) . sf!-alist
                 else result := sf!*do!*eval(name . args);
      return result;
   end;

algebraic procedure complex!*off!*switch;
   if symbolic !*complex then
      if symbolic !*msg then
         << off msg; off complex; on msg >>
      else off complex
   else t;

algebraic procedure complex!*restore!*switch(fl);
   if not fl then
      if symbolic !*msg then
         << off msg;
            if symbolic !*complex then
               off complex
            else on complex;
            on msg >>
      else if symbolic !*complex then
            off complex
         else on complex;

endmodule;

end;


