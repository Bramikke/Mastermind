package exceptions;

public class EmailInGebruikException extends RuntimeException
{
    public EmailInGebruikException()
    {
    }
    
    public EmailInGebruikException(String message)
    {
        super(message);
    }

    public EmailInGebruikException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EmailInGebruikException(Throwable cause)
    {
        super(cause);
    }

    public EmailInGebruikException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
