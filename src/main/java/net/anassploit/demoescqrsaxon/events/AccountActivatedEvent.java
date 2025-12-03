package net.anassploit.demoescqrsaxon.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.anassploit.demoescqrsaxon.enums.AccountStatus;

@Getter @AllArgsConstructor
public class AccountActivatedEvent {
    private String accountId;
    private AccountStatus status;
}
