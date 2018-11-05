package com.hedvig.botService.enteties;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectDebitRepository extends CrudRepository<DirectDebitMandateTrigger, UUID> {

  // DirectDebitMandateTrigger findOne(DirectDebitMandateTrigger directDebitMandate);
}
