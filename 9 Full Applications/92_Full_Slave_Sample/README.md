# Workshop 92 - Full Slave Sample

This is the full Java Swing slave sample. It provides a manual test slave with normal TCP and SecureTCP listener support, a visible data store and certificate management tools.

## What You Can Test

- Starting and stopping slave listeners.
- Serving values from the local data store.
- Updating values for coil, input and register areas.
- Running normal TCP and SecureTCP side by side.
- Reviewing own, trusted, issuer and rejected certificates.

## Run

~~~powershell
mvn -pl "9 Full Applications/92_Full_Slave_Sample" -am exec:java -Dexec.mainClass=example_app.SlaveExample
~~~

## Practical Use

Use this application as the counterpart for the full master sample or for external Modbus clients. The log area is especially useful when you need to see whether a request reaches the slave, which listener received it and which response was sent back.

For SecureTCP, the local PKI store is runtime material and is ignored by Git. Treat generated private keys as local test data.

## License

This example source is released under the MIT License. The PLCcom Modbus SDK itself requires a valid PLCcom license.
