CYGROOT	= /tools/gcc-3.3/$(VCO)
CYGLD	= $(CYGROOT)/bin/ld -V
CYGXX	= $(CYGROOT)/bin/g++ -v
CYGCC	= $(CYGROOT)/bin/gcc -v

all: runitshared_ccs runitG_ccs runitG_gcc runitshared_gcc1 runitshared_gcc2 libloadable-gccshared.so libloadable-gccG.so

LOADLIBDEP = loadable.c
RUNEXECDEP = runit.cxx
TESTLIBDEP = tests.cxx
RUNEXECOBJ = $(subst .cxx,.o,$(RUNEXECDEP))
TESTLIBOBJ = $(subst .cxx,.o,$(TESTLIBDEP))

runitshared_ccs: $(RUNEXECOBJ) libtest-gccshared.so
	@echo "=====================================================================================================\n";
	/usr/ccs/bin/ld	-V $< -o $@ -L. -ltest-gccshared -ldl -lc -L$(CYGROOT)/lib -lstdc++ -lgcc_s

runitG_ccs: $(RUNEXECOBJ) libtest-gccG.so
	@echo "=====================================================================================================\n";
	/usr/ccs/bin/ld	-V $< -o $@ -L. -ltest-gccG -ldl -lc -L$(CYGROOT)/lib -lstdc++ -lgcc_s

runitG_gcc: $(RUNEXECOBJ) libtest-gccG.so
	@echo "=====================================================================================================\n";
	$(CYGXX) $< -o	$@ -L. -ltest-gccG -ldl

runitshared_gcc1: $(RUNEXECOBJ) libtest-gccshared.so
	@echo "=====================================================================================================\n";
	$(CYGXX) $< -o $@  -Wl,-export-dynamic -L.	-ltest-gccshared -ldl

runitshared_gcc2: $(RUNEXECOBJ) libtest-gccshared.so
	@echo "=====================================================================================================\n";
	$(CYGXX) $< -o $@  -L.	-ltest-gccshared -ldl

$(RUNEXECOBJ): $(RUNEXECDEP)
	@echo "=====================================================================================================\n";
	$(CYGXX) -DDLL1=\"./libloadable-gccshared.so\" -DDLL2=\"./libloadable-gccG.so\" -g -c $^	-o $@

libtest-gccG.so: $(TESTLIBDEP)
	@echo "=====================================================================================================\n";
	$(CYGXX) -g -fPIC -Wl,-G $^ -o $@ -nodefaultlibs -lgcc -lc -lstdc++

libtest-gccshared.so: $(TESTLIBOBJ)
	@echo "=====================================================================================================\n";
	$(CYGXX) -g -shared $^ -o $@ -nodefaultlibs -lgcc -lc

$(TESTLIBOBJ): $(TESTLIBDEP)
	@echo "=====================================================================================================\n";
	$(CYGXX) -g -fPIC -c $^ -o foo.o;
	$(CYGLD) -g -Ur -Bstatic -L$(CYGROOT)/lib -lstdc++ foo.o -o $@
	rm -f foo.o

libloadable-gccshared.so:	$(LOADLIBDEP)
	@echo "=====================================================================================================\n";
	$(CYGCC) -g -fPIC -shared $^ -o $@ -nodefaultlibs -lgcc -lc

libloadable-gccG.so: $(LOADLIBDEP)
	@echo "=====================================================================================================\n";
	 $(CYGCC) -g -fPIC -Wl,-G $^ -o $@ -nodefaultlibs -lgcc -lc

clean:
	rm -f *.o *.so run_* core

