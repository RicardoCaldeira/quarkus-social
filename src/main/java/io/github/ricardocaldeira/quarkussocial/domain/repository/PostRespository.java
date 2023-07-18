package io.github.ricardocaldeira.quarkussocial.domain.repository;

import io.github.ricardocaldeira.quarkussocial.domain.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRespository implements PanacheRepository<Post> {
}
