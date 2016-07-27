#!/bin/tcsh -f

set solr_log = "$1"
set solr_core = "$2"
set run_date = "$3"

grep -h "\[$solr_core"'_.*'"\] webapp=/solr path=/select_anchor params={" "$solr_log" | grep -F "INFO  - $run_date" | sed -e "s/^INFO  - $run_date /$run_date /" -e "s/; org.apache.solr.core.SolrCore; \[$solr_core"'_.*'"\] webapp=\/solr path=\/select_anchor params={/\t/" -e 's/} hits=.*QTime=/\t/'
