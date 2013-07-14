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

SET CLASSPATH=%IOF_HOME%\lib\antlr-2.7.7.jar;%IOF_HOME%\lib\bcprov-jdk15-1.43.jar;%IOF_HOME%\lib\commons-codec-1.3.jar;%IOF_HOME%\lib\commons-compress-1.1.jar;%IOF_HOME%\lib\commons-httpclient-3.1.jar;%IOF_HOME%\lib\commons-lang-2.3.jar;%IOF_HOME%\lib\commons-io-2.0.1.jar;%IOF_HOME%\lib\commons-logging-1.0.4.jar;%IOF_HOME%\lib\commons-collections-3.2.jar;%IOF_HOME%\lib\commons-net-2.0.jar;%IOF_HOME%\lib\commons-vfs-2.0.jar;%IOF_HOME%\lib\dom4j-1.6.1.jar;%IOF_HOME%\lib\hibernate-commons-annotations-4.0.1.Final.jar;%IOF_HOME%\lib\hibernate-core-4.1.1.Final.jar;%IOF_HOME%\lib\hibernate-jpa-2.0-api-1.0.1.Final.jar;%IOF_HOME%\lib\io.framework.jar;%IOF_HOME%\lib\javassist-3.15.0-GA.jar;%IOF_HOME%\lib\jaxen-1.1.3.jar;%IOF_HOME%\lib\jboss-logging-3.1.0.GA.jar;%IOF_HOME%\lib\jboss-transaction-api_1.1_spec-1.0.0.Final.jar;%IOF_HOME%\lib\jdom-2.0.0.jar;%IOF_HOME%\lib\jsch-0.1.41.jar;%IOF_HOME%\lib\log4j-1.2.14.jar;%IOF_HOME%\lib\mail.jar;%IOF_HOME%\lib\org.eclipse.core.commands_3.5.0.I20090525-2000.jar;%IOF_HOME%\lib\org.eclipse.core.runtime_3.5.0.v20090525.jar;%IOF_HOME%\lib\org.eclipse.equinox.common_3.5.1.R35x_v20090807-1100.jar;%IOF_HOME%\lib\org.eclipse.jface_3.7.0.v20110928-1505.jar;%IOF_HOME%\lib\org.eclipse.swt.win32.win32.x86_3.7.0.jar;%IOF_HOME%\lib\quartz-all-2.1.3.jar;%IOF_HOME%\lib\sevenzipjbinding-AllPlatforms.jar;%IOF_HOME%\lib\sevenzipjbinding.jar;%IOF_HOME%\lib\slf4j-api-1.6.0.jar;%IOF_HOME%\lib\velocity-1.6.3.jar;%IOF_HOME%\lib\xercesImpl.jar;%IOF_HOME%\lib\xml-apis.jar;%IOF_HOME%\lib\truezip-7.6.1.jar

echo    ClassPath is %CLASSPATH%

SET NATIVE_CP=%IOF_HOME%/native
echo    NativePath is %NATIVE_CP%

REM Extensions JARS

SET CONTRIBUTION_CP=%IOF_HOME%\extensions\lib\*

echo Start Up Agent!
echo %JAVA_HOME%\bin\java -Xms512M -Xmx1024M -server -cp %CLASSPATH%;%CONTRIBUTION_CP% -Dlog4j.configuration=file:"E:/IOF/config/log4j.properties" -Djava.library.path=%NATIVE_CP%  pt.ptsi.stfe.io.engine.MainDisplayScheduler  %CONFIG_PATH%
%JAVA_HOME%\bin\java -Xms512M -Xmx1024M -server -cp %CLASSPATH%;%CONTRIBUTION_CP% -Dlog4j.configuration=file:"E:/IOF/config/log4j.properties" -Djava.library.path=%NATIVE_CP%  pt.ptsi.stfe.io.engine.MainDisplayScheduler  %CONFIG_PATH%
