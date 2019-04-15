package ru.drudenko.alisa.core.controller.impl;

import ru.drudenko.alisa.core.controller.MainController;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MainControllerImpl implements MainController {
    public Response index() {
        return Response.ok(this.getClass().getClassLoader().getResourceAsStream("templates/index.html"),
                MediaType.TEXT_HTML_TYPE)
                .build();
    }
}
