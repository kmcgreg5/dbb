#! /bin/sh
#
##############################################################################################
##
# *******************************************************************************
# * Licensed Materials - Property of IBM
# * (c) Copyright IBM Corporation 2022. All Rights Reserved.
# * 
# * Note to U.S. Government Users Restricted Rights:  
# * Use, duplication or disclosure restricted by GSA ADP Schedule 
# * Contract with IBM Corp. 
# *******************************************************************************
##
##  DBB Static Report Migration Groovy script execution.
##
#######################################################################

if [ -z "${DBB_HOME:+set}" ]; then
   echo "DBB_HOME must be set"
   exit 1
fi

for arg do
    shift
    #[ "$arg" = "--grp" ] && [ "$1" = "*" ] && shift; continue
    #[ "$arg" = "--debug" ] && debug=-Dorg.slf4j.simpleLogger
    set -- "$@" "$arg"
done

SCRIPT_DIR=$(dirname "$0")

echo "$@"
#$DBB_HOME/bin/groovyz ${debug} $SCRIPT_DIR/../groovy/create-migration-list.groovy "$@"
exit $?
