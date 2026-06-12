# Workshop 11 - Simple Master TCP

This workshop is the first master-side example. It connects to the local slave from Workshop 12, writes a few holding registers, reads values back and prints the result state.

## What You Learn

- How a `ModbusMaster` is created for normal TCP communication.
- Which connection parameters belong to the connector.
- How write and read requests are executed in a small, visible sequence.
- Why result quality and diagnostic output should be checked instead of assuming success.

## Before You Run It

Start Workshop 12 first. The master expects a slave on `127.0.0.1:502` with slave id `1`.

## Run

~~~powershell
mvn -pl "1 First Steps/11_Simple_Master_TCP" -am exec:java -Dexec.mainClass=_11_Simple_Master_TCP
~~~

## What To Inspect

Look at the source from top to bottom. The example is intentionally linear: license data, connector setup, connection, write request, read request and cleanup. That order is close to the shape of a real first integration test.

If the result quality is not good, check the slave first. Most first failures are caused by the slave not running, the wrong port, a blocked port or a different slave id.

## License

This example source is released under the MIT License. The PLCcom Modbus SDK itself requires a valid PLCcom license.
