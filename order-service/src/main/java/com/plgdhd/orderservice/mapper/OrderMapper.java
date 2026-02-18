package com.plgdhd.orderservice.mapper;

import com.plgdhd.orderservice.model.dto.*;
import com.plgdhd.orderservice.model.Order;
import com.plgdhd.orderservice.model.OrderItem;
import com.plgdhd.orderservice.model.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {


    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    Order toEntity(OrderCreateDTO dto);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", ignore = true)
    OrderItem toOrderItemEntity(OrderItemCreateDTO dto);

    @Mapping(target = "id", source = "order.id")
    @Mapping(target = "status", source = "order.status")
    @Mapping(target = "creationDate", source = "order.creationDate")
    @Mapping(target = "userInfo", ignore = true)
    OrderResponseDTO toResponseDTO(Order order);


    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "item.name", target = "itemName")
    @Mapping(source = "item.price", target = "itemPrice")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);
}
