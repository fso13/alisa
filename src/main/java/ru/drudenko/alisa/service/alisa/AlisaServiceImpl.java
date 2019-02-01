package ru.drudenko.alisa.service.alisa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drudenko.alisa.dto.dialog.req.Command;

import java.util.List;

@Service
public class AlisaServiceImpl implements AlisaService {

    private final List<CommandService> commandServices;

    @Autowired
    public AlisaServiceImpl(final List<CommandService> commandServices) {
        this.commandServices = commandServices;
    }

    @Override
    public String getText(Command command) {
        return commandServices.stream().filter(service -> service.doFilter(command)).findFirst().orElse(new CommandService() {
            @Override
            public boolean doFilter(final Command command) {
                return false;
            }

            @Override
            public String getMessage(final Command command) {
                return "Что то не понятное.";
            }
        }).getMessage(command);
    }
}
