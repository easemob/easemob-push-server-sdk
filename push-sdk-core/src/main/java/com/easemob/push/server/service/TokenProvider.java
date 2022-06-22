package com.easemob.push.server.service;

import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<String> getToken();
}
