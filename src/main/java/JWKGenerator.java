import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
public class JWKGenerator {

    public static final String[] USES = new String[] {"sig", "enc"};

    public static final String[] CURVES = new String[] {"P-256", "secp256k1", "P-384", "P-521"};

    public JWKGenerator(){

    }



    public ECKey generateECJWK(String kid, String curve, String use, String alg ) throws JOSEException {

        if(!curve.equals("P-256") && !curve.equals("secp256k1") && !curve.equals("P-384") && !curve.equals("P-521")){
            return null;
        }
        if(!use.equals("enc") && !use.equals("sig")){
            return null;
        }
        Curve safeCurve = Curve.parse(curve);
        KeyUse keyUse = new KeyUse(use);
        String keyId = kid != null ? kid : UUID.randomUUID().toString();

        ECKey jwk = new ECKeyGenerator(safeCurve)
                .algorithm(Algorithm.parse(alg))
                .keyUse(keyUse) // indicate the intended use of the key
                .keyID(keyId) // give the key a unique ID
                .generate();

        return jwk;
    }

    public static void writeKeyToFile(boolean keySet, String outFile, JWK jwk, Gson gson) throws IOException,
            java.text.ParseException {
        JsonElement json;
        File output = new File(outFile);
        if (keySet) {
            List<JWK> existingKeys = output.exists() ? JWKSet.load(output).getKeys() : Collections.<JWK>emptyList();
            List<JWK> jwkList = new ArrayList<JWK>(existingKeys);
            jwkList.add(jwk);
            JWKSet jwkSet = new JWKSet(jwkList);
            json = JsonParser.parseString(jwkSet.toJSONObject(false).toString());

        } else {
            json = JsonParser.parseString(jwk.toJSONString());
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(output);
            IOUtils.write(gson.toJson(json), os, Charset.defaultCharset());
        } finally {
            IOUtils.closeQuietly(os, null);
        }
    }



    public static String readKeystore(String inFile) throws IOException,
            java.text.ParseException {
        JWKSet localKeys = JWKSet.load(new File(inFile));
        return localKeys.toJSONObject(true).toString();
    }

}
