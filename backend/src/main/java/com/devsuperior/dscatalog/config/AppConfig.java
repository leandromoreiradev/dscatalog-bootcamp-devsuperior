package com.devsuperior.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

//Classe de configuração
@Configuration // Indica que esta é uma classe de configuração
public class AppConfig {

    @Bean // Bean é um componente do spring com assinatura de metodo, o spring intancia e gerencia ele em outros components
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //Auxiliar que traduz entre valores de token codificados por JWT e informações de autenticação OAuth
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        //Instancia o obj
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
        //Registra a chave do token
        tokenConverter.setSigningKey("MY-JWT-SECRET");
        return tokenConverter;
    }

    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

}
