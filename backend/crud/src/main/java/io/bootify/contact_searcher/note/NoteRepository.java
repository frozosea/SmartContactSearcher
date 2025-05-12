package io.bootify.contact_searcher.note;

import io.bootify.contact_searcher.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NoteRepository extends JpaRepository<Note, Integer> {

    Note findFirstByContact(Contact contact);

    List<Note> findAllByContactId(Integer contactId);
}
