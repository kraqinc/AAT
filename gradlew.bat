@echo off
setlocal enabledelayedexpansion
set GRADLE_VERSION=9.3.1
set GRADLE_DIST=gradle-%GRADLE_VERSION%-bin.zip
set GRADLE_URL=https://services.gradle.org/distributions/%GRADLE_DIST%
set CACHE_DIR=%USERPROFILE%\.gradle\custom-gradle\%GRADLE_VERSION%
set ROOT_DIR=%~dp0

if not exist "%CACHE_DIR%\bin\gradle.bat" (
  echo Downloading Gradle %GRADLE_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ProgressPreference='SilentlyContinue';" ^
    "$zip = Join-Path $env:TEMP '%GRADLE_DIST%';" ^
    "Invoke-WebRequest -Uri '%GRADLE_URL%' -OutFile $zip;" ^
    "Remove-Item -Recurse -Force '%CACHE_DIR%' -ErrorAction SilentlyContinue;" ^
    "New-Item -ItemType Directory -Force -Path '%CACHE_DIR%' | Out-Null;" ^
    "Expand-Archive -Path $zip -DestinationPath '%USERPROFILE%\.gradle\custom-gradle' -Force;" ^
    "Move-Item -Force '%USERPROFILE%\.gradle\custom-gradle\gradle-%GRADLE_VERSION%' '%CACHE_DIR%';" ^
    "Remove-Item $zip -Force"
)

"%CACHE_DIR%\bin\gradle.bat" -p "%ROOT_DIR%" %*
