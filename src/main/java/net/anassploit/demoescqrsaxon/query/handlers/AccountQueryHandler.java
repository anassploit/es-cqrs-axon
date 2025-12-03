package net.anassploit.demoescqrsaxon.query.handlers;

import net.anassploit.demoescqrsaxon.query.entities.Account;
import net.anassploit.demoescqrsaxon.query.queries.GetAllAccountsQuery;
import net.anassploit.demoescqrsaxon.query.repository.AccountRepository;
import net.anassploit.demoescqrsaxon.query.repository.OperationRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class AccountQueryHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    public AccountQueryHandler(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @QueryHandler
    public List<Account> getAllAccounts(GetAllAccountsQuery query) {
        return accountRepository.findAll();
    }
}
