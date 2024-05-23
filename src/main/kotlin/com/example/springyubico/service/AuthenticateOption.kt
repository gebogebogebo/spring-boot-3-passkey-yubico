package com.example.springyubico.service

import com.yubico.webauthn.AssertionRequest

class AuthenticateOption(
    val assertionRequest: AssertionRequest
)
