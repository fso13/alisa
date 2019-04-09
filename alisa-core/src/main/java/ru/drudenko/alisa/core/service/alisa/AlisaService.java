package ru.drudenko.alisa.core.service.alisa;

import org.springframework.transaction.annotation.Transactional;
import ru.drudenko.alisa.api.dialog.dto.req.Command;

@Transactional
public interface AlisaService {
    String getText(Command command);
}
