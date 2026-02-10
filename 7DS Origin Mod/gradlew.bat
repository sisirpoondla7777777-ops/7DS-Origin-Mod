@echo off
set DIR=%~dp0
set WRAPPER_JAR=%DIR%gradle\wrapper\gradle-wrapper.jar
if not exist "%WRAPPER_JAR%" (
  echo Missing %WRAPPER_JAR%
  echo Download it from https://services.gradle.org/distributions/gradle-8.7-bin.zip (gradle-wrapper.jar)
  echo Or run: gradle wrapper (if you have Gradle installed)
  exit /b 1
)
"%JAVA_HOME%\bin\java" -jar "%WRAPPER_JAR%" %*
