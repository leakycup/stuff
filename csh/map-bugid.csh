#!/bin/csh -f

set cset_filename = $1

foreach filename ("`cat $cset_filename | sed -e 's/[ ].*//'`")
    echo "$filename" "`grep -F -x -l $filename /auto/os-comp/MCP_DEV_OS_INFRA_FIN_DTHO-branch-diff/per-ddts-id-mcp_dev_os_infra/*.cset1 | sort -u | sed -e 's/^.*CSC/CSC/' | sed -e 's/[.]cset1//'`"
end # foreach filename
