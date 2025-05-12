package io.bootify.contact_searcher.contact;

import io.bootify.contact_searcher.tag.Tag;
import io.bootify.contact_searcher.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContactRepository extends JpaRepository<Contact, Integer> {

    Contact findFirstByTags(Tag tag);

    Contact findFirstByOwner(User user);

    List<Contact> findAllByOwner(User user);

    Contact findFirstByContactTagTags(Tag tag);

    List<Contact> findAllByContactTagTags(Tag tag);

}
