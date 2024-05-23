package com.example.springyubico.service

class AttestationVerifyResult(
    val credentialId: ByteArray,
    val signCount: Long,
    val credentialPublicKey: ByteArray,
)
