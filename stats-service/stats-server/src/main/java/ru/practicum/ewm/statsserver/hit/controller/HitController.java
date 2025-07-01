package ru.practicum.ewm.statsserver.hit.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.statsdto.HitDto;
import ru.practicum.ewm.statsdto.StatsDto;
import ru.practicum.ewm.statsserver.hit.service.HitService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
public class HitController {
    private final HitService hitService;

    @PostMapping("/hit")
    public HitDto create(@RequestBody @Valid HitDto hitDto) {
        return hitService.create(hitDto);
    }

    @GetMapping("/stats")
    public Collection<StatsDto> findAll(@RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            LocalDateTime start,
                                        @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        LocalDateTime end,
                                        @RequestParam(name = "uris", required = false) List<String> uris,
                                        @RequestParam(name = "unique", required = false, defaultValue = "false")
                                            boolean unique) {
        return hitService.findAll(start, end, uris, unique);
    }
}
