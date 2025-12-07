package com.plgdhd.paymentservice.mapper;

import com.plgdhd.paymentservice.model.Payment;
import com.plgdhd.paymentservice.dto.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toEntity(PaymentDto dto);
    PaymentDto toDto(Payment entity);
}
