rem test --- Run a REDUCE test file.

rem Author: Anthony C. Hearn.
rem Modified by FJW for testing multiple files entirely within current directory.

:loop
if "%1" == "" goto ret
set testfile=%1
shift

if %lisp% == psl goto psl

start /wait /min %reduce%\lisp\csl\%MACHINE%\csl -i %reduce%\lisp\csl\reduce.img test.dat -- %testfile%.lg

goto loop
:psl

start /wait /min %reduce%\lisp\psl\%MACHINE%\psl\bpsl -td 6000000 -f %reduce%\lisp\psl\%MACHINE%\red\reduce.img -i test.dat -o %testfile%.lg

goto loop
:ret
set testfile=
