package net.anassploit.demoescqrsaxon.commands.aggregates;

import lombok.extern.slf4j.Slf4j;
import net.anassploit.demoescqrsaxon.commands.commands.AddAccountCommand;
import net.anassploit.demoescqrsaxon.commands.commands.CreditAccountCommand;
import net.anassploit.demoescqrsaxon.commands.commands.DebitAccountCommand;
import net.anassploit.demoescqrsaxon.commands.commands.UpdateAccountStatusCommand;
import net.anassploit.demoescqrsaxon.enums.AccountStatus;
import net.anassploit.demoescqrsaxon.events.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@Slf4j
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double balance;
    private AccountStatus status;


    public AccountAggregate() {}

    @CommandHandler
    public AccountAggregate(AddAccountCommand command) {
        log.info("------------------------- Account command received -------------------------");
        if(command.getInitialBalance() < 0) throw new IllegalArgumentException("Initial balance cannot be negative");
        AggregateLifecycle.apply(new AccountCreatedEvent(command.getId(),
                command.getInitialBalance(),
                AccountStatus.CREATED,
                command.getCurrency()
        ));

        AggregateLifecycle.apply(new AccountActivatedEvent(
                command.getId(),
                AccountStatus.ACTIVATED
        ));
    }

    @CommandHandler
    public void handle(CreditAccountCommand command) {
        log.info("-------------------------- Credit Account command received --------------------------");
        if(!status.equals(AccountStatus.ACTIVATED)) throw new RuntimeException("Account " + accountId + " is not activated");
        if(command.getAmount() < 0) throw new IllegalArgumentException("The amount cannot be negative");
        AggregateLifecycle.apply(new AccountCreditedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }
    @CommandHandler
    public void handle(DebitAccountCommand command) {
        log.info("-------------------------- Debit Account command received --------------------------");
        if(!status.equals(AccountStatus.ACTIVATED)) throw new RuntimeException("Account " + accountId + " is not activated");
        if(balance < command.getAmount()) throw new RuntimeException("Insufficient balance");
        if(command.getAmount() < 0) throw new IllegalArgumentException("The amount cannot be negative");
        AggregateLifecycle.apply(new AccountDebitedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }

    @CommandHandler
    public void handle(UpdateAccountStatusCommand command) {
        log.info("-------------------------- Update Account status command received --------------------------");
        if(command.getAccountStatus()==status) throw new RuntimeException("The account is already in status: " + status);
        AggregateLifecycle.apply(new AccountStatusUpdatedEvent(
                command.getId(),
                command.getAccountStatus()
        ));
    }

    @EventSourcingHandler
    public void on(AccountStatusUpdatedEvent event) {
        log.info("----------------------- AccountStatusUpdatedEvent occurred -----------------------");
        this.accountId = event.getAccountId();
        this.status = event.getStatus();
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        log.info("----------------------- AccountCreatedEvent occurred -----------------------");
        this.accountId = event.getAccountId();
        this.balance = event.getInitialBalance();
        this.status = event.getStatus();
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        log.info("------------------------ AccountCreditedEvent occurred ------------------------");
        this.accountId = event.getAccountId();
        this.balance = this.balance + event.getAmount();
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        log.info("------------------------ AccountDebitedEvent occurred ------------------------");
        this.accountId = event.getAccountId();
        this.balance = this.balance - event.getAmount();
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent event) {
        log.info("------------------------ AccountActivatedEvent occurred ------------------------");
        this.accountId = event.getAccountId();
        this.status = event.getStatus();
    }


}
