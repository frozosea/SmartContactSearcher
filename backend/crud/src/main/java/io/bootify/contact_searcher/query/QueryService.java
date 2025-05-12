package io.bootify.contact_searcher.query;

import io.bootify.contact_searcher.contact.Contact;
import io.bootify.contact_searcher.contact.CreateContactRequest;
import io.bootify.contact_searcher.note.Note;
import io.bootify.contact_searcher.query.requests.*;
import io.bootify.contact_searcher.query.repository.ContactQueryRepository;
import io.bootify.contact_searcher.tag.Tag;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueryService {

    private final ContactQueryRepository contactQueryRepository;

    public QueryService(ContactQueryRepository contactQueryRepository) {
        this.contactQueryRepository = contactQueryRepository;
    }

    /**
     * Convert your Java-side CreateContactRequest to the ContactIndexDTO
     * for the Python service.
     */
    public ContactIndexDTO toContactIndexDTO(CreateContactRequest contactRequest) {
        ContactIndexDTO dto = new ContactIndexDTO();
        // The contactId is something you generate or get from the Contact entity
        // We'll keep it empty or set it externally if needed
        dto.setContactId(null);

        // userId as string, because the Python service expects strings
        dto.setUserId(String.valueOf(contactRequest.getOwnerId()));

        dto.setName(contactRequest.getName());
        dto.setJobTitle(contactRequest.getJobTitle());

        // Convert integer tags to string tags if needed
        List<String> tagStrings = new ArrayList<>();
        if (contactRequest.getContactTags() != null) {
            for (Integer tagId : contactRequest.getContactTags()) {
                tagStrings.add(String.valueOf(tagId));
            }
        }
        dto.setTags(tagStrings);

        // notesCombined is not in CreateContactRequest directly—adjust if needed
        // Possibly set it to an empty string or some logic
        dto.setNotesCombined("");

        return dto;
    }

    public ContactIndexDTO toContactIndexDTO(Contact contactRequest) {
        ContactIndexDTO dto = new ContactIndexDTO();
        // The contactId is something you generate or get from the Contact entity
        // We'll keep it empty or set it externally if needed
        dto.setContactId(null);

        // userId as string, because the Python service expects strings
        dto.setUserId(String.valueOf(contactRequest.getOwner()));

        dto.setName(contactRequest.getName());
        dto.setJobTitle(contactRequest.getJobTitle());

        // Convert integer tags to string tags if needed
        List<String> tagStrings = new ArrayList<>();
        if (contactRequest.getTags() != null) {
            for (Tag tag : contactRequest.getContactTagTags()) {
                tagStrings.add(tag.getName());
            }
        }
        dto.setTags(tagStrings);

        // notesCombined is not in CreateContactRequest directly—adjust if needed
        // Possibly set it to an empty string or some logic
        if (contactRequest.getContactNotes() != null) {
            String joinedContents = String.join(
                    ".",
                    contactRequest.getContactNotes().stream()
                            .map(Note::getContent)
                            .toArray(String[]::new)
            );
            dto.setNotesCombined(joinedContents);
        } else {
            dto.setNotesCombined("");
        }
        return dto;
    }

    // --------------------------------------------------
    //  External Calls
    // --------------------------------------------------

    public List<Float> generateEmbedding(String text) {
        return contactQueryRepository.generateEmbedding(text);
    }

    public BaseResponse createContact(ContactIndexDTO contactIndexDTO) {
        return contactQueryRepository.createContact(contactIndexDTO);
    }

    public BaseResponse updateContact(ContactIndexDTO contactIndexDTO) {
        return contactQueryRepository.updateContact(contactIndexDTO);
    }

    public BaseResponse deleteContact(DeleteContactRequest request) {
        return contactQueryRepository.deleteContact(request);
    }

    public List<ContactIndexDTO> searchContacts(SearchContactsRequest request) {
        return contactQueryRepository.searchContacts(request);
    }

    public List<ContactIndexDTO> searchContactsByTags(SearchByTagsRequest request) {
        return contactQueryRepository.searchContactsByTags(request);
    }
}
