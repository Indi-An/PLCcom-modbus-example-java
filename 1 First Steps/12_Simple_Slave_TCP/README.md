# Workshop 12 - Simple Slave TCP

This workshop hosts a small local Modbus TCP slave. It provides predictable data for Workshop 11 and shows the server side of the communication.

## What You Learn

- How a `ModbusSlave` is created and started.
- How initial values are placed into the data store.
- How a TCP listener is attached to the slave.
- Why a slave must shut down its listener cleanly at the end of a test.

## Run

~~~powershell
mvn -pl "1 First Steps/12_Simple_Slave_TCP" -am exec:java -Dexec.mainClass=_12_Simple_Slave_TCP
~~~

Leave this workshop running, then start Workshop 11 in another terminal or IDE run configuration.

## What To Inspect

The important part is the lifecycle: create the slave, prepare data, start the listener, wait for a client and stop the listener again. In production code this lifecycle usually belongs to a service or application component, not to a button click or static initializer.

If port `502` is already used or requires elevated permissions on your system, change the port in both Workshop 12 and Workshop 11.

## License

This example source is released under the MIT License. The PLCcom Modbus SDK itself requires a valid PLCcom license.
