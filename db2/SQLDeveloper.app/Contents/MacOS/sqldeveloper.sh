#!/bin/bash

#----------------------------------------------------------------------
# Locate the correct Java Home to use. Priority is:
#   1) Checking for a product.conf that sets the value
#   2) Look for a JDK 11
#   3) Look for a JDK 17
#----------------------------------------------------------------------
findJavaHome()
{
  if [[ -f $HOME/.sqldeveloper/22.2.0/product.conf ]];
  then
    IncludeConfFile $HOME/.sqldeveloper/22.2.0/product.conf
  fi

  if [ -z "$APP_JAVA_HOME"]; then
    APP_JAVA_HOME=`/usr/libexec/java_home -F -v 11`
    if [ -z "$APP_JAVA_HOME" ] ; then
      APP_JAVA_HOME=`/usr/libexec/java_home -F -v 17`
    fi
  fi
}

#-----------------------------------------------------------------------------
# SetJavaHome: conf file directive
#   NOTE: Taken from launcher.sh
#-----------------------------------------------------------------------------
SetJavaHome()
{
  APP_JAVA_HOME="`Resolve $1`"
}

#-----------------------------------------------------------------------------
# Resolve: resolve relative paths in a conf file directive argument.
# Args:
#   1) A string containing zero or more relative paths to be resolved.
#
# Paths in conf file directive arguments are expected to be relative to the
# directory  containing the  conf file, but such paths cannot be naively passed
# to the JVM because the current directory when the JVM is launched may be a
# different directory. This function resolves relative paths embedded in
# arguments to absolute paths.
#
# A (Unix) relative path is a path that starts with "../" or "./". The following
# are examples of the arguments that need to be handled:
#   AddVMOption     -Xbootclasspath/p:../lib/lwawt.jar
#   AddVMOption     -Djava.endorsed.dirs=../../../oracle_common/modules/endorsed
#   AddJavaLibFile  ../../../jdeveloper/ide/lib/ide-boot.jar
#   AddVMOption     -Dide.cluster.dirs=../../../jdeveloper/netbeans/bridge/:../../../jdeveloper/netbeans/ide/
#   AddVMOption     -Dosgi.bundles=file:../lib/oracle.ide.osgi.jar@3:start
#   AddVMOption     -Xbootclasspath/p:../../../jdeveloper/rdbms/jlib/ojdi.jar
#   AddVMOption     -Doracle.ide.reportEDTViolations.exceptionsfile=./swing-thread-violations.conf
#
#-----------------------------------------------------------------------------
Resolve()
{
  # Only do this for strings that might contain relative paths
  local dotslash="./"
  local arg="$*"
  if [ "$arg" = "${arg%$dotslash*}" ]
  then
    echo "$*"
  else
    # We can't use the absolute $PWD path because it might have spaces in it
    # Instead make $PWD relative to the launching directory
    # Then make incoming path relative to that 'relative $PWD'
    # We save time by not recomputing the relative PWD while we
    # are in the same script
    local relative_pwd=""
    if [ "$LAST_PWD_MADE_RELATIVE" != "$PWD" ]
    then
      relative_pwd=`computeRelativePath "$PWD" "$LaunchDir"`
    else
      relative_pwd="$LAST_RELATIVE_PWD"
    fi

    if [ "$relative_pwd" = "" ]
    then
      echo "$*"
    else
      # Any occurrence of "../" or "./" that is not preceded by one of "-", ".",
      # "/", "$", a letter, a digit, or "_" starts a relative path.
      # Merging the four substitutions requires regular expression
      # features not supported in all environments.
      echo "$*" | sed -e "s|^[.][.]/|$relative_pwd/../|; s|^[.]/|$relative_pwd/|; s|\([^-./$\w]\)[.][.]/|\1$relative_pwd/../|g; s|\([^-./$\w]\)[.]/|\1$relative_pwd/|g"
    fi
  fi
}

computeRelativePath()
{
  # Canonicalize and remove trailing '/' if any
  pushd "$1" > /dev/null 2>&1
  MakeRelative=`pwd -P`
  popd > /dev/null 2>&1
  MakeRelative=`echo "$MakeRelative" | sed -e "s|\n||g"`
  MakeRelative=`echo "$MakeRelative" | sed -e "s|/$||g"`
  pushd "$2" > /dev/null 2>&1
  StartingDir=`pwd -P`
  popd > /dev/null 2>&1
  StartingDir=`echo "$StartingDir" | sed -e "s|\n||g"`
  StartingDir=`echo "$StartingDir" | sed -e "s|/$||g"`

  # Split paths into pieces separated by '/'
  local OLDIFS=$IFS
  IFS="/"

  local makeRelativeArrayLength=0
  local makeRelativeArray="makeRelativeArray"
  for piece in $MakeRelative
  do
    # Need to doubly quote in case path pieces have spaces
    eval ${makeRelativeArray}${makeRelativeArrayLength}="'$piece'"
    let makeRelativeArrayLength++
  done

  local startingDirArrayLength=0
  local startingDirArray="startingDirArray"
  for piece in $StartingDir
  do
    # Need to doubly quote in case path pieces have spaces
    eval ${startingDirArray}${startingDirArrayLength}="'$piece'"
    let startingDirArrayLength++
  done

  IFS=$OLDIFS

  # First skip the parts of the paths that are the same
  local i
  for (( i = 0; i < $makeRelativeArrayLength && i < $startingDirArrayLength; i++ ))
  do
    local makeRelativePiece=$(echo $(eval echo \$$makeRelativeArray${i}))
    local startingDirPiece=$(echo $(eval echo \$$startingDirArray${i}))
    if [ "$makeRelativePiece" != "$startingDirPiece" ]
    then
      break
    fi
  done

  # Then collect the dotdots
  local dotdots=""
  local j
  for (( j = i; j < $startingDirArrayLength ; j++ ))
  do
    dotdots="${dotdots}../"
  done

  #Then collect the rest
  local appendToEnd=""
  for (( j = i; j < $makeRelativeArrayLength; j++ ))
  do
    local makeRelativePiece=$(echo $(eval echo \$$makeRelativeArray${j}))
    appendToEnd="${appendToEnd}$makeRelativePiece/"
  done

  # Strip any trailing '/' and we're done
  appendToEnd=`echo $appendToEnd | sed -e "s|/$||g"`
  echo "${dotdots}${appendToEnd}"
}

#-----------------------------------------------------------------------------
# IncludeConfFile: conf file directive
# Args:
#   1) file name of another conf file to include
#
  #
# A .conf file can specify that it includes another .conf file.  This
# allows common configuration to be shared across IDE products. Relative paths
# within a conf file are resolved against its containing directory.
#-----------------------------------------------------------------------------
IncludeConfFile()
{
  if ( [ -f "$1" ] )
  then
    local verboseIndent="${APP_VERBOSE_INDENT}"
    APP_VERBOSE_INDENT="${APP_VERBOSE_INDENT}    "
    local previousConfigFile="${CURRENT_CONFIG_FILE}"
    CURRENT_CONFIG_FILE="$1"
    # Broken up into steps to be able to handle paths with spaces
    local PARENT=$PWD
    local dirName=`dirname "$1"`
    cd "$dirName"
    local cur_last_pwd_made_relative="$LAST_PWD_MADE_RELATIVE"
    local cur_last_relative_pwd="$LAST_RELATIVE_PWD"
    if [ "$LAST_PWD_MADE_RELATIVE" != "$PWD" ]
    then
      LAST_PWD_MADE_RELATIVE="$PWD"
      LAST_RELATIVE_PWD=`computeRelativePath "$PWD" "$LaunchDir"`
    fi
    local baseName=`basename "$1"`
    source "./$baseName"
    cd "$PARENT"
    LAST_PWD_MADE_RELATIVE="$cur_last_pwd_made_relative"
    LAST_RELATIVE_PWD="$cur_last_relative_pwd"
    APP_VERBOSE_INDENT="${verboseIndent}"
    CURRENT_CONFIG_FILE="${previousConfigFile}"
  else
    echo "Unable to find configuration file \"$1\" in \"$PWD\""
  fi
}


#-----------------------------------------------------------------------
# If no home was found, tell the user to install Java
# Using a 12+ JDK requires explicitly setting it in the product.conf
#-----------------------------------------------------------------------
findJavaHome
if [ -z "$APP_JAVA_HOME" ] ; then
      osascript -e 'tell app "System Events" to display alert "SQL Developer supports Java SE 11 and 17. \nJava SE can be downloaded from:\n https://www.oracle.com/java/technologies/javase-downloads.html"'
      exit 1
fi

echo $APP_JAVA_HOME

export JAVA_HOME=$APP_JAVA_HOME

if [[ -f $HOME/.sqldeveloper/22.2.0/env.sh ]];
then
  	source "$HOME/.sqldeveloper/22.2.0/env.sh" >> /dev/null
elif [[ -f $HOME/.sqldeveloper/env.sh ]]; 
then
	source "$HOME/.sqldeveloper/env.sh"  >> /dev/null
fi

here="${0%/*}"
cd "${here}"
cd ../Resources/sqldeveloper/sqldeveloper/bin
bash ./sqldeveloper >>/dev/null
