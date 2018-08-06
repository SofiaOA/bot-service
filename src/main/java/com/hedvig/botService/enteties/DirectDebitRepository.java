package com.hedvig.botService.enteties;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DirectDebitRepository extends CrudRepository<DirectDebitMandateTrigger, UUID> {

  // DirectDebitMandateTrigger findOne(DirectDebitMandateTrigger directDebitMandate);
}
