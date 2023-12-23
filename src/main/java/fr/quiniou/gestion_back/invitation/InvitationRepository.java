package fr.quiniou.gestion_back.invitation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long>{

	Optional<Invitation> findByMdp(String codeInvitation);
}
