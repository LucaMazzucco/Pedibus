package it.polito.appinternet.pedibus.repository;
import it.polito.appinternet.pedibus.model.Line;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineRepository extends MongoRepository<Line, Long> {
    List<Line> findAll();
    Line findByLineName (String line_name);
    List<Line> findByRides_Companions(String companion);
}
