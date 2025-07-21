package ru.practicum.ewm.mainservice.user.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.mainservice.advice.constant.ValidationConst;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @Null(message = "При создании id пользователя должен быть null")
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = ValidationConst.NAME_MIN,
            max = ValidationConst.NAME_MAX,
            message = "Имя должно быть от {min} до {max} символов")
    private String name;
    @Email(message = "Неверный формат Email")
    @NotBlank(message = "Почта не может быть пустой")
    @Size(min = ValidationConst.EMAIL_MIN,
            max = ValidationConst.EMAIL_MAX,
            message = "Длина email должна быть от {min} до {max} символов")
    @Pattern(regexp = ValidationConst.EMAIL_REGEX,
            message = "Local-part ≤ 64, каждая метка домена ≤ 63 символов")
    private String email;
}
