%  Description: This file contains the tables guiding the program
%
%  Version 26 March 2000
%
%  Author: Luis Alvarez Sobreviela
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

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



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Declaration of a series of table lists which contain the function to be executed  %
% when a certain token is encountered.                                                    %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

constants!*:=
'(!&true!; !&false!; !&imaginaryi!; !&ii!; !&exponentiale!; !&ee!; !&pi!; !&infin!; !&gamma!; !&differentiald!;
!&dd!;);

% The relations!* list contains the correspondance between
% tokens to be found between <reln></reln> tags and the
% reduce function to be executed as well as the argument
% the reduce function should take.
% The <reln> tag is deprecated in MathML2.0 but we have
% kept it for backwards compatibilty.

relations!*:=
'((tendsto . (binaryRD tendsto))
(tendsto!/ . (binaryRD tendsto))
(eq!/ . (naryRD eq))
(neq!/ . (binaryRD neq))
(lt!/ . (naryRD lt))
(gt!/ . (naryRD gt))
(geq!/ . (naryRD geq))
(leq!/ . (naryRD leq))
(in!/ . (binaryRD in))
(notin!/ . (binaryRD notin))
(subset!/ . (subsetrd subset))
(prsubset!/ . (prsubsetrd prsubset))
(notprsubset!/ . (binaryRD notprsubset))
(notsubset!/ . (binaryRD notsubset)));


% The functions!* list contains the correspondance between
% tokens to be found between <apply></apply> tags and the
% reduce function to be executed as well as the argument
% the reduce function should take.

functions!*:=
'((divide!/ . (binaryRD divide))
(scalarproduct!/ . (binaryRD scalarproduct))
(vectorproduct!/ . (binaryRD vectorproduct))
(outerproduct!/ . (binaryRD outerproduct))
(divergence!/ . (unaryRD divergence))
(curl!/ . (unaryRD curl))
(laplacian!/ . (unaryRD laplacian))
(grad!/ . (unaryRD grad))
(size!/ . (unaryRD size))
(setdiff!/ . (setFuncsBinRD setdiff))
(select!/ . (selectRD selector))
(selector!/ . (selectRD selector))
(transpose!/ . (transposeRD transpose))
(determinant!/ . (determinantRD determinant))
(fn . (applyfnRD fn))
(union!/ . (setFuncsnaryRD union))
(intersect!/ . (setFuncsnaryRD intersect))
(implies!/ . (binaryRD implies))
(not!/ . (unaryRD not))
(xor!/ . (naryRD xor))
(or!/ . (naryRD or))
(and!/ . (naryRD and))
(mean!/ . (naryRD mean))
(mode!/ . (naryRD mode))
(var!/ . (naryRD variance))
(variance!/ . (naryRD var))
(sdev!/ . (naryRD sdev))
(moment!/ . (momentRD moment))
(median!/ . (naryRD median))
(sin!/ . (unaryRD sin))
(sec!/ . (unaryRD sec))
(sinh!/ . (unaryRD sinh))
(sech!/ . (unaryRD sech))
(arcsin!/ . (unaryRD arcsin))
(cos!/ . (unaryRD cos))
(csc!/ . (unaryRD csc))
(cosh!/ . (unaryRD cosh))
(csch!/ . (unaryRD csch))
(arccos!/ . (unaryRD arccos))
(tan!/ . (unaryRD tan))
(cot!/ . (unaryRD cot))
(tanh!/ . (unaryRD tanh))
(coth!/ . (unaryRD coth))
(arctan!/ . (unaryRD arctan))
(abs!/ . (unaryRD abs))
(ln!/ . (unaryRD ln))
(plus!/ . (naryRD plus))
(times!/ . (naryRD times))
(power!/ . (binaryRD power))
(exp!/ . (unaryRD exp))
(factorial!/ . (unaryRD factorial))
(quotient!/ . (binaryRD quotient))
(max!/ . (minmaxRD max))
(min!/ . (minmaxRD min))
(minus!/ . (minusRD minus))
(rem!/ . (binaryRD rem))
(conjugate!/ . (unaryRD conjugate))
(root!/ . (rootRD root))
(gcd!/ . (naryRD gcd))
(log!/ . (logRD log))
(int!/ . (symbolsRD int))
(sum!/ . (symbolsRD sum))
(limit!/ . (limitRD limit))
(condition . (conditionRD condition))
(product!/ . (symbolsRD product))
(diff!/ . (diffRD diff))
(partialdiff!/ . (partialdiffRD partialdiff))
(inverse!/ . (unaryRD inverse))
(tendsto . (binaryRD tendsto))
(tendsto!/ . (binaryRD tendsto))
(eq!/ . (naryRD eq))
(neq!/ . (binaryRD neq))
(lt!/ . (naryRD lt))
(gt!/ . (naryRD gt))
(geq!/ . (naryRD geq))
(leq!/ . (naryRD leq))
(in!/ . (setFuncsBinRD in))
(notin!/ . (setFuncsBinRD notin))
(subset!/ . (subsetrd subset))
(prsubset!/ . (prsubsetrd prsubset))
(notprsubset!/ . (setFuncaBinRD notprsubset))
(notsubset!/ . (setFuncsBinRD notsubset))
(forall!/ . (quantifierRD forall))
(exists!/ . (quantifierRD exists))
(equivalent!/ . (binaryRD equivalent))
(approx!/ . (binaryRD approx))
(imaginary!/ . (unaryRD imaginary))
(real!/ . (unaryRD real))
(arg!/ . (unaryRD arg))
(compose!/ . (naryRD compose))
(csymbol . (csymbolrd csymbol)));

% The constructors!* list sets a correspondance between MathML
% constructor tags, the reduce function to be executed and the
% closing tag which must be looked for in order to make sure
% syntax is correct

constructors!* :=
'((reln . (relnRD !/reln "</reln>"))
(set . ( setRD !/set "</set>"))
(fn . ( fnRD !/fn "</fn>"))
(declare . ( declareRD !/declare "</declare>"))
(interval . ( intervalRD !/interval "</interval>"))
(list . ( listRD !/list "</list>"))
(matrix . ( matrixRD !/matrix "</matrix>"))
(apply . ( applyRD !/apply "</apply>"))
(cn . ( cnRD !/cn "</cn>"))
(ci . ( ciRD !/ci "</ci>"))
(lambda . ( lambdaRD !/lambda "</lambda>")));

% The mml!* list determines the correspondance between elements
% in the intermediate representation and the reduce functions to be
% executed.

% The ir2mml!* table determines what function to execute for each
% element of the intermediate representation.
% Its syntax is the following:
%
% (ir_element . (reduce_function function_argument))
%
% The function argument is the equvalent MathML tag usually

ir2mml!* :=
'((determinant . (nary determinant naryOM))
(semantic . (semanticML nil semanticOM))
(string . (nil nil strOM))
(based_integer . (numML based_integer naryOM))
(complex_cartesian . (numML complex_cartesian naryOM))
(complex_polar . (numML complex_polar naryOM))
(ci . (ciML nil ciOM))
(cn . (cnML nil cnOM))
(vectorml . (vectorML nil containerOM))
(scalarproduct . (nary scalarproduct naryOM))
(vectorproduct . (nary vectorproduct naryOM))
(outerproduct . (nary outerproduct naryOM))
(lambda . (containerML lambda lambdaOM))
(declare . (declareML nil))
(divergence . (nary divergence naryOM))
(laplacian . (nary laplacian naryOM))
(curl . (nary curl naryOM))
(grad . (nary grad naryOM))
(size . (nary size naryOM))
(moment . (degreetoksML moment naryOM))
(transpose . (nary transpose naryOM))
(sum . (nary sum symbolsOM))
(product . (nary product symbolsOM))
(limit . (nary limit limitOM))
(tendsto . (tendstoML nil))
(df . (dfML nil))
(diff . (nary diff symbolsOM))
(partialdiff . (nary partialdiff partialdiffOM))
(conjugate . (nary conjugate naryOM))
(inverse . (nary inverse naryOM))
(abs . (nary abs naryOM))
(gcd . (nary gcd naryOM))
(set . (containerML set containerOM))
(factorial . (nary factorial naryOM))
(max . (nary max naryOM))
(min . (nary min naryOM))
(and . (nary and naryOM))
(or . (nary or naryOM))
(xor . (nary xor naryOM))
(selector . (nary selector selectOM))
(cos . (nary cos naryOM))
(sin . (nary sin naryOM))
(sec . (nary sec naryOM))
(cosh . (nary cosh naryOM))
(cot . (nary cot naryOM))
(coth . (nary coth naryOM))
(csch . (nary csch naryOM))
(arccos . (nary arccos naryOM))
(arcsin . (nary arcsin naryOM))
(arctan . (nary arctan naryOM))
(sech . (nary sech naryOM))
(sinh . (nary sinh naryOM))
(tan . (nary tan naryOM))
(tanh . (nary tanh naryOM))
(csc . (nary csc naryOM))
(arg . (nary arg naryOM))
(real . (nary real naryOM))
(exp . (nary exp naryOM))
(not . (nary not naryOM))
(rem . (nary rem naryOM))
(imaginary . (nary imaginary naryOM))
(quotient . (quotientML quotient naryOM))
(divide . (quotientML divide naryOM))
(equivalent . (nary equivalent naryOM))
(approx . (nary approx naryOM))
(implies . (nary implies naryOM))
(plus . (nary plus naryOM))
(times . (nary times naryOM))
(power . (nary power naryOM))
(median . (nary median naryOM))
(mean . (nary mean naryOM))
(sdev . (nary sdev naryOM))
(variance . (nary variance naryOM))
(mode . (nary mode naryOM))
(compose . (nary compose naryOM))
(root . (degreetoksML root naryOM))
(log . (log_baseML log naryOM))
(logb . (log_baseML logb))
(log10 . (log_baseML log10))
(ln . (nary ln naryOM))
(eq . (reln eq naryOM))
(neq . (reln neq naryOM))
(gt . (reln gt naryOM))
(lt . (reln lt naryOM))
(geq . (reln geq naryOM))
(leq . (reln leq naryOM))
(union . (sets union naryOM))
(intersect . (sets intersect naryOM))
(in . (reln in naryOM))
(notin . (reln notin naryOM))
(subset . (reln subset naryOM))
(prsubset . (reln prsubset naryOM))
(notsubset . (reln notsubset naryOM))
(notprsubset . (reln notprsubset naryOM))
(setdiff . (sets setdiff naryOM))
(rational . (rationalML nil naryOM))
(matrix . (matrixML nil matrixOM))
(minus . (minusML nil naryOM))
(int . (nary int symbolsOM))
(equal . (equalML nil naryOM))
(bvar . (bvarML nil))
(degree . (degreeML nil))
(interval . (containerML interval intervalOM))
(integer_interval . (containerML interval intervalOM))
(condition . (conditionML nil))
(lowupperlimit . (lowupperlimitML nil intervalOM))
(lowlimit . (lowlimitML nil))
(fn . (csymbol_fn nil))
%Ident has no OpenMath equivalent
(ident . (identML nil))
(forall . (nary forall quantOM))
(exists . (nary exists quantOM))
(list . (containerML list containerOM)));


% This table contains all the OpenMath elements which are understood by the
% translator and which have a MathML equivalent. The symbol and originating CDs
% are contained in this table.

valid_om!*:=
'((divide . (arith1))
(integer (omtypes))
(float (omtypes))
(selector .(linalg3))
(complex_cartesian . (nums1))
(complex_polar . (nums1))
(based_integer . (nums1))
(equivalent . (logic2))
(approx . (relation2))
(determinant . (linalg3))
(transpose . (linalg3))
(inverse . (fns1 arith2))
(in . (set1 multiset1))
(subset . (set1 multiset1))
(prsubset . (set1 multiset1))
(notsubset . (set1 multiset1))
(notprsubset . (set1 multiset1))
(set . (set1 multiset1))
(setdiff . (set1 multiset1))
(union . (set1 multiset1))
(notin . (set1 multiset1))
(intersect . (set1 multiset1))
(implies . (logic1))
(not . (logic1))
(xor . (logic1))
(vectorproduct . (linalg1))
(vector . (linalg1 linalg2))
(or . (logic1))
(forall . (quant1))
(and . (logic1))
(mean . (stats1))
(mode . (stats1))
(variance . (stats1))
(sdev . (stats1))
(moment . (stats1))
(median . (stats1))
(sin . (transc1))
(sinh . (transc1))
(arcsin . (transc1))
(arcsinh . (transc1 transc2))
(sec . (transc1))
(sech . (transc1))
(arcsec . (transc1 transc2))
(arcsech . (transc1 transc2))
(cos . (transc1))
(arccos . (transc1))
(cosh . (transc1))
(arccosh . (transc1 arctrans2))
(csc . (transc1))
(csch . (transc1))
(arccsc . (transc1 transc2))
(arccsch . (transc1 transc2))
(tan . (transc1))
(tanh . (transc1))
(arctan . (transc1))
(arctanh . (transc1 transc2))
(cot . (transc1))
(coth . (transc1))
(arccot . (transc1 transc2))
(arccoth . (transc1 transc2))
(ln . (transc1))
(exp . (transc1))
(abs . (arith1))
(plus . (arith1))
(times . (arith1 arith2))
(power . (arith1))
(factorial . (integer1))
(minus . (arith1))
(rem . (integer1))
(conjugate . (arith1))
(root . (arith1))
(log . (transc1))
(int . (calculus1))
(gcd . (integer1))
(quotient . (integer1))
(sum . (arith1))
(product . (arith1))
(scalarproduct . (linalg1))
(outerproduct . (linalg1))
(diff . (calculus1))
(partialdiff . (calculus1))
(eq . (relation1))
(neq . (relation1))
(leq . (relation1))
(geq . (relation1))
(lt . (relation1))
(gt . (relation1))
(quotient . (integer1))
(interval . (interval1))
(integer_interval . (interval1))
(min . (minmax1))
(max . (minmax1))
(imaginary . (nums1))
(real . (nums1))
(forall . (quant1))
(exists . (quant1))
(lambda . (fns1))
(list . (list1))
(arg . (arith2))
(type . (typmml))
(rational . (nums1))
(curl . (veccalc1))
(divergence . (veccalc1))
(grad . (veccalc1))
(size . (linalg3))
(laplacian . (veccalc1)));


% The following table keeps information about OpenMath elements which have
% a MathML equivalent, but with another name. It also makes sure that
% the MathML translation has the correct attributes when attributes
% are needed to have a correct semantic translation.
%
% The format of the table is:
% (OpenMath_symbol_name . (one_or_more_CDs  MathML_equivalent  MathML_attribute))

special_cases!*:=
'((unary_minus . (arith1 minus nil))
(both_sides . (limit1 nil nil))
(above . (limit1 tendsto above))
(below . (limit1 tendsto below))
(null . (limit1 nil nil))
(multiset . (multiset1 set (type multiset)))
(complex_cartesian_type . (typmml complex_cartesian nil))
(complex_polar_type . (typmml complex_polar nil))
(constant_type . (typmml constant nil))
(fn_type . (typmml csymbol nil))
(integer_type . (typmml integer nil))
(list_type . (typmml list nil))
(matrix_type . (typmml matrix nil))
(rational_type . (typmml rational nil))
(real_type . (typmml real nil))
(set_type . (typmml set nil))
(vector_type . (typmml vectorml nil))
(integer_interval . (interval1 interval nil))
(interval_oo . (interval1 interval (closure open)))
(interval_cc . (interval1 interval (closure close)))
(interval_oc . (interval1 interval (closure open!-closed)))
(interval_co . (interval1 interval (closure closed!-open))));

% The following table specifies when it is
% necessary to call a function to deal in a
% precise way with the translation of the
% symbol.

special_cases2!*:=
'((matrix . (matrixIR))
(limit . (limitIR))
(vector_selector . (selectIR))
(matrix_selector . (selectIR))
(complex_cartesian . (numIR))
(complex_polar . (numIR))
(rational . (numIR))
(defint . (integralIR))
(int . (integralIR))
(diff . (integralIR))
(partialdiff . (partialdiffIR))
(sum . (sum_prodIR))
(product . (sum_prodIR))
(one . (unaryIR alg1 1))
(zero . (unaryIR alg1 0))
(i . (unaryIR nums1 !&ImaginaryI!;))
(e . (unaryIR nums1 !&ExponentialE!;))
(pi . (unaryIR nums1 !&pi!;))
(nan . (unaryIR nums1 !&NotANumber!;))
(gamma . (unaryIR nums1 !&gamma!;))
(infinity . (unaryIR nums1 !&infin!;))
(false . (unaryIR logic1 !&false!;))
(true . (unaryIR logic1 !&true!;)));


% This table contains the OpenMath elements which map simply to MathML.
% These symbols have direct mapping into MathML.

mmleq!*:= '(divide based_integer
equivalent approx determinant transpose inverse in subset prsubset
notsubset notprsubset set setdiff union notin intersect implies not xor
vectorproduct vector or forall and mean mode variance sdev moment median
sin sinh arcsin arcsinh sec sech arcsec arcsech cos arccos cosh arccosh
csc csch arccsc arccsch tan tanh arctan arctanh cot coth arccot arccoth
ln exp abs plus times power factorial minus rem conjugate root log gcd
quotient scalarproduct outerproduct eq neq leq geq lt gt quotient
interval min max imaginary real forall exists lambda list arg type
laplacian divergence curl grad size integer);

end;
