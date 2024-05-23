package com.example.springyubico.service

import com.fasterxml.jackson.annotation.JsonValue

enum class Status(@JsonValue val value: String) {
    OK("ok"),
    FAILED("failed"),
}
