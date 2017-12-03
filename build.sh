./gradlew clean webmin build && mkdir build/app && cd build/app && jar xf ../libs/sage-1.0-SNAPSHOT.jar \
&& cp -r ../../src/main/webapp public && cp ../../*.sh . && cp ../../production-files/* .