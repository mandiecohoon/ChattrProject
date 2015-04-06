/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services;

import entities.Chattr;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Amanda Cohoon - c0628569
 */
@Path("/room")
@RequestScoped
public class ChattrREST {

    @PersistenceContext(unitName="WebApplication6PU")
    EntityManager em;

    List<Chattr> roomList;
   
    @Inject
    UserTransaction transaction;
   
    @GET
    public Response getAll() {
        JsonArrayBuilder json = Json.createArrayBuilder();
        Query q = em.createQuery("SELECT r FROM room r");
        roomList = q.getResultList();
        for (Chattr r : roomList) {
            json.add(r.toJSON());
        }
        return Response.ok(json.build().toString()).build();
    }
    
    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        Query q = em.createQuery("SELECT r FROM room r WHERE r.roomId = :roomId");
        q.setParameter("roomId", id);
        Chattr r = (Chattr) q.getSingleResult();
        String out = r.toJSON().toString();
        return Response.ok(out).build();
    }
    
    @POST
    @Consumes("application/json")
    public Response add(JsonObject json) {
        Response result;
        try {
            transaction.begin();
            Chattr r = new Chattr(json);
            em.persist(r);
            transaction.commit();
            result = Response.ok().build();
        } catch (Exception ex) {
            result = Response.status(500).build();
        }
        return result;
    }
    
    @PUT
    @Consumes("application/json")
    public Response edit(JsonObject json) {
        Response result;
        try {
            transaction.begin();
            Chattr r = (Chattr) em.createNamedQuery("Room.findByRoomId")
                    .setParameter("roomId", json.getInt("roomId"))
                    .getSingleResult();
            r.setRoomName(json.getString("roomName"));
            r.setDescription(json.getString("description"));
            em.persist(r);
            transaction.commit();
            result = Response.ok().build();
        } catch (Exception ex) {
            result = Response.status(500).entity(ex.getMessage()).build();
        }
        return result;
    }
    
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        Response result;
        try {
            transaction.begin();
            Chattr r = (Chattr) em.createNamedQuery("Room.findByRoomId")
                    .setParameter("roomId", id).getSingleResult();
            em.remove(r);
            transaction.commit();
            result = Response.ok().build();
        } catch (Exception ex) {
            result = Response.status(500).entity(ex.getMessage()).build();
        }
        return result;
    }
}
