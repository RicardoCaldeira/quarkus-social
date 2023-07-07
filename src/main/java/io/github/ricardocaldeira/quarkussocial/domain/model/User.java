package io.github.ricardocaldeira.quarkussocial.domain.model;

// import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Table(name = "users")
// PanacheEntity/PanacheEntityBase responsavel por criar a classe de requisição da entidade (CreateUserRequest), a classe
// UserResource e atribuir metodos de operações com o banco (user.persist, user.delete etc)
// extends PanacheEntityBase
public class User {

    // A anotação PanacheEntity já provê o Id da entidade, com isso tem de se remover o atributo
    // Já a anotação PanacheEntityBase permite a customização do Id

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer age;

}
