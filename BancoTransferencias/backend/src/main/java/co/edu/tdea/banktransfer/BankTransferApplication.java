package co.edu.tdea.banktransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación de transferencias bancarias.
 */
@SpringBootApplication
public class BankTransferApplication {

    /**
     * Arranca la aplicación Spring Boot.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(BankTransferApplication.class, args);
    }
}
