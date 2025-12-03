package net.anassploit.demoescqrsaxon.query.handlers;

import lombok.extern.slf4j.Slf4j;
import net.anassploit.demoescqrsaxon.enums.OperationType;
import net.anassploit.demoescqrsaxon.events.*;
import net.anassploit.demoescqrsaxon.query.entities.Account;
import net.anassploit.demoescqrsaxon.query.entities.AccountOperation;
import net.anassploit.demoescqrsaxon.query.repository.AccountRepository;
import net.anassploit.demoescqrsaxon.query.repository.OperationRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AccountEventHandler {

    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    public AccountEventHandler(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @EventHandler
    public void on(AccountCreatedEvent event, EventMessage eventMessage) {
        log.info("--------------- Query Side AccountCreatedEvent received ---------------");
        Account account = Account.builder()
                .id(event.getAccountId())
                .balance(event.getInitialBalance())
                .currency(event.getCurrency())
                .status(event.getStatus())
                .createdAt(eventMessage.getTimestamp())
                .build();

        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        log.info("--------------- Query Side AccountActivatedEvent received ---------------");
        Account account = accountRepository.findById(event.getAccountId()).get();
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountStatusUpdatedEvent event) {
        log.info("--------------- Query Side AccountUpdatedEvent received ---------------");
        Account account = accountRepository.findById(event.getAccountId()).get();
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountDebitedEvent event, EventMessage eventMessage) {
        log.info("--------------- Query Side AccountDebitedEvent received ---------------");
        Account account = accountRepository.findById(event.getAccountId()).get();
        AccountOperation operation = AccountOperation.builder()
                .account(account)
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .date(eventMessage.getTimestamp())
                .type(OperationType.DEBIT)
                .build();
        operationRepository.save(operation);
        account.setBalance(account.getBalance() - event.getAmount());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditedEvent event, EventMessage eventMessage) {
        log.info("--------------- Query Side AccountCreditedEvent received ---------------");
        Account account = accountRepository.findById(event.getAccountId()).get();
        AccountOperation operation = AccountOperation.builder()
                .account(account)
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .date(eventMessage.getTimestamp())
                .type(OperationType.CREDIT)
                .build();
        operationRepository.save(operation);
        account.setBalance(account.getBalance() + event.getAmount());
        accountRepository.save(account);
    }
}
