package ru.drudenko.alisa.core.service.alisa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drudenko.alisa.api.dialog.CommandService;
import ru.drudenko.alisa.api.dialog.dto.req.Command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListCommandsCommandServiceImpl implements CommandService {
    private static final List<String> LIST_COMMANDS_1 = Arrays.asList("список", "команд");
    private static final List<String> LIST_COMMANDS_2 = Arrays.asList("Список", "команд");

    @Autowired
    private final List<CommandService> commandServices;

    public ListCommandsCommandServiceImpl(final List<CommandService> commandServices) {
        this.commandServices = commandServices;
    }


    @Override
    public boolean doFilter(final Command command) {
        List<String> tokens = command.getRequest().getNlu().getTokens();
        return tokens.containsAll(LIST_COMMANDS_1) || tokens.containsAll(LIST_COMMANDS_2);
    }

    @Override
    public String getMessage(final Command command) {
        return commandServices.stream().map(CommandService::getCommands).collect(Collectors.joining("\n"));
    }

    @Override
    public String getCommands() {
        return "Список команд";
    }
}
