package ru.drudenko.alisa.service.alisa;

import ru.drudenko.alisa.dto.dialog.req.Command;

public interface CommandService {
    boolean doFilter(Command command);

    String getMessage(Command command);
}
