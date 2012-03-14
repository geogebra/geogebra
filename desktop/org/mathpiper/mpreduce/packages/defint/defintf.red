module defintf;

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


algebraic <<

operator case20,case21,case22,case23,case24,case25,
         case26,case27,case28,case29,case30,case31,case32,case33,
         case34,case35;

case20_rules :=

{  case20(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when n = 0
        and m > 0
        and epsilon > 0
        and phi < 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_12 = 't
              and transform_test('test2,'test12,nil,nil,nil,nil,nil,nil) = 't
};

let case20_rules;

case21_rules :=

{  case21(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when m = 0
        and n > 0
        and epsilon > 0
        and phi > 0
        and test_1a = 't and test_1b = 't and test_3 = 't
              and test_12 = 't
        and transform_test('test12,nil,nil,nil,nil,nil,nil,nil) = 't
};

let case21_rules;

case22_rules :=

{  case22(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when k*l = 0
        and delta > 0
        and epsilon > 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_10 = 't and test_12 = 't
        and transform_test('test2,'test3,'test10,'test12,nil,nil,nil,
        nil)= 't
};

let case22_rules;

case23_rules :=

{  case23(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when m*n = 0
        and delta > 0
        and epsilon > 0
        and test_1a = 't and test_1b = 't and test_2 = 't
              and test_3 = 't and test_10 = 't and test_12 = 't
        and transform_test('test2,'test3,'test10,'test12,nil,nil,nil,
        nil) = 't
};

let case23_rules;

case24_rules :=

{  case24(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when m + n > p
        and l = 0
        and phi = 0
        and k > 0
        and delta > 0
        and epsilon < 0
        and mylessp(abs(atan(impart omega/repart omega)),m + n - p + 1)
        and test_1a = 't and test_1b = 't and test_2 = 't
        and test_10 = 't and test_14 = 't and test_15 ='t
        and transform_test('test2,'test10,'test14,'test15,nil,nil,nil,
        nil) = 't
};

let case24_rules;

case25_rules :=

{  case25(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when m + n > q
        and k = 0
        and phi = 0
        and l > 0
        and delta > 0
        and epsilon < 0
        and mylessp(abs(atan(impart omega/repart omega)),m + n - q + 1)
        and test_1a = 't and test_1b = 't and test_3 = 't
        and test_10 = 't and test_14 = 't and test_15 ='t
        and transform_test('test3,'test10,'test14,'test15,nil,nil,nil,
        nil) = 't
};

let case25_rules;

case26_rules :=

{  case26(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p = q - 1
        and l = 0
        and phi = 0
        and k > 0
        and delta > 0
        and epsilon >= 0
        and test_arg(abs(atan(impart omega/repart omega)),
                                                epsilon,epsilon + 1)
        and test_1a = 't and test_1b = 't and test_2 = 't
        and test_10 = 't and test_14 = 't and test_15 = 't
        and transform_test('test2,'test10,'test14,'test15,nil,nil,nil,
        nil) = 't
};

let case26_rules;

case27_rules :=

{  case27(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p = q + 1
        and k = 0
        and phi = 0
        and l > 0
        and delta > 0
        and epsilon >= 0
        and test_arg(abs(atan(impart omega/repart omega)),
                                                epsilon,epsilon + 1)
        and test_1a = 't and test_1b = 't and test_3 = 't
        and test_10 = 't and test_14 = 't and test_15 = 't
        and transform_test('test3,'test10,'test14,'test15,nil,nil,nil,
        nil) = 't
};

let case27_rules;

case28_rules :=

{  case28(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p < q - 1
        and l = 0
        and phi = 0
        and k > 0
        and delta > 0
        and epsilon >= 0
        and test_arg(abs(atan(impart omega/repart omega)),
                                                epsilon,m + n - p + 1)
        and test_1a = 't and test_1b = 't and test_2 = 't
        and test_10 = 't and test_14 = 't and test_15 = 't
        and transform_test('test2,'test10,'test14,'test15,nil,nil,nil,
        nil) = 't

};

let case28_rules;

case29_rules :=

{  case29(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when p > q + 1
        and k = 0
        and phi = 0
        and l > 0
        and delta > 0
        and epsilon >= 0
        and test_arg(abs(atan(impart omega/repart omega)),
                                                epsilon,m + n - q + 1)
        and test_1a = 't and test_1b = 't and test_3 = 't
        and test_10 = 't and test_14 = 't and test_15 = 't
        and transform_test('test3,'test10,'test14,'test15,nil,nil,nil,
        nil) = 't
};

let case29_rules;

case30_rules :=

{  case30(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when n = 0
        and phi = 0
        and k + l > u
        and m > 0
        and epsilon > 0
        and delta < 0
        and mylessp(abs(atan(impart sigma/repart sigma)),k + l - u + 1)
        and test_1a = 't and test_1b = 't and test_2 = 't
        and test_12 = 't and test_14 = 't and test_15 = 't
        and transform_test('test2,'test12,'test14,'test15,nil,nil,nil,
        nil) = 't
};

let case30_rules;

case31_rules :=

{  case31(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when m = 0
        and phi = 0
        and k + l > v
        and n > 0
        and epsilon > 0
        and delta < 0
        and mylessp(abs(atan(impart sigma/repart sigma)),k + l - v + 1)
        and test_1a = 't and test_1b = 't and test_3 = 't
        and test_12 = 't and test_14 = 't and test_15 = 't
        and transform_test('test3,'test12,'test14,'test15,nil,nil,nil,
        nil) = 't
};

let case31_rules;

case32_rules :=

{  case32(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when n = 0
        and phi = 0
        and u = v - 1
        and m > 0
        and epsilon > 0
        and delta >= 0
        and test_arg(abs(atan(impart sigma/repart sigma)),
                                                delta,delta + 1)
        and test_1a = 't and test_1b = 't and test_2 = 't
        and test_12 = 't and test_14 = 't and test_15 = 't
        and transform_test('test2,'test12,'test14,'test15,nil,nil,nil,
        nil) = 't
};

let case32_rules;

case33_rules :=

{  case33(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when m = 0
        and phi = 0
        and u = v + 1
        and n > 0
        and epsilon > 0
        and delta >= 0
        and test_arg(abs(atan(impart sigma/repart sigma)),
                                                delta,delta + 1)
        and test_1a = 't and test_1b = 't and test_3 = 't
        and test_12 = 't and test_14 = 't and test_15 = 't
        and transform_test('test3,'test12,'test14,'test15,nil,nil,nil,
        nil) = 't
};

let case33_rules;

case34_rules :=

{  case34(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => 't

     when n = 0
        and phi = 0
        and u < v - 1
        and m > 0
        and epsilon > 0
        and delta >= 0
        and test_arg(abs(atan(impart sigma/repart sigma)),
                                                delta,k + l - u + 1)
        and test_1a = 't and test_1b = 't and test_2 = 't
        and test_12 = 't and test_14 = 't and test_15 = 't
        and transform_test('test2,'test12,'test14,'test15,nil,nil,nil,
        nil) = 't
};

let case34_rules;

case35_rules :=

{  case35(~m,~n,~p,~q,~k,~l,~u,~v,~delta,~epsilon,~sigma,~omega,~rho,
        ~eta,~mu,~r1,~r2,~phi,~test_1a,~test_1b,~test_2,~test_3,
        ~test_4,~test_5,~test_6,~test_7,~test_8,~test_9,~test_10,
        ~test_11,~test_12,~test_13,~test_14,~test_15) => t

     when m = 0
        and phi = 0
        and u > v + 1
        and n > 0
        and epsilon > 0
        and delta >= 0
        and test_arg(abs(atan(impart sigma/repart sigma)),
                                                delta,k + l - v + 1)
        and test_1a = t and test_1b = t and test_3 = t
        and test_12 = t and test_14 = t and test_15 = t
        and transform_test('test3,'test12,'test14,'test15,nil,nil,nil,
        nil) = t
};

let case35_rules;


flag('(test_arg),'boolean);

algebraic procedure test_arg(a,b,c);

begin scalar !*rounded,dmode!*;
if transform_tst neq t then
<< on rounded;
   if b*pi < a and a < c*pi then << off rounded; return t>>
   else << off rounded; return nil>>;
>>
else return t;
end;

>>;

symbolic procedure transform_test(n1,n2,n3,n4,n5,n6,n7,n8);

begin scalar lst,temp,cond_test;

if transform_tst neq t then return t
else
<< lst := {n1,n2,n3,n4,n5,n6,n7,n8};
   for each i in lst do
    if i then  temp := lispeval cdr assoc(i,transform_lst) . temp; ;

   temp := 'and . temp;
   for each j in spec_cond do if j = temp then cond_test := t;
   if cond_test neq t then spec_cond := temp . spec_cond;
   return nil;
>>;
end;

symbolic operator transform_test;

flag('(sigma_tst),'boolean);

algebraic procedure sigma_tst(sigma);

begin scalar test;
if transform_tst neq t then
        << if sigma > 0 then return t else return nil>>
else
<< if test neq t then
   << symbolic(transform_lst := cons (('sigma_cond .'(list 'greaterp
                'sigma 0)),transform_lst));
      test := t>>;
   return reval t>>;
end;

flag('(omega_tst),'boolean);

symbolic procedure omega_tst(omega);

begin scalar test;

if transform_tst neq t then
<< if omega > 0 then return t else return nil>>
else
<< if test neq t then
   << symbolic(transform_lst := cons (('omega_cond .'(list 'greaterp
                'omega 0)),transform_lst));
      test := t>>;
   return reval t>>;
end;

endmodule;

end;



