package dk.kea.mulpenbackend.service;


import dk.kea.mulpenbackend.config.SecurityConfiguration;
import dk.kea.mulpenbackend.model.UserModel;
import dk.kea.mulpenbackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class UserService implements IUserService {

  private UserRepository userRepository;

  @Override
  public Set<UserModel> findAll() {
    Set<UserModel> set = new HashSet<>();
    userRepository.findAll().forEach(set::add);
    return set;
  }

  @Override
  public UserModel save(UserModel user) {
//        if(user.getPassword() == null) {
    PasswordEncoder pw = SecurityConfiguration.passwordEncoder();
    user.setPassword(pw.encode(user.getPassword()));
//        }
    return userRepository.save(user);
  }

  @Override
  public void delete(UserModel object) {
    userRepository.delete(object);
  }

  @Override
  public void deleteById(Long aLong) {

  }

  @Override
  public Optional<UserModel> findById(Long aLong) {
    return userRepository.findById(aLong);
  }

  @Override
  public List<UserModel> findByName(String name) {
    System.out.println("Userservice called findByName with argument: " + name);
    return userRepository.findByUsername(name);
  }
}
