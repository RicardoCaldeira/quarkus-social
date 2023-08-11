package io.github.ricardocaldeira.quarkussocial.rest;

import io.github.ricardocaldeira.quarkussocial.domain.model.Post;
import io.github.ricardocaldeira.quarkussocial.domain.model.User;
import io.github.ricardocaldeira.quarkussocial.domain.repository.FollowerRepository;
import io.github.ricardocaldeira.quarkussocial.domain.repository.PostRespository;
import io.github.ricardocaldeira.quarkussocial.domain.repository.UserRepository;
import io.github.ricardocaldeira.quarkussocial.rest.dto.CreatePostRequest;
import io.github.ricardocaldeira.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;
    private PostRespository postRespository;

    @Inject
    public PostResource(
            UserRepository userRepository,
            PostRespository postRespository,
            FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRespository = postRespository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);

        postRespository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the header followerId").build();
        }

        User follower = userRepository.findById(followerId);
        if (follower == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nonexistent followeId").build();
        }

        if (!followerRepository.follows(follower, user)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can't see these posts").build();
        }

        PanacheQuery<Post> query = postRespository
                .find("user", Sort.by("dateTime", Sort.Direction.Descending), user);

        List<Post> list = query.list();

        var postResponseList = list.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

       return Response.ok(postResponseList).build();
    }

}
