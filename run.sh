nohup java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=~/dumps \
  -cp './BOOT-INF/classes:./BOOT-INF/lib/*' sage.ApplicationKt >> app.nohup.log 2>&1 &
sleep 0.1