# Workshop 21 - SecureTCP Master

This workshop is the SecureTCP master counterpart to Workshop 22. It connects to a SecureTCP slave, performs a simple Modbus request and shows where certificate validation can be influenced.

## What You Learn

- How a master is configured for SecureTCP instead of normal TCP.
- Where the master-side PKI store is located.
- How local certificates are created or loaded for a test endpoint.
- How a certificate validator changes the trust decision.

## Before You Run It

Start Workshop 22 first. It listens on SecureTCP port `802`.

## Run

~~~powershell
mvn -pl "2 Secure Modbus TCP/21_SecureTCP_Master" -am exec:java -Dexec.mainClass=_21_SecureTCP_Master
~~~

## What To Inspect

The SecureTCP options are the center of this example. They define the PKI path and the validator hook. When a custom validator is present, the PKI store is no longer the only trust decision. That is useful for teaching and for special integration policies, but production code should be deliberate and restrictive.

## License

This example source is released under the MIT License. The PLCcom Modbus SDK itself requires a valid PLCcom license.
