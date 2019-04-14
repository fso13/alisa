package ru.drudenko.alisa.core.controller;

import ru.drudenko.alisa.api.dialog.dto.req.Command;
import ru.drudenko.alisa.api.dialog.dto.res.Render;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Transactional
public interface AlisaController {
    @POST
    @Path("/alisa/command")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Render command(Command command);
}
