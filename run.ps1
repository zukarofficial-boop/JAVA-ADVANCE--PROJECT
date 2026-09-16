$env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.2.1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Write-Host "=========================================================================" -ForegroundColor Cyan
Write-Host "Starting Help Desk & Ticket Management System (Java 26 Console)..." -ForegroundColor Green
Write-Host "=========================================================================" -ForegroundColor Cyan

if (Test-Path "target\helpdesk-ticket-system-1.0.0.jar") {
    & "$env:JAVA_HOME\bin\java.exe" -jar "target\helpdesk-ticket-system-1.0.0.jar"
} else {
    .\mvnw.cmd compile exec:java
}
