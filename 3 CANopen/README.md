# 3 CANopen - Java

This chapter demonstrates CANopen-style object access through Modbus communication. Some devices expose data through an index/subindex model while the physical transport is still Modbus. The example shows how that mental model can be mapped into readable application code.

## Workshops

| # | Project | What it demonstrates |
|---|---------|----------------------|
| 31 | `31_CANopen_Master` | Read and write access to CANopen-style object entries over Modbus. |

## What Matters

The important questions are the same as in any device integration: which index and subindex identify the value, which datatype is expected, how many registers are involved, which byte order is documented and whether the device allows writes in its current state.

Keep the device manual close while reading the source. The example shows the structure; the real values always come from the device documentation.
