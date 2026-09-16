@echo off
setlocal
if "%JAVA_HOME%"=="" set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.2.1"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo =========================================================================
echo Starting Help Desk & Ticket Management System (Java 26 Console)...
echo =========================================================================

if exist "target\helpdesk-ticket-system-1.0.0.jar" (
    "%JAVA_HOME%\bin\java.exe" -jar "target\helpdesk-ticket-system-1.0.0.jar"
) else (
    call mvnw.cmd compile exec:java
)
pause
