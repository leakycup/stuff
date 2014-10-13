#!/bin/tcsh -ef

# defaults/initializations
set gateway = nitro-gw.blue.ygrid.yahoo.com
set todir = /homes/$USER/sombrero
set confdir = $todir/conf
set libdir = $todir/lib
set yellver = 4.1.3
set yelldir = $todir/libyell-$yellver
set fromdir = ""
set rulename = ""
set incr = ""
set yellbundle = libyell-$yellver.zip
set confbundle = sombreroconf.zip
set sombrero_on_grid = /export/crawlspace/sombrero_on_grid
set boostdir = $sombrero_on_grid/libyell-$yellver
set opencrawljar = $sombrero_on_grid/jars/opencrawl_1.jar
set pigfile = ""
set rulefile = ""
set runfile = ""
set gw_command_prefix = "ssh $gateway"
set vespa_services_xml = $sombrero_on_grid/scratch/vespa_services.xml

set localhost = `hostname`
if ("$localhost" != egldev1036) then
    set sombrero_on_grid = /net/egldev1036$sombrero_on_grid
endif

# process arguments
if ($#argv < 2) then
    goto USAGE
endif

set last_keyword = ""
foreach arg ($*)
    if ("$last_keyword" == "-gw") then
        set gateway = "$arg"
    endif
    if ("$last_keyword" == "-to") then
        set todir = "$arg"
    endif
    if ("$last_keyword" == "-from") then
        set fromdir = "$arg"
    endif
    if ("$last_keyword" == "-rule") then
        set rulename = "$arg"
    endif
    if ("$last_keyword" == "-pig") then
        set pigfile = "$arg"
    endif
    if ("$last_keyword" == "-rulefile") then
        set rulefile = "$arg"
    endif
    if ("$last_keyword" == "-runfile") then
        set runfile = "$arg"
    endif
    if ("$arg" == "-incr") then
        set incr = yes
    endif
    set last_keyword = "$arg"
end

if (("$fromdir" == "") && ($incr == "")) then
    goto USAGE
endif

if ("$rulename" != "") then
    if ($pigfile != "" || $rulefile != "" || $runfile != "") then
        echo "-rule can not be used with -pig or -rulefile or -runfile"
        exit 1
    endif
    set pigfile = $sombrero_on_grid/ruleapplier/pigs/$rulename.pig
    set rulefile = $sombrero_on_grid/ruleapplier/rules/$rulename.zip
    set runfile = $sombrero_on_grid/ruleapplier/runscripts/run.sh
    if (! -r $pigfile) then
        echo "pig-script $pigfile does not exist for $rulename"
        exit 1
    endif
    if (! -r $rulefile) then
        echo "rules-file $rulefile does not exist for $rulename"
        exit 1
    endif
endif

# TODO grab a kerberos ticket on gateway, if we don't have one already
# this is interactive and prompts for password. do this if an option is passed.
#$gw_command_prefix "klist | grep 'renew until'; if [$? != 0] then kinit $USER@DS.CORP.YAHOO.COM fi"

if ("$incr" == "") then # if not incremental
# create directory structure on gateway
    $gw_command_prefix mkdir $todir
    $gw_command_prefix mkdir $confdir $libdir $yelldir

    # install yell and copy boost
    $gw_command_prefix yinst install -force -noprerequisites -root $yelldir -nosudo -yes libyell-$yellver
    scp $boostdir/lib/libboost_regex.so.1 "$gateway":$yelldir/lib
    scp $boostdir/lib64/libboost_regex.so.1 "$gateway":$yelldir/lib64
endif

if ("$fromdir" != "") then
    # copy yell exception, regex and config files
    scp $fromdir/etc/yell/exception/sombrero.exp "$gateway":$yelldir/libdata/yell/exception
    scp $fromdir/etc/yell/regex/sombrero_patterns.txt "$gateway":$yelldir/libdata/yell/regex
    scp $fromdir/etc/yell/libyell.sombrero.config "$gateway":$yelldir/conf/yell
endif

# bundle what we need from libyell
$gw_command_prefix "cd $yelldir; find -L . '(' -wholename ./var -o -wholename ./tmp -o -wholename ./src -o -wholename './include' -o -wholename './share' -o -wholename './doc' -o -wholename './include' -o -wholename './share' -o -wholename './doc' ')' -prune -o -print0 | xargs -0 zip ../$yellbundle"

if ("$fromdir" != "") then
    # edit vespa-services.xml
    set tmpfile = `hostname`
    set tmpfile = $tmpfile.`cat /proc/self/status | grep '^PPid' | cut -f 2`
    set tmpfile = $tmpfile.`date -u +%N`
    set tmpfile = $sombrero_on_grid/scratch/$tmpfile
    sed -e 's/<installdir>\/home\/y\/<\/installdir>/<installdir>yell<\/installdir>/' $fromdir/src/main/yinst/conf/sombrero/vespa-services.xml > $tmpfile

    # copy sombrero config files
    # FIXME: 
    # temporary hack till sriram adds RuleApplierChain in
    # $fromdir/src/main/yinst/conf/sombrero/vespa-services.xml
    #scp $tmpfile "$gateway":$confdir/vespa-services.xml
    scp $vespa_services_xml "$gateway":$confdir/vespa-services.xml
    /bin/rm -f $tmpfile
    scp $fromdir/src/main/yinst/conf/sombrero/vespa-hosts.xml "$gateway":$confdir
    scp -r $fromdir/etc/searchdefinitions/ "$gateway":$confdir
endif

# bundle conf dir
$gw_command_prefix "cd $confdir; zip -r ../$confbundle *"

if ("$fromdir" != "") then
    # copy sombrero_docprocs-standalone.jar
    scp $fromdir/target/capsombrero_docprocs_standalone.jar "$gateway":$libdir
endif

if ("$incr" == "") then # if not incremental
    # copy opencrawl_1.jar
    scp $opencrawljar "$gateway":$todir
endif

# copy rules-file
if ($rulefile != "") then
    scp $rulefile "$gateway":$todir
    $gw_command_prefix "hadoop fs -rm $rulefile:t; hadoop fs -copyFromLocal $todir/$rulefile:t $rulefile:t"
endif

# copy pig-script
if ($pigfile != "") then
    scp $pigfile "$gateway":$todir
endif

# copy run-file
if ($runfile != "") then
    set dest_runfile = $todir/$runfile:t
    scp $runfile "$gateway":$dest_runfile
    if ($rulename != "") then
        set dest_runfile = $todir/$rulename.sh
        $gw_command_prefix "touch $dest_runfile; echo '#! /bin/sh' >> $dest_runfile; echo pigfile=$pigfile:t >> $dest_runfile; echo yellbundle=/user/$USER/$yellbundle:t >> $dest_runfile; echo confbundle=/user/$USER/$confbundle:t >> $dest_runfile; echo rulefile=/user/$USER/$rulefile:t >> $dest_runfile; cat $todir/$runfile:t | grep -v '^#' >> $dest_runfile"
    endif
    $gw_command_prefix "chmod +x $dest_runfile"
endif

# copy yell and conf bundle to HDFS
$gw_command_prefix "hadoop fs -rm $yellbundle; hadoop fs -copyFromLocal $todir/$yellbundle $yellbundle"
$gw_command_prefix "hadoop fs -rm $confbundle; hadoop fs -copyFromLocal $todir/$confbundle $confbundle"

# echo some useful prolog
if ($pigfile != "") then
    echo "*** Edit the pig-script $todir/$pigfile:t on $gateway to select your shard ***"
endif
if ($runfile != "") then
    echo "*** Run $dest_runfile on $gateway to run sombrero on grid ***"
endif

exit 0

USAGE:
echo "$0 [-from <sombrero build root>] [-gw <gateway machine name>] [-to <top-level directory in gateway machine>] [-rule <name of rule>] [-rulefile <rules-file>] [-pig <pig-script>] [-incr]"
exit 1

