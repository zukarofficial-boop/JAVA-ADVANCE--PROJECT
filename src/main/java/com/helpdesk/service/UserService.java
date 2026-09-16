package com.helpdesk.service;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.helpdesk.exception.DuplicateUserException;
import com.helpdesk.exception.UserNotFoundException;
import com.helpdesk.model.Customer;
import com.helpdesk.model.User;
import com.helpdesk.repository.InMemoryDatabase;
/**
 * Service managing User operations (Customers).
 */
public class UserService {
    private final InMemoryDatabase db;

    public UserService() {
        this.db = InMemoryDatabase.getInstance();
    }

    public Customer registerCustomer(String name, String email, String department, String phoneNumber) {
        if (db.getEmailSet().contains(email.toLowerCase())) {
            throw new DuplicateUserException("A user with email '" + email + "' already exists!");
        }

        int id = db.nextUserId();
        Customer customer = new Customer(id, name, email, department, phoneNumber);
        db.getUsers().put(id, customer);
        db.getEmailSet().add(email.toLowerCase());
        return customer;
    }

    public User getUserById(int id) {
        User user = db.getUsers().get(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        return user;
    }

    public Customer getCustomerById(int id) {
        User user = getUserById(id);
        if (!(user instanceof Customer customer)) {
            throw new UserNotFoundException("User with ID " + id + " is not a Customer (Role: " + user.getRole() + ")");
        }
        return customer;
    }

    public Collection<User> getAllUsers() {
        return db.getUsers().values();
    }

    public List<Customer> getAllCustomers() {
        return db.getUsers().values().stream()
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .collect(Collectors.toList());
    }
}
