package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

//Implementando o AuthorizationServer
@Configuration
@EnableAuthorizationServer //Faz o processamento por debaixo dos panos pra dizer que esta classe representa o authorizationServer do Oauth
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    //Injetando o BCryptPasswordEncoder
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //Injetando o JwtAccessTokenConverter
    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    //Injetando o JwtTokenStore
    @Autowired
    private JwtTokenStore tokenStore;

    //Injetando o AuthenticationManager
    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    //Configura como vai ser a autenticaçao e quais serao os dados do client
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
       clients.inMemory() //Processo em memoria
              .withClient("dscatalog") //Define o client_id
              .secret(passwordEncoder.encode("dscatalog123")) //Define o client_secret
              .scopes("read", "write")//informa tipo de acesso dado, escrita ou leitura ou os 2
              .authorizedGrantTypes("password") //tipos de acesso de login
              .accessTokenValiditySeconds(86400); //Tempo de expiração do token em segundos nesse caso aqui 24hs = 86400 segundos
    }

    //Configura quem que vai autorizar e o qual o formato do token
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                 .tokenStore(tokenStore) //obj responsaveis por processar o token
                 .accessTokenConverter(accessTokenConverter);
    }
}
