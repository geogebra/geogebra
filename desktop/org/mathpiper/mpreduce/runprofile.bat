if "L%1" == "L" then goto L
set m=%1
goto M
:L
set m=alg
:M
echo testing module %m%
java -classic -Djava.compiler=none -Xrunhprof:cpu=samples Jlisp -w profile.red -Dwhich_module=%m%

