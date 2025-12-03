package net.anassploit.demoescqrsaxon.commands.dto;

import net.anassploit.demoescqrsaxon.enums.AccountStatus;

public record updateAccountStatusRequestDTO(String accountId, AccountStatus accountStatus) {}
