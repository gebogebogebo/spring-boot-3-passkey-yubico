package com.example.springyubico.controller

import com.example.springyubico.service.Status

open class ServerResponse(
    var status: Status,
    var errorMessage: String,
)
