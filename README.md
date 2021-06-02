# Pastro

![Tests](https://github.com/pavponn/pastro/workflows/GitHub%20CI/badge.svg)
[![MIT license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/pavponn/pastro/blob/master/LICENSE)

This is a proof-of-concept implementation of the Pastro protocol presented in Permissionless and Asynchronous Asset Transfer paper, which is available on [arXiv](https://arxiv.org/abs/2105.04966).  

## Abstract
Most modern asset transfer systems use *consensus* to maintain a totally ordered chain of transactions. 
It was recently shown that consensus is not always necessary for implementing asset transfer. 
More efficient, *asynchronous* solutions can be built using *reliable broadcast* instead of consensus. 
This approach has been originally used in the closed (permissioned) setting. 
In the paper, we extend it to the open (*permissionless*) environment.
We present **Pastro**, a permissionless and asynchronous asset-transfer implementation, in which *quorum systems*, traditionally used in reliable broadcast, are replaced with a weighted *Proof-of-Stake* mechanism. 
Pastro tolerates a *dynamic* adversary that is able to adaptively corrupt participants based on the assets owned by them into account.

## Scenarios

In the  `CorrectScenario.kt`, we check whether if all issued transactions are valid and non-conflicting, then they all eventually become confirmed.

In the `DoubleSpendingScenario.kt` the transactions that are issued contain invalid and conflicting ones. We assert that all valid and non-conflicting transactions are eventually confirmed. Besides, we check that no two transactions `tx` and `tx'` that conflict were confirmed. Similiarly to `CorrectScenario.kt` we verify that all confirmed trasactions are valid. 

## Tests

Implementations of Transaction Validation and Adjustable Byzantine Lattice Agreement are covered with unit tests.
Main primitives like Transaction, Configuration and History are tested as well.

All unit tests are placed [here](https://github.com/pavponn/pastro/tree/master/src/test/kotlin/com/github/pavponn).
