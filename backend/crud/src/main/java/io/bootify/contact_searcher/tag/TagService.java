package io.bootify.contact_searcher.tag;

import io.bootify.contact_searcher.contact.Contact;
import io.bootify.contact_searcher.contact.ContactRepository;
import io.bootify.contact_searcher.note.Note;
import io.bootify.contact_searcher.note.NoteRepository;
import io.bootify.contact_searcher.query.QueryService;
import io.bootify.contact_searcher.util.NotFoundException;
import io.bootify.contact_searcher.util.ReferencedWarning;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final ContactRepository contactRepository;
    private final NoteRepository noteRepository;
    private final QueryService queryService;

    public TagService(final TagRepository tagRepository,
                      final ContactRepository contactRepository, NoteRepository noteRepository, QueryService queryService) {
        this.tagRepository = tagRepository;
        this.contactRepository = contactRepository;
        this.noteRepository = noteRepository;
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

    public List<TagDTO> findAll() {
        final List<Tag> tags = tagRepository.findAll(Sort.by("id"));
        return tags.stream()
                .map(tag -> mapToDTO(tag, new TagDTO()))
                .toList();
    }


    public TagDTO get(final Integer id) {
        return tagRepository.findById(id)
                .map(tag -> mapToDTO(tag, new TagDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final CreateTagRequest tagRequest) {
        final Tag tag = new Tag();
        mapToEntity(tagRequest, tag);
        return tagRepository.save(tag).getId();
    }

    public void update(final Integer id, final TagDTO tagDTO) {
        final Tag tag = tagRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(tagDTO, tag);
        tagRepository.save(tag);
    }

    public void delete(final Integer id) {
        final Tag tag = tagRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        contactRepository.findAllByContactTagTags(tag)
                .forEach(contact -> contact.getContactTagTags().remove(tag));
        tagRepository.delete(tag);
    }

    private TagDTO mapToDTO(final Tag tag, final TagDTO tagDTO) {
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());
        return tagDTO;
    }

    private Tag mapToEntity(final TagDTO tagDTO, final Tag tag) {
        tag.setName(tagDTO.getName());
        return tag;
    }

    private Tag mapToEntity(final CreateTagRequest tagRequest, final Tag tag) {
        tag.setName(tagRequest.getName());
        return tag;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Tag tag = tagRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Contact tagsContact = contactRepository.findFirstByTags(tag);
        if (tagsContact != null) {
            referencedWarning.setKey("tag.contact.tags.referenced");
            referencedWarning.addParam(tagsContact.getId());
            return referencedWarning;
        }
        return null;
    }

}
