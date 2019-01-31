package ru.drudenko.alisa.service.alisa;

import ru.drudenko.alisa.dto.dialog.req.Command;

public interface AlisaService {
    String getText(Command command);
}
