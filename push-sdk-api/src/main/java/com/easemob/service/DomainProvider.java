package com.easemob.service;

import com.easemob.model.Endpoint;
import reactor.core.publisher.Mono;

public interface DomainProvider {
    Mono<Endpoint> getEndpoint();
}
