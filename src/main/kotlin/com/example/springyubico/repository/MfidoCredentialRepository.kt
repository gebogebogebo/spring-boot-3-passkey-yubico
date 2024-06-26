package com.example.springyubico.repository

import org.springframework.data.jpa.repository.JpaRepository

interface MfidoCredentialRepository : JpaRepository<MfidoCredentialForYubico, Int> {
    fun findByUserInternalId(userInternalId: String): List<MfidoCredentialForYubico>
    fun findByCredentialId(credentialId: ByteArray): List<MfidoCredentialForYubico>
}
