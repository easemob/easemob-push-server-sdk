package com.easemob.reactor.push.service;

import com.easemob.common.model.Endpoint;
import reactor.core.publisher.Mono;

public interface DomainProvider {
    Mono<Endpoint> getEndpoint();
}
