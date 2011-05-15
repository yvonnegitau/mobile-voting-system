@ECHO off
IF NOT EXIST %OPENSSL_CONF% GOTO trypath
set WD=%OPENSSL_CONF:openssl.cfg=%
%WD%openssl req -new -newkey rsa:1024 -nodes -subj "/CN=VotingPointDefault" -keyout delete.key -out delete.rq
if %ERRORLEVEL% NEQ 0 goto trypath
%WD%openssl x509 -req -days 365 -in delete.rq -signkey delete.key -out delete.crt
%WD%openssl pkcs12 -export -inkey delete.key -in delete.crt -password pass:12345 -out certs\server.p12
del delete.*
del .rnd
exit


:trypath
openssl req -new -newkey rsa:1024 -nodes -subj "/CN=VotingPointDefault" -keyout delete.key -out delete.rq
if %ERRORLEVEL% NEQ 0 goto failed
openssl x509 -req -days 365 -in delete.rq -signkey delete.key -out delete.crt
openssl pkcs12 -export -inkey delete.key -in delete.crt -password pass:12345 -out server.p12
del delete.*
del .rnd
exit

exit



:failed   
echo Please install OpenSSL, or add it into your path variable. Please visit http://www.openssl.org/ for information
