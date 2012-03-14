module random;  % Random Number Generator.

% Author: C.J. Neerdaels, with adjustments by A.C. Norman.

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


% Entrypoints:
%     random_new_seed n Re-seed the random number generator
%     random n          return next value (range 0 <= r < n)
%     next!-random!-number()
%                       return next value in range 0<=r<randommodulus!*

% Note that random_new_seed is automatically called with argument 1 if
% the user does not explicitly call it with some other argument, and
% that resetting the seed in the generator is a fairly expensive
% business. % The argument to random() may be integer or floating, large
% or small, but should be strictly positive.


global '(unidev_vec!* randommodulus!*);
global '(unidev_fac!* unidev_next!* unidev_nextp!* unidev_mj!*);
global '(randomseed!*);

unidev_vec!* := mkvect(54)$
randommodulus!* := 100000000; % This is a fixnum in PSL and CSL (10^8).
unidev_fac!* :=  1.0/randommodulus!*;

% The following two lines are for speed fanatics - they should be OK
% with both PSL and CSL (as of June 1993).  They can be removed with no
% serious effect to code that is not random-number intensive.

% compiletime on fastfor;
% compiletime flag('(randommodulus!* unidev_fac!*), 'constant!?);

flag('(random random_new_seed),'opfn); % Make symbolic operators.

symbolic procedure random_new_seed offset;
% Sets the unidev seed to offset
  begin scalar mj, mk, ml, ii;
    if not fixp offset or offset <= 0
      then typerr(offset,"positive integer");
    mj := remainder(offset, randommodulus!*);
    putv(unidev_vec!*, 54, mj);
    mk := mj + 1;          % This arranges that one entry in the vector
                           % will end up with '1' in it, and that is
                           % enough to ensure we get a long cycle.
    for i:= 1:54 do <<
      ml := mk #- mj;
      if iminusp ml then ml := ml #+ randommodulus!*;
      ii := remainder(21*i,55);
      putv(unidev_vec!*, ii #- 1, ml);
      mk := mj;
      mj := ml >>;
    for k:=1:4 do <<      % Cycle generator a few times to pre-scramble.
      for i:=0:54 do <<
        ml := getv(unidev_vec!*, i) #-
              getv(unidev_vec!*, remainder(i #+ 31,55));
        if iminusp ml then ml := ml #+ randommodulus!*;
        putv(unidev_vec!*, i, ml) >> >>;
    unidev_next!* := 0;
    unidev_nextp!* := 31;
    return nil
  end;

%*************************UNIDEV****************************************

symbolic procedure next!-random!-number;
% Returns a uniform random deviate between 0 and randommodulus!*-1.
  begin scalar mj;
    if unidev_next!* = 54 then unidev!_next!* := 0
     else unidev!_next!* := unidev!_next!* #+ 1;
    if unidev!_nextp!* = 54 then unidev!_nextp!* := 0
     else unidev!_nextp!* := unidev!_nextp!* #+ 1;
    mj := getv(unidev_vec!*, unidev_next!*) #-
          getv(unidev_vec!*, unidev_nextp!*);
    if iminusp mj then mj := mj #+ randommodulus!*;
    putv(unidev_vec!*, unidev_next!*, mj);
    return mj
  end;

symbolic procedure random size;
% Returns a random value in the range 0 <= r < size.
  begin scalar m, r;
    if not numberp size or size <= 0
      then typerr(size,"positive number");
    if floatp size then <<
% next!-random!-number() returns just under 27 bits of randomness, and
% for a properly random double precision (IEEE) value I need 52 or 53
% bits.  So I just call next!-random!-number() twice and glue the bits
% together.
      r := float next!-random!-number() * unidev_fac!*;
      return (float next!-random!-number() + r) * unidev_fac!* * size >>
    else <<
% I first generate a random variate over a range that is some power of
% randommodulus!*.  Then I select from this to restrict my range to be
% an exact multiple of size.  The worst case for this selection is when
% the power of randommodulus!* is just less than twice size, in which
% case on average two trials are needed.  In the vast majority of cases
% the cost of making the selection will be much less.  With a value
% uniform over some multiple of my range I can use simple remaindering
% to get the result.
      repeat <<
        r := next!-random!-number();
        m := randommodulus!*;
        while m < size do <<
          m := m * randommodulus!*;
          r := randommodulus!* * r + next!-random!-number() >>;
        >> until r < m - remainder(m, size);
      return remainder(r, size) >>
  end;

random_new_seed 1;    % Ensure that code is set up ready for use.

endmodule;

end;


