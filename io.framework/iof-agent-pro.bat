@echo off

REM  
REM   IOF - IO Framework
REM   STFE - 2010,2012
REM  
REM   License Info
REM  

echo ### IO Framework startup Agent ###

SET IOF_HOME=%cd%
echo    IOF Home is %IOF_HOME%

SET CONFIG_PATH=%IOF_HOME%/config/config.properties

SET JAVA_VENDOR=Sun
SET JAVA_HOME=C:\Java\jdk1.7.0_02
echo    Java Home is %JAVA_HOME%

echo    ClassPath is %CLASSPATH%

SET NATIVE_CP=%IOF_HOME%/native
echo    NativePath is %NATIVE_CP%

REM Extensions JARS

SET CONTRIBUTION_CP=%IOF_HOME%\extensions\lib\*

echo Start Up Agent!
echo %JAVA_HOME%\bin\java -jar io.framework -Xms512M -Xmx1024M -server -cp %CLASSPATH%;%CONTRIBUTION_CP% -Dlog4j.configuration=file:"E:/IOF/config/log4j.properties" -Djava.library.path=%NATIVE_CP%    %CONFIG_PATH%
%JAVA_HOME%\bin\java -jar io.framework  -Xms512M -Xmx1024M -server -cp %CLASSPATH%;%CONTRIBUTION_CP% -Dlog4j.configuration=file:"E:/IOF/config/log4j.properties" -Djava.library.path=%NATIVE_CP%  %CONFIG_PATH%
