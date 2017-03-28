./gradlew clean webmin build && mkdir build/app && cd build/app && jar xf ../libs/sage-0.3-SNAPSHOT.jar \
&& cp -r ../../src/main/webapp public && cp ../../*.sh . && cd ../..