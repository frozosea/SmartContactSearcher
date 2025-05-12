package io.bootify.contact_searcher.tag;

import io.bootify.contact_searcher.contact.ContactService;
import io.bootify.contact_searcher.user.UserRepository;
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
@RequestMapping(value = "/api/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagResource {

    private final TagService tagService;
    private final UserRepository userRepository;
    private final ContactService contactService;
    private final JwtUtil jwtUtil;


    public TagResource(final UserRepository userRepository,
                       final ContactService contactService,
                       final JwtUtil jwtUtil,
                       final TagService tagService) {
        this.tagService = tagService;
        this.userRepository = userRepository;
        this.contactService = contactService;
        this.jwtUtil = jwtUtil;

    }

    private void authUser(String token) {
        String strUserId = jwtUtil.getUserIdFromToken(token);

        userRepository.findById(Integer.parseInt(strUserId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }


    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags(
            @RequestHeader("Authorization") String token) {
        authUser(token);
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTag(@PathVariable(name = "id") final Integer id,
                                         @RequestHeader("Authorization") String token) {
        authUser(token);
        return ResponseEntity.ok(tagService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createTag(@RequestBody @Valid final CreateTagRequest tagDTO,
                                             @RequestHeader("Authorization") String token) {
        authUser(token);
        final Integer createdId = tagService.create(tagDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateTag(@PathVariable(name = "id") final Integer id,
                                             @RequestBody @Valid final TagDTO tagDTO,
                                             @RequestHeader("Authorization") String token) {
        authUser(token);
        tagService.update(id, tagDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTag(@PathVariable(name = "id") final Integer id,
                                          @RequestHeader("Authorization") String token) {
        authUser(token);
        final ReferencedWarning referencedWarning = tagService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
