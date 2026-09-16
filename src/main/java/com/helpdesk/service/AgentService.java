package com.helpdesk.service;

import com.helpdesk.exception.AgentNotFoundException;
import com.helpdesk.exception.DuplicateUserException;
import com.helpdesk.model.Agent;
import com.helpdesk.repository.InMemoryDatabase;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

/**
 * Service managing Support Agents and workload analytics.
 */
public class AgentService {
    private final InMemoryDatabase db;

    public AgentService() {
        this.db = InMemoryDatabase.getInstance();
    }

    public Agent registerAgent(String name, String email, String specialization) {
        if (db.getEmailSet().contains(email.toLowerCase())) {
            throw new DuplicateUserException("An agent or user with email '" + email + "' already exists!");
        }

        int id = db.nextUserId();
        Agent agent = new Agent(id, name, email, specialization);
        db.getUsers().put(id, agent);
        db.getAgents().put(id, agent);
        db.getEmailSet().add(email.toLowerCase());
        return agent;
    }

    public Agent getAgentById(int id) {
        Agent agent = db.getAgents().get(id);
        if (agent == null) {
            throw new AgentNotFoundException("Support Agent not found with ID: " + id);
        }
        return agent;
    }

    public Collection<Agent> getAllAgents() {
        return db.getAgents().values();
    }

    public void toggleAvailability(int agentId) {
        Agent agent = getAgentById(agentId);
        agent.setAvailable(!agent.isAvailable());
    }

    /**
     * Finds the agent with the highest number of assigned tickets using Java Streams API.
     */
    public Optional<Agent> getBusiestAgent() {
        return db.getAgents().values().stream()
                .max(Comparator.comparingInt(a -> a.getAssignedTickets().size()));
    }
}
