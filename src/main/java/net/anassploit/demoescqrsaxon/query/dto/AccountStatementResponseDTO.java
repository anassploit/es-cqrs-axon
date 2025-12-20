package net.anassploit.demoescqrsaxon.query.dto;

import net.anassploit.demoescqrsaxon.query.entities.Account;
import net.anassploit.demoescqrsaxon.query.entities.AccountOperation;

import java.util.List;

public record AccountStatementResponseDTO(
        Account account,
        List<AccountOperation> operations
) {
}
