package com.utn.tpfinal.dtos;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        Map<String, String> validations
) {


}
