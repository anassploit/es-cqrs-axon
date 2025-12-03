package net.anassploit.demoescqrsaxon.commands.aggregates;

import net.anassploit.demoescqrsaxon.commands.commands.AddAccountCommand;
import net.anassploit.demoescqrsaxon.enums.AccountStatus;
import net.anassploit.demoescqrsaxon.events.AccountCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class AccountAggregates {
    private String accountId;
    private double balance;
    private AccountStatus status;


    public AccountAggregates() {}

    @CommandHandler
    public AccountAggregates(AddAccountCommand command) {
        if(command.getInitialBalance() < 0) throw new IllegalArgumentException("Initial balance cannot be negative");
        AggregateLifecycle.apply(new AccountCreatedEvent(command.getId(),
                command.getInitialBalance(),
                AccountStatus.CREATED,
                command.getCurrency()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getAccountId();
        this.balance = event.getInitialBalance();
        this.status = event.getStatus();
    }


}
