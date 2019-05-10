package it.polito.appinternet.pedibus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import it.polito.appinternet.pedibus.model.Role;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Indexed;

import java.util.Set;

@Document(collection = "users")
@NoArgsConstructor
@Getter @Setter
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private String name;
    private boolean enabled;
    private Set<Role> roles;
}
