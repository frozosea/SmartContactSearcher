package io.bootify.contact_searcher.user;

import io.bootify.contact_searcher.util.JwtUtil;
import io.bootify.contact_searcher.util.ReferencedException;
import io.bootify.contact_searcher.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserResource(final JwtUtil jwtUtil, final UserService userService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    private UserDTO getUser(String token, int userId) {
        String strUserId = jwtUtil.getUserIdFromToken(token);

        UserDTO authenticatedUser = userService.get(Integer.parseInt(strUserId));
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        System.out.println("User id from db: " + authenticatedUser.getId()  + "->" + "UserId from token " + userId);
        if (!authenticatedUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own contacts");
        }
        return authenticatedUser;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "id") final Integer id,
                                           @RequestHeader("Authorization") String token) {
        getUser(token, id);
        return ResponseEntity.ok(userService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createUser(@RequestBody @Valid final UserDTO userDTO) {
        final Integer createdId = userService.create(userDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateUser(@PathVariable(name = "id") final Integer id,
                                              @RequestBody @Valid final UserDTO userDTO,
                                              @RequestHeader("Authorization") String token) {
        getUser(token, id);
        userService.update(id, userDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") final Integer id,
                                           @RequestHeader("Authorization") String token) {
        getUser(token, id);
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
