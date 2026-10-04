package com.estudante.sistemaautomotivo;

import com.estudante.sistemaautomotivo.repository.MarcaRepository;
import com.estudante.sistemaautomotivo.repository.ModeloRepository;
import com.estudante.sistemaautomotivo.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Ponta a ponta: HTTP → controller → serviço → JPA → banco (H2 com as migrações Flyway reais). */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SistemaAutomotivoApiTest {

    @Autowired MockMvc mvc;
    @Autowired VeiculoRepository veiculos;
    @Autowired ModeloRepository modelos;
    @Autowired MarcaRepository marcas;

    @BeforeEach
    void limpar() {
        veiculos.deleteAll();
        modelos.deleteAll();
        marcas.deleteAll();
    }

    private long criar(String url, String json) throws Exception {
        String corpo = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(url + "/")))
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(corpo.replaceAll(".*?\"id\":(\\d+).*", "$1"));
    }

    private long marca(String nome) throws Exception {
        return criar("/marcas", "{\"nome\":\"" + nome + "\"}");
    }

    private long modelo(String nome, long marcaId) throws Exception {
        return criar("/modelos", "{\"nome\":\"" + nome + "\",\"marcaId\":" + marcaId + "}");
    }

    private long veiculo(long modeloId, String cor, int ano, String preco, String status) throws Exception {
        return criar("/veiculos", """
                {"modeloId":%d,"cor":"%s","anoFabricacao":%d,"quilometragem":1000,"preco":%s,"status":"%s"}"""
                .formatted(modeloId, cor, ano, preco, status));
    }

    @Test
    void cicloCompletoDeVeiculo() throws Exception {
        long fiat = marca("Fiat");
        long uno = modelo("Uno", fiat);
        long id = veiculo(uno, "Prata", 2020, "35000.00", "DISPONIVEL");

        mvc.perform(get("/veiculos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca").value("Fiat"))
                .andExpect(jsonPath("$.modelo").value("Uno"))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));

        mvc.perform(put("/veiculos/" + id).contentType(MediaType.APPLICATION_JSON).content("""
                        {"modeloId":%d,"cor":"Preto","anoFabricacao":2020,"quilometragem":1500,"preco":33000.50,"status":"VENDIDO"}"""
                        .formatted(uno)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cor").value("Preto"))
                .andExpect(jsonPath("$.preco").value(33000.50))
                .andExpect(jsonPath("$.status").value("VENDIDO"));

        mvc.perform(delete("/veiculos/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/veiculos/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void filtrosSaoCombinadosEPaginados() throws Exception {
        long fiat = marca("Fiat");
        long vw = marca("Volkswagen");
        long uno = modelo("Uno", fiat);
        long gol = modelo("Gol", vw);
        veiculo(uno, "Prata", 2020, "30000", "DISPONIVEL");
        veiculo(uno, "Preto", 2021, "40000", "DISPONIVEL");
        veiculo(gol, "Branco", 2020, "28000", "DISPONIVEL");
        veiculo(gol, "Vermelho", 2020, "50000", "VENDIDO");

        // O original só aplicava UM filtro por vez (o primeiro não nulo); agora todos valem juntos.
        mvc.perform(get("/veiculos").param("anoFabricacao", "2020").param("status", "DISPONIVEL").param("precoMax", "29000"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].modelo").value("Gol"));

        mvc.perform(get("/veiculos").param("marcaId", String.valueOf(fiat)).param("precoMax", "35000"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].cor").value("Prata"));

        mvc.perform(get("/veiculos").param("modeloId", String.valueOf(gol)).param("status", "VENDIDO"))
                .andExpect(jsonPath("$.content", hasSize(1)));

        mvc.perform(get("/veiculos").param("tamanho", "2"))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page.totalElements").value(4))
                .andExpect(jsonPath("$.page.totalPages").value(2));
    }

    @Test
    void tamanhoDePaginaEhLimitado() throws Exception {
        mvc.perform(get("/veiculos").param("tamanho", "1000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(100));
    }

    @Test
    void statusInvalidoNoFiltroDevolve400() throws Exception {
        mvc.perform(get("/veiculos").param("status", "disponivel-ou-nao")).andExpect(status().isBadRequest());
    }

    @Test
    void marcaENomeDuplicadosDevolvem409() throws Exception {
        long fiat = marca("Fiat");
        mvc.perform(post("/marcas").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"  fiat \"}"))
                .andExpect(status().isConflict());

        modelo("Uno", fiat);
        mvc.perform(post("/modelos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"UNO\",\"marcaId\":" + fiat + "}"))
                .andExpect(status().isConflict());

        // Mesmo nome em OUTRA marca é permitido
        long vw = marca("Volkswagen");
        modelo("Uno", vw);
    }

    @Test
    void naoRemoveMarcaComModelosNemModeloComVeiculos() throws Exception {
        long fiat = marca("Fiat");
        long uno = modelo("Uno", fiat);
        long carro = veiculo(uno, "Prata", 2020, "30000", "DISPONIVEL");

        mvc.perform(delete("/marcas/" + fiat)).andExpect(status().isConflict());
        mvc.perform(delete("/modelos/" + uno)).andExpect(status().isConflict());

        mvc.perform(delete("/veiculos/" + carro)).andExpect(status().isNoContent());
        mvc.perform(delete("/modelos/" + uno)).andExpect(status().isNoContent());
        mvc.perform(delete("/marcas/" + fiat)).andExpect(status().isNoContent());
    }

    @Test
    void referenciasInexistentesDevolve400EIdsInexistentesNaUrlDevolve404() throws Exception {
        mvc.perform(post("/modelos").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"X\",\"marcaId\":999}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/veiculos").contentType(MediaType.APPLICATION_JSON).content(
                        "{\"modeloId\":999,\"cor\":\"A\",\"anoFabricacao\":2020,\"quilometragem\":0,\"preco\":1,\"status\":\"VENDIDO\"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/veiculos/999")).andExpect(status().isNotFound());
        mvc.perform(delete("/marcas/999")).andExpect(status().isNotFound());
        mvc.perform(put("/marcas/999").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"X\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void validaCamposDoVeiculo() throws Exception {
        long uno = modelo("Uno", marca("Fiat"));
        mvc.perform(post("/veiculos").contentType(MediaType.APPLICATION_JSON).content("""
                        {"modeloId":%d,"cor":"  ","anoFabricacao":1800,"quilometragem":-5,"preco":0,"status":"VENDIDO"}"""
                        .formatted(uno)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.cor").exists())
                .andExpect(jsonPath("$.campos.anoFabricacao").exists())
                .andExpect(jsonPath("$.campos.quilometragem").exists())
                .andExpect(jsonPath("$.campos.preco").exists());

        mvc.perform(post("/veiculos").contentType(MediaType.APPLICATION_JSON).content("""
                        {"modeloId":%d,"cor":"A","anoFabricacao":2020,"quilometragem":0,"preco":10.999,"status":"VENDIDO"}"""
                        .formatted(uno)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.preco").exists());

        mvc.perform(post("/veiculos").contentType(MediaType.APPLICATION_JSON).content("""
                        {"modeloId":%d,"cor":"A","anoFabricacao":2020,"quilometragem":0,"preco":10,"status":"quase-novo"}"""
                        .formatted(uno)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void clienteNaoImpoeIdENemEstruturaInternaPorJson() throws Exception {
        long id = criar("/marcas", "{\"id\":777,\"nome\":\"Ford\",\"modelos\":[{\"nome\":\"Ka\"}]}");
        org.junit.jupiter.api.Assertions.assertNotEquals(777L, id);
        mvc.perform(get("/modelos")).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void erroInesperadoNaoVazaDetalhes_ERotaInexistenteEh404() throws Exception {
        mvc.perform(get("/nao-existe")).andExpect(status().isNotFound());
        mvc.perform(get("/veiculos/abc")).andExpect(status().isBadRequest());
        mvc.perform(post("/marcas").contentType(MediaType.APPLICATION_JSON).content("{quebrado"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Requisição malformada"));
    }

    @Test
    void corsSoParaOrigensConfiguradasEActuatorMinimo() throws Exception {
        mvc.perform(options("/veiculos").header("Origin", "http://localhost:5500")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());
        mvc.perform(options("/veiculos").header("Origin", "http://malicioso.example")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
        mvc.perform(get("/actuator/env")).andExpect(status().isNotFound());
    }
}
