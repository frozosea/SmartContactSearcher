package io.bootify.contact_searcher.note;

import io.bootify.contact_searcher.contact.Contact;
import io.bootify.contact_searcher.contact.ContactRepository;
import io.bootify.contact_searcher.query.QueryService;
import io.bootify.contact_searcher.user.User;
import io.bootify.contact_searcher.util.NotFoundException;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final ContactRepository contactRepository;
    private final QueryService queryService;

    public NoteService(final NoteRepository noteRepository,
                       final ContactRepository contactRepository, QueryService queryService) {
        this.noteRepository = noteRepository;
        this.contactRepository = contactRepository;
        this.queryService = queryService;
    }

    protected void updateContact(Integer contactId) {
        List<Note> notes = noteRepository.findAllByContactId(contactId);
        Optional<Contact> contact = contactRepository.findById(contactId);
        if (contact.isPresent()) {
            Contact c1 = contact.get();
            c1.setContactNotes(new HashSet<Note>(notes));
            queryService.updateContact(queryService.toContactIndexDTO(c1));
        }
    }

    public List<NoteDTO> findAll() {
        final List<Note> notes = noteRepository.findAll(Sort.by("id"));
        return notes.stream()
                .map(note -> mapToDTO(note, new NoteDTO()))
                .toList();
    }

    public List<NoteDTO> findByContact(int contactId) {
        final List<Note> notes = noteRepository.findAllByContactId(contactId);
        return notes.stream()
                .map(note -> mapToDTO(note, new NoteDTO()))
                .toList();
    }

    public NoteDTO get(final Integer id) {
        return noteRepository.findById(id)
                .map(note -> mapToDTO(note, new NoteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final CreateNoteRequest noteRequest) {
        final Note note = new Note();
        mapToEntity(noteRequest, note);
        Note createdNote = noteRepository.save(note);
        updateContact(noteRequest.getContact());
        return createdNote.getId();
    }

    public void update(final Integer id, final NoteDTO noteDTO) {
        final Note note = noteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(noteDTO, note);
        noteRepository.save(note);
        updateContact(noteDTO.getContact());
    }

    public void delete(final Integer id,final Integer userId) {
        noteRepository.deleteById(id);
        updateContact(userId);

    }

    private NoteDTO mapToDTO(final Note note, final NoteDTO noteDTO) {
        noteDTO.setId(note.getId());
        noteDTO.setContent(note.getContent());
        noteDTO.setCreatedAt(note.getCreatedAt());
        noteDTO.setUpdatedAt(note.getUpdatedAt());
        noteDTO.setContact(note.getContact() == null ? null : note.getContact().getId());
        return noteDTO;
    }

    private Note mapToEntity(final NoteDTO noteDTO, final Note note) {
        note.setContent(noteDTO.getContent());
        note.setCreatedAt(noteDTO.getCreatedAt());
        note.setUpdatedAt(noteDTO.getUpdatedAt());
        final Contact contact = noteDTO.getContact() == null ? null : contactRepository.findById(noteDTO.getContact())
                .orElseThrow(() -> new NotFoundException("contact not found"));
        note.setContact(contact);
        return note;
    }

    private Note mapToEntity(final CreateNoteRequest createNoteRequest, final Note note) {
        note.setContent(createNoteRequest.getContent());
        note.setCreatedAt(OffsetDateTime.now());
        note.setUpdatedAt(OffsetDateTime.now());
        final Contact contact = contactRepository.findById(createNoteRequest.getContact())
                .orElseThrow(() -> new NotFoundException("contact not found"));
        note.setContact(contact);
        return note;
    }

}
