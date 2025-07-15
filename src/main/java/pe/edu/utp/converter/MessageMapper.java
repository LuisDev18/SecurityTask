package pe.edu.utp.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.utp.dto.MessageRequestDTO;
import pe.edu.utp.entity.Message;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "recipient", ignore = true)
    @Mapping(target = "sender" , ignore = true)
    Message toEntity(MessageRequestDTO dto);
}
