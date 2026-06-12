# Workshop 22 - SecureTCP Slave

This workshop hosts a SecureTCP slave. It is used by Workshop 21 and demonstrates the server side of TLS protected Modbus communication.

## What You Learn

- How a SecureTCP listener is attached to a `ModbusSlave`.
- How the slave prepares its local certificate and private key.
- How incoming client certificates are validated for a lab scenario.
- Why the listener must be stopped cleanly when the workshop exits.

## Run

~~~powershell
mvn -pl "2 Secure Modbus TCP/22_SecureTCP_Slave" -am exec:java -Dexec.mainClass=_22_SecureTCP_Slave
~~~

Leave this workshop running, then start Workshop 21.

## What To Inspect

Pay attention to the comments around the PKI folders. `own` is the local endpoint identity, `trusted` contains remote certificates that are accepted, `issuer` contains chain certificates and `rejected` is where unknown certificates can be placed for review.

The generated `pki` folder is ignored by Git. It belongs to the local test machine and should not be committed.

## License

This example source is released under the MIT License. The PLCcom Modbus SDK itself requires a valid PLCcom license.
