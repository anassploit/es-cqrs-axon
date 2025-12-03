package net.anassploit.demoescqrsaxon.commands.commands;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.anassploit.demoescqrsaxon.enums.AccountStatus;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
public class UpdateAccountStatusCommand {
    @TargetAggregateIdentifier
    private String id;
    private AccountStatus accountStatus;

}
