%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                      %
% Examples of calculations of matrix normal forms using the REDUCE     %
% NORMFORM package.                                                    %
%                                                                      %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% load_package normform;

on errcont; % So that computation continues after an error.

%
% If using xr, the X interface for REDUCE, then turn on looking_good to 
% improve the appearance of the output.
%
fluid '(options!*);

lisp if memq('fmprint ,options!*) then on looking_good;

procedure test(tmp,A);
  % 
  % Checks that P * N * P^-1 = A where tmp is the output {P,N,P^-1} 
  % of the Normal form calculation on A.
  %
  begin
    if second tmp * first tmp * third tmp = A then
    write "Seems O.K." else rederr "something isn't working.";
  end;


%%%%%%%%%%%%%%%%%%%%%%%%%%%% Smithex %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  A := mat((3*x,x^2+x),(0,x^2));
  answer := smithex(A,x);
  test(answer,A);

  %
  % Extend algebraic field to include sqrt2.
  %
  load_package arnum;
  defpoly sqrt2**2-2;
  A := mat((sqrt2*y^2,y+1),(3*sqrt2,y^3+y*sqrt2));
  answer := smithex(A,y);
  test(answer,A);
  off arnum;

  %
  % smithex will compute the Smith normal form of matrices containing 
  % only integer entries but the integers are regarded as univariate 
  % polynomials in x over a field F (the rationals unless the field has 
  % been extended). For calculations over the integers use smithex_int.
  %
  A := mat((9,-36,30),(-36,192,-180),(30,-180,180));
  answer := smithex(A,x);
  test(answer,A);

%%%%%%%%%%%%%%%%%%%%%%%%%%%% Smithex_int %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  A := mat((1,2,3),(4,5,6),(7,8,x));
  answer := smithex_int(A);

  A := mat((9,-36,30),(-36,192,-180),(30,-180,180));
  answer := smithex_int(A);
  test(answer,A);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Frobenius %%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  A := mat(((y+y^2-x^2)/y,(x-x^2-y+y^2)/y,(x^2-y^2)/y),(1+x+y,
            (x-y+y^2+x*y)/y,-x-y),((y-x+y^2-x^2)/y,(x-y+y^2-x^2)/y,
            (x+x^2-y^2)/y));
  answer := frobenius(A);
  test(answer,A);

  %
  % Extend algebraic field to include i.
  %
%   load_package arnum;
    defpoly i^2+1;
    A := mat((-3-i,1,2+i,7-9*i),(-2,1,1,5-i),(-2-2*i,1,2+2*i,4-2*i),
             (2,0,-1,-2+8*i));
    answer := frobenius(A);
    off arnum;

  A := mat((10,-5,-5,8,3,0),(-4,2,-10,-7,-5,-5),(-8,2,7,3,7,5),
           (-6,-7,-7,-7,10,7),(-4,-3,-3,-6,8,-9),(-2,5,-5,9,7,-4));
  F := first frobenius(A);

  %
  % Calculate in Z\23Z...
  %
    on modular;
    setmod 23;
    F_mod := first frobenius(A);
 
  %
  % ...and with a balanced modular representation.
  %
    on balanced_mod;
    F_bal_mod := first frobenius(A);
    off balanced_mod;
    off modular;


%%%%%%%%%%%%%%%%%%%%%%%%%%% Ratjordan %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  A := mat(((y+y^2-x^2)/y,(x-x^2-y+y^2)/y,(x^2-y^2)/y),(1+x+y,
            (x-y+y^2+x*y)/y,-x-y),((y-x+y^2-x^2)/y,(x-y+y^2-x^2)/y,
            (x+x^2-y^2)/y));
  answer := ratjordan(A);
  test(answer,A);

  %
  % Extend algebraic field to include sqrt(2).
  %
  %  load_package arnum;
    defpoly sqrt2**2-2;
    A:= mat((4*sqrt2-6,-4*sqrt2+7,-3*sqrt2+6),(3*sqrt2-6,-3*sqrt2+7,
            -3*sqrt2+6),(3*sqrt2,1-3sqrt2,-2*sqrt2));
    answer := ratjordan(A);
    test(answer,A);
    off arnum;

  A := mat((-12752,-6285,-9457,-7065,-4939,-5865,-3769),(13028,6430,
           9656, 7213,5041,5984,3841),(16425,8080,12192,9108,6370,7569,
           4871), (-6065,-2979,-4508,-3364,-2354,-2801,-1803),(2968,
           1424,2231, 1664,1171,1404,919),(-22762,-11189,-16902,-12627,
           -8833, -10498,-6760),(23112,11400,17135,12799,8946,10622,
           6821));
  R := first ratjordan(A);

  %
  % Calculate in Z/23Z...
  %
    on modular;
    setmod 23;
    R_mod := first ratjordan(A);

  %
  % ...and with a balanced modular representation.
  %
    on balanced_mod;
    R_bal_mod := first ratjordan(A);
    off balanced_mod;
    off modular;


%%%%%%%%%%%%%%%%%%%%%%%%%%% jordansymbolic %%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  A := mat(((y+y^2-x^2)/y,(x-x^2-y+y^2)/y,(x^2-y^2)/y),(1+x+y,
            (x-y+y^2+x*y)/y,-x-y),((y-x+y^2-x^2)/y,(x-y+y^2-x^2)/y,
            (x+x^2-y^2)/y));
  answer := jordansymbolic(A);


  %
  % Extend algebraic field.
  %
  % load_package arnum;
    defpoly b^3-2*b+b-5;
    A := mat((1-b,2+b^2),(3+b-2*b^2,3));
    answer := jordansymbolic(A);
    off arnum;

  A := mat((-9,21,-15,4,2,0),(-10,21,-14,4,2,0),(-8,16,-11,4,2,0),
           (-6,12,-9,3,3,0),(-4,8,-6,0,5,0),(-2,4,-3,0,1,3));
  answer := jordansymbolic(A);
  
  % Check to see if looking_good (*) is on as the choice of using 
  % either lambda or xi is dependent upon this.
  % (* -> the use of looking_good is described in the manual.).
  if not lisp !*looking_good then
  <<
    %
    % NB: we use lambda_ in solve (instead of lambda) as lambda is used
    % for other purposes in REDUCE which mean it cannot be used with
    % solve.
    %
    solve(lambda_^2-4*lambda_+5,lambda_);
    J := sub({lambda31=i + 2,lambda32= - i + 2},first answer);
    P := sub({lambda31=i + 2,lambda32= - i + 2},third answer);
    Pinv :=sub({lambda31=i + 2,lambda32= - i + 2},third rest answer);
  >>
  else 
  <<
    solve(xi^2-4*xi+5,xi);
    J := sub({xi(3,1)=i + 2,xi(3,2)= - i + 2},first answer);
    P := sub({xi(3,1)=i + 2,xi(3,2)= - i + 2},third answer);
    Pinv := sub({xi(3,1)=i + 2,xi(3,2)= - i + 2},third rest answer);
  >>;
  test({J,P,Pinv},A);

  %
  % Calculate in Z/23Z...
  %
    on modular;
    setmod 23;
    answer := jordansymbolic(A)$
    J_mod := {first answer, second answer};

  %
  % ...and with a balanced modular representation.
  %
    on balanced_mod;
    answer := jordansymbolic(A)$
    J_bal_mod := {first answer, second answer};
    off balanced_mod;
    off modular;
 

%%%%%%%%%%%%%%%%%%%%%%%%%%%% jordan %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  A := mat((1,y),(y^2,3));
  answer := jordan(A);
  test(answer,A);

  A := mat((-12752,-6285,-9457,-7065,-4939,-5865,-3769),(13028,6430,
          9656, 7213,5041,5984,3841),(16425,8080,12192,9108,6370,7569,
          4871), (-6065,-2979,-4508,-3364,-2354,-2801,-1803),(2968,
          1424,2231, 1664,1171,1404,919),(-22762,-11189,-16902,-12627,
          -8833, -10498,-6760),(23112,11400,17135,12799,8946,10622,
          6821));
  on rounded;
  J := first jordan(A);
  off rounded;

  %
  % Extend algebraic field.
  %
  % load_package arnum;
    defpoly b^3-2*b+b-5;
    A := mat((1-b,2+b^2),(3+b-2*b^2,3));
    J := first jordan(A);
    off arnum;

end;

