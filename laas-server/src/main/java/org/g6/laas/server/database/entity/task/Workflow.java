package org.g6.laas.server.database.entity.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.g6.laas.server.database.entity.LaaSAuditable;
import org.g6.laas.server.database.entity.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WORKFLOW",uniqueConstraints = @UniqueConstraint(columnNames = {"NAME"}))
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Workflow extends LaaSAuditable<User> {
    private static final long serialVersionUID = 3937898097604595839L;

    @Column(name="NAME")
    private String name;

    private String description;

    @ManyToOne
    private Scenario scenario;

    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "workflow")
    private List<WorkflowTask> tasks = new ArrayList<>();
}
