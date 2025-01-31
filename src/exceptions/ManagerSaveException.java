package exceptions;

import java.io.File;

public class ManagerSaveException extends RuntimeException {
    private File managerFile;

    public ManagerSaveException() {
    }

    public ManagerSaveException(final String message) {
        super(message);
    }

    public ManagerSaveException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ManagerSaveException(final Throwable cause) {
        super(cause);
    }

    public ManagerSaveException(String message, File managerFile) {
        super(message);
        this.managerFile = managerFile;
    }

    public String getDetailMessage() {
        return String.format("%s файл: %s", getMessage(), managerFile.getName());
    }

}
