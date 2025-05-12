package io.bootify.contact_searcher.contact;

import io.bootify.contact_searcher.user.User;
import io.bootify.contact_searcher.user.UserRepository;
import io.bootify.contact_searcher.util.ReferencedException;
import io.bootify.contact_searcher.util.ReferencedWarning;
import io.bootify.contact_searcher.util.JwtUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(value = "/api/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContactResource {
    private final JwtUtil jwtUtil;
    private final ContactService contactService;
    private final UserRepository userRepository;

    public ContactResource(JwtUtil jwtUtil, final ContactService contactService, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.contactService = contactService;
        this.userRepository = userRepository;
    }

    private User getUser(String token, int userId) {
        String strUserId = jwtUtil.getUserIdFromToken(token);

        User authenticatedUser = userRepository.findById(Integer.parseInt(strUserId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!authenticatedUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own contacts");
        }
        return authenticatedUser;
    }

    @GetMapping
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        return ResponseEntity.ok(contactService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getContact(@PathVariable(name = "id") final Integer id,
                                                 @RequestHeader("Authorization") String token) {
        ContactDTO contact = contactService.get(id);
        if (contact == null) {
            return ResponseEntity.notFound().build();
        }
        getUser(token, contact.getOwner());
        return ResponseEntity.ok(contact);
    }

    @GetMapping("/owner/{id}")
    public ResponseEntity<List<ContactDTO>> getContactsByUser(@PathVariable(name = "id") final Integer id,
                                                              @RequestHeader("Authorization") String token) {
        User authenticatedUser = getUser(token, id);
        List<ContactDTO> contacts = contactService.getByOwner(authenticatedUser);
        return ResponseEntity.ok(contacts);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createContact(@RequestBody @Valid final CreateContactRequest contactDTO,
                                                 @RequestHeader("Authorization") String token) {
        System.out.println(contactDTO.getOwnerId());
        getUser(token, contactDTO.getOwnerId());
        final Integer createdId = contactService.create(contactDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateContact(@PathVariable(name = "id") final Integer id,
                                                 @RequestBody @Valid final ContactDTO contactDTO,
                                                 @RequestHeader("Authorization") String token) {
        getUser(token, contactDTO.getOwner());
        contactService.update(id, contactDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteContact(@PathVariable(name = "id") final Integer id,
                                              @RequestHeader("Authorization") String token) {
        final ReferencedWarning referencedWarning = contactService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        User user = getUser(token, contactService.get(id).getOwner());
        contactService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

}
