echo Begin build...
call mvn clean package
rd /s /q %TOMCAT_HOME%\webapps\sage\
xcopy /Y %cd%\target\sage %TOMCAT_HOME%\webapps\sage\ /e /q
echo Build OK!