module sfbdata;  % Generate necessary data for Bernoulli computation.

% Author:  Winfried Neun.

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


fluid '(compute!-bernoulli);

global '(!*force);

flag('(force),'switch);

flag('(on),'eval);

on force;

!#if (greaterp (length (explode (expt 10 100))) 100)

% The conditional compilation here is so that on a system that does
% not support bignums there is no waste of effort tabulating
% meaningless overflowed values. I have also adjusted this code so that
% it displays the values that it computes at build-time so that especially
% if anything goes wrong it will be possible to observe the progress that
% has been made. The vsl bignum arithmetic is REALLY SLOW so in that case
% I only pre-compute the first 100 not the first 300.

!#if (memq 'vsl lispsystem!*)
symbolic macro procedure mk!-bernoulli u;
   <<for i := 1:100 do print list(i, retrieve!*bern i);
     list('quote, bernoulli!-alist) >>;
!#else
symbolic macro procedure mk!-bernoulli u;
   <<for i := 1:300 do print list(i, retrieve!*bern i);
     list('quote, bernoulli!-alist) >>;
!#endif
!#else
symbolic macro procedure mk!-bernoulli u;
   nil;
!#endif

% When I read in save!-bernoulli the macro mk!-bernoulli() will get
% expanded.  This is because of the RLISP flag "*force".  The effect
% will be that the definition of save!-bernoulli() is in effect
% just   bernoulli!-alist := '((....))

symbolic procedure save!-bernoulli();
   bernoulli!-alist := mk!-bernoulli();

% I want to execute save!-bernoulli() just once to initialize the
% table.  That way even if I am running interpreted the painfully
% slow initial calculation of the table gets done only once when
% I first process this chunk of code.

save!-bernoulli()$

compute!-bernoulli := t;

off force;

remflag('(on),'eval);

endmodule;

end;

