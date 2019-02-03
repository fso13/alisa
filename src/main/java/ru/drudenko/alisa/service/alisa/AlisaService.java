package ru.drudenko.alisa.service.alisa;

import org.springframework.transaction.annotation.Transactional;
import ru.drudenko.alisa.dto.dialog.req.Command;

@Transactional
public interface AlisaService {
    String getText(Command command);
}
