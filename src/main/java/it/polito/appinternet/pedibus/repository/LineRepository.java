package it.polito.appinternet.pedibus.repository;
import it.polito.appinternet.pedibus.model.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {
    List<Line> findAll();
}
