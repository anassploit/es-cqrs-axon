package net.anassploit.demoescqrsaxon.query.controllers;

import net.anassploit.demoescqrsaxon.query.dto.AccountStatementResponseDTO;
import net.anassploit.demoescqrsaxon.query.entities.Account;
import net.anassploit.demoescqrsaxon.query.entities.AccountOperation;
import net.anassploit.demoescqrsaxon.query.queries.GetAccountStatementQuery;
import net.anassploit.demoescqrsaxon.query.queries.GetAllAccountsQuery;
import net.anassploit.demoescqrsaxon.query.queries.WatchEventQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/query/accounts")
public class AccountQueryController {
    private QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/all")
    public CompletableFuture<List<Account>> getAllAccounts() {
        CompletableFuture<List<Account>> response = queryGateway.query(
                new GetAllAccountsQuery(),
                ResponseTypes.multipleInstancesOf(Account.class)
        );
        return response;
    }

    @GetMapping("/accouStatement/{accountId}")
    public CompletableFuture<AccountStatementResponseDTO> getAccountHistory(@PathVariable String accountId) {

        return queryGateway.query(new GetAccountStatementQuery(accountId), ResponseTypes.instanceOf(AccountStatementResponseDTO.class));
    }

    @GetMapping(value = "/watch/{accountId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AccountOperation> watch(@PathVariable String accountId) {
        SubscriptionQueryResult<AccountOperation, AccountOperation> result =
                queryGateway.subscriptionQuery(new WatchEventQuery(accountId),
                        ResponseTypes.instanceOf(AccountOperation.class),
                        ResponseTypes.instanceOf(AccountOperation.class)
                );
        return result.initialResult().concatWith(result.updates());
    }

}
