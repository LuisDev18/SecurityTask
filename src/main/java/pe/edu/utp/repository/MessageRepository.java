package pe.edu.utp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {

}
