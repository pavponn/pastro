# Pastro

![Tests](https://github.com/pavponn/pastro/workflows/GitHub%20CI/badge.svg)
[![MIT license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/pavponn/pastro/blob/master/LICENSE)

## Abstract
Most modern asset transfer systems use *consensus* to maintain a totally ordered chain of transactions. 
It was recently shown that consensus is not always necessary for implementing asset transfer. 
More efficient, *asynchronous* solutions can be built using *reliable broadcast* instead of consensus. 
This approach has been originally used in the closed (permissioned) setting. 
In the paper, we extend it to the open (*permissionless*) environment.
We present **Pastro**, a permissionless and asynchronous asset-transfer implementation, in which *quorum systems*, traditionally used in reliable broadcast, are replaced with a weighted *Proof-of-Stake* mechanism. 
Pastro tolerates a *dynamic* adversary that is able to adaptively corrupt participants based on the assets owned by them into account.
