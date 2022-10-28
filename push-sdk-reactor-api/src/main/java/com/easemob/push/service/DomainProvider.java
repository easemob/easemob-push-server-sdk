package com.easemob.push.service;

import com.easemob.common.model.Endpoint;
import reactor.core.publisher.Mono;

public interface DomainProvider {
    Mono<Endpoint> getEndpoint();
}
