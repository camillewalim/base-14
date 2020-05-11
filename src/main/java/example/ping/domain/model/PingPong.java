package example.ping.domain.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author camille.walim
 * dummy model
 */
@Entity
public class PingPong{
	
	@Id
	public final String uuid;
	public final boolean ping;
	
	public PingPong(){
		this(false);
	}
	public PingPong (String uuid, boolean isPing) {
		this.uuid = uuid;
		this.ping = isPing;
	}	
	public PingPong (boolean isPing) {
		this(UUID.randomUUID().toString(), isPing);
	}
	
}
