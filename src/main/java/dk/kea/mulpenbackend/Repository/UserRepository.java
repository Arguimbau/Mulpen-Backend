package dk.kea.mulpenbackend.repository;

import dk.kea.mulpenbackend.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<UserModel,Long> {
    List<UserModel> findByUsername(String name);
    //List<User> findUserByPasswordContains(String passwordPart);

    public boolean existsByFilePath(String filepath);

}
