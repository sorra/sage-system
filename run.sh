if test "$1" = "up"
  then cmd="startup"
elif test "$1" = "down"
  then cmd="shutdown"
else echo $1?
fi
$TOMCAT_HOME/bin/${cmd}.sh