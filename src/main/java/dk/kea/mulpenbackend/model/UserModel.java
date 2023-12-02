package dk.kea.mulpenbackend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class UserModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String name;
    private String title;
    private String email;
    private String filePath;
    private String type;

    public UserModel(String username, String password) {
        this.username=username;
        this.password=password;
    }
}
