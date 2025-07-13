package ru.practicum.ewm.mainservice.event.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.mainservice.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.mainservice.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.mainservice.event.model.Event;

@Mapper(componentModel = "spring")
public interface EventPatchMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventFromDto(UpdateEventAdminRequest src,
                            @MappingTarget Event target);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventFromDto(UpdateEventUserRequest src,
                            @MappingTarget Event target);
//    Location toLocation(LocationDto dto);
}
