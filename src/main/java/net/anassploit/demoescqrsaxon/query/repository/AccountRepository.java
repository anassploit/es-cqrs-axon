package net.anassploit.demoescqrsaxon.query.repository;

import net.anassploit.demoescqrsaxon.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
