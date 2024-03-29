package com.example.ticketnow.service;

import com.example.ticketnow.bo.TikcetBO;
import com.example.ticketnow.model.Category;
import com.example.ticketnow.model.Ticket;
import com.example.ticketnow.repository.TicketRepository;
import com.example.ticketnow.util.TicketStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    private MongoTemplate mongoTemplate;


    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Ticket> fetchAllTicketsUsingUserId(String userId) {
        List<String> idOfUser;
        Query query;
        Query query2;
        Query query3;
        List<Ticket> tickets;
        List<Category> categories;
        query = new Query();
        query2 = new Query();
        idOfUser = new ArrayList<String>(2);
        idOfUser.add(userId);
        query.addCriteria(Criteria.where("IDof_createdBy").in(userId));
        // userid is username
        query3= new Query();
        query3.addCriteria(Criteria.where("userId").in(userId));
        String username="";
        categories= mongoTemplate.find(query3, Category.class);
               if (categories.size()!=0){
                   username=categories.get(0).getUsername();
               }
        query2.addCriteria(Criteria.where("IDof_assignedTo").in(username));
        // IDof_assignedTo
        tickets = mongoTemplate.find(query, Ticket.class);
        tickets.addAll(mongoTemplate.find(query2, Ticket.class));

        return tickets;
    }

    @Override
    public Ticket addATicket(TikcetBO tikcetBO) {
        Ticket ticket;

        ticket = new Ticket();

        BeanUtils.copyProperties(tikcetBO, ticket,"ticketId");

        ticket.setCreationTime(LocalDateTime.now());
        ticket.setCreationDate(LocalDate.now());

        ticket.setStatus(String.valueOf(TicketStatus.NEW));

        return ticketRepository.save(ticket);

    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public boolean deleteTicket(String ticketId) {
        Ticket ticket;
        boolean ticketDeleted = false;
        ticket = new Ticket();
        ticket.setTicketId(ticketId);
        try {
            if (ticketRepository.findByTicketId(ticketId) != null) {
                ticketRepository.delete(ticket);
                ticketDeleted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ticketDeleted;
    }

    @Override
    public Ticket editTicket(TikcetBO ticketBO) {
        Ticket ticket;

        ticket=ticketRepository.findByTicketId(ticketBO.getTicketId());

        BeanUtils.copyProperties(ticketBO,ticket,"creationDate","creationTime","IDof_createdBy","idOfComment");

        return ticketRepository.save(ticket);
    }
}
