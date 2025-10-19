@REM @echo off
@REM setlocal enableextensions
@REM pushd %~dp0
@REM
@REM cd ..
@REM call gradlew clean shadowJar
@REM
@REM cd build\libs
@REM for /f "tokens=*" %%a in (
@REM     'dir /b *.jar'
@REM ) do (
@REM     set jarloc=%%a
@REM )
@REM
@REM java -jar %jarloc% < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL.TXT
@REM
@REM cd ..\..\text-ui-test
@REM
@REM FC ACTUAL.TXT EXPECTED.TXT >NUL && ECHO Test passed! || Echo Test failed!
@REM pause
