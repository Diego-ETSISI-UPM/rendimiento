package es.upm.etsisi.bg0272.tfg.rendimiento.model;
public class TablaPruebaRegistro {
    private Integer id;
    private String texto;
    public TablaPruebaRegistro(Integer id, String texto) {
        this.id = id;
        this.texto = texto;
    }
    public Integer getId() {
        return id;
    }
    public String getTexto() {
        return texto;
    }

}
