package ru.drudenko.alisa.core.controller.impl;

import ru.drudenko.alisa.api.dialog.dto.req.Command;
import ru.drudenko.alisa.api.dialog.dto.res.Render;
import ru.drudenko.alisa.core.controller.AlisaController;
import ru.drudenko.alisa.core.service.AlisaService;

public class AlisaControllerImpl implements AlisaController {

    private final AlisaService alisaService;

    public AlisaControllerImpl(AlisaService alisaService) {
        this.alisaService = alisaService;
    }

    @Override
    public Render command(Command command) {
        String text = alisaService.getText(command);
        Render render = new Render();
        render.setSession(command.getSession());
        render.getResponse().setText(text);
        return render;
    }
}
