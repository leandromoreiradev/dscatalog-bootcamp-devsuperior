package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.services.validation.UserInsertValid;
import com.devsuperior.dscatalog.services.validation.UserUpdateValid;

@UserUpdateValid //Essa annotation customizada verifica se o email inserido já existe no banco
public class UserUpdateDTO extends UserDTO{

    private static final long serialVersionUID = 1L;


}
