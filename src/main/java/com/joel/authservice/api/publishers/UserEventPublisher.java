package com.joel.authservice.api.publishers;

import com.joel.authservice.domain.dtos.request.UserEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value(value = "${learn.broker.exchange}")
    private String exchangeUserEvent;

    public void publisherEvent(UserEventDTO userEventDTO) {
        rabbitTemplate.convertAndSend(exchangeUserEvent, "", userEventDTO);
        log.info("Send UserId ------>>> {} ", userEventDTO.getUserId());

    }
}
