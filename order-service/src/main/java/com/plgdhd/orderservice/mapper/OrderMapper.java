package com.plgdhd.orderservice.mapper;

import com.plgdhd.orderservice.model.dto.*;
import com.plgdhd.orderservice.model.Order;
import com.plgdhd.orderservice.model.OrderItem;
import com.plgdhd.orderservice.model.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    // DTO → Entity
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    Order toEntity(OrderCreateDTO dto);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", ignore = true)
    OrderItem toOrderItemEntity(OrderItemCreateDTO dto);

    // ЯВНО УКАЗЫВАЕМ: id заказа → dto.id
    @Mapping(target = "id", source = "order.id")  // ← ВОТ ТУТ!
    @Mapping(target = "status", source = "order.status")
    @Mapping(target = "creationDate", source = "order.creationDate")
    @Mapping(target = "user", ignore = true)  // заполним вручную
    OrderResponseDTO toResponseDTO(Order order, UserInfoDTO userInfo);

    // Заполняем user вручную
    @AfterMapping
    default void fillUserInfo(@MappingTarget OrderResponseDTO dto, UserInfoDTO userInfo) {
        dto.setUser(userInfo);
    }

    // OrderItem → DTO
    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "item.name", target = "itemName")
    @Mapping(source = "item.price", target = "itemPrice")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);
}
