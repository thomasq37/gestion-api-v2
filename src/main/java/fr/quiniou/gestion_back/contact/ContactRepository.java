package fr.quiniou.gestion_back.contact;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

	Page<Contact> findAllByAppartementIdIn(List<Long> appartementIds, Pageable pageable);

	// étends des méhodes de JpaRepository
}
