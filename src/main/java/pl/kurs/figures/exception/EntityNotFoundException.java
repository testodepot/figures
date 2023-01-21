package pl.kurs.figures.exception;

public class EntityNotFoundException extends RuntimeException {

    private Long idNotFound;

    private String entity;

    public EntityNotFoundException(Long idNotFound, String entity) {
        this.idNotFound = idNotFound;
        this.entity = entity;
    }


    public Long getIdNotFound() {
        return idNotFound;
    }

    public String getEntity() {
        return entity;
    }


}
