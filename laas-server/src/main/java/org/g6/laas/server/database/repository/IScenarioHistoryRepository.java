package org.g6.laas.server.database.repository;

import org.g6.laas.server.database.entity.task.ScenarioHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IScenarioHistoryRepository extends JpaRepository<ScenarioHistory, Long> {
}
