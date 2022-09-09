package com.easemob.service;

import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<String> getToken();
}
