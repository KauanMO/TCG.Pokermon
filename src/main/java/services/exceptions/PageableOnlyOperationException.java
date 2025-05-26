package services.exceptions;

public class PageableOnlyOperationException extends RuntimeException {
    public PageableOnlyOperationException() {
        super("Essa operação está disponível apenas com paginação nessa versão, especifique os atributos: [page, pageSize]");
    }
}
