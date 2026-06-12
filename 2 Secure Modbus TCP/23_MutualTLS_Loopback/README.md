# Workshop 23 - Mutual TLS Loopback

This workshop runs master and slave in one process. It is the compact lab for mutual TLS because both sides can be inspected without coordinating two applications.

## What You Learn

- How a master certificate and a slave certificate participate in one connection.
- How both endpoints can have their own PKI store.
- Which parts of the handshake belong to transport security and which parts still belong to Modbus.
- Why permissive validators are useful for understanding the flow but not suitable as a production policy.

## Run

~~~powershell
mvn -pl "2 Secure Modbus TCP/23_MutualTLS_Loopback" -am exec:java -Dexec.mainClass=_23_MutualTLS_Loopback
~~~

## What To Inspect

Use this workshop before testing two separate SecureTCP applications. It removes timing and startup-order noise and keeps the certificate story in one source file.

The local PKI material is generated under the project folder at runtime and ignored by Git.

## License

This example source is released under the MIT License. The PLCcom Modbus SDK itself requires a valid PLCcom license.
