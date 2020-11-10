package com.handcontrol.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Test {

    @GetMapping("/test")
    fun test() : String {
        println("TESTTTTT")
        return "hey ritt"
    }

}