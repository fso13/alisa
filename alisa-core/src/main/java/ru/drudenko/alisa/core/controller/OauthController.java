package ru.drudenko.alisa.core.controller;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Transactional
public interface OauthController {
    @GET
    @Path("/oauth/{agentId}")
    @Produces("text/html;charset=UTF-8")
    Response oauth(@PathParam("agentId") String agentId,
                   @QueryParam("state") String state,
                   @QueryParam("code") String code) throws Exception;
}
