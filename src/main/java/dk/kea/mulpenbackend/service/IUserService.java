package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.Entity.User;

import java.util.List;

public interface IUserService extends ICrudService<User,Long>{
    List<User> findByName(String name);
}
