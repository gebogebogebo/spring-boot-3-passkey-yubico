package com.example.springyubico.service.yubico

import com.example.springyubico.repository.MfidoCredentialForYubico
import com.example.springyubico.repository.MfidoCredentialRepository
import com.example.springyubico.repository.MuserRepository
import com.example.springyubico.service.AttestationVerifyResult
import com.example.springyubico.service.FidoCredentialService
import org.springframework.stereotype.Service


@Service
class YubicoCredentialServiceImpl(
    private val mUserRepository: MuserRepository,
    private val mFidoCredentialRepository: MfidoCredentialRepository,
) : FidoCredentialService {
    override fun save(userId: String, attestationVerifyResult: AttestationVerifyResult) {
        val mUser = mUserRepository.findByUserId(userId) ?: throw RuntimeException("User not found")

        val entity = MfidoCredentialForYubico(
            0,
            mUser.internalId,
            attestationVerifyResult.credentialId,
            attestationVerifyResult.signCount,
            attestationVerifyResult.credentialPublicKey
        )
        mFidoCredentialRepository.save(entity)
    }

    override fun load(userInternalId: String, credentialId: ByteArray):  MfidoCredentialForYubico? {
        val credential = mFidoCredentialRepository.findByUserInternalId(userInternalId)
            .find { it.credentialId.contentEquals(credentialId) }
        return credential
    }
}
