package ru.practicum.ewm.mainservice.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.mainservice.advice.constant.ValidationConst;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    @Null(message = "При создании id категории должен быть null")
    private Long id;
    @NotBlank(message = "Имя категории не может быть пустым")
    @Size(max = ValidationConst.CATEGORY_NAME_MAX,
    message = "Длина имени категории должна быть до {max} символов")
    private String name;
}
