package example.ping.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import example.ping.domain.model.PingPong;

/**
 * @author camille.walim
 * 
 * JPA repository
 */
public interface PingPongRepository extends JpaRepository<PingPong, String>{}
