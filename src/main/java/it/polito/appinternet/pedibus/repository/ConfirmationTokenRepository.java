package it.polito.appinternet.pedibus.repository;

import it.polito.appinternet.pedibus.model.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, String> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
}
