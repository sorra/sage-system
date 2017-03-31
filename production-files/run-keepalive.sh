# Stop existing loops
existing=$(ps -ef | grep func-keepalive.sh | grep -v grep | awk '{print $2}')
if [ "$existing" ] ; then
echo ${existing} | xargs kill
fi
# Start a new loop
nohup sh func-keepalive.sh >> keepalive.nohup.log 2>&1 &
sleep 0.1