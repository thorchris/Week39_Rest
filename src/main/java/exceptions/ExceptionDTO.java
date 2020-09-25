package exceptions;

/**
 *
 * @author thorc
 */
public class ExceptionDTO {
 
    public ExceptionDTO(int code, String description){
        this.code = code;
        this.description = description;
    }
    private int code;
    private String description;
}
