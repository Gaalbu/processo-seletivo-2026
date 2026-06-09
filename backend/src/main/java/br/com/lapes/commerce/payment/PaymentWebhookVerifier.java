package br.com.lapes.commerce.payment;

import com.fasterxml.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentWebhookVerifier {

  private final String webhookSecret;

  public PaymentWebhookVerifier(@Value("${app.payment.webhook-secret:}") String webhookSecret) {
    this.webhookSecret = webhookSecret;
  }

  public boolean isValid(String requestId, String signature, JsonNode payload) {
    if (webhookSecret == null || webhookSecret.isBlank()) {
      return true;
    }
    if (requestId == null || signature == null || payload == null) {
      return false;
    }

    String dataId = payload.path("data").path("id").asText(null);
    String ts = part(signature, "ts");
    String v1 = part(signature, "v1");
    if (dataId == null || ts == null || v1 == null) {
      return false;
    }

    String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";";
    return hmacSha256(manifest, webhookSecret).equalsIgnoreCase(v1);
  }

  private String part(String signature, String key) {
    for (String segment : signature.split(",")) {
      String[] pieces = segment.trim().split("=", 2);
      if (pieces.length == 2 && pieces[0].equals(key)) {
        return pieces[1];
      }
    }
    return null;
  }

  private String hmacSha256(String value, String secret) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] bytes = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder(bytes.length * 2);
      for (byte b : bytes) {
        hex.append(String.format("%02x", b));
      }
      return hex.toString();
    } catch (Exception exception) {
      throw new PaymentGatewayException("Could not verify payment webhook", exception);
    }
  }
}
