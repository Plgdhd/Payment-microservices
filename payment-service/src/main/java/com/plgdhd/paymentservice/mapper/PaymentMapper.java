package com.plgdhd.paymentservice.mapper;

import com.plgdhd.paymentservice.event.OrderCreatedEvent;
import com.plgdhd.paymentservice.model.Payment;
import com.plgdhd.paymentservice.model.dto.PaymentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toEntity(PaymentDto dto);
    PaymentDto toDto(Payment entity);
    PaymentDto toDto(OrderCreatedEvent event);

}
