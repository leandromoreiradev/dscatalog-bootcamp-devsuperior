package com.devsuperior.dscatalog.config;

import com.devsuperior.dscatalog.components.JwtTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

//Implementando o AuthorizationServer
@Configuration
@EnableAuthorizationServer //Faz o processamento por debaixo dos panos pra dizer que esta classe representa o authorizationServer do Oauth
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${jwt.duration}")
    private Integer jwtDuration;

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

    //Injetando o JwtTokenEnhancer
    @Autowired
    private JwtTokenEnhancer tokenEnhancer;


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    //Configura como vai ser a autenticaçao e quais serao os dados do client
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
       clients.inMemory() //Processo em memoria
              .withClient(clientId) //Define o client_id
              .secret(passwordEncoder.encode(clientSecret)) //Define o client_secret
              .scopes("read", "write")//informa tipo de acesso dado, escrita ou leitura ou os 2
              .authorizedGrantTypes("password") //tipos de acesso de login
              .accessTokenValiditySeconds(jwtDuration); //Tempo de expiração do token em segundos nesse caso aqui 24hs = 86400 segundos
    }

    //Configura quem que vai autorizar e o qual o formato do token
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        TokenEnhancerChain chain = new TokenEnhancerChain();
        chain.setTokenEnhancers(Arrays.asList(accessTokenConverter, tokenEnhancer));

        endpoints.authenticationManager(authenticationManager)
                 .tokenStore(tokenStore) //obj responsaveis por processar o token
                 .accessTokenConverter(accessTokenConverter)
                 .tokenEnhancer(chain);
    }
}
