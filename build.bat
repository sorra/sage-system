echo Begin build...
call mvn clean package
xcopy /Y %cd%\target\sage %TOMCAT_HOME%\webapps\sage\ /e /q
echo Build OK!