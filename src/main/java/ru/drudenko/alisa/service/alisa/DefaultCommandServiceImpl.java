package ru.drudenko.alisa.service.alisa;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.drudenko.alisa.dto.dialog.req.Command;

@Service
@Order(Integer.MIN_VALUE)
public class DefaultCommandServiceImpl implements CommandService {
    @Override
    public boolean doFilter(final Command command) {
        return true;
    }

    @Override
    public String getMessage(final Command command) {
        return "Что то не понятное.";
    }
}
