package ru.drudenko.alisa.api.dialog;


import ru.drudenko.alisa.api.dialog.dto.req.Command;

public interface CommandService {
    boolean doFilter(Command command);

    String getMessage(Command command);

    String getCommands();
}
