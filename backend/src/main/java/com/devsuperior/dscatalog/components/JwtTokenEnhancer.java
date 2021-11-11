package com.devsuperior.dscatalog.components;

import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenEnhancer implements TokenEnhancer {

    @Autowired
    private UserRepository userRepository;

    //Implementando metodo para acrescentar obj/dados ao token
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        //Buscando o User por username que aqui foi definido pelo email ou seja, vai buscar por email
        User user = userRepository.findByEmail(authentication.getName());

        //Criando Map<Key,Value> para add no token
        Map<String,Object> map = new HashMap<>();
        map.put("userFirstName",user.getFirstName());
        map.put("userId",user.getId());

        //Fazendo o Downcast de OAuth2AccessToken para o tipo DefaultOAuth2AccessToken
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;

        //Adicionando as informações do map no token
        token.setAdditionalInformation(map);

        return accessToken;

    }
}
