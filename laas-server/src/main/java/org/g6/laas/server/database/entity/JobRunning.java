package org.g6.laas.server.database.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.g6.laas.server.database.entity.task.TaskRunning;
import org.g6.laas.server.database.entity.user.User;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class JobRunning extends LaaSNotifiable<User> {
    private static final long serialVersionUID = -5742125355431226460L;
    private Collection<User> users;
    private String summary;

    @ManyToOne
    private Job job;

    private String status;

    @OneToMany(cascade={CascadeType.PERSIST}, mappedBy = "jobRunning")
    private Collection<TaskRunning> taskRunnings = new ArrayList<>();

    @Override
    public Collection<User> sendTo() {
        return users;
    }

    @Override
    public String getSummary() {
        return summary;
    }
}
