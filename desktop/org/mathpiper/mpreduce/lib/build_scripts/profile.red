% Collect profile information about all REDUCE modules that have
% associated test scripts.  The information is put in "profile.dat"
% in the current directory but you then probably want to move it up
% to the place it really lives. This step is not automated here at
% present.

symbolic;

load!-module 'remake;

get_configuration_data();

delete!-file "profile.dat";

profile_a_package reduce_test_cases;

bye;



