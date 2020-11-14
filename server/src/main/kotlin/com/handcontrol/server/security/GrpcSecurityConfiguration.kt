package com.handcontrol.server.security

import org.lognet.springboot.grpc.security.EnableGrpcSecurity
import org.lognet.springboot.grpc.security.GrpcSecurityConfigurerAdapter

@EnableGrpcSecurity
class GrpcSecurityConfiguration : GrpcSecurityConfigurerAdapter() {
    //one day there will be implemented security config
}