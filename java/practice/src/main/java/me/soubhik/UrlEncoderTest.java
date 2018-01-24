package me.soubhik;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class UrlEncoderTest {
    public static void main(String[] args) {
        String espana = "Espana";
        String espanaWithAccent = "Espa\u00F1a";
        String espanaWithAccentDecomposed = "Espan\u0303a";

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://www.example.com/{param}/v1").
                                                                queryParam("espana", espana).
                                                                queryParam("espanaWithAccent", espanaWithAccent).
                                                                queryParam("espanaWithAccentDecomposed", espanaWithAccentDecomposed);
        Map<String, String> params = new HashMap<>();
        params.put("param", "someparam");

        UriComponents encodedComponents = uriBuilder.buildAndExpand(params).encode();
        URI encodedUri = encodedComponents.toUri();
        String encoded = encodedUri.toString();

        UriComponents unencodedComponents = uriBuilder.buildAndExpand(params);
        URI unencodedUri = unencodedComponents.toUri();
        String unencoded = unencodedUri.toString();

        String fromEncoded = UriComponentsBuilder.fromHttpUrl(encoded).build(false).toUri().toString();
        String fromUnencoded = UriComponentsBuilder.fromHttpUrl(unencoded).build().toUri().toString();

        System.out.println(encoded.toString());
        System.out.println(unencoded.toString());
        System.out.println(fromEncoded.toString());
        System.out.println(fromUnencoded.toString());

        assert encoded.equals(unencoded);
    }
}
