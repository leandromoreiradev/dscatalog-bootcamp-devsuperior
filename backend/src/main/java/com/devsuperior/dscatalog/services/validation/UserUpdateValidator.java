package com.devsuperior.dscatalog.services.validation;

import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//UserInsertValidator implementa ConstraintValidator que é uma interface do beansValidator
// Tipo da annotation -> UserInsertValid,
// Quem recebe a annotation -> UserInsertDTO
public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

    //Guarda as informações da requisição
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserRepository repository;

    //Neste metodo pode ou não ter uma logica quando o objeto for inicializado
    @Override
    public void initialize(UserUpdateValid ann) {
    }


    //Testa se o UserInsertDTO vai ser valido ou não, seu retorno é booleano
    @Override
    public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {

        //Pega o Mapa (par key value) os atributos da url  guarda no uriVars
        @SuppressWarnings("unchecked") // Suprimindo o warning
        var uriVars = (Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        //Convertendo a string id para long
        long userId = Long.parseLong(uriVars.get("id"));

        List<FieldMessage> list = new ArrayList<>();
        // Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista

        //Buscando usuario por email
        User user = repository.findByEmail(dto.getEmail());
        if (user != null && userId != user.getId()) { // Se email for diferente de nulo e o id diferente do id a ser atualizado
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
