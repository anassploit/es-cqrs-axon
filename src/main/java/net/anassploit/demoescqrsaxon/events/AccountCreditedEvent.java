package net.anassploit.demoescqrsaxon.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.anassploit.demoescqrsaxon.enums.AccountStatus;

@Getter @AllArgsConstructor
public class AccountCreditedEvent {
    private String accountId;
    private double amount;
    private String currency;
}
