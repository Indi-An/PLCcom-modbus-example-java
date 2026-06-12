# 2 Secure Modbus TCP - Java

This chapter takes the same Modbus idea and puts a secure TLS transport underneath it. The goal is not to hide security behind one property. The goal is to make the certificate flow visible: where the local certificate lives, why a remote certificate may be rejected, how trust is established and where a project-specific validator can make the final decision.

## Workshops

| # | Project | What it demonstrates |
|---|---------|----------------------|
| 21 | `21_SecureTCP_Master` | Master-side SecureTCP options, PKI path, TLS connection and validation hook. |
| 22 | `22_SecureTCP_Slave` | SecureTCP listener setup, endpoint certificate handling and local PKI store creation. |
| 23 | `23_MutualTLS_Loopback` | Master and slave in one process with mutual certificate authentication. |
| 24 | `24_RoleBasedAuthorization` | A slave-side authorization callback that receives request context. |

## The PKI Story

On first access, PLCcom creates the store structure. Unknown remote certificates go to `rejected`. Certificates that the application should trust belong in `trusted`. CA or issuer certificates belong in `issuer`. Local endpoint certificates live in `own`, with private keys in `own/private`.

If no custom certificate validator is assigned, PLCcom validates against this store. If a custom validator is assigned, the decision belongs to that validator. The workshops make this visible so integrators can decide what their production policy should look like.

The permissive validators in the samples are intentionally permissive. They belong in a workshop because they show the control flow. Production code should replace them with a real trust decision.
