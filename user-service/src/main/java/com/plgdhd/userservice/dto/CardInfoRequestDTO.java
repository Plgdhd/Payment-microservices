    package com.plgdhd.userservice.dto;

    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Size;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class CardInfoRequestDTO
    {
        @NotBlank(message = "Card number is required")
        @Size(min = 8, max = 32, message = "Card number must be from 8 to 32 symbols")
        private  String number;

        @NotBlank(message = "Holder of card is required")
        private String holder;

        @NotNull(message = "Users id is required")
        private Long userId;
    }
