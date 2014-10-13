#/bin/csh -f

set CYGROOT	= /tools/gcc-3.3/$VCO
setenv LD_LIBRARY_PATH .":"$CYGROOT/lib":"$LD_LIBRARY_PATH

echo "#****runitshared_ccs*********#"
./runitshared_ccs
echo "#****runitG_ccs*********#"
./runitG_ccs
echo "#****runitG_gcc*********#"
./runitG_gcc
echo "#****runitshared_gcc1*********#"
./runitshared_gcc1
echo "#****runitshared_gcc2*********#"
./runitshared_gcc2

rm -f core
