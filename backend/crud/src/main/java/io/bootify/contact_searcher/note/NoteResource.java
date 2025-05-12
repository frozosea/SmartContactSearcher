package io.bootify.contact_searcher.note;

import io.bootify.contact_searcher.contact.ContactDTO;
import io.bootify.contact_searcher.contact.ContactService;
import io.bootify.contact_searcher.user.User;
import io.bootify.contact_searcher.user.UserRepository;
import io.bootify.contact_searcher.util.JwtUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(value = "/api/notes", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoteResource {

    private final NoteService noteService;
    private final UserRepository userRepository;
    private final ContactService contactService;
    private final JwtUtil jwtUtil;

    public NoteResource(final ContactService contactService, final JwtUtil jwtUtil, final UserRepository userRepository, final NoteService noteService) {
        this.noteService = noteService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.contactService = contactService;
    }

    private User getUser(String token) {
        String strUserID = jwtUtil.getUserIdFromToken(token);

        return userRepository.findById(Integer.parseInt(strUserID))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private User getUserByNoteId(String token, int noteId) {
        User authenticatedUser = getUser(token);

        NoteDTO note = noteService.get(noteId);
        ContactDTO contact = contactService.get(note.getContact());

        if (contact == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found");
        }

        System.out.println("Contact: " + contact.getOwner() + "User" + authenticatedUser.getId());

        if (!authenticatedUser.getId().equals(contact.getOwner())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own contacts");
        }

        return authenticatedUser;
    }

    private User authUser(String token, NoteDTO noteDTO) {
        User user = getUser(token);

        if (!Objects.equals(user.getId(), contactService.get(noteDTO.getContact()).getOwner())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own contacts");
        }
        return user;
    }

    private User authUser(String token, int contactId) {
        User user = getUser(token);
        if (!Objects.equals(user.getId(), contactService.get(contactId).getOwner())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own contacts");
        }
        return user;
    }

    @GetMapping
    public ResponseEntity<List<NoteDTO>> getAllNotes() {
        return ResponseEntity.ok(noteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNote(@PathVariable(name = "id") final Integer id,
                                           @RequestHeader("Authorization") String token) {
        NoteDTO note = noteService.get(id);
        if (note == null) {
            return ResponseEntity.notFound().build();
        }
        getUserByNoteId(token, note.getId());
        return ResponseEntity.ok(note);
    }

    @GetMapping("/contact/{id}")
    public ResponseEntity<List<NoteDTO>> getNoteByContactId(@PathVariable(name = "id") final Integer id,
                                                            @RequestHeader("Authorization") String token) {
        List<NoteDTO> notes = noteService.findByContact(id);
        if (notes == null) {
            return ResponseEntity.notFound().build();
        }
        getUserByNoteId(token, notes.get(0).getId());
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createNote(@RequestBody @Valid final CreateNoteRequest noteDTO,
                                              @RequestHeader("Authorization") String token) {
        authUser(token, noteDTO.getContact());
        final Integer createdId = noteService.create(noteDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateNote(@PathVariable(name = "id") final Integer id,
                                              @RequestBody @Valid final NoteDTO noteDTO,
                                              @RequestHeader("Authorization") String token) {
        authUser(token, noteDTO);
        noteService.update(id, noteDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteNote(@PathVariable(name = "id") final Integer id,
                                           @RequestHeader("Authorization") String token) {
        NoteDTO note = noteService.get(id);
        if (note == null) {
            return ResponseEntity.notFound().build();
        }
        authUser(token, note);
        User user = getUser(token);
        noteService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

}
