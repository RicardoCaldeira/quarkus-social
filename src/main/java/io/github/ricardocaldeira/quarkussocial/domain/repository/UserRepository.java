package io.github.ricardocaldeira.quarkussocial.domain.repository;

import io.github.ricardocaldeira.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped // cria instancia do repositorio dentro do contexto de injeção de independencias da aplicação
public class UserRepository implements PanacheRepository<User> {

}
