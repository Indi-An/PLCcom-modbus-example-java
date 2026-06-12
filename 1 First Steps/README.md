# 1 First Steps - Java

This chapter is the clean starting point. One slave listens on normal Modbus TCP, one master connects to it, and the code shows the complete read/write path without TLS, PKI or UI complexity.

## Workshops

| # | Project | What to look for |
|---|---------|------------------|
| 11 | `11_Simple_Master_TCP` | License setup, connector setup, write request, read request, quality check and diagnostic output. |
| 12 | `12_Simple_Slave_TCP` | Slave startup, data store initialization, listener lifecycle, incoming requests and clean shutdown. |

## Run Order

1. Start `12_Simple_Slave_TCP`.
2. Start `11_Simple_Master_TCP`.
3. Watch both consoles. The master shows the application view, the slave shows what arrived.

When this pair is clear, SecureTCP becomes easier because only the transport changes. The Modbus request idea stays the same.
