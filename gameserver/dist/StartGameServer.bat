@echo off
title Lineage II GameServer - JTS
:start
echo Starting Lineage II - GameServer(High Five).
echo.

java -Duser.timezone=Europe/Moscow -server -Dfile.encoding=UTF-8 -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -Xms1024m -Xmx1024m -cp config;./lib/* org.mmocore.gameserver.GameServer

REM Debug ...
REM java -Dfile.encoding=UTF-8 -cp config;./lib/* -Xmx1G -Xnoclassgc -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7456 org.mmocore.gameserver.GameServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo GameServer restarted ...
echo.
goto start
:error
echo.
echo GameServer terminated abnormaly ...
echo.
:end
echo.
echo GameServer terminated ...
echo.

pause
