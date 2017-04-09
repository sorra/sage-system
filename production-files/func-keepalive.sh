echo "Current DIR is " $PWD
while :
do
stillRunning=$(ps -ef | grep sage.ApplicationKt | grep -v grep)
if [ "$stillRunning" ] ; then
:
else
echo "Service was not started"
echo "Starting service ..."
sh run.sh
echo "Service was exited!"
fi
sleep 5
done