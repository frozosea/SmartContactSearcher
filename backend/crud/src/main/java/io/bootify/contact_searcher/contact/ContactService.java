package io.bootify.contact_searcher.contact;

import io.bootify.contact_searcher.note.Note;
import io.bootify.contact_searcher.note.NoteRepository;
import io.bootify.contact_searcher.query.QueryService;
import io.bootify.contact_searcher.query.requests.DeleteContactRequest;
import io.bootify.contact_searcher.tag.Tag;
import io.bootify.contact_searcher.tag.TagRepository;
import io.bootify.contact_searcher.user.User;
import io.bootify.contact_searcher.user.UserRepository;
import io.bootify.contact_searcher.util.NotFoundException;
import io.bootify.contact_searcher.util.ReferencedWarning;
import jakarta.transaction.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final QueryService queryService;

    public ContactService(final ContactRepository contactRepository,
                          final TagRepository tagRepository, final UserRepository userRepository,
                          final NoteRepository noteRepository, QueryService queryService) {
        this.contactRepository = contactRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.queryService = queryService;
    }

    public List<ContactDTO> findAll() {
        final List<Contact> contacts = contactRepository.findAll(Sort.by("id"));
        return contacts.stream()
                .map(contact -> mapToDTO(contact, new ContactDTO()))
                .toList();
    }

    public ContactDTO get(final Integer id) {
        return contactRepository.findById(id)
                .map(contact -> mapToDTO(contact, new ContactDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public List<ContactDTO> getByTag(final Tag tag) {
        List<Contact> contacts = contactRepository.findAllByContactTagTags(tag);
        List<ContactDTO> contactDTOS = new ArrayList<>();
        for (Contact contact : contacts) {
            contactDTOS.add(mapToDTO(contact, new ContactDTO()));
        }
        return contactDTOS;
    }

    public List<ContactDTO> getByOwner(final User user) {
        List<Contact> contacts = contactRepository.findAllByOwner(user);
        List<ContactDTO> contactDTOS = new ArrayList<>();
        for (Contact contact : contacts) {
            contactDTOS.add(mapToDTO(contact, new ContactDTO()));
        }
        return contactDTOS;
    }

    public Integer create(final CreateContactRequest contactRequest) {
        Contact contact = new Contact();
        mapToEntity(contactRequest, contact);
        Contact createdContact = contactRepository.save(contact);
        queryService.createContact(queryService.toContactIndexDTO(createdContact));
        return createdContact.getId();
    }

    public void update(final Integer id, final ContactDTO contactDTO) {
        final Contact contact = contactRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(contactDTO, contact);
        contactRepository.save(contact);
        Contact createdContact = contactRepository.save(contact);
        queryService.updateContact(queryService.toContactIndexDTO(createdContact));
    }

    public void delete(final Integer contactId, final Integer userId) {
        contactRepository.deleteById(contactId);
        DeleteContactRequest r = new DeleteContactRequest();
        r.setContactId(contactId.toString());
        r.setUser_id(userId.toString());
        queryService.deleteContact(r);
    }

    private ContactDTO mapToDTO(final Contact contact, final ContactDTO contactDTO) {
        contactDTO.setId(contact.getId());
        contactDTO.setName(contact.getName());
        contactDTO.setJobTitle(contact.getJobTitle());
        contactDTO.setPhone(contact.getPhone());
        contactDTO.setEmail(contact.getEmail());
        contactDTO.setCreatedAt(contact.getCreatedAt());
        contactDTO.setUpdatedAt(contact.getUpdatedAt());
        contactDTO.setTags(contact.getTags() == null ? null : contact.getTags().getId());
        contactDTO.setOwner(contact.getOwner() == null ? null : contact.getOwner().getId());
        contactDTO.setContactTagTags(contact.getContactTagTags().stream()
                .map(tag -> tag.getId())
                .toList());
        return contactDTO;
    }

    private Contact mapToEntity(final ContactDTO contactDTO, final Contact contact) {
        contact.setName(contactDTO.getName());
        contact.setJobTitle(contactDTO.getJobTitle());
        contact.setEmail(contactDTO.getEmail());
        contact.setPhone(contactDTO.getPhone());
        contact.setCreatedAt(contactDTO.getCreatedAt());
        contact.setUpdatedAt(contactDTO.getUpdatedAt());
        final Tag tags = contactDTO.getTags() == null ? null : tagRepository.findById(contactDTO.getTags())
                .orElseThrow(() -> new NotFoundException("tags not found"));
        contact.setTags(tags);
        final User owner = contactDTO.getOwner() == null ? null : userRepository.findById(contactDTO.getOwner())
                .orElseThrow(() -> new NotFoundException("owner not found"));
        contact.setOwner(owner);
        final List<Tag> contactTagTags = tagRepository.findAllById(
                contactDTO.getContactTagTags() == null ? Collections.emptyList() : contactDTO.getContactTagTags());
        if (contactTagTags.size() != (contactDTO.getContactTagTags() == null ? 0 : contactDTO.getContactTagTags().size())) {
            throw new NotFoundException("one of contactTagTags not found");
        }
        contact.setContactTagTags(new HashSet<>(contactTagTags));
        return contact;
    }

    private Contact mapToEntity(final CreateContactRequest contactRequest, final Contact contact) {
        contact.setName(contactRequest.getName());
        contact.setJobTitle(contactRequest.getJobTitle());
        contact.setCreatedAt(OffsetDateTime.now());
        contact.setUpdatedAt(OffsetDateTime.now());
        final List<Integer> tags = contactRequest.getContactTags();
        Set<Tag> tagSet = new HashSet<>();
        if (tags != null) {
            for (Integer tagId : tags) {
                Optional<Tag> tag = tagRepository.findById(tagId);
                tag.ifPresent(tagSet::add);
            }
        }
        final User owner = userRepository.findById(contactRequest.getOwnerId())
                .orElseThrow(() -> new NotFoundException("owner not found"));
        contact.setOwner(owner);
        contact.setContactTagTags(tagSet);
        return contact;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Contact contact = contactRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Note contactNote = noteRepository.findFirstByContact(contact);
        if (contactNote != null) {
            referencedWarning.setKey("contact.note.contact.referenced");
            referencedWarning.addParam(contactNote.getId());
            return referencedWarning;
        }
        return null;
    }

}
