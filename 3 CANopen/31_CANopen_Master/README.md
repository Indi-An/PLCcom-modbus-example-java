# Workshop 31 - CANopen Master

This workshop shows a CANopen-style master access pattern over Modbus. It is useful for devices that document values as object dictionary entries but expose the actual communication through Modbus requests.

## What You Learn

- How object-like access can be represented in Java code.
- Why index, subindex, datatype and byte order must be treated as a single mapping decision.
- How diagnostics help distinguish communication errors from wrong data interpretation.
- Why write access should always be checked against the device state and manual.

## Run

~~~powershell
mvn -pl "3 CANopen/31_CANopen_Master" -am exec:java -Dexec.mainClass=_31_CANopen_Master
~~~

## What To Inspect

Look for the mapping values in the source. In a real integration, keep those values close to comments that reference the device manual. Future maintenance becomes much easier when the reason for an address or datatype is still visible.

## License

This example source is released under the MIT License. The PLCcom Modbus SDK itself requires a valid PLCcom license.
