package ru.drudenko.alisa.core.controller;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Transactional
public interface MainController {
    @GET
    @Path("/index")
    @Produces(MediaType.TEXT_HTML)
    Response index();
}
