package galeev.prizeservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prizes")
public class Prize implements Persistable<UUID> {
    @Id
    private UUID id;

    private String prizeDescription;

    private String winnerDescription;

    private String fileId;

    private boolean isAnimation;

    private boolean isRolled;

    private Long userId;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
