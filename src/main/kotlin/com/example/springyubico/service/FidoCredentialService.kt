package com.example.springyubico.service

import com.example.springyubico.repository.MfidoCredentialForYubico

interface FidoCredentialService {
    fun save(userId: String, attestationVerifyResult: AttestationVerifyResult)
    fun load(userInternalId: String, credentialId: ByteArray):  MfidoCredentialForYubico?
}
