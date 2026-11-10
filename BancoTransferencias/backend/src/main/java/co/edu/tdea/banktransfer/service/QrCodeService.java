package co.edu.tdea.banktransfer.service;

import co.edu.tdea.banktransfer.dto.TransactionResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

/**
 * Genera el código QR con los datos básicos de una transacción.
 */
@Service
public class QrCodeService {

    private static final int SIZE = 300;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Arma el texto que se codifica en el QR.
     *
     * @param t transacción
     * @return texto con referencia, cuentas, monto, fecha y estado
     */
    public String buildContent(TransactionResponse t) {
        var money = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
        money.setMaximumFractionDigits(2);
        return """
                BANCO TdeA - Comprobante de transferencia
                Referencia: %s
                Origen: %s
                Destino: %s
                Monto: %s
                Fecha: %s
                Estado: %s""".formatted(t.reference(), t.sourceAccount(), t.targetAccount(),
                money.format(t.amount()), t.createdAt().format(DATE_FORMAT), t.status());
    }

    /**
     * Genera la imagen PNG del QR de la transacción.
     *
     * @param t transacción
     * @return bytes del PNG de 300x300
     */
    public byte[] generatePng(TransactionResponse t) {
        try {
            BitMatrix matrix = new QRCodeWriter().encode(buildContent(t), BarcodeFormat.QR_CODE, SIZE, SIZE,
                    Map.of(EncodeHintType.CHARACTER_SET, "UTF-8",
                            EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                            EncodeHintType.MARGIN, 1));
            var out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (WriterException e) {
            throw new IllegalStateException("No se pudo generar el código QR", e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Genera el QR como data URI lista para un {@code <img src>}.
     *
     * @param t transacción
     * @return cadena {@code data:image/png;base64,...}
     */
    public String generateDataUri(TransactionResponse t) {
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(generatePng(t));
    }
}
