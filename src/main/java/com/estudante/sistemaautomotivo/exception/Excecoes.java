package com.estudante.sistemaautomotivo.exception;

/** Exceções de domínio traduzidas em respostas HTTP pelo {@link TratadorDeErros}. */
public final class Excecoes {

    private Excecoes() {}

    /** 404: o recurso pedido na URL não existe. */
    public static class NaoEncontrado extends RuntimeException {
        public NaoEncontrado(String tipo, Long id) { super(tipo + " " + id + " não encontrado(a)"); }
    }

    /** 409: a operação conflita com o estado atual (nome duplicado, registro em uso). */
    public static class Conflito extends RuntimeException {
        public Conflito(String mensagem) { super(mensagem); }
    }

    /** 400: o corpo da requisição referencia algo que não existe. */
    public static class ReferenciaInvalida extends RuntimeException {
        public ReferenciaInvalida(String mensagem) { super(mensagem); }
    }
}
