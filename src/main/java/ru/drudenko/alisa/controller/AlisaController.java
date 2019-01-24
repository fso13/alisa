package ru.drudenko.alisa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.drudenko.alisa.dto.dialog.req.Command;
import ru.drudenko.alisa.dto.dialog.res.Render;
import ru.drudenko.alisa.service.AlisaService;

@RestController
@RequestMapping(value = "/alisa/command", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlisaController {

    private final AlisaService alisaService;

    @Autowired
    public AlisaController(AlisaService alisaService) {
        this.alisaService = alisaService;
    }

    @PostMapping
    public Render command(@RequestBody Command command) {
        System.out.println(command);

        String text = alisaService.getText(command);

        Render render = new Render();
        render.setSession(command.getSession());
        render.getResponse().setText(text);
        return render;
    }

}
