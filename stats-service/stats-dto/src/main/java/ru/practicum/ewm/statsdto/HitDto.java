package ru.practicum.ewm.statsdto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    @Null(message = "При добавлении hit_id должен быть null")
    private Long id;
    @NotBlank(message = "Имя сервиса не может быть пустым ")
    private String app;
    @NotBlank(message = "uri запроса не может быть пустым")
    private String uri;
    @NotBlank(message = "ip-адрес пользователя не может быть пустым")
    private String ip;
    @NotNull(message = "Дата запроса к эндпоинту не может быть null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
