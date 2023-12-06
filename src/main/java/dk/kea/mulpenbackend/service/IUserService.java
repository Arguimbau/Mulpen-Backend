package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.model.UserModel;

import java.util.List;

public interface IUserService extends ICrudService<UserModel, Long> {
  List<UserModel> findByName(String name);
}
