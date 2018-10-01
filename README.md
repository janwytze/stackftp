# StackFTP

StackFTP is a nifty piece of software that aims to provide a simple and sleek solution to act as a translation mechanism from the FTP to the WebDAV protocol.

## Naming
StackFTP was initially named after the [Stack](https://www.transip.nl/stack/) product, as offered by the Dutch hosting company [TransIP](https://www.transip.nl/).
TransIP has built their "Stack" based on OwnCloud, which means our software should offer full compatibility with other WebDAV implementations.

## Configuration
StackFTP shall be used with the following environment configs.

##### FTP_HOST
You can define an IP address to bond to. By default, StackFTP will listen at `127.0.0.1`.

##### FTP_PORT
Using `FTP_PORT` you can decide what port StackFTP should use to listen at. By default, it will use the standard FTP port `21`.

##### FTP_SSL
By default StackFTP offers no SSL encrypted connection. You can, however, enable this function by setting this value to `true`.

##### FTP_KEYSTORE
Default: **None**  
This value represents the keystore to use when using SSL to encrypt the connection.
** REQUIRED WHEN FTP_SSL IS ENABLED **

##### FTP_KEYSTORE_PASSWORD
Default: **None**  
This value represents the password that belongs to the keystore.
** REQUIRED WHEN FTP_SSL IS ENABLED **

##### FTP_IDLE_TIME
Default: `3600`  
How many seconds is StackFTP supposed to idle? By default we wait for 3600 seconds, equal to one hour.

## Building StackFTP
You can get started using StackFTP rather easily. Just execute the following command to build this piece of software using Maven:
```
mvn clean install
```
Maven will then generate a `.war` file, which you can execute using Java.
