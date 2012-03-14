module genmod; % Modular arithmetic where the modulus may be any size.

% Authors: A. C. Norman and P. M. A. Moore, 1981.

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


% Modifications by: John Abbott.

% Note: when balanced_mod is on, the results here are not always in
% range. *modular2f is used to correct this. However, these routines
% should be updated to give balanced results.

fluid '(!*balanced_mod current!-modulus modulus!/2);

global '(largest!-small!-modulus);

symbolic procedure set!-general!-modulus p;
  if not numberp p or p=0 then current!-modulus
   else begin
     scalar previous!-modulus;
     previous!-modulus:=current!-modulus;
     current!-modulus:=p;
     modulus!/2 := p/2;
     % Allow for use of small moduli where appropriate.
     if p <= largest!-small!-modulus then set!-small!-modulus p;
     return previous!-modulus
  end;

symbolic procedure general!-modular!-plus(a,b);
  begin scalar result;
     result:=a+b;
     if result >= current!-modulus then result:=result-current!-modulus;
     return result
  end;

symbolic procedure general!-modular!-difference(a,b);
  begin scalar result;
     result := a - b;
     if result < 0 then result:=result+current!-modulus;
     return result
  end;

symbolic procedure general!-modular!-number a;
  begin
     a:=remainder(a,current!-modulus);
     if a < 0 then a:=a+current!-modulus;
     return a
  end;

symbolic procedure general!-modular!-times(a,b);
  begin scalar result;
     result:=remainder(a*b,current!-modulus);
     if result<0
       then result := result+current!-modulus;  %can this happen?
     return result
  end;

symbolic procedure general!-modular!-reciprocal a;
   % Note this returns a positive result.
   if !*balanced_mod and a<0
     then general!-reciprocal!-by!-gcd(current!-modulus,
                                       a + current!-modulus,0,1)
    else general!-reciprocal!-by!-gcd(current!-modulus,a,0,1);

symbolic procedure general!-modular!-quotient(a,b);
    general!-modular!-times(a,general!-modular!-reciprocal b);

symbolic procedure general!-modular!-minus a;
    if a=0 then a else current!-modulus - a;

symbolic procedure general!-reciprocal!-by!-gcd(a,b,x,y);
%On input A and B should be coprime. This routine then
%finds X and Y such that A*X+B*Y=1, and returns the value Y
%on input A > B;
   if b=0 then rerror(alg,8,"Invalid modular division")
   else if b=1 then if y < 0 then y+current!-modulus else y
   else begin scalar w;
%N.B. Invalid modular division is either:
% a)  attempt to divide by zero directly
% b)  modulus is not prime, and input is not
%     coprime with it;
     w:=quotient(a,b); %Truncated integer division;
     return general!-reciprocal!-by!-gcd(b,a-b*w,y,x-y*w)
   end;

% The next two functions compute the "reverse" of a binary number.
% This is the number obtained when writing down the binary expansion
% in reverse order.  If 2^r divides n (but 2^(r+1) does not) then
% reverse-num(reverse-num(n)) = abs(n)/2^r. r can be computed using
% height2.

symbolic procedure reverse!-num(n);
   if n = 0 then n
    else if n<0 then -reverse!-num1(-n,ilog2(-n)+1)
    else reverse!-num1(n,ilog2(n)+1);

global '(reverse!-num!-table!*);

reverse!-num!-table!* := mkvect 16;
putv(reverse!-num!-table!*,1,8);
putv(reverse!-num!-table!*,2,4);
putv(reverse!-num!-table!*,3,12);
putv(reverse!-num!-table!*,4,2);
putv(reverse!-num!-table!*,5,10);
putv(reverse!-num!-table!*,6,6);
putv(reverse!-num!-table!*,7,14);
putv(reverse!-num!-table!*,8,1);
putv(reverse!-num!-table!*,9,9);
putv(reverse!-num!-table!*,10,5);
putv(reverse!-num!-table!*,11,13);
putv(reverse!-num!-table!*,12,3);
putv(reverse!-num!-table!*,13,11);
putv(reverse!-num!-table!*,14,7);
putv(reverse!-num!-table!*,15,15);

symbolic procedure reverse!-num1(n,bits);
   if n = 0 then 0
    else if bits = 1 then n
    else if bits = 2 then getv(reverse!-num!-table!*,4*n)
    else if bits = 3 then getv(reverse!-num!-table!*,2*n)
    else if bits = 4 then getv(reverse!-num!-table!*,n)
    else begin scalar shift,qr;
       shift := 2**(bits/2);
       qr := divide(n,shift);
       if not evenp bits then shift := shift*2;
       return reverse!-num1(cdr qr,bits/2)*shift +
           reverse!-num1(car qr,(bits+1)/2)
   end;

% Interface to algebraic mode.

flag('(reverse!-num),'integer);

deflist('((reverse!-num rnreverse!-num!*)),'!:rn!:);

%put('fibonacci,'!:rn!:,'rnfibonacci!*);

put('reverse!-num,'number!-of!-args,1);

put('reverse!-num,'simpfn,'simpiden);

symbolic procedure rnreverse!-num!*(x);
   (if fixp y then reverse!-num(y)
    else !*p2f mksp(list('reverse!-num,y),1))
    where y=rnfixchk x;

% Interface to algebraic mode.

put('reverse!-num, 'simpfn, 'simpreverse!-num);

symbolic procedure simpreverse!-num(u);
   begin scalar arg;
      if length(u) neq 1 then typerr(u,"integer");
      arg := simpcar u;
      if denr(arg) neq 1 or not fixp(numr(arg))
        then rederr("reverse!-num: argument should be an integer");
      return reverse!-num(numr(arg)) ./ 1
   end;

% This is an iterative version of general!-modular!-expt.
% Its principal advantage over the (simpler) recursive implementation
% is that it avoids excessive memory consumption when both n and the
% modulus are quite large -- try primep(2^10007-1) if you don't believe
% it!

symbolic procedure general!-modular!-expt(a,n);
   % Computes a**n modulo current-modulus.  Uses Fermat's Little
   % Theorem where appropriate for a prime modulus.
    if a=0 then if n=0 then rerror(alg,101,"0^0 formed") else 0
    else if n=0 then 1
    else if n=1 then a
    else if n>=current!-modulus-1 and primep current!-modulus
     then general!-modular!-expt(a,remainder(n,current!-modulus-1))
    else begin scalar x, revn;
      while evenp n do <<n := n/2; a := general!-modular!-times(a,a)>>;
      revn := reverse!-num n;
      x := 1;
      while revn>0 do
      <<x := general!-modular!-times(x,x);
        if not evenp(revn) then x := general!-modular!-times(x,a);
        revn := revn/2>>;
      return x
    end;

endmodule;

end;
