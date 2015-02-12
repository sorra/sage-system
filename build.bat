echo Begin build...
call mvn clean package
rd /s /q %TOMCAT_HOME%\webapps\ROOT\
xcopy /Y %cd%\target\sage %TOMCAT_HOME%\webapps\ROOT\ /e /q
echo Build OK!