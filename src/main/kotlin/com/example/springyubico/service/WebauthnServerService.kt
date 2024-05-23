package com.example.springyubico.service

interface WebauthnServerService {
    fun getRegisterOption(
        userId: String,
    ): RegisterOption

    fun verifyRegisterAttestation(
        registerOption: RegisterOption,
        publicKeyCredentialCreateResultJson: String,
    ): AttestationVerifyResult

    fun getAuthenticateOption(): AuthenticateOption

    fun verifyAuthenticateAssertion(
        authenticateOption: AuthenticateOption,
        publicKeyCredentialGetResultJson: String,
    ): AssertionVerifyResult

}
