package ru.practicum.ewm.mainservice.event.mapper;

import ru.practicum.ewm.mainservice.event.dto.LocationDto;
import ru.practicum.ewm.mainservice.event.model.Location;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }

    public static Location toLocation(LocationDto locationDto) {
        return new Location(
                locationDto.getLat(),
                locationDto.getLon()
        );
    }
}
