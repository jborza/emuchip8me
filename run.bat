@echo off
rem This file runs the corresponded demo.

if "%OS%" == "Windows_NT" setlocal
set DEMO=chip8


if not exist .\%DEMO%.jar (
  echo *** You should build the %DEMO%.jar first. ***
  goto end
)

rem set the WTK emulator path here
c:\WTK21\bin\emulator -Xdescriptor:%DEMO%.jad

:end
if "%OS%" == "Windows_NT" endlocal

rem do a "pause" always
pause
