package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;



//UserInsertValidator implementa ConstraintValidator que é uma interface do beansValidator
// Tipo da annotation -> UserInsertValid,
// Quem recebe a annotation -> UserInsertDTO
public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository repository;

    //Neste metodo pode ou não ter uma logica quando o objeto for inicializado
    @Override
    public void initialize(UserInsertValid ann) {
    }


    //Testa se o UserInsertDTO vai ser valido ou não, seu retorno é booleano
    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        // Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista

        //Buscando usuario por email
        User user = repository.findByEmail(dto.getEmail());
        if (user != null) { // Se email for diferente de nulo
            // Insere na lista um novo erro
            list.add(new FieldMessage("email", "Email já existe"));
        }
        //Inserindo erros na lista de erros do beansValidation
        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}
