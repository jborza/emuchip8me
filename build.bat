@echo off
rem
rem This batch file builds and preverifies the code for the app, then packages in a JAR.
rem
if "%OS%" == "Windows_NT" setlocal
set DEMO=chip8
rem set path to WTK installation
set LIB_DIR=c:\WTK21\lib
set CLDCAPI=%LIB_DIR%\cldcapi10.jar
set MIDPAPI=%LIB_DIR%\midpapi20.jar
set PREVERIFY=c:\WTK21\bin\preverify

set JAVA_FILES=
set JAVA_FILES=%JAVA_FILES% src\com\jborza\chip8me\*.java

rem set path to JDK here
set JAVAC=c:\j2sdk1.4.2_19\bin\javac
set JAR=c:\j2sdk1.4.2_19\bin\jar


echo *** Creating directories ***
if not exist tmpclasses md tmpclasses
if not exist classes md classes

echo *** Compiling source files ***
%JAVAC% -bootclasspath %CLDCAPI%;%MIDPAPI% -d tmpclasses -classpath tmpclasses %JAVA_FILES%

echo *** Preverifying class files ***
%PREVERIFY% -classpath %CLDCAPI%;%MIDPAPI%;tmpclasses -d classes tmpclasses

echo *** Jaring preverified class files ***
%JAR% cmf MANIFEST.MF %DEMO%.jar -C classes .

if exist res (
    echo *** Jaring resource files ***
    %JAR% uf %DEMO%.jar -C res .
)

echo *** Updating JAR size in JAD file ***
python update-build-size.py

:end
if "%OS%" == "Windows_NT" endlocal

