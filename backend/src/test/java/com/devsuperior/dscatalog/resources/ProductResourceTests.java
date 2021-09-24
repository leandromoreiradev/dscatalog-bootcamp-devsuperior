package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class) // Carrega o contexto, porem somente a camada web(teste de unidade: testa o controlador)
public class ProductResourceTests {

    //Injetando o MockMvc
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service; //Mockando o service

    @Autowired
    private ObjectMapper objectMapper; //Transforma um obj java em JSON

    private Long existingId;
    private Long nonExistingId;
    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;

    @BeforeEach
    void setUp() throws Exception {
        //Inicializando variaveis auxiliares para o teste
        existingId = 1L;
        nonExistingId = 2L ;
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));
        //Simulando o comportamento do service
        //Quando chamar sevice.findAllPaged passando qualquer argumento, retorne um page
        when(service.findAllPaged(any())).thenReturn(page);
        //Quando chamar sevice.findById passando existingId, retorne um productDTO
        when(service.findById(existingId)).thenReturn(productDTO);

        //Quando chamar sevice.findById passando nonExistingId, lance uma ResourceNotFoundException
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        //Quando chamar sevice.update passando existingId, retorne um productDTO
        //any() é qualquer parametro
        //Se quisermos usar um valor específico para um argumento, podemos usar o eq(*aqui vai o argumento especifico)método
        when(service.update(eq(existingId), any())).thenReturn(productDTO);

        //Quando chamar sevice.update passando nonExistingId, lance uma ResourceNotFoundException
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);


    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        //o mockMvc.perform(get("/products")) faz uma requisição do tipo GET no path "/products"
        //e espero como um retorno status 200 isOK
        //.accept(MediaType.APPLICATION_JSON) significa que essa requisição vai aceitar como resposta o tipo json
        ResultActions result =
                mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON));
        //Assertions
        result.andExpect(status().isOk());
    }

    @Test
    public  void findByIdShouldReturnProductWhenIdExists() throws Exception {
        //Quando chamar GET "/products/{id}" passando um id existente deve retornar status 200
        ResultActions result =
                mockMvc.perform(get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));
        //Assertions
        result.andExpect(status().isOk()); //Verifica se o status é ok (200)
        result.andExpect(jsonPath("$.id").exists()); //Verifica se o corpo tem o atributo id
        result.andExpect(jsonPath("$.name").exists()); //Verifica se o corpo  o atributo name
        result.andExpect(jsonPath("$.description").exists()); //Verifica se o corpo  o atributo description;
    }

    @Test
    public  void findByIdShouldReturnNotFoundWhenIdDoesNonExists() throws Exception {
        //Quando chamar GET "/products/{id}" passando um id que não existe  deve retornar status 404 not found
        ResultActions result =
                mockMvc.perform(get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));
        //Assertions
        result.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        //Tranformando um obj para json
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        //Quando chamar PUT "/products/{id}" passando um id que existe  deve retornar productDTO
        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody) // Conteudo JSON que será mandado no body da REQ
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteudo que vai no body da REQ
                        .accept(MediaType.APPLICATION_JSON));// Tipo de conteudo aceito
        //Assertions
        result.andExpect(status().isOk()); //Verifica se o status é ok (200)
        result.andExpect(jsonPath("$.id").exists()); //Verifica se o corpo tem o atributo id
        result.andExpect(jsonPath("$.name").exists()); //Verifica se o corpo  o atributo name
        result.andExpect(jsonPath("$.description").exists()); //Verifica se o corpo  o atributo description

    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

        //Tranformando um obj para json
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        //Quando chamar PUT "/products/{id}" passando um id que não existe  deve lançar ResourceNotFoundException
        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody) // Conteudo JSON que será mandado no body da REQ
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de conteudo que vai no body da REQ
                        .accept(MediaType.APPLICATION_JSON));// Tipo de conteudo aceito
        //Assertions
        result.andExpect(status().isNotFound()); //Verifica se o status é ok (404)
    }

}
