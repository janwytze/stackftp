# StackFtp

StackFtp is a ftp server that is linked to a stack Webdav server.  

## Configuration
StackFtp can be configured with environment variables.  
The following environment variables are available:  

##### FTP_ADDRESS
Default: `127.0.0.1`  
The address the ftp server will run on.

##### FTP_PORT
Default: `21`  
The port the ftp server will run on.

##### FTP_SSL
Default: `false`  
Does the server support SSL?

##### FTP_KEYSTORE
Default: **None**  
The keystore path for SSL.

##### FTP_KEYSTORE_PASSWORD
Default: **None**  
The keystore password.

##### FTP_IDLE_TIME
Default: `3600`  
The maximum amount of seconds idle time of an user.

## Building
Building StackFtp can be done using maven.  
Execute the following command to build the application:
```
mvn clean install
```
This will create a `.war` file that can be executed with java.
