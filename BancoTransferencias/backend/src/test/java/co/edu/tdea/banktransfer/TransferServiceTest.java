package co.edu.tdea.banktransfer;

import co.edu.tdea.banktransfer.dto.TransactionResponse;
import co.edu.tdea.banktransfer.dto.TransferRequest;
import co.edu.tdea.banktransfer.dto.TransferResponse;
import co.edu.tdea.banktransfer.entity.Account;
import co.edu.tdea.banktransfer.entity.TransactionStatus;
import co.edu.tdea.banktransfer.exception.AccountNotFoundException;
import co.edu.tdea.banktransfer.exception.InsufficientFundsException;
import co.edu.tdea.banktransfer.exception.InvalidTransferException;
import co.edu.tdea.banktransfer.repository.AccountRepository;
import co.edu.tdea.banktransfer.repository.TransactionRepository;
import co.edu.tdea.banktransfer.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de integración de las reglas de transferencia (H2 en memoria).
 */
@SpringBootTest
class TransferServiceTest {

    @Autowired TransferService service;
    @Autowired AccountRepository accounts;
    @Autowired TransactionRepository transactions;

    @BeforeEach
    void setUp() {
        transactions.deleteAll();
        accounts.deleteAll();
        accounts.save(new Account("111111", "Ana", new BigDecimal("1000.00")));
        accounts.save(new Account("222222", "Beto", new BigDecimal("500.00")));
    }

    private BigDecimal balance(String number) {
        return accounts.findByAccountNumber(number).orElseThrow().getBalance();
    }

    @Test
    void successfulTransferMovesMoneyAndReturnsQr() {
        TransferResponse response = service.transfer(new TransferRequest("111111", "222222", new BigDecimal("300"), "Pago"));
        assertEquals(0, new BigDecimal("700").compareTo(balance("111111")));
        assertEquals(0, new BigDecimal("800").compareTo(balance("222222")));
        assertEquals(TransactionStatus.COMPLETED, response.transaction().status());
        assertTrue(response.qrCode().startsWith("data:image/png;base64,"));
        assertTrue(response.qrContent().contains(response.transaction().reference()));
    }

    @Test
    void insufficientFundsIsRejectedAndKeptInHistory() {
        assertThrows(InsufficientFundsException.class,
                () -> service.transfer(new TransferRequest("111111", "222222", new BigDecimal("5000"), null)));
        assertEquals(0, new BigDecimal("1000").compareTo(balance("111111")));
        var history = service.findHistory(null);
        assertEquals(1, history.size());
        TransactionResponse rejected = history.get(0);
        assertEquals(TransactionStatus.REJECTED, rejected.status());
        assertTrue(rejected.failureReason().contains("Saldo insuficiente"));
    }

    @Test
    void sameAccountIsRejected() {
        assertThrows(InvalidTransferException.class,
                () -> service.transfer(new TransferRequest("111111", "111111", new BigDecimal("10"), null)));
    }

    @Test
    void unknownAccountIsRejected() {
        assertThrows(AccountNotFoundException.class,
                () -> service.transfer(new TransferRequest("111111", "999999", new BigDecimal("10"), null)));
        assertEquals(0, new BigDecimal("1000").compareTo(balance("111111")));
    }

    @Test
    void historyCanBeFilteredByAccount() {
        service.transfer(new TransferRequest("111111", "222222", new BigDecimal("10"), null));
        accounts.save(new Account("333333", "Caro", new BigDecimal("100.00")));
        service.transfer(new TransferRequest("333333", "222222", new BigDecimal("10"), null));
        assertEquals(2, service.findHistory("222222").size());
        assertEquals(1, service.findHistory("111111").size());
    }
}
