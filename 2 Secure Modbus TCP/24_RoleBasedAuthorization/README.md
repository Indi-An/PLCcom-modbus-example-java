# Workshop 24 - Role Based Authorization

This workshop demonstrates where a slave-side application can decide whether a request is allowed. TLS can prove and protect a connection, but many real systems still need an application policy for what that connection may do.

## What You Learn

- How authorization is separated from the TLS handshake.
- How request context can be evaluated before a Modbus operation is accepted.
- Why read and write operations often need different rules.
- How to keep an authorization decision visible and auditable in application code.

## Run

~~~powershell
mvn -pl "2 Secure Modbus TCP/24_RoleBasedAuthorization" -am exec:java -Dexec.mainClass=_24_RoleBasedAuthorization
~~~

## What To Inspect

The key point is not the particular demo rule. The key point is the location of the hook. In a production application, this is where you would check certificate identity, role assignment, allowed function codes, permitted address ranges and operational mode of the machine.

## License

This example source is released under the MIT License. The PLCcom Modbus SDK itself requires a valid PLCcom license.
