# Workshop 91 - Full Master Sample

This is the full Java Swing master sample. It is intended for manual validation and troubleshooting, not just for reading a few lines of API usage.

## What You Can Test

- Normal TCP and SecureTCP connector settings.
- Read and write operations for different Modbus areas.
- Register modes and byte order settings.
- Log output and connection state changes.
- Certificate review through the SecureTCP certificate manager.

## Run

~~~powershell
mvn -pl "9 Full Applications/91_Full_Master_Sample" -am exec:java -Dexec.mainClass=example_app.Master_Example
~~~

## Practical Use

Start the matching slave sample or connect to a known test device. Begin with a harmless read request. Only use write operations when you know exactly which address is affected and what the device will do with the value.

For SecureTCP tests, expect certificate trust to be an explicit step. Unknown certificates should be reviewed and trusted deliberately, not silently accepted in a production setup.

## License

This example source is released under the MIT License. The PLCcom Modbus SDK itself requires a valid PLCcom license.
