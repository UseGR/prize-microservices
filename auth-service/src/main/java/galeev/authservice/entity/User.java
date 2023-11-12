package galeev.authservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements Persistable<Long> {
    @Id
    private Long id;

    private String firstname;

    private String username;

    private String fullname;

    private String phoneNumber;

    private String dateOfBirth;

    private boolean isParticipated;

    private Sex sex;

    private KnowFrom knowFrom;

    private boolean isBlocked;

    private boolean isAdmin;

    @Override
    public boolean isNew() {
        return id != null;
    }

    public enum Sex {
        MALE, FEMALE
    }

    public enum KnowFrom {
        INSTAGRAM, TELEGRAM, WHATSAPP
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isParticipated == user.isParticipated && isBlocked == user.isBlocked && isAdmin == user.isAdmin && Objects.equals(firstname, user.firstname) && Objects.equals(username, user.username) && Objects.equals(fullname, user.fullname) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(dateOfBirth, user.dateOfBirth) && sex == user.sex && knowFrom == user.knowFrom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, username, fullname, phoneNumber, dateOfBirth, isParticipated, sex, knowFrom, isBlocked, isAdmin);
    }
}
