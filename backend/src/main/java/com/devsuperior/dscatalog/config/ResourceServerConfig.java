package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

// Implementando OAuth2 resource server
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private JwtTokenStore tokenStore;

    //Definindo endpoints publicos
    private static final String[] PUBLIC = {"/oauth/token"};

    private static final String[] OPERATOR_OR_ADMIN = {"/products/**", "/categories/**"}; // O /** significa que todos apos a barra

    private static final String[] ADMIN = {"/users/**"};

    // Decodifica, analiza e verifica se o token é valido
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
       resources.tokenStore(tokenStore);
    }

    //Configurando quem pode acessar as rotas
    @Override
    public void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
               .antMatchers(PUBLIC).permitAll() // Definindo autorizações (Quem tiver acessando PUBLIC ta liberado não precisa do login, permite todos = "permitAll()"
               .antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll() // permite apenas acessar metodo get
               .antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN") // Pode acessar quem tiver algum dos roles definidos "OPERATOR", "ADMIN"
               .antMatchers(ADMIN).hasAnyRole("ADMIN") //So pode acessar quem tiver logado como "ADMIN"
               .anyRequest().authenticated(); //Para acessar qualquer outra rota n definida precisa estar logado não importando o perfil do usuario
    }
}
