package io.github.ricardocaldeira.quarkussocial.rest;

import io.github.ricardocaldeira.quarkussocial.domain.model.User;
import io.github.ricardocaldeira.quarkussocial.domain.repository.UserRepository;
import io.github.ricardocaldeira.quarkussocial.rest.dto.CreateUserRequest;
// import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.github.ricardocaldeira.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository userRepository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if (!violations.isEmpty()) {
            return ResponseError.createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        // user.persist(); metodo seria usado se a classe user extendesse panacheEntityBase e ñ houvesse repositorio
        userRepository.persist(user);

        return Response.status(Response.Status.CREATED.getStatusCode()).entity(user).build();
    }

    @GET
    public Response listAllUsers() {
        // PanacheQuery<User> query = User.findAll(); metodo seria usado se a classe user extendesse panacheEntityBase e ñ houvesse repositorio
        PanacheQuery<User> query = userRepository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        // User user = User.findById(id); metodo seria usado se a classe user extendesse panacheEntityBase e ñ houvesse repositorio
        User user = userRepository.findById(id);
        if (user != null) {
            // user.delete(); metodo seria usado se a classe user extendesse panacheEntityBase e ñ houvesse repositorio
            userRepository.delete(user);

            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        // User user = User.findById(id); metodo seria usado se a classe user extendesse panacheEntityBase e ñ houvesse repositorio
        User user = userRepository.findById(id);
        if (user != null) {
            user.setName(userData.getName());
            user.setAge(userData.getAge());

            // não é necessário usar o userRepository.save ou user.update() por conta da anotação @Transctional
            // pois quando se tem um contexto transacional, qalquer alteração dentro dessa entidade será persistida quando o método finalizar

            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
