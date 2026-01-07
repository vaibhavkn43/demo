@echo off
setlocal enabledelayedexpansion

REM ===============================
REM OUTPUT FILE
REM ===============================
set OUTPUT=project_snapshot.txt

REM Clear old output
if exist %OUTPUT% del %OUTPUT%

echo =============================== >> %OUTPUT%
echo PROJECT SNAPSHOT >> %OUTPUT%
echo Generated on %DATE% %TIME% >> %OUTPUT%
echo =============================== >> %OUTPUT%
echo. >> %OUTPUT%

REM ===============================
REM ROOT FILES
REM ===============================
call :writeFile pom.xml
call :writeFile build.gradle
call :writeFile settings.gradle
call :writeFile mvnw
call :writeFile mvnw.cmd

REM ===============================
REM SPRING BOOT SOURCE
REM ===============================
call :processDir src\main\java
call :processDir src\main\resources

echo. >> %OUTPUT%
echo =============================== >> %OUTPUT%
echo END OF SNAPSHOT >> %OUTPUT%
echo =============================== >> %OUTPUT%

echo Done! Output written to %OUTPUT%
exit /b

REM ===============================
REM FUNCTIONS
REM ===============================

:processDir
if not exist %1 exit /b
for /r %1 %%f in (
    *.java *.html *.js *.css *.json *.yml *.yaml *.properties *.xml
) do (
    call :writeFile "%%f"
)
exit /b

:writeFile
if not exist %1 exit /b
echo. >> %OUTPUT%
echo -------------------------------------------------- >> %OUTPUT%
echo FILE: %1 >> %OUTPUT%
echo -------------------------------------------------- >> %OUTPUT%
type %1 >> %OUTPUT%
exit /b
