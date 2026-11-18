package dev.ioza.kingdom.bastion.adapters.out.gateway;

import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import dev.ioza.kingdom.bastion.domain.ports.out.GatewayRoutesPort;

public class SpringGatewayRoutesAdapter implements GatewayRoutesPort {

    private final ApplicationEventPublisher eventPublisher;

    public SpringGatewayRoutesAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void refresh() {
        eventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }

}
