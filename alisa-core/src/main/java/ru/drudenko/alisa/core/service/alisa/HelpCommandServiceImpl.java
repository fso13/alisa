package ru.drudenko.alisa.core.service.alisa;

import org.springframework.stereotype.Service;
import ru.drudenko.alisa.api.dialog.CommandService;
import ru.drudenko.alisa.api.dialog.dto.req.Command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class HelpCommandServiceImpl implements CommandService {
    private static final List<String> HEKP_1_1 = Collections.singletonList("Помощь");
    private static final List<String> HEKP_1_2 = Collections.singletonList("помощь");
    private static final List<String> HELP_2_1 = Arrays.asList("Что", "ты", "умеешь");
    private static final List<String> HELP_2_2 = Arrays.asList("что", "ты", "умеешь");

    @Override
    public boolean doFilter(final Command command) {
        List<String> tokens = command.getRequest().getNlu().getTokens();
        return tokens.containsAll(HEKP_1_1) || tokens.containsAll(HEKP_1_2) ||
                tokens.containsAll(HELP_2_1) || tokens.containsAll(HELP_2_2);
    }

    @Override
    public String getMessage(final Command command) {
        return "Я умею работать с Яндекс Паспорт и Google Почтой." +
                " Что бы сделать привязку вызовите команду \"привяжи устройство\". " +
                "Дальше следуйцте инструкции." +
                "Что бы узнать все возможные команды скажите \"Список команд\"";
    }

    @Override
    public String getCommands() {
        return "Помощь \n что ты умеешь";
    }
}
