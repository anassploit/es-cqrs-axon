package net.anassploit.demoescqrsaxon.commands.dto;

public record DebitAccountRequestDTO(String accountId, double amount, String currency) {}
