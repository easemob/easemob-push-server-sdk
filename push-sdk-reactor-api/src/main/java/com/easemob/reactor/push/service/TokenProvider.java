package com.easemob.reactor.push.service;

import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<String> getToken();
}
