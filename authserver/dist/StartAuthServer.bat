@echo off
title Lineage II AuthServer - JTS
:start
echo Starting Lineage II - AuthServer(High Five).
echo.
java -Duser.timezone=Europe/Moscow -server  -Dfile.encoding=UTF-8 -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -Xms1024m -Xmx1024m -cp config;./lib/* org.mmocore.authserver.AuthServer
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo AuthServer restarted ...
echo.
goto start
:error
echo.
echo AuthServer terminated abnormaly ...
echo.
:end
echo.
echo AuthServer terminated ...
echo.

pause
