package dev.ioza.kingdom.bastion.domain.ports.in;

import dev.ioza.kingdom.bastion.application.postern.PosternCommand;
import dev.ioza.kingdom.bastion.application.postern.PosternResult;
import reactor.core.publisher.Mono;

public interface PosternUseCase {

    Mono<PosternResult> postern(PosternCommand command);

}
