echo Begin build...
call mvn package
xcopy /Y %cd%\target\sage %TOMCAT_HOME%\webapps\sage\ /e /q
echo Build OK!