# PLCcom.Console

`PLCcom.Console` is the shared Swing console used by the small Java workshops. It is intentionally a separate Maven project so the workshop source files can focus on Modbus communication instead of duplicating console window code.

The console redirects standard output into a simple window, provides an enter prompt for workshop pauses and keeps output readable while master and slave examples are running side by side.

This helper is part of the MIT-licensed example repository. It is not required by the PLCcom Modbus SDK itself.
